/**
 * @Class Name : RequestRouteTransmitter.java
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

public class RequestRouteTransmitter implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteTransmitter.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteTransmitter");
		
		try{
			String destination = "";
			String isTransmit = "false";

			/*
			// form_info_ext 값 가져오기
			JSONParser parser = new JSONParser();
			String ctxJsonStr = (String)execution.getVariable("g_context");
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			if(formInfoExt.containsKey("IsTransmit")){
				isTransmit = (String)formInfoExt.get("IsTransmit");
			}
			*/
			if (isTransmit.equalsIgnoreCase("true")) {
				destination = "Transmitter";
			} else {
				destination = "RoutePreview";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteTransmitter", e);
			throw e;
		}
				
		
	}
}
