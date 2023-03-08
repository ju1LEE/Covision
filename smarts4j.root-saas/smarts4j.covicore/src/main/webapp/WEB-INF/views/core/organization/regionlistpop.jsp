<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script type="text/javascript" src="/covicore/resources/script/menu/adminorganization.js<%=resourceVersion%>"></script>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv='cache-control' content='no-cache'> 
	<meta http-equiv='expires' content='0'> 
	<meta http-equiv='pragma' content='no-cache'>
	<jsp:include page="/WEB-INF/views/cmmn/include_admin.jsp"></jsp:include>

	<style>
		/* div padding */
		.divpop_body {
			padding: 20px !important;
		}
		
		label {
			font-size: 13px;
		}
		
		.colHeadTable, .gridBodyTable {
			font-size: 13px;
		}
	</style>
	
</head>
<form>
	<div style="width:100%;">
		<div class="topbar_grid">
			<span> <spring:message code="Cache.lbl_SearchCondition"/><!-- 검색 조건 -->
				<select name="" class="AXSelect" id="searchType">
					<option value="DisplayName"><spring:message code='Cache.lbl_DisplayName'/></option> <!-- 표시이름 -->
					<option value="RegionCode"><spring:message code='Cache.lbl_RegionCode'/></option> <!-- 지역(사업장) 코드 -->
				</select>
				<input type="text" class="AXInput"	placeholder="<spring:message code='Cache.msg_apv_001' />" id="searchText"> 
				<input type="button" class="AXButton" onclick="onClickSearchButton(this);" value="<spring:message code='Cache.lbl_search'/>"> <!-- 검색 -->
				<input type="hidden" id="hidOldCompanyCode" style="display: none;">
			</span>
		</div>
		<!-- Grid -->
		<div id="resultBoxWrap">
			<div id="orgGrid"></div>
		</div>
	</div>
</form>

<script>

	var orgMyGrid = new coviGrid();
	var lang = "${lang}";
	
	window.onload = initContent();

	function initContent(){
		setGrid(); 
	}
	
	//Grid 관련 사항 추가 -
	//Grid 생성 관련
	function setGrid(){
		
		orgMyGrid.setGridHeader([
							  {key:'GroupCode', label:"<spring:message code='Cache.lbl_RegionCode'/>", width:'70', align:'center', formatter : function(){
								  return "<a href='#' onclick='bindRegion(\""+ this.item.GroupCode +"\", \"" + this.item.MultiGroupName + "\"); return false;'>"+ "<span name='code'>" + this.item.GroupCode + "</span>"+"</a>";
							  }}, //지역(사업장) 코드
			                  {key:'MultiGroupName',  label:"<spring:message code='Cache.lbl_DisplayName'/>", width:'70', align:'center', formatter : function(){
								  return CFN_GetDicInfo(this.item.MultiGroupName, lang);
							  }}, //표시이름
			                  {key:'PrimaryMail',  label:"<spring:message code='Cache.lbl_Mail'/>", width:'70', align:'center'}//메일
				      		]);
		setGridConfig();
		bindGridData();
	}
	
	//Grid 설정 관련
	function setGridConfig(){
		var configObj = {
			targetID : "orgGrid",		// grid target 지정
			height:"auto",
			page : {
				pageNo:1,
				pageSize:10
			},
			paging : true
		};
		
		// Grid Config 적용
		orgMyGrid.setGridConfig(configObj);
	}	

	function bindGridData() {	
		
		var searchText = $("#searchText").val();
		var searchType = $("#searchType").val();
		var domain = "${oldCompanyCode}";
		
		orgMyGrid.page.pageNo = 1;

		orgMyGrid.bindGrid({
			ajaxUrl:"/covicore/admin/orgmanage/getregionlist.do",
			ajaxPars: {
				domain : domain,
				IsUse : 'Y',
				IsHR : '',
				IsMail : '',
				searchText : searchText,
				searchType : searchType,
				sortBy: orgMyGrid.getSortParam()
			}
		});
	}
		
	function onClickSearchButton(){
		bindGridData();
	}
	
	function bindRegion(regionCode, multiRegionName) {
		if("${functionName}" != null && "${functionName}" != ''){
    		if(parent["${functionName}"] != undefined){
				parent["${functionName}"](regionCode, multiRegionName);
    		}
    	}
		Common.Close();
	}
	
</script>