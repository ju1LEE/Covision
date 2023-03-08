<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="validator" uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="egovframework.baseframework.util.PropertiesUtil" %>
<% String appPath = PropertiesUtil.getGlobalProperties().getProperty("app.path"); %>
<% String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path"); %>

<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>coviFlow</title>

<!-- Common -->

<script type="text/javascript" src="<%=appPath%>/resources/script/jquery.min.js"></script>
<script type="text/javascript" src="<%=appPath%>/resources/script/axisj/AXJ.min.js"></script>
<script type="text/javascript" src="<%=appPath%>/resources/script/activiti/manageActiviti.js"></script>

<!-- slimscroll -->
<script type="text/javascript" src="<%=appPath%>/resources/script/Controls/jquery.slimscroll.js"></script>
<script type="text/javascript" src="<%=appPath%>/resources/script/Controls/jquery.slimscroll.min.js"></script>

<!-- Layer Popup -->
<!-- <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css"> -->
<script type="text/javascript" src="<%=appPath%>/resources/script/jquery-ui.js"></script>
<!-- <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script> -->


<!-- top menu -->
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXToolBar.css" />





<!-- grid -->
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/covicore/resources/css/axisj/arongi/AXGrid.css" />
<script type="text/javascript" src="<%=appPath%>/resources/script/Controls/CommonControls.js"></script>
<script type="text/javascript" src="<%=appPath%>/resources/script/Controls/Common.js"></script>










<!-- Utils -->
<script type="text/javascript" src="<%=appPath%>/resources/script/Controls/Utils.js"></script>


<!-- Jsoner -->
<script type="text/javascript" src="<%=appPath%>/resources/script/Jsoner/Jsoner.0.8.2.js"></script>

<link rel="stylesheet" type="text/css" href="<%=cssPath%>/mobile/Base/css/e_wp.css" />
<script type="text/javascript" src="/approval/resources/script/forms/Form.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/FormMenu.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/FormBody.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/FormAttach.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/FormApvLine.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/json2.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/underscore.js"></script>
<script type="text/javascript" src="/approval/resources/script/forms/jquery.xml2json.js"></script>
<script type="text/javascript" src="/approval/resources/script/user/common/ApprovalUserCommon.js"></script>
<script type="text/javascript" src="/approval/resources/script/user/script.js"></script>
<script src="https://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.js"></script>
<script type="text/javascript" src="/approval/resources/script/user/ApprovalListCommon.js"></script>


</head>