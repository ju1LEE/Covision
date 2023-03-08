/**
 * @Class Name : RequestRouteDeptProcess.java
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

public class RequestRouteDeptProcess implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteDeptProcess.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteDeptProcess");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String destination = "";
			String hasReceiptBox = "false";
			boolean hasJF = false;
			boolean hasChargeOU = false;
			
			if(execution.hasVariable("g_hasReceipt")){
				hasReceiptBox = execution.getVariable("g_hasReceipt").toString();
			}
			
			// [2020-02-19] 수정된 소스 반영 이전에 진행중이던 문서 오류발생하지 않도록 예외처리 함.
			if(execution.hasVariable("g_jfids")) {
				if(hasReceiptBox.equalsIgnoreCase("true"))
				{
					destination = "RequestRouteChargedDeptLegacy"; 			
				} else {
					destination = "RequestRouteIssueDocNumber";
				}
			}
			else {
				if(execution.hasVariable("g_jfid")){
					if(StringUtils.isNotBlank((String)execution.getVariable("g_jfid"))){
						hasJF = true;	
					} else {
						hasJF = false;
					}
				}
				
				if(execution.hasVariable("g_chargeOU")){
					if(StringUtils.isNotBlank((String)execution.getVariable("g_chargeOU"))){
						hasChargeOU = true;	
					} else {
						hasChargeOU = false;
					}
				}
				
				if(hasReceiptBox.equalsIgnoreCase("true")||hasJF||hasChargeOU)
				{
					//결재선 변경
					JSONParser parser = new JSONParser();
					JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
					String apvLine = (String)execution.getVariable("g_appvLine");
					JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
					int piid = Integer.parseInt(execution.getProcessInstanceId());
					int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
					
					//jfid가 존재하면 receive division을 추가
					if(hasJF){
						String jf = (String)execution.getVariable("g_jfid");
						String jfId = "";
						String jfName = "";
						if(StringUtils.isNotBlank(jf)){
							jfId = jf.split("@")[0];
							jfName = jf.split("@")[1];
						}
						
						//업무담당함에 대해서 division을 추가할 것
						/*
						 * {
							"taskinfo": {
								"_status": "pending",
								"_result": "inactive",
								"_kind": "receive",
								"_datereceived": "2017-01-24 17:39:45"
							},
							"step": {
								"ou": {
									"role": {
										"taskinfo": {
											"_status": "pending",
											"_result": "inactive",
											"_kind": "normal",
											"_datereceived": "2017-01-24 17:39:45"
										},
										"_code": "0",
										"_name": "카드신청서",
										"_oucode": "0",
										"_ouname": "카드신청서"
									},
									"_code": "0",
									"_name": "카드신청서"
								},
								"_unittype": "person",
								"_routetype": "receive",
								"_name": "담당결재"
							},
							"_divisiontype": "receive",
							"_name": "담당결재"
						   }
						 * 
						 * 
						 * */
						JSONObject division = new JSONObject();
						JSONObject divTaskInfo = new JSONObject();
						JSONObject step = new JSONObject();
						JSONObject ou = new JSONObject();
						JSONObject role = new JSONObject();
						JSONObject roleTaskInfo = new JSONObject();
						
						//role > taskinfo
						roleTaskInfo.put("status", "pending");
						roleTaskInfo.put("result", "pending");
						roleTaskInfo.put("kind", "normal");
						roleTaskInfo.put("datereceived", sdf.format(new Date()));
						
						//role
						role.put("taskinfo", roleTaskInfo);
						role.put("code", jfId);
						role.put("name", jfName);
						role.put("oucode", jfId);
						role.put("ouname", jfName);
						
						//ou
						ou.put("role", role);
						ou.put("code", jfId);
						ou.put("name", jfName);
						
						//step
						step.put("ou", ou);
						step.put("unittype", "person");
						step.put("routetype", "receive");
						step.put("name", "담당결재");
						
						//division > taskinfo
						divTaskInfo.put("status", "pending");
						divTaskInfo.put("result", "pending");
						divTaskInfo.put("kind", "receive");
						divTaskInfo.put("datereceived", sdf.format(new Date()));
						
						//division
						division.put("taskinfo", divTaskInfo);
						division.put("step", step);
						division.put("divisiontype", "receive");
						division.put("name", "담당결재");
						
						apvLineObj = CoviFlowApprovalLineHelper.setDivision(apvLineObj, division);
						
					} else if(hasChargeOU){
						String chargeOU = (String)execution.getVariable("g_chargeOU");
						String chargeOUID = "";
						String chargeOUName = "";
						if(StringUtils.isNotBlank(chargeOU)){
							chargeOUID = chargeOU.split("@")[0];
							chargeOUName = chargeOU.split("@")[1];
						}
						
						JSONObject division = new JSONObject();
						JSONObject divTaskInfo = new JSONObject();
						JSONObject step = new JSONObject();
						JSONObject ou = new JSONObject();
						JSONObject ouTaskInfo = new JSONObject();
						
						//role > taskinfo
						ouTaskInfo.put("status", "pending");
						ouTaskInfo.put("result", "pending");
						ouTaskInfo.put("kind", "normal");
						ouTaskInfo.put("datereceived", sdf.format(new Date()));
						
						//ou
						ou.put("taskinfo", ouTaskInfo);
						ou.put("code", chargeOUID);
						ou.put("name", chargeOUName);
						
						//step
						step.put("ou", ou);
						step.put("unittype", "ou");
						step.put("routetype", "receive");
						
						//division > taskinfo
						divTaskInfo.put("status", "pending");
						divTaskInfo.put("result", "pending");
						divTaskInfo.put("kind", "receive");
						divTaskInfo.put("datereceived", sdf.format(new Date()));
						
						//division
						division.put("taskinfo", divTaskInfo);
						division.put("step", step);
						division.put("divisiontype", "receive");
						division.put("name", "담당결재");
						division.put("oucode", chargeOUID);
						division.put("ouname", chargeOUName);
						
						apvLineObj = CoviFlowApprovalLineHelper.setDivision(apvLineObj, division);
						
					} else{
						apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, 1, CoviFlowVariables.APPV_PENDING);	
					}
					
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
					
					execution.setVariable("g_appvLine", apvLineObj.toJSONString());
					
					destination = "RequestRouteChargedDeptLegacy"; 			
				} else {
					destination = "RequestRouteIssueDocNumber";
				}
			}
			
			execution.setVariable("g_request_destination", destination);
			
			//txManager.commit(status);
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteDeptProcess", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
}
