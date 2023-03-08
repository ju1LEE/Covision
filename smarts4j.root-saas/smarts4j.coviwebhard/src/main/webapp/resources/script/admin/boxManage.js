/*
 * boxManage.js
 * use: webhardBoxManage.jsp
 */

var pageInit = function(){
	
	webhardAdminView.changeView("boxManage").init().render();
	
	// 새로고침
	$("#btnRefresh").off("click").on("click", function(){
		webhardAdminView.render();
	});
	
	// 검색 - 도메인 변경
	$("#selectDomain").off("change").on("change", function(){
		webhardAdminView.render();
	});
	
	// 박스 잠금
	$("#btnBoxLock").off("click").on("click", function(){
		var checkedList = webhardAdminView.getCheckedList(0);
		
		if (checkedList.length > 0) {
			var boxUuids = checkedList.map(function(item){
				return item.UUID;
			}, []).join(";");
			
			webhardAdmin.setBoxBlock(boxUuids).done(function(){
				webhardAdminView.render();
			});
		} else {
			Common.Inform(Common.getDic("msg_selectBox")); // 처리할 BOX를 선택하십시오.
		}
	});
	
	// 박스 삭제
	$("#btnDelete").off("click").on("click", function(){
		var checkedList = webhardAdminView.getCheckedList(0);
		
		if (checkedList.length > 0) {
			var boxUuids = checkedList.map(function(item){
				return item.UUID;
			}, []).join(";");
			
			webhardAdmin.boxDelete(boxUuids).done(function(){
				webhardAdminView.render();
			});
		} else {
			Common.Inform(Common.getDic("msg_selectBox")); // 처리할 BOX를 선택하십시오.
		}
	});
}

pageInit();