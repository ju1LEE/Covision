/*
 * publishedBox.js (based on sharedBox.jsp)
 * use: PublishedBox.jsp (공유한 폴더) 
 */

var boxinit = function(){

	if( treeObj.list.length === 0 ) return;
	treeObj.expandAll(2);		// 초기 진입 시, 내 드라이브는 2depth까지 열어둠.
	
	// 부서 드라이브 체크
	if($("#driveSelect :selected").data("item").OWNER_TYPE === "G"){
		treeObj.click(0);
	}
	
	//init viewtype
	["thumbnail","timeLine"].indexOf(localStorage.__coviWebhardView) > -1 && $("#btnAlbumView").addClass('active') && webhardView.changeView('thumbnail');
	localStorage.__coviWebhardView === "list" && $("#btnListView").addClass('active') && webhardView.changeView('list');
	localStorage.__coviWebhardView || $("#btnAlbumView").addClass('active') && webhardView.changeView('thumbnail'); 
	
	$("#driveTitle").text( Common.getDic("lbl_shareFolder") );		// 타이틀. 공유한 폴더.
	
	$("#btnCopy, #btnMove, #btnDelete, #btnRename").hide();		// 공유한 폴더에서는 '복사', '이동', '삭제', '이름바꾸기' 버튼 안보이게 함. dafault is show.

	//contents init draw
	webhardView.init().setHideImportant();		// 중요표시(북마크) 숨김. list type 의 중요 항목 숨김.	

	// 데이터 조회.
	this.getData = function(pFolderID, pSearchText, pStartDate, pEndDate) {
		var params = {
			"folderID" : pFolderID
			, "searchText" : pSearchText
			, "startDate" : pStartDate
			, "endDate" : pEndDate
		};
		coviWebHard.getPublishedData(params)
		.done(function(data) {
			webhardView.render(webhardView.modJson4Render(data.list));		// rendering은 webhard.js에서 '내 드라이브' 기준, jsonFormat 변경.
			webhardView.sharedAfterRendering();								// 각각의 폴더 및 파일 정보 로딩 후 추가 화면 처리.
		})
	}
	
	// 초기 데이터 및 화면 loading.
	$("#btnRefresh").on('click',function(){
		getData( $("#tmpUid").val(), "", "", "" ); 	// pFolderID, pSearchText, pStartDate, pEndDate
	}).trigger('click');

	// 리스트형, 앨범형 타입 보기 클릭 이벤트.
	$("#btnListView,#btnAlbumView").on('click',function(){
		$(this).addClass('active');	
		this.id === "btnAlbumView" && $("#btnListView").removeClass('active') 	&& webhardView.changeView('thumbnail').init();
		this.id === "btnListView"  && $("#btnAlbumView").removeClass('active') 	&& webhardView.changeView('list').init().setHideImportant();		

		var pSearchText = "";		// 검색 파라미터.
		var pStartDate = "";
		var pEndDate = "";
		// 상세 검색이 펼쳐져 있으면, 상세 검색으로 진행하며, 상단 우측의 텍스트 검색은 초기화함.
		if ( $("#searchDetail").hasClass("active") ) {
			$("#nameSearchText").val("");				// 상단 우측 텍스트 검색 초기화.
			pSearchText = $("#searchText").val();		// 상세 텍스트 내용으로 검색.
			pStartDate = $("#startDate").val().replaceAll(".", "-").substr(0,10);	// 검색 파라미터 수정.
			pEndDate = $("#endDate").val().replaceAll(".", "-").substr(0,10);
		} else {
			pSearchText = $("#nameSearchText").val();	// 상세 검색이 접혀져 있으면 우측 상단의 검색 텍스트로 조회.
		}

		// 검색.
		getData( $("#tmpUid").val(), pSearchText, pStartDate, pEndDate ); 	// pFolderID, pSearchText, pStartDate, pEndDate
	});
	
	// 상위폴더 이동(위로 버튼)
	$("#btnPre").on('click',function(){
		// 현재 폴더의 UUID를 통해 이전 폴더의 UUID 값을 조회.
		coviWebHard.getPrevFolder( $("#tmpUid").val() )
		.done(function(data1st) {
			$("#tmpUid").val(data1st.prev.FolderID);				// 구조변경으로 uuid값을 tmpUid에 임시 저장함.
			// 조회.
			getData(data1st.prev.FolderID, "", "", ""); 		// pFolderID, pSearchText, pStartDate, pEndDate
		});
	});

	// 상단 우측 검색창 클릭 이벤트.
	$("#nameSearchBtn").off("click").on("click", function(){
		// 상세 검색이 열려 있으면 초기화 및 닫아준다.
		if ( $("#searchDetail").hasClass("active") ) {
			$("#searchText").val("");		// 상세검색, 파일명.
			$("#startDate").val("");		// 상세검색, 시작날짜.
			$("#endDate").val("");			// 상세검색, 종료날자.
			$("#searchDetail").removeClass("active");
		}
		getData($("#tmpUid").val(), $("#nameSearchText").val(), "", "");	// pFolderID, pSearchText, pStartDate, pEndDate
	});

	// 상세 검색 버튼 이벤트.
	$("#btnSearch").off("click").on("click", function(){
		$("#nameSearchText").val("");		// 상세검색 진행 시, 입력문구가 있다면 삭제.

		var pSearchText = "";
		var pStartDate = "";
		var pEndDate = "";

		if ( $("#searchText").val().trim().length > 0 ) {		// 검색어.
			pSearchText = $("#searchText").val();
		}

		if ( ($("#startDate").val() !== "") && ($("#startDate").val() !== "") ) {  	// 날짜.
			pStartDate = $("#startDate").val().replaceAll(".", "-").substr(0,10);
			pEndDate = $("#endDate").val().replaceAll(".", "-").substr(0,10);
		}

		getData($("#tmpUid").val(), pSearchText, pStartDate, pEndDate);	// pFolderID, pSearchText, pStartDate, pEndDate
	});

}
boxinit();
