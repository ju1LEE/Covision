/**
 * @Class Name : RequestRouteConfirmor.java
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

public class RequestRouteConfirmor implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteConfirmor.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteConfirmor");
		
		try{
			//공람자 처리
			//공람대상이 없는 경우 system을 default로 넣어준다
			int vRoleMemberCnt = 0;
			
			String destination = "";
			
			if(vRoleMemberCnt > 0){
				destination = "RequestConfirmors";
			} else {
				destination = "RequestRouteSetDocBox";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteConfirmor", e);
			throw e;
		}
		
	}
}
