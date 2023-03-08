/**
 * @Class Name : BasicInitiatorHandler.java
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class BasicInitiatorHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicInitiatorHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("BasicInitiator");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId().toString());
			//결재선 변경
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);

			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			/* 분기처리
			 * 1. 수신쪽 처리인 경우
			 * 2. 발신쪽 처리인 경우
			*/
			int divisionIndex = 0;
			int stepIndex = 0;
			
			String hasReceipt = "false";
			if(delegateTask.hasVariable("g_hasReceipt")){
				hasReceipt = (String)delegateTask.getVariable("g_hasReceipt");
			}
			
			if(hasReceipt.equalsIgnoreCase("true")){//request
				JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
				divisionIndex = Integer.parseInt(activeDivision.get("divisionIndex").toString());
				stepIndex = 0;
			} else {//draft
				divisionIndex = 0;
				stepIndex = 0;
			}
			
			//결재선 수정 - g_appvLine만 사용
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", CoviFlowVariables.APPV_PENDING);
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			int stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			
			//전결 처리
			boolean isAuthorized = false;
			isAuthorized = CoviFlowApprovalLineHelper.isAuthorized(pendingStep);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//workitem, workitem desc, performer 생성 및 update
			
			// 기안자 (재기안자)
			if(!delegateTask.hasVariable("g_basic_isRedraftCharge") || ((String)delegateTask.getVariable("g_basic_isRedraftCharge")).equalsIgnoreCase("n")){
				// [19-02-27] kimhs, 재기안자인 경우 task 새로 생성되도록 조치함(charge => initiator => approval)
				apvLineObj = CoviFlowApprovalLineHelper.clearStep(apvLineObj, divisionIndex, stepIndex, "");
				
				//재기안자의 경우 workitem의 Charge값에 재기안자 이름
				//apvLineObj = CoviFlowWorkHelper.setActivity("initiator", delegateTask, ctx, "T006");
				apvLineObj = CoviFlowWorkHelper.setActivity("initiator", delegateTask, ctx, "T006", apvLineObj);
				
				//Step 1. performer를 할당.
				//LOG.info("Initiator performer를 할당");
				
				//Step 2. 결재선 수정 부분이 있음.
				//의견 표시, wiid
				
				//complete 이후 후처리
				//결재선 수정 - g_appvLine만 사용
				String isMobile = ctxJsonObj.containsKey("isMobile") ? ctxJsonObj.get("isMobile").toString() : "N";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
				}
				
				apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", "datecompleted", sdf.format(new Date()));
				
				//mobile
				if(isMobile.equalsIgnoreCase("Y")){
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", "mobileType", "y");
				}
				
				if(delegateTask.getVariable("g_basic_approvalResult") == "rejected") { // 접수자가 반려하는 경우
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", CoviFlowVariables.APPV_REJECT);
				} else {
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", CoviFlowVariables.APPV_COMP);
				
					//후결 처리
					boolean isReview = false;
								
					if(stepSize > 1 && !isAuthorized){
						apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
						apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
					}
				}
			}
			// 부서 수신함에서 담당자 지정 시
			else{
			}
			
			//결재선 update
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
			
			//txManager.commit(status);
			
			// 기안 성공 이후에 연동 호출
			String isLegacy = "";
			
			if(delegateTask.hasVariable("g_isLegacy")){
				isLegacy = (String)delegateTask.getVariable("g_isLegacy");	
			}
			
			if(StringUtils.isNotBlank(isLegacy)&&CoviFlowWorkHelper.checkLegacyInfo(isLegacy, "DRAFT"))
			{
				if(!delegateTask.getProcessDefinitionId().toString().contains("request")
						&& !hasReceipt.equalsIgnoreCase("true")){
					CoviFlowWorkHelper.callLegacy(delegateTask, "DRAFT", null, null, null);
				} 			
			}

			if(StringUtils.isNotBlank(isLegacy)&&CoviFlowWorkHelper.checkLegacyInfo(isLegacy, "OTHERSYSTEM"))
			{
				if(hasReceipt.equalsIgnoreCase("true") && !delegateTask.hasVariable("g_isDistribution")){
					CoviFlowWorkHelper.callLegacy(delegateTask, "OTHERSYSTEM", null, null, null);
				} 			
			}
			
			//기안 task complete 처리
			//async로 처리되는 것 같은 데 문제 여부 확인이 필요.
			List<String> deleteTasks = new ArrayList<String>();
			deleteTasks.add(delegateTask.getId());
			delegateTask.setVariable("g_deleteTasks", deleteTasks);
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicInitiatorHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		LOG.info("BasicInitiator call complete");
		ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
		LOG.info("BasicInitiator task completed");
	}

}