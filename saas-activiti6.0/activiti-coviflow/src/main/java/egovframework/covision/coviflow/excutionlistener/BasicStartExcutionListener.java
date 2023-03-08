/**
 * @Class Name : BasicStartExcutionListener.java
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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
public class BasicStartExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(BasicStartExcutionListener.class);
	
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("basic Process Start.");
		/*
		 * basic 프로세스 시작 시 처리가 들어 올 곳
		 * */
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowPropHelper propHelper = CoviFlowPropHelper.getInstace();
			//configuration 변수 처리
			execution.setVariable("g_config", propHelper.getPropertyObject().toJSONString());
			
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			JSONObject apvLineObj = new JSONObject();
			Object apvLine = execution.getVariable("g_appvLine");
			if(apvLine instanceof LinkedHashMap){
				apvLineObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
			} else {
				apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			}
			
			String docNumber = (String)execution.getVariable("g_docNumber");
			
			/* 분기처리
			 * 1. 수신쪽 처리인 경우
			 * 2. 발신쪽 처리인 경우
			*/
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			int divisionIndex = 0;
			
			JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
			if(activeDivision.get("isDivision").toString().equalsIgnoreCase("true")){
				divisionIndex = Integer.parseInt(activeDivision.get("divisionIndex").toString());
				execution.setVariable("g_hasReceipt", "true");
				
				// [19-09-02] kimhs, 수신자 or 수신부서 구분 값(부서내반송 시 사용)
				if(CoviFlowApprovalLineHelper.getStepAttr(apvLineObj, 1, 0, "unittype").equals("ou")) {
					execution.setVariable("g_hasReceiptType", "ou");
				}
				else {
					execution.setVariable("g_hasReceiptType", "person");
				}
			}
			
			//기안자의 step을 제거
			//JSONObject privateDomain = CoviFlowApprovalLineHelper.removeStepTask(apvLineObj, divisionIndex, 0);
			
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, divisionIndex, CoviFlowVariables.APPV_PENDING);
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, divisionIndex, "datereceived", sdf.format(new Date()));
			
			//0. 변수 선언
			int parentPiid = Integer.parseInt(execution.getVariable("g_piid").toString());
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionAttr(apvLineObj, divisionIndex, "processID", execution.getProcessInstanceId().toString());
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			if(execution.hasVariable("g_subFiid")) {
				fiid = Integer.parseInt(execution.getVariable("g_subFiid").toString());
			}
			int fmid = Integer.parseInt(ctxJsonObj.get("FormID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formPrefix = ctxJsonObj.get("FormPrefix").toString();
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			if(execution.hasVariable("g_subSubject")) {
				formSubject = execution.getVariable("g_subSubject").toString();
			}
			String subRowSeq = "";
			if(execution.hasVariable("g_subRowSeq")) {
				subRowSeq = execution.getVariable("g_subRowSeq").toString();
			}
			String isSecureDoc = (String)pDesc.get("IsSecureDoc");
			String isFile = (String)pDesc.get("IsFile");
			String fileExt = (String)pDesc.get("FileExt");
			String isComment = (String)pDesc.get("IsComment");
			String usid = (String)ctxJsonObj.get("usid");
			String usdn = (String)ctxJsonObj.get("usdn");
			String dpid_apv = (String)ctxJsonObj.get("dpid_apv");
			String dpdn_apv = (String)ctxJsonObj.get("dpdn_apv");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String jfid = (!execution.hasVariable("g_jfid") || execution.getVariable("g_jfid") == null ||execution.getVariable("g_jfid").toString().equals("")) ? (String)formInfoExt.get("JFID") : execution.getVariable("g_jfid").toString();
			String isLegacy = "";
			if(formInfoExt.get("IsLegacy") instanceof String){
				isLegacy = (String)formInfoExt.get("IsLegacy");
			} else {
				JSONObject jsonLegacy = (JSONObject)formInfoExt.get("IsLegacy");
				isLegacy = jsonLegacy.toJSONString();
			}
			String approvalStep = ""; //approvalStep 구하는 함수를 CoviFlowApprovalLineHelper에 생성할 것
			String approverSIPAddress = ""; //SIP값
			String isReserved = (String)pDesc.get("IsReserved");
			String reservedType = ""; //ReservedGubun?
			String reservedTime = null;
			String priority = (String)pDesc.get("Priority");
			String isModify = (String)pDesc.get("IsModify");
			String reserved1 = (docNumber != null && !docNumber.equals("")) ? docNumber : "";
			String reserved2 = "";
			String BusinessData1 = (String)pDesc.get("BusinessData1");
			String BusinessData2 = (String)pDesc.get("BusinessData2");
			String BusinessData3 = (String)pDesc.get("BusinessData3");
			String BusinessData4 = (String)pDesc.get("BusinessData4");
			String BusinessData5 = (String)pDesc.get("BusinessData5");
			String BusinessData6 = (String)pDesc.get("BusinessData6");
			String BusinessData7 = (String)pDesc.get("BusinessData7");
			String BusinessData8 = (String)pDesc.get("BusinessData8");
			String BusinessData9 = (String)pDesc.get("BusinessData9");
			String BusinessData10 = (String)pDesc.get("BusinessData10");
			
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//process description insert
			Map<String, Object> procDescMap = new HashMap<String, Object>();
			procDescMap.put("formInstID", fiid);
			procDescMap.put("formID", fmid);
			procDescMap.put("formPrefix", formPrefix);
			procDescMap.put("formName", formName);
			procDescMap.put("formSubject", formSubject);
			procDescMap.put("isSecureDoc", isSecureDoc);
			procDescMap.put("isFile", isFile);
			procDescMap.put("fileExt", fileExt);
			procDescMap.put("isComment", isComment);
			procDescMap.put("approverCode", usid);
			procDescMap.put("approverName", usdn);
			procDescMap.put("approvalStep", approvalStep);
			procDescMap.put("approverSIPAddress", approverSIPAddress);
			procDescMap.put("isReserved", isReserved);
			procDescMap.put("reservedGubun", reservedType);
			procDescMap.put("reservedTime", reservedTime);
			procDescMap.put("priority", priority);
			procDescMap.put("isModify", isModify);
			procDescMap.put("reserved1", reserved1);
			procDescMap.put("reserved2", reserved2);
			procDescMap.put("BusinessData1", BusinessData1);
			procDescMap.put("BusinessData2", BusinessData2);
			procDescMap.put("BusinessData3", BusinessData3);
			procDescMap.put("BusinessData4", BusinessData4);
			procDescMap.put("BusinessData5", BusinessData5);
			procDescMap.put("BusinessData6", subRowSeq);
			procDescMap.put("BusinessData7", BusinessData7);
			procDescMap.put("BusinessData8", BusinessData8);
			procDescMap.put("BusinessData9", BusinessData9);
			procDescMap.put("BusinessData10", BusinessData10);
			
			int processDescID = 0;
			processDAO.insertProcessDescription(procDescMap);
			processDescID = (int)procDescMap.get("ProcessDescriptionID");
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionAttr(apvLineObj, divisionIndex, "processDescID", Integer.toString(processDescID));
			
			//process insert
			Map<String, Object> procMap = new HashMap<String, Object>();
			procMap.put("processID", piid);
			procMap.put("processKind", 0);
			procMap.put("parentProcessID", parentPiid);
			procMap.put("parentInstanceID", 0); //불필요시 삭제할 것
			procMap.put("processDescriptionID", processDescID);
			procMap.put("processName", "Basic");
			procMap.put("docSubject", formSubject);
			procMap.put("businessState", "01_01_01");
			procMap.put("initiatorID", usid);
			procMap.put("initiatorName", usdn);
			procMap.put("initiatorUnitID", dpid_apv);
			procMap.put("initiatorUnitName", dpdn_apv);
			procMap.put("formInstID", fiid);
			procMap.put("processState", 288);
			procMap.put("initiatorSIPAddress", "");
			procMap.put("startDate", sdf.format(new Date()));			
			
			String initiatorID = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
			if(!initiatorID.equals(usid)) {
				Map<String, Object> initParams = new HashMap<String, Object>();
				initParams.put("processID", parentPiid);
				JSONObject initiatorInfo = processDAO.selectProcessInitiatorInfo(initParams);

				if(initiatorInfo != null && initiatorInfo.size() > 0) {
					procMap.put("initiatorID", initiatorInfo.get("InitiatorID"));
					procMap.put("initiatorName", initiatorInfo.get("InitiatorName"));
					procMap.put("initiatorUnitID", initiatorInfo.get("InitiatorUnitID"));
					procMap.put("initiatorUnitName", initiatorInfo.get("InitiatorUnitName"));
				}
			}			
			
			processDAO.insertProcess(procMap);
			
			Map<String, Object> appvLinePublicMap = new HashMap<String, Object>();
			appvLinePublicMap.put("processID", piid);
			appvLinePublicMap.put("domainDataContext", apvLineObj.toJSONString());
			
			//domain data  insert
			processDAO.insertAppvLinePublic(appvLinePublicMap);
			
			//update parent 결재선
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
			
			//txManager.commit(status);
			
			Map<String, Object> global = new HashMap<String, Object>();
			//변수의 갯수가 성능에 끼치는 영향?
			//별도로 변수화 할 요소와
			//하나의 변수에서 parsing후 사용할 요소의 구분이 필요함.
			// 변수의 갯수가 성능에 영향을 미칠 경우 스키마에 대한 값 묶어서 사용 필요
			global.put("g_context", ctxJsonObj.toJSONString());
			global.put("g_appvLine", apvLineObj.toJSONString());
			global.put("g_docNumber", docNumber);
			global.put("g_basic_hasStepsFinished", "false");
			global.put("g_currentRouteType", "");
			global.put("g_currentUnitType", "");
			global.put("g_basic_approvalResult", "");
			global.put("g_jfid", jfid);
			global.put("g_subject", formSubject);
			global.put("g_isLegacy", isLegacy);
			
			execution.setVariables(global);

		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicProcessStart", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	
}