/**
 * @Class Name : RequestPostDeptRouteProcess.java
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

import java.util.HashMap;
import java.util.Map;

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
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class RequestPostDeptRouteProcess implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestPostDeptRouteProcess.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestPostDeptRouteProcess");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			String reqResult = "";
			if(execution.hasVariable("g_request_approvalResult"))
			{
				reqResult = (String) execution.getVariable("g_request_approvalResult"); 
				if((reqResult == null || reqResult.isEmpty()) && execution.hasVariable("g_basic_approvalResult")) {
					reqResult = (String) execution.getVariable("g_basic_approvalResult");
				}
				
				// [2020-02-19] 수정된 소스 반영 이전에 진행중이던 문서 오류발생하지 않도록 예외처리 함.
				if(execution.hasVariable("g_jfids")) {
					if(reqResult != null && reqResult.equalsIgnoreCase("rejected")){
						execution.setVariable("g_hasReceipt", "false");
					} else {
						execution.setVariable("g_hasReceipt", "false");
						
						//분기처리, 결재선을 분석하여 looping 조건을 판단
						JSONParser parser = new JSONParser();
						String apvLine = (String)execution.getVariable("g_appvLine");
						JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
						
						JSONObject inactiveObject = (JSONObject)CoviFlowApprovalLineHelper.getInactiveDivision(apvLineObj);
						Integer inactiveDivisionIndex = Integer.parseInt(inactiveObject.get("divisionIndex").toString());
						
						if(inactiveDivisionIndex != 0) { // 수신부서 진행인 경우
							execution.setVariable("g_hasReceipt", "true");
						} else {
							if(execution.hasVariable("g_jfids")){
								if(StringUtils.isNotBlank((String)execution.getVariable("g_jfids"))){
									execution.setVariable("g_hasReceipt", "true");
								}
							}
							
							if(execution.hasVariable("g_chargeOUs")){
								if(StringUtils.isNotBlank((String)execution.getVariable("g_chargeOUs"))){
									execution.setVariable("g_hasReceipt", "true");
								}
							}
							else if(execution.hasVariable("g_chargeOU")){
								if(StringUtils.isNotBlank((String)execution.getVariable("g_chargeOU"))){
									execution.setVariable("g_hasReceipt", "true");
								}
							}
						}
					}	
				}
				else {
					if(reqResult != null && reqResult.equalsIgnoreCase("rejected")){
						//execution.setVariable("HasReceipt", "false");
					} else {
						//분기처리, 결재선을 분석하여 looping 조건을 판단
						
						// 다중수신처 고려하여 pending 으로 상태값 변경하도록 추가함.
						JSONParser parser = new JSONParser();
						JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
						String apvLine = (String)execution.getVariable("g_appvLine");
						JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
						int piid = Integer.parseInt(execution.getProcessInstanceId());
						int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
						
						JSONObject inactiveObject = (JSONObject)CoviFlowApprovalLineHelper.getInactiveDivision(apvLineObj);
						Integer inactiveDivisionIndex = Integer.parseInt(inactiveObject.get("divisionIndex").toString());
						
						apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, inactiveDivisionIndex, CoviFlowVariables.APPV_PENDING);
						
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
						
						execution.setVariable("g_appvLine", apvLineObj.toJSONString());
					}	
				}
			}
			
			//out parameter 값에 대한 처리
			//txManager.commit(status);
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestPostDeptRouteProcess", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
}
