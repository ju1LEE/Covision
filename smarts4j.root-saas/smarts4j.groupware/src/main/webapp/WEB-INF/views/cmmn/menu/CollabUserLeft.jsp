<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"  import="egovframework.baseframework.util.SessionHelper,egovframework.baseframework.util.PropertiesUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="egovframework.baseframework.util.RedisDataUtil"%>
<%@ page import="egovframework.baseframework.util.StringUtil"%>
<%
	String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
	int tabCnt = Integer.parseInt(StringUtil.replaceNull(RedisDataUtil.getBaseConfig("CollabMaxTab"),"5"));
%>

<style>
.container-fluid { display:none;}
.container-fluid.active { display:inherit; }
.CollabTable { width:100%; table-layout:fixed;}
.CollabTable th {height:33px; border-bottom:1px solid #ACAEB2; font-size:13px; font-family:맑은 고딕, Malgun Gothic,sans-serif, dotum,'돋움',Apple-Gothic;font-color:#5E5E5E}
.CollabTable td{height:40px;border-bottom:1px solid #ededed;}
.rptab_cont { display:none; position:absolute; left:0; right:0; padding:30px 25px 30px 35px; }
.rptab_cont.active { display:inherit; }
.bodyTdText {color:#9a9a9a; text-shadow:none; overflow:hidden; white-space:nowrap; text-overflow:ellipsis;}
.cardBox_area { min-height: 30px;  }



.GanttChart { margin:0 10px; background-color:#fff; -webkit-box-shadow: 3px 3px 6px 0px rgba(0,0,0,0.16); -moz-box-shadow: 3px 3px 6px 0px rgba(0,0,0,0.16); box-shadow: 3px 3px 6px 0px rgba(0,0,0,0.16); }
.gannt-container {
    min-width: 1500px;
    max-width: 1800px;
    margin: 1em auto;
}

.hidden {
    display: none;
}
.layer_alert .txt_red {color:red; font-weight:bold;}
</style>

<div class="cLnbTop">
	<h2><spring:message code='Cache.lbl_Collab' /></h2><!-- 협업스페이스 -->
	<!--  <div><a href="javascript:;" class="btnType01" onclick="board.goWrite();"><spring:message code='Cache.lbl_task_addTask'/></a></div>
	<div class="searchBox02 lnb">
		<span>
			<input type="text" id="txtTotalSearch" class="HtmlCheckXSS ScriptCheckXSS" placeholder="<spring:message code='Cache.lbl_AllSearch'/>">	
			<button type="button" id="btnSearchTotal" class="btnSearchType01"><spring:message code='Cache.btn_search'/></button>	
		</span>
	</div>-->
</div>

<div class='cLnbMiddle mScrollV scrollVType01' style="top:70px;">
	<ul id="collaboMenu" class="contLnbMenu collaboMenu">
		<li class="collaboMenu01" data-menu-target="Current" data-menu-alias="" data-menu-url="">
			<a href="javascript:;" class="non selected" id="list_todo"><spring:message code='Cache.lbl_MyWork'/></a>
			<button onclick="" class="btn_spc_add" id="btnTaskAdd"></button>
		</li>
		<li class="collaboMenu_no" data-menu-target="Current" data-menu-alias="" data-menu-url="">
			<div class="selOnOffBox">
				<a class="non selected"><spring:message code='Cache.lbl_Favorite'/><span></span></a>	<!-- 즐겨찾기 -->
			</div>
			<div class="selOnOffBoxChk type02 boxList" id="favorList"></div>
		</li>
		<li class="collaboMenu02" data-menu-target="Current" data-menu-alias="" data-menu-url="">
			<div class="selOnOffBox">
				<a class="btnOnOff active"><spring:message code='Cache.lbl_TeamWorkBox'/>	<span></span></a>
			</div>
			<div class="selOnOffBoxChk type02 boxList active" id="deptList">
			</div>
		</li>
		<li class="collaboMenu_no" data-menu-target="Current" data-menu-alias="" data-menu-url="">
			<div class="selOnOffBox">
				<a class="non selected"><spring:message code='Cache.lbl_Project'/><span></span></a>
			</div>
			<div class="selOnOffBoxChk type02 boxList active" id="prjListP"></div>
			<button onclick="" class="btn_spc_add" id="btnPrjAdd"></button>
			<div class="column_menu">
                <a href="#" id="prj_tmpl"><spring:message code='Cache.btn_UseTemplate'/></a>
                <a href="#" style="display:none"><spring:message code='Cache.lbl_ExcelUpload'/></a>
                <a href="#" id="prj_add"><spring:message code='Cache.btn_EmptyProject'/></a>
             </div>
		</li>
		<li class="collaboMenu_no" data-menu-target="Current" data-menu-alias="" data-menu-url="" style="display:none">
			<div class="selOnOffBox">
				<a class="non selected"><spring:message code='Cache.lbl_Project'/> [<spring:message code='Cache.lbl_Ready'/>]<span></span></a>
			</div>
			<div class="selOnOffBoxChk type02 boxList" id="prjListW"></div>
		</li>
		<li class="collaboMenu_no" data-menu-target="Current" data-menu-alias="" data-menu-url="" style="display:none">
			<div class="selOnOffBox">
				<a class="non selected"><spring:message code='Cache.lbl_Project'/> [<spring:message code='Cache.lbl_Hold'/>]<span></span></a>
			</div>
			<div class="selOnOffBoxChk type02 boxList" id="prjListH"></div>
		</li>
		<li class="collaboMenu_no" data-menu-target="Current" data-menu-alias="" data-menu-url="" style="display:none">
			<div class="selOnOffBox">
				<a class="non selected"><spring:message code='Cache.lbl_Project'/> [<spring:message code='Cache.lbl_apv_completed'/>]<span></span></a>
			</div>
			<div class="selOnOffBoxChk type02 boxList" id="prjListC"></div>
		</li>
		<li class="collaboMenu_no" data-menu-target="Current" data-menu-alias="" data-menu-url="" style="display:none">
			<div class="selOnOffBox">
				<a class="non selected"><spring:message code='Cache.lbl_Project'/> [<spring:message code='Cache.btn_Cancel'/>]<span></span></a>
			</div>
			<div class="selOnOffBoxChk type02 boxList" id="prjListF"></div>
		</li>
	</ul>
	<ul  id="tabLeftmenu" class="contLnbMenu collaboMenu"></ul>			
	
</div>
<div class="btn_foldArea">
	<a href="#" id="" class="btnOpen" onclick="" style="display: none;"><spring:message code='Cache.lbl_Open'/></a> <!-- 열기 -->
	<a href="#" id="" class="btnClose" onclick="" style="display: block;"><spring:message code='Cache.btn_Close'/></a> <!-- 닫기 -->
</div>

<script type="text/javascript">
	var collabMenu = {
		leftData : '${leftMenuData}',
		loadContent : '${loadContent}',
		domainId : '${domainId}',
		isAdmin : '${isAdmin}',
		$mScrollV : $('.mScrollV'),
		activeTab : '',
		myConf:{"dashThema":"","taskShowCode":""},
		objectInit : function(){			
			//기준날짜 구하기
			this.initLeft();
			 /* 메인 좌측영역 슬라이드 */
			$(".btn_foldArea .btnOpen").click(function(){
			    if($(this).parents().find(".commContLeft").is(":hidden")==false){
			      $('.btn_foldArea .btnOpen').css('display','none');
			      $('.btn_foldArea .btnClose').show();
			      $('.commContLeft').css('left','0');
			      $('.commContRight').css('left','280px');
			    }
			});
			$(".btn_foldArea .btnClose").click(function(){
			    if($(this).parents().find(".commContLeft").is(":hidden")==false){
			      $('.btn_foldArea .btnOpen').css('display','block');
			      $('.btn_foldArea .btnClose').hide();
			      $('.commContLeft').css('left','-280px');
			      $('.commContRight').css('left','0');
			    }
			});
		}	,
		initLeft : function(){
		 	var opt = {
		 			lang : "ko",
		 			isPartial : "true"
		 	};
		 	var leftData = ${leftMenuData};
		 	var coviMenu = new CoviMenu(opt);
		 	
		 	if(leftData.length != 0){
		 		coviMenu.render('#tabLeftmenu', leftData, 'userCustomLeft');
		 	}

			$("#tabLeftmenu li a").on( 'click', function(e){
				var $a = $(this);
				var $li = $a.parent();
				var target = $li.attr('data-menu-target');
				
				//선택된 리스트항복 selected
				$('.non').removeClass('selected');
				$('.sub').removeClass('selected');
				$a.addClass('selected');
				
				if ($li.attr('data-menu-url') == undefined) return;
				var url = decodeURIComponent($li.attr('data-menu-url'));
				$a.attr("id","list_"+"m_"+$li.attr('data-menu-id').trim());
				collabMenu.goTab(this, "m_"+$li.attr('data-menu-id').trim(), $a.text(), "", url,"");
			});

		 	this.getUserMenu();
			this.addEvent();
			
			/* initLeft는 처음 한번만 실행이 되기 때문에 이 때, 각 메뉴의 load 값을 N으로 초기화해준다. */
			$("#collaboMenu").find('#list_todo').attr('load', 'N'); // 내 업무
			$("#collaboMenu").find('.sub').attr('load', 'N'); // 프로젝트 리스트
			
			coviCtrl.bindmScrollV($('.mScrollV'));
	 		$("#tab").load("/groupware/layout/multiTab.do");
		 	$("#tab").show();
		 	
		 	/*home탭 클릭*/
			$("#fixTab").on( 'click', function(e){
				$("#content .cRContBottom").hide();
				$("#multiTabListDiv .l-contents-tabs__item").removeClass("l-contents-tabs__item--active");
				
				$(".l-contents-tabs__fixed-tab").addClass("l-contents-tabs__fixed-tab--active");
				$$("#content .cRContBottom:eq(0)").show();
			});
		 	
		 	if(this.loadContent == 'true'){
		 		CoviMenu_GetContent('/groupware/layout/collab_CollabPortal.do?CLSYS=collab&CLMD=user&CLBIZ=Collab');
		   		//g_lastURL = '/groupware/layout/collab_CollabPortal.do?CLSYS=collab&CLMD=user&CLBIZ=Collab';
		  	}
		},
		goTab: function(obj, objId, objTitle, objType,  sUrl, aData){
			if ($(obj).attr("load") != "Y" && $("#multiTabListDiv .l-contents-tabs__item").length > <%=tabCnt%>){
				Common.Error("<spring:message code='Cache.CPMail_mail_msgNotOpenTab' />");
				return ;
			}
			collabMenu.activeTab = objId;
			$("#content .cRContBottom").hide();
			$("#multiTabListDiv .l-contents-tabs__item").removeClass("l-contents-tabs__item--active");
			$(".l-contents-tabs__fixed-tab").removeClass("l-contents-tabs__fixed-tab--active");
			
			if ($(obj).attr("load") == "Y"){
				$("#multiTabListDiv #tab_"+objId).addClass("l-contents-tabs__item--active");
				$("#content #"+objId).show();
				if (aData.tagType == "TAG"){
					collabMain.getTagTask($("#" + objId + " #totnum"),"TAG",aData.tagVal);
				}				
			}
			else{
				var tabHtml = $('<div />', {"class": "l-contents-tabs__item l-contents-tabs__item--active", id:"tab_"+objId, "data-map":objId})
					.append($("<div>",{ "class" : "l-contents-tabs__title", "text" : objTitle}))
					.append($("<a>",{ "class" : "i-l-contents-tabs__delete"}).append($("<i>",{ "class" : "i-cancle"}))
					);
				
				$("#multiTabListDiv").append(tabHtml);
				$("#content").append("<div class='cRContBottom cRContCollabo_sub mScrollVH' id='"+objId+"'></div>");
				$(".btn_foldArea .btnOpen").trigger("click");

				if (objType != ""){
					$("#content #"+objId).load(sUrl, function(e) {
						collabMain.objectInit(objId, aData);
					});
				}	
				else{
					$("#content #"+objId).load(sUrl, function(e) {
					});
				}
/*				if (objType == "MAIN"){
					$(".btn_foldArea .btnOpen").trigger("click");
					$("#content #"+objId).load(sUrl, function(e) {
						collabMain.objectInit(objId, aData);
					});
				}	
				else{
					$("#content #"+objId).load(sUrl, function(e) {
						collabTodo.objectInit();
					});
				}	
*/				
				/*탭클릭*/
				$("#tab_"+objId).on( 'click', function(e){
					$("#content .cRContBottom").hide();
					$("#multiTabListDiv .l-contents-tabs__item").removeClass("l-contents-tabs__item--active");
					$(this).addClass("l-contents-tabs__item--active");
					$("#content #"+$(this).attr("data-map")).show();
				});	
				
				/*탭삭제*/
				$("#tab_"+objId+" .i-l-contents-tabs__delete").on('click', function(){
					var objId = $(this).parent().attr("data-map");
					$("#content #"+objId).remove();
					$(this).parent().remove();
					$("#list_"+objId).attr("load","N");
					$("#fixTab").click();
				});
				
				
			}	
			$(obj).attr("load", 'Y');
		},
		getUserMenu : function(){
			$.ajax({
				url:"/groupware/collab/getUserMenu.do",
				type:"POST",
				async:false,
				success:function (data) {
					$("#favorList").children().html("");
					$("#deptList").children().html("");
					$("#prjListP").children().html("");
					$("#prjListW").children().html("");
					$("#prjListH").children().html("");
					$("#prjListC").children().html("");
					$("#prjListF").children().html("");
					
					if(data.deptList.length > 0){
						$(data.deptList).each(function(i,v){
							var dataMap = {"prjSeq": v["GroupID"], "prjType": "T"+v["ExecYear"], "prjName":v["DeptName"]};
							$("#deptList").append("<div><a class='sub' id='list_T"+v["ExecYear"]+"_"+v["GroupID"]+"' data-map='"+JSON.stringify(dataMap)+"'  data-type='D'><span>"+v.DeptName+"</span></a></div>");
				    	});
					}
					
					if(data.prjList.length > 0){
						//즐겨찾기 메뉴
						$(data.prjList).each(function(i,v){
							var dataMap = {"prjSeq": v["PrjSeq"], "prjType": "P", "prjName":XFN_ReplaceAllChars(v["PrjName"],"\'","")};

							if(v["IsFav"] == 1)
								$("#favorList").append("<div><a class='sub'  id='list_F_"+v["PrjSeq"]+"' data-map='"+JSON.stringify(dataMap)+"' data-type='P'>"+
									(v.PrjStatus=="C"?"<span class='btn_spc_end'><spring:message code='Cache.lbl_Completed' /></span>":"")+		//완료
									"<span "+(v.PrjStatus=="C"?"class='txt_line'":"")+">"+v.PrjName+"</span></a></div>");
				    	});
					}
					
					//프로젝트 메뉴
					if (Common.getBaseConfig("isUseCollabStatMenu") == "Y"){	//진행상태메뉴 미사용
						$("#prjListW").parent().show();
						$("#prjListH").parent().show();
						$("#prjListC").parent().show();
						$("#prjListF").parent().show();
						
						$(data.prjList).each(function(i,v){
							var dataMap = {"prjSeq": v["PrjSeq"], "prjType": "P", "prjName":XFN_ReplaceAllChars(v["PrjName"],"\'","")};

							$("#prjList"+v.PrjStatus).append("<div><a class='sub'  id='list_P_"+v["PrjSeq"]+"' data-map='"+JSON.stringify(dataMap)+"'  data-type='P'>"+
									(v.PrjStatus=="C"?"<span class='btn_spc_end'><spring:message code='Cache.lbl_Completed' /></span>":"")+		//완료
									"<span "+(v.PrjStatus=="C"?"class='txt_line'":"")+">"+v.PrjName+"</span></a></div>");
				    	});
					}else{	//진행상태메뉴 미사용	
						$("#prjListW").parent().hide();
						$("#prjListH").parent().hide();
						$("#prjListC").parent().hide();
						$("#prjListF").parent().hide();
						
						$(data.prjList).each(function(i,v){
							var dataMap = {"prjSeq": v["PrjSeq"], "prjType": "P", "prjName":XFN_ReplaceAllChars(v["PrjName"],"\'","")};

							if(v.PrjStatus == "P")
								$("#prjList"+v.PrjStatus).append("<div><a class='sub'  id='list_P_"+v["PrjSeq"]+"' data-map='"+JSON.stringify(dataMap)+"'  data-type='P'>"+
									(v.PrjStatus=="C"?"<span class='btn_spc_end'><spring:message code='Cache.lbl_Completed' /></span>":"")+		//완료
									"<span "+(v.PrjStatus=="C"?"class='txt_line'":"")+">"+v.PrjName+"</span></a></div>");
				    	});
					}
					
					//활성화된 메뉴 체크
					$("#multiTabListDiv .l-contents-tabs__item").each(function(i,v){
						$("#list_"+$(this).data("map")).attr("load", "Y");
					});
					
					var menuList = ["favorList","prjListP","prjListW","prjListH","prjListC","prjListF"];
					for (var i=0; i < menuList.length; i++){
						if ($("#"+menuList[i]+" div").length > 0){
							$("#"+menuList[i]).closest("li").removeClass("collaboMenu_no").addClass("collaboMenu03");
							$("#"+menuList[i]).prev().find("a").removeClass("non selected").addClass("btnOnOff");
						}
					}	
					//진행 프로젝트는 열어놓기
					if ($("#prjListP div").length > 0) $("#prjListP").prev().find("a").addClass("active");
				},
				error:function (error){
					CFN_ErrorAjax(url, response, status, error);
				}
			});

		},
		addEvent: function(){
			$('#btnSearchTotal').on('click', function(){
				if (!XFN_ValidationCheckOnlyXSS()) { return; }
				
				var url = String.format('/groupware/layout/board_BoardList.do?CLSYS=board&CLMD=user&CLBIZ=Board&boardType=Total&searchText={0}&searchType={1}', encodeURIComponent($('#txtTotalSearch').val()), "Total");
				CoviMenu_GetContent(url);
				$('#txtTotalSearch').val("");
			});
			$('#list_todo').on('click', function(){
				collabMenu.goTab(this, "todo", "TODO", "TODO", "/groupware/collab/CollabTodo.do", {"myTodo":"Y"});
			});
			
			$(document).off('click','#deptList a, #prjListP a, #prjListW a, #prjListH a, #prjListC a, #prjListF a').on('click','#deptList a, #prjListP a, #prjListW a, #prjListH a, #prjListC a, #prjListF a', function(){
				var dataMap =JSON.parse($(this).attr("data-map"));
				dataMap["myTodo"]= "N";
				
				var prjSeq = dataMap["prjSeq"];
				var prjType = dataMap["prjType"];
				var prjName = dataMap["prjName"];
				var objId = prjType+"_"+prjSeq;
				collabMenu.goTab(this, objId, prjName, "MAIN", "/groupware/collab/CollabMain.do?param="+encodeURI($(this).text()),dataMap);
			});
			
			//즐겨찾기 메뉴 이벤트
			$(document).off('click','#favorList a').on('click','#favorList a', function(){
				var dataMap =JSON.parse($(this).attr("data-map"));
				dataMap["myTodo"]= "N";
				
				var prjSeq = dataMap["prjSeq"];
				var prjType = dataMap["prjType"];
				var prjName = dataMap["prjName"];
				var objId = prjType+"_"+prjSeq;
				
				if ($("#list_P_"+prjSeq).attr("load") != "Y" && $("#multiTabListDiv .l-contents-tabs__item").length > <%=tabCnt%>){
					Common.Error("<spring:message code='Cache.CPMail_mail_msgNotOpenTab' />");
					return ;
				}
				
				collabMenu.goTab($("#list_P_"+prjSeq), objId, prjName, "MAIN", "/groupware/collab/CollabMain.do?param="+encodeURI($(this).text()),dataMap);
			});
			
			/* 내업무 탭 
			  $('.myTask_tabList ul li').click(function(){
			    var tab_id = $(this).attr('data-tab');

			    $('.myTask_tabList ul li').removeClass('active');
			    $('.tstab_cont').removeClass('active');

			    $(this).addClass('active');
			    $("#"+tab_id).addClass('active');
			  });*/
			$("#btnTaskAdd").click(function(){
				collabUtil.openTaskAddPopup("CollabTaskAddPopup", "callbackTodoSave", "", "M", "", "", Common.getDic("lbl_MyWork"), "");	//내업무
			});
			  
			//프로젝트 추가
			$("#btnPrjAdd").click(function(){
			  if($(this).siblings('.column_menu').hasClass('active')){
				  $(this).siblings('.column_menu').removeClass('active');
			  }else{
				  $(this).siblings('.column_menu').addClass('active');
			  }
			});
			
			$(document).off('click','#collaboMenu .btnOnOff, #tabLeftmenu .btnOnOff').on('click','#collaboMenu .btnOnOff, #tabLeftmenu .btnOnOff',function(){
				if($(this).hasClass('active')){
					$(this).removeClass('active');
					$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').removeClass('active');
				}else {
					$(this).addClass('active');
					$(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').addClass('active');
				}
			});
			
			//템플릿 선택
			$("#prj_tmpl").click(function(){
				$("#btnPrjAdd").trigger("click");
				var popupID	= "CollabTmplPopup";
				var openerID = "AttendReq";
				var popupTit	= Common.getDic("lbl_AddProject");//"<spring:message code='Cache.lbl_app_approval_extention' />";
				var popupYN		= "N";
				var callBack	= "";
				var popupUrl	= "/groupware/collabProject/CollabTmplPopup.do?"
								+ "popupID="		+ popupID	+ "&"
								+ "openerID="		+ openerID	+ "&"
								+ "popupYN="		+ popupYN	+ "&"
								+ "callBackFunc="	+ callBack	;

				
				Common.open("", popupID, popupTit, popupUrl, "720px", "815px", "iframe", true, null, null, true); 
			});
			//빈프로젝트
			$("#prj_add").click(function(){
				//	      	var callBack	= encodeURI(JSON.stringify({'prjSeq':aData.prjSeq, 'prjType':aData.prjType, 'objId':objId}));
				$("#btnPrjAdd").trigger("click");

				var popupID	= "CollabProjectPopup";
				var openerID = "AttendReq";
				var popupTit	= Common.getDic("lbl_AddProject");//"<spring:message code='Cache.lbl_app_approval_extention' />";
				var popupYN		= "N";
				var callBack	= "";
				var popupUrl	= "/groupware/collabProject/CollabProjectPopup.do?"
								+ "popupID="		+ popupID	+ "&"
								+ "openerID="		+ openerID	+ "&"
								+ "popupYN="		+ popupYN	+ "&"
								+ "callBackFunc="	+ callBack	;

				
				Common.open("", popupID, popupTit, popupUrl, "720px", "815px", "iframe", true, null, null, true); 
			});
		}
	}
	
	$(document).ready(function(){
		collabMenu.objectInit();
	});
	$(document).off("click","#fixTab").on("click","#fixTab",function(){
		$("#content .cRContBottom").hide();
		$("#multiTabListDiv .l-contents-tabs__item").removeClass("l-contents-tabs__item--active");
		$(".l-contents-tabs__fixed-tab").addClass("l-contents-tabs__fixed-tab--active");
		$("#content .cRContBottom:eq(0)").show();
		collabPortal.reloadPortal();
	});

	window.addEventListener( 'message', function(ev){
	    // 부모창의 함수 실행
	    switch (ev.data.functionName){
	    	case "callbackTodoSave":	
		    case "callbackTaskSave":	
		    case "callbackTaskCopy":
		    	var objId="";
		    	if (ev.data.reqParams["prjSeq"] == "undefined" || ev.data.reqParams["prjSeq"] == ""){
		    		objId ="todo";
		    	}else{
			    	objId = ev.data.reqParams["prjType"]+"_"+ ev.data.reqParams["prjSeq"];
		    	}
		    	collabMain.reloadMain(objId);
		    	break;
		    case "setProjectSel":
		    	var list = ev.data.params.list;
		    	$("#prjList").show();
		    	var maxCount = false; // 중복 여부
		    	var duplication = false; // 중복 여부
		    	$.each(list, function (i, v){
		    		if ($('#prjList' ).find("p[data-prjSeq='"+ v.PrjSeq+"']").length > 0) {
						duplication = true;
					}
					if ($('#prjList').find("p").length >=5){
						maxCount = true;
					}

					if (!duplication && !maxCount ){				
				    	$("#prjList").append($("<p>",{"class":"date_del", "data":{"prjSeq":v.PrjSeq}, "data-prjSeq":v.PrjSeq, "text":""+v.PrjName})
								.append($("<a>",{"class":"ui-icon ui-icon-close","id":"btn_del"}))
						);
					}	
		    	});	
				if(duplication){
					Common.Warning('<spring:message code="Cache.lbl_surveyMsg10" />');
				}
		    	if (maxCount){
					Common.Warning("<spring:message code='Cache.CPMail_limitCount'/>".replace("{0}", "5")); //최대 5개 입니다.
				}
		    	break;
		    case "callbackTaskLink" :
		    	// 관련업무 팝업 콜백.
		    	var taskData = ev.data.params;
		    	var data ={"taskSeq": $("#linkTaskSeq").data("taskSeq"),
						"linkTaskSeq":taskData.TaskSeq,
						"linkTaskName":taskData.TaskName};
		    	
		    	var aData = {"prjType":  $("#linkTaskSeq").data("prjType"), "prjSeq":  $("#linkTaskSeq").data("prjSeq")};
				var objId = $("#linkTaskSeq").data("prjType")+'_'+$("#linkTaskSeq").data("prjSeq");

				if (aData["prjType"] == "todo"){
					aData["myTodo"]="Y";
					objId = "todo";
				}
				
		    	$.ajax({
					type:"POST",
					data: data,
					url:"/groupware/collabTask/addTaskLink.do",
					success: function(data) {
						$('#addTaskDialog').addClass('hidden');
				        collabMain.getMyTask(objId, aData);
					},
					error:function (jqXHR, textStatus, errorThrown) {
						Common.Error("<spring:message code='Cache.msg_ErrorOccurred' />")
					}
				});
		    	break;
		    }
	    
	});
	
	function ProjectInvite_CallBack(orgData){
		var data = $.parseJSON(orgData);
		var item = data.item
		var objId = data.userParams;
		var objData = $("#" + objId).closest('.cRContBottom').data();
		var len = item.length;
		var trgMemberArr = new Array();
		
		if (item != '') {
			var addSaveData;
			$.each(item, function (i, v) {
				if (!v.Dis  ) {
					var type = (v.itemType == 'user') ? 'UR' : 'GR';
					var code = (v.itemType == 'user') ? v.UserCode : v.GroupCode;
	
					var saveData = { "type":type, "userCode":code};
					trgMemberArr.push(saveData);
					
					//추가 담담장 정보
					var POArr = v.po.split(";");
					var DNArr = v.DN.split(";");
					var RGNMArr = v.RGNM.split(";");
					addSaveData = { "JobPositionName":POArr[1], "PhotoPath":v.PhotoPath, "UserID":v.UserID, "UserCode":v.UserCode, "DisplayName":DNArr[0], "DeptName":RGNMArr[0]};
				}	
			});
			
			if (trgMemberArr.length>0){
				//변경전 담당자
				var orgitem =$('#'+objId).data( "prjMemberList") ;
				var resetMemberArr = new Array();
				$(orgitem).each(function (i, v) {
					var saveData = { "JobPositionName":v.JobPositionName, "PhotoPath":v.PhotoPath, "UserID":v.UserID, "UserCode":v.UserCode, "DisplayName":v.DisplayName, "DeptName":v.DeptName};
					resetMemberArr.push(saveData);
				});
				///추가 담담장 정보 포함
				resetMemberArr.push(addSaveData);
				
				///추가 담담장 정보 갱신
				$("#"+objId).data( "prjMemberList", resetMemberArr );
			}			
			
		}
		
		if (trgMemberArr.length > 0){
			$.ajax({
				type:"POST",
				contentType:'application/json; charset=utf-8',
				dataType   : 'json',
				data:JSON.stringify({"trgMember":trgMemberArr
					,"prjSeq":  objData.prjSeq
					,"prjName": objData.prjName
					}),
				url:"/groupware/collabProject/addProjectInvite.do",
				success:function (data) {
					if(data.status == "SUCCESS"){
						Common.Inform(Common.getDic("msg_com_processSuccess"));
						
					} else {
						Common.Error(Common.getDic("msg_ErrorOccurred")+data); //	오류가 발생했습니다. 관리자에게 문의바랍니다
					}
				},
				error:function (request,status,error){
					Common.Error(Common.getDic("msg_ErrorOccurred")+"code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error)
				}
			});
		}else{
			Common.Inform(Common.getDic("CPMail_approvalAlreadyUser"));	//
			
		}
	}
	var initData ={};

</script>
