/**
 * @Class Name : RequestRouteRegisterToLegacy.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class RequestRouteRegisterToLegacy implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteRegisterToLegacy.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteRegisterToLegacy");
		
		try{
			String destination = "";
			//분기처리 
			String isLegacy = "false";
			//form_info_ext값으로 부터 isLegacy값을 추출
			
			if(isLegacy.equalsIgnoreCase("true"))
			{
				destination = "RequestRegisterToLegacy"; 			
			} else {
				destination = "RequestLoopSteps";
			}
				
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteRegisterToLegacy", e);
			throw e;
		}
		
	}
}
