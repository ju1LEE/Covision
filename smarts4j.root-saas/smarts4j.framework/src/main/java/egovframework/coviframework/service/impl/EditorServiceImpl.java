package egovframework.coviframework.service.impl;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import egovframework.coviframework.util.AsyncTaskFileEncryptor;
import egovframework.coviframework.util.FileUtil;
import egovframework.coviframework.util.s3.AwsS3;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.ClientInfoHelper;
import egovframework.baseframework.util.DateHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.EditorService;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;


/**
 * @Class Name : EditorServiceImpl.java
 * @Description : 에디터 처리 
 * @Modification Information 
 * @ 2018.08.14 최초생성
 *
 * @author 코비젼 연구소
 * @since 2018.08.14
 * @version 1.0
 * Copyright (C) by Covision All right reserved.
 */

@Service("editorService")
public class EditorServiceImpl extends EgovAbstractServiceImpl implements EditorService {
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;

	@Resource(name = "asyncTaskFileEncryptor")
	private AsyncTaskFileEncryptor asyncTaskFileEncryptor;
	
	private final Logger LOGGER = LogManager.getLogger(EditorServiceImpl.class);
	
	public final String FRONT_PATH;	//FrontStorage Path
	public final String BACK_PATH; 	//GWStorage Path

	AwsS3 awsS3 = AwsS3.getInstance();
	
	public EditorServiceImpl(){
			BACK_PATH = FileUtil.getBackPath();
			FRONT_PATH = FileUtil.getFrontPath();
	}
	
	@Override
	public CoviMap getContent(CoviMap editorParam) throws Exception{
		CoviMap resultObj = new CoviMap();
		
		switch (RedisDataUtil.getBaseConfig("EditorType")){
		case "3":
		case "8":
		case "10":
		case "11":
		case "12":
		case "13":
		case "14":
			resultObj = getInlineValue(editorParam);
			break;
		case "9":
			break;
		default:
			break;
			
		}
		
		return resultObj;
	}
	
	//에디터 bodyHtml 이 escape() 처리 돼서 넘어올 경우
	@Override
	public CoviMap getEscapeContent(CoviMap editorParam) throws Exception{
		CoviMap resultObj = new CoviMap();
		
		switch (RedisDataUtil.getBaseConfig("EditorType")){
		case "3":
		case "8":
		case "9":
		case "10":
		case "11":
		case "12":
		case "13":
		case "14":
			resultObj = getEscapeInlineValue(editorParam);
			break;
		default:
			break;
			
		}
		
		return resultObj;
	}
	
	
	@Override
	public CoviMap getInlineValue(CoviMap editorParam) throws Exception{
			CoviMap returnObj = new CoviMap();
		
			StringUtil func = new StringUtil();
			String bodyHtml = editorParam.getString("bodyHtml");
			String subPath = "";
			int fileSize = 0;
			
			//GWStorage Info
			String strBackStoragePath =  ""; 	
			String strBackStorageUrl =  "";
            // FrontStorage Info
			String strFrontStoragePath = "";
			String strFrontStorageUrl = "";
			String strFrontStorageSubPath = "";
			
			String strImageLoadUrl = RedisDataUtil.getBaseConfig("ImageLoadURL");
			String companyCode = SessionHelper.getSession("DN_Code");
			
			String[] arrImg = new String[0];
			String FileIDs = "";
			String backImg = "";

			// 스토리지 정보 읽어 오기(LastSeq 1을 증가시킨 상태로 가져옴.)
			CoviMap params = new CoviMap();
			params.put("saveType", "INLINE");
			params.put("serviceType", editorParam.getString("serviceType"));
			params.put("domainID", SessionHelper.getSession("DN_ID"));
			params.put("osType", PropertiesUtil.getGlobalProperties().getProperty("Globals.OsType"));
			CoviMap storageInfo = coviMapperOne.select("framework.editor.selectStorageInfo", params);
			
            // 스토리지 정보 바인딩
			if(!storageInfo.isEmpty()){
				subPath = DateHelper.getCurrentDay("yyyy/MM/dd");
				storageInfo.put("LastSeq", storageInfo.getInt("LastSeq")+1);
				strBackStoragePath = BACK_PATH.substring(0, BACK_PATH.length() - 1) + storageInfo.getString("Path").replace("{0}", companyCode) + subPath + File.separator;
				strBackStorageUrl =  RedisDataUtil.getBaseConfig("ImageServiceURL") + storageInfo.getString("URL").replace("{0}", companyCode)+ subPath + "/";
			}
			
			// FrontStorage 정보 가져오기
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			
			String smart4jPath = "";
			
			if(ClientInfoHelper.isMobile(request)){
				smart4jPath = PropertiesUtil.getGlobalProperties().getProperty("mobile.smart4j.path");
			}else{
				smart4jPath = PropertiesUtil.getGlobalProperties().getProperty("smart4j.path");
			}
			
            strFrontStorageUrl = smart4jPath + RedisDataUtil.getBaseConfig("FrontStorage").replace("/{0}", "");
            strFrontStoragePath = FRONT_PATH; 
            strFrontStorageSubPath = FRONT_PATH + File.separator + companyCode + File.separator;
			
			if(editorParam.containsKey("imgInfo") || editorParam.containsKey("backgroundImage")){
				arrImg = editorParam.getString("imgInfo").split("[|]");
				backImg = editorParam.getString("backgroundImage");
				
				if ((backImg != null && !"".equals(backImg)) || (arrImg.length > 0 && checkFrontStorageUrl(editorParam.getString("imgInfo"),strFrontStorageUrl))) // 인라인 이미지 정보가 존재한다면 일단 백스토리지에 폴더를 생성함.
				{
					File saveFile = new File(strBackStoragePath);		
					if (!saveFile.exists()) {
						if(saveFile.mkdirs()) {
							LOGGER.info("path1 : " + saveFile + " mkdirs();");
						}
					}
				}
			}
			
            // 발번 받은 Seq의 내부 Seq
            int innerSeq = 0;
			
			 // Front에서 Back으로 파일 복사 및 HTML 내 파일 경로 변경
            for (int i = 0; i < arrImg.length; i++)
            {
                String imgFullPath = URLDecoder.decode(arrImg[i],"utf-8"); // 이동 대상 이미지 파일

                if (!(func.f_NullCheck(imgFullPath).equals("")))
                {
                    if (imgFullPath.indexOf(strFrontStorageUrl) > -1)
                    {
                        String strFrontFileFullPath = imgFullPath.replace(strFrontStorageUrl, strFrontStoragePath).replace("/", "\\").replace("\\", File.separator);

                        String fileName = imgFullPath.substring(imgFullPath.lastIndexOf("/") + 1);
                        String saveName = storageInfo.getInt("LastSeq")+"_"+innerSeq+"_"+fileName;
                        
                        File frontPath = new File(strFrontFileFullPath);
                        File frontSubPath = new File(strFrontStorageSubPath+saveName);
                        File backPath = new File(strBackStoragePath+saveName);
                        fileSize = (int) frontPath.length();
                        
                        if(!frontPath.exists() ||  !frontPath.isFile()){
                        	LOGGER.error("InlineImage move BackStorage Error. file path is " + frontPath.getAbsolutePath());
							throw new Exception("InlineImage move BackStorage Error");
                        }else{
                        	if(backPath.delete()) { //overwrite
                        		LOGGER.info("file : " + backPath.toString() + " delete();");
                        	}
                        	
                        	if(frontSubPath.delete()) { //overwrite
                        		LOGGER.info("file : " + frontSubPath.toString() + " delete();");
                        	}
                        	
                        	FileUtils.copyFile(frontPath, frontSubPath);
                        	FileUtils.copyFile(frontPath, backPath);
                        	
                        	if(frontPath.delete()) {
                        		LOGGER.info("file : " + frontPath.toString() + " delete();");
                        	}
                        	
                        	//frontPath.delete();
                        	if (!imgFullPath.equals(""))
                              {
                                  // Front의 이미지 경로를 Back의 경로로 변경 
								bodyHtml = bodyHtml.replace("‡" + i + "‡", "src=\"" + strImageLoadUrl + strBackStorageUrl + saveName + "\" covi_inlinefileid=\"" + storageInfo.getInt("LastSeq") + "_" + innerSeq + "\"");

								// 등록된 인라인 이미지로 섬네일 이미지로 만듬.
								//TODO 섬네일 이미지 생성 &  원본 이미지 압축

								// DB에 이미지 정보 기록
								CoviMap fileParams = new CoviMap();
								fileParams.addAll(editorParam);
								fileParams.put("storageID", storageInfo.getString("StorageID"));
								fileParams.put("saveType", "INLINE");
								fileParams.put("lastSeq", storageInfo.getString("LastSeq"));
								fileParams.put("seq", innerSeq);
								fileParams.put("filePath", subPath+ "/");
								fileParams.put("fileName", fileName);
								fileParams.put("saveName", saveName);
								fileParams.put("Extention", fileName.split("[.]")[1]);
								fileParams.put("size", fileSize);
								fileParams.put("registerCode", SessionHelper.getSession("USERID"));
								fileParams.put("companyCode", companyCode);

								coviMapperOne.insert("framework.editor.insertFileInfo", fileParams);
								
								// [파일보안] 스토리지 저장시 AES 암호화
								String isUse = PropertiesUtil.getSecurityProperties().getProperty("file.encryptor.bean.use", "");
								if("Y".equals(isUse)) {
									final String fCompanyCode = companyCode;
									final File fOriginFile = backPath;
									TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
										@Override
										public void afterCommit() {
											final CoviMap param = new CoviMap();
											try {
												param.put("FileID", fileParams.getString("FileID"));
												param.put("FileName", fileParams.getString("fileName"));
												param.put("RegisterCode", fileParams.getString("registerCode"));
												asyncTaskFileEncryptor.encrypt(fOriginFile, fCompanyCode, param);
											} catch(NullPointerException e){	
												LOGGER.error(e.getLocalizedMessage(), e);
											}catch(Exception e) {
												LOGGER.error(e.getLocalizedMessage(), e);
											}
										}
									});
								}
								
								FileIDs += fileParams.getString("FileID")+";";
                            }
                        }
                       
                        innerSeq++;
                    }else{ 
                    	bodyHtml = bodyHtml.replace("‡" + i + "‡", "src=\""+ imgFullPath + "\"");
                    }
                }
            }

		if(backImg != null && !"".equals(backImg)) { //배경 이미지 처리
			String imgFullPath = URLDecoder.decode(backImg,"utf-8"); // 이동 대상 이미지 파일

			if (!(func.f_NullCheck(imgFullPath).equals(""))) {
				if (imgFullPath.indexOf(strFrontStorageUrl) > -1) {
					String strFrontFileFullPath = imgFullPath.replace(strFrontStorageUrl, strFrontStoragePath).replace("/", "\\").replace("\\", File.separator);

					String fileName = imgFullPath.substring(imgFullPath.lastIndexOf("/") + 1);
					String saveName = storageInfo.getInt("LastSeq")+"_"+innerSeq+"_"+fileName;

					File frontPath = new File(strFrontFileFullPath);
					File frontSubPath = new File(strFrontStorageSubPath+saveName);
					File backPath = new File(strBackStoragePath+saveName);
					fileSize = (int) frontPath.length();

					if(!frontPath.exists() ||  !frontPath.isFile()){
						LOGGER.error("InlineImage move BackStorage Error. file path is " + frontPath.getAbsolutePath());
						throw new Exception("InlineImage move BackStorage Error");
					}else{
						if(backPath.delete()) { //overwrite
							LOGGER.info("file : " + backPath.toString() + " delete();");
						}

						if(frontSubPath.delete()) { //overwrite
							LOGGER.info("file : " + frontSubPath.toString() + " delete();");
						}

						FileUtils.copyFile(frontPath, frontSubPath);
						FileUtils.copyFile(frontPath, backPath);

						if(frontPath.delete()) {
							LOGGER.info("file : " + frontPath.toString() + " delete();");
						}

						if (!imgFullPath.equals(""))
						{
							// Front의 이미지 경로를 Back의 경로로 변경
							bodyHtml = bodyHtml.replace("‡" + 9999 + "‡", strImageLoadUrl + strBackStorageUrl + saveName);

							// 등록된 인라인 이미지로 섬네일 이미지로 만듬.
							//TODO 섬네일 이미지 생성 &  원본 이미지 압축

							// DB에 이미지 정보 기록
							CoviMap fileParams = new CoviMap();
							fileParams.addAll(editorParam);
							fileParams.put("storageID", storageInfo.getString("StorageID"));
							fileParams.put("saveType", "INLINE");
							fileParams.put("lastSeq", storageInfo.getString("LastSeq"));
							fileParams.put("seq", innerSeq);
							fileParams.put("filePath", subPath+ "/");
							fileParams.put("fileName", fileName);
							fileParams.put("saveName", saveName);
							fileParams.put("Extention", fileName.split("[.]")[1]);
							fileParams.put("size", fileSize);
							fileParams.put("registerCode", SessionHelper.getSession("USERID"));
							fileParams.put("companyCode", companyCode);

							coviMapperOne.insert("framework.editor.insertFileInfo", fileParams);
							
							FileIDs += fileParams.getString("FileID")+";";
						}
					}

					if(innerSeq == 0) { //배경이미지 작업 일때 만 처리
						innerSeq++;
					}
				} else {
					bodyHtml = bodyHtml.replace("‡" + 9999 + "‡", imgFullPath);
				}
			}
		}
			
            if(innerSeq > 0){
            	CoviMap seqParam  = new CoviMap();
            	seqParam.put("storageID", storageInfo.getString("StorageID"));
            	coviMapperOne.update("framework.editor.updateLastSeq", seqParam);
            }
            
            returnObj.put("BodyHtml", bodyHtml);
            returnObj.put("FileID",FileIDs);
            returnObj.put("result", Return.SUCCESS);
			
			return returnObj;
	}
	
	private boolean checkFrontStorageUrl(String imgInfo, String strFrontStorageUrl) throws Exception{
		strFrontStorageUrl = strFrontStorageUrl.replace("https://", "").replace("http://", "");
		String[] sUrl = strFrontStorageUrl.split("/")[0].split(":");
		
		if (imgInfo.indexOf(sUrl[0]) > -1 || imgInfo.indexOf(URLEncoder.encode(sUrl[0], "utf-8")) > -1)
			return true;
		else return false;
	}
	
	@Override
	public CoviMap getEscapeInlineValue(CoviMap editorParam) throws Exception{
		CoviMap returnObj = new CoviMap();
	
		StringUtil func = new StringUtil();
		String bodyHtml = editorParam.getString("bodyHtml");
		String subPath = "";
		int fileSize = 0;
		
		//GWStorage Info
		String strBackStoragePath =  ""; 	
		String strBackStorageUrl =  "";
        // FrontStorage Info
		String strFrontStoragePath = "";
		String strFrontStorageUrl = "";
		String strFrontStorageSubPath = "";
		
		String strImageLoadUrl = RedisDataUtil.getBaseConfig("ImageLoadURL");
		String companyCode = SessionHelper.getSession("DN_Code");
		
		String[] arrImg = new String[0];
		String FileIDs = "";
		String backImg = "";

		// 스토리지 정보 읽어 오기(LastSeq 1을 증가시킨 상태로 가져옴.)
		CoviMap params = new CoviMap();
		params.put("saveType", "INLINE");
		params.put("serviceType", editorParam.getString("serviceType"));
		params.put("domainID", SessionHelper.getSession("DN_ID"));
		params.put("osType", PropertiesUtil.getGlobalProperties().getProperty("Globals.OsType"));
		CoviMap storageInfo = coviMapperOne.select("framework.editor.selectStorageInfo", params);
		
        // 스토리지 정보 바인딩
		if(!storageInfo.isEmpty()){
			subPath = DateHelper.getCurrentDay("yyyy/MM/dd");
			storageInfo.put("LastSeq", storageInfo.getInt("LastSeq")+1);
			strBackStoragePath = FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1) + storageInfo.getString("Path").replace("{0}", companyCode) + subPath + File.separator;
			strBackStorageUrl =  RedisDataUtil.getBaseConfig("ImageServiceURL") + storageInfo.getString("URL").replace("{0}", companyCode) + subPath + "/";
		}
		
		// FrontStorage 정보 가져오기
		//escape 된 값으로 client에서 가져오기 
        //strFrontStorageUrl =   PropertiesUtil.getGlobalProperties().getProperty("smart4j.path")+ RedisDataUtil.getBaseConfig("FrontStorage");
		strFrontStorageUrl =   URLDecoder.decode(editorParam.getString("frontStorageURL"),"utf-8").replace("/{0}", "");
        strFrontStoragePath = FileUtil.getFrontPath();
        strFrontStorageSubPath = FileUtil.getFrontPath() + companyCode + File.separator;
		if (editorParam.containsKey("imgInfo") || editorParam.containsKey("backgroundImage")) {
			arrImg = editorParam.getString("imgInfo").split(URLEncoder.encode("|", "utf-8"));
			backImg = editorParam.getString("backgroundImage");

			if(!awsS3.getS3Active()) {

				if ((backImg != null && !"".equals(backImg)) || (arrImg.length > 0 && checkFrontStorageUrl(editorParam.getString("imgInfo"),strFrontStorageUrl))) // 인라인 이미지 정보가 존재한다면 일단 백스토리지에 폴더를 생성함.
				{
					File saveFile = new File(strBackStoragePath);
					if (!saveFile.exists()) {
						if (saveFile.mkdirs()) {
							LOGGER.info("path2 : " + saveFile + " mkdirs();");
						}
					}
				}
			}
		}
		
        // 발번 받은 Seq의 내부 Seq
        int innerSeq = 0;
        
    	// Front에서 Back으로 파일 복사 및 HTML 내 파일 경로 변경
        for (int i = 0; i < arrImg.length; i++)
        {
            String imgFullPath = URLDecoder.decode(arrImg[i],"utf-8"); // 이동 대상 이미지 파일

			if (!(func.f_NullCheck(imgFullPath).equals("")))
            {
                if (imgFullPath.indexOf(strFrontStorageUrl) > -1)
                {
                    String strFrontFileFullPath = imgFullPath.replace(strFrontStorageUrl, strFrontStoragePath);
					strFrontFileFullPath = strFrontFileFullPath.replaceAll("//", "/");
					//System.out.println("strFrontFileFullPath>"+strFrontFileFullPath);

                    String fileName = imgFullPath.substring(imgFullPath.lastIndexOf("/") + 1);
                    String saveName = storageInfo.getInt("LastSeq")+"_"+innerSeq+"_"+fileName;

					if(awsS3.getS3Active()) {
						if(awsS3.exist(strFrontFileFullPath)) {
							String key1 = strFrontStorageSubPath + saveName;
							key1 = key1.replaceAll("//","/");
							byte[] bytes = awsS3.down(strFrontFileFullPath);
							fileSize = bytes.length;
							awsS3.copy(strFrontFileFullPath, key1);
							String key2 = strBackStoragePath + saveName;
							key2 = key2.replaceAll("//","/");
							awsS3.copy(strFrontFileFullPath, key2);
							awsS3.delete(strFrontFileFullPath);
							if (!imgFullPath.equals("")) {
								// Front의 이미지 경로를 Back의 경로로 변경
								bodyHtml = bodyHtml.replace("%u2021" + i + "%u2021", "src=\"" + strImageLoadUrl + strBackStorageUrl + saveName + "\" covi_inlinefileid=\"" + storageInfo.getInt("LastSeq") + "_" + innerSeq + "\"");

								// 등록된 인라인 이미지로 섬네일 이미지로 만듬.
								//TODO 섬네일 이미지 생성 &  원본 이미지 압축

								// DB에 이미지 정보 기록
								CoviMap fileParams = new CoviMap();
								fileParams.addAll(editorParam);
								fileParams.put("storageID", storageInfo.getString("StorageID"));
								fileParams.put("saveType", "INLINE");
								fileParams.put("lastSeq", storageInfo.getString("LastSeq"));
								fileParams.put("seq", innerSeq);
								fileParams.put("filePath", subPath + "/");
								fileParams.put("fileName", fileName);
								fileParams.put("saveName", saveName);
								fileParams.put("Extention", fileName.split("[.]")[1]);
								fileParams.put("size", fileSize);
								fileParams.put("registerCode", SessionHelper.getSession("USERID"));
								fileParams.put("companyCode", companyCode);

								coviMapperOne.insert("framework.editor.insertFileInfo", fileParams);
								
								FileIDs += fileParams.getString("FileID") + ";";
							}
						}else{
							LOGGER.error("DEXT5 InlineImage move BackStorage Error");
							throw new Exception("InlineImage move BackStorage Error");
						}

					}else {
						File frontPath = new File(strFrontFileFullPath);
						File frontSubPath = new File(strFrontStorageSubPath + saveName);
						File backPath = new File(strBackStoragePath + saveName);
						fileSize = (int) frontPath.length();

						if (!frontPath.exists() || !frontPath.isFile()) {
							LOGGER.error("DEXT5 InlineImage move BackStorage Error");
							throw new Exception("InlineImage move BackStorage Error");
						} else { 
							if (backPath.delete()) { //overwrite
								LOGGER.info("file : " + backPath.toString() + " delete();");
							}

							if (frontSubPath.delete()) { //overwrite
								LOGGER.info("file : " + frontSubPath.toString() + " delete();");
							}

							FileUtils.copyFile(frontPath, frontSubPath);
							FileUtils.copyFile(frontPath, backPath);

							
							if (frontPath.delete()) {
								LOGGER.info("file : " + frontPath.toString() + " delete();");
							}

							if (!imgFullPath.equals("")) {
								// Front의 이미지 경로를 Back의 경로로 변경
								
								bodyHtml = bodyHtml.replace("%u2021" + i + "%u2021", "src=\"" + strImageLoadUrl + strBackStorageUrl + saveName + "\" covi_inlinefileid=\"" + storageInfo.getInt("LastSeq") + "_" + innerSeq + "\"");
								if(editorParam.getString("isMailToBoardYn").equals("Y")) {
									bodyHtml = bodyHtml.replace(arrImg[i], strImageLoadUrl + strBackStorageUrl + saveName + "\" covi_inlinefileid=\"" + storageInfo.getInt("LastSeq") + "_" + innerSeq);
								}

								// 등록된 인라인 이미지로 섬네일 이미지로 만듬.
								//TODO 섬네일 이미지 생성 &  원본 이미지 압축

								// DB에 이미지 정보 기록
								CoviMap fileParams = new CoviMap();
								fileParams.addAll(editorParam);
								fileParams.put("storageID", storageInfo.getString("StorageID"));
								fileParams.put("saveType", "INLINE");
								fileParams.put("lastSeq", storageInfo.getString("LastSeq"));
								fileParams.put("seq", innerSeq);
								fileParams.put("filePath", subPath + "/");
								fileParams.put("fileName", fileName);
								fileParams.put("saveName", saveName);
								fileParams.put("Extention", fileName.split("[.]")[1]);
								fileParams.put("size", fileSize);
								fileParams.put("registerCode", SessionHelper.getSession("USERID"));
								fileParams.put("companyCode", companyCode);

								coviMapperOne.insert("framework.editor.insertFileInfo", fileParams);
								
								// [파일보안] 스토리지 저장시 AES 암호화
								String isUse = PropertiesUtil.getSecurityProperties().getProperty("file.encryptor.bean.use", "");
								if("Y".equals(isUse)) {
									final String fCompanyCode = companyCode;
									final File fOriginFile = backPath;
									TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
										@Override
										public void afterCommit() {
											final CoviMap param = new CoviMap();
											try {
												param.put("FileID", fileParams.getString("FileID"));
												param.put("FileName", fileParams.getString("FileName"));
												param.put("RegisterCode", fileParams.getString("RegisterCode"));
												asyncTaskFileEncryptor.encrypt(fOriginFile, fCompanyCode, param);
											} catch(NullPointerException e){	
												LOGGER.error(e.getLocalizedMessage(), e);
											}catch(Exception e) {
												LOGGER.error(e.getLocalizedMessage(), e);
											}
										}
									});
								}
								
								FileIDs += fileParams.getString("FileID") + ";";
							}
						}
					}
                   
                    innerSeq++;
                }else{ 
                	bodyHtml = bodyHtml.replace("%u2021" + i + "%u2021", "src=\""+ imgFullPath + "\"");
                }
            }
        }

		if(backImg != null && !"".equals(backImg)&& !"null".equals(backImg)) { //배경 이미지 처리
			String imgFullPath = URLDecoder.decode(backImg,"utf-8"); // 이동 대상 이미지 파일

			if (!(func.f_NullCheck(imgFullPath).equals(""))) {
				if (imgFullPath.indexOf(strFrontStorageUrl) > -1) {
					String strFrontFileFullPath = imgFullPath.replace(strFrontStorageUrl, strFrontStoragePath);
					strFrontFileFullPath = strFrontFileFullPath.replaceAll("//", "/");
					//System.out.println("strFrontFileFullPath>"+strFrontFileFullPath);

					String fileName = imgFullPath.substring(imgFullPath.lastIndexOf("/") + 1);
					String saveName = storageInfo.getInt("LastSeq")+"_"+innerSeq+"_"+fileName;
					if(awsS3.getS3Active()) {
						if(awsS3.exist(strFrontFileFullPath)){
							byte[] bytes = awsS3.down(strFrontFileFullPath);
							fileSize = bytes.length;
							String key1 = strFrontStorageSubPath + saveName;
							key1 = key1.replaceAll("//","/");
							awsS3.copy(strFrontFileFullPath, key1);
							String key2 = strBackStoragePath + saveName;
							key2 = key2.replaceAll("//","/");
							awsS3.copy(strFrontFileFullPath, key2);
							awsS3.delete(strFrontFileFullPath);
							if (!imgFullPath.equals("")) {
								// Front의 이미지 경로를 Back의 경로로 변경
								bodyHtml = bodyHtml.replace("%u2021" + 9999 + "%u2021", strImageLoadUrl + strBackStorageUrl + saveName);

								// 등록된 인라인 이미지로 섬네일 이미지로 만듬.
								//TODO 섬네일 이미지 생성 &  원본 이미지 압축

								// DB에 이미지 정보 기록
								CoviMap fileParams = new CoviMap();
								fileParams.addAll(editorParam);
								fileParams.put("storageID", storageInfo.getString("StorageID"));
								fileParams.put("saveType", "INLINE");
								fileParams.put("lastSeq", storageInfo.getString("LastSeq"));
								fileParams.put("seq", innerSeq);
								fileParams.put("filePath", subPath + "/");
								fileParams.put("fileName", fileName);
								fileParams.put("saveName", saveName);
								fileParams.put("Extention", fileName.split("[.]")[1]);
								fileParams.put("size", fileSize);
								fileParams.put("registerCode", SessionHelper.getSession("USERID"));
								fileParams.put("companyCode", companyCode);

								coviMapperOne.insert("framework.editor.insertFileInfo", fileParams);
								FileIDs += fileParams.getString("FileID") + ";";
							}
						}else {
							LOGGER.error("DEXT5 InlineImage move BackStorage Error");
							throw new Exception("InlineImage move BackStorage Error");
						}
					}else {
						File frontPath = new File(strFrontFileFullPath);
						File frontSubPath = new File(strFrontStorageSubPath + saveName);
						File backPath = new File(strBackStoragePath + saveName);
						fileSize = (int) frontPath.length();

						if (!frontPath.exists() || !frontPath.isFile()) {
							LOGGER.error("DEXT5 InlineImage move BackStorage Error");
							throw new Exception("InlineImage move BackStorage Error");
						} else {
							if (backPath.delete()) { //overwrite
								LOGGER.info("file : " + backPath.toString() + " delete();");
							}

							if (frontSubPath.delete()) { //overwrite
								LOGGER.info("file : " + frontSubPath.toString() + " delete();");
							}

							FileUtils.copyFile(frontPath, frontSubPath);
							FileUtils.copyFile(frontPath, backPath);

							if (frontPath.delete()) {
								LOGGER.info("file : " + frontPath.toString() + " delete();");
							}

							if (!imgFullPath.equals("")) {
								// Front의 이미지 경로를 Back의 경로로 변경
								bodyHtml = bodyHtml.replace("%u2021" + 9999 + "%u2021", strImageLoadUrl + strBackStorageUrl + saveName);

								// 등록된 인라인 이미지로 섬네일 이미지로 만듬.
								//TODO 섬네일 이미지 생성 &  원본 이미지 압축

								// DB에 이미지 정보 기록
								CoviMap fileParams = new CoviMap();
								fileParams.addAll(editorParam);
								fileParams.put("storageID", storageInfo.getString("StorageID"));
								fileParams.put("saveType", "INLINE");
								fileParams.put("lastSeq", storageInfo.getString("LastSeq"));
								fileParams.put("seq", innerSeq);
								fileParams.put("filePath", subPath + "/");
								fileParams.put("fileName", fileName);
								fileParams.put("saveName", saveName);
								fileParams.put("Extention", fileName.split("[.]")[1]);
								fileParams.put("size", fileSize);
								fileParams.put("registerCode", SessionHelper.getSession("USERID"));
								fileParams.put("companyCode", companyCode);

								coviMapperOne.insert("framework.editor.insertFileInfo", fileParams);
								
								FileIDs += fileParams.getString("FileID") + ";";
							}
						}
					}

					if(innerSeq == 0) { //배경이미지 작업 일때 만 처리
						innerSeq++;
					}
				} else {
					bodyHtml = bodyHtml.replace("%u2021" + 9999 + "%u2021", imgFullPath);
				}
			}
		}
		
        if(innerSeq > 0){
        	CoviMap seqParam  = new CoviMap();
        	seqParam.put("storageID", storageInfo.getString("StorageID"));
        	coviMapperOne.update("framework.editor.updateLastSeq", seqParam);
        }
        
        returnObj.put("BodyHtml", bodyHtml);
        returnObj.put("FileID",FileIDs);
        returnObj.put("result", Return.SUCCESS);
		
		return returnObj;
	}
	
	@Override
	public void updateFileMessageID(CoviMap editorParam){
		if(editorParam.containsKey("FileID") && (!editorParam.getString("FileID").isEmpty()) ){
			String[] fileIDs = editorParam.getString("FileID").split(";");
			
			CoviMap params = new CoviMap();
			params.put("fileIDs", fileIDs);
			params.put("messageID", editorParam.getString("messageID"));
			
			
			coviMapperOne.update("framework.editor.updateFileMessageID",params);
		}
	}
	
	@Override
	public void updateFileObjectID(CoviMap editorParam){
		if(editorParam.containsKey("FileID") && (!editorParam.getString("FileID").isEmpty()) ){
			String[] fileIDs = editorParam.getString("FileID").split(";");
			
			CoviMap params = new CoviMap();
			params.put("fileIDs", fileIDs);
			params.put("objectID", editorParam.getString("objectID"));
			
			
			coviMapperOne.update("framework.editor.updateFileObjectID",params);
		}
	}

	@Override
	public int deleteInlineFile(CoviMap editorParam) throws Exception{
		int retCnt = 0;
		
		editorParam.put("osType", PropertiesUtil.getGlobalProperties().getProperty("Globals.OsType"));
		
		CoviList deletedList = coviMapperOne.list("framework.editor.selectInlineFIle", editorParam);
		
		for(Object obj : deletedList){
			CoviMap deletedMap = (CoviMap)obj;
			
			String companyCode = deletedMap.getString("CompanyCode").equals("") ? SessionHelper.getSession("DN_Code") : deletedMap.getString("CompanyCode");
			//String delFilePath  = deletedMap.getString("Path") + deletedMap.getString("FIlePath") + deletedMap.getString("SavedName");
			String delFilePath  = FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1) + deletedMap.getString("Path").replace("{0}", companyCode) + deletedMap.getString("FIlePath") + deletedMap.getString("SavedName");
			
			File delFile= new File(delFilePath);
			
			if(delFile.isFile()&&delFile.exists()){
				if(delFile.delete()){
					LOGGER.info("file : " + delFile.toString() + " delete();");
				} else {
					//삭제 실패시 Logging
					LOGGER.error("Fail on deleteInlineFIle() : " + delFilePath);
					throw new Exception("deleteInlineFIle error.");
				}
			}
		}
		retCnt = coviMapperOne.delete("framework.editor.deleteInlineFIle", editorParam);
		
		return retCnt;
	}
}
