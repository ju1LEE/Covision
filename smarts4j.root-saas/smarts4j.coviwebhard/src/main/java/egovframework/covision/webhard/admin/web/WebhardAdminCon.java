package egovframework.covision.webhard.admin.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
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
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.util.ComUtils;
import egovframework.coviframework.util.FileUtil;
import egovframework.covision.webhard.admin.service.WebhardAdminSvc;
import egovframework.covision.webhard.properties.ReloadWebhardPropertyHelper;

/**
 * @Class Name : WebhardAdminCon.java
 * @Description : 웹하드 - 관리자 페이지
 * @Modification Information 
 * @ 2019.02.12 최초생성
 *
 * @author 코비젼 연구소
 * @since 2019. 02.12
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
@Controller
public class WebhardAdminCon {
	static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(WebhardAdminCon.class.getName());
			
	@Autowired
	private WebhardAdminSvc webhardAdminSvc;
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");	
	
	/**
	 * getConfig: 웹하드 - 환경설정 조회
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/config/getConfig.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap getConfig(HttpServletRequest request) {
		CoviMap resultList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("domainID", request.getParameter("domainID"));
			
			CoviMap configMap = webhardAdminSvc.searchConfig(params);
			
			if (configMap != null) {
				resultList.put("config", webhardAdminSvc.searchConfig(params));
			} else {
				resultList.put("config", "");
			}
			
			resultList.put("status", Return.SUCCESS);
		} catch(NullPointerException e) {
			resultList.put("status", Return.FAIL);
			resultList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			resultList.put("status", Return.FAIL);
			resultList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return resultList;
	}
	
	/**
	 * saveConfig: 웹하드 - 환경설정 저장
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/config/save.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap saveConfig(HttpServletRequest request) {
		CoviMap resultList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("domainID", request.getParameter("domainID"));
			params.put("boxVolume", request.getParameter("boxVolume"));
			params.put("maxUploadSize", request.getParameter("maxUploadSize"));
			params.put("extensions", request.getParameter("extensions"));
			params.put("isSharing", request.getParameter("isSharing"));
			
			int result = webhardAdminSvc.saveConfig(params);
			
			if (result > 0) {
				resultList.put("status", Return.SUCCESS);
			} else {
				resultList.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			resultList.put("status", Return.FAIL);
			resultList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			resultList.put("status", Return.FAIL);
			resultList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return resultList;
	}
	
	/**
	 * list: 박스 목록 조회
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/list.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap searchBoxList(HttpServletRequest request) {
		CoviMap resultObject = new CoviMap();
		CoviMap returnObject = new CoviMap();
		
		try {
			String sortBy = request.getParameter("sortBy");
			
			String sortColumn = (sortBy != null)? sortBy.split(" ")[0] : "";
			String sortDirection = (sortBy != null)? sortBy.split(" ")[1] : "";
			
			CoviMap params = new CoviMap();
			
			params.put("pageNo", request.getParameter("pageNo"));
			params.put("pageSize", request.getParameter("pageSize"));
			params.put("domainID", request.getParameter("domainID"));
			params.put("searchWord", request.getParameter("searchWord"));
			params.put("searchOption", request.getParameter("searchOption"));
			params.put("sortColumn", ComUtils.RemoveSQLInjection(sortColumn, 100));
			params.put("sortDirection", ComUtils.RemoveSQLInjection(sortDirection, 100));
			
			resultObject = webhardAdminSvc.searchBoxList(params);
			
			returnObject.put("page", resultObject.get("page"));
			returnObject.put("list", resultObject.get("list"));
			returnObject.put("status", Return.SUCCESS);
		} catch (NullPointerException e) {
			returnObject.put("status", Return.FAIL);
			returnObject.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch (Exception e) {
			returnObject.put("status", Return.FAIL);
			returnObject.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnObject;
	}
	
	/**
	 * getBoxConfig : 웹하드 - BOX 설정값 조회
	 * @param request
	 * @param response
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/getBoxConfig.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getBoxConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("ownerID", request.getParameter("ownerID"));
			
			returnList.put("data", webhardAdminSvc.getBoxConfig(params));
			returnList.put("result", "ok");
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
	 * setBoxConfig : 웹하드 - BOX 설정값 수정
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/setBoxConfig.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap setBoxConfig(HttpServletRequest request) throws Exception {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("totalQuota", request.getParameter("totalQuota"));
			params.put("ownerID", request.getParameter("ownerID"));
			
			int result = webhardAdminSvc.setBoxConfig(params);
			
			if (result > 0) {
				returnList.put("status", Return.SUCCESS);
			} else {
				returnList.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * blockBox : 웹하드 - 박스 잠금
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/blockBox.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap blockBox(HttpServletRequest request) throws Exception {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("boxUuids", request.getParameter("boxUuids"));
			
			int result = webhardAdminSvc.blockBox(params);
			
			if (result > 0) {
				returnList.put("status", Return.SUCCESS);
			} else {
				returnList.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * setBlockYn : 웹하드 - 잠금 여부 설정
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/setBoxBlockYn.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap setBlockYn(HttpServletRequest request) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("boxUuid", request.getParameter("boxUuid"));
			params.put("blockYn", request.getParameter("blockYn"));
			params.put("blockReason", request.getParameter("blockReason"));
			
			int result = webhardAdminSvc.setBoxBlockYn(params);
			
			if (result > 0) {
				returnList.put("status", Return.SUCCESS);
			} else {
				returnList.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}

	/**
	 * getBoxBlockReason : 웹하드 - 잠금 사유
	 * @param request
	 * @param response
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/getBoxBlockReason.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getBoxBlockReason(HttpServletRequest request, HttpServletResponse response) {
		CoviMap returnList = new CoviMap();

		try {
			CoviMap params = new CoviMap();
			
			params.put("boxUuid", request.getParameter("boxUuid"));
			
			returnList.put("reason", webhardAdminSvc.getBoxBlockReason(params));
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
	 * deleteBox : 웹하드 - 박스 삭제
	 * @param request
	 * @param response
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/deleteBox.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap deleteBox(HttpServletRequest request, HttpServletResponse response) {	
		CoviMap returnList = new CoviMap();

		try {
			CoviMap params = new CoviMap();
			
			params.put("boxUuids", request.getParameter("boxUuids"));
			
			int result = webhardAdminSvc.deleteBox(params);
			
			if (result > 0) {
				returnList.put("status", Return.SUCCESS);
			} else {
				returnList.put("status", Return.FAIL);
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
	 * setUseYn : 웹하드 - 사용 여부 설정
	 * @param request
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/boxManage/setBoxUseYn.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap setUseYn(HttpServletRequest request) throws Exception {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("boxUuid", request.getParameter("boxUuid"));
			params.put("useYn", request.getParameter("useYn"));
			
			int result = webhardAdminSvc.setBoxUseYn(params);
			
			if (result > 0) {
				returnList.put("status", Return.SUCCESS);
			} else {
				returnList.put("status", Return.FAIL);
			}
		} catch(NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * callBoxConfigPopup : 웹하드 - 박스 설정 레이어 팝업
	 * @param paramMap Map<String, String>
	 * @return mav
	 */
	@RequestMapping(value = "admin/popup/callBoxConfigPopup.do", method = RequestMethod.GET)
	public ModelAndView callBoxConfigPopup(@RequestParam Map<String, String> paramMap) throws Exception {
		ModelAndView mav = new ModelAndView("webhard/admin/popupBoxConfig");
		mav.addAllObjects(paramMap);
		return mav;
	}
	
	/**
	 * callBoxLockPopup: 박스 잠금 등의 코멘트 설정용 팝업
	 * @param paramMap Map<String, String>
	 * @return mav
	 */
	@RequestMapping(value = "admin/popup/callBoxLockPopup.do", method = RequestMethod.GET)
	public ModelAndView callBoxLockPopup(@RequestParam Map<String, String> paramMap) throws Exception {
		ModelAndView mav = new ModelAndView("webhard/admin/popupBoxLock");
		mav.addAllObjects(paramMap);
		return mav;
	}
	
	/**
	 * callFileDeletePopup: 파일 삭제 팝업
	 * @param paramMap Map<String, String>
	 * @return mav
	 */
	@RequestMapping(value = "admin/popup/callFileDeletePopup.do", method = RequestMethod.GET)
	public ModelAndView callFileDeletePopup(@RequestParam Map<String, String> paramMap) throws Exception {
		ModelAndView mav = new ModelAndView("webhard/admin/popupFileDelete");
		mav.addAllObjects(paramMap);
		return mav;
	}
	
	/**
	 * list: 파일 목록 조회
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/fileSearch/list.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap searchFileList(HttpServletRequest request) throws Exception {
		CoviMap returnList = new CoviMap();
		
		try {
			int pageNo = StringUtil.isNull(request.getParameter("pageNo")) ? 1 : Integer.parseInt(request.getParameter("pageNo"));
			int pageSize = StringUtil.isNull(request.getParameter("pageSize")) ? 10 : Integer.parseInt(request.getParameter("pageSize"));
			
			String sortBy = request.getParameter("sortBy");
			
			String sortColumn = (sortBy != null)? sortBy.split(" ")[0] : "";
			String sortDirection = (sortBy != null)? sortBy.split(" ")[1] : "";
			
			CoviMap params = new CoviMap();
			
			params.put("pageNo", pageNo);
			params.put("pageSize", pageSize);
			params.put("domainID", request.getParameter("domainID"));
			params.put("fileType", request.getParameter("fileType"));
			params.put("fileName", request.getParameter("fileName"));
			params.put("fileSizeOption", request.getParameter("fileSizeOption"));
			params.put("periodOption", request.getParameter("periodOption"));
			params.put("startDate", request.getParameter("startDate"));
			params.put("endDate", request.getParameter("endDate"));
			params.put("sortColumn", ComUtils.RemoveSQLInjection(sortColumn, 100));
			params.put("sortDirection", ComUtils.RemoveSQLInjection(sortDirection, 100));
			
			
			CoviMap result = webhardAdminSvc.searchFileList(params);
			
			returnList.put("page", result.get("page"));
			returnList.put("list", result.get("list"));
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
	 * deleteFile: 웹하드 - 파일 삭제
	 * @param request
	 * @return JSONObject
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/fileSearch/delete.do", method = RequestMethod.POST)
	public @ResponseBody CoviMap deleteFile(HttpServletRequest request) {
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			
			params.put("fileUuids", request.getParameter("fileUuids"));
			params.put("comment", request.getParameter("comment"));
			
			int result = webhardAdminSvc.deleteFile(params);
			
			if (result == 0) {
				returnList.put("status", Return.FAIL);
				return returnList;
			}
			
			returnList.put("status", Return.SUCCESS);
		} catch(NullPointerException e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		} catch(Exception e) {
			returnList.put("status", Return.FAIL);
			returnList.put("message", isDevMode.equalsIgnoreCase("Y") ? e.getMessage() : DicHelper.getDic("msg_apv_030"));
		}
		
		return returnList;
	}
	
	/**
	 * fileDown: 웹하드 - 파일 다운로드
	 * @param request
	 * @param response
	 * @return mav
	 * @throws Exception
	 */
	@RequestMapping(value = "admin/fileSearch/fileDown.do", method = RequestMethod.POST)	
	public ModelAndView fileDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileUuids = request.getParameter("fileUuids");
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("mode", "admin");
		mav.addObject("fileUuids", fileUuids);
		mav.addObject("folderUuids", "");
		mav.setViewName("fileDownloadView");
		
		return mav;
	}

	/**
	 * 2021 웹하드 고도화, Migration.
	 */
	@RequestMapping(value = "admin/config/mig2021webhard.do", method = RequestMethod.GET)
	public @ResponseBody CoviMap mig2021webhard(HttpServletRequest request) throws Exception {

		String osType = PropertiesUtil.getGlobalProperties().getProperty("Globals.OsType");		// OS 정보 불러오기.
		String rootPath = "";
		
		Configuration props = ReloadWebhardPropertyHelper.getCompositeConfiguration();			// 설정정보 불러오기.
				
		if(osType.equals("UNIX")){		// UNIX 계열일 때 물리적 경로 불러오기.
			rootPath = props.getString("webhard.UNIX.path");
		} else {						// windows 계열.
			rootPath = props.getString("webhard.WINDOW.path");
		}
		String webhardHomePath = props.getString("webhard.home.path");	// webhard.properties 안의 webhard.home.path 값.(/SaaSWebhard)
		String tmpDir = ""; 											// 임시 물리적 주소값
		
		Logger log4Mig = Logger.getLogger("mig2021webhard");			// 마이그레이션용 로그파일 준비(프로젝트에서 사용하는 로그 아닌 다른 파일을 생성).
		log4Mig.setLevel(Level.ALL);
		FileHandler fileHandler;
		
		CoviMap returnList = new CoviMap();
		LocalDate dateNow = LocalDate.now();
		LocalTime timeNow = LocalTime.now();
		
		// 1. 마이그레이션 대상을 조회(기존의 uuid로 경로 설정되어 있는 것들은 제외).
		// 1-1. WH_BOX_LIST 중에서 BOX_PATH가 email 뒷부분으로 되어 있는 것을 조회. OWNER_TYPE = 'U' 인 사용자 대상으로만 한정. 기존에는 'U'만 사용하고 있었음.
		CoviList boxList = webhardAdminSvc.selectMigBoxList();
		// 1-2. wh_folder_list 기준 폴더 조회.(DB 상에서 경로가 파일명으로 되어있는 것만 추출(마이그레이션 대상). FILE의 UUID가 FILE_PATH에 포함되어 있으면 제외.)
		CoviList folderList = webhardAdminSvc.selectMigFolderList();
		CoviList fileList = webhardAdminSvc.selectMigFileList();		// 1-3. wh_file_list의 파일 리스트 조회.
		CoviList fileDelList = webhardAdminSvc.selectMigDelFileInfo();	// 1-4. wh_file_list의 삭제된 파일 리스트 조회.		
		
		CoviList resultErrLog = new CoviList();			// log 기록.
		CoviList createdFolderLog = new CoviList();
		CoviList copiedFileLog = new CoviList();
		CoviList fileNotExistLog = new CoviList();
		CoviList fileSizeDiffer = new CoviList();
		
		fileHandler = new FileHandler( rootPath + "/mig2021webhard_" + dateNow.getMonthValue() + dateNow.getDayOfMonth() + "_" + timeNow.getHour() + timeNow.getMinute() + "_%g.log", false);
		fileHandler.setLevel(Level.ALL);
		log4Mig.addHandler(fileHandler);
		
		SimpleFormatter formatter = new SimpleFormatter();
		fileHandler.setFormatter(formatter);

		log4Mig.info("============== START =================");
		
		// 2. 생성 및 복사.
		// 2-1. wh_box_list 기준 BOX UUID 폴더 생성		
		Iterator<CoviMap> iter = boxList.iterator();
		mig2021createFolder("BOX", rootPath, webhardHomePath, iter, log4Mig, createdFolderLog);
		
		// 2-2. wh_folder_list 관련 폴더 생성.
		iter = folderList.iterator();
		mig2021createFolder("FOLDER", rootPath, webhardHomePath, iter, log4Mig, createdFolderLog);
		
		// 2-3. wh_file_list의 파일 생성. TOBE_PATH : {rootPath}/{domainCode}/{webhardHomePath}/{boxUuid}
		iter = fileList.iterator();
		CoviMap coviMap = new CoviMap();
		String originFilePath = "";
		
		while (iter.hasNext()) {
			coviMap = iter.next();
			tmpDir = rootPath;
			tmpDir += "/" + coviMap.get("DOMAIN_CODE");
			tmpDir += "/" + webhardHomePath;
			tmpDir += "" + coviMap.get("TOBE_PATH");  	// {BOX_UUID 포함 폴더까지의 경로}
			tmpDir += "/" + coviMap.get("FILE_UUID");
			tmpDir = tmpDir.replaceAll("//", "/");		// '/'값이 중복으로 들어가 있다면 변경(개발서버에 /값이 있기도 없기도 함).
			
			originFilePath = "" + coviMap.get("ASIS_PATH");
			mig2021copyFile(originFilePath, tmpDir, ""+coviMap.get("DOMAIN_ID"), log4Mig, copiedFileLog, fileNotExistLog, resultErrLog, fileSizeDiffer);
		}
		
		// 2-4. wh_file_list의 삭제된 파일 복사(files_trashbin).
		// 생성한 boxList 기준으로 /files_trashbin 안의 파일을 검색한다.
		iter= boxList.iterator();
		while (iter.hasNext()) {
			coviMap = iter.next();
			tmpDir = rootPath;			// TOBE_PATH : {rootPath}/{domainCode}/{webhardHomePath}/{boxUuid}
			tmpDir += "/" + coviMap.get("DOMAIN_CODE");
			tmpDir += "/" + webhardHomePath;
			tmpDir = tmpDir.replaceAll("//", "/");
			
			File fileTrashbin = new File("" + coviMap.get("BOX_PATH") + "/files_trashbin");		// 원본 파일 시작 위치. BOX_PATH로 시작 위치 검색.
			mig2021recursive(fileTrashbin, fileDelList, ""+coviMap.get("DOMAIN_ID"), ""+coviMap.get("BOX_PATH"), tmpDir, log4Mig, copiedFileLog, fileNotExistLog, resultErrLog, fileSizeDiffer);
		}
		
		String strMsg = "";			// 3. 로그 정리.
		log4Mig.info("");
		log4Mig.info("----------- LOG RESULT -----------");
		log4Mig.info("Created Folder Count		: " + createdFolderLog.size());
		log4Mig.info("Copied Files Count 		: " + copiedFileLog.size());
		log4Mig.info("File Not Exist Count 		: " + fileNotExistLog.size());
		log4Mig.info("File size differnet Count : " + fileSizeDiffer.size());
		log4Mig.info("");
		log4Mig.info("----------- ERROR RESULT("  + resultErrLog.size() + ") -----------");
		
		Iterator<String> iterLog = resultErrLog.iterator();
		while (iterLog.hasNext()) {
			strMsg = iterLog.next();
			log4Mig.info(strMsg);
		}
		
		returnList.put("status", Return.SUCCESS);
		log4Mig.info("============== END =================");
		
		return returnList;
	}
	
	/**
	 * 2021 웹하드 고도화, Migration.
	 * 경로(디렉토리) 생성
	 */
	public void mig2021createFolder(String type, String rootPath, String webhardHomePath, Iterator<CoviMap> iter, Logger log4Mig, CoviList createdFolderLog) {
		String tmpDir = "";
		String logMsg = "";
		CoviMap coviMap = new CoviMap();
		while (iter.hasNext()) {
			tmpDir = rootPath;
			coviMap = iter.next();
			tmpDir += "/" + coviMap.get("DOMAIN_CODE");		// DOMAIN_CODE 경로 추가
			tmpDir += "/" + webhardHomePath;				// webhardHomePath 추가(/SaaSWebhard).
			
			if (type.equals("BOX")) {
				tmpDir += "/" + coviMap.get("UUID");		// box_uuid 생성.
			} else if (type.equals("FOLDER")) {
				tmpDir += "/" + coviMap.get("BOX_UUID");
				tmpDir += "/" + coviMap.get("FOLDER_PATH");
			}
			tmpDir = tmpDir.replaceAll("//", "/");			// '/'값이 중복으로 들어가 있다면 변경(개발서버 data에서 발견).

			File folderDir = new File(FileUtil.checkTraversalCharacter(tmpDir));				// 폴더 생성.
			if (!folderDir.exists()) {
				if(!folderDir.mkdirs()) {
					LOGGER.debug("Failed to make directories.");
				}
				logMsg = "(1), DOMAIN_ID : " + coviMap.get("DOMAIN_ID") + ", UUID : " + coviMap.get("UUID") + ", FULL_PATH : " + tmpDir + ", Folder is created.";
				log4Mig.info(logMsg);						// 폴더 생성 로그.
				createdFolderLog.add(logMsg);
			} else {
				logMsg = "(0), DOMAIN_ID : " + coviMap.get("DOMAIN_ID") + ", UUID : " + coviMap.get("UUID") + ", FULL_PATH : " + tmpDir + ", Folder is already exist.";
				log4Mig.info(logMsg);						// 폴더가 이미 존재함.
			}
		}
	}

	/**
	 * 2021 웹하드 고도화, Migration.
	 * 파일 복사
	 */
	public void mig2021copyFile(String originFilePath, String newFilePath, String domainId , Logger log4Mig, CoviList copiedFileLog, CoviList fileNotExistLog,CoviList resultErrLog, CoviList fileSizeDiffer) {
		String logMsg = "";
		
		File originFile = new File(FileUtil.checkTraversalCharacter(originFilePath));
		File newFile = new File(FileUtil.checkTraversalCharacter(newFilePath));
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcIn = null;
		FileChannel fcOut = null;
		long size = 0;
		
		if (originFile.exists()) {
			// 원본파일, 새로운 파일 동시 존재 시, 파일 크기 비교.
			if ( newFile.exists() && (originFile.length() == newFile.length()) ) {
				logMsg = "(0), DOMAIN_ID : " + domainId + ", ORIGIN_PATH : " + originFilePath + ", File is already exist.";
				log4Mig.info(logMsg);
			} else {
				if ( (newFile.exists()) && (originFile.length() != newFile.length()) ) {	// 파일 복사 중 끊겼을 경우, 로그만 남기고, 복사 진행.
					logMsg = "(-1), DOMAIN_ID : " + domainId + ", ORIGIN_PATH : " + originFilePath + ", File is already exist, but file size different.";
					logMsg += " OriginFile : " + originFile.getName() + "(" + originFile.length() + "), NewFile : " + newFile.getName() + "(" + newFile.length() + ")";
					fileSizeDiffer.add(logMsg);
					log4Mig.info(logMsg + " resultErrorLog size : " + resultErrLog.size());
				}
				
				try {
					inputStream = new FileInputStream(originFile);
					outputStream = new FileOutputStream(newFile);
					fcIn = inputStream.getChannel();
					fcOut = outputStream.getChannel();
					size = fcIn.size();
					fcIn.transferTo(0, size, fcOut);		// 파일 복사.
					
					logMsg = "(1), DOMAIN_ID : " + domainId + ", ORIGIN_PATH : " + originFilePath + ", File is copied.";
					log4Mig.info(logMsg);
					copiedFileLog.add(logMsg);
				} catch (IOException e) {
					logMsg = "(-2), DOMAIN_ID : " + domainId + ", ORIGIN_PATH : " + originFilePath + ", Exception occurred";
					log4Mig.info(logMsg);
					resultErrLog.add(logMsg);
				} catch (Exception e) {
					logMsg = "(-2), DOMAIN_ID : " + domainId + ", ORIGIN_PATH : " + originFilePath + ", Exception occurred";
					log4Mig.info(logMsg);
					resultErrLog.add(logMsg);
				} finally {
					if (fcOut != null) {try { fcOut.close(); } catch(IOException ioe) {	LOGGER.error(ioe.getLocalizedMessage(), ioe);}}
					if (fcIn != null) {try { fcIn.close();} catch(IOException ioe) {LOGGER.error(ioe.getLocalizedMessage(), ioe);}}
					if (outputStream != null) {try {outputStream.close();} catch(IOException ioe) {LOGGER.error(ioe.getLocalizedMessage(), ioe);}}
					if (inputStream != null) {try {inputStream.close();} catch(IOException ioe) {LOGGER.error(ioe.getLocalizedMessage(), ioe);}}
				}	
			}
		} else {	// 원본 파일이 경로에 존재하지 않음.
			logMsg = "(0), DOMAIN_ID : " + domainId + ", ORIGIN_PATH : " + originFilePath + ", Origin file does not exist in ORIGIN_PATH.";
			log4Mig.info(logMsg);
			fileNotExistLog.add(logMsg);
		}
		
	}
	
	/**
	 * 2021 웹하드 고도화, Migration.
	 * 삭제파일(files_trashbin) 복사
	 */
	public void mig2021recursive(File fileTrashbin, CoviList fileDelList, String domainId, String strBoxPath, String homePath, Logger log4Mig, CoviList copiedFileLog, CoviList fileNotExistLog,CoviList resultErrLog, CoviList fileSizeDiffer) {
		String osType = PropertiesUtil.getGlobalProperties().getProperty("Globals.OsType");
		String filePath = "";
		
		CoviMap coviMap = new CoviMap();
		Iterator<CoviMap> iter;
		String newFilePath = "";
		String logMsg = "";
		
		if (fileTrashbin.exists()) {
			File files[] = fileTrashbin.listFiles();	// 사용자의 files_trashbin의 경로가 존재하면, 경로의 폴더/파일 리스트를 만들어 검색.
			if(files != null) {
				for (File fileObj : files) {	// files_trashbin 경로의 폴더/파일 목록.
					if (fileObj.isFile()) {		// 파일이면 fileDelList의 내용에서 해당 파일 정보를 찾는다(BOX_PATH, FILE_NAME 대조).
						iter = fileDelList.iterator();
						while (iter.hasNext()) {
							coviMap =  iter.next();
							filePath = ""+coviMap.get("FILE_PATH");
							if (!osType.equals("UNIX")) {						// 윈도우 OS일 경우, \로 변경.
								filePath = filePath.replaceAll("/", "\\\\");
							}
							
							// fileObj의 경로, 파일이름, 현재 진행 중인 row의 DOMAIN_ID를 이용, fileDelList 에서 FILE_UUID, TOBE_PATH를 알아낸다.
							if ( coviMap.get("BOX_PATH").equals(strBoxPath) &&  coviMap.get("FILE_NAME").equals(fileObj.getName())
									&& coviMap.get("DOMAIN_ID").equals(domainId) ) {
								
								// 현재는 파일 삭제 시 trashbin에 같은 파일명이 있으면 삭제되는 파일명과 wh_file_list에 들어가는 파일명에 (n)으로 구분되나,
								// 패치 이전에는 그런 기능이 없어 기존 삭제 파일을 덮어씌우는 CASE였음.
								// 예를 들어 삭제된 파일이 파일명은 같으나 경로가 다를 경우, 이를 파일명 만으로 구분할 수 없음.
								// 이에 가장 최근에 지워진 파일의 기록으로 migration 진행. 파일 복사를 진행하고, 반복문을 빠져나간다.
								logMsg = "(0), DOMAIN_ID : " + domainId + ", FILE_NAME : " + coviMap.get("FILE_NAME") + ", mig2021recursive(), file will be copy.";
								log4Mig.info(logMsg);
								
								newFilePath = homePath + coviMap.get("TOBE_PATH") + "/" +  coviMap.get("FILE_UUID");
								mig2021copyFile(fileObj.getPath(), newFilePath, domainId, log4Mig, copiedFileLog, fileNotExistLog, resultErrLog, fileSizeDiffer);
								break;
							}
						}
					} else if (fileObj.isDirectory()) {
						mig2021recursive( new File(fileObj.getPath()), fileDelList, domainId, strBoxPath, homePath, log4Mig, copiedFileLog, fileNotExistLog, resultErrLog, fileSizeDiffer);
					}
				}
			}
		}
	}

}
