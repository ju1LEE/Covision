/**
 * @Class Name : RequestRouteApprovalResult.java
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

public class RequestRouteApprovalResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteApprovalResult.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteApprovalResult");
		
		try{
			//destination
			String destination = "";
			String approvalResult = "";
			if(execution.hasVariable("g_request_approvalResult"))
			{
				approvalResult = (String)execution.getVariable("g_request_approvalResult");
			}
			
			if(approvalResult != null &&  (approvalResult.equalsIgnoreCase("rejected")||approvalResult.equalsIgnoreCase("cancelled"))){
				destination = "RequestProcessReject";
			} else {
				destination = "RequestFinalizeApproval";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteApprovalResult", e);
			throw e;
		}
		
		
	}
}
