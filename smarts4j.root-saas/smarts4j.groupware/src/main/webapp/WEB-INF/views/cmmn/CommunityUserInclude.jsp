<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@page import="egovframework.baseframework.util.SessionHelper"%>
<%@page import="egovframework.baseframework.util.RedisDataUtil"%>
<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@page import="egovframework.coviframework.util.FileUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% String approvalAppPath = PropertiesUtil.getGlobalProperties().getProperty("approval.app.path"); %>
<% String appPath = PropertiesUtil.getGlobalProperties().getProperty("app.path"); %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<%
	pageContext.setAttribute("isUseMail", RedisDataUtil.getBaseConfig("isUseMail")) ;
	pageContext.setAttribute("isUseAccount", RedisDataUtil.getBaseConfig("isUseAccount"));
	pageContext.setAttribute("themeType", SessionHelper.getSession("UR_ThemeType"));
	pageContext.setAttribute("themeCode", SessionHelper.getSession("UR_ThemeCode"));
	pageContext.setAttribute("LanguageCode", SessionHelper.getSession("LanguageCode"));	
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
<c:if test="${isUseMail eq 'Y'}">	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/mail/resources/css/mail.css<%=resourceVersion%>" /></c:if>
<c:if test="${isUseAccount eq 'Y'}"><link rel="stylesheet" type="text/css" href="<%=cssPath%>/eaccounting/resources/css/eaccounting.css<%=resourceVersion%>"></c:if>
<!--  통합업무관리 추가-->
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/IntegratedTaskManagement/resources/css/IntegratedTaskManagement.css<%=resourceVersion%>">

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
<script	type="text/javascript" src="/covicore/resources/script/bootstrap.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/jquery-ui-1.12.1.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXJ.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXGrid.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/idle-timer.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/CommonControls.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXTree.js<%=resourceVersion%>"></script>
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
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.notify.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/validation.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.policy.js<%=resourceVersion%>"></script>

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

<!-- covision util 함수  -->
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.util.js<%=resourceVersion%>"></script>