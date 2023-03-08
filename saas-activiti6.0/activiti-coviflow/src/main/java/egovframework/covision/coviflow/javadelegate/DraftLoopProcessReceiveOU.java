/**
 * @Class Name : DraftLoopProcessReceiveOU.java
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

public class DraftLoopProcessReceiveOU implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftLoopProcessReceiveOU.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		//수신처 발송
		LOG.info("DraftLoopProcessReceiveOU");
		
		try{
			//Looping 후처리
			String destination = "";
			String isLoop = "false";
			
			if(isLoop.equalsIgnoreCase("true"))
			{
				destination = "DraftPreProcessReceiveOU";
			} else {
				destination = "DraftRouteSentBox";
			}
			
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftLoopProcessReceiveOU", e);
			throw e;
		}
		
		
	}
}
