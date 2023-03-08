<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<nav class="lnb">
	<h2 class="gnb_left_tit">
		<span class="gnb_tit" onclick="">CoviWebhard</span>
	</h2>
	<ul id="lnb_con" class=lnb_list></ul>
</nav>

<script type="text/javascript">
	//# sourceURL=adminLeft.jsp
	
	(function(param){
		var init = function() {
			var opt = {
				lang: Common.getSession("lang"),
				isPartial: "true"
			};
			
			var leftMenu = new CoviMenu(opt);
			leftData = param.leftData;
			
			if(param.leftData.length){
				leftMenu.render("#lnb_con", param.leftData, "adminLeft");
				
				if (param.loadContent) {
					var $first = $("#lnb_con li").first().find("a");
		      		
					if ($first) $first.click();
				}
	    	}
		}
		
		init();
	})({
		  leftData: ${leftMenuData}
		, loadContent: ${loadContent}
		, domainId: "${domainId}"
		, isAdmin: "${isAdmin}"
	});
	
</script>