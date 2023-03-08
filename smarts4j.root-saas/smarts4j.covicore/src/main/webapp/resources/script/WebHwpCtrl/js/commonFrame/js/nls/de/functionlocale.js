/**
 * @author jhmoon
 */
define({
    LocaleName : "de",
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //    function list
    //    type = basic : 0, standard : 1, extend : 2, auto : 3
    //    category = Date : 0, Database : 1, Engineering : 2, Financial : 3, Information : 4, logical : 5, Lookup & Find : 6, Math & Trig : 7, Statistical : 8, text : 9, cube : 10, Add-in and Automation : -1
    //    type, category, order(if type is extend then default extended function name), name, min param count, max param count, support, [visible, [block start, block count]]
    //    only localized function list should be existed except default(EN)
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    functionInfoList: [
        "0|8|0|ANZAHL|1|-1|1",
        "0|5|1|WENN|2|3|1",
        "1|4|2|ISTNV|1|1|1",
        "1|4|3|ISTFEHLER|1|1|1",
        "0|7|4|SUMME|1|-1|1",
        "0|8|5|MITTELWERT|1|-1|1",
        "1|6|8|ZEILE|0|1|1",
        "1|6|9|SPALTE|0|1|1",
        "1|4|10|NV|0|0|1",
        "1|3|11|NBW|2|-1|1",
        "0|8|12|STABW|1|-1|1",
        "1|9|13|DM|1|2|1",
        "1|9|14|FEST|1|3|1",
        "1|7|18|ARCTAN|1|1|1",
        "0|7|20|WURZEL|1|1|1",
        "1|7|25|GANZZAHL|1|1|1",
        "1|7|26|VORZEICHEN|1|1|1",
        "0|7|27|RUNDEN|2|2|1",
        "0|6|28|VERWEIS|2|3|1",
        "1|9|30|WIEDERHOLEN|2|2|1",
        "0|9|31|TEIL|3|3|1",
        "0|9|32|LÄNGE|1|1|1",
        "1|9|33|WERT|1|1|1",
        "1|5|34|WAHR|0|0|1",
        "1|5|35|FALSCH|0|0|1",
        "0|5|36|UND|1|-1|1",
        "0|5|37|ODER|1|-1|1",
        "0|5|38|NICHT|1|1|1",
        "0|7|39|REST|2|2|1",
        "1|1|40|DBANZAHL|3|3|1",
        "1|1|41|DBSUMME|3|3|1",
        "1|1|42|DBMITTELWERT|3|3|1",
        "1|1|43|DBMIN|3|3|1",
        "1|1|44|DBMAX|3|3|1",
        "1|1|45|DBSTABW|3|3|1",
        "0|8|46|VARIANZ|1|-1|1",
        "1|1|47|DBVARIANZ|3|3|1",
        "1|8|49|RGP|1|4|1",
        "1|8|51|RKP|1|4|1",
        "1|8|52|VARIATION|1|4|1",
        "0|3|56|BW|3|5|1",
        "0|3|57|ZW|3|5|1",
        "0|3|58|ZZR|3|5|1",
        "0|3|59|RMZ|3|5|1",
        "0|3|60|ZINS|3|6|1",
        "1|3|61|QIKV|3|3|1",
        "1|3|62|IKV|1|2|1",
        "1|7|63|ZUFALLSZAHL|0|0|1",
        "0|6|64|VERGLEICH|2|3|1",
        "0|0|65|DATUM|3|3|1",
        "1|0|66|ZEIT|3|3|1",
        "1|0|67|TAG|1|1|1",
        "0|0|68|MONAT|1|1|1",
        "0|0|69|JAHR|1|1|1",
        "0|0|70|WOCHENTAG|1|2|1",
        "1|0|71|STUNDE|1|1|1",
        "1|0|73|SEKUNDE|1|1|1",
        "0|0|74|JETZT|0|0|1",
        "1|6|75|BEREICHE|1|1|1",
        "1|6|76|ZEILEN|1|1|1",
        "1|6|77|SPALTEN|1|1|1",
        "0|6|78|BEREICH.VERSCHIEBEN|3|5|1",
        "1|9|82|SUCHEN|2|3|1",
        "1|6|83|MTRANS|1|1|1",
        "1|4|86|TYP|1|1|1",
        "1|7|97|ARCTAN2|2|2|1",
        "1|7|98|ARCSIN|1|1|1",
        "1|7|99|ARCCOS|1|1|1",
        "1|6|100|WAHL|2|-1|1",
        "0|6|101|WVERWEIS|3|4|1",
        "0|6|102|SVERWEIS|3|4|1",
        "1|4|105|ISTBEZUG|1|1|1",
        "0|9|111|ZEICHEN|1|1|1",
        "1|9|112|KLEIN|1|1|1",
        "1|9|113|GROSS|1|1|1",
        "1|9|114|GROSS2|1|1|1",
        "1|9|115|LINKS|1|2|1",
        "1|9|116|RECHTS|1|2|1",
        "1|9|117|IDENTISCH|2|2|1",
        "0|9|118|GLÄTTEN|1|1|1",
        "1|9|119|ERSETZEN|4|4|1",
        "1|9|120|WECHSELN|3|4|1",
        "1|9|124|FINDEN|2|3|1",
        "1|4|125|ZELLE|1|2|1",
        "1|4|126|ISTFEHL|1|1|1",
        "1|4|127|ISTTEXT|1|1|1",
        "1|4|128|ISTZAHL|1|1|1",
        "1|4|129|ISTLEER|1|1|1",
        "1|0|140|DATWERT|1|1|1",
        "1|0|141|ZEITWERT|1|1|1",
        "1|3|142|LIA|3|3|1",
        "1|3|143|DIA|4|4|1",
        "1|3|144|GDA|4|5|1",
        "1|6|148|INDIREKT|1|2|1",
        "1|9|162|SÄUBERN|1|1|1",
        "1|7|163|MDET|1|1|1",
        "1|7|164|MINV|1|1|1",
        "1|3|167|ZINSZ|4|6|1",
        "1|3|168|KAPZ|4|6|1",
        "0|8|169|ANZAHL2|1|-1|1",
        "1|7|183|PRODUKT|1|-1|1",
        "1|7|184|FAKULTÄT|1|1|1",
        "1|1|189|DBPRODUKT|3|3|1",
        "1|4|190|ISTKTEXT|1|1|1",
        "1|8|193|STABWN|1|-1|1",
        "1|8|194|VARIANZEN|1|-1|1",
        "1|1|195|DBSTDABWN|3|3|1",
        "1|1|196|DBVARIANZEN|3|3|1",
        "0|7|197|KÜRZEN|1|2|1",
        "1|4|198|ISTLOG|1|1|1",
        "1|1|199|DBANZAHL2|3|3|1",
        "1|9|205|FINDENB|2|3|0",
        "1|9|206|SUCHENB|2|3|0",
        "1|9|207|ERSETZENB|4|4|0",
        "1|9|208|LINKSB|1|2|0",
        "1|9|209|RECHTSB|1|2|0",
        "1|9|210|TEILB|3|3|0",
        "1|9|211|LÄNGEB|1|1|0",
        "1|7|212|AUFRUNDEN|2|2|1",
        "1|7|213|ABRUNDEN|2|2|1",
        "1|9|215|JIS|1|1|0|0",
        "0|8|216|RANG|2|3|1",
        "1|6|219|ADRESSE|2|5|1",
        "0|0|220|TAGE360|2|3|1",
        "0|0|221|HEUTE|0|0|1",
        "1|7|228|SUMMENPRODUKT|1|-1|1",
        "1|7|229|SINHYP|1|1|1",
        "1|7|230|COSHYP|1|1|1",
        "1|7|231|TANHYP|1|1|1",
        "1|7|232|ARCSINHYP|1|1|1",
        "1|7|233|ARCCOSHYP|1|1|1",
        "1|7|234|ARCTANHYP|1|1|1",
        "1|1|235|DBAUSZUG|3|3|1",
        "0|3|247|GDA2|4|5|1",
        "0|8|252|HÄUFIGKEIT|2|2|1",
        "1|4|261|FEHLER.TYP|1|1|1",
        "1|8|269|MITTELABW|1|-1|1",
        "1|8|270|BETAVERT|3|5|1",
        "1|8|273|BINOMVERT|4|4|1",
        "1|8|274|CHIVERT|2|2|1",
        "1|7|276|KOMBINATIONEN|2|2|1",
        "1|8|277|KONFIDENZ|3|3|1",
        "1|8|278|KRITBINOM|3|3|1",
        "1|7|279|GERADE|1|1|1",
        "1|8|280|EXPONVERT|3|3|1",
        "1|8|281|FVERT|3|3|1",
        "0|7|285|UNTERGRENZE|2|2|1",
        "1|8|286|GAMMAVERT|4|4|1",
        "1|7|288|OBERGRENZE|2|2|1",
        "1|8|289|HYPGEOMVERT|4|4|1",
        "1|8|290|LOGNORMVERT|3|3|1",
        "1|8|292|NEGBINOMVERT|3|3|1",
        "1|8|293|NORMVERT|4|4|1",
        "1|8|294|STANDNORMVERT|1|1|1",
        "1|8|296|STANDNORMINV|1|1|0",
        "1|8|297|STANDARDISIERUNG|3|3|1",
        "1|7|298|UNGERADE|1|1|1",
        "1|8|299|VARIATIONEN|2|2|1",
        "1|8|301|TVERT|3|3|1",
        "1|7|303|SUMMEXMY2|2|2|1",
        "1|7|304|SUMMEX2MY2|2|2|1",
        "1|7|305|SUMMEX2PY2|2|2|1",
        "1|8|307|KORREL|2|2|1",
        "1|8|308|KOVAR|2|2|1",
        "1|8|309|SCHÄTZER|3|3|1",
        "1|8|311|ACHSENABSCHNITT|2|2|1",
        "1|8|313|BESTIMMTHEITSMASS|2|2|1",
        "1|8|314|STFEHLERYX|2|2|1",
        "1|8|315|STEIGUNG|2|2|1",
        "1|8|317|WAHRSCHBEREICH|3|4|1",
        "1|8|318|SUMQUADABW|1|-1|1",
        "1|8|319|GEOMITTEL|1|-1|1",
        "1|8|320|HARMITTEL|1|-1|1",
        "1|7|321|QUADRATESUMME|1|-1|1",
        "1|8|323|SCHIEFE|1|-1|1",
        "1|8|324|GTEST|2|3|1",
        "1|8|325|KGRÖSSTE|2|2|1",
        "1|8|326|KKLEINSTE|2|2|1",
        "1|8|328|QUANTIL|2|2|1",
        "1|8|329|QUANTILSRANG|2|3|1",
        "1|8|330|MODALWERT|1|-1|1",
        "1|8|331|GESTUTZTMITTEL|2|2|1",
        "1|9|336|VERKETTEN|2|-1|1",
        "0|7|337|POTENZ|2|2|1",
        "1|7|342|BOGENMASS|1|1|1",
        "1|7|343|GRAD|1|1|1",
        "0|7|344|TEILERGEBNIS|2|-1|1",
        "0|7|345|SUMMEWENN|2|3|1",
        "0|8|346|ZÄHLENWENN|2|2|1",
        "1|8|347|ANZAHLLEEREZELLEN|1|1|1",
        "1|7|354|RÖMISCH|1|2|1",
        "1|8|361|MITTELWERTA|1|-1|1",
        "1|8|364|STABWNA|1|-1|1",
        "1|8|365|VARIANZENA|1|-1|1",
        "1|8|366|STABWA|1|-1|1",
        "1|8|367|VARIANZA|1|-1|1",
        "2|3|ACCRINT|AUFGELZINS|6|8|1",
        "2|3|ACCRINTM|AUFGELZINSF|3|5|1",
        "2|3|AMORDEGRC|AMORDEGRK|6|7|1",
        "2|3|AMORLINC|AMORLINEARK|6|7|1",
        "2|2|BIN2DEC|BININDEZ|1|1|1",
        "2|2|BIN2HEX|BININHEX|1|2|1",
        "2|2|BIN2OCT|BININOKT|1|2|1",
        "2|2|COMPLEX|KOMPLEXE|2|3|1",
        "2|2|CONVERT|UMWANDELN|3|3|1",
        "2|3|COUPDAYBS|ZINSTERMTAGVA|3|4|1",
        "2|3|COUPDAYS|ZINSTERMTAGE|3|4|1",
        "2|3|COUPDAYSNC|ZINSTERMTAGNZ|3|4|1",
        "2|3|COUPNCD|ZINSTERMNZ|3|4|1",
        "2|3|COUPNUM|ZINSTERMZAHL|3|4|1",
        "2|3|COUPPCD|ZINSTERMVZ|3|4|1",
        "2|3|CUMIPMT|KUMZINSZ|6|6|1",
        "2|3|CUMPRINC|KUMKAPITAL|6|6|1",
        "2|2|DEC2BIN|DEZINBIN|1|2|1|",
        "2|2|DEC2HEX|DEZINHEX|1|2|1|",
        "2|2|DEC2OCT|DEZINOKT|1|2|1|",
        "2|3|DISC|DISAGIO|4|5|1|",
        "2|3|DOLLARDE|NOTIERUNGDEZ|2|2|1|",
        "2|3|DOLLARFR|NOTIERUNGBRU|2|2|1|",
        "2|0|EDATE|EDATUM|2|2|1|",
        "2|3|EFFECT|EFFEKTIV|2|2|1|",
        "2|0|EOMONTH|MONATSENDE|2|2|1|",
        "2|2|ERF|GAUSSFEHLER|1|2|1|",
        "2|2|ERFC|GAUSSFKOMPL|1|1|1|",
        "2|2|FACTDOUBLE|ZWEIFAKULTÄT|1|1|1|",
        "2|3|FVSCHEDULE|ZW2|2|2|1|",
        "2|7|GCD|GGT|1|-1|1",
        "2|2|GESTEP|GGANZZAHL|1|2|1",
        "2|2|HEX2BIN|HEXINBIN|1|2|1",
        "2|2|HEX2DEC|HEXINDEZ|1|1|1",
        "2|2|HEX2OCT|HEXINOKT|1|2|1",
        "2|2|IMAGINARY|IMAGINÄRTEIL|1|1|1",
        "2|2|IMCONJUGATE|IMKONJUGIERTE|1|1|1",
        "2|2|IMPOWER|IMAPOTENZ|2|2|1",
        "2|2|IMPRODUCT|IMPRODUKT|1|-1|1",
        "2|2|IMREAL|IMREALTEIL|1|1|1",
        "2|2|IMSQRT|IMWURZEL|1|1|1",
        "2|2|IMSUM|IMSUMME|1|-1|1",
        "2|3|INTRATE|ZINSSATZ|4|5|1",
        "2|4|ISODD|ISTUNGERADE|1|1|1",
        "2|7|LCM|KGV|1|-1|1",
        "2|7|MROUND|VRUNDEN|2|2|1",
        "2|7|MULTINOMIAL|POLYNOMIAL|1|-1|1",
        "2|0|NETWORKDAYS|NETTOARBEITSTAGE|2|3|1",
        "2|2|OCT2BIN|OKTINBIN|1|2|1|",
        "2|2|OCT2DEC|OKTINDEZ|1|1|1|",
        "2|2|OCT2HEX|OKTINHEX|1|2|1|",
        "2|3|ODDFPRICE|UNREGER.KURS|8|9|1|",
        "2|3|ODDFYIELD|UNREGER.REND|8|9|0|",
        "2|3|ODDLPRICE|UNREGLE.KURS|7|8|0|",
        "2|3|ODDLYIELD|UNREGLE.REND|7|8|0|",
        "2|3|PRICE|KURS|6|7|1|",
        "2|3|PRICEDISC|KURSDISAGIO|4|5|1|",
        "2|3|PRICEMAT|KURSFÄLLIG|5|6|1|",
        "2|7|RANDBETWEEN|ZUFALLSBEREICH|2|2|1|",
        "2|3|RECEIVED|AUSZAHLUNG|4|5|1|",
        "-1|-1|REGISTER.ID|REGISTER.KENNUMMER|2|3|0|0",
        "2|7|SERIESSUM|POTENZREIHE|4|4|1|",
        "2|7|SQRTPI|WURZELPI|1|1|1|",
        "2|3|TBILLEQ|TBILLÄQUIV|3|3|1|",
        "2|3|TBILLPRICE|TBILLKURS|3|3|1|",
        "2|3|TBILLYIELD|TBILLRENDITE|3|3|1|",
        "2|0|WEEKNUM|KALENDERWOCHE|1|2|1|",
        "2|0|WORKDAY|ARBEITSTAG|2|3|1|",
        "2|3|XIRR|XINTZINSFUSS|2|3|1|",
        "2|3|XNPV|XKAPITALWERT|3|3|1|",
        "2|0|YEARFRAC|BRTEILJAHRE|2|3|1|",
        "2|3|YIELD|RENDITE|6|7|1|",
        "2|3|YIELDDISC|RENDITEDIS|4|5|1|",
        "2|3|YIELDMAT|RENDITEFÄLL|5|6|1|"
    ],

    functionArgumentList: [
        "winkel",
        "matrix",
        "datumstext",
        "fehlerwert",
        "inumber",
        "wahrheitswert1",
        "zahl",
        "wahrsch",
        "text",
        "zeit",
        "typ",
        "bereich",
        "bezug",
        "zahl",
        "set",
        "wert",
        "x",
        "y",
        "z",
        "beobachtetewerte|erwartetewerte",
        "matrix|k",
        "matrix|prozent",
        "matrix|quartil",
        "matrix1|matrix2",
        "matrix_x|matrix_y",
        "bottom|top",
        "connection|member_expression",
        "criteria_range|criteria",
        "daten|klassen",
        "decimal_dollar|fraction",
        "effect_rate|npery",
        "fractional_dollar|fraction",
        "funktion|bezug",
        "index|wert",
        "infotyp| bezug",
        "inumber1|inumber2",
        "inumber|number",
        "y_werte|x_werte",
        "suchkriterium|matrix",
        "lower_limit|upper_limit",
        "hyperlink_adresse|freundlicher_name",
        "nominal_rate|npery",
        "numerator|denominator",
        "zahl|basis",
        "zahl|divisor",
        "zahl|dezimalstellen",
        "zahl|typ",
        "number|multiple",
        "n|k",
        "zahl|anzahl_stellen",
        "number|places",
        "zahl|potenz",
        "zahl|schritt",
        "number|step",
        "number1|number2",
        "principal|schedule",
        "wahrscheinlichkeit|freiheitsgrade",
        "bereich|kriterien",
        "zins|wert",
        "bezug|a1",
        "start_date|months",
        "zahl|typ",
        "text|multiplikator",
        "text|anzahl_bytes",
        "text|anzahl_zeichen",
        "text1|text2",
        "wert|textformat",
        "value|value_if_error",
        "werte|schätzwert",
        "x|n",
        "x|freiheitsgrade",
        "x_koordinate|y_koordinate",
        "jahr|monat|tag",
        "alpha|standabwn|umfang_s",
        "matrix|zeile|spalte",
        "matrix|x|sigma",
        "matrix|x|genauigkeit",
        "average_range|criteria_range|criteria",
        "connection|member_expression|caption",
        "connection|member_expression|property",
        "ansch_wert|restwert|nutzungsdauer",
        "ansch_wert|restwert|nutzungsdauer|zr",
        "datenbank|datenbankfeld|suchkriterien",
        "suchtext|text|erstes_zeichen",
        "stunde|minute|sekunde",
        "prüfung|dann_wert|sonst_wert",
        "suchkriterium|suchmatrix|vergleichstyp",
        "suchkriterium|suchvektor|ergebnisvektor",
        "zahl|dezimalstellen|keine_punkte",
        "number|from_unit|to_unit",
        "zahl|bezug|reihenfolge",
        "zahl_misserfolge|zahl_erfolge|erfolgswahrsch",
        "wahrsch|alpha|beta",
        "wahrsch|freiheitsgrade1|freiheitsgrade2",
        "wahrsch|mittelwert|standabwn",
        "progid|server|topic",
        "bereich|kriterien|summe_bereich",
        "range|criteria|average_range",
        "rate|values|dates",
        "real_num|i_num|suffix",
        "settlement|maturity|discount",
        "settlement|maturity|pr",
        "start_date|days|holidays",
        "start_date|end_date|basis",
        "start_date|end_date|interval",
        "start_date|end_date|holidays",
        "ausgangsdatum|enddatum|methode",
        "sum_range|criteria_range|criteria",
        "text|erstes_zeichen|anzahl_bytes",
        "text|erstes_zeichen|anzahl_zeichen",
        "versuche|erfolgswahrsch|alpha",
        "values|dates|guess",
        "werte|investition|reinvestition",
        "x|freiheitsgrade|seiten",
        "x|freiheitsgrade1|freiheitsgrade2",
        "x|y_werte|x_werte",
        "x|lambda|kumuliert",
        "x|mittelwert|kumuliert",
        "x|mittelwert|standabwn",
        "matrix1|matrix2|seiten|typ",
        "connection|kpi_name|kpi_property|caption",
        "connection|set_expression|rank|caption",
        "y_werte|x_werte|konstante|stats",
        "y_werte|x_werte|neue_x_werte|konstante",
        "lookup_value|lookup_vector|result_vector|array",
        "suchkriterium|matrix|spaltenindex|bereich_verweis",
        "suchkriterium|matrix|zeilenindex|bereich_verweis",
        "anzahlerfolge|versuche|erfolgswahrscheinlichkeit|kumuliert",
        "alter_text|erstes_zeichen|anzahl_bytes|neuer_text",
        "alter_text|erstes_zeichen|anzahl_zeichen|neuer_text",
        "zins|pro|zzr|bw",
        "bezug|zeile|spalte|bereich",
        "zeile|spalte|abs|a1|tabellenname",
        "erfolge_s|umfang_s|erfolge_g|umfang_g",
        "settlement|maturity|frequency|basis",
        "text|alter_text|neuer_text|ntes_auftreten",
        "x|alpha|beta|kumuliert",
        "x|mittelwert|standabwn|kumuliert",
        "x|n|m|coefficients",
        "beob_werte|beob_wahrsch|untergrenze|obergrenze",
        "array|row_num|column_num|reference|area_num",
        "connection|set_expression|caption|set_order|sort_by",
        "anschaffungswert|restwert|nutzungsdauer|periode|monate",
        "anschaffungswert|restwert|nutzungsdauer|periode|faktor",
        "issue|settlement|rate|par|basis",
        "wahrscheinlichkeit|alpha|beta|a|b",
        "zins|zzr|rmz|zw|f",
        "rate|nper|pmt|pv|type",
        "zins|zzr|bw|zw|f",
        "zins|rmz|bw|zw|f",
        "bezug|zeilen|spalten|höhe|breite",
        "settlement|maturity|coupon|yld|frequency|basis",
        "settlement|maturity|discount|redemption|basis",
        "settlement|maturity|investment|discount|basis",
        "settlement|maturity|investment|redemption|basis",
        "settlement|maturity|pr|redemption|basis",
        "x|alpha|beta|a|b",
        "zins|zzr|rmz|bw|f",
        "zzr|rmz|bw|zw|f|schätzwert",
        "rate|nper|pv|start_period|end_period|type",
        "zins|zr|zzr|bw|zw|f",
        "settlement|maturity|issue|rate|pr|basis",
        "settlement|maturity|issue|rate|yld|basis",
        "ansch_wert|restwert|nutzungsdauer|anfang|fertig stellen|faktor|nicht_wechseln",
        "settlement|maturity|rate|pr|redemption|frequency|basis",
        "settlement|maturity|rate|yld|redemption|frequency|basis",
        "issue|first_interest|settlement|rate|par|frequency|basis|calc_method",
        "settlement|maturity|issue|first_coupon|rate|pr|redemption|frequency|basis",
        "settlement|maturity|issue|first_coupon|rate|yld|redemption|frequency|basis",
        "settlement|maturity|last_interest|rate|pr|redemption|frequency|basis",
        "settlement|maturity|last_interest|rate|yld|redemption|frequency|basis",
        "data_field|pivot_table|field|item"
    ]
});