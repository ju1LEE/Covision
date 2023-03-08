<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil" %>

<!-- 사용자가이드 -->
<div class="HUG_background" id="guide_wrap" style="display:none;">
	<!-- 사용자가이드_01_메인 -->
	<div class="HUG_MainLayerpopup_wrap hug_div_on" id="guide_1">
		<div class="HUG_MainLayerpopup">
			<div class="topimg">
				<div class="centerimg"></div>
			</div>
			<a href="#" class="close" onclick="closeGuide();"></a>
			<h1>환영합니다!</h1>
			<p class="sub_txt">그룹웨어에 어떤 기능들이 있는지</p><p class="sub_txt_color">지금부터 간단히 살펴볼까요?</p>
		
			<a href="#" class="startbtn" onclick="goPrevNextPage('next');">시작하기</a>
		</div>
		<a href="#" class="MainLayerpop_bottom_txtbtn" onclick="doNotOpenGuide();">가이드 페이지를 보지 않겠습니다.</a>
	</div>
	
	<!-- 사용자가이드_02 -->
	<div class="HUG_box_wrap" style="display:none;" id="guide_2">
		<div class="box_Left box_on">
			<div class="box_txt_wrap">
				<p class="box_txt_title">Top-Down Notice</p>
				<p class="box_txt_sub">공지사항, 경조사, 임직원 소식 등 <span>사내 주요 소식</span> 및 <br>업무시스템으로 이동할 수 있는 메뉴가 있습니다. </p>
			</div>
		</div>
		<div class="box_Right">
			<div class="box_txt_wrap">
				<p class="box_txt_title">My Work Area</p>
				<p class="box_txt_sub"><span>컨텐츠를 드래그 앤 드롭하여<br><span>개인별로 자유롭게 </span>배치할 수 있습니다.</p>
			</div>
		</div>
	</div>
	
	<!-- 사용자가이드_03 -->
	<div class="HUG_box_wrap" style="display:none;" id="guide_3">
		<div class="box_Left">
			<div class="box_txt_wrap">
				<p class="box_txt_title">Top-Down Notice</p>
				<p class="box_txt_sub">공지사항, 경조사, 임직원 소식 등 <span>사내 주요 소식</span> 및 <br>업무시스템으로 이동할 수 있는 메뉴가 있습니다. </p>
			</div>
		</div>
		<div class="box_Right box_on">
			<div class="box_txt_wrap">
				<p class="box_txt_title">My Work Area</p>
				<p class="box_txt_sub">컨텐츠를 드래그 앤 드롭하여<br><span>개인별로 자유롭게 </span>배치할 수 있습니다.</p>
			</div>
		</div>
	</div>
	
	<!-- 사용자가이드_04 -->
	<div class="HUG_guide_wrap" style="display:none;" id="guide_4">
		<div class="guidepage_04"></div>
		<div class="guidepage_txt guidepage_txt_04_01">
			<p class="guidepage_txt_01">전체메뉴</p>
			<p class="guidepage_txt_02">전체메뉴를 한 눈에 볼 수 있습니다.</p>
		</div>
		<div class="guidepage_txt guidepage_txt_04_02">
			<p class="guidepage_txt_01">메인메뉴설정</p>
			<p class="guidepage_txt_02">상단 메뉴 노출 설정을 할 수 있습니다.</p>
		</div>
		<div class="guidepage_txt guidepage_txt_04_03">
			<p class="guidepage_txt_01">즐겨찾는 메뉴</p>
			<p class="guidepage_txt_02">즐겨 찾는 페이지로 바로가기 할 수 있습니다.</p>
		</div>
	</div>
	
	<!-- 사용자가이드_05 -->
	<div class="HUG_guide_wrap" style="display:none;" id="guide_5">
		<div class="guidepage_05"></div>
		<div class="guidepage_txt guidepage_txt_05_01">
			<p class="guidepage_txt_01">자주쓰는 메뉴</p>
			<p class="guidepage_txt_02">자주가는 컨텐츠에 업데이트 확인과<br>편리하게 이동 할 수 있습니다.</p>
		</div>
	</div>
	
	<!-- 사용자가이드_06 -->
	<div class="HUG_guide_wrap" style="display:none;" id="guide_6">
		<div class="guidepage_06"></div>
		<div class="guidepage_txt guidepage_txt_06_01">
			<p class="guidepage_txt_01">자주쓰는 메뉴 즐겨찾기 설정</p>
			<p class="guidepage_txt_02">나의 업무특성에 맞춰 자주쓰는 메뉴에<br>노출설정과 드래그하여 순서를 변경 할 수 있습니다.</p>
		</div>
	</div>
	
	<!-- 사용자가이드_07 -->
	<div class="HUG_guide_wrap" style="display:none;" id="guide_7">
		<div class="guidepage_07"></div>
		<div class="guidepage_txt guidepage_txt_07_01">
			<p class="guidepage_txt_01">조직도</p>
			<p class="guidepage_txt_02">임직원 현황을<br>손쉽게 확인 할 수 있습니다.</p>
		</div>
	</div>

	<!-- 사용자가이드_08 -->
	<div class="HUG_guide_wrap" style="display:none;" id="guide_8">
		<div class="guidepage_08"></div>
		<div class="guidepage_txt guidepage_txt_08_01">
			<p class="guidepage_txt_01">간편작성</p>
			<p class="guidepage_txt_02">메일, 결재, 일정, 예약, 게시를 <br>간편하게 작성 할 수 있습니다.</p>
		</div>
	</div>
	
	<!-- 사용자가이드_09 -->
	<div class="HUG_guide_wrap" style="display:none;" id="guide_9">
		<div class="guidepage_10"></div>
		<div class="guidepage_txt guidepage_txt_10_01">
			<p class="guidepage_txt_10">My menu</p>
			<p class="guidepage_txt_10">언어선택, 알림 설정, 겸직변경,<br>테마변경을  할 수 있습니다.</p>
		</div>
	</div>
		
	<!-- 하단 컨트롤 -->
	<div class="HUG_Control" style="display:none;" id="guide_control">
		<div class="roll_btn">
			<a class="roll_btn_on" href="#" id="btn_2" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_3" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_4" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_5" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_6" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_7" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_8" onclick="goSelectPage(this);"></a>
			<a class="roll_btn_off" href="#"id="btn_9" onclick="goSelectPage(this);"></a>
		</div>
		<div class="pagebtn">
			<a href="#" class="prevbtn" onclick="goPrevNextPage('prev');">&lt;이전</a>
			<a href="#" class="nextbtn" onclick="goPrevNextPage('next');" id="btnNext">다음&gt;</a>
			<a href="#" class="nextbtn" onclick="closeGuide()" style="display:none;" id="btnClose">마침</a>
		</div>
		<div class="guideclosebtn">
			<div class="chkStyle02"><input type="checkbox" id="hug_con"><label for="hug_con"><span></span>다시보지않기</label></div>
			<a href="#" class="guideclose" onclick="closeGuide();"></a>
		</div>
	</div>
			
	<script type="text/javascript">
	
		if(Common.getBaseConfig("UseWebGuidePopup") == "Y") init();
		
		function init() {
			if (coviCmn.getCookie("USER_GUIDE_" + Common.getSession("LogonID")) == "") {
				$("#guide_wrap").show();
				
				//팝업 공지랑 겹쳐서 표시될 경우 팝업공지 숨김처리
				$(".layer_divpop[id^='boardViewPop']").hide();
			}
		}
		
		function goPrevNextPage(type) {
			var page = $("#guide_wrap").find(".hug_div_on").attr("id").split("_")[1];

			if(type == "prev") {
				page--;
			} else if(type = "next") {
				page++;
			}
			
			if(page >=2 & page <= 9) {
				$("#guide_wrap").find(".hug_div_on").hide();
				$("#guide_wrap").find(".hug_div_on").removeClass("hug_div_on");
				$("#guide_" + page).show();
				$("#guide_" + page).addClass("hug_div_on");
				
				$("#guide_control").find(".roll_btn_on").removeClass("roll_btn_on").addClass("roll_btn_off");
				$("#btn_" + page).removeClass("roll_btn_off").addClass("roll_btn_on");
				$("#guide_control").show();
			}
			
			if(page == 9) {
				$("#btnNext").hide();
				$("#btnClose").show();
			} else {
				$("#btnNext").show();
				$("#btnClose").hide();
			}
		}
		
		function goSelectPage(obj) {
			var page = parseInt($(obj).attr("id").split("_")[1]);
			
			$("#guide_wrap").find(".hug_div_on").hide().removeClass("hug_div_on");
			$("#guide_" + page).show();
			$("#guide_" + page).addClass("hug_div_on");
			
			$("#guide_control").find(".roll_btn_on").removeClass("roll_btn_on").addClass("roll_btn_off");
			$("#btn_" + page).removeClass("roll_btn_off").addClass("roll_btn_on");
			
			if(page == 9) {
				$("#btnNext").hide();
				$("#btnClose").show();
			} else {
				$("#btnNext").show();
				$("#btnClose").hide();
			}
		}
		
		function doNotOpenGuide() {
			coviCmn.setCookie("USER_GUIDE_" + Common.getSession("LogonID"), Common.getSession("LogonID"), 3650);
			$("#guide_wrap").hide();

			//가이드 닫을 경우 팝업 공지 표시
			$(".layer_divpop[id^='boardViewPop']").show();
		}
		
		function closeGuide() {
			if($("#hug_con").prop("checked")) {
				coviCmn.setCookie("USER_GUIDE_" + Common.getSession("LogonID"), Common.getSession("LogonID"), 3650);
			}
			
			$("#guide_wrap").hide();
			
			//가이드 닫을 경우 팝업 공지 표시
			$(".layer_divpop[id^='boardViewPop']").show();
		}
	</script>
</div>