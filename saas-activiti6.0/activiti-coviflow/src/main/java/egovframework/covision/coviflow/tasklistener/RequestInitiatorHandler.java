/**
 * @Class Name : RequestInitiatorHandler.java
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestInitiatorHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestInitiatorHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception {
		
		LOG.info("RequestInitiator");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			
			//결재선 변경
			String apvLine = (String)delegateTask.getVariable("g_appvLine");
			JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
			String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
			JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			//결재선 수정 - g_appvLine만 사용
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, 0, 0, "", CoviFlowVariables.APPV_PENDING);
			//결재선의 pending인 상태를 가져옴
			JSONObject pendingObject = (JSONObject)CoviFlowApprovalLineHelper.getPendingStep(apvLineObj);
			JSONObject pendingStep = (JSONObject)pendingObject.get("step");
			int stepSize = Integer.parseInt(pendingObject.get("stepSize").toString());
			
			//전결 처리
			boolean isAuthorized = false;
			isAuthorized = CoviFlowApprovalLineHelper.isAuthorized(pendingStep);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//workitem, workitem desc, performer 생성 및 update
			apvLineObj = CoviFlowWorkHelper.setActivity("initiator", delegateTask, ctx, "T006");
			
			
			//첫번째 결재자 or 협조자 or 합의자는 제외 - 미결함에 문서도착
			
			//예고 대상이 한명도 없는 경우 system를 default로 넣어준다. 
			
			//전달함 설정
						
			//complete 이후 후처리
			//결재선 수정 - g_appvLine만 사용
			String isMobile = ctxJsonObj.get("isMobile").toString();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, 0, 0, "", CoviFlowVariables.APPV_COMP);
			apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, 0, 0, "", "datecompleted", sdf.format(new Date()));
			
			//mobile
			if(isMobile.equalsIgnoreCase("Y")){
				apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, 0, 0, "", "mobileType", "y");
			}
			
			if(stepSize > 1 && !isAuthorized){
				apvLineObj = CoviFlowApprovalLineHelper.setStepTask(apvLineObj, 0, 1, CoviFlowVariables.APPV_PENDING);
				apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, 0, 1, "", CoviFlowVariables.APPV_PENDING);
			}
			
			//결재선 update
			if(delegateTask.hasVariable("g_isDistribution")){
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("processID", Integer.parseInt(delegateTask.getProcessInstanceId().toString()));
				processDAO.updateAppvLinePublic(domainUMap);
			} else {
				Map<String, Object> domainUMap = new HashMap<String, Object>();
				domainUMap.put("domainDataContext", apvLineObj.toJSONString());
				domainUMap.put("formInstID", fiid);
				processDAO.updateAllAppvLinePublic(domainUMap);
			}
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);

			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestInitiatorHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		//기안 task complete 처리
		//async로 처리되는 것 같은 데 문제 여부 확인이 필요.
		//state값이 288로 들어가는 문제가 있음
		org.activiti.engine.ProcessEngines.getDefaultProcessEngine().getTaskService().complete(delegateTask.getId());
		LOG.info("RequestInitiator task completed");
	}

}