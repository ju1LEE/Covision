/*
 * covision util
 * 
 * use : covisionUtil.test();
 * */

var coviUtil = function(){
	if (!(this instanceof arguments.callee )) return new arguments.callee();
	
	/* this.checkValidation: 페이지 이동
	 * @method pageMove
	 * @param pBoxID 박스 ID
	 * @param pFolderID 폴더 ID
	 * @param pFolderType 폴더 타입 (ex. Normal, Published, Trashbin, ...)
	 * */
	this.showErrorMsg = function(sMsgId, oTarget, bShowMsg, bFocus, sAddMsg){
		let sMsg = Common.getDic(sMsgId).replace("{0}", oTarget.attr("title"));
		if (sAddMsg != undefined) sMsg+= sAddMsg;
		
		if (bShowMsg){
			Common.Warning(sMsg, "Warning", function(){ 
				if (bFocus) oTarget.focus();
			});
			
		}
	}
	
	this.checkValidation = function(obj, bShowMsg, bFocus){
		let bValidation = true;
		if (obj == undefined) {
			obj="";
		}
		//필수값 체크
		$(obj+" .Required").each(function () {
			if ($(this).attr("type") != "radio" && $(this).attr("type") != "checkbox" ){
				if ($.trim($(this).val()) == ""){
					coviUtil.showErrorMsg("msg_EnterTheRequiredValue", $(this), bShowMsg, bFocus );
					bValidation = false;
					return false;
				}
			}
			
		});
		if (bValidation == false) return bValidation;

		// Html 태그 입력 불가
		$(obj+" .HtmlCheckXSS").each(function () {
			if (XFN_CheckHTMLinText($(this).val())) {
				coviUtil.showErrorMsg("msg_ThisPageLimitedHTMLTag", $(this), bShowMsg, bFocus );
				bValidation = false;
				return false;
			}
		});
		if (bValidation == false) return bValidation;
		
		// <script>, <style> 입력 불가
		$(obj+" .ScriptCheckXSS").each(function () {
			if (XFN_CheckInputLimit($(this).val())) {
				coviUtil.showErrorMsg("msg_ThisInputLimitedScript", $(this), bShowMsg, bFocus );
				bValidation = false;
				return false;
			}
		});
		if (bValidation == false) return bValidation;

		// <script> 입력 불가
		$(obj+" .ScriptCheckXSSOnlyScript").each(function () {
			if (XFN_CheckInputLimitOnlyScript($(this).val())) {
				coviUtil.showErrorMsg("msg_ThisInputLimitedScript", $(this), bShowMsg, bFocus );
				bValidation = false;
				return false;
			}
		});
		if (bValidation == false) return bValidation;
		
		// 특수 문자 입력 불가 
		var spePatt = /[`~!@#$%^&*|\\\'\";:\/?(){}\[\]¢™$®]/gi;
		$(obj+" .SpecialCheck").each(function () {
			if (spePatt.test($(this).val())) {
				coviUtil.showErrorMsg("msg_specialNotAllowed", $(this), bShowMsg, bFocus );
				bValidation = false;
				return false;
			}
		});
		if (bValidation == false) return bValidation;
		
		//최대 사이즈
		$(obj+" .MaxSizeCheck").each(function () {
			if (CFN_CalByte($(this).val()) > $(this).attr("max")){
				coviUtil.showErrorMsg("msg_RxceedNumberOfEnter", $(this), bShowMsg, bFocus,  "["+CFN_CalByte($(this).val())+" > "+$(this).attr("max")+"]" );
				bValidation = false;
				return false;
			}
		});

		if (bValidation == false) return bValidation;
		
		//숫자만
		var numPatt 	= /^[+\-\.\d]*$/;
		$(obj+" .Number").each(function () {
			if (!(numPatt.test($(this).val()))){
				coviUtil.showErrorMsg("msg_apv_249", $(this), bShowMsg, bFocus);
				bValidation = false;
				return false;
			};

			if ($(this).attr("minnum") != "" && $(this).attr("maxnum") != ""){
				if ((parseInt($(this).val(),10) < $(this).attr("minnum")) || (parseInt($(this).val(),10) > $(this).attr("maxnum") ) ){
					coviUtil.showErrorMsg("msg_RxceedNumberOfEnter", $(this), bShowMsg, bFocus,  "["+$(this).attr("minnum")+" ~ "+$(this).attr("maxnum")+"]" );
					bValidation = false;
					return false;
				}
			}	
			
		});
		return bValidation;

	}

}();
