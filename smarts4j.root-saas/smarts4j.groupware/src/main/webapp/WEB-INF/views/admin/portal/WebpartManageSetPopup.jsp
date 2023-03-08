<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/cmmn/AdminInclude.jsp"></jsp:include>

<form name="form1" style="margin-right: 5px;">
	<div id="popBox" >
       	<div class="AXTabsLarge" style="margin-bottom: 10px">
			<div class="AXTabsTray">
				<a onclick="clickTab(this);" class="AXTab on" value="divWebpartBasicInfo"><spring:message code='Cache.lbl_apv_baseInfo'/></a><!--기본정보-->
				<a onclick="clickTab(this);" class="AXTab" value="divWebpartDataInfo"><spring:message code='Cache.lbl_DataInfo'/></a><!--데이터정보-->
			</div>
			<div id="divWebpartInfo" class="TabBox">
				<!--웹파트 기본정보 탭 시작 -->	 
				<div id="divWebpartBasicInfo">				
				    <table class="AXFormTable" >
					  <colgroup>
				          <col style="width:100px;"/>
				          <col />
				          <col style="width:100px;"/>
				          <col />
				      </colgroup>
					  <tbody>
						<tr>
						  <th><spring:message code="Cache.lbl_WebPartName"/><font color="red">*</font></th> <!-- 웹 파트 이름  -->
						  <td colspan="3">
						  	<input id="webpartName" name="webpartName" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:83%"/> 
						  	<input id="hidNameDicInfo" name="hidNameDicInfo" type="hidden" />
						  	<input id="dictionaryBtn" type="button" value="<spring:message code='Cache.lbl_MultiLang2'/>" class="AXButton" onclick="dictionaryLayerPopup();" /> <!-- 다국어 -->
						  </td> 
					    </tr>
					    <tr>
					      <th><spring:message code="Cache.lbl_OwnedCompany"/><font color="red">*</font></th>   <!-- 소유회사  -->
						  <td><select id="companySelectBox" name="companySelectBox" class="AXSelect W100" ></select> </td>
					      <th><spring:message code="Cache.lbl_selUse"/><font color="red">*</font></th>  <!-- 사용유무  -->
						  <td><select id="isUseSelectBox" name="isUseSelectBox" class="AXSelect W100"></select>  </td>
					    </tr>
					    <tr>
					      <th><spring:message code="Cache.lbl_BizSection"/><font color="red">*</font></th> <!-- 업무구분  -->
						  <td><select id="bizSectionSelectBox" name="bizSectionSelectBox" class="AXSelect W100"></select> </td>
					      <th><spring:message code="Cache.lbl_ModuleRange"/><font color="red">*</font></th> <!-- 사용범위  -->
						  <td><select id="rangeSelectBox" name="rangeSelectBox" class="AXSelect W100"></select> </td>
					    </tr>
						<tr>
						  <th><spring:message code="Cache.lbl_MinHeight"/>(px)<font color="red">*</font></th> <!--최소높이 -->
						  <td colspan="3">
						  	<input id="minHeight" name="minHeight" type="text" class="AXInput" mode="numberint" placeholder="Number only" style="width:96%"/> 
						  </td>
					    </tr>
						<tr>
						  <th><spring:message code="Cache.lbl_HTML_path"/></th> <!--HTML 경로-->
						  <td colspan="3">
						  	<input id="htmlPath" name="htmlPath" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:96%" placeholder="Ex). webpart/UserApprovalList.html"/> 
						  </td>
					    </tr>
						<tr>
						  <th><spring:message code="Cache.lbl_JS_path"/></th> <!--JS 경로-->
						  <td colspan="3">
						  	<input id="jsPath" name="jsPath" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:96%" placeholder="Ex). webpart/UserApprovalList.js"/> 
						  </td>
					    </tr>
					    <tr>
					      <th><spring:message code="Cache.lbl_JS_module_name"/></th> <!--JS 모듈명-->
						  <td colspan="3">
						  	<input id="jsModuleName" name="jsModuleName" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:79%"/> 
						  	<input type="button" class="AXButton" value="<spring:message code='Cache.btn_CheckDouble'/>" onclick="clickChkDuplication()"/> 
						  	<input type="hidden" id="chkDuplication" value="N"/> 
						  </td>
					    </tr>
					    <tr>
						  <th>Preview</th>
						  <td colspan="3">
						  	<textarea id="preview" rows="5" style="width: 96%; margin: 0px;  resize:none;" class="AXTextarea"></textarea> 
						  </td>
					    </tr>
					    <tr>
						  <th>Ref(.js, .css)</th>
						  <td colspan="3">
						  	<input id="ref" name="ref" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:80%"/>  Division : ";"
						  </td>
					    </tr>
					    <tr>
						  <th>Script Method</th>
						  <td colspan="3">
						  	<input id="scriptMethod" name="scriptMethod" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:96%"/>
						  </td>
					    </tr>
					    <tr>
						  <th><spring:message code='Cache.lbl_Thumbnail'/></th><!--썸네일-->
						  <td colspan="3">
						  	<input id="thumbnail" name="thumbnail" type="file" style="width:96%"/>
						  	<input id="thumbnailPath" name="thumbnailPath" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:96%"/>
						  </td>
					    </tr>
					    <tr>
						  <th><spring:message code='Cache.lbl_Description'/></th><!--설명-->
						  <td colspan="3">
						  	<input id="description" name="description" type="text" class="AXInput HtmlCheckXSS ScriptCheckXSS" style="width:96%"/>
						  </td>
					    </tr>
					   </tbody>
					</table>
					<h4 style="margin-top:25px;"><spring:message code='Cache.lbl_webpart_exJSON'/></h4><!--웹 파트 확장 JSON -->
					<div style="float:right; margin-bottom:5px;">
						<input type="button" class="AXButtonSmall" value="<spring:message code='Cache.btn_Add'/>" onclick="addExJsonRow()"/>
						<input type="button" class="AXButtonSmall" value="<spring:message code='Cache.btn_delete'/>" onclick="delExJsonRow()"/>
					</div>
				    <table id="exJson" cellpadding="0" cellspacing="0" style="border: 1px solid #d4d4d4;width: 100%;" class="AXFormTable" >
	                   <colgroup>
	                       <col width="25px" />
	                       <col width="100px" />
	                       <col />
	                   </colgroup>
	                   <tbody id="exJsonData">
		                   <tr>
			                   <th style="text-align: center; padding-left:5px;"><input type="checkbox" id="allChk" onClick="selectAll()"/></th>
			                   <th style="text-align: center;">key</th>
			                   <th style="text-align: center;">value</th>
			               </tr>
	                   </tbody>
			        </table>		      		       
		        </div>		   
		        <!--웹파트 기본정보 탭 종료 -->
		        
		        <!--웹파트 데이터 정보 탭 시작-->
				<div id="divWebpartDataInfo" style="display:none">		
				 	<div style="float:right; margin:5px 0;">
						<input type="button" class="AXButtonSmall" value="<spring:message code='Cache.btn_Add'/>" onclick="addDataInfoDiv()"/>
						<input type="button" class="AXButtonSmall" value="<spring:message code='Cache.btn_delete'/>" onclick="delDataInfoDiv()"/>
					</div>
					<!--데이터 정보 입력 DIV 시작-->
					<div id="dataInfo">
						<div name="dataInfoDiv" style="border: 1px #949292 solid; clear:both;">
							<table class="AXFormTable" style="border: 1px solid #d4d4d4;">
								<colgroup>
									<col width="120px"/>
									<col width="100px"/>
									<col/>
									<col width="100px"/>
								</colgroup>
								<tbody>
									<tr>
										<th>QueryID</th>
										<td colspan="3"><input type="text" class="AXInputSmall" name="queryID" style="width:98%;"/></td>
									</tr>
									<tr>
										<th><spring:message code='Cache.lbl_result_value_composition'/></th> <!--결과값 구성-->
										<td colspan="3"><input type="text" class="AXInputSmall" name="result" style="width:98%;"/></td>
									</tr>
									<tr>
										<th rowspan="2">
											<spring:message code='Cache.lbl_parameter'/><br>
											<span class="AXButton" style="font-size: 12px; font-weight: 100; padding: 0 4px; margin-top: 4px;" name="addParamBtn" onclick="addParameter(this)">
												<img id="addParam" class="ico_btn" alt="" src="/HtmlSite/smarts4j_n/covicore/resources/images/covision/ico_plus.gif">
												<spring:message code='Cache.btn_Add'/>
											</span>
											<span class="AXButton" style="font-size: 12px; font-weight: 100; padding: 0 4px; margin-top: 4px;" name="delParamBtn" onclick="delParameter(this)">
												<img id="delParma" class="ico_btn" alt="" src="/HtmlSite/smarts4j_n/covicore/resources/images/covision/ico_x.gif" >						
												<spring:message code='Cache.lbl_delete'/>
											</span>
										</th>
										<th style="text-align:center;">Key</th>
										<th style="text-align:center;">Value</th>
										<th style="text-align:center;">Type</th>
									</tr>
									<tr name="param">
										<td style="text-align:center;"><input type="text" name="key" class="AXInputSmall" style="width:90%;"/></td>
										<td style="text-align:center;"><input type="text" name="value" class="AXInputSmall" style="width:90%;"/></td>
										<td style="text-align:center;">
											<select class="AXSelect" name="type" style="height:18px;">
												<option value="fixed">fixed</option>
												<option value="session">session</option>
												<option value="config">config</option>
												<option value="acl">acl</option>
											</select>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<!--데이터 정보 입력 DIV 시작-->
        		</div>
        		<!--웹파트 데이터 정보 탭 종료 -->
			</div>								
		</div>
	</div>
	<div class="popBtn">
		<input type="button" id="btnSave"  class="AXButton red" value="<spring:message code="Cache.btn_apv_save"/>" onclick="saveWebpart();"/>
		<input type="button" id="btnClose" class="AXButton" value="<spring:message code="Cache.btn_apv_close"/>" onclick="Common.Close();"/>
	</div>
</form>
				


<script  type="text/javascript">
	//# sourceURL=WebpartManageSetPopup.jsp

	var webpartID = CFN_GetQueryString("webpartID") == 'undefined'? 0 : CFN_GetQueryString("webpartID");
	var mode = CFN_GetQueryString("mode") == 'undefined'? '' : CFN_GetQueryString("mode");
	var lang = Common.getSession("lang");
	
	//ready
	init();
	
	function init(){
		
		setPopUpSelectBox();					
		
		if(webpartID!=0){
			setData(webpartID);
		}
		
		$("#jsModuleName").change(function(){
			$("#chkDuplication").val("N");
		});
	}
	
	function setPopUpSelectBox(){
		coviCtrl.renderCompanyAXSelect("companySelectBox",lang,true,"","",'')
		coviCtrl.renderAXSelect("BizSection,WebmoduleRange","bizSectionSelectBox,rangeSelectBox",lang,"" ,"", ""); 
		coviCtrl.renderAXSelect("BizSection","bizSectionSelectBox",lang,"", 	"", "");
		$("#isUseSelectBox").bindSelect({ //검색 조건
			options: [{'optionValue':'','optionText':'선택'},{'optionValue':'Y','optionText':"<spring:message code='Cache.lbl_USE_Y'/>"},{'optionValue':'N','optionText':"<spring:message code='Cache.lbl_USE_N'/>"}]
		});
	}

	
	
	function setData(webpartID){
		$.ajax({
			type:"POST",
			url:"/groupware/portal/getWebpartData.do",
			data:{
				"webpartID":webpartID
			},
			success:function(data){
				if(data.status == 'SUCCESS'){
					if(mode!='copy'){
						$("#webpartName").val(data.list[0].DisplayName);
						$("#hidNameDicInfo").val(data.list[0].MultiDisplayName);
						$("#jsModuleName").val(data.list[0].JsModuleName);
						$("#chkDuplication").val("Y");
					}
				
					$("#companySelectBox").bindSelectSetValue(data.list[0].CompanyCode);
					$("#isUseSelectBox").bindSelectSetValue(data.list[0].IsUse);
					$("#bizSectionSelectBox").bindSelectSetValue(data.list[0].BizSection);
					$("#rangeSelectBox").bindSelectSetValue(data.list[0].Range);
					$("#minHeight").val(isNull(data.list[0].MinHeight,''));
					$("#htmlPath").val(isNull(data.list[0].HtmlFilePath,''));
					$("#jsPath").val(isNull(data.list[0].JsFilePath,''));
					$("#preview").val(Base64.b64_to_utf8(isNull(data.list[0].Preview,'')));
					$("#ref").val(isNull(data.list[0].Resource,''));
					$("#scriptMethod").val(isNull(data.list[0].ScriptMethod,''));
					$("#description").val(isNull(data.list[0].Description,''));	
					$("#thumbnailPath").val(isNull(data.list[0].Thumbnail,''))
					
					setExpansionJSON(data.list[0].ExtentionJSON);
					setDataInfo(data.list[0].DataJSON);

					//커뮤니티용일 경우 이름만 변경 가능
					if(data.list[0].Range == "Community"){
						$("#divWebpartInfo input,button,select,textarea").prop( "disabled", true );
						$("#companySelectBox").bindSelectDisabled(true);
						$("#isUseSelectBox").bindSelectDisabled(true);
						$("#bizSectionSelectBox").bindSelectDisabled(true);
						$("#rangeSelectBox").bindSelectDisabled(true);
						
						$("#webpartName").prop("disabled", false);
						$("#dictionaryBtn").prop("disabled", false);
					}
				}else{
					Common.Warning("<spring:message code='Cache.msg_apv_030'/>"); //오류가 발생했습니다.
				}
			},
			error:function(response, status, error){
			     //TODO 추가 오류 처리
			     CFN_ErrorAjax("/groupware/portal/getWebpartData.do", response, status, error);
			}
		});
	}
	
	function setExpansionJSON(extJson){
		if(extJson == undefined){
			return;
		}
		var keys = Object.keys(extJson);
		
		for(var i = 0; i< keys.length; i++){
			addExJsonRow();
		}
		
		$("#exJson #exJsonData tr[name='exData']").each(function(i,obj){
			$(obj).find("[name=exKey]").val(keys[i]);
			if(typeof extJson[keys[i]] == "object"){
				try{
					$(obj).find("[name=exValue]").val(JSON.stringify(extJson[keys[i]]));				
				}catch(e){
					$(obj).find("[name=exValue]").val("");				
				}
			}else{
				$(obj).find("[name=exValue]").val(extJson[keys[i]]);
			}
		});
		
	}
	
	function setDataInfo(dataObj){
		if(dataObj == undefined){
			return;
		}
		for(var i = 0; i< dataObj.length-1; i++){ //이미 한칸은 만들어져 있기 때문에 -1
			addDataInfoDiv();
		}
		
		for(var i = 0; i< dataObj.length; i++){ //이미 한칸은 만들어져 있기 때문에 -1
			for(var j = 0; j <dataObj[i].paramData.length-1; j++){
				$.find("span[name='addParamBtn']")[i].click();//addParameter()
			}
		}
		
		if(dataObj.length > 0){
			$("div[name='dataInfoDiv']").each(function(i,obj){
				$(obj).find("tr>td>input[name=queryID]").val(dataObj[i].queryID)
				$(obj).find("tr>td>input[name=result]").val(dataObj[i].resultKey)
				
				if(dataObj[i].paramData.length>0){
					$(obj).find("tr[name='param']").each(function(j,param){
						$(param).find("td>input[name='key']").val(dataObj[i].paramData[j].key);
						$(param).find("td>input[name='value']").val(dataObj[i].paramData[j].value);
						$(param).find("td>select[name='type']").val(dataObj[i].paramData[j].type);
					});
				}
			});
		}
	}
	
	//저장
	function saveWebpart(){
        if(!validationChk()){
        	return false;
        }
		
        var sDictionaryInfo = document.getElementById("hidNameDicInfo").value;
        if (sDictionaryInfo == "") {
            switch (lang.toUpperCase()) {
                case "KO": sDictionaryInfo = document.getElementById("webpartName").value + ";;;;;;;;;"; break;
                case "EN": sDictionaryInfo = ";" + document.getElementById("webpartName").value + ";;;;;;;;"; break;
                case "JA": sDictionaryInfo = ";;" + document.getElementById("webpartName").value + ";;;;;;;"; break;
                case "ZH": sDictionaryInfo = ";;;" + document.getElementById("webpartName").value + ";;;;;;"; break;
                case "E1": sDictionaryInfo = ";;;;" + document.getElementById("webpartName").value + ";;;;;"; break;
                case "E2": sDictionaryInfo = ";;;;;" + document.getElementById("webpartName").value + ";;;;"; break;
                case "E3": sDictionaryInfo = ";;;;;;" + document.getElementById("webpartName").value + ";;;"; break;
                case "E4": sDictionaryInfo = ";;;;;;;" + document.getElementById("webpartName").value + ";;"; break;
                case "E5": sDictionaryInfo = ";;;;;;;;" + document.getElementById("webpartName").value + ";"; break;
                case "E6": sDictionaryInfo = ";;;;;;;;;" + document.getElementById("webpartName").value; break;
                default : sDictionaryInfo = document.getElementById("webpartName").value+ ";;;;;;;;;"; break;
            }
            document.getElementById("hidNameDicInfo").value = sDictionaryInfo
        }
		
  
        var exJsonData = makeExpansionJSON();
        if(exJsonData == -1 ){
        	Common.Warning("<spring:message code='Cache.msg_WebPartManage_09'/>") //확장 JSON의 key를 입력하십시오.
        	return false;
        }
        
        var dataInfo = makeDataInfo();
        if(dataInfo == -1 ){
        	Common.Warning("<spring:message code='Cache.msg_WebPartManage_10'/>") //데이터 정보의 QueryID를 입력하십시오.
        	return false;
        }
		
        var url;
		if(webpartID!=0 && mode!='copy'){			
			url = "/groupware/portal/updateWebpartData.do";			
		}else{
			url = "/groupware/portal/insertWebpartData.do";
		}
		
		var formData = new FormData();
		formData.append("webpartID", webpartID);
		formData.append("webpartName", $("#webpartName").val());
		formData.append("dicWebpartName", $("#hidNameDicInfo").val());
		formData.append("companyCode", $("#companySelectBox").val());
		formData.append("isUse", $("#isUseSelectBox").val());
		formData.append("bizSection", $("#bizSectionSelectBox").val());
		formData.append("range", $("#rangeSelectBox").val());
		formData.append("minHeight", $("#minHeight").val());
		formData.append("htmlPath", $("#htmlPath").val());
		formData.append("jsPath", $("#jsPath").val());
		formData.append("jsModuleName", $("#jsModuleName").val());
		formData.append("preview", Base64.utf8_to_b64($("#preview").val()));
		formData.append("ref", $("#ref").val());
		formData.append("scriptMethod", $("#scriptMethod").val());
		formData.append("description", $("#description").val());
		formData.append("exJson", JSON.stringify(exJsonData));
		formData.append("dataInfo", JSON.stringify(dataInfo));
		formData.append("thumbnail", $("#thumbnail")[0].files[0]);
		formData.append("thumbnailPath", $("#thumbnailPath").val());
		
		/* var saveJson ={
				"webpartID":webpartID,
				"webpartName":$("#webpartName").val(),
				"dicWebpartName":$("#hidNameDicInfo").val(),
				"companyCode":$("#companySelectBox").val(),
				"isUse":$("#isUseSelectBox").val(),
				"bizSection":$("#bizSectionSelectBox").val(),
				"range":$("#rangeSelectBox").val(),
				"minHeight":$("#minHeight").val(),
				"htmlPath":$("#htmlPath").val(),
				"jsPath":$("#jsPath").val(),
				"jsModuleName":$("#jsModuleName").val(),
				"preview":Base64.utf8_to_b64($("#preview").val()),
				"ref":$("#ref").val(),
				"scriptMethod":$("#scriptMethod").val(),
				"description":$("#description").val(),
				"exJson":JSON.stringify(exJsonData),
				"dataInfo":JSON.stringify(dataInfo),
				"thumbnail":$("#thumbnail")[0].files[0]
		} */
		
		//insert 호출		
		 $.ajax({
	            type : 'post',
	            url : url,
	            data : formData,
	            dataType : 'json',
	            processData : false,
		        contentType : false,
	            success : function(data){	
	            	if(data.status=='SUCCESS'){
		            	Common.Inform("<spring:message code='Cache.msg_37'/>","Information",function(){ //저장되었습니다.
							Common.Close();
		            		if(parent.webpartGrid != undefined){parent.webpartGrid.reloadList();}
		            	});
	            	}else{
	            		Common.Warning("<spring:message code='Cache.msg_apv_030'/>");
	            	}
	            },
	            error:function(response, status, error){
	                //TODO 추가 오류 처리
	                CFN_ErrorAjax(url, response, status, error);
	            }
	        });
	}
	
	//확장 JSON 생성
	function makeExpansionJSON(){
		var hasKey = true;
		var jsonObj = new Object();
		
		$("#exJson #exJsonData tr[name='exData']").each(function(i,obj){
			var key = $(obj).find("[name=exKey]").val();
			if(key != '' && typeof(key) != 'undefined'){
				jsonObj[key] = $(obj).find("[name=exValue]").val();
			}else{
				if($(obj).find("[name=exValue]").val()!=''){ //value는 입력되어 있지만 key 값이 없으면.
					$(obj).find("[name=exKey]").focus();
					hasKey = false;;
					return false;
				}
			}
		});
		if(hasKey){
			return jsonObj;
		}else{
			return -1;
		}
	}
	
	function makeDataInfo(){
		var hasQueryID = true;
		var jsonArr = new Array();
		
		$("div[name='dataInfoDiv']").each(function(i,obj){
			var jsonObj = new Object();
			if($(obj).find("tr>td>input[name=queryID]").val()!=''){
				jsonObj["queryID"] = $(obj).find("tr>td>input[name=queryID]").val();
				jsonObj["resultKey"] = $(obj).find("tr>td>input[name=result]").val();
			}else{
				if($(obj).find("tr>td>input[name=result]").val()!=''){ //resultKey는 입력되어 있지만  QueryID 값이 없으면.
					$(obj).find("tr>td>input[name=queryID]").focus();
					hasQueryID = false;
					return false; //break;
				}
				return true; //confinue;
			}
			
			var paramArr = new Array();		
			$(obj).find("tr[name='param']").each(function(i,param){
				var paramObj = new Object();
				if($(param).find("td>input[name='key']").val()!=''){
					paramObj["key"] = $(param).find("td>input[name='key']").val();
					paramObj["value"] = $(param).find("td>input[name='value']").val();
					paramObj["type"] = $(param).find("td>select[name='type']").val();
					paramArr.push(paramObj);
				}
			});
			jsonObj["paramData"] = paramArr;
			
			jsonArr.push(jsonObj);
		});
		
		if(hasQueryID){
			return jsonArr;
		}else{
			return -1;
		}
	}
	
 	// 입력값 체크
    function validationChk() {
    	var aStrSpecialChar = new Array(";", "'", "^");
 	    var nLength = aStrSpecialChar.length;

 	   	if (!XFN_ValidationCheckOnlyXSS(false)) { return; }
 		
        if (document.getElementById("webpartName").value == '') {
            Common.Warning("<spring:message code='Cache.msg_WebPartManage_04'/>", "Warning Dialog", function () {     // 웹 파트 이름을 입력하여 주십시오.
            	document.getElementById("webpartName").focus();
            });
            return false;
        }   
        
        for (var i = 0; i < nLength; i++) {
  	        if (document.getElementById("webpartName").value.indexOf(aStrSpecialChar[i]) >= 0) {
  	            var sMessage = "<spring:message code='Cache.msg_DictionaryInfo_01'/>";  // 특수문자 [{0}]는 사용할 수 없습니다.
  	            sMessage = sMessage.replace(/\{0\}/gi, aStrSpecialChar[i]);
  	            Common.Warning(sMessage, 'Warning Dialog', function () {
  	            	document.getElementById("webpartName").focus();
  	            });
  	            return false;
  	        }
  	    }
        
        if($("#rangeSelectBox").val() == "Community"){
        	return true;
        }
		
        if($("#companySelectBox").val()==''){
        	 Common.Warning("<spring:message code='Cache.msg_WebPartManage_05'/>")  // 소유회사를 선택하여 주십시오.
             return false;
        }else if($("#isUseSelectBox").val()=='') {
			Common.Warning("<spring:message code='Cache.msg_WebPartManage_16'/>")  // 사용유무를 선택하여 주십시오.
			return false;
		}else if($("#bizSectionSelectBox").val()==''||$("#bizSectionSelectBox").val()=='BizSection'){
        	 Common.Warning("<spring:message code='Cache.msg_WebPartManage_06'/>")  // 업무구분을 선택하여 주십시오.
             return false;
		}else if($("#rangeSelectBox").val()==''||$("#rangeSelectBox").val()=='WebmoduleRange') {
			Common.Warning("<spring:message code='Cache.msg_WebPartManage_17'/>")  // 웹 모듈 사용범위를 선택하여 주십시오.
			return false;
        }else if($("#minHeight").val()==''){
        	 Common.Warning("<spring:message code='Cache.msg_WebPartModuleManage_05'/>", "Warning Dialog", function () {     // 최소높이를 입력하여 주십시오.
             	document.getElementById("minHeight").focus();
             });
             return false;
        }/* else if($("#htmlPath").val()==''){
        	 Common.Warning("<spring:message code='Cache.msg_WebPartManage_11'/>", "Warning Dialog", function () {     // HTML 경로를 입력하십시오.
             	document.getElementById("htmlPath").focus();
             });
             return false;
 		} */else if($("#jsModuleName").val()!='' && $("#chkDuplication").val()=="N"){
 			 Common.Warning("<spring:message code='Cache.msg_WebPartManage_15'/>")  // JS 모듈명 중복체크를 해주세요
             return false;
 		}else{
        	return true;
        }
    }
 
 	// 탭 클릭 이벤트
	function clickTab(pObj){
		var strObjName = $(pObj).attr("value");
		$(".AXTab").attr("class","AXTab");
		$(pObj).addClass("AXTab on");

	
		$("#divWebpartBasicInfo").hide();
		$("#divWebpartDataInfo").hide();
		
		$("#" + strObjName).show();
	}
	
 	function selectAll(){
 		if($("#allChk").is(":checked")){
 			  $("#exJson input[name=jsonChk]").prop("checked",true);
 		}else{
 			   $("#exJson input[name=jsonChk]").prop("checked",false);
 		}
 	}
 	
 	function addExJsonRow(){
 		var html = ''
 		html += '<tr name="exData">'
 		html += '<td><input type="checkbox" name="jsonChk"/></td>'
 		html += '<td><input type="text" name="exKey" class="AXInput" style="width:90%;"/></td>'
 		html += '<td><input type="text" name="exValue" json-value="true" class="AXInput" style="width:98%;"/></td>'
 		html += '</tr>'
 		
 		$('#exJson > tbody:last').append(html);
 	}	
 
 	function delExJsonRow(){
 		$("#exJson input[type='checkbox'][name='jsonChk']:checked").parent().parent().remove();
 	}	
 	
 	function addParameter(obj){
 		var oldRowSpan = $(obj).closest("th").last().attr("rowspan");
 		var html = '';
 		
 		html += '<tr name="param">';
 		html += '<td style="text-align:center;"><input type="text" name="key" class="AXInputSmall" style="width:90%;"/></td>';
		html += '<td style="text-align:center;"><input type="text" name="value" class="AXInputSmall" style="width:90%;"/></td>';
		html += '<td style="text-align:center;">';
		html += '<select class="AXSelect" style="height:18px;" name="type">';
		html += '<option value="fixed">fixed</option>';
		html += '<option value="session">session</option>';
		html += '<option value="config">config</option>';
		html += '<option value="acl">acl</option>';
		html += '</select>';
		html += '</td>';
		html += '</tr>';
 		
		$(obj).closest("th").last().attr("rowspan", parseInt(oldRowSpan)+1);
		$(obj).closest("tbody").last().append(html);
 	}
 	
	function delParameter(obj){
		var oldRowSpan = $(obj).closest("th").last().attr("rowspan");
		var cnt = $(obj).closest("tbody").children("[name='param']").length;
		
		if(cnt>1){
			$(obj).closest("tbody").children("[name='param']").last().remove();
			$(obj).closest("th").last().attr("rowspan", parseInt(oldRowSpan)-1);
		}
 	}
 	
	function addDataInfoDiv(){
		var html = '';
		html += '<div name="dataInfoDiv" style="border: 1px #949292 solid; margin-top: 15px;">';
		html += '<table class="AXFormTable" style="border: 1px solid #d4d4d4;">';
		html += '<colgroup>';
		html += '<col width="100px"/>';
		html += '<col width="100px"/>';
		html += '<col/>';
		html += '<col width="100px"/>';
		html += '</colgroup>';
		html += '<tbody>';
		html += '<tr>';
		html += '<th>QueryID</th>';
		html += '<td colspan="3"><input type="text" class="AXInputSmall" name="queryID" style="width:98%;"/></td>';
		html += '</tr>';
		html += '<tr>';
		html += "<th><spring:message code='Cache.lbl_result_value_composition'/></th>";
		html += '<td colspan="3"><input type="text" class="AXInputSmall" name="result" style="width:98%;"/></td>';
		html += '</tr>';
		html += '<tr>';
		html += '<th rowspan="2">';
		html += "<spring:message code='Cache.lbl_parameter'/><br>";
		html += '<span style="color:#888; cursor: pointer;" name="addParamBtn" onclick="addParameter(this)">';
		html += '<img id="addParam" class="ico_btn" alt="" src="/HtmlSite/smarts4j_n/covicore/resources/images/covision/ico_plus.gif">';
		html += " <spring:message code='Cache.btn_Add'/> ";
		html += '</span>';
		html += '<span style="color:#888; cursor: pointer;" name="delParamBtn" onclick="delParameter(this)">';
		html += '<img id="delParma" class="ico_btn" alt="" src="/HtmlSite/smarts4j_n/covicore/resources/images/covision/ico_x.gif" >';					
		html += " <spring:message code='Cache.lbl_delete'/> ";
		html += '</span>';
		html += '</th>';
		html += '<th style="text-align:center;">Key</th>';
		html += '<th style="text-align:center;">Value</th>';
		html += '<th style="text-align:center;">Type</th>';
		html += '</tr>';
		html += '<tr name="param">';
		html += '<td style="text-align:center;"><input type="text" name="key" class="AXInputSmall" style="width:90%;"/></td>';
		html += '<td style="text-align:center;"><input type="text" name="value" class="AXInputSmall" style="width:90%;"/></td>';
		html += '<td style="text-align:center;">';
		html += '<select class="AXSelect" name="type" style="height:18px;">';
		html += '<option value="fixed">fixed</option>';
		html += '<option value="session">session</option>';
		html += '<option value="config">config</option>';
		html += '<option value="acl">acl</option>';
		html += '</select>';
		html += '</td>';
		html += '</tr>';
		html += '</tbody>';
		html += '</table>';
		html += '</div>';
		
		$("#dataInfo").last().append(html);
	} 
	
	
	function delDataInfoDiv(){
		var cnt = $("#dataInfo").children("[name='dataInfoDiv']").length;
		
		if(cnt>1){
			$("#dataInfo").children("[name='dataInfoDiv']").last().remove()
		}
	}
	
	//다국어 설정 팝업
	function dictionaryLayerPopup(){
		var option = {
				lang : lang,
				hasTransBtn : 'true',
				allowedLang : 'ko,en,ja,zh,lang1,lang2',
				useShort : 'false',
				dicCallback : 'dicCallback',
				popupTargetID : 'setMultiLangData',
				init : 'dicInit'
		};
		
		var url = "";
		url += "/covicore/control/calldic.do?lang=" + option.lang;
		url += "&hasTransBtn=" + option.hasTransBtn;
		url += "&useShort=" + option.useShort;
		url += "&dicCallback=" + option.dicCallback;
		url += "&allowedLang=" + option.allowedLang;
		url += "&popupTargetID=" + option.popupTargetID;
		url += "&init=" + option.init;
		
		Common.open("","setMultiLangData","<spring:message code='Cache.lbl_MultiLangSet' />",url,"400px","280px","iframe",true,null,null,true);
	}
	
	//다국어 세팅 함수
	function dicInit(){
		if(document.getElementById("hidNameDicInfo").value == ''){
			value = document.getElementById('webpartName').value;
		}else{
			value = document.getElementById("hidNameDicInfo").value;
		}
		
		return value;
	}
	
	//다국어 콜백 함수
	function dicCallback(data){
		$("#hidNameDicInfo").val(coviDic.convertDic(data));
		if(document.getElementById('webpartName').value == ''){
			document.getElementById('webpartName').value = CFN_GetDicInfo(coviDic.convertDic(data),lang);
		}
		
		Common.Close("setMultiLangData");
	}
	
	function clickChkDuplication(){
		if (!XFN_ValidationCheckOnlyXSS(false)) { return; }

		if($("#jsModuleName").val()!=""){
			$.ajax({
				type:"POST",
				url:"/groupware/portal/checkJsModuleNameDuplication.do",
				data:{
					"webpartID": webpartID,
					"moduleName":$("#jsModuleName").val()
				},
				success:function(data){
					if(data.cnt<1){
						Common.Inform("<spring:message code='Cache.lbl_apv_useOK'/>");
						$("#chkDuplication").val("Y");
					}else{
						Common.Inform("<spring:message code='Cache.lbl_apv_useNOT'/>");
						$("#chkDuplication").val("N");
					}
				},
	            error:function(response, status, error){
	                //TODO 추가 오류 처리
	                CFN_ErrorAjax("/groupware/portal/checkJsModuleNameDuplication.do", response, status, error);
	            }
				
			});
			
			
		}else{
			Common.Warning("<spring:message code='Cache.msg_WebPartManage_14'/>", "Warning Dialog", function () {     // HTML 경로를 입력하십시오.
             	document.getElementById("jsModuleName").focus();
            });
		}
	}
	
	function isNull(data, defaultVal){
		if(typeof data == 'undefined' || data == null){
			return defaultVal;
		}
		return data;
	}
</script>
