/**
 * @Class Name : RequestProcessResult.java
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

public class RequestProcessResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestProcessResult.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestProcessResult");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String ctx = CoviFlowVariables.getGlobalContextStr(execution);
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			String usid = ctxJsonObj.get("usid").toString();
			String formName = ctxJsonObj.get("FormName").toString();
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
			
			//연동 처리
			Boolean isLegacy = false;
			String legacyInfo = "";
			if(execution.hasVariable("g_isLegacy")){
				legacyInfo = (String)execution.getVariable("g_isLegacy");	
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
			Integer stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			Integer pendingStepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			String isStep = pendingObject.get("isStep").toString();
			String allotType = pendingObject.get("allotType").toString();
			String routeType = pendingObject.get("routeType").toString();
			String deputyCode = pendingObject.get("deputyCode").toString();
			String approverCode = pendingObject.get("approverCode").toString();
			
			//processdescript 업데이트를 위한 변수
			String isModified = "";
			String isFile = "";
			String isComment = "";
			String isSubjectModified = "";
			
			String isApproveCancelled = "";
			if(execution.getProcessDefinitionId().toString().contains("request")){
				isApproveCancelled = execution.getVariable("g_request_approvalResult") == null ? "" : execution.getVariable("g_request_approvalResult").toString();	
			} else {
				isApproveCancelled = execution.getVariable("g_basic_approvalResult") == null ? "" : execution.getVariable("g_request_approvalResult").toString();
			}
			
			if(isStep.equalsIgnoreCase("true") 
					&& !isApproveCancelled.equalsIgnoreCase("approvecancelled")){
				//알림 메시지를 위한 parameter
				//subject
				String formSubject = execution.getVariable("g_subject").toString();
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
					JSONObject pendingOu = (JSONObject)ous.get(i);
										
					if(pendingOu.containsKey("taskid") && (pendingStep.containsKey("unittype") && !pendingStep.get("unittype").toString().equalsIgnoreCase("ou")) ){
						
						Object pendingP = pendingOu.get("person");
						JSONArray persons = new JSONArray();
						if(pendingP instanceof JSONObject){
							JSONObject personJsonObj = (JSONObject)pendingP;
							persons.add(personJsonObj);
						} else {
							persons = (JSONArray)pendingP;
						}
						
						JSONObject personTaskInfo = (JSONObject) ((JSONObject)persons.get(0)).get("taskinfo");
						
						String taskid = (String)pendingOu.get("taskid");
						String actionMode = "";
						String actionComment = "";
						List<JSONObject> actionComment_Attach = new ArrayList<JSONObject>();
						String signImage = "";
						String isMobile = "";
						String isBatch = "";
						JSONObject comment = new JSONObject();
						boolean hasOldComment = false;
						
						if(execution.hasVariable("g_action_" + taskid))
						{
							actionMode = (String)execution.getVariable("g_action_" + taskid);
							actionComment = (String)execution.getVariable("g_actioncomment_" + taskid);
							actionComment_Attach = (ArrayList)execution.getVariable("g_actioncomment_attach_" + taskid);
							signImage = (String)execution.getVariable("g_signimage_" + taskid);
							isMobile = (String)execution.getVariable("g_isMobile_" + taskid);
							isBatch = (String)execution.getVariable("g_isBatch_" + taskid);
							
							if(personTaskInfo.containsKey("comment")){
								hasOldComment = true;
							}
							
							if(hasOldComment || StringUtils.isNotBlank(actionComment)){
								if(StringUtils.isNotBlank(actionComment))	isComment = "true";
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
						
						/* 참고
						 * 전결 시 comment 구조 
						 * "comment": {
									"_relatedresult": "authorized",
									"_datecommented": "2017-01-17 09:06:10",
									"__cdata": "전결"
								}
						 * */
						
						//대결자가 개입하면
						//결재선 수정이 일어 남, 수정된 결재선을 변경
						boolean isSubstituted = false;
						//대결자가 지정되어 있고 결재선에 대결자가 있으면
						if(StringUtils.isNotBlank(deputyCode)){
							isSubstituted = true;
						}
						
						//전결 처리
						boolean isAuthorized = false;
						isAuthorized = CoviFlowApprovalLineHelper.isAuthorized(pendingStep);
						
						if(actionMode.equalsIgnoreCase("REJECTTO")){//지정반려
							CoviFlowWorkHelper.processRejectTo(CoviFlowApprovalLineHelper.getRejecteeStep(apvLineObj, activeDivisionIndex));
							
							apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_REJECT);
							if(isSubstituted){//대결
								//status
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "status", "rejectedto");
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "status", "rejectedto");
								
								//result
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "result", "rejectedto");
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
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "status", "rejectedto");
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "result", "rejectedto");
								} else{//전결
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "status", "rejectedto");
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
								
								//signimage
								if(StringUtils.isNotBlank(signImage)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "customattribute1", signImage);
								}
							}
							
							//결재선 변경
							if(!pendingStepIndex.equals(stepSize - 1)&&!isAuthorized){
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, CoviFlowVariables.APPV_PENDING);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
							}
							
						} else if(actionMode.equalsIgnoreCase("REJECT")){
							
							CoviFlowWorkHelper.processReject(piid, isMobile);
							
							apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_REJECT);
							if(isSubstituted){//대결
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
								
								//signimage
								if(StringUtils.isNotBlank(signImage)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "customattribute1", signImage);
								}
							}
							execution.setVariable("g_request_approvalResult", "rejected");
														
							//알림 호출
							if(receivers.size() > 0){
								CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "REJECT", Integer.toString(piid), receivers, apvLineObj));
							}
							
							isLegacy = false;
						} else if(actionMode.equalsIgnoreCase("APPROVAL")){
							//추가적으로 분석이 필요
							CoviFlowWorkHelper.processResult(apvLineObj, "", isMobile, isBatch);

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
							//결재선 변경
							if(!pendingStepIndex.equals(stepSize - 1)&&!isAuthorized){
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, CoviFlowVariables.APPV_PENDING);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
							}
						} else if(actionMode.equalsIgnoreCase("APPROVECANCEL")
								&&!allotType.equalsIgnoreCase("parallel")){//병렬 합의, 협조의 경우 아래 처리 제외
							//승인취소
							CoviFlowWorkHelper.processApproveCancel(apvLineObj.toJSONString(), activeDivisionIndex, pendingStepIndex);
							
							/* 결재선 수정
							 * 1. 현재 결재 단계는 inactive로
							 * 2. 이전 결재 단계는 pending으로
							 * 3. 불필요한 데이터는(taskid, wiid, widescid, pfid, datecompleted, datereceived, comment)  remove
							 * */
							String sKind = CoviFlowApprovalLineHelper.getOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "kind");
							
							if(sKind.equalsIgnoreCase("substitute")){
								apvLineObj = CoviFlowApprovalLineHelper.removePersonTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, 0); // 대결자 삭제
								apvLineObj = CoviFlowApprovalLineHelper.setPersonTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, 0, "kind", "normal"); // bypass => normal 로 변경
							}
							
							apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, CoviFlowVariables.APPV_PENDING);
							apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "", CoviFlowVariables.APPV_PENDING);
							
							apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_INACTIVE);
							apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_INACTIVE);
							
							apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex - 1, "ALL");
							apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, pendingStepIndex, "NoID");
							
							// 중간에 후열자 있는 경우, 이전 결재자 승인취소 가능하도록 수정.
							if(sKind.equalsIgnoreCase("bypass") || sKind.equalsIgnoreCase("review") || sKind.equalsIgnoreCase("reference")) {
								int resetCnt = 0;
								boolean referenceStatus = true;
								JSONObject ObjapvLine = new JSONObject();
								if(apvLine instanceof LinkedHashMap){
									ObjapvLine = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
								} else {
									ObjapvLine = (JSONObject)parser.parse(apvLine.toString());
								}
								
								JSONObject root = (JSONObject)ObjapvLine.get("steps");
								Object divisionObj = root.get("division");
								JSONArray divisions = new JSONArray();
								if(divisionObj instanceof JSONObject){
									JSONObject divisionJsonObj = (JSONObject)divisionObj;
									divisions.add(divisionJsonObj);
								} else {
									divisions = (JSONArray)divisionObj;
								}
								JSONObject division = (JSONObject)divisions.get(activeDivisionIndex);
								Object stepObj = division.get("step");
								JSONArray stepArray = new JSONArray();
								if(stepObj instanceof JSONObject){
									JSONObject stepJsonObj = (JSONObject)stepObj;
									stepArray.add(stepJsonObj);
								} else {
									stepArray = (JSONArray)stepObj;
								}
								
								for(int nowStep = stepArray.size()-1; nowStep >= 0; nowStep--){
									JSONObject step = (JSONObject)stepArray.get(nowStep);
									Object ouObj = step.get("ou");
									JSONArray ouArray = new JSONArray();
									if(ouObj instanceof JSONArray){
										ouArray = (JSONArray)ouObj;
									} else {
										ouArray.add((JSONObject)ouObj);
									}
									
									for(int z = 0; z < ouArray.size(); z++){
										JSONObject ou = (JSONObject)ouArray.get(z);
										Object personObj = ou.get("person");
										JSONArray personArray = new JSONArray();
										if(personObj instanceof JSONObject){
											JSONObject jsonObj = (JSONObject)personObj;
											personArray.add(jsonObj);
										} else {
											personArray = (JSONArray)personObj;
										}
										
										JSONObject personObject = (JSONObject)personArray.get(0);
										JSONObject taskObject = new JSONObject();
										taskObject = (JSONObject)personObject.get("taskinfo");

										boolean isReview = false;
										if(referenceStatus && taskObject.get("kind").toString().equalsIgnoreCase("review")) {
											for(int chkStep = nowStep - 1; chkStep >= 0; chkStep--) {
												sKind = CoviFlowApprovalLineHelper.getOuTask(apvLineObj, activeDivisionIndex, chkStep, "kind");
												String sStatus = CoviFlowApprovalLineHelper.getOuTask(apvLineObj, activeDivisionIndex, chkStep, "status");
												
												if(sKind.equalsIgnoreCase("review")) {
													continue;
												} else if(sStatus.equalsIgnoreCase("completed")) {
													isReview = true;
													break;
												} else {
													isReview = false;
													break;
												}
											}
										}
										
										if ((isReview == false && taskObject.get("status").toString().equalsIgnoreCase("inactive")) || taskObject.get("status").toString().equalsIgnoreCase("pending")) {
											continue;
										}else if (referenceStatus && (taskObject.get("kind").toString().equalsIgnoreCase("bypass") || taskObject.get("kind").toString().equalsIgnoreCase("review") || taskObject.get("kind").toString().equalsIgnoreCase("reference"))) {
								//승인취소
											CoviFlowWorkHelper.processApproveCancel(apvLineObj.toJSONString(), activeDivisionIndex, nowStep);
								
											apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, nowStep - 1, CoviFlowVariables.APPV_PENDING);
											apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, nowStep - 1, "", CoviFlowVariables.APPV_PENDING);
								
											apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, nowStep, CoviFlowVariables.APPV_INACTIVE);
											apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, nowStep, "", CoviFlowVariables.APPV_INACTIVE);
								
											apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, nowStep - 1, "ALL");
											apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, activeDivisionIndex, nowStep, "NoID");
										}else if (taskObject.get("status").toString().equalsIgnoreCase("completed")){
											referenceStatus = false;
										}
									}
								}
							}
							
							//변수 초기화
							execution.setVariable("g_request_approvalResult", "");
							
							//알림 호출
							receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, "APPROVECANCEL");
							if(receivers.size() > 0){
								JSONObject approveCancelledPendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
								String approveCancelledApproverCode = approveCancelledPendingObject.get("approverCode").toString();
								CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approveCancelledApproverCode, formSubject, "APPROVECANCEL", Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
							}
							
						} else if((actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW"))
								&&!allotType.equalsIgnoreCase("parallel")){//병렬 합의, 협조의 경우 아래 처리 제외
							//회수, 기안취소
							CoviFlowWorkHelper.processCancel(fiid, Integer.parseInt(pendingOu.get("wiid").toString()), ctx, apvLineObj.toJSONString());
							
							//알림 호출
							receivers = CoviFlowApprovalLineHelper.getCancelledMessageReceivers(apvLineObj, activeDivisionIndex, pendingStepIndex, actionMode);
							if(receivers.size() > 0){
								CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, actionMode, Integer.toString(piid), receivers, apvLineObj, CoviFlowWorkHelper.base64Decode(actionComment)));
							}
							
							//프로세스 종료
							execution.setVariable("g_request_approvalResult", "cancelled");
							
							isLegacy = false;
							if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "WITHDRAW"))
							{
								CoviFlowWorkHelper.callLegacy(execution, "WITHDRAW", usid, taskid, CoviFlowWorkHelper.base64Decode(actionComment));
							}
						} else if((actionMode.equalsIgnoreCase("AGREE")||actionMode.equalsIgnoreCase("DISAGREE"))&& allotType.equalsIgnoreCase("serial")){//순차합의인 경우
							
							apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_COMP);
							if(isSubstituted){
								//추가적으로 분석이 필요
								
								if (actionMode.equalsIgnoreCase("DISAGREE") && routeType.equalsIgnoreCase("assist")) { // 개인협조 반려									
									CoviFlowWorkHelper.processReject(piid, isMobile);
								
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "status", "rejected");								
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "result", "rejected");
									
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "status", "rejected");	
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "result", "bypassed");
									
									execution.setVariable("g_basic_approvalResult", "rejected");
									
								} else {
									CoviFlowWorkHelper.processResult(apvLineObj, "", isMobile, isBatch);
	
									if(actionMode.equalsIgnoreCase("DISAGREE")) { // 개인합의 거부
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "status", "rejected");							
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "result", "disagreed");
										
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "status", "rejected");			
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "result", "bypassed");
									} else { // 개인합의 동의 / 개인협조 승인
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "status", "completed");								
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 0, "", "result", (routeType.equalsIgnoreCase("assist") ? "completed" : "agreed"));
										
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "status", "completed");		
										apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, 1, "", "result", "bypassed");
									}
									
								}
								
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
								if(actionMode.equalsIgnoreCase("AGREE")&&routeType.equalsIgnoreCase("assist")){
									CoviFlowWorkHelper.processResult(apvLineObj, "", isMobile, isBatch);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_COMP);
								} else if (actionMode.equalsIgnoreCase("DISAGREE")&&routeType.equalsIgnoreCase("assist")) {
									CoviFlowWorkHelper.processReject(piid, isMobile);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_REJECT);
									execution.setVariable("g_request_approvalResult", "rejected");
								} else if(actionMode.equalsIgnoreCase("AGREE")&&routeType.equalsIgnoreCase("consult")){
									CoviFlowWorkHelper.processResult(apvLineObj, "", isMobile, isBatch);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_AGREE);
								} else if(actionMode.equalsIgnoreCase("DISAGREE")&&routeType.equalsIgnoreCase("consult")){
									CoviFlowWorkHelper.processResult(apvLineObj, "", isMobile);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_DISAGREE);
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
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "mobileType", "y");
								}
								
								//signimage
								if(StringUtils.isNotBlank(signImage)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "customattribute1", signImage);
								}
							}
							
							if(actionMode.equalsIgnoreCase("DISAGREE")&&routeType.equalsIgnoreCase("assist")){
								//알림 호출
								if(receivers.size() > 0){
									CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "REJECT", Integer.toString(piid), receivers, apvLineObj));
								}
							}
							
							//결재선 변경
							if(!pendingStepIndex.equals(stepSize - 1)){
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, CoviFlowVariables.APPV_PENDING);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
								
								//다음 step이 serial이 아니면
								String nextAllottype = CoviFlowApprovalLineHelper.getStepAttr(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "allottype");
								if(!nextAllottype.equalsIgnoreCase("serial")){
									//process business state update
									//01-02-01 -> 01_01_01
									Map<String, Object> procUMap = new HashMap<String, Object>();
									procUMap.put("businessState", "01_01_01");
									procUMap.put("processState", 288);
									procUMap.put("processID", piid);
									
									processDAO.updateProcessBusinessState(procUMap);	
								}
							}
							
						} else if(allotType.equalsIgnoreCase("parallel")){ //병렬합의 및 병렬협조인 경우
							
							String appvRet = (String)execution.getVariable("g_request_approvalResult");
							if(appvRet != null && (appvRet.equalsIgnoreCase("agreed")
									||appvRet.equalsIgnoreCase("disagreed")
									||appvRet.equalsIgnoreCase("rejected"))){//기안취소, 회수일 경우 아래 skip
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_COMP);
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "datecompleted", sdf.format(new Date()));
								
								// 병렬합의일때만
								if((appvRet.equalsIgnoreCase("agreed") || appvRet.equalsIgnoreCase("disagreed")) && !pendingStepIndex.equals(stepSize - 1)){
									apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, CoviFlowVariables.APPV_PENDING);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
								}
								
								//process business state update
								//01-02-01 -> 01_01_01
								Map<String, Object> procUMap = new HashMap<String, Object>();
								procUMap.put("businessState", "01_01_01");
								procUMap.put("processState", 288);
								procUMap.put("processID", piid);
								
								processDAO.updateProcessBusinessState(procUMap);
								
								//반려 알림 안 가는 문제로 주석처리(22-11-30)
								//break; //중복 실행 방지
							}
							else {
								isLegacy = false;
							}
						}
						
						if(isLegacy){
							String personid = "";
							if(pendingOu.get("person") instanceof JSONObject){
								personid = (String)((JSONObject)pendingOu.get("person")).get("code");
							}else if(pendingOu.get("person") instanceof JSONArray){
								personid = (String)((JSONObject)((JSONArray)pendingOu.get("person")).get(0)).get("code");
							}
							
							CoviFlowWorkHelper.callLegacy(execution, "OTHERSYSTEM", personid, taskid, CoviFlowWorkHelper.base64Decode(actionComment));
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
						
					}  else if(pendingStep.containsKey("unittype") && pendingStep.get("unittype").toString().equalsIgnoreCase("ou")){ // 부서내 합의, 협조, 감사
						JSONObject taskInfo = (JSONObject)pendingOu.get("taskinfo");
						String execId = taskInfo.get("execid").toString();
						String approvalResult = execution.getVariable("g_sub_approvalResult_" + execId).toString();
						
						approverCode = CoviFlowApprovalLineHelper.getRejectedPerson(pendingStep).get("approverCode").toString();
						
						if(execution.hasVariable("g_request_approvalResult") && execution.getVariable("g_request_approvalResult") != null && execution.getVariable("g_request_approvalResult").toString().equalsIgnoreCase("cancelled")){
							//회수, 기안취소
							CoviFlowWorkHelper.processCancel(fiid, Integer.parseInt(pendingOu.get("wiid").toString()), ctx, apvLineObj.toJSONString());
							
							//프로세스 종료
							execution.setVariable("g_request_approvalResult", "cancelled");
						}else{
							if(allotType.equalsIgnoreCase("parallel")){
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_COMP);
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "datecompleted", sdf.format(new Date()));
								
								if(!pendingStepIndex.equals(stepSize - 1) && !(approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("assist"))){
									apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, CoviFlowVariables.APPV_PENDING);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
								}
								
								if(approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("assist")){
									//알림 호출
									if(receivers.size() > 0){
										CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "REJECT", Integer.toString(piid), receivers, apvLineObj));
									}
								}
								
								//process business state update
								//01-02-01 -> 01_01_01
								Map<String, Object> procUMap = new HashMap<String, Object>();
								procUMap.put("businessState", "01_01_01");
								procUMap.put("processState", 288);
								procUMap.put("processID", piid);
								
								processDAO.updateProcessBusinessState(procUMap);
						
								break; //중복 실행 방지
															
							} else { //serial(순차)
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex, CoviFlowVariables.APPV_COMP);
								if(approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("consult")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_DISAGREE);
								} else if(!approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("consult")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_AGREE);
								} else if(approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("assist")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_REJECT);
									execution.setVariable("g_request_approvalResult", "rejected");
								} else if(!approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("assist")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", CoviFlowVariables.APPV_COMP);
								} 
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex, "", "datecompleted", sdf.format(new Date()));
								
								//결재선 변경
								if(!pendingStepIndex.equals(stepSize - 1) && !(approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("assist"))){
									apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, CoviFlowVariables.APPV_PENDING);
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
									
									//다음 step이 serial이 아니면
									String nextAllottype = CoviFlowApprovalLineHelper.getStepAttr(apvLineObj, activeDivisionIndex, pendingStepIndex + 1, "allottype");
									if(!nextAllottype.equalsIgnoreCase("serial")){
										//process business state update
										//01-02-01 -> 01_01_01
										Map<String, Object> procUMap = new HashMap<String, Object>();
										procUMap.put("businessState", "01_01_01");
										procUMap.put("processState", 288);
										procUMap.put("processID", piid);
										
										processDAO.updateProcessBusinessState(procUMap);	
									}
								}
									
								if(approvalResult.equalsIgnoreCase("rejected")&&routeType.equalsIgnoreCase("assist")){
									//알림 호출
									if(receivers.size() > 0){
										CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, approverCode, formSubject, "REJECT", Integer.toString(piid), receivers, apvLineObj));
									}
								}
								
							}
						}
					}
					
				}//for문 끝
				
				//action 삭제
				CoviFlowWorkHelper.removeActionAll(execution);
				
				/*
				 * isModify - g_isModified - true/false
				 * isComment - IsNotblank(comment)
				 * isFile - g_isFile - true / false
				 * isSubjectModified - g_isSubjectModified - true / false
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
				
				execution.setVariable("g_appvLine", apvLineObj.toJSONString());
				
				//예고자 재생성
				if(isApvLineModified){
					CoviFlowWorkHelper.resetPreviewActivity("preview", execution, ctx, "T010");
				}
			} else if(isApproveCancelled.equalsIgnoreCase("approvecancelled")) {
				execution.setVariable("g_request_approvalResult", "");
			}
			
			//txManager.commit(status);
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestProcessResult", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
		
	}
}
