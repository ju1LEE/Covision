//퍼블리서 작업분 추가
//필요한 부분만 추가할 것
//2017-10-25 최초 추가

var winW = $(window).width();
var winH = $(window).height();

var mobile_ui_optionSetting = function() {
	
	//다른 페이지 먼저 호출되면 이벤트 바인딩 안됨.
	/*
	$('.menu_link .pg_tit').click(function() {
		if ($(this).parent().hasClass('show')) {
			$(this).parent().removeClass('show')
		} else {
			$(this).parent().addClass('show')
		}
	});
	*/
	
	// 설정 on/off
	$(document).off("click", '.opt_setting').on('click', '.opt_setting', function (e){
		if(!$(this).hasClass('disable')) {
			if($(this).hasClass('on')) {
				$(this).removeClass('on');
			} else {
				$(this).addClass('on');
			}
		}
	});
	
	$(document).on('pagebeforeshow', function (){
		$(document).off("click", '.opt_setting').on('click', '.opt_setting', function (e){
			if(e.handled != true) {		
				e.handled = true;
				
				if(!$(this).hasClass('disable')) {
					if($(this).hasClass('on')) {
						$(this).removeClass('on');
					} else {
						$(this).addClass('on');
					}
				}
			}
		});
	});	
}