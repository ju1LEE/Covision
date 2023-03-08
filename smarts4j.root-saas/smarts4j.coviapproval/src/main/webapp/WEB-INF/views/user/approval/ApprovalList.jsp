<%@page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@page import="egovframework.baseframework.util.StringUtil"%>
<%@page import="egovframework.baseframework.util.SessionHelper"%>
<%@page import="egovframework.coviframework.util.XSSUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% String as = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.login.salt")); %>
<% String aI = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.login.iv")); %>
<% String ak = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.login.keysize")); %>
<% int ac = Integer.parseInt(PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.login.iterationCount"))); %>
<% int app = Integer.parseInt(PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.login.passPhrase"))); %>
<%
	String userID 				= SessionHelper.getSession("USERID");
	String useFido			    = PropertiesUtil.getSecurityProperties().getProperty("fido.login.used");
	String cookie 				= "N";
	String listChangeVal 		= "737";
	Cookie[] cookies  			= request.getCookies();
	for(int i = 0; i < cookies.length; i++){
		if(cookies[i].getValue().equals(userID)){
			for(int j= 0; j < cookies.length; j++){
				if(cookies[j].getName().equals("ListViewCookie")){
					cookie = cookies[j].getValue();
				}
				if(cookies[j].getName().equals("ListChangeVal")){
					listChangeVal = cookies[j].getValue();
				}
			}
		}
	}
	
	String mode = request.getParameter("mode") == null ? "Approval" : request.getParameter("mode");
	
	// 이전 검색 조건 세팅
	String schFrmSeGroupID = request.getParameter("schFrmSeGroupID") == null ? "" : request.getParameter("schFrmSeGroupID");
	String schFrmSeGroupWord = request.getParameter("schFrmSeGroupWord") == null ? "" : request.getParameter("schFrmSeGroupWord");
	String schFrmTitleNm = request.getParameter("schFrmTitleNm") == null ? "" : request.getParameter("schFrmTitleNm");
	String schFrmUserNm = request.getParameter("schFrmUserNm") == null ? "" : request.getParameter("schFrmUserNm");
	String schFrmFormSubject = request.getParameter("schFrmFormSubject") == null ? "" : request.getParameter("schFrmFormSubject");
	String schFrmInitiatorName = request.getParameter("schFrmInitiatorName") == null ? "" : request.getParameter("schFrmInitiatorName");
	String schFrmInitiatorUnitName = request.getParameter("schFrmInitiatorUnitName") == null ? "" : request.getParameter("schFrmInitiatorUnitName");
	String schFrmFormName = request.getParameter("schFrmFormName") == null ? "" : request.getParameter("schFrmFormName");
	String schFrmDocNo = request.getParameter("schFrmDocNo") == null ? "" : request.getParameter("schFrmDocNo");
	String schFrmDeputyFromDate = request.getParameter("schFrmDeputyFromDate") == null ? "" : request.getParameter("schFrmDeputyFromDate");
	String schFrmDeputyToDate = request.getParameter("schFrmDeputyToDate") == null ? "" : request.getParameter("schFrmDeputyToDate");
	String schFrmSeSearchID = request.getParameter("schFrmSeSearchID") == null ? "" : request.getParameter("schFrmSeSearchID");
	String schFrmSearchInput = request.getParameter("schFrmSearchInput") == null ? "" : request.getParameter("schFrmSearchInput");
	String schFrmDtlSchBoxSts = request.getParameter("schFrmDtlSchBoxSts") == null ? "" : request.getParameter("schFrmDtlSchBoxSts");
	String schFrmTabId = request.getParameter("schFrmTabId") == null ? "" : request.getParameter("schFrmTabId");
	
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<script type="text/javascript" src="/approval/resources/script/user/ApprovalListCommon.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/approval/resources/script/user/approvestat.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/groupware/resources/script/user/collab_util.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/security/AesUtil.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/security/aes.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/security/pbkdf2.js<%=resourceVersion%>"></script>
<form name="IframeFrom" method="post">
	<input type="hidden" id="ProcessID" 			name="ProcessID" 			value="">
	<input type="hidden" id="WorkItemID" 			name="WorkItemID" 			value="">
	<input type="hidden" id="PerformerID" 			name="PerformerID" 			value="">
	<input type="hidden" id="ProcessDescriptionID" 	name="ProcessDescriptionID" value="">
	<input type="hidden" id="FormTempInstBoxID" 	name="FormTempInstBoxID" 	value="">
	<input type="hidden" id="FormInstID" 			name="FormInstID" 			value="">
	<input type="hidden" id="FormID" 				name="FormID" 				value="">
	<input type="hidden" id="FormInstTableName" 	name="FormInstTableName" 	value="">
	<input type="hidden" id="Mode" 					name="Mode" 				value="">
	<input type="hidden" id="Gloct" 				name="Gloct" 				value="">
	<input type="hidden" id="Subkind" 				name="Subkind" 				value="">
	<input type="hidden" id="Archived" 				name="Archived" 			value="">
	<input type="hidden" id="UserCode" 				name="UserCode" 			value="">
	<input type="hidden" id="Admintype" 			name="Admintype" 			value="">
	<input type="hidden" id="Usisdocmanager" 		name="Usisdocmanager" 		value="true">
	<input type="hidden" id="Listpreview" 			name="Listpreview" 			value="Y">
	<input type="hidden" id="BusinessData1" 				name="BusinessData1" 				value="">
	<input type="hidden" id="BusinessData2" 				name="BusinessData2" 			value="">
	<input type="hidden" id="TaskID" 				name="TaskID" 			value="">
	<input type="hidden" id="RowSeq" 				name="RowSeq" 			value="">
</form>
<div class="cRConTop titType">
	<h2 class="title"></h2>
	<div class="searchBox02">
		<span><input type="text" class="sm" id="searchInput" onkeypress="if (event.keyCode==13){ $('#simpleSearchBtn').click(); return false;}"><button id="simpleSearchBtn"  type="button" onclick="onClickSearchButton(this);" class="btnSearchType01">검색</button></span><a id="detailSchBtn" onclick="DetailDisplay(this);" class="btnDetails"><spring:message code="Cache.lbl_apv_detail"/></a> <!-- 상세 -->
	</div>
</div>
<div class="cRContBottom mScrollVH ">
	<div class="inPerView type02 appSearch" id="DetailSearch">
		<%
			if(mode.equals("Complete")){
		%>
		<div id="DetailSearchType2">
			<div>
				<div class="selectCalView">
					<span><spring:message code='Cache.lbl_apv_subject'/></span>
					<!-- 제목 -->
					<input type="text" id="txtFormSubject" style="width: 236px;" onkeypress="if (event.keyCode==13){ $('#detailSearchBtn').click(); return false;}">
				</div>
				<div class="selectCalView">
					<span><spring:message code='Cache.lbl_apv_writer'/></span>
					<!-- 기안자 -->
					<input type="text" id="txtInitiatorName" onkeypress="if (event.keyCode==13){ $('#detailSearchBtn').click(); return false;}">
				</div>
				<div class="selectCalView">
					<span><spring:message code='Cache.lbl_DraftDept'/></span>
					<!-- 기안부서 -->
					<input type="text" id="txtInitiatorUnitName" onkeypress="if (event.keyCode==13){ $('#detailSearchBtn').click(); return false;}">
				</div>
			</div>
			<div>
				<div class="selectCalView">
					<span><spring:message code='Cache.lbl_Period'/></span>	<!-- 기간 -->
					<div id="divCalendar" class="dateSel type02">
						<input class="adDate" type="text" id="DeputyFromDate" date_separator="-"> - <input id="DeputyToDate" date_separator="-" kind="twindate" date_startTargetID="DeputyFromDate" class="adDate" type="text">
					</div>
				</div>
				<div class="selectCalView">
					<span><spring:message code='Cache.lbl_apv_formname'/></span>
					<!-- 양식명 -->
					<input type="text" id="txtFormName" onkeypress="if (event.keyCode==13){ $('#detailSearchBtn').click(); return false;}">
				</div>
				<div class="selectCalView">
					<span><spring:message code='Cache.lbl_apv_DocNo'/></span>
					<!-- 문서번호 -->
					<input type="text" id="txtDocNo" onkeypress="if (event.keyCode==13){ $('#detailSearchBtn').click(); return false;}">
				</div>
				<a id="detailSearchBtn"  onclick="onClickSearchButton(this)" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code="Cache.btn_search"/></a>
			</div>
		</div>
		<%
			} else {
		%>
		<div id="DetailSearchType1">
			<div>
				<div class="selectCalView">
					<!--<span>* 제목+: 제목+기안자명+기안부서명 검색</span><br/>  todo: 다국어처리 필요 -->
					<span><spring:message code='Cache.lbl_Contents'/></span>	<!-- 내용 -->
					<div class="selBox" style="width: 110px;" id="selectSearchType"></div>
					<input type="text" id="titleNm" style="width: 215px;" onkeypress="if (event.keyCode==13){ $('#detailSearchBtn').click(); return false;}" >
				</div>
			</div>
			<div>
				<div>
					<div class="selectCalView">
						<span><spring:message code='Cache.lbl_Period'/></span>	<!-- 기간 -->
						<div id="divCalendar" class="dateSel type02">
							<input class="adDate" type="text" id="DeputyFromDate" date_separator="-"> - <input id="DeputyToDate" date_separator="-" kind="twindate" date_startTargetID="DeputyFromDate" class="adDate" type="text">
						</div>
					</div>
					<a id="detailSearchBtn"  onclick="onClickSearchButton(this)" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code="Cache.btn_search"/></a>
				</div>
			</div>
		</div>
		<%
			}
		%>
	</div>
	<div class="apprvalContent">
		<div class="boradTopCont apprvalTopCont">
			<div class="pagingType02 buttonStyleBoxLeft">
				<div class="selBox" style="width: 95px;" id="selectGroupType"></div>
				<a id="copyBtn" class="btnTypeDefault" style="display:none" onclick="onClickFolderListPopup();"><spring:message code='Cache.btn_Copy' /></a><!-- 복사 -->
				<a id="chkAllBtn" class="btnTypeDefault" style="display:none" onclick="BatchCheck(ListGrid);"><spring:message code='Cache.btn_CheckAll' /></a><!-- 일괄확인 -->
				<a id="circulationBtn" class="btnTypeDefault" style="display:none" onclick="ListCirculation(ListGrid);"><spring:message code='Cache.btn_apv_blocCirculate' /></a><!-- 일괄회람 -->
				<a id="transferTaskBtn" class="btnTypeDefault" style="display:none" onclick="TransferCollabTask(ListGrid);"><spring:message code='Cache.btn_apv_AddTask' /></a><!-- Task등록 -->
				<a id="batchApvBtn" class="btnTypeDefault" onclick="BatchApproval(ListGrid, 'APPROVAL', '<%=XSSUtils.removeXSSScript(useFido)%>');"><spring:message code='Cache.btn_apv_blocApprove' /></a><!-- 일괄결재 -->
				<a id="serialApvBtn" class="btnTypeDefault" style="display:none;" onclick="SerialApproval('APPROVAL');"><spring:message code='Cache.btn_apv_SerialApprove' /></a><!-- 연속결재 -->
				<a id="saveExlBtn" class="btnTypeDefault btnExcel" style="display:none" onclick="ExcelDownLoad(selectParams, getHeaderDataForExcel(), gridCount);"><spring:message code='Cache.btn_SaveToExcel' /></a> <!-- 엑셀저장 -->
				<a id="handoverBtn" class="btnTypeDefault" style="display:none" onclick="openHandoverPopup(ListGrid);"><spring:message code='Cache.lbl_handover' /></a><!-- 인계 -->
				<a id="delBtn481" class="btnTypeDefault" style="display:none" onclick="DeleteCheck(ListGrid,'Reject');"><spring:message code='Cache.btn_delete' /></a><!-- 삭제 -->
				<a id="delBtn482" class="btnTypeDefault" style="display:none" onclick="DeleteCheck(ListGrid,'TempSave');"><spring:message code='Cache.btn_delete' /></a><!-- 삭제 -->
				<div class="selBox" style="width: 90px;" id="selectPerson"></div>
				<a id="docRead" class="btnTypeDefault" onclick="doDocRead();" style="display:none"><spring:message code='Cache.lbl_apv_ReadCheck' /></a><!-- 읽음확인 -->
				<div class="selBox" style="width: 90px;" id="selectCompany"></div>
			</div>
			<%
			if(mode.equals("Approval") || mode.equals("PreApproval") || mode.equals("Process") || mode.equals("Approval") || mode.equals("Complete") || mode.equals("Reject") || mode.equals("RecExhibit")){
			%>
			<p style = "float: right; padding: 20px 0 0; margin-right: 190px; font-size:12px;">
    	        <img src="/approval/resources/images/Approval/ico_comment.gif" alt="" title="의견" class="ico_btn"><span class="txt_gn11">의견</span> <span style="color: #dbdbdb;">I</span>
              	<img src="/approval/resources/images/Approval/ico_research_join.gif" alt="" title="수정" class="ico_btn"><span class="txt_gn11">수정</span>
        	</p>
        	<%
			}
			%>
			<div class="buttonStyleBoxRight">
				<select id="selectPageSize" class="selectType02 listCount">
					<option value="10">10</option>
					<option value="20">20</option>
					<option value="30">30</option>
				</select>
				<a class="btnListView listViewType01 active" onclick="onClickListView(this);" value="listView" id="listView" ></a>
				<a class="btnListView listViewType03" onclick="onClickListView(this);" value="beforeView" id="beforeView"></a>
				<button class="btnRefresh" onclick="Refresh();"></button><!-- 새로고침 -->
			</div>
		</div>
		<div class="apprvalBottomCont">
			<!-- 상단 고정영역 끝 -->
			<%
				if(cookie.equals("Y")){
			%>
			<div class="searchBox" style='display: none' id="groupLiestDiv">
				<div class="searchInner">
					<ul class="usaBox" id='groupLiestArea'></ul>
				</div>
			</div>
			<div class="appRelBox">
				<div class="contbox" > <!-- 상단 영역 확장시 값 변경 (기본 125px;) -->
					<!-- 컨텐츠 좌측 시작 -->
					<div class="conin_list ovscroll_x">
						<div id="approvalListGrid"></div>
					</div>
					<!-- 컨텐츠 좌측 끝 -->
					<!-- 컨텐츠 우측 시작 -->
					<div class="conin_view" id="conin_view"><!-- 좌우 폭 조정에 따라 값 변경(좌측 width값) -->
						<!-- 좌우폭 조정 Bar시작 -->
						<div class="xbar" id="changeScroll"></div>
						<div id="IframeDiv" style="display: none;">
							<iframe id="Iframe" name="Iframe" frameborder="0" width="100%" height="770px" class="wordLayout" scrolling="no"></iframe>
						</div>
						<div class="rightFixed" id="contDiv"><spring:message code='Cache.msg_approval_clickSubject' /></div>  <!--제목을 클릭해주세요.  -->
					</div>
					<!-- 컨텐츠 우측 끝 -->
				</div>
			</div>
			<%
				}else{
			%>
			<div class="searchBox" style='display: none' id="groupLiestDiv">
				<div class="searchInner">
					<ul class="usaBox" id='groupLiestArea' ></ul>
				</div>
			</div>
			<div class="appRelBox">
				<div class="contbox"> <!-- 상단 영역 확장시 값 변경 (기본 125px;) -->
					<div class="conin_list w100">
						<div id="approvalListGrid"></div>
					</div>
				</div>
			</div>
			<%
				}
			%>
		</div>
	</div>
	<!-- 본문 시작 -->
	<input type="hidden" id="hiddenGroupWord" value="" />
</div>
<form id="schFrm" style="visibility:hidden" action="" method="post">
	<input type="hidden" id="schFrmSeGroupID" name="schFrmSeGroupID" value=""/>
	<input type="hidden" id="schFrmSeGroupWord" name="schFrmSeGroupWord" value=""/>
	<input type="hidden" id="schFrmTitleNm" name="schFrmTitleNm" value=""/>
	<input type="hidden" id="schFrmUserNm" name="schFrmUserNm" value=""/>
	<input type="hidden" id="schFrmFormSubject" name="schFrmFormSubject" value=""/>
	<input type="hidden" id="schFrmInitiatorName" name="schFrmInitiatorName" value=""/>
	<input type="hidden" id="schFrmInitiatorUnitName" name="schFrmInitiatorUnitName" value=""/>
	<input type="hidden" id="schFrmFormName" name="schFrmFormName" value=""/>
	<input type="hidden" id="schFrmDocNo" name="schFrmDocNo" value=""/>
	<input type="hidden" id="schFrmDeputyFromDate" name="schFrmDeputyFromDate" value=""/>
	<input type="hidden" id="schFrmDeputyToDate" name="schFrmDeputyToDate" value=""/>
	<input type="hidden" id="schFrmSeSearchID" name="schFrmSeSearchID" value=""/>
	<input type="hidden" id="schFrmSearchInput" name="schFrmSearchInput" value=""/>
	<input type="hidden" id="schFrmDtlSchBoxSts" name="schFrmDtlSchBoxSts" value=""/>
	<input type="hidden" id="schFrmTabId" name="schFrmTabId" value=""/>
</form>

<script>
	var g_mode = CFN_GetQueryString("mode") == "undefined" ? "Approval" : CFN_GetQueryString("mode");
	var bstored = "false";
	var ListGrid = new coviGrid();		// ListGrid 라는 변수는 각 함에서 동일하게 사용
	var headerData;						// Grid 의 헤더 데이터. 엑셀저장을 위함. JSONObject
	var gridCount = 0;					// gridCount 라는 변수는 각 함에서 동일하게 사용
	var selectParams;					// 그리드 조회 파라미터. 엑셀저장을 위함. JSONObject
	
	var approvalListType = "user";		// 공통 사용 변수 - 결재함 종류 표현 - 개인결재함
	var gloct 	= "";
	var subkind = "";
	var userID  = "";
	var min = 550;
	var max = 1700;
	var right_min = 100;
	var title = "";
	var approvalCnt;
	var processCnt;
	var tcInfoCnt;
	var groupUrl;
	var ProfileImagePath = Common.getBaseConfig('ProfileImagePath').replace("{0}", Common.getSession("DN_Code"));
	var proaas = "<%=as%>";
	var proaaI = "<%=aI%>";
	var proaapp = "<%=app%>";
	var aesUtil = new AesUtil("<%=ak%>", "<%=ac%>");
	
	var prevClickIdx  = -1; ///// 결재 상세정보 표시하기 위해 필요한 플래그. 다른 행을 클릭했는지 동일한 행을 토글하기 위해 클릭했는지 판단할때 사용됨
	
	var schFrmSeGroupID = "<%=schFrmSeGroupID%>";
	var schFrmSeGroupWord = "<%=schFrmSeGroupWord%>";
	var schFrmTitleNm = "<%=schFrmTitleNm%>";
	var schFrmUserNm = "<%=schFrmUserNm%>";
	var schFrmFormSubject = "<%=schFrmFormSubject%>";
	var schFrmInitiatorName = "<%=schFrmInitiatorName%>";
	var schFrmInitiatorUnitName = "<%=schFrmInitiatorUnitName%>";
	var schFrmFormName = "<%=schFrmFormName%>";
	var schFrmDocNo = "<%=schFrmDocNo%>";
	var schFrmDeputyFromDate = "<%=schFrmDeputyFromDate%>";
	var schFrmDeputyToDate = "<%=schFrmDeputyToDate%>";
	var schFrmSeSearchID = "<%=schFrmSeSearchID%>";
	var schFrmSearchInput = "<%=schFrmSearchInput%>";
	var schFrmDtlSchBoxSts = "<%=schFrmDtlSchBoxSts%>";
	var schFrmTabId = "<%=schFrmTabId%>";
	
	var contentHeight = 0;
	
	//일괄 호출 처리
	initApprovalListComm(initApprovalList, setGrid);
	
	function initApprovalList(){
		// 상단 제목 세팅
		var menuStr = "";
		switch (g_mode) {
		case "PreApproval":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_pre2' />";		// 예고함
			break;
		case "Approval":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_approve2' />";		// 미결함
			break;
		case "Process":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_process2' />";		// 진행함
			break;
		case "Complete":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_complete2' />";		// 완료함
			break;
		case "Reject":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_reject2' />";		// 반려함
			break;
		case "TempSave":
			menuStr = "<spring:message code='Cache.lbl_apv_composing2' />";		// 임시함
			break;
		case "TCInfo":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_circulation' />";		// 참조회람함
			break;
		case "RecExhibit":
			menuStr = "<spring:message code='Cache.lbl_apv_doc_recExhibit' />";		// 수신공람함
			break;
		default:
			break;
		}
		$(".title").html(menuStr);
		
		window.onresize = function(event) {
			$(".contbox").css('top', $(".content").height());
		};
		
	<%
		if(!cookie.equals("N")){
	%>
		var cWidth = "<%=XSSUtils.removeXSSScript(listChangeVal)%>";
        $('.conin_list').attr('style', 'width: ' + cWidth + 'px;');
        $('.conin_view').css('left', cWidth+'px');
		$("#beforeView").addClass("active");
		$("#listView").removeClass("active");
	<%
		}else{
	%>
		$("#listView").addClass("active");
		$("#beforeView").removeClass("active");
	<%
		}
	%>
		
		// 각 함에 대한 별도 작업 진행
		setApprovalBox();
		
		setDivision();
		coviInput.setDate();
		setSearchParam();	// 이전 검색조건 세팅
		
		setDocreadCount("USER"); // 좌측메뉴 카운트 조회
	}
	
	// 이전 검색조건 세팅
	function setSearchParam() {
 		if (schFrmSeGroupID != "") {$('#grp_'+schFrmSeGroupID).click();$("#seGroupID").click();}
 		if (schFrmTitleNm != "") {$("#titleNm").val(schFrmTitleNm)}
 		if (schFrmUserNm != "") {$("#userNm").val(schFrmUserNm)}
 		if (schFrmFormSubject != "") {$("#txtFormSubject").val(schFrmFormSubject)}
 		if (schFrmInitiatorName != "") {$("#txtInitiatorName").val(schFrmInitiatorName)}
 		if (schFrmInitiatorUnitName != "") {$("#txtInitiatorUnitName").val(schFrmInitiatorUnitName)}
 		if (schFrmFormName != "") {$("#txtFormName").val(schFrmFormName)}
 		if (schFrmDocNo != "") {$("#txtDocNo").val(schFrmDocNo)}
 		if (schFrmDeputyFromDate != "") {$("#DeputyFromDate").val(schFrmDeputyFromDate)}
 		if (schFrmDeputyToDate != "") {$("#DeputyToDate").val(schFrmDeputyToDate)}
 		if (schFrmSeSearchID != "") {$('#sch_'+schFrmSeSearchID).click();$("#seSearchID").click();}
 		if (schFrmSearchInput != "") {$("#searchInput").val(schFrmSearchInput)}
		// 상세 검색이 열려 있을때
		if (schFrmDtlSchBoxSts == "open") {DetailDisplay($('#detailSchBtn'));}
			
		schFrmSeGroupID = "";
		schFrmSeGroupWord = "";
		schFrmTitleNm = "";
		schFrmUserNm = "";
		schFrmFormSubject = "";
		schFrmInitiatorName = "";
		schFrmInitiatorUnitName = "";
		schFrmFormName = "";
		schFrmDocNo = "";
		schFrmDeputyFromDate = "";
		schFrmDeputyToDate = "";
		schFrmSeSearchID = "";
		schFrmSearchInput = "";
		schFrmDtlSchBoxSts = "";
	}
	
	function setDivision(){
		$(".conin_list").append("<div id='over'></div>");
	    var pan1 = $('.conin_list');
	    var pan2 = $('.conin_view');
	    var bar =  $('#changeScroll');
	    var frame = $("#Iframe"); //뷰 아이프레임 선언
	    var over = $('#over');
	    var curr_width = pan1.width();
	    var unlock = false;
		$(document).mousemove(function (e) {
			//e.preventDefault();
	        if (unlock) {
		        var change = event.pageX - pan1.offset().left;
	        	if (change > min && change < max && event.pageX < $(window).width() - right_min) {
	        		pan1.css('width', change);
	                pan2.css('left', change);
	                //ListGrid.windowResizeApply(); //스크롤바를 리드로우한다.
	                
	        		CFN_SetCookieDay("ListChangeVal", change, null); // 쿠키저장
	        	}
	        }
	    });
		bar.mouseup(function (e) {
		    ListGrid.windowResizeApply();
		});
	    bar.mousedown(function (e) {
	        curr_width = pan1.width();
	        unlock = true;
		over.css("visibility","visible");
		frame.css("pointer-events","none");
	    });
	    $(document).mousedown(function (e) {
	        if (unlock) {
	            e.preventDefault();
	        }
	    });
	    $(document).mouseup(function (e) {
	    	if(unlock) {
	    	    ListGrid.windowResizeApply(); 
	    	 }
	        unlock = false;
			over.css("visibility","hidden");
			frame.css("pointer-events", "");
	    });
	}
	
	//탭 선택(그리드 헤더 변경)
	function setApprovalBox(){
		ListGrid = new coviGrid();
		
		if (CFN_GetCookie("ListViewCookie") == "Y") {						
			var html = "<div id='IframeDiv' style='display: none;'>";
			html += "<iframe id='Iframe' name='Iframe' frameborder='0' width='100%' height='100%' class='wordLayout' scrolling='no'>";
			html += "</iframe></div>";
			$('#IframeDiv').replaceWith(html);
		}
		
		$('#chkAllBtn,#saveExlBtn,#copyBtn,#delBtn481,#delBtn482,#batchApvBtn,#serialApvBtn,#docRead').hide();
		$("#selectPerson").hide();
		
		// 기안자 input 처리
		if (g_mode=="TempSave") {
			$("#userNm").attr("disabled",true);
		} else {
			$("#userNm").attr("disabled",false);
		}
		
		switch (g_mode){
			case "PreApproval" : 
				if(Common.getBaseConfig("isUsechkAll") == "Y") {
					$('#chkAllBtn').show();
				}
				break; // 예고함
			case "Approval" : 
				$('#saveExlBtn,#docRead').show();
				if(Common.getBaseConfig("isUsechkAll") == "Y") {
					$('#chkAllBtn').show();
				}
				if(Common.getBaseConfig("isUsebatchApv") == "Y") {
					$('#batchApvBtn').show();
				}
				if(Common.getBaseConfig("isUseSerial") == "Y") {
					$('#serialApvBtn').show();
				}
				break;    // 미결함
			case "Process" : 
				$('#saveExlBtn').show();
				if(Common.getBaseConfig("isUsechkAll") == "Y") {
					$('#chkAllBtn').show();
				}
				break;		// 진행함
			case "Complete" : 
				$('#copyBtn,#saveExlBtn').show();
				$("#selectPerson").show();
				if(Common.getBaseConfig("isUseCollabApproval") == "Y") {
				    $("#transferTaskBtn").show();
				}
				if(Common.getBaseConfig("isUsechkAll") == "Y") {
					$('#chkAllBtn').show();
				}
				if (Common.getBaseConfig("isUseCircular") == "Y") {
					$('#circulationBtn').show();
				} else {
					$('#circulationBtn').hide();
				}
				break;	// 완료함
			case "Reject" : 
				$('#delBtn481,#saveExlBtn').show();
				if(Common.getBaseConfig("isUsechkAll") == "Y") {
					$('#chkAllBtn').show();
				}
				break;		// 반려함
			case "TempSave" : 
				$('#handoverBtn').show();
				$('#delBtn482').show();
				$("#userNm").attr("disabled",true);
				break;	// 임시함
			case "TCInfo" : 
				$('#copyBtn,#saveExlBtn,#docRead').show();
				$("#selectPerson").show();
				if(Common.getBaseConfig("isUsechkAll") == "Y") {
					$('#chkAllBtn').show();
				}
				break;		// 참조/회람함
		}
		
		groupUrl = "/approval/user/getApprovalGroupListData.do?&mode="+g_mode;
		setSelect(ListGrid, g_mode, groupUrl);				// 공통함수
		
		if($("#selectPerson").is(":visible") == true)
			setSelectPerson();				// 공통함수
		
		//겸직자 법인별 조회 요건 사용 시 처리
		if(Common.getBaseConfig("IsUseAddJobApprovalList") == "Y" && g_mode!="TempSave"){
			setSelectCompnay();
		}else{
			$("#selectCompany").hide();
		}
			
		$("#searchInput").val("");
			
		// 상세검색 초기화
		if($("#detailSchBtn").attr("class") == "opBtn on")
			DetailDisplay($("#detailSchBtn"));
		$("#titleNm").val("");
		$("#userNm").val("");
		$("#txtFormSubject").val("");
		$("#txtInitiatorName").val("");
		$("#txtInitiatorUnitName").val("");
		$("#txtFormName").val("");
		$("#txtDocNo").val("");
		$("#DeputyFromDate").val("");
		$("#DeputyToDate").val("");
		
		$("#hiddenGroupWord").val("");
		$("#groupLiestDiv").css("display","none");
		$(".contbox").css('top', $(".content").height());
		
		setGrid();	//탭선택에 따른 그리드  변경을 위해 setGrid()호출
	}
	
	// 새로고침
	function Refresh(){
		CoviMenu_GetContent(location.href.replace(location.origin, ""),false);
		setDocreadCount("USER");
	}
	
	function setGrid(){
		setGridConfig();
		setApprovalListData();
	}
	//
	function setGridConfig(){
		var notFixedWidth = 3;
		var overflowCell = [];
		//var height = "621px";
		
		if(g_mode == "Approval"){ //미결함 - 읽은 게시글은 style : font-weight를 사용하여 굵게 표시 하도록 분리
			 headerData =[	/// 결재 상세정보 표시 ////
        					  {
        						display: false,
        						key: 'showInfo', label: '' , width: '1', align: 'center'
        					  },
			              	  {key:'chk', label:'chk', width:'25', align:'center', formatter:'checkbox',sort:false,
        						  checked:function(){
			              			var checkedList = strWiid_List.split(";"); 
			              			if(this.item.WorkItemID!="" && checkedList.includes(this.item.WorkItemID)){
			              				return true;
			              			}else{
			              				return false;
			              			}
			              	  	  },
	      						  disabled : function(){
	    							  /* var isChkConfirm = this.item.ExtInfo;
	    							  var returnStr = true;
	    							  
	    							  if(isChkConfirm != "" && isChkConfirm != undefined && (isChkConfirm.UseBlocApprove == "Y" || isChkConfirm.UseBlocCheck  =='Y')  )
	    								  returnStr = false;
	    							  
	    							  return returnStr; */
	    							  
	    							  return false;
	    						 }
        					  },
			                  {key:'InitiatorName', label:'<spring:message code="Cache.lbl_apv_writer"/>', width:'60', align:'center',sort:false,
			                	  formatter: function () {
			                		  return "<div class=\"poRela\"><a class=\"cirPro\" onclick=\'showDetailInfo(\""+this.index+"\");return false;'><img style=\"max-width: 100%; height: auto;\" src="+coviCmn.loadImage(this.item.PhotoPath)+" alt=\"\" onerror='coviCmn.imgError(this, true)'></a><a class=\"opnBt\" onclick=\'showDetailInfo(\""+this.index+"\");return false;'></a></div>";
								   }
			              	  },// 기안자
			                  {key:'FormSubject', label:'<spring:message code="Cache.lbl_apv_subject"/>', width:'396', align:'left',			    // 제목
							   	  formatter:function () {		
							   			var html = "<div class=\"tableTxt\" style=\"width : 95%; text-overflow : ellipsis; white-space :  nowrap; overflow : hidden\";>";
							   			
							   			if(this.item.Priority  == "5"){	html +="<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_surveyUrgency'/></span>";	} //긴급
							   			
							   			if(CFN_GetCookie("ListViewCookie") == "Y"){
							   				html += "<a class=\"taTit\"" + ((this.item.ReadDate != "" && this.item.ReadDate != '0000-00-00 00:00:00')?"":" style=\"font-weight : 900;\"") + " onclick='onClickIframeButton(\""+this.item.ProcessID+"\",\""+this.item.WorkItemID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionID+"\",\""+this.item.FormSubKind+"\",\"\",\"\",\"\",\"\",\""+this.item.UserCode+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
							   			}else{
							   				html += "<a class=\"taTit\"" + ((this.item.ReadDate != "" && this.item.ReadDate != '0000-00-00 00:00:00')?"": " style=\"font-weight : 900;\"") + " onclick='onClickPopButton(\""+this.item.ProcessID+"\",\""+this.item.WorkItemID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionID+"\",\""+this.item.FormSubKind+"\",\"\",\""+this.item.FormInstID+"\",\""+this.item.FormID+"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
							   			}
							   			
							   			if(this.item.IsSecureDoc == "Y"){	html += "<span class=\"security\"><spring:message code='Cache.lbl_apv_AuthoritySetting'/></span>";	} //보안
						   				if(this.item.IsReserved=="Y"){	html +="<span class='btn_emer'><spring:message code='Cache.lbl_apv_hold'/></span>";	} //보류
							   			html += "</div><ui class=\"listinfo\" style = 'overflow : visible;'><li>"+ this.item.SubKind  +" </li><li>"+setUserFlowerName(this.item.InitiatorID, this.item.InitiatorName)+"</li><li>"
										+ this.item.InitiatorUnitName +"</li><li>" + getStringDateToString("yyyy.MM.dd HH:mm:ss",this.item.Created)  +"</li><li>"+ CFN_GetDicInfo(this.item.FormName)  +"</li><li class=\"poRela\"><span class=\"usa\" style=\"display: none;\">유사양식</span></li></ul>";
							   			
							   			return html;
									}
			                  },
			                  {key:'IsFile', label:'<spring:message code="Cache.lbl_File"/>', width:'45', align:'center',sort:false,
			                	  formatter:function () {
			                		  if(this.item.IsFile == "Y"){
			                			  return "<div class=\"fClip\" style=\"float: none;\"><a class=\"mClip\" onclick='openFileList(this,\""+this.item.FormInstID+"\")'><spring:message code='Cache.lbl_attach'/></a></div>"; //첨부
			                		  }
			                	  }
			                  }, // 파일
			                  {key:'IsComment', label:' ',  width:'12', align:'center', sort:false,
			                	  formatter:function () {
			                		  if(this.item.IsComment == "Y"){
			                			  return "<a class = 'iconLayout' onclick='openCommentView(\""+this.item.ProcessID+"\",\""+this.item.FormInstID+"\")' style='cursor: default;'><img src=\"/approval/resources/images/Approval/ico_comment.gif\" alt=\"\"></a>";
			                		  }
			                	  }
			                  }, // 의견
			                  {key:'IsModify', label:' ', width:'12', align:'center', sort:false,
			                	  formatter:function () {
			                		  if(this.item.IsModify == "Y"){
			                			  if(this.item.ExtInfo.UseMultiEditYN == "Y") {
			                				  return "<span class = 'iconLayout'><img src=\"/approval/resources/images/Approval/ico_research_join.gif\" alt=\"\"></span>";
			                			  } else {
			                			 	 return "<a class = 'iconLayout' onclick='openModifyPop(\""+this.item.ProcessID+"\",\""+this.item.WorkItemID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionID+"\",\""+this.item.FormSubKind+"\",\"\",\""+this.item.FormInstID+"\",\"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\")'><img src=\"/approval/resources/images/Approval/ico_research_join.gif\" alt=\"\"></a>";
			                		 	 }
			                	 	 }
			                	  }
			                  }]; // 수정
			                  
			 overflowCell = [4];
		}else if(g_mode == "PreApproval" || g_mode == "Process"){ //예고함, 진행함
			 headerData =[	/// 결재 상세정보 표시하기 위해 추가함 ////
      					  {
      						display: false,
      						key: 'showInfo', label: '' , width: '1', align: 'center'
      					  },
			              	  {key:'chk', label:'chk', width:'25', align:'center', formatter:'checkbox',sort:false},
			                  {key:'InitiatorName', label:'<spring:message code="Cache.lbl_apv_writer"/>', width:'60', align:'center',sort:false,
			                	  formatter: function () {
			                		  return "<div class=\"poRela\"><a class=\"cirPro\" onclick=\'showDetailInfo(\""+this.index+"\");return false;'><img style=\"max-width: 100%; height: auto;\" src="+coviCmn.loadImage(this.item.PhotoPath)+" alt=\"\" onerror='coviCmn.imgError(this, true)'></a><a class=\"opnBt\" onclick=\'showDetailInfo(\""+this.index+"\");return false;'></a></div>";
								   }
			              	  },// 기안자
			                  {key:'FormSubject', label:'<spring:message code="Cache.lbl_apv_subject"/>', width:'396', align:'left',						    // 제목
							   	  formatter:function () {
							   			var html = "<div class=\"tableTxt\" style=\"width : 95%; text-overflow : ellipsis; white-space :  nowrap; overflow : hidden;\">";
							   			
							   			if(this.item.Priority  == "5"){	html +="<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_surveyUrgency'/></span>";	} //긴급
							   			
							   			if(CFN_GetCookie("ListViewCookie") == "Y"){
							   				html += "<a class=\"taTit\" onclick='onClickIframeButton(\""+this.item.ProcessID+"\",\""+this.item.WorkItemID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionID+"\",\""+this.item.FormSubKind+"\",\"\",\"\",\"\",\"\",\""+this.item.UserCode + "\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
							   			}else{
							   				html += "<a class=\"taTit\" onclick='onClickPopButton(\""+this.item.ProcessID+"\",\""+this.item.WorkItemID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionID+"\",\""+this.item.FormSubKind+"\",\"\",\""+this.item.FormInstID+"\",\""+this.item.FormID+"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
							   			}
							   			
							   			if(this.item.IsSecureDoc == "Y"){	html += "<span class=\"security\"><spring:message code='Cache.lbl_apv_AuthoritySetting'/></span>";	} //보안
						   				if(this.item.IsReserved=="Y"){	html +="<span class='btn_emer'><spring:message code='Cache.lbl_apv_hold'/></span>";		} //보류
							   			html += "</div><ui class=\"listinfo\" style = 'overflow : visible;'><li>"+ this.item.SubKind  +" </li><li>"+setUserFlowerName(this.item.InitiatorID, this.item.InitiatorName)+"</li><li>"
							   			+ this.item.InitiatorUnitName +"</li><li>"+ getStringDateToString("yyyy.MM.dd HH:mm:ss",g_mode=="PreApproval"?this.item.Created:this.item.Finished) +"</li><li>"+ CFN_GetDicInfo(this.item.FormName)  +"</li><li class=\"poRela\"><span class=\"usa\" style=\"display: none;\">유사양식</span></li></ul>";		
							   			return html;
									}
			                  },
			                  {key:'IsFile', label:'<spring:message code="Cache.lbl_File"/>', width:'45', align:'center',sort:false,
			                	  formatter:function () {
			                		  if(this.item.IsFile == "Y"){
			                			  //return "<a><img src=\"/approval/resources/images/Approval/data_jpg.gif\" alt=\"\"></a>";
			                			  return "<div class=\"fClip\" style=\"float: none;\"><a class=\"mClip\" onclick='openFileList(this,\""+this.item.FormInstID+"\")'><spring:message code='Cache.lbl_attach'/></a></div>"; //첨부
			                		  }
			                	  }
			                  }, // 파일
			                  {key:'IsComment', label:' ',  width:'12', align:'center', sort:false,
			                	  formatter:function () {
			                		  if(this.item.IsComment == "Y"){
			                			  return "<a class = 'iconLayout' onclick='openCommentView(\""+this.item.ProcessID+"\",\""+this.item.FormInstID+"\")' style='cursor: default;'><img src=\"/approval/resources/images/Approval/ico_comment.gif\" alt=\"\"></a>";
			                		  }
			                	  }
			                  }, // 의견
			                  {key:'IsModify', label:' ', width:'12', align:'center', sort:false,
			                	  formatter:function () {
			                		  if(this.item.IsModify == "Y"){
			                			  if(this.item.ExtInfo.UseMultiEditYN == "Y") {
			                				  return "<span class = 'iconLayout'><img src=\"/approval/resources/images/Approval/ico_research_join.gif\" alt=\"\"></span>";
			                			  } else {
			                			 	 return "<a class = 'iconLayout' onclick='openModifyPop(\""+this.item.ProcessID+"\",\""+this.item.WorkItemID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionID+"\",\""+this.item.FormSubKind+"\",\"\",\""+this.item.FormInstID+"\",\"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\")'><img src=\"/approval/resources/images/Approval/ico_research_join.gif\" alt=\"\"></a>";
			                		 	 }
			                		  }
			                	  }
			                  }]; //수정	
			                  
			 overflowCell = [4];
		}else if(g_mode == "Complete" || g_mode == "Reject" || g_mode == "RecExhibit"){ //완료함 || 반려함 || 수신공람함
			 headerData =[
    					  {
      						display: false,
      						key: "showInfo", label: " " , width: "1", align: "center"
      					  },
			              {key:'chk', label:'chk', width:'20', align:'center', formatter:'checkbox', sort:false},
		                  {key:'SubKind', label:'<spring:message code="Cache.lbl_apv_gubun"/>', width:'45', align:'center', sort:false,
		                	  formatter: function () {
		                		   return "<a onclick=\'showDetailInfo(\""+this.index+"\");return false;'>"+this.item.SubKind+"</a>";
							   }
		                  },								   // 구분
		                  {key:'FormSubject', label:'<spring:message code="Cache.lbl_apv_subject"/>', width:'176', align:'left',						       // 제목
						   	  formatter:function () {
						   			var html = "<div class=\"tableTxt\" style=\"width : 95%; text-overflow : ellipsis; white-space :  nowrap; overflow : hidden;\">";
						   			
						   			//if(this.item.Priority  == "5"){	html += "<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_surveyUrgency'/></span>";	} //긴급
						   			if(CFN_GetCookie("ListViewCookie") == "Y"){
						   				html += "<a class=\"taTit\" onclick='onClickIframeButton(\""+this.item.ProcessArchiveID+"\",\""+this.item.WorkitemArchiveID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionArchiveID+"\",\""+this.item.FormSubKind+"\",\"\",\"\",\"\",\"\",\""+this.item.UserCode+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
						   			}else{
						   				html += "<a class=\"taTit\" onclick='onClickPopButton(\""+this.item.ProcessArchiveID+"\",\""+this.item.WorkitemArchiveID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionArchiveID+"\",\""+this.item.FormSubKind+"\",\"\",\""+this.item.FormInstID+"\",\""+this.item.FormID+"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
						   			}
						   			
						   			if(this.item.IsSecureDoc == "Y"){	html+= "<span class=\"security\"><spring:message code='Cache.lbl_apv_AuthoritySetting'/></span>";	}  //보안
						   			//if(this.item.IsReserved =="Y"){	html += "<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_hold'/></span>";	} //보류
						   			
						   			html += "</div>";
						   			
							   		return html;
								}
		                  },
		                  {key:'InitiatorUnitName',  label:'<spring:message code="Cache.lbl_DraftDept"/>', width:'55', align:'center',
		                	  formatter:function(){return this.item.InitiatorUnitName;}
		                  },				           // 기안부서
		                  {key:'InitiatorName',  label:'<spring:message code="Cache.lbl_apv_writer"/>', width:'55', align:'center',
		                	  formatter:function(){
		                		  return setUserFlowerName(this.item.InitiatorID, this.item.InitiatorName);
		                		  }
		                  },						   // 기안자
		                  {key:'IsFile', label:'<spring:message code="Cache.lbl_File"/>', width:'20', align:'center', sort:false,
		                	  formatter:function () {
		                		  if(this.item.IsFile == "Y"){
		                			  //return "<a href=\"#none\"><img src=\"/approval/resources/images/Approval/data_jpg.gif\" alt=\"\"></a>";
		                			  return "<div class=\"fClip\" style=\"float: none;\"><a class=\"mClip\" onclick='openFileList(this,\""+this.item.FormInstID+"\")'><spring:message code='Cache.lbl_attach'/></a></div>"; //첨부
		                		  }
		                	  }
		                  }, // 파일
		                  {key:'FormName',  label:'<spring:message code="Cache.lbl_FormNm"/>', width:'50', align:'center',
		                	  formatter:function () {
		                		  return CFN_GetDicInfo(this.item.FormName);
	                	  	  }
		                  }, // 양식명
		                  {key:'DocNo',  label:'<spring:message code="Cache.lbl_DocNo"/>', width:'50', align:'center'},									   // 문서번호
		                  {key:'EndDate', label:'<spring:message code="Cache.lbl_RepeateDate"/>' + Common.getSession("UR_TimeZoneDisplay"),  width:'60', align:'center', sort:'desc',
		                	  formatter:function () {
		                			  return getStringDateToString("yyyy.MM.dd HH:mm:ss",this.item.EndDate);
		                	  }
		                  },				   // 일시
		                  {key:'IsComment', label:' ',  width:'12', align:'center', sort:false,
		                	  formatter:function () {
		                		  if(this.item.IsComment == "Y"){
		                			  return "<a class = 'iconLayout' onclick='openCommentView(\""+this.item.ProcessArchiveID+"\",\""+this.item.FormInstID+"\")' style='cursor: default;'><img src=\"/approval/resources/images/Approval/ico_comment.gif\" alt=\"\"></a>";
		                		  }
		                	  }
		                  },							   // 의견
		                  {key:'IsModify', label:' ', width:'12', align:'center', sort:false,
		                	  formatter:function () {
		                		  if(this.item.IsModify == "Y"){
		                			  if(this.item.ExtInfo.UseMultiEditYN == "Y") {
		                				  return "<span class = 'iconLayout'><img src=\"/approval/resources/images/Approval/ico_research_join.gif\" alt=\"\"></span>";
		                			  } else {
		                				  return "<a class = 'iconLayout' onclick='openModifyPop(\""+this.item.ProcessArchiveID+"\",\""+this.item.WorkitemArchiveID+"\",\""+this.item.PerformerID+"\",\""+this.item.ProcessDescriptionArchiveID+"\",\""+this.item.FormSubKind+"\",\"\",\""+this.item.FormInstID+"\",\"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\")'><img src=\"/approval/resources/images/Approval/ico_research_join.gif\" alt=\"\"></a>";
		                			  }
		                		  }
		                	  }
		                  }];								       // 수정
		                  
			 overflowCell = [6];
		}else if(g_mode == "TempSave"){ //임시함
			 headerData =[
   					     {
     						display: false,
     						key: "showInfo", label: " " , width: "1", align: "center"
     					  },
			              {key:'chk', label:'chk', width:'20', align:'center', formatter:'checkbox'},
		                  {key:'SubKind', label:'<spring:message code="Cache.lbl_apv_gubun"/>', width:'45', align:'center' ,sort:false},								    // 구분
		                  {key:'FormName',  label:'<spring:message code="Cache.lbl_FormNm"/>', width:'50', align:'center', sort:false,
		                	  formatter:function () {
		                		  return CFN_GetDicInfo(this.item.FormName);
	                	  	  }
		                  },							            // 양식명
		                  {key:'Subject', label:'<spring:message code="Cache.lbl_apv_subject"/>', width:'300', align:'left',						        // 제목
						   	  formatter:function () {
						   			var html = "<div class=\"tableTxt\" style=\"width : 95%; text-overflow : ellipsis; white-space :  nowrap; overflow : hidden;\">";
						   			
						   			//if(this.item.Priority  == "5"){	html += "<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_surveyUrgency'/></span>";	} //긴급
						   			
						   			if(CFN_GetCookie("ListViewCookie") == "Y"){
						   				html += "<a class=\"taTit\" onclick='onClickIframeButton(\"\",\"\",\"\",\"\",\"\",\""+this.item.FormTempInstBoxID+"\",\""+this.item.FormInstID+"\",\""+this.item.FormID+"\",\""+this.item.FormInstTableName+"\",\""+this.item.UserCode+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\"); return false;'>"+this.item.Subject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
						   			}else{
						   				html += "<a class=\"taTit\" onclick='onClickPopButton(\"\",\"\",\"\",\"\",\"\",\""+this.item.FormTempInstBoxID+"\",\""+this.item.FormInstID+"\",\""+this.item.FormID+"\",\""+this.item.FormInstTableName+"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\"); return false;'>"+this.item.Subject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
						   			}
						   			
						   	 		if(this.item.IsSecureDoc == "Y"){	html+= "<span class=\"security\"><spring:message code='Cache.lbl_apv_AuthoritySetting'/></span>";  } //보안
						   			if(this.item.IsReserved =="Y"){		html += "<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_hold'/></span>"; } //보류
						   			
						   			html += "</div>";
							   		
							   		return html;
								}
		                  },
		                  {key:'CreatedDate', label:'<spring:message code="Cache.lbl_RepeateDate"/>' + Common.getSession("UR_TimeZoneDisplay"),  width:'60', align:'center', sort:'desc',
		                	  formatter:function () {
	                			  return getStringDateToString("yyyy.MM.dd HH:mm:ss",this.item.CreatedDate);
	                	  }
		                  }];			        // 일시
		                  
			 notFixedWidth = 4;
			 //height = "511px";
		}else{ //참조/회람함
			 headerData =[
   					  	  {
     						display: false,
     						key: "showInfo", label: " " , width: "1", align: "center"
     					  },
			              {key:'chk', label:'chk', width:'20', align:'center', formatter:'checkbox'},
		                  {key:'SubKind', label:'<spring:message code="Cache.lbl_apv_gubun"/>', width:'45', align:'center',sort:false,
		                	  formatter: function () {
		                		  return "<a onclick=\'showDetailInfo(\""+this.index+"\");return false;'>"+this.item.SubKind+"</a>";
							   }
		                  },								    // 구분
		                  {key:'FormName',  label:'<spring:message code="Cache.lbl_FormNm"/>', width:'50', align:'center',
		                	  formatter:function () {
	                			  return CFN_GetDicInfo(this.item.FormName);
	                	  		}},							            // 양식명
		                  {key:'FormSubject', label:'<spring:message code="Cache.lbl_apv_subject"/>', width:'150', align:'left',						        // 제목
						   	  formatter:function () {
						   			var html = "<div class=\"tableTxt\" style=\"width : 100%; text-overflow : ellipsis; white-space :  nowrap; overflow : hidden;\">"
						   			
						   			//if(this.item.Priority  == "5"){	html += "<span class=\"btn_emer\"><spring:message code='Cache.lbl_apv_surveyUrgency'/></span>"; } //긴급
						   			
						   			if(CFN_GetCookie("ListViewCookie") == "Y"){
						   				html += "<a class=\"taTit\" "+ ((this.item.ReadDate != "" && this.item.ReadDate != '0000-00-00 00:00:00')?"":" style=\"font-weight : 900;\"") + "onclick='onClickIframeButton(\""+this.item.ProcessID+"\",\"\",\"\",\"\",\"" + this.item.Kind + "\",\"\",\""+this.item.FormInstID+"\",\"\",\"\",\""+this.item.UserCode+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
						   			}else{
						   				html += "<a class=\"taTit\" "+ ((this.item.ReadDate != "" && this.item.ReadDate != '0000-00-00 00:00:00')?"":" style=\"font-weight : 900;\"") +"onclick='onClickPopButton(\""+this.item.ProcessID+"\",\"\",\"\",\"\",\""+this.item.Kind+"\",\"\",\""+this.item.FormInstID+"\",\""+this.item.FormID+"\",\"\",\""+this.item.UserCode+"\",\""+this.item.FormPrefix+"\",\""+this.item.BusinessData1+"\",\""+this.item.BusinessData2+"\",\""+this.item.TaskID+"\",\""+this.item.BusinessData6+"\"); return false;'>"+this.item.FormSubject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</a>";
						   			}
						   			
						   			if(this.item.IsSecureDoc == "Y"){  html += "<span class=\"security\"><spring:message code='Cache.lbl_apv_AuthoritySetting'/></span>"; } //보안
						   			if(this.item.IsReserved =="Y"){  html += "<span class=\"security\"><spring:message code='Cache.lbl_apv_hold'/></span>"; }  //보류
						   			
						   			html += "</div>";
						   			
						   			return html;
								}
		                  },
		                  {key:'InitiatorUnitName',  label:'<spring:message code="Cache.lbl_DraftDept"/>', width:'55', align:'center',
		                	  formatter:function(){return this.item.InitiatorUnitName;}
		                  },				            // 기안부서
		                  {key:'InitiatorName',  label:'<spring:message code="Cache.lbl_apv_writer"/>', width:'55', align:'center',
		                	  formatter:function(){
		                		  return setUserFlowerName(this.item.InitiatorID, this.item.InitiatorName);
		                		  }
		                  },						    // 기안자
		                  {key:'RegDate', label:'<spring:message code="Cache.lbl_RepeateDate"/>' + Common.getSession("UR_TimeZoneDisplay"),  width:'60', align:'center',sort:'desc',
		                	  formatter:function () {
	                			  return getStringDateToString("yyyy.MM.dd HH:mm:ss",this.item.RegDate);
	                	  	   }
		                   }, // 일시(참조 지정일시)
		                  {key:'DocNo',  label:'<spring:message code="Cache.lbl_DocNo"/>', width:'50', align:'center'}]; // 문서번호			        
		                   
			 notFixedWidth = 4;
			 //height = "511px";
		}
		
		ListGrid.setGridHeader(headerData);

		var configObj = {
			targetID : "approvalListGrid",
			height:"auto",
			//height:height,
			listCountMSG:"<b>{listCount}</b> <spring:message code='Cache.lbl_Count'/>",
			 body: {
					/// 결재 상세정보 표시하기 위해 추가함 ////
					marker       :
					{
						display: function () { return this.item.showInfo; },
						rows: [
							[{
								colSeq  : null, colspan: 12, formatter: function () {
									$.Event(event).stopPropagation();
									if(g_mode == "PreApproval" || g_mode == "Approval" || g_mode == "Process" || g_mode == "TCInfo"){ //예고함 || 미결함||진행함 || 참조/회람함
										return "<iframe src='/approval/user/ApprovalDetailList.do?FormInstID=" + this.item.FormInstID + "&ProcessID=" + this.item.ProcessID + "' width=100% height=240px frameborder=0>" + this.item.no + "</iframe>" ;
									}else if(g_mode == "Complete" || g_mode == "Reject"){ //완료함 || 반려함
										return "<iframe src='/approval/user/ApprovalDetailList.do?FormInstID=" + this.item.FormInstID + "&ProcessID=" + this.item.ProcessArchiveID + "' width=100% height=240px frameborder=0>" + this.item.no + "</iframe>" ;
									}
								}, align: "center"

							}]
						]
					}
					///////////////////////////////////////////
             },
			page : {
				pageNo:1,
				pageSize:$("#selectPageSize").val()
			},
			paging : true,
			notFixedWidth : notFixedWidth,
			overflowCell : overflowCell
		};
		
		ListGrid.setGridConfig(configObj);
	}

 	/// 결재 상세정보 표시하기 위해 추가함 ////
    function showDetailInfo(index) {
        $.Event(event).stopPropagation();

		var item = ListGrid.list[index];
		if(prevClickIdx != index){ //현재 그리드에서 펼쳐진 행은 모두 닫는다.
			$.each(
					ListGrid.list, function(idx) {
				if(ListGrid.list[idx].showInfo)
					ListGrid.updateItem(0,0,idx,false);
			});
		}
		if(!item.showInfo){
			ListGrid.updateItem(0,0,index,true);
			prevClickIdx = index;
		}
		else{
			ListGrid.updateItem(0,0,index,false);
		}
		ListGrid.setFocus(index);
		ListGrid.windowResizeApply(); //스크롤바를 리드로우한다.
        /* $('#approvalListGrid_AX_col_AX_2_AX_CB').css('width', '100px');
        $('#approvalListGrid_AX_col_AX_2_AX_CH').css('width', '100px'); */
    }

	function setApprovalListData(){
		if(searchValueValidationCheck()){		// 공통 함수
			setSelectParams(ListGrid);// 공통 함수
			
			ListGrid.bindGrid({
				ajaxUrl:"/approval/user/getApprovalListData.do?&mode="+g_mode,
				ajaxPars: selectParams,
				onLoad: function(){
 					$('.gridBodyTable > tbody > tr > td').css('background', 'white');
 					$('.AXGrid').css('overflow','visible');
 					$('.iconLayout').parent().css('padding',0)
					coviInput.init();
					if(g_mode=='Approval' || g_mode=='PreApproval' || g_mode=='Process'){
						$('.listinfo').parent().css('overflow','visible');
					}else if(g_mode == "Complete" || g_mode == "Reject" || g_mode == "RecExhibit" || g_mode == "TCInfo"){
						$('.btnFlowerName').parent().css('overflow','visible');
					}
					setGridCount();
					if(ListGrid.getCheckedList(1).length<=0){ //일괄 승인시 최상위 체크 박스가 유지되는 문제 해결
						ListGrid.checkedColSeq(1, false);
					}
				}
			});
		}
	}
	
	//
	function openModifyPop(ProcessID,WorkItemID,PerformerID,ProcessDescriptionID,SubKind,FormTempInstBoxID,FormInstID,FormID,FormInstTableName,UserCode,FormPrefix){
		var archived = "false";
		switch (g_mode){
			case "Complete" : mode="COMPLETE"; gloct = "COMPLETE"; subkind=SubKind; archived="true"; userID=UserCode; break;
			case "Reject" : mode="REJECT"; gloct = "REJECT";  subkind=SubKind; archived="true"; userID=UserCode; break;
		}
		CFN_OpenWindow("/approval/goHistoryListPage.do?ProcessID="+ProcessID+"&FormInstID="+FormInstID+"&FormPrefix="+FormPrefix+"&workitemID="+WorkItemID+"&performerID="+PerformerID+"&processdescriptionID="+ProcessDescriptionID+"&userCode="+userID+"&gloct="+gloct+"&formID="+FormID+"&formtempID="+FormTempInstBoxID+"&forminstancetablename="+FormInstTableName+"&admintype=&archived="+archived+"&usisdocmanager=true&listpreview=N&subkind="+subkind+"&mode="+mode+"&type=List", "", 830, (window.screen.height - 100), "resize");
	}
	
	function openCommentView(ProcessID,FormInstID) {
		var archived = "false";
		switch(g_mode) {
			case "Complete": case "Reject": archived = "true"; break;
		}
		CFN_OpenWindow("/approval/goCommentViewPage.do?ProcessID="+ProcessID+"&FormInstID="+FormInstID+"&archived="+archived+"&bstored="+bstored, "", 540, 500, "resize");
	}

	//
	var attachFileInfoObj;
	function openFileList(pObj,FormInstID){
		if(!axf.isEmpty($(pObj).parent().find('.file_box').html())){
			$(pObj).parent().find('.file_box').remove();
			return false;
		}
		$('.file_box').remove();
		var Params = {
				FormInstID : FormInstID
		};
		$.ajax({
			url:"/approval/getCommFileListData.do",
			type:"post",
			data:Params,
			async:false,
			success:function (data) {
				if(data.list.length > 0){
					var vHtml = "<ul class='file_box'>";
						vHtml += "<li class='boxPoint'></li>";
					for(var i=0; i<data.list.length;i++){
						attachFileInfoObj = data.list;
						vHtml += "<li><a onclick='attachFileDownLoadCall("+i+")'>"+data.list[i].FileName+"</a></li>";
					}
					vHtml += "</ul>";
					$(pObj).parent().append(vHtml);
				}
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

	function setGridCount(){
		gridCount = ListGrid.page.listCount;
		//$("#approvalCnt").html(gridCount);
		if (g_mode == "Approval") {
			$("#approvalAllCnt").text(gridCount);
		} else if (g_mode == "Process") {
			$("#processAllCnt").text(gridCount);
		}
	}

	function setSearchGroupWord(id){														// 공통함수에서 호출
		ListGrid.page.pageNo = 1;				// 조회기능 사용시 페이지 초기화
		$("#hiddenGroupWord").val(id);
		schFrmSeGroupWord = id;
		
		// 클릭 시, hover 와 동일한 css 적용되도록 추가
		$("a[id^='fieldName_']").attr("style", "");
		$("a[id='fieldName_" + id + "']").css("background", "#4497dc");
		$("a[id='fieldName_" + id + "']").css("color", "#fff");
		
		setApprovalListData();
	}

	function onClickSearchButton(pObj){
		if($(pObj).attr("class") == "searchImgGry"){
			if($("#seSearchID").attr("value") !="" && $("#searchInput").val() ==""){
				Common.Warning("<spring:message code='Cache.msg_EnterSearchword' />");							//검색어를 입력하세요
				$("#searchInput").focus();
				return false;
			}
		}
		if(pObj.id == "simpleSearchBtn") $("#titleNm").val($("#searchInput").val());
		
		var QSDATE = $("#DeputyFromDate").val();
		var QEDATE = $("#DeputyToDate").val();

		if((QSDATE != "" && QEDATE == "") || (QSDATE == "" && QEDATE != "")){
			Common.Warning("<spring:message code='Cache.msg_apv_periodSearchAllInputDT' />");	// 기간 검색 시 시작일과 종료일을 모두 입력해주십시오.
			return false;
		}

		// 본문검색인 경우 기간 설정 필요
		// 최대 6개월
		if($("#seSearchID").attr("value") == "BodyContextOrg") {
			if(QSDATE == "" || QEDATE == "") {
				Common.Warning("<spring:message code='Cache.msg_apv_chk_date' />");	// 날짜를 입력해주세요.
				return false;
			}
			
	        var chkQEDATE = getCurrentSixMonth($("#DeputyToDate").val());
	        if (QSDATE < chkQEDATE) {
	        	Common.Warning("<spring:message code='Cache.gMessageSixMonth' />");	// 본문검색은 검색기간이 6개월로 제한됩니다
				return false;
			}
		}
        
		var url = "/approval/user/getApprovalGroupListData.do?&mode="+g_mode;
		ListGrid.page.pageNo = 1;				// 조회기능 사용시 페이지 초기화
		setGroupType(ListGrid, url);			// 공통함수. 그룹별 보기 목록 다시 조회함.
		//setApprovalListData();
		
		// 그룹별 보기 이후에 검색하는 경우 다시 선택할 수 있도록 조회결과 표시하지 않도록 수정
		if($("#seGroupID").attr("value") != "all") {
			ListGrid.bindGrid({page:{pageNo:1,pageSize:$("#selectPageSize").val(),pageCount:1},list:[]});
		}
		
		//2012.02.14 그룹바이 검색 - 그룹 선택 후 하단 페이지 목록에는 구체적인 그룹명 선택 시 까지 아무것도 안보이게 처리 시작
		//todo
        //view_list("","","","");
        //ListGrid.
		
		// 검색어 저장
		coviCtrl.insertSearchData([$("#searchInput").val(), $("#titleNm").val()], 'Approval');
	}
	
	function onClickPopButton(ProcessID,WorkItemID,PerformerID,ProcessDescriptionID,SubKind,FormTempInstBoxID,FormInstID,FormID,FormInstTableName,UserCode,FormPrefix,BusinessData1,BusinessData2,TaskID,BusinessData6){
		strPiid_List = "";
		strWiid_List = "";
		strFiid_List = "";
		strPtid_List = "";
		
		var width;
		var archived = "false";
		var rowSeq = ""; // 문서유통 다안기안 내부수신용
		
		switch (g_mode){
			case "PreApproval" : mode="PREAPPROVAL"; gloct = "PREAPPROVAL"; subkind="T010"; userID=UserCode; break;
			case "Approval" : mode="APPROVAL"; gloct = "APPROVAL"; subkind=SubKind; userID=UserCode; break;
			case "Process" : mode="PROCESS"; gloct = "PROCESS"; subkind=SubKind; userID=UserCode; break;
			case "Complete" : mode="COMPLETE"; gloct = "COMPLETE"; subkind=SubKind; archived="true"; userID=UserCode; break;
			case "Reject" : mode="REJECT"; gloct = "REJECT";  subkind=SubKind; archived="true"; userID=UserCode; break;
			case "TempSave" : mode="TEMPSAVE"; gloct = "TEMPSAVE"; subkind="";  userID=""; break;
			case "TCInfo" : mode="COMPLETE"; gloct = "TCINFO"; subkind=SubKind; userID=""; break;
		}
		if(IsWideOpenFormCheck(FormPrefix, FormID) == true){
			width = 1070;
		}else{
			width = 790;
		}
		
		// 문서유통 다안기안용
		rowSeq = ((typeof BusinessData6!="undefined"&&BusinessData6!="undefined")?BusinessData6:"");
		
		CFN_OpenWindow("/approval/approval_Form.do?mode="+mode+"&processID="+ProcessID+"&workitemID="+WorkItemID+"&performerID="+PerformerID+"&processdescriptionID="+ProcessDescriptionID+"&userCode="+userID+"&gloct="+gloct+"&formID="+FormID+"&forminstanceID="+FormInstID+"&formtempID="+FormTempInstBoxID+"&forminstancetablename="+FormInstTableName+"&admintype=&archived="+archived+"&usisdocmanager=true&listpreview=N&subkind="+subkind+""
			+"&ExpAppID="+((typeof BusinessData2!="undefined"&&BusinessData2!="undefined")?BusinessData2:"")+"&taskID="+(typeof TaskID!="undefined"?TaskID:"")+(rowSeq != "" ? "&rowSeq="+rowSeq : ""), "", width, (window.screen.height - 100), "resize");
	}

	// 목록변경
	function onClickListView(pObj){
		CFN_SetCookieDay("CookieUserID", Common.getSession("USERID"), null); // 쿠키저장
		if($(pObj).attr("value") == "listView"){
			CFN_SetCookieDay("ListViewCookie", "N", null); // 쿠키저장
		}else{
			CFN_SetCookieDay("ListViewCookie", "Y", null); //쿠키저장
		}
		
		// 이전 검색 조건 세팅
		// TODO 다른 방식으로 처리 필요
		$('#schFrmSeGroupID').val($('#seGroupID').attr('value'));
		$('#schFrmSeGroupWord').val($("#hiddenGroupWord").val());
		$('#schFrmTitleNm').val($("#titleNm").val());
		$('#schFrmUserNm').val($("#userNm").val());
		$('#schFrmFormSubject').val($("#txtFormSubject").val());
		$('#schFrmInitiatorName').val($("#txtInitiatorName").val());
		$('#schFrmInitiatorUnitName').val($("#txtInitiatorUnitName").val());
		$('#schFrmFormName').val($("#txtFormName").val());
		$('#schFrmDocNo').val($("#txtDocNo").val());
		$('#schFrmDeputyFromDate').val($("#DeputyFromDate").val());
		$('#schFrmDeputyToDate').val($("#DeputyToDate").val());
		$('#schFrmSeSearchID').val($('#seSearchID').attr('value'));
		$('#schFrmSearchInput').val($("#searchInput").val());
		// 상세검색 열림 여부
		if ($('#DetailSearch').css('display') == "none") {
			$('#schFrmDtlSchBoxSts').val("close");
		} else if ($('#DetailSearch').css('display') == "block"){
			$('#schFrmDtlSchBoxSts').val("open");
		}
		$('#schFrmTabId').val($('.AXTab.on').attr('id'));
		$("#schFrm").attr("action", "/approval/layout/approval_ApprovalList.do?CLSYS=approval&CLMD=user&CLBIZ=Approval&mode="+g_mode+"");
		$('#schFrm').submit();
	}

	// 상세검색 열고닫기
	function DetailDisplay(pObj){
		if($("#DetailSearch").hasClass("active")){
			$(pObj).removeClass("active");
			//$('#groupLiestDiv').hide();
			$('#DetailSearch').removeClass("active");
		}else{
			$(pObj).addClass("active");
			$('#DetailSearch').addClass("active");
		}
		
		$(".contbox").css('top', $(".content").height());
		coviInput.setDate();
	}

	function onClickIframeButton(ProcessID,WorkItemID,PerformerID,ProcessDescriptionID,SubKind,FormTempInstBoxID,FormInstID,FormID,FormInstTableName,UserCode,BusinessData1,BusinessData2,TaskID,BusinessData6){
		strPiid_List = "";
		strWiid_List = "";
		strFiid_List = "";
		strPtid_List = "";
		
		var archived = "false";
		
		switch (g_mode){
			case "PreApproval" : mode="PREAPPROVAL"; gloct = "PREAPPROVAL"; subkind="T010"; userID=UserCode; break;
			case "Approval" : mode="APPROVAL"; gloct = "APPROVAL"; subkind=SubKind; userID=UserCode; break;
			case "Process" : mode="PROCESS"; gloct = "PROCESS"; subkind=SubKind; userID=UserCode; break;
			case "Complete" : mode="COMPLETE"; gloct = "COMPLETE"; subkind=SubKind; archived="true"; userID=UserCode; break;
			case "Reject" : mode="REJECT"; gloct = "REJECT";  subkind=SubKind; archived="true"; userID=UserCode; break;
			case "TempSave" : mode="TEMPSAVE"; gloct = "TEMPSAVE"; subkind="";  userID=""; break;
			case "TCInfo" : mode="COMPLETE"; gloct = "TCINFO"; subkind=SubKind; userID=""; break;
		}
		
		/* if(typeof BusinessData1 != "undefined" && BusinessData1 == "ACCOUNT") {
			Common.Warning(Common.getDic("msg_NoSupportForm"));
			return false;
		} */
		
		// 문서유통 다안기안용
		var rowSeq = ((typeof BusinessData6!="undefined"&&BusinessData6!="undefined")?BusinessData6:"");
		
		document.IframeFrom.target = "Iframe";
	  	document.IframeFrom.action = "/approval/user/ApprovalIframeList.do";
	  	//
	  	document.IframeFrom.ProcessID.value = ProcessID;
	  	document.IframeFrom.WorkItemID.value = WorkItemID;
	  	document.IframeFrom.PerformerID.value = PerformerID;
	  	document.IframeFrom.ProcessDescriptionID.value = ProcessDescriptionID;
	  	document.IframeFrom.Subkind.value = SubKind;
	  	document.IframeFrom.FormTempInstBoxID.value = FormTempInstBoxID;
	  	document.IframeFrom.FormInstID.value = FormInstID;
	  	document.IframeFrom.FormID.value = FormID;
	  	document.IframeFrom.FormInstTableName.value = FormInstTableName;
	  	document.IframeFrom.Mode.value = mode;
	  	document.IframeFrom.Gloct.value = gloct;
	  	document.IframeFrom.UserCode.value = userID;
	  	document.IframeFrom.Archived.value = archived;
	  	document.IframeFrom.BusinessData1.value = BusinessData1;
	  	document.IframeFrom.BusinessData2.value = BusinessData2;
	  	document.IframeFrom.TaskID.value = TaskID;
	  	document.IframeFrom.RowSeq.value = rowSeq;
	  	
	  	//
	  	document.IframeFrom.submit();
	  	$("#IframeDiv").show();
	  	$("#contDiv").hide();
	}

	// 읽음 확인
	function doDocRead() {
		var checkApprovalList = ListGrid.getCheckedList(1);
		
		if (checkApprovalList.length == 0) {
			Common.Warning("<spring:message code='Cache.lbl_Mail_NoSelectItem' />");
		} else if (checkApprovalList.length > 0) {
			Common.Confirm("<spring:message code='Cache.msg_Mail_SelectedItemRead' />", "Confirmation Dialog", function (confirmResult) {
				if (confirmResult) {
			    	var paramArr = new Array();
					$(checkApprovalList).each(function(i, v) {
						if (typeof(v.ReadDate) == "undefined" || v.ReadDate == "") {
							var str = v.ProcessID + "|" + v.FormInstID + "|" + v.Kind;
							paramArr.push(str);
						}
					})
					
					if (paramArr.length > 0) {
	 					$.ajax({
							url:"/approval/user/docRead.do",
							type:"post",
							data:{"mode" : g_mode, 
								  "paramArr" : paramArr
							},
							async:false,
							success:function (data) {
								setDocreadCount();
								ListGrid.reloadList(); //Grid만 reload되도록 변경
							},
							error:function(response, status, error){
								CFN_ErrorAjax("/approval/user/docRead.do", response, status, error);
							}
						});
					} else {
						Common.Warning("<spring:message code='Cache.msg_ReadProcessingError' />");
						//TODO reload
					}
				} else {
					return false;
				}
			});
		} else {
			Common.Error("<spring:message code='Cache.msg_ScriptApprovalError' />", "Error");
		}
	}
	//겸직자 회사선택 추가
	function setSelectCompnay(){
		$.ajax({
			type:"POST",
			data:{
			},
			url:"/groupware/privacy/getUserBaseGroupAll.do",
			async:false,
			success : function (data) {
				if(data.status == "SUCCESS"){
					if(data.list != undefined && data.list.length > 0){
						$("#selectCompany").show();
						var compcompanyCode = "";
						var deptHtml = "<span class=\"selTit\" ><a id=\"selCompanyCode\" onclick=\"clickSelectBox(this);\" value=\""+""+"\" class=\"up\">"+Common.getDic("lbl_apv_listbycompany")+"</a></span>"
						deptHtml += "<div class=\"selList\" style=\"width:105px;display:none;\">";
						deptHtml += "<a class=\"listTxt\" value=\""+""+"\" onclick=\"clickCompanySelectBoxList(this);\" id=\""+""+"\">"+Common.getDic("lbl_Whole")+"</a>";
						$(data.list).each(function(index){
							var value = this.CompanyCode;
							if(compcompanyCode != value && deptHtml.indexOf(value) == -1){
								deptHtml += "<a class=\"listTxt\" value=\""+value+"\" onclick=\"clickCompanySelectBoxList(this);\" id=\""+this.CompanyCode+"\">"+this.CompanyName+"</a>";
							}
							compcompanyCode = value;
						});
						deptHtml += "</div>";
						$("#selectCompany").html(deptHtml);
					}else{
						$("#selectCompany").hide();
					}
				}
			},
			error : function(response, status, error){
				CFN_ErrorAjax("/groupware/privacy/getUserBaseGroupAll", response, status, error);
			}
		});		
	}
	/**
	 * 특정 법인별 보기 SelectBox
	 * @param pObj
	 */
	function clickCompanySelectBoxList(pObj){
		var searchGroupType = "";
		var searchGroupWord = "";
		
		clickSelectBox(pObj);
		_func("#groupLiestDiv").hide();
		//$('#DetailSearch').hide();
		_func('#DetailSearch').removeClass("active");
		ListGrid.bindGrid({page:{pageNo:1,pageSize:$("#selectPageSize").val(),pageCount:1},list:[]});
		
		if(_func("#sePersonID").attr("value") == "undefined" || _func("#sePersonID").attr("value") == undefined || _func("#sePersonID").attr("value") == "all"){
			userID = SessionObj["USERID"];
		}else{
			userID = _func("#sePersonID").attr("value");
		}
		
		var approvallistCompanyCode;
		if(_func("#selCompanyCode").attr("value") == "undefined" || _func("#selCompanyCode").attr("value") == undefined || _func("#selCompanyCode").attr("value") == ""){
			approvallistCompanyCode = "";
		}else{
			approvallistCompanyCode = _func("#selCompanyCode").attr("value");
		}		

		selectParams = {
				"searchGroupType":searchGroupType,
				"searchGroupWord":searchGroupWord,
				"userID":userID,
				"mode":g_mode,
				"bstored":bstored,
				"companyCode" : approvallistCompanyCode
			};
		_func("#groupLiestArea").hide();
		_func("#groupLiestArea").empty();
		
		var startDate = "";
		var endDate =   "";
		if(_func("#sePersonID").attr("value") == "undefined" || _func("#sePersonID").attr("value") == undefined || _func("#sePersonID").attr("value") == "all"){
			startDate = "";
			endDate =   "";
		}else{
			startDate = _func("#sePersonID").attr("value").indexOf(";") > -1 ? _func("#sePersonID").attr("value").split(";")[1] : "";
			endDate =   _func("#sePersonID").attr("value").indexOf(";") > -1 ? _func("#sePersonID").attr("value").split(";")[2] : "";

			if(startDate == "0000.00.00")
				startDate = "";
			if(endDate == "0000.00.00")
				endDate = "";
		}

		if(startDate != undefined && startDate != "" && endDate != undefined && endDate != ""){
			$$(selectParams).attr("startDate", startDate);
			$$(selectParams).attr("endDate", endDate);
		}
		
		if(searchValueValidationCheck()){		// 공통 함수
			var url = "/approval/user/getApprovalListData.do";
			
			ListGrid.bindGrid({						//
				ajaxUrl:url,
				ajaxPars: selectParams,
				onLoad: function(){
					$('.gridBodyTable > tbody > tr > td').css('background', 'white');
					$('.AXGrid').css('overflow','visible');
					coviInput.init();
					setGridCount();
					if(ListGrid.getCheckedList(1).length<=0){ //일괄 승인시 최상위 체크 박스가 유지되는 문제 해결
						ListGrid.checkedColSeq(1, false);
					}
				}
			});
		}
	}
	
	// 협업스페이스 업무 등록.
	function TransferCollabTask(grid){
		var checkApprovalList = grid.getCheckedList(1);
		if (checkApprovalList.length == 0) {
			Common.Warning("<spring:message code='Cache.lbl_Mail_NoSelectItem' />");
		} else if (checkApprovalList.length > 0) {
			// 프로젝트 선택
			window.addEventListener( 'message', collabSelectCallback );
	 		collabUtil.openProjectAllocPopup(0, "N", "appCollabSelectCallback");
		}
	}
	
	function collabSelectCallback(ev){
		if( ev.data.functionName != 'appCollabSelectCallback' ) {
            return;
	    }
		window.removeEventListener( 'message', collabSelectCallback );
		
		setTimeout(function(){
			transferCollabTask(ev);
		}, 0);
	}
	
	// 선택한 문서를 협업스페이스 업무로 등록.
	function transferCollabTask(ev){
		var param = ev.data.params;
		var checkApprovalList = ListGrid.getCheckedList(1);
		if(checkApprovalList.length > 0){
	    	var paramArr = [];
			$(checkApprovalList).each(function(i, v) {
				var json = {};
				json.formInstanceId = v.FormInstID;
				json.processId = v.ProcessArchiveID;
				paramArr.push(json);
			});
			
			if (paramArr.length > 0) {
				var _data = Base64.utf8_to_b64(JSON.stringify(paramArr));
				
				var url = "/approval/user/transferCollabLink.do";
				
				var $f = $("#collabLinkIfrm");
				if($f.length == 0){
					$f = $("<form>", {id:"collabLinkIfrm", name:"collabLinkIfrm", action:url, method:"POST", target:"collabLinkIframe"});
					$("body").append($f);
				}

				var params = $.extend({ 
					  "formInstanceIDs" : _data
				}, param);
				
				$f.html("");
				for(key in params){
					try{
						if(key != "nodeName"){// jquery reserved.
							$f.append($("<input/>", {"type":"hidden", "value":""+params[key], "name":""+key}));
						}
					}catch(e){
						console.error(e);
					}
				}
				
				var $ifr = $("#collabLinkIframe");
				if($ifr.length == 0){
					$ifr = $("<iframe>", {id:"collabLinkIframe", name:"collabLinkIframe", style:"border:none;width:0px; height:0px;position:absolute;"});
					$("body").append($ifr);
				}

				var txt = "<span style='font-weight:bold'>" + "Process 1 of "+ (checkApprovalList.length)  +" ...</span>";
				Common.Progress("", null, 0);
				$("p","#popup_message").last().html(txt);
				
				// 문서변환 시간이 소요되므로, 서버에서 jspWriter 에 flush 하여 진행율 표시.
				window.callbackCollabTrans = function(mode, idx){
					if(mode == "progress"){
						if(idx < checkApprovalList.length - 1){
							var txt = "<span style='font-weight:bold'>" + "Process "+(Number(idx) + 2)+" of "+ (checkApprovalList.length)  +" ...</span>";
							$("p","#popup_message").last().html(txt);
						}
					}
					else if(mode == "complete"){
						var resultinfo = arguments[1];
						var status = resultinfo.status;
						var msg = resultinfo.msg;
						var totalCnt = resultinfo.totalCnt;
						var successCnt = resultinfo.successCnt;
						var failCnt = resultinfo.failCnt;
						
						var informMessage = msg;
						informMessage += "<br>";
						informMessage += "Success : " + successCnt + ", Fail : " + failCnt + " ( Total : "+totalCnt+" )";
						
						if(status == "SUCCESS" && failCnt == 0){
							Common.Inform(informMessage);
						}else {
							Common.Warning(informMessage);
						}
						$.ajaxSettings.complete.call(this);
					}
				};
				
				$f.submit();
				
			} else {
				Common.Warning("<spring:message code='Cache.lbl_Mail_NoSelectItem' />");
			}
		}
	}
</script>
