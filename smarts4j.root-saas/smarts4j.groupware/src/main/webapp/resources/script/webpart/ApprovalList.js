/**
 * approvalBox - 전자결재 - 결재함
 */
var approvalBox ={
		init: function (){
			$("#spnTteApprovalBox").prepend("<spring:message code='Cache.tte_ApprovalListBox'/>");
			$("#spnTteProcessBox").prepend("<spring:message code='Cache.tte_ProcessListBox'/>");
			$("#spnTtePreApprovalBox").prepend("<spring:message code='Cache.tte_PreApprovalListBox'/>");
			$("#spnTteTCInfoBox").prepend("<spring:message code='Cache.lbl_TCInfoListBox'/>");
			
			approvalBox.getCount();
			approvalBox.getApprovalList('Approval');
		},
		getApprovalList:function(pMode){
			var params = {
				pageSize: 7,
				mode: pMode
			};
			
			$.ajax({
				type:"GET",
				url:"/groupware/webpart/getUserApprovalList.do",
				data: params,
				success:function(data){
					var listData = data.list;
					
					$("#ulApprovalList").html("");
					
					if(listData.length == 0) {
						$('#ulApprovalList').append('<li style="text-align: center; margin-top: calc(15% - 23px);"><span class="" style="color: #999;">'+"<spring:message code='Cache.msg_NoDataList'/>"+'</span></li>');
					}
					else {
						$.each( listData, function(index, value) {
			            	var liWrap = $('<li />');
			            	var date = "";
			            	var formInstID = "";
			            	
			            	switch(pMode) {
			            	case "Approval": 
			            	case "PreApproval":
			            		date = value.Created; break;
			            	case "Process": 
			            		date = value.StartDate; break;
			            	case "TCInfo":
			            		date = value.ReceiptDate;
			            		formInstID = value.FormInstID; break;
			            	}
			            	
			            	
			            	date = CFN_TransLocalTime(date, "yyyy.MM.dd");
			            	
			            	var param = "\'" + pMode + "\',\'" + value.ProcessID + "\',\'" + value.WorkItemID + "\',\'"+ value.PerformerID + "\',\'"+ value.ProcessDescriptionID + "\',\'" + value.FormSubKind + "\',\'\',\'" + formInstID + "\',\'\',\'\',\'"+ value.UserCode + "\',\'" + value.FormPreFix + "\'";
			            	
			            	var anchorSubject = $('<a class="title" onclick="approvalBox.clickPopup(' + param + ');"/>').text(value.FormSubject).attr('title', value.FormSubject);
			            	var spanType = $('<span class="type" />').text(value.InitiatorName);
			            	var spanDate = $('<span class="date" />').text(date);
			            	
							liWrap.append(anchorSubject, spanType, spanDate);
							
							$('#ulApprovalList').append(liWrap);
						});
					}
				}
			});
		},
		getCount:function (){
			var iApproval = 0;
			var iProcess = 0;
			var iPreApproval = 0;
			var iTCInfo;
			
			$.ajax({
				type:"GET",
				url:"/groupware/webpart/getApprovalListCnt.do",
				success:function(data){
					iApproval = data.approval;
					iProcess = data.process;
					iPreApproval = data.preapproval;
					iTCInfo = data.tcinfo;
					
					$("#divApprovalCnt").text(iApproval);
					$("#divProcessCnt").text(iProcess);
					$("#divPreApprovalCnt").text(iPreApproval);
					$("#divTCInfoCnt").text(iTCInfo);
				}
			});
		},
		clickPopup:function (mode,ProcessID,WorkItemID,PerformerID,ProcessDescriptionID,SubKind,FormTempInstBoxID,FormInstID,FormID,FormInstTableName,UserCode,FormPrefix){
			var width;
			var gloct;
			var archived = "false";
			var subkind = SubKind; 
			var userID = UserCode;
			
			switch(mode){
				case "Approval": gloct = "APPROVAL"; break;// 미결함
				case "Process": gloct = "PROCESS"; break; //진행함 
				case "PreApproval": gloct = "PROCESS"; subkind="T010"; break; //예고함
				case "TCInfo": gloct = "TCINFO"; userID=""; break; //참조/회람함
			}
			
			if(IsWideOpenFormCheck(FormPrefix) == true){
				width = 1070;
			}else{
				width = 790;
			}
			
			CFN_OpenWindow("/approval/approval_Form.do?mode="+mode.toUpperCase()+"&processID="+ProcessID+"&workitemID="+WorkItemID+"&performerID="+PerformerID+"&processdescriptionID="+ProcessDescriptionID+"&userCode="+userID+"&gloct="+gloct+"&formID="+FormID+"&forminstanceID="+FormInstID+"&formtempID="+FormTempInstBoxID+"&forminstancetablename="+FormInstTableName+"&admintype=&archived="+archived+"&usisdocmanager=true&listpreview=N&subkind="+subkind+"", "", width, (window.screen.height - 100), "resize");
		},
		changeTab : function(obj, pMode) {
			$("ul.mSubTabMenu02").find("li").removeClass("active");
			$(obj).parent().addClass("active");
			approvalBox.getApprovalList(pMode);
		}
}

