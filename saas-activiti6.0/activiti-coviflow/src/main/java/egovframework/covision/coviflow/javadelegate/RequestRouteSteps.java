/**
 * @Class Name : RequestRouteSteps.java
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
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

public class RequestRouteSteps implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteSteps.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteSteps");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			//내부 결재 분기처리 
			String destination = "";
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//결재선 변경
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject pendingObj = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObj.get("step");
			
			//jsonarray, jsonobject 분기
			String unitType = (String)pendingObj.get("unitType");
			String routeType = (String)pendingObj.get("routeType");
			String allotType = (String)pendingObj.get("allotType");
			
			if(unitType.equalsIgnoreCase("role")){
				JSONObject ou = (JSONObject)pendingStep.get("ou");
				JSONObject role = (JSONObject)ou.get("role");
				String code = (String)role.get("code");
				
				//부서장결재
				if(code.equalsIgnoreCase("UNIT_MANAGER")){
					
					String ouCode = (String)role.get("oucode");
					String ouName = (String)role.get("ouname");
					
					Map<String, Object> paramManager = new HashMap<String, Object>();
					paramManager.put("grCode", ouCode);
					String managerCode = processDAO.selectManager(paramManager);
					
					if(StringUtils.isNotBlank(managerCode)){
						
						JSONObject userInfo = new JSONObject();
						Map<String, Object> paramUserInfo = new HashMap<String, Object>();
						paramUserInfo.put("urCode", managerCode);
						
						userInfo = processDAO.selectUserInfo(paramUserInfo);
						
						if(userInfo != null){
							//unittype이 role인 경우 해당 person으로 대체
							//하드코딩을 제거할 것
							JSONObject taskInfo = new JSONObject();
							taskInfo.put("result", "pending");
							taskInfo.put("status", "pending");
							taskInfo.put("kind", "normal");
							
							JSONObject person = new JSONObject();
							person.put("position", (String)userInfo.get("JobPosition"));
							person.put("title", (String)userInfo.get("JobTitle"));
							person.put("level", (String)userInfo.get("JobLevel"));
							person.put("taskinfo", taskInfo);
							person.put("sipaddress", (String)userInfo.get("MailAddress"));
							person.put("name", (String)userInfo.get("Name"));
							person.put("code", (String)userInfo.get("Code"));
							person.put("ouname", ouName);
							person.put("oucode", ouCode);
							
							pendingStep.put("unittype", "person");
							ou.remove("role");
							ou.put("person", person);	
						}
						
					} else {
						throw new Exception("조직[" + ouCode + "] 정보에 조직장[MANAGER_PERSON_CODE] 정보가 없습니다.");
					}
				}
			}
			
			String kind = CoviFlowApprovalLineHelper.getOuTask(pendingStep, "kind");
			int divisionIndex = Integer.parseInt(pendingObj.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObj.get("stepIndex").toString());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			//process business state update
			String bizState = "";
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("processState", 288);
			procUMap.put("processID", Integer.parseInt(execution.getProcessInstanceId()));
			
			if(kind.equalsIgnoreCase("bypass")){
				destination = "RequestBypassedEx"; //후열자 추가
			} else {
				
				switch(routeType){
					case "approve" :
						if(allotType.equalsIgnoreCase("parallel")){
							List<String> assignees = CoviFlowApprovalLineHelper.getAssignees(pendingStep);
							execution.setVariable("g_request_assignees", assignees);
							
							destination = "RequestApprovers"; //동시결재
						} else if(allotType.equalsIgnoreCase("queue")){
							destination = "RequestQApprover"; //대기열결재
						} else{
							
							if(kind.equalsIgnoreCase("confirm")){
								destination = "RequestConfirmor"; //확인결재자
							} else if(kind.equalsIgnoreCase("reference")){
								destination = "RequestSharer"; //참조결재자
							} else {
								destination = "RequestApprover"; //결재자
							}
						}
						break;
						
					case "receive" :
						destination = "RequestApprover"; //결재자
						break;
						
					case "assist" :
						if(unitType.equalsIgnoreCase("ou")){
							bizState = "03_01_03";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getOuAssignees(pendingStep);
								
								execution.setVariable("g_request_assignees", assignees);
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex, "datereceived", sdf.format(new Date()));
								
								destination = "RequestParOus"; //병렬부서협조
								
							} else {
								destination = "RequestSeqOus"; //순차부서협조
							}
						} else {
							bizState = "01_04_02";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getAssignees(pendingStep);
								execution.setVariable("g_request_assignees", assignees);
								
								destination = "RequestCoordinators2"; //병렬협조자
							} else {
								destination = "RequestCoordinators"; //협조자
							}
						}
						
						procUMap.put("businessState", bizState);
						processDAO.updateProcessBusinessState(procUMap);
						
						break;
						
					case "consult" :
						if(unitType.equalsIgnoreCase("ou")){
							bizState = "01_02_02";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getOuAssignees(pendingStep);
								execution.setVariable("g_request_assignees", assignees);
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex, "datereceived", sdf.format(new Date()));
								
								destination = "RequestParOus"; //병렬부서합의
								
							} else {
								destination = "RequestSeqOus"; //순차부서합의
							}
							
						} else {
							bizState = "01_02_01";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getAssignees(pendingStep);
								execution.setVariable("g_request_assignees", assignees);
								
								destination = "RequestConsultors"; //병렬합의자
								
							} else {
								destination = "RequestConsultors2"; //순차합의자
							}
						}
						
						procUMap.put("businessState", bizState);
						processDAO.updateProcessBusinessState(procUMap);
						
						break;
				}
			}
			
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteSteps", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}
}
