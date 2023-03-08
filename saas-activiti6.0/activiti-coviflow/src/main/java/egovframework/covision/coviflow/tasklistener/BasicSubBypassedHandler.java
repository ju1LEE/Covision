/**
 * @Class Name : BasicSubBypassedHandler.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.tasklistener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

@SuppressWarnings("serial")
public class BasicSubBypassedHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicSubBypassedHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("BasicSubBypassed");
		
		try{
			//Step 1. performer, workitem 할당
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicSubBypassedHandler", e);
			throw e;
		}
		
		
	}

}