<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path");
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision.control.css<%=resourceVersion%>" />	
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/common.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/user_common_controls.css<%=resourceVersion%>" />  
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/Controls.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/webhard/resources/css/webhard.css<%=resourceVersion%>" />

<script type="text/javascript" src="/covicore/resources/script/jquery.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/jquery-ui-1.12.1.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXJ.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.control.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/CommonControls.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Utils.js<%=resourceVersion%>"></script>

<script type="text/javascript" src="/webhard/resources/script/common/common.js<%=resourceVersion%>"></script>

<div class="HUG_background">
	<div id="containerDiv" class="linkDpop">
		<div class="body_category">
			<span id="fileIcon"></span>
		</div>
		<div class="linkDpopTxt">
			<p id="subject" class="Pname"></p>
			<p id="fileSize" class="DS"></p>
			<p id="createDate" class="Pdate"></p>
		</div>
		<a href="#" id="btnDownload" class="linkDpopBtn"><spring:message code='Cache.WH_download'/></a> <!--  다운로드 -->
	</div>
</div>

<script>
	
	(function(param){
		
		var init = function(){
			setEvent();
			checkAuth();
		};
		
		var setEvent = function(){
			$("#btnDownload").off("click").on("click", function(){
				fileDown(param.uuid);
			});
		};
		
		var checkAuth = function(){
			if(param.isExist !== "Y"){
				Common.Warning("<spring:message code='Cache.WH_msg_noExistLink'/>", "Warning", function(){ // 존재하지 않는 링크입니다.
					history.back();
				});
				return false;
			}else if(param.auth === "STOP"){
				Common.Warning("<spring:message code='Cache.WH_msg_stopSharingLink'/>", "Warning", function(){ // 공유 중지된 링크입니다.
					history.back();
				});
				return false;
			}else if(param.isValid !== "Y"){
				Common.Warning("<spring:message code='Cache.WH_msg_expireLink'/>", "Warning", function(){ // 기간 만료된 링크입니다.
					history.back();
				});
				return false;
			}else if(param.auth === "PW"){
				checkPassword();
			}else{
				setLinkData();
			}
		};
		
		var checkPassword = function(){
			Common.Password("<spring:message code='Cache.WH_msg_enterPassworkForLink'/>", "Password", "<spring:message code='Cache.lbl_ConfirmPassword'/>", function(result){ // 링크접속에 필요한 비밀번호를 입력해주세요. / 비밀번호 확인 
				if(!result){
					history.back();
				}else{
					$.ajax({
						url: "/webhard/link/checkPassword.do",
						type: "POST",
						data: {
							  "link": param.link
							, "password": result
						},
						success: function(data){
							if(data.status === "SUCCESS"){
								if(data.cnt > 0){
									setLinkData();
								}else{
									Common.Warning("<spring:message code='Cache.WH_msg_incorrectPW'/>", "Warning", function(){ // 비밀번호가 올바르지 않습니다.
										location.reload();
									});
								}
							}else{
								Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
							}
						},
						error: function(response, status, error){
			        	     CFN_ErrorAjax("/webhard/link/checkPassword.do", response, status, error);
			        	}
					});
				}
			});
		};

		var setLinkData = function(){
			$.ajax({
				url: "/webhard/link/getFileInfo.do",
				type: "POST",
				data: {
					"uuid": param.uuid
				},
				success: function(data){
					if(data.status === "SUCCESS"){
						$("#subject").text(data.file.fileName);
						$("#fileType").text(data.file.fileType);
						$("#createDate").text(data.file.createdDate);
						$("#fileSize").text(webhardCommon.convertFileSize(data.file.fileSize));
						$("#fileIcon").addClass(setIcon(data.file.fileType.toLowerCase()));
					}else{
						Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
					}
				},
				error:function(response, status, error){
		    	     //TODO 추가 오류 처리
		    	     CFN_ErrorAjax("/webhard/link/getFileInfo.do", response, status, error);
		    	}
			});
		};
		
		var setIcon = function(ext){
			if (ext === "xls" || ext === "xlsx") {
				return "ic_xls";
			} else if (ext === "folder") {
				return "ic_folder";z
			} else if (ext === "zip" || ext === "rar" || ext === "7z") {
				return "ic_zip";
			} else if (ext === "doc" || ext === "docx") {
				return "ic_word";
			} else if (ext === "ppt" || ext === "pptx") {
				return "ic_ppt";
			} else if (ext === "pdf" ) {
				return "ic_pdf";
			} else {
				return "ic_etc";
			}
		};
		
		var fileDown = function(pFileUuids){
			var $form = $("<form />", {"action": "/webhard/common/fileDown.do", "method": "POST"});
			
			$form.append($("<input />", {"name": "mode", "value": "link"}));
			$form.append($("<input />", {"name": "fileUuids", "value": pFileUuids ? pFileUuids : ""}));
			$form.append($("<input />", {"name": "folderUuids", "value": ""}));
			$form.appendTo("body");
			
			$form.submit();
			$form.remove();
		}
		
		init();
		
	})({
		  uuid:		"${uuid}"
		, link:		"${link}"
		, auth:		"${auth}"
		, isValid:	"${isValid}"
		, isExist:	"${isExist}"
	});
	
</script>