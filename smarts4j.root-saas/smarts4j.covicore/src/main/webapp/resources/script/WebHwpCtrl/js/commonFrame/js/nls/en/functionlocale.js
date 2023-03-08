/**
 * @author jhmoon
 */
define({
    LocaleName : "en",
    /*
     function list
     type = basic : 0, standard : 1, extend : 2, auto : 3
     category = Date : 0, Database : 1, Engineering : 2, Financial : 3, Information : 4, logical : 5, Lookup & Find : 6, Math & Trig : 7, Statistical : 8, text : 9, cube : 10, Add-in and Automation : -1
     type, category, order(if type is extend then default extended function name), name, min param count, max param count, support, [visible, [block start, block count]]
     only localized function list should be existed except default(EN)

     max param count
     -1 : 최대 255개 까지 가질 수 있다는 것이고, (처리 가능한 최대 인자 갯수)
     -2 : 최대 254개 까지 가질 수 있다는 의미입니다. (처리 가능한 최대 인자 갯수  - 1)
     참고로, 254(함수 최대 인자 갯수 -1) 개의 인자를 max param count로 가지는 경우는 다음과 같습니다.
     현재 함수의 최대 인자는 255개 까지인데, 몇몇 함수들은 항상 2개의 인자가 쌍으로 존재해야 합니다. (SUMIFS, COUNTIFS, AVERAGEIFS)
     함수의 총 인자 객수는 255보다 작아야 하므로, 그 쌍의 갯수는 최대 127개로 한정됩니다.
     */
    functionInfoList: [
        "0|8|0|COUNT|1|-1|1|",
        "0|5|1|IF|2|3|1|",
        "1|4|2|ISNA|1|1|1|",
        "1|4|3|ISERROR|1|1|1|",
        "0|7|4|SUM|1|-1|1|",
        "0|8|5|AVERAGE|1|-1|1|",
        "0|8|6|MIN|1|-1|1|",
        "0|8|7|MAX|1|-1|1|",
        "1|6|8|ROW|0|1|1|",
        "1|6|9|COLUMN|0|1|1|",
        "1|4|10|NA|0|0|1|",
        "1|3|11|NPV|2|-1|1|1|1|1",
        "0|8|12|STDEV|1|-1|1|",
        "1|9|13|DOLLAR|1|2|1|",
        "1|9|14|FIXED|1|3|1|",
        "1|7|15|SIN|1|1|1",
        "1|7|16|COS|1|1|1",
        "1|7|17|TAN|1|1|1",
        "1|7|18|ATAN|1|1|1",
        "1|7|19|PI|0|0|1",
        "0|7|20|SQRT|1|1|1",
        "0|7|21|EXP|1|1|1",
        "0|7|22|LN|1|1|1",
        "0|7|23|LOG10|1|1|1",
        "0|7|24|ABS|1|1|1",
        "1|7|25|INT|1|1|1",
        "1|7|26|SIGN|1|1|1",
        "0|7|27|ROUND|2|2|1",
        "0|6|28|LOOKUP|2|3|1",
        "0|6|29|INDEX|2|4|1",
        "1|9|30|REPT|2|2|1",
        "0|9|31|MID|3|3|1",
        "0|9|32|LEN|1|1|1",
        "1|9|33|VALUE|1|1|1",
        "1|5|34|TRUE|0|0|1",
        "1|5|35|FALSE|0|0|1",
        "0|5|36|AND|1|-1|1",
        "0|5|37|OR|1|-1|1",
        "0|5|38|NOT|1|1|1",
        "0|7|39|MOD|2|2|1",
        "1|1|40|DCOUNT|3|3|1",
        "1|1|41|DSUM|3|3|1",
        "1|1|42|DAVERAGE|3|3|1",
        "1|1|43|DMIN|3|3|1",
        "1|1|44|DMAX|3|3|1",
        "1|1|45|DSTDEV|3|3|1",
        "0|8|46|VAR|1|-1|1",
        "1|1|47|DVAR|3|3|1|",
        "1|9|48|TEXT|2|2|1|",
        "1|8|49|LINEST|1|4|1|",
        "0|8|50|TREND|1|4|1|",
        "1|8|51|LOGEST|1|4|1|",
        "1|8|52|GROWTH|1|4|1|",
        "-1|-1|53|GOTO|1|1|0|0",
        "-1|-1|54|HALT|0|1|0|0",
        "0|3|56|PV|3|5|1|",
        "0|3|57|FV|3|5|1|",
        "0|3|58|NPER|3|5|1|",
        "0|3|59|PMT|3|5|1|",
        "0|3|60|RATE|3|6|1|",
        "1|3|61|MIRR|3|3|1|",
        "1|3|62|IRR|1|2|1|",
        "1|7|63|RAND|0|0|1|",
        "0|6|64|MATCH|2|3|1|",
        "0|0|65|DATE|3|3|1|",
        "1|0|66|TIME|3|3|1|",
        "1|0|67|DAY|1|1|1|",
        "0|0|68|MONTH|1|1|1|",
        "0|0|69|YEAR|1|1|1|",
        "0|0|70|WEEKDAY|1|2|1|",
        "1|0|71|HOUR|1|1|1|",
        "1|0|72|MINUTE|1|1|1|",
        "1|0|73|SECOND|1|1|1|",
        "0|0|74|NOW|0|0|1|",
        "1|6|75|AREAS|1|1|1|",
        "1|6|76|ROWS|1|1|1|",
        "1|6|77|COLUMNS|1|1|1|",
        "0|6|78|OFFSET|3|5|1|",
        "-1|-1|79|ABSREF|2|2|0|0",
        "-1|-1|80|RELREF|2|2|0|0",
        "-1|-1|81|ARGUMENT|1|2|0|0",
        "1|9|82|SEARCH|2|3|1|",
        "1|6|83|TRANSPOSE|1|1|1|",
        "-1|-1|84|ERROR|1|2|0|0",
        "-1|-1|85|STEP|0|0|0|0",
        "1|4|86|TYPE|1|1|1|",
        "-1|-1|87|ECHO|0|1|0|0",
        "-1|-1|88|SET.NAME|1|2|0|0",
        "-1|-1|89|CALLER|0|0|0|0",
        "-1|-1|90|DEREF|1|1|0|0",
        "-1|-1|91|WINDOWS|0|2|0|0",
        "-1|-1|92|SERIES|2|4|0|0",
        "-1|-1|93|DOCUMENTS|0|2|0|0",
        "-1|-1|94|ACTIVECELL|0|0|0|0",
        "-1|-1|95|SELECTION|0|0|0|0",
        "-1|-1|96|RESULT|0|1|0|0",
        "1|7|97|ATAN2|2|2|1|",
        "1|7|98|ASIN|1|1|1|",
        "1|7|99|ACOS|1|1|1|",
        "1|6|100|CHOOSE|2|-1|1|1|1|1",
        "0|6|101|HLOOKUP|3|4|1|",
        "0|6|102|VLOOKUP|3|4|1|",
        "-1|-1|103|LINKS|0|2|0|0",
        "-1|-1|104|INPUT|1|7|0|0",
        "1|4|105|ISREF|1|1|1|",
        "-1|-1|106|GET.FORMULA|1|1|0|0",
        "-1|-1|107|GET.NAME|1|2|0|0",
        "-1|-1|108|SET.VALUE|1|2|0|0",
        "1|7|109|LOG|1|2|1|",
        "-1|-1|110|EXEC|0|2|0|0",
        "0|9|111|CHAR|1|1|1|",
        "1|9|112|LOWER|1|1|1|",
        "1|9|113|UPPER|1|1|1|",
        "1|9|114|PROPER|1|1|1|",
        "1|9|115|LEFT|1|2|1|",
        "1|9|116|RIGHT|1|2|1|",
        "1|9|117|EXACT|2|2|1|",
        "0|9|118|TRIM|1|1|1|",
        "1|9|119|REPLACE|4|4|1|",
        "1|9|120|SUBSTITUTE|3|4|1|",
        "1|9|121|CODE|1|1|1|",
        "-1|-1|122|NAMES|0|3|0|0",
        "-1|-1|123|DIRECTORY|1|1|0|0",
        "1|9|124|FIND|2|3|1|",
        "1|4|125|CELL|1|2|1|",
        "1|4|126|ISERR|1|1|1|",
        "1|4|127|ISTEXT|1|1|1|",
        "1|4|128|ISNUMBER|1|1|1|",
        "1|4|129|ISBLANK|1|1|1|",
        "1|9|130|T|1|1|1|",
        "1|4|131|N|1|1|1|",
        "-1|-1|132|FOPEN|1|2|0|0",
        "-1|-1|133|FCLOSE|1|1|0|0",
        "-1|-1|134|FSIZE|1|1|0|0",
        "-1|-1|135|FREADLN|1|1|0|0",
        "-1|-1|136|FREAD|2|2|0|0",
        "-1|-1|137|FWRITELN|2|2|0|0",
        "-1|-1|138|FWRITE|2|2|0|0",
        "-1|-1|139|FPOS|1|2|0|0",
        "1|0|140|DATEVALUE|1|1|1|",
        "1|0|141|TIMEVALUE|1|1|1|",
        "1|3|142|SLN|3|3|1|",
        "1|3|143|SYD|4|4|1|",
        "1|3|144|DDB|4|5|1|",
        "-1|-1|145|GET.DEF|1|3|0|0",
        "-1|-1|146|REFTEXT|1|2|0|0",
        "-1|-1|147|TEXTREF|1|2|0|0",
        "1|6|148|INDIRECT|1|2|1|",
        "-1|-1|149|REGISTER|1|-1|0|0",
        "-1|-1|150|CALL|3|-1|0|0",
        "-1|-1|151|ADD.BAR|0|1|0|0",
        "-1|-1|152|ADD.MENU|2|4|0|0",
        "-1|-1|153|ADD.COMMAND|3|5|0|0",
        "-1|-1|154|ENABLE.COMMAND|4|5|0|0",
        "-1|-1|155|CHECK.COMMAND|4|5|0|0",
        "-1|-1|156|RENAME.COMMAND|4|5|0|0",
        "-1|-1|157|SHOW.BAR|0|1|0|0",
        "-1|-1|158|DELETE.MENU|2|3|0|0",
        "-1|-1|159|DELETE.COMMAND|3|4|0|0",
        "-1|-1|160|GET.CHART.ITEM|1|3|0|0",
        "-1|-1|161|DIALOG.BOX|1|1|0|0",
        "1|9|162|CLEAN|1|1|1|",
        "1|7|163|MDETERM|1|1|1|",
        "1|7|164|MINVERSE|1|1|1|",
        "1|7|165|MMULT|2|2|1|",
        "-1|-1|166|FILES|0|1|0|0",
        "1|3|167|IPMT|4|6|1|",
        "1|3|168|PPMT|4|6|1|",
        "0|8|169|COUNTA|1|-1|1|",
        "-1|-1|170|CANCELKEY|1|2|0|0",
        "-1|-1|175|INITIATE|2|2|0|0",
        "-1|-1|176|REQUEST|2|2|0|0",
        "-1|-1|177|POKE|3|3|0|0",
        "-1|-1|178|EXECUTE|2|2|0|0",
        "-1|-1|179|TERMINATE|1|1|0|0",
        "-1|-1|180|RESTART|0|1|0|0",
        "-1|-1|181|HELP|0|1|0|0",
        "-1|-1|182|GET.BAR|0|0|0|0",
        "1|7|183|PRODUCT|1|-1|1|",
        "1|7|184|FACT|1|1|1|",
        "-1|-1|185|GET.CELL|1|2|0|0",
        "-1|-1|186|GET.WORKSPACE|1|1|0|0",
        "-1|-1|187|GET.WINDOW|1|2|0|0",
        "-1|-1|188|GET.DOCUMENT|1|2|0|0",
        "1|1|189|DPRODUCT|3|3|1|",
        "1|4|190|ISNONTEXT|1|1|1|",
        "-1|-1|191|GET.NOTE|0|3|0|0",
        "-1|-1|192|NOTE|0|4|0|0",
        "1|8|193|STDEVP|1|-1|1|",
        "1|8|194|VARP|1|-1|1|",
        "1|1|195|DSTDEVP|3|3|1|",
        "1|1|196|DVARP|3|3|1|",
        "0|7|197|TRUNC|1|2|1|",
        "1|4|198|ISLOGICAL|1|1|1|",
        "1|1|199|DCOUNTA|3|3|1|",
        "-1|-1|200|DELETE.BAR|1|1|0|0",
        "-1|-1|201|UNREGISTER|1|1|0|0",
        "1|9|204|USDOLLAR|1|2|1|",
//        "1|9|204|DOLLAR|1|2|1|",
        "1|9|205|FINDB|2|3|0|",
        "1|9|206|SEARCHB|2|3|0|",
        "1|9|207|REPLACEB|4|4|0|",
        "1|9|208|LEFTB|1|2|0|",
        "1|9|209|RIGHTB|1|2|0|",
        "1|9|210|MIDB|3|3|0|",
        "1|9|211|LENB|1|1|0|",
        "1|7|212|ROUNDUP|2|2|1|",
        "1|7|213|ROUNDDOWN|2|2|1|",
        "1|9|214|ASC|1|1|0",
        "1|9|215|DBCS|1|1|0|0",
        "0|8|216|RANK|2|3|1",
        "1|6|219|ADDRESS|2|5|1",
        "0|0|220|DAYS360|2|3|1",
        "0|0|221|TODAY|0|0|1",
        "1|3|222|VDB|5|7|1",
        "0|8|227|MEDIAN|1|-1|1",
        "1|7|228|SUMPRODUCT|1|-1|1",
        "1|7|229|SINH|1|1|1",
        "1|7|230|COSH|1|1|1",
        "1|7|231|TANH|1|1|1",
        "1|7|232|ASINH|1|1|1",
        "1|7|233|ACOSH|1|1|1",
        "1|7|234|ATANH|1|1|1",
        "1|1|235|DGET|3|3|1",
        "-1|-1|236|CREATE.OBJECT|3|10|0|0",
        "-1|-1|237|VOLATILE|0|1|0|0",
        "-1|-1|238|LAST.ERROR|0|0|0|0",
        "-1|-1|239|CUSTOM.UNDO|1|2|0|0",
        "-1|-1|240|CUSTOM.REPEAT|0|3|0|0",
        "-1|-1|241|FORMULA.CONVERT|2|5|0|0",
        "-1|-1|242|GET.LINK.INFO|2|4|0|0",
        "-1|-1|243|TEXT.BOX|1|4|0|0",
        "1|4|244|INFO|1|1|1|",
        "-1|-1|245|GROUP|0|0|0|0",
        "-1|-1|246|GET.OBJECT|1|5|0|0",
        "0|3|247|DB|4|5|1|",
        "-1|-1|248|PAUSE|0|1|0|0",
        "-1|-1|251|RESUME|0|1|0|0",
        "0|8|252|FREQUENCY|2|2|1|",
        "-1|-1|253|ADD.TOOLBAR|0|2|0|0",
        "-1|-1|254|DELETE.TOOLBAR|1|1|0|0",
        "-1|-1|256|RESET.TOOLBAR|0|1|0|0",
        "-1|-1|257|EVALUATE|1|1|0|0",
        "-1|-1|258|GET.TOOLBAR|1|2|0|0",
        "-1|-1|259|GET.TOOL|3|3|0|0",
        "-1|-1|260|SPELLING.CHECK|1|3|0|0",
        "1|4|261|ERROR.TYPE|1|1|1|",
        "-1|-1|262|APP.TITLE|0|1|0|0",
        "-1|-1|263|WINDOW.TITLE|0|1|0|0",
        "-1|-1|264|SAVE.TOOLBAR|0|2|0|0",
        "-1|-1|265|SAVE.TOOLBAR|0|0|0|0",
        "-1|-1|266|PRESS.TOOL|2|3|0|0",
        "-1|-1|267|REGISTERID|2|3|0|0",
        "-1|-1|268|GET.WORKBOOK|1|2|0|0",
        "1|8|269|AVEDEV|1|-1|1|",
        "1|8|270|BETADIST|3|5|1|",
        "1|8|271|GAMMALN|1|1|1",
        "1|8|272|BETAINV|3|5|1",
        "1|8|273|BINOMDIST|4|4|1",
        "1|8|274|CHIDIST|2|2|1",
        "1|8|275|CHIINV|2|2|1",
        "1|7|276|COMBIN|2|2|1",
        "1|8|277|CONFIDENCE|3|3|1",
        "1|8|278|CRITBINOM|3|3|1",
        "1|7|279|EVEN|1|1|1",
        "1|8|280|EXPONDIST|3|3|1",
        "1|8|281|FDIST|3|3|1",
        "1|8|282|FINV|3|3|1",
        "1|8|283|FISHER|1|1|1",
        "1|8|284|FISHERINV|1|1|1",
        "0|7|285|FLOOR|2|2|1",
        "1|8|286|GAMMADIST|4|4|1",
        "1|8|287|GAMMAINV|3|3|1",
        "1|7|288|CEILING|2|2|1",
        "1|8|289|HYPGEOMDIST|4|4|1",
        "1|8|290|LOGNORMDIST|3|3|1",
        "1|8|291|LOGINV|3|3|1",
        "1|8|292|NEGBINOMDIST|3|3|1",
        "1|8|293|NORMDIST|4|4|1",
        "1|8|294|NORMSDIST|1|1|1",
        "1|8|295|NORMINV|3|3|1",
        "1|8|296|NORMSINV|1|1|1",
        "1|8|297|STANDARDIZE|3|3|1",
        "1|7|298|ODD|1|1|1",
        "1|8|299|PERMUT|2|2|1",
        "1|8|300|POISSON|3|3|1",
        "1|8|301|TDIST|3|3|1",
        "1|8|302|WEIBULL|4|4|1",
        "1|7|303|SUMXMY2|2|2|1",
        "1|7|304|SUMX2MY2|2|2|1",
        "1|7|305|SUMX2PY2|2|2|1",
        "1|8|306|CHITEST|2|2|1",
        "1|8|307|CORREL|2|2|1",
        "1|8|308|COVAR|2|2|1",
        "1|8|309|FORECAST|3|3|1",
        "1|8|310|FTEST|2|2|1",
        "1|8|311|INTERCEPT|2|2|1",
        "1|8|312|PEARSON|2|2|1",
        "1|8|313|RSQ|2|2|1",
        "1|8|314|STEYX|2|2|1",
        "1|8|315|SLOPE|2|2|1",
        "1|8|316|TTEST|4|4|1",
        "1|8|317|PROB|3|4|1",
        "1|8|318|DEVSQ|1|-1|1",
        "1|8|319|GEOMEAN|1|-1|1|",
        "1|8|320|HARMEAN|1|-1|1|",
        "1|7|321|SUMSQ|1|-1|1|",
        "1|8|322|KURT|1|-1|1|",
        "1|8|323|SKEW|1|-1|1|",
        "1|8|324|ZTEST|2|3|1|",
        "1|8|325|LARGE|2|2|1|",
        "1|8|326|SMALL|2|2|1|",
        "1|8|327|QUARTILE|2|2|1|",
        "1|8|328|PERCENTILE|2|2|1|",
        "1|8|329|PERCENTRANK|2|3|1|",
        "1|8|330|MODE|1|-1|1|",
        "1|8|331|TRIMMEAN|2|2|1|",
        "1|8|332|TINV|2|2|1|",
        "-1|-1|334|MOVIECOMMAND|0|0|0|0",
        "-1|-1|335|GETMOVIE|0|0|0|0",
        "1|9|336|CONCATENATE|2|-1|1|",
        "0|7|337|POWER|2|2|1|",
        "-1|-1|338|PIVOT.ADD.DATA|0|9|0|0",
        "-1|-1|339|GET.PIVOT.TABLE|1|2|0|0",
        "-1|-1|340|GET.PIVOT.FIELD|0|3|0|0",
        "-1|-1|341|GET.PIVOT.ITEM|1|4|0|0",
        "1|7|342|RADIANS|1|1|1|",
        "1|7|343|DEGREES|1|1|1|",
        "0|7|344|SUBTOTAL|2|-1|1|1|1|1",
        "0|7|345|SUMIF|2|3|1|",
        "0|8|346|COUNTIF|2|2|1|",
        "1|8|347|COUNTBLANK|1|1|1|",
        "-1|-1|348|SCENARIO.GET|1|2|0|0",
        "-1|-1|349|OPTIONSLISTSGET|0|0|0|0",
        "1|3|350|ISPMT|4|4|1|",
        "1|5|351|DATEDIF|3|3|0|0",
        "1|9|352|DATESTRING|1|1|0|0",
        "1|9|353|NUMBERSTRING|2|2|1|0",
        "1|7|354|ROMAN|1|2|1|",
        "-1|-1|355|OPEN.DIALOG|0|4|0|0",
        "-1|-1|356|SAVE.DIALOG|0|5|0|0",
        "-1|-1|357|VIEWGET|1|2|0|0",
        "1|6|358|GETPIVOTDATA|2|-1|1|1|2|2",
        "1|6|359|HYPERLINK|1|2|0|",
        "1|9|360|PHONETIC|1|1|0|",
        "1|8|361|AVERAGEA|1|-1|1|",
        "1|8|362|MAXA|1|-1|1|",
        "1|8|363|MINA|1|-1|1|",
        "1|8|364|STDEVPA|1|-1|1|",
        "1|8|365|VARPA|1|-1|1|",
        "1|8|366|STDEVA|1|-1|1|",
        "1|8|367|VARA|1|-1|1|",
        "2|3|ACCRINT|ACCRINT|6|8|1",
        "2|3|ACCRINTM|ACCRINTM|3|5|1",
        "2|3|AMORDEGRC|AMORDEGRC|6|7|1",
        "2|3|AMORLINC|AMORLINC|6|7|1",
        "2|8|AVERAGEIF|AVERAGEIF|2|3|1",
        "2|8|AVERAGEIFS|AVERAGEIFS|3|-2|1|1|1|2",
        "2|9|BAHTTEXT|BAHTTEXT|1|1|0",
        "2|2|BESSELI|BESSELI|2|2|1",
        "2|2|BESSELJ|BESSELJ|2|2|1",
        "2|2|BESSELK|BESSELK|2|2|1",
        "2|2|BESSELY|BESSELY|2|2|1",
        "2|2|BIN2DEC|BIN2DEC|1|1|1",
        "2|2|BIN2HEX|BIN2HEX|1|2|1",
        "2|2|BIN2OCT|BIN2OCT|1|2|1",
        "2|2|COMPLEX|COMPLEX|2|3|1",
        "2|2|CONVERT|CONVERT|3|3|1",
        "2|8|COUNTIFS|COUNTIFS|2|-2|1|1|0|2",
        "2|3|COUPDAYBS|COUPDAYBS|3|4|1",
        "2|3|COUPDAYS|COUPDAYS|3|4|1",
        "2|3|COUPDAYSNC|COUPDAYSNC|3|4|1",
        "2|3|COUPNCD|COUPNCD|3|4|1",
        "2|3|COUPNUM|COUPNUM|3|4|1",
        "2|3|COUPPCD|COUPPCD|3|4|1",
        "2|10|CUBEKPIMEMBER|CUBEKPIMEMBER|3|4|0|0",
        "2|10|CUBEMEMBER|CUBEMEMBER|3|4|0|0",
        "2|10|CUBEMEMBERPROPERTY|CUBEMEMBERPROPERTY|3|3|0|0",
        "2|10|CUBERANKEDMEMBER|CUBERANKEDMEMBER|3|4|0|0",
        "2|10|CUBESET|CUBESET|2|5|0|0",
        "2|10|CUBESETCOUNT|CUBESETCOUNT|1|1|0|0",
        "2|10|CUBEVALUE|CUBEVALUE|2|-1|0|0",
        "2|3|CUMIPMT|CUMIPMT|6|6|1",
        "2|3|CUMPRINC|CUMPRINC|6|6|1",
        "2|2|DEC2BIN|DEC2BIN|1|2|1|",
        "2|2|DEC2HEX|DEC2HEX|1|2|1|",
        "2|2|DEC2OCT|DEC2OCT|1|2|1|",
        "2|2|DELTA|DELTA|1|2|1|",
        "2|3|DISC|DISC|4|5|1|",
        "2|3|DOLLARDE|DOLLARDE|2|2|1|",
        "2|3|DOLLARFR|DOLLARFR|2|2|1|",
        "2|3|DURATION|DURATION|5|6|1|",
        "2|0|EDATE|EDATE|2|2|1|",
        "2|3|EFFECT|EFFECT|2|2|1|",
        "2|0|EOMONTH|EOMONTH|2|2|1|",
        "2|2|ERF|ERF|1|2|1|",
        "2|2|ERFC|ERFC|1|1|1|",
        "-1|-1|EUROCONVERT|EUROCONVERT|3|5|0|0",
        "2|2|FACTDOUBLE|FACTDOUBLE|1|1|1|",
        "2|3|FVSCHEDULE|FVSCHEDULE|2|2|1|",
        "2|7|GCD|GCD|1|-1|1",
        "2|2|GESTEP|GESTEP|1|2|1",
        "2|2|HEX2BIN|HEX2BIN|1|2|1",
        "2|2|HEX2DEC|HEX2DEC|1|1|1",
        "2|2|HEX2OCT|HEX2OCT|1|2|1",
        "2|5|IFERROR|IFERROR|2|2|1|1",
        "2|2|IMABS|IMABS|1|1|1",
        "2|2|IMAGINARY|IMAGINARY|1|1|1",
        "2|2|IMARGUMENT|IMARGUMENT|1|1|1",
        "2|2|IMCONJUGATE|IMCONJUGATE|1|1|1",
        "2|2|IMCOS|IMCOS|1|1|1",
        "2|2|IMDIV|IMDIV|2|2|1",
        "2|2|IMEXP|IMEXP|1|1|1",
        "2|2|IMLN|IMLN|1|1|1",
        "2|2|IMLOG10|IMLOG10|1|1|1",
        "2|2|IMLOG2|IMLOG2|1|1|1",
        "2|2|IMPOWER|IMPOWER|2|2|1",
        "2|2|IMPRODUCT|IMPRODUCT|1|-1|1",
        "2|2|IMREAL|IMREAL|1|1|1",
        "2|2|IMSIN|IMSIN|1|1|1",
        "2|2|IMSQRT|IMSQRT|1|1|1",
        "2|2|IMSUB|IMSUB|2|2|1",
        "2|2|IMSUM|IMSUM|1|-1|1",
        "2|3|INTRATE|INTRATE|4|5|1",
        "2|4|ISEVEN|ISEVEN|1|1|1",
        "2|4|ISODD|ISODD|1|1|1",
        "2|7|LCM|LCM|1|-1|1",
        "2|3|MDURATION|MDURATION|5|6|1",
        "2|7|MROUND|MROUND|2|2|1",
        "2|7|MULTINOMIAL|MULTINOMIAL|1|-1|1",
        "2|0|NETWORKDAYS|NETWORKDAYS|2|3|1",
        "2|3|NOMINAL|NOMINAL|2|2|1",
        "2|2|OCT2BIN|OCT2BIN|1|2|1|",
        "2|2|OCT2DEC|OCT2DEC|1|1|1|",
        "2|2|OCT2HEX|OCT2HEX|1|2|1|",
        "2|3|ODDFPRICE|ODDFPRICE|8|9|1|",
        "2|3|ODDFYIELD|ODDFYIELD|8|9|0|",
        "2|3|ODDLPRICE|ODDLPRICE|7|8|0|",
        "2|3|ODDLYIELD|ODDLYIELD|7|8|0|",
        "2|3|PRICE|PRICE|6|7|1|",
        "2|3|PRICEDISC|PRICEDISC|4|5|1|",
        "2|3|PRICEMAT|PRICEMAT|5|6|1|",
        "2|7|QUOTIENT|QUOTIENT|2|2|1|",
        "2|7|RANDBETWEEN|RANDBETWEEN|2|2|1|",
        "2|3|RECEIVED|RECEIVED|4|5|1|",
        "-1|-1|REGISTER.ID|REGISTER.ID|2|3|0|0",
        "2|6|RTD|RTD|3|-1|0|0|2|1",
        "2|7|SERIESSUM|SERIESSUM|4|4|1|",
        "-1|-1|SQL.REQUEST|SQL.REQUEST|2|5|0|0",
        "2|7|SQRTPI|SQRTPI|1|1|1|",
        "2|7|SUMIFS|SUMIFS|3|-2|1|1|1|2",
        "2|3|TBILLEQ|TBILLEQ|3|3|1|",
        "2|3|TBILLPRICE|TBILLPRICE|3|3|1|",
        "2|3|TBILLYIELD|TBILLYIELD|3|3|1|",
        "2|0|WEEKNUM|WEEKNUM|1|2|1|",
        "2|0|WORKDAY|WORKDAY|2|3|1|",
        "2|3|XIRR|XIRR|2|3|1|",
        "2|3|XNPV|XNPV|3|3|1|",
        "2|0|YEARFRAC|YEARFRAC|2|3|1|",
        "2|3|YIELD|YIELD|6|7|1|",
        "2|3|YIELDDISC|YIELDDISC|4|5|1|",
        "2|3|YIELDMAT|YIELDMAT|5|6|1|"
    ],

    functionArgumentList: [
        "angle",
        "array",
        "date_text",
        "error_val",
        "inumber",
        "logical",
        "number",
        "probability",
        "text",
        "time_text",
        "type_text",
        "range",
        "reference",
        "serial_number",
        "set",
        "value",
        "x",
        "y",
        "z",
        "actual_range|expected_range",
        "array|k",
        "array|percent",
        "array|quart",
        "array1|array2",
        "array_x|array_y",
        "bottom|top",
        "connection|member_expression",
        "criteria_range|criteria",
        "data_array|bins_array",
        "decimal_dollar|fraction",
        "effect_rate|npery",
        "fractional_dollar|fraction",
        "function_num|ref",
        "index_num|value",
        "info_type|reference",
        "inumber1|inumber2",
        "inumber|number",
        "known_y's|known_x's",
        "lookup_value|array",
        "lower_limit|upper_limit",
        "link_location|friendly_name",
        "nominal_rate|npery",
        "numerator|denominator",
        "number|base",
        "number|divisor",
        "number|decimals",
        "number|form",
        "number|multiple",
        "number|number_chosen",
        "number|num_digits",
        "number|places",
        "number|power",
        "number|significance",
        "number|step",
        "number1|number2",
        "principal|schedule",
        "probability|deg_freedom",
        "range|criteria",
        "rate|value",
        "ref_text|a1",
        "start_date|months",
        "serial_number|return_type",
        "text|number_times",
        "text|num_bytes",
        "text|num_chars",
        "text1|text2",
        "value|format_text",
        "value|value_if_error",
        "values|guess",
        "x|n",
        "x|deg_freedom",
        "x_num|y_num",
        "year|month|day",
        "alpha|standard_dev|size",
        "array|row_num|column_num",
        "array|x|sigma",
        "array|x|significance",
        "average_range|criteria_range|criteria",
        "connection|member_expression|caption",
        "connection|member_expression|property",
        "cost|salvage|life",
        "cost|salvage|life|per",
        "database|field|criteria",
        "find_text|within_text|start_num",
        "hour|minute|second",
        "logical_test|value_if_true|value_if_false",
        "lookup_value|lookup_array|match_type",
        "lookup_value|lookup_vector|result_vector",
        "number|decimals|no_commas",
        "number|from_unit|to_unit",
        "number|ref|order",
        "number_f|number_s|probability_s",
        "probability|alpha|beta",
        "probability|deg_freedom1|deg_freedom2",
        "probability|mean|standard_dev",
        "progid|server|topic",
        "range|criteria|sum_range",
        "range|criteria|average_range",
        "rate|values|dates",
        "real_num|i_num|suffix",
        "settlement|maturity|discount",
        "settlement|maturity|pr",
        "start_date|days|holidays",
        "start_date|end_date|basis",
        "start_date|end_date|interval",
        "start_date|end_date|holidays",
        "start_date|end_date|method",
        "sum_range|criteria_range|criteria",
        "text|start_num|num_bytes",
        "text|start_num|num_chars",
        "trials|probability_s|alpha",
        "values|dates|guess",
        "values|finance_rate|reinvest_rate",
        "x|deg_freedom|tails",
        "x|deg_freedom1|deg_freedom2",
        "x|known_y's|known_x's",
        "x|lambda|cumulative",
        "x|mean|cumulative",
        "x|mean|standard_dev",
        "array1|array2|tails|type",
        "connection|kpi_name|kpi_property|caption",
        "connection|set_expression|rank|caption",
        "known_y's|known_x's|const|stats",
        "known_y's|known_x's|new_x's|const",
        "lookup_value|lookup_vector|result_vector|array",
        "lookup_value|table_array|col_index_num|range_lookup",
        "lookup_value|table_array|row_index_num|range_lookup",
        "number_s|trials|probability_s|cumulative",
        "old_text|start_num|num_bytes|new_text",
        "old_text|start_num|num_chars|new_text",
        "rate|per|nper|pv",
        "reference|row_num|column_num|area_num",
        "row_num|column_num|abs_num|a1|sheet_text",
        "sample_s|number_sample|population_s|number_pop",
        "settlement|maturity|frequency|basis",
        "text|old_text|new_text|instance_num",
        "x|alpha|beta|cumulative",
        "x|mean|standard_dev|cumulative",
        "x|n|m|coefficients",
        "x_range|prob_range|lower_limit|upper_limit",
        "array|row_num|column_num|reference|area_num",
        "connection|set_expression|caption|set_order|sort_by",
        "cost|salvage|life|period|month",
        "cost|salvage|life|period|factor",
        "issue|settlement|rate|par|basis",
        "probability|alpha|beta|a|b",
        "rate|nper|pmt|fv|type",
        "rate|nper|pmt|pv|type",
        "rate|nper|pv|fv|type",
        "rate|pmt|pv|fv|type",
        "reference|rows|cols|height|width",
        "settlement|maturity|coupon|yld|frequency|basis",
        "settlement|maturity|discount|redemption|basis",
        "settlement|maturity|investment|discount|basis",
        "settlement|maturity|investment|redemption|basis",
        "settlement|maturity|pr|redemption|basis",
        "x|alpha|beta|a|b",
        "cost|date_purchased|first_period|salvage|period|rate|basis",
        "nper|pmt|pv|fv|type|guess",
        "rate|nper|pv|start_period|end_period|type",
        "rate|per|nper|pv|fv|type",
        "settlement|maturity|issue|rate|pr|basis",
        "settlement|maturity|issue|rate|yld|basis",
        "cost|salvage|life|start_period|end_period|factor|no_switch",
        "settlement|maturity|rate|pr|redemption|frequency|basis",
        "settlement|maturity|rate|yld|redemption|frequency|basis",
        "issue|first_interest|settlement|rate|par|frequency|basis|calc_method",
        "settlement|maturity|issue|first_coupon|rate|pr|redemption|frequency|basis",
        "settlement|maturity|issue|first_coupon|rate|yld|redemption|frequency|basis",
        "settlement|maturity|last_interest|rate|pr|redemption|frequency|basis",
        "settlement|maturity|last_interest|rate|yld|redemption|frequency|basis",
        "data_field|pivot_table|field|item"
    ],

    //-------------------------------------------------------------
    //function name| def index| argument index1| argument index2
    //-------------------------------------------------------------
    functionArgumentInfoList: [
        "COUNT|5|16|",
        "IF|15|86|",
        "ISNA|25|16|",
        "ISERROR|25|16|",
        "SUM|4|7|",
        "AVERAGE|102|7|",
        "MIN|102|7|",
        "MAX|102|7|",
        "ROW|86|13|",
        "COLUMN|86|13|",
        "NA|3|0|",
        "NPV|37|59|",
        "STDEV|5|7|",
        "DOLLAR|6|46|",
        "FIXED|94|89|",
        "SIN|0|7|",
        "COS|0|7|",
        "TAN|0|7|",
        "ATAN|0|7|",
        "PI|3|0|",
        "SQRT|0|7|",
        "EXP|0|7|",
        "LN|0|7|",
        "LOG10|0|7|",
        "ABS|0|7|",
        "INT|0|7|",
        "SIGN|0|7|",
        "ROUND|1|50|",
        "LOOKUP|73|88|39",
        "INDEX|72|75|132",
        "REPT|24|63|",
        "MID|22|110|",
        "LEN|11|9|",
        "VALUE|11|9|",
        "TRUE|3|0|",
        "FALSE|3|0|",
        "AND|49|6|",
        "OR|49|6|",
        "NOT|16|6|",
        "MOD|1|45|",
        "DCOUNT|77|83|",
        "DSUM|77|83|",
        "DAVERAGE|77|83|",
        "DMIN|77|83|",
        "DMAX|77|83|",
        "DSTDEV|77|83|",
        "VAR|98|7|",
        "DVAR|77|83|",
        "TEXT|48|67|",
        "LINEST|69|123|",
        "TREND|68|124|",
        "LOGEST|69|123|",
        "GROWTH|96|124|",
        "HARMEAN|102|7|",
        "PV|28|147|",
        "FV|28|148|",
        "NPER|79|150|",
        "PMT|28|149|",
        "RATE|32|159|",
        "MIRR|71|113|",
        "IRR|70|69|",
        "RAND|3|0|",
        "MATCH|74|87|",
        "DATE|10|73|",
        "TIME|10|85|",
        "DAY|0|14|",
        "MONTH|0|14|",
        "YEAR|0|14|",
        "WEEKDAY|6|62|",
        "HOUR|0|14|",
        "MINUTE|0|14|",
        "SECOND|0|14|",
        "NOW|3|0|",
        "AREAS|25|13|",
        "ROWS|25|2|",
        "COLUMNS|25|2|",
        "OFFSET|75|151|",
        "SEARCH|19|84|",
        "TRANSPOSE|25|2|",
        "TYPE|25|16|",
        "ATAN2|1|72|",
        "ASIN|0|7|",
        "ACOS|0|7|",
        "CHOOSE|56|34|",
        "HLOOKUP|58|127|",
        "VLOOKUP|58|126|",
        "ISREF|25|16|",
        "LOG|6|44|",
        "CHAR|0|7|",
        "LOWER|11|9|",
        "UPPER|11|9|",
        "PROPER|11|9|",
        "LEFT|21|65|",
        "RIGHT|21|65|",
        "EXACT|18|66|",
        "TRIM|11|9|",
        "REPLACE|23|130|",
        "SUBSTITUTE|26|136|",
        "CODE|11|9|",
        "FIND|19|84|",
        "CELL|87|35|",
        "ISERR|25|16|",
        "ISTEXT|25|16|",
        "ISNUMBER|25|16|",
        "ISBLANK|25|16|",
        "T|25|16|",
        "N|25|16|",
        "DATEVALUE|11|3|",
        "TIMEVALUE|11|10|",
        "SLN|10|81|",
        "SYD|33|82|",
        "DDB|27|144|",
        "INDIRECT|93|60|",
        "CLEAN|11|9|",
        "MDETERM|50|2|",
        "MINVERSE|50|2|",
        "MMULT|8|24|",
        "IPMT|29|161|",
        "PPMT|29|161|",
        "COUNTA|5|16|",
        "PRODUCT|103|7|",
        "FACT|0|7|",
        "DPRODUCT|77|83|",
        "ISNONTEXT|25|16|",
        "STDEVP|98|7|",
        "VARP|98|7|",
        "DSTDEVP|77|83|",
        "DVARP|77|83|",
        "TRUNC|6|50|",
        "ISLOGICAL|25|16|",
        "DCOUNTA|77|83|",
        "DOLLAR|6|46|",
        "FINDB|-1|84|",
        "SEARCHB|-1|84|",
        "REPLACEB|-1|129|",
        "LEFTB|-1|64|",
        "RIGHTB|-1|64|",
        "MIDB|-1|109|",
        "LENB|-1|9|",
        "ROUNDUP|1|50|",
        "ROUNDDOWN|1|50|",
        "ASC|-1|9|",
        "JUNJA|-1|9|",
        "RANK|95|91|",
        "ADDRESS|92|133|",
        "DAYS360|12|107|",
        "TODAY|3|0|",
        "VDB|34|164|",
        "MEDIAN|5|7|",
        "SUMPRODUCT|7|2|",
        "SINH|0|7|",
        "COSH|0|7|",
        "TANH|0|7|",
        "ASINH|0|7|",
        "ACOSH|0|7|",
        "ATANH|0|7|",
        "DGET|77|83|",
        "INFO|11|11|",
        "DB|27|143|",
        "FREQUENCY|90|29|",
        "ERRORTYPE|25|4|",
        "AVEDEV|98|7|",
        "BETADIST|28|157|",
        "GAMMALN|0|17|",
        "BETAINV|28|146|",
        "BINOMDIST|35|128|",
        "CHIDIST|1|71|",
        "CHIINV|1|57|",
        "COMBIN|1|49|",
        "CONFIDENCE|10|74|",
        "CRITBINOM|10|111|",
        "EVEN|0|7|",
        "EXPONDIST|36|117|",
        "FDIST|10|115|",
        "FINV|-1|94|",
        "FISHER|0|17|",
        "FISHERINV|-1|18|",
        "FLOOR|1|53|",
        "GAMMADIST|35|137|",
        "GAMMAINV|-1|93|",
        "CEILING|1|53|",
        "HYPGEOMDIST|33|134|",
        "LOGNORMDIST|10|119|",
        "LOGINV|-1|95|",
        "NEGBINOMDIST|10|92|",
        "NORMDIST|35|138|",
        "NORMSDIST|25|19|",
        "NORMINV|-1|95|",
        "NORMSINV|-1|8|",
        "STANDARDIZE|10|119|",
        "ODD|0|7|",
        "PERMUT|1|49|",
        "POISSON|36|118|",
        "TDIST|10|114|",
        "WEIBULL|35|137|",
        "SUMXMY2|9|25|",
        "SUMX2MY2|9|25|",
        "SUMX2PY2|9|25|",
        "CHITEST|101|20|",
        "CORREL|2|24|",
        "COVAR|9|24|",
        "FORECAST|40|116|",
        "FTEST|2|24|",
        "INTERCEPT|9|38|",
        "PEARSON|9|24|",
        "RSQ|9|38|",
        "STEYX|9|38|",
        "SLOPE|9|38|",
        "TTEST|97|120|",
        "PROB|44|140|",
        "DEVSQ|102|7|",
        "GEOMEAN|98|7|",
        "HARMEAN|102|7|",
        "SUMSQ|103|7|",
        "KURT|98|7|",
        "SKEW|5|7|",
        "ZTEST|46|76|",
        "LARGE|42|21|",
        "SMALL|42|21|",
        "QUARTILE|42|23|",
        "PERCENTILE|88|21|",
        "PERCENTRANK|89|77|",
        "MODE|5|7|",
        "TRIMMEAN|42|22|",
        "TINV|-1|57|",
        "CONCATENATE|17|9|",
        "POWER|1|52|",
        "RADIANS|0|1|",
        "DEGREES|0|1|",
        "SUBTOTAL|82|33|",
        "SUMIF|51|97|",
        "COUNTIF|38|58|",
        "COUNTBLANK|39|12|",
        "ISPMT|33|131|",
        "ROMAN|6|47|",
        "GETPIVOTDATA|106|172|",
        "HYPERLINK|-1|41|",
        "PHONETIC|-1|13|",
        "AVERAGEA|5|16|",
        "MAXA|5|16|",
        "MINA|5|16|",
        "STDEVPA|5|16|",
        "VARPA|5|16|",
        "STDEVA|5|16|",
        "VARA|5|16|",
        "ACCRINT|78|167|",
        "ACCRINTM|85|145|",
        "AMORDEGRC|62|158|",
        "AMORLINC|62|158|",
        "AVERAGEIF|-1|98|",
        "AVERAGEIFS|-1|78|",
        "BAHTTEXT|-1|7|",
        "BESSELI|1|70|",
        "BESSELJ|1|70|",
        "BESSELK|1|70|",
        "BESSELY|1|70|",
        "BIN2DEC|11|7|",
        "BIN2HEX|21|51|",
        "BIN2OCT|21|61|",
        "COMPLEX|52|100|",
        "CONVERT|-1|90|",
        "COUNTIFS|-1|28|",
        "COUPDAYBS|63|135|",
        "COUPDAYS|63|135|",
        "COUPDAYSNC|63|135|",
        "COUPNCD|63|135|",
        "COUPNUM|63|135|",
        "COUPPCD|63|135|",
        "CUBEKPIMEMBER|-1|121|",
        "CUBEMEMBER|-1|79|",
        "CUBEMEMBERPROPERTY|-1|80|",
        "CUBERANKEDMEMBER|-1|122|",
        "CUBESET|-1|142|",
        "CUBESETCOUNT|-1|15|",
        "CUBEVALUE|-1|27|",
        "CUMIPMT|64|160|",
        "CUMPRINC|64|160|",
        "DEC2BIN|6|51|",
        "DEC2HEX|6|51|",
        "DEC2OCT|6|51|",
        "DELTA|6|55|",
        "DISC|27|156|",
        "DOLLARDE|1|32|",
        "DOLLARFR|1|30|",
        "DURATION|53|152|",
        "EDATE|1|61|",
        "EFFECT|1|42|",
        "EOMONTH|1|61|",
        "ERF|6|40|",
        "ERFC|0|17|",
        "FACTDOUBLE|0|7|",
        "FVSCHEDULE|54|56|",
        "GCD|4|7|",
        "GESTEP|6|54|",
        "HEX2BIN|21|51|",
        "HEX2DEC|21|7|",
        "HEX2OCT|21|51|",
        "IFERROR|-1|68|",
        "IMABS|11|5|",
        "IMAGINARY|11|5|",
        "IMARGUMENT|11|5|",
        "IMCONJUGATE|11|5|",
        "IMCOS|11|5|",
        "IMDIV|18|36|",
        "IMEXP|11|5|",
        "IMLN|11|5|",
        "IMLOG10|11|5|",
        "IMLOG2|11|5|",
        "IMPOWER|24|37|",
        "IMPRODUCT|17|5|",
        "IMREAL|11|5|",
        "IMSIN|11|5|",
        "IMSQRT|11|5|",
        "IMSUB|18|5|",
        "IMSUM|17|5|",
        "INTRATE|27|155|",
        "ISEVEN|0|7|",
        "ISODD|0|7|",
        "LCM|4|7|",
        "MDURATION|53|152|",
        "MROUND|1|48|",
        "MULTINOMIAL|4|7|",
        "NETWORKDAYS|13|106|",
        "NOMINAL|1|31|",
        "OCT2BIN|21|51|",
        "OCT2DEC|11|7|",
        "OCT2HEX|21|51|",
        "ODDFPRICE|65|169|",
        "ODDFYIELD|-1|168|",
        "ODDLPRICE|-1|171|",
        "ODDLYIELD|-1|170|",
        "PRICE|59|166|",
        "PRICEDISC|27|153|",
        "PRICEMAT|53|163|",
        "QUOTIENT|1|43|",
        "RANDBETWEEN|1|26|",
        "RECEIVED|27|154|",
        "RTD|-1|96|",
        "SERIESSUM|81|139|",
        "SQRTPI|0|7|",
        "SUMIFS|-1|108|",
        "TBILLEQ|10|101|",
        "TBILLPRICE|10|101|",
        "TBILLYIELD|10|102|",
        "WEEKNUM|6|62|",
        "WORKDAY|6|103|",
        "XIRR|60|112|",
        "XNPV|61|99|",
        "YEARFRAC|14|104|",
        "YIELD|59|165|",
        "YIELDDISC|27|156|",
        "YIELDMAT|53|162|"
    ]
});