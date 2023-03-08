package egovframework.covision.webhard.common.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.logging.LoggerHelper;
import egovframework.baseframework.util.DateHelper;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.util.FileUtil;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;

@Component("fileDownloadView")
public class FileDownloadView extends AbstractView {
	private static final Logger LOGGER = LogManager.getLogger(FileDownloadView.class);
			
	@Autowired
	private WebhardFileSvc webhardFileSvc;
	
	@Autowired
	private WebhardFolderSvc webhardFolderSvc;
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mode = model.get("mode").toString(); // user - 사용자, admin - 관리자, link - 링크
		String fileUuidStr = model.get("fileUuids").toString();
		String folderUuidStr = model.get("folderUuids").toString();
		
		String fileUuids[] = null;
		String folderUuids[] = null;
		String zipFileName = "";
		
		if(!fileUuidStr.equals("")) {
			fileUuids = fileUuidStr.split(";");
		}
		
		if(!folderUuidStr.equals("")) {
			folderUuids = folderUuidStr.split(";");
		}
		
		String downloadResult = "N";
		String failReason = "";
		
		if ((fileUuids != null && fileUuids.length > 1)
			|| (folderUuids != null && folderUuids.length > 0)) {
			zipFileName = "Webhard_" + getCurrentDate() + ".zip";
			
			ServletOutputStream sos = null;
			
			File zipFile = null;// ByteArrayOutputStream 은 Heap 을 사용하므로 FileStream 사용
			try {
				zipFile = makeZipFile(mode, folderUuids, fileUuids);
				downloadResult = "Y";
				
				String disposition = getDisposition(zipFileName, getBrowser(request));
				
				response.setCharacterEncoding("UTF-8");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Content-Disposition", disposition);
				response.setContentType("application/octet-stream;");
				response.setContentLength(new Long(zipFile.length()).intValue());
				
				sos = response.getOutputStream();
				
				// Buffer write
				try (InputStream fis = new FileInputStream(zipFile);){
					IOUtils.copy(fis, sos); // Default Buffer : 4,096 byte
					//sos.write(zipFile);
					sos.flush();
				}
			} catch(IOException e) {
				downloadResult = "N";
				failReason = e.getMessage();
			} catch(Exception e) {
				downloadResult = "N";
				failReason = e.getMessage();
			} finally {
				if (sos != null) { sos.close(); }
				
				// 파일 다운로드 로그
				LoggerHelper.filedownloadLogger("-1", "-1", "Webhard", zipFileName, downloadResult, failReason);
				
				// remove ziped file
				try {
					if(zipFile != null && zipFile.exists()) {
						if(!zipFile.delete()) {
							LOGGER.info("Fail to delete file");
						}
					}

				} catch(NullPointerException e) {
					LOGGER.error(e.getLocalizedMessage(), e);
				}catch(Exception e) {
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			}
		} else if(fileUuids.length == 1) {
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			
			String uuid = "";
			String orgFileName = "";
			
			try {
				uuid = fileUuids[0];
				
				request.setCharacterEncoding("UTF-8");
				
				CoviMap params = new CoviMap();
				
				params.put("mode", mode);
				params.put("fileUuid", uuid);
				
				if (StringUtil.isNotNull(mode) && mode.equalsIgnoreCase("user")) {
					params.put("userCode", SessionHelper.getSession("UR_ID"));
					params.put("groupCode", SessionHelper.getSession("DEPTID"));
					params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
				}
				
				CoviMap fileInfo = webhardFileSvc.getFileInfo(params);
				
				orgFileName = fileInfo.getString("fileName"); //다운로드시 변경될 파일명
				String savePath = fileInfo.getString("fileRealPath");
				
				File file = null;
				boolean skip = false;
				
				// 파일을 읽어 스트림에 담기
				try{
					file = new File(FileUtil.checkTraversalCharacter(savePath));
					
					fis = new FileInputStream(file);
				}catch(FileNotFoundException fe){
					skip = true;
				}

				// 파일 다운로드 헤더 지정
				
				String disposition = getDisposition(orgFileName, getBrowser(request));

				if(!skip){
					downloadResult = "Y";
					
					response.reset();
					response.setHeader("Content-Disposition", disposition);
					response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
					response.setHeader("Content-Length", file.length() + "");
					
					byte buff[] = new byte[2048];
					int bytesRead = -1;
					
					bis = new BufferedInputStream(fis);
					bos = new BufferedOutputStream(response.getOutputStream());
	
					//JSYun:Memory분산처리 시작 
					int bytesBuffered=0;
					while ((bytesRead = bis.read(buff)) != -1) {
						bos.write(buff, 0, bytesRead);
						bytesBuffered += bytesRead;
						if(bytesBuffered > 1024*1024)
						{
							bytesBuffered=0;
							bos.flush();
						}
					}
					//JSYun:Memory분산처리 종료
					bos.flush();
				}else{
					downloadResult = "N";
					failReason = "File not found";
					
					response.setContentType("text/html;charset=UTF-8");
					try(PrintWriter out = response.getWriter();){
						out.println("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
					}
					return;
				}
			} catch(IOException e) {
				downloadResult = "N";
				failReason = e.getMessage();
			} catch(Exception e) {
				downloadResult = "N";
				failReason = e.getMessage();
			} finally {
				if(fis != null){ fis.close(); }
				if(bis != null){ bis.close(); }
				if(bos != null){ bos.close(); }
				
				//파일 다운로드 로그
				LoggerHelper.filedownloadLogger("-1", uuid, "Webhard", orgFileName, downloadResult, failReason);
			}
		}
	}
	
	private File makeZipFile(String mode, String folderUuids[], String fileUuids[]) throws Exception {
		FileInputStream fis = null;
		File zipFile = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			String temporaryZipPath = FileUtil.getFrontPath() + File.separator + "webhardzip" + File.separator + DateHelper.getCurrentDay("yyyyMMddHH") + File.separator + UUID.randomUUID() + ".zip";
			zipFile = new File(temporaryZipPath);
			if(!zipFile.getParentFile().exists()) {
				if(!zipFile.getParentFile().mkdirs()) {
					LOGGER.info("Fail to make DIR["+ zipFile.getParentFile() +"]");
				}
			}
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			if (folderUuids != null && folderUuids.length > 0) {
				for (String folderUuid : folderUuids) {
					CoviMap params = new CoviMap();
					
					params.put("mode", mode);
					params.put("folderUuid", folderUuid);
					
					if (StringUtil.isNotNull(mode) && mode.equalsIgnoreCase("user")) {
						params.put("userCode", SessionHelper.getSession("UR_ID"));
						params.put("groupCode", SessionHelper.getSession("DEPTID"));
						params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
					}
					
					List<CoviMap> folderList = webhardFolderSvc.selectDownFolderList(params);
					List<CoviMap> fileList = webhardFileSvc.selectDownFileList(params);
					
					HashSet<String> check = new HashSet<String>();
					CoviMap dupMap = new CoviMap();
					
					// 폴더
					for (int i = 0; i < folderList.size(); i++) {
						CoviMap folderObj = folderList.get(i);
						String folderNamePath = folderObj.getString("folderNamePath").substring(1) + "/";
						
						zos.putNextEntry(new ZipEntry(folderNamePath));
						zos.closeEntry();
					}
					
					// 파일
					for (int i = 0; i < fileList.size(); i++) {
						CoviMap fileObj = fileList.get(i);
						CoviMap fileInfo = webhardFileSvc.getFileInfo(fileObj);
						
						String fileNamePath = fileObj.getString("fileNamePath");
						String filePath = fileInfo.getString("fileRealPath");
						
						File file = new File(FileUtil.checkTraversalCharacter(filePath));
						
						// 파일명 중복 체크
						if(!check.add(fileNamePath)){
							if(dupMap.get(fileNamePath) != null){
								int dupCnt = dupMap.getInt(fileNamePath) + 1;
								dupMap.put(fileNamePath, dupCnt);
								
								fileNamePath = fileNamePath.substring(0, fileNamePath.lastIndexOf(".")) + " (" + dupCnt + ")." + fileInfo.getString("fileType");
							}else{
								dupMap.put(fileNamePath, 1);
								fileNamePath = fileNamePath.substring(0, fileNamePath.lastIndexOf(".")) + " (1)." + fileInfo.getString("fileType");
							}
						}
						
						if (file.exists()) {
							fis = new FileInputStream(file);
							zos.putNextEntry(new ZipEntry(fileNamePath.substring(1)));
							
							IOUtils.copy(fis, zos);
							
							zos.closeEntry();
						} else {
							throw new Exception("File not found");
						}
					}
				}
			}
			
			if (fileUuids != null && fileUuids.length > 0) {
				HashSet<String> check = new HashSet<String>();
				CoviMap dupMap = new CoviMap();
				
				for (String fileUuid : fileUuids) {
					CoviMap params = new CoviMap();
					
					params.put("mode", mode);
					params.put("fileUuid", fileUuid);
					
					if (StringUtil.isNotNull(mode) && mode.equalsIgnoreCase("user")) {
						params.put("userCode", SessionHelper.getSession("UR_ID"));
						params.put("groupCode", SessionHelper.getSession("DEPTID"));
						params.put("groupPath", SessionHelper.getSession("GR_GroupPath"));
					}
					
					CoviMap fileInfo = webhardFileSvc.getFileInfo(params);
					
					String fileName = fileInfo.getString("fileName");
					String filePath = fileInfo.getString("fileRealPath");
					
					File file = new File(FileUtil.checkTraversalCharacter(filePath));
					
					// 파일명 중복 체크
					if(!check.add(fileName)){
						if(dupMap.get(fileName) != null){
							int dupCnt = dupMap.getInt(fileName) + 1;
							dupMap.put(fileName, dupCnt);
							
							fileName = fileName.substring(0, fileName.lastIndexOf(".")) + " (" + dupCnt + ")." + fileInfo.getString("fileType");
						}else{
							dupMap.put(fileName, 1);
							fileName = fileName.substring(0, fileName.lastIndexOf(".")) + " (1)." + fileInfo.getString("fileType");
						}
					}
					
					if (file.exists()) {
						fis = new FileInputStream(file);
						zos.putNextEntry(new ZipEntry(fileName));
						
						IOUtils.copy(fis, zos);
						
						zos.closeEntry();
					} else {
						throw new Exception("File not found");
					}
				}
			}
		} catch(IOException e) {
			throw new Exception(e);
		} catch(Exception e) {
			throw new Exception(e);
		} finally {
			if (fis != null) { fis.close(); }
			if (zos != null) { zos.close(); }
			if (fos != null) { fos.close(); }
		}
		
		return zipFile;
	}
	
	private String getCurrentDate() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDate = sdf.format(new Date());
		
		return currentDate;
	}
	
	private String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header.indexOf("Trident") > -1) {
			return "MSIE";
		} else if (header.indexOf("Chrome") > -1) {
			return "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			return "Opera";
		} else if (header.indexOf("iPhone") > -1 && header.indexOf("Mobile") > -1) {
			return "iPhone";
		} else if (header.indexOf("Android") > -1 && header.indexOf("Mobile") > -1) {
			return "Android";
		}
		
		return "Firefox";
	}
	
	private String getDisposition(String filename, String browser) throws Exception {
		String dispositionPrefix = "attachment; filename=";
		String encodedFilename = null;
		
		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("iPhone") || browser.equals("Android")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < filename.length(); i++) {
				char c = filename.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = "\"" + sb.toString()+ "\"";
		} else {
			throw new RuntimeException("Not supported browser");
		}
		
		return dispositionPrefix + encodedFilename;
	}
}
