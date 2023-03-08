package egovframework.covision.coviflow.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CoviFlowDateHelper {
	/**
     * 현재 년월 - YYYYMM
     */
    public static String getMonth() {
        String month;
 
        Calendar cal = Calendar.getInstance(Locale.getDefault());
 
        StringBuffer buf = new StringBuffer();
 
        buf.append(Integer.toString(cal.get(Calendar.YEAR)));
        month = Integer.toString(cal.get(Calendar.MONTH) + 1);
        if (month.length() == 1)
            month = "0" + month;
 
        buf.append(month);
 
        return buf.toString();
    }
 
    /**
     * 현재 년월일 - YYYYMMDD
     */
    public static String getDate() {
        String month, day;
 
        Calendar cal = Calendar.getInstance(Locale.getDefault());
 
        StringBuffer buf = new StringBuffer();
 
        buf.append(Integer.toString(cal.get(Calendar.YEAR)));
        month = Integer.toString(cal.get(Calendar.MONTH) + 1);
        if (month.length() == 1)
            month = "0" + month;
        day = Integer.toString(cal.get(Calendar.DATE));
        if (day.length() == 1)
            day = "0" + day;
 
        buf.append(month);
        buf.append(day);
 
        return buf.toString();
    }
 
    /**
     * 현재 시간 - HHMISS
     */
    public static String getTime() {
        String hour, min, sec;
 
        Calendar cal = Calendar.getInstance(Locale.getDefault());
 
        StringBuffer buf = new StringBuffer();
 
        hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        if (hour.length() == 1)
            hour = "0" + hour;
 
        min = Integer.toString(cal.get(Calendar.MINUTE));
        if (min.length() == 1)
            min = "0" + min;
 
        sec = Integer.toString(cal.get(Calendar.SECOND));
        if (sec.length() == 1)
            sec = "0" + sec;
 
        buf.append(hour);
        buf.append(min);
        buf.append(sec);
 
        return buf.toString();
    }
 
    /**
     * 특정날짜에 일자를 더한 값
     * 
     * @param DateTime
     *            YYMMDDHHMMSS
     * @param plusDay
     *            더할 일자
     * @return 특정날짜에 일자를 더한 값
     */
    public static String getAddDay(String DateTime, int plusDay) {
 
        if (DateTime == null)
            return "";
 
        if (DateTime.length() == 8)
            DateTime += "000000";
 
        if (DateTime.equals("99991231")) {
            return "99991231000000";
        }
 
        if (DateTime.equals("99991231235959")) {
            return "99991231235959";
        }
 
        int y = Integer.parseInt(DateTime.substring(0, 4));
        int m = Integer.parseInt(DateTime.substring(4, 6));
        int d = Integer.parseInt(DateTime.substring(6, 8));
 
        java.util.GregorianCalendar sToday = new java.util.GregorianCalendar();
        sToday.set(y, m - 1, d);
        sToday.add(GregorianCalendar.DAY_OF_MONTH, plusDay);
 
        int day = sToday.get(GregorianCalendar.DAY_OF_MONTH);
        int month = sToday.get(GregorianCalendar.MONTH) + 1;
        int year = sToday.get(GregorianCalendar.YEAR);
 
        String sNowyear = String.valueOf(year);
        String sNowmonth = "";
        String sNowday = "";
 
        if (month < 10)
            sNowmonth = "0" + String.valueOf(month);
        else
            sNowmonth = String.valueOf(month);
 
        if (day < 10)
            sNowday = "0" + String.valueOf(day);
        else
            sNowday = String.valueOf(day);
 
        return sNowyear + sNowmonth + sNowday + DateTime.substring(8, 14);
 
    }
 
    /**
     * 특정날짜에 달을 더한 값
     * 
     * @param DateTime
     *            YYMMDDHHMMSS
     * @param plusDay
     *            더할 일자
     * @return 특정날짜에 일자를 더한 값
     */
    public static String getAddMonth(String DateTime, int plusMonth) {
 
        if (DateTime == null)
            return "";
 
        if (DateTime.length() == 8)
            DateTime += "000000";
 
        if (DateTime.equals("99991231")) {
            return "99991231000000";
        }
 
        if (DateTime.equals("99991231235959")) {
            return "99991231235959";
        }
 
        int y = Integer.parseInt(DateTime.substring(0, 4));
        int m = Integer.parseInt(DateTime.substring(4, 6));
        int d = Integer.parseInt(DateTime.substring(6, 8));
 
        java.util.GregorianCalendar sToday = new java.util.GregorianCalendar();
        sToday.set(y, m - 1, d);
        sToday.add(GregorianCalendar.MONTH, plusMonth);
 
        int day = sToday.get(GregorianCalendar.DAY_OF_MONTH);
        int month = sToday.get(GregorianCalendar.MONTH) + 1;
        int year = sToday.get(GregorianCalendar.YEAR);
 
        String sNowyear = String.valueOf(year);
        String sNowmonth = "";
        String sNowday = "";
 
        if (month < 10)
            sNowmonth = "0" + String.valueOf(month);
        else
            sNowmonth = String.valueOf(month);
 
        if (day < 10)
            sNowday = "0" + String.valueOf(day);
        else
            sNowday = String.valueOf(day);
 
        return sNowyear + sNowmonth + sNowday + DateTime.substring(8, 14);
 
    }
 
    /**
     * 어제 날짜 - YYYYMMDD
     */
    public static String getYesterday() {
        java.util.GregorianCalendar sToday = new java.util.GregorianCalendar();
        sToday.add(GregorianCalendar.DAY_OF_MONTH, -1);
 
        int day = sToday.get(GregorianCalendar.DAY_OF_MONTH);
        int month = sToday.get(GregorianCalendar.MONTH) + 1;
        int year = sToday.get(GregorianCalendar.YEAR);
 
        String sNowyear = String.valueOf(year);
        String sNowmonth = "";
        String sNowday = "";
 
        if (month < 10)
            sNowmonth = "0" + String.valueOf(month);
        else
            sNowmonth = String.valueOf(month);
 
        if (day < 10)
            sNowday = "0" + String.valueOf(day);
        else
            sNowday = String.valueOf(day);
 
        return sNowyear + sNowmonth + sNowday;
    }
 
    /**
     * 내일 날짜 - YYYYMMDD
     */
    public static String getTomorrow() {
        java.util.GregorianCalendar sToday = new java.util.GregorianCalendar();
        sToday.add(GregorianCalendar.DAY_OF_MONTH, 1);
 
        int day = sToday.get(GregorianCalendar.DAY_OF_MONTH);
        int month = sToday.get(GregorianCalendar.MONTH) + 1;
        int year = sToday.get(GregorianCalendar.YEAR);
 
        String sNowyear = String.valueOf(year);
        String sNowmonth = "";
        String sNowday = "";
 
        if (month < 10)
            sNowmonth = "0" + String.valueOf(month);
        else
            sNowmonth = String.valueOf(month);
 
        if (day < 10)
            sNowday = "0" + String.valueOf(day);
        else
            sNowday = String.valueOf(day);
 
        return sNowyear + sNowmonth + sNowday;
    }
 
    /**
     * 년도 1900 - 9999, 월 01 - 12, 일 01 - 31, 시 00 - 23, 분 00 - 59, 초 00 - 59
     * 
     * @param param
     *            검사 문자열
     * 
     * @return 검사결과
     */
    public static boolean isDate(String param) {
        if (param == null || param.length() != 8)
            return false;
 
        try {
            int year = Integer.parseInt(param.substring(0, 4));
            int month = Integer.parseInt(param.substring(4, 6));
            int day = Integer.parseInt(param.substring(6, 8));
 
            if (year < 1900 || year > 9999)
                return false;
            if (month < 1 || month > 12)
                return false;
            if (day < 1 || day > 31)
                return false;
 
            return true;
        } catch (Exception e) {
            return false;
        }
    }
 
    /**
     * 년도 1900 - 9999, 월 01 - 12, 일 01 - 31, 시 00 - 23, 분 00 - 59, 초 00 - 59
     * 
     * @param param
     *            검사 문자열
     * 
     * @return 검사결과
     */
    public static boolean isTime(String param) {
        if (param == null || param.length() != 6)
            return false;
 
        try {
            int hour = Integer.parseInt(param.substring(0, 2));
            int min = Integer.parseInt(param.substring(2, 4));
            int sec = Integer.parseInt(param.substring(4, 6));
 
            if (hour < 0 || hour > 23)
                return false;
            if (min < 0 || min > 59)
                return false;
            if (sec < 0 || sec > 59)
                return false;
 
            return true;
        } catch (Exception e) {
            return false;
        }
    }
 
    /**
     * 현재년월에서 다음 한달후 년월을 불러온다.
     * 
     * @param month
     *            YYYY-MM 타입으로 년월
     * @return 년월을 String으로 리턴한다.
     */
    public static String getNextMonth(String month) {
        String lsYear = null;
        String lsMonth = null;
 
        int liYear = Integer.parseInt(month.substring(0, 4));
        int liMonth = Integer.parseInt(month.substring(5, 7));
 
        if (liMonth == 12) {
            liMonth = 1;
            liYear++;
        } else
            liMonth++;
 
        lsYear = liYear + "";
 
        if (liMonth < 10)
            lsMonth = "0" + liMonth;
        else
            lsMonth = "" + liMonth;
 
        return lsYear + "-" + lsMonth;
    }// end of getNextMonth
 
    /**
     * 현재년월에서 이전 한달전 년월을 불러온다.
     * 
     * @param Month
     *            YYYYMM 타입으로 년월
     * @return 년월을 String으로 리턴한다.
     */
    public static String getPrevMonth(String Month) {
        String lsYear = null;
        String lsMonth = null;
 
        int liYear = Integer.parseInt(Month.substring(0, 4));
        int liMonth = Integer.parseInt(Month.substring(5, 7));
 
        if (liMonth == 1) {
            liMonth = 12;
            liYear--;
        } else
            liMonth--;
 
        lsYear = liYear + "";
 
        if (liMonth < 10)
            lsMonth = "0" + liMonth;
        else
            lsMonth = liMonth + "";
 
        return lsYear + "-" + lsMonth;
 
    }// end of getPrevMonth
 
    /**
     * @param date
     *            YYYY-MM-DD 포멧이나 YYYY-DD 포멧의 날짜
     * @return 해당 달의 마지막 날
     */
    public static String getLastDay(String date) {
        return getLastDay(Integer.parseInt(date.substring(0, 4)), Integer
                .parseInt(date.substring(5, 7)), false);
    }
 
    /**
     * @param yyyy
     *            년
     * @param mm
     *            월
     * @return 해당 달의 마지막 날
     */
    public static String getLastDay(String yyyy, String mm) {
        return getLastDay(Integer.parseInt(yyyy), Integer.parseInt(mm), false);
    }
 
    /**
     * @param yyyy
     *            년
     * @param mm
     *            월
     * @param isNowDate
     *            - 구하려는 달이 현재달일 경우 현재 날짜를 리턴할지
     * @return 해당 달의 마지막 날
     */
    public static String getLastDay(String yyyy, String mm, boolean isNowDate) {
        return getLastDay(Integer.parseInt(yyyy), Integer.parseInt(mm),
                isNowDate);
    }
 
    /**
     * @param yyyy
     *            년
     * @param mm
     *            월
     * @param isNowDate
     *            - 구하려는 달이 현재달일 경우 현재 날짜를 리턴할지
     * @return 해당 달의 마지막 날
     */
    public static String getLastDay(int yyyy, int mm, boolean isNowDate) {
        Calendar calendar = Calendar.getInstance();
        String str = "";
        if (isNowDate && mm == calendar.get(Calendar.MONTH) + 1) {
            str = calendar.get(Calendar.DATE) + "";
        } else {
            calendar.set(yyyy, mm - 1, 1);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            int date = calendar.get(Calendar.DATE);
            str = date < 10 ? "0" + date : date + "";
        }
        return str;
    }
}
