<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="covi" uri="/WEB-INF/tlds/covi.tld"%>
<div class="cRConTop titType AtnTop">
	<h2 class="title"><spring:message code='Cache.lbl_manage_passwordPolicy'/><!-- 암호정책 관리 --></h2>	
</div>		

<div class="cRContBottom mScrollVH">
<form id="form1">
	<div class="sadminContent">
		<table class="sadmin_table pwPolicy">
			<colgroup>
				<col width="200px;">
				<col width="*">
			</colgroup>
			<covi:admin>
			<tr>
				<th><spring:message code='Cache.lbl_passwordComplexity'/>1</th><!-- 암호 복잡성 -->
				<td>
					<select id="selectPasswordIsUse" class="selectType02"></select> 
					<span><span class="thstar">*</span> <spring:message code='Cache.msg_ChangePasswordDSCR05'/></span><!-- 동일 문자 연속 3개 이상, 동일 숫자 3개 이상은 사용 불가 -->
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_max_passwordAge'/></th><!-- 최대 암호 사용 기간 -->
				<td>
					<input type="text" id="maxpasswduseddate"  /> <spring:message code='Cache.lbl_day'/><!-- 일 --> (1 ~ 365)
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_minimum_passwordLength'/></th><!-- 최소 암호 길이 -->
				<td>
					<input type="text" id="minpasswdlen" /> <spring:message code='Cache.lbl_Char'/> (4 ~ 20)
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_PwdChangeNoticeTerm'/></th> <!-- 암호변경 공지기간  -->
				<td>
					<input type="text" id="passwdrenewal"  /> <spring:message code='Cache.lbl_day'/><!-- 일 --> (1 ~ 30)
				</td>
			</tr>
			<tr>
				<th><spring:message code='Cache.lbl_PwdSpecialCharTerm'/></th> <!-- 특수문자정책  -->
				<td>
					<input type="text" id="specialCharacterPolicy" style="text-align:left" />
				</td>
			</tr>
			
			</covi:admin>
			<tr>
				<th><spring:message code='Cache.lbl_initPassword'/></th> <!-- 초기 비밀번호  -->
				<td>
					<input type="text" id="initPassword" style="text-align:left" />
				</td>
			</tr>
			<covi:admin>
			<tr>
				<th><spring:message code='Cache.lbl_loginFailCount'/></th> <!-- 비밀번호 오류 횟수  -->
				<td>
					<input type="text" id="loginFailCount"/>
				</td>
			</tr>
			</covi:admin>
		</table>
		<div class="bottomBtnWrap">
			<a id="btnSave" class="btnTypeDefault" ><spring:message code='Cache.lbl_Save'/></a>
			<a id="btnRefresh" class="btnTypeDefault btnTypeBg"><spring:message code='Cache.lbl_Refresh'/></a>
		</div>
	</div>
</form>
</div>
<script>
var confPolicy = {
		initContent:function(){
			<covi:admin>
			$("#maxpasswduseddate").validation("onlyNumber").validation("limitChar",3);
			$("#minpasswdlen").validation("onlyNumber").validation("limitChar",2);
			$("#passwdrenewal").validation("onlyNumber").validation("limitChar",2);
			$("#loginFailCount").validation("onlyNumber");
			</covi:admin>
			$("#btnRefresh").on("click",function(){
				confPolicy.getData();
			});
			
			$("#btnSave").on("click",function(){
				confPolicy.goSave();
			});
			
			$.ajax({
				type:"POST",
				data:{
					domainID : confMenu.domainId
				},
				async : true,
				url:"/covicore/passwordPolicy/getSelectPolicyComplexity.do",
				success:function (e1) {
					var HTML = "";
					$("#selectPasswordIsUse").html("");
					if(e1.list.length > 0){
						$(e1.list).each(function(ii,vv){
							HTML += '<option value="'+vv.Code+'"  >'+vv.CodeName+'</option>';
		    			});
					}
					$("#selectPasswordIsUse").append(HTML);
					confPolicy.getData();
				},
				error:function(response, status, error){
					CFN_ErrorAjax("/covicore/passwordPolicy/getSelectPolicyComplexity.do", response, status, error);
				}
			}); 	
		},
		getData:function(){
			$.ajax({
				type:"POST",
				data:{
					domainID : confMenu.domainId
				},
				async : true,
				url:"/covicore/passwordPolicy/getPolicy.do",
				success:function (e3) {
					if(e3.list.length > 0){
						$(e3.list).each(function(i3,v3){
							$("#selectPasswordIsUse").val(v3.IsUseComplexity);
							$("#maxpasswduseddate").val(v3.MaxChangeDate);
							$("#minpasswdlen").val(v3.MinimumLength);
							$("#passwdrenewal").val(v3.ChangeNoticeDate);
							$("#specialCharacterPolicy").val(v3.SpecialCharacterPolicy);
		    			});
					}else{
						$("#selectPasswordIsUse").val("0");
						$("#maxpasswduseddate").val("");
						$("#minpasswdlen").val("");
						$("#passwdrenewal").val("");
						$("#specialCharacterPolicy").val("");
					}
					// 초기 비밀번호 , 비밀번호 오류 횟수
					$(e3.settingVal).each(function(i3,v3){
						if(v3.InitPassword != null && v3.InitPassword.length > 0 && v3.InitPassword != ""){
							$("#initPassword").val(v3.InitPassword);
						}else{
							$("#initPassword").val("");
						}
						if(v3.LoginFailCount != null && v3.LoginFailCount.length > 0 && v3.LoginFailCount != ""){
							$("#loginFailCount").val(v3.LoginFailCount);
						}else{
							$("#loginFailCount").val("");
						}
		    		});
				},
				error:function(response, status, error){
					CFN_ErrorAjax("/covicore/passwordPolicy/getPolicy.do", response, status, error);
				}
			}); 
		},
		goSave:function(){
			
			if($("#maxpasswduseddate").val() == ""){
				Common.Warning("<spring:message code='Cache.msg_enter_max_passwordAge'/>", "Warning", function(){ /* 최대 암호 사용 기간을 입력하세요.  */
					$("#maxpasswduseddate").focus();
				}); 
				return ;
			}
			
			if($("#minpasswdlen").val() == ""){
				Common.Warning("<spring:message code='Cache.msg_enter_minimum_passwordLength'/>", "Warning", function(){ /* 최소 암호 길이를 입력하세요.  */
					$("#minpasswdlen").focus();
				}); 
				return ;
			}
			
			if($("#passwdrenewal").val() == ""){
				Common.Warning("<spring:message code='Cache.msg_enter_PwdChangeNoticeTerm'/>", "Warning", function(){ /* 암호변경 공지기간을 입력하세요.  */
					$("#passwdrenewal").focus();
				}); 
				return ;
			}
			
			if($("#initPassword").val() == ""){
				Common.Warning("<spring:message code='Cache.msg_ObjectUR_03'/>", "Warning", function(){ /* 비밀번호를 입력하여 주십시오.  */
					$("#initPassword").focus();
				}); 
				return ;
			}
			
			if($("#loginFailCount").val() == ""){
				Common.Warning("<spring:message code='Cache.msg_enter_loginFailCount'/>", "Warning", function(){ /* 비밀번호 오류 횟수를 입력하세요.  */
					$("#loginFailCount").focus();
				}); 
				return ;
			}
			
			if($("#maxpasswduseddate").val() < 1 || $("#maxpasswduseddate").val() > 365){
				Common.Warning(String.format("<spring:message code='Cache.msg_check_inputRange'/>", 1, 365), "Warning", function(){ /* {0}부터 {1} 사이의 값만 입력할 수 있습니다.  */
					$("#maxpasswduseddate").focus();
				}); 
				return ;
			}
			
			if($("#minpasswdlen").val() < 4 || $("#minpasswdlen").val() > 20){
				Common.Warning(String.format("<spring:message code='Cache.msg_check_inputRange'/>", 4, 20), "Warning", function(){ /* {0}부터 {1} 사이의 값만 입력할 수 있습니다.  */
					$("#minpasswdlen").focus();
				}); 
				return ;
			}
			
			
			if($("#passwdrenewal").val() < 1 || $("#passwdrenewal").val() > 30){
				Common.Warning(String.format("<spring:message code='Cache.msg_check_inputRange'/>", 1, 30), "Warning", function(){ /* {0}부터 {1} 사이의 값만 입력할 수 있습니다.  */
					$("#passwdrenewal").focus();
				}); 
				return ;
			}
			
			if(($("#maxpasswduseddate").val() - $("#passwdrenewal").val()) <= 0){
				Common.Warning(String.format("<spring:message code='Cache.msg_check_max_passwordAge'/>", 1, 30)); /* 최대 암호 사용 기간은  암호변경 공지기간보다 길어야합니다. */
				
				return ;
			}
			
			Common.Confirm("<spring:message code='Cache.msg_RUSave'/>" , "Confirmation Dialog", function (confirmResult) {
				if (confirmResult) {
					$.ajax({
						url:"/covicore/passwordPolicy/updatePasswordPolicy.do",
						type:"post",
						data:{
							domainID : confMenu.domainId,
							complexity : $("#selectPasswordIsUse").val(),
							maxChangeDate : $("#maxpasswduseddate").val(),
							minmumLength : $("#minpasswdlen").val(),
							changeNotIceDate : $("#passwdrenewal").val(),
							specialCharacterPolicy : $("#specialCharacterPolicy").val(),
							initPassword : $("#initPassword").val(),
							loginFailCount : $("#loginFailCount").val()
						},
						success:function (data) {
							if(data.status == "SUCCESS"){
								Common.Inform("<spring:message code='Cache.msg_SuccessRegist'/>" );
							}else{ 
								Common.Warning("<spring:message code='Cache.msg_38'/>" );
							}
						},
						error:function(response, status, error){
							CFN_ErrorAjax("/covicore/passwordPolicy/updatePasswordPolicy.do", response, status, error); 
						}
					}); 
				}
				
			});
		}
}
window.onload = confPolicy.initContent();
</script>
