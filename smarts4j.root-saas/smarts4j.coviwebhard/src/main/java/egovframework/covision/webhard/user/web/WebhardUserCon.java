package egovframework.covision.webhard.user.web;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import egovframework.coviframework.util.s3.AwsS3;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.sec.AES;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.AuthorityService;
import egovframework.coviframework.service.FileUtilService;
import egovframework.coviframework.util.ComUtils;
import egovframework.coviframework.util.FileUtil;
import egovframework.covision.webhard.common.service.CommonSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;
import egovframework.covision.webhard.user.service.WebhardUserSvc;


import egovframework.baseframework.util.json.JSONSerializer;

/**
 * @Class Name : WebhardUserCon.java
 * @Description : 웹하드 - 사용자 페이지
 * @Modification Information
 * @ 2019.02.14 최초생성
 *
 * @author 코비젼 연구소
 * @since 2019. 02.14
 * @version 1.0
 * Copyright(c) by Covision All right reserved.
 */
@Controller
public class WebhardUserCon {
	org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(WebhardUserCon.class.getName());
	
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

	@Autowired
	private AuthorityService authorityService;
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");

	AwsS3 awsS3 = AwsS3.getInstance();
	/**
	 * callFolderNamePopup: 폴더 생성/이름변경 팝업
	 * @param paramMap Map<String, String>
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callFolderNamePopup.do", method = RequestMethod.GET)
	public ModelAndView callFolderNamePopup(@RequestParam Map<String, String> paramMap){
		ModelAndView mv = new ModelAndView("webhard/user/popupFolderName");
		mv.addAllObjects(paramMap);
		return mv;
	}
	
	/**
	 * callFolderTreePopup: 폴더 이동/복사용 폴더 트리 팝업
	 * @param paramMap Map<String, String>
	 * @param request HTTP Request Object
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callFolderTreePopup.do", method = RequestMethod.GET)
	public ModelAndView callFolderTreePopup(@RequestParam Map<String, String> paramMap, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("webhard/user/popupRadioFolderTree");
		mv.addAllObjects(paramMap);
		return mv;
	}
	
	/**
	 * callSharePopup: 공유 멤버 초대하기 팝업
	 * @param paramMap Map<String, String>
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callSharePopup.do", method = RequestMethod.GET)
	public ModelAndView callSharePopup(@RequestParam Map<String, String> paramMap) {
		ModelAndView mv = new ModelAndView("webhard/user/popupShare");
		mv.addAllObjects(paramMap);
		return mv;
	}
	
	/**
	 * callFileUploadPopup: 파일/폴더 업로드 팝업
	 * @param paramMap Map<String, String>
	 * @return 파일 업로드 뷰
	 */
	@RequestMapping(value = "user/popup/callFileUpload.do", method = RequestMethod.GET)
	public ModelAndView callFileUploadPopup(@RequestParam Map<String, String> paramMap) {
		ModelAndView mv = new ModelAndView("webhard/user/popupUpload");
		mv.addAllObjects(paramMap);
		return mv;
	}
	
	/**
	 * callWebhardAttachPopup: 웹하드 파일 첨부용 팝업
	 * @param paramMap Map<String, String>
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callWebhardAttachPopup.do", method = RequestMethod.GET)
	public ModelAndView callWebhardAttachPopup(@RequestParam Map<String, String> paramMap) {
		ModelAndView mav = new ModelAndView("webhard/user/popupWebhardAttach");
		mav.addAllObjects(paramMap);
		return mav;
	}
	
	/**
	 * selectSharedMember: 공유대상자 조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return returnData
	 */
	@RequestMapping(value = "user/shared/selectMember.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap selectSharedMember(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			params.put("sharedType", StringUtil.replaceNull(request.getParameter("sharedType"),"").toUpperCase());
			params.put("sharedID", request.getParameter("sharedID"));
			
			CoviMap resultObj = webhardUserSvc.selectSharedMember(params);
			
			returnData.put("list", resultObj.get("list"));
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * selectPrevFolder: 이전 폴더 정보 조회 
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return returnData
	 */
	@RequestMapping(value = "user/prevFolder.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap selectPrevFolder(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			params.put("folderID", request.getParameter("folderID"));
			params.put("folderType", request.getParameter("folderType"));
			params.put("userCode", SessionHelper.getSession("USERID"));
			
			CoviMap result = webhardUserSvc.selectPrevFolder(params);
			
			returnData.put("prev", result);
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * getUsageWebHard: 웹하드 박스 사용량 조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 * @throws Exception 
	 */
	@RequestMapping(value = "user/getUsageWebHard.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getUsageWebHard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap params = new CoviMap();
		
		if (StringUtil.isNotNull(request.getParameter("boxUuid"))) {
			params.put("boxUuid", request.getParameter("boxUuid"));
		} else {
			params.put("domainId", SessionHelper.getSession("DN_ID"));
			params.put("ownerId", SessionHelper.getSession("USERID"));
			params.put("ownerType", "U");
		}
		
		return webhardUserSvc.getUsageWebHard(params);
	}
	
	/**
	 * callLinkPopup: 웹하드 - 링크 공유 팝업
	 * @param request HTTP Request Object
	 * @param locale 다국어 정보
	 * @param model 데이터 모델
	 * @return mav
	 */
	@RequestMapping(value = "user/popup/callLinkPopup.do", method = RequestMethod.GET)
	public ModelAndView callLinkPopup(HttpServletRequest request, Locale locale, Model model) {
		ModelAndView mav =  new ModelAndView("webhard/user/popupLink");
		
		try {
			final String aeskey = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.key"));
			AES aes = new AES(aeskey, "N");
			
			StringUtil func = new StringUtil();
			CoviMap params = new CoviMap();
			params.put("targetType", StringUtil.replaceNull(request.getParameter("targetType"),"").toUpperCase());
			params.put("targetUuid", request.getParameter("targetUuid"));
			
			CoviMap linkData = webhardUserSvc.selectLinkData(params);
			mav.addObject("targetType", StringUtil.replaceNull(request.getParameter("targetType"),"").toUpperCase());
			mav.addObject("targetUuid", request.getParameter("targetUuid"));
			mav.addObject("isNew", linkData.getString("isNew"));
			mav.addObject("link", linkData.getString("LINK"));
			mav.addObject("auth", linkData.getString("AUTH"));
			mav.addObject("startDate", func.f_NullCheck(linkData.getString("STARTDATE")));
			mav.addObject("endDate", func.f_NullCheck(linkData.getString("ENDDATE")));
			mav.addObject("password", aes.decrypt(func.f_NullCheck(linkData.getString("PASSWORD"))));
		} catch(NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		
		return mav;
	}
	
	/**
	 * saveLink: 공유 링크 정보 저장
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return returnData
	 */
	@RequestMapping(value = "link/saveLink.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap saveLink(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			
			params.put("targetType", StringUtil.replaceNull(request.getParameter("targetType"),"").toUpperCase());
			params.put("targetUuid", request.getParameter("targetUuid"));
			params.put("isNew", Objects.toString(request.getParameter("isNew"), "Y"));
			params.put("link", request.getParameter("link"));
			params.put("auth", StringUtil.replaceNull(request.getParameter("auth"),"").toUpperCase());
			
			if(!StringUtil.replaceNull(request.getParameter("password"),"").equals("")){
				final String aeskey = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.key"));
				AES aes = new AES(aeskey, "N");
				params.put("password", aes.encrypt(request.getParameter("password")));
			}
			
			if(!StringUtil.replaceNull(request.getParameter("startDate"),"").equals("")){
				params.put("startDate", request.getParameter("startDate"));
			}
			
			if(!StringUtil.replaceNull(request.getParameter("endDate"),"").equals("")){
				params.put("endDate", request.getParameter("endDate"));
			}
			
			webhardUserSvc.saveLink(params);
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * link: 링크 정보 조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return ModelAndView
	 */
	@RequestMapping(value = "link.do", method = RequestMethod.GET)
	public ModelAndView link(HttpServletRequest request, HttpServletResponse response) {
		String returnUrl = "webhard/user/LinkFileDownload";
		ModelAndView mav = new ModelAndView();
		
		try {
			CoviMap params = new CoviMap();
			params.put("link", request.getParameter("id"));
			
			CoviMap linkInfo = webhardUserSvc.selectLinkInfo(params);
			
			if (!linkInfo.isEmpty()) {
				if(linkInfo.getString("TARGET_TYPE").equalsIgnoreCase("FOLDER")){
					returnUrl = "webhard/user/LinkBoxList";
				}else{
					returnUrl = "webhard/user/LinkFileDownload";
				}
				
				mav.addObject("uuid", linkInfo.getString("TARGET_UUID"));
				mav.addObject("boxUuid", linkInfo.getString("BOX_UUID"));
				mav.addObject("link", linkInfo.getString("LINK"));
				mav.addObject("auth", linkInfo.getString("AUTH"));
				mav.addObject("isValid", linkInfo.getString("ISVALID")); //유효기간 만료 여부
				mav.addObject("isExist", "Y"); // 링크 존재여부
			} else {
				mav.addObject("isExist", "N"); // 링크 존재여부
			}
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		
		mav.setViewName(returnUrl);
		
		return mav;
	}
	
	/**
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return returnData
	 */
	@RequestMapping(value = "link/checkPassword.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap checkPassword(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			params.put("link", request.getParameter("link"));
			
			if(request.getParameter("password") != null){
				final String aeskey = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.key"));
				AES aes = new AES(aeskey, "N");
				params.put("password", aes.encrypt(request.getParameter("password"))); 
			}
			
			returnData.put("cnt", webhardUserSvc.checkPassword(params));
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * getFileInfo: 파일 정보  조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return returnData
	 */
	@RequestMapping(value = "link/getFileInfo.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getFileInfo(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			params.put("fileUuid", request.getParameter("uuid"));
			
			CoviMap fileInfo = webhardFileSvc.getFileInfo(params);
			fileInfo.addAll(fileInfo); // null 방지
			
			// timezone 적용
			fileInfo.put("createdDate", ComUtils.TransLocalTime(fileInfo.getString("createdDate")));
			
			returnData.put("file", fileInfo);
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * getFolderInfo: 폴더 정보  조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return returnData
	 */
	@RequestMapping(value = "link/getFolderInfo.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getFolderInfo(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try{
			CoviMap params = new CoviMap();
			params.put("folderUuid", request.getParameter("uuid"));
			
			CoviMap folderInfo = webhardFolderSvc.getFolderInfo(params);
			folderInfo.addAll(folderInfo); //null 방지
			
			returnData.put("folder", folderInfo);
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * uploadFile: 파일/폴더 업로드
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "common/uploadFile", method = RequestMethod.POST)
	@Transactional
	public @ResponseBody CoviMap uploadFile(MultipartHttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			List<MultipartFile> files = request.getFiles("files");
			CoviMap fInfos = CoviMap.fromObject(JSONSerializer.toJSON(request.getParameter("fileMap")));
			CoviList fileInfos = fInfos.getJSONArray("fileInfos");
			CoviList directories = fInfos.getJSONArray("directories");
			CoviMap boxInfo = commonSvc.getBaseInfo();
			CoviMap params = new CoviMap();
			
			if (fileInfos == null || fileInfos.size() == 0) {
				params.put("boxUuid", directories.getJSONObject(0).getString("boxUUID"));
			} else {
				params.put("boxUuid", fileInfos.getJSONObject(0).getString("boxUUID"));
			}
			
			// 박스 권한 체크
			if (!webhardUserSvc.checkBoxOwner(params)) {
				returnData.put("status", "NOT_OWNER");
				return returnData;
			}
			
			// 박스 용량 체크
			if (files != null && files.size() != 0 && !webhardUserSvc.checkUsageWebHard(params, files)) {
				returnData.put("status", "BOX_FULL");
				return returnData;
			}
			
			// 업로드 용량 체크
			if (files != null && files.size() != 0 && !webhardUserSvc.checkUploadSize(files)) {
				returnData.put("status", "UPLOAD_MAX");
				return returnData;
			}
			
			if (directories != null && !directories.isEmpty()) {
				int result = webhardFolderSvc.saveFolderInfo(boxInfo, directories);
				
				if (result <= 0) {
					returnData.put("status", Return.FAIL);
					return returnData;
				}
			}
			
			if (fileInfos != null && !fileInfos.isEmpty()
				&& files != null && !files.isEmpty()) {
				int result = webhardFileSvc.saveFileInfo(boxInfo, files, fileInfos);
				
				if (result <= 0) {
					returnData.put("status", Return.FAIL);
					return returnData;
				}
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * fileDown: 파일/폴더 다운로드
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return ModelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "common/fileDown.do", method = RequestMethod.POST)
	public ModelAndView fileDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("mode", request.getParameter("mode")); // user - 사용자, link - 링크
		mav.addObject("fileUuids", request.getParameter("fileUuids"));
		mav.addObject("folderUuids", request.getParameter("folderUuids"));
		
		mav.setViewName("fileDownloadView");
		
		return mav;
	}
	
	/**
	 * addFolder: 폴더 추가
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/addFolder", method = RequestMethod.POST)
	public @ResponseBody CoviMap addFolder(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("boxUuid", request.getParameter("boxUuid"));
			params.put("parentUuid", request.getParameter("parentUuid"));
			params.put("folderName", request.getParameter("folderName"));
			
			int result = webhardFolderSvc.addFolder(params);
			
			if (result <= 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * renameFolder: 폴더명 수정
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/renameFolder", method = RequestMethod.POST)
	public @ResponseBody CoviMap renameFolder(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("boxUuid", request.getParameter("boxUuid"));
			params.put("folderUuid", request.getParameter("folderUuid"));
			params.put("folderName", request.getParameter("folderName"));
			
			int result = webhardFolderSvc.renameFolder(params);
			
			if (result <= 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * renameFile: 파일명 수정
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/renameFile", method = RequestMethod.POST)
	public @ResponseBody CoviMap renameFile(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("boxUuid", request.getParameter("boxUuid"));
			params.put("fileUuid", request.getParameter("fileUuid"));
			params.put("fileName", request.getParameter("fileName"));
			
			int result = webhardFileSvc.renameFile(params);
			
			if (result <= 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * delete: 파일/폴더 삭제
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/delete", method = RequestMethod.POST)
	public @ResponseBody CoviMap delete(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("boxUuid", request.getParameter("boxUuid"));
			params.put("fileUuids", request.getParameter("fileUuids"));
			params.put("folderUuids", request.getParameter("folderUuids"));
			params.put("folderType", request.getParameter("folderType"));
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			int fileCnt = 0, folderCnt = 0;
			
			if (StringUtil.isNotNull(request.getParameter("fileUuids"))) {
				fileCnt = webhardFileSvc.delete(params);
			}
			
			if (StringUtil.isNotNull(request.getParameter("folderUuids"))) {
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
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
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
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * copy: 파일/폴더 복사
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/copy", method = RequestMethod.POST)
	public @ResponseBody CoviMap copy(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap sources = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("sources"),"").replaceAll("&quot;", "\"")));
			CoviMap targets = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("targets"),"").replaceAll("&quot;", "\"")));
			
			int fileCnt = 0, folderCnt = 0;
			
			CoviMap params = new CoviMap();
			
			params.put("fileUuids", sources.getString("fileUuids"));
			params.put("folderUuids", sources.getString("folderUuids"));
			params.put("boxUuid", sources.getString("boxUuid"));
			params.put("folderUuid", sources.getString("folderUuid"));
			params.put("targetBoxUuid", targets.getString("boxUuid"));
			params.put("targetFolderUuid", targets.getString("folderUuid"));
			
			if (!webhardUserSvc.checkUsageWebHard(params, sources.getString("folderUuids"), sources.getString("fileUuids"))) {
				returnData.put("status", "BOX_FULL");
				return returnData;
			}
			
			if (StringUtil.isNotNull(sources.getString("fileUuids"))) {
				fileCnt = webhardFileSvc.copy(params);
			}
			
			if (StringUtil.isNotNull(sources.getString("folderUuids"))) {
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
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * move: 파일/폴더 이동
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/move", method = RequestMethod.POST)
	public @ResponseBody CoviMap move(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			CoviMap sources = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("sources"),"").replaceAll("&quot;", "\"")));
			CoviMap targets = CoviMap.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("targets"),"").replaceAll("&quot;", "\"")));
			
			int fileCnt = 0, folderCnt = 0;
			
			CoviMap params = new CoviMap();
			
			params.put("fileUuids", sources.getString("fileUuids"));
			params.put("folderUuids", sources.getString("folderUuids"));
			params.put("boxUuid", sources.getString("boxUuid"));
			params.put("folderUuid", sources.getString("folderUuid"));
			params.put("targetBoxUuid", targets.getString("boxUuid"));
			params.put("targetFolderUuid", targets.getString("folderUuid"));
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			if (StringUtil.isNotNull(sources.getString("fileUuids"))) {
				fileCnt = webhardFileSvc.move(params);
			}
			
			if (StringUtil.isNotNull(sources.getString("folderUuids"))) {
				folderCnt = webhardFolderSvc.move(params);
			}
			
			if (fileCnt == 0 && folderCnt == 0) {
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * share: 파일/폴더 공유
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/share.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap share(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			int errorCnt = 0;
			
			CoviMap targetInfo = new CoviMap();
			List<CoviMap> targetFolderList = null;
			
			if (StringUtil.replaceNull(request.getParameter("targetType"),"").toUpperCase().equals("FOLDER")) {
				targetInfo.put("folderUuid", request.getParameter("targetUuid"));
				targetInfo = webhardFolderSvc.getFolderInfo(targetInfo);
				targetFolderList = webhardFolderSvc.getFolderListInPath(targetInfo);
			} else {
				targetInfo.put("fileUuid", request.getParameter("targetUuid"));
				targetInfo = webhardFileSvc.getFileInfo(targetInfo);
			}
			
			for (String sharedTo : request.getParameter("sharedTo").split(";")) {
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
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * unshare: 파일/폴더 공유해제
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/unshare.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap unshare(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		
		try {
			int errorCnt = 0;
			
			CoviMap targetInfo = new CoviMap();
			List<CoviMap> targetFolderList = null;
			
			if (StringUtil.replaceNull(request.getParameter("targetType"),"").toUpperCase().equals("FOLDER")) {
				targetInfo.put("folderUuid", request.getParameter("targetUuid"));
				targetInfo = webhardFolderSvc.getFolderInfo(targetInfo);
				targetFolderList = webhardFolderSvc.getFolderListInPath(targetInfo);
			} else {
				targetInfo.put("fileUuid", request.getParameter("targetUuid"));
				targetInfo = webhardFileSvc.getFileInfo(targetInfo);
			}
			
			CoviMap unsharedInfo = new CoviMap();
			
			for (String unsharedTo : request.getParameter("unsharedTo").split(";")) {
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
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * getUserDriveList: 좌측 사용자 권한 드라이브 리스트 조회
	 * @return 드라이브 리스트
	 */
	@RequestMapping(value = "user/tree/getUserDriveList.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getUserDriveList() {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			CoviMap domain = commonSvc.getOriginDomain();
			
			params.put("domainCode", domain.getString("DomainCode"));
			params.put("userCode", SessionHelper.getSession("USERID"));
			params.put("deptCode", SessionHelper.getSession("DEPTID"));
			params.put("lang", SessionHelper.getSession("lang"));
			
			returnList.put("list", webhardUserSvc.getUserDriveList(params));
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}
	
	/**
	 * getSelectDriveTreeList: 좌측 트리메뉴 데이터 조회
	 * @param paramMap Map<String, String>
	 * @return 좌측 트리메뉴 데이터
	 */
	@RequestMapping(value = "user/tree/getSelectDriveTreeList.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getSelectDriveTreeList(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("UUID", paramMap.get("BOX_UUID"));
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			returnList.put("list", webhardUserSvc.getSelectDriveTreeList(params));
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}
	
	/**
	 * createBox: 선택 드라이브 BOX 생성
	 * @param paramMap HMap<String, String>
	 * @return 생성된 박스 정보
	 */
	@RequestMapping(value = "user/tree/createBox.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap createBox(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {			
			CoviMap params = new CoviMap();
			params.put("ownerType", paramMap.get("ownerType"));
			params.put("ownerId", paramMap.get("ownerId"));
			params.put("boxName", paramMap.get("boxName"));
			params.put("domainCode", paramMap.get("domainCode"));
			
			webhardUserSvc.createBox(params);
			
			returnList.put("createBoxInfo", params);
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * getCheckedTreeData: 선택된 트리의 폴더/파일 가져오기
	 * @param paramMap Map<String, String>
	 * @return 폴더/파일 리스트
	 */
	@RequestMapping(value = "user/tree/getCheckedTreeData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getCheckedTreeData(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.putAll(paramMap);
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("groupCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			
			if (params.get("mode") == null || StringUtil.isNull(params.getString("mode"))) {
				params.put("mode", "user");
			}
			
			returnList.put("fileList", webhardUserSvc.getSelectTreeFileList(params));
			returnList.put("folderList", webhardUserSvc.getSelectTreeFolderList(params));
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}

	/**
	 * saveBookMark: 북마크
	 * @param paramMap Map<String, String>
	 * @return 폴더/파일 리스트
	 */
	@RequestMapping(value = "user/saveBookMark.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap saveBookMark(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {			
			CoviMap params = new CoviMap();
			params.putAll(paramMap);
			
			if(params.get("TYPE").toString().equals("file")) {
				returnList.put("cnt", webhardUserSvc.updateFileBookmark(params));
			}else {
				returnList.put("cnt", webhardUserSvc.updateFolderBookmark(params));
			}
			
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "저장되었습니다.");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}
	
	/**
	 * getSelectRecentFileList: 선택된 BOX 최근문서 가져오기
	 * @param paramMap Map<String, String>
	 * @return 폴더/파일 리스트
	 */
	@RequestMapping(value = "user/getSelectRecentFileList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getSelectRecentFileList(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.putAll(paramMap);
			
			returnList.put("list", webhardUserSvc.getSelectRecentFileList(params));
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다.");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * getSelectImportantFileList: 선택된 BOX의 중요 폴더/파일 가져오기
	 * @param paramMap Map<String, String>
	 * @return 폴더/파일 리스트
	 */
	@RequestMapping(value = "user/getSelectImportantFileList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getSelectImportantFileList(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.putAll(paramMap);
			
			returnList.put("fileList",  webhardUserSvc.getSelectImportantFileList(params));
			returnList.put("folderList", webhardUserSvc.getSelectImportantFolderList(params));
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}
	
	/**
	 * getTrashbinBoxList: 휴지통의 폴더/파일 목록 조회
	 * @param paramMap Map<String, String>
	 * @return 폴더/파일 목록
	 */
	@RequestMapping(value = "user/getTrashbinBoxList.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getTrashbinBoxList(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.putAll(paramMap);
			
			returnList.put("fileList", webhardFileSvc.selectTrashbinFileList(params));
			returnList.put("folderList", webhardFolderSvc.selectTrashbinFolderList(params));
			returnList.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}
	
	/**
	 * uploadToFront: 공통 파일 첨부 - 웹하드 파일 첨부를 위한 FrontStorage 복사 작업
	 * @RequestParam paramMap
	 * @return returnList
	 */
	@RequestMapping(value="attach/uploadToFront.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap uploadToFront(@RequestParam Map<String, String> paramMap) {
		CoviList returnList = new CoviList();
		CoviMap returnData = new CoviMap();

		try{
			String companyCode = SessionHelper.getSession("DN_Code");
			String FrontPath = FileUtil.getFrontPath(companyCode);
			if(FrontPath.endsWith("/")){
				FrontPath = FrontPath.substring(0,FrontPath.length()-1);
			}
			String fullPath = FrontPath + File.separator + companyCode;
			
			CoviMap params = new CoviMap();
			params.put("fileUuids", paramMap.get("fileUuids").split(";"));
			
			List<MultipartFile> mf = new ArrayList<>();
			List<CoviMap> fileInfoList = webhardFileSvc.selectFileInfoList(params);

			for(CoviMap fileInfo : fileInfoList){
				String fileName = fileInfo.getString("fileName");
				String fileFullPath = fileInfo.getString("boxPath") + fileInfo.getString("filePath");
				fileFullPath = fileFullPath.replace("/", File.separator);

				mf.add(FileUtil.makeMockMultipartFile(fileFullPath, fileName));
			}

			if (FileUtil.isEnableExtention(mf)) {
				if (!mf.isEmpty()) {
					for(int i = 0; i < mf.size(); i++){
						CoviMap frontObj = new CoviMap();
						
						String originalfileName = mf.get(i).getOriginalFilename();
						String genId = UUID.randomUUID().toString().replace("-", ""); // 파일 중복명 처리
						String ext = FilenameUtils.getExtension(originalfileName);
						String saveFileName = genId + "." + ext; // 저장되는 파일 이름
						String size = String.valueOf(mf.get(i).getSize());
						
						String fullFileNamePath = fullPath + fileSvc.getFILE_SEPARATOR() + saveFileName; // 저장 될 파일 경로
						fullFileNamePath = fullFileNamePath.replaceAll("//","/");
						fullFileNamePath = new String(fullFileNamePath.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
						File originFile = null;

						if(awsS3.getS3Active()){
							awsS3.upload(mf.get(i).getInputStream(), fullFileNamePath, mf.get(i).getContentType(), mf.get(i).getSize());
							/*originFile = FileUtil.multipartToFile(mf.get(i));

							if(fileSvc.getIS_USE_DECODE_DRM().equalsIgnoreCase("Y")){
								//TODO - DRM for AWS S3
							}*/
							if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("bmp")){
								fileSvc.makeThumb(fullPath + fileSvc.getFILE_SEPARATOR() + genId + "_thumb.jpg", mf.get(i).getInputStream()); //썸네일 저장
							}
						}else {
							originFile = new File(FileUtil.checkTraversalCharacter(fullFileNamePath));

							// FrontStorage 경로 없을 때 경로에 폴더 생성하도록 처리
							File folder = new File(FileUtil.checkTraversalCharacter(fullPath));

							if (!folder.exists()) {
								if(!folder.mkdirs()) {
									LOGGER.debug("Failed to make directories.");
								}
							}

							fileSvc.transferTo(mf.get(i), originFile, ext);

							if(fileSvc.getIS_USE_DECODE_DRM().equalsIgnoreCase("Y")){
								originFile = fileSvc.callDRMDecoding(originFile, fullFileNamePath);
							}

							if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("bmp")){
								fileSvc.makeThumb(fullPath + fileSvc.getFILE_SEPARATOR() + genId + "_thumb.jpg", originFile); //썸네일 저장
							}
						}

						/*if(awsS3.getS3Active()) {
							originFile.delete();
						}*/
						frontObj.put("FileType", "webhard");
						frontObj.put("FileName", originalfileName);
						frontObj.put("SavedName", saveFileName);
						frontObj.put("Size", size);
						
						returnList.add(frontObj);
					}
				}
			} else {
				returnData.put("list", "0");
				returnData.put("status", Return.FAIL);
				return returnData;
			}
			
			returnData.put("files", returnList);
			returnData.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnData;
	}
	
	/**
	 * uploadFile - 웹하드 - 업로드(메일)
	 * @param request HTTP Request Object
	 * @return 처리 결과
	 */
	@RequestMapping(value = "user/uploadFileFromMail.do", method = RequestMethod.POST)
	@Transactional
	public @ResponseBody CoviMap uploadFileToMail(HttpServletRequest request) {
		CoviMap returnObj = new CoviMap();
		
		try {
			CoviList saveFileInfoList = CoviList.fromObject(JSONSerializer.toJSON(StringUtil.replaceNull(request.getParameter("saveFileInfo"),"").replaceAll("&quot;", "\"")));
			
			if(!saveFileInfoList.isEmpty()){
				CoviList fileInfos = new CoviList();
				
				List<MultipartFile> files = new ArrayList<>();
				
				for(int i = 0; i < saveFileInfoList.size(); i++){
					CoviMap saveFileInfo = saveFileInfoList.getJSONObject(i);
					
					String saveFilePath = saveFileInfo.getString("saveFilePath");
					String saveFileName = saveFileInfo.getString("saveFileName");
					
					files.add(FileUtil.makeMockMultipartFile(saveFilePath + saveFileName, saveFileName));
					
					// fileInfo는 실제 file과 같은 인덱스의 데이터로 관리되어야 함
					String uuid = commonSvc.generateUuid();
					CoviMap fileInfo = new CoviMap();
					fileInfo.put("UUID", uuid);
					fileInfo.put("boxUUID", request.getParameter("boxUuid"));
					fileInfo.put("folderUUID", request.getParameter("folderUuid"));
					fileInfo.put("filePath", "/" + uuid);
					
					fileInfos.add(fileInfo);
				}
				
				CoviMap params = new CoviMap();
				params.put("boxUuid", request.getParameter("boxUuid"));
				
				if(!webhardUserSvc.checkUsageWebHard(params, files)){
					returnObj.put("status", "BOX_FULL");
					return returnObj;
				}
				
				if(!webhardUserSvc.checkUploadSize(files)){
					returnObj.put("status", "UPLOAD_MAX");
					return returnObj;
				}
				
				if(fileInfos != null && !fileInfos.isEmpty()
					&& files != null && !files.isEmpty()){
					int result = webhardFileSvc.saveFileInfo(commonSvc.getBaseInfo(), files, fileInfos);
					
					if(result <= 0){
						returnObj.put("status", Return.FAIL);
						return returnObj;
					}
				}
				
				returnObj.put("status", Return.SUCCESS);
			}else{
				returnObj.put("status", "NOT_EXIST_FILE");
			}
		} catch(NullPointerException e) {
			returnObj.put("status", Return.FAIL);
			returnObj.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			returnObj.put("status", Return.FAIL);
			returnObj.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnObj;
	}
	
	/**
	 * getExtConfig: 업로드 제한 확장자 조회
	 * @param paramMap Map<String, String>
	 * @return JSONObject
	 */
	@RequestMapping(value = "user/getExtConfig.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getExtConfig(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("domainID", SessionHelper.getSession("DN_ID"));
			
			returnList.put("ext", webhardUserSvc.getExtConfig(params));
			returnList.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * getSharedData: '공유받은 폴더' 폴더 데이터 가져오기.
	 * @param paramMap Map<String, String>
	 * @return 폴더 목록
	 */
	@RequestMapping(value = "user/getSharedData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getSharedData(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.putAll(paramMap);
			
			// 조회 파라미터 추가.
			params.put("userCode", SessionHelper.getSession("USERID"));
			params.put("subjectInArr", authorityService.getAssignedSubject(SessionHelper.getSession()));
			
			if ( (params.get("startDate") == null) || (params.get("endDate") == null) ) {
				params.put("startDate", "");
				params.put("endDate", "");
			}
			
			// 조회 건수 조회.
			int cnt = webhardUserSvc.selectSharedCnt(params);
			
			// 데이터 조회. 건수가 0이면 데이터 조회 pass.
			CoviMap resultList = new CoviMap();
			if (cnt == 0) {
				returnList.put("list", resultList );
			} else {
				resultList = webhardUserSvc.selectSharedList(params);
				returnList.put("list", resultList.get("list"));
			}
			
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}

	/**
	 * getPublishedData: '공유받은 폴더' 폴더 데이터 가져오기.
	 * @param paramMap Map<String, String>
	 * @return 폴더 목록
	 */
	@RequestMapping(value = "user/getPublishedData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getPublishedData(@RequestParam Map<String, String> paramMap) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.putAll(paramMap);
			
			// 조회 파라미터 추가.
			params.put("userCode", SessionHelper.getSession("USERID"));
			
			if ( (params.get("startDate") == null) || (params.get("endDate") == null) ) {
				params.put("startDate", "");
				params.put("endDate", "");
			}
			
			// 조회 건수 조회.
			int cnt = webhardUserSvc.selectPublishedCnt(params);
			
			// 데이터 조회. 건수가 0이면 데이터 조회 pass.
			CoviMap resultList = new CoviMap();
			if (cnt == 0) {
				returnList.put("list", resultList);
			} else {
				resultList = webhardUserSvc.selectPublishedList(params);
				returnList.put("list", resultList.get("list"));
			}
			
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList; 
	}
	
	/**
	 * getFolderOrFileName: 폴더 및 파일 명 조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return 드라이브 리스트
	 */
	@RequestMapping(value = "user/getFolderOrFileName.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getFolderOrFileName(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			String mode = request.getParameter("mode");

			if(mode.equals("edit")) {
				params.put("boxUuid", request.getParameter("boxUuid"));
				params.put("folderUuid", request.getParameter("folderUuid"));
				returnList.put("existingName", webhardFolderSvc.getFolderName(params));
				returnList.put("status", Return.SUCCESS);
			} else if(mode.equals("edit_file")) {
				params.put("boxUuid", request.getParameter("boxUuid"));
				params.put("fileUuid", request.getParameter("fileUuid"));	
				
				String fileName = webhardFileSvc.getFileName(params);
				String existingName = fileName.substring(0, fileName.lastIndexOf("."));
				
				returnList.put("existingName", existingName);
				returnList.put("status", Return.SUCCESS);
			}
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * getDomainMaxUploadSize: 도메인 별 업로드 최대 사이즈 조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return 도메인 별 최대 업로드 사이즈
	 */
	@RequestMapping(value = "user/getDomainMaxUploadSize.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getDomainMaxUploadSize(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("domainID", SessionHelper.getSession("DN_ID"));
			
			returnList.put("maxUploadSize", webhardUserSvc.getDomainMaxUploadSize(params));
			returnList.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * getTotalFileData: 전체 파일 데이터 조회
	 * @param request HTTP Request Object
	 * @param response HTTP Response Object
	 * @return 파일 리스트
	 */
	@RequestMapping(value = "user/tree/getTotalFileData.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getTotalFileData(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnData = new CoviMap();
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			CoviMap boxUUIDMap = new CoviMap();
			CoviMap domain = commonSvc.getOriginDomain();
			CoviList boxUUIDList = new CoviList();
		
			String searchText = StringUtil.replaceNull(request.getParameter("searchText"), "");
			String startDate = StringUtil.replaceNull(request.getParameter("startDate"), "");
			String endDate = StringUtil.replaceNull(request.getParameter("endDate"), "");
			startDate = startDate.replace(".", "-");
			endDate = endDate.replace(".", "-");
					
			params.put("domainCode", domain.getString("DomainCode"));
			params.put("userCode", SessionHelper.getSession("UR_ID"));
			params.put("deptCode", SessionHelper.getSession("DEPTID"));
			params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
			params.put("lang", SessionHelper.getSession("lang"));
			params.put("subjectInArr", authorityService.getAssignedSubject(SessionHelper.getSession()));
			
			String pageNo = request.getParameter("pageNo");
			String pageSize = request.getParameter("pageSize");
			params.put("pageNo", pageNo);
			params.put("pageSize", pageSize);
			
			if (params.get("mode") == null || StringUtil.isNull(params.getString("mode"))) {
				params.put("mode", "user");
			}

			boxUUIDMap.put("list", webhardUserSvc.getUserDriveList(params));
			boxUUIDList = boxUUIDMap.getJSONArray("list");
			String boxUUIDStr = "";
			
			for(int i=0; i<boxUUIDList.size(); i++) {
				CoviMap tmp = boxUUIDList.getMap(i);
				
				boxUUIDStr += tmp.get("BOX_UUID") + " ";
			}
			
			String[] boxUUIDArr = boxUUIDStr.split(" ");
			
			params.put("boxUUIDArr", boxUUIDArr);
			params.put("searchText", searchText);
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			
			returnList = webhardUserSvc.selectTotalFileList(params);
			
			returnData.put("list", returnList.get("list"));
			returnData.put("page", returnList.get("page"));
			returnData.put("status", Return.SUCCESS);
			returnData.put("message", "조회되었습니다");
		} catch (NullPointerException e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnData.put("status", Return.FAIL);
			returnData.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	@RequestMapping(value = "user/loadImage/{fileUUID}", method = RequestMethod.GET)
	public void viewImage(HttpServletResponse response, @PathVariable String fileUUID) throws Exception {
		try {
			String companyCode = SessionHelper.getSession("DN_Code");
			CoviMap params = new CoviMap();
			params.put("fileUuid", fileUUID);
			CoviMap fileInfo = webhardFileSvc.getFileInfo(params);
			
			String fullPath =  fileInfo.getString("fileRealPath");
			String filePath = fileInfo.getString("filePath");
			String fileType = fileInfo.getString("fileType");
			
			fileSvc.loadImageByPath(response, companyCode, fullPath, filePath, fileType, null);
		} catch(NullPointerException e) {
			LOGGER.error("WebhardUserCon/viewImage", e);
		} catch(Exception e) {
			LOGGER.error("WebhardUserCon/viewImage", e);
		}
	};
	
	@RequestMapping(value = "user/thumbnail/{fileUUID}", method = RequestMethod.GET)
	public void loadThumbImage(HttpServletResponse response, @PathVariable String fileUUID) throws Exception {
		try {
			CoviMap params = new CoviMap();
			
			params.put("fileUuid", fileUUID);
			CoviMap fileInfo = webhardFileSvc.getFileInfo(params);
			
			String imgUrl =  fileInfo.getString("fileRealPath");
			BufferedImage sourceImage= ImageIO.read(new File(imgUrl));
			
			int width=120;
			int height=120;
			
			byte[] buffer = null;
	
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
			Image scaledImage = sourceImage.getScaledInstance(width,height, Image.SCALE_SMOOTH);
			
			img.createGraphics().drawImage(scaledImage, 0, 0, null);
	
			BufferedImage thumbNail = new BufferedImage(width, height ,BufferedImage.TYPE_INT_RGB);
	
			thumbNail = img.getSubimage(0, 0, width, height);
	
			OutputStream os =response.getOutputStream();
			ImageIO.write(thumbNail, "jpeg", os);
			os.close();
		} catch(NullPointerException e) {
			LOGGER.error("WebhardUserCon/loadThumbImage", e);
		} catch(IOException e) {
			LOGGER.error("WebhardUserCon/loadThumbImage", e);
		} catch(Exception e) {
			LOGGER.error("WebhardUserCon/loadThumbImage", e);
		}
	}
}
