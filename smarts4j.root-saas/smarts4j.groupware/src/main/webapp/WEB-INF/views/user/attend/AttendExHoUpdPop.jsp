<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!doctype html>
<html lang="ko">
<head>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>

<script type="text/javascript">

$(document).ready(function(){
	init(); 
});

function init(){
	$(".attChgTime").change(function(){
		calculTime();
	});
}

function closeLayer(){
	parent.Common.Close("ExHoSchPop");
}

function save(){
	var params = $("#exHoFrm").serialize();

	$.ajax({
		url : "/groupware/attendExHo/updExHoSchInfo.do",
		type: "POST",
		dataType : 'json',
		data : params,
		success:function (data) {
			if(data.status=='SUCCESS'){
				Common.Inform("<spring:message code='Cache.msg_SuccessModify'/>", "Infomation", function(){
					parent.search(1);
					closeLayer();		
				});	
			}else{
				Common.Warning("<spring:message code='Cache.msg_sns_03'/>");/* 저장 중 오류가 발생하였습니다. */
			}
		},
		error:function (error){
			//CFN_ErrorAjax("/groupware/layout/attendance_AttendanceBaseInfo.do", response, status, error);
		}
	}); 
}

function calculTime(){
    var startHour =  $("#StartHour").val();
	var startMin =  $("#StartMin").val();
    var endHour = $("#EndHour").val();
    var endMin = $("#EndMin").val();
    
    var startCa = Number(startHour*60)+Number(startMin);
    var endCa = Number(endHour*60)+Number(endMin);
    if(startCa > endCa){
    	Common.Warning("<spring:message code='Cache.msg_att_exhoTimeChgAlert'/>");/* 시작일은 종료일보다 클 수 없습니다. */
    	$("#StartHour").val(endHour);
    	$("#StartMin").val(endMin);
    	$("#WorkHour").val("00");
    	$("#WorkMin").val("00");
    }else{
    	
    	var chTime = endCa - startCa;
    	
    	var workHour = Math.round(chTime/60);
    	var workMin = Math.round(chTime%60);
    	
    	$("#WorkHour").val(workHour>9?workHour:"0"+workHour);
    	$("#WorkMin").val(workMin>9?workMin:"0"+workMin);

    }
}

</script>
<style type="text/css">

</style>
</head>
<body> 
	<div class="popContent">
		<form id="exHoFrm" method="post">
		<input type="hidden" name="ExHoSeq" value="${ExHoSeq }" />
		<input type="hidden" name="JobDate" value="${ExHo.JobDate }" />
		<div class="rowTypeWrap formWrap">
 			<dl>
				<dt style="width:100px;"><spring:message code='Cache.lbl_StartTime'/></dt>	<!-- 시작시간 -->
				<dd>
					<select class="selectType04 size48 attChgTime" id="StartHour" name="StartHour">
					<c:forEach begin="00" end="23" var="hour">
						<option value="<c:out value="${ hour < 10 ? '0' : '' }${hour}"/>" <c:if test="${hour == ExHo.sHour }">selected</c:if> ><c:out value="${ hour < 10 ? '0' : '' }${hour}" /></option>
					</c:forEach>
					</select>
					<span>:</span>
					<select class="selectType04 size48 attChgTime" id="StartMin" name="StartMin">
					<c:forEach begin="00" end="59" step="10" var="min">
						<option value="<c:out value="${ min < 10 ? '0' : '' }${min}"/>" <c:if test="${min == ExHo.sMin }">selected</c:if> ><c:out value="${ min < 10 ? '0' : '' }${min}"  /></option>
					</c:forEach>
					</select>
				</dd>
			</dl>
			<dl>
				<dt style="width:100px;"><spring:message code='Cache.lbl_EndTime'/></dt>	<!-- 종료시간 -->
				<dd>
					<select class="selectType04 size48 attChgTime" id="EndHour" name="EndHour">
					<c:forEach begin="00" end="23" var="hour">
						<option value="<c:out value="${ hour < 10 ? '0' : '' }${hour}"/>" <c:if test="${hour == ExHo.eHour }">selected</c:if> ><c:out value="${ hour < 10 ? '0' : '' }${hour}" /></option>
					</c:forEach>
					</select>
					<span>:</span>
					<select class="selectType04 size48 attChgTime" id="EndMin" name="EndMin">
					<c:forEach begin="00" end="59" step="10" var="min">
						<option value="<c:out value="${ min < 10 ? '0' : '' }${min}"/>" <c:if test="${min == ExHo.eMin }">selected</c:if> ><c:out value="${ min < 10 ? '0' : '' }${min}"  /></option>
					</c:forEach>
					</select>
				</dd>
			</dl>
			<dl>
				<dt style="width:100px;"><spring:message code='Cache.lbl_att_ac_time'/></dt>	<!-- 인정시간 -->
				<dd>
					<select class="selectType04 size48" id="WorkHour" name="WorkHour">
					<c:forEach begin="00" end="23" var="hour">
						<option value="<c:out value="${ hour < 10 ? '0' : '' }${hour}"/>" <c:if test="${hour == ExHo.WorkTimeHour }">selected</c:if> ><c:out value="${ hour < 10 ? '0' : '' }${hour}" /></option>
					</c:forEach>
					</select>
					<span>:</span>
					<select class="selectType04 size48 " id="WorkMin" name="WorkMin">
					<c:forEach begin="00" end="59" step="10" var="min">
						<option value="<c:out value="${ min < 10 ? '0' : '' }${min}"/>" <c:if test="${min == ExHo.WorkTimeMin }">selected</c:if> ><c:out value="${ min < 10 ? '0' : '' }${min}"  /></option>
					</c:forEach>
					</select>
				</dd>
			</dl>
		</div>
		</form>
		<div class="popBtnWrap">
			<a class="btnTypeDefault btnTypeBg" href="javascript:save();"><spring:message code='Cache.btn_Edit'/></a>	<!-- 수정 -->
			<a class="btnTypeDefault" href="javascript:closeLayer();"><spring:message code='Cache.btn_Cancel'/></a>	<!-- 취소 -->
		</div>
	</div>
</body>
</html>
