package egovframework.covision.groupware.portal.user.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;




import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.baseframework.base.ThreadExecutorBean;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.AuthorityService;
import egovframework.coviframework.util.ACLHelper;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.groupware.portal.user.service.PortalSvc;
import egovframework.covision.groupware.portal.user.service.TemplateFileCacheSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("portalService")
public class PortalSvcImpl extends EgovAbstractServiceImpl implements PortalSvc{

	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;

	@Autowired
	private TemplateFileCacheSvc templateFileCacheSvc;

	@Autowired
	private AuthorityService authSvc;
	
	private Logger LOGGER = LogManager.getLogger(PortalSvcImpl.class);
	//ThreadPool 전역변수 사용방식 변경
	//클래스로 빼서 Spring Bean 등록하여 톰캣 종료시 shutdown 하는 방법으로 변경
	/*
	int poolSize = 2;
    int maxPoolSize = 2;
    long keepAliveTime = 10;
    final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5);
	
    ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
	*/
	
    private CoviMap sessionObj;
    //private CoviList aclArray;
	private final String jsPath = initJSFilePath();
	private final String htmlPath = initHTMLFilePath();
	private static final String osType = PropertiesUtil.getGlobalProperties().getProperty("Globals.OsType");
	
	// 포탈 로딩 관련 method 시작
	private static String initHTMLFilePath(){
		String ret;
		if(osType.equals("WINDOWS")){
			ret = PropertiesUtil.getGlobalProperties().getProperty("portalHTML.WINDOW.path");
		} else {
			ret = PropertiesUtil.getGlobalProperties().getProperty("portalHTML.UNIX.path");
		}
		return ret;
	}
	
	private static String initJSFilePath(){
		String ret;
		if(osType.equals("WINDOWS")){
			ret = PropertiesUtil.getGlobalProperties().getProperty("portalJS.WINDOW.path");
		} else {
			ret = PropertiesUtil.getGlobalProperties().getProperty("portalJS.UNIX.path");
		}
		return ret;
	}
	
	@Override
	public Object getWebpartData(final CoviList webPartArray, CoviMap userDataObj) throws Exception {
		return getWebpartData(webPartArray, userDataObj, "N");
	}
	
	@Override
	public Object getWebpartData(final CoviList webPartArray, CoviMap userDataObj, final String isMobile) throws Exception {
		//		sessionObj = SessionHelper.getSession(mobile);
		sessionObj = userDataObj;
		// aclArray = CoviList.fromObject(RedisDataUtil.getACL(sessionObj.getString("USERID"), sessionObj.getString("DN_ID"))); 
		CoviList retWebPartArr = new CoviList();
		Set<Future<Object>> set = new HashSet<Future<Object>>();

		for (final Object webPart: webPartArray) {
			Callable<Object> task = new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					CoviMap callRet = (CoviMap)webPart;
					String viewHtml = "";

					if(callRet.has("HtmlFilePath") && (!callRet.getString("HtmlFilePath").equals("")) ){
						viewHtml = new String(Base64.encodeBase64(
								templateFileCacheSvc.readAllText(sessionObj.getString("lang"), ((isMobile.equals("Y")) ? htmlPath.replace("/user", "") : htmlPath) + callRet.getString("HtmlFilePath"), "UTF8").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
					}

					String initMethod = new String(Base64.encodeBase64(callRet.getString("ScriptMethod").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
					callRet.put("viewHtml", viewHtml);
					callRet.put("initMethod", initMethod);

					//Server-SideRendering
					if(callRet.getString("WebpartOrder").indexOf('-')>-1){
						callRet.put("data", getWebpartQueryData(callRet));
					}

					return callRet;
				}
			};

			Future<Object> future = ThreadExecutorBean.getInstance().submit(task);
			set.add(future);
		}


		for (Future<Object> future : set) {
			CoviMap temp = new CoviMap();
			CoviMap futureObj = (CoviMap)future.get();
			temp.put("webPartID", futureObj.getString("WebpartID"));
			temp.put("jsModuleName", futureObj.getString("JsModuleName"));
			temp.put("webpartOrder", futureObj.getString("WebpartOrder"));
			temp.put("extentionJSON", futureObj.getString("ExtentionJSON"));
			temp.put("displayName", futureObj.getString("DisplayName"));
			temp.put("viewHtml", futureObj.getString("viewHtml"));
			temp.put("initMethod", futureObj.getString("initMethod"));
			if(futureObj.getString("WebpartOrder").indexOf('-')>-1){
				temp.put("data", futureObj.getString("data"));
			}
			retWebPartArr.add(temp);
			
			// net.sf.json 사용하지 않으므로 CBR 방식 -> clear 하면 안됨.
			// temp.clear();
			// futureObj.clear();
		}


		return retWebPartArr;
	}
	
	
	
	public CoviList getWebpartQueryData(CoviMap webpart) throws Exception{
		CoviList returnWebPartData = new CoviList();
		CoviList dataJson = webpart.getJSONArray("DataJSON");
		
		for(int i = 0 ; i < dataJson.size(); i++){
			try{
				CoviMap oWebpart = dataJson.getJSONObject(i);
				CoviMap param = new CoviMap();
				
				// 세션 값 받아오지 못하는 경우 오류 발생 => sessionObj 수정
				param.put("localCurrentDate", ComUtils.GetLocalCurrentDate(ComUtils.ServerDateFullFormat, 0, sessionObj.getString("UR_TimeZone"))); //timezone 적용 현재시간(now() 사용하지 못해 추가)
				
				CoviList xmlParamData = oWebpart.getJSONArray("paramData");
				for(int j = 0; j<xmlParamData.size();j++){
					CoviMap obj = xmlParamData.getJSONObject(j);
					String key = obj.getString("key");
					String value = getXmlParamData(obj.getString("value"), obj.getString("type"));
					
					param.put(key, value);
					
					if(value.indexOf(",") > -1 && value.split(",").length > 1) {
						param.put(key + "Arr", value.split(","));
					}
				}
				
				LOGGER.info("start: "+ oWebpart.getString("queryID"));
				CoviList list = coviMapperOne.list(oWebpart.getString("queryID"),param);
				LOGGER.info("end: "+ oWebpart.getString("queryID"));
				
				returnWebPartData.add(CoviSelectSet.coviSelectJSON(list,oWebpart.getString("resultKey")));
			} catch(NullPointerException e){
				LOGGER.error("PortalSvcImpl", e);
				returnWebPartData.add(new CoviList());
			} catch(Exception e){
				LOGGER.error("PortalSvcImpl", e);
				returnWebPartData.add(new CoviList());
			}
		}
		
		return returnWebPartData;
	}
	
	//xml Parameter 값을 실제 값으로 변경
	private String getXmlParamData(String value, String type){
		String result = "";
		try{
			if(value!=null){
				switch (type) {
				case "session":
					result = sessionObj.getString(value);
					break;
				case "fixed":
					result = value;
					break;
				case "config":
					result = RedisDataUtil.getBaseConfig(value, sessionObj.getString("DN_ID"));
					break;
				case "acl":  
					/**
					 * ACL 정보를 조회하여 IN 절로 생성
					 * Data Format : ObjectType|aclColName|aclValue 
					 * Ex). FD|View|V
					 */
					String[] aclConf = (value.equals("") ? "FD|View|V|Board".split("[|]") : value.split("[|]") ) ;
					
					String objectType = (aclConf.length > 0 ? aclConf[0] : "FD");
					// String aclColName = (aclConf.length > 1 ? aclConf[1] : "View");
					String aclValue = (aclConf.length > 2 ? aclConf[2] : "V");
					String serviceType = (aclConf.length > 3 ? aclConf[3] : "Board");
					
					Set<String> authorizedObjectCodeSet = ACLHelper.getACL(sessionObj, objectType, serviceType, aclValue);
			
					String[] objectArray = authorizedObjectCodeSet.toArray(new String[authorizedObjectCodeSet.size()]);
					
					if(objectArray.length > 0){
						result = "(" + ACLHelper.join(objectArray, ",") + ")";
					}
					
					break;
				default:
					result = "";
					break;
				}
			}
		} catch(NullPointerException e){
			LOGGER.error("PortalSvcImpl", e);
			result ="";
		} catch(Exception e){
			LOGGER.error("PortalSvcImpl", e);
			result ="";
		}
		
		return result;
	}

	@Override
	public String getLayoutTemplate(CoviList webPartList, CoviMap params) throws Exception {
		Pattern p = null;
		Matcher m = null;
		
		String portalTag = Objects.toString(coviMapperOne.selectOne("user.portal.selectPortalTag",params), "");
		
		String layoutHTML = new String(Base64.decodeBase64(portalTag), StandardCharsets.UTF_8);
		StringBuilder builder = new StringBuilder(layoutHTML);

		p = Pattern.compile("\\{\\{\\s*doc.layout.div(\\d+)\\s*\\}\\}");
		m = p.matcher(builder.toString());

		StringBuffer layoutResult = new StringBuffer(builder.length());
		
		while(m.find()){ 
			StringBuilder divHtml = new StringBuilder("");
			for(int i = 0;i<webPartList.size();i++){
				CoviMap webpart = webPartList.getJSONObject(i);
				
				if(webpart.getString("LayoutDivNumber").equals(m.group(1))){
					String webpartID = webpart.getString("WebpartID");
					String preview = new String(Base64.decodeBase64(webpart.getString("Preview")), StandardCharsets.UTF_8);
					int minHeight = webpart.getInt("MinHeight");
					
					// 2022.12.20 클라이언트 렌더링으로 웹파트 설정 시 웹파트 html에 center 태그로 감싸는 구문 사용하지 않도록 변경.
//					if(webpart.getString("WebpartOrder").indexOf('-')>-1){ //Server-Rendering
						divHtml.append(String.format("<div id=\"WP%s\" data-wepart-id=\"%s\" style=\"min-height:%dpx;\">%s</div>",webpartID, webpartID, minHeight, preview));
//					}else{  //Client-Rendering
//						divHtml.append(String.format("<div id=\"WP%s\" style=\"min-height:%dpx;\" ><center>%s</center></div>", webpartID, minHeight, preview));
//					}
				}
				
			}
			m.appendReplacement(layoutResult,divHtml.toString());
		}
		m.appendTail(layoutResult);
		
		return layoutResult.toString();
	}
	
	@Override
	public String getJavascriptString(String lang, CoviList webPartList) throws Exception {
		StringBuilder builder = new StringBuilder();
		CoviList jsFiles = new CoviList();
		String lineSeparator = System.getProperty("line.separator");
		
		for(int i = 0 ; i < webPartList.size(); i++){
			String jsFilePath = webPartList.getJSONObject(i).getString("JsFilePath");
			
			if(jsFilePath==null || jsFilePath.equals("") || jsFiles.indexOf(jsFilePath) > -1){
				continue;
			}
			
			String sCurrentJS = templateFileCacheSvc.readAllText(lang, jsPath+jsFilePath, "UTF8");
			builder.append(sCurrentJS+lineSeparator);
			
			jsFiles.add(jsFilePath);
		}
		
		return builder.toString();
	}

	@Override
	public String getPortalTheme(String portalID) throws Exception {
		String retTheme = ""; 
		
		CoviMap param = new CoviMap();
		param.put("portalID", portalID);
		
		retTheme = coviMapperOne.selectOne("user.portal.selectPortalTheme", param);
		
		retTheme = retTheme == null? "": retTheme;
		
		return retTheme;
	}

	@Override
	public CoviList getWebpartList(CoviMap params) throws Exception {
		CoviList webpartList  = new CoviList();
		CoviList list = coviMapperOne.list("user.portal.selectPortalWebpart", params);
		
		if(! list.isEmpty()){
			webpartList = CoviSelectSet.coviSelectJSON(list, "WebpartID,LayoutDivNumber,WebpartOrder,DisplayName,HtmlFilePath,JsFilePath,JsModuleName,Preview,Resource,ScriptMethod,MinHeight,DataJSON,ExtentionJSON");
		}
		
		return webpartList;
	}

	@Override
	public CoviMap getPortalInfo(CoviMap params) throws Exception {
		CoviMap portalObj = new CoviMap();
		CoviMap portalInfo = coviMapperOne.selectOne("user.portal.selectLayoutInfo", params);
		
		if(portalInfo == null){
			portalObj.put("URL", "");
			portalObj.put("IsDefault", "N");
			portalObj.put("LayoutType", "0");
		}else{
			portalObj = CoviSelectSet.coviSelectJSON(portalInfo, "URL,IsDefault,LayoutType,PortalType").getJSONObject(0);
		}

		portalObj.put("PortalID", params.getString("portalID"));
		
		return portalObj;
	}

	@Override
	public String getIncResource(CoviList webpartList) throws Exception {
		StringBuilder incResource = new StringBuilder();
		StringUtil func = new StringUtil();
		
		String lineSeparator = System.getProperty("line.separator");
		
		for(int i = 0 ; i < webpartList.size(); i++){
			String resource = webpartList.getJSONObject(i).getString("Resource");
			
			if(func.f_NullCheck(resource).equals("")){
				continue;
			}
			
			for(String ref : resource.split(";")){
				if(ref.lastIndexOf(".js")==ref.length()-3){
					incResource.append("<script type=\"text/javascript\" src=\""+ ref +"\"></script>");
				}else if(ref.lastIndexOf(".css")==ref.length()-4){
					incResource.append("<link type=\"text/css\" rel=\"stylesheet\" href=\""+ ref +"\"></script>");
				}
			}
		
			incResource.append(lineSeparator);
		}
		
		return incResource.toString();
	}
	
	@Override
	public String setInitPortal(Set<String> authorizedObjectCodeSet, String userID) throws Exception {
		String initPortalID = "0";
		
		//JSONArray aclArray = CoviList.fromObject(RedisDataUtil.getACL(SessionHelper.getSession("USERID"), SessionHelper.getSession("DN_ID")) ); 
		//Set<String> authorizedObjectCodeSet  = ACLHelper.queryObjectFromACL("PT", aclArray, "View", "V");	
		String[] aclPortalArr = authorizedObjectCodeSet.toArray(new String[authorizedObjectCodeSet.size()]);
		
		
		if(aclPortalArr.length > 0){
				
			CoviMap params = new CoviMap();
			params.put("aclPortalArr", aclPortalArr);
			
			initPortalID = coviMapperOne.getString("user.portal.selectDefaultInitPortalID", params);
			
			if(!initPortalID.equals("0")){
				params.clear();
				params.put("userCode", userID);
				params.put("initPortalID", initPortalID);
				coviMapperOne.update("user.portal.updateUserInitPortal", params);
			}
		}
		
		return initPortalID;
	}
	
	@Override
	public int updateInitPortal(CoviMap params) throws Exception {
		return (coviMapperOne.update("user.portal.updateUserInitPortal", params));
	}
	// 포탈 로딩 관련 method 끝

	
	
	// 마이컨텐츠 관련 method 시작
	@Override
	public CoviList getMyContentsWebpartList(CoviMap params) throws Exception {
		return coviMapperOne.list("user.portal.selectMyContentsWebpartList", params);
	}
	
	@Override
	public void saveMyContentsSetting(CoviMap params) throws Exception {
		coviMapperOne.delete("user.portal.deleteMyContentsSetting",params);
		if(params.containsKey("webpartArr")){
			coviMapperOne.insert("user.portal.insertMyContentsSetting", params);
		}
		
		int setCnt = (int) coviMapperOne.getNumber("user.portal.selectMyContentsCnt", params);
		
		if(setCnt >=1){
			 coviMapperOne.update("user.portal.updateMyContents", params);
		}else{
			coviMapperOne.insert("user.portal.insertMyContents", params);
		}
	}

	@Override
	public CoviList getMyContentSetWebpartList(CoviMap params) throws Exception {
		CoviList webpartList  = new CoviList();
		
		int setCnt = (int) coviMapperOne.getNumber("user.portal.selectMyContentsCnt", params);
		CoviList list  = new CoviList();
		
		if(setCnt >=1){
			 list = coviMapperOne.list("user.portal.selectMyContentSetWebpartList", params);
		}else{
			 list = coviMapperOne.list("user.portal.selectMyContentAllWebpartList", params);
		}
		
		
		if(! list.isEmpty()){
			webpartList = CoviSelectSet.coviSelectJSON(list, "WebpartID,HtmlFilePath,JsFilePath,,ScriptMethod,ExtentionJSON,BizSection,Preview");
		}
		
		for(int i = 0 ; i < webpartList.size(); i++){
			String viewHtml = ""; 
			CoviMap webpartObj = webpartList.getJSONObject(i);
			
			if(webpartObj.has("HtmlFilePath") && (!webpartObj.getString("HtmlFilePath").equals("")) ){
	    		viewHtml = new String(Base64.encodeBase64(templateFileCacheSvc.readAllText(sessionObj.getString("lang"),htmlPath+webpartObj.getString("HtmlFilePath"), "UTF8").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	    	}
        	String initMethod = new String(Base64.encodeBase64(webpartObj.getString("ScriptMethod").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        	webpartList.getJSONObject(i).put("extentionJSON", webpartObj.getString("ExtentionJSON"));
			webpartList.getJSONObject(i).put("viewHtml", viewHtml);
			webpartList.getJSONObject(i).put("initMethod", initMethod);
			webpartList.getJSONObject(i).put("preview", webpartObj.getString("Preview"));
		}
		
		return webpartList;
		
	}
	
	@Override
	public CoviList getMyContentWebpartList(CoviMap params) throws Exception {
		CoviList webpartList  = new CoviList();
		
		CoviList list = coviMapperOne.list("user.portal.selectMyContentWebpartList", params);
		
		if(! list.isEmpty()){
			webpartList = CoviSelectSet.coviSelectJSON(list, "WebpartID,HtmlFilePath,JsFilePath,,ScriptMethod,ExtentionJSON,Preview");
		}
		
		for(int i = 0 ; i < webpartList.size(); i++){
			String viewHtml = ""; 
			CoviMap webpartObj = webpartList.getJSONObject(i);
			
			if(webpartObj.has("HtmlFilePath") && (!webpartObj.getString("HtmlFilePath").equals("")) ){
	    		viewHtml = new String(Base64.encodeBase64(templateFileCacheSvc.readAllText(sessionObj.getString("lang"),htmlPath+webpartObj.getString("HtmlFilePath"), "UTF8").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	    	}
        	String initMethod = new String(Base64.encodeBase64(webpartObj.getString("ScriptMethod").getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        	webpartList.getJSONObject(i).put("extentionJSON", webpartObj.getString("ExtentionJSON"));
			webpartList.getJSONObject(i).put("viewHtml", viewHtml);
			webpartList.getJSONObject(i).put("initMethod", initMethod);
			webpartList.getJSONObject(i).put("preview", webpartObj.getString("Preview"));
		}
		
		return webpartList;
		
	}

	// 마이컨텐츠 관련 method 끝
	
	@Override
	public CoviMap getEmployeesNoticeList(CoviMap params) throws Exception {
		CoviMap retrunList  = new CoviMap();
		CoviList resultList  = new CoviList();
				
		params.put("localCurrentDate", ComUtils.GetLocalCurrentDate(ComUtils.ServerDateFullFormat, 0, sessionObj.getString("UR_TimeZone"))); //timezone 적용 현재시간(now() 사용하지 못해 추가)
		
		int setCnt = 0;
		CoviMap employrs = coviMapperOne.selectOne("webpart.board.selectEmployeesNoticeCount",params);
		if(!"".equals(employrs.get("Cnt").toString())) setCnt = Integer.parseInt(employrs.get("Cnt").toString());
				
		CoviList list  = new CoviList();
		list = coviMapperOne.list("webpart.board.selectEmployeesNotice", params);		
		resultList = CoviSelectSet.coviSelectJSON(list, "UserName,JobLevelName,JobTitleName,JobPositionName,MailAddress,PhotoPath,UserCode,Type,DateDiv,Date");
		
		retrunList.put("list", resultList);
		retrunList.put("Count", setCnt);
		
		return retrunList;
		
	}
	
}
