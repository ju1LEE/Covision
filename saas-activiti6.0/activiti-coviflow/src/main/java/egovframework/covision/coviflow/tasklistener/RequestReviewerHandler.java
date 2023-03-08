/**
 * @Class Name : RequestReviewerHandler.java
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
public class RequestReviewerHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestReviewerHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("RequestReviewer");
		
		try{
			//결재선 변경
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			CoviFlowWorkHelper.setMultiActivityReview("Consultors", delegateTask, ctx, delegateTask.getAssignee(), "T005");	
			
			//Step 1. 후결자 전처리
			CoviFlowWorkHelper.preProcessReviewer();
			//Step 2. performer, workitem를 할당.
			//Step 3. 후결자 후처리
			CoviFlowWorkHelper.postProcessReviewer();

		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestReviewerHandler", e);
			throw e;
		}
		
		
	}

}