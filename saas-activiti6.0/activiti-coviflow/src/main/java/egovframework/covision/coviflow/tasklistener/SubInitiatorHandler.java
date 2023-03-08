/**
 * @Class Name : SubInitiatorHandler.java
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
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
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class SubInitiatorHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(SubInitiatorHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("SubInitiator");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//결재선 변경
			Object apvLine = delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = new JSONObject();
			
			if(apvLine instanceof LinkedHashMap)
				apvLineObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
			else 
				apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			
			JSONObject pendingObj = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObj.get("step");
			
			String execId =  delegateTask.getExecutionId();//(String)delegateTask.getVariable("g_execid");
			
			//결재선 수정
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			CoviFlowApprovalLineHelper.setPersonTask(pendingOu, 0, CoviFlowVariables.APPV_PENDING);
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			String receiptOuTaskID = pendingOu.get("taskid").toString();

			//workitem, workitem desc, performer 생성 및 update
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			
			// 기안자
			//if(!delegateTask.hasVariable("g_charge_"+receiptOuTaskID)){
			if(!delegateTask.hasVariable("g_sub_isRedraftCharge") || ((String)delegateTask.getVariable("g_sub_isRedraftCharge")).equalsIgnoreCase("n")){
				//재기안자의 경우 workitem의 Charge값에 재기안자 이름
				apvLineObj = CoviFlowWorkHelper.setActivityForSubProcess("initiator", delegateTask, ctx, "T006");
				
				//Step 1. performer를 할당.
				//LOG.info("Initiator performer를 할당");
				
				//Step 2. 결재선 수정 부분이 있음.
				//의견 표시, wiid
				
				//complete 이후 후처리
				//결재선 수정 - g_appvLine만 사용
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
				}
				JSONObject pendingObj2 = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
				JSONObject pendingStep2 = (JSONObject)pendingObj2.get("step");
				
				//결재선 수정
				JSONObject pendingOu2 = CoviFlowApprovalLineHelper.getPendingOu(pendingStep2, execId);
				if(delegateTask.getVariable("g_sub_approvalResult_" + execId) == "rejected") { // 접수자가 반려하는 경우
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu2, 0, CoviFlowVariables.APPV_REJECT);
				} else {
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu2, 0, CoviFlowVariables.APPV_COMP);
					
					CoviFlowApprovalLineHelper.setPersonTask(pendingOu2, 0, "datecompleted", sdf.format(new Date()));

					JSONObject pendingPerson2 = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu2);
					JSONObject person = (JSONObject)pendingPerson2.get("person");
					JSONObject taskinfo = (JSONObject)person.get("taskinfo");
					
					// 다음 결재자가 있을 경우 pending으로 상태 변경 
					// getPendingPerson에서 넘겨준 person은 ou 내 가장 마지막 결재자를 의미
					// person의 taskinfo가  inactive일 경우는 다음 결재자가 있다는 뜻
					if(taskinfo.get("status").toString().equals("inactive")) {
						CoviFlowApprovalLineHelper.setPersonTask(pendingOu2, 1, CoviFlowVariables.APPV_PENDING);
					}
				}
			}
			// 담당자
			else{
			}
			
			//process business state update
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("businessState", "01_02_03");
			procUMap.put("processState", 288);
			procUMap.put("processID", Integer.parseInt(execId));
			
			processDAO.updateProcessBusinessState(procUMap);
			
			//결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", Integer.parseInt(delegateTask.getProcessInstanceId().toString()));
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			delegateTask.setVariable("g_sub_appvLine", apvLineObj.toJSONString());
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("SubInitiatorHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		//기안 task complete 처리
		//async로 처리되는 것 같은 데 문제 여부 확인이 필요.
		org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
		
		LOG.info("SubInitiator task completed");
	}

}