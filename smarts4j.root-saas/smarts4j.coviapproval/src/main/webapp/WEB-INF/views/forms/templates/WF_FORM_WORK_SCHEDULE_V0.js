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

function setWorkType(){
	CFN_CallAjax("/groupware/attendReq/getScheduleList.do", {}, function (data){ 
		var tarType = $('select[name="WORK_TYPE"]');
		$(data.list).each(function(i, v) {
			tarType.append($('<option/>', { 
				value: v.SchSeq,
				text : v.SchName 
			}));
		});
	}, false, 'json');
}

//양식별 후처리를 위한 필수 함수 - 삭제 시 오류 발생
function postRenderingForTemplate() {
	// 체크박스, radio 등 공통 후처리
	postJobForDynamicCtrl();

	//Subject 숨김처리
	$('#tblFormSubject').hide();
	
	$("#HID_COMPANTYCODE").val(Common.getSession("DN_Code"));
	
	setWorkType();
	
	//읽기 모드 일 경우
	if (getInfo("Request.templatemode") == "Read") {

		$('*[data-mode="writeOnly"]').each(function () {
			$(this).hide();
		});
		
		if (JSON.stringify(formJson.BodyContext) != "{}" && formJson.BodyContext != undefined) {
			XFORM.multirow.load(JSON.stringify(formJson.BodyContext.tblWorkScheduleInfo), 'json', '#tblWorkScheduleInfo', 'R');
		} else {
			XFORM.multirow.load('', 'json', '#tblWorkScheduleInfo', 'R');
		}

		//특정 디자인 수정
		$('#SCHEDULE_REASON').removeAttr('style');
	} else {
		$('*[data-mode="readOnly"]').each(function () {
			$(this).hide();
		});

		document.getElementById("InitiatedDate").value = formJson.AppInfo.svdt; // 재사용, 임시함에서 오늘날짜로 들어가게함.
		
		if (formJson.Request.mode == "DRAFT" || formJson.Request.mode == "TEMPSAVE") {
			document.getElementById("InitiatorOUDisplay").value = m_oFormMenu.getLngLabel(formJson.AppInfo.dpnm, false);
			document.getElementById("InitiatorCodeDisplay").value = m_oFormMenu.getLngLabel(formJson.AppInfo.usid, false);
			document.getElementById("InitiatorDisplay").value = m_oFormMenu.getLngLabel(formJson.AppInfo.usnm, false);
			
			if (CFN_GetQueryString("RequestFormInstID") != "undefined") {
				$("#HID_WORK_SCHEDULE_FIID").val(CFN_GetQueryString("RequestFormInstID"));
				$("#headname").text("근무일정 변경 신청서");
				getWorkScheduleData();
			}else{
				if (JSON.stringify(formJson.BodyContext) != "{}" && JSON.stringify(formJson.BodyContext) != undefined) {
					XFORM.multirow.load(JSON.stringify(formJson.BodyContext.tblWorkScheduleInfo), 'json', '#tblWorkScheduleInfo', 'W');
				} else {
					XFORM.multirow.load('', 'json', '#tblWorkScheduleInfo', 'W', { minLength: 1 });
				}
			}

            if (CFN_GetQueryString("RequestProcessID") != "undefined") {
				$("#HID_PROCESSID").val(CFN_GetQueryString("RequestProcessID"));
			}
		}else{
			if (JSON.stringify(formJson.BodyContext) != "{}" && JSON.stringify(formJson.BodyContext) != undefined) {
				XFORM.multirow.load(JSON.stringify(formJson.BodyContext.tblWorkScheduleInfo), 'json', '#tblWorkScheduleInfo', 'W');
			} else {
				XFORM.multirow.load('', 'json', '#tblWorkScheduleInfo', 'W', { minLength: 1 });
			}
		}
	}
}

function clickHolidayLabel(thisObj){
	var idx = $(".chkHolidayLabel").index(thisObj);
	
	if($("tr.multi-period").eq(idx).find(".chkHoliday").prop("checked")){
		chkCompanyHoliday(idx);
	}
}

function setLabel() {
}

function setFormInfoDraft() {
}

function checkForm(bTempSave) {
	if (document.getElementById("Subject").value == "") {
		document.getElementById("Subject").value = CFN_GetDicInfo(getInfo("AppInfo.usnm")) + " - " + CFN_GetDicInfo(getInfo("FormInfo.FormName"));
	}
	
	if (bTempSave) {
		return true;
	} else {
		if(document.getElementsByName("_MULTI_SCHEDULE_SDT").length <= 1){
			Common.Warning("근무 일정 기간을 입력해주세요.");
			return false;
		}
	}
	
	return true;
}

function getWorkScheduleData() {
	CFN_CallAjax("/approval/legacy/getFormInstData.do", {"FormInstID":$("#HID_WORK_SCHEDULE_FIID").val()}, function (data){ 
		receiveHTTPState(data); 
	}, false, 'json');
}

function receiveHTTPState(dataresponseXML) {
	var xmlReturn = dataresponseXML.Table;
	var errorNode = dataresponseXML.error;
	if (errorNode != null && errorNode != undefined) {
		Common.Error("Desc: " + $(errorNode).text());
	} else {
		$(xmlReturn).each(function (i, elm) {
			SetData(elm.BodyContext);
		});
	}
}

function SetData(pData) {
	var jsonObj = $.parseJSON(Base64.b64_to_utf8(pData)); // xml을 json으로 변경하기
	var ones = $$(Base64.b64_to_utf8(pData)).remove("tblWorkScheduleInfo").json();
	
	// 최하위노드를 찾아서  mField에 매핑
	GetLastNode(ones);
	
	//멀티로우 Table 값 매핑
	XFORM.multirow.load(JSON.stringify(jsonObj.tblWorkScheduleInfo), "json", "#tblWorkScheduleInfo", 'W');
}

function GetLastNode(obj) {
	for(var key in obj){
		if($$(obj).find(key).valLength()>0){
			GetLastNode($$(obj).find(key).json());
		}else{
			$("[id=" + key.toUpperCase() + "]").val($$(obj).attr(key));
			$("[id=" + key.toUpperCase() + "]").text($$(obj).attr(key));
		}
	}
}

function setBodyContext(sBodyContext) {
}

function validateScheduleDate() {
}

function chkCompanyHoliday(idx) {
	var sDate = $("input[name=_MULTI_SCHEDULE_SDT]").eq(idx).val();
	var eDate = $("input[name=_MULTI_SCHEDULE_EDT]").eq(idx).val();
	var companyHoliday = "";
	
	if(sDate != "" && eDate != ""){
		sDate = new Date(sDate);
		eDate = new Date(eDate);
		
		CFN_CallAjax("/covicore/anniversary/getAnniversaryList.do", {
			"domainID": Common.getSession("DN_ID"),
			"anniversaryType": "Company",
			"startYear": new Date().getFullYear()
		}, function (data){ 
			$(data.list).each(function(i, v) {
				var solarDate = new Date(v.SolarDate);
				
				if(sDate.getTime() <= solarDate.getTime() && eDate.getTime() >= solarDate.getTime()){
					companyHoliday += v.SolarDate + ";";
				}
			});
		}, false, 'json');
		
		$(".chkHoliday").eq(idx).closest("label").find("input[name=companyHoliday]").val(companyHoliday);
	}
}

function calSDATEEDATE(obj) {
	// 현재 객채(input) 에서 제일 가까이 있는 tr을 찾음
	var tmpObj = $(obj).closest("tr");
	var len = $("#tblWorkScheduleInfo tr.multi-row").length;
	
	var m_index;
	if (obj.name == "_MULTI_SCHEDULE_EDT") {
		m_index = $("input[name=_MULTI_SCHEDULE_EDT]").index(obj);
	} else {
		m_index = $("input[name=_MULTI_SCHEDULE_SDT]").index(obj);
	}
	
	var sDate = $("input[name=_MULTI_SCHEDULE_SDT]").eq(m_index).val();
	var eDate = $("input[name=_MULTI_SCHEDULE_EDT]").eq(m_index).val();
	
	if(sDate != "" && eDate != ""){
		if(sDate > eDate){
			Common.Warning(Common.getDic("msg_StartDateCannotAfterEndDate")); // 시작일은 종료일 보다 이후일 수 없습니다.
			$("input[name=_MULTI_SCHEDULE_SDT]").eq(m_index).val("");
			$("input[name=_MULTI_SCHEDULE_EDT]").eq(m_index).val("");
		}else{
			for(var i = 1; i < len; i++){
				if(m_index != i){
					var startDate = $("input[name=_MULTI_SCHEDULE_SDT]").eq(i).val();
					var endDate = $("input[name=_MULTI_SCHEDULE_EDT]").eq(i).val();
					
					if(((sDate >= startDate && sDate <= endDate) && (eDate >= startDate && eDate <= endDate))
							|| (sDate == startDate || sDate == endDate || eDate == startDate || eDate == endDate)
							|| (sDate <= startDate && eDate >= startDate && eDate <= endDate)
							|| (sDate >= startDate && sDate <= endDate && eDate >= endDate)
							|| (sDate <= startDate && eDate >= endDate)){
						Common.Warning("선택한 근무 일자가 기존에 지정한 일자에 포함됩니다.");
						$("input[name=_MULTI_SCHEDULE_SDT]").eq(m_index).val("");
						$("input[name=_MULTI_SCHEDULE_EDT]").eq(m_index).val("");
					}
				}
			}
		}
	}
	
	if($("input[name=_MULTI_SCHEDULE_SDT]").eq(m_index).val() != "" 
		&& $("input[name=_MULTI_SCHEDULE_EDT]").eq(m_index).val() != ""){
		if($(".chkHoliday").eq(m_index).prop("checked")){
			chkCompanyHoliday(m_index);
		}
	}
}

//본문 XML로 구성
function makeBodyContext() {
   	var bodyContextObj = {};
	bodyContextObj["BodyContext"] = getFields("mField");
	$$(bodyContextObj["BodyContext"]).append(getMultiRowFields("tblWorkScheduleInfo", "rField"));
	return bodyContextObj;
}
