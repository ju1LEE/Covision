<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/adminInclude.jsp"></jsp:include>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script defer type="text/javascript" src="/webhard/resources/script/admin/webhard_admin.js<%=resourceVersion%>"></script>

<body>
	<form id="frmComment" style="text-align: center;">	
	    <table style="font-size: small; width: 100%;">
	    	<tr>
	    		<td>
	    			<span class="join_warning"></span>
	    		</td>
			   	<td>
			   		<div id="divCommentInfo" style="margin-left: 10px;"></div>
			   	</td>
		   	</tr>
	   	</table>
	   	<br/>
	   	<textarea id="txtComment" style="width: 100%; height: 100px; resize: none;"></textarea>
		<div align="center" style="padding-top: 10px;">
			<input id="btnLock" class="AXButton red" type="button" value="<spring:message code='Cache.lbl_Lock'/>" style="display: none;"> <!-- 잠금 -->
			<input id="btnUnlock" class="AXButton red" type="button" value="<spring:message code='Cache.lbl_UnLock'/>" style="display: none;"> <!-- 잠금해제 -->
			<input id="btnCancel" class="AXButton" type="button" value="<spring:message code='Cache.lbl_Cancel'/>"> <!-- 취소 -->
		</div>
	</form>
</body>

<script>
	//# sourceURL=popupBoxLock.jsp
	
	(function(param){
		
		var init = function(){
			setPopup();
			setEvent();	
		}
		
		var setPopup = function(){
			if(param.mode === "lock"){
				$("#divCommentInfo").html("<spring:message code='Cache.msg_selectedBoxLock' /><br><spring:message code='Cache.msg_EnterReason'/>"); // 선택한 Box를 잠금 처리하시겠습니까?<br>사유를 입력하여 주세요.
				$("#btnLock").show();
			}else{
				setComment();
				$("#divCommentInfo").html("<spring:message code='Cache.msg_boxLockReason' />"); // 선택된 BOX는 아래와 같은 사유로<br>잠금 처리 되었습니다.
				$("#btnUnlock").show();
			}
		}
		
		var setEvent = function(){
			// 잠금, 잠금 해제
			$("#btnLock, #btnUnlock").off("click").on("click", setBoxBlockYN);
			
			// 취소
			$("#btnCancel").off("click").on("click", function(){
				parent.webhardAdminView.render();
				Common.Close();
			});
		}
		
		var setComment = function(){
			$.ajax({
				url: "/webhard/admin/boxManage/getBoxBlockReason.do",
				type:"POST",
				data : {
					"boxUuid" : param.boxUuid
				},
				success: function(data){
					if(data.status === "SUCCESS"){
						$("#txtComment").html(data.reason);
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/admin/boxManage/getBoxBlockReason.do", response, status, error);
				}
			});
		}
		
		var setBoxBlockYN = function(){
			$.ajax({
				url: "/webhard/admin/boxManage/setBoxBlockYn.do",
				type: "POST",
				data: {
					  boxUuid: param.boxUuid
					, blockYn: param.mode === "lock" ? "Y" : "N"
					, blockReason: $("#txtComment").val() ? $("#txtComment").val() : ""
				},
				success: function(data){
					if(data.status === "SUCCESS"){
						parent.webhardAdminView.render();
						Common.Close();
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/admin/boxManage/setBoxBlockYn.do", response, status, error);
				}
			});
		}
		
		init();
		
	})({
		  mode: "${mode}"
		, boxUuid: "${boxUuid}"
	});
</script>