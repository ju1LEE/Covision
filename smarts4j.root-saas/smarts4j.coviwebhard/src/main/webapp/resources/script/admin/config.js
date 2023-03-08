/*
 * config.js
 * use: webhardConfig.jsp
 */

var pageInit = function(){
	
	var setConfig = function(){
		$.ajax({
			url: "/webhard/admin/config/getConfig.do",
			type: "POST",
			data: {
				"domainID": $("#selectDomain").val()
			},
			success: function(data){
				if(data.status === "SUCCESS"){
					var config = data.config;
					
					if(config){
						$("#inputBoxVolume").val(config.BASIC_BOX_VOLUME);
						$("#inputMaxUploadSize").val(config.MAX_UPLOAD_SIZE);
						$("#inputExtensions").val(config.RESTRICTED_EXTENSIONS);
						$("#switchSharing").setValueInput(config.IS_SHARING);
					}else{
						$("#inputBoxVolume").val("");
						$("#inputMaxUploadSize").val("");
						$("#inputExtensions").val("");
						$("#switchSharing").setValueInput("N");
					}
					
					//coviInput.setSwitch();
				}
			},
			error: function(response, status, error){
				CFN_ErrorAjax("/webhard/admin/config/search.do", response, status, error);
			}
		});
	};
	
	var validation = function(){
		var boxVolume = $("#inputBoxVolume").val();
		var maxUploadSize = $("#inputMaxUploadSize").val();
		
		if (!boxVolume || !maxUploadSize) {
			Common.Inform(Common.getDic("ACC_msg_noReqData")); // 입력되지 않은 필수값이 있습니다.
			return false;
		} else {
			if (Number(boxVolume) < Number(maxUploadSize)) {
				Common.Inform(Common.getDic("msg_noMaxUploadSize")); // 최대 업로드 크기가 유효하지 않습니다.
				return false;
			}
		}
		
		return true;
	};
	
	// 입력 값 체크
	var checkFunc = {
		checkNumKeydown: function(event){
			event = event || window.event;
			var keyCode = event.which ? event.which : window.event.keyCode;
			
			// 8(Backspace), 37(왼쪽 방향키), 39(오른쪽 방향키), 45(Insert), 46(Delete), 
			// 48~57(숫자키), 96~105(키패드 숫자키)
			if (keyCode === 8 || keyCode === 37 || keyCode === 39 || keyCode === 45 || keyCode === 46 
					|| (keyCode > 47 && keyCode < 58) || (keyCode > 95 && keyCode < 106)) {
				return true;
			} else {
				return false;
			}
		},
		
		checkNumKeyup: function(event){
			var inputVal = $(this).val();
			$(this).val(inputVal.replace(/[^a-z0-9]/gi, ""));
		},
		
		checkExtKeydown: function(event){
			event = event || widow.event;
			var keyCode = event.which ? event.which : window.event.keyCode;
			
			// 8(Backspace), 32(Space), 37(왼쪽 방향키), 39(오른쪽 방향키), 45(Insert), 46(Delete), 
			// 65~90(영문), 48~57(숫자키), 96~105(키패드 숫자키), 188(,)
			if (keyCode === 8 || keyCode === 32 || keyCode === 37 || keyCode === 39 || keyCode === 45 || keyCode === 46 
					|| (keyCode > 64 && keyCode < 91) || (keyCode > 47 && keyCode < 58)|| (keyCode > 95 && keyCode < 106) || keyCode === 188) {
				return true;
			} else {
				return false;
			}
		},
		
		checkExtKeyup: function(event){
			var inputVal = $(this).val();
			$(this).val(inputVal.replace(/[^a-z0-9," "]/gi, ""));
		}
	};
	
	$("#inputBoxVolume, #inputMaxUploadSize").on("keydown", checkFunc.checkNumKeydown).on("keyup", checkFunc.checkNumKeyup);
	$("#inputExtensions").on("keydown", checkFunc.checkExtKeydown).on("keyup", checkFunc.checkExtKeyup);
	
	// 검색 - 도메인 변경
	$("#selectDomain").off("change").on("change", setConfig).trigger("change");
	
	// 저장
	$("#btnSave").off("click").on("click", function(){
		if(!validation()) return false;
		
		Common.Confirm(Common.getDic("ACC_isSaveCk"), "Confirmation Dialog", function(confirmResult){ // 저장하시겠습니까?
			if(confirmResult){
				$.ajax({
					url: "/webhard/admin/config/save.do",
					type: "POST",
					data: {
						  "domainID": $("#selectDomain").val()
						, "boxVolume": $("#inputBoxVolume").val()
						, "maxUploadSize": $("#inputMaxUploadSize").val()
						, "extensions": $("#inputExtensions").val()
						, "isSharing": $("#switchSharing").val()
					},
					success: function(data){
						if(data.status === "SUCCESS"){
							Common.Inform(Common.getDic("msg_apv_331")); // 저장되었습니다.
						}
					},
					error: function(response, status, error){
						CFN_ErrorAjax("/webhard/admin/config/save.do", response, status, error);
					}
				});					
			}
		});
	});
}

pageInit();