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
	    		<td><span class="join_warning"></span></td>
			   	<td><div id="divCommentInfo" style="margin-left: 10px;"><spring:message code='Cache.lbl_DeleteAttachFile' /><br><spring:message code='Cache.msg_EnterReason'/></div></td> <!-- 파일을 삭제하시겠습니까?<br>사유를 입력하여 주세요. -->
		   	</tr>
	   	</table>
	   	<br/>
	   	<textarea id="txtComment" style="width:100%; height:100px; resize: none;"></textarea>
		<div align="center" style="padding-top: 10px;">
			<input id="btnDelete" class="AXButton red" type="button" value="<spring:message code='Cache.lbl_delete'/>"> <!-- 삭제 -->
			<input id="btnCancel" class="AXButton" type="button" value="<spring:message code='Cache.lbl_Cancel'/>"> <!-- 취소 -->
		</div>
	</form>	
</body>

<script>
	//# sourceURL=popupFileDelete.jsp
	
	(function(param){
		var init = function(){
			setEvent();
		}
		
		var setEvent = function(){
			// 삭제
			$("#btnDelete").off("click").on("click", fileDelete);
			
			// 취소
			$("#btnCancel").off("click").on("click", function(){
				Common.Close();
			});
			
		}
		
		var validation = function(){
			if(!$("#txtComment").val()){
				parent.Common.Warning("<spring:message code='Cache.msg_EnterReason' />", "Warning Dialog", function(result){ // 사유를 입력하여 주세요.
			    	result && $("#txtComment").focus();
			    });
				
				return false;
			}
			
			return true;
		}
		
		var fileDelete = function(){
			if(!validation()) return false;
			
			webhardAdmin.fileDelete(param.fileUuids, $("#txtComment").val()).done(function(){
				parent.webhardAdminView.render();
				Common.Close();
			});
		}
		
		init();
	})({
		fileUuids: "${fileUuids}"
	});
	
</script>
