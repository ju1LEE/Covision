/**
 * @Class Name : DraftRouteOuterPublication.java
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
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class DraftRouteOuterPublication implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteOuterPublication.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteOuterPublication");

		try{
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			//destination
			String destination = "";
			
			//분기처리 
			String isOuterPublication = "false";
			String isInnerPublication = "false";
			
			//대외공문 분기
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String scOPub = (String)formInfoExt.get("scOPub");
			if(scOPub.equalsIgnoreCase("True")){
				isOuterPublication = "true";
			}
			
			//배포 처리 분기
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			if(formInfoExt.containsKey("ReceiveNames")){
				if(StringUtils.isNotBlank(formInfoExt.get("ReceiveNames").toString())){
					isInnerPublication = "true";
				}
			}
			
			if(isOuterPublication.equalsIgnoreCase("true"))
			{
				//PGlobal("SplitCount").Value += 1
				//SplitCount?
				destination = "DraftPreProcessOuterPublication"; 			
			} else {
				if(isInnerPublication.equalsIgnoreCase("true"))
				{
					//PGlobal("SplitCount").Value += 1
					//SplitCount?
					
					destination = "DraftRouteInnerPubSender";
				} else {
					//PGlobal("SplitCount").Value += 1
					//SplitCount?
					
					destination = "DraftFinalizeApproval";
				}
				
				
			}
				
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteOuterPublication", e);
			throw e;
		}
		
		
	}
}
