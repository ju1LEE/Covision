/**
 * @Class Name : RequestLoopSteps.java
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class RequestLoopSteps implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestLoopSteps.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestLoopSteps");
		
		try{
			String hasStepsFinished = "false";
			if(execution.hasVariable("g_request_hasStepsFinished"))
			{
				hasStepsFinished = (String)execution.getVariable("g_request_hasStepsFinished");
			}
			
			String approvalResult = "";
			if(execution.hasVariable("g_request_approvalResult"))
			{
				approvalResult = (String)execution.getVariable("g_request_approvalResult");
			}
			
			if(approvalResult != null && (approvalResult.equalsIgnoreCase("rejected")||approvalResult.equalsIgnoreCase("cancelled"))){
				hasStepsFinished = "true";
			} else {
				JSONParser parser = new JSONParser();
				
				//정상적인 승인 처리
				//pending인 step ou의 taskid 값을 가지고서 api로 넘어오는 변수 값을 가져옴
				String apvLine = (String)execution.getVariable("g_appvLine");
				JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
				
				//결재선의 pending인 상태를 가져옴
				JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
				String isStep = pendingObject.get("isStep").toString();
				
				if(isStep.equalsIgnoreCase("false")){
					hasStepsFinished = "true";
				}
				
			}
			
			//set variable
			execution.setVariable("g_request_hasStepsFinished", hasStepsFinished);
		} catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestLoopSteps", e);
			throw e;
		}

	}
}
