<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>
<style>
.cRConTop , .cRContEnd {display:none;}
.cRContBottom {top:13px;bottom:20px;}
</style>

<div id="tarDiv" style="display:none;height:590px;">
</div>						

<script>
	var eventId = CFN_GetQueryString("eventID");
	var dateId = CFN_GetQueryString("dateID");
	var repeatId = CFN_GetQueryString("repeatID");
	var isRepeat = CFN_GetQueryString("isRepeat");
	var isRepeatAll = CFN_GetQueryString("isRepeatAll");
	var resourceId = CFN_GetQueryString("resourceID");
    
	initContent();

	// 승인 및 검토 요청 팝업
	function initContent(){
		$('#tarDiv').load('/groupware/resource/detailView.do?CLSYS=resource&CLMD=user&CLBIZ=Resource&eventID=' + eventId + '&dateID=' + dateId + '&repeatID=' + repeatId + '&isRepeat=' + isRepeat + '&isRepeatAll=' + isRepeatAll + '&resourceID=' + resourceId, function() {
			setTimeout(function() {$('#tarDiv').css('display','');}, 500);
	    });
	}
</script>