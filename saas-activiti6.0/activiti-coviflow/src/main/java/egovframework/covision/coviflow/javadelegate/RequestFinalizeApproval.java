/**
 * @Class Name : RequestFinalizeApproval.java
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
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class RequestFinalizeApproval implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestFinalizeApproval.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestFinalizeApproval");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			//결재선 변경
			// 기안부서 결재 완료 시, 해당 division completed 처리하기 위해 추가함.
			JSONParser parser = new JSONParser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject apvLineObj = (JSONObject)parser.parse((String)execution.getVariable("g_appvLine"));

			int divisionIndex = 0;
			if(!execution.hasVariable("g_jfids")) {
				JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
				if(activeDivision.get("isDivision").toString().equalsIgnoreCase("true")){
					divisionIndex = Integer.parseInt(activeDivision.get("divisionIndex").toString());
				}
			}
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//결재선 처리
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, divisionIndex, CoviFlowVariables.APPV_COMP);
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, divisionIndex, "datecompleted", sdf.format(new Date()));
			
			Map<String, Object> domainUMap = new HashMap<String, Object>();
			domainUMap.put("domainDataContext", apvLineObj.toJSONString());
			domainUMap.put("formInstID", fiid);
			processDAO.updateAllAppvLinePublic(domainUMap);
			
			//txManager.commit(status);
			
			// [2020-02-19] 수정된 소스 반영 이전에 진행중이던 문서 오류발생하지 않도록 예외처리 함.
			if(execution.hasVariable("g_jfids")) {
				execution.setVariable("g_hasReceipt", "false");
				
				// 수신부서 처리여부 판단
				boolean hasJF = false;
				boolean hasChargeOU = false;
				
				if(execution.hasVariable("g_jfids")){
					if(StringUtils.isNotBlank((String)execution.getVariable("g_jfids"))){
						hasJF = true;	
					} else {
						hasJF = false;
					}
				}
				
				if(execution.hasVariable("g_chargeOUs")){
					if(StringUtils.isNotBlank((String)execution.getVariable("g_chargeOUs"))){
						hasChargeOU = true;	
					} else {
						hasChargeOU = false;
					}
				}
				else if(execution.hasVariable("g_chargeOU")){
					if(StringUtils.isNotBlank((String)execution.getVariable("g_chargeOU"))){
						hasChargeOU = true;	
					} else {
						hasChargeOU = false;
					}
				}
							
				if(CoviFlowApprovalLineHelper.hasReceiveInactiveStep(apvLineObj)||hasJF||hasChargeOU) {
					execution.setVariable("g_hasReceipt", "true");	
				}
			}
			else {
				
			}
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			// Request finalize 시점에 변수 초기화. 2022.4.4
			execution.setVariable("g_request_approvalResult", "");
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestFinalizeApproval", e);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
}
