/**
 * @Class Name : SubApproverHandler.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class SubApproverHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(SubApproverHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("SubApprover");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			int execId = Integer.parseInt(delegateTask.getExecutionId());
			
			//workitem, workitem desc, performer 생성 및 update
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			
			if(delegateTask.hasVariable("g_sub_isRedraftCharge") && ((String)delegateTask.getVariable("g_sub_isRedraftCharge")).equalsIgnoreCase("y")){
				JSONObject chargeInfo = new JSONObject();
				Map<String, Object> chargeRMap = new HashMap<String, Object>();
				chargeRMap.put("processID", execId);
				
				chargeInfo = processDAO.selectCharge(chargeRMap);
				
				Map<String, Object> chargeWorkitemUMap = new HashMap<String, Object>();
				chargeWorkitemUMap.put("userCode", "admin");
				chargeWorkitemUMap.put("userName", "admin");
				chargeWorkitemUMap.put("workItemID", chargeInfo.get("WorkItemID").toString());
				
				processDAO.updateWorkItemForCharge(chargeWorkitemUMap);
				
				Map<String, Object> chargePerformerUMap = new HashMap<String, Object>();
				chargePerformerUMap.put("state", "2");
				chargePerformerUMap.put("performerID", chargeInfo.get("PerformerID").toString());
				
				processDAO.updatePerformerForCharge(chargePerformerUMap);
				
				//workitem, workitem desc, performer 생성 및 update
				JSONObject apvLineObj = CoviFlowWorkHelper.setActivityForSubProcess("approver", delegateTask, ctx, "T000");
				delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			}else{
				//workitem, workitem desc, performer 생성 및 update
				CoviFlowWorkHelper.setActivityForSubProcess("approver", delegateTask, ctx, "T000");
			}
			//Step 1. performer를 할당.
						
			//Step 2. businessState 할당
			
			//Step 3. 예고함 변경
			
			//Step 4. 
			//1. 결재자가 부서장인 경우 기존에 할당된 조직장과 현재 할당된 조직장을 비교한다.
	        //2. 현재 조직장이 존재하면서 기 할당 조직장과 틀린경우 할당 조직장을 현재 조직장으로 변경한다.
	        //3. WORKITEM을 할당받은 PERFORMER가 대결사용을(DEPUTY_USAGE : '1') 하면서 대결자를 지정한 경우 대결자를 지정한다.
			
			//Step 4. 결재 안내 메일 발송
			
			//Step 5. 결재 행위 후 처리 부분은 분석이 더 필요	
			
			//txManager.commit(status);
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("SubApproverHandler", e);
			throw e;
		}finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}

}