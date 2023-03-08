<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<h3 class="con_tit_box">
	<span class="con_tit"><spring:message code="Cache.lbl_LicenseLookup"/></span><!-- 라이선스 조회  -->
	<span style="margin-top:7px;margin-left:10px;">
		<spring:message code="Cache.lbl_License_User_search"/>
	</span>
</h3>
<form id="form1">
	<div style="width:100%;min-height: 500px">
		<div id="topitembar01" class="topbar_grid">
			<input type="button" class="AXButton BtnRefresh" value="<spring:message code="Cache.lbl_Refresh"/>" onclick="refresh();"/>
			<input type="button" class="AXButton BtnDelete" value="<spring:message code="Cache.lbl_RemoveLicenseCache"/>" onclick="confirmRemove();"/> <!-- 라이선스 인증 캐쉬 제거 -->
			<span style="float:right;margin-top:7px;margin-right:20px;display:none">
				<spring:message code="Cache.lbl_License_ActiveUserCnt"/>: <span id="activeUserCnt"></span>&nbsp;&nbsp;/&nbsp;<!-- 활성 사용자 수 -->
				<spring:message code="Cache.lbl_License_UserCnt"/>: <span id="licenseInfo"></span> <!-- 라이선스 사용자 수 -->
			</span>
		</div>
		<div id="topitembar02" class="topbar_grid">
	       		<spring:message code="Cache.lbl_SearchTarget"/><!-- 검색 대상 --> : <input id="searchUserName" type="text" class="AXInput" readonly="readonly" onclick="popupOrgChart();"/>
				<input id="hidUserCode" type="hidden" value=""/> 
				<input type="button" value="<spring:message code="Cache.lbl_DeptOrgMap"/>" onclick="popupOrgChart();" class="AXButton"/><!--조직도 -->
		</div>
		<div id="connectLogGrid"></div>
	</div>
</form>

<script>
	// #sourceURL=LicenseManage.jsp
	var logGrid = new coviGrid();
	
	init();
	
	function init(){
		$("#searchUserName").val( CFN_GetDicInfo(Common.getSession("UR_MultiName")) );
		$("#hidUserCode").val( CFN_GetDicInfo(Common.getSession("UR_Code")) );
		
		getLicenseInfo();
		setGrid();
		
		$("#selectPageSize").val(logGrid.page.pageSize);
	}
	
	function popupOrgChart(){
		Common.open("","orgmap_pop","<spring:message code='Cache.lbl_apv_org'/>","/covicore/control/goOrgChart.do?type=A1&callBackFunc=callBackOrgChart","540px","580px","iframe",true,null,null,true);
	}
	
	function callBackOrgChart(data){
		var jsonOrg = $.parseJSON(data);
		
		if(jsonOrg.item[0] != null){
			$("#searchUserName").val(CFN_GetDicInfo(jsonOrg.item[0].DN));
			$("#hidUserCode").val(jsonOrg.item[0].AN);
			
			searchGrid();
		}	
	}
	
	//그리드 세팅
	function setGrid(){
		logGrid.setGridHeader([	
				                  {key:'IPAddress', label:'<spring:message code="Cache.lbl_IPAddress"/>', width:'30', align:'center'},     //IP 주소
				                  {key:'OS', label:'<spring:message code="Cache.lbl_OS"/>', width:'30', align:'center'},    //운영체제
				                  {key:'Browser',  label:'<spring:message code="Cache.lbl_Browser"/>', width:'30', align:'center' },	     //브라우져
				                  {key:'LogonDate',  label:'<spring:message code="Cache.lbl_LogonDate"/>' + Common.getSession("UR_TimeZoneDisplay"), sort:"desc", width:'30', align:'center', formatter: function(){
				          				return CFN_TransLocalTime(this.item.LogonDate);
						          }},	//로그온 일시
				                  {key:'LogoutDate',  label:'<spring:message code="Cache.lbl_LogoutDate"/>' + Common.getSession("UR_TimeZoneDisplay"), width:'30', align:'center', formatter: function(){
				          				return CFN_TransLocalTime(this.item.LogoutDate);
						          }} 	//로그아웃 일시
					      		]);
		
		setGridConfig();
		searchGrid();
	}

	//그리드 Config 설정
	function setGridConfig(){
		var configObj = {
			targetID : "connectLogGrid",
			height:"auto"
		};
		
		logGrid.setGridConfig(configObj);
	}

	function searchGrid(){
		logGrid.page.pageNo = 1;
		
		logGrid.bindGrid({
			ajaxUrl:"/covicore/license/getConnectionLogList.do",
			ajaxPars: {
				userCode : $("#hidUserCode").val(), 
			}
			,objectName: 'logGrid'
			,callbackName: 'searchGrid'
		});
	}
	
	
	function getLicenseInfo(){
		$.ajax({
			type: "post"
			, url: "/covicore/license/getLicenseInfo.do"
			, success: function(data){
				if(data.status == "SUCCESS"){
					$("#activeUserCnt").text(data.activeUserCnt);
					$("#licenseInfo").text(data.licenseInfo);
				}else{
					Common.Warning("<spring:message code='Cache.msg_apv_030'/>");	//오류가 발생헸습니다.
				}
			},
        	error:function(response, status, error){
       	     CFN_ErrorAjax("/covicore/license/getLicenseInfo.do", response, status, error);
       		}	
		});
	}
	
	function refresh(){
		CoviMenu_GetContent(location.href.replace(location.origin, ""),false);
	}
	
	function confirmRemove(){
		Common.Confirm("라이센스 인증캐쉬를 삭제하시겠습니까?", "<spring:message code='Cache.lbl_RemoveLicenseCache'/>", function(result){ /* 라이선스 인증 캐쉬 제거 */
			if(result)
				removeLicenseCache();		
		});
	}
	
	function removeLicenseCache(){
		Common.Confirm("<spring:message code='Cache.msg_RemoveLicenseCache'/>" /* 사용자 별 라이선스 인증 캐쉬를 제거합니다.<br>금일 관리자 알림 메일도 재전송하려면 [확인] 아니면 [취소]를 눌러주세요. */
				, "<spring:message code='Cache.lbl_RemoveLicenseCache'/>", function(result){ /* 라이선스 인증 캐쉬 제거 */
			var removeMailCache;
			
			if(result){
				removeMailCache = "Y";
			}else{
				removeMailCache = "N";
			}
			
			$.ajax({
				type: "post"
				, url: "/covicore/license/removeLicenseCache.do"
				, data: {
					"removeMailCache":removeMailCache
				}, 
				success: function(data){
					if(data.status == "SUCCESS"){
						Common.Inform("<spring:message code='Cache.msg_50'/>");			//삭제되었습니다.
					}else{
						Common.Warning("<spring:message code='Cache.msg_apv_030'/>");	//오류가 발생헸습니다.
					}
				},
	        	error:function(response, status, error){
	       	     CFN_ErrorAjax("/covicore/license/removeLicenseCache.do", response, status, error);
	       		}	
			});

		});
	}
</script>