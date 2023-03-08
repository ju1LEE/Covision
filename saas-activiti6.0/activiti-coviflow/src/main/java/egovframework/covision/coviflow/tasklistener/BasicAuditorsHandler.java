/**
 * @Class Name : BasicAuditorsHandler.java
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

import java.util.Date;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class BasicAuditorsHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicAuditorsHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("Auditors");
		
		try{
			//Step 1. 감사자 할당
			//workitem, workitem desc, performer 생성 및 update
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			
			
			//개인감사, 준법감시 분리
			JSONParser parser = new JSONParser();
			//active division을 가져옴
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject pendingObj = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObj.get("step");			
			String routeName = pendingStep.get("name").toString();
			
			//감사 T016, 준법감시 : T017
			String subkind = "T016";
			if(routeName.equalsIgnoreCase("audit_law")){
				subkind = "T017";
			}
			
			CoviFlowWorkHelper.setActivity("auditors", delegateTask, ctx, subkind);
			
			//Step 2. 메일발송
			//Step 3. 결재행위 후처리
			CoviFlowWorkHelper.processWorkitem();
					
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicAuditorsHandler", e);
			throw e;
		}
		
		
	}

}