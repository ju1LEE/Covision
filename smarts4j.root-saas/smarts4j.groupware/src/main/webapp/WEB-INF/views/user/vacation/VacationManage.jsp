<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class='cRConTop titType'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.MN_663' /></h2>
	<div class="searchBox02">
		<span><input type="text" id="schUrName"/><button type="button" class="btnSearchType01" onclick="search()"><spring:message code='Cache.btn_search' /></button></span>
		<a href="#" class="btnDetails active"><spring:message code='Cache.lbl_detail' /></a>
	</div>	
</div>
<div class='cRContBottom mScrollVH'>
	<div class="inPerView type02 active">
		<div style="width: 550px;">
			<div class="selectCalView">
				<select class="selectType02" id="schYearSel">
				</select>				
				<select class="selectType02" id="schEmploySel">
						<option value=""><spring:message code='Cache.lbl_Whole' /></option>
					    <option value="INOFFICE" selected="selected"><spring:message code="Cache.lbl_inoffice" /></option>
					    <option value="RETIRE"><spring:message code="Cache.msg_apv_359" /></option>
				</select>
				<select class="selectType02" id="schTypeSel">
					<%-- <option value=""><spring:message code='Cache.lbl_apv_searchcomment' /></option> --%>
				    <option value="deptName"><spring:message code="Cache.lbl_dept" /></option>
				    <option value="displayName" selected="selected"><spring:message code="Cache.lbl_username" /></option>
				</select>
				<div class="dateSel type02">
					<input type="text" id="schTxt">
				</div>											
			</div>
			<div>
				<a href="#" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code='Cache.btn_search' /></a>
			</div>
		</div>
	</div>
	<div class="boardAllCont">
		<div class="mt20 tabMenuCont">
			<ul class="tabMenu clearFloat tabMenuType02">
				<li class="topToggle active"><a href="#" onclick="toggleTab('1');"><spring:message code='Cache.lbl_apv_annual' /> <spring:message code='Cache.lbl_Occurrence' /> <spring:message code='Cache.TodoCategory_Admin' /></a></li>
				<li class="topToggle"><a href="#" onclick="toggleTab('2');"><spring:message code='Cache.lbl_Etc' /><spring:message code='Cache.lbl_Vacation' /> <spring:message code='Cache.lbl_Occurrence' /> <spring:message code='Cache.TodoCategory_Admin' /></a></li>
				<li class="topToggle"><a href="#" onclick="toggleTab('3');"><spring:message code='Cache.lbl_Joint' /><spring:message code='Cache.lbl_apv_annual' /> <spring:message code='Cache.lbl_Use' /> <spring:message code='Cache.TodoCategory_Admin' /></a></li>
			</ul>
		</div>
		<div class="boradTopCont" id="boradTopCont_1" style="display: block;">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
				<a href="#" class="btnTypeDefault btnTypeChk" id="insVacBtn" onclick="openVacationInsertPopup();" style="display:none"><spring:message code='Cache.btn_register' /></a>
				<a href="#" class="btnTypeDefault" id="vacPeriodBtn" onclick="openVacationPeriodManagePopup();" style="display:none"><spring:message code='Cache.lbl_apv_setVacTerm' /></a>
				<a href="#" class="btnTypeDefault btnRepeatGray" id="btnDeptInfo" onclick="updateDeptInfo();" style="display:none"><spring:message code='Cache.lbl_attend_currentYearSync' /></a><!-- 현재년도 부서정보 동기화 -->
				<a href="#" class="btnTypeDefault btnExcel" id="excelUpBtn" onclick="openVacationInsertByExcelPopup();" style="display:none"><spring:message code='Cache.Personnel_ExcelAdd' /></a>
				<a href="#" class="btnTypeDefault btnExcel" onclick="excelListDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a> 
				<div id=divDate>
			        <span class="dateTip">
			        	<spring:message code='Cache.msg_occVac'/>
			        </span>
		        </div>
				
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
		<div class="boradTopCont" id="boradTopCont_2" style="display: none;">
			<div class="pagingType02 buttonStyleBoxLeft" >
				<a href="#" class="btnTypeDefault btnTypeChk" id="insExtraVacBtn" onclick="openExtraVacationInsertPopup();"><spring:message code='Cache.btn_register' /></a>
				<%--<a href="#" class="btnTypeDefault btnExcel" id="excelDownBtn2" onclick="excelDownload2();"><spring:message code='Cache.lbl_templatedownload' /></a>
				<a href="#" class="btnTypeDefault btnExcel" id="excelUpBtn2" onclick="openVacationInsertByExcelPopup2();"><spring:message code='Cache.btn_ExcelUpload' /></a>
				<a href="#" class="btnTypeDefault btnExcel" id="excelCanBtn2" onclick="openVacationCancelByExcelPopup2();"><spring:message code='Cache.lbl_common_vacation_cancel' /></a>--%>
				<a href="#" class="btnTypeDefault btnExcel" onclick="excelListDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a> 
					
				<a href="#" class="btnTypeDefault" style="visibility: hidden"></a>
				<div id=divDate>
			        <span class="dateTip">
			        	<spring:message code='Cache.msg_occVac'/>
			        </span>
		        </div>
        	</div>
			<div class="buttonStyleBoxRight">
				<select class="selectType02 listCount" id="listCntSel2">
					<option>10</option>
					<option>15</option>
					<option>20</option>
					<option>30</option>
					<option>50</option>
				</select>
				<button href="#" class="btnRefresh" type="button" onclick="search()"></button>
			</div>
		</div>
		<div class="boradTopCont" id="boradTopCont_3" style="display: none;">
			<div class="pagingType02 buttonStyleBoxLeft" >
<!--				<a href="#" class="btnTypeDefault btnExcel" id="excelDownBtn" onclick="excelDownload();" style="display:block;"><spring:message code='Cache.lbl_templatedownload' /></a>-->
				<a href="#" class="btnTypeDefault btnExcel" id="excelUpBtn3" onclick="openVacationInsertByExcelPopup3();"><spring:message code='Cache.Personnel_ExcelAdd' /></a>
				<a href="#" class="btnTypeDefault btnExcel" id="excelCanBtn" onclick="openVacationCancelByExcelPopup3();"><spring:message code='Cache.lbl_common_vacation_cancel' /></a>
				<a href="#" class="btnTypeDefault btnExcel" onclick="excelListDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a> 
					

				<a href="#" class="btnTypeDefault" style="visibility: hidden"></a>
				<div id=divDate>
			        <span class="dateTip">
			        	<spring:message code='Cache.msg_useVac'/>
			        </span>
		        </div>
			</div>
			<div class="buttonStyleBoxRight">
				<select class="selectType02 listCount" id="listCntSel3">
					<option>10</option>
					<option>15</option>
					<option>20</option>
					<option>30</option>
					<option>50</option>
				</select>
				<button href="#" class="btnRefresh" type="button" onclick="search()"></button>
			</div>
		</div>
		<div class="tblList tblCont">
			<div id="gridDiv" style="display: block;"></div>
			<div id="gridDiv2" style="display: none;"></div>
			<div id="gridDiv3" style="display: none;"></div>
		</div>
	</div>
</div>

<script>
	var tabNo = 1;
	var reqType = CFN_GetQueryString("reqType");	// reqType : manage(연차관리),  promotionPeriod(연차촉진 기간설정)
	var grid = new coviGrid();
	var grid2 = new coviGrid();
	var grid3 = new coviGrid();
	var duplChkUserIdArr = new Array();
	var g_CreateMethod = "F";
	var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
	initContent();

	// 연차관리, 연차촉진, 연차촉진기간설정
	function initContent() {
		getVacationConfigVal();
		init();	// 초기화
		search();	// 검색
	}

	function getVacationConfigVal(){
		$.ajax({
			type : "POST",
			url : "/groupware/vacation/getVacationConfigVal.do",
			data: {
				getName: "CreateMethod"
			},
			async:false,
			success:function (data) {
				if(data.status == 'SUCCESS') {
					if(data.data!=null && data.data!=""){
						g_CreateMethod = data.data;
					}	
				} else {
					Common.Warning("<spring:message code='Cache.msg_apv_030' />");
				}
			},
			error:function(response, status, error) {
				CFN_ErrorAjax(url, response, status, error);
			}
		});
	}

	// 초기화
	function init() {
		// 그리드 카운트
		$('#listCntSel').on('change', function (e) {
			var pageSize = $(this).val();
			grid.page.pageSize  = pageSize;
			grid2.page.pageSize = pageSize;
			grid3.page.pageSize = pageSize;
			$("#listCntSel2").val(pageSize).prop("selected", true);
			$("#listCntSel3").val(pageSize).prop("selected", true);
			CFN_SetCookieDay("VacListCnt", $(this).find("option:selected").val(), 31536000000);
			grid.reloadList();
		});
		$('#listCntSel2').on('change', function (e) {
			var pageSize = $(this).val();
			grid.page.pageSize  = pageSize;
			grid2.page.pageSize = pageSize;
			grid3.page.pageSize = pageSize;
			$("#listCntSel").val(pageSize).prop("selected", true);
			$("#listCntSel3").val(pageSize).prop("selected", true);
			grid2.reloadList();
		});
		$('#listCntSel3').on('change', function (e) {
			var pageSize = $(this).val();
			grid.page.pageSize  = pageSize;
			grid2.page.pageSize = pageSize;
			grid3.page.pageSize = pageSize;
			$("#listCntSel").val(pageSize).prop("selected", true);
			$("#listCntSel2").val(pageSize).prop("selected", true);
			grid3.reloadList();
		});
		// 화면 처리
//		$("#reqTypeTxt").html("<spring:message code='Cache.MN_663' />");
//		$('#excelDownBtn').html('<spring:message code="Cache.lbl_templatedownload" />');
//		$('#excelUpBtn').html('<spring:message code="Cache.Personnel_ExcelAdd" />');
		$('#genVacBtn, #insVacBtn, #excelDownBtn, #excelUpBtn').css('display', '');
		$('#btnDeptInfo').css('display', '');

		var nowYear = new Date().getFullYear();
		// 년도 option 생성
		for (var i = 2; i > -4; i--) {
			var temp = nowYear + i;
			if (temp == nowYear) {
				$('#schYearSel').append($('<option>', {
					value: temp,
					text: temp,
					selected: 'selected'
				}));
			} else {
				$('#schYearSel').append($('<option>', {
					value: temp,
					text: temp
				}));
			}
		}

		$('#schUrName').on('keypress', function (e) {
			if (e.which == 13) {
				e.preventDefault();

				var schName = $('#schUrName').val();

				$('#schTypeSel').val('displayName');
				$('#schTxt').val(schName);

				search();
			}
		});

		$('#schTxt').on('keypress', function (e) {
			if (e.which == 13) {
				e.preventDefault();

				search();
			}
		});


		// 검색 버튼
		$('.btnSearchBlue').on('click', function (e) {
			search();
		});

		// 상세 보기
		$('.btnDetails').on('click', function () {
			var mParent = $('.inPerView');
			if (mParent.hasClass('active')) {
				mParent.removeClass('active');
				$(this).removeClass('active');
			} else {
				mParent.addClass('active');
				$(this).addClass('active');
			}
		});


		$('.btnOnOff').unbind('click').on('click', function () {
			if ($(this).hasClass('active')) {
				$(this).removeClass('active');
				$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').removeClass('active');
			} else {
				$(this).addClass('active');
				$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').addClass('active');
			}
		});

	}

	// 그리드 세팅
	function setGrid() {
		// header
		var headerData = null;
		if(tabNo===1) {
			headerData = [
				{
					key: 'DeptName', label: '<spring:message code="Cache.lbl_dept" />', width: '80', align: 'center',
					formatter: function () {
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openVacationUpdatePopup(\"" + this.item.UR_Code + "\", \"" + this.item.YEAR + "\"); return false;'>";
						if (typeof (this.item.DeptName) != 'undefined') html += CFN_GetDicInfo(this.item.DeptName);
						html += "</a>";
						html += "</div>";

						return html;
					}
				},
				{
					key: 'DisplayName',
					label: '<spring:message code="Cache.lbl_username" />',
					width: '70',
					align: 'center',
					formatter: function () {
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openVacationUpdatePopup(\"" + this.item.UR_Code + "\", \"" + this.item.YEAR + "\"); return false;'>";
						if (typeof (this.item.DisplayName) != 'undefined') html += this.item.DisplayName;
						html += "</a>";
						html += "</div>";

						return html;
					}
				},
				{
					key: 'EnterDate',
					label: '<spring:message code="Cache.lbl_EnterDate" />',
					width: '60',
					align: 'center'
				},
				{
					key: 'JobPositionName',
					label: '<spring:message code="Cache.lbl_apv_jobposition" />',
					width: '60',
					align: 'center',
					formatter: function () {
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openVacationUpdatePopup(\"" + this.item.UR_Code + "\", \"" + this.item.YEAR + "\"); return false;'>";
						if (typeof (this.item.JobPositionName) != 'undefined') html += this.item.JobPositionName;
						html += "</a>";
						html += "</div>";

						return html;
					}
				},{key:'ExpDate', label:'<spring:message code="Cache.lbl_expiryDate" />', width:'100', align:'center',
					formatter: function () {
						return this.item.ExpDateStart+" ~ "+this.item.ExpDateEnd;
					}
				},//유효기간
				{
					key: 'VacDay',
					label: '<spring:message code="Cache.lbl_TotalVacation" />',
					width: '50',
					align: 'center',
					formatter:function(){
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openVacationPlanHistPopup(\"" + this.item.UR_Code + "\", \"" + this.item.ExpDateStart + "\", \"" + this.item.ExpDateEnd + "\"); return false;'>";
						if (typeof (this.item.VacDay) != 'undefined') html += this.item.VacDay;
						html += "</a>";
						html += "</div>";

						return html;
					}
				},{key:'UseVacDay', label:'<spring:message code="Cache.lbl_Use" />', width:'50', align:'center', sort:false},//사용
				{key:'RemainVacDay', label:'<spring:message code="Cache.lbl_n_att_remain" />', width:'50', align:'center', sort:false},//잔여
				{key:'LastVacDay', label:'<spring:message code="Cache.lbl_vacationMsg64" /><spring:message code="Cache.VACATION_TYPE_VACATION_ANNUAL" />', hideFilter: (Common.getBaseConfig("DisplayLastVacDay") == "N" ? "Y" : "N"), display:(Common.getBaseConfig("DisplayLastVacDay") == "N" ? false : true), width:'50', align:'center', sort:false}, //이월년차
				{key:'Reason', label:'<spring:message code="Cache.lbl_Reason" />', width:'120', align:'left', sort:false}
			];

			grid.setGridHeader(headerData);

			// config
			grid.setGridConfig({
				targetID: "gridDiv",
				height: "auto"
			});
		}else if(tabNo===2){
			headerData = [
				{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'50', align:'center',
					formatter:function() {
						return CFN_GetDicInfo(this.item.DeptName);
					}},
				{key:'DisplayName', label:'<spring:message code="Cache.lbl_name" />', width:'50', align:'center', addClass:'bodyTdFile',
					formatter:function(){
						var html ="<div class='btnFlowerName' onclick='coviCtrl.setFlowerName(this)' style='position:relative;cursor:pointer' data-user-code='"+ this.item.UR_Code +"' data-user-mail=''>"
						html += this.item.DisplayName;
						html += "</div>";
							
						return html;
					}		
				},
				{key:'ExtVacName', label:'<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />', width:'50', align:'center'},
				{key:'ExtVacDay', label:'<spring:message code="Cache.lbl_Vacation" />', width:'50', align:'center',
					formatter: function () {
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openExtraVacationUpdatePopup(\"" + this.item.UR_Code + "\", \"" + this.item.ExtVacType + "\", \"" + this.item.ExtSdate + "\", \"" + this.item.ExtEdate + "\", \"" + this.item.ExtVacYear + "\"); return false;'>";
						if (typeof (this.item.ExtVacDay) != 'undefined') html += this.item.ExtVacDay;
						html += "</a>";
						html += "</div>";

						return html;
					}
				},
				{key:'RegisterName', label:'<spring:message code="Cache.ACC_lbl_registerName" />', width:'50', align:'center', addClass:'bodyTdFile',
					formatter:function(){
						var html ="<div class='btnFlowerName' onclick='coviCtrl.setFlowerName(this)' style='position:relative;cursor:pointer' data-user-code='"+ this.item.RegisterCode +"' data-user-mail=''>"
						html += this.item.RegisterName;
						html += "</div>";
							
						return html;
					}	
				},//부여자
				{key:'RegistDate', label:'<spring:message code="Cache.lbl_registeDate" />', width:'50', align:'center'},//생성일
				{key:'ExpDate', label:'<spring:message code="Cache.lbl_expiryDate" />', width:'80', align:'center'},//유효기간
				{key:'ExtUseVacDay', label:'<spring:message code="Cache.lbl_Use" />', width:'30', align:'center'},//사용
				{key:'ExtRemainVacDay', label:'<spring:message code="Cache.lbl_n_att_remain" />', width:'30', align:'center'},//잔여
				{key:'IsUse', label:'<spring:message code="Cache.lbl_IsUse" />', width:'50', align:'center',
					formatter: function () {
						if (this.item.IsUse==='Y'){
							return "사용가능";
						}else{
							return "사용불가";
						}
					}
				},
				{key:'ExtReason', label:'<spring:message code="Cache.lbl_Reason" />', width:'100', align:'left'}
			];


			grid2.setGridHeader(headerData);

			// config
			var configObj = {
				targetID : "gridDiv2"
				,page : {
					pageNo: (page != undefined && page != '')?page:1,
					pageSize: (pageSize != undefined && pageSize != '')?pageSize:10,
						}
				,mergeCells:[1,2]
				,height:"auto"
			};
			grid2.setGridConfig(configObj);
		}else if(tabNo===3){
			headerData = [
				{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'50', align:'center',
					formatter:function() {
						return CFN_GetDicInfo(this.item.DeptName);
					}},
				{key:'DisplayName', label:'<spring:message code="Cache.lbl_name" />', width:'50', align:'center',
					formatter:function () {
						var html = "<div>";
						html += "<a href='#' onclick='alert(\"<spring:message code='Cache.lbl_vacationMsg25' />\"); return false;'>";
						html += this.item.DisplayName;
						html += "</a>";
						html += "</div>";

						return html;
					}
				},
				{key:'GubunName', label:'<spring:message code="Cache.lbl_Gubun" />', width:'50', align:'center',
					formatter:function () {
						var html = "<div>";
						var gubunName = this.item.GubunName;
						if (this.item.GUBUN == 'VACATION_PUBLIC' || this.item.GUBUN == 'VACATION_APPLY') {
							html += "<a href='#' onclick='openVacationCancelPopup(\"" + this.item.VacationInfoID + "\", \"" + this.item.VacYear + "\"); return false;'>";
							html += gubunName;
							html += "</a>";
						} else {
							html += gubunName;
						}
						html += "</div>";

						return html;
					}
				},
				{key:'VacFlagName', label:'<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />', width:'50', align:'center'},
				{key:'APPDATE', label:'<spring:message code="Cache.lbl_apv_approvdate" />', width:'50', align:'center'},
				{key:'Sdate', label:'<spring:message code="Cache.lbl_startdate" />', width:'50', align:'center'},
				{key:'Edate', label:'<spring:message code="Cache.lbl_EndDate" />', width:'50', align:'center'},
				{key:'VacDay', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'50', align:'center'},
				{key:'Reason', label:'<spring:message code="Cache.lbl_Reason" />', width:'200', align:'left'}
			];


			grid3.setGridHeader(headerData);

			// config
			var configObj = {
				targetID : "gridDiv3",
				height:"auto"
			};
			grid3.setGridConfig(configObj);
		}
	}
	// 엑셀 파일 다운로드
	function excelListDownload() {
		// 연차관리
		if(tabNo===1){
 			location.href = "/groupware/vacation/excelDownVacationDayList.do?"
 				+"year="+$('#schYearSel').val()
 				+"&schTypeSel="+$('#schTypeSel').val()
 				+"&schEmploySel="+$('#schEmploySel').val()
 				+"&reqType="+reqType
 				+"&schTxt="+$('#schTxt').val();
 			
		// 기타연차관리
		}else if(tabNo===2){
 			location.href = "/groupware/vacation/excelDownVacationExtraList.do?"
 				+"urName="+$('#schUrName').val()
 				+"&year="+$('#schYearSel').val()
 				+"&schTypeSel="+$('#schTypeSel').val()
 				+"&schTxt="+$('#schTxt').val();
		
 		// 공통연차관리
		}else if(tabNo===3){
 			location.href = "/groupware/vacation/excelDownVacationInfoList.do?"
 				+"urName="+$('#schUrName').val()
 				+"&year="+$('#schYearSel').val()
 				+"&reqType="+reqType
 				+"&schTypeSel="+$('#schTypeSel').val()
 				+"&schTxt="+$('#schTxt').val();
		}
	}
	// 검색
	function search() {
		setGrid();
		var params = null;
		if(tabNo===1){
			params = {
				year : $('#schYearSel').val(),
				schTypeSel : $('#schTypeSel').val(),
				schEmploySel : $('#schEmploySel').val(),
				reqType : reqType,
				schTxt : $('#schTxt').val()
			};

			grid.page.pageSize  = $("#listCntSel option:selected").val();
			// bind
			grid.bindGrid({
				ajaxUrl : "/groupware/vacation/getVacationDayList.do",
				ajaxPars : params
			});
		}else if(tabNo===2){
			params = {
				urName : $('#schUrName').val(),
				year : $('#schYearSel').val(),
				schTypeSel : $('#schTypeSel').val(),
				schTxt : $('#schTxt').val()
			};
			grid2.page.pageSize  = $("#listCntSel2 option:selected").val();
			// bind
			grid2.bindGrid({
				ajaxUrl : "/groupware/vacation/getVacationExtraList.do",
				ajaxPars : params
			});
		}else if(tabNo===3){
			params = {
				urName : $('#schUrName').val(),
				year : $('#schYearSel').val(),
				reqType : reqType,
				schTypeSel : $('#schTypeSel').val(),
				schTxt : $('#schTxt').val()
			};
			grid3.page.pageSize  = $("#listCntSel3 option:selected").val();
			// bind
			grid3.bindGrid({
				ajaxUrl : "/groupware/vacation/getVacationManageList.do",
				ajaxPars : params
			});

		}
	}


	function toggleTab(t){
		$(".topToggle").attr("class","topToggle");

		if(t==="1"){
			tabNo = 1;
			$(".topToggle").eq(0).attr("class","topToggle active");
			$("#boradTopCont_1").show();
			$("#boradTopCont_2").hide();
			$("#boradTopCont_3").hide();
			$("#gridDiv").show();
			$("#gridDiv2").hide();
			$("#gridDiv3").hide();
			$("#schEmploySel").show();
		}else if(t==="2"){
			tabNo = 2;
			$(".topToggle").eq(1).attr("class","topToggle active");
			$("#boradTopCont_1").hide();
			$("#boradTopCont_2").show();
			$("#boradTopCont_3").hide();
			$("#gridDiv").hide();
			$("#gridDiv2").show();
			$("#gridDiv3").hide();
			$("#schEmploySel").hide();
		}else if(t==="3"){
			tabNo = 3;
			$(".topToggle").eq(2).attr("class","topToggle active");
			$("#boradTopCont_1").hide();
			$("#boradTopCont_2").hide();
			$("#boradTopCont_3").show();
			$("#gridDiv").hide();
			$("#gridDiv2").hide();
			$("#gridDiv3").show();
			$("#schEmploySel").hide();
		}
		search();
	}
	
	// 연차등록 버튼
	function openVacationInsertPopup() {
		var year = $('#schYearSel').val();
		
		Common.open("","target_pop", year + "<spring:message code='Cache.lblNyunDo' /> : <spring:message code='Cache.lbl_apv_Vacation_days' /> <spring:message code='Cache.btn_register' />","/groupware/vacation/goVacationInsertPopup.do?year="+year,"500px","265px","iframe",true,null,null,true);
	}

	// 기타휴가등록 버튼
	function openExtraVacationInsertPopup() {
		var year = $('#schYearSel').val();

		Common.open("","target_pop", year + "<spring:message code='Cache.lblNyunDo' /> : <spring:message code='Cache.lbl_apv_Vacation_days' /> <spring:message code='Cache.btn_register' />","/groupware/vacation/goExtraVacationInsertPopup.do?year="+year,"630px","420px","iframe",true,null,null,true);
	}

	// 그리드 클릭
	function openVacationUpdatePopup(urCode, year) {
		var parYear = $("#schYearSel option:selected").val();
		Common.open("","target_pop","<spring:message code='Cache.lbl_apv_Vacation_days' />","/groupware/vacation/goVacationUpdatePopup.do?urCode="+urCode+"&year="+parYear,"499px","281px","iframe",true,null,null,true);
	}

	function openExtraVacationUpdatePopup(urCode, vacKind, sDate, eDate, year) {
		Common.open("","target_pop","<spring:message code='Cache.lbl_apv_vacation_etc' /><spring:message code='Cache.lbl_apv_Vacation_days' />","/groupware/vacation/goExtraVacationUpdatePopup.do?urCode="+urCode+"&year="+year+"&vacKind="+vacKind+"&sDate="+sDate+"&eDate="+eDate,"499px","401px","iframe",true,null,null,true);
	}
	function openVacationPlanHistPopup(urCode, startDate, endDate){
		var popupTit	= "<spring:message code='Cache.lbl_vacationMsg52' />";
		var popupUrl	= "/groupware/vacation/goVacationPlanHistPopup.do?"
						+ "urCode="			+ urCode	+ "&"
						+ "startDate="		+ startDate	+ "&"
						+ "endDate="		+ endDate
		
		Common.open("", "target_pop", popupTit, popupUrl, "950px", "600px", "iframe", true, null, null, true);
	}
	// 연차기간설정 버튼
	function openVacationPeriodManagePopup(urCode, year) {
		Common.open("","target_pop","<spring:message code='Cache.lbl_apv_setVacTerm' />","/groupware/vacation/goVacationPeriodManagePopup.do","420px","520px","iframe",true,null,null,true);
	}
	
	// 엑셀 업로드
	function openVacationInsertByExcelPopup() {
		Common.open("","target_pop","<spring:message code='Cache.lbl_ExcelUpload' />","/groupware/vacation/goVacationInsertByExcel1Popup.do?reqType="+reqType,"500px","320px","iframe",true,null,null,true);
	}
	
	// 조직도 팝업
	function openOrgMapLayerPopup() {
		duplChkUserIdArr = new Array();
		
		Common.open("","orgmap_pop","<spring:message code='Cache.lbl_apv_org' />","/covicore/control/goOrgChart.do?callBackFunc=orgMapLayerPopupCallBack&type=B9","1060px","580px","iframe",true,null,null,true);
	}

	// 조직도 팝업 콜백
	function orgMapLayerPopupCallBack(orgData) {
		var data = $.parseJSON(orgData);
		var item = data.item
		var len = item.length;
		var html = '';

		$.each(item, function (i, v) {
			var userId = v.UserID;

			if ($.inArray(userId, duplChkUserIdArr) == -1) {
				html += '<li class="listCol" value="' + v.UserCode + '">';
				html += '<div><span>' + CFN_GetDicInfo(v.RGNM) + '</span></div>';
				html += '<div><span>' + CFN_GetDicInfo(v.DN) + '</span></div>';
				html += '<div><span>' + (CFN_GetDicInfo(v.LV.split("&")[1]) == "undefined" ? "" : CFN_GetDicInfo(v.LV.split("&")[1])) + '</span></div>';
				html += '<div><input type="text" placeholder="0"></div>';
				html += '</li>';

				duplChkUserIdArr.push(userId);
			}
		});
		$('#target_pop_if').contents().find('#listColNotice').hide();
		$('#target_pop_if').contents().find('#listHeader').after(html);
	}

	function updateDeptInfo() {
		Common.Confirm("<spring:message code='Cache.msg_n_att_wantToSync' />", "Confirmation Dialog", function (confirmResult) { //동기화 하시겠습니까?
			if (confirmResult) {
		 		$.ajax({
					type : "POST",
					url : "/groupware/vacation/updateDeptInfo.do",
					async:false,
					success:function (data) {
						if(data.status == 'SUCCESS') {
							Common.Inform("<spring:message code='Cache.msg_apv_170' />", "Inform", function() {
								search();
							});
		          		} else {
		          			Common.Warning("<spring:message code='Cache.msg_apv_030' />");
		          		}
					},
					error:function(response, status, error) {
						CFN_ErrorAjax(url, response, status, error);
					}
		 		});
			} else {
				return false;
			}
		});	
	}


	///////////////////


	// 휴가 신청
	function openVacationApplyPopup() {
		CFN_OpenWindow("/approval/approval_Form.do?formPrefix=WF_FORM_VACATION_REQUEST2&mode=DRAFT", "", 790, (window.screen.height - 100), "resize");
	}

	// 휴가 신청/취소 내역
	function openVacationViewPopup(urCode, processId, workItemId) {
//		CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&workitemID="+workItemId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
		CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
	}

	// 템플릿 파일 다운로드
	function excelDownload() {

		if (confirm("<spring:message code='Cache.msg_bizcard_downloadTemplateFiles' />")) {
			location.href = "/groupware/vacation/excelDownload.do?reqType=commonInsert";
		}
	}

	// 엑셀 업로드(공통연차)
	function openVacationInsertByExcelPopup3() {
		if (reqType == 'manage') {
			Common.open("","target_pop","<spring:message code='Cache.btn_ExcelUpload' />","/groupware/vacation/goVacationInsertByExcel2Popup.do?reqType="+reqType,"499px","440px","iframe",true,null,null,true);
		}
	}

	function openVacationCancelByExcelPopup3() {
		if (reqType == 'manage') {
			Common.open("","target_pop","<spring:message code='Cache.lbl_common_vacation_cancel' />","/groupware/vacation/goVacationCancelByExcelpopup.do?reqType="+reqType,"499px","358px","iframe",true,null,null,true);
		}
	}

	// 구분
	function openVacationCancelPopup(vacationInfoId, vacYear) {
		Common.open("","target_pop","<spring:message code='Cache.lbl_apv_vacation_cancel' />","/groupware/vacation/goVacationCancelPopup.do?vacationInfoId="+vacationInfoId+"&vacYear="+vacYear,"499px","405px","iframe",true,null,null,true);
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