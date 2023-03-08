/**
 * @Class Name : DraftRouteReservedDistribute.java
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

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class DraftRouteReservedDistribute implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteReservedDistribute.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftRouteReservedDistribute");
		
		try{
			// destination
			String destination = "";

			// Process description 에서 값 가져오기
			String isReserved = "N"; //예약발송(배포) 여부
			
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			
			isReserved = (String)pDesc.get("IsDistReserved");
			
			if ("Y".equals(isReserved)) {
				// Reserved.
				destination = "DraftReservedDistribute"; // go to pending.
			} else {
				// Default
				destination = "DraftRouteSendInnerPublication";
			}

			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteReservedDistribute", e);
			throw e;
		}
				
		
	}
}
