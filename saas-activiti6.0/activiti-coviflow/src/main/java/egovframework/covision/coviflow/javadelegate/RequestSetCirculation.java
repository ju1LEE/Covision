/**
 * @Class Name : RequestSetCirculation.java
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

public class RequestSetCirculation implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestSetCirculation.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestSetCirculation");
		
		try{
			//참조자/참조부서 회람함에 추가
			//개인 참조 등록
			//부서참조 등록
				
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestSetCirculation", e);
			throw e;
		}
		
	}
}
