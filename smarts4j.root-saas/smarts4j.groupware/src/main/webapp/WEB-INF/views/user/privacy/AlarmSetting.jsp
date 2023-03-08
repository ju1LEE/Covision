<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.Arrays,egovframework.baseframework.util.StringUtil,egovframework.baseframework.util.PropertiesUtil,egovframework.baseframework.util.SessionHelper" %>
<div class="cRConTop titType">
	<h2 class="title"><spring:message code="Cache.lbl_MessagingSetting"/></h2> <!-- 통합 메시징 설정 -->
</div>
<div class="cRContBottom mScrollVH">						
	<div class="myInfoContainer myInfoMsgCont">
		<div class="boradTopCont anniversaryTop">					
			<div class="pagingType02 buttonStyleBoxLeft">
				<a href="#" class="btnTypeDefault btnTypeBg" id="btnSave">저장</a>	<!-- 저장 -->
				<a href="#" class="btnTypeDefault" onclick="setResetSetting()"><spring:message code="Cache.btn_AllRefresh"/></a> <!-- 모두 초기화 -->
				<a href="#" class="btnTypeDefault" onclick="openDetail();"><spring:message code="Cache.btn_DetailSetting"/></a> <!-- 세부 설정 -->
			</div>								
		</div>
		<div class="anniversaryBtm">
			<div class="anniversaryCont">
				<table class="tblAnniversaryCont" id="alarmTable">
				</table>
				<br />
				
				<table class="tblAnniversaryCont" id="targetTable">
				</table>									
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	//# sourceURL=AlarmSetting.jsp
	var settingInfo = null;
	var params = new Object();
	var tempChkCnt = 1;
	var scrollHeight = 0; 
	initContent();
	
	// 개인환경설정 > 통합 메세징 설정
	function initContent() {
		init();	// 초기화
		
		search('');
	}
	
	// 초기화
	function init() {
    	$('#targetTable').on('change', '.gridCheckBox', function () {
    		var checkedFg = $(this).is(":checked");
   			var tr = $(this).closest('tr');
    		var td = $(this).closest('td');
    		var tdIdx = td.index();
    		var className = td.attr('class');
    		var serviceTypes = new Array();
    		var tempObj, tempServiceType, tempMediaType;
    		var reqTr, tarTr;
    		var serviceType = '', tarMediaType = '';
   			var id = tr.attr('id');
   			
   			if (id == 'headerTr') {
   				reqTr = 'head'
   				tarTr = $('#targetBody').find('tr').not('.first');
   				tarMediaType = $(this).closest('th').find('input').val().toUpperCase();
   			} else if (tr.hasClass('first')) {
   				reqTr = 'body'
   				tarTr = $('.' + id + '_detailTr');
   				serviceType = id;
   				tarMediaType = $('#headerTr > th').eq(tdIdx).find('input').val().toUpperCase();
   			} else {
   				reqTr = 'detail'
   				tarTr = td.parent();
   				serviceType = td.siblings('td').eq(0).attr('value');
   				tarMediaType = $('#headerTr > th').eq(tdIdx + 1).find('input').val().toUpperCase();
   			}
   			
     		tarTr.each(function (i, v) {
     			tempServiceType = $(this).find('td').eq(0).attr('value');
   				tempObj = new Object();
   				tempMediaType = '';
			
   				$(this).find('.colDetailTd').each(function (i, v) {
   					var tar = $(this).find('input');
  	    		   	var inputVal = $('#headerTr > th').eq(i + 2).find('input').val().toUpperCase();
  	    		   		
  	    		   	if (tar.length > 0) {
	   	    		   	if (tarMediaType == inputVal && tar.is(":enabled")) { //수정 가능 항목일 경우만 지정
	   	    		   		tar.prop("checked",checkedFg);
	   	    		   		
	   	    		   		if(checkedFg){
		   	    		   		if (i > 0 && tempMediaType != '') tempMediaType += ';';
		   	    		   		tempMediaType += inputVal;
	   	    		   		}
	   		   			} else {
	   		   				if (tar.is(":checked")) {
		    		   			if (i > 0 && tempMediaType != '') tempMediaType += ';';
		   	    		   		tempMediaType += inputVal;  
	   		   				}
	   		   			}
  	    		   	}
   				});
   				
   				tempObj.serviceType = tempServiceType;
   				tempObj.mediaType = ( tempMediaType != '' ? tempMediaType :  ';' ) ; //공백관련 오류 방지

   				//if (tempMediaType != '') serviceTypes.push(tempObj); 
   			 	serviceTypes.push(tempObj); 
   			});
     		/*
    		var params = new Object();
    		params.reqTr = reqTr;
    		params.serviceType = serviceType;
    		params.serviceTypes = serviceTypes;
    		
      		$.ajax({
				type : "POST",
				contentType : 'application/json; charset=utf-8',
				data : JSON.stringify(params),
				url : '/groupware/privacy/updateUserMessagingSetting.do',
				success:function (data) {
					if (data.result == "ok") {
						if(data.status == 'SUCCESS') {
							var openArray = [];	
							$("a[id^='bizSection_'][opened='Y']").each(function(idx,obj){
								openArray.push($(obj).attr("bizsection"));
							});
							
							scrollHeight = $(".cRContBottom").scrollTop(); //스크롤 위치 저장
							search(openArray.join(";"));
							
							//search();
							//Common.Inform("수정 되었습니다.", "Inform", function() {
							//	search();
							//}); 
		          		} else {
		          			Common.Warning(Common.getDic("msg_ErrorOccurred")); //오류가 발생하였습니다.
		          		}
					}
				},
				error:function(response, status, error) {
					CFN_ErrorAjax('/groupware/privacy/updateUserMessagingSetting.do', response, status, error);
				}
			});
      		*/
    	});		
    	
    	//저장
    	$('#btnSave').on('click', function () {
    		//PUSH알림설정
   			params = new Object();
   			params.pushAllowYN = $('#pushAllowYN').hasClass('on') ? 'Y' : 'N';
   			params.pushAllowStartTime = $("#pushStartTime option:selected").val().replace(":","");
   			params.pushAllowEndTime = $("#pushEndTime option:selected").val().replace(":","");
   			
   			var pushweek = "";
       		$.each($("#pushweek").find(".btnTypeDefault"), function (i, v) {
       			if($(v).attr("class").lastIndexOf("selected") > 0 ) 
       				pushweek += "1";
       			else
       				pushweek += "0";
       		});
   			params.pushAllowWeek = pushweek;
   			
   			$.ajax({
   				type : "POST",
   				data : params,
   				async: false,
   				url : "/groupware/privacy/updateUserPush.do",
   				success:function (list) {
   					/*Common.Inform('<spring:message code="Cache.msg_Edited"/>', "Inform", function() {	//수정되었습니다
   						location.reload();
					});*/
   				},
   				error:function(response, status, error) {
   					CFN_ErrorAjax(url, response, status, error);
   				}
   			});
   			
   			//통합 메세징 설정
    		var serviceType = '';
    		var tarMediaType = '';
    		var serviceTypes = new Array();
    		
     		$('#targetBody').find('tr').not('.first').each(function (i, v) {
     				var tempServiceType = $(this).find('td').eq(0).attr('value');
     				var tempObj = new Object();
     				var tempMediaType = '';
     				
       				$(this).find('.colDetailTd').each(function (i, v) {
       					var tar = $(this).find('input');
      	    		   	var inputVal = $('#headerTr > th').eq(i + 2).find('input').val().toUpperCase();
      	    		   	
      	    		   	if (tar.length > 0) {
	      	    		   	$('#headerTr > th').find('input').each(function (i, v) {
	         					tarMediaType = $(this).val().toUpperCase();
	         					
	         					if(tarMediaType == inputVal && tar.is(":checked")) {
									if (i > 0 && tempMediaType != '') tempMediaType += ';';
							   		tempMediaType += inputVal;  
	         					}
	         				});
      	    		   	}
       				});
       				
       				tempObj.serviceType = tempServiceType;
       				tempObj.mediaType = ( tempMediaType != '' ? tempMediaType :  ';' ) ; //공백관련 오류 방지

       			 	serviceTypes.push(tempObj);
   			});
     		
     		var params = new Object();
    		params.reqTr = 'head';
    		params.serviceType = '';
    		params.serviceTypes = serviceTypes;
    		
      		$.ajax({
				type : "POST",
				contentType : 'application/json; charset=utf-8',
				data : JSON.stringify(params),
				url : '/groupware/privacy/updateUserMessagingSetting.do',
				success:function (data) {
					if (data.result == "ok") {
						if(data.status == 'SUCCESS') {
							var openArray = [];	
							$("a[id^='bizSection_'][opened='Y']").each(function(idx,obj){
								openArray.push($(obj).attr("bizsection"));
							});
							
							scrollHeight = $(".cRContBottom").scrollTop(); //스크롤 위치 저장
							search(openArray.join(";"));
							
							Common.Inform('<spring:message code="Cache.msg_Edited"/>', 'Inform');	//수정 되었습니다.
							
							/*Common.Inform("수정 되었습니다.", "Inform", function() {
								search();
							});*/ 
		          		} else {
		          			Common.Warning(Common.getDic("msg_ErrorOccurred")); //오류가 발생하였습니다.
		          		}
					}
				},
				error:function(response, status, error) {
					CFN_ErrorAjax('/groupware/privacy/updateUserMessagingSetting.do', response, status, error);
				}
			});
    		
    	});
    	
	}
	
	// 조회
	function search(defaultOpen) {
		$.ajax({
			type : "POST",
			async: false,
			url : "/groupware/privacy/getUserMessagingSetting.do",
			success:function (list) {
				settingInfo = $.extend(true, {}, list.list);
				
				setAlarm(settingInfo.map[0]);
				
				setHeaderHtml();	// 헤더 html
				
				setDataHtml(defaultOpen);	// 데이터 html
				
				$("input[type='checkbox'][indeterminate='true']").prop("indeterminate", true);
			},
			error:function(response, status, error) {
				CFN_ErrorAjax("/groupware/vacation/getUserMessagingSetting.do", response, status, error);
			}
		});	
	}
	
	//공통 알람설정
	function setAlarm(privacyInfo) {
		$('#alarmTable').empty();
		
		var html = '<colgroup>';
	 		html += '<col width="10%">';
			html += '<col width="15%">';
			html += '<col>';
			html += '<col width="30%">';		
		html += '</colgroup>';
		html += '<thead>';
			html += '<tr>';
			html += '<th>업무영역</th>';		
			html += '<th>메세지 유형</th>';
			html += '<th class="colHeadTh">관리</th>';
			html += '<th>설명</th>';			
			html += '</tr>';
		html += '</thead>';
		html += '<tbody>';
		html += '<tr class="Collabo_alartTr first">';
			html += '<th rowspan="2">';
			html += '<a href="#none">모바일알림스케줄 설정</a><br>';
			html += '<div class="alarm type01"><a href="#" class="onOffBtn ';
			html += (typeof(privacyInfo.PushAllowYN) != 'undefined' && privacyInfo.PushAllowYN == 'Y')?'on':'';
			html += '" id="pushAllowYN"><span></span></a></div>';

			html += '</th>';
			html += '<td>요일 설정</td>';
			html += '<td class="colDetailTd setting02">';
			
			var pushweek = privacyInfo.PushAllowWeek;
			var weekStr = Array('월','화','수','목','금','토','일');

			html += '<div class="pagingType02 buttonStyleBoxLeft" id="pushweek">';
			for (var i = 0; i < 7; i++) {
				html += '	<a href="#" class="btnTypeDefault middle' + ((pushweek.charAt(i) == 1)?' selected':'') + '">' + weekStr[i] + '</a>';
			}
			html += '</div>';
			html += '</td>';
			html += '<td>모바일알림를 받을 요일을 설정할 수 있습니다.</td>';
		html += '</tr>';
		html += '<tr class="Collabo_alartTr">';
			html += '<td>시간 설정(GMT+9)</td>';
			html += '<td class="colDetailTd setting02">';
			html += '<select class="selectType02" id="pushStartTime">';
			html += '</select> ~ ';
			html += '<select class="selectType02" id="pushEndTime">';
			html += '</select>';
			html += '</td>';
			html += '<td>모바일알림를  받을 시간을 설정할 수 있습니다.</td>';
		html += '</tr>';
		html += '</tbody>';
		html += '</table>';
		
		$('#alarmTable').append(html);
		
		setSelect(privacyInfo);
		
		$(".onOffBtn").on('click',function(){
			if($(this).attr("class").lastIndexOf("on") > 0 )
				$(this).removeClass("on");
			else
				$(this).addClass("on");
		});
		
		$("#pushweek").find(".btnTypeDefault").on('click',function(){
			if($(this).attr("class").lastIndexOf("selected") > 0 )
				$(this).removeClass("selected");
			else
				$(this).addClass("selected");
		});
		
	}
	
	// 헤더 html
	function setHeaderHtml() {
		var header = settingInfo.header;
		$('#targetTable').empty();
		
		var html = '<colgroup>';
 		html += '<col width="10%">';
		html += '<col width="15%">';
		$.each(header, function (i, v) {
			html += '<col>';
		});
		html += '<col width="30%">';		
		html += '</colgroup>';
		html += '<thead>';
		html += '<tr>';
		html += '<th>'+ Common.getDic("BizSection_BizSection") +'</th>'; //업무영역		
		html += '<th>' + Common.getDic("lbl_MessageType") + '</th>';		//메세지 유형
		
 		$.each(header, function (i, v) {
 			html +=  ( (i == 0) ? '<th class="colHeadTh">' : '<th class="noneLine colHeadTh">' );
 			html += v.CodeName;
 			html += '</th>';
 			
 			tempChkCnt++;
		});
		
		html += '<th>' + Common.getDic("lbl_Description") + '</th>';			
		html += '</tr>';
		html += '<tr id="headerTr">';
		html += '<th class="BgGray">'+ Common.getDic("lbl_all") +'</th>'; //전체		
		html += '<th class="BgGray noneLine"></th>';
		
 		$.each(header, function (i, v) {
 			var style = "";
 			
 			if( v.State == "checked"){
				style = "checked";
			}else if(v.State == "indeterminate"){
				style = "indeterminate='true'";
			}
 			
 			html += '<th class="BgGray noneLine">';
 			html += '<div class="chkStyle01 chkType01">';
 			html += '	<input type="checkbox" class="gridCheckBox" id="chkGrade' + tempChkCnt + '" value="' + v.Code + '" ' + style + '><label for="chkGrade' + tempChkCnt + '"><span></span></label>';
 			html += '</div>';
 			html += '</th>';
 			
 			tempChkCnt++;
		});
		
		html += '<th class="BgGray noneLine"></th>';			
		html += '</tr>';
		html += '</thead>';
		html += '<tbody id="targetBody">';
		html += '</tbody>';
		
		$('#targetTable').append(html);
	}
	
	// 데이터 html
	function setDataHtml(defaultOpen) {
		$('#targetBody').css('display', 'none');
		$('#targetBody').empty();
		
		var header = settingInfo.header;
		var data = settingInfo.data;
		var defaultOpenArr = defaultOpen.split(';');
		
		var html = '';
		$.each(data, function (dataIdx, dataObj) {
			var bizSection = dataObj.bizSection;
			var detailRowspan = dataObj.detail.length + 1;
			var isOpen = defaultOpenArr.includes(bizSection) ? "Y": "N";

			//전자결제 제외
			if(bizSection != "Approval" ){
				
				if(bizSection == "Collab"){	//협업스페이스 분리
					
					//협업 프로젝트
					html += '<tr class="first" id="' + bizSection + '_Prj">';
					html += '<th>';
					html += '<a href="#none" id="bizSection_'+bizSection+'_Prj" opened="'+isOpen+'" bizsection="'+bizSection+'" onclick="showDetail(\'' + bizSection + '\',\'_Prj\'); return false;" >' + dataObj.parentCodeName + '<br />프로젝트 전체</a>';
					html += '</th>';
					html += '<td></td>';
					$.each(header, function (headerIdx, headerObj) {
						html += '<td class="noneLine colBodyTd">';
						if(dataObj[headerObj.Code] != "disabled"){
							var style ="";
							
							if(dataObj[headerObj.Code] == "checked"){
								style = "checked";
							}else if(dataObj[headerObj.Code] == "indeterminate"){
								style = "indeterminate='true'";
							}
							
							html += '<div class="chkStyle01 chkType01">';
							html += '<input type="checkbox" id="chkGrade' + tempChkCnt + '" class="gridCheckBox" ' + style + '><label for="chkGrade' + tempChkCnt + '"><span></span>' + headerObj.CodeName + '</label>';
							html += '</div>';
						}
						html += '</td>';
						
						tempChkCnt++;
					});
					html += '<td></td>';
					html += '</tr>';
					
					//협업 업무
					html += '<tr class="first" id="' + bizSection + '_Tas">';
					html += '<th>';
					html += '<a href="#none" id="bizSection_'+bizSection+'_Tas" opened="'+isOpen+'" bizsection="'+bizSection+'" onclick="showDetail(\'' + bizSection + '\',\'_Tas\'); return false;" >' + dataObj.parentCodeName + '<br />업무 전체</a>';
					html += '</th>';
					html += '<td></td>';
					$.each(header, function (headerIdx, headerObj) {
						html += '<td class="noneLine colBodyTd">';
						if(dataObj[headerObj.Code] != "disabled"){
							var style ="";
							
							if(dataObj[headerObj.Code] == "checked"){
								style = "checked";
							}else if(dataObj[headerObj.Code] == "indeterminate"){
								style = "indeterminate='true'";
							}
							
							html += '<div class="chkStyle01 chkType01">';
							html += '<input type="checkbox" id="chkGrade' + tempChkCnt + '" class="gridCheckBox" ' + style + '><label for="chkGrade' + tempChkCnt + '"><span></span>' + headerObj.CodeName + '</label>';
							html += '</div>';
						}
						html += '</td>';
						
						tempChkCnt++;
					});
					html += '<td>';
					html += '</tr>';
					
				}else{
					html += '<tr class="first" id="' + bizSection + '">';
					html += '<th>';
					html += '<a href="#none" id="bizSection_'+bizSection+'" opened="'+isOpen+'" bizsection="'+bizSection+'" onclick="showDetail(\'' + bizSection + '\'); return false;" >' + dataObj.parentCodeName + '</a>';
					html += '</th>';
					html += '<td></td>';
					$.each(header, function (headerIdx, headerObj) {
						html += '<td class="noneLine colBodyTd">';
						if(dataObj[headerObj.Code] != "disabled"){
							var style ="";
							
							if(dataObj[headerObj.Code] == "checked"){
								style = "checked";
							}else if(dataObj[headerObj.Code] == "indeterminate"){
								style = "indeterminate='true'";
							}
							
							html += '<div class="chkStyle01 chkType01">';
							html += '<input type="checkbox" id="chkGrade' + tempChkCnt + '" class="gridCheckBox" ' + style + '><label for="chkGrade' + tempChkCnt + '"><span></span>' + headerObj.CodeName + '</label>';
							html += '</div>';
						}
						html += '</td>';
						
						tempChkCnt++;
					});
					html += '<td></td>';
					html += '</tr>';
				}
				
			}
		
		});
		
		$('#targetBody').append(html);
		
		// 데이터 상세 html
		//var totalDetailCntArr;	// 디테일 총 체크박스 개수
		//var checkedDetailCntArr;	// 디테일 체크된 체크박수 개수
		//var totalCnt = data.length;	// 총 체크박스 개수
		//var checkedCntArr = new Array();	// 체크된 체크박수 개수
		
  		$('#targetBody .first').each(function () {
			//totalDetailCntArr = new Array();
			//checkedDetailCntArr = new Array();
			html = '';
			var id = $(this).attr('id');
			var header = settingInfo.header;
			var detail;
			var lang = Common.getSession("lang");
			
			$.each(settingInfo.data, function (i, v) {
				if (v.bizSection == id) {
					detail = v.detail;
					return false;
				} else if (v.bizSection == 'Collab' && (id == 'Collab_Prj' || id == 'Collab_Tas')) {
					
					var prjdetail = new Array();
					var tasdetail = new Array();
					$.each(v.detail, function (j, s) {
						var codeSub = s.Code.substring(0, 3);
						
						if (codeSub.substring(0, 3) == 'Prj') prjdetail.push(s);
						else if(codeSub.substring(0, 3) == 'Tas') tasdetail.push(s);
					});
					
					if(id == 'Collab_Prj') detail = prjdetail;	
					else if(id == 'Collab_Tas') detail = tasdetail;
					
					return false;
				}
			});

 	 		$.each(detail, function (i, v) {
				
				if(v.BizSection == 'Collab'){
					var codeSub = v.Code.substring(0, 3);
					var checkboxStatus = v.checkboxStatus.split(',');
					
					html += '<tr class="Collabo_alartTr ' + v.BizSection + '_' + codeSub + '_detailTr" style="display:none;">';
					html += '<td value="' + v.Code + '">' + CFN_GetDicInfo(v.MultiCodeName, lang) + '</td>';
					$.each(checkboxStatus, function (i, v) {
						var status = v.split(';');	// 1;2;3;4 : 1-Code, 2-체크박스 보임 여부, 3-체크박스 수정 가능 여부, 4-체크박스 체크 여부
						if (i == 0) html += '<td class="colDetailTd">'; else html += '<td class="noneLine colDetailTd">';
						
						if (status[1] == 'Y') {
							var disabled = (status[2] == 'N' ? "disabled" : ""); 
							var checked = (status[3] == 'Y' ? "checked" : ""); 
							html += '<div class="chkStyle01 chkType01">';
							html += '	<input type="checkbox" class="gridCheckBox" id="chkGrade' + tempChkCnt + '" '+checked+' '+disabled+'><label for="chkGrade' + tempChkCnt + '"><span></span></label>';
							html += '</div>';

							tempChkCnt++;
						}
						
						html += '</td>';
					});
					
					html += '<td>' + v.Description;
					
					if(v.Code == 'TaskCloseDate')
						html += ' <a href="#none" class="btnTypeDefault type02" onclick="collabUtil.openClosingAlarmPopup();">상세 설정</a>';
					else if (v.Code=="PrjSummDay")	
						html += ' <br>프로젝트별 알림 여부를 설정할 수있습니다. <a href="#none" class="btnTypeDefault type02" onclick="collabUtil.openProjectAlarmPopup();">프로젝트 설정</a>';
					
					html += '</td>';
					html += '</tr>';
					
				}else{
					var checkboxStatus = v.checkboxStatus.split(',');
					
					html += '<tr class="Collabo_alartTr ' + v.BizSection + '_detailTr" style="display:none;">';
					html += '<td value="' + v.Code + '">' + CFN_GetDicInfo(v.MultiCodeName, lang) + '</td>';
					$.each(checkboxStatus, function (i, v) {
						var status = v.split(';');	// 1;2;3;4 : 1-Code, 2-체크박스 보임 여부, 3-체크박스 수정 가능 여부, 4-체크박스 체크 여부
						if (i == 0) html += '<td class="colDetailTd">'; else html += '<td class="noneLine colDetailTd">';
						
						if (status[1] == 'Y') {
							var disabled = (status[2] == 'N' ? "disabled" : ""); 
							var checked = (status[3] == 'Y' ? "checked" : ""); 
							html += '<div class="chkStyle01 chkType01">';
							html += '	<input type="checkbox" class="gridCheckBox" id="chkGrade' + tempChkCnt + '" '+checked+' '+disabled+'><label for="chkGrade' + tempChkCnt + '"><span></span></label>';
							html += '</div>';

							tempChkCnt++;

							//if (typeof(totalDetailCntArr[i]) == 'undefined') totalDetailCntArr.push(1); else totalDetailCntArr[i] += 1;
						}
						
						html += '</td>';
						
						//if (typeof(checkedDetailCntArr[i]) == 'undefined') checkedDetailCntArr.push(0);
						//if (typeof(totalDetailCntArr[i]) == 'undefined') totalDetailCntArr.push(0);
					});
					
					html += '<td>' + v.Description + '</td>';
					html += '</tr>';
				}
			});
			
 	 		if(id == 'Collab')
 	 			$('#' + id).after(html);
 	 		else
				$('#' + id).after(html);
			
			// 테이블 바디 체크박스 체크
			
			/* $(checkedDetailCntArr).each(function (i, v) {
				if (totalDetailCntArr[i] > 0 && totalDetailCntArr[i] == v) $('#' + id + ' > td').eq(i + 1).find('input').prop('checked', true);
			}); */
		});
		
  		/*
  		//체크박스 데이터 가공
   		$('#targetBody .first').each(function () {
			$(this).find('.colBodyTd').each(function (i, v) {
				if ($(this).find('input').prop('checked')) {
					if (typeof(checkedCntArr[i]) == 'undefined') checkedCntArr.push(1); else checkedCntArr[i] += 1;	// 체크된 개수
				} else {
					if (typeof(checkedCntArr[i]) == 'undefined') checkedCntArr.push(0);
				}
			});
		});
  		
		// 테이블 헤더 체크박스 체크
		$(checkedCntArr).each(function (i, v) {
			if (totalCnt == v) $('#headerTr > th').eq(i + 2).find('input').prop('checked', true);
		}); 
		*/
		$.each(defaultOpenArr,function(idx,obj){
			showDetail(obj);
		});
		
		$('#targetBody').css('display', '');
		$(".cRContBottom").scrollTop(scrollHeight);
	}
	
	// 모두 초기화
	function setResetSetting() {
		//PUSH알림설정
		params = new Object();
		params.pushAllowYN = 'N';
		params.pushAllowStartTime = '';
		params.pushAllowEndTime = '';
		params.pushAllowWeek = '';
		
		$.ajax({
			type : "POST",
			data : params,
			async: false,
			url : "/groupware/privacy/updateUserPush.do",
			success:function (list) {
				
			},
			error:function(response, status, error) {
				CFN_ErrorAjax(url, response, status, error);
			}
		});
		
		//통합 메시징 설정
 		$.ajax({
			type : "POST",
			contentType : 'application/json; charset=utf-8',
			data : JSON.stringify({}),
			url : '/groupware/privacy/deleteUserMessagingSetting.do',
			success:function (data) {
				if (data.result == "ok") {
					if(data.status == 'SUCCESS') {
						Common.Inform(Common.getDic("msg_Common_44"), "Inform", function() { //초기화 되었습니다.
							scrollHeight = 0;
							search('');
						});
	          		} else {
	          			Common.Warning(Common.getDic("msg_ErrorOccurred")); //오류가 발생하였습니다.
	          		}
				}
			},
			error:function(response, status, error) {
				CFN_ErrorAjax('/groupware/privacy/deleteUserMessagingSetting.do', response, status, error);
			}
		});
	}
	
	// 세부 설정
	function openDetail() {
		var tar = $("#targetBody > tr:visible").not('.first');
		var len = tar.length;	//	상세 보임 개수
		var notVisibleTarLen = $("#targetBody > tr").not('.first').length;
		var data = settingInfo.data;
		
		if (len == notVisibleTarLen) {
			$('#targetBody .first').find('th').attr('rowspan', 1);
			tar.css('display', 'none');
			$.each(data, function (i, v) {
				if(v.bizSection == "Collab"){
					$('#bizSection_' + v.bizSection + '_Prj').attr("opened","N");
					$('#bizSection_' + v.bizSection + '_Tas').attr("opened","N");
				}else{
					$('#bizSection_' + v.bizSection).attr("opened","N");	
				}
			});
		} else {
			$.each(data, function (i, v) {
				if(v.bizSection == "Collab"){
					var prjdetail = new Array();
					var tasdetail = new Array();
					$.each(v.detail, function (j, s) {
						var codeSub = s.Code.substring(0, 3);
						
						if (codeSub.substring(0, 3) == 'Prj') prjdetail.push(s);
						else if(codeSub.substring(0, 3) == 'Tas') tasdetail.push(s);
					});
					
					$('#' + v.bizSection + '_Prj').find('th').attr('rowspan', prjdetail.length + 1);
					$('.' + v.bizSection + '_Prj' + '_detailTr').css('display', '');
					$('#bizSection_' + v.bizSection + '_Prj').attr("opened","Y");
					
					$('#' + v.bizSection + '_Tas').find('th').attr('rowspan', tasdetail.length + 1);
					$('.' + v.bizSection + '_Tas' + '_detailTr').css('display', '');
					$('#bizSection_' + v.bizSection + '_Tas').attr("opened","Y");
				}else{
					$('#' + v.bizSection).find('th').attr('rowspan', v.detail.length + 1);
					$('.' + v.bizSection + '_detailTr').css('display', '');
					$('#bizSection_' + v.bizSection).attr("opened","Y");
				}
			});
		}
	}

	// 업무영역 클릭
	function showDetail(tar, codeSub) {
		
		if(tar == "Collab"){
			var len = $('.' + tar + codeSub +'_detailTr:visible').length;
			var totLen = $('.' + tar + codeSub + '_detailTr').length;
			
			if (len > 0) {
				$('#bizSection_' + tar  + codeSub).attr("opened","N");
				$('#' + tar + codeSub).find('th').attr('rowspan', 1);
				$('.' + tar  + codeSub + '_detailTr').css('display', 'none');
			} else {
				$('#bizSection_' + tar  + codeSub).attr("opened","Y");
				$('#' + tar + codeSub).find('th').attr('rowspan', totLen + 1);
				$('.' + tar  + codeSub + '_detailTr').css('display', '');
			}
		}else{
			var len = $('.' + tar + '_detailTr:visible').length;
			var totLen = $('.' + tar + '_detailTr').length;
			
			if (len > 0) {
				$('#bizSection_' + tar).attr("opened","N");
				$('#' + tar).find('th').attr('rowspan', 1);
				$('.' + tar + '_detailTr').css('display', 'none');
			} else {
				$('#bizSection_' + tar).attr("opened","Y");
				$('#' + tar).find('th').attr('rowspan', totLen + 1);
				$('.' + tar + '_detailTr').css('display', '');
			}
		}
	}
	
	// Select box 바인드
	function setSelect(privacyInfo){		
		var lang = Common.getSession("lang");
		coviCtrl.renderAXSelect('pushTime', 'pushStartTime', lang, '', '', ((privacyInfo.PushAllowStartTime.length == 4)?privacyInfo.PushAllowStartTime.substr(0,2)+':'+privacyInfo.PushAllowStartTime.substr(2,2):'') ,'1');
		coviCtrl.renderAXSelect('pushTime', 'pushEndTime', lang, '', '', ((privacyInfo.PushAllowEndTime.length == 4)?privacyInfo.PushAllowEndTime.substr(0,2)+':'+privacyInfo.PushAllowEndTime.substr(2,2):''));
	}
</script>
