<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page import="egovframework.baseframework.util.SessionHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); 
	String portalOption = egovframework.covision.groupware.portal.user.web.PNPortalCon.getUserPortalOption();
	String isDark = "";
	String useLeft = "";
	String isActiveMyContents = "";
	
	if(portalOption != null && !portalOption.equals("")){
		org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
		Object obj = parser.parse(portalOption);
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) obj;
		
		isDark = json.get("isDark") == null ? "N" : (String) json.get("isDark");
		useLeft = json.get("useLeft") == null ? "N" : (String) json.get("useLeft");
		isActiveMyContents = json.get("isActiveMyContents") == null ? "N" : (String) json.get("isActiveMyContents");
	}else{
		isDark = "N";
		useLeft = "Y";
		isActiveMyContents = "N";
	}
	
	pageContext.setAttribute("isDark", isDark);
	pageContext.setAttribute("useLeft", useLeft);
	pageContext.setAttribute("isActiveMyContents", isActiveMyContents);
	
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();	//포탈페이지의 정적 스크립트 버전관리 하지 않아 추가
	String assignedBizSection = SessionHelper.getSession("UR_AssignedBizSection");
%>

${incResource}

<script type="text/javascript" src="<%=cssPath%>/customizing/ptype05/js/jcarousellite_1.0.1.pack.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/waypoints/2.0.3/waypoints.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="<%=cssPath%>/customizing/ptype05/js/jquery.counterup.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="<%=cssPath%>/customizing/ptype05/js/packery.pkgd.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/approval/resources/script/user/approvestat.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/ExControls/Chart.js-master/Chart.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/mail/resources/script/cmmn/cmmn.variables.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/mail/resources/script/cmmn/mail.api.js<%=resourceVersion%>"></script>

<script>
const gwPortal = {};
	function setDarkMode(){
		var isDark = "<%= isDark %>";
		
		if(isDark == "Y"){
			$("#wrap").addClass("PN_darkmode");
			
			$(window).load(function(){
				$(".PN_popMode").show();
				$(".PN_btnLite").show();
				$(".PN_btnDark").hide();
				
				// 다크모드 버튼 이벤트
				$(".PN_btnDark").click(function(){
					$(this).parents("#wrap").addClass("PN_darkmode");
					$(this).hide();
					$(".PN_btnLite").show();
					saveUserPortalOption();
				});
				
				$(".PN_btnLite").click(function(){
					$(this).parents("#wrap").removeClass("PN_darkmode");
					$(this).hide();
					$(".PN_btnDark").show();
					saveUserPortalOption();
				});
			});
		}else{
			$("#wrap").removeClass("PN_darkmode");
			
			$(window).load(function(){
				$(".PN_popMode").show();
				$(".PN_btnLite").hide();
				$(".PN_btnDark").show();
				
				// 다크모드 버튼 이벤트
				$(".PN_btnDark").click(function(){
					$(this).parents("#wrap").addClass("PN_darkmode");
					$(this).hide();
					$(".PN_btnLite").show();
					saveUserPortalOption();
				});
				
				$(".PN_btnLite").click(function(){
					$(this).parents("#wrap").removeClass("PN_darkmode");
					$(this).hide();
					$(".PN_btnDark").show();
					saveUserPortalOption();
				});
			});
		}
	}
	
	setDarkMode();
</script>

<div id="portal_con" class="commContRight mainContainer">${layout}</div>

<script type="text/javascript">
	${javascriptString}
	
	var access = '${access}' 
	var _portalInfo = '${portalInfo}';
	var _data = ${data};
	var cssPath = "<%=cssPath%>";
	var g_pageNum = 1; // 메일 스크롤 페이징 변수
	var g_isActiveMyContents = '${isActiveMyContents}';
	var g_useLeft = '${useLeft}';
		
	//ready 
	$(window).load(function(){
		initPortal();
	})
	
	function initPortal(){
		// 히스토리 추가
		var url  = location.href;
		var state = CoviMenu_makeState(url); 
		history.pushState(state, url, url);
		CoviMenu_SetState(state)
		
		if(access == 'SUCCESS'){
			loadPortal();
		}else if(access == 'NOT_AHTHENTICATION'){
			var sHtml = '';
			sHtml += '<div class="errorCont">';
			sHtml += '	<h1>포탈 접속 실패</h1>';
			sHtml += '	<div class="bottomCont">';
			sHtml += '		<p class="txt">해당 포탈에 접속 권한이 없습니다.<br>관리자에게 문의 바랍니다.</p>';
			sHtml += '		<p class="copyRight mt30">Copyright 2017. Covision Corp. All Rights Reserved.</p>';
			sHtml += '	<div/>';
			sHtml += '<div/>';
			
			$("#portal_con").html(sHtml);
		}
	}
	
	function loadPortal(){
		var oData = _data;
		
		oData.sort(sortFunc);
		
		$.each(oData, function(idx, value){
			
			var jsMethod = Base64.b64_to_utf8(value.initMethod);
			var methodType = typeof window[jsMethod];		
			try{
				if(parseInt(value.webpartOrder, 10) > 100){					
					setTimeout("loadWebpart('" + JSON.stringify(value) + "','"+methodType+"')", parseInt(value.webpartOrder, 10));
				}else{
					loadWebpart(value, methodType)
				}
			}catch(e){
				coviCmn.traceLog(e);
				$("#WP"+value.webPartID+" .webpart").css({"border": "2px solid red"});
			}		
		});
		
		setDefaultEvent();
		
		// 이벤트 바인딩
		setEvent();

		
		// 전자결재
		//setApvCnt();
		//getApprovalList();
		
		// 일정
		//setToday();
		
		// 내업무
// 		$.ajax({
// 			url: "/groupware/biztask/getAllMyTaskList.do",
// 			type: "GET",
// 			data: {
// 				"userCode" : sessionObj["USERID"],
// 				"stateCode" : ""
// 			},
// 			success: function(data){				
// 				var listData = data.ProjectTaskList.concat(data.TaskList);				
// 				listData.length > 0 && $(".PN_tabList .PN_tab1_5 .countStyle").text(listData.length);
// 			},
// 			error: function(response, status, error){
// 				CFN_ErrorAjax("/groupware/biztask/getAllMyTaskList.do", response, status, error);
// 			}
// 		});
// 		// 스크롤
 		setScrollEvent(); 
	}
	
	function loadWebpart(value, pMethodType){
		if(typeof(value) === "string"){
			value = $.parseJSON(value);
		}
		
		var html = Base64.b64_to_utf8(value.viewHtml==undefined?"":value.viewHtml);
		
		// 포탈 개선 My Place active 표시 여부
		if(html.indexOf("PN_myContents") > -1 && html.indexOf("PN_myBtn")){
			if(g_isActiveMyContents && g_isActiveMyContents == "Y"){
				html = html.replace("PN_myContents", "PN_myContents active");
				html = html.replace("PN_myBtn", "PN_myBtn active");
			}
		}
		
		if (html != ''){
			//default view로 인한 분기문
			if($("#WP"+value.webPartID).attr("isLoad")=='Y'){
				$("#WP"+value.webPartID).append(html);
			}else{
				$("#WP"+value.webPartID).html(html);
				$("#WP"+value.webPartID).attr("isLoad",'Y');
			}
		}
		
		if(pMethodType == 'function'){			// 웹파트 스크립트 및 소스를 재사용하기 위한 코드 추가. 전역변수 gwPortal에 pMethodType이 함수인경우 실행후 반환되는 객체를 등록.
			gwPortal[value.webPartID] =  window[Base64.b64_to_utf8(value.initMethod)].call(this, value.data, value.extentionJSON, 'portal', value.webPartID);
		}
		else {
			if(value.jsModuleName != '' && typeof window[value.jsModuleName] != 'undefined' && typeof(new Function ("return "+value.jsModuleName+".webpartType").apply()) != 'undefined'){
				new Function (value.jsModuleName+".webpartType = "+ value.webpartOrder).apply();
			}
			
			if(value.initMethod != '' && typeof(value.initMethod) != 'undefined'){
				if(typeof(value.data)=='undefined'){
					value.data = $.parseJSON("[]");
				}
				
				if(typeof(value.extentionJSON) == 'undefined'){
					value.extentionJSON = $.parseJSON("{}");
				}
				
				let ptFunc = new Function('a', 'b', Base64.b64_to_utf8(value.initMethod)+'(a, b)');
				ptFunc(value.data, value.extentionJSON);
			}
		}
	}
	
	function sortFunc(a, b) {
		if(a.webpartOrder < b.webpartOrder){
			return -1;
		}else if(a.webpartOrder > b.webpartOrder){
			return 1;
		}else{
			return 0;
		} 
	}
	 
	function setDefaultEvent() {
// 		if (_portalInfo == "") return;
		
// 		var sLayoutType = $.parseJSON(_portalInfo).LayoutType;
// 		switch (sLayoutType) {
// 			case "8": 	//1행2열 데모용
// 			case "7":   // 1행3열
// 				$('.btnMyContView').on('click', function(){
// 					var parentCont = $(this).closest('.mainMyContent');
// 					if(parentCont.hasClass('active')){
// 						parentCont.removeClass('active');
// 					}else {
// 						parentCont.addClass('active');
// 					}
// 				});
// 				break;
// 			case "0":   // 기타
// 				break;
// 			default:
// 				break;
// 		}
	}
	
	function setEvent(){
		/* 메인 상단탭 */
// 		$(".PN_tabList li").click(function(){
// 			var tab_id = $(this).attr("data-tab");
			
// 			$(".PN_tabList li").removeClass("active");
// 			$(".PN_tabContent").removeClass("active");
			
// 			$(this).addClass("active");
// 			$("#" + tab_id).addClass("active");
				
// 			if(  tab_id === 'PN_tab1_2'  ){
// 				setMailInfo();
// 				setMailUnreadCount();				
// 			}else if(  tab_id === 'PN_tab1_3'  ){
// 				setApvCnt();
// 				getApprovalList();
// 			}else if(  tab_id === 'PN_tab1_4'  ){
// 				setToday();	
// 			}else if(  tab_id === 'PN_tab1_5'  ){
// 				setMyTaskList($("#selTaskType").val(), "List");
// 				setTaskReportList();
// 			}
// 		});
		
		/* 결재현황 */
// 		$(".PN_TitleBox .PN_btnSlide_close").click(function(){
// 			$(this).parents().next(".PN_aprListBox").slideToggle(500);
// 			$(this).toggleClass("PN_btnSlide_close");
// 			$(this).toggleClass("PN_btnSlide_open");
// 			if($(".PN_TitleBox").has("PN_btnSlide_open")) {
// 			   $(".PN_btnSlide_open").html("<spring:message code='Cache.btn_Open'/>"); // 열기
// 			   $(".PN_btnSlide_close").html("<spring:message code='Cache.btn_Close'/>"); // 닫기
// 			}
// 		});
		
// 		// 내업무 상단 셀렉트 박스 이벤트
// 		$("#selTaskType").on("change", function(){
// 			setMyTaskList($("#selTaskType").val(), "Graph");
// 		});
	}
	
	function setScrollEvent(){
//		coviCtrl.bindmScrollV($(".mScrollV"));
	}
	
	function saveUserPortalOption(){
		var isDark = $("#wrap").hasClass("PN_darkmode") ? "Y" : "N";
		//var useLeft = $(".PN_btnArea .btnOpen").css("display") == "none" ? "Y" : "N";
		var useLeft = $(".PN_mainLeft .PN_btnArea a").hasClass("btnClose") ? "Y" : "N";
		var isActiveMyContents = $(".PN_myContents .PN_myBtn").hasClass("active") ? "Y" : "N";
		var portalOption = {
			"isDark": isDark,
			"useLeft": useLeft,
			"isActiveMyContents": isActiveMyContents
		};
		
		$.ajax({
			url: "/groupware/pnPortal/saveUserPortalOption.do",
			type: "POST",
			data: {
				"portalOption": JSON.stringify(portalOption)
			},
			success: function(data){},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/pnPortal/saveUserPortalOption.do", response, status, error);
			}
		});
	}
	
	function setNoImage(imgObj, imgOption){
		var noImgSrc = cssPath;
		
		if(imgOption && imgOption == "n2"){
			noImgSrc += "/customizing/ptype05/images/project/noimg02.jpg";
		}else{
			noImgSrc += "/customizing/ptype05/images/project/noimg01.jpg";
		}
		
		$(imgObj).attr("src", noImgSrc)
			.css({"width": "100%", "height": "100%"});
	}
	
	/* 주요소식 영역 시작 */

	
	function goViewPopup(pBizSection, pOpenOption, pMenuID, pVersion, pFolderID, pMessageID){
		var url = String.format("/groupware/board/goBoardViewPopup.do?CLBIZ={0}&menuID={1}&version={2}&folderID={3}&messageID={4}&viewType=Popup", pBizSection, pMenuID, pVersion, pFolderID, pMessageID);
		
		if(pOpenOption == "New") CFN_OpenWindow(url, 1080, (window.screen.height - 100), "resize");
		else parent.Common.open("", "boardViewPop", "<spring:message code='Cache.lbl_DetailView'/>", url, "1080px", "600px", "iframe", true, null, null, true);
	}
	
	/* 주요소식 영역 끝 */
	/* 받은메일 영역 시작 */
	
	
	
	
	
	function getProfileImgPath(searchMode, userCodes, userMails){
		var returnImage = "";
		
		$.ajax({
			url: "/groupware/pnPortal/getProfileImagePath.do",
			type: "POST",
			data: {
				"searchMode": searchMode,
				"userCodes": userCodes,
				"userMails": userMails
			},
			async: false,
			success: function(data){
				if(data.list && data.list.length > 0){
					returnImage = data.list.filter(function(d){
						return d.PhotoPath != "";
					});
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/approval/getDomainListData.do", response, status, error);
			}
		});
		
		return returnImage;
	}
	
	function onErrorImage(imgObj, name){
		var bTag = $("<b>").addClass("mColor").text(name);
		var imgSpan = $(imgObj).closest(".PN_mImg");
		imgSpan = imgSpan.length != 0 ? imgSpan : $(imgObj).closest(".PN_apImg");
		imgSpan = imgSpan.length != 0 ? imgSpan : $(imgObj).closest(".PN_apImg2");
		var cbClassName = imgSpan.closest(".contentBox").attr("class");
		cbClassName = cbClassName ? "." + cbClassName.replaceAll(" ", ".") : "";
		
		imgSpan.empty();
		imgSpan.append(bTag);
		addClassImgBox(cbClassName);
	}
	
	function addClassImgBox(posTagName){
		var imgCnt = 0;
		
		if(posTagName){
			$.each($(posTagName).find(".mColor"), function(idx, itme){
				var classIdx = (imgCnt % 5) + 1;
				$(this).attr("class", "");
				$(this).attr("class", "mColor mColor" + classIdx);
				imgCnt++;
			});
		}
	}
	
	/* 받은메일 영역 끝 */
	/* 결재현황 시작 */
	
	function setApvCnt(){
		// 카운트 초기화
		$(".PN_tabList .PN_tab1_3 .countStyle").text(0);
		
		if (Common.getExtensionProperties('isUse.account') == 'N') return false;
		
		//미결함
		$.ajax({
			url: "/approval/user/getApprovalCnt.do",
			type: "POST",
			data: {
				businessData1: "APPROVAL"
			},
			success: function(data){
				if(data.status == "SUCCESS"){
					var approvalCnt = Number(data.cnt) >= 100 ? "99+" : data.cnt;
					var totalCnt = Number($(".PN_tabList .PN_tab1_3 .countStyle").text().replaceAll(/\+/gi, ""));
					
					totalCnt = totalCnt + Number(data.cnt) >= 100 ? "99+" : totalCnt + Number(data.cnt);
					$("#PN_tab1_3 .PN_linkBox .l_link2_1 .l_Num").text(approvalCnt);
					$(".PN_tabList .PN_tab1_3 .countStyle").text(totalCnt);
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/approval/user/getApprovalCnt.do", response, status, error);
			}
		});
		
		//진행함
		$.ajax({
			url: "/approval/user/getProcessCnt.do",
			type: "POST",
			data: {
				businessData1: "APPROVAL"
			},
			success: function(data){
				if(data.status == "SUCCESS"){
					var processCnt = Number(data.cnt) >= 100 ? "99+" : data.cnt;
					var totalCnt = Number($(".PN_tabList .PN_tab1_3 .countStyle").text().replaceAll(/\+/gi, ""));
					totalCnt = totalCnt + Number(data.cnt) >= 100 ? "99+" : totalCnt + Number(data.cnt);
					
					$("#PN_tab1_3 .PN_linkBox .l_link2_2 .l_Num").text(processCnt);
					$(".PN_tabList .PN_tab1_3 .countStyle").text(totalCnt);
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/approval/user/getProcessCnt.do", response, status, error);
			}
		});
	}
	
	var arrDomainDataList = {};
	function getApprovalList(){
		$.ajax({
			url: "/groupware/pnPortal/getApprovalList.do",
			type: "POST",
			data: {
				"businessData1": "APPROVAL"
			},
			success: function(data){
				if(data.status == "SUCCESS"){
					var apvListData = data.list.approval;
					var procListData = data.list.process;
					
					setApprovalList(apvListData);
					
					setProcessList(procListData);
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/pnPortal/getApprovalList.do", response, status, error);
			}
		});
	}
	
	function setApprovalList(listData){
		if(listData.length > 0){
			var apvHtml = "";
			
			$.each(listData, function(idx, item){
				var divWrap = $("<div class='PN_aprList'></div>");
				var approvalStr = "";
				var rejectStr = "";
				arrDomainDataList[item.ProcessID] = item.DomainDataContext;
				
				var isUsePwd = "N";
				if(item.SchemaContext) isUsePwd = this.SchemaContext.scWFPwd.isUse;
				
				switch(item.FormSubKind){
					case "T005": // 후결
						approvalStr = "<spring:message code='Cache.lbl_apv_Confirm' />"; // 확인
						break;
					case "T009": // 합의
						approvalStr = "<spring:message code='Cache.lbl_apv_agree' />"; // 동의
						rejectStr = "<spring:message code='Cache.lbl_apv_disagree' />"; // 거부
						break;
					case "T018": // 공람
						approvalStr = "<spring:message code='Cache.lbl_apv_Confirm' />"; // 확인
						break;
					case "T019": // 확인결재
						approvalStr = "<spring:message code='Cache.lbl_apv_Confirm' />"; // 확인
						break;
					default:
						approvalStr = "<spring:message code='Cache.btn_Approval2' />"; // 승인
						rejectStr = "<spring:message code='Cache.lbl_apv_reject' />"; // 반려
						break;
				}
				
				divWrap.append($("<a class='PN_apCont'></a>")
							.attr("onclick", "openApvPopup('Approval', '" + item.ProcessID + "', '" + item.WorkItemID + "', '" + item.PerformerID + "', '" + item.ProcessDescriptionID + "', '" + item.FormSubKind + "', '" + item.FormInstID + "', '" + item.FormID + "', '" + sessionObj["USERID"] + "', '" + item.FormPrefix + "');")
							.append($("<span class='PN_apImg'></span>"))
							.append($("<strong class='PN_apTitle'></strong>").text(item.FormSubject))
							.append($("<div class='PN_apTxt'></div>")
								.append($("<span class='PN_apName'></span>").text(item.InitiatorName))
								.append($("<span class='PN_apTime'></span>").text(item.Created))))
						.append($("<div class='PN_apBtn1'></div>")
							.append($("<a class='PN_btn_Approval'></a>")
								.attr("onclick", "onClickApvBtn('" + item.FormSubKind + "', '" + item.TaskID + "', 'approved', '" + item.FormInstID + "', '" + item.ProcessID + "', '" + item.UserCode + "', '" + isUsePwd + "', '" + item.ProcessName + "', '" + item.FormID + "', '" + item.WorkItemID + "', '" + item.formDraftkey + "');")
								.text(approvalStr)))
						.append($("<div class='PN_apBtn2'></div>"));
				
				if(rejectStr){
					divWrap.find(".PN_apBtn1")
						.append($("<a class='PN_btn_Reject'></a>")
							.attr("onclick", "onClickApvBtn('" + item.FormSubKind + "', '" + item.TaskID + "', 'reject', '" + item.FormInstID + "', '" + item.ProcessID + "', '" + item.UserCode + "', '" + isUsePwd + "', '" + item.ProcessName + "', '" + item.FormID + "', '" + item.WorkItemID + "', '" + item.formDraftkey + "');")
							.text(rejectStr));
				}
				
				if(item.PhotoPath){
					divWrap.find(".PN_apImg")
						.append($("<img>")
							.attr("src", item.PhotoPath)
							.attr("onerror", "onErrorImage(this, '" + item.InitiatorName.substring(0, 1) + "');"));
				}else{
					divWrap.find(".PN_apImg")
						.append($("<span class='PN_apImg'></span>")
							.append($("<b class='mColor'></b>").text(item.InitiatorName.substring(0, 1))));
				}
				
				if(item.IsComment == "Y"){
					divWrap.find(".PN_apBtn2")
						.append($("<a class='PN_btn_Comment'></a>")
							.attr("onclick", "javascript:openCommentView('" + item.ProcessID + "', '" + item.FormInstID + "');")
							.text("<spring:message code='Cache.lbl_Comments'/>")); // 댓글
				}
				
				if(item.IsFile == "Y"){
					divWrap.find(".PN_apBtn2")
					 	.append($("<a class='PN_btn_File'></a>")
							.attr("onclick", "javascript:openFileList(this, '" + item.FormInstID + "');")
							.text("<spring:message code='Cache.lbl_apv_filelist'/>")); // 첨부파일
				}
				
				apvHtml += $(divWrap)[0].outerHTML;
			});
			
			$(".PN_aprListArea .PN_aprBox1 .PN_aprListBox").show();
			$(".PN_aprListArea .PN_aprBox1 .PN_nolist").hide();
			$(".PN_aprListArea .PN_aprBox1 .PN_aprListBox").empty().append(apvHtml);
		}else{
			$(".PN_aprListArea .PN_aprBox1 .PN_aprListBox").hide();
			$(".PN_aprListArea .PN_aprBox1 .PN_nolist").show();
		}
		
		addClassImgBox(".PN_aprListArea .PN_aprBox1 .PN_aprListBox");
	}
	
// 	function setProcessList(listData){
// 		if(listData.length > 0){
// 			var procHtml = "";
			
// 			$(listData).each(function(idx, item){
// 				var cnt = 0;
// 				statusCheck = false;
// 				arrDomainDataList[item.ProcessID] = item.DomainDataContext;
// 				var divWrap = $("<div class='PN_aprList'></div>");
// 				var dataObj = Object.toJSON(item.DomainDataContext);
// 				var objGraphicList = ApvGraphicView.getGraphicData(dataObj, true);
// 				var statusCnt = 0;
// 				var statusCnt2 = 0;
// 				var firstImgPath = "";
// 				var nowImgPath = "";
// 				var firstPersonName = "";
// 				var nowPersonName = "";
				
// 				for(var i = 0; i < objGraphicList.length; i++){
// 					var division = objGraphicList[i];
// 					var steps = division.steps;
// 					for(var j = 0; j < steps.length; j++){
// 						var step = steps[j];
// 						var substeps = step.substeps;
// 						cnt++;
						
// 						for(var k = 0; k < substeps.length; k++){
// 							var substep = substeps[k];
// 							var substep2 = substep.substeps;
// 							if(i == 0 && j == 0){					//기안부서의 기안자
// 								firstImgPath = substeps[0].photo;
// 								firstPersonName = substeps[0].name;
// 							}else{
// 								if(substeps[k].waitCircle == "cirBlue"){
// 									if(typeof substep2 === 'object'){
// 										for(var l=0; l < substep2.length; l++){
// 											if(substep2[l].state == "wait"){
// 												statusCheck = true;
// 												nowImgPath = substep2[l].photo;
// 												nowPersonName = substep2[l].name;
// 												cnt = 0;
// 											}
// 										}
// 									}else{
// 										statusCheck = true;
// 										nowImgPath = substeps[k].photo;
// 										nowPersonName = substeps[k].name;
// 										cnt = 0;
// 									}
// 									if(substeps[k].state == "wait"){
// 										statusCheck = true;
// 										nowImgPath = substeps[k].photo;
// 										nowPersonName = substeps[k].name;
// 										cnt = 0;
// 									}
// 								}else if(k == substeps.length-1){
// 									if(statusCheck == false){
// 										++statusCnt;
// 									}
// 								}
// 							}
// 						}
// 						statusCnt2 = cnt;
// 					}
// 				}
				
// 				divWrap.append($("<a class='PN_apCont'></a>")
// 						.attr("onclick", "javascript:openApvPopup(\'Process\', '" + item.ProcessID + "', '" + item.WorkItemID + "', '" + item.PerformerID + "', '" + item.ProcessDescriptionID + "', '" + item.FormSubKind + "', '" + item.FormInstID + "', '" + item.FormID + "', '" + sessionObj["USERID"] + "', '" + item.FormPrefix + "');")
// 							.append($("<strong class='PN_apTitle'></strong>").text(item.FormSubject))
// 							.append($("<div class='PN_apTxt'></div>")
// 								.append($("<span class='PN_apName'></span>").text(item.InitiatorName))
// 								.append($("<span class='PN_apTime'></span>").text(item.Finished)))
// 							.append($("<ul class='PN_apProcess'></ul>")))
// 						.append($("<div class='PN_apBtn2'></div>"));
				
// 				if(firstImgPath){
// 					divWrap.find(".PN_apProcess")
// 						.append($("<li></li>")
// 							.append($("<span class='PN_apImg2'></span>")
// 								.append($("<img>")
// 									.attr("src", firstImgPath)
// 									.attr("onerror", "onErrorImage(this, '" + firstPersonName.substring(0, 1) + "');"))));
// 				}else{
// 					divWrap.find(".PN_apProcess")
// 						.append($("<li></li>")
// 							.append($("<span class='PN_apImg2'></span>")
// 								.append($("<b class='mColor'></b>").text(firstPersonName.substring(0, 1)))));
// 				} 
				
// 				if(statusCnt != 0){
// 					divWrap.find(".PN_apProcess")
// 						.append($("<li></li>")
// 							.append($("<span class='PN_apImg2'></span>")
// 								.append($("<b class='mPlus'></b>").text("+" + statusCnt))));
// 				}
					 
// 				if(statusCheck){
// 					if(nowImgPath){
// 						divWrap.find(".PN_apProcess")
// 							.append($("<li></li>")
// 								.append($("<span class='PN_apImg2 active'></span>")
// 									.append($("<img>")
// 										.attr("src", nowImgPath)
// 										.attr("onerror", "onErrorImage(this, '" + nowPersonName.substring(0, 1) + "');"))));
// 					}else{
// 						divWrap.find(".PN_apProcess")
// 							.append($("<li></li>")
// 								.append($("<span class='PN_apImg2 active'></span>")
// 									.append($("<b class='mColor'></b>").text(nowPersonName.substring(0, 1)))));
// 					}
					
// 					if(statusCnt2 != 0){
// 						divWrap.find(".PN_apProcess")
// 							.append($("<li></li>")
// 								.append($("<span class='PN_apImg2'></span>")
// 									.append($("<b class='mPlus'></b>").text("+" + statusCnt2))));
// 					}
// 				}
				
// 				if(item.IsComment == "Y"){
// 					divWrap.find(".PN_apBtn2")
// 						.append($("<a class='PN_btn_Comment'></a>")
// 							.attr("onclick", "javascript:openCommentView('" + item.ProcessID + "', '" + item.FormInstID + "');")
// 							.text("<spring:message code='Cache.lbl_Comments'/>")); // 댓글
// 				}
				
// 				if(item.IsFile == "Y"){
// 					divWrap.find(".PN_apBtn2")
// 						.append($("<a class='PN_btn_File'></a>")
// 							.attr("onclick", "javascript:openFileList(this, '" + item.FormInstID + "');")
// 							.text("<spring:message code='Cache.lbl_apv_filelist'/>")); // 첨부파일
// 				}
				
// 				procHtml += $(divWrap)[0].outerHTML;
// 			});
			
// 			$(".PN_aprListArea .PN_aprBox2 .PN_aprListBox").show();
// 			$(".PN_aprListArea .PN_aprBox2 .PN_nolist").hide();
// 			$(".PN_aprListArea .PN_aprBox2 .PN_aprListBox").empty().append(procHtml);
// 		}else{
// 			$(".PN_aprListArea .PN_aprBox2 .PN_aprListBox").hide();
// 			$(".PN_aprListArea .PN_aprBox2 .PN_nolist").show();
// 		}
		
// 		addClassImgBox(".PN_aprListArea .PN_aprBox2 .PN_aprListBox");
// 	}
	
	var commentPopupTitle = "";
	var commentPopupButtonID = "";
	var commentPopupReturnValue = false;
	function onClickApvBtn(subKind, taskID, kind, formInstID, processID, UserCode, isUsePwd, ProcessName, formID, workitemID, formDraftkey){
		if(kind == "approved"){
			commentPopupTitle = "<spring:message code='Cache.btn_apv_Approved'/>"; // 승인
			commentPopupButtonID = "btApproved";
		}else{
			commentPopupTitle = "<spring:message code='Cache.lbl_apv_reject'/>"; // 반려
			commentPopupButtonID = "btReject";
		}
		
		var commonWritePopup = CFN_OpenWindow("/approval/CommentWrite.do?usePWD="+isUsePwd, "", 540, 549, "resize");
		
		var commonWritePopupOnload = function(){
			commonWritePopup.$("input#commentOK").click(function(){
				var _g_authKey = ""; //TODO
				apvDoButtonAction("APPROVAL", subKind, taskID, kind, $("#ACTIONCOMMENT").val(), formInstID, false, processID, UserCode, $("#SIGNIMAGETYPE").val(), false, ProcessName, _g_password, _g_authKey, formID, workitemID, formDraftkey);
			});
		};
			
		var pollTimer = window.setInterval(function(){
			if(commonWritePopup.document.getElementById("commentOK")){
				window.clearInterval(pollTimer);
				commonWritePopupOnload();
			}
		}, 200);
	}
	
	function apvDoButtonAction(mode, subkind, taskId, actionMode, actionComment,forminstID, isMobile, processID, UserCode, signImage, isSerial, ProcessName, apvToken, authKey, formID, workitemID, formDraftkey){
		if(actionMode == "approved"){
			if(subkind == "T009" || subkind == "T004")
				actionMode = "AGREE";
			else
				actionMode = "APPROVAL";
		}else if(actionMode == "reject"){
			if(subkind == "T009" || subkind == "T004")
				actionMode = "DISAGREE";
			else
				actionMode = "REJECT";
		}
		
		if(subkind == "T009" || subkind == "T004"){		// 합의 및 협조
			mode = "PCONSULT";
		}else if((subkind == "C" || subkind == "AS") && approvalType == "DEPT"){
			mode = "SUBREDRAFT";
		}else if(subkind == "T016"){
			mode = "AUDIT";
		}else{
			mode = "APPROVAL";
		}	
		
		var sJsonData = {};
		
		$.extend(sJsonData, {"mode": mode});
		$.extend(sJsonData, {"subkind": subkind});
		$.extend(sJsonData, {"taskID": taskId});
		$.extend(sJsonData, {"FormInstID": forminstID});
		$.extend(sJsonData, {"usid": sessionObj["USERID"]});
		$.extend(sJsonData, {"actionMode": actionMode});
		$.extend(sJsonData, {"signimagetype" : signImage});
		$.extend(sJsonData, {"gloct": ""});
		$.extend(sJsonData, {"actionComment": actionComment});
	    $.extend(sJsonData, {"processName": ProcessName}); //프로세스이름
	    $.extend(sJsonData, {"workitemID": workitemID});
	    $.extend(sJsonData, {"g_authKey": authKey});
	    $.extend(sJsonData, {"g_password": apvToken});
	    $.extend(sJsonData, {"formDraftkey" : formDraftkey});
	    $.extend(sJsonData, {"formID" : formID});
		
		if(isSerial) { // 연속결재 처리 todo
			$.extend(sJsonData, {"actionComment_Attach": "[]"});
		}
		else {
			$.extend(sJsonData, {"actionComment_Attach": document.getElementById("ACTIONCOMMENT_ATTACH").value});
		}
		
		// 대결자가 결재하는 경우 결재선 변경
		var apvList = setDeputyList(mode, subkind, taskId, actionMode, actionComment,forminstID, isMobile, processID, UserCode);
		
		if(apvList != arrDomainDataList[processID]){
			$.extend(sJsonData, {"ChangeApprovalLine" : apvList});
		}
		
		var formData = new FormData();
		// 양식 기안 및 승인 정보
		formData.append("formObj", Base64.utf8_to_b64(JSON.stringify(sJsonData)));
		
		$.ajax({
			url: "/approval/draft.do",
			data: formData,
			type: "POST",
			dataType: "json",
			processData: false,
			contentType: false,
			success: function(res){
				if (res.status == "SUCCESS" || (res.status == "FAIL" && res.message.indexOf("NOTASK") > -1)) {
					if(res.status == "FAIL"){
						res.message = '<spring:message code="Cache.msg_apv_notask"/>'.replace(/(<([^>]+)>)/gi, "");
					}
					
					Common.Inform(res.message, "Inform", function(){
						getApprovalList();
					});	//완료되었습니다.
				}else{
					Common.Warning("<spring:message code='Cache.msg_apv_030'/>: " + res.message);			//오류가 발생했습니다.
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/approval/draft.do", response, status, error);
			}
		});
	}
	
	function openCommentView(pProcessID, pFormInstID) {
		CFN_OpenWindow("/approval/goCommentViewPage.do?ProcessID="+pProcessID+"&FormInstID="+pFormInstID+"&archived=false&bstored=false", "", 540, 500, "resize");
	}
	
	var attachFileInfoObj;
	function openFileList(pObj,FormInstID){
		if(!axf.isEmpty($(pObj).parent().find(".file_box").html())){
			$(pObj).parent().find(".file_box").remove();
			return false;
		}
		
		$(".file_box").remove();
		
		$.ajax({
			url: "/approval/getCommFileListData.do",
			type: "POST",
			data: {
				FormInstID: FormInstID
			},
			async: false,
			success: function(data){
				if(data.list.length > 0){
					var ulWrap = $("<ul class='file_box'></ul>")
									.append($("<li class='boxPoint'></li>"));
					
					for(var i = 0; i < data.list.length; i++){
						attachFileInfoObj = data.list;
						ulWrap.append($("<li></li>")
									.append($("<a></a>")
										.attr("onclick", "attachFileDownLoadCall("+i+")")
										.text(data.list[i].FileName)));
					}
					
					$(pObj).parent().append(ulWrap);
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/approval/getCommFileListData.do", response, status, error);
			}
		});
	}
	
	function attachFileDownLoadCall(index){
		var fileInfoObj = attachFileInfoObj[index];
		Common.fileDownLoad($$(fileInfoObj).attr("FileID"), $$(fileInfoObj).attr("FileName"), $$(fileInfoObj).attr("FileToken"));
	}
	
	function openApvPopup(mode, ProcessID, WorkItemID, PerformerID, ProcessDescriptionID, SubKind, FormInstID, FormID, userCode, FormPrefix){
		var width, gloct;
		var archived = "false";
		
		switch(mode){
			case "Approval": // 미결함
				mode = "APPROVAL"; 
				gloct = "APPROVAL"; 
				break;
			case "Process": // 진행함
				mode = "PROCESS"; 
				gloct = "PROCESS"; 
				break;
		}
		
		if(IsWideOpenFormCheck(FormPrefix) == true){
			width = 1070;
		}else{
			width = 790;
		}
		
		CFN_OpenWindow("/approval/approval_Form.do?mode="+mode+"&processID="+ProcessID+"&workitemID="+WorkItemID+"&performerID="+PerformerID+"&processdescriptionID="+ProcessDescriptionID+"&userCode="+userCode+"&gloct="+gloct+"&formID="+FormID+"&forminstanceID="+FormInstID+"&admintype=&archived="+archived+"&usisdocmanager=true&listpreview=N&subkind="+SubKind+"", "", width, (window.screen.height - 100), "resize");
	}
	
	/* 결재 현황 끝 */
	/* 일정 현황 시작 */
	
// 	function setToday(){
// 		var nowDate = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss"));
// 		var sun = schedule_GetSunday(nowDate);
// 		var sat = schedule_AddDays(schedule_SetDateFormat(sun, "/"), 6);
		
// 		$("#schStartDate").text(schedule_SetDateFormat(sun, "."));
// 		$("#schEndDate").text(schedule_SetDateFormat(sat, "."));
		
// 		getScheduleList();
// 	}
	
// 	function clickDateBtn(btnType){
// 		var sDateObj = new Date($("#schStartDate").text().replaceAll(".", "/"));
// 		var sDate = "";
// 		var eDate = "";
		
// 		if(btnType == "PREV"){
// 			sDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, -7), ".");
// 			eDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, -1), ".");
// 		}else{
// 			sDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, 7), ".");
// 			eDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, 13), ".");
// 		}
		
// 		$("#schStartDate").text(sDate);
// 		$("#schEndDate").text(eDate);
		
// 		getScheduleList();
// 	}
	
// 	function getScheduleList(){
// 		console.log('getScheduleList')
// 		var folderCheckList = ";";
// 		var sDate = $("#schStartDate").text();
// 		var eDate = $("#schEndDate").text();
// 		var sDateObj = new Date(sDate.replaceAll(".", "/"));
// 		var schList = {};
// 		var schKeyList = null;
		
// 		for(var i = 0; i < 7; i++){
// 			var dayStr = schedule_SetDateFormat(schedule_AddDays(sDateObj, i), ".");
// 			schList[dayStr] = new Array();
// 		}
		
// 		schKeyList = Object.keys(schList);
		
// 		if(Object.keys(schAclArray).length == 0){
// 			scheduleUser.setAclEventFolderData();
// 		}
		
// 		$(schAclArray.read).each(function(idx,obj){
// 			folderCheckList += (obj.FolderID + ";");
// 		});
		
// 		$.ajax({
// 			url: "/groupware/schedule/getList.do",
// 			type: "POST",
// 			data: {
// 				"FolderIDs": folderCheckList,
// 				"StartDate": sDate.replaceAll(".", "-"),
// 				"EndDate": eDate.replaceAll(".", "-"),
// 				"UserCode": sessionObj["USERID"],
// 				"lang": sessionObj["lang"]
// 			},
// 			success: function(data){
// 				var listData = data.list;
// 				var photoPaths = new Array();
				
// 				if(listData && listData.length > 0){
// 					var userCodes = listData.map(function(d){
// 						return d.OwnerCode;
// 					});
// 					photoPaths = getProfileImgPath("Code", userCodes.join(";"), "");
					
// 					$.each(schKeyList, function(idx, key){
// 						schList[key] = listData.filter(function(d){
// 							var keyDateTime = new Date(key.replaceAll(".", "/")).getTime();
// 							var startDateTime = new Date(d.StartDate.replaceAll("-", "/")).getTime();
// 							var endDateTime = new Date(d.EndDate.replaceAll("-", "/")).getTime();
							
// 							return keyDateTime >= startDateTime && keyDateTime <= endDateTime;
// 						});
						
// 						schList[key].sort(function(a, b){
// 							return a.StartTime < b.StartTime ? -1 : 0;
// 						});
// 					});
// 				}
				
// 				setScheduleList(schList, photoPaths);
// 			},
// 			error: function(response, status, error){
// 				CFN_ErrorAjax("/groupware/schedule/getList.do", response, status, error);
// 			}
// 		});
// 	}
	
// 	function setScheduleList(schList, photoPaths){
// 		var schHtml = "";
// 		var schKeyList = Object.keys(schList);
// 		var nowDateTime = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss")).getTime();
		
// 		$.each(schKeyList, function(kIdx, key){
// 			var divWrap = $("<div class='PN_scheduleBox'></div>");
			
// 			if(schList[key].length > 0){
// 				divWrap.append($("<strong class='PN_scDate'></strong>").text(key))
// 							.append($("<ul class='PN_scheduleList'></ul>"));
				
// 				$.each(schList[key], function(idx, item){
// 					var keyEDateTime = new Date(key.replaceAll(".", "/") + " " + item.EndTime).getTime();
// 					var activeClass = keyEDateTime < nowDateTime ? "" : "active";
// 					var photoPath = photoPaths.filter(function(d){
// 						return d.UserCode == item.RegisterCode;
// 					});
// 					photoPath = photoPath.length == 0 ? "" : photoPath[0].PhotoPath;
					
// 					divWrap.find(".PN_scheduleList")
// 								.append($("<li class='" + activeClass + "'></li>")
// 									.attr("onclick", "scheduleUser.goDetailViewPage('Webpart', " + item.EventID + ", " + item.DateID + ", " + item.RepeatID + ", '" + item.IsRepeat + "', " + item.FolderID + ")")
// 									.append($("<a href='#' class='PN_scCont'></a>")
// 										.append($("<span class='PN_scImg'></span>"))
// 										.append($("<strong class='PN_scTitle'></strong>").text(item.Subject))
// 										.append($("<span class='PN_scTime'></span>").text(item.StartTime + " ~ " + item.EndTime))));
					
// 					if(photoPath){
// 						divWrap.find(".PN_scImg")
// 									.append($("<img>")
// 										.attr("src", photoPath)
// 										.attr("onerror", "onErrorImage(this, '" + item.MultiRegisterName.substring(0, 1) + "');"));
// 					}else{
// 						divWrap.find(".PN_scImg")
// 									.append($("<b class='mColor'></b>").text(item.MultiRegisterName.substring(0, 1)));
// 					}
// 				});
// 			}else{
// 				divWrap.addClass("PN_nolist")
// 							.append($("<strong class='PN_scDate'></strong>").text(key))
// 							.append($("<ul class='PN_scheduleList'></ul>")
// 								.append($("<li></li>")
// 									.append($("<strong></strong>").text("<spring:message code='Cache.lbl_NoRegistSchedule'/>")) // 등록된 일정이 없습니다.
// 									.append($("<p></p>").html("<spring:message code='Cache.msg_RegistImportantSchedule'/>")) // 효율적인 일정 관리를 위해<br />중요하거나 반복되는 업무를 등록해 보세요.
// 									.append($("<a href='#' class='PN_btnRegist'></a>")
// 										.attr("onclick", "addSchedule('" + key + "')")
// 										.text("<spring:message code='Cache.lbl_RegistSchedule'/>")))); // 일정 등록하기
// 			}
			
// 			schHtml += $(divWrap)[0].outerHTML;
// 		});
		
// 		$(".PN_scheduleInner").empty().append(schHtml);
// 		addClassImgBox(".PN_scheduleInner");
// 	}
	
	function addSchedule(pSchDate){
		coviCtrl.toggleSimpleMake();
		$(".tabMenuArrow li[type=Schedule]").trigger("click");
		
		$("#simpleSchDateCon_StartDate").val(pSchDate);
		$("#simpleSchDateCon_EndDate").val(pSchDate);
	}
	
	/* 일정 현황 끝 */
	/* 내업무 시작 */
	
// 	function setMyTaskList(mode, setMode){
// 		var nowDateTime = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss")).getTime();
		
// 		$.ajax({
// 			url: "/groupware/biztask/getAllMyTaskList.do",
// 			type: "GET",
// 			data: {
// 				"userCode" : sessionObj["USERID"],
// 				"stateCode" : ""
// 			},
// 			success: function(data){
// 				var listData = null;
// 				var myTaskStr = '';
// 				var todayTaskStr = '';
// 				var isEmptyMyTask = false;
// 				var isEmptyTodayTask = false;
// 				var todayCnt = 0;
// 				var waitingNum = 0;
// 				var progressNum = 0;
// 				var delayNum = 0;
// 				var completeNum = 0;
// 				var taskHtml = "";
// 				var todayHtml = "";
				
// 				if(mode == "TF"){
// 					listData = data.ProjectTaskList;
// 				}else if(mode == "TASK"){
// 					listData = data.TaskList;
// 				}else{
// 					listData = $.merge(data.ProjectTaskList, data.TaskList);
// 				}
				
// 				if(listData && listData.length > 0){
// 					listData.sort(function(a, b){
// 						return a.EndDate < b.EndDate ? -1 : 0;
// 					});
					
// 					$.each(listData, function(idx, item){
// 						var sDateTime = new Date(item.StartDate.replaceAll("-", "/")).getTime();
// 						var eDateTime = new Date(item.EndDate.replaceAll("-", "/")).getTime();
// 						var isOwner = (item.RegisterCode == sessionObj["USERID"] || item.OwnerCode == sessionObj["USERID"]) ? "Y" : "N";
						
// 						switch(item.State){
// 							case "Waiting":
// 								waitingNum++;
// 								break;
// 							case "Process":
// 								if(Number(item.DelayDay) > 0){
// 									delayNum++;
// 								}else{
// 									progressNum++;
// 								}
// 								break;
// 							case "Complete":
// 								completeNum++;
// 								break;
// 						}
						
// 						if(setMode == "List"){
// 							if(idx < 3){
// 								var liWrap = $("<li></li>");
// 								var onclickEvent = "";
// 								var titleStr = "";
								
// 								if(item.hasOwnProperty("ATName")){
// 									onclickEvent = "javascript:openTaskPopup('TF', '" + item.CU_ID + "', '" + item.AT_ID + "', 'Y');";
// 									titleStr = item.ATName;
// 								}else{
// 									onclickEvent = "javascript:openTaskPopup('TASK', '" + item.FolderID + "', '" + item.TaskID + "', '" + isOwner + "');";
// 									titleStr = item.Subject;
// 								}
								
// 								liWrap.append($("<a href='#' class='PN_tsCont'></a>")
// 											.attr("onclick", onclickEvent)
// 										.append($("<strong class='PN_tsTitle'></strong>").text(titleStr))
// 										.append($("<div class='PN_tsTxt'></div>")
// 											.append($("<span class='PN_tsState'></span>").text(item.TaskState))
// 											.append($("<span class='PN_tsDate'></span>").text("~" + item.EndDate.replaceAll("-", "."))))
// 										.append($("<div class='PN_graphBox'></div>")
// 											.append($("<div class='PN_graph'></div>")
// 												.append($("<span class='PN_graphBar'></span>").css("width", item.Progress + "%")))
// 											.append($("<span class='PN_graphPer'></span>").text(item.Progress + "%"))));
								
// 								taskHtml += $(liWrap)[0].outerHTML;
// 							}
							
// 							if($(".PN_TodayList li").length < 3 
// 								&& (nowDateTime >= sDateTime && nowDateTime <= eDateTime)){
// 								var liWrap = $("<li></li>");
// 								var onclickEvent = "";
// 								var titleStr = "";
// 								var dDayStr = "";
								
// 								if(item.hasOwnProperty("ATName")){
// 									onclickEvent = "javascript:openTaskPopup('TF', '" + item.CU_ID + "', '" + item.AT_ID + "', 'Y');";
// 									titleStr = item.ATName;
// 								}else{
// 									onclickEvent = "javascript:openTaskPopup('TASK', '" + item.FolderID + "', '" + item.TaskID + "', '" + isOwner + "');";
// 									titleStr = item.Subject;
// 								}
								
// 								if(item.DelayDay == 0){
// 									dDayStr += "<spring:message code='Cache.lbl_UntilToday'/>" // 오늘까지
// 								}else{
// 									dDayStr = item.DelayDay > 0 ? "D+" + item.DelayDay : "D" + item.DelayDay;
// 								}
								
// 								liWrap.append($("<a href='#' class='PN_tlCont'></a>")
// 									.attr("onclick", onclickEvent)
// 									.append($("<span class='PN_tlDday'></span>").text(dDayStr))
// 									.append($("<strong class='PN_tlTitle'></strong>").text(titleStr))
// 									.append($("<div class='PN_tlTxt'></div>")
// 										.append($("<span class='PN_tlState'></span>").text("<spring:message code='Cache.lbl_TFProgressing'/>: " + item.Progress + "%")) // 진행율
// 										.append($("<span class='PN_tlDate'></span>").text("~" + item.EndDate.replaceAll("-", ".")))));
								
// 								todayHtml += $(liWrap)[0].outerHTML;
// 							}
							
// 							$(".PN_taskList").empty().append(taskHtml);
// 							$(".PN_TodayList").empty().append(todayHtml);
// 						}
// 					});
// 				}
				
// 				if(setMode == "List"){
// 					if(!taskHtml){
// 						$(".PN_taskList").hide();
// 						$(".PN_taskBox3 .PN_nolist").show();
// 					}
					
// 					if(!todayHtml){
// 						$(".PN_TodayList").hide();
// 						$(".PN_taskBox4 .PN_nolist").show();
// 					}
					
// 					var totalNum = waitingNum + progressNum + delayNum + completeNum;
// 					$(".PN_tabList .PN_tab1_5 .countStyle").text(totalNum);
// 				}
				
// 				setTaskCircularGraph(waitingNum, progressNum, delayNum, completeNum);
// 			},
// 			error: function(response, status, error){
// 				CFN_ErrorAjax("/groupware/biztask/getAllMyTaskList.do", response, status, error);
// 			}
// 		});
// 	}
	
// 	function setTaskCircularGraph(waitingNum, progressNum, delayNum, completeNum){
// 		var totalNum = waitingNum + progressNum + delayNum + completeNum;
		
// 		if(totalNum != 0){
// 			var perArr = [
// 							{
// 								percent: waitingNum / totalNum * 100,
// 								style: "40px solid #0E8FD4"
// 							},
// 							{
// 								percent: progressNum / totalNum * 100,
// 								style: "40px solid #48C5EE"
// 							},
// 							{
// 								percent: delayNum / totalNum * 100,
// 								style: "40px solid #246C95"
// 							},
// 							{
// 								percent: completeNum / totalNum * 100,
// 								style: "40px solid #1BC4DF"
// 							}
// 						];
			
// 			perArr.sort(function(a, b){
// 				return a.percent > b.percent ? -1 : a.percent < b.percent ? 1 : 0;
// 			});
			
// 			$(".slice").removeClass("gt50");
			
// 			$.each(perArr, function(idx, item){
// 				if(idx == 0){
// 					$(".cycleBg .pie").css("border", item.style);
// 				}else{
// 					var percent = 0;
					
// 					for(var i = idx; i < perArr.length; i++){
// 						percent += perArr[i].percent;
// 					}
					
// 					$(".slice").eq(idx-1).find(".pie").css("border", item.style);
// 					$(".slice").eq(idx-1).find(".pie").eq(0).css("transform", "rotate(" + (3.6*percent) + "deg)");
					
// 					if(percent > 49) {
// 						$(".slice").eq(idx-1).addClass("gt50");
// 					}
// 				}
// 			});
// 		}else{
// 			$(".cycleBg .pie").css("border", "40px solid rgb(221, 221, 221)");
// 			$(".slice .pie").css("transform", "rotate(0deg)");
// 		}
		
// 		$(".PN_cycleList .cyNum1").text(progressNum);
// 		$(".PN_cycleList .cyNum2").text(completeNum);
// 		$(".PN_cycleList .cyNum3").text(waitingNum);
// 		$(".PN_cycleList .cyNum4").text(delayNum);
// 		$(".PN_cycleCont .cycleTxt .cNum").text(totalNum);
// 	}
	
	
	
	function openTaskPopup(mode, parentID, objectID, isOwner){
		if(mode == "TF"){
			Common.open("", "ActivitySet", "<spring:message code='Cache.lbl_Edit' />", "/groupware/tf/goActivitySetPopup.do?mode=MODIFY&CLSYS=TF&CLMD=user&CLBIZ=TF&CSMU=T&C="+parentID+"&ActivityId="+objectID+"&isOwner="+isOwner, "950px", "650px", "iframe", true, null, null, true); // 수정
		}else{
			Common.open("", "TaskSet", "<spring:message code='Cache.lbl_task_taskManage' />", "/groupware/task/goTaskSetPopup.do?mode=Modify&isOwner="+isOwner+"&taskID="+objectID+"&folderID="+parentID+"&isSearch=N", "950px", "650px", "iframe", true, null, null, true); // 업무관리
		}
	}
	
	function getFolderItemList(){}
	
	function taskScrolling(){
		var slider = $(".PN_rpSlider");
		slider.slick({
			slide: "div",			// 슬라이드 되어야 할 태그 ex) div, li
			infinite : false,		// 무한 반복 옵션
			slidesToShow : 1,		// 한 화면에 보여질 컨텐츠 개수
			slidesToScroll : 1,		// 스크롤 한번에 움직일 컨텐츠 개수
			speed : 500,			// 다음 버튼 누르고 다음 화면 뜨는데까지 걸리는 시간(ms)
			arrows : true,			// 옆으로 이동하는 화살표 표시 여부
			dots : true,			// 스크롤바 아래 점으로 페이지네이션 여부
			autoplay : false,		// 자동 스크롤 사용 여부
			autoplaySpeed : 3000,	// 자동 스크롤 시 다음으로 넘어가는데 걸리는 시간 (ms)
			pauseOnHover : true,	// 슬라이드 이동	시 마우스 호버하면 슬라이더 멈추게 설정
			vertical : false,		// 세로 방향 슬라이드 옵션
			draggable : false		// 드래그 가능 여부
		});
	}	
	/* 내업무 끝 */
	
	function slideLeftArea(target){
		var slideWidth = (typeof $(target).attr("data-slide-width") != 'undefined') ? Number($(target).attr("data-slide-width")) : 388;
		var leftAreaPositionLeft = '-' + slideWidth + 'px';
		var rightAreaWidth = '100%';
		var rightAreaMarginLeft = '0';
		
		if ($(target).hasClass('btnOpen')){
			leftAreaPositionLeft = '0';
			rightAreaWidth = 'calc(100% - '+slideWidth+'px)';
			rightAreaMarginLeft = slideWidth + 'px';
			$(document).trigger("leftOpen")
		}
		else {
			$(document).trigger("leftClose")
		}

		$(target).toggleClass("btnClose");
		$(target).toggleClass("btnOpen");
		$('.PN_mainLeft').css('left', leftAreaPositionLeft);
		$('.PN_mainRight').css({'width': rightAreaWidth,'margin-left': rightAreaMarginLeft});
		
		saveUserPortalOption();
	}
	
	function slideUpdownArea(target){
		var targetArea = (typeof $(target).attr("data-target-area") != 'undefined') ? $(target).attr("data-target-area") : 'PN_myContents';
		var slideHeight = ($(target).hasClass('active')) ? 0 : ((typeof $(target).attr("data-slide-height") != 'undefined') ? Number($(target).attr("data-slide-height")) : -547);
		$(target).toggleClass("active");
		$('.' + targetArea).css({ top: slideHeight});
		
		saveUserPortalOption();
	}
</script>