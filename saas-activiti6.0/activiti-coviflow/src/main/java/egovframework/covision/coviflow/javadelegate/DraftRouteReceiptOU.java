/**
 * @Class Name : DraftRouteReceiptOU.java
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class DraftRouteReceiptOU implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteReceiptOU.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteReceiptOU");

		try{
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			String destination = "";
			String hasReceiptOU = "false";
			//배포 처리 분기
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			if(formInfoExt.containsKey("ReceiptList")){
				String receiptList = formInfoExt.get("ReceiptList").toString();
				String receiptOuList = (receiptList.split("@").length > 0 ? receiptList.split("@")[0] : "" );
				if(!StringUtils.isBlank(receiptOuList) || receiptList.split("@").length > 2){ // 공용배포처가 있는 경우에도 실행
					hasReceiptOU = "true";
				}
			}
			
			if(hasReceiptOU.equalsIgnoreCase("true"))
			{
				//PGlobal("HasAllSent").Value = False
			    //PGlobal("ReceiptList").Value = PGlobal("GroupReceiptList").Value.Split("@")(0)
				destination = "DraftLoopAllSentOU"; 			
			} else {
				destination = "DraftRouteSentBox";
			}
				
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteReceiptOU", e);
			throw e;
		}
				

	}
}
