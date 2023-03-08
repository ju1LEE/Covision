/**
 * @Class Name : SubRouteBypassed.java
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

public class SubRouteBypassed implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(SubRouteBypassed.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("SubRouteBypassed");

		try{
			String destination = "";
			String isBypassed = "false";
			
			if (isBypassed.equalsIgnoreCase("false")) {
				destination = "SubLoopResult";
			} else {
				destination = "SubBypassed";
			}

			execution.setVariable("g_sub_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("SubRouteBypassed", e);
			throw e;
		}
		
		
	}
}
