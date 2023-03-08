/**
 * @Class Name : RequestParallelCoordinatorsHandler.java
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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestParallelCoordinatorsHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestParallelCoordinatorsHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("RequestParallelCoordinators");
		
		try{
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			CoviFlowWorkHelper.setMultiActivity("coordinators", delegateTask, ctx, delegateTask.getAssignee(), "T004");
			
			/*//Step 1. 협조자 전처리
			CoviFlowWorkHelper.processCoordinators();			
			//Step 2. 메일발송
			CoviFlowWorkHelper.sendMailWorkitem();
			//Step 3. 결재행위 후처리
			CoviFlowWorkHelper.processWorkitem();*/
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestParallelCoordinatorsHandler", e);
			throw e;
		}

	}

}