/**
 * 
 */

var ScheduleWithCalendar = {
	config: {
		type: 'N',			// 일정 유형 사용여부
		time: 'Y',			// 시간 표시 여부
		subject: 'Y',		// 제목 표시 여부
		webpartClassName: 'PN_scCalendar',			// 췝파트 최상단 div 클래스명
		webpartTopClassName: 'PN_TitleBox',			// 췝파트 톱영역 클래스명
		webpartContentClassName: '',				// 췝파트 컨텐츠영역 클래스명
		moreBtnClassName: 'PN_More',				// 더보기 버튼 클래스명
		listType: 1,		// listType 정의 - 웹파트 목록에 정리할 것
		folderList: ';'
	},
	init: function(data, ext, caller){
		var _ext = (typeof ext == 'object') ? ext : {};
		ScheduleWithCalendar.caller = caller;
		
		ScheduleWithCalendar.config = $.extend(ScheduleWithCalendar.config, _ext);
		
		$(".webpart").addClass(ScheduleWithCalendar.config.webpartClassName);
		$(".webpart-top").addClass(ScheduleWithCalendar.config.webpartTopClassName);
		$(".webpart-content").addClass(ScheduleWithCalendar.config.webpartContentClassName);
		$(".webpart-more").addClass(ScheduleWithCalendar.config.moreBtnClassName);
		
		if(ScheduleWithCalendar.caller == 'myPlace'){
			$(".webpart").closest(".PN_myContents_box").addClass("PN_myContents_sizeH");
			$(".webpart").closest(".PN_myContents_box").find(".PN_portlet_link").text($(".webpart-top h2").text());
			$(".webpart").closest(".PN_myContents_box").find(".PN_portlet_head").append(
				'<a href="/groupware/layout/schedule_View.do?CLSYS=schedule&amp;CLMD=user&amp;CLBIZ=Schedule&amp;viewType=M" class="PN_More">more</a>'
			);
			$(".webpart-top").remove();
		}
		
		var $this = this;
		$this.calendar = CoviCalendar('wp_swc', { weekLine: 6 });
		
		ScheduleWithCalendar.schedule.setData();
		
		$("#wp_swc .covi-calendar-date").on("click", function(e){
		    ScheduleWithCalendar.schedule.list();
		});
		
		$("#wp_swc").on("load", function(){
			ScheduleWithCalendar.schedule.setData();

			$("#wp_swc .covi-calendar-date").on("click", function(e){
			    ScheduleWithCalendar.schedule.list();
			});
		});
		
	},
	schedule: {
		data: [],		// todo: 디비에서 데이터를 가져와서 셋팅해야 함.
		setData: function(sDate, eDate){
//			console.log('디비에서 데이터를 가져와서 셋팅해야 함.');
//			ScheduleWithCalendar.schedule.data.length = 0;
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-11-01', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-11-02', Time: '09:00~10:00', Subject: '인사팀 주간회의', Type: 'personal' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-11-12', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-11-19', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-12-01', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-12-02', Time: '09:00~10:00', Subject: '인사팀 주간회의', Type: 'personal' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-12-12', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-12-19', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '09:00~10:00', Subject: '솔루션 검토 회의' });
//			ScheduleWithCalendar.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '10:00~12:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
//			ScheduleWithCalendar.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '14:00~15:00', Subject: '인사팀 주간회의', Type: 'personal' });
//			ScheduleWithCalendar.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '15:00~16:00', Subject: '솔루션 검토 회의' });
//			ScheduleWithCalendar.schedule.data.push({ Date: '2022-12-31', Time: '16:00~17:00', Subject: '종무식', Type: 'company' });
			if(schAclArray.status != "SUCCESS") {
				scheduleUser.setAclEventFolderData();
			}
			
			$(schAclArray.read).each(function(idx, el){
				ScheduleWithCalendar.config.folderList += (el.FolderID + ";");
			});
			
			$.ajax({
			    url: "/groupware/schedule/getList.do",
			    type: "POST",
			    data: {
			    	StartDate: $("[data-day-order=1]").attr("data-day"),
			    	EndDate: $("[data-day-order=42]").attr("data-day"),
			    	FolderIDs: ScheduleWithCalendar.config.folderList,
					UserCode: Common.getSession("USERID"),
					lang: Common.getSession("lang")
				},
			    success: function (res) {
			    	if(res.status == "SUCCESS"){
						ScheduleWithCalendar.schedule.data = res.list
						
						ScheduleWithCalendar.schedule.mark();
						ScheduleWithCalendar.schedule.list();
			    	} else {
			    		parent.Common.Error("<spring:message code='Cache.msg_apv_030'/>");		// 오류가 발생했습니다.
			    	}
			    },
			    error:function(response, status, error){
			    	parent.CFN_ErrorAjax("/groupware/schedule/getList.do", response, status, error);
			    }
			});
		},
		mark: function(){
			$.each(ScheduleWithCalendar.schedule.data, function(idx, el){
				if (el.StartDate == el.EndDate) {
					ScheduleWithCalendar.calendar.setMark(el.StartDate);
				}
			});
		},
		list: function(){
			var selDate = ScheduleWithCalendar.calendar.current;
			var selday = selDate.year+'-'+ScheduleWithCalendar.calendar.padding(selDate.month,2)+'-'+ScheduleWithCalendar.calendar.padding(selDate.day,2);
			var timeClass = 'caldate';
			var subjectClass = 'calcont'
			if (ScheduleWithCalendar.config.listType == 1){
				timeClass = 'sTime';
				subjectClass = 'sTxt';
			}
			var listHTML = '';
			$.each(ScheduleWithCalendar.schedule.data, function(idx, el){
				var isList = false;
				var _time = '';
				if (el.StartDate == el.EndDate && el.StartDate == selday) {
					isList = true;
					_time = el.StartTime+'~'+el.EndTime
				}
				else if (el.StartDate != el.EndDate && el.StartDate == selday) {
					isList = true;
					_time = el.StartTime+'~23:59'
				}
				else if (el.StartDate != el.EndDate && el.EndDate == selday){
					isList = true;
					_time = '00:00~'+el.EndTime
				}
				else if (el.StartDate != el.EndDate && el.StartDate < selday && el.EndDate > selday){
					isList = true;
					_time = '00:00~23:59'
				}

				if (isList){
					listHTML += '<li>';
					listHTML += '<a href="#">';
					if (ScheduleWithCalendar.config.type == 'Y') {
						var typeClass = (el.Type == 'company') ? 'sCpn' : (el.Type == 'dept') ? 'sDep' : 'sPsn';				// sCpn 회사, sDep: 부서, sPsn 개인
						var typeName = (el.Type == 'company') ? '회사' : (el.Type == 'dept') ? '부서' : '개인';
						listHTML += '<span class="sType '+typeClass+'">'+typeName+'</span>';
					}
					listHTML += (ScheduleWithCalendar.config.time == 'Y') ? '<span class="'+timeClass+'">'+_time+'</span>' : '';
					listHTML += (ScheduleWithCalendar.config.subject == 'Y') ? '<span class="'+subjectClass+'">'+el.Subject+'</span>' : '';
					listHTML += '</a>';
					listHTML += '</li>';
				}
			});
			
			coviCtrl.bindmScrollV($("#scheduleWithCalendar .mScrollV"));
			
			if (listHTML == ''){
				$("#scheduleWithCalendar .schedule_list ul").html('').append('<li>선택된 날짜에 일정이 없습니다.</li>');
			}
			else {
				$("#scheduleWithCalendar .schedule_list ul").html('').append(listHTML);
			}
		}
	}
}


/* 함수형 테스트
function ScheduleWithCalendar(pData, pExt, pCaller, pWebPartID){
	var swcObj = {
		init: function(data, ext, caller, webPartID){
			var _ext = (typeof ext == 'object') ? ext : {};
			swcObj.caller = caller;
			swcObj.webPartID = webPartID;
			swcObj.calid = "wp_swc_"+pWebPartID;
			
			$("#WP"+pWebPartID + " #wp_swc").attr("id", swcObj.calid);
			
			swcObj.schedule.config = $.extend(swcObj.schedule.config, _ext);
			
			if(swcObj.caller == 'myPlace'){
				$(".PLinkCalendar").closest(".PN_myContents_box").addClass("PN_myContents_sizeH");
				$(".PLinkCalendar").closest(".PN_myContents_box").find(".PN_portlet_link").text($(".PLinkCalendar .mNotiTitle h3").text());
				$(".PLinkCalendar").closest(".PN_myContents_box").find(".PN_portlet_head").append(
					'<a href="/groupware/layout/schedule_View.do?CLSYS=schedule&amp;CLMD=user&amp;CLBIZ=Schedule&amp;viewType=M" class="PN_More">more</a>'
				);
				$(".PLinkCalendar .mNotiTitle").remove();
			}
			
			var $this = this;
			$this.calendar = CoviCalendar(swcObj.calid, { lineHeight: '30px', weekLine: 6 });
			
			swcObj.schedule.setData();
			$.each($this.schedule.data, function(idx, el){
				$this.calendar.setMark(el.Date);
			});
			
			swcObj.schedule.list();
			coviCtrl.bindmScrollV($("#"+swcObj.calid+" .PLinkCalendar .mScrollV"));
			
			$("#"+swcObj.calid+" .covi-calendar-date").on("click", function(e){
			    swcObj.schedule.list();
			});
			
			$("#"+swcObj.calid).on("load", function(){
				swcObj.schedule.setData();
				$.each(swcObj.schedule.data, function(idx, el){
					swcObj.calendar.setMark(el.Date);
				});
			    swcObj.schedule.list();
	
				$("#"+swcObj.webPartID+" #wp_swc .covi-calendar-date").on("click", function(e){
				    swcObj.schedule.list();
				});
			});
			
		},
		schedule: {
			config: {
				type: 'N',
				time: 'Y',
				subject: 'Y'
			},
			data: [],		// todo: 디비에서 데이터를 가져와서 셋팅해야 함.
			setData: function(){
				console.log('디비에서 데이터를 가져와서 셋팅해야 함.');
				swcObj.schedule.data.length = 0;
				swcObj.schedule.data.push({ Date: '2022-11-01', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: '2022-11-02', Time: '09:00~10:00', Subject: '인사팀 주간회의', Type: 'personal' });
				swcObj.schedule.data.push({ Date: '2022-11-12', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: '2022-11-19', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: '2022-12-01', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: '2022-12-02', Time: '09:00~10:00', Subject: '인사팀 주간회의', Type: 'personal' });
				swcObj.schedule.data.push({ Date: '2022-12-12', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: '2022-12-19', Time: '09:00~10:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '09:00~10:00', Subject: '솔루션 검토 회의' });
				swcObj.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '10:00~12:00', Subject: '디자인팀 전체 회의', Type: 'dept' });
				swcObj.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '14:00~15:00', Subject: '인사팀 주간회의', Type: 'personal' });
				swcObj.schedule.data.push({ Date: CFN_GetLocalCurrentDate('yyyy-MM-dd'), Time: '15:00~16:00', Subject: '솔루션 검토 회의' });
				swcObj.schedule.data.push({ Date: '2022-12-31', Time: '16:00~17:00', Subject: '종무식', Type: 'company' });
			},
			list: function(){
				var selDate = swcObj.calendar.current;
				var selday = selDate.year+'-'+swcObj.calendar.padding(selDate.month,2)+'-'+swcObj.calendar.padding(selDate.day,2);
	
				var listHTML = '';
				$.each(swcObj.schedule.data, function(idx, el){
					if (el.Date == selday){
						listHTML += '<li>';
						listHTML += '<a href="#">';
						if (swcObj.schedule.config.type == 'Y') {
							var typeClass = (el.Type == 'company') ? 'sCpn' : (el.Type == 'dept') ? 'sDep' : 'sPsn';				// sCpn 회사, sDep: 부서, sPsn 개인
							var typeName = (el.Type == 'company') ? '회사' : (el.Type == 'dept') ? '부서' : '개인';
							listHTML += '<span class="sType '+typeClass+'">'+typeName+'</span>';
						}
	//					listHTML += (swcObj.schedule.config.time == 'Y') ? '<span class="sTime">'+el.Time+'</span>' : '';
						listHTML += (swcObj.schedule.config.time == 'Y') ? '<span class="caldate">'+el.Time+'</span>' : '';
	//					listHTML += (swcObj.schedule.config.subject == 'Y') ? '<span class="sTxt">'+el.Subject+'</span>' : '';
						listHTML += (swcObj.schedule.config.subject == 'Y') ? '<span class="calcont">'+el.Subject+'</span>' : '';
						listHTML += '</a>';
						listHTML += '</li>';
					}
				});
				
				if (listHTML == ''){
					$("#WP"+swcObj.webPartID+" .PLinkCalendar .schedule_list ul").html('').append('<li>선택된 날짜에 일정이 없습니다.</li>');
				}
				else {
					$("#WP"+swcObj.webPartID+" .PLinkCalendar .schedule_list ul").html('').append(listHTML);
				}
			}
		}
	}
	
	swcObj.init(pData, pExt, pCaller, pWebPartID);
	
	return swcObj;
} 
*/