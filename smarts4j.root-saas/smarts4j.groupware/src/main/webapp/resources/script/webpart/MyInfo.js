var wpMyInfo = {
	init: function(){
		wpMyInfo.setMyInfo();
	},
	setMyInfo: function(){
		var sesstionObj = Common.getSession();
		$(".PN_myInfo .pr_name").text(sesstionObj.UR_Name);
		$(".PN_myInfo .pr_name").append('<span>' + sesstionObj.UR_JobPositionName + '</span>');
		$(".PN_myInfo .pr_team").text(sesstionObj.GR_Name);
		$(".PN_myInfo .pr_time").text(sesstionObj.UR_LoginTime);
		$(".PN_myInfo .pr_img").css("background", "url("+coviCmn.loadImage(Common.getSession('PhotoPath'))+")");
	}
}