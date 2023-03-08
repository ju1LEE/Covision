$.fn.serializeObject = function() {
    var obj = null;
    try {
        if ( this[0].tagName && this[0].tagName.toUpperCase() == "FORM" ) {
            var arr = this.serializeArray();
            if ( arr ) {
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });             
            }//if ( arr ) {
        }
    }
    catch(e) {alert(e.message);}
    finally  {}
     
    return obj;
};

var boardAdmin = {
	alert: function(){
		alert("board admin!!!");
	},
	//padding zero 월,일,시,분,초를 두자리수로 보여줌
	pad : function(n){
		return n<10 ? '0'+n : n
	},
	
	//date format처리 YYYY-MM-DD hh:mm:ss
	dateString : function( pDate ){
		var year = pDate.getFullYear();
		if (year < 1000){
		     year+=1900;
		}
		var day = pDate.getDate();
		var month=(pDate.getMonth()+1);
		var hour = pDate.getHours();
		var min = pDate.getMinutes();
		var sec = pDate.getSeconds();
		var currentTime = year + "-" + boardAdmin.pad(month) + "-" + boardAdmin.pad(day) + " " + boardAdmin.pad(hour) + ":" + boardAdmin.pad(min) + ":" + boardAdmin.pad(sec);
		return currentTime;
	},
	
	//FolderGrid 폴더이름 표시 포멧
	formatFolderName: function( pObj ){
		var returnStr;
		if(pObj.item.FolderType != 'Root' && pObj.item.FolderType != 'DocRoot' && pObj.item.FolderType !='Folder'){
			returnStr = String.format("<a href='#' onclick='selectMessageGridList({0});selectFolderPath(\"{1}\") '>{2}</a>", pObj.item.FolderID, pObj.item.FolderPath, pObj.item.DisplayName);
		} else {
			returnStr = String.format("<a href='#' onclick='editBoardPopup(\"{0}\", \"{1}\")'>{2}</a>", pObj.item.FolderID, pObj.item.MenuID, pObj.item.DisplayName);
		}
		
		//삭제된 폴더의 경우 취소선 표시
		if(pObj.item.DeleteDate!="" && pObj.item.DeleteDate!=undefined){
			returnStr = String.format("<strike>{0}</strike>",returnStr);
		}
			
		return returnStr;
	},
	
	//FolderGrid 폴더타입 표시 포멧
	formatFolderType: function( pObj ){
		var folderType = "FolderType_" + pObj.item.FolderType;
		return pObj.item.FolderType != "" ? Common.getDic(folderType) : "";
	},
	
	//FolderGrid 사용여부, 표시여부 포멧
	formatUpdateFlag: function( pObj ){
		var returnStr = "<input type='text' kind='switch' on_value='Y' off_value='N' style='width:50px;height:21px;border:0px none;' ";
		if(pObj.key == "IsUse"){
			returnStr += String.format("value='{1}' onchange='updateFlag({0},\"{1}\",\"{2}\");' />", pObj.item.FolderID, pObj.item.IsUse, pObj.key);
		} else {
			returnStr += String.format("value='{1}' onchange='updateFlag({0},\"{1}\",\"{2}\");' />", pObj.item.FolderID, pObj.item.IsDisplay, pObj.key);
		}
		return returnStr;
	},
	
	//FolderGrid 사용여부, 표시여부 포멧
	formatDocNumberUpdateFlag: function( pObj ){
		var returnStr = "<input type='text' kind='switch' on_value='Y' off_value='N' style='width:50px;height:21px;border:0px none;' ";
		returnStr += String.format("value='{1}' onchange='updateDocNumberFlag({0},\"{1}\");' />", pObj.item.ManagerID, pObj.item.IsUse);
		return returnStr;
	},
	
	formatFieldtype: function( pObj ){
		var returnStr = String.format("<a href='#' onclick='editDocNumberPopup({0}, {1})'>{2}</a>", pObj.item.DomainID, pObj.item.ManagerID, pObj.item.FieldType);
		return returnStr;
	},
	
	//폴더 복원 버튼 생성용 - restoreFolder(FolderID) 호출
	formatFolderDeleteDate: function( pObj ){
		var deleteBtn; 
		if(pObj.item.DeleteDate!="" && pObj.item.DeleteDate!=undefined){
			deleteBtn = String.format("<a href='#' onclick='restoreFolder({0})'>{1}</a>", pObj.item.FolderID, pObj.item.DeleteDate);
		}else{
			deleteBtn = "";
		}
		return deleteBtn;
	},
	
	//게시글 MsgState 라벨처리
	formatMessageState: function(pObj){
		var msgState;
		switch(pObj.item.MsgState){
		case "C":
			msgState = coviDic.dicMap["lbl_Registor"];	//등록
			break;
		case "A":
			msgState = coviDic.dicMap["lbl_Approval"];	//승인
			break;
		case "D":
			msgState = coviDic.dicMap["lbl_Rejected"];	//거부
			break;
		case "P":
			msgState = coviDic.dicMap["lbl_Lock"];		//잠금
			break;
		case "R":
			msgState = coviDic.dicMap["lbl_RegistReq"];	//등록 요청 ( 승인 프로세스 )
			break;
		case "T":
			msgState = coviDic.dicMap["lbl_TempSave"];	//임시저장
			break;
		default:
		}

		if(new Date(pObj.item.ExpiredDate.split(" ")[0]).getTime() <= new Date().getTime()){
			msgState = coviDic.dicMap["lbl_expiration"]; // 만료
		}
		
		if(pObj.item.DeleteDate != "" && pObj.item.DeleteDate != undefined){
			msgState = coviDic.dicMap["lbl_delete"];		//DeleteDate컬럼의 데이터가 있을경우 무조건 삭제된 게시글로 표시
		}
		
		return msgState;
	},
	
	//게시글 제목 포멧: 삭제된 게시글 취소선 처리, 답글표시
	formatSubjectName: function( pObj ){
		var returnStr =  pObj.item.Subject.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		
		if(pObj.item.CommentCnt > 0 ){
			returnStr += " (" + pObj.item.CommentCnt + ")";
		}
		
		//삭제된 게시글의 경우 취소선 표시
		if(pObj.item.DeleteDate!="" && pObj.item.DeleteDate!=undefined){
			returnStr = String.format("<strike>{0}</strike>",returnStr);
		}

		// 그룹웨어설정 > 통합게시 : CLBIZ 값이(Conf) 조회 대상("Borad")과 다르고, bizSection이 global 변수가 아니게 되면서 추가 수정( 그룹웨어설정 메뉴들에서 첨부파일 조회 시 필요).
		if (typeof(bizSection) === "undefined") {
			bizSection = pObj.page.bizSection;
		}
		
		returnStr = String.format("<a href='#' onclick='javascript:boardAdmin.goViewPopup(\"{1}\", {2}, {3}, {4});' >{0}</a>", 
			returnStr,
			bizSection,
			pObj.item.Version,
			pObj.item.FolderID,
			pObj.item.MessageID
		);
		
		if(pObj.item.Depth!="0" && pObj.item.Depth!=undefined){
			returnStr = String.format("<div class='tblLink re'>{0}</div>",returnStr);
			//답글 게시글 화살표 표시
		} else {
			returnStr = String.format("<div class='tblLink'>{0}</div>",returnStr);
		}
		//댓글 개수 표시가 필요하다.
		return returnStr
	},
	
	formatRequestStatus: function ( pObj ){
		var returnStr = String.format("<a href='#' onclick='requestStatusPopup({0}, \"{1}\", \"{2}\");'>{2}</a>", pObj.item.RequestID, (pObj.item.AnswerContent==undefined?"":pObj.item.AnswerContent), pObj.item.RequestStatus);
		return returnStr;
	},
	
	formatVersion: function( pObj ){
		return "<div><span class='badge'>Ver." + pObj.item.Version + "</span></div>";
	},
	
	formatCheckOut: function( pObj ){
		var divIcoWrap = document.createElement("div");
		if(pObj.item.IsCheckOut == "Y"){
			$(divIcoWrap).append("<span class='icoDocList icoLock'><span class='tooltip'></span></span>");
		} else {
			$(divIcoWrap).append("<span class='icoDocList icoUnLock'><span class='tooltip'></span></span>");
		}
		return divIcoWrap.outerHTML;
	},
	
	formatSeqChange : function(pObj) {
		var returnStr = String.format("&nbsp;<a style='text-decoration: none; display:inline-block;height:24px;line-height:22px;background-color:#fff;border:1px solid #d5d5d5;border-radius:3px;padding:0 8px;margin:0;' href='javascript:;' onclick='fieldSeqChange(" + pObj.item.ManagerID + ","+pObj.item.Seq+","+pObj.item.DomainID+",\""+pObj.item.FieldType+"\", \"UP\");return false;'>Up</a>");
		returnStr += String.format("&nbsp;<a style='text-decoration: none; display:inline-block;height:24px;line-height:22px;background-color:#fff;border:1px solid #d5d5d5;border-radius:3px;padding:0 8px;margin:0;' href='javascript:;' onclick='fieldSeqChange(" + pObj.item.ManagerID + ","+pObj.item.Seq+","+pObj.item.DomainID+",\""+pObj.item.FieldType+"\", \"DOWN\");return false;'>Down</a>");
		return returnStr;
	},
	
	//Grid Header 항목 시작
	getGridHeader: function( pHeaderType ){
		
		var headerData;
		switch( pHeaderType ){
			case "Folder":
				headerData = [
		          	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},	//FolderID
		        	{key:'DomainID', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'MenuID', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'FolderID',  label:'ID', width:'4', align:'center'},						//FolderID							
		        	{key:'SortKey',  label:coviDic.dicMap["lbl_PriorityOrder"], width:'5', align:'center', sort:'asc', display:false},  //sortkey  
		        	{key:'DisplayName',  label:coviDic.dicMap["lbl_BoardNm"], width:'20', align:'left',		//폴더 이름
		        		formatter:function(){ 
		        			return boardAdmin.formatFolderName(this); 
		        		}
		        	},
		        	{key:'FolderType', label:coviDic.dicMap["lbl_FolderType"], width:'7', align:'center',
		        		formatter:function(){ 
		        			return boardAdmin.formatFolderType(this); 
		        		}
		        	},		//폴더 타입
		        	{key:'DeleteDate', label:coviDic.dicMap["lbl_DeleteDate"]+"(" + coviDic.dicMap["lbl_Restore"]+")", width:'10', align:'center',
		        		formatter:function () {
		        			return boardAdmin.formatFolderDeleteDate(this);
		        		}
		        	},
		        	{key:'RegisterCode',  label:coviDic.dicMap["lbl_Register"], width:'7', align:'center', 
		        		formatter: function(){ return CFN_GetDicInfo(this.item.RegisterCode)
		        	}},		//생성 유저
		        	{key:'IsDisplay', label:coviDic.dicMap["lbl_Display"], width:'7', align:'center',			//표시여부
		        		formatter:function () { 
		        			return boardAdmin.formatUpdateFlag(this); 
		         		}
		        	},      
		        	{key:'IsUse', label:coviDic.dicMap["lbl_selUse"], width:'7', align:'center',					//사용여부
		        		formatter:function () {
		        			return boardAdmin.formatUpdateFlag(this); 
		        		}
		         	},      
		        	{key:'RegistDate', label:coviDic.dicMap["lbl_RegistrationDate"]  + Common.getSession("UR_TimeZoneDisplay"), width:'9', align:'center', formatter: function(){
						return CFN_TransLocalTime(this.item.RegistDate);
					}},	//등록일자       
		         	{key:'FolderPath',  label:'FolderPath', align:'center', display:false, hideFilter : 'Y'}
		        ];
				break;
			case "Normal":
				headerData = [ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'5', align:'center'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'20', align:'left',
		        		formatter:function(){
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'MsgStateDetail',  label:coviDic.dicMap["lbl_State2"], width:'7', align:'center',
		        		formatter:function(){ 
		        			return boardAdmin.formatMessageState(this); 
		        		}
		        	},
		        	{key:'MsgState', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'DeleteDate', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorCode', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorName',  label:coviDic.dicMap["lbl_Register"], width:'10', align:'left',
		        		formatter: function(){ return CFN_GetDicInfo(this.item.CreatorName)
		        	}},
		        	{key:'FileCnt',  label:coviDic.dicMap["lbl_attach"], width:'4', align:'left'},
		        	{key:'RegistDate',  label:coviDic.dicMap["lbl_RegistDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'left', sort:'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.RegistDate);
					}},
		        	{key:'ExpiredDate',  label:coviDic.dicMap["lbl_ExpireDate"], width:'10', align:'left'}
		        ];
				break;
			case "UserDefField":
				headerData =[ 
		         	{key:'chk', label:'chk', width:'1', align:'center', formatter: 'checkbox'},
		       		{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		       		{key:'UserFormID',  label:'UserFormID', align:'center', display:false, hideFilter : 'Y'},
		       		{key:'IsOption',  label:'IsOption', align:'center', display:false, hideFilter : 'Y'},
		       		{key:'FieldName',  label:'FieldName', display:false, hideFilter : 'Y'},
		       		{key:'SortKey',  label:'SortKey', display:false, hideFilter : 'Y', sort:'asc'},
		       		{key:'DisplayName',  label:coviDic.dicMap["lbl_FieldNm"], width:'3', align:'left',
		       			formatter: function(){ return CFN_GetDicInfo(this.item.FieldName) }
		       		},
		       		{key:'FieldType',  label:coviDic.dicMap["lbl_FieldType"], width:'3', align:'center'},
		       		{key:'FieldLimitCnt',  label:coviDic.dicMap["lbl_Limit2"], width:'2', align:'center'},
		       		{key:'FieldSize',  label:coviDic.dicMap["lbl_FieldSize"], width:'3', align:'center'},
		       		{key:'OptionCnt',  label:coviDic.dicMap["lbl_OptionCnt"], width:'2', align:'center'},
		       		{key:'IsList',  label:coviDic.dicMap["lbl_DisplayList"], width:'3', align:'center'},
		       		{key:'IsSearchItem',  label:coviDic.dicMap["lbl_ListSearchItem"], width:'3', align:'center'},
		       		{key:'IsCheckVal',  label:coviDic.dicMap["lbl_surveyIsRequire"], width:'3', align:'center'}
		       ];
				break;
			case "Report":
				// 신고게시물관리 다국어.
				Common.getDicList(["lbl_no2","lbl_BoardCate2","lbl_subject","lbl_Register","lbl_SingoUserNm","lbl_ReportDate","lbl_TrialContent"]);
			
				headerData =[ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'5', align:'center'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'20', align:'left',
		        		formatter:function(){
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'CreatorCode', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorName',  label:coviDic.dicMap["lbl_Register"], width:'10', align:'left',			//등록자
		        		formatter: function(){ return CFN_GetDicInfo(this.item.CreatorName)
		        	}},
		        	{key:'ReportUserName',  label:coviDic.dicMap["lbl_SingoUserNm"], width:'10', align:'left'},	//신고자
		        	{key:'ReportDate',  label:coviDic.dicMap["lbl_ReportDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'left', sort:'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.ReportDate);
					}},
		        	{key:'ReportComment',  label:coviDic.dicMap["lbl_TrialContent"], width:'10', align:'left'},
		        	{key:'RegistDate', width:'10', align:'left', display:false, hideFilter : 'Y'}
		        ];
				break;
			case "Lock":
				// 잠금 게시물 다국어
				Common.getDicList(["lbl_no2","lbl_BoardCate2","lbl_subject","lbl_Register","lbl_RegistDate","lbl_attach","lbl_LockDate"]);
				headerData =[ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'5', align:'center'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'20', align:'left' ,
		        		formatter:function(){
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'CreatorCode', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorName',  label:coviDic.dicMap["lbl_Register"], width:'10', align:'left',
		        		formatter: function(){ return CFN_GetDicInfo(this.item.CreatorName)
		        	}},
		        	{key:'RegistDate',  label:coviDic.dicMap["lbl_RegistDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'left', formatter: function(){
						return CFN_TransLocalTime(this.item.RegistDate);
					}},
		        	{key:'FileCnt',  label:coviDic.dicMap["lbl_attach"], width:'4', align:'left'},
		        	{key:'LockDate',  label:coviDic.dicMap["lbl_LockDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'left', sort: 'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.LockDate);
					}}
		        ];
				break;
			case "DeleteNExpire":
				// 삭제게시물 다국어
				Common.getDicList(["lbl_no2","lbl_BoardCate2","lbl_subject","lbl_State2","lbl_Register","lbl_attach","lbl_RegistDate","lbl_ExpireDate"]);
				headerData =[ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'5', align:'center'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'20', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'MsgStateDetail',  label:coviDic.dicMap["lbl_State2"], width:'7', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatMessageState(this); 
		        		}
		        	},
		        	{key:'MsgState', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'DeleteDate', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorCode', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorName',  label:coviDic.dicMap["lbl_Register"], width:'10', align:'left',
		        		formatter: function(){ return CFN_GetDicInfo(this.item.CreatorName)
		        	}},
		        	{key:'FileCnt',  label:coviDic.dicMap["lbl_attach"], width:'4', align:'left'},
		        	{key:'RegistDate',  label:coviDic.dicMap["lbl_RegistDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'left', sort:'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.RegistDate);
					}},
		        	{key:'ExpiredDate',  label:coviDic.dicMap["lbl_ExpireDate"], width:'10', align:'left'}
		        ];
				break;
			case "RequestModify":
				// 수정요청게시물관리
				Common.getDicList(["lbl_no2","lbl_BoardCate2","lbl_subject","lbl_Requester","lbl_RequestDate","lbl_RequestContent","lbl_ProcessContents","lbl_State"]);
				headerData =[ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'5', align:'center'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'RequestID',  label:'RequestID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'DeleteDate', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'20', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'RequestorName',  label:coviDic.dicMap["lbl_Requester"], width:'6', align:'left',
		        		formatter: function(){ return CFN_GetDicInfo(this.item.RequestorName)
		        	}},
		        	{key:'RequestDate',  label:coviDic.dicMap["lbl_RequestDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'6', align:'left', sort:'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.RequestDate);
					}},
		        	{key:'RequestMessage',  label:coviDic.dicMap["lbl_RequestContent"], width:'10', align:'left'},
		        	{key:'AnswerContent',  label:coviDic.dicMap["lbl_ProcessContents"], width:'10', align:'left'},
		        	{key:'RequestStatus',  label:coviDic.dicMap["lbl_State"], width:'6', align:'left',
		        		formatter:function(){
		        			return boardAdmin.formatRequestStatus(this);
		        		}
		        	}
		        ];
				break;
			case "Stats":
				headerData =[ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'4', align:'center', sort:'desc'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'CreatorName', label:coviDic.dicMap["lbl_Register"], width:'6', align:'left'},
		        	{key:'CreatorCode',  label:coviDic.dicMap["lbl_Register"]+' ID', width:'6', align:'left'},
		        	{key:'EmpNo', label:coviDic.dicMap["lbl_PersonNo"], width:'4', align:'left'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'10', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'RegistDate',  label:coviDic.dicMap["lbl_CreateDates"] + Common.getSession("UR_TimeZoneDisplay"), width:'7', align:'left', formatter: function(){
						return CFN_TransLocalTime(this.item.RegistDate);
					}},
		        	{key:'ReadCnt',  label:coviDic.dicMap["lbl_ReadCount"], width:'3', align:'left'},
		        	{key:'CommentCnt',  label:coviDic.dicMap["lbl_AnswerNo"], width:'3', align:'left'}
		        ];
				break;
			case "Doc":
				headerData =[ 
		         	{key:'chk', label:'chk', width:'2', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:coviDic.dicMap["lbl_no2"], width:'5', align:'center'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:coviDic.dicMap["lbl_BoardCate2"], width:'7', align:'left'},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'20', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatSubjectName(this); 
		        		}
		        	},
		        	{key:'MsgStateDetail',  label:coviDic.dicMap["lbl_State2"], width:'7', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatMessageState(this); 
		        		}
		        	},
		        	{key:'MsgState', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'MsgType', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'DeleteDate', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorCode', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreatorName',  label:coviDic.dicMap["lbl_Register"], width:'10', align:'left',
		        		formatter: function(){ return CFN_GetDicInfo(this.item.CreatorName)
		        	}},
		        	{key:'FileCnt',  label:coviDic.dicMap["lbl_attach"], width:'4', align:'left'},
		        	{key:'RegistDate',  label:coviDic.dicMap["lbl_RegistDate"] + Common.getSession("UR_TimeZoneDisplay"), width:'10', align:'left', sort:'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.RegistDate);
					}},
		        	{key:'ExpiredDate',  label:coviDic.dicMap["lbl_ExpireDate"], width:'10', align:'left'}
		        ];
				break;
			case "OwnerManage":
				headerData =[ 
		         	{key:'chk', label:'chk', width:'1', align:'center', formatter: 'checkbox'},
		        	{key:'MessageID',  label:'MessageID', display:false, hideFilter : 'Y'},
		        	{key:'FolderID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'MenuID',  label:'MenuID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Seq',  label:'Seq', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Step',  label:'Step', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'Depth',  label:'Depth', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'FolderName',  label:'FolderName', display:false, hideFilter : 'Y'},
		        	{key:'IsCheckOut',  label:'IsCheckOut', display:false, hideFilter : 'Y'},
		        	{key:'Version',  label:'Ver', width:'3', align:'center', 
		        		formatter:function(){
		        			return boardAdmin.formatVersion(this);
		        		}
		        	},
		        	{key:'Subject',  label:coviDic.dicMap["lbl_subject"], width:'15', align:'left',
		        		formatter:function(){ 
		        			return boardAdmin.formatSubjectName(this);
		        		}
		        	},
		        	{key:'Number',  label:"문서번호", width:'7', align:'center'},
		        	{key:'IsCheckOut',  label:'체크아웃', width:'2', align:'center',
		        		formatter:function(){
		        			return boardAdmin.formatCheckOut(this);
		        		}
		        	},
		        	{key:'RegistDept', label:'등록부서',width:'7', align:'center',
		        		formatter: function(){ return this.item.RegistDeptName;
		        	}},
		        	{key:'RegistDept', label:'', display:false, hideFilter : 'Y'},
		        	{key:'OwnerName',  label:'소유자', width:'10', align:'center' },
		        	{key:'RevisionDate', label:'개정일' + Common.getSession("UR_TimeZoneDisplay"), width:'7', align:'center', sort:'desc', formatter: function(){
						return CFN_TransLocalTime(this.item.RevisionDate);
					}},
		        	{key:'MsgType', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'DeleteDate', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'CreaterDepth', align:'left', display:false, hideFilter : 'Y'},
		        	{key:'OwnerCode', align:'left', display:false, hideFilter : 'Y'},
		        	
		        ];
				break;
			case "DocNumberManage":
				headerData =[ 
		         	{key:'chk', label:'chk', width:'1', align:'center', formatter: 'checkbox'},
		        	{key:'ManagerID',  label:'MessageID', display:false, hideFilter : 'Y'},
		        	{key:'DomainID',  label:'DomainID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'DomainName',  label:'소유회사', width:'5', align:'center' },
		        	{key:'Seq',  label:'순서', display:false, hideFilter : 'Y', sort:'desc'},
		        	{key:'FieldType',  label:'필드명', width:'7', align:'center', formatter:function () {
	        			return boardAdmin.formatFieldtype(this); 
	        		}},
		        	{key:'FieldLength',  label:'필드크기', width:'7', align:'center'},
		        	{key:'LanguageCode',  label:'언어', width:'3', align:'center'},
		        	{key:'Separator',  label:'구분', width:'3', align:'center'},
		        	{key:'IsUse', label:coviDic.dicMap["lbl_selUse"], width:'5', align:'center',					//사용여부
		        		formatter:function () {
		        			return boardAdmin.formatDocNumberUpdateFlag(this); 
		        		}
		         	}, 
		         	{key:'CreateDate', label:'생성일' + Common.getSession("UR_TimeZoneDisplay"), width:'7', align:'center', formatter: function(){
						return CFN_TransLocalTime(this.item.CreateDate);
					}}
					, {key:'Action', label: '우선순위', width:'7', align: 'center', formatter:function() {
						return boardAdmin.formatSeqChange(this);
					}}
		        ];
				break;
			case "DocNumberStats":
				headerData =[ 
		        	{key:'DomainID',  label:'FolderID', align:'center', display:false, hideFilter : 'Y'},
		        	{key:'DomainName',  label:'소유회사', width:'5', align:'center' },
		        	{key:'Prefix',  label:'접두사', width:'15', align:'center' },
		        	{key:'Number',  label:'전체 개수', width:'5', align:'center' },
		         	{key:'CreateDate', label:'생성일' + Common.getSession("UR_TimeZoneDisplay"), width:'7', align:'center', formatter: function(){
						return CFN_TransLocalTime(this.item.CreateDate);
					}}
		        ];
				break;
			default:
				
		}
		
		return headerData;
	},
	
	//연결 URL 사용시 URL입력 input 표시
	changeIsUrl : function( pObj ){
		if($(pObj).val() == "Y"){
			$("#linkURL").show();
		} else {
			$("#linkURL").hide();
		}
	},
	
	//탭 클릭 했을 경우 처리
	clickTab: function(pObj){
		$(".AXTab").attr("class","AXTab");
		$(pObj).addClass("AXTab on");
		var str = $(pObj).attr("value");
		if(str == "basic") {
			$("#searchTab_Basic").show();	
			$("#searchTab_AuthSetting").hide();
			$("#searchTab_BasicOption").hide();
			$("#searchTab_ExtOption").hide();
			$("#btn_preview").hide();
		}else if(str == "authSetting") {
			$("#searchTab_Basic").hide();	
			$("#searchTab_AuthSetting").show();
			$("#searchTab_BasicOption").hide();
			$("#searchTab_ExtOption").hide();
			$("#btn_preview").hide();
		}else if(str == "basicOption") {
			$("#searchTab_Basic").hide();	
			$("#searchTab_AuthSetting").hide();
			$("#searchTab_BasicOption").show();
			$("#searchTab_ExtOption").hide();
			$("#btn_preview").hide();
		}else if(str == "extOption") {
			$("#searchTab_Basic").hide();
			$("#searchTab_AuthSetting").hide();
			$("#searchTab_BasicOption").hide();
			$("#searchTab_ExtOption").show();

			if($("#chkUseUserForm").prop("checked")){
				userDefFieldGrid.init();
			}
			$("#btn_preview").show();
		}
		//개선필요.
		selectSmallCategoryList();
		//CHECK: AXSelectbox UI문제
		//$("select").bindSelect();
		$(window).resize();
	},
	
	//폴더유형에따른 게시판Default환경설정 셋팅
	changeFolderTypeSetDefault : function (){
        $('#selectMenuID').prop('disabled',true);
        $('#chkUseBasicConfig').prop('checked', true);
        selectDefaultConfig();
	},
	
	//폴더유형 변경시 상단의 탭 표시,숨김 처리
	changeFolderType : function ( pObj ){
		if (mode == "edit") {
	        if ($(pObj).val() == "Folder" || $(pObj).val() == "Root" || $(pObj).val() == "DocRoot" || $(pObj).val() == "ShareBoard") {
				//기본옵션설정탭/확장옵션설정탭 비활성화
				$('#basicOptionTab').hide();
				$('#extOptionTab').hide();
				
				// 최상위 폴더 또는 폴더일 경우 등록알림, 등록알림 수신자, 등록알림 제외자 숨김
				$("#regNotifyTr").hide();
				$("#receiverTr").hide();
				$("#excepterTr").hide();
	        } else if($(pObj).val() == "Doc"){
	        	$('#basicOptionTab').hide();
	        	$('#extOptionTab').show();
	        	
	        	$("#regNotifyTr").show();
				$("#receiverTr").show();
				$("#excepterTr").show();
				$("#selectIsAdminNotice").css("visibility", "visible");
	        } else if($(pObj).val() == "QuickComment") {
	        	//기본옵션설정탭/확장옵션설정탭 비활성화
				$('#basicOptionTab').hide();
				$('#extOptionTab').hide();
	        } else {
				//기본옵션설정탭/확장옵션설정탭 활성화
				$('#basicOptionTab').show();
				$('#extOptionTab').show();
				
				$("#regNotifyTr").show();
				$("#receiverTr").show();
				$("#excepterTr").show();
				$("#selectIsAdminNotice").css("visibility", "visible");
	        }

	    } else {
	    	if($(pObj).val() == "Root" || $(pObj).val() == "DocRoot"){
				$('#selectMenuID').prop('disabled', false);
			} else {
				$('#selectMenuID').prop('disabled', true).val(menuID);
			}
	    	
	    	if($(pObj).val() == "Root" || $(pObj).val() == "DocRoot" || $(pObj).val() == "Folder"){
	    		$("#regNotifyTr").hide();
	    		$("#receiverTr").hide();
	    		$("#excepterTr").hide();
	    	} else {
	    		$("#regNotifyTr").show();
	    		$("#receiverTr").show();
	    		$("#excepterTr").show();
	    		$("#selectIsAdminNotice").css("visibility", "visible");
	    	}
	    }
		
	 	//폴더유형에따른 게시판 특정옵션 Disable 처리
	    boardAdmin.disabledBoardOptByFolderType(pObj);
	    //확장 옵션 탭 설정사항 변경 처리 
	    boardAdmin.enDisableExtOptionField();
	},
	
	//폴더 타입에 따른 체크박스 및 selectbox 옵션 Disable 처리
	//권한 관련 설정사항 체크 필요
	disabledBoardOptByFolderType : function( pObj ){
		$('#chkUsePopNotice').attr('disabled', false);
	    $('#chkUseFile').attr('disabled', false);
	    $('#chkUseImage').attr('disabled', false);
	    $('#chkUseSecret').attr('disabled', false);
	    $('#chkUseAnony').attr('disabled', false);
	    $("#selectIsMsgSecurity").bindSelectDisabled(false);
	    $('#chkUseVideo').prop('checked', false).attr('disabled', true);
	    $('#chkUsePubDeptName').attr('disabled', false);
	    $('#txtLimitFileCnt').removeAttr('readonly');
	    if ($(pObj).val() == "Board") {  
	        //일반게시
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	    } else if ($(pObj).val() == "Notice") {  
	        //공지게시
	        $('#chkUsePopNotice').prop('checked', true).attr('disabled', true);
	        $('#chkUseAnony').prop('checked', false).attr('disabled', true);
	        $('#selectIsMsgSecurity').bindSelectSetValue("N").bindSelectDisabled(true);
	    } else if ($(pObj).val() == "File") {  
	        //자료게시
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseFile').prop('checked', true).attr('disabled', true);
	    } else if ($(pObj).val() == "Album") {  
	        //앨범게시
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseImage').prop('checked', true).attr('disabled', true);
	        $('#selectIsMsgSecurity').bindSelectSetValue("N").bindSelectDisabled(true);
	    } else if ($(pObj).val() == "Video") {  
	        //동영상 게시
	        $('#chkUsePopNotice').prop('checked', false);
	        $('#chkUsePopNotice').attr('disabled', true);
	        $('#chkUseFile').prop('checked', true).attr('disabled', true);
	        $('#chkUseVideo').prop('checked', true).attr('disabled', true);
	        $('#chkUseReply').prop('checked', false).attr('disabled', true);
	        $('#selectIsMsgSecurity').bindSelectSetValue("N").bindSelectDisabled(true);
	        $('#txtLimitFileCnt').attr('readonly', true).val("1");
	    } else if ($(pObj).val() == "Anonymous") {  
	        //익명게시
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseAnony').prop('checked', true).attr('disabled', true);
	        $('#chkUseSecret').prop('checked', false).attr('disabled', true);
	    } else if ($(pObj).val() == "Contents") {  
	        //컨텐츠 게시
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseTopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseExpiredDate').prop('checked', false).attr('disabled', true);
	        $('#chkUseAnony').prop('checked', false).attr('disabled', true);
	        $('#chkUseReply').prop('checked', false).attr('disabled', true);
	        $('#chkUseComment').prop('checked', false).attr('disabled', true);
	        $('#chkUseIntrest').prop('checked', false).attr('disabled', true);
	        $('#chkUseRecommend').prop('checked', false).attr('disabled', true);
	        $('#chkUseScrap').prop('checked', false).attr('disabled', true);
	        $('#chkUseCopy').prop('checked', false).attr('disabled', true);
	        $('#chkUseCommentRecommend').prop('checked', false).attr('disabled', true);
	    } else if ($(pObj).val() == "LinkSite") {  
	        //링크 사이트
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseTopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseExpiredDate').prop('checked', false).attr('disabled', true);
	        $('#chkUseAnony').prop('checked', false).attr('disabled', true);
	        $('#chkUseReply').prop('checked', false).attr('disabled', true);
	        $('#chkUseComment').prop('checked', false).attr('disabled', true);
	        $('#chkUseIntrest').prop('checked', false).attr('disabled', true);
	        $('#chkUseRecommend').prop('checked', false).attr('disabled', true);
	        $('#chkUseScrap').prop('checked', false).attr('disabled', true);
	        $('#chkUseCopy').prop('checked', false).attr('disabled', true);
	        $('#chkUseCommentRecommend').prop('checked', false).attr('disabled', true);
	        $('#chkUseLink').prop('checked', false).attr('disabled', true);
	        $('#chkUseMessageAuth').prop('checked', false).attr('disabled', true);
	        $('#chkUseMessageReadAuth').prop('checked', true);
	    } else if ($(pObj).val() == "AnonymousPW") {  
	        //비밀익명게시
	        $('#chkUsePopNotice').prop('checked', false).attr('disabled', true);
	        $('#chkUseAnony').prop('checked', true).attr('disabled', true);
	        $('#chkUseSecret').prop('checked', false).attr('disabled', true);
	        $('#chkUseRecommend').prop('checked', false).attr('disabled', true);
	        $('#chkUseScrap').prop('checked', false).attr('disabled', true);
	        $('#chkUseCopy').prop('checked', false).attr('disabled', true);
	        $('#chkUseMessageAuth').prop('checked', false).attr('disabled', true);
	        $('#chkUseMigrationToDoc').prop('checked', false).attr('disabled', true);
	    } else {
	    	$('#selectIsMsgSecurity').bindSelectSetValue("N").bindSelectDisabled(true);
	    }
	},
	
	//확장 옵션 탭 내부 체크박스 UI컨트롤
	enDisableExtOptionField: function () {
	    // 카테고리(분류)
	    if ($("#chkUseCategory").prop("checked")) {
	        $("#divCategory").show();
	        selectLargeCategoryList();
	        if($("#selectLargeCategory option").size()>0)
	        	selectSmallCategoryList();
	    } else {
	        $("#divCategory").hide();
	    }

	    // 본문양식
	    if ($("#chkUseBodyForm").prop("checked")) {
	        $("#divBodyForm").show();
	        selectBodyForm();
	    } else {
	        $("#divBodyForm").hide();
	    }

	    // 사용자정의필드  
	    if ($("#chkUseUserForm").prop("checked") ) {
	        $("#divUserDefField").show();
	        setUserDefFieldGrid();	//사용할경우 Grid데이터 조회
	    } else {
	        $("#divUserDefField").hide();
	    }

	    // 승인프로세서
	    if ($("#chkUseOwnerProcess").prop("checked") || $("#chkUseUserProcess").prop("checked")) {
	        $("#divApprovalProcSet").show();
	        
	        // 답글 사용 여부일 경우 답글 승인 프로세스 사용여부 선택 가능	        
	        if(bizSection != "Doc" && $("#chkUseReply").prop("checked")) {
	        	$("#divUseReplyProcess").show();
	        } else {
	        	$("#divUseReplyProcess").hide();
	        }
	        
	    	if($('#divApprovalProcSet input:checkbox').length == 0) {
	    		selectProcessActivityList();	    		
	    	}
	    } else {
	        $("#divApprovalProcSet").hide();
	        $("#divUseReplyProcess").hide();
	        $("#chkUseReplyProcess").prop('checked', false)
	    }

	    // 진행상태
	    if ($("#chkUseProgressState").prop("checked")) {
	        $("#divProgressState").show();
	        selectProgressStateList();	//사용할경우 진행 상태 조회
	    } else {
	        $("#divProgressState").hide();
	    }
	},

	//만족도 체크시 원본글/덧글 설정
	enDisableSatisfactionField: function() {
	    // 만족도
	    if ($("#chkUseSatisfaction").prop("checked")) {
	        $("#spnUseSatisfaction").show();
	    } else {
	        $("#spnUseSatisfaction").hide();
	        $("[group=chkUseSatisfaction]").attr("checked",false);
	    }
	},

	//만료일 사용 설정시 기본값 0/90
	clickUseExpireDate: function() {
	    if ($('#chkUseExpiredDate').prop('checked'))
	        $("#txtExpireDay").val("90");
	    else
	        $("#txtExpireDay").val("0");
	},

	//만족도 원본글 체크박스와 만족도 답글 체크박스 둘중 하나만 선택
	checkOneByUseSatisfactionBox: function(pChk) {
	    if (pChk.checked) {
	        $("[group=chkUseSatisfaction]").each(function (i) {
	            $(this).prop('checked', false);
	        });
	        pChk.checked = true;
	    }
	},

	//운영자지정프로세스 체크박스와 사용자지정프로세스 체크박스 둘중 하나만 선택되게 : CS파일과 동일
	checkOneByUseProcessCheckBox: function(pChk) {
	    if (pChk.checked) {
	        $("[group=chkUseProcess]").each(function (i) {
	            $(this).attr('checked', false);
	        });
	        pChk.checked = true;
	        
	        // 운영자지정프로세스 사용 시 비밀글 사용 불가
	        if($("#chkUseSecret").prop("checked")) {
				Common.Inform(Common.getDic('msg_disableUseSecret'));
			}
			$("#chkUseSecret").prop("checked", false);
	    }
	    else {
			// 운영자지정프로세스 사용 해제 시 비밀글 설정 원복
			$("#chkUseSecret").prop("checked", ($("#chkUseSecret").attr("data-value") == 'Y'));
		}
	    $("#chkUseSecret").prop("disabled", pChk.checked);
	    
	    boardAdmin.enDisableExtOptionField();
	},
	
	//결재행위 유형 변경
//    ChangeActivityType: function(pSeq, pObj) {
//        if ($("#spnApprover_" + pSeq).attr("ActivityType") == "Serial") {
//            $("#spnApprover_" + pSeq).attr("ActivityType", "Parall");
//            $(pObj).text(Common.getDic("lbl_Parall"));
//        } else {
//            $("#spnApprover_" + pSeq).attr("ActivityType", "Serial");
//            $(pObj).text(Common.getDic("lbl_Serial"));
//        }
//    },
	
	//*****************************************************************************************************************************************/
	
	//사용자 정의 필드에 필드 옵션 Selectbox 표시,숨김처리
	changeFieldType : function( pObj ){
		switch ($(pObj).val()) {	
		case "Radio":  	//Radio, CheckBox, DropDown 항목은 옵션을 표시
		case "CheckBox": 
		case "DropDown":
			$("#rowFieldOption").show();	//필드 옵션 표시
			$("#spanFieldLimitCnt").hide();	//제한 입력 항목 숨김
			
			if($("#hidUserFormID").val() == ""){
				var option = String.format('<ul><li><input type="text" id="fieldOption_{0}" name="fieldOption_{0}" class="w60" />', 0);
				option += String.format('<input type="hidden" id="hidFieldOption_{0}_DicInfo"/>', 0);
				option += String.format('<input type="button" value="{1}" class="AXButton" onclick="javascript:userDefFieldnDicLayerPopup({0},$(\'#fieldOption_{0}\'),$(\'#hidFieldOption_{0}_DicInfo\'));">',0, Common.getDic("lbl_MultiLang2"));
				option += String.format('<input type="button" id="btnAddOption" value="+" class="AXButton" onclick="javascript:boardAdmin.addOption()" /></li></ul>');	 
				$("#optionPlaceHolder").html(option);
			}
			break;
		case "Input":	//Input, TextArea 항목은 제한 입력 항목 표시 
		case "TextArea":	
			$("#spanFieldLimitCnt").show();
			$("#rowFieldOption").hide();
			$("#optionPlaceHolder").html("");
			$("#fieldLimitCnt").val("");
			break;
		default:
			$("#rowFieldOption").hide();
			$("#optionPlaceHolder").html("");
			$("#fieldLimitCnt").val("");
		}
	},
	//옵션 추가
	addOption : function (){
		var lastIndex = $("input[name^=fieldOption_]").size();
		var option = String.format('<ul><li><input type="text" id="fieldOption_{0}" name="fieldOption_{0}" class="AXInput" />',lastIndex);

		$("#btnAddOption").remove();	//추가버튼 삭제
		option += String.format('<input type="hidden" id="hidFieldOption_{0}_DicInfo"/>',lastIndex);
		option += String.format('<input type="button" value="{1}" class="AXButton" onclick="javascript:userDefFieldnDicLayerPopup({0},$(\'#fieldOption_{0}\'),$(\'#hidFieldOption_{0}_DicInfo\'));">',lastIndex, Common.getDic("lbl_MultiLang2"));
		option += String.format('<input type="button" id="btnDeleteOption" value=" - " class="AXButton" onclick="javascript:deleteOption(this)" /></li></ul>');	 
		$("#optionPlaceHolder").append(option);

		//입력항목 추가후 옵션 추가 버튼을 마지막 항목에 추가 
		$("#optionPlaceHolder>ul>li>input:last").after('<input type="button" id="btnAddOption" value=" + " class="AXButton" onclick="javascript:boardAdmin.addOption()">');
	},
	
	//validation check - 분류명, 우선순위, 확장옵션 사용 체크 후 등록하지 않았을경우
	checkBoardConfigValidation: function(){
		var flag = false;	//false로 실패처리 

		if (!XFN_ValidationCheckOnlyXSS(false)) { return; }

		//개별호출-일괄호출
		Common.getDicList(["msg_EnterCateNm","msg_Common_01","msg_NoAddCate","msg_NoAddBodyFormFile","msg_survey25","msg_checkOperator","msg_ChooseMenu","msg_CannotExceed_50"]);
		
		if ($("#selectMenuID").val() == "") {
	        parent.Common.Warning(coviDic.dicMap["msg_ChooseMenu"], "Warning Dialog", function () {     //소속 메뉴 선택 
	        });
	        return false;
	    }
		
		if ($("#folderName").val() == "") {
	        parent.Common.Warning(coviDic.dicMap["msg_EnterCateNm"], "Warning Dialog", function () {     //분류명 입력
	        	document.getElementById("folderName").focus();
	        });
	        return false;
	    }

		if ($("#folderName").val().length > 50) {
	        parent.Common.Warning(coviDic.dicMap["msg_CannotExceed_50"], "Warning Dialog", function () {     //분류명은 50자를 넘을 수 없습니다.
	        	document.getElementById("folderName").focus();
	        });
	        return false;
	    }
		
		if($("#ownerCode").val() == ""){
			parent.Common.Warning(coviDic.dicMap["msg_checkOperator"], "Warning Dialog", function () {     //담당자, 운영자 지정
	        });
			return false;
		}

		if(mode != "create" && coviACL_getACLData("con_acl") == "[]"){
			parent.Common.Warning(coviDic.dicMap["msg_Common_01"], "Warning Dialog", function () {
	        });
	        return false;
		}

		 //카테고리
	    if ($('#chkUseCategory').prop('checked')) {
	        if ($("#selectLargeCategory option").size() == 0) {
	        	parent.Common.Warning(coviDic.dicMap["msg_NoAddCate"]);  //카테고리가 추가되지 않았습니다
	            return false;
	        }
	    }

	    //본문양식 선택
	    if ($('#chkUseBodyForm').prop('checked') && $("[name=spnBodyForm]").length == 0) {
	    	parent.Common.Warning(coviDic.dicMap["msg_NoAddBodyFormFile"]);  // 게시판 설명이 등록되지 않았습니다
	        return false;
	    }
	    
	    //승인 프로세스
	    if ($('#chkUseOwnerProcess').prop('checked')) {
	        if ($("#divApprovalProcSet #spnApprovalLine div").size() == 0) {
	        	parent.Common.Warning(coviDic.dicMap["msg_survey25"]);  //승인자를 지정하세요.
	            return false;
	        }
	    }

		if(bizSection == "Board"){
		    // 기본옵션설정 1건 첨부용량, 최대 용량 비교
		    if(Number($("#txtLimitFileSize").val()) > Number(convertToMegaBytes(Common.getGlobalProperties("groupware.max.upload.size")))) {
		    	parent.Common.Warning(Common.getDic("msg_FileSizeLimit").replace("{0}", coviFile.convertFileSize(Common.getGlobalProperties("groupware.max.upload.size"))));	    	
	            return false;
		    }
		    
		    // 기본옵션설정 총 첨부용량, 최대 용량 비교
		    if(Number($("#txtTotalFileSize").val()) > Number(convertToMegaBytes(Common.getGlobalProperties("groupware.max.upload.size")))) {
		    	parent.Common.Warning(Common.getDic("msg_FileSizeLimit").replace("{0}", coviFile.convertFileSize(Common.getGlobalProperties("groupware.max.upload.size"))));
	            return false;
		    }
		    
		    // 기본옵션설정 1건 첨부용량, 총 첨부용량 비교
		    if(parseInt($("#txtLimitFileSize").val()) > parseInt($("#txtTotalFileSize").val())) {
		    	parent.Common.Warning(Common.getDic("msg_LimitLargerThanTotal"));  // 1건 첨부용량이 총 첨부용량 보다 큽니다.
	            return false;
		    }
		}

	    return true;
	},
	
	//순차, 병렬 승인 변경시
	changeActivityType: function( pSeq, pObj){
		if ($("#spnApprover_" + pSeq).attr("ActivityType") == "Serial") {
	        $("#spnApprover_" + pSeq).attr("ActivityType", "Parall");
	        $(pObj).val(Common.getDic("lbl_Parallel"));
	    } else {
	        $("#spnApprover_" + pSeq).attr("ActivityType", "Serial");
	        $(pObj).val(Common.getDic("lbl_Serial"));
	    }
	},
	
	//승인자 정보 추출
	setProcessData: function(){
		var approverArray = [];
		var prevActivityType = "";
		var step = 0, substep = 0;
		$("[name=spnApprover]").each(function(i, item){
			var activityDef = new Object();
			
			//순차, 병렬 승인에 따라 step 값 설정
			if(( prevActivityType != "" && $(this).attr("activitytype") == "Serial" )
					|| ( prevActivityType == "Serial" && $(this).attr("activitytype") == "Parall" )){
				step++;
				substep = 0;
				//approverArray[approverArray.length-1].ActivityType //이전 행위 타입
			} else if (prevActivityType == "Parallel" && $(this).attr("activitytype") == "Parall") {
				substep++;
            }
			
			activityDef.Step = step;
			activityDef.SubStep = substep;
			activityDef.ActorRole = $(this).attr("actorrole");
		    activityDef.ActorCode = $(this).attr("usercode");
		    activityDef.ActivityType = $(this).attr("activitytype");
			prevActivityType = $(this).attr("activitytype");

	    	approverArray.push(activityDef);
		});
		
		return JSON.stringify(approverArray); 
	},
	
	goViewPopup: function(pBizSection, pVersion, pFolderID, pMessageID){
		var url = String.format("/groupware/board/goBoardViewPopup.do?CLBIZ={0}&version={1}&folderID={2}&messageID={3}&viewType=Popup", pBizSection, pVersion, pFolderID, pMessageID);
		parent.Common.open("", "boardViewPop", "상세보기", url, "1080px", "600px", "iframe", true, null, null, true);
	}

}