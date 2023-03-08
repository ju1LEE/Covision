<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<script type="text/javascript" src="/webhard/resources/script/common/common.js<%=resourceVersion%>"></script>
<script type="text/javascript" src="/webhard/resources/script/common/webhard_new.js<%=resourceVersion%>"></script>
<style>
	.cLnbMiddle {
		top: 180px;
	}
	
	.fixRateLayer {
		position: fixed;
		bottom: 0;
		width: 279px;
		border-top: 1px solid #d9d9d9;
		padding-top: 15px;
	}
	
	div.cLnbMiddle > div {
		max-height: calc(100% - 65px) !important;
		padding-bottom: 10px;
	}
	
	.WebHard_exmenu_list {
		border-bottom: none;
	}
	
	.WebHard_exmenu_list li {
		border-bottom: 1px solid #f0f0f0;
	}
</style>

<div class="cLnbTop">
	<h2><spring:message code='Cache.lbl_webhard'/></h2> <!-- 웹하드  -->
	<div>
		<a id="btnUpload" href="#" class="btnType01"><spring:message code='Cache.btn_Upload'/></a> <!-- 업로드 -->
	</div>
	<div class="searchBox02 lnb webHard">
		<span>
			<input type="text" id="txtTotalSearch" class="HtmlCheckXSS ScriptCheckXSS" placeholder="<spring:message code='Cache.lbl_AllSearch'/>">	<!-- 전체검색 -->
			<button type="button" id="btnSearchTotal" class="btnSearchType01"><spring:message code='Cache.btn_search'/></button>	<!-- 검색 -->
		</span>
	</div>
	<select id="driveSelect" class="org_tree_top_select webHard"></select>
</div>
<div class="cLnbMiddle mScrollV scrollVType01">
	<ul id="leftmenu" class="contLnbMenu whardMenu"></ul>
	<ul class="contLnbMenu fixRateLayer">
		<li class="whardnoIcon">
			<div class="capacity_wrap">
				<p class="capacity_tit" id="currentWhSize">
					0.0GB/<span class="capacity_gray" id="maxWhSize">10GB</span>
				</p>
				<div class="capacity_box">
					<div class="capacity_bar" id="currentWhRate" style="width:0%; background-color:#e12f2f;"></div>
				</div>
			</div>
		</li>
	</ul>
</div>

<!-- 업로드버튼 컨텍스트 메뉴 -->
<div id="uploadContextMenu" class="WebHard_exmenu_layer" style="display:none;">
	<ul class="WebHard_exmenu_list">
		<li value="FolderUpload"><a href="#" class="folder_upload"><spring:message code='Cache.lbl_uploadFolder'/></a></li> <!-- 폴더 업로드 -->
	</ul>
	<ul class="WebHard_exmenu_list">
		<li value="FileUpload"><a href="#" class="file_upload"><spring:message code='Cache.lbl_uploadFile'/></a></li> <!-- 파일 업로드 -->
	</ul>
</div>

<!-- 폴더트리 컨텍스트 메뉴 -->
<div id="folderContextMenu" class="WebHard_exmenu_layer" style="display:none;"></div>

<script type="text/javascript">
	//# sourceURL=userLeft.jsp
	
	(function(param){
		var init = function(){
			setLeftMenu();
			setEvent();
			
			// 웹하드 사용량 조회
			coviWebHard.getUsageWebHard().done(function(data){
				var maxSize = webhardCommon.convertFileSize(data.BOX_MAX_SIZE * 1024  * 1024);
				var currentSize = data.CURRENT_SIZE_BYTE ? webhardCommon.convertFileSize(data.CURRENT_SIZE_BYTE) : "0.0GB";
				
				$("#currentWhSize").empty();
                $("#currentWhSize").append(
                	currentSize + "/",
					$("<span/>", {"class": "capacity_gray", "text": maxSize})
				);
                
                // 80% 미만일 때: #49bedf / 80% 이상일 때: #e12f2f
                $("#currentWhRate").css({"width": data.RATE + "%", "background-color": data.RATE >= 80 ? "#e12f2f" : "#49bedf"});
			});
		};
		
		var setEvent = function(){
			// 업르도 버튼 클릭 이벤트
			$("#btnUpload").on("click", function(){
				webhardCommon.uploadPopup(treeObj.getBoxUuid(), treeObj.getCheckedTreeList(0) ? treeObj.getCheckedTreeList(0).UUID : "");
			});
			
			// 업로드 버튼 컨텍스트 바인딩
		 	/* $("#btnUpload").bind("contextmenu", function(event){
		 		event.preventDefault();
				
		 		$("#uploadContextMenu").show();
		 		$("#uploadContextMenu").css({top:event.pageY-70 +"px", left: event.pageX-70+"px"});
				
		 		//컨텍스트메뉴를 호출한 보관함의 태그 정보
		 		selectBoxContext = $(event.target);
		 	});
			
			if($("#uploadContextMenu").css("display") === "block"){
				$("#uploadContextMenu").hide();
			} */
			
			// 좌측 트리 > 컨텍스트 메뉴 바인딩
			$("#coviTree_FolderMenu").unbind("contextmenu").bind("contextmenu", function(event, objSeq){
				event.preventDefault();
				
				var $target = $(event.target);
				var uid = $target.attr("uuid") ? $target.attr("uuid") : Number($target.attr("id").split("").pop());
				
				//Context Menu를 호출한 트리 노트 focus처리
				var idx = $("#coviTree_FolderMenu_AX_tbody tr").index($target.closest("tr"));
				treeObj.setTreeFocus(idx);
				
				webhardContext.setContextType("tree");
				webhardContext.init(uid ? "folder" : "root");
				
				$("#folderContextMenu").show();
				$("#folderContextMenu").css({top: (event.pageY - 70) + "px", left: (event.pageX - 70) + "px"});
			});
		};
		
		var setLeftMenu = function(){
			var webhardLeft = new CoviMenu({lang: Common.getSession("lang"), isPartial: "true"});
			
			// 좌측 메뉴 가져오기
			webhardLeft.render("#leftmenu", param.leftData, "userLeft");
			
			$("#leftmenu > li > a").removeClass("selected");
			$("#leftmenu").prepend($("<li/>", {"class": "whardMenu01"}).append($("<div/>", {"id": "coviTree_FolderMenu", "class": "treeList radio"})));
			
			// 조회된 LeftMenu에 스크롤 바인드
			coviCtrl.bindmScrollV($('.mScrollV'));
		};
		
		init();
	})({
		leftData: ${leftMenuData}
	});
	
	var treeObj = function(){
		
		var axtree = new AXTree();
		
		axtree.setConfig({
			targetID : "coviTree_FolderMenu",	// HTML element target ID
			theme: "AXTree_none",				// css style name (AXTree or AXTree_none)
			align: "left",
			xscroll: true,
			height: "auto",
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
					var anchorName = $("<a/>", {"id": "folder_item_" + this.item.UUID, "uuid": this.item.UUID, "text": (this.item.UUID ? this.item.FOLDER_NAME : this.item.BOX_NAME)});
					
					return anchorName.prop("outerHTML");
				}
			}],						// tree 헤드 정의 값
			body: {
				onclick: function(idx, item){
					$("#leftmenu > li > a").removeClass("selected");
					webhardCommon.pageMove(item.BOX_UUID, item.UUID, "Normal");
				},
				onexpand: function(idx, item){ // [Function] 트리 아이템 확장 이벤트 콜백함수
					$("#coviTree_FolderMenu .treeBodyTable tr").removeClass("selected");
				},
				oncontract: function(idx, item){ // [Function] 트리 아이템 축소 이벤트 콜백함수
					$("#coviTree_FolderMenu .treeBodyTable tr").removeClass("selected");
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
			$("#driveSelect").trigger("change");
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

				// 21.11.04 : 이전 삭제 데이터의 문제로 트리구조가 깨진 상태로 데이터를 loading 하게 되면 트리구조를 만들지 못해 error 발생.
				// 1/2 : PARENT_UUID가 FOLDER_PATH에 없으면 트리구조 생성 못함. 필터링 대상.
				var filterObj = data.list;
				filterObj = filterObj.filter(function(obj) {
					if ( obj.FOLDER_PATH.indexOf(obj.PARENT_UUID) === -1 ) {
						return false;
					}
					return true;
				});
				
				// 2/2 : PARENT_UUID에 해당하는 UUID가 data list에 없으면 중간단계가 빠져있어 트리구조를 그릴 수 없어서 오류 발생. 필터링.
				var cntLoop = 0;
				for (var i=0; filterObj.length-1;) {	// 필터링으로 변동되는 length를 증감식없이 break 문으로 처리. 
					if (filterObj.length-1 <= i ) {
						break;
					}
					cntLoop = 0;
					
					if (filterObj[i].PARENT_UUID === "") {
						i++;
						continue;
					} else {
						filterObj.forEach(function(j) {
							if (filterObj[i].PARENT_UUID === j.UUID) {
								cntLoop++;		// 연결관계 있는 것. cntLoop 값을 +1 함.
							}
						})
						
						if (cntLoop === 0) {	// 삭제하면 i값 유지.
							filterObj.splice(i,1);
						} else {
							i++;				// 삭제없으면 idx +1.
						}
					}
				}
				
				try {
					axtree.setList( [drive].concat( filterObj ) );
				} catch (error) {
					Common.Warning(String.format("<spring:message code='Cache.msg_AjaxErrorEtc'/>", "AXTree.setList"));
				}
				
				var webhardUrl = [
					"/webhard/layout/user_BoxList.do"
					,"/webhard/layout/user_SharedBoxList.do"
					,"/webhard/layout/user_PublishedBoxList.do"
					,"/webhard/layout/user_RecentBoxList.do"
					,"/webhard/layout/user_ImportantBoxList.do"
					,"/webhard/layout/user_TrashbinBoxList.do"
					,"/webhard/layout/user_TotalView.do"
				]
				if( webhardUrl.indexOf(location.pathname) === -1 ){
					axtree.click(0, "open");
				}else if(location.pathname.indexOf("/webhard/layout/user_BoxList.do") > -1
						&& data.list.length && CFN_GetQueryString("boxID") !== data.list[0].BOX_UUID){
					axtree.click(0);
				}else{
					typeof boxinit === "function" && boxinit();
				}
			});
		}
		
		var setDriveList = function(){
			ajax('/webhard/user/tree/getUserDriveList.do').done(function(data){
				$("#driveSelect")
					.append(
						data.list.map(function(item,idx){
							item.BOX_NAME = (idx === 0 ? "<spring:message code='Cache.lbl_myDrive'/>" : item.BOX_NAME); // 내 드라이브
							return $("<option>", { value : item.deptCode, text : item.BOX_NAME, selected : CFN_GetQueryString("boxID") === item.BOX_UUID})/* .prop("disabled", (item.USE_YN === "N")) */.data('item',item)
						})
					)
					.on('change', function(){
						var $this = this;
						var drive = $.extend({},$("option:selected",$this).data('item'));
						
						// BOX 존재 여부 체크
						if( !drive.BOX_UUID ){
							ajax('/webhard/user/tree/createBox.do', {
								ownerType : drive.OWNER_TYPE
								,ownerId : drive.GROUP_CODE
								,boxName : drive.BOX_NAME
								,domainCode : drive.DOMAIN_CODE
							}).done(function( data ){
								drive.BOX_UUID = data.createBoxInfo.boxUuid;
								drive.USE_YN = "Y"; 	// 새로운 드라이브 생성 시 USE_YN = Y.
								coviWebHard.getUsageWebHard(drive.BOX_UUID).done(function(data){
									var maxSize = webhardCommon.convertFileSize(data.BOX_MAX_SIZE * 1024  * 1024);
									var currentSize = data.CURRENT_SIZE_BYTE ? webhardCommon.convertFileSize(data.CURRENT_SIZE_BYTE) : "0.0GB";
										
									$("#currentWhSize").empty();
						            $("#currentWhSize").append(
						               	currentSize + "/",
										$("<span/>", {"class": "capacity_gray", "text": maxSize})
									);
						                
						            $("#currentWhRate").css({"width": data.RATE + "%", "background-color": data.RATE >= 80 ? "#e12f2f" : "#49bedf"});
								});
									
								$("option:selected",$this).data('item',drive);
								setTree( $.extend({},$("option:selected",$this).data('item')) );
							});
						}else{
							// BOX 사용 여부 체크
							if(drive.USE_YN === "N"){
								// 선택된 드라이브를 사용할 수 없을 때.
								if (drive.BOX_NAME.length > 0) {
									Common.Warning("'"+drive.BOX_NAME+"' "+"<spring:message code='Cache.msg_driveNotAvailable'/>");
								} else {
									Common.Warning("<spring:message code='Cache.msg_driveNotAvailable'/>"); // 해당 드라이브를 사용할 수 없습니다.
								}
								
								// 사용할 수 없는 드라이브 선택 시, 사용할 수 있는 드라이브 중 첫번째로 강제 이동.
								var useList = data.list.filter(function(obj) {
                                    return obj.USE_YN === 'Y'
                              	});
								
								if (useList.length > 0) {
                                    setTree( $.extend({}, useList[0]) );
                                    $("#driveSelect").val(useList[0].BOX_NAME).prop("selected", true)
                                    $("#driveSelect").trigger("change");      
	                            }
								
							} else {
								coviWebHard.getUsageWebHard(drive.BOX_UUID).done(function(data){
									var maxSize = webhardCommon.convertFileSize(data.BOX_MAX_SIZE * 1024  * 1024);
									var currentSize = data.CURRENT_SIZE_BYTE ? webhardCommon.convertFileSize(data.CURRENT_SIZE_BYTE) : "0.0GB";
									
									$("#currentWhSize").empty();
					                $("#currentWhSize").append(
					                	currentSize + "/",
										$("<span/>", {"class": "capacity_gray", "text": maxSize})
									);
					                
					                $("#currentWhRate").css({"width": data.RATE + "%", "background-color": data.RATE >= 80 ? "#e12f2f" : "#49bedf"});
								});
								setTree( $.extend({},$("option:selected",$this).data('item')) );	
								
								// 부서 드라이브에서는 공유받은 폴더, 공유한 폴더 표시X
								if(drive.OWNER_TYPE !== "U"){
									$("#leftmenu li[data-menu-alias=webhard_Shared]").hide();
									$("#leftmenu li[data-menu-alias=webhard_Published]").hide();
								}else{
									$("#leftmenu li[data-menu-alias=webhard_Shared]").show();
									$("#leftmenu li[data-menu-alias=webhard_Published]").show();
								}
							}
						}
					}).trigger('change');
			});
		}
		
		setDriveList();
		
		return axtree;
	}();
	
</script>
