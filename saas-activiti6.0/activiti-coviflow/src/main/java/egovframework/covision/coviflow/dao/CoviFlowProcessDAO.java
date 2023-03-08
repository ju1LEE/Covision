/**
 * @Class Name : CoviFlowProcessDAO.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.dao;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface CoviFlowProcessDAO {
	public void insertErrorLog(Map<String, Object> params) throws Exception;
	public void insertLegacy(Map<String, Object> params) throws Exception;
	
	public void insertAppvLine(Map<String, Object> publicParams, Map<String, Object> privateParams) throws Exception;
	public void insertAppvLinePublic(Map<String, Object> params) throws Exception;
	public void updateAppvLinePublic(Map<String, Object> params) throws Exception;
	public void updateAppvLinePublicForOU(Map<String, Object> params) throws Exception;
	public void updateAppvLinePublicForSub(Map<String, Object> params) throws Exception;
	public void updateAllAppvLinePublic(Map<String, Object> params) throws Exception;
	public void insertAppvLinePrivate(Map<String, Object> params) throws Exception;
	public JSONObject selectProcessInitiatorInfo(Map<String, Object> params) throws Exception;
	public void insertProcess(Map<String, Object> params) throws Exception;
	public Object insertProcessDescription(Map<String, Object> params) throws Exception;
	public Object insertCirculationDescription(Map<String, Object> params) throws Exception;
	public void insertCirculation(Map<String, Object> params) throws Exception;
	public void updateCirculation(Map<String, Object> params) throws Exception;
	public void updateCirculationForSubject(Map<String, Object> params) throws Exception;
	public void updateCirculationUsingProcessID(Map<String, Object> params) throws Exception;
	public Object insertWorkItemDescription(Map<String, Object> params) throws Exception;
	public Object insertWorkItem(Map<String, Object> params) throws Exception;
	public JSONObject selectCharge(Map<String, Object> params) throws Exception;
	public void updateWorkItemForCharge(Map<String, Object> params) throws Exception;
	public void updateWorkItemForApproveCancel(Map<String, Object> params) throws Exception;
	public void updateWorkItem(Map<String, Object> params) throws Exception;
	public void updateWorkItemForReject(Map<String, Object> params) throws Exception;
	public void updateWorkItemForResult(Map<String, Object> params) throws Exception;
	public void updateWorkItemForChange(Map<String, Object> params) throws Exception;
	public void updateWorkItemForDateChange(Map<String, Object> params) throws Exception;
	public void updateWorkItemForReview(Map<String, Object> params) throws Exception;
	public void updateWorkItemUsingTaskId(Map<String, Object> params) throws Exception;
	public void updateWorkItemState(Map<String, Object> params) throws Exception;
	public void updateWorkItemStateUsingWorkitemId(Map<String, Object> params) throws Exception;
	public void deleteWorkItemUsingTaskId(Map<String, Object> params) throws Exception;
	public Object insertPerformer(Map<String, Object> params) throws Exception;
	public void updatePerformer(Map<String, Object> params) throws Exception;
	public void updatePerformerForCharge(Map<String, Object> params) throws Exception;
	public void updateProcessDescription(Map<String, Object> params) throws Exception;
	public void updateProcessDescForModify(Map<String, Object> params) throws Exception;
	public void updateCirculationboxDescForModify(Map<String, Object> params) throws Exception;
	public void updateProcessDescForHold(Map<String, Object> params) throws Exception;
	public void updateProcessDescForHoldAll(Map<String, Object> params) throws Exception;
	public void updateWorkItemDescForModify(Map<String, Object> params) throws Exception;
	public void updateProcess(Map<String, Object> params) throws Exception;
	public void updateAllProcess(Map<String, Object> params) throws Exception;
	public void updateProcessBusinessState(Map<String, Object> params) throws Exception;
	public void updateProcessState(Map<String, Object> params) throws Exception;
	public void updateProcessForWithdraw(Map<String, Object> params) throws Exception;
	public void updateProcessForSubject(Map<String, Object> params) throws Exception;
	
	public void insertProcessArchive(Map<String, Object> params) throws Exception;
	public void insertProcessDescriptionArchive(Map<String, Object> params) throws Exception;
	public void insertWorkitemArchive(Map<String, Object> params) throws Exception;
	public void insertWorkitemDescriptionArchive(Map<String, Object> params) throws Exception;
	public void insertDomainArchive(Map<String, Object> params) throws Exception;
	
	public JSONArray selectReceiptPersonInfo(Map<String, Object> params) throws Exception;
	public JSONObject selectDeputy(Map<String, Object> params) throws Exception;
	public int selectDeputyCount(Map<String, Object> params) throws Exception;
	public void updateWorkItemForDeputy(Map<String, Object> params) throws Exception;
	
	public Object insertFormInstance(Map<String, Object> params) throws Exception;
	public Object insertFormsTempInstBox(Map<String, Object> params) throws Exception;
	
	public void deleteProcess(Map<String, Object> params) throws Exception;
	public void deleteWorkItem(Map<String, Object> params) throws Exception;
	
	public void deleteOneWorkItem(Map<String, Object> params) throws Exception;
	public void deleteOneWorkItemDescription(Map<String, Object> params) throws Exception;
	public void deleteOnePerformer(Map<String, Object> params) throws Exception;
	
	public Object selectSerialNumber(Map<String, Object> params) throws Exception;
	public void insertDocumentNumber(Map<String, Object> params) throws Exception;
	public void updateDocumentNumber(Map<String, Object> params) throws Exception;
	public void updateFormInstanceForReceiveNo(Map<String, Object> params) throws Exception;
	public void updateFormInstanceForDocNo(Map<String, Object> params) throws Exception;
	public JSONObject selectFormInstance(Map<String, Object> params) throws Exception;
	
	public String selectManager(Map<String, Object> params) throws Exception;
	public JSONObject selectUserInfo(Map<String, Object> params) throws Exception;
	
	public JSONArray selectPerformer(Map<String, Object> params) throws Exception;
	public JSONArray selectPerformerByWorkItemID(Map<String, Object> params) throws Exception;
	public JSONArray selectSubDept(Map <String, Object> params) throws Exception;
	
	public JSONArray selectSharedPerson(Map <String, Object> params) throws Exception;
	public JSONArray selectSharedOU(Map <String, Object> params) throws Exception;
	
	public JSONArray selectSignImage(Map <String, Object> params) throws Exception;
	public int selectSerialNumber_Integer(Map<String, Object> paramsC) throws Exception;
	JSONObject selectGroupName(Map<String, Object> params) throws Exception;
	void insertGovApprovalDataE(Map<String, Object> params) throws Exception;
	void updateGovApprovalDataE(Map<String, Object> params) throws Exception;
	
	public Object insertFormInstanceMulti(Map<String, Object> params) throws Exception;
	public void insertEdmsTransferData(Map<String, Object> params) throws Exception;
	
	public void updateProcessArchive(Map<String, Object> params) throws Exception;
	public void updateWorkitemArchive(Map<String, Object> params) throws Exception;

	
	public int getUniqueId() throws Exception;
	public void updateSubTable(Map<String, Object> params);
	public void insertGovApprovalDataMulti(Map<String, Object> params) throws Exception;
	public JSONObject selectMultiAttachfileInfo(Map<String, Object> params) throws Exception;
}
