package egovframework.covision.webhard.admin.service;


import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;

public interface WebhardAdminSvc {

	/**
	 * 환경설정 - 환경설정 값 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap searchConfig(CoviMap params) throws Exception;
	
	/**
	 * 환경설정 - 환경설정 저장
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int saveConfig(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 BOX 관리 - 박스 목록 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	public CoviMap searchBoxList(CoviMap params) throws Exception;

	/**
	 * 웹하드 BOX 관리 - 박스 설정값 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getBoxConfig(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 관리 - 박스 설정값 수정
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int setBoxConfig(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 BOX 관리 - 박스 잠금
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int blockBox(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 BOX 관리 - 박스 잠금 여부 설정
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int setBoxBlockYn(CoviMap params) throws Exception;

	/**
	 * 웹하드 BOX 관리 - 잠금 사유
	 * @param params
	 * @return String
	 * @throws Exception
	 */
	public String getBoxBlockReason(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 BOX 관리 - 박스 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int deleteBox(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 BOX 관리 - 박스 사용 여부 설정
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int setBoxUseYn(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 파일검색 - 파일 목록 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	public CoviMap searchFileList(CoviMap params) throws Exception;
	
	/**
	 * 웹하드 파일검색 - 파일 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int deleteFile(CoviMap params) throws Exception;

	
	/**
	 * Migration 대상 조회 : covi_webhard4j.wh_box_list 의 owner_type 이 'U' 인 사용자만 대상으로 한다.
	 * @return
	 * @throws Exception
	 */
	public CoviList selectMigBoxList() throws Exception;

	/**
	 * Migration 대상 조회 : covi_webhard4j.wh_folder_list 폴더 경로 조회.
	 * @return
	 * @throws Exception
	 */
	public CoviList selectMigFolderList() throws Exception;
	
	/**
	 * Migration 대상 조회 : covi_webhard4j.wh_file_list의 FILE_PATH 경로 조회.
	 * @return
	 * @throws Exception
	 */
	public CoviList selectMigFileList() throws Exception;
	
	/**
	 * Migration 대상 조회 : covi_webhard4j.wh_file_list의 삭제 파일 조회.
	 * @return
	 * @throws Exception
	 */
	public CoviList selectMigDelFileInfo() throws Exception;
	
}
