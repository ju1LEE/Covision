/**
 * @Class Name : DraftChargedJobBoxHandler.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.tasklistener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

@SuppressWarnings("serial")
public class DraftChargedJobBoxHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(DraftChargedJobBoxHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("DraftChargedJobBox");
		
		try{
			//Step 1. performer, workitem을 할당.
			
			//메일 발송
			//Bas_TWPr_SendMailCharge
					
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("DraftChargedJobBoxHandler", e);
			throw e;
		}
		
		 
	}

}