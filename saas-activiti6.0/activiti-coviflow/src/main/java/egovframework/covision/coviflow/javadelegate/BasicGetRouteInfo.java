/**
 * @Class Name : BasicGetRouteInfo.java
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

public class BasicGetRouteInfo implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(BasicGetRouteInfo.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("BasicGetRouteInfo");
		
		try{
			//다음 결재 단계 추출 
			String approvalResult = ""; //default값은 false
			String hasStepsFinished = "false";
			if(execution.hasVariable("g_basic_approvalResult"))
			{
				approvalResult = (String)execution.getVariable("g_basic_approvalResult"); 			
			}
			
			if(approvalResult != null && approvalResult.equalsIgnoreCase("rejected")){
				hasStepsFinished = "true";
			} else {
				//결재선에서 ou 추출
				int ouCnt = 0;
				String currentRoutType = "";
				String currentUnitType = "";
				if(ouCnt > 0){
					//currentRouteType과 currentUnitType값을 할당
				
				} else {
					hasStepsFinished = "true";
				}
				
				if(currentRoutType.equalsIgnoreCase("receive") && currentUnitType.equalsIgnoreCase("person")){
					hasStepsFinished = "true";
				} else {
					// 결재선에서 role 추출
					//"division/step/ou[(*/taskinfo/@kind!='review' and */taskinfo/@kind!='bypass' and  */taskinfo/@kind!='skip' and */taskinfo/@status='inactive') or (../@unittype='ou' and taskinfo/@status='inactive')]/role"
				}
				
				//신청서 결재단계 변경은 좀 더 분석이 필요함.
				
				//role 해당하는 조직정보를 select하여 performer를 할당
				
				//workitem 할당하는 부분도 좀 더 분석이 필요함.
				
			}
		
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("BasicGetRouteInfo", e);
			throw e;
		}
		
	}
}
