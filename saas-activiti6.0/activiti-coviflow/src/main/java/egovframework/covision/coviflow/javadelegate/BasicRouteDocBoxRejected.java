/**
 * @Class Name : BasicRouteDocBoxRejected.java
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

public class BasicRouteDocBoxRejected implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteDocBoxRejected.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteDocBoxRejected");
		
		try{
			//docbox 값을 config에서 읽음
			String isUseDocBox = "false";
			String destination = "";
					
			//분기처리
			if(isUseDocBox.equalsIgnoreCase("true")){
				destination = "BasicSetDocBoxReject";
			} else {
				destination = "BasicEndRejected";
			}
			
			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteDocBoxRejected", e);
			throw e;
		}
		
		
	}
}
