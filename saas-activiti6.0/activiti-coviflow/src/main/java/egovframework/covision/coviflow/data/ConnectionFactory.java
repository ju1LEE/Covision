package egovframework.covision.coviflow.data;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConnectionFactory {

	//private static SqlSessionFactory sqlSessionFactory;
	private static SqlSessionTemplate sqlSession;
	
	static {
		String resource = "activiti-custom-context.xml";
		ApplicationContext ctx = new ClassPathXmlApplicationContext(resource);
				
		try {
			/*
			if(sqlSessionFactory == null) {
				sqlSessionFactory=(SqlSessionFactory)ctx.getBean("sqlSessionFactory");  
			}
			*/
			if(sqlSession == null){
				sqlSession = (SqlSessionTemplate)ctx.getBean("sqlSessionTemplate");
			}
			
		} catch(Exception e) {
			//오류처리 할 것
			e.printStackTrace();
		} finally {
			//context close 구문 - 반드시 close 할 것
			((ClassPathXmlApplicationContext) ctx).close();
		}
	}
	
	//public static SqlSessionFactory getSqlSessionFactory() {
	//	return sqlSessionFactory;
	//}
	
	public static SqlSessionTemplate getSqlSession(){
		return sqlSession;
	}
}
