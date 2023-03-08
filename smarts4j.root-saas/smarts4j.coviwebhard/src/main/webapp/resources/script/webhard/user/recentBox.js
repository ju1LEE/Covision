/*
 * recentBox.js
 * 최근 문서함
 * 새로고침, 뒤로가기, 사용자이벤트 마다 로딩수준이 달라 분기처리
 *  
 */

var boxinit = function(){
	// default button setting
	$("#btnListView, #btnAlbumView").hide();
	$("#btnRename").hide();
	if( treeObj.list.length === 0 ) return;
	
	treeObj.expandAll(2);
	
	// title setting
	$("#driveTitle").text( Common.getDic("lbl_recentlyFolder") ); // 최근 문서함
		
	// contents init
	webhardView.changeView("timeLine").init();
	
	// 최근 문서함 검색
	var recentBoxSearchKeyword = function(keyword){
		coviWebHard.getSelectRecentFileList($("#driveSelect option:selected").data("item")).done((function(data){
			var filter;
			if(Object.keys(keyword).length){
				filter = data.list.filter(function(item){
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
				filter = data.list;
			}
			webhardView.render(filter);
		}).bind(this) );
	};
	
	// 새로고침
	$("#btnRefresh").off("click").on("click", function(){
		coviWebHard.getSelectRecentFileList($("#driveSelect option:selected").data("item")).done(function(data){
			webhardView.render( data.list );
			
			// context - thumbnail
			$(".WebHardList").unbind("contextmenu").bind("contextmenu", function(event, objSeq){
				event.preventDefault();
				
				var $target = $(event.target);
				var className = $target.closest(".WebHardList").find(".body_category span").attr("class");
				
				webhardView.resetCheckedList();
				$target.closest(".WebHardList").find("input[type=checkbox]").trigger("click");
				
				webhardContext.setContextType("list");
				webhardContext.init(className.indexOf("folder") > -1 ? "folder" : "file");
				webhardContext.setData(webhardView.getCheckedList()[0]);
				
				$target.closest(".WebHardList").find("input[type=checkbox]").trigger("click");
				
				$("#folderContextMenu").show();
				$("#folderContextMenu").css({top: (event.pageY - 70) + "px", left: (event.pageX - 70) + "px"});
			});
		});
	}).trigger("click");
	
	// 입력창 우측 이벤트
	$("#nameSearchBtn").off("click").on("click", function(){
		var sObj = {};
		$.trim($("#nameSearchText").val()) && (sObj.input = $("#nameSearchText").val())
		recentBoxSearchKeyword( sObj );
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
		recentBoxSearchKeyword( sObj );
	});
}

boxinit();
