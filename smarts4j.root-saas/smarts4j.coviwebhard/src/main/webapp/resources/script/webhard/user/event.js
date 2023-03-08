
/*
 * event.js
 */


// 복사
$("#btnCopy").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	var folderUuids = [], fileUuids = [];
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}
	
	$(checkedList).map(function(idx, item){
		item.TYPE.toLocaleLowerCase() === "folder" ? folderUuids.push(item.UUID) : fileUuids.push(item.UUID);
	});
	
	webhardCommon.tree("copy", treeObj.getBoxUuid(), treeObj.getCheckedTreeList(0).UUID, fileUuids.join(";"), folderUuids.join(";"));
});

// 이동
$("#btnMove").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	var folderUuids = [], fileUuids = [];
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}
	
	$(checkedList).map(function(idx, item){
		item.TYPE.toLocaleLowerCase() === "folder" ? folderUuids.push(item.UUID) : fileUuids.push(item.UUID);
	});
	
	webhardCommon.tree("move", treeObj.getBoxUuid(), treeObj.getCheckedTreeList(0).UUID, fileUuids.join(";"), folderUuids.join(";"));
});

// 삭제
$("#btnDelete").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	var folderUuids = [], fileUuids = [];
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}
	
	$(checkedList).map(function(idx, item){
		item.TYPE.toLocaleLowerCase() === "folder" ? folderUuids.push(item.UUID) : fileUuids.push(item.UUID);
	});
	
	coviWebHard.delete(treeObj.getBoxUuid(), fileUuids.join(";"), folderUuids.join(";")).done(function(data){
		treeObj.refresh();
	});
});

// 복원
$("#btnRestore").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	var folderUuids = [], fileUuids = [];
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}
	
	$(checkedList).map(function(idx, item){
		item.TYPE.toLocaleLowerCase() === "folder" ? folderUuids.push(item.UUID) : fileUuids.push(item.UUID);
	});
	
	coviWebHard.restore(fileUuids.join(";"), folderUuids.join(";")).done(function(data){
		treeObj.refresh();
	});
});

// 다운로드
$("#btnDownload").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	var folderUuids = [], fileUuids = [];
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}
	
	$(checkedList).map(function(idx, item){
		item.TYPE.toLocaleLowerCase() === "folder" ? folderUuids.push(item.UUID) : fileUuids.push(item.UUID);
	});
	
	coviWebHard.fileDown("user", fileUuids.join(";"), folderUuids.join(";"));
});

// 공유
$("#btnShare").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}else if(checkedList.length > 1){
		Common.Warning(Common.getDic("msg_SelectOne")); // 한개만 선택되어야 합니다
		return false;
	}
	
	webhardCommon.share(checkedList[0].TYPE === "folder" ? "FOLDER" : "FILE", checkedList[0].UUID);
});

// 링크보내기
$("#btnLink").off("click").on("click", function(){
	var checkedList = webhardView.getCheckedList();
	
	if(checkedList.length === 0){
		Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}else if(checkedList.length > 1){
		Common.Warning(Common.getDic("msg_SelectOne")); // 한개만 선택되어야 합니다
		return false;
	}
	
	webhardCommon.link(checkedList[0].TYPE === "folder" ? "FOLDER" : "FILE", checkedList[0].UUID);
});

// 이름 바꾸기
$("#btnRename").off("click").on("click", function(event, data){
	if(data === undefined) { // 상단 '이름바꾸기' 버튼을 사용할 경우,
		var checkedList = webhardView.getCheckedList();
		
		if(checkedList.length === 0){
			Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
			return false;
		}else if(checkedList.length > 1){
			Common.Warning(Common.getDic("msg_SelectOne")); // 한개만 선택되어야 합니다
			return false;
		}
		
		if(checkedList[0].TYPE == "folder") {
			webhardCommon.addFolderPopup("edit", treeObj.getBoxUuid(), checkedList[0].UUID, "");
		} else {
			webhardCommon.addFolderPopup("edit_file", treeObj.getBoxUuid(), "", checkedList[0].UUID);
		}
	} else {  // 우클릭 컨텍스트인 경우
		var selectedData = data.selectedData;
		
		if(selectedData.TYPE == "folder") {
			webhardCommon.addFolderPopup("edit", selectedData.BOX_UUID, selectedData.UUID, "");
		} else {
			webhardCommon.addFolderPopup("edit_file", selectedData.BOX_UUID, "", selectedData.UUID);
		}
	}
});

// 폴더 추가
$("#btnAddFolder").off("click").on("click", function() {
	var boxID = CFN_GetQueryString("boxID");
	var folderID = CFN_GetQueryString("folderID");
	
	if(boxID === "" || boxID === undefined) return;
	
	webhardCommon.addFolderPopup("add",boxID, folderID);
});

/*
 * 우측 상단 검색
 */

// 달력 컨트롤 바인드
$("#selectSearch").coviCtrl("setDateInterval", $("#startDate"), $("#endDate"), "", {"changeTarget": "start"});

// 텍스트 입력 검색
$("#nameSearchText").off("keypress").on("keypress", function(){
	event.keyCode === 13 && $("#nameSearchBtn").trigger("click");
});

// 상세 검색
$("#searchText").off("keypress").on("keypress", function(){
	event.keyCode === 13 && $("#btnSearch").trigger("click");
});

// 상세 검색 펼치기
$("#btnDetails").off("click").on("click", function(){
	$("#btnDetails, #searchDetail").toggleClass("active");
});

/* userLeft 전체검색 기능*/
$("#btnSearchTotal").off("click").on("click", function() {
	var url = "/webhard/layout/user_TotalView.do?CLSYS=webhard"
		+ "&CLMD=user&CLBIZ=Webhard&folderType=Total";
		
	CoviMenu_GetContent(url);
});

$("#txtTotalSearch").off("keypress").on("keypress", function() {
	event.keyCode === 13 && $("#btnSearchTotal").trigger("click");
});