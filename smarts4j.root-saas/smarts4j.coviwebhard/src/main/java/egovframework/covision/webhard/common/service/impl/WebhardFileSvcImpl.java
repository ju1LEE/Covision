package egovframework.covision.webhard.common.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.covision.webhard.common.service.CommonSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;



@Service("webhardFileSvc")
public class WebhardFileSvcImpl extends EgovAbstractServiceImpl implements WebhardFileSvc {
	
	@Resource(name = "coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Resource(name = "commonSvc")
	private CommonSvc commonSvc;
	
	@Resource(name = "webhardFolderSvc")
	private WebhardFolderSvc webhardFolderSvc;
	
	private static Logger LOGGER = LogManager.getLogger(WebhardFileSvcImpl.class);
	
	/**
	 * 파일 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getFileInfo(CoviMap params) throws Exception {
		CoviMap fileInfo = coviMapperOne.selectOne("webhardCommon.file.selectFile", params);
		
		if (fileInfo != null) {
			CoviMap boxInfo = commonSvc.getBaseInfo(fileInfo);
			String fileRealPath = boxInfo.getString("homePath") + "/" + fileInfo.getString("boxUuid") + fileInfo.getString("filePath");
			
			fileInfo.put("fileRealPath", fileRealPath);
		}
		
		return fileInfo;
	}
	
	/**
	 * 파일 정보 목록 조회
	 * @param fileUuids
	 * @return List<CoviMap>
	 * @throws Exception
	 */
	@Override
	public List<CoviMap> getFileInfoList(String[] fileUuids) throws Exception {
		CoviMap params = new CoviMap();
		
		List<CoviMap> fileInfoList = new ArrayList<CoviMap>();
		CoviMap fileInfo = null;
		
		int uuidsLength = fileUuids.length;
		
		for (int i = 0; i < uuidsLength; i ++) {
			params.put("fileUuid", fileUuids[i]);
			fileInfo = coviMapperOne.selectOne("webhardCommon.file.selectFile", params);
			
			fileInfoList.add(fileInfo);
		}
		
		return fileInfoList;
	}
	
	/**
	 * 특정 폴더 내 모든 파일 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 * @throws Exception
	 */
	@Override
	public List<CoviMap> getFileListInFolder(CoviMap params) throws Exception {
		return coviMapperOne.selectList("webhardCommon.file.selectFileListInFolder", params);
	}
	
	/**
	 * 파일 공유 
	 * @param target
	 * @param sharedTo
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int shareFile(CoviMap target, CoviMap sharedTo) throws Exception {
		int result = -1;
		
		if (target != null) {
			sharedTo.put("sharedType", "FILE");
			sharedTo.put("sharedUuid", target.getString("fileUuid"));
		
			// 파일 공유
			result = coviMapperOne.insert("webhardCommon.common.insertSharedList", sharedTo);
			// 공유자에게 알림 메세지 보내기
			sharedTo.put("PusherCode",  SessionHelper.getSession("USERID"));
			int sharedCnt = coviMapperOne.insert("webhardCommon.common.sendMessagingShared", sharedTo);
		}

		return result;
	}
	
	/**
	 * 파일 공유 해제
	 * @param target
	 * @param unsharedTo
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int unshareFile(CoviMap target, CoviMap unsharedTo) throws Exception {
		int result = -1;
		
		if (target != null) {
			unsharedTo.put("sharedType", "FILE");
			unsharedTo.put("sharedUuid", target.getString("fileUuid"));
		
			// 파일 공유 해제
			result = coviMapperOne.delete("webhardCommon.common.deleteSharedList", unsharedTo);
		}

		return result < 1 ? -1 : 1;
	}
	
	/**
	 * 파일 정보 저장
	 * @param boxInfo 박스 정보
	 * @param files 파일 리스트
	 * @param fileInfos 파일 정보 리스트
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional(isolation=Isolation.READ_UNCOMMITTED)
	public int saveFileInfo(CoviMap boxInfo, List<MultipartFile> files, CoviList fileInfos) throws Exception {
		int result = 0;
		
		for (int i = 0; i < files.size(); i++) {
			MultipartFile mf = files.get(i);
			CoviMap fileInfo = fileInfos.getJSONObject(i);
			
			String uuid = fileInfo.getString("UUID");
			String boxUuid = fileInfo.getString("boxUUID");
			String folderUuid = fileInfo.getString("folderUUID");
			String filePath = fileInfo.getString("filePath");
			String originName = mf.getOriginalFilename();
			String fileExt = FilenameUtils.getExtension(originName);
			
			if(StringUtil.isNotNull(folderUuid)){
				CoviMap fParams = new CoviMap();
				fParams.put("boxUuid", boxUuid);
				fParams.put("folderUuid", folderUuid);
				
				CoviMap folderInfo = webhardFolderSvc.getFolderInfo(fParams);
				
				if(folderInfo != null){
					// 폴더 업로드 시 파일 경로에 폴더 UUID가 붙은 상태로 오면 경로가 중복되서 분기 처리 작업함
					if (folderInfo.getString("folderPath").indexOf(filePath.substring(0, filePath.indexOf(uuid) - 1)) > -1) {
						filePath = folderInfo.getString("folderPath").replaceAll(filePath.substring(0, filePath.indexOf(uuid) - 1), "") + filePath;
					} else {
						filePath = folderInfo.getString("folderPath") + filePath;
					}
				}
			}

			File dir = new File(boxInfo.getString("homePath") + "/" + boxUuid);
			if (!dir.exists()) {
				if (dir.mkdirs()) {
					LOGGER.info("path : " + dir + " mkdirs();");
				}
			}
			
			String fileRealPath = boxInfo.getString("homePath") + "/" + boxUuid + filePath;
			File file = new File(fileRealPath);
			
			if (commonSvc.fileUpload(mf, file)) {
				CoviMap fileParam = new CoviMap();
				
				fileParam.put("boxUuid", boxUuid);
				fileParam.put("fileUuid", uuid);
				fileParam.put("folderUuid", folderUuid);
				fileParam.put("fileName", originName);
				fileParam.put("fileSize", mf.getSize());
				fileParam.put("fileType", fileExt);
				fileParam.put("filePath", filePath);
				fileParam.put("fileContentType", mf.getContentType());
				
				result = coviMapperOne.insert("webhardCommon.file.insertFileInfo", fileParam);
			}
		}
		
		return result;
	}
	
	/**
	 * 파일 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int delete(CoviMap params) throws Exception {
		int result = 0;
		
		CoviMap delParams = new CoviMap();
		delParams.addAll(params);
		
		List<String> fileUuids = Arrays.asList(params.getString("fileUuids").split(";"));
		
		delParams.put("fileUuids", fileUuids);
		delParams.put("folderUuids", new ArrayList<String>());
		
		if (delParams.getString("folderType").equals("Trashbin")) {
			result = coviMapperOne.update("webhardCommon.file.deleteFileListFromTrashbin", delParams);
			
			if (result > 0) {
				for (int i = 0; i < fileUuids.size(); i++) {
					CoviMap fParams = new CoviMap();
					fParams.put("boxUuid", params.getString("boxUuid"));
					fParams.put("fileUuid", fileUuids.get(i));
					
					CoviMap fileInfo = getFileInfo(fParams);
					File file = new File(fileInfo.getString("fileRealPath"));
					
					if (file.isFile()) {
						if (file.delete()) {
							LOGGER.info("file : " + file.toString() + " delete();");
						}
					}
				}
			}
		} else if (delParams.getString("folderType").equals("Published")) {
			result = coviMapperOne.update("webhardCommon.file.deleteFileSharedList", delParams);
		} else {
			result = coviMapperOne.update("webhardCommon.file.deleteFileList", delParams);
		}
		
		return result;
	}
	
	/**
	 * 파일 복원
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int restore(CoviMap params) throws Exception {
		List<String> fileUuids = Arrays.asList(params.getString("fileUuids").split(";"));
		params.put("fileUuids", fileUuids);
		
		return coviMapperOne.update("webhardCommon.file.restoreFile", params);
	}
	
	/**
	 * 파일 복사
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int copy(CoviMap params) throws Exception {
		String fileUuids[] = params.getString("fileUuids").split(";");
		String targetBoxUuid = params.getString("targetBoxUuid");
		String targetFolderUuid = params.getString("targetFolderUuid");
		
		CoviMap tParams = new CoviMap();
		tParams.put("boxUuid", targetBoxUuid);
		tParams.put("folderUuid", targetFolderUuid);
		
		CoviMap targetFolderInfo = webhardFolderSvc.getFolderInfo(tParams);
		
		int result = 0;
		
		CoviMap fParams = new CoviMap();
		fParams.put("boxUuid", targetBoxUuid);
		fParams.put("folderUuid", targetFolderUuid);
		
		for (String fileUuid : fileUuids) {
			fParams.put("fileUuid", fileUuid);
			
			String newFileUUid = commonSvc.generateUuid();
			CoviMap fileInfo = getFileInfo(fParams);
			File sourceFile = new File(fileInfo.getString("fileRealPath"));
			File targetFolder = new File(targetFolderInfo.getString("folderRealPath") + "/" + newFileUUid);
			
			if (commonSvc.copy(sourceFile, targetFolder)) {
				fParams.put("fileUuid", fileUuid);
				fParams.put("newFileUUid", newFileUUid);
				fParams.put("fileName", fileInfo.getString("fileName"));
				fParams.put("fileType", fileInfo.getString("fileType"));
				fParams.put("fileContentType", fileInfo.getString("fileContentType"));
				fParams.put("fileSize", fileInfo.getInt("fileSize"));
				fParams.put("filePath", targetFolderInfo.getString("folderPath") + "/" + newFileUUid);
				fParams.put("fileGrant", fileInfo.getString("fileGrant"));
				fParams.put("fileQuickUrl", fileInfo.getString("fileQuickUrl"));
				fParams.put("bookmark", "N");
				
				result += coviMapperOne.insert("webhardCommon.file.insertCopyFile", fParams);
			} else {
				return 0;
			}
		}
		
		return result;
	}
	
	/**
	 * 파일 이동
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int move(CoviMap params) throws Exception {
		// copy files
		int result = copy(params);
		
		// delete files
		if(result > 0) {
			params.put("folderType", "Trashbin");
			result += delete(params);
		}
		
		return result;
	}
	
	/**
	 * 파일 다운로드 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	@Override
	public List<CoviMap> selectDownFileList(CoviMap params){
		return coviMapperOne.selectList("webhardCommon.file.selectDownFileList", params);
	}
	
	/**
	 * 휴지통 - 파일 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	@Override
	public List<CoviMap> selectTrashbinFileList(CoviMap params){
		List<CoviMap> resultList = coviMapperOne.selectList("webhardCommon.file.selectTrashbinFileList", params);
		
		// null 체크
		for (int i = 0; i < resultList.size(); i++) {
			CoviMap result = resultList.get(i);
			result.addAll(result);
			
			resultList.remove(i);
			resultList.add(i, result);
		}
		
		return resultList;
	}
	
	/**
	 * 파일 정보 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	@Override
	public List<CoviMap> selectFileInfoList(CoviMap params){
		return coviMapperOne.selectList("webhardCommon.file.selectFileInfoList", params);
	}
	
	/**
	 * 파일명 조회
	 * @param params
	 * @return String
	 * @throws Exception
	 */
	public String getFileName(CoviMap params) {
		return coviMapperOne.getString("webhardCommon.file.selectFileName", params);
	}
	
	/**
	 * 파일명 변경
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int renameFile(CoviMap params) throws Exception {
		int result = 0;
		
		result = coviMapperOne.update("webhardCommon.file.renameFile", params);
		
		return result;
	}
}