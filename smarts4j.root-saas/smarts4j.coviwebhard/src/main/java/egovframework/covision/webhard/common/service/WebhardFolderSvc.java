package egovframework.covision.webhard.common.service;

import java.util.List;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;

public interface WebhardFolderSvc {
	
	/**
	 * 박스 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getBoxInfo(CoviMap params) throws Exception;
	
	/**
	 * 박스 생성
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap generateBox(CoviMap params) throws Exception;
	
	/**
	 * folderPath에 특정 폴더를 포함하는 모든 폴더 조회
	 * @param params
	 * @return List<CoviMap>
	 * @throws Exception
	 */
	public List<CoviMap> getFolderListInPath(CoviMap params) throws Exception;
	
	/**
	 * 폴더 공유 
	 * @param target
	 * @param sharedTo
	 * @return int
	 * @throws Exception
	 */
	public int shareFolder(CoviMap target, CoviMap sharedTo) throws Exception;
	
	/**
	 * 폴더 공유 해제
	 * @param target
	 * @param unsharedTo
	 * @return int
	 * @throws Exception
	 */
	public int unshareFolder(CoviMap target, CoviMap unsharedTo) throws Exception;
	
	/**
	 * 폴더 정보 저장
	 * @param boxInfo 박스 정보
	 * @param directories 폴더 정보 목록
	 * @return int
	 * @throws Exception
	 */
	public int saveFolderInfo(CoviMap boxInfo, CoviList directories) throws Exception;
	
	/**
	 * 폴더 생성
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int addFolder(CoviMap params) throws Exception;
	
	/**
	 * 폴더명 변경
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int renameFolder(CoviMap params) throws Exception;
	
	/**
	 * 폴더 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getFolderInfo(CoviMap params) throws Exception;
	
	/**
	 * 폴더 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int delete(CoviMap params) throws Exception;
	
	/**
	 * 폴더 복원
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int restore(CoviMap params) throws Exception;
	
	/**
	 * 폴더 복사
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int copy(CoviMap params) throws Exception;
	
	/**
	 * 폴더 이동
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int move(CoviMap params) throws Exception;
	
	/**
	 * 폴더 다운로드 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	public List<CoviMap> selectDownFolderList(CoviMap params);
	
	/**
	 * 휴지통 - 폴더 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	public List<CoviMap> selectTrashbinFolderList(CoviMap params);
	
	/**
	 * 폴더명 조회
	 * @param params
	 * @return String
	 * @throws Exception
	 */
	public String getFolderName(CoviMap params) throws Exception;
}
