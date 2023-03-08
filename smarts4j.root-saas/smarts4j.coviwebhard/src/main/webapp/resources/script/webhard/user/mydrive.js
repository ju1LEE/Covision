/*
 * mydrive.js
 * 새로고침, 뒤로가기, 사용자 이벤트 마다 로딩수준이 달라 분기처리
 * use: BoxList.jsp
 */

var boxinit = function(){
	
	if( treeObj.list.length === 0 ) return;
	
	(function(){ //tree cursor init
		var boxID = CFN_GetQueryString("boxID") === "undefined" ? "" : CFN_GetQueryString("boxID");
		var folderID = CFN_GetQueryString("folderID") === "undefined" ? "" : CFN_GetQueryString("folderID");
		var treeCnt = treeObj.getCheckedTreeList().length;
		treeObj.expandAll(2);
		
		if(folderID){
			var bCheck = false;
			
			$(treeObj.list).each(function(i, obj){
				if(folderID === obj.UUID){
					treeObj.click(i, "open", true);
					bCheck = true;
					return false;
				}
			});
			
			if(!bCheck){
				treeObj.click(0);
			}
		}else if(treeCnt === 0){
			treeObj.click(0);
		}
		
	})();
	
	var tree = treeObj.getCheckedTreeList(0);
	
	//init viewtype
	["thumbnail","timeLine"].indexOf(localStorage.__coviWebhardView) > -1 && $("#btnAlbumView").addClass('active') && webhardView.changeView('thumbnail');
	localStorage.__coviWebhardView === "list" && $("#btnListView").addClass('active') && webhardView.changeView('list');
	localStorage.__coviWebhardView || $("#btnAlbumView").addClass('active') && webhardView.changeView('thumbnail'); 
	
	//title setting
	$("#driveTitle").text( tree.BOX_NAME || tree.FOLDER_NAME || Common.getDic("lbl_myDrive") );
	
	//default button setting
	$("#btnShare,#btnLink,#btnAddFolder").show();
	tree.__index && $("#btnPre").show();
	
	//contents init draw
	webhardView.init();
	
	
	
	//add Event
	$("#btnRefresh").off("click").on("click", function(){
		coviWebHard.getCheckedTreeData().done(function(data){
			webhardView.render( data.folderList.concat(data.fileList) ).bindDragEvent();
			
			// context - list
			$("#listGrid .gridBodyTr").unbind("contextmenu").bind("contextmenu", function(event, objSeq){
				event.preventDefault();
				
				var $target = $(event.target);
				var className = $target.closest(".gridBodyTr").find(".body_category span").attr("class");
				
				webhardView.resetCheckedList();
				$target.closest(".gridBodyTr").find("input[type=checkbox]").trigger("click");
				
				webhardContext.setContextType("list");
				webhardContext.init(className.indexOf("folder") > -1 ? "folder" : "file");
				webhardContext.setData(webhardView.getCheckedList()[0]);
				
				$target.closest(".gridBodyTr").find("input[type=checkbox]").trigger("click");
				
				$("#folderContextMenu").show();
				$("#folderContextMenu").css({top: (event.pageY - 70) + "px", left: (event.pageX - 70) + "px"});
			});
			
			// context - thumbnail
			$(".body_category").unbind("contextmenu").bind("contextmenu", function(event, objSeq){
				event.preventDefault();
				
				var $target = $(event.target);
				var className = $target.closest(".body_category").find("span").attr("class");
				var data = $target.closest(".body_category").parents(".WebHardList").data("item");
				
				webhardContext.setContextType("thumb");
				webhardContext.init(className.indexOf("folder") > -1 ? "folder" : "file");
				webhardContext.setData(data);
				
				$("#folderContextMenu").show();
				$("#folderContextMenu").css({top: (event.pageY - 70) + "px", left: (event.pageX - 70) + "px"});
			});
		});
	}).trigger("click");

	$("#btnListView, #btnAlbumView").off("click").on("click", function(){
		$(this).addClass('active');
		this.id === "btnAlbumView" && $("#btnListView").removeClass('active') 	&& webhardView.changeView('thumbnail').init();
		this.id === "btnListView"  && $("#btnAlbumView").removeClass('active') 	&& webhardView.changeView('list').init();		
		$("#btnRefresh").trigger("click");
	});
	
	//위로
	$("#btnPre").off("click").on("click", function(){
		var parentUuid = treeObj.getCheckedTreeList(0).PARENT_UUID
		var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === parentUuid })[0];
		treeObj.click(obj.__index);
	});
	
	// 입력창 우측 이벤트
	$("#nameSearchBtn").off("click").on("click", function(){
		var sObj = {};
		$.trim($("#nameSearchText").val()) && (sObj.input = $("#nameSearchText").val())
		webhardView.searchKeyword( sObj );
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
		webhardView.searchKeyword( sObj );
	});
	
}
boxinit();
