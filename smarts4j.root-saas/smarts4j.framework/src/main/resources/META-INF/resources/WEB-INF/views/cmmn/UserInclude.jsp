<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@page import="egovframework.baseframework.util.SessionHelper"%>
<%@page import="egovframework.baseframework.util.RedisDataUtil"%>
<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@page import="egovframework.coviframework.util.FileUtil"%>
<%@page import="egovframework.baseframework.data.CoviMap"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<% String approvalAppPath = PropertiesUtil.getGlobalProperties().getProperty("approval.app.path"); %>
<% String appPath = PropertiesUtil.getGlobalProperties().getProperty("app.path"); %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>
<%
	org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(this);
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
	pageContext.setAttribute("isUseMail", PropertiesUtil.getExtensionProperties().getProperty("isUse.mail"));
	pageContext.setAttribute("isUseAccount", PropertiesUtil.getExtensionProperties().getProperty("isUse.account"));
	pageContext.setAttribute("isUseProjectmng", PropertiesUtil.getExtensionProperties().getProperty("isUse.projectmng"));
	pageContext.setAttribute("isUseWebhard", PropertiesUtil.getExtensionProperties().getProperty("isUse.webhard"));
	pageContext.setAttribute("isUseBizMnt", PropertiesUtil.getExtensionProperties().getProperty("isUse.bizMnt"));
	pageContext.setAttribute("isUseHrManage", PropertiesUtil.getExtensionProperties().getProperty("isUse.hrmanage"));
	pageContext.setAttribute("themeType", SessionHelper.getSession("UR_ThemeType"));
	pageContext.setAttribute("themeCode", SessionHelper.getSession("UR_ThemeCode"));
	pageContext.setAttribute("LanguageCode", SessionHelper.getSession("LanguageCode"));		
	//out.println("<script>var PARAM_VALID='"+  egovframework.baseframework.util.StringUtil.replaceNull(PropertiesUtil.getSecurityProperties().getProperty("param.valid"),"N")+"';</script>");
	out.println("<script>");
	out.println("var PARAM_VALID='"+  egovframework.baseframework.util.StringUtil.replaceNull(PropertiesUtil.getSecurityProperties().getProperty("param.valid"),"N")+"';");
	try {
		out.println("");
		out.println("var resourceVersion='" + resourceVersion + "';");
		
		String userAgent = request.getHeader("User-Agent");
		String pIsTeamsAddIn = request.getParameter("teamsaddin");
	    if ((userAgent != null && userAgent.toLowerCase().indexOf("teams") > -1) || (pIsTeamsAddIn != null && pIsTeamsAddIn.toUpperCase().equals("Y"))) {
	    	// Teams Add-In 구분용 변수
	    	out.println("var _IsTeamsAddIn=true;");
	    	
	    	// Teams Add-In 속도 개선을 위한 SessionData 처리
	    	out.println("try {");
	    	out.println("function setTeamsSessionData(sessionKey, sessionValue) {");
	    	out.println("var _TeamIsLocalStorage = true;");
	    	out.println("try {");
	    	out.println("sessionStorage.setItem('test', 'test');");
	    	out.println("sessionStorage.removeItem('test');");
	    	out.println("} catch(e) {");
	    	out.println("_TeamIsLocalStorage = false;");
	    	out.println("}");
	    	out.println("if (_TeamIsLocalStorage) {");
	    	out.println("sessionStorage.removeItem(sessionKey); var sessionStoragedItem = { _ : new Date().getTime(), data : sessionValue }; sessionStorage.setItem(sessionKey, JSON.stringify(sessionStoragedItem));");
	    	out.println("} else {");
	    	out.println("localCache.remove(sessionKey); localCache.data[sessionKey] = { _ : new Date().getTime(), data : value };");
	    	out.println("}");
	    	out.println("}");
	    	CoviMap sessionObj = SessionHelper.getSession();
	    	java.util.Iterator arrKeys = sessionObj.keys();
	    	out.println("setTeamsSessionData(\"SESSION_all\", $.parseJSON(\"" + sessionObj.toJSONString().replace("\"", "\\\"") + "\"));");
			while (arrKeys.hasNext()) {
		        String sessionKey = arrKeys.next().toString();

		    	out.println("setTeamsSessionData(\"SESSION_" + sessionKey + "\", \"" + sessionObj.getString(sessionKey) + "\");");
			}
	    	out.println("} catch(ee) { }");
	    } else {
	    	out.println("var _IsTeamsAddIn=false;");
	    }
	} catch (NullPointerException e) {
		LOGGER.debug("teams error");
	} catch (Exception e) {
		LOGGER.debug("teams error");
	}
    
    out.println("</script>");
%>

<link rel="stylesheet" type="text/css" href="/covicore/resources/css/font-awesome-4.7.0/css/font-awesome.min.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXJ.min.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXGrid.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXTree.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/jquery.mCustomScrollbar.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/approval/resources/css/approval.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision.control.css<%=resourceVersion%>" />	
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/common.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/user_common_controls.css<%=resourceVersion%>" />  
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/Controls.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/survey/resources/css/survey.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/schedule/resources/css/schedule.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/resource/resources/css/resource.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/board/resources/css/board.css<%=resourceVersion%>" />	
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/doc/resources/css/doc.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/bizcard/resources/css/bizcard.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/community/resources/css/community.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/task/resources/css/task.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/myInfo/resources/css/myInfo.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/extension/resources/css/extension.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/VacationManager/resources/css/VacationManager.css<%=resourceVersion%>" />	
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/jquery-ui-1.12.1.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/palette-color-picker.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/slick.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/AttendanceManagement/resources/css/AttendanceManagement.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/AttendanceManagement/resources/css/AttendanceMgt.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/IntegratedTaskManagement/resources/css/IntegratedTaskManagement.css<%=resourceVersion%>">
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/collaboration/resources/css/collaboration.css<%=resourceVersion%>">

	
<c:if test="${isUseMail eq 'Y'}"><link rel="stylesheet" type="text/css" href="<%=cssPath%>/mail/resources/css/mail.css<%=resourceVersion%>" /></c:if>
<c:if test="${isUseAccount eq 'Y'}"><link rel="stylesheet" type="text/css" href="<%=cssPath%>/eaccounting/resources/css/eaccounting.css<%=resourceVersion%>"></c:if>
<c:if test="${isUseProjectmng eq 'Y'}"><link rel="stylesheet" type="text/css" href="<%=cssPath%>/project/resources/css/Project_m.css<%=resourceVersion%>"></c:if>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/webhard/resources/css/webhard.css<%=resourceVersion%>" />
<c:if test="${isUseBizMnt eq 'Y'}"><link rel="stylesheet" type="text/css" href="<%=cssPath%>/BusinessManagement/resources/css/BusinessManagement.css<%=resourceVersion%>" /></c:if>
<c:if test="${isUseHrManage eq 'Y'}"><link rel="stylesheet" type="text/css" href="<%=cssPath%>/personnel/resources/css/personnel.css<%=resourceVersion%>"></c:if>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/public/resources/css/public.css<%=resourceVersion%>" />

<!-- AppStore -->
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/appstore/resources/css/appstore.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/contentsApp/resources/css/contentsApp.css<%=resourceVersion%>" />

<!-- 간편관리자 추가 -->
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/simpleAdmin/resources/css/simpleAdmin.css">
<link rel="stylesheet" id="themeCSS" type="text/css" href="<%=cssPath%>/covicore/resources/css/theme/<c:out value="${themeType}"/>.css<%=resourceVersion%>" />

<c:if test="${themeCode != 'default'}">
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/customizing/<c:out value="${themeCode}"/>/css/project.css<%=resourceVersion%>" />
	<c:choose>
		<c:when test="${themeType == 'blue'}">
			<link rel="stylesheet" type="text/css" href="<%=cssPath%>/customizing/<c:out value="${themeCode}"/>/css/color_01.css<%=resourceVersion%>" />
		</c:when>
		<c:when test="${themeType == 'green'}">
			<link rel="stylesheet" type="text/css" href="<%=cssPath%>/customizing/<c:out value="${themeCode}"/>/css/color_02.css<%=resourceVersion%>" />
		</c:when>
		<c:when test="${themeType == 'red'}">
			<link rel="stylesheet" type="text/css" href="<%=cssPath%>/customizing/<c:out value="${themeCode}"/>/css/color_03.css<%=resourceVersion%>" />
		</c:when>
		<c:when test="${themeType == 'black'}">
			<link rel="stylesheet" type="text/css" href="<%=cssPath%>/customizing/<c:out value="${themeCode}"/>/css/color_04.css<%=resourceVersion%>" />
		</c:when>
	</c:choose>
	<link rel="text/javascript" src="<%=cssPath%>/customizing/<c:out value="${themeCode}"/>/js/project.js<%=resourceVersion%>" />
</c:if>

<link rel="stylesheet" id="languageCSS" type="text/css" href="<%=cssPath%>/covicore/resources/css/language/<c:out value="${LanguageCode}"/>.css<%=resourceVersion%>" />

<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js<%=resourceVersion%>"></script>
<![endif]-->
<script type="text/javascript" src="/covicore/resources/script/jquery.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/jquery.mousewheel.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/jquery.mCustomScrollbar.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/typed.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/bootstrap.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/jquery-ui-1.12.1.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXJ.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXGrid.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/idle-timer.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/CommonControls.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXTree.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Utils.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.editor.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.file.js<%=(resourceVersion+(resourceVersion.equals("")?"?":"&")+"maxsize="+FileUtil.getMaxUploadSize())%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.menu.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.acl.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.dic.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.control.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.comment.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/autosize.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.orgchart.A0.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.orgchart.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.orgchart.mail.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.orgchart.teams.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.notify.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/validation.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.policy.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/approval/resources/script/user/common/ApprovalUserCommon.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Dictionary.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/menu/adminorganization.js<%=resourceVersion%>"></script>

<script type="text/javascript" src="/covicore/resources/script/palette-color-picker.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Jsoner/Jsoner.0.8.2.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/slick.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/jquery.slimscroll.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/html2canvas.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/clipboard.min.js<%=resourceVersion%>"></script>

<script type="text/javascript" src="/groupware/resources/script/user/board.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/event_date.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/schedule.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/schedule_google.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/event_calendar.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/resource.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/bizcard.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/bizcard_list.js<%=resourceVersion%>"></script>

<script type="text/javascript" src="/groupware/resources/script/user/attendance.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/collab_main.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/collab_util.js<%=resourceVersion%>"></script>
<!-- covision util 함수  -->
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.util.js<%=resourceVersion%>"></script>

<%if (RedisDataUtil.getBaseConfig("CollabGantt").equals("Y")){ %>
<script type="text/javascript" src="/covicore/resources/script/highcharts/highcharts-gantt.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/highcharts/modules/draggable-points.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/highcharts/modules/no-data-to-display.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/highcharts/modules/exporting.js<%=resourceVersion%>"></script>
<%} %>

<c:if test="${isUseMail eq 'Y'}">
<script type="text/javascript" src="/mail/resources/script/cmmn/route.config.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/cmmn/cmmn.func.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/cmmn/cmmn.router.js<%=resourceVersion%>"></script>

<script type="text/javascript" src="/mail/resources/script/user/mail.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/user/mail_list.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/user/mail_write.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/user/mail_read.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/user/mail_approval.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/user/mailCommon.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/mail/resources/script/cmmn/mail.common.js<%=resourceVersion%>"></script>
</c:if>

<c:if test="${isUseAccount eq 'Y'}">
<script type="text/javascript" src="/account/resources/script/user/accountCommon.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/account/resources/script/user/accountFileCommon.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/account/resources/script/common/account.control.js<%=resourceVersion%>"></script>
</c:if>

<c:if test="${isUseHrManage eq 'Y'}">
<script type="text/javascript" src="/hrmanage/resources/script/user/common_hr.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/hrmanage/resources/script/user/common_hrBase.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/hrmanage/resources/script/user/hr_fileUtil.js<%=resourceVersion%>"></script>
</c:if>

<!--  파라메터 유호성 체크 -->
<script type="text/javascript" src="/covicore/resources/script/security/aes.js"></script>
<script type="text/javascript" src="/covicore/resources/script/security/x64-core.js"></script>
<script type="text/javascript" src="/covicore/resources/script/security/sha256.js"></script>
<script type="text/javascript" src="/covicore/resources/script/security/pbkdf2.js"></script>
	
<%	out.println("<script>var PARAM_VALID='"+  egovframework.baseframework.util.StringUtil.replaceNull(PropertiesUtil.getSecurityProperties().getProperty("param.valid"),"N")+"';</script>");	
 %>
<style>
<%
if (!SessionHelper.getSession("DomainImagePath").equals("") 
		&& SessionHelper.getSession("DomainImagePath").indexOf(";") > 0
			&& !SessionHelper.getSession("DomainImagePath").split(";")[0].equals("")){
	out.println("#header > h1 a { background: url('/covicore/common/logo/PC_Logo.png.do') no-repeat left center}");
}	

if (PropertiesUtil.getGlobalProperties().getProperty("isSaaS").equalsIgnoreCase("Y") && !SessionHelper.getSession("DN_ID").equalsIgnoreCase("0")){
	out.print(".domain {display:none}");
}
%>

.ui-datepicker select.ui-datepicker-year {
	width: 40%;
}
</style>


