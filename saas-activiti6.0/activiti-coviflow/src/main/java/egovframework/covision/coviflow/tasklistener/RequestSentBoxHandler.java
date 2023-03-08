/**
 * @Class Name : RequestSentBoxHandler.java
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestSentBoxHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestSentBoxHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
				
		LOG.info("RequestSentBox");
		
		try{
			JSONParser parser = new JSONParser();
			//Step 1. performer, workitem을 할당.
			//insert vrole member
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx);
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String isSecure = pDesc.get("IsSecureDoc").toString();
			String scSBox = "";
			if(formInfoExt.containsKey("scSBox")){
				scSBox = (String)formInfoExt.get("scSBox").toString();
			}
			
			if(isSecure.equalsIgnoreCase("N") && scSBox.equalsIgnoreCase("Y")){
				JSONObject root = (JSONObject)apvLineObj.get("steps");
				
				Object divisionObj = root.get("division");
				JSONArray divisions = new JSONArray();
				if(divisionObj instanceof JSONObject){
					divisions.add(divisionObj);
				} else {
					divisions = (JSONArray)divisionObj;
				}
				
				JSONObject division = (JSONObject)divisions.get(0);
				String ouCode = (String)division.get("oucode");
				String ouName = (String)division.get("ouname");
				
				CoviFlowWorkHelper.setListActivity("발신함", Integer.parseInt(delegateTask.getProcessInstanceId()), delegateTask.getId(), ctx, ouCode, ouName, "S", 528);
			}
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestSentBoxHandler", e);
			throw e;
		}
		
		org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
		LOG.info("발신함 completed");
	}

}