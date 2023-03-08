/**
 * @Class Name : RequestConfirmorsHandler.java
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
public class RequestConfirmorsHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestConfirmorsHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("RequestConfirmors");
		
		try{
			//Step 1. 공람자 전 처리
			CoviFlowWorkHelper.postProcessReviewer();
			//Step 2. 공람자 performer, workitem 할당
			//Step 3. 공람자 후처리
			CoviFlowWorkHelper.postProcessReviewer();
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestConfirmorsHandler", e);
			throw e;
		}
		
	}

}