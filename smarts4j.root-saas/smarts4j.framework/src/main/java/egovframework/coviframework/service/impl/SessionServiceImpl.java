package egovframework.coviframework.service.impl;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.coviframework.service.SessionService;
import egovframework.coviframework.util.ADAuthUtil;
import egovframework.coviframework.util.FileUtil;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.SessionHelper;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;



import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

@Service("sessionService")
public class SessionServiceImpl extends EgovAbstractServiceImpl implements SessionService {

	private Logger LOGGER = LogManager.getLogger(egovframework.coviframework.service.impl.SessionServiceImpl.class);
	private String aeskey = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.key"));
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Override
	public CoviMap checkAuthetication(String authType, String id, String password, String locale) throws Exception
	{
		CoviMap resultList = new CoviMap();
		
		/*
		 * 인증 처리를 세분화 할 것
		 * */
		boolean isAuthenticated = false;
		switch(authType){
			case "DB":
				int resultCnt = checkDB(id, password, "NONE");
				
				if(resultCnt > 0){
					isAuthenticated = true;
				}
				break;
			case "SAML":
			case "OAUTH":
			case "SSO":
				isAuthenticated = true;
				break;
			case "OTP":
				break;		
			default :
				break;

		}
		
		
		if (isAuthenticated) 
		{
			//account 획득
			CoviMap params = new CoviMap();
			params.put("id", id);
			params.put("password", password);
			params.put("lang", locale);
			params.put("aeskey", aeskey);
			
			CoviMap account  = new CoviMap();
			
			if("SSO".equals(authType) || "SAML".equals(authType) || "OAUTH".equals(authType)){
				account = coviMapperOne.select("common.login.selectSSO", params);
			}else{
				account = coviMapperOne.select("common.login.select", params);
			}
			coviMapperOne.update("common.login.updateUserInfo", account);	//로그온 회수 증가 및 세션ID갱신
			
			resultList.put("map", CoviSelectSet.coviSelectJSON(account, "LanguageCode,LogonID,URBG_ID,LogonPW,UR_ID,UR_Code,UR_EmpNo,UR_Name,UR_Mail,UR_JobPositionCode,UR_JobPositionName,UR_JobTitleCode,UR_JobTitleName,UR_JobLevelCode,UR_JobLevelName,UR_ManagerCode,UR_ManagerName,UR_IsManager,DN_ID,DN_Code,DN_Name,GR_Code,ApprovalParentGR_Code,ApprovalParentGR_Name,GR_Name,GR_GroupPath,GR_FullName,UR_ThemeType,UR_ThemeCode,SubDomain,Attribute,UR_MultiName,UR_MultiJobPositionName,UR_MultiJobTitleName,UR_MultiJobLevelName,GR_MultiName"));		
			resultList.put("account", account);

			resultList.put("status", "OK");
			
		} else {
			resultList.put("account", null);
			resultList.put("status", "NOT");

		}
		
		return resultList;
	}
	
	public String checkSSO(String OpType){
		String value = "";
		CoviMap params = new CoviMap();
		
		switch(OpType){
		 case "SERVER":
			params.put("Code", "sso_server");
			break;
		 case "DAY":
			params.put("Code","sso_expiration_day");
			break;
		 case "URL":
			params.put("Code","sso_sp_url");
			break;	
		 case "ACS":
			params.put("Code","sso_acs_url");
			break;
		 case "SPACS":
			params.put("Code","sso_spacs_url");
			break;	
		 case "RS":
			params.put("Code","sso_rs_url");
			break;			
		 default: 
			 params.put("Code","sso_storage_path");
			 break;
		}
		params.put("DomainID", "0");
		value = (String) coviMapperOne.getString("common.login.selectSSOValue", params);
		return value;
	}
	
	private int checkDB(String id, String password, String pwdEncType){
		int iRet = 0;
		String encryptedPwd = null;
		
		switch(pwdEncType){
		case "NONE": 
			encryptedPwd = password;
			break;
			
		case "3DES":
			break;
			
		case "MD5":
			break;
			
		default : encryptedPwd = password;
			break;
		}
		
		CoviMap params = new CoviMap();
		params.put("id", id);
		params.put("password", encryptedPwd);
		params.put("aeskey", aeskey);
		iRet = (int) coviMapperOne.getNumber("common.login.selectCount", params);
		
		return iRet;
	}
	
	public String selectUserMailAddress(String id){
		String value = "";
		CoviMap params = new CoviMap();
		params.put("id", id);
		
		value = (String) coviMapperOne.getString("common.login.selectUserMailAddress", params);
		return value;
	}
	
	public String selectUserLanguageCode(String id){
		String value = "";
		CoviMap params = new CoviMap();
		params.put("id", id);
		
		value = (String) coviMapperOne.getString("common.login.selectUserLanguageCode", params);
		return value;
	}
	
	//계정 잠금여부 확인
	public int selectAccountLock(String id) throws Exception
	{
		CoviMap params = new CoviMap();
		params.put("id", id);
		
		int cnt = 0;
		cnt = (int) coviMapperOne.getNumber("common.login.selectAccountLock", params);
		
		return cnt;
	}
	
	public String selectUserAuthetication(String id, String password) throws Exception{
		String value = "";
		
		// 통합인증(Unify)의 경우는 이미 인증된 상태이기 떄문에 별도 체크 X 
		switch(PropertiesUtil.getSecurityProperties().getProperty("loginAuthType").toUpperCase()) {
			case "DB":
				CoviMap params = new CoviMap();
				params.put("id", id);
				params.put("password", password);
				params.put("aeskey",aeskey);
				
				value = (String) coviMapperOne.getString("common.login.selectUserAuthetication", params);
				
				break;
			case "AD":
				ADAuthUtil adAuthUtil = new ADAuthUtil();
				HashMap<String, Object> userInfo = adAuthUtil.getUserAuthetication(id, password);
				
				if(userInfo != null) {
					value = userInfo.get("sAMAccountName").toString();
				}
				
				break;
			default : break;
		}
		
		return value;
	}
	
	public String getAssignedBizSection(String userCode, String domainId, String isManage, List assingedOptLicArr ){
		CoviMap params = new CoviMap();
		params.put("userCode", userCode);
		params.put("DomainID", domainId);
		params.put("assingedOptLicArr", assingedOptLicArr);
		params.put("isManage", isManage);
		
		return coviMapperOne.selectOne("common.login.selectAssignedBizSection", params);
		
	}
	
}
