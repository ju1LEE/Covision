<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil" %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>  
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv='cache-control' content='no-cache'> 
	<meta http-equiv='expires' content='0'> 
	<meta http-equiv='pragma' content='no-cache'>
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/admin.css<%=resourceVersion%>" />
	<jsp:include page="/WEB-INF/views/cmmn/CoreInclude.jsp"></jsp:include>

	<style>
		/* div padding */
		.divpop_body {
			padding: 20px !important;
		}
		
		label {
			font-size: 13px;
		}
		
		.txt_red {
    		color: #e75c00;
		}
	</style>
	
</head>
<body>
	<div class="AXTabs">
		<div id="divTabTray" class="AXTabsTray" style="height:30px">
			<a id="deptDefaultSetTab" onclick="clickTab(this);" class="AXTab on" value="divDeptDefault"><spring:message code='Cache.lbl_SettingDefault' /></a> <!-- 기본 설정 -->
			<a id="deptAddSetTab" onclick="clickTab(this);" class="AXTab" value="divDeptAdd"><spring:message code='Cache.lbl_SettingAdditional' /></a> <!-- 추가 설정 -->
		</div>
	</div>
	<form>
		<div style="width:100%;" id="divDeptDefault" class="mt15">
			<table class="AXFormTable">
				<colgroup>	
					<col style="width: 20%">
					<col style="width: 30%">
					<col style="width: 20%">
					<col style="width: 30%">
				</colgroup>
				<tr>
					<th><spring:message code="Cache.lbl_GroupType"/></th>
					<td colspan="3">						
						<select id="selGroupType" name="selGroupType" class="AXSelect" disabled="disabled"></select>
						<label id="lblCreateDeptSchedule" for="chkCreateDeptSchedule" style="font-size: 13px;">
							<input type="checkbox" id="chkCreateDeptSchedule" name="chkCreateDeptSchedule"><spring:message code="Cache.lbl_OrganizationDeptScheduleCreate"/>
						</label>
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_DeptCode"/><span class="txt_red">*</span></th>
					<td colspan="3">
						<label id="txtPfx"></label>
						<input type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" id="txtDeptCode">
						<input type="button" class="AXButton" value="<spring:message code="Cache.lbl_DuplicateCheck"/>" onclick="checkDuplicate();" id="btnIsDuplicate">
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_DeptName"/><span class="txt_red">*</span></th>
					<td colspan="3">
						<input type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" id="txtDeptName" onchange="resetDicData(this);">
						<input type="button" class="AXButton" value="<spring:message code="Cache.lbl_MultiLangSet"/>" onclick="dictionaryLayerPopup('true', 'txtDeptName');">
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_Organization_ShortName"/><span class="txt_red">*</span></th>
					<td colspan="3">
						<input type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" id="txtDeptShortName" onchange="resetDicData(this);">
						<input type="button" class="AXButton" value="<spring:message code="Cache.lbl_MultiLangSet"/>" onclick="dictionaryLayerPopup('true', 'txtDeptShortName');">
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_CompanyName"/></th>
					<td colspan="3">
						<input type="text" class="AXInput" id="txtCompanyName" readonly="readonly">
						<input type="text" id="hidCompanyCode" style="display: none;">
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_ParentDeptName"/></th>
					<td colspan="3">
						<input type="text" class="AXInput" id="txtParentDeptName" readonly="readonly">
						<input type="text" id="hidParentDeptCode" style="display: none;">
						<input type="button" class="AXButton" value="<spring:message code="Cache.btn_Select"/>" onclick="OrgMap_Open('dept');" style="min-width: 20px;">
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_IsUse"/><span class="txt_red">*</span></th>
					<td>
						<select id="selIsUse" name="selIsUse" class="AXSelect" style="width: 50%;">
							<option value="Y" selected><spring:message code="Cache.lbl_UseY"/></option>
							<option value="N"><spring:message code="Cache.lbl_noUse"/></option>
						</select>
					</td>
					<th><spring:message code="Cache.lbl_IsHR"/><span class="txt_red">*</span></th>
					<td>
						<select id="selIsHR" name="selIsHR" class="AXSelect" style="width: 50%;">
							<option value="Y"><spring:message code="Cache.lbl_UseY"/></option>
							<option value="N" selected><spring:message code="Cache.lbl_noUse"/></option>
						</select>
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_IsDisplay"/><span class="txt_red">*</span></th>
					<td>
						<select id="selIsDisplay" name="selIsDisplay" class="AXSelect" style="width: 50%;">
							<option value="Y" selected><spring:message code="Cache.lbl_Display"/></option>
							<option value="N"><spring:message code="Cache.lbl_noDisplay"/></option>
						</select>
					</td>
					<th><spring:message code="Cache.lbl_PriorityOrder"/><span class="txt_red">*</span></th>
					<td>
						<input type="text" class="AXInput" id="txtPriorityOrder" style="width: 30%;" onkeyup='writeNum(this);'>
						<input type="hidden" class="AXInput" id="hidOldPriorityOrder">
						<input type="button" class="AXButton" style="display: none;" value="<spring:message code="Cache.lbl_DuplicateCheck"/>" onclick="checkDuplicateSortKey();" id="btnIsDuplicateSortKey">
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_ApprovalDept"/></th>
					<td>
						<select id="selIsApprovalDept" name="selIsApprovalDept" class="AXSelect" style="width: 50%;">
							<option value="1" selected><spring:message code="Cache.lbl_UseY"/></option>
							<option value="0"><spring:message code="Cache.lbl_noUse"/></option>
						</select>
					</td>
					<th>결재수신여부</th> <!-- <spring:message code="Cache.lbl_IsApprovalReceive"/> -->
					<td>
						<select id="selIsApprovalReceive" name="selIsApprovalReceive" class="AXSelect" style="width: 50%;">
							<option value="1" selected><spring:message code="Cache.lbl_UseY"/></option>
							<option value="0"><spring:message code="Cache.lbl_noUse"/></option>
						</select>
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lblAdminSetting"/></th>
					<td colspan="3">
						<input type="text" class="AXInput" id="txtAdminSetting" readonly="readonly">
						<input type="text" id="hidAdminSetting" style="display: none;">
						<input type="button" class="AXButton" value="<spring:message code="Cache.btn_Select"/>" onclick="OrgMap_Open('user');" style="min-width: 20px; margin-top:5px;">
						<input type="button" class="AXButton" value="<spring:message code="Cache.btn_init"/>" onclick="initManageCode();" style="min-width: 20px; margin-top:5px;">									
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_IsMail"/><span class="txt_red">*</span></th>
					<td id="tdIsMail" colspan="3">
						<select id="selIsMail" name="selIsMail" class="AXSelect">
							<option value="Y" selected><spring:message code="Cache.lbl_UseY"/></option>
							<option value="N"><spring:message code="Cache.lbl_noUse"/></option>
						</select>
						<input type="text" id="hidtxtUseMailConnect" style="display: none;">
					</td>
					<th id="thIsCRM" style="display:none"><spring:message code="Cache.lbl_IsCRM"/></th>
					<td id="tdIsCRM" style="display:none">
						<select id="selIsCRM" name="selIsCRM" class="AXSelect">
							<option value="Y"><spring:message code="Cache.lbl_UseY"/></option>
							<option value="N" selected><spring:message code="Cache.lbl_noUse"/></option>
						</select>
					</td>
				</tr>
				<tr id="trIndiMail">
					<th><spring:message code="Cache.lbl_Mail"/></th><!-- 메일(인디) -->
					<td colspan='3'>
						<input type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" id="txtMailID" style="width: 100px;"/> <label>@</label> 
						<select id="selMailDomain" name="selMailDomain" class="AXSelect" onchange="fnCPMailUse(this)">
							<option value="" selected><spring:message code="Cache.lbl_Select" /></option>
						</select>
						<input type="text" id="hidtxtMailID" style="display: none;">
						<input type="button" class="AXButton" value="<spring:message code="Cache.lbl_DuplicateCheck"/>" onclick="checkDuplicateMail(mode, 'Dept');" id="btnIsDuplicateMail">						
					</td>
				</tr>
				<tr>
					<th><spring:message code="Cache.lbl_Description"/></th>
					<td colspan="3">
						<textarea id="txtDescription" class="AXTextarea HtmlCheckXSS ScriptCheckXSS" rows="8" style="width: 98%;"></textarea>
					</td>
				</tr>
			</table>
		</div>
		<div style="width:100%;display: none;" id="divDeptAdd" class="mt15">
			<table class="AXFormTable">
				<colgroup>
					<col style="width: 20%;">
					<col style="width: 80%;">
				</colgroup>
				<tr>
					<th rowspan="3"><spring:message code='Cache.lbl_Mail_Address' /></th>
					<td>
						<div id="topitembar_2" class="">
							<label>
								<input type="button" value="<spring:message code="Cache.btn_Add"/>" onclick="btnMailAdd_OnClick();" class="usa" /> <!-- 추가 -->
								<input type="button" value="<spring:message code="Cache.btn_delete"/>" onclick="btnMailDel_OnClick();"class="usa"/> <!-- 삭제 -->
								<input type="button" value="▲ <spring:message code="Cache.btn_UP"/>" onclick="btnMailUp_OnClick();"class="usa"/> <!-- 위로 -->
								<input type="button" value="▼ <spring:message code="Cache.btn_Down"/>" onclick="btnMailDown_OnClick();"class="usa"/> <!-- 아래로 -->
								<input type="hidden" ID="hidMailAttribute" />
								<input type="hidden" ID="hidMailAttribute1" />
								<input type="hidden" ID="hidMailAttribute2" />
								<input type="hidden" ID="hidMailAttribute3" />
								<input type="hidden" ID="hidMailAttribute4" />
								<input type="hidden" ID="hidMailAttribute5" />
								<input type="hidden" ID="hidMailAttribute6" />
								<input type="hidden" ID="hidMailAttribute7" />
								<input type="hidden" ID="hidMailAttribute8" />
								<input type="hidden" ID="hidMailAttribute9" />
								<input type="hidden" ID="hidMailAttribute10" />
								<input type="hidden" ID="hidMailAttribute11" />
								<input type="hidden" ID="hidMailAttribute12" />
								<input type="hidden" ID="hidMailAttribute13" />
								<input type="hidden" ID="hidMailAttribute14" />
								<input type="hidden" ID="hidMailAttribute15" />
							</label>
							<input type="hidden" ID="hidMailAddressInfo" />
						</div>
						<div id="divAttributeList" class="" style="height: 85px;overflow-y: scroll;"></div>
					</td>
				</tr>
				<tr>
				</tr>
			</table>
		</div>
		<div style="width: 100%; text-align: center; margin-top: 10px;">
			<input type="button" id="btnSave" value="<spring:message code='Cache.btn_save' />" onclick="return CheckValidation();" class="AXButton red" style="display: none;"> <!-- 저장 -->
			<input type="button" id="btnClose" value="<spring:message code='Cache.btn_Close' />" onclick="closePopup();" class="AXButton" > <!-- 닫기 -->
		</div>
	</form>
<input type="text" id="DeptNameHidden" value="" style="display:none;"/>
<input type="text" id="DeptShortNameHidden" value="" style="display:none;"/>
<input type="hidden" id="hidPrimary"  />
<input type="hidden" id="hidSecondary"  />
</body>

<script type="text/javascript">

	var gr_code = "${GR_Code}";
	var memberOf = "${MemberOf}";
	var domainCode = "${DomainCode}";
	var mode = "${mode}";
	var groupType = "${GroupType}";

	// 개별호출 일괄처리
	var sessionObj = Common.getSession();
	//Common.getBaseCodeList(["MailDomain"]);
	//Common.getBaseConfigList(["IsSyncMail","IsSyncIndi","ScheduleTotalFolderID","FolderDefaultColor","DefaultSecurityLevel"]);
	
	var isSyncMail = Common.getBaseConfig('IsSyncMail');
	var isSyncIndi = Common.getBaseConfig('IsSyncIndi');
	var IsUseCRM = Common.getBaseConfig('IsUseCRM');
	
	$(document).ready(function(){
		if(mode == "add"){
			setDefaultInfo();
	//		if(Common.getGlobalProperties("isSaaS") == "Y") {
	//			$("#txtMailID").attr("readonly", "readonly");
	//			$("#selMailDomain").attr("disabled","disabled");
	//		} else {
 	//			$("#selIsMail").on("change", function() {
	//				fnMailUse(this);
	//			}); 
	//		}
		}
		
		if(mode == "modify"){
			getDeptInfoData(gr_code);
		}
		
		setSelectGroupType();
		
		//기초설정에 따른 메일 표기 여부
		if(isSyncIndi == "Y"){
			$("#deptAddSetTab").css("display","none");
		}
		else if(isSyncMail == "Y"){
			$("#trIndiMail").css("display", "none");
		}
		else {
			$("#deptAddSetTab").css("display","none");
			var trMail = $("#selIsMail").closest('tr');
			trMail.children('td:first').attr('colspan', '3');
			trMail.children('th:nth-of-type(2)').css("display","none");
			trMail.children('td:nth-of-type(2)').css("display","none");
		}

		if(IsUseCRM == "Y"){
			$("#tdIsMail").attr("colspan", "");
			$("#thIsCRM").css("display", "");
			$("#tdIsCRM").css("display", "");
		}
		else{
			$("#tdIsMail").attr("colspan", "3");
			$("#thIsCRM").css("display", "none");
			$("#tdIsCRM").css("display", "none");
		}
		
		$(Common.getBaseCode('MailDomain').CacheData).each(function(idx, obj) {
			if (obj.Code == 'MailDomain')
				return true;
			$('#selMailDomain').append($('<option>', { 
				value: obj.CodeName,
				text: obj.CodeName
			}));
		});
	});
	
	function setDefaultInfo() {			
		$.ajax({
			type: "POST",
			data: {
				"memberOf": memberOf,
				"domainCode": domainCode
			},
			url: "/covicore/admin/orgmanage/getdefaultsetinfo.do",
			success: function (data) {
				$("#txtCompanyName").val(data.list[0].CompanyName);
				$("#hidCompanyCode").val(domainCode);
				$("#txtParentDeptName").val(data.list[0].DeptName);
				$("#hidParentDeptCode").val(memberOf);
				
				if(Common.getGlobalProperties("isSaaS") == "Y") {
					$("#txtPfx").text(domainCode + "_");
					//$("#selMailDomain").val(data.list[0].CompanyURL);
				}
				
				$("#selIsMail").on("change", function() {
					fnMailUse(this);
				});
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/covicore/admin/orgmanage/getdefaultsetinfo.do", response, status, error);
			}
		});
	}
	
	function setSelectGroupType() {
		$("#selGroupType").bindSelect({
			reserveKeys: {
				options: "list",
				optionValue: "GroupType",
				optionText: "DisplayName"
			},
			ajaxUrl: "/covicore/admin/orgmanage/getgrouptype.do",			
			ajaxAsync: false,
			onchange: function(){}
		});
		
		if(groupType == "Company" && mode == "modify") {
			$("#selGroupType").bindSelectSetValue('Company');
			$("#btnSave").hide();
			$("input[type=text]").attr('readonly',true)
			$("select").attr('disabled',true)
			$("input[type=button][id!=btnClose]").hide()
		} else {
			$("#selGroupType").bindSelectSetValue('Dept');
			$("#btnSave").show();
		}
		$("#selGroupType").bindSelectDisabled(true);
	}

	var sFieldName ="";
	function dictionaryLayerPopup(hasTransBtn, dicCallback) {
		sFieldName = dicCallback;
		
		var option = {
				lang : 'ko',
				hasTransBtn : hasTransBtn,
				allowedLang : 'ko,en,ja,zh',
				useShort : 'false',
				dicCallback : dicCallback,
				popupTargetID : 'DictionaryPopup',
				init : 'initDicPopup'
		};
		
		var url = "";
		url += "/covicore/control/calldic.do?lang=" + option.lang;
		url += "&hasTransBtn=" + option.hasTransBtn;
		url += "&useShort=" + option.useShort;
		url += "&dicCallback=" + option.dicCallback;
		url += "&allowedLang=" + option.allowedLang;
		url += "&popupTargetID=" + option.popupTargetID;
		url += "&init=" + option.init;
		
		//CFN_OpenWindow(url,"다국어 지정",500,300,"");
		Common.open("", "DictionaryPopup", "<spring:message code='Cache.lbl_MultiLangSet'/>", url, "400px", "200px", "iframe", true, null, null, true);
	}
	function initDicPopup(){
		var sHiddenName = sFieldName == "txtDeptName" ? "DeptNameHidden" : "DeptShortNameHidden";
		if(sFieldName==''){
			return "";
		}
		return $("#"+sHiddenName).val()==''? $("#"+sFieldName).val() : $("#"+sHiddenName).val();
	}
	function txtDeptName(data){
		$('#txtDeptName').val(data.KoFull);
		$('#DeptNameHidden').val(coviDic.convertDic(data))
	}
	function txtDeptShortName(data){
		$('#txtDeptShortName').val(data.KoFull);
		$('#DeptShortNameHidden').val(coviDic.convertDic(data))
	}

	// 다국어 값 바인드 및 처리
	parent._CallBackMethod = setDictionaryData;
	function setDictionaryData(nameValue){
		// nameValeu : ko;en;ja;zh;reserved1;reserved2;;;;;
		var koData = nameValue.split(';')[0];
		if(koData == $("#txtDeptName").val())
			$("#DeptNameHidden").val(nameValue);
		if(koData == $("#txtDeptShortName").val())
			$("#DeptShortNameHidden").val(nameValue);
	}
	
	// 해당 값이 바뀌었을 경우 이전 다국어 데이터를 지움
	function resetDicData(thisObj){
		if($(thisObj)[0].id == "txtDeptName"){
			$("#DeptNameHidden").val("");
		}else if($(thisObj)[0].id == "txtDeptShortName"){
			$("#DeptShortNameHidden").val("");
		}
	}
	
	function clickTab(pObj){
		$("#divTabTray .AXTab").attr("class","AXTab");
		$(pObj).addClass("AXTab on");
		
		var str = $(pObj).attr("value");
		
		$("#divDeptDefault").hide();
		$("#divDeptAdd").hide();
		
		$("#" + str).show();
	}
	
	function closePopup() {
		Common.Close();
	}
	
	function getDeptInfoData(gr_code) {
		var lang = sessionObj["lang"];
		$.ajax({
			type:"POST",
			data:{
				"gr_code" : gr_code
			},
			url:"/covicore/admin/orgmanage/getdeptinfo.do",
			success:function (data) {
				$("#txtDeptCode").val(data.list[0].GroupCode);
				$("#txtDeptName").val(data.list[0].DisplayName);
				$("#DeptNameHidden").val(data.list[0].MultiDisplayName);
				$("#txtDeptShortName").val(data.list[0].ShortName);
				$("#DeptShortNameHidden").val(data.list[0].MultiShortName);
				$("#txtCompanyName").val(CFN_GetDicInfo(data.list[0].CompanyName, lang));
				$("#hidCompanyCode").val(data.list[0].CompanyCode);
				$("#txtParentDeptName").val(CFN_GetDicInfo(data.list[0].ParentName, lang));
				$("#hidParentDeptCode").val(data.list[0].MemberOf);
				$("#selIsUse").val(data.list[0].IsUse == "" ? "N" : data.list[0].IsUse);
				$("#selIsHR").val(data.list[0].IsHR == "" ? "N" : data.list[0].IsHR);
				$("#selIsDisplay").val(data.list[0].IsDisplay == "" ? "N" : data.list[0].IsDisplay);
				$("#txtPriorityOrder").val(data.list[0].SortKey);
				$("#hidOldPriorityOrder").val(data.list[0].SortKey);
				$("#selIsApprovalDept").val(data.list[0].Approvable);
				$("#selIsApprovalReceive").val(data.list[0].Receivable);
				$("#txtAdminSetting").val(data.list[0].Manager.split('|')[1]);
				$("#hidAdminSetting").val(data.list[0].Manager.split('|')[0]);					
				$("#selIsMail").val(data.list[0].IsMail == "" ? "N" : data.list[0].IsMail);
				$("#hidtxtUseMailConnect").val(data.list[0].IsMail == "" ? "N" : data.list[0].IsMail);
				$("#selIsCRM").val(data.list[0].IsCRM == "" ? "N" : data.list[0].IsCRM);
				
				check_dupsortkey = "Y";
				var txtPrimaryMail = data.list[0].PrimaryMail;
				if(txtPrimaryMail != ""){
					$("#txtMailID").val(txtPrimaryMail.split("@")[0]);
					$("#selMailDomain").val(txtPrimaryMail.split("@")[1]);
					$("#hidtxtMailID").val(txtPrimaryMail);
					
					$("#txtMailID").attr("readonly", "readonly");
					$("#selMailDomain").attr("disabled","disabled");
				}

				$("#txtDescription").val(data.list[0].Description); //data.list[0].Description
				
				if(data.list[0].PrimaryMail != ""){
					$("#hidMailAddressInfo").val(data.list[0].PrimaryMail);	
				}
				if(data.list[0].SecondaryMail != ""){
					$("#hidMailAddressInfo").val($("#hidMailAddressInfo").val() + "," + data.list[0].SecondaryMail);
				}
				
                if ($("#hidMailAddressInfo").val() != "" && isSyncMail == "Y") {
                    var sMailAttributeInfo = $("#hidMailAddressInfo").val();
                    if (sMailAttributeInfo.indexOf(';') != -1) {
                        sMailAttributeInfo = sMailAttributeInfo.replace(';', ',');
                    }
                    var sAttributeID = "";
                    var sMailID = "";
                    var sXML = "<Attribute>";
                    if (sMailAttributeInfo.indexOf(',') != -1) {
                        var aMailAttributeInfo = sMailAttributeInfo.split(',');
                        for (var i = 0; i < aMailAttributeInfo.length; i++) {
                            if (aMailAttributeInfo[i].indexOf('|') != -1) {
                                var aMailAttributeInfo_Secondary = aMailAttributeInfo[i].split('|');
                                for (var j = 0; j < aMailAttributeInfo_Secondary.length; j++) {
                                    sXML += "<Mail><![CDATA[" + aMailAttributeInfo_Secondary[j] + "]]></Mail>";
                                    sXML += "<AttributeID><![CDATA[" + aMailAttributeInfo_Secondary[j] + "]]></AttributeID>";
                                }
                            } else {
                                sXML += "<Mail><![CDATA[" + aMailAttributeInfo[i] + "]]></Mail>";
                                sXML += "<AttributeID><![CDATA[" + sAttributeID + "]]></AttributeID>";
                            }
                        }

                    } else {
                        sXML += "<Mail><![CDATA[" + sMailAttributeInfo + "]]></Mail>";
                        sXML += "<AttributeID><![CDATA[" + sAttributeID + "]]></AttributeID>";
                    }
                    sXML += "</Attribute>";
                    btnAttributeAdd_OnClick_After(sXML);
                }
                
                $("#btnIsDuplicate").css("display", "none");
    			$("#txtDeptCode").attr("readonly", "readonly");
    			$("#lblCreateDeptSchedule").css("display", "none");
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/covicore/admin/orgmanage/getdeptinfo.do", response, status, error);
			}
		});
	}
	
	var check_dup = "N";
	
	function checkDuplicate() {
		if (!XFN_ValidationCheckOnlyXSS(false)) { return; }
		
		var stringRegx = /[\{\}\[\]?.,;:|\)*~`!^\-+┼<>@\#$%&\\(\=\'\"]/;
		var stringRegx2 = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;

		if("${mode}" == "add") {
			var groupCode = $('#txtDeptCode').val();
			if(groupCode == "") {
				Common.Warning("<spring:message code='Cache.msg_GRCODE_01'/>");
			} else{	
				if(stringRegx.test($("#txtDeptCode").val())) {
					Common.Warning("<spring:message code='Cache.msg_specialNotAllowed'/>", 'Warning Dialog', function() {
						$("#txtDeptCode").focus();
					});
					return false;
				} else if(stringRegx2.test($("#txtDeptCode").val())) {
					Common.Warning("<spring:message code='Cache.msg_KoreanNotAllowed'/>", 'Warning Dialog', function() {
						$("#txtDeptCode").focus();
					});
					return false;
				} 
				
				$.ajax({
					type:"POST",
					data:{
						"groupCode" : groupCode
					},
					url:"/covicore/admin/orgmanage/getisduplicatedeptcode.do",
					success:function (data) {
						if(data.status != "FAIL") {
							if(data.list[0].isDuplicate > 0) {
								Common.Warning("<spring:message code='Cache.msg_EXIST_GRCODE'/>");
								$('#txtDeptCode').focus();
								check_dup = "N";
							} else {
								Common.Inform("<spring:message code='Cache.msg_Not_Duplicate'/>");
								check_dup = "Y";
								
								if(Common.getGlobalProperties("isSaaS") == "Y") {
									$("#txtMailID").val(groupCode);
								}
							}
						} else {
							Common.Warning(data.message);
							check_dup = "N";
						}
					},
					error:function(response, status, error){
						CFN_ErrorAjax("/covicore/admin/orgmanage/getisdulicatedeptcode.do", response, status, error);
					}
				});
			}
		}
	}
	
	//중복 확인-메일
	var check_dupMail = "N";
	
	//우선순위 중복 체크
	var check_dupsortkey = "N";
	function checkDuplicateSortKey(){
		var strSortKey =$("#txtPriorityOrder").val();
		var strGroupCode = $("#txtDeptCode").val();
		var strMemberOf = $("#hidParentDeptCode").val();
		if(strSortKey == "") {
			Common.Warning("<spring:message code='Cache.msg_EnterPriorityNumber' />"); //우선순위를 입력하세요.
		} else if ("${mode}" != "add" && $("#hidOldPriorityOrder").val() == $("#txtPriorityOrder").val()){
			Common.Inform("<spring:message code='Cache.msg_Not_Duplicate'/>");
			check_dupsortkey = "Y";
		} else{
			$.ajax({
				type:"POST",
				data:{
					"groupCode" : strGroupCode,
					"SortKey" : strSortKey,
					"MemberOf" : strMemberOf
				},
				url:"/covicore/admin/orgmanage/getisduplicatesortkey.do",
				success:function (data) {
					if(data.status != "FAIL") {
						if(data.object.list[0].isDuplicate > 0) {
							Common.Warning("<spring:message code='Cache.msg_EXIST_GRCODE'/> MaxSortKey : " + data.object.list[0].MaxSortKey);
							$('#txtPriorityOrder').focus();
							check_dupsortkey = "N";
						} else {
							Common.Inform("<spring:message code='Cache.msg_Not_Duplicate'/>");
							check_dupsortkey = "Y";
						}
					} else {
						Common.Warning(data.message);
						check_dupsortkey = "N";
					}
				},
				error:function(response, status, error){
					CFN_ErrorAjax("/covicore/admin/orgmanage/getisduplicatesortkey.do", response, status, error);
				}
			});
		}
	}
	
	function OrgMap_Open(type){
		if(type == "user") {
			parent.Common.open("","orgchart_pop","<spring:message code='Cache.btn_OrgManage'/>" ,"/covicore/control/goOrgChart.do?callBackFunc=_CallBackMethod3&openerID=divDeptInfo&type=A1&companyCode="+domainCode,"540px", "580px", "iframe", true, null, null,true);
		} else if(type == "dept") {
			parent.Common.open("","orgchart_pop","<spring:message code='Cache.btn_OrgManage'/>" ,"/covicore/control/goOrgChart.do?callBackFunc=_CallBackMethod2&openerID=divDeptInfo&type=C1&companyCode="+domainCode,"1040px", "580px", "iframe", true, null, null,true);
		}
	}
	
	function _CallBackMethod2(data){ //조직도 콜백함수 구현 : 부서(parent에 정의)
		var jsonData = JSON.parse(data);
		$("#txtParentDeptName").val(CFN_GetDicInfo(jsonData.item[0].DN));
		$("#hidParentDeptCode").val(jsonData.item[0].AN);
		check_dupsortkey = "N";
	}
	
	function _CallBackMethod3(data){ //조직도 콜백함수 구현 : 사용자(parent에 정의)
		var jsonData = JSON.parse(data);
		
		$("#txtAdminSetting").val(CFN_GetDicInfo(jsonData.item[0].DN));
		$("#hidAdminSetting").val(jsonData.item[0].AN);
	}
	
	function CheckValidation() {
		var stringRegx = /[\{\}\[\]?.,;:|*~`!^\-+┼<>@\#$%&\=\'\"]/;

		if (!XFN_ValidationCheckOnlyXSS(false)) { return; }

		if($("#txtDeptCode").val() == "") {
			Common.Warning("<spring:message code='Cache.msg_GRCODE_01' />", 'Warning Dialog', function() {  //부서코드를 입력하세요.
				$("#txtDeptCode").focus();
			});
			return false;
		} else if("${mode}" == "add" && check_dup == "N") {
			Common.Warning("<spring:message code='Cache.msg_BaseCode_04' />"); //코드 중복을 확인하여 주십시오.
			return false;
		} else if($("#txtDeptName").val() == "") {
			Common.Warning("<spring:message code='Cache.msg_GRNAME_01' />", 'Warning Dialog', function() { //부서명을 입력하세요.
				$("#txtDeptName").focus();
			});
			return false;
		} else if(stringRegx.test($("#txtDeptName").val())) {
			Common.Warning("<spring:message code='Cache.msg_specialNotAllowed'/>", 'Warning Dialog', function() { //부서명을  입력하세요.
				$("#txtDeptName").focus();
			});
			return false;
		} else if($("#DeptNameHidden").val() == "") {
			$("#DeptNameHidden").val($("#txtDeptName").val() + ';;;;;;;;;;');
/* 			Common.Warning("<spring:message code='Cache.msg_GRNAME_02'/>", 'Warning Dialog', function() { //부서명의 다국어 처리를 변경하십시오.
				$("#txtDeptName").focus();
			});
			return false; */
		}
		
		if($("#txtDeptShortName").val() == "") {
			Common.Warning("<spring:message code='Cache.msg_ShortName_Req' />", 'Warning Dialog', function() { //간략명칭을 입력하세요.
				$("#txtDeptShortName").focus();
			});
			return false;
		} else if($("#DeptShortNameHidden").val() == "") {
			$("#DeptShortNameHidden").val($("#txtDeptShortName").val() + ';;;;;;;;;;');
			/* Common.Warning("<spring:message code='Cache.msg_GRNAME_03' />", 'Warning Dialog', function() { //간략명칭의 다국어 처리를 변경하십시오.
				$("#txtDeptShortName").focus();
			});
			return false; */
		}
		
		if($("#txtPriorityOrder").val() == "") {
			Common.Warning("<spring:message code='Cache.msg_EnterPriorityNumber' />", 'Warning Dialog', function() { //우선순위를 입력하세요.
				$("#txtPriorityOrder").focus();
			});
			return false;
		} /* else if("${mode}" == "add" && check_dup == "N") {
			Common.Warning("SortKey<spring:message code='Cache.msg_BaseCode_04' />"); //SortKey 코드 중복을 확인하여 주십시오.
			return false;
		} else if("${mode}" != "add" && (check_dupsortkey == "N" || (check_dupsortkey == "Y" && $("#hidOldPriorityOrder").val() != $("#txtPriorityOrder").val()))){
			Common.Warning("SortKey<spring:message code='Cache.msg_BaseCode_04' />"); //SortKey 코드 중복을 확인하여 주십시오.
			return false;
		} */

		if ($("#selIsMail").val() == "Y"){
			if(isSyncMail == 'Y') {
				//Exch 메일 사용
			} 
			 else if(isSyncIndi == "Y" && $("#selIsMail option:selected").val() == "Y"){
				if($("#txtMailID").val() == "" && $("#selMailDomain option:selected").val() == "") {
					Common.Warning("<spring:message code='Cache.msg_insert_MailAddress'/>", 'Warning Dialog', function() { // 메일주소를 입력해 주십시오.
						$("#selMailDomain").focus();
					});
					return false;
				} else if($("#txtMailID").val() != "" && $("#selMailDomain option:selected").val() == "") {
					Common.Warning("<spring:message code='Cache.msg_select_MailDomain'/>", 'Warning Dialog', function() { // 메일 도메인을 선택해 주십시오.
						$("#selMailDomain").focus();
					});
					return false;
				}else if ($("#txtMailID").val() == "" && $("#selMailDomain option:selected").val() != "") {
					Common.Warning("<spring:message code='Cache.msg_insert_MailAddress'/>", 'Warning Dialog', function() { // 메일주소를 입력해 주십시오.
						$("#txtMailID").focus();
					});
					return false;
				}else if ($("#txtMailID").val() != "" && $("#selMailDomain option:selected").val() != "") {
					if("${mode}" == "add" && check_dupMail == "N") {
						Common.Warning("<spring:message code='Cache.msg_Check_Duplicate_Mail'/>", 'Warning Dialog', function() { // 메일 중복을 확인하여 주십시오.
							$("#txtMailID").focus();
						});
						return false;
					}else if("${mode}" == "modify" && $("#hidtxtMailID").val() != $("#txtMailID").val() + "@" + $("#selMailDomain option:selected").val() && check_dupMail == "N") {
						Common.Warning("<spring:message code='Cache.msg_Check_Duplicate_Mail'/>", 'Warning Dialog', function() { // 메일 중복을 확인하여 주십시오.
							$("#txtMailID").focus();
						});
						return false;
					}else if("${mode}" == "modify" && $("#hidtxtUseMailConnect").val() != $("#selIsMail").val() && check_dupMail == "N") {
						Common.Warning("<spring:message code='Cache.msg_Check_Duplicate_Mail'/>", 'Warning Dialog', function() { // 메일 중복을 확인하여 주십시오.
							$("#txtMailID").focus();
						});
						return false;
					}
				}
			} else {
				if($("#txtMailID").val() != "" && $("#selMailDomain option:selected").val() == "") {
					Common.Warning("<spring:message code='Cache.msg_select_MailDomain'/>", 'Warning Dialog', function() { // 메일 도메인을 선택해 주십시오.
						$("#selMailDomain").focus();
					});
					return false;
				}else if ($("#txtMailID").val() == "" && $("#selMailDomain option:selected").val() != "") {
					Common.Warning("<spring:message code='Cache.msg_insert_MailAddress'/>", 'Warning Dialog', function() { // 메일주소를 입력해 주십시오.
						$("#txtMailID").focus();
					});
					return false;
				}else if ($("#txtMailID").val() != "" && $("#selMailDomain option:selected").val() != "") {
					if("${mode}" == "add" && check_dupMail == "N") {
						Common.Warning("<spring:message code='Cache.msg_Check_Duplicate_Mail'/>", 'Warning Dialog', function() { // 메일 중복을 확인하여 주십시오.
							$("#txtMailID").focus();
						});
						return false;
					}else if("${mode}" == "modify" && $("#hidtxtMailID").val() != $("#txtMailID").val() + "@" + $("#selMailDomain option:selected").val() && check_dupMail == "N") {
						Common.Warning("<spring:message code='Cache.msg_Check_Duplicate_Mail'/>", 'Warning Dialog', function() { // 메일 중복을 확인하여 주십시오.
							$("#txtMailID").focus();
						});
						return false;
					}
				}
			}
		}
		
		//추가속성 :: Primary, Secondary값 HiddenField에 저장합니다.
		var aObjectTR = $("#divAttributeList > TABLE > TBODY > TR");
		var nLength = aObjectTR.length;
		for (var i = 1; i < nLength; i++) {
			if (i == nLength - 1) {
				$("#hidSecondary").val($("#hidSecondary").val() +aObjectTR.filter(":eq(" + i.toString() + ")").text());
			} else {
				$("#hidSecondary").val($("#hidSecondary").val() + aObjectTR.filter(":eq(" + i.toString() + ")").text() + "|");
			}
		}
		$("#hidPrimary").val(aObjectTR.filter(":eq(0)").text());

		if (isSyncMail == "Y") { //[2012-09-14 Modi] 기초 설정 메일 연동 여부에 따른 필수값 패스
			if ($("#selUseMail").val() == "Y") {
				if (aObjectTR.filter(":eq(0)").text() == "") {
					parent.Common.Warning("<spring:message code='Cache.msg_37'/>", "Warning Dialog", function () {                       // 메일주소를 입력하여 주십시오.
						clickTab($("#deptAddSetTab"));
					});
					return false;
				}
			}
		}
		
		var now = new Date();
		now = XFN_getDateTimeString("yyyy-MM-dd HH:mm:ss",now);
		
		var url = "/covicore/admin/organization/insertdeptinfo.do";
		var message = "<spring:message code='Cache.msg_117'/>";
		if(mode == "modify") {
			url = "/covicore/admin/organization/updatedeptinfo.do";
			message = "<spring:message code='Cache.msg_137'/>"
		} 

		if(mode == "add"){
			if(Common.getGlobalProperties("isSaaS") == "Y") {
				if($("#txtPfx").text() != "") {
					var sDeptCode = $("#txtPfx").text() + $("#txtDeptCode").val();
					$("#txtDeptCode").val(sDeptCode);	
				}				
			}
		}
		
		$.ajax({
			type:"POST",
			data:{
				"GroupCode" : $("#txtDeptCode").val(),
				"DisplayName" : $("#txtDeptName").val(),
				"MultiDisplayName" : $("#DeptNameHidden").val(),
				"ShortName" : $("#txtDeptShortName").val(),
				"MultiShortName" : $("#DeptShortNameHidden").val(),
				"PrimaryMail" : $("#txtMailID").val() != "" ? $("#txtMailID").val() + "@" + $("#selMailDomain").val() : "",
				"CompanyCode" : $("#hidCompanyCode").val(),
				"MemberOf" : $("#hidParentDeptCode").val(),
				"GroupType" : $("#selGroupType option:selected").val(),
				"IsUse" : $("#selIsUse option:selected").val(),
				"IsHR" : $("#selIsHR option:selected").val(),
				"IsDisplay" : $("#selIsDisplay option:selected").val(),
				"IsMail" : $("#selIsMail option:selected").val(),
				"hidtxtUseMailConnect" : $("#hidtxtUseMailConnect").val(),
				"Approvable" : $("#selIsApprovalDept option:selected").val(),
				"Receivable" : $("#selIsApprovalReceive option:selected").val(),
				"SortKey" : $("#txtPriorityOrder").val(),
				"ManagerCode" : $("#hidAdminSetting").val(),
				"Description" : $("#txtDescription").val(),
				//"RegistID" : sessionObj["UR_Code"],
				"RegistDate" : now,
				"OUName" : $("#txtDeptName").val(),
				"RegionCode" : "",
				"SecondaryMail" : $("#hidSecondary").val(),
				"CompanyName" : $("#txtCompanyName").val(),
				"EX_PrimaryMail" : $("#hidPrimary").val(),
				"ChkDeptSchedule" : $('#chkCreateDeptSchedule').prop('checked') ? "Y" : "N"
			},
			url:url,
			success:function (data) {
				if(data.result == "ok" && data.status == "SUCCESS") {
					Common.Inform(message, "Information Dialog", function(result) {
						if(result) {
							parent.window.location.reload();
							Common.Close();
						}
					});
				} else {
					Common.Error(data.message, "Error", function(result) {
						if(result=="parentDeptError"){
							return false;
						}
						else if(result == "fail"){
							Common.Close();
						}
					});
				}
			},
			error:function(response, status, error){
				CFN_ErrorAjax(url, response, status, error);
			}
		});
		
	}
	
	var writeNum = function(obj) {
		// Validation Chk
		var inputKey = event.key;
		var inputBox = $(obj);
		var value = inputBox.val();
		
		
		if(_ie) {
			if(inputKey == "Decimal")
				inputKey = ".";
		}
		
		// 숫자 및 소수점이 아닌 문자 치환
		value = value.replace(/[^0-9.]/g, '');
		
		value = value == "" ? "0" : value;

		// 숫자형식으로 변경
		// 숫자형식이거나 event에 바인딩되어 넘어오지 않은경우
		if(inputKey != ".") {
			//반올림 첫번째자리까지			
			value = parseFloat(value);
			
			inputBox.val(value);
			
		} else if(inputKey == ".") {
			inputBox.val(value);
		}
		return false;
	};
	
	// 추가속성 추가버튼 클릭시 호출되며, 추가속성 추가를 위한 팝업을 화면에 표시합니다.
	function btnMailAdd_OnClick() {
		var sMailID = $("#txtDeptCode").val();
  	  	$("#hidMailAddressInfo").val("");
        ShowMailAttributeSetting(sMailID, "0", "Add");
	}

	// 추가 속성을 입력하는 화면을 표시합니다.
	function ShowMailAttributeSetting(sMail, nIndex, sMode) {
		var sOpenName = "divAttribute";
		var sURL = "/covicore/mailaddressattributepop.do";
		sURL += "?iframename=divDeptInfo";
        sURL += "&openname=" + sOpenName;
        sURL += "&mailaddress=hidMailAddressInfo";
        sURL += "&mail=" + sMail;
        sURL += "&index=" + nIndex;
        sURL += "&mode=" + sMode;
        sURL += "&callbackmethod=btnAttributeAdd_OnClick_After";
		
         var aStrDictionary = null;
         var sTitle = "";
         if ($("#hidMailAddressInfo").val() == "") {
             sTitle = "<spring:message code='Cache.lbl_ResourceManage_06'/>" + " ||| " + "<spring:message code='Cache.msg_ResourceManage_06'/>";
         }
         else {
             sTitle = "<spring:message code='Cache.lbl_ResourceManage_07'/>" + " ||| " + "<spring:message code='Cache.msg_ResourceManage_07'/>";
         }

		parent.Common.open("", "divAttribute", sTitle, sURL, "500px", "150px", "iframe", true, null, null, true);
	}

	// 추가 속성을 입력받은 후 호출되는 함수로, 선택된 추가 속성 정보를 화면에 표시합니다.
	function btnAttributeAdd_OnClick_After(pStrAttributeInfo) {
		var sHTML = "";

		if ((pStrAttributeInfo != null) && (pStrAttributeInfo != "")) {

			$($.parseXML(pStrAttributeInfo)).find("Attribute").find("Mail").each(function () {
				sHTML = "";

				var sMail = $(this).text();
				var sAttributeID = $(this).parent().find("AttributeID").text();
				var sIndex = $(this).parent().find("Index").text();
				var sMode = $(this).parent().find("Mode").text();

				var nIndex = $("#divAttributeList").children("TABLE").children("TBODY").children("TR").length;
				var oTR = $("#divAttributeList").children("TABLE").children("TBODY").children("TR");
				var oTD = $("#divAttributeList").children("TABLE").children("TBODY").children("TR").children("TD");

				if (sMode == "Add") {
					for (var i = 0; i < oTR.length; i++) {
						if ($(oTD[i]).children("A").text() == sMail) {
							Common.Warning("<spring:message code='Cache.lbl_EmailAddresSame.'/>" , "Warning Dialog");
							return;
						}
					}
				}
				else if (sMode == "Edit") {
					for (var i = 0; i < oTR.length; i++) {
						if ($(oTD[i]).children("A").text() == sMail) {
							if (sIndex != "0") {
								$(oTD[i]).children("A")[0].href = "javascript:ShowMailAttributeSetting('" + sMail + "', '" + i + "', 'Edit');"
								$(oTD[i]).children("A").text(sMail);
								$("TD[Index=" + sIndex + "]").remove();
								return;
							}
						}
					}
					if (sIndex != "") {
						$(oTD[sIndex]).children("A")[0].href = "javascript:ShowMailAttributeSetting('" + sMail + "', '" + sIndex + "', 'Edit');"
						$(oTD[sIndex]).children("A").text(sMail);
						return;
					}
				}

				if ($("#divAttributeList").children().length == 0) {
					sHTML += "<tr Mail=\"Primary\">";
					sHTML += "<td class=\"t_back01_line\" width=\"10\" Index='" + nIndex + "'>";
					sHTML += "<input id=\"chkAttribute_" + nIndex + "\" type=\"checkbox\" style=\"cursor: pointer;\" class=\"input_check\" />";
					sHTML += "<a href=\"javascript:ShowMailAttributeSetting('" + sMail + "', '" + nIndex + "', 'Edit');\" style=\"font-weight:bold\">";
					sHTML += sMail;
					sHTML += "</a>";
					sHTML += "</td>";
				}
				else {
					sHTML += "<tr Mail=\"Secondary\">";
					sHTML += "<td class=\"t_back01_line\" width=\"10\" Index='" + nIndex + "'>";
					sHTML += "<input id=\"chkAttribute_" + nIndex + "\" type=\"checkbox\" style=\"cursor: pointer;\" class=\"input_check\" />";
					sHTML += "<a href=\"javascript:ShowMailAttributeSetting('" + sMail + "', '" + nIndex + "', 'Edit');\" style=\"font-weight:\">";
					sHTML += sMail;
					sHTML += "</a>";
					sHTML += "</td>";
				}
				sHTML += "</tr>";

				if ($("#divAttributeList").children().length <= 0) {
					sHTML = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + sHTML + "</table>";
					$("#divAttributeList").append(sHTML);
				}
				else {
					$("#divAttributeList").children("TABLE").children("TBODY").append(sHTML);
				}
			});
		}
	}

	// 추가속성 삭제버튼 클릭시 호출되며, 선택된 추가속성을 삭제합니다.
	function btnMailDel_OnClick() {
		if ($("input[id^='chkAttribute_']:checked").length > 0) {
			Common.Confirm("<spring:message code='Cache.msg_Common_08' />", 'Confirmation Dialog', function (result) {       // 선택한 항목을 삭제하시겠습니까?
				if (result) {
					$("input[id^='chkAttribute_']:checked").each(function () {
						$(this).parent().parent().remove();
					});
				}
				if ($("#divAttributeList > TABLE > TBODY >TR").length == 0) {
					$("#divAttributeList").html("");
				}
			});
		}
		else {
			Common.Warning("<spring:message code='Cache.msg_Common_03' />", "Warning Dialog", function () { });      // 이동할 항목을 선택하여 주십시오.
			return;
		}
	}

	// 추가속성 위로버튼 클릭시 호출되며, 선택된 추가속성의 우선순위를 한단계 올립니다.
	function btnMailUp_OnClick() {
		var bSelected = false;
		$("input[id^='chkAttribute_']:checked").each(function () {
			bSelected = true;
		});

		if (!bSelected) {
			Common.Warning("<spring:message code='Cache.msg_Common_09' />", "Warning Dialog", function () { });      // 이동할 항목을 선택하여 주십시오.
			return;
		}

		var oPrevTR = null;
		var oNowTR = null;

		var oResult = null;
		var bSucces = true;
		var sResult = "";
		var sErrorMessage = "";

		var aObjectTR = $("#divAttributeList > TABLE > TBODY > TR");
		var nLength = aObjectTR.length;
		for (var i = 0; i < nLength; i++) {
			if (!aObjectTR.filter(":eq(" + i.toString() + ")").children("TD").children("INPUT").is(":checked")) {
				continue;
			}

			// 현재 행: 위에서부터 선택 되어 있는 행 찾기
			oNowTR = aObjectTR.filter(":eq(" + i.toString() + ")");

			// 이전 행 찾기: 현재 행 기준 위에서 선택 안되어 있는 행 찾기
			oPrevTR = null;
			for (var j = i - 1; j >= 0; j--) {
				if (aObjectTR.filter(":eq(" + j.toString() + ")").children("TD").children("INPUT").is(":checked")) {
					continue;
				}
				oPrevTR = aObjectTR.filter(":eq(" + j.toString() + ")");
				break;
			}
			if (oPrevTR == null) {
				continue;
			}

			oPrevTR.insertAfter(oNowTR);

			if (oPrevTR.attr("Mail") == "Primary") {
				oPrevTR.removeAttr("Mail");
				oPrevTR.attr("Mail", "Secondary");
				oNowTR.removeAttr("Mail");
				oNowTR.attr("Mail", "Primary");

				$(oNowTR).children("TD").children("a").css({ "font-weight": "bold" });
				$(oPrevTR).children("TD").children("a").css({ "font-weight": "" });
			}
		}
		if (sErrorMessage != "") {
			Common.Error(sErrorMessage, "Error Dialog", function () { });
		}
	}

	// 추가속성 아래로버튼 클릭시 호출되며, 선택된 추가속성의 우선순위를 한단계 내립니다.
	function btnMailDown_OnClick() {
		var bSelected = false;
		$("input[id^='chkAttribute_']:checked").each(function () {
			bSelected = true;
		});

		if (!bSelected) {
			Common.Warning("<spring:message code='Cache.msg_Common_09' />", "Warning Dialog", function () { });      // 이동할 항목을 선택하여 주십시오.
			return;
		}

		var oNextTR = null;
		var oNowTR = null;

		var oResult = null;
		var bSucces = true;
		var sResult = "";
		var sTemp = "";
		var sErrorMessage = "";

		var aObjectTR = $("#divAttributeList > TABLE > TBODY > TR");
		var nLength = aObjectTR.length;
		for (var i = nLength; i >= 0; i--) {
			if (!aObjectTR.filter(":eq(" + i.toString() + ")").children("TD").children("INPUT").is(":checked")) {
				continue;
			}

			// 현재 행: 아래에서부터 선택되어 있는 행 찾기
			oNowTR = aObjectTR.filter(":eq(" + i.toString() + ")");

			// 다음 행 찾기: 현재 행 기준 아래에서 선택 안되어 있는 행 찾기
			oNextTR = null;
			for (var j = i + 1; j < nLength; j++) {
				if (aObjectTR.filter(":eq(" + j.toString() + ")").children("TD").children("INPUT").is(":checked")) {
					continue;
				}
				oNextTR = aObjectTR.filter(":eq(" + j.toString() + ")");
				break;
			}
			if (oNextTR == null) {
				continue;
			}

			oNowTR.insertAfter(oNextTR);

			if (oNowTR.attr("Mail") == "Primary") {
				oNowTR.removeAttr("Mail");
				oNowTR.attr("Mail", "Secondary");
				oNextTR.removeAttr("Mail");
				oNextTR.attr("Mail", "Primary");

				$(oNextTR).children("TD").children("a").css({ "font-weight": "bold" });
				$(oNowTR).children("TD").children("a").css({ "font-weight": "" });
			}
		}
		if (sErrorMessage != "") {
			Common.Error(sErrorMessage, "Error Dialog", function () { });
		}
	}

	//추가속성 메일속성 값
	function btnMailAttributeAdd_OnClick() {
		 var sOpenName = "divMailAttribute";
	        var sURL = "/covicore/mailattributepop.do";
	        sURL += "?iframename=divDeptInfo";
	        sURL += "&openname=" + sOpenName;
	        sURL += "&attributes=hidMailAttribute";
	        sURL += "&callbackmethod=btnMailAttributeAdd_OnClick_After";

	        var aStrDictionary = null;
	        var sTitle = "";
	        if ($("#hidMailAddressInfo").val() == "") {
	            sTitle = "<spring:message code='Cache.lbl_ResourceManage_06'/>" + " ||| " + "<spring:message code='Cache.msg_ResourceManage_06'/>";
	        }
	        else {
	            sTitle = "<spring:message code='Cache.lbl_ResourceManage_07'/>" + " ||| " + "<spring:message code='Cache.msg_ResourceManage_07'/>";
	        }
	        parent.Common.open("", "divMailAttribute", sTitle, sURL, "380px", "550px", "iframe", true, null, null, true);
	}

	// 추가 속성 메일속성 값을 히든필드에 입력
	function btnMailAttributeAdd_OnClick_After(pStrAttributeInfo) {
        var sHTML = "";

        if ((pStrAttributeInfo != null) && (pStrAttributeInfo != "")) {
        	$("#hidMailAttribute").val(pStrAttributeInfo);
        	$("#hidMailAttribute1").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_1").text());
        	$("#hidMailAttribute2").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_2").text());
        	$("#hidMailAttribute3").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_3").text());
        	$("#hidMailAttribute4").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_4").text());
        	$("#hidMailAttribute5").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_5").text());
        	$("#hidMailAttribute6").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_6").text());
        	$("#hidMailAttribute7").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_7").text());
        	$("#hidMailAttribute8").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_8").text());
        	$("#hidMailAttribute9").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_9").text());
        	$("#hidMailAttribute10").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_10").text());
        	$("#hidMailAttribute11").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_11").text());
        	$("#hidMailAttribute12").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_12").text());
        	$("#hidMailAttribute13").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_13").text());
        	$("#hidMailAttribute14").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_14").text());
        	$("#hidMailAttribute15").val($($.parseXML(pStrAttributeInfo)).find("Attribute").find("Attribute_15").text());
        }
	}
	
	function fnMailUse(pObj) {
		if($("#selIsMail option:selected").val() == "N") {
    		if(isSyncIndi == "Y") {
	    		$("#txtMailID").attr("readonly", "readonly");
	    		$("#selMailDomain").attr("disabled","disabled");
    		} else if (isSyncMail == "Y") {
    			$("#deptAddSetTab").css("display","none");
    		}
    	}else {
    		if(isSyncIndi == "Y") {
	    		$("#txtMailID").removeAttr("readonly");
	    		$("#selMailDomain").removeAttr("disabled");
    		} else if(isSyncMail == "Y") {
    			$("#deptAddSetTab").css("display","");
    		}
    	}
    }
	
	function initManageCode() {
		$("#txtAdminSetting").val('');
		$("#hidAdminSetting").val('');
	}
</script>