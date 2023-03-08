package egovframework.covision.coviflow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class CoviFlowWSHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(CoviFlowWSHelper.class);
	private static CoviFlowPropHelper propHelper = CoviFlowPropHelper.getInstace();
	
	// Java Virtual Machine에서 활용할 수 있는 프로세서의 수
	private final static int cores = Runtime.getRuntime().availableProcessors();
	final static int AVG_WAIT_TIME = 100;
	final static int AVG_SVC_TIME = 50; // HTTP avarage run time.
	final static long KEEP_ALIVE_TIMEOUT = 60L; // in seconds
	final static int OPTIMIZED_THREADS_COUNT = cores * (1 + AVG_WAIT_TIME / AVG_SVC_TIME);
	final static int CORE_THREADS_COUNT = OPTIMIZED_THREADS_COUNT / 2;
	final static int FUTURE_GET_TIMEOUT = 120;// in seconds, Timeout 이 발생할경우 Transaction 정합성이 안맞을 수 있으므로 여유있게 준다.
	private static ExecutorService executor = (new ThreadPoolExecutor(CORE_THREADS_COUNT, OPTIMIZED_THREADS_COUNT, KEEP_ALIVE_TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
	
	private static LinkedBlockingQueue<Callable<String>> queue = new LinkedBlockingQueue<Callable<String>>(Integer.MAX_VALUE);
	private static Thread queueExecutor = null;
	
    public static void invokeApi(final String mode, final JSONArray params) throws Exception {
    	/*
    	 * Runnable과 Callable변수에 대한 이해 - http://palpit.tistory.com/732
    	 * */
		final boolean isAsync = Boolean.parseBoolean(propHelper.getPropertyValue("IsAsync"));
    	
    	//legacy start
    	CoviFlowLogHelper.insertLegacy(mode, "start", params, null);
    	
    	
    	Callable<String> callabeleTask = new Callable<String>() {
			@Override
			public String call() throws Exception {
            	try {
	           		String result = "";
					switch (mode) {
						case "MESSAGE":
							result = sendMessageService(params);
							break;
						case "LEGACY":
							result = callLegacyService(params);	
							break;
						case "APVLINE":
							result = callApvLineService(params);	
							break;
						case "DISTRIBUTION":
							result = callDistributionService(params);	
							break;							
					}

					return "[SUCESS]" + result;
				} catch (MalformedURLException e) {
					LOG.error("invokeapi MalformedURLException", e);
					try {
						CoviFlowLogHelper.insertLegacy(mode, "error", params, e);
					} catch (Exception e1) {
						LOG.error("MalformedURLException - insertLegacy", e);
					} finally {
					}
					return "[ERROR]" + e.getLocalizedMessage();
					
				} catch (IOException e) {
					LOG.error("invokeapi IOException", e);
					// Connection reset, refused 등 발생하면 재처리 가능하도록 jwf_legacy에 이력 남김.
					try {
						CoviFlowLogHelper.insertLegacy(mode, "error", params, e);
					} catch (Exception e1) {
						LOG.error("IOException - insertLegacy", e);
					}
					return "[ERROR]" + e.getLocalizedMessage();
				} catch(RuntimeException e) {
					LOG.error("invokeapi RuntimeException", e);
					return "[ERROR]" + e.getLocalizedMessage();
				} catch (Exception e) {
					LOG.error("invokeapi Exception", e);
					
					try {
						CoviFlowLogHelper.insertLegacy(mode, "error", params, e);
					} catch (Exception e1) {
						LOG.error("Exception - insertLegacy", e);
					}
					return "[ERROR]" + e.getLocalizedMessage();
				}
			}
    	};
    	
    	
    	final Runnable taker = new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						Callable<String> callable = queue.take();
						callable.call();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
		};
		
		/* isAsync 
		 * true의 의미는 결과 return을 기다리지 않는 다는 의미
		 * false의 경우 Future 객체를 이용하여 결과값 return을 기다리게 된다.
		 * 현재 false의 경우 특정 케이스에서 정상적인 처리의 결과값 return을 받지 못하고 timeout이 발생하는 오류가 있어 true로 사용할 것.
		 * 
		 * --> 2021.08.20
		 * Async만  사용하고 있어 결재연동시 오류처리/Rollback 에 어려움이 있어 Callable, Future 사용코드 개선처리함. hgsong
		 */
		if (isAsync || !"LEGACY".equals(mode)) {
			// 사전작업이 다 끝난(commit) 후에 Rest 호출하도록 한다.
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					try {
						//병렬처리시 진행중연동(OTHERSYSTEM) 완료연동(COMPLETE) 요청이 순서가 역전될 수 있으므로 Queue 방식으로 전환. 2021.09.15
						//executor.execute(runnableTask);
						queue.offer(callabeleTask);
						if(queueExecutor == null) {
							queueExecutor = new Thread(taker, "Legacy execute queue worker");
							queueExecutor.setDaemon(true);
							queueExecutor.start();
						}
					} catch (Exception e) {
						LOG.error("DraftEnd - Sending Message", e);
					}
				}
			});
			
		} else {
			// Async = false 로 설정후 /executeLegacy 에서 오류발생시 Engine Transaction 도 롤백된다.
			// But, /executeLegacy 에서 결재 테이블 에 대해서 DB처리를 하는 경우(동일 레코드) Deadlock 이 발생할 수 있으므로 연동방식에 따라 주의해야 함!!!
			Future<String> future = executor.submit(callabeleTask);
			String result = "";
			try {
				result = (String)future.get(FUTURE_GET_TIMEOUT, TimeUnit.SECONDS);
				//CoviFlowLogHelper.insertLegacy(mode, "complete", params, null);
			} catch(TimeoutException e){
            	LOG.error("invokeapi TimeoutException", e);
            	CoviFlowLogHelper.insertLegacy(mode, "error", params, e);
            	throw new Exception(e);
            } catch (InterruptedException e) {
				LOG.error("invokeapi InterruptedException", e);
            	CoviFlowLogHelper.insertLegacy(mode, "error", params, e);
            	throw new Exception(e);
			} catch (ExecutionException e) {
				LOG.error("invokeapi ExecutionException", e);
            	CoviFlowLogHelper.insertLegacy(mode, "error", params, e);
            	throw new Exception(e);
			}
			
			if(result == null || result.startsWith("[ERROR]")) {
				throw new RuntimeException(result);
			}
		}
    }
    
    @SuppressWarnings("unchecked")
   	public static String callDistributionService(JSONArray params) throws Exception {
   		String ret = null;

   		/*
		 * {
		 * 		"DistributionInfo" : {
		 * 			"receiptList" : "",
		 * 			"context" : "",
		 * 			"approvalLine" : "",
		 * 			"piid" : ""
		 * 		}
		 * }
		 */
   		String sURL = propHelper.getPropertyValue("OuterDistributionUrl");

   		if (StringUtils.isNotBlank(sURL)) {
   			HttpURLConnection conn = null;
   			OutputStream os = null;
   			BufferedReader br = null;
   			
   			try {
	   			URL url = new URL(sURL);
	   			conn = (HttpURLConnection) url.openConnection();
	   			conn.setDoOutput(true);
	   			conn.setRequestMethod("POST");
	   			conn.setRequestProperty("Content-Type", "application/json");
	
	   			JSONObject input = new JSONObject(); 
	   			
	   			JSONObject param = new JSONObject();
	   			param = (JSONObject)params.get(0);
	   			
	   			input.put("DistributionInfo", param);
	   			
	   			os = conn.getOutputStream();
	   			os.write(input.toJSONString().getBytes());
	   			os.flush();
	
	   			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	   				LOG.error("Failed : HTTP error code : " + conn.getResponseCode());
	   				CoviFlowLogHelper.insertLegacy("DISTRIBUTION", "error", params, new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode()));
	   				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	   			}
	
	   			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
	   			String output;
	   			while ((output = br.readLine()) != null) {
	   				ret += output;
	   			}
   			} catch(Exception e) {
   				throw e;
   			} finally {
   				if(br != null) br.close();
	   			if(os != null) os.close();
	   			conn.disconnect();   				
   			}
   		}
   		else {
   			LOG.error("OuterDistributionUrl is blank");
			CoviFlowLogHelper.insertLegacy("DISTRIBUTION", "error", params, new RuntimeException("OuterDistributionUrl is blank"));
			throw new RuntimeException("OuterDistributionUrl is blank");
   		}

   		return ret;
   	} 
    
    @SuppressWarnings("unchecked")
   	public static String callApvLineService(JSONArray params) throws Exception {
   		String ret = null;

   		/*
		 * {
		 * 		"ApvLineInfo" : {
		 * 			"mode" : "",
		 * 			"approvalLine" : "",
		 * 			"actionMode" : "",
		 *  		"FormSubject" : "",
		 *  		"FormName" : "",
		 *  		"usid" : "",
		 *  		"usdn" : "",
		 *  		"FormPrefix" : ""
		 * 		}
		 * }
		 */
   		String sURL = propHelper.getPropertyValue("ApvLineUrl");

   		if (StringUtils.isNotBlank(sURL)) {
   			HttpURLConnection conn = null;
   			OutputStream os = null;
   			BufferedReader br = null;
   			
   			try {
	   			URL url = new URL(sURL);
	   			conn = (HttpURLConnection) url.openConnection();
	   			conn.setDoOutput(true);
	   			conn.setRequestMethod("POST");
	   			conn.setRequestProperty("Content-Type", "application/json");
	
	   			JSONObject input = new JSONObject(); 
	   			
	   			JSONObject apvLineParam = new JSONObject();
	   			apvLineParam = (JSONObject)params.get(0);
	   			
	   			JSONObject apvLineInfo = new JSONObject();
	   			apvLineInfo.put("mode", (String)apvLineParam.get("mode"));
	   			apvLineInfo.put("approvalLine", (String)apvLineParam.get("approvalLine"));
	   			apvLineInfo.put("actionMode", (String)apvLineParam.get("actionMode"));
	   			apvLineInfo.put("FormSubject", (String)apvLineParam.get("FormSubject"));
	   			apvLineInfo.put("FormName", (String)apvLineParam.get("FormName"));
	   			apvLineInfo.put("usid", (String)apvLineParam.get("usid"));
	   			apvLineInfo.put("usdn", (String)apvLineParam.get("usdn"));
	   			apvLineInfo.put("FormPrefix", (String)apvLineParam.get("FormPrefix"));
	   			   			 
	   			input.put("ApvLineInfo", apvLineInfo);
	   			
	   			os = conn.getOutputStream();
	   			os.write(input.toJSONString().getBytes());
	   			os.flush();
	
	   			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	   				LOG.error("Failed : HTTP error code : " + conn.getResponseCode());
	   				CoviFlowLogHelper.insertLegacy("APVLINE", "error", params, new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode()));
	   				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	   			}
	
	   			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
	   			String output;
	   			while ((output = br.readLine()) != null) {
	   				ret += output;
	   			}
   			}
   			catch(Exception e) {
   				throw e;
   			} finally {
   				if(br != null) br.close();
	   			if(os != null) os.close();
	   			conn.disconnect();
   			}
   		}
   		else {
   			LOG.error("ApvLineUrl is blank");
			CoviFlowLogHelper.insertLegacy("APVLINE", "error", params, new RuntimeException("ApvLineUrl is blank"));
			throw new RuntimeException("ApvLineUrl is blank");
   		}

   		return ret;
   	}     
    
    @SuppressWarnings("unchecked")
	public static String callLegacyService(JSONArray params) throws Exception {
		String ret = null;

		//1. parameter 처리
		/*
		 *{"formPrefix":"","bodyContext":"","formInfoExt":"","approvalContext":"","preApproveprocsss":"","apvResult":"","docNumber":"","approverId":"","formInstID":"","apvMode":"","processID":"","formHelperContext":"","formNoticeContext":""}
		 */
		
		/*
		 * {
			  "LegacyInfo": 
			   {
				  "formPrefix": "",
				  "bodyContext": "",
				  "formInfoExt": "",
				  "approvalContext": "",
				  "preApproveprocsss": "",
				  "apvResult": "",
				  "docNumber": "",
				  "approverId": "",
				  "comment":"",
				  "formInstID": "",
				  "apvMode": "",
				  "processID":"",
				  "formHelperContext":"",
				  "formNoticeContext":""
			    }
			}
		 * 
		 */
		String sURL = propHelper.getPropertyValue("LegacyUrl");

		if (StringUtils.isNotBlank(sURL)) {
			HttpURLConnection conn = null;
   			OutputStream os = null;
   			BufferedReader br = null;
   			
			try {
				URL url = new URL(sURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
	
				JSONObject input = new JSONObject(); 
				
				JSONObject legacyParam = new JSONObject();
				legacyParam = (JSONObject)params.get(0);
				
				String _formPrefix = (String)legacyParam.get("formPrefix");
				//String _bodyContext = (String)legacyParam.get("bodyContext");
				String _formInfoExt = (String)legacyParam.get("formInfoExt");
				String _approvalContext = (String)legacyParam.get("approvalContext");
				String _preApproveprocsss = (String)legacyParam.get("preApproveprocsss");
				String _apvResult = (String)legacyParam.get("apvResult");
				String _docNumber = (String)legacyParam.get("docNumber");
				String _approverId = (String)legacyParam.get("approverId");
				String _comment = (String)legacyParam.get("comment");
				String _formInstID = (String)legacyParam.get("formInstID");
				String _apvMode = (String)legacyParam.get("apvMode");
				String _processID = (String)legacyParam.get("processID");
				String _formHelperContext = (String)legacyParam.get("formHelperContext");
				String _formNoticeContext = (String)legacyParam.get("formNoticeContext");
				
				JSONObject legacyInfo = new JSONObject();
				
				legacyInfo.put("formPrefix", _formPrefix);
				//legacyInfo.put("bodyContext", _bodyContext);
				legacyInfo.put("formInfoExt", _formInfoExt);
				legacyInfo.put("approvalContext", _approvalContext);
				legacyInfo.put("preApproveprocsss", _preApproveprocsss);
				legacyInfo.put("apvResult", _apvResult);
				legacyInfo.put("docNumber", _docNumber);
				legacyInfo.put("approverId", _approverId);
				legacyInfo.put("comment", _comment);
				legacyInfo.put("formInstID", _formInstID);
				legacyInfo.put("apvMode", _apvMode);
				legacyInfo.put("processID", _processID);
				legacyInfo.put("formHelperContext", _formHelperContext);
				legacyInfo.put("formNoticeContext", _formNoticeContext);
				 
				input.put("LegacyInfo", legacyInfo);
				
				os = conn.getOutputStream();
				os.write(input.toJSONString().getBytes());
				os.flush();
	
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					LOG.error("Failed : HTTP error code : " + conn.getResponseCode());
					CoviFlowLogHelper.insertLegacy("LEGACY", "error", params, new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode()));
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
	
				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
				String output;
				while ((output = br.readLine()) != null) {
					ret += output;
				}
			}
			catch(Exception e) {
				throw e;
			} finally {
				if(br != null) br.close();
	   			if(os != null) os.close();
	   			conn.disconnect();
			}
		}
		else {
			LOG.error("LegacyUrl is blank");
			CoviFlowLogHelper.insertLegacy("LEGACY", "error", params, new RuntimeException("LegacyUrl is blank"));
			new RuntimeException("LegacyUrl is blank");
		}

		return ret;
	} 
    
	@SuppressWarnings("unchecked")
	public static String sendMessageService(JSONArray params) throws Exception {
		String ret = null;
		/* 1. url
		 * http://localhost:8081/approval/legacy/setmessage.do
		 * 
		 * @RequestMapping(value = "legacy/setmessage.do",
		 * method=RequestMethod.POST)
		 * 
		 * 2. data format
		 * <data format> { "MessageInfo": [ { "UserId": "bjlsm2",
		 * "MessageSubject": "", "MessageContext": "", "ReceiverText" : "",
		 * "Status": "APPROVAL" }, { "UserId": "bjlsm2", "MessageSubject": "",
		 * "MessageContext": "", "ReceiverText" : "", "Status": "COMPLETE" } ] }
		 * 
		 * 3.status
			"APPROVAL": "Y;MAIL;MESSENGER;TODOLIST;MDM;",
			"COMPLETE": "Y;MESSENGER;TODOLIST;",
			"REJECT": "Y;TODOLIST;",
			"COMMENT": "Y;MESSENGER;",
			"CCINFO": "N;",
			"CIRCULATION": "Y;",
			"REDRAFT": "Y;",
			"REDRAFT_RE": "Y;",
			"CHARGE": "N;",
			"CHARGEJOB": "Y;",
			"HOLD": "Y;MESSENGER;TODOLIST;MDM;",
			"WITHDRAW": "Y;MDM;",
			"ABORT": "N;",
			"APPROVECANCEL": "Y;",
			"ASSIGN_APPROVAL": "Y;",
			"ASSIGN_R": "Y;"
		 */

		String sURL = propHelper.getPropertyValue("MessageUrl");

		if (StringUtils.isNotBlank(sURL)) {
			HttpURLConnection conn = null;
   			OutputStream os = null;
   			BufferedReader br = null;
   			
			try {
				URL url = new URL(sURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				
				JSONObject input = new JSONObject(); 
					 
				input.put("MessageInfo", params);
				
				os = conn.getOutputStream();
				os.write(input.toJSONString().getBytes());
				os.flush();
	
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					LOG.error("Failed : HTTP error code : " + conn.getResponseCode());
					CoviFlowLogHelper.insertLegacy("MESSAGE", "error", params, new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode()));
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
	
				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
				String output;
				while ((output = br.readLine()) != null) {
					ret += output;
				}
			}
			catch (Exception e) {
				throw e;
			} finally {
				if(br != null) br.close();
	   			if(os != null) os.close();
				conn.disconnect();
			}
		}
		else {
			LOG.error("MessageUrl is blank");
			CoviFlowLogHelper.insertLegacy("MESSAGE", "error", params, new RuntimeException("MessageUrl is blank"));
			throw new RuntimeException("MessageUrl is blank");
		}

		return ret;
	}
}
