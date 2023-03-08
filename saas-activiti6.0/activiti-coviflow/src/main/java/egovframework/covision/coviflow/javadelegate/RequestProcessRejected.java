/**
 * @Class Name : RequestProcessRejected.java
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

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import org.springframework.context.ApplicationContext;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;

public class RequestProcessRejected implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestProcessRejected.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestProcessRejected");
		
		try{

			ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
			
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			//수신부서반려처리 상태
			String reqResult = "";
			if(execution.hasVariable("g_request_approvalResult"))
			{
				reqResult = (String) execution.getVariable("g_request_approvalResult"); 
				if((reqResult == null || reqResult.isEmpty()) && execution.hasVariable("g_basic_approvalResult")) {
					reqResult = (String) execution.getVariable("g_basic_approvalResult");
				}

				if(reqResult != null && reqResult.equalsIgnoreCase("rejected")){

					int piid = Integer.parseInt(execution.getProcessInstanceId());
					// WorkItem - businessState 변경
					Map<String, Object> procWorkitemArchMap = new HashMap<String, Object>();
					procWorkitemArchMap.put("piBusinessState", "02_02");
					procWorkitemArchMap.put("processID", piid);
					procWorkitemArchMap.put("parentYN", "Y"); // sub 프로세스도 같이 완료처리 하기 위한 옵션
					
					processDAO.updateWorkitemArchive(procWorkitemArchMap);
					
					// Process - businessState 변경
					Map<String, Object> procArchMap = new HashMap<String, Object>();
					procArchMap.put("businessState", "02_02_02");
					procArchMap.put("processState", 528);
					procArchMap.put("processID", piid);
					procArchMap.put("parentYN", "Y"); // sub 프로세스도 같이 완료처리 하기 위한 옵션
					
					processDAO.updateProcessArchive(procArchMap);
					
					//결재반려메일 발송
				}
			}
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestProcessRejected", e);
			throw e;
		}
		
		
	}
}
