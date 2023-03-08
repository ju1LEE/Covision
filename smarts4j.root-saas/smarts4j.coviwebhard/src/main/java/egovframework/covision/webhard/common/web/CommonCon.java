package egovframework.covision.webhard.common.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.ClientInfoHelper;
import egovframework.baseframework.util.JsonUtil;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.AuthorityService;
import egovframework.coviframework.util.ACLHelper;
import egovframework.coviframework.util.CommonUtil;


/**
 * @Class Name : CommonCon.java
 * @Description : 공통컨트롤러
 * @Modification Information 
 * @ 2019.02.07 최초생성
 *
 * @author 코비젼 연구소
 * @since 2019.02.07
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
@Controller
public class CommonCon {
	
	private static final String PARAM_SYS = "CLSYS";
	private static final String PARAM_MODE = "CLMD";
	private static final String PARAM_BIZ = "CLBIZ";
	
	@Autowired
	private AuthorityService authSvc;

	@RequestMapping(value = "layout/admin/include.do")
	public ModelAndView getAdminInclude(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String returnURL = "webhard/common/layout/adminInclude";
		ModelAndView mav = new ModelAndView(returnURL);

		return mav;
	}
	
	@RequestMapping(value = "layout/admin/header.do")
	public ModelAndView getAdminHeader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonUtil jUtil = new JsonUtil();
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CoviMap userDataObj = SessionHelper.getSession(isMobile);
		
		String isAdmin = "Y";
		String returnURL = "webhard/common/layout/adminHeader";
		String domainId = userDataObj.getString("DN_ID");
		String menuStr = ACLHelper.getMenu(userDataObj);

		CoviList queriedMenu = null;
		
		if (StringUtils.isNoneBlank(menuStr)) {
			CoviList menuArray = jUtil.jsonGetObject(menuStr);
			// menuArray로 부터 menuType, BizSection별로 쿼리
			queriedMenu = ACLHelper.parseMenu(domainId, isAdmin, "Top", "0", menuArray);
		}
		
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("topMenuData", queriedMenu);
		
		return mav;
	}
	
	@RequestMapping(value = "layout/admin/left.do")
	public ModelAndView getAdminLeft(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> queries = splitQuery(StringUtil.replaceNull(request.getQueryString(),""));
		JsonUtil jUtil = new JsonUtil();
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CoviMap userDataObj = SessionHelper.getSession(isMobile);
		
		String bizSect = Objects.toString(queries.get(PARAM_BIZ), "");
		String loadContent = Objects.toString(queries.get("loadContent"), "false");
		String domainId = userDataObj.getString("DN_ID");
		String menuStr = ACLHelper.getMenu(userDataObj);
		String isAdmin = "Y";
		
		CoviList queriedMenu = null;
		
		if (StringUtils.isNoneBlank(menuStr)) {
			CoviList menuArray = jUtil.jsonGetObject(menuStr);
			// menuArray로 부터 menuType, BizSection별로 쿼리
			queriedMenu = ACLHelper.parseMenuByBiz(domainId, isAdmin, bizSect, "Left", menuArray, userDataObj.getString("lang"));
		}

		String returnURL = "webhard/common/layout/adminLeft";
		
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("domainId", domainId);
		mav.addObject("isAdmin", isAdmin);
		mav.addObject("leftMenuData", queriedMenu);
		mav.addObject("loadContent", loadContent);
		
		return mav;
	}
	
	@RequestMapping(value = "layout/user/include.do")
	public ModelAndView getUserInclude(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String returnURL = "webhard/common/layout/userInclude";
		ModelAndView mav = new ModelAndView(returnURL);

		return mav;
	}
	
	@RequestMapping(value = "layout/user/header.do")
	public ModelAndView getHeader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CommonUtil commonUtil = new CommonUtil();
		StringUtil func = new StringUtil();
		CoviMap params = new CoviMap();//
		JsonUtil jUtil = new JsonUtil();
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CoviMap userDataObj = SessionHelper.getSession(isMobile);
		
		String isAdmin = "N";
		String returnURL = "webhard/common/layout/userHeader";
		String domainId = userDataObj.getString("DN_ID");
		String menuStr = ACLHelper.getMenu(userDataObj);

		String authType = PropertiesUtil.getSecurityProperties().getProperty("admin.auth.type");
		String adminAuth = "N";
		String adminSession = userDataObj.getString("isAdmin");
		
		if (func.f_NullCheck(authType).equals("Y") && func.f_NullCheck(adminSession).equals("Y")) {
			String ipaddress = func.getRemoteIP(request);
			String[] ip = ipaddress.split("\\.");
			String partIPAddress = String.format("%03d.", Integer.parseInt(ip[0]))+String.format("%03d.", Integer.parseInt(ip[1]))+String.format("%03d.", Integer.parseInt(ip[2]))+String.format("%03d", Integer.parseInt(ip[3]));
			
			params.put("domainID", domainId);
			params.put("partIPAddress", partIPAddress);

			if (authSvc.selectTwoFactorIpCheck(params, "A") == 0) {
				adminAuth = "N";
			} else {
				adminAuth = "Y";
			}
		} else if (func.f_NullCheck(adminSession).equals("Y")) {
			adminAuth = "Y";
		}
		
		CoviMap myInfoData = commonUtil.makeMyInfoData(authType,adminAuth,userDataObj); // MyInfo 사이드 메뉴 전용
		
		CoviList queriedMenu = null;
		
		if (StringUtils.isNoneBlank(menuStr)) {
			CoviList menuArray = jUtil.jsonGetObject(menuStr);
			// menuArray로 부터 menuType, BizSection별로 쿼리
			queriedMenu = ACLHelper.parseMenu(domainId, isAdmin, "Top", "0", menuArray);
		}
		
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("topMenuData", queriedMenu);
		mav.addObject("myInfoData", myInfoData);
		
		return mav;
	}
	
	@RequestMapping(value = "layout/user/left.do")
	public ModelAndView getLeft(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> queries = splitQuery(StringUtil.replaceNull(request.getQueryString(),""));
		JsonUtil jUtil = new JsonUtil();
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CoviMap userDataObj = SessionHelper.getSession(isMobile);
		
		String bizSect = Objects.toString(queries.get(PARAM_BIZ), "");
		String loadContent = Objects.toString(queries.get("loadContent"), "false");
		String domainId = userDataObj.getString("DN_ID");
		String menuStr = ACLHelper.getMenu(userDataObj);
		String isAdmin = "N";
		
		CoviList queriedMenu = null;
		
		if (StringUtils.isNoneBlank(menuStr)) {
			CoviList menuArray = jUtil.jsonGetObject(menuStr);
			// menuArray로 부터 menuType, BizSection별로 쿼리
			queriedMenu = ACLHelper.parseMenuByBiz(domainId, isAdmin, bizSect, "Left", menuArray, userDataObj.getString("lang"));
		}

		String returnURL = "webhard/common/layout/userLeft";
		
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("domainId", domainId);
		mav.addObject("isAdmin", isAdmin);
		mav.addObject("leftMenuData", queriedMenu);
		mav.addObject("loadContent", loadContent);
		
		return mav;
	}
	
	private Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
	    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
	    String[] pairs = url.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    return query_pairs;
	}
}
