/*
 * webhard Common
 * 
 * use : webhardCommon.test();
 * */
var webhardCommon = function(){
	
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	/* webhardCommon.pageMove: 페이지 이동
	 * @method pageMove
	 * @param pBoxID 박스 ID
	 * @param pFolderID 폴더 ID
	 * @param pFolderType 폴더 타입 (ex. Normal, Published, Trashbin, ...)
	 * */
	this.pageMove = function(pBoxID, pFolderID, pFolderType){
		if(pFolderType === "Trashbin") return; // 휴지통 내 디렉토리 탐색 금지
		
		var	url = "/webhard/layout/user_BoxList.do"
				+ "?CLSYS=webhard"
				+ "&CLMD=user"
				+ "&CLBIZ=Webhard"
				+ "&boxID=" + pBoxID
				+ "&folderID=" + pFolderID
				+ "&folderType=" + pFolderType;
		
		// 마지막 이동 경로 저장
		sessionStorage.setItem("urlHistory", url);
		
		// 본문 페이지 이동처리
		CoviMenu_GetContent(url);
	},
	
	/* webhardCommon.uploadPopup: 업로드 팝업 호출
	 * @method uploadPopup
	 * @param pBoxUUID 업로드 되는 박스 UUID
	 * @param pTargetUUID 업로드 되는 폴더 UUID
	 * */
	this.uploadPopup = function(pBoxUUID, pTargetUUID){
		var url = "/webhard/user/popup/callFileUpload.do"
				+ "?boxUUID=" + (pBoxUUID ? pBoxUUID : "")
				+ "&targetFolder=" + (pTargetUUID ? pTargetUUID : "");
		
		Common.open("", "fileUploadPopup", "Upload", url, "500px", "250px", "iframe", true, null, null, true); 
	},
	
	/* webhardCommon.addFolderPopup:폴더 추가 및 폴더, 파일 이름 수정 팝업
	 * @method addFolderPopup
	 * @param pMode 폴더 추가 및 폴더, 파일 이름 수정 구분 (add - 추가, edit - 폴더 이름 수정, edit_file - 파일 이름 수정)
	 * @param pBoxUUID 추가/수정 되는 박스 UUID
	 * @param pFolderUUID 수정 되는 폴더 UUID
	 * @param pFileUUID 수정 되는 파일 UUID
	 * */
	this.addFolderPopup = function(pMode, pBoxUUID, pFolderUUID, pFileUUID){
		var title =  pMode === "add" ? Common.getDic("WH_createNewFolder")
					 : pMode === "edit" ? Common.getDic("WH_changeFolderName") : Common.getDic("lbl_changeFileName");
					 
		var url = "/webhard/user/popup/callFolderNamePopup.do"
				+ "?mode=" + pMode
				+ "&boxUUID=" + (pBoxUUID ? pBoxUUID : "")
				+ "&folderUUID=" + (pFolderUUID ? pFolderUUID : "")
				+ "&fileUUID=" + (pFileUUID ? pFileUUID : "");
		Common.open("", "folderNamePopup", title, url, "350px", "160px", "iframe", true, null, null, true);
	},
	
	/* webhardCommon.share: 공유 팝업
	 * @method share
	 * @param pShareType 공유 타입 (Folder, File)
	 * @param pShareID 공유 대상 ID
	 * */
	this.share = function(pShareType, pShareID){
		var url = "/webhard/user/popup/callSharePopup.do"
				+ "?sharedType=" + pShareType
				+ "&sharedID=" + (pShareID ? pShareID : "");
		
		Common.open("", "sharePopup", Common.getDic("lbl_shareMemberInvitation"), url, "400px", "430px", "iframe", true, null, null, true); // 공유 멤버 초대하기
	},
	
	/* webhardCommon.link: 링크 팝업
	 * @method link
	 * @param pTargetType 타겟 타입 (FILE, FOLDER)
	 * @param pTargetUuid 타겟 UUID
	 * */
	this.link = function(pTargetType, pTargetUuid){
		var url = "/webhard/user/popup/callLinkPopup.do"
				+ "?targetType=" + pTargetType
				+ "&targetUuid=" + (pTargetUuid ? pTargetUuid : "");
		
		Common.open("", "linkPopup", Common.getDic("WH_sendLink"), url, "420px", "400px;", "iframe", true, null, null, true); // 링크 보내기
	},
	
	/* webhardCommon.tree: 트리 팝업(복사/이동)
	 * @method tree
	 * @param pMode 팝업 모드 (copy, move)
	 * @param pBoxUuid 현재 박스 UUID
	 * @param pFolderUuid 현재 폴더 UUID
	 * @param pFileUuids: 파일 UUID (ex. uuid01;uuid02;uuid03)
	 * @param pFolderUuids: 폴더 UUID (ex. uuid01;uuid02;uuid03)
	 * */
	this.tree = function(pMode, pBoxUuid, pFolderUuid, pFileUuids, pFolderUuids){
		var title = pMode === "copy" ? Common.getDic("lbl_Copy") : Common.getDic("lbl_Move");
		var url = "/webhard/user/popup/callFolderTreePopup.do"
				+ "?mode=" + pMode
				+ "&boxUuid=" + pBoxUuid
				+ "&folderUuid=" + pFolderUuid
				+ "&fileUuids=" + pFileUuids
				+ "&folderUuids=" + pFolderUuids;
		
		Common.open("", "folderTreePopup", title, url, "350px", "500px", "iframe", true, null, null, true);
	},
	
	/* webhardCommon.convertFileSize: 파일 사이즈 변환
	 * @method convertFileSize
	 * @param pSize 파일 사이즈
	 * */
	this.convertFileSize = function(pSize){
		var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
	    if (pSize == 0) return '0';
	    var i = parseInt(Math.floor(Math.log(pSize) / Math.log(1024)));
	    return (pSize / Math.pow(1024, i)).toFixed(1) + sizes[i];
	}
}();
