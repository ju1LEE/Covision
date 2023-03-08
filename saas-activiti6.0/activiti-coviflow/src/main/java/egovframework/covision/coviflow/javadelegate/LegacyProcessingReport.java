/**
 * @Class Name : LegacyProcessingReport.java
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

public class LegacyProcessingReport implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(LegacyProcessingReport.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("LegacyProcessingReport");
		
		try{
			//레포트 생성 처리
			//LOB_리포트데이터처리
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("LegacyProcessingReport", e);
			throw e;
		}
		
		
	}
}
