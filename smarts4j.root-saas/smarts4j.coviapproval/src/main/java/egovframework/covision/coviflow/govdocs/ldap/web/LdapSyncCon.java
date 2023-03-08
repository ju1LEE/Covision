package egovframework.covision.coviflow.govdocs.ldap.web;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviMap;
import egovframework.covision.coviflow.govdocs.ldap.service.LdapSyncSvc;


@Controller
public class LdapSyncCon {
	private Logger LOGGER = LogManager.getLogger(LdapSyncCon.class);

	@Autowired
	private LdapSyncSvc ldapSyncSvc;
	
	
	/**
	 * ldapFullSync - Ldap 조직도 전체 동기화
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "ldap/ldapFullSync.do" )
	public @ResponseBody CoviMap ldapFullSync(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		try {
			ldapSyncSvc.doFullUpdateLDAPData();
			returnList.put("status", Return.SUCCESS);	
		}catch (NullPointerException npE) {
			LOGGER.error(npE.getLocalizedMessage(), npE);
        	returnList.put("status", Return.FAIL);			
		}catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
        	returnList.put("status", Return.FAIL);			
		}
        return returnList;
	}

}
