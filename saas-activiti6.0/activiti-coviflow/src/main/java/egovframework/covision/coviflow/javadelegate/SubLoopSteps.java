/**
 * @Class Name : SubLoopSteps.java
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

public class SubLoopSteps implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(SubLoopSteps.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("SubLoopSteps");
		
		try{
			String execId = execution.getId();//(String)execution.getVariable("g_execid");
			
			String hasStepsFinished = "false";
			if(execution.hasVariableLocal("g_sub_hasStepsFinished"))
			{
				hasStepsFinished = (String)execution.getVariableLocal("g_sub_hasStepsFinished");
			}
			
			String approvalResult = "";
			if(execution.hasVariable("g_sub_approvalResult_" + execId))
			{
				approvalResult = (String)execution.getVariable("g_sub_approvalResult_" + execId);
			}
			
			JSONParser parser = new JSONParser();
			
			//정상적인 승인 처리
			//pending인 step ou의 taskid 값을 가지고서 api로 넘어오는 변수 값을 가져옴
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String routeType = pendingObject.get("routeType").toString();
			
			// 내부결재 중 반려 시, 합의/협조 구분하지 않도록 수정함.
			if(approvalResult.equalsIgnoreCase("rejected")
					|| (execution.hasVariable("g_request_approvalResult") && execution.getVariable("g_request_approvalResult") != null && execution.getVariable("g_request_approvalResult").toString().equalsIgnoreCase("cancelled"))
					|| (execution.hasVariable("g_basic_approvalResult") && execution.getVariable("g_basic_approvalResult") != null && execution.getVariable("g_basic_approvalResult").toString().equalsIgnoreCase("cancelled"))){
				hasStepsFinished = "true";
			} 
			else if("rejectedtodept".equals(approvalResult)) {
				hasStepsFinished = "true";
			}
			else {
				JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
				JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
				String isPerson = pendingPerson.get("isPerson").toString();
				
				if(isPerson.equalsIgnoreCase("false")){
					hasStepsFinished = "true";
				}
				
			}
			
			//set variable
			execution.setVariableLocal("g_sub_hasStepsFinished", hasStepsFinished);
		} catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("SubLoopSteps", e);
			throw e;
		}
	}
}
