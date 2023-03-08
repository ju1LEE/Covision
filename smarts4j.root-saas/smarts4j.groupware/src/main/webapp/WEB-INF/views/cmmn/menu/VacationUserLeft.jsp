<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="cLnbTop">
	<h2 onclick="goVactionHome();" style="cursor:pointer;"><spring:message code='Cache.MN_658' /></h2>
	<div>
		<a class="btnType01 btnSurveyAdd" href="#" onclick="AttendUtils.openVacationPopup('USER');"><spring:message code='Cache.MN_659' /></a>
	</div>	
</div>
<div class="cLnbMiddle mScrollV scrollVType01 mCustomScrollbar _mCS_2 mCS_no_scrollbar"><div id="mCSB_2" class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside" style="max-height: none;" tabindex="0"><div id="mCSB_2_container" class="mCSB_container mCS_y_hidden mCS_no_scrollbar_y" style="position: relative; top: 0px; left: 0px;" dir="ltr">
	<ul class="contLnbMenu extensionMenu" id="leftmenu"></ul>	
</div><div id="mCSB_2_scrollbar_vertical" class="mCSB_scrollTools mCSB_2_scrollbar mCS-light mCSB_scrollTools_vertical mCSB_scrollTools_onDrag" style="display: none;"><div class="mCSB_draggerContainer"><div id="mCSB_2_dragger_vertical" class="mCSB_dragger mCSB_dragger_onDrag" style="position: absolute; min-height: 0px; display: block; height: 0px; top: 0px;" oncontextmenu="return false;"><div class="mCSB_dragger_bar" style="line-height: 0px;"></div></div><div class="mCSB_draggerRail"></div></div></div></div></div>

<script type="text/javascript">
	//# sourceURL=VacationUserLeft.jsp
	var leftData = ${leftMenuData};
	var loadContent = '${loadContent}';
	
	initLeft();
	
	function initLeft(){
		
		var opt = {
    			lang : "ko",
    			isPartial : "true"
    	};
		var left = new CoviMenu(opt);
		left.render('#leftmenu', leftData, 'userLeft');
		
		if(loadContent == 'true'){
			CoviMenu_GetContent('/groupware/layout/vacation_Home.do?CLSYS=vacation&CLMD=user&CLBIZ=vacation');
		}
	}
	
	// 휴가 포탈
	function goVactionHome(){
		CoviMenu_GetContent('/groupware/layout/vacation_Home.do?CLSYS=vacation&CLMD=user&CLBIZ=vacation');
	}
	
	// 휴가 신청
	function openVacationApplyPopup() {
		CFN_OpenWindow("/approval/approval_Form.do?formPrefix=WF_FORM_VACATION_REQUEST2&mode=DRAFT", "", 790, (window.screen.height - 100), "resize");
	}	
	
	
	
	 $(".selOnOffBox").find('a').click(function () {	
		var cla= $(this).closest('li').find('.selOnOffBoxChk');
	
		var liname = $(this).parent().parent().attr("class");
	
		if(liname=="extensionMenu02"){
			if(cla.hasClass('active')){
				cla.removeClass("active");
				$(this).removeClass("active");
			}else{
				cla.addClass("active");
				$(this).addClass("active");
			}			
		}else if(liname=="extensionMenu03"){
			if(cla.hasClass('active')){
				cla.removeClass("active");
				$(this).removeClass("active");
			}else{
				cla.addClass("active");
				$(this).addClass("active");
			}
		}else if(liname=="extensionMenu04"){
			if(cla.hasClass('active')){
				cla.removeClass("active");
				$(this).removeClass("active");
			}else{
				cla.addClass("active");
				$(this).addClass("active");
			}
		}
		
	});  
</script>
