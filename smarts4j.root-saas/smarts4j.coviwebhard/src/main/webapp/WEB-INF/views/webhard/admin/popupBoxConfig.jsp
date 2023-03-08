<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/adminInclude.jsp"></jsp:include>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script type="text/javascript" src="/webhard/resources/script/admin/webhard_admin.js<%=resourceVersion%>"></script>

<body>
	<form id="frmComment">	
	    <table class="AXFormTable">
	    	<colgroup>
	    		<col style='width: 20%'>
	    		<col style='width: 80%'>
	    	</colgroup>
	    	<tbody>
		    	<tr>
		    		<th><spring:message code='Cache.lbl_Quota'/></th> <!-- 할당량 -->
				   	<td style="line-height: 26px;">
				   		<input id="totalQuota" type="text" class="AXInput" style="width: 30%;" />&nbsp;MB
				   		<span id="usageQuata"></span>
				   	</td>
			   	</tr>
			   	<tr style="display: none;">
			   		<th><spring:message code='Cache.lbl_Authority'/></th> <!-- 권한 -->
			   		<td>
			   			<input type="button" id="btnOrg" class="AXButton" style="margin-right: 5px;" value="<spring:message code='Cache.btn_OrgManage'/>"> <!-- 조직도 -->
			   			<input id="txtAuthDelegation" type="text" class="AXInput" style="width: 80%;" placeholder="<spring:message code='Cache.btn_apv_directinsert'/>" /> <!-- 직접입력 -->
			   			<div id="divAuthDelegation" style="width:94%; height:150px; border: 1px solid #c6c6c6; padding: 10px; margin-top: 5px;">
			   				<table style="width: 100%; font-size: small;">
								<tbody>
									<!-- <tr>
							            <td>류다은 사원</td>
							            <td width="20">X</td>
						        	</tr> -->
								</tbody>
							</table>
			   			</div>
			   		</td>
			   	</tr>
		   	</tbody>
	   	</table>
		<div align="center" style="padding-top: 10px">
			<input type="button" id="btnSave" class="AXButton red" value="<spring:message code='Cache.lbl_Save' />"> <!-- 저장 -->
			<input type="button" id="btnClose" class="AXButton" value="<spring:message code='Cache.lbl_Cancel' />"> <!-- 취소 -->
		</div>
	</form>	
</body>

<script>
	//# sourceURL=popupBoxConfig.jsp
	
	(function(param){
		
		var curBoxSize;
		
		var init = function(){
			setEvent();
			setConfig();
		}
		
		var setEvent = function(){
			// 저장
			$("#btnSave").off("click").on("click", save);
			
			// 닫기
			$("#btnClose").off("click").on("click", function(){
				Common.Close();
			});
			
			// 조직도
			$("#btnOrg").off("click").on("click", openOrg);
		}
		
		var setConfig = function(){
			$.ajax({
				url: "/webhard/admin/boxManage/getBoxConfig.do",
				type: "POST",
				data: {
					"ownerID": param.ownerID
				},
				success: function(res){
					if(res.data){
						curBoxSize = res.data.BOX_CURRENT_SIZE;
						
						$("#totalQuota").val(res.data.BOX_MAX_SIZE);
						$("#usageQuata").text(String.format("<spring:message code='Cache.lbl_currentlyUsing'/>", webhardAdminCommon.convertFileSize(curBoxSize))); // (현재 {0} 사용)
						
						//TODO: 권한 위임 내역 바인딩
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/admin/boxManage/getBoxConfig.do", response, status, error);
				}
			});
		}
		
		var save = function(){
			if(!validation()) return false;
			
			$.ajax({
				url: "/webhard/admin/boxManage/setBoxConfig.do",
				type: "POST",
				data: {
					"totalQuota": $("#totalQuota").val(),
					"ownerID": param.ownerID
				},
				success: function(result){
					parent.webhardAdminView.render();
	  				Common.Close();
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/webhard/admin/boxManage/setBoxConfig.do", response, status, error);
				}
			});
		}
		
		var validation = function(){
			if(!$("#totalQuota").val()){
				parent.Common.Warning("<spring:message code='Cache.msg_enterQuota'/>", "Warning Dialog", function(){ // 할당량을 입력하세요
			    	$("#totalQuota").focus();
			    });
				
				return false;
			}else{
				var totalQuota = Number($("#totalQuota").val()) * 1024 * 1024;
				
				if(totalQuota <= curBoxSize){
					parent.Common.Warning("<spring:message code='Cache.msg_enterMoreCurrentUsage'/>", "Warning Dialog", function(){ // 현재 사용량보다 많게 입력해야 합니다.
				    	$("#totalQuota").focus();
				    });
					
					return false;
				}
			}
			
			return true;
		}
		
		var openOrg = function(){
			parent.Common.open("", "orgchart_pop", "<spring:message code='Cache.lbl_DeptOrgMap'/>", "/covicore/control/goOrgChart.do?callBackFunc=callBackOrgPopup&openerID=popupBoxConfig&type=B1", "1040px", "580px", "iframe", true, null, null, true);
			
			window.callBackOrgPopup = function(result){
				var result = JSON.parse(result).item;
			};
		}
		
		init();
		
	})({
		ownerID: "${ownerID}"
	});
	
</script>