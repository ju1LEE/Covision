package egovframework.covision.coviflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommonPropertiesUtil {
	private static Map<String, Properties> properties;
	
	private static String propBasePath = System.getProperty("DEPLOY_PATH")+"/covi_property/";
	public static Properties getProperties(String filename) {
		try {
			if (properties == null || properties.get(filename) == null) {		
				Properties p = new CommentedProperties();							
				File file = new File( propBasePath + filename + ".properties" );
				try(FileInputStream fis = new FileInputStream( file );
						InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
						){
					p.load(isr);
				}
				if(properties == null) {
					properties = new HashMap<String, Properties>();
				}
				properties.put(filename, p);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new Properties();
		}
		
		return properties.get(filename);
	}
	
	public static void reloadProperties(String filename) {
		if(properties != null) {
			properties.remove(filename);
		}
		getProperties(filename);
	}
	
	
	public static void store(Properties prop, String filename, String comment) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(propBasePath + filename + ".properties");
			prop.store(fos, comment);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
