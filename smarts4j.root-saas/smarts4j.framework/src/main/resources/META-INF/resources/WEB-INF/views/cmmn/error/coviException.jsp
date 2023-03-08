<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="format-detection" content="telephone=no">
	<meta name="viewport" content="width=1280">
	
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision.control.css<%=resourceVersion%>" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/jquery.mCustomScrollbar.css<%=resourceVersion%>" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/approval/resources/css/approval.css<%=resourceVersion%>" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/common.css<%=resourceVersion%>" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/covision/Controls.css<%=resourceVersion%>" />
	<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/theme/blue.css<%=resourceVersion%>" />	
	<title>Smart²</title>
</head>
<body>	
	<div>
	<section class="errorContainer">
			<div class="errorCont serviceError">
				<c:choose>
					<c:when test="${exceptionType eq 'NoAuth'}">
						<h1>(No permission) 요청한 작업에 대한 권한이 없습니다.</h1>
						<div class="bottomCont">
							<p class="txt">
								<span class="col_red"> 
								요청한 작업에 대해 권한을 부여받지 못했습니다.
								<br /> 
								권한 필요 시 운영자(또는 관리자)에게 권한을 요청하십시요.
							</p>	
							<p class="txt02 mt20">
								Authorization was not granted for the requested operation.
								<br />
								If permission is required, ask the operator (or manager) for permission.
							</p>
							<p class="errorBtnBox mt15">
								<a class="btnTypeDefault btnTypeBg" onclick="javascript:goHome();">홈으로 이동</a>
								<a class="btnTypeDefault " onclick="javascript:history.go(-1);">이전페이지</a>
							</p>				
						</div>
					</c:when>
					<c:otherwise>
						<h1>(Authentication access error) 인증 접속 오류</h1>
						<div class="bottomCont">
							<p class="txt">
								<span class="col_red">부여받지 않은 시스템 접속</span>입니다.
								<br />
								관리자에게 문의 바랍니다.
							</p>
							<br />	
							<p class="txt">
								(Unauthorized system access. Please contact the administrator)
							</p>
							<p class="txt02 mt20">
								페이지에 요구되는 권한 재인증 바랍니다.
								<br />
								동일한 문제가 지속적으로 발생하실 경우에 관리자에게 문의 부탁드립니다.
							</p>
							<p class="errorBtnBox mt15">
								<a class="btnTypeDefault btnTypeBg" onclick="javascript:goHome();">홈으로 이동</a>
								<a class="btnTypeDefault " onclick="javascript:history.go(-1);">이전페이지</a>
							</p>				
						</div>
					</c:otherwise>
				</c:choose>	
			</div>
	</section>
</div>							

</body>

</html>

<script type="text/javascript">
	function goHome(){
		document.location.href = '/groupware/portal/home.do?CLSYS=portal&CLMD=user&CLBIZ=Portal';
	}
</script>