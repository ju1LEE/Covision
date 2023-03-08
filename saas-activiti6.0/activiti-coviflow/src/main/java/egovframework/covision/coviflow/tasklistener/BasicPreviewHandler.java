/**
 * @Class Name : BasicPreviewHandler.java
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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class BasicPreviewHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicPreviewHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("BasicPreview");
		
		try{
			//pending인 대상자를 제외한 나머지 
			//performer, workitem 할당.
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			CoviFlowWorkHelper.setPreviewActivity("preview", delegateTask, ctx, "T010");
			
			//Step 1. performer를 할당.
			//LOG.info("Preview performer를 할당");
			
			//V Role insert
			
			//Step 2. 변수 값을 넣어 줌.
			//PGlobal("PreviewerWIID").Value = Workitem.id
			//PGlobal("PreviewerPFID").Value = Workitem.performerId

		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicPreviewHandler", e);
			throw e;
		}

		//task complete 처리
		ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
		LOG.info("BasicPreview task completed");
	}

}