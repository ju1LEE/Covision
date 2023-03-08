package egovframework.covision.groupware.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.ClientInfoHelper;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.groupware.base.service.LongPollingSvc;

/**
 * @Class Name : LongPollingCon.java
 * @Description : long polling 요청 처리
 * @Modification Information 
 * @ 2017.04.21 최초생성
 *
 * @author 코비젼 연구소
 * @since 2017. 04.21
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
@Controller
public class LongPollingCon {
	
	private Logger LOGGER = LogManager.getLogger(LongPollingCon.class);
	
	@Autowired
	private LongPollingSvc longPollingSvc; 
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");

	@RequestMapping(value = "callablelongpolling.do", method = RequestMethod.POST)
	public @ResponseBody Callable<CoviMap> callableLongPolling(final HttpServletRequest request,final HttpServletResponse response) throws Exception{
		
		return new Callable<CoviMap>() {
			@Override
			public CoviMap call() throws Exception {
				CoviMap returnData = new CoviMap();
				
				try{
					String msg = request.getParameter("message");
					
					//WsServer.sendMessage(msg);
					Thread.sleep(2000);
					
					returnData.put("status", Return.SUCCESS);
					returnData.put("message", "long polling server pushed : " + msg);
					
				} catch (NullPointerException e) {
					returnData.put("status", Return.FAIL);
					returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
				} catch (Exception e) {
					returnData.put("status", Return.FAIL);
					returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
				}
				
				return returnData;
			}
		};
		
	}
	
	@RequestMapping(value = "longpolling.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap longPolling(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		CoviMap returnData = new CoviMap();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String msg = request.getParameter("message");
			
			// WsServer.sendMessage(msg);
			returnData.put("status", Return.SUCCESS);
			returnData.put("message", sdf.format(new Date()) + " long polling server pushed : " + msg);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}

		return returnData;
		
	}
	
	@RequestMapping(value = "longpolling/getQuickData.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getQuickData(
			@RequestParam(value = "menuListStr", required = true, defaultValue="") String menuListStr, HttpServletRequest request) throws Exception{
		
		CoviMap returnData = new CoviMap();
		CoviMap resultObj = new CoviMap();
		
		try {
			
			boolean isMobile = ClientInfoHelper.isMobile(request);
			CoviMap userDataObj = SessionHelper.getSession(isMobile);
			
			String userId = userDataObj.getString("USERID");
			ArrayList<String> menuList = new ArrayList<String>(Arrays.asList(menuListStr.split(";")));
		    
			CoviMap params = new CoviMap();
			params.put("menuList", menuList);
			params.put("userID", userId);
			params.put("localCurrentDate", ComUtils.GetLocalCurrentDate(ComUtils.ServerDateFullFormat));
			
			resultObj = longPollingSvc.getQuickMenuCount(params, userDataObj); 
			
			returnData.put("status", Return.SUCCESS);
			returnData.put("countObj",resultObj);
		} catch (NullPointerException e) {
			returnData.put("countObj", new CoviMap());
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("countObj", new CoviMap());
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}

		return returnData;
		
	}
}
