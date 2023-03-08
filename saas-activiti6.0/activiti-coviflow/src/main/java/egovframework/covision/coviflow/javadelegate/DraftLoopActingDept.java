/**
 * @Class Name : DraftLoopActingDept.java
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
import egovframework.covision.coviflow.util.CoviFlowWSHelper;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftLoopActingDept implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftLoopActingDept.class);
			
	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftLoopActingDept");
		
		try{
			if(execution.getVariable("g_subTable_param") != null) {
				JSONParser parser = new JSONParser();
				JSONObject receiptParam = new JSONObject();
				
				receiptParam = (JSONObject) parser.parse((String) execution.getVariable("g_subTable_param"));
				
				if(receiptParam.containsKey("receiptList")) {
					/*
					JSONArray receiptList = (JSONArray) parser.parse((String) receiptParam.get("receiptList"));
					
					execution.setVariable("g_recpList_size", String.valueOf(receiptList.size()));
					if(execution.hasVariable("g_recpList_idx")) {
						execution.setVariable("g_recpList_idx", execution.getVariable("g_recpList_idx"));
					} else {
						execution.setVariable("g_recpList_idx", "0");
					}
					
					JSONObject receipt = (JSONObject)receiptList.get(Integer.parseInt((String) execution.getVariable("g_recpList_idx")));

					String apvLine = (String)execution.getVariable("g_appvLine");
					JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
					
					apvLineObj = CoviFlowWorkHelper.addReceiptDivision(apvLineObj, receipt, "0");
					
					execution.setVariable("g_appvLine", apvLineObj.toJSONString());
					execution.setVariable("g_isDistribution", "true");
					*/
					
					JSONArray params = new JSONArray();
					params.add(receiptParam);
					CoviFlowWSHelper.invokeApi("DISTRIBUTION", params);	
				}	
			}
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftLoopActingDept", e);
			throw e;
		}
		
		
	}
}
