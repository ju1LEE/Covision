<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>

<div class="layer_divpop ui-draggable boradPopLayer " id="testpopup_p" style="width:800px;" source="iframe" modallayer="false" layertype="iframe" pproperty="">
		<div class="divpop_contents">
			<div class="popContent layerType02 boardReadingList ">
				<div>
					<div class="top">
						<p>총 0명</p>
					</div>
					<div class="middle">
						<div class="tblList tblCont">
							<div id="historyGrid"></div>
						</div>
					</div>
					<div class="bottom">
					
					</div>
				</div>
			</div>
		</div>
	</div>
<script>

	var historyGrid = new coviGrid();
	var messageID_pop = CFN_GetQueryString("messageID");
	var version_pop = CFN_GetQueryString("version");
	var bizSection_pop = CFN_GetQueryString("bizSection");
	
	$(document).ready(function () {
		setGrid();
	});
	
	function setGrid(){
		setGridHeader();
		setGridConfig();
		setListData();
	}
	
	function setGridHeader(){
		var headerData =[
						{key:'HistoryID', label:' ', width:'2',display:false, hideFilter:'Y'},
		         		{key:'Version', label:'버전', width:'2',align:'center' , sort:'desc'},			//버전
						{key:'ActType', label:'작업 유형',  width:'4', align:'center'},	//작업유형
						{key:'CO_Date', label:'체크아웃' + Common.getSession("UR_TimeZoneDisplay"),  width:'5', align:'center', formatter: function(){
							return CFN_TransLocalTime(this.item.CO_Date);
						}},		//체크아웃
						{key:'CI_Date', label:'체크인' + Common.getSession("UR_TimeZoneDisplay"),  width:'5', align:'center', formatter: function(){
							return CFN_TransLocalTime(this.item.CI_Date);
						}},		//체크인
						{key:'Comment', label:'처리 사유',  width:'7', align:'center'},	//처리 사유
						{key:'ActorName', label:'처리자', width:'5',align:'center'},		//처리자
						{key:'ActorCode', label:' ', display:false, hideFilter : 'Y'}
		];
		historyGrid.setGridHeader(headerData);
	}
	
	function setGridConfig(){
		var configObj = {
				targetID : "historyGrid",
				height:"auto",
				listCountMSG:"<b>{listCount}</b> <spring:message code='Cache.lbl_Count'/>", 
				page : {
					pageNo:1,
					pageSize:10
				},
				paging : true,
				colHead:{},
				body:{}
		}
		
		historyGrid.setGridConfig(configObj);
	}
	
	function setListData(){
		historyGrid.bindGrid({
			ajaxUrl: "/groupware/board/selectCheckInHistoryGridList.do",//조회 컨트롤러
			ajaxPars: {
				messageID: messageID_pop,
				version: version_pop,
				bizSection: bizSection_pop
			},
		});
	} 

	//Grid Index 표시용
	function formatRowNum(pObj){
		return pObj.index+1;
	}
	
	//하단의 닫기 버튼 함수
	function btnClose_Click(){
		Common.Close();
	}

</script>
