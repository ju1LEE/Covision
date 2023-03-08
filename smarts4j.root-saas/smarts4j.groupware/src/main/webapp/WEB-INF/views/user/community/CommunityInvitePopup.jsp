<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>

<link rel="stylesheet" id="cCss" type="text/css" /> 
<link rel="stylesheet" id="cthemeCss" type="text/css" /> 

<style>
	.taskFolderAdmin .autoCompleteCustom .ui-autocomplete-multiselect.ui-state-default { width: calc(100% - 77px)!important; }
</style>

<input type="hidden" id="inviteCode" value=''/>
<div class="divpop_contents">
	<div class="popContent">
		<!-- 팝업 내부 시작 -->
		<div class="taskPopContent taskFolderAdmin" style="padding: 0;">
			<div class="middle">
				<div class="inputBoxSytel01 type03">
					<div><span><spring:message code='Cache.lbl_To'/></span></div>
					<div>
						<div class="autoCompleteCustom">
							<input id="myAutocompleteMultiple" type="text" class="ui-autocomplete-input" style="width: calc(100% - 82px) !important;" autocomplete="off">
							<a class="btnTypeDefault" onclick="inviteMember();return false;" style="height: 31px; line-height: 30px;"><spring:message code='Cache.lbl_To'/></a>
						</div>
					</div>
				</div>
				<div class="inputBoxSytel01 type03">
					<div><span><spring:message code='Cache.lbl_subject'/></span></div>
					<div>
						<input type="text" id="inviteTitle" style="width: 100%;">
					</div>
				</div>
				<div class="inputBoxSytel01 type03">
					<div><span><spring:message code='Cache.lbl_InvitationMessage'/></span></div>
					<div>
						<textarea id="inviteMsg" kind="write" style=""></textarea>
					</div>
				</div>
			</div>
			<div class="bottom mt20" style="text-align: center;">
				<a name="registBtn" onclick="goSendInviteMsg()" class="btnTypeDefault btnTypeBg" style=""><spring:message code='Cache.btn_Mail_Send'/></a> 
				<a name="cancelBtn" onclick="Common.Close(); return false; " class="btnTypeDefault " style=""><spring:message code='Cache.btn_Cancel'/></a>
			</div>
		</div>
		<!-- 팝업 내부 끝 -->
	</div>
</div>

<script type="text/javascript">

// 자동완성 옵션
var autoInfos = {
	labelKey : 'UserName',
	valueKey : 'UserCode',
	minLength : 1,
	useEnter : false,
	multiselect : true,
};

coviCtrl.setCustomAjaxAutoTags('myAutocompleteMultiple', '/covicore/control/getAllUserAutoTagList.do', autoInfos);
//$('#myAutocompleteMultiple').parent().css('width', 'calc(100% - 89px)');

$(function(){
	if (parent && parent.communityID != '' && parent.communityCssPath != '' && parent.communityTheme != ''){
		$("#cCss").attr("href",parent.communityCssPath + "/community/resources/css/community.css");
		$("#cthemeCss").attr("href",parent.communityCssPath + "/covicore/resources/css/theme/community/"+parent.communityTheme+".css");
	}
	else {
		$("#cCss, #cthemeCss").remove();
	}
})

function goSendInviteMsg(){
	if(!$('.ui-autocomplete-multiselect-item').length > 1){
		Common.Warning("<spring:message code='Cache.msg_recipients'/>");
		return;
	}
	
	if($("#inviteTitle").val() == '' || $("#inviteTitle").val() == null){
		Common.Warning("<spring:message code='Cache.msg_028'/>");
		return;
	}
	
	if($("#inviteMsg").val() == '' || $("#inviteMsg").val() == null){
		Common.Warning("<spring:message code='Cache.msg_InviteMessage'/>");
		return;
	} 
	
/* 	var text = $("#inviteMsg").val().split(' ').join('&nbsp;'); //replaceAll() 함수 효과
	text = text.replace(/(\r\n|\n|\n\n)/gi, '<br />'); */
	
	var text = $("#inviteMsg").val().replace(/(\r\n|\n|\n\n)/gi, '<br />');
	Common.Confirm("커뮤니티 초대 메시지를 보내겠습니까?" , "Confirmation Dialog", function (confirmResult) {
		if (confirmResult) {
			$.ajax({
				url: "/groupware/layout/communitySendSimpleMail.do",
				type: "POST",
				data: {
					userCode: getReceiverData()
					,subject: $('#inviteTitle').val()
					,bodyText: text
					,cid : parent.cID
				},
				async: false,
				success: function (res) {
					if(res.status == "SUCCESS"){
						Common.Inform("<spring:message code='Cache.msg_SentMDM'/>", "Information Dialog", function(result){ //정상적으로 발송되었습니다.
							Common.Close();
							return false;
						});
					}else{
						Common.Error("<spring:message code='Cache.msg_FailedToSend'/>"); //발송에 실패하였습니다.
					}
				},
				error:function(response, status, error){
					CFN_ErrorAjax("/covicore/control/sendSimpleMail.do", response, status, error);
				}
			});
		}
	});
}

//var aStrDictionary = Common.getDicAll(["lbl_ACL_Allow","lbl_ACL_Denial","lbl_User","lbl_company","lbl_group"]);
//var lang = Common.getSession("lang");

function inviteMember(){
/* 	if(opener){
		CFN_OpenWindow("/covicore/control/goOrgChart.do?callBackFunc=depUserManage_CallBack&strType=D1","조직도",1000,580,"");
	}else{
		parent.Common.open("","orgmap_pop","조직도","/covicore/control/goOrgChart.do?callBackFunc=depUserInvite_CallBack&type=D1","1000px","580px","iframe",true,null,null,true);
	} */

	parent.Common.open("","orgmap_pop","<spring:message code='Cache.lbl_DeptOrgMap'/>","/covicore/control/goOrgChart.do?callBackFunc=depUserManage_CallBack&type=B9&treeKind=Group&groupDivision=Basic","1000px","580px","iframe",true,null,null,true);
}

/*function depUserManage_CallBack(orgData){
 	var readAuthJSON =  $.parseJSON(orgData);
	var sCode = "";
	var sDisplayName = "";
	var sObjectType_A = "";
	var sHTML = "";
	var step = $("[name=readAuth]").length;
	var sObjectType = "";
	$(readAuthJSON.item).each(function (i, item) {
		sObjectType = item.itemType
  		if(sObjectType.toUpperCase() == "USER"){ //사용자
  			sObjectTypeText = aStrDictionary["lbl_user"]; // 사용자
  			sObjectType_A = "UR";
  			sCode = item.AN;//UR_Code
  			sDisplayName  = CFN_GetDicInfo(item.DN, lang);
  			sDNCode = item.ETID;; //DN_Code
  		}else{ //그룹
	  			switch(item.GroupType.toUpperCase()){
	 			 case "COMPANY":
					sObjectTypeText = aStrDictionary["lbl_company"]; // 회사
					sObjectType_A = "CM";
					break;
				case "JOBLEVEL":
					//sObjectTypeText = "직급";
					//sObjectType_A = "JL";
					//break;
				case "JOBPOSITION":
					//sObjectTypeText = "직위";
					//sObjectType_A = "JP";
					//break;
				case "JOBTITLE":
					//sObjectTypeText = "직책";
					//sObjectType_A = "JT";
					//break;
				case "MANAGE":
					//sObjectTypeText = "관리";
					//sObjectType_A = "MN";
					//break;
				case "OFFICER":
					//sObjectTypeText = "임원";
					//sObjectType_A = "OF";
					//break;
				case "DEPT":
					sObjectTypeText = aStrDictionary["lbl_group"]; // 그룹
					//sObjectTypeText = "부서";
					sObjectType_A = "GR";
					break;
			}
	  			sCode = item.AN;
				sDisplayName = CFN_GetDicInfo(item.DN, lang);
				sDNCode = item.ETID;
  		}
	});
	
	$("#inviteMember").val(sDisplayName);
	$("#inviteCode").val(sCode);
}*/

function inviteMsgReset(){
	$("#inviteCode").val('');
	$("#inviteMember").val('');
	$("#inviteTitle").val('');
	$("#inviteMsg").val('');
}

//수신자 목록 jsonstring으로 변경
function getReceiverData(){
	var userArray = [];
	
	//subject 추출
	$('.ui-autocomplete-multiselect-item').each(function () {
		var receiver = new Object();
		receiver.Type = $(this).attr("data-type")==undefined?"UR":$(this).attr("data-type");
		receiver.Code = $(this).attr("data-value");
		userArray.push(receiver);
	});
	
	return JSON.stringify(userArray);
}

</script>