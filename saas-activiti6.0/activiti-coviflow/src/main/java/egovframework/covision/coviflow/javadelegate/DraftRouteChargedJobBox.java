/**
 * @Class Name : DraftRouteChargedJobBox.java
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

public class DraftRouteChargedJobBox implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftRouteChargedJobBox.class);

	public void execute(DelegateExecution execution) throws Exception {

		LOG.info("DraftRouteChargedJobBox");

		try{
			//destination
			String destination = "";
			
			//특정권한함(담당업무완료함) 설정
			//분기처리 추가 분석 필요함
			int vRoleMemberCnt = 0;
			
			if(vRoleMemberCnt == 0)
			{
				destination = "DraftRouteLegacy"; 			
			} else {
				destination = "DraftChargedJobBox";
			}
				
			execution.setVariable("g_draft_destination", destination);
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftRouteChargedJobBox", e);
			throw e;
		}
		
		

	}
}
