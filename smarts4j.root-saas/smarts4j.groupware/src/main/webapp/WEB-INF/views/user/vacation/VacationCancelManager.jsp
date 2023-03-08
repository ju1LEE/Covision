<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
    .AXGrid input:disabled { background-color:#Eaeaea; }
</style>
<div class='cRConTop titType'>
    <h2 class="title" id="reqTypeTxt"><spring:message code='Cache.btn_apv_vacation_req' /></h2>
    <div class="searchBox02">
        <span><input type="text" id="schUrName"/><button type="button" class="btnSearchType01" onclick="search()"><spring:message code='Cache.btn_search' /></button></span>
        <a href="#" class="btnDetails active" ><spring:message code='Cache.lbl_detail' /></a>
    </div>
</div>
<div class='cRContBottom mScrollVH'>
    <div class="inPerView type02 active">
        <div style="width: 460px;">
            <div class="selectCalView">
                <select class="selectType02" id="schYearSel">
                </select>
                <select class="selectType02" id="schTypeSel" style="display:none;">
                    <option value="displayName"><spring:message code='Cache.lbl_username' /></option>
                    <option value="deptName"><spring:message code='Cache.lbl_dept' /></option>
                </select>
                <div class="dateSel type02" id="schTxtDiv" style="display:none;">
                    <input type="text" id="schTxt">
                </div>
            </div>
            <div>
                <a href="#" class="btnTypeDefault btnSearchBlue nonHover"><spring:message code='Cache.btn_search' /></a>
            </div>
            <div class="chkStyle01">
                <span id="myVacText"></span>
            </div>
        </div>
    </div>
    <div class="boardAllCont">
        <div class="boradTopCont">
            <div class="pagingType02 buttonStyleBoxLeft" id="selectBoxDiv">
                <!-- 삭제 -->
                <a href="#" class="btnTypeDefault left" id="btnVacationDel"><spring:message code='Cache.lbl_all_together'/><spring:message code='Cache.Messaging_Cancel'/></a>
            </div>

            <div class="buttonStyleBoxRight">
                <select class="selectType02 listCount" id="listCntSel">
                    <option>10</option>
                    <option>15</option>
                    <option>20</option>
                    <option>30</option>
                    <option>50</option>
                </select>
                <button href="#" class="btnRefresh" type="button" onclick="search()"></button>
            </div>
        </div>
        <div class="tblList tblCont">
            <div id="gridDiv">
            </div>
        </div>
    </div>
</div>

<script>
    var grid = new coviGrid();
    var page = 1;
	var pageSize = CFN_GetQueryString("pageSize")== 'undefined'?10:CFN_GetQueryString("pageSize");
	
	if(CFN_GetCookie("VacListCnt")){
		pageSize = CFN_GetCookie("VacListCnt");
	}
	
	$("#listCntSel").val(pageSize);
    initContent();

    // 휴가신청, 공동연차등록, 나의휴가현황
    function initContent() {
        init();	// 초기화

        setGrid();	// 그리드 세팅

        search();	// 검색
    }

    // 초기화
    function init() {

        $("#reqTypeTxt").html("<spring:message code='Cache.MN_666' />");
        $('#schTypeSel, #schTxtDiv').css('display', '');

        var nowYear = new Date().getFullYear();
        // 년도 option 생성
        for (var i=2; i>-4;i--) {
            var temp = nowYear + i;
            if (temp == nowYear) {
                $('#schYearSel').append($('<option>', {
                    value: temp,
                    text : temp,
                    selected : 'selected'
                }));
            } else {
                $('#schYearSel').append($('<option>', {
                    value: temp,
                    text : temp
                }));
            }
        }

        $('#schUrName').on('keypress', function(e){
            if (e.which == 13) {
                e.preventDefault();
                var schName = $('#schUrName').val();

                $('#schTypeSel').val('displayName');
                $('#schTxt').val(schName);

                search();
            }
        });

        $('#schTxt').on('keypress', function(e){
            if (e.which == 13) {
                e.preventDefault();

                search();
            }
        });

        // 검색 버튼
        $('.btnSearchBlue').on('click', function(e) {
            search();
        });

        // 상세 보기
        $('.btnDetails').on('click', function() {
            var mParent = $('.inPerView');
            if(mParent.hasClass('active')){
                mParent.removeClass('active');
                $(this).removeClass('active');
            }else {
                mParent.addClass('active');
                $(this).addClass('active');
            }
        });

        // 그리드 카운트
        $('#listCntSel').on('change', function(e) {
            grid.page.pageSize = $(this).val();
            CFN_SetCookieDay("VacListCnt", $(this).find("option:selected").val(), 31536000000);
            grid.reloadList();
        });

        // 년도
        $('#schYearSel').on('change', function(e) {

        });

        $('.btnOnOff').unbind('click').on('click', function(){
            if($(this).hasClass('active')){
                $(this).removeClass('active');
                $(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').removeClass('active');
            }else {
                $(this).addClass('active');
                $(this).closest('.selOnOffBox').siblings('.selOnOffBoxChk').addClass('active');
            }
        });
        // 삭제
        $("#btnVacationDel").on('click',function(){
            //delVacation();
            if($("input[name=chk]:checked").length==0){
                Common.Warning("<spring:message code='Cache.msg_noCancelData'/>");
            }else {
                var year = $('#schYearSel').val();
                Common.open("", "target_pop", "<spring:message code='Cache.btn_apv_vacation_req' /> <spring:message code='Cache.CPMail_CancelSendAll' />", "/groupware/vacation/goVacationDeletePopup.do?year=" + year, "630px", "420px", "iframe", true, null, null, true);
            }
        });
    }

    // 그리드 세팅
    function setGrid() {
        // header
        var	headerData = null;
    	// 휴가취소처리
        headerData = [
            {key:'VacationInfoID', label:'chk', width:'30', align:'center', formatter:'checkbox', disabled: function (){
                    return Number(this.item.VacDayTot)<=0;
                }},
            {key:'DeptName', label:'<spring:message code="Cache.lbl_dept" />', width:'50', align:'center',
                formatter:function() {
                    return CFN_GetDicInfo(this.item.DeptName);
                }},
            {key:'DisplayName', label:'<spring:message code="Cache.lbl_name" />', width:'50', align:'center',
                formatter:function () {
                    var html = "<div>";
                    if (this.item.GUBUN == 'VACATION_PUBLIC' || this.item.GUBUN == 'VACATION_PUBLIC_CANCEL') {
                        html += "<a href='#' onclick='alert(\"<spring:message code='Cache.lbl_vacationMsg25' />\"); return false;'>";
                    } else if (typeof(this.item.ProcessID) == 'undefined' || typeof(this.item.WorkItemID) == 'undefined') {
                        html += "<a href='#' onclick='alert(\"<spring:message code='Cache.lbl_vacationMsg26' />\"); return false;'>";
                    } else {
                        html += "<a href='#' onclick='openVacationViewPopup(\"" + this.item.UR_Code + "\", \"" + this.item.ProcessID + "\", \"" + this.item.WorkItemID + "\"); return false;'>";
                    }
                    html += this.item.DisplayName;
                    html += "</a>";
                    html += "</div>";

                    return html;
                }
            },
            {key:'GubunName', label:'<spring:message code="Cache.lbl_Gubun" />', width:'50', align:'center',sort:false,
                formatter:function () {
                    var html = "<div>";
                    var gubunName = this.item.GubunName;
                    var btnColor = "#4abde1";
                    var btnTxt = "<spring:message code="Cache.lbl_att_approval" />";
                    var cursor = "cursor : pointer;"
                    if(parseFloat(this.item.VacDayTot)===0){
                        btnColor = "#7c7c7c";
                        btnTxt = "<spring:message code="Cache.Messaging_Cancel" />";
                        cursor = "cursor : default;"
                    }else if ((this.item.VacDay - this.item.VacDayTot)>0) {
                    	btnTxt = "<spring:message code="Cache.CPMail_Part"/>" +"<spring:message code="Cache.Messaging_Cancel" />";;
                    }
                    
                    if (this.item.GUBUN == 'VACATION_APPLY' || this.item.GUBUN == 'VACATION_PUBLIC') {
                        html += "<a class=\"WorkBoxM Calling\" style=\"background-color: "+btnColor+" !important;height: 18px;margin-top: -1px;color:#fff;"+cursor+"\""
                        if(parseFloat(this.item.VacDayTot)>0) {
                            html += " onclick='openVacationCancelPopup(\"" + this.item.VacationInfoID + "\", \"" + this.item.VacYear + "\"); return false;'";
                        }
                        html += ">";
                        html += btnTxt+"("+gubunName+")";
                        html += "</a>";
                    } else {
                        html += gubunName;
                    }
                    html += "</div>";

                    return html;
                }
            },
            {key:'VacFlagName', label:'<spring:message code="Cache.VACATION_TYPE_VACATION_TYPE" />', width:'50', align:'center',sort:false},
            {key:'APPDATE', label:'<spring:message code="Cache.lbl_apv_approvdate" />', width:'50', align:'center'},
            {key:'Sdate', label:'<spring:message code="Cache.lbl_startdate" />', width:'50', align:'center'},
            {key:'Edate', label:'<spring:message code="Cache.lbl_EndDate" />', width:'50', align:'center'},
            {key:'VacDay', label:'<spring:message code="Cache.lbl_att_approval" />', width:'50', align:'center'},
            {key:'VacDay', label:'<spring:message code="Cache.ACC_btn_cancel" />', width:'50', align:'center',sort:false,  formatter:function (){if (this.item.VacDayTot-this.item.VacDay < 0 ){return this.item.VacDayTot-this.item.VacDay}}},
            {key:'VacDayTot', label:'<spring:message code="Cache.lbl_UseVacation" />', width:'50', align:'center', sort:false},
            {key:'Reason', label:'<spring:message code="Cache.lbl_Reason" />', width:'200', align:'left', sort:false}
        ];

        grid.setGridHeader(headerData);

        // config
        var configObj = {
            targetID : "gridDiv",
            page : {
				pageNo: (page != undefined && page != '')?page:1,
				pageSize: (pageSize != undefined && pageSize != '')?pageSize:10,
			},
            height:"auto"
        };
        grid.setGridConfig(configObj);
    }

    // 검색
    function search() {
        var params = {
            urName : $('#schUrName').val(),
            year : $('#schYearSel').val(),
            schTypeSel : $('#schTypeSel').val(),
            schTxt : $('#schTxt').val(),
            reqType : "vacationCancel"
        };

        grid.page.pageNo = 1;

        // bind
        grid.bindGrid({
            ajaxUrl : "/groupware/vacation/getVacationCancelList.do",
            ajaxPars : params
        });
    }

    //근로정보 삭제
    function delVacation(){
        if($("input[name=chk]:checked").length==0){
            Common.Warning("<spring:message code='Cache.msg_selectTargetDelete'/>");
        }else{
            Common.Confirm("<spring:message code='Cache.msg_Common_08' />", "Confirmation Dialog", function (confirmResult) {
                if (confirmResult) {

                    var delArry = new Array();
                    for(var i=0;i<$("input[name=chk]:checked").length;i++){
                        delArry.push($("input[name=chk]:checked").eq(i).val());
                    }
                    var params = {
                        VacationInfoID : JSON.stringify(delArry)
                        ,year : $('#schYearSel').val()
                    };
                    $.ajax({
                        type:"POST",
                        dataType : "json",
                        data: params,
                        url:"/groupware/vacation/getVacationCancelDel.do",
                        success:function (data) {
                            if(data.status =="SUCCESS"){
                                if(data.msg!=""){
                                    Common.Warning(data.msg);
                                }
                                search();
                                Common.Close();
                            }else{
                                Common.Warning("<spring:message code='Cache.msg_sns_03'/>");/* 저장 중 오류가 발생하였습니다. */
                            }
                        }
                    });
                }
            });
        }
    }
    // 연차 사용정보
    function getUserVacationInfo() {
        $.ajax({
            type : "POST",
            data : {year : $('#schYearSel').val()},
            async: false,
            url : "/groupware/vacation/getUserVacationInfo.do",
            success:function (list) {
                var data = list.list[0];
                var text = "( <spring:message code='Cache.lbl_total' /> " + data.OWNDAYS + "<spring:message code='Cache.lbl_day' />, <spring:message code='Cache.lbl_UseVacation' /> " + data.USEDAYS + "<spring:message code='Cache.lbl_day' />, <spring:message code='Cache.lbl_RemainVacation' /> " + data.REMINDDAYS + "<spring:message code='Cache.lbl_day' /> )"

                $('#myVacText').html(text);
            },
            error:function(response, status, error) {
                CFN_ErrorAjax("/groupware/vacation/getUserVacationInfo.do", response, status, error);
            }
        });
    }

    // 휴가 신청
    function openVacationApplyPopup() {
        CFN_OpenWindow("/approval/approval_Form.do?formPrefix=WF_FORM_VACATION_REQUEST2&mode=DRAFT", "", 790, (window.screen.height - 100), "resize");
    }

    // 휴가 신청/취소 내역
    function openVacationViewPopup(urCode, processId, workItemId) {
//		CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&workitemID="+workItemId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
        CFN_OpenWindow("/approval/approval_Form.do?mode=COMPLETE&processID="+processId+"&userCode="+urCode+"&archived=true", "", 790, (window.screen.height - 100), "resize");
    }

    // 템플릿 파일 다운로드
    function excelDownload() {
        if (confirm("<spring:message code='Cache.msg_bizcard_downloadTemplateFiles' />")) {
            location.href = "/groupware/vacation/excelDownload.do";
        }
    }

    // 구분
    function openVacationCancelPopup(vacationInfoId, vacYear) {
        Common.open("","target_pop","<spring:message code='Cache.lbl_apv_vacation_cancel' />","/groupware/vacation/goVacationCancelPopup.do?vacationInfoId="+vacationInfoId+"&vacYear="+vacYear,"499px","405px","iframe",true,null,null,true);
    }

    function openPromotionPopup(formType, empType){
        var year = $('#schYearSel').val();
        var params = "?year="+year+"&viewType=user&formType=" + formType + "&empType=" + empType;

        CFN_OpenWindow("/groupware/vacation/goVacationPromotionPopup.do" + params, "", 960, (window.screen.height - 100), "scroll");
    }

    // 연차촉진제 서식출력 1,2차
    function openVacationPromotion1Popup(time) {
        var year = $('#schYearSel').val();
        CFN_OpenWindow("/groupware/vacation/goVacationPromotion1Popup.do?year="+year+"&viewType=user&isAll=N&time="+time, "", 960, (window.screen.height - 100), "scroll");
    }

    // 연차촉진제 서식출력 3
    function openVacationPromotion3Popup() {
        var year = $('#schYearSel').val();
        CFN_OpenWindow("/groupware/vacation/goVacationPromotion3Popup.do?year="+year+"&viewType=user&time=3", "", 960, (window.screen.height - 100), "scroll");
    }

    // 사용시기 지정통보서
    function openVacationUsePlanPopup(time) {
        var year = $('#schYearSel').val();
        CFN_OpenWindow("/groupware/vacation/goVacationUsePlanPopup.do?year="+year+"&time="+time+"&viewType=user&isAll=N", "", 960, (window.screen.height - 100), "scroll");
    }

</script>