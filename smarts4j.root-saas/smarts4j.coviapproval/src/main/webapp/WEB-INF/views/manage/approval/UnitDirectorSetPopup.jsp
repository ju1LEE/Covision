<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="covi" uri="/WEB-INF/tlds/covi.tld" %>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>
<script  type="text/javascript">
	var param = location.search.substring(1).split('&');
	var mode =param[0].split('=')[1];
	var paramEntCode =  param[1].split('=')[1];
	var paramUnitCode =  param[2].split('=')[1];
	var doublecheck = false;
	var flag = 2;  //falg 0=권한부서선택 falg 1=피권한부서추가

	$(document).ready(function(){
		$("#EntCode").val(paramEntCode);
		
		if(mode=="modify"){
			$("#btn_delete").show();
			modifySetData();
		}
		
		$(document).on("keyup", "input:text[InputMode]", function() {
			if($(this).attr('InputMode')=="Numberic"){
				$(this).val( $(this).val().replace(/[^0-9]/gi,"") );
				var max = parseInt($(this).attr('num_max'));
			    var min = parseInt($(this).attr('num_min'));
			    if ($(this).val() > max)
			    {
			        $(this).val(max);
			    }
			    else if ($(this).val() < min)
			    {
			        $(this).val(min);
			    }   
			}			
		});
	});
	
	// 피권한 부서 테이블 내 행 추가
	function addDataRow(pCode, pName, pViewStartDate, pViewEndDate) {
		if(pCode && pName) {
			var sHTML = "";
			
			sHTML += "<tr style=''>";
			sHTML += "	<td style='text-align: center; word-break: break-all;'><input type='checkbox' name='ckhalias' id='user_" + XFN_ReplaceAllSpecialChars(pCode) + "' value='" + pCode + "' class='input_check'></td>";
			sHTML += "	<td style='text-align: center; word-break: break-all;'><input type='hidden' name='hidden_UNIT_NAME' id='hidden_UNIT_NAME_" + XFN_ReplaceAllSpecialChars(pCode) + "' value='" + pName + "'><label for='user_" + XFN_ReplaceAllSpecialChars(pCode) + "'>" + CFN_GetDicInfo(pName) + "</label></td>";
			sHTML += "	<td style='text-align: center; word-break: break-all;'><span>" + pCode + "</span><input type='hidden' name='hidden_UNIT_CODE' id='hidden_UNIT_CODE_" + XFN_ReplaceAllSpecialChars(pCode) + "' value='" + pCode + "'></td>";
			sHTML += "	<td style='text-align: center;'><input type='text' id='SEARCH_START_"+ XFN_ReplaceAllSpecialChars(pCode) +"' style='width: 105px' class='' /><div style='width: 15px; display: inline-block; padding-top: 5px;'>~</div><input type='text' kind='twindate' date_startTargetID='SEARCH_START_" + XFN_ReplaceAllSpecialChars(pCode) + "' id='SEARCH_END_" + XFN_ReplaceAllSpecialChars(pCode) + "' style='width: 105px' class='' /></td>";
			sHTML += "</tr>";
			
			$("#MGRDEPTLIST").append(sHTML);
			
			coviInput.init();
			
			if(pViewStartDate && pViewEndDate) {
				$("#SEARCH_START_" + XFN_ReplaceAllSpecialChars(pCode)).val(isEmpty(pViewStartDate,"") == "0000-00-00 00:00:00" ? "" : getStringDateToString("yyyy-MM-dd", isEmpty(pViewStartDate, "")));
				$("#SEARCH_END_" + XFN_ReplaceAllSpecialChars(pCode)).val(isEmpty(pViewEndDate,"") == "0000-00-00 00:00:00" ? "" : getStringDateToString("yyyy-MM-dd", isEmpty(pViewEndDate, "")));
			}
		}
	}
	
	// 레이어 팝업 닫기
	function closeLayer(){
		Common.Close();
	}
		
	function isEmpty(value, defaultValue){
		return (value == null || value == undefined) ? defaultValue : value;
	}
	
	//수정화면 data셋팅
	function modifySetData(){
		//data 조회
		$.ajax({
			type:"POST",
			data:{
				"UnitCode" : paramUnitCode,
				"EntCode" : paramEntCode
			},
			url:"getUnitDirectorData.do",
			success:function (data) {	
				if(data.cnt>0){
					$("#DIS_APP_NAME").val(CFN_GetDicInfo(data.list[0].UnitName));
					$("#APP_NAME").val(data.list[0].UnitName);
					$("#UnitCode").val(data.list[0].UnitCode);				
					$("#AuthStartDate").val(data.list[0].AuthStartDate == "0000-00-00 00:00:00"?"":getStringDateToString("yyyy-MM-dd",data.list[0].AuthStartDate));				
					$("#AuthEndDate").val(data.list[0].AuthEndDate == "0000-00-00 00:00:00"?"":getStringDateToString("yyyy-MM-dd",data.list[0].AuthEndDate));				
													
					$("#MGRDEPTLIST").empty();	
					
					for(var i=0; i<data.list.length;i++){	
						addDataRow(data.list[i].TargetUnitCode, data.list[i].TargetUnitName, data.list[i].ViewStartDate, data.list[i].ViewEndDate);
					}
					$("#APP_DETAIL").val(data.list[0].Description);
					$("#APP_SORT").val(data.list[0].SortKey);
				}else{
					$("#AuthStartDate").val("");
					$("#AuthEndDate").val("");
					$("#MGRDEPTLIST").empty();
					$("#APP_DETAIL").val("");
					$("#APP_SORT").val("");
				}
				
			},
			error:function(response, status, error){
				CFN_ErrorAjax("getUnitDirectorData.do", response, status, error);
			}
		});
	}
	
	//조직도띄우기
	function OrgMap_Open(mapflag){
		flag = mapflag;
		
		if(mapflag == "0"){
			parent.Common.open("","orgmap_pop","<spring:message code='Cache.lbl_apv_org'/>","/covicore/control/goOrgChart.do?callBackFunc=_CallBackMethod2&type=C1","1060px","580px","iframe",true,null,null,true);
		}else{
			parent.Common.open("","orgmap_pop","<spring:message code='Cache.lbl_apv_org'/>","/covicore/control/goOrgChart.do?callBackFunc=_CallBackMethod2&type=C9","1060px","580px","iframe",true,null,null,true);
		}
	}
	
	//저장
	function saveSubmit(){
		var UnitCode = $("#UnitCode").val();
		var EntCode= $("#EntCode").val();
		var Description = $("#APP_DETAIL").val();
		var SortKey = $("#APP_SORT").val();
		var UnitName = $("#APP_NAME").val();
		var AuthStartDate = $("#AuthStartDate").val();
		var AuthEndDate = $("#AuthEndDate").val();
		
		if (axf.isEmpty(UnitCode)) {
			parent.Common.Warning("<spring:message code='Cache.msg_apv_157' />");//대상자를 선택해 주세요.
            return false;
        }
		
		parent.Common.Confirm("<spring:message code='Cache.msg_apv_155' />", "Confirmation Dialog", function(result){
			if(!result){
				return false;
			}else{
				var jwf_unitdirectormemberArray = new Array();		
				
				jQuery.ajaxSettings.traditional = true;
				
				$("#MGRDEPTLIST > tr").each(function(i, oRow){		
					var jwf_unitdirectormemberObj = new Object();
					
					jwf_unitdirectormemberObj.UnitCode = UnitCode;
					jwf_unitdirectormemberObj.TargetUnitCode = $(oRow).find("input[name='hidden_UNIT_CODE']").val();
					jwf_unitdirectormemberObj.TargetUnitName = $(oRow).find("input[name='hidden_UNIT_NAME']").val();
					jwf_unitdirectormemberObj.ViewStartDate = $(oRow).find("input[id^='SEARCH_START']").val();
					jwf_unitdirectormemberObj.ViewEndDate = $(oRow).find("input[id^='SEARCH_END']").val();

					jwf_unitdirectormemberArray.push(jwf_unitdirectormemberObj);		
				});
				
				//jsavaScript 객체를 JSON 객체로 변환
				jwf_unitdirectormemberArray = JSON.stringify(jwf_unitdirectormemberArray);		
				
				//insert 호출
				$.ajax({
					type:"POST",
					data:{
						"UnitCode"      : UnitCode,
						"EntCode"       : EntCode,
						"Description"   : Description,
						"SortKey"       : SortKey,
						"UnitName"      : UnitName,
						"AuthStartDate" : AuthStartDate,
						"AuthEndDate" : AuthEndDate,
						"jwf_unitdirectormember" : jwf_unitdirectormemberArray
					},
					url:"insertUnitDirector.do",
					success:function (data) {
						if(data.result == "ok") {
							parent.Common.Inform("<spring:message code='Cache.msg_apv_117' />");
							closeLayer();
							parent.searchConfig();
						} else {
							parent.Common.Warning(data.message);
						}
					},
					error:function(response, status, error){
						CFN_ErrorAjax("insertUnitDirector.do", response, status, error);
					}
				});
			}
		});
	}
	
	
	//삭제
	function deleteSubmit(){
		parent.Common.Confirm("<spring:message code='Cache.msg_AreYouDelete' />", "Confirmation Dialog", function(result){
			if(!result){
				return false;
			}else{
				var UnitCode = $("#UnitCode").val();
				var EntCode = $("#EntCode").val();		
				//delete 호출
				$.ajax({
					type:"POST",
					data:{
						"UnitCode" : UnitCode,			 
						"EntCode" : EntCode			
					},
					url:"deleteUnitDirector.do",
					success:function (data) {
						if(data.result == "ok")
							parent.Common.Inform("<spring:message code='Cache.msg_apv_138' />");
						closeLayer();
						parent.searchConfig();
					},
					error:function(response, status, error){
						CFN_ErrorAjax("deleteUnitDirector.do", response, status, error);
					}
				});
			}
		});
	}
		
	//조직도선택후처리관련
	var peopleObj = {};
	parent._CallBackMethod2 = setMGRDEPTLIST;
	function setMGRDEPTLIST(peopleValue){		
	    var dataObj = eval("("+peopleValue+")");	
	    
		if(dataObj.item.length > 0){
			if(flag == 0){
				var sUnitName = dataObj.item[0].DN;
				var sUnitCode = dataObj.item[0].AN;
				var sEntCode = dataObj.item[0].ETID;
				
				paramUnitCode = sUnitCode;
				paramEntCode = sEntCode;
				
				modifySetData();
				
				$("#DIS_APP_NAME").val(CFN_GetDicInfo(sUnitName));
				$("#APP_NAME").val(sUnitName);
				$("#UnitCode").val(sUnitCode);
				$("#EntCode").val(sEntCode);
			} else if(flag == 1) {				
				$(dataObj.item).each(function(i){	
					//기존리스트에 포함되어있는지 확인
                    if($("input[name='ckhalias'][value='" + dataObj.item[i].AN + "']").length > 0){
                    	parent.Common.Warning(CFN_GetDicInfo(dataObj.item[i].DN) + "(" + dataObj.item[i].AN + ")"+"<spring:message code='Cache.msg_AlreadyAdd' />");		//은(는) 이미 추가 되었습니다
                        return;
                    }
					
                    addDataRow(dataObj.item[i].AN, dataObj.item[i].DN);
				});
			}
		}
	}

	//직접입력
	function addConfig(){		 
		if(!axf.isEmpty($("#DEPTNAME").val())&& !axf.isEmpty($("#DEPTCODE").val())){			
			if($("#user_"+$("#DEPTCODE").val()).val() == $("#DEPTCODE").val()){
				parent.Common.Warning($("#DEPTNAME").val()+"("+$("#DEPTCODE").val()+")"+"<spring:message code='Cache.msg_AlreadyAdd' />");		//은(는) 이미 추가 되었습니다
				return;
			}

			// 행 추가
			addDataRow($("#DEPTCODE").val(), $("#DEPTNAME").val());
			
			// 데이터 초기화
			$("#DEPTCODE").val("");
			$("#DEPTNAME").val("");
		}
	}

	//사용자 삭제
	function delConfig(){
		$("#MGRDEPTLIST input[type='checkbox']:checked").each(function(){
			$(this).closest("tr").remove();	
		});

		coviInput.init();
	}
	
	function void_showManual_OnClick() {
		$("#DEPTCODE").val("");
		$("#DEPTNAME").val("");
		
		if($("#hDivManualInput").is(":visible")) {
			void_btnClose_OnClick();
		} else {
			$("#hDivManualInput").show();
			$("#DEPTNAME").focus();
		}
		
		coviInput.init();
	}
	
	function void_btnClose_OnClick() {
		$("#hDivManualInput").hide();
	}
</script>
<form id="PersonDirectorOfUnitSetPopup" name="PersonDirectorOfUnitSetPopup">
	<div class="sadmin_pop">
		<table class="sadmin_table sa_menuBasicSetting mb20">
			<colgroup>
				<col width="120px;">
				<col width="*">
			</colgroup>
			<tr>
				<th><spring:message code='Cache.lbl_Specific_Dept'/></th>
				<td>
					<input name="DIS_APP_NAME" type="text" id="DIS_APP_NAME" style="width:100px;" ReadOnly="true" class="mr0" />
					<input name="APP_NAME" type="hidden" id="APP_NAME" />
					<input name="UnitCode" id="UnitCode" type="hidden" />
					<a href="#" class="btnTypeDefault" onclick="OrgMap_Open(0);"><spring:message code='Cache.lbl_apv_selection'/></a>
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_Auth_Duration'/></th>
				<td>
					<input type="text" id="AuthStartDate" style="width: 105px" class="" />
					~
					<input type="text" kind="twindate" date_startTargetID="AuthStartDate" id="AuthEndDate" style="width: 105px" class="" />
				</td>
			</tr> 
			<tr style="border-bottom:0px;">
				<th rowspan="2"><spring:message code='Cache.lbl_apv_Assignmented_Dept'/></th>
				<td class="">
					<a href="#" class="btnTypeDefault" onclick="void_showManual_OnClick();"><spring:message code='Cache.btn_apv_directinsert'/></a> <!-- 직접입력 -->
					<a href="#" class="btnTypeDefault" onclick="OrgMap_Open(1);"><spring:message code='Cache.lbl_apv_orgselect'/></a> <!-- 조직도 선택 -->
					<a href="#" class="btnTypeDefault" onclick="delConfig();"><spring:message code='Cache.btn_apv_delete'/></a> <!-- 삭제 -->
					<div id="hDivManualInput" style="display: none; background-color: #eee; border: solid 1px #cccccc; padding: 4px; margin-top: 5px;">
						<span>
							<div style="display: inline-block; width: 55px; margin-left: 10px; padding-top: 5px;"><spring:message code="Cache.lbl_DeptName"/></div>
							<input type="text" id="DEPTNAME" style="width: 100px;" maxlength="30" class=""/>
						</span>
						<span>
							<div style="display: inline-block; width: 70px; margin-left: 10px; padding-top: 5px;"><spring:message code="Cache.lbl_DeptCode"/></div>
							<input type="text" id="DEPTCODE" style="width: 100px;" maxlength="30" class=""/>
						</span>
						<span style="float: right;">
							<a href="#" class="btnTypeDefault" onclick="addConfig();"><spring:message code='Cache.btn_Add'/></a> <!-- 추가 -->
							<a href="#" class="btnTypeDefault" onclick="void_btnClose_OnClick();"><spring:message code='Cache.btn_Close'/></a> <!-- 닫기 -->
						</span>
					</div>
				</td>
			</tr> 
			<tr>
				<td>
					<div style="min-height: 200px;">
						<table class="" style="border-top:1px solid #bbb;">
							<colgroup>
								<col style="width: 5%;"/>
								<col style="width: 20%;"/>
								<col style="width: 25%;"/>
								<col style="width: 55%;"/>
							</colgroup>
							<thead>
								<tr style="height: 38px;">
									<th style=""></th>
									<th style="text-align: center; padding: 5px;"><spring:message code="Cache.lbl_DeptName"/></th>
									<th style="text-align: center; padding: 5px;"><spring:message code="Cache.lbl_DeptCode"/></th>
									<th style="text-align: center; padding: 5px;"><spring:message code="Cache.lbl_Search_Duration"/></th>
								</tr>
							</thead>
							<tbody id="MGRDEPTLIST" name="MGRDEPTLIST"></tbody>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_apv_desc'/></th>
				<td><input name="APP_DETAIL" type="text" id="APP_DETAIL" style="" maxlength="255" class="" /></td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_apv_Sort'/></th>
				<td><input name="APP_SORT" type="text" id="APP_SORT" InputMode="Numberic"  num_min="0" num_max="32767" style="width: 50px;" maxlength="9"  class=""/>
			</tr>
		</table>
		<div class="bottomBtnWrap">
			<a href="#" class="btnTypeDefault btnTypeBg" onclick="saveSubmit();" ><spring:message code="Cache.btn_apv_save"/></a>
			<a id="btn_delete" href="#" class="btnTypeDefault" onclick="deleteSubmit();" style="display:none"><spring:message code="Cache.btn_apv_delete"/></a>
			<a href="#" class="btnTypeDefault" onclick="closeLayer();" ><spring:message code="Cache.btn_apv_close"/></a>
		</div>
		<div class="collabo_help">
			<p><spring:message code='Cache.msg_apv_DeptDocAuthorityScope'/></p> <!-- 대상 부서 부서함의 완료함, 발신함, 수신처리함, 참조/회람함에 대하여 조회 권한을 부여합니다. -->
			<p><spring:message code='Cache.msg_apv_269'/></p> <!-- 폐지된 부서의 경우 부서명, 부서코드를 직접 입력하여 사용 가능합니다. -->
		</div>
	</div>
	<input type="hidden" id="SeqHiddenValue" value="" />
	<input type="hidden" id="EntCode" value="" />
</form>
