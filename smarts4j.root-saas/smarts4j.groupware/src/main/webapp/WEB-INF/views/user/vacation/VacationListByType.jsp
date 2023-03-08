<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="egovframework.coviframework.util.ComUtils"%>

<div class='cRConTop titType AtnTop'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.MN_701' /></h2>
	<div class="searchBox02">
		<span><input type="text" id="schUrName"/><button type="button" class="btnSearchType01" onclick="search()"><spring:message code='Cache.btn_search' /></button></span>
		<a href="#" class="btnDetails active"><spring:message code='Cache.lbl_detail' /></a>
	</div>
</div>
<div class='cRContBottom mScrollVH'>
	<div class="inPerView type02 active">
		<div style="width:550px;">
			<div class="selectCalView">
				<select class="selectType02" id="schYearSel" >
				</select>
				<select class="selectType02" id="schTypeSel">
				    <option value="deptName" ><spring:message code="Cache.lbl_dept" /></option>
				    <option value="displayName" selected="selected"><spring:message code="Cache.lbl_username" /></option>
				</select>
				<select class="selectType02" id="schEmploySel" style="display: none;">
					<option value=""><spring:message code='Cache.lbl_Whole' /></option>
				    <option value="INOFFICE" selected="selected"><spring:message code="Cache.lbl_inoffice" /></option>
				    <option value="RETIRE"><spring:message code="Cache.msg_apv_359" /></option>
				</select>
				<div class="dateSel type02">
					<input type="text" id="schTxt">
				</div>
			</div>
			<div>
				<a href="#" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code='Cache.btn_search' /></a>
			</div>
		</div>
		<div style="width:550px;">
			<div class="chkStyle10" style="display:block">
				<input type="checkbox" class="check_class" id="stndCur" value="Y" onclick="$('#schYearSel').attr('disabled',$(this).is(':checked'))">
				<label for="stndCur"><span class="s_check"></span>[<%=ComUtils.GetLocalCurrentDate("yyyy/MM/dd")%>]<spring:message code='Cache.lbl_OnlyValid' /></label>
			</div>	
		</div>
	</div>
	<div class="boardAllCont">
		<div class="boradTopCont">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">

				<div id="addSchOptions" style="display: none;">
					<span class="TopCont_option"><spring:message code='Cache.lbl_att_select_department'/></span><!-- 부서선택 -->
					<select class="selectType02" id="deptList"></select>
				</div>

				<a href="#" class="btnTypeDefault btnExcel" onclick="excelDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a>
			</div>
			<div class="buttonStyleBoxRight">
				<div class="ATMbuttonStyleBoxRight" style="font-size:12px;">
					<input type="checkbox" id="ckb_extra_vacation"> 기타휴가
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
		</div>
		<div class="tblList tblCont">
			<div id="gridDiv">
			</div>
		</div>
	</div>
</div>

<script>
	var lang = Common.getSession("lang");
	var reqType = CFN_GetQueryString("reqType");	// reqType : dept(부서휴가유형별조회), user(휴가유형별현황)
	var grid = new coviGrid();
	var	headerData = [];
	var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
	initContent();

	// 부서휴가유형별조회, 휴가유형별현황
	function initContent() {
		init();	// 초기화

		search();	// 검색
	}
	
	// 초기화
	function init() {
		if (reqType == 'user') $('#reqTypeTxt').text('<spring:message code="Cache.MN_664" />');
		
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
		
		$('#schUrName').on('keypress', function(e){ 
			if (e.which == 13) {
		        e.preventDefault();
		        
		        var schName = $('#schUrName').val();
		        
		        $('#schTypeSel').val('displayName');
		        $('#schTxt').val(schName);
				
		        search();
		    }
		});
		
		$('#schTxt').on('keypress', function(e){ 
			if (e.which == 13) {
		        e.preventDefault();
			
		        search();
		    }
		});

		$("#ckb_extra_vacation").change(function() {
			search();
		});
		
		// 검색 버튼
		$('.btnSearchBlue').on('click', function(e) {
			search();
		});
		
		// 상세 보기
 		$('.btnDetails').on('click', function(){
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
		
 		$('.btnOnOff').unbind('click').on('click', function(){
			if($(this).hasClass('active')){
				$(this).removeClass('active');
				$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').removeClass('active');
			}else {
				$(this).addClass('active');
				$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').addClass('active');			
			}	
		});
 		
 		if (reqType == 'user') {
 			$("#schEmploySel").show();
 			$("#addSchOptions").css("display", "inline-block");
 			
	 		getDeptList(); //부서리스트조회	
	 		
	 		$("#deptList").change(function(){
	 			search();
	 		});
 		}
	}
	
	// 그리드 세팅
	function setGrid() {
		// header
		headerData = [
	  	        {key:'year', label:'<spring:message code="Cache.lblNyunDo" />', width:'100', align:'center', sort:false,
	  	        	tooltip:	function(){return this.item.UseStartDate+"~"+this.item.UseEndDate},
	  	        	formatter:function () {
	  	        		var html = "";
	  	        		if ($("#stndCur").is(':checked') && "<%=ComUtils.GetLocalCurrentDate("yyyy")%>"!= this.item.year)
	  	        			html = "<font style='font-weight:bold;color:#4ABDE1'>";
	  	        		html+=this.item.year;
	  	        		return html;
	  	        	}	
	  	        },
				{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'100', align:'center'},
				{key:'DisplayName', label:'<spring:message code="Cache.lbl_username" />', width:'100', align:'center',
					formatter:function () {
						var html = "<div class='tblLink'>";
						html += "<a href='#' onclick='openVacationInfoPopup(\"" + this.item.UR_Code + "\", \"" + this.item.year + "\"); return false;'>";
						if (typeof(this.item.DisplayName) != 'undefined') html += this.item.DisplayName;
						html += "</a>";
						html += "</div>";
							
						return html;
					}
				},
				{key:'JobPositionName', label:'<spring:message code="Cache.lbl_JobPosition" />', width:'100', align:'center'},
		        {key:'EnterDate', label:'<spring:message code="Cache.lbl_EnterDate" />', width:'100', align:'center'},	           
		        {key:'RetireDate',  label:'<spring:message code="Cache.lbl_RetireDate" />', width:'100', align:'center'},	          	     
		        {key:'planVacDay',  label:'<spring:message code="Cache.lbl_TotalVacation" />', width:'100', align:'center', sort:false,
					formatter:function () {
						return Number(this.item.planVacDay);
					}
				},
		        {key:'useDays', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'100', align:'center',sort:false,
					formatter:function () {
						return this.item.useDays;
					}
				},
				{key:'remindDays',  label:'<spring:message code="Cache.lbl_RemainVacation" />', width:'100', align:'center',sort:false,
					formatter:function () {
						return Number(this.item.remindDays);
					}
				}
		];

		//컬럼 정보 조회
		$.ajax({
			url : "/groupware/vacation/getVacTypeCol.do",
			type: "POST",
			dataType : 'json',
			data :{ "vacPageSize":5, "hideExtraVacation":$("#ckb_extra_vacation").is(":checked")?'N':'Y'},
			async: false,
			success:function (data) {
				var listData = data.list;
				for(var i=0;i<listData.length;i++){
					var listCode = "VAC_"+listData[i].Code;
					headerData.push({key:listCode, label:CFN_GetDicInfo(listData[i].MultiCodeName, lang), width:'100', align:'center',sort:false});
				}
				headerData.push({key:"EtcVac",  label:"<spring:message code='Cache.lbl_hr_base'/> <spring:message code='Cache.lbl_att_and'/>", width:'100', align:'center',sort:false});

			}
		});
		
		grid.setGridHeader(headerData);
		
		grid.setGridConfig({
			targetID : "gridDiv",
			page : {
				pageNo: (page != undefined && page != '')?page:1,
				pageSize: (pageSize != undefined && pageSize != '')?pageSize:10,
			},
			height:"auto"	
		});
	}

	// 검색
	function search() {
		setGrid();

		var params = {
			urName : $('#schUrName').val(),
			reqType : reqType,
			stndCur :$("#stndCur").is(':checked')?"Y":"N",
			year : $('#schYearSel').val(),
			hideExtraVacation : $("#ckb_extra_vacation").is(":checked")?'N':'Y',
			schTypeSel : $('#schTypeSel').val(),
			schEmploySel : $('#schEmploySel').val(),
			DEPTID : ($("#deptList").val() == null ? "" : $("#deptList").val()),
			schTxt : $('#schTxt').val()
		};
		
		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationListByType.do",
			ajaxPars : params
		});
	}
	
	// 이름 클릭
	function openVacationInfoPopup(urCode, year) {
		Common.open("","target_pop","<spring:message code='Cache.MN_657' />","/groupware/vacation/goVacationInfoPopup.do?urCode="+urCode+"&year="+year,"1000px","550px","iframe",true,null,null,true);
	}
	
	// 엑셀 파일 다운로드 (기초코드 설정에 따라 컬럼이 달라지므로 공통 엑셀 다운로드 사용)
	function excelDownload(){
		var headerName = getHeaderNameForExcel();
		var headerKey = getHeaderKeyForExcel();
		var headerType = getHeaderTypeForExcel();
		
		var sortInfo = grid.getSortParam("one").split("=");
		var	sortBy = sortInfo.length>1? sortInfo[1]:""; 	
		var hideVac = $("#ckb_extra_vacation").is(":checked")?'N':'Y';
		var url = "/groupware/vacation/excelDownloadForVacationListByType.do?"
		+"year="+$('#schYearSel').val()
		+"&schTypeSel="+$('#schTypeSel').val()
		+"&schTxt="+$('#schTxt').val()
		+"&stndCur="+($("#stndCur").is(':checked')?"Y":"N")
		+"&hideExtraVacation="+hideVac
		+"&schEmploySel="+$('#schEmploySel').val()
		+"&DEPTID="+($("#deptList").val() == null ? "" : $("#deptList").val())
		+"&deptName="+($("#deptList").val() == null ? "" : $("#deptList option:selected" ).text())
		+"&reqType="+reqType
		+"&sortBy="+sortBy
		+"&headerName="+encodeURI(headerName)
		+"&headerKey="+encodeURI(headerKey)
		+"&headerType="+encodeURI(headerType);
		
		location.href = url;
	}
	
	//엑셀용 Grid 헤더정보 설정
	function getHeaderNameForExcel(){
		var returnStr = "";
		
	   	for(var i=0;i<headerData.length-6; i++){
			returnStr += headerData[i].label + "|";
		}
	   	
	   	returnStr = returnStr.substring(0, returnStr.length-1);
		return returnStr;
	}

	function getHeaderKeyForExcel(){
		var returnStr = "";
		
	   	for(var i=0;i<headerData.length-6; i++){
			returnStr += headerData[i].key + "|";
		}
	   	returnStr = returnStr.substring(0, returnStr.length-1);
		return returnStr;
	}

	function getHeaderTypeForExcel(){
		var returnStr = "";

	   	for(var i=0;i<headerData.length-6; i++){
				returnStr += (headerData[i].dataType != undefined ? headerData[i].dataType:"Text") + "|";
		}
	   	returnStr = returnStr.substring(0, returnStr.length-1);
		return returnStr;
	}
	
	
	function getDeptList(){
		
		$.ajax({
			url : "/groupware/vacation/getDeptList.do",
			type: "POST",
			dataType : 'json',
			success:function (data) {
				var deptList = data.deptList;
				var subDeptOption = "<option value=''><spring:message code='Cache.lbl_Whole'/></option>";		//전체
				for(var i=0;i<deptList.length;i++){
					
					subDeptOption += "<option value='"+deptList[i].GroupCode+"'>";
					var SortDepth = deptList[i].SortDepth;
					for(var j=1;j<SortDepth;j++) {
						subDeptOption += "&nbsp;";
					}
					subDeptOption += deptList[i].MultiDisplayName+"</option>";
				}
				$("#deptList").html(subDeptOption);				
			},
			error:function (response, status, error){
				CFN_ErrorAjax("/groupware/layout/getDeptSchList.do", response, status, error);
			}
		}); 
		
	}
</script>			