/**
 * @Class Name : DraftRegistLegacyRD.java
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

public class DraftRegistLegacyRD implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRegistLegacyRD.class);
			
	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRegistLegacyRD");
		
		try{
			//apvMode에 대한 추가 분석 및 수정이 필요함
			// 연동 이후 기안 실패할 것을 고려하여 위치 이동
			//CoviFlowWorkHelper.callLegacy(execution, "DRAFT", null, null, null);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRegistLegacyRD", e);
			throw e;
		}
		
	}
}
