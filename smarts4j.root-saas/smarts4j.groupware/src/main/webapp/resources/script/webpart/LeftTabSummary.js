/**
 * 
 */

var LeftTabSummary = {
	init: function(data, ext) {
		if (!window.hasOwnProperty('g_commentAttachList')) window.g_commentAttachList = [];
		if (g_useLeft == 'N') $(".PN_mainLeft .btnClose").click();
		
		$(".PN_infoTab").closest("div[id^=WP]").css("height", "100%");	
		if(Common.getBaseConfig("isUseMail") == "Y" && Common.getSession('UR_AssignedBizSection').includes('Mail')){
			$(".PN_infoTab li[data-tab=PN_tab1_2]").show();
		}
		
		LeftTabSummary.setEvent();
		LeftTabSummary.setCount();
		LeftTabSummary.rollingBanner.show();
	},
	setEvent: function(){
		
		$(".PN_mailListBox").mCustomScrollbar({
			mouseWheelPixels: 50, scrollInertia: 350,
 			callbacks: {
 				onTotalScroll: function(){
 					var bottomNum = Number($(".PN_mailListBox .mCSB_dragger").css("bottom").replace("px", ""))
					
 					if(bottomNum == 0){
 						LeftTabSummary.mail.pageNo += 1;
 						LeftTabSummary.mail.list('append');
 					}
 				}
 			}
		});
		coviCtrl.bindmScrollV($(".PN_aprListArea"));
		coviCtrl.bindmScrollV($(".PN_scheduleCont"));
		coviCtrl.bindmScrollV($(".PN_taskCont"));
	},
	setCount: function(){
		LeftTabSummary.mail.countUnread();
	},
	clickTab: function(target){
		var tab_id = $(target).closest("li").attr("data-tab");
		
		$(".PN_tabList li").removeClass("active");
		$(".PN_tabContent").removeClass("active");
		
		$(target).closest("li").addClass("active");
		$("#" + tab_id).addClass("active");
			
		if(tab_id === 'PN_tab1_1'){
			LeftTabSummary.rollingBanner.show();
		}
		else if(tab_id === 'PN_tab1_2'){
			LeftTabSummary.mail.show();		
		}
		else if(tab_id === 'PN_tab1_3'){
			LeftTabSummary.approval.show();
			//setApvCnt();
			//getApprovalList();
		}
		else if(tab_id === 'PN_tab1_4'){
			LeftTabSummary.schedule.show();
			//setToday();	
		}
		else if(tab_id === 'PN_tab1_5'){
			LeftTabSummary.task.show($("#selTaskType").val(), "List");
			//setMyTaskList($("#selTaskType").val(), "List");
			//setTaskReportList();
		}
	},
	rollingBanner: {
		isLoad: 'N',
		show: function(){
			if (LeftTabSummary.rollingBanner.isLoad == 'N') {
				LeftTabSummary.rollingBanner.get();
			}
		},
		get: function(){
			console.log('롤링배너정보를 가져옴');
			$.ajax({
				url: "/groupware/pnPortal/selectRollingBannerBoardList.do",
				type: "POST",
				data: "",
				success: function(data){
					var listData = data.list;
					
					if(listData && listData.length > 0){
						var bannerHtml = "";
						
						$.each(listData, function(idx, item){
							var bannerImgPath = "";
							var bannerOption = "";
							var bannerClickEvent = "";
							var divWrap = $("<div></div>");
							
							item.SavedName && (bannerImgPath = item.FullPath);
							item.BannerImageOption === "Dark" && (bannerOption = "opacity"); 
							
							if(item.BannerBoardOption == "Y"){
								if(item.BannerLinkOption == "Link"){								
									if(item.LinkURL){
										if(item.BannerOpenOption == "New"){
											bannerClickEvent = "window.open('" + item.LinkURL + "');";
										}else{
											bannerClickEvent = "javascript: location.href='" + item.LinkURL + "';";
										}
									}else{
										bannerClickEvent = "javascript: Common.Inform('<spring:message code='Cache.msg_noRegisteredLink'/>');"; // 링크가 등록되지 않았습니다.
									}
									
								}else{
									bannerClickEvent = "javascript: goViewPopup('Board', '" + item.BannerOpenOption + "', " + item.MenuID + ", " + item.Version + ", " + item.FolderID + ", " + item.MessageID + ");";
								}
							}else{
								divWrap.css("cursor", "default");
							}
							
							if(bannerImgPath && bannerImgPath != ""){
								divWrap.attr("onclick", bannerClickEvent)
									.append($("<div class='mImg " + bannerOption + "'></div>")
									.append($("<img onerror='setNoImage(this);'>").attr("src", coviCmn.loadImage(bannerImgPath))))
									.append($("<div class='mBox'></div>")
									.append($("<strong class='mTitle'></strong>").text(item.BannerTitle))
									.append($("<span class='mTitle_s'></span>").text(item.BannerSubTitle))
									.append($("<p class='mTxt'></p>").text(item.BannerText)));
								
								bannerHtml += $(divWrap)[0].outerHTML;
							}
						});
						
						$("#rollingBannerList").empty().append(bannerHtml);
						
						LeftTabSummary.rollingBanner.setScroll();
						LeftTabSummary.rollingBanner.isLoad = 'Y'
						//bannerScrolling();
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/groupware/pnPortal/selectRollingBannerBoardList.do", response, status, error);
				}
			});
		},
		setScroll: function(){
			$(".PN_mSlider").not(".slick-initialized").slick({
				slide: "div",			// 슬라이드 되어야 할 태그 ex) div, li
				infinite : true,		// 무한 반복 옵션
				slidesToShow : 2,		// 한 화면에 보여질 컨텐츠 개수
				slidesToScroll : 1,		// 스크롤 한번에 움직일 컨텐츠 개수
				speed : 1500,	 		// 다음 버튼 누르고 다음 화면 뜨는데까지 걸리는 시간(ms)
				arrows : false, 		// 옆으로 이동하는 화살표 표시 여부
				dots : false,			// 스크롤바 아래 점으로 페이지네이션 여부
				autoplay : true,		// 자동 스크롤 사용 여부
				autoplaySpeed : 4000,	// 자동 스크롤 시 다음으로 넘어가는데 걸리는 시간 (ms)
				pauseOnHover : true,	// 슬라이드 이동	시 마우스 호버하면 슬라이더 멈추게 설정
				vertical : true,		// 세로 방향 슬라이드 옵션
				draggable : true,		// 드래그 가능 여부
			});
			
			$(".PN_mSlider").on("wheel", (function(e) {
				e.preventDefault();
				if (e.originalEvent.deltaY < 0) {
					$(this).slick("slickPrev");
				} else {
					$(this).slick("slickNext");
				}
			}));
		}
	},
	mail: {
		pageNo: 1,
		loadComplete: false,
		show: function(){
			LeftTabSummary.mail.countUnread();
			LeftTabSummary.mail.list();
			LeftTabSummary.mail.capacity();
		},
		countUnread: function(){
			if(Common.getSession('UR_AssignedBizSection').indexOf('Mail') > -1){
				$.ajax({ 
					type: "POST",
					url: "/groupware/longpolling/getQuickData.do",
					data: { "menuListStr": "Mail;" },
					dataType: "json", 
					success: function(data){
						var mailCnt = Number(data.countObj.Mail) >= 100 ? "99+" : data.countObj.Mail;
						$(".PN_tabList .PN_tab1_2 .countStyle").text(mailCnt);
					}
				});
			}
		},
		list: function(mode){
			if (LeftTabSummary.mail.loadComplete) return false;
			var userMail = Common.getSession("UR_Mail");
			var nowDate = schedule_SetDateFormat(new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd")), "-");
			
			// 메일 리스트 가져올 때 필요한 옵션
			var opts = {
				"useMail": userMail,
				"detailMap": "",
				"mailBox": "INBOX",
				"page": LeftTabSummary.mail.pageNo,
				"replyToList": null,
				"sortType": "",
				"threadsRowNum": 0,
				"type": "MAILLIST",
				"type2": "ALL",
				"viewNum": "10"
			};
			
			// 메일 리스트 가져오기
			MessageAPI.getList(userMail, opts).done(function(data){
				if(data && data[0].mailList.length > 0){
					var fieldList = data[0].mailList;
					var listCnt = 0;
					var userMails = fieldList.map(function(d){
						return d.mailSenderAddr.trim();
					});
					var imgPaths = getProfileImgPath("Mail", "", userMails.join(";"));
					var mailWrap = "";
					
					$(".PN_linkBox .l_link1_1 .l_Num").text(Number(data[0].mailListLength) >= 100 ? "99+" : data[0].mailListLength);
					
					$(fieldList).each(function(idx, item){
						var readClass = item.flag == "\\Seen" ? "PN_read" : "";
						var divWrap = $("<div class='PN_mailList " + readClass + "'></div>");
						var mailOpenMethod = "javascript:LeftTabSummary.mail.read('" + item.mailId + "', " + item.uid + ");";
						var newMail = '<span class="countStyle new">N</span>';
//						var photoPath = imgPaths.filter(function(d){
//							return d.MailAddress == item.mailSenderAddr.trim();
//						});
//						photoPath = photoPath.length == 0 ? "" : photoPath[0].PhotoPath;
						var photoPath = "";
						var sentDate = item.mailSentDateStr.replaceAll("-", ".").split(" ")[0];
						
						divWrap
							.append($("<a class='PN_mCont'></a>").attr("onclick", mailOpenMethod)
							.append($("<span class='PN_mImg'></span>"))
							.append($("<strong class='PN_mTitle'></strong>").text(item.subject))
							.append($("<div class='PN_mTxt'></div>")
							.append($("<span class='PN_mName'></span>").text(item.mailSender))
							.append($("<span class='PN_mTime'></span>").text(sentDate))))
							.append($("<a class='PN_btnTray'></a>"))
								.attr("data-delete-mail-info", JSON.stringify([{ "boxName": "INBOX", "msgId": item.mailId, "sender": item.mailSenderAddr, "uid": item.uid }]))
								.attr("onclick", "javascript:LeftTabSummary.mail.delete(this);");
						
						var photoColorIdx = (idx%5) +1;
						if(!photoPath){
							divWrap.find(".PN_mImg")
								//.append($("<b class='mColor'></b>").text(item.mailSender.substring(0, 1)));
								.append($("<b></b>")
									.attr("class", 'mColor mColor' + photoColorIdx)
									.text(item.mailSender.substring(0, 1)));
						}else{
							divWrap.find(".PN_mImg")
								.append($("<img>")
									.attr("src", photoPath)
									.attr("onerror", "coviCmn.imgError(this, true);"));
						}
						
						if(item.mailSentDateStr.split(" ")[0] == nowDate){
							divWrap.find(".PN_mTitle")
								.text(" " + item.subject)
								.prepend($("<span class='countStyle new'></span>").text("N"));
						}
						
						mailWrap += $(divWrap)[0].outerHTML;
					});

					$(".PN_mailInner").append(mailWrap);
//					addClassImgBox(".PN_mailInner");
				}else{
					if (mode == 'append'){
						LeftTabSummary.mail.loadComplete = true;
					}
					else {
						$(".PN_infoCont .PN_mailArea .PN_mailCont .PN_mailListBox .PN_mailInner").empty().append(
							'<div class="PN_nolist">'+
							'	<p class="list_none"><spring:message code="Cache.lbl_NoMailList"/></p>'+
							'</div>'
						);
					}
				}
			});
		},
		capacity: function(){
			var userMail = Common.getSession("UR_Mail");
			
			// 메일 세션 세팅
			if(!$("#inputUserId").val()) $("#inputUserId").val(Common.getSession("DN_Code") + "_" + Common.getSession("USERID"));
			
			if(!sessionStorage.getItem($("#inputUserId").val())) MailCommon.getUserSession();
			
			FolderAPI.getList(userMail).done(function(data){
				if(data.length){
					var folderSizeInfo = data.filter(function(d){
						return d.hasOwnProperty("t_totalMailSize");
					});
					folderSizeInfo = folderSizeInfo[0];
					var totalMailSize = coviFile.convertFileSize(folderSizeInfo.t_totalMailSize);
					var totalMailSizeCap = coviFile.convertFileSize(folderSizeInfo.t_totalMailSizeCap);
					var totalMailSizeSub = coviFile.convertFileSize(Number(folderSizeInfo.t_totalMailSizeCap) - folderSizeInfo.t_totalMailSize);
					var totalMailSizePer = folderSizeInfo.t_totalMailSizePer;
					var folderCountInfo = data.filter(function(d){
						return d.hasOwnProperty("folderName") && d.folderName == "INBOX";
					});
					var noSeenCount = Number(folderCountInfo[0].noSeenCount) >= 100 ? "99+" : folderCountInfo[0].noSeenCount;
					
					$(".PN_capacityTit strong").text(totalMailSize);
					$(".PN_capacityAll").text("/ " + totalMailSizeCap);
					$(".PN_capacityRest").text("<spring:message code='Cache.lbl_Spare'/> " + totalMailSizeSub); // 여유
					$(".PN_capacityBox .PN_capacityBar").css("width", totalMailSizePer + "%");
					$(".PN_linkBox .l_link1_2 .l_Num").text(noSeenCount);
				}else{
					$(".PN_capacityTit strong").text("-");
					$(".PN_capacityAll").text("/ -");
					$(".PN_capacityBox .PN_capacityBar").css("width", 0 + "%");
				}
			});
		},
		read: function(pMailID, pUID){
			var _query = "/mail/userMail/goMailWindowPopup.do?";
			var _queryParam = {
				messageId: pMailID.replace("%3C", "<").replace("%3E", ">"),
				folderNm: MailCommon.convertCode("INBOX"),
				viewType: "LST",
				sort: "",
				uid: pUID,
				userMail: Common.getSession("UR_Mail"),
				inputUserId: $("#inputUserId").val(),
				popup: "Y",
				CLSYS: "mail",
				isSendMail: undefined
			};
			_query += $(_queryParam).serializeQuery();
			window.open(_query, "Mail Read" + stringGen(10), "height=700, width=1000");
		},
		delete: function(target){
			var _target = target;
			Common.Confirm("<spring:message code='Cache.msg_Common_08' />", "Confirmation Dialog", function(result){ // 선택한 항목을 삭제하시겠습니까?
				if(result){
					MessageAPI.deleteMessage(Common.getSession("UR_Mail"), "INBOX", "MD", JSON.parse($(_target).attr("data-delete-mail-info"))).done(function(){
						$(".PN_mailInner").empty();
						LeftTabSummary.mail.pageNo = 1;
						LeftTabSummary.mail.show();
						Common.Inform("<spring:message code='Cache.msg_DeleteResult' />"); // 성공적으로 삭제되었습니다
					});
				}
			});
		}
	},
	approval: {
		domainDataList: {},
		show: function(){
			console.log('결재정보를 가져옴');
			LeftTabSummary.approval.get();
		},
		toggleBox: function(target){
 			$(target).parents().next(".PN_aprListBox").slideToggle(500);
 			$(target).toggleClass("PN_btnSlide_close");
 			$(target).toggleClass("PN_btnSlide_open");
		},
		setEmptyList: function(target){
			$(target).empty().append(
				'<div class="PN_nolist PN_nolist_bd"">'+
				'	<p class="list_none"><spring:message code="Cache.msg_NoDataList"/></p>'+
				'</div>'
			)
		},
		get: function(){
			$.ajax({
				url: "/groupware/pnPortal/getApprovalList.do",
				type: "POST",
				data: {
					"businessData1": "APPROVAL"
				},
				success: function(data){
					if(data.status == "SUCCESS"){
						var apvListData = data.list.approval;
						var procListData = data.list.process;
						
						LeftTabSummary.approval.setApproval(apvListData);
						LeftTabSummary.approval.setProcess(procListData);
					}
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/groupware/pnPortal/getApprovalList.do", response, status, error);
				}
			});
		},
		setApproval: function(listData){
			if(listData.length > 0){
				var apvHtml = "";
				
				$.each(listData, function(idx, item){
					var divWrap = $("<div class='PN_aprList'></div>");
					var approvalStr = "";
					var rejectStr = "";
					LeftTabSummary.approval.domainDataList[item.ProcessID] = item.DomainDataContext;
					
					var isUsePwd = "N";
					if(item.SchemaContext) isUsePwd = this.SchemaContext.scWFPwd.isUse;
					
					switch(item.FormSubKind){
						case "T005": // 후결
							approvalStr = "<spring:message code='Cache.lbl_apv_Confirm' />"; // 확인
							break;
						case "T009": // 합의
							approvalStr = "<spring:message code='Cache.lbl_apv_agree' />"; // 동의
							rejectStr = "<spring:message code='Cache.lbl_apv_disagree' />"; // 거부
							break;
						case "T018": // 공람
							approvalStr = "<spring:message code='Cache.lbl_apv_Confirm' />"; // 확인
							break;
						case "T019": // 확인결재
							approvalStr = "<spring:message code='Cache.lbl_apv_Confirm' />"; // 확인
							break;
						default:
							approvalStr = "<spring:message code='Cache.btn_Approval2' />"; // 승인
							rejectStr = "<spring:message code='Cache.lbl_apv_reject' />"; // 반려
							break;
					}
					
					divWrap
						.append($("<a class='PN_apCont'></a>")
							.attr("onclick", "openApvPopup('Approval', '" + item.ProcessID + "', '" + item.WorkItemID + "', '" + item.PerformerID + "', '" + item.ProcessDescriptionID + "', '" + item.FormSubKind + "', '" + item.FormInstID + "', '" + item.FormID + "', '" + sessionObj["USERID"] + "', '" + item.FormPrefix + "');")
						.append($("<span class='PN_apImg'></span>"))
						.append($("<strong class='PN_apTitle'></strong>").text(item.FormSubject))
						.append($("<div class='PN_apTxt'></div>")
						.append($("<span class='PN_apName'></span>").text(item.InitiatorName))
						.append($("<span class='PN_apTime'></span>").text(item.Created))))
						.append($("<div class='PN_apBtn1'></div>")
						.append($("<a class='PN_btn_Approval'></a>")
							.attr("onclick", "onClickApvBtn('" + item.FormSubKind + "', '" + item.TaskID + "', 'approved', '" + item.FormInstID + "', '" + item.ProcessID + "', '" + item.UserCode + "', '" + isUsePwd + "', '" + item.ProcessName + "', '" + item.FormID + "', '" + item.WorkItemID + "', '" + item.formDraftkey + "');")
							.text(approvalStr)))
						.append($("<div class='PN_apBtn2'></div>"));
					
					if(rejectStr){
						divWrap.find(".PN_apBtn1")
							.append($("<a class='PN_btn_Reject'></a>")
								.attr("onclick", "onClickApvBtn('" + item.FormSubKind + "', '" + item.TaskID + "', 'reject', '" + item.FormInstID + "', '" + item.ProcessID + "', '" + item.UserCode + "', '" + isUsePwd + "', '" + item.ProcessName + "', '" + item.FormID + "', '" + item.WorkItemID + "', '" + item.formDraftkey + "');")
								.text(rejectStr));
					}
					
					if(item.PhotoPath){
						divWrap.find(".PN_apImg")
							.append($("<img>")
								.attr("src", item.PhotoPath)
								.attr("onerror", "onErrorImage(this, '" + item.InitiatorName.substring(0, 1) + "');"));
					}else{
						divWrap.find(".PN_apImg")
							.append($("<span class='PN_apImg'></span>")
							.append($("<b class='mColor'></b>").text(item.InitiatorName.substring(0, 1))));
					}
					
					if(item.IsComment == "Y"){
						divWrap.find(".PN_apBtn2")
							.append($("<a class='PN_btn_Comment'></a>")
								.attr("onclick", "javascript:openCommentView('" + item.ProcessID + "', '" + item.FormInstID + "');")
								.text("<spring:message code='Cache.lbl_Comments'/>")); // 댓글
					}
					
					if(item.IsFile == "Y"){
						divWrap.find(".PN_apBtn2")
						 	.append($("<a class='PN_btn_File'></a>")
								.attr("onclick", "javascript:openFileList(this, '" + item.FormInstID + "');")
								.text("<spring:message code='Cache.lbl_apv_filelist'/>")); // 첨부파일
					}
					
					apvHtml += $(divWrap)[0].outerHTML;
				});
				
				$(".PN_aprListArea .PN_aprBox1 .PN_aprListBox").empty().append(apvHtml);
			}else{
				LeftTabSummary.approval.setEmptyList($(".PN_aprListArea .PN_aprBox1 .PN_aprListBox"))
			}
			
			//addClassImgBox(".PN_aprListArea .PN_aprBox1 .PN_aprListBox");
		},
		setProcess: function(listData){
			if(listData.length > 0){
				var procHtml = "";
				
				$(listData).each(function(idx, item){
					var cnt = 0;
					statusCheck = false;
					LeftTabSummary.approval.domainDataList[item.ProcessID] = item.DomainDataContext;
					var divWrap = $("<div class='PN_aprList'></div>");
					var dataObj = Object.toJSON(item.DomainDataContext);
					var objGraphicList = ApvGraphicView.getGraphicData(dataObj, true);
					var statusCnt = 0;
					var statusCnt2 = 0;
					var firstImgPath = "";
					var nowImgPath = "";
					var firstPersonName = "";
					var nowPersonName = "";
					
					for(var i = 0; i < objGraphicList.length; i++){
						var division = objGraphicList[i];
						var steps = division.steps;
						for(var j = 0; j < steps.length; j++){
							var step = steps[j];
							var substeps = step.substeps;
							cnt++;
							
							for(var k = 0; k < substeps.length; k++){
								var substep = substeps[k];
								var substep2 = substep.substeps;
								if(i == 0 && j == 0){					//기안부서의 기안자
									firstImgPath = substeps[0].photo;
									firstPersonName = substeps[0].name;
								}else{
									if(substeps[k].waitCircle == "cirBlue"){
										if(typeof substep2 === 'object'){
											for(var l=0; l < substep2.length; l++){
												if(substep2[l].state == "wait"){
													statusCheck = true;
													nowImgPath = substep2[l].photo;
													nowPersonName = substep2[l].name;
													cnt = 0;
												}
											}
										}else{
											statusCheck = true;
											nowImgPath = substeps[k].photo;
											nowPersonName = substeps[k].name;
											cnt = 0;
										}
										if(substeps[k].state == "wait"){
											statusCheck = true;
											nowImgPath = substeps[k].photo;
											nowPersonName = substeps[k].name;
											cnt = 0;
										}
									}else if(k == substeps.length-1){
										if(statusCheck == false){
											++statusCnt;
										}
									}
								}
							}
							statusCnt2 = cnt;
						}
					}
					
					divWrap.append($("<a class='PN_apCont'></a>")
							.attr("onclick", "javascript:openApvPopup(\'Process\', '" + item.ProcessID + "', '" + item.WorkItemID + "', '" + item.PerformerID + "', '" + item.ProcessDescriptionID + "', '" + item.FormSubKind + "', '" + item.FormInstID + "', '" + item.FormID + "', '" + sessionObj["USERID"] + "', '" + item.FormPrefix + "');")
								.append($("<strong class='PN_apTitle'></strong>").text(item.FormSubject))
								.append($("<div class='PN_apTxt'></div>")
									.append($("<span class='PN_apName'></span>").text(item.InitiatorName))
									.append($("<span class='PN_apTime'></span>").text(item.Finished)))
								.append($("<ul class='PN_apProcess'></ul>")))
							.append($("<div class='PN_apBtn2'></div>"));
					
					if(firstImgPath){
						divWrap.find(".PN_apProcess")
							.append($("<li></li>")
								.append($("<span class='PN_apImg2'></span>")
									.append($("<img>")
										.attr("src", firstImgPath)
										.attr("onerror", "onErrorImage(this, '" + firstPersonName.substring(0, 1) + "');"))));
					}else{
						divWrap.find(".PN_apProcess")
							.append($("<li></li>")
								.append($("<span class='PN_apImg2'></span>")
									.append($("<b class='mColor'></b>").text(firstPersonName.substring(0, 1)))));
					} 
					
					if(statusCnt != 0){
						divWrap.find(".PN_apProcess")
							.append($("<li></li>")
								.append($("<span class='PN_apImg2'></span>")
									.append($("<b class='mPlus'></b>").text("+" + statusCnt))));
					}
						 
					if(statusCheck){
						if(nowImgPath){
							divWrap.find(".PN_apProcess")
								.append($("<li></li>")
									.append($("<span class='PN_apImg2 active'></span>")
										.append($("<img>")
											.attr("src", nowImgPath)
											.attr("onerror", "onErrorImage(this, '" + nowPersonName.substring(0, 1) + "');"))));
						}else{
							divWrap.find(".PN_apProcess")
								.append($("<li></li>")
									.append($("<span class='PN_apImg2 active'></span>")
										.append($("<b class='mColor'></b>").text(nowPersonName.substring(0, 1)))));
						}
						
						if(statusCnt2 != 0){
							divWrap.find(".PN_apProcess")
								.append($("<li></li>")
									.append($("<span class='PN_apImg2'></span>")
										.append($("<b class='mPlus'></b>").text("+" + statusCnt2))));
						}
					}
					
					if(item.IsComment == "Y"){
						divWrap.find(".PN_apBtn2")
							.append($("<a class='PN_btn_Comment'></a>")
								.attr("onclick", "javascript:openCommentView('" + item.ProcessID + "', '" + item.FormInstID + "');")
								.text("<spring:message code='Cache.lbl_Comments'/>")); // 댓글
					}
					
					if(item.IsFile == "Y"){
						divWrap.find(".PN_apBtn2")
							.append($("<a class='PN_btn_File'></a>")
								.attr("onclick", "javascript:openFileList(this, '" + item.FormInstID + "');")
								.text("<spring:message code='Cache.lbl_apv_filelist'/>")); // 첨부파일
					}
					
					procHtml += $(divWrap)[0].outerHTML;
				});

				$(".PN_aprListArea .PN_aprBox2 .PN_aprListBox").empty().append(procHtml);
			}
			else{
				LeftTabSummary.approval.setEmptyList($(".PN_aprListArea .PN_aprBox2 .PN_aprListBox"))
			}
			
			//addClassImgBox(".PN_aprListArea .PN_aprBox2 .PN_aprListBox");
		}
	},
	schedule: {
		show: function(){
			LeftTabSummary.schedule.setDate('today');
		},
		setDate: function(mode){
			var sDateObj;
			var sDate;
			var eDate;
			if (mode == 'today'){
				sDateObj = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss"));
				sDate = schedule_GetSunday(sDateObj);
				eDate = schedule_AddDays(schedule_SetDateFormat(sDate, "/"), 6);
			}
			else {
				sDateObj = new Date($("#schStartDate").text().replaceAll(".", "/"));
				if (mode == 'PREV'){
					sDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, -7), ".");
					eDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, -1), ".");
				}
				else {
					sDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, 7), ".");
					eDate = schedule_SetDateFormat(schedule_AddDays(sDateObj, 13), ".");
				}
			}
	
			$("#schStartDate").text(schedule_SetDateFormat(sDate, "."));
			$("#schEndDate").text(schedule_SetDateFormat(eDate, "."));
			
			LeftTabSummary.schedule.get();
		},
		get: function(){
			var folderCheckList = ";";
			var sDate = $("#schStartDate").text();
			var eDate = $("#schEndDate").text();
			var sDateObj = new Date(sDate.replaceAll(".", "/"));
			var schList = {};
			var schKeyList = null;
			
			for(var i = 0; i < 7; i++){
				var dayStr = schedule_SetDateFormat(schedule_AddDays(sDateObj, i), ".");
				schList[dayStr] = new Array();
			}
			
			schKeyList = Object.keys(schList);
			
			if(Object.keys(schAclArray).length == 0){
				scheduleUser.setAclEventFolderData();
			}
			
			$(schAclArray.read).each(function(idx,obj){
				folderCheckList += (obj.FolderID + ";");
			});
			
			$.ajax({
				url: "/groupware/schedule/getList.do",
				type: "POST",
				data: {
					"FolderIDs": folderCheckList,
					"StartDate": sDate.replaceAll(".", "-"),
					"EndDate": eDate.replaceAll(".", "-"),
					"UserCode": Common.getSession('UR_Code'),
					"lang": Common.getSession('lang')
				},
				success: function(data){
					var listData = data.list;
					var photoPaths = [];
					
					if(listData && listData.length > 0){
						var userCodes = listData.map(function(d){
							return d.OwnerCode;
						});
						//photoPaths = getProfileImgPath("Code", userCodes.join(";"), "");
						
						$.each(schKeyList, function(idx, key){
							schList[key] = listData.filter(function(d){
								var keyDateTime = new Date(key.replaceAll(".", "/")).getTime();
								var startDateTime = new Date(d.StartDate.replaceAll("-", "/")).getTime();
								var endDateTime = new Date(d.EndDate.replaceAll("-", "/")).getTime();
								
								return keyDateTime >= startDateTime && keyDateTime <= endDateTime;
							});
							
							schList[key].sort(function(a, b){
								return a.StartTime < b.StartTime ? -1 : 0;
							});
						});
					}
					
					LeftTabSummary.schedule.set(schList, photoPaths);
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/groupware/schedule/getList.do", response, status, error);
				}
			});
		},
		set: function(schList, photoPaths){
			var schHtml = "";
			var schKeyList = Object.keys(schList);
			var nowDateTime = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss")).getTime();
			
			$.each(schKeyList, function(kIdx, key){
				var divWrap = $("<div class='PN_scheduleBox'></div>");
				
				if(schList[key].length > 0){
					divWrap
						.append($("<strong class='PN_scDate'></strong>").text(key))
						.append($("<ul class='PN_scheduleList'></ul>"));
					
					$.each(schList[key], function(idx, item){
						var keyEDateTime = new Date(key.replaceAll(".", "/") + " " + item.EndTime).getTime();
						var activeClass = keyEDateTime < nowDateTime ? "" : "active";
						var photoPath = photoPaths.filter(function(d){
							return d.UserCode == item.RegisterCode;
						});
						photoPath = photoPath.length == 0 ? "" : photoPath[0].PhotoPath;
						
						divWrap.find(".PN_scheduleList")
							.append($("<li class='" + activeClass + "'></li>")
								.attr("onclick", "scheduleUser.goDetailViewPage('Webpart', " + item.EventID + ", " + item.DateID + ", " + item.RepeatID + ", '" + item.IsRepeat + "', " + item.FolderID + ")")
							.append($("<a href='#' class='PN_scCont'></a>")
							.append($("<span class='PN_scImg'></span>"))
							.append($("<strong class='PN_scTitle'></strong>").text(item.Subject))
							.append($("<span class='PN_scTime'></span>").text(item.StartTime + " ~ " + item.EndTime))));
						
						if(photoPath){
							divWrap.find(".PN_scImg")
								.append($("<img>")
									.attr("src", photoPath)
									.attr("onerror", "onErrorImage(this, '" + item.MultiRegisterName.substring(0, 1) + "');"));
						}else{
							divWrap.find(".PN_scImg")
								.append($("<b class='mColor'></b>").text(item.MultiRegisterName.substring(0, 1)));
						}
					});
				}else{
					divWrap.addClass("PN_nolist")
						.append($("<strong class='PN_scDate'></strong>").text(key))
						.append($("<ul class='PN_scheduleList'></ul>")
						.append($("<li></li>")
						.append($("<strong></strong>").text("<spring:message code='Cache.lbl_NoRegistSchedule'/>")) // 등록된 일정이 없습니다.
						.append($("<p></p>").html("<spring:message code='Cache.msg_RegistImportantSchedule'/>")) // 효율적인 일정 관리를 위해<br />중요하거나 반복되는 업무를 등록해 보세요.
						.append($("<a href='#' class='PN_btnRegist'></a>")
							.attr("onclick", "addSchedule('" + key + "')")
							.text("<spring:message code='Cache.lbl_RegistSchedule'/>")))); // 일정 등록하기
				}
				
				schHtml += $(divWrap)[0].outerHTML;
			});
			
			$(".PN_scheduleInner").empty().append(schHtml);
			//addClassImgBox(".PN_scheduleInner");
		}
	},
	task: {
		show: function(mode, setMode){
			var nowDateTime = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss")).getTime();
			
			$.ajax({
				url: "/groupware/biztask/getAllMyTaskList.do",
				type: "GET",
				data: {
					"userCode" : Common.getSession("UR_Code"),
					"stateCode" : ""
				},
				success: function(data){
					var listData = null;
					var myTaskStr = '';
					var todayTaskStr = '';
					var isEmptyMyTask = false;
					var isEmptyTodayTask = false;
					var todayCnt = 0;
					var waitingNum = 0;
					var progressNum = 0;
					var delayNum = 0;
					var completeNum = 0;
					var taskHtml = "";
					var todayHtml = "";
					
					if(mode == "TF"){
						listData = data.ProjectTaskList;
					}else if(mode == "TASK"){
						listData = data.TaskList;
					}else{
						listData = $.merge(data.ProjectTaskList, data.TaskList);
					}
					
					if(listData && listData.length > 0){
						listData.sort(function(a, b){
							return a.EndDate < b.EndDate ? -1 : 0;
						});
						
						$.each(listData, function(idx, item){
							var sDateTime = new Date(item.StartDate.replaceAll("-", "/")).getTime();
							var eDateTime = new Date(item.EndDate.replaceAll("-", "/")).getTime();
							var isOwner = (item.RegisterCode == sessionObj["USERID"] || item.OwnerCode == Common.getSession("UR_Code")) ? "Y" : "N";
							
							switch(item.State){
								case "Waiting":
									waitingNum++;
									break;
								case "Process":
									if(Number(item.DelayDay) > 0){
										delayNum++;
									}else{
										progressNum++;
									}
									break;
								case "Complete":
									completeNum++;
									break;
							}
							
							if(setMode == "List"){
								if(idx < 3){
									var liWrap = $("<li></li>");
									var onclickEvent = "";
									var titleStr = "";
									
									if(item.hasOwnProperty("ATName")){
										onclickEvent = "javascript:openTaskPopup('TF', '" + item.CU_ID + "', '" + item.AT_ID + "', 'Y');";
										titleStr = item.ATName;
									}else{
										onclickEvent = "javascript:openTaskPopup('TASK', '" + item.FolderID + "', '" + item.TaskID + "', '" + isOwner + "');";
										titleStr = item.Subject;
									}
									
									liWrap.append($("<a href='#' class='PN_tsCont'></a>")
												.attr("onclick", onclickEvent)
											.append($("<strong class='PN_tsTitle'></strong>").text(titleStr))
											.append($("<div class='PN_tsTxt'></div>")
												.append($("<span class='PN_tsState'></span>").text(item.TaskState))
												.append($("<span class='PN_tsDate'></span>").text("~" + item.EndDate.replaceAll("-", "."))))
											.append($("<div class='PN_graphBox'></div>")
												.append($("<div class='PN_graph'></div>")
													.append($("<span class='PN_graphBar'></span>").css("width", item.Progress + "%")))
												.append($("<span class='PN_graphPer'></span>").text(item.Progress + "%"))));
									
									taskHtml += $(liWrap)[0].outerHTML;
								}
								
								if($(".PN_TodayList li").length < 3 
									&& (nowDateTime >= sDateTime && nowDateTime <= eDateTime)){
									var liWrap = $("<li></li>");
									var onclickEvent = "";
									var titleStr = "";
									var dDayStr = "";
									
									if(item.hasOwnProperty("ATName")){
										onclickEvent = "javascript:openTaskPopup('TF', '" + item.CU_ID + "', '" + item.AT_ID + "', 'Y');";
										titleStr = item.ATName;
									}else{
										onclickEvent = "javascript:openTaskPopup('TASK', '" + item.FolderID + "', '" + item.TaskID + "', '" + isOwner + "');";
										titleStr = item.Subject;
									}
									
									if(item.DelayDay == 0){
										dDayStr += "<spring:message code='Cache.lbl_UntilToday'/>" // 오늘까지
									}else{
										dDayStr = item.DelayDay > 0 ? "D+" + item.DelayDay : "D" + item.DelayDay;
									}
									
									liWrap.append($("<a href='#' class='PN_tlCont'></a>")
										.attr("onclick", onclickEvent)
										.append($("<span class='PN_tlDday'></span>").text(dDayStr))
										.append($("<strong class='PN_tlTitle'></strong>").text(titleStr))
										.append($("<div class='PN_tlTxt'></div>")
											.append($("<span class='PN_tlState'></span>").text("<spring:message code='Cache.lbl_TFProgressing'/>: " + item.Progress + "%")) // 진행율
											.append($("<span class='PN_tlDate'></span>").text("~" + item.EndDate.replaceAll("-", ".")))));
									
									todayHtml += $(liWrap)[0].outerHTML;
								}
								
								$(".PN_taskList").empty().append(taskHtml);
								$(".PN_TodayList").empty().append(todayHtml);
							}
						});
					}
					
					if(setMode == "List"){
						if(!taskHtml){
							$(".PN_taskList").hide();
							$(".PN_taskBox3 .PN_nolist").show();
						}
						
						if(!todayHtml){
							$(".PN_TodayList").hide();
							$(".PN_taskBox4 .PN_nolist").show();
						}
						
						var totalNum = waitingNum + progressNum + delayNum + completeNum;
						$(".PN_tabList .PN_tab1_5 .countStyle").text(totalNum);
					}
					
					LeftTabSummary.task.setCircularGraph(waitingNum, progressNum, delayNum, completeNum);
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/groupware/biztask/getAllMyTaskList.do", response, status, error);
				}
			});
			
			LeftTabSummary.task.reportList();
		},
		setCircularGraph : function(waitingNum, progressNum, delayNum, completeNum){
			var totalNum = waitingNum + progressNum + delayNum + completeNum;
			
			if(totalNum != 0){
				var perArr = [
					{ percent: waitingNum / totalNum * 100, style: "40px solid #0E8FD4" },
					{ percent: progressNum / totalNum * 100, style: "40px solid #48C5EE" },
					{ percent: delayNum / totalNum * 100, style: "40px solid #246C95" },
					{ percent: completeNum / totalNum * 100, style: "40px solid #1BC4DF" }
				];
				
				perArr.sort(function(a, b){
					return a.percent > b.percent ? -1 : a.percent < b.percent ? 1 : 0;
				});
				
				$(".slice").removeClass("gt50");
				
				$.each(perArr, function(idx, item){
					if(idx == 0){
						$(".cycleBg .pie").css("border", item.style);
					}else{
						var percent = 0;
						
						for(var i = idx; i < perArr.length; i++){
							percent += perArr[i].percent;
						}
						
						$(".slice").eq(idx-1).find(".pie").css("border", item.style);
						$(".slice").eq(idx-1).find(".pie").eq(0).css("transform", "rotate(" + (3.6*percent) + "deg)");
						
						if(percent > 49) {
							$(".slice").eq(idx-1).addClass("gt50");
						}
					}
				});
			}else{
				$(".cycleBg .pie").css("border", "40px solid rgb(221, 221, 221)");
				$(".slice .pie").css("transform", "rotate(0deg)");
			}
			
			$(".PN_cycleList .cyNum1").text(progressNum);
			$(".PN_cycleList .cyNum2").text(completeNum);
			$(".PN_cycleList .cyNum3").text(waitingNum);
			$(".PN_cycleList .cyNum4").text(delayNum);
			$(".PN_cycleCont .cycleTxt .cNum").text(totalNum);
		},
		reportList: function(){
			var nowDate = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd HH:mm:ss"));
			var dayArr = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
			var sun = schedule_GetSunday(nowDate);
			var mon = schedule_AddDays(schedule_SetDateFormat(sun, '/'), 1);
			var fri = schedule_AddDays(schedule_SetDateFormat(sun, '/'), 5);
			var sDate = schedule_SetDateFormat(mon, '-');
			var eDate = schedule_SetDateFormat(fri, '-');
			
			$.ajax({
				url: "/groupware/bizreport/getTaskReportWeeklyList.do",
				type: "GET",
				data: {
					"startDate": sDate,
					"endDate": eDate,
					"userCode": sessionObj["USERID"],
					"projectCode": "",
					"taskGubunCode": ""
				},
				success: function(data){
					var taskDateList = data.TaskReportWeeklyList.map(function(data){
						return data.TaskDate;
					});
					taskDateList = taskDateList.filter(function(element, index){
						return taskDateList.indexOf(element) == index;
					}, []);
					var reportHtml = "";
					
					if(taskDateList && taskDateList.length > 0){
						for(var i = 1; i <= 5; i++){
							var chkDate = schedule_AddDays(schedule_SetDateFormat(sun, '/'), i);
							var chkDateStr = schedule_SetDateFormat(chkDate, '-');
							var reportDate = schedule_SetDateFormat(chkDate, '.').substr(5, chkDateStr.length);
							var divWrap = $("<div></div>");
							var deptStr = "<spring:message code='Cache.lbl_dept'/>: " + sessionObj["GR_Name"]; // 부서
							var chkDayStr = "(" + Common.getDic("lbl_sch_" + dayArr[chkDate.getDay()]) + ")";
							var stateStr = "";
							var btnStr = "";
							
							if(taskDateList.indexOf(chkDateStr) > -1){
								stateStr = "<spring:message code='Cache.lbl_ReportComplete'/>"; // 보고완료
								btnStr = "<spring:message code='Cache.lbl_DetailView'/>"; // 상세보기
							}else{
								stateStr = "<spring:message code='Cache.lbl_reportn'/>"; // 미보고
								btnStr = "<spring:message code='Cache.lbl_ReportRegist'/>"; // 보고서 등록
								divWrap.addClass("PN_rpNone");
							}
							
							divWrap.append($("<a href='#'></a>")
									.append($("<div class='PN_rpCont'></div>")
										.append($("<div class='PN_rpCont_l'></div>")
											.append($("<span class='PN_rpState'></span>").text(stateStr))
											.append($("<strong class='PN_rpDate'></strong>").text(reportDate + " ")
												.append($("<span></span>").text(chkDayStr))))
										.append($("<div class='PN_rpCont_r'></div>")
											.append($("<ul></ul>")
												.append($("<li></li>").text(deptStr)))))
									.append($("<a class='PN_btnDetail'></a>").text(btnStr)
										.attr("href", "/groupware/layout/bizreport_DailyReport.do?CLSYS=bizreport&CLMD=user&CLBIZ=BizReport&currentDate="+chkDateStr)));
							
					 		reportHtml += $(divWrap)[0].outerHTML;
						}
						$("#pn_reSliderParent").empty().append( $("<div>",{ "class" : "PN_rpSlider" }).html( reportHtml ) );
					}else{
						for(var i = 1; i <= 5; i++){
							var chkDate = schedule_AddDays(schedule_SetDateFormat(sun, '/'), i);
							var chkDateStr = schedule_SetDateFormat(chkDate, '-');
							var reportDate = schedule_SetDateFormat(chkDate, '.').substr(5, chkDateStr.length);
							var divWrap = $("<div class='PN_rpNone'></div>");
							var deptStr = "<spring:message code='Cache.lbl_dept'/>: " + sessionObj["GR_Name"]; // 부서
							var chkDayStr = "(" + Common.getDic("lbl_sch_" + dayArr[chkDate.getDay()]) + ")";
							
							divWrap.append($("<a href='#'></a>")
									.append($("<div class='PN_rpCont'></div>")
										.append($("<div class='PN_rpCont_l'></div>")
											.append($("<span class='PN_rpState'></span>").text("<spring:message code='Cache.lbl_reportn'/>")) // 미보고
											.append($("<strong class='PN_rpDate'></strong>").text(reportDate + " ")
												.append($("<span></span>").text(chkDayStr))))
										.append($("<div class='PN_rpCont_r'></div>")
											.append($("<ul></ul>")
												.append($("<li></li>").text(deptStr)))))
									.append($("<a class='PN_btnDetail'></a>").text("<spring:message code='Cache.lbl_ReportRegist'/>") // 보고서 등록
										.attr("href", "/groupware/layout/bizreport_DailyReport.do?CLSYS=bizreport&CLMD=user&CLBIZ=BizReport&currentDate="+chkDateStr)));
							
					 		reportHtml += $(divWrap)[0].outerHTML;
						}
						$("#pn_reSliderParent").empty().append( $("<div>",{ "class" : "PN_rpSlider" }).html( reportHtml ) );
					}
					
					taskScrolling();
				},
				error: function(response, status, error){
					CFN_ErrorAjax("/groupware/bizreport/getTaskReportDailyList.do", response, status, error);
				}
			});
		},
		setReportSlideEvent: function(){
			var slider = $(".PN_rpSlider");
			slider.slick({
				slide: "div",			// 슬라이드 되어야 할 태그 ex) div, li
				infinite : false,		// 무한 반복 옵션
				slidesToShow : 1,		// 한 화면에 보여질 컨텐츠 개수
				slidesToScroll : 1,		// 스크롤 한번에 움직일 컨텐츠 개수
				speed : 500,			// 다음 버튼 누르고 다음 화면 뜨는데까지 걸리는 시간(ms)
				arrows : true,			// 옆으로 이동하는 화살표 표시 여부
				dots : true,			// 스크롤바 아래 점으로 페이지네이션 여부
				autoplay : false,		// 자동 스크롤 사용 여부
				autoplaySpeed : 3000,	// 자동 스크롤 시 다음으로 넘어가는데 걸리는 시간 (ms)
				pauseOnHover : true,	// 슬라이드 이동	시 마우스 호버하면 슬라이더 멈추게 설정
				vertical : false,		// 세로 방향 슬라이드 옵션
				draggable : false		// 드래그 가능 여부
			});
		}
	}
}