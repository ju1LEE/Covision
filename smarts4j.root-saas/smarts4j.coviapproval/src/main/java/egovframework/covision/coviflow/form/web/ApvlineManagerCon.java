package egovframework.covision.coviflow.form.web;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.util.AuthHelper;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.coviflow.form.service.ApvlineManagerSvc;

/**
 * @Class Name : ApvlineManagerCon.java
 * @Description : 결재선 요청 처리 (일부 기능[부서 또는 사용자 목록 조회]은 조직도에 포함된 API로 처리)
 * @ 2017.06.28 최초생성
 *
 * @author 코비젼 연구소
 * @see Copyright (C) by Covision All right reserved.
 */

@Controller
public class ApvlineManagerCon {
	
	@Autowired
	private AuthHelper authHelper;

	private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private ApvlineManagerSvc apvlineManagerSvc;
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");
	
	/**
	 * approvalLine - 결재선 지정 요청 처리
	 * @param locale
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "approvalline.do")
	public ModelAndView approvalLine(Locale locale, Model model) {
		String returnURL = "forms/ApvlineManager";
		return new ModelAndView(returnURL);
	}
	
	/**
	 * multiReceiveline - 수신처 지정 요청 처리
	 * @param locale
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "multiReceiveline.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView multiReceiveLine(HttpServletRequest request, Locale locale, Model model) {
		String returnURL = "forms/MultiReceiveManager";
		ModelAndView mav = new ModelAndView(returnURL);
		String gbn = request.getParameter("gbn");
		mav.addObject("gbn", gbn);		
		return mav;
	}
	
	/**
	 * multiReceiveline - 수신처 지정 요청 처리
	 * @param locale
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "ReceiveManagerPopup.do", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView ReceiveManagerPopup(HttpServletRequest request, Locale locale, Model model) throws Exception {
		
		String returnURL = "forms/ReceiveManagerPopup";
		ModelAndView mav = new ModelAndView(returnURL);		
		return mav;
	}

	/**
	 * 수신처 별도 팝업
	 * @return mav
	 */
	@RequestMapping(value = "deployline.do", method = RequestMethod.GET)
	public ModelAndView deployLine(@RequestParam Map<String, String> paramMap) {
		String returnURL = "forms/DeployManager";
				
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("AllCompany", paramMap.get("AllCompany"));
		
		return mav;
	}

	/**
	 * 대외수신처 지정 팝업 - LDAP 조직도
	 * @return mav
	 */
	@RequestMapping(value = "govdoc/deployline.do", method = RequestMethod.GET)
	public ModelAndView deployGovLine(@RequestParam Map<String, String> paramMap) {
		String returnURL = "forms/DeployGovManager";
				
		ModelAndView mav = new ModelAndView(returnURL);
		mav.addObject("itemnum", paramMap.get("itemnum"));
		
		return mav;
	}
	
	/**
	 * getPrivateDomainList : 결재선 지정 우측에 위치하는 개인결재선 목록 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/getPrivateDomainList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getPrivateDomainList(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String userID = request.getParameter("userID");

		try {
			CoviMap params = new CoviMap();
			params.put("userID", userID);
			
			returnList.put("list",apvlineManagerSvc.selectPrivateDomainList(params).get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}

	/**
	 * deletePrivateDomain - 개인결재선 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/deletePrivateDomain.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap deletePrivateDomain(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap)
	{
		CoviMap returnList = new CoviMap();

		String privateDomainID = request.getParameter("privateDomainID");

		try {
			CoviMap params = new CoviMap();
			params.put("privateDomainID", privateDomainID);
			
			int cnt = apvlineManagerSvc.deletePrivateDomain(params);
			
			returnList.put("cnt",cnt);
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	
	/**
	 * getDistributionList - 공용 배포목록 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/getDistributionList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getDistributionList(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String entCode = request.getParameter("entCode");
		String type = StringUtil.replaceNull(request.getParameter("type"), "D");

		try {
			CoviMap params = new CoviMap();
			params.put("entCode", entCode);
			params.put("type", type);
			
			returnList.put("list",apvlineManagerSvc.selectDistributionList(params).get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}	
	
	/**
	 * getDistributionMember - 공용 배포목록 멤버 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/getDistributionMember.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getDistributionMember(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String groupID = request.getParameter("groupID");

		try {
			CoviMap params = new CoviMap();
			params.put("groupID", groupID);
			
			returnList.put("list",apvlineManagerSvc.selectDistributionMember(params).get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	/**
	 * updatePrivateDomain - 개인결재선 저장
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/insertPrivateDomain.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap insertPrivateDomainData(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String customCategory = request.getParameter("customCategory");
		String displayName = request.getParameter("displayName");
		String sAbstract = request.getParameter("abstract");
		String ownerID = request.getParameter("ownerID");
		String description = request.getParameter("description");
		String privateContext = StringUtil.replaceNull(request.getParameter("privateContext")).replace("&amp;", "&").replace("&quot;", "\"");

		try {
			CoviMap params = new CoviMap();
			params.put("customCategory", customCategory);
			params.put("displayName", ComUtils.RemoveScriptAndStyle(displayName));
			params.put("abstract", ComUtils.RemoveScriptAndStyle(sAbstract));
			params.put("ownerID", ownerID);
			params.put("description", ComUtils.RemoveScriptAndStyle(description));
			params.put("privateContext", privateContext);
			
			returnList.put("cnt",apvlineManagerSvc.insertPrivateDomainData(params));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	
	/**
	 * updatePrivateDomain - 개인결재선 멤버 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/updatePrivateDomain.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap updatePrivateDomain(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String privateDomainDataID = request.getParameter("privateDomainDataID");
		String sAbstract = request.getParameter("abstract");
		String privateContext = StringUtil.replaceNull(request.getParameter("privateContext")).replace("&amp;", "&").replace("&quot;", "\"");

		try {
			CoviMap params = new CoviMap();
			params.put("privateDomainDataID", privateDomainDataID);
			params.put("abstract", ComUtils.RemoveScriptAndStyle(sAbstract));
			params.put("privateContext", privateContext);
			
			returnList.put("cnt",apvlineManagerSvc.updatePrivateDomain(params));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	/**
	 * updatePrivateDomainDefault - 개인결재선 기본으로 지정
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/updatePrivateDomainDefault.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap updatePrivateDomainDefault(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String defaultYN = StringUtil.replaceNull(request.getParameter("defaultYN"));
		String userID = request.getParameter("userID");
		String privateDomainID = request.getParameter("PrivateDomainID");
		
		try {
			CoviMap params = new CoviMap();			
			params.put("userID", userID);
			params.put("PrivateDomainID", privateDomainID);
			
			int cnt = 0;
			if(defaultYN.equals("Y")){
				cnt = apvlineManagerSvc.updatePrivateDomainDefaultY(params);
			}else if(defaultYN.equals("N")){
				cnt = apvlineManagerSvc.updatePrivateDomainDefaultN(params);
			}			
			
			returnList.put("cnt",cnt);
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	
	/**
	 * getPrivateDistribution : 결재선 지정창 우측에 위치하는 개인수신처 목록조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/getPrivateDistribution.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getPrivateDistribution(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		String userID = request.getParameter("userID");	
		String type = StringUtil.replaceNull(request.getParameter("type"), "D");

		try {
			CoviMap params = new CoviMap();
			params.put("userID", userID);
			params.put("type", type);
			
			returnList.put("list",apvlineManagerSvc.selectPrivateDistribution(params).get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	
	/**
	 * getPrivateDistribution : 결재선 지정창 우측에 위치하는 개인수신처 목록조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/checkAbsentMember.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap checkAbsentMember(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		String userID = StringUtil.replaceNull(request.getParameter("users"));
		String[] userCodes = null;		
		
		try {
			if(userID.indexOf(';')>=0){
				userCodes = userID.split(";");
			}else{
				userCodes = new String[]{ userID };
			}
			
			CoviMap params = new CoviMap();
			params.put("userCodes", userCodes);
			returnList.put("list",apvlineManagerSvc.selectAbsentMember(params).get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	
	/**
	 * getPrivateDistributionMember : 특정 개인 수신처의 멤버 목록 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/getPrivateDistributionMember.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getPrivateDistributionMember(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		String groupID = request.getParameter("groupID");
		String CheckType = StringUtil.replaceNull(request.getParameter("CheckType"),"");
		
		try {
			CoviMap params = new CoviMap();
			params.put("groupID", groupID);
			//개인배포(외부전자 - 문서유통)
			if(CheckType.equals("privateGovDistributionCheckbox")){ 
				returnList.put("list",apvlineManagerSvc.selectPrivateGovDistributionMember(params).get("list"));
			//개인배포(내부전자 - CheckType:divtPersonDeployList || else)
			}else {
				returnList.put("list",apvlineManagerSvc.selectPrivateDistributionMember(params).get("list"));
			}
			
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	/**
	 * insertPrivateDistribution - 개인수신처 저장
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/insertPrivateDistribution.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap insertPrivateDistribution(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		
		String type = request.getParameter("type");
		String ownerID = request.getParameter("ownerID");
		String displayName = request.getParameter("displayName");
		String privateContext = StringUtil.replaceNull(request.getParameter("privateContext")).replace("&amp;", "&").replace("&quot;", "\"");
		CoviMap distributionLine = CoviMap.fromObject(privateContext);
		CoviList userLine = distributionLine.getJSONArray("user");
		CoviList groupLine = distributionLine.getJSONArray("group");
		
		try {
			CoviMap params = new CoviMap();
			CoviMap params2 = new CoviMap();
			
			params.put("type", type);
			params.put("ownerID", ownerID);
			params.put("displayName", ComUtils.RemoveScriptAndStyle(displayName));
			
			int cnt = apvlineManagerSvc.insertPrivateDistribution(params);
			
			for (int i = 0; i < userLine.size(); i++) {
				CoviMap obj = userLine.getJSONObject(i).getJSONObject("item");		
				params2.put("groupID",params.getInt("GroupID"));
				params2.put("type", "user");
				params2.put("receiptID",obj.getString("AN"));
				params2.put("receiptName",obj.getString("DN"));
				params2.put("receiptDeptID",obj.getString("RG"));
				params2.put("receiptDeptName",obj.getString("RGNM"));
				params2.put("DNName",obj.getString("ETNM"));
				params2.put("hasChild",obj.getString("hasChild"));
				params2.put("sortKey",obj.getString("SORTKEY"));
				
				cnt += apvlineManagerSvc.insertPrivateDistributionMember(params2);
			}
			
			for (int i = 0; i < groupLine.size(); i++) {
				CoviMap obj = groupLine.getJSONObject(i).getJSONObject("item");			
				params2.put("groupID",params.getInt("GroupID"));
				params2.put("type", "group");
				params2.put("receiptID",obj.getString("AN"));
				params2.put("receiptName",obj.getString("DN"));
				params2.put("receiptDeptID",obj.getString("RG"));
				params2.put("receiptDeptName",obj.getString("RGNM"));
				params2.put("DNName",obj.getString("ETNM"));
				params2.put("hasChild",obj.getInt("hasChild"));
				params2.put("sortKey",obj.getString("SORTKEY"));
				
				cnt += apvlineManagerSvc.insertPrivateDistributionMember(params2);
			}
			
			returnList.put("cnt",cnt);
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	/**
	 * deletePrivateDistribution - 개인수신처 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/deletePrivateDistribution.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap deletePrivateDistribution(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		String groupID = request.getParameter("groupID");

		try {
			CoviMap params = new CoviMap();
			params.put("groupID", groupID);
			
			int cnt = apvlineManagerSvc.deletePrivateDistribution(params);
			
			returnList.put("cnt",cnt);
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	/**
	 * deletePrivateDistributionMemberData - 개인수신처의 특정 멤버 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/deletePrivateDistributionMemberData.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap deletePrivateDistributionMemberData(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		String groupMemberID = request.getParameter("groupMemberID");
		try {
			CoviMap params = new CoviMap();
			params.put("groupMemberID", groupMemberID);
			
			int cnt = apvlineManagerSvc.deletePrivateDistributionMemberData(params);
			
			returnList.put("cnt",cnt);
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException npE) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?npE.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(npE.getLocalizedMessage(), npE);
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
			logger.error(e.getLocalizedMessage(), e);
		}
		return returnList;
	}
	
	// 사용자 > 작성 > 전결규정 팝업
	@RequestMapping(value = "getApprovalLinePopup.do")
	public ModelAndView goApprovalLinePopup(Locale locale, Model model) {
		String rtnUrl = "forms/ApvlinemgrRule";
		return new ModelAndView(rtnUrl);
	}
	
	/**
	 * getAddInLineAutoSearch : 문서 내 결재선 임직원/부서 자동완성 목록 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "apvline/getAddInLineAutoSearch.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getAddInLineAutoSearch(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();

		String searchText = request.getParameter("searchText");
		String searchType = request.getParameter("searchType");

		try {
			CoviMap params = new CoviMap();
			params.put("searchText", searchText);
			params.put("searchType", searchType);
			
			returnList.put("list", apvlineManagerSvc.selectAddInLineAutoSearchList(params).get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch(NullPointerException e){	
			logger.error(e.getLocalizedMessage(), e);
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			returnList.put("status", Return.FAIL);
			returnList.put("message", "Y".equals(isDevMode)?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		return returnList;
	}
}
