/**
 * @Class Name : BasicRouteDocumentList.java
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

public class BasicRouteDocumentList implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteDocumentList.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicRouteDocumentList");
		
		try{
			//기밀문서인 경우 부서함에 넣지 않는다.
			//폐지부서인 경우 지나간다.
			String hasDocumentBox = "false";
			String destination = "";
			
			if(hasDocumentBox.equalsIgnoreCase("false"))
			{
				destination = "BasicRoutePAssistDocumentBox"; 			
			} else {
				destination = "BasicDocumentBox";
				//vrole을 추가
				
			}
			
			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteDocumentList", e);
			throw e;
		}
				
		
	}
}
