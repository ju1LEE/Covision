/**
 * @Class Name : RequestReceiptLoop.java
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

public class RequestReceiptLoop implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestReceiptLoop.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestReceiptLoop");
		
		try{
			// [2020-02-19] 수정된 소스 반영 이전에 진행중이던 문서 오류발생하지 않도록 예외처리 함.
			if(execution.hasVariable("g_jfids")) {
				
			}
			else {
				String hasDivisionsFinished = "false";
				if(execution.hasVariable("g_request_hasDivisionsFinished"))
				{
					hasDivisionsFinished = (String)execution.getVariable("g_request_hasDivisionsFinished");
				}
				
				String approvalResult = "";
				if(execution.hasVariable("g_basic_approvalResult"))
				{
					approvalResult = (String)execution.getVariable("g_basic_approvalResult");
				}
				
				if(approvalResult != null && (approvalResult.equalsIgnoreCase("rejected")||approvalResult.equalsIgnoreCase("cancelled"))){
					hasDivisionsFinished = "true";
				} else {
					JSONParser parser = new JSONParser();
					//active division을 가져옴
					String apvLine = (String)execution.getVariable("g_appvLine");
					JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
					JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
					String isDivision = activeDivision.get("isDivision").toString();
					
					if(isDivision.equalsIgnoreCase("false")){
						hasDivisionsFinished = "true";
					}
				}
				
				//set variable
				execution.setVariable("g_request_hasDivisionsFinished", hasDivisionsFinished);	
			}
		} catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestReceiptLoop", e);
			throw e;
		}
		
	}
}
