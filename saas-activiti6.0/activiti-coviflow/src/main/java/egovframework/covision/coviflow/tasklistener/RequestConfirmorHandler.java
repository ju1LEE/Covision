/**
 * @Class Name : RequestConfirmorHandler.java
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
public class RequestConfirmorHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestConfirmorHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
				
		LOG.info("RequestConfirmor");
		
		try{
			//performer, workitem 할당
			//Step 1. 결재자 전 처리
			CoviFlowWorkHelper.processNormal();
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			
			CoviFlowWorkHelper.setActivity("confirmor", delegateTask, ctx, "T019");				
			//Step 2. 메일발송
			CoviFlowWorkHelper.sendMailWorkitem();
			//Step 3. 결재행위 후처리
			CoviFlowWorkHelper.processWorkitem();
		
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestConfirmorHandler", e);
			throw e;
		}
		
			
	}

}