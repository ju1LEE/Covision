package egovframework.covision.webhard.user.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.jdo.annotations.Transactional;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.AuthorityService;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.webhard.common.service.CommonSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;
import egovframework.covision.webhard.user.service.WebhardUserSvc;

@Service("webhardUserSvc")
public class WebhardUserSvcImpl implements WebhardUserSvc {
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Resource(name="webhardFileSvc")
	private WebhardFileSvc webhardFileSvc;
	
	@Resource(name="webhardFolderSvc")
	private WebhardFolderSvc webhardFolderSvc;
	
	@Resource(name="commonSvc")
	private CommonSvc commonSvc;
	
	@Autowired
	private AuthorityService authorityService;
	
	/**
	 * 본문 폴더/파일 목록 개수 조회
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int selectBoxCount(CoviMap params) throws Exception {
		int retCnt = 0;
		
		if(params.getString("folderType").equalsIgnoreCase("Shared")){
			params.put("subjectInArr", authorityService.getAssignedSubject(SessionHelper.getSession()));
			
			if(params.getString("folderID").equalsIgnoreCase("ROOT")){
				retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectSharedRootListCount", params);
			}else{
				retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectSharedListCount", params);
			}
		}else if(params.getString("folderType").equalsIgnoreCase("Published")){
			if(params.getString("folderID").equalsIgnoreCase("ROOT")){
				retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectPublishedRootListCount", params);
			}else{
				retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectPublishedListCount", params);
			}
		}else{
			retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectBoxCount", params);
		}
		
		return retCnt;
	}
	
	/**
	 * 폴더/파일 목록 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectBoxList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviList list  = new CoviList();
		
		CoviMap boxInfo = webhardFolderSvc.getBoxInfo(params);
		params.put("boxUuid", boxInfo.getString("boxUuid"));

		if(params.getString("folderType").equalsIgnoreCase("Shared")){
			params.put("subjectInArr", authorityService.getAssignedSubject(SessionHelper.getSession()));

			if(params.getString("folderID").equalsIgnoreCase("ROOT")){
				list = coviMapperOne.list("webhardUser.box.selectSharedRootList", params);
			}else{
				list = coviMapperOne.list("webhardUser.box.selectSharedList", params);
			}
		}else if(params.getString("folderType").equalsIgnoreCase("Published")){
			if(params.getString("folderID").equalsIgnoreCase("ROOT")){
				list = coviMapperOne.list("webhardUser.box.selectPublishedRootList", params);
			}else{
				list = coviMapperOne.list("webhardUser.box.selectPublishedList", params);
			}
		}else{
			list = coviMapperOne.list("webhardUser.box.selectBoxList", params);
		}
		
		resultList.put("list",CoviSelectSet.coviSelectJSON(list, "box_uid,uid,createdDate,name,bookmark,fileSize,fileType"));
		
		return resultList;
	}
	
	/**
	 * 폴더 트리 데이터 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectFolderList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviList list = coviMapperOne.list("webhardUser.box.selectFolderList", params);
		
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "seq,uid,name,folderGrant,folderNamePath,folderPath,parentID,hasChild,boxUuid"));
		
		return resultList;
	}
	
	/**
	 * 공유 대상자 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectSharedMember(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviList list = coviMapperOne.list("webhardUser.box.selectSharedMember", params);
		
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "sharedOwnerID,sharedGrantType,sharedOwnerName,sharedOwnerJobLevelName,sharedOwnerJobPositionName,sharedOwnerJobTitleName,sharedOwnerDeptName,sharedOwnerPhotoPath"));		
		
		return resultList;
	}
	
	/**
	 * 중요 폴더 상태 변경
	 * @param params FolderID, FolderType
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateFolderBookmark(CoviMap params) throws Exception {
		return coviMapperOne.update("webhardUser.box.updateFolderBookmark", params);
	}
	
	/**
	 * 중요 파일 상태 변경
	 * @param params FolderID, FolderType
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateFileBookmark(CoviMap params) throws Exception {
		return coviMapperOne.update("webhardUser.box.updateFileBookmark", params);
	}
	
	/**
	 * 이전 폴더 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectPrevFolder(CoviMap params) throws Exception {
		CoviMap retrnObj = new CoviMap();
		
		if(params.getString("folderType").equalsIgnoreCase("Shared")) {
			params.put("subjectInArr", authorityService.getAssignedSubject(SessionHelper.getSession()));
		}
		
		CoviMap result = coviMapperOne.select("webhardUser.box.selectPrevFolder", params);
		retrnObj.putAll(result);
		
		return retrnObj;
	}
	
	/**
	 * 박스 사용량 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap getUsageWebHard(CoviMap params) throws Exception {
		CoviMap rtnObj = new CoviMap();
		
		if (StringUtil.isNull(params.getString("boxUuid"))) {
			CoviMap boxInfo = webhardFolderSvc.getBoxInfo(params);
			params.put("boxUuid", boxInfo.getString("boxUuid"));
		}
		
		CoviMap result = coviMapperOne.select("webhardUser.box.getBoxSizeAndRate", params);
		rtnObj.putAll(result);
		
		return rtnObj;
	}
	
	/**
	 * 파일 복사 시 웹하드 용량 체크
	 * @param params
	 * @param folderList
	 * @param fileList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	@Override
	public boolean checkUsageWebHard(CoviMap params, List<CoviMap> folderList, List<CoviMap> fileList) throws Exception {
		boolean retVal = false;
		
		CoviMap webhardUsageInfo = getUsageWebHard(params);
		
		long boxMaxSize = webhardUsageInfo.getLong("BOX_MAX_SIZE") * 1024 * 1024;
		long currentSize = webhardUsageInfo.getLong("CURRENT_SIZE_BYTE");
		long uploadSize = 0;
		
		CoviMap baseInfo = commonSvc.getBaseInfo();
		String boxFilesPath = baseInfo.getString("boxFilesPath");
		
		if (folderList != null && !folderList.isEmpty()) {
			for (CoviMap folderObj : folderList) {
				String folderPath = boxFilesPath + folderObj.getString("folderNamePath");
				folderPath = folderPath.replace("/", File.separator);
				
				uploadSize += commonSvc.getTotalSize(new File(folderPath));
			}
		}
		
		if (fileList != null && !fileList.isEmpty()) {
			for (CoviMap fileObj : fileList) {
				String filePath = boxFilesPath + fileObj.getString("filePath");
				filePath = filePath.replace("/", File.separator);
				
				File file = new File(filePath);
				
				uploadSize += file.length();
			}
		}
		
		if (boxMaxSize >= currentSize + uploadSize) {
			retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * 업로드용 파일/폴더 용량 조회
	 * @param params
	 * @param folderList
	 * @param fileList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	@Override
	public boolean checkUsageWebHard(CoviMap params, String folderUuids, String fileUuids) throws Exception {
		boolean retVal = false;
		
		try {
			CoviMap webhardUsageInfo = getUsageWebHard(params);
			
			long boxMaxSize = webhardUsageInfo.getLong("BOX_MAX_SIZE") * 1024 * 1024;
			long currentSize = webhardUsageInfo.getLong("CURRENT_SIZE_BYTE");
			long uploadSize = 0;
			
			CoviMap baseInfo = commonSvc.getBaseInfo();
			String homePath = baseInfo.getString("homePath");
			
			if (StringUtil.isNotNull(folderUuids)) {
				String folderUuidList[] = folderUuids.split(";");
				
				for (String folderUuid : folderUuidList) {
					CoviMap fParams = new CoviMap();
					fParams.put("boxUuid", params.getString("boxUuid"));
					fParams.put("folderUuid", folderUuid);
					
					CoviMap folderInfo = webhardFolderSvc.getFolderInfo(fParams);
					
					String folderPath = homePath + "/" + params.getString("boxUuid") + folderInfo.getString("folderPath");
					folderPath = folderPath.replace("/", File.separator);
					
					uploadSize += commonSvc.getTotalSize(new File(folderPath));
				}
			}
			
			if (StringUtil.isNotNull(fileUuids)) {
				String fileUuidList[] = fileUuids.split(";");
				
				for (String fileUuid : fileUuidList) {
					CoviMap fParams = new CoviMap();
					fParams.put("fileUuid", fileUuid);
					
					CoviMap fileInfo = webhardFileSvc.getFileInfo(fParams);
					
					String filePath = homePath + "/" + params.getString("boxUuid") + fileInfo.getString("filePath");
					filePath = filePath.replace("/", File.separator);
					
					File file = new File(filePath);
					
					uploadSize += file.length();
				}
			}
			
			if(boxMaxSize >= currentSize + uploadSize ) {
				retVal = true;
			}
		} catch(IOException e) {
			return false;
		} catch(Exception e) {
			return false;
		}
		
		return retVal;
	}
	
	/**
	 * 업로드용 파일 용량 조회
	 * @param params
	 * @param mfList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	@Override
	public boolean checkUsageWebHard(CoviMap params, List<MultipartFile> mfList) throws Exception {
		boolean retVal = false;
		
		CoviMap webhardUsageInfo = getUsageWebHard(params);
		
		long boxMaxSize = webhardUsageInfo.getLong("BOX_MAX_SIZE") * 1024 * 1024;
		long currentSize = webhardUsageInfo.getLong("CURRENT_SIZE_BYTE");
		long uploadSize = 0;
		
		for (MultipartFile mf : mfList) {
			uploadSize += mf.getSize();
		}
		
		if (boxMaxSize >= (currentSize + uploadSize)) {
			retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * 도메인 별 업로드 크기 제한 확인
	 * @param mfList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	@Override
	public boolean checkUploadSize(List<MultipartFile> mfList) throws Exception {
		boolean retVal = false;
		
		CoviMap params = new CoviMap();
		params.put("domainID", SessionHelper.getSession("DN_ID"));
		
		long uploadSize = 0;
		long uploadMaxSize = getDomainMaxUploadSize(params) * 1024 * 1024;
		
		for (MultipartFile mf : mfList) {
			uploadSize += mf.getSize();
		}
		
		if (uploadMaxSize >= uploadSize) {
			retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * target 정보로 link 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap selectLinkData(CoviMap params) throws Exception {
		CoviMap linkData = coviMapperOne.select("webhardUser.link.selectLinkData", params);
		
		if (linkData.isEmpty()) { //생성된 링크가 없을 경우
			SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
			Calendar today = Calendar.getInstance();
			
			linkData.put("isNew", "Y");
			linkData.put("LINK", commonSvc.generateUuid());
			linkData.put("AUTH", "ALL");
			linkData.put("STARTDATE", ft.format(today.getTime()));
		} else {
			linkData.put("isNew", "N");
		}
		
		return linkData;
	}
	
	/**
	 * link 정보 조회 
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap selectLinkInfo(CoviMap params) throws Exception {
		return coviMapperOne.select("webhardUser.link.selectLinkInfo", params);
	}
	
	/**
	 * link 저장
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int saveLink(CoviMap params) throws Exception {
		int cnt = 0;
		
		if (params.getString("isNew").equalsIgnoreCase("Y")) {
			cnt = coviMapperOne.insert("webhardUser.link.insertLinkData", params);
		} else {
			cnt = coviMapperOne.update("webhardUser.link.updateLinkData", params);
		}
		
		return cnt;
	}
	
	/**
	 * 비밀번호 확인
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int checkPassword(CoviMap params) throws Exception {
		int cnt = (int)coviMapperOne.getNumber("webhardUser.link.selectCheckPassworkd", params);
		return cnt;
	}
	
	/**
	 * 드라이브 box 생성
	 * @param params
	 * @throws Exception
	 */
	@Override
	public void createBox(CoviMap params) throws Exception {
		webhardFolderSvc.generateBox(params);
		// 박스 정보 DB 저장
		coviMapperOne.insert("webhardCommon.box.insertBox", params);
	}
	
	/**
	 * 유저 권한 드라이브 조회
	 * @param params
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override
	public CoviList getUserDriveList(CoviMap params) throws Exception {		
		CoviList list = coviMapperOne.list("webhardUser.box.getUserDriveList", params);
		return CoviSelectSet.coviSelectJSON(list, "GROUP_CODE,UUID,BOX_NAME,DOMAIN_CODE,BOX_UUID,OWNER_TYPE,USE_YN");
	}
	
	/**
	 * 드라이브 별 트리 조회
	 * @param params UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override
	public CoviList getSelectDriveTreeList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("webhardUser.box.getSelectDriveTreeList", params);
		return CoviSelectSet.coviSelectJSON(list, "BOX_UUID,UUID,PARENT_UUID,FOLDER_NAME,FOLDER_PATH,FOLDER_NAME_PATH,FOLDER_LEVEL,FOLDER_BOOKMARK,FOLDER_GRANT,TRASHBIN_FLAG");
	}
	
	/**
	 * 파일 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override	
	public CoviList getSelectTreeFileList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("webhardUser.box.getSelectTreeFileList", params);
		return CoviSelectSet.coviSelectJSON(list, "UUID,BOX_UUID,FOLDER_UUID,FILE_NAME,FILE_TYPE,FILE_CONTENT_TYPE,FILE_SIZE,FILE_GRANT,CREATED_DATE,UPDATED_DATE,DELETED_DATE,TRASHBIN_FLAG,TYPE,DATE,BOOKMARK");
	}
	
	/**
	 * 폴더 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override
	public CoviList getSelectTreeFolderList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("webhardUser.box.getSelectTreeFolderList", params);		
		return CoviSelectSet.coviSelectJSON(list, "UUID,BOX_UUID,FOLDER_NAME,PARENT_UUID,FOLDER_PATH,FOLDER_NAME_PATH,FOLDER_LEVEL,CREATED_DATE,UPDATED_DATE,BOOKMARK,FOLDER_GRANT,TRASHBIN_FLAG,TYPE,DATE");
	}
	
	/**
	 * 최근문서 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override
	public CoviList getSelectRecentFileList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("webhardUser.box.getSelectRecentFileList", params);
		return CoviSelectSet.coviSelectJSON(list, "UUID,BOX_UUID,FOLDER_UUID,FILE_NAME,FILE_TYPE,FILE_SIZE,FILE_PATH,CREATED_DATE,BOOKMARK,TYPE,DATE");
	}
	
	/**
	 * 중요 폴더 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override
	public CoviList getSelectImportantFolderList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("webhardUser.box.getSelectImportantFolderList", params);
		return CoviSelectSet.coviSelectJSON(list, "UUID,BOX_UUID,FOLDER_NAME,PARENT_UUID,FOLDER_PATH,FOLDER_NAME_PATH,FOLDER_LEVEL,CREATED_DATE,UPDATED_DATE,BOOKMARK,FOLDER_GRANT,TRASHBIN_FLAG,TYPE,DATE");
	}
	
	/**
	 * 중요 파일 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override	
	public CoviList getSelectImportantFileList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("webhardUser.box.getSelectImportantFileList", params);
		return CoviSelectSet.coviSelectJSON(list, "UUID,BOX_UUID,FOLDER_UUID,FILE_NAME,FILE_TYPE,FILE_CONTENT_TYPE,FILE_SIZE,FILE_GRANT,CREATED_DATE,UPDATED_DATE,DELETED_DATE,TRASHBIN_FLAG,TYPE,DATE,BOOKMARK");
	}
	
	/**
	 * 업로드 제한 확장자 조회
	 * @param params domainID
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String getExtConfig(CoviMap params) throws Exception {
		return coviMapperOne.getString("webhardUser.box.selectExtConfig", params);
	}
	
	/**
	 * 최대 업로드 크기 조회
	 * @param params domainID
	 * @return long
	 * @throws Exception
	 */
	@Override
	public long getDomainMaxUploadSize(CoviMap params) throws Exception {
		return coviMapperOne.getNumber("webhardUser.box.selectDomainMaxUploadSize", params);
	}
	
	/**
	 * '공유받은 폴더' 데이터 갯수 가져오기
	 * @param params folderID
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int selectSharedCnt(CoviMap params) throws Exception {
		int retCnt = 0;

		if (params.getString("folderID").equalsIgnoreCase("ROOT")) {
			retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectSharedRootListCount", params);
		} else {
			retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectSharedListCount", params);
		}
		
		return retCnt;
	}

	/**
	 * '공유받은 폴더' 데이터 조회.
	 * @param params folderID
	 * @return JSONArray
	 * @throws Exception
	 */
	@Override
	public CoviMap selectSharedList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviList list = new CoviList();

		if (params.getString("folderID").equalsIgnoreCase("ROOT")){
			list = coviMapperOne.list("webhardUser.box.selectSharedRootList", params);
		} else {
			list = coviMapperOne.list("webhardUser.box.selectSharedList", params);
		}
		resultList.put("list",CoviSelectSet.coviSelectJSON(list, "box_uid,uid,createdDate,name,bookmark,fileSize,fileType"));

		return resultList;
	}

	/**
	 * '공유한 폴더' 데이터 갯수 조회
	 * @param params folderID
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int selectPublishedCnt(CoviMap params) throws Exception {
		int retCnt = 0;
		
		if (params.getString("folderID").equalsIgnoreCase("ROOT")) {
			retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectPublishedRootListCount", params);
		} else {
			retCnt = (int) coviMapperOne.getNumber("webhardUser.box.selectPublishedListCount", params);
		}
		
		return retCnt;
	}

	/**
	 * '공유한 폴더' 데이터 조회.
	 * @param params folderID
	 * @return CoviList 
	 * @throws Exception
	 */
	@Override
	public CoviMap selectPublishedList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviList list = new CoviList();

		if (params.getString("folderID").equalsIgnoreCase("ROOT")){
			list = coviMapperOne.list("webhardUser.box.selectPublishedRootList", params);
		} else {
			list = coviMapperOne.list("webhardUser.box.selectPublishedList", params);
		}
		resultList.put("list",CoviSelectSet.coviSelectJSON(list, "box_uid,uid,createdDate,name,bookmark,fileSize,fileType"));

		return resultList;
	}
	
	/**
	 * 박스 소유자 체크
	 * @param params boxUuid
	 * @return <code>true</code>: 권한 체크 성공, <code>false</code>: 권한 체크 실패
	 * @throws Exception
	 */
	@Override
	public boolean checkBoxOwner(CoviMap params) throws Exception {
		boolean bCheckOwner = false;
		
		CoviMap folderInfo = webhardFolderSvc.getFolderInfo(params);
		
		if (folderInfo != null && folderInfo.size() != 0) {
			if (folderInfo.getString("ownerType").equalsIgnoreCase("U")
				&& folderInfo.getString("ownerId").equals(SessionHelper.getSession("USERID"))) {
				bCheckOwner = true;
			} else if (folderInfo.getString("ownerType").equalsIgnoreCase("G")
				&& SessionHelper.getSession("GR_GroupPath").indexOf(folderInfo.getString("ownerId") + ";") > -1) {
				bCheckOwner = true;
			}
		}
		
		return bCheckOwner;
	}
	
	/**
	 * 전체 파일 리스트 조회
	 */
	@Override
	public CoviMap selectTotalFileList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		
		int cnt = (int) coviMapperOne.getNumber("webhardUser.box.selectTotalFileListCount", params);
		page = ComUtils.setPagingData(params, cnt);
		params.addAll(page);
		resultList.put("page", page);
		
		CoviList list = coviMapperOne.list("webhardUser.box.selectTotalFileList", params);
		resultList.put("list",  CoviSelectSet.coviSelectJSON(list, "UUID,BOX_UUID,BOX_NAME,FOLDER_UUID,FOLDER_NAME_PATH,FILE_NAME,FILE_TYPE,FILE_CONTENT_TYPE,FILE_SIZE,FILE_GRANT,CREATED_DATE,DATE,UPDATED_DATE,DELETED_DATE,TRASHBIN_FLAG,TYPE,SHARED_TYPE"));
		return resultList;
	}
}
