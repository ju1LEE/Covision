<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/userInclude.jsp"></jsp:include>
<%
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<!-- 웹하드 공통 -->
<script type="text/javascript" src="/webhard/resources/script/common/common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/webhard/resources/script/common/webhard_new.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/webhard/user/event.js<%=resourceVersion%>"></script>

<style>
	.WebHard_file_add_left{
		border: none;
	}
	
	#boxList{
		margin-bottom: 10px;
	}
	
	#webhardTree{
		border: 1px solid #c8c8c8;
	}
	
	#searchDetail > div{
		width: 530px;
	}
	
	.bottom{
		text-align: center;
	    font-size: 0;
    }
</style>

<body>
	<div class="popContent WebHard_file_add">
		<div class="WebHard_file_add_wrap">
			<div class="WebHard_file_add_left">
				<select id="boxList" class="org_tree_top_select"></select>
				<div id="webhardTree" class="treeList radio"></div>
			</div>
			<div class="WebHard_file_add_right">
				<jsp:include page="/WEB-INF/views/webhard/include/contents.jsp"></jsp:include>
			</div>
		</div>
		<div class="bottom">
			<a id="btnRegist" class="btnTypeDefault btnTypeBg upload"><spring:message code='Cache.btn_apv_register'/></a> <!-- 등록 -->
		</div>
	</div>
</body>

<script>
//# sourceURL=popupWebhardAttach.jsp

	var treeObj = function(){
		
		var axtree = new AXTree();
		
		axtree.setConfig({
			targetID : "webhardTree",			// HTML element target ID
			theme: "AXTree_none",				// css style name (AXTree or AXTree_none)
			align: "left",
			xscroll: true,
			height: "359.5px",
			showConnectionLine: false,			// 점선 여부
			relation: {
				parentKey: "PARENT_UUID",		// 부모 아이디 키
				childKey: "UUID"				// 자식 아이디 키
			},
			persistExpanded: false,				// 쿠키를 이용해서 트리의 확장된 상태를 유지 여부
			persistSelected: false,				// 쿠키를 이용해서 트리의 선택된 상태를 유지 여부
			colGroup: [{
				key: "UUID",					// 컬럼에 매치될 item 의 키
				width: "100%",
				align: "left",	
				indent: true,					// 들여쓰기 여부
				getIconClass: function(){		// indent 속성 정의된 대상에 아이콘을 지정 가능
					var iconNames = "folder, AXfolder, movie, img, zip, file, fileTxt, company".split(/, /g);
					var iconName = "";
					
					if (typeof this.item.type === "number") {
						iconName = iconNames[this.item.type];
					} else if(typeof this.item.type === "string") {
						iconName = this.item.type.toLowerCase(); 
					}
					
					return iconName;
				},
				formatter: function(){
					var anchorName = $("<a />").attr("id", "folder_item_" + this.item.UUID).attr("uuid", this.item.UUID);
					
					if(this.item.UUID) anchorName.text(this.item.FOLDER_NAME);
					else anchorName.text(this.item.BOX_NAME);
					
					return anchorName.prop("outerHTML");
				}
			}],						// tree 헤드 정의 값
			body: {
				onclick: function(idx, item){
					item.__index ? $("#btnPre").show() : $("#btnPre").hide(); // 위로 버튼 표시
					$("#driveTitle").text(item.FOLDER_NAME || item.BOX_NAME); // 폴더명 표시
					$("#btnRefresh").trigger("click"); // 리스트 조회
				},
				onexpand: function(idx, item){ // [Function] 트리 아이템 확장 이벤트 콜백함수
					$("#webahrdTree .treeBodyTable tr").removeClass("selected");
				},
				oncontract: function(idx, item){ // [Function] 트리 아이템 축소 이벤트 콜백함수
					$("#webahrdTree .treeBodyTable tr").removeClass("selected");
				}
			}						// 이벤트 콜벡함수 정의 값
		});
		
		axtree.getCheckedTreeList = function(idx){
			var collect = [];
			var list = this.list;
			
			this.body.find(".selected [uuid]").each(function(){
				var uuid = $(this).attr("uuid");
				
				for(var i = 0; i < list.length; i++){
					if(list[i].UUID === uuid) collect.push(list[i]);
				}
			});
			
			// 처음 화면 로드 시 트리에 클릭된 항목이 없어서 목록이 표시되지 않는 이슈가 있어 분기처리함
			if(idx === 0 && collect.length === 0){
				return this.list[0];
			}
			
			return idx != null ? collect[idx] : collect;
		};
		
		axtree.getTreeDataList = function(idx){
			if(idx != null && idx !== ""){
				return this.list[idx];
			}else{
				return this.list;
			}
		};
		
		axtree.getBoxUuid = function(){
			return axtree.getTreeDataList(0).BOX_UUID;
		};
		
		axtree.setTreeFocus = function(idx){
			axtree.click(idx, "open", true);
		};
		
		axtree.refresh = function(){
			$("#boxList").trigger("change");
		};
		
		var ajax = function(pUrl, param){
			var deferred = $.Deferred();
			$.ajax({
				url: pUrl,
				type:"POST",
				data: param || {},
				//async: bAsync,
				success:function (data) { deferred.resolve(data);},
				error:function(response, status, error){ deferred.reject(status); }
			});
			return deferred.promise();
		}
		
		var setTree = function(drive){
			ajax('/webhard/user/tree/getSelectDriveTreeList.do', drive ).done(function( data ){
				try {
					axtree.setList( [drive].concat( data.list ) );
				} catch (error) {
					Common.Warning(String.format("<spring:message code='Cache.msg_AjaxErrorEtc'/>", "AXTree.setList"));
				}
				
				axtree.expandAll(2);
				
				axtree.getCheckedTreeList().length === 0 && axtree.click(0) /* && $("#btnRefresh").trigger("click") */;
			});
		}
		
		var setDriveList = function(){
			ajax('/webhard/user/tree/getUserDriveList.do').done(function(data){
				$("#boxList")
					.append(
						data.list.map(function(item,idx){
							item.BOX_NAME = (idx === 0 ? "<spring:message code='Cache.lbl_myDrive'/>" : item.BOX_NAME); // 내 드라이브
							return $("<option>", { value : item.deptCode, text : item.BOX_NAME })/* .prop("disabled", (item.USE_YN === "N")) */.data('item', item)
						})
					)
					.on('change',function(){
						var $this = this;
						var drive = $.extend({},$("option:selected",$this).data('item'));
						
						// BOX 사용 여부 체크
						/* if(drive.USE_YN === "N"){
							Common.Warning("<spring:message code='Cache.msg_driveNotAvailable'/>"); // 해당 드라이브를 사용할 수 없습니다.
						}else{ */
							// BOX 존재 여부 체크
							if( !drive.BOX_UUID ){
								ajax('/webhard/user/tree/createBox.do', {
									ownerType : drive.OWNER_TYPE
									,ownerId : drive.GROUP_CODE
									,boxName : drive.BOX_NAME
									,domainCode : drive.DOMAIN_CODE
								}).done(function( data ){
									drive.BOX_UUID = data.createBoxInfo.boxUuid;
									$("option:selected", $this).data('item', drive);
									setTree( $.extend({},$("option:selected",$this).data('item')) );
								});
							}else{
								setTree( $.extend({},$("option:selected",$this).data('item')) );
							}
							
							// 부서 드라이브에서는 공유받은 폴더, 공유한 폴더 표시X
							if(drive.OWNER_TYPE !== "U"){
								$("#leftmenu li[data-menu-alias=webhard_Shared]").hide();
								$("#leftmenu li[data-menu-alias=webhard_Published]").hide();
							}else{
								$("#leftmenu li[data-menu-alias=webhard_Shared]").show();
								$("#leftmenu li[data-menu-alias=webhard_Published]").show();
							}
						/* } */
					}).trigger('change');
			});
		}
		
		setDriveList();
		
		return axtree;
	}();
	
	(function(param){
		var init = function(){
			// view setting(default type: list)
			$("#btnListView").addClass("active") && webhardView.changeView("list").init().setHideImportant().setConfigGrid({
				body: {
			          onclick: function(){ redefineGridEvent.call(this) }
					, oncheck: function(){ redefineGridEvent.call(this) }
			    }
			});
			
			// button default setting
			$("#btnCopy, #btnMove, #btnDelete, #btnDownload, #btnRename").remove();
			
			setEvent();
			
			//조회된 LeftMenu에 스크롤 바인드
			coviCtrl.bindmScrollV($(".mScrollV"));
		};
		
		var setEvent = function(){
			// 새로고침
			$("#btnRefresh").off("click").on("click", function(){
				coviWebHard.getCheckedTreeData().done(function(data){
					webhardView.render( data.folderList.concat(data.fileList) );
					
					setListEvent();
				});
			});
			
			// 리스트 표시 타입 변경
			$("#btnListView, #btnAlbumView").off("click").on("click", function(){
				$(this).addClass('active');	
				this.id === "btnAlbumView" && $("#btnListView").removeClass("active") 	&& webhardView.changeView("thumbnail").init();
				this.id === "btnListView"  && $("#btnAlbumView").removeClass("active") 	&& webhardView.changeView("list").init().setHideImportant().setConfigGrid({
					body: {
				          onclick: function(){ redefineGridEvent.call(this) }
						, oncheck: function(){ redefineGridEvent.call(this) }
				    }
				});
				$("#btnRefresh").trigger("click");
			});
			
			// 위로
			$("#btnPre").off("click").on("click", function(){
				var parentUuid = treeObj.getCheckedTreeList(0).PARENT_UUID;
				var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === parentUuid })[0];
				
				treeObj.click(obj.__index, "open", true);
				
				$("#driveTitle").text(obj.FOLDER_NAME || obj.BOX_NAME); // 폴더명 표시
				obj.__index ? $("#btnPre").show() : $("#btnPre").hide(); // 위로 버튼 표시
				$("#btnRefresh").trigger("click"); // 리스트 조회
			});

			// 입력창 우측 이벤트
			$("#nameSearchBtn").off("click").on("click", function(){
				var sObj = {};
				$.trim($("#nameSearchText").val()) && (sObj.input = $("#nameSearchText").val())
				webhardView.searchKeyword( sObj );
			});
			
			// 상세 검색 버튼
			$("#btnSearch").off("click").on("click", function(){
				var sObj = {};
				$.trim($("#searchText").val()) && (sObj.input = $("#searchText").val())
				if( $.trim($("#startDate").val()) && $.trim($("#endDate").val()) ){
					sObj.dateTerm = {};
					sObj.dateTerm.start = $("#startDate").val().replaceAll(".", "");
					sObj.dateTerm.end	= $("#endDate").val().replaceAll(".", "");
				}
				webhardView.searchKeyword( sObj );
			});
			
			// 등록
			$("#btnRegist").off("click").on("click", function(){
				var checkedList = webhardView.getCheckedList();
				
				if(checkedList.length > 0){
					var fileUuids = $.map(checkedList, function(obj){
						return obj.UUID;
					}, []);
					
					$.ajax({
						url: "/webhard/attach/uploadToFront.do",
						data: {
							fileUuids: fileUuids.join(";"),
						},
						type: "POST",
						success: function(result){
							if(result.status === "SUCCESS"){
								if(param.openType === "common"){
									eval("parent." + param.callbackFunc +"(result.files);");
								}else if(param.openType === "approval"){
									eval("opener." + param.callbackFunc +"(result.files);")
								}
								
								Common.Close();
							} else {
								Common.Warning("<spring:message code='Cache.msg_apv_030'/>"); // 오류가 발생했습니다.
							}
						},
						error: function(response, status, error){
							CFN_ErrorAjax("/webhard/attach/uploadToFront.do", response, status, error);
						}
					});
				}else{
					Common.Warning(Common.getDic("msg_apv_003")); // 선택된 항목이 없습니다.
					return false;
				}
			});
		};
		
		var setListEvent = function(){
			if($("#btnListView").hasClass("active")){
				// 폴더 체크박스 X
				$("#listGrid .gridBodyTr").each(function(idx, item){
					$(item).find(".body_category span").attr("class").indexOf("folder") > -1 && $(item).find("input[type=checkbox]").remove();
				});
			}else{
				// 중요 버튼 표시 X
				$(".body_important").remove();
				
				// 폴더 체크박스 X
				$(".body_category").each(function(idx, item){
					$(item).find("span").attr("class").indexOf("folder") > -1 && $(item).closest(".WebHardList").find(".WebHardList_chk").remove();
				});
				
				// 클릭 이벤트 재정의
				$(".body_category").off("click").on("click", function(e){
					var $this = $(this);
					var className = $this.find("span").attr("class");
					var data = $this.parents(".WebHardList").data("item");
					
					if(className.indexOf("folder") > -1){ // 폴더
						var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === data.UUID })[0];
						
						treeObj.click(obj.__index, "open", true);
						
						$("#driveTitle").text(obj.FOLDER_NAME || obj.BOX_NAME); // 폴더명 표시
						obj.__index ? $("#btnPre").show() : $("#btnPre").hide(); // 위로 버튼 표시
						$("#btnRefresh").trigger("click"); // 리스트 조회
					}
				});
			}
		};
		
		//grid Event
		var redefineGridEvent = function(){
			switch( this.c ){
				case "0" :
					break;
				case "1" :
					break;
				case "2" :
					if(event.target.tagName === "A"){
						if(this.item.TYPE === "folder"){
							var uuid = this.item.UUID;
							var obj = treeObj.getTreeDataList().filter(function(item){ return item.UUID === uuid })[0];
							
							treeObj.click(obj.__index, "open", true);
							
							$("#driveTitle").text(this.item.FOLDER_NAME || this.item.BOX_NAME); // 폴더명 표시
							obj.__index ? $("#btnPre").show() : $("#btnPre").hide(); // 위로 버튼 표시
							$("#btnRefresh").trigger("click"); // 리스트 조회
						}
					}
					
					break;
			}
		};
		
		init();
	})({
		  openType: "${openType}"
		, callbackFunc: "${callbackFunc}"
	});

</script>
