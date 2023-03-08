<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil,egovframework.baseframework.util.RedisDataUtil"%>

<!doctype html>
<html lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=1280">
	<title><%=PropertiesUtil.getGlobalProperties().getProperty("front.title") %></title>
	<tiles:insertAttribute name="include" />
</head>
<!-- groupware/user_default  -->
<body>	
	<div id="wrap" class="ui_app">
		<div id="header" class="ui_global">
			<tiles:insertAttribute name="header" />
		</div>
		<div id="comm_container" class="ui_root">
			<div class="ui_dock">
	            <div class="dock_root">
	                <div class="nav_list">
	                    <nav class="mCustomScrollbar _mCS_1 mCS_no_scrollbar"><div id="mCSB_1" class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside" style="max-height: none;" tabindex="0"><div id="mCSB_1_container" class="mCSB_container mCS_y_hidden mCS_no_scrollbar_y" style="position:relative; top:0; left:0;" dir="ltr">
	                        <a href="#" class="link mail" data-tooltip-title="메일" data-tooltip-position="right" data-tooltip-space="8"><span>메일</span><em class="ui_badge">7</em></a>
	                        <a href="#" class="link approval" data-tooltip-title="결재" data-tooltip-position="right" data-tooltip-space="8"><span>결재</span></a>
	                        <a href="#" class="link schedule" data-tooltip-title="일정" data-tooltip-position="right" data-tooltip-space="8"><span>일정</span></a>
	                        <a href="#" class="link survey" data-tooltip-title="설문" data-tooltip-position="right" data-tooltip-space="8"><span>설문</span></a>
	                        <a href="#" class="link notice" data-tooltip-title="게시" data-tooltip-position="right" data-tooltip-space="8"><span>게시</span></a>
	                        <a href="#" class="link request" data-tooltip-title="요청업무" data-tooltip-position="right" data-tooltip-space="8"><span>요청업무</span></a>
	                        <a href="#" class="link community" data-tooltip-title="커뮤니티" data-tooltip-position="right" data-tooltip-space="8"><span>커뮤니티</span></a>
	                        <button type="button" class="link mention" data-tooltip-title="멘션" data-tooltip-position="right" data-tooltip-space="8"><span>멘션</span></button>
	                        <a href="#" class="link eum" data-tooltip-title="이음톡" data-tooltip-position="right" data-tooltip-space="8"><span>이음톡</span></a>
	                        <a href="#" class="link corporation" data-tooltip-title="협업스페이스" data-tooltip-position="right" data-tooltip-space="8"><span>협업스페이스</span></a>
	                    </div><div id="mCSB_1_scrollbar_vertical" class="mCSB_scrollTools mCSB_1_scrollbar mCS-light mCSB_scrollTools_vertical" style="display: none;"><div class="mCSB_draggerContainer"><div id="mCSB_1_dragger_vertical" class="mCSB_dragger" style="position: absolute; min-height: 30px; height: 0px; top: 0px;"><div class="mCSB_dragger_bar" style="line-height: 30px;"></div></div><div class="mCSB_draggerRail"></div></div></div></div></nav>
	                </div>
	                <button type="button" class="nav_setting_toggle ui_button icon" id="dock_setting_toggle"><span>편집</span></button>
	                <button type="button" class="dock_open"><span>펼침</span></button>
	            </div>
	        </div>
        	<div class="ui_container">
        		<div class="ui_page_links" aria-expanded="true">
	                <div class="link_list">
	                    <div class="ui_page_link" data-code="code1" aria-selected="true">
	                        <a href="#">주간보고</a>
	                        <button type="button" data-icon="pin" aria-checked="true"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code2" style="display: flex;">
	                        <a href="#">뉴스레터</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code3" style="display: flex;">
	                        <a href="#">나의 게시</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code4" style="display: flex;">
	                        <a href="#">승인요청</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code5" style="display: flex;">
	                        <a href="#">경비결재</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code6" style="display: flex;">
	                        <a href="#">사업관리</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code7" style="display: flex;">
	                        <a href="#">진행중인 설문</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code8" style="display: flex;">
	                        <a href="#">휴가현황</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code9" style="display: flex;">
	                        <a href="#">연락처</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code10" style="display: flex;">
	                        <a href="#">뉴스레터</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code11" style="display: flex;">
	                        <a href="#">내 근태현황</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                    <div class="ui_page_link" data-code="code12" style="display: flex;">
	                        <a href="#">전체 글 보기</a>
	                        <button type="button" data-icon="pin"><span>고정</span></button>
	                        <button type="button" data-icon="close"><span>닫기</span></button>
	                    </div>
	                </div>
	                <div class="link_more">
	                    <button type="button" class="ui_button icon basic" data-icon="more" id="page_link_more_toggle"><span>더보기</span>
	                    </button>
	                    <div class="ui_more_contextmenu">
	                        <button type="button" class="ui_button" data-icon="other" id="delete_other_page_links">
	                            <span>다른 링크 삭제</span>
	                        </button>
	                        <button type="button" class="ui_button" data-icon="all" id="delete_all_page_links">
	                            <span>모든 링크 삭제</span>
	                        </button>
	                        <div class="auto"></div>
	                    </div>
	                </div>
	                <button type="button" class="page_action" data-icon="toggle" id="page_links_toggle"><span>접기/펼치기</span>
	                </button>
	            </div>
        		<div class='ui_content'>
        			<div id="left" class="ui_snb">
						<div class="snb_root">
						<tiles:insertAttribute name="left" />
						</div> 
						<div class="snb_toggle">
	                        <button type="button" aria-expanded="true"><span>접기/펼치기</span></button>
	                    </div>
					</div>
					<div class='ui_main'  id="content">
						<tiles:insertAttribute name="content" />
					</div>
        		</div>
        	</div>
	</div>
</body>
</html>