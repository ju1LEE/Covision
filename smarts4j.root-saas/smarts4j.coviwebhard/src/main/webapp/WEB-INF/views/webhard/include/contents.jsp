<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
	.body_category {
		cursor: pointer;
	}
	
	#inputBasic_AX_endDate{
		left: 321.438px !important;
	}
</style>

<div class="cRConTop titType">
	<h2 id="driveTitle" class="title"></h2>
	<div class="searchBox02">
		<span>
			<input id="nameSearchText" type="text">
			<button type="button" id="nameSearchBtn" class="btnSearchType01"><spring:message code='Cache.btn_search'/></button> <!-- 검색 -->
		</span>
		<a href="#" class="btnDetails" id="btnDetails"><spring:message code='Cache.lbl_detail'/></a> <!-- 상세 -->
	</div>
</div>
<div class="cRContBottom mScrollVH ">
	<div id="searchDetail" class="inPerView type05 webhardSearchDetail">
		<div>
			<div class="selectCalView">
				<div class="msd_top">
					<span><spring:message code='Cache.WH_fileName'/></span> <!-- 파일명 -->
					<input id="searchText" type="text" style="width: 380px !important;">
				</div>
				<div class="msd_top tc02" style="display:none;">
					<span><spring:message code='Cache.lbl_webhardFileSize'/></span>
					<select id="searchSize" class="selectType02" style="width:185px;">
						<option value=""><spring:message code='Cache.lbl_Whole'/></option> <!-- 전체 -->
						<option value="1048576">~1MB</option>
						<option value="10485760_1048576">1MB~10MB</option>
						<option value="104857600_10485760">10MB~100MB</option>
						<option value="104857600">100MB~</option>
					</select>
				</div>	
			</div>
			<div>
				<a href="#" id="btnSearch" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code='Cache.btn_search'/></a> <!-- 검색 -->
			</div>
		</div>
		<div>
			<div class="selectCalView">
				<div class="selectCalView">
				<span><spring:message code='Cache.lbl_Period'/></span> <!-- 기간 -->
					<select id="selectSearch" class="selectType02"></select>
					<div id="divCalendar" class="dateSel type02">
						<input class="adDate" type="text" id="startDate" date_separator="." readonly=""> - <input id="endDate" date_separator="." kind="twindate" date_startTargetID="startDate" class="adDate" type="text" readonly="">
					</div>											
				</div>									
			</div>
		</div>
	</div>
	<div class="boardAllCont" id="boardAllCont">
		<div class="boradTopCont" style="min-height: 45px;">
			<div class="pagingType02 buttonStyleBoxLeft">
				<a href="#" id="btnPre" class="bntPrevStep" style="display:none; margin-right:5px;"><spring:message code='Cache.lbl_Up'/></a> <!-- 위로 -->
				<a href="#" id="btnCopy" class="btnTypeDefault right"><spring:message code='Cache.btn_Copy'/></a> <!-- 복사 -->
				<a href="#" id="btnMove" class="btnTypeDefault middle"><spring:message code='Cache.btn_Move'/></a> <!-- 이동 -->
				<a href="#" id="btnDelete" class="btnTypeDefault left"><spring:message code='Cache.btn_delete'/></a> <!-- 삭제 -->
				<a href="#" id="btnAddFolder" class="btnTypeDefault" style="display:none"><spring:message code='Cache.btn_AddFolder'/></a> <!-- 폴더 추가 -->
				<a href="#" id="btnRestore" class="btnTypeDefault left" style="display:none"><spring:message code='Cache.lbl_Restore'/></a> <!-- 복원 -->
				<a href="#" id="btnDownload" class="btnTypeDefault"><spring:message code='Cache.WH_download'/></a> <!-- 다운로드 -->
				<a href="#" id="btnShare" class="btnTypeDefault" style="display:none"><spring:message code='Cache.lbl_sharing'/></a> <!-- 공유 -->
				<a href="#" id="btnLink" class="btnTypeDefault" style="display:none"><spring:message code='Cache.WH_sendLink'/></a> <!-- 링크 보내기 -->
				<a href="#" id="btnRename" class="btnTypeDefault"><spring:message code='Cache.WH_rename'/></a> <!-- 이름 바꾸기 -->
			</div>
			<div class="buttonStyleBoxRight" id="changeViewType">
				<a href="#" id="btnListView" class="btnListView listViewType01"></a>
				<a href="#" id="btnAlbumView" class="btnListView listViewType02"></a>
				<select id="selectWebhardPageSize" class="selectType02 listCount" style="display:none">
								<option value="10">10</option>
								<option value="20">20</option>
								<option value="30">30</option>
							</select>
				<button href="#" id="btnRefresh" class="btnRefresh" type="button"></button>
			</div>
		</div>
		<!-- content  -->
		<div id="view"></div>
	</div>
</div>
<input type="hidden" id="tmpUid" />