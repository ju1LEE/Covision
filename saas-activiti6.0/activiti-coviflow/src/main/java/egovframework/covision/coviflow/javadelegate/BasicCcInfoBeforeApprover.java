/**
 * @Class Name : BasicCcInfoBeforeApprover.java
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

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

public class BasicCcInfoBeforeApprover implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicCcInfoBeforeApprover.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		LOG.info("BasicCcInfoBeforeApprover");
		
		//분기처리 
		String hasReceiptBox = "false"; 
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
			String ctxJsonStr = CoviFlowVariables.getGlobalContextStr(execution);
			CoviFlowWorkHelper.ccinfoBefore(execution, ctxJsonStr);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicCcInfoBeforeApprover", e);
			throw e;
		}
		
	}
}
