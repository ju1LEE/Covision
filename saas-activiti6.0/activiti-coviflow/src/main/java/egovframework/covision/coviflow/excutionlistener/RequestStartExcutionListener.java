/**
 * @Class Name : RequestStartExcutionListener.java
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

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.json.simple.JSONArray;
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
import egovframework.covision.coviflow.util.CoviFlowWSHelper;

@SuppressWarnings("serial")
public class RequestStartExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(RequestStartExcutionListener.class);
	
	@SuppressWarnings("unchecked")
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("Request Process Start.");
		
		/*
		 * Request 프로세스 시작 시 처리가 들어 올 곳
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
						
			Object ctxObj;
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = new JSONObject();
			if(execution.hasVariable("g_context"))
			{
				ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			} else {
				//파라미터 변수 처리
				ctxObj = parser.parse(new FileReader("D:/temp/appv_json/parameters2.json"));
				ctxJsonObj = (JSONObject)ctxObj;
			}
			
			/*
			 * g_context는 serializable type, scope은 global로 저장할 것
			 * 아래는 type에 대한 테스트 
			 
			LOG.info("excution id : " + execution.getId() + ", g_context : " + ctxObj.toString());
			execution.setVariable("g_context", ctxObj);
			
			Object jsonObj = (Object)execution.getVariable("g_context");
			
			LOG.info("g_context : " + jsonObj.toString());
			*/
			
			//LOG.info("g_context : " + ctxJsonObj.toString());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			JSONObject apvLineObj = (JSONObject)ctxJsonObj.get("ApprovalLine");
			//기안자의 step을 제거
			//JSONObject privateDomain = CoviFlowApprovalLineHelper.removeStepTask(apvLineObj, 0, 0);
						
			//결재선 수정 - g_appvLine만 사용
			apvLineObj = CoviFlowApprovalLineHelper.setRootAttr(apvLineObj, "status", "pending");
			apvLineObj = CoviFlowApprovalLineHelper.setRootAttr(apvLineObj, "datecreated", sdf.format(new Date()));
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, 0, CoviFlowVariables.APPV_PENDING);
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, 0, "datereceived", sdf.format(new Date()));
			
			/* data insert 처리 */
			//public domain data
			//private domain data
			/*
			 * if (Request.QueryString["mode"] != "REDRAFT") 
			 * INPUT.add("@OWNER_ID", Request.QueryString["usid"] + "_" + Request.QueryString["fmpf"]);
			 * 재기안 INPUT.add("@OWNER_ID", Request.QueryString["usid"] + "_" + Request.QueryString["fmpf"] + "__" + Request.QueryString["mode"]); 
			 * */

			if(CoviFlowApprovalLineHelper.HasReceiveDivision(apvLineObj)){
				execution.setVariable("g_hasReceipt", "true");
			} else {
				execution.setVariable("g_hasReceipt", "false");
			}
			
			//0. 변수 선언
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			ctxJsonObj.put("processID", execution.getProcessInstanceId().toString());
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionAttr(apvLineObj, 0, "processID", execution.getProcessInstanceId().toString());
						
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//int scid = Integer.parseInt(ctxJsonObj.get("SchemaID").toString());
			int fmid = Integer.parseInt(ctxJsonObj.get("FormID").toString());
			String mode = ctxJsonObj.get("mode").toString();
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formPrefix = ctxJsonObj.get("FormPrefix").toString();
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String isSecureDoc = (String)pDesc.get("IsSecureDoc");
			String isFile = (String)pDesc.get("IsFile");
			String fileExt = (String)pDesc.get("FileExt");
			String isComment = (String)pDesc.get("IsComment");
			String usid = (String)ctxJsonObj.get("usid");
			String usdn = (String)ctxJsonObj.get("usdn");
			String dpid_apv = (String)ctxJsonObj.get("dpid_apv");
			String dpdn_apv = (String)ctxJsonObj.get("dpdn_apv");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String jfid = (String)formInfoExt.get("JFID");
			String chargeOU  = (String)formInfoExt.get("ChargeOU");
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
			String reserved1 = "";
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
			procDescMap.put("BusinessData6", BusinessData6);
			procDescMap.put("BusinessData7", BusinessData7);
			procDescMap.put("BusinessData8", BusinessData8);
			procDescMap.put("BusinessData9", BusinessData9);
			procDescMap.put("BusinessData10", BusinessData10);
			
			int processDescID = 0;
			processDAO.insertProcessDescription(procDescMap);
			processDescID = (int)procDescMap.get("ProcessDescriptionID");
			ctxJsonObj.put("processDescID", Integer.toString(processDescID));
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionAttr(apvLineObj, 0, "processDescID", Integer.toString(processDescID));
			
			//process insert
			Map<String, Object> procMap = new HashMap<String, Object>();
			procMap.put("processID", piid);
			procMap.put("processKind", 0);
			procMap.put("parentProcessID", 0);
			procMap.put("parentInstanceID", 0);
			procMap.put("processDescriptionID", processDescID);
			procMap.put("processName", "Request");
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
			
			processDAO.insertProcess(procMap);
			
			Map<String, Object> appvLinePublicMap = new HashMap<String, Object>();
			appvLinePublicMap.put("processID", piid);
			appvLinePublicMap.put("domainDataContext", apvLineObj.toJSONString());
			
			//domain data  insert
			processDAO.insertAppvLinePublic(appvLinePublicMap);
			
			//call private domain data insert
			JSONArray params = new JSONArray();
			JSONObject param = new JSONObject();
			param.put("mode", "DRAFT");
			param.put("approvalLine", apvLineObj.toJSONString());
			param.put("actionMode", "DRAFT");
			param.put("FormSubject", formSubject);
			param.put("FormName", formName);
			param.put("usid", usid);
			param.put("usdn", usdn);
			param.put("FormPrefix", formPrefix);
			
			params.add(param);
			CoviFlowWSHelper.invokeApi("APVLINE", params);			
			
			//txManager.commit(status);

			Map<String, Object> global = new HashMap<String, Object>();
			//변수의 갯수가 성능에 끼치는 영향?
			//별도로 변수화 할 요소와
			//하나의 변수에서 parsing후 사용할 요소의 구분이 필요함.
			// 변수의 갯수가 성능에 영향을 미칠 경우 스키마에 대한 값 묶어서 사용 필요
			global.put("g_context", ctxJsonObj.toJSONString());
			global.put("g_appvLine", apvLineObj.toJSONString());
			global.put("g_piid", piid);
			global.put("g_request_hasStepsFinished", "false");
			global.put("g_currentRouteType", "");
			global.put("g_currentUnitType", "");
			global.put("g_request_approvalResult", "");
			global.put("g_request_approver", "");
			global.put("g_jfid", jfid);
			global.put("g_jfids", jfid); // 담당업무함 여러개인 경우를 고려하여 추가함.
			global.put("g_chargeOU", chargeOU);
			global.put("g_chargeOUs", chargeOU); // 담당부서 여러개인 경우를 고려하여 추가함.
			global.put("g_subject", formSubject);
			global.put("g_isLegacy", isLegacy);
			global.put("g_docNumber", "");
			//hasReceipt에 대한 추가 분석 필요.
			//global.put("g_hasReceipt", "true");
			//global.put("ReceiptBoxId", "");
			//global.put("ReceiptBoxName", "");
			//global.put("ReceiptBoxSubKind", "");
			//global.put("HasDocumentBox", "");
			//global.put("DocumentBoxId", "");
			//global.put("ReceiptBoxId", "");
			//global.put("ReceiptBoxId", "");
			
			execution.setVariables(global);
			
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestProcessStart", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	
}