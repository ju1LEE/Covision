<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!-- TODO : 다국어 처리 -->
<div data-role="page" id="webhard_list_page" data-close-btn="none" class="mob_rw">

	<script>
		var WebhardMenu = ${Menu};	//좌측 메뉴
	</script>

	<header data-role="header" id="webhard_list_header" class="header">
		<div id="webhard_list_subheader" class="sub_header">
			<div class="l_header">
				<a href="javascript:mobile_comm_back();" id="webhard_list_back" class="topH_back" style="display:none;"><span>이전페이지로 이동</span></a>
				<a href="javascript:mobile_comm_TopMenuClick('webhard_list_topmenu');" class="topH_menu"><span>전체메뉴</span></a>
				<div class="menu_link gnb">
					<span id="webhard_list_title" href="javascript:void(0);" class="topH_tit"></span>
					<div class="cover_leftmenu" style="display:none;">
						<div class="LsubHeader">
							<span class="LsubTitle webhard"><spring:message code='Cache.lbl_webhard' /></span>
							<span class="LsubTitle_btn">
								<button onclick="mobile_comm_TopMenuClick('webhard_list_topmenu',true);return false;" type="button" class="btn_close"><i>닫기</i></button>
							</span>
						</div>
						<div class="tree_default" id="webhard_list_topmenu">
							<ul class="h_tree_menu_wrap"></ul>
					 	</div> 
					 	<div class="m_capacity_wrap">
							<p class="m_capacity_tit" id="webhard_currentWhSize">0.0GB/<span class="m_capacity_gray">10GB</span></p>
							<div class="m_capacity_box"><div class="m_capacity_bar" id="webhard_currentWhRate" style="width:0%; background-color:#49bedf;"></div></div>
							<!--  80% 미만일 때: #49bedf / 80% 이상일 때: #e12f2f -->
					  	</div>
					</div>
					<div class="left_bg_dim" onclick="mobile_comm_TopMenuClick('webhard_list_topmenu',true);return false;" style="display: none;"></div>
				</div>
			</div>			
			<div id="webhard_list_util" class="utill">				
				<a href="javascript: mobile_webhard_ChangeView('List');" id="btnListView" class="btn_listviwe ui-link" style="display:none;"><span><spring:message code='Cache.lbl_List_View'/></span></a> <!-- 목록보기 -->
				<a href="javascript: mobile_webhard_ChangeView('Album');" id="btnthumview" class="btn_thumbviwe ui-link" style="display: none;"><span><spring:message code='Cache.lbl_apv_view_thumbnail'/></span></a> <!-- 썸네일 보기 -->
				<a href="javascript: mobile_comm_opensearch();" class="topH_search"><span class="Hicon">검색</span></a>
				<div class="dropMenu" onclick="mobile_webhard_ToggleShowClass(this);">
					<a href="#" class="topH_exmenu ui-link"><span class="Hicon">확장메뉴</span></a>
					<div class="exmenu_layer">
						<ul class="exmenu_list">
							<li><a href="javascript:mobile_webhard_ViewAddFolder('Add');" class="btn ui-link"><spring:message code='Cache.lbl_AddFolder'/></a></li> <!-- 폴더추가 -->
							<li><a href="javascript:mobile_webhard_ChangeMultiMode();" class="btn ui-link"><spring:message code='Cache.lbl_EditList'/></a></li> <!-- 목록편집 -->
						</ul>
					</div>
				</div>
	        </div>
			<div id="webhard_list_popuputil" class="utill" style="display:none;">	
				<a href="javascript:mobile_webhard_filetransfer();" class="topH_save"><span class="Hicon"><spring:message code='Cache.lbl_Upload'/></span></a> <!-- 업로드 -->			
	        </div>			
		</div>
		
		<div id="webhard_list_editsubheader" class="sub_header" style="display:none;">
	        <div class="l_header">
	          <a id="webhard_list_subbackbtn" href="javascript: mobile_webhard_ChangeMultiMode(); mobile_comm_closesearch();" class="btn_back ui-link"><span>이전페이지로 이동</span></a>
	          <div class="menu_link gnb">
	            <a href="#" id="webhard_list_chkcount" class="pg_tit ui-link">0</a>
	          </div>
	        </div>
	        <div id="webhard_list_editutil" class="utill">
				<a href="javascript:mobile_webhard_AllCheck();" class="topH_allcheck ui-link"><span class="Hicon"><spring:message code='Cache.btn_All' /></span></a>							
				<a href="javascript:mobile_webhard_TargetDelete('','','Y');" class="topH_delete ui-link"><span class="Hicon"><spring:message code='Cache.btn_delete' /></span></a>
				<div class="dropMenu" onclick="mobile_webhard_ToggleShowClass(this);">
					<a href="#" class="topH_exmenu ui-link"><span class="Hicon">확장메뉴</span></a>
					<div class="exmenu_layer">
						<ul class="exmenu_list">
							<li><a href="javascript:mobile_webhard_ViewMoveAndCopy('M');" id="btnMove" class="btn ui-link"><spring:message code='Cache.btn_Move' /></a></li>
							<li><a href="javascript:mobile_webhard_ViewMoveAndCopy('C');" id="btnCopy" class="btn ui-link"><spring:message code='Cache.btn_Copy' /></a></li>
							<li><a href="javascript:mobile_webhard_Restore();" id="btnRestore" class="btn ui-link"><spring:message code='Cache.lbl_Restore'/></a></li> <!-- 복원 -->
						</ul>
					</div>
				</div>
	        </div>
		</div>
				
		<div class="ly_search ly_change">
			<a href="javascript: mobile_comm_closesearch(); mobile_webhard_closesearch();" class="topH_back"><span><spring:message code='Cache.btn_Close' /></span></a> <!-- 닫기 -->
	        <input type="text" id="mobile_search_input" name="" value="" voiceInputType="callback" voiceStyle="margin-right:25px;" voiceCallBack="mobile_webhard_clicksearch"  class="mobileViceInputCtrl" onkeypress="if (event.keyCode==13){ mobile_webhard_clicksearch(); return false;}" onkeyup="mobile_comm_searchinputui('mobile_search_input');" placeholder="<spring:message code='Cache.msg_FileResearch' />">
			<a id="mobile_search_input_btn" href="javascript: mobile_webhard_clicksearch()" class="topH_search" style="display: none;"></a>
			<a href="javascript: mobile_comm_cleansearch(); mobile_comm_searchinputui('mobile_search_input');" class="topH_del"></a>		  
		</div>
	</header>
	
	<div data-role="content" class="cont_wrap" id="webhard_list_content">
		<div class="webhard_cont">
        <div id="webhard_list_loc" class="webhard_loc"></div>
		<!-- 폴더추가 시작 -->
		<div id="webhard_list_add" class="title set2 webhardadd" style="display:none;">
			<div class="ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset"><input id="txtWebhardFolderName" type="text" class="full" placeholder="<spring:message code='Cache.msg_295' />"></div>
			<a id="btnFolderAdd" href="javascript:mobile_webhard_addFolder('Add');" class="btn" style="right: 40px;">추가</a>
			<a id="btnFolderEdit" href="javascript:mobile_webhard_addFolder('Edit');" class="btn" style="right: 40px;display:none;">수정</a>
			<a href="javascript:mobile_webhard_ViewAddFolder('');" class="timeline_wr_del" style="height:25px;margin-top:1px;"></a>
			<input id="hidUID" type="hidden"/>
        </div>
		<!-- 폴더추가 끝 -->
		<!-- 이동/복사 시작-->
		<%--
		<div id="webhard_list_moveandcopy" style="text-align: right; border-bottom: 1px solid grey;display:none;">
			<div class="ui-select">
				<div class="ui-btn ui-icon-carat-d ui-btn-icon-right ui-corner-all ui-shadow">
					<span id="webhard_list_seltype" class="full sel_type">&nbsp;</span>
						<select id="webhard_list_ddlseltype" class="full sel_type"></select>
				</div>
				</div>
			<a href="javascript:mobile_webhard_MoveandCopy('','','M','Y');" id="webhard_list_move" class="g_btn01 ui-link" style="margin-top:10px; margin-right:10px; margin-bottom:10px;"><spring:message code='Cache.btn_Move' /></a>
			<a href="javascript:mobile_webhard_MoveandCopy('','','C','Y');" id="webhard_list_copy" class="g_btn01 ui-link" style="margin-top:10px; margin-right:10px; margin-bottom:10px;display:none;"><spring:message code='Cache.btn_Copy' /></a>
		</div>
		 --%>
		<div id="webhard_list_moveandcopy" class="webhard_Smove" style="display:none;">
			<div class="ui-select">
				<div class="ui-btn ui-icon-carat-d ui-btn-icon-right ui-corner-all ui-shadow">
					<span id="webhard_list_seltype" class="full sel_type">&nbsp;</span>
					<select id="webhard_list_ddlseltype" class="full sel_type"></select>
				</div>
			</div>
			<a href="javascript:mobile_webhard_MoveandCopy('','','M','Y');" id="webhard_list_move" class="g_btn01 ui-link" style=""><spring:message code='Cache.btn_Move' /></a>
			<a href="javascript:mobile_webhard_MoveandCopy('','','C','Y');" id="webhard_list_copy" class="g_btn01 ui-link" style="display:none;"><spring:message code='Cache.btn_Copy' /></a>
		</div>
		<!-- 이동/복사 시작-->
		<!-- 썸네일 시작 -->
		<div class="webhard_thumbnail_wrap" id="webhard_thumbnail_wrap" style="display:none;">
			<div id="webhard_thumbnail_box" class="thumbnailBox"></div>
		</div>
		<!-- 썸네일 끝 -->
		<!-- 리스트 시작 -->
		<div class="webhard_list_wrap" id="webhard_list_wrap">
			<div id="webhard_list_box" class="WlistBox" style="overflow:hidden;"></div>
		</div>
		<!-- 리스트 끝 -->
		<!-- 최근 리스트 시작 -->
		<div class="webhard_list_wrap" id="webhard_time_wrap" style="display:none;">
			<div id="webhard_time_box" class="WlistBox" style="overflow:hidden;"></div>
		</div>
		<!-- 최근 리스트 끝 -->
		<div id="webhard_list_more" class="btn_list_more" style="display: none;">
			<a href="javascript: mobile_webhard_nextlist();"><span><spring:message code='Cache.lbl_MoreView' /></span></a> <!-- 더보기 -->
		</div>
		
      </div>
	</div>
	<!-- 팝업 시작 -->
	<div id="webhard_list_popup" class="mobile_popup_wrap" style="display:none;" onclick="$('#webhard_list_popup').toggle();return false;"></div>
	<!-- 팝업 끝 -->
	<!-- 비서 시작 -->
    <div class="btn_private_secretary" style="display:none;">
      <a href="#"><span>개인비서</span></a>
      <span class="ico_new">N</span>
    </div>
   	<!-- 파일업로드 시작 -->
	<div class="WuploadBtn">
		<a href="#" onclick="javascript: $('#btnSelectFile').click();"
			class="ui-link"></a>
		<input id="btnSelectFile" type="hidden" value="파일선택" />
	</div>
	<div id="uploadDiv">
		<input id="uploadFiles" type="file" multiple="multiple"
			style="display: none;">
	</div>	
	<!-- 파일업로드 끝 -->
	<div class="bg_dim" style="display: none;"></div>


	<input type="hidden" id="tmpUid" />
	<input type="hidden" id="tmpFldrNm" />

</div>