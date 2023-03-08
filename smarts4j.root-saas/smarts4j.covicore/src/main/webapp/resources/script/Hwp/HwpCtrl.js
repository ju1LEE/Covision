﻿﻿﻿﻿﻿﻿﻿//* 프로그램 저작권 정보
// 이 프로그램에 대한 저작권을 포함한 지적재산권은 (주)코비젼에 있으며,
// (주)코비젼이 명시적으로 허용하지 않은 사용, 복사, 변경, 제3자에의 공개, 배포는 엄격히 금지되며,
// (주)코비젼의 지적재산권 침해에 해당됩니다.
// (Copyright ⓒ 2011 Covision Co., Ltd. All Rights Reserved)
//
// You are strictly prohibited to copy, disclose, distribute, modify, or use  this program in part or
// as a whole without the prior written consent of Covision Co., Ltd. Covision Co., Ltd.,
// owns the intellectual property rights in and to this program.
// (Copyright ⓒ 2011 Covision Co., Ltd. All Rights Reserved)

///<creator>박한(k96mi005@covision.co.kr)</creator> 
///<createDate>2013.02.01</createDate> 
///<lastModifyDate>2014.06.26</lastModifyDate> 
///<version>1.1.0</version>
///<summary> 
///
///</summary>
///<ModifySpc>
///2014.06.26 ( 임소희 shlim@covision.co.kr) : Copyright, ID Block 추가(작성자 Admin -> 박한으로 변경)
///</ModifySpc>
//*/
var l_Clsid = "BD9C32DE-3155-4691-8972-097D53B10052";
//var l_Version = "3,5,1,2002";
//var l_Cab = "/WebSite/Common/ExControls/TagFree/tweditor.cab";
//var l_Applyinitdata = 1; //apply:1
//var l_Editmode = 0; //edit:0
var l_Env = "/WebSite/Common/ExControls/TagFree/";


//[2012-07-26 add] admin page chk variable S --------------
var isAdmPage = 'N';
if ("undefined" != typeof (gIsAdmPage)) {
    isAdmPage = gIsAdmPage;
}
//[2012-07-26 add] admin page chk variable E --------------

switch (g_editorTollBar) {
    case "0":
        l_Env += "env";
        break;
    case "1":
        l_Env += "env_type1";
        break;
    case "2":
        l_Env += "env_type2";
        break;
}
//l_Env += "." + Common.GetSession("LanguageCode") + ".xml";
//var l_Key = "a4GM+XmkJTIyvn3ztfUcA9jCi1w4QBfJppdx/7SeUsWqHhmyb4KhnqE1tzmRmENYKvB0ASNfwmWWHO7ebgWDeuPsRlNbhVACR3CJrLz6BJIf1bb20B9nzV7Qugb2WOJIFYlJQiwhMpUhsf0G+51I8BJceyFomxek4LK08DzmdtkK3svey9MZGpLIzKMXrDfe162370Rjpjwvjr4o/PrfeA==";
//var l_Key = "3pMv/aEABvR/C7QIaROt8PJXj7uDg+MayFCdqD3e0zSUGl6ht4/3PmvguNSKBCHq+3quRA9twYI7oLoi8kQVR9i4ooT/8FV3Jd6/mPWnMJAphhDGdYnRAwYgU+X0s14HKc9AEKWgantq+xYvnsH4dKdw2opzdHe8/aXdMe6RqXQVZCQDICqS9sfHJebgz9FFSQr6+FFVw1n1+lFZTtrA78qACT38wU4x5bbIyc7dFa8WLsT8eUmwfLOgubwGGPTJX5APKqKgQAllJT+GPPDR2Q==";

function LoadtweditorForApproval() {
    try {
        if (getInfo("Request.templatemode") != "Write") {
            g_heigth = "1000";
        }
    } catch (e) {
        g_heigth = "1000";
    }

    var oHtml = '';
    if (g_id == "tbContentElement") {
        oHtml += '<table class="table_4" summary="Contents" cellpadding="0" cellspacing="0" id="tbEditer">';

        //[2012-07-25 add] add html control S ----------------------------------
        if ("Y" == isAdmPage) {
            oHtml += '    <tr id="trControl"style="display:none;">';
            oHtml += '        <td width="100%" height="30px" style="vertical-align:middle;">';
            oHtml += 'Add Control : ';
            oHtml += '<a href=\'javascript:AddControl("text");\'><img src="/images/images/approval/editor_text.png" alt="TextBox" title="TextBox" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("text");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '<a href=\'javascript:AddControl("check");\'><img src="/images/images/approval/editor_check.png" alt="CheckBox" title="CheckBox" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("check");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '<a href=\'javascript:AddControl("radio");\'><img src="/images/images/approval/editor_radio.png" alt="RadioButton" title="RadioButton" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("radio");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '<a href=\'javascript:AddControl("select");\'><img src="/images/images/approval/editor_combo.png" alt="SelectBox" title="SelectBox" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("select");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '        </td>';
            oHtml += '    </tr>	';
        }
        //[2012-07-25 add] add html control E ----------------------------------

        oHtml += '    <tr>';
        oHtml += '        <td width="100%" height="362px">';
    }
    oHtml += '<object ID="' + g_id + '" width="100%" height="' + g_heigth + 'px" CLASSID="CLSID:' + l_Clsid + '" >';
    oHtml += '<PARAM name="_Version" value="65537"/>';
    oHtml += '<PARAM name="_ExtentX" value="24950"/>';
    oHtml += '<PARAM name="_ExtentY" value="9234"/>';
    oHtml += '<PARAM name="_StockProps" value="0"/>';
    oHtml += '<PARAM name="FILENAME" value=""/>';
    oHtml += '<PARAM name="TOOLBAR_MENU" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_STANDARD" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_FORMAT" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_DRAW" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_TABLE" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_IMAGE" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_HEADER_FOOTER" value="0"/>';
    oHtml += '<PARAM name="SHOW_TOOLBAR" value="0"/>';
    oHtml += '<PARAM name="SHOW_STATUSBAR" value="0"/>';
    oHtml += '</object>';

    if (g_id == "tbContentElement") {
        oHtml += '        </td>';
        oHtml += '    </tr>	';
        //oHtml += '    <tr>';
        //oHtml += '        <td  style="height:30px; background:#f3f3f3; text-align:center;">';
        //oHtml += '            <table class="editor_size" cellpadding="0" cellspacing="0">';
        //oHtml += '			  <tr>';
        //oHtml += '                <td style="height:11px; text-align:center;"><a href=\'javascript:PlusMinusEditorHeight("minus");\'><img src="/images/images/approval/editor_minus.gif" width="25" alt="minus" title="minus" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>';
        //oHtml += '                <td style="height:11px; text-align:center;"><a href=\'javascript:PlusMinusEditorHeight("plus");\'><img src="/images/images/approval/editor_plus.gif" width="25" alt="plus" title="plus" /></a></td></tr>';
        //oHtml += '            </table>';
        //oHtml += '        </td>';
        //oHtml += '    </tr>	';
        oHtml += '</table>';
    }    
    return oHtml;
}

function LoadtweditorForApprovalMulti(idx, doctype) {
    try {
        if (getInfo("Request.templatemode") != "Write") {
            g_heigth = "1000";
        }
    } catch (e) {
        g_heigth = "1000";
    }
    var oHtml = '';
    if (g_id == "tbContentElement"+idx) {
        oHtml += '<table class="table_4" summary="Contents" cellpadding="0" cellspacing="0" id="tbEditer"' + idx + '">';

        //[2012-07-25 add] add html control S ----------------------------------
        if ("Y" == isAdmPage) {
            oHtml += '    <tr id="trControl' + '"style="display:none;">';
            oHtml += '        <td width="100%" height="30px" style="vertical-align:middle;">';
            oHtml += 'Add Control : ';
            oHtml += '<a href=\'javascript:AddControl_Multi("text", ' + idx + ');\'><img src="/images/images/approval/editor_text.png" alt="TextBox" title="TextBox" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("text");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '<a href=\'javascript:AddControl_Multi("check", ' + idx + ');\'><img src="/images/images/approval/editor_check.png" alt="CheckBox" title="CheckBox" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("check");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '<a href=\'javascript:AddControl_Multi("radio", ' + idx + ');\'><img src="/images/images/approval/editor_radio.png" alt="RadioButton" title="RadioButton" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("radio");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '<a href=\'javascript:AddControl_Multi("select", ' + idx + ');\'><img src="/images/images/approval/editor_combo.png" alt="SelectBox" title="SelectBox" /></a>';
            oHtml += '<a href=\'javascript:SetControlOption("select");\'><strong>[O]</strong>&nbsp;&nbsp;';
            oHtml += '        </td>';
            oHtml += '    </tr>	';
        }
        //[2012-07-25 add] add html control E ----------------------------------

        oHtml += '    <tr>';
        oHtml += '        <td width="100%" height="362px">';
    }
    oHtml += '<object ID="' + (g_id + idx) + '" width="100%" height="' + g_heigth + 'px" doctype="' + doctype + '" CLASSID="CLSID:' + l_Clsid + '" >';
    oHtml += '<PARAM name="_Version" value="65537"/>';
    oHtml += '<PARAM name="_ExtentX" value="24950"/>';
    oHtml += '<PARAM name="_ExtentY" value="9234"/>';
    oHtml += '<PARAM name="_StockProps" value="0"/>';
    oHtml += '<PARAM name="FILENAME" value=""/>';
    oHtml += '<PARAM name="TOOLBAR_MENU" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_STANDARD" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_FORMAT" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_DRAW" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_TABLE" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_IMAGE" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_HEADER_FOOTER" value="0"/>';
    oHtml += '<PARAM name="SHOW_TOOLBAR" value="0"/>';
    oHtml += '<PARAM name="SHOW_STATUSBAR" value="0"/>';
    oHtml += '</object>';

    if (g_id == "tbContentElement"+idx) {
        oHtml += '        </td>';
        oHtml += '    </tr>	';
        //oHtml += '    <tr>';
        //oHtml += '        <td  style="height:30px; background:#f3f3f3; text-align:center;">';
        //oHtml += '            <table class="editor_size" cellpadding="0" cellspacing="0">';
        //oHtml += '			  <tr>';
        //oHtml += '                <tr><td style="height:11px; text-align:center;"><a href=\'javascript:PlusMinusEditorHeightMulti("minus", ' + ');\'><img src="/images/images/approval/editor_minus.gif" width="25" alt="minus" title="minus" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>';
        //oHtml += '                <td style="height:11px; text-align:center;"><a href=\'javascript:PlusMinusEditorHeightMulti("plus", ' + ');\'><img src="/images/images/approval/editor_plus.gif" width="25" alt="plus" title="plus" /></a></td></tr>';
        //oHtml += '            </table>';
        //oHtml += '        </td>';
        //oHtml += '    </tr>	';
        oHtml += '</table>';
    }
    return oHtml;
}


//편집기 높이조정
var ntbContentElementHeight = 600;
if (document.tbContentElement != undefined) {
    ntbContentElementHeight = parseInt(document.tbContentElement.height);
}
function PlusMinusEditorHeight(pMode) {
    if ((pMode == "plus" && document.tbContentElement.height >= parseInt(g_heigth)) || (pMode == "minus" && document.tbContentElement.height > parseInt(g_heigth))) {
        if (pMode == "minus") {
            if (parseInt(document.tbContentElement.height) + -parseInt(200) >= ntbContentElementHeight - 600) document.tbContentElement.height = parseInt(document.tbContentElement.height) + -parseInt(200);
        } else {
            if (parseInt(document.tbContentElement.height) + parseInt(200) <= ntbContentElementHeight + 600) document.tbContentElement.height = parseInt(document.tbContentElement.height) + parseInt(200);
        }
        try { cTagreSize(); } catch (e) { } //design조정
    }
}

function PlusMinusEditorHeightMulti(pMode, idx) {
    var objEditor = $("#tbContentElement"+idx);
    if ((pMode == "plus" && $(objEditor)[0].height >= parseInt(g_heigth)) || (pMode == "minus" && $(objEditor)[0].height > parseInt(g_heigth))) {
        if (pMode == "minus") {
            if (parseInt($(objEditor)[0].height) + -parseInt(200) >= ntbContentElementHeight - 600) $(objEditor)[0].height = parseInt($(objEditor)[0].height) + -parseInt(200);
        } else {
            if (parseInt($(objEditor)[0].height) + parseInt(200) <= ntbContentElementHeight + 600) $(objEditor)[0].height = parseInt($(objEditor)[0].height) + parseInt(200);
        }
        try { cTagreSize(); } catch (e) { } //design조정
    }
}

//[2012-07-25 추가] 에디터에 컨트롤 추가 S ------------------------------------------------
var idxControl = 0;
var firstLoad = true;
function AddControl(argContNm) {
    var twe = document.tbContentElement;       //editor object
    var insertTag = '';                        //html control tag string
    var optionAttr = '';                       //html control option tag
    var optionFont = '';                       //check box and radio button text style, class

    //고유한 name 부여를 위한 index hidden value
    var editorForm = twe.GetDOM();
    if (null != editorForm.getElementById("hdnIdxCnt")) {
        var tmpIdx = editorForm.getElementById("hdnIdxCnt").value;
        idxControl = parseInt(tmpIdx);
        idxControl++;

        editorForm.getElementById("hdnIdxCnt").value = idxControl.toString();
    }
    else {
        twe.InsertHtml('<input type="hidden" id="hdnIdxCnt" value="1" />');
        idxControl++;
    }

    //해당 컨트롤 추가
    var arrTxtOption = new Array();
    if ("text" == argContNm) {         //Text Box
        arrTxtOption[0] = $('#hdnHtmlControlOptVal_0').val();  //width
        arrTxtOption[1] = $('#hdnHtmlControlOptVal_1').val();  //Height
        arrTxtOption[2] = $('#hdnHtmlControlOptVal_2').val();  //MultiLine
        arrTxtOption[3] = $('#hdnHtmlControlOptVal_3').val();  //Default Value
        arrTxtOption[4] = $('#hdnHtmlControlOptVal_4').val();  //class
        arrTxtOption[5] = $('#hdnHtmlControlOptVal_5').val();  //style

        optionAttr = ' style="width:' + arrTxtOption[0] + 'px; ';
        optionAttr += 'height:' + arrTxtOption[1] + 'px; ';
        optionAttr += arrTxtOption[5] + '"';
        optionAttr += ' class="' + arrTxtOption[4] + '"';

        if ('false' == arrTxtOption[2]) {  //TextBox
            optionAttr += ' value="' + arrTxtOption[3] + '"';
            insertTag = '<input type="text" id="mField" name="editorTxt_' + idxControl.toString() + '" ' + optionAttr + ' />';
        }
        else {                             //textarea
            insertTag = '<textarea id="mField" name="editorTxt_' + idxControl.toString() + '" ' + optionAttr + ' >' + arrTxtOption[3] + '</textarea>';
        }
    }
    else if ("check" == argContNm) {   //Check Box
        arrTxtOption[0] = $('#hdnChkOptVal_0').val();  //checked
        arrTxtOption[1] = $('#hdnChkOptVal_1').val();  //class
        arrTxtOption[2] = $('#hdnChkOptVal_2').val();  //style

        if ("true" == arrTxtOption[0]) {
            optionAttr = ' checked';
        }

        optionFont = ' class="' + arrTxtOption[1] + '" ';
        optionFont += ' style="' + arrTxtOption[2] + '" ';

        insertTag = '<input class="input_check" type="checkbox" id="mField" value="' + idxControl.toString() + '" name="editorChk_' + idxControl.toString() + '" ' + optionAttr + ' />';
        insertTag += '<span ' + optionFont + '>Chk_1</span>';
    }
    else if ("radio" == argContNm) {   //Radio Button
        arrTxtOption[0] = $('#hdnRdoOptVal_0').val();  //checked
        arrTxtOption[1] = $('#hdnRdoOptVal_1').val();  //class
        arrTxtOption[2] = $('#hdnRdoOptVal_2').val();  //style

        if ("true" == arrTxtOption[0]) {
            optionAttr = ' checked';
        }

        optionFont = ' class="' + arrTxtOption[1] + '" ';
        optionFont += ' style="' + arrTxtOption[2] + '" ';

        insertTag = '<input class="input_check" type="radio" id="mField" value="' + idxControl.toString() + '" name="editorRdo_' + idxControl.toString() + '" ' + optionAttr + ' />';
        insertTag += '<span ' + optionFont + '>Rdo_1</span>';
    }
    else if ("select" == argContNm) {  //Select Box
        arrTxtOption[0] = $('#hdnSelOptVal_0').val();  //width
        arrTxtOption[1] = $('#hdnSelOptVal_1').val();  //key
        arrTxtOption[2] = $('#hdnSelOptVal_2').val();  //value

        var arrKey = new Array();
        var arrVal = new Array();

        if (-1 != arrTxtOption[1].indexOf('[|^|]') && -1 != arrTxtOption[2].indexOf('[|^|]')) {
            arrKey = arrTxtOption[1].split('[|^|]');
            arrVal = arrTxtOption[2].split('[|^|]');
        }
        else if ('' != arrTxtOption[1] && '' != arrTxtOption[2]) {
            arrKey[0] = arrTxtOption[1];
            arrVal[0] = arrTxtOption[2];
        }

        if ('' != arrTxtOption[0]) {
            optionAttr = ' style="width:' + arrTxtOption[0] + '; "';
        }

        insertTag = '<select id="mField" name="editorCbo_' + idxControl.toString() + '" ' + optionAttr + ' >';

        if (0 < arrKey.length) {
            insertTag += '<option value="0">Choose Item</option>';

            for (var i = 0; i < arrKey.length; i++) {
                if ('' != arrVal[i] && '' != arrKey[i]) {
                    insertTag += '<option value="' + arrVal[i] + '" >' + arrKey[i] + '</option>';
                }
            }
        }
        else {
            insertTag += '<option value="0">No Data</option>';
        }


        insertTag += '</select>';
    }

    arrTxtOption = null;
    twe.InsertHtml(insertTag);
    twe.focus();
}


function AddControl_Multi(argContNm, idx) {
    var twe = document.tbContentElement+idx;       //editor object
    var insertTag = '';                        //html control tag string
    var optionAttr = '';                       //html control option tag
    var optionFont = '';                       //check box and radio button text style, class

    //고유한 name 부여를 위한 index hidden value
    var editorForm = twe.GetDOM();
    if (null != editorForm.getElementById("hdnIdxCnt")) {
        var tmpIdx = editorForm.getElementById("hdnIdxCnt").value;
        idxControl = parseInt(tmpIdx);
        idxControl++;

        editorForm.getElementById("hdnIdxCnt").value = idxControl.toString();
    }
    else {
        twe.InsertHtml('<input type="hidden" id="hdnIdxCnt" value="1" />');
        idxControl++;
    }

    //해당 컨트롤 추가
    var arrTxtOption = new Array();
    if ("text" == argContNm) {         //Text Box
        arrTxtOption[0] = $('#hdnHtmlControlOptVal_0').val();  //width
        arrTxtOption[1] = $('#hdnHtmlControlOptVal_1').val();  //Height
        arrTxtOption[2] = $('#hdnHtmlControlOptVal_2').val();  //MultiLine
        arrTxtOption[3] = $('#hdnHtmlControlOptVal_3').val();  //Default Value
        arrTxtOption[4] = $('#hdnHtmlControlOptVal_4').val();  //class
        arrTxtOption[5] = $('#hdnHtmlControlOptVal_5').val();  //style

        optionAttr = ' style="width:' + arrTxtOption[0] + 'px; ';
        optionAttr += 'height:' + arrTxtOption[1] + 'px; ';
        optionAttr += arrTxtOption[5] + '"';
        optionAttr += ' class="' + arrTxtOption[4] + '"';

        if ('false' == arrTxtOption[2]) {  //TextBox
            optionAttr += ' value="' + arrTxtOption[3] + '"';
            insertTag = '<input type="text" id="mField" name="editorTxt_' + idxControl.toString() + '" ' + optionAttr + ' />';
        }
        else {                             //textarea
            insertTag = '<textarea id="mField" name="editorTxt_' + idxControl.toString() + '" ' + optionAttr + ' >' + arrTxtOption[3] + '</textarea>';
        }
    }
    else if ("check" == argContNm) {   //Check Box
        arrTxtOption[0] = $('#hdnChkOptVal_0').val();  //checked
        arrTxtOption[1] = $('#hdnChkOptVal_1').val();  //class
        arrTxtOption[2] = $('#hdnChkOptVal_2').val();  //style

        if ("true" == arrTxtOption[0]) {
            optionAttr = ' checked';
        }

        optionFont = ' class="' + arrTxtOption[1] + '" ';
        optionFont += ' style="' + arrTxtOption[2] + '" ';

        insertTag = '<input class="input_check" type="checkbox" id="mField" value="' + idxControl.toString() + '" name="editorChk_' + idxControl.toString() + '" ' + optionAttr + ' />';
        insertTag += '<span ' + optionFont + '>Chk_1</span>';
    }
    else if ("radio" == argContNm) {   //Radio Button
        arrTxtOption[0] = $('#hdnRdoOptVal_0').val();  //checked
        arrTxtOption[1] = $('#hdnRdoOptVal_1').val();  //class
        arrTxtOption[2] = $('#hdnRdoOptVal_2').val();  //style

        if ("true" == arrTxtOption[0]) {
            optionAttr = ' checked';
        }

        optionFont = ' class="' + arrTxtOption[1] + '" ';
        optionFont += ' style="' + arrTxtOption[2] + '" ';

        insertTag = '<input class="input_check" type="radio" id="mField" value="' + idxControl.toString() + '" name="editorRdo_' + idxControl.toString() + '" ' + optionAttr + ' />';
        insertTag += '<span ' + optionFont + '>Rdo_1</span>';
    }
    else if ("select" == argContNm) {  //Select Box
        arrTxtOption[0] = $('#hdnSelOptVal_0').val();  //width
        arrTxtOption[1] = $('#hdnSelOptVal_1').val();  //key
        arrTxtOption[2] = $('#hdnSelOptVal_2').val();  //value

        var arrKey = new Array();
        var arrVal = new Array();

        if (-1 != arrTxtOption[1].indexOf('[|^|]') && -1 != arrTxtOption[2].indexOf('[|^|]')) {
            arrKey = arrTxtOption[1].split('[|^|]');
            arrVal = arrTxtOption[2].split('[|^|]');
        }
        else if ('' != arrTxtOption[1] && '' != arrTxtOption[2]) {
            arrKey[0] = arrTxtOption[1];
            arrVal[0] = arrTxtOption[2];
        }

        if ('' != arrTxtOption[0]) {
            optionAttr = ' style="width:' + arrTxtOption[0] + '; "';
        }

        insertTag = '<select id="mField" name="editorCbo_' + idxControl.toString() + '" ' + optionAttr + ' >';

        if (0 < arrKey.length) {
            insertTag += '<option value="0">Choose Item</option>';

            for (var i = 0; i < arrKey.length; i++) {
                if ('' != arrVal[i] && '' != arrKey[i]) {
                    insertTag += '<option value="' + arrVal[i] + '" >' + arrKey[i] + '</option>';
                }
            }
        }
        else {
            insertTag += '<option value="0">No Data</option>';
        }


        insertTag += '</select>';
    }

    arrTxtOption = null;
    twe.InsertHtml(insertTag);
    twe.focus();
}

//[2012-07-31 add] Html Control 추가 option 창 popup
function SetControlOption(argType) {
    var l_url = "HtmlControlOption.aspx?cType=" + argType;
    var divHeight = "200px";
    var divWidth = "370px";

    if ("text" == argType) {         //Text Box
        divHeight = "290px";
    }
    else if ("check" == argType) {   //Check Box
        //설정 항목이 늘어나면 높이 변경
    }
    else if ("radio" == argType) {   //Radio Button
        //설정 항목이 늘어나면 높이 변경
    }
    else if ("select" == argType) {  //Select Box
        divHeight = "350px";
        divWidth = "390px";
    }

    Common.showDialog("", "divHtmlControlOption", "Select Control Option", l_url, divWidth, divHeight, "iframe");
}
//[2012-07-25 추가] 에디터에 컨트롤 추가 E ------------------------------------------------

function LoadEditorHWP(elm) {
	if(CFN_GetQueryString("isMobile") == "Y" || _mobile) {
		setEditorHWP();
	} else if(getInfo("ExtInfo.UseWebHWPEditYN") == "Y") { //웹한글기안기
    	coviEditor.loadEditor(
    			elm,
    			{
    				editorType : "webhwpctrl",
    				containerID : g_id,
    				frameHeight : '600',
    				focusObjID : '',
    				doctype : '',
    				onLoad: function(){
    					setEditorHWP();
    				}
    			}
    	);
    } else { //한글 Active X
	    if (_ie) {
	        $('#' + elm).html(LoadtweditorForApproval())
				.append('<script language="javascript" for="' + g_id + '" event="OnControlInit">setTimeout("setEditorHWP()", 1000);</script>');
	    }
	    else {
	    	Common.Warning("아래한글 에디터는 ie에서만 지원합니다. ie로 접속해주세요.");
	    }
    }
}

function LoadEditorHWP_Multi_old(elm, idx) {
    if (_ie) {
        $('#' + elm).html(LoadtweditorForApprovalMulti(idx))
			.append('<script language="javascript" for="' + g_id+idx + '" event="OnControlInit">setTimeout("setEditorHWP_Multi(' + idx + ')", 1000);</script>');
    }
    else {
    	Common.Warning("아래한글 에디터는 ie에서만 지원합니다. ie로 접속해주세요.");
    }
}

function LoadEditorHWP_Multi(elm, idx, templateFile, doctype) {
	if(templateFile == null) templateFile = "draft_Template";
	if(doctype == null) doctype = "normal";
	var rowSeq ="";
	
	// 다안기안 + 문서유통일때 QueryString에 rowSeq 가 있는 경우에는 해당 안건만 에디터에 로드 시킨다.
	// => rowSeq 가 있다는건 수신부서로 넘어갔거나 문서유통 발송함으로 간 경우이므로 다른 안건을 같이 볼 필요가 없다.
	if (isGovMulti()) {
		    rowSeq = CFN_GetQueryString('rowSeq') != '' && CFN_GetQueryString('rowSeq') != 'undefined' ? CFN_GetQueryString('rowSeq') : '0';
		// rowSeq 가 있을때만 체크
		if (rowSeq != '0') {
			// rowSeq 값과 MULTI_ROWSEQ 를 비교하여 같은 안일때만 load 한다.
			//if (formJson.BodyData.SubTable1[idx-1] != null && formJson.BodyData.SubTable1[idx-1].MULTI_ROWSEQ != null && rowSeq == formJson.BodyData.SubTable1[idx-1].MULTI_ROWSEQ) {
			if (formJson.BodyData.SubTable1[idx-1] != null && formJson.BodyData.SubTable1[idx-1].MULTI_ROWSEQ != null ) {
			} else {
				return;
			}
    	}
    	
    	// 한글 웹 에디터 로드 시작
		setGovMultiEditorLoadStart(idx);
	}
	
	if(getInfo("ExtInfo.UseWebHWPEditYN") == "Y") { //웹한글기안기
		if(rowSeq !='0'){//선택 안만 보여주는 경우
	    	coviEditor.loadEditor(
	    			elm,
	    			{
	    				editorType : "webhwpctrl",
	    				containerID : g_id+idx,
	    				frameHeight : '600',
	    				focusObjID : '',
	    				doctype : doctype,
	    				onLoad: function(){
	    					setEditorHWP_Multi(rowSeq, templateFile);
	    				}
	    			}
	    	);
		}
		else if(idx=="1"){//1안만 에디터 load
	    	coviEditor.loadEditor(
	    			elm,
	    			{
	    				editorType : "webhwpctrl",
	    				containerID : g_id+idx,
	    				frameHeight : '600',
	    				focusObjID : '',
	    				doctype : doctype,
	    				onLoad: function(){
	    					setEditorHWP_Multi(idx, templateFile);
	    				}
	    			}
	    	);
		}		
		else{
			setEditorHWP_Multi(idx, templateFile);
		}
    } else { //한글 Active X
	    if (_ie) {
	        $('#' + elm).html(LoadtweditorForApprovalMulti(idx, doctype))
	        	.append("<script language='javascript' for='" + g_id+idx + "' event='OnControlInit'>setTimeout('setEditorHWP_Multi(" + idx + ", \"" + templateFile + "\")', 10);</script>");
	    }
	    else {
	    	Common.Warning("아래한글 에디터는 ie에서만 지원합니다. ie로 접속해주세요.");
	    }
	    setTimeout("drawApvLineHWP()", 1000); // 템플릿 편집
    }
}

function LoadEditorHWP_Multi_Audit(elm, idx, templateFile) {
    if (_ie) {
        $('#' + elm).html(LoadtweditorForApprovalMulti(idx))
			.append("<script language='javascript' for='" + g_id+idx + "' event='OnControlInit'>setTimeout('setEditorHWP_Multi_Audit(" + idx + ", \"" + templateFile + "\")', 10);</script>");
    }
    else {
        //$('#' + elm).append('<div id="hDivEditor" style="width: 100%; height:100%;"></div>')
		//	.append('<iframe id="xFreeFrame" src="/WebSite/Approval/Forms/Templates/common/XFree.html" marginwidth="0" frameborder="0" scrolling="no" ></iframe>');
    	Common.Warning("아래한글 에디터는 ie에서만 지원합니다. ie로 접속해주세요.");
    }
}

var HwpCtrl;
var webHwpYN;
var HwpYN;
// editor data 처리를 위한 함수 추가
function setEditorHWP() {
    var dom;
    
    webHwpYN = getInfo("ExtInfo.UseWebHWPEditYN");
    HwpYN = getInfo("ExtInfo.UseHWPEditYN");
    
    if(CFN_GetQueryString("isMobile") == "Y" || _mobile) {
    	webHwpYN = "N";
    	SetBodyGovReceiveMobile();
    } else if (getInfo("Request.templatemode") == "Write") { //쓰기 모드인 경우

        HwpCtrl = document.getElementById(g_id);
        if(HwpCtrl == null && webHwpYN == "Y") {
        	HwpCtrl = document.getElementById(g_id + 'Frame').contentWindow.HwpCtrl;
        }

        HwpCtrl.SetClientName("DEBUG");
        if(webHwpYN != "Y") {
        	InitToolBarJS();
        }
        if (_ie || webHwpYN == "Y") {
            if (getInfo("BodyContext") != undefined && getInfo("BodyContext") != "{}") {     //기안,임시저장으로 저장된 값 setting
                if (typeof formJson.BodyContext != 'undefined') {
                	if (getInfo("ExtInfo.UseWebHWPEditYN") == "Y" && getInfo("SchemaContext.scDistribution.isUse") == "Y") {
//                    	var strHTML = formJson.BodyContext.tbContentElement;
//                    	HwpCtrl.SetTextFile(Base64.b64_to_utf8(strHTML), "HTML", "");
//                    	drawApvLineHWP();
                		var strBody = Base64.b64_to_utf8(formJson.BodyContext.BODY_CONTEXT_HWP);
                    	HwpCtrl.SetTextFile(strBody, "HWPML2X", "", function (result) {
                    		drawApvLineHWP();
                    	});
                    } else if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI") {
                        /*var dataObj;
                        
                        if (formJson.BodyData != null && formJson.BodyData.SubTable1 != null && formJson.BodyData.SubTable1 != "") {
                            dataObj = removeSeperatorForMultiRow(formJson.BodyData.SubTable1);
                            if (dataObj != null) {
                                var strHWP = $(dataObj).eq(idx)[0].ST_HWPDATA;
                                SetHwpEditMode(HwpCtrl, "edit");
                                SetBody(HwpCtrl, strHWP, "draft", 0, dataObj);
                                SetHwpEditMode(HwpCtrl, "form");
                            }
                        }*/
	                	var dataObj = formJson.BodyContext;
	                    var strHWP = dataObj.ST_HWPDATA;
	                    
	                    SetHwpEditMode(HwpCtrl, "edit");
	                    SetBody(HwpCtrl, strHWP, "draft", 0, dataObj);
	                    SetHwpEditMode(HwpCtrl, "form");
                	} else if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP") {
                		if(formJson.BodyContext.BODY_CONTEXT_HWP != undefined) {
                			//var strHWP = decodeJsonObj(formJson.BodyContext.BODY_CONTEXT_HWP);
                			var strHWP = formJson.BodyContext.BODY_CONTEXT_HWP;
	
	                        if (strHWP == "") {
	                        	var strHTML = formJson.BodyContext.BODY_CONTEXT_HTML;
	                        	if(strHTML != "" && webHwpYN == "Y") {
		                            HwpCtrl.SetTextFile(Base64.b64_to_utf8(strHTML), "HTML", "");
	                        	} else {
		                            strHWP = formJson.BodyContext.tbContentElement["#cdata-section"].replace(/<br \/>/gi, "").replace(/\\n/gi, "").replace("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />", "");
		                            
		                            strHWP = strHWP.replace("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">", "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
		                            strHWP = strHWP.replace("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />", "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
		                            strHWP = strHWP.replace("<style title=\"__xfree_default\" type=\"text/css\">", "<style type=\"text/css\">");
		                            strHWP = strHWP.replace("<link href=\"http://gw.hit.ac.kr/WebSite/Common/ExControls/TagFree/tagfree.css\" rel=\"stylesheet\" />", "");
		                            strHWP = strHWP.replace("<meta http-equiv=\"X-UA-Compatible\" content=\"IE =5\" />", "");
		                            strHWP = strHWP.replace("<meta content=\"TAGFREE active designer\" name=\"GENERATOR\" />", "<meta name=\"Generator\" content=\"Hancom HWP 8.5.8.1401\">");
		                            
		                            HwpCtrl.SetTextFile(strHWP, "HTML", "");
	                        	}
	                        } else {
	                            HwpCtrl.SetTextFile(strHWP, "HWP", "");
	    	                	var dataObj = formJson.BodyContext;
	    	                    
	    	                    SetHwpEditMode(HwpCtrl, "edit");
	    	                    //SetBody(HwpCtrl, strHWP, "draft_external", 0, dataObj);
	    	                    //SetHwpEditMode(HwpCtrl, "form");
	                        }
                		}
                    } else if(getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
						var strBody = Base64.b64_to_utf8(formJson.BodyContext.BODY_CONTEXT_HWP);
                    	HwpCtrl.SetTextFile(strBody, "HWPML2X", "", "");
					} else {
						var strBody = formJson.BodyContext.tbContentElement;
					}
                }
            } else {
            	if (getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
            		// HwpCtrl.SetTextFile(Base64.b64_to_utf8(getInfo("FormInfo.BodyDefault")), "HTML", "");
            		SetBody(HwpCtrl, getInfo("BodyData"), "draft_Template", 0, null);
            		SetHwpEditMode(HwpCtrl, "form");
            	}
            	if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI") { //양식 생성 시 입력한 본문내역 조회  
	                SetHwpEditMode(HwpCtrl, "edit");
	                SetBody(HwpCtrl, atob(getInfo("FormInfo.BodyDefault")), "draft", 0, null);
	                SetHwpEditMode(HwpCtrl, "form");
            	}
            }

            try { G_displaySpnDocLinkInfo(); } catch (e) { }
            
        } else {
            if (getInfo("BodyContext") != undefined) {     //기안,임시저장으로 저장된 값 setting
                dom = document.tbContentElement.getDom();
                document.getElementById('tbContentElement').HtmlValue = formJson.BodyContext.tbContentElement["#cdata-section"].replace(/<br \/>/gi, "").replace(/\\n/gi, "");
                try { G_displaySpnDocLinkInfo(); } catch (e) { }

            } else {//양식 생성 시 입력한 본문내역 조회            
                if (getInfo("BodyData") != undefined) {
                    try {
                        var dom = document.tbContentElement.getDom();
                        document.getElementById('tbContentElement').HtmlValue = getInfo("BodyData").replace("euc-kr", "utf-8").replace(/\\n/gi, "");
                    } catch (e) { }
                }
            }
        }

    	/*var vp = HwpCtrl.CreateSet("ViewProperties");
    	vp.SetItem("ZoomType", 2);
    	HwpCtrl.ViewProperties = vp;*/
        
    } else {// 읽기 모드인 경우
        HwpCtrl = document.getElementById(g_id);
        if(HwpCtrl == null && webHwpYN == "Y") {
        	HwpCtrl = document.getElementById(g_id + 'Frame').contentWindow.HwpCtrl;
        }

        HwpCtrl.SetClientName("DEBUG");
        if(webHwpYN != "Y") {
        	InitToolBarJS_Read();
        }

        if(typeof formJson.BodyContext["BODY_CONTEXT_HWP"] == "undefined" && typeof formJson.BodyContext["pubdocinfo"] != 'undefined') {
        	SetBody(HwpCtrl, null, "draft_Template", 0, null);
        	SetHwpEditMode(HwpCtrl, "read");
        } else if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI") {
	    	var dataObj = formJson.BodyContext;
	        var strHWP = dataObj.ST_HWPDATA;
	        
	        //SetHwpEditMode(HwpCtrl, "edit");
	        SetBody(HwpCtrl, strHWP, "draft", 0, dataObj);
	        SetHwpEditMode(HwpCtrl, "read");
        } else { 
        	var strHWP = "";
        	var strHTML = "";

            if (typeof formJson.BodyContext["tbContentElement"]["#cdata-section"] != 'undefined') {
                strHWP = formJson.BodyContext["tbContentElement"]["#cdata-section"];
            }
            else if (typeof formJson.BodyContext["tbContentElement"] != 'undefined') {
                strHWP = formJson.BodyContext["tbContentElement"];
                strHTML = formJson.BodyContext["tbContentElement"];
            }
            else {
                strHWP = "error at setEditor().";
            }
            
            if (typeof formJson.BodyContext.BODY_CONTEXT_HWP != 'undefined') {
                strHWP = formJson.BodyContext.BODY_CONTEXT_HWP;
            }

            if (typeof formJson.BodyContext.BODY_CONTEXT_HTML != 'undefined') {
                strHTML = formJson.BodyContext.BODY_CONTEXT_HTML;
            }

	    	var dataObj = formJson.BodyContext;
	        
	        //SetHwpEditMode(HwpCtrl, "form");
	        
	        // 양식프로세스 > 결재유형옵션 > 문서유통 == Y && 양식 > 양식본문정보 > 한글웹기안기 == Y
	        if (getInfo("ExtInfo.UseWebHWPEditYN") == "Y" && getInfo("SchemaContext.scDistribution.isUse") == "Y") {
//	        	HwpCtrl.SetTextFile(Base64.b64_to_utf8(strHTML), "HTML", "", function() {
//	        		drawApvLineHWP();
//	        	});
	        	var strBody = Base64.b64_to_utf8(formJson.BodyContext.BODY_CONTEXT_HWP);
	        	HwpCtrl.SetTextFile(strBody, "HWPML2X", "", function (result) {
	        		SetHwpEditMode(HwpCtrl, "read");
	        		drawApvLineHWP();
	        		// 인장
	        		if (typeof formJson.BodyContext["pubdocinfo"] == 'undefined' && formJson.Request.mode == "COMPLETE") {
	        			HwpCtrl.PutFieldText("chief", formJson.BodyContext.BODY_CONTEXT_CHIEF);
	        			HwpCtrl.MoveToField("sealsign", true, false, true);

	        			if (Common.getGlobalProperties("isDevMode") == "Y") {
	        				HwpCtrl.InsertPicture("http://192.168.11.146/gwstorage/e-sign/ApprovalForm/HWP/stamp.jpg", true, 1, false, false, 0, 17, 17, function (ctrl) { });
	        			} else {
	        				var imgFileUrl = Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId(formJson.BodyContext.STAMP);
	        				HwpCtrl.InsertPicture(imgFileUrl, true, 1, false, false, 0, 17, 17, function (ctrl) { });
	        			}
	        			
	        			//setHwpFieldText();
	        			// 양식별 함수로 되어 있는 부분을 옮김.
	        			HwpCtrl.PutFieldText("docnumber", formJson.FormInstanceInfo.DocNo); // 등록번호
	    				HwpCtrl.PutFieldText("enforcedate", formJson.FormInstanceInfo.CompletedDate.substring(0, 10)); // 시행일자
	        		}
	        	});
	        } else if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP") { //기안지(대외공문)
	        	if(webHwpYN == "Y") {
	        		SetBody(HwpCtrl, Base64.b64_to_utf8(strHTML), "draft_external", 0, dataObj);
	        	} else {
	        		SetBody(HwpCtrl, strHWP, "draft_external", 0, dataObj);
	        	}
	        } else if (getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
				var strBody = Base64.b64_to_utf8(formJson.BodyContext.BODY_CONTEXT_HWP);
				HwpCtrl.SetTextFile(strBody, "HWPML2X", "", function (result) {
                    // drawApvLineHWP();
					HwpCtrl.EditMode = 0;
                });
			} else {
	        	SetBody(HwpCtrl, strHWP, "", 0, dataObj);
	        }
	        //SetHwpEditMode(HwpCtrl, "read");
        }

    	var vp = HwpCtrl.CreateSet("ViewProperties");
    	vp.SetItem("ZoomType", 2);
    	HwpCtrl.ViewProperties = vp;
    }
    
    if(webHwpYN == "Y") {
		InitPageSetup(15, HwpCtrl);
    }
}

function setEditorHWP_Multi(idx, templateFile) {
    var dom;
    const firstIdx =1;
    
    webHwpYN = getInfo("ExtInfo.UseWebHWPEditYN");
    HwpYN = getInfo("ExtInfo.UseHWPEditYN");
    
    //쓰기 모드인 경우
    if (getInfo("Request.templatemode") == "Write") {

        HwpCtrl = document.getElementById(g_id + firstIdx);
        if(HwpCtrl == null && webHwpYN == "Y") {
        	HwpCtrl = document.getElementById(g_id + firstIdx + 'Frame').contentWindow.HwpCtrl;
        }

		// 문서유통 다안기안일때 에디터에 idx 속성 추가
		if (HwpCtrl.Impl) HwpCtrl.Impl['idx'] = idx;
		
        HwpCtrl.SetClientName("DEBUG");
        if(webHwpYN != "Y") {
        	InitToolBarJS();
        }

        if (_ie|| webHwpYN == "Y") {
        	if (getInfo('BodyContext') != undefined && getInfo("BodyContext") != "{}") {     //기안,임시저장으로 저장된 값 setting
        		if (typeof (isGovMulti) == 'function' && isGovMulti()) {
					if ($(formJson.BodyData.SubTable1)[idx-1]) {
						var strBody = Base64.b64_to_utf8($(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP);
						HwpCtrl.SetTextFile(strBody, "HWPML2X", "", function (result) {
	                		drawApvLineHWP();

							// 웹 한글기안 에디터 로드 세팅 종료
            				setGovMultiEditorLoadEnd(result.HwpCtrl.Impl.idx);
	                	}, {"HwpCtrl":HwpCtrl});
                	} else {
						SetBody(HwpCtrl, getInfo("BodyData"), templateFile, 0, null);
					}
				} else if($(formJson.BodyData.SubTable1)[idx-1] != undefined) {
    				var strBody = $(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP;
    				var strBodyHTML = Base64.b64_to_utf8($(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HTML);
    				/*if(webHwpYN == "Y") {
        				HwpCtrl.SetTextFile(strBodyHTML, "HTML", "");
    				} else {
        				HwpCtrl.SetTextFile(strBody, "HWP", "");	
    				}*/
    				HwpCtrl.SetTextFile(strBody, "HWP", "", function(result) {
        				SetHwpEditMode(result.HwpCtrl, "form");
    			    	drawApvLineHWP();
    			    	InitPageSetup(15, result.HwpCtrl);		
    				}, {"HwpCtrl":HwpCtrl});	
    				
    				if(getInfo("Request.reuse") == 'Y'){
    					HwpCtrl.PutFieldText("isStamp", " ");
    					HwpCtrl.PutFieldText("chief_sign", " "); // 이미지 삽입 전 공백 삽입(이미지만 삽입시 출력/미리보기시 이미지 사라짐)
    					HwpCtrl.PutFieldText("seal", "   "); // 이미지 삽입 전 공백 삽입(이미지만 삽입시 출력/미리보기시 이미지 사라짐)
    				}
        		} else {
                    SetBody(HwpCtrl, getInfo("BodyData"), templateFile, 0, null);
        		}
        	} else {//양식 생성 시 입력한 본문내역 조회
                SetBody(HwpCtrl, getInfo("BodyData"), templateFile, 0, null);
            }
            var vp = HwpCtrl.CreateSet("ViewProperties");
            vp.SetItem("ZoomType", 2);
            HwpCtrl.ViewProperties = vp;
        }
    }
    else {// 읽기 모드인 경우

        HwpCtrl = document.getElementById(g_id + firstIdx);
        if(HwpCtrl == null && webHwpYN == "Y") {
        	HwpCtrl = document.getElementById(g_id + firstIdx + 'Frame').contentWindow.HwpCtrl;
        }

		// 문서유통 다안기안일때 에디터에 idx 속성 추가
		if (HwpCtrl.Impl) HwpCtrl.Impl['idx'] = idx;
		
        HwpCtrl.SetClientName("DEBUG");
        if(webHwpYN != "Y") {
        	InitToolBarJS_Read();
        }

    	if(formJson.BodyData.SubTable1 != undefined && formJson.BodyData.SubTable1 != ""){
			if (typeof (isGovMulti) == 'function' && isGovMulti()) {
				var strBody = Base64.b64_to_utf8($(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP);
				HwpCtrl.SetTextFile(strBody, "HWPML2X", "", function (result) {
            		drawApvLineHWP();
            		
            		if (formJson.Request.mode == "COMPLETE") {
            			// 양식별 함수로 되어 있는 부분을 옮김.
        				result.HwpCtrl.PutFieldText("docnumber", formJson.FormInstanceInfo.DocNo); // 등록번호
    					result.HwpCtrl.PutFieldText("enforcedate", formJson.FormInstanceInfo.CompletedDate.substring(0, 10)); // 시행일자
    				}
    				
    				if(templateFile == "draft_Template" && getInfo("FormInstanceInfo.DocNo") != null && getInfo("FormInstanceInfo.DocNo") != ""){
			    		result.HwpCtrl.PutFieldText('docnumber', getInfo("FormInstanceInfo.DocNo"));
			    		result.HwpCtrl.PutFieldText('enforcedate', formatDate(getInfo("FormInstanceInfo.CompletedDate").split('.')[0], 'SB2'));
			    	} else if(templateFile == "review_Template" && getInfo("FormInstanceInfo.ReceiveNo") != ""){
			    		result.HwpCtrl.PutFieldText('docnumber', getInfo("FormInstanceInfo.ReceiveNo"));
			    	}
			    	// 수신부서 접수번호 표시
			    	var recNo = getInfo('ProcessInfo.ProcessDescription.Reserved2');
					var m_oApvList =jQuery.parseJSON(m_oFormMenu.document.getElementById("APVLIST").value.replace(/&/g, "&amp;"));
					$$_m_oApvList = $$(m_oApvList);
			    	if(templateFile == "draft_Template" && recNo != null && recNo != "" && $$_m_oApvList.find("division[divisiontype='receive']").length > 0){
			    		result.HwpCtrl.PutFieldText('receipt_docnumber', recNo);
			    		var rcvCompleteDt = $$_m_oApvList.find("division[divisiontype='receive']>taskinfo").attr('datecompleted');
			    		result.HwpCtrl.PutFieldText('receiptdate', formatDate(rcvCompleteDt, 'SB2'));
			    	}
			    	
    				SetHwpEditMode(result.HwpCtrl, "read");
            		// 웹 한글기안 에디터 로드 세팅 종료
            		setGovMultiEditorLoadEnd(result.HwpCtrl.Impl.idx);
            	}, {"HwpCtrl":HwpCtrl});
			} else {
				var strBody = $(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP;
				var strBodyHTML = Base64.b64_to_utf8($(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HTML);
				/*if(webHwpYN == "Y") {
					HwpCtrl.SetTextFile(strBodyHTML, "HTML", "");	
				} else {
					HwpCtrl.SetTextFile(strBody, "HWP", "");	
				}*/
				HwpCtrl.SetTextFile(strBody, "HWP", "", function(result) {
			        SetHwpEditMode(result.HwpCtrl, "read");
			    	drawApvLineHWP();
			    	InitPageSetup(15, result.HwpCtrl);

					if(templateFile == "draft_Template" && getInfo("FormInstanceInfo.DocNo") != null && getInfo("FormInstanceInfo.DocNo") != ""){
			    		HwpCtrl.PutFieldText('docnumber', getInfo("FormInstanceInfo.DocNo"));
			    		HwpCtrl.PutFieldText('enforcedate', '(' + formatDate(getInfo("FormInstanceInfo.CompletedDate").split('.')[0], 'SB2') + ')');
			    	} else if(templateFile == "review_Template" && getInfo("FormInstanceInfo.ReceiveNo") != ""){
			    		HwpCtrl.PutFieldText('docnumber', getInfo("FormInstanceInfo.ReceiveNo"));
			    	}
			    	// 수신부서 접수번호 표시
			    	var recNo = getInfo('ProcessInfo.ProcessDescription.Reserved2');
					var m_oApvList =jQuery.parseJSON(m_oFormMenu.document.getElementById("APVLIST").value.replace(/&/g, "&amp;"));
					$$_m_oApvList = $$(m_oApvList);
			    	if(templateFile == "draft_Template" && recNo != null && recNo != "" && $$_m_oApvList.find("division[divisiontype='receive']").length > 0){
			    		HwpCtrl.PutFieldText('receipt_docnumber', recNo);
			    		var rcvCompleteDt = $$_m_oApvList.find("division[divisiontype='receive']>taskinfo").attr('datecompleted');
			    		HwpCtrl.PutFieldText('receiptdate', '(' + formatDate(rcvCompleteDt, 'SB2') + ')');
			    	}
				}, {"HwpCtrl":HwpCtrl});
			}
		}
        
        var vp = HwpCtrl.CreateSet("ViewProperties");
        vp.SetItem("ZoomType", 2);
        HwpCtrl.ViewProperties = vp;
    }
}

function setEditorHWP_Multi_old(idx) {
    var dom;
    //쓰기 모드인 경우
    if (getInfo("Request.templatemode") == "Write") {

        HwpCtrl = document.getElementById(g_id + idx);

        HwpCtrl.SetClientName("DEBUG");
        InitToolBarJS();

        if (_ie) {
        	if (getInfo('BodyContext') != undefined && getInfo("BodyContext") != "{}") {     //기안,임시저장으로 저장된 값 setting
        		if(getInfo("FormInfo.FormPrefix").indexOf("WF_FORM_SB_MULTI_") > -1 || getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI_GOV") { //저축은행중앙회  or 공공버전 개발용
        			if($(formJson.BodyData.SubTable1)[idx-1] != undefined) {
        				var strBody = $(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP;
        				HwpCtrl.SetTextFile(strBody, "HWP", "");
        				SetHwpEditMode(HwpCtrl, "form");
	        		} else {
	                    SetBody(HwpCtrl, getInfo("BodyData"), "draft_Template", 0, null);
	                    SetHwpEditMode(HwpCtrl, "form");
	        		}
        		}
        	} else {//양식 생성 시 입력한 본문내역 조회
                SetBody(HwpCtrl, getInfo("BodyData"), "draft_Template", 0, null);
                SetHwpEditMode(HwpCtrl, "form");
            }
            var vp = HwpCtrl.CreateSet("ViewProperties");
            vp.SetItem("ZoomType", 2);
            HwpCtrl.ViewProperties = vp;
        }
    }
    else {// 읽기 모드인 경우
        HwpCtrl = document.getElementById(g_id+idx);

        HwpCtrl.SetClientName("DEBUG");
        InitToolBarJS_Read();
        
		if(getInfo("FormInfo.FormPrefix").indexOf("WF_FORM_SB_MULTI_") > -1 || getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI_GOV") { //저축은행중앙회  or 공공버전 개발용
        	if(formJson.BodyData.SubTable1 != undefined){
				var strBody = $(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP;
				HwpCtrl.SetTextFile(strBody, "HWP", "");
                SetHwpEditMode(HwpCtrl, "read");
    		}
		}
        var vp = HwpCtrl.CreateSet("ViewProperties");
        vp.SetItem("ZoomType", 2);
        HwpCtrl.ViewProperties = vp;
    }
}

function setEditorHWP_Multi_Audit(idx, templateFile) {
    var dom;
    //쓰기 모드인 경우
    if (getInfo("Request.templatemode") == "Write") {

        HwpCtrl = document.getElementById(g_id + idx);

        HwpCtrl.SetClientName("DEBUG");
        InitToolBarJS();

        if (_ie) {
        	if (getInfo('BodyContext') != undefined && getInfo("BodyContext") != "{}") {     //기안,임시저장으로 저장된 값 setting
        		if(getInfo("FormInfo.FormPrefix") == "WF_FORM_SB_MULTI_AUDIT_DRAFT") {
        			if($(formJson.BodyData.SubTable1)[idx-1] != undefined) {
        				var strBody = $(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP;
        				HwpCtrl.SetTextFile(strBody, "HWP", "");
        				SetHwpEditMode(HwpCtrl, "form");
	        		} else {
	                    SetBody(HwpCtrl, getInfo("BodyData"), templateFile, 0, null);
	                    SetHwpEditMode(HwpCtrl, "form");
	        		}
        		}
        	} else {//양식 생성 시 입력한 본문내역 조회
                SetBody(HwpCtrl, getInfo("BodyData"), templateFile, 0, null);
                SetHwpEditMode(HwpCtrl, "form");
            }
            var vp = HwpCtrl.CreateSet("ViewProperties");
            vp.SetItem("ZoomType", 2);
            HwpCtrl.ViewProperties = vp;
        }
    }
    else {// 읽기 모드인 경우
        HwpCtrl = document.getElementById(g_id+idx);

        HwpCtrl.SetClientName("DEBUG");
        InitToolBarJS_Read();
        
        if(getInfo("FormInfo.FormPrefix") == "WF_FORM_SB_MULTI_AUDIT_DRAFT") {
        	if(formJson.BodyData.SubTable1 != undefined){
				var strBody = $(formJson.BodyData.SubTable1)[idx-1].MULTI_BODY_CONTEXT_HWP;
				HwpCtrl.SetTextFile(strBody, "HWP", "");
	            SetHwpEditMode(HwpCtrl, "read");
    		}
		}
        var vp = HwpCtrl.CreateSet("ViewProperties");
        vp.SetItem("ZoomType", 2);
        HwpCtrl.ViewProperties = vp;
    }
}

function getBodyContextHWP() {
    var ret = {};
    
    //읽기 모드 일 경우
    if (getInfo("Request.templatemode") == "Read") {
        ret = {};
    }
    else {
    	try {
    		if (getInfo("ExtInfo.UseWebHWPEditYN") == "Y" && getInfo("SchemaContext.scDistribution.isUse") == "Y" && getInfo('ExtInfo.UseMultiEditYN') != "Y") { // 양식본문정보 > 웹한글기안기 > Y
    			var HwpCtrl = document.getElementById('tbContentElementFrame').contentWindow.HwpCtrl;
    			var strHTML = Base64.utf8_to_b64(HwpCtrl.GetTextFile("HTML", ""));
    			document.getElementById("dhtml_body").value = strHTML;
    			document.getElementById("BODY_CONTEXT_SENDERMASTER").value = document.getElementById("SENDERMASTER").value; 
    		} else if (typeof (isGovMulti) == 'function' && isGovMulti()) {
				// 다안기안 + 문서유통일때 처리 
				// 다안기안 + 문서유통 문서 내용 가져오는 부분(HwpCtrl.GetTextFile("HWPML2X", "", function(data) {..});)이 비동기로 처리되어 getBodyContextHWP에서 가져올수 없음.
				// => FormMenu.js 파일 saveWebHWPMultiContext 함수에서 처리함
				
				/*$("[id^=tbContentElement][id$=Frame]").each(function(idx, item) {
					const objEditor = $(item)[0].contentWindow.HwpCtrl;
					
					// 에디터에 있는 제목을 MULTI_TITLE 컬럼에 대입
			        if (document.getElementsByName('MULTI_TITLE')[idx]) {
						document.getElementsByName('MULTI_TITLE')[idx].value = objEditor.GetFieldText("doctitle");
					}
					
					// 공개/비공개 에디터에 표시
					const savedVal = $('input:radio[name="RELEASE_CHECK"]:checked').val();
			        if (savedVal != undefined) {
						if (savedVal == "1") {
							objEditor.PutFieldText("publication", Common.getDic("lbl_apv_open")); // 공개
						} else {
							objEditor.PutFieldText("publication", Common.getDic("lbl_Private")); // 비공개
						}
					}
					
					objEditor.GetTextFile("HWPML2X", "", function(data) {	
						const strHWP = Base64.utf8_to_b64(data);
						document.getElementsByName("MULTI_BODY_CONTEXT_HWP")[idx].value = idx; //strHWP;
					});
				});*/
			} else if (getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI") {

    			var objDocType = $('#ST_DOCTYPE');
    			var objReservedStr_1 = $('#ST_RESERVEDSTR_1');
    			var objReservedStr_2 = $('#ST_RESERVEDSTR_2');
    			var objRecLineOPD = $('#ST_RECLINEOPD');
    			
				var objEditor = $("#tbContentElement");

				$(objEditor)[0].MoveToField("BODY", true, true, true);
				var strBODY = $(objEditor)[0].GetTextFile("HWP", "saveblock");
				if (strBODY != null) {
					document.getElementById("ST_HWPDATA").value = strBODY;
				} else {
					document.getElementById("ST_HWPDATA").value = "";
				}
				if ($(objEditor)[0].MoveToField("SUBJECT")) {
					document.getElementById("Subject").value = $(objEditor)[0].GetFieldText("SUBJECT");
					
					$(objEditor)[0].MoveToField("SUBJECT", true, true, true);
					var strSUBJECT = $(objEditor)[0].GetTextFile("HWP", "saveblock");
					if (strSUBJECT != null) {
						document.getElementById("ST_SUBJECT").value = strSUBJECT;
					} else {
						document.getElementById("ST_SUBJECT").value = "";
					}
				}
				if ($(objEditor)[0].MoveToField("SendCC_DP")) {
					document.getElementById("SendCC_DP").value = $(objEditor)[0].GetFieldText("SendCC_DP");

					$(objEditor)[0].MoveToField("SendCC_DP", true, true, true);
					var strSendCC = $(objEditor)[0].GetTextFile("HWP", "saveblock");
					if (strSendCC != null) {
						document.getElementById("ST_CC_DP").value = strSendCC;
					} else {
						document.getElementById("ST_CC_DP").value = "";
					}
				}
				if ($(objEditor)[0].MoveToField("ST_DESTINATION_DP")) {
					document.getElementById("ST_DESTINATION_DP").value = $(objEditor)[0].GetFieldText("ST_DESTINATION_DP");
				} else {
					document.getElementById("ST_DESTINATION_DP").value = "";
				}

				if ($(objEditor)[0].MoveToField("INITIATOR_TEL")) {
					document.getElementById("INITIATOR_TEL").value = $(objEditor)[0].GetFieldText("INITIATOR_TEL");
				}
				if ($(objEditor)[0].MoveToField("INITIATOR_FAX")) {
					document.getElementById("INITIATOR_FAX").value = $(objEditor)[0].GetFieldText("INITIATOR_FAX");
				}
				if ($(objEditor)[0].MoveToField("TEMP_DOC_NO")) {
					document.getElementById("TEMP_DOC_NO").value = $(objEditor)[0].GetFieldText("TEMP_DOC_NO");
				}
				if ($(objEditor)[0].MoveToField("INITIATOR_DATE_INFO2")) {
					document.getElementById("INITIATOR_DATE_INFO2").value = $(objEditor)[0].GetFieldText("INITIATOR_DATE_INFO2");
				}
				if ($(objEditor)[0].MoveToField("HANDLING")) {
					document.getElementById("HANDLING").value = $(objEditor)[0].GetFieldText("HANDLING");
				}
				if ($(objEditor)[0].MoveToField("ST_SEND_DP")) {
					document.getElementById("ST_SEND_DP").value = $(objEditor)[0].GetFieldText("ST_SEND_DP");
				} else {
					document.getElementById("ST_SEND_DP").value = "";
				}				
				
    		} else if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP") {
    			if (webHwpYN == "Y") {
                	var objEditor = document.getElementById('tbContentElementFrame').contentWindow.HwpCtrl;
                	var strHTML = "";
                	var strHWP = "";

                    if(formJson.Request.mode == "DRAFT") {
            			strHTML = Base64.utf8_to_b64($(objEditor)[0].GetTextFile("HTML", ""));
                    	if (strHTML != "") {
                    		document.getElementById("dhtml_body").value = strHTML;
                        	document.getElementById("BODY_CONTEXT_HTML").value = strHTML;
                        }
                   	}
                    else {
                        $(objEditor)[0].MoveToField("BODY", true, true, true);
                    	
                    	strHTML = Base64.utf8_to_b64($(objEditor)[0].GetTextFile("HTML", ""));       
                    	document.getElementById("BODY_CONTEXT_HTML").value = strHTML;
                    }
        			
        			$(objEditor)[0].GetTextFile("HWP", "", function(data){ 
            			strHwp = data;                    			
            			if (strHWP != "") {
                        	document.getElementById("BODY_CONTEXT_HWP").value = strHWP;
                    	}
        			});
    			} else if (_ie) {    				
                    var objEditor = $("#tbContentElement");
                    var strHWP = $(objEditor)[0].GetTextFile("HWP", "");
                    var strHTML = $(objEditor)[0].GetTextFile("HTML", "");
                    var TransferHTML = strHTML;
                    if (strHWP != "") {
                    	// 대외공문 변환 관련 소스코드 - 추후 개발 필요
                    	if (getInfo("SchemaContext.scOPub.isUse") == "Y" && false) { //TODO: 후에 연구1팀에서 전자공문 사용 시 false 제거
                            if (strHTML != null) {
                            	var action_mode = ""; // TODO: 확인필요 - 닷넷(화재보험)에서는 사용 안 함
                        		var Items = {
                        			mode : action_mode,
                        			via : document.getElementById("via").value,
                        			subject : document.getElementById("SUBJECT").value,
                        			hbody : strHTML,
                        			apvlist : document.getElementById("APVLIST").value,
                        			com_addr : '',
                        			initiator_tel : document.getElementById("INITIATOR_TEL").value,
                        			initiator_fax : document.getElementById("INITIATOR_FAX").value,
                        			initiator_email : document.getElementById("INITIATOR_EMAIL").value,
                        			publication_1 : document.getElementsByName("PUBLICATION_1")[0].value,
                        			publication_2 : document.getElementsByName("PUBLICATION_2")[0].value,
                        			st_docno : '',
                        			initiator_date_info2 : '',
                        			st_reclineopd : document.getElementById("RECEIVEGOV_NAMES").value 
                        		};

                                if (document.getElementById("RECEIVEGOV_TEXT") != null) {
                                    Items.st_reclineopd_text = encodeURIComponent(document.getElementById("RECEIVEGOV_TEXT").value);
                                }
                                if (document.getElementsByName("SEND_DEPT")[0] != null && document.getElementsByName("SEND_DEPT")[0].value != "") {
                                    Items.send_dept = document.getElementsByName("SEND_DEPT")[0].value;
                                } else {
                                    Items.send_dept = '106';
                                }

                                var sUrl = "/approval/user/getDocTransfer.do";
                                $.ajax({
                        			url:sUrl,
                        			type:"post",
                        			data:Items,
                        			async:false,
                        			success:function (data) {
                        				if(data.result=="ok"){
                                           TransferHTML = data.TRANSFERHTML;

                                           document.getElementById("BODY_HTML").value = data.BODY_HTML;
                                           document.getElementById("SIGNIMAGE").value = data.SIGNIMAGE;
                                           document.getElementById("RECEIVER_NAME").value = data.RECEIVER_NAME;
                                           document.getElementById("RECEIVER").value = data.RECEIVER;
                                           document.getElementById("GOV_BODY_CONTEXT").value = data.BODY_CONTEXT;
                        				} else {
                                            //Common.Error("Desc: " + data.message);
                                            throw new Error("공문 필터링 중 오류가 발생하였습니다.");
                        				}
                        			},
                        			error:function(response, status, error){
                        				CFN_ErrorAjax(sUrl, response, status, error);
                        			}
                        		});
                            }
                        }
                        document.getElementById("dhtml_body").value = TransferHTML;
                        if(formJson.Request.mode == "DRAFT") {
                        	document.getElementById("BODY_CONTEXT_HWP").value = strHWP; //패키지에 만들어진 DB 필드값 써주기
                        }
                        else {
                        	$(objEditor)[0].MoveToField("BODY", true, true, true);
                        	//var strBODY = $(objEditor)[0].GetTextFile("HWP", "saveblock");
                        	var strBODY = $(objEditor)[0].GetTextFile("HWP", "");
                        	document.getElementById("BODY_CONTEXT_HWP").value = strBODY;
                        }
                    }
    			}
    		} else if(getInfo('ExtInfo.UseMultiEditYN') == "Y") { //저축은행중앙회 or 공공버전 개발용
                if(webHwpYN == "Y") {
                	$("[id^=tbContentElement][id$=Frame]").each(function(idx, item){
                    	var objEditor = $(item)[0].contentWindow.HwpCtrl;
                    	var strHTML = "";
                    	var strHWP = "";
                    	
                		if(formJson.Request.mode == "DRAFT") {
                			strHTML = Base64.utf8_to_b64($(objEditor)[0].GetTextFile("HTML", ""));
                			if (strHTML != "") {
                            	document.getElementById("dhtml_body").value = strHTML;
                				$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HTML]')[idx].value = strHTML;
                			}
                			
                			$(objEditor)[0].GetTextFile("HWP", "", function(data){ 
                    			strHwp = data;                    			
                    			if (strHWP != "") {
                					$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HWP]')[idx].value = strHWP;
                    			}
                			});
                        } else {
                        	$(objEditor)[0].MoveToField("BODY", true, true, true);
                        	
                        	strHTML = Base64.utf8_to_b64($(objEditor)[0].GetTextFile("HTML", ""));       
                			$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HTML]')[idx].value = strHTML;
                			
                        	$(objEditor)[0].GetTextFile("HWP", "", function(data){ 
                        		strHWP = data; 
                				$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HWP]')[idx].value = strHWP;
                        	});       	
                        }
                		
                		var subject = $(objEditor)[0].GetFieldText("SUBJECT");
                		$('#SubTable1 .multi-row').find('[name=MULTI_TITLE]')[idx].value = subject;
                	});
                } else if (_ie) {
    				$("[id^=tbContentElement]").each(function(idx, item){
    					var strHWP = $(item)[0].GetTextFile("HWP", "");
    					$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HWP]')[idx].value = strHWP;

    					var strHTML = $(item)[0].GetTextFile("HTML", "");
    					strHTML = Base64.utf8_to_b64(strHTML);
    					$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HTML]')[idx].value = strHTML;
    					
    					var subject = $(item)[0].GetFieldText("SUBJECT");
    					$('#SubTable1 .multi-row').find('[name=MULTI_TITLE]')[idx].value = subject;
    				});
    			}
    		} else if(getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
    			// 한글 웹 기안기 
    			var HwpCtrl = document.getElementById('tbContentElementFrame').contentWindow.HwpCtrl;
    			var strHTML = Base64.utf8_to_b64(HwpCtrl.GetTextFile("HTML", ""));
    			document.getElementById("dhtml_body").value = strHTML;
    		} else {
//    			var objEditor = $("#tbContentElement");
//    			var strHWP = $(objEditor)[0].GetTextFile("HWP", "");
//    			if (strHWP != "") {
//    				document.getElementById("dhtml_body").value = strHWP;
//    			}
    		}
    	} catch (e) {
    		throw new Error(e);
    	}
    	
        var sBodyContext = makeBodyContext();

        ret = sBodyContext;
    }
    return ret;
}

function event_getDocHTML(dataresponseXML) {
    var xmlReturn = dataresponseXML;
    var errorNode = $(xmlReturn).find("response > error");
    if (errorNode.length > 0) {
        alert("AttachImage ERROR : " + errorNode[0].text);
        return;
    } else {
        document.getElementById("dhtml_body").value = $(xmlReturn).find("response > htmldata").text();
    }
}

function UpdateImageDataMulti(obj, idx) {
    gz_Editor = editortype.is();
    var nfolder = "IMAGEATTACH";
    var n = -1;

    n = hidContentHtml.indexOf('file:///');

    if (n > 0) {
        if (hidContentMime != "") {
            // frontstorage에 있는 이미지를 BackStorage\Approval 로 이동
            var szRequestXml = "<?xml version='1.0'?>" +
								"<parameters>" +
									"<BodyContent><![CDATA[" + hidContentHtml + "]]></BodyContent>" +
									"<EditorContent><![CDATA[" + hidContentMime + "]]></EditorContent>" +
									"<ImageContent><![CDATA[" + hidContentImage + "]]></ImageContent>" +
									"<fiid><![CDATA[" + getInfo("FormInfo.FormID") + "]]></fiid>" +
									"<itemnum><![CDATA[" + "]]></itemnum>" +
                                "</parameters>";
            CFN_CallAjax("/WebSite/Approval/FileAttach/BodyImgMoveBAckStorageHWP.aspx", szRequestXml, function (data) {
                event_attchSyncMulti(data, obj, idx);
            }, false, "xml");
        }
    }
}

//=== 여기서 부터 새로 생성
//에디터 모드
function SetHwpEditMode(HwpCtrl, sMode) {
    var sKind;
    switch (sMode) {
        case "read": sKind = 0; break; //읽기 전용
        case "edit": sKind = 1; break; //입력
        case "form": sKind = 2; break; //양식폼 - 누름틀만 활성화
        default:
            sKind = 0; break; //읽기 전용
    }
    HwpCtrl.EditMode = sKind;
}

//문서 가져오기
function SetBody(HwpCtrl, strBody, strfilename, iSelIdx, dataObj) {//한글 입력양식기 Base64 Decoding Stream
	iSelIdx = 0;
	
	var strBackStorage = Common.agentFilterGetData("smart4j.path", "P") + Common.getBaseConfig("BackStorage").replace("{0}", Common.getSession("DN_Code"));
	
    var g_BaseFormURL = strBackStorage + "e-sign/ApprovalForm/HWP/";
	
    if ((formJson.Request.mode == "DRAFT" || formJson.Request.mode == "TEMPSAVE") || (formJson.Request.mode == "SUBREDRAFT" && formJson.Request.gloct == "DEPART")) {
        if (getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI") {
	        if (strfilename != '') {
	        	HwpCtrl.RegisterModule("FilePathCheckDLL", "SntFilePathCheckerModule");
	        	if (!HwpCtrl.Open(g_BaseFormURL + strfilename + ".hwp", "HWP", "code:acp;url:true;lock:false;template:true;")) { alert("문서 열기 실패"); return; } 
        	}
	        fnPutFieldText(iSelIdx, 'COM_ADDR', $("#COM_ADDR").children('option:selected').text());
	        fnPutFieldText(iSelIdx, 'INITIATOR_TEL', formJson.AppInfo.ustp);
	        fnPutFieldText(iSelIdx, 'INITIATOR_FAX', formJson.AppInfo.usfx);
	        fnPutFieldText(iSelIdx, 'SAVE_TERM', $("#SaveTerm").children('option:selected').text());
	        fnPutFieldText(iSelIdx, 'TEMP_DOC_NO', formJson.AppInfo.dpdn_apv + formatDate(formJson.AppInfo.svdt, "S").substring(2, 4));
	        fnPutFieldText(iSelIdx, 'INITIATOR_DATE_INFO2', formatDate(formJson.AppInfo.svdt, "D2").substring(0, 5));
	        createTableArea(iSelIdx, "MASTERSIGN");
	        fnPutFieldText(iSelIdx, 'MEMO', m_oFormMenu.getLngLabel(formJson.AppInfo.dpdn_apv, false));
	    	
	        initApvList();
	        PutSignMaster(iSelIdx); //직인 표시
        } else if (getInfo('ExtInfo.UseWebHWPEditYN') == "Y"){ // 웹한글기안 문서 사용 양식일때.
	        if (strfilename != '') {	        	
	        	var hwpFileName = g_BaseFormURL + strfilename + ".hwp";
	        	if(webHwpYN != "Y") {
	        		HwpCtrl.RegisterModule("FilePathCheckDLL", "SntFilePathCheckerModule");
		        	if (!HwpCtrl.Open(g_BaseFormURL + strfilename + ".hwp", "HWP", "code:acp;url:true;lock:false;template:true;")) { alert("문서 열기 실패"); return; }
		        	
            		SetBodyMultiGov(strfilename, HwpCtrl);
	        	} else {
	        		HwpCtrl.Open(Common.getBaseConfig("HwpCtrlFilePath"), "HWP", "code:acp;url:true;lock:false;template:true;", function(res) { 
	            		console.log(res); 
	            		
	            		SetBodyMultiGov(strfilename, HwpCtrl);
	        		});
	        	}
        	}
        }
    } else {
        if (strfilename != '') {
        	var hwpFileName = g_BaseFormURL + strfilename + ".hwp";
        	if(webHwpYN != "Y") {
        		HwpCtrl.RegisterModule("FilePathCheckDLL", "SntFilePathCheckerModule");
            	if (!HwpCtrl.Open(hwpFileName, "HWP", "code:acp;url:true;lock:false;")) { alert("문서 열기 실패"); return; } 
        	} else {
        		HwpCtrl.Open(Common.getBaseConfig("HwpCtrlFilePath"), "HWP", "code:acp;url:true;lock:false;", function(res) { 
            		console.log(res); 
            		if(typeof formJson.BodyContext["BODY_CONTEXT_HWP"] == "undefined" && typeof formJson.BodyContext["pubdocinfo"] != 'undefined') {
            			SetBodyGovReceive(HwpCtrl);
            		} else if (typeof (isGovMulti) == 'function' && isGovMulti()) {
						SetBodyMultiGov(strfilename, HwpCtrl);
					} else {
            			SetBodyExternal(HwpCtrl, strBody, iSelIdx); //대외공문
            		}
        		});
        	}
    	}
        
        if (getInfo("FormInfo.FormPrefix") == "WF_FORM_SB_MULTI_AUDIT_DRAFT"){
        	if(getInfo("ProcessInfo.ProcessDescription.Reserved2") != ""){
        		HwpCtrl.PutFieldText('docnumber', getInfo("ProcessInfo.ProcessDescription.Reserved2"));
        	}
        } else if (getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP_MULTI") {        
	        if (strBody != null && strBody != "") {
	            HwpCtrl.MoveToField("BODY", true, true, false);
	            HwpCtrl.SetTextFile(strBody, "HWP", "insertfile");
	        } else {
	            HwpCtrl.PutFieldText('BODY', " ");
	        }
	        if (formJson.FormInstanceInfo.Subject != "" && HwpCtrl.MoveToField("SUBJECT")) {
	            HwpCtrl.PutFieldText('SUBJECT', formJson.FormInstanceInfo.Subject);
	        } else {
	            HwpCtrl.PutFieldText('SUBJECT', " ");
	        }
	        if (formJson.BodyContext.SendCC_DP != null && formJson.BodyContext.SendCC_DP != "" && HwpCtrl.MoveToField("SendCC_DP")) {
	            HwpCtrl.PutFieldText('SendCC_DP', formJson.BodyContext.SendCC_DP);
	        } else {
	            HwpCtrl.PutFieldText('SendCC_DP', " ");
	        }
	        if (formJson.BodyContext.COM_ADDR_TEXT != "" && HwpCtrl.MoveToField("COM_ADDR")) {
	            HwpCtrl.PutFieldText('COM_ADDR', formJson.BodyContext.COM_ADDR_TEXT);
	        }
	        if (formJson.BodyContext.INITIATOR_TEL != null && formJson.BodyContext.INITIATOR_TEL != "" && HwpCtrl.MoveToField("INITIATOR_TEL")) {
	            HwpCtrl.PutFieldText('INITIATOR_TEL', formJson.BodyContext.INITIATOR_TEL);
	        }
	        if (formJson.BodyContext.INITIATOR_FAX != null && formJson.BodyContext.INITIATOR_FAX != "" && HwpCtrl.MoveToField("INITIATOR_FAX")) {
	            HwpCtrl.PutFieldText('INITIATOR_FAX', formJson.BodyContext.INITIATOR_FAX);
	        }
	        if (getInfo("Request.templatemode") != "Read" && formJson.Request.mode == "DRAFT") {
	        	fnPutFieldText(iSelIdx, 'TEMP_DOC_NO', formJson.AppInfo.dpdn_apv + formatDate(formJson.AppInfo.svdt, "S").substring(2, 4));
	        	fnPutFieldText(iSelIdx, 'INITIATOR_DATE_INFO2', formatDate(formJson.AppInfo.svdt, "D2").substring(0, 5));
	        } else {
	        	if (formJson.BodyContext.TEMP_DOC_NO != "" && HwpCtrl.MoveToField("TEMP_DOC_NO")) {
	        		HwpCtrl.PutFieldText('TEMP_DOC_NO', formJson.BodyContext.TEMP_DOC_NO);
	        	}
	        	if (formJson.Request.mode != "DRAFT" && formJson.FormInstanceInfo.DocNo != null && formJson.FormInstanceInfo.DocNo != "" && HwpCtrl.MoveToField("TEMP_DOC_NO")) {
	        		HwpCtrl.PutFieldText('TEMP_DOC_NO', formJson.FormInstanceInfo.DocNo);
	        	}
	        	if (formJson.BodyContext.INITIATOR_DATE_INFO2 != null && formJson.BodyContext.INITIATOR_DATE_INFO2 != "" && HwpCtrl.MoveToField("INITIATOR_DATE_INFO2")) {
	        		HwpCtrl.PutFieldText('INITIATOR_DATE_INFO2', formJson.BodyContext.INITIATOR_DATE_INFO2);
	        	}
	        	if (formJson.BodyContext.COMPLETED_DATE != null && formJson.BodyContext.COMPLETED_DATE != "" && HwpCtrl.MoveToField("INITIATOR_DATE_INFO2")) {
	        		HwpCtrl.PutFieldText('INITIATOR_DATE_INFO2', formatDate(formJson.BodyContext.COMPLETED_DATE, "D2"));
	        	}
	        }
	        if (formJson.BodyContext.HANDLING != null && formJson.BodyContext.HANDLING != "" && HwpCtrl.MoveToField("HANDLING")) {
	            HwpCtrl.PutFieldText('HANDLING', formJson.BodyContext.HANDLING);
	        } else {
	            HwpCtrl.PutFieldText('HANDLING', " ");
	        }
	        if (formJson.FormInstanceInfo.SaveTerm != "" && HwpCtrl.MoveToField("SAVE_TERM")) {
	            var sSAVE_TERM = "";
	            if (formJson.FormInstanceInfo.SaveTerm == "99") {
	                sSAVE_TERM = "영구";
	            } else {
	                sSAVE_TERM = formJson.FormInstanceInfo.SaveTerm + "년";
	            }
	            HwpCtrl.PutFieldText('SAVE_TERM', sSAVE_TERM);
	        }
	        
	        if (iSelIdx > 0 && $(dataObj)[0].ST_DOCTYPE == "1") {
	            initRecListMulti_V1(iSelIdx);
	        }else if (iSelIdx > 0 && $(dataObj)[0].ST_DOCTYPE == "3") {
	            initGovListMulti_V1(iSelIdx);
	        }
	        if (getInfo("Request.templatemode") == "Read") {
	            createTableArea(iSelIdx, "MASTERSIGN");
	            fnPutFieldText(0, 'MEMO', formJson.BodyContext.MEMO);
	            SetHwpEditMode(HwpCtrl, "read");
	        } else {
	            createTableArea(iSelIdx, "MASTERSIGN");
	            fnPutFieldText(iSelIdx, 'MEMO', formJson.BodyContext.MEMO);
	        }
	        
	        initApvList();
        } else if(getInfo("FormInfo.FormPrefix") == "WF_FORM_DRAFT_HWP") {    
        	if(webHwpYN != "Y") {
        		SetBodyExternal(HwpCtrl, strBody, iSelIdx);
        	}
        } else if(getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
			HwpCtrl.SetTextFile(strBody, "HWP", "insertfile");
		}
    }
    HwpCtrl.MovePos(2);
}

function SetBodyExternal(HwpCtrl, strBody, iSelIdx) {
    if (formJson.BodyContext.SEND_DEPT_TEXT != null && formJson.BodyContext.SEND_DEPT_TEXT != "" && HwpCtrl.MoveToField("ST_SEND_DP")) {
        HwpCtrl.PutFieldText('ST_SEND_DP', formJson.BodyContext.SEND_DEPT_TEXT);
        HwpCtrl.PutFieldText('MASTERSIGNAREA', formJson.BodyContext.SEND_DEPT_TEXT + " 대표이사");
    }
    if (formJson.BodyContext.COM_ADDR != null && formJson.BodyContext.COM_ADDR != "" && HwpCtrl.MoveToField("COM_ADDR")) {
        HwpCtrl.PutFieldText('COM_ADDR', formJson.BodyContext.COM_ADDR);
    }
    if (formJson.BodyContext.INITIATOR_EMAIL != null && formJson.BodyContext.INITIATOR_EMAIL != "" && HwpCtrl.MoveToField("INITIATOR_EMAIL")) {
        HwpCtrl.PutFieldText('INITIATOR_EMAIL', formJson.BodyContext.INITIATOR_EMAIL);
    }    
    if (formJson.BodyContext.INITIATOR_TEL != null && formJson.BodyContext.INITIATOR_TEL != "" && HwpCtrl.MoveToField("INITIATOR_TEL")) {
        HwpCtrl.PutFieldText('INITIATOR_TEL', formJson.BodyContext.INITIATOR_TEL);
    }
    if (formJson.BodyContext.INITIATOR_FAX != null && formJson.BodyContext.INITIATOR_FAX != "" && HwpCtrl.MoveToField("INITIATOR_FAX")) {
        HwpCtrl.PutFieldText('INITIATOR_FAX', formJson.BodyContext.INITIATOR_FAX);
    }       	
    if (formJson.FormInstanceInfo.DocNo != null && formJson.FormInstanceInfo.DocNo != "" && HwpCtrl.MoveToField("ST_DOCNO")) {
    	HwpCtrl.PutFieldText('ST_DOCNO', formJson.FormInstanceInfo.DocNo);
    } else {
    	HwpCtrl.PutFieldText('ST_DOCNO', "자동 발번");
    }     	
    if (formJson.FormInstanceInfo.InitiatedDate != null && formJson.FormInstanceInfo.InitiatedDate != "" && HwpCtrl.MoveToField("ST_SEND_DATE")) {
    	HwpCtrl.PutFieldText('ST_SEND_DATE', formJson.FormInstanceInfo.InitiatedDate.split(' ')[0]);
    } else {
    	HwpCtrl.PutFieldText('ST_SEND_DATE', formJson.AppInfo.svdt.split(' ')[0]);
    }
    if (formJson.BodyContext.RECEIVEGOV_NAMES != null && formJson.BodyContext.RECEIVEGOV_NAMES != "" && HwpCtrl.MoveToField("ST_DESTINATION_DP")) {
    	var receive_names = formJson.BodyContext.RECEIVEGOV_NAMES.split(";");
        var destination = ""
        for(var i = 0; i < receive_names.length; i++) {
        	destination += receive_names[i].split("X:")[1];
        	if(i < receive_names.length-1) {
        		 destination += ", ";
        	}
        }
        HwpCtrl.PutFieldText('ST_DESTINATION_DP', destination);
    } else if(formJson.BodyContext.RECEIVEGOV_TEXT != null && formJson.BodyContext.RECEIVEGOV_TEXT != "" && HwpCtrl.MoveToField("ST_DESTINATION_DP")) {
        HwpCtrl.PutFieldText('ST_DESTINATION_DP', formJson.BodyContext.RECEIVEGOV_TEXT);
    }
    
    if (formJson.BodyContext.via != null && formJson.BodyContext.via != "" && HwpCtrl.MoveToField("ST_CC_DP")) {
        HwpCtrl.PutFieldText('ST_CC_DP', formJson.BodyContext.via);
    }
    if (formJson.FormInstanceInfo.Subject != null && formJson.FormInstanceInfo.Subject != "" && HwpCtrl.MoveToField("ST_SUBJECT")) {
        HwpCtrl.PutFieldText('ST_SUBJECT', formJson.FormInstanceInfo.Subject);
    } 
    if (formJson.BodyContext.SEND_DEPT != null && formJson.BodyContext.SEND_DEPT) {
    	PutSignMaster(iSelIdx, 'stamp' + formJson.BodyContext.SEND_DEPT); //직인 표시
    }
	if (strBody != null && strBody != "") {
        HwpCtrl.MoveToField("BODY", true, true, false);
        if(webHwpYN == "Y") {
        	HwpCtrl.SetTextFile(strBody, "HTML", "insertfile");
        } else {
        	HwpCtrl.SetTextFile(strBody, "HWP", "insertfile");
        }
    }

    if (getInfo("Request.templatemode") == "Read") {
        SetHwpEditMode(HwpCtrl, "read");
    } else {
    	SetHwpEditMode(HwpCtrl, "form");
    }
}

function SetBodyMultiGov(strfilename, HwpCtrl) {
	// 템플릿 편집
	if(strfilename == 'draft_Template'){		        	
		HwpCtrl.PutFieldText('zipcode', Common.getBaseConfig("HYU_PostNum"));
    	HwpCtrl.PutFieldText('address', Common.getBaseConfig("HYU_Address") + " " + sessionObjFormMenu.DEPTNAME);
    	HwpCtrl.PutFieldText('fax', " "); //Common.getBaseConfig("SB_FaxNum") / getInfo('AppInfo.usfx')
    	HwpCtrl.PutFieldText('homepage', Common.getBaseConfig("HYU_Hompage")); 
    	HwpCtrl.PutFieldText('email', getInfo('AppInfo.ussip'));
    	HwpCtrl.PutFieldText('publication', " ");
    	HwpCtrl.PutFieldText('telephone',  Common.GetObjectInfo("UR",getInfo("AppInfo.usid"), "PHONENUMBER").PHONENUMBER); //getInfo('AppInfo.ustp')
    	HwpCtrl.PutFieldText('docnumber'," ");
    	HwpCtrl.PutFieldText('receiptnumber'," ");
	}
	else if(strfilename == 'review_Template'){
    	HwpCtrl.PutFieldText('content_title', getInfo("FormInstanceInfo.Subject"));
    	HwpCtrl.PutFieldText('drafter_dept_name', getInfo("FormInstanceInfo.InitiatorUnitName"));
    } else if(strfilename == 'opinion_Template'){
    	
    }	

    if (getInfo("Request.templatemode") == "Read") {
        SetHwpEditMode(HwpCtrl, "read");
    } else {
    	SetHwpEditMode(HwpCtrl, "form");
    }
	
	drawApvLineHWP();
	/*initSetData(formJson.Request.mode);
	if(formJson.Request.mode == "TEMPSAVE"){
    	var strBody = Base64.b64_to_utf8(formJson.BodyContext.SubTable1.MULTI_BODY_CONTEXT_HWP);
    	HwpCtrl.SetTextFile(strBody, "HWPML2X", "");
    }*/

	if (isGovMulti()) {
		// 웹 한글기안 에디터 로드 세팅 종료
        setGovMultiEditorLoadEnd(HwpCtrl.Impl.idx);
		
		// 에디터적용 후 세팅
		initGovMultiEditorDisplay();
	}
}

function SetBodyMultiGovMobile(idx) {
	if (formJson.BodyData.SubTable1[idx-1] != null) {
		var fileInfo = "";
		var SubTable1 = formJson.BodyData.SubTable1[idx-1];
		
		if(SubTable1.MULTI_ATTACH_FILE != null && SubTable1.MULTI_ATTACH_FILE != "" && SubTable1.MULTI_ATTACH_FILE != "undefined"){
			fileInfo = typeof(SubTable1.MULTI_ATTACH_FILE) == "string" ? $.parseJSON(SubTable1.MULTI_ATTACH_FILE).FileInfos : SubTable1.MULTI_ATTACH_FILE.FileInfos;
		}
		$("#approval_view_fileArea").html(mobile_comm_downloadhtml(fileInfo, "Approval"));
		
		$("#headcampaign").text(SubTable1.MULTI_CAMPAIGN_T);
		$("#organ").text(SubTable1.MULTI_ORGAN);
		$("#logo").html($("<img>", {"src" : mobile_comm_getGlobalProperties("mobile.smart4j.path") 
			+ mobile_comm_getImgById("", SubTable1.MULTI_LOGO)}));
		$("#symbol").html($("<img>", {"src" : mobile_comm_getGlobalProperties("mobile.smart4j.path") 
			+ mobile_comm_getImgById("", SubTable1.MULTI_SYMBOL)}));
		
		var ReceiveGovKo = "";
		var ReceiveNames = SubTable1.MULTI_RECEIVENAMES;
		var receveCnt = ReceiveNames.split(":")[0] == "0" ? 13 : 8;
		
		for (var i = 0; i < ReceiveNames.split(";").length; i++) {
	        if (ReceiveNames.split(";")[i] != '') {
	            ReceiveGovKo = ((ReceiveGovKo == "") ? "" : ", ") + ReceiveNames.split(";")[i].split(":")[receveCnt];
	        }
	    }
		
		if (SubTable1.MULTI_APV_RECEIVER_TYPE == "indoc") {
			$("#recipient").text("내부결재");
			$("#recipients").text(" ");
			$("#hrecipients").text(" ");
		} else if (ReceiveGovKo.split(",").length <= 1){
			$("#recipient").text(ReceiveGovKo);
			$("#recipients").text(" ");
			$("#hrecipients").text(" ");
		} else {
			$("#recipient").text("수신자 참조");
			$("#recipients").text(ReceiveGovKo);
			$("#hrecipients").text("수신자");
		}
		
		$("#doctitle").text(SubTable1.MULTI_TITLE);
		$("#body").html(Base64.b64_to_utf8(SubTable1.MULTI_BODY));
		$("#chief").text(SubTable1.MULTI_CHIEF);
		$("#sealsign").css("background-image", "url(" + mobile_comm_getGlobalProperties("mobile.smart4j.path")
			+ mobile_comm_getImgById("", SubTable1.MULTI_STAMP) + ")");
		if (formJson.Request.mode == "COMPLETE") {
			$("#docnumber").text(formJson.FormInstanceInfo.DocNo);
			$("#enforcedate").text(formatDate(getInfo("FormInstanceInfo.CompletedDate").split('.')[0], 'SB2'));
		}
		$("#receiptnumber").text(" ");
		$("#receiptdate").text(" ");
		$("#zipcode").text(SubTable1.MULTI_ZIPCODE);
		$("#address").text(SubTable1.MULTI_ADDRESS);
		$("#homepage").text(SubTable1.MULTI_HOMEURL);
		$("#telephone").text(SubTable1.MULTI_TELEPHONE);
		$("#fax").text(SubTable1.MULTI_FAX);
		$("#email").text(SubTable1.MULTI_EMAIL);
		
		if (formJson.BodyContext.RELEASE_CHECK == "1") {
			$("#publication").text("공개");
		} else {
			$("#publication").text("비공개");
		}
		$("#footcampaign").text(SubTable1.MULTI_CAMPAIGN_F);
		
		drawApvLineMobile();
	}
}

function SetBodyGovReceive(HwpCtrl) {
	var filePath = "";
	var gfileId= "";
	
	HwpCtrl.PutFieldText("logo", " ");
	if($.trim(formJson.BodyContext.pubdocinfo.pubFLogo) != "") {
		filePath = Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId($.trim(formJson.BodyContext.pubdocinfo.pubFLogo));
		
		gfileId = $.trim(formJson.BodyContext.pubdocinfo.pubFLogo); 
		m_arrConverImgArr.push({
			TYPE:"ICON",
			IMGNAME:"logo",
			FILEPATH :filePath,
			WIDTH: 70,
			HEIGHT :70,					
			BINARYDATA :getFileBinaryData(gfileId,70,70),
			HwpCtrl : HwpCtrl				
		});
	}

	HwpCtrl.PutFieldText("symbol", " ");
	if($.trim(formJson.BodyContext.pubdocinfo.pubFSymbol) != "") {
		filePath = Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId($.trim(formJson.BodyContext.pubdocinfo.pubFSymbol));
		
		gfileId = $.trim(formJson.BodyContext.pubdocinfo.pubFSymbol); 
		m_arrConverImgArr.push({
			TYPE:"ICON",
			IMGNAME:"symbol",
			FILEPATH :filePath,
			WIDTH: 70,
			HEIGHT :70,					
			BINARYDATA :getFileBinaryData(gfileId,70,70),
			HwpCtrl : HwpCtrl					
		});
	}
	
	HwpCtrl.PutFieldText("sealsign", " ");
	if($.trim(formJson.BodyContext.pubdocinfo.pubFSealSrc) != "") {
		filePath = Common.agentFilterGetData("smart4j.path", "P") + coviCmn.loadImageId($.trim(formJson.BodyContext.pubdocinfo.pubFSealSrc));
		
		gfileId = $.trim(formJson.BodyContext.pubdocinfo.pubFSealSrc);
		m_arrConverImgArr.push({
			TYPE:"ICON",
			IMGNAME:"sealsign",
			FILEPATH :filePath,
			WIDTH: 100,
			HEIGHT :100,
			BINARYDATA :getFileBinaryData(gfileId,100,60),
			HwpCtrl : HwpCtrl					
		});
	}
	
	//내부수신 수신자 값 셋팅
	var recData = formJson.BodyContext.pubdocinfo.pubHRcptInfRec;
	recDataLen = recData.split(",").length;
	if(recDataLen <=1){
		HwpCtrl.PutFieldText("recipient", formJson.BodyContext.pubdocinfo.pubHRcptInfRec);
		HwpCtrl.PutFieldText("recipients", " ");		
		HwpCtrl.PutFieldText("hrecipients", " ");
	}else {
		HwpCtrl.PutFieldText("hrecipients", "수신자:");
		HwpCtrl.PutFieldText("recipient", "수신자 참조");
		HwpCtrl.PutFieldText("recipients", formJson.BodyContext.pubdocinfo.pubHRcptInfRec);		
	}
	HwpCtrl.PutFieldText("headcampaign", formJson.BodyContext.pubdocinfo.pubFCmpHead);
	HwpCtrl.PutFieldText("organ", formJson.BodyContext.pubdocinfo.pubHOrgan);
	HwpCtrl.PutFieldText("route", formJson.BodyContext.pubdocinfo.pubHRcptInfVia);
	HwpCtrl.PutFieldText("doctitle", formJson.BodyContext.pubdocinfo.hTitle);
	HwpCtrl.PutFieldText("docnumber", formJson.BodyContext.pubdocinfo.pubFRegNumberValue);
	HwpCtrl.PutFieldText("enforcedate", formJson.BodyContext.pubdocinfo.pubFEnforceDate);
	HwpCtrl.PutFieldText("receiptnumber", " ");
	HwpCtrl.PutFieldText("receiptdate", " ");
	HwpCtrl.PutFieldText("zipcode", formJson.BodyContext.pubdocinfo.pubFZipCode);
	HwpCtrl.PutFieldText("address", formJson.BodyContext.pubdocinfo.pubFAddress);
	HwpCtrl.PutFieldText("homepage", formJson.BodyContext.pubdocinfo.pubFHomeUrl);
	HwpCtrl.PutFieldText("telephone", (formJson.BodyContext.pubdocinfo.pubFTel==''?' ':formJson.BodyContext.pubdocinfo.pubFTel));
	HwpCtrl.PutFieldText("fax", formJson.BodyContext.pubdocinfo.pubFFax);
	HwpCtrl.PutFieldText("email", formJson.BodyContext.pubdocinfo.pubFEmail);
	HwpCtrl.PutFieldText("publication", formJson.BodyContext.pubdocinfo.pubFPubliValue);
	HwpCtrl.PutFieldText("footcampaign", formJson.BodyContext.pubdocinfo.pubFCmpFoot);
	HwpCtrl.PutFieldText("chief", formJson.BodyContext.pubdocinfo.pubFSenderNm);
	if(typeof formJson.BodyContext.pubdocinfo.pubFAppvalInfo.approvalinfo.approval != 'undefined') {
		var pubApv = formJson.BodyContext.pubdocinfo.pubFAppvalInfo.approvalinfo.approval;
		for(var i=0; i<pubApv.length; i++) {
			HwpCtrl.PutFieldText("jikwe"+(i+1), pubApv[i].signposition);
			HwpCtrl.PutFieldText("sign"+(i+1), pubApv[i].name);
		}
	}
	
	ConvertToBinaryToHWP(HwpCtrl, updateDocHWP);
}

function SetBodyGovReceiveMobile() {
	$("#headcampaign").text(formJson.BodyContext.pubdocinfo.pubFCmpHead);
	$("#organ").text(formJson.BodyContext.pubdocinfo.pubHOrgan);
	$("#logo").html($("<img>", {"src" : mobile_comm_getGlobalProperties("mobile.smart4j.path") 
		+ mobile_comm_getImgById("", $.trim(formJson.BodyContext.pubdocinfo.pubFLogo))}));
	$("#symbol").html($("<img>", {"src" : mobile_comm_getGlobalProperties("mobile.smart4j.path") 
		+ mobile_comm_getImgById("", $.trim(formJson.BodyContext.pubdocinfo.pubFSymbol))}));
	
	var recData = formJson.BodyContext.pubdocinfo.pubHRcptInfRec;
	recDataLen = recData.split(",").length;
	if(recDataLen <= 1){
		$("#recipient").text(formJson.BodyContext.pubdocinfo.pubHRcptInfRec);
		$("#recipients").text(" ");
		$("#hrecipients").text(" ");
	} else {
		$("#recipient").text("수신자 참조");
		$("#recipients").text(formJson.BodyContext.pubdocinfo.pubHRcptInfRec);
		$("#hrecipients").text("수신자:");
	}
	
	$("#doctitle").text(formJson.BodyContext.pubdocinfo.hTitle);
	$("#body").html(formJson.BodyContext.tbContentElement.replaceAll("\r\n",""));
	$("#chief").text(formJson.BodyContext.pubdocinfo.pubFSenderNm);
	$("#sealsign").css("background-image", "url(" + mobile_comm_getGlobalProperties("mobile.smart4j.path")
		+ mobile_comm_getImgById("", $.trim(formJson.BodyContext.pubdocinfo.pubFSealSrc)) + ")");
	$("#docnumber").text(formJson.BodyContext.pubdocinfo.pubFRegNumberValue);
	$("#enforcedate").text(formJson.BodyContext.pubdocinfo.pubFEnforceDate);
	$("#receiptnumber").text(" ");
	$("#receiptdate").text(" ");
	$("#zipcode").text(formJson.BodyContext.pubdocinfo.pubFZipCode);
	$("#address").text(formJson.BodyContext.pubdocinfo.pubFAddress);
	$("#homepage").text(formJson.BodyContext.pubdocinfo.pubFHomeUrl);
	$("#telephone").text(formJson.BodyContext.pubdocinfo.pubFTel);
	$("#fax").text(formJson.BodyContext.pubdocinfo.pubFFax);
	$("#email").text(formJson.BodyContext.pubdocinfo.pubFEmail);
	$("#publication").text(formJson.BodyContext.pubdocinfo.pubFPubliValue);
	$("#footcampaign").text(formJson.BodyContext.pubdocinfo.pubFCmpFoot);
	
	drawApvLineMobile();
}

function updateDocHWP(HwpCtrl) {
	try {
		//본문
		HwpCtrl.MoveToField("body", true, false, false);
		HwpCtrl.SetTextFile(formJson.BodyContext.tbContentElement.replaceAll("\r\n",""), "HTML", "insertfile", function(data){
			if(data.result) {
				HwpCtrl.GetTextFile("HWPML2X", "", function(data) {	
					var strHWP = Base64.utf8_to_b64(data);
					
					formJson.BodyContext.BODY_CONTEXT_HWP = strHWP;
					
					var params = {
						"FormInstID" : getInfo("FormInstanceInfo.FormInstID"),
						"BodyContext": Base64.utf8_to_b64(JSON.stringify(formJson.BodyContext))
					};
					
					$.ajax({
						type:"POST",
						url : "/approval/govDocs/updateDocHWP.do",
						data : params,
						dataType : "json",
						async:false,
						success:function(res){
							if(res.status == 'SUCCESS'){
								
							} else {
								Common.Error(Common.getDic("msg_apv_030")); //오류가 발생했습니다.
								return false;
							}	
						},
						error:function(response, status, error){
							CFN_ErrorAjax("/approval/govdocs/updateDocHWP.do", response, status, error);
						}
					});
				});
			} else {
				Common.Error(Common.getDic("msg_apv_030")); //오류가 발생했습니다.
				return false;
			}
		});
    } catch (e) {
    	Common.Error(Common.getDic("msg_apv_030")); //오류가 발생했습니다.
    }
}

/*기안문 발신 결재선 표현*/
function getRequestApvListDraft_V1(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge) {//신청서html
	var sDate = "";
	if(formJson.FormInstanceInfo.InitiatedDate != null && formJson.FormInstanceInfo.InitiatedDate != ""){
		sDate = formJson.FormInstanceInfo.InitiatedDate;
	}else{
		sDate = formJson.AppInfo.svdt;
	}
	var dtDate = formatDateCVT(sDate);
	var createDate = new Date(dtDate.replace(/-/g, "/").replace(/오후/, "pm").replace(/오전/, "am"));
	var checkDate = new Date("2015-05-30T00:00:00Z");
	if (createDate.getTime() > checkDate.getTime()) {
		if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") { //노사발전회원회
			getRequestApvListDraft_V2(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge);
		} else if (formJson.AppInfo.dpid_apv == "100990000") {
			getRequestApvListDraft_V2(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge);
		} else {
			getRequestApvListDraft_V2(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge);
		}
		return;
	}
    var elm, elmTaskInfo, elmReceive, elmApv;
    var strDate;
    var j = 0;
    var Apvlines = "";
    var ApvPOSLines = ""; //부서명 or 발신부서,신청부서,담당부서,수신부서 등등 으로 표기<tr>
    var ApvTitleLines = "";
    var ApvSignLines = ""; //결재자 사인이미지 들어가는 부분<tr>
    var ApvApproveNameLines = ""; //결재자 성명 및 contextmenu 및 </br>붙임 후 결재일자 표기<tr>
    var ApvDateLines = ""; //사용안함
    var ApvCmt = "<tr>";
    var strColTD = elmList.length - elmVisible.length;
    var appidx = 0;
    var idx = 1;
    var cntDPLine = tableLineMax;
    var defaultAppRow = 2;
    
    HwpCtrl = document.getElementById("tbContentElement");
    if (HwpCtrl != null) {
        SetHwpEditMode(HwpCtrl, "edit");

        if (HwpCtrl.GetFieldText("APPROVER130NAME").replace(" ", "") != "") {
            HwpCtrl.MoveToField("APPROVER130BG");
            HwpCtrl.Run("TableCellBorderDiagonalUp");
        }
        HwpCtrl.PutFieldText("DRAFTERNAME", " ");
        HwpCtrl.PutFieldText("DRAFTERSIGN", " ");

    	try {
    		if (getInfo("Request.templatemode") == "Read") {
    			if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") {
    				HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
    				HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
    			} else if (formJson.AppInfo.dpid_apv == "100990000") {
    				HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
    				HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
    			}
    		} else {
    			if (formJson.AppInfo.dpid_apv == "100990000") {
    				HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
    				HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
    			}
    		}
    	} catch (e) {
    		HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
    	}

        HwpCtrl.PutFieldText("APPROVER130NAME", " ");
        HwpCtrl.PutFieldText("APPROVER130SIGN", " ");

        while (true) {
            if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
                //HwpCtrl.MoveToField("APPROVERTITLE");
                //HwpCtrl.Run("TableCellBorderDiagonalUp");
                HwpCtrl.MoveToField("APPROVERBG");
                HwpCtrl.Run("TableCellBorderDiagonalUp");
            }
        	if (getInfo("Request.templatemode") == "Read") {
        		if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
        		} else if (formJson.AppInfo.dpid_apv == "100990000") {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
        		} else if (formJson.AppInfo.dpid_apv == "1545") {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "실장");
        		} else {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "팀장");
        		}
        	} else {
        		if (formJson.AppInfo.dpid_apv == "100990000") {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
        		} else if (formJson.AppInfo.dpid_apv == "1545") {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "실장");
        		} else {
        			HwpCtrl.PutFieldText("APPROVERTITLE", "팀장");
        		}
        	}
            HwpCtrl.PutFieldText("APPROVERNAME", " ");
            HwpCtrl.PutFieldText("APPROVERSIGN", " "); 
            if (!HwpCtrl.MoveToField("APPROVERTITLE")) {
                break;
            }
            HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 열의 끝으로 이동								
            DeleteField();
        }
        $$(elmList).each(function (i, elm) {

            elmTaskInfo = $$(elm).find("taskinfo");

            //전달, 후열은 결재방에 제외
            if (elmTaskInfo.attr("kind") != 'conveyance' && elmTaskInfo.attr("kind") != 'bypass') {
                if ((bDisplayCharge && i == 0) || (elmTaskInfo.attr("visible") != "n")) //결재선 숨기기한 사람 숨기기
                {
                    strDate = $$(elmTaskInfo).attr("datecompleted");
                    if (strDate == null && $$(elmTaskInfo).attr("result") != "reserved") {   //보류도 결재방에 표시
                        strDate = "";
                    }

                    var temp_charge = "";
                    if (elmTaskInfo.attr("kind") == 'charge') {
                        HwpCtrl.PutFieldText("DRAFTERNAME", CFN_GetDicInfo($$(elm).attr("name")));
                        if (strDate != "") {
                            getSignUrlDraft_V1(HwpCtrl, "DRAFTERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                        }
                    }
                    else {
                        var sTitle = "";
                        try {
                            sTitle = $$(elm).attr("title");
                            if (getLngLabel(sTitle, true) == "") sTitle = $$(elm).attr("position"); // kckangy update 2005.02.26
                            sTitle = getLngLabel(sTitle, true);
                        } catch (e) {
                            if ($$(elm).nodename() == "role") {
                                sTitle = $$(elm).attr("name");
                                sTitle = sTitle.substr(sTitle.length - 2);
                            }
                        }
                        if (sTitle == Common.getDic("lbl_apv_charge_person")) {	//m_oFormMenu.gLabel_charge_person) {
                            sTitle = Common.getDic("lbl_apv_charge");	//m_oFormMenu.gLabel_charge; //"담당"
                        }
                        ApvTitleLines += "";

                        ApvDateLines += "";
                        ApvSignLines += "";
                        ApvApproveNameLines += "";

                        var sCode = "";
                        var elmtemp;
                        if ($$(elm).nodename() == "role")
                            try { sCode = $$(elm).find("person").attr("code"); elmtemp = elm.find("person"); } catch (e) { }
                        else
                            sCode = $$(elm).attr("code");

                        var elmname = (elmtemp != null) ? elmtemp : elm;

                        var bRejected = false;
                        if ($$(elmTaskInfo).attr("result") == "rejected") {
                            bRejected = true;
                        }

                        var sTitleCode = $$(elm).attr("title").split(";")[0];
                        if (sTitleCode == "T100") {//부서장(부서장은 계속 갱신된다, 즉 마지막 부서장만 표시)
                            if (HwpCtrl.GetFieldText("APPROVERNAME1").replace(" ", "") == "") {
                                //HwpCtrl.MoveToField("APPROVERTITLE1");
                                //HwpCtrl.Run("TableCellBorderDiagonalUp");
                                HwpCtrl.MoveToField("APPROVERBG1");
                                HwpCtrl.Run("TableCellBorderDiagonalUp");
                            }
                            HwpCtrl.PutFieldText("APPROVERTITLE1", sTitle);
                            HwpCtrl.PutFieldText("APPROVERNAME1", CFN_GetDicInfo($$(elm).attr("name")));
                            if (appidx == strColTD - 1) {
                                HwpCtrl.PutFieldText("APPROVERSIGN1", "[전결]");
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                            } else {
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("APPROVERSIGN1", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN1", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                            }
                        } else if (sTitleCode == "T200") {//팀장(팀장은 계속 갱신된다, 즉 마지막 팀장만 표시)
                            if (HwpCtrl.GetFieldText("APPROVERNAME2").replace(" ", "") == "") {
                                //HwpCtrl.MoveToField("APPROVERTITLE2");
                                //HwpCtrl.Run("TableCellBorderDiagonalUp");
                                HwpCtrl.MoveToField("APPROVERBG2");
                                HwpCtrl.Run("TableCellBorderDiagonalUp");
                            }
                            HwpCtrl.PutFieldText("APPROVERTITLE2", sTitle);
                            HwpCtrl.PutFieldText("APPROVERNAME2", CFN_GetDicInfo($$(elm).attr("name")));
                            if (appidx == strColTD - 1) {
                                HwpCtrl.PutFieldText("APPROVERSIGN2", "[전결]");
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                            } else {
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("APPROVERSIGN2", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN2", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                            }
                        } else if (sTitleCode == "T080") {//본부장(본부장은 계속 갱신된다, 즉 마지막 본부장만 표시)
                            if (appidx == strColTD - 1) {
                                HwpCtrl.PutFieldText("APPROVER130SIGN", "[전결]");
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                                //document.getElementById("tdLastApproverSign").innerHTML = ((strDate == "") ? "&nbsp;" : ((bRejected) ? interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"))));
                                //요청에 의해 표시하지 않음 document.getElementById("tdLastApproverInfo").innerHTML = ApvDateLines;
                                //ApvSignLines = (strDate == "") ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]" : ((bRejected) ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]");
                                //document.getElementById("td130ApproverInfo").innerHTML = ApvSignLines;
                            } else {
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("APPROVER130SIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "APPROVER130SIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                                //document.getElementById("td130ApproverInfo").innerHTML = ApvSignLines;
                            }
                            if (HwpCtrl.GetFieldText("APPROVER130NAME").replace(" ", "") == "") {
                                HwpCtrl.MoveToField("APPROVER130BG");
                                HwpCtrl.Run("TableCellBorderDiagonalUp");
                            }
                            HwpCtrl.PutFieldText("APPROVER130NAME", CFN_GetDicInfo($$(elm).attr("name")));
                        } else if (appidx == strColTD - 1) {
                            if (strDate != "") {
                                if (bRejected) {
                                    HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                } else {
                                    getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                }
                            }
                            if (sTitleCode != "T_120_1" && sTitleCode != "T3053004_1" && sTitleCode != "T_910_1" && sTitleCode != "T3053009_1") {//소장, 위원장
                                //document.getElementById("tdLastApproverTitle").innerHTML = "소장";
                                //document.getElementById("tdLastApproverSign").innerHTML = ((strDate == "") ? "&nbsp;" : ((bRejected) ? interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"))));
                                //요청에 의해 표시하지 않음 document.getElementById("tdLastApproverInfo").innerHTML = ApvDateLines;
                                //ApvSignLines = (strDate == "") ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]" : ((bRejected) ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]");
                                if (idx > defaultAppRow) {
                                    AppendField(idx, "APPROVER", "Lower");
                                }
                                HwpCtrl.PutFieldText("APPROVERTITLE", sTitle);
                                HwpCtrl.PutFieldText("APPROVERNAME", CFN_GetDicInfo($$(elm).attr("name")));
                                HwpCtrl.PutFieldText("APPROVERSIGN", "[전결]");
                                if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
                                    HwpCtrl.MoveToField("APPROVERBG");
                                    HwpCtrl.Run("TableCellBorderDiagonalUp");
                                }
                                idx++;
                            }
                        } else {
                            if (idx > defaultAppRow) {
                                AppendField(idx, "APPROVER", "Lower");
                            }
                            HwpCtrl.PutFieldText("APPROVERTITLE", sTitle);
                            HwpCtrl.PutFieldText("APPROVERNAME", CFN_GetDicInfo($$(elm).attr("name")));
                            if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
                                HwpCtrl.MoveToField("APPROVERBG");
                                HwpCtrl.Run("TableCellBorderDiagonalUp");
                            }
                            if (strDate != "") {
                                if (bRejected) {
                                    HwpCtrl.PutFieldText("APPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                } else {
                                    getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                }
                            }

                            idx++;
                        }
                    }
                    appidx++
                }
            } else {
                strColTD--;
            }
        });

        if (getInfo("Request.templatemode") == "Read") {
            SetHwpEditMode(HwpCtrl, "read");
        } else {
            SetHwpEditMode(HwpCtrl, "form");
        }
    }
}

/*기안문 발신 결재선 표현(부서장과 실장 같은 곳에 표시)*/
function getRequestApvListDraft_V2(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge) {//신청서html
	var elm, elmTaskInfo, elmReceive, elmApv;
	var strDate;
	var j = 0;
	var Apvlines = "";
	var ApvPOSLines = ""; //부서명 or 발신부서,신청부서,담당부서,수신부서 등등 으로 표기<tr>
	var ApvTitleLines = "";
	var ApvSignLines = ""; //결재자 사인이미지 들어가는 부분<tr>
	var ApvApproveNameLines = ""; //결재자 성명 및 contextmenu 및 </br>붙임 후 결재일자 표기<tr>
	var ApvDateLines = ""; //사용안함
	var ApvCmt = "<tr>";
	var strColTD = elmList.length - elmVisible.length;
	var appidx = 0;
	var idx = 1;
	var cntDPLine = tableLineMax;
	var defaultAppRow = 2;

	//[2017-10-23] Covision - HJH : 기안문 대결시 서명이미지 표시방법(마지막 대결자 합의 갯수 제외)
	var chk_approve;
	var chk_bypass = 0;
	//chk_assist = $(getInfo('apst')).find("step[routetype='assist'][allottype='parallel']").length;
	chk_approve = $$(getInfo('ApprovalLine')).find("step[routetype='approve']").length - 1;
	chk_bypass = $($$(getInfo('ApprovalLine')).find("step[routetype='approve']")[chk_approve]).find("ou > person > taskinfo[kind='bypass']").length;
	strColTD = strColTD - chk_bypass;
	//[2017-10-23] Covision - HJH : 기안문 대결시 서명이미지 표시방법(마지막 대결자 합의 갯수 제외)

	HwpCtrl = document.getElementById("tbContentElement");
	if (HwpCtrl != null) {
		SetHwpEditMode(HwpCtrl, "edit");

		if (HwpCtrl.GetFieldText("APPROVER130NAME").replace(" ", "") != "") {
			HwpCtrl.MoveToField("APPROVER130BG");
			HwpCtrl.Run("TableCellBorderDiagonalUp");
		}
		HwpCtrl.PutFieldText("DRAFTERNAME", " ");
		HwpCtrl.PutFieldText("DRAFTERSIGN", " ");

		try {
			if (getInfo("Request.templatemode") == "Read") {
				if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") {
					HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
					HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
				} else if (formJson.AppInfo.dpid_apv == "100990000") {
					HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
					HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
				} else {
					HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
				}
			} else {
				if (formJson.AppInfo.dpid_apv == "100990000") {
					HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
					HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
				} else {
					HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
				}
			}
		} catch (e) {
			HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
		}

		HwpCtrl.PutFieldText("APPROVER130NAME", " ");
		HwpCtrl.PutFieldText("APPROVER130SIGN", " ");

		while (true) {
			if (idx <= defaultAppRow) {
				if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
					//HwpCtrl.MoveToField("APPROVERTITLE");
					//HwpCtrl.Run("TableCellBorderDiagonalUp");
					HwpCtrl.MoveToField("APPROVERBG");
					HwpCtrl.Run("TableCellBorderDiagonalUp");
				}
				if (getInfo("Request.templatemode") == "Read") {
					if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") {
						HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
					} else if (formJson.AppInfo.dpid_apv == "100990000") {
						HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
					} else {
						HwpCtrl.PutFieldText("APPROVERTITLE", "팀장");
					}
				} else {
					if (formJson.AppInfo.dpid_apv == "100990000") {
						HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
					} else {
						HwpCtrl.PutFieldText("APPROVERTITLE", "팀장");
					}
				}
				HwpCtrl.PutFieldText("APPROVERNAME", " ");
				HwpCtrl.PutFieldText("APPROVERSIGN", " ");
			} else {
				if (!HwpCtrl.MoveToField("APPROVERTITLE")) {
					break;
				}
				HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 열의 끝으로 이동								
				DeleteField();
			}
			idx++;
		}
		idx = 3;
		$$(elmList).each(function (i, elm) {

			elmTaskInfo = $$(elm).find("taskinfo");

			//전달, 후열은 결재방에 제외
			if (elmTaskInfo.attr("kind") != 'conveyance' && elmTaskInfo.attr("kind") != 'bypass') {
				if ((bDisplayCharge && i == 0) || (elmTaskInfo.attr("visible") != "n")) //결재선 숨기기한 사람 숨기기
				{
					strDate = $$(elmTaskInfo).attr("datecompleted");
					if (strDate == null && $$(elmTaskInfo).attr("result") != "reserved") {   //보류도 결재방에 표시
						strDate = "";
					}

					var temp_charge = "";
					if (elmTaskInfo.attr("kind") == 'charge') {
						HwpCtrl.PutFieldText("DRAFTERNAME", CFN_GetDicInfo($$(elm).attr("name")));
						if (strDate != "") {
							getSignUrlDraft_V1(HwpCtrl, "DRAFTERSIGN", sCode, elmTaskInfo.attr("customattribute1"), $$(elm).attr("name"), strDate, true, elmTaskInfo.attr("result"), true, 'list', elmTaskInfo.attr("kind"));
						}
					}
					else {
						var sTitle = "";
						try {
							sTitle = $$(elm).attr("title");
							if (getLngLabel(sTitle, true) == "") sTitle = $$(elm).attr("position"); // kckangy update 2005.02.26
							sTitle = getLngLabel(sTitle, true);
						} catch (e) {
							if ($$(elm).nodename() == "role") {
								sTitle = $$(elm).attr("name");
								sTitle = sTitle.substr(sTitle.length - 2);
							}
						}
	                    if (sTitle == Common.getDic("lbl_apv_charge_person")) {	//m_oFormMenu.gLabel_charge_person) {
	                        sTitle = Common.getDic("lbl_apv_charge");	//m_oFormMenu.gLabel_charge; //"담당"
	                    }
						ApvTitleLines += "";

						ApvDateLines += "";
						ApvSignLines += "";
						ApvApproveNameLines += "";

						var sCode = "";
						var elmtemp;
						if ($$(elm).nodename() == "role")
							try { sCode = $$(elm).find("person").attr("code"); elmtemp = $$(elm).find("person"); } catch (e) { }
						else
							sCode = $$(elm).attr("code");

						var elmname = (elmtemp != null) ? elmtemp : elm;

						var bRejected = false;
						if ($$(elmTaskInfo).attr("result") == "rejected") {
							bRejected = true;
						}

						var sTitleCode = $$(elm).attr("title").split(";")[0];
						if (sTitleCode == "T100") {//부서장(부서장은 계속 갱신된다, 즉 마지막 부서장만 표시)
							if (HwpCtrl.GetFieldText("APPROVERNAME1").replace(" ", "") == "") {
								//HwpCtrl.MoveToField("APPROVERTITLE1");
								//HwpCtrl.Run("TableCellBorderDiagonalUp");
								HwpCtrl.MoveToField("APPROVERBG1");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE1", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME1", CFN_GetDicInfo($$(elm).attr("name")));
							if (appidx == strColTD - 1) {
								HwpCtrl.PutFieldText("APPROVERSIGN1", "[전결]");
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVERSIGN1", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN1", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							}
						} else if (sTitleCode == "T200") {//팀장(팀장은 계속 갱신된다, 즉 마지막 팀장만 표시)
							if (HwpCtrl.GetFieldText("APPROVERNAME2").replace(" ", "") == "") {
								HwpCtrl.MoveToField("APPROVERBG2");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE2", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME2", CFN_GetDicInfo($$(elm).attr("name")));
							if (appidx == strColTD - 1) {
								HwpCtrl.PutFieldText("APPROVERSIGN2", "[전결]");
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVERSIGN2", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN2", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							}
						} else if (sTitleCode == "T080") {//본부장/실장(본부장/실장은 계속 갱신된다, 즉 마지막 본부장/실장만 표시)
							if (appidx == strColTD - 1) {
								if (chk_bypass != 1) {	//[2017-10-23] 최종 결재자 대결일 경우 누락
									HwpCtrl.PutFieldText("APPROVER130SIGN", "[전결]");
								}
								
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
								//document.getElementById("tdLastApproverSign").innerHTML = ((strDate == "") ? "&nbsp;" : ((bRejected) ? interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"))));
								//요청에 의해 표시하지 않음 document.getElementById("tdLastApproverInfo").innerHTML = ApvDateLines;
								//ApvSignLines = (strDate == "") ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]" : ((bRejected) ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;" + interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]");
								//document.getElementById("td130ApproverInfo").innerHTML = ApvSignLines;
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVER130SIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVER130SIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
								//document.getElementById("td130ApproverInfo").innerHTML = ApvSignLines;
							}
							if (HwpCtrl.GetFieldText("APPROVER130NAME").replace(" ", "") == "") {
								HwpCtrl.MoveToField("APPROVER130BG");

								if (chk_bypass != 1) {	//[2017-10-23] 최종 결재자 대결일 경우 누락
									HwpCtrl.Run("TableCellBorderDiagonalUp");
								}								
							}
							
							HwpCtrl.PutFieldText("APPROVER130TITLE", CFN_GetDicInfo(sTitle));

							if (chk_bypass != 1) {	//[2017-10-23] 최종 결재자 대결일 경우 누락
								HwpCtrl.PutFieldText("APPROVER130NAME", CFN_GetDicInfo($$(elm).attr("name")));
							}

						} else if (appidx == strColTD - 1) {
							if (strDate != "") {
								if (bRejected) {
									HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
								} else {
									getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
								}
							}
							if (sTitleCode != "T_120_1" && sTitleCode != "T3053004_1" && sTitleCode != "T_910_1" && sTitleCode != "T3053009_1") {//소장, 위원장
								//document.getElementById("tdLastApproverTitle").innerHTML = "소장";
								//document.getElementById("tdLastApproverSign").innerHTML = ((strDate == "") ? "&nbsp;" : ((bRejected) ? interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"))));
								//요청에 의해 표시하지 않음 document.getElementById("tdLastApproverInfo").innerHTML = ApvDateLines;
								//ApvSignLines = (strDate == "") ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]" : ((bRejected) ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;" + interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]");
								if (idx > defaultAppRow) {
									AppendField(idx, "APPROVER", "Lower");
								}
								HwpCtrl.PutFieldText("APPROVERTITLE", sTitle);
								HwpCtrl.PutFieldText("APPROVERNAME", CFN_GetDicInfo($$(elm).attr("name")));
								HwpCtrl.PutFieldText("APPROVERSIGN", "[전결]");
								if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
									HwpCtrl.MoveToField("APPROVERBG");
									HwpCtrl.Run("TableCellBorderDiagonalUp");
								}
								idx++;
							}
						} else {
							if (idx > defaultAppRow) {
								AppendField(idx, "APPROVER", "Lower");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME", CFN_GetDicInfo($$(elm).attr("name")));
							if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
								HwpCtrl.MoveToField("APPROVERBG");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							if (strDate != "") {
								if (bRejected) {
									HwpCtrl.PutFieldText("APPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
								} else {
									getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
								}
							}

							idx++;
						}
					}
					appidx++
				}
			} else {
				strColTD--;
			}
		});

		if (getInfo("Request.templatemode") == "Read") {
			SetHwpEditMode(HwpCtrl, "read");
		} else {
			SetHwpEditMode(HwpCtrl, "form");
		}
	}
}

/*기안문 발신 결재선 표현(부서장과 실장 다른 곳에 표시)*/
function getRequestApvListDraft_V3(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge) {//신청서html
	var elm, elmTaskInfo, elmReceive, elmApv;
	var strDate;
	var j = 0;
	var Apvlines = "";
	var ApvPOSLines = ""; //부서명 or 발신부서,신청부서,담당부서,수신부서 등등 으로 표기<tr>
	var ApvTitleLines = "";
	var ApvSignLines = ""; //결재자 사인이미지 들어가는 부분<tr>
	var ApvApproveNameLines = ""; //결재자 성명 및 contextmenu 및 </br>붙임 후 결재일자 표기<tr>
	var ApvDateLines = ""; //사용안함
	var ApvCmt = "<tr>";
	var strColTD = elmList.length - elmVisible.length;
	var appidx = 0;
	var idx = 1;
	var cntDPLine = tableLineMax;
	var defaultAppRow = 3;

	//[2017-10-23] Covision - HJH : 기안문 대결시 서명이미지 표시방법(마지막 대결자 합의 갯수 제외)
	var chk_approve;
	var chk_bypass = 0;
	//chk_assist = $(getInfo('apst')).find("step[routetype='assist'][allottype='parallel']").length;
	chk_approve = $$(getInfo('ApprovalLine')).find("step[routetype='approve']").length - 1;
	chk_bypass = $($$(getInfo('ApprovalLine')).find("step[routetype='approve']")[chk_approve]).find("ou > person > taskinfo[kind='bypass']").length;
	strColTD = strColTD - chk_bypass;
	//[2017-10-23] Covision - HJH : 기안문 대결시 서명이미지 표시방법(마지막 대결자 합의 갯수 제외)

	HwpCtrl = document.getElementById("tbContentElement");
	if (HwpCtrl != null) {
		SetHwpEditMode(HwpCtrl, "edit");

		if (HwpCtrl.GetFieldText("APPROVER130NAME").replace(" ", "") != "") {
			HwpCtrl.MoveToField("APPROVER130BG");
			HwpCtrl.Run("TableCellBorderDiagonalUp");
		}
		HwpCtrl.PutFieldText("DRAFTERNAME", " ");
		HwpCtrl.PutFieldText("DRAFTERSIGN", " ");

		try {
			if (getInfo("Request.templatemode") == "Read") {
				if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") {
					HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
					HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
				} else if (formJson.AppInfo.dpid_apv == "100990000") {
					HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
					HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
				} else {
					HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
				}
			} else {
				if (formJson.AppInfo.dpid_apv == "100990000") {
					HwpCtrl.PutFieldText("APPROVER130TITLE", " ");
					HwpCtrl.PutFieldText("LASTAPPROVERTITLE", "노사발전위원장");
				} else {
					HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
				}
			}
		} catch (e) {
			HwpCtrl.PutFieldText("APPROVER130TITLE", "본부장");
		}

		HwpCtrl.PutFieldText("APPROVER130NAME", " ");
		HwpCtrl.PutFieldText("APPROVER130SIGN", " ");

		while (true) {
			if (idx <= defaultAppRow) {
				if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
					HwpCtrl.MoveToField("APPROVERBG");
					HwpCtrl.Run("TableCellBorderDiagonalUp");
				}
				if (getInfo("Request.templatemode") == "Read") {
					if (formJson.FormInstanceInfo.InitiatorUnitID != null && formJson.FormInstanceInfo.InitiatorUnitID == "100990000") {
						HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
					} else if (formJson.AppInfo.dpid_apv == "100990000") {
						HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
					} else {
						HwpCtrl.PutFieldText("APPROVERTITLE", "실장");
					}
				} else {
					if (formJson.AppInfo.dpid_apv == "100990000") {
						HwpCtrl.PutFieldText("APPROVERTITLE", "사무처장");
					} else {
						HwpCtrl.PutFieldText("APPROVERTITLE", "실장");
					}
				}
				HwpCtrl.PutFieldText("APPROVERNAME", " ");
				HwpCtrl.PutFieldText("APPROVERSIGN", " ");
			} else {
				if (!HwpCtrl.MoveToField("APPROVERTITLE")) {
					break;
				}
				HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 열의 끝으로 이동								
				DeleteField();
			}
			idx++;
		}
		idx = 4;
		$$(elmList).each(function (i, elm) {

			elmTaskInfo = $$(elm).find("taskinfo");

			//전달, 후열은 결재방에 제외
			if (elmTaskInfo.attr("kind") != 'conveyance' && elmTaskInfo.attr("kind") != 'bypass') {
				if ((bDisplayCharge && i == 0) || (elmTaskInfo.attr("visible") != "n")) //결재선 숨기기한 사람 숨기기
				{
					strDate = $$(elmTaskInfo).attr("datecompleted");
					if (strDate == null && $$(elmTaskInfo).attr("result") != "reserved") {   //보류도 결재방에 표시
						strDate = "";
					}

					var temp_charge = "";
					if (elmTaskInfo.attr("kind") == 'charge') {
						HwpCtrl.PutFieldText("DRAFTERNAME", CFN_GetDicInfo($$(elm).attr("name")));
						if (strDate != "") {
							getSignUrlDraft_V1(HwpCtrl, "DRAFTERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
						}
					}
					else {
						var sTitle = "";
						try {
							sTitle = $$(elm).attr("title");
							if (getLngLabel(sTitle, true) == "") sTitle = $$(elm).attr("position"); // kckangy update 2005.02.26
							sTitle = getLngLabel(sTitle, true);
						} catch (e) {
							if ($$(elm).nodename() == "role") {
								sTitle = $$(elm).attr("name");
								sTitle = sTitle.substr(sTitle.length - 2);
							}
						}
	                    if (sTitle == Common.getDic("lbl_apv_charge_person")) {	//m_oFormMenu.gLabel_charge_person) {
	                        sTitle = Common.getDic("lbl_apv_charge");	//m_oFormMenu.gLabel_charge; //"담당"
	                    }
						ApvTitleLines += "";

						ApvDateLines += "";
						ApvSignLines += "";
						ApvApproveNameLines += "";

						var sCode = "";
						var elmtemp;
						if ($$(elm).nodename() == "role")
							try { sCode = $$(elm).find("person").attr("code"); elmtemp = elm.find("person"); } catch (e) { }
						else
							sCode = $$(elm).attr("code");

						var elmname = (elmtemp != null) ? elmtemp : elm;

						var bRejected = false;
						if ($$(elmTaskInfo).attr("result") == "rejected") {
							bRejected = true;
						}

						var sTitleCode = $$(elm).attr("title").split(";")[0];
						if (sTitleCode == "T100") {//부서장(부서장은 계속 갱신된다, 즉 마지막 부서장만 표시)
							if (HwpCtrl.GetFieldText("APPROVERNAME1").replace(" ", "") == "") {
								HwpCtrl.MoveToField("APPROVERBG1");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE1", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME1", CFN_GetDicInfo($$(elm).attr("name")));
							if (appidx == strColTD - 1) {
								HwpCtrl.PutFieldText("APPROVERSIGN1", "[전결]");
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVERSIGN1", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN1", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							}
						} else if ((sTitleCode == "T200")) {//팀장(팀장은 계속 갱신된다, 즉 마지막 팀장만 표시)
							if (HwpCtrl.GetFieldText("APPROVERNAME2").replace(" ", "") == "") {
								HwpCtrl.MoveToField("APPROVERBG2");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE2", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME2", CFN_GetDicInfo($$(elm).attr("name")));
							if (appidx == strColTD - 1) {
								HwpCtrl.PutFieldText("APPROVERSIGN2", "[전결]");
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVERSIGN2", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN2", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							}
						} else if (sTitleCode == "T100") {//부서장(부서장은 계속 갱신된다, 즉 마지막 부서장만 표시) //TODO: 실장????
							if (HwpCtrl.GetFieldText("APPROVERNAME3").replace(" ", "") == "") {
								HwpCtrl.MoveToField("APPROVERBG3");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE3", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME3", CFN_GetDicInfo($$(elm).attr("name")));
							if (appidx == strColTD - 1) {
								HwpCtrl.PutFieldText("APPROVERSIGN3", "[전결]");
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVERSIGN3", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN3", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
							}
						} else if (sTitleCode == "T080") {//본부장(본부장은 계속 갱신된다, 즉 마지막 본부장만 표시)
							if (appidx == strColTD - 1) {
								if (chk_bypass != 1) {	//[2017-10-23] 최종 결재자 대결일 경우 누락
									HwpCtrl.PutFieldText("APPROVER130SIGN", "[전결]");
								}
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
								//document.getElementById("tdLastApproverSign").innerHTML = ((strDate == "") ? "&nbsp;" : ((bRejected) ? interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"))));
								//요청에 의해 표시하지 않음 document.getElementById("tdLastApproverInfo").innerHTML = ApvDateLines;
								//ApvSignLines = (strDate == "") ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]" : ((bRejected) ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;" + interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]");
								//document.getElementById("td130ApproverInfo").innerHTML = ApvSignLines;
							} else {
								if (strDate != "") {
									if (bRejected) {
										HwpCtrl.PutFieldText("APPROVER130SIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
									} else {
										getSignUrlDraft_V1(HwpCtrl, "APPROVER130SIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
									}
								}
								//document.getElementById("td130ApproverInfo").innerHTML = ApvSignLines;
							}
							if (HwpCtrl.GetFieldText("APPROVER130NAME").replace(" ", "") == "") {
								HwpCtrl.MoveToField("APPROVER130BG");
								if (chk_bypass != 1) {	//[2017-10-23] 최종 결재자 대결일 경우 누락
									HwpCtrl.Run("TableCellBorderDiagonalUp");
								}					
							}

							HwpCtrl.PutFieldText("APPROVER130TITLE", CFN_GetDicInfo(sTitle));							
							if (chk_bypass != 1) {	//[2017-10-23] 최종 결재자 대결일 경우 누락
								HwpCtrl.PutFieldText("APPROVER130NAME", CFN_GetDicInfo($$(elm).attr("name")));
							}

						} else if (appidx == strColTD - 1) {
							if (strDate != "") {
								if (bRejected) {
									HwpCtrl.PutFieldText("LASTAPPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
								} else {
									getSignUrlDraft_V1(HwpCtrl, "LASTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
								}
							}
							if (sTitleCode != "T_120_1" && sTitleCode != "T3053004_1" && sTitleCode != "T_910_1" && sTitleCode != "T3053009_1") {//소장, 위원장
								//document.getElementById("tdLastApproverTitle").innerHTML = "소장";
								//document.getElementById("tdLastApproverSign").innerHTML = ((strDate == "") ? "&nbsp;" : ((bRejected) ? interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"))));
								//요청에 의해 표시하지 않음 document.getElementById("tdLastApproverInfo").innerHTML = ApvDateLines;
								//ApvSignLines = (strDate == "") ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]" : ((bRejected) ? CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;" + interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) : CFN_GetDicInfo($$(elm).attr("name")) + "&nbsp;[전결]");
								if (idx > defaultAppRow) {
									AppendField(idx, "APPROVER", "Lower");
								}
								HwpCtrl.PutFieldText("APPROVERTITLE", sTitle);
								HwpCtrl.PutFieldText("APPROVERNAME", CFN_GetDicInfo($$(elm).attr("name")));
								HwpCtrl.PutFieldText("APPROVERSIGN", "[전결]");
								if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
									HwpCtrl.MoveToField("APPROVERBG");
									HwpCtrl.Run("TableCellBorderDiagonalUp");
								}
								idx++;
							}
						} else {
							if (idx > defaultAppRow) {
								AppendField(idx, "APPROVER", "Lower");
							}
							HwpCtrl.PutFieldText("APPROVERTITLE", sTitle);
							HwpCtrl.PutFieldText("APPROVERNAME", CFN_GetDicInfo($$(elm).attr("name")));
							if (HwpCtrl.GetFieldText("APPROVERNAME").replace(" ", "") != "") {
								HwpCtrl.MoveToField("APPROVERBG");
								HwpCtrl.Run("TableCellBorderDiagonalUp");
							}
							if (strDate != "") {
								if (bRejected) {
									HwpCtrl.PutFieldText("APPROVERSIGN", "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
								} else {
									getSignUrlDraft_V1(HwpCtrl, "APPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
								}
							}

							idx++;
						}
					}
					appidx++
				}
			} else {
				strColTD--;
			}
		});

		if (getInfo("Request.templatemode") == "Read") {
			SetHwpEditMode(HwpCtrl, "read");
		} else {
			SetHwpEditMode(HwpCtrl, "form");
		}
	}
}

/*기안문 수신 결재선 표현*/
function getRequestApvListDraftRec_V1(elmList, elmVisible, sMode, bReceive, sApvTitle, bDisplayCharge) {
    var elm, elmTaskInfo, elmReceive, elmApv;
    var strDate;
    var j = 0;
    var Apvlines = "";
    var ApvPOSLines = ""; //부서명 or 발신부서,신청부서,담당부서,수신부서 등등 으로 표기<tr>
    var ApvTitleLines = "";
    var ApvSignLines = ""; //결재자 사인이미지 들어가는 부분<tr>
    var ApvApproveNameLines = ""; //결재자 성명 및 contextmenu 및 </br>붙임 후 결재일자 표기<tr>
    var ApvDateLines = ""; //사용안함
    var ApvCmt = "<tr>";
    var strColTD = elmList.length - elmVisible.length;
    var idx = 0;
    var cntDPLine = tableLineMax;

    HwpCtrl = document.getElementById("tbContentElement");
    if (HwpCtrl != null) {
        SetHwpEditMode(HwpCtrl, "edit");

        HwpCtrl.PutFieldText("FIRSTAPPROVERTITLE", " ");
        HwpCtrl.PutFieldText("FIRSTAPPROVERSIGN", " ");
        HwpCtrl.PutFieldText("FIRSTAPPROVERCMT", " ");
        HwpCtrl.PutFieldText("FIRSTAPPROVERDATE", " ");
        HwpCtrl.PutFieldText("FIRSTAPPROVERTIME", " ");
        HwpCtrl.PutFieldText("FIRSTAPPROVERDEPT", " ");

        HwpCtrl.PutFieldText("CHARGEPERSON", " ");
        HwpCtrl.PutFieldText("CHARGEPERSONSIGN", " ");
        
        while (true) {
            if (idx < 5) {
                HwpCtrl.PutFieldText("APPROVERRECTITLE", " ");
                HwpCtrl.PutFieldText("APPROVERRECSIGN", " ");
            } else {
                if (!HwpCtrl.MoveToField("APPROVERRECTITLE")) {
                    break;
                }
                HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 열의 끝으로 이동								
                DeleteField();
            }
            idx++;
        }
        idx = 0;

        var apvidx = 1;
        $$(elmList).each(function (i, elm) {
            elmTaskInfo = $$(elm).find("taskinfo");

            //전달, 후열은 결재방에 제외
            if (elmTaskInfo.attr("kind") != 'conveyance' && elmTaskInfo.attr("kind") != 'bypass') {
                if ((bDisplayCharge && i == 0) || (elmTaskInfo.attr("visible") != "n")) //결재선 숨기기한 사람 숨기기
                {
                    strDate = $$(elmTaskInfo).attr("datecompleted");
                    if (strDate == null && $$(elmTaskInfo).attr("result") != "reserved") {   //보류도 결재방에 표시
                        strDate = "";
                    }

                    var sTitle = "";
                    try {
                        sTitle = $$(elm).attr("title");
                        if (getLngLabel(sTitle, true) == "") sTitle = $$(elm).attr("position"); // kckangy update 2005.02.26
                        sTitle = getLngLabel(sTitle, true);
                    } catch (e) {
                        if ($$(elm).nodename() == "role") {
                            sTitle = $$(elm).attr("name");
                            sTitle = sTitle.substr(sTitle.length - 2);
                        }
                    }
                    if (sTitle == Common.getDic("lbl_apv_charge_person")) {	//m_oFormMenu.gLabel_charge_person) {
                        sTitle = Common.getDic("lbl_apv_charge");	//m_oFormMenu.gLabel_charge; //"담당"
                    }

                    var temp_charge = "";
                    if (elmTaskInfo.attr("kind") == 'charge') {
                        //표시하지 않는다.
                    }
                    else {
                        ApvTitleLines += "";

                        ApvDateLines += "";
                        ApvSignLines += "";
                        ApvApproveNameLines += "";

                        var sCode = "";
                        var elmtemp;
                        if ($$(elm).nodename() == "role")
                            try { sCode = $$(elm).find("person").attr("code"); elmtemp = elm.find("person"); } catch (e) { }
                        else
                            sCode = $$(elm).attr("code");

                        var elmname = (elmtemp != null) ? elmtemp : elm;

                        var bRejected = false;
                        if ($$(elmTaskInfo).attr("result") == "rejected") {
                            bRejected = true;
                        }

                        HwpCtrl.PutFieldText("FIRSTAPPROVERTITLE", sTitle);
                        HwpCtrl.PutFieldText("FIRSTAPPROVERDEPT", CFN_GetDicInfo($$(elm).attr("ouname")));
                        if (strDate != "") {
                            getSignUrlDraft_V1(HwpCtrl, "FIRSTAPPROVERSIGN", sCode, $$(elmTaskInfo).attr("customattribute1"), $$(elm).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                            var assistcmt = $$(elm).find("taskinfo > comment");
                            if (assistcmt.length != 0) {
                                HwpCtrl.PutFieldText("FIRSTAPPROVERCMT", $$(assistcmt).text().replace("\n", "\r\n"));
                            }
                        }
                    }
                    idx++
                }
            } else {
                strColTD--;
            }
        });
        if (getInfo("Request.templatemode") == "Read") {
            SetHwpEditMode(HwpCtrl, "read");
        } else {
            SetHwpEditMode(HwpCtrl, "form");
        }
    }
}

function getSignUrlDraft_V1(HwpCtrl, field, apvid, signtype, apvname, sDate, bDisplayDate, sResult, breturn, sDisplayType, sKind) {
    //var rtn = "";
    var strWidth = "60px";
    var strHeight = "50px";

    if (sDisplayType != null && sDisplayType == "list") {
        if (sDate != "") {
            if (signtype != "" && signtype != null && signtype != "undefined") {
                if (signtype == "stamp") {
                    HwpCtrl.PutFieldText(field, interpretResult(sResult, "", sKind));
                }
                else {
                	if (HwpCtrl.MoveToField(field, true, false, true)) {
                		HwpCtrl.PutFieldText(field, " ");
                        var path = _HostFullName + g_BaseImgURL + signtype;
                        if (field.indexOf("ASSIST") > -1) {
                            var pCtrl = HwpCtrl.InsertPicture(path, true, 2, 0, 0, 0);
                        } else {
                            var pCtrl = HwpCtrl.InsertPicture(path, true, 3, 0, 0, 0);
                        }
                		//[2016-09-30] HJH - 대결자 서명 시, 代 추가
                        if (sKind == "substitute") {
                        	HwpCtrl.PutFieldText(field, "代");
                        	if (field.indexOf("ASSIST") > -1) {
                        		var pCtrl = HwpCtrl.InsertPicture(path, true, 1, false, false, 0, 10, 8);
                        		//var pCtrl = HwpCtrl.InsertPicture(path, true, 2, 0, 0, 0);
                        	} else {
                        		var pCtrl = HwpCtrl.InsertPicture(path, true, 1, false, false, 0, 10, 8);	//[2016-09-30] 가로10 세로8 Fix
                        		//var pCtrl = HwpCtrl.InsertPicture(path, true, 2, 0, 0, 0);
                        	}
                        }
                        else {
                        	//HwpCtrl.PutFieldText(field, " ");
                        }
                    }
                    //rtn += "<img src='http://" + _HostName + g_BaseImgURL + signtype + "' border=0 style='height:30px'>";
                }
            } else {
                if (bDisplayDate == false) {
                    HwpCtrl.PutFieldText(field, Common.GetDic("lbl_apv_sign_none"));
                } else {
                    HwpCtrl.PutFieldText(field, interpretResult(sResult, "", sKind));
                }
                //rtn += (bDisplayDate == false) ? Common.GetDic("lbl_apv_sign_none") : interpretResult(sResult, "", sKind); //  + '<br>' + formatDate(sDate);
            }
        }
        //return rtn;
    } else {
        if (!breturn) {
            HwpCtrl.PutFieldText(field, CFN_GetDicInfo(apvname));
            //return CFN_GetDicInfo(apvname);
        } else {
            if (sDate != "") {
                if (signtype != "" && signtype != null && signtype != "undefined") {
                    if (signtype == "stamp") {
                        HwpCtrl.PutFieldText(field, CFN_GetDicInfo(apvname));
                        //return CFN_GetDicInfo(apvname);
                    }
                    else {
                        if (HwpCtrl.MoveToField(field, true, false, false)) {
                            var path = _HostFullName + g_BaseImgURL + signtype;
                            var pCtrl = HwpCtrl.InsertPicture(path, true, 3, 0, 0, 0);
                        }
                        //rtn += "<img src='http://" + _HostName + g_BaseImgURL + signtype + "' border=0 style='width:" + strWidth + ";height:" + strHeight + "'>";
                    }
                } else {
                    if (bDisplayDate == false) {
                        HwpCtrl.PutFieldText(field, Common.GetDic("lbl_apv_sign_none"));
                    } else {
                        HwpCtrl.PutFieldText(field, CFN_GetDicInfo(apvname));
                    }
                    //rtn += (bDisplayDate == false) ? Common.GetDic("lbl_apv_sign_none") : CFN_GetDicInfo(apvname); //  + '<br>' + formatDate(sDate);
                }
                //return rtn;
            }
        }
    }
}

//합의출력 기안지
function displayAssistDraft_V1(elmRoot) {
	var Apvlines = "";
    var Apvlines130 = "";
    var elmOPList, elmOUList, elmListCount;
    var idx130 = 1;
    var idx = 1;
    var strDate, LastTitle, LastCmt, LastResult;
    HwpCtrl = document.getElementById("tbContentElement");
    if (HwpCtrl != null) {
        SetHwpEditMode(HwpCtrl, "edit");

        while (true) {
            if (idx < 2) {
                HwpCtrl.PutFieldText("ASSISTTITLE", " ");
                HwpCtrl.PutFieldText("ASSISTNAME", " ");
                HwpCtrl.PutFieldText("ASSISTSIGN", " ");
            } else {
                if (!HwpCtrl.MoveToField("ASSISTTITLE")) {
                    break;
                }
                HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 열의 끝으로 이동								
                DeleteField();
            }
            idx++;
        }
        idx = 1;

        while (true) {
            if (idx130 < 2) {
                HwpCtrl.PutFieldText("ASSIST130TITLE" + idx130.toString(), " ");
                HwpCtrl.PutFieldText("ASSIST130NAME" + idx130.toString(), " ");
                HwpCtrl.PutFieldText("ASSIST130SIGN" + idx130.toString(), " ");
            } else {
                if (!HwpCtrl.MoveToField("ASSIST130TITLE" + idx130.toString())) {
                    break;
                }
                HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 열의 끝으로 이동								
                DeleteField();
            }
            idx130++;
        }
        idx130 = 1;

        elmOPList = $$(elmRoot).find("division > step[routetype='consult'][unittype='person'] > ou > person,division > step[routetype='assist'][unittype='person'] > ou > person");
        elmOUList = $$(elmRoot).find("division > step[routetype='consult'][unittype='ou'] > ou, division > step[routetype='assist'][unittype='ou'] > ou"); //부서협조		
        elmListCount = elmOPList.length + elmOUList.length;

        if (elmListCount != 0) {
            Apvlines130 = "";
            Apvlines = "";
            $$(elmOPList).each(function (index, element) {
                var sCode = "";
                var sTitle = "";
                if ($$(element).nodename() == "role") {
                    try { sCode = $$(element).find("person").code; } catch (e) { }
                    try { sTitle = CFN_GetDicInfo($$(element).attr("name")); } catch (e) { }
                } else {
                    sCode = $$(element).attr("code");
                    sTitle = getLngLabel($$(element).attr("position"), true) + " " + CFN_GetDicInfo($$(element).attr("name"));
                }
                var elmTaskInfo = $$(element).find("taskinfo");

                var bRejected = false;
                if ($$(elmTaskInfo).attr("result") == "rejected") {
                    bRejected = true;
                }
                if (elmTaskInfo.attr("visible") != "n") {
                    strDate = $$(elmTaskInfo).attr("datecompleted");
                    if (strDate == null) { strDate = ""; }
                    var assistcmt = $$(element).find("taskinfo > comment"); //이준희(2007-07-06): 문서창 상단에서 협조자의 의견이 잘리지 않도록 수정함.
                    switch ($$(elmTaskInfo).kind) {
                        case "substitute":
                            //LastTitle = getPresence(sCode, "assist" + i + sCode, $$(element).attr("sipaddress")) + CFN_GetDicInfo($(element).attr("name"));
                            //LastCmt = ($(assistcmt).text() == null) ? "&nbsp;" : $(assistcmt).text().replace(/\n/g, "<br />");
                            //LastResult = ((strDate == "") ? "&nbsp;" : formatDate(strDate) + interpretResult($(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")));
                            break;
                        case "bypass":
                            //Apvlines += "<tr>";
                            //Apvlines += "<td>" + CFN_GetDicInfo($(element).attr("ouname")) + "</td>"; //이준희(2007-07-06): 문서창 상단에서 협조자의 의견이 잘리지 않도록 수정함.
                            //Apvlines += "<td>" + sTitle + " " + m_oFormMenu.gLabel_substitue + " " + LastTitle + "</td>";
                            //Apvlines += "<td>" + LastResult + "</td>";
                            //Apvlines += "<td>" + LastCmt + "</td>";
                            //Apvlines += "</tr>";
                            //break; //"후열"
                        default:
                            //var strApvlines = "<p style='color:#444444'>"
                            //            + CFN_GetDicInfo($(element).attr("ouname"))
                            //            + "장&nbsp;"
                            //            + CFN_GetDicInfo($(element).attr("name"))
                            //            + "&nbsp;"
                            //            + ((strDate == "") ? "&nbsp;" : getSignUrlDraft(sCode, $$(elmTaskInfo).attr("customattribute1"), $$(element).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind")))
                            //            + "</p>";

                            var sTitleCode = $$(element).attr("title").split(";")[0];
                            if (sTitleCode == "T080") {//본부장
                                //Apvlines130 += strApvlines;
                                if (idx130 > 1) {
                                    //AppendField(idx130, "ASSIST130", "Lower");
                                    AppendField(idx130, "ASSIST130", "Upper");
                                }
                                HwpCtrl.PutFieldText("ASSIST130TITLE" + idx130.toString(), CFN_GetDicInfo($$(element).attr("ouname")) + "장");
                                HwpCtrl.PutFieldText("ASSIST130NAME" + idx130.toString(), CFN_GetDicInfo($$(element).attr("name")));
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("ASSIST130SIGN" + idx130.toString(), "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "ASSIST130SIGN" + idx130.toString(), sCode, $$(elmTaskInfo).attr("customattribute1"), $$(element).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                                idx130++;
                            } else {
                                //Apvlines += strApvlines;
                                if (idx > 1) {
                                    //AppendField(idx, "ASSIST", "Lower");
                                    AppendField(idx, "ASSIST", "Upper");
                                }
                                HwpCtrl.PutFieldText("ASSISTTITLE" + idx.toString(), CFN_GetDicInfo($$(element).attr("ouname")) + "장");
                                HwpCtrl.PutFieldText("ASSISTNAME" + idx.toString(), CFN_GetDicInfo($$(element).attr("name")));
                                if (strDate != "") {
                                    if (bRejected) {
                                        HwpCtrl.PutFieldText("ASSISTSIGN" + idx.toString(), "[" + interpretResult($$(elmTaskInfo).attr("result"), "", $$(elmTaskInfo).attr("kind")) + "]");
                                    } else {
                                        getSignUrlDraft_V1(HwpCtrl, "ASSISTSIGN" + idx.toString(), sCode, $$(elmTaskInfo).attr("customattribute1"), $$(element).attr("name"), strDate, true, $$(elmTaskInfo).attr("result"), true, 'list', $$(elmTaskInfo).attr("kind"));
                                    }
                                }
                                idx++;
                            }
                            //Apvlines += "<td>" + CFN_GetDicInfo($(element).attr("ouname")) + "</td>"; //이준희(2007-07-06): 문서창 상단에서 협조자의 의견이 잘리지 않도록 수정함.
                            //Apvlines += "<td>" + sTitle + "</td>"; //이준희(2007-07-06): 문서창 상단에서 협조자의 의견이 잘리지 않도록 수정함.
                            //Apvlines += "<td>" + ((strDate == "") ? "&nbsp;" : formatDate(strDate) + " " + interpretResult($(elmTaskInfo).attr("result"))) + "</td>";
                            //Apvlines += "<td>" + (($(assistcmt).text() == null) ? "&nbsp;" : $(assistcmt).text().replace(/\n/g, "<br />")) + "</td>";
                            //Apvlines += "</tr>";
                            break;
                    }
                }
            });
        }
        if (getInfo("Request.templatemode") == "Read") {
            SetHwpEditMode(HwpCtrl, "read");
        } else {
            SetHwpEditMode(HwpCtrl, "form");
        }
    }
}


// 결재줄을 추가한다.
function AppendField(idx, field, mode) {
    HwpCtrl.MoveToField(field + "TITLE" + (idx - 1).toString());
    HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 행(row)의 시작	
    if (mode == "Lower") {
        HwpCtrl.MovePos(103);			// 현재 캐럿이 위치한 셀의 아래쪽으로 이동
    } else {
        HwpCtrl.MovePos(102);			// 현재 캐럿이 위치한 셀의 위쪽으로 이동
    }
    HwpCtrl.Run("TableInsert" + mode + "Row");
    SetFieldsName(idx, field, mode);
}

// 결재줄을 추가한다.(시행문용)
function AppendFieldRec(idx, field, mode) {
    HwpCtrl.MoveToField(field + "TITLE" + (idx - 1).toString());
    if (mode == "Lower") {
        HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 행(row)의 시작	
        HwpCtrl.MovePos(103);			// 현재 캐럿이 위치한 셀의 아래쪽으로 이동
    } else {
        HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 행(row)의 시작	
        HwpCtrl.MovePos(102);			// 현재 캐럿이 위치한 셀의 위쪽으로 이동
    }
    HwpCtrl.Run("TableInsert" + mode + "Row");
    SetFieldsNameRec(idx, field, mode);
}


// 테이블 각 셀의 필드명을 다시 부여한다.
function SetFieldsName(idx, field, mode) {
    HwpCtrl.MoveToField(field + "TITLE" + (idx - 1).toString());
    if (mode == "Lower") {
        HwpCtrl.MovePos(20);			// 현재 캐럿이 위치한 셀에서 한 줄 아래로 이동.
    } else {
        HwpCtrl.MovePos(21);			// 현재 캐럿이 위치한 셀에서 한 줄 위로 이동.
    }
    HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 행(row)의 시작	
    HwpCtrl.SetCurFieldName(field + "TITLE");
    HwpCtrl.MovePos(101);			// 현재 캐럿이 위치한 셀에서 오른쪽으로 이동
    if (field == "APPROVER") {
        HwpCtrl.SetCurFieldName(field + "BG");
        if (HwpCtrl.GetFieldText("APPROVERNAME" + (idx - 1).toString()).replace(" ", "") != "") {
            //HwpCtrl.MoveToField(field + "TITLE");
            //HwpCtrl.Run("TableCellBorderDiagonalUp");
            HwpCtrl.MoveToField(field + "BG");
            HwpCtrl.Run("TableCellBorderDiagonalUp");
        }
        HwpCtrl.SetTextFile(document.getElementById("TempApproverAreaBlank").value, "HWP", "insertfile");
        HwpCtrl.MoveToField("TEMPNAME");
        HwpCtrl.SetCurFieldName(field + "NAME");
        HwpCtrl.MoveToField("TEMPSIGN");
        HwpCtrl.SetCurFieldName(field + "SIGN");
    }
    HwpCtrl.MovePos(2);
}

// 테이블 각 셀의 필드명을 다시 부여한다.(시행문용)
function SetFieldsNameRec(idx, field, mode) {
    HwpCtrl.MoveToField(field + "TITLE" + (idx - 1).toString());
    if (mode == "Lower") {
        HwpCtrl.MovePos(20);			// 현재 캐럿이 위치한 셀에서 한 줄 아래로 이동.
    } else {
        HwpCtrl.MovePos(21);			// 현재 캐럿이 위치한 셀에서 한 줄 위로 이동.
    }
    HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 행(row)의 시작	
    HwpCtrl.SetCurFieldName(field + "TITLE");
    HwpCtrl.MovePos(101);			// 현재 캐럿이 위치한 셀에서 오른쪽으로 이동
    HwpCtrl.SetCurFieldName(field + "SIGN");
    HwpCtrl.MovePos(104);			// 현재 캐럿이 위치한 셀에서 행의 시작으로 이동	
}

// 현재 캐럿이 있는 줄을 삭제한다.
function DeleteField() {
    HwpCtrl.Run("TableDeleteRow");
}

function fnPutFieldText(idx, field, text) {
	//FormTopDraft 내의 주소 및 보존 select box 값 가져오기
	if(field == "COM_ADDR_SEL" || field == "SAVE_TERM_SEL") {
		text = $(text).children('option:selected').text();
		field = field.replace("_SEL", "");
	}
	
    if (getInfo("Request.templatemode") == "Read") {
        HwpCtrl = document.getElementById("tbContentElement");
        if (text.replace(" ", "") != "") {
            HwpCtrl.PutFieldText(field, text);
        } else {
            HwpCtrl.PutFieldText(field, " ");
            if (field == "RecLineOPDEdit") {
                HwpCtrl.MoveToField(field);
                HwpCtrl.MovePos(104);
                HwpCtrl.PutFieldText(HwpCtrl.GetCurFieldName(1), " ");
            }
        }
    } else {
        HwpCtrl = document.getElementById("tbContentElement");
        HwpCtrl.PutFieldText(field, text);
    }
}

//연결문서 그리기
function G_displaySpnDocLinkInfoMulti_V1(idx) {
    var iCount = 1;
    var szdoclinksinfo = "";
    var szdoclinksinfoEditor = "";
    var szdoclinks = "";
    try { szdoclinks = document.getElementsByName("ST_DOCLINKS")[idx].value; } catch (e) { }
    szdoclinks = szdoclinks.replace("undefined^", "");
    szdoclinks = szdoclinks.replace("undefined", "");
    if (szdoclinks != "") {
        var adoclinks = szdoclinks.split("^^^");
        for (var i = 0; i < adoclinks.length; i++) {

            var adoc = adoclinks[i].split("@@@");
        	//var FormUrl = "http://" + document.location.host + "/WebSite/approval/forms/form.aspx"; (yongwug)
            var FormUrl = document.location.protocol + "//" + document.location.host + "/WebSite/approval/forms/form.aspx";
            var bEdit = false;
            if (getInfo("Request.templatemode") == "Read") {
                bEdit = false
            } else {
                bEdit = true;
            }
            var iWidth = 790;
            if (IsWideOpenFormCheck(adoc[1])) {
                iWidth = 1070;
            }
            var iHeight = window.screen.height - 82;
            if (bEdit) {
                szdoclinksinfo += "<div style='width:100%;'>";
                if (getInfo("Request.mode") == "DRAFT" || getInfo("Request.mode") == "TEMPSAVE" || (getInfo("Request.loct") == "APPROVAL" && getInfo("Request.mode") == "SUBAPPROVAL") || (getInfo("Request.loct") == "APPROVAL" && getInfo("Request.mode") == "PCONSULT") || (getInfo("Request.loct") == "APPROVAL" && getInfo("Request.mode") == "SUBREDRAFT") || g_szEditable == true) {
                    szdoclinksinfo += "<input type=checkbox id='chkDoc' name='_" + adoc[0] + "' value='" + adoc[0] + "'>";
                }
                szdoclinksinfo += "<span onmouseover='this.style.color=\"#2f71ba\";' onmouseout='this.style.color=\"#111111\";'  style='cursor:pointer;'  onclick=\"CFN_OpenWindow('";
                szdoclinksinfo += FormUrl + "?mode=COMPLETE&archived=true" + "&piid=" + adoc[0];
                szdoclinksinfo += "',''," + iWidth + ", " + iHeight + ", 'scroll') \"><span name='spCountD" + "'>" + iCount.toString() + ". </span>" + adoc[2] + "</span></div>";
                szdoclinksinfoEditor += ((szdoclinksinfoEditor == "") ? "" : "\r\n") + iCount.toString() + ". " + adoc[2];
            } else {
                if (bDisplayOnly) {
                    szdoclinksinfo += adoc[2];
                } else {
                    szdoclinksinfo += "<div style='width:100%;'><span onmouseover='this.style.color=\"#2f71ba\";' onmouseout='this.style.color=\"#111111\";'  style='cursor:pointer;'  onclick=\"CFN_OpenWindow('";
                    szdoclinksinfo += FormUrl + "?mode=COMPLETE&archived=true" + "&piid=" + adoc[0];
                    szdoclinksinfo += "',''," + iWidth + ", " + iHeight + ", 'scroll') \"><span name='spCountD" + "'>" + iCount.toString() + ". </span>" + adoc[2] + "</span></div>";
                    szdoclinksinfoEditor += ((szdoclinksinfoEditor == "") ? "" : "\r\n") + iCount.toString() + ". " + adoc[2];
                }
            }
            iCount++;
        }
        if (bEdit) {
            if (szdoclinksinfo != "" && (getInfo("Request.mode") == "DRAFT" || getInfo("Request.mode") == "TEMPSAVE" || (getInfo("Request.loct") == "APPROVAL" && getInfo("Request.mode") == "SUBAPPROVAL")) || (getInfo("Request.loct") == "APPROVAL" && getInfo("Request.mode") == "PCONSULT") || (getInfo("Request.loct") == "APPROVAL" && getInfo("Request.mode") == "SUBREDRAFT") || g_szEditable == true) {
                szdoclinksinfo += "<div class='xbtn'><a onclick='deletedocitemMulti(" + ");' ><em class='btn_iws_l'><span class='btn_iws_r'><strong class='txt_btn_ws'><img src='/Images/Images/Approval/ico_x.gif' alt='' class='ico_btn' />" + m_oFormMenu.gLabel_link_delete + "</strong></span></em></a></div>";
            }
        }
    }
    if (szdoclinksinfo != "") {
        if (getInfo("Request.templatemode") == "Read") {
            document.getElementById("trDocLinkInfo0").style.display = "";
            if (idx == 0) {
                document.getElementById("DocLinkInfo0").innerHTML = "(" + (idx + 1).toString() + "안)<BR />" + szdoclinksinfo;
            } else {
                document.getElementById("DocLinkInfo0").innerHTML += "(" + (idx + 1).toString() + "안)<BR />" + szdoclinksinfo;
            }
        } else {
            document.getElementById("trDocLinkInfo").style.display = "";
            document.getElementById("DocLinkInfo").innerHTML = szdoclinksinfo;
        }
    } else {
        document.getElementById("trDocLinkInfo").style.display = "none";
    }

    if (iCount <= 2) {
        if (document.getElementsByName("spCountD")[0] != null) {
            document.getElementsByName("spCountD")[0].style.display = "none";
            szdoclinksinfoEditor = szdoclinksinfoEditor.replace("1. ", "");
        }
    }
    //fnPutFieldText(idx, 'DocLinkInfo', szdoclinksinfoEditor);
}

//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가
var Attach_Html = "";
var szAttFileInfo_Add = "";

//첨부리스트 그리기
function G_displaySpnAttInfoMulti_V1(bRead, iItemNum) {
    var attFiles, fileLoc, szAttFileInfo;
    var displayFileName;
    var szAttFileInfoEditor = "";
    var re = /_N_/g;
    var bReadOnly = false;
    if (bRead != null) bReadOnly = bRead;
    // 편집 모드인지 확인
    var bEdit = false;
    var iCount = 1;
    var iCount2 = 1;		//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가

    szAttFileInfo = "";
    MultiDownLoadString = "";

    if (document.getElementsByName("ST_ATTACH_FILE_INFO")[iItemNum].value != "") {
        var r, res;
        var s = document.getElementsByName("ST_ATTACH_FILE_INFO")[iItemNum].value;
        res = /^^^/i;
        attFiles = s.replace(res, "");
        var fState;
        var m_oFileList;
        if (attFiles.indexOf("</fileinfos>") < 0) {
            m_oFileList = $.parseXML("<fileinfos>" + attFiles + "</fileinfos>");
        } else {
            m_oFileList = $.parseXML(attFiles);
        }
        var elmRoot, elmList, elm, elmTaskInfo;
        elmRoot = m_oFileList.documentElement;
        if (elmRoot != null) {
            szAttFileInfo = "";
            szAttFileInfoEditor = "";

        	//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가
            $(elmRoot).find("fileinfo > file").each(function (i, elm) {
            	iCount2++;
            });

            elmList = $(elmRoot).find("fileinfo > file");
            $(elmRoot).find("fileinfo > file").each(function (i, elm) {
                var filename = $(elm).attr("name");
                var filesize = $(elm).attr("size");
                var limitSize = (filesize == null) ? "0" : (parseInt(filesize) / 1024);
                displayFileName = $(elm).attr("name").substring(0, $(elm).attr("name").lastIndexOf("."));
                displayFileName = displayFileName.replace(re, "&");
                if ($(elm).attr("state") != null) {
                    fState = $(elm).attr("state");
                }
                else {
                    fState = "";
                }

                if (bEdit) {
                    szAttFileInfo += '<input type=\"checkbox\" id=\"chkFile\" name=\"chkFile\" value=\"' + $(elm).attr("name") + '\" style=\"vertical-align:middle;\">';
                } else {//편집모드가 아닐때만 다중다운로드 문자열 생성
                    MultiDownLoadString += $(elm).attr("location").replace(new RegExp("\\+", "g"), "%2B")
                                            + "|" + $(elm).attr("name").replace(new RegExp("\\+", "g"), "%2B")
                                            + "|" + filesize
                                            + "`";
                }
                if ($(elm).attr("location").indexOf(".") > -1) {
                    if (bReadOnly) {
                        if (bReadOnly == "display") {
                            if (limitSize >= 1024) {
                                limitSize = limitSize / 1024;
                                limitSize = parseFloat(limitSize).toFixed(3);
                                szAttFileInfo += getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length))
                                szAttFileInfo += "<b>" + $(elm).attr("name") + "</b>&nbsp;";
                            }
                            else {
                                szAttFileInfo += getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length))
                                szAttFileInfo += "<b>" + $(elm).attr("name") + "</b>&nbsp;";
                            }
                        }
                        else {
                            szAttFileInfo += displayFileName;
                        }
                    }
                    else {
                        if (fState == "" || fState == "OLD") {
                            if (limitSize >= 1024) {
                                limitSize = limitSize / 1024;
                                limitSize = parseFloat(limitSize).toFixed(3);
                                szAttFileInfo   += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length))
                                                + "<a href=\"javascript:PopListSingle_Approval('" + $(elm).attr("name") + "', '" + $(elm).attr("location") + "');\"  >" + $(elm).attr("name")
                                                + "</a></p>";
                                szAttFileInfoEditor += ((szAttFileInfoEditor == "") ? "" : "\r\n") + iCount.toString() + ". " + displayFileName;
                            }
                            else {
                                szAttFileInfo   += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length))
                                                + "<a href=\"javascript:PopListSingle_Approval('" + $(elm).attr("name") + "', '" + $(elm).attr("location") + "');\"  >" + $(elm).attr("name");
                                szAttFileInfo += "</a></p>"
                                szAttFileInfoEditor += ((szAttFileInfoEditor == "") ? "" : "\r\n") + iCount.toString() + ". " + displayFileName;
                            }

                        	//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가
                            if (iCount2 == 2) {		//첨부파일 1개일 경우
                            	//szAttFileInfo_Add += "<span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + displayFileName + ".　끝.";
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>붙   임 : " + iCount.toString() + ". </span>" + displayFileName + ".　끝.</p>";
                            }
                            else if (iCount == (iCount2 - 1)) {		//마지막 첨부파일 경우
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>　　　　" + iCount.toString() + ". </span>" + displayFileName + ".　끝.</p>";
                            }
                            else if (iCount == 1) {		//첨부파일 다수, 첫번째 첨부파일일 경우
                            	//szAttFileInfo_Add += "<span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + displayFileName + ".";
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>붙   임 : " + iCount.toString() + ". </span>" + displayFileName + ".</p>";
                            }
                            else {
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>　　　　" + iCount.toString() + ". </span>" + displayFileName + ".</p>";
                            }

                            gFileArray[i] = $(elm).attr("location").replace(new RegExp("\\+", "g"), "%2B"); // +":" + filesize;
                            gFileNameArray[i] = $(elm).attr("name");
                            iCount++;
                        }
                        else if (fState == "NEW") {
                            if (limitSize >= 1024) {
                                limitSize = limitSize / 1024;
                                limitSize = parseFloat(limitSize).toFixed(3);
                                szAttFileInfo += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length))
                                + "<a href=\"javascript:PopListSingle_Approval('" + $(elm).attr("name") + "', '" + $(elm).attr("location") + "');\"  >" + $(elm).attr("name")
                                + "</a></p>";
                                szAttFileInfoEditor += ((szAttFileInfoEditor == "") ? "" : "\r\n") + iCount.toString() + ". " + displayFileName;
                            }
                            else {
                                szAttFileInfo += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length))
                                + "<a href=\"javascript:PopListSingle_Approval('" + $(elm).attr("name") + "', '" + $(elm).attr("location") + "');\"  >" + $(elm).attr("name")
                                + "</a></p>";
                                szAttFileInfoEditor += ((szAttFileInfoEditor == "") ? "" : "\r\n") + iCount.toString() + ". " + displayFileName;
                            }

                        	//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가
                            if (iCount2 == 2) {		//첨부파일 1개일 경우
                            	//szAttFileInfo_Add += "<span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + displayFileName + ".　끝.";
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>붙   임 : " + iCount.toString() + ". </span>" + displayFileName + ".　끝.</p>";
                            }
                            else if (iCount == (iCount2 - 1)) {		//마지막 첨부파일 경우
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>　　　　" + iCount.toString() + ". </span>" + displayFileName + ".　끝.</p>";
                            }
                            else if (iCount == 1) {		//첨부파일 다수, 첫번째 첨부파일일 경우
                            	//szAttFileInfo_Add += "<span name='spCountA" + iItemNum.toString() + "'>" + iCount.toString() + ". </span>" + displayFileName + ".";
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>붙   임 : " + iCount.toString() + ". </span>" + displayFileName + ".</p>";
                            }
                            else {
                            	szAttFileInfo_Add += "<p style='padding-bottom:2pt;'><span name='spCountA" + iItemNum.toString() + "'>　　　　" + iCount.toString() + ". </span>" + displayFileName + ".</p>";
                            }

                            gFileArray[i] = $(elm).attr("location").replace(new RegExp("\\+", "g"), "%2B"); // +":" + filesize;
                            gFileNameArray[i] = $(elm).attr("name");
                            iCount++;
                        }
                        else {//삭제일경우
                            szAttFileInfo += "";
                            gFileArray[i] = "";
                            gFileNameArray[i] = "";
                        }
                    }
                }
                else {
                    if (limitSize >= 1024) {
                        limitSize = limitSize / 1024;
                        limitSize = parseFloat(limitSize).toFixed(3);
                        szAttFileInfo += "<span style='cursor:pointer;' onclick='PopListSingle_Approval(\"" + $(elm).attr("name") + "\", \"" + $(elm).attr("location") + "\");'>" + getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length)) + "&nbsp;<b>" + $(elm).attr("name") + "</b></span><br/>"; //TARGET=\"_blank\"
                    }
                    else {
                        limitSize = parseFloat(limitSize).toFixed(2); // 2012.11.16 myungwan.jin@samyang.com KB 표시할 때, 소수점 둘째자리까지 표기하도록 추가
                        szAttFileInfo += "<span style='cursor:pointer;' onclick='PopListSingle_Approval(\"" + $(elm).attr("name") + "\", \"" + $(elm).attr("location") + "\");'>" + getAttachImage(filename.substring(filename.lastIndexOf('.') + 1, filename.length)) + "&nbsp;<b>" + $(elm).attr("name") + "</b></span><br/>"; //TARGET=\"_blank\"
                    }
                }
            }
            );
        }
        if (szAttFileInfo != "") {
            if (getInfo("Request.templatemode") == "Read") {
                document.getElementById("trAttFileInfo").style.display = "";
                if (iItemNum == 0) {
                    document.getElementById("AttFileInfo").innerHTML = "(" + (iItemNum + 1).toString() + "안)<BR />" + szAttFileInfo;
                } else {
                    document.getElementById("AttFileInfo").innerHTML += "(" + (iItemNum + 1).toString() + "안)<BR />" + szAttFileInfo;
                }
            } else {
                document.getElementById("trAttFileInfo" + iItemNum.toString()).style.display = "";
                document.getElementById("AttFileInfo" + iItemNum.toString()).innerHTML = szAttFileInfo;
            }
            createTableArea(iItemNum, "FILE");
            if (iCount <= 2) {
                if (document.getElementsByName("spCountA" + iItemNum.toString())[0] != null) {
                    document.getElementsByName("spCountA" + iItemNum.toString())[0].style.display = "none";
                    szAttFileInfoEditor = szAttFileInfoEditor.replace("1. ", "");
                    szAttFileInfo_Add = szAttFileInfo_Add.replace("1. ", "");			//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가
                }
            }
            fnPutFieldText(iItemNum, 'AttFileInfo', szAttFileInfoEditor);
        } else {
            document.getElementById("trAttFileInfo" + iItemNum.toString()).style.display = "none";
            deleteTableArea(iItemNum, "FILE");
        }
    } else {
        document.getElementById("trAttFileInfo" + iItemNum.toString()).style.display = "none";
        deleteTableArea(iItemNum, "FILE");
        document.getElementById("AttFileInfo" + iItemNum.toString()).innerHTML = szAttFileInfo;
    }
	//[2017-11-06] Covision - HJH : 대외문서(전자) 시행문 본문 첨부파일 '붙임' 추가
	//Attach_Html = szAttFileInfoEditor.replace(/\r\n/gi, "");
	//Attach_Html = szAttFileInfo;
    Attach_Html = szAttFileInfo_Add;

}

function createTableArea(idx, type) {
    if (getInfo("Request.templatemode") == "Read") {
        HwpCtrl = document.getElementById("tbContentElement");
        if (type == "FILE") {
            if (!HwpCtrl.MoveToField("ATT_FILE_INFO")) {
                HwpCtrl.PutFieldText("ATTFILEAREA", " ");
                HwpCtrl.MoveToField("ATTFILEAREA", true, true, true);
                HwpCtrl.SetTextFile(document.getElementById("fileAttArea").value, "HWP", "insertfile");
            }
        } else if (type == "REC") {
            if (!HwpCtrl.MoveToField("RECLINE_INFO")) {
                HwpCtrl.PutFieldText("RECLINEAREA", " ");
                HwpCtrl.MoveToField("RECLINEAREA", true, true, true);
                HwpCtrl.SetTextFile(document.getElementById("RecLineArea").value, "HWP", "insertfile");
                HwpCtrl.PutFieldText("RecLineOPDEdit", " ");
            }
        } else {
            if (!HwpCtrl.MoveToField("SIGN_MASTER")) {
                HwpCtrl.MoveToField("MASTERSIGNAREA", true, true, true);
                HwpCtrl.SetTextFile(document.getElementById("masterSignArea").value, "HWP", "insertfile");
            }
        }
    } else {
        HwpCtrl = document.getElementById("tbContentElement");
        SetHwpEditMode(HwpCtrl, "edit");
        if (type == "FILE") {
            if (!HwpCtrl.MoveToField("ATT_FILE_INFO")) {
                HwpCtrl.PutFieldText("ATTFILEAREA", " ");
                HwpCtrl.MoveToField("ATTFILEAREA", true, true, true);
                HwpCtrl.SetTextFile(document.getElementById("fileAttArea").value, "HWP", "insertfile");
            }
        } else if (type == "REC"){
            if (!HwpCtrl.MoveToField("RECLINE_INFO")) {
                HwpCtrl.PutFieldText("RECLINEAREA", " ");
                HwpCtrl.MoveToField("RECLINEAREA", true, true, true);
                HwpCtrl.SetTextFile(document.getElementById("RecLineArea").value, "HWP", "insertfile");
                HwpCtrl.PutFieldText("RecLineOPDEdit", " ");
            }
        } else {
            if (!HwpCtrl.MoveToField("SIGN_MASTER")) {
                HwpCtrl.MoveToField("MASTERSIGNAREA", true, true, true);
                HwpCtrl.SetTextFile(document.getElementById("masterSignArea").value, "HWP", "insertfile");
            }
        }
        HwpCtrl.MovePos(2);
        SetHwpEditMode(HwpCtrl, "form");
    }
}

function deleteTableArea(idx, type) {
    HwpCtrl = document.getElementById("tbContentElement");
    SetHwpEditMode(HwpCtrl, "edit");
    if (type == "FILE") {
        if (HwpCtrl.MoveToField("ATT_FILE_INFO")) {
            HwpCtrl.PutFieldText("ATTFILEAREA", " ");
            HwpCtrl.MoveToField("ATTFILEAREA", true, true, true);
            HwpCtrl.SetTextFile(document.getElementById("fileAttAreaBlank").value, "HWP", "insertfile");
        }
    } else if (type == "REC") {
        if (HwpCtrl.MoveToField("RECLINE_INFO")) {
            HwpCtrl.PutFieldText("RECLINEAREA", " ");
            HwpCtrl.MoveToField("RECLINEAREA", true, true, true);
            HwpCtrl.SetTextFile(document.getElementById("RecLineAreaBlank").value, "HWP", "insertfile");
        }
    } else {
        if (HwpCtrl.MoveToField("SIGN_MASTER")) {
            HwpCtrl.PutFieldText("MASTERSIGNAREA", " ");
        }
    }
    HwpCtrl.MovePos(2);
    if (getInfo("Request.templatemode") == "Read") {
        SetHwpEditMode(HwpCtrl, "read");
    } else {
        SetHwpEditMode(HwpCtrl, "form");
    }
}

//수신처 표시
function initRecListMulti_V1(idx) {
    var szReturn = '';
    var aRec = document.getElementById("ST_RECEIVE_NAMES").value.split(";");
    var Include = "";
    var setCnt = 0;
    for (var i = 0; i < aRec.length; i++) {
        if (aRec[i] != "") {
            if (aRec[i].split(":")[3] == "Y") {
                Include = "(" + gLabel__recinfo_td2 + ")";
            }
            if (aRec[i].split(":")[1] == "100990000") {
                szReturn += (szReturn != '' ? ", " : "") + "노측대표";
            } else {
                szReturn += (szReturn != '' ? ", " : "") + (getLngLabel(aRec[i].split(":")[2], false, "^") + "장");
            }
            Include = "";
            setCnt++;
        }
    }
    if (setCnt <= 1) {
        document.getElementById("RecLine").innerHTML = "";
        fnPutFieldText(idx, 'RecLine', " ");
        deleteTableArea(idx, "REC");
    } else {
        createTableArea(idx, "REC");
        document.getElementById("RecLine").innerHTML = szReturn;
        fnPutFieldText(idx, 'RecLine', szReturn);
    }
    try {
        if (setCnt == 0) {
            document.getElementById("ST_DESTINATION_DP").value = "";
            fnPutFieldText(idx, 'ST_DESTINATION_DP', " ");
        } else if (setCnt == 1) {
            document.getElementById("ST_DESTINATION_DP").value = szReturn;
            fnPutFieldText(idx, 'ST_DESTINATION_DP', szReturn);
        } else {
            document.getElementById("ST_DESTINATION_DP").value = "수신처 참조";
            fnPutFieldText(idx, 'ST_DESTINATION_DP', "수신처 참조");
        }
    } catch (e) {
    }
}

//수신처 표시
function initGovListMulti_V1(idx) {
    var szReturn = '';
    var aRec = document.getElementsByName("ST_RECLINEOPD")[idx].value.split(";");
    var setCnt = 0;
    for (var i = 0; i < aRec.length; i++) {
        if (aRec[i] != "") {
            szReturn += (szReturn != '' ? ", " : "") + (getLngLabel(aRec[i].split(":")[13], false, "^"));
            setCnt++;
        }
    }
    if (setCnt <= 1) {
        //document.getElementById("RecLineOPD").innerHTML = "";
        fnPutFieldText(idx, 'RecLineOPD', " ");
        deleteTableArea(idx, "REC");
    } else {
        createTableArea(idx, "REC");
        //document.getElementById("RecLineOPD").innerHTML = szReturn;
        fnPutFieldText(idx, 'RecLineOPD', szReturn);
    }
    try {
        if (setCnt == 0) {
            document.getElementsByName("ST_DESTINATION_DP")[idx].value = "";
            fnPutFieldText(idx, 'ST_DESTINATION_DP', " ");
        } else if (setCnt == 1) {
            document.getElementsByName("ST_DESTINATION_DP")[idx].value = szReturn;
            fnPutFieldText(idx, 'ST_DESTINATION_DP', szReturn);
        } else {
            document.getElementsByName("ST_DESTINATION_DP")[idx].value = "수신처 참조";
            fnPutFieldText(idx, 'ST_DESTINATION_DP', "수신처 참조");
        }
    } catch (e) {
    }
}

//직인 표시
function PutSignMaster(idx, sign) {
    var g_BaseImgURL = Common.getBaseConfig("BackStorage") + "e-sign/ApprovalForm/HWP/";
    
    if(sign == undefined) {
        sign = "stamp.jpg";
    }
    
    HwpCtrl = document.getElementById(g_id);
    if(HwpCtrl == null && webHwpYN == "Y") {
    	HwpCtrl = document.getElementById(g_id + 'Frame').contentWindow.HwpCtrl;
    }

    HwpCtrl.MoveToField("SIGN_MASTER", true, false, false);
    if (!HwpCtrl.MoveToField("SIGN_MASTER")) {
        HwpCtrl.MoveToField("MASTERSIGNAREA", true, true, true);
    }
    var path = _HostFullName + g_BaseImgURL + sign;
    var pCtrl = HwpCtrl.InsertPicture(path, true, 3, 0, 0, 0);
    HwpCtrl.MovePos(2);
}

// 요약전 시작
function LoadSummaryEditorHWP(elm, type) {
	if(opener.getInfo("ExtInfo.UseWebHWPEditYN") == "Y") { //웹한글기안기
    	coviEditor.loadEditor(
    			elm,
    			{
    				editorType : "webhwpctrl",
    				containerID : "summaryContent",
    				frameHeight : '450',
    				focusObjID : '',
    				doctype : '',
    				onLoad: function(){
    					setSummaryEditorHWP(type);
    				}
    			}
    	);
    } else { //한글 Active X
	    if (_ie) {
	        $('#' + elm).html(LoadtweditorForSummary())
	        	.append('<script language="javascript" for="SummaryEditorArea" event="OnControlInit">setTimeout("setSummaryEditorHWP(\''+type+'\')", 10);</script>');
	    }
	    else {
	        Common.Warning("아래한글 에디터는 ie에서만 지원합니다. ie로 접속해주세요.");
	    }
    }
}

function setSummaryEditorHWP(type) {
    HwpCtrl = document.getElementById("SummaryEditorArea");
    if(HwpCtrl == null) {
    	HwpCtrl = document.getElementById("summaryContentFrame").contentWindow.HwpCtrl;
    }
    
    HwpCtrl.SetClientName("DEBUG");
    
    //쓰기 모드인 경우
    if (type == "Write") {
    	
    	if(opener.getInfo("ExtInfo.UseWebHWPEditYN") != "Y") {
    		InitToolBarJS();
    	}
        
        var strHWP = opener.document.getElementById("SummaryContent").value;
        
        if(strHWP != undefined && strHWP != "") {
        	if(opener.getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
        		HwpCtrl.SetTextFile(strHWP, "HTML", "");
        	} else {
        		HwpCtrl.SetTextFile(strHWP, "HWP", "");
        	}
        }
            
        SetHwpEditMode(HwpCtrl, "edit");
            
    } else {// 읽기 모드인 경우

    	if(opener.getInfo("ExtInfo.UseWebHWPEditYN") != "Y") {
    		InitToolBarJS_Read();
    	} else {
    		HwpCtrl.ShowRibbon(false);
    	}
        
        var strHWP = opener.document.getElementById("SummaryContent").value;
        
        if(strHWP != undefined && strHWP != "") {
        	if(opener.getInfo("ExtInfo.UseWebHWPEditYN") == "Y") {
        		HwpCtrl.SetTextFile(strHWP, "HTML", "");
        	} else {
        		HwpCtrl.SetTextFile(strHWP, "HWP", "");
        	}
        }
        
        SetHwpEditMode(HwpCtrl, "read");
        
        
        $("#summaryBtnSave").hide();
        $("#summaryBtnDelete").hide();
    }
    
    var vp = HwpCtrl.CreateSet("ViewProperties");
	vp.SetItem("ZoomType", 2);
	HwpCtrl.ViewProperties = vp;
	
	//폰트 및 사이즈 변경
	var  act = HwpCtrl.CreateAction("CharShape");
	var set = act.CreateSet();
	    act.GetDefault(set);

    set.SetItem("Height", 1600);
    set.SetItem("FaceNameHangul", "굴림");
    set.SetItem("FaceNameLatin", "굴림");
    set.SetItem("FaceNameHanja", "굴림");
    set.SetItem("FaceNameJapanese", "굴림");
    set.SetItem("FaceNameOther", "굴림");
    set.SetItem("FaceNameSymbol", "굴림");
    set.SetItem("FaceNameUser", "굴림");

    act.Execute(set); // 액션 실행

}

function LoadtweditorForSummary() {
    var oHtml = '';
    oHtml += '<object ID="SummaryEditorArea" width="100%" height="300px" CLASSID="CLSID:' + l_Clsid + '" >';
    oHtml += '<PARAM name="_Version" value="65537"/>';
    oHtml += '<PARAM name="_ExtentX" value="24950"/>';
    oHtml += '<PARAM name="_ExtentY" value="9234"/>';
    oHtml += '<PARAM name="_StockProps" value="0"/>';
    oHtml += '<PARAM name="FILENAME" value=""/>';
    oHtml += '<PARAM name="TOOLBAR_MENU" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_STANDARD" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_FORMAT" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_DRAW" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_TABLE" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_IMAGE" value="0"/>';
    oHtml += '<PARAM name="TOOLBAR_HEADER_FOOTER" value="0"/>';
    oHtml += '<PARAM name="SHOW_TOOLBAR" value="0"/>';
    oHtml += '<PARAM name="SHOW_STATUSBAR" value="0"/>';
    oHtml += '</object>';
    return oHtml;
}

//Test
function setHwpContent(args, callback){	
	if(window.byHWPCallBack == undefined) window.byHWPCallBack = false;
		
	if(window.byHWPCallBack == false){
		var _scope = this;
		var isHWPBODY_SET_ARR = [];
		for(var i = 0; i < $("[id^=tbContentElement][id$=Frame]").length; i++) {
			isHWPBODY_SET_ARR.push(false);
		}
		
		$("[id^=tbContentElement][id$=Frame]").each(function(idx, item){
        	var objEditor = $(item)[0].contentWindow.HwpCtrl;
        	var strHTML = "";
        	var strHWP = "";
        	
    		if(formJson.Request.mode == "DRAFT") {
    			strHTML = Base64.utf8_to_b64($(objEditor)[0].GetTextFile("HTML", ""));
    			if (strHTML != "") {
                	document.getElementById("dhtml_body").value = strHTML;
    				$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HTML]')[idx].value = strHTML;
    			}
    			
    			$(objEditor)[0].GetTextFile("HWP", "", function(data){ 
    				strHWP = data;                    			
        			if (strHWP != "") {
    					$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HWP]')[idx].value = strHWP;
        			}
        			
        			isHWPBODY_SET_ARR[idx] = true;
        			var allCheck = true;
        			for(var i = 0; i < isHWPBODY_SET_ARR.length; i++){
        				if(isHWPBODY_SET_ARR[i] == false){
        					allCheck = false;
        					break;
        				}
        			}
        			if(allCheck){
        				setTimeout(function(){
        					window.byHWPCallBack = true;
        					try {
        						callback.apply(_scope, args);
        					}finally{
        						window.byHWPCallBack = false;
        					}
        				});
        			}
    			});
            } else {
            	$(objEditor)[0].MoveToField("BODY", true, true, true);
            	
            	strHTML = Base64.utf8_to_b64($(objEditor)[0].GetTextFile("HTML", ""));       
    			$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HTML]')[idx].value = strHTML;
    			
            	$(objEditor)[0].GetTextFile("HWP", "", function(data){ 
            		strHWP = data; 
    				$('#SubTable1 .multi-row').find('[name=MULTI_BODY_CONTEXT_HWP]')[idx].value = strHWP;
    				
    				isHWPBODY_SET_ARR[idx] = true;
        			var allCheck = true;
        			for(var i = 0; i < isHWPBODY_SET_ARR.length; i++){
        				if(isHWPBODY_SET_ARR[i] == false){
        					allCheck = false;
        					break;
        				}
        			}
        			if(allCheck){
        				setTimeout(function(){
	        				window.byHWPCallBack = true;
	        				try {
	        					callback.apply(_scope, args);
	        				}finally{
	        					window.byHWPCallBack = false;
	        				}
        				});
        			}
            	});       	
            }
    		
    		var subject = $(objEditor)[0].GetFieldText("SUBJECT");
    		$('#SubTable1 .multi-row').find('[name=MULTI_TITLE]')[idx].value = subject;
    	});
		
		return true;
	}else{
		// 셋팅되었으므로 그냥 수행 (by callback)
		return false;
	}
}

// 