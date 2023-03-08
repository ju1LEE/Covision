/**
 * @Class Name : RequestPreProcessDept.java
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

public class RequestPreProcessDept implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestPreProcessDept.class);
			
	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestPreProcessDept");
		
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
			
			// [2020-02-19] 수정된 소스 반영 이전에 진행중이던 문서 오류발생하지 않도록 예외처리 함.
			if(execution.hasVariable("g_jfids")) {
				boolean hasChargeOU = false;
				boolean hasJF = false;
				String sJFIDs = (String)execution.getVariable("g_jfids");
				String sChargeOUs = "";
				
				if(execution.hasVariable("g_jfids")){
					if(!StringUtils.isBlank(sJFIDs) && sJFIDs.split("\\^").length > 0) {
						hasJF = true;	
					} else {
						hasJF = false;
					}
				}
				
				if(execution.hasVariable("g_chargeOUs")){
					sChargeOUs = (String)execution.getVariable("g_chargeOUs");
					
					if(!StringUtils.isBlank(sChargeOUs) && sChargeOUs.split("\\^").length > 0) {
						hasChargeOU = true;	
					} else {
						hasChargeOU = false;
					}
				}
				else if(execution.hasVariable("g_chargeOU")){
					sChargeOUs = (String)execution.getVariable("g_chargeOU");
					
					if(StringUtils.isNotBlank((String)execution.getVariable("g_chargeOU"))){
						hasChargeOU = true;	
					} else {
						hasChargeOU = false;
					}
				}
				
				//결재선 변경
				JSONParser parser = new JSONParser();
				JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
				String apvLine = (String)execution.getVariable("g_appvLine");
				JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
				int piid = Integer.parseInt(execution.getProcessInstanceId());
				int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
										
				//jfid가 존재하면 receive division을 추가
				if(hasJF) {
					String jf = sJFIDs.split("\\^")[0];
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
					
					//in parameter 값에 대한 처리
					if(sJFIDs.split("\\^").length > 1)
						execution.setVariable("g_jfids", sJFIDs.replace(jf + "^", ""));
					else
						execution.setVariable("g_jfids", sJFIDs.replace(jf, ""));
					
					execution.setVariable("g_jfid", jf);
				} else if(hasChargeOU){
					String chargeOU = sChargeOUs.split("\\^")[0];					
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
					
					//in parameter 값에 대한 처리
					if(sChargeOUs.split("\\^").length > 1)
						execution.setVariable("g_chargeOUs", sChargeOUs.replace(chargeOU + "^", ""));
					else
						execution.setVariable("g_chargeOUs", sChargeOUs.replace(chargeOU, ""));
					
					execution.setVariable("g_chargeOU", chargeOU);
				} else{
					JSONObject inactiveObject = (JSONObject)CoviFlowApprovalLineHelper.getInactiveDivision(apvLineObj);
					Integer inactiveDivisionIndex = Integer.parseInt(inactiveObject.get("divisionIndex").toString());
					
					// 수신부서 여러개인 경우 고려하여 1 => inactiveDivisionIndex 로 변경함.
					apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, inactiveDivisionIndex, CoviFlowVariables.APPV_PENDING);	
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
			}
			else {
				// 담당업무함이 결재선으로 넘어올 경우 처리
				JSONParser parser = new JSONParser();
				String apvLine = (String)execution.getVariable("g_appvLine");
				JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
				
				JSONObject pengingObj = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
				
				if(CoviFlowApprovalLineHelper.HasReceiveDivision(apvLineObj)
						&& pengingObj.get("unitType").toString().equalsIgnoreCase("role")){
					
					String jfId = pengingObj.get("approverCode").toString();
					String jfName = pengingObj.get("approverName").toString();
					
					execution.setVariable("g_jfid", jfId+"@"+jfName);
				}
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestPreProcessDept", e);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
}
