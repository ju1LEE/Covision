package egovframework.covision.coviflow.data;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class MyBatisTransactionManager extends DefaultTransactionDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2725779326011563749L;

	TransactionStatus status;

	PlatformTransactionManager transactionManager;
	
	public MyBatisTransactionManager(PlatformTransactionManager txManager){
		this.transactionManager = txManager;
	}
	
	public void start() throws TransactionException {
		status = transactionManager.getTransaction(this);
	}

	public void commit() throws TransactionException {
		if (!status.isCompleted()) {
			transactionManager.commit(status);
		}
	}

	public void rollback() throws TransactionException {
		if (!status.isCompleted()) {
			transactionManager.rollback(status);
		}
	}

	public void end() throws TransactionException {
		rollback();
	}

}
