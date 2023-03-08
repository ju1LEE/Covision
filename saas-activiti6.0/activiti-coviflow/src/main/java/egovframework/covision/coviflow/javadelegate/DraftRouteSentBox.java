/**
 * @Class Name : DraftRouteSentBox.java
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

public class DraftRouteSentBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteSentBox.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteSentBox");

		try{
			String destination = "";
			String hasSentBox = "false";
			
			JSONParser parser = new JSONParser();
			String ctx = (String)execution.getVariable("g_context");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx);
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String isSecure = pDesc.get("IsSecureDoc").toString();
			
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String scSBox = "";
			if(formInfoExt.containsKey("scSBox")){
				scSBox = (String)formInfoExt.get("scSBox");
			}
			
			//배포여부 확인
			String isInnerPublication = "false";
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			if(formInfoExt.containsKey("ReceiveNames")){
				if(StringUtils.isNotBlank(formInfoExt.get("ReceiveNames").toString())){
					isInnerPublication = "true";
				}
			}
			
			//부서발송함
			if(isSecure.equalsIgnoreCase("N") && scSBox.equalsIgnoreCase("Y") && isInnerPublication.equalsIgnoreCase("true")){
				hasSentBox = "true";
			}
			
			if(hasSentBox.equalsIgnoreCase("true"))
			{
				//추가적인 분기처리 파악 필요.
				destination = "DraftSentBox"; 			
			} else {
				destination = "DraftRouteBudgetChange";
			}
				
			execution.setVariable("g_draft_destination", destination);
	
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteSentBox", e);
			throw e;
		}
		
		
	}
}
