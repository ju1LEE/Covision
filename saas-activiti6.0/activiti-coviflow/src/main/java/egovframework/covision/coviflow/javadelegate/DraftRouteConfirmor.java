/**
 * @Class Name : DraftRouteConfirmor.java
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

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class DraftRouteConfirmor implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteConfirmor.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteConfirmor");

		try{
			//destination
			String destination = "";
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONParser parser = new JSONParser();
			JSONObject appvLineObj = (JSONObject)parser.parse(apvLine);
			List<String> reviewList = CoviFlowApprovalLineHelper.getInactiveStepRouteReview(appvLineObj, 0);	
			
			//추가 분석 필요
			int vRoleMemberCnt = reviewList.size();
			
			if (vRoleMemberCnt > 0) {
				execution.setVariable("g_draft_reviewers", reviewList);
				
				destination = "DraftConfirmors";
				
			} else {
				destination = "DraftRouteSetDocBox";
			}
				
			execution.setVariable("g_draft_destination", destination);	
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteConfirmor", e);
			throw e;
		}
		

	}
}
