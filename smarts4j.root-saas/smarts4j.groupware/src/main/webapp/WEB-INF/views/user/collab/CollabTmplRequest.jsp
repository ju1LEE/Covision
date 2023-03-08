<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"  import="egovframework.baseframework.util.SessionHelper,egovframework.baseframework.util.PropertiesUtil"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>
<div class="cRConTop titType">
	<h2 class="title"><spring:message code='Cache.lbl_RequestMng'/></h2>	
	
</div>
<div class="cRContCollabo mScrollVH" id="collabTmplRequest">
	<!-- 컨텐츠 시작 -->
	<div class="temp_cont">
		<div class="pagingType02 buttonStyleBoxLeft">
			<a href="#" class="btnTypeDefault  btnTypeBg" id="btnApproval"><spring:message code="Cache.lbl_apv_Approved"/></a>
			<a href="#" class="btnTypeDefault" id="btnReject"><spring:message code="Cache.lbl_Reject"/></a>
		</div>
		<span class="TopCont_option">
			<input id="ReqStatus" value="Y" type="checkbox" checked class="check_class">
			<label for=ReqStatus><spring:message code="Cache.lbl_adstandby"/></label><!-- 승인대기건만-->

			<button href="#" class="btnRefresh" type="button" id="btnRefresh"></button>
			<select class="selectType02 listCount" id="selectSize">
				<option>5</option>
				<option selected>10</option>
				<option>20</option>
				<option>30</option>
			</select>
		</span>
					<!-- 승인 의견 레이어 팝업 시작-->
			<div class="layer_divpop" style="width:440px; left:170px; z-index:104;display:none"  id="divWork">
				<div class="divpop_contents">
					<div class="pop_header"><h4 class="divpop_header"><span class="divpop_header_ico"><spring:message code="Cache.lbl_apv_comment_write"/></span></h4><a onclick='$("#divWork").hide()'  class="divpop_close" style="cursor:pointer;"></a></div>
					<div class="divpop_body" style="overflow:hidden; padding:0;">
						<div class="ATMgt_popup_wrap">
							<div class="ATMgt_opinion_wrap">
								<table class="ATMgt_popup_table" cellpadding="0" cellspacing="0">
									<tbody>
										<tr>	
											<td class="ATMgt_T_th"><spring:message code="Cache.lbl_comment"/></td>
											<td><textarea id="ApprovalRemark" name="ApprovalRemark" class="ATMgt_Tarea"  cols="30" rows="40"></textarea>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="bottom">
								<a href="#" class="btnTypeDefault  btnTypeBg btnAttAdd" id="btnSaveApproval"><spring:message code="Cache.lbl_apv_Approved"/></a>
								<a href="#" class="btnTypeDefault" id="btnSaveReject"><spring:message code="Cache.lbl_Reject"/></a>
								<a href="#" class="btnTypeDefault" onclick='$("#divWork").hide()'><spring:message code="Cache.btn_Close"/></a>
							</div>
						</div>
					</div>
				</div>
			</div>	
		<div class="tblList">
			<div id="collabTmplRequestGridDiv"></div>
		</div>
	</div>	
	<!-- 컨텐츠 끝 -->
</div>
<script>
var collabTmplRequest = {
		grid:'',
		objectInit : function(){		
			this.makeGrid();
			this.addEvent();
			this.searchData(1);
		}	,
		makeGrid :function(){
			var configObj = {	targetID : "collabTmplRequestGridDiv",height : "auto",
					page : {pageNo: 1,pageSize: 10,},
					paging : true};
			var headerData =  [ 
				       {key:'chk', label:'chk', width:'30', align:'center', formatter:'checkbox', disabled: function (){
				    	    return this.item.RequestStatus!=='ApprovalRequest';
				       }},
						{key:'TmplKindName',			label:"<spring:message code='Cache.lbl_SchDivision' />",			width:'90', align:'center'},		//요청종류
						{key:'URName',  label:'<spring:message code="Cache.lbl_RequestUser" />', width:'90', align:'center'}, 	//요청자
						{key:'RequestTitle',  label:'<spring:message code="Cache.lbl_Title" />', width:'150', align:'left',
							formatter : function () {
				           		 return "<a id='reqInfo' data-map='" + JSON.stringify(this.item) + "'  class='gridLink'>"+this.item.RequestTitle+"</a>";
							}
						},
						{key:'RequestRemark',  label:'<spring:message code="Cache.lbl_sms_send_contents" />', width:'250', align:'left',
							formatter : function () {
				           		 return this.item.RequestRemark;
							}
						},
						{key:'StatusName',  label:'<spring:message code="Cache.lbl_VendorStatus" />', width:'70', align:'center',
							formatter : function () {
								var className = "";
								switch (this.item.RequestStatus)
								{
									case "ApprovalRequest": //승인요청
										className ="stay";
										break;
									case "Approval": //완료
										className ="comp";
										break;
									case "Reject"://반려
										className ="stay";
										break;
									case "ApprovalCancel"://신청철회
										className ="stay";
										break;
								}
				           		 return "<p class='tx_status "+className+"'>"+this.item.StatusName+"</p>";
							}
						},
						{key:'RegisteDate',  label:'<spring:message code="Cache.lbl_Application_Day" />', width:'120', align:'center', formatter : function () { return AttendUtils.maskDateTime(CFN_TransLocalTime(this.item.RegisteDate))}},
						{key:'RequestStatus',  label:'<spring:message code="Cache.TodoMsgType_Approval" />', width:'150', align:'center', sort:false,
							formatter : function () {
								if (this.item.RequestStatus== "ApprovalRequest"){ //승인요청
									return "<a class='btn_Ok' id='btnOk'><spring:message code='Cache.lbl_apv_Approved'/></a><a class='btn_No' id='btnNo'><spring:message code='Cache.lbl_Reject'/></a>";
								}	
								else{
									return "<a class='btn_Approval' href='#'>"+this.item.StatusName+"</a>";
								}
							}
						},
						{key:'',  label:"<spring:message code='Cache.lbl_Move'/>",width:'80', display:true, sort:false, formatter:function () {
		 		  			return String.format("<a href='#' title='{0}' class='btnTypeDefault btnSearchBlue01' id='btnMove'>{1}</a>", this.item.PrjName, "<spring:message code='Cache.lbl_Move'/>");
	   		        		}
						}

				];
			collabTmplRequest.grid = new coviGrid();
			collabTmplRequest.grid.setGridHeader(headerData);
			collabTmplRequest.grid.setGridConfig(configObj);
		},
		addEvent : function(){
			$('#collabTmplRequest #selectSize' ).on( 'change', function(e){
				collabTmplRequest.searchData(1);
			});
			
			$('#collabTmplRequest #btnSearch, #collabTmplRequest .btnRefresh, #collabTmplRequest .check_class' ).on( 'click', function(e){
				collabTmplRequest.searchData(1);
			});

			//승인화면
			$(document).on("click","#btnOk",function(){
				gMode="DIV";
				$("#ApprovalRemark").val("");
				$("#divWork").show();
				$("#btnSaveApproval").show();
				$("#btnSaveReject").hide();
			});
			
			$("#btnApproval").click(function(){
				if(!collabTmplRequest.validationChk())     	return ;
				gMode = "";
				$("#ApprovalRemark").val("");
				$("#divWork").show();
				$("#btnSaveApproval").show();
				$("#btnSaveReject").hide();
			});	

			//거부
			$(document).on("click","#btnNo",function(){
				gMode="DIV";
				$("#ApprovalRemark").val("");
				$("#divWork").show();
				$("#btnSaveApproval").hide();
				$("#btnSaveReject").show();
				
			});
			
			$("#btnReject").click(function(){
				if(!collabTmplRequest.validationChk())     	return ;
				gMode = "";
				$("#ApprovalRemark").val("");
				$("#divWork").show();
				$("#btnSaveApproval").hide();
				$("#btnSaveReject").show();
			});	
			

			$("#btnSaveApproval").click(function(){
				if(gMode == "" && !collabTmplRequest.validationChk())     	return ;
				Common.Confirm("<spring:message code='Cache.msg_TFAsk_apv' />", "Confirmation Dialog", function (confirmResult) {
					if (confirmResult) {
						var aJsonArray = new Array();
						if (gMode == ""){
							var selectObj = collabTmplRequest.grid.getCheckedList(0);
							for(var i=0; i<selectObj.length; i++){
		                        aJsonArray.push({"requestSeq":selectObj[i].RequestSeq,
												"prjSeq":selectObj[i].PrjSeq,
												"tmplName":selectObj[i].RequestTitle});
							}
						}
						else{
							aJsonArray.push({"requestSeq":collabTmplRequest.grid.getSelectedItem()["item"]["RequestSeq"],
											  "prjSeq":collabTmplRequest.grid.getSelectedItem()["item"]["PrjSeq"]});
						}
						
						$.ajax({
							type:"POST",
							contentType:'application/json; charset=utf-8',
							dataType   : 'json',
							data:JSON.stringify({"dataList" : aJsonArray  , "approvalRemark":$("#ApprovalRemark").val()}),
							url:"/groupware/collabTmpl/approvalTmplRequest.do",
							success:function (data) {
								if(data.status=='SUCCESS'){
									Common.Inform("["+data.resultCnt+"<spring:message code='Cache.lbl_DocCount'/>] <spring:message code='Cache.msg_com_processSuccess'/>");	//저장되었습니다.
									collabTmplRequest.searchData(1);
									$("#divWork").hide();
								}else{
				            		Common.Warning("<spring:message code='Cache.msg_apv_030'/>");
									$("#divWork").hide();
					            }	
							},
							error:function (request,status,error){
								Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />"+"code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error)
							}
						});
					}	
				});				
			});
			
			//거부
			$("#btnSaveReject").click(function(){
				if(gMode == "" && !collabTmplRequest.validationChk())     	return ;
				Common.Confirm("<spring:message code='Cache.msg_RUReject' />", "Confirmation Dialog", function (confirmResult) {
					if (confirmResult) {
						var aJsonArray = new Array();
						if (gMode == ""){
							var selectObj = collabTmplRequest.grid.getCheckedList(0);
							for(var i=0; i<selectObj.length; i++){
		                        aJsonArray.push({"requestSeq":selectObj[i].RequestSeq,
												"prjSeq":selectObj.PrjSeq});
							}
						}
						else{
							aJsonArray.push({"requestSeq":collabTmplRequest.grid.getSelectedItem()["item"]["RequestSeq"],
											"prjSeq":collabTmplRequest.grid.getSelectedItem()["item"]["PrjSeq"]});
						}

						
						$.ajax({
							type:"POST",
							contentType:'application/json; charset=utf-8',
							dataType   : 'json',
							data:JSON.stringify({"dataList" : aJsonArray  , "approvalRemark":$("#ApprovalRemark").val()}),
							url:"/groupware/collabTmpl/rejectTmplRequest.do",
							success:function (data) {
								if(data.result == "ok"){
									Common.Inform("["+data.resultCnt+"<spring:message code='Cache.lbl_DocCount'/>] <spring:message code='Cache.msg_com_processSuccess'/>");	//저장되었습니다.
									collabTmplRequest.searchData(1);
									$("#divWork").hide();
								}
								else{
									Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />"+data); //	오류가 발생했습니다. 관리자에게 문의바랍니다
								}
							},
							error:function (request,status,error){
								Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />"+"code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error)
							}
						});
					}	
				});				
			});	
			
			//페이지 이동
			$(document).on("click","#btnMove",function(){
				var obj = collabTmplRequest;
				var gridData = obj.grid.getSelectedItem();
				var item = gridData["item"];
				if (item.IsManager == "Y" || 		item.IsMember == "Y" 	|| item.IsRegister == "Y"){//프로젝트 멤버인 경우 탭으로 이동
					$('.collaboMenu #list_P_'+item.PrjSeq).trigger("click");
				}
				else{
					//				collabMenu.goTab(this, objId, prjName, "MAIN", "/groupware/collab/CollabMain.do?param="+encodeURI($(this).text()),dataMap);
					var popupID	= "CollabProjectAllocPopup";
			 		var openerID	= "";
			 		var popupYN		= "N";
			 		var popupUrl	= "/groupware/collab/CollabMainView.do?"
			 						+ "&prjSeq="+item.PrjSeq
			 						+ "&prjName="+encodeURI(item.RequestTitle)
			 						+ "&popupID="	 + popupID;
					Common.open("", popupID, "", popupUrl, "1600", "800", "iframe", true, null, null, true);
				}
			});				
			
		},
		validationChk:function(){
			var listobj = collabTmplRequest.grid.getCheckedList(0);
			var aJsonArray = new Array();
			if(listobj.length == 0){
				Common.Warning("<spring:message code='Cache.msg_SelectTarget'/>");
				return false;
			}
			return true;
		},
		searchData:function(pageNo){
			var params = { "reqStatus":$('#collabTmplRequest #ReqStatus' ).is(":checked")?"Y":""};
			if (pageNo !="-1"){
				collabTmplRequest.grid.page.pageNo =pageNo;
				this.grid.page.pageSize = $('#collabTmplRequest #selectSize').val();
			}	
			// bind
			collabTmplRequest.grid.bindGrid({
				ajaxPars : params,
				ajaxUrl:"/groupware/collabTmpl/getTmplRequestList.do"
			});

		}
}

$(document).ready(function(){
	collabTmplRequest.objectInit();
});

</script>					