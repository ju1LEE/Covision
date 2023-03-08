<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/cmmn/AdminInclude.jsp"></jsp:include>
<%@ page import="egovframework.baseframework.util.PropertiesUtil" %>
<% String approvalAppPath = PropertiesUtil.getGlobalProperties().getProperty("approval.app.path"); %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<script type="text/javascript" src="<%=approvalAppPath%>/resources/script/admin/Monitoring.js<%=resourceVersion%>"></script>
<script>

	var g_personObj;

	function setDeputyPerson(){
		_CallBackMethod2 = doSetDeputyPerson;
		CFN_OpenWindow("/covicore/control/goOrgChart.do?callBackFunc=_CallBackMethod2&type=B1","<spring:message code='Cache.lbl_apv_org' />",1060,580,"");
	}
	
	function doSetDeputyPerson(personObj){
		$("#deputyID").val($$(personObj).find("item").concat().attr("AN"));
		$("#deputyName").val($$(personObj).find("item").concat().attr("DN"));
		
		g_personObj = personObj;
	}
	
	//대결 설정 후 엔진호출
	function approveDeputyProcess(){
		var apvLineObj = $.parseJSON(opener.$("#APVLIST").val());
		
		var person = $$(apvLineObj).find(opener.$("#hidDPath").val());
		var personTaskinfo = $$(person).find("taskinfo");
		
		var deputyID = $("#deputyID").val();
		var deputyName = $("#deputyName").val();
		var comment = Base64.utf8_to_b64($("#txtComment").val());
		var approvalAction = $("input[name=action]:checked").val();
		
		var taskID = $$(person).parent().attr("taskid");
				
		if($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("unittype")=="ou"
			  && ($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") == "assist"
    		  || $$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") =="consult")){
			taskID = $$(person).attr("taskid");
    	}
		
		if(approvalAction != "" && approvalAction != undefined){
            if( ($$(person).parent().parent().attr("routetype") == "consult" || $$(person).parent().parent().attr("routetype") == "assist")
            	&& $$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("unittype")!="ou" ){
            	if(approvalAction == "APPROVAL")
            		approvalAction = "AGREE";
            	else
            		approvalAction = "DISAGREE";
            }
            
            if(approvalAction == "REJECT" && comment == ""){
            	Common.Warning("반려시 의견을 입력해주시기 바랍니다.", "Warning", function(){
            		return false;
            	});
            }else{
            	if(deputyID != "" && deputyName != ""){
    				try{
    					// 대결
    					$$(personTaskinfo).attr("status", "inactive");
    	                $$(personTaskinfo).attr("result", "inactive");
    	                $$(personTaskinfo).attr("kind", "bypass");
    	                $$(personTaskinfo).remove("datereceived");
    	                
    	                var oStep = {};
    	                var oOU = {};
    	                var oPerson = {};
    	                var oTaskinfo = {};
    	                
    	                //workitem Update
    	                
    	                if($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("unittype")=="ou"
        	      			  && ($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") == "assist"
        	          		  || $$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") =="consult")){
      	                	$$(oPerson).append("taskid", taskID);
          	                $$(oPerson).append("wiid", $$(person).attr("wiid"));
        	          	}
    	                $$(oTaskinfo).attr("status", "pending");
    	                $$(oTaskinfo).attr("result", "pending");
    	                $$(oTaskinfo).attr("kind", "substitute");
    	                $$(oTaskinfo).attr("datereceived", "");
    	                
    	                $$(oPerson).attr("code", $$(g_personObj).find("item").concat().attr("AN"));
    	                $$(oPerson).attr("name", $$(g_personObj).find("item").concat().attr("DN"));
    	                $$(oPerson).attr("position", $$(g_personObj).find("item").concat().attr("po"));
    	                $$(oPerson).attr("title", $$(g_personObj).find("item").concat().attr("tl"));
    	                $$(oPerson).attr("level", $$(g_personObj).find("item").concat().attr("lv"));
    	                $$(oPerson).attr("oucode", $$(g_personObj).find("item").concat().attr("RG"));
    	                $$(oPerson).attr("ouname", $$(g_personObj).find("item").concat().attr("RGNM"));
    	                $$(oPerson).attr("sipaddress", $$(g_personObj).find("item").concat().attr("SIP"));
    	                
    	                $$(oPerson).append("taskinfo", oTaskinfo);
    	                
    	                var elmOU = $$(person).parent();
    	                
    	                $$(elmOU).append("person", oPerson);							// person이 object일 경우를 위해서 추가하여 배열로 만듬
    	                
    	                if($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("unittype")=="ou"
        	      			  && ($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") == "assist"
        	          		  || $$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") =="consult")){
                        	$$(elmOU).find("person").json().splice($$(apvLineObj).find(opener.$("#hidDPath").val()).index(), 0, oPerson);
                        } else {
                        	$$(elmOU).find("person").json().splice(0, 0, oPerson);			// 다시 앞에 추가
                        }
    	                
    	          	    $$(elmOU).find("person").concat().eq($$(elmOU).find("person").concat().length-1).remove();			// 배열로 만들기 위해서 추가했던 person을 지움
    	                var workitemID = $$(elmOU).attr("wiid");
    	          	    
    	                if($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("unittype")=="ou"
        	      			  && ($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") == "assist"
        	          		  || $$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") =="consult")){
    	                	workitemID = $$(person).attr("wiid");
        	          	}
    	          	    
    	                setWorkitemData(workitemID, deputyID, deputyName);
    	                
    	                //엔진 호출
    	                var apiData = {};
    	                $$(apiData).append("g_action_"+taskID, approvalAction);
    	                $$(apiData).append("g_actioncomment_"+taskID, comment);
    	                $$(apiData).append("g_actioncomment_attach_"+taskID, []);
    	                $$(apiData).append("g_signimage_"+taskID, getUserSignInfo(deputyID));
    	                $$(apiData).append("g_appvLine", apvLineObj);
    	                $$(apiData).append("g_isMobile_"+taskID, "N");
    	                $$(apiData).append("g_isBatch_"+taskID, "N");
    	                
    	                if($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("unittype")=="ou"
    	      			  && ($$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") == "assist"
    	          		  || $$(apvLineObj).find(opener.$("#hidDPath").val()).parent().parent().attr("routetype") =="consult")){
        	                $$(apiData).append("g_sub_appvLine",apvLineObj);
    	          		}
    	          		
    	                callRestAPI(taskID, apiData);
    				}catch (e) {
    					Common.Error("Error : " + e.message);
    				}
    			}else{
    				// 일반결재
    				var apiData = {};
                    $$(apiData).append("g_action_"+taskID, approvalAction);
                    $$(apiData).append("g_actioncomment_"+taskID, comment);
                    $$(apiData).append("g_actioncomment_attach_"+taskID, []);
                    $$(apiData).append("g_signimage_"+taskID, getUserSignInfo($$(person).attr("code")));
                    $$(apiData).append("g_isMobile_"+taskID, "N");
                    $$(apiData).append("g_isBatch_"+taskID, "N");
    	                
    				callRestAPI(taskID, apiData);
    			}
            }
		}else{
			Common.Warning("결재 Action을 선택해주시기 바랍니다.");
		}
	}
	
	// Workitem의 Deputy 데이터 수정
	function setWorkitemData(workitemID, deputyID, deputyName){
		$.ajax({
			url:"setWorkitemData.do",
			type:"post",
			data: {
				"WorkitemID": workitemID,
				"deputyID" : deputyID,
				"deputyName" : deputyName
			},
			async:false,
			success:function (data) {
				if(data.status == "SUCCESS"){
				}else{
					throw true;
				}
			},
			error:function(response, status, error){
				CFN_ErrorAjax("setWorkitemData.do", response, status, error);
			}
		});
	}
	
	function getUserSignInfo(usercode){
		var retVal = "";
		
		$.ajax({
		    url: "/approval/user/getUserSignInfo.do",
		    type: "POST",
		    data: {
				"UserCode" : usercode
			},
			async:false,
		    success: function (res) {
		    	if(res.status == 'SUCCESS'){
		    		retVal = res.data;
		    	} else if(res.status == 'FAIL'){
		    		Common.Error(res.message);
		    	}
	        },
	        error:function(response, status, error){
				CFN_ErrorAjax("getUserSignInfo.do", response, status, error);
			}
		});
		
		return retVal;
	}
	
	function Close(){
		Common.Close();
	}
</script>
<body>
	<div style="margin: 10px">
		<h3 class="con_tit_box">
			<span class="con_tit">수동 결재하기</span>	
		</h3>
		<div>
			<table id="" class="AXFormTable" width="100%">
				<colgroup>
					<col style="width: 30%">
					<col style="width: 70%">
				</colgroup>
				<tr>
					<th>대리결재자 ID</th>
					<td><input type="text" id="deputyID" ></td>
				</tr>
				<tr>
					<th>대리결재자명</th>
					<td>
						<input type="text" id="deputyName" >
						<a onclick="setDeputyPerson()">
                        <img alt="search" style="vertical-align: middle; cursor:pointer;" src="/HtmlSite/smarts4j_n/covicore/resources/images/covision/btn_org.gif" width="25" height="21">
                    </a>
					</td>
				</tr>
				<tr>
					<th>결재 Action</th>
					<td>
						<input type="radio" id ="resultApprove" name="action" value="APPROVAL">승인(동의)&nbsp;
    					<input type="radio" id ="resultReject" name="action"  value="REJECT">반려(거부)
					</td>
				</tr>
				<tr>
					<th>결재 의견</th>
					<td>
						<textarea id="txtComment" name="txtComment" rows="12" style="width:98%;resize:none" ></textarea>
					</td>
				</tr>
			</table>
			<br>
			<div align="center">
				<input type="button" class="AXButton Blue" value="결재하기" onclick="approveDeputyProcess();"/>
				<input type="button" class="AXButton" value="닫기" onclick="Close();"/>
			</div>
		</div>
	</div>
	
</body>
</html>