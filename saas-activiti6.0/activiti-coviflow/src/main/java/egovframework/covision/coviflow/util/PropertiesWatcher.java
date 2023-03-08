package egovframework.covision.coviflow.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * /covi_propery Directory 의 설정파일 Monitoring 및 Reload 클래스. ( java.nio WatchService 사용 )
 * @author hgsong
 *
 */
public class PropertiesWatcher {
	private Logger LOGGER = LogManager.getLogger(PropertiesWatcher.class);

	private static final String deploy_path = System.getProperty("DEPLOY_PATH");
	
	// SingleTon
	private volatile static PropertiesWatcher uniqueInstance;
	public static PropertiesWatcher getInstance() {
	      if(uniqueInstance == null) {
	          synchronized(PropertiesWatcher.class) {
	             if(uniqueInstance == null) {
	                uniqueInstance = new PropertiesWatcher(); 
	             }
	          }
	       }
	       return uniqueInstance;
	}
	
	private PropertiesWatcher() {
		initialize();
	}
	
	WatchService watchService = null;
	Thread t = null;
	private void initialize() {
		try {
			if(!"Y".equals(CoviFlowPropHelper.getInstace().getPropertyValue("properties.reload.used"))) {
				return;
			}
			
			watchService = FileSystems.getDefault().newWatchService();
	        Path path = Paths.get(deploy_path + File.separator + "/covi_property");
	        if(!Files.exists(path)) {
	        	return;
	        }
	        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

	        LOGGER.info("file watch service(covi_property) registerd.");
	        t = new Thread(new Runnable() {
				@Override
				public void run() {
					WatchKey watchKey = null;
					while(true) {
		                try {
		                    watchKey = watchService.take(); // Block Wait
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                    break;
		                } catch (ClosedWatchServiceException e) {
		                	LOGGER.warn("ClosedWatchServiceException.");
		                	break;
		                }
		                if(watchKey == null) {
		                	continue;
		                }
		                List<WatchEvent<?>> events = watchKey.pollEvents();
		                for(WatchEvent<?> event : events) {
		                    Kind<?> kind = event.kind();
		                    Path paths = (Path)event.context();
		                    //System.out.println(paths.toAbsolutePath());//C:\...\...\test.txt
		                    if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
		                        String fileName = paths.getFileName().toString();
		                        switch (fileName) {
		                        case "activiti-admin.properties":
		                        case "activiti-app.properties":
//								case "activiti_db.properties":
//								case "db.properties":
//								case "engine.properties":
									LOGGER.info("[" + fileName + "] changed, reload properties.");
									CommonPropertiesUtil.reloadProperties(fileName.replace(".properties", ""));
									break;
								case "coviflow_config.properties":
									LOGGER.info("[" + fileName + "] changed, reload properties.");
									CoviFlowPropHelper.getInstace().reloadProperties();
									break;
								default:
									break;
								}
		                    }
		                }
		                
		                if(!watchKey.reset()) {
		                    try {
		                        watchService.close();
		                    } catch (IOException e) {
		                        LOGGER.warn("Error on watchService closing.", e);
		                        break;
		                    }
		                }
			        }
				}
				
			});
	        t.setName("covi_property properties File change watcher");
	        t.setDaemon(true);
	        t.start();
	        
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}finally {
			
		}
	}
	
	public void close() {
		if(watchService != null) {
			try {
				watchService.close();
				t.interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Component
	public static class ContextListener implements ApplicationListener<ApplicationEvent> {
		@Override
		public void onApplicationEvent(ApplicationEvent event) {
			if(event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent) {
				getInstance().close();
			}
			else if(event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
				getInstance();
			}
		}
	}
}
