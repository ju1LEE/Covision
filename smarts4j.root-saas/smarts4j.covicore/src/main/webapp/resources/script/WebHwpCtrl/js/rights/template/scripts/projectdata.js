// Publish project specific data
(function() {
rh = window.rh;
model = rh.model;

rh.consts('DEFAULT_TOPIC', encodeURI("#introduction/hwpcontrol(intro).htm".substring(1)));
rh.consts('START_FILEPATH', encodeURI('index.htm'));
rh.consts('HELP_ID', '6EB55DA2-3A11-4571-9098-6760A121BAF7' || 'preview');
rh.consts('LNG_STOP_WORDS', ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "after", "all", "along", "already", "also", "am", "among", "an", "and", "another", "any", "are", "at", "be", "because", "been", "between", "but", "by", "can", "do", "does", "doesn", "done", "each", "either", "for", "from", "get", "has", "have", "here", "how", "i", "if", "in", "into", "is", "isn", "it", "like", "may", "maybe", "more", "must", "need", "non", "not", "of", "ok", "okay", "on", "or", "other", "rather", "re", "s", "same", "see", "so", "some", "such", "t", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "those", "to", "too", "unless", "use", "used", "using", "ve", "want", "was", "way", "were", "what", "when", "when", "whenever", "where", "whether", "which", "will", "with", "within", "without", "yet", "you", "your"]);
rh.consts('LNG_SUBSTR_SEARCH', 0);

model.publish(rh.consts('KEY_DIR'), "ltr");
model.publish(rh.consts('KEY_LNG'), {"SearchResultsPerScreen":"페이지당 검색 결과","Reset":"재설정","SyncToc":"SyncToc","HomeButton":"홈","WebSearchButton":"WebSearch","GlossaryFilterTerms":"용어 찾기","HighlightSearchResults":"검색 결과 강조 표시","ApplyTip":"적용","WebSearch":"WebSearch","Show":"표시","ShowAll":"모두 표시","EnableAndSearch":"모든 검색어를 포함하는 결과를 표시합니다","Next":">>","PreviousLabel":"이전","NoScriptErrorMsg":"이 페이지를 보려면 JavaScript 지원을 활성화해야 합니다.","Print":"인쇄","Contents":"목차","Search":"검색","Hide":"숨기기","Canceled":"취소됨","ShowHide":"표시/숨기기","Loading":"로드하는 중...","EndOfResults":"검색 결과의 마지막입니다.","Logo":"로고","ContentFilterChanged":"내용 필터가 변경되었습니다. 다시 검색하십시오.","SidebarToggleTip":"펼치기/접기","Logo/Author":"제공:","JS_alert_LoadXmlFailed":"오류: xml 파일 로드에 실패했습니다.","Searching":"검색 중...","SearchTitle":"검색","Disabled Next":">>","JS_alert_InitDatabaseFailed":"오류: 데이터베이스 초기화에 실패했습니다.","Cancel":"취소","UnknownError":"알 수 없는 오류","ResultsFoundText":"%2에 대한 %1개의 결과를 찾았습니다","Index":"색인","Seperate":"|","SearchPageTitle":"검색 결과","TopicsNotFound":"항목을 찾을 수 없습니다.","Glossary":"용어집","Filter":"필터","NextLabel":"다음","TableOfContents":"목차","HideAll":"모두 숨기기","Disabled Prev":"<<","SearchOptions":"검색 옵션","Back":"뒤로","Prev":"<<","OpenLinkInNewTab":"새 탭에서 열기","JS_alert_InvalidExpression_1":"입력한 단어가 잘못된 표현입니다.","IndexFilterKewords":"키워드 찾기","IeCompatibilityErrorMsg":"이 페이지는 Internet Explorer 8 또는 이전 버전에서 볼 수 없습니다.","NavTip":"메뉴","ToTopTip":"맨 위로 가기"});
})();