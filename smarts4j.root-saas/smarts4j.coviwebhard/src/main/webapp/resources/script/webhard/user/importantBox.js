/*
 * importantBox.js
 * 중요 문서함
 */

var boxinit = function(){
	// default button setting
	$("#btnListView, #btnAlbumView").hide();
	$("#btnRename").hide();
	if( treeObj.list.length === 0 ) return;
	treeObj.expandAll(2);
	
	//title
	$("#driveTitle").text( Common.getDic("lbl_ImportantFolder") ); // 중요 문서함

	// 중요문서함 북마크 토글.
	var setBookMark = function(element){
		var param = $.extend(element.data("item"), { BOOKMARK : element.hasClass("on") ? "N" : "Y" });
		coviWebHard.saveBookMark( param ).done(function(data){
			element.toggleClass("on");
		});
	}
	
	//grid Event
	var redefineGridEvent = function(){
		switch( this.c ){
			case "0" :
				break;
			case "1" :
				setBookMark( $(event.target).data('item',this.item) );	// 북마크 토글.
				break;
			case "2" :
				break;
			case "3" :
				if(event.target.tagName === "A"){
					if(this.item.TYPE === "folder"){
						var uuid = this.item.UUID;
						var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === uuid; })[0];
						
						$("#driveTitle").text(this.item.FOLDER_NAME);
						treeObj.click(obj.__index);
					}else{
						coviWebHard.fileDown("user", this.item.UUID);
					}
				}
				
				break;
		}
	}
	
	//pageInit
	webhardView.changeView("list").init().setConfigGrid({
		body: {
	          onclick: function(){ redefineGridEvent.call(this) }
			, oncheck: function(){ redefineGridEvent.call(this) }
	    }
	});
	
	// 중요 문서함 검색
	var importantBoxSearchKeyword = function(keyword){
		coviWebHard.getSelectImportantFileList($("#driveSelect option:selected").data('item')).done((function(data){
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
	
	// 새로고침
	$("#btnRefresh").off("click").on("click", function(){
		coviWebHard.getSelectImportantFileList($("#driveSelect option:selected").data('item')).done(function(data){
			webhardView.render( data.folderList.concat(data.fileList) );
			
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
			/*$(".body_category").unbind("contextmenu").bind("contextmenu", function(event, objSeq){
				event.preventDefault();
				
				var $target = $(event.target);
				var className = $target.closest(".body_category").find("span").attr("class");
				var data = $target.closest(".body_category").parents(".WebHardList").data("item");
				
				webhardContext.setContextType("thumb");
				webhardContext.init(className.indexOf("folder") > -1 ? "folder" : "file");
				webhardContext.setData(data);
				
				$("#folderContextMenu").show();
				$("#folderContextMenu").css({top: (event.pageY - 70) + "px", left: (event.pageX - 70) + "px"});
			});*/
		});
	}).trigger("click");
	
	// 입력창 우측 이벤트
	$("#nameSearchBtn").off("click").on("click", function(){
		var sObj = {};
		$.trim($("#nameSearchText").val()) && (sObj.input = $("#nameSearchText").val())
		importantBoxSearchKeyword( sObj );
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
		importantBoxSearchKeyword( sObj );
	});
}

boxinit();
