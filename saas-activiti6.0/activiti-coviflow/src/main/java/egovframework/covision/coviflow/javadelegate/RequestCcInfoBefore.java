/**
 * @Class Name : RequestCcInfoBefore.java
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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class RequestCcInfoBefore implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestCcInfoBefore.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("CcInfoBefore");
		
		try{
			//참조함 저장
			CoviFlowWorkHelper.ccinfoBefore(execution, CoviFlowVariables.getGlobalContextStr(execution));
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestCcInfoBefore", e);
			throw e;
		}
		
	}
	
}
