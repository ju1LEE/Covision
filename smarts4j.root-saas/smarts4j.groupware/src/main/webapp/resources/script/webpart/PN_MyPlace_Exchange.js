/**
 * pnExchange - [포탈개선] My Place - 환율게시
 */
var pnExchange = {
	webpartType: "",
	clickURL: "",
	attachFileInfoObj: {},
	init: function(data, ext){
		pnExchange.clickURL = ext.clickURL;
		var nowDate = new Date(CFN_GetLocalCurrentDate("yyyy/MM/dd"));
		var nowDateStr = schedule_SetDateFormat(nowDate, ".");
		var nowDay = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
		var nowDayStr = Common.getDic("lbl_sch_" + nowDay[nowDate.getDay()]);
		
		$(".PN_exchangeRate .eDate").text(nowDateStr + " (" + nowDayStr + ")");
		$("#PN_exchange_link").attr("href", pnExchange.clickURL);
		
		pnExchange.setEvent();
		
		var urlList = ext.urlList;
		
		$.each(urlList, function(i, item){
			if(item) pnExchange.getExchangeList(item.URL);
		});
	},
	setEvent: function(){
		$(".PN_exchangeRate").closest(".PN_myContents_box").find(".PN_portlet_btn").off("click").on("click", function(){
			if(!$(this).hasClass("active")){
				$(this).addClass("active");
				$(this).next(".PN_portlet_menu").stop().slideDown(300);
				$(this).children(".PN_portlet_btn > span").addClass("on");
			}else{
				$(this).removeClass("active");
				$(this).next(".PN_portlet_menu").stop().slideUp(300);
				$(this).children(".PN_portlet_btn > span").removeClass("on");
			}
		});
		
		$(".PN_exchangeRate").closest(".PN_myContents_box").find(".PN_portlet_close").click(function(){
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn").removeClass("active");
			$(this).parents(".PN_portlet_menu").stop().slideUp(300);
			$(this).parents(".PN_portlet_function").find(".PN_portlet_btn > span").removeClass("on");
		});
		
		$(".PN_exchangeRate").on("click", function(){
			window.open(pnExchange.clickURL);
		});
	},
	getExchangeList: function(pUrl){
		$.ajax({
			type: "GET",
			url: pUrl,
			success: function(data){
				if(data.length > 0){
					var exchangeInfo = data[0];
					var liObj = $("li[curcode="+exchangeInfo.currencyCode+"]");
					var exClass = "";
					
					if(exchangeInfo.signedChangePrice > 0){
						exClass = "up";
					}else if(exchangeInfo.signedChangePrice < 0){
						exClass = "down";
					}else{
						exClass = "equal";
					}
					
					liObj.find(".eRate").text(exchangeInfo.basePrice.toFixed(2)).addClass(exClass);
				}
			}
		});
	}
}