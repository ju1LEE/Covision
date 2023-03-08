/**
 * @Class Name : RequestPostDeptProcess.java
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

public class RequestPostDeptProcess implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestPostDeptProcess.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestPostDeptProcess");
		
		try{
			String approvalResult = "";
			if(execution.hasVariable("g_basic_approvalResult"))
			{
				approvalResult = (String)execution.getVariable("g_basic_approvalResult");
				if(approvalResult != null && approvalResult.equalsIgnoreCase("rejected")){
					//결재선 수정
					
					//PInstance.businessState = "02_02_02"
				} else {
					
					//PInstance.businessState = "02_01_02"
				}
			}
					
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestPostDeptProcess", e);
			throw e;
		}
				
				
	}
}
