<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="cLnbTop">
	<h2 onclick="goMain();" style="cursor: pointer;"><spring:message code='Cache.lbl_Community'/></h2>
	<div><a id="btnWrite" href="#" onclick="goAddCommunity();" class="btnType01"><spring:message code='Cache.lbl_MakeCommunity'/></a></div>
	<div class="selectBox lnb">
		<select class="selectType02" id="selectJoinCommunity"></select>
	</div>
</div>
<div class="cLnbMiddle mScrollV scrollVType01 communityLnbContent mCustomScrollbar _mCS_2 mCS_no_scrollbar">
	<div id="mCSB_2" class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside" style="max-height: none;" tabindex="0">
		<div id="mCSB_2_container" class="mCSB_container mCS_y_hidden mCS_no_scrollbar_y" style="position:relative; top:0; left:0;" dir="ltr">
			<div>
				<ul class="contLnbMenu communityMenu">
					<li class="communityMenu01">
						<button class="btnLnbOption"><spring:message code='Cache.lbl_Option'/></button>
						<div class="selOnOffBox ">
							<a href="#" class="btnOnOff active" style="max-width: 80%;"><spring:message code='Cache.lbl_FavoriteCommunity'/><span></span></a>
						</div>
						<div class="selOnOffBoxChk type02 boxList active" id="boxList">
						</div>
					</li>
					<li class="communityMenu02">
						<div class="treeList" id='coviTree_FolderMenu' style='height:210px;'></div>
					</li>
				</ul>
				<ul id="leftMenu" class="contLnbMenu communityMenu"></ul>
			</div>
		</div>
		<div id="mCSB_2_scrollbar_vertical" class="mCSB_scrollTools mCSB_2_scrollbar mCS-light mCSB_scrollTools_vertical" style="display: none;">
			<a href="#" class="mCSB_buttonUp" oncontextmenu="return false;" style="display: block;"></a>
			<div class="mCSB_draggerContainer">
				<div id="mCSB_2_dragger_vertical" class="mCSB_dragger" style="position: absolute; min-height: 30px; height: 0px; top: 0px; display: block; max-height: 271px;" oncontextmenu="return false;">
					<div class="mCSB_dragger_bar" style="line-height: 30px;"></div>
				</div>
				<div class="mCSB_draggerRail"></div>
			</div>
			<a href="#" class="mCSB_buttonDown" oncontextmenu="return false;" style="display: block;"></a>
		</div>
	</div>
</div>

<script type="text/javascript">
	var myFolderTree = new coviTree();
	var leftData = ${leftMenuData};
	var loadContent = '${loadContent}';
	var domainId = '${domainId}';
	var isAdmin = '${isAdmin}';
	var g_bizSection = '<%=request.getParameter("CLBIZ")%>';
	var g_lastURL;
	
	var boardLeft;
	
	var body = {
		onclick:function(idx, item){ //[Function] 바디 클릭 이벤트 콜백함수
			if(item.FolderType == "Root" && item.FolderPath == ""){
				selectCommunityTreeListByTree(item.FolderID, item.FolderType, "0", item.FolderName);
			}else{
				selectCommunityTreeListByTree(item.FolderID, item.FolderType, item.FolderPath, item.FolderName);
			}
		}
	};
	
	initLeft();
	
	function initLeft(){
		
		setFavorites();
		setTreeData();
		setEvent();
		
		var opt = {
			lang : "ko",
			isPartial : "true"
		};
		
		var coviMenu = new CoviMenu(opt);
		
		if(leftData.length != 0){
			coviMenu.render('#leftMenu', leftData, 'userLeft');
		}
		
		if(loadContent == 'true'){
			CoviMenu_GetContent('/groupware/layout/community_CommunityList.do?CLSYS=community&CLMD=user&CLBIZ=' + g_bizSection);
			g_lastURL = '/groupware/layout/community_CommunityList.do?CLSYS=community&CLMD=user&CLBIZ=' + g_bizSection;
		}
		
		$('.btnLnbOption').on('click', function(){
			if($(this).hasClass('active')){
				$(this).removeClass('active');
				$(this).closest('li').removeClass('active');
			}else {
				$(this).addClass('active');
				$(this).closest('li').addClass('active');
			}
		});
		
	}
	
	function goMain(){
		CoviMenu_GetContent('/groupware/layout/community_CommunityList.do?CLSYS=community&CLMD=user&CLBIZ=' + g_bizSection);
		g_lastURL = '/groupware/layout/community_CommunityList.do?CLSYS=community&CLMD=user&CLBIZ=' + g_bizSection;
	}
	
	function setFavorites(){
		var recResourceHTML = "";
		$.ajax({
			url:"/groupware/layout/communityFavoritesSetting.do",
			type:"POST",
			async:false,
			data:{
				
			},
			success:function (cfs) {
				$("#boxList").html("");
				if(cfs.list.length > 0){
					$(cfs.list).each(function(i,v){
						recResourceHTML += "<a href='#'><span onclick='goUserCommunity("+v.CU_ID+")'>"+v.CommunityName+"</span><button class='btnLnbFavoriteRemove' onclick='removeFavorite("+v.CU_ID+")'></button></a>";
					});
				}
				
				$("#boxList").html(recResourceHTML);
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/groupware/layout/communityFavoritesSetting.do", response, status, error); 
			}
		});
	}
	
	function removeFavorite(communityID){
		$.ajax({
			url:"/groupware/layout/communityFavoritesDelete.do",
			type:"POST",
			async:false,
			data:{
				CU_ID : communityID
			},
			success:function (datac) {
				if(datac.status == "SUCCESS"){
					
				}else{
					alert("<spring:message code='Cache.msg_FailProcess' />");
				}
				setFavorites();
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/groupware/layout/communityFavoritesDelete.do", response, status, error); 
			}
		});
	}
	
	function setTreeData(){
		$.ajax({
			url:"/groupware/layout/selectCommunityTreeData.do",
			type:"POST",
			data:{},
			async:false,
			success:function (tdata) {
				var List = tdata.list;
				
				myFolderTree.setTreeList("coviTree_FolderMenu", List, "nodeName", "170", "left", false, false, body);
			},
			error:function (error){
				CFN_ErrorAjax("/groupware/layout/selectCommunityTreeData.do", response, status, error);
			}
		});
		myFolderTree.displayIcon(true);
		myFolderTree.clearFocus();
	}
	
	function setEvent(){
		$("#selectJoinCommunity").coviCtrl("setSelectOption",
			"/groupware/layout/selectUserJoinCommunity.do",
			{},
			Common.getDic("lbl_MyCommunity"), // 내가 가입한 커뮤니티
			""
		);
		
		$("#selectJoinCommunity").change(function(){
			if($("#selectJoinCommunity").val() != "" ){
				var specs = "left=10,top=10,width=1050,height=900";
				specs += ",toolbar=no,menubar=no,status=no,scrollbars=no,resizable=no";
				window.open("/groupware/layout/userCommunity/communityMain.do?C="+$("#selectJoinCommunity").val(), "community", specs);
				$("#selectJoinCommunity").val("");	//커뮤니티 팝업 호출 이후 초기 값으로 재설정
			}
		});
	}
	
	function selectCommunityTreeListByTree(pFolderID, pFolderType, pFolderPath, pFolderName){
		var path = "";
		var folder = "";
		
		folder = pFolderID;
		path = pFolderPath;
		
		CoviMenu_GetContent('/groupware/layout/community_CommunityUserList.do?CLSYS=community&CLMD=user&CLBIZ=Community&folder='+folder);
		g_lastURL = '/groupware/layout/community_CommunityUserList.do?CLSYS=community&CLMD=user&CLBIZ=Community&folder='+folder
	}
	
	function goAddCommunity(){
		CoviMenu_GetContent('/groupware/layout/community_CommunityCreate.do?CLSYS=community&CLMD=user&CLBIZ=Community');
	}
	
	function goUserCommunity(cID){
		var specs = "left=10,top=10,width=1050,height=900";
		specs += ",toolbar=no,menubar=no,status=no,scrollbars=no,resizable=no";
		window.open("/groupware/layout/userCommunity/communityMain.do?C="+cID, "community", specs);
	}
</script>
