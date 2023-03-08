package egovframework.coviaccount.approvalline.service;
import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.DicHelper;
import egovframework.baseframework.util.SessionHelper;
import egovframework.coviaccount.approvalline.util.AutoApprover;
import egovframework.coviaccount.approvalline.util.AutoCCBefore;
import egovframework.coviaccount.approvalline.util.AutoCCinfo;



@Service
public class AutoApprovalLineService {
	final String lang = SessionHelper.getSession("lang");
	static final String PUBLIC_COMPANY = "ORGROOT";

	/** 공통 조직도 데이터 구조가 변경되면 반드시 수정해야 함 */
	// 양식 설정의 자동결재선 세팅
	public CoviMap setFormAutoApprovalData(CoviMap autoApprovalLine, String strSessionDNCode, String strSessionRegionCode) throws Exception{
		CoviMap formAutoApprovalData = autoApprovalLine;
		CoviList oCCAfterWorkedApprovalLine = new CoviList();
		CoviList oCCBeforeWorkedApprovalLine = new CoviList();
		CoviList oApprovalWorkedApprovalLine = new CoviList();
		CoviList oAgreeWorkedApprovalLine = new CoviList();
		CoviList oAssistWorkedApprovalLine = new CoviList();
				
		if(formAutoApprovalData != null){
			
			// 자동결재선 - 사후참조
			CoviMap oCCAfter = formAutoApprovalData.getJSONObject("CCAfter");
			oCCAfterWorkedApprovalLine = new AutoCCinfo(oCCAfter, strSessionDNCode, strSessionRegionCode).getApproverLine();
			
			// 자동결재선 - 사전참조
			CoviMap oCCBefore = formAutoApprovalData.getJSONObject("CCBefore");
			oCCBeforeWorkedApprovalLine = new AutoCCBefore(oCCBefore, strSessionDNCode, strSessionRegionCode).getApproverLine();
						
			// 자동결재선 - 결재
			CoviMap oApproval = formAutoApprovalData.getJSONObject("Approval");
			oApprovalWorkedApprovalLine = new AutoApprover(oApproval, strSessionDNCode, strSessionRegionCode).getApproverLine();
						
			// 자동결재선 - 합의
			CoviMap oAgree = formAutoApprovalData.getJSONObject("Agree");
			CoviMap autoAgreeApprovers = getSettedApproversData(oAgree, strSessionDNCode, strSessionRegionCode);
			
			if(autoAgreeApprovers != null){
				// 사용자
				CoviList itemArr = autoAgreeApprovers.getJSONArray("item");
				
				CoviMap oStep = new CoviMap();
				CoviMap oStepTaskinfo = new CoviMap();
				CoviList oArrOU = new CoviList();
				
				for (int i = 0; i < itemArr.size(); i++) {
					if (itemArr.getJSONObject(i).getString("itemType").equals("user")) {
						CoviMap peopleObj = itemArr.getJSONObject(i);

						CoviMap oOU = new CoviMap();
						CoviMap oPerson = createPersonNode(peopleObj);
						CoviMap oPersonTaskinfo = new CoviMap();

						oOU.put("name", peopleObj.optString("RGNM"));
						oOU.put("code", peopleObj.optString("RG"));

						oPersonTaskinfo.put("kind", "normal");
						oPersonTaskinfo.put("result", "inactive");
						oPersonTaskinfo.put("status", "inactive");

						oPerson.put("taskinfo", oPersonTaskinfo);

						oOU.put("person", oPerson);

						if (peopleObj.containsKey("isSerial")
								&& peopleObj.optString("isSerial").equals("Y")) {
							oStep.put("name", "순차합의");
							oStep.put("unittype", "person");
							oStep.put("routetype", "consult");
							oStep.put("allottype", "serial");

							oStepTaskinfo.put("kind", "normal");
							oStepTaskinfo.put("result", "inactive");
							oStepTaskinfo.put("status", "inactive");

							oStep.put("taskinfo", oStepTaskinfo);
							oStep.put("ou", oOU);
							oAgreeWorkedApprovalLine.add(oStep);
						} else {
							oArrOU.add(oOU);
						}
					}
				}
				
				oStep.put("name", "병렬합의");
				oStep.put("unittype", "person");
				oStep.put("routetype", "consult");
				oStep.put("allottype", "parallel");
				
				oStepTaskinfo.put("kind", "normal");
				oStepTaskinfo.put("result", "inactive");
				oStepTaskinfo.put("status", "inactive");
				
				oStep.put("taskinfo", oStepTaskinfo);
				oStep.put("ou", oArrOU);
				oAgreeWorkedApprovalLine.add(oStep);
			}
			
			// 자동결재선 - 협조
			CoviMap oAssist = formAutoApprovalData.getJSONObject("Assist");
			CoviMap autoAssistApprovers = getSettedApproversData(oAssist, strSessionDNCode, strSessionRegionCode);

			if(autoAssistApprovers != null){
				// 사용자
				CoviList itemArr = autoAssistApprovers.getJSONArray("item");
				
				CoviMap oStep = new CoviMap();
				CoviMap oStepTaskinfo = new CoviMap();
				CoviList oArrOU = new CoviList();

				for (int i = 0; i < itemArr.size(); i++) {
					if (itemArr.getJSONObject(i).getString("itemType").equals("user")) {
						CoviMap peopleObj = itemArr.getJSONObject(i);

						CoviMap oOU = new CoviMap();
						CoviMap oPerson = createPersonNode(peopleObj);
						CoviMap oPersonTaskinfo = new CoviMap();

						oOU.put("name", peopleObj.optString("RGNM"));
						oOU.put("code", peopleObj.optString("RG"));

						oPersonTaskinfo.put("kind", "normal");
						oPersonTaskinfo.put("result", "inactive");
						oPersonTaskinfo.put("status", "inactive");

						oPerson.put("taskinfo", oPersonTaskinfo);

						oOU.put("person", oPerson);

						if (peopleObj.containsKey("isSerial")
								&& peopleObj.optString("isSerial").equals("Y")) {
							oStep.put("name", "순차협조");
							oStep.put("unittype", "person");
							oStep.put("routetype", "assist");
							oStep.put("allottype", "serial");

							oStepTaskinfo.put("kind", "normal");
							oStepTaskinfo.put("result", "inactive");
							oStepTaskinfo.put("status", "inactive");

							oStep.put("taskinfo", oStepTaskinfo);
							oStep.put("ou", oOU);
							oAssistWorkedApprovalLine.add(oStep);
						} else {
							oArrOU.add(oOU);
						}
					}
				}

				oStep.put("name", "병렬협조");
				oStep.put("unittype", "person");
				oStep.put("routetype", "assist");
				oStep.put("allottype", "parallel");

				oStepTaskinfo.put("kind", "normal");
				oStepTaskinfo.put("result", "inactive");
				oStepTaskinfo.put("status", "inactive");

				oStep.put("taskinfo", oStepTaskinfo);
				oStep.put("ou", oArrOU);
				oAssistWorkedApprovalLine.add(oStep);
			}
			
			//자동결재선 - 사후참조
			if(!oCCAfterWorkedApprovalLine.isEmpty()){
				CoviMap returnCCAfter = new CoviMap();
				returnCCAfter.put("ccinfo", oCCAfterWorkedApprovalLine);
				oCCAfter.put("WorkedApprovalLine", returnCCAfter);
				autoApprovalLine.put("CCAfter", oCCAfter);
			}
			
			//자동결재선 - 사전참조
			if(!oCCBeforeWorkedApprovalLine.isEmpty()){
				CoviMap returnCCBefore = new CoviMap();
				returnCCBefore.put("ccinfo", oCCBeforeWorkedApprovalLine);
				oCCBefore.put("WorkedApprovalLine", returnCCBefore);
				autoApprovalLine.put("CCBefore", oCCBefore);
			}
			
			//자동결재선 - 결재자
			if(!oApprovalWorkedApprovalLine.isEmpty()){
				CoviMap returnApproval = new CoviMap();
				returnApproval.put("step", oApprovalWorkedApprovalLine);
				oApproval.put("WorkedApprovalLine", returnApproval);
				autoApprovalLine.put("Approval", oApproval);
			}
			
			//자동결재선 - 합의자
			if(!oAgreeWorkedApprovalLine.isEmpty()){
				CoviMap returnAgree = new CoviMap();
				returnAgree.put("step", oAgreeWorkedApprovalLine);
				oAgree.put("WorkedApprovalLine", returnAgree);
				autoApprovalLine.put("Agree", oAgree);
			}
			
			//자동결재선 - 협조자
			if(!oAssistWorkedApprovalLine.isEmpty()){
				CoviMap returnAssist = new CoviMap();
				returnAssist.put("step", oAssistWorkedApprovalLine);
				oAssist.put("WorkedApprovalLine", returnAssist);
				autoApprovalLine.put("Assist", oAssist);
			}
		}
		
		return autoApprovalLine;
	}

	private CoviMap getSettedApproversData(CoviMap settedApproversByType, String strSessionDNCode, String strSessionRegionCode) {
		CoviMap autoApprovers = null;
		
		// 계열사별
		if(settedApproversByType.getString("autoType").equals("E")){
			autoApprovers = getDocAutoApprovalForEnt(settedApproversByType.getJSONObject("DocAutoApprovalEnt"), strSessionDNCode);
		}
		// 사업장별
		else if(settedApproversByType.getString("autoType").equals("R")){
			autoApprovers = getDocAutoApprovalForReg(settedApproversByType.getJSONObject("DocAutoApprovalReg"), strSessionRegionCode); 
		}
		return autoApprovers;
	}

	private CoviMap getDocAutoApprovalForReg(CoviMap docAutoApprovalReg, String regionCode) {
		CoviMap autoApprovers = null;

		if(docAutoApprovalReg.has(regionCode)) {
			CoviMap autoApprovalDataForReg = docAutoApprovalReg.getJSONObject(regionCode);
			if(autoApprovalDataForReg.getString("isUse").equals("Y") && !autoApprovalDataForReg.getJSONArray("item").isEmpty()){
				autoApprovers = docAutoApprovalReg.getJSONObject(regionCode);
			}
		}
		
		return autoApprovers;
	}
	
	public CoviMap getDocAutoApprovalForEnt(CoviMap docAutoApprovalEnt, String entCode) {
		CoviMap autoApprovers = null;
		
		if(docAutoApprovalEnt.has(entCode)) {
			CoviMap autoApprovalDataForEnt = docAutoApprovalEnt.getJSONObject(entCode);
			if(autoApprovalDataForEnt.getString("isUse").equals("Y") && !autoApprovalDataForEnt.getJSONArray("item").isEmpty()){
				autoApprovers = docAutoApprovalEnt.getJSONObject(entCode);
			}
		}
	
		if(autoApprovers == null) {
			CoviMap publicApprovers = docAutoApprovalEnt.getJSONObject(PUBLIC_COMPANY);
			if(!publicApprovers.isNullObject() && publicApprovers.getString("isUse").equals("Y") 
				&& !publicApprovers.getJSONArray("item").isEmpty()){
				autoApprovers = docAutoApprovalEnt.getJSONObject(PUBLIC_COMPANY);
			}
		}
		
		return autoApprovers;
	}

	public CoviMap createPersonNode(CoviMap peopleObj) {
		CoviMap personObject = new CoviMap();
		String titleCode  = "";
		String titleName  = "";
		String levelCode  = "";
		String levelName  = "";
		String positionCode  = "";
		String positionName  = "";
		
		/* 공통 조직도에서 구분값이 & > | 로 변경되어 수정 (tl,lv,po 소문자는 구분자가 ; 로 전자결재에서 사용하는 값)
		// 직위,직급,직책 정보 없는 경우 대비하여 수정
		if(peopleObj.optString("TL").split("&").length > 1) {
			titleCode  = peopleObj.optString("TL").split("&")[0];
			titleName  = DicHelper.getDicInfo(peopleObj.optString("TL").split("&")[1], lang);
		}
		if(peopleObj.optString("LV").split("&").length > 1) {
			levelCode  = peopleObj.optString("LV").split("&")[0];
			levelName  = DicHelper.getDicInfo(peopleObj.optString("LV").split("&")[1], lang);
		}
		if(peopleObj.optString("PO").split("&").length > 1) {
			positionCode  = peopleObj.optString("PO").split("&")[0];
			positionName  = DicHelper.getDicInfo(peopleObj.optString("PO").split("&")[1], lang);
		}
		*/
		
		personObject.put("title", peopleObj.optString("tl"));				// 직책
		personObject.put("level", peopleObj.optString("lv"));				// 직급
		personObject.put("name", peopleObj.getString("DN"));
		personObject.put("oucode", peopleObj.getString("RG"));
		personObject.put("ouname", peopleObj.getString("RGNM"));
		personObject.put("code", peopleObj.getString("AN"));
		personObject.put("position", peopleObj.optString("po"));			// 직위
		personObject.put("sipaddress", peopleObj.getString("EM"));
		
		return personObject;
	}
}
