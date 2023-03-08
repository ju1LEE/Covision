/**
 * @Class Name : DraftRouteApprovalResult.java
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

public class DraftRouteApprovalResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteApprovalResult.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteApprovalResult");

		try{
			String destination = "";
			String draftApprovalResult = "";
			if(execution.hasVariable("g_draft_approvalResult")){
				draftApprovalResult = (String) execution.getVariable("g_draft_approvalResult");	
			}
			String basicApprovalResult = "";
			if(execution.hasVariable("g_basic_approvalResult")){
				basicApprovalResult = (String) execution.getVariable("g_basic_approvalResult");	
			}
			
			if((draftApprovalResult != null && draftApprovalResult.equalsIgnoreCase("rejected"))
					|| (basicApprovalResult !=null && (basicApprovalResult.equalsIgnoreCase("rejected")||basicApprovalResult.equalsIgnoreCase("cancelled"))))
			{
				//PInstance.businessState = "02_02_01"
				destination = "DraftRouteLegacyReject"; 			
			} else {
				destination = "DraftRouteOuterPublication";
			}
				
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteApprovalResult", e);
			throw e;
		}
				

	}
}
