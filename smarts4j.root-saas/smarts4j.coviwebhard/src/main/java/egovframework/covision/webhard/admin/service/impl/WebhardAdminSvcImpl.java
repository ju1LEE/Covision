package egovframework.covision.webhard.admin.service.impl;

import java.io.File;

import javax.annotation.Resource;
import javax.jdo.annotations.Transactional;




import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.webhard.admin.service.WebhardAdminSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("webhardAdminSvc")
public class WebhardAdminSvcImpl extends EgovAbstractServiceImpl implements WebhardAdminSvc {
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Autowired
	private WebhardFileSvc webhardFileSvc;
	
	private static Logger LOGGER = LogManager.getLogger(WebhardAdminSvcImpl.class);
	
	/**
	 * 환경설정 - 환경설정 값 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap searchConfig(CoviMap params) throws Exception {
		CoviMap configMap = coviMapperOne.selectOne("webhardAdmin.config.selectConfigInfo", params);
		
		// null 체크
		if(configMap != null){
			configMap.addAll(configMap);
		}
		
		return configMap;
	}
	
	/**
	 * 환경설정 - 환경설정 저장
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int saveConfig(CoviMap params) throws Exception {
		int result = 0;
		
		CoviMap selectMap = coviMapperOne.selectOne("webhardAdmin.config.selectConfigInfo", params);
		
		if (selectMap != null) {
			result = coviMapperOne.update("webhardAdmin.config.updateConfigInfo", params);
		} else {
			result = coviMapperOne.update("webhardAdmin.config.insertConfigInfo", params);
		}
		
		return result;
	}
	
	/**
	 * 웹하드 BOX 관리 - 박스 목록 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap searchBoxList(CoviMap params) throws Exception {
		CoviMap result = new CoviMap();
		
		int cnt = (int) coviMapperOne.getNumber("webhardAdmin.boxManage.selectBoxListCount", params);
		CoviMap page = ComUtils.setPagingData(params, cnt);
		params.addAll(page);
		
		CoviList resultList = coviMapperOne.list("webhardAdmin.boxManage.selectBoxList", params);
		
		if (resultList != null) {
			result.put("list", CoviSelectSet.coviSelectJSON(resultList, "UUID,DOMAIN_ID,DOMAIN_CODE,OWNER_TYPE,OWNER_ID,OWNER_NAME,BOX_NAME,BOX_MAX_SIZE,BOX_CURRENT_SIZE,USE_YN,BLOCK_YN,BLOCK_REASON,BOX_PATH,CREATED_DATE,UPDATED_DATE,DELETED_DATE,CURRENT_SIZE_BYTE"));
			result.put("page", page);
		}
		
		return result;
	}
	
	/**
	 * 웹하드 BOX 관리 - 박스 설정값 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getBoxConfig(CoviMap params) throws Exception {
		return coviMapperOne.select("webhardAdmin.boxManage.getBoxConfig", params);
	}
	
	/**
	 * 웹하드 관리 - 박스 설정값 수정
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int setBoxConfig(CoviMap params) throws Exception {
		return coviMapperOne.update("webhardAdmin.boxManage.setBoxConfig", params);
	}
	
	/**
	 * 웹하드 BOX 관리 - 박스 잠금
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int blockBox(CoviMap params) throws Exception {
		params.put("boxUuids", params.getString("boxUuids").split(";"));
		
		return coviMapperOne.update("webhardAdmin.boxManage.blockBox", params);
	}
	
	/**
	 * 웹하드 BOX 관리 - 박스 잠금 여부 설정
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int setBoxBlockYn(CoviMap params) throws Exception {
		return coviMapperOne.update("webhardAdmin.boxManage.setBoxBlockYn", params);
	}
	
	/**
	 * 웹하드 BOX 관리 - 잠금 사유
	 * @param params
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String getBoxBlockReason(CoviMap params) throws Exception {
		return coviMapperOne.getString("webhardAdmin.boxManage.getBoxBlockReason", params);
	}
	
	/**
	 * 웹하드 BOX 관리 - 박스 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int deleteBox(CoviMap params) throws Exception {
		params.put("boxUuids", params.getString("boxUuids").split(";"));
		
		return coviMapperOne.update("webhardAdmin.boxManage.deleteBox", params);
	}
	
	/**
	 * 웹하드 BOX 관리 - 박스 사용 여부 설정
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int setBoxUseYn(CoviMap params) throws Exception {
		return coviMapperOne.update("webhardAdmin.boxManage.setBoxUseYn", params);
	}
	
	/**
	 * 웹하드 파일검색 - 파일 목록 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap searchFileList(CoviMap params) throws Exception {
		CoviMap result = new CoviMap();
		
		int cnt = (int) coviMapperOne.getNumber("webhardAdmin.fileSearch.selectFileListCount", params);
		CoviMap page = ComUtils.setPagingData(params, cnt);
		params.addAll(page);
		
		CoviList resultList = coviMapperOne.list("webhardAdmin.fileSearch.selectFileList", params);
		
		if (resultList != null) {
			result.put("list", CoviSelectSet.coviSelectJSON(resultList, "SEQ,UUID,BOX_UUID,FOLDER_UUID,FILE_NAME,FILE_TYPE,FILE_CONTENT_TYPE,FILE_SIZE,FILE_PATH,FILE_GRANT,FILE_QUICK_URL,FILE_FLAG,CREATED_DATE,UPDATED_DATE,DELETED_DATE,DOMAIN_ID,DOMAIN_CODE,OWNER_TYPE,OWNER_ID,OWNER_NAME,BOX_PATH,FOLDER_NAME,PARENT_UUID,FOLDER_PATH,FOLDER_NAME_PATH,FOLDER_LEVEL,FOLDER_GRANT"));
			result.put("page", page);
		}
		
		return result;
	}
	
	/**
	 * 웹하드 파일검색 - 파일 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int deleteFile(CoviMap params) throws Exception {
		String fileUuids[] = params.getString("fileUuids").split(";");
		params.put("fileUuids", fileUuids);
		
		int result = coviMapperOne.update("webhardAdmin.fileSearch.deleteFile", params);
		
		if (result > 0) {
			for (int i = 0; i < fileUuids.length; i++) {
				CoviMap fParams = new CoviMap();
				fParams.put("fileUuid", fileUuids[i]);
				
				CoviMap fileInfo = webhardFileSvc.getFileInfo(fParams);
				File file = new File(fileInfo.getString("fileRealPath"));
				
				if (file.isFile()) {
					if(file.delete()) {
						LOGGER.info("file : " + file.toString() + " delete();");
					}
					
					continue;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 2021 웹하드 고도화 migration.
	 * wh_box_list 기준 migration 대상 조회.
	 */
	@Override
	public CoviList selectMigBoxList() throws Exception {
		CoviList targetList = coviMapperOne.list("webhardAdmin.config.selectMigBoxList", null);
		return targetList;
	}
	
	/**
	 * 2021 웹하드 고도화 migration.
	 * wh_folder_list 기준 migration 대상 조회.
	 */
	@Override
	public CoviList selectMigFolderList() throws Exception {
		CoviList folderList = coviMapperOne.list("webhardAdmin.config.selectMigFolderList", null);
		return folderList;
	}
	
	/**
	 * 2021 웹하드 고도화 migration.
	 * wh_folder_list 기준 migration 대상 조회.
	 */
	@Override
	public CoviList selectMigFileList() throws Exception {
		CoviList fileList = coviMapperOne.list("webhardAdmin.config.selectMigFileList", null);
		return fileList;
	}
	
	/**
	 * 2021 웹하드 고도화 migration.
	 * 서버의 삭제된 파일인 files_trashbin 에 있는 파일 조회.
	 */
	@Override
	public CoviList selectMigDelFileInfo() throws Exception {
		CoviList fileList = coviMapperOne.list("webhardAdmin.config.selectMigDelFileInfo", null);
		return fileList;
	}

}