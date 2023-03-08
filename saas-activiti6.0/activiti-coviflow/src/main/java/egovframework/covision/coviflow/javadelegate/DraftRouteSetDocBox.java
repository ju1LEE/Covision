/**
 * @Class Name : DraftRouteSetDocBox.java
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

public class DraftRouteSetDocBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteSetDocBox.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteSetDocBox");

		try{
			String destination = "";
			//configuration에서 값을 읽음
			String useDocBox = "false";
			if(useDocBox.equalsIgnoreCase("true"))
			{
				destination = "DraftSetDocBox"; 			
			} else {
				destination = "DraftEnd";
			}
				
			execution.setVariable("g_draft_destination", destination);
			
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteSetDocBox", e);
			throw e;
		}
		
		

	}
}
