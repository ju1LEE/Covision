/**
 * @Class Name : LegacyWS.java
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

public class LegacyWS implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(LegacyWS.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("LegacyWS");
		
		try{
			//전자결재 webservice 연동
			//LOB_전자결재WS연동
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("LegacyWS", e);
			throw e;
		}
		
	}
}
