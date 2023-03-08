/* 프로그램 저작권 정보
//이 프로그램에 대한 저작권을 포함한 지적재산권은 (주)코비젼에 있으며,
//(주)코비젼이 명시적으로 허용하지 않은 사용, 복사, 변경, 제3자에의 공개, 배포는 엄격히 금지되며,
//(주)코비젼의 지적재산권 침해에 해당됩니다.
//(Copyright ⓒ 2011 Covision Co., Ltd. All Rights Reserved)
//
//You are strictly prohibited to copy, disclose, distribute, modify, or use  this program in part or
//as a whole without the prior written consent of Covision Co., Ltd. Covision Co., Ltd.,
//owns the intellectual property rights in and to this program.
//(Copyright ⓒ 2011 Covision Co., Ltd. All Rights Reserved)

///<creator>기술연구소</creator> 
///<createDate>2019.04.24</createDate> 
///<lastModifyDate></lastModifyDate> 
///<version>0.1.0</version>
///<summary> 
///webhard js
///</summary>
///<ModifySpc>
/// 
///</ModifySpc>
*/

$(document).on('pageinit', '#webhard_list_page', function () {
	if($("#webhard_list_page").attr("IsLoad") != "Y"){
		$("#webhard_list_page").attr("IsLoad", "Y");
		setTimeout("mobile_webhard_ListInit()",10);
		if(mobile_comm_isAndroidApp()) {
			window.covimoapp.SetPullToRefresh(false);
		}
	}
});

$(document).on('pageinit', '#webhard_list_recentdoc_page', function () {
	if($("#webhard_list_recentdoc_page").attr("IsLoad") != "Y"){
		$("#webhard_list_recentdoc_page").attr("IsLoad", "Y");
		setTimeout("mobile_webhard_list_recentdocInit()",10);	
	}
});

$(document).on('pageinit', '#webhard_share_page', function () {
	if($("#webhard_share_page").attr("IsLoad") != "Y"){
		$("#webhard_share_page").attr("IsLoad", "Y");
		setTimeout("mobile_webhard_share_ListInit()",10);	
	}
});

var webhardTouchX = 0;
//리스트 전역변수
var _mobile_webhard_list = {
		// 리스트 조회 초기 데이터
		MenuID: '',			//메뉴ID
		BizSection: '',		//메뉴 Code		
		BoxType: 'Receive',		//boxType-Receive/Request/Distribute 등등
		FolderID: '',		//폴더ID-없으면 전체 조회
		boxUuid : '',
		FolderType: '',		//폴더type- Shared/Published/Recent/Important/Trashbin
		Page: 1,			//조회할 페이지
		PageSize: 10,		//페이지당 건수
		SearchText: '',		//검색어
		searchSize : '',		
		
		// 페이징을 위한 데이터
		Loading: false,		//데이터 조회 중
		TotalCount: -1,		//전체 건수
		RecentCount: 0,		//현재 보여지는 건수
		EndOfList: false,	//전체 리스트를 다 보여줬는지
		
		//스크롤 위치 고정
		OnBack: false,		//뒤로가기로 왔을 경우
		Scroll: 0,			//스크롤 위치
		
		//ViewType
		ViewType: "List",  //Album : 썸네일, List : list
		ViewMode: "list", // thumbnail : 썸네일, list : list
		divExmenuPfx: "div_webhard_exmenu_list_", //컨텍스트 메뉴 컨테이너
		divContextPfx: "div_webhard_exmenu_list_list_", //컨텍스트 메뉴
		liObjPfx: "li_webhard_list_", //폴더/파일 li 객체
		divObjPfx: "div_webhard_list_", //폴더/파일 div 객체
		divChkPfx : "divchk_webhard_list_", //CheckBox Div
		chkPfx : "chk_webhard_list_", //CheckBox

		//Dialog open type
		opentype : "" 
	};

//공유자 전역변수
var _mobile_webhard_share = {			
		mode : "",
		UID :"",
		FileType : ""		
	};

//웹하드 리스트 페이지 초기화
function mobile_webhard_ListInit(){
	
	mobile_comm_getDicList(["msg_SelecteBeforeProcess", "lbl_apv_userfoldertitle_2", "lbl_Mail_ChangeFolderName","msg_Added", "msg_Changed", "msg_selectTargetDelete","msg_AreYouDelete", "msg_50","msg_59", "msg_UploadOk", "msg_sns_11", "lbl_shareMemberInvitation" ]);	
	
	//1. 초기화		
	_mobile_webhard_list.ViewType = 'List';
	_mobile_webhard_list.BoxType = 'Receive';
	_mobile_webhard_list.FolderID = '';
	_mobile_webhard_list.FolderType = 'Normal';
	_mobile_webhard_list.SearchText = '';
	_mobile_webhard_list.searchSize = '';
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.TotalCount = -1;
	_mobile_webhard_list.RecentCount = 0;	
	_mobile_webhard_list.EndOfList = false;		
	_mobile_webhard_list.divExmenuPfx = "div_webhard_exmenu_list_";
	_mobile_webhard_list.divContextPfx = "div_webhard_exmenu_list_list_";
	_mobile_webhard_list.liObjPfx = "li_webhard_list_";	
	_mobile_webhard_list.divObjPfx = "div_webhard_list_";
	_mobile_webhard_list.divChkPfx = "divchk_webhard_list_";
	_mobile_webhard_list.chkPfx = "chk_webhard_list_";
	
	if (window.sessionStorage['webhardopentype'] != undefined && window.sessionStorage['webhardopentype'] != '') {
		_mobile_webhard_list.opentype = window.sessionStorage['webhardopentype'];
		$("#webhard_list_back").show();
		mobile_webhard_ChangeMultiMode();		
    } else {
    	_mobile_webhard_list.opentype = '';
    	// 2. 상단메뉴	
    	$('#webhard_list_topmenu').html("<ul class=\"h_tree_menu_wrap\">" + mobile_webhard_getTopMenuHtml(WebhardMenu)+"</ul>");	//webhardMenu - 서버에서 넘겨주는 좌측메뉴 목록    	
    }
	
	$("#btnSelectFile").on("click", function(event) {
		$("#uploadFiles").trigger("click");
	});
	
	$("#uploadFiles").on("change", function(event) {					
		mobile_webhard_UploadFile($(this)[0].files);
	});
		
	mobile_webhard_getTreeData();		
	
	mobile_webhard_getUsage();
	
	// 3. 글 목록 조회
	mobile_webhard_getList(_mobile_webhard_list);
		 
}

//공유자 조회페이지 초기화
function mobile_webhard_share_ListInit() {
	mobile_comm_getDicList(["msg_SelecteBeforeProcess", "lbl_apv_userfoldertitle_2", "lbl_Mail_ChangeFolderName","msg_Added", "msg_Changed"
        , "msg_selectTargetDelete","msg_AreYouDelete", "msg_50","msg_59", "msg_UploadOk", "msg_sns_11", "lbl_shareMemberInvitation" ]);
	
	// 자동완성 조회 render
	$(document).on("keyup keydown", "#txtwebhard_sharemember", function (e) {
		e.stopImmediatePropagation();		
		// 자동완성 영역 초기화
		var divShareAutoCompleteArea = $("#webhard_share_divAutoComplete");
		var ulShareAutoCompleteArea = $("#webhard_share_ulAutoComplete");		
		divShareAutoCompleteArea.hide();
		
		if($(this).val().length > 1){			
			var target = $("#txtwebhard_sharemember");
			var keyword = $("#txtwebhard_sharemember").val();
			
			// 자동완성영역 포지셔닝
			divShareAutoCompleteArea.css("top", target.offset().top + 28);
			
			mobile_comm_showload();
			$.ajax({
				url : "/covicore/control/getAllUserGroupAutoTagList.do",
				type : "POST",
				data : {
					keyword : keyword
				},				
				dataType : "json",
				success : function(res){
					var list = res.list;
					var cnt = list.length;
					var appendHtml = "";
					ulShareAutoCompleteArea.html("");
					for(var i=0; i<cnt; i++){
						var item = list[i];
						var targetCode = item.Code;
						var targetName = item.Name;
						var targetDeptName = item.DeptName;
						var targetType = item.Type;
						if(targetType == "UR"){
							appendHtml += "<li class=\"staff\">";
							appendHtml += "<a href='javascript:javascript:void(0);\' onclick='webhard_share_autoTag(this);' id='webhard_share_autoTag_"+targetCode+"' name='webhard_share_autoTag' itemtype='"+targetType+"' username='"+targetName+"' code='"+targetCode+"' groupname='"+targetDeptName+"' class='con_link ui-link'>";
							appendHtml += "<span class='photo' style=\"background-image: url('" + mobile_comm_getimg(mobile_comm_getBaseConfig("BackStorage").replace("{0}", mobile_comm_getSession("DN_Code")) + "PhotoPath/" + targetCode + ".jpg") + "'), url('" + mobile_comm_noperson() + "')\"></span>";
							appendHtml += "<div class='info'>";
							appendHtml += "<p class=\"name\">"+targetName+"</p>";
							appendHtml += "<p class=\"detail\">";
							appendHtml += "<span>"+targetDeptName+"</span>";
							//appendHtml += "<span> | 팀원</span>";
							//appendHtml += "<span> | 사원</span>";
							appendHtml += "<span>&nbsp;</span>";
							appendHtml += "</p>";
							appendHtml += "</div>";
							appendHtml += "</a>";
							appendHtml += "<div class=\"check\">";
							appendHtml += "<div class=\"ui-checkbox\">";
							appendHtml += "<label for=\"org_list_selectuser_"+targetCode+"\" class=\"ui-btn ui-corner-all ui-btn-inherit ui-btn-icon-left ui-checkbox-off\"></label>";
							appendHtml += "<input type=\"checkbox\" name=\"\" value=\"\" id=\"org_list_selectuser_"+targetCode+"\" onclick=\"webhard_share_autoTag($('#webhard_share_autoTag_"+targetCode+"'));\">";
							appendHtml += "</div>";
							appendHtml += "</div>";
							appendHtml += "</li>";			
						}							
					}
					ulShareAutoCompleteArea.append(appendHtml).trigger("create");
					
					if(cnt > 0 ){
						divShareAutoCompleteArea.show();
					}else{
						divShareAutoCompleteArea.hide();
					}
					
					mobile_comm_hideload();
				 },
				 error: function (response, status, error){
					 mobile_comm_hideload();
					mobile_comm_ajaxerror(url, response, status, error);
				}
			});
		}
	});
	
	_mobile_webhard_share.mode = mobile_comm_getQueryStringForUrl(document.location.href, 'mode');
	_mobile_webhard_share.UID = mobile_comm_getQueryStringForUrl(document.location.href, 'uid');
	_mobile_webhard_share.FileType = mobile_comm_getQueryStringForUrl(document.location.href, 'filetype');	
	
	if(_mobile_webhard_share.mode == "share"){
		$("#webhard_share_title").text(mobile_comm_getDic('lbl_shareMemberInvitation'));
		//$("#webhard_share_addheader").show();
		$("#webhard_share_addcontent").show();		
	}else if(_mobile_webhard_share.mode == "view"){
		$("#webhard_share_title").text(mobile_comm_getDic('lbl_SharedMemberView'));		
		//$("#webhard_share_addheader").hide();
		$("#webhard_share_addcontent").hide();		
	}		
	mobile_webhard_getSharedList(_mobile_webhard_share);
}

function mobile_webhard_list_recentdocInit() {
	mobile_comm_getDicList(["msg_SelecteBeforeProcess", "lbl_apv_userfoldertitle_2", "lbl_Mail_ChangeFolderName","msg_Added", "msg_Changed"
        , "msg_selectTargetDelete","msg_AreYouDelete", "msg_50","msg_59", "msg_UploadOk", "msg_sns_11", "lbl_shareMemberInvitation" ]);
	//1. 초기화		
	_mobile_webhard_list.BoxType = 'Album';
	_mobile_webhard_list.BoxType = 'Receive';
	_mobile_webhard_list.FolderID = '';
	_mobile_webhard_list.FolderType = 'Recent';
	_mobile_webhard_list.SearchText = '';
	_mobile_webhard_list.searchSize = '';
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.TotalCount = -1;
	_mobile_webhard_list.RecentCount = 0;	
	_mobile_webhard_list.EndOfList = false;
	
	// 2. 상단메뉴		
	$('#webhard_list_topmenu').html("<ul class=\"h_tree_menu_wrap\">" + mobile_webhard_getTopMenuHtml(WebhardMenu) +"</ul>");	//webhardMenu - 서버에서 넘겨주는 좌측메뉴 목록	
	mobile_webhard_getTreeData();			
	
	mobile_webhard_getUsage();
	
	// 3. 글 목록 조회	
	mobile_webhard_getList(_mobile_webhard_list);    
}

//View Mode 변경
function mobile_webhard_ChangeView(pMode){		
	//1. 초기화		
	_mobile_webhard_list.ViewType = pMode;
	_mobile_webhard_list.BoxType = 'Receive';	
	//_mobile_webhard_list.FolderType = 'Normal';
	_mobile_webhard_list.SearchText = '';
	_mobile_webhard_list.searchSize = '';
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.TotalCount = -1;
	_mobile_webhard_list.RecentCount = 0;	
	_mobile_webhard_list.EndOfList = false;
		
	if(pMode == "Album"){
		//$("#btnListView").show();
		//$("#btnthumview").hide();		
		$("#webhard_list_wrap").hide();
		$("#webhard_thumbnail_wrap").show();
		$("#webhard_time_wrap").hide();
		_mobile_webhard_list.ViewMode = "thumbnail";
		_mobile_webhard_list.divExmenuPfx = "div_webhard_exmenu_thumbnail_";
		_mobile_webhard_list.divContextPfx = "div_webhard_exmenu_list_thumbnail_";
		_mobile_webhard_list.liObjPfx = "li_webhard_thumbnail_";
		_mobile_webhard_list.divObjPfx = "div_webhard_thumbnail_";
		_mobile_webhard_list.divChkPfx = "divchk_webhard_thumbnail_";
		_mobile_webhard_list.chkPfx = "chk_webhard_thumbnail_";		
	} else if(pMode == "List"){
		//$("#btnListView").hide();
		//$("#btnthumview").show();
		$("#webhard_list_wrap").show();
		$("#webhard_thumbnail_wrap").hide();
		$("#webhard_time_wrap").hide();
		_mobile_webhard_list.ViewMode = "list";
		_mobile_webhard_list.divExmenuPfx = "div_webhard_exmenu_list_";
		_mobile_webhard_list.divContextPfx = "div_webhard_exmenu_list_list_";
		_mobile_webhard_list.liObjPfx = "li_webhard_list_";
		_mobile_webhard_list.divObjPfx = "div_webhard_list_";
		_mobile_webhard_list.divChkPfx = "divchk_webhard_list_";
		_mobile_webhard_list.chkPfx = "chk_webhard_list_";		
	} else if(pMode == "Time"){
		//$("#btnListView").hide();
		//$("#btnthumview").hide();
		$("#webhard_list_wrap").hide();
		$("#webhard_thumbnail_wrap").hide();
		$("#webhard_time_wrap").show();
		_mobile_webhard_list.ViewMode = "list";
		_mobile_webhard_list.divExmenuPfx = "div_webhard_exmenu_time_";
		_mobile_webhard_list.divContextPfx = "div_webhard_exmenu_time_list_";
		_mobile_webhard_list.liObjPfx = "li_webhard_time_";
		_mobile_webhard_list.divObjPfx = "div_webhard_time_";
		_mobile_webhard_list.divChkPfx = "divchk_webhard_time_";
		_mobile_webhard_list.chkPfx = "chk_webhard_time_";		
	}		
	mobile_webhard_getList(_mobile_webhard_list);
}


//트리 조회
function mobile_webhard_getTreeData() {	
	var rootData = {"chk": "N", "no": "", "boxUuid": "", "nodeName": mobile_comm_getDic("lbl_myDrive"), "nodeValue": "ROOT", "rdo": "N"}; /*  내 드라이브 */	
	var url = "/webhard/mobile/webhard/user/tree/select.do";
	
	$.ajax({
		url: url,
		data: {},
		type: "post",
		async: false,
		success: function (res) {			
			if(res.status == "SUCCESS"){				
				var treeList = res.list;
				if(res.list.length > 0 ){
					treeList.unshift(rootData);
				} else {
					treeList.push(rootData);
				}
				$("#li_foldertree").remove();
				if(_mobile_webhard_list.opentype == ""){
					if($("#webhard_folder_tree").length >0)
						$("#webhard_folder_tree").remove();
					$('#webhard_list_topmenu').find('li').filter(':eq(0)').before(mobile_webhard_getTopTreeHtml(treeList, 'nodeValue', 'ROOT', 'pno', ''));
				}else{
					$('#webhard_list_topmenu').html("<ul class=\"h_tree_menu_wrap\">" + mobile_webhard_getTopTreeHtml(treeList, 'nodeValue', 'ROOT', 'pno', '') + "</ul>");
				}
				mobile_webhard_ddlseltypeBinding(treeList);
			}			
		},
		error: function (response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});
}

//복사/이동 대상 폴더 selectbox 바인딩
function mobile_webhard_ddlseltypeBinding(pData){
	$("#webhard_list_ddlseltype").html("");
	var sHtml = "";
	$(pData).each(function (i, data){
		sHtml +="<option value=\""+data.nodeValue+"\">"+data.nodeName+"</option>"; 		
	});
	$("#webhard_list_ddlseltype").html(sHtml);
	
}

//함 정보 랜더링
function mobile_webhard_getTopMenuHtml(webhardMenu) {
	var sHtml = "";
	var nSubLength = 0;
	var sALink = "";
	var sLeftMenuID = "";
	var sIconClass = "";
	var sFolderType = "";
		
	$(webhardMenu).each(function (i, data){
		nSubLength = data.Sub.length;		
		sALink = "";		
		switch(data.IconClass.replace("whardMenu", "")) {		
			case "02": sIconClass = "t_ico_share01"; break;
			case "03": sIconClass = "t_ico_share02";  break;
			case "04": sIconClass = "t_ico_docbox";   break;
			case "05": sIconClass = "t_ico_important";   break;
			case "06": sIconClass = "t_ico_trashcan";  break;
			case "06 gline": sIconClass = "t_ico_trashcan";  break;
			default: sIconClass = "t_ico_app";   break;
		}		
		sFolderType = mobile_comm_getQueryStringForUrl(data.URL, 'folderType');
		sHtml += "<li folderid=\"ROOT\" foldertype=\"" + sFolderType + "\" displayname=\"" + data.DisplayName + "\">";
		sHtml += "    <div class=\"h_tree_menu\">";
		if(nSubLength > 0) {
			
			sALink = "javascript: mobile_webhard_openclose('li_sub_" + data.MenuID + "', 'span_menu_" + data.MenuID + "');";
			
			sHtml += "    <ul class=\"h_tree_menu_list\">";
			sHtml += "        <li>";
			sHtml += "            <a onclick=\"" + sALink + "\" class=\"t_link not_tree\">";
			sHtml += "                <span id=\"span_menu_" + data.MenuID + "\" class=\"t_ico_open\"></span><span class=\"" + sIconClass + "\"></span>";
			sHtml += "                " + data.DisplayName; //mobile_comm_getDicInfo(data.MultiDisplayName, lang);
			sHtml += "            </a>";
			sHtml += "        </li>";
			sHtml += "    </ul>";
		} else {
			
			sALink = "javascript: mobile_webhard_ChangeMenu('" + sFolderType + "', this);";
			
			sHtml += "    <a onclick=\"" + sALink + "\" class=\"t_link ui-link\">";
			sHtml += "        <span class=\"" + sIconClass + "\"></span>";
			sHtml += "        " + data.DisplayName;
			sHtml += "    </a>";
		}
		sHtml += "    </div>";
		sHtml += "</li>";		
	});
	
	return sHtml;
}

//트리 그리기
function mobile_webhard_getTopTreeHtml(pData, pRootNode, pRootValue, pParentNode, pParentValue) {
	
	//pData - 트리 전체 데이터 Array
	//pRootNode - 루트 여부 확인하는 node명
	//pRootValue - pRootNode가 이 값인게 루트
	//pParentNode - 상위 여부 확인하는 node명
	//pParentValue - pParentNode가 이 값임을 확인하여 sub 노드를 조회
	
	//루트를 조회
	var arrRoot = new Array();
	try {
		var cnt = pData.length;
		for(var i = 0; i < cnt; i++) {
			if(pData[i][pRootNode] == pRootValue) {
				arrRoot.push(pData[i]);
			}
		}
	} catch(e) {
		arrRoot = null;
	}
	
	var sHtml = "";
	
	var iDepth = 0;
	var sALink = "";
	var sALink2 = "";
	
	sHtml +="<li id=\"webhard_folder_tree\">";	
	$(arrRoot).each(function (j, root) {				
		sALink = "mobile_webhard_ChangeFolder('" + root.nodeValue + "','Normal');";
		sALink2 = "mobile_webhard_openclose('ul_sub_" + root.nodeValue + "', 'span_menu_" + root.nodeValue + "');";		
		sHtml += "<ul class=\"h_tree_menu_list\">";
		sHtml += "    <li folderid=\""+root.nodeValue+"\" displayname=\"" + root.nodeName + "\">";
		sHtml += "        <a href=\"#\" class=\"t_link ui-link\"><span onclick=\""+sALink2+"\" id=\"span_menu_" + root.nodeValue + "\" class=\"t_ico_open\"></span><span class=\"t_ico_cloud\"></span><span onclick=\""+sALink+"\">" + root.nodeName + "</span></a>";
		sHtml += mobile_webhard_getTopTreeHtmlRecursive(pData, pParentNode, "", iDepth + 1);
		sHtml += "    </li>";
		sHtml += "</ul>";		
	});	
	sHtml +="</li>"
	
	return sHtml;
}

//트리  랜더링
function mobile_webhard_getTopTreeHtmlRecursive(pData, pParentNode, pParentValue, pDepth) {
	
	var sHtml = "";
	
	if(pData == null) {
		return sHtml;
	}
	
	var arrSub = new Array();
	try {
		var cnt = pData.length;
		for(var i = 0; i < cnt; i++) {
			if(pData[i][pParentNode] == pParentValue) {
				arrSub.push(pData[i]);
			}
		}
	} catch(e) {
		arrSub = null;
	}
	
	var sClass = "t_ico_open";
	var sClass2 = "t_ico_f_open";
	var sALink = "";
	var sALink2 = "";
	
	sHtml += "<ul id=\"ul_sub_" + (pParentValue == "" ? "ROOT" : pParentValue) + "\" class=\"sub_list\">";
	$(arrSub).each(function (j, sub) {		
		sALink = "mobile_webhard_ChangeFolder('" + sub.nodeValue + "','Normal');";
		sALink2 = "mobile_webhard_openclose('ul_sub_" + sub.nodeValue + "', 'span_menu_" + sub.nodeValue + "');";
		if(sub.hasChild != "0") {
			sClass = "t_ico_open";
			sClass2 = "t_ico_f_open";
		}else{
			sClass = "t_ico_space";
			sClass2 = "t_ico_board";
			sALink2 = "";
		}		
		sHtml += "    <li folderid=\"" + sub.nodeValue + "\" displayname=\"" + sub.nodeName + "\">";
		sHtml += "        <a href=\"#\" class=\"t_link ui-link\"><span onclick=\""+sALink2+"\" id=\"span_menu_" + sub.nodeValue + "\" class=\"" + sClass + "\"></span><span class=\""+sClass2+"\"></span><span onclick=\""+sALink+"\">" + sub.nodeName + "</span></a>";
		sHtml += mobile_webhard_getTopTreeHtmlRecursive(pData, pParentNode, sub.nodeValue, pDepth + 1);
		sHtml += "    </li>";
	});
	sHtml += "</ul>";
	
	return sHtml;
	
}

//하위 메뉴/트리 열고 닫기
function mobile_webhard_openclose(liId, iconId) {
	var oLi = $('#' + liId);
	var oCon = $('#' + iconId);
	if($(oCon).hasClass('t_ico_open')){		
		$(oCon).removeClass('t_ico_open').addClass('t_ico_close');
		$(oLi).hide();
	} else {
		$(oCon).removeClass('t_ico_close').addClass('t_ico_open');
		$(oLi).show();
	}
}

//폴더 변경
function mobile_webhard_ChangeFolder(folderID, folderType, boxType) {
	mobile_webhard_ViewAddFolder("init");
	if(folderType == undefined || folderType == 'undefined' || folderType == '') {
		folderType = "Normal";
	}
	
	if(boxType == undefined || boxType == 'undefined') {
		boxType = 'Receive';
	}		
	
	if(folderType == "Normal" && (folderID == undefined || folderID == 'undefined' || folderID == "ROOT")) {
		folderID = '';
	}
	_mobile_webhard_list.BoxType = boxType;
	_mobile_webhard_list.FolderID = folderID;
	_mobile_webhard_list.FolderType = folderType;
	if(folderType == "Recent"){
		mobile_webhard_ChangeView("Time");		
	}else{
		mobile_webhard_ChangeView("List");		
	}
	
	/*
	_mobile_webhard_list.BoxType = boxType;
	_mobile_webhard_list.FolderID = folderID;
	_mobile_webhard_list.FolderType = folderType;
	_mobile_webhard_list.SearchText = '';
	_mobile_webhard_list.SearchSize = '';
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.EndOfList = false;
	
	$('#mobile_search_input').val('');	
	
	mobile_webhard_getList(_mobile_webhard_list);	
	*/
}

//메뉴 변경
function mobile_webhard_ChangeMenu(folderType,pObj) {
	mobile_webhard_ChangeFolder('ROOT', folderType);
}

//웹하드 목록 조회
function mobile_webhard_getList(params) {
	
	// 공유폴더의 경우, 클릭한 폴더ID를 임시저장.
	if ( (params.FolderType === "Shared") || (params.FolderType === "Published") ) {
		$("#tmpUid").val(params.FolderID);	
	}
	
	mobile_comm_TopMenuClick('webhard_list_topmenu',true);
	
	var sPage = params.Page;
	var sPageSize = params.PageSize;
	

	if(_mobile_webhard_list.OnBack) {
		sPageSize = sPage * (sPageSize);
        sPage = 1;
	}
	
	if(params.FolderType != "Normal")
		$(".WuploadBtn").hide()
	else
		$(".WuploadBtn").show()		
		
	var url = "/webhard/mobile/webhard/user/box/select.do";
	var paramdata = {};
	if(params.ViewType == "Album"){
		paramdata = {
				viewType: params.ViewType,
				boxType: params.BoxType,
				boxUuid: params.boxUuid,
				folderID: params.FolderID,
				folderType: params.FolderType,
				searchSize: params.SearchSize,
				searchText: params.SearchText,
				startDate: '',
				endDate: '',
				sortBy: ''
			};
	}else{
		paramdata = {
				viewType: params.ViewType,
				boxType: params.BoxType,
				boxUuid: params.boxUuid,
				folderID: params.FolderID,
				folderType: params.FolderType,
				searchSize: params.SearchSize,
				searchText: params.SearchText,
				startDate: '',
				endDate: '',
				sortBy: '',
				searchType : '',
				pageNo: sPage,
				pageSize: sPageSize
			};
	}	
	
	$.ajax({
		url: url,
		data: paramdata,
		type: "POST",
		success: function (res) {
			if(res.status == "SUCCESS") {				
				var sHtml = "";
				$("#webhard_list_more").hide();
				if(params.ViewType.toUpperCase() == "ALBUM"){
					sHtml = mobile_webhard_getListHtml(res.list,"thumbnail");
					$("#webhard_thumbnail_box").html(sHtml);
				} else if(params.ViewType.toUpperCase() == "TIME"){
					sHtml = mobile_webhard_getRecentListHtml(res.list);
					$("#webhard_time_box").html(sHtml);
				} else{
					_mobile_webhard_list.TotalCount = res.page.listCount;					
					if(params.Page == 1) {
						sHtml = mobile_webhard_getListHtml(res.list,"list");
						$('#webhard_list_box').html(sHtml);
					} else {
						sHtml = mobile_webhard_getListHtml(res.list,"list",true);
						$('#webhard_list_box').append(sHtml);
					}					
					if (Math.min((_mobile_webhard_list.Page) * _mobile_webhard_list.PageSize, _mobile_webhard_list.TotalCount) == _mobile_webhard_list.TotalCount) {
						_mobile_webhard_list.EndOfList = true;
		                $('#webhard_list_more').hide();
		            } else {
		                $('#webhard_list_more').show();
		            }
					
					if(_mobile_webhard_list.OnBack) {
						_mobile_webhard_list.OnBack = false;
					}
				}
				if(params.FolderType == "Normal") {
					mobile_webhard_GetLocation(res.info);		// 상단 Location Rendering.
				}
					
				// 공유받은/공유한 폴더일 경우의 상단 Location Rendering.
				if ( (params.FolderType === "Shared") || (params.FolderType === "Published") ) {
					mobile_webhard_GetLocation(res);	
				}
				// 메뉴명 셋팅
				mobile_webhard_getTopMenuName(res.info);				
			}
			
			if($("#webhard_list_header").hasClass("edit")){
				var sTargetIDPfx = "";
				var sObjectIDPfx = "";
				var oTarget;
				var obj;
				sTargetIDPfx = _mobile_webhard_list.divChkPfx;	
				sObjectIDPfx = _mobile_webhard_list.liObjPfx;								
				obj = $("[id^='"+sObjectIDPfx+"']");				
				$(obj).find(".btn_drop_menu").hide();
				
				if(_mobile_webhard_list.opentype != ""){						
					obj = $(obj).filter(function(){
						return ($(this).attr("filetype") != "folder")
					});
					$(obj).find(".checkbox").show();
					if(_mobile_webhard_list.ViewMode != "thumbnail"){
						$(obj).find(".lzImg").hide();				
					}
				}else {
					oTarget= $("[id^='"+sTargetIDPfx+"']");
					$(oTarget).show();
					if(_mobile_webhard_list.ViewMode != "thumbnail"){
						$(obj).find(".lzImg").hide();				
					}
				}
			}
			// '내 드라이브'이외의 메뉴에서는 '확장메뉴'인 class="dropMenu" 를 노출하지 않는다.
			if (
				(_mobile_webhard_list.FolderType === "Shared") ||			// 공유받은 폴더 메뉴일 때.
				(_mobile_webhard_list.FolderType === "Published") || 		// 공유한 폴더
				(_mobile_webhard_list.FolderType === "Recent") || 			// 최근 문서함.
				(_mobile_webhard_list.FolderType === "Important") || 		// 중요 문서함.
				(_mobile_webhard_list.FolderType === "Trashbin") )  {		// 휴지통.
				
				$(".dropMenu").hide();
			} else {
				$(".dropMenu").show();
			}			
			
			if(params.ViewType.toUpperCase() == "ALBUM"){				
				$("#webhard_thumbnail_box").trigger("create");
			}else if(params.ViewType.toUpperCase() == "TIME"){
				$("#webhard_time_box").trigger("create");
			}
			else{
				$("#webhard_list_box").trigger("create");
			}
			
			$("[id^='"+_mobile_webhard_list.liObjPfx+"']").off("taphold").on("taphold", function(e){				
				mobile_webhard_showcontext(e,$(this).attr("uid"));
			});
						
			$("[id^='"+_mobile_webhard_list.divObjPfx+"']")
		     .on('touchstart', function(e) {    	 		     	 
		    	 webhardTouchX = e.originalEvent.targetTouches[0].pageX; // anchor point
		         mobile_TouchActionAction = "";
		         if(mobile_comm_isAndroidApp()) {
		     		window.covimoapp.SetPullToRefresh(false);
		     	}
		     })
		     .on('touchmove', function(e) {         	 
		         var change = e.originalEvent.targetTouches[0].pageX - webhardTouchX;
		         change = Math.min(Math.max(-(winW/2), change), 100);		         
		         if(change <= 0){
		        	 if(parseInt(-change) > 20 && mobile_TouchActionAction == "" ) {
		        		 mobile_TouchActionAction = "Action";
		        		 mobile_comm_disablescroll();    		
		        	} else if(mobile_TouchActionAction == "Action") {
		        		e.currentTarget.style.marginLeft = change + 'px';	
		        	}
		         }
		     })
		     .on('touchend', function(e) {    	 
		         var left = parseInt(e.currentTarget.style.marginLeft);
		         var width = parseInt(winW/2);
		         var new_left;		         
		         if (left <= -width) {
		        	 mobile_webhard_TargetDelete($(e.currentTarget).parent().attr("uid"),$(e.currentTarget).parent().attr("filetype"));
		        	 $(e.currentTarget).animate({marginLeft: -parseInt(winW)}, 200, function () { $(e.currentTarget).parent().detach();	});
		         } else {
		             new_left = '0px';
		             $(e.currentTarget).animate({marginLeft: new_left}, 200);
		         }
		         mobile_comm_enablescroll();
		     });		
		},
		error: function (response, status, error){
			mobile_comm_hideload();
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});
		
}

// 공유받은/공유한 폴더의 경우, 현재 폴더의 UUID를 가지고 상위폴더의 UUID값을 가지고 온다.
function mobile_webhard_prevFolder(pFolderID) {

	$("#tmpUid").val(pFolderID);	// 현재의 폴더 UUID 임시 저장(최상위 폴더 : ROOT, 하위 폴더 : UUID).
	
	var params = {"folderID" : pFolderID, "folderType" : _mobile_webhard_list.FolderType};
	var targetUrl = "/webhard/user/prevFolder.do";		// PC버전의 상위 UUID 조회 사용.
	
	$.ajax({
		url: targetUrl,
		data: params,
		type: "POST",
		success: function (res) {
			if(res.status == "SUCCESS") {
				$("#tmpFldrNm").val(res.prev.FolderName);
				mobile_webhard_GoLocation(res.prev.FolderID);	// parentUUID 로 조회.
			}
		},
		error: function (response, status, error){
			mobile_comm_ajaxerror(targetUrl, response, status, error);
		}
	});
}

//리스트 클릭 이벤트
function mobile_webhard_listOnclick(pObj){

	//휴지통일 경우 동작 하지 않음
	if (_mobile_webhard_list.FolderType === "Trashbin")  {
		return false;
	}

	// 공유폴더의 경우, 클릭한 폴더의 이름을 임시 저장해서, 상단에 보여준다.
	if (( _mobile_webhard_list.FolderType === "Shared") || ( _mobile_webhard_list.FolderType === "Published")) {
		if ($(pObj).attr("filetype") === "folder") {
			$("#tmpFldrNm").val(pObj.getAttributeNode("data-name").value);
		}
	}
	
	if(_mobile_webhard_list.opentype == "" && $("#webhard_list_header").hasClass("edit"))
		return false;
	var pUID = $(pObj).attr("uid");
	var pFileType = $(pObj).attr("filetype");
	var pFolderType = _mobile_webhard_list.FolderType;
	var pFileName = $(pObj).text();	
	if(pFileType == "folder")
		mobile_webhard_ChangeFolder(pUID,pFolderType);
	else if(_mobile_webhard_list.opentype == "")
		mobile_comm_getFile(pUID, pFileName, "", "WEBHARD");
}

function mobile_webhard_listOnclick2(pID){
	var pUID = $("#"+pID).attr("uid");
	var pFileType = $("#"+pID).attr("filetype");
	var pFolderType = _mobile_webhard_list.FolderType;
	if(pFileType == "folder")
		mobile_webhard_ChangeFolder(pUID,pFolderType);
}

//새로고침 
function mobile_webhard_clickrefresh() {	 
	 mobile_webhard_getTreeData();
	 mobile_webhard_getUsage();
	$('#mobile_search_input').val('');		
	_mobile_webhard_list.SearchText = '';
	_mobile_webhard_list.searchSize = '';
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.TotalCount = -1;
	_mobile_webhard_list.RecentCount = 0;	
	_mobile_webhard_list.EndOfList = false;
	$("#webhard_list_chkcount").text('0');		
	mobile_webhard_getList(_mobile_webhard_list);				
}

//상단 메뉴명 셋팅
function mobile_webhard_getTopMenuName(folderInfo) {	
	var sTopMenu = $('#webhard_list_topmenu').find("li").filter(":eq(0)").text();
	var sFolderID = _mobile_webhard_list.FolderID == "" ? "ROOT" : _mobile_webhard_list.FolderID;
	var sFolderType = _mobile_webhard_list.FolderType;	
	if(sFolderType == "Normal"){
		$(".btn_fileadd").show();
		$('#webhard_list_topmenu').find("li[displayname]").each(function(){		
			if($(this).attr('folderid') == sFolderID) {
				sTopMenu = $(this).attr('displayname');
				return false;
			}			
		});
	}else{
		$(".btn_fileadd").hide();
		$('#webhard_list_topmenu').find("li[displayname]").each(function(){		
			if($(this).attr('foldertype') == sFolderType) {
				sTopMenu = $(this).attr('displayname');
				return false;
			}			
		});	
		var pData = {
			folderNamePath : sTopMenu,
			folderPath : sFolderType,
			boxUuid : folderInfo.boxUuid
		};
		
		mobile_webhard_GetLocation(pData);
	}
	
	$('#webhard_list_title').html(sTopMenu);
}

//List 랜더링
function mobile_webhard_getListHtml(datalist, type, isAppend) {
	var sHtml = "";
	var sChkHtml = "";
	var sBookMark = "";
	var sBoxUID = "";
	var sCreateDate = "";
	var sFileSize = "";
	var sFileType = "";
	var sName = "";
	var sUID = "";
	var liclass ="";
	var aclass ="";
	var bclass ="";
	var dclass ="";
	var lzclass ="";		
	var sShareImg = "<span class=\"ic_share\"></span>";
	var sFolderImg = "<span class=\"ic_folder\"></span>";
	var sPdfImg = "<span class=\"ic_pdf\"></span>";
	var sWordImg = "<span class=\"ic_word\"></span>";
	var sExelImg = "<span class=\"ic_xls\"></span>";
	var sPptImg = "<span class=\"ic_ppt\"></span>";
	var sZipImg = "<span class=\"ic_zip\"></span>";
	var sEtcImg = "<span class=\"ic_etc\"></span>";
	var bIsShared = (_mobile_webhard_list.FolderType == "Shared"|| _mobile_webhard_list.FolderType == "Published") ? true : false;
	
	if(datalist.length > 0) {
		if(isAppend == undefined || isAppend != false){
			sHtml = "<ul class=\"uio_"+type+"\">";
		}			
		$(datalist).each(function (i, data){
			sChkHtml = "";
			sBookMark = data.bookmark;
			sBoxUID = data.box_uid;
			//sCreatedDate = data.createdDate;
			sFileSize = data.fileSize;
			sFileType = data.fileType;
			sName = data.name;			
			sUID = data.uid;
			
			if(type =="thumbnail"){
				liclass ="ut_item";
				aclass ="ut_a";
				bclass ="ut_b";
				dclass ="ut_d";
				lzclass ="lzImg";					
			}else{
				liclass ="utl_item";
				aclass ="utl_a";
				bclass ="utl_b";
				dclass ="utl_d";
				lzclass ="lzImg List";					
			}
			
			sChkHtml += "<div id=\"divchk_webhard_"+type+"_"+sUID+"\" class=\"checkbox\" style=\"display:none;\">";
			sChkHtml += "<div class=\"ui-checkbox\">";
			sChkHtml += "<label for=\"chk_webhard_"+type+"_"+sUID+"\" class=\"ui-btn ui-corner-all ui-btn-inherit ui-btn-icon-left ui-checkbox-off\"></label><input type=\"checkbox\" name=\"chk_webhard_"+type+"_"+sUID+"\" value=\"\" id=\"chk_webhard_"+type+"_"+sUID+"\" onchange=\"mobile_webhard_CheckboxOnchange(this);\">";
			sChkHtml += "</div>";
			sChkHtml += "</div>";			
			sHtml += "<li id=\"li_webhard_"+type+"_"+sUID+"\" data-name=\""+sName+"\" boxuid=\""+sBoxUID+"\" filetype=\""+sFileType+"\" uid=\""+sUID+"\" filesize=\""+sFileSize+"\" class=\""+liclass+"\" onclick=\"mobile_webhard_listOnclick(this);\">";
			sHtml += "<div id=\"div_webhard_"+type+"_"+sUID+"\" class=\"webhard_list_li\">";
			sHtml += "<a href=\"javascript:void(0);\" class=\""+aclass+"\">";
			if(type == "list")
				sHtml += sChkHtml;
			switch(sFileType){
				case "folder" :			
					sHtml += "<span class=\""+lzclass+"\">"+sFolderImg +(bIsShared? sShareImg : "") +"</span>";
					break;
				case "pdf" :					
					sHtml += "<span class=\""+lzclass+"\">"+sPdfImg+(bIsShared? sShareImg : "") +"</span>";
					break;
				case "doc" :	
				case "docx" :
					sHtml += "<span class=\""+lzclass+"\">"+sWordImg+(bIsShared? sShareImg : "") +"</span>";
					break;
				case "xls" :	
				case "xlsx" :
					sHtml += "<span class=\""+lzclass+"\">"+sExelImg+(bIsShared? sShareImg : "") +"</span>";
					break;
				case "ppt" :	
				case "pptx" :
					sHtml += "<span class=\""+lzclass+"\">"+sPptImg+(bIsShared? sShareImg : "") +"</span>";
					break;
				case "zip" :
					sHtml += "<span class=\""+lzclass+"\">"+sZipImg+(bIsShared? sShareImg : "") +"</span>";
					break;
				default :
				sHtml += "<span class=\""+lzclass+"\">"+sEtcImg+(bIsShared? sShareImg : "") +"</span>";
					break;
			}								
			if(type == "list"){
				sHtml += "</a>";
				sHtml += "<a href=\"javascript:void(0);\" class=\""+bclass+"\">";
			}
			//mobile_comm_getDateTimeString2("LIST",sCreatedDate)
			sHtml += "<span class=\""+dclass+"\">"+sName+"</span>";
			if(type == "thumbnail")
				sHtml += sChkHtml;
	
			sHtml += "</a>";				
			//sHtml += "<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_showcontext(event,'"+sUID+"');return false;\" class=\"btn_drop_menu ui-link\"></a>";
			sHtml += "<div id=\"div_webhard_exmenu_"+type+"_"+sUID+"\" class=\"exmenu_layer\"></div>";
			
			// 21.09.15. 공유받은/공유한 폴더의 경우만 북마크 표시 안보이게 합니다.
			if (!bIsShared) {
				if(sBookMark == "Y")
					sHtml += "<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_bookmarkclick(event,'"+sUID+"');\" class=\"bookmark ui-link active\"></a>";
				else
					sHtml += "<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_bookmarkclick(event,'"+sUID+"');\" class=\"bookmark ui-link\"></a>";
			}
			
			sHtml +="</div>";
			sHtml += "<div class=\"listswipe delete\"><div class=\"delete_ico\"></div></div>";
			sHtml += "</li>";			
		});
		
		if(isAppend == undefined || isAppend != false){
			sHtml += "</ul>";
		}	
	}else{
		if(isAppend == undefined || isAppend != false){
			sHtml += "<div class=\"no_list\" style=\"padding-top: 50px;\">";
			sHtml += "    <p>" + mobile_comm_getDic("msg_NoDataList") + "</p>";//조회할 목록이 없습니다.
			sHtml += "</div>";
		}
	}
	
	return sHtml;
}

function mobile_webhard_getRecentListHtml(datalist, isAppend) {
	var sHtml = "";
	var listHtml = "";
	var sTodayHtml = "";
	var sPrevHtml = "";
	var sChkHtml = "";
	var sBookMark = "";
	var sBoxUID = "";
	var sCreatedDate = "";
	var sCreateDate = "";
	var sFileSize = "";
	var sFileType = "";
	var sName = "";
	var sUID = "";
	var liclass ="";
	var aclass ="";
	var bclass ="";
	var dclass ="";
	var lzclass ="";		
	var sFolderImg = "<span class=\"ic_folder\"></span>";
	var sPdfImg = "<span class=\"ic_pdf\"></span>";
	var sWordImg = "<span class=\"ic_word\"></span>";
	var sExelImg = "<span class=\"ic_xls\"></span>";
	var sPptImg = "<span class=\"ic_ppt\"></span>";
	var sZipImg = "<span class=\"ic_zip\"></span>";
	var sEtcImg = "<span class=\"ic_etc\"></span>";
	
	if(datalist.length > 0) {
		if(isAppend == undefined || isAppend != false){
			//sHtml = "<ul class=\"uio_list Rdoc\">";
		}			
		$(datalist).each(function (i, data){
			sHtml = "";
			sChkHtml = "";
			sBookMark = data.bookmark;
			sBoxUID = data.box_uid;
			sCreatedDate = data.createdDate;
			sFileSize = data.fileSize;
			sFileType = data.fileType;
			sName = data.name;			
			sUID = data.uid;
			
			liclass ="utl_item";
			aclass ="utl_a";
			bclass ="utl_b";
			dclass ="utl_d";
			lzclass ="lzImg List";	
			
			sChkHtml += "<div id=\"divchk_webhard_time_"+sUID+"\" class=\"checkbox\" style=\"display:none;\">";
			sChkHtml += "<div class=\"ui-checkbox\">";
			sChkHtml += "<label for=\"chk_webhard_time_"+sUID+"\" class=\"ui-btn ui-corner-all ui-btn-inherit ui-btn-icon-left ui-checkbox-off\"></label><input type=\"checkbox\" name=\"chk_webhard_time_"+sUID+"\" value=\"\" id=\"chk_webhard_time_"+sUID+"\" onchange=\"mobile_webhard_CheckboxOnchange(this);\">";
			sChkHtml += "</div>";
			sChkHtml += "</div>";			
			sHtml += "<li id=\"li_webhard_time_"+sUID+"\" data-name=\""+sName+"\" boxuid=\""+sBoxUID+"\" filetype=\""+sFileType+"\" uid=\""+sUID+"\" filesize=\""+sFileSize+"\" class=\""+liclass+"\" onclick=\"mobile_webhard_listOnclick(this);\">";
			sHtml += "<div id=\"div_webhard_time_"+sUID+"\" class=\"webhard_list_li\">";
			sHtml += "<a href=\"javascript:void(0);\" class=\""+aclass+"\">";
			sHtml += sChkHtml;
			
			switch(sFileType){
				case "folder" :			
					sHtml += "<span class=\""+lzclass+"\">"+sFolderImg +"</span>";
					break;
				case "pdf" :					
					sHtml += "<span class=\""+lzclass+"\">"+sPdfImg+"</span>";
					break;
				case "doc" :	
				case "docx" :
					sHtml += "<span class=\""+lzclass+"\">"+sWordImg+"</span>";
					break;
				case "xls" :	
				case "xlsx" :
					sHtml += "<span class=\""+lzclass+"\">"+sExelImg+"</span>";
					break;
				case "ppt" :	
				case "pptx" :
					sHtml += "<span class=\""+lzclass+"\">"+sPptImg+"</span>";
					break;
				case "zip" :
					sHtml += "<span class=\""+lzclass+"\">"+sZipImg+"</span>";
					break;
				default :
				sHtml += "<span class=\""+lzclass+"\">"+sEtcImg+"</span>";
					break;
			}								
			
			sHtml += "</a>";
			sHtml += "<a href=\"javascript:void(0);\" class=\""+bclass+"\">";
			
			//mobile_comm_getDateTimeString2("LIST",sCreatedDate)
			sHtml += "<span class=\""+dclass+"\">"+sName+"</span>";
	
			sHtml += "</a>";				
			//sHtml += "<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_showcontext(event,'"+sUID+"');return false;\" class=\"btn_drop_menu ui-link\"></a>";
			sHtml += "<div id=\"div_webhard_exmenu_time_"+sUID+"\" class=\"exmenu_layer\"></div>";
			
			if(sBookMark == "Y")
				sHtml += "<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_bookmarkclick(event,'"+sUID+"');\" class=\"bookmark ui-link active\"></a>";
			else
				sHtml += "<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_bookmarkclick(event,'"+sUID+"');\" class=\"bookmark ui-link\"></a>";
			sHtml +="</div>";
			sHtml += "<div class=\"listswipe delete\"><div class=\"delete_ico\"></div></div>";
			sHtml += "</li>";			
			
			if(mobile_comm_getDateTimeString("yyyy/MM/dd",new Date(mobile_comm_ReplaceDate(sCreatedDate))) == mobile_comm_getDateTimeString("yyyy/MM/dd",new Date())){
				sTodayHtml += sHtml;								
			} else{
				sPrevHtml += sHtml;
			}			
		});
						
		if(isAppend == undefined || isAppend != false){
			//sHtml += "</ul>";
		}
		
		if(sTodayHtml != ""){			
			listHtml += "<div class=\"area_category\">"+mobile_comm_getDic("btn_Today")+"</div>";
			listHtml += "<ul class=\"uio_list Rdoc\">" + sTodayHtml + "</ul>"; 			
		}
		
		if(sPrevHtml != ""){
			listHtml += "<div class=\"area_category\">"+mobile_comm_getDic("btn_apv_previous")+"</div>";			
			listHtml += "<ul class=\"uio_list Rdoc\">" + sPrevHtml + "</ul>"; 
		}		
		
	}else{
		if(isAppend == undefined || isAppend != false){
			listHtml += "<div class=\"no_list\" style=\"padding-top: 50px;\">";
			listHtml += "    <p>" + mobile_comm_getDic("msg_NoDataList") + "</p>";//조회할 목록이 없습니다.
			listHtml += "</div>";
		}
	}
	
	return listHtml;
}


//Context Menu Show
function mobile_webhard_showcontext(e,pUID){		
	// '휴지통'과 '공유받은문서' 메뉴에서는 context menu를 사용하지 않습니다.
	if ((_mobile_webhard_list.FolderType == "Trashbin") || (_mobile_webhard_list.FolderType == "Shared")) {
		e.stopPropagation()
		return false;
	}
	
	var sObjectID =  "";	
	var sHtml = "";			
	sObjectID = _mobile_webhard_list.liObjPfx + pUID;	
	sHtml += "<div class=\"card_list_popup mail_list_delete_popup\" style=\"top:40%;\">";
	sHtml += "	<div class=\"card_list_popup_cont\" style=\"height: 100%;\">";
	sHtml += "		<div class=\"portal_config\">";
	sHtml += "			<ul>";

	// '공유한문서'메뉴 그 외의 메뉴의 기능 구분
	if (_mobile_webhard_list.FolderType === "Published") {
			sHtml += "				<li class=\"portal_config_list01\">";	
			sHtml += "					<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_contextclick(event,'"+pUID+"','share');\" class=\"ui-link\"><span>"+mobile_comm_getDic("lbl_ShareWebhard")+"</span></a>"; //공유하기		
			sHtml += "				</li>";
	} else {
		if (_mobile_webhard_list.FolderType == "Normal") {
			sHtml += "				<li class=\"portal_config_list01\">";	
			sHtml += "					<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_contextclick(event,'"+pUID+"','share');\" class=\"ui-link\"><span>"+mobile_comm_getDic("lbl_ShareWebhard")+"</span></a>"; //공유하기		
			sHtml += "				</li>";
		}
		sHtml += "				<li class=\"portal_config_list01\">";	
		sHtml += "					<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_contextclick(event,'"+pUID+"','copy');\" class=\"ui-link\"><span>"+mobile_comm_getDic('btn_Copy')+"</span></a>"; //복사		
		sHtml += "				</li>";			
		sHtml += "				<li class=\"portal_config_list01\">";	
		sHtml += "					<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_contextclick(event,'"+pUID+"','move');\" class=\"ui-link\"><span>"+mobile_comm_getDic('btn_Move')+"</span></a>"; //이동		
		sHtml += "				</li>";			
		sHtml += "				<li class=\"portal_config_list01\">";	
		sHtml += "					<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_contextclick(event,'"+pUID+"','del');\" class=\"ui-link\"><span>"+mobile_comm_getDic('btn_delete')+"</span></a>"; //삭제		
		sHtml += "				</li>";
	}
		
	/* PC 버전과 맞추기 위해 주석처리
	if(_mobile_webhard_list.FolderType == "Shared"){
		sHtml += "				<li class=\"portal_config_list01\">";	
		sHtml += "					<a href=\"javascript:void(0);\" onclick=\"mobile_webhard_contextclick(event,'"+pUID+"','sharerview');\" class=\"ui-link\"><span>"+mobile_comm_getDic("lbl_SharedMemberView")+"</span></a>"; //공유멤버보기		
		sHtml += "				</li>";
	}
	*/
		
	sHtml += "			</ul>";
	sHtml += "		</div>";		
	sHtml += "	</div>";	
	sHtml += "</div>";
	$("#webhard_list_popup").html(sHtml).show("slide");
	e.stopPropagation();
}

//컨텍스트 메뉴 클릭
function mobile_webhard_contextclick(e,pUID,pMode){	
	var sObjID = "";	
	$("#hidUID").val(pUID);
	
	sObjID = _mobile_webhard_list.liObjPfx + pUID;	
	
	switch(pMode){
		case "manage":			
			break;
		case "copy":
			$( 'html, body' ).animate( { scrollTop : 0 }, 400 );
			mobile_webhard_ViewMoveAndCopy("C");
			
			$("#webhard_list_copy").attr("href","javascript:mobile_webhard_MoveandCopy('"+pUID+"','"+$("#"+sObjID).attr("filetype")+"','C','N');");
			break;
		case "move":
			$( 'html, body' ).animate( { scrollTop : 0 }, 400 );
			mobile_webhard_ViewMoveAndCopy("M");
			$("#webhard_list_move").attr("href","javascript:mobile_webhard_MoveandCopy('"+pUID+"','"+$("#"+sObjID).attr("filetype")+"','M','N');");			
			break;
		case "del":		
			var result = confirm(mobile_comm_getDic('msg_152'));
			
			if(!result)
				break;
			mobile_webhard_TargetDelete(pUID,$("#"+sObjID).attr("filetype"));			
			break;
		case "edit":
			$("#txtWebhardFolderName").val($("#"+sObjID).attr("data-name"));			
			mobile_webhard_ViewAddFolder("edit");					
			break;
		case "sharerview":
			mobile_webhard_GoShareMember($("#"+sObjID).attr("filetype"),pUID,"view");
			break;
		case "share":
			mobile_webhard_GoShareMember($("#"+sObjID).attr("filetype"),pUID,"share");
			break;		
	}		
	
	$("#webhard_list_popup").hide();    
    
    e.stopPropagation();
}

//공유자조회/추가 페이지 이동
function mobile_webhard_GoShareMember(pFileType,pUID,pMode){
	mobile_comm_go('/webhard/mobile/webhard/popupShare.do?mode='+pMode+"&filetype="+(pFileType !="folder" ? "file" : pFileType)+"&uid="+encodeURIComponent(pUID));
}

//웹하드 공유자 목록 조회
function mobile_webhard_getSharedList(params) {
	var url = "/webhard/mobile/webhard/user/shared/selectMember.do";
	var paramdata = {};
	paramdata = {					
			sharedID: params.UID,		
			sharedType: params.FileType			
		};	

	$.ajax({
		url: url,
		data: paramdata,
		type: "POST",
		success: function (res) {
			if(res.status == "SUCCESS") {				
				var sHtml = "";
				var lists = res.list;				
				if(lists.length >0){					
					$(lists).each(function (i, list){										
						var sSharedOwnerID = list.sharedOwnerID;
						var sSharedGrantType = list.sharedGrantType;
						var sSharedOwnerName = mobile_comm_getDicInfo(list.sharedOwnerName);
						var sSharedOwnerJobLevelName = mobile_comm_getDicInfo(list.sharedOwnerJobLevelName);
						var sPhotoPath = list.sharedOwnerPhotoPath;
						var sSharedGroupName = mobile_comm_getDicInfo(list.sharedOwnerDeptName);						
						//if(sSharedGrantType == "UR"){
						//	sHtml += mobile_webhard_makeSharedMemberHtml("OLD",sSharedGrantType, sSharedOwnerID, sPhotoPath, sSharedGroupName,sSharedOwnerName,sSharedOwnerJobLevelName);
						//}else{
							sHtml += mobile_webhard_makeSharedMemberHtml("OLD",sSharedGrantType, sSharedOwnerID, sPhotoPath, sSharedGroupName,sSharedOwnerName,sSharedOwnerJobLevelName);
						//}						
					});
					$("#webhard_shared_list").html(sHtml);
				}
			}
		},
		error: function (response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});		
}

//조직도 호출
function mobile_webhard_openOrg() {
	var sUrl = "/covicore/mobile/org/list.do";

	window.sessionStorage["mode"] = "Select";
	window.sessionStorage["multi"] = "Y";
	window.sessionStorage["callback"] = "mobile_webhard_setSharedMemberOrgMap();";
	
	mobile_comm_go(sUrl, 'Y');
}

//조직도 콜백 함수 - 공유대상
function mobile_webhard_setSharedMemberOrgMap() {
	//var sHtml = "";
	var arrSharedCode = new Array();
	var dataObj = JSON.parse("[" + window.sessionStorage["userinfo"] + "]");
	var sExt = "|";
	$("[id^='liSharedMember_']").each(function(i){
		sExt += $(this).attr("code")+"|";
	});
	$(dataObj).each(function(i){
		var userCode = $$(this).attr("AN");
		var itemtype =  $$(this).attr("itemType");

		if(sExt.indexOf("|"+userCode+"|") > -1)
			return;		
		if(itemtype == "user")
			itemtype = "UR";
		else
			itemtype = "GR";
		
		arrSharedCode.push(userCode+"|"+itemtype);				
	});

	if(arrSharedCode.length >0)
		mobile_webhard_regSharedMember(arrSharedCode);
}

//공유자 자동완성 추가
function webhard_share_autoTag(pObj){
	var sItemType = $(pObj).attr("itemtype");
	var sCode = $(pObj).attr("code");
	var sExt = "|";

	$("#webhard_share_divAutoComplete").hide();
	
	$("#txtwebhard_sharemember").val("");
	
	$("[id^='liSharedMember_']").each(function(i){
		sExt += $(this).attr("code")+"|";
	});
	if(sExt.indexOf("|"+sCode+"|") > -1){
		alert(mobile_comm_getDic("msg_board_alreadyAdded"));		
		return;
	}
			
	var arrSharedCode = new Array();	
	arrSharedCode.push(sCode+"|"+sItemType);	
	mobile_webhard_regSharedMember(arrSharedCode);
}

//공유 맴버 랜더링
function mobile_webhard_makeSharedMemberHtml(pStatus,pItemType, pCode, pPhotoPath, pGroupName,pUserName,pLevelName){
	var sHtml = "";
	//if(pItemType == "UR"){
	//	sHtml +="<li id=\"liSharedMember_"+pCode+"\" status=\""+pStatus+"\" itemtype=\""+pItemType+"\" code=\""+pCode+"\" class=\"staff\">";
	//	sHtml += "	<a href=\"#\" class=\"con_link\">";						
	//	sHtml += "		<span class=\"photo\" style=\"background-image:url('"+pPhotoPath+"')\"></span>";						
	//	sHtml += "		<div class=\"info\">";						
	//	sHtml += "			<p class=\"name\">"+pUserName+"</p>";
	//	sHtml += "			<p class=\"detail\"><span class=\"team\">"+pGroupName+"</span><span>"+mobile_comm_getDicInfo(pLevelName)+"</span></p>";						
	//	sHtml += "		</div>";
	//	sHtml += "	</a>";
	//	if(_mobile_webhard_share.mode == "share")
	///		sHtml += "<a href=\"javascript:mobile_webhard_deleteSharedMember('liSharedMember_"+pCode+"');\" class=\"Smember_del\"><span>삭제</span></a>";
	//	sHtml += "</li>";
	//}else{
		sHtml +="<li id=\"liSharedMember_"+pCode+"\" status=\""+pStatus+"\" itemtype=\""+pItemType+"\" code=\""+pCode+"\" class=\"staff\">";
		sHtml += "	<a href=\"#\" class=\"con_link\">";						
		sHtml += "		<span class=\"photo\" style=\"background-image:url('" + mobile_comm_getimg(pPhotoPath) + "'), url('" + mobile_comm_noperson() + "')\"></span>";						
		sHtml += "		<div class=\"info\">";						
		sHtml += "			<p class=\"name\">"+pUserName+"</p>";
		sHtml += "			<p class=\"detail\"><span class=\"team\">"+pGroupName+"</span><span>"+mobile_comm_getDicInfo(pLevelName)+"</span></p>";						
		sHtml += "		</div>";
		sHtml += "	</a>";
		if(_mobile_webhard_share.mode == "share")
			sHtml += "<a href=\"javascript:mobile_webhard_deleteSharedMember('liSharedMember_"+pCode+"');\" class=\"Smember_del\"><span>삭제</span></a>";
		sHtml += "</li>";
	//}	
	return sHtml;
}

//공유자 삭제
function mobile_webhard_deleteSharedMember(pTargetID){
	var obj = $("#"+pTargetID);
	var sItemtype = $(obj).attr("itemtype"); 
	var sCode =$(obj).attr("code");
	var sStatus =$(obj).attr("status");
	if(sStatus == "NEW"){
		$(obj).remove();		
	}else{
		var url = "/webhard/mobile/webhard/user/shared/unshareTarget.do";
		var arrSharedCode = new Array();
		arrSharedCode.push(sCode+"|"+sItemtype);
		
		$.ajax({
			url : url,
			type : "POST",
			data : {
				targetType : _mobile_webhard_share.FileType,	// Folder / File
				targetUuid : _mobile_webhard_share.UID,			// 공유대상 UUID
				unsharedTo : arrSharedCode.join(";")			// 공유할 부서 또는 사용자 ID
			},
			success: function (result) {
				if(result.status !== "SUCCESS"){
					alert(mobile_comm_getDic("msg_ErrorProcessRequest"));
				}else{
					$(obj).remove();	
				}
			},
			error: function (response, status, error) {
				mobile_comm_ajaxerror(url, response, status, error);
			}
		});	
	}
}

//공유자 초대하기
function mobile_webhard_addSharedMember(){
	var arrSharedCode = new Array();
	var obj = $("[id^='liSharedMember_'][status='NEW']");
	if($(obj).length == 0){
		alert(mobile_comm_getDic('msg_ValidationTargetShare'));
		return;
	}
	
	$(obj).each(function(i,target){
		arrSharedCode.push($(target).attr("code")+"|"+$(target).attr("itemtype"));
	});
	
	var url = "/webhard/mobile/webhard/user/shared/shareTarget.do";
	
	$.ajax({
		url : url,
		type : "POST",
		data : {
			targetType : _mobile_webhard_share.FileType,	// Folder / File
			targetUuid : _mobile_webhard_share.UID,			// 공유대상 UUID
			sharedTo : arrSharedCode.join(";")				// 공유할 부서 또는 사용자 ID
		},
		success: function (result) {
			if(result.status === "SUCCESS"){
				mobile_comm_back();
			}else{
				alert(mobile_comm_getDic("msg_ErrorProcessRequest"));				
			}						
		},
		error: function (response, status, error) {
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});	
	
}

//공유자 초대하기
function mobile_webhard_regSharedMember(pArrCode){
	if(pArrCode == undefined){
		alert(mobile_comm_getDic('msg_ValidationTargetShare'));
		return;
	}
	if(pArrCode.length == 0){
		alert(mobile_comm_getDic('msg_ValidationTargetShare'));
		return;
	}			
	
	var url = "/webhard/mobile/webhard/user/shared/shareTarget.do";
	
	$.ajax({
		url : url,
		type : "POST",
		data : {
			targetType : _mobile_webhard_share.FileType,	// Folder / File
			targetUuid : _mobile_webhard_share.UID,			// 공유대상 UUID
			sharedTo : pArrCode.join(";")					// 공유할 부서 또는 사용자 ID
		},
		success : function (result) {
			if(result.status === "SUCCESS"){
				mobile_webhard_share_ListInit();
			}else{
				alert(mobile_comm_getDic("msg_ErrorProcessRequest"));				
			}						
		},
		error : function (response, status, error) {
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});	
	
}

//북마크 클릭
function mobile_webhard_bookmarkclick(e,pUID){
	var sObjectID =  "";	
	var sflagValue = "";
	var sFileType = "";
	var obj;
	sObjectID = _mobile_webhard_list.liObjPfx + pUID;
	obj = $("#"+sObjectID);
	if($(obj).find(".bookmark").hasClass("active")){
		sflagValue = "N";
	}else{
		sflagValue = "Y";
	}
	
	sFileType = $(obj).attr("filetype");
	
	var url = "/webhard/mobile/webhard/user/bookmark.do";	
	
	$.ajax({
		type:"POST",
		data:{
			"uid" : pUID,
			"fileType": sFileType,
			"flagValue": sflagValue
		},
		url: url,
		success:function (res) {
			if(res.status == "SUCCESS"){
				$(obj).find(".bookmark").toggleClass('active');
			}
		},
		error:function(response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});
	
	e.stopPropagation();
}

//더보기 클릭
function mobile_webhard_nextlist() {		
	if (!_mobile_webhard_list.EndOfList) {
		_mobile_webhard_list.Page++;
		mobile_webhard_getList(_mobile_webhard_list);
    } else {
        $('#webhard_list_more').hide();
    }	
}

//스크롤 더보기
function mobile_webhard_list_page_ListAddMore(){
	mobile_webhard_nextlist();
}

//검색 닫기
function mobile_webhard_closesearch() {	
	_mobile_webhard_list.SearchText = '';
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.EndOfList = false;
	$('#mobile_search_input').val('');		
	mobile_webhard_getList(_mobile_webhard_list);	
}

//멀티 선택 변경
function mobile_webhard_ChangeMultiMode() {	
	var sTargetIDPfx = "";
	var sObjectIDPfx = "";
	
	sTargetIDPfx = _mobile_webhard_list.divChkPfx;	
	sObjectIDPfx = _mobile_webhard_list.liObjPfx;
	
	mobile_comm_TopMenuClick('webhard_list_topmenu',true);
	$(".dropMenu").removeClass('show');
	$("#webhard_list_loc").show();
	$("#webhard_list_add").hide();
	$("#webhard_list_moveandcopy").hide();
	$("#webhard_list_move").attr("href","javascript:mobile_webhard_MoveandCopy('','','M','Y');");
	$("#webhard_list_copy").attr("href","javascript:mobile_webhard_MoveandCopy('','','C','Y');");	
	$("[id^='"+_mobile_webhard_list.chkPfx+"']").checkboxradio();
	mobile_webhard_AllCheck('init');
	$("#webhard_list_chkcount").text('0');
	
	
	if($("#webhard_list_header").hasClass("edit")){
		$("#webhard_list_header").removeClass("edit");
		$("#webhard_list_subheader").show();
		$("#webhard_list_editsubheader").hide();
		$("[id^='"+sTargetIDPfx+"']").hide();
		$("[id^='"+sObjectIDPfx+"']").find(".btn_drop_menu").show();
		if(_mobile_webhard_list.ViewMode != "thumbnail"){
			$("[id^='"+sObjectIDPfx+"']").find(".lzImg").show();	
		}
	}else{
		$("#webhard_list_header").addClass("edit");
		if(_mobile_webhard_list.opentype == ""){
			$("#webhard_list_subheader").hide();
			$("#webhard_list_editsubheader").show();
		}else{
			$("#webhard_list_subbackbtn").attr("href","javascript:mobile_comm_back();");		
			$("#webhard_list_util").hide();
			$("#webhard_list_popuputil").show();
		}
		
		$("[id^='"+sTargetIDPfx+"']").show();
		$("[id^='"+sObjectIDPfx+"']").find(".btn_drop_menu").hide();
		if(_mobile_webhard_list.ViewMode != "thumbnail"){
			$("[id^='"+sObjectIDPfx+"']").find(".lzImg").hide();			
		}
	}		
}

//폴더 추가 영역 활성화
function mobile_webhard_ViewAddFolder(pMode){
	mobile_comm_TopMenuClick('webhard_list_topmenu',true);
	$("#webhard_list_moveandcopy").hide();	
	if($("#webhard_list_loc").css("display") == "none" || pMode == "init"){
		$("#webhard_list_loc").css("display","");
		$("#webhard_list_add").css("display","none");			
	}else{
		$("#webhard_list_loc").css("display","none");
		$("#webhard_list_add").css("display","");	
		$( 'html, body' ).animate( { scrollTop : 0 }, 400 );
		if(pMode == "Add"){
			$("#txtWebhardFolderName").val("");
			$("#btnFolderAdd").css("display","");
			$("#btnFolderEdit").css("display","none");
		}else{
			$("#btnFolderAdd").css("display","none");
			$("#btnFolderEdit").css("display","");
		}
	}
}

//폴더 추가
function mobile_webhard_addFolder(pMode){
	var sUID =$("#hidUID").val(); 
	var sFolderName = $("#txtWebhardFolderName").val().trim();
	var spePatt = /[`~!@#$%^&*|\\\'\";:\/?(){}\[\]¢™$®]/gi;
	
	if(!sFolderName){
		alert(mobile_comm_getDic('msg_apv_146')); // 공백은 등록할 수 없습니다.
		return;
	}else if(spePatt.test(sFolderName)){
		alert(mobile_comm_getDic("msg_specialNotAllowed")); // 특수문자는 사용할 수 없습니다.
		return false;
	}
	
	var url = "";
	
	if(pMode == "Add"){
		var saveData = {
			"boxUuid": _mobile_webhard_list.boxUuid,
			"parentUuid": _mobile_webhard_list.FolderID,
			"folderName": sFolderName,
			"folderType" : _mobile_webhard_list.FolderType
		};
		
		url = "/webhard/mobile/webhard/user/createFolder.do";		
		
		$.ajax({
			url : url,
			type : "POST",
			data : JSON.stringify(saveData),
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success : function (res) {
				if(res.status == "SUCCESS") {
					alert(mobile_comm_getDic('msg_com_processSuccess'));
					mobile_webhard_ViewAddFolder('Add')
					mobile_webhard_clickrefresh();
				}else{
					alert(mobile_comm_getDic('lbl_Fail'));
				}
			},
			error: function (response, status, error){
				mobile_comm_ajaxerror(url, response, status, error);
			}
		});	
	}else{
		url = "/webhard/mobile/webhard/user/updateFolderName.do";
		var param = {
			boxUuid: _mobile_webhard_list.boxUuid,
			folderUuid : sUID,
			newFolderName : sFolderName
		};
		$.ajax({
			url: url,
			data: JSON.stringify(param),
			type: "post",
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success: function (res) {			
				if(res.status == "SUCCESS"){
					alert(mobile_comm_getDic('msg_com_processSuccess'));
					mobile_webhard_ViewAddFolder('Edit')
					mobile_webhard_clickrefresh();
				}else{
					alert(mobile_comm_getDic('lbl_Fail'));
				}
			},
			error: function (response, status, error){
				mobile_comm_ajaxerror(url, response, status, error);
			}
		});
	}
		
}

//show class add or remove
function mobile_webhard_ToggleShowClass(pObj,pType) {	
	if($(pObj).hasClass('show') || pType == "init") {
		$(pObj).removeClass('show');		
	}else{
		if(_mobile_webhard_list.FolderType == "Trashbin"){
			$("#btnRestore").show();
			$("#btnMove").hide();
			$("#btnCopy").hide();
		}else{
			$("#btnMove").show();
			$("#btnCopy").show();
			$("#btnRestore").hide();
		}
		$(pObj).addClass('show');		
		
	}
}

//이동/복사 영역 활성화
function mobile_webhard_ViewMoveAndCopy(pMode){
	$("#webhard_list_move").attr("href","javascript:mobile_webhard_MoveandCopy('','','M','Y');");
	$("#webhard_list_copy").attr("href","javascript:mobile_webhard_MoveandCopy('','','C','Y');");
	if($("#webhard_list_loc").css("display") == "none"){
		$("#webhard_list_loc").css("display","");
		$("#webhard_list_moveandcopy").css("display","none");
	}else{
		$("#webhard_list_loc").css("display","none");
		$("#webhard_list_moveandcopy").css("display","");
		$( 'html, body' ).animate( { scrollTop : 0 }, 400 );
	}		
	if(pMode =="M"){
		$("#webhard_list_move").show();
		$("#webhard_list_copy").hide();
	}else if(pMode =="C"){
		$("#webhard_list_move").hide();
		$("#webhard_list_copy").show();
	}
}

//복원
function mobile_webhard_Restore(){

	var folderUuids = [], fileUuids = [];
	var sTargetIDPfx = _mobile_webhard_list.chkPfx;
	var sObjectIDPfx = _mobile_webhard_list.liObjPfx;
	
	$("[id^='"+sTargetIDPfx+"']:checked").each(function(){
		var sObjID = $(this).attr("id").replace(sTargetIDPfx,sObjectIDPfx);
		var obj = $("#"+sObjID);
		var sUID = $(obj).attr("uid");
		var sFileType = $(obj).attr("filetype");
		
		if(sFileType === "folder"){
			folderUuids.push(sUID);
		}else{
			fileUuids.push(sUID);
		}
	});
	
	if(folderUuids.length === 0 && fileUuids.length === 0){
		alert(mobile_comm_getDic("msg_apv_003")); // 선택된 항목이 없습니다.
		return false;
	}
	
	var pFileUuids = fileUuids.join(";");
	var pFolderUuids = folderUuids.join(";");
	
	$.ajax({
		url: "/webhard/mobile/webhard/user/restore.do",
		type: "POST",
		data: {
			fileUuids: (pFileUuids ? pFileUuids : ""),
			folderUuids: (pFolderUuids ? pFolderUuids : "")
		},
		success: function(res){	
			if(res.status === 'SUCCESS'){
				alert(mobile_comm_getDic('msg_Common_36'));
	   		}else{
	   			alert(mobile_comm_getDic('lbl_Fail'));
	   		}
			mobile_webhard_clickrefresh();						
		},
		error: function(response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});	
	
}



//검색
function mobile_webhard_clicksearch(){
	_mobile_webhard_list.Page = 1;
	_mobile_webhard_list.EndOfList = false;	
	_mobile_webhard_list.SearchText = $('#mobile_search_input').val();
	
	mobile_webhard_getList(_mobile_webhard_list);
}

//이동/복사
function mobile_webhard_MoveandCopy(pUID, pType, pMode, pIsMulti){
	var sources = {
		  boxUuid: _mobile_webhard_list.boxUuid
		, folderUuid: _mobile_webhard_list.FolderID
	};
	var targets = {
		  boxUuid: _mobile_webhard_list.boxUuid
		, folderUuid: $("#webhard_list_ddlseltype").val() === "ROOT" ? "" : $("#webhard_list_ddlseltype").val()
	};
	
	var url = "";
	var folderUuids = [];
	var fileUuids = [];
	
	if(pMode === "C"){
		url = "/webhard/mobile/webhard/user/copyTarget.do";
	}else if(pMode === "M"){
		url = "/webhard/mobile/webhard/user/moveTarget.do";
	}
	
	if(pIsMulti === "Y"){				
		var sTargetIDPfx = _mobile_webhard_list.chkPfx;
		var sObjectIDPfx = _mobile_webhard_list.liObjPfx;
		
		$("[id^='"+sTargetIDPfx+"']:checked").each(function(){
			var sObjID = $(this).attr("id").replace(sTargetIDPfx,sObjectIDPfx);
			var obj = $("#"+sObjID);
			var sUID = $(obj).attr("uid");
			var sFileType = $(obj).attr("filetype");
			
			if(sFileType === "folder"){
				folderUuids.push(sUID);
			}else{
				fileUuids.push(sUID);
			}
		});
	}else{
		if(!pUID){
			alert(mobile_comm_getDic('msg_SelectTarget'));
			return;
		}
		
		if(pType === "folder"){
			folderUuids.push(pUID);
		}else{
			fileUuids.push(pUID);
		}
	}
	
	if(folderUuids.length === 0 && fileUuids === 0){
		alert(mobile_comm_getDic('msg_SelectTarget'));
		return;
	}
		
	sources["fileUuids"] = fileUuids.join(";");
	sources["folderUuids"] = folderUuids.join(";");
	
	$.ajax({
		url: url,
		data: {
			"sources": JSON.stringify(sources),
			"targets": JSON.stringify(targets)
		},
		type: "post",
		success: function(res){	
			if(res.status === 'BOX_FULL'){
				alert(mobile_comm_getDic("msg_mobile_boxFull")); /* 웹하드 용량이 가득 찼습니다.<BR />필요 없는 파일을 완전 삭제하고 다시 시도해 주십시오.*/
	   		}else{
	   			alert(mobile_comm_getDic('msg_Common_36'));
	   		}
			mobile_webhard_ViewMoveAndCopy(pMode);
			mobile_webhard_clickrefresh();						
		},
		error: function(response, status, error){
			mobile_comm_ajaxerror(url, response, status, error);
		}
	});	
}

//삭제
function mobile_webhard_TargetDelete(pUID, pType, pIsMulti){
	var folderUuids = [];
	var fileUuids = [];
	
	if(pIsMulti === "Y"){				
		var sTargetIDPfx = _mobile_webhard_list.chkPfx;
		var sObjectIDPfx = _mobile_webhard_list.liObjPfx;
		
		$("[id^='"+sTargetIDPfx+"']:checked").each(function(){
			var sObjID = $(this).attr("id").replace(sTargetIDPfx, sObjectIDPfx);
			var obj = $("#"+sObjID);
			var sUID = $(obj).attr("uid");
			var sFileType = $(obj).attr("filetype");
			
			if(sFileType === "folder"){
				folderUuids.push(sUID);
			}else{
				fileUuids.push(sUID);
			}			
		});		
	}else{
		if(!pUID){
			alert(mobile_comm_getDic('msg_SelectTarget'));
			return;
		}
		
		if(pType === "folder"){
			folderUuids.push(pUID);
		}else{
			fileUuids.push(pUID);
		}
	}
	
	if(folderUuids.length == 0 && fileUuids.length == 0){
		alert(mobile_comm_getDic('msg_SelectTarget'));
		return;
	}
	
	if(pIsMulti === "Y"){
		var result = confirm(mobile_comm_getDic('msg_152'));
		
		if(!result) return;
	}		
	
	$.ajax({
		url: "/webhard/mobile/webhard/user/deleteTarget.do",
		data: {
			  folderUuids: folderUuids.join(";")
			, fileUuids: fileUuids.join(";")
			, folderType: (_mobile_webhard_list.FolderType === "Trashbin") ? "Trashbin" : "Normal"
			, boxUuid: _mobile_webhard_list.boxUuid
		},
		type: "post",
		success: function(res){					
			if(res.status === "SUCCESS"){
				if(pIsMulti === "Y")
					alert(mobile_comm_getDic('msg_Deleted'));
				mobile_webhard_clickrefresh();
			}
		},
		error: function(response, status, error){
			mobile_comm_ajaxerror("/webhard/mobile/webhard/user/deleteTarget.do", response, status, error);
		}
	});	
}

//전체 선택
function mobile_webhard_AllCheck(pMode){			
	var sTargetIDPfx = "";
	var sObjectIDPfx = "";
	var iCnt = 0;
	var iCheckCnt = 0;	
	var obj;
	sTargetIDPfx = _mobile_webhard_list.chkPfx;	
	obj = $("[id^='"+sTargetIDPfx+"']");
	iCnt = $(obj).length;
	iCheckCnt = $(obj).filter(":checked").length;	
	if(pMode == "init")
		$(obj).prop("checked",false).checkboxradio("refresh");
	else if(iCheckCnt == 0)
		$(obj).prop("checked",true).checkboxradio("refresh");
	else if(iCheckCnt != iCnt)
		$(obj).prop("checked",true).checkboxradio("refresh");
	else //else if(iCheckCnt == iCnt)
		$(obj).prop("checked",false).checkboxradio("refresh");	
	mobile_webhard_CheckCount();
}

//CheckboxOnchange 이벤트
function mobile_webhard_CheckboxOnchange(pObj){
	mobile_webhard_CheckCount();
}

//선택된 Checkbox count 표시
function mobile_webhard_CheckCount(pCnt){	
	var sTargetIDPfx = "";
	var iCheckCnt = 0;
	sTargetIDPfx = _mobile_webhard_list.chkPfx;
	if(pCnt != undefined)
		iCheckCnt = pCnt;
	else
		iCheckCnt = $("[id^='"+sTargetIDPfx+"']:checked").length;
	$("#webhard_list_chkcount").text(iCheckCnt);
}

//상단 Location 표시
function mobile_webhard_GetLocation(pData){

	// 공유받은/공유한 폴더가 아닌 경우. 
	if ( (pData.folderPath !== "Shared") && (pData.folderPath !== "Published") ) {
		
		var arrFolderNamePath = pData.folderNamePath.split("/");
		var arrFolderPath = pData.folderPath.split("/");
		var cnt = arrFolderNamePath.length;
		var sFolderNamePath = "";
		var sFolderPath = "";	
		var sHtml = "";
		_mobile_webhard_list.boxUuid = pData.boxUuid;
			
		if(cnt>1)
			sHtml += "<a href=\"javascript:void(0);\" class=\"webhard_loc_ico ui-link\" onclick=\"mobile_webhard_GoLocation('"+arrFolderPath[cnt-2]+"');\"></a>";
		else
			sHtml += "<a href=\"javascript:void(0);\" class=\"webhard_loc_ico ui-link\"></a>";
		
		sHtml += "<ul class=\"webhard_loc_list\">";			
		
		for(var i=0;i<cnt;i++){
			var sName = arrFolderNamePath[i];
			var sValue = arrFolderPath[i];
			
			if(sValue == "" || sValue == "ROOT"){
				sFolderNamePath = "/";
				sFolderPath = "/";
				sName = mobile_comm_getDic("lbl_myDrive");
				sValue = "ROOT";
			}else if(sFolderNamePath == "/"){
				sFolderNamePath +=sName;
				sFolderPath += sValue;
			}else{
				sFolderNamePath += "/"+sName;
				sFolderPath += "/"+sValue;
			}
			
			sHtml += "<li onclick=\"mobile_webhard_GoLocation('"+sValue+"');\" folderid=\""+sValue+"\"><p class=\"tx\">"+sName+"</p></li>";		
		}
			
		sHtml += "</ul>";  
		
	} else {
		// 공유 폴더일 경우.
		var sHtml = "";
		var sValue = "Shared";
		var sName = "";
		var tmpUid = $("#tmpUid").val();	// 현재 위치 폴더의 UUID.	
		if (pData.folderPath === "Shared") {
			sName = mobile_comm_getDic("lbl_sharedFolder");	// 공유받은 폴더.	
		} else if (pData.folderPath === "Published") {
			sName = mobile_comm_getDic("lbl_shareFolder");	// 공유한 폴더.
		}
		
		if ( (typeof(tmpUid) === "undefined") || (tmpUid === "") || (tmpUid === "ROOT") ) {
			$("#tmpFldrNm").val(sName);
			sHtml += "<a href=\"javascript:void(0);\" class=\"webhard_loc_ico ui-link\"></a>";
		} else {
			sHtml += "<a href=\"javascript:void(0);\" class=\"webhard_loc_ico ui-link\" onclick=\"mobile_webhard_prevFolder('"+ tmpUid +"');\"></a>";
		}
		sHtml += "<ul class=\"webhard_loc_list\">";
		sHtml += "<li onclick=\"mobile_webhard_prevFolder('"+tmpUid+"');\" folderid=\""+sValue+"\"><p class=\"tx\">"+ $("#tmpFldrNm").val() +"</p></li>";			
		sHtml += "</ul>";	
		
	}
	
	$("#webhard_list_loc").html(sHtml);	
}

//Location Click
function mobile_webhard_GoLocation(pFolderid){
	if(_mobile_webhard_list.opentype != "" || (!$("#webhard_list_header").hasClass("edit") && _mobile_webhard_list.FolderType == "Normal"))
		mobile_webhard_ChangeFolder(pFolderid);
	
	// 공유폴더일 경우
	if (_mobile_webhard_list.FolderType === "Shared") {
		mobile_webhard_ChangeFolder(pFolderid, "Shared");
	} else if (_mobile_webhard_list.FolderType === "Published") {
		mobile_webhard_ChangeFolder(pFolderid, "Published");
	}
}

//파일 업로드
function mobile_webhard_UploadFile(fileBuffer) {
	if (fileBuffer.length > 0) {		
		mobile_comm_showload();		
		var uploadFile = fileBuffer; // 넘겨받은 파일 정보, [object FileList]
		fileBuffer = null;
		var url = "/webhard/mobile/webhard/user/uploadFile.do";
		
		var formData = new FormData();		
		formData.append("boxUuid", _mobile_webhard_list.boxUuid); // 필수값 (폴더가 없는 경우 반드시 필요)
		formData.append("folderUuid", _mobile_webhard_list.FolderID); // 업로드 대상 폴더가 있는 경우 필수값

		var bufferLength = uploadFile.length;
		
		if (bufferLength > 0) {
			for (var i = 0; i < bufferLength; i++) {
				formData.append("files", uploadFile[i]);
			}
		}		
		
		$.ajax({
			type : "post",
			url : url,
			data : formData,
			dataType : "json",
			processData : false,
			contentType : false,
			success : function(res) {				
				mobile_comm_hideload();								
				if (res.status === 'SUCCESS') {
					alert(mobile_comm_getDic("msg_UploadOk"));				
					mobile_webhard_clickrefresh();
				} else if(res.status === 'BOX_FULL'){
					alert(mobile_comm_getDic("msg_mobile_boxFull")); // 웹하드 용량이 가득 찼습니다.<BR />필요 없는 파일을 완전 삭제하고 다시 시도해 주십시오.
		   		} else if (res.status === "UPLOAD_MAX") {
		   			alert(mobile_comm_getDic("msg_fileUploadMax"));// 업로드 하려는 파일이 최대 업로드 크기보다 큽니다.<br/>파일의 크기를 줄인 후 다시 시도해 주십시오.
		   		} else {
					alert(mobile_comm_getDic("msg_ErrorProcessRequest"));
				}
			},
			error : function(response, status, error) {
				mobile_comm_ajaxerror(url, response, status, error);
			}
		});
	} else {
		alert(mobile_comm_getDic("msg_066"));		
	}
}

//선택파일 업로드
function mobile_webhard_filetransfer() {
	var url = "/webhard/mobile/webhard/attach/uploadToFront.do";
	var fileArray = [];				
	var sTargetIDPfx = _mobile_webhard_list.chkPfx;
	var sObjectIDPfx = _mobile_webhard_list.liObjPfx;			
	
	$("[id^='"+sTargetIDPfx+"']:checked").each(function(){
		var sObjID = $(this).attr("id").replace(sTargetIDPfx,sObjectIDPfx);
		var obj = $("#"+sObjID);
		var sUID = $(obj).attr("uid");
		var sFileType = $(obj).attr("filetype");
		if(sFileType != "folder"){
			fileArray.push(sUID);
		}		
	});
	
	if(fileArray.length > 0){
		$.ajax({
			url: url, 
			data: JSON.stringify(fileArray),
			type:"POST",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(result){
				if(result.status == "SUCCESS"){
					window.sessionStorage["webhardfileinfo"] = JSON.stringify(result.files);
					var fnCallback = new Function("return " + window.sessionStorage["callback"] + "()");
					fnCallback();
					window.sessionStorage["webhardopentype"] = "";
					window.sessionStorage["callback"] = "";
					$("#webhard_list_page").find("ul").html('');
					$("#webhard_list_page").hide();					
					$('#webhard_list_page').dialog('close');										
				} else {
					alert("Upload Error!!!"); //오류가 발생했습니다.
				}
			},
			error : function (response, status, error) {
				mobile_comm_ajaxerror(url, response, status, error);
			}			
		});
	}else{
		alert(mobile_comm_getDic('msg_SelectTarget')) 	// 대상을 선택해주세요.
	}
}

//웹 하드 용량
function mobile_webhard_getUsage(){
	$.ajax({
		url: "/webhard/mobile/webhard/user/getUsageWebHard.do",
		method: "POST",
		async: false,
		success: function(res){
			var maxSize = mobile_webhard_convertFileSize(res.BOX_MAX_SIZE * 1024  * 1024);
			var currentSize = res.CURRENT_SIZE_BYTE ? mobile_webhard_convertFileSize(res.CURRENT_SIZE_BYTE) : "0.0GB";
			
			$("#webhard_currentWhSize").empty();
            $("#webhard_currentWhSize").append(
            	currentSize + "/",
				$("<span/>", {"class": "m_capacity_gray", "text": maxSize})
			);
            
            $("#webhard_currentWhRate").css({"width": res.RATE + "%", "background-color": res.RATE >= 80 ? "#e12f2f" : "#49bedf"});
		},
		error: function(response, status, error){
			mobile_comm_ajaxerror("/webhard/mobile/webhard/user/getUsageWebHard.do", response, status, error);
		}
	});
}

function mobile_webhard_convertFileSize(pSize){
	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (pSize == 0) return '0';
    var i = parseInt(Math.floor(Math.log(pSize) / Math.log(1024)));
    return (pSize / Math.pow(1024, i)).toFixed(1) + sizes[i];
}