/**
 * @Class Name : BasicChargeHandler.java
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
public class BasicChargeHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicChargeHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("Charge");
		
		try{
			//Step 1. performer를 할당.
			//LOG.info("Charge performer를 할당");
			
			//1. 결재자가 부서장인 경우 기존에 할당된 조직장과 현재 할당된 조직장을 비교한다.
	        //2. 현재 조직장이 존재하면서 기 할당 조직장과 틀린경우 할당 조직장을 현재 조직장으로 변경한다.
	        //3. WORKITEM을 할당받은 PERFORMER가 대결사용을(DEPUTY_USAGE : '1') 하면서 대결자를 지정한 경우 대결자를 지정한다.
			
			
			//Step 2. 배치로 담당자가 할당되는 경우에 대한 처리 파악이 필요 - Bas_TWPo_ProcessCharge 스크립트를 다시 분석할 것
			//결재선을 새로 생성하는 부분이 있음.
			
			//Step 3. mail을 발송
			//LOG.info("Charge 메일을 발송");

		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicChargeHandler", e);
			throw e;
		}
		
		
	}

}