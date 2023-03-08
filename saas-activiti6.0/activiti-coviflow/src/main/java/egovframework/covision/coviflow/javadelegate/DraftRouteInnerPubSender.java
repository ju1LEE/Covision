/**
 * @Class Name : DraftRouteInnerPubSender.java
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

public class DraftRouteInnerPubSender implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteInnerPubSender.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteInnerPubSender");

		try{
			//destination
			String destination = "";
			
			//분기처리 
			String hasInnerPubSender = "false";
			
			if(hasInnerPubSender.equalsIgnoreCase("true"))
			{
				destination = "DraftProcessInnerPubTransform"; 			
			} else {
				destination = "DraftRegisterApprovedBoxForDistribute";
			}
			
			//execution.setVariable("input", var);
			execution.setVariable("g_draft_destination", destination);
			
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteInnerPubSender", e);
			throw e;
		}
		
		

	}
}
