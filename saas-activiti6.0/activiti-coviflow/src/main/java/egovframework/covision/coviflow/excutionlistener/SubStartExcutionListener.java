/**
 * @Class Name : SubStartExcutionListener.java
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
public class SubStartExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(SubStartExcutionListener.class);
	
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("SubStartExcutionListener");
		/*
		 * 프로세스 시작 시 처리가 들어 올 곳
		 * */
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
								
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			/* 분기처리
			 * 1. 부서내 합의
			*/
			//execution.getEngineServices().getRuntimeService().getVariable("", "")
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			JSONObject pendingObj = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObj.get("step");
			String allotType = (String)pendingObj.get("allotType");
			int divisionIndex = Integer.parseInt(pendingObj.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObj.get("stepIndex").toString());
			
			apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, divisionIndex, stepIndex, "datereceived", sdf.format(new Date()));
			
			/*
			 * 1. pending step의 pending ou
			 * 2. ou의 taskinfo에 processID, processDescID 삽입
			 * 
			 * */
			
			//0. 변수 선언
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			String execId = execution.getId().toString();
			
			//순차와 병렬을 분기 처리 할 것
			apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "piid", execId);
			apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "execid", execId);
			apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "status", "pending");
			apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "result", "pending");
			
			//data 처리
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int fmid = Integer.parseInt(ctxJsonObj.get("FormID").toString());
			
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
			apvLineObj = CoviFlowApprovalLineHelper.setOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "pdescid", processDescID);
			
			//process insert
			Map<String, Object> procMap = new HashMap<String, Object>();
			procMap.put("processID", Integer.parseInt(execId));
			procMap.put("processKind", 1);
			procMap.put("parentProcessID", piid);
			procMap.put("parentInstanceID", 0); //불필요시 삭제할 것
			procMap.put("processDescriptionID", processDescID);
			procMap.put("processName", "Sub");
			procMap.put("docSubject", formSubject);
			procMap.put("businessState", "03_01_04");
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
			appvLinePublicMap.put("processID", Integer.parseInt(execId));
			appvLinePublicMap.put("domainDataContext", apvLineObj.toJSONString());
			
			//domain data  insert
			processDAO.insertAppvLinePublic(appvLinePublicMap);
			
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
			
			//txManager.commit(status);
			
			Map<String, Object> global = new HashMap<String, Object>();
			//변수의 갯수가 성능에 끼치는 영향?
			//별도로 변수화 할 요소와
			//하나의 변수에서 parsing후 사용할 요소의 구분이 필요함.
			//global.put("g_context", ctxJsonObj.toJSONString());
			global.put("g_appvLine", apvLineObj.toJSONString());
			//global.put("g_sub_hasStepsFinished", "false");
			//global.put("g_execid", execId);
			//global.put("g_currentUnitType", "");
			global.put("g_sub_approvalResult_" + execId , "completed");
			List<Integer> execIds = CoviFlowApprovalLineHelper.getExecIdsFromOuTask(pendingStep);
			if(execIds.size() > 0){
					List<Integer> tempIds = new ArrayList<Integer>();
					if(execution.hasVariable("g_execIds")){
						tempIds =  (List<Integer>)execution.getVariable("g_execIds");
					}
				if(allotType.equalsIgnoreCase("parallel")){
					for(int i = 0;i < execIds.size();i++){
						tempIds.add(execIds.get(i));
					}
				} else {
					tempIds.add(execIds.get(0));
				}
					global.put("g_execIds", CoviFlowApprovalLineHelper.removeDupeFromExecIds(tempIds));
			}
			
			execution.setVariables(global);
			execution.setVariableLocal("g_sub_hasStepsFinished", "false");
			
		} catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("SubStartExcutionListener", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	
}