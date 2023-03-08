/**
 * @Class Name : DraftPostProcessReceiveOU.java
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

public class DraftPostProcessReceiveOU implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPostProcessReceiveOU.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftPostProcessReceiveOU");
		
		try{
			//Looping 후처리
			String destination = "";
			String isLoop = "false";
			
			if(isLoop.equalsIgnoreCase("true"))
			{
				destination = "DraftLoopProcessReceiveOU";
			} else {
				destination = "DraftRouteSentBox";
			}
			
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPostProcessReceiveOU", e);
			throw e;
		}
		
		
	}
}
