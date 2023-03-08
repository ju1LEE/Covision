/**
 * @Class Name : SubLoopResult.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.javadelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
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
import egovframework.covision.coviflow.excutionlistener.SubStartExcutionListener;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWSHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class SubLoopResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(SubLoopResult.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("SubLoopResult");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			String ctx = CoviFlowVariables.getGlobalContextStr(execution);
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			String execId = execution.getId();  // (String)execution.getVariable("g_execid");
			//결재선 수정에 대한 처리
			JSONObject apvLineObj_org = new JSONObject();
			JSONObject apvLineObj = new JSONObject();
			Object apvLine_org = execution.getVariable("g_appvLine");
			Object apvLine = execution.getVariable("g_sub_appvLine");
			boolean isApvLineModified = false;
			
			if(apvLine_org instanceof LinkedHashMap)
				apvLineObj_org = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine_org));
			else
				apvLineObj_org = (JSONObject)parser.parse(apvLine_org.toString());
			
			if(apvLine != null){
				if(apvLine instanceof LinkedHashMap){
					apvLineObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
				} else {
					apvLineObj = (JSONObject)parser.parse(apvLine.toString());
				}
				if(execution.hasVariable("g_isChangeLine") && execution.getVariable("g_isChangeLine").toString().equalsIgnoreCase("true")){
					isApvLineModified = true;
				}
			}else{
				apvLineObj = (JSONObject)parser.parse(apvLineObj_org.toString());
			}
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String routeType = pendingObject.get("routeType").toString();
			
			// 결재선을 parsing
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
						
			Integer activeDivisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			Integer personSize = Integer.parseInt(pendingPerson.get("personSize").toString());
			Integer personIndex = Integer.parseInt(pendingPerson.get("personIndex").toString());
			Integer pendingStepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			String isPerson = pendingPerson.get("isPerson").toString();
			String deputyCode = pendingPerson.get("deputyCode").toString();
			
			//processdescript 업데이트를 위한 변수
			String isModified = "";
			String isFile = "";
			String isComment = "";
			String isSubjectModified = "";
			
			if(isPerson.equalsIgnoreCase("true")){
				JSONObject person = (JSONObject)pendingPerson.get("person");
				
				if(person.containsKey("taskid")){
					String taskid = (String)person.get("taskid");
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
					
					// 알림 메시지를 위한 parameter
					String formSubject = execution.getVariable("g_subject").toString();
					String approverCode = person.get("code").toString(); //pendingObject.get("approverCode").toString();
					String formName = ctxJsonObj.get("FormName").toString();
					String usid = ctxJsonObj.get("usid").toString();
					ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getCompletedMessageReceivers_AddOu(apvLineObj, false, "");
					
					if(execution.hasVariable("g_action_" + taskid))
					{
						actionMode = (String)execution.getVariable("g_action_" + taskid);
						actionComment = (String)execution.getVariable("g_actioncomment_" + taskid);
						actionComment_Attach = (ArrayList)execution.getVariable("g_actioncomment_attach_" + taskid);
						signImage = (String)execution.getVariable("g_signimage_" + taskid);	
						isMobile = (String)execution.getVariable("g_isMobile_" + taskid);
						isBatch = (String)execution.getVariable("g_isBatch_" + taskid);
												
						if(StringUtils.isNotBlank(actionComment)){
							isComment = "true";
							comment.put("#text", actionComment);
						}
						
						//삭제
						execution.removeVariable("g_action_" + taskid);
						execution.removeVariable("g_actioncomment_" + taskid);
						execution.removeVariable("g_actioncomment_attach_" + taskid);
						execution.removeVariable("g_signimage_" + taskid);
						execution.removeVariable("g_isMobile_" + taskid);
						execution.removeVariable("g_isBatch_" + taskid);
					}
					
					boolean isSubstituted = false;
					//대결자가 지정되어 있고 결재선에 대결자가 있으면
					if(StringUtils.isNotBlank(deputyCode)){
						isSubstituted = true;
					}
					
					//전결 처리
					boolean isAuthorized = false;
					if(person.containsKey("taskinfo")){
						JSONObject pTaskInfo = (JSONObject)person.get("taskinfo");
						if(pTaskInfo.containsKey("kind")){
							String kind = (String)pTaskInfo.get("kind");
							if(kind.equalsIgnoreCase("authorize")){
								isAuthorized = true;
							}
						}

						if(pTaskInfo.containsKey("comment")){
							hasOldComment = true;
							comment.put("#text", actionComment);
						}
					}
					
					//필요할 경우, 전달, 보류, 지정반려를 추가 할 것.
					if(actionMode.equalsIgnoreCase("REJECTTO")){//지정반려
						CoviFlowWorkHelper.processRejectTo(CoviFlowApprovalLineHelper.getRejecteeStepForOU(apvLineObj, activeDivisionIndex));
						
						if(routeType.equalsIgnoreCase("consult")){
							CoviFlowWorkHelper.processResultForOu(apvLineObj, execId, isMobile);
						} else {
							CoviFlowWorkHelper.processRejectForOu(pendingOu, isMobile);
						}

						if(isSubstituted){
							//status
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "rejectedto");
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "status", "rejectedto");
							
							//result
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "rejectedto");
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "result", "bypassed");
							
							//datecompleted
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "datecompleted", sdf.format(new Date()));
							
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
							}
							
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
							}
							
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
							}
							
							//signimage
							if(StringUtils.isNotBlank(signImage)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
							}
						} else {
							//pending step의 deputy값 초기화
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("taskID", taskid);
							processDAO.updateWorkItemForDeputy(params);
							
							if(!isAuthorized){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "rejectedto");
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "rejectedto");
							} else{
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "rejectedto");
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "authorized");
							}
							
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
							
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
							}
							
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
							}
							
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
							}
							
							//signimage
							if(StringUtils.isNotBlank(signImage)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
							}
						}
						
						//결재선 변경
						if(!personIndex.equals(personSize - 1)&&!isAuthorized){
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, CoviFlowVariables.APPV_PENDING);
						}
					} else if(actionMode.equalsIgnoreCase("REJECTTODEPT")){//부서내 반송
						int tmpProcessid = Integer.parseInt(execId);
						CoviFlowWorkHelper.processCancel(tmpProcessid);
						CoviFlowWorkHelper.workitemCancel(tmpProcessid);

						apvLineObj = CoviFlowApprovalLineHelper.getClearOU_Person(apvLineObj, activeDivisionIndex, pendingStepIndex);
						
						execution.setVariable("g_sub_approvalResult_" + execId, "rejectedtodept");
						if(execution.getProcessDefinitionId().toString().contains("request")){
							execution.setVariable("g_request_approvalResult", "cancelled");	
						} else {
							execution.setVariable("g_basic_approvalResult", "cancelled");
						}
					} else if(actionMode.equalsIgnoreCase("REJECT")){
						
						if(routeType.equalsIgnoreCase("consult")){
							CoviFlowWorkHelper.processResultForOu(apvLineObj, execId, isMobile);
						} else {
							CoviFlowWorkHelper.processRejectForOu(pendingOu, isMobile);
						}
						
						if(isSubstituted){
							//status
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "rejected");
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "status", "rejected");
							
							//result
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "rejected");
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "result", "bypassed");
							
							//datecompleted
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "datecompleted", sdf.format(new Date()));
							
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
							}
							
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
							}
							
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
							}
							
							//signimage
							if(StringUtils.isNotBlank(signImage)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
							}
						} else {
							//pending step의 deputy값 초기화
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("taskID", taskid);
							processDAO.updateWorkItemForDeputy(params);
							
							if(!isAuthorized){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, CoviFlowVariables.APPV_REJECT);
							} else{//전결
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "rejected");
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "rejected");
							}
							
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
							
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
							}
							
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
							}
							
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
							}
							
							//signimage
							if(StringUtils.isNotBlank(signImage)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
							}
						}
						
						execution.setVariable("g_sub_approvalResult_" + execId, "rejected");
						
					} else if(actionMode.equalsIgnoreCase("APPROVAL")){
						//추가적으로 분석이 필요
						CoviFlowWorkHelper.processResultForOu(apvLineObj, execId, isMobile, isBatch);

						if(isSubstituted){
							//status
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "completed");
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "status", "completed");
							
							//result
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "substituted");
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "result", "bypassed");
							
							//datecompleted
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, "datecompleted", sdf.format(new Date()));
							
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
							}
							
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
							}
							
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
							}
							
							//signimage
							if(StringUtils.isNotBlank(signImage)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
							}
							
							//결재선 변경
							if(!personIndex.equals(personSize - 2)&&!isAuthorized){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 2, CoviFlowVariables.APPV_PENDING);
							}
							
						} else {
							//pending step의 deputy값 초기화
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("taskID", taskid);
							processDAO.updateWorkItemForDeputy(params);
							
							if(!isAuthorized){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, CoviFlowVariables.APPV_COMP);
							} else{
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "status", "completed");
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "result", "authorized");
							}
							
							CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
							
							//comment
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
							}
							
							if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
							}
							
							//mobile
							if(isMobile.equalsIgnoreCase("Y")){
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
							}
							
							//signimage
							if(StringUtils.isNotBlank(signImage)){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
							}
							
							//결재선 변경
							if(!personIndex.equals(personSize - 1)&&!isAuthorized){
								CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex + 1, CoviFlowVariables.APPV_PENDING);
							}
						}
						
					} else if(actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW")){
						
						JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
						
						if(pDesc.containsKey("FormInstID") && !pDesc.get("FormInstID").toString().equalsIgnoreCase("")) {
							fiid = Integer.parseInt(pDesc.get("FormInstID").toString());
						}
						
						CoviFlowWorkHelper.processCancelForSubProcess(fiid, pendingStep, ctx);
						
						//알림 호출
						receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, actionMode);
						
						if(receivers.size() > 0){
							CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, actionMode, Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
						}
						
						if(execution.getProcessDefinitionId().toString().contains("request")){
							execution.setVariable("g_request_approvalResult", "cancelled");	
						} else {
							execution.setVariable("g_basic_approvalResult", "cancelled");
						}
						
						//task 강제 종료
						String allotType = pendingObject.get("allotType").toString();
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
									if(!ei.equalsIgnoreCase(execId)){
										JSONObject pendingPer = CoviFlowApprovalLineHelper.getPendingPerson(ou);
										String isPer = pendingPer.get("isPerson").toString();
										if(isPer.equalsIgnoreCase("true")){
											JSONObject p = (JSONObject)pendingPer.get("person");
											if(p.containsKey("taskid")){
												ProcessEngines.getDefaultProcessEngine().getTaskService().complete(p.get("taskid").toString());	
											}
										}else{
											//execution.getEngineServices().getTaskService().complete(ou.get("taskid").toString());
										}
									}
								}
							}
						}
						
						String legacyInfo = "";
						if(execution.hasVariable("g_isLegacy")){
							legacyInfo = (String)execution.getVariable("g_isLegacy");	
						}
						if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "WITHDRAW"))
						{
							CoviFlowWorkHelper.callLegacy(execution, "WITHDRAW", usid, taskid, CoviFlowWorkHelper.base64Decode(actionComment));
						}
					}
					
					// 의견알림추가 - 일반케이스(결재/반려/동의/거부)만 발송
					if(StringUtils.isNotBlank(actionComment) &&
						(actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REJECT") 
							|| actionMode.equalsIgnoreCase("AGREE") || actionMode.equalsIgnoreCase("DISAGREE")
							|| actionMode.equalsIgnoreCase("REDRAFT") || actionMode.equalsIgnoreCase("CHARGE"))
							) {
						if(receivers.size() > 0){
							CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "COMMENT", Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
						}
					}
					
				}
				
			}
				
			/*
			 * isModify - g_isModified - true/false
			 * isComment - IsNotblank(comment)
			 * isFile - g_isFile - true / false
			 * isSubjectModified - g_isSubjectModified - true
			 * */
			if(execution.hasVariable("g_isModified")){
				isModified = execution.getVariable("g_isModified").toString();
			}
			
			if(execution.hasVariable("g_isFile")){
				isFile = execution.getVariable("g_isFile").toString();
			}
			
			if(execution.hasVariable("g_isSubjectModified")){
				isSubjectModified = execution.getVariable("g_isSubjectModified").toString();
			}
			
			// 의견유무체크시 최종결재선이 필요해서 상단으로 이동 
			apvLineObj_org = CoviFlowApprovalLineHelper.setOuForSubProcess(apvLineObj_org, activeDivisionIndex, pendingStepIndex, pendingOu, execId);
			
			//processdescription update 
			//ctxJsonObj의 값을 수정
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			
			Map<String, Object> procDescUMap = new HashMap<String, Object>();
			Map<String, Object> subjectUMap = new HashMap<String, Object>();
			
			if( !(execution.hasVariable("g_request_approvalResult") && execution.getVariable("g_request_approvalResult") != null && execution.getVariable("g_request_approvalResult").toString().equalsIgnoreCase("cancelled"))
					&& !(execution.hasVariable("g_basic_approvalResult") && execution.getVariable("g_basic_approvalResult") != null && execution.getVariable("g_basic_approvalResult").toString().equalsIgnoreCase("cancelled")) ){
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
				String isCommentUpdate = CoviFlowApprovalLineHelper.chkIsCommentUpdate(apvLineObj_org, isComment, originIsComment);
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
					procDescUMap.put("subject", execution.getVariable("g_subject"));
					
					subjectUMap.put("subject", execution.getVariable("g_subject"));
					subjectUMap.put("formInstID", fiid);
					
					processDAO.updateProcessForSubject(subjectUMap);
					processDAO.updateCirculationForSubject(subjectUMap);
					
					pDesc.put("FormSubject", execution.getVariable("g_subject"));
				} else {
					procDescUMap.put("subject", null);
				}
				
				procDescUMap.put("formInstID", fiid);
				
				if(isModified.equalsIgnoreCase("true")||StringUtils.isNotBlank(isFile)||StringUtils.isNotBlank(isCommentUpdate)||isSubjectModified.equalsIgnoreCase("true")){
					processDAO.updateProcessDescForModify(procDescUMap);
					processDAO.updateCirculationboxDescForModify(procDescUMap);
					
					execution.setVariable("g_context", ctxJsonObj.toJSONString());
				}
				
				//process description table update 
				//Parent Process 까지 업데이트 되도록 수정함. 
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("isReserved", "N");
				params.put("formInstID", fiid);
				processDAO.updateProcessDescForHoldAll(params);
			}
			
			//결재선 update
			if(execution.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj_org.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj_org.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			execution.setVariable("g_appvLine", apvLineObj_org.toJSONString());
			
			//예고자 재생성
			if(isApvLineModified){
				CoviFlowWorkHelper.resetPreviewActivity("preview", execution, ctx, "T010");
			}
			
			//txManager.commit(status);
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("SubLoopResult", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
				
		
	}
}
