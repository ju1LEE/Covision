package egovframework.covision.webhard.user.service;




import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;

public interface WebhardUserSvc {
	
	/**
	 * 본문 폴더/파일 목록 개수 조회
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	int selectBoxCount(CoviMap params) throws Exception;
	
	/**
	 * 폴더/파일 목록 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviMap selectBoxList(CoviMap params) throws Exception;
	
	/**
	 * 폴더/게시판 트리 메뉴 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviMap selectFolderList(CoviMap params) throws Exception;
	
	/**
	 * 중요 폴더 상태변경
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	int updateFolderBookmark(CoviMap params) throws Exception;
	
	/**
	 * 중요 파일 상태변경
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	int updateFileBookmark(CoviMap params) throws Exception;
	
	/**
	 * 공유 대상자 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviMap selectSharedMember(CoviMap params) throws Exception;

	/**
	 * 이전 폴더 조회 
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviMap selectPrevFolder(CoviMap params) throws Exception;
	
	/**
	 * 박스 사용량 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviMap getUsageWebHard(CoviMap params) throws Exception;
	
	/**
	 * 파일 복사 시 웹하드 용량 체크
	 * @param params
	 * @param folderList
	 * @param fileList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	boolean checkUsageWebHard(CoviMap params, List<CoviMap> folderList, List<CoviMap> fileList) throws Exception;
	
	/**
	 * 업로드용 파일/폴더 용량 조회
	 * @param params
	 * @param folderList
	 * @param fileList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	boolean checkUsageWebHard(CoviMap params, String folderUuids, String fileUuids) throws Exception;
	
	/**
	 * 업로드용 파일 용량 조회
	 * @param params
	 * @param mfList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	boolean checkUsageWebHard(CoviMap params, List<MultipartFile> mfList) throws Exception;
	
	/**
	 * 도메인 별 업로드 크기 제한 확인
	 * @param mfList
	 * @return <code>true</code>: 업로드 가능, <code>false</code>: 업로드 불가능
	 * @throws Exception
	 */
	boolean checkUploadSize(List<MultipartFile> mfList) throws Exception;
	
	/**
	 * target 정보로 link 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	CoviMap selectLinkData(CoviMap params) throws Exception;
	
	/**
	 * link 정보 조회 
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	CoviMap selectLinkInfo(CoviMap params) throws Exception;
	
	/**
	 * link 저장
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	int saveLink(CoviMap params) throws Exception;
	
	/**
	 * 비밀번호 확인
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	int checkPassword(CoviMap params) throws Exception;
	
	/**
	 * 드라이브 box 생성
	 * @param params
	 * @throws Exception
	 */
	void createBox(CoviMap params) throws Exception;
	
	/**
	 * 유저 권한 드라이브 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviList getUserDriveList(CoviMap params) throws Exception;
	
	/**
	 * 드라이브 별 트리 조회
	 * @param params UUID
	 * @return JSONObject
	 * @throws Exception
	 */
	CoviList getSelectDriveTreeList(CoviMap params) throws Exception;
	
	/**
	 * 파일 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	CoviList getSelectTreeFileList(CoviMap params) throws Exception;
	
	/**
	 * 폴더 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	CoviList getSelectTreeFolderList(CoviMap params) throws Exception;
	
	/**
	 * 최근문서 리스트
	 * @param params UUID, BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	CoviList getSelectRecentFileList(CoviMap params) throws Exception;
	
	/**
	 * 중요 폴더 리스트
	 * @param params BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	CoviList getSelectImportantFolderList(CoviMap params) throws Exception;
	
	/**
	 * 중요 파일 리스트
	 * @param params BOX_UUID
	 * @return JSONArray
	 * @throws Exception
	 */
	CoviList getSelectImportantFileList(CoviMap params) throws Exception;
	
	/**
	 * 업로드 제한 확장자 조회
	 * @param params domainID
	 * @return String
	 * @throws Exception
	 */
	String getExtConfig(CoviMap params) throws Exception;
	
	/**
	 * 최대 업로드 크기 조회
	 * @param params domainID
	 * @return long
	 * @throws Exception
	 */
	long getDomainMaxUploadSize(CoviMap params) throws Exception;

	/**
	 * '공유받은 폴더' 데이터 갯수 조회.
	 * @param params folderID
	 * @return int 
	 * @throws Exception
	 */
	int selectSharedCnt(CoviMap params) throws Exception;
	
	/**
	 * '공유받은 폴더' 데이터 조회.
	 * @param params folderID
	 * @return CoviList 
	 * @throws Exception
	 */
	CoviMap selectSharedList(CoviMap params) throws Exception;

	/**
	 * '공유한 폴더' 데이터 갯수 조회.
	 * @param params folderID
	 * @return int 
	 * @throws Exception
	 */
	int selectPublishedCnt(CoviMap params) throws Exception;

	/**
	 * '공유한 폴더' 데이터 조회.
	 * @param params folderID
	 * @return CoviList 
	 * @throws Exception
	 */
	CoviMap selectPublishedList(CoviMap params) throws Exception;
	
	/**
	 * 박스 소유자 체크
	 * @param params boxUuid
	 * @return <code>true</code>: 권한 체크 성공, <code>false</code>: 권한 체크 실패
	 * @throws Exception
	 */
	boolean checkBoxOwner(CoviMap params) throws Exception;
	
	/**
	 * 전체 파일 조회
	 * @param params
	 * @throws Exception
	 */
	CoviMap selectTotalFileList(CoviMap params) throws Exception;
}