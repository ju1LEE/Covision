<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="/WEB-INF/tlds/covi.tld" prefix="covi" %>
<jsp:include page="/WEB-INF/views/cmmn/UserInclude.jsp"></jsp:include>

<form>
	<div class="sadmin_pop" >
	<input type="hidden" id ="_CategoryID" value = "${CategoryID}"/>
	<input type="hidden" id ="_selTreeId" value = ""/>
	<input type="hidden" id ="_selTreeName" value = ""/>
	<input type="hidden" id ="target" value ="${target}"/>
	<!-- <div id='coviTree_FolderMenu' > -->
		<div id='coviTree_FolderMenu' style='height:250px;'>
		
		</div>
		<div class="bottomBtnWrap">
	   		<a onclick="btnOnClick();" class="btnTypeDefault btnTypeBg"><spring:message code='Cache.btn_ok'/></a>
	     	<a onclick="Common.Close();"  class="btnTypeDefault" >   <spring:message code='Cache.btn_apv_close'/></a>                 
		</div>
	</div>
</form>
<script type="text/javascript">

(function() {
	var myFolderTree = new coviTree();
	var body = {
		onclick : function(idx, item) { //[Function] 바디 클릭 이벤트 콜백함수
			if (item.FolderType == "Root" && item.FolderPath == "") {
				selectCommunityTreeListByTree(item.FolderID, item.FolderType, "0", item.FolderName);
			} else {
				selectCommunityTreeListByTree(item.FolderID, item.FolderType, item.FolderPath, item.FolderName);
			}
		}
	};
	
	var selectCommunityTreeListByTree = function(pFolderID, pFolderType, pFolderPath, pFolderName) {
		$("#_selTreeId").val(pFolderID);
		$("#_selTreeName").val(pFolderName);
	}
	
	var setTreeData = function() {
		$.ajax({
			url:"/groupware/manage/community/selectCommunityTreeData.do",
			type:"POST",
			data:{
				domain :"${DN_ID}"
			},
			async:false,
			success:function (data) {
				var List = data.list;
				//바인딩할 Selector, Param, DisplayName, width, align, checkbox, ? )
				myFolderTree.setTreeList("coviTree_FolderMenu", List, "nodeName", "170", "left", false, false, body);
			},
			error:function (error){
				CFN_ErrorAjax("/groupware/manage/community/selectCommunityTreeData.do", response, status, error);
			}
		});
		myFolderTree.displayIcon(true);
		myFolderTree.clearFocus(); 
	}
	
	this.btnOnClick = function() {
		if($("#_selTreeId").val() == "" || $("#_selTreeName").val() == ""){
			alert("<spring:message code='Cache.msg_selCategory'/>"); 	// 분류를 선택하세요.
		}else{
			//부모 접근.
			if($("#target").val() == "C"){
				if(opener){
					opener.TreeData($("#_selTreeId").val(),$("#_selTreeName").val(),"C");
				}else{
					parent.TreeData($("#_selTreeId").val(),$("#_selTreeName").val(),"C");
				}
			}else{
				if(opener){
					opener.TreeData($("#_selTreeId").val(),$("#_selTreeName").val(),"P");
				}else{
					parent.TreeData($("#_selTreeId").val(),$("#_selTreeName").val(),"P");
				}
			}
			Common.Close();
		}
	}
	
	$(document).ready(function(){	
		setTreeData();
	});
	
})();

</script>
