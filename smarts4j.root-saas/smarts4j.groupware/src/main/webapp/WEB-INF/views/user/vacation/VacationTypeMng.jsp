<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.AXGrid .AXgridScrollBody .AXGridBody .gridBodyTable tbody tr.hover {
	background: #f5f5f5;
}
input[type="checkbox"] { display:inline-block; }
.tblList .AXGrid .AXgridScrollBody .AXGridBody .gridBodyTable tbody tr td select { min-width:56px; }
</style>

<div class='cRConTop titType AtnTop'>
	<h2 class="title" id="reqTypeTxt"><spring:message code='Cache.lbl_n_att_vacTypeMng' /></h2>
</div>
<div class='cRContBottom mScrollVH'>
	<div class="inPerView type02"></div>
	<div class="boardAllCont">
		<div class="boradTopCont">
			<div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
				<a href="#" class="btnTypeDefault btnTypeBg" onclick="addVacationTypePop('');"><spring:message code='Cache.lbl_VacationTypeAdd' /></a>
				<a href="#" class="btnTypeDefault" onclick="delVacationType();"><spring:message code='Cache.btn_Delete' /></a>
			</div>
			<div class="buttonStyleBoxRight">	
				<select class="selectType02 listCount" id="listCntSel" >
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
	var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
	
	$(document).ready(function(){
		init();
		
		// 그리드 카운트
		$('#listCntSel').on('change', function(e) {
			grid.page.pageSize = $(this).val();
			CFN_SetCookieDay("VacListCnt", $(this).find("option:selected").val(), 31536000000);
			grid.reloadList();
		});
		
	});
	function init() {
		setGrid();	// 그리드 세팅
		search();	// 검색
	}
	
	// 그리드 세팅
	function setGrid() {
		// header
		var	headerData = [
				{ key:'CodeID', label:'chk', width:'20', align:'center', formatter:'checkbox'},
				{ key:'IsUse', label:"<spring:message code='Cache.lbl_IsUse' />",width:'40', align:'center', formatter:function(){
	           		 return (this.item.IsUse=="Y"?"<spring:message code='Cache.lbl_Use' />":"<spring:message code='Cache.lbl_UseN' />");
				}
			},
				{key:'Code', label:'<spring:message code="Cache.lbl_Codes" />', width:'200', align:'left'}, //코드
				{key:'CodeName', label:'<spring:message code="Cache.lbl_Vaction_Name" />', width:'200', align:'center', formatter:function(){
		           		 return "<a onclick='addVacationTypePop(\"" + this.item.Code + "\")';><font color=blue><u>"+this.item.CodeName+"</u></font></a>";
					}
				}, //코드명
				{key:'SortKey', label:'<spring:message code="Cache.lbl_Sort" />', width:'200', align:'center'}, //정렬
				{key:'Reserved1', label:"<spring:message code='Cache.lbl_n_att_vacTypeDed' />",width:'40', align:'center', formatter:function(){
	           		 return (this.item.Reserved1=="Y"?"<spring:message code='Cache.lbl_Deduction' />":"<spring:message code='Cache.lbl_NoDeduction' />");
				}}
				
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
		var params = {
			sortBy : "SortKey asc"
			,showAll : "Y"
		};
		
		// bind
		grid.bindGrid({
			ajaxUrl : "/groupware/vacation/getVacationTypeList.do",
			ajaxPars : params,
			onLoad : function() {
				//아래 처리 공통화 할 것
				coviInput.setSwitch();
				//custom 페이징 추가
				//$('.AXgridPageBody').append('<div id="custom_navi" style="text-align:center;margin-top:2px;"></div>');
				//grid.fnMakeNavi("grid");
			}
		});
	}
	
	function updVacUsedYn(o){
		var id = $(o).data("swid");
		var isUse = "";
		var reserved1 = "";
		var reservedInt = "";
		if(id=="IsUse"){
			if($(o).attr("class").lastIndexOf('on')>0){
				isUse = "N";
				$(o).removeClass("on");
			}else{
				isUse = "Y";
				$(o).addClass("on");
			}
		}else if(id=="Reserved1"){
			if($(o).attr("class").lastIndexOf('on')>0){
				reserved1 = "N";
				$(o).removeClass("on");
			}else{
				reserved1 = "Y";
				$(o).addClass("on");
			}
		}else if(id=="AttGroup"){
			reservedInt = $(o).val();
		}
		
		
		
		var params = {
			IsUse : isUse,
			Reserved1 : reserved1,
			ReservedInt : reservedInt,
			CodeID : $(o).data("codeid")
		};
		
		$.ajax({
			type : "POST",
			data : params,
			async: false,
			url : "/groupware/vacation/updVacationType.do",
			success:function (list) {
				
			},
			error:function(response, status, error) {
				CFN_ErrorAjax("/groupware/vacation/updVacationType.do", response, status, error);
			}
		});
		
	}
	
	//휴가구분 등록 팝업
	function addVacationTypePop(Code){
		Common.open("","target_pop","<spring:message code='Cache.lbl_n_att_vacTypeMng' />","/groupware/vacation/goVacationTypeAddPopup.do?Code="+Code,"551px","400px","iframe",true,null,null,true);
	}
	
	//휴가구분 삭제
	function delVacationType(){
		if($("input[name=chk]:checked").length==0){
			Common.Warning("<spring:message code='Cache.msg_selectTargetDelete'/>");
		}else{
			Common.Confirm("<spring:message code='Cache.msg_Common_08' />", "Confirmation Dialog", function (confirmResult) {
				if (confirmResult) {
					
					var delArry = new Array();
					for(var i=0;i<$("input[name=chk]:checked").length;i++){
						delArry.push($("input[name=chk]:checked").eq(i).val());
					}
					var params = {
						CodeID : delArry
					}; 
					
					jQuery.ajaxSettings.traditional = true;	
					$.ajax({
						type : "POST",
						data : params,
						async: false,
						url : "/groupware/vacation/deleteVacationType.do",
						success:function (data) {
							if(data.status =="SUCCESS"){
								//휴가 유평 변경완료시 basecode reload cache
								coviCmn.reloadCache("BASECODE", Common.getSession("DN_ID"));
								search();
							}else{
								Common.Warning("<spring:message code='Cache.msg_sns_03'/>");/* 저장 중 오류가 발생하였습니다. */
							}
						},
						error:function(response, status, error) {
							CFN_ErrorAjax("/groupware/vacation/deleteVacationType.do", response, status, error);
						}
					});
				}
			});
		}
	}
	
	
</script>			