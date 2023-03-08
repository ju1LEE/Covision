/**
 * @Class Name : CoviFlowDocNumberHelper.java
 * @Description : 
 * @Modification Information 
 * @ 2017.02.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2017. 02.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;


public class CoviFlowDocNumberHelper {

	public static String issueDocNumber(int serialNumber, String docNumberType, String initiatorUnitID, String initiatorUnitName, 
			String shortUnitName, String fiscalYear, String catagoryCode, String entName){
		
		//기본 : 일련번호 4개
		String formattedSerialNumber = String.format("%04d", serialNumber);
		String displayNumber = initiatorUnitName + "-" + fiscalYear + "-" + formattedSerialNumber;
		
		//일련번호 5개 출력할 양식
		if(docNumberType.equals("10")) {
			formattedSerialNumber =  String.format("%05d", serialNumber);
		}
		
		switch(docNumberType){
			case "1": //부서약어:분류번호-일련번호(4)
				displayNumber = shortUnitName + ":" + catagoryCode + "-" + formattedSerialNumber;
				break;
			case "2": //부서약어-일련번호(4)
				displayNumber = shortUnitName + "-" + formattedSerialNumber;
				break;
			case "3": //부서약어YYYY-일련번호(4)
				displayNumber = shortUnitName + fiscalYear + "-" + formattedSerialNumber;
				break;
			case "4": //부서약어 분류번호(숫자)-일련번호(4)
				displayNumber = shortUnitName + " " + catagoryCode + "-" + formattedSerialNumber;
				break;
			case "5": //부서명-YYMM-일련번호(4)
				displayNumber = shortUnitName + "-" + fiscalYear.substring(2, 4) + CoviFlowDateHelper.getMonth().substring(4, 6) + "-" + formattedSerialNumber;
				break;
			case "6": //부서명-YYYY-일련번호(4)	
				displayNumber = shortUnitName + "-" + fiscalYear + "-" + formattedSerialNumber;
				break;
			case "7": //부서약어  YYYY-일련번호(4)
				displayNumber = shortUnitName + " " + fiscalYear + "-" + formattedSerialNumber;
				break;
			case "8": //회사명-일련번호(4)
				displayNumber = entName + "-" + formattedSerialNumber;
				break;
			case "9": //부서약어 제 YY - 일련번호(4)호
				displayNumber = shortUnitName + " 제 " + fiscalYear.substring(2, 4) + " - " + formattedSerialNumber + "호";
				break;
			case "10": //부서코드 YY-일련번호(5)
				displayNumber = initiatorUnitID + " " + fiscalYear.substring(2, 4) + "-" + formattedSerialNumber;
				break;
			case "11": //회사명-YYYY-일련번호(4)
				displayNumber = entName + "-" + fiscalYear + "-" + formattedSerialNumber;
				break;
			case "12": //문서분류 - YYYY - 일련번호(4)
				displayNumber = catagoryCode + "-" +fiscalYear + "-" + formattedSerialNumber;
				break;
		}
		
		return displayNumber;
	}


}
