package egovframework.covision.groupware.portal.user.web;

import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;





import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.ClientInfoHelper;
import egovframework.baseframework.util.CookiesUtil;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.util.ACLHelper;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.groupware.portal.user.service.PortalSvc;

/**
 * @Class Name : PortalCon.java
 * @Description : 포탈 공통 컨테이너 Controller
 * @Modification Information 
 * @ 2017.06.19 최초생성
 *
 * @author 코비젼 연구소
 * @since 2017. 06.19
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */

@Controller
public class PortalCon {
	
	private Logger LOGGER = LogManager.getLogger(PortalCon.class);
	
	@Autowired
	private PortalSvc portalSvc;
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");
	
	@RequestMapping(value = "portal/home.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPortalHome(@RequestParam(value = "portalID", required = true, defaultValue="") String portalID, HttpServletRequest request) throws Exception {
		String returnURL = "portal";
		ModelAndView mav = new ModelAndView(returnURL);
		StringUtil func = new StringUtil();
		CookiesUtil cUtil = new CookiesUtil();
		
		boolean isMobile = ClientInfoHelper.isMobile(request);
		
		String key = cUtil.getCooiesValue(request);
		
		try{
			
			CoviMap userDataObj = SessionHelper.getSession(isMobile, key);
			Set<String> authorizedObjectCodeSet = ACLHelper.getACL(userDataObj, "PT", "", "V");
			
			String initPortalID = Objects.toString(userDataObj.getString("UR_InitPortal"),"0");
			String licInitPortal = Objects.toString(userDataObj.getString("UR_LicInitPortal"),"0");
			
			String userID = userDataObj.getString("USERID");
			
			// #1 조회할 포탈 ID 결정.
			if (licInitPortal.isEmpty() || licInitPortal.equals("0")){
				if(! portalID.isEmpty()){
					if(authorizedObjectCodeSet.contains(portalID)){
						if(initPortalID.equals("0")){
							CoviMap initParam = new CoviMap();
							initParam.put("userCode", userID);
							initParam.put("initPortalID", portalID);
							
							portalSvc.updateInitPortal(initParam);
							
						}
					}else{
						portalID = "0"; 	//권한 없음
					}
				}else{
					if( !(initPortalID.equals("0")) && authorizedObjectCodeSet.contains(initPortalID)){
						
						portalID = initPortalID;
					}else{
						String authPortalID = portalSvc.setInitPortal(authorizedObjectCodeSet, userID);
						//portalID = authPortalID.equals("0") ? "0" : authPortalID
						if(authPortalID.equals("0")){
							portalID = "0"; 	//권한 없음
							if(!initPortalID.equals("0")){
								CoviMap initParam = new CoviMap();
								initParam.put("userCode", userID);
								initParam.put("initPortalID", "0");
								
								portalSvc.updateInitPortal(initParam);
								
								SessionHelper.setSimpleSession("UR_InitPortal", "0", isMobile, key);
							}
						}else{
							portalID = authPortalID;
						}
					}
				}
				
				// #2 권한이 없을 경우 종료.
				if(portalID.equals("0")){ //권한 없음
					SessionHelper.setSimpleSession("UR_ThemeCode", "default", isMobile, key);
					
					mav.addObject("access", Return.NOT_AHTHENTICATION);
					mav.addObject("portalInfo", new CoviMap());
					mav.addObject("incResource", "");
					mav.addObject("layout", "");
					mav.addObject("javascriptString","");
					mav.addObject("data",new CoviList());
					return mav; 
				}
				
				// #3 포탈에 따라 테마 지정 및 세션값 업데이트
				if( !portalID.isEmpty() ){ 			
					String themeCode = portalSvc.getPortalTheme(portalID);
					SessionHelper.setSimpleSession("UR_ThemeCode", themeCode, isMobile, key);
				}
				
				if( ! portalID.equals(initPortalID) ){ 
					SessionHelper.setSimpleSession("UR_InitPortal", portalID, isMobile, key);
				}
			}else{
				String userId = userDataObj.getString("USERID");
				String jobSeq = userDataObj.getString("URBG_ID");	// 본직, 겸직 Seq 값
				String domainID = userDataObj.getString("DN_ID");
				//RedisDataUtil.setACL(userId + "_" + jobSeq, domainID, "PT", "", 7200);
						
				portalID = licInitPortal;
			}
			// #4 포탈 정보 조회
			CoviMap portalParma = new CoviMap();
			portalParma.put("portalID", portalID);
			CoviMap portalInfo = portalSvc.getPortalInfo(portalParma);
			if(!(func.f_NullCheck(portalInfo.get("URL").toString()).equals(""))){
				returnURL = "redirect:";
				
				if (portalInfo.get("PortalType").toString().equals("License"))
					returnURL += request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
				
				returnURL += portalInfo.getString("URL");
				//returnURL = "redirect:"+portalInfo.getString("URL");
				mav.setViewName(returnURL);
				return mav;
			}
			CoviList webpartList = portalSvc.getWebpartList(portalParma);
			
//				String javascriptString =  portalSvc.getJavascriptString(SessionHelper.getSession("lang", isMobile),webpartList);
			String javascriptString =  portalSvc.getJavascriptString(userDataObj.getString("lang"),webpartList);
			String layoutTemplate = portalSvc.getLayoutTemplate(webpartList, portalParma);
			String incResource = portalSvc.getIncResource(webpartList);
			CoviList webpartData = (CoviList)portalSvc.getWebpartData(webpartList, userDataObj);
			mav.setViewName(returnURL);
			mav.addObject("access", Return.SUCCESS);
			mav.addObject("portalInfo", portalInfo);
			mav.addObject("incResource", incResource);
			mav.addObject("layout", layoutTemplate);
			mav.addObject("javascriptString",javascriptString);
			mav.addObject("data",webpartData);
			
			return mav;
			
		} catch(NullPointerException e){
			LOGGER.error(e);
			mav.addObject("access", Return.FAIL);
			return mav;
		} catch(Exception e){
			LOGGER.error(e);
			mav.addObject("access", Return.FAIL);
			return mav;
		}
		
	}
	@RequestMapping(value = "portal/ceo_portal.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getCeoPortalHome(ModelAndView mav, HttpServletRequest request) throws Exception {
		String returnURL = "ceo_portal";

		boolean isMobile = ClientInfoHelper.isMobile(request);
		CookiesUtil cUtil = new CookiesUtil();
		String portalID = SessionHelper.getSession("UR_InitPortal", isMobile);
		String key = cUtil.getCooiesValue(request);
		try{
			CoviMap portalParma = new CoviMap();
			portalParma.put("portalID", portalID);
			CoviMap userDataObj = SessionHelper.getSession(isMobile, key);
			
			CoviList webpartList = portalSvc.getWebpartList(portalParma);
			
			String javascriptString =  portalSvc.getJavascriptString(userDataObj.getString("lang"),webpartList);
			String layoutTemplate = portalSvc.getLayoutTemplate(webpartList, portalParma);
			String incResource = portalSvc.getIncResource(webpartList);
			CoviList webpartData = (CoviList)portalSvc.getWebpartData(webpartList, userDataObj);
			
			
			mav.setViewName(returnURL);
			mav.addObject("access", Return.SUCCESS);
//			mav.addObject("portalInfo", portalInfo);
			mav.addObject("incResource", incResource);
			mav.addObject("layout", layoutTemplate);
			mav.addObject("javascriptString",javascriptString);
			mav.addObject("data",webpartData);
			
			return mav;
		} catch(NullPointerException e){
			LOGGER.error(e);
			mav.addObject("access", Return.FAIL);
			return mav;
		} catch(Exception e){
			LOGGER.error(e);
			mav.addObject("access", Return.FAIL);
			return mav;
		}
	}
	
	@RequestMapping(value = "portal/pn_portal.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPNPortalHome(ModelAndView mav, HttpServletRequest request) throws Exception {
		String returnURL = "pn_portal";
		
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CookiesUtil cUtil = new CookiesUtil();
		String portalID = SessionHelper.getSession("UR_InitPortal", isMobile);
		String key = cUtil.getCooiesValue(request);
		try{
			CoviMap portalParma = new CoviMap();
			portalParma.put("portalID", portalID);
			CoviMap userDataObj = SessionHelper.getSession(isMobile, key);
			
			CoviList webpartList = portalSvc.getWebpartList(portalParma);
			
			String javascriptString =  portalSvc.getJavascriptString(userDataObj.getString("lang"),webpartList);
			String layoutTemplate = portalSvc.getLayoutTemplate(webpartList, portalParma);
			String incResource = portalSvc.getIncResource(webpartList);
			CoviList webpartData = (CoviList)portalSvc.getWebpartData(webpartList, userDataObj);
			
			mav.setViewName(returnURL);
			mav.addObject("access", Return.SUCCESS);
//			mav.addObject("portalInfo", portalInfo);
			mav.addObject("incResource", incResource);
			mav.addObject("layout", layoutTemplate);
			mav.addObject("javascriptString",javascriptString);
			mav.addObject("data",webpartData);
			
			return mav;
		} catch(NullPointerException e){
			LOGGER.error(e);
			mav.addObject("access", Return.FAIL);
			return mav;
		} catch(Exception e){
			LOGGER.error(e);
			mav.addObject("access", Return.FAIL);
			return mav;
		}
	}
	
	//신규 포탈 개선
	@RequestMapping(value = "portal/mn_portal.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getMnPortalHome(ModelAndView mav, HttpServletRequest request) throws Exception {
		String returnURL = "mn_portal";
		mav.setViewName(returnURL);
		return mav;
	}
	
	/**
	 * goMyContentsSetPopup: 마이컨텐츠 설정 팝업 오픈
	 * @return mav
	 * @throws Exception
	 */
	@RequestMapping(value = "mycontents/goMyContentsSetPopup.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goMyContentsSetPopup(HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView("user/portal/MyContentsSetPopup");
		
		CoviMap params = new CoviMap();
		params.put("lang", SessionHelper.getSession("lang"));
		params.put("cMode", request.getParameter("contentsMode"));
		
		CoviList list = portalSvc.getMyContentsWebpartList(params);
		CoviList retList = new CoviList();
		for (int i=0; i< list.size();i++){
			CoviMap cmap = list.getMap(i);
			if (ComUtils.getAssignedBizSection(cmap.getString("BizSection"))){
				retList.add(cmap);
			}
		}
		mav.addObject("webpartList", retList);
		
		return mav;
	}


	/**
	 * saveMyContentsSetting - 마이컨텐츠 설정 저장
	 * @param portalID
	 * @return returnData
	 * @throws Exception
	 */
	@RequestMapping(value = "mycontents/saveMyContentsSetting.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap saveMyContentsSetting(
			@RequestParam(value = "webparts", required = true, defaultValue="") String webparts,
			@RequestParam(value = "contentsMode", required = true, defaultValue="") String contentsMode	) throws Exception{
		CoviMap returnData = new CoviMap();

		try {
			String[] webpartArr  = webparts.split("-"); 
		 
			CoviMap params = new CoviMap();
			params.put("cMode", contentsMode);
			params.put("userCode",SessionHelper.getSession("UR_Code"));
			if(!webparts.equals("")){
				params.put("webpartArr",webpartArr);
			}
			
			portalSvc.saveMyContentsSetting(params);
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}

		return returnData;
	}

	/**
	 * getMyContentSetWebpartList - 마이컨텐츠 설정 가져오기
	 * @param portalID
	 * @return returnData
	 * @throws Exception
	 */
	@RequestMapping(value = "mycontents/getMyContentSetWebpartList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getMyContentSetWebpartList(HttpServletRequest request) throws Exception{
		CoviMap returnData = new CoviMap();

		try {
			boolean isMobile = ClientInfoHelper.isMobile(request);
			CoviMap userDataObj = SessionHelper.getSession(isMobile);
			
			CoviMap params = new CoviMap();
			params.put("userCode", userDataObj.getString("UR_Code"));
			params.put("cMode", request.getParameter("contentsMode"));
			
			CoviList webpartList = portalSvc.getMyContentSetWebpartList(params);
			//CoviList list = portalSvc.getMyContentsWebpartList(params);
			CoviList retList = new CoviList();
			for (int i=0; i< webpartList.size();i++){
				CoviMap cmap = webpartList.getMap(i);
				if (ComUtils.getAssignedBizSection(cmap.getString("BizSection"))){
					retList.add(cmap);
				}
			}

			String javascriptString =  portalSvc.getJavascriptString(userDataObj.getString("lang"),retList);
			
			returnData.put("webpartList", retList);
			returnData.put("myContentsJavaScript", javascriptString);
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}

		return returnData;
	}
	
	/**
	 * getMyContentWebpartList - 마이컨텐츠  웹파트 가져오기
	 * @param portalID
	 * @return returnData
	 * @throws Exception
	 */
	@RequestMapping(value = "mycontents/getMyContentWebpartList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getMyContentWebpartList(HttpServletRequest request) throws Exception{
		CoviMap returnData = new CoviMap();
		
		try {
			boolean isMobile = ClientInfoHelper.isMobile(request);
			CoviMap userDataObj = SessionHelper.getSession(isMobile);
			
			CoviMap params = new CoviMap();
			String webpartID = request.getParameter("webpartID");
			params.put("webpartID", webpartID == null ? "" : webpartID);
			
			CoviList webpartList = portalSvc.getMyContentWebpartList(params);
			
			String javascriptString =  portalSvc.getJavascriptString(userDataObj.getString("lang"),webpartList);
			
			returnData.put("webpartList", webpartList);
			returnData.put("myContentsJavaScript", javascriptString);
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	/**
	 * goMyContentsSetPopup: 임직원 소식 팝업 오픈
	 * @return mav
	 * @throws Exception
	 */
	@RequestMapping(value = "portal/goEmployeesNoticeListPopup.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goEmployeesNoticeListPopup() throws Exception {
		ModelAndView mav = new ModelAndView("user/portal/EmployeesNoticePopup");		
		
		return mav;
	}
	
	/**
	 * getEmployeesNoticeList - 임직원 소식 팝업/웹파트
	 * @param page, paging, mode 
	 * @return returnData
	 * @throws Exception
	 */
	@RequestMapping(value = "portal/getEmployeesNoticeList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getEmployeesNoticeList(@RequestParam(value = "page", required = true, defaultValue="0") String page,
			@RequestParam(value = "selMode", required = true, defaultValue="ALL") String selMode,
			@RequestParam(value = "addinterval", required = true, defaultValue="0") String addinterval,
			@RequestParam(value = "searchName", required = false, defaultValue="") String searchName,
			@RequestParam(value = "birthMode", required = true, defaultValue="D") String birthMode,
			@RequestParam(value = "enterInterval", required = true, defaultValue="14") String enterInterval,
			@RequestParam(value = "callerId",required = true, defaultValue="") String callerId
			) throws Exception{
		CoviMap returnData = new CoviMap();

		try {
			CoviMap params = new CoviMap();
			
			String domainID = SessionHelper.getSession("DN_ID");

			String option = "";
			option = RedisDataUtil.getBaseConfig("Employees_WebpartDisplayOption");
			if(!option.equals("")) {
				String[] optionSet = option.split(";");
				if(optionSet != null) {
					for(int i=0; i<optionSet.length; i++) {
						params.put(optionSet[i],"Y");
					}
				}
			}else { //기초설정값이 없을 때 기초코드 참조
				CoviList BaseOptionSet = RedisDataUtil.getBaseCode("EmployeesNotice"); 
				for(int i=0; BaseOptionSet != null && i<BaseOptionSet.size(); i++) {
					CoviMap omap = BaseOptionSet.getMap(i);
					params.put(omap.getString("Code"),"Y");
				}
			}
			
			String orgMapOrderSet = RedisDataUtil.getBaseConfig("OrgMapOrderSet", domainID);
			if(!orgMapOrderSet.equals("")) {
                String[] orgOrders = orgMapOrderSet.split("\\|");
                params.put("orgOrders", orgOrders);
			}
			
			params.put("page",Integer.parseInt(page));
			
			if(callerId.equals("webpart")) {
				params.put("rowStart", Integer.parseInt(page) + 1);
	    		params.put("rowEnd", Integer.parseInt(page) + 5);
	    		params.put("paging","portal");
			}else {
				params.put("paging","popup");
			} 
			
			params.put("selMode",selMode);
			params.put("searchName",searchName);
			params.put("lang",SessionHelper.getSession("lang"));	
			params.put("DN_Code", SessionHelper.getSession("DN_Code"));
			params.put("addinterval",addinterval);
			params.put("birthMode",birthMode);
			params.put("enterInterval",enterInterval);
			
			CoviMap employeesList = portalSvc.getEmployeesNoticeList(params);
			
			returnData.put("employeesList", employeesList);
			returnData.put("status", Return.SUCCESS);
		} catch (ArrayIndexOutOfBoundsException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", e.getMessage());
		}

		return returnData;
	}
}
