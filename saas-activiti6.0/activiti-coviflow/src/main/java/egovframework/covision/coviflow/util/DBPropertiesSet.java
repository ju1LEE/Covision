package egovframework.covision.coviflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class DBPropertiesSet {
	
	private static CoviFlowPropHelper propHelper = CoviFlowPropHelper.getInstace();
	private static Properties db_prop = null;
	
	public String getDriver() {
		String dbType = getDBProperties().getProperty("db.mapper.engine.sql");
		
		String getDriver = "db."+dbType+".driver";
		getDriver = getDBProperties().getProperty(getDriver);
		
		return getDriver;
	}


	public String getUrl() {
		String dbType = getDBProperties().getProperty("db.mapper.engine.sql");
		
		String getUrl = "db."+dbType+".url";
		getUrl = propHelper.getDecryptedProperty(getDBProperties().getProperty(getUrl));
		
		return getUrl;
	}
	
	public String getUserName() {
		String dbType = getDBProperties().getProperty("db.mapper.engine.sql");
		
		String getUserName = "db."+dbType+".username";
		getUserName = propHelper.getDecryptedProperty(getDBProperties().getProperty(getUserName));
		
		return getUserName;
	}

	public String getPassword() {
		String dbType = getDBProperties().getProperty("db.mapper.engine.sql");
		
		String getPassword = "db."+dbType+".password";
		getPassword = propHelper.getDecryptedProperty(getDBProperties().getProperty(getPassword));
		
		return getPassword;
	}


	public String getClassPath() {
		return "classpath:/coviflow/sql/"+getDBProperties().getProperty("db.mapper.engine.sql")+"/*.xml";
	}
	
	public String getValidationQuery() {
		String dbType = getDBProperties().getProperty("db.mapper.engine.sql");
		String getValidationQuery = "SELECT 1";
		
		if(dbType.toLowerCase().replaceAll("\\p{Z}", "").equals("oracle")
				|| dbType.toLowerCase().replaceAll("\\p{Z}", "").equals("tibero")){
			getValidationQuery = "SELECT 1 FROM DUAL";
		}else if(dbType.toLowerCase().replaceAll("\\p{Z}", "").equals("mssql")){
			getValidationQuery = "SELECT 1";
		}
		
		return getValidationQuery;
	}
	
	public static Properties getDBProperties() {
		if(db_prop == null) {
			InputStream fi = null;
			InputStreamReader isr = null;
			try {
				db_prop = new Properties();
				
				String full_path = propHelper.getConfigPropPath("db.properties");			// 외부의 프로퍼티를 볼때는 그룹웨어와 동일한 db.properties를 봄
				File fp = new File(full_path);
				
				// 해당경로에 파일이 존재하면 경로내 Properties Read
				if (fp.exists()) {
					// 해당경로에 파일이 존재한다.
					fi = new FileInputStream(fp);
					db_prop.load(fi);
				} else {
					InputStream resIs = null;
					try {
						resIs = CoviFlowPropHelper.class.getClassLoader().getResourceAsStream("coviflow/coviflow_db.properties");
						isr = new InputStreamReader( resIs, "utf-8");
						db_prop.load(isr);
					} finally {
						if(resIs != null) {
							try { resIs.close(); }catch(IOException ioe) {}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(fi != null) {
					try{
						fi.close();
					} catch(IOException e){}	
				}
				if(isr != null) {
					try{
						isr.close();
					} catch(IOException e){}	
				}
			}
		}
		return db_prop;
	}
}
