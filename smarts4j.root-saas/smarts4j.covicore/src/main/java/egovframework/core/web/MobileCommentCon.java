package egovframework.core.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import egovframework.core.sevice.CommentSvc;
import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.coviframework.service.FileUtilService;
import egovframework.baseframework.util.DateHelper;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.coviframework.util.FileUtil;
import egovframework.coviframework.util.XSSUtils;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.SessionHelper;


@Controller
@RequestMapping("/mobile/comment/")
public class MobileCommentCon {
	
	private Logger LOGGER = LogManager.getLogger(MobileCommentCon.class);

	
	@Autowired
	private CommentSvc commentSvc;
	
	@Autowired
	private FileUtilService fileSvc;
	
	final String isDevMode = PropertiesUtil.getGlobalProperties().getProperty("isDevMode");
	
	/**
	 * getCommentInfo : 댓글 - 특정 댓글 1개 정보 조회
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "getCommentInfo.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getCommentInfo(
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String commentID = request.getParameter("commentID");
		
		CoviMap returnList = new CoviMap();
		
		CoviList resultList = new CoviList();
		
		try{
			
			CoviMap params = new CoviMap();
			params.put("commentID", commentID);
			
			//댓글 조회
			resultList = (CoviList) commentSvc.selectOne(params).get("list");
			
			//조회결과 리턴
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "OK");
			returnList.put("comment", resultList);
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
	 * getCommentLike : 댓글 - 좋아요/댓글 목록
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "getCommentLike.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap getCommentLike(
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String targetType = request.getParameter("targetType");
		String targetID = request.getParameter("targetID");
		String folderType = request.getParameter("folderType");
		
		CoviMap returnList = new CoviMap();
		
		CoviList resultList = new CoviList();
		
		try{
			
			CoviMap params = new CoviMap();
			params.put("registerCode", SessionHelper.getSession("USERID"));
			params.put("targetServiceType", targetType);
			params.put("targetID", targetID);
			params.put("folderType", folderType);
			
			// 1. 좋아요 카운트 조회
			int nLike = commentSvc.selectLikeCount(params);
			
			// 2. 댓글 카운트 조회
			int nComment = commentSvc.selectCommentCount(params);
			
			//3. 전체 댓글 리스트 조회
			resultList = (CoviList) commentSvc.selectListAll(params).get("list");
			
			//조회결과 리턴
			returnList.put("myLike", commentSvc.selectMyLike(params));
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "OK");
			returnList.put("likecount", nLike);
			returnList.put("commentcount", nComment);
			returnList.put("commentlist", resultList);
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
	 * addComment : 댓글 - 추가
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "addComment.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap addComment(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> map ) throws Exception
	{
		String targetType = Objects.toString(map.get("targetType"), "0");
		String targetID = Objects.toString(map.get("targetID"), "");
		String memberOf = Objects.toString(map.get("memberOf"), "");
		String folderType = Objects.toString(map.get("folderType"), "");
		String comment = XSSUtils.XSSFilter(Objects.toString(map.get("comment"), ""));
		
		//String context = request.getParameter("context");
		CoviMap objContext = new CoviMap();
		if(map.get("context") != null) {
			objContext = CoviMap.fromObject(map.get("context"));
		}
		
		CoviMap returnList = new CoviMap();
		
		CoviList resultList = new CoviList();
		
		try{
			
			CoviMap params = new CoviMap();
			params.put("targetServiceType", targetType);
			params.put("targetID", targetID);
			params.put("memberOf", memberOf);
			params.put("comment", comment);
			params.put("context", objContext);
			params.put("likeCnt", 0);
			params.put("replyCnt", 0);
			params.put("registerCode", SessionHelper.getSession("USERID"));
			params.put("registDate", DateHelper.getUTCString());
			params.put("deleteDate", null);
			params.put("reserved1", null);
			params.put("reserved2", null);
			params.put("reserved3", null);
			params.put("reserved4", null);
			params.put("reserved5", null);
			params.put("folderType", folderType);
			
			//댓글 추가
			resultList = (CoviList) commentSvc.insert(params).get("list");
			
			//결과 리턴
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "OK");
			returnList.put("add", resultList);
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
	 * delComment : 댓글 - 삭제
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "delComment.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap delComment(
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String commentID = request.getParameter("commentID");
		String memberOf = request.getParameter("memberOf");
		String folderType = request.getParameter("folderType");
		
		CoviMap returnList = new CoviMap();
		
		CoviList resultList = new CoviList();
		
		try{
			
			CoviMap params = new CoviMap();
			params.put("commentID", commentID);
			params.put("memberOf", memberOf);
			params.put("folderType", folderType);
			
			//상위 댓글이 있을 경우 답글 개수 체크
			if(Integer.parseInt(memberOf) != 0) {
				params.put("replyCount", commentSvc.selectReplyCount(Integer.parseInt(memberOf)));
			}
			
			//댓글 삭제
			resultList = (CoviList) commentSvc.delete(params).get("list");
			
			//결과 리턴
			returnList.put("status", Return.SUCCESS);
			returnList.put("message", "OK");
			returnList.put("delete", resultList);
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
	 * like : 좋아요 추가/삭제
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "like.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap like(
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String targetType = request.getParameter("targetType");
		String targetID = request.getParameter("targetID");
		String likeType = "";
		String emoticon = "";
		String point = "";
		String folderType = request.getParameter("FolderType");
		
		CoviMap returnList = new CoviMap();
		
		try {
			CoviMap params = new CoviMap();
			params.put("targetServiceType", targetType);
			params.put("targetID", targetID);
			params.put("likeType", likeType);
			params.put("emoticon", emoticon);
			params.put("point", point);
			params.put("folderType", folderType);
			params.put("registerCode", SessionHelper.getSession("USERID"));
			params.put("registDate", DateHelper.getUTCString());
			params.put("reserved1", null);
			params.put("reserved2", null);
			params.put("reserved3", null);
			
			returnList.put("data", commentSvc.insertLike(params));
			returnList.put("myLike", commentSvc.selectMyLike(params));
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
	 * uploadToFront : 프론트 저장소에 업로드
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "uploadToFront.do", method=RequestMethod.POST)
	public @ResponseBody CoviMap uploadToFront(MultipartHttpServletRequest request)
	{
		CoviMap returnList = new CoviMap();
		
		try {
			CoviList fileInfos = CoviList.fromObject(request.getParameter("fileInfos"));
			List<MultipartFile> mf = request.getFiles("files");
			
			if(FileUtil.isEnableExtention(mf)){
				returnList.put("list", fileSvc.uploadToFront(fileInfos, mf, request.getParameter("servicePath").toString()));
				returnList.put("status", Return.SUCCESS);
				returnList.put("message", "조회되었습니다");
			} else {
				returnList.put("list", "0");
				returnList.put("status", Return.FAIL);
				returnList.put("message", DicHelper.getDic("msg_ExistLimitedExtensionFile"));
			}
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
	 * previewImage : 미리보기화면에서 표시할 내용 파일 조회시 사용
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	//ex) [GET] /covicore/preview/Board/282.do
	@RequestMapping(value = "preview/{bizSection}/{fileID}", method = RequestMethod.GET)
	public void previewImage(HttpServletResponse response, @PathVariable String bizSection, @PathVariable long fileID) throws Exception {
	    try{
	    	String companyCode = SessionHelper.getSession("DN_Code");
	    	String strFileID = Long.toString(fileID);
	    	fileSvc.loadImageByID(response, strFileID, companyCode, "no_image.jpg", true);
	    } catch (NullPointerException e) {
	    	  LOGGER.error("FileUtilCon", e);
		} catch(Exception e){
	      if(!e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")) {
	    		// 해당 에러 외의 에러 처리
	    	  LOGGER.error("FileUtilCon", e);
	      }
	    }
	}
	
	/**
	 * previewImageSrc : 미리보기화면에서 표시할 이미지 경로 가져오기
	 * @param request
	 * @param response
	 * @return returnList
	 * @throws Exception
	 */
	@RequestMapping(value = "previewsrc/{bizSection}/{fileID}", method = RequestMethod.GET)
	public @ResponseBody String previewImageSrc(HttpServletResponse response, @PathVariable String bizSection, @PathVariable long fileID) throws Exception {
		CoviMap params = new CoviMap();
		String fileSrc = "";
		String companyCode = SessionHelper.getSession("DN_Code");
		//String backStorageURL = RedisDataUtil.getBaseConfig("BackStorage");
		try {
			params.put("FileID", fileID);
			CoviMap fileMap = fileSvc.selectOne(params);
			companyCode = fileMap.getString("CompanyCode").equals("") ? companyCode : fileMap.getString("CompanyCode");
			//String backStorage = RedisDataUtil.getBaseConfig("BackStorage").replace("{0}", companyCode);
			//backStorageURL = backStorageURL.replace("{0}", companyCode);
			String fileName = fileMap.getString("SavedName");
			fileName = fileName.replace("."+fileMap.getString("Extention"),"_thumb.jpg");
			fileSrc = fileMap.getString("StorageFilePath").replace("{0}", companyCode) + fileMap.getString("FilePath") + fileName;
			String filePath = FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1) + fileSrc;
			

	        // 파일을 읽어 스트림에 담기
		    File file = new File(FileUtil.checkTraversalCharacter(filePath));
		    
		    if(!file.exists()){
		    	//fileSrc = backStorageURL + "no_image.jpg";
		    	//fileSrc = fileSvc.getErrorImgURL("no_image.jpg", companyCode);
		    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    }
		} catch (NullPointerException e) {
			//return backStorageURL + "no_image.jpg";
			//fileSrc = fileSvc.getErrorImgURL("no_image.jpg", companyCode);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			//return backStorageURL + "no_image.jpg";
			//fileSrc = fileSvc.getErrorImgURL("no_image.jpg", companyCode);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		return fileSrc;
	}
	
}
