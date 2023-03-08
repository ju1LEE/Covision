/**
 * @Class Name : CoviFlowPropHelper.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class CoviFlowPropHelper {

	private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";
	private static Properties prop = null;
	private static volatile CoviFlowPropHelper instance;
	
	public static CoviFlowPropHelper getInstace() {
		synchronized (CoviFlowPropHelper.class) {
			if (instance == null)
				instance = new CoviFlowPropHelper();
			
			return instance;
		}
	}

	public CoviFlowPropHelper() {
		initialize();
	}
	public void initialize() {
		if(prop == null) { // load 이후에 new 되는 현상 발견되어 조건 추가함.
			InputStream is = null;
			InputStreamReader isr = null;
			try {
				prop = new Properties();
				String full_path = getConfigPropPath("coviflow_config.properties");
				File fp = new File(full_path);
	
				// 해당경로에 파일이 존재하면 경로내 Properties Read
				if (fp.exists()) {
					// 해당경로에 파일이 존재한다.
					is = new FileInputStream(fp);
					prop.load(is);
				} else {
					// 해당경로에 파일이 존재하지 않는다.
					// 입력된 경로는 classpath root부터 시작.
					InputStream resIs = null;
					try {
						resIs = CoviFlowPropHelper.class.getClassLoader().getResourceAsStream("egovframework/covision/coviflow/util/coviflow_config.properties");
						isr = new InputStreamReader( resIs, "utf-8");
						prop.load(isr);
					}finally {
						if(resIs != null) {
							try {resIs.close();}catch(IOException ioe) {}
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(is != null) {
					try{
						is.close();
					} catch(IOException e){}	
				}
				if(isr != null) {
					try{
						isr.close();
					} catch(IOException e){}	
				}
			}
		}
	}

	public Set<Object> getAllKeys() {
		Set<Object> keys = prop.keySet();
		return keys;
	}

	public String getPropertyValue(String key) {
		return prop.getProperty(key);
	}

	@SuppressWarnings("unchecked")
	public JSONObject getPropertyObject() {
		JSONObject retObj = new JSONObject();
		Set<Object> keys = prop.keySet();
		for (Object k : keys) {
			String key = (String)k;
			retObj.put(key, prop.getProperty(key));
		}

		return retObj;
	}
	
	/**
	 * @param pPostfixPath : 프로퍼티 이름
	 * 
	 * @return 서비스 환경에서의 property 경로
	 */
	public String getConfigPropPath(String pPostfixPath) {
		String path = "";
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			path = "C:" + System.getProperty("file.separator");
		}
		// /usr/local/covision/webapps/covi_property/...
		path = path + System.getProperty("DEPLOY_PATH") + File.separator + "covi_property" + File.separator + pPostfixPath;
		
		return path;
	}
	
	/**
     * 암호화값인지 여부 확인
     * @param value final String 암호화값여부확인할 문자열
     * @exception 
     * @return boolean 암호화값여부
     */
    public boolean isEncryptedValue(final String value) {
        if (value == null) {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(ENCRYPTED_VALUE_PREFIX) && 
                trimmedValue.endsWith(ENCRYPTED_VALUE_SUFFIX));
    }
    
   /**
	 * 암호화값 함수(ENC)에 넣기
	 * @param value final String 암호화값 함수에 넣을 암호화된 문자열
	 * @exception 
	 * @return String 함수에 넣은 암호화된 문자열
	 */
	public String getInnerEncryptedValue(final String value) {
        return value.substring(
                ENCRYPTED_VALUE_PREFIX.length(),
                (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }
	
	public String getDecryptedProperty(final String value){
		String propRet = "";
		String propEncKey = CoviFlowPropHelper.getInstace().getPropertyValue("sec.pbe.key");
		if(StringUtils.isNoneBlank(value)&&isEncryptedValue(value)){
			propRet = PBE.decode(getInnerEncryptedValue(value), propEncKey);
		} else {
			propRet = value;
		}
		
		return propRet;
	}
	
	
	public String getEncryptedValue(final String value){
		String propRet = "";
		String propEncKey = CoviFlowPropHelper.getInstace().getPropertyValue("sec.pbe.key");
		if(StringUtils.isNoneBlank(value) && !isEncryptedValue(value)){
			propRet = ENCRYPTED_VALUE_PREFIX + PBE.encode(value, propEncKey) + ENCRYPTED_VALUE_SUFFIX;
		} else {
			propRet = value;
		}
		return propRet;
	}

	public void reloadProperties() {
		prop = null;
		initialize();
	}
}