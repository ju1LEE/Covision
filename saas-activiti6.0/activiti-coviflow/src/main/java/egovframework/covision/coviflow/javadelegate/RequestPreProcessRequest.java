/**
 * @Class Name : RequestPreProcessRequest.java
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

public class RequestPreProcessRequest implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestPreProcessRequest.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestPreProcessRequest");
		
		try{
			
			//문서이관 여부설정
			
			//양식Key 넣어주기
			
			//첫번째 결재자 or 협조자 or 합의자는 제외 - 미결함에 문서도착
			
			//예고 대상이 한명도 없는 경우 system를 default로 넣어준다.  
			
			//전달자 처리
			
			
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestPreProcessRequest", e);
			throw e;
		}
		
		
	}
}
