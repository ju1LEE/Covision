/**
 * @Class Name : RequestEndRejectedExcutionListener.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.excutionlistener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
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

@SuppressWarnings("serial")
public class RequestEndRejectedExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(RequestEndRejectedExcutionListener.class);
	
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestEndRejected");
		/*
		 * Request 프로세스 반려 종료 처리
		 * */
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			JSONParser parser = new JSONParser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject apvLineObj = (JSONObject)parser.parse((String)execution.getVariable("g_appvLine"));

			//0. 변수 선언
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			String formName = ctxJsonObj.get("FormName").toString();
			String formPrefix = ctxJsonObj.get("FormPrefix").toString();
			
			String businessState = "02_02_01";
			//g_hasReceiptBox 값에 따라 분기처리 구현 필요
			int state = 528;
			
			String requestApprovalResult = "";
			if(execution.hasVariable("g_request_approvalResult")){
				requestApprovalResult = (String) execution.getVariable("g_request_approvalResult");	
			}
			
			String basicApprovalResult = "";
			if(execution.hasVariable("g_basic_approvalResult")){
				basicApprovalResult = (String) execution.getVariable("g_basic_approvalResult");	
			}
			
			if((requestApprovalResult == null || !requestApprovalResult.equalsIgnoreCase("cancelled")) && (basicApprovalResult == null || !basicApprovalResult.equalsIgnoreCase("cancelled")))
			{
				//결재선 처리
				apvLineObj = CoviFlowApprovalLineHelper.setRootAttr(apvLineObj, "status", "rejected");
				apvLineObj = CoviFlowApprovalLineHelper.setRootAttr(apvLineObj, "datecompleted", sdf.format(new Date()));
				
				CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
				
				//process update
				Map<String, Object> procUMap = new HashMap<String, Object>();
				procUMap.put("businessState", businessState);
				procUMap.put("processState", state);
				procUMap.put("processID", piid);
				
				processDAO.updateProcess(procUMap);
				
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
				
				//ProcessArchive Insert
				Map<String, Object> procArchMap = new HashMap<String, Object>();
				procArchMap.put("processID", piid);
				procArchMap.put("formName", formName);
				procArchMap.put("formPrefix", formPrefix);
				procArchMap.put("divisionKind", "D");
				procArchMap.put("docFolder", null);
				procArchMap.put("docFolderName", null);
				procArchMap.put("saveTermExpired", null);
				procArchMap.put("ownerUnitID", "");
				procArchMap.put("entCode", "");
							
				processDAO.insertProcessArchive(procArchMap);
				
				//ProcessDescriptionArchive Insert
				Map<String, Object> procDescMap = new HashMap<String, Object>();
				procDescMap.put("processID", piid);
				
				processDAO.insertProcessDescriptionArchive(procDescMap);
				
				//not in 조건에 넣을 정보
				List<String> subkindList = new ArrayList<String>();
				subkindList.add("T010");
				subkindList.add("R");
				
				//WorkitemArchive Insert
				Map<String, Object> procWorkitemArchMap = new HashMap<String, Object>();
				procWorkitemArchMap.put("piBusinessState", "02_02");
				procWorkitemArchMap.put("processID", piid);
				procWorkitemArchMap.put("subkindList", subkindList);
				
				processDAO.insertWorkitemArchive(procWorkitemArchMap);
										
				//DomainArchive Insert
				Map<String, Object> procMap = new HashMap<String, Object>();
				procMap.put("processID", piid);
				
				processDAO.insertDomainArchive(procMap);
				
				if(execution.hasVariable("g_execIds")){
					List<Integer> execIds = (List<Integer>)execution.getVariable("g_execIds");
					for(Integer execid : CoviFlowApprovalLineHelper.removeDupeFromExecIds(execIds, piid)){
						//process update
						procUMap.put("processID", execid);
						processDAO.updateProcess(procUMap);
						
						//ProcessArchive Insert
						procArchMap.put("processID", execid);
						processDAO.insertProcessArchive(procArchMap);
						
						//ProcessDescriptionArchive Insert
						procDescMap.put("processID", execid);
						processDAO.insertProcessDescriptionArchive(procDescMap);
						
						//WorkitemArchive Insert
						procWorkitemArchMap.put("processID", execid);
						processDAO.insertWorkitemArchive(procWorkitemArchMap);
						
						//DomainArchive Insert
						procMap.put("processID", execid);
						processDAO.insertDomainArchive(procMap);
					}
				}
			}
			
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestEndRejected", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	
}