/**
 * 
 */

var worldTime = {
	timer: '',
	config: {
		type: 'Card',			// 웹파트 표시 유형	Card/List
		currentCountry: 'ko',	// 기초코드 'GlobalTime'의 Reserved1 중 1
		currentCity: 'Asia/Seoul',	// 기초코드 'GlobalTime'의 Code 중 1
		cityClassName: 'ptype02_c_t03',
		citySelectClassName: 'ptype02_c_select',
		dateTimeClassName: 'ptype02_c_t04'
	},
	timezone: {},				// 타임존 객체 { name: 국가코드, zone: { 타임존코드1: 도시명1 ... } ... }
	init: function(data, ext, caller){
		var _ext = (typeof ext == 'object') ? ext : {};
		worldTime.caller = caller;
		
		worldTime.config = $.extend(worldTime.config, _ext);
		
		if(worldTime.caller == 'myPlace'){
			$("#worldTime.webpart").closest(".PN_myContents_box").find(".PN_portlet_link").text($("#worldTime .webpart-top h2").text());
			$("#worldTime .webpart-top").remove();
		}
		
		var _timezone = Common.getBaseCode('GlobalTime').CacheData;
		$.each(_timezone, function(idx, el){
			if (typeof worldTime.timezone[el.Reserved1] == 'undefined'){
				worldTime.timezone[el.Reserved1] = {
					name: el.Reserved2,
					zone: {}
				}
			}
			worldTime.timezone[el.Reserved1].zone[el.Code] = el.MultiCodeName;
		});		
		
		if (worldTime.config.type == 'Card'){
			$("#worldTime.webpart").addClass("Worldtime");
				
			$("#worldTime .seoul_time_wrap").append(
				$("#tempateWorldTimeCard").html()
					.replace('{country}', CFN_GetDicInfo(worldTime.timezone[worldTime.config.currentCountry].name, Common.getSession('lang')))
					.replace('{city}', CFN_GetDicInfo(worldTime.timezone[worldTime.config.currentCountry].zone[worldTime.config.currentCity], Common.getSession('lang')))
			);
			
			$("#worldTime .select_time_wrap").append(
				$("#tempateWorldTimeCard").html()
					.replace('{country}', '')
					.replace('{city}', '')
			);
			$("#worldTime .select_time_wrap .tx_country").addClass('sel_country');
			$("#worldTime .select_time_wrap .tx_city").addClass('sel_city');
			
			worldTime.setCountry();
		}
		else if (worldTime.config.type == 'List'){
			$("#worldTime .webpart-top").remove();
			$("#worldTime.webpart").addClass("ptype02_universal_time");
			$("#worldTime.ptype02_universal_time .seoul_time_wrap, #worldTime.ptype02_universal_time .select_time_wrap").addClass("ptype02_timebox");
			
			$("#worldTime .seoul_time_wrap").append(
				$("#tempateWorldTimeList").html()
					.replace('{cityClassName}', worldTime.config.cityClassName)
					.replace('{dateTimeClassName}', worldTime.config.dateTimeClassName)
					.replace('{city}', CFN_GetDicInfo(worldTime.timezone[worldTime.config.currentCountry].zone[worldTime.config.currentCity], Common.getSession('lang')))
			);
			
			$("#worldTime .select_time_wrap").append(
				$("#tempateWorldTimeList").html()
					.replace('{cityClassName}', worldTime.config.citySelectClassName)
					.replace('{dateTimeClassName}', worldTime.config.dateTimeClassName)
			);
			
			worldTime.setCity('all');
		}
		
		worldTime.getTime();
	},
	getTime: function(){
		var current = new Date();
		worldTime.date = current.format('yyyy.MM.dd');
		worldTime.time = current.format('hh:mm');
		worldTime.ampm = (current.getHours() < 12) ? 'AM' : 'PM' 

		var selected = worldTime.changeTimeZone(current, $("#worldTime .city_select").val());
		worldTime.selectedDate = selected.format('yyyy.MM.dd');
		worldTime.selectedTime = selected.format('hh:mm');
		worldTime.selectedAmpm = (selected.getHours() < 12) ? 'AM' : 'PM' 
		
		worldTime.setTime();
		
		clearTimeout(worldTime.timer);
		worldTime.timer = setTimeout(worldTime.getTime, 1000);
	},
	setTime: function(){
		if (worldTime.config.type == 'Card'){
			$("#worldTime .seoul_time_wrap .tx_date").text(worldTime.date);
			$("#worldTime .seoul_time_wrap .tx_time").contents()[0].textContent = worldTime.time
			$("#worldTime .seoul_time_wrap .tx_ampm").text(worldTime.ampm);
			
			$("#worldTime .select_time_wrap .tx_date").text(worldTime.selectedDate);
			$("#worldTime .select_time_wrap .tx_time").contents()[0].textContent = worldTime.selectedTime
			$("#worldTime .select_time_wrap .tx_ampm").text(worldTime.selectedAmpm);
		}
		else if (worldTime.config.type == 'List'){
			$("#worldTime .seoul_time_wrap .tx_time").text(worldTime.date+' '+worldTime.time);
			$("#worldTime .select_time_wrap .tx_time").text(worldTime.selectedDate+' '+worldTime.selectedTime);
		}
	},
	changeTimeZone: function(date, timeZone) {
		if (typeof date === 'string') {
			return new Date(new Date(date).toLocaleString('en-US', { timeZone }));
		}

		return new Date(date.toLocaleString('en-US', { timeZone }));
	},
	setCountry: function(){
		if ($("#worldTime .select_time_wrap .sel_country country_select").length == 0) {
			$("#worldTime .select_time_wrap .sel_country").html('<select class="country_select" onchange="javascreipt:worldTime.setCity(this);"></select>')
		}
		$.each(worldTime.timezone, function(idx, el){
			if (idx != worldTime.config.currentCountry){
				$("#worldTime .select_time_wrap .sel_country .country_select").append(
					'<option value="' + idx + '">' + CFN_GetDicInfo(el.name, Common.getSession('lang')) + '</option>'
				);
			}
		});
		if (worldTime.config.targetCity) $("#worldTime .select_time_wrap .sel_country .country_select").val(worldTime.config.targetCity);
		$("#worldTime .select_time_wrap .sel_country .country_select").change();
	},
	setCity: function(country){
		var _country = (typeof country == 'string') ? country : country.value;
		if ($("#worldTime .select_time_wrap .sel_city city_select").length == 0) {
			$("#worldTime .select_time_wrap .sel_city").html('<select class="city_select ptype02_time_select" onchange="javascreipt:worldTime.getTime();"></select>')
		}

		$("#worldTime .select_time_wrap .sel_city .city_select").empty();
		$.each(worldTime.timezone, function(countryIdx, countries){
			if ((countryIdx == _country || _country == 'all') && countryIdx != worldTime.config.currentCountry){
				$.each(countries.zone, function(idx, el){
					$("#worldTime .select_time_wrap .sel_city .city_select").append(
						'<option value="' + idx + '">' + CFN_GetDicInfo(el, Common.getSession('lang')) + '</option>'
					)	
				})
			}
		});
	}
}

/* 시간존
Intl.supportedValuesOf('timeZone')
var aryIannaTimeZones = [
  'Europe/Andorra',
  'Asia/Dubai',
  'Asia/Kabul',
  'Europe/Tirane',
  'Asia/Yerevan',
  'Antarctica/Casey',
  'Antarctica/Davis',
  'Antarctica/DumontDUrville', // https://bugs.chromium.org/p/chromium/issues/detail?id=928068
  'Antarctica/Mawson',
  'Antarctica/Palmer',
  'Antarctica/Rothera',
  'Antarctica/Syowa',
  'Antarctica/Troll',
  'Antarctica/Vostok',
  'America/Argentina/Buenos_Aires',
  'America/Argentina/Cordoba',
  'America/Argentina/Salta',
  'America/Argentina/Jujuy',
  'America/Argentina/Tucuman',
  'America/Argentina/Catamarca',
  'America/Argentina/La_Rioja',
  'America/Argentina/San_Juan',
  'America/Argentina/Mendoza',
  'America/Argentina/San_Luis',
  'America/Argentina/Rio_Gallegos',
  'America/Argentina/Ushuaia',
  'Pacific/Pago_Pago',
  'Europe/Vienna',
  'Australia/Lord_Howe',
  'Antarctica/Macquarie',
  'Australia/Hobart',
  'Australia/Currie',
  'Australia/Melbourne',
  'Australia/Sydney',
  'Australia/Broken_Hill',
  'Australia/Brisbane',
  'Australia/Lindeman',
  'Australia/Adelaide',
  'Australia/Darwin',
  'Australia/Perth',
  'Australia/Eucla',
  'Asia/Baku',
  'America/Barbados',
  'Asia/Dhaka',
  'Europe/Brussels',
  'Europe/Sofia',
  'Atlantic/Bermuda',
  'Asia/Brunei',
  'America/La_Paz',
  'America/Noronha',
  'America/Belem',
  'America/Fortaleza',
  'America/Recife',
  'America/Araguaina',
  'America/Maceio',
  'America/Bahia',
  'America/Sao_Paulo',
  'America/Campo_Grande',
  'America/Cuiaba',
  'America/Santarem',
  'America/Porto_Velho',
  'America/Boa_Vista',
  'America/Manaus',
  'America/Eirunepe',
  'America/Rio_Branco',
  'America/Nassau',
  'Asia/Thimphu',
  'Europe/Minsk',
  'America/Belize',
  'America/St_Johns',
  'America/Halifax',
  'America/Glace_Bay',
  'America/Moncton',
  'America/Goose_Bay',
  'America/Blanc-Sablon',
  'America/Toronto',
  'America/Nipigon',
  'America/Thunder_Bay',
  'America/Iqaluit',
  'America/Pangnirtung',
  'America/Atikokan',
  'America/Winnipeg',
  'America/Rainy_River',
  'America/Resolute',
  'America/Rankin_Inlet',
  'America/Regina',
  'America/Swift_Current',
  'America/Edmonton',
  'America/Cambridge_Bay',
  'America/Yellowknife',
  'America/Inuvik',
  'America/Creston',
  'America/Dawson_Creek',
  'America/Fort_Nelson',
  'America/Vancouver',
  'America/Whitehorse',
  'America/Dawson',
  'Indian/Cocos',
  'Europe/Zurich',
  'Africa/Abidjan',
  'Pacific/Rarotonga',
  'America/Santiago',
  'America/Punta_Arenas',
  'Pacific/Easter',
  'Asia/Shanghai',
  'Asia/Urumqi',
  'America/Bogota',
  'America/Costa_Rica',
  'America/Havana',
  'Atlantic/Cape_Verde',
  'America/Curacao',
  'Indian/Christmas',
  'Asia/Nicosia',
  'Asia/Famagusta',
  'Europe/Prague',
  'Europe/Berlin',
  'Europe/Copenhagen',
  'America/Santo_Domingo',
  'Africa/Algiers',
  'America/Guayaquil',
  'Pacific/Galapagos',
  'Europe/Tallinn',
  'Africa/Cairo',
  'Africa/El_Aaiun',
  'Europe/Madrid',
  'Africa/Ceuta',
  'Atlantic/Canary',
  'Europe/Helsinki',
  'Pacific/Fiji',
  'Atlantic/Stanley',
  'Pacific/Chuuk',
  'Pacific/Pohnpei',
  'Pacific/Kosrae',
  'Atlantic/Faroe',
  'Europe/Paris',
  'Europe/London',
  'Asia/Tbilisi',
  'America/Cayenne',
  'Africa/Accra',
  'Europe/Gibraltar',
  'America/Godthab',
  'America/Danmarkshavn',
  'America/Scoresbysund',
  'America/Thule',
  'Europe/Athens',
  'Atlantic/South_Georgia',
  'America/Guatemala',
  'Pacific/Guam',
  'Africa/Bissau',
  'America/Guyana',
  'Asia/Hong_Kong',
  'America/Tegucigalpa',
  'America/Port-au-Prince',
  'Europe/Budapest',
  'Asia/Jakarta',
  'Asia/Pontianak',
  'Asia/Makassar',
  'Asia/Jayapura',
  'Europe/Dublin',
  'Asia/Jerusalem',
  'Asia/Kolkata',
  'Indian/Chagos',
  'Asia/Baghdad',
  'Asia/Tehran',
  'Atlantic/Reykjavik',
  'Europe/Rome',
  'America/Jamaica',
  'Asia/Amman',
  'Asia/Tokyo',
  'Africa/Nairobi',
  'Asia/Bishkek',
  'Pacific/Tarawa',
  'Pacific/Enderbury',
  'Pacific/Kiritimati',
  'Asia/Pyongyang',
  'Asia/Seoul',
  'Asia/Almaty',
  'Asia/Qyzylorda',
  'Asia/Qostanay', // https://bugs.chromium.org/p/chromium/issues/detail?id=928068
  'Asia/Aqtobe',
  'Asia/Aqtau',
  'Asia/Atyrau',
  'Asia/Oral',
  'Asia/Beirut',
  'Asia/Colombo',
  'Africa/Monrovia',
  'Europe/Vilnius',
  'Europe/Luxembourg',
  'Europe/Riga',
  'Africa/Tripoli',
  'Africa/Casablanca',
  'Europe/Monaco',
  'Europe/Chisinau',
  'Pacific/Majuro',
  'Pacific/Kwajalein',
  'Asia/Yangon',
  'Asia/Ulaanbaatar',
  'Asia/Hovd',
  'Asia/Choibalsan',
  'Asia/Macau',
  'America/Martinique',
  'Europe/Malta',
  'Indian/Mauritius',
  'Indian/Maldives',
  'America/Mexico_City',
  'America/Cancun',
  'America/Merida',
  'America/Monterrey',
  'America/Matamoros',
  'America/Mazatlan',
  'America/Chihuahua',
  'America/Ojinaga',
  'America/Hermosillo',
  'America/Tijuana',
  'America/Bahia_Banderas',
  'Asia/Kuala_Lumpur',
  'Asia/Kuching',
  'Africa/Maputo',
  'Africa/Windhoek',
  'Pacific/Noumea',
  'Pacific/Norfolk',
  'Africa/Lagos',
  'America/Managua',
  'Europe/Amsterdam',
  'Europe/Oslo',
  'Asia/Kathmandu',
  'Pacific/Nauru',
  'Pacific/Niue',
  'Pacific/Auckland',
  'Pacific/Chatham',
  'America/Panama',
  'America/Lima',
  'Pacific/Tahiti',
  'Pacific/Marquesas',
  'Pacific/Gambier',
  'Pacific/Port_Moresby',
  'Pacific/Bougainville',
  'Asia/Manila',
  'Asia/Karachi',
  'Europe/Warsaw',
  'America/Miquelon',
  'Pacific/Pitcairn',
  'America/Puerto_Rico',
  'Asia/Gaza',
  'Asia/Hebron',
  'Europe/Lisbon',
  'Atlantic/Madeira',
  'Atlantic/Azores',
  'Pacific/Palau',
  'America/Asuncion',
  'Asia/Qatar',
  'Indian/Reunion',
  'Europe/Bucharest',
  'Europe/Belgrade',
  'Europe/Kaliningrad',
  'Europe/Moscow',
  'Europe/Simferopol',
  'Europe/Kirov',
  'Europe/Astrakhan',
  'Europe/Volgograd',
  'Europe/Saratov',
  'Europe/Ulyanovsk',
  'Europe/Samara',
  'Asia/Yekaterinburg',
  'Asia/Omsk',
  'Asia/Novosibirsk',
  'Asia/Barnaul',
  'Asia/Tomsk',
  'Asia/Novokuznetsk',
  'Asia/Krasnoyarsk',
  'Asia/Irkutsk',
  'Asia/Chita',
  'Asia/Yakutsk',
  'Asia/Khandyga',
  'Asia/Vladivostok',
  'Asia/Ust-Nera',
  'Asia/Magadan',
  'Asia/Sakhalin',
  'Asia/Srednekolymsk',
  'Asia/Kamchatka',
  'Asia/Anadyr',
  'Asia/Riyadh',
  'Pacific/Guadalcanal',
  'Indian/Mahe',
  'Africa/Khartoum',
  'Europe/Stockholm',
  'Asia/Singapore',
  'America/Paramaribo',
  'Africa/Juba',
  'Africa/Sao_Tome',
  'America/El_Salvador',
  'Asia/Damascus',
  'America/Grand_Turk',
  'Africa/Ndjamena',
  'Indian/Kerguelen',
  'Asia/Bangkok',
  'Asia/Dushanbe',
  'Pacific/Fakaofo',
  'Asia/Dili',
  'Asia/Ashgabat',
  'Africa/Tunis',
  'Pacific/Tongatapu',
  'Europe/Istanbul',
  'America/Port_of_Spain',
  'Pacific/Funafuti',
  'Asia/Taipei',
  'Europe/Kiev',
  'Europe/Uzhgorod',
  'Europe/Zaporozhye',
  'Pacific/Wake',
  'America/New_York',
  'America/Detroit',
  'America/Kentucky/Louisville',
  'America/Kentucky/Monticello',
  'America/Indiana/Indianapolis',
  'America/Indiana/Vincennes',
  'America/Indiana/Winamac',
  'America/Indiana/Marengo',
  'America/Indiana/Petersburg',
  'America/Indiana/Vevay',
  'America/Chicago',
  'America/Indiana/Tell_City',
  'America/Indiana/Knox',
  'America/Menominee',
  'America/North_Dakota/Center',
  'America/North_Dakota/New_Salem',
  'America/North_Dakota/Beulah',
  'America/Denver',
  'America/Boise',
  'America/Phoenix',
  'America/Los_Angeles',
  'America/Anchorage',
  'America/Juneau',
  'America/Sitka',
  'America/Metlakatla',
  'America/Yakutat',
  'America/Nome',
  'America/Adak',
  'Pacific/Honolulu',
  'America/Montevideo',
  'Asia/Samarkand',
  'Asia/Tashkent',
  'America/Caracas',
  'Asia/Ho_Chi_Minh',
  'Pacific/Efate',
  'Pacific/Wallis',
  'Pacific/Apia',
  'Africa/Johannesburg'
];
*/