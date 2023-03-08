/**
 * @Class Name : RequestGetRouteInfo.java
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

public class RequestGetRouteInfo implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestGetRouteInfo.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestGetRouteInfo");
		
		try{
			//공통적으로 호출되는 함수 사용
			CoviFlowWorkHelper.getRouteInfo();
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestGetRouteInfo", e);
			throw e;
		}
				
		
	}
}
