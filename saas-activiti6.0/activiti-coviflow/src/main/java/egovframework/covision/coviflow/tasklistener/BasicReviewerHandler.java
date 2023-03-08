/**
 * @Class Name : BasicReviewerHandler.java
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
public class BasicReviewerHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicReviewerHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("BasicReviewer");
		
		try{
			//Step 1. performer를 할당.
			//LOG.info("Reviewer performer를 할당");
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);			
			CoviFlowWorkHelper.setMultiActivityReview("Consultors", delegateTask, ctx, delegateTask.getAssignee(), "T005");	
			
			//Step 2. 후결자 전처리
			//Step 3. 후결처리

		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicReviewerHandler", e);
			throw e;
		}
		
	}

}