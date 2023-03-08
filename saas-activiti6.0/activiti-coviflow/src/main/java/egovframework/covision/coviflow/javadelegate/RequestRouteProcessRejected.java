/**
 * @Class Name : RequestRouteProcessRejected.java
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

public class RequestRouteProcessRejected implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteProcessRejected.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteProcessRejected");
		
		try{
			String approvalResult = "";
			String destination = "";
			
			if(execution.hasVariable("g_basic_approvalResult")) {
				approvalResult = (String)execution.getVariable("g_basic_approvalResult");
			}
			
			if(approvalResult != null && (approvalResult.equalsIgnoreCase("rejected")||approvalResult.equalsIgnoreCase("cancelled"))){
				destination = "RequestProcessRejected";
			} else {
				destination = "RequestRouteRegisterToEDMS";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteProcessRejected", e);
			throw e;
		}
		
		
	}
}
