/**
 * @Class Name : DraftConvertingToOuterPublicationHandler.java
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

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

@SuppressWarnings("serial")
public class DraftConvertingToOuterPublicationHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(DraftConvertingToOuterPublicationHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("DraftConvertingToOuterPublication");
		
		try{
			//Step 1. performer, workitem을 할당.
			//insert vrole member
		
			ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
			LOG.info("DraftConvertingToOuterPublication task completed");
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("DraftConvertingToOuterPublicationHandler", e);
			throw e;
		}
		
	}

}