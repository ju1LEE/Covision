<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- TODO : 다국어 처리 -->
<div data-role="page" id="webhard_list_recentdoc_page">

	<script>
		var WebhardMenu = ${Menu};	//좌측 메뉴
	</script>

	<header data-role="header" id="webhard_list_recentdoc_header" class="header">
      <div class="sub_header">
        <div class="l_header">
          <a href="javascript: mobile_comm_back();" class="btn_back"><span>이전페이지로 이동</span></a>
          <div class="menu_link gnb">
            <a id="webhard_list_recentdoc_title" href="#" onclick="javascript: mobile_comm_TopMenuClick('webhard_list_recentdoc_title'); return false;" class="pg_tit"><span class="Tit_ellip">최근문서함</span><i class="arr_menu"></i></a>
            <ul id="webhard_list_topmenu" class="h_tree_menu_wrap"></ul>
          </div>
        </div>
        <div class="utill">
			<a href="javascript:mobile_webhard_ChangeMultiMode();" class="btn_mailcheck"><span>편집</span></a>
			<a href="javascript: mobile_webhard_ChangeView('List');" class="btn_thumbviwe"><span>목록보기</span></a>
			<a href="javascript: mobile_comm_opensearch();" class="btn_search"><span>검색</span></a>
        </div>
      </div>
      <div class="ly_search">
      	<a href="javascript: mobile_comm_closesearch(); mobile_webhard_closesearch();" class="btn_back"><span><spring:message code='Cache.btn_Close' /></span></a> <!-- 닫기 -->
        <input type="text" id="mobile_search_input" name="" value="" placeholder="제목, 등록자, 설문지내용 검색">
        <a href="javascript: mobile_comm_cleansearch();" class="del"></a>
      </div>
    </header>
    
    <div data-role="content" class="cont_wrap" id="webhard_list_content">
      <div class="webhard_cont">
		<!-- 리스트 시작 -->
		<div id="webhard_list_recentdoc_wrap" class="webhard_list_wrap">
			<div class="WlistBox">
				<div class="area_category">오늘</div>
				<ul class="uio_list Rdoc">
					<li class="utl_item" style="display:none;">
						<a href="#" class="utl_a">
							<span class="lzImg List"><img src="/HtmlSite/smarts4j_n/mobile/resources/images/folder.png" class="lzImgF"><img src="/HtmlSite/smarts4j_n/mobile/resources/images/ico_share.png" class="lzImgS"></span>
						</a>
						<a href="#" class="utl_b"><span class="utl_d">디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안</span></a>
						<a href="#" class="bookmark ui-link"></a>
					</li>
				</ul>
				<div class="area_category">이전</div>
				<ul class="uio_list Rdoc">
					<li class="utl_item"  style="display:none;">
						<a href="#" class="utl_a">
							<span class="lzImg List"><img src="/HtmlSite/smarts4j_n/mobile/resources/images/pdf.png" class="lzImgF"><img src="/HtmlSite/smarts4j_n/mobile/resources/images/ico_share.png" class="lzImgS"></span>
						</a>
						<a href="#" class="utl_b"><span class="utl_d">디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안디자인시안</span></a>
						<a href="#" class="bookmark ui-link"></a>
					</li>
				</ul>
			</div>
		</div>
		<!-- 리스트 끝 -->
    </div>
	<!-- 비서 시작 -->
    <div class="btn_private_secretary" style="display:none;">
      <a href="#"><span>개인비서</span></a>
      <span class="ico_new">N</span>
    </div>
	<!-- 비서 끝 -->
	<!-- 파일업로드 시작 -->
	<div class="WuploadBtn">
      <a href="#" class="ui-link"><span>작성</span></a>
    </div>
	<!-- 파일업로드 끝 -->

	</div>
</div>