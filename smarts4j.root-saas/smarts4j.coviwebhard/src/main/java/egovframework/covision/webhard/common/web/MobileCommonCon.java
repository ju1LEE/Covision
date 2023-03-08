package egovframework.covision.webhard.common.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import egovframework.baseframework.util.json.JSONSerializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import egovframework.coviframework.util.ACLHelper;
import egovframework.coviframework.util.CommonUtil;
import egovframework.baseframework.util.ClientInfoHelper;
import egovframework.baseframework.util.JsonUtil;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.covision.webhard.common.service.CommonSvc;


@Controller
@RequestMapping("/mobile")
public class MobileCommonCon {

	private Logger LOGGER = LogManager.getLogger(MobileCommonCon.class);
	
	private final String PARAM_SYS = "CLSYS";
	private final String PARAM_MODE = "CLMD";
	private final String PARAM_BIZ = "CLBIZ";
	private final String PARAM_SMU = "CSMU";

	/*
	 *  layout/tiles 처리 시작
	 * 
	 * 
	 */

	//(tiles-left) 햄버거/좌측 메뉴 표시를 위해 메뉴 목록을 조회해서 넘김 
	@RequestMapping(value = "layout/left.do", method = RequestMethod.GET)
	public ModelAndView getLeft(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonUtil jUtil = new JsonUtil();
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CoviMap userDataObj = SessionHelper.getSession(isMobile);
		String domainId = userDataObj.getString("DN_ID");
		
		String menu = ACLHelper.getMenu(userDataObj);
		
		String isAdmin = "N";
		String menuType = "Top";
		String memberOf = "0";

		CoviList menuArray = null;
		CoviList queriedMenu = null;
		if (StringUtils.isNoneBlank(menu)) {
			menuArray = jUtil.jsonGetObject(menu);
			queriedMenu = ACLHelper.parseMenu(domainId, isAdmin, menuType, memberOf, menuArray);
		}

		String returnURL = "mobile/MobileLeft";
		
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("MenuData", queriedMenu);

		return mav;
	}
	
	//(tiles-content) 상단 메뉴 목록을 조회해서 넘김(list만)
	@RequestMapping(value = "/{programName}/go{pageName}.do", method = RequestMethod.GET)
	public ModelAndView getTopMenu(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String programName,
			@PathVariable String pageName) throws Exception {
		
		boolean isMobile = ClientInfoHelper.isMobile(request);
		CoviMap userDataObj = SessionHelper.getSession(isMobile);
		
		String domainId = userDataObj.getString("DN_ID");
		String returnURL = "mobile/" + programName + "/" + pageName;
		String hasTopMenu = RedisDataUtil.getBaseConfig("hasTopMenu", domainId);
		if(StringUtils.isBlank(hasTopMenu)) {
			hasTopMenu = "";
		}
		
		ModelAndView mav = new ModelAndView(returnURL);
		if(hasTopMenu.indexOf(programName + "/" + pageName) > -1) {
			mav.addObject("Menu", getTopMenuList(programName, userDataObj));
		}
		
		return mav;
	}
	
	//상단 메뉴를 조회
	private CoviList getTopMenuList(String bizSection, CoviMap userDataObj) {
		CoviList result = null;
		JsonUtil jUtil = new JsonUtil();
		String domainId = userDataObj.getString("DN_ID");
		String isAdmin = "N";
		String menuType = "Left";
		
		String menu = "";
		
		try {
			menu = ACLHelper.getMenu(userDataObj);
			
			if (StringUtils.isNoneBlank(menu)) {
				result = jUtil.jsonGetObject(menu);
				result = ACLHelper.parseMenuByBiz(domainId, isAdmin, bizSection, menuType, result, userDataObj.getString("lang"));
			}
		} catch(NullPointerException e){
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch(Exception e){
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		
		return result;
	}
	
	
	
	
	/*
	 *  layout/tiles 처리 끝
	 * 
	 * 
	 */
	
	
	// 일반 페이지 호출 처리 
	// ex> /mobile/board/list.do 
	//       => /mobile/board/list.mobile
	//       => /WEB-INF/views/mobile/board/list.jsp
	@RequestMapping(value = "/{programName}/{pageName}.do", method = RequestMethod.GET)
	public ModelAndView getContent(HttpServletRequest request, HttpServletResponse response, 
				@PathVariable String programName, 
				@PathVariable String pageName) throws Exception {
			
		//String returnURL = "mobile/" + programName.toLowerCase() + "/" + pageName + ".mobile";
		
		String isPopup = request.getParameter("IsPopup");
		
		if("list".equals(pageName)) {
			isPopup = "";
		}
			
		String returnURL = "mobile/" + programName.toLowerCase() + "/" + pageName + ((StringUtils.isNoneBlank(isPopup) && isPopup.equals("Y")) ? "" : ".mobile");
		
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("QueryString", request.getQueryString());
	
		return mav;
	}
	
	private Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
	    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
	    if(url == null)
	    	url = "";
	    String[] pairs = url.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    return query_pairs;
	}

}
