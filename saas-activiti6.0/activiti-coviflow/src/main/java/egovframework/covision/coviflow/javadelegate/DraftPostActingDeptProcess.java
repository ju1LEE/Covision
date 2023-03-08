/**
 * @Class Name : DraftPostActingDeptProcess.java
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

public class DraftPostActingDeptProcess implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPostActingDeptProcess.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftPostActingDeptProcess");
		
		try{
			//Looping 후처리
			
			//execution.setVariable("input", var);

			/*
			String destination = "";
			int recpSize = Integer.parseInt((String) execution.getVariable("g_recpList_size"));
			int recpIdx = Integer.parseInt((String) execution.getVariable("g_recpList_idx"));
			
			if(recpIdx+1 < recpSize) {
				execution.setVariable("g_recpList_idx", String.valueOf(recpIdx+1));
				destination = "DraftLoopActingDept";
			} else {
				destination = "DraftPostProcessInSending";
			}
			
			execution.setVariable("g_draft_destination", destination);	
			*/
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPostActingDeptProcess", e);
			throw e;
		}
		
		
		
	}
}
