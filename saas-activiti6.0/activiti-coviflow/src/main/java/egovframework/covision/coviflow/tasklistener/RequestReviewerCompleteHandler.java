package egovframework.covision.coviflow.tasklistener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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

public class RequestReviewerCompleteHandler implements TaskListener{
	
	private static final Logger LOG = LoggerFactory.getLogger(RequestReviewerCompleteHandler.class);

	@Override
	public void notify(DelegateTask delegateTask) throws Exception {
		LOG.info("RequestReviewerCompleteHandler");
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		try {
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			JSONParser parser = new JSONParser();
			JSONObject ctxJsonObj = CoviFlowVariables.getGlobalContext(delegateTask);
			int fiid = Integer.parseInt(ctxJsonObj.get("FormInstID").toString());
			int piid = Integer.parseInt(delegateTask.getProcessInstanceId());
			String usid = ctxJsonObj.get("usid").toString();
			String formName = ctxJsonObj.get("FormName").toString();
			//정상적인 승인 처리
			//pending인 step ou의 taskid 값을 가지고서 api로 넘어오는 변수 값을 가져옴
			//결재선 수정에 대한 처리
			JSONObject apvLineObj = new JSONObject();
			Object apvLine = delegateTask.getVariable("g_appvLine");
			boolean isApvLineModified = false;
			if(apvLine instanceof LinkedHashMap){
				apvLineObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(apvLine));
			} else {
				apvLineObj = (JSONObject)parser.parse(apvLine.toString());
			}
			if(delegateTask.hasVariable("g_isChangeLine") && delegateTask.getVariable("g_isChangeLine").toString().equalsIgnoreCase("true")){
				isApvLineModified = true;
			}
			
			//결재선의 pending인 상태를 가져옴
			JSONArray stepObject = CoviFlowApprovalLineHelper.getReviewerStep(apvLineObj);
			
			//알림 메시지를 위한 parameter
			//subject
			String formSubject = delegateTask.getVariable("g_subject").toString();
			//알림 대상 - 결재단계의 모든 사람?
			ArrayList<HashMap<String,String>> receivers = CoviFlowApprovalLineHelper.getCompletedMessageReceivers(apvLineObj, false, "");
			
			for(int i = 0; i < stepObject.size(); i++)
			{
				int pendingStepIndex = Integer.parseInt(((JSONObject)stepObject.get(i)).get("stepIndex").toString());
				int pendingDivisionIndex = Integer.parseInt(((JSONObject)stepObject.get(i)).get("divisionIndex").toString());
				
				//해당 taskid에 대한 처리
				JSONObject pendingOu = (JSONObject)((JSONObject)stepObject.get(i)).get("ou");
				
				if(pendingOu.containsKey("taskid")){
					String taskid = (String)pendingOu.get("taskid");
					
					if(taskid.equalsIgnoreCase(delegateTask.getId().toString())){
						
						String actionMode = "";
						String actionComment = "";
						List<JSONObject> actionComment_Attach = new ArrayList<JSONObject>();
						String signImage = "";	
						String isMobile = "";
						String isBatch = "";
						JSONObject comment = new JSONObject();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
							sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
						}
						
						if(delegateTask.hasVariable("g_action_" + taskid))
						{
							actionMode = (String)delegateTask.getVariable("g_action_" + taskid);
							actionComment = (String)delegateTask.getVariable("g_actioncomment_" + taskid);
							actionComment_Attach = (ArrayList)delegateTask.getVariable("g_actioncomment_attach_" + taskid);
							signImage = (String)delegateTask.getVariable("g_signimage_" + taskid);	
							isMobile = (String)delegateTask.getVariable("g_isMobile_" + taskid);
							isBatch = (String)delegateTask.getVariable("g_isBatch_" + taskid);
							
							if(StringUtils.isNotBlank(actionComment)){
								comment.put("#text", actionComment);
							}
							
							//병렬 처리의 경우 삭제 해 버리면 step 상태값 처리가 되지 않음
							delegateTask.removeVariable("g_action_" + taskid);
							delegateTask.removeVariable("g_actioncomment_" + taskid);
							delegateTask.removeVariable("g_actioncomment_attach_" + taskid);
							delegateTask.removeVariable("g_signimage_" + taskid);
							delegateTask.removeVariable("g_isMobile_" + taskid);
							delegateTask.removeVariable("g_isBatch_" + taskid);
							
							if(actionMode.equalsIgnoreCase("APPROVAL")){
								CoviFlowWorkHelper.processReviewResult(apvLineObj, taskid, isMobile, isBatch);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, pendingDivisionIndex, pendingStepIndex, taskid, CoviFlowVariables.APPV_REVIEW);
								apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, pendingDivisionIndex, pendingStepIndex, taskid, "datecompleted", sdf.format(new Date()));
								
								//comment
								if(StringUtils.isNotBlank(actionComment)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, pendingDivisionIndex, pendingStepIndex, taskid, "comment", comment);
								}
								
								if(actionComment_Attach.size() > 0) { // 의견 첨부가 있으면
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, pendingDivisionIndex, pendingStepIndex, taskid, "comment_fileinfo", actionComment_Attach);
								}
								
								//signimage
								if(StringUtils.isNotBlank(signImage)){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, pendingDivisionIndex, pendingStepIndex, taskid, "customattribute1", signImage);
								}
								
								//mobile
								if(isMobile.equalsIgnoreCase("Y")){
									apvLineObj = CoviFlowApprovalLineHelper.setOuTask(apvLineObj, pendingDivisionIndex, pendingStepIndex, taskid, "mobileType", "y");
								}
								
								if(delegateTask.hasVariable("g_basic_approvalResult") && delegateTask.getVariable("g_request_approvalResult") != null){
									if(delegateTask.getVariable("g_basic_approvalResult").toString().equalsIgnoreCase("confirm")
											||delegateTask.getVariable("g_basic_approvalResult").toString().isEmpty()){
										delegateTask.setVariable("g_basic_approvalResult", "confirm");	
									}
								} else {
									delegateTask.setVariable("g_basic_approvalResult", "confirm");	
								}
							}
						}
					}
				}
			}
			
			//action 삭제
			CoviFlowWorkHelper.removeActionAll(delegateTask);
			
			Map<String, Object> domainUMap = new HashMap<String, Object>();
			domainUMap.put("domainDataContext", apvLineObj.toJSONString());
			domainUMap.put("formInstID", fiid);
			processDAO.updateAllAppvLinePublic(domainUMap);
			
			delegateTask.setVariable("g_appvLine", apvLineObj.toJSONString());
			
			//txManager.commit(status);
		}catch(Exception e){
			//txManager.rollback(status);
			
			CoviFlowLogHelper.insertErrorLog(delegateTask, e, "ERROR");
			LOG.error("RequestReviewerCompleteHandler", e);
			
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
}
