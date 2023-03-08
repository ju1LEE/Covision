/**
 * @Class Name : RequestRegisterToLegacy.java
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

public class RequestRegisterToLegacy implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRegisterToLegacy.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRegisterToLegacy");
		
		try{
			//차후 필요한 경우 subprocess로 대체 할 것
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRegisterToLegacy", e);
			throw e;
		}
		
		
	}
}
