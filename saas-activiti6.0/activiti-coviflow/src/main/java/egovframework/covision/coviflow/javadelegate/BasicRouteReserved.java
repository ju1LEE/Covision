/**
 * @Class Name : BasicRouteReserved.java
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;

public class BasicRouteReserved implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteReserved.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteReserved");
		
		try{
			String destination = "";
			
			String isReserved = "N";
			String reservedTime = "";
			
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(execution);
			JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
			
			// IsReserved 명칭을 "보류" 로 사용하고 있어 IsDraftReserved 로 사용.
			isReserved = (String)pDesc.get("IsDraftReserved");
			reservedTime = (String)pDesc.get("DraftReservedTime"); // yyyyMMddHHmm
			
			if(((String)ctxJsonObj.get("processDefinitionID")).startsWith("request1:")){
				isReserved = "N"; // 신청서 프로세스는 별도처리한다. 
			}
			
			if(!"Y".equalsIgnoreCase(isReserved)){
				destination = "BasicRouteTransmitter";			
			} else {
				// 예약기안 Pending 처리 -> BasicReserved task 생성 Event(BasicReservedHandler.java) 에서 workitem.limit 값 셋팅.
				destination = "BasicReserved";
			}
			
			execution.setVariable("g_basic_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteReserved", e);
			throw e;
		}
		
		
	}
}
