/**
 * @Class Name : RequestRouteSetDocBox.java
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

public class RequestRouteSetDocBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteSetDocBox.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteSetDocBox");
		
		try{
			//config로 부터 docbox값을 가져 온다.
			String useDocBox = "false"; 
			String destination = "";
			
			if(useDocBox.equalsIgnoreCase("true")){
				destination = "RequestSetDocBox";
			} else {
				destination = "RequestEnd";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteSetDocBox", e);
			throw e;
		}
		
		
	}
}
