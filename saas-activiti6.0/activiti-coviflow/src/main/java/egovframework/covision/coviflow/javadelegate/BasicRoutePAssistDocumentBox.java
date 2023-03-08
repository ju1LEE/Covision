/**
 * @Class Name : BasicRoutePAssistDocumentBox.java
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

public class BasicRoutePAssistDocumentBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRoutePAssistDocumentBox.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRoutePAssistDocumentBox");
		
		try{
			//분기처리 
			//기밀문서인 경우 부서함에 넣지 않는다.
			//폐지부서인 경우 지나간다.
			String isSecure = "false"; 
			String destination = "";
			
			if(isSecure.equalsIgnoreCase("true"))
			{
				destination = "BasicPAssistDocumentBox"; 			
			} else {
				//분기처리 더 추가가 필요함.
				
				destination = "BasicRouteRegisterToEDMS";
			}
			
			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRoutePAssistDocumentBox", e);
			throw e;
		}
				
		
	}
}
