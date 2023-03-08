/**
 * mainBanner.js - 메인 배너
 */
var mainBanner = {
	init: function(data,ext){
		var $this = this;
		var targetDiv = $("#mainBanner_Container");
		var portalBannerObj = {
							useSlide: "N",
							imageJson : new Array()
						};
		if(data.length>0 && data[0][0].DomainBannerPath != ""){
			targetDiv.empty();
			var portalBannerList = data[0][0].DomainBannerPath.split(";");
			var portalBannerLinkList = data[0][0].DomainBannerLink.split(";");
			var bannerCnt = 0;
			$(portalBannerList).each(function(idx, portalBanner){
				var bannerLink = portalBannerLinkList.length>idx?portalBannerLinkList[idx]:"";
				if (portalBanner != ""){
					bannerCnt++;
					portalBannerObj.imageJson.push({"href": bannerLink, "target": bannerLink==""?"":"_blank", "src": "/covicore/common/banner/" + portalBanner+".do"});
				}	
			});
			if(bannerCnt > 0){
				portalBannerObj.useSlide = "Y";
			}
		}
		else{//defaut 배너 
			if(ext.useSlide != undefined && ext.imageJson != undefined){
				var obj;  
				try{
					if(typeof(ext.imageJson) == "object"){
						obj = ext.imageJson;
					}else if(typeof(ext.imageJson) == "string"){
						obj = JSON.parse(ext.imageJson);
					}else{
						obj = undefined;
					}
				}catch(e){
					obj = undefined;
				}
				if(obj != undefined && obj.length > 0){
					
					if(ext.useSlide == "Y"){
						portalBannerObj.useSlide = "Y";
						$(obj).each(function(idx, data){
							portalBannerObj.imageJson.push({"href": data.href, "target":  data.target, "src": data.src});
						});
					}
				}
			}	
		}
		
		if(portalBannerObj.imageJson.length != 0){
			if(portalBannerObj.useSlide != undefined && portalBannerObj.imageJson != undefined){
				var obj;  
				try{
					if(typeof(portalBannerObj.imageJson) == "object"){
						obj = portalBannerObj.imageJson;
					}else{
						obj = undefined;
					}
				}catch(e){
					obj = undefined;
				}
				
				if(obj != undefined && obj.length > 0){
					targetDiv.empty();
					
					if(portalBannerObj.useSlide == "Y"){
						$(obj).each(function(idx, data){
							targetDiv.append($this.formatHTML(data.href, data.target, data.src));
						});
						
						targetDiv.slick({
							  arrows: true,
							  autoplay: true
						});
					}else if(portalBannerObj.useSlide == "N"){
						targetDiv.append($this.formatHTML(obj[0].href, obj[0].target, obj[0].src));
					}
					
				}
			}
		}else{
			if(ext.useSlide != undefined && ext.imageJson != undefined){
				var obj;  
				try{
					if(typeof(ext.imageJson) == "object"){
						obj = ext.imageJson;
					}else if(typeof(ext.imageJson) == "string"){
						obj = JSON.parse(ext.imageJson);
					}else{
						obj = undefined;
					}
				}catch(e){
					obj = undefined;
				}
				
				if(obj != undefined && obj.length > 0){
					targetDiv.empty();
					
					if(ext.useSlide == "Y"){
						$(obj).each(function(idx, data){
							targetDiv.append($this.formatHTML(data.href, data.target, data.src));
						});
						
						targetDiv.slick({
							  arrows: true,
							  autoplay: true
						});
					}else if(ext.useSlide == "N"){
						targetDiv.append($this.formatHTML(obj[0].href, obj[0].target, obj[0].src));
					}
					
				}
			}
		}
	}, 
	formatHTML: function(href, target, src){
		var strHTML = "";
		
		strHTML += "<div>";
		strHTML += "	<a href='" + ( (href != undefined && href != '') ? href : "javascript:void(0);" ) + "' target='" + ( (target != undefined && target != '') ? target : "_self" )+ "'>";
		strHTML += "		<img src='" + src + "' onerror=\"this.src='/covicore/resources/images/banner.jpg'\">";
		strHTML += "	</a>";
		strHTML += "</div>";
		
		return strHTML;
	},
	getPortalBanner: function(domainID){
		var returnObj = {
							useSlide: "N",
							imageJson : new Array()
						};
		
		$.ajax({
			type: "POST",
			data: {
				"DomainID" : domainID
			},
			async: false,
			url: "/covicore/domain/get.do",
			success: function(data){
				var backStorage = Common.getBaseConfig("BackStorage").replace("{0}", data.list[0].DomainCode);
				var bannerPath = Common.getBaseConfig("PortalBanner_SavePath");
				
				if(data.list[0].DomainBannerPath != ""){
					var portalBannerList = data.list[0].DomainBannerPath.split(";");
					
					if(portalBannerList.length > 1){
						returnObj.useSlide = "Y";
					}
					
					$(portalBannerList).each(function(idx, portalBanner){
						returnObj.imageJson.push({"href": "", "target": "", "src": backStorage + bannerPath + portalBanner});
					});
				}
			},
			error: function(response, status, error){
			     CFN_ErrorAjax("/covicore/domain/get.do", response, status, error);
			}
		});
		
		return returnObj;
	}
}