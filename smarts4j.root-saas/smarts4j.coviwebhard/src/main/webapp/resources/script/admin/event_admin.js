/*
 * event_admin.js
 */

// 검색
$("#btnSearch").off("click").on("click", function(){
	webhardAdminView.render();
});

// 검색 - 키 입력 이벤트
$("#inputSearchWord").off("keypress").on("keypress", function(){
	event.keyCode === 13 && $("#btnSearch").trigger("click");
});

// 컴포넌트 세팅
coviCtrl.renderDomainAXSelect('selectDomain', Common.getSession("lang"), '', '', '', false);

// 검색 조건
$("#selectSearchOption").bindSelect({
	options: [
		  {"optionValue": "",		"optionText": Common.getDic("lbl_all")} // 전체
		, {"optionValue": "name",	"optionText": Common.getDic("lbl_name")} // 이름
		, {"optionValue": "id",		"optionText": Common.getDic("lbl_executive_id")} // 아이디
	]
});

// 검색 - 기간
$("#selectPeriodOption").off("change").on("change", function(){
	if ($("#selectPeriodOption").val() === "manual") {
		$("#startDate, #endDate").val("") && $("#inputDate").show();
	} else {
		$("#inputDate").hide();
	}
});