/**
 * albumBoard - 게시판 웹파트 기본형 
 */
var albumBoard ={
	webpartType: '',
	init: function (data,ext){
		var folderID = ext.FolderID;
		albumBoard.getAlbumList(folderID);
		albumBoard.bindScroll();					//Album 웹파트 위/아래버튼 이벤트 바인드
	},
	
	getAlbumList : function(pFolderID){
		board.getBoardConfig(pFolderID);
		var params = {
			"boardType"	:	"Normal"
			,"bizSection":	"Board"
			,"folderID"	:	pFolderID
			,"folderType":	g_boardConfig.FolderType
			,"viewType"	: 	"Album"
			,"startDate": 	""
			,"endDate"	:	""
			,"pageSize"	:	10
			,"pageNo"	:	1
			,"sortBy"	: 	"MessageID desc"
		}
		$.ajax({
			type:"POST",
			url:"/groupware/board/selectMessageGridList.do",
			data: params,
			success:function(data){
				var listData = data.list;
				$("#albumBoard_messageList").html("");
				$.each( listData, function(i, item) {
					//게시판 목록 조회 팝업
					//var previewURL = item.FileID==undefined?"/GWStorage/no_image.jpg":String.format("/covicore/common/preview/{0}/{1}.do", "Board", item.FileID);
					var previewURL = Common.getThumbSrc("Board", item.FileID);
	            	var titleAnchor = $("<a href='#' onclick='defaultBoard.goImageSlidePopup("+item.MessageID+","+ item.FolderID+")'/>").append($("<img/>").attr("src", previewURL));
					var liWrap = $('<li />').append(titleAnchor);
					
					$("#albumBoard_messageList").append(liWrap);
				});
			}
		});
	},
	
	goImageSlidePopup : function(pMessageID, pFolderID) {
		//TODO: 팝업 제목 다국어처리 필요
		var contextParam = String.format("&messageID={0}&serviceType={1}&objectID={2}&objectType={3}", pMessageID, "Board", pFolderID, 'FD');
		CFN_OpenWindow("/covicore/control/goImageSlidePopup.do?" + contextParam, Common.getDic("lbl_ImageSliderShow"),800,780,"");
	},
	
	bindScroll : function(){
		$('.myContAlbum a.abTop').on('click', function(){
			albumBoard.moveScroll(Math.ceil(albumBoard.scrollIndex/2)-1);
		});
		
		$('.myContAlbum a.abDown').on('click', function(){
			albumBoard.moveScroll(Math.ceil(albumBoard.scrollIndex/2)+1);
		});
		
	},
	
	moveScroll : function(pIndex){
		if(pIndex < 0){
			pIndex = 0;
		} else if(pIndex > $('#albumBoard_messageList li').size()-1){
			pIndex = ($('#albumBoard_messageList li').size()/2)+1;
		}
		albumBoard.scrollIndex = pIndex;
		var scrollPosition = pIndex * 120;
		$('#albumBoard_messageList').parent().animate({
	        scrollTop : scrollPosition,
	    }, 200);
	}
}
