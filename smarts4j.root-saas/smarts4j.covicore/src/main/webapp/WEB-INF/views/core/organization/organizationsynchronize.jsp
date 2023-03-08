<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
	.AXFormTable thead th {
		border: 1px solid #ddd !important;
		background-color: #f7f7f7;
	}
	
	.AXFormTable tbody td, #divMonitering tbody td {
		background-color: #FFFFFF;
	}

	.divpop_overlay {display:none;}
	
	.changed { font-weight: bold; color: red; }
	
	#divCompareGroup table thead tr th, 
	#divCompareGroup table tbody tr td,
	#divCompareUser table thead tr th, 
	#divCompareUser table tbody tr td,
	#divCompareAddjob table thead tr th, 
	#divCompareAddjob table tbody tr td {
	    border: 1px solid #ddd;
	    padding: 2px;
	    text-align: center;
	}
	
	#divCompareGroup table,
	#divCompareUser table,
	#divCompareAddjob table {
	    border-spacing: 0;
	    width: 100%;
	}
</style>

<script type="text/javascript" src="/groupware/resources/script/moment.min.js"></script>
	
<h3 class="con_tit_box">
   <span class="con_tit"><spring:message code='Cache.lbl_OrgSync'/></span>
	<!-- TODO 초기 페이지로 설정 향후 개발 (미구현 사항으로 숨김처리) -->
	<%-- <a href="#" class="set_box">
		<span class="set_initialpage"><p><spring:message code="Cache.btn_SettingFirstPage"/></p></span>
	</a> --%>
</h3>
<form id="form1">
	<div class="AXTabs">
		<div class="AXTabsTray" style="height:30px">
			<a id="agridtest" href="#" onclick="clickTab(this);" class="AXTab on"" value="gridtest"><spring:message code='Cache.lbl_SyncConfig'/></a>
			<a id="aloggridtest" href="#" onclick="clickTab(this);" class="AXTab" value="loggridtest"><spring:message code='Cache.menu_LogManage'/></a>
		</div>
		<div id="gridtest" style="">
			<div id="topitembar" style="width:100%;">
				<div style="width:100%;">
					<div id="topitembar_1" class="topbar_grid">
						<div>
							<!-- 
							<input type="button" value="<spring:message code='Cache.btn_ReadSyncData'/>" onclick="createSyncData();"class="AXButton"/>
							<input type="button" value="<spring:message code='Cache.btn_Synchronization'/>" onclick="synchronize();" class="AXButton" />
							<input type="button" value="<spring:message code='Cache.btn_SynchronizationAll'/>" onclick="synchronizeAll();" class="AXButton" />
							 -->
							<input type="button" value="인사데이터 연동" onclick="createSyncData();"class="AXButton"/>
							<input type="button" value="동기화" onclick="synchronize();" class="AXButton"/>
							<input type="button" value="<spring:message code='Cache.btn_SynchronizationAll'/>" onclick="synchronizeAll();" class="AXButton" />
						</div>
  
						<div style="margin-top: 10px;">
							<!-- <label><spring:message code='Cache.lbl_SyncTarget'/></label> -->
							<label>동기화 범위 설정</label>
							<table class="AXFormTable" style="border-top: 1px solid #dddddd;">
								<thead>
									<tr>
										<!-- 
										<th><spring:message code='Cache.lbl_IsSyncDB'/></th>
										<th><spring:message code='Cache.lbl_IsSyncApproval'/></th>
										<th><spring:message code='Cache.lbl_IsSyncIndi'/></th>
										<th><spring:message code='Cache.lbl_IsSyncTimeSquare'/></th>
										<th><spring:message code='Cache.lbl_IsUseAD'/></th>
										<th><spring:message code='Cache.lbl_IsSyncMail'/></th>
										<th><spring:message code='Cache.lbl_IsSyncMessenger'/></th>
										 -->
										<th style="display: none;">그룹웨어</th>
										<th>CoviMail</th>
										<th>Exchange Mail</th>
										<th>AD</th>
										<th>SFB</th>
									</tr>
								</thead>
								<tbody>
									<tr style="text-align: center;">
										<td style="display: none;"><input type="text" class="swOff" kind="switch" name="" on_value="Y" off_value="N" style="width:50px;height:20px;border:0px none;" id="IsSyncDB" value=""/></td>
										<td><input type="text" class="swOff" kind="switch" name="" on_value="Y" off_value="N" style="width:50px;height:20px;border:0px none;" id="IsSyncIndi" value=""/></td>
										<td><input type="text" class="swOff" kind="switch" name="" on_value="Y" off_value="N" style="width:50px;height:20px;border:0px none;" id="IsSyncMail" value=""/></td>
										<td><input type="text" class="swOff" kind="switch" name="" on_value="Y" off_value="N" style="width:50px;height:20px;border:0px none;" id="IsSyncAD" value=""/></td>
										<td><input type="text" class="swOff" kind="switch" name="" on_value="Y" off_value="N" style="width:50px;height:20px;border:0px none;" id="IsSyncMessenger" value=""/></td>
									</tr>
								</tbody>
							</table>
						</div>
					
						<!-- 조직도 동기화 설정 -->
						<div style="margin-top: 10px;">
							<label>연동 범위 설정</label>
							<!-- <label><spring:message code='Cache.lbl_SyncConfig'/></label> -->
					        <table class="AXFormTable" style="border-top: 1px solid #dddddd;" width="500px;">
								<tbody><tr>
									<th style="width: 145px;">부서</th>
									<td>
										<spring:message code='Cache.lbl_Add'/> :  <input name="chkDept" type="checkbox" value="chkDeptAdd" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Edit'/> :  <input name="chkDept" type="checkbox" value="chkDeptMod" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_delete'/> :  <input name="chkDept" type="checkbox" value="chkDeptDel" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Whole'/> :  <input name="chkDept" type="checkbox" value="chkDeptAll" onchange="ctrlChkBox(this,'ALL');" checked/>
									</td>
									<th style="width: 145px;"><spring:message code='Cache.lbl_JobTitle'/></th>
									<td>
										<spring:message code='Cache.lbl_Add'/> :  <input name="chkJobTitle" type="checkbox" value="chkJobTitleAdd" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Edit'/> :  <input name="chkJobTitle" type="checkbox" value="chkJobTitleMod" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_delete'/> :  <input name="chkJobTitle" type="checkbox" value="chkJobTitleDel" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Whole'/> :  <input name="chkJobTitle" type="checkbox" value="chkJobTitleAll" onchange="ctrlChkBox(this,'ALL');" checked/>
									</td>
								</tr>
								<tr>
									<th style="width: 145px;"><spring:message code='Cache.lbl_User'/></th>
									<td>
										<spring:message code='Cache.lbl_Add'/> :  <input name="chkUser" type="checkbox" value="chkUserAdd" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Edit'/> :  <input name="chkUser" type="checkbox" value="chkUserMod" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_delete'/> :  <input name="chkUser" type="checkbox" value="chkUserDel" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Whole'/> :  <input name="chkUser" type="checkbox" value="chkUserAll" onchange="ctrlChkBox(this,'ALL');" checked/>
									</td>
									<th style="width: 145px;"><spring:message code='Cache.lbl_JobPosition'/></th>
									<td>
										<spring:message code='Cache.lbl_Add'/> :  <input name="chkJobPosition" type="checkbox" value="chkJobPositionAdd" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Edit'/> :  <input name="chkJobPosition" type="checkbox" value="chkJobPositionMod" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_delete'/> :  <input name="chkJobPosition" type="checkbox" value="chkJobPositionDel" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Whole'/> :  <input name="chkJobPosition" type="checkbox" value="chkJobPositionAll" onchange="ctrlChkBox(this,'ALL');" checked/>
									</td>
								</tr>
								<tr>
									<th style="width: 145px;"><spring:message code='Cache.lbl_AddJob'/></th>
									<td>
										<spring:message code='Cache.lbl_Add'/> :  <input name="chkAddJob" type="checkbox" value="chkAddJobAdd" onchange="ctrlChkBox(this,'ONE');" checked />
										<!-- <spring:message code='Cache.lbl_Edit'/> :  <input name="chkAddJob" type="checkbox" value="chkAddJobMod" checked /> -->
										<spring:message code='Cache.lbl_delete'/> :  <input name="chkAddJob" type="checkbox" value="chkAddJobDel" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Whole'/> :  <input name="chkAddJob" type="checkbox" value="chkAddJobAll" onchange="ctrlChkBox(this,'ALL');" checked />
									</td>
									<th style="width: 145px;"><spring:message code='Cache.lbl_JobLevel'/></th>
									<td>
										<spring:message code='Cache.lbl_Add'/> :  <input name="chkJobLevel" type="checkbox" value="chkJobLevelAdd" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Edit'/> :  <input name="chkJobLevel" type="checkbox" value="chkJobLevelMod" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_delete'/> :  <input name="chkJobLevel" type="checkbox" value="chkJobLevelDel" onchange="ctrlChkBox(this,'ONE');" checked />
										<spring:message code='Cache.lbl_Whole'/> :  <input name="chkJobLevel" type="checkbox" value="chkJobLevelAll" onchange="ctrlChkBox(this,'ALL');" checked/>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="AXTabsTray" style="height: 30px; margin-top: 10px;">
						<a id="asyncHistory" href="#" onclick="clickInnerTab(this);" class="AXTab on" data-role="inner" value="syncHistory">동기화 이력 조회</a>
						<a id="asyncTargetList" href="#" onclick="clickInnerTab(this);" class="AXTab" data-role="inner" value="syncTargetList">동기화 대상 조회</a>
					</div>
					<div id="divMonitering"></div>
				</div>
			</div>
		</div>
	</div>
		
	<div id="loggridtest" style="height:1000px; display:none;" >
		<div style="padding:10px;height:1000px;">
			<div id="topitembar_1" class="topbar_grid">
				<input type="button" value="<spring:message code="Cache.lbl_Refresh"/>" onclick="refresh();"class="AXButton BtnRefresh"/>					
				&nbsp;&nbsp;<spring:message code='Cache.lbl_SearchByDate'/>&nbsp;&nbsp;
				<input type="text" class="AXInput adDate" id="txtFirstDate" date_separator="." kind="date" data-axbind="date" vali_early="true" vali_date_id="txtLastDate" onchange="changeLogTitleList(this.value,'');">
				&nbsp;~&nbsp;
				<input type="text" class="AXInput adDate" id="txtLastDate" date_separator="." kind="date" data-axbind="date"  vali_late="true" vali_date_id="txtFirstDate" onchange="changeLogTitleList('',this.value);">
			</div>	
			<div id="resultBoxWrap">
				<div id="orgLogListGrid"></div>
			</div>
		</div>
	</div>
</form>

<script type="template" id="tempTableCompareGroup">
	<table style="white-space: nowrap;">
		<thead>
			<tr>
				<th></th>
				<th>Action</th>
				<th>SyncType</th>
				<th>GroupCode</th>
				<th>CompanyCode</th>
				<th>GroupType</th>
				<th>MemberOf</th>
				<th>DisplayName</th>
				<th>MultiDisplayName</th>
				<th>SortKey</th>
				<th>IsDisplay</th>
				<th>IsMail</th>
				<th>PrimaryMail</th>
				<th>SecondaryMail</th>
				<th>ManagerCode</th>
				<th>Reserved1</th>
				<th>Reserved2</th>
				<th>Reserved3</th>
				<th>Reserved4</th>
				<th>Reserved5</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>	
</script>

<script type="template" id="tempTableCompareUser">
	<table style="white-space: nowrap;">
		<thead>
			<tr>
				<th></th>
				<th>Action</th>
				<th>SyncType</th>
				<th>UserCode</th>
				<th>LogonID</th>
				<th>EmpNo</th>
				<th>DisplayName</th>
				<th>CompanyCode</th>
				<th>DeptCode</th>
				<th>JobTitleCode</th>
				<th>JobPositionCode</th>
				<th>JobLevelCode</th>
				<th>RegionCode</th>
				<th>SortKey</th>
				<th>IsDisplay</th>
				<th>EnterDate</th>
				<th>RetireDate</th>
				<th>BirthDiv</th>
				<th>BirthDate</th>
				<th>MailAddress</th>
				<th>ExternalMailAddress</th>
				<th>ChargeBusiness</th>
				<th>PhoneNumberInter</th>
				<th>PhoneNumber</th>
				<th>Mobile</th>
				<th>Fax</th>
				<th>Reserved1</th>
				<th>Reserved2</th>
				<th>Reserved3</th>
				<th>Reserved4</th>
				<th>Reserved5</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>	
</script>

<script type="template" id="tempTableCompareAddJob">
	<table>
		<thead>
			<tr>
				<th></th>
				<th>Action</th>
				<th>SyncType</th>
				<th>UserCode</th>
				<th>JobType</th>
				<th>CompanyCode</th>
				<th>DeptCode</th>
				<th>JobTitleCode</th>
				<th>JobPositionCode</th>
				<th>JobLevelCode</th>
				<th>RegionCode</th>
				<th>SortKey</th>
				<th>Reserved1</th>
				<th>Reserved2</th>
				<th>Reserved3</th>
				<th>Reserved4</th>
				<th>Reserved5</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>	
</script>

<script type="text/javascript">	
	var _syncStart = false;
	var _syncStop = false;
	var _lastSyncMasterID = 0;
	var orgLogListGrid = new coviGrid();
	
	// 개별호출 일괄처리
	Common.getBaseConfigList(["IsSyncDB", "IsSyncApproval", "IsSyncIndi", "IsSyncTimeSquare", "IsSyncAD", "IsSyncMail", "IsSyncMessenger"]);
	
	$(document).ready(function(){
		$.ajaxSetup({
		    beforeSend: function (xhr){
		       var overlay = $('<div id="dupe_overlay" class="divpop_overlay" style="display:none;position:fixed;z-index:100;top:0px;left:0px;width:100%;height:100%;background:rgb(0,0,0);opacity:0;"></div>');
		       overlay.appendTo(document.body);
		    },
		    complete : function(xhr){
		    	$('#dupe_overlay').remove();
		    	$(".divpop_overlay").remove();
		    }
		});
		
		setSyncConfig();
		coviInput.setSwitch();		// oviInput.init(); 이 2.5초 지연 실행되도록 되어있음. CommonControls.js, 스위치 변환 스크립트 바로 실행
		showSyncHistory();
	});	
	
	function refresh(){
		orgLogListGrid.reloadList();
	}
	
	function setSyncConfig() {
		if (coviCmn.configMap["IsSyncDB"] == null | coviCmn.configMap["IsSyncDB"] == '' |  coviCmn.configMap["IsSyncDB"] == 'N'){ $("#IsSyncDB").val("N").prop("selected", true); } 
		else { $("#IsSyncDB").val("Y").prop("selected", true); }
			
		if (coviCmn.configMap["IsSyncIndi"] == null | coviCmn.configMap["IsSyncIndi"] == '' |  coviCmn.configMap["IsSyncIndi"] == 'N'){ $("#IsSyncIndi").val("N").prop("selected", true); } 
		else { $("#IsSyncIndi").val("Y").prop("selected", true); }
		
		if (coviCmn.configMap["IsSyncAD"] == null | coviCmn.configMap["IsSyncAD"] == '' |  coviCmn.configMap["IsSyncAD"] == 'N'){ $("#IsSyncAD").val("N").prop("selected", true); }
		else { $("#IsSyncAD").val("Y").prop("selected", true); }
		
		if (coviCmn.configMap["IsSyncMail"] == null | coviCmn.configMap["IsSyncMail"] == '' |  coviCmn.configMap["IsSyncMail"] == 'N'){ $("#IsSyncMail").val("N").prop("selected", true); }
		else { $("#IsSyncMail").val("Y").prop("selected", true); }
		
		if (coviCmn.configMap["IsSyncMessenger"] == null | coviCmn.configMap["IsSyncMessenger"] == '' |  coviCmn.configMap["IsSyncMessenger"] == 'N'){ $("#IsSyncMessenger").val("N").prop("selected", true); }
		else { $("#IsSyncMessenger").val("Y").prop("selected", true); }
	}
	
	function ctrlChkBox(obj, type) {
        if (type == "ALL") {
            if ($(obj).prop("checked"))
                $(obj).parent().find('input[type=checkbox]').prop("checked", true);
            else
                $(obj).parent().find('input[type=checkbox]').prop("checked", false);
        }
        else if (type == "ONE") {
        	if ($(obj).prop("checked")) {
        		if ($(obj).parent().find('input[type=checkbox]:not(:checked)').not(':last').length == 0)
        			$(obj).parent().find('input[type=checkbox]').last().prop("checked", true);
        	}
        	else {
        		$(obj).parent().find('input[type=checkbox]').last().prop("checked", false);
        	}
        }
    }
	
	// 인사데이터 읽어오기
	function createSyncData(){
		Common.Confirm("<spring:message code='Cache.msg_153'/>", "Confirmation Dialog", function (result) { 
			if(result) {
				$.ajax({
					url: "/covicore/admin/orgmanage/createsyncdata.do",
					type: "POST",
					data: { Web: "Y" },
					success: function(d) {
						console.info(d);
						if(d.status == "SUCCESS") {
							$("#asyncTargetList").click();
							alert("인사데이터를 가져오는 데 성공하였습니다. \n [그룹 : " + d.compareList.compareGroupCnt + " 건][사용자 : " + d.compareList.compareUserCnt + " 건][겸직 : " + d.compareList.compareAddJobCnt + "] 건");
						} 
						else {
							alert("인사데이터를 정상적으로 가져오지 못했습니다. \n");					
						}				
					},
					error: function(response, status, error) {			
						var err =new Function ("return (" + response.responseText + ")").apply();	
						alert("[ERROR] - createSyncData:" + err.Message);
					}
				});
			}
		});
	}
	
	// 인사데이터 동기화
	function synchronize(){		
		Common.Confirm("<spring:message code='Cache.msg_154'/>", "Confirmation Dialog", function (result) {
			if(result) {
				var sChkBoxConfig = "";
				$('input[type="checkbox"]:checked').each(function () {
					sChkBoxConfig += $(this).val() + ";";
				});
				
				setTimeout(function() { 
					if (!_syncStart) _lastSyncMasterID = 0;
					_syncStart = true;
					moniterCompareCount();
			    	 
			    	 $.ajax({
							url: "/covicore/admin/orgmanage/synchronizeByweb.do",
							data: {
								sChkBoxConfig: sChkBoxConfig,
								IsSyncDB: $("#IsSyncDB").val(),
								IsSyncIndi: $("#IsSyncIndi").val(),
								IsSyncAD: $("#IsSyncApproval").val(),
								IsSyncMail: $("#IsSyncMail").val(),
								IsSyncMessenger: $("#IsSyncMessenger").val(),
							},
							type: "POST",
							success: function(d) {
								_syncStart = false;
								if(d.status == "SUCCESS") {						
									Common.Inform(d.status, "Inform", function() {
										parent.Refresh();
										Common.Close();
									});
								} 
								else {
									if(d.isStopped == "N") {
										Common.Warning(d.message, "Warning", function() {
											parent.Refresh();
											Common.Close();
										});
									}
									else {
										_syncStop = true;
									}
								}
							},
							error:function(response, status, error){
								_syncStart = false;
								CFN_ErrorAjax("/covicore/admin/orgmanage/synchronize.do", response, status, error);
							}
						});
			    }, 1000);
			}
		});
	}
	
	// 동기화 일시중지
	function stopSync() {
		if(_syncStart){
			$.ajax({
				url: "/covicore/admin/orgmanage/stopSync.do",
				data: {},
				type: "POST",
				success: function(d) {},
				error: function(response, status, error) {}
			});
		}
	} 
	
	// 동기화 대상 잔여건수 표시 
	function moniterCompareCount(){
		if(_syncStart){
			$.ajax({
				url: "/covicore/admin/orgmanage/MoniterSyncStatus.do",
				type: "POST",
				success: function(d) {
					if(d.status == "SUCCESS") {
						if (d.SyncMasterID == _lastSyncMasterID && _lastSyncMasterID != 0){
							Common.Progress("동기화 처리대상 잔여건 \n [그룹 : " + d.GR_Cnt +" 건][사용자 : " + d.UR_Cnt +" 건][겸직 : " + d.Add_Cnt +" 건]<br><button class=\"AXButton\" onclick=\"javascript:stopSync();\">동기화 중지</button>");
							showSyncHistory();
							
							setTimeout(function() { moniterCompareCount(); }, 3000);
						}
						else {
							showSyncHistory();
							setTimeout(function() { moniterCompareCount(); }, 1000);
						}
					}
					else {
						Common.Warning("동기화 처리건을 가져오는데 실패하였습니다. \n" + d.message);					
					}				
				},
				error: function(response, status, error) {				
					var err =new Function ("return (" + response.responseText + ")").apply();	
					Common.Error("[ERROR] - createSyncData:" + err.Message);				}
			});
		}
		else {
			if (_syncStop) {
				Common.Inform("동기화가 중단되었습니다.");
				_syncStop = false;
			}
			else {
				Common.Inform("동기화가 종료되었습니다.");
			}
			
			showSyncHistory();
		}		
	}
	
	// 전체 동기화
	function synchronizeAll() {
		Common.Confirm("<spring:message code='Cache.msg_157'/>", "Confirmation Dialog", function (result) { 
			if(result) {
				setTimeout(function() {
					if (!_syncStart) _lastSyncMasterID = 0;
					_syncStart = true;
					moniterCompareCount();

					$.ajax({
						url: "/covicore/admin/orgmanage/organizationsynchronize.do",
						data: {},
						type: "POST",
						success: function(d) {
							console.info('synchronizeAll', d);	
							_syncStart = false;
							if(d.status == "SUCCESS") {						
								Common.Inform(d.status, "Inform", function() {
									Common.Close();
								});
							} 
							else {
								if(d.isStopped == "N") {
									Common.Warning(d.message, "Warning", function() {
										Common.Close();
									});
								}
								else {
									_syncStop = true;
								}
							}
						},
						error: function(response, status, error) {
							CFN_ErrorAjax("/covicore/admin/orgmanage/organizationsynchronize.do", response, status, error);
							_syncStart = false;
						}
					});
				}, 1000);
			}
		});
	}
	
	function clickTab(pObj) {
		$(".AXTab[data-role!=inner]").attr("class","AXTab");
		$(pObj).addClass("AXTab on");

		var str = $(pObj).attr("value");
		$(".AXTab[data-role!=inner]").each(function() {
			if ($(this).attr("class") == "AXTab on") {
				$("#" + $(this).attr("value")).show();
				setLogGridConfig();
			}
			else {
				$("#" + $(this).attr("value")).hide();
			}
		});
	}
	
	function clickInnerTab(target) {
		$(".AXTab[data-role=inner]").attr("class","AXTab");
		$(target).addClass("AXTab on");

		$(".AXTab[data-role=inner]").each(function() {
			if ($(this).attr("class") == "AXTab on") {
				$("#" + $(this).attr("value")).show();
			} else {
				$("#" + $(this).attr("value")).hide();
			}
		});
		
		var str = $(target).attr("value");
		if (str == 'syncTargetList') {
			showCompareList();
		}
		else if (str == 'syncHistory') {
			showSyncHistory();
		}
	}
	
	//Grid 관련 사항 추가 -
	function setLogGridConfig() {
		setLogListGrid();
		
		var configObj = {
			targetID: "orgLogListGrid", // grid target 지정
			height: "auto",
			page: {
				pageNo: 1,
				pageSize: 15
			},
			paging: true
		};
		
		// Grid Config 적용
		orgLogListGrid.setGridConfig(configObj);
		
		bindLogListGridData();
	}
	
	//Grid 생성 관련	
	function setLogListGrid(){
		orgLogListGrid.setGridHeader([
			{key:'SyncMasterID', label:'SyncID', width:'20',  align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"GR\",\"INSERT\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.SyncMasterID + "</span></a>";
			}},
			{key:'GRInsertCnt', label:'그룹추가', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"GR\",\"INSERT\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.GRInsertCnt + " 건</span></a>";
			}}, 
			{key:'GRUpdateCnt', label:'그룹수정', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"GR\",\"UPDATE\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.GRUpdateCnt + " 건</span></a>";
			}}, 
			{key:'GRDeleteCnt', label:'그룹삭제', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"GR\",\"DELETE\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.GRDeleteCnt + " 건</span></a>";
			}}, 
			{key:'URInsertCnt', label:'사용자추가', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"UR\",\"INSERT\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.URInsertCnt + " 건</span></a>";
			}}, 
			{key:'URUpdateCnt', label:'사용자수정', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"UR\",\"UPDATE\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.URUpdateCnt + " 건</span></a>";
			}}, 
			{key:'URDeleteCnt', label:'사용자삭제', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"UR\",\"DELETE\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.URDeleteCnt + " 건</span></a>";
			}}, 
			{key:'AddJobInsertCnt', label:'겸직추가', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"AddJob\",\"INSERT\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.AddJobInsertCnt + " 건</span></a>";
			}}, 
			{key:'AddJobUpdateCnt', label:'겸직수정', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"AddJob\",\"UPDATE\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.AddJobUpdateCnt + " 건</span></a>";
			}}, 
			{key:'AddJobDeleteCnt', label:'겸직삭제', width:'20', align:'center', formatter : function(){
				return "<a href='#' onclick='viewLogList(\""+this.item.SyncMasterID+"\",\"AddJob\",\"DELETE\", \"" + this.item.InsertDate + "\"); return false;'><span name='code'>" + this.item.AddJobDeleteCnt + " 건</span></a>";
			}},  
			{key:'InsertDate', label:'일자', width:'50', align:'center', formatter : function(){
				return "<span name='code'>" + CFN_TransLocalTime(this.item.InsertDate) + "</span>";
			}}
		]);
	}
	
	function bindLogListGridData(pFirstDate, pLastDate) {	
		var ty = "one";
		orgLogListGrid.bindGrid({	
			ajaxUrl:"/covicore/admin/orgmanage/getTitleLogList.do",
			ajaxPars: {
				FirstDate: pFirstDate,
				LastDate: pLastDate,
				sortBy: orgLogListGrid.getSortParam(ty)
			}
		});		
	}
	
	function changeLogTitleList(pFirstDate, pLastDate) {
		var strFirstDate = pFirstDate;
		var strLastDate = pLastDate;
		
		if(pFirstDate == '') {
			strFirstDate = $("#txtFirstDate").val();
		}
		
		if(pLastDate == '') {
			strLastDate = $("#txtLastDate").val();
		}
	
		bindLogListGridData(strFirstDate, strLastDate);
	}
	
	function viewLogList(pStrSyncMasterID,pStrObjectType,pStrSyncType, pStrInsertDate) {
		var sOpenName = "divLogList";

		var sURL = "/covicore/loglistpop.do";
		sURL += "?ObjectType=" + pStrObjectType;
		sURL += "&SyncType=" + pStrSyncType;
		sURL += "&SyncMasterID=" + pStrSyncMasterID;		
		sURL += "&InsertDate=" + pStrInsertDate;
		sURL += "&OpenName=" + sOpenName;
		
		var sTitle = "";
		sTitle = "<spring:message code='Cache.lbl_LogList'/>" + " ||| " + "<spring:message code='Cache.lbl_PrintLogList'/>";

		var sWidth = "1000px";
		var sHeight = "680px";
		Common.open("", sOpenName, sTitle, sURL, sWidth, sHeight, "iframe", false, null, null, true);		
	}
	
	// 동기화 이력 조회
	function showSyncHistory() {
		$.ajax({
			async: true,
			type: "POST",
			data: { Cnt: 20 },
			url: "/covicore/admin/orgmanage/selectSyncHitory.do",
			success: function(data) {
				if (data.status == "FAIL") {
					alert(data.message);
					return;
				}
				
				var varIdx = 0
				var varLastLog = 0;
				var strContent = "<table style=\"width: 100%; margin-top: 10px; border-spacing: 0; display: block; height: 430px; overflow: hidden;\"><tr><td style='vertical-align: top; width: 550px; height: 420px;'><ui id='log_list'>";
				
				$(data.list).each(function(idx, obj) {
					if (idx == 0) { _lastSyncMasterID = obj.SyncMasterID; }
					strContent += "<li id=\"log_"+obj.SyncMasterID+"\" onclick=\"javascript:showSyncItemLog(\'tdItemLogList\',\'" + obj.SyncMasterID + "');\" style=\" padding: 0 5px;\">"; 
					if(varLastLog == 0) {
						varLastLog = obj.SyncMasterID;
					}
					var x = new moment(obj.StartDate);
					var y = new moment(obj.EndDate);
					var dif = y.diff(x, 'seconds');
					var difstr = (isNaN(dif)) ? ((_syncStart) ? ' (처리중) ' : ' (중단) ') : " ("+ dif+"s) ";
					
					strContent += "<strong>" + obj.SyncMasterID + ".</strong> ";
					strContent += obj.StartDate + difstr;
					strContent += " 그룹(" + Number(Number(obj.GRInsertCnt)+Number(obj.GRUpdateCnt)+Number(obj.GRDeleteCnt)) +"), ";
					strContent += " 사용자(" + Number(Number(obj.URInsertCnt)+Number(obj.URUpdateCnt)+Number(obj.URDeleteCnt)) +"), ";
					strContent += " 겸직(" + Number(Number(obj.AddJobInsertCnt)+Number(obj.AddJobUpdateCnt)+Number(obj.AddJobDeleteCnt)) +"), ";
					strContent += "</li>";
				});
				strContent += "</ui></td><td id='tdItemLogList' style='vertical-align:top; width: 1000px; padding: 5px; background-color: #d0d0d0;'></td></tr></table>";
				$("#divMonitering").html(strContent);
				// 최종로그 조회
				showSyncItemLog("tdItemLogList", varLastLog);
			},
			error : function(response, status, error) {
				CFN_ErrorAjax("/covicore/admin/orgmanage/selectSyncHitory.do", response, status, error);
			}
		});
	}
	
	// 동기화 실행 회차별 로그 조회 
	function showSyncItemLog(pTarget, pSyncMasterID){
		$("#log_list li").css("background-color", "white");
		$("#log_"+pSyncMasterID).css("background-color", "#d0d0d0");
		
		$.ajax({
			async : false,
			type : "POST",
			data : { "SyncMasterID" : pSyncMasterID },
			url : "/covicore/admin/orgmanage/selectSyncItemLog.do",
			success : function(data) {
				if (data.status == "FAIL") {
					alert(data.message);
					return;
				}
				
				var varIdx = 0
				var varLastLog = 0;
				var strContent = "";
				strContent += '<table style="width: 100%">';
				strContent += '<tbody style="display: block; max-height: 420px; overflow-y: auto;">';
				$(data.list).each(function(idx, obj) {
					strContent += '<tr>';
					strContent += '<td style="width: 50px; background-color: transparent; vertical-align: top;"><strong>' + obj.Seq + '.</strong></td>';
					strContent += '<td style="width: 50px; background-color: transparent; vertical-align: top;">' + ((obj.LogType == 'Error') ? '<span style="color: red; font-weight: bold;">' : '') + obj.LogType + ((obj.LogType == 'Error') ? '</span>' : '') + '</td>';
					strContent += '<td style="background-color: transparent;">' + obj.LogMessage + '</td>';
					strContent += '</tr>';
				});
				strContent += '</tbody>';
				strContent += '</table>';
				$("#"+pTarget).html(strContent);
				window.scrollTo(0,document.body.scrollHeight); 
				$("#tdItemLogList table tbody").scrollTop($("#tdItemLogList table tbody")[0].scrollHeight);
			},
			error : function(response, status, error) {
				CFN_ErrorAjax("/covicore/admin/orgmanage/selectSyncItemLog.do", response, status, error);
			}
		});
	}
	
	function showCompareList(){
		$("#divMonitering").html("");
		$("#divMonitering").append(
			"<div id=\"divCompareGroup\" style=\"width: 100%; overflow-x: auto;\"></div>"+
			"<div style=\"border: 1px dashed gray; margin: 10px 0;\"></div>"+
			"<div id=\"divCompareUser\" style=\"width: 100%; overflow-x: auto;\"></div>"+
			"<div style=\"border: 1px dashed gray; margin: 10px 0;\"></div>"+
			"<div id=\"divCompareAddjob\"></div>"
		);
		
		showCompareGroupList();
		showCompareUserList();
		showCompareAddjobList();
	}
	
	function showCompareGroupList(){
		$("#divMonitering #divCompareGroup").append($("#tempTableCompareGroup").html());
		
		$.ajax({
			async: true,
			type: "POST",
			url: "/covicore/admin/orgmanage/selectCompareList.do",
			data: { type: 'Group' },
			success: function(data) {
				if (data.status == "FAIL") {
					alert(data.message);
					return;
				}
				
				$("#divMonitering #divCompareGroup").prepend("<span><b>Group ("+data.list.length+")</b></span>");
				$(data.list).each(function(idx, obj) {
					var strContent = "<tr><td><input type=\"checkbox\"/></td><td><b>" + (idx+1) + ".</b></td>";
					strContent += "<td>" + obj.SyncType + "</td>";
					strContent += "<td>" + obj.GroupCode + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.CompanyCode != obj.CompanyCode_Before) ? "<span class=\"changed\">" : "") + obj.CompanyCode + ((obj.SyncType == 'UPDATE' && obj.CompanyCode != obj.CompanyCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.GroupType != obj.GroupType_Before) ? "<span class=\"changed\">" : "") + obj.GroupType + ((obj.SyncType == 'UPDATE' && obj.GroupType != obj.GroupType_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.MemberOf != obj.MemberOf_Before) ? "<span class=\"changed\">" : "") + obj.MemberOf + ((obj.SyncType == 'UPDATE' && obj.MemberOf != obj.MemberOf_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.DisplayName != obj.DisplayName_Before) ? "<span class=\"changed\">" : "") + obj.DisplayName + ((obj.SyncType == 'UPDATE' && obj.DisplayName != obj.DisplayName_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.MultiDisplayName != obj.MultiDisplayName_Before) ? "<span class=\"changed\">" : "") + obj.MultiDisplayName + ((obj.SyncType == 'UPDATE' && obj.MultiDisplayName != obj.MultiDisplayName_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.SortKey != obj.SortKey_Before) ? "<span class=\"changed\">" : "") + obj.SortKey + ((obj.SyncType == 'UPDATE' && obj.SortKey != obj.SortKey_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.IsDisplay != obj.IsDisplay_Before) ? "<span class=\"changed\">" : "") + obj.IsDisplay + ((obj.SyncType == 'UPDATE' && obj.IsDisplay != obj.IsDisplay_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.IsMail != obj.IsMail_Before) ? "<span class=\"changed\">" : "") + obj.IsMail + ((obj.SyncType == 'UPDATE' && obj.IsMail != obj.IsMail_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.PrimaryMail != obj.PrimaryMail_Before) ? "<span class=\"changed\">" : "") + obj.PrimaryMail + ((obj.SyncType == 'UPDATE' && obj.PrimaryMail != obj.PrimaryMail_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.SecondaryMail != obj.SecondaryMail_Before) ? "<span class=\"changed\">" : "") + obj.SecondaryMail + ((obj.SyncType == 'UPDATE' && obj.SecondaryMail != obj.SecondaryMail_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.ManagerCode != obj.ManagerCode_Before) ? "<span class=\"changed\">" : "") + obj.ManagerCode + ((obj.SyncType == 'UPDATE' && obj.ManagerCode != obj.ManagerCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved1 != obj.Reserved1_Before) ? "<span class=\"changed\">" : "") + obj.Reserved1 + ((obj.SyncType == 'UPDATE' && obj.Reserved1 != obj.Reserved1_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved2 != obj.Reserved2_Before) ? "<span class=\"changed\">" : "") + obj.Reserved2 + ((obj.SyncType == 'UPDATE' && obj.Reserved2 != obj.Reserved2_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved3 != obj.Reserved3_Before) ? "<span class=\"changed\">" : "") + obj.Reserved3 + ((obj.SyncType == 'UPDATE' && obj.Reserved3 != obj.Reserved3_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved4 != obj.Reserved4_Before) ? "<span class=\"changed\">" : "") + obj.Reserved4 + ((obj.SyncType == 'UPDATE' && obj.Reserved4 != obj.Reserved4_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved5 != obj.Reserved5_Before) ? "<span class=\"changed\">" : "") + obj.Reserved5 + ((obj.SyncType == 'UPDATE' && obj.Reserved5 != obj.Reserved5_Before) ? "</span>" : "") + "</td></tr>";
					$("#divMonitering #divCompareGroup table tbody").append(strContent);
				});
			},
			error : function(response, status, error) {
				CFN_ErrorAjax("/covicore/admin/orgmanage/selectCompareList.do", response, status, error);
			}
		});
	}
	
	function showCompareUserList(){
		$("#divMonitering #divCompareUser").append($("#tempTableCompareUser").html());
		
		$.ajax({
			async: true,
			type: "POST",
			url: "/covicore/admin/orgmanage/selectCompareList.do",
			data: { type: 'User' },
			success: function(data) {
				if (data.status == "FAIL") {
					alert(data.message);
					return;
				}
				
				$("#divMonitering #divCompareUser").prepend("<span><b>User ("+data.list.length+")</b></span>");
				$(data.list).each(function(idx, obj) {
					var strContent = "<tr><td><input type=\"checkbox\"/></td><td><b>" + (idx+1) + ".</b></td>";
					strContent += "<td>" + obj.SyncType + "</td>";
					strContent += "<td>" + obj.UserCode + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.LogonID != obj.LogonID_Before) ? "<span class=\"changed\">" : "") + obj.LogonID + ((obj.SyncType == 'UPDATE' && obj.LogonID != obj.LogonID_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.EmpNo != obj.EmpNo_Before) ? "<span class=\"changed\">" : "") + obj.EmpNo + ((obj.SyncType == 'UPDATE' && obj.EmpNo != obj.EmpNo_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.DisplayName != obj.DisplayName_Before) ? "<span class=\"changed\">" : "") + obj.DisplayName + ((obj.SyncType == 'UPDATE' && obj.DisplayName != obj.DisplayName_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.CompanyCode != obj.CompanyCode_Before) ? "<span class=\"changed\">" : "") + obj.CompanyCode + ((obj.SyncType == 'UPDATE' && obj.CompanyCode != obj.CompanyCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.DeptCode != obj.DeptCode_Before) ? "<span class=\"changed\">" : "") + obj.DeptCode + ((obj.SyncType == 'UPDATE' && obj.DeptCode != obj.DeptCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.JobTitleCode != obj.JobTitleCode_Before) ? "<span class=\"changed\">" : "") + obj.JobTitleCode + ((obj.SyncType == 'UPDATE' && obj.JobTitleCode != obj.JobTitleCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.JobPositionCode != obj.JobPositionCode_Before) ? "<span class=\"changed\">" : "") + obj.JobPositionCode + ((obj.SyncType == 'UPDATE' && obj.JobPositionCode != obj.JobPositionCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.JobLevelCode != obj.JobLevelCode_Before) ? "<span class=\"changed\">" : "") + obj.JobLevelCode + ((obj.SyncType == 'UPDATE' && obj.JobLevelCode != obj.JobLevelCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.RegionCode != obj.RegionCode_Before) ? "<span class=\"changed\">" : "") + obj.RegionCode + ((obj.SyncType == 'UPDATE' && obj.RegionCode != obj.RegionCode_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.SortKey != obj.SortKey_Before) ? "<span class=\"changed\">" : "") + obj.SortKey + ((obj.SyncType == 'UPDATE' && obj.SortKey != obj.SortKey_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.IsDisplay != obj.IsDisplay_Before) ? "<span class=\"changed\">" : "") + obj.IsDisplay + ((obj.SyncType == 'UPDATE' && obj.IsDisplay != obj.IsDisplay_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.EnterDate != obj.EnterDate_Before) ? "<span class=\"changed\">" : "") + obj.EnterDate + ((obj.SyncType == 'UPDATE' && obj.EnterDate != obj.EnterDate_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.RetireDate != obj.RetireDate_Before) ? "<span class=\"changed\">" : "") + obj.RetireDate + ((obj.SyncType == 'UPDATE' && obj.RetireDate != obj.RetireDate_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.BirthDiv != obj.BirthDiv_Before) ? "<span class=\"changed\">" : "") + obj.BirthDiv + ((obj.SyncType == 'UPDATE' && obj.BirthDiv != obj.BirthDiv_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.BirthDate != obj.BirthDate_Before) ? "<span class=\"changed\">" : "") + obj.BirthDate + ((obj.SyncType == 'UPDATE' && obj.BirthDate != obj.BirthDate_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.MailAddress != obj.MailAddress_Before) ? "<span class=\"changed\">" : "") + obj.MailAddress + ((obj.SyncType == 'UPDATE' && obj.MailAddress != obj.MailAddress_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.ExternalMailAddress != obj.ExternalMailAddress_Before) ? "<span class=\"changed\">" : "") + obj.ExternalMailAddress + ((obj.SyncType == 'UPDATE' && obj.ExternalMailAddress != obj.ExternalMailAddress_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.ChargeBusiness != obj.ChargeBusiness_Before) ? "<span class=\"changed\">" : "") + obj.ChargeBusiness + ((obj.SyncType == 'UPDATE' && obj.ChargeBusiness != obj.ChargeBusiness_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.PhoneNumberInter != obj.PhoneNumberInter_Before) ? "<span class=\"changed\">" : "") + obj.PhoneNumberInter + ((obj.SyncType == 'UPDATE' && obj.PhoneNumberInter != obj.PhoneNumberInter_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.PhoneNumber != obj.PhoneNumber_Before) ? "<span class=\"changed\">" : "") + obj.PhoneNumber + ((obj.SyncType == 'UPDATE' && obj.PhoneNumber != obj.PhoneNumber_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Mobile != obj.Mobile_Before) ? "<span class=\"changed\">" : "") + obj.Mobile + ((obj.SyncType == 'UPDATE' && obj.Mobile != obj.Mobile_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Fax != obj.Fax_Before) ? "<span class=\"changed\">" : "") + obj.Fax + ((obj.SyncType == 'UPDATE' && obj.Fax != obj.Fax_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved1 != obj.Reserved1_Before) ? "<span class=\"changed\">" : "") + obj.Reserved1 + ((obj.SyncType == 'UPDATE' && obj.Reserved1 != obj.Reserved1_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved2 != obj.Reserved2_Before) ? "<span class=\"changed\">" : "") + obj.Reserved2 + ((obj.SyncType == 'UPDATE' && obj.Reserved2 != obj.Reserved2_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved3 != obj.Reserved3_Before) ? "<span class=\"changed\">" : "") + obj.Reserved3 + ((obj.SyncType == 'UPDATE' && obj.Reserved3 != obj.Reserved3_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved4 != obj.Reserved4_Before) ? "<span class=\"changed\">" : "") + obj.Reserved4 + ((obj.SyncType == 'UPDATE' && obj.Reserved4 != obj.Reserved4_Before) ? "</span>" : "") + "</td>";
					strContent += "<td>" + ((obj.SyncType == 'UPDATE' && obj.Reserved5 != obj.Reserved5_Before) ? "<span class=\"changed\">" : "") + obj.Reserved5 + ((obj.SyncType == 'UPDATE' && obj.Reserved5 != obj.Reserved5_Before) ? "</span>" : "") + "</td>";
					$("#divMonitering #divCompareUser table tbody").append(strContent);
				});
			},
			error : function(response, status, error) {
				CFN_ErrorAjax("/covicore/admin/orgmanage/selectCompareList.do", response, status, error);
			}
		});
	}
	
	function showCompareAddjobList(){
		$("#divMonitering #divCompareAddjob").append($("#tempTableCompareAddJob").html());

		$.ajax({
			async: true,
			type: "POST",
			url: "/covicore/admin/orgmanage/selectCompareList.do",
			data: { type: 'Addjob' },
			success: function(data) {
				if (data.status == "FAIL") {
					alert(data.message);
					return;
				}
				
				$("#divMonitering #divCompareAddjob").prepend("<span><b>AddJob ("+data.list.length+")</b></span>");
				$(data.list).each(function(idx, obj) {
					var strContent = "<tr><td><input type=\"checkbox\"/></td><td><b>" + (idx+1) + ".</b></td>";
					strContent += "<td>" + obj.SyncType + "</td>";
					strContent += "<td>" + obj.UserCode + "</td>";
					strContent += "<td>" + obj.JobType + "</td>";
					strContent += "<td>" + obj.CompanyCode + "</td>";
					strContent += "<td>" + obj.DeptCode + "</td>";
					strContent += "<td>" + obj.JobTitleCode + "</td>";
					strContent += "<td>" + obj.JobPositionCode + "</td>";
					strContent += "<td>" + obj.JobLevelCode + "</td>";
					strContent += "<td>" + obj.RegionCode + "</td>";
					strContent += "<td>" + obj.SortKey + "</td>";
					strContent += "<td>" + obj.Reserved1 + "</td>";
					strContent += "<td>" + obj.Reserved2 + "</td>";
					strContent += "<td>" + obj.Reserved3 + "</td>";
					strContent += "<td>" + obj.Reserved4 + "</td>";
					strContent += "<td>" + obj.Reserved5 + "</td></tr>";
					$("#divMonitering #divCompareAddjob table tbody").append(strContent);
				});
			},
			error : function(response, status, error) {
				CFN_ErrorAjax("/covicore/admin/orgmanage/selectCompareList.do", response, status, error);
			}
		});
	}
</script>