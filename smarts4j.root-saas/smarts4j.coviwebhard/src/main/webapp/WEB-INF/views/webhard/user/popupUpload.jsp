<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/webhard/common/layout/userInclude.jsp"></jsp:include>

<style>
	button.btnTypeDefault{
		cursor: pointer;
	}
</style>

<div id="con_file">				
	<div>
		<div class="addDataBtn">
			<!-- 폴더 추가-->
			<button type="button" id="folderUploadBtn" class="btnTypeDefault" name="folderBtn" style="display: none;">
				<i class="fa fa-plus" aria-hidden="true"></i> <spring:message code='Cache.lbl_uploadFolder'/> <!-- 폴더 업로드 -->
			</button>
			<input id="uploadFolders" type="file" webkitdirectory=ture multiple="" style="position: absolute; left: -9999px;">
			<button type="button" class="btnTypeDefault" id="fileUploadBtn" name="addBtn">
				<i class="fa fa-plus" aria-hidden="true"></i> <spring:message code='Cache.lbl_uploadFile'/> <!-- 파일 업로드 -->
			</button>
			<button type="button" class="btnTypeDefault btnTypeBg" id="uploadBtn" name="uploadBtn">
				<i class="fa fa-upload" aria-hidden="true"></i> <spring:message code='Cache.btn_register'/> <!-- 등록 -->
			</button>
			<div id="divFileLimitInfo" style="display: inline-block; margin-left: 10px;"></div>
			<input id="uploadFiles" type="file" multiple="" style="position: absolute; left: -9999px;">
		</div>
		<div id="fileBox" style="min-height: 125px; border: 2px dashed #0087F7; border-radius: 5px;">
			<div class="addData">
				<div class="dragFileBox">
					<span class="dragFile">icn</span><spring:message code='Cache.lbl_DragFile'/> <!-- 마우스로 파일을 끌어 오세요. -->
				</div>
				<div class="confile"></div>
			</div>
		</div>
	</div>
</div>

<script>
	
	(function( param ){
		
		var _chrome = ((window.navigator.userAgent.indexOf("Chrome") != -1) ? true : false);
		var _firefox = ((window.navigator.userAgent.indexOf("Firefox") != -1) ? true : false);
		
		var convertFileSize = function(pSize){
			var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
		    if (pSize == 0) return 'n/a';
		    var i = parseInt(Math.floor(Math.log(pSize) / Math.log(1024)));
		    return (pSize / Math.pow(1024, i)).toFixed(1) + sizes[i];
		}
		
		var uuidv4 = function uuidv4(){		
			return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c){
				var r = coviCmn.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
				return v.toString(16);
			});
		}
		
		var getIconClass = function(extension){
			var strReturn = "";
			var lowerExt = (extension||"").toLowerCase();
			
			if (lowerExt == "xls" || lowerExt == "xlsx") {
				strReturn = "exCel";
			} else if (lowerExt == "jpg" || lowerExt == "png" || lowerExt == "bmp") {
				strReturn = "imAge";
			} else if (lowerExt == "doc" || lowerExt == "docx") {
				strReturn = "woRd";
			} else if (lowerExt == "ppt" || lowerExt == "pptx") {
				strReturn = "pPoint";
			} else if (lowerExt == "txt" || lowerExt == "hwp") {
				strReturn = "teXt";
			} else {
				strReturn = "etcFile";
			}
			
			return strReturn;
		}
		
		var scanDir = function(item){
			var getFile = function(fileEntry){
				try {
			    	return new Promise(function(resolve, reject){
			    		fileEntry.file(function(file){ resolve( file ); });		    		
			    	});
				} catch(err) {
			    	console.log(err);
				}
			}
			
			var readentry = function(directoryReader, item){
				var promiseList = new Array();
				item && promiseList.push(item);
				return new Promise(function(resolve, reject){
					directoryReader.readEntries(function(entries){
						entries.length > 0 && promiseList.push(readentry(directoryReader));
						entries.forEach(function(entry){
							if (entry.isDirectory) {
								promiseList.push( scanDir(entry) );
							} else {
								promiseList = promiseList.concat( getFile(entry) );
							}
						});
						Promise.all(promiseList).then(function(value){
							resolve(value);
						});
					});
				});
			}
			
			return new Promise(function(resolve,reject){
				var directoryReader = item.createReader();
				resolve(readentry(directoryReader,item));
			});
		}
		
		var createRowTemplate = function(extension){
			var $dvFileItem = $("<div>",{ "class" : "dvFileItem" });
			var $dvFileItemHolder = $("<div>", { "class" : "dvFileItemHolder" } );
			var $dvFileItemCell1 = $("<div>", { "class" : "dvFileItemCell" } ).append( $("<span>",{ "class" : getIconClass( extension ), "text" : "<spring:message code='Cache.btn_attachfile'/>" }) ); // 파일첨부
			var $dvFileItemCell2 = $("<div>", { "class" : "dvFileItemCell" } );
			var $dvFileItemCell3 = $("<div>", { "class" : "dvFileItemCell" } ).append( $("<a>").append( $("<i>",{ "class" : "fa fa-times fa-lg", "aria-hidden" : "true" }) ) );
			
			$dvFileItemHolder
				.append( $dvFileItemCell1 )
				.append( $dvFileItemCell2 )
				.append( $dvFileItemCell3 )
				.appendTo( $dvFileItem );
			return $($dvFileItem);
		}
		
		var addFolderClick = function(){
			if (_chrome || _firefox) {
				$("#uploadFolders").focus().trigger('click');
			} else {
				Common.Warning("<spring:message code='Cache.msg_59'/>"); // 잘못된 호출 입니다.
			}
		}
		
		var addFileClick = function(){
			$("#uploadFiles").focus().trigger('click');
		}
		
		// 파일추가
		var fileOnChange = function(){
			var $this = $(event.target);
			var $files = Array.prototype.slice.call($this.prop('files'));
			$files.reduce(function(acc, cur, idx, arr){
				var fName = cur.name;
				var sExtension = fName.split('.')[fName.split('.').length - 1];
				var $row = createRowTemplate(sExtension);
				var fileUUID = uuidv4();
				$(".dvFileItemCell:eq(1)", $row).text( fName+"("+ convertFileSize( cur.size ) +")" );
				$row.data('files', [
					cur
				]).data( 'fileInfos', [{
					filePath 	: 	"/" + fileUUID
					,folderUUID	:	(param.targetFolder || "")
					,UUID		:	fileUUID
					,fileName 	: 	fName
					,boxUUID	:	param.boxUUID
					,size		:	cur.size
				}]).data('showList', {'name': cur.name, 'size': cur.size}).appendTo( acc );
				return acc;
			}, $(document.createDocumentFragment()) ).appendTo("#fileBox .confile");
			$("#fileBox .confile > div").length && $("#fileBox .dragFileBox").hide();
		}
		
		// 폴더추가
		var folderOnChange = function(){
			var $this = $(event.target);
			var $files = Array.prototype.slice.call($this.prop("files"));
			var folderUuidList = [];
			
			var uploadObj = $files.reduce(function(acc, cur, idx, arr){
				var folderPathArr = cur.webkitRelativePath.split("/");
				folderPathArr = folderPathArr.slice(0, (folderPathArr.length - 1));
				
				var folderObj = folderPathArr.map(function(folder, fIdx, fArr){
					var isNew = !(acc.dir.length ? acc.dir.find(function(dir){
						return dir.folderName === folder
								&& dir.folderNamePath === (fIdx ? "/" + fArr.slice(0, fIdx).join("/") + "/" + folder : "/" + folder);
					}) : false);
					
					if(isNew){
						var folderUuid = uuidv4();
						var folderUuidPath = fIdx ? replaceUuid(acc.dir, fArr.slice(0, fIdx)) : "";
						var folderInfo = {
							  folderPath		: folderUuidPath ? folderUuidPath + "/" + folderUuid : "/" + folderUuid
							, folderNamePath	: fIdx ? "/" + fArr.slice(0, fIdx).join("/") + "/" + folder : "/" + folder
							, folderName		: folder
							, UUID				: folderUuid
							, parentUUID		: fIdx ? folderUuidPath.split("/").pop() : param.targetFolder ? param.targetFolder : "/"
							, boxUUID			: param.boxUUID
						};
						
						acc.dir = acc.dir.concat(folderInfo);
					}
				});
				
				var fileUUID = uuidv4();
				var folderPath = replaceUuid(acc.dir, folderPathArr);
				var fileInfo = {
					  filePath		: folderPath + "/" + fileUUID
					, folderUUID	: folderPath.split("/").pop()
					, UUID			: fileUUID
					, fileName		: cur.name
					, boxUUID		: param.boxUUID
					, size			: cur.size
				};
				
				acc.info = acc.info.concat(fileInfo);
				acc.fileSize += cur.size;
				
				return acc;
			}, { dir: [], info: [], fileSize: 0 });
			
			var $row = createRowTemplate();
			$(".dvFileItemCell:eq(1)", $row).text(uploadObj.dir[0].folderName + "(" + convertFileSize( uploadObj.fileSize ) + ")");
			$row.data('files', $files).data('directories', uploadObj.dir ).data('fileInfos', uploadObj.info ).data('showList', {'name': uploadObj.dir[0].folderName, 'size': uploadObj.fileSize});
			$("#fileBox .confile").append( $row );
			$("#fileBox .confile > div").length && $("#fileBox .dragFileBox").hide();
		}
		
		var replaceUuid = function(folderInfoList, pathArr){
			var path = pathArr.join("/")
			var selObj = folderInfoList.find(function(info){
				return info.folderName === pathArr[(pathArr.length - 1)]
						&& info.folderNamePath === "/" + path;
			});
			
			return selObj.folderPath;
		}
		
		var registFileClick = function(){
			var uploadData = {
				"files": []
			};
			var obj = {
				  fileInfos		: []
				, directories	: []
				, showList		: []
			}
			
			$("#fileBox .dvFileItem").map(function(idx, item){
			    var $obj = $(item);
			    $obj.data('directories')	&& ( obj.directories		= obj.directories.concat($obj.data('directories')) );
			    $obj.data('fileInfos') 		&& ( obj.fileInfos 			= obj.fileInfos.concat($obj.data('fileInfos')) );
			    $obj.data('showList') 		&& ( obj.showList 			= obj.showList.concat($obj.data('showList')) );
			    $obj.data('files').map(function(item){ uploadData["files"].push(item) });
			});
			
			uploadData["fileMap"] = JSON.stringify(obj);
			
			// 파일명, 확장자 체크
			parent.coviWebHard.getExtConfig().done(function(data){
				var bCheck = true;
				var fileDisenableName = Common.getBaseConfig("DisenableFileName").split("|").filter(function(fileName){
					return fileName ? true : false;
				}, []);
				var config = data.ext ? data.ext.split(",").map(function(ext){
					return ext.trim();
				}, []) : "";
				
				$(obj.fileInfos).each(function(idx, item){
					var fArr = item.fileName.split(".");
					var ext = fArr.pop();
					var fileName = fArr.join(".");
					
					bCheck = !(config.includes(ext) || fileDisenableName.includes(fileName));
					
					if(!bCheck) return false;
				});
				
				if(bCheck){
					// 도메인 별 최대 업로드 용량 체크
					parent.coviWebHard.getDomainMaxUploadSize().done(function(data){
						if (data.status === "SUCCESS") {
							var totalSize = 0;
							var maxUploadSize = data.maxUploadSize * 1024 * 1024;
							
							uploadData.files.forEach(function(file){
								totalSize += file.size;
							});
							
							if (totalSize <= maxUploadSize) {
								parent.coviWebHard.uploadFile(uploadData);
								
								Common.Close();
							} else {
								Common.Warning(String.format("{0}<br>{1}: {2}", Common.getDic("msg_fileUploadMax"), Common.getDic("lbl_maxUploadSize"), convertFileSize(maxUploadSize)));
								// 업로드 하려는 파일이 최대 업로드 크기보다 큽니다.<br/>파일의 크기를 줄인 후 다시 시도해 주십시오.<br>최대 업로드 크기
							}
						}
					});
				}else{
					Common.Warning(String.format("{0}<br>=> {1}", Common.getDic("msg_ExistLimitedExtensionFile"))); // 업로드가 제한된 확장자의 파일이 있습니다.
				}
			});
		}		
		
		var onDrop = function(){
			event.stopPropagation();
			event.preventDefault();
			
			// directory upload는 firefox, chrome, edge만 지원. edge browser는 agnet에 chrome 포함 .
			if(_chrome || _firefox){
				var items = event.dataTransfer.items;
				
				for(var i = 0; i < items.length; i++){
					var item = items[i].webkitGetAsEntry();
					
					if(item){
						if(item.isDirectory){ // folder drag
							scanDir(item).then(function(rtn){
								var dirFullPath, dirName;
								var folderPathUUIDMap = {};
								var folderSize = 0;
								var filter = function(files){
									return files.reduce(function(acc, cur, idx, arr){
										if( Array.isArray(cur) ){
											var obj = filter( cur );
											acc.files = acc.files.concat( obj.files );
											acc.directories = acc.directories.concat( obj.directories );
											acc.fileInfos = acc.fileInfos.concat( obj.fileInfos );
											return acc;
										}else{
											if( cur.isDirectory ){ // add directories
												var _pathArr = cur.fullPath.split("/");
												var _folderNm = _pathArr.pop();
												
												folderPathUUIDMap[cur.fullPath] = ( _pathArr.length === 1 && "/"+uuidv4() ) || (folderPathUUIDMap[cur.fullPath] || folderPathUUIDMap[_pathArr.join('/')]+"/"+uuidv4());
												dirFullPath = cur.fullPath;
												
												var _uuidPathArr = folderPathUUIDMap[cur.fullPath].split('/');
												var _uuid = _uuidPathArr.pop();
												dirName = _uuidPathArr.pop();
												
												acc.directories = acc.directories.concat({
													folderPath		:	folderPathUUIDMap[cur.fullPath]
													,folderNamePath	:	dirFullPath
													,folderName 	: 	cur.name
													,UUID			: 	_uuid
													,parentUUID		:	dirName ? dirName : (param.targetFolder ? param.targetFolder : "/")
													,boxUUID		:	param.boxUUID
												});
											}else{ // add files, fileinfo
												var fileUUID = uuidv4();
												folderSize += cur.size;
												acc.fileInfos = acc.fileInfos.concat({
													filePath 	: 	folderPathUUIDMap[dirFullPath] + "/" + fileUUID
													,folderUUID	:	folderPathUUIDMap[dirFullPath].split('/').pop()
													,UUID		:	fileUUID
													,fileName 	: 	cur.name
													,boxUUID	:	param.boxUUID
													,size		:	cur.size
												});
												acc.files = acc.files.concat(cur);
											}
											return acc;
										}
									}, { files : [], directories : [], fileInfos : [] })
								}
								var $row = createRowTemplate();
								var rtn = filter(rtn);
								
								$(".dvFileItemCell:eq(1)", $row).text( rtn.directories[0].folderName+"("+ convertFileSize( folderSize ) +")" );
								$row.data('files', rtn.files )
									.data('directories', rtn.directories )
									.data('fileInfos', rtn.fileInfos )
									.data('showList', {'name': rtn.directories[0].folderName, 'size': folderSize} )
									.appendTo("#fileBox .confile");
								$("#fileBox .confile > div").length && $("#fileBox .dragFileBox").hide();
							});
						}else{ // file drag
							item.file(function(file){
								var fName = file.name;
								var sExtension = fName.split('.').pop();
								var $row = createRowTemplate(sExtension);
								var fileUUID = uuidv4();
								
								$(".dvFileItemCell:eq(1)", $row).text(file.name+"("+ convertFileSize( file.size ) +")");
								
								$row.data('files', [
									file
								]).data('fileInfos', [{
									filePath 	: 	"/" + fileUUID
									,folderUUID	:	param.targetFolder || ""
									,UUID		:	fileUUID
									,fileName 	: 	fName
									,boxUUID	:	param.boxUUID
									,size		:	file.size
								}])
								.data('showList', {'name': file.name, 'size': file.size} ).appendTo("#fileBox .confile");
								$("#fileBox .confile > div").length && $("#fileBox .dragFileBox").hide();
							});
						}
					}
				}//for end
			}else{ // ie etc...
				var fileList = event.dataTransfer.files;
				
				if(fileList.length === 0){
					Common.Warning("<spring:message code='Cache.msg_notSupportFolderUpload'/>"); // 사용중인 브라우저는 폴더 업로드를 지원하지 않습니다.
					return false;
				}
				
				$(fileList).each(function(idx, file){
					var fName = file.name;
					var sExtension = fName.split('.').pop();
					var $row = createRowTemplate(sExtension);
					var fileUUID = uuidv4();
					
					$(".dvFileItemCell:eq(1)", $row).text(file.name+"("+ convertFileSize( file.size ) +")");
					
					$row.data('files', [
						file
					]).data('fileInfos', [{
						filePath 	: 	"/" + fileUUID
						,folderUUID	:	param.targetFolder || ""
						,UUID		:	fileUUID
						,fileName 	: 	fName
						,boxUUID	:	param.boxUUID
						,size		:	file.size
					}])
					.data('showList', {'name': file.name, 'size': file.size} ).appendTo("#fileBox .confile");
					$("#fileBox .confile > div").length && $("#fileBox .dragFileBox").hide();
				});
			}
		}
		
		var addEvent = function(){
			//폴더추가
			$("#folderUploadBtn").on('click', addFolderClick);
			$("#uploadFolders").on('change',folderOnChange);
			
			//파일추가
			$("#fileUploadBtn").on('click', addFileClick);
			$("#uploadFiles").on('change', fileOnChange);
			
			//등록
			$("#uploadBtn").on('click', registFileClick);
			
			$(document).on({
				"drop"			:	onDrop
				,"dragover"		:	function(event) { event.preventDefault(); }
				,"dragenter"	:	function(event) { event.preventDefault(); }
			},"#fileBox");
			
			//fileBox 내부 이벤트
			$("#fileBox .addData").on('click', function(){
				event.target.className === "fa fa-times fa-lg" && $(event.target).parents('.dvFileItem').remove() && !$("#fileBox .confile > div").length && $("#fileBox .dragFileBox").show();
			});
		}
		
		var init = function(){
			if(_chrome || _firefox) $("#folderUploadBtn").show();
			
			addEvent();
		};
		
		init();
		
	})({
		boxUUID : "${boxUUID}"
		,targetFolder : "${targetFolder}"
	})	
</script>