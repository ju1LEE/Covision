/**
 * @Class Name : DraftLoopAllSentDept.java
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

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftLoopAllSentDept implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftLoopAllSentDept.class);
			
	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftLoopAllSentDept");
		
		try{
			//looping 처리
			//PGlobal("HasSent")
						
			//set variable
			//execution.setVariable("input", var);
			
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");

			if(formData.containsKey("BodyData")) {
				JSONObject bodyData = (JSONObject)formData.get("BodyData");
				
				if(bodyData.containsKey("SubTable1")) {
					JSONArray subTable1 = (JSONArray)bodyData.get("SubTable1");
					
					execution.setVariable("g_subTable_size", String.valueOf(subTable1.size()));
					if(execution.hasVariable("g_subTable_idx")) {
						execution.setVariable("g_subTable_idx", execution.getVariable("g_subTable_idx"));
					} else {
						execution.setVariable("g_subTable_idx", "0");
					}
					
					JSONObject stObj = (JSONObject)subTable1.get(Integer.parseInt((String) execution.getVariable("g_subTable_idx")));
					String receiptList = (String)stObj.get("MULTI_RECEIPTLIST");
					String receiptOuList = (receiptList.split("@").length > 0 ? receiptList.split("@")[0] : "" );
					
					if(!StringUtils.isBlank(receiptOuList) || receiptList.split("@").length > 2){ // 공용배포처가 있는 경우에도 실행
						
						//set variable						
						String receiptNames = "";
						if(stObj.containsKey("MULTI_RECEIVENAMES")){
							receiptNames = stObj.get("MULTI_RECEIVENAMES").toString();
						}
						String fiid = "";
						if(stObj.containsKey("MULTI_FORM_INST_ID")) {
							fiid = stObj.get("MULTI_FORM_INST_ID").toString();
						}else {
							fiid = ctxJsonObj.get("FormInstID").toString();
						}
						String subject = "";
						if(stObj.containsKey("MULTI_TITLE")) {
							subject = stObj.get("MULTI_TITLE").toString();
						}
						String rowSeq = "";
						if(stObj.containsKey("MULTI_ROWSEQ")) {
							rowSeq = stObj.get("MULTI_ROWSEQ").toString();
						}
						
						if(!StringUtils.isBlank(receiptNames) && !StringUtils.isBlank(fiid)){
							JSONArray receipts = CoviFlowWorkHelper.setReceipts(receiptNames, "0");
							
							// [19-04-24] 공용배포처 처리 추가.
							JSONArray receipts_shared = CoviFlowWorkHelper.setShareReceipts(receiptNames, false, formInfoExt.get("entcode").toString());
							for(int i=0; i<receipts_shared.size(); i++) {
								receipts.add(receipts_shared.get(i));
							}
							
							// 수신공람인 경우
							// Draft 프로세스에 workitem 생성
							if(((String)formInfoExt.get("scDocBoxRE")).equals("Y")) {
								for(int j = 0; j < receipts.size(); j++)
								{
									JSONObject receipt = (JSONObject)receipts.get(j);
									CoviFlowWorkHelper.setActivityForRE("ReceiptBox", execution, ctxJsonObj.toString(), receipt.get("code").toString(), receipt.get("name").toString(), "RE");
								}
							}
							else {
								CoviFlowPropHelper propHelper = CoviFlowPropHelper.getInstace();
								boolean useOuterDistribution = Boolean.parseBoolean(propHelper.getPropertyValue("useOuterDistribution"));
								
								String apvLine = (String)execution.getVariable("g_appvLine");
								JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);

								if(useOuterDistribution){
									if(receipts.size() > 0) {
										//call private domain data insert
										JSONObject param = new JSONObject();
										param.put("type", "0");
										param.put("receiptList", receipts.toJSONString());
										param.put("context", ctxJsonObj.toJSONString());
										param.put("approvalLine", apvLineObj.toJSONString());
										param.put("piid", execution.getProcessInstanceId());
										param.put("fiid", fiid);
										param.put("subject", subject);
										param.put("docNumber", execution.getVariable("g_docNumber"));
										param.put("rowSeq", rowSeq);
										
										execution.setVariable("g_subTable_param", param.toJSONString());
									}
								} else {									
									for(int j = 0; j < receipts.size(); j++)
									{
										JSONObject receipt = (JSONObject)receipts.get(j);
										
										apvLineObj = CoviFlowWorkHelper.addReceiptDivision(apvLineObj, receipt, "0");
										
										Map<String, Object> processVariables = new HashMap<String, Object>();
										processVariables.put("g_context", ctxJsonObj.toJSONString());
										processVariables.put("g_appvLine", apvLineObj.toJSONString());
										processVariables.put("g_piid", execution.getProcessInstanceId());
										
										ProcessEngines.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByKey("basic1", processVariables);
									}									
								}
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftLoopAllSentDept", e);
			throw e;
		}
		
		
		
	}
}
