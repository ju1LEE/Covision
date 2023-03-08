/**
 * @Class Name : BasicRouteReview.java
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

public class BasicRouteReview implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteReview.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteReview");
		
		try{
			//분기처리 
			String destination = "";
			
			// 결재선에 후결 존재 여부 판단
			// 배포처에 후결자 있는 경우만 basic에서 reviewer를 생성함.
			if(execution.hasVariable("g_isDistribution")){
				String apvLine = (String)execution.getVariable("g_appvLine");
				JSONParser parser = new JSONParser();
				JSONObject appvLineObj = (JSONObject)parser.parse(apvLine);
				List<String> reviewList = CoviFlowApprovalLineHelper.getInactiveReviewStep(appvLineObj, 1);
				
				//추가 분석 필요
				int vRoleMemberCnt = reviewList.size();
				
				if (vRoleMemberCnt > 0) {
					execution.setVariable("g_basic_reviewers", reviewList);
					
					destination = "BasicReviewer";
				} else {
					destination = "BasicRouteDocBox";
				}
			}
			else {
				destination = "BasicRouteDocBox";
			}
			
			execution.setVariable("g_basic_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteReview", e);
			throw e;
		}
		
		
	}
}
