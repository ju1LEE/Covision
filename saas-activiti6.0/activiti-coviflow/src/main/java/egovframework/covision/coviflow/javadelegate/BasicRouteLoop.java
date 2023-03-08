/**
 * @Class Name : BasicRouteLoop.java
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

public class BasicRouteLoop implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteLoop.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteLoop");
		
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
			//procUMap.put("businessState", bizState);
			procUMap.put("processState", 288);
			procUMap.put("processID", Integer.parseInt(execution.getProcessInstanceId()));
			
			if(kind.equalsIgnoreCase("bypass")){
				destination = "BasicBypassedExt"; //후열자 추가
			} else {
				
				switch(routeType){
					case "approve" :

						//bizState추가
						if(divisionIndex > 0) {
							bizState = "01_01_02";
						} else {
							bizState = "01_01_01";
						}

						if(allotType.equalsIgnoreCase("parallel")){
							List<String> assignees = CoviFlowApprovalLineHelper.getAssignees(pendingStep);
							execution.setVariable("g_basic_assignees", assignees);
							
							destination = "BasicApprovers"; //동시결재
						} else if(allotType.equalsIgnoreCase("queue")){
							destination = "BasicQApprover"; //대기열결재
						} else{
							
							if(kind.equalsIgnoreCase("confirm")){
								destination = "BasicConfirmor"; //확인결재자
							} else if(kind.equalsIgnoreCase("reference")){
								destination = "BasicSharer"; //참조결재자
							} else {
								destination = "BasicApprover"; //결재자
							}
						}
						
						procUMap.put("businessState", bizState);
						processDAO.updateProcessBusinessState(procUMap);						

						break;
						
					case "receive" :
						destination = "BasicApprover"; //결재자
						break;
						
					case "assist" :
						
						if(unitType.equalsIgnoreCase("ou")){
							bizState = "03_01_03";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getOuAssignees(pendingStep);
								//delegateTask.addCandidateUsers(assignees);
								execution.setVariable("g_basic_assignees", assignees);
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex, "datereceived", sdf.format(new Date()));
								
								destination = "BasicParOus"; //병렬부서협조
								
							} else {
								destination = "BasicSeqOus"; //순차부서협조
							}
							
						} else {
							bizState = "01_04_02";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getAssignees(pendingStep);
								//delegateTask.addCandidateUsers(assignees);
								execution.setVariable("g_basic_assignees", assignees);
								
								destination = "BasicCoordinators2"; //병렬협조자
								
							} else {
								destination = "BasicCoordinators"; //순차협조자
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
								//delegateTask.addCandidateUsers(assignees);
								execution.setVariable("g_basic_assignees", assignees);
								
								apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex, "datereceived", sdf.format(new Date()));
								
								destination = "BasicParOus"; //병렬부서합의
								
							} else {
								destination = "BasicSeqOus"; //순차부서합의
							}
							
						} else {
							bizState = "01_02_01";
							if(allotType.equalsIgnoreCase("parallel")){
								//assignees setting
								List<String> assignees = CoviFlowApprovalLineHelper.getAssignees(pendingStep);
								//delegateTask.addCandidateUsers(assignees);
								execution.setVariable("g_basic_assignees", assignees);
								
								destination = "BasicConsultors"; //병렬합의자
								
							} else {
								destination = "BasicConsultors2"; //순차합의자
							}	
						}
						
						procUMap.put("businessState", bizState);
						processDAO.updateProcessBusinessState(procUMap);
						
						break;
						
					case "audit" :
						
						if(unitType.equalsIgnoreCase("ou")){
							bizState = "01_03_02";
							destination = "BasicSeqOus"; //부서감사
						} else {
							bizState = "01_03_01";
							destination = "BasicAuditors"; //개인감사
						}
						
						procUMap.put("businessState", bizState);
						processDAO.updateProcessBusinessState(procUMap);
						
						break;
						
				}
			}
			
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			execution.setVariable("g_basic_destination", destination);
			
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteLoop", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		

	}
}
