<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"  import="egovframework.baseframework.util.SessionHelper,egovframework.baseframework.util.PropertiesUtil"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>
<div class="cRConTop titType">
	<h2 class="title"></h2>	
	
	<div class="searchBox02">
        <span><input type="text" id="searchText"><button type="button" class="btnSearchType01"  id="icoSearch"><spring:message code='Cache.btn_search' /></button></span> <!-- 검색 -->
       
    </div>
	
</div>
<div class="cRContCollabo mScrollVH">
	<!-- 컨텐츠 시작 -->
	
	<div class="temp_cont">
		<div class="main_Titbox main_Titbox02">
			<h2 class="title"><spring:message code='Cache.lbl_mainCategory' /></h2> <!-- 주요카테고리 -->
		</div>
		<div class="temp_cate_list">
			<ul>
				<c:forEach items="${tmlpKind}" var="list" varStatus="status">
					<li class="${list.Reserved1}"><a href="#" data-kind="${list.Code}"><strong>${list.CodeName}</strong></a></li>
				</c:forEach>
			</ul>
		</div>
	
		<div class="main_Titbox">
			<h2 class="title"  id="tmplTitle"><spring:message code='Cache.lbl_Business2' /></h2> <!-- 사업 -->
			<a href="#" class="btnTypeDefault_r middle1 mailico_filter"><spring:message code='Cache.lbl_Filter' /></a> <!-- 필터 -->
		</div>
		<div class="project_manage">
			<ul>
			</ul>
		</div>
	</div>
			<!-- 컨텐츠 끝 -->
</div>
<script>
var collabTmplList = {
		objectInit : function(){		
			this.addEvent();
			this.searchData('BUSINESS');
		}	,
		addEvent : function(){
			//const slider = $(".temp_cate_list ul");
			$(".temp_cate_list ul").slick({
			    slide: 'li',		//슬라이드 되어야 할 태그 ex) div, li
			    infinite : false, 	//무한 반복 옵션
			    slidesToShow : 7,		 //한 화면에 보여질 컨텐츠 개수
			    slidesToScroll : 1,		//스크롤 한번에 움직일 컨텐츠 개수
			    speed : 500,	  //다음 버튼 누르고 다음 화면 뜨는데까지 걸리는 시간(ms)
			    arrows : true, 		 //옆으로 이동하는 화살표 표시 여부
			    dots : false, 		 //스크롤바 아래 점으로 페이지네이션 여부
			    autoplay : false,			 //자동 스크롤 사용 여부
			    autoplaySpeed : 3000, 		 //자동 스크롤 시 다음으로 넘어가는데 걸리는 시간 (ms)
			    pauseOnHover : true,		 //슬라이드 이동	시 마우스 호버하면 슬라이더 멈추게 설정
			    vertical : false,		 //세로 방향 슬라이드 옵션
			    draggable : false, 	//드래그 가능 여부
			    variableWidth: false,
			    centerMode: false,
			    responsive: [ // 반응형 웹 구현 옵션
						{
							breakpoint: 1600, //화면 사이즈 1600px
							settings: {
								//위에 옵션이 디폴트 , 여기에 추가하면 그걸로 변경
								slidesToShow:6
							}
						},
						{
							breakpoint: 1400, //화면 사이즈 1400px
							settings: {
								//위에 옵션이 디폴트 , 여기에 추가하면 그걸로 변경
								slidesToShow:5
							}
						}
					]
			  });
			
			//검색
			$(document).off('click','#icoSearch').on('click','#icoSearch',function(){
				$("#tmplTitle").text("<spring:message code='Cache.lbl_all' />");
				collabTmplList.searchData( $(this).data("kind"), $("#searchText").val());
		    });
			
			//엔터검색
			$(document).off('keypress','#searchText').on('keypress','#searchText',function(e){
				if (e.which == 13) {
					$("#tmplTitle").text("<spring:message code='Cache.lbl_all' />");
					collabTmplList.searchData( $("#icoSearch").data("kind"), $(this).val());
			    }
			});
			  
			$(document).off('click','.pr_like').on('click','.pr_like',function(e){
				e.stopPropagation();
				coviComment.addLikeCount(this, 'CollabTmpl', $(this).data("tmplSeq"));
				$(this).toggleClass("active");
		    });
			$(document).off('click','.temp_cate_list a').on('click','.temp_cate_list a',function(){
				$("#tmplTitle").text($(this).find("strong").text());
				collabTmplList.searchData( $(this).data("kind"));
		    });
			
			$(document).off('click','.btn_card_delete').on('click','.btn_card_delete',function(e){
				e.stopPropagation();
				var aJsonArray = new Array();
				aJsonArray.push({"tmplSeq":$(this).data("tmplSeq")});
				
				Common.Confirm("<spring:message code='Cache.msg_RUDelete' />", "Confirmation Dialog", function (confirmResult) {
					if (confirmResult) {
						$.ajax({
							type:"POST",
							contentType:'application/json; charset=utf-8',
							dataType   : 'json',
							data:JSON.stringify({"dataList" : aJsonArray }),
							url:"/groupware/collabTmpl/deleteTmplRequest.do",
							success:function (data) {
								if(data.result == "ok"){
									Common.Inform("["+data.resultCnt+"<spring:message code='Cache.lbl_DocCount'/>] <spring:message code='Cache.msg_com_processSuccess'/>");	//저장되었습니다.
									collabTmplList.searchData(1);
								}
								else{
									Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />"+data); //	오류가 발생했습니다. 관리자에게 문의바랍니다
								}
							},
							error:function (request,status,error){
								Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />"+"code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error)
							}
						});
					}
				});	
			});	
			
			//템플릿 상세
			$(document).off('click','.project_manage  ul li').on('click','.project_manage  ul li',function(){
				var popupID	= "CollabTmplViewPopup";
		 		var openerID	= "";
		 		var popupYN		= "N";
		 		var popupUrl	= "/groupware/collabTmpl/CollabTmplSavePopup.do?"
		 						+ "&tmplSeq="+$(this).data("tmplSeq")
		 						+ "&tmplName="+encodeURI( $(this).find(".pr_tit").text())
		 						+ "&popupID="	 + popupID;
				Common.open("", popupID, $(this).find(".pr_tit").text(), popupUrl, "1600", "800", "iframe", true, null, null, true);

				
			});
		},
		searchData:function(tmplKind, tmplName){
			if (tmplName == undefined) tmplName = "";

			var params = {"tmplKind" : tmplKind, "tmplName" : tmplName };
			$.ajax({
	    		type:"POST",
	    		data:params,
	    		url:"/groupware/collabTmpl/getTmplList.do",
	    		success:function (data) {
	    			  $(".project_manage ul").empty();
	    			  data.list.map( function( item,idx ){
	    				  $(".project_manage  ul").append( $("<li>",{"class":"icon03"})
	    						  .append($("<strong>",{"class":"pr_tit"}).text(item.TmplName)).data({"tmplSeq":item.TmplSeq})
	    						  .append($("<span>",{"class":"pr_txt"}).text(item.Remark))
	    						  .append($("<div>",{"class":"pr_info02"})
	    								  .append($("<span>",{"class":"pr_manage"}).text(item.TmplKindName))
	    								  .append($("<a>",{"class":"pr_like" + (item.MyLikeCnt>0?" active":"")}).data({"tmplSeq":item.TmplSeq}).append($("<span>",{"name":"likeCnt"}).text(item.LikeCnt)))
	    								  .append($("<a>",{"class":"btn_card_delete"}).data({"tmplSeq":item.TmplSeq}))
	    							)) ;
	    			});
	    		},
	    		error:function (request,status,error){
	    			Common.Error(Common.getDic("msg_ErrorOccurred")+"code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error)
	    		}
	    	});
		}
}

$(document).ready(function(){
	collabTmplList.objectInit();
});

</script>					