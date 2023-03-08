/**
 * @Class Name : RequestReservedHandler.java
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;
import egovframework.covision.coviflow.util.CoviFlowVariables;
import egovframework.covision.coviflow.util.CoviFlowWorkHelper;

@SuppressWarnings("serial")
public class RequestReservedHandler implements TaskListener {

	private static final Logger LOG = LoggerFactory.getLogger(RequestReservedHandler.class);
	
	@Override
	public void notify(DelegateTask delegateTask) throws Exception{
		
		LOG.info("RequestReserved");
		
		try{
			//Step 1. performer, workitem을 할당.
			//Step 1. 예약 발송(상신) 처리.
			if("create".equals(delegateTask.getEventName())) {
				// 가상 workitem 등록 > 이후 스케쥴러가 jwf_workitem - limit 값 기준으로 complete 처리
				String reservedTime = "";
				
				JSONParser parser = new JSONParser();
				String ctx = CoviFlowVariables.getGlobalContextStr(delegateTask);
				JSONObject ctxJsonObj = (JSONObject)parser.parse(ctx.toString());
				JSONObject pDesc = (JSONObject)ctxJsonObj.get("ProcessDescription");
				
				reservedTime = (String)pDesc.get("DraftReservedTime"); // yyyyMMddHHmm
				
				java.util.Date limitTime = null;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
				limitTime = formatter.parse(reservedTime);
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

					// 사용자 Timezone 기준으로 GMT 변환.
					String userTimeZoneStr = (String)pDesc.get("ReservedTimeZone"); // ex) "09:00:00"
					if(!userTimeZoneStr.startsWith("-"))userTimeZoneStr = "+" + userTimeZoneStr;
					userTimeZoneStr = "GMT" + userTimeZoneStr.substring(0, userTimeZoneStr.length() - 3);
					
					TimeZone userTimeZone = TimeZone.getTimeZone(userTimeZoneStr);
					formatter.setTimeZone(userTimeZone);
				
					limitTime = formatter.parse(reservedTime);
					
					Calendar cal = Calendar.getInstance(userTimeZone);
					cal.setTimeInMillis(limitTime.getTime());
					limitTime = cal.getTime();
				}
				
				int piid = Integer.parseInt(delegateTask.getExecution().getProcessInstanceId());
				CoviFlowWorkHelper.setActivityReserved("예약기안", piid, delegateTask.getId(), ctx, "_RESERVE_", "예약기안", "A", 528, limitTime);//대기
			}
			//Step 2. Scheduler 가 Task 완료처리해주면 발생하는 Event. 예약기안(상신) 시 기안일자, 결재선 날짜 변경, workitem 도 완료처리
			else if("complete".equals(delegateTask.getEventName())){
				ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
				
				/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
				def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
				DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
				TransactionStatus status = txManager.getTransaction(def);*/
				try {
					CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
					
					// 1. 결재선 정보(기안일) 변경
					JSONParser parser = new JSONParser();
					int piid = Integer.parseInt(delegateTask.getProcessInstanceId().toString());
					String apvLine = (String)delegateTask.getVariable("g_appvLine");
					JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);

					JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
					int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
					// drafter.
					int divisionIndex = 0;
					int stepIndex = 0;

					// Change draft date ( task complete, received. )
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
						sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					}
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", "datecompleted", sdf.format(new Date()));
					apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, divisionIndex, stepIndex, "", "datereceived", sdf.format(new Date()));
					
					// Update jwf_domain Table.
					Map<String, Object> domainUMap = new HashMap<String, Object>();
					domainUMap.put("domainDataContext", apvLineObj.toJSONString());
					domainUMap.put("formInstID", fiid);
					processDAO.updateAllAppvLinePublic(domainUMap);
					
					// Change Task variable.
					delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());

					// Update jwf_workitem's Finished, Created (Draft) 
					// Get initiator's workItemId.
					String wiid = CoviFlowApprovalLineHelper.getOuAttr(apvLineObj, divisionIndex, stepIndex, "wiid");
					Map<String, Object> workitemUMap = new HashMap<>();
					workitemUMap.put("created", sdf.format(new Date()));
					workitemUMap.put("finished", sdf.format(new Date()));
					workitemUMap.put("workItemID", wiid);
					
					processDAO.updateWorkItemForDateChange(workitemUMap);	
					
					
					// 2. Update Current Workitem (Request Reserved)
					workitemUMap = new HashMap<>();
					workitemUMap.put("state", 288);
					workitemUMap.put("taskID", delegateTask.getId());
					workitemUMap.put("isMobile", false);
					workitemUMap.put("isBatch", false);
					
					processDAO.updateWorkItemForResult(workitemUMap);
					
					//txManager.commit(status);
				} catch(Exception e){
					LOG.error("RequestReservedHandler Error.", e);	
					//txManager.rollback(status);
				} finally{
					//if(context != null)((ClassPathXmlApplicationContext) context).close();
				}
			}
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestReservedHandler", e);
			throw e;
		}
		
	}

}