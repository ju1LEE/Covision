<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/userInclude.jsp"></jsp:include>

<body>
	<div id="contentDiv" class="popContent WebHardPopup">
		<div class="">
			<div class="top">						
				<div class="ulList Ltop">
					<ul>
						<li class="listCol">
							<div class="Ltit"><strong><spring:message code='Cache.lbl_Link'/>&nbsp;:</strong></div><!-- 링크 -->
							<div>
								<div class="dateSel type02">
									<input id="txtLink" type="text" readonly="readonly" class="name">
									<a href="#" id="btnCopy" class="btnTypeDefault btnBlueBoder"><spring:message code='Cache.lbl_Copy'/></a> <!-- 복사 -->
								</div>
							</div>
						</li>
					</ul>
				</div>
				<div class="ulList" style="padding-bottom:52px">
					<ul>
						<li class="listCol">
							<div><strong><spring:message code='Cache.lbl_Authority'/></strong></div> <!-- 권한  -->
							<div>
								<select id="selectAuth" class="selectType02">
									<option value="STOP"><spring:message code='Cache.WH_auth_stop'/></option> <!-- 링크 공유 중지 -->
									<option value="ALL"><spring:message code='Cache.WH_auth_all'/></option> <!-- 링크가 있는 모든 사용자 조회 가능 -->
									<option value="PW"><spring:message code='Cache.WH_auth_pw'/></option> <!-- 링크 접속 시 비밀번호 확인 후 조회 -->
								</select>
							</div>
						</li>	
						<li id="pwDiv" class="listCol" style="display:none;">
							<div><strong><spring:message code='Cache.WH_accessPW'/></strong></div> <!-- 접근 비밀번호 -->
							<div class="grayBox">
								<div class="passwordBox">
									<input type="password" id="txtPassword" class="inputP" value="${password}" placeholder="<spring:message code='Cache.lbl_Password'/>"> <!-- 비밀번호  -->
									<input type="password" id="txtRePassword" class="inputP" value="${password}" placeholder="<spring:message code='Cache.lbl_ConfirmPassword'/>"> <!-- 비밀번호 확인  -->
								</div>
							</div>	
						</li>
						<li class="listCol" style="position:absolute">
							<div><strong><spring:message code='Cache.WH_deadline'/></strong></div> <!-- 기한 -->
							<div>
								<div class="dateSel type02">
									<input id="calStartDate" type="text" class="adDate" date_separator="." value="${startDate}"> 
									<span>~</span>
									<input id="calEndDate" type="text" class="adDate" date_separator="." value="${endDate}" kind="twindate" date_startTargetID="calStartDate">
								</div>
							</div>
						</li>	
					</ul>							
				</div>
			</div>
			<div class="bottom">
				<!-- 적용 버튼 비 활성화 -> 활성화 클래스: btnTypeChk, 비활성화 클래스 : disabled --> 
				<a id="btnApply" href="#" class="btnTypeDefault disabled"><spring:message code='Cache.lbl_apply'/></a> <!-- 적용 -->
			</div>
		</div>
	</div>
</body>

<script>
//# sourceURL=popupLink.jsp

	(function(param){
		function init(){
			setEvent();
			setControl();
			
		//	$("#btnApply").attr("class", "btnTypeDefault disabled").off("click");
			
			if(param.isNew === "Y"){
				saveLink(true);
			}
		}
		
		var setEvent = function(){
			$("#btnCopy").off("click").on("click", function(){
				copyToClipBoard();
			});
			
			$("#selectAuth").off("change").on("change", function(){
				changeAuth();
				changeData();
			});
			
			$("#txtPassword, #txtRePassword, #calStartDate, #calEndDate").off("change").on("change", function(){
				changeData();
			});
		};

		var setControl = function(){
			var copiedLink = String.format(Common.getGlobalProperties("smart4j.path") + Common.getBaseConfig("WebhardLinkURL"), param.link);
			$("#txtLink").val(copiedLink);
			
			$("#selectAuth").val(param.auth).change();
		};
		
		var changeData = function(){
			$("#btnApply").attr("class", "btnTypeDefault btnTypeChk").off("click").on("click", function(){
				saveLink(false);
			});
		};

		var saveLink = function(isAuto){
			var authType = $("#selectAuth").val();
			
			if(authType === "PW" && !validationChk(authType)) return false;
			
			$.ajax({
				  type: "POST"
				, url: "/webhard/link/saveLink.do"
				, data: {
					  "isNew":		param.isNew
					, "targetType":	param.targetType
					, "targetUuid":	param.targetUuid
					, "link":		param.link
					, "auth":		authType
					, "password":	$("#txtPassword").val()
					, "startDate":	($("#calStartDate").val() ? $("#calStartDate").val() : "")
					, "endDate":	($("#calEndDate").val() ? $("#calEndDate").val() : "")
				},
				success: function(data){
					if(data.status === "SUCCESS"){
						param.isNew = "N";
						if(!isAuto){ // 처음 링크를 열어 자동 저장된 경우가 아닐때만 저장 팝업 표시
							Common.Inform("<spring:message code='Cache.msg_37'/>", "Information", function(){ // 저장되었습니다.
								Common.Close();
							});
						}
					}else{
						Common.Warning("<spring:message code='Cache.msg_sns_03'/>", "Warning", function(){ // 저장 중 오류가 발생하였습니다.
							Common.Close();
						});
					}
				}, 
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/link/saveLink.do", response, status, error);
					Common.Close();
				}
				
			});
		};

		var validationChk = function(){
			var txtPassword = $("#txtPassword").val();
			var txtRePassword = $("#txtRePassword").val();
			
			if(txtPassword === "") {
				Common.Warning("<spring:message code='Cache.msg_ObjectUR_03' />", 'Warning', function() { // 비밀번호를 입력하여 주십시오.
					$("#txtPassword").focus();
				});
				return false;
			} else if (txtPassword.length < 10 || txtPassword.length > 16) {
				Common.Warning("<spring:message code='Cache.msg_passwordLength' />", 'Warning', function() { // 비밀번호는 10자리 이상 16자리 이하로 입력해주세요
					$("#txtPassword").focus();
				});
				return false;
			} else if(txtRePassword === "") {
				Common.Warning("<spring:message code='Cache.msg_ObjectUR_04' />", 'Warning', function() { // 비밀번호를 다시한번 입력하여 주십시오.
					$("#txtRePassword").focus();
				});
				return false;
			} else if(txtPassword !== txtRePassword) {
				Common.Warning("<spring:message code='Cache.msg_ObjectUR_05' />", 'Warning', function() { // 비밀번호가 다릅니다.<br /> 다시 확인하여 입력하여 주십시오.
					$("#txtPassword").focus();
				});
				return false;
			}
			
			return true;
		};

		var copyToClipBoard = function(){
			$("#txtLink").select();
			document.execCommand("Copy");
			
			parent.Common.Inform("<spring:message code='Cache.msg_task_completeCopy'/>"); // 복사 되었습니다.
		};

		var changeAuth = function(selObj){
			if($("#selectAuth").val() !== "PW"){
				$("#txtPassword").val("");
				$("#txtRePassword").val("");
				$("#pwDiv").hide();
			}else{
				$("#pwDiv").show();
			}
			
			pageResize();
		};

		var pageResize = function() {
			var nHeight = $("#contentDiv").outerHeight() + 3;
			
			$("#linkpopup_pc", parent.document).height(nHeight + "px");
			$("#linkpopup_if", parent.document).height(nHeight + "px");
		};

		init();
		
	})({
		  targetType:	"${targetType}"
		, targetUuid:	"${targetUuid}"
		, isNew:		"${isNew}"
		, link:			"${link}"
		, auth:			"${auth}"
	});

</script>
