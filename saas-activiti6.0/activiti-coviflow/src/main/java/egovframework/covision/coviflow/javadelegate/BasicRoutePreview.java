/**
 * @Class Name : BasicRoutePreview.java
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

public class BasicRoutePreview implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRoutePreview.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("BasicRoutePreview");

		try{
			JSONParser parser = new JSONParser();
			String destination = "";
			String isPreview = "false";
			// config 값 가져오기
			String configJsonStr = (String)execution.getVariable("g_config");
			JSONObject configJsonObj = (JSONObject)parser.parse(configJsonStr);
			
			if(configJsonObj.containsKey("IsPreview")){
				isPreview = (String)configJsonObj.get("IsPreview");
			}

			if (isPreview.equalsIgnoreCase("false")) {
				destination = "BasicGetRouteInfo";
			} else {
				destination = "BasicPreview";
			}

			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRoutePreview", e);
			throw e;
		}
		
		
	}
}
