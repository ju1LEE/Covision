/**
 * @Class Name : RequestRouteBypassed.java
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

public class RequestRouteBypassed implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteBypassed.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteBypassed");
		
		try{
			//분기처리 
			String hasBypassed = "false"; //default값은 false
			String destination = "";
			
			if(hasBypassed.equalsIgnoreCase("true")){
				destination = "RequestBypassed";
			} else {
				destination = "RequestProcessResult";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteBypassed", e);
			throw e;
		}
		
	}
}
