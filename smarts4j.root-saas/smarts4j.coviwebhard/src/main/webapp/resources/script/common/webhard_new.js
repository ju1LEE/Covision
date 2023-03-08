/*
 * webhard_new.js
 * 
 * */

// 조직도에서 보내는 postMessage() 부모창에서 수신.
window.addEventListener( 'message', function(e) {
	if ( (e.data.functionName === 'toParent') && (document.getElementById('sharePopup_if') != null) ) {
		var ifrChild = document.getElementById(e.data.param3).contentWindow;
		ifrChild.postMessage(
			{functionName : "toPopupShare", param1 : e.data.param1, param2 : e.data.param2 }
			, '*'
		);
	}
});

var coviWebHard = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	/* coviWebHard.uploadFile: 파일/폴더 업로드
	 * @method uploadFile
	 * @param pUploadData 파일/폴더 업로드 관련 정보
	 * */
	this.uploadFile = function(pUploadData){
		var fileMap = JSON.parse(pUploadData.fileMap);
		var showList = fileMap.showList;
		
		var formData = new FormData();
		$(pUploadData.files).each(function(idx, file){
			formData.append("files", file);
		});
		formData.append("fileMap", pUploadData.fileMap);
		
		$.ajax({
			url: "/webhard/common/uploadFile.do",
			type: "POST",
			data: formData,
			dataType: "json",
			enctype: "multipart/form-data",
			processData: false,
			contentType: false,
			success: function(res){
				if (res.status === "SUCCESS") {
					Common.Inform(Common.getDic("msg_UploadOk"), "Information", function(){ // 업로드 되었습니다.
						treeObj.refresh();
					});
		   		} else if (res.status === "NOT_OWNER") {
		   			Common.Warning(Common.getDic("msg_notHaveDriveOwnerAuth"), "Warning", function(){ // 해당 드라이브에 권한이 없는 사용자입니다.
		   				$("#Webhard_fileload_list_Window").remove();
		   			});
		   		} else if (res.status === "BOX_FULL") {
		   			Common.Warning(Common.getDic("msg_boxFull"), "Warning", function(){ // 웹하드 용량이 가득 찼습니다.<BR />필요 없는 파일을 완전 삭제하고 다시 시도해 주십시오.
		   				$("#Webhard_fileload_list_Window").remove();
		   			});
		   		} else if (res.status === "UPLOAD_MAX") {
		   			Common.Warning(Common.getDic("msg_fileUploadMax"), "Warning", function(){ // 업로드 하려는 파일이 최대 업로드 크기보다 큽니다.<br/>파일의 크기를 줄인 후 다시 시도해 주십시오.
		   				$("#Webhard_fileload_list_Window").remove();
		   			});
		   		} else {
		   			Common.Warning(Common.getDic("msg_sns_11")); // 업로드에 실패했습니다.
		   		}
			},
			error: function(err){
				if(!xhrProgress.isaborted){
					Common.Warning(Common.getDic("msg_sns_11")); // 업로드에 실패했습니다.
			   		console.log(err);
				}
			},
			xhr: function(){
				xhrProgress.setProgress(showList);
				
				return xhrProgress;
			}
		});
	},
	
	/* coviWebHard.fileDown: 파일/폴더 다운로드
	 * @method fileDown
	 * @param pMode: 다운로드 모드 (user: 사용자, link: 링크)
	 * @param pFileUuids: 파일 UUID (ex. uuid01;uuid02;uuid03)
	 * @param pFolderUuids: 폴더 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.fileDown = function(pMode, pFileUuids, pFolderUuids){
		var $form = $("<form />", {"action": "/webhard/common/fileDown.do", "method": "POST"});
		
		$form.append($("<input />", {"name": "mode", "value": pMode ? pMode : "user"}));
		$form.append($("<input />", {"name": "fileUuids", "value": pFileUuids ? pFileUuids : ""}));
		$form.append($("<input />", {"name": "folderUuids", "value": pFolderUuids ? pFolderUuids : ""}));
		$form.appendTo("body");
		
		$form.submit();
		$form.remove();
	},
	
	/* coviWebHard.delete: 파일/폴더 삭제
	 * @method delete
	 * @param pBoxUuid: 박스 UUID
	 * @param pFileUuids: 파일 UUID (ex. uuid01;uuid02;uuid03)
	 * @param pFolderUuids: 폴더 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.delete = function(pBoxUuid, pFileUuids, pFolderUuids){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/delete.do",
			type: "POST",
			data: {
				folderType: (CFN_GetQueryString("folderType") === "undefined" ? "Normal" : CFN_GetQueryString("folderType")),
				boxUuid: pBoxUuid,
				fileUuids: (pFileUuids ? pFileUuids : ""),
				folderUuids: (pFolderUuids ? pFolderUuids : "")
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* coviWebHard.restore: 파일/폴더 복원
	 * @method restore
	 * @param pFileUuids 파일 UUID (ex. uuid01;uuid02;uuid03)
	 * @param pFolderUuids 폴더 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.restore = function(pFileUuids, pFolderUuids){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/restore.do",
			type: "POST",
			data: {
				fileUuids: (pFileUuids ? pFileUuids : ""),
				folderUuids: (pFolderUuids ? pFolderUuids : "")
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* coviWebHard.copy: 파일/폴더 복사
	 * @method copy
	 * @param pSources 복사하고자 하는 파일/폴더 UUID (ex. {fileUuids: fileUuid01;fileUuid02;fileUuid03;..., folderUuids: folderUuid01;folderUuid02;folderUuid03;..., boxUuid: (...), folderUuid: (...)})
	 * @param pTargets 복사되는 위치 정보 (ex. {boxUuid: (...), folderUuid: (...)})
	 * */
	this.copy = function(pSources, pTargets){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/copy.do",
			type: "POST",
			data: {
				sources: JSON.stringify(pSources),
				targets: JSON.stringify(pTargets)
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* coviWebHard.move: 파일/폴더 이동
	 * @method move
	 * @param pSources 이동하고자 하는 파일/폴더 UUID (ex. {fileUuids: fileUuid01;fileUuid02;fileUuid03;..., folderUuids: folderUuid01;folderUuid02;folderUuid03;..., boxUuid: (...), folderUuid: (...)})
	 * @param pTargets 이동되는 위치 정보 (ex. {boxUuid: (...), folderUuid: (...)})
	 * */
	this.move = function(pSources, pTargets){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/move.do",
			type: "POST",
			data: {
				sources: JSON.stringify(pSources),
				targets: JSON.stringify(pTargets)
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* coviWebHard.getCheckedTreeData: 선택된 트리의 폴더/파일 가져오기
	 * @method getCheckedTreeData
	 * @param 
	 * */
	this.getCheckedTreeData = function(){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/tree/getCheckedTreeData.do",
			type: "POST",
			data: treeObj.getCheckedTreeList(0),
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
	 	return deferred.promise();
	}
	
	/* coviWebHard.saveBookMark: 북마크 기능
	 * @method saveBookMark
	 * @param dataSet
	 * */
	this.saveBookMark = function( chkData ){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/saveBookMark.do",
			type: "POST",
			data: chkData,
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
	 	return deferred.promise();
	}
	
	/* coviWebhard.getSelectRecentFileList: 최근 문서 가져오기
	 * @method getSelectRecentFileList
	 * @param dataSet
	 * */
	this.getSelectRecentFileList = function(boxData){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/getSelectRecentFileList.do",
			type: "POST",
			data: boxData,
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
	/* coviWebHard.getSelectImportantFileList: 중요 체크 파일 가져오기
	 * @method getSelectImportantFileList
	 * @param dataSet
	 * */
	this.getSelectImportantFileList = function(boxData){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/getSelectImportantFileList.do",
			type: "POST",
			data: boxData,
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
	/* coviWebHard.getTrashbinBoxList: 휴지통 목록 가져오기
	 * @method getTrashbinBoxList
	 * @param dataSet
	 * */
	this.getTrashbinBoxList = function(boxData){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/getTrashbinBoxList.do",
			type: "POST",
			data: boxData,
			success: function(data){ deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
	/* coviWebHard.getExtConfig: 업로드 제한 확장자 조회
	 * @method getExtConfig
	 * */
	this.getExtConfig = function(){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/getExtConfig.do",
			type: "POST",
			data: {},
			success: function(data){ deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
	/* coviWebHard.getUsageWebHard: 웹하드 사용량 조회
	 * @method getUsageWebHard
	 * @params pBoxUuid BOX UUID
	 * */
	this.getUsageWebHard = function(pBoxUuid){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/getUsageWebHard.do",
			method: "POST",
			data: {
				"boxUuid": pBoxUuid ? pBoxUuid : ""
			},
			success: function(data){ deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
	/* coviWebHard.getDomainMaxUploadSize: 도메인 별 업로드 최대 사이즈 조회
	 * @method getUsageWebHard
	 * */
	this.getDomainMaxUploadSize = function(){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/user/getDomainMaxUploadSize.do",
			method: "POST",
			data: {},
			success: function(data){ deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
	
	var xhrProgress = function(){ // uplaod progress
		
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var xhr = $.ajaxSettings.xhr();
		var fIdx = 0;
		var showList;
		var bound = 0;
		
		xhr.isaborted = false;
		
		xhr.setProgress = function(pShowList){
			fIdx = 0;
			showList = pShowList;
			bound = showList[fIdx].size;
			
			close();
			setXhrEvent();
		};
		
		var drawView = function(){
			var $fragment = $(document.createDocumentFragment());
			
			var $aside = $("<aside/>", {"id": "Webhard_fileload_list_Window", "class": "WebHard_fileload_list"})
							.append($("<div/>", {"class": "fileloadTit"})
								.append($("<span/>", {"id": "progressTitle"}))
								.append($("<a/>", {"href": "#", "class": "close", "text": Common.getDic("btn_Close")}))) // 닫기
							.append($("<div/>", {"id": "fileUploadStatus", "class": "fileloadSTit"})
								.append($("<span/>", {"id": "progressBar"}))
								.append($("<a/>", {"href": "#", "class": "cancel", "text": Common.getDic("btn_Cancel")}))); // 취소
			var $ul = $("<ul/>", {"id": "fileloadList", "class": "fileloadList"});
			
			close();
			$aside.append($ul);
			$fragment.append($aside);
			$("#folderContextMenu").after($fragment);
			
			var $li = $("<li/>");
			
			$.each(showList, function(index, item){
				var sName = item.name;
				var sExt = sName.split(".").pop();
				var $p = $("<p/>", {"class": "fName"})
							.append($("<span/>", {"class": getIconClass(sExt), "text": sName}))
							.append($("<img/>", {"id": "progressStatus"+index, "src": "/webhard/resources/images/ic_loading.png"}));
				
				$li.append($p);
				$("#progressTitle").text(String.format(Common.getDic("lbl_uploadingItems"), showList.length)); // {0}개 항목 업로드 중
			});
			
			$("#fileloadList").append($li);
			
			$("#dupe_overlay").remove();
		};
		
		var setEvent = function(){
			xhr.isaborted = false;
			
			// 취소 이벤트
			$("#Webhard_fileload_list_Window .cancel").on("click", function(){
				xhr.isaborted = true;
				
				// error 윈도우 방지용 flag
				// abort시 error, xhr.onload, xhr.abort 순으로 이벤트 발생
				$("#progressTitle").text(String.format(Common.getDic("lbl_cancelUploadedItems"), showList.length)); // {0}개 항목 업로드 취소
				$("#fileUploadStatus").remove();
				Common.Inform(Common.getDic("msg_uploadCanceled")); // 업로드가 취소 되었습니다.
				xhr.abort();
			});
			
			// 닫기 이벤트
			$("#Webhard_fileload_list_Window .close").on("click", close);
		};
		
		var setXhrEvent = function(){
			xhr.upload.onloadstart = function(e){
				drawView();
				setEvent();
			};
			
			xhr.upload.onprogress = function(e){
				$("#progressBar").text(String.format("{0}... ({1}%)", Common.getDic("CPMail_FileUploading"), Math.floor(e.loaded / e.total * 100))); // 업로드 중
				
				// Upload Progress
				if(e.loaded >= bound && fIdx < showList.length){
					$("#progressStatus" + (fIdx++)).attr("src", "/webhard/resources/images/ic_done.png");
					if(fIdx < showList.length) bound = showList[fIdx].size;
				}
			};
			
			xhr.upload.onloadend = function(e){
				$("#progressBar").text(Common.getDic("lbl_finishingUpload")); // 업로드 마무리중
			};
			
			xhr.onloadend = function(e){
				for(var i = 0; i < showList.length; i++){
					$("#progressStatus" + i).attr("src","/webhard/resources/images/ic_done.png");
				}
				
				$("#progressTitle").text(String.format(Common.getDic("lbl_uploadedItems"), showList.length)); // {0}개 항목 업로드 완료
				$("#fileUploadStatus").remove(); // 업로드 완료 시 취소 버튼 제거.
			};
		};
		
		var close = function(){
			$("#Webhard_fileload_list_Window").remove();
		};
		
		var getIconClass = function(ext){
			var strReturn = "";
			var lowerExt = ext.toLowerCase();
			
			if (lowerExt === "xls" || lowerExt === "xlsx") {
				strReturn = "fNameexcel";
			} else if (lowerExt === "jpg" || lowerExt === "jpeg" || lowerExt === "png" || lowerExt === "bmp") {
				strReturn = "attImg";
			} else if (lowerExt === "doc" || lowerExt === "docx") {
				strReturn = "word";
			} else if (lowerExt === "ppt" || lowerExt === "pptx") {
				strReturn = "ppt";
			} else if (lowerExt ==="pdf" || lowerExt === "ppdf") {
				strReturn = "pdf";
			} else if (lowerExt === "zip" || lowerExt === "gz" || lowerExt === "rar" || lowerExt === "tar") {
				strReturn = "zip";
			} else {
				strReturn = "default";
			}
			
			return strReturn;
		};
		
		return xhr;
	}();
	
	// coviWebHard.getSharedData: 공유받은 폴더 데이터 가져오기
	this.getSharedData = function(params){
		var searchParam = {
			"folderID": ((params.folderID === '' || params.folderID === undefined) ? "ROOT": params.folderID)
			, "searchText" : params.searchText
			, "startDate" : (params.startDate === "") ? "" : CFN_TransServerTime(params.startDate + " 00:00:00")
			, "endDate" : (params.endDate === "") ? "" : CFN_TransServerTime(params.endDate + " 23:59:59")
		};
		
		var deferred = $.Deferred();
		$.ajax({
			url : "/webhard/user/getSharedData.do"
			, type : "POST"
			, data : searchParam
			, success : function(data) {deferred.resolve(data);} 
		 	, error : function(response, status, error) {deferred.reject(status);}
		});
		return deferred.promise();
	}
	
	// coviWebHard.getPublishedData : 공유한 폴더 데이터 가져오기
	this.getPublishedData = function(params){
		// 21.10.18 : 사용자 화면에는 CFN_TransLocalTime()을 사용하여 사용자의 Timezone으로 변환하여 보여주고,
		// DB 데이터를 비교할 때는 CFN_TransServerTime()으로 변환.
		// 값이 들어있을 때만, 시:분:초 추가 입력.
		var searchParam = {
			"folderID": ((params.folderID === '' || params.folderID === undefined) ? "ROOT": params.folderID)
			, "searchText" : params.searchText
			, "startDate" : (params.startDate === "") ? "" : CFN_TransServerTime(params.startDate + " 00:00:00")
			, "endDate" : (params.endDate === "") ? "" : CFN_TransServerTime(params.endDate + " 23:59:59")
		};
		var deferred = $.Deferred();
		$.ajax({
			url : "/webhard/user/getPublishedData.do"
			, type : "POST"
			, data : searchParam
			, success : function(data) {deferred.resolve(data);} 
		 	, error : function(response, status, error) {deferred.reject(status);}
		});
		return deferred.promise();
	}

	// coviWebHard.getPrevFolder : 이전 폴더로 되돌아 가기. pFolderID : 현재 폴더의 UUID. 
	this.getPrevFolder = function(pFolderID) {
		var deferred = $.Deferred();
		var params = { "folderID" : pFolderID
						, "folderType" : CFN_GetQueryString("folderType")}
		$.ajax({
			url : "/webhard/user/prevFolder.do"
			, type : "POST"
			, data : params
			, success : function(data) {deferred.resolve(data);} 
		 	, error : function(response, status, error) {deferred.reject(status);}
		});
		return deferred.promise();
	}
	
}();



var webhardView = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	var loadThumbImage = function(file_uuid) {
		return "/webhard/user/thumbnail/"+file_uuid+".do";
	} // 이미지 파일인 경우, 아이콘 대신 이미지가 보이게 하기 위한 함수
	
	//view common
	var getIconClass = function(type){
		var iconClass = "ic_etc";
	//	[ "jpg","png","gif","bmp","jpeg" ].indexOf( type ) > -1 && ( iconClass = "ic_image" );
		[ "pdf","ppdf" ].indexOf( type ) > -1 && ( iconClass = "ic_pdf" );
		[ "xlsx","xls" ].indexOf( type ) > -1 && ( iconClass = "ic_xls" );
		[ "ppt","pptx" ].indexOf( type ) > -1 && ( iconClass = "ic_ppt" );
		[ "zip","tar","gz","rar" ].indexOf( type ) > -1 && ( iconClass = "ic_zip" );
		[ "docx","doc" ].indexOf( type ) > -1 && ( iconClass = "ic_word" );
		return iconClass;
	}
	
	var convertFileSize = function(pSize){
		var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
	    if (pSize == 0) return 'n/a';
	    var i = parseInt(Math.floor(Math.log(pSize) / Math.log(1024)));
	    return (pSize / Math.pow(1024, i)).toFixed(1) + sizes[i];
	}
	
	var setBookMark = function(element){
		var param = $.extend(element.data("item"), { BOOKMARK : element.hasClass("on") ? "N" : "Y" });
		coviWebHard.saveBookMark( param ).done(function(data){
			element.toggleClass("on");
		});
	}
	
	var contentTemplate = function(){
		var $WebHardList	= $("<div>"	, { "class" : "WebHardList" });
		var $check			= $("<span>", { "class" : "WebHardList_chk chkStyle01" });
		var $body_important	= $("<div>"	, { "class" : "body_important" });
		var $body_category	= $("<div>"	, { "class" : "body_category" });
		var $body_title		= $("<a>"	, { "class" : "body_title" });
	//	var $body_size 		= $("<p>"	, { "class" : "body_size" });
		var $body_date		= $("<p>"	, { "class" : "body_date" });
		
		return $WebHardList
				.append( $check )
				.append( $body_important )
				.append( $body_category )
				.append( $body_title )
				//.append( $body_size )
				.append( $body_date );
	}
	
	// Drag&Drop Upload
	var dropPage = function(){
		var $DnDWrap 	= $("<div>",{ "class" : "DnDWrap" });
		var $DnDarea01 	= $("<div>",{ "class" : "DnDarea01", "style": "pointer-events: none; z-index: 5;" });
		var $DnDtextbox = $("<div>",{ "class" : "DnDtextbox", "style": "pointer-events: none; z-index: 5; max-height: 54px;" }).append($("<span>",{ "text" : Common.getDic("msg_dropFileUpload") })); // 파일을 업로드 하려면 여기에 드롭하세요.
		return $DnDWrap.append( $DnDarea01 ).append( $DnDtextbox );
	} 
	
	var uuidv4 = function uuidv4(){
		return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c){
			var r = coviCmn.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
			return v.toString(16);
		});
	}
	
	var scanDir = function(item){
		var getFile = function(fileEntry){
			try {
		    	return new Promise(function(resolve,reject){
		    		fileEntry.file(function(file){ resolve( file ); });
		    	});
			} catch(err) {
		    	console.log(err);
			}
		}
		
		var readentry = function(directoryReader, item){
			var promiseList = new Array();
			item && promiseList.push(item);
			return new Promise(function(resolve, reject){
				directoryReader.readEntries(function(entries){
					entries.length > 0 && promiseList.push(readentry(directoryReader));
					entries.forEach(function(entry){
						if (entry.isDirectory) {
							promiseList.push( scanDir(entry) );
						} else {
							promiseList = promiseList.concat( getFile(entry) );
						}
					});
					Promise.all(promiseList).then(function(value){
						resolve(value);
					});
				});
			});
		}
		
		return new Promise(function(resolve, reject){
			var directoryReader = item.createReader();
			resolve(readentry(directoryReader, item));
		});
	}
	
	var dropFile = function(fileList, folderInfo){
		var returnList = [];
		var param = {
			  "boxUUID": folderInfo.BOX_UUID
			, "targetFolder": folderInfo.UUID
		};
		
		if(_chrome || _firefox){
			var items = fileList.items;
			var itemLen = items.length;
			
			for(var i = 0; i < itemLen; i++){
				var item = items[i].webkitGetAsEntry();
				
				if(item){
					if(item.isDirectory){ // folder drag
						scanDir(item).then(function(rtn){
							var dirFullPath, dirName;
							var folderPathUUIDMap = {};
							var folderSize = 0;
							var filter = function(files){
								return files.reduce(function(acc, cur, idx, arr){
									if( Array.isArray(cur) ){
										var obj = filter( cur );
										acc.files = acc.files.concat( obj.files );
										acc.directories = acc.directories.concat( obj.directories );
										acc.fileInfos = acc.fileInfos.concat( obj.fileInfos );
										return acc;
									}else{
										if( cur.isDirectory ){ // add directories
											var _pathArr = cur.fullPath.split("/");
											var _folderNm = _pathArr.pop();
											
											folderPathUUIDMap[cur.fullPath] = ( _pathArr.length === 1 && "/"+uuidv4() ) || (folderPathUUIDMap[cur.fullPath] || folderPathUUIDMap[_pathArr.join('/')]+"/"+uuidv4());
											dirFullPath = cur.fullPath;
											
											var _uuidPathArr = folderPathUUIDMap[cur.fullPath].split('/');
											var _uuid = _uuidPathArr.pop();
											dirName = _uuidPathArr.pop();
											
											acc.directories = acc.directories.concat({
												folderPath		:	folderPathUUIDMap[cur.fullPath]
												,folderNamePath	:	dirFullPath
												,folderName 	: 	cur.name
												,UUID			: 	_uuid
												,parentUUID		:	dirName ? dirName : (param.targetFolder ? param.targetFolder : "/")
												,boxUUID		:	param.boxUUID
											});
										}else{ // add files, fileinfo
											var fileUUID = uuidv4();
											folderSize += cur.size;
											acc.fileInfos = acc.fileInfos.concat({
												filePath 	:	folderPathUUIDMap[dirFullPath] + "/" + fileUUID
												,folderUUID	:	folderPathUUIDMap[dirFullPath].split('/').pop()
												,UUID		:	fileUUID
												,fileName 	: 	cur.name
												,boxUUID	:	param.boxUUID
												,size		:	cur.size
											});
											acc.files = acc.files.concat(cur);
										}
										return acc;
									}
								}, { files : [], directories : [], fileInfos : [] })
							}
							
							var rtn = filter(rtn);
							var returnObj = {
								  "files": rtn.files
								, "directories": rtn.directories
								, "fileInfos": rtn.fileInfos
								, "showList": {
									  "name": rtn.directories[0].folderName
									, "size": folderSize
								}
							}
							
							returnList.push(returnObj);
							uploadFile(returnList);
						});
					}else{ // file drag
						item.file(function(file){
							var fName = file.name;
							var sExtension = fName.split('.').pop();
							var fileUUID = uuidv4();
							
							var returnObj = {
								  "files": [file]
								, "fileInfos": [{
									  filePath		: 	"/" + fileUUID
									, folderUUID	:	param.targetFolder || ""
									, UUID			:	fileUUID
									, fileName		: 	fName
									, boxUUID		:	param.boxUUID
									, size			:	file.size
								}]
								, "showList": {
									  "name": file.name
									, "size": file.size
								}
							}
							
							returnList.push(returnObj);
							itemLen === returnList.length && uploadFile(returnList);
						});
					}
				}
			}//for end
		}else{ // ie etc...
			var fileList = fileList.files;
			
			if(fileList.length === 0){
				Common.Warning(Common.getDic("msg_notSupportFolderUpload")); // 사용중인 브라우저는 폴더 업로드를 지원하지 않습니다.
				return false;
			}
			
			$(fileList).each(function(idx, file){
				var fName = file.name;
				var sExtension = fName.split('.').pop();
				var fileUUID = uuidv4();
				
				var returnObj = {
					  "files": [file]
					, "fileInfos": [{
						  filePath		: 	"/" + fileUUID
						, folderUUID	:	param.targetFolder || ""
						, UUID			:	fileUUID
						, fileName		: 	fName
						, boxUUID		:	param.boxUUID
						, size			:	file.size
					}]
					, "showList": {
						  "name": file.name
						, "size": file.size
					}
				}
				
				returnList.push(returnObj);
			});
			
			uploadFile(returnList);
		}
	}
	
	var uploadFile = function(uploadList){
		var uploadData = {
			"files": []
		};
		var obj = {
			  fileInfos		: []
			, directories	: []
			, showList		: []
		};
	
		uploadList.map(function(item){
			item.directories	&& ( obj.directories		= obj.directories.concat(item.directories) );
			item.fileInfos		&& ( obj.fileInfos			= obj.fileInfos.concat(item.fileInfos) );
			item.showList		&& ( obj.showList			= obj.showList.concat(item.showList) );
			item.files			&& ( uploadData["files"]	= uploadData["files"].concat(item.files.map(function(file){ return file; }, [])) );
		});
		
		uploadData["fileMap"] = JSON.stringify(obj);
		
		// 파일명, 확장자 체크
		coviWebHard.getExtConfig().done(function(data){
			var bCheck = true;
			var fileDisenableName = Common.getBaseConfig("DisenableFileName").split("|").filter(function(fileName){
				return fileName ? true : false;
			}, []);
			var config = data.ext ? data.ext.split(",").map(function(ext){
				return ext.trim();
			}, []) : "";
			
			$(obj.fileInfos).each(function(idx, item){
				var fArr = item.fileName.split(".");
				var ext = fArr.pop();
				var fileName = fArr.join(".");
				
				bCheck = !(config.includes(ext) || fileDisenableName.includes(fileName));
				
				if(!bCheck) return false;
			});
			
			if(bCheck){
				// 도메인 별 최대 업로드 용량 체크
				coviWebHard.getDomainMaxUploadSize().done(function(data){
					if (data.status === "SUCCESS") {
						var totalSize = 0;
						var maxUploadSize = data.maxUploadSize * 1024 * 1024;
						
						uploadList.forEach(function(item){
							item.files.forEach(function(file){
								totalSize += file.size;
							});
						});
						
						if (totalSize <= maxUploadSize) {
							coviWebHard.uploadFile(uploadData);
						} else {
							Common.Warning(String.format("{0}<br>{1}: {2}", Common.getDic("msg_fileUploadMax"), Common.getDic("lbl_maxUploadSize"), convertFileSize(maxUploadSize)));
							// 업로드 하려는 파일이 최대 업로드 크기보다 큽니다.<br/>파일의 크기를 줄인 후 다시 시도해 주십시오.<br>최대 업로드 크기
						}
					}
				});
			}else{
				Common.Warning(String.format("{0}<br>=> {1}", Common.getDic("msg_ExistLimitedExtensionFile"), data.ext)); // 업로드가 제한된 확장자의 파일이 있습니다.
			}
		});
	}
	
	//view common end
	
	
	//thumbnailView
	var thumbnailView = function(){
		
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var setlayout = function(){
			var $fragment 		= $(document.createDocumentFragment());
			var $folderTitle	= $("<div>", { "class" : "WebHardTitleLine" }).append($("<p>",{ "class" : "tx_title", "text" : Common.getDic("lbl_Folder") })); // 폴더
			var $fileTitle 		= $("<div>", { "class" : "WebHardTitleLine" }).append($("<p>",{ "class" : "tx_title", "text" : Common.getDic("lbl_File") })); // 파일
			return $fragment
						.append( $folderTitle )
						.append( $("<div>",{ "class" : "WebHardListType02_div" }) )
						.append( $fileTitle )
						.append( $("<div>",{ "class" : "WebHardListType02_div" }) );
		}
		
		var addEvent = function(){
			$("#view")
				.on('click', function(){
					var $target = $(event.target);
					if( $target.hasClass("btn_improtant") ){ //bookMark
						setBookMark( $target );
					}else if( ["body_category","body_title","ic_folder","ic_image","ic_pdf","ic_excel","ic_xls","ic_ppt","ic_zip","ic_word","ic_etc"].indexOf( $target.attr('class') ) > -1 ){ //inFolder & fileDown
						var data = $target.parents(".WebHardList").data('item');
						if( data.FILE_NAME ){ //file
							(CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType") !== "Trashbin") && coviWebHard.fileDown("user", data.UUID);
						}else{ //folder
							// 폴더 클릭 시 생기는 이벤트가 공유받은/공유한 폴더 일 경우와 아닌 경우를 분기. 
							if ((CFN_GetQueryString("folderType") !== "Shared") && (CFN_GetQueryString("folderType") !== "Published")) {
								var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === data.UUID })[0];
								(CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType") !== "Trashbin") && treeObj.click(obj.__index, "open");
							} else if (CFN_GetQueryString("folderType") === "Shared") {
								// 공유받은 폴더
								var params = {"folderID" : data.UUID, "searchText" : "", "startDate" : "", "endDate" : ""};
								coviWebHard.getSharedData(params)
									.done(function(data2) { thumbnailView.render( webhardView.modJson4Render(data2.list) ) })
									.done(function() { $("#tmpUid").val(data.UUID) })
									.done(function() { webhardView.sharedAfterRendering()})
							} else if (CFN_GetQueryString("folderType") === "Published") {
								// 공유한 폴더
								var params = {"folderID" : data.UUID, "searchText" : "", "startDate" : "", "endDate" : ""};
								coviWebHard.getPublishedData(params)
									.done(function(data2) { thumbnailView.render( webhardView.modJson4Render(data2.list) ) })
									.done(function() { $("#tmpUid").val(data.UUID) })
									.done(function() { webhardView.sharedAfterRendering()})
							}
						}
					}
				});
		}
		
		this.render = function(data){
			var folderFileObj =
				data.reduce(function( acc,cur,idx,arr ){
					var $template = contentTemplate();
					$template.data('item',cur);
					//checkbox
					$('.WebHardList_chk',$template)
						.append( $("<input>",{ "id"	 : cur.TYPE+idx ,"type" : "checkbox" }).data('item',cur) )
						.append( $("<label>",{ "for" : cur.TYPE+idx }).append("<span>")  );
					//important
					$('.body_important',$template)
						.append( $("<a>",{ "class" : cur.BOOKMARK === "Y" && "btn_improtant on" || "btn_improtant"  }).data('item',cur)  );
					//icon
					if([ "jpg","png","gif","bmp","jpeg" ].indexOf( cur.FILE_TYPE ) > -1) {
						var $img = $("<img>", {"src":loadThumbImage(cur.UUID), "width":"112", "height":"112"});
						$('.body_category',$template).css('padding', 0)
							.append($("<span>", {"class":""}).append( $img ));
					} else {
						$('.body_category',$template)
							.append( $("<span>",{ "class" : cur.TYPE === "file" && getIconClass( cur.FILE_TYPE ) || "ic_folder" }) );
					}
					//description
					$('.body_title',$template).text( cur.FILE_NAME || cur.FOLDER_NAME );
					$('.body_date',$template).text( CFN_TransLocalTime(cur.CREATED_DATE,"yyyy-MM-dd HH:mm:ss") );
					//append
					acc[cur.TYPE].append($template);
					return acc;
				}, { file : $(document.createDocumentFragment()), folder : $(document.createDocumentFragment()) });
			
			folderFileObj.file[0].querySelectorAll("div.WebHardList").length === 0 && folderFileObj.file.append( $("<p>",{ "style" : "text-align: center;color: #9a9a9a;padding: 100px 0px;", "text" : Common.getDic('msg_NoDataList') }) ); 
			folderFileObj.folder[0].querySelectorAll("div.WebHardList").length === 0 && folderFileObj.folder.append( $("<p>",{ "style" : "text-align: center;color: #9a9a9a;padding: 100px 0px;", "text" : Common.getDic('msg_NoDataList') }) );
			
			var $contents = $("#view > div").detach();
			$($contents[1]).empty().append(folderFileObj.folder);
			$($contents[3]).empty().append(folderFileObj.file);
			$("#view").append( $contents );
		}
		
		// webhardView.searchKeyword
		this.searchKeyword = function(keyword){
			coviWebHard.getCheckedTreeData().done((function(data){
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
				this.render(filter);
			}).bind(this) );
		}
		
		this.getCheckedList = function(){
			return $("#view .WebHardList input[type=checkbox]:checked").map(function(idx, item){
				return $(item).data("item");
			});
		}
		
		this.init = function(){
			$("#view")
				.empty()
				.prop("class","WebHardListType02")
				.append( setlayout() )
				.off('click');
			addEvent();
		}
		
		this.bindDragEvent = function(){
			// 폴더 드래그 이벤트
			$("#view > .WebHardListType02_div:eq(0) .body_category")
				.off("dragleave").on("dragleave", function(){
					$(".DnDWrap").remove();
				})
				.off("dragenter").on("dragenter", function(){
					if(!$(".DnDWrap").length){
						$("#view > .WebHardListType02_div:eq(0)").after(dropPage()); 
						
						var $target = $(this);
						$(".DnDarea01").css({
							  "height": $target.outerHeight() + "px"
							, "width": $target.outerWidth() + "px"
							, "top": ($target.offset().top - $(".WebHardListType02").offset().top - 1) + "px"
							, "left": ($target.offset().left - $(".WebHardListType02").offset().left - 1) + "px"
						});
						
						$(".DnDtextbox").css({
							  "bottom": "unset"
							, "top": ($(".WebHardTitleLine").outerHeight() + $(".WebHardListType02_div:eq(0)").outerHeight() - 40) + "px"
						});
					}
				})
				.off("dragover").on("dragover", function(){
					event.stopPropagation();
					event.preventDefault();
					
					if(!$(".DnDWrap").length){
						$("#view > .WebHardListType02_div:eq(0)").after(dropPage()); 
						
						var $target = $(this);
						$(".DnDarea01").css({
							  "height": $target.outerHeight() + "px"
							, "width": $target.outerWidth() + "px"
							, "top": ($target.offset().top - $(".WebHardListType02").offset().top - 1) + "px"
							, "left": ($target.offset().left - $(".WebHardListType02").offset().left - 1) + "px"
						});
						
						$(".DnDtextbox").css({
							  "bottom": "unset"
							, "top": ($(".WebHardTitleLine").outerHeight() + $(".WebHardListType02_div:eq(0)").outerHeight() - 40) + "px"
						});
					}
				})
				.off("drop").on("drop", function(){
					event.stopPropagation();
					event.preventDefault();
					
					$(".DnDWrap").remove();
					dropFile(event.dataTransfer, $(this).closest(".WebHardList").data("item"));
				});
			
			// 파일 목록 드래그 이벤트
			$("#view > .WebHardListType02_div:eq(1)")
				.off("dragleave").on("dragleave", function(){
					$(".DnDWrap").remove();
				})
				.off("dragenter").on("dragenter", function(){
					if(!$(".DnDWrap").length){
						$(this).after(dropPage());
						
						var $target = $(".WebHardListType02_div:eq(1)");
						$(".DnDarea01").css({
							  "height": $target.outerHeight() + "px"
							, "top": ($target.offset().top - $(".WebHardListType02").offset().top - 1) + "px"
						});
					}
				})
				.off("dragover").on("dragover", function(){
					event.stopPropagation();
					event.preventDefault();
					
					if(!$(".DnDWrap").length){
						$(this).after(dropPage());
						
						var $target = $(".WebHardListType02_div:eq(1)");
						$(".DnDarea01").css({
							  "height": $target.outerHeight() + "px"
							, "top": ($target.offset().top - $(".WebHardListType02").offset().top - 1) + "px"
						});
					}
				})
				.off("drop").on("drop", function(){
					event.preventDefault();
					
					$(".DnDWrap").remove();
					dropFile(event.dataTransfer, treeObj.getCheckedTreeList(0));
				});
		}
		
		this.resetCheckedList = function(){
			$("#view .WebHardList input[type=checkbox]").prop("checked", false);
		}
	}();
	
	//listView
	var listView = function(){
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var listGrid = new coviGrid();
		
		// 화면 크기 조정 시 오류 발생 및 컨텍스트 표시 안되는 현상 처리
		listGrid.redrawGrid = function(){
			this.pageBody.show();
			this.pageBody.data("display", "show");
			
			this.defineConfig(true);
			this.setColHead();
			
			this.contentScrollResize();
			
			if(typeof coviInput == "object"){
				coviInput.setSwitch();
			}
			
			// 컨텍스트 설정
			if(CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType") !== "Trashbin"){
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
			}
		};
		
		var gridformater = {
			  fmtFileName : function(){ return $("<div>").append( $("<a>",{ "text" : this.item.FILE_NAME || this.item.FOLDER_NAME }) ).html();  }
			, fmtBoxName : function(){ return $("<div>").append($("<a>", {"text" : this.item.SHARED_TYPE == "FILE" ? Common.getDic("lbl_sharedFolder") : this.item.BOX_NAME})).html(); }
			, fmtFolderPathName : function() {return $("<div>").append($("<a>", {"text" : this.item.FOLDER_NAME_PATH})).html(); }
			, fmtFileType : function(){
				var $body_category = $("<div>",{ "class" : "body_category" });
				var $icon = $("<span>",{ "class" : ( this.item.FILE_TYPE && getIconClass(this.item.FILE_TYPE) ) || "ic_folder" });
				
				if([ "jpg","png","gif","bmp","jpeg" ].indexOf( this.item.FILE_TYPE ) > -1) {
					var $img = $("<img>", {"src":loadThumbImage(this.item.UUID), "width":"30", "height":"30"});
					return $("<div>").append( $body_category.append( $("<span>", {"class" : ""}).append( $img ) )).html();
				}
				
				return $("<div>").append( $body_category.append( $icon ) ).html();
			}
			, fmtImportant : function(){
				var $body_important = $("<div>",{ "class" : "body_important" });
				var $icon = $("<a>",{ "class" : this.item.BOOKMARK === "Y" ? "btn_improtant on" : "btn_improtant" });
				return $("<div>").append( $body_important.append( $icon ) ).html();
			}
			, fmtFileSize : function(){ return (this.item.FILE_SIZE &&  convertFileSize(this.item.FILE_SIZE))  || "-" }
			, fmtCreateDate : function(){ return CFN_TransLocalTime(this.item.CREATED_DATE,"yyyy-MM-dd HH:mm:ss") || "" }
		};
		
		var initalGrid = function(pHeader){
			var header = pHeader || [
				{key:'chk',			label:'chk', 							width:'2', 	align:'center', formatter: 'checkbox'},
				{key:'uid',			label:Common.getDic("lbl_Important"), 	width:'3',	align:'center', hideFilter : 'Y', formatter: gridformater.fmtImportant},
				{key:'fileType',  	label:Common.getDic("lbl_kind"), 		width:'5', 	align:'center',	hideFilter : 'Y', formatter: gridformater.fmtFileType},
				{key:'name',  		label:Common.getDic("lbl_apv_Name"),	width:'25', align:'left', hideFilter : 'Y', formatter: gridformater.fmtFileName },
				{key:'fileSize', 	label:Common.getDic("lbl_Size"), 		width:'5', 	align:'center', hideFilter : 'Y', formatter: gridformater.fmtFileSize },
				{key:'createdDate', label:Common.getDic("lbl_RegistDate") +Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'center', hideFilter : 'Y',formatter: gridformater.fmtCreateDate}
			];
			
			// 전체보기(TotalView), 헤더에 컬럼(폴더명) 추가
			if(CFN_GetQueryString("folderType") == "Total") {
				header.splice(0, 2, {key:'category', label:Common.getDic("lbl_Category"), width:'7', align:'center', formatter: gridformater.fmtBoxName})
				header.splice(1, 0, {key:'filePath', label:Common.getDic("lbl_Path"), width:'7', align:'center', formatter: gridformater.fmtFolderPathName})
			}
			
			listGrid.setGridHeader(header);
			listGrid.setGridConfig({
				targetID		: "listGrid",
				height			: "auto",
				paging			: true,
				sort			: true,
				overflowCell	: [],
				body: {
					  onclick	: function(){ addEvent.call(this) }
					, oncheck	: function(){ addEvent.call(this) }
			    }
			});
		}		
		this.render = function(data){
			if(CFN_GetQueryString("folderType") === "Total") {
				listGrid.page.pageSize = $("#selectWebhardPageSize").val();
				listGrid.bindGrid({
					ajaxUrl: data.url,
					ajaxPars: data.pars
				});
			} else {
				listGrid.setData({
					  list: data
					, page: {
						listCount: data.length
					}
				});
			}
		}
		
		var addEvent = function(){
			switch( this.c ){
				case "0" :
					break;
				case "1" :
					(CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType")) && setBookMark( $(event.target).data('item',this.item) );
					break;
				case "2" :
 					// 공유받은/공유한 폴더 메뉴와 그 외의 분기처리. 중요 표시가 setHideImportant()로 2번째 항목에 이벤트가 걸림.
					if ((CFN_GetQueryString("folderType") !== "Shared" ) && (CFN_GetQueryString("folderType") !== "Published")) {
						var iconClass = ["ic_folder","ic_image","ic_pdf","ic_excel","ic_ppt","ic_zip","ic_word","ic_etc"];	
						if(this.item.TYPE === "file" && event.target.classList.value ){
							iconClass.indexOf(event.target.classList.value) > -1 && coviWebHard.fileDown("user", this.item.UUID);
						}	
					} else if (CFN_GetQueryString("folderType") === "Shared") {		// 공유받은 폴더.
						var uuid = this.item.UUID;
						if(this.item.TYPE === "folder"){		// 폴더일 경우.
							var params = {"folderID" : uuid, "searchText" : "", "startDate" : "", "endDate" : ""};
							coviWebHard.getSharedData(params)
									.done(function(data) { listView.render( webhardView.modJson4Render(data.list) ) })
									.done(function() { $("#tmpUid").val(uuid) })
									.done(function() { webhardView.sharedAfterRendering()})
						} else {	// 파일이면 다운로드.
							coviWebHard.fileDown("user", uuid);
						}
					} else if (CFN_GetQueryString("folderType") === "Published") {	// 공유한 폴더.
						var uuid = this.item.UUID;
						if(this.item.TYPE === "folder"){		// 폴더일 경우
							var params = {"folderID" : uuid, "searchText" : "", "startDate" : "", "endDate" : ""};
							coviWebHard.getPublishedData(params)
									.done(function(data) { listView.render( webhardView.modJson4Render(data.list) ) })
									.done(function() { $("#tmpUid").val(uuid) })
									.done(function() { webhardView.sharedAfterRendering()})
						} else {	// 파일이면 다운로드.
							coviWebHard.fileDown("user", uuid);
						}
					}	
					break;
				case "3" :
					if((CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType") !== "Trashbin")
						&& event.target.tagName === "A"){
						if(this.item.TYPE === "folder"){
							var uuid = this.item.UUID;
							var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === uuid })[0];
							treeObj.click(obj.__index, "open");
						}else{
							(CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType") !== "Trashbin") && coviWebHard.fileDown("user", this.item.UUID);
						}
					}
					break;
			}
		}
		
		this.init = function(){
			$("#view")
				.empty()
				.prop("class","WebHardListType01")
				.append( $("<div>",{ "id" : "listGrid", "style" : "height: auto" }) )
				.off('click');
			addEvent();
			initalGrid();
		}
		
		this.bindDragEvent = function(){
			// 드래그 이벤트
			$("#listGrid tr.gridBodyTr")
				.off("dragleave").on("dragleave", function(){
					$(".DnDWrap").remove();
				})
				.off("dragenter").on("dragenter", function(){
					if(!$(".DnDWrap").length){
						$("#view").append(dropPage()); 
						
						var $target = $(event.target).closest("tr");
						var type = $target.find(".body_category > span").attr("class");
						
						if(type.indexOf("folder") > -1){ // 폴더
							$(".DnDarea01").css({
								  "height": $target.outerHeight() + "px"
								, "top": ($target.offset().top - $("#view").offset().top - 1) + "px"
							});
							
							$(".DnDtextbox").css({
								  "bottom": "unset"
								, "top": ($target.offset().top - $("#view").offset().top + 50) + "px"
							});
						}else{ // 파일
							$(".DnDarea01").css({
								  "height": ($("#listGrid_AX_tbody").outerHeight() + 2)
							});
							
							$(".DnDtextbox").css({
								  "bottom": "-5px"
							});
						}
					}
				})
				.off("dragover").on("dragover", function(){
					event.stopPropagation();
					event.preventDefault();
					
					if(!$(".DnDWrap").length){
						$("#view").append(dropPage()); 
						
						var $target = $(event.target).closest("tr");
						var type = $target.find(".body_category > span").attr("class");
						
						if(type.indexOf("folder") > -1){ // 폴더
							$(".DnDarea01").css({
								  "height": $target.outerHeight() + "px"
								, "top": ($target.offset().top - $("#view").offset().top - 1) + "px"
							});
							
							$(".DnDtextbox").css({ // 파일
								  "bottom": "unset"
								, "top": ($target.offset().top - $("#view").offset().top + 50) + "px"
							});
						}else{
							$(".DnDarea01").css({
								  "height": ($("#listGrid_AX_tbody").outerHeight() + 2) + "px"
							});
							
							$(".DnDtextbox").css({
								  "bottom": "-5px"
							});
						}
					}
				})
				.off("drop").on("drop", function(){
					event.stopPropagation();
					event.preventDefault();
					
					var $target = $(event.target).closest("tr");
					var type = $target.find(".body_category > span").attr("class");
					var data;
					
					if(type.indexOf("folder") > -1){ // 폴더
						$(event.target).closest("tr").find("input[type=checkbox]").trigger("click");
						data = webhardView.getCheckedList()[0];
						webhardView.resetCheckedList();
					}else{
						data = treeObj.getCheckedTreeList(0);
					}
					
					$(".DnDWrap").remove();
					dropFile(event.dataTransfer, data);
				});
			
			$("#listGrid tr.noListTr")
				.off("dragleave").on("dragleave", function(){
					$(".DnDWrap").remove();
				})
				.off("dragenter").on("dragenter", function(){
					if(!$(".DnDWrap").length){
						$("#view").append(dropPage()); 
						
						$(".DnDarea01").css({
							  "height": ($("#listGrid_AX_tbody").outerHeight() + 2)
						});
						
						$(".DnDtextbox").css({
							  "bottom": "-5px"
						});
					}
				})
				.off("dragover").on("dragover", function(){
					event.stopPropagation();
					event.preventDefault();
					
					if(!$(".DnDWrap").length){
						$("#view").append(dropPage()); 
						
						$(".DnDarea01").css({
							  "height": ($("#listGrid_AX_tbody").outerHeight() + 2) + "px"
						});
						
						$(".DnDtextbox").css({
							  "bottom": "-5px"
						});
					}
				})
				.off("drop").on("drop", function(){
					event.stopPropagation();
					event.preventDefault();
					
					var data = treeObj.getCheckedTreeList(0);
					
					$(".DnDWrap").remove();
					dropFile(event.dataTransfer, data);
				});
		}
		
		this.searchKeyword = function(keyword){
			coviWebHard.getCheckedTreeData().done((function(data){
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
				this.render(filter);
			}).bind(this));
		}
		
		this.getCheckedList = function(){
			return listGrid.getCheckedList(0);
		}
		
		this.setConfigGrid = function(config){
			listGrid.setGridConfig(config);
		}
		
		this.setHideImportant = function(){
			var header =  [
				{key:'chk',			label:'chk', 							width:'2', 	align:'center', formatter: 'checkbox'},
				{key:'fileType',  	label:Common.getDic("lbl_kind"), 		width:'5', 	align:'center',	hideFilter : 'Y', formatter: gridformater.fmtFileType},
				{key:'name',  		label:Common.getDic("lbl_apv_Name"),	width:'25', align:'left', 	hideFilter : 'Y', formatter: gridformater.fmtFileName }, 
				{key:'fileSize', 	label:Common.getDic("lbl_Size"), 		width:'5', 	align:'center', hideFilter : 'Y', formatter: gridformater.fmtFileSize },
				{key:'createdDate', label:Common.getDic("lbl_RegistDate") + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'center', hideFilter : 'Y', formatter: gridformater.fmtCreateDate}	
			];
			
			initalGrid(header);
		}
		
		this.resetCheckedList = function(){
			if($("#listGrid tr").eq(0).find("input[type=checkbox]").prop("checked")){
				$("#listGrid tr").eq(0).find("input[type=checkbox]").trigger("click");
			}else{
				$("#listGrid tr").eq(0).find("input[type=checkbox]").trigger("click").trigger("click");
			}
		}
	}();
	
	//timeLineView
	var timeLineView = function(){
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var addEvent = function(){
			$("#view")
				.on('click',function(){
					var $target = $(event.target);
					if( $target.hasClass("btn_improtant") ){ //bookMark
						setBookMark( $target );
					}else if( ["body_category","body_title","ic_folder","ic_image","ic_pdf","ic_excel","ic_ppt","ic_zip","ic_word","ic_etc"].indexOf( $target.attr('class') ) > -1 ){ //inFolder & fileDown
						var data = $target.parents(".WebHardList").data('item');
						if( data.FILE_NAME ){ //file
							coviWebHard.fileDown("user", data.UUID);
						}else{ //folder
							var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === data.UUID })[0];
							treeObj.click(obj.__index,"open");
						}
					}	
				})		
		}
		
		var setlayout = function(){
			var $fragment 		= $(document.createDocumentFragment());
			var $folderTitle	= $("<div>",{ "class" : "WebHardTitleLine" }).append( $("<p>",{ "class" : "tx_title", "text" : "오늘" }) );
			var $fileTitle 		= $("<div>",{ "class" : "WebHardTitleLine" }).append( $("<p>",{ "class" : "tx_title", "text" : "이전" }) );
			return $fragment
						.append( $folderTitle )
						.append( $("<div>",{ "class" : "WebHardListType03_div" }) )
						.append( $fileTitle )
						.append( $("<div>",{ "class" : "WebHardListType03_div" }) );
		}
		
		this.render = function(data){
			var fileObj =
				data.reduce(function( acc,cur,idx,arr ){
					var $template = contentTemplate();
					$template.data('item',cur);
					//checkbox
					$('.WebHardList_chk',$template)
						.append($("<input>", {"id"	: "FILE"+idx ,"type" : "checkbox"}).data('item', cur))
						.append($("<label>", {"for" : "FILE"+idx }).append("<span>"));
					//important
					$('.body_important',$template)
						.append($("<a>", {"class" : cur.BOOKMARK === "Y" && "btn_improtant on" || "btn_improtant"}).data('item', cur));
					//icon
					if([ "jpg","png","gif","bmp","jpeg" ].indexOf( cur.FILE_TYPE ) > -1) {
						var $img = $("<img>", {"src":loadThumbImage(cur.UUID), "width":"40", "height":"40"});
						$('.body_category',$template)
							.append($("<span>", {"class":""}).append( $img ));
					} else {
						$('.body_category',$template)
							.append( $("<span>",{ "class" : getIconClass( cur.FILE_TYPE ) }) );
					}
					//description
					$('.body_title', $template).text( cur.FILE_NAME );
					$('.body_date', $template).text(CFN_TransLocalTime(cur.CREATED_DATE, "yyyy-MM-dd HH:mm:ss"));
					//append
					CFN_TransLocalTime(cur.CREATED_DATE, "yyyyMMdd") === XFN_getDateTimeString("yyyyMMdd", new Date()) && acc.today.append($template) || acc.weekDay.append($template);
					return acc;
				},{ today : $(document.createDocumentFragment()), weekDay : $(document.createDocumentFragment()) });
			
			fileObj.today[0].querySelectorAll("div.WebHardList").length === 0 	&& fileObj.today.append( $("<p>",{ "style" : "text-align: center;color: #9a9a9a;padding: 100px 0px;", "text" : Common.getDic('msg_NoDataList') }) ); 
			fileObj.weekDay[0].querySelectorAll("div.WebHardList").length === 0 && fileObj.weekDay.append( $("<p>",{ "style" : "text-align: center;color: #9a9a9a;padding: 100px 0px;", "text" : Common.getDic('msg_NoDataList') }) );
			
			var $contents = $("#view > div").detach();
			$($contents[1]).empty().append(fileObj.today);
			$($contents[3]).empty().append(fileObj.weekDay);
			$("#view").append( $contents );
		}
		
		this.searchKeyword = function(keyword){
			coviWebHard.getSelectRecentFileList($("#driveSelect option:selected").data('item')).done((function(data){
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
				this.render(filter);
			}).bind(this));
		}
		
		this.init = function(){
			$("#view")
				.empty()
				.prop("class","WebHardListType02")
				.append( setlayout() )
				.off('click');
			addEvent();
		}
		
		this.getCheckedList = function(){
			var collect = $.map($("#view .WebHardList"), function(item){
				if($(item).find(".WebHardList_chk input[type=checkbox]").prop("checked")) return $(item).data("item");
			});
			
			return collect;
		}
		
		this.resetCheckedList = function(){
			$("#view .WebHardList_chk").find("input[type=checkbox]").prop("checked", false);
		}
	}();
	
	
	//view main event
	var _view;
	this.changeView = function(type){
		var viewType = type || 'thumbnail';
		viewType === "thumbnail"	&& (_view = thumbnailView);
		viewType === "list" 		&& (_view = listView);
		viewType === "timeLine"		&& (_view = timeLineView);
		
		// 중요 문서함일때는 로컬 스토리지에 저장X
		if(CFN_GetQueryString("folderType") !== "undefined" && CFN_GetQueryString("folderType") !== "Important"){
			localStorage.__coviWebhardView = viewType;
		}
		
		return this;
	}
	
	this.init = function(){
		typeof _view.init === "function" && _view.init();
		return this;
	}
	
	this.refresh = function(){ typeof _view.refresh === "function" && _view.refresh(); }
	
	this.searchKeyword = function(keyword){
		typeof _view.searchKeyword === "function" && _view.searchKeyword(keyword);
		return this;
	}
	
	this.getCheckedList = function(){
		if(typeof _view.getCheckedList === "function") return _view.getCheckedList();
	}
	
	this.render = function(data){
		typeof _view.render === "function" && _view.render(data);
		return this;
	}
	
	this.setConfigGrid = function(config){
		typeof _view.setConfigGrid === "function" && _view.setConfigGrid(config);
		return this;
	}
	
	this.setHideImportant = function(){
		typeof _view.setHideImportant === "function" && _view.setHideImportant();
		return this;
	}
	
	this.resetCheckedList = function(){
		typeof _view.resetCheckedList === "function" && _view.resetCheckedList();
		return this;
	}
	
	this.bindDragEvent = function(){
		typeof _view.bindDragEvent === "function" && _view.bindDragEvent();
		return this;
	}

	// webhardView.modJson4Render(), webhard_new.js에서 rendering 하는 부분은 '내 드라이브' 기준으로, folder 정보와 file 정보를 각각 DB조회해서 json형태로 묶어줌.
	// 공유받은/공유한 폴더의 경우, rendering 에 맞게 구조 수정.
	this.modJson4Render = function(data) {
		var rtnObj = [];
		if ( Object.keys(data).length === 0 ) {	// 조회 결과가 없을 경우, 함수 return.
			return rtnObj;
		} else {
			data.forEach(function(item) {
				var tmpStr = '';
				if (item.bookmark == '') {									// 1. bookmark
					tmpStr += '{"BOOKMARK" : "N"';
				} else {
					tmpStr += '{"BOOKMARK" : "' + item.bookmark + '"';	
				}
				tmpStr += ', "BOX_UUID" : "' + item.box_uid + '"';			// 2. BOX_UUID
				tmpStr += ', "CREATED_DATE" : "' + item.createdDate + '"';	// 3. CREATED_DATE
				if (item.fileType == "folder") {							// 4, 5. fileSize, fileType, name
					tmpStr += ', "TYPE" : "folder"';
					tmpStr += ', "FOLDER_NAME" : "' + item.name + '"';
				} else {
					tmpStr += ', "TYPE" : "file"';
					tmpStr += ', "FILE_TYPE" : "' + item.fileType + '"';
					tmpStr += ', "FILE_SIZE" : "' + item.fileSize + '"';
					tmpStr += ', "FILE_NAME" : "' + item.name + '"';
				}
				tmpStr += ', "UUID" : "' + item.uid + '"}';					// 6. UUID
				rtnObj.push( JSON.parse(tmpStr) );
			});
			return rtnObj;
		}
	}
	
	// webhardView.sharedAfterRendering(), 공유받은/공유한 폴더의 데이터 로딩 후 추가 렌더링.
	this.sharedAfterRendering = function() {
		$(".body_important").remove();		// 별표시 제거(Thumbnail 타입). 공유 폴더/파일의 북마크 기능은 업로더 기준이며 공유자 아님. listType은 setHideImportant() 함수로 제거.
		$("#btnRename").remove();			// '이름바꾸기'
		
		if ( $("#tmpUid").val() === "" ) {	// 최상위 폴더일 경우.
			$("#btnPre").hide();			// 위로 버튼 숨김.
		} else {
			$("#btnPre").show();
		}
		
		// 렌더링 후 context menu 세팅.
		webhardContext.showContextMenu();
	};
	
}();



//context 관리
var webhardContext = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	var data = null;
	
	var $menuList	= $("<ul/>", {"class": "WebHard_exmenu_list"});
	var $download	= $("<li/>", {"value": "Download"}).append($("<a/>", {"href": "#", "class": "layermenu_download", "text": Common.getDic("siteLinkDownLoad")})); // 다운로드
	var $share		= $("<li/>", {"value": "Share"}).append($("<a/>", {"href": "#", "class": "layermenu_share", "text": Common.getDic("lbl_sharing")})); // 공유
	var $link		= $("<li/>", {"value": "Link"}).append($("<a/>", {"href": "#", "text": Common.getDic("WH_sendLink")})); // 링크 보내기
	var $rename		= $("<li/>", {"value": "Rename"}).append($("<a/>", {"href": "#", "text": Common.getDic("WH_rename")})); // 이름 바꾸기
	var $append		= $("<li/>", {"value": "Append"}).append($("<a/>", {"href": "#", "class": "layermenu_folder", "text": Common.getDic("WH_createNewFolder")})); // 새폴더 만들기
	var $upload		= $("<li/>", {"value": "FileUpload"}).append($("<a/>", {"href": "#", "class": "file_upload", "text": Common.getDic("lbl_uploadFile")})); // 파일 업로드
	
	// context menu event
	$(document).off("click").on("click", function(event){
		if($("#folderContextMenu").css("display") === "block"){
			$("#folderContextMenu").hide();
		}
	});
	
	// treeContext
	var treeContext = function(){
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var drawContext = function(type){
			var $fragment = $(document.createDocumentFragment());
			var $menuList = $("<ul/>", {"class": "WebHard_exmenu_list"});
			
			switch(type.toLowerCase()){
				case "folder": // 폴더
					$menuList.append($download);
					$("#driveSelect :selected").data("item").OWNER_TYPE !== 'G' && $menuList.append($share); // 부서 드라이브는 공유기능 사용X
					$menuList.append($link);
					$menuList.append($rename);
				case "root": // 박스
					$menuList.append($append);
					$menuList.append($upload);
					break;
				default:
			};
			
			$fragment.append($menuList);
			
			$("#folderContextMenu").empty().append($fragment);
		}
		
		var setEvent = function(){
			$("#folderContextMenu li").off("click").on("click", function(){
				contextClick($(this).attr("value"), treeObj.getCheckedTreeList(0));
			});
		}
		
		var contextClick = function(type, data){
			switch(type){
				case "Download":	// 다운로드
					coviWebHard.fileDown("user", "", data.UUID);
					break;
				case "Append":		// 새폴더 만들기
					webhardCommon.addFolderPopup("add", data.BOX_UUID, data.UUID);
					break;
				case "Rename":		// 이름 바꾸기
					webhardCommon.addFolderPopup("edit", data.BOX_UUID, data.UUID);
					break;
				case "FileUpload":	// 파일 업로드
					webhardCommon.uploadPopup(data.BOX_UUID, data.UUID);
					break;
				case "Share":		// 공유
					webhardCommon.share("Folder", data.UUID);
					break;
				case "Link":		// 링크 공유
					webhardCommon.link("FOLDER", data.UUID);
					break;
			}
		}
		
		this.init = function(type){
			drawContext(type);
			setEvent();
		}
	}();
	
	// listContext
	var listContext = function(){
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var drawContext = function(type){
			var $fragment = $(document.createDocumentFragment());
			var $menuList = $("<ul/>", {"class": "WebHard_exmenu_list"});

			if(CFN_GetQueryString("folderType") !== "Trashbin") {
				$menuList.append($download);
			}
			
			if(CFN_GetQueryString("folderType") === "Normal") {
				$("#driveSelect :selected").data("item").OWNER_TYPE !== 'G' && $menuList.append($share); // 부서 드라이브는 공유기능 사용X
					$menuList.append($link);
					$menuList.append($rename);
					if(type.toLowerCase() === "folder") {
						$menuList.append($append);
						$menuList.append($upload);
					}
			}

			$fragment.append($menuList);
			
			$("#folderContextMenu").empty().append($fragment);
		}
		
		var setEvent = function(){
			$("#folderContextMenu li").off("click").on("click", function(){
				contextClick($(this).attr("value"), data);
				data = null;
			});
		}
		
		var contextClick = function(type, data){
			switch(type){
				case "Download":	// 다운로드
					if(data.TYPE === "folder"){
						coviWebHard.fileDown("user", "", data.UUID);
					}else{
						coviWebHard.fileDown("user", data.UUID);
					}
					break;
				case "Append":		// 새폴더 만들기
					webhardCommon.addFolderPopup("add", data.BOX_UUID, data.UUID);
					break;
				case "Rename":		// 이름 바꾸기
					$("#btnRename").trigger("click", [{selectedData: data}]);
					//webhardCommon.addFolderPopup("edit", data.BOX_UUID, data.UUID);
					break;
				case "FileUpload":	// 파일 업로드
					webhardCommon.uploadPopup(data.BOX_UUID, data.UUID);
					break;
				case "Share":		// 공유
					webhardCommon.share((data.TYPE === "folder" ? "folder" : "file"), data.UUID);
					break;
				case "Link":		// 링크 공유 
					webhardCommon.link((data.TYPE === "folder" ? "folder" : "file"), data.UUID);
					break;
			}
		}
		
		this.init = function(type){
			drawContext(type);
			setEvent();
		}
	}();
	
	// thumbContext
	var thumbContext = function(){
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		var drawContext = function(type){
			var $fragment = $(document.createDocumentFragment());
			var $menuList = $("<ul/>", {"class": "WebHard_exmenu_list"});
			
			if(CFN_GetQueryString("folderType") !== "Trashbin") {
				$menuList.append($download);
			}
			
			if(CFN_GetQueryString("folderType") === "Normal") {
				$("#driveSelect :selected").data("item").OWNER_TYPE !== 'G' && $menuList.append($share); // 부서 드라이브는 공유기능 사용X
					$menuList.append($link);
					$menuList.append($rename);
					if(type.toLowerCase() === "folder") {
						$menuList.append($append);
						$menuList.append($upload);
					}
			}
			
			$fragment.append($menuList);
			
			$("#folderContextMenu").empty().append($fragment);
		}
		
		var setEvent = function(){
			$("#folderContextMenu li").off("click").on("click", function(){
				contextClick($(this).attr("value"), data);
				data = null;
			});
		}
		
		var contextClick = function(type, data){
			switch(type){
				case "Download":	// 다운로드
					if(data.TYPE === "folder"){
						coviWebHard.fileDown("user", "", data.UUID);
					}else{
						coviWebHard.fileDown("user", data.UUID);
					}
					break;
				case "Append":		// 새폴더 만들기
					webhardCommon.addFolderPopup("add", data.BOX_UUID, data.UUID);
					break;
				case "Rename":		// 이름 바꾸기
					$("#btnRename").trigger("click", [{selectedData: data}]);
					//webhardCommon.addFolderPopup("edit", data.BOX_UUID, data.UUID);
					break;
				case "FileUpload":	// 파일 업로드
					webhardCommon.uploadPopup(data.BOX_UUID, data.UUID);
					break;
				case "Share":		// 공유
					webhardCommon.share((data.TYPE === "folder" ? "folder" : "file"), data.UUID);
					break;
				case "Link":		// 링크 공유
					webhardCommon.link((data.TYPE === "folder" ? "folder" : "file"), data.UUID);
					break;
			}
		}
		
		this.init = function(type){
			drawContext(type);
			setEvent();
		}
	}();
	
	var _context;
	this.setContextType = function(cType){
		var contextType = cType || "tree";
		contextType === "tree"	&& (_context = treeContext);
		contextType === "list" 	&& (_context = listContext);
		contextType === "thumb"	&& (_context = thumbContext);
		
		return this;
	};
	
	this.init = function(type){
		typeof _context.init === "function" && _context.init(type);
		return this;
	};
	
	this.setData = function(pData){
		data = pData;
	};
	
	// 공유받은/공유한 폴더의 경우, 클릭 이벤트의 렌더링에서 하위 js 호출 안되어 이 곳에서 선언.
	this.showContextMenu = function() {
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
	};
	
}();
