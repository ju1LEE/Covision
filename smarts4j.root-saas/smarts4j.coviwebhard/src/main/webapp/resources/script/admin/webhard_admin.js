/*
 * webhard Admin
 * 
 * use : webhardAdmin.test();
 * */
var webhardAdmin = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	/* webhardAdmin.getBoxList: 박스 목록 조회
	 * @method getBoxList
	 * @param dataSet
	 * */
	this.getBoxList = function(params){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/admin/boxManage/list.do",
			type: "POST",
			data: params,
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* webhardAdmin.getFileList: 파일 목록 조회
	 * @method getFileList
	 * @param dataSet
	 * */
	this.getFileList = function(params){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/admin/fileSearch/list.do",
			type: "POST",
			data: params,
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* webhardAdmin.fileDelete: 파일 삭제
	 * @method fileDelete
	 * @param pFileUuids: 파일 UUID (ex. uuid01;uuid02;uuid03)
	 * @param pComment: 삭제 코멘트
	 * */
	this.fileDelete = function(pFileUuids, pComment){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/admin/fileSearch/delete.do",
			type: "POST",
			data: {
				  fileUuids: pFileUuids
				, comment: pComment
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* webhardAdmin.fileDown: 파일 다운로드
	 * @method fileDown
	 * @param pFileUuids: 파일 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.fileDown = function(pFileUuids){
		var $form = $("<form />", {"action": "/webhard/admin/fileSearch/fileDown.do", "method": "POST"});
		
		$form.append($("<input />", {"name": "fileUuids", "value": pFileUuids ? pFileUuids : ""}));
		$form.appendTo("body");
		
		$form.submit();
		$form.remove();
	},
	
	/* webhardAdmin.boxDelete: 박스 삭제
	 * @method boxDelete
	 * @param pBoxUuids: 박스 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.boxDelete = function(pBoxUuids){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/admin/boxManage/deleteBox.do",
			type: "POST",
			data: {
				boxUuids: boxUuids
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* webhardAdmin.setBoxUseYN: 박스 사용 여부 설정
	 * @method setBoxUseYN
	 * @param pBoxUuid: 박스 UUID
	 * @param pUseYN: 박스 사용 여부 (Y/N)
	 * */
	this.setBoxUseYN = function(pBoxUuid, pUseYN){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/admin/boxManage/setBoxUseYn.do",
			type: "POST",
			data: {
				  boxUuid: pBoxUuid
				, useYn: pUseYN
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	},
	
	/* webhardAdmin.setBoxBlock: 박스 잠금처리
	 * @method setBoxBlock
	 * @param pBoxUuids: 박스 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.setBoxBlock = function(pBoxUuids){
		var deferred = $.Deferred();
		$.ajax({
			url: "/webhard/admin/boxManage/blockBox.do",
			type: "POST",
			data: {
				boxUuids: pBoxUuids
			},
			success: function(data) { deferred.resolve(data); },
			error: function(response, status, error){ deferred.reject(status); }
		});
		return deferred.promise();
	}
	
}();

var webhardAdminCommon = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	/* webhardAdminCommon.boxConfig: 박스 설정 팝업
	 * @method boxConfig
	 * @param pOwnerID 소유자 ID
	 * @param pOwnerName 소유자 이름
	 * */
	this.boxConfig = function(pOwnerID, pOwnerName){
		var title = String.format("BOX {0}[{1}({2})]", Common.getDic("lbl_Setting"), pOwnerName, pOwnerID); // 설정
		var url = "/webhard/admin/popup/callBoxConfigPopup.do"
				+ "?ownerID=" + pOwnerID;
		
		Common.open("", "popupBoxConfig", title, url, "500px", "300px", "iframe", true, null, null, true);
	},
	
	/* webhardAdminCommon.deletePopup: 삭제 팝업
	 * @method deletePopup
	 * @param pFileUuids 파일 UUID
	 * */
	this.deletePopup = function(pFileUuids){
		var url = "/webhard/admin/popup/callFileDeletePopup.do"
			+ "?fileUuids=" + pFileUuids;
		
		Common.open("", "popupFileDelete", Common.getDic("lbl_DeleteAttchaFile"), url, "350px", "195px", "iframe", true, null, null, true); // 파일삭제
	},
	
	/* webhardAdminCommon.boxLockPopup: 박스 잠금 / 잠금해제 팝업
	 * @method boxLockPopup
	 * @param pMode 모드 (lock, unlock)
	 * @param pBoxUuid 박스 UUID
	 * */
	this.boxLockPopup = function(pMode, pBoxUuid){
		var title, popupID;
		var url = "/webhard/admin/popup/callBoxLockPopup.do"
			+ "?mode=" + pMode
			+ "&boxUuid=" + pBoxUuid;
		
		if(pMode === "lock") {
			title = "BOX " + Common.getDic("lbl_Lock");
			popupID = "popupBoxLock";
		} else {
			title = "BOX " + Common.getDic("lbl_UnLock");
			popupID = "popupBoxUnLock";
		}
		
		Common.open("", popupID, title, url, "350px", "195px", "iframe", true, null, null, true); // 파일삭제
	},
	
	/* webhardAdminCommon.convertFileSize: 크기 변환
	 * @method convertFileSize
	 * @param pSize 파일 크기
	 * */
	this.convertFileSize = function(pSize){
		var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
	    if (pSize == 0) return '0MB';
	    var i = parseInt(Math.floor(Math.log(pSize) / Math.log(1024)));
	    return (pSize / Math.pow(1024, i)).toFixed(1) + sizes[i];
	},
	
	/* webhardAdminCommon.setSizeLabel: 라벨 적용
	 * @method setSizeLabel
	 * @param pSize 파일 크기
	 * */
	this.setSizeLabel = function(pSize){
		var curSize = pSize ? Number(pSize) : 0;
		var sizeUnit = "MB";
		
		if(curSize >= 1024){
			curSize = curSize / 1024;
			sizeUnit = "GB";
		} 
		
		return curSize + sizeUnit;
	}
	
}();

var webhardAdminView = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	var boxManageView = function(){
		
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var listGrid = new coviGrid();
		
		var gridFormater = {
			  fmtOwnerID: function(){ return $("<a/>", {"href": "#", "text": this.item.OWNER_ID}).prop("outerHTML"); }
			, fmtBoxCurrentSize: function(){
				var $div = $("<div/>");
				
				$div.append($("<span/>", {"style": "margin-left: 10px;", "text": webhardAdminCommon.convertFileSize(this.item.CURRENT_SIZE_BYTE)}))
					.append($("<span/>", {"style": "margin-left: 5px;", "text": "/"}))
					.append($("<span/>", {"style": "margin-left: 5px;", "text": webhardAdminCommon.setSizeLabel(this.item.BOX_MAX_SIZE)}));
				
				return $div.prop("outerHTML");
			}
			, fmtUseYN: function(){
				return $("<input/>", {"id": "useYn_" + this.item.OWNER_ID.replaceAll("#", "_").replaceAll(".", "_"), "type": "text", "kind": "switch", "on_value": "Y", "off_value": "N", "style": "width:50px; height:21px; border:0px none;", "value": this.item.USE_YN}).prop("outerHTML");
			}
			, fmtBlockYN: function(){
				return $("<input/>", {"id": "blockYn_" + this.item.OWNER_ID.replaceAll("#", "_").replaceAll(".", "_"), "type": "text", "kind": "switch", "on_value": "Y", "off_value": "N", "style": "width:50px; height:21px; border:0px none;", "value": this.item.BLOCK_YN}).prop("outerHTML");
			}
		};
		
		var initalGrid = function(){
			var header = [
			//	  {key: 'chk',					label: 'chk', 									width: '2', 	align: 'center',	hideFilter: 'Y',	formatter: 'checkbox'}
				  {key: 'OWNER_ID',				label: Common.getDic("lbl_executive_id"), 		width: '10',	align: 'center',	hideFilter: 'Y',	formatter: gridFormater.fmtOwnerID}
				, {key: 'OWNER_NAME',			label: Common.getDic("lbl_name"), 				width: '10',	align: 'center',	hideFilter: 'Y'}
				, {key: 'CREATED_DATE',			label: Common.getDic("lbl_registeDate"),		width: '10',	align: 'center',	hideFilter: 'Y',	sort: "desc"}
				, {key: 'USE_YN',				label: Common.getDic("lbl_IsUse"),				width: '5', 	align: 'center',	hideFilter: 'Y',	formatter: gridFormater.fmtUseYN}
			//	, {key: 'BLOCK_YN',				label: Common.getDic("lbl_Lock"),				width: '5', 	align: 'center',	hideFilter: 'Y',	formatter: gridFormater.fmtBlockYN}
				, {key: 'CURRENT_SIZE_BYTE',	label: Common.getDic("lbl_Mail_UsageCapacity"),	width: '15', 	align: 'right',		hideFilter: 'Y',	formatter: gridFormater.fmtBoxCurrentSize}
			];
			
			listGrid.setGridHeader(header);
			listGrid.setGridConfig({
				targetID		: "listGrid",
				height			: "auto",
				page: {
					pageNo: 1,
					pageSize: 10
				},
				paging			: true,
				sort			: true,
				overflowCell	: [],
				body: {
					  onclick 	: function(){ addEvent.call(this) }
					, oncheck	: function(){ addEvent.call(this) }
				}
			});
		}
		
		this.render = function(){
			listGrid.bindGrid({
				ajaxUrl: "/webhard/admin/boxManage/list.do",
				ajaxPars: {
					  "domainID": $("#selectDomain").val()
					, "searchWord": $("#inputSearchWord").val()
					, "searchOption": $("#selectSearchOption").val()
				},
			});
		}
		
		var addEvent = function(){
			switch( this.c ){
				case "0": // 설정 팝업
					webhardAdminCommon.boxConfig(this.item.OWNER_ID, this.item.OWNER_NAME);
					break;
				case "3": // 사용여부
					webhardAdmin.setBoxUseYN(this.item.UUID, $("#useYn_" + this.item.OWNER_ID.replaceAll("#", "_").replaceAll(".", "_")).val());
					break;
				/*case "4": // 잠금
					webhardAdminCommon.boxLockPopup(($("#blockYn_" + this.item.OWNER_ID.replaceAll("#", "_").replaceAll(".", "_")).val() === "Y" ? "lock" : "unlock"), this.item.UUID);
					break;*/
			}
		}
		
		this.init = function(){
			$("#page")
				.empty()
				.append($("<div/>", {"id": "listGrid", "style": "height: auto"}))
				.off("click");
			addEvent();
			initalGrid();
		}
		
		this.getCheckedList = function(){
			return listGrid.getCheckedList(0);
		}
	}();
	
	var fileSearchView = function(){
		
		if (!(this instanceof arguments.callee )) return new arguments.callee();
		
		var listGrid = new coviGrid();
		
		var gridformater = {
			  fmtFileSize: function(){
					var x = this.item.FILE_SIZE;
					var pattern = /(-?\d+)(\d{3})/;
					
					while (pattern.test(x)) {
						x = x.replace(pattern, "$1,$2");
					}
					
					return x;
			  }
			, fmtCreateDate: function(){ return CFN_TransLocalTime(this.item.CREATED_DATE, _StandardServerDateFormat); }
		};
		
		var initalGrid = function(){
			var header = [
				  {key: 'chk',				label: 'chk', 																		width: '2',		align: 'center',	hideFilter: 'Y',	formatter: 'checkbox'}
				, {key: 'OWNER_ID',			label: Common.getDic("lbl_executive_id"),											width: '5',		align: 'center',	hideFilter: 'Y'}
				, {key: 'OWNER_NAME',		label: Common.getDic("lbl_name"),													width: '5',		align: 'center',	hideFilter: 'Y'}
				, {key: 'FILE_TYPE',		label: Common.getDic("lbl_kind"),													width: '4',		align: 'left',		hideFilter: 'Y'}
				, {key: 'FILE_NAME',		label: Common.getDic("WH_fileName"),												width: '10',	align: 'center',	hideFilter: 'Y'}
				, {key: 'FOLDER_NAME_PATH',	label: Common.getDic("lbl_Path"),													width: '10',	align: 'left',		hideFilter: 'Y',	/*formatter: ''*/}
				, {key: 'FILE_SIZE',		label: Common.getDic("lbl_Size") + " (byte)",										width: '8',		align: 'right',		hideFilter: 'Y',	formatter: gridformater.fmtFileSize}
				, {key: 'CREATED_DATE',		label: Common.getDic("lbl_RegistDate") + Common.getSession("UR_TimeZoneDisplay"),	width: '10',	align: 'center',	hideFilter: 'Y',	formatter: gridformater.fmtCreateDate, sort: "desc"}
			];
			
			listGrid.setGridHeader(header);
			listGrid.setGridConfig({
				targetID		: "listGrid",
				height			: "auto",
				page: {
					pageNo: 1,
					pageSize: 10
				},
				paging			: true,
				sort			: true,
				overflowCell	: [],
				body			: {}
			});
		}
		
		this.render = function(){
			listGrid.bindGrid({
				ajaxUrl: "/webhard/admin/fileSearch/list.do",
				ajaxPars: {
					  "domainID": $("#selectDomain").val()
					, "fileType": $("#inputFileType").val()
					, "fileName": $("#inputFileName").val()
					, "fileSizeOption": $("#selectFileSizeOption").val()
					, "periodOption": $("#selectPeriodOption").val()
					, "startDate": $("#startDate").val()
					, "endDate": $("#endDate").val()
				}
			});
		}
		
		this.init = function(){
			$("#page")
				.empty()
				.append($("<div/>", {"id": "listGrid", "style": "height: auto"}))
				.off("click");
			initalGrid();
		}
		
		this.getCheckedList = function(){
			return listGrid.getCheckedList(0);
		}
	}();
	
	var _view;
	this.changeView = function(type){
		var viewType = type || "boxManage";
		viewType === "boxManage"	&& (_view = boxManageView);
		viewType === "fileSearch" 	&& (_view = fileSearchView);
		return this;
	}
	
	this.init = function(){
		typeof _view.init === "function" && _view.init();
		return this;
	}
	
	this.getCheckedList = function(){
		if(typeof _view.getCheckedList === "function") return _view.getCheckedList();
	}
	
	this.render = function(data){ typeof _view.render === "function" && _view.render(data); }
	
}();