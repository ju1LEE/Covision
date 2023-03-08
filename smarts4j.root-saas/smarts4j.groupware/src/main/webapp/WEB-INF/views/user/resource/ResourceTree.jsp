<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>
<style>
	#resourceTree.treeList .AXTree_none .AXTreeScrollBody .AXTreeBody .treeBodyTable tbody tr td .bodyTdText a:last-child { display:inline !important; }
</style>
<body>
	<div class="layer_divpop ui-draggable boradPopLayer" id="testpopup_p" style="width:100%;" source="iframe" modallayer="false" layertype="iframe" pproperty="">
		<div class="divpop_contents">
			<div class="popContent layerType02 treeDefaultPop">
				<div class="">				
					<div class="top">
						<div id="placeOfBusinessSel">
						</div>
					</div>
					<div class="middle">
						<div id="resourceTree" class="treeList radio radioType03" style="height:280px">
						</div>						
					</div>
					<div class="bottom mt10">
						<div>
							<a onclick="setFolderID();" class="btnTypeDefault btnTypeBg"><spring:message code='Cache.btn_Confirm'/></a><!-- 확인 -->
							<a onclick="Common.Close();" class="btnTypeDefault"><spring:message code='Cache.btn_Cancel'/></a><!-- 취소 -->
						</div>
					</div>
				</div>
			</div>
		</div>	
	</div>
</body>
<script type="text/javascript">
	var resourceTree = new coviTree(); 
 	var placeOfBusiness = "";
	
 	$(document).ready(function(){
		// 사업장 사용 여부
		if((Common.getBaseConfig("IsUsePlaceOfBusinessSel") === "N")){
			$("#placeOfBusinessSel").hide();
		}else{
			$("#placeOfBusinessSel").show();
			setPlaceOfBusiness();
		}
 		setTreeData();
 	});
 	
	// 사업장 Select Box 세팅
	function setPlaceOfBusiness(){
		var initInfos = [
	        {
		        target : 'placeOfBusinessSel',
		        codeGroup : 'PlaceOfBusiness',
		        defaultVal : 'PlaceOfBusiness',
		        width : '300',
		        onchange : 'onchangePlaceOfBusiness'
	        }
        ];
        coviCtrl.renderAjaxSelect(initInfos, '', 'ko');
	}
	
	// 사업장 Select Box 변경시
	function onchangePlaceOfBusiness(){
		placeOfBusiness = coviCtrl.getSelected('placeOfBusinessSel').val;
		
		if(placeOfBusiness == "PlaceOfBusiness"){
			placeOfBusiness = "";
		}
		setTreeData();
	}
 	
	// 자원 폴더 Tree 세팅
 	function setTreeData(){		
 		$.ajax({
 			url:"/groupware/resource/getResourceTreeList.do",
 			type:"POST",
 			data:{
 				"placeOfBusiness": placeOfBusiness
 			},
 			success:function (data) {
 				var List = data.list;
 				resourceTree.setTreeList("resourceTree", List, "nodeName", "170", "left", false, true);
 				
 				resourceTree.expandAll(1);
 				if(parent.selectFolderID != "")
 					resourceTree.setCheckedObj('radio', parent.selectFolderID, true);
 				
 				// 예약 불가 자원 radio disabled 처리
 				$("#resourceTree").find("input[type=radio]").each(function() { 
 					var bookingType = JSON.parse($(this).val()).BookingType;
 					if(bookingType == "ApprovalProhibit") $(this).attr("disabled", "disabled");
 				});
 				
 				$("#resourceTree").find("a[id^=folder_item_]").each(function(idx, el) {		// 트리노드가 폴더가 아닌경우, 라디오 버튼이 클릭되도록 처리
 					if(el.type != 'folder') el.onclick = function() { $(el).siblings('input[type=radio]').click(); }
 				});
 			},
 			error:function(response, status, error){
 				CFN_ErrorAjax("/groupware/resource/getResourceTreeList.do", response, status, error);
 			}
 		});
 	}
 	
	// 선택한 자원 폴더 세팅
	function setFolderID(){
		var setType = CFN_GetQueryString("setType");
		var selectFolder = resourceTree.getCheckedTreeList("radio");
		
		if(setType == "D" || setType == "S"){
			parent.$("#ResourceID").val(selectFolder[0].FolderID);
			parent.selectFolderID = selectFolder[0].FolderID;
			parent.resourceUser.setResourceInfo("W", selectFolder[0].FolderID, null);
			parent.$("#simpleWritePop .inputDateView02").find("#FolderName").val(CFN_GetDicInfo(selectFolder[0].MultiDisplayName, parent.lang));
		}else{
			parent.$("#resourceSimpleMake #ResourceID").val(selectFolder[0].FolderID);
			parent.$("#FolderName").css('color', 'black');
			parent.$("#resourceSimpleMake .inputDateView02").find("#FolderName").val(CFN_GetDicInfo(selectFolder[0].MultiDisplayName, parent.lang));
			parent.$("#checkRESChange").val("true");
		}
		
		Common.Close();
	}
</script>