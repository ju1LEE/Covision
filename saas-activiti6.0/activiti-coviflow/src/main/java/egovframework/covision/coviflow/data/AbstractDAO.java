package egovframework.covision.coviflow.data;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDAO{
	/*
	 * 일반적
	 * http://addio3305.tistory.com/62
	 * 트랜잭션 관리 - http://barunmo.blogspot.kr/2013/06/mybatis.html
	 * */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDAO.class);
	
	protected SqlSessionTemplate sqlSession;
	
	public AbstractDAO(SqlSessionTemplate sqlSession){
		this.sqlSession = sqlSession;
	}
	
	protected void printQueryId(String queryId) {
        if(LOG.isDebugEnabled()){
        	LOG.debug("\t QueryId  \t:  " + queryId);
        }
    }

	/*
    public Object insert(String queryId, Object params){
        printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        Object oRet = null;
        
        try {
			oRet = session.insert(queryId, params);
		} finally {
			session.commit();
			session.close();
		}
        
        return oRet;
    }
     
    public Object update(String queryId, Object params){
    	printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        Object oRet = null;
        
        try {
			oRet = session.update(queryId, params);
		} finally {
			session.commit();
			session.close();
		}
        
        return oRet;
    }
     
    public Object delete(String queryId, Object params){
    	printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        Object oRet = null;
        
        try {
			oRet = session.delete(queryId, params);
		} finally {
			session.commit();
			session.close();
		}
        
        return oRet;
    }
     
    public Object selectOne(String queryId){
    	printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        Object oRet = null;
        
        try {
			oRet = session.selectOne(queryId);
		} finally {
			session.close();
		}
        
        return oRet;
    }
     
    public Object selectOne(String queryId, Object params){
    	printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        Object oRet = null;
        
        try {
			oRet = session.selectOne(queryId, params);
		} finally {
			session.close();
		}
        
        return oRet;
    }
     
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId){
    	printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;
        
        try {
			list = session.selectList(queryId);
		} finally {
			session.close();
		}
        
        return list;
    }
     
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId, Object params){
    	printQueryId(queryId);
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;
        
        try {
			list = session.selectList(queryId, params);
		} finally {
			session.close();
		}
        
        return list;
    }
	*/
	 
    public Object insert(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.insert(queryId, params);
    }
     
    public Object update(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.update(queryId, params);
    }
     
    public Object delete(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.delete(queryId, params);
    }
     
    public Object selectOne(String queryId){
        printQueryId(queryId);
        return sqlSession.selectOne(queryId);
    }
     
    public Object selectOne(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.selectOne(queryId, params);
    }
     
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId){
        printQueryId(queryId);
        return sqlSession.selectList(queryId);
    }
     
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.selectList(queryId,params);
    }
    
}
