/**
 * @Class Name : BasicFinalizeApproval.java
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

public class BasicFinalizeApproval implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicFinalizeApproval.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicFinalizeApproval");
		
		//수신결재 완료
		
		//기안결재 완료
		
		//결재선 변경
		
		//메일 발송
		
		try{
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicFinalizeApproval", e);
			throw e;
		}
		
	}
}
