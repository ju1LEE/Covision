/**
 * myContents - 마이 컨텐츠 - 일정
 */
var myContents_Schedule ={
	init: function (data,ext){
		myContents_Schedule.getScheduleList('today');	// 일정 조회
	},
	// 일정 조회
	getScheduleList : function(reqDate) {
		$.ajax({
			type:"POST",
			url:"/groupware/schedule/getWebpartScheduleList.do",
			data: {'reqDate' : reqDate},
			success:function(data) {
				var tarMiddle = $('.pieceSchedule > .pieceMiddle');
				var tarCont = $('.pieceSchedule > .pieceCont');

				var date = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss"));
				if (reqDate == 'yesterday') date.setDate(date.getDate() - 1); else if (reqDate == 'tomorrow') date.setDate(date.getDate() + 1);
				var week = new Array('일', '월', '화', '수', '목', '금', '토');
				var nowDate = $.datepicker.formatDate('yy.mm.dd ', date) + week[date.getDay()] + "요일";
				
				var list = data.list;
				
				var html = '<ul class="clearFloat pieceSchDay">';
				html += '<li'; 
				if (reqDate == 'yesterday') html += ' class="active"';
				html += '>';
				html += '<a href="#" onClick="myContents_Schedule.getScheduleList(\'yesterday\');">'+ "<spring:message code='Cache.lbl_Yesterday'/>" +'</a></li>';
				html += '<li'; 
				if (reqDate == 'today') html += ' class="active"';
				html += '>';
				html += '<a href="#" onClick="myContents_Schedule.getScheduleList(\'today\');">'+ "<spring:message code='Cache.lbl_Todays'/>" + '</a></li>';
				html += '<li'; 
				if (reqDate == 'tomorrow') html += ' class="active"';
				html += '>';				
				html += '<a href="#" onClick="myContents_Schedule.getScheduleList(\'tomorrow\');">' + "<spring:message code='Cache.lbl_Mail_Tomorrow'/>" + '</a></li>';
				html += '</ul>';
				html += '<div class="pieceSchSelDay">';
				html += '<p>' + nowDate + '</p>';
				html += '</div>';
				
				tarMiddle.empty().append(html);
				
				var now = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss"));
				html = '';
				
				if (list.length > 0) {
					$.each(list, function(i, v) {
						if (reqDate == 'today') {
							var startDate = myContents_Schedule.dateObj(v.StartTime + ' am');
							var endDate = myContents_Schedule.dateObj(v.EndTime + ' am');
							
							if (now <= endDate && now >= startDate) {
								html += '<li class="active">';
							} else {
								html += '<li>';
							}
						} else {
							html += '<li>';
						}
						html += '<p class="tit"><a onclick="scheduleUser.goDetailViewPage(\'Webpart\',' + v.EventID + ','  + v.DateID + ',' + v.RepeatID + ',\'' + v.IsRepeat + '\',' + v.FolderID + ')">' + v.Subject + '</a></p>';
						html += '<p class="date">' + v.StartTime + '~' + (v.EndTime.substring(0,2) >= 12 ? v.EndTime + ' pm' : v.EndTime + ' am') + '</p>';
						html += '</li>';
					});
					
	
					if (list.length > 0) html = '<ul class="pieceSchList">' + html + '</ul>'; 
	
					var tar = tarCont.find('.mCSB_container');
					if (tar.length == 0) tarCont.empty().append(html); else tar.html(html);
				}
				else {
					 
					html += '<div class="Schedule_Nodata">';
					html += '<p class="Schedule_NodataTxt">'+"<spring:message code='Cache.msg_NoScheduleInProgress'/>"+'</p>';
					html += '</div>';
					var tar = tarCont.find('.mCSB_container');
					if (tar.length == 0) tarCont.empty().append(html); else tar.html(html);
				}
			}
		});
	}, 
	dateObj: function(d){ // date 생성
		   var parts = d.split(/:|\s/);
		    var date  = new Date();
		    
		    if (parts.pop() == 'pm') parts[0] = (+parts[0]) + 12;
		    date.setHours(+parts.shift());
		    date.setMinutes(+parts.shift());
		    
		    return date;
	}
	
}


