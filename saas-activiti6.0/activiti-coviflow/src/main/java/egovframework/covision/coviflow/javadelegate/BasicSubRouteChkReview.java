/**
 * @Class Name : BasicSubRouteChkReview.java
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

public class BasicSubRouteChkReview implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicSubRouteChkReview.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicSubRouteChkReview");
		
		try{
			//분기조건이 Looping의 조건과 같다면 필요 없을 수 있는 task
			
			//destination
			String destination = "";
			String isFinishedSteps = "false";
					
			//분기처리
			if("true".equalsIgnoreCase(isFinishedSteps)){
				destination = "BasicSubRouteBypassed";
			} else {
				destination = "BasicSubApprover";
			}
			
			execution.setVariable("g_basic_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicSubRouteChkReview", e);
			throw e;
		}
		
		
	}
}
