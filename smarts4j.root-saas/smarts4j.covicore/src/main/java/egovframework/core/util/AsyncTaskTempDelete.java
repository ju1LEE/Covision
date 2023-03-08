package egovframework.core.util;


import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import egovframework.coviframework.util.s3.AwsS3;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.DateHelper;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.util.FileUtil;

/**
 * AsyncTaskTempDelete *
 */
@Service("asyncTaskTempDelete")
public class AsyncTaskTempDelete{
	
	private Logger log = LogManager.getLogger(AsyncTaskTempDelete.class);
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;

	private int BACKUP_AFTER_DAYS = -1; 
	private int DELETE_AFTER_DAYS = -1; 
	private String DELETE_FILTER_EXT = null; 
	private String DELETE_FILTER_FILENAME = null; 
	
	File backupDir = null;
	File frontDir = null;
	AwsS3 awsS3 = AwsS3.getInstance();
	StringBuilder moveLogBuf = new StringBuilder();
	StringBuilder deleteLogBuf = new StringBuilder();
	@Async("executorTempDelete")
	public void execute() throws Exception{
		try {
			long start = System.currentTimeMillis();
			long elapsed = 0L;
			
			String backupDays = PropertiesUtil.getExtensionProperties().getProperty("frontstorage.auto.delete.backup.days");
			String deleteDays = PropertiesUtil.getExtensionProperties().getProperty("frontstorage.auto.delete.delete.days");
				
			BACKUP_AFTER_DAYS = (backupDays != null && !"".equals(backupDays))? Integer.parseInt(backupDays) : -1;
			DELETE_AFTER_DAYS = (deleteDays != null && !"".equals(deleteDays))? Integer.parseInt(deleteDays) : -1;
			
			DELETE_FILTER_EXT = PropertiesUtil.getExtensionProperties().getProperty("frontstorage.auto.delete.filter.ext");
			DELETE_FILTER_FILENAME = PropertiesUtil.getExtensionProperties().getProperty("frontstorage.auto.delete.filter.filename");

			String frontPath = FileUtil.getFrontPath();
			String frontBackupPath = frontPath + (frontPath.endsWith(File.separator) ? "" : File.separator) + "autodelete";
			if(awsS3.getS3Active()) {
				// TODO
				
				return;
			}
			
			frontDir = new File(frontPath);
			if (!frontDir.exists()) {
				throw new FileNotFoundException(frontDir.getAbsolutePath());
			}

			backupDir = new File(frontBackupPath);
			if (!backupDir.exists()) {
				if(!backupDir.mkdirs()) {
					log.debug("Failed to make directories.");
				}
			}
			// 1. delete files from backup directory(after 14days)
			if(BACKUP_AFTER_DAYS > -1) {
				moveLogBuf.append("[ " + new java.util.Date() + " ] Moved file logs. \n\n");
				moveToBackup(frontDir);
			}
			elapsed = System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
			log.info("Frontstorage temporary file move to backup complete. elapsed " + elapsed + "ms");
			
			// 2. move files from front path. (after 3days) 
			if(DELETE_AFTER_DAYS > -1) {
				deleteLogBuf.append("[ " + new java.util.Date() + " ] Delete file logs. \n\n");
				deleteFromBackup(backupDir);
			}
			elapsed = System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
			log.info("Frontstorage temporary file delete from backup Directory complete. elapsed " + elapsed + "ms");
		} 
		catch (NullPointerException ne) {
			log.error(ne.getLocalizedMessage(), ne);
		}
		catch (Exception ex) {
			log.error(ex.getLocalizedMessage(), ex);
		} 
		finally {
			try {
				String format = DateHelper.getCurrentDay("yyyy-MM-dd-HHmmss");
				Files.write( new File(frontDir, format + ".log").toPath(), moveLogBuf.toString().getBytes("UTF-8"));
				Files.write( new File(backupDir, format + ".log").toPath(), deleteLogBuf.toString().getBytes("UTF-8"));
			}
			catch(IOException ie) {
				log.error("[IOException] " + ie.getLocalizedMessage(), ie);
			}
			catch(Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	
	private boolean moveToBackup(File file) throws IOException {
		if(file != null) {
			if(file.isFile()) {
				int backupDays = BACKUP_AFTER_DAYS;
				long modified = file.lastModified();
				long now = System.currentTimeMillis();
				long offsetTime = 1L * backupDays * 24 * 60 * 60 * 1000;
				if(modified < ( now - offsetTime )) {
					String frontPath = frontDir.toPath().toString();
					String relativePath = file.toPath().toString().replace(frontPath, "");
					
					Path targetPath = new File(backupDir.toPath().toString() + relativePath).toPath();
					try {
						Files.createDirectories(targetPath);
					} catch (FileAlreadyExistsException e) {
						log.debug(e);
					}
					Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
					moveLogBuf.append("[Moved] " + file.toPath() + " to " + targetPath + "\n");
				}
			}else {
				if(!file.equals(backupDir)) {
					File[] files = file.listFiles(new FileFilter() {
						@Override
						public boolean accept(File check) {
							return !isFilterFile(check);
						}
					});
					if(files != null) {
						for(File f : files) {
							// recursive call.
							moveToBackup(f);
						}
					}
					
					// 빈폴더 삭제
					if(!file.equals(frontDir) && file.list() != null && file.list().length == 0) {
						if(!file.delete()) {
							log.debug("Failed to delete directories.");
						}
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean deleteFromBackup(File file) {
		if(file != null) {
			if(file.isFile()) {
				int backupDays = DELETE_AFTER_DAYS;
				long modified = file.lastModified();
				long now = System.currentTimeMillis();
				long offsetTime = 1L * backupDays * 24 * 60 * 60 * 1000;
				if(modified < ( now - offsetTime )) {
					if(file.delete()) {
						log.debug("Failed to delete file.");
					}
					deleteLogBuf.append("[Deleted] " + file.toPath() + "( "+ new java.util.Date(modified) +" )" + "\n");
				}
			}else {
				File[] files = file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File check) {
						return !isFilterFile(check);
					}
				});
				if(files != null) {
					for(File f : files) {
						// recursive call.
						deleteFromBackup(f);
					}
				}
				
				// 빈폴더 삭제
				if(!file.equals(backupDir) && file.list() != null && file.list().length == 0) {
					if(!file.delete()) {
						log.debug("Failed to delete directories.");
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean isFilterFile(File file) {
		// Check file to ignore.
		String ext = FilenameUtils.getExtension(file.getName());
		String filename = file.getName();
		
		if(DELETE_FILTER_EXT != null && !StringUtil.isEmpty(DELETE_FILTER_EXT)) {
			String [] excludeFileExt = StringUtils.split(DELETE_FILTER_EXT, ",");
			List<String> excludeFileExtList = Arrays.asList(excludeFileExt);
			if(excludeFileExtList.contains(ext)) {
				return true;
			}
		}
		
		if(DELETE_FILTER_FILENAME != null && !StringUtil.isEmpty(DELETE_FILTER_FILENAME)) {
			String [] excludeFileName = StringUtils.split(DELETE_FILTER_FILENAME, ",");
			if(excludeFileName != null) {
				for(String nameChk : excludeFileName) {
					if(filename.indexOf(nameChk) > -1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
}