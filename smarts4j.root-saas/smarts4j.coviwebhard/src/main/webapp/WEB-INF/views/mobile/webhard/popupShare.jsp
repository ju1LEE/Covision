<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- TODO : 다국어 처리 -->
<div data-role="page" id="webhard_share_page">

	<script>
		var webhardQueryString = "${QueryString}";	//QueryString		
	</script>

	<header data-role="header" id="webhard_share_header" class="header">
		<div id="webhard_share_subheader" class="sub_header">
	        <div class="l_header">
	          <a href="javascript: mobile_comm_back();" class="btn_back"><span>닫기</span></a>
	          <div class="menu_link">
	            <a href="#" class="pg_tit" id="webhard_share_title"><span class="Tit_ellip"><spring:message code='Cache.lbl_SharedMemberView' /></span></a>	            
	          </div>
	        </div>
			<div id="webhard_share_addheader" class="utill" style="display:none;">
          		<a href="javascript:mobile_webhard_addSharedMember();" class="btn_txt"><spring:message code='Cache.lbl_Invitation' /></a>
        	</div>
      	</div>		
	</header>

	<div data-role="content" class="cont_wrap" id="webhard_share_content">
		<div class="webhard_cont">
			<!-- 폴더추가 시작 -->
			<div id="webhard_share_addcontent" class="title set2 webhardadd" style="display:none;">
				<div class="ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset">
					<input id="txtwebhard_sharemember" type="text" class="full" placeholder="<spring:message code='Cache.lbl_Mail_DirectInput' />">
					<a href="javascript:mobile_webhard_openOrg();" class="btn_add_file"><spring:message code='Cache.lbl_apv_org' /></a>							
	        	</div>
				<!-- 메일 작성 이름 입력시 주소록 간단보기 창 시작 -->
				<div id="webhard_share_divAutoComplete" class="mail_simplelist" style="top:99px;padding-left:0px; display: none;">
					<ul id="webhard_share_ulAutoComplete" class="org_list select">
					</ul>
				</div>
				<!-- 메일 작성 이름 입력시 주소록 간단보기 끝 -->
	        </div>
			<!-- 폴더추가 끝 -->
			<!-- 공유멤버 리스트 시작 -->
			<div class="tab_wrap member_info">
				<div class="webhard_Smember">
					<ul id="webhard_shared_list" class="org_list select"></ul>
				</div>
			</div>
			<!-- 공유멤버 리스트 끝 -->
		</div>
	</div>
	<!-- 비서 시작 -->
    <div class="btn_private_secretary" style="display:none;">
      <a href="#"><span>개인비서</span></a>
      <span class="ico_new">N</span>
    </div>	

</div>

