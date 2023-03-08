<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<h3 class="con_tit_box">
	<span class="con_tit">기본게시판설정</span>	
</h3>
<form id="form1">
	<input type="hidden" id ="hiddenCategory" value = ""/>
	<input type="hidden" id ="DIC_Code_ko" value = ""/>
	<input type="hidden" id ="DIC_Code_en" value = ""/>
	<input type="hidden" id ="DIC_Code_ja" value = ""/>
	<input type="hidden" id ="DIC_Code_zh" value = ""/>
    <div style="width:100%;min-height: 500px">
    	<div id="topitembar01" class="topbar_grid">
			<input id="refresh" type="button" class="AXButton BtnRefresh" value="<spring:message code="Cache.lbl_Refresh"/>" onclick="gridRefresh();"/>
			<input id="add" type="button" class="AXButton BtnAdd"  value="<spring:message code="Cache.btn_Add"/>" onclick="boardProperty('','C');"/>
		</div>
		<div id="topitembar02" class="topbar_grid">
			
			<%-- 21.12.29, 도메인 관리자는 도메인 선택 화면을 안보여줍니다(CoreInclude.jsp를 통한 class domain 처리). --%>
			<span class="domain">
				<spring:message code='Cache.lbl_OwnedCompany'/>
				<select id="communityDoSelBox" class="AXSelect W100"></select>
			</span>
			
			<spring:message code="Cache.lbl_CommunityType"/> <!-- 커뮤니티 유형 -->
			<select id="communityCmSelBox" class="AXSelect W100"></select>
			<spring:message code="Cache.lbl_BoardNm"/> <!-- 게시판명 -->
			<input name="search" type="text" id="searchValue" class="AXInput" />
			<input type="button" value="<spring:message code='Cache.btn_search'/>" id="searchBtn" class="AXButton" />
		</div>	
		<div id="gridDiv"></div>
	</div>
</form>
<script type="text/javascript">
var communityGrid = new coviGrid();

var msgHeaderData = "";

communityGrid.config.fitToWidthRightMargin=0;

window.onload = initLoad();

function initLoad(){
	event();
	setCommunityGrid();
}

function GridHeader(){
	var communityGridHeader = [
		                       	{key:'MenuID', label:'', width:'1', align:'center', formatter: 'checkbox', hideFilter : 'Y'},
		                       	{key:'DN_NAME', label:'<spring:message code="Cache.lbl_Domain"/>', width:'2', align:'center' ,formatter:function () {
			      		  			var html = "";
			      		        	html = String.format("<a href='#' onclick='javascript:boardProperty({1},{2});' style='text-decoration:none'>{0}</a>", this.item.DN_NAME, this.item.MenuID, '');
		      		        		return html;
		      		        	}},  /* 도메인 */
		                       	{key:'CommunityTypeName', label:'<spring:message code="Cache.lbl_CommunityType"/>', width:'2', align:'center'}, /* 커뮤니티 유형 */
		                       	{key:'BoardName', label:'<spring:message code="Cache.lbl_BoardNm"/>', width:'2', align:'center'}, 			/* 게시판명 */
		                       	{key:'FolderTypeName', label:'<spring:message code="Cache.lbl_FolderType"/>', width:'2', align:'center'},		/* 폴더유형 */
		                       	{key:'IsUse', label:'<spring:message code="Cache.lbl_Use"/>', width:'2', align:'center',  formatter:function () { /* 사용 */
		      		        		var str = "";
				      				str = "<input type='text' kind='switch' on_value='Y' off_value='N' id='AXInputSwitch"+this.item.MenuID+"' style='width:50px;height:21px;border:0px none;' value='"+this.item.IsUse+"' onchange='updateIsCheck(\""+this.item.MenuID+"\");' />";
				      				return str;
		      		        	}}, 
		                       	{key:'Description', sort:false, label:'<spring:message code="Cache.lbl_Description"/>', width:'4', align:'center'} /* 설명 */
		      		        ]; 
	
	msgHeaderData = communityGridHeader;
	return communityGridHeader;
}

function setCommunityGridConfig(){
	var configObj = {
		targetID : "gridDiv",
		listCountMSG:"<b>{listCount}</b> <spring:message code='Cache.lbl_Count'/>", 
		height:"auto",
		page : {
			pageNo: 1,
			pageSize:10
		},
		paging : true,
		colHead:{},
		body:{}
	};
	
	communityGrid.setGridConfig(configObj);
}

function gridRefresh(){
	selectCommunityList();
}


function setCommunityGrid(){
	communityGrid.setGridHeader(GridHeader());
	selectCommunityList();				
}

function selectCommunityList(){
	//폴더 변경시 검색항목 초기화
	setCommunityGridConfig();
	communityGrid.bindGrid({
		ajaxUrl:"/groupware/layout/community/selectCommunityBoardSettingGridList.do",
		ajaxPars: {
			 domainID : $("#communityDoSelBox").val(),
			 code : $("#communityCmSelBox").val(),
			 searchValue : $("#searchValue").val()
		},
	}); 
}

function event(){
	$.ajax({
		type:"POST",
		data:{},
		async : false,
		url:"/groupware/layout/community/selectCommunityD.do",
		success:function (data) {
			var recResourceHTML = "";
			$("#communityDoSelBox").html("");
			if(data.list.length > 0){
				$(data.list).each(function(i,v){
					recResourceHTML += '<option value="'+v.DomainID+'"  >'+v.DisplayName+'</option>';
    			});
			}
			
			$("#communityDoSelBox").append(recResourceHTML);
			
		},
		error:function(response, status, error){
		     //TODO 추가 오류 처리
		     CFN_ErrorAjax("/groupware/layout/community/selectCommunityD.do", response, status, error);
		}
	});
	
	$("#communityCmSelBox").coviCtrl("setSelectOption", 
			"/groupware/layout/community/selectCommunityBaseCode.do", 
			{"CodeGroup": "CommunityDefaultBoardType", "DomainID": $("#communityDoSelBox").val()},
			"<spring:message code='Cache.lbl_selection'/>",
			""
	);
	
	$("#searchBtn").on("click",function(){
		setCommunityGrid();
	});
	
	$("#communityCmSelBox").change(function(){
		setCommunityGrid();
	});
	
	$("#communityDoSelBox").change(function(){
		setCommunityGrid();
	});
}

function updateIsCheck(MenuID){
	var isUseValue = $("#AXInputSwitch"+MenuID).val();
	
	$.ajax({
		type:"POST",
		data:{
			"MenuID" : MenuID,
			"IsUse" : isUseValue
		},
		url:"/groupware/layout/community/boardSettingUseChange.do",
		success:function (data) {
			if(data.status == "SUCCESS")
				Common.Inform("<spring:message code='Cache.msg_ChangeAlert'/>");
			
			selectCommunityList();
		},
		error:function(response, status, error){
		     //TODO 추가 오류 처리
		     CFN_ErrorAjax("/groupware/layout/community/boardSettingUseChange.do", response, status, error);
		}
	}); 
	
}

function boardProperty(MenuID, Type){
	var url = "";
	
	if(Type == "" || Type == null){
		Type = "E";
	}
	
	url = "/groupware/community/communityBoardSettingProperty.do?MenuID="+MenuID+"&mode="+Type;
	Common.open("", "CommunityBoardSettingPopup", "<spring:message code='Cache.lbl_CommuntyBoardSetting'/>", url, "650px", "240px", "iframe", true, null, null, true);
}


function addSubDicCallback(data){
	
	document.getElementById("DIC_Code_ko").value = data.KoFull;
	document.getElementById("DIC_Code_en").value = data.EnFull;
	document.getElementById("DIC_Code_ja").value = data.JaFull;
	document.getElementById("DIC_Code_zh").value = data.ZhFull;
	document.getElementById("hiddenCategory").value = coviDic.convertDic(data);
	
	$("iframe[id^='CommunityBoardSettingPopup']").contents().find("#txtBoardName").val(data.KoFull);
	
	Common.Close('DictionaryPopup');
}
</script>