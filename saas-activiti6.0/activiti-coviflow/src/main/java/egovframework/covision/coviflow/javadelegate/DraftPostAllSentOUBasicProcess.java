/**
 * @Class Name : DraftPostAllSentOUBasicProcess.java
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

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftPostAllSentOUBasicProcess implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPostAllSentOUBasicProcess.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		//수신자후처리
		LOG.info("DraftPostAllSentOUBasicProcess");
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			//looping 처리
			//PGlobal("HasAllSent")
			//배포 처리 분기
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			if(formData.containsKey("ReceiptList")){
				String receiptList = formData.get("ReceiptList").toString();
				String receiptOuList = receiptList.split("@")[0];
				
				if(!StringUtils.isBlank(receiptOuList)){
					
					//pending -> completed
					//inactive -> pending
					JSONArray receiptOu = (JSONArray)parser.parse(execution.getVariable("g_receiptOu").toString());
					receiptOu = CoviFlowWorkHelper.changePendingReceiptStatus(receiptOu);
					execution.setVariable("g_receiptOu", receiptOu.toJSONString());
					
					JSONObject pendingObj = CoviFlowWorkHelper.getPendingReceipt(receiptOu);
					if(pendingObj.get("isReceipt").toString().equalsIgnoreCase("false")){
						execution.setVariable("g_hasAllSent", "true");	
					}
				}
			}
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPostAllSentOUBasicProcess", e);
			throw e;
		}
		
		
	}
}
