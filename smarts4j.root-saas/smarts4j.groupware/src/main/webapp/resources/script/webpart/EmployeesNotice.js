/**
 * employeesNotice - 임직원 소식
 */

var employeesNoticeCount = 0;
var employeesNoticePage = 1;
var employeesNoticeTotalPage = 1;
var profilePath = Common.getBaseConfig("ProfileImagePath").replace("{0}", Common.getSession("DN_Code"));
var strSelMode = 'ALL';		//검색조건
var strJobMode = 'level';	// 표시기준 level: 직급, position : 직위, title : 직급
var strBirthMode = 'D';		// 생일기준 M: 월, D : 날짜
var strEnterInterval = 14;	// 입사일자 기준
var strVacationMode = "N";	//휴가 포함여부
var straddinterval = 0;		//휴가 몇일간 표시
var employeesOption = Common.getBaseConfig('Employees_WebpartDisplayOption');

var employeesNotice ={
		webpartType: '', 
		init: function (data,ext){
			employeesNotice.initNoticeType();
			$(".inPerTitbox").attr("style","display: inline-block; margin:-2px 0 0 20px;");
			$(".staffNews").css("padding-bottom","15px");
			
			var $this = this;
			straddinterval = ext.Addinterval;
			strJobMode = ext.JobMode;
			strBirthMode = ext.BirthMode;
			strEnterInterval = ext.EnterInterval;
			strVacationMode = ext.VacationMode;
			
			$('#employeeTitle').prepend("<spring:message code='Cache.lbl_Employees'/>");	//임직원
			$('#newsTitle').text("<spring:message code='Cache.lbl_news'/>");				//소식
			
			
			$("#boardURL").attr("href",ext.boardURL);

			employeesNotice.getList("0");
			
			//타입 검색
			$("#selMode").on('change',function(){
				strSelMode = $(this).val();
				employeesNotice.getCnt();
				employeesNotice.getList("0");
			});
		},	
		getClassKind: function(eventObj){
			var oType = new Object();
			
			switch(eventObj.Type){
				case 'Birth': //생일
					oType["Class"] ="birthday";
					oType["Name"] = "<spring:message code='Cache.lbl_Birthday'/>";
					break;
				case 'Enter'://신규 입사
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.lbl_New_Recruit'/>";
					break;
				case 'Promotion'://진급
					oType["Class"] ="promotion";
					oType["Name"] = eventObj.JobLevelName + " " + "<spring:message code='Cache.lbl_Promotion'/>";
					break;
				case 'Event'://돌잔치
					oType["Class"] ="event";
					oType["Name"] = "<spring:message code='Cache.lbl_FirstBirthday'/>";
					break;
				case 'Marry'://결혼
					oType["Class"] ="marry";
					oType["Name"] = "<spring:message code='Cache.lbl_Marriage'/>";
					break;
				case 'VACATION_ANNUAL'://휴가
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.lbl_Vacation'/>";
					break;
				case 'VACATION_OFF'://반차
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.VACATION_OFF'/>";
					break;
				case 'VACATION_CONGRATULATIONS'://경조사
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.VACATION_CONGRATULATIONS'/>";
					break;
				case 'VACATION_SICK_LEAVE'://병가
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.VACATION_SICK_LEAVE'/>";
					break;
				case 'VACATION_BEFOREANNUAL'://선연차
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.VACATION_BEFOREANNUAL'/>";
					break;
				case 'VACATION_BEFOREOFF'://선반차
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.VACATION_BEFOREOFF'/>";
					break;
				case 'VACATION_OTHER'://기타
					oType["Class"] ="newJoin";
					oType["Name"] = "<spring:message code='Cache.VACATION_OTHER'/>";
					break;
				default:
					var $codeData = $$(Common.getBaseCode("VACATION_TYPE")).find('CacheData[Code='+eventObj.Type+']');
					
					if ($codeData.length > 0){
						oType["Class"] = "newJoin";
						oType["Name"] = CFN_GetDicInfo($codeData.attr('MultiCodeName'));
					}
					else {
						oType["Class"] ="";
						oType["Name"] ="";
					}

					break;
			}
			
			return oType;
		},
		getCnt: function(){
			$.ajax({
				type:"POST",
				async:false,
				url:"/groupware/portal/getEmployeesNoticeCnt.do",
				data: {'selMode': strSelMode,
					   'addinterval' : straddinterval,
					   'birthMode' : strBirthMode,
					   'enterInterval' : strEnterInterval
				},
				success:function(data) {
					employeesNoticePage = 1;
					employeesNoticeCount = data.employeesCnt;
					if(employeesNoticeCount == 0) employeesNoticeCount =1;
					employeesNoticeTotalPage = parseInt(employeesNoticeCount / 5) ;
					if(employeesNoticeCount % 5  > 0) employeesNoticeTotalPage++;
					
					$('.staffNewsBtnTx').html('<strong>'+employeesNoticePage+'</strong>/'+employeesNoticeTotalPage);
				}
			});
		},
		getList: function(strPage){
			if(strPage == "-"){
				if(employeesNoticePage == 1) {
					return;
				}
				else {
					employeesNoticePage--;
					strPage = ((employeesNoticePage-1) * 5).toString();		
					$('.staffNewsBtnTx').html('<strong>'+employeesNoticePage+'</strong>/'+employeesNoticeTotalPage);
				}
			}else if (strPage == "+"){
				if(employeesNoticePage == employeesNoticeTotalPage) {
					return;
				}
				else {
					employeesNoticePage++;
					strPage = ((employeesNoticePage-1) * 5).toString();		
					$('.staffNewsBtnTx').html('<strong>'+employeesNoticePage+'</strong>/'+employeesNoticeTotalPage);
				}
			}
			
			$.ajax({
				type:"POST",
				url:"/groupware/portal/getEmployeesNoticeList.do",
				data: {'page' : strPage,
					   'selMode': strSelMode,
					   'addinterval' : straddinterval,
					   'birthMode' : strBirthMode,
					   'enterInterval' : strEnterInterval, 
					   'callerId' : 'webpart'
				},
				success:function(data) {
					//수행할 업무
					$("#employeesNotice_list").html("");
					employeesNoticeCount = data.employeesList.Count;
					if (employeesNoticeCount > 0)  {
						employeesNoticeTotalPage = parseInt(employeesNoticeCount / 5) ;
						if(employeesNoticeCount % 5  > 0) employeesNoticeTotalPage++;
						
						$('.staffNewsBtn').show();
						$('.staffNewsBtnTx').html('<strong>'+employeesNoticePage+'</strong>/'+employeesNoticeTotalPage);
					}else {
						$('.staffNewsBtnTx').html('<strong>'+employeesNoticePage+'</strong>/'+employeesNoticeTotalPage);
					}
					$.each( data.employeesList.list, function(index, obj) {
						var classObj = employeesNotice.getClassKind(obj);
						var sRepJobTypeConfig = Common.getBaseConfig("RepJobType");
						var strJob = obj.JobLevelName;
				        if(sRepJobTypeConfig == "PN"){
				        	strJob = obj.JobPositionName;
				        } else if(sRepJobTypeConfig == "TN"){
				        	strJob = obj.JobTitleName;
				        } else if(sRepJobTypeConfig == "LN"){
				        	strJob = obj.JobLevelName;
				        }
						
						if (strJobMode == "position") strJob = obj.JobPositionName;
						else if (strJobMode == "title") strJob = obj.JobTitleName;
						$("#employeesNotice_list").append('<li class="'+ classObj.Class+'">'
								+ '<div class="flowerPopup" onclick="coviCtrl.setFlowerName(this)" data-user-code="' + obj.UserCode + '" data-user-mail="'+ obj.MailAddress +'">'
								+ '<p class="stfIcon colorTheme"><a class="btnFlowerName">'
								+		'<strong class="fcStyle">' + classObj.Name + '</strong>'
								+	'</a></p></div>'
								+ '<div class="flowerPopup" onclick="coviCtrl.setFlowerName(this)" data-user-code="' + obj.UserCode + '" data-user-mail="'+ obj.MailAddress +'">'
								+	'<p class="staffPhoto">'
								+	'	<a class="btnFlowerName"><img src="' + coviCmn.loadImage(profilePath + this.UserCode + '.jpg')+'" alt="" onerror="coviCmn.imgError(this, true);" class="fcStyle"></a>	'
								+	'</p></div>'						
								+	'<div class="flowerPopup" onclick="coviCtrl.setFlowerName(this)" data-user-code="' + obj.UserCode + '" data-user-mail="'+ obj.MailAddress +'">'
								+	'<p class="staffName"><a class="btnFlowerName">'
								+	'		<span class="fcStyle">' +obj.UserName + ' ' +  strJob + '</span>'
								+	'</a></p></div>'
								//+	'<p class="date">' + obj.Date.substring(0, 10).replaceAll('-', '.') + '</p>'
								+	'<p class="date">' + obj.Date + '</p>'
								+	'</li>');
						});	
				}
			});			
		},
		goMoreList: function(){			
			var sTitle = Common.getDic("lbl_Employees") +" "+ Common.getDic("lbl_news");
			var sURL = "/groupware/portal/goEmployeesNoticeListPopup.do?"
					+ 'addinterval=' + straddinterval
					 +  '&birthMode=' + strBirthMode
					 +  '&enterInterval=' + strEnterInterval
					 +  '&jobMode=' + strJobMode
					 + '&vacationMode=' + strVacationMode;
			
			Common.open("","EmployeesList",	sTitle,sURL.replace("{0}", Common.getBaseConfig("WebpartEmp")),"865px","700px","iframe",true,null,null,true);
		},
		initNoticeType: function(){
			var noticeType = Common.getBaseCode('EmployeesNotice');
			var lang = Common.getSession('lang');
			
			var empNotice = noticeType.CacheData;

			$.each(noticeType.CacheData, function(empidx, el){
				var option = el.Code
				if(employeesOption.includes(option)){
					$("#selMode").append('<option value="'+el.Code+'">' + CFN_GetDicInfo(this.MultiCodeName, lang) + '</option>');
				}
			});
		}
}

