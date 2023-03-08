package egovframework.covision.coviflow.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class CoviFlowProcessDAO_depreacted {

	private SqlSessionTemplate sqlSession;
	
	public CoviFlowProcessDAO_depreacted(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSession = sqlSessionTemplate;
	}

	/* 테스트용 
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectProcess() throws Exception {
		return (List<Map<String, Object>>)selectList("test.select", null);
	}
	*/
	
	public void insertAppvLine(Map<String, Object> publicParams, Map<String, Object> privateParams)throws Exception {
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		/*DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		PlatformTransactionManager txManager = (PlatformTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);*/
		
		//SqlSessionTemplate sqlSession = (SqlSessionTemplate)context.getBean("sqlSessionTemplate");
		
		try{
			sqlSession.insert("appvLine.insertAppvLinePublic", publicParams);
			sqlSession.insert("appvLine.insertAppvLinePrivate", privateParams);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}
	
	public void insertAppvLinePublic(Map<String, Object> params)throws Exception {
		sqlSession.insert("appvLine.insertAppvLinePublic", params);
	}
	
	public void insertAppvLinePrivate(Map<String, Object> params)throws Exception {
		sqlSession.insert("appvLine.insertAppvLinePrivate", params);
	}
	
}
