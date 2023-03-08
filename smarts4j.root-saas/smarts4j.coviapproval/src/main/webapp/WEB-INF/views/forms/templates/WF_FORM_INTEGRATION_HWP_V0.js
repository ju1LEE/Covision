//양식별 다국어 정의 부분
var localLang_ko = {
    localLangItems: {
    }
};

var localLang_en = {
    localLangItems: {
    }
};

var localLang_ja = {
    localLangItems: {
    }
};

var localLang_zh = {
    localLangItems: {
    }
};


//양식별 후처리를 위한 필수 함수 - 삭제 시 오류 발생
function postRenderingForTemplate() {

    setSelect();
	
    // 체크박스, radio 등 공통 후처리
    postJobForDynamicCtrl();
    
    //읽기 모드 일 경우
    if (getInfo("Request.templatemode") == "Read") {
    	
    	//문서정보 숨김처리
		var divLen = $$($.parseJSON(XFN_ChangeOutputValue(document.getElementById("APVLIST").value))).find("steps").find("division").valLength();
		if(formJson.Request.isMobile != "Y" && (divLen == 1 || formJson.Request.mode == "REDRAFT"))
			displayDocInfo();
    	
        $('*[data-mode="writeOnly"]').each(function () {
            $(this).hide();
        });
        
        //<!--loadMultiRow_Read-->
        LoadEditorHWP("divWebEditorContainer");
    }
    else {
        $('*[data-mode="readOnly"]').each(function () {
            $(this).hide();
        });

        // 에디터 처리
        //<!--AddWebEditor-->
        LoadEditorHWP("divWebEditorContainer");
     
        //<!--loadMultiRow_Write-->
    }
    

}

function setSelect() {
	setSelectSenderMaster();
}

function setSelectSenderMaster() {
	$.ajax({
		url: "/approval/user/selectGovSenderMasterUpper.do",
		type: "POST",
        data: {
        	deptCode: getInfo("AppInfo.dpid")
        },
        async: false,
		success: function(data) {
			var senderMaster = data.list;
			
			console.log(senderMaster);
			
			for (var i = 0; i < senderMaster.length; i++) {
				var option = document.createElement("option");
				option.setAttribute("value", senderMaster[i].SEND_ID);
				option.setAttribute("data-stamp", nullToBlank(senderMaster[i].STAMP));
				option.setAttribute("data-logo", nullToBlank(senderMaster[i].LOGO));
				option.setAttribute("data-symbol", nullToBlank(senderMaster[i].SYMBOL));
				option.setAttribute("data-chief", nullToBlank(senderMaster[i].NAME));
				option.setAttribute("data-campaign-t", nullToBlank(senderMaster[i].CAMPAIGN_T));
				option.setAttribute("data-campaign-f", nullToBlank(senderMaster[i].CAMPAIGN_F));
				option.innerText = nullToBlank(senderMaster[i].NAME);
				document.getElementById("SENDERMASTER").appendChild(option);
			}
			
			document.getElementById("SENDERMASTER").value = nullToBlank(formJson.BodyContext.BODY_CONTEXT_SENDERMASTER);
		},
		error: function(error){
			Common.Error("<spring:message code='Cache.msg_apv_030' />");  // 오류가 발생했습니다.
		}
	});
}

function setSenderMasterAndSealSignAndChief() {
	document.getElementById("BODY_CONTEXT_SENDERMASTER").value = event.target.options[event.target.selectedIndex].value;
	document.getElementById("BODY_CONTEXT_CHIEF").value = event.target.options[event.target.selectedIndex].getAttribute("data-chief");
	document.getElementById("CAMPAIGN_T").value = event.target.options[event.target.selectedIndex].getAttribute("data-campaign-t");
	document.getElementById("CAMPAIGN_F").value = event.target.options[event.target.selectedIndex].getAttribute("data-campaign-f");
	
	document.getElementById("STAMP").value = event.target.options[event.target.selectedIndex].getAttribute("data-stamp");
	document.getElementById("LOGO").value = event.target.options[event.target.selectedIndex].getAttribute("data-logo");
	document.getElementById("SYMBOL").value = event.target.options[event.target.selectedIndex].getAttribute("data-symbol");
	
	HwpCtrl.PutFieldText("chief", !document.getElementById("BODY_CONTEXT_CHIEF").value ? " " : document.getElementById("BODY_CONTEXT_CHIEF").value);
}

function setLabel() {
}

function setFormInfoDraft() {
}

function checkForm(bTempSave) {
    if (bTempSave) {
        return true;
    } else {
        // 필수 입력 필드 체크
        return EASY.check().result;
    }
}

function setBodyContext(sBodyContext) {
	document.getElementById("SENDERMASTER").value = JSON.parse(sBodyContext).BODY_CONTEXT_SENDERMASTER;
}

//본문 XML로 구성
function makeBodyContext() {
    /*var sBodyContext = "";
    sBodyContext = "<BODY_CONTEXT>" + getFields("mField") + "</BODY_CONTEXT>";*/
	var bodyContextObj = {};
	var editorContent = { "tbContentElement" : document.getElementById("dhtml_body").value };
	bodyContextObj["BodyContext"] = $.extend(editorContent, getFields("mField"));
	
	//대외수신처
	var receiveCodes = [];
	var receiveNames = [];	
	$("#RECEIVEGOV_NAMES").val().length > 0 && $("#RECEIVEGOV_NAMES").val().split(";").map(function(item,index){
		var size = item.split(':').length;
		receiveCodes = receiveCodes.concat( item.split(':')[1] );
		receiveNames = receiveNames.concat( item.split(':')[size-1] );		 
	});
	bodyContextObj.BodyContext.receiver = receiveCodes.join(';');
	bodyContextObj.BodyContext.receiverName = receiveNames.join(',');
	if (getInfo("Request.isgovDocReply") == "Y") {
		bodyContextObj.BodyContext.govDocReply = "Y";
		bodyContextObj.BodyContext.govFormInstID = getInfo("Request.govFormInstID");
	}
	
    return bodyContextObj;
}

function setHwpFieldText() {
	if (formJson.Request.mode == "COMPLETE") {
		HwpCtrl.PutFieldText("docnumber", formJson.FormInstanceInfo.DocNo); // 등록번호
        HwpCtrl.PutFieldText("enforcedate", formJson.FormInstanceInfo.CompletedDate.substring(0, 10)); // 시행일자
	}
}

var callPacker = function(status){	
	try {
		HwpCtrl.MoveToField('body', true, true, true);
		HwpCtrl.SaveAs("test.xml", "PUBDOCBODY", "saveblock;", function (res) {
			//alert(res.downloadUrl);
			
	    	$.ajax({
				url: "/govdocs/service/callPacker.do", // local:govdocs, prd:covigovdocs
				type:"POST",
				data: {
					formInstId 	: formJson.FormInstanceInfo.FormInstID
					,processId 	: formJson.FormInstanceInfo.ProcessID
					,type 		: status || "send"
					,bodyUrl	: res.downloadUrl
					,bodySize	: res.size
//					,receiver 		: formJson.BodyContext.RECEIVE_INFO.split("^")[1]
//					,receiverName 	: formJson.BodyContext.RECEIVE_INFO.split("^")[0]
					,bodyUniqueID	: res.uniqueId,
					/*stampUrl: nullToBlank(formJson.BodyContext.STAMP) == "" ? "" : Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId(formJson.BodyContext.STAMP),
	    			logoUrl: nullToBlank(formJson.BodyContext.LOGO) == "" ? "" : Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId(formJson.BodyContext.LOGO),
	    			symbolUrl: nullToBlank(formJson.BodyContext.SYMBOL) == "" ? "" : Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId(formJson.BodyContext.SYMBOL)*/
					stampUrl: formJson.BodyContext.STAMP,
	    			logoUrl: formJson.BodyContext.LOGO,
	    			symbolUrl: formJson.BodyContext.SYMBOL
				},				
				success:function (data) { 
					data.status === "OK" && Common.Inform("발송되었습니다.","",function(){ 
						$("#btGovDocsSend").hide();
						$("#btGovDocsReSend").hide();
						opener.docFunc.refresh(); 
						window.close();
	                });
				},  
				error:function(response, status, error){ 
	                    Common.Inform("처리 실패하였습니다.", 'Information Dialog', null);
	                }
			});
			
		});
		HwpCtrl.MoveToField('body', true, true, false);

    } catch (e) {
        Common.Error(e.message);
    }	
}
function chkdocInfo(obj){
	if(getInfo("Request.templatemode") != "Read" || getInfo("Request.mode") == "REDRAFT"){
		var idx = '1';
		var id = $(obj).attr("id");
		var savedVal = "";
		
		if(id == "RELEASE_CHECK") {//공개여부
			savedVal = $(obj).find('input:checked').val();
			
			if(savedVal == "1"){
				$("#RELEASE_CHECK").val(savedVal);
				HwpCtrl.PutFieldText("publication", "공개");
				$("#chk_secrecy").prop("checked", false);
			}else{
				$("#RELEASE_CHECK").val(savedVal);
				HwpCtrl.PutFieldText("publication", "비공개");
				$("#chk_secrecy").prop("checked", "checked");
			}
			//document.getElementsByName("MULTI_"+id)[idx].value = savedVal;
			$(obj).val(savedVal);
		}
		else if(id == "RELEASE_CHECK_rec") {//공개여부
			savedVal = $(obj).find('input:checked').val();
			
			if(savedVal == "1"){
				$("#RELEASE_CHECK_rec").val(savedVal);
				//HwpCtrl.PutFieldText("publication", "공개");
				$("#chk_secrecy_rec").prop("checked", false);
			}else{
				$("#RELEASE_CHECK_rec").val(savedVal);
				//HwpCtrl.PutFieldText("publication", "비공개");
				$("#chk_secrecy_rec").prop("checked", "checked");
			}
			//document.getElementsByName("MULTI_"+id)[idx].value = savedVal;
			$(obj).val(savedVal);
		}
		//else if(id == "MANUAL_APV"){//종이접수공문
		else if(id == "CHK_MANUAL_APV"){
			//savedVal = $(obj).find("input").is(":checked");
			savedVal = $(obj).is(":checked");
			$("#CHK_MANUAL_APV").val(savedVal ? "Y" : "N");
			if(savedVal == true){
				savedVal = 'Y';
				$("#CHK_MANUAL_APV").prop("checked", "checked");
				$("#MANUAL_APV").val("Y");
				$("#RECEIVE_CHECK_indoc").prop("checked","checked");
				//document.getElementsByName("MULTI_RECEIVE_CHECK")[idx].value = "indoc";
				$("#btMultiReceive").hide();
				HwpCtrl.PutFieldText("recipient", "내부결재");
				$("#RECEIVE_CHECK_indoc").attr("disabled",true);
				$("#RECEIVE_CHECK_else").attr("disabled",true);
				Common.Warning(Common.getDic("msg_selectPaperScanfile"));//종이접수문서 스캔 파일을 첨부하여 기안해야 합니다.
			}else{
				savedVal = 'N';
				$("#CHK_MANUAL_APV").prop("checked", false);
				$("#MANUAL_APV").val("N");
				$("#RECEIVE_CHECK_else").prop("checked","checked");
				$("#btMultiReceive").show();
				HwpCtrl.PutFieldText("recipient", " ");
				$("#RECEIVE_CHECK_indoc").attr("disabled",false);
				$("#RECEIVE_CHECK_else").attr("disabled",false);
			}
			//document.getElementsByName("MULTI_MANUAL_APV")[idx].value = savedVal;
			//$(obj).val(savedVal);
		}
		else if(id == "RECEIVE_CHECK"){
			savedVal= $(obj).find("input:checked").val();
			if(savedVal == "indoc"){//내부결재
				HwpCtrl.MoveToField("recipient", true, true, false);
				HwpCtrl.PutFieldText("hrecipients", " ");
				HwpCtrl.PutFieldText("recipient", "내부결재");
				HwpCtrl.PutFieldText("recipients", " ");
				$("#btRecDept").hide();
			}
			else if(savedVal == "else"){//시행발송
				HwpCtrl.PutFieldText("recipient", " ");
				$("#btRecDept").show();
			}
			//document.getElementsByName("MULTI_"+id)[idx].value = savedVal;
		}	
		//$("#DOC_NO").val(opener.document.getElementById("DocNo").value);
	}
}