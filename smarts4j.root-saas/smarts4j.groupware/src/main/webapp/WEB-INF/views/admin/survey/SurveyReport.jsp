<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>    
<jsp:include page="/WEB-INF/views/cmmn/AdminInclude.jsp"></jsp:include>
<style>	
	.colHeadTable, .gridBodyTable {
		font-size: 13px;
	}
</style>
<div style="width:100%;" id="listBtn">
    	<div id="topitembar01" class="topbar_grid">
			<input type="button" class="AXButton" onclick="goSurveyManage()" value="목록">
		</div>
</div>
<table class="tableStyle linePlus AXFormTable" >
	<colgroup>
		<col style="width:18%">
		<col style="width:16%">
		<col style="width:18%">
		<col style="width:16%">
		<col style="width:18%">
		<col style="width:16%">											
	</colgroup>
	<tbody>
		<tr>
			<th>제목</th>
			<td colspan="3" id="subject" style="height:27px;"></td>
			<th>총 참여자 수</th>
			<td id="respondentCnt"></td>
		</tr>
		<tr>
			<th>부서/직급별 통계</th>
			<td>
				<select id="reqTypesel" class="AXSelect">
				    <option value="all">전체</option>
				    <option value="job">직급</option>
				    <option value="dept">부서</option>
				</select>
			</td>
			<th>사용자별통계</th>
			<td id="userReportBtn">
				<input type="button" class="AXButton" onclick="userReport()" value="사용자통계">
			</td>
			<th>설문참여자통계</th>
			<td>
				<input type="button" class="AXButton" onclick="search('respondent')" value="참여자통계">
			</td>											
		</tr>											
	</tbody>
</table>

<div class="topbar_grid" style=" background-color: #fff; margin-top: 10px;  padding: 17px !important;">
	<div class='topbar_grid' style="display:none" id="searchBar">
		<input type='button' class='AXButton BtnRefresh' onclick='search("respondent")' value='새로고침'>&nbsp;&nbsp;&nbsp;
		 <select id='targetJoinSel' class='AXSelect' onchange='search("respondent")' style='float: right;'>
			<option value=''>선택</option>
			<option value='attendance'>참여</option>
			<option value='nonAttendance'>미참여</option>
		</select> 
		<select id='targetTypeSel' class='AXSelect'></select> 
		<input type='text' id='schTxt' onkeypress='if (event.keyCode==13){ search("respondent"); return false;}' class='AXInput HtmlCheckXSS ScriptCheckXSS' />
		<input type='button' id='schBtn' class='AXButton' onclick='search("respondent")' value='검색'>
	</div>
	<div  id="reportDiv" >
	</div>
</div>
					
<script>
	//#sourceURL=SurveyReport.jsp
	var surveyId = CFN_GetQueryString("surveyId");
	var type = CFN_GetQueryString("type");
	
	var grid = new coviGrid();
	var reqType = "all";
	
	var IsAnonymouse = false;
	init();

	// 설문 통계보기
	function init(){
		if(type.toUpperCase() == "USER"){
			$("#listBtn").hide();
		}
		
		search('report');
		
		$("#targetTypeSel").bindSelect({ //검색 조건
			options: [{'optionValue':'ur','optionText':'참여자'},{'optionValue':'gt','optionText':'부서'}]
		});
		
		$("#targetJoinSel").bindSelect({ //참여 여부
			options: [{'optionValue':'','optionText':'전체'},{'optionValue':'attendance','optionText':'참여'},{'optionValue':'nonAttendance','optionText':'미참여'}]
		});
		
	}

	$(function() {
		// 승인 거부 버튼
		$("#reqTypesel").change(function() {
			reqType = $(this).val();
		
			search('report');	// 검색
		});
	});
	
	// 검색
	function search(type) {
		var params = {surveyId : surveyId,
					 targetType : $("#targetTypeSel").val(),
					 schTxt : $("#schTxt").val(),
					  targetJoin : $("#targetJoinSel").val(),
					  reqType : reqType};
		
		if (type == 'report') {
			var url = "/groupware/survey/getSurveyReportList.do";
	 		$.ajax({
				type : "POST",
				data : params,
				url : url,
				success:function (data) {
					if(data.result == "ok") {
						if(data.status == 'SUCCESS') {
							if(data.list.survey[0].IsAnonymouse == "Y"){
								$("#userReportBtn").html("<spring:message code='Cache.msg_AnonymousQuestion'/>");
								IsAnonymouse = true;
							}
							setReportHtml(data.list);	// 통계 html
		          		} else {
		          			Common.Warning("<spring:message code='Cache.msg_apv_030'/>");
		          		}
					}
				},
				error:function(response, status, error) {
					CFN_ErrorAjax(url, response, status, error);
				}
			});
		} else if(type == 'respondent') {	//익명 설문이면 참여자 표시 X
			if(IsAnonymouse){
				$("#searchBar").show();
				$("#targetTypeSel").hide();
				$("#AXselect_AX_targetTypeSel").hide();
				$("#schBtn").hide();
				$("#schTxt").hide();
				
				$("#reportDiv").empty();
				$("#reportDiv").html('<div id="gridDiv"></div>');
				
				grid.setGridHeader( [{key:'RespondentID', label:'대상자', width:'15', align:'center'},
										{key:'RegistDate', label:'참여여부', width:'15', align:'center', formatter: function(){
					              			if(this.item.RegistDate == null || typeof(this.item.RegistDate) == 'undefined'){
					              				return "미참여"
					              			}else{
					              				return CFN_TransLocalTime(this.item.RegistDate);
					              			}
					              		}},
					              		{key:'EtcOpinion', label:'의견', width:'65', align:'center'}]);
				
				grid.setGridConfig( {
					targetID : "gridDiv",
					height:"auto",
					paging: true,
					page : {
						pageNo:1,
						pageSize:10
					}
				});
				
	 	 		grid.bindGrid({
					ajaxUrl : "/groupware/survey/getTargetRespondentList.do",
					ajaxPars : params
				});
			}else{
				$("#searchBar").show();
				$("#reportDiv").empty();
				$("#reportDiv").html('<div id="gridDiv"></div>');
				
				grid.setGridHeader( [{key:'DisplayName', label:"<spring:message code='Cache.lbl_Participation_User'/>", width:'10', align:'center'},
					              		{key:'DeptName', label:"<spring:message code='Cache.lbl_apv_dept'/>", width:'10', align:'center'},
					              		{key:'RegistDate', label:"<spring:message code='Cache.lbl_Participation_Check'/>", width:'15', align:'center', formatter: function(){
					              			if(this.item.RegistDate == null || typeof(this.item.RegistDate) == 'undefined'){
					              				return "<spring:message code='Cache.lbl_Nonparticipation'/>"
					              			}else{
					              				return CFN_TransLocalTime(this.item.RegistDate);
					              			}
					              		}},
					              		{key:'EtcOpinion', label:"<spring:message code='Cache.lbl_comment'/>", width:'65', align:'center'}]);

				
				grid.setGridConfig( {
					targetID : "gridDiv",
					height:"auto",
					paging: true,
					page : {
						pageNo:1,
						pageSize:10
					}
				});
				
	 	 		grid.bindGrid({
					ajaxUrl : "/groupware/survey/getTargetRespondentList.do",
					ajaxPars : params
				});
			}
		}
	}

 	// 통계 html
    function setReportHtml(list) {
 		$("#subject").html(list.survey[0].Subject);
 		$("#respondentCnt").html(list.survey[0].respondentCnt);
 		
 		var isNType = false;	// 순위선택 문항 존재 여부
 		var questions = list.questions;
 		var itemRecords = list.itemRecords;
 		var calcItemWidth = 0;
 		
 		$("#searchBar").hide();
		$("#reportDiv").empty();
		
    	var html = "";
    	html += "<tbody>";
    	html += "<tr>";
    	html += "<th rowspan='2' style='width:70px'>";
    	html += "문항";
    	html += "</th>";
    	
    	var titleHtml1 = "";
    	var titleHtml2 = "";
    	
    	//타이틀 생성
		$(questions).each(function() {
	    	if (this.questionType == 'N') isNType = true;
			
	    	var colspan = 0;
	    	if (reqType == 'user') {
	    		colspan = this.items.length;
	    	} else {
	    		colspan = this.itemLen;
	    	}
	    	
	    	if(this.items !== undefined && this.items.length > 0) {
	    		calcItemWidth += this.items.length;
	    	}
	    	
	    	
	    	if(this.groupingNo != null && this.groupingNo != "0" && this.groupingNo != 0){
		    	titleHtml1 += "<th colspan='" + colspan + "'  class='center'>" + this.groupingNo + "-" + this.questionNo + "</th>";
	    	}else{
	    		titleHtml1 += "<th colspan='" + colspan + "'  class='center'>" + this.questionNo + "</th>";
	    	}
	    	titleHtml2 += "<td colspan='" + colspan + "'>" + this.questionTypeNm + "</td>";
		});
		
    	html += titleHtml1+ "</tr>";
    	html += "<tr>"+titleHtml2+ "</tr>";
    	
     	if (reqType == 'user') {
    		html += "<tr>";
    		html += "<th>" + "사용자" + "</th>";
    		
    		$(questions).each(function() {
     	    	$(this.items).each(function() {
    	    		html += "<td>" + this.itemNo + "</td>";		
    	    	});
    		}); 
    		
    		html += "</tr>";
    		
     		$(itemRecords).each(function() {
     			html += "<tr>";
     			html += "<td>" + this.userNm + "</td>";
     			
    	    	$(this.questionRecords).each(function() {
    	    		var questionType = this.questionType;
         	    	$(this.items).each(function() {
         	    		var cnt = this.cnt;
						var str = "";         	    		
         	    		
         	    		if (typeof(cnt) == 'undefined') {
         	    			$(this.items).each(function() {
         	    			    if (this.cnt == '1') return str += "<td>" + this.itemNo + "</td>";
         	    			});
         	    		} else {
            	    		if (questionType == 'D') {	//주관식
            	    			str += "<td>" + this.answerItem + "</td>";
            	    		}
            	    		else{
	         	    			str += "<td>" + cnt + "</td>";
            	    		}	
         	    		}

         	    		if (str == "") str = "<td>0</td>";
         	    		html += str;
        	    	});   	    		
    	    	});
    	    	html += "</tr>";
    		});    		
    	} else {
    		html += "<tr>";
    		
    		var txt = "";
    		if (reqType == 'job') {
    			txt = "직급";
    		} else if (reqType == 'dept') {
    			txt = "부서";
    		} else {
    			txt = "전체";
    		}
    		
    		var rspan = 1;			// UI 깨짐 수정
    		var subRankhtml = "";
    		if (isNType) rspan = reqType == "all" ? 4 : 2;
    		else 		 rspan = reqType == "all" ? 3 : 2;
    		html += "<th  rowspan='"+ rspan +"'>" + txt + "</th>";    			
    
    		
    		$(questions).each(function() {
    			var itemLen = this.items.length;
    			var questionType = this.questionType;
    			
     	    	$(this.items).each(function() {
     	    		if (questionType == 'N') {
	    	    		html += "<td colspan='" + itemLen + "' class='center'>" + this.itemNo + "</td>";	
	    	    		$(this.items).each(function() {
		    	    		subRankhtml += "<td  class='center'>" + this.itemNo + "</td>";
	        	    	});	
     	    		} else {
     	    			html += "<td rowspan='2'  class='center'>" + this.itemNo + "</td>";
     	    		}
    	    	});
    		});
    		
    		html += "</tr>";
    		html += "<tr>"+subRankhtml+"</tr>";
    		
      		$(itemRecords).each(function() {
     			html += "<tr>";
     			
        		var rowTxt = "";
        		if (reqType == 'job') {
        			rowTxt = this.jobLevelName;
        		} else if (reqType == 'dept') {
        			rowTxt = this.deptName;
        		} 
        		
        		if(reqType != 'all'){
	     			html += "<td>" + rowTxt + "</td>";
        		}
     			
    	    	$(this.questionRecords).each(function() {
    	    		var questionType = this.questionType;
    	    		
         	    	$(this.items).each(function() {
         	    		if (questionType == 'N') {
         	    			$(this.items).each(function() {
         	    			    html += "<td>" + this.cnt + "</td>";
         	    			});
         	    		} else if (questionType == 'D') {
         	    			html += "<td>"+ this.cnt +"명 응답</td>";
         	    		}else {
         	    			html += "<td>" + this.cnt + "</td>";
         	    		}
        	    	});   	    		
    	    	});
    	    	
    	    	html += "</tr>";
    		});
    	}
    	
    	html += "</tbody>";
    	html += "</table>";
    	
    	var isPopup = CFN_GetQueryString("isPopup");	// 통계보기 : 문항이 많아질경우 layer popup 의 iframe 내의 Table 깨짐을 방지
    	
    	if(isPopup == 'y') {
	    	calcItemWidth = calcItemWidth * 160;
	    	html = '<table class="tableStyle linePlus AXFormTable" style="width:'+calcItemWidth+'px">' + html;			// 팝업
    	} else {
	    	html = '<table class="tableStyle linePlus AXFormTable">' + html;											// 관리자 사이트
    	}
    	
    	$("#reportDiv").append(html);	 
 	}
 	
	// 관리자(설문 관리) 이동
    function goSurveyManage() {
		CoviMenu_GetContent('/groupware/layout/survey_SurveyManage.do?CLSYS=survey&CLMD=admin&CLBIZ=Survey');	
    }
	
	// 사용자통계
    function userReport() {
		reqType = 'user';
		
		search('report');	// 검색
    }
	
</script>	
</body>
</html>
