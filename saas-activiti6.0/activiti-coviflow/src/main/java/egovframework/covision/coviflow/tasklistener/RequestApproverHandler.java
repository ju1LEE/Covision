/**
 * @Class Name : RequestApproverHandler.java
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
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestApproverHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestApproverHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("RequestApprover");
		
		try {
			//workitem, workitem desc, performer 생성 및 update
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			CoviFlowWorkHelper.setActivity("approver", delegateTask, ctx, "T000");
						
			/*
			 * 추가 분석 및 정리가 필요
			 * */
			//결재자 전처리
			CoviFlowWorkHelper.processNormal();

			//메일발송
			CoviFlowWorkHelper.sendMailWorkitem();
			
			//결재 행위 후처리
			CoviFlowWorkHelper.processWorkitem();
		} catch (Exception e) {
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestApproverHandler", e);
			throw e;
		}
		
	}

}