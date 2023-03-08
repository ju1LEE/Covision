/**
 * @Class Name : RequestApproversHandler.java
 * @Description : 
 * @Modification Information 
 * @ 2021.03.08 최초생성
 *
 * @author 코비젼 연구소
 * @since 2021.03.08
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.tasklistener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWSHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestApproversCompleteHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestApproversCompleteHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("ApproversComplete");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			String usid = ctxJsonObj.get("usid").toString();
			String formName = ctxJsonObj.get("FormName").toString();
			//정상적인 승인 처리
			//pending인 step ou의 taskid 값을 가지고서 api로 넘어오는 변수 값을 가져옴
			//결재선 수정에 대한 처리
			JSONObject apvLineObj = new JSONObject();
			Object apvLine = delegateTask.getVariable("g_appvLine");
			boolean isApvLineModified = false;
			if(apvLine instanceof LinkedHashMap){
				apvLineObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
			} else {
				apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			}
			if(delegateTask.hasVariable("g_isChangeLine") && delegateTask.getVariable("g_isChangeLine").toString().equalsIgnoreCase("true")){
				isApvLineModified = true;
			}
			
			//연동 처리
			Boolean isLegacy = false;
			String legacyInfo = "";
			if(delegateTask.hasVariable("g_isLegacy")){
				legacyInfo = (String)delegateTask.getVariable("g_isLegacy");	
			}
			//form_info_ext값으로 부터 isLegacy값을 추출
			if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "OTHERSYSTEM"))
			{
				isLegacy = true;
			}
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			Integer activeDivisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			Integer pendingStepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			String routeType = pendingObject.get("routeType").toString();
			String deputyCode = pendingObject.get("deputyCode").toString();
			
			//processdescript 업데이트를 위한 변수
			String isModified = "";
			String isFile = "";
			String isComment = "";
			String isSubjectModified = "";
			
			int commentCnt = 0;
			boolean hasReserved = false;
			
			//알림 메시지를 위한 parameter
			//subject
			String formSubject = delegateTask.getVariable("g_subject").toString();
			//알림 대상 - 결재단계의 모든 사람?
			ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getCompletedMessageReceivers(apvLineObj, false, "");
			
			Object pendingO = pendingStep.get("ou");
			JSONArray ous = new JSONArray();
			if(pendingO instanceof JSONObject){
				JSONObject ouJsonObj = (JSONObject)pendingO;
				ous.add(ouJsonObj);
			} else {
				ous = (JSONArray)pendingO;
			}
			
			for(int i = 0; i < ous.size(); i++)
			{
				//해당 taskid에 대한 처리
				JSONObject pendingOu = (JSONObject)ous.get(i);
				
				Object pendingP = pendingOu.get("person");
				JSONArray persons = new JSONArray();
				if(pendingP instanceof JSONObject){
					JSONObject personJsonObj = (JSONObject)pendingP;
					persons.add(personJsonObj);
				} else {
					persons = (JSONArray)pendingP;
				}
				
				JSONObject personTaskInfo = (JSONObject) ((JSONObject)persons.get(0)).get("taskinfo");
				
				if(pendingOu.containsKey("taskid")){
					String taskid = (String)pendingOu.get("taskid");
					
					if(taskid.equalsIgnoreCase(delegateTask.getId().toString())){
						
						String actionMode = "";
						String actionComment = "";
						List<JSONObject> actionComment_Attach = new ArrayList<JSONObject>();
						String signImage = "";	
						String isMobile = "";
						String isBatch = "";
						JSONObject comment = new JSONObject();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
							sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
						}
						boolean hasOldComment = false;
						
						if(delegateTask.hasVariable("g_action_" + taskid))
						{
							actionMode = (String)delegateTask.getVariable("g_action_" + taskid);
							actionComment = (String)delegateTask.getVariable("g_actioncomment_" + taskid);
							actionComment_Attach = (ArrayList)delegateTask.getVariable("g_actioncomment_attach_" + taskid);
							signImage = (String)delegateTask.getVariable("g_signimage_" + taskid);
							isMobile = (String)delegateTask.getVariable("g_isMobile_" + taskid);
							isBatch = (String)delegateTask.getVariable("g_isBatch_" + taskid);
							
							if(personTaskInfo.containsKey("comment")){
								hasOldComment = true;
							}
							
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								comment.put("#text", actionComment);
							}
							
							//병렬 처리의 경우 삭제 해 버리면 step 상태값 처리가 되지 않음
							delegateTask.removeVariable("g_action_" + taskid);
							delegateTask.removeVariable("g_actioncomment_" + taskid);
							delegateTask.removeVariable("g_actioncomment_attach_" + taskid);
							delegateTask.removeVariable("g_signimage_" + taskid);
							delegateTask.removeVariable("g_isMobile_" + taskid);
							delegateTask.removeVariable("g_isBatch_" + taskid);
						}
						
						boolean isSubstituted = false;
						//대결자가 지정되어 있고 결재선에 대결자가 있으면
						if(StringUtils.isNotBlank(deputyCode) && pendingOu.get("person") instanceof JSONArray){
							isSubstituted = true;
						}
						
						if(actionMode.equalsIgnoreCase("APPROVECANCEL")){//승인취소
							CoviFlowWorkHelper.processApproveCancel(apvLineObj.toJSONString(), activeDivisionIndex, pendingStepIndex);
							
							// 결재선 수정되기 전에 처리하기 위해 위치 이동
							//task 강제 종료
							JSONArray completeTarget = CoviFlowApprovalLineHelper.getActiveTask(ous, taskid);
							for(int j = 0; j < completeTarget.size(); j++)
							{
								JSONObject ou = (JSONObject)completeTarget.get(j);
								String ti = (String)ou.get("taskid");
								org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(ti);
							}
							
							/* 결재선 수정
							 * 1. 현재 결재 단계는 inactive로
							 * 2. 이전 결재 단계는 pending으로
							 * 3. 불필요한 데이터는(taskid, wiid, widescid, pfid, datecompleted, datereceived, comment)  remove
							 * */
							// 아무도 병렬결재 하지 않았을 때
							if(persons.size() > 1 && persons.size() == completeTarget.size() + 1) {
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_PENDING);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_PENDING);
								apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex, "ALL");
							}
							else {										
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, CoviFlowVariables.APPV_PENDING);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "", CoviFlowVariables.APPV_PENDING);
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_INACTIVE);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_INACTIVE);
								
								apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "ALL");
								apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex, "NoID");
							}
							
							//알림 호출
							receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, "APPROVECANCEL");
							if(receivers.size() > 0){
								JSONObject approveCancelledPendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
								String approveCancelledApproverCode = approveCancelledPendingObject.get("approverCode").toString();
								CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approveCancelledApproverCode, formSubject, "APPROVECANCEL", Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
							}
							
							if(delegateTask.getProcessDefinitionId().toString().contains("request")){
								delegateTask.setVariable("g_request_approvalResult", "approvecancelled");	
							} else {
								delegateTask.setVariable("g_basic_approvalResult", "approvecancelled");
							}
						} else if(actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW")){//회수, 기안취소
			
							CoviFlowWorkHelper.processCancel(fiid, Integer.parseInt(pendingOu.get("wiid").toString()), ctx, apvLineObj.toJSONString());
							
							//알림 호출
							receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, actionMode);
							if(receivers.size() > 0){
								CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, actionMode, Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
							}
							
							//프로세스 종료
							if(delegateTask.getProcessDefinitionId().toString().contains("request")){
								delegateTask.setVariable("g_request_approvalResult", "cancelled");	
							} else {
								delegateTask.setVariable("g_basic_approvalResult", "cancelled");
							}
							
							//task 강제 종료
							JSONArray completeTarget = CoviFlowApprovalLineHelper.getActiveTask(ous, taskid);
							for(int j = 0; j < completeTarget.size(); j++)
							{
								JSONObject ou = (JSONObject)completeTarget.get(j);
								String ti = (String)ou.get("taskid");
								org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(ti);
							}
							
							isLegacy = false;
							if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "WITHDRAW"))
							{
								CoviFlowWorkHelper.callLegacy(delegateTask, "WITHDRAW", usid, taskid, CoviFlowWorkHelper.base64Decode(actionComment));
							}
						} else if(actionMode.equalsIgnoreCase("APPROVAL")){
							//추가적으로 분석이 필요
							CoviFlowWorkHelper.processResult(apvLineObj, taskid, isMobile, isBatch);
							
							if(isSubstituted){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "status", "completed");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "result", "substituted");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "datecompleted", sdf.format(new Date()));
								//comment
								if(hasOldComment || StringUtils.isNotBlank(actionComment)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "comment", comment);
								}
								
								if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "comment_fileinfo", actionComment_Attach);
								}
								
								//mobile
								if(isMobile.equalsIgnoreCase("Y")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "mobileType", "y");
								}
								
								//signimage
								if(StringUtils.isNotBlank(signImage)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "customattribute1", signImage);
								}
								
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, taskid, "status", "completed");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, taskid, "result", "bypassed");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, taskid, "datecompleted", sdf.format(new Date()));
							}else{
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("taskID", taskid);
								processDAO.updateWorkItemForDeputy(params);
								
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, CoviFlowVariables.APPV_COMP);	
							}
							
							if(delegateTask.getProcessDefinitionId().toString().contains("request")){
								delegateTask.setVariable("g_request_approvalResult", "agreed");	
							} else {
								delegateTask.setVariable("g_basic_approvalResult", "agreed");
							}
						} else if(actionMode.equalsIgnoreCase("REJECT")){
							//추가적으로 분석이 필요
							CoviFlowWorkHelper.processReject(piid, isMobile);
							if(isSubstituted){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "status", "completed");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "result", "substituted");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "datecompleted", sdf.format(new Date()));
								//comment
								if(hasOldComment || StringUtils.isNotBlank(actionComment)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "comment", comment);
								}
								if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "comment_fileinfo", actionComment_Attach);
								}
								//mobile
								if(isMobile.equalsIgnoreCase("Y")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "mobileType", "y");
								}
								//signimage
								if(StringUtils.isNotBlank(signImage)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, taskid, "customattribute1", signImage);
								}
								
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, taskid, "status", "completed");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, taskid, "result", "bypassed");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, taskid, "datecompleted", sdf.format(new Date()));
							}else{
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("taskID", taskid);
								processDAO.updateWorkItemForDeputy(params);
								
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, CoviFlowVariables.APPV_REJECT);
							}
							//delegateTask.setVariable("g_basic_approvalResult", "rejected");
							
							//알림 호출
							// 병렬인 경우, 오류 발생 하여 수정함.
							//String actionUserCode = ((JSONObject)pendingOu.get("person")).get("code").toString();
							String actionUserCode = ((JSONObject)persons.get(0)).get("code").toString();
							
							receivers = CoviFlowApprovalLineHelper.getCompletedPendingMessageReceivers(apvLineObj, actionUserCode);
							if(receivers.size() > 0){
								CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, actionUserCode, formSubject, "REJECT", Integer.toString(piid), receivers, apvLineObj));
							}
							
							if(delegateTask.getProcessDefinitionId().toString().contains("request")){
								delegateTask.setVariable("g_request_approvalResult", "rejected");	
							} else {
								delegateTask.setVariable("g_basic_approvalResult", "rejected");
							}
							
							//task 강제 종료
							JSONArray completeTarget = CoviFlowApprovalLineHelper.getActiveTask(ous, taskid);
							for(int j = 0; j < completeTarget.size(); j++)
							{
								JSONObject ou = (JSONObject)completeTarget.get(j);
								String ti = (String)ou.get("taskid");
								org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(ti);
							}
							isLegacy = false;
						}
						
						if(actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REJECT")){
							//결재선 변경
							apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, "datecompleted", sdf.format(new Date()));
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, "comment", comment);
							}
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, "comment_fileinfo", actionComment_Attach);
							}
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, "mobileType", "y");
							}
							//signImage
							if(StringUtils.isNotBlank(signImage)){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, taskid, "customattribute1", signImage);
							}
						}
						
						if(isLegacy){
							String personid = "";
							if(pendingOu.get("person") instanceof JSONObject){
								personid = (String)((JSONObject)pendingOu.get("person")).get("code");
							}else if(pendingOu.get("person") instanceof JSONArray){
								personid = (String)((JSONObject)((JSONArray)pendingOu.get("person")).get(0)).get("code");
							}
							
							CoviFlowWorkHelper.callLegacy(delegateTask, "OTHERSYSTEM", personid, taskid, CoviFlowWorkHelper.base64Decode(actionComment));
						}
					}
					
					if(personTaskInfo.get("result").toString().equalsIgnoreCase("reserved")){
						hasReserved = true;
					}
					
					if(personTaskInfo.containsKey("comment") && !((JSONObject)personTaskInfo.get("comment")).get("#text").toString().equals(""))
						commentCnt++;
				}
			} //for문 끝
			
			if(commentCnt > 0)
				isComment = "true";
			else
				isComment = "false";
			
			//action 삭제
			CoviFlowWorkHelper.removeActionAll(delegateTask);
			
			/*
			 * isModify - g_isModified - true/false
			 * isComment - IsNotblank(comment)
			 * isFile - g_isFile - true / false
			 * isSubjectModified - g_isSubjectModified - true
			 * */
			if(delegateTask.hasVariable("g_isModified")){
				isModified = delegateTask.getVariable("g_isModified").toString();
			}
			
			if(delegateTask.hasVariable("g_isFile")){
				isFile = delegateTask.getVariable("g_isFile").toString();
			}
			
			if(delegateTask.hasVariable("g_isSubjectModified")){
				isSubjectModified = delegateTask.getVariable("g_isSubjectModified").toString();
			}
			
			//processdescription update 
			//ctxJsonObj의 값을 수정
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			
			Map<String, Object> procDescUMap = new HashMap<String, Object>();
			Map<String, Object> subjectUMap = new HashMap<String, Object>();
			
			if(isModified.equalsIgnoreCase("true")){
				procDescUMap.put("isModify", "Y");	
				
				pDesc.put("IsModify", "Y");
			} else if(isModified.equalsIgnoreCase("false")){
				procDescUMap.put("isModify", "N");	
				
				pDesc.put("IsModify", "N");
			} else {
				procDescUMap.put("isModify", null);
			}
			
			if(isFile.equalsIgnoreCase("true")){
				procDescUMap.put("isFile", "Y");
				
				pDesc.put("IsFile", "Y");
			} else if(isFile.equalsIgnoreCase("false")){
				procDescUMap.put("isFile", "N");	
				
				pDesc.put("IsFile", "N");
			} else {
				procDescUMap.put("isFile", null);
			}
			
			// 지정반려 후 진행 , 부서내반송 후 진행 , 승인취소 후 진행 , 병렬결재 등 문제로 인해 전체결재라인에서 의견 유무 다시 체크 (부서내반송으로 인해 actionComment체크도 필요하므로 기존로직도 유지)
			String originIsComment = (pDesc.containsKey("IsComment")) ? (String)pDesc.get("IsComment") : "";
			String isCommentUpdate = CoviFlowApprovalLineHelper.chkIsCommentUpdate(apvLineObj, isComment, originIsComment);
			if(StringUtils.isBlank(isCommentUpdate)) {
				procDescUMap.put("isComment", null); // 빈값인경우 스킵
			}
			else if(isCommentUpdate.equalsIgnoreCase("false")) {
				procDescUMap.put("isComment", "N");	
				pDesc.put("IsComment", "N");
			}else if(isCommentUpdate.equalsIgnoreCase("true")) { 
				procDescUMap.put("isComment", "Y");	
				pDesc.put("IsComment", "Y");
			}
			
			if(isSubjectModified.equalsIgnoreCase("true")){
				procDescUMap.put("subject", delegateTask.getVariable("g_subject"));
				
				subjectUMap.put("subject", delegateTask.getVariable("g_subject"));
				subjectUMap.put("formInstID", fiid);
				
				processDAO.updateProcessForSubject(subjectUMap);
				processDAO.updateCirculationForSubject(subjectUMap);
				
				pDesc.put("FormSubject", delegateTask.getVariable("g_subject"));
			} else {
				procDescUMap.put("subject", null);
			}
			
			procDescUMap.put("formInstID", fiid);
			
			if(isModified.equalsIgnoreCase("true")||StringUtils.isNotBlank(isFile)||StringUtils.isNotBlank(isCommentUpdate)||isSubjectModified.equalsIgnoreCase("true")){
				processDAO.updateProcessDescForModify(procDescUMap);
				processDAO.updateCirculationboxDescForModify(procDescUMap);
				
				delegateTask.setVariable("g_context", ctxJsonObj.toJSONString());
			}
			
			//process description table update 
			if(!hasReserved){
				//Parent Process 까지 업데이트 되도록 수정함. 
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("isReserved", "N");
				params.put("formInstID", fiid);
				processDAO.updateProcessDescForHoldAll(params);
			}
			
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//예고자 재생성
			if(isApvLineModified){
				CoviFlowWorkHelper.resetPreviewActivity("preview", delegateTask, ctx, "T010");
			}
			
			//txManager.commit(status);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestApproversCompleteHandler", e);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}

}