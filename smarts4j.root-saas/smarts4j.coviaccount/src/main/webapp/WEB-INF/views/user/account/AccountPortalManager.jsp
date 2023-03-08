<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="egovframework.baseframework.util.RedisDataUtil,egovframework.baseframework.util.SessionHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>


	<script type="text/javascript" src="/covicore/resources/ExControls/Chart.js-master/Chart.js<%=resourceVersion%>"></script>  
<style>
	.Gragh_color.blue6 { background-color:#e5e5e5; }
	.DetailRank10 { width:45%; }
	.Detailaccount10 { text-align:right; }
	.Detailaccount10, .Detailaccount30 { width:auto; }
	#monthTooltip{ z-index:9999; position:absolute !important; }
</style>
		<!-- 컨텐츠 시작 -->
			<!-- 이어카운팅 포탈 시작-->
			<div class="eAccountingPortalWrap" style="overflow-x:hidden; height: 100%;">
				<div class="eAPListBoxWrap">
					<!-- 상단 배너 시작 -->
					<fmt:parseDate value="${fn:replace( deadline.DeadlineFinishDate,'.','')}" var="dlDate" pattern="yyyyMMdd"/>
					<fmt:formatDate value="${dlDate}" pattern="yyyy" var="yyyy"/>					
					<fmt:formatDate value="${dlDate}" pattern="MM" 	 var="mm"/>
					<fmt:formatDate value="${dlDate}" pattern="dd" 	 var="dd"/>
					<fmt:formatDate value="${dlDate}" pattern="E" 	 var="e"/>					
					<div class="eAPBanner_1_new_n">
						<div class="eAPBannerUpText_new" <c:if test="${deadline.IsUse == 'N'}">style="display: none;"</c:if>>
							<strong>${deadline.StandardMonth == "01" ? 12 : mm-1}월 경비 마감<span class="tx_blue"> ${mm}월 ${dd}일(${e})</span>까지</strong>	
							<span class="noti">${deadline.NoticeText}</span>
						</div>
					</div>
					<!-- 상단 배너 끝 -->	
				</div>
				<div class="squaregraph01_wrap">
					<div class="selBox" style="width:95px;" >
						<select class="selectType02" id="topReportCate">
							<option value=""><%=SessionHelper.getSession("DN_Name")%></option>							
						</select>						
					</div>
					<span class="squaregragh_Title" style="margin-left:5px;" id="reportCalTxt">${fn:substring(payDate,0,4)}년 ${fn:substring(payDate,4,6)}월 리포트</span>
					<div class="MyBtnType01 newbtn01" 	id="reportCal"><a href="#" class="pre"></a><a href="#" class="next"></a></div>
					<span class="squaregragh_Textb" 	id="reportExpTot"><fmt:formatNumber value="${totalSummery.amount}" pattern="#,###" />						</span>
					<span class="squaregragh_Title"> 원</span>
					<span class="squaregragh_Text_sub" 	id="reportExpCnt">(총 ${totalSummery.cnt}건)</span>					
				</div>
				
				
				<!-- 1단 그래프 시작 -->
				<div class="Newgragh_eAPBoard" id="reportDiv">
					<div class="Newgragh_eAPBoardL" id="proofDiv">					
						<h3 class="Newgragh_eAPBoardTitle">용도별 지출현황</h3>
						
						<div class="Newgragh_eAPBoardContents">
							
							<div class="Newgragh_eAPBoardCardBox">
								<canvas id="proofDoughnut"></canvas>
							</div>	
							
							
							<div class="Newgragh_Detailrank_menu">
								<div class="selBox" style="width:105px;" >
									<select class="selectType02" id="proofCategory">
										<c:forEach var="list" items="${deptCateList}" varStatus="st">
											<option value="${list.DeptCode}">${list.DeptName}</option>
										</c:forEach>
									</select>						
								</div>
								<a class="per_tit">전월대비</a>
							</div>								
							<div class="Newgragh_Detailrank" id="proofList">								
								<ul>
									<c:forEach var="list" items="${proofCount}" varStatus="st">
										<c:if test="${list.ProofCode ne 'Total' }">
											<li code="${list.ProofCode}">
												<div class="DetailRank10 height"><div class="Gragh_color blue${st.index}"></div>>${list.ProofCodeName}</div>
												<div class="Detailaccount10"><fmt:formatNumber value="${list.Amount}"/>원</div>
												<div class="Detailaccount30"><font style="color:#002bb4">-10%</font><div class="Gragh_per down"></div></div>												
											</li>
										</c:if>
									</c:forEach>
									<c:if test="${fn:length(proofCount) == 0}">
										<div class="OWList_none">조회할 목록이 없습니다.</div>
									</c:if>
								</ul>								
							</div>								
						</div>
						<!--슬라이드 버튼 -->						
						<c:set var="totRow" value="${ fn:length(proofCount)/5 }"/>						
						<div class="Slideroll_btn">
							<c:forEach var="list" begin="1" end="${totRow+( 1-(totRow%1))%1}" step="1" varStatus="st">
								<a class="Slideroll_btn_${st.index eq 1 ? 'on' : 'off'}" href="#"><span></span></a>
							</c:forEach>
						</div>	
						<!--슬라이드 버튼 끝 -->
						
					</div>
					
					<div class="Newgragh_eAPBoardL" id="accountDiv">
						<h3 class="Newgragh_eAPBoardTitle">부서별 지출현황</h3>
						<div class="Newgragh_eAPBoardContents">							
							<div class="Newgragh_eAPBoardCardBox">
								<canvas id="accountDoughnut"></canvas>
							</div>	
							<div class="Newgragh_Detailrank_menu">
								<div class="selBox" style="width:105px;" >
									<select class="selectType02" id="accountCategory">
										<c:forEach var="list" items="${accountCateList}" varStatus="st">
											<option value="${list.AccountCode}">${list.AccountName}</option>
										</c:forEach>
									</select>						
								</div>
								<a class="per_tit">전월대비</a>
							</div>
							<div class="Newgragh_Detailrank" id="accountList">
								<ul>
									<c:forEach var="acctCont" items="${accountCount}" varStatus="st">
										<c:if test="${acctCont.Code ne 'Total' and st.index <= 5 }">
											<li code="${acctCont.Code}">
												<div class="DetailRank10 height"><div class="Gragh_color blue${st.index}"></div>${acctCont.Name}</div>
												<div class="Detailaccount10"><fmt:formatNumber value="${acctCont.Amount}"/>원</div>
												<div class="Detailaccount30"><font style="color:#002bb4">-10%</font><div class="Gragh_per down"></div></div>
											</li>
										</c:if>
									</c:forEach>
									<c:if test="${fn:length(accountCount) == 0}">
										<div class="OWList_none">조회할 목록이 없습니다.</div>
									</c:if>															
								</ul>
							</div>	
						</div>
						<!--슬라이드 버튼 -->						
						<c:set var="totRow" value="${ fn:length(accountCount)/5 }"/>						
						<div class="Slideroll_btn">
							<c:forEach var="list" begin="1" end="${totRow+( 1-(totRow%1))%1}" step="1" varStatus="st">
								<a class="Slideroll_btn_${st.index eq 1 ? 'on' : 'off'}" href="#"><span></span></a>
							</c:forEach>
						</div>	
						<!--슬라이드 버튼 끝 -->
					</div>
				</div>
				<!-- 1단 그래프 끝 -->	
				<div class="squaregraph01_wrap" id="monthChartTit">						
					<div class="selBox" style="width:95px;" >
						<select class="selectType02" id="topMonthCate">
							<option value=""><%=SessionHelper.getSession("DN_Name")%></option>							
						</select>						
					</div>
					<span class="squaregragh_Title" style="margin-left:5px;">${yyyy}년 월별 신청내역</span>
					<div class="MyBtnType01 newbtn01" id="monthCal"><a href="#" class="pre"></a><a href="#" class="next"></a></div>	
				</div>
				<!-- 월별 그래프 시작 -->				
				<canvas id="monthChart" width="400" height="100"></canvas>
				<!-- 월별그래프 끝 -->
				
			<!-- 하단 게시판영역 -->
			<article class="mainBoradTabView eaccportal">
				<div class="mainBoardTabCont clearFloat total">				
					<div class="mBoardNoticeCont03">
						<div class="mBoardTabCont">
							<div>
								<div class="mNotiTitle">
									<ul class="tabBoard_tabList mSubTabMenu03 clearFloat">
										<li class="active">
											<a href="#" class="tabTitle_sub" ><span>감사레포팅</span></a>
											<a data-type="AD" class="btnMainMore">more +</a>
										</li>
									</ul>
								</div>
								<div class="circle_box" id="auditList">								
									<ul>
										<li class="circleBtncolor01" style="cursor: pointer;">
											<span class="txt01">동일가맹점 중복 사용</span>
											<span class="txt02"><strong>${auditDupStore}건</strong></span>
										</li>
										<li class="circleBtncolor01" style="cursor: pointer;">
											<span class="txt01">접대비 사용보고서</span>
											<span class="txt02"><strong>${auditEnterTain}건</strong></span>
										</li>
										<li class="circleBtncolor01" style="cursor: pointer;">
											<span class="txt01">휴일/심야 사용보고서</span>
											<span class="txt02"><strong>${auditHolidayUse}건</strong></span>
										</li>										
										<li class="circleBtncolor01" style="cursor: pointer;">
											<span class="txt01">규정금액 이상 사용보고서</span>
											<span class="txt02"><strong>${auditLimitAmount}건</strong></span>
										</li>										
										<li class="circleBtncolor01" style="cursor: pointer;">
											<span class="txt01">사용자 휴가 사용 보고서</span>
											<span class="txt02"><strong>${auditUserVacation}건</strong></span>
										</li>
									</ul>									
								</div>								
							</div>
						</div>	
					</div><div class="mBoardNoticeCont04">
						<div class="mBoardTabCont">
							<div>
								<div class="mNotiTitle" id="apprTabs">
									<ul class="tabBoard_tabList mSubTabMenu clearFloat">
										<li class="active">
											<a href="#" class="tabTitle" data-mode="APPROVAL"><span>My 승인대기(<span id="ApprovalCnt">0</span>)</span></a>
											<a data-type="AA" class="btnMainMore">more +</a>
										</li>
										<li>
											<a href="#" class="tabTitle" data-mode="Process"><span>My 진행 (<span id="ProcessCnt">0</span>)</span></a>
											<a data-type="AP" class="btnMainMore">more +</a>
										</li>
									</ul>
								</div>
								<div class="mBaordTabView active EarWaitList" id="apprTabsList">
									<ul class="Earwait_time"></ul>
								</div>								
							</div>
						</div>	
					</div>
					<div class="mBoardNoticeCont05">
						<div class="mBoardTabCont02" id="expsTabs">
							<div>
								<div class="mNotiTitle">
									<strong class="mNoTit">사용내역</strong>	
									<ul class="mSubTabMenu02 clearFloat">
										<li class="active">
											<a href="#" class="tabTitle"><span>My 법인카드(${corpCardListCnt})</span></a>
											<a data-type="C" class="btnMainMore">more +</a>
										</li>
										<li class="">
											<a href="#" class="tabTitle"><span>My 세금계산서(${taxBillListCnt})</span></a>
											<a data-type="T" class="btnMainMore">more +</a>
										</li>
										<li class="">
											<a href="#" class="tabTitle"><span>My 영수증(${billListCnt})</span></a>
											<a data-type="R" class="btnMainMore">more +</a>
										</li>
									</ul>
								</div>																						
							</div>
						</div>								
						<div class="mBaordTabView02 active AllCadegory_searchlist">
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
						
						<div class="mBaordTabView02 AllCadegory_searchlist">
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
						<div class="cost_bottom" id="expenceClaimGuide">
						<li>	
							<span class="cost_txt01" >경비 청구 가이드</span>
							<div class="cost_arrow Rig"></div>
						</li>	
						<c:forEach items="${guideList}" var="list" varStatus="status">
							<li>	
								<a href="#" onclick="board.goViewPopup('Board', '${list.Reserved3}', 1, '${list.Reserved1}', '${list.Reserved2}','W');">
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
			<c:if test="${fn:length(budgetStd)> 0}">
				<div class="squaregraph01_wrap" >
					<div class="selBox" style="width:95px;" >
						<select class="selectType02" id="topBudgetCate">
							<option value=""><%=SessionHelper.getSession("DN_Name")%></option>							
						</select>						
					</div>
					<span class="squaregragh_Title" style="margin-left:5px;"><font id="budgetTitle">${yyyy}년</font> 
						<div class="selBox" style="width:95px; ${fn:length(budgetStd)==1?";display:none":""}" >
							<select class="selectType02" id="budgetCategory">
							<c:forEach items="${budgetStd}" var="list" varStatus="status">
								<option value="${list.Code}†${list.Reserved1}†${list.Reserved2}">${list.CodeName}</option>
							</c:forEach>
							</select>						
						</div>
						<c:if test="${fn:length(budgetStd)==1}"><span class="tx_blue">${budgetStd[0].CodeName}</span> </c:if>
						
						월별 예산대비 지출 현황
					</span>
					<div class="MyBtnType01 newbtn01" id="budgetCal"><a href="#" class="pre"></a><a href="#" class="next"></a></div>	
				</div>
				<div class="Monthlygraph_box">
					
					<div class="Monthly_graph_1">	
						<span class="squaregragh_Title" >예산 </span>
						<span class="squaregragh_Textb2" id="budgetAmount"><fmt:formatNumber value="${budgetTotal.BudgetAmount}" pattern="#,###" /></span>
						<span class="squaregragh_Title" id="baseTermTitle"> 원</span>
					</div>
					<div class="Monthly_graph_lineBar" style="display:none">
						<div class="Monthly_graph_lineBar blue_1" style="margin-left:0px;width:30%;"></div>
						<div class="Monthly_graph_lineBar blue_2" style="margin-left:30%;width:20%"></div>
						<div class="Monthly_graph_lineBar blue_3" style="margin-left:50%;width:50%;"></div>
					</div>
					
					<div class="Monthly_3box" style="height: 100px;width:100%;">
						<div class="Monthly_content">
							<div class="Monthly_graph_lineBar blue_a" style="margin-left:0px; height:85px; width:5px;"></div>
							<div class="Monthly_content_tbox">
								<p class="Monthly_content_tit">지출</p>
								<span class="Monthly_content_t1" id="UsedAmount"><fmt:formatNumber value="${budgetMonthList[12].UsedAmount}" pattern="#,###" /></span><span>원</span>
							</div>
						</div>
						<div class="Monthly_content">
							<div class="Monthly_graph_lineBar blue_b" style="margin-left:0px; height:85px; width:5px;"></div>
							<div class="Monthly_content_tbox">
								<p class="Monthly_content_tit">진행</p>
								<span class="Monthly_content_t1" id="processAmount"><fmt:formatNumber value="${budgetMonthList[12].pending}" pattern="#,###" /></span><span>원</span>
							</div>
						</div>
						<div class="Monthly_content">
							<div class="Monthly_graph_lineBar blue_c" style="margin-left:0px; height:85px; width:5px;"></div>
							<div class="Monthly_content_tbox">
								<p class="Monthly_content_tit">잔액</p>
								<span class="Monthly_content_t1" id="leftAmount"><fmt:formatNumber value="${budgetTotal.BudgetAmount-budgetMonthList[12].UsedAmount}" pattern="#,###" /></span><span>원</span>
							</div>
						</div>
					</div>					
					<!--자기개발비 월별 지출 현황 그래프 -->
					<div class="MonColorChart_graphContent_b">									
						<canvas id="budgetChart" width="400" height="100"></canvas>						
					</div>
					
				</div>
			</c:if>
						
<script>

	var ProfileImagePath = Common.getBaseConfig('ProfileImagePath').replace("{0}", Common.getSession("DN_Code"));
	
	/* 공통함수 */
	
	var mngComm = {		
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
		,getPrevDate : function( str ){ 
			var dateArr 		= /(\d{4})(\d{2})/.exec( str );
			var strYear  		= this.lpad( (dateArr[2]-1) * 1 === 0 ? dateArr[1]-1 : dateArr[1] );
            var strMonth 		= this.lpad( (dateArr[2]-1) * 1 === 0 ? 12 : (dateArr[2]-1) );
            return strYear+strMonth;			
		}
		,getNextDate : function( str ){ 
			var dateArr 		= 	/(\d{4})(\d{2})/.exec( str );
			var date  			=   new Date( dateArr[1] , dateArr[2] , 1 );
			var strYear  		=   date.getFullYear();
            var strMonth   		=   this.lpad( date.getMonth()+1 );
			return strYear+strMonth;	
		}
		,addEvent : function(){				
			
			/* 리포트 이벤트 */
			reportTopObj.addEvent();
			
			/* 월별 신청내역 이벤트 */			
			monthChartObj.addEvent();
						
			/* 예산대비 지출현황 이벤트 */			
			budgetChartObj.addEvent();			

			$("#topReportCate,#topMonthCate,#topBudgetCate,#budgetCategory").on( 'change', function(){
				var attr =  this.getAttribute('id');
				attr === 'topReportCate' 	&& reportTopObj.changeReport();
				attr === 'topMonthCate'		&& monthChartObj.changeChart();
				attr === 'topBudgetCate' 	&& budgetChartObj.changeChart();
				attr === 'budgetCategory' 	&& budgetChartObj.changeChart();
			});	
			
			/* 경비 청구 가이드		
			$("#expenceClaimGuide a").on('click',function(){
				var obj = Common.getBaseCode('EXPENSE_CLAIM_GUIDE').CacheData[ $(this).index() ];
				board.goViewPopup('Board', obj.Reserved3, 1, obj.Reserved1, obj.Reserved2,'W');
			});
				 */			
			/* 탭 이벤트 */
			$("#expsTabs li").on('click',function(){							
				$("#expsTabs li").removeClass('active').eq( $(this).index() ).addClass('active');
				$(".mBaordTabView02").removeClass('active').eq( $(this).index() ).addClass('active');
			});			
			
			/* 승인대기, 진행 */
			$("#apprTabs #ApprovalCnt").text( $("li.eaccountingMenu02 a[id$=Approval] span.fCol19abd8").text().trim());
			$("#apprTabs #ProcessCnt").text( $("li.eaccountingMenu02 a[id$=Process] span.fCol19abd8").text().trim());
			
			$("#apprTabsList").on('click',function(){
				var value = $(event.target).closest('.clearFloat').data('item');
				var mode = $("#apprTabs .active").data('mode');
				value && onClickPopButton(value.ProcessID,value.WorkItemID,value.TaskID,value.PerformerID,value.ProcessDescriptionID,value.FormInstID,value.FormSubKind,value.UserCode,value.FormID,value.BusinessData2,mode);
			});
			
			$("#apprTabs li .tabTitle").on('click',function(){							
				var idx = $("#apprTabs li .tabTitle").index(this);
				var obj = $("#apprTabs").next().find('ul');
				var dataObj = this.dataset;				
				$("#apprTabs li").removeClass('active').eq( idx ).addClass('active');
				mngComm.getData("/approval/user/getApprovalListData.do",{ 
						mode : dataObj.mode
						,searchGroupType:"" 
						,bstored: "false"
						,userID: Common.getSession().UR_Code
						,businessData1: "ACCOUNT"
						,pageSize: "3"
						,pageNo: "1" })
				.done(function( data ){					
					obj.empty().append(
						data.list.length > 0 		
							?	data.list.map( function( item,idx ){
									var $li = $("<li>",{ "class" : "clearFloat" }).data( 'item',item );
									var $listShell = $("<div>",{ "class" : "listShell" });	
									$listShell
										.append(
											$("<a>",{ "class" : "exePhoto_List" })
												.append( 
													$("<p>").append( $("<img>",{ "src" : coviCmn.loadImage(item.PhotoPath), "class" : "mCS_img_loaded" }) ) 
												)	
										)
										.append(
											$("<a>",{ "class" : "exeInfoTxt Proceed" })
												.append(
													$("<p>",{ "class" : "exeTit unread ApprovalList" })
														.append( $("<p>",{ "class" : "title_unread", "text" : item.FormSubject }) )
														.append( $("<p>",{ "class" : "exepart" }) )
														.append( $("<span>",{ "class" : "fcStyle1", "text" : item.InitiatorName }) )
														.append( $("<span>",{ "class" : "fcStyle", "text" :  dataObj.mode === "APPROVAL" ? item.Created : item.Finished }) )											
												)												
										).appendTo( $li );
									return $li;
								})
							:	$("<li>",{ "class" : "OWList_none", "text" : "조회할 목록이 없습니다." })
					);
				})
				.fail(  function( e ){  console.log(e) });
			}).eq(0).trigger('click');
			
			/* 감사규칙 링크 */
			$("#auditList span").on('click',function(){ $("#account_AuditReportaccountuserAccount").trigger('click') });
			
			//법인카드
			$('.corpCard').click(function(){
				mngComm.cardReceiptPopup($(this).attr("data"));
			});
			//세금계산서
			$('.taxInvoice').click(function(){
				mngComm.taxInvoicePop($(this).attr("data"));
			});
			//영수증팝업
			$('.mobileReceipt').click(function(){
				mngComm.mobileReceiptPopup($(this).attr("data"));
			});
			//법인카드 - 계정과목(표준적요)/적요
			$('.regist_corpcard').click(function(){
				mngComm.usageTextWritePopup($(this).attr("data"), "CorpCard");
			});
			//영수증 - 계정과목(표준적요)/적요
			$('.regist_receipt').click(function(){
				mngComm.usageTextWritePopup($(this).attr("data"), "Receipt");
			});
			
			/*more */
			$(".btnMainMore").on("click", function(){
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
					case "AD":	//감사레포팅
						eAccountContentHtmlChangeAjax('account_AuditReportaccountuserAccount'
								, "<spring:message code='Cache.ACC_lbl_auditReport' />"
								, '/account/layout/account_AuditReport.do?CLSYS=account&CLMD=user&CLBIZ=Account'
								, {callType:"Portal"});
						break;
				}
			});
			
		}
		,objectInit : function(){			
			//기준날짜 구하기
			this.setCategory();
			this.addEvent();
		}
		,setCategory : function(){			
			mngComm.getData("/account/accountPortal/getTopCategory.do",{})
				.done( $.proxy( function( data ){
					$("#topReportCate,#topMonthCate,#topBudgetCate").append( data.topCateList.map( function( item,idx ){ return $("<option>",{ "value" : item.UserCode, "text" : item.DisplayName }).data('type', item.type )}) )
					var date = new Date();			
					var strDate = "${payDate}".length > 0 ? "${payDate}" : date.getFullYear()+this.lpad( date.getMonth()+1 );
					reportTopObj.init(strDate);
					monthChartObj.init( strDate );
					<c:if test="${fn:length(budgetStd)> 0}">
						budgetChartObj.init( strDate );
					</c:if>	
					
				} ,this))
				.fail(  function( e ){  console.log(e) })
		}		
		,imgError : function(image) {
		    image.onerror = "";
		    image.src = ProfileImagePath+"no_image.jpg";
		    return true;
		}
		,cardReceiptPopup : function(key){
			var popupName	=	"CardReceiptPopup";
			var popupID		=	"cardReceiptPopup";
			var openerID	=	"mngComm";
			var popupTit	=	"<spring:message code='Cache.ACC_lbl_cardReceiptInvoice' />"; //신용카드 매출전표
			var url			=	"/account/accountCommon/accountCommonPopup.do?"
							+	"popupID="		+ popupID	+ "&"
							+	"openerID="		+ openerID	+ "&"
							+	"popupName="	+ popupName	+ "&"
							+	"approveNo="	+ key		+ "&"
							+	"receiptID="	+ key;
			Common.open("",popupID,popupTit,url,"320px", "510px","iframe",true,null,null,true);
		}
		,taxInvoicePop : function(key){
			var popupName	=	"TaxInvoicePopup";
			var popupID		=	"taxInvoicePopup";
			var popupTit	=	"<spring:message code='Cache.ACC_lbl_taxInvoiceCash' />";	//전자세금계산서
			var url			=	"/account/accountCommon/accountCommonPopup.do?"
							+	"popupID="		+	popupID		+	"&"
							+	"popupName="	+	popupName	+	"&"
							+	"taxInvoiceID="	+	key;
			Common.open("",popupID,popupTit,url,"980px", "720px","iframe",true,null,null,true);
		}
		,mobileReceiptPopup : function(FileID){
			var popupName	=	"FileViewPopup";
			var popupID		=	"FileViewPopup";
			var openerID	=	"mngComm";
			var callBack	=	"zoomMobileReceiptPopup";
			var popupTit	=	"<spring:message code='Cache.ACC_lbl_receiptPopup' />"; //영수증 보기
			var url			=	"/account/accountCommon/accountCommonPopup.do?"
							+	"popupID="		+ popupID	+ "&"
							+	"popupName="	+ popupName	+ "&"
							+	"fileID="		+ FileID	+ "&"
							+	"openerID="		+ openerID	+ "&"
							+	"callBackFunc="	+	callBack;
			Common.open("",popupID,popupTit,url,"340px","500px","iframe",true,null,null,true);
		}
		,zoomMobileReceiptPopup : function(info){
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
		}
		,usageTextWritePopup : function(key, proofCode){
			var popupName	=	"UsageTextWritePopup";
			var popupID		=	"UsageTextWritePopup";
			var openerID	= 	"mngComm";
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
		}
		,usageTextWritePopup_CallBack : function() {
			location.reload(); //증빙사용내역은 c:foreach 구문으로 출력되기 때문에 해당 목록만 새로고침 불가능
		}
	}
	
	/* 리포트 함수  */	
	var reportTopObj = {
		data : {}
		,init : function( date ){ 
			this.data.payDate = date;
			this.changeReport();
		}
		,prevReport : function(){			
			this.data.payDate 	= mngComm.getPrevDate( this.data.payDate );			
            this.changeReport();
		}
		,nextReport : function(){
			this.data.payDate 	= mngComm.getNextDate( this.data.payDate );			
			this.changeReport();
		}
		,changeReport : function(){
			var type = $('#topReportCate option:selected').data('type');
			this.data.stdCode = $('#topReportCate').val();			
			this.data.searchType = type ? type : '';
			mngComm.getData("/account/accountPortal/getReportCategoryList.do",this.data)
				.done( $.proxy( function( data ){  this.drawReport( data ) } ,this) )
				.fail(  function( e ){  console.log(e) })				
		}
		,drawReport : function( data ){
			var dateArr 		= /(\d{4})(\d{2})/.exec( this.data.payDate );
			$("#reportCalTxt").text( dateArr[1]+"년 "+dateArr[2]+"월 리포트");						
			$("#reportExpTot").text( mngComm.makeComma( data.totalSummery.amount )  );
			
			$("#reportExpCnt").text( "(총 "+data.totalSummery.cnt+"건)" );			
			$("#proofCategory")
				.empty()
				.append( data.deptCateList.map( function( item,idx ){ return $("<option>",{ "value" : item.DeptCode, "text" : item.DeptName }) }) )
				.trigger('change');				
			$("#accountCategory")
				.empty()
				.append( data.accountCateList.map( function( item,idx ){ return $("<option>",{ "value" : item.AccountCode, "text" : item.AccountName }) }) )
				.trigger('change');			            			
		}
		,addEvent : function(){
			$('#proofCategory').on('change',function(){ proofObj.changeCategory( $(this).val() ) });
			$('#accountCategory').on('change',function(){ accountObj.changeCategory( $(this).val() ) });			
			
			$("#reportCal").on('click',function(){
				event.target.classList.value === 'pre'  && reportTopObj.prevReport();
				event.target.classList.value === 'next' && reportTopObj.nextReport();
			});
			
			$("#proofDiv .Slideroll_btn").on('click',function(){
				var click = $(event.target).parent();
				if( $(event.target).parent().attr('class') === 'Slideroll_btn_off' ){
					var idx = $('a',this).index( click );					
					$("#proofList li").hide().slice( idx*5 , (idx*5)+5 ).show();
					$('a',this).prop('class','Slideroll_btn_off').eq( idx ).prop('class','Slideroll_btn_on')
				}				
			});
			$("#accountDiv .Slideroll_btn").on('click',function(){
				var click = $(event.target).parent();
				if( $(event.target).parent().attr('class') === 'Slideroll_btn_off' ){
					var idx = $('a',this).index( click );					
					$("#accountList li").hide().slice( idx*5 , (idx*5)+5 ).show();
					$('a',this).prop('class','Slideroll_btn_off').eq( idx ).prop('class','Slideroll_btn_on')
				}				
			});
		}
		,reportChartDraw : function(calcList){			
			var chartColorList = [ "rgba(35,29,180,100)","rgba(0,127,244,100)","rgba(100,202,204,100)","rgba(251,210,5,100)","rgba(255,108,100,100)" ];
			var chartObj = {
				data : {
					labels : calcList.length > 0 ? calcList.map(function(item){ return item.ProofCodeName || item.Name  }) : []					
					,datasets : [{
			    		data : calcList.length > 0 ? calcList.map(function(item){ return item.Amount }) : [1]
			    		,borderWidth : 0
			    		,backgroundColor : chartColorList.slice(0,calcList.length)
			    	}]					
				}
				,options: {		    	
			    	tooltips: { 
			    		enabled: true//calcList.length > 0
			    		,callbacks: {
			                label: function(tooltipItem, data) {				                	
			                    /* var label = data.labels[tooltipItem.index] || '';
			                    if (label) {
			                        label += ': ';
			                    }
			                    return label += mngComm.makeComma(data.datasets[0].data[tooltipItem.index])+"원"; */
			                    return mngComm.makeComma(data.datasets[0].data[tooltipItem.index])+"원";
			                }
			            }
			    	}
					,legend: { display: false }
			    }
			}			
			if( !this.chart ){				
				chartObj.type = 'doughnut';
				this.chart = new Chart(  this === proofObj ? $("#proofDoughnut") : $("#accountDoughnut") , chartObj ); 
			} else {				
				this.chart.data = chartObj.data;
				this.chart.options.tooltips.enabled = chartObj.options.tooltips.enabled;				
				this.chart.update();
			}
		}
	}
	
	var proofObj = {			
		data : {}
		,changeCategory : function( dept ){						
			this.data.payDate 		= reportTopObj.data.payDate; 
			this.data.deptCode 		= (dept == "전체" ? "" : dept);			
			this.data.searchType 	= reportTopObj.data.searchType;
			this.data.stdCode 		= reportTopObj.data.stdCode;
			this.data.prevPayDate 	= mngComm.getPrevDate( this.data.payDate );
			mngComm.getData("/account/accountPortal/portalProof.do",this.data)
				.done( $.proxy( function( data ){ this.draw( data ) },this))
				.fail(  function( e ){  console.log(e) })
		}
		,draw : function( list ){				
			var $proofList = $("#proofList");
			var calcList = 
					list.proofList.filter( function(item){ 						
						//증감 표기
						var prvObj =  list.prevProofList.filter(function(prev){ return prev.AccountCode === item.AccountCode });						
						prvObj.length > 0 && (item.prevAmount = prvObj[0].Amount);
						prvObj.length === 0 && (item.prevAmount = 0);
//						item.inDecreate = item.prevAmount === 0 ? "100" : ( Math.floor( (((item.Amount-item.prevAmount)/item.prevAmount)*100)  ) ) + "=( Math.floor( ((("+item.Amount+"-"+item.prevAmount+")/"+item.prevAmount+")*100)  ) )";
						item.inDecreate = item.prevAmount === 0 ? "" : ( Math.floor( (((item.Amount-item.prevAmount)/item.prevAmount)*100)  ) );
						return 	item.AccountCode !== 'Total'					
					});
			//리스트
			$("<ul>").append(				
				calcList.length > 0 
				?	calcList.map(function(item,idx){
						var $li 	= $("<li>", { "code"  : item.AccountCode });
						var $tit 	= $("<div>",{ "class" : "DetailRank10 height" });
						var $price 	= $("<div>",{ "class" : "Detailaccount10" });
						var $prev 	= $("<div>",{ "class" : "Detailaccount30" });
						var sign	=  item.inDecreate >0  ? "+" : "";
						
						$tit.append( $("<div>",{ "class" : "Gragh_color blue"+( (idx+1) > 5 ? 6 : (idx+1) )}) )
							.append( $("<font>",{ "text" : item.AccountName }) )
							.appendTo( $li );
						
						$price.append( $("<font>",{"text" : mngComm.makeComma( item.Amount )+"원" }) )
							  .appendTo( $li );
						
						item.inDecreate != "" ?
								$prev.append( $("<font>",{"style" :item.inDecreate >0  ? "color:#cb0a2e" : "color:#002bb4","text" : sign+item.inDecreate+"%" }) )
									 .append( $("<div>",{ "class" : "Gragh_per "+(item.inDecreate >0  ? "up":"down") }) )
									 .appendTo( $li )
								:						
								$prev.append( $("<font>")).append( $("<div>")).appendTo( $li)

						/*$prev.append( $("<font>",{"style" : item.inDecreate >0  ? "color:#002bb4" : "color:#cb0a2e","text" : sign+item.inDecreate+"%" }) )
							 .append( $("<div>",{ "class" : "Gragh_per down" }) )
							 .appendTo( $li );*/
						return $li;
					})
				:	$("<div>",{ "class" : "OWList_none", "text" : "조회할 목록이 없습니다." })
			)
			.appendTo( $proofList.empty() );
			
			$proofList.find("li").css("cursor","pointer").on("click",function() { proofObj.clickDetail(this) });
			
			//차트
			reportTopObj.reportChartDraw.call(this,calcList);
			
			$("li",$proofList).slice( 5 ).hide();
			var btnLen = Math.ceil( $("li",$proofList).length / 5 );
			var $fragment = $( document.createDocumentFragment());			
			for( var i =0; i< btnLen; i++) $fragment.append( $("<a>",{ "class" : "Slideroll_btn_"+(i===0 ? "on" : "off") , "href" : "#" }).append("<span>") );
			$("#proofDiv .Slideroll_btn").empty().append( $fragment );
			
		}
		,clickDetail : function( obj ){
			var proofMonth		= this.data.payDate;
			var proofMonthTxt	= proofMonth.substring(0,4) + "<spring:message code='Cache.lbl_year'/>" + " " + proofMonth.substring(4,6) + "<spring:message code='Cache.ACC_lbl_month'/>";
			var deptCode		= this.data.stdCode;
			var costCenterCode	= this.data.deptCode;
			var costCenterTxt	= (costCenterCode == "" ? $("#topReportCate").find("option:selected").text() : $("#proofCategory").find("option:selected").text());
			var accountCode		= $(obj).attr("code");
			var totalAmountTxt	= $(obj).find("div.Detailaccount10").find("font").html();
			
			var popupName	=	"ReportDetailPopup";
			var popupID		=	"reportDetailPopup";
			var openerID	=	"proofObj";
			var popupTit	=	"<spring:message code='Cache.ACC_lbl_reportDetail' />"
									.replace("{0}", proofMonthTxt)
									.replace("{1}", costCenterTxt)
									.replace("{2}", totalAmountTxt); //2020년 12월 리포트 상세내역 - 코비젼 (1,234,567원)
			var url			=	"/account/accountCommon/accountCommonPopup.do?"
							+	"popupID="			+ popupID			+ "&"
							+	"openerID="			+ openerID			+ "&"
							+	"popupName="		+ popupName			+ "&"
							+	"ProofMonth="		+ proofMonth		+ "&"
							+	"CostCenterCode="	+ costCenterCode	+ "&"
							+	"DeptCode="			+ deptCode			+ "&"
							+	"AccountCode="		+ accountCode;
			
			Common.open("",popupID,popupTit,url,"1200px","770px","iframe",true,null,null,true);
		}
	}
	
	var accountObj = {
		data : {}
		,changeCategory : function( code ){			
			this.data.payDate = reportTopObj.data.payDate;
			this.data.accountCode = code;
			this.data.searchType = reportTopObj.data.searchType;
			this.data.stdCode = reportTopObj.data.stdCode;
			this.data.prevPayDate 	= mngComm.getPrevDate( this.data.payDate );			
			this.data.searchType = reportTopObj.data.searchType;			
			mngComm.getData("/account/accountPortal/portalAccount.do",this.data)
				.done( $.proxy( function( data ){  this.draw( data ) } ,this) )
				.fail(  function( e ){  console.log(e) })
		}
		,draw : function( list ){
			var $accountList = $("#accountList");
			var chartColorList = [ "rgba(0,43,180,100)","rgba(1,5,138,100)","rgba(101,203,204,100)","rgba(27,169,234,100)","rgba(179.209,222,100)" ];
			var calcList = 
				list.accountList.filter( function(item){ 					
					var prvObj =  list.prevAccountList.filter(function(prev){ return prev.Code === item.Code });
					prvObj.length > 0 && (item.prevAmount = prvObj[0].Amount);
					prvObj.length === 0 && (item.prevAmount = 0);
					item.inDecreate = item.prevAmount === 0 ? "" : ( Math.floor( (((item.Amount-item.prevAmount)/item.prevAmount)*100)  ) ) ;
					return 	item.Code !== 'Total'					
				});
			
			$("<ul>").append(
				calcList.length > 0 
				?	calcList.map(function(item,idx){					
						var $li 	= $("<li>", { "code"  : item.Code });
						var $tit 	= $("<div>",{ "class" : "DetailRank10 height" });
						var $price 	= $("<div>",{ "class" : "Detailaccount10" });
						var $prev 	= $("<div>",{ "class" : "Detailaccount30" });
						var sign	=  item.inDecreate >0  ? "+" : "";
						
						$tit.append( $("<div>",{ "class" : "Gragh_color blue"+( (idx+1) > 5 ? 6 : (idx+1) ) }) )
							.append( $("<font>",{ "text" : item.Name}) )
							.appendTo( $li );
						
						$price.append( $("<font>",{"text" : mngComm.makeComma( item.Amount )+"원" }) )
							  .appendTo( $li );
						item.inDecreate != "" ?
							$prev.append( $("<font>",{"style" :item.inDecreate >0  ? "color:#cb0a2e" : "color:#002bb4","text" : sign+item.inDecreate+"%" }) )
								 .append( $("<div>",{ "class" : "Gragh_per "+(item.inDecreate >0  ? "up":"down") }) )
								 .appendTo( $li )
							:						
							$prev.append( $("<font>")).append( $("<div>")).appendTo( $li)
						
						return $li;
					})
				:	$("<div>",{ "class" : "OWList_none", "text" : "조회할 목록이 없습니다." })
			)
			.appendTo( $accountList.empty() );
			
			$accountList.find("li").css("cursor","pointer").on("click",function() { accountObj.clickDetail(this) });
			
			//차트
			reportTopObj.reportChartDraw.call(this,calcList);
			
			$("li",$accountList).slice( 5 ).hide();
			var btnLen = Math.ceil( $("li",$accountList).length / 5 );
			var $fragment = $( document.createDocumentFragment());			
			for( var i =0; i< btnLen; i++) $fragment.append( $("<a>",{ "class" : "Slideroll_btn_"+(i===0 ? "on" : "off") , "href" : "#" }).append("<span>") );
			$("#accountDiv .Slideroll_btn").empty().append( $fragment );
		}	
		,clickDetail : function( obj ){
			var proofMonth		= this.data.payDate;
			var proofMonthTxt	= proofMonth.substring(0,4) + "<spring:message code='Cache.lbl_year'/>" + " " + proofMonth.substring(4,6) + "<spring:message code='Cache.ACC_lbl_month'/>";
			var deptCode		= this.data.stdCode;
			var costCenterCode	= $(obj).attr("code");
			var costCenterTxt	= $(obj).find("div.DetailRank10").find("font").html();
			var accountCode		= this.data.accountCode;
			var totalAmountTxt	= $(obj).find("div.Detailaccount10").find("font").html();
			
			var popupName	=	"ReportDetailPopup";
			var popupID		=	"reportDetailPopup";
			var openerID	=	"accountObj";
			var popupTit	=	"<spring:message code='Cache.ACC_lbl_reportDetail' />"
									.replace("{0}", proofMonthTxt)
									.replace("{1}", costCenterTxt)
									.replace("{2}", totalAmountTxt); //2020년 12월 리포트 상세내역 - 코비젼 (1,234,567원)
			var url			=	"/account/accountCommon/accountCommonPopup.do?"
							+	"popupID="			+ popupID			+ "&"
							+	"openerID="			+ openerID			+ "&"
							+	"popupName="		+ popupName			+ "&"
							+	"ProofMonth="		+ proofMonth		+ "&"
							+	"CostCenterCode="	+ costCenterCode	+ "&"
							+	"DeptCode="			+ deptCode			+ "&"
							+	"AccountCode="		+ accountCode;
			
			Common.open("",popupID,popupTit,url,"1200px","770px","iframe",true,null,null,true);
		}
	}
	
	/* 리포트 함수 END */
	
	/* 월별 신청내역 함수 */
	var monthChartObj = {
		data : {}
		,init : function( date ){ 
			this.data.payYear = Number( date.replace(/(\d{4})(\d{2})/,"$1") );			
			this.chart = new Chart( $("#monthChart"),{
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
			                    	return mngComm.makeComma(Math.round(value * 10) / 10)   
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

			                if (tooltipModel.body) {
			                    var title = tooltipModel.title[0];
			                	var body = tooltipModel.body;
			                	var lineIndex = Number( title.replace('월',"") ) - 1;			                				                    
			                	var $arrow_box = $(".arrow_box",tooltipEl);
			                	var $ul = $("<ul>");
			                	var $h1 = $("<h1>",{ "class" : "arrow_titlename" }).append( title );
			                	var monthHeader = 	this._chart._orgData.monthHeader; 
			                	var monthList 	=	this._chart._orgData.monthList;
			                	var cnt = 0;
			                	$ul.append(
		                			monthHeader.reduce( function(acc,cur,idx,arr){
				                		var price = monthList[lineIndex]["SUM_"+cur.Code]; 
				                		if( price === 0 ) return acc;			                		
				                		var $li = $("<li>",{ "class" : (idx+1) > 5 ?  "arrowBtncolor06" : "arrowBtncolor0"+(idx+1) });
				                		var $txt01 = $("<span>",{ "class" : "txt01", "text" : cur.Name });
				                		var $txt02 = $("<span>",{ "class" : "txt02", "text" : mngComm.makeComma( price ) });	
				                		cnt += 1;
				                		return acc = acc.concat( $li.append( $txt01 ).append( $txt02 ).data('item', price ) );				                		
				                	},[]).sort( function(a,b){  return $(b).data('item') - $(a).data('item') })
			                	);
			                	
			                	var $span = $("<span>",{ "text" : mngComm.makeComma(monthList[lineIndex].AmountSum) });
			                	$h1.append( $span).append("원 ( "+cnt+"건 )" ).appendTo( $arrow_box.empty() );
			                	$arrow_box.append( $ul );
			                }

			                var position = this._chart.canvas.getBoundingClientRect();

			                tooltipEl.style.opacity = 1;
			                tooltipEl.style.position = 'absolute';
			                tooltipEl.style.left = (position.left + window.pageXOffset + tooltipModel.caretX - 50 ) + 'px';
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
			var type = $('#topMonthCate option:selected').data('type');	
			this.data.stdCode = $('#topMonthCate').val();
			this.data.searchType = type ? type : '';			
			mngComm.getData("/account/accountPortal/getAccountMonth.do",this.data)
				.done( $.proxy( function( data ){  this.draw( data.chartObj ) } ,this) )
				.fail(  function( e ){  console.log(e) })
		}
		,draw : function( obj ){			
			var chartColorList = [ "rgba(253,207,2,100)","rgba(255,105,101,100)","rgba(101,203,204,100)","rgba(158,112,176,100)","rgba(118,215,116,100)","rgba(118,215,116,200)" ];			
			this.chart.data.datasets = 
					obj.monthHeader.slice(0,6).reduce(function( acc,cur,idx,arr ){
						var rowHeader = {};				
						rowHeader.label					= 	idx < 5 ? 	cur.Name : "기타" ;
						rowHeader.data 					=	idx < 5 ?	obj.monthList.map(function(item,idx){ return item["SUM_"+cur.Code] || 0 })
																	:	obj.monthList.map(function( item,idx ){ return obj.monthHeader.slice(5).reduce(function( acc,cur,idx,arr ){ return acc = acc + item["SUM_"+cur.Code] || 0; },0) });
						rowHeader.backgroundColor 		=	chartColorList[idx]
						rowHeader.hoverBackgroundColor	=	chartColorList[idx]
						rowHeader.borderColor			=	'rgb(255, 255, 255)'
						return acc = acc.concat( rowHeader );
					},[]);
			obj.monthHeader.length > 0 && this.chart.update();			
			obj.monthHeader.length === 0 && this.chart.render();
			this.chart._orgData = obj;
			//타이틀
			$('#monthChartTit .squaregragh_Title').text( this.data.payYear+"년 월별 신청내역" );
		}
		,addEvent : function(){
			$("#monthCal").on('click',function(){				
				event.target.classList.value === 'pre'  && monthChartObj.prevReport();
				event.target.classList.value === 'next' && monthChartObj.nextReport();
			});
		}
	}
	
	/* 월별 신청내역 함수 END */
	
	/* 월별 예산대비 지출 함수 */	
	var budgetChartObj = {
		data : {}
		,init : function( date ){ 
			//var comm = Common.getBaseCode('BUDGET_STD').CacheData[0];			
			this.data.payYear = Number( date.replace(/(\d{4})(\d{2})/,"$1") );
			/* this.data.accountCode	=	comm.Reserved1;
			this.data.sbCode1		=	comm.Reserved2;
			this.data.sbCode2		=	comm.Reserved3; */
			this.chart = new Chart( $("#budgetChart"),{
			    type: 'bar',
			    data: { labels: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] }
			    ,options: {
			    	scales: {
			            yAxes: [{
			                ticks: {
			                    callback: function(value, index, values) {
			                    	return mngComm.makeComma(Math.round(value * 10) / 10)   
			                    }
			                }
			            }]
			        }
			    	,legend: {
			            display: false,			            
			            labels: {			                
		                	boxWidth : 20	
		                	,usePointStyle : true
		                	,padding : 30
			            }
			        }
			        ,tooltips: {
			    	 	enabled: true,	
			    	 	callbacks: {
			                  label: function(tooltipItem, data) {
			                      var value = data.datasets[0].data[tooltipItem.index];
			                      return "<spring:message code='Cache.ACC_lbl_spending'/>:"+mngComm.makeComma(Math.round(value * 10) / 10); 
			                  }
			            } // end callbacks:						            
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
			var type = $('#topBudgetCate option:selected').data('type');	
			this.data.stdCode = $('#topBudgetCate').val();
			this.data.accountCode= $('#budgetCategory').val();
			this.data.searchType = type ? type : '';			
			mngComm.getData("/account/accountPortal/getBudgetMonthSum.do",this.data)
				.done( $.proxy( function( data ){  this.draw( data ) } ,this) )
				.fail(  function( e ){  console.log(e) })
		}
		,draw : function( obj ){
			var budgetAmount = (typeof obj.budgetTotal.BudgetAmount != "undefined") ?  obj.budgetTotal.BudgetAmount : 0;
			var baseTermName = (typeof obj.budgetTotal.BaseTermName != "undefined") ?  obj.budgetTotal.BaseTermName : "";
			var totalObj = obj.chartObj[12];						
			this.chart.data.datasets = [{
				label : '지출'
				,data : obj.chartObj.slice(0,12).map(function( item,idx ){ return item.UsedAmount })
				,backgroundColor	: 	'rgba(0, 43, 180, 100)'
				,borderColor		:	'rgb(0, 43, 180)'
			}]			
			this.chart.update();
			
			//title			
			$("#budgetTitle").empty()				
				.append( this.data.payYear+"년 ");
			 
			$("#baseTermTitle")	.text( "원("+baseTermName +")");
			$("#budgetAmount")	.text( mngComm.makeComma(budgetAmount) );
			$("#UsedAmount")	.text( mngComm.makeComma(totalObj.UsedAmount) );
			$("#processAmount")	.text( mngComm.makeComma(totalObj.pending) );
			$("#leftAmount")	.text( mngComm.makeComma(budgetAmount - totalObj.UsedAmount) );
		}
		,addEvent : function(){			
			$("#budgetCal").on('click',function(){				
				event.target.classList.value === 'pre'  && budgetChartObj.prevReport();
				event.target.classList.value === 'next' && budgetChartObj.nextReport();
			});			
		}
	}
	/* 월별 예산대비 지출 함수 END */
	
	//최근기안팝업
	function onClickPopButton(ProcessID,WorkItemID,TaskID,PerformerID,ProcessDescriptionID,FormInstID,SubKind,UserCode,FormID,BusinessData2, g_mode){
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
	
	
	
	$(document).ready(function(){
		mngComm.objectInit();
	});
	
</script>				
				