/**
 * @Class Name : SubEndExcutionListener.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.excutionlistener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class SubEndExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(SubEndExcutionListener.class);
	
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("SubEnd");
		/*
		 * 프로세스 종료 처리
		 * */
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject ProcessDescription = (JSONObject) ctxJsonObj.get("ProcessDescription");
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//결재선 수정에 대한 처리
			JSONObject apvLineObj = new JSONObject();
			Object apvLine = execution.getVariable("g_appvLine");
			apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			
			String approverID = null;
			String taskid = null;
			String comment = null;
			
			if( !(execution.hasVariable("g_request_approvalResult") && execution.getVariable("g_request_approvalResult") != null && execution.getVariable("g_request_approvalResult").toString().equalsIgnoreCase("cancelled"))
					&& !(execution.hasVariable("g_basic_approvalResult") && execution.getVariable("g_basic_approvalResult") != null && execution.getVariable("g_basic_approvalResult").toString().equalsIgnoreCase("cancelled")) ){
				//결재선의 pending인 상태를 가져옴
				JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
				JSONObject pendingStep = (JSONObject)pendingObject.get("step");
				Integer activeDivisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
				Integer pendingStepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
				String allotType = pendingObject.get("allotType").toString();
				String routeType = pendingObject.get("routeType").toString();
				String execId = execution.getId().toString();
				String approvalResult = execution.getVariable("g_sub_approvalResult_" + execId).toString();
			
			
				if(routeType.equalsIgnoreCase("consult")){//합의
					if(approvalResult.equalsIgnoreCase("rejected")){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, CoviFlowVariables.APPV_DISAGREE);
					} else {
						apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, CoviFlowVariables.APPV_AGREE);
					}
				} else if(routeType.equalsIgnoreCase("assist")){//협조
					if(approvalResult.equalsIgnoreCase("rejected")){
						if(execution.getProcessDefinitionId().toString().contains("request")){
							execution.setVariable("g_request_approvalResult", "rejected");
						}else{
							execution.setVariable("g_basic_approvalResult", "rejected");
						}
						
						apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, CoviFlowVariables.APPV_REJECT);
						
						if(allotType.equalsIgnoreCase("parallel")){
							//진행중인 task를 종료시키고, "g_sub_approvalResult_" + execId 에 rejected를 입력
							//task 강제 종료
							Object pendingO = pendingStep.get("ou");
							JSONArray ous = new JSONArray();
							if(pendingO instanceof JSONObject){
								JSONObject ouJsonObj = (JSONObject)pendingO;
								ous.add(ouJsonObj);
							} else {
								ous = (JSONArray)pendingO;
							}
							for(int j = 0; j < ous.size(); j++)
							{
								JSONObject ou = (JSONObject)ous.get(j);
								JSONObject t = (JSONObject)ou.get("taskinfo");
								if(t.containsKey("execid")){
									String ei = (String)t.get("execid");
									String stat = (String)t.get("status");
									if(!ei.equalsIgnoreCase(execId)){
										String chkSubResult = "";
										if(execution.hasVariable("g_sub_approvalResult_" + ei))
											chkSubResult = execution.getVariable("g_sub_approvalResult_" + ei).toString();
										if(!chkSubResult.equalsIgnoreCase("rejected") && stat.equalsIgnoreCase("pending")) {
										execution.setVariable("g_sub_approvalResult_" + ei, "rejected");
										JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(ou);
										String isPerson = pendingPerson.get("isPerson").toString();
										if(isPerson.equalsIgnoreCase("true")){
											JSONObject p = (JSONObject)pendingPerson.get("person");
											if(p.containsKey("taskid")){
												ProcessEngines.getDefaultProcessEngine().getTaskService().complete(p.get("taskid").toString());	
											}
										}else{
												execution.setVariable("g_action_" + ou.get("taskid").toString(), "KILL");
												ProcessEngines.getDefaultProcessEngine().getTaskService().complete(ou.get("taskid").toString());
											}
										}
									}
								}
							}
						}
					} else {
						apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, CoviFlowVariables.APPV_COMP);
					}
				} else if(routeType.equalsIgnoreCase("audit")){//감사
					if(approvalResult.equalsIgnoreCase("rejected")){
						if(execution.getProcessDefinitionId().toString().contains("request")){
							execution.setVariable("g_request_approvalResult", "rejected");
						}else{
							execution.setVariable("g_basic_approvalResult", "rejected");
						}
						
						apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, CoviFlowVariables.APPV_REJECT);
						
					} else {
						apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, CoviFlowVariables.APPV_COMP);
					}
				}
				
				apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, activeDivisionIndex, pendingStepIndex, execId, "datecompleted", sdf.format(new Date()));
				
				//결재선 update
				if(execution.hasVariable("g_isDistribution")){
					Map<String, Object> domainUMap = new HashMap<String, Object>();
					domainUMap.put("domainDataContext", apvLineObj.toJSONString());
					domainUMap.put("processID", Integer.parseInt(execution.getProcessInstanceId()));
					processDAO.updateAppvLinePublic(domainUMap);
				} else {
					Map<String, Object> domainUMap = new HashMap<String, Object>();
					domainUMap.put("domainDataContext", apvLineObj.toJSONString());
					domainUMap.put("formInstID", fiid);
					processDAO.updateAllAppvLinePublic(domainUMap);
				}
				
				execution.setVariable("g_appvLine", apvLineObj.toJSONString());
				
				String businessState;
				//g_hasReceiptBox 값에 따라 분기처리 구현 필요
				//int state = 528;
				int state = 288; // Basic 완료 시 528 처리되도록 변경함.
				
				if(approvalResult.equalsIgnoreCase("rejected")){
					businessState = "02_02_01";
				}
				else {
					businessState = "02_01_03";
				}
				
				//process update
				Map<String, Object> procUMap = new HashMap<String, Object>();
				procUMap.put("businessState", businessState);
				procUMap.put("processState", state);
				procUMap.put("processID", Integer.parseInt(execId));
				
				processDAO.updateProcess(procUMap);
				
				// 수신처리함에 저장할 것
				Object pendingO = pendingStep.get("ou");
				JSONArray ous = new JSONArray();
				if(pendingO instanceof JSONObject){
					JSONObject ouJsonObj = (JSONObject)pendingO;
					ous.add(ouJsonObj);
				} else {
					ous = (JSONArray)pendingO;
				}
				for(int j = 0; j < ous.size(); j++)
				{
					JSONObject ou = (JSONObject)ous.get(j);
					JSONObject t = (JSONObject)ou.get("taskinfo");
					if(t.containsKey("execid")){
						String ei = (String)t.get("execid");
						if(ei.equalsIgnoreCase(execId)&& ProcessDescription.get("IsSecureDoc").equals("N")){
							CoviFlowWorkHelper.setListActivity("수신처리함", Integer.parseInt(execId), "", CoviFlowVariables.getGlobalContextStr(execution), (String)ou.get("code"), (String)ou.get("name"), "AA", 528);			
						}
						
						if(ei.equalsIgnoreCase(execId)){
							if(ou.containsKey("person")){
								Object personObj = ou.get("person");
								JSONArray persons = new JSONArray();
								if(personObj instanceof JSONObject){
									JSONObject jsonObj = (JSONObject)personObj;
									persons.add(jsonObj);
								} else {
									persons = (JSONArray)personObj;
								}
								
								if(persons != null){
									for(int l = 0; l < persons.size(); l++)
									{
										JSONObject person = (JSONObject)persons.get(l);
										JSONObject taskinfo = new JSONObject();
										if(person.containsKey("taskinfo")){
											taskinfo = (JSONObject)person.get("taskinfo");
											String s = "";
											if(taskinfo.containsKey("status")){
												s = (String)taskinfo.get("status");
												
												if((s.equalsIgnoreCase("completed") || s.equalsIgnoreCase("rejected")) && ou.containsKey("taskid")){
													approverID = (String)person.get("code");
													comment = taskinfo.containsKey("comment") ? CoviFlowWorkHelper.base64Decode((String)((JSONObject)taskinfo.get("comment")).get("#text")) : "";
													taskid = person.containsKey("taskid") ? (String)person.get("taskid").toString() : (String)ou.get("taskid").toString();
												}
											}
										}
									}
								}
							}
						}
					}
				}
				
				
				//연동 처리
				String legacyInfo = "";
				if(execution.hasVariable("g_isLegacy")){
					legacyInfo = (String)execution.getVariable("g_isLegacy");	
				}
				//form_info_ext값으로 부터 isLegacy값을 추출
				if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "OTHERSYSTEM"))
				{
					// 부서합의/협조 종료 시 연동 호출
					if(routeType.equalsIgnoreCase("assist")){//협조
						if(approvalResult.equalsIgnoreCase("rejected")){
							CoviFlowWorkHelper.callLegacy(execution, "SUBREJECT", approverID, taskid, comment);
						}
						else {
							CoviFlowWorkHelper.callLegacy(execution, "SUBCOMPLETE", approverID, taskid, comment);
						}
					} else if(routeType.equalsIgnoreCase("audit")){//감사
						if(approvalResult.equalsIgnoreCase("rejected")){
							CoviFlowWorkHelper.callLegacy(execution, "SUBREJECT", approverID, taskid, comment);
						}
						else {
							CoviFlowWorkHelper.callLegacy(execution, "SUBCOMPLETE", approverID, taskid, comment);
						}
					}
					else { // consult
						CoviFlowWorkHelper.callLegacy(execution, "SUBCOMPLETE", approverID, taskid, comment);
					}
				}
			}
			
			//txManager.commit(status);
			
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("SubEnd", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	
}