/**
 * pnSurveyList - [포탈개선] My Place - 진행중인 설문
 */
var pnSurveyList = {
	webpartType: "", 
	init: function(data, ext){
		pnSurveyList.setEvent();
		pnSurveyList.getSurveyList();
	},
	setEvent: function(){
		$(".PN_survey").closest(".PN_myContents_box").find(".PN_portlet_btn").off("click").on("click", function(){
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

		$(".PN_survey").closest(".PN_myContents_box").find(".PN_portlet_close").click(function(){
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn").removeClass("active");
			$(this).parents(".PN_portlet_menu").stop().slideUp(300);
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn > span").removeClass("on");
		});
		
		$(".PN_survey").closest(".PN_myContents_box").find(".PN_portlet_menu li[mode=write]").off("click").on("click", function(){
			window.open("/groupware/layout/survey_SurveyWrite.do?CLSYS=survey&CLMD=user&CLBIZ=Survey&reqType=create&surveyId=&communityId=0");
		});
	},
	getSurveyList: function() {
		$.ajax({
			url: "/groupware/survey/getSurveyList.do",
			type: "POST",
			data: {
				pageNo: 1,
				pageSize: 1,
				reqType: "proceed",
				schContentType: "subject",
				schMySel: "written",
				notReadFg: "N",
				schTxt: "",
				simpleSchTxt: "",
				communityId: 0,
				startDate: "",
				endDate: ""
			},
			success: function(data){
				if(data.status == "SUCCESS"){
					var surveyList = data.list.filter(function(data){
						return data.joinFg == "N";
					}, []);
					
					if(surveyList && surveyList.length != 0){
						var surveyInfo = surveyList[0];
						var divWrap = $("<div class='PN_surveyCont'></div>");
						var percent = Math.floor(Number(surveyInfo.joinRate)) + "%";
						var surveyWriteUrl = "/groupware/layout/survey_Survey.do?CLSYS=survey&CLMD=user&CLBIZ=Survey&reqType=join&listType=proceed&surveyId=" + surveyInfo.SurveyID + "&communityId=0";
						var joinStr = "";
						
						if(surveyInfo.joinFg == "Y"){
							joinStr = "참여";
						}else{
							joinStr = "미참여";
						}
						
						divWrap.append($("<span class='sPart'></span>").text(joinStr))
								.append($("<span class='sTitle'></span>").text(surveyInfo.Subject))
								.append($("<span class='sPeriod'></span>").text(surveyInfo.SurveyStartDate + " ~ " + surveyInfo.SurveyEndDate))
								.append($("<div class='PN_graphBox_sv'></div>")
									.append($("<span class='PN_graphPer_sv'></span>").text(percent + " <spring:message code='Cache.lbl_survey_participation'/>")) // 참여
									.append($("<div class='PN_graph_sv'></div>")
										.append($("<span class='PN_graphBar_sv'></span>")
											.css("width", percent))));
						
						$(".PN_survey").append(divWrap)
							.append($("<a class='PN_btnLink'></a>")
								.attr("href", surveyWriteUrl)
								.append($("<span></span>").text("<spring:message code='Cache.lbl_SurveyParticipant'/>"))); // 설문참여
					}else{
						$(".PN_survey").closest(".PN_portlet_contents").find(".PN_nolist").show();
						$(".PN_survey").hide();
					}
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/survey/getSurveyList.do", response, status, error);
			}
		});
	}
}