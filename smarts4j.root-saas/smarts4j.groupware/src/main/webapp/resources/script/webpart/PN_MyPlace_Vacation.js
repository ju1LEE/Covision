/**
 * pnVacation - [포탈개선] My Place - 내 휴가현황
 */
var pnVacation = {
	webpartType: "", 
	init: function(data, ext){
		var nowDateStr = schedule_SetDateFormat(new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd")), '.');
		var nowYear = nowDateStr.substr(0, 4);
		
		pnVacation.setEvent();
		pnVacation.getVacationList(nowYear);
		pnVacation.getUserRewardVacDay(nowYear);
	},
	setEvent: function(){
		$(".PN_vacation").closest(".PN_myContents_box").find(".PN_portlet_btn").off("click").on("click", function(){
			if(!$(this).hasClass("active")){
				$(this).addClass("active");
				$(this).next(".PN_portlet_menu").stop().slideDown(300);
				$(this).children(".PN_portlet_btn > span").addClass("on");
			}else{
				$(this).removeClass("active");
				$(this).next(".PN_portlet_menu").stop().slideUp(300);
				$(this).children(".PN_portlet_btn > span").removeClass("on");
			}
		});
		
		$(".PN_vacation").closest(".PN_myContents_box").find(".PN_portlet_close").click(function(){
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn").removeClass("active");
			$(this).parents(".PN_portlet_menu").stop().slideUp(300);
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn > span").removeClass("on");
		});
		
		$(".PN_vacation .PN_btnLink").off("click").on("click", function(){
			pnVacation.openVacationPopup();
		});
		
		$(".PN_vacation").closest(".PN_myContents_box").find(".PN_portlet_menu li[mode=write]").off("click").on("click", function(){
			CFN_OpenWindow("/approval/approval_Form.do?formPrefix=WF_FORM_VACATION_REQUEST2&mode=DRAFT", "", 790, (window.screen.height - 100), "resize");
		});
	},
	getVacationList: function(pYear) {
		$.ajax({
			url: "/groupware/vacation/getVacationInfoForHome.do",
			type: "POST",
			data: {
				"year": pYear
			},
			success: function(data){
				if(data.status == "SUCCESS"){
					var vacationInfo = data.list[0];
					
					$(".PN_vacationCont .vTotal span").text("<spring:message code='Cache.lbl_TotalHolidayDay'/> : " + vacationInfo.VacDay); // 총 휴가일
					$(".PN_vacationCont li[mode=use] .vNum").text(vacationInfo.VacDayUse);
					$(".PN_vacationCont li[mode=remind] .vNum").text(vacationInfo.RemainVacDay);
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/vacation/getVacationInfoForHome.do", response, status, error);
			}
		});
	},
	getUserRewardVacDay: function(pYear){
		$.ajax({
			url: "/groupware/pnPortal/selectUserRewardVacDay.do",
			type: "POST",
			data: {
				userCode: sessionObj["USERID"],
				year: pYear
			},
			success: function(data){
				var rVacDay = data.rewardVacDay;
				$(".PN_vacationCont li[mode=reward] .vNum").text(rVacDay);
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/pnPortal/selectUserRewardVacDay.do", response, status, error);
			}
		});
	},
	openVacationPopup: function(){
		CFN_OpenWindow("/approval/approval_Form.do?formPrefix=WF_FORM_VACATION_REQUEST2&mode=DRAFT", "", "790", (window.screen.height - 100), "resize");
	}
}