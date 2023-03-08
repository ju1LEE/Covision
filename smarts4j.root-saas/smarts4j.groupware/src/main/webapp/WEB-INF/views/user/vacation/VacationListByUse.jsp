<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="egovframework.baseframework.util.RedisDataUtil
,egovframework.coviframework.util.ComUtils,egovframework.baseframework.util.DicHelper"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
String mode = ComUtils.removeMaskAll(request.getParameter("mode"));
%>
<style>
.selectType02, .selectType02 option {color:#717b85;font-size:12px}
</style>
<div class='cRConTop titType AtnTop'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.lbl_vacationUseStatus' /><%=(!mode.equals("DEPT")?"["+DicHelper.getDic("lbl_apv_chkShareDept")+"]":"") %></h2>
	<div class="searchBox02">
		<span><input type="text" id="schUrName"/><button type="button" class="btnSearchType01" onclick="search()"><spring:message code='Cache.btn_search' /></button></span>
		<a href="#" class="btnDetails active"><spring:message code='Cache.lbl_detail' /></a>
	</div>	
</div>
<div class='cRContBottom mScrollVH'>
	<div class="inPerView active">
		<div class="selectCalView">
			<select class="selectType02" id="schTypeSel">
			    <option value="deptName" ><spring:message code="Cache.lbl_dept" /></option>
			    <option value="displayName" selected="selected"><spring:message code="Cache.lbl_username" /></option>
			</select>
			<div class="dateSel type02">
				<input id="schTxt" type="text" class="HtmlCheckXSS ScriptCheckXSS">
			</div>											
			<spring:message code='Cache.lbl_Period'/>	<!-- 기간 -->
			<span><select  class="selectType02" id="monthSel">
				<option value=''><spring:message code="Cache.lbl_all" /></option>
			<%for (int i=1; i <13;i++){
				out.println("<option value='"+(i<10?"0"+i:i)+"'>"+i+"월</option>");
			}%>
			</select></span>
			<span><select  class="AXSelect W80" id="vacFlagSel"></select></span>
			<a href="#" id="btnSearch" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code='Cache.btn_search'/></a> <!-- 검색 -->
		</div>	
	</div>
	<div class="boardAllCont">
		<div class="boradTopCont">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
				<select class="selectType02" id="schYearSel">
				</select>
				<%if ("DEPT".equals(mode)){ %>
				<div id="addSchOptions" style="display:inline-block">
					<span class="TopCont_option"><spring:message code='Cache.lbl_att_select_department'/></span>	<!-- 부서선택 -->
					<select class="selectType02" id="deptList">
					</select>
				</div>
				<% }%>
				<a href="#" class="btnTypeDefault btnExcel" onclick="excelDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a>
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
		<div class="tblList tblCont Nonefix">
			<div id="gridDiv">
			</div>
		</div>
	</div>
</div>

<script>
	var reqType = CFN_GetQueryString("reqType");
	var grid = new coviGrid();
	var	headerData = [];
	var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
	initContent();

	// 부서휴가월별현황, 휴가월별현황
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
	////		;

		coviCtrl.renderAXSelect("VACATION_TYPE","vacFlagSel",'ko',"", 	"", "", true);
 		getDeptList(); //부서리스트조회	
		$('#schUrName').on('keypress', function(e){ 
			if (e.which == 13) {
		        e.preventDefault();
		        
		        search();
		    }
		});
		
		$("#schYearSel, #deptList").on('change', function(e) {
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
	}
	
	// 그리드 세팅
	function setGrid() {
		// header
			headerData = [
				{key:'VacYear', label:'<spring:message code="Cache.lblNyunDo" />', width:'50', align:'center', sort:false},
				{key:'AppDate', label:'<spring:message code="Cache.lbl_DraftDate" />', width:'80', align:'center'},
				{key:'EndDate', label:'<spring:message code="Cache.lbl_apv_accepted" />', width:'80', align:'center'},
				{key:'UpDeptName', label:'<spring:message code="Cache.lbl_ParentDeptName" />', width:'60', align:'center'},
				{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'60', align:'left'},
				{key:'DisplayName', label:'<spring:message code="Cache.lbl_name" />', width:'50', align:'center', addClass:'bodyTdFile',
					formatter:function(){
						var html ="<div class='btnFlowerName' onclick='coviCtrl.setFlowerName(this)' style='position:relative;cursor:pointer' data-user-code='"+ this.item.UR_Code +"' data-user-mail=''>"
						html += this.item.DisplayName;
						html += "</div>";
							
						return html;
					}	
				},
		        {key:'UR_Code', label:'<spring:message code="Cache.lbl_UserCode" />', width:'50', align:'center'},
		        {key:'JobPositionName', label:'<spring:message code="Cache.lbl_apv_jobposition" />', width:'60', align:'center'},
		        {key:'VacFlagName', label:'<spring:message code="Cache.lbl_Gubun" />', width:'50', align:'center'},
				{key:'VacText', label:'<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />', width:'50', align:'center',
					formatter:function () {
						var html = "<div>";
						if (this.item.Gubun == 'VACATION_PUBLIC' || this.item.Gubun == 'VACATION_PUBLIC_CANCEL') {
							html += "<a href='#' onclick='Common.Inform(\"<spring:message code='Cache.lbl_vacationMsg25' />\"); return false;'>";
						} else if (typeof(this.item.ProcessID) == 'undefined' || typeof(this.item.WorkItemID) == 'undefined') {
							html += "<a href='#' onclick='Common.Inform(\"<spring:message code='Cache.lbl_vacationMsg26' />\"); return false;'>";
						} else {
							html += "<a href='#' onclick='openVacationViewPopup(\"" + this.item.UR_Code + "\", \"" + this.item.ProcessID + "\", \"" + this.item.WorkItemID + "\"); return false;'>";
						}
						html += this.item.VacText;
						html += "</a>";
						html += "</div>";
							
						return html;
				}},
				{key:'Sdate', label:'<spring:message code="Cache.lbl_startdate" />', width:'75', align:'left'},
				{key:'Edate', label:'<spring:message code="Cache.lbl_EndDate" />', width:'75', align:'left'},
				{key:'VacDay', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'40', align:'center'},
				{key:'Reason', label:'<spring:message code="Cache.lbl_Reason" />', width:'100', align:'left', sort:false}
			];
/*		headerData = [
  			{key:'VacYear', label:'<spring:message code="Cache.lblNyunDo" />', width:'40', align:'center'},
			{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'60', align:'left',
				formatter:function() {
					return CFN_GetDicInfo(this.item.DeptName);
				}},
			{key:'DisplayName', label:'<spring:message code="Cache.lbl_name" />', width:'60', align:'center',
				formatter:function () {
					var html = "<div>";
					if (typeof(this.item.ProcessID) == 'undefined' || typeof(this.item.WorkItemID) == 'undefined') {
						html += "<a href='#' onclick='alert(\"<spring:message code='Cache.lbl_vacationMsg26' />\"); return false;'>";
					} else {
						html += "<a href='#' onclick='openVacationViewPopup(\"" + this.item.UR_Code + "\", \"" + this.item.ProcessID + "\", \"" + this.item.WorkItemID + "\"); return false;'>";
					}
					html += this.item.DisplayName;
					html += "</a>";
					html += "</div>";
						
					return html;
				}
			},
	        {key:'UR_Code', label:'<spring:message code="Cache.lbl_UserCode" />', width:'50', align:'center'},
	        {key:'JobPositionName', label:'<spring:message code="Cache.lbl_apv_jobposition" />', width:'80', align:'center'},
			{key:'VacFlagName', label:'<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />', width:'50', align:'center'},
			{key:'VacDate', label:'<spring:message code="Cache.lbl_Vacation" />', width:'50', align:'center'},
			{key:'VacDay', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'50', align:'center'},
			{key:'Reason', label:'<spring:message code="Cache.lbl_Reason" />', width:'200', align:'left', sortable:false}
		];
*/
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
		
		var params = {"mode":"<%=mode%>"
				,"year":$('#schYearSel').val()
				,"DEPTID":($("#deptList").val() == null ? "" : $("#deptList").val())
				,"schUrName":$('#schUrName').val()
				,"schTypeSel":$('#schTypeSel').val()
				,"schTxt":$('#schTxt').val()
				,"month":$('#monthSel').val()
				,"vacFlag":$('#vacFlagSel').val()
				,"gubun":$('#gubunSel').val()}; // DeptSortKey, B.SortKey ASC
		
		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationListByUse.do",
			ajaxPars : params
		});
	}
	

	// 휴가 신청/취소 내역
	function openVacationViewPopup(urCode, processId, workItemId) {
		CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
	}

	// 엑셀 파일 다운로드
	function excelDownload() {
		// 페이지 이동 시, 예전 엑셀 파일 내려받아지는 경우 있어서 수정함.
		location.href = "/groupware/vacation/excelDownloadForVacationListByUse.do?"
			+"mode=<%=mode%>"
			+"&year="+$('#schYearSel').val()
			+"&DEPTID="+($("#deptList").val() == null ? "" : $("#deptList").val())
			+"&displayName="+$('#schUrName').val()
			+"&schTypeSel="+$('#schTypeSel').val()
			+"&schTxt="+$('#schTxt').val()
			+"&month="+$('#monthSel').val()
			+"&vacFlag="+$('#vacFlagSel').val()
			+"&gubun="+$('#gubunSel').val()
			+"&deptName="+($("#deptList").val() == null ? "" : $("#deptList option:selected" ).text());
		
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