/**
 * @Class Name : CoviFlowWorkHelper.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;


public class CoviFlowWorkHelper {

	private static final Logger LOG = LoggerFactory.getLogger(CoviFlowWorkHelper.class);
	private static final String CTXXML = "activiti-custom-context.xml";
	
	final static Object lock = new Object();
	
	/*
	 * 문서번호 발번
	 * Bas_App_IssueDocNumber
	 * */
	public static void registerDocNumber(DelegateExecution execution) throws Exception{
		
		//ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			/* 1. 문서번호 발번 - covi_approval4j.jwf_documentnumber
			 * 2. forminstance update
			 * 3. ESignMeta update - 확인이 필요
			 * */
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			String pidescid = "";
			String fmpf = ctxJsonObj.get("FormPrefix").toString();
			int serialNumber = 0;
			String initiatorUnitID = "";
			String initiatorUnitName = "";
			String shortInitiatorUnitName = "";
			int docListType = 1;
			String fiscalYear = CoviFlowDateHelper.getMonth().substring(0, 4);
			String fiscalMonth = CoviFlowDateHelper.getMonth().substring(4, 6);
			String docNoType = "11";
			
			String entcode = "";
			String entname = "";
			String docNoTypeExt = "";
			String distDocNumber = "";
			String docClassName = "";
			String docClassID = "";
			String fmnm = ctxJsonObj.get("FormName").toString().split(";")[0]; // todo: 양식명 multiDisplayName 분리되어 있지 않음, 우선 한국어를 대표명으로 사용
			
			if(ctxJsonObj.containsKey("FormInfoExt")){
				JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
				
				if(formInfoExt.containsKey("docnotype")){
					docNoType = (String)formInfoExt.get("docnotype");
				}
				if(formInfoExt.containsKey("entcode")){
					entcode = (String)formInfoExt.get("entcode");
				}
				if(formInfoExt.containsKey("scDNumExt")){
					docNoTypeExt = (String)formInfoExt.get("scDNumExt");
				}
				if(formInfoExt.containsKey("scDistDocNum")) {
					distDocNumber = (String)formInfoExt.get("scDistDocNum");
				}
			}
			
			//문서분류 
			if(ctxJsonObj.containsKey("FormData")){
				JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
				docClassName = (String)formData.get("DocClassName"); // 문서분류명
				docClassID = (String)formData.get("DocClassID"); // 문서분류코드
			}
			
			// 회계기준년도 변경
			String baseFiscalMonth = CoviFlowPropHelper.getInstace().getPropertyValue("fiscalmonth");
			if(baseFiscalMonth != null && !baseFiscalMonth.isEmpty()) {
				if(Integer.parseInt(fiscalMonth) < Integer.parseInt(baseFiscalMonth)) {
					fiscalYear = String.valueOf(Integer.parseInt(fiscalYear) - 1);
				}
			}
			
			boolean isDistNow = (execution.hasVariable("g_docNumber") 
					&& execution.getVariable("g_docNumber") != null
					&& !execution.getVariable("g_docNumber").equals(""));
			
			if(isDistNow && distDocNumber.equalsIgnoreCase("Y")) { //수신처 문서번호 발번
				initiatorUnitID = CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, 1, "oucode");
				pidescid = CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, 1, "processDescID");
			} else {
				initiatorUnitID = CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, 0, "oucode");
				pidescid = CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, 0, "processDescID");
			}
			
			// 부서 DisplayName, ShortName, 회사 DisplayName DB에서 조회해 올 것(문서번호는 다국어 X)
			Map<String, Object> paramsS = new HashMap<>();
			paramsS.put("groupCode", initiatorUnitID);
			
			JSONObject nameObj = processDAO.selectGroupName(paramsS);
			initiatorUnitName = nameObj.get("GR_DisplayName").toString();
			shortInitiatorUnitName = nameObj.get("GR_ShortName").toString();
			entname = nameObj.get("EntName").toString();
			
			/*
			 * 1-부서약어:분류번호-일련번호(4)
			 * 2-부서약어-일련번호(4)
			 * 3-부서약어YYYY-일련번호(4)
			 * 4-부서약어 분류번호-일련번호(4)
			 * 5-부서약어-YYMM-일련번호
			 * 6-부서약어-YYYY-일련번호(4)
			 * 7-부서약어 YYYY - 일련번호(4)
			 * 8-회사명-일련번호(4)(회사별발번)
			 * 9-부서약어 제 YY - 일련번호(4)
			 * 10-부서코드 YY-일련번호(5)
			 * 11-회사명-YYYY-일련번호(4)
			 * 12-문서분류-YYYY-일련번호(4)
			 * 99-정규식형태
			*/
			
			//문서번호 발번형식에 따른 deptCode 재설정
			String sDocNoUnitCode = ""; //문서번호발번 기준 코드
			String regDisplayFormat = "";
			sDocNoUnitCode = initiatorUnitID;
			switch(docNoType){
				//부서약어 발번
				case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "9": case "10":
					sDocNoUnitCode = initiatorUnitID;
					break;
				//회사명 발번
				case "8": case "11": case "12":
					sDocNoUnitCode = entcode;
					break;
				case "99": //정규식형태정의
					if (docNoTypeExt.indexOf("@") != -1){
						String regdocKey = docNoTypeExt.split("@")[0];
						regDisplayFormat = docNoTypeExt.split("@")[1];
						
						if(regdocKey.indexOf(";") > -1){ //연도별 발번이 아닐 경우 키값 변경
							sDocNoUnitCode = regdocKey.split(";")[0].replaceAll("ent", entcode).replaceAll("dept", initiatorUnitID).replaceAll("fmpf", fmpf)
									+ regdocKey.split(";")[1].replaceAll("YYYYMMDD", CoviFlowDateHelper.getDate()).replaceAll("YYYYMM", CoviFlowDateHelper.getMonth());
						}else{
							sDocNoUnitCode = regdocKey.replaceAll("ent", entcode).replaceAll("dept", initiatorUnitID).replaceAll("fmpf", fmpf);
						}
					}
			}
			
			Map<String, Object> paramsC = new HashMap<>();

			paramsC.put("docListType", docListType);
			paramsC.put("fiscalYear", fiscalYear);
			paramsC.put("deptCode", sDocNoUnitCode);
			paramsC.put("deptName", initiatorUnitName);
			paramsC.put("categoryNumber", "");
			
			String displayedDocNumber = "";
			synchronized (lock) {
				serialNumber = CoviApplicationContextUtil.getApplicationContext().getBean(DocNumberService.class).getDocumentNumber(paramsC);
				
				if(docNoType.equals("99")){
					//ent;YYYYMM@entnm-YYYY-seq(4)
					String formattedSerialNumber = String.format("%0"+regDisplayFormat.split("seq")[1].replace("(", "").replace(")", "")+"d", serialNumber); //"%04d"
					String displayNumber = regDisplayFormat.split("seq")[0] + formattedSerialNumber;
					displayedDocNumber = displayNumber.replaceAll("entnm", entname).replaceAll("deptnm", initiatorUnitName).replaceAll("deptstnm", shortInitiatorUnitName).replaceAll("fmpf",fmpf).replaceAll("fmnm", fmnm).replaceAll("dcid", docClassID).replaceAll("YYYYMMDD", CoviFlowDateHelper.getDate()).replaceAll("YYYYMM", CoviFlowDateHelper.getMonth()).replaceAll("YYYY", fiscalYear).replaceAll("YYMM", CoviFlowDateHelper.getMonth().substring(2));
				}else{
					displayedDocNumber = CoviFlowDocNumberHelper.issueDocNumber(serialNumber, docNoType, initiatorUnitID, initiatorUnitName, shortInitiatorUnitName, fiscalYear, docClassID, entname);
				}
				
				// displayedNumber update
				paramsC.remove("displayedNumber");
				paramsC.put("displayedNumber", displayedDocNumber);
				
				CoviApplicationContextUtil.getApplicationContext().getBean(DocNumberService.class).updateDocumentNumber(paramsC);
			}

			//jwf_forminstance update
			Map<String, Object> paramsU = new HashMap<>();
			paramsU.put("formInstID", fiid);
			
			if(isDistNow && distDocNumber.equalsIgnoreCase("Y")) { //수신부서 발번
				String piid = (String) execution.getProcessInstanceId();
				String subFiid = (String) execution.getVariable("g_subFiid");
				paramsU.put("processID", piid);
				paramsU.put("receiveNo", displayedDocNumber);
				paramsU.put("processDescriptionID", pidescid);
				if(subFiid != null && !subFiid.equals("")) { // 다안기안 , 문서유통
					paramsU.put("formInstID", subFiid);
				}
				processDAO.updateFormInstanceForReceiveNo(paramsU);
			} else { //기안부서 발번
				paramsU.put("docNo", displayedDocNumber);				
				processDAO.updateFormInstanceForDocNo(paramsU);
				//processDAO.updateFormInstanceArchiveForDocNo(paramsU);
				
				execution.setVariable("g_docNumber", displayedDocNumber);
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	
	/*
	 * Legacy 연동 호출
	 * 기안시 연동 - DRAFT - scLegacyDraft
	 * 완료(승인) 후 연동 - COMPLETE - scLegacyComplete
	 * 완료(반려) 후 연동 - REJECT - scLegacyReject
	 * 완료(배포처) 후 연동 - DISTCOMPLETE - scLegacyDistComplete
	 * 담당부서 처리 전 연동 - CHARGEDEPT - scLegacyChargeDept
	 * 진행 중 연동 - OTHERSYSTEM - scLegacyOtherSystem
	 * 회수 후 연동 - WITHDRAW - scLegacyWithdraw
	 * 삭제 후 연동 - DELETE - scLegacyDelete
	 * */
	@SuppressWarnings("unchecked")
	public static void callLegacy(VariableScope execution, String apvMode, String approverID, String taskid, String comment) throws Exception{
		
		String piid = "";
		
		if(execution instanceof DelegateExecution){
			execution = (DelegateExecution) execution;
			piid = (String)((DelegateExecution) execution).getProcessInstanceId();
		}else if(execution instanceof DelegateTask){
			execution = (DelegateTask) execution;
			piid = (String)((DelegateTask) execution).getProcessInstanceId();
		}
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			Object apvLine = execution.getVariable("g_appvLine");
			String apvLineStr = "";
			if(apvLine instanceof LinkedHashMap){
				apvLineStr = JSONValue.toJSONString(apvLine);
			} else {
				apvLineStr = apvLine.toString();
			}
			
			//0. 변수 선언
			String fiid = (String)ctxJsonObj.get("FormInstID").toString();
			
			String formPrefix = ctxJsonObj.get("FormPrefix").toString();
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			String docNo = "";
			String bodyContext = "";
			String preApproveprocsss = "";
			String apvResult = "";
			String formHelperContext = "";
			String formNoticeContext = "";
			
			//select bodycontext
			Map<String, Object> map = new HashMap<>();
			map.put("formInstID", Integer.parseInt(fiid));
			JSONObject formInstance = processDAO.selectFormInstance(map);
			
			if(formInstance != null){
				bodyContext = (String)formInstance.get("BodyContext");
			}
			docNo = (String)execution.getVariable("g_docNumber");
			
			if(approverID == null || taskid == null || comment == null){
				JSONObject approverInfo = CoviFlowApprovalLineHelper.getLegacyApproverInfo(apvLineStr, apvMode);
				approverID = (String) approverInfo.get("approverID");
				taskid = (String) approverInfo.get("taskid");
				comment = (String) approverInfo.get("comment");
			}
			
			//차후 필요한 경우 subprocess로 대체 할 것
			//기간계연동 호출
			//value는 Base64 encoding
			JSONArray params = new JSONArray();
			JSONObject param = new JSONObject();
			param.put("formPrefix", base64Encode(formPrefix));
			param.put("bodyContext", bodyContext);
			param.put("formInfoExt", base64Encode(formInfoExt.toJSONString()));
			param.put("approvalContext", base64Encode(apvLineStr));
			param.put("preApproveprocsss", base64Encode(preApproveprocsss));
			param.put("apvResult", base64Encode(apvResult));
			param.put("docNumber", base64Encode(docNo));
			param.put("approverId", base64Encode(approverID));
			param.put("comment", base64Encode(comment));
			param.put("formInstID", base64Encode(fiid));
			param.put("apvMode", base64Encode(apvMode));
			param.put("processID", base64Encode(piid));
			param.put("formHelperContext", base64Encode(formHelperContext));
			param.put("formNoticeContext", base64Encode(formNoticeContext));
			
			params.add(param);
			CoviFlowWSHelper.invokeApi("LEGACY", params);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	/*
	 * 기안시 연동 - DRAFT - scLegacyDraft
	 * 완료(승인) 후 연동 - COMPLETE - scLegacyComplete
	 * 완료(반려) 후 연동 - REJECT - scLegacyReject
	 * 완료(배포처) 후 연동 - DISTCOMPLETE - scLegacyDistComplete
	 * 담당부서 처리 전 연동 - CHARGEDEPT - scLegacyChargeDept
	 * 진행 중 연동 - OTHERSYSTEM - scLegacyOtherSystem
	 * 회수 후 연동 - WITHDRAW - scLegacyWithdraw
	 * 삭제 후 연동 - DELETE - scLegacyDelete
	 * */
	public static Boolean checkLegacyInfo(String legacyInfo, String apvMode) throws Exception{
		Boolean bRet = false;
		
		if(StringUtils.isNotBlank(legacyInfo)){
			JSONParser parser = new JSONParser();
			JSONObject legacyObj = (JSONObject)parser.parse(legacyInfo);
			
			switch (apvMode) {
        		case "DRAFT":
	        		if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyDraft"))){
	        			bRet = true;
	        		}
	        		break;
				case "COMPLETE":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyComplete"))){
	        			bRet = true;
	        		}
					break;
				case "DISTCOMPLETE":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyDistComplete"))){
	        			bRet = true;
	        		}
					break;
				case "REJECT":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyReject"))){
	        			bRet = true;
	        		}
					break;
				case "CHARGEDEPT":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyChargeDept"))){
	        			bRet = true;
	        		}
					break;
				case "OTHERSYSTEM":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyOtherSystem"))){
	        			bRet = true;
	        		}
					break;
				case "WITHDRAW":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyWithdraw"))){
	        			bRet = true;
	        		}
					break;
				case "DELETE":
					if("Y".equalsIgnoreCase((String)legacyObj.get("scLegacyDelete"))){
	        			bRet = true;
	        		}
					break;
			}	
		}
		
		return bRet;
	}
	
	public static String base64Encode(String token) throws Exception{
		String ret = "";
		if(StringUtils.isNotBlank(token)){
			byte[] encodedBytes = Base64.encode(token.getBytes());
			ret = new String(encodedBytes, Charset.forName("UTF-8"));
		}
	    return ret;
	}


	public static String base64Decode(String token) throws Exception{
		String ret = "";
		if(StringUtils.isNotBlank(token)){
			byte[] decodedBytes = Base64.decode(token.getBytes());
			ret = new String(decodedBytes, Charset.forName("UTF-8"));
		}
	    return ret;
	}
	
	public static void deleteWorkItem(int wiid, int widescid, int pfid) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			//workitem delete 처리
			Map<String, Object> workitemMap = new HashMap<>();
			workitemMap.put("workitemID", wiid);
			processDAO.deleteOneWorkItem(workitemMap);
			
			//performer delete 처리
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("performerID", pfid);
			processDAO.deleteOnePerformer(performerMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	/*
	 * message parameter 생성
	 * */
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParam(String subject, String status, String userId, String piid, String wiid) throws Exception{
		JSONArray params = new JSONArray();
		
		JSONObject param = new JSONObject();
		param.put("subject", subject);
		param.put("userId", userId);
		param.put("status", status);
		param.put("piid", piid);
		param.put("wiid", wiid);
		
		params.add(param);
		
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
			JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", "");
			param.put("SenderID", "");
			param.put("Subject", subject);
			
			if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
			}

			if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
			}else{
				param.put("ProcessId", piid);
			}
			
			if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
			}
			
			param.put("Status", status);
			
			if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
			}
			
			if(i == 0) param.put("ApvLineObj", "{}");
			param.put("Comment", "");
			
			params.add(param);
		}
				
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers, JSONObject apvLineObj) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
			JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", "");
			param.put("SenderID", "");
			param.put("Subject", subject);
			if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
			}

			if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
			}else{
				param.put("ProcessId", piid);
			}
			
			if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
			}
			
			param.put("Status", status);
			
			if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
			}
			
			if(i == 0) param.put("ApvLineObj", apvLineObj == null? "{}" : apvLineObj);
			param.put("Comment", "");
			
			if(receivers.get(i).containsKey("deputyCode")){
				param.put("DeputyCode", (String)receivers.get(i).get("deputyCode"));	
			} else {
				param.put("DeputyCode", "");
			}
			
			params.add(param);
		}
				
		return params;
	}
	
	// 회수, 기안취소 시 의견 param 추가
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers, JSONObject apvLineObj, String comment) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
			JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", "");
			param.put("SenderID", "");
			param.put("Subject", subject);
			if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
			}

			if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
			}else{
				param.put("ProcessId", piid);
			}
			
			if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
			}
			
			param.put("Status", status);
			
			if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
			}
			
			if(i == 0) param.put("ApvLineObj", apvLineObj == null? "{}" : apvLineObj);
			param.put("Comment", comment);
			
			params.add(param);
		}
				
		return params;
	}
	
	//참조/회람 지정자
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers, ArrayList<HashMap<String, String>> senders, JSONObject apvLineObj) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
			JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", "");
			param.put("SenderID", senders.get(i).get("senderid"));
			param.put("Subject", subject);
			if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
			}

			if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
			}else{
				param.put("ProcessId", piid);
			}
			
			if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
			}
			
			param.put("Status", status);
			
			if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
			}
			
			if(i == 0) param.put("ApvLineObj", apvLineObj == null? "{}" : apvLineObj);
			param.put("Comment", "");
			
			params.add(param);
		}
				
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String approver, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
			JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", approver);
			param.put("SenderID", "");
			param.put("Subject", subject);
			
			if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
			}
			
			if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
			}else{
				param.put("ProcessId", piid);
			}
			
			if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
			}
			
			param.put("Status", status);
			
			if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
			}
			
			if(i == 0) param.put("ApvLineObj", "{}");
			param.put("Comment", "");
			
			params.add(param);
		}
				
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String approver, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers, JSONObject apvLineObj) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
			JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", approver);
			param.put("SenderID", "");
			param.put("Subject", subject);
			
			if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
			}
			
			if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
			}else{
				param.put("ProcessId", piid);
			}
			
			if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
			}

			param.put("Status", status);
			
			if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
			}
			
			if(i == 0) param.put("ApvLineObj", apvLineObj == null? "{}" : apvLineObj);
			param.put("Comment", "");
			
			params.add(param);
		}
				
		return params;
	}
	
	// 회수, 기안취소 시 의견 param 추가
	@SuppressWarnings("unchecked")
	public static JSONArray makeMessageParams(int fiid, String formName, String initiator, String approver, String subject, String status, String piid, ArrayList<HashMap<String, String>> receivers, JSONObject apvLineObj, String comment) throws Exception{
		JSONArray params = new JSONArray();
		
		for(int i = 0; i < receivers.size(); i++){
				JSONObject param = new JSONObject();
			param.put("FormInstId", Integer.toString(fiid));
			param.put("FormName", formName);
			param.put("Initiator", initiator);
			param.put("ApproveCode", approver);
			param.put("SenderID", "");
			param.put("Subject", subject);
			
				if(receivers.get(i).containsKey("userId")){
				param.put("UserId", (String)receivers.get(i).get("userId"));	
			} else {
				param.put("UserId", "");
				}
			
				if(receivers.get(i).containsKey("piid") && !receivers.get(i).get("piid").equals("")){
				param.put("ProcessId", (String)receivers.get(i).get("piid"));
				}else{
				param.put("ProcessId", piid);
				}
				
				if(receivers.get(i).containsKey("wiid")){
				param.put("WorkitemId", (String)receivers.get(i).get("wiid"));
			} else {
				param.put("WorkitemId", "");
				}
			
			param.put("Status", status);
			
				if(receivers.get(i).containsKey("type")){
				param.put("Type", (String)receivers.get(i).get("type"));	
			} else {
				param.put("Type", "");
				}
				
			if(i == 0) param.put("ApvLineObj", apvLineObj == null? "{}" : apvLineObj);
			param.put("Comment", comment);
				
				params.add(param);
		}
				
		return params;
	}
	
	public static void removeActionAll(DelegateExecution execution) throws Exception{
		Map<String, Object> m = new HashMap<>();
		m = execution.getVariables();
		for( String key : m.keySet() ){
			if(key.contains("g_action_") || key.contains("g_actioncomment_") || key.contains("g_actioncomment_attach_") || key.contains("g_signimage_")){
				execution.removeVariable(key);
			}
        }
	}
	
	public static void removeActionAll(DelegateTask delegateTask) throws Exception{
		Map<String, Object> m = new HashMap<>();
		m = delegateTask.getVariables();
		for( String key : m.keySet() ){
			if(key.contains("g_action_") || key.contains("g_actioncomment_") || key.contains("g_actioncomment_attach_") || key.contains("g_signimage_")){
				delegateTask.removeVariable(key);
			}
        }
	}
	
	/*
	 * 배포 데이터 Helper
	 * */
	@SuppressWarnings("unchecked")
	public static JSONArray setReceipts(String receiptNames, String type) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
		
		JSONArray receipts = new JSONArray();
		
		try{
			String[] receiptItems = receiptNames.split(";");
			
			for(String item: receiptItems){
				if(!item.equals("") && item.split(":").length >= 3) {				
					String[] splitItem = item.split(":");
					
					if(splitItem.length > 1) {				
						String code = splitItem[1];
						String name = splitItem[2].replace("^", ";");
						String isHasSubDept = splitItem[3];
						String ouCode = "";
						if(splitItem.length > 4) ouCode = splitItem[4];
						
						if(splitItem[0].equalsIgnoreCase(type)){
							JSONObject o = new JSONObject();
							o.put("type", type);
							o.put("code", code);
							o.put("name", name);
							o.put("oucode", ouCode);
							o.put("status", "inactive");
							
							receipts.add(o);
							
							/*
							 * 하위부서 포함 여부
							 * o.put("hasdept", splitItem[3]);
							 * Y - 하위 부서 있고, 하위부서까지 보냄, N - 하위부서 있고, 하위부서 안 보냄, X - 하위부서가 없음
							 * */
							if(type.equalsIgnoreCase("0") && isHasSubDept.equalsIgnoreCase("Y")){
								Map<String, Object> params = new HashMap<>();
								params.put("deptCode", splitItem[1]);
								JSONArray subDeptList = processDAO.selectSubDept(params);
								for(Object obj : subDeptList){
									JSONObject subDept = (JSONObject)obj;
									subDept.put("type", type);
									subDept.put("status", "inactive");
									
									receipts.add(subDept);
								}
							}
						}
					}
				}
			}
		} catch(Exception e){
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return receipts;
	}
	
	/*
	 * 배포 데이터 Helper
	 * */
	@SuppressWarnings("unchecked")
	public static JSONArray setShareReceipts(String receiptNames, boolean bPerson, String entCode) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
		
		JSONArray receipts = new JSONArray();
		
		try{
			String[] receiptItems = receiptNames.split(";");
			
			for(String item: receiptItems){
				String[] splitItem = item.split(":");
				
				if(splitItem.length > 1) {
					String code = splitItem[1];
					
					if(splitItem[0].equalsIgnoreCase("2")){
						if(bPerson) { // 공용 배포처에 저장된 사용자 찾기
			                JSONArray returnedPerson = new JSONArray();
							Map<String, Object> params = new HashMap<>();
							params.put("groupCode", code);
							params.put("entCode", entCode);
							
							returnedPerson = processDAO.selectSharedPerson(params);	
							
							String SharedPersonCode, SharedPersonName;
							if(returnedPerson != null){
								for(int i = 0; i < returnedPerson.size(); i++) {
									SharedPersonCode = ((JSONObject)returnedPerson.get(i)).get("USERCODE").toString();
									SharedPersonName = ((JSONObject)returnedPerson.get(i)).get("DISPLAYNAME").toString();
									
									// 중복체크
									if(receiptNames.indexOf(SharedPersonCode) < 0) {
										JSONObject o = new JSONObject();
										o.put("type", "1");
										o.put("code", SharedPersonCode);
										o.put("name", SharedPersonName);
										o.put("status", "inactive");
										
										receipts.add(o);
									}
								}
							}
						}
						else { // 공용 배포처에 저장된 부서 찾기
							JSONArray returnedOU = new JSONArray();
							Map<String, Object> params = new HashMap<>();
							params.put("groupCode", code);
							params.put("entCode", entCode);
							
							returnedOU = processDAO.selectSharedOU(params);	
							
							String SharedOUCode, SharedOUName;
							if(returnedOU != null){
								for(int i = 0; i < returnedOU.size(); i++) {
									SharedOUCode = ((JSONObject)returnedOU.get(i)).get("GROUPCODE").toString();
									SharedOUName = ((JSONObject)returnedOU.get(i)).get("DISPLAYNAME").toString();
						
									// 중복체크
									if(receiptNames.indexOf(SharedOUCode) < 0) {								
										JSONObject o = new JSONObject();
										o.put("type", "0");
										o.put("code", SharedOUCode);
										o.put("name", SharedOUName);
										o.put("status", "inactive");
										
										receipts.add(o);
									}
								}
							}
						}
					}
				}
			}
		} catch(Exception e){
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return receipts;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPendingReceipt(JSONArray receipts) throws Exception{
		JSONObject ret = new JSONObject();
		ret.put("receipt", null);
		ret.put("isReceipt", "false");
		
		for(int j = 0; j < receipts.size(); j++)
		{
			JSONObject ro = (JSONObject)receipts.get(j);
			if(ro.get("status").toString().equalsIgnoreCase("pending")){
				ret.put("receipt", ro);
				ret.put("isReceipt", "true");
				break;
			}
			
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray changePendingReceiptStatus(JSONArray receipts){
		
		for(Integer j = 0; j < receipts.size(); j++)
		{
			JSONObject ro = (JSONObject)receipts.get(j);
			if(ro.get("status").toString().equalsIgnoreCase("pending")){
				ro.put("status", "completed");
								
				//마지막 row가 아니면
				if(!j.equals(receipts.size()-1)){
					JSONObject nextRo = (JSONObject)receipts.get(j+1);
					nextRo.put("status", "pending");
				}
				
				break;
			}
		}
		
		return receipts;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject addReceiptDivision(JSONObject apvLineObj, JSONObject receipt, String type) throws Exception {
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		JSONObject ret = new JSONObject();
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String code = receipt.get("code").toString();
			String name = receipt.get("name").toString();
			
			JSONObject root = (JSONObject)apvLineObj.get("steps");
			JSONArray divisions = new JSONArray();
			divisions.add((JSONObject)root.get("division"));
			
			JSONObject taskinfo = new JSONObject();
			taskinfo.put("status", "pending");
			taskinfo.put("result", "pending");
			taskinfo.put("kind", "normal");
			taskinfo.put("datereceived", sdf.format(new Date()));
			
			JSONObject ou = new JSONObject();
			
			JSONObject step = new JSONObject();
			step.put("unittype", "ou");
			step.put("routetype", "receive");
			
			JSONObject addingDivision = new JSONObject();
			addingDivision.put("divisiontype", "receive");

			if(type.equalsIgnoreCase("0")){
				//부서
				ou.put("code", code);
				ou.put("name", name);
				ou.put("taskinfo", taskinfo);
				
				step.put("unittype", "ou");
				step.put("routetype", "receive");
				step.put("ou", ou);
				step.put("name", "수신");
				
				addingDivision.put("name", "수신");
				addingDivision.put("oucode", code);
				addingDivision.put("ouname", name);
				addingDivision.put("step", step);
				
			} else{ 
				//사람
				//get user 정보
				Map<String, Object> params = new HashMap<>();
				params.put("urCode", code);
				JSONArray personInfos = new JSONArray();
				JSONObject personInfo = new JSONObject();
				
				personInfos = processDAO.selectReceiptPersonInfo(params);
				personInfo = (JSONObject)personInfos.get(0);
				
				String oucode = personInfo.get("DeptID").toString();
				String ouname = personInfo.get("DeptName").toString();
				String level = (personInfo.containsKey("JobLevel") && personInfo.get("JobLevel") != null) ? personInfo.get("JobLevel").toString() : "";
				String position = (personInfo.containsKey("JobTitle") && personInfo.get("JobTitle") != null) ? personInfo.get("JobTitle").toString() : "";
				String title = (personInfo.containsKey("JobPosition") && personInfo.get("JobPosition") != null) ? personInfo.get("JobPosition").toString() : "";
								
				taskinfo.put("kind", "charge");
				
				JSONObject person = new JSONObject();
				person.put("code", code);
				person.put("name", name);
				person.put("oucode", oucode);
				person.put("ouname", ouname);
				person.put("taskinfo", taskinfo);
				
				person.put("level", level);
				person.put("position", position);
				person.put("title", title);
				
				ou.put("code", oucode);
				ou.put("name", ouname);
				ou.put("taskinfo", taskinfo);
				ou.put("person", person);

				step.put("unittype", "person");
				step.put("routetype", "approve");
				step.put("ou", ou);
				step.put("name", "담당결재");
				
				addingDivision.put("name", "담당결재");
				addingDivision.put("oucode", oucode);
				addingDivision.put("ouname", ouname);
				addingDivision.put("step", step);
			}
			
			JSONObject division_taskinfo = new JSONObject();
			division_taskinfo.put("status", "pending");
			division_taskinfo.put("result", "pending");
			division_taskinfo.put("kind", "receive");
			division_taskinfo.put("datereceived", sdf.format(new Date()));
			
			addingDivision.put("taskinfo", division_taskinfo);
			
			divisions.add(addingDivision);
			
			root.put("division", divisions);
			apvLineObj.put("steps", root);
			
			ret = apvLineObj;
			
		} catch(Exception e){
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	/*
	 * 1. workitem과 workitem desc, performer insert
	 * 2. workitem update (performerid)
	 * 3. process desc update
	 * domain data 변경은 향후 문제 되면
	 * 
	 * param subkind에 대해서는 추가 검토가 필요함(subkind값을 결재선 값으로 찾아 낼 순 없는가?)
	 * */
	@SuppressWarnings("unchecked")
	public static JSONObject setActivity(String taskName, DelegateTask delegateTask, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String approverCode = (String)pendingObject.get("approverCode");
			String approverName = (String)pendingObject.get("approverName");
			String approverSIPAddress = (String)pendingObject.get("approverSIPAddress"); //SIP값
			String approvalStep = (String)pendingObject.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			int state = 288;
			if(taskName.equalsIgnoreCase("initiator") || taskName.equalsIgnoreCase("bypass") || taskName.equalsIgnoreCase("sharer")){
				state = 528;
			}
						
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			for(int j = 0; j < ouArray.size(); j++)
			{
				deputyCode = "";
				deputyName = "";
				
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				JSONObject pendingOu = (JSONObject)ouArray.get(j);
				
				JSONObject taskObject = new JSONObject();
				if(pendingOu.containsKey("person")){
					Object personObj = pendingOu.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							personObject = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)personObject.get("taskinfo");
						}
					}
					
					//개인배포의 경우 ou로 들어오는 문제점이 있음
					approverCode = personObject.get("code").toString();
					approverName = personObject.get("name").toString();
					//알림대상 add
					receiver.put("userId", approverCode);
					receiver.put("type", "UR");
					
					//대결자가 지정되어 있는가?
					JSONObject returnedDeputy = new JSONObject();
					Map<String, Object> params = new HashMap<>();
					params.put("urCode", personObject.get("code").toString());
					params.put("today", sdf.format(new Date()));
					
					returnedDeputy = processDAO.selectDeputy(params);	
					
					if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
						deputyCode = returnedDeputy.get("DeputyCode").toString();
						deputyName = returnedDeputy.get("DeputyName").toString();
						
						receiver.put("deputyCode", deputyCode);
					}
					
				} else {
					taskObject = (JSONObject)pendingOu.get("taskinfo");
				}
				
				//수신시각입력
				taskObject.put("datereceived", sdf.format(new Date()));
				
				if(pendingOu.containsKey("taskid")){
					//performer의 subkind 변경(예고자에서 결재자로)
					Map<String, Object> performerUMap = new HashMap<>();
					performerUMap.put("subkind", subkind);
					performerUMap.put("workItemID", pendingOu.get("wiid").toString());
					
					processDAO.updatePerformer(performerUMap);
					
					//workitem의 taskid, name, state 변경
					Map<String, Object> workitemUCMap = new HashMap<>();
					workitemUCMap.put("name", routeType);
					workitemUCMap.put("taskID", delegateTask.getId());
					workitemUCMap.put("state", state);
					workitemUCMap.put("created", sdf.format(new Date()));
					workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
					
					if(!taskName.equalsIgnoreCase("initiator")){
						workitemUCMap.put("deputyID", deputyCode);
						workitemUCMap.put("deputyName", deputyName);	
					}
					
					processDAO.updateWorkItemForChange(workitemUCMap);	
					
					//추가 데이터
					pendingOu.put("taskid", delegateTask.getId());
					
					//알림대상 add
					receiver.put("wiid", pendingOu.get("wiid").toString());
					
				} else {
					// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
					int workItemDescID = 0;
					
					//1-2. workitem insert
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", delegateTask.getId());
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", workItemDescID);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", approverCode);
					workItemMap.put("userName", approverName);
					workItemMap.put("deputyID", deputyCode);
					workItemMap.put("deputyName", deputyName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					if(taskName.equalsIgnoreCase("initiator")){
						workItemMap.put("finished", sdf.format(new Date()));	
					} else {
						workItemMap.put("finished", null);	
					}
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", approverCode);
					performerMap.put("userName", approverName);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					//결재선 추가 데이타
					pendingOu.put("taskid", delegateTask.getId());
					pendingOu.put("wiid", Integer.toString(wiid));
					pendingOu.put("widescid", Integer.toString(workItemDescID));
					pendingOu.put("pfid", Integer.toString(pfid));
					
					//알림대상 add
					receiver.put("wiid", Integer.toString(wiid));
				}
				
				//알림 add
				receivers.add(receiver);
			}
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//4. 결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//도착 알림 처리
			if(!taskName.equalsIgnoreCase("initiator")&&(receivers.size() > 0)){
				String msgStatus = "APPROVAL";
				if(taskName.equalsIgnoreCase("Charge")){
					msgStatus = "CHARGE";
				}
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivity(String taskName, DelegateTask delegateTask, String ctxJsonStr, String subkind, JSONObject apvLineObj) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			JSONObject pendingNextStep = new JSONObject();
			// 결재선을 parsing
			String approverCode = (String)pendingObject.get("approverCode");
			String approverName = (String)pendingObject.get("approverName");
			String approverSIPAddress = (String)pendingObject.get("approverSIPAddress"); //SIP값
			String approvalStep = (String)pendingObject.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			int stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
			
			boolean isByPassDeputy = false;
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			int state = 288;
			if(taskName.equalsIgnoreCase("initiator")){
				state = 528;
			}
						
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			//수신자 대결 기안
			if(taskName.equalsIgnoreCase("initiator") && stepSize-1 > stepIndex ){
				if(CoviFlowApprovalLineHelper.getOuTask(apvLineObj, divisionIndex , stepIndex + 1, "kind").equals("bypass")) {
					isByPassDeputy = true;
					pendingNextStep = CoviFlowApprovalLineHelper.getOuIndex(apvLineObj, divisionIndex , stepIndex + 1);
				}
			}
			
			for(int j = 0; j < ouArray.size(); j++)
			{
				deputyCode = "";
				deputyName = "";
				
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				JSONObject pendingOu = (JSONObject)ouArray.get(j);
				
				JSONObject taskObject = new JSONObject();
				if(pendingOu.containsKey("person")){
					Object personObj = pendingOu.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							personObject = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)personObject.get("taskinfo");
						}
					}
					
					//개인배포의 경우 ou로 들어오는 문제점이 있음

					if(!isByPassDeputy) {
						approverCode = personObject.get("code").toString();
						approverName = personObject.get("name").toString();
					}else {
						//수신자 대결 기안
						approverCode = ((JSONObject)((JSONObject)pendingNextStep.get("ou")).get("person")).get("code").toString();
						approverName = ((JSONObject)((JSONObject)pendingNextStep.get("ou")).get("person")).get("name").toString();
					}
					//알림대상 add
					receiver.put("userId", approverCode);
					receiver.put("type", "UR");
					
					//대결자가 지정되어 있는가?
					JSONObject returnedDeputy = new JSONObject();
					Map<String, Object> params = new HashMap<>();
					
					if(!isByPassDeputy) {
						params.put("urCode", personObject.get("code").toString());
						params.put("today", sdf.format(new Date()));
					}else {
						//수신자 대결 기안
						params.put("urCode", ((JSONObject)((JSONObject)pendingNextStep.get("ou")).get("person")).get("code").toString());
						params.put("today", sdf.format(new Date()));
					}
					
					returnedDeputy = processDAO.selectDeputy(params);	
					
					if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
						deputyCode = returnedDeputy.get("DeputyCode").toString();
						deputyName = returnedDeputy.get("DeputyName").toString();
						
						receiver.put("deputyCode", deputyCode);
					}
					
				} else {
					taskObject = (JSONObject)pendingOu.get("taskinfo");
				}
				
				//수신시각입력
				taskObject.put("datereceived", sdf.format(new Date()));
				
				if(pendingOu.containsKey("taskid")){
					//performer의 subkind 변경(예고자에서 결재자로)
					Map<String, Object> performerUMap = new HashMap<>();
					performerUMap.put("subkind", subkind);
					performerUMap.put("workItemID", pendingOu.get("wiid").toString());
					
					processDAO.updatePerformer(performerUMap);
					
					//workitem의 taskid, name, state 변경
					Map<String, Object> workitemUCMap = new HashMap<>();
					workitemUCMap.put("name", routeType);
					workitemUCMap.put("taskID", delegateTask.getId());
					workitemUCMap.put("state", state);
					workitemUCMap.put("created", sdf.format(new Date()));
					workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
					
					if(!taskName.equalsIgnoreCase("initiator")){
						workitemUCMap.put("deputyID", deputyCode);
						workitemUCMap.put("deputyName", deputyName);	
					}
					
					processDAO.updateWorkItemForChange(workitemUCMap);	
					
					//추가 데이터
					pendingOu.put("taskid", delegateTask.getId());
					
					//알림대상 add
					receiver.put("wiid", pendingOu.get("wiid").toString());
					
				} else {
					// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
					int workItemDescID = 0;
					
					//1-2. workitem insert
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", delegateTask.getId());
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", workItemDescID);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", approverCode);
					workItemMap.put("userName", approverName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					if(taskName.equalsIgnoreCase("initiator") && !isByPassDeputy){
						workItemMap.put("finished", sdf.format(new Date()));
						workItemMap.put("deputyID", "");
						workItemMap.put("deputyName", "");
					}else if(taskName.equalsIgnoreCase("initiator") && isByPassDeputy){
						//수신자 대결 기안
						workItemMap.put("finished", sdf.format(new Date()));
						workItemMap.put("deputyID", deputyCode);
						workItemMap.put("deputyName", deputyName);
					}else {
						workItemMap.put("finished", null);	
						workItemMap.put("deputyID", deputyCode);
						workItemMap.put("deputyName", deputyName);
					}
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", approverCode);
					performerMap.put("userName", approverName);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					//결재선 추가 데이타
					pendingOu.put("taskid", delegateTask.getId());
					pendingOu.put("wiid", Integer.toString(wiid));
					pendingOu.put("widescid", Integer.toString(workItemDescID));
					pendingOu.put("pfid", Integer.toString(pfid));
					
					//알림대상 add
					receiver.put("wiid", Integer.toString(wiid));
				}
				
				//알림 add
				receivers.add(receiver);
			}
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//도착 알림 처리
			if(!taskName.equalsIgnoreCase("initiator")&&(receivers.size() > 0)){
				String msgStatus = "APPROVAL";
				if(taskName.equalsIgnoreCase("Charge")){
					msgStatus = "CHARGE";
				}
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivity(String taskName, DelegateExecution execution, String ctxJsonStr, int piid, String taskId, int state, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String approverCode = (String)pendingObject.get("approverCode");
			String approverName = (String)pendingObject.get("approverName");
			String approverSIPAddress = (String)pendingObject.get("approverSIPAddress"); //SIP값
			String approvalStep = (String)pendingObject.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			for(int j = 0; j < ouArray.size(); j++)
			{
				deputyCode = "";
				deputyName = "";
				
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				JSONObject pendingOu = (JSONObject)ouArray.get(j);
				
				JSONObject taskObject = new JSONObject();
				if(pendingOu.containsKey("person")){
					Object personObj = pendingOu.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							personObject = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)personObject.get("taskinfo");
						}
					}
					
					//개인배포의 경우 ou로 들어오는 문제점이 있음
					approverCode = personObject.get("code").toString();
					approverName = personObject.get("name").toString();
					//알림대상 add
					receiver.put("userId", approverCode);
					receiver.put("type", "UR");
					
					//대결자가 지정되어 있는가?
					JSONObject returnedDeputy = new JSONObject();
					Map<String, Object> params = new HashMap<>();
					params.put("urCode", personObject.get("code").toString());
					params.put("today", sdf.format(new Date()));
					
					returnedDeputy = processDAO.selectDeputy(params);	
					
					if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
						deputyCode = returnedDeputy.get("DeputyCode").toString();
						deputyName = returnedDeputy.get("DeputyName").toString();
						
						receiver.put("deputyCode", deputyCode);
					}
					
				} else {
					taskObject = (JSONObject)pendingOu.get("taskinfo");
				}
				
				//수신시각입력
				taskObject.put("datereceived", sdf.format(new Date()));
				
				// 담당자(Charge) 지정 혹은 수신부서 1인결재인 경우는 무조건 새로운 workitem & performer 생성 (else문)
				if(pendingOu.containsKey("taskid") 
						&& !(taskName.equalsIgnoreCase("CHARGE") 
								|| (taskName.equalsIgnoreCase("APPROVE") && stepSize <= 1))){
					//performer의 subkind 변경(예고자에서 결재자로)
					Map<String, Object> performerUMap = new HashMap<>();
					performerUMap.put("subkind", subkind);
					performerUMap.put("workItemID", pendingOu.get("wiid").toString());
					
					processDAO.updatePerformer(performerUMap);	
					
					//workitem의 taskid, name, state 변경
					Map<String, Object> workitemUCMap = new HashMap<>();
					workitemUCMap.put("name", routeType);
					workitemUCMap.put("taskID", taskId);
					workitemUCMap.put("state", state);
					workitemUCMap.put("created", sdf.format(new Date()));
					workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
					
					if(!taskName.equalsIgnoreCase("initiator")){
						workitemUCMap.put("deputyID", deputyCode);
						workitemUCMap.put("deputyName", deputyName);	
					}
					
					processDAO.updateWorkItemForChange(workitemUCMap);	
					
					//추가 데이터
					pendingOu.put("taskid", taskId);
					//알림대상 add
					receiver.put("wiid", pendingOu.get("wiid").toString());
					
				} else {
					// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
					int workItemDescID = 0;
					
					//1-2. workitem insert
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", taskId);
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", workItemDescID);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", approverCode);
					workItemMap.put("userName", approverName);
					workItemMap.put("deputyID", deputyCode);
					workItemMap.put("deputyName", deputyName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					workItemMap.put("finished", null);
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", approverCode);
					performerMap.put("userName", approverName);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					//결재선 추가 데이타
					pendingOu.put("taskid", taskId);
					pendingOu.put("wiid", Integer.toString(wiid));
					pendingOu.put("widescid", Integer.toString(workItemDescID));
					pendingOu.put("pfid", Integer.toString(pfid));
					
					//알림대상 add
					receiver.put("wiid", Integer.toString(wiid));
				}
				
				//알림대상 add
				receivers.add(receiver);
			}
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//4. 결재선 update
			if(execution.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			//도착 알림 처리
			if(!subkind.equalsIgnoreCase("T006") && !taskName.equalsIgnoreCase("CHARGE")&&(receivers.size() > 0)){
				String msgStatus = "CHARGE";
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("setActivity", e);
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivity(String taskName, DelegateExecution execution, String ctxJsonStr, int piid, String taskId, int state, String subkind, JSONObject apvLineObj) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String approverCode = (String)pendingObject.get("approverCode");
			String approverName = (String)pendingObject.get("approverName");
			String approverSIPAddress = (String)pendingObject.get("approverSIPAddress"); //SIP값
			String approvalStep = (String)pendingObject.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			for(int j = 0; j < ouArray.size(); j++)
			{
				deputyCode = "";
				deputyName = "";
				
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				JSONObject pendingOu = (JSONObject)ouArray.get(j);
				
				JSONObject taskObject = new JSONObject();
				if(pendingOu.containsKey("person")){
					Object personObj = pendingOu.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							personObject = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)personObject.get("taskinfo");
						}
					}
					
					//개인배포의 경우 ou로 들어오는 문제점이 있음
					approverCode = personObject.get("code").toString();
					approverName = personObject.get("name").toString();
					//알림대상 add
					receiver.put("userId", approverCode);
					receiver.put("type", "UR");
					
					//대결자가 지정되어 있는가?
					JSONObject returnedDeputy = new JSONObject();
					Map<String, Object> params = new HashMap<>();
					params.put("urCode", personObject.get("code").toString());
					params.put("today", sdf.format(new Date()));
					
					returnedDeputy = processDAO.selectDeputy(params);	
					
					if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
						deputyCode = returnedDeputy.get("DeputyCode").toString();
						deputyName = returnedDeputy.get("DeputyName").toString();
						
						receiver.put("deputyCode", deputyCode);
					}
					
				} else {
					taskObject = (JSONObject)pendingOu.get("taskinfo");
				}
				
				//수신시각입력
				taskObject.put("datereceived", sdf.format(new Date()));
				
				if(pendingOu.containsKey("taskid")){
					//performer의 subkind 변경(예고자에서 결재자로)
					Map<String, Object> performerUMap = new HashMap<>();
					performerUMap.put("subkind", subkind);
					performerUMap.put("workItemID", pendingOu.get("wiid").toString());
					
					processDAO.updatePerformer(performerUMap);	
					
					//workitem의 taskid, name, state 변경
					Map<String, Object> workitemUCMap = new HashMap<>();
					workitemUCMap.put("name", routeType);
					workitemUCMap.put("taskID", taskId);
					workitemUCMap.put("state", state);
					workitemUCMap.put("created", sdf.format(new Date()));
					workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
					
					if(!taskName.equalsIgnoreCase("initiator")){
						workitemUCMap.put("deputyID", deputyCode);
						workitemUCMap.put("deputyName", deputyName);	
					}
					
					processDAO.updateWorkItemForChange(workitemUCMap);	
					
					//추가 데이터
					pendingOu.put("taskid", taskId);
					//알림대상 add
					receiver.put("wiid", pendingOu.get("wiid").toString());
					
				} else {
					// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
					int workItemDescID = 0;
					
					//1-2. workitem insert
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", taskId);
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", workItemDescID);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", approverCode);
					workItemMap.put("userName", approverName);
					workItemMap.put("deputyID", deputyCode);
					workItemMap.put("deputyName", deputyName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					workItemMap.put("finished", null);
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", approverCode);
					performerMap.put("userName", approverName);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					//결재선 추가 데이타
					pendingOu.put("taskid", taskId);
					pendingOu.put("wiid", Integer.toString(wiid));
					pendingOu.put("widescid", Integer.toString(workItemDescID));
					pendingOu.put("pfid", Integer.toString(pfid));
					
					//알림대상 add
					receiver.put("wiid", Integer.toString(wiid));
				}
				
				//알림대상 add
				receivers.add(receiver);
			}
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			if(execution.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			//도착 알림 처리
			if(!subkind.equalsIgnoreCase("T006") && !taskName.equalsIgnoreCase("CHARGE")&&(receivers.size() > 0)){
				String msgStatus = "CHARGE";
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivityForSubProcess(String taskName, DelegateTask delegateTask, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			String execId = delegateTask.getExecutionId();//(String)delegateTask.getVariable("g_execid");
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			// 결재선을 parsing
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
			JSONObject person = (JSONObject)pendingPerson.get("person");
			String approverCode = (String)person.get("code");
			String approverName = (String)person.get("name");
			String approverSIPAddress = "";
			if(pendingPerson.containsKey("sipaddress")){
				approverSIPAddress = (String)person.get("sipaddress"); //SIP값
			}
			String approvalStep = (String)pendingPerson.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "pdescid"));
			//알림대상
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			HashMap<String,String> receiver = new HashMap<>();
			receiver.put("userId", approverCode);
			receiver.put("type", "UR");
			
			int state = 288;
			if(taskName.equalsIgnoreCase("initiator")){
				state = 528;
			}
				
			//대결자가 지정되어 있는가?
			JSONObject returnedDeputy = new JSONObject();
			Map<String, Object> params = new HashMap<>();
			params.put("urCode", person.get("code").toString());
			params.put("today", sdf.format(new Date()));
			
			returnedDeputy = processDAO.selectDeputy(params);	
			
			if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
				deputyCode = returnedDeputy.get("DeputyCode").toString();
				deputyName = returnedDeputy.get("DeputyName").toString();

				receiver.put("deputyCode", deputyCode);
			}
			
			JSONObject taskObject = (JSONObject)person.get("taskinfo");
				
			//수신시각입력
			taskObject.put("datereceived", sdf.format(new Date()));
			
			if(person.containsKey("taskid")){
				//performer의 subkind 변경(예고자에서 결재자로)
				Map<String, Object> performerUMap = new HashMap<>();
				performerUMap.put("subkind", subkind);
				performerUMap.put("workItemID", person.get("wiid").toString());
				
				processDAO.updatePerformer(performerUMap);	
				
				//workitem의 taskid, name, state 변경
				Map<String, Object> workitemUCMap = new HashMap<>();
				workitemUCMap.put("name", routeType);
				workitemUCMap.put("taskID", delegateTask.getId());
				workitemUCMap.put("state", state);
				workitemUCMap.put("created", sdf.format(new Date()));
				workitemUCMap.put("workItemID", person.get("wiid").toString());
				
				if(!taskName.equalsIgnoreCase("initiator")){
					workitemUCMap.put("deputyID", deputyCode);
					workitemUCMap.put("deputyName", deputyName);	
				}
				
				processDAO.updateWorkItemForChange(workitemUCMap);	
				
				//추가 데이터
				person.put("taskid", delegateTask.getId().toString());
				//알림대상 add
				receiver.put("wiid", person.get("wiid").toString());
				
			} else {
				// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
				int workItemDescID = 0;
				
				//1-2. workitem insert
				Map<String, Object> workItemMap = new HashMap<>();
				workItemMap.put("taskID", delegateTask.getId());
				workItemMap.put("processID", Integer.parseInt(execId));
				workItemMap.put("performerID", 0);
				workItemMap.put("workItemDescriptionID", workItemDescID);
				workItemMap.put("name", taskName);
				workItemMap.put("dscr", "");
				workItemMap.put("priority", "");
				workItemMap.put("actualKind", "");
				workItemMap.put("userCode", approverCode);
				workItemMap.put("userName", approverName);
				workItemMap.put("deputyID", deputyCode);
				workItemMap.put("deputyName", deputyName);
				workItemMap.put("state", state);
				workItemMap.put("created", sdf.format(new Date()));
				workItemMap.put("finishRequested", null);
				if(taskName.equalsIgnoreCase("initiator")){
					workItemMap.put("finished", sdf.format(new Date()));	
				} else {
					workItemMap.put("finished", null);	
				}
				workItemMap.put("limit", null);
				workItemMap.put("lastRepeated", null);
				workItemMap.put("finalized", null);
				workItemMap.put("deleted", null);
				workItemMap.put("inlineSubProcess", "");
				workItemMap.put("charge", "");
				workItemMap.put("businessData1", "");
				workItemMap.put("businessData2", "");
				workItemMap.put("businessData3", "");
				workItemMap.put("businessData4", "");
				workItemMap.put("businessData5", "");
				workItemMap.put("businessData6", "");
				workItemMap.put("businessData7", "");
				workItemMap.put("businessData8", "");
				workItemMap.put("businessData9", "");
				workItemMap.put("businessData10", "");
				
				processDAO.insertWorkItem(workItemMap);
				wiid = (int)workItemMap.get("WorkItemID");
				
				//1-3. performer insert
				Map<String, Object> performerMap = new HashMap<>();
				performerMap.put("workitemID", wiid);
				performerMap.put("allotKey", "");
				performerMap.put("userCode", approverCode);
				performerMap.put("userName", approverName);
				performerMap.put("actualKind", "0");
				performerMap.put("state", "1");
				performerMap.put("subKind", subkind);
				
				processDAO.insertPerformer(performerMap);
				pfid = (int)performerMap.get("PerformerID");
				
				//2. workitem update
				Map<String, Object> workitemUMap = new HashMap<>();
				workitemUMap.put("performerID", pfid);
				workitemUMap.put("workItemID", wiid);
				
				processDAO.updateWorkItem(workitemUMap);
				
				//결재선 추가 데이타
				person.put("taskid", delegateTask.getId());
				person.put("wiid", Integer.toString(wiid));
				person.put("widescid", Integer.toString(workItemDescID));
				person.put("pfid", Integer.toString(pfid));
				
				//알림대상 add
				receiver.put("wiid", Integer.toString(wiid));
				
			}
			//알림대상 add
			receivers.add(receiver);
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);

			//4. 결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			delegateTask.setVariable("g_sub_appvLine", apvLineObj.toJSONString());
			//도착 알림 처리
			if(!taskName.equalsIgnoreCase("initiator")&&(receivers.size() > 0)){
				String msgStatus = "APPROVAL";
				if(taskName.equalsIgnoreCase("Charge")){
					msgStatus = "CHARGE";
				}
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, execId, receivers, apvLineObj));
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivityForSubProcess(String taskName, DelegateExecution execution, String ctxJsonStr, String subkind, String taskId) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			String execId = execution.getId();//(String)delegateTask.getVariable("g_execid");
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			// 결재선을 parsing
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
			JSONObject person = (JSONObject)pendingPerson.get("person");
			String approverCode = (String)person.get("code");
			String approverName = (String)person.get("name");
			String approverSIPAddress = "";
			if(pendingPerson.containsKey("sipaddress")){
				approverSIPAddress = (String)person.get("sipaddress"); //SIP값
			}
			String approvalStep = (String)pendingPerson.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "pdescid"));
			//알림대상
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			HashMap<String,String> receiver = new HashMap<>();
			receiver.put("userId", approverCode);
			receiver.put("type", "UR");
			
			int state = 288;
			if(taskName.equalsIgnoreCase("initiator")){
				state = 528;
			}
				
			//대결자가 지정되어 있는가?
			JSONObject returnedDeputy = new JSONObject();
			Map<String, Object> params = new HashMap<>();
			params.put("urCode", person.get("code").toString());
			params.put("today", sdf.format(new Date()));
			
			returnedDeputy = processDAO.selectDeputy(params);	
			
			if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
				deputyCode = returnedDeputy.get("DeputyCode").toString();
				deputyName = returnedDeputy.get("DeputyName").toString();
				
				receiver.put("deputyCode", deputyCode);
			}
			
			JSONObject taskObject = (JSONObject)person.get("taskinfo");
				
			//수신시각입력
			taskObject.put("datereceived", sdf.format(new Date()));
			
			if(person.containsKey("taskid")){
				//performer의 subkind 변경(예고자에서 결재자로)
				Map<String, Object> performerUMap = new HashMap<>();
				performerUMap.put("subkind", subkind);
				performerUMap.put("workItemID", person.get("wiid").toString());
				
				processDAO.updatePerformer(performerUMap);	
				
				//workitem의 taskid, name, state 변경
				Map<String, Object> workitemUCMap = new HashMap<>();
				workitemUCMap.put("name", routeType);
				workitemUCMap.put("taskID", taskId);
				workitemUCMap.put("state", state);
				workitemUCMap.put("created", sdf.format(new Date()));
				workitemUCMap.put("workItemID", person.get("wiid").toString());
				
				if(!taskName.equalsIgnoreCase("initiator")){
					workitemUCMap.put("deputyID", deputyCode);
					workitemUCMap.put("deputyName", deputyName);	
				}
				
				processDAO.updateWorkItemForChange(workitemUCMap);	
				
				//추가 데이터
				person.put("taskid", taskId);
				//알림대상 add
				receiver.put("wiid", person.get("wiid").toString());
				
			} else {				
				// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
				int workItemDescID = 0;

				//1-2. workitem insert
				Map<String, Object> workItemMap = new HashMap<>();
				workItemMap.put("taskID", taskId);
				workItemMap.put("processID", Integer.parseInt(execId));
				workItemMap.put("performerID", 0);
				workItemMap.put("workItemDescriptionID", workItemDescID);
				workItemMap.put("name", taskName);
				workItemMap.put("dscr", "");
				workItemMap.put("priority", "");
				workItemMap.put("actualKind", "");
				workItemMap.put("userCode", approverCode);
				workItemMap.put("userName", approverName);
				workItemMap.put("deputyID", deputyCode);
				workItemMap.put("deputyName", deputyName);
				workItemMap.put("state", state);
				workItemMap.put("created", sdf.format(new Date()));
				workItemMap.put("finishRequested", null);
				if(taskName.equalsIgnoreCase("initiator")){
					workItemMap.put("finished", sdf.format(new Date()));	
				} else {
					workItemMap.put("finished", null);	
				}
				workItemMap.put("limit", null);
				workItemMap.put("lastRepeated", null);
				workItemMap.put("finalized", null);
				workItemMap.put("deleted", null);
				workItemMap.put("inlineSubProcess", "");
				workItemMap.put("charge", "");
				workItemMap.put("businessData1", "");
				workItemMap.put("businessData2", "");
				workItemMap.put("businessData3", "");
				workItemMap.put("businessData4", "");
				workItemMap.put("businessData5", "");
				workItemMap.put("businessData6", "");
				workItemMap.put("businessData7", "");
				workItemMap.put("businessData8", "");
				workItemMap.put("businessData9", "");
				workItemMap.put("businessData10", "");
				
				processDAO.insertWorkItem(workItemMap);
				wiid = (int)workItemMap.get("WorkItemID");
				
				//1-3. performer insert
				Map<String, Object> performerMap = new HashMap<>();
				performerMap.put("workitemID", wiid);
				performerMap.put("allotKey", "");
				performerMap.put("userCode", approverCode);
				performerMap.put("userName", approverName);
				performerMap.put("actualKind", "0");
				performerMap.put("state", "1");
				performerMap.put("subKind", subkind);
				
				processDAO.insertPerformer(performerMap);
				pfid = (int)performerMap.get("PerformerID");
				
				//2. workitem update
				Map<String, Object> workitemUMap = new HashMap<>();
				workitemUMap.put("performerID", pfid);
				workitemUMap.put("workItemID", wiid);
				
				processDAO.updateWorkItem(workitemUMap);
				
				//결재선 추가 데이타
				person.put("taskid", taskId);
				person.put("wiid", Integer.toString(wiid));
				person.put("widescid", Integer.toString(workItemDescID));
				person.put("pfid", Integer.toString(pfid));
				
				//알림대상 add
				receiver.put("wiid", Integer.toString(wiid));
				
			}
			//알림대상 add
			receivers.add(receiver);
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//4. 결재선 update
			if(execution.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			execution.setVariable("g_sub_appvLine", apvLineObj.toJSONString());
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivityForSubProcessOU(String taskName, DelegateTask delegateTask, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			String execId = delegateTask.getExecutionId();//(String)delegateTask.getVariable("g_execid");
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			// 결재선을 parsing
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "pdescid"));
			
			String userCode = (String)pendingOu.get("code");
			String userName = (String)pendingOu.get("name");
			
			int state = 288;
			if(taskName.equalsIgnoreCase("initiator")){
				state = 528;
			}
			
			JSONObject taskObject = (JSONObject)pendingOu.get("taskinfo");
				
			//수신시각입력
			taskObject.put("datereceived", sdf.format(new Date()));
						
			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", delegateTask.getId());
			workItemMap.put("processID", Integer.parseInt(execId));
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", userCode);
			workItemMap.put("userName", userName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", userCode);
			performerMap.put("userName", userName);
			performerMap.put("actualKind", "0");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", userCode);
			pdescUMap.put("approverName", userName);
			pdescUMap.put("approvalStep", "1_1");
			pdescUMap.put("approverSIPAddress", "");
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//4. 결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			delegateTask.setVariable("g_sub_appvLine", apvLineObj.toJSONString());
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setActivityForSubProcess(String taskName, DelegateExecution execution, String ctxJsonStr, String subkind, int taskId, int state) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			String execId = execution.getId().toString();//(String)delegateTask.getVariable("g_execid");
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			// 결재선을 parsing
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
			JSONObject person = (JSONObject)pendingPerson.get("person");
			String approverCode = (String)person.get("code");
			String approverName = (String)person.get("name");
			String approverSIPAddress = "";
			if(pendingPerson.containsKey("sipaddress")){
				approverSIPAddress = (String)person.get("sipaddress"); //SIP값
			}
			String approvalStep = (String)pendingPerson.get("approvalStep");
			String deputyCode = "";
			String deputyName = "";
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int stepIndex = Integer.parseInt(pendingObject.get("stepIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getOuTaskForOU(apvLineObj, divisionIndex, stepIndex, execId, "pdescid"));
			//알림대상
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			HashMap<String,String> receiver = new HashMap<>();
			receiver.put("userId", approverCode);
			receiver.put("type", "UR");
				
			//대결자가 지정되어 있는가?
			JSONObject returnedDeputy = new JSONObject();
			Map<String, Object> params = new HashMap<>();
			params.put("urCode", person.get("code").toString());
			params.put("today", sdf.format(new Date()));
			
			returnedDeputy = processDAO.selectDeputy(params);	
			
			if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
				deputyCode = returnedDeputy.get("DeputyCode").toString();
				deputyName = returnedDeputy.get("DeputyName").toString();
				
				receiver.put("deputyCode", deputyCode);
			}
			
			JSONObject taskObject = (JSONObject)person.get("taskinfo");
				
			//수신시각입력
			taskObject.put("datereceived", sdf.format(new Date()));
			
			if(person.containsKey("taskid")){
				//performer의 subkind 변경(예고자에서 결재자로)
				Map<String, Object> performerUMap = new HashMap<>();
				performerUMap.put("subkind", subkind);
				performerUMap.put("workItemID", person.get("wiid").toString());
				
				processDAO.updatePerformer(performerUMap);	
				
				//workitem의 taskid, name, state 변경
				Map<String, Object> workitemUCMap = new HashMap<>();
				workitemUCMap.put("name", routeType);
				workitemUCMap.put("taskID", taskId);
				workitemUCMap.put("state", state);
				workitemUCMap.put("created", sdf.format(new Date()));
				workitemUCMap.put("workItemID", person.get("wiid").toString());
				
				if(!taskName.equalsIgnoreCase("initiator")){
					workitemUCMap.put("deputyID", deputyCode);
					workitemUCMap.put("deputyName", deputyName);	
				}
				
				processDAO.updateWorkItemForChange(workitemUCMap);	
				
				//추가 데이터
				person.put("taskid", taskId);
				//알림대상 add
				receiver.put("wiid", person.get("wiid").toString());
				
			} else {				
				// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
				int workItemDescID = 0;
				
				//1-2. workitem insert
				Map<String, Object> workItemMap = new HashMap<>();
				workItemMap.put("taskID", taskId);
				workItemMap.put("processID", Integer.parseInt(execId));
				workItemMap.put("performerID", 0);
				workItemMap.put("workItemDescriptionID", workItemDescID);
				workItemMap.put("name", taskName);
				workItemMap.put("dscr", "");
				workItemMap.put("priority", "");
				workItemMap.put("actualKind", "");
				workItemMap.put("userCode", approverCode);
				workItemMap.put("userName", approverName);
				workItemMap.put("deputyID", deputyCode);
				workItemMap.put("deputyName", deputyName);
				workItemMap.put("state", state);
				workItemMap.put("created", sdf.format(new Date()));
				workItemMap.put("finishRequested", null);
				if(taskName.equalsIgnoreCase("initiator")){
					workItemMap.put("finished", sdf.format(new Date()));	
				} else {
					workItemMap.put("finished", null);	
				}
				workItemMap.put("limit", null);
				workItemMap.put("lastRepeated", null);
				workItemMap.put("finalized", null);
				workItemMap.put("deleted", null);
				workItemMap.put("inlineSubProcess", "");
				workItemMap.put("charge", "");
				workItemMap.put("businessData1", "");
				workItemMap.put("businessData2", "");
				workItemMap.put("businessData3", "");
				workItemMap.put("businessData4", "");
				workItemMap.put("businessData5", "");
				workItemMap.put("businessData6", "");
				workItemMap.put("businessData7", "");
				workItemMap.put("businessData8", "");
				workItemMap.put("businessData9", "");
				workItemMap.put("businessData10", "");
				
				processDAO.insertWorkItem(workItemMap);
				wiid = (int)workItemMap.get("WorkItemID");
				
				//1-3. performer insert
				Map<String, Object> performerMap = new HashMap<>();
				performerMap.put("workitemID", wiid);
				performerMap.put("allotKey", "");
				performerMap.put("userCode", approverCode);
				performerMap.put("userName", approverName);
				performerMap.put("actualKind", "0");
				performerMap.put("state", "1");
				performerMap.put("subKind", subkind);
				
				processDAO.insertPerformer(performerMap);
				pfid = (int)performerMap.get("PerformerID");
				
				//2. workitem update
				Map<String, Object> workitemUMap = new HashMap<>();
				workitemUMap.put("performerID", pfid);
				workitemUMap.put("workItemID", wiid);
				
				processDAO.updateWorkItem(workitemUMap);
				
				//결재선 추가 데이타
				person.put("taskid", taskId);
				person.put("wiid", Integer.toString(wiid));
				person.put("widescid", Integer.toString(workItemDescID));
				person.put("pfid", Integer.toString(pfid));
				
				//알림대상 add
				receiver.put("wiid", Integer.toString(wiid));
				
			}
			//알림대상 add
			receivers.add(receiver);
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//4. 결재선 update
			if(execution.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			execution.setVariable("g_sub_appvLine", apvLineObj.toJSONString());
			//도착 알림 처리
			if(!subkind.equalsIgnoreCase("T006")&&!taskName.equalsIgnoreCase("initiator")&&(receivers.size() > 0)){
				String msgStatus = "APPROVAL";
				if(taskName.equalsIgnoreCase("Charge")){
					msgStatus = "CHARGE";
				}
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, execId, receivers, apvLineObj));
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	// 수신공람 workitem 생성
	public static JSONObject setActivityForRE(String taskName, DelegateExecution execution, String ctxJsonStr, String approverCode, String approverName, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", execution.getId());
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", approverCode);
			workItemMap.put("userName", approverName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", "528");
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", sdf.format(new Date()));
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", approverCode);
			performerMap.put("userName", approverName);
			performerMap.put("actualKind", "0");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
						
			//txManager.commit(status);
			
			//도착 알림 처리
			/* if(!taskName.equalsIgnoreCase("initiator")&&(receivers.size() > 0)){
				String msgStatus = "APPROVAL";
				if(taskName.equalsIgnoreCase("Charge")){
					msgStatus = "CHARGE";
				}
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, apvLineObj));
			} */
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setMultiActivity(String taskName, DelegateTask delegateTask, String ctxJsonStr, String assignee, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String approverCode = (String)pendingObject.get("approverCode");
			String approverName = (String)pendingObject.get("approverName");
			String approverSIPAddress = (String)pendingObject.get("approverSIPAddress"); //SIP값
			String approvalStep = (String)pendingObject.get("approvalStep");
			String routeType = (String)pendingObject.get("routeType");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
			boolean isChangeYN = false;
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			int state = 288;
			String taskId = delegateTask.getId();
			
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			for(int j = 0; j < ouArray.size(); j++)
			{
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				JSONObject pendingOu = (JSONObject)ouArray.get(j);
				String code = "";
				String name = "";
				String sip = "";
				String deputyCode = "";
				String deputyName = "";
				
				JSONObject taskObject = new JSONObject();
				
				if(pendingOu.containsKey("person")){
					Object personObj = pendingOu.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject person = (JSONObject)persons.get(0);
					taskObject = (JSONObject)person.get("taskinfo");
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							person = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)person.get("taskinfo");
						}
					}
					
					code = (String)person.get("code");
					name = (String)person.get("name");
					if(person.containsKey("sipaddress")){
						sip = (String)person.get("sipaddress");
					}

					//대결자가 지정되어 있는가?
					JSONObject returnedDeputy = new JSONObject();
					Map<String, Object> params = new HashMap<>();
					params.put("urCode", code);
					params.put("today", sdf.format(new Date()));
					
					returnedDeputy = processDAO.selectDeputy(params);	
					
					if(returnedDeputy != null && !returnedDeputy.get("Count").toString().equalsIgnoreCase("0") && !returnedDeputy.get("DeputyOption").toString().equalsIgnoreCase("P")){
						deputyCode = returnedDeputy.get("DeputyCode").toString();
						deputyName = returnedDeputy.get("DeputyName").toString();
						
						receiver.put("deputyCode", deputyCode);
					}
					
				} else {
					code = (String)pendingOu.get("code");
					name = (String)pendingOu.get("name");
					
					taskObject = (JSONObject)pendingOu.get("taskinfo");
				}
				
				if(code.equals(assignee)){
					
					//수신 시각 입력
					taskObject.put("datereceived", sdf.format(new Date()));
					//알림대상 add
					receiver.put("userId", code);
					receiver.put("type", "UR");
					
					if(pendingOu.containsKey("taskid")){
						//performer의 subkind 변경(예고자에서 결재자로)
						Map<String, Object> performerUMap = new HashMap<>();
						performerUMap.put("subkind", subkind);
						performerUMap.put("workItemID", pendingOu.get("wiid").toString());
						
						JSONArray performers = processDAO.selectPerformerByWorkItemID(performerUMap);
						JSONObject firstPerformer = (JSONObject)performers.get(0);
						if(firstPerformer.get("SubKind").equals("T010") && !isChangeYN) {
							processDAO.updatePerformer(performerUMap);	
							
							//workitem의 taskid, name, state 변경
							Map<String, Object> workitemUCMap = new HashMap<>();
							workitemUCMap.put("name", routeType);
							workitemUCMap.put("taskID", taskId);
							workitemUCMap.put("state", state);
							workitemUCMap.put("created", sdf.format(new Date()));
							workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
							
							if(!taskName.equalsIgnoreCase("initiator")){
								workitemUCMap.put("deputyID", deputyCode);
								workitemUCMap.put("deputyName", deputyName);	
							}
							
							processDAO.updateWorkItemForChange(workitemUCMap);	
							
							pendingOu.put("taskid", taskId);
							//알림대상 add
							receiver.put("wiid", pendingOu.get("wiid").toString());
							isChangeYN = true;
						}
					} else {		
						if(!isChangeYN) {
							// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
							int workItemDescID = 0;
							
							//1-2. workitem insert
							Map<String, Object> workItemMap = new HashMap<>();
							workItemMap.put("taskID", taskId);
							workItemMap.put("processID", piid);
							workItemMap.put("performerID", 0);
							workItemMap.put("workItemDescriptionID", workItemDescID);
							workItemMap.put("name", taskName);
							workItemMap.put("dscr", "");
							workItemMap.put("priority", "");
							workItemMap.put("actualKind", "");
							workItemMap.put("userCode", code);
							workItemMap.put("userName", name);
							workItemMap.put("deputyID", deputyCode);
							workItemMap.put("deputyName", deputyName);
							workItemMap.put("state", state);
							workItemMap.put("created", sdf.format(new Date()));
							workItemMap.put("finishRequested", null);
							workItemMap.put("finished", null);
							workItemMap.put("limit", null);
							workItemMap.put("lastRepeated", null);
							workItemMap.put("finalized", null);
							workItemMap.put("deleted", null);
							workItemMap.put("inlineSubProcess", "");
							workItemMap.put("charge", "");
							workItemMap.put("businessData1", "");
							workItemMap.put("businessData2", "");
							workItemMap.put("businessData3", "");
							workItemMap.put("businessData4", "");
							workItemMap.put("businessData5", "");
							workItemMap.put("businessData6", "");
							workItemMap.put("businessData7", "");
							workItemMap.put("businessData8", "");
							workItemMap.put("businessData9", "");
							workItemMap.put("businessData10", "");
							
							processDAO.insertWorkItem(workItemMap);
							wiid = (int)workItemMap.get("WorkItemID");
							
							//1-3. performer insert
							Map<String, Object> performerMap = new HashMap<>();
							performerMap.put("workitemID", wiid);
							performerMap.put("allotKey", "");
							performerMap.put("userCode", code);
							performerMap.put("userName", name);
							performerMap.put("actualKind", "0");
							performerMap.put("state", "1");
							performerMap.put("subKind", subkind);
							
							processDAO.insertPerformer(performerMap);
							pfid = (int)performerMap.get("PerformerID");
							
							//2. workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("performerID", pfid);
							workitemUMap.put("workItemID", wiid);
							
							processDAO.updateWorkItem(workitemUMap);
							
							pendingOu.put("taskid", taskId);
							pendingOu.put("wiid", Integer.toString(wiid));
							pendingOu.put("widescid", Integer.toString(workItemDescID));
							pendingOu.put("pfid", Integer.toString(pfid));
							//알림대상 add
							receiver.put("wiid", Integer.toString(wiid));
							isChangeYN = true;
						}
					}
					//break; //필요할 경우 주석을 제거 할 것
					
					//알림대상 add
					if(receiver.get("wiid") != null) {
						receivers.add(receiver);
					}
				}
				
			}
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			//알림 호출
			if(!receivers.isEmpty()){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "APPROVAL", Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("setActivity", e);
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setMultiActivityOu(String taskName, DelegateTask delegateTask, String ctxJsonStr, String assignee, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getExecutionId());
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String approverCode = (String)pendingObject.get("approverCode");
			String approverName = (String)pendingObject.get("approverName");
			String approverSIPAddress = (String)pendingObject.get("approverSIPAddress"); //SIP값
			String approvalStep = (String)pendingObject.get("approvalStep");
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
			int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			int state = 288;
			String taskId = delegateTask.getId();
			
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			//알림대상
			HashMap<String,String> receiver = new HashMap<>();
			for(int j = 0; j < ouArray.size(); j++)
			{
				JSONObject pendingOu = (JSONObject)ouArray.get(j);
				String code = "";
				String name = "";
				String deputyCode = "";
				String deputyName = "";
				
				JSONObject taskObject = new JSONObject();
				code = (String)pendingOu.get("code");
				name = (String)pendingOu.get("name");
				taskObject = (JSONObject)pendingOu.get("taskinfo");
				
				if(code.equals(assignee)){
					
					//수신 시각 입력
					taskObject.put("datereceived", sdf.format(new Date()));
					
					//1-2. workitem insert
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", taskId);
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", 0);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", code);
					workItemMap.put("userName", name);
					workItemMap.put("deputyID", deputyCode);
					workItemMap.put("deputyName", deputyName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					workItemMap.put("finished", null);
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					//알림대상 add
					receiver.put("userId", code);
					receiver.put("type", "GR");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", code);
					performerMap.put("userName", name);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					pendingOu.put("taskid", taskId);
					pendingOu.put("wiid", Integer.toString(wiid));
					pendingOu.put("widescid", Integer.toString(0));
					pendingOu.put("pfid", Integer.toString(pfid));
					
					//알림대상 add
					receiver.put("wiid", pendingOu.get("wiid").toString());
					receivers.add(receiver);
					break;
				}
			}
			
			//3. process desc update
			Map<String, Object> pdescUMap = new HashMap<>();
			pdescUMap.put("approverCode", approverCode);
			pdescUMap.put("approverName", approverName);
			pdescUMap.put("approvalStep", approvalStep);
			pdescUMap.put("approverSIPAddress", approverSIPAddress);
			pdescUMap.put("processDescriptionID", pidescid);
			
			processDAO.updateProcessDescription(pdescUMap);
			
			//결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			//알림 호출
			if(!receivers.isEmpty()){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "APPROVAL", Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("setActivity", e);
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setMultiActivityReview(String taskName, DelegateTask delegateTask, String ctxJsonStr, String assignee, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 후결 가져옴
			JSONArray stepObject = CoviFlowApprovalLineHelper.getReviewerStep(apvLineObj);
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			int state = 288;
			String taskId = delegateTask.getId();
			
			for(int j = 0; j < stepObject.size(); j++)
			{
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				JSONObject pendingOu = (JSONObject)((JSONObject)stepObject.get(j)).get("ou");
				String code = "";
				String name = "";
				String sip = "";
				String deputyCode = "";
				String deputyName = "";
				
				JSONObject taskObject = new JSONObject();
				
				if(pendingOu.containsKey("person")){
					Object personObj = pendingOu.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject person = (JSONObject)persons.get(0);
					taskObject = (JSONObject)person.get("taskinfo");
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							person = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)person.get("taskinfo");
						}
					}
					
					code = (String)person.get("code");
					name = (String)person.get("name");
					if(person.containsKey("sipaddress")){
						sip = (String)person.get("sipaddress");
					}
				} else {
					code = (String)pendingOu.get("code");
					name = (String)pendingOu.get("name");
					
					taskObject = (JSONObject)pendingOu.get("taskinfo");
				}
				
				if(code.equals(assignee)){
					
					//수신 시각 입력
					taskObject.put("datereceived", sdf.format(new Date()));
					//알림대상 add
					receiver.put("userId", code);
					receiver.put("type", "UR");
					
					if(pendingOu.containsKey("taskid")){
						//performer의 subkind 변경(예고자에서 결재자로)
						Map<String, Object> performerUMap = new HashMap<>();
						performerUMap.put("subkind", subkind);
						performerUMap.put("workItemID", pendingOu.get("wiid").toString());
						
						processDAO.updatePerformer(performerUMap);	
						
						//후결 상위 프로세스, workitem의 taskid, name, state 변경
						Map<String, Object> workitemUCMap = new HashMap<>();
						workitemUCMap.put("processID", piid);
						workitemUCMap.put("name", "reviewer");
						workitemUCMap.put("taskID", taskId);
						workitemUCMap.put("state", state);
						workitemUCMap.put("created", sdf.format(new Date()));
						workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
						
						processDAO.updateWorkItemForReview(workitemUCMap);	
						
						pendingOu.put("taskid", taskId);
						//알림대상 add
						receiver.put("wiid", pendingOu.get("wiid").toString());
						
					} else {
						// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
						int workItemDescID = 0;
						
						//1-2. workitem insert
						Map<String, Object> workItemMap = new HashMap<>();
						workItemMap.put("taskID", taskId);
						workItemMap.put("processID", piid);
						workItemMap.put("performerID", 0);
						workItemMap.put("workItemDescriptionID", workItemDescID);
						workItemMap.put("name", taskName);
						workItemMap.put("dscr", "");
						workItemMap.put("priority", "");
						workItemMap.put("actualKind", "");
						workItemMap.put("userCode", code);
						workItemMap.put("userName", name);
						workItemMap.put("deputyID", deputyCode);
						workItemMap.put("deputyName", deputyName);
						workItemMap.put("state", state);
						workItemMap.put("created", sdf.format(new Date()));
						workItemMap.put("finishRequested", null);
						workItemMap.put("finished", null);
						workItemMap.put("limit", null);
						workItemMap.put("lastRepeated", null);
						workItemMap.put("finalized", null);
						workItemMap.put("deleted", null);
						workItemMap.put("inlineSubProcess", "");
						workItemMap.put("charge", "");
						workItemMap.put("businessData1", "");
						workItemMap.put("businessData2", "");
						workItemMap.put("businessData3", "");
						workItemMap.put("businessData4", "");
						workItemMap.put("businessData5", "");
						workItemMap.put("businessData6", "");
						workItemMap.put("businessData7", "");
						workItemMap.put("businessData8", "");
						workItemMap.put("businessData9", "");
						workItemMap.put("businessData10", "");
						
						processDAO.insertWorkItem(workItemMap);
						wiid = (int)workItemMap.get("WorkItemID");
						
						//1-3. performer insert
						Map<String, Object> performerMap = new HashMap<>();
						performerMap.put("workitemID", wiid);
						performerMap.put("allotKey", "");
						performerMap.put("userCode", code);
						performerMap.put("userName", name);
						performerMap.put("actualKind", "0");
						performerMap.put("state", "1");
						performerMap.put("subKind", subkind);
						
						processDAO.insertPerformer(performerMap);
						pfid = (int)performerMap.get("PerformerID");
						
						//2. workitem update
						Map<String, Object> workitemUMap = new HashMap<>();
						workitemUMap.put("performerID", pfid);
						workitemUMap.put("workItemID", wiid);
						
						processDAO.updateWorkItem(workitemUMap);
						
						pendingOu.put("taskid", taskId);
						pendingOu.put("wiid", Integer.toString(wiid));
						pendingOu.put("widescid", Integer.toString(workItemDescID));
						pendingOu.put("pfid", Integer.toString(pfid));
						//알림대상 add
						receiver.put("wiid", Integer.toString(wiid));
					}
					//break; //필요할 경우 주석을 제거 할 것
					
					//알림대상 add
					receivers.add(receiver);
					
				}
				
			}
			
			//결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			//알림 호출
			if(!receivers.isEmpty()){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "APPROVAL", Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("setActivity", e);
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	/*
	 * 부서완료함, 발신함 처리
	 * 결재선이 존재하지 않는 유형
	 * */
	public static void setListActivity(String taskName, int piid, String taskId, String ctxJsonStr, String ouCode, String ouName, String subkind, int state){
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
						
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", taskId);
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", ouCode);
			workItemMap.put("userName", ouName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", ouCode);
			performerMap.put("userName", ouName);
			performerMap.put("actualKind", "1");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			LOG.error("setListActivity", e);	
			//txManager.rollback(status);
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	/*
	 * return jsonobject - wiid, pfid, taskid, widescid
	 * */
	@SuppressWarnings("unchecked")
	public static JSONObject setListActivity(String taskName, int piid, DelegateTask delegateTask, int divisionIndex, String subkind, int state){
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			String taskId = delegateTask.getId();
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");			
			String usid = (String)ctxJsonObj.get("usid");
			
			//다안기안 시 각 안 별 제목(g_subSubject)를 배포 시 넘겨주기 때문에 아래와 같이 처리
			if(delegateTask.hasVariable("g_subSubject")) {
				formSubject = (String)delegateTask.getVariable("g_subSubject");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			//알림대상
			HashMap<String,String> receiver = new HashMap<>();
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject root = (JSONObject)apvLineObj.get("steps");
			
			Object divisionObj = root.get("division");
			JSONArray divisions = new JSONArray();
			if(divisionObj instanceof JSONObject){
				divisions.add(divisionObj);
			} else {
				divisions = (JSONArray)divisionObj;
			}
			
			JSONObject division = (JSONObject)divisions.get(divisionIndex);
			String ouCode = (String)division.get("oucode");
			String ouName = (String)division.get("ouname");

			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", taskId);
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", ouCode);
			workItemMap.put("userName", ouName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", ouCode);
			performerMap.put("userName", ouName);
			performerMap.put("actualKind", "1");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			ret.put("taskid", taskId);
			ret.put("wiid", Integer.toString(wiid));
			ret.put("widescid", Integer.toString(workItemDescID));
			ret.put("pfid", Integer.toString(pfid));
			
			//txManager.commit(status);
			
			receiver.put("userId", ouCode);
			receiver.put("type", "GR");
			
			//알림대상 add
			receiver.put("wiid", Integer.toString(wiid));
			receivers.add(receiver);
			
			//알림 호출
			if(receivers.size() > 0){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "REDRAFT", Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			LOG.error("setListActivity", e);	
			//txManager.rollback(status);
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}

	/*
	 * return jsonobject - wiid, pfid, taskid, widescid
	 * */
	@SuppressWarnings("unchecked")
	public static JSONObject setListActivity(String taskName, int piid, DelegateExecution execution, int divisionIndex, String subkind, int state){
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			String taskId = execution.getId();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject root = (JSONObject)apvLineObj.get("steps");
			
			Object divisionObj = root.get("division");
			JSONArray divisions = new JSONArray();
			if(divisionObj instanceof JSONObject){
				divisions.add(divisionObj);
			} else {
				divisions = (JSONArray)divisionObj;
			}
			
			JSONObject division = (JSONObject)divisions.get(divisionIndex);
			String ouCode = (String)division.get("oucode");
			String ouName = (String)division.get("ouname");
			
			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", taskId);
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", ouCode);
			workItemMap.put("userName", ouName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", ouCode);
			performerMap.put("userName", ouName);
			performerMap.put("actualKind", "1");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			ret.put("taskid", taskId);
			ret.put("wiid", Integer.toString(wiid));
			ret.put("widescid", Integer.toString(workItemDescID));
			ret.put("pfid", Integer.toString(pfid));
			
			//txManager.commit(status);
		} catch(Exception e){
			LOG.error("setListActivity", e);	
			//txManager.rollback(status);
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	/*
	 * return apvLineObj
	 * */
	@SuppressWarnings("unchecked")
	public static JSONObject setListActivity(String taskName, DelegateTask delegateTask, String jfId, String jfName, String actualKind, String subkind, int state){
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			String taskId = delegateTask.getId();
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			HashMap<String,String> receiver = new HashMap<>();
			//알림대상 add
			receiver.put("userId", jfId);
			receiver.put("type", "JF");
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			
			//json array, json object 분기
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			//담당부서는 1개만 있다고 가정함.
			JSONObject pendingOu = (JSONObject)ouArray.get(0);
						
			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", taskId);
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", jfId);
			workItemMap.put("userName", jfName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", jfId);
			performerMap.put("userName", jfName);
			performerMap.put("actualKind", actualKind);
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			pendingOu.put("taskid", taskId);
			pendingOu.put("wiid", Integer.toString(wiid));
			pendingOu.put("widescid", Integer.toString(workItemDescID));
			pendingOu.put("pfid", Integer.toString(pfid));
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			//알림 대상
			receiver.put("wiid", Integer.toString(wiid));
			receivers.add(receiver);
			
			//알림 호출
			if(!receivers.isEmpty()){
				String msgStatus = "APPROVAL";
				if(taskName.equalsIgnoreCase("ChargeJob")){
					msgStatus = "CHARGEJOB";
				}
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			LOG.error("setListActivity", e);	
			//txManager.rollback(status);
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
		
	}
		
	@SuppressWarnings("unchecked")
	public static JSONObject setListActivity(JSONObject pendingStep, String taskName, int piid, DelegateTask delegateTask, String subkind, int state){
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);

			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			String taskId = delegateTask.getId();
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");		
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			//알림대상
			HashMap<String,String> receiver = new HashMap<>();
			JSONObject pendingOU = (JSONObject)pendingStep.get("ou");
			String ouCode = (String)pendingOU.get("code");
			String ouName = (String)pendingOU.get("name");
			String usid = (String)ctxJsonObj.get("usid");			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", taskId);
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", 0);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", ouCode);
			workItemMap.put("userName", ouName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", null);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", ouCode);
			performerMap.put("userName", ouName);
			performerMap.put("actualKind", "1");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			ret.put("taskid", taskId);
			ret.put("wiid", Integer.toString(wiid));
			ret.put("widescid", Integer.toString(0));
			ret.put("pfid", Integer.toString(pfid));
			
			receiver.put("userId", ouCode);
			receiver.put("type", "GR");
			
			//알림대상 add
			receiver.put("wiid", Integer.toString(wiid));
			receivers.add(receiver);
			
			//알림 호출
			if(receivers.size() > 0){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "REDRAFT", Integer.toString(piid), receivers, apvLineObj));
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			LOG.error("setListActivity", e);	
			//txManager.rollback(status);
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
		
	/*
	 * 1. workitem과 workitem desc, performer insert
	 * 2. workitem update (performerid)
	 * 
	 * param subkind에 대해서는 추가 검토가 필요함(subkind값을 결재선 값으로 찾아 낼 순 없는가?)
	 * */
	@SuppressWarnings("unchecked")
	public static JSONObject setPreviewActivity(String taskName, DelegateTask delegateTask, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<String> receivers = new ArrayList<>();
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending 다음 단계를 모두 가져 옴
			JSONArray inactiveObjects = CoviFlowApprovalLineHelper.getInactiveStep(apvLineObj);
			for(int i = 0; i < inactiveObjects.size(); i++)
			{
				//0. 변수 선언
				int wiid = 0;
				int workItemDescID = 0; // jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
				int pfid = 0;
								
				JSONObject inactiveObj = (JSONObject)inactiveObjects.get(i);
				String approverCode = "";
				String approverName = "";
				String approverSIPAddress = ""; //SIP값
				String deputyCode = "";
				String deputyName = "";
				
				//json array, json object 분기
				Object ouObj = inactiveObj.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONObject){
					JSONObject ouObject = (JSONObject)ouObj;
					ouArray.add(ouObject);
				} else {
					ouArray = (JSONArray)ouObj;
				}
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					deputyCode = "";
					deputyName = "";
					
					JSONObject ou = (JSONObject)ouArray.get(j);
					JSONObject taskObject = new JSONObject();
					
					if(ou.containsKey("person")){
						Object personObj = ou.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
						//전달 처리
						if(taskObject.containsKey("kind")){
							if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
								personObject = (JSONObject)persons.get(persons.size()-1);
								taskObject = (JSONObject)personObject.get("taskinfo");
							}
						}
						
						approverCode = (String)personObject.get("code");
						approverName = (String)personObject.get("name");
						if(personObject.containsKey("sipaddress")){
							approverSIPAddress = (String)personObject.get("sipaddress");
						}
						
						//알림대상 add
						receivers.add(approverCode);
					} else {
						approverCode = (String)ou.get("code");
						approverName = (String)ou.get("name");
						
						taskObject = (JSONObject)ou.get("taskinfo");
					}
					
					//1-2. workitem insert
					int state = 528;
					
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", delegateTask.getId());
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", workItemDescID);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", approverCode);
					workItemMap.put("userName", approverName);
					workItemMap.put("deputyID", deputyCode);
					workItemMap.put("deputyName", deputyName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					workItemMap.put("finished", null);
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", approverCode);
					performerMap.put("userName", approverName);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					ou.put("taskid", delegateTask.getId());
					ou.put("wiid", Integer.toString(wiid));
					ou.put("widescid", Integer.toString(workItemDescID));
					ou.put("pfid", Integer.toString(pfid));
				}
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	/*
	 * 1. workitem과 workitem desc, performer insert
	 * 2. workitem update (performerid)
	 * 
	 * param subkind에 대해서는 추가 검토가 필요함(subkind값을 결재선 값으로 찾아 낼 순 없는가?)
	 * */
	@SuppressWarnings("unchecked")
	public static JSONObject setPreviewActivity(String taskName, DelegateExecution execution, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			//0. 변수 선언
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<String> receivers = new ArrayList<>();
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending 다음 단계를 모두 가져 옴
			JSONArray inactiveObjects = CoviFlowApprovalLineHelper.getInactiveStep(apvLineObj);
			for(int i = 0; i < inactiveObjects.size(); i++)
			{
				//0. 변수 선언
				int wiid = 0;
				int workItemDescID = 0; // jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
				int pfid = 0;
								
				JSONObject inactiveObj = (JSONObject)inactiveObjects.get(i);
				String approverCode = "";
				String approverName = "";
				String approverSIPAddress = ""; //SIP값
				String deputyCode = "";
				String deputyName = "";
				
				//json array, json object 분기
				Object ouObj = inactiveObj.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONObject){
					JSONObject ouObject = (JSONObject)ouObj;
					ouArray.add(ouObject);
				} else {
					ouArray = (JSONArray)ouObj;
				}
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					deputyCode = "";
					deputyName = "";
					
					JSONObject ou = (JSONObject)ouArray.get(j);
					JSONObject taskObject = new JSONObject();
					
					if(ou.containsKey("person")){
						Object personObj = ou.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
						//전달 처리
						if(taskObject.containsKey("kind")){
							if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
								personObject = (JSONObject)persons.get(persons.size()-1);
								taskObject = (JSONObject)personObject.get("taskinfo");
							}
						}
						
						approverCode = (String)personObject.get("code");
						approverName = (String)personObject.get("name");
						if(personObject.containsKey("sipaddress")){
							approverSIPAddress = (String)personObject.get("sipaddress");
						}
						
						//알림대상 add
						receivers.add(approverCode);
					} else {
						approverCode = (String)ou.get("code");
						approverName = (String)ou.get("name");
						//approverSIPAddress = (String)ouObject.get("sipaddress");
						
						taskObject = (JSONObject)ou.get("taskinfo");
					}
										
					//1-2. workitem insert
					int state = 528;
					
					Map<String, Object> workItemMap = new HashMap<>();
					workItemMap.put("taskID", "");
					workItemMap.put("processID", piid);
					workItemMap.put("performerID", 0);
					workItemMap.put("workItemDescriptionID", workItemDescID);
					workItemMap.put("name", taskName);
					workItemMap.put("dscr", "");
					workItemMap.put("priority", "");
					workItemMap.put("actualKind", "");
					workItemMap.put("userCode", approverCode);
					workItemMap.put("userName", approverName);
					workItemMap.put("deputyID", deputyCode);
					workItemMap.put("deputyName", deputyName);
					workItemMap.put("state", state);
					workItemMap.put("created", sdf.format(new Date()));
					workItemMap.put("finishRequested", null);
					workItemMap.put("finished", null);
					workItemMap.put("limit", null);
					workItemMap.put("lastRepeated", null);
					workItemMap.put("finalized", null);
					workItemMap.put("deleted", null);
					workItemMap.put("inlineSubProcess", "");
					workItemMap.put("charge", "");
					workItemMap.put("businessData1", "");
					workItemMap.put("businessData2", "");
					workItemMap.put("businessData3", "");
					workItemMap.put("businessData4", "");
					workItemMap.put("businessData5", "");
					workItemMap.put("businessData6", "");
					workItemMap.put("businessData7", "");
					workItemMap.put("businessData8", "");
					workItemMap.put("businessData9", "");
					workItemMap.put("businessData10", "");
					
					processDAO.insertWorkItem(workItemMap);
					wiid = (int)workItemMap.get("WorkItemID");
					
					//1-3. performer insert
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("workitemID", wiid);
					performerMap.put("allotKey", "");
					performerMap.put("userCode", approverCode);
					performerMap.put("userName", approverName);
					performerMap.put("actualKind", "0");
					performerMap.put("state", "1");
					performerMap.put("subKind", subkind);
					
					processDAO.insertPerformer(performerMap);
					pfid = (int)performerMap.get("PerformerID");
					
					//2. workitem update
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("performerID", pfid);
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItem(workitemUMap);
					
					ou.put("taskid", "");
					ou.put("wiid", Integer.toString(wiid));
					ou.put("widescid", Integer.toString(workItemDescID));
					ou.put("pfid", Integer.toString(pfid));
				}
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject resetPreviewActivity(String taskName, DelegateExecution execution, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			//0. 변수 선언
			int piid = Integer.parseInt(execution.getProcessInstanceId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<String> receivers = new ArrayList<>();
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending 다음 단계를 모두 가져 옴
			JSONArray inactiveObjects = CoviFlowApprovalLineHelper.getInactiveStep(apvLineObj);
			
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			Object pendingO = pendingStep.get("ou");
			JSONArray ous = new JSONArray();
			if(pendingO instanceof JSONObject){
				JSONObject ouJsonObj = (JSONObject)pendingO;
				ous.add(ouJsonObj);
			} else {
				ous = (JSONArray)pendingO;
			}
			
			ArrayList<String> ouWiids = new ArrayList<String>();
			
			if(ous != null) {
				for(int i = 0; i < ous.size(); i++)
				{
					JSONObject pendingOu = (JSONObject)ous.get(i);
					
					if(pendingOu.containsKey("wiid")){
						ouWiids.add(pendingOu.get("wiid").toString());
					}
				}
			}
			
			//piid에 해당하는 workitem을 모두 가져옴
			JSONArray previewObjects = new JSONArray();
			Map<String, Object> previewParam = new HashMap<>();
			previewParam.put("subkind", "T010");
			previewParam.put("processID", piid);
			previewObjects = processDAO.selectPerformer(previewParam);
			
			JSONArray modifiedApprovers = new JSONArray();
			
			for(int i = 0; i < inactiveObjects.size(); i++)
			{
				//0. 변수 선언
				int wiid = 0;
				int workItemDescID = 0; // jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
				int pfid = 0;
								
				JSONObject inactiveObj = (JSONObject)inactiveObjects.get(i);
				String approverCode = "";
				String approverName = "";
				String approverSIPAddress = ""; //SIP값
				String deputyCode = "";
				String deputyName = "";
				
				//json array, json object 분기
				Object ouObj = inactiveObj.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONObject){
					JSONObject ouObject = (JSONObject)ouObj;
					ouArray.add(ouObject);
				} else {
					ouArray = (JSONArray)ouObj;
				}
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					deputyCode = "";
					deputyName = "";
					
					JSONObject ou = (JSONObject)ouArray.get(j);
					JSONObject taskObject = new JSONObject();
					
					if(ou.containsKey("person")){
						Object personObj = ou.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
						//전달 처리
						if(taskObject.containsKey("kind")&&taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							personObject = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)personObject.get("taskinfo");
						}
						
						approverCode = (String)personObject.get("code");
						approverName = (String)personObject.get("name");
						if(personObject.containsKey("sipaddress")){
							approverSIPAddress = (String)personObject.get("sipaddress");
						}
						
						//알림대상 add
						receivers.add(approverCode);
						
					} else {
						approverCode = (String)ou.get("code");
						approverName = (String)ou.get("name");
						
						taskObject = (JSONObject)ou.get("taskinfo");
					}
					
					int _wiid = 0;
					if(ou.containsKey("wiid")){
						_wiid = Integer.parseInt(ou.get("wiid").toString());
					}
					JSONObject modifiedApprover = new JSONObject();
					modifiedApprover.put("UserCode", approverCode);
					modifiedApprover.put("WorkItemID", _wiid);
					modifiedApprovers.add(modifiedApprover);
					
					if(!checkExistInJsonArray(previewObjects, modifiedApprover)){						
						//1-2. workitem insert
						int state = 528;
						
						Map<String, Object> workItemMap = new HashMap<>();
						workItemMap.put("taskID", "");
						workItemMap.put("processID", piid);
						workItemMap.put("performerID", 0);
						workItemMap.put("workItemDescriptionID", workItemDescID);
						workItemMap.put("name", taskName);
						workItemMap.put("dscr", "");
						workItemMap.put("priority", "");
						workItemMap.put("actualKind", "");
						workItemMap.put("userCode", approverCode);
						workItemMap.put("userName", approverName);
						workItemMap.put("deputyID", deputyCode);
						workItemMap.put("deputyName", deputyName);
						workItemMap.put("state", state);
						workItemMap.put("created", sdf.format(new Date()));
						workItemMap.put("finishRequested", null);
						workItemMap.put("finished", null);
						workItemMap.put("limit", null);
						workItemMap.put("lastRepeated", null);
						workItemMap.put("finalized", null);
						workItemMap.put("deleted", null);
						workItemMap.put("inlineSubProcess", "");
						workItemMap.put("charge", "");
						workItemMap.put("businessData1", "");
						workItemMap.put("businessData2", "");
						workItemMap.put("businessData3", "");
						workItemMap.put("businessData4", "");
						workItemMap.put("businessData5", "");
						workItemMap.put("businessData6", "");
						workItemMap.put("businessData7", "");
						workItemMap.put("businessData8", "");
						workItemMap.put("businessData9", "");
						workItemMap.put("businessData10", "");
						
						processDAO.insertWorkItem(workItemMap);
						wiid = (int)workItemMap.get("WorkItemID");
						
						//1-3. performer insert
						Map<String, Object> performerMap = new HashMap<>();
						performerMap.put("workitemID", wiid);
						performerMap.put("allotKey", "");
						performerMap.put("userCode", approverCode);
						performerMap.put("userName", approverName);
						performerMap.put("actualKind", "0");
						performerMap.put("state", "1");
						performerMap.put("subKind", subkind);
						
						processDAO.insertPerformer(performerMap);
						pfid = (int)performerMap.get("PerformerID");
						
						//2. workitem update
						Map<String, Object> workitemUMap = new HashMap<>();
						workitemUMap.put("performerID", pfid);
						workitemUMap.put("workItemID", wiid);
						
						processDAO.updateWorkItem(workitemUMap);
						
						ou.put("taskid", "");
						ou.put("wiid", Integer.toString(wiid));
						ou.put("widescid", Integer.toString(workItemDescID));
						ou.put("pfid", Integer.toString(pfid));
						
					}
					
				}
				
			}
			
			if(previewObjects != null){
				for(int i = 0; i < previewObjects.size(); i++)
				{
					JSONObject previewObj = (JSONObject)previewObjects.get(i);
					
					// 기존에 예고자가 삭제된 경우 & pending인 사용자가 아닌 경우 jwf_performer state='2(취소)' 로 변경함
					if(!checkExistInJsonArray(modifiedApprovers, previewObj) && (ous == null ? true : !ouWiids.contains(previewObj.get("WorkItemID").toString()))) {
						Map<String, Object> previewWorkitemUMap = new HashMap<>();
						previewWorkitemUMap.put("state", 512);
						previewWorkitemUMap.put("workItemID", Integer.parseInt(previewObj.get("WorkItemID").toString()));
						
						processDAO.updateWorkItemStateUsingWorkitemId(previewWorkitemUMap);
						
						previewWorkitemUMap.clear();
						previewWorkitemUMap.put("state", 2);
						previewWorkitemUMap.put("performerID", Integer.parseInt(previewObj.get("PerformerID").toString()));
						
						processDAO.updatePerformerForCharge(previewWorkitemUMap);
					}
				}
			}
						
			ret = apvLineObj;
			//txManager.commit(status);
			
			execution.setVariable("g_appvLine", apvLineObj.toJSONString());
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject resetPreviewActivity(String taskName, DelegateTask delegate, String ctxJsonStr, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			//0. 변수 선언
			int piid = Integer.parseInt(delegate.getProcessInstanceId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<String> receivers = new ArrayList<>();
			
			String apvLine = (String)delegate.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 pending 다음 단계를 모두 가져 옴
			JSONArray inactiveObjects = CoviFlowApprovalLineHelper.getInactiveStep(apvLineObj);
			
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			Object pendingO = pendingStep.get("ou");
			JSONArray ous = new JSONArray();
			if(pendingO instanceof JSONObject){
				JSONObject ouJsonObj = (JSONObject)pendingO;
				ous.add(ouJsonObj);
			} else {
				ous = (JSONArray)pendingO;
			}
			
			ArrayList<String> ouWiids = new ArrayList<String>();
			
			if(ous != null) {
				for(int i = 0; i < ous.size(); i++)
				{
					JSONObject pendingOu = (JSONObject)ous.get(i);
					
					if(pendingOu.containsKey("wiid")){
						ouWiids.add(pendingOu.get("wiid").toString());
					}
				}
			}
						
			//piid에 해당하는 workitem을 모두 가져옴
			JSONArray previewObjects = new JSONArray();
			Map<String, Object> previewParam = new HashMap<>();
			previewParam.put("subkind", "T010");
			previewParam.put("processID", piid);
			previewObjects = processDAO.selectPerformer(previewParam);
			
			JSONArray modifiedApprovers = new JSONArray();
			
			for(int i = 0; i < inactiveObjects.size(); i++)
			{
				//0. 변수 선언
				int wiid = 0;
				int workItemDescID = 0;
				int pfid = 0;
								
				JSONObject inactiveObj = (JSONObject)inactiveObjects.get(i);
				String approverCode = "";
				String approverName = "";
				String approverSIPAddress = ""; //SIP값
				String deputyCode = "";
				String deputyName = "";
				
				//json array, json object 분기
				Object ouObj = inactiveObj.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONObject){
					JSONObject ouObject = (JSONObject)ouObj;
					ouArray.add(ouObject);
				} else {
					ouArray = (JSONArray)ouObj;
				}
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					deputyCode = "";
					deputyName = "";
					
					JSONObject ou = (JSONObject)ouArray.get(j);
					JSONObject taskObject = new JSONObject();
					
					if(ou.containsKey("person")){
						Object personObj = ou.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
						//전달 처리
						if(taskObject.containsKey("kind")){
							if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
								personObject = (JSONObject)persons.get(persons.size()-1);
								taskObject = (JSONObject)personObject.get("taskinfo");
							}
						}
						
						approverCode = (String)personObject.get("code");
						approverName = (String)personObject.get("name");
						if(personObject.containsKey("sipaddress")){
							approverSIPAddress = (String)personObject.get("sipaddress");
						}
						
						//알림대상 add
						receivers.add(approverCode);						
					} else {
						approverCode = (String)ou.get("code");
						approverName = (String)ou.get("name");
						
						taskObject = (JSONObject)ou.get("taskinfo");
					}

					int _wiid = 0;
					if(ou.containsKey("wiid")){
						_wiid = Integer.parseInt(ou.get("wiid").toString());
					}
					JSONObject modifiedApprover = new JSONObject();
					modifiedApprover.put("UserCode", approverCode);
					modifiedApprover.put("WorkItemID", _wiid);
					modifiedApprovers.add(modifiedApprover);
					
					if(!checkExistInJsonArray(previewObjects, modifiedApprover)){
						//1-2. workitem insert
						int state = 528;
						
						Map<String, Object> workItemMap = new HashMap<>();
						workItemMap.put("taskID", "");
						workItemMap.put("processID", piid);
						workItemMap.put("performerID", 0);
						workItemMap.put("workItemDescriptionID", workItemDescID);
						workItemMap.put("name", taskName);
						workItemMap.put("dscr", "");
						workItemMap.put("priority", "");
						workItemMap.put("actualKind", "");
						workItemMap.put("userCode", approverCode);
						workItemMap.put("userName", approverName);
						workItemMap.put("deputyID", deputyCode);
						workItemMap.put("deputyName", deputyName);
						workItemMap.put("state", state);
						workItemMap.put("created", sdf.format(new Date()));
						workItemMap.put("finishRequested", null);
						workItemMap.put("finished", null);
						workItemMap.put("limit", null);
						workItemMap.put("lastRepeated", null);
						workItemMap.put("finalized", null);
						workItemMap.put("deleted", null);
						workItemMap.put("inlineSubProcess", "");
						workItemMap.put("charge", "");
						workItemMap.put("businessData1", "");
						workItemMap.put("businessData2", "");
						workItemMap.put("businessData3", "");
						workItemMap.put("businessData4", "");
						workItemMap.put("businessData5", "");
						workItemMap.put("businessData6", "");
						workItemMap.put("businessData7", "");
						workItemMap.put("businessData8", "");
						workItemMap.put("businessData9", "");
						workItemMap.put("businessData10", "");
						
						processDAO.insertWorkItem(workItemMap);
						wiid = (int)workItemMap.get("WorkItemID");
						
						//1-3. performer insert
						Map<String, Object> performerMap = new HashMap<>();
						performerMap.put("workitemID", wiid);
						performerMap.put("allotKey", "");
						performerMap.put("userCode", approverCode);
						performerMap.put("userName", approverName);
						performerMap.put("actualKind", "0");
						performerMap.put("state", "1");
						performerMap.put("subKind", subkind);
						
						processDAO.insertPerformer(performerMap);
						pfid = (int)performerMap.get("PerformerID");
						
						//2. workitem update
						Map<String, Object> workitemUMap = new HashMap<>();
						workitemUMap.put("performerID", pfid);
						workitemUMap.put("workItemID", wiid);
						
						processDAO.updateWorkItem(workitemUMap);
						
						ou.put("taskid", "");
						ou.put("wiid", Integer.toString(wiid));
						ou.put("widescid", Integer.toString(workItemDescID));
						ou.put("pfid", Integer.toString(pfid));
						
					}
					
				}
				
			}
			
			if(previewObjects != null){
				for(int i = 0; i < previewObjects.size(); i++)
				{
					JSONObject previewObj = (JSONObject)previewObjects.get(i);
					
					// 기존에 예고자가 삭제된 경우 jwf_performer state='2(취소)' 로 변경함
					if(!checkExistInJsonArray(modifiedApprovers, previewObj) && (ous == null ? true : !ouWiids.contains(previewObj.get("WorkItemID").toString()))) {
						Map<String, Object> previewWorkitemUMap = new HashMap<>();
						previewWorkitemUMap.put("state", 512);
						previewWorkitemUMap.put("workItemID", Integer.parseInt(previewObj.get("WorkItemID").toString()));
						
						processDAO.updateWorkItemStateUsingWorkitemId(previewWorkitemUMap);
						
						previewWorkitemUMap.clear();
						previewWorkitemUMap.put("state", 2);
						previewWorkitemUMap.put("performerID", Integer.parseInt(previewObj.get("PerformerID").toString()));
						
						processDAO.updatePerformerForCharge(previewWorkitemUMap);
					}
				}
			}
						
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegate.setVariable("g_appvLine", apvLineObj.toJSONString());
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	
	public static boolean checkExistInJsonArray(JSONArray targetArr, JSONObject jsonObj){
		boolean ret = false;
		
		try{
			if(targetArr == null){
				ret = false;
			} else {
				for(int i = 0; i < targetArr.size(); i++)
				{
					JSONObject targetObj = (JSONObject)targetArr.get(i);
					String userCode = targetObj.get("UserCode").toString();
					String wiid = targetObj.get("WorkItemID").toString();
					
					if(jsonObj.get("UserCode") != null &&
							jsonObj.get("WorkItemID") != null &&
							userCode.equalsIgnoreCase(jsonObj.get("UserCode").toString()) &&
							wiid.equalsIgnoreCase(jsonObj.get("WorkItemID").toString())){
						ret = true;
						break;
					}
				}	
			}
			
		} catch(Exception e){
			throw e;
		}
		
		return ret;
	}
	
	
	/*
	 * Bas_TWPo_ProcessWorkitem 스크립트와 같이 여러 tasklistener에 사용되는 스크립를 공통화 시키는 곳
	 * */
	
	//결재 행위 후처리
	//Bas_TWPo_ProcessWorkitem
	public static void processWorkitem(){
		
		try{
			
			//의견이 있을 경우 완료 표시.
			
			//추가적인 분석이 필요.
			
		} catch(Exception e){
			throw e;
		}
		
	}
	
	//결재 행위 전처리
	//Bas_TAPr_ProcessNormal
	public static void processNormal(){
		
		try{
			//business state 입력
			
			//예고함변경
			
		} catch(Exception e){
			throw e;
		}
		
	}
	
	//결재단계 후처리
	//Bas_App_ProcessResult
	public static void processResultForOu(JSONObject apvLineObj, String execId, String isMobile) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
			JSONObject person = (JSONObject)pendingPerson.get("person");
			String routeType = pendingObject.get("routeType").toString();
			
			int state = 528;
			
			if(person.containsKey("wiid")){
				
				//workitem update
				Map<String, Object> workitemUMap = new HashMap<>();
				workitemUMap.put("state", state);
				workitemUMap.put("workItemID", person.get("wiid").toString());
				workitemUMap.put("isMobile", isMobile);
				
				processDAO.updateWorkItemForResult(workitemUMap);			
				
				//performer의 subkind 변경(예고자에서 결재자로)
				Map<String, Object> performerUMap = new HashMap<>();
				performerUMap.put("subkind", getSubKind(routeType));
				performerUMap.put("workItemID", person.get("wiid").toString());
				
				processDAO.updatePerformer(performerUMap);	
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//결재단계 후처리
	//Bas_App_ProcessResult
	public static void processResultForOu(JSONObject apvLineObj, String execId, String isMobile, String isBatch) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			JSONObject pendingOu = CoviFlowApprovalLineHelper.getPendingOu(pendingStep, execId);
			JSONObject pendingPerson = CoviFlowApprovalLineHelper.getPendingPerson(pendingOu);
			JSONObject person = (JSONObject)pendingPerson.get("person");
			String routeType = pendingObject.get("routeType").toString();
			
			int state = 528;
			
			if(person.containsKey("wiid")){
				
				//workitem update
				Map<String, Object> workitemUMap = new HashMap<>();
				workitemUMap.put("state", state);
				workitemUMap.put("workItemID", person.get("wiid").toString());
				workitemUMap.put("isMobile", isMobile);
				workitemUMap.put("isBatch", isBatch);
				
				processDAO.updateWorkItemForResult(workitemUMap);			
				
				//performer의 subkind 변경(예고자에서 결재자로)
				/*
				Map<String, Object> performerUMap = new HashMap<>();
				performerUMap.put("subkind", getSubKind(routeType));
				performerUMap.put("workItemID", person.get("wiid").toString());
				
				processDAO.updatePerformer(performerUMap);	
				*/
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//결재단계 후처리
	//Bas_App_ProcessResult
	@SuppressWarnings("unchecked")
	public static void processResult(JSONObject apvLineObj, String taskId, String isMobile) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String routeType = pendingObject.get("routeType").toString();
			String isStep = pendingObject.get("isStep").toString();
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
				
			if(isStep.equalsIgnoreCase("true")){
				int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
				
				//json array, json object 분기
				Object ouObj = pendingStep.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONObject){
					JSONObject ouObject = (JSONObject)ouObj;
					ouArray.add(ouObject);
				} else {
					ouArray = (JSONArray)ouObj;
				}
				
				//process desc update
				Map<String, Object> pdescUMap = new HashMap<>();
				pdescUMap.put("approverCode", pendingObject.get("approverCode").toString());
				pdescUMap.put("approverName", pendingObject.get("approverName").toString());
				pdescUMap.put("approvalStep", pendingObject.get("approvalStep").toString());
				pdescUMap.put("approverSIPAddress", pendingObject.get("approverSIPAddress").toString());
				pdescUMap.put("processDescriptionID", pidescid);
				
				processDAO.updateProcessDescription(pdescUMap);
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					JSONObject ou = (JSONObject)ouArray.get(j);
					int state = 528;
					
					if(ou.containsKey("wiid")){
						
						if(StringUtils.isNotBlank(taskId)&&
								ou.containsKey("taskid")){
							
							if(ou.get("taskid").toString().equalsIgnoreCase(taskId)){
								
								//workitem update
								Map<String, Object> workitemUMap = new HashMap<>();
								workitemUMap.put("state", state);
								workitemUMap.put("workItemID", ou.get("wiid").toString());
								workitemUMap.put("isMobile", isMobile);
								
								processDAO.updateWorkItemForResult(workitemUMap);
								
								//performer의 subkind 변경(예고자에서 결재자로)
								String subKind = getSubKind(routeType);
								if(!subKind.equals("")){
									Map<String, Object> performerUMap = new HashMap<>();
									performerUMap.put("subkind", subKind);
									performerUMap.put("workItemID", ou.get("wiid").toString());
									
									processDAO.updatePerformer(performerUMap);
								}
							}
							
						} else {
							//workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("state", state);
							workitemUMap.put("workItemID", ou.get("wiid").toString());
							workitemUMap.put("isMobile", isMobile);
							
							processDAO.updateWorkItemForResult(workitemUMap);			
							
							//performer의 subkind 변경(예고자에서 결재자로)
							String subKind = getSubKind(routeType);
							if(!subKind.equals("")){
								Map<String, Object> performerUMap = new HashMap<>();
								performerUMap.put("subkind", getSubKind(routeType));
								performerUMap.put("workItemID", ou.get("wiid").toString());
								
								processDAO.updatePerformer(performerUMap);
							}
						}
					}
				}
				
				//txManager.commit(status);
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//결재단계 후처리
	//Bas_App_ProcessResult
	@SuppressWarnings("unchecked")
	public static void processResult(JSONObject apvLineObj, String taskId, String isMobile, String isBatch) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			String routeType = pendingObject.get("routeType").toString();
			String isStep = pendingObject.get("isStep").toString();
			int divisionIndex = Integer.parseInt(pendingObject.get("divisionIndex").toString());
				
			if(isStep.equalsIgnoreCase("true")){
				int pidescid = Integer.parseInt(CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, divisionIndex, "processDescID"));
				
				//json array, json object 분기
				Object ouObj = pendingStep.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONObject){
					JSONObject ouObject = (JSONObject)ouObj;
					ouArray.add(ouObject);
				} else {
					ouArray = (JSONArray)ouObj;
				}
				
				//process desc update
				Map<String, Object> pdescUMap = new HashMap<>();
				pdescUMap.put("approverCode", pendingObject.get("approverCode").toString());
				pdescUMap.put("approverName", pendingObject.get("approverName").toString());
				pdescUMap.put("approvalStep", pendingObject.get("approvalStep").toString());
				pdescUMap.put("approverSIPAddress", pendingObject.get("approverSIPAddress").toString());
				pdescUMap.put("processDescriptionID", pidescid);
				
				processDAO.updateProcessDescription(pdescUMap);
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					JSONObject ou = (JSONObject)ouArray.get(j);
					int state = 528;
					
					if(ou.containsKey("wiid")){
						
						if(StringUtils.isNotBlank(taskId)&&
								ou.containsKey("taskid")){
							
							if(ou.get("taskid").toString().equalsIgnoreCase(taskId)){
								
								//workitem update
								Map<String, Object> workitemUMap = new HashMap<>();
								workitemUMap.put("state", state);
								workitemUMap.put("workItemID", ou.get("wiid").toString());
								workitemUMap.put("isMobile", isMobile);
								workitemUMap.put("isBatch", isBatch);
								
								processDAO.updateWorkItemForResult(workitemUMap);
								
								//performer의 subkind 변경(예고자에서 결재자로)
								String subKind = getSubKind(routeType);
								if(!subKind.equals("")){
									Map<String, Object> performerUMap = new HashMap<>();
									performerUMap.put("subkind", subKind);
									performerUMap.put("workItemID", ou.get("wiid").toString());
									
									processDAO.updatePerformer(performerUMap);
								}
							}
							
						} else {
							//workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("state", state);
							workitemUMap.put("workItemID", ou.get("wiid").toString());
							workitemUMap.put("isMobile", isMobile);
							workitemUMap.put("isBatch", isBatch);
							
							processDAO.updateWorkItemForResult(workitemUMap);			
							
							//performer의 subkind 변경(예고자에서 결재자로)
							String subKind = getSubKind(routeType);
							if(!subKind.equals("")){
								Map<String, Object> performerUMap = new HashMap<>();
								performerUMap.put("subkind", getSubKind(routeType));
								performerUMap.put("workItemID", ou.get("wiid").toString());
								
								processDAO.updatePerformer(performerUMap);
							}
						}
					}
				}
				
				//txManager.commit(status);
			}
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//후결 후처리
	public static void processReviewResult(JSONObject apvLineObj, String taskId, String isMobile) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			//결재선의 pending인 상태를 가져옴
			JSONArray stepObject = CoviFlowApprovalLineHelper.getReviewerStep(apvLineObj);
			
			for(int j = 0; j < stepObject.size(); j++)
			{
				JSONObject ou = (JSONObject)((JSONObject)stepObject.get(j)).get("ou");
				int state = 528;
				
				if(ou.containsKey("wiid")){
					
					if(StringUtils.isNotBlank(taskId)&&
							ou.containsKey("taskid")){
						
						if(ou.get("taskid").toString().equalsIgnoreCase(taskId)){
							
							//workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("state", state);
							workitemUMap.put("workItemID", ou.get("wiid").toString());
							workitemUMap.put("isMobile", isMobile);
							
							processDAO.updateWorkItemForResult(workitemUMap);
						}
						
					} else {
						//workitem update
						Map<String, Object> workitemUMap = new HashMap<>();
						workitemUMap.put("state", state);
						workitemUMap.put("workItemID", ou.get("wiid").toString());
						workitemUMap.put("isMobile", isMobile);
						
						processDAO.updateWorkItemForResult(workitemUMap);
					}
				}
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//후결 후처리
	public static void processReviewResult(JSONObject apvLineObj, String taskId, String isMobile, String isBatch) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			//결재선의 pending인 상태를 가져옴
			JSONArray stepObject = CoviFlowApprovalLineHelper.getReviewerStep(apvLineObj);
			
			for(int j = 0; j < stepObject.size(); j++)
			{
				JSONObject ou = (JSONObject)((JSONObject)stepObject.get(j)).get("ou");
				int state = 528;
				
				if(ou.containsKey("wiid")){
					
					if(StringUtils.isNotBlank(taskId)&&
							ou.containsKey("taskid")){
						
						if(ou.get("taskid").toString().equalsIgnoreCase(taskId)){
							
							//workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("state", state);
							workitemUMap.put("workItemID", ou.get("wiid").toString());
							workitemUMap.put("isMobile", isMobile);
							workitemUMap.put("isBatch", isBatch);
							
							processDAO.updateWorkItemForResult(workitemUMap);
						}
						
					} else {
						//workitem update
						Map<String, Object> workitemUMap = new HashMap<>();
						workitemUMap.put("state", state);
						workitemUMap.put("workItemID", ou.get("wiid").toString());
						workitemUMap.put("isMobile", isMobile);
						workitemUMap.put("isBatch", isBatch);
						
						processDAO.updateWorkItemForResult(workitemUMap);
					}
				}
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//반려처리
	//Bas_App_ProcessReject
	public static void processReject(int piid, String isMobile) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			int state = 528;
			
			//workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("state", state);
			workitemUMap.put("processID", piid);
			workitemUMap.put("isMobile", isMobile);
			
			processDAO.updateWorkItemForReject(workitemUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//반려처리
	//Bas_App_ProcessReject
	@SuppressWarnings("unchecked")
	public static void processRejectForOu(JSONObject ou, String isMobile) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			int state = 528;
			
			if(ou.containsKey("person")){
				Object personObj = ou.get("person");
				JSONArray persons = new JSONArray();
				if(personObj instanceof JSONObject){
					JSONObject jsonObj = (JSONObject)personObj;
					persons.add(jsonObj);
				} else {
					persons = (JSONArray)personObj;
				}
				
				for(int j = 0; j < persons.size(); j++)
				{
					JSONObject person = (JSONObject)persons.get(j);
					
					if(person.containsKey("wiid")){
						//workitem update
						Map<String, Object> workitemUMap = new HashMap<>();
						workitemUMap.put("state", state);
						workitemUMap.put("workItemID", person.get("wiid").toString());
						workitemUMap.put("isMobile", isMobile);
						
						processDAO.updateWorkItemForResult(workitemUMap);	
					}
				}	
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//지정반려 처리
	public static void processRejectTo(List<String> wiids) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			int state = 546;
			for(int i = 0; i < wiids.size(); i++){
				//workitem update
				Map<String, Object> workitemUMap = new HashMap<>();
				workitemUMap.put("state", state);
				workitemUMap.put("workItemID", wiids.get(i));
				
				processDAO.updateWorkItemForResult(workitemUMap);	
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//승인 취소
	@SuppressWarnings("unchecked")
	public static void processApproveCancel(String appvLine, int divisionIndex, int stepIndex) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject appvLineObj = (JSONObject)parser.parse(appvLine);
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject root = (JSONObject)appvLineObj.get("steps");
			Object divisionObj = root.get("division");
			JSONArray divisions = new JSONArray();
			if(divisionObj instanceof JSONObject){
				JSONObject divisionJsonObj = (JSONObject)divisionObj;
				divisions.add(divisionJsonObj);
			} else {
				divisions = (JSONArray)divisionObj;
			}
			JSONObject division = (JSONObject)divisions.get(divisionIndex);
			
			if(division.containsKey("processID")){
				if(divisionIndex == 0) { // 기안부서
					Map<String, Object> procUMap = new HashMap<>();
					procUMap.put("businessState", "01_01_01");
					procUMap.put("processState", 288);
					procUMap.put("processID", Integer.parseInt(division.get("processID").toString()));
					processDAO.updateProcessBusinessState(procUMap);
				} else { // 수신부서
					Map<String, Object> procUMap = new HashMap<>();
					procUMap.put("businessState", "01_01_02");
					procUMap.put("processState", 288);
					procUMap.put("processID", Integer.parseInt(division.get("processID").toString()));
					processDAO.updateProcessBusinessState(procUMap);	
				}
			}	
			
			Object stepO = division.get("step");
			JSONArray stepArray = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				stepArray.add(stepJsonObj);
			} else {
				stepArray = (JSONArray)stepO;
			}
			
			/*
			 * 1. 현재 단계의 workitem, workitemdescription, performer는 다시 예고자로 변경 , 
			 * 		workitem : 288 -> 528, preview
			 * 		process : business_state 01_01_01
			 * 2. 이전 단계의 workitem, workitemdescription, performer는 delete 처리
			 * 3. approveCancel 변수 초기화
			 * */
			//1. 현재 단계
			JSONObject nowStepObject = (JSONObject)stepArray.get(stepIndex);
			Object nowOuObj = nowStepObject.get("ou");
			if(nowOuObj instanceof JSONObject){
				JSONObject nowOuObject = (JSONObject)nowStepObject.get("ou");
			
				if(nowOuObject.containsKey("wiid")){
					int wiid = Integer.parseInt(nowOuObject.get("wiid").toString());
					
					//performer의 subkind 변경(결재자에서 예고자로)
					Map<String, Object> performerUMap = new HashMap<>();
					performerUMap.put("subkind", "T010");
					performerUMap.put("workItemID", wiid);
					processDAO.updatePerformer(performerUMap);
					//workitem : 288 -> 528, preview
					Map<String, Object> workUMap = new HashMap<>();
					workUMap.put("state", 528);
					workUMap.put("name", "preview");
					workUMap.put("workItemID", wiid);
					processDAO.updateWorkItemForApproveCancel(workUMap);
				}
			}
			else if(nowOuObj instanceof JSONArray){ // 병렬결재
				for(int i=0; i<((JSONArray)nowOuObj).size(); i++) {
					JSONObject nowOuObject = (JSONObject)((JSONArray)nowOuObj).get(i);
					
					if(nowOuObject.containsKey("wiid")){
						int wiid = Integer.parseInt(nowOuObject.get("wiid").toString());
						
						//performer의 subkind 변경(결재자에서 예고자로)
						Map<String, Object> performerUMap = new HashMap<>();
						performerUMap.put("subkind", "T010");
						performerUMap.put("workItemID", wiid);
						processDAO.updatePerformer(performerUMap);
						//workitem : 288 -> 528, preview
						Map<String, Object> workUMap = new HashMap<>();
						workUMap.put("state", 528);
						workUMap.put("name", "preview");
						workUMap.put("workItemID", wiid);
						processDAO.updateWorkItemForApproveCancel(workUMap);
					}	
				}
			}
			
			//2. 이전 단계
			JSONObject prevStepObject = (JSONObject)stepArray.get(stepIndex - 1);
			Object prevOuObj = prevStepObject.get("ou");
			if(prevOuObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)prevStepObject.get("ou");
				
				int wiid = 0;
				int pfid = 0;
				if(ouObject.containsKey("wiid")){
					wiid = Integer.parseInt(ouObject.get("wiid").toString());
					
					Map<String, Object> workitemMap = new HashMap<>();
					workitemMap.put("workitemID", wiid);
					
					processDAO.deleteOneWorkItem(workitemMap);
				}
				
				if(ouObject.containsKey("pfid")){
					pfid = Integer.parseInt(ouObject.get("pfid").toString());
					
					Map<String, Object> performerMap = new HashMap<>();
					performerMap.put("performerID", pfid);
								
					processDAO.deleteOnePerformer(performerMap);
				}
				
			}
			else if(prevOuObj instanceof JSONArray){ // 병렬결재
				for(int i=0; i<((JSONArray)prevOuObj).size(); i++) {
					JSONObject ouObject = (JSONObject)((JSONArray)prevOuObj).get(i);
					
					int wiid = 0;
					int pfid = 0;
					if(ouObject.containsKey("wiid")){
						wiid = Integer.parseInt(ouObject.get("wiid").toString());
						
						Map<String, Object> workitemMap = new HashMap<>();
						workitemMap.put("workitemID", wiid);
						
						processDAO.deleteOneWorkItem(workitemMap);
					}
					
					if(ouObject.containsKey("pfid")){
						pfid = Integer.parseInt(ouObject.get("pfid").toString());
						
						Map<String, Object> performerMap = new HashMap<>();
						performerMap.put("performerID", pfid);
									
						processDAO.deleteOnePerformer(performerMap);
					}
				}
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//취소 / 회수 처리
	//Bas_App_ProcessReject
	public static void processCancel(int fiid, int wiid, String ctxJsonStr, String appvLine) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			String initiatorId = "";
			if(formData.containsKey("InitiatorID")){
				initiatorId = formData.get("InitiatorID").toString();
			}
			
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("state", 546);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItemForResult(workitemUMap);
						
			//process update
			Map<String, Object> procUMap = new HashMap<>();
			procUMap.put("processState", 546);
			procUMap.put("formInstID", fiid);
			
			processDAO.updateProcessForWithdraw(procUMap);	
			
			//circulationbox update
			/*	DataState = D
				DeletedDate = NOW() 
				ModID = 기안자 id
				ModDate = NOW()
				
				where FormInstID = fiid
			*/
			Map<String, Object> circulUMap = new HashMap<>();
			circulUMap.put("dataState", "D");
			circulUMap.put("modID", initiatorId);
			circulUMap.put("formInstID", fiid);
			
			processDAO.updateCirculation(circulUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			//LOG.error("processReject", e);	
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	// processid 기준으로 process, workitem state 업데이트
	public static void processCancel(int piid) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
									
			//process update
			Map<String, Object> procUMap = new HashMap<>();
			procUMap.put("processState", 546);
			procUMap.put("processID", piid);
			
			processDAO.updateProcessForWithdraw(procUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
	
	public static void workitemCancel(int piid) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("state", 546);
			workitemUMap.put("processID", piid);
			
			processDAO.updateWorkItemForResult(workitemUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	//취소 / 회수 처리
	//Bas_App_ProcessReject
	@SuppressWarnings("unchecked")
	public static void processCancelForSubProcess(int fiid, JSONObject pendingStep, String ctxJsonStr) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			String initiatorId = "";
			if(formData.containsKey("InitiatorID")){
				initiatorId = formData.get("InitiatorID").toString();
			}
			
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//workitem update
			Object ouObj = pendingStep.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONObject){
				JSONObject ouObject = (JSONObject)ouObj;
				ouArray.add(ouObject);
			} else {
				ouArray = (JSONArray)ouObj;
			}
			
			for(int i = 0; i < ouArray.size(); i++){
				JSONObject oOU = (JSONObject) ouArray.get(i);
				Map<String, Object> workitemUMap = new HashMap<>();
				if (oOU.containsKey("person")) {
					Object oPerson = oOU.get("person");
					JSONArray arrPerson = new JSONArray();
					if (oPerson instanceof JSONObject) {
						arrPerson.add((JSONObject) oPerson);
					} else {
						arrPerson = (JSONArray) oPerson;
					}

					for (int wi = 0; wi < arrPerson.size(); wi++) {
						workitemUMap.put("state", 546);
						workitemUMap.put("workItemID", ((JSONObject) arrPerson.get(wi)).get("wiid"));

						processDAO.updateWorkItemForResult(workitemUMap);
					}
				} else {
				workitemUMap.put("state", 546);
				workitemUMap.put("workItemID", oOU.get("wiid"));
				
				processDAO.updateWorkItemForResult(workitemUMap);
				}
			}
				
			//process update
			Map<String, Object> procUMap = new HashMap<>();
			procUMap.put("processState", 546);
			procUMap.put("formInstID", fiid);
			
			processDAO.updateProcessForWithdraw(procUMap);	
			
			//circulationbox update
			/*	DataState = D
				DeletedDate = NOW() 
				ModID = 기안자 id
				ModDate = NOW()
				
				where FormInstID = fiid
			*/
			Map<String, Object> circulUMap = new HashMap<>();
			circulUMap.put("dataState", "D");
			circulUMap.put("modID", initiatorId);
			circulUMap.put("formInstID", fiid);
			
			processDAO.updateCirculation(circulUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			//LOG.error("processReject", e);	
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	public static void processCharge(int wiid, JSONObject chargeInfo) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			if(chargeInfo != null 
					&& chargeInfo.containsKey("CHARGEID")
					&& chargeInfo.containsKey("CHARGENAME")){
				
				String userCode = chargeInfo.get("CHARGEID").toString();
				String userName = chargeInfo.get("CHARGENAME").toString();
				
				//update workitem
				Map<String, Object> chargeWorkitemUMap = new HashMap<>();
				chargeWorkitemUMap.put("userCode", userCode);
				chargeWorkitemUMap.put("userName", userName);
				chargeWorkitemUMap.put("workItemID", wiid);
				
				processDAO.updateWorkItemForCharge(chargeWorkitemUMap);
				
				//performer insert
				Map<String, Object> performerMap = new HashMap<>();
				performerMap.put("workitemID", wiid);
				performerMap.put("allotKey", "");
				performerMap.put("userCode", userCode);
				performerMap.put("userName", userName);
				performerMap.put("actualKind", "0");
				performerMap.put("state", "1");
				performerMap.put("subKind", "T008");
				
				processDAO.insertPerformer(performerMap);
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//LOG.error("processReject", e);	
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
		
	//결재단계 추출
	//Bas_App_GetRouteInfo
	public static void getRouteInfo(){
				
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//후열자 후처리
	//Bas_TWPo_ProcessByPassedEx
	public static void processByPassedEx(){
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//메일발송
	//Bas_TWPr_SendMailWorkitem
	public static void sendMailWorkitem(){
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//동시결재 예고함 및 결재자 처리
	//Bas_TAPr_ProcessApprovers
	//Bas_TAPo_ProcessApprovers
	public static void processApprovers(){
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//협조자 전처리
	//Bas_TAPr_ProcessCoordinators
	public static void processCoordinators(){
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//합의자 전처리
	//Bas_TAPr_ProcessConsultors
	public static void processConsultors(){
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//대기열 결재
	//Bas_TAPr_ProcessQApprovers
	public static void processQApprovers(){
		try{
			//business state 입력
			
			//결재선 변경
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//사전참조
	//LOB_App_CcinfoBefore
	@SuppressWarnings("unchecked")
	public static void ccinfoBefore(DelegateExecution execution, String ctxJsonStr) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int piid = Integer.parseInt(execution.getProcessInstanceId());
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
			String approverSIPAddress = ""; //SIP값
			String isReserved = (String)pDesc.get("IsReserved");
			String reservedType = ""; //ReservedGubun?
			String reservedTime = null;
			String priority = (String)pDesc.get("Priority");
			String isModify = (String)pDesc.get("IsModify");
			String reserved1 = "";
			String reserved2 = "";
			String actionMode = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			ArrayList<HashMap<String, String>> senders = new ArrayList<>();
						
//			String apvLine = (String)execution.getVariable("g_appvLine");
//			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			
			Map<String, Object> m = new HashMap<>();
			m = execution.getVariables();
			for( String key : m.keySet() ){
				if(key.contains("g_action_")){
					actionMode = (String)execution.getVariable(key);
					break;
				}
	        }
			
			Object apvLine = execution.getVariable("g_appvLine");
			String apvLineStr = "";
			if(apvLine instanceof LinkedHashMap){
				apvLineStr = JSONValue.toJSONString(apvLine);
			} else {
				apvLineStr = apvLine.toString();
			}
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLineStr);
			
			JSONObject root = (JSONObject)apvLineObj.get("steps");
			
			if(root.containsKey("ccinfo")){
				String procDef = execution.getProcessDefinitionId().toString();
				String parentProcDef = "";
				if(ctxJsonObj.containsKey("processDefinitionID")){
					parentProcDef = ctxJsonObj.get("processDefinitionID").toString(); 
				}
				
				Object ccObj = root.get("ccinfo");
				JSONArray ccArray = new JSONArray();
				if(ccObj instanceof JSONObject){
					JSONObject jsonObject = (JSONObject)ccObj;
					ccArray.add(jsonObject);
				} else {
					ccArray = (JSONArray)ccObj;
				}
				
				for(int i = 0; i < ccArray.size(); i++)
				{
					//알림대상
					HashMap<String,String> receiver = new HashMap<>();
					//참조/회람 지정자
					HashMap<String,String> sender = new HashMap<>();
					
					JSONObject ccInfo = (JSONObject)ccArray.get(i);
					
					if(!ccInfo.containsKey("datereceived") || (ccInfo.containsKey("datereceived") && ccInfo.get("datereceived").toString().equals("")) || actionMode.equals("WITHDRAW")){
						String type = "";
						if(ccInfo.containsKey("beforecc")){
							type = ccInfo.get("beforecc").toString();
						}
						
						String belongto = "";
						if(ccInfo.containsKey("belongto")){
							belongto = ccInfo.get("belongto").toString();
						}
						
						String senderid = "";
						if(ccInfo.containsKey("senderid")){
							senderid = ccInfo.get("senderid").toString();
						} else {
							senderid = usid;
						}
						
						String sendername = "";
						if(ccInfo.containsKey("sendername")){
							sendername = ccInfo.get("sendername").toString();
						} else {
							sendername = usdn;
						}
						
						//사전 참조 처리
						if(type.equalsIgnoreCase("y")
								&& !(parentProcDef.contains("request")&&procDef.contains("basic")&&belongto.equalsIgnoreCase("sender"))){
						//if(type.equalsIgnoreCase("y")){
							//결재선 수정
							ccInfo.put("datereceived", sdf.format(new Date()));
							
							//String belongto = (String)ccInfo.get("belongto");
							JSONObject ou = (JSONObject)ccInfo.get("ou");
							String receiptType = "";
							String receiptID = "";
							String receiptName = "";
							String receiptDate = (String)ccInfo.get("datereceived");
							String kind = ""; //kind?
							String comment = "";
							if(ou.containsKey("person")){
								receiptType = "P";
								
								JSONObject person = (JSONObject)ou.get("person");
								receiptID = (String)person.get("code");
								receiptName = (String)person.get("name");
								if(person.containsKey("sipaddress")){
									approverSIPAddress = (String)person.get("sipaddress");
								}
								
								//알림대상 add
								receiver.put("userId", receiptID);
								receiver.put("type", "UR");
								
							} else {
								receiptType = "U";
								receiptID = (String)ou.get("code");
								receiptName = (String)ou.get("name");
								
								//알림대상 add
								receiver.put("userId", receiptID);
								receiver.put("type", "GR");
							}
							
							//사전참조 1
							//사후참조 0
							kind = "1";
							
							//1. circulation description insert
							Map<String, Object> circulationDescMap = new HashMap<>();
							circulationDescMap.put("formInstID", fiid);
							circulationDescMap.put("formID", fmid);
							circulationDescMap.put("formPrefix", formPrefix);
							circulationDescMap.put("formName", formName);
							circulationDescMap.put("formSubject", formSubject);
							circulationDescMap.put("isSecureDoc", isSecureDoc);
							circulationDescMap.put("isFile", isFile);
							circulationDescMap.put("fileExt", fileExt);
							circulationDescMap.put("isComment", isComment);
							circulationDescMap.put("approverCode", usid);
							circulationDescMap.put("approverName", usdn);
							circulationDescMap.put("approvalStep", "");
							circulationDescMap.put("approverSIPAddress", approverSIPAddress);
							circulationDescMap.put("isReserved", isReserved);
							circulationDescMap.put("reservedGubun", reservedType);
							circulationDescMap.put("reservedTime", reservedTime);
							circulationDescMap.put("priority", priority);
							circulationDescMap.put("isModify", isModify);
							circulationDescMap.put("reserved1", reserved1);
							circulationDescMap.put("reserved2", reserved2);
							
							int circulationDescID = 0;
							processDAO.insertCirculationDescription(circulationDescMap);
							circulationDescID = (int)circulationDescMap.get("CirculationBoxDescriptionID");
							
							//2. circulation insert
							Map<String, Object> circulationMap = new HashMap<>();
							circulationMap.put("processID", piid);
							circulationMap.put("circulationBoxDescriptionID", circulationDescID);
							circulationMap.put("formInstID", fiid);
							circulationMap.put("receiptID", receiptID);
							circulationMap.put("receiptType", receiptType);
							circulationMap.put("receiptName", receiptName);
							circulationMap.put("receiptDate", receiptDate);
							circulationMap.put("kind", kind);
							circulationMap.put("state", "C");
							circulationMap.put("readDate", null);
							circulationMap.put("senderID", senderid);
							circulationMap.put("senderName", sendername);
							circulationMap.put("subject", formSubject);
							circulationMap.put("comment", comment);
							circulationMap.put("dataState", "C");
							circulationMap.put("regID", "Engine");
							circulationMap.put("regDate", sdf.format(new Date()));
							circulationMap.put("modID", "");
							circulationMap.put("modDate", sdf.format(new Date()));
							
							processDAO.insertCirculation(circulationMap);
							
							//알림대상 add
							receiver.put("wiid", "");
							receiver.put("fiid", Integer.toString(fiid));
	
							//알림대상 add
							receivers.add(receiver);
							
							//참조/회람 지정자
							sender.put("senderid", senderid);
							sender.put("sendername", sendername);
	
							senders.add(sender);
						}
					}
										
				}
				execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			}
			
			//txManager.commit(status);
			
			//알림 호출
			if(receivers.size() > 0){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				String msgStatus = "CCINFO_BEFORE";
				
				if(actionMode.equalsIgnoreCase("WITHDRAW")){
					msgStatus = "WITHDRAW";
				}
				
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, msgStatus, Integer.toString(piid), receivers, senders, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("CcInfoBefore", e);	
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	//사후참조
	//LOB_App_CcinfoAfter
	@SuppressWarnings("unchecked")
	public static void ccinfoAfter(DelegateExecution execution, String ctxJsonStr) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int piid = Integer.parseInt(execution.getProcessInstanceId());
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
			String approverSIPAddress = ""; //SIP값
			String isReserved = (String)pDesc.get("IsReserved");
			String reservedType = ""; //ReservedGubun?
			String reservedTime = null;
			String priority = (String)pDesc.get("Priority");
			String isModify = (String)pDesc.get("IsModify");
			String reserved1 = "";
			String reserved2 = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			ArrayList<HashMap<String, String>> senders = new ArrayList<>();
			
			String apvLine = (String)execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			JSONObject root = (JSONObject)apvLineObj.get("steps");
			
			if(root.containsKey("ccinfo")){
				String procDef = execution.getProcessDefinitionId();
				String parentProcDef = "";
				if(ctxJsonObj.containsKey("processDefinitionID")){
					parentProcDef = ctxJsonObj.get("processDefinitionID").toString(); 
				}
				
				Object ccObj = root.get("ccinfo");
				JSONArray ccArray = new JSONArray();
				if(ccObj instanceof JSONObject){
					JSONObject jsonObject = (JSONObject)ccObj;
					ccArray.add(jsonObject);
				} else {
					ccArray = (JSONArray)ccObj;
				}
				
				//수신부서 여부에 따른 참조 처리 (발신참조/수신참조)
				String targetBelongTo = "sender";
				if(execution.hasVariable("g_hasReceiptType") && (execution.getVariable("g_hasReceiptType").equals("ou") || execution.getVariable("g_hasReceiptType").equals("person"))) {
					targetBelongTo = "receiver";
				}
				
				for(int i = 0; i < ccArray.size(); i++)
				{
					//알림대상
					HashMap<String,String> receiver = new HashMap<>();
					//참조/회람 지정자
					HashMap<String,String> sender = new HashMap<>();
					
					JSONObject ccInfo = (JSONObject)ccArray.get(i);
					
					if(!ccInfo.containsKey("datereceived") || (ccInfo.containsKey("datereceived") && ccInfo.get("datereceived").toString().equals(""))){
						String type = "";
						if(ccInfo.containsKey("beforecc")){
							type = ccInfo.get("beforecc").toString();
						}
						
						String belongto = "";
						if(ccInfo.containsKey("belongto")){
							belongto = ccInfo.get("belongto").toString();
						}
						
						String senderid = "";
						if(ccInfo.containsKey("senderid")){
							senderid = ccInfo.get("senderid").toString();
						} else {
							senderid = usid;
						}
						
						String sendername = "";
						if(ccInfo.containsKey("sendername")){
							sendername = ccInfo.get("sendername").toString();
						} else {
							sendername = usdn;
						}
						
						//사후참조 처리
						if(!type.equalsIgnoreCase("y") && targetBelongTo.equals(belongto)){
							//결재선 수정
							ccInfo.put("datereceived", sdf.format(new Date()));
							
							//String belongto = (String)ccInfo.get("belongto");
							JSONObject ou = (JSONObject)ccInfo.get("ou");
							String receiptType = "";
							String receiptID = "";
							String receiptName = "";
							String receiptDate = (String)ccInfo.get("datereceived");
							String kind = ""; //kind?
							String comment = "";
							if(ou.containsKey("person")){
								receiptType = "P";
								
								JSONObject person = (JSONObject)ou.get("person");
								receiptID = (String)person.get("code");
								receiptName = (String)person.get("name");
								if(person.containsKey("sipaddress")){
									approverSIPAddress = (String)person.get("sipaddress");
								}
								
								//알림대상 add
								receiver.put("userId", receiptID);
								receiver.put("type", "UR");
								
							} else {
								receiptType = "U";
								receiptID = (String)ou.get("code");
								receiptName = (String)ou.get("name");
								
								//알림대상 add
								receiver.put("userId", receiptID);
								receiver.put("type", "GR");
							}
							
							//사후참조 0
							kind = "0";
							
							//1. circulation description insert
							Map<String, Object> circulationDescMap = new HashMap<>();
							circulationDescMap.put("formInstID", fiid);
							circulationDescMap.put("formID", fmid);
							circulationDescMap.put("formPrefix", formPrefix);
							circulationDescMap.put("formName", formName);
							circulationDescMap.put("formSubject", formSubject);
							circulationDescMap.put("isSecureDoc", isSecureDoc);
							circulationDescMap.put("isFile", isFile);
							circulationDescMap.put("fileExt", fileExt);
							circulationDescMap.put("isComment", isComment);
							circulationDescMap.put("approverCode", usid);
							circulationDescMap.put("approverName", usdn);
							circulationDescMap.put("approvalStep", "");
							circulationDescMap.put("approverSIPAddress", approverSIPAddress);
							circulationDescMap.put("isReserved", isReserved);
							circulationDescMap.put("reservedGubun", reservedType);
							circulationDescMap.put("reservedTime", reservedTime);
							circulationDescMap.put("priority", priority);
							circulationDescMap.put("isModify", isModify);
							circulationDescMap.put("reserved1", reserved1);
							circulationDescMap.put("reserved2", reserved2);
							
							int circulationDescID = 0;
							processDAO.insertCirculationDescription(circulationDescMap);
							circulationDescID = (int)circulationDescMap.get("CirculationBoxDescriptionID");
							
							//2. circulation insert
							Map<String, Object> circulationMap = new HashMap<>();
							circulationMap.put("processID", piid);
							circulationMap.put("circulationBoxDescriptionID", circulationDescID);
							circulationMap.put("formInstID", fiid);
							circulationMap.put("receiptID", receiptID);
							circulationMap.put("receiptType", receiptType);
							circulationMap.put("receiptName", receiptName);
							circulationMap.put("receiptDate", receiptDate);
							circulationMap.put("kind", kind);
							circulationMap.put("state", "C");
							circulationMap.put("readDate", null);
							circulationMap.put("senderID", senderid);
							circulationMap.put("senderName", sendername);
							circulationMap.put("subject", formSubject);
							circulationMap.put("comment", comment);
							circulationMap.put("dataState", "C");
							circulationMap.put("regID", "Engine");
							circulationMap.put("regDate", sdf.format(new Date()));
							circulationMap.put("modID", "");
							circulationMap.put("modDate", sdf.format(new Date()));
							
							processDAO.insertCirculation(circulationMap);
							
							//알림대상 add
							receiver.put("wiid", "");
							receiver.put("fiid", Integer.toString(fiid));
	
							//알림대상 add
							receivers.add(receiver);
							
							//참조/회람 지정자
							sender.put("senderid", senderid);
							sender.put("sendername", sendername);
	
							senders.add(sender);							
						}
					}
				}

				execution.setVariable("g_appvLine", apvLineObj.toJSONString());
			}
			//txManager.commit(status);
			
			//알림 호출
			if(receivers.size() > 0){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "CCINFO_AFTER", Integer.toString(piid), receivers, senders, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("CcInfoBefore", e);	
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}

	}
	
	//결재완료처리-문서이관처리
	//Bas_App_CommonFinalize
	public static void commonFinalize(){
		try{
			//문서번호 발번
			
			//문서대장 등록
		
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//후결자 전처리
	//Dft_TWPr_ProcessReviewer
	public static void preProcessReviewer(){
		try{
			//pre, post 구분이 불필요할 경우 합칠 것
			//후결자 전처리
			
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//후결자 후처리
	//Dft_TWPo_ProcessReviewer
	public static void postProcessReviewer(){
		try{
			//pre, post 구분이 불필요할 경우 합칠 것
			//후결자 후처리
			
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//결재 문서함 데이터 insert
	//Bas_App_ProcessDocBox
	public static void processDocBox(){
		try{
			//archive로 데이터 이동
			
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//Draft 변수처리
	//Dft_App_InitiateRelevantData
	public static void initiateRelevantData(){
		try{
			//archive로 데이터 이동
			
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//Draft 등록대장 처리
	//Dft_App_RegisterApprovedBox
	public static void registerApprovedBox(){
		try{
			//기안부서 등록대장 등록/수신부서 존재 시 발신대장 등록
			
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//발신함
	//Bas_TAPo_ProcessSentBox
	public static void processSentBox(){
		try{
			//기안부서 등록대장 등록/수신부서 존재 시 발신대장 등록
			
			
		} catch(Exception e){
			throw e;
		}
	}
	
	//util성
	public static String getSubKind(String routeType){
		String subkind = "";
		if(routeType.equalsIgnoreCase("approve")){
			subkind = "T000";
		} else if(routeType.equalsIgnoreCase("consult")){
			subkind = "T009";
		} else if(routeType.equalsIgnoreCase("assist")){
			subkind = "T004";
		} else if(routeType.equalsIgnoreCase("audit")){
			subkind = "T016";
		}
		
		return subkind;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static JSONObject insertFormInstanceMulti(JSONObject bodyData, String formInstID) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONArray subTable1 = (JSONArray)bodyData.get("SubTable1");
			
			for(int i = 0; i < subTable1.size(); i++) {
				Map<String, Object> formInstMap = new HashMap<>();
				JSONObject stObj = (JSONObject)subTable1.get(i);
				String rowseq = (String)stObj.get("ROWSEQ");
				
				formInstMap.put("formInstID", formInstID);
				formInstMap.put("rowseq", rowseq);
				
				int multiFormInstID = (int)processDAO.insertFormInstanceMulti(formInstMap);
				
				stObj.put("MULTI_FORM_INST_ID", multiFormInstID);
				subTable1.set(i, stObj);
			}
			
			bodyData.put("SubTable1", subTable1);
			
			//txManager.commit(status);
		} catch(Exception e) {
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return bodyData;
	}

	//공람함할당
	@SuppressWarnings("unchecked")
	public static JSONObject setMultiActivityConfirmors(String taskName, DelegateTask delegateTask, String ctxJsonStr, String assignee, String subkind) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		JSONObject ret = new JSONObject();
		
		try{
			JSONParser parser = new JSONParser();
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctxJsonStr);
			
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			String formName = ctxJsonObj.get("FormName").toString();
			String formSubject = (String)pDesc.get("FormSubject");
			String usid = (String)ctxJsonObj.get("usid");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			
			//결재선의 공람 가져옴
			JSONArray stepObject = CoviFlowApprovalLineHelper.getStepRouteReviewer(apvLineObj);
			
			ArrayList<HashMap<String, String>> receivers = new ArrayList<>();
			
			int state = 288;
			String taskId = delegateTask.getId();
			
			for(int j = 0; j < stepObject.size(); j++)
			{
				//알림대상
				HashMap<String,String> receiver = new HashMap<>();
				
				//JSONObject pendingOu = (JSONObject)((JSONObject)stepObject.get(j)).get("ou");
				JSONArray pendingOuArr = new JSONArray();
				Object pendingOuObj = ((JSONObject)stepObject.get(j)).get("ou");
				if(pendingOuObj instanceof JSONObject) {
					pendingOuArr.add((JSONObject)pendingOuObj);
				}else {
					pendingOuArr = (JSONArray)pendingOuObj;
				}
				
				for(int k=0; k <pendingOuArr.size(); k++) {
									
					JSONObject pendingOu =  (JSONObject)pendingOuArr.get(k);
					
					String code = "";
					String name = "";
					String sip = "";
					String deputyCode = "";
					String deputyName = "";
					
					JSONObject taskObject = new JSONObject();
					
					if(pendingOu.containsKey("person")){
						Object personObj = pendingOu.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject person = (JSONObject)persons.get(0);
						taskObject = (JSONObject)person.get("taskinfo");
						//전달 처리
						if(taskObject.containsKey("kind")){
							if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
								person = (JSONObject)persons.get(persons.size()-1);
								taskObject = (JSONObject)person.get("taskinfo");
							}
						}
						
						code = (String)person.get("code");
						name = (String)person.get("name");
						if(person.containsKey("sipaddress")){
							sip = (String)person.get("sipaddress");
						}
					} else {
						code = (String)pendingOu.get("code");
						name = (String)pendingOu.get("name");
						
						taskObject = (JSONObject)pendingOu.get("taskinfo");
					}
					
					if(code.equals(assignee)){
						//상태변경
						taskObject.put("status", "pending");
						taskObject.put("result", "pending");
						//수신 시각 입력
						taskObject.put("datereceived", sdf.format(new Date()));
						//알림대상 add
						receiver.put("userId", code);
						receiver.put("type", "UR");
						
						if(pendingOu.containsKey("taskid")){
							//performer의 subkind 변경(예고자에서 결재자로)
							Map<String, Object> performerUMap = new HashMap<>();
							performerUMap.put("subkind", subkind);
							performerUMap.put("workItemID", pendingOu.get("wiid").toString());
							
							processDAO.updatePerformer(performerUMap);	
							
							//후결 상위 프로세스, workitem의 taskid, name, state 변경
							Map<String, Object> workitemUCMap = new HashMap<>();
							workitemUCMap.put("processID", piid);
							workitemUCMap.put("name", "reviewer");
							workitemUCMap.put("taskID", taskId);
							workitemUCMap.put("state", state);
							workitemUCMap.put("created", sdf.format(new Date()));
							workitemUCMap.put("workItemID", pendingOu.get("wiid").toString());
							
							processDAO.updateWorkItemForReview(workitemUCMap);	
							
							pendingOu.put("taskid", taskId);
							//알림대상 add
							receiver.put("wiid", pendingOu.get("wiid").toString());
							
						} else {
							// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
							int workItemDescID = 0;
							
							//1-2. workitem insert
							Map<String, Object> workItemMap = new HashMap<>();
							workItemMap.put("taskID", taskId);
							workItemMap.put("processID", piid);
							workItemMap.put("performerID", 0);
							workItemMap.put("workItemDescriptionID", workItemDescID);
							workItemMap.put("name", taskName);
							workItemMap.put("dscr", "");
							workItemMap.put("priority", "");
							workItemMap.put("actualKind", "");
							workItemMap.put("userCode", code);
							workItemMap.put("userName", name);
							workItemMap.put("deputyID", deputyCode);
							workItemMap.put("deputyName", deputyName);
							workItemMap.put("state", state);
							workItemMap.put("created", sdf.format(new Date()));
							workItemMap.put("finishRequested", null);
							workItemMap.put("finished", null);
							workItemMap.put("limit", null);
							workItemMap.put("lastRepeated", null);
							workItemMap.put("finalized", null);
							workItemMap.put("deleted", null);
							workItemMap.put("inlineSubProcess", "");
							workItemMap.put("charge", "");
							workItemMap.put("businessData1", "");
							workItemMap.put("businessData2", "");
							workItemMap.put("businessData3", "");
							workItemMap.put("businessData4", "");
							workItemMap.put("businessData5", "");
							workItemMap.put("businessData6", "");
							workItemMap.put("businessData7", "");
							workItemMap.put("businessData8", "");
							workItemMap.put("businessData9", "");
							workItemMap.put("businessData10", "");
							
							processDAO.insertWorkItem(workItemMap);
							wiid = (int)workItemMap.get("WorkItemID");
							
							//1-3. performer insert
							Map<String, Object> performerMap = new HashMap<>();
							performerMap.put("workitemID", wiid);
							performerMap.put("allotKey", "");
							performerMap.put("userCode", code);
							performerMap.put("userName", name);
							performerMap.put("actualKind", "0");
							performerMap.put("state", "1");
							performerMap.put("subKind", subkind);
							
							processDAO.insertPerformer(performerMap);
							pfid = (int)performerMap.get("PerformerID");
							
							//2. workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("performerID", pfid);
							workitemUMap.put("workItemID", wiid);
							
							processDAO.updateWorkItem(workitemUMap);
							
							pendingOu.put("taskid", taskId);
							pendingOu.put("wiid", Integer.toString(wiid));
							pendingOu.put("widescid", Integer.toString(workItemDescID));
							pendingOu.put("pfid", Integer.toString(pfid));
							//알림대상 add
							receiver.put("wiid", Integer.toString(wiid));
						}
						//break; //필요할 경우 주석을 제거 할 것
						
						//알림대상 add
						receivers.add(receiver);
						
					}
				}	
			}
			
			//결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", piid);
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			ret = apvLineObj;
			//txManager.commit(status);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			//알림 호출
			if(!receivers.isEmpty()){
				usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(fiid, formName, usid, formSubject, "APPROVAL", Integer.toString(piid), receivers, apvLineObj));
			}
		} catch(Exception e){
			//LOG.error("setActivity", e);
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		return ret;
	}
	//공람 후처리
	public static void processStepRouteReviewResult(JSONObject apvLineObj, String taskId, String isMobile, String isBatch) throws Exception{
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			//결재선의 pending인 상태를 가져옴
			JSONArray stepObject = CoviFlowApprovalLineHelper.getStepRouteReviewerPending(apvLineObj);
			
			for(int j = 0; j < stepObject.size(); j++)
			{
				//JSONObject ou = (JSONObject)((JSONObject)stepObject.get(j)).get("ou");
				JSONArray pendingOuObj = (JSONArray) ((JSONObject)stepObject.get(j)).get("ou");
				for(int k=0; k <pendingOuObj.size(); k++) {
					JSONObject ou =  (JSONObject)pendingOuObj.get(k);
					int state = 528;
					
					if(ou.containsKey("wiid")){
						
						if(StringUtils.isNotBlank(taskId)&&
								ou.containsKey("taskid")){
							
							if(ou.get("taskid").toString().equalsIgnoreCase(taskId)){
								
								//workitem update
								Map<String, Object> workitemUMap = new HashMap<>();
								workitemUMap.put("state", state);
								workitemUMap.put("workItemID", ou.get("wiid").toString());
								workitemUMap.put("isMobile", isMobile);
								workitemUMap.put("isBatch", isBatch);
								
								processDAO.updateWorkItemForResult(workitemUMap);
							}
							
						} else {
							//workitem update
							Map<String, Object> workitemUMap = new HashMap<>();
							workitemUMap.put("state", state);
							workitemUMap.put("workItemID", ou.get("wiid").toString());
							workitemUMap.put("isMobile", isMobile);
							workitemUMap.put("isBatch", isBatch);
							
							processDAO.updateWorkItemForResult(workitemUMap);
						}
					}
				}
			}
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	// 예약기안(발송) workitem 생성 > 스케쥴러 조회대상.
	public static void setActivityReserved(String taskName, int piid, String taskId, String ctxJsonStr, String ouCode, String ouName, String subkind, int state, Date reservedTime){
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
						
			//0. 변수 선언
			int wiid = 0;
			int pfid = 0;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String limit = "";
			limit = sdf.format(reservedTime);// reservedTime : 서버타임존 기준으로 변환된 시간정보.
			
			// jwf_workitemdescription 테이블 사용안함에 따라 0으로 고정함.
			int workItemDescID = 0;
			
			//1-2. workitem insert
			Map<String, Object> workItemMap = new HashMap<>();
			workItemMap.put("taskID", taskId);
			workItemMap.put("processID", piid);
			workItemMap.put("performerID", 0);
			workItemMap.put("workItemDescriptionID", workItemDescID);
			workItemMap.put("name", taskName);
			workItemMap.put("dscr", "");
			workItemMap.put("priority", "");
			workItemMap.put("actualKind", "");
			workItemMap.put("userCode", ouCode);
			workItemMap.put("userName", ouName);
			workItemMap.put("deputyID", "");
			workItemMap.put("deputyName", "");
			workItemMap.put("state", state);
			workItemMap.put("created", sdf.format(new Date()));
			workItemMap.put("finishRequested", null);
			workItemMap.put("finished", null);
			workItemMap.put("limit", limit);
			workItemMap.put("lastRepeated", null);
			workItemMap.put("finalized", null);
			workItemMap.put("deleted", null);
			workItemMap.put("inlineSubProcess", "");
			workItemMap.put("charge", "");
			workItemMap.put("businessData1", "");
			workItemMap.put("businessData2", "");
			workItemMap.put("businessData3", "");
			workItemMap.put("businessData4", "");
			workItemMap.put("businessData5", "");
			workItemMap.put("businessData6", "");
			workItemMap.put("businessData7", "");
			workItemMap.put("businessData8", "");
			workItemMap.put("businessData9", "");
			workItemMap.put("businessData10", "");
			
			processDAO.insertWorkItem(workItemMap);
			wiid = (int)workItemMap.get("WorkItemID");
			
			//1-3. performer insert
			Map<String, Object> performerMap = new HashMap<>();
			performerMap.put("workitemID", wiid);
			performerMap.put("allotKey", "");
			performerMap.put("userCode", ouCode);
			performerMap.put("userName", ouName);
			performerMap.put("actualKind", "1");
			performerMap.put("state", "1");
			performerMap.put("subKind", subkind);
			
			processDAO.insertPerformer(performerMap);
			pfid = (int)performerMap.get("PerformerID");
			
			//2. workitem update
			Map<String, Object> workitemUMap = new HashMap<>();
			workitemUMap.put("performerID", pfid);
			workitemUMap.put("workItemID", wiid);
			
			processDAO.updateWorkItem(workitemUMap);
			
			//txManager.commit(status);
		} catch(Exception e){
			LOG.error("setActivityReserved", e);	
			//txManager.rollback(status);
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
}