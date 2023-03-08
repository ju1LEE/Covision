<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/WEB-INF/views/cmmn/CoreInclude.jsp"></jsp:include>

<form id="JobInfoForm">
	<div id="popBox" >
	    <div class="AXTabsLarge" id="LayoutDiv" >
	        <div id="divJobDefault" class="TabBox" style="padding-bottom: 20px !important;">
	            <table class="AXFormTable">
			      <colgroup>
				    <col id="t_tit" style="width:17%;">
				    <col id="" style="width:33%;">
				    <col id="t_tit2" style="width:17%;">
				    <col id="" style="width:33%;">
			      </colgroup>
			      <tbody>
	               <tr>
			          <th><spring:message code='Cache.lbl_JobName'/></th>
				      <td colspan="3">
	                    <input type="text" ID="txtJobTitle" class="HtmlCheckXSS ScriptCheckXSS" style="float:left; width: 98%;"> 
	                  </td>
			        </tr>
	                <tr>
			          <th scope="col"><spring:message code='Cache.lbl_JobKind'/></th>
				      <td scope="col">
				      	<select id="ddlJobType" style="width: 95%;">
		               		<option selected="selected" value="WebService">웹서비스</option>
							<option value="Procedure">저장 프로시져</option>
	                   	</select>
	                  </td>
				      <th scope="col"><spring:message code='Cache.lbl_IsUse'/>(<spring:message code='Cache.lbl_Run'/>)</th>
				      <td scope="col">
	                    <select id="ddlIsUse" style="width: 95%;">
	                    	<option selected="selected" value="Y"><spring:message code='Cache.lbl_apv_formcreate_LCODE13'/></option>
							<option value="N"><spring:message code='Cache.lbl_apv_formcreate_LCODE14'/></option>
	                    </select>
	                   <select id="ddlAgentServer" style="width: 40%;display:none">
	                   </select> 
	                  </td>
			        </tr>
	                <tr>
			          <th><spring:message code='Cache.lbl_Path'/>(URL)</th>
				      <td colspan="3">
	                    <input type="text" ID="txtPath" class="HtmlCheckXSS ScriptCheckXSS" style="float:left; width:98%;"> 
	                  </td>
			        </tr>
			        <tr>
				      <th scope="col"><spring:message code='Cache.lbl_Enterprise_By'/>(<spring:message code='Cache.lbl_apv_formcreate_LCODE13'/>)</th>
				      <td scope="col">
	                    <select id="ddlIsCopy" style="width: 55%;">
	                    	<option selected="selected" value="Y"><spring:message code='Cache.lbl_apv_formcreate_LCODE13'/></option>
							<option value="N"><spring:message code='Cache.lbl_apv_formcreate_LCODE14'/></option>
	                    </select>
	                  </td>
			        </tr>
	                <tr>
			          <th scope="col"><spring:message code='Cache.lbl_TimeOut'/></th>
				      <td scope="col">
	                  	<input type="text" ID="txtTimeOut" InputMode="Numberic" MaxLength="3" value="10" style="width:40px;"><span>(Sec)</span>
	                  </td>
				      <th scope="col" ><spring:message code='Cache.lbl_RunCnt'/></th>
				      <td scope="col">
	                    <input type="text" ID="txtRepeatCnt" InputMode="Numberic" MaxLength="3" value="0" style="width:40px;"> <span>※ 0 : <spring:message code='Cache.lbl_NotLimite'/></span>
	                  </td>
			        </tr>
	                <tr>
			          <th><spring:message code='Cache.lbl_FailRetryCnt'/></th>
				      <td colspan="3">
	                    <input type="text" ID="txtRetryCnt" InputMode="Numberic" MaxLength="1" value="0" style="width:40px;">  <span>※ <spring:message code='Cache.msg_FailRetryCount'/></span>
	                  </td>
			        </tr>
	                <tr>
			          <th ><spring:message code='Cache.lbl_Description'/></th>
				      <td colspan="3">
	                    <textarea class="AXTextarea HtmlCheckXSS ScriptCheckXSS" ID="txtDescription" Rows="3" TextMode="MultiLine" style="width: 96%; overflow-x:hidden; resize:none;"></textarea>
	                  </td>
			        </tr>
	               <tr>
			          <th><spring:message code='Cache.lbl_name'/></th>
				      <td colspan="3">
	                    <input type="text" ID="txtScheduleTitle" class="HtmlCheckXSS ScriptCheckXSS" style="float:left; width:98%;" > 
	                  </td>
			        </tr>
			        <tr>
			        	<th>일정
			        		<input type="button" class="AXButton red" id="btnSch" value="<spring:message code="Cache.lbl_apply"/>"/>
			        		
			        		</th>
			        	<td colspan=3>
			        		 <table class="AXFormTable">
			        		 	<tr>
			        		 		<th>구분</th>
			        		 		<th></th>
			        		 		<th>특정시간</th>
			        		 		<th>반복</th>
			        		 	</tr>
			        		 	<tr>
			        		 		<th><span class=cronName>초</span></th>
			        		 		<td><span style=display:none>매초실행<input type=checkbox name=allTime value="*"></span></td>
			        		 		<td>
					                    <input type="text" name="fixTime" data-max="59" InputMode="Numberic" MaxLength="3" value="0" style="width:40px;"><span> (Sec)</span>
			        		 		</td>
			        		 		<td>
						      			<select name="termTime" style="width:70px;" data-start="0">
											<option value=""/>반복없음</option>
						      				<c:forEach begin="5" end="55" var="time" step="5">
												<option value="${time}"/>${time} (Sec) 반복</option>
											</c:forEach>
					                   </select>
			        		 		</td>
			        		 	</tr>	
			        		 	<tr>
			        		 		<th><span class=cronName>분</span></th>
			        		 		<td>매분실행<input type=checkbox name=allTime value="*"></td>
			        		 		<td>
					                    <input type="text" name="fixTime" data-max="59" InputMode="Numberic" MaxLength="3" value="0" style="width:40px;"><span> (Min)</span>
			        		 		</td>
			        		 		<td>
						      			<select name="termTime"  style="width:70px;" data-start="0">
											<option value=""/>반복없음</option>
						      				<c:forEach begin="1" end="55" var="time" step="1">
												<option value="${time}"/>${time} (Min) 반복</option>
											</c:forEach>
					                   </select>
			        		 		</td>
			        		 	</tr>
			        		 	<tr>
						        	<th><span class=cronName>시</span></th>	
			        		 		<td>매시실행<input type=checkbox name=allTime value="*"></td>
			        		 		<td>
					                    <input type="text" name="fixTime"  data-max="23" InputMode="Numberic" MaxLength="3" value="0" style="width:40px;"><span> (Hour)</span>
			        		 		</td>
			        		 		<td>
						      			<select name="termTime"  style="width:70px;" data-start="0">
											<option value=""/>반복없음</option>
						      				<c:forEach begin="1" end="23" var="time" step="1">
												<option value="${time}"/>${time} (Hour) 반복</option>
											</c:forEach>
					                   </select>
			        		 		</td>
						         </tr> 
			        		 	<tr>
						        	<th><span class=cronName>일</span></th>	
						        	<td>매일실행<input type=checkbox name=allTime value="*"></td>
			        		 		<td>
					                    <input type="text" name="fixTime"  data-max="31" InputMode="Numberic" MaxLength="3" value="1" style="width:40px;"><span> (Day)</span>
			        		 		</td>
			        		 		<td>
						      			<select name="termTime"  style="width:70px;" data-start="1">
											<option value=""/>반복없음</option>
						      				<c:forEach begin="1" end="31" var="time" step="1">
												<option value="${time}"/>${time} (Day) 반복</option>
											</c:forEach>
					                   </select>
			        		 		</td>
						         </tr> 
			        		 	<tr>
						        	<th><span class=cronName>월</span></th>	
						        	<td>매월실행<input type=checkbox name=allTime value="*"></td>
			        		 		<td>
					                    <input type="text" name="fixTime"  data-max="12"  InputMode="Numberic" MaxLength="3" value="1" style="width:40px;"><span> (Month)</span>
			        		 		</td>
			        		 		<td>
						      			<select name="termTime"  style="width:70px;"  data-start="1">
											<option value=""/>반복없음</option>
						      				<c:forEach begin="1" end="12" var="time" step="1">
												<option value="${time}"/>${time} (Month) 반복</option>
											</c:forEach>
					                   </select>
			        		 		</td>
						         </tr> 
			        		 </table>		
			        	</td>
			        </tr>
			        <tr id="trCron">
			       	  <th>Cron</th>
				      <td colspan="3">
	                    <input type="text" ID="txtCronExpression" class="HtmlCheckXSS ScriptCheckXSS" style="float:left; width:98%;" > 
	                  </td>
			        </tr>
	              </tbody>
			    </table>
	        </div>
			<div style="width: 100%; text-align: center;">
				<input type="button" class="AXButton red" id="btnAdd" value="<spring:message code="Cache.btn_Add"/>" onclick="btnAdd_Click();"/>
				<input type="button" class="AXButton red" id="btnModify" value="<spring:message code="Cache.btn_Modify"/>" onclick="btnModify_Click();" style="display: none;"/>
				<input type="button" class="AXButton" id="btnClose" value="<spring:message code="Cache.btn_Close"/>" onclick="closeLayer();"/>
			</div>
			<!-- hidden -->
			<input type="hidden" id="hidSchID">
		  </div>
	</div>
</form>
<script  type="text/javascript">	
	var paramEventID = "${EventID}";	
	var mode = "${mode}";
	var doublecheck = false;
	var _cbe = new Object();
	
	$(document).ready(function(){		
		init();	
	});
	
	function init(){
		$(document).on("keyup", "input:text[InputMode]", function() {
			if($(this).attr('InputMode')=="Numberic"){
				$(this).val( $(this).val().replace(/[^0-9]/gi,"") );
			}
			if($(this).attr('InputMode')=="Alphabetic"){
			
			}
		});
		
		//매설정시 특정시간. 간격은 선택못함
		$(document).on("click", "input[name=allTime]", function(){
			var idx = $("input[name=allTime]").index($(this));
			$("input[name=fixTime]").eq(idx).attr('disabled', $(this).is(':checked'));
			$("select[name=termTime]").eq(idx).attr('disabled', $(this).is(':checked'));
		});

		//고정시간 선택시
		$(document).on("change", "input[name=fixTime]", function(){
			var idx = $("input[name=fixTime]").index($(this));
			if ($(this).val() != ""){
				$("select[name=termTime]").eq(idx).val("");
			}
		});

		//고정시간 입력시 max값 체크
		$(document).on("blur", "input[name=fixTime]", function(){
			if  (parseInt ($(this).attr("data-max"), 10) < parseInt($(this).val(),10)   ){
				Common.Warning("<spring:message code='Cache.ACC_032'/>");
				$(this).val("");
			}
		});

		
		//반복시간 선택시
		$(document).on("change", "select[name=termTime]", function(){
			var idx = $("select[name=termTime]").index($(this));
			if ($(this).val() != ""){
				$("input[name=fixTime]").eq(idx).val("");
			}
		});

		$("#btnSch").click( function(){
			var cron = "";
			var cronTitle = "";
			$("input[name=allTime]").each(function(index, item){ 
				if (index > 0) {
					cron += " ";
					cronTitle = " " + cronTitle;
				}
				if ($("input[name=allTime]").eq(index).is(':checked')){
					cron += "*";
					cronTitle = "매"+$(".cronName").eq(index).text() + cronTitle;
				}
				else{
					if ($("input[name=fixTime]").eq(index).val() != ""){
						cron += $("input[name=fixTime]").eq(index).val();
						cronTitle = $("input[name=fixTime]").eq(index).val() + $(".cronName").eq(index).text() + cronTitle;
					}
					else{
						cron += $("select[name=termTime]").eq(index).attr("data-start")+"/"+$("select[name=termTime]").eq(index).val();
						cronTitle = $("select[name=termTime]").eq(index).val() + $(".cronName").eq(index).text() + "간격" + cronTitle;
					}
				}
					
			})
			
			cron +=" ? *";
			Common.Confirm("["+cronTitle+"] <spring:message code='Cache.ACC_msg_ckApply' />", "Confirmation Dialog", function (confirmResult) {
				if (confirmResult) {
					$("#txtCronExpression").val(cron);
					$("#txtScheduleTitle").val(cronTitle);
				}	
			});				
		});
		
		setSelectBox();
		
		if(mode == "modify"){
			setData();
			$("#btnModify").css("display", "");
		}
		
	}
	
	function setData(){
		$.ajax({
			type:"POST",
			data: {"EventID" : paramEventID},
			url:"/covicore/admin/jobscheduler/getJobSchedulerEventData.do",
			success:function (json) {
				
				var cronExpr = json.data[0].CronExpr;
				
				if (cronExpr != ""){
					var times = cronExpr.split(" ");
					$(times).each(function(index, item){ 
						if (item == "*"){
							$("input[name=allTime]").eq(index).attr("checked", true);
							$("input[name=fixTime]").eq(index).attr('disabled', true);
							$("select[name=termTime]").eq(index).attr('disabled', true);
						}
						else{
							if (item.indexOf("/")>0){	//간격
								$("input[name=fixTime]").eq(index).val("");
								$("select[name=termTime]").eq(index).val(item.split("/")[1]);
							}
							else{
								$("input[name=fixTime]").eq(index).val(item);
								$("select[name=termTime]").eq(index).val("");
							}
						}
					});					
					
				}
				$("#txtJobTitle").val(json.data[0].JobTitle);
				$("#ddlJobType").val(json.data[0].JobType);
		        $("#ddlIsUse").val(json.data[0].IsUse);
		        $("#ddlIsCopy").val(json.data[0].IsCopy);
				$("#ddlAgentServer").val(json.data[0].AgentServer);

		        $("#txtTimeOut").val(json.data[0].TimeOut);
		        $("#txtRepeatCnt").val(json.data[0].RepeatCnt);
		        $("#txtRetryCnt").val(json.data[0].RetryCnt);
		        $("#txtPath").val(json.data[0].Path);
		        $("#txtCommandText").val(json.data[0].CommandText);
		        $("#txtDescription").val(json.data[0].Description);
		        
		     	// 일정
		        $("#txtScheduleTitle").val(json.data[0].ScheduleTitle);
		     	// 일정 유형
		     	$("#txtCronExpression").val(json.data[0].CronExpr) // 일정 크론 표현식
			},
			error:function (error){
				Common.Error(error.message);
			}
		});
	}
	
	//셀렉트박스
	function setSelectBox(){	
		
		$.ajax({
			url:"/covicore/admin/jobscheduler/getagentserverlist.do",
			type:"post",
			data:{"pageNo": 1, "pageSize": 1000},
			async:false,
			success:function (data) {				
				$(data.list).each(function(index){
					$("#ddlAgentServer").append("<option value='"+this.AgentServer+"'>"+this.AgentServer+"</option>");
				});					
			}				
		});
		
	}
	
    function make_cbe(){    	
    	// 작업 정보
		_cbe.EventID = paramEventID; 
        _cbe.JobTitle = $("#txtJobTitle").val();
        _cbe.JobType = $("#ddlJobType").val();
        _cbe.IsUse = $("#ddlIsUse").val();
        _cbe.IsCopy = $("#ddlIsCopy").val();
        _cbe.AgentServer = $("#ddlAgentServer").val();
        _cbe.TimeOut = $("#txtTimeOut").val();
        _cbe.RetryCnt = $("#txtRetryCnt").val();
        _cbe.RepeatCnt = $("#txtRepeatCnt").val();
        _cbe.Path = $("#txtPath").val();
        _cbe.CommandText = $("#txtCommandText").val();
        _cbe.Description = $("#txtDescription").val();

        // 일정 정보
        _cbe.ScheduleTitle = $("#txtScheduleTitle").val(); // 일정명
        _cbe.CronExpr = $("#txtCronExpression").val(); // cron 표현식
       
    }
    
    // 작업 클러스터 추가
    function btnAdd_Click(){    	
    	if(!ValidationCheck()){
    		return;
    	}
    	
    	make_cbe();    
    	
      	//insert 호출
		$.ajax({
			type:"POST",
			data: _cbe,
			url:"/covicore/admin/jobscheduler/insertJobSchedulerEvent.do",
			success:function (data) {
				if(data.result == "ok"){
					Common.Inform("<spring:message code='Cache.msg_DeonRegist'/>", "Information", function(){
						closeLayer();
						parent.bindGridData();
					});				
				}else{
					Common.Warning("<spring:message code='Cache.CPMail_error_msg'/>");
				}
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/covicore/admin/jobschedulerevent/insertJobSchedulerEvent.do", response, status, error);
			}
		}); 

    }
    
 	// 작업 클러스터 수정
    function btnModify_Click(){    	
    	if(!ValidationCheck()){
    		return;
    	}
    	
    	make_cbe();    	
    	      
      	//insert 호출
		$.ajax({
			type:"POST",
			data: _cbe,
			url:"/covicore/admin/jobscheduler/updateJobSchedulerEvent.do",
			success:function (data) {
				if(data.result == "ok"){
					Common.Inform("<spring:message code='Cache.msg_DeonModify'/>", "Confirmation Dialog", function () {;				
						closeLayer();
						parent.bindGridData();
					});
				}else{
					Common.Warning(data.message);
				}
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/covicore/admin/jobscheduler/updateJobSchedulerEvent.do", response, status, error);
			}
		}); 

    }

    
 	// 벨리데이션 체크
    function ValidationCheck() {
    	if (!XFN_ValidationCheckOnlyXSS(false)) { return; }

        // 작업명
        if ($("#txtJobTitle").val() == "") {
            Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtJobTitle").focus();
            
            return false;
        }

        // 타임아웃
        if ($("#txtTimeOut").val() == "" || $("#txtTimeOut").value == "0") {
            Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtTimeOut").focus();
            return false;
        }
        // 경로 체크
        if ($("#txtPath").val() == "") {
        	Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtPath").focus();

            return false;
        }
        // 웹서비스인 경우 Method가 필수 입력 값임.
    	/* if ($("#ddlJobType").val() == "WebService" && $("#txtPath").val() == "") {
        	Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtMethod").focus();

            return false;
        } */
        // 실행 순서
        if ($("#txtSeq").val() == "") {
        	Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtSeq").focus();

            return false;
        }
        // 실행 횟수
        if ($("#txtRepeatCnt").val() == "") {
        	Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtRepeatCnt").focus();

            return false;
        }
        // 실패시 재처리 시도
        if ($("#txtRetryCnt").val() == "") {
        	Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtRetryCnt").focus();

            return false;
        }

        // 일정명
        if ($("#txtScheduleTitle").val() == "") {
        	Common.Warning("<spring:message code='Cache.msg_EnterValueInField'/>");
                $("#txtScheduleTitle").focus();

            return false;
        }

        
        return true;
    }
 	
 	// 레이어 팝업 닫기
	function closeLayer(){
		Common.Close();
	}
</script>