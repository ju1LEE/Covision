<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script defer type="text/javascript" src="/webhard/resources/script/admin/webhard_admin.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/admin/event_admin.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/admin/fileSearch.js<%=resourceVersion%>"></script>

<h3 class="con_tit_box">
	<span class="con_tit"><spring:message code='Cache.lbl_searchWebhardFiles'/></span> <!-- 웹하드 파일 검색 -->
</h3>

<div style="width: 100%; min-height: 500px">
	<!-- 버튼 영역 -->
	<div id="topitembar01" class="topbar_grid">
		<label>
			<input type="button" id="btnDelete" class="AXButton BtnDelete" value="<spring:message code='Cache.lbl_delete'/>"/> <!-- 삭제  -->
			<input type="button" id="btnDownload" class="AXButton" value="<spring:message code='Cache.siteLinkDownLoad'/>"/> <!-- 다운로드  -->
		</label>
	</div>
	<!-- // 버튼 영역 -->
	<!-- 검색 영역 -->
	<div id="topitembar02" class="topbar_grid">

		<span class="domain">
			<label>
				<span style="margin-right: 10px;"><spring:message code='Cache.lbl_secAtt1'/></span> <!-- 계열사 선택 -->
				<select id="selectDomain" class="AXSelect" style="border-radius: 0px; height: 24px; line-height: 24px;"></select>
			</label>
		</span>

		<label style="margin: 0px 10px 0px 10px;">
			<span style="margin-right: 5px;"><spring:message code='Cache.lbl_Mail_FileFormat'/></span> <!-- 파일형식 -->
			<input id="inputFileType" type="text" class="AXInput" style="width: 100px" /> 	
		</label>
		<label style="margin: 0px 10px 0px 10px;">
			<span style="margin-right: 5px;"><spring:message code='Cache.WH_fileName'/></span> <!-- 파일명 -->
			<input id="inputFileName" type="text" class="AXInput" style="width: 160px" /> 	
		</label>
		<label style="margin: 0px 10px 0px 10px;">
			<span style="margin-right: 5px;"><spring:message code='Cache.lbl_webhardFileSize'/></span> <!-- 파일크기 -->
			<select id="selectFileSizeOption" class="AXSelect" style="border-radius: 0px;">
				<option value="" selected="selected"><spring:message code='Cache.lbl_Whole'/></option> <!-- 전체  -->
				<option value="below1MB">~ 1MB</option>
				<option value="below10MB">1MB ~ 10MB</option>
				<option value="below100MB">10MB ~ 100MB</option>
				<option value="over100MB">100MB</option>
			</select>
		</label>
		<label>
			<span style="margin-right: 5px;"><spring:message code='Cache.lbl_Period'/></span> <!-- 기간 -->
			<select id="selectPeriodOption" class="AXSelect" style="border-radius: 0px;">
				<option value="" selected="selected"><spring:message code='Cache.lbl_Whole'/></option> <!--전체 -->
				<option value="1Week"><spring:message code='Cache.lbl_1Week'/></option> <!-- 1주일 -->
				<option value="1Month"><spring:message code='Cache.lbl_1Month'/></option> <!-- 1개월 -->
				<option value="3Month"><spring:message code='Cache.lbl_3Month'/></option> <!-- 3개월 -->
				<option value="6Month"><spring:message code='Cache.lbl_6Month'/></option> <!-- 6개월 -->
				<option value="manual"><spring:message code='Cache.lbl_Mail_DirectInput'/></option> <!-- 직접입력 -->
			</select>
		</label>
		<label id="inputDate" style="display: none;">
			<input type="text" id="startDate" class="AXInput" style="width: 90px"/> ~ 
			<input type="text" id="endDate" kind="twindate" date_startTargetID="startDate" class="AXInput" style="width: 90px"/>
		</label>
		<label style="margin-left: 10px;">
			<input type="button" id="btnSearch" class="AXButton" value="<spring:message code="Cache.btn_search"/>" /> <!-- 검색  -->
		</label>
	</div>
	<!-- // 검색 영역 -->
	<!-- 그리드 영역 -->
	<div id="page"></div>
	<!-- // 그리드 영역 -->
</div>
