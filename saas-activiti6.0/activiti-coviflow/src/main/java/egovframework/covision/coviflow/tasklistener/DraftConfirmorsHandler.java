/**
 * @Class Name : DraftConfirmorsHandler.java
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
public class DraftConfirmorsHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(DraftConfirmorsHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		//공람자 : 기완료된 결재문서에 대해서 읽음여부및 의견만 개진
		LOG.info("DraftConfirmors");
		
		try{
			//Step 1. performer, workitem을 할당.
			
			//결재선 변경
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			
			//Step 1. performer, workitem을 할당.
			CoviFlowWorkHelper.setMultiActivityConfirmors("Reviewers", delegateTask, ctx, delegateTask.getAssignee(), "T018");	
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("DraftConfirmorsHandler", e);
			throw e;
		}
			 
	}

}