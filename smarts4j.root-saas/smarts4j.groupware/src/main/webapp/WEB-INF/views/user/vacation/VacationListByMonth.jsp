<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="egovframework.baseframework.util.RedisDataUtil"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="egovframework.coviframework.util.ComUtils"%>
<style>
#gridDiv_AX_fixedTbody tr td { height:40px !important; } 
#gridDiv_AX_fixedTbody tr:first-child td { height:39px !important; }
.AXGrid .AXgridScrollBody .AXGridBody .gridFixedBodyTable tbody tr td .bodyNode.bodyTdText.bodyTdFile {overflow:visible}
</style>
<div class='cRConTop titType AtnTop'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.MN_702' /></h2>
	<div class="searchBox02">
		<span><input type="text" id="schUrName"/><button type="button" class="btnSearchType01" onclick="search()"><spring:message code='Cache.btn_search' /></button></span>
		<a href="#" class="btnDetails active"><spring:message code='Cache.lbl_detail' /></a>
	</div>	
</div>
<div class='cRContBottom mScrollVH'>
	<div class="inPerView type02 active">
		<div  >
			<div class="selectCalView">
				<select class="selectType02" id="schYearSel">
				</select>
				<select class="selectType02" id="schEmploySel">
					<option value=""><spring:message code='Cache.lbl_Whole' /></option>
				    <option value="INOFFICE" selected="selected"><spring:message code="Cache.lbl_inoffice" /></option>
				    <option value="RETIRE"><spring:message code="Cache.msg_apv_359" /></option>
				</select>
			</div>
			<div>
				<a href="#" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code='Cache.btn_search' /></a>
			</div>
		</div>
		<div>
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
					<span class="TopCont_option"><spring:message code='Cache.lbl_att_select_department'/></span>	<!-- 부서선택 -->
					<select class="selectType02" id="deptList">
					</select>
				</div>
				
				<a href="#" class="btnTypeDefault btnExcel" onclick="excelDownload();"><spring:message code='Cache.lbl_SaveToExcel' /></a>
			</div>
			<div class="buttonStyleBoxRight">
				<div class="ATMbuttonStyleBoxRight" style="font-size:12px;">
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
		<div class="tblList tblCont Nonefix">
			<div id="gridDiv">
			</div>
		</div>
		<div id=divDate>
	        <span class="dateTip">
	        	<spring:message code='Cache.msg_onlyDeduc'/>
	        </span>
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
		if (reqType == 'user') $('#reqTypeTxt').text('<spring:message code="Cache.MN_665" />');
		
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
	  	        		html+=coviCmn.isNull(this.item.year,'');
	  	        		return html;
	  	        	}	
	  	        },
				{key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'150', align:'center',
					formatter:function() {
						return CFN_GetDicInfo(this.item.DeptName);
					}},
				{key:'DisplayName', label:'<spring:message code="Cache.lbl_username" />', width:'120', align:'center',addClass:'bodyTdFile', 
					formatter:function () {
						var html = "<div>";
						html += "<strong class='black f13'>";
						html +="<div class='btnFlowerName' onclick='coviCtrl.setFlowerName(this)' style='position:relative;cursor:pointer' data-user-code='"+ this.item.UR_Code +"' data-user-mail=''>"
						html += this.item.DisplayName;
						html += "</div>";
						html += "</strong>"; 
						html += "</div>";
							
						return html;
					}},
					{key:'EnterDate', label:'<spring:message code="Cache.lbl_EnterDate" />', width:'120', align:'center',
						formatter:function(){
							var enterDate = this.item.EnterDate;
							enterDate = enterDate.toString().replaceAll("-","");
							return CFN_TransLocalTime(enterDate, "yyyy-MM-dd");
						}
					},
					{key:'RetireDate',  label:'<spring:message code="Cache.lbl_RetireDate" />', width:'120', align:'center'},
				{key:'planVacDay', label:'<spring:message code="Cache.lbl_TotalVacation" />', width:'60', align:'center', sort:false,
					formatter: function(){
						return Number(this.item.planVacDay);
					}
				},
				{key:'useDays', label:'<spring:message code="Cache.lbl_apv_Use" />', width:'60', align:'center', sort:false,
					formatter: function(){
						return Number(this.item.useDays);
					}
				},
				{key:'remindDays', label:'<spring:message code="Cache.lbl_appjanyu" />', width:'80', align:'center', sort:false,
					formatter: function(){
						return Number(this.item.remindDays);
					}
				},
				<c:forEach begin="1" end="12" varStatus="st">
					{key:'VacDay_${st.index}',  label:'<spring:message code="Cache.lbl_Month_${st.index}" />', width:'90', align:'center', sort:false},
				</c:forEach>
				
				{key:'', label:' ', width:'100', align:'center', sort:false,
					formatter:function () {
						var html = "<div>";
						html += "<a href='#' class='btnDocView' onclick='openVacationInfoPopup(\"" + this.item.UR_Code + "\", \"" + this.item.year + "\"); return false;'>";
						html += "<spring:message code='Cache.lbl_view' />";
						html += "</a>";
						html += "</div>";
							
						return html;
					}
				}
		];
		
		grid.setGridHeader(headerData);
		
		// config
		var configObj = {
			targetID : "gridDiv",
			height:"auto",			
			fixedColSeq: 7
		};
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
		var params = {
			displayName : $('#schUrName').val(),
			reqType : reqType,
			year : $('#schYearSel').val(),
			stndCur :$("#stndCur").is(':checked')?"Y":"N",
			schEmploySel : $('#schEmploySel').val(),
			DEPTID : ($("#deptList").val() == null ? "" : $("#deptList").val())
		}; // DeptSortKey, B.SortKey ASC
		
		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationListByMonth.do",
			ajaxPars : params
		});
	}
	
	// 이름 클릭
	function openVacationInfoPopup(urCode, year) {
		Common.open("","target_pop","<spring:message code='Cache.MN_657' />","/groupware/vacation/goVacationInfoPopup.do?urCode="+urCode+"&year="+year,"1000px","550px","iframe",true,null,null,true);
	}
	
	// 엑셀 파일 다운로드
	function excelDownload() {
		var sortInfo = grid.getSortParam("one").split("=");
		var	sortBy = sortInfo.length>1? sortInfo[1]:"";

		// 페이지 이동 시, 예전 엑셀 파일 내려받아지는 경우 있어서 수정함.
		location.href = "/groupware/vacation/excelDownloadForVacationListByMonth.do?"
			+"reqType="+reqType
			+"&year="+$('#schYearSel').val()
			+"&displayName="+$('#schUrName').val()
			+"&schEmploySel="+$('#schEmploySel').val()
			+"&deptName="+($("#deptList").val() == null ? "" : $("#deptList option:selected" ).text())
			+"&DEPTID="+($("#deptList").val() == null ? "" : $("#deptList").val())
			+"&sortBy="+sortBy;
		
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