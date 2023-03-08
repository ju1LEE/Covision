/* 웹하드 전체 자료 보기 : 연구 2팀 임준혁 */
(function() {
	// 헤더명 설정
	$("#driveTitle").text(Common.getDic("lbl_webhardTotalFile"));
	// 리스트 형, 앨범 형 보기 버튼 숨김 처리
	$("#btnListView, #btnAlbumView").hide();
	// 복사, 이동, 삭제, 다운로드, 이름바꾸기 버튼 숨김 처리
	$("#btnCopy, #btnMove, #btnDelete, #btnDownload, #btnRename").hide();
	// pageSize 셀렉트 박스 보이기
	$("#selectWebhardPageSize").show();
	
	// 페이지 데이터 조회 changView([album, list])
	webhardView.changeView("list").init().setConfigGrid({
		body: { onclick: function(){ redefineGridEvent.call(this) } }
	});
	
	// 검색 함수
	var searchKeyword = function(){
		var data = {};
		data.url = "/webhard/user/tree/getTotalFileData.do";
		data.pars = {"searchText" : $("#nameSearchText").val() || $("#txtTotalSearch").val() || $("#searchText").val(),
				     "startDate" : $("#startDate").val(),
				     "endDate" : $("#endDate").val()}
		webhardView.render(data);

		$("#nameSearchText").val("");
		$("#searchText").val("");
		$("#txtTotalSearch").val("");
		$("#startDate").val("");
		$("#endDate").val("");
	};
	
	// 그리드 데이터 컬럼 클릭 시 이벤트 발생 함수
	var redefineGridEvent = function(){
		switch( this.c ){
			case "0" : /* 분류 */
			case "1" : /* 경로 */
					var url;
					if(this.item.SHARED_TYPE == "FILE") {
						url = "/webhard/layout/user_SharedBoxList.do?CLSYS=webhard&CLMD=user&CLBIZ=Webhard&folderID="+ this.item.FOLDER_UUID +"&folderType=Shared";
						location.href = url;
					} else {
						url = "/webhard/layout/user_BoxList.do?CLSYS=webhard&CLMD=user&CLBIZ=Webhard" +
							"&boxID=" + this.item.BOX_UUID + "&folderID=" + this.item.FOLDER_UUID + "&folderType=Normal" ;
						location.href = url;
					}
				break; /* 분류 또는 경로를 클릭하면 해당 파일이 존재하는 디렉토리로 이동한다. */
			case "2" : /* 확장자 아이콘 */
				break;
			case "3" : /* 파일명 */ 
				if(this.item.TYPE === "file") {
					coviWebHard.fileDown("user", this.item.UUID);
				}
				break; /* 확장자 아이콘 및 파일명을 클릭하면 파일을 다운로드 한다. */
		}
	}
	
	// 새로고침 및 검색 기능
	$("#btnRefresh, #nameSearchBtn, #btnSearch").off("click").on("click", function() {
		searchKeyword();
	});
	
	$("#selectWebhardPageSize").on('change', function(){
		searchKeyword();
	});
	
	// 그리드 데이터 렌더링
	searchKeyword();
})();