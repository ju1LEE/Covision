/**
 * @Class Name : BasicLoopConsentC.java
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

public class BasicLoopConsentC implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicLoopConsentC.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicLoopConsentC");
		
		//Local 변수
		String isFinishedSteps = "false";
		
		//looping 처리
		
		//Bas_App_PrepareSubApprover
		//합의처 결재자 지정
		
		try{
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicLoopConsentC", e);
			throw e;
		}
		
	}
}
