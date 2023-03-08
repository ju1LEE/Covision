package egovframework.covision.groupware.portal.admin.service.impl;

import javax.annotation.Resource;



import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.coviframework.util.ComUtils;
import egovframework.covision.groupware.portal.admin.service.WebpartManageSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("webpartManageService")
public class WebpartManageSvcImpl extends EgovAbstractServiceImpl implements WebpartManageSvc {

	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Override
	public CoviMap getWebpartList(CoviMap params) throws Exception {
		CoviMap returnList = new CoviMap();
		CoviMap page = new CoviMap();
		
		int cnt = (int) coviMapperOne.getNumber("admin.portal.selectWebpartListCnt", params);
		page = ComUtils.setPagingData(params,cnt);
		params.addAll(page);
		
		CoviList list = coviMapperOne.list("admin.portal.selectWebpartList", params);
		returnList.put("list", CoviSelectSet.coviSelectJSON(list,"WebpartID,CompanyName,BizSectionName,DisplayName,Description,Range,RegisterName,IsUse,RegistDate"));
		returnList.put("page",page);
		
		return returnList;
	}
	
/*	@Override
	public CoviMap getCompanyList() throws Exception {
		String lang = SessionHelper.getSession("lang");		
		CoviMap resultList = new CoviMap();
		
		
		CoviList list = coviMapperOne.list("admin.portal.selectCompanyList",new CoviMap());
		CoviList companyList= CoviSelectSet.coviSelectJSON(list, "optionText,optionValue");
		
		for(int i = 0 ; i<companyList.size() ; i++){
			String langData = DicHelper.getDicInfo(companyList.getJSONObject(i).getString("optionText"), lang);
			companyList.getJSONObject(i).put("optionText", langData);
		}
		
		CoviMap item = new CoviMap();
		item.put("optionValue", "");
		item.put("optionText", DicHelper.getDic("lbl_all") );
		companyList.add(0,item);
		
		resultList.put("list",companyList);
		
		return resultList;
	}*/

	@Override
	public int insertWebpartData(CoviMap params) throws Exception {
		//웹파트 정보 데이터 추가
		 int retCnt = coviMapperOne.insert("admin.portal.insertWebpartData",params);
	
	/*	//다국어 데이터 추가
		String dicCode = "WP_"+params.getString("webpartID");
		String ko="", en="", ja="", zh="";
		
		try { ko = params.getString("dicWebpartName").split(";")[0]; } catch(Exception e) { ko="";}
		try { en = params.getString("dicWebpartName").split(";")[1]; } catch(Exception e) { en=""; }
		try { ja = params.getString("dicWebpartName").split(";")[2]; } catch(Exception e) { ja=""; }
		try { zh = params.getString("dicWebpartName").split(";")[3]; } catch(Exception e) { zh=""; }
		
		CoviMap dicParam = new CoviMap();
		dicParam.put("dicCode",dicCode);
		dicParam.put("ko", ko);
		dicParam.put("en", en);
		dicParam.put("ja", ja);
		dicParam.put("zh", zh);
		dicParam.put("userCode",params.getString("registerCode"));
		
		
		int cnt = (int) coviMapperOne.getNumber("admin.portal.selectDictionaryData", dicParam);
		
		if(cnt>0){
			retCnt += coviMapperOne.update("admin.portal.updateWebpartDictionary", dicParam);
		}else{
			retCnt += coviMapperOne.insert("admin.portal.insertWebpartDictionary", dicParam);
		}*/
		
		return retCnt;
	}
	
	@Override
	public int chnageWebpartIsUse(CoviMap params) throws Exception {
		int retCnt = 0;
		
		retCnt += coviMapperOne.update("admin.portal.updateWebpartIsUse",params);
		
		return retCnt;
	}

	@Override
	public int deleteWebpartData(CoviMap delParam) throws Exception {
		int retCnt = 0;	
		
		retCnt += coviMapperOne.delete("admin.portal.deleteWebpartData",delParam);
		
		//retCnt += coviMapperOne.delete("admin.portal.deleteWebpartDictionary",delParam);
		
		return retCnt;
	}

	@Override
	public CoviMap getWebpartData(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		
		CoviList list = coviMapperOne.list("admin.portal.selectWebpartData",params);
		resultList.put("list",CoviSelectSet.coviSelectJSON(list,"WebpartID,CompanyCode,BizSection,Range,DisplayName,MultiDisplayName,HtmlFilePath,JsFilePath,JsModuleName,Preview,Resource,ScriptMethod,MinHeight,DataJSON,ExtentionJSON,IsUse,Description,Thumbnail"));
		
		return resultList;
	}

	@Override
	public int updateWebpartData(CoviMap params) throws Exception {
		//웹파트 정보 데이터 수정
	
	/*	//다국어 데이터 수정
		String dicCode = "WP_"+params.getString("webpartID");
		String ko="", en="", ja="", zh="";
		
		try { ko = params.getString("dicWebpartName").split(";")[0]; } catch(Exception e) { }
		try { en = params.getString("dicWebpartName").split(";")[1]; } catch(Exception e) { }
		try { ja = params.getString("dicWebpartName").split(";")[2]; } catch(Exception e) { }
		try { zh = params.getString("dicWebpartName").split(";")[3]; } catch(Exception e) { }
		
		CoviMap dicParam = new CoviMap();
		dicParam.put("dicCode",dicCode);
		dicParam.put("ko", ko);
		dicParam.put("en", en);
		dicParam.put("ja", ja);
		dicParam.put("zh", zh);
		dicParam.put("userCode",params.getString("registerCode"));
		
		
		int cnt = (int) coviMapperOne.getNumber("admin.portal.selectDictionaryData", dicParam);
		
		if(cnt>0){
			retCnt += coviMapperOne.update("admin.portal.updateWebpartDictionary", dicParam);
		}else{
			retCnt += coviMapperOne.insert("admin.portal.insertWebpartDictionary", dicParam);
		}*/
		
		return coviMapperOne.update("admin.portal.updateWebpartData",params);
	}

	@Override
	public int selectDuplJsModuleName(CoviMap params) throws Exception {
		return (coviMapperOne.selectOne("admin.portal.selectDuplJsModuleName", params));
	}
}
