<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil,egovframework.baseframework.util.RedisDataUtil"%>
<% 
String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); 
String cssPath2 = PropertiesUtil.getGlobalProperties().getProperty("css.path").replace("_n","");%>
<!doctype html>
<html lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=1280">
	<title><%=PropertiesUtil.getGlobalProperties().getProperty("front.title") %></title>
	<jsp:include page="/WEB-INF/views/cmmn/PortalInclude.jsp"></jsp:include> 
	<link rel="stylesheet" type="text/css" href="<%=cssPath2%>/customizing/ptype07/css/swiper.css" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath2%>/customizing/ptype07/css/project.css" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath2%>/customizing/ptype07/css/PN_dark.css" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath2%>/customizing/ptype07/css/color_01.css" />
	
	
	<script src="<%=cssPath2%>/customizing/ptype07/js/swiper.min.js"></script>
	<script src="<%=cssPath2%>/customizing/ptype07/js/project.js"></script>
</head>
<body class="portalBodyWrap">	
<div class="ui_portal" data-current-view="main">
	<div class="portal_background">
		<img src="<%=cssPath2%>/customizing/ptype07/images/project/bg01.jpg" alt="배경이미지">
	</div>
	<div class="portal_dim"></div>
	<div class="ui_global">
		<div class="global_service">
			<button type="button" class="service_toggle ui_icon_button" id="all_menu_toggle">
				<i class="material-icons-outlined">&#xe5d2;</i>
				<span>전체메뉴</span>
			</button>
		</div>
		<div class="global_logo">
			<a href="#"><img src="<%=cssPath2%>/customizing/ptype07/images/project/logo_global.png" alt="코비젼"></a>
		</div>
		<nav class="global_nav">
			<ul>
				<li><a href="#">전자결재</a>
					<ul>
						<li><a href="#">e-Accounting</a></li>
						<li><a href="#">사업관리</a></li>
					</ul>
				</li>
				<li><a href="#">메일</a>
					<ul>
						<li><a href="#">SPAM Mail</a></li>
					</ul>
				</li>
				<li><a href="#">문서관리</a></li>
				<li><a href="#">웹하드</a></li>
				<li><a href="#">업무도우미</a>
					<ul>
						<li><a href="#">자원예약</a></li>
						<li><a href="#">전자설문</a></li>
						<li><a href="#">일정관리</a></li>
						<li><a href="#">인명관리</a></li>
						<li><a href="#">업무관리</a></li>
						<li><a href="#">온라인 회의 예약</a></li>
						<li><a href="#">타임시트</a></li>
					</ul>
				</li>
				<li class="" data-menu-id="10" data-menu-target="Current" data-menu-alias="10" data-menu-url="%2Fgroupware%2Flayout%2Fleft.do%3FCLSYS%3Dboard%26CLMD%3Duser%26CLBIZ%3DBoard%26menuCode%3DBoardMain">
					<a href="javascript:;" onclick="CoviMenu_ClickTop(this);return false;"><span>통합게시</span></a>
				</li>
				<li><a href="#">커뮤니티</a>
					<ul>
						<li><a href="#">휴가관리</a></li>
						<li><a href="#">근태관리(신)</a></li>
					</ul>
				</li>
				<li><a href="#">이지뷰</a></li>
				<li><a href="#">협업스페이스</a></li>
			</ul>
		</nav>
		<nav class="global_utility">
			<button type="button" class="utility_toggle ui_icon_button utility_search_toggle" data-tooltip-title="검색">
				<i class="material-icons-outlined">&#xe8b6;</i>
				<span>검색</span>
			</button>
			<button type="button" class="utility_toggle ui_icon_button" data-tooltip-title="알림"
					onclick="UI_Drawer('alert_drawer')">
				<i class="material-icons-outlined">&#xe7f4;</i>
				<span>알림</span>
				<em>2</em>
			</button>
			<button type="button" class="utility_toggle ui_icon_button" data-tooltip-title="조직도">
				<i class="material-icons-outlined">&#xeb2f;</i>
				<span>조직도</span>
			</button>
			<button type="button" class="utility_toggle ui_icon_button" data-tooltip-title="간편작성"
					onclick="UI_Drawer('easy_writing_drawer')">
				<i class="material-icons-outlined">&#xe3c9;</i>
				<span>간편작성</span>
			</button>
			<button type="button" class="utility_toggle ui_icon_button" data-tooltip-title="가젯"
					onclick="UI_Drawer('gadget_drawer')">
				<i class="material-icons-outlined">&#xe86d;</i>
				<span>가젯</span>
			</button>
			<a href="#" class="ui_avatar utility_avatar" id="setting_toggle">
				<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
			</a>
		</nav>
	</div>
	<div class="ui_dock">
		<div class="dock_root">
			<div class="nav_list">
				<nav>
					<a href="#" class="link mail" data-tooltip-title="메일"><span>메일</span><em>7</em></a>
					<a href="#" class="link approval" data-tooltip-title="결재"><span>결재</span></a>
					<a href="#" class="link schedule" data-tooltip-title="일정"><span>일정</span></a>
					<a href="#" class="link survey" data-tooltip-title="설문"><span>설문</span></a>
					<a href="#" class="link notice" data-tooltip-title="게시"><span>게시</span></a>
					<a href="#" class="link request" data-tooltip-title="요청업무"><span>요청업무</span></a>
					<a href="#" class="link community" data-tooltip-title="커뮤니티"><span>커뮤니티</span></a>
					<button type="button" class="link mention" data-tooltip-title="멘션"
							onclick="UI_Drawer('mention_drawer')"><span>멘션</span>
					</button>
					<a href="#" class="link eum" data-tooltip-title="이음톡"><span>이음톡</span></a>
					<a href="#" class="link corporation" data-tooltip-title="협업스페이스"><span>협업스페이스</span></a>
				</nav>
			</div>
			<div class="nav_setting">
				<button type="button" class="setting_toggle ui_icon_button" id="dock_setting_toggle"><span>편집</span>
				</button>
				<div class="setting_layer ui_contextmenu" id="dock_setting" hidden>
					<div class="menu_head">
						<h3 class="title">독메뉴</h3>
						<div class="action">
							<label><input type="radio" name="dock" value="show" checked><span>고정</span></label>
							<label><input type="radio" name="dock" value="hidden"><span>숨기기</span></label>
						</div>
					</div>
					<div class="menu_content">
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>메일</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>결재</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>일정</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>설문</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>게시글</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>요청업무</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>커뮤니티</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>멘션</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>이음톡</span></label>
						<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>협업스페이스</span></label>
					</div>
				</div>
			</div>
			<button type="button" class="dock_open"><span>펼침</span></button>
		</div>
	</div>
	<div class="portal_root">
		<div class="portal_container">
			<div class="portal_main">
				<div class="main_logo">
					<a href="#"><img src="<%=cssPath2%>/customizing/ptype07/images/project/logo.png" alt="코비젼"></a>
				</div>
				<div class="main_search">
					<label class="input">
						<input type="text" placeholder="검색어를 입력하세요"/>
					</label>
					<div class="keyword_list">
						<ul>
							<li data-keyword="웹하드" class="history"><span>웹하드</span><i class="delete"><span>삭제</span></i>
							</li>
							<li data-keyword="개인정보" class="history"><span>개인정보</span><i
									class="delete"><span>삭제</span></i></li>
							<li data-keyword="휴가관리" class="history"><span>휴가관리</span><i
									class="delete"><span>삭제</span></i></li>
							<li data-keyword="타임시트" class="history"><span>타임시트</span><i
									class="delete"><span>삭제</span></i></li>
							<li data-keyword="근태관리" class="history"><span>근태관리</span><i
									class="delete"><span>삭제</span></i></li>
						</ul>
					</div>
				</div>
				<div class="main_ai">
					<div class="swiper main_ai_swiper">
						<div class="swiper-wrapper">
							<div class="swiper-slide">
								<div class="ai_root mail">
									<div class="ai_content">
										<div class="greetings">김하랑님. 오늘도 즐거운 하루 시작하세요~!</div>
										<div class="notice"><a href="#">현재 읽지 않은 메일이 <em>2건</em> 있습니다.</a></div>
									</div>
								</div>
							</div>
							<div class="swiper-slide">
								<div class="ai_root mail_weather">
									<div class="ai_content">
										<div class="weather">
											<div class="icon"><img src="<%=cssPath2%>/customizing/ptype07/images/project/icon_weather_02.svg"
																   alt="구름 많음">
											</div>
											<div class="info">
												<strong>23.3°</strong>
												<em>구름 많음</em>
											</div>
										</div>
										<div class="greetings">김하랑님. 오늘도 즐거운 하루 시작하세요~!</div>
										<div class="notice"><a href="#">현재 읽지 않은 메일이 <em>2건</em> 있습니다.</a></div>
									</div>
								</div>
							</div>
							<div class="swiper-slide">
								<div class="ai_root approval">
									<div class="ai_content">
										<div class="greetings">김하랑님. 오늘도 즐거운 하루 시작하세요~!</div>
										<div class="notice"><a href="#">결재대기중인 문서가 <em>2건</em> 있습니다.</a></div>
									</div>
								</div>
							</div>
							<div class="swiper-slide">
								<div class="ai_root must">
									<div class="ai_content">
										<div class="greetings">김하랑님. 오늘도 즐거운 하루 시작하세요~!</div>
										<div class="notice"><a href="#">[필독] 10월 수당 및 복리후생비 신청안내</a></div>
									</div>
								</div>
							</div>
						</div>
						<div class="swiper-control">
							<div class="swiper-pagination"></div>
							<button type="button" class="ui_icon_button swiper-button-play"><span>정지/재생</span></button>
						</div>
					</div>
				</div>
				<div class="main_widget">
					<div class="swiper main_widget_swiper">
						<div class="swiper-wrapper">
							<!-- 전체공지 -->
							<div class="swiper-slide">
								<div class="widget_card horizontal card_notice">
									<div class="card_aside">
										<h3><span>전체공지</span></h3>
										<em>3</em>
										<a href="#"><span>전체공지 바로가기</span></a>
									</div>
									<div class="card_list">
										<ul>
											<li>
												<a href="#">
													<span>2022년 공단 건강검진 안내입니다.</span>
													<time>2022.09.25</time>
												</a>
											</li>
											<li>
												<a href="#">
													<span>사옥 냉방(에어컨) 가동시간 변경 안내</span>
													<time>2022.09.25</time>
												</a>
											</li>
											<li>
												<a href="#">
													<span>2022년 독감 예방접종 안내</span>
													<time>2020.09.25</time>
												</a>
											</li>
										</ul>
									</div>
								</div>
							</div>
							<!-- 받은메일 -->
							<div class="swiper-slide">
								<div class="widget_card horizontal card_mail">
									<div class="card_aside">
										<h3><span>받은메일</span></h3>
										<em>+99</em>
										<a href="#"><span>받은메일 바로가기</span></a>
									</div>
									<div class="card_list">
										<ul>
											<li><a href="#"><span>그룹웨어 디자인 첨부하여 드립니다.</span></a></li>
											<li><a href="#"><span>그룹웨어 디자인 첨부하여 드립니다.</span></a></li>
											<li><a href="#"><span>그룹웨어 디자인 첨부하여 드립니다.</span></a></li>
											<li><a href="#"><span>그룹웨어 디자인 첨부하여 드립니다.</span></a></li>
											<li><a href="#"><span>그룹웨어 디자인 첨부하여 드립니다.</span></a></li>
										</ul>
									</div>
								</div>
							</div>
							<!-- 할일 -->
							<div class="swiper-slide">
								<div class="widget_card horizontal card_todo">
									<div class="card_aside">
										<h3><span>할일</span></h3>
										<em>3</em>
										<a href="#"><span>할일 바로가기</span></a>
									</div>
									<div class="card_list">
										<ul>
											<li><a href="#"><span>CP 그룹웨어 디자인 회의</span></a></li>
											<li><a href="#"><span>CP 그룹웨어 디자인 XD파일 정리</span></a></li>
											<li><a href="#"><span>웹파트 UX개선 벤치마킹</span></a></li>
										</ul>
									</div>
								</div>
							</div>
							<!-- 임직원 즐겨찾기 -->
							<div class="swiper-slide">
								<div class="widget_card card_favorites">
									<div class="card_head">
										<h3>임직원 즐겨찾기</h3>
									</div>
									<div class="card_content">
										<div class="swiper main_favorites_swiper">
											<div class="swiper-wrapper">
												<div class="swiper-slide">
													<div class="favorites_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<span>홍길동전</span>
													</div>
												</div>
												<div class="swiper-slide">
													<div class="favorites_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<span>홍길동</span>
													</div>
												</div>
												<div class="swiper-slide">
													<div class="favorites_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<span>김송이</span>
													</div>
												</div>
												<div class="swiper-slide">
													<div class="favorites_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<span>황송이</span>
													</div>
												</div>
											</div>
										</div>
										<div class="swiper-button-next main_favorites_button_next">
											<span>다음</span>
										</div>
										<div class="swiper-button-prev main_favorites_button_prev">
											<span>이전</span>
										</div>
									</div>
								</div>
							</div>
							<!-- 날씨 -->
							<div class="swiper-slide">
								<div class="widget_card card_weather">
									<div class="card_head">
										<h3>서울</h3>
									</div>
									<div class="card_content">
										<div class="weather">
											<div class="now">
												<span class="wt1">구름 많음</span>
												<strong>23°</strong>
												<em class="max">24°</em>
												<em class="min">1°</em>
											</div>
											<div class="next">
												<dl>
													<dt>26일</dt>
													<dd class="wt1"><span>구름 많음</span></dd>
												</dl>
												<dl>
													<dt>27일</dt>
													<dd class="wt1"><span>구름 많음</span></dd>
												</dl>
												<dl>
													<dt>28일</dt>
													<dd class="wt1"><span>구름 많음</span></dd>
												</dl>
												<dl>
													<dt>29일</dt>
													<dd class="wt1"><span>구름 많음</span></dd>
												</dl>
												<dl>
													<dt>30일</dt>
													<dd class="wt1"><span>구름 많음</span></dd>
												</dl>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- 환율게시 -->
							<div class="swiper-slide">
								<div class="widget_card card_money">
									<div class="card_head">
										<h3>환율게시</h3>
										<time>2022.07.25(월)</time>
									</div>
									<div class="card_content">
										<div class="item">
											<img src="<%=cssPath2%>/customizing/ptype07/images/project/flag_usa.svg" alt="미국"/>
											<div class="info">
												<strong>USD</strong>
												<em>1,284.80</em>
												<i class="up"><span>증가</span></i>
											</div>
										</div>
										<div class="item">
											<img src="<%=cssPath2%>/customizing/ptype07/images/project/flag_europe.svg" alt="유럽"/>
											<div class="info">
												<strong>EUR</strong>
												<em>1,358.77</em>
												<i class="down"><span>증가</span></i>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- 임직원 소식 -->
							<div class="swiper-slide">
								<div class="widget_card card_state">
									<div class="card_head">
										<h3>임원재실 정보</h3>
									</div>
									<div class="card_content">
										<div class="swiper main_employee_state_swiper">
											<div class="swiper-wrapper">
												<div class="swiper-slide">
													<div class="state_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<i class="on">재실</i>
														<strong>위하랑 부사장</strong>
														<em>솔루션개발부</em>
													</div>
												</div>
												<div class="swiper-slide">
													<div class="state_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<i class="holidays">휴가</i>
														<strong>김비젼 부사장</strong>
														<em>솔루션사업부</em>
													</div>
												</div>
												<div class="swiper-slide">
													<div class="state_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<i class="meeting">회의</i>
														<strong>위하랑 부사장</strong>
														<em>솔루션사업부</em>
													</div>
												</div>
												<div class="swiper-slide">
													<div class="state_item">
														<div class="ui_avatar">
															<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
														</div>
														<i class="on">재실</i>
														<strong>위하랑 부사장</strong>
														<em>솔루션사업부</em>
													</div>
												</div>
											</div>
										</div>
										<div class="swiper-button-next main_employee_state_button_next">
											<span>다음</span>
										</div>
										<div class="swiper-button-prev main_employee_state_button_prev">
											<span>이전</span>
										</div>
									</div>
								</div>
							</div>
							<!-- 최근 사용 양식 -->
							<div class="swiper-slide">
								<div class="widget_card card_form">
									<div class="card_head">
										<h3>최근 사용 양식</h3>
									</div>
									<div class="card_content">
										<div class="swiper main_form_swiper">
											<div class="swiper-wrapper">
												<div class="swiper-slide">
													<a href="#" class="form_item c1"><span>연장근무 신청서</span></a>
												</div>
												<div class="swiper-slide">
													<a href="#" class="form_item c2"><span>소명신청서</span></a>
												</div>
												<div class="swiper-slide">
													<a href="#" class="form_item c3"><span>프로젝트 집행계획서</span></a>
												</div>
												<div class="swiper-slide">
													<a href="#" class="form_item c1"><span>양식명</span></a>
												</div>
											</div>
										</div>
										<div class="swiper-button-next main_form_button_next">
											<span>다음</span>
										</div>
										<div class="swiper-button-prev main_form_button_prev">
											<span>이전</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="swiper-button-next main_widget_button_next">
						<span>다음</span>
					</div>
					<div class="swiper-button-prev main_widget_button_prev">
						<span>이전</span>
					</div>
				</div>
				<div class="main_guide">
					<div class="swiper main_guide_swiper">
						<div class="swiper-wrapper">
							<div class="swiper-slide">
								<div class="guide_text">
									<p>코비젼이 제공하는 <span><strong>협업 그룹웨어</strong></span>
										<span><strong>'하랑'</strong>과 <strong>'위하랑'</strong>을</span> 소개합니다.</p>
								</div>
							</div>
							<div class="swiper-slide">
								<div class="guide_text">
									<p>안녕하세요</p>
								</div>
							</div>
						</div>
						<div class="swiper-control">
							<button type="button" class="ui_icon_button swiper-button-play"><span>정지/재생</span></button>
							<div class="swiper-pagination"></div>
						</div>
					</div>
					<button type="button" class="close ui_icon_button"><span>닫기</span></button>
				</div>
			</div>
			<div class="portal_my_content">
				<div class="ui_page_links">
					<div class="link_list">
						<div class="link">
							<a href="#">주간보고</a>
							<button type="button" class="pin on"><span>고정</span></button>
							<button type="button" class="close"><span>닫기</span></button>
						</div>
						<div class="link">
							<a href="#">뉴스레터</a>
							<button type="button" class="pin on"><span>고정</span></button>
							<button type="button" class="close"><span>닫기</span></button>
						</div>
						<div class="link active">
							<a href="#">전체 글 보기</a>
							<button type="button" class="pin"><span>고정</span></button>
							<button type="button" class="close"><span>닫기</span></button>
						</div>
					</div>
					<div class="link_action">
						<button type="button" class="ui_button normal"><i class="material-icons">&#xe5cd;</i><span>전체삭제</span></button>
					</div>
				</div>
				<div class="my_content_body">
					<div class="gadget_area">
						<!-- 임직원 즐겨찾기 -->
						<div class="ui_my_content_gadget gadget_card gadget_employee_favorites">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">임직원 즐겨찾기</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="swiper my_content_employee_favorites_swiper">
										<div class="swiper-wrapper">
											<div class="swiper-slide">
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="red"></em>
												</a>
												<a href="#" class="item">
													<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
													<strong>홍길동</strong>
													<em class="white"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="green"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="gray"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="green"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="pink"></em>
												</a>
											</div>
											<div class="swiper-slide">
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="red"></em>
												</a>
												<a href="#" class="item">
													<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
													<strong>홍길동</strong>
													<em class="white"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="green"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="gray"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="green"></em>
												</a>
												<a href="#" class="item">
													<span class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></span>
													<strong>홍길동</strong>
													<em class="pink"></em>
												</a>
											</div>
										</div>
									</div>
									<div class="swiper-button-next my_content_employee_favorites_button_next">
										<span>다음</span>
									</div>
									<div class="swiper-button-prev my_content_employee_favorites_button_prev">
										<span>이전</span>
									</div>
								</div>
								<div class="profile" hidden>
									<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
									<div class="title">
										<strong>이길동 대리 /</strong>
										<em>UX디자인팀</em>
									</div>
									<div class="tel">010-1234-5678</div>
									<div class="link">
										<a href="#"><i class="material-icons">&#xe0ca;</i><span>대화</span></a>
										<a href="#"><i class="material-icons">&#xe158;</i><span>이메일</span></a>
										<a href="#"><i class="material-icons">&#xe7f4;</i><span>알림</span></a>
									</div>
									<button type="button" class="ui_icon_button close"><i class="material-icons">&#xe5cd;</i><span>닫기</span></button>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 프로필 -->
						<div class="ui_my_content_gadget gadget_card gadget_profile">
							<div class="gadget_root">
								<div class="user_info">
									<div class="avatar">
										<div class="ui_avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar_big.jpg" alt="아바타">
										</div>
									</div>
									<div class="info">
										<strong>홍길동 과장</strong>
										<span>UX디자인팀</span>
									</div>
								</div>
								<nav class="link">
									<a href="#"><i class="material-icons">&#xe151;</i><span>메일</span><em>2</em></a>
									<a href="#"><i
											class="material-icons-outlined">&#xe9a2;</i><span>미결함</span><em>5</em></a>
									<a href="#"><i class="material-icons-outlined">&#xe878;</i><span>일정</span><em>3</em></a>
									<a href="#"><i class="material-icons">&#xe873;</i><span>업무</span><em>6</em></a>
								</nav>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 알림 -->
						<div class="ui_my_content_gadget gadget_card gadget_alert x2">
							<div class="gadget_root">
								<p class="hello">홍길동님 안녕하세요~</p>
								<p class="alert">현재 읽지 않은 메일이 <em>2건</em> 있습니다.</p>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 동영상 -->
						<div class="ui_my_content_gadget gadget_card gadget_video">
							<div class="gadget_root">
								<div class="item">
									<img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_gadget_video.jpg" alt="이미지">
									<div class="contents">
										<h3>올해도 코비젼과 함께 스마트하게 일하기</h3>
										<div class="info">
											<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg"
																			   alt="아바타"></div>
											<strong>김하랑 사원</strong>
											<time>05.02</time>
										</div>
									</div>
									<a href="#" class="link"><span>상세보기</span></a>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 임원재실 정보 -->
						<div class="ui_my_content_gadget gadget_card gadget_executives">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">임원재실 정보</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<ul class="list">
										<li>
											<div class="item">
												<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg"
																				   alt="아바타"></div>
												<i class="on">재실</i>
												<strong>위하랑 부사장</strong>
												<em>솔루션사업부</em>
											</div>
										</li>
										<li>
											<div class="item">
												<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg"
																				   alt="아바타"></div>
												<i class="holidays">휴가</i>
												<strong>김비젼 부사장</strong>
												<em>솔루션사업부</em>
											</div>
										</li>
										<li>
											<div class="item">
												<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg"
																				   alt="아바타"></div>
												<i class="meeting">회의</i>
												<strong>위하랑 부사장</strong>
												<em>솔루션사업부</em>
											</div>
										</li>
										<li>
											<div class="item">
												<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg"
																				   alt="아바타"></div>
												<i class="on">재실</i>
												<strong>김비젼 부사장</strong>
												<em>솔루션사업부</em>
											</div>
										</li>
									</ul>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 일정 -->
						<div class="ui_my_content_gadget gadget_card gadget_schedule y2">
							<div class="gadget_root">
								<div class="schedule_calendar">
									<div class="calendar_head">
										<h3>2022.05.02 <em>월요일</em></h3>
										<div class="action">
											<button type="button" class="ui_icon_button"><i class="material-icons">&#xe5cb;</i><span>이전달</span></button>
											<button type="button" class="ui_icon_button"><i class="material-icons">&#xe5cc;</i><span>다음달</span></button>
										</div>
									</div>
									<div class="calendar_body">
										<table>
											<thead>
											<tr>
												<th class="sun">Sun</th>
												<th>Mon</th>
												<th>Tue</th>
												<th>Wed</th>
												<th>Thu</th>
												<th>Fri</th>
												<th class="sat">Sat</th>
											</tr>
											</thead>
											<tbody>
											<tr>
												<td><a href="#" class="sun">1</a></td>
												<td><a href="#" class="current">2</a></td>
												<td><a href="#" class="event">3</a></td>
												<td><a href="#">4</a></td>
												<td><a href="#">5</a></td>
												<td><a href="#">6</a></td>
												<td><a href="#" class="sat">7</a></td>
											</tr>
											<tr>
												<td><a href="#" class="sun">8</a></td>
												<td><a href="#">9</a></td>
												<td><a href="#">10</a></td>
												<td><a href="#">11</a></td>
												<td><a href="#">12</a></td>
												<td><a href="#">13</a></td>
												<td><a href="#" class="sat">14</a></td>
											</tr>
											<tr>
												<td><a href="#" class="sun">15</a></td>
												<td><a href="#">16</a></td>
												<td><a href="#" class="event">17</a></td>
												<td><a href="#">18</a></td>
												<td><a href="#">19</a></td>
												<td><a href="#">20</a></td>
												<td><a href="#" class="sat">21</a></td>
											</tr>
											<tr>
												<td><a href="#" class="sun">22</a></td>
												<td><a href="#">23</a></td>
												<td><a href="#">24</a></td>
												<td><a href="#">25</a></td>
												<td><a href="#">26</a></td>
												<td><a href="#">27</a></td>
												<td><a href="#" class="sat">28</a></td>
											</tr>
											<tr>
												<td><a href="#" class="sun">29</a></td>
												<td><a href="#" class="other">30</a></td>
												<td><a href="#" class="other">31</a></td>
												<td><a href="#" class="other">1</a></td>
												<td><a href="#" class="other">2</a></td>
												<td><a href="#" class="other">3</a></td>
												<td><a href="#" class="other sat">4</a></td>
											</tr>
											<tr>
												<td><a href="#" class="other sun">5</a></td>
												<td><a href="#" class="other">6</a></td>
												<td><a href="#" class="other">7</a></td>
												<td><a href="#" class="other">8</a></td>
												<td><a href="#" class="other">9</a></td>
												<td><a href="#" class="other">10</a></td>
												<td><a href="#" class="other sat">11</a></td>
											</tr>
											</tbody>
										</table>
									</div>
								</div>
								<div class="schedule_list scroll_event">
									<ul>
										<li>
											<a href="#" class="now">
												<strong>연간추진과제 보고</strong>
												<time>10:00~11:00</time>
											</a>
										</li>
										<li>
											<a href="#">
												<strong>기술연구소 회의</strong>
												<time>11:00~12:00</time>
											</a>
										</li>
										<li>
											<a href="#">
												<strong>1분기 실적 보고</strong>
												<time>13:00~14:00</time>
											</a>
										</li>
										<li>
											<a href="#">
												<strong>코비젼 그룹웨어 일정 제목 테스트입니다.</strong>
												<time>14:00~15:00</time>
											</a>
										</li>
										<li>
											<a href="#">
												<strong>코비젼 그룹웨어 일정 제목 테스트입니다.</strong>
												<time>16:00~17:00</time>
											</a>
										</li>
									</ul>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 게시글 -->
						<div class="ui_my_content_gadget gadget_card gadget_board x2 y2">
							<div class="gadget_root">
								<div class="gadget_head">
									<div class="tabs ui_tabs">
										<button type="button" class="tab" aria-selected="true" id="gadget_board_notice_tab1" aria-controls="gadget_board_notice_panel1"><span>공지사항</span></button>
										<button type="button" class="tab" aria-selected="false" id="gadget_board_notice_tab2" aria-controls="gadget_board_notice_panel2"><span>받은메일</span></button>
										<button type="button" class="tab" aria-selected="false" id="gadget_board_notice_tab3" aria-controls="gadget_board_notice_panel3"><span>미결문서</span></button>
										<button type="button" class="tab" aria-selected="false" id="gadget_board_notice_tab4" aria-controls="gadget_board_notice_panel4"><span>최근게시</span></button>
									</div>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div role="tabpanel" class="board_list" id="gadget_board_notice_panel1" aria-labelledby="gadget_board_notice_tab1">
									<ul>
										<li>
											<a href="#">
												<em class="highlight">전체공지</em>
												<i>N</i>
												<strong>2022년 공단 건강검진 안내입니다.</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em class="highlight">전체공지</em>
												<strong>사옥 냉방(에어컨) 가동시간 변경 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>2022년 독감 예방접종 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>8/17(월) 임시공휴일 휴무 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>2021년 공단 건강검진 실시 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>정규직 채용에 대한 인사발령</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>사옥 소방 안전점검 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>8/17(월) 임시공휴일 휴무 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>2021년 공단 건강검진 실시 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>정규직 채용에 대한 인사발령</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
										<li>
											<a href="#">
												<em>일반공지</em>
												<strong>사옥 소방 안전점검 안내</strong>
												<span>김하랑</span>
												<time>2022.05.02</time>
											</a>
										</li>
									</ul>
								</div>
								<div hidden role="tabpanel" class="board_list" id="gadget_board_notice_panel2" aria-labelledby="gadget_board_notice_tab2">
									받은 메일
								</div>
								<div hidden role="tabpanel" class="board_list" id="gadget_board_notice_panel3" aria-labelledby="gadget_board_notice_tab4">
									미결문서 부분
								</div>
								<div hidden role="tabpanel" class="board_list" id="gadget_board_notice_panel4" aria-labelledby="gadget_board_notice_tab5">
									최근게시 부분
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 업무 일정 -->
						<div class="ui_my_content_gadget gadget_card gadget_work_schedule y2">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">업무 일정</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="history-list">
										<h4>2022-05-02</h4>
										<ul>
											<li><a href="#" class="history old"><span>연간추진과제 보고</span><time>10:00~11:00</time></a></li>
											<li><a href="#" class="history old"><span>영업본부 회의</span><time>13:00~14:00</time></a></li>
											<li><a href="#" class="history old"><span>기획서 제출</span><time>15:00</time></a></li>
											<li><a href="#" class="history"><span>3분기 실적 보고</span><time>16:00~18:00</time></a></li>
										</ul>
										<h4>2022-05-03</h4>
										<ul>
											<li><a href="#" class="history"><span>영업본부 회의</span><time>09:00~10:00</time></a></li>
											<li><a href="#" class="history"><span>연간추진과제 보고</span><time>10:00~11:00</time></a></li>
										</ul>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 최근 사용 양식 -->
						<div class="ui_my_content_gadget gadget_card gadget_recently_used_form">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">최근 사용 양식</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="list">
										<ul>
											<li><a href="#" class="i1">연장근무 신청서</a></li>
											<li><a href="#" class="i2">소명신청서</a></li>
											<li><a href="#" class="i3">프로젝트 집행계획서</a></li>
										</ul>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- web hard -->
						<div class="ui_my_content_gadget gadget_card gadget_web_hard">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">web hard</h3>
									<button type="button" class="ui_icon_button"><i
											class="material-icons">&#xe2c6;</i><span>업로드</span></button>
								</div>
								<div class="gadget_content">
									<div class="progress">
										<div class="bar">
											<div class="ing" style="width:70%"></div>
										</div>
										<div class="info">
											<dl>
												<dt>현재</dt>
												<dd>70GB</dd>
											</dl>
											<dl>
												<dt>최대</dt>
												<dd>100GB</dd>
											</dl>
										</div>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 받은 메일 -->
						<div class="ui_my_content_gadget gadget_card gadget_mail">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">받은 메일</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="mail_list">
										<ul>
											<li>
												<a href="#" class="mail_item unread">
													<span>박경선</span> <time>2020.04.09</time>
													<strong>그룹웨어 디자인 시안 첨부하여 드립니다.</strong>
												</a>
											</li>
											<li>
												<a href="#" class="mail_item unread">
													<span>박경선</span> <time>2020.04.09</time>
													<strong>그룹웨어 디자인 시안 첨부하여 드립니다.</strong>
												</a>
											</li>
											<li>
												<a href="#" class="mail_item read">
													<span>박경선</span> <time>2020.04.09</time>
													<strong>그룹웨어 디자인 시안 첨부하여 드립니다.</strong>
												</a>
											</li>
											<li>
												<a href="#" class="mail_item read">
													<span>박경선</span> <time>2020.04.09</time>
													<strong>그룹웨어 디자인 시안 첨부하여 드립니다.</strong>
												</a>
											</li>
										</ul>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 희이실 예약 -->
						<div class="ui_my_content_gadget gadget_card gadget_meeting">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">회의실 예약</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="form">
										<div class="date">
											<label class="ui_text_field icon_date"><input type="text" value="2022-05-03"></label>
											<label class="ui_text_field icon_time"><input type="text" value="14:00 ~ 15:30"></label>
										</div>
										<div class="target">
											<label class="ui_select">
												<select>
													<option>302호 (8인)</option>
												</select>
											</label>
										</div>
										<div class="selector_time">
											<dl>
												<dt>09</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="0900"><i></i><span>09:00</span></label>
													<label><input type="checkbox" name="meeting" value="0930"><i></i><span>09:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>10</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1000"><i></i><span>10:00</span></label>
													<label><input type="checkbox" name="meeting" value="1030"><i></i><span>10:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>11</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1100"><i></i><span>11:00</span></label>
													<label><input type="checkbox" name="meeting" value="1130"><i></i><span>11:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>12</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1200"><i></i><span>12:00</span></label>
													<label><input type="checkbox" name="meeting" value="1230"><i></i><span>12:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>13</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1300"><i></i><span>13:00</span></label>
													<label><input type="checkbox" name="meeting" value="1350"><i></i><span>13:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>14</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1400"><i></i><span>14:00</span></label>
													<label><input type="checkbox" name="meeting" value="1430"><i></i><span>14:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>15</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1500"><i></i><span>15:00</span></label>
													<label><input type="checkbox" name="meeting" value="1530"><i></i><span>15:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>16</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1600"><i></i><span>16:00</span></label>
													<label><input type="checkbox" name="meeting" value="1630"><i></i><span>16:30</span></label>
												</dd>
											</dl>
											<dl>
												<dt>17</dt>
												<dd>
													<label><input type="checkbox" name="meeting" value="1700"><i></i><span>17:00</span></label>
													<label><input type="checkbox" name="meeting" value="1730"><i></i><span>17:30</span></label>
												</dd>
											</dl>
										</div>
									</div>
									<div class="gadget_link"><a href="#"><span>회의실 예약</span></a></div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 할일 -->
						<div class="ui_my_content_gadget gadget_card gadget_todo">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">할일</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="todo">
										<div class="count">0개 완료됨</div>
										<ul class="list">
											<li>
												<div class="todo_item done">
													<a href="#">CP 그룹웨어 디자인 회의</a>
													<label><input type="checkbox" checked><i></i><span>완료</span></label>
												</div>
											</li>
											<li>
												<div class="todo_item">
													<a href="#">CP 그룹웨어 디자인 XD파일 레이어 정리</a>
													<label><input type="checkbox"><i></i><span>완료</span></label>
												</div>
											</li>
											<li>
												<div class="todo_item">
													<a href="#">웹파트 UX개선 벤치마킹</a>
													<label><input type="checkbox"><i></i><span>완료</span></label>
												</div>
											</li>
											<li>
												<div class="todo_item">
													<a href="#">메인 배경이미지 3종 디자인</a>
													<label><input type="checkbox"><i></i><span>완료</span></label>
												</div>
											</li>
											<li>
												<div class="todo_item">
													<a href="#">아이콘 디자인</a>
													<label><input type="checkbox"><i></i><span>완료</span></label>
												</div>
											</li>
										</ul>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 휴가관리 -->
						<div class="ui_my_content_gadget gadget_card gadget_holiday">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">휴가관리</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="holidays">
										<div class="info">
											<dl>
												<dt>총 휴가일:</dt>
												<dd>20</dd>
											</dl>
											<dl>
												<dt>잔여:</dt>
												<dd>2</dd>
											</dl>
										</div>
										<ul class="list">
											<li><span>1번째 사용</span></li>
											<li><span>2번째 사용</span></li>
											<li><span>3번째 사용</span></li>
											<li><span>4번째 사용</span></li>
											<li><span>5번째 사용</span></li>
											<li><span>6번째 사용</span></li>
											<li><span>7번째 사용</span></li>
											<li><span>8번째 사용</span></li>
											<li><span>9번째 사용</span></li>
											<li><span>10번째 사용</span></li>
											<li><span>11번째 사용</span></li>
											<li><span>12번째 사용</span></li>
											<li><span>13번째 사용</span></li>
											<li><span>14번째 사용</span></li>
											<li><span>15번째 사용</span></li>
											<li><span>16번째 사용</span></li>
											<li><span>17번째 사용</span></li>
											<li><span>18번째 사용</span></li>
											<li class="active"><span>19번째 미사용</span></li>
											<li class="active"><span>20번째 미사용</span></li>
										</ul>
									</div>
									<div class="gadget_link"><a href="#"><span>휴가신청</span></a></div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 업무시스템 -->
						<div class="ui_my_content_gadget gadget_card gadget_work_system">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">업무시스템</h3>
								</div>
								<div class="gadget_content">
									<nav class="link">
										<a href="#" class="i1"><span>전결규정</span></a>
										<a href="#" class="i2"><span>전사연간일정</span></a>
										<a href="#" class="i3"><span>타임시트</span></a>
										<a href="#" class="i4"><span>개인프로필</span></a>
										<a href="#" class="i5"><span>팀사이트</span></a>
										<a href="#" class="i6"><span>사옥A/S접수</span></a>
									</nav>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 설문 -->
						<div class="ui_my_content_gadget gadget_card gadget_survey">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">설문</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="survey_item">
										<h4 class="title"><a href="#">코비젼 2022년 워크샵 장소</a></h4>
										<ul>
											<li>
												<div class="option">
													<dl class="info">
														<dt>가평</dt>
														<dd>50%</dd>
													</dl>
													<div class="progress c1">
														<div class="bar" style="width:50%"></div>
													</div>
												</div>
											</li>
											<li>
												<div class="option">
													<dl class="info">
														<dt>강화도</dt>
														<dd>40%</dd>
													</dl>
													<div class="progress c2">
														<div class="bar" style="width:40%"></div>
													</div>
												</div>
											</li>
											<li>
												<div class="option">
													<dl class="info">
														<dt>본사</dt>
														<dd>10%</dd>
													</dl>
													<div class="progress c3">
														<div class="bar" style="width:10%"></div>
													</div>
												</div>
											</li>
										</ul>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 내 근태현황 -->
						<div class="ui_my_content_gadget gadget_card gadget_attendance">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">내 근태현황</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="chart" style="background:conic-gradient(#848cff 130deg, #debeff 130deg 160deg, #ffb300 160deg 190deg, #f1f3fb 190deg);"></div>
									<div class="info">
										<dl class="normal">
											<dt>정상근무</dt>
											<dd>8h</dd>
										</dl>
										<dl class="extension">
											<dt>연장근무</dt>
											<dd>2h</dd>
										</dl>
										<dl class="holiday">
											<dt>휴일근무</dt>
											<dd>2h</dd>
										</dl>
										<dl class="surplus">
											<dt>잔여근무</dt>
											<dd>10h</dd>
										</dl>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 날씨 -->
						<div class="ui_my_content_gadget gadget_card gadget_weather white_text">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">서울</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="weather">
										<div class="now">
											<div class="state wt1">구름 많음</div>
											<div class="temperature">
												<strong>23°</strong>
												<em class="max">24°</em>
												<em class="min">1°</em>
											</div>
										</div>
										<div class="next">
											<dl>
												<dt>26일</dt>
												<dd class="wt1"><span>구름 많음</span></dd>
											</dl>
											<dl>
												<dt>27일</dt>
												<dd class="wt1"><span>구름 많음</span></dd>
											</dl>
											<dl>
												<dt>28일</dt>
												<dd class="wt1"><span>구름 많음</span></dd>
											</dl>
											<dl>
												<dt>29일</dt>
												<dd class="wt1"><span>구름 많음</span></dd>
											</dl>
											<dl>
												<dt>30일</dt>
												<dd class="wt1"><span>구름 많음</span></dd>
											</dl>
										</div>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 인기 커뮤니티 -->
						<div class="ui_my_content_gadget gadget_card gadget_community white_text">
							<div class="gadget_root">
								<div class="gadget_head fixed">
									<h3 class="title">인기 커뮤니티</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="swiper my_content_community_swiper">
										<div class="swiper-wrapper">
											<div class="swiper-slide">
												<div class="community_item">
													<div class="preview"><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_sample_1.jpg" alt="이미지"></div>
													<div class="info">
														<h4><em>코비젼 등산 커뮤니티</em> <strong>코비몽</strong></h4>
														<span class="count">57</span>
													</div>
													<a href="#" class="link"><span>상세보기</span></a>
												</div>
											</div>
											<div class="swiper-slide">
												<div class="community_item">
													<div class="preview"><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_sample_2.jpg" alt="이미지"></div>
													<div class="info">
														<h4><em>코비젼 취미 커뮤니티</em> <strong>코비</strong></h4>
														<span class="count">100</span>
													</div>
													<a href="#" class="link"><span>상세보기</span></a>
												</div>
											</div>
										</div>
										<div class="swiper-pagination"></div>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 게시글 -->
						<div class="ui_my_content_gadget gadget_card gadget_post">
							<div class="gadget_root">
								<div class="post_item">
									<div class="preview">
										<img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_post.jpg" alt="이미지">
									</div>
									<div class="info">
										<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
										<strong class="name">위하랑 사원</strong>
										<time class="date">05.02</time>
										<div class="meta">
											<dl class="like">
												<dt><span>좋아요</span></dt>
												<dd>3</dd>
											</dl>
											<dl class="comments">
												<dt><span>댓글</span></dt>
												<dd>1</dd>
											</dl>
										</div>
									</div>
									<a href="#" class="link"><span>상세보기</span></a>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 갤러리 -->
						<div class="ui_my_content_gadget gadget_card gadget_gallery">
							<div class="gadget_root">
								<div class="grid">
									<ul>
										<li><a href="#"><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_sample_1.jpg" alt="이미지"></a>
										</li>
										<li><a href="#"><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_sample_2.jpg" alt="이미지"></a>
										</li>
										<li><a href="#"><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_sample_3.jpg" alt="이미지"></a>
										</li>
										<li><a href="#"><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_sample_4.jpg" alt="이미지"></a>
										</li>
									</ul>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 회사사보 -->
						<div class="ui_my_content_gadget gadget_card gadget_newsletter">
							<div class="gadget_root">
								<div class="gadget_head">
									<h3 class="title">회사사보</h3>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div class="gadget_content">
									<div class="swiper my_content_newsletter_swiper">
										<div class="swiper-wrapper">
											<div class="swiper-slide">
												<a href="#" class="item">
													<span><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_newsletter01.jpg" alt="이미지"></span>
													<strong>2022.07월호</strong>
												</a>
											</div>
											<div class="swiper-slide">
												<a href="#" class="item">
													<span><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_newsletter02.jpg" alt="이미지"></span>
													<strong>2022.05월호</strong>
												</a>
											</div>
											<div class="swiper-slide">
												<a href="#" class="item">
													<span><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_newsletter01.jpg" alt="이미지"></span>
													<strong>2022.07월호</strong>
												</a>
											</div>
											<div class="swiper-slide">
												<a href="#" class="item">
													<span><img src="<%=cssPath2%>/customizing/ptype07/images/project/photo_newsletter02.jpg" alt="이미지"></span>
													<strong>2022.05월호</strong>
												</a>
											</div>
										</div>
									</div>
									<div class="swiper-button-next my_content_newsletter_button_next">
										<span>다음</span>
									</div>
									<div class="swiper-button-prev my_content_newsletter_button_prev">
										<span>이전</span>
									</div>
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
						<!-- 결재 -->
						<div class="ui_my_content_gadget gadget_card gadget_approval">
							<div class="gadget_root">
								<div class="gadget_head">
									<div class="tabs ui_tabs">
										<button type="button" class="tab" aria-selected="true" id="gadget_approval_tab1" aria-controls="gadget_approval_panel1"><span>미결함</span></button>
										<button type="button" class="tab" aria-selected="false" id="gadget_approval_tab2" aria-controls="gadget_approval_panel2"><span>완료함</span></button>
										<button type="button" class="tab" aria-selected="false" id="gadget_approval_tab3" aria-controls="gadget_approval_panel3"><span>참조/회람</span></button>
									</div>
									<div class="action">
										<button type="button" class="ui_icon_button option"><span>옵션</span></button>
										<a href="#" class="ui_icon_button more"><span>더보기</span></a>
									</div>
								</div>
								<div role="tabpanel" class="approval_list" id="gadget_approval_panel1" aria-labelledby="gadget_approval_tab1">
									<ul>
										<li>
											<a href="#">
												<strong>그룹웨어 집행 계획서</strong>
												<em>개인 합의</em>
												<em>홍길동</em>
												<time>04.09</time>
												<em>2/5</em>
												<span><span style="width:40%"></span></span>
											</a>
										</li>
										<li>
											<a href="#">
												<strong>그룹웨어 집행 계획서</strong>
												<em>개인 합의</em>
												<em>홍길동</em>
												<time>04.09</time>
												<em>2/5</em>
												<span><span style="width:40%"></span></span>
											</a>
										</li>
										<li>
											<a href="#">
												<strong>그룹웨어 집행 계획서</strong>
												<em>개인 합의</em>
												<em>홍길동</em>
												<time>04.09</time>
												<em>2/5</em>
												<span><span style="width:40%"></span></span>
											</a>
										</li>
									</ul>
								</div>
								<div hidden role="tabpanel" class="board_list" id="gadget_approval_panel2" aria-labelledby="gadget_approval_tab2">
									완료함
								</div>
								<div hidden role="tabpanel" class="board_list" id="gadget_approval_panel3" aria-labelledby="gadget_approval_tab3">
									참조/회람
								</div>
							</div>
							<div class="gadget_control">
								<button type="button" class="delete"><span>삭제</span></button>
								<button type="button" class="move"><span>이동</span></button>
							</div>
							<div class="ui_contextmenu selector gadget_option" hidden>
								<button type="button" class="context_menu_item" data-action="edit"><span>편집</span></button>
								<button type="button" class="context_menu_item" data-action="hide"><span>컨텐츠 숨기기</span></button>
								<button type="button" class="context_menu_item" data-action="color"><span>위젯색상 변경</span></button>
								<button type="button" class="context_menu_item" data-action="setting"><span>설정</span></button>
							</div>
						</div>
					</div>
					<div class="gadget_save">
						<label class="ui_switch"><span>배경이미지 적용</span> <input type="checkbox" checked><i></i></label>
						<button type="button" class="ui_button lg" id="cancel_my_content_setting"><span>취소</span></button>
						<button type="button" class="ui_button lg primary"><span>변경 완료</span></button>
					</div>
				</div>
			</div>
		</div>
		<div class="portal_control">
			<div class="root up">
				<p><em>스크롤</em> 또는 <span>아래 버튼 <em>클릭</em>시</span> <strong>마이컨텐츠</strong>로 이동~!</p>
				<div class="control">
					<button type="button" class="move"><span>마이컨텐츠로 이동</span></button>
				</div>
			</div>
			<div class="root down">
				<div class="control">
					<button type="button" class="setting"><span>마이컨텐츠 설정</span></button>
					<button type="button" class="move up"><span>메인으로 이동</span></button>
				</div>
			</div>
			<div class="root only">
				<button type="button" class="setting"><span>마이컨텐츠 설정</span></button>
			</div>
		</div>
	</div>
	<div class="portal_widget" hidden>
		<button type="button" class="widget_toggle ui_icon_button">
			<i class="material-icons">navigate_before</i>
			<span>펼침</span>
		</button>
		<div class="widget_container">

		</div>
	</div>
</div>
<div id="wrap" style="display:none">
	<header id="header" class="clear">
		<tiles:insertAttribute name="header" />
	</header>
	<section id="comm_container">
		<aside class="favoritCont">
			<div class="faovriteListCont mScrollV">
				<ul id="quickContainer" class="favoriteList"></ul>			
			</div>
			<ul id="quickSetContainer" class="favorite_set clear"></ul>
		</aside>
		<section class="commContent">
			<div id="content">
			</div>
		</section>
	</section>
</div>
<script type="text/template" id="all_menu_drawer">
	<div class="drawer_header">
		<h2 class="drawer_title">전체메뉴</h2>
		<div class="drawer_action">
			<button type="button" class="ui_icon_button" id="all_menu_setting_toggle"><i
					class="material-icons-outlined">&#xe8b8;</i><span>설정</span>
			</button>
			<div class="all_menu_setting_layer ui_contextmenu" hidden>
				<div class="menu_head">
					<h3 class="title">전체메뉴 설정</h3>
					<button type="button" class="ui_icon_button close"><span>닫기</span></button>
				</div>
				<div class="menu_content">
					<ul>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>전자결재</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>메일</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>문서관리</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>웹하드</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>업무도우미</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>통합게시</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>커뮤니티</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>이지뷰</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>품질관리</span></label></li>
						<li><label class="ui_checkbox"><input type="checkbox"><i></i><span>협업스페이스</span></label></li>
					</ul>
				</div>
				<div class="menu_footer">
					<button type="button" class="ui_button normal"><i
							class="material-icons">&#xe5d5;</i><span>초기화</span></button>
					<button type="button" class="ui_button"><span>취소</span></button>
					<button type="button" class="ui_button primary"><span>완료</span></button>
				</div>
			</div>
		</div>
	</div>
	<div class="drawer_toolbar favorites_menu" aria-expanded="true">
		<div class="menu_toggle">
			<h3>즐겨찾는 메뉴</h3>
			<button type="button" class="ui_icon_button"><span>펼침/닫힘</span></button>
		</div>
		<ul class="menu_list">
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">주간보고</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">자유게시판</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">개인프로필</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">온라인 회의 예약</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">휴가관리</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">근태관리</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">타임시트</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">전자설문</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
			<li>
				<div class="ui_chip">
					<a href="#" class="chip_label">일정관리</a>
					<button type="button" class="chip_action action_delete ui_icon_button"><span>삭제</span></button>
				</div>
			</li>
		</ul>
	</div>
	<div class="all_menu_toolbar">
		<label class="ui_switch"><input type="checkbox" checked><i></i><span>메인화면 전체메뉴</span></label>
		<label class="ui_select">
			<select>
				<option>업무시스템 바로가기</option>
			</select>
		</label>
	</div>
	<hr class="drawer_divider">
	<div class="drawer_content">
		<nav class="all_menu_list">
			<ul>
				<li><a href="#">전자결재</a>
					<ul>
						<li><a href="#">e-Accounting</a></li>
						<li><a href="#">사업관리</a></li>
					</ul>
				</li>
				<li><a href="#">메일</a>
					<ul>
						<li><a href="#">SPAM Mail</a></li>
					</ul>
				</li>
				<li><a href="#">문서관리</a></li>
				<li><a href="#">웹하드</a></li>
				<li><a href="#">업무도우미</a>
					<ul>
						<li><a href="#">자원예약</a></li>
						<li><a href="#">전자설문</a></li>
						<li><a href="#">일정관리</a></li>
						<li><a href="#">인명관리</a></li>
						<li><a href="#">업무관리</a></li>
						<li><a href="#">온라인 회의 예약</a></li>
						<li><a href="#">타임시트</a></li>
					</ul>
				</li>
				<li><a href="#">통합게시</a></li>
				<li><a href="#">커뮤니티</a>
					<ul>
						<li><a href="#">휴가관리</a></li>
						<li><a href="#">근태관리(신)</a></li>
					</ul>
				</li>
				<li><a href="#">이지뷰</a></li>
				<li><a href="#">품질관리</a></li>
				<li><a href="#">협업스페이스</a></li>
			</ul>
		</nav>
	</div>
</script>
<script type="text/template" id="setting_drawer">
	<div class="drawer_header">
		<div class="setting_profile">
			<div class="ui_avatar">
				<img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타">
			</div>
			<strong>위하랑 과장</strong>
			<em>경영지원팀</em>
		</div>
	</div>
	<div class="drawer_toolbar">
		<div class="drawer_tabs ui_tabs" role="tablist">
			<button type="button" role="tab" class="tab" aria-selected="true" id="setting_drawer_tab"
					aria-controls="setting_drawer_tab_panel"><span>설정</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="design_setting_drawer_tab"
					aria-controls="design_setting_drawer_tab_panel"><span>디자인 변경</span></button>
		</div>
	</div>
	<div role="tabpanel" class="drawer_content setting_drawer" id="setting_drawer_tab_panel"
		 aria-labelledby="setting_drawer_tab">
		<button type="button" class="setting_menu sm right"><i
				class="material-icons-outlined">&#xe86a;</i><span>캐쉬초기화</span></button>
		<ul>
			<li>
				<a href="#" class="setting_menu">
					<i class="material-icons-outlined">&#xea67;</i>
					<span>나의 정보</span>
				</a>
			</li>
			<li>
				<a href="#" class="setting_menu">
					<i class="material-icons-outlined">&#xf02e;</i>
					<span>개인 정보 설정</span>
				</a>
			</li>
			<li>
				<a href="#" class="setting_menu menu_toggle">
					<i class="material-icons-outlined">&#xe894;</i>
					<span>한국어</span>
				</a>
				<ul class="ul_row">
					<li>
						<label class="ui_radio"><input type="radio" name="language" checked><i></i><span>한국어</span></label>
					</li>
					<li>
						<label class="ui_radio"><input type="radio" name="language"><i></i><span>English</span></label>
					</li>
					<li>
						<label class="ui_radio"><input type="radio" name="language"><i></i><span>日本語</span></label>
					</li>
					<li>
						<label class="ui_radio"><input type="radio" name="language"><i></i><span>中国語</span></label>
					</li>
					<li>
						<label class="ui_radio"><input type="radio" name="language"><i></i><span>추가언어1</span></label>
					</li>
					<li>
						<label class="ui_radio"><input type="radio" name="language"><i></i><span>추가언어2</span></label>
					</li>
				</ul>
			</li>
			<li>
				<a href="#" class="setting_menu menu_toggle">
					<i class="material-icons-outlined">&#xe2e7;</i>
					<span>겸직변경</span>
				</a>
				<ul class="ul_column">
					<li>
						<label class="ui_radio">
							<input type="radio" name="position" checked><i></i><span>솔루션영업팀(코비젼)</span>
						</label>
					</li>
					<li>
						<label class="ui_radio">
							<input type="radio" name="position"><i></i><span>재무팀(코비건설)</span>
						</label>
					</li>
					<li>
						<label class="ui_radio">
							<input type="radio" name="position"><i></i><span>감사팀(KBSN)</span>
						</label>
					</li>
				</ul>
			</li>
			<li>
				<a href="#" class="setting_menu">
					<i class="material-icons-outlined">&#xef3d;</i>
					<span>관리자 사이트</span>
				</a>
			</li>
			<li>
				<a href="#" class="setting_menu">
					<i class="material-icons-outlined">&#xe8fd;</i>
					<span>도움말</span>
				</a>
			</li>
		</ul>
		<button type="button" class="setting_menu fixed"><i
				class="material-icons-outlined">&#xe9ba;</i><span>로그아웃</span></button>
	</div>
	<div role="tabpanel" hidden class="drawer_content design_setting_drawer" id="design_setting_drawer_tab_panel"
		 aria-labelledby="design_setting_drawer_tab">
		<div class="setting_grid">
			<div class="grid_row">
				<label class="ui_radio"><input type="radio" name="mode" checked><i></i><span>라이트 모드</span></label>
				<label class="ui_radio"><input type="radio" name="mode"><i></i><span>다크 모드</span></label>
			</div>
		</div>
		<div class="setting_grid">
			<div class="grid_toggle">
				<h3>레이아웃</h3>
				<button type="button"><span>펼침/닫힘</span></button>
			</div>
			<div class="grid_layout_list">
				<label class="layout_selector vertical"><input type="radio" name="direction" checked><i></i><span>상하 이동형</span></label>
				<label class="layout_selector horizontal"><input type="radio" name="direction"><i></i><span>좌우 이동형</span></label></div>
			<div class="grid_switch">
				<span class="switch_label">마이컨텐츠 화면만 보기</span>
				<label class="ui_switch"><input type="checkbox"><i></i></label>
			</div>
		</div>
		<div class="setting_grid">
			<div class="grid_toggle">
				<h3>포탈 테마 변경</h3>
				<button type="button"><span>펼침/닫힘</span></button>
			</div>
			<div class="grid_switch">
				<span class="switch_label">배경위 검정 필터</span>
				<label class="ui_switch"><input type="checkbox" checked><i></i></label>
			</div>
			<h4 class="grid_sub_title">이미지 배경</h4>
			<div class="grid_selector_list image">
				<label class="image_upload"><input type="file"><span>기기에서 업로드</span></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_01.jpg" alt="배경이미지"></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_02.jpg" alt="배경이미지"></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_03.jpg" alt="배경이미지"></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_04.jpg" alt="배경이미지"></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_05.jpg" alt="배경이미지"></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_06.jpg" alt="배경이미지"></label>
				<label class="selector"><input type="radio" name="background"><i></i><img src="<%=cssPath2%>/customizing/ptype07/images/project/bg_thumbnail_07.jpg" alt="배경이미지"></label>
			</div>
			<h4 class="grid_sub_title">단색 컬러 배경</h4>
			<div class="grid_selector_list color">
				<label class="selector" style="background-color:#744DA9;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#8E8CD8;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#5E6ED3;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#3565B7;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#2D7D9A;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#587F62;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#A68559;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#9D9994;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#6E6E75;"><input type="radio" name="background"><i></i></label>
				<label class="selector" style="background-color:#555567;"><input type="radio" name="background"><i></i></label>
			</div>
			<div class="grid_switch">
				<span class="switch_label">마이컨텐츠 화면에 배경 적용</span>
				<label class="ui_switch"><input type="checkbox" checked><i></i></label>
			</div>
		</div>
		<div class="setting_grid">
			<div class="grid_toggle">
				<h3>서브 테마 변경</h3>
				<button type="button"><span>펼침/닫힘</span></button>
			</div>
			<div class="grid_selector_list sub">
				<label class="selector" style="background-color:#5141C2;"><input type="radio" name="theme" checked><i></i></label>
				<label class="selector" style="background-color:#0066CC;"><input type="radio" name="theme"><i></i></label>
				<label class="selector" style="background-color:#0BA1B9;"><input type="radio" name="theme"><i></i></label>
				<label class="selector" style="background-color:#F25B65;"><input type="radio" name="theme"><i></i></label>
				<label class="selector" style="background-color:#3C3B3B;"><input type="radio" name="theme"><i></i></label>
			</div>
		</div>
		<div class="setting_grid">
			<div class="grid_toggle">
				<h3>포탈 텍스트 컬러</h3>
				<button type="button"><span>펼침/닫힘</span></button>
			</div>
			<div class="grid_row bg">
				<label class="ui_radio"><input type="radio" checked><i></i><span>White</span></label>
				<label class="ui_radio"><input type="radio"><i></i><span>Black</span></label>
			</div>
		</div>
		<div class="setting_grid">
			<div class="grid_toggle">
				<h3>포탈변경</h3>
				<button type="button"><span>펼침/닫힘</span></button>
			</div>
			<div class="grid_check_list">
				<label class="ui_radio"><input type="radio" name="portal_type"
											   checked><i></i><span>코비젼 웹포탈</span></label>
				<label class="ui_radio"><input type="radio" name="portal_type"><i></i><span>포탈템플릿-01</span></label>
				<label class="ui_radio"><input type="radio" name="portal_type"><i></i><span>포탈템플릿-02</span></label>
				<label class="ui_radio"><input type="radio" name="portal_type"><i></i><span>포탈템플릿-03</span></label>
				<label class="ui_radio"><input type="radio" name="portal_type"><i></i><span>포탈 개선</span></label>
				<label class="ui_radio"><input type="radio"
											   name="portal_type"><i></i><span>코비젼 그룹(데모용)</span></label>
			</div>
		</div>
		<div class="setting_grid">
			<div class="grid_toggle">
				<h3>위젯 모드</h3>
				<button type="button"><span>펼침/닫힘</span></button>
			</div>
			<div class="grid_check_list">
				<label class="ui_checkbox"><input type="checkbox" disabled checked><i></i><span>전체공지</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>받은 메일</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>할일</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>임직원 즐겨찾기</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>날씨</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>환율게시</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>임직원 소식</span></label>
				<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>최근 사용 양식</span></label></div>
		</div>
	</div>
</script>
<script type="text/template" id="easy_writing_drawer">
	<div class="drawer_header">
		<h2 class="drawer_title"><i class="material-icons-outlined">&#xe3c9;</i>간편작성</h2>
	</div>
	<div class="drawer_toolbar">
		<div class="drawer_tabs ui_tabs" role="tablist">
			<button type="button" role="tab" class="tab" aria-selected="true" id="easy_mail_tab"
					aria-controls="easy_mail_tab_panel"><span>메일</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="easy_approval_tab"
					aria-controls="easy_approval_tab_panel"><span>결재</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="easy_schedule_tab"
					aria-controls="easy_schedule_tab_panel"><span>일정</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="easy_reservation_tab"
					aria-controls="easy_reservation_tab_panel"><span>예약</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="easy_notice_tab"
					aria-controls="easy_notice_tab_panel"><span>게시</span></button>
		</div>
	</div>
	<div role="tabpanel" class="drawer_content setting_drawer" id="easy_mail_tab_panel" aria-labelledby="easy_mail_tab">
		메일
	</div>
	<div role="tabpanel" hidden class="drawer_content setting_drawer" id="easy_approval_tab_panel"
		 aria-labelledby="easy_approval_tab">
		결재
	</div>
	<div role="tabpanel" hidden class="drawer_content setting_drawer" id="easy_schedule_tab_panel"
		 aria-labelledby="easy_schedule_tab">
		일정
	</div>
	<div role="tabpanel" hidden class="drawer_content setting_drawer" id="easy_reservation_tab_panel"
		 aria-labelledby="easy_reservation_tab">
		예약
	</div>
	<div role="tabpanel" hidden class="drawer_content setting_drawer" id="easy_notice_tab_panel"
		 aria-labelledby="easy_notice_tab">
		게시
	</div>
</script>
<script type="text/template" id="gadget_drawer">
	<div class="drawer_header">
		<h2 class="drawer_title"><i class="material-icons-outlined">&#xe86d;</i>가젯</h2>
	</div>
	<div class="drawer_toolbar">
		<div class="drawer_tabs ui_tabs" role="tablist">
			<button type="button" role="tab" class="tab" aria-selected="true" id="gadget_todo_tab"
					aria-controls="gadget_todo_tab_panel"><i
					class="material-icons-outlined">&#xe6b3;</i><span>To Do</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="gadget_subscribe_tab"
					aria-controls="gadget_subscribe_tab_panel"><i
					class="material-icons-outlined">&#xe9b7;</i><span>구독</span></button>
			<button type="button" role="tab" class="tab" aria-selected="false" id="gadget_contact_tab"
					aria-controls="gadget_contact_tab_panel"><i
					class="material-icons-outlined">&#xe0cf;</i><span>연락처</span></button>
		</div>
	</div>
	<div role="tabpanel" class="drawer_content setting_drawer" id="gadget_todo_tab_panel"
		 aria-labelledby="gadget_todo_tab">
		<div class="drawer_todo">
			<div class="content_toolbar">
				<button type="button" class="ui_icon_button"><i
						class="material-icons-outlined">&#xe3c9;</i><span>쓰기</span></button>
				<button type="button" class="ui_icon_button"><i
						class="material-icons-outlined">&#xe5d5;</i><span>새로고침</span></button>
				<button type="button" class="ui_icon_button"><i
						class="material-icons-outlined">&#xe8b8;</i><span>설정</span></button>
			</div>
			<div class="todo_container">
				<div class="todo_action">
					<label class="checkbox"><input type="checkbox"><i></i><span>전체 완료</span></label>
				</div>
				<div class="todo_list">
					<ul>
						<li>
							<label class="todo_item checkbox">
								<input type="checkbox" checked><i></i>
								<span>CP 그룹웨어 디자인 회의</span>
								<time>2022-07-21</time>
							</label>
						</li>
						<li>
							<label class="todo_item checkbox">
								<input type="checkbox"><i></i>
								<span>CP 그룹웨어 디자인 XD파일 레이어 정리</span>
								<time>2022-07-21</time>
							</label>
						</li>
						<li>
							<label class="todo_item checkbox">
								<input type="checkbox"><i></i>
								<span>웹파트 UX개선 벤치마킹</span>
								<time>2022-07-21</time>
							</label>
						</li>
						<li>
							<label class="todo_item checkbox">
								<input type="checkbox" checked><i></i>
								<span>메인 배경이미지 3종 디자인</span>
								<time>2022-07-21</time>
							</label>
						</li>
						<li>
							<label class="todo_item checkbox">
								<input type="checkbox" checked><i></i>
								<span>아이콘 디자인</span>
								<time>2022-07-21</time>
							</label>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div role="tabpanel" hidden class="drawer_content setting_drawer" id="gadget_subscribe_tab_panel"
		 aria-labelledby="gadget_subscribe_tab">
		<div class="drawer_subscribe">
			<div class="content_toolbar">
				<button type="button" class="ui_icon_button"><i
						class="material-icons-outlined">&#xe5d5;</i><span>새로고침</span></button>
				<button type="button" class="ui_icon_button"><i
						class="material-icons-outlined">&#xe8b8;</i><span>설정</span></button>
			</div>
			<div class="subscribe_list">
				<ul>
					<li>
						<div class="subscribe_item">
							<div class="user">
								<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
								<strong>이정은 차장</strong>
								<em class="department">(PM팀)</em>
							</div>
							<h3 class="title">[공지] 2022년 공단 건강검진 실시 안내</h3>
							<time class="date">2021-07-14 15:23</time>
							<div class="meta">
								<dl class="like">
									<dt><span>좋아요</span></dt>
									<dd>7</dd>
								</dl>
								<dl class="comment">
									<dt><span>댓글</span></dt>
									<dd>2</dd>
								</dl>
								<dl class="view">
									<dt><span>조회수</span></dt>
									<dd>8</dd>
								</dl>
							</div>
							<a href="#"><span>상세보기</span></a>
						</div>
					</li>
					<li>
						<div class="subscribe_item">
							<div class="user">
								<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
								<strong>이정은 차장</strong>
								<em class="department">(PM팀)</em>
							</div>
							<h3 class="title">[공지] 2022년 공단 건강검진 실시 안내</h3>
							<time class="date">2021-07-14 15:23</time>
							<div class="meta">
								<dl class="like">
									<dt><span>좋아요</span></dt>
									<dd>7</dd>
								</dl>
								<dl class="comment">
									<dt><span>댓글</span></dt>
									<dd>2</dd>
								</dl>
								<dl class="view">
									<dt><span>조회수</span></dt>
									<dd>8</dd>
								</dl>
							</div>
							<a href="#"><span>상세보기</span></a>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</div>
	<div role="tabpanel" hidden class="drawer_content setting_drawer" id="gadget_contact_tab_panel"
		 aria-labelledby="gadget_contact_tab">
		<div class="drawer_contact">
			<ul>
				<li>
					<div class="contact_item">
						<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
						<div class="info">
							<div class="title"><span class="name">이정은</span> <em class="position">(팀원/차장)</em></div>
							<span class="department">연구1팀</span>
							<div class="meta">
								<dl>
									<dt class="phone"><span>휴대폰</span></dt>
									<dd>010-1234-5678</dd>
								</dl>
								<dl>
									<dt class="tel"><span>회사전화</span></dt>
									<dd>02-1234-5678</dd>
								</dl>
								<dl>
									<dt class="email"><span>이메일</span></dt>
									<dd>good123@covision.co.kr</dd>
								</dl>
							</div>
						</div>
						<a href="#"><span>상세보기</span></a>
					</div>
				</li>
				<li>
					<div class="contact_item">
						<div class="ui_avatar avatar"><img src="<%=cssPath2%>/customizing/ptype07/images/project/avatar.jpg" alt="아바타"></div>
						<div class="info">
							<div class="title"><span class="name">이정은</span> <em class="position">(팀원/차장)</em></div>
							<span class="department">연구1팀</span>
							<div class="meta">
								<dl>
									<dt class="phone"><span>휴대폰</span></dt>
									<dd>010-1234-5678</dd>
								</dl>
								<dl>
									<dt class="tel"><span>회사전화</span></dt>
									<dd>02-1234-5678</dd>
								</dl>
								<dl>
									<dt class="email"><span>이메일</span></dt>
									<dd>good123@covision.co.kr</dd>
								</dl>
							</div>
						</div>
						<a href="#"><span>상세보기</span></a>
					</div>
				</li>
			</ul>
		</div>
	</div>
</script>
<script type="text/template" id="alert_drawer">
	<div class="drawer_header">
		<h2 class="drawer_title"><i class="material-icons-outlined">&#xe7f4;</i>알림</h2>
	</div>
	<div class="drawer_content">
		<div class="drawer_post_list">
			<ul>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청 [전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
				<li>
					<a href="#" class="post_item">
						<strong>[전자결재-결재요청] 8월 일반 경비신청</strong>
						<span>슈퍼관리자</span>
						<time>2022-09-08 10:04:25</time>
					</a>
				</li>
			</ul>
		</div>
	</div>
</script>
<script type="text/template" id="mention_drawer">
	<div class="drawer_header">
		<h2 class="drawer_title"><i class="material-icons-outlined">&#xe0e6;</i>멘션</h2>
	</div>
	<div class="drawer_content">
		<div class="drawer_post_empty">내용이 없습니다.</div>
	</div>
</script>
<script type="text/template" id="my_content_setting_drawer">
	<div class="drawer_content">
		<div class="setting_list">
			<ul>
				<li>
					<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>개인정보</span></label>
				</li>
				<li>
					<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>전체공지</span></label>
				</li>
				<li>
					<label class="ui_checkbox"><input type="checkbox" checked><i></i><span>임직원 즐겨찾기</span></label>
				</li>
			</ul>
		</div>
	</div>
</script>	

<jsp:include page="/WEB-INF/views/cmmn/UserGuide.jsp"></jsp:include> 
</body>
</html>