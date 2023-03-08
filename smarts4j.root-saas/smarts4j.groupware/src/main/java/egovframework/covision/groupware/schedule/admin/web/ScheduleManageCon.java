package egovframework.covision.groupware.schedule.admin.web;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import org.apache.commons.lang.StringEscapeUtils;
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
import egovframework.coviframework.util.ComUtils;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.AuthorityService;
import egovframework.coviframework.util.AuthHelper;
import egovframework.covision.groupware.schedule.admin.service.ScheduleManageSvc;

@Controller
public class ScheduleManageCon {
	@Autowired
	private AuthHelper authHelper;
	@Autowired
	private AuthorityService authSvc;
	
	@Autowired
	private ScheduleManageSvc scheduleManageSvc;
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");
	
	/**
	 * 일정관리 관리자 - 일정 폴더 트리 데이터 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getFolderTreeList.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getFolderTreeList(HttpServletRequest request, 
			@RequestParam(value = "MenuID", required = true, defaultValue = "") String menuID,
			@RequestParam(value = "FolderType", required = true, defaultValue = "") String folderType,
			@RequestParam(value = "lang", required = true, defaultValue = "") String lang,
			@RequestParam(value = "DomainID", required = true, defaultValue = "") String domainID) throws Exception
	{
		CoviList resultList = new CoviList();
		CoviMap returnList = new CoviMap();
		
		try {
			
			CoviMap params = new CoviMap();
			
			params.put("MenuID", menuID);
			params.put("FolderType", folderType);
			params.put("lang", lang);
			params.put("DomainID", domainID);
			
			resultList = scheduleManageSvc.selectFolderTreeList(params);
			
			CoviList folderList = new CoviList();
			
			for(Object jsonobject : resultList){
				CoviMap folderData = (CoviMap) jsonobject;
				// 트리를 그리기 위한 데이터
				folderData.put("no", StringUtil.getSortNo(folderData.get("SortPath").toString()));
				folderData.put("nodeName", folderData.getString("FolderName"));
				folderData.put("nodeValue", folderData.get("FolderID"));
				folderData.put("pno", StringUtil.getParentSortNo(folderData.get("SortPath").toString()));
				folderData.put("chk", "N");
				folderData.put("rdo", "N");
				
				// 폴더 타입 (트리 표시를 위한 타입)
				//최상위 폴더의 표시명 변경
				String folderTypeStr = folderData.getString("FolderType");
				if(folderTypeStr.equals("Schedule.Total") || folderTypeStr.equals("Schedule.Cafe") || folderTypeStr.equals("Schedule.Theme")){
					folderData.put("type", "sch_root");
					folderData.put("nodeName", DicHelper.getDic("lbl_Whole"));				//전체
					folderData.put("onclick", "CoviMenu_GetContent(\"/groupware/layout/schedule_FolderManage.do?CLSYS=schedule&CLMD=admin&CLBIZ=Schedule&FolderID="+folderData.get("FolderID")+"\")");
				}else if(folderTypeStr.equals("Schedule")){
					folderData.put("type", "sch_normal");
					folderData.put("url", "#");
					folderData.put("onclick", "addScheduleFolderData("+folderData.getString("FolderID")+")");
				}else{
					folderData.put("type", "sch_folder");
					folderData.put("onclick", "CoviMenu_GetContent(\"/groupware/layout/schedule_FolderManage.do?CLSYS=schedule&CLMD=admin&CLBIZ=Schedule&FolderID="+folderData.get("FolderID")+"\")");
				}
				
				folderList.add(folderData);
			}
			
			returnList.put("list", folderList);
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
		
	}
	
	/**
	 * 일정관리 관리자 - 일정 폴더 타이틀명 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getFolderDisplayName.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getFolderDisplayName(HttpServletRequest request, 
			@RequestParam(value = "FolderID", required = true, defaultValue = "") String folderID,
			@RequestParam(value = "lang", required = true, defaultValue = "") String lang) throws Exception
	{
		CoviMap returnList = new CoviMap();
		CoviMap resultObj = new CoviMap();
		
		try {
			CoviMap param = new CoviMap();
			param.put("FolderID", folderID);
			param.put("lang", lang);
			
			resultObj = scheduleManageSvc.selectFolderDisplayName(param);
			
			returnList.put("value", resultObj.getString("MultiDisplayName"));
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 일정 폴더 리스트 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getAdminFolderList.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getAdminFolderList(HttpServletRequest request, 
			@RequestParam(value = "FolderID", required = true, defaultValue = "") String folderID,
			@RequestParam(value = "MenuID", required = true, defaultValue = "") String menuID,
			@RequestParam(value = "FolderType", required = true, defaultValue = "") String folderType,
			@RequestParam(value = "selectSearchType", required = true, defaultValue = "") String selectSearchType,
			@RequestParam(value = "searchValue", required = true, defaultValue = "") String searchValue,
			@RequestParam(value = "lang", required = true, defaultValue = "") String lang,
			@RequestParam(value = "DomainID", required = true, defaultValue = "") String domainID,
			@RequestParam(value = "sortBy", required = true, defaultValue = "") String sortStr,
			@RequestParam(value = "pageNo", required = true, defaultValue = "") String pageNoStr,
			@RequestParam(value = "pageSize", required = true, defaultValue = "") String pageSizeStr) throws Exception
	{
		CoviMap resultList = new CoviMap();
		CoviMap returnList = new CoviMap();
		
		try {
			sortStr = sortStr.split(",")[0];
//			String sortColumn = sortStr.split(" ")[0];
//			String sortDirection = sortStr.split(" ")[1];
			
			int pageSize = 1;
			int pageNo =  Integer.parseInt(pageNoStr);
			if (pageSizeStr != null && pageSizeStr.length() > 0){
				pageSize = Integer.parseInt(pageSizeStr);	
			}
			
			CoviMap params = new CoviMap();

			params.put("FolderID", folderID);
			params.put("MenuID", menuID);
			params.put("FolderType", folderType);
			params.put("selectSearchType", selectSearchType);
			params.put("searchValue", ComUtils.RemoveSQLInjection(searchValue, 100));
			params.put("lang", lang);
			params.put("DomainID", domainID);
//			params.put("sortColumn", ComUtils.RemoveSQLInjection(sortColumn, 100));
//			params.put("sortDirection", ComUtils.RemoveSQLInjection(sortDirection, 100));
			params.put("pageSize", pageSize);
			params.put("pageNo", pageNo);

			int cnt = scheduleManageSvc.selectFolderListCount(params);
			params.addAll(ComUtils.setPagingData(params,cnt));
			
			resultList = scheduleManageSvc.selectFolderList(params);
			
			returnList.put("page", params);
			returnList.put("list", resultList.get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (NumberFormatException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
		
	}
	
	/**
	 * 일정관리 관리자 - 일정 폴더 팝업
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "schedule/adminFolderPopup.do", method = RequestMethod.GET)
	public ModelAndView goScheduleFolderPopup(@RequestParam Map<String, String> paramMap, Locale locale, Model model) {
		ModelAndView mav = new ModelAndView("admin/schedule/FolderPopup");
		mav.addAllObjects(paramMap);
		return mav;
	}
	
	/**
	 * 일정관리 관리자 - 일정 폴더 팝업에서 색깔 지정
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "schedule/goScheduleFolderColor.do", method = RequestMethod.GET)
	public ModelAndView goScheduleFolderColor(Locale locale, Model model) {
		String returnURL = "admin/schedule/FolderColor";
		return new ModelAndView(returnURL);
	}
	
	/**
	 * 일정관리 관리자 - 연결 일정 Select Box 목록
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getLinkFolderListData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getLinkFolderListData(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		CoviList resultObj = new CoviList();
		
		try {
			// 선택값 추가
			CoviMap none = new CoviMap();
			none.put("optionText", DicHelper.getDic("lbl_Choice"));			//선택
			none.put("optionValue", "none");
			resultObj.add(none);

			CoviMap params = new CoviMap();
			params.put("manageCompany", request.getParameter("manageCompany"));
			
			resultObj.addAll(scheduleManageSvc.selectLinkFolderListData(params));
			
			returnList.put("list", resultObj);
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 일정유형 Select Box 목록
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getFolderTypeData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getFolderTypeData(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> paramMap) throws Exception
	{
		CoviMap returnList = new CoviMap();
		CoviList resultObj = new CoviList();
		
		try {
			// 선택값 추가
			CoviMap none = new CoviMap();
			none.put("optionText", DicHelper.getDic("lbl_Choice"));			//선택
			none.put("optionValue", "none");
			resultObj.add(none);
			
			resultObj.addAll(scheduleManageSvc.selectFolderTypeData());
			
			returnList.put("list", resultObj);
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 폴더 생성
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/saveAdminFolderData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap saveAdminFolderData(HttpServletRequest request, 
			@RequestParam(value = "mode", required = true, defaultValue = "") String mode,
			@RequestParam(value = "folderData", required = true, defaultValue = "") String folderDataStr,
			@RequestParam(value = "aclData", required = true, defaultValue = "") String aclDataStr,
			@RequestParam(value = "aclActionData", required = true, defaultValue = "") String aclActionDataStr) throws Exception{
		
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap folderDataObj = CoviMap.fromObject(StringEscapeUtils.unescapeHtml(folderDataStr));
			CoviList aclDataObj = new CoviList();
			CoviList aclActionDataObj = new CoviList();
			
			if(!aclDataStr.equals("[]")){
				aclDataObj = CoviList.fromObject(StringEscapeUtils.unescapeHtml(aclDataStr));
			}
			
			if(!aclActionDataStr.equals("[]")){
				aclActionDataObj = CoviList.fromObject(StringEscapeUtils.unescapeHtml(aclActionDataStr));
			}
			
			CoviMap params = new CoviMap();
			params.put("FolderData", folderDataObj);
			params.put("ACLData", aclDataObj);
			
			if(mode.equals("U")){
				scheduleManageSvc.updateFolderData(params);
				
				if(aclActionDataObj.size() > 0) authSvc.syncActionACL(aclActionDataObj, "FD_SCHEDULE");
			}else{
				if (SessionHelper.getSession("isEasyAdmin").equals("Y")){
					folderDataObj.put("SecurityLevel", "256");
					folderDataObj.put("IsUse", "Y");
					folderDataObj.put("IsDisplay", "Y");
					folderDataObj.put("IsURL", "N");
					folderDataObj.put("Reserved4", "Y");
				}
				
				folderDataObj.put("ScheduleType","Schedule."+folderDataObj.get("ScheduleType"));
				scheduleManageSvc.insertFolderData(params);
				
				if(aclActionDataObj.size() > 0) authSvc.syncActionACL(aclActionDataObj, "FD_SCHEDULE");
			}
			
			returnList.put("status", Return.SUCCESS);			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 일정 폴더 데이터 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/deleteAdminFolderData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap deleteAdminFolderData(HttpServletRequest request, 
			@RequestParam(value = "folderData", required = true, defaultValue = "") String folderDataStr) throws Exception
	{
		CoviMap returnList = new CoviMap();
		
		try {
			CoviList folderDataObj = CoviList.fromObject(StringEscapeUtils.unescapeHtml(folderDataStr));
			
			CoviList params = new CoviList();
			params.addAll(folderDataObj);
			
			scheduleManageSvc.deleteFolderData(params);
			
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 폴더 데이터 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getAdminFolderData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getAdminFolderData(HttpServletRequest request, 
			@RequestParam(value = "FolderID", required = true, defaultValue = "") String folderID,
			@RequestParam(value = "ObjectID", required = true, defaultValue = "") String objectID,
			@RequestParam(value = "ObjectType", required = true, defaultValue = "") String objectType) throws Exception
	{
		CoviMap returnList = new CoviMap();
		CoviMap resultObj = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("FolderID", folderID);
			params.put("objectType", objectType);
			params.put("objectID", objectID);
			
			CoviList aclArray = authSvc.selectACL(params);
			
			resultObj = scheduleManageSvc.selectFolderData(params);
			
			returnList.put("aclData", aclArray);
			returnList.put("data", resultObj);
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 공유 일정 데이터 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getAdminShareList.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getShareScheduleData(HttpServletRequest request, 
			@RequestParam(value = "domainID", required = true, defaultValue = "") String domainID,
			@RequestParam(value = "selectSearchType", required = true, defaultValue = "") String selectSearchType,
			@RequestParam(value = "shareUserName", required = true, defaultValue = "") String shareUserName,
			@RequestParam(value = "startDate", required = true, defaultValue = "") String startDate,
			@RequestParam(value = "endDate", required = true, defaultValue = "") String endDate,
			@RequestParam(value = "sortBy", required = true, defaultValue = "") String sortStr,
			@RequestParam(value = "pageNo", required = true, defaultValue = "") String pageNoStr,
			@RequestParam(value = "pageSize", required = true, defaultValue = "") String pageSizeStr) throws Exception
	{
		CoviMap resultList = new CoviMap();
		CoviMap returnList = new CoviMap();
		
		try {
			String sortColumn = sortStr.split(" ")[0];
			String sortDirection = sortStr.split(" ")[1];
			
			int pageSize = 1;
			int pageNo =  Integer.parseInt(pageNoStr);
			if (pageSizeStr != null && pageSizeStr.length() > 0){
				pageSize = Integer.parseInt(pageSizeStr);	
			}
			
			CoviMap params = new CoviMap();
			
			params.put("domainID", domainID);
			params.put("selectSearchType", selectSearchType);
			params.put("shareUserName", shareUserName);
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			params.put("sortColumn", ComUtils.RemoveSQLInjection(sortColumn, 100));
			params.put("sortDirection", ComUtils.RemoveSQLInjection(sortDirection, 100));
			params.put("pageSize", pageSize);
			params.put("pageNo", pageNo);
			params.put("lang", SessionHelper.getSession("lang"));
			
			int cnt = scheduleManageSvc.selectShareScheduleDataCount(params);
			params.addAll(ComUtils.setPagingData(params,cnt));
			
			resultList = scheduleManageSvc.selectShareScheduleData(params);
			
			returnList.put("page", params);
			returnList.put("list", resultList.get("list"));
			returnList.put("result", "ok");
			returnList.put("status", Return.SUCCESS);
			
		} catch (ArrayIndexOutOfBoundsException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
		
	}

	
	/**
	 * 일정관리 관리자 - 공유 일정 등록 및 수정 팝업
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "schedule/adminSharePopup.do", method = RequestMethod.GET)
	public ModelAndView goShareSchedulePopup(Locale locale, Model model) {
		String returnURL = "admin/schedule/SharePopup";
		return new ModelAndView(returnURL);
	}
	
	/**
	 * 일정관리 관리자 - 공유 일정 설정 등록
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "schedule/saveAdminShareData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap saveAdminShareData(HttpServletRequest request, 
			@RequestParam(value = "mode", required = true, defaultValue = "") String mode,
			@RequestParam(value = "TargetDataArr", required = true, defaultValue = "") String targetDataArrStr,
			@RequestParam(value = "SpecifierCode", required = true, defaultValue = "") String specifierCode,
			@RequestParam(value = "SpecifierName", required = true, defaultValue = "") String specifierName,
			@RequestParam(value = "RegisterCode", required = true, defaultValue = "") String registerCode) throws Exception
	{
		CoviMap returnList = new CoviMap();
		
		try {
			if(mode.equals("U")){
				CoviMap param = new CoviMap();
				param.put("SpecifierCode", specifierCode);
				
				scheduleManageSvc.deleteShareData(param);
				
				/*
				 * //권한 데이터 삭제
				 */
			}
			
			CoviList targetDataArr = CoviList.fromObject(StringEscapeUtils.unescapeHtml(targetDataArrStr));
			
			CoviList paramsList = new CoviList();
			for(Object obj : targetDataArr){
				CoviMap targetDataObj = (CoviMap)obj;
				
				CoviMap params = new CoviMap();
				
				params.putAll((Map)targetDataObj); 
				params.put("SpecifierCode", specifierCode);
				params.put("SpecifierName", specifierName);
				params.put("RegisterCode", registerCode);
				
				paramsList.add(params);
			}
			scheduleManageSvc.insertShareData(paramsList);
			
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
		
	}
	
	/**
	 * 일정관리 관리자 - 공유 일정 데이터 개별 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/getAdminShareData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getAdminShareData(HttpServletRequest request, 
			@RequestParam(value = "SpecifierCode", required = true, defaultValue = "") String specifierCode) throws Exception
	{
		CoviMap resultList = new CoviMap();
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("SpecifierCode", specifierCode);
			resultList = scheduleManageSvc.selectOneShareScheduleData(params);
			
			returnList.put("list", resultList.get("list"));
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * 일정관리 관리자 - 공유 일정 데이터 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/deleteAdminShareData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap deleteAdminShareData(HttpServletRequest request, 
			@RequestParam(value = "mode", required = true, defaultValue = "") String mode,
			@RequestParam(value = "SpecifierCode", required = true, defaultValue = "") String specifierCode) throws Exception
	{
		CoviMap returnList = new CoviMap();
		
		try {
			
			if(mode.equals("U")){
				CoviMap param = new CoviMap();
				param.put("SpecifierCode", specifierCode);
				
				scheduleManageSvc.deleteShareData(param);
			}
			
			returnList.put("status", Return.SUCCESS);
			
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y")?e.getMessage():DicHelper.getDic("msg_apv_030"));
		}
		return returnList;
	}
	
	/**
	 * 게시판 권한 정보 조회
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "schedule/selectBoardACLData.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap selectBoardACLData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		CoviMap returnData = new CoviMap();
		CoviList result = new CoviList();
		StringUtil func = new StringUtil();
		
		try {
			String objectID = func.f_NullCheck(request.getParameter("objectID"));
			String objectType = func.f_NullCheck(request.getParameter("objectType"));
			String scheduleType = !func.f_NullCheck(request.getParameter("ScheduleType")).equals("") ? "Schedule." + request.getParameter("ScheduleType") : "";
			
			CoviMap params = new CoviMap();
			params.put("objectID", objectID);
			params.put("objectType", objectType);
			params.put("ScheduleType", scheduleType);
					 
			if(StringUtil.isEmpty(objectID)) {
				CoviMap ids = scheduleManageSvc.selectDomainMenuID(params);
				params.put("objectID", ids.getString("FolderID"));
			}
			
			result = authSvc.selectACL(params);
			
		    returnData.put("data", result);
		    returnData.put("inheritedObjectID", params.getString("objectID"));
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
}
