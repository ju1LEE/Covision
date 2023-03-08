<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="egovframework.baseframework.util.PropertiesUtil,egovframework.baseframework.util.RedisDataUtil"%>

<div data-role="page" id="portal_main_page">

	${incResource}

	<script type="text/javascript">
		${javascriptString}
	</script>

	<script>
		var _portalAccess = '${access}';
		var _portalInfo = '${portalInfo}';
		var _data = ${data};
	</script>
	<% 
		String isSaaS = PropertiesUtil.getGlobalProperties().getProperty("isSaaS"); 
		String cssPath = PropertiesUtil.getGlobalProperties().getProperty("css.path");	
	%>
	<% if("Y".equals(isSaaS)){ %>
		<style>
		 #imgPortalLogo { background : url('/covicore/common/logo/Mobile_Logo.png.do') no-repeat center center; background-size:120px;}
		</style>
	<% } %>
	<header class="Htype02">
		<div class="sub_header">
			<div class="l_header" >
				<a href="#" class="logo" id="imgPortalLogo"><span>covismart2</span></a>
				<a href="#" class="portal_setting" style="display: none;"></a>
			</div>
		</div>
	</header>

	<div data-role="content" class="cont_wrap" style="overflow-x: hidden;">

		<div id="divPortalMainHome" class="portal_wrap Ptype02">

			<!-- 상단 탭 -->
			<div id="portal_main_tab" class="Plist_tab_wrap" style="display: none;">
				<ul class="Plist_tab">
					<li tab="P" style="display: none;"><a onclick="mobile_portal_SelectTab(this);"><spring:message code="Cache.BizSection_Portal" /></a></li>
					<%if (RedisDataUtil.getBaseConfig("isUseMail").equals("Y")){%>
					<li tab="M" style="display: none;"><a onclick="mobile_portal_SelectTab(this);"><spring:message code="Cache.CPMail_mail_inbox" /></a></li>
					<%} %>
					<li tab="A" style="display: none;"><a onclick="mobile_portal_SelectTab(this);"><spring:message code="Cache.lbl_apv_appDoc" /></a></li>
					<li tab="B" style="display: none;"><a onclick="mobile_portal_SelectTab(this);"><spring:message code="Cache.lbl_IncludeRecentReg" /></a></li>
				</ul>
			</div>
			
			<!-- 탭 Contents -->
			<div id="portal_main_tabcontent" class="Plist_wrap">
				
				<div id="divPortalList_P" tab="P" class="Plist" style="display: none;">
					
					${layout}
					
				</div>
				
				<!-- 메일 -->
				<div id="divPortalList_M" tab="M" class="Plist" style="display: none;">
					
					<div class="Plist_title">
						<span class="Plist_title_mail"><spring:message code="Cache.CPMail_mail_inbox" /></span> 
						<a href="javascript:mobile_comm_go('/mail/mobile/mail/List.do')">more</a>
					</div>
					
					<div class="Plist_cont">
						<ul id="ulPortalMailList">
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">--.--.--</span></p>
											<p class="title read">--------------------------------</p>
										</div>
									</a>
								</div>
							</li>
						</ul>
					</div>
					
					<input type="hidden" id="portal_mail_currentPage" value="1">
					<input type="hidden" id="portal_mail_endoflist" value="false">
					
					<div id="portalMailListMore" name="BtnPortalListMore"  style="display: none;">
						<a onclick="mobile_portal_main_page_ListAddMore();" ><span><spring:message code="Cache.lbl_MoreView" /></span></a>
					</div>
				</div>
				
				<!-- 미결문서 -->
				<div id="divPortalList_A" tab="A" class="Plist" style="display: none;">
					
					<div class="Plist_title">
						<span class="Plist_title_approval02"><spring:message code="Cache.lbl_apv_appDoc" /></span>
						<a href="javascript:mobile_comm_go('/approval/mobile/approval/list.do')">more</a>
					</div>
					
					<div class="Plist_cont">
						<ul id="ulPortalApprovalList">
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor01" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="title read">-------------------------------</p>
											<p class="list_info">
												<span class="point_cr">--------</span><span>------</span>
												<span class="date">---------</span><span>--------</span>
											</p>
										</div>
									</a>
								</div>
							</li>
						</ul>
					</div>
					
					<input type="hidden" id="portal_approval_currentPage" value="1">
					<input type="hidden" id="portal_approval_endoflist" value="false">
					
					<div id="portalApprovalListMore" name="BtnPortalListMore"  style="display: none;">
						<a onclick="mobile_portal_main_page_ListAddMore()" ><span><spring:message code="Cache.lbl_MoreView" /></span></a>
					</div>
				</div>
				
				<!-- 최근게시 -->
				<div id="divPortalList_B" tab="B" class="Plist" style="display: none;">
				
					<div class="Plist_title">
						<span class="Plist_title_board"><spring:message code="Cache.lbl_IncludeRecentReg" /></span> 
						<a href="javascript:mobile_comm_go('/groupware/mobile/board/list.do?menucode=BoardMain&boardtype=Total')">more</a>
					</div>
					
					<div class="Plist_cont">
						<ul id="ulPortalBoardList">
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
							<li>
								<div class="PlistShell">
									<a href="#" class="Shell_link">
										<div class="staff_list"><div href="#" class="staff"><span class="photo BGcolor02" style="background-image: none">-</span></div></div>
										<div class="Plistcont">
											<p class="name read"><span class="Pinname">-----</span><span class="Pdate">-------</span></p>
											<p class="title read"><span>------------------------------------</span></p>
										</div>
									</a>
								</div>
							</li>
						</ul>
					</div>
					
					<input type="hidden" id="portal_board_currentPage" value="1">
					<input type="hidden" id="portal_board_endoflist" value="false">
					
					<div id="portalBoardListMore" name="BtnPortalListMore" style="display: none;">
						<a onclick="mobile_portal_main_page_ListAddMore()" ><span><spring:message code="Cache.lbl_MoreView" /></span></a>
					</div>
					
				</div>
			</div>
			
			<!-- Plist_wrap EMD  -->
		</div>
		
		
		<!-- 음성인식 시작 -->
		<div id="divportalVoiceListener" class="PVoiceBtn used" style="display: none;">
			<a onclick="mobile_portalVoiceCall();"><span></span></a>
		</div>
		<!-- 음성인식 끝 -->
		
		<!-- 음성가이드 팝업 시작 -->
		<div id="divVoiceGuidePop" class="mobile_popup_wrap" style="display: none;">
			<div class="voice_search_wrap" style="height: 260px;">
				<a onclick="mobile_portalVoiceGuideClose()" class="btn_voice_cancel"><span>닫기</span></a>
				<p class="voice_pop_title">음성지원 명령</p>
				<div class="voice_pop_cont">
					<ul class="voice_pop_cont_list">
						<li>사용자 OOO 검색</li>
						<li>받은 메일 보여줘</li>
						<li>안읽은 메일 보여줘</li>
						<li>결재 미결함 보여줘</li>
					</ul>
				</div>
				<div class="write_info_wrap" style="display: none;">
					<p class="write_info">음성인식 버튼을 길게 눌러 기능을 활성 또는 비활성화 할 수 있습니다.</p>
				</div>
				<p style="display: none;">
					<div class="voice_input_check" style="display: none;">
						<input type="checkbox" id="chkVoiceGuideDisplay" class="voice_check">
						<label for="chkVoiceGuideDisplay" id="chkVoiceGuideDisplayLib">더이상 보지 않기</label>
					</div>
				</p>
			</div>
		</div>
		<!-- 음성가이드 팝업 끝 -->
		
		<!-- 팝업 시작 -->
		<div id="divViceMulti" class="mobile_popup_wrap" style="display: none;">
			<div class="voice_search_wrap">
				<a href="javascript:$('#divViceMulti').hide()" class="btn_voice_cancel"><span>닫기</span></a>
				<div class="voice_search_list_scroll">
					<ul class="voice_search_list" id='ulSearchText'></ul>
				</div>
			</div>
		</div>
		<!-- 팝업 끝 -->
		
	</div>
</div>