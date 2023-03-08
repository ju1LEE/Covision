/**
 * @Class Name : BasicRouteApprovalResult.java
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;

public class BasicRouteApprovalResult implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRouteApprovalResult.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("BasicRouteApprovalResult");

		try{
			// destination
			String destination = "";
			String approvalResult = "";
			if (execution.hasVariable("g_basic_approvalResult")) {
				approvalResult = (String) execution.getVariable("g_basic_approvalResult");
			}

			if (approvalResult != null && (approvalResult.equalsIgnoreCase("rejected")||approvalResult.equalsIgnoreCase("cancelled"))) {
				destination = "BasicProcessReject";
			} else if(approvalResult != null && approvalResult.equalsIgnoreCase("rejectedtodept")) {
				// 부서 or 사람 or 담당업무함
				if(execution.hasVariable("g_jfid") && execution.getVariable("g_jfid") != null && StringUtils.isNotBlank(execution.getVariable("g_jfid").toString())) {
					destination = "BasicChargeJob";
				}
				else {
					if(execution.hasVariable("g_hasReceiptType") && execution.getVariable("g_hasReceiptType").equals("person")) {
						destination = "BasicChargePerson";
					} else {
						destination = "BasicReceiptBox";
					}
				}
				
				execution.removeVariable("g_basic_approvalResult");
				execution.setVariable("g_basic_hasStepsFinished", "false");
			} else {
				destination = "BasicFinalizeApproval";
			}

			execution.setVariable("g_basic_destination", destination);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicRouteApprovalResult", e);
			throw e;
		}
		
		
	}
}
