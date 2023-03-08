/**
 * @Class Name : DraftPreProcessReceiptGroup.java
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

public class DraftPreProcessReceiptGroup implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPreProcessReceiptGroup.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		//수신전처리
		LOG.info("DraftPreProcessReceiptGroup");
		
		try{
			//배포목록 분배 전 처리
			//Dft_App_PreProcessReceiptGroup
						
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPreProcessReceiptGroup", e);
			throw e;
		}
		
		
		
	}
}
