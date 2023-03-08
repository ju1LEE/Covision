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
	<div id="wrap">
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
				<div id="left" class="commContLeft">
					<tiles:insertAttribute name="left" /> 
				</div>
				<div id="contents" class="commContRight">
					<div id="tab">
						<tiles:insertAttribute name="tab" />
					</div>
					<div id="content">
						<tiles:insertAttribute name="content" />
					</div>			
				</div>
				<%if(RedisDataUtil.getBaseConfig("useWorkPortal").equals("Y")){ %>
					<section id="work_portal" class="mainContent work_pop clearFloat" style="right: -100%;"></section>
				
					 <!-- [s]업무형 포탈 여닫기버튼 -->
		            <div class="btn_work close"> <a href="#"></a> <span class="toolTip2">기본포탈</span> </div>
		            <div class="btn_work open"> <a href="#"></a> <span class="toolTip1">업무포탈</span> </div>
				    <!-- [e]업무형 포탈 여닫기버튼 -->
				<%} %>		
			</section>
			
			<aside class="PN_simplePop" style="display: none;">
				<ul class="PN_popMode" style="display: none;">
					<li class="PN_btnLite" style="display: none;"><a href="#"><span class="toolTip2">라이트모드로 보기</span></a></li>
					<li class="PN_btnDark" style="display: block;"><a href="#"><span class="toolTip2">다크모드로 보기</span></a></li>
				</ul>
				<ul class="PN_popBtn">
					<li class="PN_btnMore active"><a href="#">더보기</a><span class="toolTip2">더보기</span></li>
					<li class="PN_btnClose"><a href="#">닫기</a><span class="toolTip2">닫기</span></li>
				</ul>
				<ul class="PN_popBtn2">
					<li class="PN_btnSimple"><a href="#" onclick="coviCtrl.toggleSimpleMake();">간편작성</a><span class="toolTip2">간편작성</span></li>
					<li class="PN_btnUntact"><a href="https://eum-meet.covision.co.kr/" target="_blank">화상회의</a><span class="toolTip2">화상회의</span></li>
				</ul>
			</aside>
			
			<aside class="simpleMakeLayerPopUp">
				<jsp:include page="/WEB-INF/views/cmmn/SimpleMake.jsp"></jsp:include>
			</aside>
						
			<!-- <aside id="secretaryContainer" class="secretary_cont new off"></aside> -->
		</section>
	</div>
</body>
</html>