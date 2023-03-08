/**
 * @Class Name : DraftPostAllSentBasicProcess.java
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

public class DraftPostAllSentBasicProcess implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPostAllSentBasicProcess.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		//수신자후처리
		LOG.info("DraftPostAllSentBasicProcess");
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			//looping 처리
			//PGlobal("HasAllSent")
			//배포 처리 분기
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			if(formData.containsKey("ReceiptList")){
				String receiptList = formData.get("ReceiptList").toString();
				String receiptPersonList = receiptList.split("@")[1];
				
				if(!StringUtils.isBlank(receiptPersonList)){
					
					//pending -> completed
					//inactive -> pending
					JSONArray receiptPerson = (JSONArray)parser.parse(execution.getVariable("g_receiptPerson").toString());
					receiptPerson = CoviFlowWorkHelper.changePendingReceiptStatus(receiptPerson);
					execution.setVariable("g_receiptPerson", receiptPerson.toJSONString());
					
					JSONObject pendingObj = CoviFlowWorkHelper.getPendingReceipt(receiptPerson);
					if(pendingObj.get("isReceipt").toString().equalsIgnoreCase("false")){
						execution.setVariable("g_hasAllSent", "true");	
					}
				}
			}
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPostAllSentBasicProcess", e);
			throw e;
		}
		
		
	}
}
