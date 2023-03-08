package egovframework.covision.webhard.common.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import egovframework.baseframework.data.CoviMap;

public interface CommonSvc {
	
	/**
	 * 기본 정보 조회
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getBaseInfo() throws Exception;
	
	/**
	 * 기본 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getBaseInfo(CoviMap params) throws Exception;
	
	/**
	 * 사용자 도메인 가져오기(JobType = Origin)
	 * @return CoviMap
	 * @throws Exception
	 */
	public CoviMap getOriginDomain() throws Exception;
	
	/**
	 * UUID 생성
	 * @return String
	 * @throws Exception
	 */
	public String generateUuid() throws Exception;
	
	/**
	 * 파일 복사
	 * @param sourceFile 복사하려는 파일
	 * @param targetFile 복사될 위치의 파일
	 * @return <code>true</code>: 복사 성공, <code>false</code>: 복사 실패
	 * @throws Exception
	 */
	public boolean copy(File sourceFile, File targetFile);
	
	/**
	 * 파일/폴더 전체 복사
	 * @param sourceFolder 복사하려는 폴더
	 * @param targetFolder 복사될 위치의 폴더
	 * @param params
	 * @return <code>true</code>: 복사 성공, <code>false</code>: 복사 실패
	 * @throws Exception
	 */
	public boolean copyAll(File sourceFolder, File targetFolder, CoviMap params);
	
	/**
	 * 파일/폴더 삭제
	 * @param targetFolder 삭제하려는 폴더
	 * @return <code>true</code>: 삭제 성공, <code>false</code>: 삭제 실패
	 * @throws Exception
	 */
	public boolean delete(File targetFolder) throws Exception;
	
	/**
	 * 파일/폴더 전체 삭제
	 * @param targetFolder 삭제하려는 폴더
	 * @return <code>true</code>: 삭제 성공, <code>false</code>: 삭제 실패
	 * @throws Exception
	 */
	public boolean deleteAll(File targetFolder) throws Exception;
	
	/**
	 * 파일 생성
	 * @param mf 파일
	 * @param file 저장 위치
	 * @param ext 확장자
	 * @throws Exception
	 */
	public void transferTo(MultipartFile mf, File file, String ext) throws Exception;
	
	/**
	 * 파일 전체 크기 조회
	 * @param file 크기를 확인하려는 파일
	 * @return long 파일 크기
	 * @throws Exception
	 */
	public long getTotalSize(File file) throws Exception;
	
	/**
	 * 파일 업로드
	 * @param mf 업로드 하는 파일
	 * @param file 업로드 위치
	 * @return <code>true</code>: 업로드 성공, <code>false</code>: 업로드 실패
	 */
	public boolean fileUpload(MultipartFile mf, File file);
}
