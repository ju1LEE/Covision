/**
\ * @Class Name : DraftPreProcessBatchInnerPubSending.java
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftPreProcessBatchInnerPubSending implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPreProcessBatchInnerPubSending.class);
			
	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		
		//일괄분배선처리
		LOG.info("DraftPreProcessBatchInnerPubSending");
		
		try{
			//Dft_App_PreProcessBatchInnerPubSending
			//수협(문서유통Y & 웹기안기Y & 다안기안Y)
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			String IsUseScDistribution = "N";
			String IsUseHWPEditYN = "N";
			String IsUseMultiEdit = "N";
			String subTable1 = "";
			
			if(ctxJsonObj.containsKey("FormInfoExt")){
				JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
				// 문서유통
				if(formInfoExt.containsKey("scDistribution"))
					IsUseScDistribution = (String)formInfoExt.get("scDistribution");
				
				if(formInfoExt.containsKey("IsUseMultiEdit"))
					IsUseMultiEdit = (String)formInfoExt.get("IsUseMultiEdit");
				if(formInfoExt.containsKey("SubTable1"))
					subTable1 = (String)formInfoExt.get("SubTable1");
			}
						
			// 문서유통&다안기안 아닐 때
			if(!(IsUseScDistribution.equalsIgnoreCase("Y") && IsUseMultiEdit.equalsIgnoreCase("Y"))) {
				JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
				String formInstID = (String)ctxJsonObj.get("FormInstID");
				
				if(formData.containsKey("BodyData")) {
					JSONObject bodyData = (JSONObject)formData.get("BodyData");
					if(bodyData.containsKey("SubTable1")) {
						//양식 분배 작업 (FormInst 생성 및 ctx에 넣기)
						bodyData = CoviFlowWorkHelper.insertFormInstanceMulti(bodyData, formInstID);
						
						formData.put("BodyData", bodyData);
						ctxJsonObj.put("FormData", formData);
						ctxJsonObj.put("MultiEditReceiptGubun", "Y");
					}
				}
			}
			execution.setVariable("g_context", ctxJsonObj.toJSONString());	
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPreProcessBatchInnerPubSending", e);
			throw e;
		}
		
		
	}
}
