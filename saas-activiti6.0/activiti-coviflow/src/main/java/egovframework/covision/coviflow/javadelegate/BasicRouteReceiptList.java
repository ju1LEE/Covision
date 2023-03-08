/**
 * @Class Name : BasicRouteReceiptList.java
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

import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class BasicRouteReceiptList implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteReceiptList.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteReceiptList");
		
		try{
			JSONParser parser = new JSONParser();
			
			Object apvLine = execution.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			
			//분기처리 
			String hasReceipt = "false"; //default값은 false
			if(execution.hasVariable("g_hasReceipt")){
				hasReceipt = (String)execution.getVariable("g_hasReceipt");
			}
			
			//destination
			String destination = "";
			
			if(hasReceipt.equalsIgnoreCase("false")){
				destination = "BasicInitiator";
			} else {
				
				//Step1. 결재선을 parsing하여 전체 결재단계 갯수 및 현재 상태를 businessdata1에 저장함
				
				//Step2. PGlobal("ReceiptBoxSubKind").Value 에 따른 상태(businnessState)변경이 필요합니다. 합의함, 협조함 등. 기본은 수신함 상태
				
				//분기처리
				int divisionIndex = 1;
				JSONObject activeDivision = (JSONObject)CoviFlowApprovalLineHelper.getReceiveDivision(apvLineObj);
				if(activeDivision.get("isDivision").toString().equalsIgnoreCase("true")){
					divisionIndex = Integer.parseInt(activeDivision.get("divisionIndex").toString());
				}
				
				if(CoviFlowApprovalLineHelper.HasPerson(apvLineObj, divisionIndex, 0, 0)){
					destination = "BasicChargePerson";
				} else {
					if(execution.hasVariable("g_jfid") && execution.getVariable("g_jfid") != null && StringUtils.isNotBlank(execution.getVariable("g_jfid").toString())) {
						destination = "BasicChargeJob";
					} else{
						destination = "BasicReceiptBox";	
					}
				}
				
				//아래는 추가적으로 처리가 필요한 부분
				/*
				if(StringUtils.isNotBlank((String) execution.getVariable("ReceiptBoxId")))
				{
					//oFormInfoExt.SelectSingleNode("scDocBoxRE")
					String scDocBoxRE = "";
					boolean bDocBoxRE = false;
					if(StringUtils.isNotBlank(scDocBoxRE))
					{
						if(scDocBoxRE == "1"){
							bDocBoxRE = true;
						}
					}
					
					//분기처리
					if(bDocBoxRE){
						//PGlobal("ReceiptBoxId").Value.Split(";") split 처리
						
						//insert vrole
						
						destination = "BasicCoordiReceiptBox";
						
					} else {
						
						//insert vrole
						
						destination = "BasicReceiptBox";
					}
					
					
				} else {
					//분기처리
					if(StringUtils.isNotBlank((String) execution.getVariable("JFID")))
					{
						destination = "BasicChargeJob";
						
						
					} else {
						//결재선에 담당자 지정이 있고, forminfoExt에 담당자가 있으면
						boolean bChargePerson = false;
						//oApvList.SelectSingleNode("steps/division[@divisiontype='receive' and taskinfo/@status='pending']/step/ou/person[taskinfo/@status='pending']")
						
						//oFormInfoExt.SelectSingleNode("ChargePerson")
						
						//setVariable JFID값
						execution.setVariable("g_basic_JFID", jfid);
						
						//분기처리 - 담당함, 담당자?
						if(bChargePerson){
							destination = "BasicChargePerson";
						} else {
							destination = "BasicCharge";
						}
						
					}
					
				}
				*/
				
			}
			
			execution.setVariable("g_basic_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteReceiptList", e);
			throw e;
		}
		
		
	}
}
