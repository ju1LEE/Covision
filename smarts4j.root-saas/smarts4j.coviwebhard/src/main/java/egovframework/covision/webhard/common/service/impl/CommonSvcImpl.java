package egovframework.covision.webhard.common.service.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.covision.webhard.common.service.CommonSvc;
import egovframework.covision.webhard.common.service.WebhardFileSvc;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;
import egovframework.covision.webhard.properties.ReloadWebhardPropertyHelper;


@Service("commonSvc")
public class CommonSvcImpl implements CommonSvc {
	
	private Logger LOGGER = LogManager.getLogger(CommonSvcImpl.class);
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Autowired
	private WebhardFolderSvc webhardFolderSvc;
	
	@Autowired
	private WebhardFileSvc webhardFileSvc;
	
	/**
	 * 기본 정보 조회
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getBaseInfo() throws Exception {
		CoviMap baseInfo = new CoviMap();		
		return getBaseInfo(baseInfo);
	}
	
	/**
	 * 기본 정보 조회
	 * @param params
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getBaseInfo(CoviMap params) throws Exception {
		CoviMap session = SessionHelper.getSession();
		Configuration props = ReloadWebhardPropertyHelper.getCompositeConfiguration();
		
		params.put("ownerId", StringUtil.isNotNull(params.get("ownerId")) ? params.get("ownerId").toString() : session.getString("USERID"));
		params.put("ownerName", StringUtil.isNotNull(params.get("ownerName")) ? params.get("ownerName").toString() : session.getString("UR_Name"));
		params.put("ownerType", StringUtil.isNotNull(params.get("ownerType")) ? params.get("ownerType").toString() : "U");
		params.put("boxName", StringUtil.isNotNull(params.get("boxName")) ? params.get("boxName").toString() : DicHelper.getDic("lbl_myDrive")); // 내 드라이브
		
		CoviMap domain = getOriginDomain();
		String domainCode = StringUtil.isNotNull(params.get("domainCode")) ? params.get("domainCode").toString() : domain.getString("DomainCode");
		
		params.put("domainId", StringUtil.isNotNull(params.get("domainId")) ? params.get("domainId").toString() : domain.getString("DomainID"));
		params.put("domainCode", domainCode);

		String rootPath = props.getString("webhard.WINDOW.path");
		
		if (System.getProperty("os.name").indexOf("Windows") == -1) rootPath = props.getString("webhard.UNIX.path");
		
		String domainPath = rootPath + "/" + domainCode;
		String homePath = domainPath + props.getString("webhard.home.path");
		
		// 웹하드 루트 경로
		params.put("rootPath", rootPath);

		// 사용자 도메인 경로
		params.put("domainPath", domainPath);
		
		// 웹하드 홈 경로
		params.put("homePath", homePath);
		
		return params;
	}
	
	/**
	 * 사용자 도메인 가져오기(JobType = Origin)
	 * @return CoviMap
	 * @throws Exception
	 */
	@Override
	public CoviMap getOriginDomain() throws Exception {
		CoviMap params = new CoviMap();
		params.put("userCode", SessionHelper.getSession("USERID"));
		
		CoviMap resultMap = coviMapperOne.select("webhardCommon.box.selectOriginDomain", params);
		
		return resultMap;
	}
	
	/**
	 * UUID 생성
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String generateUuid() throws Exception {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 파일 복사
	 * @param sourceFile 복사하려는 파일
	 * @param targetFile 복사될 위치의 파일
	 * @return <code>true</code>: 복사 성공, <code>false</code>: 복사 실패
	 * @throws Exception
	 */
	@Override
	public boolean copy(File sourceFile, File targetFile) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		
		long size = 0;
		
		try {
			inputStream = new FileInputStream(sourceFile);
			outputStream = new FileOutputStream(targetFile);
			
			inChannel =  inputStream.getChannel();
			outChannel = outputStream.getChannel();
			
			size = inChannel.size();
	
			inChannel.transferTo(0, size, outChannel);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		} finally {
			if (outChannel != null) try { outChannel.close(); }catch(IOException ioe){LOGGER.error(ioe.getLocalizedMessage(), ioe);}
			if (inChannel != null) try { inChannel.close(); }catch(IOException ioe){LOGGER.error(ioe.getLocalizedMessage(), ioe);}
			if (outputStream != null) try { outputStream.close(); }catch(IOException ioe){LOGGER.error(ioe.getLocalizedMessage(), ioe);}
			if (inputStream != null) try { inputStream.close(); }catch(IOException ioe){LOGGER.error(ioe.getLocalizedMessage(), ioe);}
		}
		
		return true;
	}
	
	/**
	 * 파일/폴더 전체 복사
	 * @param sourceFolder 복사하려는 폴더
	 * @param targetFolder 복사될 위치의 폴더
	 * @param params
	 * @return <code>true</code>: 복사 성공, <code>false</code>: 복사 실패
	 * @throws Exception
	 */
	@Override
	public boolean copyAll(File sourceFolder, File targetFolder, CoviMap params) {
		File[] srcfileList = sourceFolder.listFiles();
		File trgtFile = null;
		
		try {
			if (srcfileList != null && srcfileList.length > 0) {
				for (File srcFile : srcfileList) {
					CoviMap boxInfo = getBaseInfo();
					CoviMap fParams = params;
					String genID = generateUuid();
					
					trgtFile = new File(targetFolder.getAbsolutePath() + File.separator + genID);
					
					String fPath = trgtFile.getPath().replaceAll("\\\\", "/").replaceAll(boxInfo.getString("homePath"), "").replaceAll("/" + params.getString("boxUuid"), "");
					String fPathArr[] = fPath.split("/");
					
					if (srcFile.isDirectory()) {
						if (!trgtFile.exists() && trgtFile.mkdirs()) {
							LOGGER.info("path : " + trgtFile + " mkdirs();");
						}
						
						int folderLevel = fPathArr.length - 1;
						
						CoviMap checkParams = new CoviMap();
						
						checkParams.put("folderUuid", srcFile.getName());
						fParams.put("folderUuid", srcFile.getName());
						
						CoviMap folderInfo = webhardFolderSvc.getFolderInfo(checkParams);
						
						// 폴더 정보가 없거나 삭제된 폴더인 경우에 복사하지 않음
						if (folderInfo != null && StringUtil.isNull(folderInfo.getString("deletedDate"))) {
							fParams.put("newFolderUuid", genID);
							fParams.put("parentUuid", folderInfo.getString("parentUuid"));
							fParams.put("folderLevel", folderLevel);
							fParams.put("folderName", folderInfo.getString("folderName"));
							fParams.put("folderPath", fPath);
							fParams.put("folderNamePath", params.getString("targetFNamePath") + folderInfo.getString("folderNamePath").substring(params.getString("changeFNamePath").length(), folderInfo.getString("folderNamePath").length()));
							fParams.put("bookmark", "N");
							fParams.put("folderGrant", folderInfo.getString("folderGrant"));
							
							if (fPathArr.length > 2) {
								fParams.put("parentUuid", fPathArr[(fPathArr.length - 2)]);
							}
							
							coviMapperOne.insert("webhardCommon.folder.insertCopyFolder", fParams);
						}
						
						copyAll(srcFile, trgtFile, params);
					} else {
						String folderPath = trgtFile.getAbsolutePath().substring(0, trgtFile.getAbsolutePath().lastIndexOf(File.separator));
						File folder = new File(folderPath);
						
						fParams.put("fileUuid", srcFile.getName());
						CoviMap fileInfo = webhardFileSvc.getFileInfo(fParams);
						
						// 파일 정보가 없거나 삭제된 파일인 경우에 복사하지 않음
						if (fileInfo != null && StringUtil.isNull(fileInfo.getString("deletedDate"))) {
							if (!folder.exists() && folder.mkdirs()) {
								LOGGER.info("path : " + folder + " mkdirs();");
							}
							
							fParams.put("newFileUUid", genID);
							fParams.put("fileName", fileInfo.getString("fileName"));
							fParams.put("fileType", fileInfo.getString("fileType"));
							fParams.put("fileContentType", fileInfo.getString("fileContentType"));
							fParams.put("fileSize", fileInfo.getInt("fileSize"));
							fParams.put("filePath", fPath);
							fParams.put("fileGrant", fileInfo.getString("fileGrant"));
							fParams.put("fileQuickUrl", fileInfo.getString("fileQuickUrl"));
							fParams.put("bookmark", "N");
							
							if (fPathArr.length > 2) {
								fParams.put("folderUuid", fPathArr[(fPathArr.length - 2)]);
							}
							
							coviMapperOne.insert("webhardCommon.file.insertCopyFile", fParams);
						}
						
						copy(srcFile, trgtFile);
					}
				}
			} else { // 빈 폴더 일 때
				if (!targetFolder.exists() && targetFolder.mkdirs()) {
					LOGGER.info("path : " + targetFolder + " mkdirs();");
				}
				
				return true;
			}
		} catch(IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 파일/폴더 삭제
	 * @param targetFolder 삭제하려는 폴더
	 * @return <code>true</code>: 삭제 성공, <code>false</code>: 삭제 실패
	 * @throws Exception
	 */
	@Override
	public boolean delete(File targetFolder) throws Exception {
		File[] trgtFiles = targetFolder.listFiles();
		
		try {
			if(trgtFiles != null) {
				int filesLength = trgtFiles.length;
				for (int i = 0; i < filesLength; i ++) {
					if (trgtFiles[i].isFile()) {
						if(trgtFiles[i].delete()) {
							LOGGER.info("file : " + trgtFiles[i].toString() + " delete();");
						}
						continue;
					}
					
					if (trgtFiles[i].isDirectory()) {
						deleteAll(trgtFiles[i]);
					}
					
					if(trgtFiles[i].delete()) {
						LOGGER.info("file : " + trgtFiles[i].toString() + " delete();");
					}
				}
			}
		} catch(IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 파일/폴더 전체 삭제
	 * @param targetFolder 삭제하려는 폴더
	 * @return <code>true</code>: 삭제 성공, <code>false</code>: 삭제 실패
	 * @throws Exception
	 */
	@Override
	public boolean deleteAll(File targetFolder) throws Exception {
		try {
			delete(targetFolder);
			
			if (targetFolder.delete()) {
				LOGGER.info("file : " + targetFolder.toString() + " delete();");
			}
		} catch(IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;			
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 파일 생성
	 * @param mf 파일
	 * @param file 저장 위치
	 * @param ext 확장자
	 * @throws Exception
	 */
	@Override
	public void transferTo(MultipartFile mf, File file, String ext) throws Exception {
		try {
			int orientation = getOrientation(mf);
			
			if (orientation != -1) {
				BufferedImage bfImg = rotateImageForExif(mf, orientation);
				
				if (bfImg != null) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(bfImg, ext, os);
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					
					mf.transferTo(file);
					FileUtils.copyInputStreamToFile(is, file);
				}
			} else {
				mf.transferTo(file);
			}
		} catch(IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);		
		} catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} 
	}
	
	private int getOrientation(MultipartFile mf) throws Exception {
		try(InputStream is = new BufferedInputStream(mf.getInputStream())){
		
			return getOrientation(is);
		}
	}
	
	private int getOrientation(InputStream is) throws Exception {
		int orientation = -1;

		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			Metadata metadata = ImageMetadataReader.readMetadata(bis, true);
			Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
			
			if (directory != null) {
				int tagType = ExifIFD0Directory.TAG_ORIENTATION;
				Integer integer = directory.getInteger(tagType);
				
				if (integer != null) {
					orientation = directory.getInt(tagType);
				}
			}
		} catch (IOException e) {
			orientation = -1;
		} catch (Exception e) {
			orientation = -1;
		}

		return orientation;
	}
	
	private BufferedImage rotateImageForExif(MultipartFile mf, int orientation) throws Exception {
		try(InputStream is = new BufferedInputStream(mf.getInputStream())){
			BufferedImage bi = ImageIO.read(is);
			
			return rotateImageForExif(bi, orientation);
		}
	}

	private BufferedImage rotateImageForExif(BufferedImage bi, int orientation) throws Exception {
		if (orientation == 6) { // 정위치
			return rotateImage(bi, 90);
	    } else if (orientation == 1) { // 왼쪽으로 누웠을 때 
	       return bi;
	    } else if (orientation == 3) { // 오른쪽으로 누웠을 때
	       return rotateImage(bi, 180);
	    } else if (orientation == 8) { // 180도
	       return rotateImage(bi, 270);      
	    } else {
	       return bi;
	    }
	}
	
	private BufferedImage rotateImage(BufferedImage orgImage, int radians) {
		BufferedImage newImage = null;
		
		if (radians==90 || radians==270) {
			newImage = new BufferedImage(orgImage.getHeight(),orgImage.getWidth(),orgImage.getType());
	    } else if (radians==180){
	    	newImage = new BufferedImage(orgImage.getWidth(),orgImage.getHeight(),orgImage.getType());
	    } else{
	    	return orgImage;
	    }
		
		Graphics2D graphics = (Graphics2D) newImage.getGraphics();
	    graphics.rotate(Math.toRadians(radians), newImage.getWidth() / 2, newImage.getHeight() / 2);
	    graphics.translate((newImage.getWidth() - orgImage.getWidth()) / 2, (newImage.getHeight() - orgImage.getHeight()) / 2);
	    graphics.drawImage(orgImage, 0, 0, orgImage.getWidth(), orgImage.getHeight(), null);
	    
	    return newImage;
	}
	
	/**
	 * 파일 전체 크기 조회
	 * @param file 크기를 확인하려는 파일
	 * @return long 파일 크기
	 * @throws Exception
	 */
	@Override
	public long getTotalSize(File file) throws Exception {
		long totalSize = 0;
		
	    File[] srcfileList = file.listFiles();
	    
	    if(srcfileList != null) {
	    	for (File srcFile : srcfileList) {
	    		if (srcFile.isDirectory()) {
	    			totalSize += getTotalSize(srcFile);
	    		} else {
	    			totalSize += srcFile.length();
	    		}
	    	}
	    }
		
		return totalSize;
	}
	
	/**
	 * 파일 업로드
	 * @param mf 업로드 하는 파일
	 * @param file 업로드 위치
	 * @return <code>true</code>: 업로드 성공, <code>false</code>: 업로드 실패
	 */
	@Override
	public boolean fileUpload(MultipartFile mf, File file) {
		try {
			mf.transferTo(file);
		}catch(IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		}catch(Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return false;
		}
		
		return true;
	}
	
}
