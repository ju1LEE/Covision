/**
 * @Class Name : BasicCcInfoBefore.java
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

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class BasicCcInfoBefore implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicCcInfoBefore.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		LOG.info("BasicCcInfoBefore");
		
		//분기처리 
		String hasReceiptBox = "false"; //default값은 false
		
		//결재선 parsing 
		//수신부서에 참조자, 참조부서가 있는 지
		//발신부서에 참조자, 참조부서가 있는 지
		
		int ccPersonCnt = 0;
		int ccOuCnt = 0;
		
		if((ccPersonCnt + ccOuCnt) > 0){
			//참조함 저장
			
			//메일 발송
			
		}
		
		try{
			CoviFlowWorkHelper.ccinfoBefore(execution, CoviFlowVariables.getGlobalContextStr(execution));
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicCcInfoBefore", e);
			throw e;
		}
		
	}
}
