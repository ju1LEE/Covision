/**
 * @Class Name : DraftRouteProcessReceiptGroup.java
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class DraftRouteProcessReceiptGroup implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteProcessReceiptGroup.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		//수신처리
		LOG.info("DraftRouteProcessReceiptGroup");
		
		try{
			//destination
			String destination = "";
			
			//분기처리 
			String hasBatchPublish = "N";
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			
			if(formInfoExt.containsKey("scBatchPub")) {
				hasBatchPublish = (String)formInfoExt.get("scBatchPub");
			}
			
			boolean isMultiSub = false;
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			if(formData.containsKey("BodyData")) {
				JSONObject bodyData = (JSONObject)formData.get("BodyData");
				if(bodyData.containsKey("SubTable1")) {
					JSONArray subTable1 = (JSONArray)bodyData.get("SubTable1");
					if(subTable1.size() > 0){
						isMultiSub = true;
					}
				}
			}
			
			if(hasBatchPublish != null && (hasBatchPublish.equalsIgnoreCase("true") || hasBatchPublish.equalsIgnoreCase("Y")) && isMultiSub) {
				destination = "DraftPreProcessBatchInnerPubSending"; 			
			} else {
				destination = "DraftPreProcessReceiptGroup";
			}

			execution.setVariable("g_draft_destination", destination);	
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteProcessReceiptGroup", e);
			throw e;
		}
		
		
		
	}
}
