/**
 * @Class Name : SubReceiptBoxHandler.java
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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
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
public class SubReceiptBoxHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(SubReceiptBoxHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("SubReceiptBox");
		
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
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//active division을 가져옴
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			String execId = delegateTask.getExecutionId();// (String)delegateTask.getVariable("g_execid");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject pendingObj = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObj.get("step");
			int divisionIndex = Integer.parseInt(pendingObj.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObj.get("stepIndex").toString());
			String routeType = pendingObj.get("routeType").toString();
			String allotType = pendingObj.get("allotType").toString();
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			
			//차후 수신함의 상태 변경은 재기안시로 변경할 것
			//apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, 0, "", CoviFlowVariables.APPV_PENDING);
			apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "datereceived", sdf.format(new Date()));
			
			//합의는 C
			//협조는 AS
			//감사 AD, 준법감시 : AE
			String subkind = "C";
			if(routeType.equalsIgnoreCase("consult")){
				subkind = "C";
			} else if(routeType.equalsIgnoreCase("assist")){
				subkind = "AS";
			} else if(routeType.equalsIgnoreCase("audit")){
				subkind = "AD";
				String routeName = pendingStep.get("name").toString();
				if(routeName.equalsIgnoreCase("audit_law_dept")){
					subkind = "AE";
				}
			}
			
			//수신함
			JSONObject ret = new JSONObject();
			if(allotType.equalsIgnoreCase("parallel")){
				apvLineObj = CoviFlowWorkHelper.setMultiActivityOu("수신함", delegateTask, CoviFlowVariables.getGlobalContextStr(delegateTask), delegateTask.getVariable("assignee").toString(), subkind);
			}else if(allotType.equalsIgnoreCase("serial")){
				ret = CoviFlowWorkHelper.setListActivity(pendingStep, "수신함", Integer.parseInt(execId), delegateTask, subkind, 288);
				
				pendingOu.put("taskid", ret.get("taskid").toString());
				pendingOu.put("wiid", ret.get("wiid").toString());
				pendingOu.put("widescid", ret.get("widescid").toString());
				pendingOu.put("pfid", ret.get("pfid").toString());
			}
					
			//process business state update
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("businessState", "01_01_05");
			procUMap.put("processState", 288);
			procUMap.put("processID", piid);
			
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
			
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//Step 1. performer를 할당.
			//LOG.info("ReceiptBox performer를 할당");
			
			//Step 2. 배치로 담당자가 할당되는 경우에 대한 처리 파악이 필요 - Bas_TWPo_ProcessReceiptBox 스크립트를 다시 분석할 것
			//결재선을 새로 생성하는 부분이 있음.
			
			//Step 3. mail을 발송 - Bas_TWPr_RecipientSendMailWorkItem
			//LOG.info("ReceiptBox 메일을 발송");
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("SubReceiptBoxHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}

}