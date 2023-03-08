/**
 * @Class Name : LegacyStartExcutionListener.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.excutionlistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

@SuppressWarnings("serial")
public class LegacyStartExcutionListener implements ExecutionListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(LegacyStartExcutionListener.class);
	
	public void notify(DelegateExecution execution) throws Exception {
		
		LOG.info("Legacy Process Start.");
		/*
		 * legacy 프로세스 시작 시 처리가 들어 올 곳
		 * */
		
		try{
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("LegacyStart", e);
			throw e;
		}
		

	}
	
	
}