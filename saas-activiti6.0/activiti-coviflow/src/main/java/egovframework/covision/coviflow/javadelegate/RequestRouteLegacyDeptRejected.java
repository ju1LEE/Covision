/**
 * @Class Name : RequestRouteLegacyDeptRejected.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.javadelegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class RequestRouteLegacyDeptRejected implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteLegacyDeptRejected.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteLegacyDeptRejected");
		
		try{
			String destination = "";
			// form_info_ext 값 가져오기
			String isLegacy = "";
			
			String basicResult = "";
			if(execution.hasVariable("g_basic_approvalResult")){
				basicResult = (String) execution.getVariable("g_basic_approvalResult");	
			}

			if(execution.hasVariable("g_isLegacy")){
				isLegacy = (String)execution.getVariable("g_isLegacy");	
			}

			if(!basicResult.equalsIgnoreCase("cancelled") && StringUtils.isNotBlank(isLegacy)&&CoviFlowWorkHelper.checkLegacyInfo(isLegacy, "REJECT"))
			{
				destination = "RequestLegacyDeptRejected";
			} else {
				destination = "RequestEndRejected";
			}

			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteLegacyDeptRejected", e);
			throw e;
		}
				
	}
}
