/**
 * @Class Name : DraftRegisterOuterPubSentBox.java
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

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
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
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowDateHelper;
import egovframework.covision.coviflow.util.CoviFlowDocNumberHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftRegisterOuterPubSentBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRegisterOuterPubSentBox.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftRegisterOuterPubSentBox");
		
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
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			/* 1. 문서번호 발번 - covi_approval4j.jwf_documentnumber
			 * 2. forminstance update
			 * 3. ESignMeta update
			 * */
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int serialNumber = 0;
			String initiatorUnitID = "";
			String initiatorUnitName = "";
			String shortInitiatorUnitName = "";
			String entName = "";
			int docListType = 3;
			String fiscalYear = CoviFlowDateHelper.getMonth().substring(0, 4);
			String fiscalMonth = CoviFlowDateHelper.getMonth().substring(4, 6);
			String formPrefix = "";
			
			//수협(문서유통Y & 웹기안기Y & 다안기안Y)
			String IsUseScDistribution = "N";
			String IsUseHWPEditYN = "N";
			String IsUseMultiEdit = "N";
			String IsUseEditYN = "N";
			String subTable1 = "";
			
			if(ctxJsonObj.containsKey("FormInfoExt")){
				JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
				
				if(formInfoExt.containsKey("DocInfo")){
					JSONObject docInfo = (JSONObject)formInfoExt.get("DocInfo");
					
					if(docInfo.containsKey("AppliedYear")){
						fiscalYear = (String)docInfo.get("AppliedYear");
					}
				}
				if(formInfoExt.containsKey("outerpub_doctype"))
					docListType = Integer.parseInt((String)formInfoExt.get("outerpub_doctype"));
				//수협(문서유통Y & 웹기안기Y & 다안기안Y)
				if(formInfoExt.containsKey("scDistribution"))
					IsUseScDistribution = (String)formInfoExt.get("scDistribution");
				if(formInfoExt.containsKey("IsUseWebHWPEditYN"))
					IsUseHWPEditYN = (String)formInfoExt.get("IsUseWebHWPEditYN");
				if(formInfoExt.containsKey("IsUseMultiEdit"))
					IsUseMultiEdit = (String)formInfoExt.get("IsUseMultiEdit");
				if(formInfoExt.containsKey("SubTable1"))
					subTable1 = (String)formInfoExt.get("SubTable1");
				if(formInfoExt.containsKey("UseEditYN"))
					IsUseEditYN = (String)formInfoExt.get("UseEditYN");
			}
			if(ctxJsonObj.containsKey("FormPrefix") ) {
				formPrefix = ctxJsonObj.get("FormPrefix").toString();
			}
            
			// 회계기준년도 변경
			String baseFiscalMonth = CoviFlowPropHelper.getInstace().getPropertyValue("fiscalmonth");
			if(baseFiscalMonth != null && !baseFiscalMonth.isEmpty()) {
				if(Integer.parseInt(fiscalMonth) < Integer.parseInt(baseFiscalMonth)) {
					fiscalYear = String.valueOf(Integer.parseInt(fiscalYear) - 1);
				}
			}
			
			initiatorUnitID = CoviFlowApprovalLineHelper.getDivisionAttr(apvLineObj, 0, "oucode");
			
			// 부서 DisplayName, ShortName, 회사 DisplayName DB에서 조회해 올 것(문서번호는 다국어 X)
			Map<String, Object> paramsS = new HashMap<>();
			paramsS.put("groupCode", initiatorUnitID);
			
			JSONObject nameObj = processDAO.selectGroupName(paramsS);
			initiatorUnitName = nameObj.get("GR_DisplayName").toString();
			shortInitiatorUnitName = nameObj.get("GR_ShortName").toString();
			entName = nameObj.get("EntName").toString();
			
			final Map<String, Object> paramsC = new HashMap<>();

			paramsC.put("docListType", docListType);
			paramsC.put("fiscalYear", fiscalYear);
			paramsC.put("deptCode", initiatorUnitID);
			paramsC.put("deptName", initiatorUnitName);
			paramsC.put("categoryNumber", "");
			
			processDAO.insertDocumentNumber(paramsC);
			serialNumber = (int)paramsC.get("SerialNumber");
			
			String displayedDocNumber = "";
			displayedDocNumber = CoviFlowDocNumberHelper.issueDocNumber(serialNumber, "11", initiatorUnitID, initiatorUnitName, shortInitiatorUnitName, fiscalYear, "", entName);
			
			// displayedNumber update
			paramsC.remove("displayedNumber");
			paramsC.put("displayedNumber", displayedDocNumber);
			processDAO.updateDocumentNumber(paramsC);
			
			//jwf_forminstance update
			Map<String, Object> paramsU = new HashMap<String, Object>();
			paramsU.put("receiveNo", displayedDocNumber);
			paramsU.put("formInstID", fiid);
			
			processDAO.updateFormInstanceForReceiveNo(paramsU);
			
			// (문서유통Y & 다안기안Y) GOV테이블에 안 갯수대로 insert
			if(IsUseScDistribution.equalsIgnoreCase("Y") && IsUseMultiEdit.equalsIgnoreCase("Y")) {
				
				Map<String, Object> map = new HashMap<>();
				map.put("formInstID", fiid);
				map.put("tableName", subTable1);
				String multiApvReceiverType = "else";
				String multiReceiveType = "outelecdoc";
				String bodyContext = "";
				
				//폼인스턴스에서 바디컨텍스트 가져오기
				JSONObject formInstance = processDAO.selectFormInstance(map);
				JSONArray subTableList = null;
				JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
				if(formData.containsKey("BodyData")) {
					JSONObject bodyData = (JSONObject)formData.get("BodyData");
					if(bodyData.containsKey("SubTable1")) {
						subTableList = (JSONArray)bodyData.get("SubTable1");
						
					}
				}
				for(int i = 0; i < subTableList.size(); i++) {
					JSONObject subTable = (JSONObject)subTableList.get(i);
					//시행발송 & 대외수신일때만 govTable insert
					if(subTable.get("MULTI_APV_RECEIVER_TYPE").equals(multiApvReceiverType) && subTable.get("MULTI_RECEIVE_TYPE").equals(multiReceiveType)) {
						JSONObject bodyContextObj = new JSONObject();
						JSONObject attachFileinfoObj =  new JSONObject();
						bodyContext = "";
						paramsC.clear();
						
						//공통정보(기안별)
						paramsC.put("processID", execution.getProcessInstanceId());
						paramsC.put("formInstID", fiid);
						paramsC.put("docNumber", displayedDocNumber);
						paramsC.put("domainDataContext", apvLine);
						paramsC.put("docType", "wait");
						paramsC.put("tableName", subTable1);
						paramsC.put("rowSeq", subTable.get("MULTI_ROWSEQ"));
						
						attachFileinfoObj  = processDAO.selectMultiAttachfileInfo(paramsC);//다안기안 첨부파일 데이터
						
						//개별정보(안별)
						String approvalURL = "/approval/approval_Form.do?mode=COMPLETE&processID="+execution.getProcessInstanceId()+"&formPrefix=" + formPrefix + "&archived=true&menukind=notelist&doclisttype=88&rowSeq=" + subTable.get("MULTI_ROWSEQ");
				        String receiverNames = "";
				        String receivers = "";
						for( String receiver : ((String)subTable.get("MULTI_RECEIVENAMES")).split(";") ) { 
							if(receivers != "") {
								receivers += ";" + receiver.split(":")[1]; 
								receiverNames += "," + receiver.split(":")[3]; 
							}else {
								receivers = receiver.split(":")[1]; 
								receiverNames = receiver.split(":")[3]; 			
							}
						}		
						if(formInstance != null){
							bodyContext = (String)formInstance.get("BodyContext");
							bodyContext = CoviFlowWorkHelper.base64Decode(bodyContext);
							bodyContextObj = (JSONObject)parser.parse(bodyContext);					
							//공통 (공통제목, 문서번호, 보안등급, 보존기간, )
							bodyContextObj.put("subject", formInstance.get("Subject"));
							bodyContextObj.put("docNo", displayedDocNumber);
							bodyContextObj.put("docLevel", formInstance.get("DocLevel")); //bodyContext에 null로 들어가지않도록 빈문자열처리
							bodyContextObj.put("saveTerm", formInstance.get("SaveTerm"));
							//안별 (날짜, 안제목, 내부결재/시행발송, 인장, seal, symbol, logo, RECEIVE_INFO, receiver, receiverName)
							bodyContextObj.put("ENFORCEDATE", sdf.format(new Date())); // 오늘 일자
							bodyContextObj.put("multiTitle", subTable.get("MULTI_TITLE"));
							bodyContextObj.put("receiveCheck", subTable.get("MULTI_APV_RECEIVER_TYPE"));
							bodyContextObj.put("senderName", subTable.get("MULTI_CHIEF"));
							bodyContextObj.put("seal", subTable.get("MULTI_STAMP"));
							bodyContextObj.put("symbol", subTable.get("MULTI_SYMBOL"));
							bodyContextObj.put("logo", subTable.get("MULTI_LOGO"));
							bodyContextObj.put("RECEIVE_INFO", subTable.get("MULTI_RECEIVENAMES"));
							bodyContextObj.put("receiver", receivers);
							bodyContextObj.put("receiverName", receiverNames);
							bodyContextObj.put("organ", subTable.get("MULTI_ORGAN"));
							bodyContextObj.put("telephone", subTable.get("MULTI_TELEPHONE"));
							bodyContextObj.put("email", subTable.get("MULTI_EMAIL"));
							bodyContextObj.put("zipCode", subTable.get("MULTI_ZIPCODE"));
							bodyContextObj.put("address", subTable.get("MULTI_ADDRESS"));
							bodyContextObj.put("homeUrl", subTable.get("MULTI_HOMEURL"));
							bodyContextObj.put("fax", subTable.get("MULTI_FAX"));
							bodyContextObj.put("headCampaign", subTable.get("MULTI_CAMPAIGN_T"));
							bodyContextObj.put("footCampaign", subTable.get("MULTI_CAMPAIGN_F"));
							bodyContext =  CoviFlowWorkHelper.base64Encode(bodyContextObj.toString());
						}
						paramsC.put("uniqueID", processDAO.getUniqueId());
						paramsC.put("receiverName",receiverNames);
						paramsC.put("receiver", receivers);					
						paramsC.put("enforceDate", bodyContextObj.get("ENFORCEDATE").toString());
						paramsC.put("bodyContext", bodyContext);
						paramsC.put("multiTitle", subTable.get("MULTI_TITLE"));
						paramsC.put("multiSubject", subTable.get("MULTI_SUBJECT"));
						//paramsC.put("multiAttachFileInfo", CoviFlowWorkHelper.base64Encode(subTable.get("MULTI_ATTACH_FILE").toString()));
						paramsC.put("multiAttachFileInfo", attachFileinfoObj != null ? attachFileinfoObj.get("MultiAttachFile").toString() : "");
						paramsC.put("approvalURL", approvalURL);					
						paramsC.put("Subject", formInstance.get("Subject"));
						paramsC.put("InitiatorID", formInstance.get("InitiatorID"));
						paramsC.put("InitiatorName", formInstance.get("InitiatorName"));
						paramsC.put("InitiatorUnitID", formInstance.get("InitiatorUnitID"));
						paramsC.put("InitiatorUnitName", formInstance.get("InitiatorUnitName"));
						paramsC.put("EntCode", formInstance.get("EntCode"));
						
						//Gov테이블 insert
						processDAO.insertGovApprovalDataMulti(paramsC);
						// select insert 문에서는 CLOB binding 처리 오류가 발생하여 insert & update (CLOB) 로 변경.
						processDAO.updateGovApprovalDataE(paramsC);
						//sub테이블 update (gov테이블의 uniqueId)
						processDAO.updateSubTable(paramsC);
					}
					
				}
				
			}
			// 문서유통 - 대외공문 양식인 경우
			else if( "EmbbedGSimpleDraftingOUT,WF_FORM_GOVDOCS_001,WF_FORM_GOV_EDITOR,WF_FORM_GOV_MULTI_EDITOR".contains(formPrefix) ) {
				FileInputStream fis = null; 
				try {
					final Properties p = new Properties();		
					File file = new File( System.getProperty("DEPLOY_PATH")+"/covi_property/globals.properties" );
					fis = new FileInputStream( file );
					p.load(fis);
					String approvalURL = "/approval/approval_Form.do?mode=COMPLETE&processID="+execution.getProcessInstanceId()+"&formPrefix=" + formPrefix + "&archived=true&menukind=notelist&doclisttype=88";
					String bodyContext = "";
					
					//select bodycontext
					Map<String, Object> map = new HashMap<>();
					map.put("formInstID", fiid);
					JSONObject formInstance = processDAO.selectFormInstance(map);
					paramsC.clear();
					JSONObject bodyContextObj = new JSONObject();
					if(formInstance != null){
						bodyContext = (String)formInstance.get("BodyContext");
						bodyContext = CoviFlowWorkHelper.base64Decode(bodyContext);
						bodyContextObj = (JSONObject)parser.parse(bodyContext);					
						bodyContextObj.put("ENFORCEDATE", sdf.format(new Date())); // 오늘 일자
						bodyContextObj.put("REGNUMBER", displayedDocNumber);					
						paramsC.put("receiver"		, bodyContextObj.get("receiver") );
						paramsC.put("receiverName"	, bodyContextObj.get("receiverName") );					
						bodyContext =  CoviFlowWorkHelper.base64Encode( bodyContextObj.toString() );
					}
					paramsC.put("uniqueID", processDAO.getUniqueId());
					paramsC.put("processID", execution.getProcessInstanceId());
					paramsC.put("formInstID", fiid);
					paramsC.put("docNumber", displayedDocNumber);
					paramsC.put("domainDataContext", apvLine);
					paramsC.put("approvalURL", approvalURL);
					paramsC.put("bodyContext", bodyContext);							
					paramsC.put("enforceDate", bodyContextObj.get("ENFORCEDATE").toString() );
						paramsC.put("docType", "wait");
	//				if( p.getProperty("govDoc.autoSend").equals("Y") ) {
	//					paramsC.put("docType", "send");
	//				}else {
	//					paramsC.put("docType", "wait");
	//				}
					processDAO.insertGovApprovalDataE(paramsC);
					
					// select insert 문에서는 CLOB binding 처리 오류가 발생하여 insert & update (CLOB) 로 변경.
					processDAO.updateGovApprovalDataE(paramsC);
					
					if( p.getProperty("govDoc.autoSend").equals("Y") ) {
						//txManager.commit(status);
						TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
							@Override
							public void afterCommit() {
								try {
									RestTemplate restTemplate = new RestTemplate();
									JSONObject 	returnObj 	= new JSONObject();					
									MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
									
									//paramMap.add("formInstId",  Integer.toString(fiid) );
									//paramMap.add("processId", execution.getProcessInstanceId());
									paramMap.add("receiver"	,  	paramsC.get("receiver").toString() );					
									paramMap.add("type"		,  	"send" );					
									paramMap.add("uniqueID"	, 	paramsC.get("uniqueID").toString() );
									
									returnObj = (JSONObject) restTemplate.postForObject(p.getProperty("govDoc.path")+"govdocs/service/callPacker.do", paramMap, JSONObject.class);
									if( returnObj.get("status").equals("INTERNAL_SERVER_ERROR") ) throw new Exception();
								} catch (Exception e) {
									LOG.error("DraftEnd - Sending Message", e);
								}
							}
						});
					}
			    }catch (Exception e) {
			    	e.printStackTrace();			    	
				}finally {
					if(fis != null) {
						fis.close();
					}
				}
				
			}else { 
				//txManager.commit(status);
			}
		}catch(Exception e){
			//txManager.rollback(status);
			LOG.error("DraftRegisterOuterPubSentBox", e);
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}

	private Object nullCheck(Object obj, String str) {
		Object result = obj;
		if(obj == null){
			result = str;
		}
		return result;
	}
}
