<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script defer type="text/javascript" src="/webhard/resources/script/admin/webhard_admin.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/admin/event_admin.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/admin/boxManage.js<%=resourceVersion%>"></script>

<h3 class="con_tit_box">
	<span class="con_tit"><spring:message code="Cache.lbl_manageWebhardBox"/></span> <!-- 웹하드 BOX 관리 -->
</h3>

<div style="width: 100%; min-height: 500px">
	<div id="topitembar01" class="topbar_grid">
		<label>
			<%-- <input id="btnBoxLock" type="button" class="AXButton" value="<spring:message code='Cache.lbl_lockBox'/>"/> --%> <!-- BOX 잠금  -->
			<%-- <input id="btnDelete" type="button" class="AXButton BtnDelete" value="<spring:message code='Cache.lbl_delete'/>" /> --%> <!-- 삭제  -->
			<input id="btnRefresh" type="button" class="AXButton BtnRefresh" value="<spring:message code='Cache.lbl_Refresh'/>" /> <!-- 새로고침  -->
		</label>
	</div>
	<div id="topitembar02" class="topbar_grid">
		<span class="domain">
			<label>
				<span style="margin-right: 10px;"><spring:message code='Cache.lbl_secAtt1'/></span> <!-- 계열사 선택 -->
				<select id="selectDomain" class="AXSelect" style="border-radius: 0px; height: 24px; line-height: 24px;"></select>
			</label>
		</span>
		<label style="margin-left: 10px;">
			<select id="selectSearchOption" class="AXSelect" style="border-radius: 0px;"></select>			
			<input id="inputSearchWord" type="text" class="AXInput" style="width: 100px" /> 
		</label>
		<label style="margin-left: 10px;">
			<input id="btnSearch" type="button" class="AXButton" value="<spring:message code='Cache.lbl_search'/>"/> <!-- 검색-->
		</label>
	</div>
	<!-- Grid -->
	<div id="page"></div>
</div>
