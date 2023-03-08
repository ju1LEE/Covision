package egovframework.coviframework.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import egovframework.baseframework.util.json.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.RedisShardsUtil;
import egovframework.coviframework.service.AuthorityService;
import io.lettuce.core.pubsub.RedisPubSubListener;

@Component
public class AccessURLUtil {
	private static final Logger LOGGER = LogManager.getLogger(AccessURLUtil.class);
	
	public static final String CHANNELID = "accessUrlEvent";
	public static final String PUBSUB_TYPE_RESETALL = "RESET";
	public static final String PUBSUB_TYPE_REMOVE = "REMOVE";
	public static final String PUBSUB_TYPE_ADD = "ADD";
	
	private static AuthorityService authSvc;
	private static RedisLettuceSentinelUtil lettuceUtil;
	private static AccessURLRedisPubSubListener listener;
	@Autowired 
	private AccessURLUtil(AuthorityService authSvc) { 
		AccessURLUtil.authSvc = authSvc;
		
		RedisShardsUtil instance = RedisShardsUtil.getInstance();
		if(instance instanceof RedisLettuceSentinelUtil && listener == null) {
			lettuceUtil = (RedisLettuceSentinelUtil)instance;
			listener = new AccessURLRedisPubSubListener();
			lettuceUtil.subscribe(AccessURLUtil.CHANNELID, listener);
			
			LOGGER.info("AccessUrl Util cache change listen started.");
		}
	}
	
	private static HashMap<String, String> accessURL = new HashMap<String, String>();

	private static void setAccessURLList() throws Exception {
		accessURL = authSvc.selectAllowedURL();
	}
	
	public static boolean checkAccessURL(String url) throws Exception {
		if(accessURL.isEmpty()){
			setAccessURLList();
		}
		
		boolean bRet = false;
		
		if(accessURL.containsKey(url)){
			bRet = true;
		}else if(url.indexOf("/covicore/common/previewsrc/") > -1){
			bRet = true;
		}else if(url.indexOf("/covicore/common/preview/") > -1){
			bRet = true;
		}
		
		return bRet;
	}
	
	public static void resetAccessURLList() throws Exception {
		accessURL = authSvc.selectAllowedURL();
		if(lettuceUtil != null) {
			lettuceUtil.publish(AccessURLUtil.CHANNELID, AccessURLUtil.PUBSUB_TYPE_RESETALL);
		}
	}
	
	public static void addAccessURL(String accessURLID, String url) throws Exception {
		accessURL.put(url, accessURLID);
		if(lettuceUtil != null) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("accessURLID", accessURLID);
			param.put("url", url);
			String message = new CoviMap(param).toJSONString();
			lettuceUtil.publish(AccessURLUtil.CHANNELID, AccessURLUtil.PUBSUB_TYPE_ADD.concat("@").concat(message));
		}
	}
	
	public static void deleteAccessURL(String accessURLID) throws Exception {
		accessURL.values().remove(accessURLID);
		if(lettuceUtil != null) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("accessURLID", accessURLID);
			String message = new CoviMap(param).toJSONString();
			lettuceUtil.publish(AccessURLUtil.CHANNELID, AccessURLUtil.PUBSUB_TYPE_REMOVE.concat("@").concat(message));
		}
	}
	
	private static class AccessURLRedisPubSubListener implements RedisPubSubListener<String, String> {

		@Override
		public void message(String channel, String message) {
			if(!AccessURLUtil.CHANNELID.equals(channel)) {
				return;
			}
			LOGGER.info("AccessUrl Util cache Message arrived. " + message);
			if(AccessURLUtil.PUBSUB_TYPE_RESETALL.equals(message)) {
				try {
					accessURL = authSvc.selectAllowedURL();
				} catch(NullPointerException e){	
					LOGGER.error(e);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			else if(message != null && message.startsWith(AccessURLUtil.PUBSUB_TYPE_ADD)) {
				try {
					String jsonStr = message.replaceAll(AccessURLUtil.PUBSUB_TYPE_ADD.concat("@"), "");
					CoviMap jo = (CoviMap)new JSONParser().parse(jsonStr);
					String accessURLID = (String)jo.get("accessURLID");
					String url = (String)jo.get("url");
					accessURL.put(url, accessURLID);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else if(message != null && message.startsWith(AccessURLUtil.PUBSUB_TYPE_REMOVE)) {
				try {
					String jsonStr = message.replaceAll(AccessURLUtil.PUBSUB_TYPE_REMOVE.concat("@"), "");
					CoviMap jo = (CoviMap)new JSONParser().parse(jsonStr);
					String accessURLID = (String)jo.get("accessURLID");
					accessURL.values().remove(accessURLID);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void message(String pattern, String channel, String message) {
		}

		@Override
		public void subscribed(String channel, long count) {
		}

		@Override
		public void psubscribed(String pattern, long count) {
		}

		@Override
		public void unsubscribed(String channel, long count) {
		}

		@Override
		public void punsubscribed(String pattern, long count) {
		}
		
	}
}
