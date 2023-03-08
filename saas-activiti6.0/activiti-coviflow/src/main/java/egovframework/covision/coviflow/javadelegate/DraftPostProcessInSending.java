/**
 * @Class Name : DraftPostProcessInSending.java
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

public class DraftPostProcessInSending implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(DraftPostProcessInSending.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("DraftPostProcessInSending");
		
		try{
			//Looping 후처리
			
			//부서분배후처리
			//Dft_App_PostProcessInSending
			
			//execution.setVariable("input", var);

			String destination = "";
			int subSize = Integer.parseInt((String) execution.getVariable("g_subTable_size"));
			int subIdx = Integer.parseInt((String) execution.getVariable("g_subTable_idx"));
			
			if(subIdx+1 < subSize) {
				execution.setVariable("g_subTable_idx", String.valueOf(subIdx+1));
				//문서유통다안기안으로 추가: 비워주지않으면 대외수신/종이수신도 직전 대내수신안의 정보로 processDescription에 insert됨.
				execution.setVariable("g_subTable_param", null);
				destination = "DraftLoopAllSentDept";
			} else {
				destination = "DraftRouteSentBox";
			}
			
			execution.setVariable("g_draft_destination", destination);	
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("DraftPostProcessInSending", e);
			throw e;
		}
			
		
	}
}
