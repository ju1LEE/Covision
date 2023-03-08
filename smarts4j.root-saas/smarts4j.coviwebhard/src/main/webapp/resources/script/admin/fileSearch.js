/*
 * fileSearch.js
 * use: webhardFileSearch.jsp
 */

var pageInit = function(){
	
	webhardAdminView.changeView("fileSearch").init().render();
	
	// 검색
	$("#inputFileType, #inputFileName").off("keypress").on("keypress", function(){
		event.keyCode === 13 && webhardAdminView.render();
	});
	
	// 검색 - 도메인 변경
	$("#selectDomain").off("change").on("change", function(){
		webhardAdminView.render();
	});
	
	// 삭제
	$("#btnDelete").off("click").on("click", function(){
		var checkedList = webhardAdminView.getCheckedList(0);
		
		if (checkedList.length > 0) {
			var fileUuids = checkedList.map(function(item){
				return item.UUID;
			}, []).join(";");
			
			webhardAdminCommon.deletePopup(fileUuids);
		} else {
			Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		}
	});
	
	// 다운로드
	$("#btnDownload").off("click").on("click", function(){
		var checkedList = webhardAdminView.getCheckedList(0);
		
		if (checkedList.length > 0) {
			var fileUuids = checkedList.map(function(item){
				return item.UUID;
			}, []).join(";");
			
			webhardAdmin.fileDown(fileUuids);
		} else {
			Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		}
	});
}

pageInit();