<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path");
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXJ.min.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXGrid.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXTree.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/jquery.mCustomScrollbar.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision.control.css<%=resourceVersion%>" />	
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/common.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/user_common_controls.css<%=resourceVersion%>" />  
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/Controls.css<%=resourceVersion%>" />
<link rel="stylesheet" id="themeCSS" type="text/css" href="<%=cssPath%>/covicore/resources/css/theme/blue.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/board/resources/css/board.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/task/resources/css/task.css<%=resourceVersion%>" />
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/webhard/resources/css/webhard.css<%=resourceVersion%>" />

<script type="text/javascript" src="/covicore/resources/script/jquery.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/jquery.mousewheel.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/jquery.mCustomScrollbar.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/jquery-ui-1.12.1.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/axisj/AXJ.min.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/CommonControls.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/Utils.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/covicore/resources/script/Controls/covision.control.js<%=resourceVersion%>"></script>

<style>
	#btnDownloadAll {
		float: right;
		margin-top: 14px;
	}
	
	#btnPre {
		display: none;
		margin-right: 5px;
	}
</style>

<jsp:include page="/WEB-INF/views/webhard/include/contents.jsp"></jsp:include>

<script>

	(function(param){
		var nowUuid = param.uuid;
		var uuids = [];
		var titles = [];
		
		var init = function(){
			checkAuth();
		};
		
		var setLayout = function(){
			//default button setting
			$("#btnCopy, #btnMove, #btnDelete, #btnRestore, #btnRename, #btnShare, #btnLink, .searchBox02").remove();
			
			//contents init draw
			$("#btnAlbumView").addClass("active");
			linkView.changeView("thumbnail").init();
		};
		
		var setEvent = function(){
			$(document).on("click", "#btnDownloadAll", function(){
				fileDown("", param.uuid);
			});
			
			$("#btnDownload").off("click").on("click", function(){
				var checkedList = linkView.getCheckedList();
				var folderUuids = [], fileUuids = [];
				
				if(checkedList.length === 0){
					Common.Warning("<spring:message code='Cache.msg_apv_003'/>"); // 선택된 항목이 없습니다.
					return false;
				}
				
				$(checkedList).map(function(idx, item){
					item.TYPE.toLocaleLowerCase() === "folder" ? folderUuids.push(item.UUID) : fileUuids.push(item.UUID);
				});
				
				fileDown(fileUuids.join(";"), folderUuids.join(";"));
			});
			
			$("#btnRefresh").off("click").on("click", function(){
				setList(nowUuid);
			}).trigger("click");
			
			$("#btnListView, #btnAlbumView").off("click").on("click", function(){
				$(this).addClass("active");
				this.id === "btnAlbumView" && $("#btnListView").removeClass("active") 	&& linkView.changeView("thumbnail").init();
				this.id === "btnListView"  && $("#btnAlbumView").removeClass("active") 	&& linkView.changeView("list").init();		
				$("#btnRefresh").trigger("click");
			});
			
			// 위로
			$("#btnPre").off("click").on("click", function(){
				var parentUuid = uuids.pop();
				var title = titles.pop();
				
				$("#driveTitle").text(title);
				nowUuid = parentUuid;
				setList(nowUuid);
			});
		};
		
		var setList = function(uuid){
			getFolderData(param.boxUuid, uuid).done(function(data){
				// 화면 그리기
				linkView.render( data.folderList.concat(data.fileList) );
				
				// 위로 버튼 표시 여부
				uuids.length ? $("#btnPre").css("display", "inline-block") : $("#btnPre").hide();
			});
		};
		
		var checkAuth = function(){
			if(param.isExist !== "Y"){
				Common.Warning("<spring:message code='Cache.WH_msg_noExistLink'/>", "Warning", function(){ // 존재하지 않는 링크입니다.
					history.back();
				});
				return false;
			}else if(param.auth === "STOP"){
				Common.Warning("<spring:message code='Cache.WH_msg_stopSharingLink'/>", "Warning", function(){ // 공유 중지된 링크입니다.
					history.back();
				});
				return false;
			}else if(param.isValid !== "Y"){
				Common.Warning("<spring:message code='Cache.WH_msg_expireLink'/>", "Warning", function(){ // 기간 만료된 링크입니다.
					history.back();
				});
				return false;
			}else if(param.auth === "PW"){
				checkPassword();
			}else{
				setLinkData();
			}
		};
		
		var checkPassword = function(){
			Common.Password("<spring:message code='Cache.WH_msg_enterPassworkForLink'/>", "Password", "<spring:message code='Cache.lbl_ConfirmPassword'/>", function(result){ // 링크접속에 필요한 비밀번호를 입력해주세요. / 비밀번호 확인
				if(!result){
					history.back();
				}else{
					$.ajax({
						url: "/webhard/link/checkPassword.do",
						type: "POST",
						data: {
							  "link": param.link
							, "password": result
						},
						success: function(data){
							if(data.cnt > 0){
								setLinkData();
							}else{
								Common.Warning("<spring:message code='Cache.WH_msg_incorrectPW'/>", "Warning", function(){ // 비밀번호가 올바르지 않습니다.
									location.reload();
								});
							}
						},
						error: function(response, status, error){
			        	     CFN_ErrorAjax("/webhard/link/checkPassword.do", response, status, error);
			        	}
					});
				}
			});
		};
		
		var setLinkData = function(){
			$.ajax({
				url: "/webhard/link/getFolderInfo.do",
				type: "POST",
				data: {
					"uuid": param.uuid
				},
				success: function(data){
					var title = data.folder.folderName;
					
					//title setting
					var $downloadAll = $("<a/>", {"href": "#", "id": "btnDownloadAll", "class": "btnTypeDefault", "text": String.format("[{0}] {1}", title, "<spring:message code='Cache.WH_allDownload'/>")}); // 전체 다운로드
					$("#driveTitle").text(title).after($downloadAll);
					
					setLayout();
					setEvent();
				},
				error: function(response, status, error){
		    	     CFN_ErrorAjax("/webhard/link/getFolderInfo.do", response, status, error);
		    	}
			});
		};
		
		var getFolderData = function(pBoxUuid, pUuid){
			var deferred = $.Deferred();
			$.ajax({
				url: "/webhard/user/tree/getCheckedTreeData.do",
				type: "POST",
				data: {
					  "BOX_UUID": pBoxUuid
					, "UUID": pUuid
					, "mode": "link"
				},
				success: function(data) { deferred.resolve(data); },
				error: function(response, status, error){ deferred.reject(status); }
			});
		 	return deferred.promise();
		};
		
		var fileDown = function(pFileUuids, pFolderUuids){
			var $form = $("<form />", {"action": "/webhard/common/fileDown.do", "method": "POST"});
			
			$form.append($("<input />", {"name": "mode", "value": "link"}));
			$form.append($("<input />", {"name": "fileUuids", "value": pFileUuids ? pFileUuids : ""}));
			$form.append($("<input />", {"name": "folderUuids", "value": pFolderUuids ? pFolderUuids : ""}));
			$form.appendTo("body");
			
			$form.submit();
			$form.remove();
		};
		
		var linkView = function(){
			
			if (!(this instanceof arguments.callee )) return new arguments.callee();
			
			//view common
			var getIconClass = function(type){
				var iconClass = "ic_etc";
				[ "pdf","ppdf" ].indexOf( type ) > -1 && ( iconClass = "ic_pdf" );
				[ "xlsx","xls" ].indexOf( type ) > -1 && ( iconClass = "ic_xls" );
				[ "ppt","pptx" ].indexOf( type ) > -1 && ( iconClass = "ic_ppt" );
				[ "zip","tar","gz","rar" ].indexOf( type ) > -1 && ( iconClass = "ic_zip" );
				[ "docx","doc" ].indexOf( type ) > -1 && ( iconClass = "ic_word" );
				return iconClass;
			}
			
			var convertFileSize = function(pSize){
				var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
			    if (pSize == 0) return 'n/a';
			    var i = parseInt(Math.floor(Math.log(pSize) / Math.log(1024)));
			    return (pSize / Math.pow(1024, i)).toFixed(1) + sizes[i];
			}
			
			var contentTemplate = function(){
				var $WebHardList	= $("<div>"	, { "class" : "WebHardList" });
				var $check			= $("<span>", { "class" : "WebHardList_chk chkStyle01" });
				var $body_category	= $("<div>"	, { "class" : "body_category" });
				var $body_title		= $("<a>"	, { "class" : "body_title" });
				var $body_date		= $("<p>"	, { "class" : "body_date" });
				
				return $WebHardList
						.append( $check )
						.append( $body_category )
						.append( $body_title )
						.append( $body_date );
			}
			
			//view common end
			
			
			//thumbnailView
			var thumbnailView = function(){
				
				if (!(this instanceof arguments.callee )) return new arguments.callee();
				
				var setlayout = function(){
					var $fragment 		= $(document.createDocumentFragment());
					var $folderTitle	= $("<div>", { "class" : "WebHardTitleLine" }).append($("<p>",{ "class" : "tx_title", "text" : "<spring:message code='Cache.lbl_Folder'/>" })); // 폴더
					var $fileTitle 		= $("<div>", { "class" : "WebHardTitleLine" }).append($("<p>",{ "class" : "tx_title", "text" : "<spring:message code='Cache.lbl_File'/>" })); // 파일
					return $fragment
								.append( $folderTitle )
								.append( $("<div>",{ "class" : "WebHardListType02_div" }) )
								.append( $fileTitle )
								.append( $("<div>",{ "class" : "WebHardListType02_div" }) );
				}
				
				var addEvent = function(){
					$("#view")
						.on("click", function(){
							var $target = $(event.target);
							
							if(["body_category","body_title","ic_folder","ic_image","ic_pdf","ic_excel","ic_ppt","ic_zip","ic_word","ic_etc"].indexOf( $target.attr('class') ) > -1){ // inFolder & fileDown
								var data = $target.parents(".WebHardList").data("item");
							
								if( data.FILE_NAME ){ // file
									fileDown(data.UUID);
								}else{ // folder
									uuids.push(data.PARENT_UUID);
									titles.push($("#driveTitle").text());
									
									$("#driveTitle").text(data.FOLDER_NAME);
									nowUuid = data.UUID;
									setList(nowUuid);
								}
							}
						});
				}
				
				this.render = function(data){
					var folderFileObj =
						data.reduce(function( acc,cur,idx,arr ){
							var $template = contentTemplate();
							$template.data('item', cur);
							//checkbox
							$('.WebHardList_chk', $template)
								.append( $("<input>",{ "id"	 : cur.TYPE+idx ,"type" : "checkbox" }).data('item',cur) )
								.append( $("<label>",{ "for" : cur.TYPE+idx }).append("<span>")  );
							//icon
							$('.body_category',$template)
								.append( $("<span>",{ "class" : cur.TYPE === "file" && getIconClass( cur.FILE_TYPE ) || "ic_folder" }) );
							//description
							$('.body_title',$template).text( cur.FILE_NAME || cur.FOLDER_NAME );
							$('.body_date',$template).text(cur.CREATED_DATE);
							//append
							acc[cur.TYPE].append($template);
							return acc;
						}, { file : $(document.createDocumentFragment()), folder : $(document.createDocumentFragment()) });
					
					folderFileObj.file[0].querySelectorAll("div.WebHardList").length === 0 && folderFileObj.file.append( $("<p>",{ "style" : "text-align: center;color: #9a9a9a;padding: 100px 0px;", "text" : "<spring:message code='Cache.msg_NoDataList'/>" }) ); 
					folderFileObj.folder[0].querySelectorAll("div.WebHardList").length === 0 && folderFileObj.folder.append( $("<p>",{ "style" : "text-align: center;color: #9a9a9a;padding: 100px 0px;", "text" : "<spring:message code='Cache.msg_NoDataList'/>" }) );
					
					var $contents = $("#view > div").detach();
					$($contents[1]).empty().append(folderFileObj.folder);
					$($contents[3]).empty().append(folderFileObj.file);
					$("#view").append( $contents );
				}
				
				this.getCheckedList = function(){
					return $("#view .WebHardList input[type=checkbox]:checked").map(function(idx, item){
						return $(item).data("item");
					});
				}
				
				this.init = function(){
					$("#view")
						.empty()
						.prop("class", "WebHardListType02")
						.append( setlayout() )
						.off('click');
					addEvent();
				}
				
				this.resetCheckedList = function(){
					$("#view .WebHardList input[type=checkbox]").prop("checked", false);
				}
			}();
			
			//listView
			var listView = function(){
				if (!(this instanceof arguments.callee )) return new arguments.callee();
				
				var listGrid = new coviGrid();
				
				// 화면 크기 조정 시 오류 발생 및 컨텍스트 표시 안되는 현상 처리
				listGrid.redrawGrid = function(){
					this.pageBody.show();
					this.pageBody.data("display", "show");
					
					this.defineConfig(true);
					this.setColHead();
					
					this.gridTargetSetSize(true);
					this.setBody(undefined, true);
					
					if(typeof coviInput == "object"){
						coviInput.setSwitch();
					}
				};
				
				var gridformater = {
					  fmtFileName : function(){ return $("<div>").append( $("<a>",{ "text" : this.item.FILE_NAME || this.item.FOLDER_NAME }) ).html();  }
					, fmtFileType : function(){
						var $body_category = $("<div>",{ "class" : "body_category" });
						var $icon = $("<span>",{ "class" : ( this.item.FILE_TYPE && getIconClass(this.item.FILE_TYPE) ) || "ic_folder" });
						return $("<div>").append( $body_category.append( $icon ) ).html();
					}
					, fmtFileSize : function(){ return (this.item.FILE_SIZE &&  convertFileSize(this.item.FILE_SIZE))  || "-" }
					, fmtCreateDate : function(){ return this.item.CREATED_DATE || "" }
				};
				
				var initalGrid = function(){
					var header =  [
						{key:'chk',			label:'chk', 											width:'2', 	align:'center', formatter: 'checkbox'},
						{key:'fileType',  	label:"<spring:message code='Cache.lbl_kind'/>", 		width:'5', 	align:'center',	hideFilter : 'Y', formatter: gridformater.fmtFileType},
						{key:'name',  		label:"<spring:message code='Cache.lbl_apv_Name'/>",	width:'25', align:'left', 	hideFilter : 'Y', formatter: gridformater.fmtFileName }, 
						{key:'fileSize', 	label:"<spring:message code='Cache.lbl_Size'/>", 		width:'5', 	align:'center', hideFilter : 'Y', formatter: gridformater.fmtFileSize },
						{key:'createdDate', label:"<spring:message code='Cache.lbl_RegistDate'/>",	width:'10', align:'center', hideFilter : 'Y', formatter: gridformater.fmtCreateDate}	
					];
					
					listGrid.setGridHeader(header);
					listGrid.setGridConfig({
						targetID		: "listGrid",
						height			: "auto",
						paging			: true,
						sort			: true,
						overflowCell	: [],
						emptyListMSG	: "<spring:message code='Cache.msg_NoDataList'/>", // 조회할 목록이 없습니다.
						listCountMSG	: "<b>{listCount}</b> <spring:message code='Cache.lbl_Count'/>", // 개
						body: {
							  onclick	: function(){ addEvent.call(this) }
							, oncheck	: function(){ addEvent.call(this) }
					    }
					});
				}		
				this.render = function( data ){
					listGrid.setData({
						  list: data
						, page: {
							listCount: data.length
						}
					});
				}
				
				var addEvent = function(){
					switch( this.c ){
						case "0" :
							break;
						case "1" :
							break;
						case "2" :
							if(event.target.tagName === "A"){
								if(this.item.TYPE === "folder"){ // 폴더
									uuids.push(this.item.PARENT_UUID);
									titles.push($("#driveTitle").text());
									
									$("#driveTitle").text(this.item.FOLDER_NAME);
									nowUuid = this.item.UUID;
									setList(nowUuid);
								}else{ // 파일
									fileDown(this.item.UUID);
								}
							}
							
							break;
					}
				}
				
				this.init = function(){
					$("#view")
						.empty()
						.prop("class","WebHardListType01")
						.append( $("<div>",{ "id" : "listGrid", "style" : "height: auto" }) )
						.off('click');
					addEvent();
					initalGrid();
				}
				
				this.getCheckedList = function(){
					return listGrid.getCheckedList(0);
				}
				
				this.resetCheckedList = function(){
					if($("#listGrid tr").eq(0).find("input[type=checkbox]").prop("checked")){
						$("#listGrid tr").eq(0).find("input[type=checkbox]").trigger("click");
					}else{
						$("#listGrid tr").eq(0).find("input[type=checkbox]").trigger("click").trigger("click");
					}
				}
			}();
			
			
			//view main event
			var _view;
			this.changeView = function(type){
				var viewType = type || 'thumbnail';
				viewType === "thumbnail"	&& (_view = thumbnailView);
				viewType === "list" 		&& (_view = listView);
				
				return this;
			}
			
			this.init = function(){
				typeof _view.init === "function" && _view.init();
				return this;
			}
			
			this.refresh = function(){ typeof _view.refresh === "function" && _view.refresh(); }
			
			this.getCheckedList = function(){
				if(typeof _view.getCheckedList === "function") return _view.getCheckedList();
			}
			
			this.render = function(data){
				typeof _view.render === "function" && _view.render(data);
				return this;
			}
			
			this.resetCheckedList = function(){
				typeof _view.resetCheckedList === "function" && _view.resetCheckedList();
				return this;
			}
			
		}();
		
		init();
	})({
		  uuid:		"${uuid}"
		, boxUuid:	"${boxUuid}"
		, link:		"${link}"
		, auth:		"${auth}"
		, isValid:	"${isValid}"
		, isExist:	"${isExist}"
	});

</script>