/**
 * @Class Name : RequestRoutePreIssueDocNo.java
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

public class RequestRoutePreIssueDocNo implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRoutePreIssueDocNo.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRoutePreIssueDocNo");
		
		try{
			String issueDocNumber = "False"; 
			String preIssueDocNum = "N";
			String destination = "";
			
			/*
			 * 문서번호 발번 : FormInfoExt > IsUseDocNo    True/False
			 * 문서번호 선발번 : FormInfoExt > scPreDocNum   Y/N
			 * 문서번호 타입 : FormInfoExt > docnotype     10
			 * */
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			
			issueDocNumber = (String)formInfoExt.get("IsUseDocNo");
			preIssueDocNum = (String)formInfoExt.get("scPreDocNum");
			
			if (issueDocNumber.equalsIgnoreCase("True") && preIssueDocNum.equalsIgnoreCase("Y")) {
				destination = "PreIssueDocNo";	
			} else {
				destination = "CcInfoBefore";
			}
			
			execution.setVariable("g_request_destination", destination);
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRoutePreIssueDocNo", e);
			throw e;
		}
		
	}
}
