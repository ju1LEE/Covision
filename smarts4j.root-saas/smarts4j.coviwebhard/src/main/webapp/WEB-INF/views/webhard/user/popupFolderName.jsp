<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/userInclude.jsp"></jsp:include>

<style>
	.popContent {
		position: relative;
		padding: 40px 24px 52px;
		font-size: 13px;
		color: #000;
	}
	.popTop {
		position: absolute;
		top: 0;
		left: 24px;
		right: 24px;
		padding-top: 18px;
		text-align: center;
	}
	.popTop > p {
		padding-left: 30px;
		display: inline-block;
		height: 24px;
		line-height: 24px;
		overflow-y: auto;
	}
	.popMiddle {
		height: 65px;
		padding-top: 20px;
		overflow-y: auto;
	}
	.popBottom {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		padding-bottom: 22px;
		text-align: center;
	}
</style>

<body>
	<div class="popContent layerType02 ">
		<div class="popTop">
			<c:if test="${mode eq 'edit_file'}">
				<p><spring:message code='Cache.msg_enterFileNameExceptFileType'/></p> <!-- 확장자를 제외한 파일명을 입력하세요 -->
			</c:if>
			<c:if test="${mode ne 'edit_file'}">
				<p><spring:message code='Cache.WH_msg_enterFolderName'/></p> <!-- 폴더명을 입력하세요  -->
			</c:if>
		</div>
		<div class="popMiddle">
			<input type="text" id="txtInputName"></textarea>
		</div>
		<div class="popBottom">
			<a href="#" id="btnOK" class="btnTypeDefault btnTypeBg"><spring:message code='Cache.btn_ok'/></a> <!-- 확인 -->
			<a href="#" id="btnCancel" class="btnTypeDefault"><spring:message code='Cache.btn_Cancel'/></a> <!-- 취소 -->
		</div>
	</div>
</body>

<script>
	(function(param){
		var init = function(){
			setEvent();
			if(param.mode === "edit" || param.mode === "edit_file") setData();
		};
		
		var setEvent = function(){
			$("#btnOK").off("click").on("click", function(){
				param.mode === "add" && addFolder();
				param.mode === "edit" && renameFolder();
				param.mode === "edit_file" && renameFile();
			});
			
			$("#btnCancel").off("click").on("click", function(){
				Common.Close();
			});
		};
		
		var setData = function(){
			$.ajax({
				url: "/webhard/user/getFolderOrFileName.do",
				type: "POST",
				data: {
					mode: param.mode,
					boxUuid: param.boxUUID,
					folderUuid: param.folderUUID,
					fileUuid: param.fileUUID
				},
				success: function(res){
					if(res.status === "SUCCESS"){
						$("#txtInputName").val(res.existingName);
					}else{
						Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/user/renameFolder.do", response, status, error);
				}
			});
		};
		
		var validation = function(){
			var editName = $("#txtInputName").val().trim();
			var spePatt = /[`*|\\\'\";:\/]/gi;
			
			if(!editName){
				parent.Common.Warning("<spring:message code='Cache.msg_apv_146'/>", "Warning Dialog", function(){ // 공백은 등록할 수 없습니다.
			    	$("#txtInputName").focus();
			    });
				return false;
			}
			if(spePatt.test(editName)){
				parent.Common.Warning("<spring:message code='Cache.msg_specialNotAllowed'/>", "Warning Dialog", function(){ // 특수문자는 사용할 수 없습니다.
			    	$("#txtInputName").focus();
			    });
				return false;
			}
			if(editName.substr(-1) == ".") {
				parent.Common.Warning("<spring:message code='Cache.msg_removePointAtEnd'/>", "Warning Dialog", function(){ // 마지막에 .을 입력할 수 없습니다.
			    	$("#txtInputName").focus();
			    });
				return false;
			}
			
			return true;
		};
		
		var addFolder = function(){
			if(!validation()) return false;

			$.ajax({
				url: "/webhard/user/addFolder.do",
				type: "POST",
				data: {
					boxUuid: param.boxUUID,
					parentUuid: param.folderUUID,
					folderName: $("#txtInputName").val().trim()
				},
				success: function(res){
					if(res.status === "SUCCESS"){
						Common.Inform("<spring:message code='Cache.msg_Added'/>", "Information", function(){ // 추가 되었습니다.
							parent.treeObj.refresh();
							Common.Close();
	    				});
					}else{
						Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/user/addFolder.do", response, status, error);
				}
			});
		};
		
		var renameFolder = function(){
			if(!validation()) return false;
			
			$.ajax({
				url: "/webhard/user/renameFolder.do",
				type: "POST",
				data: {
					boxUuid: param.boxUUID,
					folderUuid: param.folderUUID,
					folderName: $("#txtInputName").val().trim()
				},
				success: function(res){
					if(res.status === "SUCCESS"){
						Common.Inform("<spring:message code='Cache.msg_Changed'/>", "Information", function(){ // 변경되었습니다
							parent.treeObj.refresh();
							Common.Close();
	    				})
					}else{
						Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/user/renameFolder.do", response, status, error);
				}
			});
		};
		
		var renameFile = function() {
			if(!validation()) return false;
			
			$.ajax({
				url: "/webhard/user/renameFile.do",
				type: "POST",
				data: {
					boxUuid: param.boxUUID,
					fileUuid: param.fileUUID,
					fileName: $("#txtInputName").val().trim()
				},
				success: function(res){
					if(res.status === "SUCCESS"){
						Common.Inform("<spring:message code='Cache.msg_Changed'/>", "Information", function(){ // 변경되었습니다
							parent.treeObj.refresh();
							Common.Close();
	    				})
					}else{
						Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/user/renameFile.do", response, status, error);
				}
			});
		};
		
		init();
		
	})({
		  mode: "${mode}"
		, boxUUID: "${boxUUID}"
		, folderUUID: "${folderUUID}"
		, fileUUID: "${fileUUID}"
	});
</script>