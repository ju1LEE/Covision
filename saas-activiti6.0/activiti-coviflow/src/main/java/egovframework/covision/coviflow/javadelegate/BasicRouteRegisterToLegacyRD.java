/**
 * @Class Name : BasicRouteRegisterToLegacyRD.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class BasicRouteRegisterToLegacyRD implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteRegisterToLegacyRD.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteRegisterToLegacyRD");
		
		try{
			String destination = "";
			//분기처리 
			String isLegacy = "false";
			
			//form_info_ext값으로 부터 isLegacy값을 추출
			
			if("true".equalsIgnoreCase(isLegacy))
			{
				destination = "RegisterToLegacyRD"; 			
			} else {
				destination = "RegisterReceiptBox";
			}
				
			execution.setVariable("g_basic_destination", destination);	
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteRegisterToLegacyRD", e);
			throw e;
		}
		
		
	}
}
