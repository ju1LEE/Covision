/**
 * @Class Name : RequestPreviewHandler.java
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
public class RequestPreviewHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestPreviewHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		//예고함
		LOG.info("RequestPreview");
		
		try{
			//pending인 대상자를 제외한 나머지 
			//performer, workitem 할당.
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			CoviFlowWorkHelper.setPreviewActivity("preview", delegateTask, ctx, "T010");
			
			//변수 설정
			//PGlobal("PreviewerWIID").Value = Workitem.id
			//PGlobal("PreviewerPFID").Value = Workitem.performerId
		} catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestPreviewHandler", e);
			throw e;
		}
		
		//task complete 처리
		//async로 처리되는 것 같은 데 문제 여부 확인이 필요.
		org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
		LOG.info("RequestPreview task completed");
	}

}