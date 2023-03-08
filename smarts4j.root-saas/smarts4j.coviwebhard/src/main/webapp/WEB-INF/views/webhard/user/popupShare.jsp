<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/userInclude.jsp"></jsp:include>

<body>
	<div class="popContent">
		<div class="top">
			<div class="top">
				<p class="WebHard_visit_title"><spring:message code='Cache.lbl_insertMember'/></p> <!-- 멤버 입력 -->
				<div class="autoCompleteCustom">
					<input type="text" id="shareMember" class="WebHard_visit_input" placeholder="<spring:message code='Cache.lbl_Mail_DirectInput'/>"> <!-- 직접입력 -->
					<a id="btnOrg" class="btnTypeDefault nonHover type01"><spring:message code='Cache.lbl_DeptOrgMap'/></a> <!-- 조직도 -->
				</div>
			</div>
		</div>
		<div>
			<div class="WebHard_visit_list_wrap">
				<ul id="shareList" class="WebHard_visit_list"></ul>
			</div>
		</div>
		<div class="bottom">
			<a id="btnConfirm" class="btnTypeDefault btnTypeBg"><spring:message code='Cache.btn_Confirm'/></a> <!-- 확인 -->
			<a id="btnCancel" class="btnTypeDefault"><spring:message code='Cache.lbl_Cancel'/></a> <!-- 취소 -->
		</div>
	</div>
</body>
<script>
(function(param) {
	/* 
		공유 멤버를 조회하는 조직도 정보
		covision.orgchart.js -> webhard_new.js -> popupShare.jsp 로 postMessage 전달 
	*/
	window.addEventListener('message', function(e) {
		if(e.data.functionName === 'toPopupShare') {
			e.data.param1 + OrgCallBack_SetShareMember(e.data.param2);
		}
	});
	
	var init = function() {
		setEvent(); // 버튼 이벤트 
		setAutoTags(); // 검색어 입력 시, 조직도를 클릭하지 않고 사용자 또는 부서를 검색할 수 있음
		setSharedMember(); // 해당 파일 또는 폴더의 공유자 목록을 불러옴
	};
	
	/* 버튼 이벤트 */
	var setEvent = function() {
		$("#btnOrg").off("click").on("click", function() { // 조직도
			orgChartPopup();
		});
		$("#btnConfirm").off("click").on("click", function() { // 확인
			saveShareMember();
		});
		$("#btnCancel").off("click").on("click", function() { // 취소
			Common.Close();
		});
	}
	
	/* 멤버 입력 창 */
	var setAutoTags = function(){
		 // parameters => (target, url, data)
		coviCtrl.setUserWithDeptAutoTags("shareMember", "/covicore/control/getAllUserGroupAutoTagList.do",
			{	
				labelKey : "Name",
				addInfoKey : "DeptName",
				valueKey : "Name",
				minLength : 1,
				useEnter : false,
				multiselect : false,
				select : function(event, ui) {
					if($("#shareList li[type='"+ui.item.Type+"'][code='"+ui.item.Code+"']").length <= 0) {
						addMember(ui.item.Code, ui.item.Name, ui.item.JobLevelName, ui.item.Type, "NEW", null,  ui.item.PhotoPath);
					} else{
						parent.Common.Warning("<spring:message code='Cache.msg_AlreadyRegisted'/>"); // 이미 등록되어있습니다
					}
				}
			}
		);
	};
	
	/* 공유자 목록 불러오기 */
	var setSharedMember = function(){
		$.ajax({
			url: "/webhard/user/shared/selectMember.do",
			type: "POST",
			data: {
				  "sharedType":	param.sharedType
				, "sharedID":	param.sharedID
			},
			success: function(result){
				if(result.status === "SUCCESS"){
					$(result.list).each(function(idx, obj){
						addMember(obj.sharedOwnerID, CFN_GetDicInfo(obj.sharedOwnerName), CFN_GetDicInfo(obj.sharedOwnerJobLevelName), obj.sharedGrantType, "OLD", null,obj.sharedOwnerPhotoPath);
					});
				}else{
					parent.Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
					Common.Close();
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/webhard/user/shared/selectMember.do", response, status, error);
			}
		});
	};
	
	/* 공유자 목록에 공유자 정보 html 태그 추가  */
	var addMember = function(pCode, pName, pJobLevelName, pType, pState, pDept, pPhotoPath){
		var sHTML = "";
		var photoPath;
		
		if(pPhotoPath !== null) {
			photoPath = coviCmn.loadImage(pPhotoPath);
		} else if(pDept !== null) {
			photoPath = coviCmn.loadImage(Common.getBaseConfig("ProfileImagePath").replace("{0}", pDept) + pCode + '.jpg');
		}

		sHTML += '<li code="'+pCode+'" type="'+pType+'" + state ="'+pState+'">';
		sHTML += '	<div class="tblParticipant">';
		
		if (pType=="UR"){
			sHTML += '		<img src="'+ photoPath + '" onerror="coviCmn.imgError(this, true)" alt="ProfilePhoto" style="width:30px;height:30px;border-radius:15px;">';
			sHTML += '		<span class="fcStyle">'+ pName +' '+ (pJobLevelName !== "undefined" && pJobLevelName !== null ? pJobLevelName : "") + '</span>';
		} else{
			sHTML += '		<span class="fcStyle">'+ pName +'</span>';
		}
		
		sHTML += '		<a  class="btn_visit_cancel" onclick="removeMember(this);"><spring:message code="Cache.btn_Cancel"/></a>'; /* 취소  */
		sHTML += '	</div>';
		sHTML += '</li>';
		
		$("#shareList").append(sHTML);
		
		// class 생성 시 이벤트 걸어줌.
		$(".btn_visit_cancel").off("click").on("click", function(){
			removeMember(this);
		});
		
	};
	
	// 21.09.27, addMember() 안에서 removeMember()를 사용해서 removeMember()의 scope를 var >> this.로 변경.
	this.removeMember = function(target){
		var targetObj = $(target).closest("li");
		
		if(targetObj.attr("state") === "OLD"){
			var arrSharedId = new Array();
			arrSharedId.push($(targetObj).attr("code")+"|"+$(targetObj).attr("type"));
			
			$.ajax({
				url: "/webhard/user/unshare.do",
				type: "POST",
				data: {
					  targetType: param.sharedType		// Folder / File
					, targetUuid: param.sharedID		// 공유대상 UUID
					, unsharedTo: arrSharedId.join(";")	// 공유할 부서 또는 사용자 ID
				},
				success: function(result){
					if(result.status !== "SUCCESS"){
						parent.Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
						Common.Close();
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/user/unshare.do", response, status, error);
				}
			});		
		}
		
		$(target).closest("li").remove();
	};
	
	//조직도 팝업 오픈
	var orgChartPopup = function(){
		var url = "/covicore/control/goOrgChart.do"
				+ "?type=D9"
				+ "&callBackFunc=OrgCallBack_SetShareMember"
				+ "&openerID=sharePopup";
		
		parent.Common.open("", "orgmap_pop", "<spring:message code='Cache.lbl_DeptOrgMap'/>", url, "1000px", "580px", "iframe", true, null, null, true); // 조직도
	};

	//조직도 콜백 함수(공유자 지정)
	var OrgCallBack_SetShareMember = function(data){
		var duplication = false; 
		var orgObj = $.parseJSON(data);
		
		$.each(orgObj.item,function(idx,obj){
			var type = obj.itemType.toUpperCase() == "USER" ? "UR" : (obj.GroupType.toUpperCase() == "Company" ? "CM" : "GR") ;
			var dept = obj.ETID;
			if($("#shareList li[type='"+type+"'][code='"+obj.AN+"']").length <= 0){
				addMember(obj.AN,CFN_GetDicInfo(obj.DN), type == "UR" ? CFN_GetDicInfo(obj.LV.split("&")[1], "") : "", type, "NEW", dept, null);
			}else{
				duplication = true; 
			}
		});
		
		if(duplication){
			parent.Common.Warning("<spring:message code='Cache.msg_DuplicateMembers'/>"); // 중복된 구성원이 있습니다. 중복된 구성원 제거 후 추가되었습니다.
		}
	};
	
	var saveShareMember = function(){
		var arrSharedId = new Array();
		
		$("#shareList li").each(function(idx, obj){
			if($(obj).attr("state") === "NEW"){
				arrSharedId.push($(obj).attr("code")+"|"+$(obj).attr("type"));
			}
		});
		
		if(arrSharedId.length < 1){
			Common.Close();
			
			// 21.11.10. 공유멤버가 없을 시, ajax 수행하지 않고 함수를 나옵니다.
			return;
		}
		
		$.ajax({
			url: "/webhard/user/share.do",
			type: "POST",
			data: {
				  targetType: param.sharedType		// Folder / File
				, targetUuid: param.sharedID		// 공유대상 UUID
				, sharedTo: arrSharedId.join(";")	// 공유할 부서 또는 사용자 ID
			},
			success: function(result){
				if(result.status === "SUCCESS"){
					parent.Common.Inform("<spring:message code='Cache.msg_Common_10'/>"); // 저장 되었습니다.
				}else{
					parent.Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>"); // 오류가 발생하였습니다.
				}
				
				Common.Close();
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/webhard/user/share.do", response, status, error);
			}
		});		
	};
	
	init();
})({sharedType: "${sharedType}", sharedID: "${sharedID}"});
</script>