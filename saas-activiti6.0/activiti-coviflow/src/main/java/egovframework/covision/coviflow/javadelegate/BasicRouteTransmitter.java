/**
 * @Class Name : BasicRouteTransmitter.java
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

public class BasicRouteTransmitter implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteTransmitter.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("BasicRouteTransmitter");

		try{
			// destination
			String destination = "";
			// form_info_ext에 isTransmit 값 가져오기
			String isTransmit = "false";

			if (isTransmit.equalsIgnoreCase("false")) {
				destination = "BasicRoutePreview";
			} else {
				destination = "BasicTransmitter";
			}

			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteTransmitter", e);
			throw e;
		}
				
	}
}
