/**
 * @Class Name : BasicRouteDocBox.java
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

public class BasicRouteDocBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteDocBox.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("BasicRouteDocBox");

		try{
			// destination
			String destination = "";

			String useDocBox = "false";
			//config 값에서 docbox 사용 여부를 읽어옴
			
			if (useDocBox.equalsIgnoreCase("true")) {
				destination = "BasicSetDocBox";
			} else {
				destination = "BasicEndApproved";
			}

			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteDocBox", e);
			throw e;
		}
		
		
	}
}
