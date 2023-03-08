<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<script type="text/javascript" src="/approval/resources/script/user/approvestat.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/approval/resources/script/user/common/ApprovalUserCommon.js<%=resourceVersion%>"></script>

<!-- 본문 시작 -->
<div class="cRContBottom mScrollVH noHeader">
	<div class="apprvalContent">
			  <div class="mainbox">
			    <!-- 컨텐츠 좌측 시작 -->
			    <div class="main_list"><!-- 좌우 폭 조정에 따라 값 변경(좌측 width값) -->
			      <!-- 타임라인 시작 -->
                  <div class="timeLine" id="timeLine">
                  </div>
                  <!-- 타임라인 끝 -->
                </div>
			    <!-- 컨텐츠 좌측 끝 -->
			    <!-- 컨텐츠 우측 시작 -->
			    <div class="boxin_view">
			      <ul class="layoutHam">
			        <!-- 미결함 시작 -->
			        <li class="hamBox">
			          <dl class="hamCont">
			            <dt class="hamTit"><span class="hamTxt"><spring:message code='Cache.MN_478' /></span><span class=" mNum" id="ApprovalCnt"></span><a onclick="CoviMenu_GetContent('/approval/layout/approval_ApprovalList.do?CLSYS=approval&CLMD=user&CLBIZ=Approval&mode=Approval');" class="mMore"></a></dt>
			            <dd class="boxScroll" id="ApprovalBoxList">
			            </dd>
			          </dl>
			        </li>
			        <!-- 미결함 끝 -->
			        <!-- 진행함 시작 -->
			        <li class="hamBox">
			          <dl class="hamCont">
			            <dt class="hamTit"><span class="hamTxt"><spring:message code='Cache.MN_479' /></span><span class=" mNum" id="ProcessCnt"></span><a onclick="CoviMenu_GetContent('/approval/layout/approval_ApprovalList.do?CLSYS=approval&CLMD=user&CLBIZ=Approval&mode=Process');" class="mMore"></a></dt>
			            <dd class="boxScroll" id="ProcessBoxList">
			            </dd>
			          </dl>
			        </li>
			        <!-- 진행함 끝 -->
			        <!-- 반려함 시작 -->
			        <li class="hamBox">
			          <dl class="hamCont">
			            <dt class="hamTit"><span class="hamTxt"><spring:message code='Cache.MN_481' /></span><a onclick="CoviMenu_GetContent('/approval/layout/approval_ApprovalList.do?CLSYS=approval&CLMD=user&CLBIZ=Approval&mode=Reject');" class="mMore"></a></dt>
			            <dd class="boxScroll" id="RejectBoxList">
			            </dd>
			          </dl>
			        </li>
			        <!-- 반려함 끝 -->
			        <!-- 완료함 시작 -->
			        <li class="hamBox">
			          <dl class="hamCont">
			            <dt class="hamTit"><span class="hamTxt"><spring:message code='Cache.MN_480' /></span><a onclick="CoviMenu_GetContent('/approval/layout/approval_ApprovalList.do?CLSYS=approval&CLMD=user&CLBIZ=Approval&mode=Complete');" class="mMore"></a></dt>
			            <dd class="boxScroll" id="CompleteBoxList">
			            </dd>
			          </dl>
			        </li>
			        <!-- 완료함 끝 -->
			      </ul>
			    </div>
			    <!-- 컨텐츠 우측 끝 -->
			  </div>
			  <!-- 본문 끝 -->
		<input type="hidden" data-type="field" id="ACTIONCOMMENT" value="" />
		<input type="hidden" data-type="field" id="ACTIONCOMMENT_ATTACH" value="" />
		<input type="hidden" data-type="field" id="SIGNIMAGETYPE" value="" />
	</div>						
</div>

<script type="text/javascript">
	//# sourceURL=Home.jsp
	//var firstImg;
	//var nowImg;
	var firstImgCode;
	var nowImgCode;
	var objGraphicList;
   var cnt = 0;
   var cnt2 = 0;
   var statusCheck = false;
   var ProfileImagePath = Common.getBaseConfig('ProfileImagePath').replace("{0}", Common.getSession("DN_Code"));
   var g_commentAttachList = [];
    
   var sessionObj = null; //전체호출
   //initHome(); 위치 이동.
    
	function initHome() {
		//$("header").empty();
		init();
	}
	
	function init(){
		sessionObj = Common.getSession(); //전체호출
		$("#detpNm").html(sessionObj["DEPTNAME"]);
		$("#userInfo").html(sessionObj["USERNAME"]);
		
		setListData();

		$(".top_n_menu").attr("class","top_main");

		$(".boxScroll").each(function(i, obj){
			if($(obj).find(".search_empty_wrap").length <= 0){
				$(obj).slimScroll({
					height: $(obj).height()+"px",
					width: "90%",
					alwaysVisible: true
				});
			}
		});
		
		setAlarmList();	// 나의 결재 현황
	}

	// 나의 결재 현황
	function setAlarmList() {
		$.ajax({
			url:"/approval/legacy/getAlarmList.do",
			data: {
				"businessData1":"APPROVAL"
			},
			type:"post",
			success:function (data) {
				$("#timeLine").empty();
				var html = "<div class='myTitle'><spring:message code='Cache.lbl_approval_myApproval'/></div>"; 	/* 나의 결재현황  */
				html += "<ul class='myList'>";
				
				if (typeof(data.list) != "undefined") {
					var week = new Array('일', '월', '화', '수', '목', '금', '토');
					var nowDate = $.datepicker.formatDate('yy/m/d',  new Date());
					var len = data.list.length;
					
					if (len > 0) {
	 					$.each(data.list, function(i, v) {
	  						$.each(v, function(i, v) {
	  							html += "<li class='myDD'>";
	  							if (nowDate == i) {
	  								html += "<div class='mTime'><span>" + "TODAY" + "</span></div>";
	  							} else {
	  								var sTitle;
	  								if(sessionObj["lang"].toUpperCase() == "KO"){
	  									sTitle = new Date(i).format("yyyy년 M월 d일 E");
	  								}else{
	  									sTitle =  new Date(i).format("yyyy.MM.dd");
	  								}
	  								html += "<div class='mTime'><span>" +sTitle + "</span></div>";
	  							}
	  							var tableClass = "";
	  							var spanClass = "";
	  							var spanText = "";
	  							$.each(v, function(i, v) {
	  								switch (v.MessageType) {
		  								case "COMMENT" : tableClass = "mComment"; spanClass = "bgComm"; spanText = "<spring:message code='Cache.lbl_comment'/> "; break; // 의견
		  								case "UPD_CONTEXT" : tableClass = "mUpdate"; spanText = "<spring:message code='Cache.lbl_approval_editContent'/> "; break;    // 내용 편집
		  								case "UPD_APVLINE" : tableClass = "mUpdate"; spanText = "<spring:message code='Cache.lbl_approval_editLine'/> "; break;		// 결재선 편집
		  								case "UPD_ATTACH" : tableClass = "mUpdate"; spanText = "<spring:message code='Cache.lbl_approval_editFile'/> "; break;	// 첨부파일 편집
		  								case "APPROVAL" : case "CHARGE" : tableClass = "mArrive"; spanText = "<spring:message code='Cache.lbl_approval_approvalApproval'/> "; break;		// 결재 도착
		  								case "COMPLETE" : tableClass = "mComplete"; spanText = "<spring:message code='Cache.lbl_Completed'/>"; break;	// 완료
		  								case "REJECT" : tableClass = "mReturn"; spanText = "<spring:message code='Cache.lbl_apv_reject'/>"; break;	// 반려
		  								case "CCINFO" : tableClass = "mCcinfo"; spanText = "<spring:message code='Cache.lbl_apv_cc'/> "; break;	// 참조
		  								case "CIRCULATION" : tableClass = "mCcinfo"; spanText = "<spring:message code='Cache.btn_apv_Circulate'/> "; break;	// 회람
		  								case "HOLD" : tableClass = "mHold"; spanText = "<spring:message code='Cache.lbl_apv_hold'/>"; break;	// 보류
		  								case "WITHDRAW" : tableClass = "mWithdraw"; spanText = "<spring:message code='Cache.lbl_Withdraw'/> "; break;	// 회수
		  								case "ABORT" : tableClass = "mWithdraw"; spanText = "<spring:message code='Cache.btn_apv_draftabort'/> "; break;	//기안 취소
		  								case "APPROVECANCEL" : tableClass = "mApprovalcancel"; spanText = "<spring:message code='Cache.lbl_CancelApproval'/> "; break;	// 승인 취소
		  								case "REDRAFT" : tableClass = "mRedraft"; spanText = "<spring:message code='Cache.lbl_apv_receive'/> "; break;	// 수신 
		  								case "CHARGEJOB" : tableClass = "mChargejob"; spanText = "<spring:message code='Cache.lbl_Role'/> "; break;	// 담당업무
		  								case "CONSULTATION" : tableClass = "mArrive"; spanText = "<spring:message code='Cache.lbl_apv_consultation'/> "; break;		// 검토
		  								case "CONSULTATIONCOMPLETE" : tableClass = "mArrive"; spanText = "<spring:message code='Cache.lbl_apv_consultation'/> <spring:message code='Cache.lbl_Completed'/>"; break;		// 검토 완료
		  								case "CONSULTATIONCANCEL" : tableClass = "mWithdraw"; spanText = "<spring:message code='Cache.lbl_apv_consultationCancel'/>"; break;		// 검토 취소
	  								}
	  								
	  								html += "<table class=" + tableClass + ">";
	  								html += "<tbody>";
	  								html += "<th><span class=" + spanClass + ">" + spanText + "</span></th>";
	  								html += "<td>";
	  								html += "<ul class='mLine'>";
	  								html += "<li class='mTit'>";
	  								if(v.State == "546") { // 회수, 기안취소된 문서는 양식본문을 확인할 수 없어 경고창 띄움
	  									html += "<a onClick=\"Common.Warning('<spring:message code='Cache.msg_apv_082' />');\"><span class='tBlue'>" + v.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</span></a>" + CFN_GetDicInfo(v.Subject);
	  								}
	  								else {
	  									html += "<a onClick='clickSubject(\"" + v.GotoURL + "\", \"" + v.FormPrefix + "\", \"" + v.FormID + "\", \"" + v.BusinessData1 + "\", \"" + v.BusinessData2 + "\", \"" + v.TaskID + "\");'><span class='tBlue'>" + v.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</span></a>" + CFN_GetDicInfo(v.Subject);	
	  								}
	  								html += "</li>";
	  								html += "<li class='mName'>" + CFN_GetDicInfo(v.Context) + "</li>";
	  								html += "<li class='mClock'>" + v.viewRegDate + "</li>";
	  								html += "</ul>";
	  								html += "</td>";
	  								html += "</tbody>";
	  								html += "</table>";
	  							});
	  							html += "</li>";
							});
						});
					}else{
						html +='<div class="search_empty_wrap" style="margin-top: 150px;"><span class="ico_search_empty"></span><span class="search_empty_txt"><spring:message code="Cache.msg_NoDataList"/></span></div>';	//조회할 목록이 없습니다.
					}
				}else{
					html +='<div class="search_empty_wrap" style="margin-top: 150px;"><span class="ico_search_empty"></span><span class="search_empty_txt"><spring:message code="Cache.msg_NoDataList"/></span></div>';	//조회할 목록이 없습니다.
				}
				html += "</ul>";
				$("#timeLine").append(html);

				$(".timeLine").slimScroll({
					height: $(".timeLine").height()+'px',
					alwaysVisible: true
				});
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/approval/legacy/getAlarmList.do", response, status, error);
			}
		});
	}
	// 제목 클릭
	function clickSubject(url, FormPrefix, FormID, BusinessData1, BusinessData2, TaskID){
		var width = 790;
		if (IsWideOpenFormCheck(FormPrefix, FormID) == true) {
			width = 1070;
		} else {
			width = 790;
		}
		
		CFN_OpenWindow(url+"&ExpAppID="+(typeof BusinessData2!="undefined"?BusinessData2:"")+"&taskID="+(typeof TaskID!="undefined"?TaskID:""), "", width, (window.screen.height - 100), "resize");
	}
	
	//결재함 (미결함, 진행함, 반려함, 완료함)
	function setListData(){
		$.ajax({
			url:"/approval/user/getHomeApprovalListData.do",
			data: {
				"businessData1":"APPROVAL"
			},
			type:"post",
			success:function (data) {
				
				//서명이미지 가져오기
				if(data.list.signimage.length > 0) { // 서명이미지 없는 경우 예외처리 추가
					setSignImage(data.list.signimage);	
				}
				
				// 미결함
				setApprovalBoxListData(data.list.approval);
				
				//진행함 
				setProcessBoxListData(data.list.process);
				setProcessBoxProfileImg();		// 진행함 결재선 그래픽 프로필사진 세팅
				
				//반려함
				setRejectBoxListData(data.list.reject);
				
				//완료함
				setCompleteBoxListData(data.list.complete);
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/approval/user/getApprovalListData.do", response, status, error);
			}
		});
	}
	
	// 미결함
	var arrDomainDataList = {};
	function setApprovalBoxListData(data){
		$("#ApprovalBoxList").empty();
		var ArrayFormInstID = new Array;
		var FormInstID = "";
		if(data.length > 0){
			$(data).each(function(index){
				if(arrDomainDataList == undefined) {
					arrDomainDataList = {};
				}
				arrDomainDataList[this.ProcessID] = this.DomainDataContext;
				
				ArrayFormInstID.push(this.FormInstID);
				var html = "";
				html = "<ul class='suspUl'>"
			         + "  <li>"
			         + "    <dl>"
			         + "      <dt><span style='cursor:pointer' class='msTit' onclick='onClickPopButton(\""+this.ProcessID+"\",\""+this.WorkItemID+"\",\""+this.PerformerID+"\",\""+this.ProcessDescriptionID+"\",\""+this.FormSubKind+"\",\"\",\"\",\""+this.FormID+"\",\"\",\""+this.UserCode+"\",\"APPROVAL\",\""+this.FormPrefix+"\",\""+this.BusinessData1+"\",\""+this.BusinessData2+"\",\""+this.TaskID+"\"); return false;' >"+this.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</span>";
		         
		         if(this.IsSecureDoc=="Y"){
		        	 html += "<span class='security'><spring:message code='Cache.lbl_apv_AuthoritySetting'/></span>"; //보안
		         }
		         if(this.IsFile=="Y"){
		        	 html += "<div class='fClip'><a onclick='onClickmClip(this,\""+this.ProcessID+"\",\""+this.FormInstID+"\");' class='mClip'><spring:message code='Cache.lbl_attach'/></a></div>"; //첨부
		         }
		         
		         var isUsePwd = "N";
		         if(this.SchemaContext != undefined && this.SchemaContext != "") {
		        	 isUsePwd = this.SchemaContext.scWFPwd.isUse;
		         }
		         
		         // 승인/반려
		         var buttonValueApproval = "";
		         var buttonValueReject = "";
		         
		         switch (this.FormSubKind) {
				case "T005":		// 후결
					buttonValueApproval = "<input id='btApproved' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_Confirm' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"approved\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
					break;
				case "T009":		// 합의
					buttonValueApproval = "<input id='btApproved' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_agree' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"approved\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
					buttonValueReject = "<input id='btReject' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_disagree' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"reject\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
	                var getApvAgrOpt = Common.getBaseConfig("ApvAgreementOpt");
	                if (getApvAgrOpt.length > 0){
	                	var arrApvOpt = getApvAgrOpt.split(";");
	                	if(arrApvOpt[0] == "Y") buttonValueApproval = "";
	                	if(arrApvOpt[1] == "Y") buttonValueReject = "";
	                }
					break;
				case "T018":		// 공람
					buttonValueApproval = "<input id='btApproved' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_Confirm' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"approved\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
					break;
				case "T019":		// 확인결재
					buttonValueApproval = "<input id='btApproved' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_Confirm' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"approved\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
					break;
				case "T023":		// 검토
					//buttonValueApproval = "<input id='btCAgree' class='mWBtn' type='button' value='<spring:message code='Cache.Cache.lbl_apv_agree' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"disagreed\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.ProcessName + "\",\"" + this.FormID + "\"); return false;'>";
					//buttonValueReject = "<input id='btCDisagree' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_disagree' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"agreed\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd  + "\",\"" + this.ProcessName + "\",\"" + this.FormID + "\"); return false;'>";
					break;
				default:
					buttonValueApproval = "<input id='btApproved' class='mWBtn' type='button' value='<spring:message code='Cache.btn_Approval2' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"approved\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
					buttonValueReject = "<input id='btReject' class='mWBtn' type='button' value='<spring:message code='Cache.lbl_apv_reject' />' onclick='doButtonAction(this, \""+this.FormSubKind+"\",\""+this.TaskID+"\",\"reject\",\""+this.FormInstID+"\",\"" + this.ProcessID + "\",\"" + this.UserCode + "\",\"" + isUsePwd + "\",\"" + this.FormID + "\",\"" + this.WorkItemID + "\",\"" + this.formDraftkey + "\"); return false;'>";
					break;
				}
		         
		        if(this.WorkItemID && $$(this.DomainDataContext).find("step > ou[wiid='" +this.WorkItemID+ "'] > person > consultation").length > 0
						&& $$(this.DomainDataContext).find("step > ou[wiid='" +this.WorkItemID+ "'] > person").find(">taskinfo").attr("status") !== "pending"){
					buttonValueApproval = "";
					buttonValueReject = "";
				} 
		         
			    html += "</dt>"
			    	 + "      <dd class='mPro'><a class='mainPro' style='cursor:default;'><img style=\"max-width: 100%; height: auto;\" src="+coviCmn.loadImage(this.PhotoPath)+" alt=\"\" onerror='coviCmn.imgError(this, true)"+"'></a></dd>"
			         + "      <dd class='sCont'>"
			         + "        	<spring:message code='Cache.lbl_apv_writer'/>: "+setUserFlowerName(this.InitiatorID, this.InitiatorName)+"<br>"  //기안자 
			         + "        	<spring:message code='Cache.lbl_apv_reqdate'/>: "+getStringDateToString("yyyy.MM.dd HH:mm:ss",this.Created) //기안일
			         + "      </dd>"
			         + "      <dd>"
			         + buttonValueApproval
			         + buttonValueReject
			         + "      </dd>"
			         + "    </dl>"
			         + "  </li>"
			         + "</ul>";
				$("#ApprovalBoxList").append(html);
			});
		}else{
			$("#ApprovalBoxList").append('<div class="search_empty_wrap"><span class="ico_search_empty"></span><span class="search_empty_txt"><spring:message code="Cache.msg_NoDataList"/></span></div>');	//조회할 목록이 없습니다.
		}

		FormInstID = ArrayFormInstID.join(",");
		getApprovalCnt();
	}
	
	var commentPopupTitle = '';
	var commentPopupButtonID = '';
	var commonWritePopupOnload;
	function doButtonAction(obj, subKind, taskID, kind, formInstID, processID, UserCode, isUsePwd, formID, workitemID, formDraftkey){
		commentPopupTitle = $(obj).val();		//$(obj).find('span').eq(0).text();
	    commentPopupButtonID = $(obj).attr("id");
		
    	commonWritePopupOnload = function(){
    		approvalDoButtonAction("APPROVAL", subKind, taskID, kind, $("#ACTIONCOMMENT").val(), formInstID, false, processID, UserCode, $("#SIGNIMAGETYPE").val(), false, "", _g_password, _g_authKey, formID, workitemID, formDraftkey); // 대결 처리 적용 안함
    	};

		var commonWritePopup = CFN_OpenWindow("/approval/CommentWrite.do?usePWD="+isUsePwd, "", 540, 549, "resize");
	}

	//진행함
	function setProcessBoxListData(data){
		$("#ProcessBoxList").empty();
		var ArrayFormInstID = new Array;
		var FormInstID = "";
		if(data.length > 0){
			$(data).each(function(index){
				cnt = 0;
				statusCheck = false;
				var dataObj = Object.toJSON(this.DomainDataContext);
				objGraphicList = ApvGraphicView.getGraphicData(dataObj, false);
			    var statusCnt = 0;
			    var statusCnt2 = 0;
				for(var i =0; i< objGraphicList.length; i++){
			        var division = objGraphicList[i];
				    var steps = division.steps;
			        for(var j =0; j < steps.length; j++){
				        var step = steps[j];
				        var substeps = step.substeps;
			        	cnt++;
			        	
				        for(var k=0; k<substeps.length; k++){
				        	var substep = substeps[k];
				        	var substep2 = substep.substeps;
					        if(i == 0 && j == 0){					//기안부서의 기안자
					        	//firstImg = substeps[0].photo;
					        	firstImgCode = substeps[0].code;
				            }else{
				            	if(substeps[k].waitCircle == "cirBlue"){
				            		if(typeof substep2 === 'object'){
				            			for(var l=0; l < substep2.length; l++){
				            				if(substep2[l].state == "wait"){
							            		statusCheck = true;
							            		//nowImg = substep2[l].photo;
							            		nowImgCode = substep2[l].code;
								            	cnt = 0;
				            				}
				            			}
				            		}else{
					            		statusCheck = true;
					            		//nowImg = substeps[k].photo;
					            		nowImgCode = substeps[k].code;
						            	cnt = 0;
				            		}
				            		if(substeps[k].state == "wait"){
					            		statusCheck = true;
					            		//nowImg = substeps[k].photo;
					            		nowImgCode = substeps[k].code;
						            	cnt = 0;
				            		}
				            	}else if(k == substeps.length-1){
				            		/* if(typeof substep2 === 'object' && substeps[k].state != "wait"){
							        	statusCnt += substep2.length;
							        }else if(statusCheck == false){
							        	statusCnt += substeps.length;
							        } */
				            		if(statusCheck == false){
				            			++statusCnt;
				            		}
				            	}
				            }
				        }
				        statusCnt2 = cnt;
			        }
				}
				ArrayFormInstID.push(this.FormInstID);
				var html = "";
				html = "<ul class='progUl'>"
			         + "  <li>"
			         + "    <dl>"
			         + "      <dt><span style='cursor:pointer' class='msTit' onclick='onClickPopButton(\""+this.ProcessID+"\",\""+this.WorkItemID+"\",\""+this.PerformerID+"\",\""+this.ProcessDescriptionID+"\",\""+this.FormSubKind+"\",\"\",\"\",\""+this.FormID+"\",\"\",\""+this.UserCode+"\",\"PROCESS\",\""+this.FormPrefix+"\",\""+this.BusinessData1+"\",\""+this.BusinessData2+"\",\""+this.TaskID+"\"); return false;'>"+this.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</span>"
			         if(this.IsFile=="Y"){
			        	 html += "<div class='fClip'><a onclick='onClickmClip(this,\""+this.ProcessID+"\",\""+this.FormInstID+"\");' class='mClip'><spring:message code='Cache.lbl_attach'/></a></div>"; //첨부
			         }
			    html += "      </dt>"
			         + "      <dd>"
			         + "        <ul class='mProgress'>"
			         + "          <li>"
			         // + "            <a class='mainPro' style='cursor:default;'><img style=\"max-width: 100%; height: auto;\" src="+firstImg+" alt=\"\" onerror='this.src=\"" + ProfileImagePath+"noimg.png\" "+"'></a>"
			         + "            <a class='mainPro' style='cursor:default;'><img style=\"max-width: 100%; height: auto; padding-left: 3px;padding-top: 3px; \" name='processProfileImg' userCode='"+firstImgCode+"' src='/HtmlSite/smarts4j_n/covicore/resources/images/covision/loding12.gif' alt='' onerror='coviCmn.imgError(this, true) "+"'></a>"
			         + "            <span class='tagW'><spring:message code='Cache.lbl_apv_Draft'/></span>"  //기안
			         + "          </li>"
			         if(statusCnt!=0){
			         	html  	+="          <li>"
				        	  	+ "            <span class='cirTxt'>+"+statusCnt+"</span>"
				         		+ "          </li>"
			         }
			         
			        if(statusCheck == true){
				         html += "          <li>"
				         	// + "            <a class='proBlue'><img style='max-width: 100%; height: auto;'  src="+nowImg+" alt=\"\"  onerror='this.src=\"" + ProfileImagePath+"noimg.png\" "+"'></a>"
					         + "            <a class='proBlue'><img style='max-width: 100%; height: auto; padding-left: 3px;padding-top: 3px;' name='processProfileImg' userCode='"+nowImgCode+"' src='/HtmlSite/smarts4j_n/covicore/resources/images/covision/loding12.gif' alt='' onerror='coviCmn.imgError(this, true) "+"'></a>"
					         //+ "            <span class='blueNum'>4</span>"
					         + "            <span class='tagB'><spring:message code='Cache.lbl_apv_inactive'/></span>" //대기
					         + "          </li>"
					         if(statusCnt2!=0){
					        	 html  	+= "          <li>"
						         		+ "            <span class='cirTxt'>+"+statusCnt2+"</span>"
						         		+ "          </li>";
					         }
			         }
			     html += "        </ul>"
			         + "      </dd>"
			         + "      <dd class='sCont' style='margin-top: 25px;'>"
				     + "      	<spring:message code='Cache.lbl_apv_writer'/>: "+setUserFlowerName(this.InitiatorID, this.InitiatorName)+"<br>"   //기안자
				     + "        <spring:message code='Cache.lbl_apv_reqdate'/>: "+getStringDateToString("yyyy.MM.dd HH:mm:ss",this.StartDate) //기안일
			         + "      </dd>"
			         + "    </dl>"
			         + "  </li>"
			         + "</ul>";

				$("#ProcessBoxList").append(html);
			});

		}else{
			$("#ProcessBoxList").append('<div class="search_empty_wrap"><span class="ico_search_empty"></span><span class="search_empty_txt"><spring:message code="Cache.msg_NoDataList"/></span></div>');	//조회할 목록이 없습니다.
		}

		FormInstID = ArrayFormInstID.join(",");
		getProcessCnt();
	}
	
	function setProcessBoxProfileImg(){
		var userCodeArr = new Array();
		$("img[name='processProfileImg']").each(function (idx, obj){
			var profileCode = $(obj).attr("usercode");
			if(!userCodeArr.includes(profileCode)){
				userCodeArr.push(profileCode);
			}
		});
		
		if(userCodeArr.length > 0){
			var photoPathObj = getProfileImagePath(userCodeArr.join(";"));
			    
		    $(photoPathObj).each(function(i, photo){
		    	$("img[name='processProfileImg'][usercode='"+photo.UserCode+"']").each(function (idx, obj){
		    		$(obj).css("padding", "0px");
		    		$(obj).attr("src", photo.PhotoPath);
		    		$(obj).attr("bindImg", "true");
		    	});
		    });
	    
    		$("img[name='processProfileImg'][bindImg!='true']").each(function (idx, obj){
    			$(obj).error();
    		});
		}
	    
	}
	
	function getApprovalCnt()
	{
		var cnt; 
		$.ajax({
			url:"/approval/user/getApprovalCnt.do",
			data:{
				businessData1 : "APPROVAL"
			},
			type:"post",
			success:function (data) {
				$("#ApprovalCnt").html(data.cnt);
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/approval/user/getApprovalCnt.do", response, status, error);
			}
		});
	}
	
	function getProcessCnt()
	{
		var cnt; 
		$.ajax({
			url:"/approval/user/getProcessCnt.do",
			type:"post",
			data:{
				businessData1 : "APPROVAL"
			},
			success:function (data) {
				$("#ProcessCnt").html(data.cnt);
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/approval/user/getProcessCnt.do", response, status, error);
			}
		});
	}

	//반려함
	function setRejectBoxListData(data){
		$("#RejectBoxList").empty();
		if(data.length > 0){
			$(data).each(function(index){
				var html = "";
				html = "<ul class='returnUl'>"
			         + "       <li>"
			         + "     	<dl>"
			         + "       	<dt><span style='cursor:pointer' class='msTit' onclick='onClickPopButton(\""+this.ProcessArchiveID+"\",\""+this.WorkitemArchiveID+"\",\""+this.PerformerID+"\",\""+this.ProcessDescriptionArchiveID+"\",\""+this.FormSubKind+"\",\"\",\"\",\""+this.FormID+"\",\"\",\""+this.UserCode+"\",\"REJECT\",\""+this.FormPrefix+"\",\""+this.BusinessData1+"\",\""+this.BusinessData2+"\",\""+this.TaskID+"\"); return false;'>"+this.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</span>";
			         if(this.IsFile=="Y"){
			        	 html += "<div class='fClip'><a onclick='onClickmClip(this,\""+this.ProcessID+"\",\""+this.FormInstID+"\");' class='mClip'><spring:message code='Cache.lbl_attach'/></a></div>"; //<spring:message code='Cache.lbl_apv_writer'/>
			         }
			    html += "        </dt>"
			         + "       	<dd class='mPro'><a class='mainPro' style='cursor:default;'><img style=\"max-width: 100%; height: auto;\" src="+coviCmn.loadImage(this.PhotoPath)+" alt=\"\"  onerror='coviCmn.imgError(this, true) "+"'></a></dd>"
			         + "       	<dd class='sCont'>"
			         + "			<spring:message code='Cache.lbl_apv_writer'/>: "+setUserFlowerName(this.InitiatorID, this.InitiatorName)+"<br>"  //기안자
			         + "			<spring:message code='Cache.lbl_EndDate'/>: "+getStringDateToString("yyyy.MM.dd HH:mm:ss",this.EndDate) //종료일
			         + "       	</dd>"
			         + "        </dl>"
			         + "       </li>"
			         + "     </ul>";

				$("#RejectBoxList").append(html);
				
			});
		}else{
			$("#RejectBoxList").append('<div class="search_empty_wrap"><span class="ico_search_empty"></span><span class="search_empty_txt"><spring:message code="Cache.msg_NoDataList"/></span></div>');	//조회할 목록이 없습니다.
		}
	}
	
	//완료함
	function setCompleteBoxListData(data){
		$("#CompleteBoxList").empty();
		if(data.length > 0){
			$(data).each(function(index){
				var html = "";
				html = "<ul class='returnUl'>"
			         + "	<li>"
			         + "		<dl>"
			         + "			<dt><span style='cursor:pointer' class='msTit' onclick='onClickPopButton(\""+this.ProcessArchiveID+"\",\""+this.WorkitemArchiveID+"\",\""+this.PerformerID+"\",\""+this.ProcessDescriptionArchiveID+"\",\""+this.FormSubKind+"\",\"\",\"\",\""+this.FormID+"\",\"\",\""+this.UserCode+"\",\"COMPLETE\",\""+this.FormPrefix+"\",\""+this.BusinessData1+"\",\""+this.BusinessData2+"\",\""+this.TaskID+"\"); return false;'>"+this.FormSubject+"</span>";
			         if(this.IsFile=="Y"){
			        	 html += "<div class='fClip'><a onclick='onClickmClip(this,\""+this.ProcessID+"\",\""+this.FormInstID+"\");' class='mClip'><spring:message code='Cache.lbl_attach'/></a></div>"; //첨부
			         }
			    html += "            </dt>"
			         + "				<dd class='mPro'><a class='mainPro' style='cursor:default;'><img style=\"max-width: 100%; height: auto;\" src="+coviCmn.loadImage(this.PhotoPath)+" alt=\"\"  onerror='coviCmn.imgError(this, true) "+"'></a></dd>"
			         + "			    <dd class='sCont'>"
			         + "			        <spring:message code='Cache.lbl_apv_writer'/>: "+setUserFlowerName(this.InitiatorID, this.InitiatorName)+"<br>" //기안자
			         + "			        <spring:message code='Cache.lbl_EndDate'/>: "+getStringDateToString("yyyy.MM.dd HH:mm:ss",this.EndDate) //종료일
			         + "			    </dd>"
			         + "		</dl>"
			         + "	</li>"
			         + "</ul>";

				$("#CompleteBoxList").append(html);
			});
		}else{
			$("#CompleteBoxList").append('<div class="search_empty_wrap"><span class="ico_search_empty"></span><span class="search_empty_txt"><spring:message code="Cache.msg_NoDataList"/></span></div>');	//조회할 목록이 없습니다.
		}
	}
	
	function setSignImage(data) {
		$("#SIGNIMAGETYPE").val($(data)[0].FilePath.split("/")[$(data)[0].FilePath.split("/").length-1]);
	}

	//최근기안팝업
	function onClickPopButton(ProcessID,WorkItemID,PerformerID,ProcessDescriptionID,SubKind,FormTempInstBoxID,FormInstID,FormID,FormInstTableName,UserCode,mnid,FormPrefix,BusinessData1,BusinessData2,TaskID){
		var archived = "false";
		var mode;
		var gloct;
		var subkind;
		var archived;
		var userID;
		switch (mnid){
			case "PREAPPROVAL" : mode="PREAPPROVAL"; gloct = "PREAPPROVAL"; subkind="T010"; userID=UserCode; break;
			case "APPROVAL" : mode="APPROVAL"; gloct = "APPROVAL"; subkind=SubKind; userID=UserCode; break;
			case "PROCESS" : mode="PROCESS"; gloct = "PROCESS"; subkind=SubKind; userID=UserCode; break;
			case "COMPLETE" : mode="COMPLETE"; gloct = "COMPLETE"; subkind=SubKind; archived="true"; userID=UserCode; break;
			case "REJECT" : mode="REJECT"; gloct = "REJECT";  subkind=SubKind; archived="true"; userID=UserCode; break;
			case "TEMPSAVE" : mode="TEMPSAVE"; gloct = "TEMPSAVE"; subkind="";  userID=""; break;
			case "COMPLETE" : mode="COMPLETE"; gloct = "TCINFO"; subkind=SubKind; userID=""; break;
		}
		var width = "790";
		if(IsWideOpenFormCheck(FormPrefix, FormID)){
			width = "1070";
		}else{
			width = "790";
		}
		
		CFN_OpenWindow("/approval/approval_Form.do?mode="+mode+"&processID="+ProcessID+"&workitemID="+WorkItemID+"&performerID="+PerformerID+"&processdescriptionID="+ProcessDescriptionID+"&userCode="+userID+"&gloct="+gloct+"&formID="+FormID+"&forminstanceID="+FormInstID+"&formtempID="+FormTempInstBoxID+"&forminstancetablename="+FormInstTableName+"&admintype=&archived="+archived+"&usisdocmanager=true&listpreview=N&subkind="+subkind
				+"&ExpAppID="+(typeof BusinessData2!="undefined"?BusinessData2:"")+"&taskID="+(typeof TaskID!="undefined"?TaskID:""), "", width, (window.screen.height - 100), "resize");
	}


	//파일
	var attachFileInfoObj;
	function onClickmClip(pObj,pProcessID, pFormInstID){
		if(!axf.isEmpty($(pObj).parent().find('.fileList').html())){
			$(pObj).parent().find('.fileList').remove();
			return false;
		}
		$('.fileList').remove();
		var Params = {
				ProcessID : pProcessID,
				FormInstID : pFormInstID
		};
		$.ajax({
			url:"/approval/getCommFileListData.do",
			type:"post",
			data:Params,
			success:function (data) {
				if(data.list.length=="0"){
					return false;
				}
				var vHtml = "<ul class='fileList'>";
					vHtml += "<li class='boxPoint'></li>";
				for(var i=0; i<data.list.length;i++){
					attachFileInfoObj = data.list;
					vHtml += "<li><a onclick='attachFileDownLoadCall("+i+")'>"+data.list[i].FileName+"</a></li>";
				}
				vHtml += "</ul>";
				$(pObj).parent().append(vHtml);
			},
			error:function(response, status, error){
				CFN_ErrorAjax("/approval/getCommFileListData.do", response, status, error);
			}
		});
	}
	
	function attachFileDownLoadCall(index){
    	var fileInfoObj = attachFileInfoObj[index];
    	Common.fileDownLoad($$(fileInfoObj).attr("FileID"), $$(fileInfoObj).attr("FileName"), $$(fileInfoObj).attr("FileToken"));
	}
	
	initHome();
</script>