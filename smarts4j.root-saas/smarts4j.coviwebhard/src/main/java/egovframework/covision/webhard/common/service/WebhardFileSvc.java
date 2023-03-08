package egovframework.covision.webhard.common.service;

import java.util.List;



import org.springframework.web.multipart.MultipartFile;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;

public interface WebhardFileSvc {
	
	/**
	 * 파일 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getFileInfo(CoviMap params) throws Exception;
	
	/**
	 * 파일 정보 목록 조회
	 * @param fileUuids
	 * @return List<CoviMap>
	 * @throws Exception
	 */
	public List<CoviMap> getFileInfoList(String[] fileUuids) throws Exception;
	
	/**
	 * 특정 폴더 내 모든 파일 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 * @throws Exception
	 */
	public List<CoviMap> getFileListInFolder(CoviMap params) throws Exception;
	
	/**
	 * 파일 공유 
	 * @param target
	 * @param sharedTo
	 * @return int
	 * @throws Exception
	 */
	public int shareFile(CoviMap target, CoviMap sharedTo) throws Exception;
	
	/**
	 * 파일 공유 해제
	 * @param target
	 * @param unsharedTo
	 * @return int
	 * @throws Exception
	 */
	public int unshareFile(CoviMap target, CoviMap unsharedTo) throws Exception;
	
	/**
	 * 파일 정보 저장
	 * @param boxInfo 박스 정보
	 * @param files 파일 리스트
	 * @param fileInfos 파일 정보 리스트
	 * @return int
	 * @throws Exception
	 */
	public int saveFileInfo(CoviMap boxInfo, List<MultipartFile> files, CoviList fileInfos) throws Exception;
	
	/**
	 * 파일 삭제
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int delete(CoviMap params) throws Exception;
	
	/**
	 * 파일 복원
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int restore(CoviMap params) throws Exception;
	
	/**
	 * 파일 복사
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int copy(CoviMap params) throws Exception;
	
	/**
	 * 파일 이동
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int move(CoviMap params) throws Exception;
	
	/**
	 * 파일 다운로드 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	public List<CoviMap> selectDownFileList(CoviMap params);
	
	/**
	 * 휴지통 - 파일 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	public List<CoviMap> selectTrashbinFileList(CoviMap params);
	
	/**
	 * 파일 정보 목록 조회
	 * @param params
	 * @return List<CoviMap>
	 */
	public List<CoviMap> selectFileInfoList(CoviMap params);
	
	/**
	 * 파일명 조회
	 * @param params
	 * @return String
	 * @throws Exception
	 */
	public String getFileName(CoviMap params) throws Exception;
	
	/**
	 * 파일명 변경
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int renameFile(CoviMap params) throws Exception;
}
