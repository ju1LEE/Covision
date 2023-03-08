/**
 * @Class Name : RequestRegisterToEDMS.java
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;
import egovframework.covision.coviflow.util.CoviFlowApprovalLineHelper;
import egovframework.covision.coviflow.util.CoviFlowLogHelper;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;

public class RequestRegisterToEDMS implements JavaDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(RequestRegisterToEDMS.class);
			
	public void execute(DelegateExecution execution) throws Exception {
		
		LOG.info("RequestRegisterToEDMS");
		
		try{
			// 프로세스 옵션 + 문서분류정보가 있는경우에 이쪽으로 옴.
			insertInterfaceInfo(execution);
		}catch(Exception e){
			CoviFlowLogHelper.insertErrorLog(execution, e, "ERROR");
			LOG.error("RequestRegisterToEDMS", e);
		}
	}
		
	private void insertInterfaceInfo(DelegateExecution delegate) throws Exception {
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		try{
			CoviFlowProcessDAO processDAO = (CoviFlowProcessDAO)context.getBean("coviFlowProcessDAO");
			
			if (delegate != null) {
				JSONParser parser = new JSONParser();
				String apvLine = (String)delegate.getVariable("g_appvLine");
				JSONObject apvLineObj = (JSONObject)parser.parse(apvLine);
				
				String usid = CoviFlowApprovalLineHelper.getInitiatorID(apvLineObj);
				int piid = Integer.parseInt(delegate.getVariable("g_piid").toString());
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
		}
		
				/**
					#{DocNo}, 
					#{DocType}, 
					#{DocClass}, 
					#{DocSecurity}, 
					#{EndDate}, 
					#{DraftID}, 
					#{DraftName}, 
					#{DraftDeptID}, 
					#{DraftDeptName}, 
					#{DraftDate}, 
					#{ResultID}, 
					#{ResultName}, 
					#{ResultDate}, 
					#{DocTitle}, 
					#{DocBody}, 
					#{DocTemplate}, 
					#{DocAttach}, 
					#{ResultTxt}, 
					#{EndFlag}, 
					#{ReceiptLIST}, 
					#{ErrMessage}, 
					#{ApvFlag}
				 */
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("ProcessId", piid);
				paramMap.put("DraftId", usid);
				paramMap.put("EndFlag", "0");// Ready
		
				processDAO.insertEdmsTransferData(paramMap);
			}
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
	}
}
