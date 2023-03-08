<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<jsp:include page="/WEB-INF/views/webhard/common/layout/userInclude.jsp"></jsp:include>

<!-- 웹하드 공통 -->
<script defer type="text/javascript" src="/webhard/resources/script/common/webhard_new.js<%=resourceVersion%>"></script>

<style>
	.popTop > p {
		padding-left: 30px;
		display: inline-block;
		height: 24px;
		line-height: 24px;
		overflow-y: auto;
	}
	
	#treeArea {
	    border: 1px solid #dddddd;
	}
</style>

<body>
	<div class="divpop_contents">
		<div class="popContent layerType02 treeDefaultPop">
			<div class="">
				<div class="top">
					<select id="boxSelect" class="org_tree_top_select"></select>
				</div>
				<div class="middle">
					<div id="treeArea" class="treeList radio"></div>
				</div>
				<div class="bottom">
					<div class="popTop">
						<p id="confirmMsg"></p>	
					</div>
					<div class="popBottom">
						<a href="#" id="btnConfirm" class="btnTypeDefault btnTypeBg"><spring:message code="Cache.btn_Confirm"/></a>	<!-- 확인 -->
						<a href="#" id="btnClose" class="btnTypeDefault"><spring:message code="Cache.btn_Cancel"/></a>	<!-- 취소 -->
					</div>
				</div>
			</div>
		</div>
	</div>
</body>

<script>
//# sourceURL=popupRadioFolderTree.jsp

	(function(param){
		var tree = function(){
			var axtree = new AXTree();
			
			axtree.setConfig({
				targetID: "treeArea",				// HTML element target ID
				theme: "AXTree_none",				// css style name (AXTree or AXTree_none)
				align: "left",
				xscroll: true,
				height: "280px",
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
						if(param.boxUuid === item.BOX_UUID && param.folderUuid === item.UUID) $("#treeArea .treeBodyTable tr").removeClass("selected");
					},
					onexpand: function(idx, item){ // [Function] 트리 아이템 확장 이벤트 콜백함수
						$("#treeArea .treeBodyTable tr").removeClass("selected");
					},
					oncontract: function(idx, item){ // [Function] 트리 아이템 축소 이벤트 콜백함수
						$("#treeArea .treeBodyTable tr").removeClass("selected");
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
			
			axtree.setTreeFocus = function(idx){
				axtree.click(idx, "open", true);
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
			};
			
			var setTree = function(drive){
				ajax('/webhard/user/tree/getSelectDriveTreeList.do', drive ).done(function( data ){
					tree.setList( [drive].concat( data.list ) );
					tree.expandAll(2);
				});
			};
			
			var setDriveList = function(){
				ajax('/webhard/user/tree/getUserDriveList.do').done(function(data){				
					$("#boxSelect")
						.append( 
							data.list.map(function(item,idx){ 
								item.BOX_NAME = (idx === 0 ? "<spring:message code='Cache.lbl_myDrive'/>" : item.BOX_NAME); // 내 드라이브
								return $("<option>",{ value : item.deptCode, text : item.BOX_NAME })/* .prop("disabled", (item.USE_YN === "N")) */.data('item',item) 
							}) 
						)
						.on("change", function(){									
							var $this = this;
							var drive = $.extend({},$("option:selected", $this).data('item'));
							
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
										$("option:selected",$this).data('item',drive);								
										setTree( $.extend({},$("option:selected",$this).data('item')) );
									});
								}else{
									setTree( $.extend({},$("option:selected",$this).data('item')) );
								}
							/* } */
						}).trigger('change');
				});
			};
			
			setDriveList();
			
			return axtree;
		}();
		
		var moveTarget = function(){
			
			if (!(this instanceof arguments.callee )) return new arguments.callee();
			
			this.setDisplay = function(){
				$("#confirmMsg").text("<spring:message code='Cache.WH_msg_moveToSelectedFolder'/>"); // 선택한 폴더로 이동하시겠습니까?
			},
			
			this.confirm = function(){
				var checkedTreeData = tree.getCheckedTreeList(0);
				
				if(!checkedTreeData){
					Common.Warning("<spring:message code='Cache.msg_Mail_PleaseSelectFolder'/>"); // 폴더를 선택하여 주십시오.
					return false;
				}
				
				var soruce = {
					  "fileUuids": param.fileUuids
					, "folderUuids": param.folderUuids
					, "boxUuid": param.boxUuid
					, "folderUuid": param.folderUuid
				};
				
				var target = {
					  "boxUuid": checkedTreeData.BOX_UUID
					, "folderUuid": checkedTreeData.UUID
				};
				
				coviWebHard.move(soruce, target).done(function(data){
					parent.treeObj.refresh();
					Common.Close();
				});
			}
		}();
		
		var copyTarget = function(){
			
			if (!(this instanceof arguments.callee )) return new arguments.callee();
			
			this.setDisplay = function(){
				$("#confirmMsg").text("<spring:message code='Cache.WH_msg_copyToSelectedFolder'/>"); // 선택한 폴더로 복사하시겠습니까?
			},
			
			this.confirm = function(){
				var checkedTreeData = tree.getCheckedTreeList(0);
				
				if(!checkedTreeData){
					Common.Warning("<spring:message code='Cache.msg_Mail_PleaseSelectFolder'/>"); // 폴더를 선택하여 주십시오.
					return false;
				}
				
				var soruce = {
					  "fileUuids": param.fileUuids
					, "folderUuids": param.folderUuids
					, "boxUuid": param.boxUuid
					, "folderUuid": param.folderUuid
				};
				
				var target = {
					  "boxUuid": checkedTreeData.BOX_UUID
					, "folderUuid": checkedTreeData.UUID
				};
				
				coviWebHard.copy(soruce, target).done(function(data){
					if(data.status === "BOX_FULL"){
			   			Common.Warning(Common.getDic("msg_boxFull"), "Warning", function(){ // 웹하드 용량이 가득 찼습니다.<BR />필요 없는 파일을 완전 삭제하고 다시 시도해 주십시오.
			   				$("#Webhard_fileload_list_Window").remove();
			   			});
			   		}else{
						parent.treeObj.refresh();
						Common.Close();
			   		}
				});
			}
		}();
		
		var _target = param.mode === "copy" ? copyTarget : moveTarget;
		
		var setEvent = function(){
			$("#btnConfirm").on("click", function(){
				_target.confirm();
			});
			
			$("#btnClose").on("click", function(){
				Common.Close();
			});
		};
		
		var init = function(){
			setEvent();
			_target.setDisplay();
		};
		
		init();
	})({
		  mode: "${mode}"
		, boxUuid: "${boxUuid}"
		, folderUuid: "${folderUuid}"
		, folderUuids: "${folderUuids}"
		, fileUuids: "${fileUuids}"
	})	
</script>
