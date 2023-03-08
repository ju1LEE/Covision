<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv='cache-control' content='no-cache'> 
	<meta http-equiv='expires' content='0'> 
	<meta http-equiv='pragma' content='no-cache'>
	<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>
</head>

<style>
	.pad10 { padding:10px;}
</style>

<body>
	<input id="searchProperty"	type="hidden" />
	<div class="layer_divpop ui-draggable docPopLayer" id="testpopup_p" style="width:auto;" source="iframe" modallayer="false" layertype="iframe" pproperty="">
		<div class="divpop_contents">
			<div class="popContent">
				<div class="middle">
					<div class="eaccountingCont">
						<div id="topitembar02" class="bodysearch_Type01">
							<div class="eaccountingTopCont">
							</div>
							<div class="inPerView type07">
								<div style="width:1100px;">
									<div class="inPerTitbox">
										<span class="bodysearch_tit">
											<!-- 작성일자 -->
											<spring:message code='Cache.ACC_lbl_writeDate'/>
										</span>
										<div id="ddArea" class="dateSel type02">
										</div>
									</div>
									<div id="tiSearchTypePopArea" class="inPerTitbox">
										<span class="bodysearch_tit">
											<!-- 구분 -->
											<spring:message code='Cache.ACC_lbl_division'/>
										</span>
										<span id="tiSearchTypePop" class="selectType02">
										</span>
										<input onkeydown="TaxinvoiceSearchPopup.onenter()" id="searchStr" type="text" placeholder="">
									</div>
									<div id="bodysearchArea"  class="inPerTitbox">
										<span class="bodysearch_tit">
											E-Mail
										</span>
										<input onkeydown="TaxinvoiceSearchPopup.onenter()" id="invoiceeEmail1" type="text" placeholder="">
									</div>
									<a class="btnTypeDefault  btnSearchBlue"	onclick="TaxinvoiceSearchPopup.searchList();"><spring:message code='Cache.ACC_btn_search'/></a>
								</div>
							</div>
						</div>
						<div id="gridArea" class="pad10">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>

<script>
	
	if (!window.TaxinvoiceSearchPopup) {
		window.TaxinvoiceSearchPopup = {};
	}
	
	(function(window) {
		var CompanyCode = CFN_GetQueryString("companyCode") == "undefined" ? "" : CFN_GetQueryString("companyCode");
		
		var TaxinvoiceSearchPopup = {
				params	:{
					gridPanel	: new coviGrid(),
					headerData	: []
				},
				inputParams : {},
				searchParam : {},
				
				pageInit : function(){
					var me = this;
					makeDatepicker("ddArea", "startDD", "endDD");
					setPropertySearchType('TaxInvoice','searchProperty');
					me.setPageViewController();
					
					var param = location.search.substring(1).split('&');
					for(var i = 0; i < param.length; i++){
						var paramKey	= param[i].split('=')[0];
						var paramValue	= param[i].split('=')[1];
						me.inputParams[paramKey] = paramValue
					}
					
					if(me.inputParams.openerID != null
							||me.inputParams.paramsetFunc != null){
						if(parent[me.inputParams.openerID] != null){
							var pa = parent[me.inputParams.openerID];
							if(typeof(pa[me.inputParams.paramsetFunc])=="function"){
								me.searchParam = pa[me.inputParams.paramsetFunc]("TaxBill");
							}
						}
					}
					me.setSelectCombo();
					me.setHeaderData();
					me.searchList();
				},
				
				setPageViewController :function(){
					var searchProperty	= $("#searchProperty").val();
					
					if(searchProperty == "SAP"){
						//SAP가 추가될 경우 이곳에 정의
					}else if(searchProperty == "SOAP"){
						$("#tiSearchTypePopArea").css("display",	"none");
						$("#bodysearchArea").css("display",		"none");
					}else{
						$("#tiSearchTypePopArea").css("display",	"");
						$("#bodysearchArea").css("display",		"");
					}
				},
				
				setSelectCombo : function(){
					accountCtrl.renderAXSelect('tiSearchTypePop',	'tiSearchTypePop',	'ko','','','',"<spring:message code='Cache.ACC_lbl_comboAll' />", CompanyCode);

					var cno = CFN_GetQueryString("cno");
					
					if(cno != null && cno != "" && cno != undefined && cno != "undefined" && $("#searchStr").val() == ""){
						var nowDate = new Date();
						var firstDay = new Date(nowDate.getFullYear(), nowDate.getMonth(), 1);
						var lastDay = new Date(nowDate.getFullYear(), nowDate.getMonth() + 1, 0);
						
						$("#startDD").val($.datepicker.formatDate('yy.mm.dd', firstDay));
						$("#endDD").val($.datepicker.formatDate('yy.mm.dd', lastDay));
						accountCtrl.getComboInfo("tiSearchTypePop").bindSelectSetValue("CERNUM");
						$("#searchStr").val(cno);
					}
				},
				
				setHeaderData : function() {
					var me = this;
					me.params.headerData = [	{	key:'NTSConfirmNum',			label:"<spring:message code='Cache.ACC_lbl_confirmNum' />",			width:'150',		align:'center',				//승인번호
													formatter:function () {
														var rtStr =	""
																	+	"<a onclick='TaxinvoiceSearchPopup.clickTaxinvoiceInfo(\""
																			+ this.item.TaxInvoiceID	+"\"" + ",\"" + this.item.ITEMNAME + "\""
																	+		"); return false;'>"
																	+		"<font color='blue'>"
																	+			this.item.NTSConfirmNum
																	+		"</font>"
																	+	"</a>";
														return rtStr;
													}
												},
												{	key:'FormatWriteDate',			label:"<spring:message code='Cache.ACC_lbl_writeDate' />",			width:'80',		align:'center', sort:'desc'},	//작성일
												{	key:'InvoicerCorpNum',			label:"<spring:message code='Cache.ACC_lbl_BusinessNumber' />",		width:'120',	align:'center'},				//사업자번호
												{	key:'InvoicerCorpName',			label:"<spring:message code='Cache.ACC_lbl_franchiseCorpName' />",	width:'100',	align:'left'},					//가맹점명
												{	key:'FormatTotalAmount',		label:"<spring:message code='Cache.ACC_lbl_totalAmount' />",		width:'100',	align:'right'},					//합계금액
												{	key:'FormatSupplyCostTotal',	label:"<spring:message code='Cache.ACC_lbl_supplyValue' />",		width:'100',	align:'right'},					//공급가액
												{	key:'FormatTaxTotal',			label:"<spring:message code='Cache.ACC_lbl_taxType' />",			width:'100',	align:'right'},					//부가세
												{	key:'TaxInvoiceID',				label:"<spring:message code='Cache.lbl_view' />",					width:'60',		align:'center', sort:false,		//상세보기 팝업
													formatter:function () {
														var rtStr =	""
																	+	"<a class='btn_Bill' onclick='TaxinvoiceSearchPopup.clickTaxinvoicePopup(\""
																			+ this.item.TaxInvoiceID	+"\""
																	+		"); return false;'>"
																	+	"</a>";
														return rtStr;
													}
												}
												
											]
					var gridPanel	= me.params.gridPanel;
					var gridHeader	= me.params.headerData;
					accountCtrl.setViewPageGridHeader(gridHeader,gridPanel);
				},
				
				searchList : function(){
					var me = this;
					
					me.setHeaderData();
					
					var writeDateS		= $("#startDD").val().replaceAll('.','');
					var writeDateE		= $("#endDD").val().replaceAll('.','');
					var tiSearchTypePop	= accountCtrl.getComboInfo("tiSearchTypePop").val();
					var searchStr		= $("#searchStr").val();
					var invoiceeEmail1	= $("#invoiceeEmail1").val();
					var searchProperty	= $("#searchProperty").val();
					
					var gridAreaID	= "gridArea";
					var gridPanel	= me.params.gridPanel;
					var gridHeader	= me.params.headerData;
					var ajaxUrl		= "/account/accountCommon/getTaxinvoiceSearchPopupList.do";
					var ajaxPars	= {	"ExpAppID"			: me.searchParam.ExpAppID
					 				,	"idStr"				: me.searchParam.idStr
					 				,	"writeDateS"		: writeDateS
					 				,	"writeDateE"		: writeDateE
					 				,	"tiSearchTypePop"	: tiSearchTypePop
					 				,	"searchStr"			: searchStr
					 				,	"invoiceeEmail1"	: invoiceeEmail1
					 				,	"searchProperty"	: searchProperty
						 		};
					
					var pageNoInfo		= 1;
					var pageSizeInfo	= 200;
					
					var gridParams = {	"gridAreaID"	: gridAreaID
									,	"gridPanel"		: gridPanel
									,	"gridHeader"	: gridHeader
									,	"ajaxUrl"		: ajaxUrl
									,	"ajaxPars"		: ajaxPars
									,	"pageNoInfo"	: pageNoInfo
									,	"pageSizeInfo"	: pageSizeInfo
									,	"popupYN"		: 'Y'
									, 	"pagingTF"		: false
									,	"height"		: "480px"
					}
					
					accountCtrl.setViewPageBindGrid(gridParams);
					
					accountCtrl.setGridBodyOption(gridPanel, ajaxUrl, ajaxPars);
				},
				
				clickTaxinvoiceInfo : function(key, itemName){
					var me = this;
					var grid		= me.params.gridPanel;
					var list		= grid.list;
					var clickInfo	= {};
					
					for(var i=0; i<list.length; i++){
						var info = list[i];
						if(info.TaxInvoiceID == key){
							clickInfo = info;
							break;
						}
					}
					me.closeLayer();
					
					try{
						var pNameArr = ['clickInfo'];
						eval(accountCtrl.popupCallBackStr(pNameArr));
					}catch (e) {
						console.log(e);
						console.log(CFN_GetQueryString("callBackFunc"));
					}
				},
				
				clickTaxinvoicePopup : function(TaxInvoiceID) {
					var me = this;
					accComm.accTaxBillAppClick(TaxInvoiceID, "openerID=TaxinvoiceSearchPopup&");					
				},
				
				closeLayer : function() {
					var isWindowed	= CFN_GetQueryString("CFN_OpenedWindow");
					var popupID		= CFN_GetQueryString("popupID");
					
					if(isWindowed.toLowerCase() == "true") {
						window.close();
					} else {
						parent.Common.close(popupID);
					}
				},
				
				onenter : function(){
					var me = this;
					if(event.keyCode=="13"){
						me.searchList();
					}
				}
		}
		window.TaxinvoiceSearchPopup = TaxinvoiceSearchPopup;
	})(window);
	
	TaxinvoiceSearchPopup.pageInit();
	
</script>