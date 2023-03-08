/**
 * @Class Name : SubReceiptBoxResult.java
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
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWSHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class SubReceiptBoxResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(SubReceiptBoxResult.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("SubReceiptBoxResult");

		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			String destination = "";
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			String ctx = CoviFlowVariables.getGlobalContextStr(execution);
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			String formName = ctxJsonObj.get("FormName").toString();
			String usid = ctxJsonObj.get("usid").toString();
			String formSubject = execution.getVariable("g_subject").toString();
			String isRedraftCharge = "n";
			
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
			
			String execId = execution.getId();
			
			//재기안자가 존재하면
			String taskid = "";
			String actionMode = "";
			String actionComment = "";
			List<JSONObject> actionComment_Attach = new ArrayList<JSONObject>();
			String signImage = "";	
			String isMobile = "";
			String isBatch = "";
			
			//processdescript 업데이트를 위한 변수
			String isModified = "";
			String isFile = "";
			String isComment = "";
			String isSubjectModified = "";
			
			Map<String, Object> m = new HashMap<String, Object>();
			m = execution.getVariables();
			for( String key : m.keySet() ){
				if(m.get(key)!= null){
					if(m.get(key).toString().equals("REDRAFT")){
						actionMode = "REDRAFT";
						taskid = key.split("g_action_")[1];
					} else if(m.get(key).toString().equals("REJECT")){
						actionMode = "REJECT";
						taskid = key.split("g_action_")[1];
					} else if(m.get(key).toString().equals("APPROVAL")){
						actionMode = "APPROVAL";
						taskid = key.split("g_action_")[1];
					} else if(m.get(key).toString().equals("ABORT")){
						actionMode = "ABORT";
						taskid = key.split("g_action_")[1];
					} else if(m.get(key).toString().equals("WITHDRAW")){
						actionMode = "WITHDRAW";
						taskid = key.split("g_action_")[1];
					} else if(m.get(key).toString().equals("CHARGE")){
						actionMode = "CHARGE";
						taskid = key.split("g_action_")[1];
					} else if(m.get(key).toString().equals("KILL")){
						taskid = key.split("g_action_")[1];
					}
				}
	        }
			
			JSONObject comment = new JSONObject();
			JSONObject paramChargeInfo = new JSONObject();
			if(StringUtils.isNotBlank(taskid)){
				actionComment = (String)execution.getVariable("g_actioncomment_" + taskid);
				actionComment_Attach = (ArrayList)execution.getVariable("g_actioncomment_attach_" + taskid);
				signImage = (String)execution.getVariable("g_signimage_" + taskid);
				paramChargeInfo = (JSONObject)JSONValue.parse(JSONValue.toJSONString(execution.getVariable("g_charge_" + taskid)));
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
			
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			
			
			int personSize = 0;
			String charge = "";
			
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			
			//결재선 미리 변경
			CoviFlowApprovalLineHelper.setPersonTask(pendingOu, 0, CoviFlowVariables.APPV_PENDING);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			if(personSize <= 1 && (actionMode.equalsIgnoreCase("REJECT") || actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REDRAFT"))){
				apvLineObj = CoviFlowWorkHelper.setActivityForSubProcess("approve", execution, ctx, "T006", Integer.parseInt(taskid), 528);
			}
			
			pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			pendingStep = (JSONObject)pendingObject.get("step");
			pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);

			String routeType = pendingObject.get("routeType").toString();
			Integer activeDivisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			//Integer stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			Integer pendingStepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			// 알림 메시지를 위한 parameter
			String approverCode = pendingObject.get("approverCode").toString();
			ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getCompletedMessageReceivers_AddOu(apvLineObj, false, "");
			
			Integer personIndex = null;
			JSONObject person = new JSONObject();
			
			if(pendingPerson.get("isPerson").toString().equalsIgnoreCase("TRUE")){
				personIndex = Integer.parseInt(pendingPerson.get("personIndex").toString());
				person = (JSONObject)pendingPerson.get("person");
				
				personSize = Integer.parseInt(pendingPerson.get("personSize").toString());
				charge = person.get("name").toString();
				approverCode = person.get("code").toString();
			}
			
			if(actionMode.equalsIgnoreCase("REJECT")){
				if(routeType.equalsIgnoreCase("consult")){
					CoviFlowWorkHelper.processResultForOu(apvLineObj, execId, isMobile);
				} else {
					CoviFlowWorkHelper.processRejectForOu(pendingOu, isMobile);
				}
				
				//pending step의 deputy값 초기화
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("taskID", taskid);
				processDAO.updateWorkItemForDeputy(params);
				
				//apvLineObj = CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, CoviFlowVariables.APPV_REJECT);
				CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, CoviFlowVariables.APPV_REJECT); // apvLineObj을 ou로 덮는현상 방지
				
				CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
				
				//comment
				if(StringUtils.isNotBlank(actionComment)){
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
				}
				
				if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
				}
				
				//mobile
				if(isMobile.equalsIgnoreCase("Y")){
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "mobileType", "y");
				}
				
				//signimage
				if(StringUtils.isNotBlank(signImage)){
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
				}
				
				execution.setVariable("g_sub_approvalResult_" + execId, "rejected");
			} else if(actionMode.equalsIgnoreCase("APPROVAL")){ // || actionMode.equalsIgnoreCase("REDRAFT")){
				CoviFlowWorkHelper.processResultForOu(apvLineObj, execId, isMobile, isMobile);
				 
				//pending step의 deputy값 초기화
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("taskID", taskid);
				processDAO.updateWorkItemForDeputy(params);
				
				CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, CoviFlowVariables.APPV_COMP);
				
				CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));
				
				//comment
				if(StringUtils.isNotBlank(actionComment)){
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
				}
				
				if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
				}
				
				//mobile
				if(isMobile.equalsIgnoreCase("Y")){
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "mobileType", "y");
				}
				
				//signimage
				if(StringUtils.isNotBlank(signImage)){
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
				}
				
			} else if(actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW")){//회수, 기안취소
				
				JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
				
				if(pDesc.containsKey("FormInstID") && !pDesc.get("FormInstID").toString().equalsIgnoreCase("")) {
					fiid = Integer.parseInt(pDesc.get("FormInstID").toString());
				}
				
				CoviFlowWorkHelper.processCancelForSubProcess(fiid, pendingStep, ctx);
				
				//알림 호출
				//ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, actionMode);
				receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, actionMode);
				
				if(receivers.size() > 0){
					CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, actionMode, Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
				}
				
				//프로세스 종료
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
								String isPerson = pendingPer.get("isPerson").toString();
								if(isPerson.equalsIgnoreCase("true")){
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
			} else if(actionMode.equalsIgnoreCase("CHARGE")){
				if(paramChargeInfo != null){
					CoviFlowWorkHelper.setActivity("Charge", execution, ctx, Integer.parseInt(execId), taskid, 288, "T008");
					isRedraftCharge = "y";
				}
				
			} else if(!actionMode.equalsIgnoreCase("")){
				//comment
				if(StringUtils.isNotBlank(actionComment)){
					pendingOu = CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment", comment);
				}
				
				if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
					pendingOu = CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "comment_fileinfo", actionComment_Attach);
				}
				
				//signimage
				if(StringUtils.isNotBlank(signImage)){
					pendingOu = CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "customattribute1", signImage);
				}
			}
			
			// 의견알림추가 - 일반케이스(결재/반려/동의/거부)만 발송
			if(StringUtils.isNotBlank(actionComment) &&
				(actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REJECT") 
					|| actionMode.equalsIgnoreCase("AGREE") || actionMode.equalsIgnoreCase("DISAGREE")
					|| actionMode.equalsIgnoreCase("REDRAFT") || actionMode.equalsIgnoreCase("CHARGE"))
					) {
				if(receivers.size() > 0){
					JSONObject apvLineObj_tmp = new JSONObject();
					if(actionMode.equalsIgnoreCase("REDRAFT")){
						CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, CoviFlowVariables.APPV_COMP);
						CoviFlowApprovalLineHelper.setPersonTask(pendingOu, personIndex, "datecompleted", sdf.format(new Date()));	
					}
					CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "COMMENT", Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
				}
			}
			
			if (personSize > 1 || isRedraftCharge.equalsIgnoreCase("y") || actionMode.equalsIgnoreCase("REDRAFT")) {
				destination = "SubInitiator";
			} else {
				destination = "SubApprovalResult";
			}
			
			// 의견유무체크시 최종결재선이 필요해서 상단으로 이동
			apvLineObj_org = CoviFlowApprovalLineHelper.setOuForSubProcess(apvLineObj_org, activeDivisionIndex, pendingStepIndex, pendingOu, execId);
			
			if( !(execution.hasVariable("g_request_approvalResult") && execution.getVariable("g_request_approvalResult") != null && execution.getVariable("g_request_approvalResult").toString().equalsIgnoreCase("cancelled"))
					&& !(execution.hasVariable("g_basic_approvalResult") && execution.getVariable("g_basic_approvalResult") != null && execution.getVariable("g_basic_approvalResult").toString().equalsIgnoreCase("cancelled")) ){
				//수신함의 상태를 528로 변경할 것
				//workitem update
				if(StringUtils.isNotBlank(taskid)){
					Map<String, Object> workitemUMap = new HashMap<String, Object>();
					workitemUMap.put("state", 528);
					workitemUMap.put("charge", charge);
					workitemUMap.put("taskID", taskid);
					processDAO.updateWorkItemUsingTaskId(workitemUMap);		
				}
				
				//담당자 유형일 경우 workitem 재할당
				//select WorkitemID, PerformerID .... where Name = 'Charge'
				//update workitem set usercode = 'admin', username = 'admin'
				//update performer set state = '2'
				JSONObject chargeInfo = new JSONObject();
				Map<String, Object> chargeRMap = new HashMap<String, Object>();
				chargeRMap.put("processID", execId);
				
				chargeInfo = processDAO.selectCharge(chargeRMap);
				
				if(chargeInfo != null && isRedraftCharge.equalsIgnoreCase("n")){
					Map<String, Object> chargeWorkitemUMap = new HashMap<String, Object>();
					chargeWorkitemUMap.put("userCode", "admin");
					chargeWorkitemUMap.put("userName", "admin");
					chargeWorkitemUMap.put("workItemID", chargeInfo.get("WorkItemID").toString());
					
					processDAO.updateWorkItemForCharge(chargeWorkitemUMap);
					
					Map<String, Object> chargePerformerUMap = new HashMap<String, Object>();
					chargePerformerUMap.put("state", "2");
					chargePerformerUMap.put("performerID", chargeInfo.get("PerformerID").toString());
					
					processDAO.updatePerformerForCharge(chargePerformerUMap);
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
				
				//processdescription update 
				//ctxJsonObj의 값을 수정				
				Map<String, Object> procDescUMap = new HashMap<String, Object>();
				Map<String, Object> subjectUMap = new HashMap<String, Object>();
				
				JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
				
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
				domainUMap.put("processID", execId);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj_org.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			execution.setVariable("g_appvLine", apvLineObj_org.toJSONString());
			execution.setVariable("g_sub_destination", destination);
			
			execution.setVariable("g_sub_isRedraftCharge", isRedraftCharge);
			
			//예고자 재생성
			if(isApvLineModified){
				CoviFlowWorkHelper.resetPreviewActivity("preview", execution, ctx, "T010");
			}
			
			//txManager.commit(status);
			
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("SubReceiptBoxResult", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
				

	}
}
