<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div data-role="page" id="schedule_day_page" class="mob_rw">

	<header data-role="header" id="schedule_day_header">
		<div class="sub_header">
	        <div class="l_header">
	          <a href="javascript:mobile_comm_TopMenuClick('mobile_schedule_topmenu');" class="topH_menu"><span>전체메뉴</span></a>
	          <div class="menu_link gnb">
	          	<span id="schedule_day_title" href="javascript:void(0);" class="topH_tit"><spring:message code='Cache.lbl_DailyShow' /></span>
				<div class="cover_leftmenu" style="display:none;">
					<div class="LsubHeader">
						<span class="LsubTitle schedule"><spring:message code='Cache.BizSection_Schedule' /></span>
						<span class="LsubTitle_btn">
							<button type="button" onclick="mobile_schedule_realoadPage();mobile_comm_TopMenuClick('mobile_schedule_topmenu',true);return false;" class="btn_close ui-btn ui-shadow ui-corner-all"><i>닫기</i></button>
						</span>
					</div>
					<div class="sorting">				             
						<ul>
							<li class="selected" name="schedulemenu"><a class="daily" href="javascript: mobile_schedule_changeCalendar('D');"><spring:message code='Cache.lbl_Daily' /></a></li> <!-- 일간보기 -->
							<li name="schedulemenu"><a class="weekly" href="javascript: mobile_schedule_changeCalendar('W');"><spring:message code='Cache.lbl_Weekly' /></a></li> <!-- 주간보기 -->						
							<li name="schedulemenu"><a class="monthly" href="javascript: mobile_schedule_changeCalendar('M');"><spring:message code='Cache.lbl_Monthly' /></a></li> <!-- 월간보기 -->
							<li name="schedulemenu"><a class="list" href="javascript: mobile_schedule_changeCalendar('L');"><spring:message code='Cache.lbl_Index' /></a></li> <!-- 목록보기 -->
						</ul>
					</div>
					<div class="tree_default" id="mobile_schedule_topmenu">
						<ul class="h_tree_menu_wrap">
							<li class="h_tree_sch_select">
								<div class="sch_select">								
									<ul id="mobile_schedule_treeList" class="select_list">								
										<li>
											<ul id="mobile_schedule_checklisttotal">
												<li class="all_chk chk_item">
													<a href="#"><span class="rd_chk"></span><label><spring:message code='Cache.lbl_selectall' /></label></a> <!-- 전체선택 -->
												</li>
											</ul>
										</li>
										<li>
											<ul id="mobile_schedule_checklistcommunity"></ul>
										</li>
										<li>
											<ul id="mobile_schedule_checklisttheme"></ul>
										</li>
									</ul>					
								</div>
							</li>						
						</ul>					
					</div>
				</div>
				<div class="left_bg_dim" onclick="mobile_schedule_realoadPage();mobile_comm_TopMenuClick('mobile_schedule_topmenu',true);return false;" style="display: none;"></div>
	          </div>
	        </div>
	        <div class="utill">      			
				<a href="javascript: mobile_comm_opensearch();" class="topH_search"><span class="Hicon"><spring:message code='Cache.btn_search' /></span></a> <!-- 검색 -->                                                    
        	</div>
		</div>
      
		<div class="ly_search ly_change">
			<a href="javascript: mobile_comm_closesearch();" class="topH_back"><span><spring:message code='Cache.btn_Close' /></span></a> <!-- 닫기 -->
			<input type="text" id="mobile_search_input" name="" value="" voiceInputType="change" voiceStyle="margin-right:25px;" voiceCallBack="mobile_schedule_clicksearch"  class="mobileViceInputCtrl" placeholder="<spring:message code='Cache.msg_schedule_searchBy' />" onkeypress="if (event.keyCode==13){ mobile_schedule_clicksearch(); return false;}" onkeyup="mobile_comm_searchinputui('mobile_search_input');" /><!-- 제목, 등록자로 검색 -->
			<a id="mobile_search_input_btn" href="javascript: mobile_schedule_clicksearch();" class="topH_search" style="display: none;"></a>
			<a href="javascript: mobile_comm_cleansearch(); mobile_comm_searchinputui('mobile_search_input');" class="topH_del"></a>
		</div>
	</header>
	<div data-role="content" class="cont_wrap" id="schedule_day_content">
		<div class="calendar_wrap day">
			<div class="calendar_ctrl">
				<div class="month_ctrl">
					<a href="javascript: mobile_schedule_ShowPrevNextCalendar('prev');"
						class="prev_month"></a>
					<p id="schedule_day_titleDate" class="t_month">
						<a href="#">2017.09</a> <span><spring:message code='Cache.lbl_List_View' />둘째주</span>
					</p>
					<a href="javascript: mobile_schedule_ShowPrevNextCalendar('next');"
						class="next_month"></a>
				</div>
				<a href="javascript: mobile_schedule_DayInit();" class="btn_today"><spring:message code='Cache.btn_Today' /></a> <!-- 오늘 -->
			</div>
			<div class="slide_bar">
				<span class="slide_color" id="sSlide_bar">바</span>
			</div>
			<div id="schedule_day_calendar" class="week_schedule day">
				<table>
					<colgroup>
						<col style="width: 10px;">
						<col style="width: 52px;">
						<col>
						<col>
						<col>
						<col>
						<col>
						<col>
						<col>
						<col style="width: 10px;">
					</colgroup>
					<thead>
						<tr id="schedule_day_calendarheader"></tr>
					</thead>
				</table>
			</div>
			<div class="week_schedule day scr">
				<table>
					<colgroup>
						<col style="width: 10px;">
						<col style="width: 62px">
						<col>
						<col style="width: 10px;">
					</colgroup>
					<tbody id="schedule_day_calendarbody"></tbody>
				</table>
			</div>
		</div>
		<ul id="schedule_day_list" class="schedule_list" style="height: calc(100% - 278px);"></ul>
	</div>
	
	<!-- 작성버튼 시작 -->
	<div class="list_writeBTN">
   		<a href="javascript: mobile_schedule_clickwrite();" class="btn_write"><span><spring:message code='Cache.btnWrite' /></span></a> <!-- 작성 -->
  	</div>
  	
	<div class="bg_dim" style="display: none;"></div>
		
</div>