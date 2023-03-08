package egovframework.covision.coviflow.form.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.RedisShardsUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.covision.coviflow.form.service.FormFileCacheSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("FormFileCacheSvc")
public class FormFileCacheSvcImpl extends EgovAbstractServiceImpl implements FormFileCacheSvc{
	
	private Logger LOGGER = LogManager.getLogger(FormFileCacheSvcImpl.class);
	
	@SuppressWarnings("resource")
	@Override
	public String readAllText(String lang, String file, String encoding) throws IOException{
		String[] bits = file.split("/");
		String fileName = bits[bits.length-1];
		String formTemplateKey = SessionHelper.getSession("lang")+"_"+"formTemplate_"+fileName;
		
		RedisShardsUtil instance = RedisShardsUtil.getInstance();
		String isSaveRedisTemplate = PropertiesUtil.getDBProperties().getProperty("db.redis.isSaveRedisTemplate");
		
		if(isSaveRedisTemplate.equals("false") || instance.get(formTemplateKey) == null){		// 개발시에만 false
			BufferedReader br = null;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				br = new BufferedReader(new InputStreamReader(fis, encoding));
				StringBuilder builder = new StringBuilder();
				String sCurrentLine;
				
				while ((sCurrentLine = br.readLine()) != null) {
					builder.append(sCurrentLine + System.getProperty("line.separator"));
				}
				String text = builder.toString();
				
				Pattern p = Pattern.compile("<spring:message[^>]*code=[\"']?([^>\"']+)[\"']?[^>]*(/>|></spring>|></spring:message>)");
				Matcher m = p.matcher(text);
				
				StringBuffer result = new StringBuffer(text.length());
				while(m.find()){
					String key = m.group(1).replace("Cache.", "");
					//tempDic 부분을 redis 다국어 가져 오는 걸로 대체
					m.appendReplacement(result, DicHelper.getDic(key, lang));
				}
				m.appendTail(result);
				
				if(isSaveRedisTemplate.equals("true")) {
					instance.save(formTemplateKey, result.toString());
					LOGGER.debug("formTemplate is cached in redis");
				}
				
				return result.toString();
			} catch (IOException e) {
				LOGGER.error("FormFileCacheSvcImpl", e);
				throw e;
			}finally {
				if(br != null) {
					try {
						br.close();
					}catch(NullPointerException npE) {
						LOGGER.error(npE.getLocalizedMessage(), npE);
					}catch(Exception e) {
						LOGGER.error(e.getLocalizedMessage(), e);
					}
				}
				if(fis != null) {
					try {
						fis.close();
					}catch(NullPointerException npE) {
						LOGGER.error(npE.getLocalizedMessage(), npE);
					}catch(Exception e) {
						LOGGER.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}else{
			return instance.get(formTemplateKey);
		}
	}
}
