/**
 * @Class Name : CoviFlowProcessDAOImpl.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.covision.coviflow.dao.CoviFlowProcessDAO;

/*
 * transaction 설계
 * 참고 - http://www.mybatis.org/spring/ko/sqlsession.html
 * 1. 기본 데이터 처리 sqlsessiontemplate 사용(auto commit)
 * 2. 별도의 트랜잭션 관리가 필요한 경우 프로그래밍적 트랜잭션 관리방법 사용(PlatformTransactionManager)
 * */

/*
 * process table 처리
 * */
public class CoviFlowProcessDAOImpl implements CoviFlowProcessDAO{

	private static final Logger LOG = LoggerFactory.getLogger(CoviFlowProcessDAOImpl.class);
	
	 private SqlSession sqlSession;

	 public void setSqlSession(SqlSession sqlSession) {
		 this.sqlSession = sqlSession;
	 }
	
	/* 테스트용
	@Override
	public List<Map<String, Object>> selectProcess() throws Exception {
		return processDAO.selectProcess();
	}
	*/
	
	@Override
	public void insertErrorLog(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertErrorLog", params);
	}
	
	@Override
	public void insertLegacy(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertLegacy", params);
	}
 
	@Override
	public void insertAppvLine(Map<String, Object> publicParams, Map<String, Object> privateParams) throws Exception {
		
		/*
		ApplicationContext context = egovframework.covision.coviflow.util.CoviApplicationContextUtil.getApplicationContext();
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		PlatformTransactionManager txManager = (PlatformTransactionManager)context.getBean("coviTransactionManager");		
		TransactionStatus status = txManager.getTransaction(def);
		
		try{
			sqlSession.insert("appvLine.insertAppvLinePublic", publicParams);
			sqlSession.insert("appvLine.insertAppvLinePrivate", privateParams);
			
			//txManager.commit(status);
		} catch(Exception e){
			//txManager.rollback(status);
			throw e;
		} finally{
			//((ClassPathXmlApplicationContext) context).close();
		}
		*/
		
		sqlSession.insert("coviflow.insertAppvLinePublic", publicParams);
		sqlSession.insert("coviflow.insertAppvLinePrivate", privateParams);
		
	}	
	
	@Override
	public void insertAppvLinePublic(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertAppvLinePublic", params);
	}
	
	@Override
	public void updateAppvLinePublic(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateAppvLinePublic", params);
	}
	
	@Override
	public void updateAppvLinePublicForOU(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateAppvLinePublicForOU", params);
	}
	
	@Override
	public void updateAppvLinePublicForSub(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateAppvLinePublicForSub", params);
	}
	
	@Override
	public void updateAllAppvLinePublic(Map<String, Object> params) throws Exception {
		int fiid = Integer.parseInt(params.get("formInstID").toString());
		String domainData = params.get("domainDataContext").toString();
		
		Map<String, Object> domainMap = new HashMap<>();
		domainMap.put("formInstID", fiid);
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectAllAppvLinePublic", domainMap);
		
		for (int i = 0; i < list.size(); i++) {
			int piid = Integer.parseInt(list.get(i).get("ProcessID").toString());
			Map<String, Object> domainUMap = new HashMap<>();
			domainUMap.put("domainDataContext", domainData);
			domainUMap.put("processID", piid);
			
			sqlSession.update("coviflow.updateAppvLinePublic", domainUMap);
		}
	}
	
	@Override
	public void insertAppvLinePrivate(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertAppvLinePrivate", params);
	}
	
	@Override
	public JSONObject selectProcessInitiatorInfo(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectProcessInitiatorInfo", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	@Override
	public void insertProcess(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertProcess", params);
	}
	
	@Override
	public Object insertProcessDescription(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertProcessDesc", params);
	}
	
	@Override
	public Object insertCirculationDescription(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertCirculationDesc", params);
	}
	
	@Override
	public void insertCirculation(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertCirculation", params);
	}
	
	@Override
	public void updateCirculation(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateCirculation", params);
	}
	
	@Override
	public void updateCirculationForSubject(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateCirculationForSubject", params);
	}
	
	@Override
	public void updateCirculationUsingProcessID(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateCirculationUsingProcessID", params);
	}
	
	@Override
	public Object insertWorkItemDescription(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertWorkItemDesc", params);
	}
	
	@Override
	public Object insertWorkItem(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertWorkItem", params);
	}
	
	@Override
	public JSONObject selectCharge(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectCharge", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	@Override
	public void updateWorkItemForCharge(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForCharge", params);
	}
	
	@Override
	public void updateWorkItemForApproveCancel(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForApproveCancel", params);
	}
	
	@Override
	public void updateWorkItem(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItem", params);
	}
	
	@Override
	public void updateWorkItemForReject(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForReject", params);
	}

	@Override
	public void updateWorkItemForResult(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForResult", params);
	}
	
	@Override
	public void updateWorkItemForChange(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForChange", params);
	}
	
	@Override
	public void updateWorkItemForDateChange(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForDateChange", params);
	}
	
	@Override
	public void updateWorkItemForReview(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForReview", params);
	}
	
	@Override
	public void updateWorkItemUsingTaskId(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemUsingTaskId", params);
	}
	
	@Override
	public void updateWorkItemState(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemState", params);
	}
	
	@Override
	public void updateWorkItemStateUsingWorkitemId(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemStateUsingWorkitemId", params);
	}
	
	@Override
	public void deleteWorkItemUsingTaskId(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.deleteWorkItemUsingTaskId", params);
	}
	
	@Override
	public Object insertPerformer(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertPerformer", params);
	}
	
	@Override
	public void updatePerformer(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updatePerformer", params);
	}
	
	@Override
	public void updatePerformerForCharge(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updatePerformerForCharge", params);
	}
	
	@Override
	public void updateProcessDescription(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessDesc", params);
	}
	
	@Override
	public void updateProcessDescForModify(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessDescForModify", params);
	}
	
	@Override
	public void updateCirculationboxDescForModify(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateCirculationboxDescForModify", params);
	}
	
	@Override
	public void updateProcessDescForHold(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessDescForHold", params);
	}
	
	@Override
	public void updateProcessDescForHoldAll(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessDescForHoldAll", params);
	}
	
	@Override
	public void updateWorkItemDescForModify(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemDescForModify", params);
	}
	
	@Override
	public void updateProcess(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcess", params);
		// ApvProcessSvcImpl.java 에서 문서번호 발번시에만 jwf_forminstance 의 CompletedDate 를 갱신하므로 여기서 완료일시 처리함. 22.04.28
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectFormInstanceByProcessID", params);
		map.put("processID", params.get("processID"));
		sqlSession.update("coviflow.updateFormInstanceCompletedDate", map);
	}
	
	@Override
	public void updateAllProcess(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateAllProcess", params);
	}
	
	@Override
	public void updateProcessBusinessState(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessBusinessState", params);
	}
	
	@Override
	public void updateProcessState(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessState", params);
	}
	
	@Override
	public void updateProcessForWithdraw(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessForWithdraw", params);
	}
	
	@Override
	public void updateProcessForSubject(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateProcessForSubject", params);
	}
	
	@Override
	public void insertProcessArchive(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertProcessArchive", params);
	}
	
	@Override
	public void insertProcessDescriptionArchive(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertProcessDescriptionArchive", params);
	}

	@Override
	public void updateProcessArchive(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.updateProcessArchive", params);
	}
	
	@Override
	public void insertWorkitemArchive(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertWorkitemArchive", params);
	}
	
	@Override
	public void insertWorkitemDescriptionArchive(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertWorkitemDescriptionArchive", params);
	}
	
	@Override
	public void updateWorkitemArchive(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.updateWorkitemArchive", params);
	}
	
	@Override
	public void insertDomainArchive(Map<String, Object> params) throws Exception {
		// sqlSession.insert("coviflow.insertDomainArchive", params);
	}
	
	@Override
	public JSONArray selectReceiptPersonInfo(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectReceiptPersonInfo", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}
	
	@Override
	public JSONObject selectDeputy(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectDeputy", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	@Override
	public int selectDeputyCount(Map<String, Object> params) throws Exception {
		return sqlSession.selectOne("coviflow.selectDeputyCount", params);
	}
	
	@Override
	public void updateWorkItemForDeputy(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateWorkItemForDeputy", params);
	}
	
	@Override
	public Object insertFormInstance(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertFormInstance", params);
	}
	
	@Override
	public Object insertFormsTempInstBox(Map<String, Object> params) throws Exception {
		return sqlSession.insert("coviflow.insertFormsTempInstBox", params);
	}
	
	@Override
	public void deleteProcess(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.deleteProcess", params);
	}
	
	@Override
	public void deleteWorkItem(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.deleteWorkItem", params);
	}
	
	@Override
	public void deleteOneWorkItem(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.deleteOneWorkItem", params);
	}
	
	@Override
	public void deleteOneWorkItemDescription(Map<String, Object> params) throws Exception {
		sqlSession.delete("coviflow.deleteOneWorkItemDescription", params);
	}
	
	@Override
	public void deleteOnePerformer(Map<String, Object> params) throws Exception {
		sqlSession.delete("coviflow.deleteOnePerformer", params);
	}
	
	@Override
	public Object selectSerialNumber(Map<String, Object> params) throws Exception {
		return sqlSession.selectOne("coviflow.selectSerialNumber", params);
	}
	
	@Override
	public int selectSerialNumber_Integer(Map<String, Object> params) throws Exception {
		return sqlSession.selectOne("coviflow.selectSerialNumber", params);
	}
	
	@Override
	public void insertDocumentNumber(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertDocumentNumber", params);
	}
	
	@Override
	public void updateFormInstanceForReceiveNo(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateFormInstanceForReceiveNo", params);
	}
	
	@Override
	public void updateFormInstanceForDocNo(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateFormInstanceForDocNo", params);
	}
	
	@Override
	public JSONObject selectFormInstance(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectFormInstance", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	@Override
	public JSONObject selectMultiAttachfileInfo(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectMultiAttachfileInfo", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	@Override
	public String selectManager(Map<String, Object> params) throws Exception {
		return sqlSession.selectOne("coviflow.selectManager", params);
	}
	
	@Override
	public JSONObject selectUserInfo(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectUserInfo", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	@Override
	public JSONArray selectPerformer(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectPerformer", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}

	@Override
	public JSONArray selectPerformerByWorkItemID(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectPerformerByWorkItemID", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}
	
	@Override
	public JSONArray selectSubDept(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectSubDept", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}
	
	@Override
	public JSONArray selectSharedPerson(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectSharePerson", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}
	
	@Override
	public JSONArray selectSharedOU(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectShareOU", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}
	
	@Override
	public JSONArray selectSignImage(Map<String, Object> params) throws Exception {
		JSONArray ret = new JSONArray();
		
		List<Map<String, Object>> list = sqlSession.selectList("coviflow.selectSignImage", params);
		ret = (JSONArray)JSONValue.parse(JSONValue.toJSONString(list));
		
		return ret;
	}

	@Override
	public void updateDocumentNumber(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateDocumentNumber", params);
	}
	
	@Override
	public int getUniqueId() throws Exception {
		return sqlSession.selectOne("coviflow.getUniqueId");
	}
	
	@Override
	public void insertGovApprovalDataE(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertGovApprovalDataE", params);
	}
	
	@Override
	public void insertGovApprovalDataMulti(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertGovApprovalDataMulti", params);
	}
	
	@Override
	public JSONObject selectGroupName(Map<String, Object> params) throws Exception {
		JSONObject ret = new JSONObject();
		
		Map<String, Object> map = sqlSession.selectOne("coviflow.selectGroupName", params);
		ret = (JSONObject)JSONValue.parse(JSONValue.toJSONString(map));
		
		return ret;
	}
	
	public Object insertFormInstanceMulti(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertFormInstanceMulti", params);		
		if(params.containsKey("subFormInstID")) {
			sqlSession.update("coviflow.updateSubTableForMultiGov", params);	
		}
		
		return params.get("subFormInstID");
	}

	@Override
	public void updateGovApprovalDataE(Map<String, Object> params) throws Exception {
		sqlSession.update("coviflow.updateGovApprovalDataE", params);
	}
	

	@Override
	public void insertEdmsTransferData(Map<String, Object> params) throws Exception {
		sqlSession.insert("coviflow.insertEdmsTransferData", params);
	}

	@Override
	public void updateSubTable(Map<String, Object> params) {
		sqlSession.update("coviflow.updateSubTable", params);
	}
}
