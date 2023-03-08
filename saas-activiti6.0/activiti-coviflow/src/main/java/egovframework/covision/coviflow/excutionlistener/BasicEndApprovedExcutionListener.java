/**
 * @Class Name : BasicEndApprovedExcutionListener.java
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
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWSHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class BasicEndApprovedExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(BasicEndApprovedExcutionListener.class);
	
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicEndApproved");
		/*
		 * Basic 프로세스 종료 처리
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

			int divisionIndex = 0;
			JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
			if(activeDivision.get("isDivision").toString().equalsIgnoreCase("true")){
				divisionIndex = Integer.parseInt(activeDivision.get("divisionIndex").toString());
			}
			
			//0. 변수 선언
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String isSecure = pDesc.get("IsSecureDoc").toString();
			String formPrefix = ctxJsonObj.get("FormPrefix").toString();
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String scRPBox = "";
			if(formInfoExt.containsKey("scRPBox")){
				scRPBox = (String)formInfoExt.get("scRPBox");
			}
			String divisionKind = CoviFlowApprovalLineHelper.getDivisionTask(apvLineObj, divisionIndex, "kind");
			String pDivisionKind = "";
			if(divisionKind.equalsIgnoreCase("send")){
				pDivisionKind = "D";
			} else if(divisionKind.equalsIgnoreCase("receive")) {
				pDivisionKind = "R";
			}
						
			String businessState = "02_01_01";
			//g_hasReceiptBox 값에 따라 분기처리 구현 필요
			int state = 528;
			
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//process update
			Map<String, Object> procUMap = new HashMap<String, Object>();
			procUMap.put("businessState", businessState);
			procUMap.put("processState", state);
			procUMap.put("processID", piid);
			procUMap.put("parentYN", "Y"); // sub 프로세스도 같이 완료처리 하기 위한 옵션
			
			processDAO.updateProcess(procUMap);
			
			//ProcessArchive Insert
			Map<String, Object> procArchMap = new HashMap<String, Object>();
			procArchMap.put("processID", piid);
			procArchMap.put("formName", formName);
			procArchMap.put("formPrefix", formPrefix);
			procArchMap.put("divisionKind", pDivisionKind);
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
 
			if(!scRPBox.equals("Y")||isSecure.equalsIgnoreCase("Y")){
				subkindList.add("R");
			}
		    
			//WorkitemArchive Insert
			Map<String, Object> procWorkitemArchMap = new HashMap<String, Object>();
			procWorkitemArchMap.put("piBusinessState", "02_01");
			procWorkitemArchMap.put("processID", piid);
			procWorkitemArchMap.put("subkindList", subkindList);
			
			processDAO.insertWorkitemArchive(procWorkitemArchMap);
			
			//결재선 처리
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, divisionIndex, CoviFlowVariables.APPV_COMP);
			apvLineObj = CoviFlowApprovalLineHelper.setDivisionTask(apvLineObj, divisionIndex, "datecompleted", sdf.format(new Date()));
			
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
			
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			//결과값 변수 처리
			execution.setVariable("g_basic_approvalResult", "completed");
			
			if(execution.hasVariable("g_isDistribution")){
				String usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				
				// 배포처 완료 시 연동옵션 추가
				String legacyInfo = "";
				if(execution.hasVariable("g_isLegacy")) {
					legacyInfo = (String)execution.getVariable("g_isLegacy");
					
					if(StringUtils.isNotBlank(legacyInfo)&&CoviFlowWorkHelper.checkLegacyInfo(legacyInfo, "DISTCOMPLETE")) {
						CoviFlowWorkHelper.callLegacy(execution, "DISTCOMPLETE", null, null, null);
					}
				}
				
				//알림 호출
				//알림 대상 - 결재단계의 모든 사람?
				JSONArray divisions = CoviFlowApprovalLineHelper.getDivisions(apvLineObj);
				JSONObject division = new JSONObject();
				for(int i = 0; i < divisions.size(); i++)
				{
					division = (JSONObject)divisions.get(i);
					
					if(division.get("processID").toString().equalsIgnoreCase(Integer.toString(piid))){
						ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getDivisionMessageReceivers(division);
						if(receivers.size() > 0){
							CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "COMPLETE", Integer.toString(piid), receivers, apvLineObj));
						}
						break;
					}
				}
			}
		} catch(Exception e){
			//txManager.rollback(status);
			
			LOG.error("BasicEndApproved", e);
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	
}