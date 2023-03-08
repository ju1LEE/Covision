package egovframework.covision.coviflow.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;

/**
 * 연속결재중 최종결재권자의 문서중에 동일기안부서 문서가 포함된 경우 문서번호 sequence 발번시 deadlock 발생으로 jwf_documentnumber 관련 UPDATE
 * 를 단독 트랜잭션으로 가고, synchronized block 을 걸어줌.  
 * @author hgsong
 */
@Component
@Service
public class DocNumberService {
	@Autowired
	CoviFlowProcessDAO processDAO;
	
	public int getDocumentNumber(final Map<String, Object> paramsC) throws Exception {
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		final DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");	
		TransactionTemplate txTemplate = new TransactionTemplate(txManager);                
		txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		int n = txTemplate.execute(new TransactionCallback<Integer>() {
		    public Integer doInTransaction(TransactionStatus status) {
		        // do stuff
				try {
					processDAO.insertDocumentNumber(paramsC);
					int serialNumber = (int)paramsC.get("SerialNumber");
					return serialNumber;
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
		    }
		});
		return n;
	}
	
	public void updateDocumentNumber(final Map<String, Object> paramsC) throws Exception {
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		final DataSourceTransactionManager txManager = (DataSourceTransactionManager)context.getBean("coviTransactionManager");	
		TransactionTemplate txTemplate = new TransactionTemplate(txManager);                
		txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		txTemplate.execute(new TransactionCallback<Void>() {
		    public Void doInTransaction(TransactionStatus status) {
		        // do stuff
				try {
					processDAO.updateDocumentNumber(paramsC);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
		    }
		});
	}
}
