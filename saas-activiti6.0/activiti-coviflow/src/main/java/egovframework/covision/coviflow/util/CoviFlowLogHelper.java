/**
 * @Class Name : CoviFlowLogHelper.java
 * @Description : 
 * @Modification Information 
 * @ 2017.04.03 최초생성
 *
 * @author 코비젼 연구소
 * @since 2017.04.03
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.springframework.security.crypto.codec.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;

public class CoviFlowLogHelper {

	public static void insertErrorLog(DelegateExecution delegate, Exception ex, String logType) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		
		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			if (delegate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
				}
				
				Map<String, Object> logMap = new HashMap<String, Object>();
				logMap.put("processDefID", delegate.getProcessDefinitionId());
				logMap.put("processInsID", delegate.getProcessInstanceId());
				logMap.put("executionID", delegate.getId());
				
				//task id
				logMap.put("taskID", "");
				//ip
				InetAddress ip;
				try {
					ip = InetAddress.getLocalHost();
					if(ip != null){
						logMap.put("serverIP", ip.getHostAddress());
					}
				} catch (UnknownHostException unKnownEx) {
					unKnownEx.printStackTrace();
					logMap.put("serverIP", "");
				}
				
				logMap.put("errorKind", logType);
				logMap.put("errorTime", sdf.format(new Date()).toString());
				//error message
				if(ex.getMessage() != null){
					logMap.put("errorMessage", ex.getMessage());
					//stacktrace
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					
					byte[] stackTraceByte = sw.toString().getBytes("UTF-8");
					if(stackTraceByte.length > 65535) {
					logMap.put("errorStackTrace", new String (stackTraceByte, 0, 65535));
					}else {
						logMap.put("errorStackTrace", new String (stackTraceByte));
					}
					//class name
					logMap.put("errorClass", ex.getClass().toString());
					
					pw.close();
					sw.close();
				} else {
					logMap.put("errorMessage", ex.toString());
					logMap.put("errorStackTrace", ""); // mssql 오류로 인해 추가함.
				}
				
				processDAO.insertErrorLog(logMap);
				
				//state update 부분을 추가 할 것 - 275
				if(StringUtils.isNotBlank(delegate.getProcessInstanceId().toString())){
					Map<String, Object> procUMap = new HashMap<String, Object>();
					procUMap.put("processState", 275);
					procUMap.put("processID", delegate.getProcessInstanceId());
					//Chained Transaction 사용으로 Error 발생시 전체 롤백되므로 별도 상태값 처리는 하지 않는다.
					//processDAO.updateProcessState(procUMap);
					
					// [20-05-08] 오류발생 시, 관리자에게 알림메일 발송 추가
					ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
					String errorReceivers = CoviFlowPropHelper.getInstace().getPropertyValue("ErrorReceiverCode");
					String errorSender = CoviFlowPropHelper.getInstace().getPropertyValue("ErrorSenderCode");
					
					for(String recevierCode : errorReceivers.split(";")) {
						HashMap<String,String> receiver = new HashMap<String,String>();
						
						receiver.put("wiid", "");
						receiver.put("userId", recevierCode);
						receiver.put("type", "UR");
						receivers.add(receiver);
					}
					
					// comment 에 errorMessage 담기
					if(receivers.size() > 0)
						CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(0, "", errorSender, "", "ENGINEERROR", delegate.getProcessInstanceId(), receivers, null, logMap.get("errorMessage").toString()));
				}
			}
			
			txManager.commit(status);
			
		} catch(Exception e){
			txManager.rollback(status);
			throw e;
		}
		
	}
	
	public static void insertErrorLog(DelegateTask delegate, Exception ex, String logType) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		
		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			if (delegate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
				}
				
				Map<String, Object> logMap = new HashMap<String, Object>();
				logMap.put("processDefID", delegate.getProcessDefinitionId());
				logMap.put("processInsID", delegate.getProcessInstanceId());
				logMap.put("executionID", delegate.getExecutionId());
				
				//task id
				logMap.put("taskID", delegate.getId());
				//ip
				InetAddress ip;
				try {
					ip = InetAddress.getLocalHost();
					if(ip != null){
						logMap.put("serverIP", ip.getHostAddress());
					}
				} catch (UnknownHostException unKnownEx) {
					unKnownEx.printStackTrace();
					logMap.put("serverIP", "");
				}
				
				logMap.put("errorKind", logType);
				logMap.put("errorTime", sdf.format(new Date()).toString());
				//error message
				if(ex.getMessage() != null){
					logMap.put("errorMessage", ex.getMessage());
					//stacktrace
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					
					byte[] stackTraceByte = sw.toString().getBytes("UTF-8");
					if(stackTraceByte.length > 65535) {
					logMap.put("errorStackTrace", new String (stackTraceByte, 0, 65535));
					}else {
						logMap.put("errorStackTrace", new String (stackTraceByte));
					}
					//class name
					logMap.put("errorClass", ex.getClass().toString());
					
					pw.close();
					sw.close();
				} else {
					logMap.put("errorMessage", ex.toString());
					logMap.put("errorStackTrace", ""); // mssql 오류로 인해 추가함.
				}
				
				processDAO.insertErrorLog(logMap);
				
				//state update 부분을 추가 할 것 - 275
				if(StringUtils.isNotBlank(delegate.getProcessInstanceId().toString())){
					Map<String, Object> procUMap = new HashMap<String, Object>();
					procUMap.put("processState", 275);
					procUMap.put("processID", delegate.getProcessInstanceId());
					//Chained Transaction 사용으로 Error 발생시 전체 롤백되므로 별도 상태값 처리는 하지 않는다.
					//processDAO.updateProcessState(procUMap); 
					
					// [20-05-08] 오류발생 시, 관리자에게 알림메일 발송 추가
					ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
					String errorReceivers = CoviFlowPropHelper.getInstace().getPropertyValue("ErrorReceiverCode");
					String errorSender = CoviFlowPropHelper.getInstace().getPropertyValue("ErrorSenderCode");
					
					for(String recevierCode : errorReceivers.split(";")) {
						HashMap<String,String> receiver = new HashMap<String,String>();
						
						receiver.put("wiid", "");
						receiver.put("userId", recevierCode);
						receiver.put("type", "UR");
						receivers.add(receiver);
					}
					
					// comment 에 errorMessage 담기
					if(receivers.size() > 0)
						CoviFlowWSHelper.invokeApi("MESSAGE", CoviFlowWorkHelper.makeMessageParams(0, "", errorSender, "", "ENGINEERROR", delegate.getProcessInstanceId(), receivers, null, logMap.get("errorMessage").toString()));
				}
				
				//state update 부분을 추가 할 것 - 275
				if(StringUtils.isNotBlank(delegate.getId().toString())){
					Map<String, Object> workUMap = new HashMap<String, Object>();
					workUMap.put("state", 275);
					workUMap.put("taskID", delegate.getId());
					//Chained Transaction 사용으로 Error 발생시 전체 롤백되므로 별도 상태값 처리는 하지 않는다.
					//processDAO.updateWorkItemState(workUMap);
				}
			}
			
			txManager.commit(status);
			
		} catch(Exception e){
			txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}
	
	public static void insertLegacy(String mode, String state, JSONArray parameters, Exception ex) throws Exception{
		
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		
		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);
		
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			String eventTime = null;
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("parameters", parameters.toJSONString());
			params.put("mode", mode);
			params.put("state", state);
			
			eventTime = sdf.format(new Date()).toString();
			
			params.put("eventTime", eventTime);
			
			if(((JSONObject)parameters.get(0)).containsKey("formInstID")) {
				String formInstID = base64Decode(((JSONObject)parameters.get(0)).get("formInstID").toString());
				params.put("FormInstID", formInstID);
			}
			if(((JSONObject)parameters.get(0)).containsKey("processID")) {
				String processID = base64Decode(((JSONObject)parameters.get(0)).get("processID").toString());
				params.put("ProcessID", processID);
			}
			if(((JSONObject)parameters.get(0)).containsKey("formPrefix")) {
				String formPrefix = base64Decode(((JSONObject)parameters.get(0)).get("formPrefix").toString());
				params.put("FormPrefix", formPrefix);
			}
			if(((JSONObject)parameters.get(0)).containsKey("docNumber")) {
				String docNumber = "";
				
				// 배포처의 경우는 base64 인코딩 되어있지 않음.
				try {
					docNumber = base64Decode(((JSONObject)parameters.get(0)).get("docNumber").toString());
				} catch(Exception e) {
					docNumber = ((JSONObject)parameters.get(0)).get("docNumber").toString();
				}
				params.put("DocNumber", docNumber);
			}
			if(((JSONObject)parameters.get(0)).containsKey("approverId")) {
				String approverId = base64Decode(((JSONObject)parameters.get(0)).get("approverId").toString());
				params.put("ApproverId", approverId);
			}
			if(((JSONObject)parameters.get(0)).containsKey("apvMode")) {
				String apvMode = base64Decode(((JSONObject)parameters.get(0)).get("apvMode").toString());
				params.put("ApvMode", apvMode);
			}
			if(((JSONObject)parameters.get(0)).containsKey("formInfoExt")) {
				String formInfoExt = base64Decode(((JSONObject)parameters.get(0)).get("formInfoExt").toString());
				params.put("FormInfoExt", formInfoExt);
			}
			if(((JSONObject)parameters.get(0)).containsKey("approvalContext")) {
				String approvalContext = base64Decode(((JSONObject)parameters.get(0)).get("approvalContext").toString());
				params.put("ApprovalContext", approvalContext);
			}
			
			//error message
			if(ex != null){
				params.put("errorMessage", ex.getMessage());
				//stacktrace
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				
				params.put("errorStackTrace", sw.toString());
				//class name
				params.put("errorClass", ex.getClass().toString());
				
				sw.close();
				pw.close();
			}
			else {
				params.put("errorMessage", ""); // mssql 오류로 인해 추가함.
				params.put("errorStackTrace", "");
			}
			
			//Legacy만 저장
			//message도 저장해야 할 경우 ||mode.equalsIgnoreCase("MESSAGE") 추가
			if(mode.equalsIgnoreCase("LEGACY")||
					(mode.equalsIgnoreCase("MESSAGE") && ex != null)||
					mode.equalsIgnoreCase("DISTRIBUTION")){
				processDAO.insertLegacy(params);
			}
			
			txManager.commit(status);
			
		} catch(Exception e){
			txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
	}

	public static String base64Decode(String token) throws Exception{
		String ret = "";
		if(StringUtils.isNotBlank(token)){
			byte[] decodedBytes = Base64.decode(token.getBytes());
			ret = new String(decodedBytes, Charset.forName("UTF-8"));
		}
	    return ret;
	}
}
