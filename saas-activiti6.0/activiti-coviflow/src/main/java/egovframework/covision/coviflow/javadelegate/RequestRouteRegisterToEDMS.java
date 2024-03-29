/**
 * @Class Name : RequestRouteRegisterToEDMS.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class RequestRouteRegisterToEDMS implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteRegisterToEDMS.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteRegisterToEDMS");
		
		try{
			//분기처리
			String destination = "";
			String scEdmsLegacy = "";
			String docClassId = "";
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			if(ctxJsonObj.containsKey("FormInfoExt")){
				JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
				if(formInfoExt.containsKey("scEdmsLegacy")) {
					scEdmsLegacy = (String)formInfoExt.get("scEdmsLegacy");
				}
				
				JSONObject docInfo = (JSONObject)formInfoExt.get("DocInfo");
				if(docInfo != null) {
					docClassId = (String)docInfo.get("DocClassID");
				}
			}
			
			// 문서이관 옵션 && 문서분류코드 입력
			if("True".equalsIgnoreCase(scEdmsLegacy) && !StringUtils.isEmpty(docClassId)){
				destination = "RequestRegisterToEDMS";
			} else {
				destination = "RequestRouteIssueDocNumber";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteRegisterToEDMS", e);
			throw e;
		}
		
		
	}
}
