/**
 * @Class Name : DraftRouteReceiptPerson.java
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

public class DraftRouteReceiptPerson implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteReceiptPerson.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteReceiptPerson");

		try{
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			
			String destination = "";
			String hasReceiptPerson = "false";
			//배포 처리 분기
			JSONObject formData = (JSONObject)ctxJsonObj.get("FormData");
			JSONObject formInfoExt = (JSONObject)ctxJsonObj.get("FormInfoExt");
			if(formInfoExt.containsKey("ReceiptList")){
				String receiptList = formInfoExt.get("ReceiptList").toString();
				String receiptPersonList = (receiptList.split("@").length > 1 ? receiptList.split("@")[1] : "" );
				if(!StringUtils.isBlank(receiptPersonList) || receiptList.split("@").length > 2){ // 공용배포처가 있는 경우에도 실행
					hasReceiptPerson = "true";
				}
			}
			
			// [2020-02-18] CP 수신공람 기능추가: destination 분기 사람 or 부서로 수정함. 
			if(hasReceiptPerson.equalsIgnoreCase("true")){
				//String isDoxBoxRE = "false";
				//추가적인 분석 필요함
				/*
				 * if(isDoxBoxRE.equalsIgnoreCase("true")) { destination =
				 * "DraftPreProcessReceiptPerson"; } else { destination = "DraftLoopAllSent"; }
				 */
				destination = "DraftLoopAllSent";
			} else {
				destination = "DraftRouteReceiptOU";
			}
				
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteReceiptPerson", e);
			throw e;
		}
		
		

	}
}
