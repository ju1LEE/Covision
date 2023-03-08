package egovframework.covision.webhard.common.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.covision.webhard.common.service.CommonSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;



@Service("webhardFolderSvc")
public class WebhardFolderSvcImpl extends EgovAbstractServiceImpl implements WebhardFolderSvc {
	
	@Resource(name = "coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Resource(name = "commonSvc")
	private CommonSvc commonSvc;
	
	@Resource(name = "webhardFileSvc")
	private WebhardFileSvc webhardFileSvc;
	
	private static Logger LOGGER = LogManager.getLogger(WebhardFolderSvcImpl.class);
	
	/**
	 * 박스 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getBoxInfo(CoviMap params) throws Exception {
		CoviMap baseInfo = commonSvc.getBaseInfo(params);
		CoviMap boxInfo = coviMapperOne.selectOne("webhardCommon.box.selectBox", baseInfo);
		
		return boxInfo;
	}
	
	/**
	 * 박스 생성
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap generateBox(CoviMap params) throws Exception {
		CoviMap baseInfo = commonSvc.getBaseInfo(params);
		
		String domainId = baseInfo.getString("domainId");
		String domainCode = baseInfo.getString("domainCode");
		String ownerId = baseInfo.getString("ownerId");
		String ownerName = baseInfo.getString("ownerName");
		String ownerType = baseInfo.getString("ownerType");
		String boxName = baseInfo.getString("boxName");
		String newBoxUuid = commonSvc.generateUuid();

		// Step 1. 기본 경로 확인
		
		String domainPath = baseInfo.getString("domainPath");
		String homePath = baseInfo.getString("homePath");
		String boxPath = homePath + "/" + newBoxUuid;

		File domainDir = new File(domainPath);
		
		if (!domainDir.exists()) {
			if(domainDir.mkdir()) {
				LOGGER.info("path : " + domainDir + " mkdir();");
			}
		}
		
		File homeDir = new File(homePath);
		
		if (!homeDir.exists()) {
			if(homeDir.mkdir()) {
				LOGGER.info("path : " + homeDir + " mkdir();");
			}
		}
		
		File boxDir = new File(boxPath);
		
		if (!boxDir.exists()) {
			if(boxDir.mkdir()) {
				LOGGER.info("path : " + boxDir + " mkdir();");
			}
			
			// 부서 드라이브의 경우 소유자명이 박스명과 동일함
			if (ownerType.equals("G")) {
				ownerName = boxName;
			}
			
			// Step 2. 생성된 박스 정보 DB에 저장
			params.put("boxUuid", newBoxUuid);
			params.put("domainId", domainId);
			params.put("domainCode", domainCode);
			params.put("ownerType", ownerType);
			params.put("ownerId", ownerId);
			params.put("ownerName", ownerName);
			params.put("boxName", boxName);
			params.put("boxPath", boxPath);
		}
		
		return params;
	}
	
	/**
	 * folderPath에 특정 폴더를 포함하는 모든 폴더 조회
	 * @param params
	 * @return List<CoviMap>
	 * @throws Exception
	 */
	@Override
	public List<CoviMap> getFolderListInPath(CoviMap params) throws Exception {
		return coviMapperOne.selectList("webhardCommon.folder.selectFolderListInPath", params);
	}
	
	/**
	 * 폴더 공유 
	 * @param target
	 * @param sharedTo
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int shareFolder(CoviMap targetFolder, CoviMap sharedTo) throws Exception {
		int errorCnt = 0;
		
		List<CoviMap> targetFileList = webhardFileSvc.getFileListInFolder(targetFolder);
		
		if (targetFileList != null && !targetFileList.isEmpty()) {
			for (CoviMap targetFile : targetFileList) {			
				if (webhardFileSvc.shareFile(targetFile, sharedTo) < 1) { // 폴더 내 파일 공유
					errorCnt++;
				}
			}
		}
		
		sharedTo.put("sharedType", "FOLDER");
		sharedTo.put("sharedUuid", targetFolder.getString("folderUuid"));
		
		if (coviMapperOne.insert("webhardCommon.common.insertSharedList", sharedTo) < 1) { // 폴더 공유
			errorCnt++;
		}
		// 공유자에게 알림 메세지 보내기
		if(errorCnt == 0) { 
			sharedTo.put("PusherCode",  SessionHelper.getSession("USERID"));
			int sharedCnt = coviMapperOne.insert("webhardCommon.common.sendMessagingShared", sharedTo);
		}
		return errorCnt > 0 ? -1 : 1;
	}
	
	/**
	 * 폴더 공유 해제
	 * @param target
	 * @param unsharedTo
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int unshareFolder(CoviMap targetFolder, CoviMap unsharedTo) throws Exception {
		int errorCnt = 0;
		
		List<CoviMap> targetFileList = webhardFileSvc.getFileListInFolder(targetFolder);
		
		if (targetFileList != null && !targetFileList.isEmpty()) {
			for (CoviMap targetFile : targetFileList) {			
				if (webhardFileSvc.unshareFile(targetFile, unsharedTo) < 1) { // 폴더 내 파일 공유 해제
					errorCnt++;
				}
			}
		}
		
		unsharedTo.put("sharedType", "FOLDER");
		unsharedTo.put("sharedUuid", targetFolder.getString("folderUuid"));
		
		if (coviMapperOne.delete("webhardCommon.common.deleteSharedList", unsharedTo) < 1) { // 폴더 공유 해제
			errorCnt++;
		}
		
		return errorCnt > 0 ? -1 : 1;
	}
	
	/**
	 * 폴더 정보 저장
	 * @param boxInfo 박스 정보
	 * @param directories 폴더 정보 목록
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional(isolation=Isolation.READ_UNCOMMITTED)
	public int saveFolderInfo(CoviMap boxInfo, CoviList directories) throws Exception {
		int result = 0;
		
		for (int i = 0; i < directories.size(); i++) {
			CoviMap dir = directories.getJSONObject(i);
			
			String uuid = dir.getString("UUID");
			String boxUuid = dir.getString("boxUUID");
			String parentUuid = dir.getString("parentUUID").equals("/") ? "" : dir.getString("parentUUID");
			String originName = dir.getString("folderName");
			String folderPath = dir.getString("folderPath");
			String folderNamePath = dir.getString("folderNamePath");
			
			if(StringUtil.isNotNull(parentUuid)){
				CoviMap fParams = new CoviMap();
				fParams.put("boxUuid", boxUuid);
				fParams.put("folderUuid", parentUuid);
				
				CoviMap folderInfo = getFolderInfo(fParams);
				
				// 폴더 업로드 시 폴더 경로가 중복되는 경우가 있어서 분기 처리 작업함
				if (StringUtil.isNotNull(folderPath.substring(0, folderPath.indexOf(uuid) - 1))
					&& folderInfo.getString("folderPath").indexOf(folderPath.substring(0, folderPath.indexOf(uuid) - 1)) > -1) {
					String replaceUuid = folderInfo.getString("folderPath").substring(0, folderInfo.getString("folderPath").indexOf(folderPath.substring(0, folderPath.indexOf(uuid) - 1)));
					
					fParams.put("folderUuid", replaceUuid.substring(replaceUuid.lastIndexOf("/") + 1, replaceUuid.length()));
					
					folderInfo = getFolderInfo(fParams);
				}
				
				if(folderInfo != null){
					folderPath = folderInfo.getString("folderPath") + folderPath;
					folderNamePath = folderInfo.getString("folderNamePath") + folderNamePath;
				}
			}
			
			String folderRealPath = boxInfo.getString("homePath") + "/" + boxUuid + folderPath;
			File folder = new File(folderRealPath);
			
			if (folder.mkdirs()) {
				CoviMap folderParam = new CoviMap();
				int folderLevel = folderPath.split("/").length - 1;
				
				folderParam.put("boxUuid", boxUuid);
				folderParam.put("folderUuid", uuid);
				folderParam.put("parentUuid", parentUuid);
				folderParam.put("folderName", originName);
				folderParam.put("folderPath", folderPath);
				folderParam.put("folderNamePath", folderNamePath);
				folderParam.put("folderLevel", folderLevel);
				folderParam.put("bookmark", "N");
				folderParam.put("folderGrant", "RW");
				
				result = coviMapperOne.insert("webhardCommon.folder.insertFolderInfo", folderParam);
			}
		}
		
		return result;
	}
	
	/**
	 * 폴더 생성
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addFolder(CoviMap params) throws Exception {
		int result = 0;
		
		String parentUuid = params.getString("parentUuid");
		String boxUuid = params.getString("boxUuid");
		String folderName = params.getString("folderName");
		String folderUuid = commonSvc.generateUuid();
		
		String folderPath = "";
		String folderNamePath = "";
		String folderRealPath = "";
		
		if (parentUuid != null && !parentUuid.equals("")) {
			CoviMap bParams = new CoviMap();
			bParams.put("boxUuid", boxUuid);
			bParams.put("folderUuid", parentUuid);
			
			CoviMap folderMap = getFolderInfo(bParams);
			
			folderPath = folderMap.getString("folderPath") + "/" + folderUuid;
			folderNamePath = folderMap.getString("folderNamePath") + "/" + folderName;
			folderRealPath = folderMap.getString("folderRealPath") + "/" + folderUuid;
		} else {
			CoviMap boxInfo = commonSvc.getBaseInfo();
			
			folderPath = "/" + folderUuid;
			folderNamePath = "/" + folderName;
			folderRealPath = boxInfo.getString("homePath") + "/" + boxUuid + "/" + folderUuid;
		}
		
		File folder = new File(folderRealPath);
		int folderLevel = folderPath.split("/").length - 1;
		
		if (folder.mkdirs()) {
			params.put("folderUuid", folderUuid);
			params.put("folderLevel", folderLevel);
			params.put("folderPath", folderPath);
			params.put("folderNamePath", folderNamePath);
			params.put("bookmark", "N");
			params.put("folderGrant", "RW");
			
			result = coviMapperOne.update("webhardCommon.folder.insertFolder", params);
		}
		
		return result;
	}
	
	/**
	 * 폴더명 변경
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int renameFolder(CoviMap params) throws Exception {
		int result = 0;
		
		CoviMap folderInfo = getFolderInfo(params);
		String folderNamePath = folderInfo.getString("folderNamePath");
		String thisFolderNamePath = "";
		
		// 폴더 경로에 폴더 명이 겹치는게 있을 때 전부 변경되는 것을 방지 (ex. /a/a-1/a-2를 /a -> /b 로 replace할 경우 /b/a-1/a-2가 아닌 /b/b-1/b-2로 변경 되는 것을 방지)
		if(StringUtil.isNotNull(folderInfo.getString("parentUuid"))){
			CoviMap fParams = new CoviMap();
			fParams.put("folderUuid", folderInfo.getString("parentUuid"));
			
			CoviMap parentFolderInfo = getFolderInfo(fParams);
			
			folderNamePath = folderNamePath.substring(parentFolderInfo.getString("folderNamePath").length(), folderNamePath.length());
			folderNamePath = parentFolderInfo.getString("folderNamePath") + "/" + params.getString("folderName") + "/";
			thisFolderNamePath = parentFolderInfo.getString("folderNamePath") + "/" + params.getString("folderName");
		}else{
			folderNamePath = "/" + params.getString("folderName") + "/";
			thisFolderNamePath = "/" + params.getString("folderName");
		}
		
		params.put("userCode", SessionHelper.getSession("UR_ID"));
		params.put("groupCode", SessionHelper.getSession("DEPTID"));
		params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
		params.put("folderLevel", folderInfo.getString("folderLevel"));
		params.put("thisFolderNamePath", thisFolderNamePath);
		params.put("folderNamePath", folderNamePath);
		params.put("exFolderNamePath", folderInfo.getString("folderNamePath") + "/");
		
		result += coviMapperOne.update("webhardCommon.folder.renameFolder", params);
		result += coviMapperOne.update("webhardCommon.folder.renameFolderNamePath", params);
		
		return result;
	}
	
	/**
	 * 폴더 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getFolderInfo(CoviMap params) throws Exception {
		CoviMap fParams = new CoviMap();
		CoviMap folderInfo = new CoviMap();
		if(params.getString("folderUuid").equals("ROOT")) {
			fParams.put("folderUuid", "");
		}else {
			fParams.put("folderUuid", params.getString("folderUuid"));
		}
		
		
		if(StringUtil.isNotNull(params.getString("boxUuid"))){
			fParams.put("boxUuid", params.getString("boxUuid"));
		}
		
		if(StringUtil.isNotNull(params.getString("domainId"))){
			fParams.put("domainId", params.getString("domainId"));
		}
		
		if(StringUtil.isNotNull(fParams.getString("folderUuid"))){
			folderInfo = coviMapperOne.selectOne("webhardCommon.folder.selectFolderPath", fParams);
			
			if(folderInfo == null || folderInfo.size() == 0) return folderInfo;
			
			CoviMap boxInfo = commonSvc.getBaseInfo(folderInfo);
			
			String folderRealPath = boxInfo.getString("homePath") + "/" + folderInfo.getString("boxUuid") + folderInfo.getString("folderPath");
			folderInfo.put("folderRealPath", folderRealPath);
		}else{
			folderInfo = coviMapperOne.selectOne("webhardUser.box.selectBoxPath", fParams);
			
			if(folderInfo == null || folderInfo.size() == 0) return folderInfo;
			folderInfo.put("folderPath", "");
			folderInfo.put("folderNamePath", "");
			
			CoviMap boxInfo = commonSvc.getBaseInfo(folderInfo);
			
			String folderRealPath = boxInfo.getString("homePath") + "/" + folderInfo.getString("boxUuid");
			folderInfo.put("folderRealPath", folderRealPath);
		}
		
		folderInfo.addAll(folderInfo);
		
		return folderInfo;
	}
	
	/**
	 * 폴더 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int delete(CoviMap params) throws Exception {
		int result = 0;
		
		CoviMap delParams = new CoviMap();
		delParams.addAll(params);
		
		List<String> folderUuids = Arrays.asList(params.getString("folderUuids").split(";"));
		
		delParams.put("folderUuids", folderUuids);
		delParams.put("fileUuids", new ArrayList<String>());
		
		if (delParams.getString("folderType").equals("Trashbin")) {
			result += coviMapperOne.update("webhardCommon.folder.deleteFolderListFromTrashbin", delParams); // 선택한 폴더 + 하위 폴더 삭제
			result += coviMapperOne.update("webhardCommon.file.deleteFileListFromTrashbin", delParams); // 하위 파일 삭제
			
			if(result > 0) {
				for (int i = 0; i < folderUuids.size(); i++) {
					CoviMap fParams = new CoviMap();
					fParams.put("boxUuid", params.getString("boxUuid"));
					fParams.put("folderUuid", folderUuids.get(i));
					
					CoviMap folderInfo = getFolderInfo(fParams);
					File folder = new File(folderInfo.getString("folderRealPath"));
					
					commonSvc.deleteAll(folder);
				}
			}
		} else if (delParams.getString("folderType").equals("Published")) {
			result = coviMapperOne.update("webhardCommon.folder.deleteFolderSharedList", delParams);
		} else {
			result = coviMapperOne.update("webhardCommon.folder.deleteFolderList", delParams);
		}
		
		return result;
	}
	
	/**
	 * 폴더 복원
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int restore(CoviMap params) throws Exception {
		List<String> folderUuids = Arrays.asList(params.getString("folderUuids").split(";"));
		params.put("folderUuids", folderUuids);
		
		return coviMapperOne.update("webhardCommon.folder.restoreFolder", params);
	}
	
	/**
	 * 폴더 복사
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int copy(CoviMap params) throws Exception {
		String boxUuid = params.getString("boxUuid");
		String folderUuids[] = params.getString("folderUuids").split(";");
		String targetBoxUuid = params.getString("targetBoxUuid");
		String targetFolderUuid = params.getString("targetFolderUuid");
		
		CoviMap tParams = new CoviMap();
		tParams.put("boxUuid", targetBoxUuid);
		tParams.put("folderUuid", targetFolderUuid);
		
		CoviMap targetFolderInfo = getFolderInfo(tParams);
		
		int result = 0;
		
		CoviMap saveParams = new CoviMap();
		saveParams.put("boxUuid", targetBoxUuid);
		saveParams.put("targetFNamePath", targetFolderInfo.getString("folderNamePath"));
		
		for (String folderUuid : folderUuids) {
			CoviMap fParams = new CoviMap();
			fParams.put("boxUuid", boxUuid);
			fParams.put("folderUuid", folderUuid);
			
			String newfolderUuid = commonSvc.generateUuid();
			CoviMap folderInfo = getFolderInfo(fParams);
			File sourceFolder = new File(folderInfo.getString("folderRealPath"));
			File targetFolder = new File(targetFolderInfo.getString("folderRealPath") + "/" + newfolderUuid);
			String folderPath = targetFolderInfo.getString("folderPath");
			int folderLevel = folderPath.split("/").length;
			
			saveParams.put("changeFNamePath", folderInfo.getString("folderNamePath").substring(0, folderInfo.getString("folderNamePath").indexOf(folderInfo.getString("folderName")) - 1));
			
			saveParams.put("folderUuid", folderUuid);
			saveParams.put("newFolderUuid", newfolderUuid);
			saveParams.put("parentUuid", targetFolderUuid);
			saveParams.put("folderLevel", folderLevel);
			saveParams.put("folderName", folderInfo.getString("folderName"));
			saveParams.put("folderPath", folderPath + "/" + newfolderUuid);
			saveParams.put("folderNamePath", saveParams.getString("targetFNamePath") + folderInfo.getString("folderNamePath").subSequence(saveParams.getString("changeFNamePath").length(), folderInfo.getString("folderNamePath").length()));
			saveParams.put("bookmark", "N");
			saveParams.put("folderGrant", folderInfo.getString("folderGrant"));
			
			result += coviMapperOne.insert("webhardCommon.folder.insertCopyFolder", saveParams);
			
			if (!commonSvc.copyAll(sourceFolder, targetFolder, saveParams)) {
				return 0;
			}
		}
		
		return result;
	}
	
	/**
	 * 폴더 이동
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int move(CoviMap params) throws Exception {
		// copy folders
		int result = copy(params);
		
		// delete folders
		if(result > 0) {
			params.put("folderType", "Trashbin");
			result += delete(params);
		}
		
		return result;
	}
	
	/**
	 * 폴더 다운로드 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	@Override
	public List<CoviMap> selectDownFolderList(CoviMap params){
		return coviMapperOne.selectList("webhardCommon.folder.selectDownFolderList", params);
	}
	
	/**
	 * 휴지통 - 폴더 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	@Override
	public List<CoviMap> selectTrashbinFolderList(CoviMap params){
		List<CoviMap> resultList = coviMapperOne.selectList("webhardCommon.folder.selectTrashbinFolderList", params);
		
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
	 * 폴더명 조회
	 * @param params
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String getFolderName(CoviMap params) throws Exception{
		return coviMapperOne.getString("webhardCommon.folder.selectFolderName", params);
	}
}