package egovframework.covision.coviflow.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * instead of ApplicationContextAware
 * @author hgsong
 */
public class CoviApplicationContextUtil {
    private static ApplicationContext context;
    
    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
 
    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
