/**
 * @Class Name : RequestSharerHandler.java
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestSharerHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestSharerHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
				
		LOG.info("RequestSharer");
		
		try{
			//Step 1. performer를 할당.
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			JSONParser parser = new JSONParser();
			
			//결재선 변경
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			Integer divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			Integer stepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			Integer stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			
			//workitem, workitem desc, performer 생성 및 update
			apvLineObj = CoviFlowWorkHelper.setActivity("sharer", delegateTask, ctx, "T020");
			
			//complete 이후 후처리
			//결재선 수정 - g_appvLine만 사용
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
				
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", "datecompleted", sdf.format(new Date()));			
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", CoviFlowVariables.APPV_COMP);
			
			//결재선 변경
			if(!stepIndex.equals(stepSize - 1)){
				apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
				apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
			}
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestSharerHandler", e);
			throw e;
		}
		
		org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
	}

}