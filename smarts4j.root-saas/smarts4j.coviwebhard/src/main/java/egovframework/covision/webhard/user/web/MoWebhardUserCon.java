package egovframework.covision.webhard.user.web;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import egovframework.baseframework.util.json.JSONSerializer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.coviframework.util.ACLHelper;
import egovframework.coviframework.util.ComUtils;
import egovframework.coviframework.util.FileUtil;
import egovframework.baseframework.util.DateHelper;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.JsonUtil;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.covision.webhard.common.service.CommonSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;
import egovframework.covision.webhard.user.service.WebhardUserSvc;
import egovframework.coviframework.service.FileUtilService;

/**
 * @Class Name : MoWebhardUserCon.java
 * @Description : 웹하드 - 모바일 페이지
 * @Modification Information 
 * @ 2019.02.14 최초생성
 *
 * @author 코비젼 연구소
 * @since 2019. 02.14
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
@Controller
@RequestMapping("/mobile/webhard")
public class MoWebhardUserCon {
	private Logger LOGGER = LogManager.getLogger(MoWebhardUserCon.class);
	
	@Autowired
	private WebhardUserSvc webhardUserSvc;
	
	@Autowired
	private WebhardFolderSvc webhardFolderSvc;
	
	@Autowired
	private WebhardFileSvc webhardFileSvc;
	
	@Autowired
	private CommonSvc commonSvc;
	
	@Autowired
	private FileUtilService fileSvc;
	
	
	
	/**
	 * callFileUploadPopup : 웹하드 - 파일업로드 팝업 호출
	 * @param request
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "user/popup/callFileUpload.do", method = RequestMethod.GET)
	public ModelAndView callFileUploadPopup(HttpServletRequest request, Locale locale, Model model) {
		String returnURL = "webhard/user/popupUpload";
		ModelAndView mav = new ModelAndView(returnURL);
		
		return mav;
	}
	
	/**
	 * getFolderTreeList: 좌측 트리메뉴 데이터 조회
	 * @param request
	 * @param response
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "user/tree/select.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getFolderTreeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnList = new CoviMap();
		CoviList result = new CoviList();
		CoviList resultList = new CoviList();
		try{
			CoviMap params = new CoviMap();
			CoviMap domain = commonSvc.getOriginDomain();
			
			params.put("domainID", domain.getString("DomainID"));
			params.put("userCode", SessionHelper.getSession("USERID"));
			params.put("lang", SessionHelper.getSession("lang"));
			
			String type = StringUtil.replaceNull(request.getParameter("type"));		//radio, list등 Tree 메뉴 타입 코드
			String currentFolderID = StringUtil.replaceNull(request.getParameter("currentFolderID"));	//radio타입일 경우 현재 선택된 트리 FolderID parameter로 호출
			
			result = (CoviList) webhardUserSvc.selectFolderList(params).get("list");
			int index = 0;	//AXTree index array 체크용
		    for(Object jsonobject : result){
				CoviMap folderData = (CoviMap) jsonobject;
				
				// 트리를 그리기 위한 데이터
				folderData.put("no", folderData.get("uid"));
				folderData.put("boxUuid", folderData.get("boxUuid"));
				folderData.put("nodeName", folderData.get("name"));		//TODO: 다국어 처리
				folderData.put("nodeValue", folderData.get("uid"));
				folderData.put("pno", folderData.get("parentID"));
				folderData.put("folderNamePath", folderData.get("folderNamePath"));
				folderData.put("folderPath", folderData.get("folderPath"));				
				folderData.put("chk", "N");
				if("radio".equals(type) && folderData.get("uid").equals(currentFolderID)){
					folderData.put("rdo", "N");
				} else {
					folderData.put("rdo", "Y");
				}
				folderData.put("index", index);
				resultList.add(folderData);
				index++;
			}
			returnList.put("list", resultList);

			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", e.getMessage());	
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", e.getMessage());			
		}
		return returnList;
	}
	
	/**
	 * selectBoxList : 웹하드 - 폴더/파일 목록 조회
	 * @param params
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "user/box/select.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap selectBoxList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnData = new CoviMap();
		try{
			CoviMap params = new CoviMap();
			CoviMap params2 = new CoviMap();
			
			setRequestParam(request, params);
			
			params.put("userCode", SessionHelper.getSession("USERID"));
			params.put("ownerId", StringUtil.isNotNull(params.get("ownerId")) ? params.get("ownerId").toString() : SessionHelper.getSession("USERID"));
			
			CoviMap boxInfo = webhardFolderSvc.getBoxInfo(params);
			params.put("boxUuid", boxInfo.getString("boxUuid"));
			params2.put("boxUuid", boxInfo.getString("boxUuid"));
			
			int cnt = webhardUserSvc.selectBoxCount(params);
			params.addAll(ComUtils.setPagingData(params,cnt));
			
			params2.put("folderUuid", params.getString("folderID"));
			CoviMap resultList = webhardUserSvc.selectBoxList(params);

			// 공유받은/공유한 폴더의 경우 상위폴더 UUID값을 조회해 온다.
			String folderType = StringUtil.isNotNull(request.getParameter("folderType")) ? request.getParameter("folderType") : "";
			if (folderType.equals("Shared") || folderType.equals("Published")) {
				returnData.put("folderPath", folderType);
			}
			
			returnData.put("page", params);
			returnData.put("list", resultList.get("list"));
			returnData.put("info", webhardFolderSvc.getFolderInfo(params2));
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * createFolder : 폴더 생성
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "user/createFolder.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap createFolder(@RequestBody CoviMap params) throws Exception {
		CoviMap returnList = new CoviMap();
		
		// params 정보 중 parentUuid 가 최상위 폴더 일 때, null이나 ''(빈문자)가 아니게 되면 이후 함수에서 Loop가 계속 돌게 된다.
		// 22.06.30 이전까지 내드라이브 이외의 메뉴에서 새폴더가 생성 가능했음. 이 때 최상위 폴더가 빈값으로 넘어오지 않고 ROOT 값으로 넘어와 오류발생.
		// javascript에서 치환 못해줬을 경우 변경.
		if (params.get("parentUuid").equals("ROOT")) {
			params.put("parentUuid", "");
		}

		int result = 0;
		// 공유받은 폴더, 공유한 폴더, 최근문서함, 중요문서함, 휴지통에서 새폴더 생성하려 하면, ajax 실패로 유도.(jsp화면에서는 display: none 처리함)
		String strFolerType = params.get("folderType").toString();
		if (strFolerType.equals("Shared") || strFolerType.equals("Published") || strFolerType.equals("Recent")
				|| strFolerType.equals("Important") || strFolerType.equals("Trashbin") ) {
			returnList.put("status", Return.FAIL);
		} else {
			try {
				result = webhardFolderSvc.addFolder(params);
				if (result > 0) {
					returnList.put("status", Return.SUCCESS);
				} else {
					returnList.put("status", Return.FAIL);
				}
			} catch(NullPointerException e) {
				returnList.put("status", Return.FAIL);
			} catch(Exception e) {
				returnList.put("status", Return.FAIL);
			}
		}
		
		return returnList;
	}
	
	/**
	 * updateFolderName : 웹하드 - 폴더 명 수정
	 * @param params
	 * @return resultObj
	 * @throws Exception
	 */
	@RequestMapping(value = "user/updateFolderName.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap updateFolderName(@RequestBody CoviMap params) throws Exception {
		CoviMap returnList = new CoviMap();
		
		try {
			params.put("folderName", params.getString("newFolderName"));
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			int result = webhardFolderSvc.renameFolder(params);
			
			if (result > 0) {
				returnList.put("status", Return.SUCCESS);
			} else {
				returnList.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnList.put("status", Return.FAIL);
		} catch(Exception e) {
			returnList.put("status", Return.FAIL);
		}
		
		return returnList;
	}
	
	/**
	 * deleteTarget : 웹하드 - 폴더 및 파일 삭제
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "user/deleteTarget.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap deleteTarget(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnData = new CoviMap();
		
		try {
			String boxUuid = request.getParameter("boxUuid");
			String folderUuids = request.getParameter("folderUuids");
			String fileUuids = request.getParameter("fileUuids");
			String folderType = request.getParameter("folderType");
			
			CoviMap params = new CoviMap();
			params.put("boxUuid", boxUuid);
			params.put("folderUuids", folderUuids);
			params.put("fileUuids", fileUuids);
			params.put("folderType", folderType);
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			int fileCnt = 0, folderCnt = 0;
			
			if (StringUtil.isNotNull(fileUuids)) {
				fileCnt = webhardFileSvc.delete(params);
			}
			
			if (StringUtil.isNotNull(folderUuids)) {
				folderCnt = webhardFolderSvc.delete(params);
			}
			
			if (fileCnt == 0 && folderCnt == 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * restore: 삭제된 파일/폴더 복원
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/restore", method = RequestMethod.POST)
	public @ResponseBody CoviMap restore(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("fileUuids", request.getParameter("fileUuids"));
			params.put("folderUuids", request.getParameter("folderUuids"));
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			int fileCnt = 0, folderCnt = 0;
			
			if (StringUtil.isNotNull(request.getParameter("fileUuids"))) {
				fileCnt = webhardFileSvc.restore(params);
			}
			
			if (StringUtil.isNotNull(request.getParameter("folderUuids"))) {
				folderCnt = webhardFolderSvc.restore(params);
			}
			
			if (fileCnt == 0 && folderCnt == 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * deleteTarget : 웹하드 - 폴더 및 파일 복사
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "user/copyTarget.do", method = RequestMethod.POST)
	@ResponseBody
	public CoviMap copyTarget(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap sources = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("sources"),"").replaceAll("&quot;", "\"")));
			CoviMap targets = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("targets"),"").replaceAll("&quot;", "\"")));
			
			String fileUuids = sources.getString("fileUuids");
			String folderUuids = sources.getString("folderUuids");
			String boxUuid = sources.getString("boxUuid");
			String folderUuid = sources.getString("folderUuid");
			String targetBoxUuid = targets.getString("boxUuid");
			String targetFolderUuid = targets.getString("folderUuid");
			int fileCnt = 0, folderCnt = 0;
			
			CoviMap params = new CoviMap();
			
			params.put("fileUuids", fileUuids);
			params.put("folderUuids", folderUuids);
			params.put("boxUuid", boxUuid);
			params.put("folderUuid", folderUuid);
			params.put("targetBoxUuid", targetBoxUuid);
			params.put("targetFolderUuid", targetFolderUuid);
			
			if (!webhardUserSvc.checkUsageWebHard(params, folderUuids, fileUuids)) {
				returnData.put("status", "BOX_FULL");
				return returnData;
			}
			
			if (StringUtil.isNotNull(fileUuids)) {
				fileCnt = webhardFileSvc.copy(params);
			}
			
			if (StringUtil.isNotNull(folderUuids)) {
				folderCnt = webhardFolderSvc.copy(params);
			}
			
			if (fileCnt == 0 && folderCnt == 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * deleteTarget : 웹하드 - 폴더 및 파일 이동
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "user/moveTarget.do", method = RequestMethod.POST)
	@ResponseBody
	public CoviMap moveTarget(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap sources = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("sources"),"").replaceAll("&quot;", "\"")));
			CoviMap targets = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("targets"),"").replaceAll("&quot;", "\"")));
			
			String fileUuids = sources.getString("fileUuids");
			String folderUuids = sources.getString("folderUuids");
			String boxUuid = sources.getString("boxUuid");
			String folderUuid = sources.getString("folderUuid");
			String targetBoxUuid = targets.getString("boxUuid");
			String targetFolderUuid = targets.getString("folderUuid");
			int fileCnt = 0, folderCnt = 0;
			
			CoviMap params = new CoviMap();
			
			params.put("fileUuids", fileUuids);
			params.put("folderUuids", folderUuids);
			params.put("boxUuid", boxUuid);
			params.put("folderUuid", folderUuid);
			params.put("targetBoxUuid", targetBoxUuid);
			params.put("targetFolderUuid", targetFolderUuid);
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			if (StringUtil.isNotNull(fileUuids)) {
				fileCnt = webhardFileSvc.move(params);
			}
			
			if (StringUtil.isNotNull(folderUuids)) {
				folderCnt = webhardFolderSvc.move(params);
			}
			
			if (fileCnt == 0 && folderCnt == 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * uploadFile - 웹하드 - 업로드
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "user/uploadFile.do", method=RequestMethod.POST)
	@ResponseBody
	public CoviMap uploadFile(MultipartHttpServletRequest request) throws Exception {
		CoviMap returnData = new CoviMap();
		
		try {
			List<MultipartFile> files = request.getFiles("files");
			String boxUuid = request.getParameter("boxUuid");
			String folderUuid = request.getParameter("folderUuid");
			
			CoviMap params = new CoviMap();
			params.put("boxUuid", boxUuid);
			params.put("folderUuid", folderUuid);
			
			CoviList fileInfos = new CoviList();
			CoviMap boxInfo = commonSvc.getBaseInfo();
			
			for (int i = 0; i < files.size(); i++) {
				CoviMap fileObj = new CoviMap();
				String genID = commonSvc.generateUuid();
				
				fileObj.put("UUID", genID);
				fileObj.put("boxUUID", boxUuid);
				fileObj.put("folderUUID", folderUuid);
				fileObj.put("filePath", "/" + genID);
				
				fileInfos.add(fileObj);
			}
			
			if(!webhardUserSvc.checkUsageWebHard(params, files)){
				returnData.put("status", "BOX_FULL");
				return returnData;
			}
			
			if (!webhardUserSvc.checkUploadSize(files)) {
				returnData.put("status", "UPLOAD_MAX");
				return returnData;
			}
			
			int result = webhardFileSvc.saveFileInfo(boxInfo, files, fileInfos);
			
			if (result <= 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch(NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch(Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * downloadFile : 웹하드 - 파일 다운로드
	 * @param request
	 * @param response
	 * @return mav
	 * @throws Exception
	 */
	@RequestMapping(value = "user/downloadFile.do", method={RequestMethod.GET,RequestMethod.POST})	
	public ModelAndView downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("mode", "link");
		mav.addObject("fileUuids", request.getParameter("fileUuid"));
		mav.addObject("folderUuids", "");
		
		mav.setViewName("fileDownloadView");
		
		return mav;
	}
	
	/**
	 * updateBookmark: 폴더/파일 중요 설정
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "user/bookmark.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap updateBookmark(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("UUID", request.getParameter("uid"));
			params.put("BOOKMARK", request.getParameter("flagValue"));
			
			if(StringUtil.replaceNull(request.getParameter("fileType"),"").equals("folder")){
				returnData.put("cnt", webhardUserSvc.updateFolderBookmark(params));
			}else {
				returnData.put("cnt", webhardUserSvc.updateFileBookmark(params));
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * setRequestParam: requestParam 매핑 함수
	 * @param request
	 * @param pMap
	 */
	public void setRequestParam(HttpServletRequest request, CoviMap pMap){
		try{
		    Enumeration names = request.getParameterNames();
		    
		    while (names.hasMoreElements()) {
		    	String name = (String) names.nextElement();
		    	//체크박스 항목의 경우 jsp에서 serializeObject 사용시 체크안된 항목은 Null값으로 전달되므로 이름으로 별도처리
		    	//추후 중복되는 이름의 변수를 사용할 경우  변경 필요
		    	if("sortBy".equals(name)) {
		    		//sortBy 항목 분기
		    		pMap.put("sortBy", request.getParameter(name));
		    		//pMap.put("sortColumn", StringUtil.replaceNull(request.getParameter(name),"").split(" ")[0]);
		    		//pMap.put("sortDirection", StringUtil.replaceNull(request.getParameter(name),"").split(" ")[1]);
		    		// 조건절이 삭제되어 split()에서 오류 발생. 수정.
		    		pMap.put("sortColumn", (request.getParameter(name) != null && request.getParameter(name).isEmpty())?"":request.getParameter(name).split(" ")[0]);
		    		pMap.put("sortDirection", (request.getParameter(name) != null && request.getParameter(name).isEmpty())?"":request.getParameter(name).split(" ")[1]);
		    	} else {
		    		//그외의 parameter는 key:value 형태로 set
		    		pMap.put(name, request.getParameter(name));
		    	}
		    }
		}catch(NullPointerException e){
			LOGGER.error(e.getLocalizedMessage(), e);
		}catch(Exception e){
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * callFolderNamePopup : 웹하드 - 폴더 생성/이름변경 팝업
	 * @param request
	 * @param locale
	 * @param model
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callFolderNamePopup.do", method = RequestMethod.GET)
	public ModelAndView callFolderNamePopup(HttpServletRequest request, Locale locale, Model model) {
		String returnURL = "webhard/user/popupFolderName";
		ModelAndView mav = new ModelAndView(returnURL);
		return mav;
	}
	
	/**
	 * callFolderTreePopup : 웹하드 - 폴더 이동/복사용 Radio버튼이 표시되는 폴더 트리 팝업
	 * @param request
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "user/popup/callFolderTreePopup.do", method = RequestMethod.GET)
	public ModelAndView callFolderTreePopup(HttpServletRequest request, Locale locale, Model model) {
		String returnURL = "webhard/user/popupRadioFolderTree";
		ModelAndView mav = new ModelAndView(returnURL);
		return mav;
	}
	
	/**
	 * callSharePopup : 웹하드 - 공유 멤버 초대하기 팝업
	 * @param request
	 * @param locale
	 * @param model
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callSharePopup.do", method = RequestMethod.GET)
	public ModelAndView callSharePopup(HttpServletRequest request, Locale locale, Model model) {
		String returnURL = "webhard/user/popupShare";
		ModelAndView mav = new ModelAndView(returnURL);
		return mav;
	}
	
	/**
	 * callWebhardAttachPopup  : 웹하드 - 웹하드 파일 첨부용 팝업
	 * @param request
	 * @param locale
	 * @param model
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callWebhardAttachPopup.do", method = RequestMethod.GET)
	public ModelAndView callWebhardAttachPopup() throws Exception {
		JsonUtil jUtil = new JsonUtil();
		CoviList queriedMenu = new CoviList();
		CoviMap domain = commonSvc.getOriginDomain();
		
		String domainId = domain.getString("DomainID");
		String menuStr = RedisDataUtil.getMenu(SessionHelper.getSession("USERID"), domainId);
		String lang = SessionHelper.getSession("lang");
		
		
		if (StringUtils.isNoneBlank(menuStr)) {
			CoviList menuArray = jUtil.jsonGetObject(menuStr);
			queriedMenu = ACLHelper.parseMenuByBiz(domainId, "N", "Webhard", "Left", menuArray, lang);
		}
		
		ModelAndView mav = new ModelAndView( "webhard/user/popupWebhardAttach");
		mav.addObject("leftMenuData", queriedMenu);
		return mav;
	}
	
	/**
	 * selectSharedMember: 공유대상자 조회
	 * @param request
	 * @param response
	 * @return returnData
	 * @throws Exception
	 */
	@RequestMapping(value = "user/shared/selectMember.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap selectSharedMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			params.put("sharedType", request.getParameter("sharedType"));
			params.put("sharedID", request.getParameter("sharedID"));
			
			CoviMap resultObj = webhardUserSvc.selectSharedMember(params);
			
			returnData.put("list", resultObj.get("list"));
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		
		return returnData;
	}
	
	/**
	 * shareTarget: 파일/폴더 공유
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/shared/shareTarget.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap shareTarget(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			int errorCnt = 0;
			
			String targetType = StringUtil.replaceNull(request.getParameter("targetType"),"");
			String targetUuid = request.getParameter("targetUuid");
			String sharedToList[] = StringUtil.replaceNull(request.getParameter("sharedTo"),"").split(";");
			
			CoviMap targetInfo = new CoviMap();
			List<CoviMap> targetFolderList = null;
			
			if (targetType.toUpperCase().equals("FOLDER")) {
				targetInfo.put("folderUuid", targetUuid);
				targetInfo = webhardFolderSvc.getFolderInfo(targetInfo);
				targetFolderList = webhardFolderSvc.getFolderListInPath(targetInfo);
			} else {
				targetInfo.put("fileUuid", targetUuid);
				targetInfo = webhardFileSvc.getFileInfo(targetInfo);
			}
			
			for (String sharedTo : sharedToList) {
				CoviMap sharedInfo = new CoviMap();
				String sData[] = sharedTo.split("\\|");
				
				sharedInfo.put("sharedOwnerId", sData[0]);
				sharedInfo.put("sharedGrantType", sData[1]);
				sharedInfo.put("sharedStatus", "ON");

				if (targetFolderList != null && !targetFolderList.isEmpty()) {
					for (CoviMap targetFolder : targetFolderList) {
						if (webhardFolderSvc.shareFolder(targetFolder, sharedInfo) < 1) { // 해당 폴더 및 하위 파일 공유
							errorCnt++;
						}
					}
				} else {
					if (webhardFileSvc.shareFile(targetInfo, sharedInfo) < 1) { // 파일 공유
						errorCnt++;
					}
				}
			}
			
			if (errorCnt == 0) {
				returnData.put("status", Return.SUCCESS);
			} else {
				returnData.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch(Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * unshareTarget: 파일/폴더 공유해제
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/shared/unshareTarget.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap unshareTarget(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			int errorCnt = 0;
			
			String targetType = StringUtil.replaceNull(request.getParameter("targetType"),"");
			String targetUuid = request.getParameter("targetUuid");
			String unsharedToList[] = StringUtil.replaceNull(request.getParameter("unsharedTo"),"").split(";");
			
			CoviMap targetInfo = new CoviMap();
			List<CoviMap> targetFolderList = null;
			
			if (targetType.toUpperCase().equals("FOLDER")) {
				targetInfo.put("folderUuid", targetUuid);	
				targetInfo = webhardFolderSvc.getFolderInfo(targetInfo);
				targetFolderList = webhardFolderSvc.getFolderListInPath(targetInfo);
			} else {
				targetInfo.put("fileUuid", targetUuid);
				targetInfo = webhardFileSvc.getFileInfo(targetInfo);
			}
			
			CoviMap unsharedInfo = new CoviMap();
			
			for (String unsharedTo : unsharedToList) {
				String sData[] = unsharedTo.split("\\|");
				
				unsharedInfo.put("sharedOwnerId", sData[0]);
				unsharedInfo.put("sharedGrantType", sData[1]);
				unsharedInfo.put("sharedStatus", "ON");

				if (targetFolderList != null && !targetFolderList.isEmpty()) {
					for (CoviMap targetFolder : targetFolderList) {
						if (webhardFolderSvc.unshareFolder(targetFolder, unsharedInfo) < 1) { // 해당 폴더 및 하위 파일 공유 해제
							errorCnt++;
						}
					}
				} else {
					if (webhardFileSvc.unshareFile(targetInfo, unsharedInfo) < 1) { // 파일 공유 해제
						errorCnt++;
					}
				}
			}
			
			if (errorCnt == 0) {
				returnData.put("status", Return.SUCCESS);
			} else {
				returnData.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnData.put("status", Return.FAIL);
		} catch(Exception e) {
			returnData.put("status", Return.FAIL);
		}
		
		return returnData;
	}
	
	/**
	 * uploadToFront: 공통 파일 첨부 - 웹하드 파일 첨부를 위한 FrontStorage 복사 작업
	 * @param CoviMap params
	 * @return returnData
	 */
	@RequestMapping(value = "attach/uploadToFront.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap uploadToFront(@RequestBody List<String> pfileUuids) {
		CoviList returnList = new CoviList();
		CoviMap returnData = new CoviMap();
		
		try{
			String[] fileUuids = pfileUuids.toArray(new String[pfileUuids.size()]);
			CoviList files = new CoviList();
			String companyCode = SessionHelper.getSession("DN_Code");
			String fullPath = FileUtil.getFrontPath() + File.separator + companyCode;

			List<MultipartFile> mf = new ArrayList<>();
			List<CoviMap> fileInfoList = webhardFileSvc.getFileInfoList(fileUuids);
			
			for (CoviMap fileInfo : fileInfoList) {
				String fileName = fileInfo.getString("fileName");
				String fileFullPath = fileInfo.getString("boxPath") + fileInfo.getString("filePath");
				fileFullPath = fileFullPath.replace("/", File.separator);
				
				mf.add(FileUtil.makeMockMultipartFile(fileFullPath, fileName));
				
			}
			
			if(FileUtil.isEnableExtention(mf)){
				if (!mf.isEmpty()) {
					String yyyyMMddhhmmssSSS = DateHelper.getCurrentDay(fileSvc.getCURRENT_TIME_FORMAT());
					
					for (int i = 0; i < mf.size(); i++) {
						CoviMap frontObj = new CoviMap();
						// 파일 중복명 처리
						// 본래 파일명
						String originalfileName = mf.get(i).getOriginalFilename();
						//String genId = yyyyMMddhhmmssSSS + "_" + FilenameUtils.getBaseName(originalfileName);
						String genId = UUID.randomUUID().toString().replace("-", "");
						// 저장되는 파일 이름
						String ext = FilenameUtils.getExtension(originalfileName);
						String saveFileName = genId + "." + ext;
						String size = String.valueOf(mf.get(i).getSize());
						
						String fullFileNamePath = fullPath + fileSvc.getFILE_SEPARATOR() + saveFileName; // 저장 될 파일 경로
						fullFileNamePath = new String(fullFileNamePath.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
						File originFile = new File(FileUtil.checkTraversalCharacter(fullFileNamePath));
						
//							mockedFileList.get(i).transferTo(originFile); // 파일 저장
						fileSvc.transferTo(mf.get(i), originFile, ext);
						
						if(fileSvc.getIS_USE_DECODE_DRM().equalsIgnoreCase("Y")){
							originFile = fileSvc.callDRMDecoding(originFile, fullFileNamePath);
						}
						
						if(ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("png")||ext.equalsIgnoreCase("gif")||ext.equalsIgnoreCase("bmp")){
							fileSvc.makeThumb(fullPath + fileSvc.getFILE_SEPARATOR() + genId + "_thumb.jpg", originFile); //썸네일 저장
				        }
						frontObj.put("FileType", "webhard");
						frontObj.put("FileName", originalfileName);
						frontObj.put("SavedName", saveFileName);
						frontObj.put("Size", size);
						
						returnList.add(frontObj);
					}
				}
				
			}else {
				returnData.put("list", "0");
				returnData.put("status", Return.FAIL);
				returnData.put("message", DicHelper.getDic("msg_ExistLimitedExtensionFile"));
			}
			
			returnData.put("files", returnList);
			returnData.put("status", Return.SUCCESS);
		} catch (IOException e) {
			returnData.put("status", Return.FAIL);
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		return returnData;
	}
	
	@RequestMapping(value = "user/getUsageWebHard.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getUsageWebHard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap params = new CoviMap();
		
		if (StringUtil.isNotNull(request.getParameter("boxUuid"))) {
			params.put("boxUuid", request.getParameter("boxUuid"));
		} else {
			params.put("domainId", SessionHelper.getSession("DN_ID"));
			params.put("domainCode", SessionHelper.getSession("DN_Code"));
			params.put("ownerId", SessionHelper.getSession("USERID"));
			params.put("ownerType", "U");
		}
		
		if (webhardFolderSvc.getBoxInfo(params) == null) {
			webhardUserSvc.createBox(params);
		}
		
		return webhardUserSvc.getUsageWebHard(params);
	}	
}
