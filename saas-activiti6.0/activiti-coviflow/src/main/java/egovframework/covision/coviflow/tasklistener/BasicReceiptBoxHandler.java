/**
 * @Class Name : BasicReceiptBoxHandler.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class BasicReceiptBoxHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicReceiptBoxHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("BasicReceiptBox");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//active division을 가져옴
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
			int divisionIndex = Integer.parseInt(activeDivision.get("divisionIndex").toString());
			
			//차후 수신함의 상태 변경은 재기안시로 변경할 것
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, 0, "", CoviFlowVariables.APPV_PENDING);
			
			//수신함
			JSONObject ret = new JSONObject();
			ret = CoviFlowWorkHelper.setListActivity("수신함", piid, delegateTask, divisionIndex, "R", 288);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			//부서는 1개만 있다고 가정함.
			JSONObject pendingOu = (JSONObject)ouArray.get(0);
			pendingOu.put("taskid", ret.get("taskid").toString());
			pendingOu.put("wiid", ret.get("wiid").toString());
			pendingOu.put("widescid", ret.get("widescid").toString());
			pendingOu.put("pfid", ret.get("pfid").toString());
			
			//process business state update
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("businessState", "03_01_02");
			procUMap.put("processState", 288);
			procUMap.put("processID", piid);
			
			processDAO.updateProcessBusinessState(procUMap);
				
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
			
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//Step 1. performer를 할당.
			//LOG.info("ReceiptBox performer를 할당");
			
			//Step 2. 배치로 담당자가 할당되는 경우에 대한 처리 파악이 필요 - Bas_TWPo_ProcessReceiptBox 스크립트를 다시 분석할 것
			//결재선을 새로 생성하는 부분이 있음.
			
			//Step 3. mail을 발송 - Bas_TWPr_RecipientSendMailWorkItem
			//LOG.info("ReceiptBox 메일을 발송");
			
			//delegateTask.getExecution().getEngineServices().getTaskService().complete(delegateTask.getId());
			//LOG.info("수신함 completed");
			
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicReceiptBoxHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}

}