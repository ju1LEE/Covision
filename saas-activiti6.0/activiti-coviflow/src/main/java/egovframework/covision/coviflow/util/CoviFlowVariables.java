/**
 * @Class Name : CoviFlowVariables.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.activiti.engine.delegate.VariableScope;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CoviFlowVariables {
    
	public static final HashMap<String, String> APPV_COMP = createCompMap();
    private static HashMap<String, String> createCompMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "completed");
        result.put("result", "completed");
        return result;
    }
    
    public static final HashMap<String, String> APPV_PENDING = createPendingMap();
    private static HashMap<String, String> createPendingMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "pending");
        result.put("result", "pending");
        return result;
    }
    
    public static final HashMap<String, String> APPV_INACTIVE = createInActiveMap();
    private static HashMap<String, String> createInActiveMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "inactive");
        result.put("result", "inactive");
        return result;
    }
    
    public static final HashMap<String, String> APPV_REJECT = createRejectMap();
    private static HashMap<String, String> createRejectMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "rejected");
        result.put("result", "rejected");
        return result;
    }
    
    public static final HashMap<String, String> APPV_REJECTTODEPT = createRejectToDeptMap();
    private static HashMap<String, String> createRejectToDeptMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "rejectedtodept");
        result.put("result", "rejectedtodept");
        return result;
    }
    
    public static final HashMap<String, String> APPV_AGREE = createAgreeMap();
    private static HashMap<String, String> createAgreeMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "completed");
        result.put("result", "agreed");
        return result;
    }
    
    public static final HashMap<String, String> APPV_DISAGREE = createDisAgreeMap();
    private static HashMap<String, String> createDisAgreeMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "rejected");
        result.put("result", "disagreed");
        return result;
    }
    
    public static final HashMap<String, String> APPV_REVIEW = createReviewMap();
    private static HashMap<String, String> createReviewMap() {
    	HashMap<String, String> result = new HashMap<String, String>();
        result.put("status", "completed");
        result.put("result", "reviewed");
        return result;
    }
	
    /**
     * 
     * @param scope
     * @return
     */
    public static JSONObject getGlobalContext(VariableScope scope) {
    	JSONObject ctxJsonObj = null;
		try {
			Object ctxObj = scope.getVariable("g_context"); // DelegateTask, DelegateExecution
			if(ctxObj instanceof LinkedHashMap){
				ctxJsonObj = (JSONObject)JSONValue.parse(JSONValue.toJSONString(ctxObj));
			} else {
				JSONParser parser = new JSONParser();
				ctxJsonObj = (JSONObject)parser.parse(ctxObj.toString());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ctxJsonObj;
    }
    
    public static String getGlobalContextStr(VariableScope scope) {
    	String ctx = null;
		Object ctxObj = scope.getVariable("g_context"); // DelegateTask, DelegateExecution
		if(ctxObj instanceof LinkedHashMap){
			ctx = JSONValue.toJSONString(ctxObj);
		} else if(ctxObj instanceof String){
			ctx = (String)ctxObj;
		}
    	return ctx;
    }
}
