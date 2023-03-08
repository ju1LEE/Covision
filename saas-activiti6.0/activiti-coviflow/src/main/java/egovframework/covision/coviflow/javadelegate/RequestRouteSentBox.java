/**
 * @Class Name : RequestRouteSentBox.java
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

public class RequestRouteSentBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRouteSentBox.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRouteSentBox");
		
		try{
			//dscr로 부터 secure_doc 값을 가져온다
			String isSecureDoc = "false"; 
			String destination = "";
			
			if(isSecureDoc.equalsIgnoreCase("true")){
				//분기처리 추가 파악 필요.
				destination = "RequestReceiptLoop";
			} else {
				destination = "RequestSentBox";
			}
			
			execution.setVariable("g_request_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRouteSentBox", e);
			throw e;
		}
		
		
	}
}
