/**
 * @Class Name : BasicRouteReceiptResult.java
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

public class BasicRouteReceiptResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteReceiptResult.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("BasicRouteReceiptResult");

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
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String isSecure = pDesc.get("IsSecureDoc").toString();
			String usid = ctxJsonObj.get("usid").toString();
			String usdn = ctxJsonObj.get("usdn").toString();
			String formName = ctxJsonObj.get("FormName").toString();
			String formPrefix = ctxJsonObj.get("FormPrefix").toString();
			String isRedraftCharge = "n";
			//결재선 수정에 대한 처리
			JSONObject apvLineObj = new JSONObject();
			Object apvLine = execution.getVariable("g_appvLine");
			boolean isApvLineModified = false;
			if(apvLine instanceof LinkedHashMap){
				apvLineObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
			} else {
				apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			}
			if(execution.hasVariable("g_isChangeLine") && execution.getVariable("g_isChangeLine").toString().equalsIgnoreCase("true")){
				isApvLineModified = true;
			}
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//재기안자가 존재하면
			String taskid = "";
			String actionMode = "";
			String actionComment = "";
			List<JSONObject> actionComment_Attach = new ArrayList<JSONObject>();
			String signImage = "";	
			String isMobile = "";
			
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
					}	
				}
	        }
			
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			JSONObject pendingOu = (JSONObject)pendingStep.get("ou");
			JSONObject pendingPerson = (JSONObject)pendingOu.get("person");
			JSONObject pendingTaskinfo = (JSONObject)pendingPerson.get("taskinfo");
			boolean hasOldComment = false;
			
			JSONObject comment = new JSONObject();
			JSONObject paramChargeInfo = new JSONObject();
			if(StringUtils.isNotBlank(taskid)){
				actionComment = (String)execution.getVariable("g_actioncomment_" + taskid);
				actionComment_Attach = (ArrayList)execution.getVariable("g_actioncomment_attach_" + taskid);
				signImage = (String)execution.getVariable("g_signimage_" + taskid);
				paramChargeInfo = (JSONObject)JSONValue.parse(JSONValue.toJSONString(execution.getVariable("g_charge_" + taskid)));
				isMobile = (String)execution.getVariable("g_isMobile_" + taskid);
				
				if(pendingTaskinfo.containsKey("comment")){
					hasOldComment = true;
				}
				
				if(hasOldComment || StringUtils.isNotBlank(actionComment)){
					isComment = "true";
					comment.put("#text", actionComment);
				}
				//삭제
				execution.removeVariable("g_action_" + taskid);
				execution.removeVariable("g_actioncomment_" + taskid);
				execution.removeVariable("g_actioncomment_attach_" + taskid);
				execution.removeVariable("g_signimage_" + taskid);
				execution.removeVariable("g_charge_" + taskid);
				execution.removeVariable("g_isMobile_" + taskid);
			}
			
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			Integer activeDivisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			Integer stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			Integer pendingStepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			String deputyCode = pendingObject.get("deputyCode").toString();
			String approverCode = pendingObject.get("approverCode").toString();
			String approverName = pendingObject.get("approverName").toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, activeDivisionIndex, "processDescID"));
			
			//subject
			String formSubject = execution.getVariable("g_subject").toString();
			//알림 대상 - 결재단계의 모든 사람?
			ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getCompletedMessageReceivers(apvLineObj, execution.hasVariable("g_isDistribution"), Integer.toString(piid));
			
			//연동 처리
			String legacyInfo = "";
			if(execution.hasVariable("g_isLegacy")){
				legacyInfo = (String)execution.getVariable("g_isLegacy");	
			}
			
			boolean isSubstituted = false;
			//대결자가 지정되어 있고 결재선에 대결자가 있으면
			if(StringUtils.isNotBlank(deputyCode)){
				isSubstituted = true;
			}
			
			//전결 처리
			boolean isAuthorized = false;
			isAuthorized = CoviFlowApprovalLineHelper.isAuthorized(pendingStep);
			
			//setActivity
			// 접수, 승인 외에도 [재기안] 눌렀을 때 1인 결재하면 workitem 생성되어야 함
			if(stepSize <= 1 &&
					(actionMode.equalsIgnoreCase("REJECT") || actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REDRAFT"))){
				CoviFlowWorkHelper.setActivity("approve", execution, ctx, piid, taskid, 528, "T006");
			}
			
			if(actionMode.equalsIgnoreCase("REJECT")){
				//CoviFlowWorkHelper.processReject(piid);
				
				apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_REJECT);
				
				if(isSubstituted){
					//status
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "status", "rejected");
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "status", "rejected");
					
					//result
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "result", "rejected");
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "result", "bypassed");
					
					//datecompleted
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "datecompleted", sdf.format(new Date()));
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "datecompleted", sdf.format(new Date()));
					
					//comment
					if(hasOldComment || StringUtils.isNotBlank(actionComment)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "comment", comment);	
					}
					
					if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "comment_fileinfo", actionComment_Attach);
					}
					
					//mobile
					if(isMobile.equalsIgnoreCase("Y")){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "mobileType", "y");
					}
					
					//signimage
					if(StringUtils.isNotBlank(signImage)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "customattribute1", signImage);
					}
					
				} else {
					//pending step의 deputy값 초기화
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("taskID", taskid);
					processDAO.updateWorkItemForDeputy(params);
					
					if(!isAuthorized){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_REJECT);
					} else{//전결
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "status", "rejected");
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "result", "rejected");
					}
					
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "datecompleted", sdf.format(new Date()));
					
					//comment
					if(hasOldComment || StringUtils.isNotBlank(actionComment)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "comment", comment);
					}
					
					if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "comment_fileinfo", actionComment_Attach);
					}
					
					//mobile
					if(isMobile.equalsIgnoreCase("Y")){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "mobileType", "y");
					}
					
					//signimage
					if(StringUtils.isNotBlank(signImage)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "customattribute1", signImage);
					}
				}
				
				execution.setVariable("g_basic_approvalResult", "rejected");
				
				//알림 호출
				if(receivers.size() > 0){
					CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "REJECT", Integer.toString(piid), receivers, apvLineObj));
				}
				
			} else if(actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REDRAFT")){
				//추가적으로 분석이 필요
				//CoviFlowWorkHelper.processResult(apvLineObj, "");

				apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_COMP);
				if(isSubstituted){
					//status
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "status", "completed");
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "status", "completed");
					
					//result
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "result", "substituted");
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "result", "bypassed");
					
					//datecompleted
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "datecompleted", sdf.format(new Date()));
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "datecompleted", sdf.format(new Date()));
					
					//comment
					if(hasOldComment || StringUtils.isNotBlank(actionComment)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "comment", comment);
					}
					
					if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "comment_fileinfo", actionComment_Attach);
					}
					
					//mobile
					if(isMobile.equalsIgnoreCase("Y")){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "mobileType", "y");
					}
					
					//signimage
					if(StringUtils.isNotBlank(signImage)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "customattribute1", signImage);
					}
					
				} else {
					//pending step의 deputy값 초기화
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("taskID", taskid);
					processDAO.updateWorkItemForDeputy(params);
					
					if(!isAuthorized){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_COMP);
					} else{//전결
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "status", "completed");
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "result", "authorized");
					}
					
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "datecompleted", sdf.format(new Date()));
					
					//comment
					if(hasOldComment || StringUtils.isNotBlank(actionComment)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "comment", comment);
					}
					
					if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "comment_fileinfo", actionComment_Attach);
					}
					
					//mobile
					if(isMobile.equalsIgnoreCase("Y")){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "mobileType", "y");
					}
					
					//signimage
					if(StringUtils.isNotBlank(signImage)){
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "customattribute1", signImage);
					}
				}
			} /*
			else if(actionMode.equalsIgnoreCase("APPROVECANCEL")){// Division이 달라지는 경우 승인 취소 구현 불가, 모델링을 다시 그려야 함
				//승인취소
				CoviFlowWorkHelper.processApproveCancel(apvLineObj.toJSONString(), activeDivisionIndex, pendingStepIndex);
				
				//결재선 수정
				//1. 현재 결재 단계는 inactive로
				//2. 이전 결재 단계는 pending으로
				//3. 불필요한 데이터는(taskid, wiid, widescid, pfid, datecompleted, datereceived, comment)  remove
				//
				apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, CoviFlowVariables.APPV_PENDING);
				apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "", CoviFlowVariables.APPV_PENDING);
				
				apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_INACTIVE);
				apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_INACTIVE);
				
				apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "ALL");
				apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex, "ALL");
				
				//알림 호출
				receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, "APPROVECANCEL");
				if(receivers.size() > 0){
					CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(formName, usid, formSubject, "APPROVECANCEL", Integer.toString(piid), receivers));
				}
				
			} */else if((actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW"))){
				//회수, 기안취소
				CoviFlowWorkHelper.processCancel(fiid, Integer.parseInt(pendingOu.get("wiid").toString()), ctx, apvLineObj.toJSONString());
				
				//알림 호출
				receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, actionMode);
				if(receivers.size() > 0){
					CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, actionMode, Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
				}
				
				//프로세스 종료
				execution.setVariable("g_basic_approvalResult", "cancelled");
				
				if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "WITHDRAW"))
				{
					CoviFlowWorkHelper.callLegacy(execution, "WITHDRAW", usid, taskid, CoviFlowWorkHelper.base64Decode(actionComment));
				}				
			} else if(actionMode.equalsIgnoreCase("CHARGE")){
				if(paramChargeInfo != null){
					CoviFlowWorkHelper.setActivity("Charge", execution, ctx, piid, taskid, 288, "T008");
					isRedraftCharge = "y";
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
			
			//결재선 update
			if(execution.hasVariable("g_isDistribution")){
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
			
			// 재기안을 하는 경우
			// [19-02-27] kimhs, 무조건 initiator 가 생기도록 분기처리 수정함.
			// 기존 소스로 원복함, 조건 stepSize > 0 => stepSize > 1
			if (stepSize > 1 || isRedraftCharge.equalsIgnoreCase("y")) {
				destination = "BasicInitiator";
			} else {
				destination = "BasicRouteApprovalResult";
			}

				try {
				if(actionMode.equalsIgnoreCase("APPROVAL") || actionMode.equalsIgnoreCase("REDRAFT")) {
					//call private domain data insert
					JSONArray params = new JSONArray();
					JSONObject param = new JSONObject();
					param.put("mode", "REDRAFT");
					param.put("approvalLine", apvLineObj.toJSONString());
					param.put("actionMode", "REDRAFT");
					param.put("FormSubject", formSubject);
					param.put("FormName", formName);
					param.put("usid", approverCode);
					param.put("usdn", approverName);
					param.put("FormPrefix", formPrefix);
					
					params.add(param);
					CoviFlowWSHelper.invokeApi("APVLINE", params);
				}
				} catch (Exception e) {
					
				}
			
			//기안취소, 회수가 아닌 경우
			if(execution.getVariable("g_basic_approvalResult") == null || !execution.getVariable("g_basic_approvalResult").toString().equalsIgnoreCase("cancelled"))
			{
				//business state 변경
				Map<String, Object> procUMap = new HashMap<String, Object>();
				procUMap.put("businessState", "01_01_02");
				procUMap.put("processState", 288);
				procUMap.put("processID", piid);
				
				processDAO.updateProcessBusinessState(procUMap);
				
				//수신함의 상태를 528로 변경할 것
				//workitem update
				if(StringUtils.isNotBlank(taskid)){
					Map<String, Object> workitemUMap = new HashMap<String, Object>();
					workitemUMap.put("state", 528);
					workitemUMap.put("charge", approverName);
					workitemUMap.put("taskID", taskid);
					processDAO.updateWorkItemUsingTaskId(workitemUMap);
					
					// 기밀문서는 archive로 이관 시, 부서함에 쌓이지 않도록 예외처리 되어 있음
					// 담당자 개인완료함에 기밀문서 표시되지 않아서 주석처리함.
					/*
					 * if(isSecure.equalsIgnoreCase("Y")){ Map<String, Object> workitemDMap = new
					 * HashMap<String, Object>(); workitemDMap.put("taskID", taskid);
					 * processDAO.deleteWorkItemUsingTaskId(workitemDMap); }
					 */
				}
				
				//담당자 유형일 경우 workitem 재할당
				//select WorkitemID, PerformerID .... where Name = 'Charge'
				//update workitem set usercode = 'admin', username = 'admin'
				//update performer set state = '2'
				JSONObject chargeInfo = new JSONObject();
				Map<String, Object> chargeRMap = new HashMap<String, Object>();
				chargeRMap.put("processID", piid);
				
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
					// forminstID 기준으로 update할 시 다른 배포처에도 영향 받아서 예외처리 함
					if(execution.hasVariable("g_isDistribution")){
						procDescUMap.put("processDescriptionID", pidescid);
					}					
					
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
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			execution.setVariable("g_basic_destination", destination);
			
			execution.setVariable("g_basic_isRedraftCharge", isRedraftCharge);
			
			//txManager.commit(status);
			
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteReceiptResult", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
				

	}
}
