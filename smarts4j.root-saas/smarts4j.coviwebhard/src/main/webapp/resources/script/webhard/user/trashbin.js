/*
 * trashbin.js
 * 새로고침, 뒤로가기, 사용자 이벤트 마다 로딩수준이 달라 분기처리
 * use: TrashbinBoxList.jsp 
 */

var boxinit = function(){
	if( treeObj.list.length === 0 ) return;
	
	treeObj.expandAll(2);
	
	//init viewtype
	["thumbnail","timeLine"].indexOf(localStorage.__coviWebhardView) > -1 && $("#btnAlbumView").addClass('active') && webhardView.changeView('thumbnail');
	localStorage.__coviWebhardView === "list" && $("#btnListView").addClass('active') && webhardView.changeView('list');
	localStorage.__coviWebhardView || $("#btnAlbumView").addClass('active') && webhardView.changeView('thumbnail'); 
	
	//title setting
	$("#driveTitle").text(Common.getDic("WH_trashbin")); // 휴지통
	
	//default button setting
	$("#btnCopy, #btnMove, #btnDownload, #btnRename").hide();
	$("#btnRestore").show();
	
	//contents init draw
	webhardView.init().setHideImportant();
	
	// 휴지통 검색
	var trashbinSearchKeyword = function(keyword){
		coviWebHard.getTrashbinBoxList($("#driveSelect option:selected").data("item")).done((function(data){
			var filter;
			if(Object.keys(keyword).length){
				filter = data.folderList.concat(data.fileList).filter(function(item){
					var name = item.FILE_NAME || item.FOLDER_NAME;
					if( keyword.input &&  keyword.dateTerm){
						return name.indexOf(keyword.input) > -1 && CFN_TransLocalTime(item.CREATED_DATE, "yyyyMMdd") >= keyword.dateTerm.start && CFN_TransLocalTime(item.CREATED_DATE, "yyyyMMdd") <= keyword.dateTerm.end
					}else if( keyword.input){
						return name.indexOf(keyword.input) > -1
					}else if( keyword.dateTerm){
						return CFN_TransLocalTime(item.CREATED_DATE, "yyyyMMdd") >= keyword.dateTerm.start && CFN_TransLocalTime(item.CREATED_DATE, "yyyyMMdd") <= keyword.dateTerm.end
					}
				});
			}else{
				filter = data.folderList.concat(data.fileList);
			}
			webhardView.render(filter);
		}).bind(this) );
	};
	
	//add Event
	$("#btnRefresh").off("click").on("click", function(){
		coviWebHard.getTrashbinBoxList($("#driveSelect option:selected").data("item")).done(function(data){
			webhardView.render( data.folderList.concat(data.fileList) );
			$(".body_important").remove();
		});
	}).trigger("click");

	$("#btnListView, #btnAlbumView").off("click").on("click", function(){	
		$(this).addClass('active');	
		this.id === "btnAlbumView" && $("#btnListView").removeClass('active') 	&& webhardView.changeView('thumbnail').init();
		this.id === "btnListView"  && $("#btnAlbumView").removeClass('active') 	&& webhardView.changeView('list').init().setHideImportant();		
		$("#btnRefresh").trigger("click");
	});
	
	// 입력창 우측 이벤트
	$("#nameSearchBtn").off("click").on("click", function(){
		var sObj = {};
		$.trim($("#nameSearchText").val()) && (sObj.input = $("#nameSearchText").val())
		trashbinSearchKeyword( sObj );
	});
	
	// 상세 검색 버튼
	$("#btnSearch").off("click").on("click", function(){
		var sObj = {};
		$.trim($("#searchText").val()) && (sObj.input = $("#searchText").val())
		if( $.trim($("#startDate").val()) && $.trim($("#endDate").val()) ){
			sObj.dateTerm = {};
			sObj.dateTerm.start = $("#startDate").val().replaceAll(".", "");
			sObj.dateTerm.end	= $("#endDate").val().replaceAll(".", "");
		}
		trashbinSearchKeyword( sObj );
	});
	
}

boxinit();
