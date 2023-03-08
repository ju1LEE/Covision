<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"  import="egovframework.baseframework.util.SessionHelper,egovframework.baseframework.util.PropertiesUtil,egovframework.baseframework.util.RedisDataUtil,egovframework.coviframework.util.ComUtils"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<style>
.StateBox .commuteList .coPhoto img{
    width: 40px;
}
.StateBox .commuteList .coTxt {
    width: calc(100% - 250px);
} 
</style>
<script type="text/javascript" src="/covicore/resources/ExControls/Chart.js-master/Chart.js<%=resourceVersion%>"></script>
<div class="cRContAll mScrollVH">
	<!-- 컨텐츠 시작 -->
	<div class="StateCont">
		<div class="StateArea">
			<div class="StateTop StateTop1">
				<div class="StateTopLeft">
					<input id="deptUpCode" type="hidden" onchange="changeDept()">
					<span id=deptCodeList></span>
					<h2 class="title"><spring:message code='Cache.lbl_att_attendance_sts' /></h2> <!-- 근태현황 -->
				</div>
				<div class="StateTopRight">
					<a href="#" class="btn_slide_close"><spring:message code='Cache.btn_Close' /></a> <!-- 닫기 -->
				</div>
			</div>
			<div class="StateBottom StateBottom1">
				<strong class="date">${TargetDate}(${TargetWeek}) <spring:message code='Cache.lbl_att_goWork' /> <spring:message code='Cache.lbl_CurrentSituation' /></strong> <!-- 출근 현황 -->
				<a href="#" data="DETAIL" class="more">more +</a>
				<ul class="StateList">
					<li class="bg1">
						<p class="">
							<a href=# class="Status" data="COMM"><strong id="work_cnt" class="num">0</strong>
							<span class="txt"><spring:message code='Cache.lbl_att_goWork' /></span></a> <!-- 출근 -->
						</p>
					</li>
					<li class="bg2">
						<p class="">
							<a href=#  class="Status" data="LATE"><strong id="late_cnt" class="num">0</strong>
							<span class="txt"><spring:message code='Cache.lbl_att_beingLate' /></span></a> <!-- 지각 -->
						</p>
					</li>
					<li class="bg3">
						<p class="">
							<a href=#  class="Status" data="ABSENT"><strong id="absent_cnt" class="num">0</strong>
							<span class="txt"><spring:message code='Cache.lbl_n_att_absent' /></span></span></a> <!-- 결근 -->
						</p>
					</li>
					<li class="bg4">
						<p class="">
							<a href=#  class="Status" data="VAC"><strong id="vac_cnt" class="num">0</strong>
							<span class="txt"><spring:message code='Cache.lbl_Vacation' /></span></span></a> <!-- 휴가 -->
						</p>
					</li>
				</ul>
				<div class="StateBoxArea">
					<div class="StateBox">
						<div class="StateBoxT">
							<h3 class="sTit"><spring:message code='Cache.lbl_att_attendance_sts' /></h3> <!-- 근태현황 -->
							<ul class="ATMschSelect inb tabCompany">
								<li class="selected" data-type="W"><a href="#"><spring:message code='Cache.lbl_Weekly' /></a></li> <!-- 주간 -->
								<li data-type="M"><a href="#"><spring:message code='Cache.lbl_Monthly' /></a></li> <!-- 월간 -->
							</ul>
						</div>
						<div class="StateBoxB">
							<div class="StateTit">
								<div class="StateTitLeft"  id="companyTit">
									<strong class="date">${StartDate} ~ ${EndDate}</strong>
									<div class="pagingType03" section-type="C">
										<a href="#" class="pre" data-paging="-"></a><a href="#" class="next" data-paging="+"></a>
										<a href="#" class="calendartoday btnTypeDefault"><spring:message code='Cache.lbl_Todays'/></a>
										<a href="#" class="calendarcontrol btnTypeDefault cal"></a>
										<input type="text" class="calendarinput" style="height: 0px; width:0px; border: 0px;" >
									</div>
								</div>
							</div>
							<div class="StateDg">
								<!-- 도표 영역 -->
								<div class="diagramBx">
									<div style="width:180px;height:180px; margin:0 auto;"><canvas id="companyPie"></canvas></div>
								</div>	
								<div class="diagramTxt" id="companyState">
									<p class="TotalNum"><spring:message code='Cache.lbl_total' /> <strong id=tot_cnt>0</strong><spring:message code="Cache.lbl_personCnt"/></p> <!-- 총 -->
									<ul class="TotalList">
										<li><a href=# class="Status" data="EXTEN"><span class="txt"><spring:message code='Cache.lbl_att_overtime_work' /></span><strong class="num" id="exten_cnt">0</strong></a></li> <!-- 연장근무 -->
										<li><a href=# class="Status" data="HOLI"><span class="txt"><spring:message code='Cache.lbl_att_holiday_work' /></span> <strong class="num" id="holi_cnt">0</strong></a></li> <!-- 휴일근무 -->
										<li><a href=# class="Status" data="VAC_COM"><span class="txt"><spring:message code='Cache.lbl_Vacation' /></span> <strong class="num" id="vac_cnt">0</strong></a></li> <!-- 휴가 -->
										<li><a href=# class="Status" data="COMM"><span class="txt"><spring:message code='Cache.lbl_attendance_normal' /></span> <strong class="num" id="normal_cnt">0</strong></a></li> <!-- 정상 -->
									</ul>
								</div>
								<!-- //도표 영역 -->
							</div>
						</div>
						<a href="#" data="ATT" class="more">more +</a>
					</div>
					<div class="StateBox">
						<div class="StateBoxT">
							<h3 class="sTit"><spring:message code='Cache.lbl_commuteMiss'/><span id=commuteCnt></span></h3>
						</div>
						<div class="StateBoxB pd0">
							<div class="commuteTop">
								<%if (RedisDataUtil.getBaseConfig("isUseMail").equals("Y")){%>
								<div class="chkStyle08">
									<input type="checkbox" id="checkAll" name="checkAll">
									<label for="checkAll"><span class="s_check"></span></label>
								</div>
								<a href="#" class="btnBlueType02 btnSend" id="btnSelSend"><spring:message code='Cache.lbl_Send' /></a>
								<%} %>
							</div>
							<div class="commuteList">
								<ul></ul>
							</div>
						</div>
						<a href="#" data="SEND"  class="more">more +</a>
					</div>
				</div>
			</div>
		</div>
		<div class="StateArea">
			<div class="StateTop StateTop2">
				<div class="StateTopLeft">
					<h2 class="title"><span><%=SessionHelper.getSession("UR_Name") %></span> <spring:message code='Cache.lbl_Sir' /> <spring:message code='Cache.lbl_att_attendance_sts' /></h2> <!-- 님 근태현황 -->
					<c:if test="${userSchedule.SchName != ''}">
						<span class="timeType">${userSchedule.SchName}</span> 
						<span class="time">${userSchedule.AttDayStartTime} ~ ${userSchedule.AttDayEndTime}</span>
					</c:if>
				</div>
				<div class="StateTopRight">
					<a href="#" class="btn_slide_close"><spring:message code='Cache.btn_Close' /></a> <!-- 닫기 -->
				</div>
			</div>
			<div class="StateBottom StateBottom2">
				<div class="StateTimeArea">
					<strong class="date">${TargetDate}(${TargetWeek}) <spring:message code='Cache.lbl_att_goWork' /> <spring:message code='Cache.lbl_CurrentSituation' /></strong> <!-- 출근 현황 -->
					<div class="StateTimeBox">
						<ul>
							<li>
								<a href="#">
									<strong class="tit tit1"><spring:message code='Cache.lbl_att_goWork' /></strong> <!-- 출근 -->
									<span class="txt">${userCommute}</span>
								</a>
							</li>
							<li>
								<a href="#">
									<strong class="tit tit2"><spring:message code='Cache.lbl_ThisWeek' /> <spring:message code='Cache.lbl_n_att_remain' /> <spring:message code='Cache.lbl_n_att_workingHours' /></strong> <!-- 금주 잔여 근로시간 -->
									<span class="txt">${RemainTime}</span>
								</a>
							</li>
							<li>
								<a href="#">
									<strong class="tit tit3"><spring:message code='Cache.lbl_PrescribedWorkingHours' /></strong> <!-- 소정근로시간 -->
									<span class="txt">${FixWorkTime}h</span>
								</a>
							</li>
						</ul>
					</div>
					<a href="#" data="MYATT" class="more">more +</a>
				</div>
				<div class="StateBoxArea">
					<div class="StateBox">
						<div class="StateBoxT">
							<h3 class="sTit"><spring:message code='Cache.lbl_att_attendance_sts' /></h3> <!-- 근태현황 -->
							<ul class="ATMschSelect inb tabUser">
								<li class="selected" data-type="W"><a href="#"><spring:message code='Cache.lbl_Weekly' /></a></li> <!-- 주간 -->
								<li data-type="M"><a href="#"><spring:message code='Cache.lbl_Monthly' /></a></li> <!-- 월간 -->
							</ul>
						</div>
						<div class="StateBoxB">
							<div class="StateTit">
								<div class="StateTitLeft"  id="userTit">
									<strong class="date">${StartDate} ~ ${EndDate}</strong>
									<div class="pagingType03"  section-type="U">
										<a href="#" class="pre" data-paging="-"></a>
										<a href="#" class="next" data-paging="+"></a>
										<a href="#" class="calendartoday btnTypeDefault"><spring:message code='Cache.lbl_Todays'/></a>
										<a href="#" class="calendarcontrol btnTypeDefault cal"></a>
										<input type="text" class="calendarinput" style="height: 0px; width:0px; border: 0px;" >
									</div>
								</div>
							</div>
							<div class="StateDg">
								<!-- 도표 영역 -->
								<div class="diagramBx">
									<div style="width:180px;height:180px; margin:0 auto;"><canvas id="attendPie"></canvas></div>
								</div>	
								<div class="diagramTxt" id="userState">
									<p class="TotalNum"><spring:message code='Cache.lbl_total'/> <strong id="tot_time"></strong></p> <!-- 총 -->
									<ul class="TotalList">
										<li><span class="txt"><spring:message code='Cache.lbl_n_att_normalWork'/></span> <strong class="num" id="work_time"></strong></li> <!-- 정상근무 -->
										<li><span class="txt"><spring:message code='Cache.lbl_att_overtime_work'/></span> <strong class="num" id="exten_time"></strong></li> <!-- 연장근무 -->
										<li><span class="txt"><spring:message code='Cache.lbl_att_holiday_work'/></span> <strong class="num" id="holi_time"></strong></li> <!-- 휴일근무 -->
										<li><span class="txt"><spring:message code='Cache.lbl_n_att_remainWork'/></span> <strong class="num" id="remain_time"></strong></li> <!-- 잔여근무 -->
									</ul>
								</div>
								<!-- //도표 영역 -->
							</div>
						</div>
						<a href="#" data="MYATT" class="more">more +</a>
					</div>
					<div class="StateBox">
						<div class="StateBoxT">
							<h3 class="sTit"><spring:message code='Cache.mag_Attendance28'/></h3> <!-- 근태 누락 소명 신청 -->
						</div>
						<div class="StateBoxB">
							<div class="omList">
								<c:choose>
									<c:when test="${fn:length(userCallingList)== 0}">
										<div class="taskCont4" id="itemContainer"><p class="OWList_none"><spring:message code='Cache.msg_NoDataList'/></p></div> <!-- 조회할 목록이 없습니다. -->
									</c:when>
									<c:otherwise>
									<ul>
										<c:forEach items="${userCallingList}" var="list" varStatus="status">
											<c:set var="WorkNm" value="lbl_n_att_absent" />
											<c:set var="WorkClass" value="omState1" />
											<c:choose>
												<c:when  test="${fn:length(list.EndSts)> 0 && list.EndSts!='lbl_att_normal_offWork'}">
													<c:set var="WorkNm" value="${list.EndSts}" />
													<c:set var="WorkClass" value="omState2" />
												</c:when>	
												<c:otherwise>
													<c:if  test="${fn:length(list.StartSts)> 0 && list.StartSts!='lbl_att_normal_goWork'}">
														<c:set var="WorkNm" value="${list.StartSts}" />
														<c:set var="WorkClass" value="omState2" />
													</c:if>	
												</c:otherwise>		
											</c:choose>
											<li>
												<a class="omTime" href="#">${fn:replace( list.JobDate,'-','.')}</a>
												<span class="omState ${WorkClass}"><spring:message code="Cache.${WorkNm}"></spring:message></span>
												<a class="btnTypeDefault btnApply" href="#"><spring:message code="Cache.lbl_app_approval_call"></spring:message></a>
											</li>
										</c:forEach>
									</ul>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<a href="#" data="CALL" class="more">more +</a>
					</div>
				</div>
				<div class="StateBoxArea">
					<div class="StateBox">
						<div class="StateBoxT">
							<h3 class="sTit"><spring:message code='Cache.mag_Attendance29' /></h3> <!-- 연장/휴일 근무 신청 -->
						</div>
						<div class="StateBoxB">
							<div class="holiList">
								<c:choose>
									<c:when test="${fn:length(userExtendList)== 0}">
										<div class="taskCont4" id="itemContainer"><p class="OWList_none"><spring:message code='Cache.msg_NoDataList'/></p></div> <!-- 조회할 목록이 없습니다. -->
									</c:when>
									<c:otherwise>
									<ul>
										<c:forEach items="${userExtendList}" var="list" varStatus="status">
											<li>
											<div class="holiTxt">
												<p class="holiTit"><a href="#">${list.URName} ${list.JobPositionName} / ${list.ReqDate}</a></p>
												<p class="holiCon"><a href="#">${list.Comment}</a></p>
											</div>
											<div class="holiBtn">
												<span class="stateSuc">${list.StatusName }</span>
											</div>
											</li>
										</c:forEach>
									</ul>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<a href="#"  data="MYREQ" class="more">more +</a>
					</div>
					<div class="StateBox">
						<div class="StateBoxT">
							<h3 class="sTit"><spring:message code='Cache.lbl_att_work' /> <spring:message code='Cache.lbl_att_approval' /></h3> <!-- 근무 신청 -->
						</div>
						<div class="StateBoxB StateBoxB2">
							<ul class="workList">
								<li>
									<a href="#" data="O">
										<span class="Wimg"><img src="<%=cssPath%>/AttendanceManagement/resources/images/ic_work1.jpg" /></span>
										<span class="Wtxt"><spring:message code='Cache.lbl_app_approval_extention' /></span> <!-- 연장근무 신청 -->
									</a>
								</li>
								<li>
									<a href="#" data="H">
										<span class="Wimg"><img src="<%=cssPath%>/AttendanceManagement/resources/images/ic_work2.jpg" /></span>
										<span class="Wtxt"><spring:message code='Cache.lbl_app_approval_holiday' /></span> <!-- 휴일근무 신청 -->
									</a>
								</li>
								<li>
									<a href="#" data="V">
										<span class="Wimg"><img src="<%=cssPath%>/AttendanceManagement/resources/images/ic_work3.jpg" /></span>
										<span class="Wtxt"><spring:message code='Cache.btn_apv_vacation_req' /></span> <!-- 휴가 신청 -->
									</a>
								</li>
								<li>
									<a href="#" data="J">
										<span class="Wimg"><img src="<%=cssPath%>/AttendanceManagement/resources/images/ic_work4.jpg" /></span>
										<span class="Wtxt"><spring:message code='Cache.lbl_n_att_otherJobReq' /></span> <!-- 기타근무 신청 -->
									</a>
								</li>
							</ul>
						</div>
						<a href="#" data="MYATT" class="more">more +</a>
						
					</div>
				</div>
			</div>
		</div>
		<div class="StateArea">
			<div class="StateTop StateTop3">
				<div class="StateTopLeft">
					<h2 class="title"><spring:message code='Cache.lbl_att_work' /><spring:message code='Cache.lbl_CurrentSituation' /></h2> <!-- 근무현황 -->
					<input id="deptUpCodeWork" type="hidden" onchange="changeDeptWork()">
					<span id=deptCodeListWork></span>
				</div>
				<div class="StateTopRight">
					<span class="temp"><spring:message code='Cache.mag_Attendance30' /></span> <!-- 근무템플릿 -->
					<select class="selectType02" id="SchSeq">
						<option value=""><spring:message code='Cache.lbl_all' /></option> <!-- 전체 -->
						<c:forEach items="${SchList}" var="list" varStatus="status" >
							<option value="${list.SchSeq}">${list.SchName}</option>
						</c:forEach>	
					</select>
					<div class="searchBox02">
						<span>
							<input type="text" id="searchText" />
							<button type="button" class="btnSearchType01"><spring:message code='Cache.btn_search' /></button> <!-- 검색 -->
						</span>
					</div>
					<a href="#" class="btn_slide_close"><spring:message code='Cache.btn_Close' /></a> <!-- 닫기 -->
				</div>
			</div>
			<div class="StateBottom StateBottom3">
				<div class="StateTit">
					<div class="StateTitLeft"  id="deptTit">
						<strong class="date" >${TargetDate}</strong>
						<div class="pagingType03"  section-type="D">
							<a href="#" class="pre" data-paging="-"></a>
							<a href="#" class="next" data-paging="+"></a>
							<a href="#" class="calendartoday btnTypeDefault"><spring:message code='Cache.lbl_Todays'/></a>
							<a href="#" class="calendarcontrol btnTypeDefault cal"></a>
							<input type="text" class="calendarinput" style="height: 0px; width:0px; border: 0px;" >
							<a class="btnTypeDefault" id="btnExcel" href="#"><spring:message code='Cache.btn_SaveToExcel'/></a> <!-- 엑셀저장 -->
						</div>
						<span><input type="checkbox" id="ckb_daynight" checked/> <spring:message code='Cache.lbl_DayNightMarking'/></span> <!-- 주야표기 -->
					</div>
					<div class="StateTitRight">
						<ul class="ATMschSelect TabList">
							<li class="selected" data-tab="tab-1" data-type="D"><a href="#"><spring:message code='Cache.lbl_Daily'/></a></li> <!-- 일간 -->
							<li data-tab="tab-2" data-type="M"><a href="#"><spring:message code='Cache.lbl_Monthly'/></a></li> <!-- 월간 -->
						</ul>
					</div>
				</div>
				<!-- StateTit end -->
				<div class="tblList">
					<div id="tab-1" class="TabCont selected">
						<!-- 근무현황 테이블 일간 시작 -->
						<table class="ATMTable ATMTablePd" cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th width="" rowspan="2"><spring:message code='Cache.lbl_hr_name'/></th> <!-- 성명 -->
									<th width="" rowspan="2"><spring:message code='Cache.lbl_Postion'/></th> <!-- 소속 -->
									<th width="" rowspan="2" class="lh"><spring:message code='Cache.lbl_att_work'/><br /> <spring:message code='Cache.lbl_Template'/></th> <!-- 근무<br /> 템플릿 -->
									<th width="" colspan="8"><spring:message code='Cache.lbl_att_work'/><spring:message code='Cache.lbl_CurrentSituation'/></th> <!-- 근무현황 -->
								</tr>
								<tr>
									<th class="bl"><spring:message code='Cache.lbl_att_goWork'/></th> <!-- 출근 -->
									<th><spring:message code='Cache.lbl_leave'/></th> <!-- 퇴근 -->
									<th><spring:message code='Cache.lbl_BasicWork'/></th> <!-- 기본근무 -->
									<th><spring:message code='Cache.lbl_att_overtime_work'/></th> <!-- 연장근무 -->
									<th><spring:message code='Cache.lbl_att_holiday_work'/></th> <!-- 휴일근무 -->
									<th><spring:message code='Cache.lbl_att_beingLate'/></th> <!-- 지각 -->
									<th><spring:message code='Cache.lbl_att_leaveErly'/></th> <!-- 조퇴 -->
									<th><spring:message code='Cache.lbl_n_att_resttime'/></th> <!-- 휴게 -->
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
						<!-- 근무현황 테이블 일간 끝 -->
						<div class="AXgridPageBody" style="z-index:1;">
							<div id="custom_navi_messageGrid_D" style="text-align:center;margin-top:2px;"></div>
						</div>
					</div>
					<div id="tab-2" class="TabCont">
						<!-- 근무현황 테이블 주/월 시작 (*상세보기 버튼 누르면 윈도우 팝업 노출됨)-->
						<table class="ATMTable" cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th width="" rowspan="2"><spring:message code='Cache.lbl_hr_name'/></th> <!-- 성명 -->
									<th width="" rowspan="2"><spring:message code='Cache.lbl_Postion'/></th> <!-- 소속 -->
									<th width="" colspan="6"><spring:message code='Cache.lbl_CurrentSituation'/></th> <!-- 근무현황 -->
									<th width="" rowspan="2" class="lh"><spring:message code='Cache.lbl_att_work'/><br /> <spring:message code='Cache.lbl_Template'/></th> <!-- 근무<br /> 템플릿 -->
								</tr>
								<tr>
									<th class="bl"><spring:message code='Cache.lbl_BasicWork'/></th> <!-- 기본근무 -->
									<th><spring:message code='Cache.lbl_att_overtime_work'/></th> <!-- 연장근무 -->
									<th><spring:message code='Cache.lbl_att_holiday_work'/></th> <!-- 휴일근무 -->
									<th><spring:message code='Cache.lbl_att_beingLate'/></th> <!-- 지각 -->
									<th><spring:message code='Cache.lbl_att_leaveErly'/></th> <!-- 조퇴 -->
									<th><spring:message code='Cache.lbl_n_att_resttime'/></th> <!-- 휴게 -->
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
						<!-- 근무현황 테이블 주/월 끝 -->
						<div class="AXgridPageBody" style="z-index:1;">
							<div id="custom_navi_messageGrid_M" style="text-align:center;margin-top:2px;"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 컨텐츠 끝 -->
</div>
<script>
//날짜paging
var _targetDate = "${TargetDate}";
var _companyMap = {targetDate:"${TargetDate}",startDate:"${StartDate}", endDate: "${EndDate}", pageType: "W"};
var _userMap    = {targetDate:"${TargetDate}",startDate:"${StartDate}", endDate: "${EndDate}", pageType: "W"};
var _deptMap    = {targetDate:"${TargetDate}",startDate:"${TargetDate}", endDate: "${TargetDate}", pageType: "D"};
var _pageNo     = 1;
var _pageSize   = 10;
var _endPage    = 1;
var _callStartDate = "";
var _callEndDate = "";
var _printDN = true;
var deptChart;
var myChart;
var chartColor = [ "rgb(209,239,240)","rgb(240,138,100)","rgb(90,206,126)","rgb(156,95,184)"];
	$(document).ready(function(){
		AttendUtils.getDeptStepList($("#deptCodeList"),"", false, false, "deptUpCode");
		AttendUtils.getDeptStepList($("#deptCodeListWork"),'', false, false, "deptUpCodeWork");
		//getCompanyAttInfo('A', _targetDate, 1);

		//회사별 근태현황 event
		$(".pre, .next").click(function(){
			var targetDate;
			var startDate;
			var endDate ;
			var section_type=$(this).parent().attr("section-type")
			switch (section_type){
				case "C":
					startDate = _companyMap["startDate"];
					endDate = _companyMap["endDate"];
					break;
				case "U":
					startDate = _userMap["startDate"];
					endDate = _userMap["endDate"];
					break;
				case "D":	
					startDate = _deptMap["startDate"];
					endDate = _deptMap["endDate"];
					break;
			}

			if("+"==$(this).attr("data-paging")){
				targetDate = schedule_SetDateFormat(schedule_AddDays(endDate, 1), '-');
			}else {
				targetDate = schedule_SetDateFormat(schedule_AddDays(startDate, -1), '-');
			}

			getCompanyAttInfo(section_type, targetDate, 1);
			
		});

		//오늘 클릭시
		$(".calendartoday").click(function(){
			getCompanyAttInfo($(this).parent().attr("section-type"), "${TargetDate}", 1);
		});
		$(".calendarinput").datepicker({
			dateFormat: 'yy-mm-dd',
			beforeShow: function(input) {
	           var i_offset = $(this).closest(".calendarcontrol").offset();      // 버튼이미지 위치 조회
	           setTimeout(function(){
	              jQuery("#ui-datepicker-div").css({"left":i_offset});
	           })
	        },
			onSelect: function(dateText){
				getCompanyAttInfo($(this).closest(".pagingType03").attr("section-type"), dateText, 1);
			}
		});
		$(".calendarcontrol").click(function(event){
			$(this).next("input").datepicker().datepicker("show");
		});

		//슬라이드 제어
		$(".StateTop .btn_slide_close").click(function(){
			 $(this).parents().next(".StateBottom").slideToggle(500);
			 $(this).toggleClass('btn_slide_close');
			 $(this).toggleClass('btn_slide_open');
			 if($(".StateTop").has("btn_slide_open")) {
					$(".btn_slide_open").html("<spring:message code='Cache.lbl_Open' />"); //열기
					$(".btn_slide_close").html("<spring:message code='Cache.btn_Close' />"); //닫기
			 }
		});

		//탭영역-회사근태
		$('.tabCompany li').click(function(){
			$('.tabCompany li').removeClass('selected');
			$(this).addClass('selected');
			_companyMap["pageType"]=$(this).attr('data-type');
			getCompanyAttInfo('C', "" , 1);
		});

		//탭영역-my근태
		$('.tabUser li').click(function(){
			$('.tabUser li').removeClass('selected');
			$(this).addClass('selected');
			_userMap["pageType"]=$(this).attr('data-type');
			getCompanyAttInfo('U', "", 1);
		});
		
		//탭영역-부서근태
		$('.TabList li').click(function(){
			var tab_id = $(this).attr('data-tab');

			$('.TabList li').removeClass('selected');
			$('.TabCont').removeClass('selected');

			$(this).addClass('selected');
			$("#"+tab_id).addClass('selected');
			_deptMap["pageType"]=$(this).attr('data-type');
			getCompanyAttInfo('D', "", 1);
		});
		
		//엑셀
		$("#btnExcel").click(function() {
			$('#download_iframe').remove();
			
			var url = "/groupware/attendPortal/excelPortalDept.do";
			var params = {pageType : _deptMap["pageType"]
					,dateTerm : _deptMap["pageType"]
					,targetDate : _deptMap["targetDate"]
					,sDate : _deptMap["startDate"]
					,eDate : _deptMap["endDate"]
					,deptCode : _deptMap["deptCode"]
					,deptUpCode : _deptMap["deptUpCode"]
					,deptUpCodeWork : _deptMap["deptUpCodeWork"]
			
					,queryType:"D"
					,deptUpCode:$("#deptUpCode").val()
					,deptUpCodeWork:$("#deptUpCodeWork").val()
					,searchText:$("#searchText").val()
					,schSeq:$("#SchSeq").val()

					,printDN:_printDN
				}
			ajax_download(url, params); 	// 엑셀 다운로드 post 요청*/	
		});
		
		$("#SchSeq").change(function(){
			getCompanyAttInfo('D', "", 1);
		});
		
		$(".Status").click(function(){		//출퇴근 상세
			var startDate;
			var endDate;
			var deptCode;
			var searchStatus;
			switch ($(this).attr("data")){
				case "VAC_COM":
				case "EXTEN":
				case "HOLI":
				case "COMM":
					startDate = _companyMap["startDate"];
					endDate = _companyMap["endDate"];
					break;
				default:
					startDate = _targetDate;
					endDate = _targetDate;
					break;
			}

			var popupID		= "AttendPortalStatusPopup";
			var openerID	= "AttendPortal";
			var popupTit	= "<spring:message code='Cache.lbl_n_att_history'/>";
			var popupYN		= "N";
			var popupUrl	= "/groupware/attendPortal/AttendPortalStatusPopup.do?"
							+ "popupID="		+ popupID	+ "&"
							+ "openerID="		+ openerID	+ "&"
							+ "popupYN="		+ popupYN	+ "&"
							+ "StartDate="		+ startDate	+ "&"
							+ "EndDate="		+ endDate	+ "&"
							+ "SearchStatus="	+ $(this).attr("data")	+ "&"
							+ "DeptUpCode="		+ $("#deptUpCode").val()	+ "&"
			
			Common.open("", popupID, popupTit, popupUrl, "820px", "750px", "iframe", true, null, null, true);

		});
		//검색 클릭시
		$(".btnSearchType01").click(function(){
			getCompanyAttInfo('D', "", 1);
		});
		
		//more 클릭
		$(".more").click(function(){
			switch ($(this).attr("data")){
				case "DETAIL":	//근태상셍
					var popupID		= "AttendPortalStatusPopup";
					var openerID	= "AttendPortal";
					var popupTit	= "<spring:message code='Cache.lbl_n_att_history'/>";
					var popupYN		= "N";
					var popupUrl	= "/groupware/attendPortal/AttendPortalStatusPopup.do?"
									+ "popupID="		+ popupID	+ "&"
									+ "openerID="		+ openerID	+ "&"
									+ "popupYN="		+ popupYN	+ "&"
									+ "StartDate="		+ _companyMap["startDate"]	+ "&"
									+ "EndDate="		+ _companyMap["endDate"]	+ "&"
									+ "SearchStatus="	+ "COMM&"
									+ "DeptUpCode="		+ $("#deptUpCode").val()	+ "&"
					
					Common.open("", popupID, popupTit, popupUrl, "820px", "750px", "iframe", true, null, null, true);
					break;
				case "ATT":	//매니저근태
					CoviMenu_GetContent('/groupware/layout/attend_AttendUserStatus.do?CLSYS=attend&CLMD=user&CLBIZ=Attend');
					break;
				case "SEND":	//누락
					var popupID		= "AttendPortalSendPopup";
					var openerID	= "AttendPortal";
					var popupTit	= "<spring:message code='Cache.lbl_commuteMiss'/>";
					var popupYN		= "N";
					var popupUrl	= "/groupware/attendPortal/AttendPortalSendPopup.do?"
									+ "popupID="		+ popupID	+ "&"
									+ "openerID="		+ openerID	+ "&"
									+ "popupYN="		+ popupYN	+ "&"
									+ "StartDate="		+ (_callStartDate == null?"${TargetDate}":_callStartDate)	+ "&"
									+ "EndDate="		+ (_callStartDate == null?"${TargetDate}":_callEndDate)	+ "&"
					
					Common.open("", popupID, popupTit, popupUrl, "820px", "690px", "iframe", true, null, null, true);
					break;
				case "MYATT":	//사용자근태
				case "CALL":	//소명신청
					CoviMenu_GetContent('/groupware/layout/attend_AttendMyStatus.do?CLSYS=attend&CLMD=user&CLBIZ=Attend');
					break;
				case "MYREQ":	//나의 요청
					CoviMenu_GetContent('/groupware/layout/attend_AttendMyRequestMng.do?CLSYS=attend&CLMD=user&CLBIZ=Attend');
					break;
				
			}
		});
	
		//소명신청
		$(".btnApply").click(function(){
			AttendUtils.openCallPopup();
		});
		
		//각종클릭
		$(".workList a").click(function(){
			switch ($(this).attr("data")){
				case "O":	//연장
					AttendUtils.openOverTimePopup();
					break;
				case "H":	//휴일
					AttendUtils.openHolidayPopup();
					break;
				case "V":	//휴가
					AttendUtils.openVacationPopup("USER");
					break;
				case "J":	//기타
					CoviMenu_GetContent('/groupware/layout/attend_AttendMyStatus.do?CLSYS=attend&CLMD=user&CLBIZ=Attend');
					break;
				
			}
		});
		
		$("#checkAll").click(function() {
			$(".commuteList input:checkbox").each(function() {
				$(this).prop("checked", $("#checkAll").prop("checked"));
			});

		});

		//주야 표기 모드
		if($("#ckb_daynight").is(":checked")){
			_printDN = true;
		}else{
			_printDN = false;
		}

		$("#ckb_daynight").change(function() {
			if(this.checked) {
				_printDN = true;
			}else{
				_printDN = false;
			}
			printDN();
		});
	});

	function goPage(pnum){
		_pageNo = pnum;
		$(".AXPaging").each(function(idx){
			$(this).removeClass("Blue");
			var valNum = $(this).attr("value");
			if(Number(valNum)===_pageNo){
				$(this).addClass("Blue");
			}
		});
		getCompanyAttInfo("D", "", pnum);
	}

	function pagging(deptPage){
		//pageing
		var pageNo = Number(deptPage.pageNo);
		var sPage = pageNo - 5;
		if(sPage<1){
			sPage = 1;
		}
		var lPage = sPage + 9;
		if(lPage>Number(deptPage.pageCount)){
			lPage = Number(deptPage.pageCount);
		}
		var nextPage = Number(lPage)+1;
		if(nextPage>Number(deptPage.pageCount)){
			nextPage = Number(deptPage.pageCount);
		}
		var prePage = Number(sPage)-1;
		if(prePage<1){
			prePage = 1;
		}
		var firstPage = 1;
		var endPage = Number(deptPage.pageCount);
		var htmlStr = "";
		htmlStr+='<input type="button" id="AXPaging_begin" class="AXPaging_begin" onclick="javascript:goPage('+firstPage+');">';
		htmlStr+='<input type="button" id="AXPaging_prev" class="AXPaging_prev" onclick="javascript:goPage('+prePage+');">';

		for(var i=sPage;i<=lPage;i++){
			var blue = "";
			if(_pageNo==i){
				blue = " Blue";
			}
			htmlStr+='<input type="button" value="'+i+'" style="min-width:20px;" class="AXPaging'+blue+'" onclick="javascript:goPage('+i+');">';
		}
		htmlStr+='<input type="button" id="AXPaging_next" class="AXPaging_next" onclick="javascript:goPage('+nextPage+');">';
		htmlStr+='<input type="button" id="AXPaging_end" class="AXPaging_end" onclick="javascript:goPage('+endPage+');">';
		return htmlStr;
	}

	function changeDept(){
		getCompanyAttInfo('A', "", 1);
	}
	
	function changeDeptWork(){
		getCompanyAttInfo('D', "", 1);
	}
	
	function getCompanyAttInfo(type, targetDate, pageNo){
		_pageNo =pageNo; 
		if (targetDate != ""){
			switch (type){
				case "C":
					_companyMap["targetDate"]=targetDate;
					break;
				case "U":
					_userMap["targetDate"]=targetDate;
					break;
				case "D":	
					_deptMap["targetDate"]=targetDate;
					break;
			}
		}	
		
		var params = {
			companyMap : _companyMap
			,userMap : _userMap
			,deptMap : _deptMap
			,queryType:type
			,deptUpCode:$("#deptUpCode").val()
			,deptUpCodeWork:($("#deptUpCodeWork").val()==""?$("#deptUpCode").val():$("#deptUpCodeWork").val())
			,searchText:$("#searchText").val()
			,schSeq:$("#SchSeq").val()
			,pageNo:_pageNo
			,pageSize:String(_pageSize)
		}
		
		$.ajax({
			type : "POST",
			contentType:'application/json; charset=utf-8',
			dataType   : 'json',
			data : JSON.stringify(params),
			url : "/groupware/attendPortal/getMangerAttStatus.do",
			success:function (data) {
				if(data.status=="SUCCESS"){
					if (data.companyCalling)			displayCompanyToday(data.companyToday, data.companyCalling, data.companyCallingCnt);
					if (data.companyDay)				displayCompanyAttInfo(data.StartDate, data.EndDate, data.companyDay);
					if (data.userAttWorkTime)			displayMyAttInfo(data.StartDate, data.EndDate, data.userAttWorkTime);
					if (data.deptAttendList)			displayDeptList(data.TargetDate, data.StartDate, data.EndDate, data.deptAttendList, data.deptAttendPage);
				}else{
					Common.Warning("<spring:message code='Cache.msg_ErrorOccurred'/>");
				}
			},
			error:function (request,status,error){
				Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />"+"code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error)
			}
		});
	}
	//오늘의 회사 근태 현황
	function displayCompanyToday(todayMap, callingList, callingCnt){
		$(".StateList #work_cnt").text(AttendUtils.convertNull(todayMap["WorkCnt"],"0"));
		$(".StateList #late_cnt").text(AttendUtils.convertNull(todayMap["LateCnt"],"0"));
		$(".StateList #absent_cnt").text(AttendUtils.convertNull(todayMap["AbsentCnt"],"0"));
		$(".StateList #vac_cnt").text(AttendUtils.convertNull(todayMap["VacCnt"],"0"));
		$("#commuteCnt").text("("+callingCnt["Cnt"]+")");
		_callStartDate = callingCnt["MinDate"];
		_callEndDate = callingCnt["MaxDate"];
		$(".commuteList ul").html('');
		if (callingList.length == 0){
			$(".commuteList ul").html("<p class='OWList_none'><spring:message code='Cache.msg_NoDataList' /></p>"); //조회할 목록이 없습니다.
		}
		else{
			$.each(callingList, function(idx, item){
				var sRepJobTypeConfig = Common.getBaseConfig("RepJobType");
		        var sRepJobType = item.JobLevelName;
		        if(sRepJobTypeConfig == "PN"){
		        	sRepJobType = item.JobPositionName;
		        } else if(sRepJobTypeConfig == "TN"){
		        	sRepJobType = item.JobTitleName;
		        } else if(sRepJobTypeConfig == "LN"){
		        	sRepJobType = item.JobLevelName;
		        }
		        
				var lbl_ProfilePhoto = "<spring:message code='Cache.lbl_ProfilePhoto' />"; //프로필사진
				var dataMap = {"URName": item["URName"], "UserCode": item["UserCode"], "JobDate":item["JobDate"]};
	
				var sHtml ='<li class="" data-map=\''+JSON.stringify(dataMap)+'\' >'+
							'	<div class="chkStyle08 coChk">';
							
				if (Common.getBaseConfig("isUseMail") == "Y" && Common.getSession('UR_AssignedBizSection').includes("Mail")) {
					sHtml +='		<input type="checkbox" id="a'+idx+'" name="aa">';
				}else{
					sHtml += '		<input type="checkbox" id="a'+idx+'" name="aa" disabled="disabled">';		
				}
				
				sHtml +='		<label for="a'+idx+'"><span class="s_check"></span></label>'+
						'	</div>'+
						'	<div class="coPhoto">';
				
				sHtml+=((item["PhotoPath"]!= null && item["PhotoPath"]!= "")?'<p><img src="'+coviCmn.loadImage(item["PhotoPath"])+'" alt="'+lbl_ProfilePhoto+'" class="mCS_img_loaded" onerror="coviCmn.imgError(this, true)"></p>':'<p class=\"bgColor'+Math.floor(idx%5+1)+'\"><strong>'+item["URName"].substring(0,1)+'</strong></p>');
				
				sHtml+=	'</div>'+
					'<div class="coTxt">'+
					'	<div class="coName"><a href="#" class="">'+item.URName + ' '+ sRepJobType + '('+item.DeptName+')</a></div>'+
					'	<p class="coDate">'+item.JobDate+'</p>'+
					'</div>';
					
				if (item.EndSts!= null && item.EndSts!='lbl_att_normal_offWork'){
					sHtml += '<span class="coState coState2">'+Common.getDic(item.EndSts)+'</span>';
				}	
				else if (item.StartSts!= null && item.StartSts!='lbl_att_normal_goWork'){
					sHtml += '<span class="coState coState2">'+Common.getDic(item.StartSts)+'</span>';
				}
				else{
					sHtml += '<span class="coState coState1">'+Common.getDic("lbl_n_att_absent")+'</span>';
				}
				
				if (Common.getBaseConfig("isUseMail") == "Y" && Common.getSession('UR_AssignedBizSection').includes("Mail")) {
					sHtml +='<a href="#" class="btnBlueType02 btnSend" id="btnSend""><spring:message code="Cache.lbl_Send" /></a>';
				}
				sHtml += '</li>';
				$(".commuteList ul").append(sHtml);
			 }); 
		}	
	
	}
	//기긴별 근태 현황
	function displayCompanyAttInfo(StartDate, EndDate, dataMap){
		$("#companyTit .date").text(AttendUtils.maskDate(StartDate) +" ~ "+AttendUtils.maskDate(EndDate));
		_companyMap["startDate"] = StartDate;
		_companyMap["endDate"] = EndDate;

		$("#companyState #tot_cnt").text(AttendUtils.convertNull(dataMap["UserCnt"],"0"));
		$("#companyState #exten_cnt").text(AttendUtils.convertNull(dataMap["ExtenCnt"],"0")+'<spring:message code="Cache.lbl_personCnt"/>');
		$("#companyState #holi_cnt").text(AttendUtils.convertNull(dataMap["HoliCnt"],"0")+'<spring:message code="Cache.lbl_personCnt"/>');
		$("#companyState #vac_cnt").text(AttendUtils.convertNull(dataMap["VacCnt"],"0")+'<spring:message code="Cache.lbl_personCnt"/>');
		$("#companyState #normal_cnt").text(AttendUtils.convertNull(dataMap["NormalCnt"],"0")+'<spring:message code="Cache.lbl_personCnt"/>');

		var chartData = {datasets : [{data : [],borderWidth:0,backgroundColor : chartColor}]};
		var companyObj = {data : chartData, type:"doughnut",options:{legend: { display: false }}};
		companyObj.data.labels=["<spring:message code='Cache.lbl_att_overtime_work' />","<spring:message code='Cache.lbl_att_holiday_work' />","<spring:message code='Cache.lbl_Vacation' />","<spring:message code='Cache.lbl_attendance_normal' />"]; //"연장근무","휴일근무","휴가","정상"
		if (dataMap["ExtenCnt"] == 0 && dataMap["HoliCnt"] == 0 && dataMap["VacCnt"] == 0) companyObj.data.datasets[0].data=[0,0,0,100];
		else {
			companyObj.data.datasets[0].data=[dataMap["ExtenCnt"],dataMap["HoliCnt"],dataMap["VacCnt"],dataMap["NormalCnt"]];
			//companyObj.options.tooltips={enabled: true};
		}	

		
		if( deptChart ){
			deptChart.destroy();
		}	
		deptChart= new Chart(  $("#companyPie"), companyObj ); 
/*		} else {		
			deptChart.data = companyObj.data;
			deptChart.update();
			deptChart.render(companyObj);
			alert(1);
		}*/
	}
	
	//내 근태현황
	function displayMyAttInfo(StartDate, EndDate, dataMap){
		$("#userTit .date").text(AttendUtils.maskDate(StartDate) +" ~ "+AttendUtils.maskDate(EndDate));
		_userMap["startDate"] = StartDate;
		_userMap["endDate"] = EndDate;

		
		$("#userState #tot_time").text(AttendUtils.convertSecToStr(dataMap["TotWorkTime"],"H"));
		$("#userState #work_time").text(AttendUtils.convertSecToStr(dataMap["AttRealTime"],"H"));
		$("#userState #exten_time").text(AttendUtils.convertSecToStr(dataMap["ExtenAc"],"H"));
		$("#userState #holi_time").text(AttendUtils.convertSecToStr(dataMap["HoliAc"],"H"));
		$("#userState #remain_time").text(AttendUtils.convertSecToStr(dataMap["RemainTime"],"H"));
		
		var chartData = {datasets : [{data : [],borderWidth:0,backgroundColor : chartColor}]};
		var attendObj = {data : chartData, type:"doughnut",options:{legend: { display: false }}};
		attendObj.data.labels=["<spring:message code='Cache.lbl_n_att_normalWork' />","<spring:message code='Cache.lbl_att_overtime_work' />","<spring:message code='Cache.lbl_att_holiday_work' />","<spring:message code='Cache.lbl_n_att_remainWork' />"]; //"정상근무","연장근무","휴일근무","잔여근무"
		attendObj.data.datasets[0].data=[dataMap["AttRealTime"]/60, dataMap["ExtenAc"]/60, dataMap["HoliAc"]/60, dataMap["RemainTime"]/60];
		if( myChart ){
			myChart.destroy();
		}	
		myChart= new Chart(  $("#attendPie"), attendObj ); 
	}

	function printDN(){
		if(_printDN) {
			$(".printmode").each(function () {
				var dnmode = $(this).data('dnmode');
				if ("on" == dnmode) {
					$(this).show();
				} else {
					$(this).hide();
				}
			});
		}else{
			$(".printmode").each(function () {
				var dnmode = $(this).data('dnmode');
				if ("off" == dnmode) {
					$(this).show();
				} else {
					$(this).hide();
				}
			});
		}
	}
	//부서별 근태 리스트
	function displayDeptList(TargetDate, StartDate, EndDate, deptList, deptPage){
		var tabId = "tab-1";
		_endPage= deptPage.pageCount;
		var listCount = Number(deptPage.listCount);
		if (_deptMap["pageType"] == "D"){
			_deptMap["startDate"] = TargetDate;
			_deptMap["endDate"] = TargetDate;
			$("#deptTit .date").text(AttendUtils.maskDate(TargetDate));
			tabId = "tab-1";
			$("#" + tabId + " table tbody").html("");
			//pageing
			$("#custom_navi_messageGrid_D").html(pagging(deptPage));

			var html = "";
			if(listCount<1){
				$("#custom_navi_messageGrid_D").hide();
				html += "<tr><td colspan=\"10\"><spring:message code='Cache.msg_NoDataList' /></td></tr>";
			}else{
				$("#custom_navi_messageGrid_D").show();
				$.each(deptList, function(idx, item){
					var sRepJobTypeConfig = Common.getBaseConfig("RepJobType");
			        var sRepJobType = item.JobLevelName;
			        if(sRepJobTypeConfig == "PN"){
			        	sRepJobType = item.JobPositionName;
			        } else if(sRepJobTypeConfig == "TN"){
			        	sRepJobType = item.JobTitleName;
			        } else if(sRepJobTypeConfig == "LN"){
			        	sRepJobType = item.JobLevelName;
			        }
			        
					html += '<tr>'+
							'<td>' + 
							'<div class="btnFlowerName" onclick="coviCtrl.setFlowerName(this)" style="position:relative;cursor:pointer"; data-user-code="'+ item.UserCode +'" data-user-mail="">' + item.URName + " " + sRepJobType + '</div>' +
							'</td>' +
							'<td>'+item.DeptName+'</td>'+
							'<td>'+AttendUtils.convertNull(item.SchName,'')+'</td>'+
							'<td>'+AttendUtils.convertNull(item.AttStartTime,'')+'</td>'+
							'<td>'+AttendUtils.convertNull(item.AttEndTime,'')+'</td>';
					html += '<td>' +
							'<span class="printmode" data-dnmode="off">'+AttendUtils.convertSecToStr(item.AttAc,"H")+'</span>' +
							'<span class="printmode" data-dnmode="on">'+AttendUtils.convertSecToStr(item.AttAcD,"H")+' /'+AttendUtils.convertSecToStr(item.AttAcN,"H")+'</span>' +
							'</td>';
					html += '<td>' +
							'<span class="printmode" data-dnmode="off">'+AttendUtils.convertSecToStr(item.ExtenAc,"H")+'</span>' +
							'<span class="printmode" data-dnmode="on">'+AttendUtils.convertSecToStr(item.ExtenAcD,"H")+' /'+AttendUtils.convertSecToStr(item.ExtenAcN,"H")+'</span>' +
							'</td>';
					html += '<td>' +
							'<span class="printmode" data-dnmode="off">'+AttendUtils.convertSecToStr(item.HoliAc,"H")+'</span>' +
							'<span class="printmode" data-dnmode="on">'+AttendUtils.convertSecToStr(item.HoliAcD,"H")+' /'+AttendUtils.convertSecToStr(item.HoliAcN,"H")+'</span>' +
							'</td>';
					html += '<td>'+AttendUtils.convertSecToStr(item.LateMin,"H")+'</td>'+
							'<td>'+AttendUtils.convertSecToStr(item.EarlyMin,"H")+'</td>'+
							'<td>'+AttendUtils.convertSecToStr(item.AttIdle,"H")+'</td>'+
					'</tr>';
				});
			}
			$("#"+tabId+"  table tbody").html(html);
		}
		else{
			_deptMap["startDate"] = StartDate;
			_deptMap["endDate"] = EndDate;
			$("#deptTit .date").text(AttendUtils.maskDate(StartDate) +" ~ "+AttendUtils.maskDate(EndDate));
			tabId = "tab-2";
			$("#"+tabId+" table tbody").html("");

			$("#custom_navi_messageGrid_M").html(pagging(deptPage));
			if(listCount<1){
				$("#custom_navi_messageGrid_M").hide();
				var html = "";
				html += "<tr height=\"43\"><td colspan=\"8\"><spring:message code='Cache.msg_NoDataList' /></td></tr>";
				$("#" + tabId + "  table tbody").append(html);
			}else {
				$("#custom_navi_messageGrid_M").show();
				$.each(deptList, function (idx, item) {
					var html = "";
					var sRepJobTypeConfig = Common.getBaseConfig("RepJobType");
			        var sRepJobType = item.JobLevelName;
			        if(sRepJobTypeConfig == "PN"){
			        	sRepJobType = item.JobPositionName;
			        } else if(sRepJobTypeConfig == "TN"){
			        	sRepJobType = item.JobTitleName;
			        } else if(sRepJobTypeConfig == "LN"){
			        	sRepJobType = item.JobLevelName;
			        }
			        
					html += '<tr>' +
							'<td>' + 
							'<div class="btnFlowerName" onclick="coviCtrl.setFlowerName(this)" style="position:relative;cursor:pointer"; data-user-code="'+ item.UserCode +'" data-user-mail="">' + item.URName + " " + sRepJobType + '</div>' +
							'</td>' +
							'<td>' + item.DeptName + '</td>';
					html += '<td>' +
							'<span class="printmode" data-dnmode="off">' + AttendUtils.convertSecToStr(item.AttAc, "H") + '</span>' +
							'<span class="printmode" data-dnmode="on">' + AttendUtils.convertSecToStr(item.AttAcD, "H") + ' /' + AttendUtils.convertSecToStr(item.AttAcN, "H") + '</span>' +
							'</td>';
					html += '<td>' +
							'<span class="printmode" data-dnmode="off">' + AttendUtils.convertSecToStr(item.ExtenAc, "H") + '</span>' +
							'<span class="printmode" data-dnmode="on">' + AttendUtils.convertSecToStr(item.ExtenAcD, "H") + ' /' + AttendUtils.convertSecToStr(item.ExtenAcN, "H") + '</span>' +
							'</td>';
					html += '<td>' +
							'<span class="printmode" data-dnmode="off">' + AttendUtils.convertSecToStr(item.HoliAc, "H") + '</span>' +
							'<span class="printmode" data-dnmode="on">' + AttendUtils.convertSecToStr(item.HoliAcD, "H") + ' /' + AttendUtils.convertSecToStr(item.HoliAcN, "H") + '</span>' +
							'</td>';
					html += '<td>' + AttendUtils.convertSecToStr(item.LateMin, "H") + '</td>' +
							'<td>' + AttendUtils.convertSecToStr(item.EarlyMin, "H") + '</td>' +
							'<td>' + AttendUtils.convertSecToStr(item.AttIdle,"H")+'</td>'+
							'<td><a class="btnTypeDefault btnSearchDetail" data-map=\'' + JSON.stringify(item) + '\' ><spring:message code="Cache.lbl_DetailView"/></a></td>' +
							'</tr>';
					$("#" + tabId + "  table tbody").append(html);
				});
			}

		}
		_userMap["StartDate"] = StartDate;
		_userMap["EndDate"] = EndDate;
		printDN();
	}
	
	$(document).on("click",".btnSearchDetail",function(){
		var dataMap = JSON.parse($(this).attr("data-map"));
		/*if (dataMap["JobDate"] == null)
		{
			Common.Inform("지정일에 근무가 없습니다.");	
			return;
		}*/
		var popupID		= "AttendPortalDetailPopup";
		var openerID	= "AttendPortalDetail";
		var popupTit	= "<spring:message code='Cache.lbl_WeeklyMonthlyReport' />"; //근태주보/월보
		var popupYN		= "N";
		var popupUrl	= "/groupware/attendPortal/AttendPortalDetailPopup.do?"
						+ "popupID="		+ popupID	+ "&"
						+ "openerID="		+ openerID	+ "&"
						+ "popupYN="		+ popupYN	+ "&"
						+ "UserName="		+ encodeURIComponent(dataMap["URName"])	+ "&"
						+ "UserCode="		+ dataMap["UserCode"]	+ "&"
						+ "StartDate="		+ _deptMap["startDate"]	+ "&"
						+ "EndDate="		+ _deptMap["endDate"]	+ "&"
						+ "printDN="		+ _printDN	+ "&"
						+ "DeptName="		+ encodeURIComponent(dataMap["DeptName"])	+ "&"
		
		Common.open("", popupID, popupTit, popupUrl, "1124px", "700px", "iframe", true, null, null, true);
		
	});
	
	
	//선택발송
	$(document).on("click","#btnSelSend", function(){
		var toUsers="";
		var aJsonArray = new Array();

		$(".commuteList input:checked").each(function() {
			var dataMap = JSON.parse($(this).closest("li").attr("data-map"));
			var saveData ={ "UserCode":dataMap["UserCode"], "JobDate":dataMap["JobDate"]};
			
			toUsers += (i>0?", ":"")+ dataMap["URName"];
			aJsonArray.push(saveData);
		});
		
		if(aJsonArray.length == 0){
			Common.Warning("<spring:message code='Cache.msg_SelectTarget'/>");
			return false;
		}
		Common.Confirm("<spring:message code='Cache.msg_apv_191' /> [To "+toUsers+"]", "Confirmation Dialog", function (confirmResult) {
			if (confirmResult) {
				var sendParam = {"dataList" : aJsonArray }
				sendMail(sendParam);
			}
		});
	});
		
	//개별발송
	$(document).on("click","#btnSend",function(){
		var aJsonArray = new Array();

		var dataMap = JSON.parse($(this).closest("li").attr("data-map"));
		Common.Confirm("<spring:message code='Cache.msg_SendQ' />  [To "+dataMap["URName"]+"]", "Confirmation Dialog", function (confirmResult) {
			if (confirmResult) {
				var aJsonArray = new Array();
				var saveData ={ "UserCode":dataMap["UserCode"], JobDate:dataMap["JobDate"]};
				aJsonArray.push(saveData);
				var sendParam = {"dataList" : aJsonArray }
				sendMail(sendParam);
			}
		});
		
	})
	
	/*$(".mScrollVH").scroll(function(){
        var scrollTop = $(this).scrollTop();
        var innerHeight = $(this).innerHeight();
        var scrollHeight = $(this).prop('scrollHeight');

        if (scrollTop + innerHeight >= scrollHeight) {
        	if (_pageNo+1 <= _endPage){
        		getCompanyAttInfo('D', "", (_pageNo+1));
        	}	
        } 
	});*/
	
	function sendMail(sendParam){
		$.ajax({
		    url: "/groupware/attendPortal/sendMessageTarget.do",
		    type: "POST",
			contentType:'application/json; charset=utf-8',
			dataType   : 'json',
		    data: JSON.stringify(sendParam),
		    success: function (res) {
		    	if(res.status == "SUCCESS"){
		    		Common.Inform("<spring:message code='Cache.lbl_Mail_SendCompletion'/>[<spring:message code='Cache.lbl_SendNumber' />: "+res.sendCnt+"]"); //전송 건수
		    	}else{
		    		Common.Error("<spring:message code='Cache.msg_apv_030'/>");		// 오류가 발생했습니다.
		    	}
	        },
	        error:function(response, status, error){
				CFN_ErrorAjax("/covicore/control/sendSimpleMail.do", response, status, error);
			}
		});
	}

</script>	