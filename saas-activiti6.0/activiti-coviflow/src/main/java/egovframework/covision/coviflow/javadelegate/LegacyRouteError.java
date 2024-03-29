/**
 * @Class Name : LegacyRouteError.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class LegacyRouteError implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(LegacyRouteError.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("LegacyRouteError");

		try{
			String destination = "";
			String retryLegacy = "";
			if(StringUtils.isNotBlank(retryLegacy))
			{
				destination = "LegacyProcessCharge"; 			
			} else {
				destination = "LegacyEnd";
			}
				
			execution.setVariable("g_legacy_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("LegacyRouteError", e);
			throw e;
		}
		

	}
}
