package egovframework.coviframework.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import egovframework.baseframework.data.CoviMap;



public interface SessionService {

	public CoviMap checkAuthetication(String authType, String id, String password, String locale) throws Exception;
	public String checkSSO(String OpType) throws Exception;
	public String selectUserMailAddress(String id) throws Exception;
	public String selectUserLanguageCode(String id) throws Exception;
	public int selectAccountLock(String id)throws Exception;
	public String selectUserAuthetication(String id, String password) throws Exception;
	public String getAssignedBizSection(String userCode, String domainId, String isManage, List assingedOptLicArr) throws Exception;
}

