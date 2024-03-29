/**
 * @Class Name : BasicCoordinatorsHandler.java
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
public class BasicCoordinatorsHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicCoordinatorsHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("Coordinators");
		
		try{
			//workitem, workitem desc, performer 생성 및 update
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			CoviFlowWorkHelper.setActivity("coordinators", delegateTask, ctx, "T004");
			
			//Step 1. 협조자 전처리
			
			//Step 2. 메일발송
			
			//Step 3. 결재행위 후처리
			CoviFlowWorkHelper.processWorkitem();
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicCoordinatorsHandler", e);
			throw e;
		}
		
	}

}