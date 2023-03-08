/**
 * @Class Name : RequestTransmitterHandler.java
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
public class RequestTransmitterHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestTransmitterHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("RequestTransmitter");
		
		try{
			//Step 1. performer, workitem 할당.
			//LOG.info("Reserved performer를 할당");
			//변수 설정
			//PGlobal("TransmitterWIID").Value = Workitem.id
			//PGlobal("TransmitterPFID").Value = Workitem.performerId
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestTransmitterHandler", e);
			throw e;
		}
		
	}

}