//포탈 탭 설정값(P;M;A;B;)
var _mobile_portaltab;
var _mobile_portaltabarr;

//포탈 탭 터치이벤트 변수
var portalTouchX = 0;
var portalTouchPosX = 0;
var portalTouchTargetID = "";	// 현재화면 아이디
var portalTouchDirection = "";	// 이동방향
var portalTouchMoveWidth = 0;	// 이동거리
var portalTouchMoveAction = ""; // Action
var portalTouchMoveSeq = 0;		// 현재순번
var portalTouchMoveSeqNext = 0;	// 다음에 보일
var portalTouchMoveWidthorg = 0;

/**
 * _mobile_portal_Integrated - 통합알림
 */
var _mobile_portal_Integrated = {
	sApproval_Mnid : "",
	sApproval_Alias : "",
	mobileSupportBoardList: []
}

//가이드팝업 현재순번
var portalGuideTouchMoveSeq = 1; 




//포탈 홈(대메뉴)
$(document).on('pageinit', '#portal_home_page', function () {			
	if($("#portal_home_page").attr("IsLoad") != "Y"){
		$("#portal_home_page").attr("IsLoad", "Y");
		mobile_portal_HomeInit();
		// Android 새로고침 막기
		if(mobile_comm_isAndroidApp()) {
			window.covimoapp.SetPullToRefresh(false);
		}
	} 
});

//포탈 메인
$(document).on('pageinit', '#portal_main_page', function () {
	if($("#portal_main_page").attr("IsLoad") != "Y"){
		$("#portal_main_page").attr("IsLoad", "Y");
		sessionStorage.clear(); // 이거에 대해서 검토가 필요함 -- 페이지 이동이 팝업으로 바뀌면서 또 메인이 아니라 포탈이 초기 화면 임으로
		setTimeout("mobile_portal_MainInit()", 10);
		// Android 새로
		if(mobile_comm_isAndroidApp()) {
			window.covimoapp.SetPullToRefresh(false);
		}
	}
});

//통합알림
$(document).on('pageinit', '#portal_integrated_page', function () {
	if($("#portal_integrated_page").attr("IsLoad") != "Y"){
		$("#portal_integrated_page").attr("IsLoad", "Y");
		setTimeout("mobile_portal_IntergratedInit()", 10);
		if($("#portal_integrated_page").attr("data-role") == "dialog"){
			$("#portal_integrated_page").find(".topH_close").show();
		}
	}
});

//가이드팝업
$(document).on('pageinit', '#portal_guide_main', function () {
	$(".UpgradeGuide_wrap").css("height", winH + "px", "important");
	
	mobile_guide_bindTouch();
});







/*!
 * 
 * 포탈홈(대메뉴)
 * 
 */

//포탈 홈 초기화
function mobile_portal_HomeInit() {
	//sessionStorage.clear(); // 이거에 대해서 검토가 필요함 -- 페이지 이동이 팝업으로 바뀌면서 또 메인이 아니라 포탈이 초기 화면 임으로
}

//겸직 변경 Element 생성
function mobile_portal_createAddJobSelect(){
	$.ajax({
		type:"POST",
		data:{
		},
		url:"/groupware/privacy/getUserBaseGroupAll.do",
		async:false,
		success : function (data) {
			if(data.status == "SUCCESS"){
				if(data.list != undefined && data.list.length > 1){
					var divWrap = "";
					divWrap += "<div class=\"card_list_popup\">";
					divWrap += "	<div class=\"card_list_popup_cont\">";
					divWrap += "		<p class=\"card_list_title\">" + mobile_comm_getDic("btn_GeneralJobChanges") + "</p>";
					divWrap += "		<div class=\"card_list_radio_wrap\">";
					var curAddJobSeq = "";
					$.each(data.list, function(i, item){
						if(item.DeptCode==mobile_comm_getSession("DEPTID"))
							curAddJobSeq = item.Seq;
						divWrap += "		<div class=\"ui-radio\">";
						divWrap += "			<div class=\"ui-radio\">";
						divWrap += "				<label for=\"rdAddJob_"+item.Seq+"\" class=\"ui-btn ui-corner-all ui-btn-inherit ui-btn-icon-left ui-radio-off\">"+item.DeptName+ "(" + item.CompanyName + ")"+"</label>";
						divWrap += "				<input type=\"radio\" name=\"rdAddJob\" value=\""+item.Seq +"\" id=\"rdAddJob_"+item.Seq+"\">";						
						divWrap += "			</div>";
						divWrap += "		</div>";
					});
					divWrap += "		</div>";
					divWrap += "	</div>";
					divWrap += "	<div class=\"mobile_popup_btn\">";
					divWrap += "		<a href=\"javascript:mobile_portal_setAddJobInfo();\" class=\"g_btn03\">"+ mobile_comm_getDic("lbl_Confirm") + "</a>";
					divWrap += "		<a href=\"javascript: $('#mobile_home_addjobpop').hide();\" class=\"g_btn04\">"+ mobile_comm_getDic("lbl_Cancel") + "</a>";
					divWrap += "	</div>";
					divWrap += "</div>";
					$("#mobile_home_addjobpop").html(divWrap).trigger("create");
					$("#rdAddJob_"+curAddJobSeq).prop("checked",true).checkboxradio("refresh");
					if($("#btn_AddJobChange").length == 0) {
						$('#user_profile_dept_position').append("<a id='btn_AddJobChange' href=\"javascript:$('#mobile_home_addjobpop').show();\" class=\"btn_jobchange01\"><span>" + mobile_comm_getDic("btn_GeneralJobChanges") + "</span></a>");	
					}					
				}
			}
		},
		error : function(response, status, error){
			mobile_comm_ajaxerror("/groupware/privacy/getUserBaseGroupAll", response, status, error);
		}
	});
}

//겸직 변경
function mobile_portal_setAddJobInfo() {
	var pValue = $("[name='rdAddJob']").filter(":checked").val();
	mobile_portal_setMyInfo('ChangePosition', pValue);	
}

//사용자 정보 변경
function mobile_portal_setMyInfo(pType, pValue) {//체크	
	var sUrl = "/groupware/privacy/setMyInfo.do";
	
	$.ajax({
		url : sUrl,
		data : {
			"type": pType,
			"value": pValue
		},
		type : "post",
		async : false,
		success : function(res) {
			if (res.status == "SUCCESS") {
				mobile_comm_clearCache();
				coviMobileStorage.syncStorage();
				if("ChangePosition" == pType){
					$("#user_profile_dept_position").html("<span>" + mobile_comm_getSession("DEPTNAME") + "</span>" + mobile_comm_getSession("UR_JobTitleName"));
					setTimeout("mobile_portal_createAddJobSelect()", 1000);
					$('#mobile_home_addjobpop').hide();
					mobile_comm_go('/groupware/mobile/portal/main.do');		// 겸직 변형 후 모바일 메인으로 이동
				}else if("Theme" == pType){
					var urTheme = mobile_comm_getSession("UR_ThemeType"); 					
					$("#user_theme_color").attr("class",$("#user_theme_"+urTheme).attr("class"));
					$('.topH_colorS').hide();
					document.location.href = document.location.href; 
				}
			} else {
				alert(res.message);
			}
		},
		error:function(response, status, error){
			mobile_comm_ajaxerror(sUrl, response, status, error);
		}
	});
}






/*!
 * 
 * 포탈메인
 * 
 */

//포탈 메인 초기화
function mobile_portal_MainInit() {
	
	//변수초기화
	portalTouchMoveSeq = 0;
	
	//탭 사용여부에 따른 처리
	_mobile_portaltab = mobile_comm_getBaseConfig("MobileHomeTab");
	if(_mobile_portaltab.substring(_mobile_portaltab.length - 1) == ";") {
		_mobile_portaltab = _mobile_portaltab.substring(0, _mobile_portaltab.length - 1);
	}
	_mobile_portaltabarr = _mobile_portaltab.split(';');
	
	//상단탭
	for(var i = 0; i < _mobile_portaltabarr.length; i++) {
		if(_mobile_portaltabarr[i] != '') {
			var li = $('#portal_main_tab [tab=' + _mobile_portaltabarr[i] + ']');
			if(i == 0) {
				li.addClass('on');
			} else {
				li.removeClass('on');
			}
			li.show();
		}
	}
	$('#portal_main_tab').show();
	
	//탭 컨텐츠
	$('#portal_main_tabcontent [tab=' + _mobile_portaltabarr[0] + ']').show();
	
	//탭 컨텐츠 영역 터치 이벤트 바인딩
	mobile_portal_bindTouch();
	
	//탭 컨텐츠 로드	//포탈은 포탈 로드
	if(_mobile_portaltabarr[0] == "P") {
		mobile_portal_loadPortal();
	} else {
		mobile_Portal_ListDataLoad();
	}
	
	//가이드 팝업 및 음성인식 
	if(mobile_comm_getBaseConfig("UseMobileGuidePopup") == "Y"
			&& window.localStorage.getItem("MobileQuickGuideView") != "true") {
		$("#btnBottomMenuLayout").css("display","none"); //웹에서 호출 시 하단바 제거	
		mobile_comm_go("/groupware/mobile/portal/guide.do", "Y");
	} else {	
		// 음성 인식 사용여부 체크
		if(mobile_comm_isAndroidApp()) {
			try {
				var voiceuseage = window.covimoapp.RequestVoiceRecognitionUsageStatus();
				mobile_portal_voiceStatusCallBack(voiceuseage);
			} catch(e) {
				mobile_comm_log(e);
			}
		} else if(mobile_comm_isiOSApp()) {
			try {
				window.webkit.messageHandlers.callbackHandler.postMessage({ type:'covivoicerecognitionstatus', callback: 'mobile_portal_voiceStatusCallBack' }); 
			} catch(e) {
				mobile_comm_log(e);
			}
		} 
	}
}

// 텝전환 터치 이벤트
function mobile_portal_bindTouch(){
	
	$(".Plist_wrap")
	.off("touchstart,touchmove,touchend")
	.on('touchstart', function(e) {
		portalTouchPosX = e.originalEvent.targetTouches[0].pageX;
		portalTouchX = 0;
		portalTouchMoveAction = "";
		if(mobile_comm_isAndroidApp()) {
			window.covimoapp.SetPullToRefresh(false);
		}
	})
	.on('touchmove', function(e) {
		portalTouchX = e.originalEvent.targetTouches[0].pageX;
		portalTouchDirection = "Right";
		portalTouchMoveWidth = parseInt(portalTouchPosX - portalTouchX);
		portalTouchMoveWidthorg = portalTouchMoveWidth;
		if(portalTouchMoveWidth < 0) {
			portalTouchDirection = "Left";
			portalTouchMoveWidth = -portalTouchMoveWidth;
		}
		if(portalTouchMoveWidth > 90 && portalTouchMoveAction != "Action") {
			mobile_comm_disablescroll();
			if(portalTouchDirection == "Right") {
				portalTouchMoveSeqNext = portalTouchMoveSeq + 1;
			} else {
				portalTouchMoveSeqNext = portalTouchMoveSeq - 1;
			}
			// 이동할 대상이 있는 경우만
			if(portalTouchMoveSeqNext > -1 && portalTouchMoveSeqNext < _mobile_portaltabarr.length) {
				$("#portal_main_tabcontent").css("width", (winW * 2));
				$(".Plist").css("width", winW);
				$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeqNext]).show();
				portalTouchMoveAction = "Action";
			}
		} else if(portalTouchMoveAction == "Action") {
			if(portalTouchDirection == "Right") {
				$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeq]).css("margin-left", -portalTouchMoveWidth);
			} else {
				$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeqNext]).css("margin-left", -(winW - portalTouchMoveWidth) );
			}
		}
	})
	.on('touchend', function(e) {
		var width = parseInt(winW * 0.20);
		 
		 if(portalTouchX != 0 && portalTouchMoveAction == "Action") {
			 
			 if(portalTouchMoveWidth >= width) {
				 $(".Plist_tab li").removeClass("on");
				 $(".Plist_tab li[tab=" + _mobile_portaltabarr[portalTouchMoveSeqNext] + "]").addClass("on");
				 if(portalTouchDirection == "Right") {
					$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeq]).animate({marginLeft: -winW + "px"}, 150, function() {
						$(".Plist").hide();
						// 다음화면으로 이동
						$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeqNext]).show();
						portalTouchMoveSeq = portalTouchMoveSeqNext;
						// List 데이터 가져오기 이벤트 처리
						mobile_Portal_ListDataLoad();
					});
				} else {
					$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeqNext]).animate({marginLeft: "0px"}, 150, function() {
						$(".Plist").hide();
						// 다음화면으로 이동
						$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeqNext]).show();
						portalTouchMoveSeq = portalTouchMoveSeqNext;
						// List 데이터 가져오기 이벤트 처리
						mobile_Portal_ListDataLoad();
					});
				}
			 } else {
				// 원래 화면으로
				$("#divPortalList_" + _mobile_portaltabarr[portalTouchMoveSeq]).show();
			 }
		 }
		 
		 mobile_comm_enablescroll();
	});
}

//상단탭 클릭
function mobile_portal_SelectTab(obj) {
	
	var liParent = $(obj).parent();
	var selectTab = $(liParent).attr("tab");
	
	for(var i = 0; i < _mobile_portaltabarr.length; i++) {
		if(_mobile_portaltabarr[i] == selectTab) {
			portalTouchMoveSeq = i;
		}
	}
	
	$("#portal_main_tabcontent").css("width", "");
	$(".Plist").css({"width": "", "margin-left": ""});
	
	$(".Plist_tab li").removeClass("on");
	$(liParent).addClass("on");
	
	$("div.Plist").hide();
	$("#divPortalList_" + selectTab).show();
	
	mobile_Portal_ListDataLoad();
}

//탭컨텐츠 조회 호출
function mobile_Portal_ListDataLoad() {
	
	$("div[name='BtnPortalListMore']").hide();
	
	// 포탈 웹파트 로드
	if(portalTouchMoveSeq == 0) {
		//mobile_portal_loadPortal();
	// 받은메일 목록 조회
	} else if(_mobile_portaltabarr[portalTouchMoveSeq] == "M") {
		$("#portal_mail_currentPage").val("1");
		$("#portal_mail_endoflist").val("false");
		mobile_portal_mailList();
	// 미결문서 조회
	} else if(_mobile_portaltabarr[portalTouchMoveSeq] == "A") {
		$("#portal_approval_currentPage").val("1");
		$("#portal_approval_endoflist").val("false");
		mobile_portal_approvalList();
	// 최근게시 조회
	} else if(_mobile_portaltabarr[portalTouchMoveSeq] == "B") {
		$("#portal_board_currentPage").val("1");
		$("#portal_board_endoflist").val("false");
		mobile_portal_boardList();
	} 
}

//탭컨텐츠 더보기 호출
function mobile_portal_main_page_ListAddMore() {
	if(_mobile_portaltabarr[portalTouchMoveSeq] == "M") {
		if($("#portal_mail_endoflist").val() == "false") {
			$("#portal_mail_currentPage").val(Number($("#portal_mail_currentPage").val()) + 1);
			mobile_portal_mailList();
		}
		
	} else if(_mobile_portaltabarr[portalTouchMoveSeq] == "A") {
		if($("#portal_approval_endoflist").val() == "false") {
			$("#portal_approval_currentPage").val(Number($("#portal_approval_currentPage").val()) + 1);
			mobile_portal_approvalList();
		}
		
	} else if(_mobile_portaltabarr[portalTouchMoveSeq] == "B") {
		if($("#portal_board_endoflist").val() == "false") {
			$("#portal_board_currentPage").val(Number($("#portal_board_currentPage").val()) + 1);
			mobile_portal_boardList();
		}
		
	}
}

//탭컨텐츠 메일 목록 조회
function mobile_portal_mailList() {
	
	$("#portalMailListMore").hide();
	
	$.ajax({
		url:"/mail/userMail/selectUserMail.do",
		type:"POST",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify({
			"userMail" : mobile_comm_getSession("UR_Mail"),
			"mailBox" : "INBOX",
			"page" : $("#portal_mail_currentPage").val(),
			"type" : "MAILLIST",
			"type2" : "",
			"viewNum" : 10,
			"threadsRowNum" : null,
			"detailMap": null,
			"addSearchYn" : "N"
		}),
		success:function (data, res) {
			if(data[0].mailListLength > 0 && data[0].mailList != undefined) {
				
				var temp = "";
				var totalLengPage = Math.ceil(Number(data[0].mailListLength * 0.1));
				var currentPage = Number($("#portal_mail_currentPage").val());
				
				if(currentPage < totalLengPage || totalLengPage == 1){
					$("#portal_mail_currentPage").val(currentPage);
					$("#portalMailListMore").show();
				}else if(currentPage == totalLengPage){
					$("#portal_mail_currentPage").val(currentPage + 1);
					$("#portalMailListMore").hide();
				}else if(currentPage > totalLengPage){
					$("#portal_mail_currentPage").val(totalLengPage);
					$("#portalMailListMore").hide();
				}
				
				var cnt = data[0].mailList.length;
				for(var i = 0 ; i < cnt; i++){
					
					//읽음 표시
					var seenClass = "unread";
					if(data[0].mailList[i].flag.indexOf("\Seen") > -1){
						seenClass = "read";
					}
					
					//우선순위
					var priorityClass = ""
					if(data[0].mailList[i].mailPriority != ""){
						priorityClass = '<span class="flag_cr04"></span>';
					}

					var strDate = data[0].mailList[i].mailReceivedDateStr
					var uid = data[0].mailList[i].uid;
					var messageId = data[0].mailList[i].mailId;
					var mailbox = mobile_comm_convertCode(data[0].mailList[i].folderName);
					var folderType = data[0].mailList[i].folderTy;
					var references = decodeURIComponent(data[0].mailList[i].mailReferences);
					var mailSenderAddr = data[0].mailList[i].mailSenderAddr;
					
					temp += '<li id="liMailList_'+uid+'">'
					temp += '	<div class="PlistShell">'
					temp += '		<a href="#" name="portal_mailList_aMailList" class="Shell_link ui-link" onclick="mobile_portal_MailReadPageGo(this);"  data-mailtype="NORMAL" data-uid="'+uid+'" data-messageid="'+messageId+'" data-mailbox="'+mailbox+'" data-foldertype="'+folderType+'" data-references="'+references+'">'
					temp += '			<div class="staff_list">'
					temp += '				<div href="#" class="staff ui-link">'
					// 목록 아이콘 Class 구하기
					if(data[0].mailList[i].photoPath.length != 0 && data[0].mailList[i].photoPath[0].PhotoPath != ""){
						temp += '<span class="photo BGcolorDefault" style="background-image:url(\''+data[0].mailList[i].photoPath[0].PhotoPath+'\')"></span>';
					}else{
						temp += '<span class="photo '+ mobile_comm_getClassProfile(mailSenderAddr) +'" style="background-image:none">' + mobile_comm_convertCode(data[0].mailList[i].mailSender)[0] + '</span>'
					}
					temp += '				</div>'
					temp += '			</div>'
					temp += '			<div class="Plistcont">'
					temp += '				<p class="name '+seenClass+'"><span class="Pinname">'+mobile_mail_convertCode(data[0].mailList[i].mailSender)+'</span><span class="Pdate">'+mobile_comm_getDateTimeString2("LIST", CFN_TransLocalTime(CFN_TransServerTime(strDate, '', mobile_comm_getBaseConfig("MailTimeZoneValue"))))+'</span></p>'
					temp += '				<p class="title '+seenClass+'">'
					if(seenClass == "unread") {
						temp += '					<span class="Pnum">N</span>'	
					}
					if(data[0].mailList[i].att_yn == "Y"){
						temp += '				<span class="ico_file_clip"></span>'
					}
					temp += '					'+ priorityClass + mobile_comm_convertCode(data[0].mailList[i].subject) +'</p>'
					temp += '			</div>'
					temp += '		</a>'
					temp += '	</div>'
					temp += '</li>'
				}
				
				if($("#portal_mail_currentPage").val() == "1") {
					$("#ulPortalMailList").html(temp);
				} else {
					$("#ulPortalMailList").append(temp);
				}
			
				if (Math.min($("#portal_mail_currentPage").val() * 10, data[0].mailListLength) == data[0].mailListLength) {
					$("#portal_mail_endoflist").val("true");
					$('#portalMailListMore').hide();
				}			
			} else {
				$("#ulPortalMailList").html('<li class="mail_listnone" style="height: 500px;text-align: center;padding-top: 30px;">' + mobile_comm_getDic("CPMail_NoDataList") + '</li>');
				$("#portalMailListMore").hide();
				$("#portal_mail_endoflist").val("true");
			}
		},
		error: function (response, status, error){
			mobile_comm_ajaxerror("", response, status, error);			
		}
	});
}

//메일 상세보기
function mobile_portal_MailReadPageGo(pObj){
	var userMail	= mobile_comm_getSession("UR_Mail");
	var uid			= $(pObj).attr("data-uid");
	var messageId	= $(pObj).attr("data-messageid");
	var mailbox		= $(pObj).attr("data-mailbox");
	var folderType	= $(pObj).attr("data-foldertype");
	var references	= $(pObj).attr("data-references");
	$(pObj).find(".unread").removeClass("unread").addClass("read");
	
	// 메일 기본 세션 셋팅
	mobile_portal_initMailSession();
	
	// 현재 읽은 메일에 대한 sessionStorage Setting	
	window.sessionStorage.setItem("m_mailReadUserMail", userMail);
	window.sessionStorage.setItem("m_mailReadUid", decodeURIComponent(uid));
	window.sessionStorage.setItem("m_mailReadMessageId", decodeURIComponent(messageId));
	//window.sessionStorage.setItem("m_mailReadMailBox", decodeURIComponent(mailbox));
	window.sessionStorage.setItem("m_mailReadMailBox", mailbox);
	window.sessionStorage.setItem("m_mailReadFolderType", decodeURIComponent(folderType));
	window.sessionStorage.setItem("m_mailReadReferences", decodeURIComponent(references));
	
	if(window.sessionStorage.IsMContentsImageView == undefined) {
		mobile_mail_userMailDefaultInfo('Y');
	}
	
	mobile_mail_MailHomeChangePage("mail_divMailRead", true);
}

//탭컨텐츠 미결 목록 조회
function mobile_portal_approvalList(){
	
	$('#portalApprovalListMore').hide();
	var deptID = mobile_comm_getSession("GR_Code");
	
	if( mobile_comm_getSession("GR_Code") != mobile_comm_getSession("ApprovalParentGR_Code") && !!mobile_comm_getSession("ApprovalParentGR_Code")){
		deptID = mobile_comm_getSession("ApprovalParentGR_Code");
	}
	
	var url = "/approval/mobile/approval/getMobileApprovalListData.do";//userID=superadmin&deptID=1_RetireDept&mode=Approval&titleNm=&userNm=&pageNo=2&pageSize=10&searchType=all&searchWord=
	var paramdata = {
		userID: mobile_comm_getSession("USERID"),
		deptID: deptID,
		mode: "Approval",
		titleNm: "",
		userNm: "",
		pageNo: $("#portal_approval_currentPage").val(),
		pageSize: 10,
		searchType: "all",
		searchWord: ""
	};
	var listname = "";
	
	$.ajax({
		url: url,
		data: paramdata,
		type: "post",
		async: false,
		success: function (response) {
			if(response.status == "SUCCESS") {
				
				var apvTotalCount = response.page.listCount;
				var apvlist = response.list;

				var sHtml = "";
				var sUrl = "";
				var sEmer = "";
				var sSec = "";
				var sFile = "";
				var sSubject = "";
				var sRead = "";
				
				if(apvlist.length > 0) {
					$(apvlist).each(function (i, apvitem){
						var archived = "false";
						var g_mode = "Approval";
						var mode = "";
						var gloct = "";
						var subkind = "";
						var userID = "";
						var gotoUrl = "view";
						
						switch (g_mode){
							//개인함
							case "PreApproval" 		: mode = "PREAPPROVAL"; gloct = "PREAPPROVAL"; subkind="T010"; userID=apvitem.UserCode; break; // 예고함
							case "Approval" 		: mode = "APPROVAL"; gloct = "APPROVAL"; subkind=apvitem.FormSubKind; userID=apvitem.UserCode; break;    // 미결함
							case "Process" 			: mode = "PROCESS"; gloct = "PROCESS"; subkind=apvitem.FormSubKind; userID=apvitem.UserCode; break;		// 진행함
							case "Complete" 		: mode = "COMPLETE"; gloct = "COMPLETE"; subkind=apvitem.FormSubKind; archived="true"; userID=apvitem.UserCode; break;	// 완료함
							case "Reject" 			: mode = "REJECT"; gloct = "REJECT";  subkind=apvitem.FormSubKind; archived="true"; userID=apvitem.UserCode; break;		// 반려함
							case "TempSave" 		: mode = "TEMPSAVE"; gloct = "TEMPSAVE"; gotoUrl="write"; break;	// 임시함
							case "TCInfo" 			: mode = "COMPLETE"; gloct = "TCINFO"; subkind=apvitem.FormSubKind; break;		// 참조/회람함
							//부서함
							case "DeptComplete"		: mode = "COMPLETE"; gloct = "DEPART"; subkind="A"; archived="true"; userID = apvitem.UserCode; break; // 완료함
							case "SenderComplete" 	: mode = "COMPLETE"; gloct = "DEPART"; subkind="S"; archived="true"; userID = apvitem.UserCode; break;    // 발신함
							case "Receive" 			: mode = "REDRAFT"; gloct = "DEPART"; subkind=apvitem.FormSubKind; userID = apvitem.UserCode; break;		// 수신함
							case "ReceiveComplete" 	: mode = "COMPLETE"; gloct = "DEPART"; subkind="REQCMP"; archived="true"; userID = apvitem.UserCode; break;	// 수신처리함
							case "DeptTCInfo" 		: mode = "COMPLETE"; gloct = "DEPART"; subkind=apvitem.FormSubKind; userID = apvitem.ReceiptID; break;		// 참조/회람함
							case "DeptProcess" 		: mode = "PROCESS"; gloct = "DEPART"; subkind=apvitem.FormSubKind; userID = apvitem.UserCode; break;	// 진행함
						}
						
						sUrl = "/approval/mobile/approval/" + gotoUrl + ".do";
						sUrl += "?mode=" + mode;
						if(mode != "TEMPSAVE") {
							var processID = (apvitem.ProcessID == undefined ? apvitem.ProcessArchiveID : apvitem.ProcessID);
							processID = (processID == undefined ? '' : processID);
							var workitemID = (apvitem.WorkItemID == undefined ? apvitem.WorkitemArchiveID : apvitem.WorkItemID);
							workitemID = (workitemID == undefined ? '' : workitemID);
							var performerID = apvitem.PerformerID;
							performerID = (performerID == undefined ? '' : performerID);
							var processDescriptionID = (apvitem.ProcessDescriptionID == undefined ? apvitem.ProcessDescriptionArchiveID : apvitem.ProcessDescriptionID);
							processDescriptionID = (processDescriptionID == undefined ? '' : processDescriptionID);
							
							sUrl += "&processID=" + processID;
							sUrl += "&workitemID=" + workitemID;
							sUrl += "&performerID=" + performerID;
							sUrl += "&processdescriptionID=" + processDescriptionID;
						}
						sUrl += "&userCode=" + userID;
						sUrl += "&gloct=" + gloct;
						sUrl += "&formID=" + (apvitem.FormID == undefined ? '' : apvitem.FormID);
						sUrl += "&forminstanceID=" + apvitem.FormInstID;
						if(mode == "TEMPSAVE") {
							sUrl += "&formtempID=" + apvitem.FormTempInstBoxID;
							sUrl += "&forminstancetablename=" + apvitem.FormInstTableName;
							sUrl += "&open_mode=" + mode;
						}
						sUrl += "&archived=" + archived;
						sUrl += "&subkind=" + subkind;			
						sUrl += "&formPrefix=" + apvitem.FormPrefix;
						sUrl += "&isMobile=Y";
						sUrl += "&admintype=&usisdocmanager=true&listpreview=N";	
						sUrl += "&page=" + $("#portal_approval_currentPage").val();
						sUrl += "&totalcount=" + apvTotalCount;
						sUrl += "&listmode=" + g_mode;
						sUrl += "&searchtext=";
						
						sFile = "";
						sEmer = "";
						sSec = "";
						sRead = "read";
						
						if(apvitem.IsFile == "Y") {
							sFile = "<span class=\"ico_file_clip\"></span>";
						}
						if(apvitem.Priority == "5") {
							sEmer = "<span class=\"flag_cr01\">" + mobile_comm_getDic("lbl_apv_surveyUrgency") + "</span>"; //긴급
						}
						if(apvitem.IsReserved == "Y") {
							sEmer += "<span class=\"flag_cr01\">" + mobile_comm_getDic("lbl_apv_hold") + "</span>"; //보류
						}
						if(apvitem.IsSecureDoc == "Y") {
							sSec = "<i class=\"ico_secret\"></i>";
						}
						if(apvitem.ReadDate == "") {
							sRead = "unread";
						}
				
						sSubject = apvitem.FormSubject
						
						if((g_mode == "Approval" || g_mode == "Receive" || g_mode.indexOf("TCInfo") > -1) && (apvitem.ReadDate == null || apvitem.ReadDate == '0000-00-00 00:00:00'))
							sSubject = "<strong>" + sSubject + "</strong>";
						
						sHtml += '<li>'
						sHtml += '	<div class="PlistShell">'
						sHtml += '		<a  href=\"javascript:mobile_approval_goView(\'' + sUrl + '\', \'' + apvitem.FormPrefix + '\');\" class="Shell_link ui-link">'
						sHtml += '			<div class="staff_list">'
						sHtml += '				<div href="#" class="staff ui-link">'
						//sHtml += '					<span class="photo BGcolorDefault" style="background-image:url(\'' + apvitem.PhotoPath + '\'), url(\'' + _profileImagePath + 'noimg.png\');"></span>';
						//if( apvitem.PhotoPath.length != 0 &&  apvitem.PhotoPath != ""){
						//	sHtml += '<span class="photo BGcolorDefault" style="background-image:url(\''+ apvitem.PhotoPath+'\'), url(\'' + _profileImagePath + 'noimg.png\');"></span>';
						//}else{
							sHtml += '<span class="photo '+ mobile_comm_getClassProfile(apvitem.InitiatorID) +'" style="background-image:none">' + mobile_comm_convertCode(apvitem.InitiatorName)[0] + '</span>'
						//}
						sHtml += '				</div>'
						sHtml += '			</div>'
						sHtml += '			<div class="Plistcont">'
						sHtml += '				<p class="title '+sRead+'">'
						//sHtml += '					<span class="Pnum">N</span>'
						sHtml += 					sFile + sEmer + sSec + sSubject +'</p>';
						sHtml += '				<p class="list_info"><span class="point_cr">'+ apvitem.SubKind +'</span><span class="">'+apvitem.InitiatorName+'</span><span class="date">'+ mobile_comm_getDateTimeString2('list', CFN_TransLocalTime(apvitem.Created))+'</span><span>' + apvitem.FormName +'</span></p>'
						sHtml += '			</div>'
						sHtml += '		</a>'
						sHtml += '	</div>'
						sHtml += '</li>'
					});
				} else {
					sHtml += "<li style='height: 500px;text-align: center;padding-top: 30px;'><div class=\"no_list\">";
					sHtml += "    <p>" + mobile_comm_getDic("msg_NoDataList") + "</p>";//조회할 목록이 없습니다.
					sHtml += "</div></li>";
				}
				
				if($("#portal_approval_currentPage").val() == 1) {
					$('#ulPortalApprovalList').html(sHtml);
				} else {
					$('#ulPortalApprovalList').append(sHtml);
				}
				
				if (Math.min(Number($("#portal_approval_currentPage").val()) * 10, apvTotalCount) == apvTotalCount) {
					$("#portal_approval_endoflist").val("true");
	                $('#portalApprovalListMore').hide();
	            } else {
	                $('#portalApprovalListMore').show();
	            }
			}
		},
		error: function (response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});
}

//탭컨텐츠 게시목록 조회
function mobile_portal_boardList() {
	
	$('#portalBoardListMore').hide();
	
	var url = "/groupware/mobile/board/getBoardMessageList.do";
	var paramdata = {
		bizSection: "Board",
		menuID: mobile_comm_getBaseConfig("BoardMain"),
		boardType: "Total",
		viewType: "List",
		folderID: "",
		folderType: "",
		boxType: "Receive",
		searchText: "",
		page: $("#portal_board_currentPage").val(),
		pageSize: 10,
		categoryID: "",
		communityID: ""
	};
	
	$.ajax({
		url: url,
		data: paramdata,
		type: "post",
		success: function (response) {
			
			if(response.status == "SUCCESS") {
				
				var lTotalCnt = response.totalcount;
				var messagelist = response.list;
				
				var sHtml = "";
				var sUrl = "";
				
				if(messagelist.length > 0) {
					$(messagelist).each(function (i, message) {
						
						sUrl = "/groupware/mobile/board/view.do";
						sUrl += "?boardtype=" + paramdata.boardType;
						sUrl += "&folderid=" + message.FolderID;
						sUrl += "&page=" + paramdata.page;
						sUrl += "&searchtext=" + paramdata.searchText;
						sUrl += "&messageid=" + message.MessageID;
						sUrl += "&cuid=" + paramdata.communityID;
						sUrl += "&version=" + message.Version;
						sUrl += "&menucode=Board";
						
						var sRead = "read";
						if(message.IsRead != "Y") {
							 sRead = "unread";
						}
						
						var sFile = "";
						if(message.FileCnt > 0) {
							sFile = "<span class=\"ico_file_clip\"></span>";
						}
						
						sHtml += '<li>'
						sHtml += '	<div class="PlistShell">'
						sHtml += '		<a href=\"javascript: mobile_comm_go(\'' + sUrl + '\', \'Y\');\" class="Shell_link ui-link">'
						sHtml += '			<div class="staff_list">'
						sHtml += '				<div href="#" class="staff ui-link">'
						sHtml += '					<span class="photo '+ mobile_comm_getClassProfile(message.CreatorCode) +'" style="background-image:none">' + mobile_comm_convertCode(message.CreatorName)[0] + '</span>'
						sHtml += '				</div>'
						sHtml += '			</div>'
						sHtml += '			<div class="Plistcont">' 
						sHtml += '				<p class="title '+sRead+'">'+ sFile + message.Subject + '</p>'
						sHtml += '				<p class="list_info"><span class="point_cr">'+ message.FolderName +'</span><span class="">'+message.CreatorName+'</span><span class="date">'+  mobile_comm_getDateTimeString2('list', CFN_TransLocalTime(message.CreateDate)) +'</span></p>'
						sHtml += '			</div>'
						sHtml += '		</a>'
						sHtml += '	</div>'
						sHtml += '</li>'
					});
				} else {
					sHtml += "<li style='height: 500px;text-align: center;padding-top: 30px;'><div class=\"no_list\">";
					sHtml += "    <p>" + mobile_comm_getDic("msg_NoDataList") + "</p>";//조회할 목록이 없습니다.
					sHtml += "</div></li>";
				}
				
				if(paramdata.page == 1 || sHtml.indexOf("no_list") > -1) {
					$('#ulPortalBoardList').html(sHtml);
				} else {
					$('#ulPortalBoardList').append(sHtml);
				}
				
				if (Math.min((paramdata.page) * paramdata.pageSize, lTotalCnt) == lTotalCnt) {
					$("#portal_board_endoflist").val("true");
	                $('#portalBoardListMore').hide();
	            } else {
	                $('#portalBoardListMore').show();
	            }
			}
		},
		error: function (response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});
}



/*!
 * 
 * 포탈메인 - 웹파트 관련
 * 
 */

//포탈 load
function mobile_portal_loadPortal() {
	var oData = JSON.parse(JSON.stringify(_data));
	oData.sort(mobile_portal_sortFunc);
	
	$.each(oData, function(idx, value) {			
		try {
			if (parseInt(value.webpartOrder, 10) > 100) {
				setTimeout("mobile_portal_loadWebpart('" + JSON.stringify(value) + "')", parseInt(value.webpartOrder, 10));
			} else {
				mobile_portal_loadWebpart(value)
			}
		} catch(e) {
			mobile_comm_log(e);
		}
	});
}

function mobile_portal_sortFunc(a, b) {
	if(a.webpartOrder < b.webpartOrder) {
		return -1;
	} else if(a.webpartOrder > b.webpartOrder) {
		return 1;
	} else {
		return 0;
	}
}

function mobile_portal_loadWebpart(value) {
	
	if(typeof(value) === "string") {
		value = $.parseJSON(value);
	}
	
	var html = Base64.b64_to_utf8(value.viewHtml == undefined ? "" : value.viewHtml);
	
	$("#WP" + value.webPartID).html(html);
	$("#WP" + value.webPartID).attr("isLoad",'Y');
	
	if(value.jsModuleName != '' && typeof(new Function("return "+value.jsModuleName + ".webpartType ").apply()) != 'undefined') {
		new Function(value.jsModuleName + ".webpartType = " + value.webpartOrder).apply();
	}
	
	if(value.initMethod != '' && typeof(value.initMethod) != 'undefined') {
		if(typeof(value.data) == 'undefined') {
			value.data = $.parseJSON("[]");
		}
		
		if(typeof(value.extentionJSON) == 'undefined') {
			value.extentionJSON = $.parseJSON("{}");
		}

		let ptFunc = new Function('a', 'b', Base64.b64_to_utf8(value.initMethod)+'(a, b)');
		ptFunc(value.data, value.extentionJSON) ;
	}
}

























/*!
 * 
 * 포탈메인 - 음성인식 관련
 * 
 */


// 음성인식 기능 활성화
function mobile_portal_voiceStatusCallBack(pStatus) {
	if(mobile_comm_getSession('lang') == "ko" && pStatus) {
		
		$("#divportalVoiceListener").show();
		
		if(window.localStorage.getItem("mobileVoiceUse") == null || window.localStorage.getItem("mobileVoiceUse") == "true") {
		} else if(window.localStorage.getItem("mobileVoiceUse") == "false") {
			$("#divportalVoiceListener").removeClass("used").addClass("notused");
		}	
	}	
}

//음성인식 가이드창 열기
$(document).on('taphold', "#divportalVoiceListener", function(e) {
	$("#divVoiceGuidePop").show();
});

//음성인식 가이드창 닫기
function mobile_portalVoiceGuideClose() {
	$("#divVoiceGuidePop").hide();
}

//음성인식 호출
function mobile_portalVoiceCall() {
	if($.mobile.activePage.attr("id") == "portal_main_page" && mobile_comm_getSession('lang') == "ko") {
		if($("#divportalVoiceListener").hasClass("notused")) {
			$("#divportalVoiceListener").removeClass("notused").addClass("used")
		} else if($("#divportalVoiceListener").hasClass("used")) {
			$("#divportalVoiceListener").removeClass("used").addClass("act");
			// 음성인식 시작
			mobile_comm_callappvoicerecognition(mobile_portalVoiceCallBack);
		} else if($("#divportalVoiceListener").hasClass("act")) {
			$("#divportalVoiceListener").removeClass("act").addClass("used");

			mobile_VoiceCallback = null;
			
			// 음성인식 취소
			mobile_comm_callappstopvoicerecognition();
		}
	}
}

// 포탈음성인식 콜백
function mobile_portalVoiceCallBack(pResult) {
	$("#divportalVoiceListener").removeClass("act").addClass("used");
	var strVoiceText = "";
	if(pResult != "") {
		strVoiceText = pResult[0];
		if(strVoiceText.indexOf("안읽은") > -1 || strVoiceText.indexOf("안 읽은") > -1) {
			window.sessionStorage.setItem("VoiceUnreadMailRead", "true");
			mobile_comm_go("/mail/mobile/mail/List.do");
		} else if(strVoiceText.indexOf("메일") > -1 || strVoiceText.indexOf("Mail") > -1 || strVoiceText.indexOf("받은") > -1) {
			mobile_comm_go("/mail/mobile/mail/List.do");
		} else if(strVoiceText.indexOf("결재") > -1 || strVoiceText.indexOf("결제") > -1 || strVoiceText.indexOf("미결함") > -1 
			|| strVoiceText.indexOf("미결") > -1 || strVoiceText.indexOf("미련") > -1 || strVoiceText.indexOf("미 결") > -1 
			|| strVoiceText.indexOf("이겨라") > -1) {
			mobile_comm_go("/approval/mobile/approval/list.do");
		} else if(pResult.indexOf("중지") > -1 || pResult.indexOf("stop") > -1) {
			mobile_comm_callappstopvoicerecognition();
		} else if(strVoiceText.indexOf("사용자") > -1 || strVoiceText.indexOf("검색") > -1) {
			var strList = "";
			for(var i = 0; i < pResult.length; i++) {
				var searchUser = pResult[i].replace("사용자", "").replace("검색해 줘", "").replace("검색해줘", "").replace("검색", "").replace(" 죠", "").replace(" 조", "").replace(" ", "").replace(" ", "");
				strList += ("<li onclick='mobile_portalOrgUserSearch(\"{0}\")'>사용자<span class='voice_search_kw'> {0} </span>검색해 줘</li>").replace("{0}", searchUser).replace("{0}", searchUser);
			}
			if(strList != "") {
				$("#divViceMulti").show();
				$("#ulSearchText").html(strList);
			}
		} else if(strVoiceText.indexOf("음성") > -1 || strVoiceText.indexOf("명령") > -1 || strVoiceText.indexOf("비활성화") > -1) {
			$("#divportalVoiceListener").removeClass("used").addClass("notused");
			window.localStorage.setItem("mobileVoiceUse", "false");
			alert("자동 음성입력이 비활성화 됩니다.");
		} else {
			if(pResult == "**no_match" || pResult == "**no_speech") {
				// 리슨너 재호출
				setTimeout("mobile_portalVoiceCall()", 150);
			} else if(pResult == "**stop") {
				// alert("음성인식이 중지되었습니다.")
			} else {
				alert("지원하지 않는 기능입니다. - " +pResult);
				// 리슨너 재호출
				setTimeout("mobile_portalVoiceCall()", 150);
			}
		}
	}
}

//조직도 검색 
function mobile_portalOrgUserSearch(pSearchUser){
	window.localStorage.setItem("SearchOrgUser", pSearchUser);
	mobile_comm_go("/covicore/mobile/org/list.do?OrgSearch=UserSearch");
	$("#divViceMulti").hide();
}









/*!
 * 
 * 가이드팝업
 * 
 */

//모바일 가이드 페이지 닫기&시작하기 버튼 클릭 시
function portal_guide_close() {
	window.localStorage.setItem("MobileQuickGuideView", "true"); //localStorage의 MobileQuickGuideView 값을 읽음으로 처리함.
	
	mobile_comm_back();
	
	// 음성 인식 사용여부 체크
	if(mobile_comm_isAndroidApp()) {
		try{
			var voiceuseage = window.covimoapp.RequestVoiceRecognitionUsageStatus();
			mobile_portal_voiceStatusCallBack(voiceuseage);
		} catch(e){
			mobile_comm_log(e);
		}
	} else if(mobile_comm_isiOSApp()) {
		try{
			window.webkit.messageHandlers.callbackHandler.postMessage({ type:'covivoicerecognitionstatus', callback: 'mobile_portal_voiceStatusCallBack' });  
		} catch(e){
			mobile_comm_log(e)
		}
	} else {
		$("#btnBottomMenuLayout").css("display","block"); //웹에서 호출 시 하단바 조회
	}
}

//모바일 가이드 페이지 swipe 기능 구현
function mobile_guide_bindTouch(){
	var prevNextPage = "";
	var schTouchPosX = 0, schTouchX = 0, schTouchMoveWidth = 0;
	
	$(".UpgradeGuide_wrap")
		.off("touchstart,touchmove,touchend")
		.on('click', function(e) {
			var thisPage = "portal_guide_page" + portalGuideTouchMoveSeq;
			
			prevNextPage = mobile_guide_getPrevNextPage(portalGuideTouchMoveSeq, 'next');
			
			//PREV - 제일 첫 페이지, NEXT - 마지막 페이지가 아닐 경우에만
			if(prevNextPage != "0") {
				$("#" + thisPage).css("display", "none");
				$("#" + prevNextPage).css("display", "block");
			}
		})
		.on('touchstart', function(e) {    	 		     	 
			schTouchPosX = e.originalEvent.targetTouches[0].pageX;
			schTouchX = 0;
			if(mobile_comm_isAndroidApp()) {
				window.covimoapp.SetPullToRefresh(false);
			}
		})
		.on('touchmove', function(e) {         	 
			schTouchX = e.originalEvent.targetTouches[0].pageX;
			mobile_comm_disablescroll();
			schTouchMoveWidth = parseInt(schTouchPosX - schTouchX);
			if(parseInt(schTouchMoveWidth) > parseInt(winW/2)){
				schTouchMoveWidth = parseInt(winW/2);
			}
			//$("#sSlide_bar").css("margin-left", -(parseInt(schTouchMoveWidth)));
		})
		.on('touchend', function(e) {    	 
			 var left = parseInt(schTouchPosX - schTouchX);
			 var thisPage = "portal_guide_page" + portalGuideTouchMoveSeq;
			 
			 if(schTouchX != 0) {
				 if(left < 0 ){
					//if (-left >= width) {
						prevNextPage = mobile_guide_getPrevNextPage(portalGuideTouchMoveSeq, 'prev');
					// } 
				 }else{
					//if (left >= width) {
						prevNextPage = mobile_guide_getPrevNextPage(portalGuideTouchMoveSeq, 'next');
					// }
				 }
				 
				 //PREV - 제일 첫 페이지, NEXT - 마지막 페이지가 아닐 경우에만
				 if(prevNextPage != "0") {
					$("#" + thisPage).css("display", "none");
					$("#" + prevNextPage).css("display", "block");
				 }			 
			 } 
			 //$("#sSlide_bar").css("margin-left","");
			 mobile_comm_enablescroll();
		});
}

//뿌려준 div 페이지의 이전/다음 페이지 id값을 추출함
function mobile_guide_getPrevNextPage(pTargetID, pType) {
	var len = $("[id^=portal_guide_page]").length;	
	var idx = pTargetID;
	
	if(idx == "") idx = 0;
	
	if(pType == 'prev') {
		if(idx == 1) {
			return 0;
		} else {
			portalGuideTouchMoveSeq--;
			return "portal_guide_page" + (idx - 1);
		}
	} else {
		if(idx == len) {
			return 0;
		} else {
			portalGuideTouchMoveSeq++;
			return "portal_guide_page" + (idx + 1);
		}
	}
}




/*!
 * 
 * 통합알림
 * 
 */




// 통합알림 init
function mobile_portal_IntergratedInit(){
	
	// 목록 조회
	setTimeout(function () {
		mobile_portal_getIntergratedList();
    }, 100);
}

//통합알림 데이터 조회
function mobile_portal_getIntergratedList(){
	var url = "/covicore/quick/getIntegratedList.do";
	$.ajax({
		url: url,
	    type: "POST",
	    success:function(data){
			if(data.status=='SUCCESS'){
				mobile_portal_drawInteratedList(data.list);
			}
		 },
		 error:function(response, status, error){
			 //mobile_comm_ajaxerror(url, response, status, error);
		 }
	});
}

//통합알림 데이터 그리기
function mobile_portal_drawInteratedList(list){
	var sHtml = '';
	
	if(list.length > 0){
		$.each(list, function(idx, obj){
			sHtml +='<li>';
			sHtml +='<div class="txt_area inteTuchDiv" alrmID=\''+obj.AlarmID+'\' onclick="mobile_portal_clickInteratedAlarm(\''+obj.AlarmID+'\',\''+mobile_portal_changeURLForMobileView(obj.Category, obj.URL)+'\',\''+ obj.Category +'\')">';
			sHtml +='	<span class="cr_icon"><i class="'+ mobile_portal_getIntegratedClass(obj.Category) + '"></i></span>';
			sHtml +='	<div class="total_notice_chk">';
			sHtml +='	</div>';
			sHtml +='	<p class="desc">'+obj.Title+'</p>';
			sHtml +='	<p class="info">' + mobile_comm_getDicInfo(obj.PusherName) + ' <span>' +mobile_comm_getDateTimeString2("LIST", CFN_TransLocalTime(obj.ReceivedDate))+ '</span></p>'; //여기
			sHtml +='</div>';
			sHtml +='<div class="listswipe delete"><div class="delete_ico"></div></div>';
			sHtml +='</li>';
		});
		$("#portal_integrated_list").html(sHtml);
		
		var x;
		$(".inteTuchDiv")
			.on('touchstart', function(e) {
			    x = e.originalEvent.targetTouches[0].pageX;
			    mobile_TouthActtionWidth = parseInt($("body").width()/2);
			    mobile_TouchActionAction = "";
			    if(mobile_comm_isAndroidApp()) {
					window.covimoapp.SetPullToRefresh(false);
				}
			})
			.on('touchmove', function(e) {         	 
				var change = e.originalEvent.targetTouches[0].pageX - x;
				mobile_comm_disablescroll();
			    change = Math.min(Math.max(-mobile_TouthActtionWidth, change), 100); 		    
			    if(change <= 0) {
			    	if(parseInt(-change) > 20 && mobile_TouchActionAction == "" ) {
			    		 mobile_TouchActionAction = "Action";
			    		 mobile_comm_disablescroll();    		
			    	} else if(mobile_TouchActionAction == "Action") {
			    		e.currentTarget.style.marginLeft = change + 'px';	
			    	}
			    }
			})
			.on('touchend', function(e) {
				var left = parseInt(e.currentTarget.style.marginLeft);
			    var width = parseInt(e.currentTarget.clientWidth/2);
			    var new_left;		         
			    if (left <= -width) {
			    	// 실제 삭제 처리
			    	mobile_portal_deleteInteratedAlarmItem($(e.currentTarget).attr("alrmID"));
			    	$(e.currentTarget).animate({marginLeft: -parseInt(winW)}, 200, function () {$(e.currentTarget).parent().detach();});
			    } else {
			        new_left = '0px';
			        $(e.currentTarget).animate({marginLeft: new_left}, 200);
			    }
			    mobile_comm_enablescroll();
			});
		
	}else{
		sHtml += "<p>" + mobile_comm_getDic("msg_NoDataList") + "</p>";//조회할 목록이 없습니다.
		sHtml += "<ul id=\"portal_integrated_list\" style=\"display: none;\"></ul>";
		$("#portal_integrated_list").parent("div").removeClass().addClass("no_list").html(sHtml);
	}
}

//통합알림 데이터 클래스 찾기
function mobile_portal_getIntegratedClass(category){
	var retClass = '';
	
	if(category == undefined) {
		return '';
	}
	
	switch(category.toUpperCase()){
		case 'MAIL' :
			retClass = 'ico_task_mail';
			break;
		case 'APPROVAL' :
			retClass = 'ico_task_approval';
			break;
		case 'SCHEDULE' :
			retClass = 'ico_task_sch';
			break;
		case 'RESOURCE' :
			retClass = 'ico_task_res';
			break;
		case 'SURVEY' :
			retClass = 'ico_task_survey';
			break;
			break;
		case 'WORKREPORT' :
			retClass = 'ico_task_report';
			break;
		case 'COMMUNITY' :
			retClass = 'ico_task_comm';
			break;
		case 'CHECKLIST':
			retClass = 'ico_task_chk';
			break;
		case 'SOCIAL' : 
		case 'BOARD' :
		case 'TASK' :
			retClass = 'ico_task_board';
			break;
		case 'TIMESQUARE' :
		default:
			retClass = 'ico_task_docu';
			break;
			
	}
	return retClass;
}

//통합알림 클릭
function mobile_portal_clickInteratedAlarm(alarmID, pURL, category){
	if(pURL != '' && pURL != null &&  pURL != 'undefined') {
		if (pURL == 'notMobileSupported'){
			alert("모바일을 지원하지 않는 게시글입니다.");
		}
		else {
			var isPopUp = "N";
			switch (category.toLowerCase())
		    {
		        case "survey":
		        case "doc":
		        case "community":
		            break;
		        case "approval":
		        case "board":
		        case "resource":
		        case "schedule":
		        	isPopUp = "Y";
		            break;
		        case "mail":
			        var urlSearch = pURL.split('?')[1];
					var queryStr = JSON.parse('{"' + ((urlSearch) ? urlSearch.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') : '') + '"}');
					
					// 메일 기본 세션 셋팅
		        	mobile_portal_initMailSession();
					
					// 현재 읽은 메일에 대한 sessionStorage Setting	
					window.sessionStorage.setItem("m_mailReadUserMail", mobile_comm_getSession("UR_Mail"));
					window.sessionStorage.setItem("m_mailReadUid", decodeURIComponent(queryStr.uid));
					window.sessionStorage.setItem("m_mailReadMailBox", queryStr.mailbox);
					
		        	isPopUp = "Y";
		            break;
		        default:
		            break;
		    }
			mobile_comm_go(pURL, isPopUp);
		}
		
	} else {
		alert("연결된 페이지가 없습니다.");
	}
}

// 메일 기본 세션 셋팅
function mobile_portal_initMailSession(){
	window.sessionStorage.setItem("m_userMail", mobile_comm_getSession("UR_Mail"));
	window.sessionStorage.setItem("m_ismailusernm", mobile_comm_getSession("USERNAME"));
	window.sessionStorage.setItem("m_ismailuserid", mobile_comm_getSession("USERID"));
	window.sessionStorage.setItem("m_isMailDomainCode", mobile_comm_getSession("DN_Code"));
	window.sessionStorage.setItem("m_isMailUserCode", mobile_comm_getSession("UR_Code"));
	window.sessionStorage.setItem("m_isMailUserJobTitleNm", mobile_comm_getSession("UR_JobTitleName"));
}


// 통합알림 모바일용 url 생성
function mobile_portal_changeURLForMobileView(pCategory, pURL){
	
	var sRet = "";
	
	if(pURL != undefined && pURL != '') {
	
		switch (pCategory.toLowerCase())
	    {
	        case "survey":
	        	// 연결할 url의 쿼리 스트링 분석
		        var urlSearch = pURL.split('?')[1];
				var queryStr = JSON.parse('{"' + ((urlSearch) ? urlSearch.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') : '') + '"}');
				
				// 커뮤니티에서 등록한 설문인 경우, 커뮤니티 정보 전달
				if(queryStr.communityId != 'undefined' && queryStr.communityId != '' && queryStr.communityId != '0') {
					sRet = "/groupware/mobile/survey/list.do?cuid="+queryStr.communityId;
				}
				else {
					var reqType = mobile_portal_getQueryVariable(pURL, "reqType");
		            sRet = "/groupware/mobile/survey/list.do";
		            sRet += "?surveytype=" + reqType;
				}
	            
	            break;
	        case "doc":
	        	break;
	        case "resource":
	        	var l_eventid = mobile_portal_getQueryVariable(pURL, "eventID");
	        	var l_dateid = mobile_portal_getQueryVariable(pURL, "dateID");
	        	var l_repeatid = mobile_portal_getQueryVariable(pURL, "repeatID");
	        	var l_isrepeat = mobile_portal_getQueryVariable(pURL, "isRepeat");
	            var l_resourceid = mobile_portal_getQueryVariable(pURL, "resourceID");
	            sRet = "/groupware/mobile/resource/view.do";
	            sRet += "?eventid=" + l_eventid;
	            sRet += "&dateid=" + l_dateid;
	            sRet += "&repeatid=" + l_repeatid;
	            sRet += "&isrepeat=" + l_isrepeat;
	            sRet += "&resourceid=" + l_resourceid;
	            break;
	        case "approval":
	        	//sRet = location.protocol + "//" + location.hostname + ":" + location.port;
	        	var sPage = "/approval/mobile/approval/view.do";
	        	if(pURL.indexOf("ExpAppID") > 0){
	        		sPage = "/account/mobile/account/view.do";
	        		pURL = pURL.replace("ExpAppID=", "expAppID=");
	        		pURL += "&listmode=Approval";
	        	}
	        	sRet += pURL.substring(pURL.indexOf("/approval/") + 10).replace("approval_Form.do", sPage);
		        break;
	        case "board":
	        	var boardType = mobile_portal_getQueryVariable(pURL, "boardType");
	        	var messageID = mobile_portal_getQueryVariable(pURL, "messageID");
	        	var version = mobile_portal_getQueryVariable(pURL, "version");
	        	var folderID = mobile_portal_getQueryVariable(pURL, "folderID");
	            var cuid = mobile_portal_getQueryVariable(pURL, "cuid");
	            
	            //if((";" + folderID + ";").indexOf(_mobile_portal_Integrated) > -1) { //모바일 지원 게시판인 경우
	            if(mobile_portal_isMobileSupport(folderID) == "Y") { //모바일 지원 게시판인 경우
	            	if(pURL.indexOf("board_BoardView.do") > -1 || pURL.indexOf("goBoardViewPopup.do") > -1) {
		        		//sRet = location.protocol + "//" + location.hostname + ":" + location.port + "/groupware/mobile/board/view.do";
		        		sRet = "/groupware/mobile/board/view.do";
			            sRet += "?boardtype=" + boardType;
			            sRet += "&folderid=" + folderID;
			            sRet += "&messageid=" + messageID;
			            sRet += "&cuid=" + cuid;
			            sRet += "&version=" + version;
			            sRet += "&page=1&searchtext=";
		        	} else if (pURL.indexOf("goReplyPopup.do") > -1) {
		        		//sRet = location.protocol + "//" + location.hostname + ":" + location.port + "/covicore/mobile/comment/list.do";
		        		sRet = "/covicore/mobile/comment/list.do";
		        		sRet += "?targettype=Board";
		        		sRet += "&targetid=" + messageID + "_" + version;
		        	}
	            }
	            else {
					sRet = "notMobileSupported";
				}
	        	
	            break;
	        case "community":
	        	//sRet = location.protocol + "//" + location.hostname + ":" + location.port + "/groupware/mobile/community/portal.do?menucode=CommunityMain";
	        	sRet = "/groupware/mobile/community/portal.do?menucode=CommunityMain";
	            break;
	        case "schedule":
    			var eventID = mobile_portal_getQueryVariable(pURL, "eventID");
    			var dateID = mobile_portal_getQueryVariable(pURL, "dateID");
    			var isRepeat = mobile_portal_getQueryVariable(pURL, "isRepeat");
    			var folderID_s = mobile_portal_getQueryVariable(pURL, "folderID");
    			
    			//sRet = location.protocol + "//" + location.hostname + ":" + location.port + "/groupware/mobile/schedule/view.do";
    			sRet = "/groupware/mobile/schedule/view.do";
    			sRet += "?eventid=" + eventID;
    			sRet += "&dateid=" + dateID;
    			sRet += "&isrepeat=" + isRepeat;
	            sRet += "&folderid=" + folderID_s;
	            
	            break;
	        // 업무관리 - https://gw4j.covision.co.kr/groupware/mobile/task/view.do?taskid=72&folderid=52&isowner=Y&ismine=Y
	        case "mail":
	        	// 연결할 url의 쿼리 스트링 분석
		        var urlSearch = pURL.split('?')[1];
				var queryStr = JSON.parse('{"' + ((urlSearch) ? urlSearch.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') : '') + '"}');
				
				sRet = "/mail/mobile/mail/Read.do";
				sRet += "?uid=" + queryStr.uid;
    			sRet += "&mailbox=" + queryStr.folderNm;
				
	        	break;
	        default:
	            break;
	    }
       
	}
	
    return sRet;
}

//쿼리값 가져오기
function mobile_portal_getQueryVariable(pUrl, pVariable) {
    var query = pUrl.substring(pUrl.indexOf("?") + 1);
    var vars = query.split('&');
    var cnt = vars.length;
    for (var i = 0; i < cnt; i++) {
        var pair = vars[i].split('=');
        if (decodeURIComponent(pair[0]) == pVariable) {
            return decodeURIComponent(pair[1]);
        }
    }
    return "";
}

//통합알림 cnt 초기화
function mobile_portal_setInteratedCntInit(){
	
	$("ul.my_link li[type=integrated]").removeClass("new");
	
	var menuListStr = "Integrated;";
	var url = "/groupware/longpolling/getQuickData.do";
	
    $.ajax({ 
    	type : "POST",
		url: url,
		data : { "menuListStr" : menuListStr },
		success: function(data){ 
			if(data.countObj.Integrated > 0) {
				$("ul.my_link li[type=integrated]").addClass("new");
			}
			else {
				$("ul.my_link li[type=integrated]").removeClass("new");
			}
		}, 
		error:function(response, status, error){
			$("ul.my_link li[type=integrated]").removeClass("new");
			//mobile_comm_ajaxerror(url, response, status, error);
		},
		dataType: "json"
    });
}

//알림 cnt 조회
function mobile_home_setCntInit(pObjId){
	
	var url = "/groupware/longpolling/getQuickData.do";
	var menuListStr = "";	
	
	$("#" + pObjId).children("li").each(function(idx,obj){
		$(obj).find("span.mcnt").hide(); //전체 Cnt 숨김 처리
		var biz = $(obj).attr("biz"); //bizsection 값 구함
		var menuId = $(obj).attr("menuid"); // 알림카운트를 구분해서 가져와야 할 경우의 시스템 아이디
		if(biz != undefined && biz != "") {
			if(menuId != undefined && menuId != "" ){
				menuListStr += (biz +"|"+ menuId +";"); //값이 있는 경우 menuListStr에 추가
			} else {
				menuListStr += (biz +";"); //값이 있는 경우 menuListStr에 추가	
			}			
		}
	});
	
	menuListStr += "Integrated;";
	
	$.ajax({ 
	  	type : "POST",
		url: url,
		data : { "menuListStr" : menuListStr },
		success: function(data){ 
			for(var strKey in data.countObj) {
				var cnt = parseInt(data.countObj[strKey]);
				if(cnt > 0) {
					if(strKey.indexOf("|") > -1){
						$("#" + pObjId + " li[biz='" + strKey.split("|")[0] + "'][menuid='" + strKey.split("|")[1] + "']").find("span.mcnt").text(cnt).show();	
					} else {
						$("#" + pObjId + " li[biz='" + strKey + "']").find("span.mcnt").text(cnt).show();
					}
				}
				
				if(strKey == "Integrated") {
					if(cnt > 0) {
						$("ul.my_link li[type=integrated]").addClass("new");
					} else {
						$("ul.my_link li[type=integrated]").removeClass("new");
					}
				}
			}
		}, 
		error:function(response, status, error){
			//mobile_comm_ajaxerror(url, response, status, error);
		},
		dataType: "json"
	});
}

//새로고침
function mobile_portal_clickrefresh() {
	mobile_portal_getIntergratedList();
}

//통합 알람 전체 지우기
function mobile_portal_deleteInteratedAlarm(){
	if(confirm(mobile_comm_getDic("msg_TodoList_02"))){ //모두 삭제하시겠습니까?
		var sUrl = "/covicore/quick/deleteAllAlarm.do";
		$.ajax({
			url: sUrl,
			type: "POST",
			success:function(data) {
				if(data.status=="SUCCESS"){
					mobile_portal_IntergratedInit();
					mobile_portal_setInteratedCntInit();
				}					
			},
			error:function(response, status, error){
				mobile_comm_ajaxerror(sUrl, response, status, error);
			}
		});
	}else{
		return false;
	}
}

function mobile_portal_deleteInteratedAlarmItem(pID){
	var sUrl = "/covicore/quick/deleteEachAlarm.do";
	$.ajax({
		url: sUrl,
		type: "POST",
		data : { "deleteID" : pID },
		success:function(data) {
//			if(data.status=="SUCCESS"){
//			}					
		},
		error:function(response, status, error){
			mobile_comm_ajaxerror(sUrl, response, status, error);
		}
	});
}

	
function mobile_portal_isMobileSupport(folderID){
	var isMobileSupport = 'N';
	$.ajax({
		url: "/groupware/mobile/board/selectBoardIsMobileSupport.do",
		type: "POST",
		async: false,
		data : { folderID: folderID },
		success:function(data) {
			isMobileSupport = data.isMobileSupport;	
		},
		error:function(response, status, error){
			mobile_comm_ajaxerror(sUrl, response, status, error);
		}
	});
	return isMobileSupport;
}













/*
	portal.js END
*/



