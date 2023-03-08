<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class='cRConTop titType'>
	<h2 class="title"><spring:message code='Cache.lbl_vacationMsg52'/></h2>	<!-- 발생이력 -->
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
	
		<div class="boradTopCont" id="boradTopCont_1" style="display: block;">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
				<a href="#" class="btnTypeDefault btnExcel" onclick="excelDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a>
				<%--<a href="#" class="btnTypeDefault btnTypeChk" id="insVacBtn" onclick="openVacationInsertPopup();" style="display:none"><spring:message code='Cache.btn_register' /></a>--%>
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
		<div class="tblList tblCont">
			<div id="gridDiv"></div>
		</div>
	</div>
</div>

<script>
	var grid = new coviGrid();
	
	initContent();

	function initContent() {
		init();	// 초기화
		search();	// 검색
	}

	// 초기화
	function init() {
		// 그리드 카운트
		$('#listCntSel').on('change', function (e) {
			grid.page.pageSize = $(this).val();
			grid.reloadList();
		});
		// 화면 처리
		$('#excelDownBtn').html('<spring:message code="Cache.lbl_templatedownload" />');
		$('#insVacBtn').css('display', '');

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

	}

	// 그리드 세팅
	function setGrid() {
		// header
		var headerData = null;
			headerData = [
				{key:'ChangeDate', label:'<spring:message code="Cache.lbl_apv_chgdate" />', width:'50', align:'center'},		//발생일
				{
					key: 'DeptName', label: '<spring:message code="Cache.lbl_dept" />', width: '40', align: 'center',
					formatter: function () {
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openVacationUpdatePopup(\"" + this.item.UR_Code + "\", \"" + this.item.ExtVacYear + "\"); return false;'>";
						if (typeof (this.item.DeptName) != 'undefined') html += CFN_GetDicInfo(this.item.DeptName);
						html += "</a>";
						html += "</div>";

						return html;
					}
				},
				{
					key: 'DisplayName',
					label: '<spring:message code="Cache.lbl_username" />',
					width: '60',
					align: 'center',
					addClass:'bodyTdFile',
					formatter:function(){
						var html ="<div class='btnFlowerName' onclick='coviCtrl.setFlowerName(this)' style='position:relative' data-user-code='"+ this.item.UR_Code +"' data-user-mail=''>"
						html += this.item.DisplayName;
						html += "</div>";
							
						return html;
					}
				},
				{
					key: 'ExtVacName',
					label: '<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />',
					width: '50',
					align: 'center'
				},
				{key:'ExtVacDay', label:'<spring:message code="Cache.lbl_vacationMsg53" />', width:'50', align:'center'},
				{key:'RegisterName', label:'<spring:message code="Cache.lbl_vacationMsg54" />', width:'50', align:'center', addClass:'bodyTdFile',
					formatter:function(){
						var html ="<div class='btnFlowerName' onclick='coviCtrl.setFlowerName(this)' style='position:relative' data-user-code='"+ this.item.RegisterCode +"' data-user-mail=''>"
						html += this.item.RegisterName;
						html += "</div>";
							
						return html;
					}	
				},	//부여자
				{key:'ExpDate', label:'<spring:message code="Cache.lbl_expiryDate" />', width:'80', align:'center'},			//유효기간
				/*{key:'ExtUseVacDay', label:'<spring:message code="Cache.lbl_Use" />', width:'30', align:'center'},				//사용
				{key:'ExtRemainVacDay', label:'<spring:message code="Cache.lbl_n_att_remain" />', width:'30', align:'center'},	//잔여*/
				{key:'VmComment', label:'<spring:message code="Cache.lbl_ProcessContents" />', width:'80', align:'center'}	//처리내용
			];

			grid.setGridHeader(headerData);

			// config
			grid.setGridConfig({
				targetID: "gridDiv",
				height: "auto",
				mergeCells:[2,3,4,5,6,7,8]
			});
			
			grid.page.pageSize = $('#listCntSel').val();
	}

	// 검색
	function search() {
		setGrid();
		var params = null;
		params = {
			year : $('#schYearSel').val(),
			schEmploySel : $('#schEmploySel').val(),
			schTypeSel : $('#schTypeSel').val(),
			schTxt : $('#schTxt').val()
		};

		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationPlanHistList.do",
			ajaxPars : params
		});
	}

	// 연차등록 버튼
	function openVacationInsertPopup() {
		var year = $('#schYearSel').val();
		
		Common.open("","target_pop", year + "<spring:message code='Cache.lblNyunDo' /> : <spring:message code='Cache.lbl_apv_Vacation_days' /> <spring:message code='Cache.btn_register' />","/groupware/vacation/goVacationInsertPopup.do?year="+year,"500px","265px","iframe",true,null,null,true);
	}
	
	// 그리드 클릭
	function openVacationUpdatePopup(urCode, year) {
		Common.open("","target_pop","<spring:message code='Cache.lbl_apv_Vacation_days' />","/groupware/vacation/goVacationUpdatePopup.do?urCode="+urCode+"&year="+year,"499px","281px","iframe",true,null,null,true);
	}
	
	function openExtraVacationUpdatePopup(urCode, vacKind, sDate, eDate, year) {
		Common.open("","target_pop","<spring:message code='Cache.lbl_apv_vacation_etc' /><spring:message code='Cache.lbl_apv_Vacation_days' />","/groupware/vacation/goExtraVacationUpdatePopup.do?urCode="+urCode+"&year="+year+"&vacKind="+vacKind+"&sDate="+sDate+"&eDate="+eDate,"499px","401px","iframe",true,null,null,true);
	}

	// 엑셀 파일 다운로드
	function excelDownload() {
		var params = {
			year : $('#schYearSel').val(),
			schTypeSel : $('#schTypeSel').val(),
			schTxt : $('#schTxt').val(),
			schEmploySel : $('#schEmploySel').val(),
			sortBy : "RegistDate desc"
		};

		ajax_download('/groupware/vacation/excelDownVacationOccurrenceHist.do', params);	// 엑셀 다운로드 post 요청
	}
	
</script>			