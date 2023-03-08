/**
 * @Class Name : BasicRouteRequestResponse.java
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

public class BasicRouteRequestResponse implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteRequestResponse.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteRequestResponse");
		
		try{
			//분기처리
			String destination = "";
			String hasRequestResponse = "false";
			
			if(hasRequestResponse.equalsIgnoreCase("true"))
			{
				//vrole insert
				
				destination = "BasicSenderDocumentBox";
			} else {
				destination = "BasicRouteReview";
			}
			
			execution.setVariable("g_basic_destination", destination);	
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteRequestResponse", e);
			throw e;
		}
		
		
	}
}
