<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="egovframework.baseframework.util.RedisDataUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
	<script type="text/javascript" src="/covicore/resources/ExControls/Chart.js-master/Chart.js<%=resourceVersion%>"></script>
	<style>
	#monthTooltip{z-index:9999}
	.AllMR_list li a img {width:120px; height:160px; border:1px solid #e0e0e0;}
	.AllMR_img img{width:}
	</style>  
	<div class="eAccountingPortalWrap" id = "fixedTabAccViewArea" style="overflow-x:hidden; height: 100%;">
		<div class="eAPListBoxWrap">
			<!-- 상단 배너 시작 -->
			<div class="eAPBanner_1_new_n">
				<fmt:parseDate value="${fn:replace( deadline.DeadlineFinishDate,'.','')}" var="dlDate" pattern="yyyyMMdd"/>
				<fmt:formatDate value="${dlDate}" pattern="yyyy" var="yyyy"/>					
				<fmt:formatDate value="${dlDate}" pattern="MM" 	 var="mm"/>
				<fmt:formatDate value="${dlDate}" pattern="dd" 	 var="dd"/>
				<fmt:formatDate value="${dlDate}" pattern="E" 	 var="e"/>
				<div class="eAPBannerUpText_new" <c:if test="${deadline.IsUse == 'N'}">style="display: none;"</c:if>>
					<strong>${deadline.StandardMonth == "01" ? 12 : mm-1}월 경비 마감<span class="tx_blue"> ${mm}월 ${dd}일(${e})</span>까지</strong>
					<span class="noti">${deadline.NoticeText}</span>
				</div>
			</div>
			<!-- 상단 배너 끝 -->	
		</div>
		<div class="squaregraph01_wrap">
			<span class="squaregragh_Title" style="margin-left:5px;">${urName} <font id=portalTitle>${fn:substring(payDate,0,4)}년&nbsp;${fn:substring(payDate,4,6)}</font>월 리포트</span>
			<div class="MyBtnType01 newbtn01" id="userCal"><a class="pre"></a><a class="next"></a></div>
		</div>
		<!-- 1단 그래프 시작 -->
		<div class="Newgragh_eAPBoard">
			<div class="Newgragh_eAPBoardL">
				<h3 class="Newgragh_eAPBoardTitle">증빙종류별  사용금액</h3>
				<div class="Newgragh_eAPBoardContents" >
					<div class="Newgragh_eAPBoardCardBox">
						<canvas id="userProofDoughnut"></canvas>
					</div>
					<div class="Newgragh_Detailrank">
						<h2>총 <span class="Newgragh_TxtBox"  id="account_portal_user_proof_tot"><fmt:formatNumber value="${proofCount.TotalAmount}" pattern="#,###" /></span> 원</h2>
						<ul id="account_portal_user_proof_list">
							<c:if test="${proofCount.TotalAmount>0 }">
								<fmt:parseNumber var="per" value="${proofCount.TaxBillAmount*100.00/proofCount.TotalAmount}" pattern=".00"/>
							</c:if>	
							<li>
								<div class="DetailRank10 height"><div class="Gragh_color blue1"></div>전자세금계산서(${proofCount.TaxBillCnt}건)</div>
								<div class="Detailaccount10"><fmt:formatNumber value="${proofCount.TaxBillAmount}" pattern="#,###" />원</div>
							</li>
							<c:if test="${proofCount.TotalAmount>0 }">
								<fmt:parseNumber var="per" value="${proofCount.ReceiptAmount*100.00/proofCount.TotalAmount}" pattern=".00"/>
							</c:if>	
							<li>
								<div class="DetailRank10 height"><div class="Gragh_color blue2"></div>모바일영수증(${proofCount.ReceiptCnt}건)</div>
								<div class="Detailaccount10"><fmt:formatNumber value="${proofCount.ReceiptAmount}" pattern="#,###" />원</div>
							</li>
							<c:if test="${proofCount.TotalAmount>0 }">
								<fmt:parseNumber var="per" value="${proofCount.CorpCardAmount*100.00/proofCount.TotalAmount}" pattern=".00"/>
							</c:if>	
							<li>
								<div class="DetailRank10 height"><div class="Gragh_color blue3"></div>법인카드(${proofCount.CorpCardCnt})건</div>
								<div class="Detailaccount10"><fmt:formatNumber value="${proofCount.CorpCardAmount}" pattern="#,###" />원</div>
							</li>
						</ul>
					</div>	
				</div>
			</div>
			<!-- 신청내역 그래프 시작 -->
			<div class="Newgragh_eAPBoardR">
				<h3 class="Newgragh_eAPBoardTitle2">신청내역</h3>
				<c:choose>
				<c:when test="${fn:length(accountCount)== 0}">
				<div class="Newgragh_Detailrank2">
					<h2>총 <span class="Newgragh_TxtBox"  id="account_portal_user_account_tot">0</span> 원</h2>
					<ul class="Newgraph_stick" id="account_portal_user_account_chart">
						<p class="OWList_none">조회할 목록이 없습니다.</p>
					</ul>						
				</div>								
				</c:when>
				<c:otherwise>
				<div class="Newgragh_Detailrank2">
 					<h2>총 <span class="Newgragh_TxtBox" id="account_portal_user_account_tot"><fmt:formatNumber value="${accountCount[0].Amount}" pattern="#,###.##" /></span> 원</h2>
					<ul class="Newgraph_stick" id="account_portal_user_account_chart">
						<c:forEach items="${accountCount}" var="list" varStatus="status">
							 <c:if test="${list.Code ne 'Total'}">
								<c:set var="per" value="${list.Amount*100.00/accountCount[0].Amount}"/>
								<c:if test="${status.count>5 && status.count%5 == 2}">
									</ul><ul class="Newgraph_stick" style="display:none">
								</c:if>
								<li>
									<div class="conR">
										<div class="Detailtxt20">${list.Name}(${list.Cnt})</div>
										<div class="zt-skill-bar"><div class="skillbar0${status.index%5+1}" data-width="${per}" style="width: ${per}%;"></div>
										</div>
									</div>
									<div class="Detailnumber20"><fmt:formatNumber value="${list.Amount}" pattern="#,###.##" />원</div>
								</li>
							</c:if>
						</c:forEach>
					</ul>
				</div>
				</c:otherwise>
				</c:choose>
				<!--슬라이드 버튼 -->
				<div class="Slideroll_btn" id="account_portal_user_account_slide">
				<c:if test="${fn:length(accountCount) > 6}">
					<c:forEach begin="1" end="${fn:length(accountCount)/5}"  var="x">
						<c:choose>
							<c:when test="${x== 1}">
								<a class="Slideroll_btn_on" href="#"><span></span></a>
							</c:when>
							<c:otherwise>
								<a class="Slideroll_btn_off" href="#"><span></span></a>
							</c:otherwise>	
						</c:choose>
					</c:forEach>
				</c:if>
				</div>	
				<!--슬라이드 버튼 끝 -->
			</div>
			<!-- 신청내역 그래프 끝 -->		
		</div>
		<!-- 1단 그래프 끝 -->	
		<div class="squaregraph01_wrap" id="monthChartTit">						
			<span class="squaregragh_Title" style="margin-left:5px;">${urName} <span>${fn:substring(payDate,0,4)}</span>년 월별 신청내역</span>
			<div class="MyBtnType01 newbtn01" id="yearCal"><a href="#" class="pre"></a><a href="#" class="next"></a></div>	
		</div>
		<!-- 월별 그래프 시작 -->
		<canvas id="userMonthChart" width="400" height="100"></canvas>
		<!-- 월별그래프 끝 -->
	<!-- 하단 게시판영역 -->
	<article class="mainBoradTabView eaccportal">
		<div class="mainBoardTabCont clearFloat active">				
			<div class="mBoardNoticeCont01 half">
				<div class="mBoardTabCont">
					<div>
						<div class="mNotiTitle">
							<ul class="tabBoard_tabList mSubTabMenu clearFloat">
								<li class="active">
									<a href="#" class="tabTitle"><span>승인대기(<span id="ApprovalCnt"></span>)</span></a>
									<a  data-type="AA" class="btnMainMore">more +</a>
								</li>
								<li>
									<a href="#" class="tabTitle"><span>진행 (<span id="ProcessCnt"></span>)</span></a>
									<a data-type="AP" class="btnMainMore">more +</a>
								</li>
							</ul>
						</div>
						<div class="mBaordTabView active EarWaitList">
							<div  class="listNone"><p class="OWList_none" >조회할 목록이 없습니다.</p></div>
							<ul class="Earwait_time">
							</ul>
						</div>
						<div class="mBaordTabView EarWaitList02">
							<div  class="listNone"><p class="OWList_none">조회할 목록이 없습니다.</p></div>
							<ul class="Earwait_time">
							</ul>
						</div>
					</div>
				</div>	
			</div>
			<div class="mBoardNoticeCont02 half">
				<div class="mBoardTabCont02">
					<div>
						<div class="mNotiTitle">
							<strong class="mNoTit">사용내역</strong>	
							<ul class="mSubTabMenu02 clearFloat">
								<li class="active">
									<a href="#" class="tabTitle"><span>법인카드(${corpCardListCnt})</span></a>
									<a data-type="C" class="btnMainMore">more +</a>
								</li>
								<li class="">
									<a href="#" class="tabTitle"><span>세금계산서(${taxBillListCnt})</span></a>
									<a data-type="T" class="btnMainMore">more +</a>
								</li>
								<li class="">
									<a href="#" class="tabTitle"><span>영수증(${billListCnt})</span></a>
									<a data-type="R" class="btnMainMore">more +</a>
								</li>												
							</ul>
						</div>																						
					</div>
				</div>	
				<div class="mBaordTabView02 AllCadegory_searchlist active">
					<ul class="AllCadegory__time">
					<c:choose>
						<c:when test="${fn:length(corpCardList)== 0}">
							<li class="listNone" id="itemContainer"><p class="OWList_none">조회할 목록이 없습니다.</p></li>
						</c:when>
						<c:otherwise>
							<c:forEach items="${corpCardList}" var="list" varStatus="status">
								<li class="clearFloat">
									<a href="#" class="trans_exeInfoTxt Proceed corpCard" data="${list.ReceiptID}">															
										<p class="trans_exeTit unread">${list.StoreName}</p>
										<p class="exepart">
											<span class="fcStyle1">${list.AmountWon}</span>
											<span class="trans fcStyle"><span><span class="fcStyle">${list.ApproveDate} ${list.ApproveTime}</span>												
										</p>											
									</a>
									<c:if test="${list.Code eq null }">
										<span class="FontColorSet"><a href="#" class="regist_corpcard" data="${list.ReceiptID}"><spring:message code='Cache.lbl_Unregistered'/></a></span>										
									</c:if>		
									<c:if test="${list.Code ne null }">
										<span class="FontColorSet"><a href="#" style="color: #333;" class="regist_corpcard" data="${list.ReceiptID}">${list.Name}</a></span>										
									</c:if>		
								</li>
							</c:forEach>
						</c:otherwise>
					</c:choose>
					</ul>
				</div>	
				<div class="mBaordTabView02 AllCadegory_searchlist">
					<ul class="AllCadegory__time">
						<c:choose>
							<c:when test="${fn:length(taxBillList)== 0}">
								<li class="listNone" id="itemContainer"><p class="OWList_none">조회할 목록이 없습니다.</p></li>
							</c:when>
							<c:otherwise>
						
							<c:forEach items="${taxBillList}" var="list" varStatus="status">
								<li class="clearFloat">
									<a href="#" class="trans_exeInfoTxt Proceed taxInvoice" data="${list.TaxInvoiceID}">															
										<p class="trans_exeTit unread">${list.InvoicerCorpName}</p>
										<p class="exepart">
											<span class="fcStyle1">${list.TotalAmount}</span>
											<span class="trans fcStyle"><span><span class="fcStyle">${list.WriteDate}</span>												
										</p>											
									</a>	
								</li>
							</c:forEach>
						</c:otherwise>
					</c:choose>
					</ul>
				</div>		
				<div class="mBaordTabView02 AllMR_list_wrap">
					<ul class="AllMR_list">
					<c:choose>
						<c:when test="${fn:length(billList)== 0}">
							<li class="listNone" id="itemContainer"><p class="OWList_none">조회할 목록이 없습니다.</p></li>
						</c:when>
						<c:otherwise>
							<c:forEach items="${billList}" var="list" varStatus="status">
								<li class="clearFloat">
									<a href="#" class="AllMR_img mobileReceipt" data="${list.ReceiptFileID}">
										<img src="/covicore/common/photo/photo.do?img=${list.URLPath}"  alt="" onerror="coviCmn.imgError(this, false);"/><span class="AllMR_img_date">${list.RegistDate}</span>
									</a>
									<c:if test="${list.Code eq null }">
										<span class="AllMR_tit link"><a href="#" class="regist_receipt" data="${list.ReceiptID}"><spring:message code='Cache.lbl_Unregistered'/></a></span>										
									</c:if>		
									<c:if test="${list.Code ne null }">
										<span class="AllMR_tit"><a href="#" class="regist_receipt" data="${list.ReceiptID}">${list.Name}</a></span>										
									</c:if>		
								</li>
							</c:forEach>
						</c:otherwise>
					</c:choose>
					</ul>
				</div>
			</div>	
			<!-- 청구 가이드 -->
			<c:if test="${fn:length(guideList)> 0}">
			<div class="cost_bottom">
				<li>
					<span class="cost_txt01" >경비 청구 가이드</span>
					<div class="cost_arrow Rig"></div>
				</li>
				<c:forEach items="${guideList}" var="list" varStatus="status">
					<li>
						<a href="#"  onclick="board.goViewPopup('Board', '${list.Reserved3}', 1, '${list.Reserved1}', '${list.Reserved2}','W');">
							<div class="cost_bottom_icon1"></div>
							<span class="cost_txt02"><strong>${list.CodeName}</strong> 청구</span>
						</a>
						<div class="cost_bar"></div>
					</li>
				</c:forEach>
			</div>	
			</c:if>					
		</div>
	</article>
</div>		
<script>
var ProfileImagePath = Common.getBaseConfig('ProfileImagePath');
var userPortal = {	
	pageInit : function(){		
		this.getApprovalList(0);	
		userComm.getData("/account/accountPortal/getTopCategory.do",{})
		var data = [${proofCount.TaxBillAmount},${proofCount.ReceiptAmount},${proofCount.CorpCardAmount}];
		if (${proofCount.TotalAmount} == 0){
			data=[0,0,0,100];
		}
		userMonthChartObj.init( "${payDate}" );		
		userAccountChartObj.initChart(data);
		//증빙종류내역 슬라이딩
		$('#fixedTabAccViewArea .Newgragh_eAPBoardL .Slideroll_btn a').click(function(){
			$('#fixedTabAccViewArea .Newgragh_eAPBoardL .Slideroll_btn a').removeClass('Slideroll_btn_on').addClass('Slideroll_btn_off');
			$('#fixedTabAccViewArea .Newgragh_eAPBoardL .Newgragh_Detailrank ul').hide();
			
			$(this).removeClass('Slideroll_btn_off').addClass('Slideroll_btn_on');
			$('#fixedTabAccViewArea .Newgragh_eAPBoardL .Newgragh_Detailrank ul').eq($(this).index()).show();	
				
		});
	
		//신청내역 슬라이딩
		$('#fixedTabAccViewArea .Newgragh_eAPBoardR .Slideroll_btn a').click(function(){
			$('#fixedTabAccViewArea .Newgragh_eAPBoardR .Slideroll_btn a').removeClass('Slideroll_btn_on').addClass('Slideroll_btn_off');
			$('#fixedTabAccViewArea .Newgragh_eAPBoardR .Newgragh_Detailrank2 ul').hide();
			
			$(this).removeClass('Slideroll_btn_off').addClass('Slideroll_btn_on');
			$('#fixedTabAccViewArea .Newgragh_eAPBoardR .Newgragh_Detailrank2 ul').eq($(this).index()).show();	
				
		});
		//승인대기/진행중
		$('#fixedTabAccViewArea .mBoardNoticeCont01 .mNotiTitle li').click(function(){
			$('#fixedTabAccViewArea .mBoardNoticeCont01 .mNotiTitle li').removeClass('active');
			$('#fixedTabAccViewArea .mBoardNoticeCont01 .mBaordTabView').removeClass('active');
			
			$(this).addClass('active');
			$('#fixedTabAccViewArea .mBoardNoticeCont01 .mBaordTabView').eq($(this).index()).addClass('active');	
			
			userPortal.getApprovalList($(this).index());
		});		
		
		//증빙별
		$('#fixedTabAccViewArea .mBoardNoticeCont02 .mNotiTitle li').click(function(){
			$('#fixedTabAccViewArea .mBoardNoticeCont02 .mNotiTitle li').removeClass('active');
			$('#fixedTabAccViewArea .mBoardNoticeCont02 .mBaordTabView02').removeClass('active');
			
			$(this).addClass('active');
			$('#fixedTabAccViewArea .mBoardNoticeCont02 .mBaordTabView02').eq($(this).index()).addClass('active');	
		});		
		
		//법인카드
		$('#fixedTabAccViewArea .corpCard').click(function(){
			userPortal.cardReceiptPopup($(this).attr("data"));
		});
		//세금계산서
		$('#fixedTabAccViewArea .taxInvoice').click(function(){
			userPortal.taxInvoicePop($(this).attr("data"));
		});
		//영수증팝업
		$('#fixedTabAccViewArea .mobileReceipt').click(function(){
			userPortal.mobileReceiptPopup($(this).attr("data"));
		});
		//법인카드 - 계정과목(표준적요)/적요
		$('#fixedTabAccViewArea .regist_corpcard').click(function(){
			userPortal.usageTextWritePopup($(this).attr("data"), "CorpCard");
		});
		//영수증 - 계정과목(표준적요)/적요
		$('#fixedTabAccViewArea .regist_receipt').click(function(){
			userPortal.usageTextWritePopup($(this).attr("data"), "Receipt");
		});
			
		$('#fixedTabAccViewArea .MyNewColorChart_lineBar').mouseover(function(){
			$("#fixedTabAccViewArea .eac_user_month_txt").removeClass('active')
			if ($(this).attr("data") == "") return;
			$(this).parents("li").find(".eac_user_month_txt").addClass('active')
		});
		
		$("#fixedTabAccViewArea .mBoardNoticeCont01 .tabBoard_tabList #ApprovalCnt").text($("li.eaccountingMenu02").find("a[id$=Approval]").find("span.fCol19abd8").text().trim());
		$("#fixedTabAccViewArea .mBoardNoticeCont01 .tabBoard_tabList #ProcessCnt").text($("li.eaccountingMenu02").find("a[id$=Process]").find("span.fCol19abd8").text().trim());
		
		//다음월버튼
		$('#fixedTabAccViewArea #userCal .next').click(function(){
			userAccountChartObj.getUserMonth("+");
		});
		//다음월버튼
		$('#fixedTabAccViewArea #userCal .pre').click(function(){
			userAccountChartObj.getUserMonth("-");
		});		
		
		//다음해버튼
		$('#fixedTabAccViewArea #yearCal .next').click(function(){
			userMonthChartObj.nextReport();
		});
		//이전해 버튼
		$('#fixedTabAccViewArea #yearCal .pre').click(function(){
			userMonthChartObj.prevReport();
		});
		
		/* 경비 청구 가이드 */			
		$("#fixedTabAccViewArea #expenceClaimGuide a").on('click',function(){
			var obj = Common.getBaseCode('EXPENSE_CLAIM_GUIDE').CacheData[ $(this).index() ];
			board.goViewPopup('Board', obj.Reserved3, 1, obj.Reserved1, obj.Reserved2,'W');
		});
	
		/*more */
		$("#fixedTabAccViewArea .btnMainMore").on("click", function(){
			var me = $(this);
			switch (me.attr("data-type")){
				case "C":	//법인카드
					eAccountContentHtmlChangeAjax('account_CardReceiptUseraccountuserAccount'
							, "<spring:message code='Cache.ACC_lbl_corpCardUseList' />"
							, '/account/layout/account_CardReceiptUser.do?CLSYS=account&CLMD=user&CLBIZ=Account'
							, {callType:"Portal"});
					break;
				case "A":	//전표조회
					eAccountContentHtmlChangeAjax('account_ExpenceApplicationManageUseraccountuserAccount'
							, "<spring:message code='Cache.ACC_lbl_expenceApplicationView' />"
							, '/account/layout/account_ExpenceApplicationManageUser.do?CLSYS=account&CLMD=user&CLBIZ=Account'
							, {callType:"Portal"});
					break;
				case "T":	//세금계산서
					eAccountContentHtmlChangeAjax('account_TaxInvoiceUseraccountuserAccount'
							, "<spring:message code='Cache.ACC_lbl_taxInvoiceView' />"
							, '/account/layout/account_TaxInvoiceUser.do?CLSYS=account&CLMD=user&CLBIZ=Account'
							, {callType:"Portal"});
					break;
				case "R":	//모바일영수증
					eAccountContentHtmlChangeAjax('account_MobileReceiptUseraccountuserAccount'
							, "<spring:message code='Cache.ACC_lbl_mobileReceipt' />"
							, '/account/layout/account_MobileReceiptUser.do?CLSYS=account&CLMD=user&CLBIZ=Account'
							, {callType:"Portal"});
					break;
				case "AA":	//승인대기
					eAccountContentHtmlChangeAjax('account_ApprovalListaccountuserAccountApproval'
							, "<spring:message code='Cache.ACC_lbl_doc_approve' />"
							, '/account/layout/account_ApprovalList.do?CLSYS=account&CLMD=user&CLBIZ=Account&mode=Approval'
							, {callType:"Portal"});
					break;
				case "AP":	//진행
					eAccountContentHtmlChangeAjax('account_ApprovalListaccountuserAccountProcess'
							, "<spring:message code='Cache.ACC_lbl_doc_process' />"
							, '/account/layout/account_ApprovalList.do?CLSYS=account&CLMD=user&CLBIZ=Account&mode=Process'
							, {callType:"Portal"});
					break;
			}
		});
		
	},
	
	getApprovalList : function(idx){
		var objId ="EarWaitList";
		var mode = "Approval";
		if (idx == 0){
			objId ="EarWaitList";
			mode = "Approval";
		}
		else{
			objId ="EarWaitList02";
			mode = "Process";
		}
		
		
		$.ajax({
			url	: "/approval/user/getApprovalListData.do",
			type: "POST",
			data: {"mode"  :mode,	"searchGroupType":'',	"bstored":"false",	"userID":Common.getSession().UR_Code,	"businessData1":"ACCOUNT",	"pageSize":3,	"pageNo":1},
			success:function (data) {
				if(data.status == "SUCCESS"){
					var listCount = data.page.listCount;
					if (listCount== undefined || listCount ==0) return; 
						
					$("."+objId+" .listNone").hide();
					$("."+objId+" .Earwait_time").html("");
					$.each( data.list, function(index, value) {
						$("."+objId+" .Earwait_time").append('<li class="clearFloat">'+
								'<div class="listShell">'+
								'	<a href="#" class="exePhoto_List">'+
								'		<p><img src="'+coviCmn.loadImage(value.PhotoPath)+'" onerror="coviCmn.imgError(this, true);" class="mCS_img_loaded"></p>'+
								'	</a>'+
								'	<a href="#" class="exeInfoTxt Proceed " onclick=\'userPortal.onClickPopButton("'+value.ProcessID+"\",\""+value.WorkItemID+"\",\""+value.TaskID+"\",\""+value.PerformerID+"\",\""+value.ProcessDescriptionID+"\",\""+value.FormInstID+"\",\""+value.FormSubKind+"\",\""+value.UserCode+"\",\""+value.FormID+"\",\""+value.BusinessData2+'","'+mode+'")\'>	'+
								'		<p class="exeTit unread ApprovalList"   >'+
								'			<span class="exeTitle">'+value.FormSubject+'</span>'+
								'			<span class="exePrice">'+userComm.makeComma(value.BusinessData3)+'원</span>'+
								'		</p>'+
								'		<p class="exepart">'+
								'			<span class="fcStyle1">'+value.InitiatorName+'</span>'+
								'			<span class="fcStyle">'+(mode=="Approval"?value.Created:value.Finished)+'</span>'+
								'		</p>'+
								'	</a>'+
								'</div>'+
							'</li>');
					})
				}
			},
			error:function (error){
			}
		});
	},
	cardReceiptPopup : function(key){
		var popupName	=	"CardReceiptPopup";
		var popupID		=	"cardReceiptPopup";
		var openerID	=	"userPortal";
		var popupTit	=	"<spring:message code='Cache.ACC_lbl_cardReceiptInvoice' />"; //신용카드 매출전표
		var url			=	"/account/accountCommon/accountCommonPopup.do?"
						+	"popupID="		+ popupID	+ "&"
						+	"openerID="		+ openerID	+ "&"
						+	"popupName="	+ popupName	+ "&"
						+	"approveNo="	+ key		+ "&"
						+	"receiptID="	+ key;
		Common.open("",popupID,popupTit,url,"320px", "510px","iframe",true,null,null,true);
	},
	taxInvoicePop : function(key){
		var popupName	=	"TaxInvoicePopup";
		var popupID		=	"taxInvoicePopup";
		var popupTit	=	"<spring:message code='Cache.ACC_lbl_taxInvoiceCash' />";	//전자세금계산서
		var url			=	"/account/accountCommon/accountCommonPopup.do?"
						+	"popupID="		+	popupID		+	"&"
						+	"popupName="	+	popupName	+	"&"
						+	"taxInvoiceID="	+	key;
		Common.open("",popupID,popupTit,url,"980px", "720px","iframe",true,null,null,true);
	},
	mobileReceiptPopup : function(FileID){
		var popupName	=	"FileViewPopup";
		var popupID		=	"FileViewPopup";
		var openerID	=	"userPortal";
		var callBack	=	"zoomMobileReceiptPopup";
		var popupTit	=	"<spring:message code='Cache.ACC_lbl_receiptPopup' />"; //영수증 보기
		var url			=	"/account/accountCommon/accountCommonPopup.do?"
						+	"popupID="		+ popupID	+ "&"
						+	"popupName="	+ popupName	+ "&"
						+	"fileID="		+ FileID	+ "&"
						+	"openerID="		+ openerID	+ "&"
						+	"callBackFunc="	+	callBack;
		Common.open("",popupID,popupTit,url,"340px","500px","iframe",true,null,null,true);
	},
	zoomMobileReceiptPopup : function(info){
		var me = this;			
		var popupID		=	"fileViewPopupZoom";
		var popupTit	=	"<spring:message code='Cache.ACC_lbl_receiptPopup'/>";	//영수증 보기
		var popupName	=	"FileViewPopup";
		var url			=	"/account/accountCommon/accountCommonPopup.do?"
						+	"popupID="		+	popupID		+	"&"
						+	"popupName="	+	popupName	+	"&"
						+	"fileID="		+	info.FileID	+	"&"					
						+	me.pageOpenerIDStr				+	"&"
						+	"zoom="			+	"Y"		
		Common.open(	"",popupID,popupTit,url,"490px","700px","iframe",true,null,null,true);
	},
	usageTextWritePopup : function(key, proofCode){
		var popupName	=	"UsageTextWritePopup";
		var popupID		=	"UsageTextWritePopup";
		var openerID	= 	"userPortal";
		var callBack	=	"usageTextWritePopup_CallBack"
		var popupTit	=	"<spring:message code='Cache.ACC_lbl_useHistory2' />" + " " + "<spring:message code='Cache.ACC_lbl_input'/>"; //적요 입력
		var url			=	"/account/accountCommon/accountCommonPopup.do?"
						+	"popupID="		+ popupID	+ "&"
						+	"openerID="		+ openerID	+ "&"
						+	"popupName="	+ popupName	+ "&"
						+	"receiptID="	+ key		+ "&"
						+	"proofCode="	+ proofCode + "&"
						+	"callBackFunc="	+	callBack;
		Common.open("",popupID,popupTit,url,"500px","250px","iframe",true,null,null,true);
	},	
	usageTextWritePopup_CallBack : function() {
		location.reload(); //증빙사용내역은 c:foreach 구문으로 출력되기 때문에 해당 목록만 새로고침 불가능
	},

	onClickPopButton:function(ProcessID,WorkItemID,TaskID,PerformerID,ProcessDescriptionID,FormInstID,SubKind,UserCode,FormID,BusinessData2, g_mode){
		var mode;
		var userID;
		var gloct;
		var subkind;
		var archived = "false";
	
		switch (g_mode){
			case "Approval" : mode="APPROVAL"; gloct = "APPROVAL"; subkind=SubKind; userID=UserCode; break;    // 미결함
			case "Process" : mode="PROCESS"; gloct = "PROCESS"; subkind=SubKind; userID=UserCode; break;		// 진행함
			case "Complete" : mode="COMPLETE"; gloct = "COMPLETE"; subkind=SubKind; archived="true"; userID=UserCode; break;	// 완료함
			case "Reject" : mode="REJECT"; gloct = "REJECT";  subkind=SubKind; archived="true"; userID=UserCode; break;		// 반려함
			case "TCInfo" : mode="COMPLETE"; gloct = "TCINFO"; subkind=SubKind; userID=""; break;		// 참조/회람함
		}
	
		CFN_OpenWindow("/account/expenceApplication/ExpenceApplicationViewPopup.do?mode="+mode
				+"&processID="+ProcessID
				+"&workitemID="+WorkItemID
				+"&taskID="+TaskID
				+"&performerID="+PerformerID
				+"&processdescriptionID="+ProcessDescriptionID
				+"&userCode="+userID
				+"&gloct="+gloct
				+"&formID="+FormID
				+"&forminstanceID="+FormInstID
				+"&ExpAppID="+BusinessData2
				+"&admintype=&archived="+archived
				+"&usisdocmanager=true&listpreview=N&subkind="+subkind+"", "", 1070, (window.screen.height - 100), "both");
	}
}	

/* 사용자 포탈 월별 신청내역 함수 */
var userMonthChartObj = {
	data : {"payYear":${fn:substring(payDate,0,4)}}
	,init:function(date){
		this.data.payYear = Number( date.replace(/(\d{4})(\d{2})/,"$1") );			
		this.chart = new Chart( $("#userMonthChart"),{
		    type: 'bar',
		    data: { labels: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] }
		    ,options: {
		    	legend: {
		            display: true,			            
		            labels: {			                
	                	boxWidth : 20	
	                	,usePointStyle : true
	                	,padding : 30
		            }
		        }
		        ,scales: {
		            xAxes: [{ stacked: true }],			            
			        yAxes: [{
			        	stacked: true
			        	,ticks: {
		                    callback: function(value, index, values) {
		                    	return userComm.makeComma(Math.round(value * 10) / 10)   
		                    }
		                }
		            }]
		        }
			    ,tooltips: {
		    	 	enabled: false,	
		            custom: function(tooltipModel) {	
		            	
		                // Tooltip Element			                
		                var tooltipEl = document.getElementById('monthTooltip');
		                
		             	// Create element on first render
		                if (!tooltipEl) {
		                	var $div = $("<div>",{ "id" : "monthTooltip" });			                	
		                	$div.append( $("<div>",{ "class" : "arrow_box" }) ).appendTo( $("body") );
		                	tooltipEl = $div.get(0); 
		                };
		             	// Hide if no tooltip
		                if (tooltipModel.opacity === 0) {
		                    tooltipEl.style.opacity = 0;
		                    return;
		                }
		                
		             	// Set caret Position
		                tooltipEl.classList.remove('above', 'below', 'no-transform');
		                if (tooltipModel.yAlign) {
		                    tooltipEl.classList.add(tooltipModel.yAlign);
		                } else {
		                    tooltipEl.classList.add('no-transform');
		                }
		                
		             	// Set Text
		                if (tooltipModel.body) {
		                    var title = tooltipModel.title[0];
		                	var body = tooltipModel.body;
		                	var lineIndex = Number( title.replace('월',"") ) - 1; 
		                    
		                	var $arrow_box = $(".arrow_box",tooltipEl);
		                	var $ul = $("<ul>");
		                	var $h1 = $("<h1>",{ "class" : "arrow_titlename" }).append( title );			                	
		                	var total = this._data.datasets.reduce( function(acc,cur,idx,arr){
		                		var $li = $("<li>",{ "class" : "arrowBtncolor0"+(idx+1) });
		                		var $txt01 = $("<span>",{ "class" : "txt01", "text" : cur.label });
		                		var $txt02 = $("<span>",{ "class" : "txt02", "text" : userComm.makeComma(cur.data[lineIndex]) });
		                		$li.append( $txt01 ).append( $txt02 ).appendTo( $ul );			                		
		                		return acc += cur.data[lineIndex]; 
		                	},0);
		                	var $span = $("<span>",{ "text" : userComm.makeComma(total) });			                	
		                	$h1.append( $span ).append("원").appendTo( $arrow_box.empty() );			                	
		                	$arrow_box.append( $ul );			                	
		                }
		                // `this` will be the overall tooltip
		                var position = this._chart.canvas.getBoundingClientRect();
		                // Display, position, and set styles for font
		                tooltipEl.style.opacity = 1;
		                tooltipEl.style.position = 'absolute';
		                tooltipEl.style.left = (position.left + window.pageXOffset + tooltipModel.caretX - 100 ) + 'px';
		                tooltipEl.style.top = (position.top + window.pageYOffset + tooltipModel.caretY - 20) + 'px';
		                tooltipEl.style.fontFamily = tooltipModel._bodyFontFamily;
		                tooltipEl.style.fontSize = tooltipModel.bodyFontSize + 'px';
		                tooltipEl.style.fontStyle = tooltipModel._bodyFontStyle;
		                tooltipEl.style.padding = tooltipModel.yPadding + 'px ' + tooltipModel.xPadding + 'px';
		                tooltipEl.style.pointerEvents = 'none';
		            }
		        }
		    }
		});
		this.changeChart();
	}
	,prevReport : function(){
		this.data.payYear 	-=  1;
        this.changeChart();
	}
	,nextReport : function(){
		this.data.payYear 	+=  1;
		this.changeChart();
	}		
	,changeChart : function(){			
		this.data.searchType = 'user';
		userComm.getData("/account/accountPortal/getAccountMonth.do",this.data)
			.done( $.proxy( function( data ){  this.draw( data.chartObj ) } ,this) )
			.fail(  function( e ){  console.log(e) })
	}
	,draw : function( obj ){
		var chartColorList = [ "rgba(253,207,2,100)","rgba(255,105,101,100)","rgba(101,203,204,100)","rgba(158,112,176,100)","rgba(118.215,116,100)" ];			
		this.chart.data.datasets = 
				obj.monthHeader.reduce(function( acc,cur,idx,arr ){
					var rowHeader = {};				
					rowHeader.label					= 	cur.Name;
					rowHeader.data 					= 	obj.monthList.map(function(item,idx){ return item["SUM_"+cur.Code] ? item["SUM_"+cur.Code] : 0 }); 
					rowHeader.backgroundColor 		=	chartColorList[idx] 
					rowHeader.hoverBackgroundColor	=	chartColorList[idx]
					rowHeader.borderColor			=	'rgb(255, 255, 255)'
					return acc = acc.concat( rowHeader );
				},[]);			
		obj.monthHeader.length > 0 && this.chart.update();			
		obj.monthHeader.length === 0 && this.chart.render(); 
		//타이틀
		$('#fixedTabAccViewArea #monthChartTit .squaregragh_Title').text( this.data.payYear+"년 월별 신청내역" );
	}
}
var userAccountChartObj={
	g_year :  ${fn:substring(payDate,0,4)},
	g_month: ${fn:substring(payDate,4,6)},
	chart:null,
	chartData :{labels :["전자세금계산서","모바일영수증","법인카드"]					
				,datasets : [{
					data : [${proofCount.TaxBillAmount},${proofCount.ReceiptAmount},${proofCount.CorpCardAmount}]
					,borderWidth : 0
					,backgroundColor : [ "rgba(35,29,180,1)","rgba(0,127,244,1)","rgba(100,202,204,1)","rgba(0,0,0,0.1)"]
				}]},
	chartObj : {data : this.chartData
				,options: {tooltips: { 
					    		enabled: true
					    		,callbacks: {
					                label: function(tooltipItem, data) {				                	
					                    var label = data.labels[tooltipItem.index] || '';
					                    if (label) {
					                        label += ': ';
					                    }
					                    return label += userComm.makeComma(data.datasets[0].data[tooltipItem.index])+"원";
					                }
					            }
					    	}
							,legend: { display: false } }},		
	initChart:function(data){
		this.chartObj.data = this.chartData;
		this.chartObj.data.datasets[0].data=data;
		this.chartObj.type = 'doughnut';
		if(this.chartObj.data.datasets[0].data.length > 3 && this.chartObj.data.datasets[0].data[3] === 100){
			this.chartObj.options.tooltips.enabled = false;
		} else {
			this.chartObj.options.tooltips.enabled = true;
		}
		this.chart = new Chart(  $("#userProofDoughnut"), this.chartObj ); 
	},					
	drawChart:function(data){
		this.chartObj.data.datasets[0].data=data;
		this.chart.data =this.chartObj.data;
		if(this.chartObj.data.datasets[0].data.length > 3 && this.chartObj.data.datasets[0].data[3] === 100){
			this.chartObj.options.tooltips.enabled = false;
		} else {
			this.chartObj.options.tooltips.enabled = true;
		}
		this.chart.update();
	},
	getUserMonth:function(v){

		if (v == "+") {
			this.g_month++;
			if (this.g_month > 12) { this.g_year++; this.g_month = 1; }
		} else if (v == "-"){
			this.g_month--;
			if (this.g_month < 1) { this.g_year--; this.g_month = 12; }
		}
		this.g_month=parseInt(this.g_month)<10?"0"+parseInt(this.g_month):this.g_month;

		$.ajax({
			url:"/account/accountPortal/getAccountUserMonth.do",
			type:"POST",
			data:{"payDate" : this.g_year+""+this.g_month},
			success:function (r) {
				if(r.result == "ok"){
					$("#fixedTabAccViewArea #portalTitle").text(r.payDate.substring(0,4)+"년"+r.payDate.substring(4,6));
					var proofCount = r.proofCount;
					//증빙별
					if(proofCount != undefined) {
						if(Array.isArray(proofCount)) proofCount = proofCount[0];
						var data = [proofCount["TaxBillAmount"],proofCount["ReceiptAmount"],proofCount["CorpCardAmount"]];
						if (proofCount["TotalAmount"] == 0){
							data=[0,0,0,100];
						}
						userAccountChartObj.drawChart(data);

						var sListHtml ='';
						var colArray = [["TaxBill","전자세금계산서"],["Receipt","모바일영수증"],["CorpCard","법인카드"]	];
						for (var i=0; i< colArray.length;i++){
							sListHtml   += '<li>'+
										'<div class="DetailRank10 height"><div class="Gragh_color blue'+(i+1)+'"></div>'+colArray[i][1]+'('+proofCount[colArray[i][0]+"Cnt"]+'건)</div>'+
										'<div class="Detailaccount10">'+userComm.makeComma(proofCount[colArray[i][0]+"Amount"])+'원</div>'+
									'</li>';
						}

						$("#account_portal_user_proof_tot").text(userComm.makeComma(proofCount["TotalAmount"]));
						$("#account_portal_user_proof_list").html(sListHtml);
					}	
					
					var accountCount = r.accountCount;
					//계정별
					if(accountCount != undefined) {
						if (accountCount.length == 0){
							$("#account_portal_user_account_tot").text("0");
							$("#account_portal_user_account_chart").html('<p class="OWList_none">조회할 목록이 없습니다.</p>');
							$("#account_portal_user_account_slide").html("");
						}
						else{
							var sChartHtml = '<ul class="Newgraph_stick">';
							var sListHtml ='';
							var sSlideHtml = '';
							var totCnt = accountCount[0].Amount;
							
							for (var i=1; i< accountCount.length;i++){
								var per		= accountCount[i].Amount*100.00/totCnt;

								if (i>4 && i%5 == 1){
									sChartHtml   += '</ul><ul class="Newgraph_stick" style="display:none">';
								}
								sChartHtml	+=	'<li>'+
									'		<div class="conR">'+
									'	<div class="Detailtxt20">'+accountCount[i].Name+'('+accountCount[i].Cnt+')</div>'+
									'	<div class="zt-skill-bar"><div class="skillbar0'+(i%5+1)+'" data-width="'+per+'" style="width: '+per+'%;"></div></div>'+
									'</div>'+
									'<div class="Detailnumber20">'+userComm.makeComma(String(accountCount[i].Amount))+'원</div>'+
								'</li>';
							}
							
							$("#account_portal_user_account_tot").text(userComm.makeComma(String(totCnt)));
							$("#account_portal_user_account_chart").html(sChartHtml+"</ul>");
							$("#account_portal_user_account_slide").html("");

							if (accountCount.length > 6){
								for (var i=0; i<Math.ceil(accountCount.length/5); i++){
									$("#account_portal_user_account_slide").append('<a class="'+(i==0?'Slideroll_btn_on':'Slideroll_btn_off')+'"  onclick="userComm.AccountSlide('+i+')"><span></span></a>');
									
								}
							}	
						}		
					}
					
				}
				else{
					alert(r.message);
				}
			},
			error:function(response, status, error){
				alert(error)
			}
		});
	}	
}

var userComm = {		
		getData : function(purl,param){				
			var deferred = $.Deferred();
			$.ajax({
				url: purl,
				type:"POST",
				data: param,			
				success:function (data) { deferred.resolve(data);},
				error:function(response, status, error){ deferred.reject(status); }
			});				
		 	return deferred.promise();
		}
		,makeComma : function( value ){ return String(value).replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,') }
		,lpad : function( str ){ return /^[0-9]$/.exec(str) ? String(str).replace( /^([0-9])$/, '0$1' ) : String(str) }
		,AccountSlide:function(idx){
			$("#account_portal_user_account_slide a").removeClass('Slideroll_btn_on').addClass('Slideroll_btn_off');
			$("#account_portal_user_account_chart ul").hide();
		
			$('#account_portal_user_account_slide a').eq(idx).removeClass('Slideroll_btn_off').addClass('Slideroll_btn_on');
			$('#account_portal_user_account_chart ul').eq(idx).show();	
		}	
}


userPortal.pageInit();
</script>
