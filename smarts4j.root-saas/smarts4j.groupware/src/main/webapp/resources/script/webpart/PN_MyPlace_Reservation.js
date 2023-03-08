/**
 * pnReservation - [포탈개선] My Place - 회의실 예약
 */
var pnReservation = {
	webpartType: "",
	isOverlap: false,
	init: function (data, ext){
		pnReservation.setEvent();
		pnReservation.setDefaultTime();
		pnReservation.setResourceList();
	},
	setEvent: function(){
		$(".PN_reserve").closest(".PN_myContents_box").find(".PN_portlet_btn").off("click").on("click", function(){
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

		$(".PN_reserve").closest(".PN_myContents_box").find(".PN_portlet_close").click(function(){
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn").removeClass("active");
			$(this).parents(".PN_portlet_menu").stop().slideUp(300);
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn > span").removeClass("on");
		});

		$("#PN_rsDate").addClass("input_date").datepicker({
			dateFormat: "yy.mm.dd",
			showOn: "button",
			buttonText : "calendar",
			buttonImage: Common.getGlobalProperties("css.path") +  "/covicore/resources/images/theme/blue/app_calendar.png", 
			buttonImageOnly: true,
			dayNamesMin: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
		});
		
		$(".PN_rsTime select").on("change", function(){
			var sTime = Number($("#rsSTime").val());
			var eTime = Number($("#rsETime").val());
			
			if(sTime >= eTime){
				Common.Warning("<spring:message code='Cache.msg_Mobile_InvalidStartTime'/>"); // 시작시간이 종료시간보다 클 수 없습니다.
				return false;
			}
			
			pnReservation.setTimeTable();
			pnReservation.getReservationList($(".PN_rsPlace select option:selected").val(), $("#PN_rsDate").val().replaceAll(".", "-"));
		});
		
		$(".PN_reserve .PN_btnLink").off("click").on("click", function(){
			if(pnReservation.isOverlap){
				Common.Warning("<spring:message code='Cache.msg_ResourceManage_26' />"); // 자원을 예약할 수 없습니다. 동일한 예약기간에 이미 사용되고 있습니다.
				return false;
			}
			
			pnReservation.openSubjectPopup();
		});
		
		$(".PN_rsPlace select, #PN_rsDate").off("change").on("change", function(){
			pnReservation.getReservationList($(".PN_rsPlace select option:selected").val(), $("#PN_rsDate").val().replaceAll(".", "-"));
		});
	},
	setTimeTableEvent: function(){
		$(".PN_timeTable .PN_rsChk p input[type=checkbox]").off("click").on("click", function(e){
			var checkedList = $(".PN_timeTable .PN_rsChk p > input:checked");
			pnReservation.isOverlap = false;
				
			if(checkedList.length > 1){
				var sNum = Number($(checkedList[0]).attr("id").replace(/[^\-\.0-9]/g, ""));
				var eNum = Number($(checkedList[(checkedList.length - 1)]).attr("id").replace(/[^\-\.0-9]/g, ""));
				var clickNum = Number($(this).attr("id").replace(/[^\-\.0-9]/g, ""));
				
				$.each($(".PN_timeTable .PN_rsChk p > input"), function(idx, item){
					var thisNum = Number($(item).attr("id").replace(/[^\-\.0-9]/g, ""));
					
					if(clickNum > sNum && clickNum < eNum){
						if(thisNum >= sNum && thisNum < clickNum
							&& !$(item).prop("disabled")){
							$(item).prop("checked", true);
						}
						else $(item).prop("checked", false);
					}else if(clickNum == sNum){
						if(thisNum >= clickNum && thisNum <= eNum){
							if($(item).prop("disabled")) pnReservation.isOverlap = true;
							else $(item).prop("checked", true);
						}
						else $(item).prop("checked", false);
					}else if(clickNum == eNum){
						if(thisNum >= sNum && thisNum <= clickNum){
							if($(item).prop("disabled")) pnReservation.isOverlap = true;
							else $(item).prop("checked", true);
						}
						else $(item).prop("checked", false);
					}
				});
			}
		});
	},
	setDefaultTime: function(){
		Common.getBaseConfigList(["WorkStartTime","WorkEndTime"]);
		var nowDate = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd"));
		var nowDateStr = schedule_SetDateFormat(nowDate, ".")
		var workSTime = Number(coviCmn.configMap["WorkStartTime"]);
		var workETime = Number(coviCmn.configMap["WorkEndTime"]);
		var timeHtml = "";
		var optionHtml = "";
		
		for(var i = workSTime; i < workETime; i++){
			var hourStr = "";
			var timeStr = "";
			var liWrap = $("<li></li>");
			
			if(i < 10) hourStr = "0" + i;
			else hourStr = i;
			
			timeStr = hourStr + ":00";
			
			liWrap.append($("<div class='PN_rsTime2'></div>").text(hourStr))
			.append($("<div class='PN_rsChk'></div>")
				.append($("<p></p>")
					.append($("<input type='checkbox'>").attr("time", timeStr).attr("id", "rs"+i))
					.append($("<label></label>").attr("for", "rs"+i)
						.append($("<span class='s_check'></span>"))))
				.append($("<p></p>")
						.append($("<input type='checkbox'>").attr("time", hourStr + ":30").attr("id", "rs"+(i+0.5)))
						.append($("<label></label>").attr("for", "rs"+(i+0.5))
							.append($("<span class='s_check'></span>")))));
			
			timeHtml += $(liWrap)[0].outerHTML;
			optionHtml += $("<option></option>").attr("value", i).text(timeStr)[0].outerHTML;
		}
		
		$("#PN_rsDate").val(nowDateStr);
		$(".PN_timeTable ul").empty().append(timeHtml);
		$(".PN_rsTime select").empty().append(optionHtml);
		$(".PN_rsTime select").append($("<option></option>").text(workETime + ":00"));
		$("#rsSTime").find("option:first").prop("selected", true);
		$("#rsETime").find("option:last").prop("selected", true);
		
		pnReservation.setTimeTableEvent();
	},
	setTimeTable: function(){
		var sTime = Number($("#rsSTime").val().split(":")[0]);
		var eTime = Number($("#rsETime").val().split(":")[0]);
		var timeHtml = "";
		
		for(var i = sTime; i < eTime; i++){
			var hourStr = "";
			var liWrap = $("<li></li>");
			
			if(i < 10) hourStr = "0" + i;
			else hourStr = i;
			
			liWrap.append($("<div class='PN_rsTime2'></div>").text(hourStr))
			.append($("<div class='PN_rsChk'></div>")
				.append($("<p></p>")
					.append($("<input type='checkbox'>").attr("time", hourStr + ":00").attr("id", "rs"+i))
					.append($("<label></label>").attr("for", "rs"+i)
						.append($("<span class='s_check'></span>"))))
				.append($("<p></p>")
						.append($("<input type='checkbox'>").attr("time", hourStr + ":30").attr("id", "rs"+(i+0.5)))
						.append($("<label></label>").attr("for", "rs"+(i+0.5))
							.append($("<span class='s_check'></span>")))));
			
			timeHtml += $(liWrap)[0].outerHTML;
		}
		
		$(".PN_timeTable ul").empty().append(timeHtml);
		
		pnReservation.setTimeTableEvent();
	},
	setResourceList: function(){
		$.ajax({
			url: "/groupware/resource/getResourceList.do",
			type: "POST",
			data: "",
			success: function(res){
				if(res.status == "SUCCESS"){
					if(res != null && res.data.length != 0){
						$.each(res.data, function(idx, item){
							$(".PN_rsPlace select").append($("<option>")
								.text(item.FolderName)
								.attr("value", item.FolderID));
						});
					}
					
					pnReservation.getReservationList($(".PN_rsPlace select option:selected").val(), $("#PN_rsDate").val().replaceAll(".", "-"));
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/resource/getResourceList.do", response, status, error);
			}
		});
	},
	getReservationList: function(pFolderID, pResDate){
		$.ajax({
			url: "/groupware/resource/getBookingList.do",
			type: "POST",
			data: {
				"mode": "D",
				"FolderID": ";" + pFolderID + ";",
				"StartDate": pResDate,
				"EndDate": pResDate,
				"hasAnniversary": "N"
			},
			success: function(res){
				if(res.status == "SUCCESS"){
					$(".PN_timeTable input[type=checkbox]").prop("disabled", false);
					$(".PN_timeTable input[type=checkbox]").prop("checked", false);
					
					if(res && res.data.folderList.length > 0){
						var bookingList = res.data.folderList[0].bookingList;
						
						if(bookingList && bookingList.length > 0){
							$.each(bookingList, function(bIdx, booking){
								var sTime = booking.StartTime;
								var eTime = booking.EndTime;
								var isTime = false;
								
								$.each($(".PN_timeTable input[type=checkbox]"), function(tIdx, time){
									var itemTime = $(time).attr("time");
									
									if(itemTime == sTime) isTime = true;
									else if(itemTime == eTime) isTime = false;
									
									if(isTime) $(time).prop("disabled", true);
								});
							});
						}
					}
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/groupware/resource/getBookingList.do", response, status, error);
			}
		});
	},
	openSubjectPopup: function(){
		var url = "/groupware/resource/goSubjectPopup.do?callBackFunc=pnReservation.getReservationList";
		var resID = $(".PN_rsPlace option:selected").val();
		var resDate = $("#PN_rsDate").val();
		var sHour = "00";
		var sMin = "00";
		var eHour = "01";
		var eMin = "00";
		
		if($(".PN_timeTable input[type=checkbox]:checked").length != 0){
			var sTime = $(".PN_timeTable input[type=checkbox]:checked:first").attr("time");
			var eTime = $(".PN_timeTable input[type=checkbox]:checked:last").attr("time");
			sHour = sTime.split(":")[0];
			sMin = sTime.split(":")[1];
			eHour = eTime.split(":")[0];
			eMin = eTime.split(":")[1];
			
			if(eMin == "30") {
				eHour = Number(eHour) + 1;
				eMin = "00"
			}else{
				eMin = "30"
			}
		}else{
			Common.Warning("<spring:message code='Cache.msg_selectReserveTime' />"); // 예약할 시간을 선택해 주세요.
			return false;
		}
		
		url += "&resourceID=" + resID
			+  "&resDate=" + resDate
			+  "&sTime=" + sHour + ":" + sMin
			+  "&eTime=" + eHour + ":" + eMin;
		
		Common.open("", "ResourceBooking", "<spring:message code='Cache.lbl_Booking' />", url, "400px", "300px", "iframe", true, null, null, true); // 자원예약
	}
}