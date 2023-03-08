/**
 * @Class Name : BasicReceiptBoxCHandler.java
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
public class BasicReceiptBoxCHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(BasicReceiptBoxCHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("BasicReceiptBoxC");
		
		try{
			//Bas_SP_TAPr_ProcessReceiptBoxC 처리 구현
			
			//Step 1. performer, workitem을 할당.
			//LOG.info("ReceiptBox performer를 할당");
			
			//Step 2. 배치로 담당자가 할당되는 경우에 대한 처리 파악이 필요 - Bas_TWPo_ProcessReceiptBox 스크립트를 다시 분석할 것
			//결재선을 새로 생성하는 부분이 있음.
			
			//Step 3. mail을 발송 - Bas_TWPr_RecipientSendMailWorkItem
			//LOG.info("ReceiptBox 메일을 발송");

		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("BasicReceiptBoxCHandler", e);
			throw e;
		}
		
	}

}