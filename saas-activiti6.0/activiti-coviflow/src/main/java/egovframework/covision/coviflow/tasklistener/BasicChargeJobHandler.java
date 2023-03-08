/**
 * @Class Name : BasicChargeJobHandler.java
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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class BasicChargeJobHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicChargeJobHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("ChargeJob");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//workitem, workitem desc, performer 생성 및 update
			//String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			String jf = (String)delegateTask.getVariable("g_jfid");
			
			String jfId = "";
			String jfName = "";
			if(StringUtils.isNotBlank(jf)){
				jfId = jf.split("@")[0];
				jfName = jf.split("@")[1];
			}
			
			//담당업무함
			apvLineObj = CoviFlowWorkHelper.setListActivity("ChargeJob", delegateTask, jfId, jfName, "2", "T008", 288);
			
			//process business state update
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("businessState", "03_01_02");
			procUMap.put("processState", 288);
			procUMap.put("processID", piid);
			
			processDAO.updateProcessBusinessState(procUMap);
			
			//Step 1. performer를 할당.
			//LOG.info("CoordiReceiptBox performer를 할당");
			
			//Step 2. 배치로 담당자가 할당되는 경우에 대한 처리 파악이 필요 - Bas_TWPo_ProcessCharge 스크립트를 다시 분석할 것
			//결재선을 새로 생성하는 부분이 있음.
			
			//Step 3. mail을 발송
			//LOG.info("ChargeJob 메일을 발송");
			
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
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicChargeJobHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}

}