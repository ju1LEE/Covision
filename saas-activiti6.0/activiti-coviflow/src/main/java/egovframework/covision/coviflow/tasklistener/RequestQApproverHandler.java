/**
 * @Class Name : RequestQApproverHandler.java
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
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestQApproverHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestQApproverHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("Request QApprover");
		
		try{
			//performer, workitem 할당
			//Step 1. 대기열 결재
			CoviFlowWorkHelper.processQApprovers();
			//Step 2. 동시결재자 완료처리
			CoviFlowWorkHelper.processApprovers();		
			//Step 3. 메일발송
			CoviFlowWorkHelper.sendMailWorkitem();
			//Step 4. 결재행위 후처리
			CoviFlowWorkHelper.processWorkitem();
		
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestQApproverHandler", e);
			throw e;
		}
			
	}

}