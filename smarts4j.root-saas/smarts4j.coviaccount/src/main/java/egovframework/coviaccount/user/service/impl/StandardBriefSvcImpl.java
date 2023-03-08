package egovframework.coviaccount.user.service.impl;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.util.SessionHelper;
import egovframework.coviaccount.common.util.AccountExcelUtil;
import egovframework.coviaccount.common.util.AccountUtil;
import egovframework.coviaccount.user.service.StandardBriefSvc;
import egovframework.coviframework.util.ComUtils;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;


@Service("StandardBriefSvc")
public class StandardBriefSvcImpl extends EgovAbstractServiceImpl implements StandardBriefSvc {

	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;

	@Autowired
	private AccountExcelUtil accountExcelUtil;
	
	/**
	 * @Method Name : getStandardBrieflist
	 * @Description : 표준적요 목록 조회
	 */
	@Override
	public CoviMap getStandardBrieflist(CoviMap params) throws Exception {
		CoviMap resultList	= new CoviMap();
		CoviMap page			= new CoviMap();
		CoviList list			= new CoviList();
		
		int cnt			= 0;
		int pageNo		= Integer.parseInt(params.get("pageNo").toString());
		int pageSize	= Integer.parseInt(params.get("pageSize").toString());
		int pageOffset	= (pageNo - 1) * pageSize;
		
		params.put("pageNo",		pageNo);
		params.put("pageSize",		pageSize);
		params.put("pageOffset",	pageOffset);

		cnt		= (int) coviMapperOne.getNumber("account.standardBrief.getStandardBrieflistCnt" , params);
		
		page 	= ComUtils.setPagingData(params,cnt);
		params.addAll(page);
		
		list	= coviMapperOne.list("account.standardBrief.getStandardBriefList", params);
		
		//page	= accountUtil.listPageCount(cnt,params);
		
		resultList.put("list",	AccountUtil.convertNullToSpace(list));
		resultList.put("page",	page);
		
		return resultList; 
	}
	
	/**
	 * @Method Name : getStandardBriefDetail
	 * @Description : 표준적요 상세 조회
	 */
	@Override
	public CoviMap getStandardBriefDetail(CoviMap params) throws Exception {

		CoviMap resultList	= new CoviMap();
		CoviList list			= new CoviList();

		list = coviMapperOne.list("account.standardBrief.getStandardBriefDetail",	params);
		resultList.put("list",	AccountUtil.convertNullToSpace(list));
		return resultList; 
	}
	
	/**
	 * @Method Name : saveStandardBriefInfo
	 * @Description : 표준적요 저장
	 */
	@Override
	public CoviMap saveStandardBriefInfo(CoviMap params)throws Exception {
		CoviMap resultList	= new CoviMap();
		ArrayList<Map<String, Object>> infoList	= (ArrayList<Map<String, Object>>) params.get("infoArr");
		ArrayList<Map<String, Object>> delList	= (ArrayList<Map<String, Object>>) params.get("delArr");
		params.put("UR_Code", SessionHelper.getSession("UR_Code"));
		
		String nameSql	= "'";
		if(!infoList.isEmpty()){
			StringBuilder buf = new StringBuilder();
			for(int i=0;i<infoList.size();i++){
				CoviMap infoListParam		= new CoviMap(infoList.get(i));
				String standardBriefName	= infoListParam.get("standardBriefName")	== null ? "" : infoListParam.get("standardBriefName").toString();
				//nameSql	+= standardBriefName	+ "','";
				buf.append(standardBriefName).append(",");
			}
			nameSql += buf.toString();
			
			int cnt = 0;
			if(nameSql.equals("")){
				cnt = 0;
			}else{
				CoviMap chkParam = new CoviMap();
				String accountID	= params.get("accountID") == null ? "" : params.get("accountID").toString();
				String companyCode	= params.get("companyCode") == null ? "" : params.get("companyCode").toString();
				String subStr		= nameSql.substring(0,nameSql.length()-1);
				nameSql	= subStr;
				
				chkParam.put("accountID",	accountID);
				if(nameSql != null && !nameSql.equals("")) chkParam.put("nameSql", nameSql.split(","));
				chkParam.put("companyCode",		companyCode);
				
				cnt = (int) coviMapperOne.getNumber("account.standardBrief.chkStandardBriefNameCnt" , chkParam);
			}
			
			if(cnt == 0){
				for(int i=0;i<infoList.size();i++){
					CoviMap infoListParam = new CoviMap(infoList.get(i));
					
					String standardBriefID		= infoListParam.get("standardBriefID")		== null ? "" : infoListParam.get("standardBriefID").toString();
					String standardBriefName	= infoListParam.get("standardBriefName")	== null ? "" : infoListParam.get("standardBriefName").toString();
					String standardBriefDesc	= infoListParam.get("standardBriefDesc")	== null ? "" : infoListParam.get("standardBriefDesc").toString();
					String isUse				= infoListParam.get("isUse")				== null ? "" : infoListParam.get("isUse").toString();
					String isUseSimp			= infoListParam.get("isUseSimp")			== null ? "" : infoListParam.get("isUseSimp").toString();
					String ctrlCode				= infoListParam.get("ctrlCode")				== null ? "" : infoListParam.get("ctrlCode").toString();
					String isfile				= infoListParam.get("isfile")				== null ? "" : infoListParam.get("isfile").toString();
					String isdocLink			= infoListParam.get("isdocLink")			== null ? "" : infoListParam.get("isdocLink").toString();
					
					params.put("standardBriefID",	standardBriefID);
					params.put("standardBriefName",	standardBriefName);
					params.put("standardBriefDesc",	standardBriefDesc);
					params.put("isUse",				isUse);
					params.put("isUseSimp",			isUseSimp);
					params.put("ctrlCode",			ctrlCode);
					params.put("isfile",			isfile);
					params.put("isdocLink",			isdocLink);
					
					if(standardBriefID.equals("")){
						insertStandardBriefInfo(params);
					}else{
						updateStandardBriefInfo(params);	
					}
				}
				updateAccountManageInfoByStandardBrief(params);
			}else{
				resultList.put("err", "standardBriefName");
			}
		}
		
		if(!delList.isEmpty()){
			for(int i=0;i<delList.size();i++){
				CoviMap delListParam = new CoviMap(delList.get(i));
				
				String standardBriefID	= delListParam.get("standardBriefID")	== null ? "" : delListParam.get("standardBriefID").toString();
				params.put("standardBriefID",	standardBriefID);
				deleteStandardBriefInfo(params);
			}
			updateAccountManageInfoByStandardBrief(params);
		}
		
		return resultList;
	}
	
	/**
	 * @Method Name : saveTaxTypeInfo
	 * @Description : 표준적요 세금 유형 저장
	 */
	@Override
	public CoviMap saveTaxTypeInfo(CoviMap params)throws Exception {
		CoviMap resultList	= new CoviMap();
		updateTaxTypeInfo(params);
		return resultList;
	}
	
	/**
	 * @Method Name : insertStandardBriefInfo
	 * @Description : 표준적요 Insert
	 */
	public void insertStandardBriefInfo(CoviMap params)throws Exception {
		coviMapperOne.insert("account.standardBrief.insertStandardBriefInfo", params);
	}
	
	/**
	 * @Method Name : updateStandardBriefInfo
	 * @Description : 표준적요 Update
	 */
	public void updateStandardBriefInfo(CoviMap params)throws Exception {
		coviMapperOne.update("account.standardBrief.updateStandardBriefInfo", params);
	}
	
	/**
	 * @Method Name : updateTaxTypeInfo
	 * @Description : 표준적요 세금 유형 Update
	 */
	public void updateTaxTypeInfo(CoviMap params)throws Exception {
		coviMapperOne.update("account.standardBrief.updateTaxTypeInfo", params);
	}
	
	/**
	 * @Method Name : deleteStandardBriefInfoByAccountID
	 * @Description : 표준적요 삭제
	 */
	@Override
	public CoviMap deleteStandardBriefInfoByAccountID(CoviMap params)throws Exception {
		CoviMap resultList = new CoviMap();
		
		String deleteStr = params.get("deleteSeq") == null ? "" : params.get("deleteSeq").toString();
		if(!deleteStr.equals("")){
			String[] deleteArr = deleteStr.split(",");
			for(int i = 0; i < deleteArr.length; i++){
				CoviMap sqlParam	= new CoviMap();
				sqlParam.put("accountID", deleteArr[i]);
				deleteStandardBriefByAccountID(sqlParam);
				updateAccountManageInfoByStandardBrief(sqlParam);
			}
		}
		return resultList;
	}
	
	/**
	 * @Method Name : deleteStandardBriefByAccountID
	 * @Description : 표준적요 Delete by Account ID
	 */
	public void deleteStandardBriefByAccountID(CoviMap params)throws Exception {
		coviMapperOne.delete("account.standardBrief.deleteStandardBriefByAccountID", params);
	}
	
	/**
	 * @Method Name : deleteStandardBriefInfo
	 * @Description : 표준적요 Delete
	 */
	public void deleteStandardBriefInfo(CoviMap params)throws Exception {
		coviMapperOne.delete("account.standardBrief.deleteStandardBriefInfo", params);
	}
	
	/**
	 * @Method Name : updateAccountManageInfoByStandardBrief
	 * @Description : 표준적요의 값을 계정관리 정보에 적용
	 */
	public void updateAccountManageInfoByStandardBrief(CoviMap params)throws Exception {
		coviMapperOne.update("account.standardBrief.updateAccountManageInfoByStandardBrief",		params);
		coviMapperOne.update("account.standardBrief.updateAccountManageInfoByStandardBriefNull",	params);
	}
	
	/**
	 * @Method Name : standardBriefExcelDownload
	 * @Description : 표준적요 엑셀 다운로드
	 */
	@Override
	public CoviMap standardBriefExcelDownload(CoviMap params) throws Exception{
		CoviMap resultList = new CoviMap();
		CoviList list		= coviMapperOne.list("account.standardBrief.getStandardBriefExcelList", params);
		int cnt				= (int) coviMapperOne.getNumber("account.standardBrief.getStandardBrieflistCnt" , params);
		String headerKey	= params.get("headerKey").toString();
		resultList.put("list",	accountExcelUtil.selectJSONForExcel(list,headerKey));
		resultList.put("cnt",	cnt);
		return resultList;
	}
	
	/**
	 * @Method Name : standardBriefExcelUpload
	 * @Description : 표준적요 엑셀 업로드
	 */
	@Override
	public CoviMap standardBriefExcelUpload(CoviMap params) throws Exception {
		int rtn = 0;
		int cnt = 0;
		CoviMap resultList	= new CoviMap();
		ArrayList<ArrayList<Object>> dataList = accountExcelUtil.extractionExcelData(params, 1);	// 엑셀 데이터 추출
		params.put("UR_Code", SessionHelper.getSession("UR_Code"));
		
		String accountCode ="";
		String accountID = "";
		String companyCode ="";
		CoviMap map = new CoviMap();
		CoviMap chkParam = new CoviMap();
		if(dataList == null || dataList.isEmpty()) {
			return resultList;
		}
		StringBuilder subStr = new StringBuilder("");
		StringBuilder aidStr = new StringBuilder("");
		StringBuilder nameSql = new StringBuilder("");
		
		for (ArrayList<Object> list : dataList){                    // 계정 유무확인
			accountCode = list.get(0) == null ? "" : list.get(0).toString();
			companyCode = list.get(11) == null ? "" : list.get(11).toString();
			if(aidStr.indexOf(accountCode) < 0){
				params.put("accountCode",accountCode);
				params.put("companyCode",companyCode);
				cnt = (int) coviMapperOne.getNumber("account.standardBrief.chkStandardBriefAccountCode" , params);
				
				if(cnt == 0 ){
					resultList.put("err", "accountCode");
					return resultList; 
				}
				
				aidStr.append(accountCode + ",");
				map = coviMapperOne.selectOne("account.standardBrief.getStandardBriefAccountID" , params);
				accountID = map.get("AccountID") == null ? "" : map.get("AccountID").toString();
				subStr.append(accountID +",");
				
			}
		}
		
		for (ArrayList<Object> list : dataList) {
			if(Boolean.TRUE.equals(AccountUtil.checkNull(list.get(2).toString()))){
				resultList.put("err","excelStandardBriefName");
				return resultList;
			}
			String starndardBriefName = list.get(2) == null ? "" : list.get(2).toString();
			nameSql.append(starndardBriefName + ";");
		}
		
		
		String newStr = nameSql.substring(0, nameSql.length()-1);
		String acStr = subStr.substring(0, subStr.length()-1);
		String[] chkStr;
		int chkCnt = 0;
		
		chkStr = newStr.split(";");
		
		for (int i = 0; i < chkStr.length; i++){ //엑셀 내 중복 확인
			for (int j = 0; j < chkStr.length; j ++){
				if(!chkStr[i].equals("") && chkStr[i].equals(chkStr[j])){
					chkCnt++;
				}
			}
			if(chkCnt >=2){
				resultList.put("err", "excelStandardBriefName");
				return resultList;
			}else{
				chkCnt = 0;
			}
		}
		
		newStr = newStr.replace(";", ",");
		
		if(!acStr.equals("")) chkParam.put("accountID", acStr.split(","));
		if(!newStr.equals("")) chkParam.put("nameSql", newStr.split(","));
		
		// index | (0) = 계정코드 | (1) = 계정명 | (2) = 표준적요명 | (3) = 표준적요설명 | (4) = 사용여부  | (5) = 간편신청 사용여부
		// 4번 index 미사용 (쿼리안함)
		// 과세정보 갱신 (6) TaxTypeName | (7) TaxCodeName
		rtn = (int) coviMapperOne.getNumber("account.standardBrief.chkStandardBriefNameCntExcel", chkParam); // 중복 체크
			
		if(rtn == 0 ){// 중복 체크
			if(!acStr.equals("")) params.put("accountID", acStr.split(","));
			coviMapperOne.delete("account.standardBrief.deleteStandardBriefByAccountIDExcel", params); 	 // 삭제
			
			for (ArrayList<Object> list : dataList) { // 추가
				accountCode = list.get(0) == null ? "" : list.get(0).toString();
				map = coviMapperOne.selectOne("account.standardBrief.getStandardBriefAccountID" , params);
				accountID = map.get("AccountID") == null ? "" : map.get("AccountID").toString();
				String standardBriefName = list.get(2) == null ? "" : list.get(2).toString();
				String standardBriefDesc = list.get(3) == null ? "" : list.get(3).toString();
				String isUse 			 = list.get(4).equals("예") ? "Y" : list.get(5).equals("아니오") ? "N" : "";
				String isUseSimp 		 = list.get(5).equals("예") ? "Y" : list.get(6).equals("아니오") ? "N" : "";
				
				params.put("accountID", accountID);
				params.put("standardBriefName", standardBriefName);
				params.put("standardBriefDesc", standardBriefDesc);
				params.put("isUse", isUse);
				params.put("isUseSimp", isUseSimp);
					
				insertStandardBriefInfo(params);
			}
			// 과세정보  Update.
			params.put("taxType", dataList.get(0).get(6));
			params.put("taxCode", dataList.get(0).get(8));
			updateAccountManageInfoByStandardBrief(params);
		}else{ 
			resultList.put("err", "standardBriefName");
			return resultList;
		}
		
		return resultList;
	}
}
