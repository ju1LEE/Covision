<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script defer type="text/javascript" src="/webhard/resources/script/admin/webhard_admin.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/admin/event_admin.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/admin/config.js<%=resourceVersion%>"></script>

<h3 class="con_tit_box">
	<span class="con_tit"><spring:message code='Cache.lbl_configWebhard'/></span> <!--  웹하드 환경설정 -->
</h3>

<div style="width: 100%; min-height: 500px;">

	<div id="topitembar_2" class="topbar_grid">

		<span class="domain">
			<label>
				<span style="margin-right: 10px;"><spring:message code='Cache.lbl_secAtt1'/></span> <!-- 계열사 선택 -->
				<select id="selectDomain" class="AXSelect" style="border-radius: 0px; height: 24px; line-height: 24px;"></select>
			</label>
		</span>

		<label style="margin-left: 10px;">
			<input id="btnSave" class="AXButton" type="button" value="<spring:message code='Cache.btn_save'/>" /> <!-- 저장 -->
		</label>
	</div>
	<div>
		<table class="AXFormTable">
			<colgroup>
				<col width="30%"/>
				<col width="70%"/>
			</colgroup>
			<tr>
				<th><spring:message code='Cache.lbl_defaultBoxVol'/> <span style="color: red;">*</span></th> <!-- 기본 BOX 할당량 -->
				<td>
					<input id="inputBoxVolume" type="text" class="AXInput" style="width: 50%" /><span style="margin-left: 10px;">MB</span>
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_maxUploadSize'/> <span style="color: red;">*</span></th> <!-- 최대 업로드 크기  -->
				<td>
					<input id="inputMaxUploadSize" type="text" class="AXInput" style="width: 50%" /><span style="margin-left: 10px;">MB</span>
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_uploadLimitExt'/></th> <!-- 업로드 제한 확장자 -->
				<td>
					<input id="inputExtensions" type="text" class="AXInput" style="ime-mode: disabled; width: calc(100% - 10px);" />
					<div style="float: right; margin: 5px 5px 0px;">* <spring:message code='Cache.msg_splitComma'/></div> <!-- 확장자를 콤마(,)로 구분하여 입력하시기 바랍니다. -->
				</td>
			</tr>
			<tr style="display:none">
				<th><spring:message code='Cache.lbl_useSharing'/><!-- 공유 기능 사용 --></th>
				<td>
					<input id="switchSharing" type="text" kind='switch' on_value='Y' off_value='N' style="width: 50px; height: 21px; border: 0px none;" />
				</td>
			</tr>
		</table>
	</div>
</div>
