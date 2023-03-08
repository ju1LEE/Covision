<%@ page import="egovframework.baseframework.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% 
	String resourceVersion = egovframework.coviframework.util.ComUtils.getResourceVer();
%>

<!-- 웹하드 공통 -->
<script defer type="text/javascript" src="/webhard/resources/script/common/common.js<%=resourceVersion%>"></script>
<script defer type="text/javascript" src="/webhard/resources/script/webhard/user/event.js<%=resourceVersion%>"></script>

<!-- 페이지별 스크립트 사용 -->
<script defer type="text/javascript" src="/webhard/resources/script/webhard/user/TotalView.js<%=resourceVersion%>"></script>

<jsp:include page="/WEB-INF/views/webhard/include/contents.jsp"></jsp:include>

<!-- sourceURL=ImportantBoxList.jsp -->