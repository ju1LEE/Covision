package egovframework.covision.groupware.base.service.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Resource;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.coviframework.util.ACLHelper;
import egovframework.coviframework.util.HttpURLConnectUtil;
import egovframework.covision.groupware.base.service.LongPollingSvc;

@Service("longPollingService")
public class LongPollingSvcImpl implements LongPollingSvc {

	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	private Logger LOGGER = LogManager.getLogger(LongPollingSvcImpl.class);
	
	
	@Override
	public CoviMap getQuickMenuCount(CoviMap params, CoviMap userDataObj) throws Exception {
		ArrayList<String> menuList = (ArrayList<String>) params.get("menuList");
		CoviMap returnObj = new CoviMap();
		
		for(String menuCode : menuList) {
			if(returnObj.containsKey(menuCode)) { //중복된 데이터 조회 방지
				continue;
			}
			
			String aclBoardFolder = "";
			
			if(menuCode.equalsIgnoreCase("Integrated")) {
				params.put("alarmType", menuCode);
				returnObj.put(menuCode, coviMapperOne.getNumber("user.longPolling.selectAlarmListCnt", params));
			}else if(menuCode.equalsIgnoreCase("Mention")) {
				params.put("alarmType", menuCode);
				returnObj.put(menuCode, coviMapperOne.getNumber("user.longPolling.selectAlarmListCnt", params));
			}else if(menuCode.equalsIgnoreCase("Approval")) {
				returnObj.put(menuCode, coviMapperOne.getNumber("user.longPolling.selectApprovalListCnt", params));
			}else if(menuCode.indexOf("Board") > -1) {
				if(aclBoardFolder.isEmpty()) {
					Set<String> authorizedObjectCodeSet = ACLHelper.getACL(userDataObj, "FD", "Board", "V");
					String[] boardObjectArray = authorizedObjectCodeSet.toArray(new String[authorizedObjectCodeSet.size()]);
					
					if(boardObjectArray.length > 0) {
						aclBoardFolder = ACLHelper.join(boardObjectArray, ",");
						params.put("aclDataArr", boardObjectArray);
					}
				}
				params.put("aclData", "(" + aclBoardFolder + ")");
				
				if(menuCode.indexOf("|") > -1) {
					String [] detailMenuCode = menuCode.split("[|]");
					
					params.put("boardMenuID", detailMenuCode[detailMenuCode.length-1]);
				}
				
				if(params.containsKey("aclData")) {
					returnObj.put(menuCode, coviMapperOne.getNumber("user.longPolling.selectNonReadRecentlyMessageCnt", params));
				}
			}else if(menuCode.equalsIgnoreCase("Schedule")) {
				Set<String> authorizedObjectCodeSet = ACLHelper.getACL(userDataObj, "FD", "Schedule", "V");
				// 커뮤니티 일정을 가져오기 위해 권한을 가진 커뮤니티 목록을 가져옴. (커뮤니티 일정의 ObjectType이 Community로 저장되어 있음.)
				Set<String> authorizedObjectCodeSetCommunity = ACLHelper.getACL(userDataObj, "FD", "Community", "V");
				
				if (authorizedObjectCodeSetCommunity != null) {
					authorizedObjectCodeSet.addAll(authorizedObjectCodeSetCommunity);
				}
				
				String[] scheduleObjectArray = authorizedObjectCodeSet.toArray(new String[authorizedObjectCodeSet.size()]);
				String aclFolder = "";
				
				if(scheduleObjectArray.length > 0){
					aclFolder = ACLHelper.join(scheduleObjectArray, ",");
					params.put("aclDataArr", scheduleObjectArray);
				}
				
				params.put("aclData", "(" + aclFolder + ")");
				
				if(params.containsKey("aclData")) {
					params.put("scheduleMenuID", RedisDataUtil.getBaseConfig("ScheduleMenuID", userDataObj.getString("DN_ID")));
					returnObj.put(menuCode, coviMapperOne.getNumber("user.longPolling.selectTodayScheduleCnt", params));
				}
			}else if(menuCode.equalsIgnoreCase("Survey")) {
				returnObj.put(menuCode, coviMapperOne.getNumber("user.longPolling.selectSurveyCnt", params));
			}else if(menuCode.equalsIgnoreCase("Mail")) {
				returnObj.put(menuCode, getMailCount(userDataObj));
			}else if(menuCode.equalsIgnoreCase("Eum")){
				returnObj.put(menuCode, getEumCount(params));
			}
			
		}
		
		return returnObj;
	}
	
	private String getMailCount(CoviMap userDataObj) throws Exception {
		 	String retCnt = "0";
			boolean isSync = false;
			String apiURL = null;
			String sMode = "?job=NewMailCount"; //Mail 개수 조회
			
			try {
				String dnID = userDataObj.getString("DN_ID");
				
				isSync = RedisDataUtil.getBaseConfig("IsSyncIndi",dnID).equals("Y") ? true : false;			
				if(isSync && userDataObj.getString("UR_UseMailConnect").equals("Y") && !userDataObj.getString("UR_Mail").equals(""))
				{
					apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL",dnID).toString() + sMode;
						
					String urMail = userDataObj.getString("UR_Mail");
					String sLogonID = URLEncoder.encode(urMail.split("@")[0],"UTF-8");
					String sDomain = urMail.split("@")[1];
					
					apiURL = apiURL + "&id="+sLogonID+"&domain="+ sDomain;
					
					CoviMap jObj = getJson(apiURL);
					
					if(jObj.get("returnCode").toString().equals("0")) //Success
					{
						retCnt = jObj.get("result").toString();					
					}else{
						retCnt = "0";
					}			
				}else{
					retCnt ="0";
				}
			} catch(ArrayIndexOutOfBoundsException ex) {
				LOGGER.error("LongPollingSvcImpl getMailCount Error [" + userDataObj.getString("USERID") + "]" + "[Message : " +  ex.getMessage() + "]");
			} catch(NullPointerException ex) {
				LOGGER.error("LongPollingSvcImpl getMailCount Error [" + userDataObj.getString("USERID") + "]" + "[Message : " +  ex.getMessage() + "]");
			} catch(Exception ex) {
				LOGGER.error("LongPollingSvcImpl getMailCount Error [" + userDataObj.getString("USERID") + "]" + "[Message : " +  ex.getMessage() + "]");
			}
			
			return retCnt;
	}

	private CoviMap getJson(String encodedUrl) throws Exception {
		CoviMap resultList = new CoviMap();
		HttpURLConnectUtil url= new HttpURLConnectUtil();
		
		try {
			resultList = url.httpConnect(encodedUrl,"GET",1000,1000,"xform", "N");
		} catch(NullPointerException ex) {
			throw ex;
		} catch(Exception ex) {
			throw ex;
		} finally {
			url.httpDisConnect();
		}
		
		return resultList;
	}
	
	private String getEumCount(CoviMap params) throws Exception{
		String eumServerUrl = RedisDataUtil.getBaseConfig("eumServerUrl");
		String apiURL = eumServerUrl + "/restful/na/message/" + params.getString("userID");
		String retCnt = "";
		CoviMap retObj = getJson(apiURL);
		
		if(retObj.getString("status").equals("SUCCESS")){
			retCnt = retObj.getString("result");
		}else{
			retCnt = "0";
		}
		
		return retCnt;
	}
}
