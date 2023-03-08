/**
 * @Class Name : DraftRegisterApprovedBoxForDistribute.java
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
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class DraftRegisterApprovedBoxForDistribute implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRegisterApprovedBoxForDistribute.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftRegisterApprovedBoxForDistribute");
		
		try{
			//기안부서 등록대장 등록/수신부서 존재 시 발신대장 등록
			CoviFlowWorkHelper.registerApprovedBox();
			
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRegisterApprovedBoxForDistribute", e);
			throw e;
		}
		
		
		
	}
}
