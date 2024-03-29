<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class='cRConTop titType'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.MN_700' /></h2> <!-- 미사용연차계획 저장내역 조회  -->
	<div class="searchBox02">h
		<select class="selectType02 mr10" id="schEmpTypeSel" onChange="search();" style="height: 33px">
			    <option value="normal" selected="selected"><spring:message code='Cache.lbl_generalStaff' /><!-- 일반직원 --></option>
			    <option value="newEmpForNine"><spring:message code='Cache.lbl_lessAYear' /> (9<spring:message code='Cache.lbl_day' />)<!-- 1년 미만 (9일) --></option>
			    <option value="newEmpForTwo"><spring:message code='Cache.lbl_lessAYear' /> (2<spring:message code='Cache.lbl_day' />)<!-- 1년 미만 (2일) --></option>
		</select>
		<span><input type="text" id="schUrName"/><button type="button" class="btnSearchType01" onclick="search()"><spring:message code='Cache.btn_search' /></button></span>
		<a href="#" class="btnDetails active"><spring:message code='Cache.lbl_detail' /></a>
	</div>	
</div>
<div class='cRContBottom mScrollVH'>
	<div class="inPerView type02 active">
		<div style="width: 650px;">
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
	<div class="docAllCont">
		<ul id="tabList" class="tabType2 clearFloat">
 			<li value="plan" class="active" onclick="javascript:changeFormType(this); return false;"><a><spring:message code='Cache.lbl_usePlan' /><!-- 사용계획서 --></a></li>
 			<li value="notification1" onclick="javascript:changeFormType(this); return false;"><a><spring:message code='Cache.lbl_vacation_first' /><!-- 1차 --></a></li>
 			<li value="notification2" onclick="javascript:changeFormType(this); return false;"><a><spring:message code='Cache.lbl_vacation_second' /><!-- 2차 --></a></li>
		</ul>
		<div class="boradTopCont">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
				<a href="#" class="btnTypeDefault btnExcel" id="excelDownBtn" onclick="excelDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a>					
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
			<div id="gridDiv">
			</div>
		</div>
	</div>
</div>

<script>
	var grid = new coviGrid();
	var g_formType = 'plan'; 
	var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
	initContent();

	function changeFormType(obj){
		$(obj).addClass("active")
			  .siblings("li").removeClass("active")
		
		g_formType = $(obj).attr("value");
		
		search();
	}
	
	// 미사용연차계획 저장내역 조회
	function initContent() {
		init();	// 초기화
		
		setGrid();	// 그리드 세팅
		
		search();	// 검색
	}
	
	// 초기화
	function init() {
		var nowYear = new Date().getFullYear();
		// 년도 option 생성
		for (var i=2; i>-4;i--) {
			var temp = nowYear + i;
			
			if(temp < 2020){
				continue;
			}
			
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
	}
	
	// 그리드 세팅
	function setGrid() {
		// header
		var	headerData = [
				{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'100', align:'center'},
		        {key:'JobPositionName', label:'<spring:message code="Cache.lbl_apv_jobposition" />', width:'100', align:'center'},	              
				{key:'DisplayName', label:'<spring:message code="Cache.lbl_username" />', width:'100', align:'center'},
				{key:'EnterDate', label:'<spring:message code="Cache.lbl_EnterDate" />', width:'100', align:'center'},
				{key:'OWNDAYS', label:'<spring:message code="Cache.lbl_occurrenceYear" />', width:'100', align:'center'	,sort:false}, /*발생연차 */
				{key:'USEDAYS', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'100', align:'center'	,sort:false},  /*사용연차 */
			    {key:'REMINDDAYS', label:'<spring:message code="Cache.lbl_RemainVacation" />', width:'100', align:'center'	,sort:false}, /*잔여연차 */
		        {key:'janDate', label:'<spring:message code="Cache.lbl_Month_1" />', width:'100', align:'center' ,sort:false},
		        {key:'febDate', label:'<spring:message code="Cache.lbl_Month_2" />', width:'100', align:'center' ,sort:false},
		        {key:'marDate', label:'<spring:message code="Cache.lbl_Month_3" />', width:'100', align:'center' ,sort:false},
		        {key:'aprDate', label:'<spring:message code="Cache.lbl_Month_4" />', width:'100', align:'center' ,sort:false},
		        {key:'mayDate', label:'<spring:message code="Cache.lbl_Month_5" />', width:'100', align:'center' ,sort:false},
		        {key:'junDate', label:'<spring:message code="Cache.lbl_Month_6" />', width:'100', align:'center' ,sort:false},
		        {key:'julDate', label:'<spring:message code="Cache.lbl_Month_7" />', width:'100', align:'center' ,sort:false},
		        {key:'augDate', label:'<spring:message code="Cache.lbl_Month_8" />', width:'100', align:'center' ,sort:false},
		        {key:'sepDate', label:'<spring:message code="Cache.lbl_Month_9" />', width:'100', align:'center' ,sort:false},
		        {key:'octDate', label:'<spring:message code="Cache.lbl_Month_10" />', width:'100', align:'center' ,sort:false},
		        {key:'novDate', label:'<spring:message code="Cache.lbl_Month_11" />', width:'100', align:'center' ,sort:false},
		        {key:'decDate', label:'<spring:message code="Cache.lbl_Month_12" />', width:'100', align:'center' ,sort:false}
		];
		
		grid.setGridHeader(headerData);
		
		// config
		var configObj = {
			targetID : "gridDiv",
			page : {
				pageNo: (page != undefined && page != '')?page:1,
				pageSize: (pageSize != undefined && pageSize != '')?pageSize:10,
			},
			height:"auto"			
		};
		grid.setGridConfig(configObj);
	}

	// 검색
	function search() {
		var empType = $('#schEmpTypeSel').val();
		
		if(g_formType == "plan" && empType.indexOf("newEmp") > -1){
			empType = "newEmp"; 
		}
		
		var params = {	year : $('#schYearSel').val(),
					  	formType : g_formType,
					  	empType : empType,
					  	schEmploySel : $('#schEmploySel').val(),
					  	schTypeSel : $('#schTypeSel').val(),
					  	schTxt : $('#schTxt').val(),
						term : 2020		};
		
		grid.page.pageNo = 1;
		
		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationUsePlanList.do",
			ajaxPars : params
		
		});
	}
	
	// 엑셀 저장
	function excelDownload() {
		var sortInfo = grid.getSortParam("one").split("=");
		var	sortBy = sortInfo.length>1? sortInfo[1]:""; 	
		
		var empType = $('#schEmpTypeSel').val();
		
		if(g_formType == "plan" && empType.indexOf("newEmp") > -1){
			empType = "newEmp"; 
		}
		
		var params = {	year : $('#schYearSel').val(),
					  	formType : g_formType,
					 	empType : empType,
					  	schEmploySel : $('#schEmploySel').val(),
					 	schTypeSel : $('#schTypeSel').val(),
					  	schTxt : $('#schTxt').val(),
					  	sortBy: sortBy,
						term : 2020		};
		
		ajax_download('/groupware/vacation/excelDownloadForVacationUsePlanList.do', params);	// 엑셀 다운로드 post 요청
	}
	
	// 엑셀 다운로드 post 요청
	function ajax_download(url, data) {
	    var $iframe, iframe_doc, iframe_html;

	    if ($('#download_iframe').length != 0) {
	    	$('#download_iframe').remove();
	    }
	    
        $iframe = $("<iframe id='download_iframe' style='display: none' src='about:blank'></iframe>").appendTo("#content");

        iframe_doc = $iframe[0].contentWindow || $iframe[0].contentDocument;
	    if (iframe_doc.document) {
	        iframe_doc = iframe_doc.document;
	    }

	    iframe_html = "<html><head></head><body><form method='POST' action='" + url +"'>" 
	    Object.keys(data).forEach(function(key) {
	        iframe_html += "<input type='hidden' name='"+key+"' value='"+data[key]+"'>";
	    });
        iframe_html +="</form></body></html>";

	    iframe_doc.open();
	    iframe_doc.write(iframe_html);
	    $(iframe_doc).find('form').submit();
	}
</script>			