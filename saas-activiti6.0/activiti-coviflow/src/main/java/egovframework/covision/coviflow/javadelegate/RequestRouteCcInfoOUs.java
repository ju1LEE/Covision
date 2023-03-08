/**
 * @Class Name : RequestRouteCcInfoOUs.java
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

public class RequestRouteCcInfoOUs implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteCcInfoOUs.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteCcInfoOUs");
		
		try{
			//특정권한함(담당업무완료함) 설정
			int vRoleMemberCnt = 0;
			
			String destination = "";
			
			if(vRoleMemberCnt > 0){
				destination = "RequestCcInfoOUs";
			} else {
				destination = "RequestRouteReview";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteCcInfoOUs", e);
			throw e;
		}
		
		
	}
}
