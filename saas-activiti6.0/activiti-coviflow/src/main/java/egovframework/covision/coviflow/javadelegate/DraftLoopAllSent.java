/**
 * @Class Name : DraftLoopAllSent.java
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

import java.util.ArrayList;
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
import egovframework.covision.coviflow.util.CoviFlowWSHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftLoopAllSent implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftLoopAllSent.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		//수신자분배
		LOG.info("DraftLoopAllSent");
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			//looping 처리
			//PGlobal("HasAllSent")
			//배포 처리 분기
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			if(formInfoExt.containsKey("ReceiptList")){
				//execution.setVariable("g_hasAllSent", "false");	
				
				String receiptList = formInfoExt.get("ReceiptList").toString();
				String receiptPersonList = (receiptList.split("@").length > 1 ? receiptList.split("@")[1] : "" );
				
				if(!StringUtils.isBlank(receiptPersonList) || receiptList.split("@").length > 2){ // 공용배포처가 있는 경우에도 실행
					
					//set variable
					String receiptNames = "";
					if(formInfoExt.containsKey("ReceiveNames")){
						receiptNames = formInfoExt.get("ReceiveNames").toString();
					}
					
					if(!StringUtils.isBlank(receiptNames)){
						JSONArray receipts = CoviFlowWorkHelper.setReceipts(receiptNames, "1");
						
						// [19-04-24] 공용배포처 처리 추가.
						JSONArray receipts_shared = CoviFlowWorkHelper.setShareReceipts(receiptNames, true, formInfoExt.get("entcode").toString());
						for(int i=0; i<receipts_shared.size(); i++) {
							receipts.add(receipts_shared.get(i));
						}

						// 수신공람인 경우
						// Draft 프로세스에 workitem 생성
						if(((String)formInfoExt.get("scDocBoxRE")).equals("Y")) {
							for(int j = 0; j < receipts.size(); j++)
							{
								JSONObject receipt = (JSONObject)receipts.get(j);
								CoviFlowWorkHelper.setActivityForRE("ReceiptBox", execution, ctxJsonObj.toString(), receipt.get("code").toString(), receipt.get("name").toString(), "T022");
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
									JSONArray params = new JSONArray();
									JSONObject param = new JSONObject();
									param.put("type", "1");
									param.put("receiptList", receipts.toJSONString());
									param.put("context", ctxJsonObj.toJSONString());
									param.put("approvalLine", apvLineObj.toJSONString());
									param.put("piid", execution.getProcessInstanceId());
									param.put("docNumber", execution.getVariable("g_docNumber"));
									
									params.add(param);
									CoviFlowWSHelper.invokeApi("DISTRIBUTION", params);	
								}						
							} else {
								for(int j = 0; j < receipts.size(); j++)
								{
									JSONObject receipt = (JSONObject)receipts.get(j);
									
									apvLineObj = CoviFlowWorkHelper.addReceiptDivision(apvLineObj, receipt, "1");
									
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
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftLoopAllSent", e);
			throw e;
		}
		
	}
}
