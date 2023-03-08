<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.AXGrid .AXgridScrollBody .AXGridBody .gridBodyTable tbody tr.selected td { background:#d2d9df; }
</style>
<div class='cRConTop titType'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.btn_apv_vacation_req' /></h2>
</div>
<div class='cRContBottom mScrollVH'>
	<div class="boardAllCont">
		<div class="boradTopCont">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
			<input id="schYearSel" type="hidden">
				<a href="#" class="btnTypeDefault btnPromotion" period="Code1" onclick="openPromotionPopup('plan', 'normal');" style="display:none"><spring:message code='Cache.lbl_vacation_plan_normal' /><!-- 연차사용계획서 --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code2" onclick="openPromotionPopup('request', 'normal');" style="display:none"><spring:message code='Cache.lbl_vacation_request_normal' /><!-- 1차 지정 요청서 --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code2" onclick="openPromotionPopup('notification1', 'normal');" style="display:none"><spring:message code='Cache.lbl_vacation_notification1_normal' /><!-- 1차 지정 통보서 --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code3" onclick="openPromotionPopup('notification2', 'normal');" style="display:none"><spring:message code='Cache.lbl_vacation_notification2_normal' /><!-- 2차 지정 통보서 --></a>

				<a href="#" class="btnTypeDefault btnPromotion" period="Code4" onclick="openPromotionPopup('plan', 'newEmp');" style="display:none"><spring:message code='Cache.lbl_vacation_plan_newEmp' /><!-- 1년 미만자 연차사용계획서 --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code5" onclick="openPromotionPopup('request', 'newEmpForNine');" style="display:none"><spring:message code='Cache.lbl_vacation_request_newEmpForNine' /><!-- 1년 미만자 1차 지정 요청서 (9일) --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code5" onclick="openPromotionPopup('notification1', 'newEmpForNine');" style="display:none"><spring:message code='Cache.lbl_vacation_notification1_newEmpForNine' /><!-- 1년 미만자 1차 지정 통보서 (9일) --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code6" onclick="openPromotionPopup('notification2', 'newEmpForNine');" style="display:none"><spring:message code='Cache.lbl_vacation_notification2_newEmpForNine' /><!-- 1년 미만자 2차 지정 통보서 (9일) --></a>

				<a href="#" class="btnTypeDefault btnPromotion" period="Code7" onclick="openPromotionPopup('request', 'newEmpForTwo');" style="display:none"><spring:message code='Cache.lbl_vacation_request_newEmpForTwo' /><!-- 1년 미만자 1차 지정 요청서 (2일) --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code7" onclick="openPromotionPopup('notification1', 'newEmpForTwo');" style="display:none"><spring:message code='Cache.lbl_vacation_notification1_newEmpForTwo' /><!-- 1년 미만자 1차 지정 통보서 (2일) --></a>
				<a href="#" class="btnTypeDefault btnPromotion" period="Code8" onclick="openPromotionPopup('notification2', 'newEmpForTwo');" style="display:none"><spring:message code='Cache.lbl_vacation_notification2_newEmpForTwo' /><!-- 1년 미만자 2차 지정 통보서 (2일) --></a>
				
				<a href="#" class="btnTypeDefault" style="visibility: hidden"></a>
			</div>
			<div class="buttonStyleBoxRight">	
				<select class="selectType02 listCount" id="listCntSel">
					<option>10</option>
					<option>15</option>
					<option>20</option>
					<option>30</option>
					<option>50</option>
				</select>
				<button href="#" class="btnRefresh" type="button" onclick="search()"></button>							
			</div>
		</div>
		<div>
			<div id="gridVacDiv" class="tblList tblCont" style="display:block;float:left;width:400px;"></div>
			<div style="display:block;float:right;width:calc(100% - 410px);">
				<div id="gridDiv" class="tblList tblCont"></div>
			</div>	
		</div>
	</div>
</div>

<script>
	var reqType = CFN_GetQueryString("reqType");	// reqType : commonInsert(공동연차등록), myVacation(나의휴가현황), vacationCancel(휴가취소처리)
	var testMode = CFN_GetQueryString("testMode"); //testMode : Y or N
	var gridVac = new coviGrid();
	var grid = new coviGrid();
	var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
	initContent();

	// 휴가신청, 공동연차등록, 나의휴가현황
	function initContent() {
		init();	// 초기화
		setGrid();	// 그리드 세팅
//		search();	// 검색
	}
	
	// 초기화
	function init() {
		// 화면 처리
		$("#reqTypeTxt").html("<spring:message code='Cache.MN_661' />");
		$("#schYearSel").val((new Date()).format('yyyy'))
		if(testMode==="Y"){
			$(".btnPromotion").each(function (){
				$(this).show();
			});
		}else{
			$.ajax({
				type : "POST",
				data :  {
					year : (new Date()).format('yyyy'),
					urCode : Common.getSession("USERID")
				},
				async: false,
				url : "/groupware/vacation/getVacationFacilitatingDateList.do",
				success:function (data) {
					var row = data.list;

					if(row !== undefined && row.length > 0){
						var IsOneYear = Number(row[0].IsOneYear);
						var nowDate = Number(new Date(CFN_GetLocalCurrentDate('yyyy/MM/dd')).format('yyyyMMdd'));
						var VacDate = Number(new Date(row[0].VacDate.replaceAll('-','/')).format('yyyyMMdd'));
						var VacDateUntil = Number(new Date(row[0].VacDateUntil.replaceAll('-','/')).format('yyyyMMdd'));
						//console.log(nowDate+"/"+VacDate+"/"+VacDateUntil);
						var OneDate = new Date(row[0].OneDate.replaceAll('-','/')).format('yyyyMMdd');
						var OneDateUntil = new Date(row[0].OneDateUntil.replaceAll('-','/')).format('yyyyMMdd');
						var TwoDate = new Date(row[0].TwoDate.replaceAll('-','/')).format('yyyyMMdd');
						var TwoDateUntil = new Date(row[0].TwoDateUntil.replaceAll('-','/')).format('yyyyMMdd');
						var LessVacDate = new Date(row[0].LessVacDate.replaceAll('-','/')).format('yyyyMMdd');
						var LessVacDateUntil = new Date(row[0].LessVacDateUntil.replaceAll('-','/')).format('yyyyMMdd');
						var LessOneDate9 = new Date(row[0].LessOneDate9.replaceAll('-','/')).format('yyyyMMdd');
						var LessOneDate9Until = new Date(row[0].LessOneDate9Until.replaceAll('-','/')).format('yyyyMMdd');
						var LessTwoDate9 = new Date(row[0].LessTwoDate9.replaceAll('-','/')).format('yyyyMMdd');
						var LessTwoDate9Until = new Date(row[0].LessTwoDate9Until.replaceAll('-','/')).format('yyyyMMdd');
						var LessOneDate2 = new Date(row[0].LessOneDate2.replaceAll('-','/')).format('yyyyMMdd');
						var LessOneDate2Until = new Date(row[0].LessOneDate2Until.replaceAll('-','/')).format('yyyyMMdd');
						var LessTwoDate2 = new Date(row[0].LessTwoDate2.replaceAll('-','/')).format('yyyyMMdd');
						var LessTwoDate2Until = new Date(row[0].LessTwoDate2Until.replaceAll('-','/')).format('yyyyMMdd');
						var Code1 = (IsOneYear==1&&nowDate>=VacDate && nowDate <= VacDateUntil)?true:false;
						var Code2 = (IsOneYear==1&&nowDate>=OneDate && nowDate <= OneDateUntil)?true:false;
						var Code3 = (IsOneYear==1&&nowDate>=TwoDate && nowDate <= TwoDateUntil)?true:false;
						
						var Code4 = (IsOneYear==0&&nowDate>=LessVacDate && nowDate <= LessVacDateUntil)?true:false;
						var Code5 = (IsOneYear==0&&nowDate>=LessOneDate9 && nowDate <= LessOneDate9Until)?true:false;
						var Code6 = (IsOneYear==0&&nowDate>=LessTwoDate9 && nowDate <= LessTwoDate9Until)?true:false;
						var Code7 = (IsOneYear==0&&nowDate>=LessOneDate2 && nowDate <= LessOneDate2Until)?true:false;
						var Code8 = (IsOneYear==0&&nowDate>=LessTwoDate2 && nowDate <= LessTwoDate2Until)?true:false;
						
						var codeJson = new Object();
						codeJson["Code1"] = Code1;
						codeJson["Code2"] = Code2;
						codeJson["Code3"] = Code3;
						codeJson["Code4"] = Code4;
						codeJson["Code5"] = Code5;
						codeJson["Code6"] = Code6;
						codeJson["Code7"] = Code7;
						codeJson["Code8"] = Code8;
						
						$(".btnPromotion").each(function (idx, obj){
							if(new Function ("return " + codeJson[$(this).attr("period")]).apply()){
								$(this).show();
							}
						});
					}

				},
				error:function(response, status, error) {
					CFN_ErrorAjax(url, response, status, error);
				}
			});
		}
		/*
		var nowYear = new Date().getFullYear();
		// 년도 option 생성	
		for (var i=2; i>-4;i--) {
			var temp = nowYear + i;
			if (temp == nowYear) {
			    $('#schYearSel').append($('<option>', {
			        value: temp,
			        text : temp,
			        selected : 'selected'
			    }));				
			} else {
			    $('#schYearSel').append($('<option>', {
			        value: temp,
			        text : temp
			    }));
			}
		}
		*/
		// 검색 버튼
		$('.btnSearchBlue').on('click', function(e) {
			search();
		});
		
		// 상세 보기
 		$('.btnDetails').on('click', function() {
			var mParent = $('.inPerView');
			if(mParent.hasClass('active')){
				mParent.removeClass('active');
				$(this).removeClass('active');
			}else {
				mParent.addClass('active');
				$(this).addClass('active');
			}
		});
		
		// 그리드 카운트
		$('#listCntSel').on('change', function(e) {
			grid.page.pageSize = $(this).val();
			CFN_SetCookieDay("VacListCnt", $(this).find("option:selected").val(), 31536000000);
			grid.reloadList();
		});
		
		// 년도
		/*$('#schYearSel').on('change', function(e) {
			search();
		});
		*/
		getUserVacationInfo();	// 연차 사용정보

		
 		$('.btnOnOff').unbind('click').on('click', function(){
			if($(this).hasClass('active')){
				$(this).removeClass('active');
				$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').removeClass('active');
			}else {
				$(this).addClass('active');
				$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').addClass('active');			
			}	
		});
	}
	
	// 그리드 세팅
	function setGrid() {
		// header
		var	headerData = [
			{key:'APPDATE', label:'<spring:message code="Cache.lbl_DraftDate" />', width:'50', align:'center'},
			{key:'ENDDATE', label:'<spring:message code="Cache.lbl_apv_EndDate" />', width:'50', align:'center'},
			{key:'GubunName', label:'<spring:message code="Cache.lbl_Gubun" />', width:'50', align:'center'},
			{key:'VACTEXT', label:'<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />', width:'50', align:'center',
				formatter:function () {
					var html = "<div>";
					var isLink = false;
					if (this.item.GUBUN == 'VACATION_PUBLIC' || this.item.GUBUN == 'VACATION_PUBLIC_CANCEL') {
						isLink = true;
						html += "<a href='#' onclick='Common.Inform(\"<spring:message code='Cache.lbl_vacationMsg25' />\"); return false;'>";
					} else {
						if(Number(this.item.EXIST_APPROVAL_FORM) > 0) {
							isLink = true;
							html += "<a href='#' onclick='openVacationViewPopup(\"" + this.item.UR_Code + "\", \"" + this.item.ProcessID + "\", \"" + this.item.WorkItemID + "\"); return false;'>";
						}else if(Number(this.item.EXIST_REQUEST_FORM) > 0) {
							isLink = true;
							var popupUrl	= "/groupware/attendRequestMng/AttendRequestDetailPopup.do?"
									+ "popupID=AttendRequestDetailPopup&"
									+ "openerID=AttendRequestDetail&"
									+ "popupYN=N&"
									+ "UserName="		+ this.item.DisplayName	+ "&"
									+ "ReqType=V&"
									+ "UserCode="		+ this.item.UR_Code	+ "&"
									+ "ReqSeq="		+ this.item.EXIST_REQUEST_FORM+ "&"
									+ "callBackFunc=AttendJobDetailPopup_CallBack";

							html += "<a href='#' onclick='Common.open(\"\", \"AttendRequestDetailPopup\", \""+this.item.DisplayName+" ("+this.item.ReqTitle+")"+"\", \""+popupUrl+"\", \"650px\", \"700px\", \"iframe\", true, null, null, true);'>";
						}
					}
					html += this.item.VACTEXT;
					if(isLink) {
						html += "</a>";
					}
					html += "</div>";
						
					return html;
			}},
			{key:'Sdate', label:'<spring:message code="Cache.lbl_startdate" />', width:'50', align:'center'},
			{key:'Edate', label:'<spring:message code="Cache.lbl_EndDate" />', width:'50', align:'center'},
			{key:'VacDay', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'50', align:'center',
				formatter:function () {
					return Number(this.item.VacDay);
				}
			},
			{key:'Reason', label:'<spring:message code="Cache.lbl_Reason" />', width:'200', align:'left'}
		];
		grid.setGridHeader(headerData);
		
		// config
		grid.setGridConfig({
				targetID : "gridDiv",
				page : {
					pageNo: (page != undefined && page != '')?page:1,
					pageSize: (pageSize != undefined && pageSize != '')?pageSize:10,
				},
				height:"auto"
			});
		
		
		var	headerVacData = [
		   				{key:'VacName', label:'<spring:message code="Cache.lbl_type" />', width:'50', align:'center',
		   					formatter:function () {
		   						return (this.item.VacKind=="PUBLIC"?this.item.Year+" ":"")+this.item.VacName;
		   					}
		   				},
		   				{key:'ExpDate', label:'<spring:message code="Cache.lbl_expiryDate" />', width:'100', align:'center'},
		   				{key:'VacDay', label:'<spring:message code="Cache.lbl_apv_Vacation_days" />', width:'50', align:'center'},
		   				{key:'RemainVacDay', label:'<spring:message code="Cache.lbl_appjanyu" />', width:'50', align:'center'}
		   			];
		gridVac.setGridHeader(headerVacData);
		gridVac.setGridConfig({
			targetID : "gridVacDiv",
			page : {
				pageNo: (page != undefined && page != '')?page:1,
				pageSize: (pageSize != undefined && pageSize != '')?pageSize:10,
			},
			height:"auto",
			body : {
				addClass: function() {
					if (this.item.CurYear == "Y") return "ERBoxbg";
		        },
				onclick: function() {
					search(gridVac.getSelectedItem());
				}			
			},
			page : false
		});
	}

	// 검색
	function search(row) {
		var params = {"VacKind" : row.item.VacKind,
					  "Sdate" : row.item.Sdate};
		
		grid.page.pageNo = 1;
		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getMyVacationInfoList.do",
			ajaxPars : params,
			
		});
		
	}
	// 연차 사용정보
	function getUserVacationInfo() {
		gridVac.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationListByKind.do",
			ajaxPars : {year : $('#schYearSel').val(), reqType:"myVacation"},
			onLoad:function(){gridVac.click(0);}
		});

		/*
		$.ajax({
			type : "POST",
			data : {year : $('#schYearSel').val(), reqType:"myVacation"},
			async: false,
			url : "/groupware/vacation/getVacationListByKind.do",
			success:function (list) {
				var data = list.list[0];
				var text = "( <spring:message code='Cache.lbl_total' /> " + data.VacDay + "<spring:message code='Cache.lbl_day' />, <spring:message code='Cache.lbl_UseVacation' /> " + data.VacDayUse + "<spring:message code='Cache.lbl_day' />, <spring:message code='Cache.lbl_RemainVacation' /> " + data.RemainVacDay + "<spring:message code='Cache.lbl_day' /> )"
				
				$('#myVacText').html(text);
			},
			error:function(response, status, error) {
				CFN_ErrorAjax("/groupware/vacation/getVacationListByKind.do", response, status, error);
			}
		});*/
	}
	
	// 휴가 신청/취소 내역
	function openVacationViewPopup(urCode, processId, workItemId) {
//		CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&workitemID="+workItemId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
		CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
	}
	
	function openPromotionPopup(formType, empType){
		var year = $('#schYearSel').val();
		var params = "?year="+year+"&viewType=user&formType=" + formType + "&empType=" + empType;
		
		CFN_OpenWindow("/groupware/vacation/goVacationPromotionPopup.do" + params, "", 960, (window.screen.height - 100), "scroll");
	}
	
 	// 연차촉진제 서식출력 1,2차
	function openVacationPromotion1Popup(time) {
 		var year = $('#schYearSel').val();
		CFN_OpenWindow("/groupware/vacation/goVacationPromotion1Popup.do?year="+year+"&viewType=user&isAll=N&time="+time, "", 960, (window.screen.height - 100), "scroll");
	}
	
	// 연차촉진제 서식출력 3
	function openVacationPromotion3Popup() {
 		var year = $('#schYearSel').val();
		CFN_OpenWindow("/groupware/vacation/goVacationPromotion3Popup.do?year="+year+"&viewType=user&time=3", "", 960, (window.screen.height - 100), "scroll");
	}

	// 사용시기 지정통보서
	function openVacationUsePlanPopup(time) {
 		var year = $('#schYearSel').val();
		CFN_OpenWindow("/groupware/vacation/goVacationUsePlanPopup.do?year="+year+"&time="+time+"&viewType=user&isAll=N", "", 960, (window.screen.height - 100), "scroll");
	}
	
</script>