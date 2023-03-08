/**
 * @Class Name : BasicChargePersonHandler.java
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
import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
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
public class BasicChargePersonHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicChargePersonHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
				
		LOG.info("ChargePerson");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			//workitem, workitem desc, performer 생성 및 update
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
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
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//set workitem
			CoviFlowWorkHelper.setActivity("Charge", delegateTask, ctx, "T008");
			
			//process business state update
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("businessState", "03_01_02");
			procUMap.put("processState", 288);
			procUMap.put("processID", piid);
			
			processDAO.updateProcessBusinessState(procUMap);
			
			//Step 1. 대결 지정 - V Role
			
			//Step 2. performer를 할당.
			//LOG.info("ChargePerson performer를 할당");
			
			//3. WORKITEM을 할당받은 PERFORMER가 대결사용을(DEPUTY_USAGE : '1') 하면서 대결자를 지정한 경우 대결자를 지정한다.
					
			//Step 3. 배치로 담당자가 할당되는 경우에 대한 처리 파악이 필요 - Bas_TWPo_ProcessCharge 스크립트를 다시 분석할 것
			//결재선을 새로 생성하는 부분이 있음.
			
			//Step 4. mail을 발송
			//LOG.info("ChargePerson 메일을 발송");
		
			//txManager.commit(status);
			
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicChargePersonHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}

}