package egovframework.core.sevice.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import egovframework.core.sevice.FileUtilSvc;
import egovframework.coviframework.service.FileUtilService;
import egovframework.coviframework.util.s3.AwsS3;
import egovframework.coviframework.util.s3.AwsS3Data;



import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egovframework.core.interfaces.impl.HRInterfaceImpl;
import egovframework.core.sevice.OrgSyncManageSvc;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.baseframework.logging.LoggerHelper;
import egovframework.baseframework.sec.AES;
import egovframework.coviframework.util.ComUtils;
import egovframework.coviframework.util.FileUtil;
import egovframework.coviframework.util.HttpClientUtil;
import egovframework.coviframework.util.HttpURLConnectUtil;
import egovframework.coviframework.util.MultipartUtility;
import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.RedisDataUtil;
import egovframework.baseframework.util.RedisShardsUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.vo.ObjectAcl;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("organizationSyncManageService")
public class OrganizationSyncManageSvcImpl extends EgovAbstractServiceImpl implements OrgSyncManageSvc {

	private static final Logger LOGGER = LogManager.getLogger(OrganizationSyncManageSvcImpl.class);
	private HttpURLConnection conn = null;

	@Resource(name = "coviMapperOne")
	private CoviMapperOne coviMapperOne;

	AwsS3 awsS3 = AwsS3.getInstance();

	@Autowired
	FileUtilService fileUtilSvc;

	private String aeskey = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.key"));
	private boolean isSaaS = "Y".equals(PropertiesUtil.getGlobalProperties().getProperty("isSaaS", "N"));
	private String disablePrefix = PropertiesUtil.getGlobalProperties().getProperty("disablePrefix", "N");

	// 로그 관련
	/**
	 * @description 동기화 Master 로그 기록
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int insertLogList() throws Exception {
		int SyncMasterID = 0;
		
		try {
			CoviMap params = new CoviMap();
			StringUtil func = new StringUtil();
			int GRInsertCnt = 0;
			int GRUpdateCnt = 0;
			int GRDeleteCnt = 0;
			int URInsertCnt = 0;
			int URUpdateCnt = 0;
			int URDeleteCnt = 0;
			int AddJobInsertCnt = 0;
			int AddJobUpdateCnt = 0;
			int AddJobDeleteCnt = 0;

			params.put("SyncType", "");
			if ((int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectGroupListcnt", params) > 0) {
				LOGGER.debug("GR execute");

				params.put("SyncType", "INSERT");
				GRInsertCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectGroupListcnt", params);
				params.put("SyncType", "UPDATE");
				GRUpdateCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectGroupListcnt", params);
				params.put("SyncType", "DELETE");
				GRDeleteCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectGroupListcnt", params);

				if (func.f_NullCheck(GRInsertCnt).equals(""))
					GRInsertCnt = 0;
				if (func.f_NullCheck(GRUpdateCnt).equals(""))
					GRUpdateCnt = 0;
				if (func.f_NullCheck(GRDeleteCnt).equals(""))
					GRDeleteCnt = 0;
			}
			params.put("GRInsertCnt", GRInsertCnt);
			params.put("GRUpdateCnt", GRUpdateCnt);
			params.put("GRDeleteCnt", GRDeleteCnt);
			params.put("SyncType", "");

			if ((int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectUserListcnt", params) > 0) {
				LOGGER.debug("UR execute");

				params.put("SyncType", "INSERT");
				URInsertCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectUserListcnt",
						params);
				params.put("SyncType", "UPDATE");
				URUpdateCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectUserListcnt",
						params);
				params.put("SyncType", "DELETE");
				URDeleteCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectUserListcnt",
						params);

				if (func.f_NullCheck(URInsertCnt).equals(""))
					URInsertCnt = 0;
				if (func.f_NullCheck(URUpdateCnt).equals(""))
					URUpdateCnt = 0;
				if (func.f_NullCheck(URDeleteCnt).equals(""))
					URDeleteCnt = 0;
			}
			params.put("URInsertCnt", URInsertCnt);
			params.put("URUpdateCnt", URUpdateCnt);
			params.put("URDeleteCnt", URDeleteCnt);
			params.put("SyncType", "");

			if ((int) coviMapperOne.getNumber("organization.syncmanage.selectCompareObjectAddJobListcnt", params) > 0) {
				LOGGER.debug("ADDJOB execute");

				params.put("SyncType", "INSERT");
				AddJobInsertCnt = (int) coviMapperOne
						.getNumber("organization.syncmanage.selectCompareObjectAddJobListcnt", params);
				params.put("SyncType", "DELETE");
				AddJobDeleteCnt = (int) coviMapperOne
						.getNumber("organization.syncmanage.selectCompareObjectAddJobListcnt", params);

				if (func.f_NullCheck(AddJobInsertCnt).equals(""))
					AddJobInsertCnt = 0;
				if (func.f_NullCheck(AddJobUpdateCnt).equals(""))
					AddJobUpdateCnt = 0;
				if (func.f_NullCheck(AddJobDeleteCnt).equals(""))
					AddJobDeleteCnt = 0;
			}
			params.put("AddJobInsertCnt", AddJobInsertCnt);
			params.put("AddJobUpdateCnt", AddJobUpdateCnt);
			params.put("AddJobDeleteCnt", AddJobDeleteCnt);

			coviMapperOne.insert("organization.syncmanage.insertLogList", params);
			
			SyncMasterID = params.getInt("SyncMasterID");
		} catch (NullPointerException e) {
			LOGGER.error("insertLogList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("insertLogList Error  " + "[Message : " + e.getMessage() + "]");
		}
		
		return SyncMasterID;
	}

	@Override
	public void updateLogList() throws Exception {
		try {
			CoviMap params = new CoviMap();
			params.put("SyncMasterID", this.selectSyncMasterID());

			coviMapperOne.update("organization.syncmanage.updateLogList", params);
		} catch (NullPointerException e) {
			LOGGER.error("insertLogList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("insertLogList Error  " + "[Message : " + ex.getMessage() + "]");
		}
	}

	/**
	 * insertLogListLog : 동기화 로그 기록
	 * 
	 * @param SyncMasterID
	 * @param LogType
	 * @param TargetType
	 * @param SyncType
	 * @param LogMessage
	 * @param SyncServer
	 * @throws Exception
	 */
	@Override
	public void insertLogListLog(int SyncMasterID, String LogType, String TargetType, String SyncType, String LogMessage, String SyncServer) throws Exception {
		CoviMap params = new CoviMap();

		params.put("LogMasterID", SyncMasterID);
		params.put("LogType", LogType);
		params.put("TargetType", TargetType);
		params.put("SyncType", SyncType);
		params.put("LogMessage", LogMessage);
		params.put("SyncServer", SyncServer);

		try {
			coviMapperOne.insert("organization.syncmanage.insertLogListLog", params);
		}  catch (NullPointerException e) {
			LOGGER.error("insertLogListLog Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("insertLogListLog Error  " + "[Message : " + ex.getMessage() + "]");
		}
	}

	/**
	 * selectSyncMasterID : 동기화 로그 마스터 ID 조회
	 */
	@Override
	public int selectSyncMasterID() throws Exception {
		int masterID = (int) coviMapperOne.getNumber("organization.syncmanage.selectSyncMasterID", null);
		return masterID;
	}

	/**
	 * @description Group 동기화 로그 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupLogList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectGroupLogListcnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectGroupLogList", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"Seq,SyncType,GroupCode,CompanyCode,MemberOf,DisplayName,IsUse,SyncStatus,InsertDate"));
		}  catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("selectGroupLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (NullPointerException ex) {
			LOGGER.error("selectGroupLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("selectGroupLogList Error  " + "[Message : " + ex.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * @description 동기화 모니터링 로그 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectSyncHitory(CoviMap params) throws Exception {
		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectSyncHitory", params);
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"SyncMasterID,GRInsertCnt,GRUpdateCnt,GRDeleteCnt,URInsertCnt,URUpdateCnt,URDeleteCnt,AddJobInsertCnt,AddJobUpdateCnt,AddJobDeleteCnt,StartDate,EndDate"));
		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("selectSyncHitory Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (NullPointerException ex) {
			LOGGER.error("selectSyncHitory Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("selectSyncHitory Error  " + "[Message : " + ex.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * @description 동기화 모니터링 로그 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectSyncItemLog(CoviMap params) throws Exception {
		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectSyncItemLog", params);
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"SyncMasterID,Seq,LogType,TargetType,SyncType,LogMessage,RegistDate"));
		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("selectSyncItemLog Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (NullPointerException ex) {
			LOGGER.error("selectSyncItemLog Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("selectSyncItemLog Error  " + "[Message : " + ex.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * @description 사용자 동기화 로그 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserLogList(CoviMap params) throws Exception {
		CoviMap resultList = null;
		CoviList list = null;
		int cnt = 0;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectUserLogList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectUserLogListcnt", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"Seq,SyncType,UserCode,CompanyCode,DeptCode,DisplayName,IsUse,SyncStatus,InsertDate"));
			resultList.put("cnt", cnt);
		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("selectUserLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (NullPointerException ex) {
			LOGGER.error("selectUserLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("selectUserLogList Error  " + "[Message : " + ex.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * @description 겸직 동기화 로그 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAddJobLogList(CoviMap params) throws Exception {
		CoviMap resultList = null;
		CoviList list = null;
		int cnt = 0;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectAddJobLogList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectAddJobLogListcnt", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"Seq,SyncType,JobType,UserCode,CompanyCode,DeptCode,JobLevelCode,JobPositionCode,JobTitleCode,IsUse,SyncStatus,InsertDate"));
			resultList.put("cnt", cnt);
		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("selectAddJobLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (NullPointerException ex) {
			LOGGER.error("selectAddJobLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("selectAddJobLogList Error  " + "[Message : " + ex.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * @description 동기화 로그 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectLogList(CoviMap params) throws Exception {
		CoviMap resultList =  new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;

		try {
			if(params.containsKey("pageNo")) {
				int cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectLogListTotalCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectLogList", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"SyncMasterID,GRInsertCnt,GRUpdateCnt,GRDeleteCnt,URInsertCnt,URUpdateCnt,URDeleteCnt,AddJobInsertCnt,AddJobUpdateCnt,AddJobDeleteCnt,InsertDate"));

		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("selectLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (NullPointerException ex) {
			LOGGER.error("selectLogList Error  " + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error("selectLogList Error  " + "[Message : " + ex.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * Sync여부 설정값
	 */
	@Override
	public CoviMap getIsSyncList() throws Exception {
		return coviMapperOne.select("organization.syncmanage.getIsSyncList", null);
	}

	/**
	 * 동기화 진행 여부 확인
	 */
	@Override
	public boolean isSyncProgress() throws Exception {
		boolean bReturn = true;
		String sSync = "";
		try {
			sSync = (String) coviMapperOne.getString("organization.syncmanage.getIsSyncProgress", null);
			if (sSync.equals("Y"))
				bReturn = false;
			else
				bReturn = true;
		}  catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("isSyncProgress Error [Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("isSyncProgress Error [Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("isSyncProgress Error [Message : " + e.getMessage() + "]");
		}

		return bReturn;
	}

	/**
	 * 동기화 진행 여부 업데이트
	 */
	@Override
	public int updateSyncProgress(String status) throws Exception {
		CoviMap params = new CoviMap();
		params.put("syncStatus", status);
		return coviMapperOne.update("organization.syncmanage.updateSyncProgress", params);
	}

	// 동기화 관련
	/**
	 * @description 인사데이터 불러오기
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap syncTempObject() throws Exception {

		CoviMap returnObj = new CoviMap();

		// count check to import data
		int tempJobTitleCnt = 0;
		int tempJobPositionCnt = 0;
		int tempJobLevelCnt = 0;
		int tempDeptCnt = 0;
		int tempUserCnt = 0;
		int tempAddJobCnt = 0;

		String isHRInterface = RedisDataUtil.getBaseConfig("isHRInterface").toString();

		if (isHRInterface.equalsIgnoreCase("Y")) {
			this.insertLogListLog(this.selectSyncMasterID(), "Info", "HR", "연동", "HR 연동 시작", "");
			
			LOGGER.info("InsertOrgData execute");
			LOGGER.info("---> isHRInterface : " + isHRInterface);

			LOGGER.info("---> clear covi_syndata org tables...");
			coviMapperOne.delete("organization.syncmanage.deleteOrgData");

			Iterator<Map<String, Object>> iter;

			LOGGER.info("---> get HR data through interface...");
			HRInterfaceImpl interfaceHR = new HRInterfaceImpl();
			interfaceHR.init();
			Map<String, Object> hrData = interfaceHR.getHRData();

			if (RedisDataUtil.getBaseConfig("IsDeptSync").equalsIgnoreCase("Y")) {
//				LOGGER.info("---> insert dept...");
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> deptList = (ArrayList<Map<String, Object>>) hrData.get("dept");
				iter = deptList.iterator();
				while (iter.hasNext()) {
					Map<String, Object> deptMap = (Map<String, Object>) iter.next();
					coviMapperOne.insert("organization.syncmanage.insertOrgDept", deptMap);
				}
			}

			if (RedisDataUtil.getBaseConfig("IsJobTitleSync").equalsIgnoreCase("Y")) {
//				LOGGER.info("---> insert jobtitle...");
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> jobtitleList = (ArrayList<Map<String, Object>>) hrData.get("jobtitle");
				iter = jobtitleList.iterator();
				while (iter.hasNext()) {
					Map<String, Object> jobtitleMap = (Map<String, Object>) iter.next();
					coviMapperOne.insert("organization.syncmanage.insertOrgJobTitle", jobtitleMap);
				}
			}

			if (RedisDataUtil.getBaseConfig("IsJobPositionSync").equalsIgnoreCase("Y")) {
//				LOGGER.info("---> insert jobposition...");
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> jobpositionList = (ArrayList<Map<String, Object>>) hrData
						.get("jobposition");
				iter = jobpositionList.iterator();
				while (iter.hasNext()) {
					Map<String, Object> jobpositionMap = (Map<String, Object>) iter.next();
					coviMapperOne.insert("organization.syncmanage.insertOrgJobPosition", jobpositionMap);
				}
			}

			if (RedisDataUtil.getBaseConfig("IsJobLevelSync").equalsIgnoreCase("Y")) {
//				LOGGER.info("---> insert joblevel...");
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> joblevelList = (ArrayList<Map<String, Object>>) hrData.get("joblevel");
				iter = joblevelList.iterator();
				while (iter.hasNext()) {
					Map<String, Object> joblevelMap = (Map<String, Object>) iter.next();
					coviMapperOne.insert("organization.syncmanage.insertOrgJobLevel", joblevelMap);
				}
			}

			if (RedisDataUtil.getBaseConfig("IsUserSync").equalsIgnoreCase("Y")) {
//				LOGGER.info("---> insert user...");
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> userList = (ArrayList<Map<String, Object>>) hrData.get("user");
				iter = userList.iterator();
				while (iter.hasNext()) {
					Map<String, Object> userMap = (Map<String, Object>) iter.next();
					coviMapperOne.insert("organization.syncmanage.insertOrgPerson", userMap);
				}
			}

			if (RedisDataUtil.getBaseConfig("IsAddJobSync").equalsIgnoreCase("Y")) {
//				LOGGER.info("---> insert addjob...");
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> addjobList = (ArrayList<Map<String, Object>>) hrData.get("addjob");
				iter = addjobList.iterator();
				while (iter.hasNext()) {
					Map<String, Object> addjobMap = (Map<String, Object>) iter.next();
					coviMapperOne.insert("organization.syncmanage.insertOrgAddJob", addjobMap);
				}
			}
		}
		
		this.insertLogListLog(this.selectSyncMasterID(), "Info", "HR", "연동", "HR 연동 종료", "");
		this.insertLogListLog(this.selectSyncMasterID(), "Info", "Temp", "", "임시데이터 생성 시작", "");

		LOGGER.info("InsertTempData execute");

		CoviMap params = new CoviMap();
		params.put("isSaaS", (isSaaS) ? "Y" : "N");
		params.put("disablePrefix", disablePrefix);

		coviMapperOne.delete("organization.syncmanage.deleteTempObjectGroupList");
		tempDeptCnt = coviMapperOne.insert("organization.syncmanage.insertTempObjectDept", params);
		tempJobTitleCnt = coviMapperOne.insert("organization.syncmanage.insertTempObjectJobTitle", params);
		tempJobPositionCnt = coviMapperOne.insert("organization.syncmanage.insertTempObjectJobPosition", params);
		tempJobLevelCnt = coviMapperOne.insert("organization.syncmanage.insertTempObjectJobLevel", params);

		coviMapperOne.delete("organization.syncmanage.deleteTempObjectUserList");
		tempUserCnt = coviMapperOne.insert("organization.syncmanage.insertTempObjectUser", params);
		// coviMapperOne.delete("organization.syncmanage.deleteDupPerson"); //LogonID 중복
		// 사용자 삭제

		coviMapperOne.delete("organization.syncmanage.deleteTempObjectAddJobList");
		tempAddJobCnt = coviMapperOne.insert("organization.syncmanage.insertTempObjectAddJob", params);

		LOGGER.info("InsertTempData execute complete");
		LOGGER.info("TempData count check");
		LOGGER.info("[JobTitle : " + tempJobTitleCnt + "]" + "[JobPosition : " + tempJobPositionCnt + "]"
				+ "[JobLevel : " + tempJobLevelCnt + "]" + "[Dept : " + tempDeptCnt + "]" + "[User : " + tempUserCnt
				+ "]" + "[AddJob : " + tempAddJobCnt + "]");

		returnObj.put("tempJobTitleCnt", tempJobTitleCnt);
		returnObj.put("tempJobPositionCnt", tempJobPositionCnt);
		returnObj.put("tempJobLevelCnt", tempJobLevelCnt);
		returnObj.put("tempDeptCnt", tempDeptCnt);
		returnObj.put("tempUserCnt", tempUserCnt);
		returnObj.put("tempAddJobCnt", tempAddJobCnt);
		
		this.insertLogListLog(this.selectSyncMasterID(), "Info", "Temp", "", "임시데이터 생성 종료", "");

		return returnObj;
	}

	/**
	 * 전체 퇴사 방지 체크
	 */
	@Override
	public int checkRetireCount() throws Exception {
		int iRetireUser = 0;

		try {
			iRetireUser = (int) coviMapperOne.getNumber("organization.syncmanage.selectDeleteUserCount", null);
		}  catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("checkRetireCount error : " + e.getMessage());
		} catch (NullPointerException e) {
			LOGGER.error("checkRetireCount error : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("checkRetireCount error : " + e.getMessage());
		}

		return iRetireUser;
	}

	/**
	 * @description 동기화 대상 추출
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap syncCompareObject() throws Exception {

		CoviMap returnObj = new CoviMap();

		// count check to import data
		int compareGroupCnt = 0;
		int compareUserCnt = 0;
		int compareAddJobCnt = 0;

		LOGGER.info("insertCompareData execute");
		this.insertLogListLog(this.selectSyncMasterID(), "Info", "Temp", "", "동기화 대상 생성 시작", "");

		coviMapperOne.delete("organization.syncmanage.deleteCompareObjectGroupList");
		compareGroupCnt = coviMapperOne.insert("organization.syncmanage.insertCompareObjectGroupINSERT", null);
		compareGroupCnt += coviMapperOne.insert("organization.syncmanage.insertCompareObjectGroupUPDATE", null);
		compareGroupCnt += coviMapperOne.insert("organization.syncmanage.insertCompareObjectGroupDELETE", null);
		compareGroupCnt -= coviMapperOne.delete("organization.syncmanage.deleteNoMemberOf");

		coviMapperOne.delete("organization.syncmanage.deleteCompareObjectUserList");
		compareUserCnt = coviMapperOne.insert("organization.syncmanage.insertCompareObjectUserINSERT", null);
		compareUserCnt += coviMapperOne.insert("organization.syncmanage.insertCompareObjectUserUPDATE", null);
		compareUserCnt += coviMapperOne.insert("organization.syncmanage.insertCompareObjectUserDELETE", null);

		coviMapperOne.delete("organization.syncmanage.deleteCompareObjectAddJobList");
		compareAddJobCnt = coviMapperOne.insert("organization.syncmanage.insertCompareObjectAddJobINSERT", null);
		compareAddJobCnt += coviMapperOne.insert("organization.syncmanage.insertCompareObjectAddJobDELETE", null);

		LOGGER.info("insertCompareData execute complete");
		LOGGER.info("CompareData count check");
		LOGGER.info("[Group : " + compareGroupCnt + "]" + "[User : " + compareUserCnt + "]" + "[AddJob : "
				+ compareAddJobCnt + "]");

		returnObj.put("compareGroupCnt", compareGroupCnt);
		returnObj.put("compareUserCnt", compareUserCnt);
		returnObj.put("compareAddJobCnt", compareAddJobCnt);
		
		this.insertLogListLog(this.selectSyncMasterID(), "Info", "Temp", "", "동기화 대상 생성 종료", "");

		return returnObj;
	}
	
	/**
	 * @description 동기화 대상 조회
	 * @return CoviList
	 * @throws Exception
	 */
	@Override
	public CoviList selectCompareList(String type) throws Exception {
		CoviList list = null;
		if (type.equals("Group")) {
			list = coviMapperOne.list("organization.syncmanage.selectCompareObjectGroup", null);
		}
		else if (type.equals("User")) {
			list = coviMapperOne.list("organization.syncmanage.selectCompareObjectUser", null);
		}
		else if (type.equals("Addjob")) {
			list = coviMapperOne.list("organization.syncmanage.selectCompareObjectAddjob", null);
		}

		return list;
	}

	@Override
	public CoviMap selectCompareObjectGroupList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectCompareObjectGroupList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"SyncType,GroupCode,CompanyCode,GroupType,MemberOf,GroupPath,RegionCode,DisplayName,MultiDisplayName,ShortName,MultiShortName,OUName,OUPath,SortKey,SortPath,IsUse,IsDisplay,IsMail,IsHR,PrimaryMail,SecondaryMail,ManagerCode,Description,ReceiptUnitCode,ApprovalUnitCode,Receivable,Approvable,RegistDate,ModifyDate,Reserved1,Reserved2,Reserved3,Reserved4,Reserved5"));
		return resultList;
	}

	@Override
	public CoviMap selectCompareObjectUserList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectCompareObjectUserList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"SyncType,LicSeq,UserCode,LogonID,LogonPassword,EmpNo,DisplayName,NickName,MultiDisplayName,CompanyCode,DeptCode,JobTitleCode,JobPositionCode,JobLevelCode,RegionCode,Address,MultiAddress,HomePage,PhoneNumber,Mobile,Fax,IPPhone,UseMessengerConnect,SortKey,SecurityLevel,Description,IsUse,IsHR,IsDisplay,EnterDate,RetireDate,PhotoPath,BirthDiv,BirthDate,UseMailConnect,MailAddress,ExternalMailAddress,ChargeBusiness,PhoneNumberInter,LanguageCode,MobileThemeCode,TimeZoneCode,InitPortal,RegistDate,ModifyDate,Reserved1,Reserved2,Reserved3,Reserved4,Reserved5,oldDeptCode,oldJobPositionCode,oldJobTitleCode,oldJobLevelCode,AD_IsUse,AD_DisplayName,AD_FirstName,AD_LastName,AD_Initials,AD_Office,AD_HomePage,AD_Country,AD_CountryID,AD_CountryCode,AD_State,AD_City,AD_StreetAddress,AD_PostOfficeBox,AD_PostalCode,AD_UserAccountControl,AD_AccountExpires,AD_PhoneNumber,AD_HomePhone,AD_Pager,AD_Mobile,AD_Fax,AD_Info,AD_Title,AD_Department,AD_Company,AD_ManagerCode,AD_CN,AD_SamAccountName,AD_UserPrincipalName,EX_IsUse,EX_PrimaryMail,EX_SecondaryMail,EX_StorageServer,EX_StorageGroup,EX_StorageStore,MSN_IsUse,MSN_PoolServerName,MSN_PoolServerDN,MSN_SIPAddress,MSN_Anonmy,MSN_MeetingPolicyName,MSN_MeetingPolicyDN,MSN_PhoneCommunication,MSN_PBX"));
		return resultList;
	}

	@Override
	public CoviMap selectMoniterCompareCount(CoviMap params) throws Exception {
		CoviMap list = coviMapperOne.select("organization.syncmanage.selectMoniterCompareCount", params);

		return CoviSelectSet.coviSelectMapJSON(list);
	}

	@Override
	public CoviMap selectCompareObjectAddJobList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectCompareObjectAddJobList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"SyncType,Seq,UserCode,LogonID,JobType,CompanyCode,DeptCode,JobTitleCode,JobPositionCode,JobLevelCode,RegionCode,SortKey,IsHR,RegistDate,ModifyDate,Reserved1,Reserved2,Reserved3,Reserved4,Reserved5,JobTitleName,JobPositionName,JobLevelName,DisplayName"));
		return resultList;
	}

	@Override
	public void deleteCompareObjectOne(CoviMap params) throws Exception {
		try {
			if (params.get("DataType").toString().toUpperCase().equals("GROUP")) {
				coviMapperOne.delete("organization.syncmanage.deleteCompareObjectGroupOne", params);
				LOGGER.info("deleteCompareObjectOne Info[GroupCode : " + params.get("GroupCode") + "]");
			} else if (params.get("DataType").toString().toUpperCase().equals("USER")) {
				coviMapperOne.delete("organization.syncmanage.deleteCompareObjectUserOne", params);
				LOGGER.info("deleteCompareObjectOne Info[UserCode : " + params.get("UserCode") + "]");
			} else {
				coviMapperOne.delete("organization.syncmanage.deleteCompareObjectAddJobOne", params);
				LOGGER.info("deleteCompareObjectOne Info[UserCode : " + params.get("UserCode") + "]");
			}
		} catch (NullPointerException ex) {
			if (params.get("DataType").toString().equalsIgnoreCase("GROUP")) {
				LOGGER.error("deleteCompareObjectOne Error [GroupCode : " + params.get("GroupCode") + "] "
						+ "[Message : " + ex.getMessage() + "]", ex);
				
			} else if (params.get("DataType").toString().equalsIgnoreCase("USER")) {
				LOGGER.error("deleteCompareObjectOne Error [UserCode : " + params.get("UserCode") + "] " + "[Message : "
						+ ex.getMessage() + "]", ex);
			} else {
				LOGGER.error("deleteCompareObjectOne Error [AddJob : " + params.get("UserCode") + "] " + "[Message : "
						+ ex.getMessage() + "]", ex);
			}
		} catch (Exception ex) {
			if (params.get("DataType").toString().equalsIgnoreCase("GROUP")) {
				LOGGER.error("deleteCompareObjectOne Error [GroupCode : " + params.get("GroupCode") + "] "
						+ "[Message : " + ex.getMessage() + "]", ex);
				
			} else if (params.get("DataType").toString().equalsIgnoreCase("USER")) {
				LOGGER.error("deleteCompareObjectOne Error [UserCode : " + params.get("UserCode") + "] " + "[Message : "
						+ ex.getMessage() + "]", ex);
			} else {
				LOGGER.error("deleteCompareObjectOne Error [AddJob : " + params.get("UserCode") + "] " + "[Message : "
						+ ex.getMessage() + "]", ex);
			}
		}
	}

	/**
	 * 부서 일정 대상 추가
	 * 
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int insertDeptScheduleCreation(CoviMap params) throws Exception {
		int iReturn = 0;
		try {
			iReturn = coviMapperOne.insert("organization.syncmanage.insertDeptSchedule", params);
		} catch (NullPointerException e) {
			LOGGER.error("insertDeptScheduleCreation error : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("insertDeptScheduleCreation error : " + e.getMessage());
		}
		return iReturn;
	}

	/**
	 * @description 부서 일정 대상 가져오기
	 */
	@Override
	public CoviMap selectDeptSchedule(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptSchedule", null);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"DomainID,GroupCode,CompanyCode,MenuID,FolderType,DisplayName,MultiDisplayName,OwnerCode"));
		return resultList;
	}

	/**
	 * @description 부서 일정 생성
	 * @return int
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int insertDeptScheduleInfo() throws Exception {
		// Folder 데이터 저장
		int returnVal = 0;
		int iReturn = 0;
		CoviMap folderDataObj = new CoviMap();
		folderDataObj.put("MemberOf", RedisDataUtil.getBaseConfig("ScheduleTotalFolderID"));
		String sortKey = "";
		String sSecLevel = RedisDataUtil.getBaseConfig("DefaultSecurityLevel", SessionHelper.getSession("DN_ID"))
				.toString().equals("") ? "90"
						: RedisDataUtil.getBaseConfig("DefaultSecurityLevel", SessionHelper.getSession("DN_ID"))
								.toString();

		// CoviMap ids =
		// coviMapperOne.select("organization.syncmanage.selectDomainMenuID",
		// folderDataObj);
		// sortKey = coviMapperOne.selectOne("organization.syncmanage.selectSortKey",
		// folderDataObj);

		CoviList arrDeptList = (CoviList) selectDeptSchedule(null).get("list");

		if (arrDeptList != null && arrDeptList.size() > 0) {
			int Deptcnt = arrDeptList.size();

			for (int i = 0; i < Deptcnt; i++) {
				sortKey = coviMapperOne.selectOne("organization.syncmanage.selectSortKey", folderDataObj);

				folderDataObj.put("DomainID", "0");
				folderDataObj.put("MenuID", "7");
				folderDataObj.put("FolderType", arrDeptList.getJSONObject(i).getString("FolderType"));
				folderDataObj.put("ParentObjectID", "0");
				folderDataObj.put("ParentObjectType", "DN");
				folderDataObj.put("LinkFolderID", null);
				folderDataObj.put("DisplayName", arrDeptList.getJSONObject(i).getString("DisplayName"));
				folderDataObj.put("MultiDisplayName", arrDeptList.getJSONObject(i).getString("MultiDisplayName"));
				folderDataObj.put("SortKey", sortKey);
				folderDataObj.put("SecurityLevel", sSecLevel);
				folderDataObj.put("IsInherited", "N");
				folderDataObj.put("IsShared", "N");
				folderDataObj.put("IsUse", "Y");
				folderDataObj.put("IsDisplay", "Y");
				folderDataObj.put("IsURL", "N");
				folderDataObj.put("URL", "");
				folderDataObj.put("IsMobileSupport", null);
				folderDataObj.put("IsAdminNotice", null);
				folderDataObj.put("ManageCompany", arrDeptList.getJSONObject(i).getString("DomainID"));
				folderDataObj.put("IsMsgSecurity", null);
				folderDataObj.put("Description", null);
				folderDataObj.put("OwnerCode", arrDeptList.getJSONObject(i).getString("OwnerCode"));
				folderDataObj.put("RegisterCode", arrDeptList.getJSONObject(i).getString("OwnerCode"));
				folderDataObj.put("ModifierCode", arrDeptList.getJSONObject(i).getString("OwnerCode"));
				folderDataObj.put("DeleteDate", null);
				folderDataObj.put("Reserved1", null); // 개인일정 공유
				folderDataObj.put("Reserved2", RedisDataUtil.getBaseConfig("FolderDefaultColor").toString()); // 일정 기본
																												// 컬러
				folderDataObj.put("Reserved3", null);
				folderDataObj.put("Reserved4", "Y"); // 관리자 하위메뉴 표시 여부
				folderDataObj.put("Reserved5", arrDeptList.getJSONObject(i).getString("GroupCode")); // 부서코드 맵핑 처리

				returnVal = coviMapperOne.insert("organization.syncmanage.insertFolderData", folderDataObj);

				String folderID = folderDataObj.getString("FolderID");
				CoviMap idParam = new CoviMap();
				idParam.put("FolderID", folderID);
				// Folder SortPath, FolderPath Update
				String sortPath = coviMapperOne.selectOne("organization.syncmanage.selectComSortPathCreateS", idParam);
				String folderPath = coviMapperOne.selectOne("organization.syncmanage.selectComObjectPathCreateS",
						idParam);
				idParam.put("SortPath", sortPath);
				idParam.put("FolderPath", folderPath);
				coviMapperOne.update("organization.syncmanage.updateSortPath", idParam);
				coviMapperOne.update("organization.syncmanage.updateFolderPath", idParam);
				
				String aclString = "";
				
				// 부서 일정 생성 시 수정/삭제 권한 부여 여부 (기본은 수정/삭제 권한을 부여하지 않는다)
				if(RedisDataUtil.getBaseConfig("ScheduleDefaultCorrectACL").equalsIgnoreCase("Y")) {
					aclString = "[{\"SubjectCode\":\"" + arrDeptList.getJSONObject(i).getString("GroupCode") + "\",\"SubjectType\":\"GR\",\"AclList\":\"SCDMEVR\"}]";
				} else {
					aclString = "[{\"SubjectCode\":\"" + arrDeptList.getJSONObject(i).getString("GroupCode") + "\",\"SubjectType\":\"GR\",\"AclList\":\"SC__EVR\"}]";
				}
				
				CoviList aclDataList = new CoviList();
				CoviList aclDataObj1 = CoviList.fromObject(StringEscapeUtils.unescapeHtml(aclString));
				aclDataList.addAll((Collection) aclDataObj1);
				// 권한 데이터 저장
				if (returnVal > 0) {
					CoviList aclDatas = null;
					for (Object obj : aclDataList) {
						aclDatas = new CoviList();
						CoviMap aclDataObj = new CoviMap();
						aclDataObj.addAll((Map) obj);

						ObjectAcl aclObject = new ObjectAcl();
						aclObject.setObjectID(Integer.parseInt(folderID));
						aclObject.setObjectType("FD");
						aclObject.setSubjectType(aclDataObj.getString("SubjectType"));
						aclObject.setSubjectCode(aclDataObj.getString("SubjectCode"));
						aclObject.setAclList(aclDataObj.getString("AclList"));

						aclDatas.add(aclObject);
					}

					iReturn = coviMapperOne.insert("framework.authority.insertACLList", aclDatas);

					if (iReturn == 1 && RedisDataUtil.getBaseConfig("DeptScheduleInherit").equalsIgnoreCase("N")) {
						CoviMap params1 = new CoviMap();
						params1.put("gr_code", arrDeptList.getJSONObject(i).getString("GroupCode"));
						CoviList arrresultList = (CoviList) selectSubGroupList(params1).get("list");
						CoviMap arrresult = selectSubGroupList(params1);
						int subCnt = arrresult.getInt("cnt");
						aclDataList = new CoviList();

						if (subCnt > 0) {
							for (int j = 0; j < subCnt; j++) {
								aclString = "[{\"SubjectCode\":\"" + arrresultList.getJSONObject(j).getString("GroupCode") + "\",\"SubjectType\":\"GR\",\"AclList\":\"_______\"}]";
								aclDataObj1 = CoviList.fromObject(StringEscapeUtils.unescapeHtml(aclString));
								aclDataList.addAll((Collection) aclDataObj1);

								aclDatas = new CoviList();
								for (Object obj : aclDataList) {
									CoviMap aclDataObj = new CoviMap();
									aclDataObj.addAll((Map) obj);

									ObjectAcl aclObject = new ObjectAcl();
									aclObject.setObjectID(Integer.parseInt(folderID));
									aclObject.setObjectType("FD");
									aclObject.setSubjectType(aclDataObj.getString("SubjectType"));
									aclObject.setSubjectCode(aclDataObj.getString("SubjectCode"));
									aclObject.setAclList(aclDataObj.getString("AclList"));

									aclDatas.add(aclObject);

									iReturn += coviMapperOne.insert("framework.authority.insertACLList", aclDatas);
								}
							}
						}
					}

					// Redis 권한 데이터 초기화
					RedisShardsUtil.getInstance().removeAll(folderDataObj.getString("domainID"),
							RedisDataUtil.PRE_ACL + folderDataObj.getString("domainID") + "_*");
					folderDataObj.put("CreateYN", "Y");
					folderDataObj.put("GroupCode", arrDeptList.getJSONObject(i).getString("GroupCode"));
					coviMapperOne.update("organization.syncmanage.updateDeptSchedule", folderDataObj);
				}

			}
		}
		return returnVal;
	}

	/**
	 * @description 부서 일정 수정
	 * @param params
	 * @throws Exception
	 */
	@Override
	public int updateDeptScheduleInfo(CoviMap params) throws Exception {
		CoviMap fParams = new CoviMap();
		int iReturn = coviMapperOne.update("organization.syncmanage.updateFolderData", params);
		if (iReturn > 0) {

			@SuppressWarnings("rawtypes")
			List FolderChildList = coviMapperOne.list("organization.syncmanage.selectFolderChildList", params);

			if (FolderChildList.size() > 0) {
				for (int j = 0; j < FolderChildList.size(); ++j) {
					CoviMap SubParams = new CoviMap();
					SubParams = (CoviMap) FolderChildList.get(j);
					fParams.put("FolderID", SubParams.get("FolderID"));
					fParams.put("Reserved5", SubParams.get("Reserved5"));

					String sortPath = coviMapperOne.selectOne("organization.syncmanage.selectComSortPathCreateS",
							fParams);
					fParams.put("SortPath", sortPath);

					coviMapperOne.update("organization.syncmanage.updateSortPath", fParams);
				}
			}
		}
		return iReturn;
	}

	/**
	 * 사용자 비밀번호 초기화
	 * 
	 * @param params
	 *            - CoviMap
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String resetuserpassword(CoviMap params) throws Exception {
		String sNewPassword = null;
		params.put("aeskey", aeskey);
		StringBuffer sTemp = new StringBuffer();
		int mCount = 0;

		try {
			if (!RedisDataUtil.getBaseConfig("InitPassword").toString().equals("")) {
				sNewPassword = RedisDataUtil.getBaseConfig("InitPassword").toString();

			} else {
				SecureRandom rnd = new SecureRandom();
				for (int i = 0; i < 10; i++) {
					int iIndex = rnd.nextInt(3);
					switch (iIndex) {
					case 0:
						sTemp.append((char) ((int) (rnd.nextInt(26)) + 97));
						break;
					case 1:
						sTemp.append((char) ((int) (rnd.nextInt(26)) + 65));
						break;
					case 2:
						sTemp.append(rnd.nextInt(10));
						break;
					default :
						break;
					}
				}
				sNewPassword = sTemp.toString();
				LOGGER.debug("비밀번호 랜덤 생성 : " + sNewPassword);
			}
			params.put("sNewPassword", sNewPassword);

			mCount = coviMapperOne.update("organization.syncmanage.resetuserpassword", params);
			if (mCount == 1) {
				coviMapperOne.update("organization.syncmanage.resetuserpasswordLock", params);
			}
		} catch (NullPointerException e) {
			sNewPassword = "FAIL";
			LOGGER.error("TimeSquare resetuserpassword Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		}  catch (Exception e) {
			sNewPassword = "FAIL";
			LOGGER.error("TimeSquare resetuserpassword Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		}
		return sNewPassword;
	}

	// 회사 관련
	/**
	 * @param params
	 *            DomainID
	 * @description 도메인/회사 조회
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDomainList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		params.put("lang", SessionHelper.getSession("lang"));

		CoviList list = coviMapperOne.list("organization.syncmanage.selectDomainList", params);
		CoviList companyList = CoviSelectSet.coviSelectJSON(list, "optionText,optionValue");

		resultList.put("list", companyList);

		return resultList;
	}

	/**
	 * 회사 드롭다운 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAvailableCompanyList(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectAvailableCompanyList", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list, "CompanyCode,CompanyName,CompanyID"));

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectAvailableCompanyList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectAvailableCompanyList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectAvailableCompanyList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;

	}

	// 부서 관련
	/**
	 * 부서 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDeptList(CoviMap params) throws Exception {
		params.put("isSaaS", (isSaaS) ? "Y" : "N");
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"itemType,CompanyCode,GroupCode,GroupType,GroupDisplayName,GroupName,CompanyName,PrimaryMail,MemberOf,SortKey,SortPath,GroupID,hasChild,GroupFullPath,IsUse"));
		return resultList;
	}

	@Override
	public CoviList getInitOrgTreeList(CoviMap params) throws Exception {
		String lang = SessionHelper.getSession("lang");
		
		params.put("lang", lang);
		CoviList list = coviMapperOne.list("organization.syncmanage.selectInitOrgTreeList", params);
		
		CoviList deptList = CoviSelectSet.coviSelectJSON(list, "itemType,CompanyCode,GroupCode,GroupType,GroupDisplayName,GroupName,CompanyName,PrimaryMail,MemberOf,SortKey,SortPath,GroupID,haveChild,GroupFullPath,IsUse,isLoad");
		
		return deptList;
	}
	@Override
	public CoviList getChildrenData(CoviMap params) throws Exception {
		String lang = SessionHelper.getSession("lang");
		params.put("lang", lang);
		CoviList list = coviMapperOne.list("organization.syncmanage.selectChildrenData", params);
		
		CoviList deptList = CoviSelectSet.coviSelectJSON(list, "itemType,CompanyCode,GroupCode,GroupType,GroupDisplayName,GroupName,CompanyName,PrimaryMail,MemberOf,SortKey,SortPath,GroupID,haveChild,GroupFullPath,IsUse,isLoad");
		
		return deptList;
	}
	
	/**
	 * 하위 부서 Path 정보 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDeptPathInfo(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptPathInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "GroupCode,GroupPath,OUPath,SortPath"));
		return resultList;
	}

	/**
	 * 하위 부서 목록 selectOrgGroup_Add
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectSubDeptList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectSubDeptListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectSubDeptList", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"GroupID,GroupCode,CompanyCode,GroupType,MemberOf,GroupPath,DisplayName,MultiDisplayName,ShortName,MultiShortName,OUName,OUPath,SortKey,SortPath,IsUse,IsDisplay,IsMail,IsHR,PrimaryMail,Description,ReceiptUnitCode,ApprovalUnitCode,Receivable,Approvable,RegistDate,ModifyDate,ManagerCode,DomainName,MultiDomainName,HasChild,Depth,ManagerName"));
			
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}

		return resultList;
	}

	/**
	 * 상위 분류 가져오기 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectParentName(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectParentName", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "DisplayName,GroupPath"));
		return resultList;
	}

	/**
	 * 그룹 유형 가져오기 select
	 *
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupType() throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupType", null);

		CoviMap resultList = new CoviMap();
		resultList.put("list",
				CoviSelectSet.coviSelectJSON(list, "GroupType,SortKey,Priority,DisplayName,MultiDisplayName"));
		return resultList;
	}

	/**
	 * 부서 정보 가져오기 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDeptInfo(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"GroupID,GroupCode,CompanyCode,GroupType,MemberOf,DisplayName,ShortName,ParentName,GroupPath,MultiDisplayName,MultiShortName,SortKey,SortPath,IsUse,IsDisplay,IsMail,IsHR,PrimaryMail,SecondaryMail,Description,ReceiptUnitcode,ApprovalUnitCode,Receivable,Approvable,RegistDate,ModifyDate,ManagerCode,Manager,CompanyName,CompanyCode,OUPath,OUName,IsCRM"));
		return resultList;
	}

	/**
	 * 그룹 정보 가져오기 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDeptInfoList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptInfoList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"GroupID,GroupCode,CompanyCode,GroupType,MemberOf,DisplayName,ShortName,ParentName,GroupPath,MultiDisplayName,DisplayName,MultiShortName,SortKey,SortPath,IsUse,IsDisplay,IsMail,IsHR,PrimaryMail,SecondaryMail,Description,ReceiptUnitcode,ApprovalUnitCode,Receivable,Approvable,RegistDate,ModifyDate,ManagerCode,Manager,CompanyName,CompanyCode,OUPath,OUName"));
		return resultList;
	}

	/**
	 * 코드 값으로 정보 가져오기(부서, 회사) select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDefaultSetInfo(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDefaultSetInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "CompanyName,DeptName,CompanyID,CompanyURL"));
		return resultList;
	}

	/**
	 * 그룹메일주소 가져오기 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - String
	 * @throws Exception
	 */
	@Override
	public String selectGroupMail(CoviMap params) throws Exception {
		String sReturn = coviMapperOne.getString("organization.syncmanage.selectGroupMail", params);
		if (sReturn == null) sReturn = "";
		return sReturn;
	}

	/**
	 * 부서 사용 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsUseDept(CoviMap params) throws Exception {
		// 사용여부 설정이면 object 사용여부, deletedate 처리
		params.put("ObjectCode", params.get("GroupCode").toString());
		coviMapperOne.update("organization.syncmanage.updateObject", params); // update to sys_object

		return coviMapperOne.update("organization.syncmanage.updateIsUseDept", params);
	};

	/**
	 * 부서 인사 연동 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsHRDept(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsHRDept", params);
	};

	/**
	 * 부서 표시 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsDisplayDept(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsDisplayDept", params);
	};

	/**
	 * 부서 메일 사용 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsMailDept(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsMailDept", params);
	}

	/**
	 * 하위부서 존재 여부 체크
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 하위부서 존재 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectHasChildDept(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectHasChildDept", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "HasChild"));
		return resultList;
	}

	/**
	 * 소속 사용자 존재 여부 체크
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 소속 사용자 존재 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectHasUserDept(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectHasUserDept", params);
		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "HasUser"));
		return resultList;
	}

	/**
	 * 부서 코드 중복 체크
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 중복 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsDuplicateDeptCode(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateDeptCode", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate"));
		return resultList;
	};

	/**
	 * 부서 SortKey 중복 여부 체크 - 사용 안하는듯 함
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 중복 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsDuplicateDeptPriorityOrder(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateDeptPriorityOrder", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate"));
		return resultList;
	};

	/**
	 * 부서 코드 중복 체크
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 중복 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsduplicatesortkey(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateSortKey", params);
		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate,MaxSortKey"));
		return resultList;
	};

	/**
	 * 그룹 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupList(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"itemType,DomainCode,SortKey,GroupCode,GroupType,GroupName,DomainName,PrimaryMail,MemberOf,SortPath,GroupID,hasChild,IsUse"));
		return resultList;
	}

	/**
	 * 지역 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectRegionInfo(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectRegionInfo", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"itemType,IsUse,IsHR,IsMail,CompanyCode,CompanyID,SortKey,GroupCode,GroupType,GroupName,MultiGroupName,CompanyName,PrimaryMail,SecondaryMail,MemberOf,SortPath,GroupID,Description,hasChild"));
			resultList.put("cnt", 1);

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectRegionInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectRegionInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectRegionInfo Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 임의 그룹 드롭다운 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectArbitraryGroupDropDownList(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectArbitraryGroupDropDownList", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list, "GroupType,GroupCode,GroupName"));

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectArbitraryGroupDropDownList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectArbitraryGroupDropDownList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectArbitraryGroupDropDownList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;

	}

	/**
	 * 임의 그룹 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectArbitraryGroupInfo(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectArbitraryGroupInfo", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"itemType,IsUse,IsHR,IsMail,CompanyCode,CompanyID,SortKey,GroupCode,GroupType,GroupName,MultiGroupName,CompanyName,MultiCompanyName,PrimaryMail,SecondaryMail,MemberOf,SortPath,GroupID,Description,hasChild"));
			resultList.put("cnt", 1);

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectArbitraryGroupInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectArbitraryGroupInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectArbitraryGroupInfo Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 임의 그룹 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectArbitraryGroupList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectArbitraryGroupListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectArbitraryGroupList", params);
			
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"itemType,IsUse,IsHR,IsMail,DomainCode,SortKey,GroupCode,GroupType,GroupName,DomainName,PrimaryMail,MemberOf,SortPath,GroupID,hasChild,Description"));
			
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectArbitraryGroupList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectArbitraryGroupList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectArbitraryGroupList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;

	}

	/**
	 * 부서 기타 정보 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDeptEtcInfo(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptEtcInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "CompanyID,GroupPath,SortPath,OUPath"));
		return resultList;
	}

	/**
	 * 임의그룹 중복 여부 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsDuplicateGroupCode(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateGroupCode", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate"));
		return resultList;
	};

	/**
	 * 임의 그룹 삽입 정보 select(path 등)
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupInsertData(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		params.put("lang", SessionHelper.getSession("lang"));

		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupInsertData", params);
		CoviList groupInfo = CoviSelectSet.coviSelectJSON(list, "GroupPath,OUPath,SortPath");

		resultList.put("list", groupInfo);

		return resultList;
	}

	/**
	 * 권한 관리 정보 list select
	 * 
	 * @param params
	 * @return  resultList - JSON
	 * @throws - Exeption
	 */
	@Override
	public CoviMap selectAuthorityList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectAuthorityListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectAuthorityList", params);
			

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"IsUse,IsHR,IsMail,DomainCode,SortKey,GroupCode,GroupType,GroupName,DomainName,PrimaryMail,MemberOf,GroupID,hasChild,Description"));
		
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectArbitraryGroupList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectArbitraryGroupList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectArbitraryGroupList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 권한 팝업 default
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDefaultSetAuthority(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDomainID", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list",
				CoviSelectSet.coviSelectJSON(list, "DomainID,DisplayName,MultiDisplayName,MemberOf,GroupName"));
		return resultList;
	}

	/**
	 * 권한 정보 조회
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAuthorityinfo(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectAuthorituinfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"CompanyCode,MultiDomainName,GroupCode,GroupType,DisplayName,MultiDisplayName,DomainName,PrimaryMail,MemberOf,SortKey,GroupID,IsUse,IsHR,IsMail,Description"));
		return resultList;
	}

	/**
	 * 지역 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectRegionList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectRegionListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectRegionList", params);
			
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"itemType,IsUse,IsHR,IsMail,DomainCode,SortKey,GroupCode,GroupType,GroupName,MultiGroupName,DomainName,PrimaryMail,MemberOf,SortPath,GroupID,hasChild,Description"));
			
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectRegionList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectRegionList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectRegionList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 부서 목록 select for select box
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDeptListForCategory(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectDeptListForCategory", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "GroupCode,GroupName"));
		return resultList;
	}

	/**
	 * 하위 그룹 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectSubGroupList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectSubGroupListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectSubGroupList", params);
			
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"GroupCode,MultiDisplayName,IsUse,GroupType,CompanyCode,GroupPath,SortPath,MultiShortName,OUName,ShortName,SortKey,MemberOf,IsMail,PrimaryMail,IsDisplay"));
			
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectSubGroupList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectSubGroupList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectSubGroupList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 그룹 목록 select for select box
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupListForCategory(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupListForCategory", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "GroupCode,GroupName"));
		return resultList;
	}

	/**
	 * 그룹 정보 가져오기 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupInfo(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"GroupID,GroupCode,CompanyCode,GroupType,MemberOf,ParentName,GroupPath,MultiDisplayName,MultiShortName,SortKey,SortPath,IsUse,IsDisplay,IsMail,IsHR,PrimaryMail,SecondaryMail,Description,ReceiptUnitcode,ApprovalUnitCode,Receivable,Approvable,RegistDate,ModifyDate,ManagerCode,Manager,CompanyName,CompanyCode"));
		return resultList;
	}

	/**
	 * 그룹 기타 정보 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupEtcInfo(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupEtcInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "CompanyID,GroupPath,SortPath,OUPath"));
		return resultList;
	}

	/**
	 * 코드 값으로 정보 가져오기(그룹, 회사) select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectDefaultSetInfoGroup(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectDefaultSetInfoGroup", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "CompanyName,GroupName,CompanyID"));
		return resultList;
	}

	/**
	 * 임의 그룹 설정 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap updateArbitraryGroup(CoviMap params) throws Exception {
		CoviMap resultObj = new CoviMap();
		int returnCnt = 0;

		returnCnt = coviMapperOne.update("organization.syncmanage.updateArbitraryGroup", params);

		// 사용여부 설정이면 object 사용여부, deletedate 처리
		if (params.get("Type").toString().equalsIgnoreCase("IsUse")) {
			params.put("ObjectCode", params.get("GroupCode").toString());
			params.put("IsUse", params.get("ToBeValue").toString());

			coviMapperOne.update("organization.syncmanage.updateObject", params); // update to sys_object
		}

		if (returnCnt == 1)
			resultObj.put("result", "OK");
		else
			resultObj.put("result", "FAIL");

		return resultObj;
	}

	/**
	 * 임의 그룹 목록 delete
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public int deleteArbitraryGroupList(CoviMap params) throws Exception {
		int cnt = coviMapperOne.delete("organization.syncmanage.deleteArbitraryGroupList", params);
		return cnt;
	}

	/**
	 * 임의 그룹 목록 우선순위 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap updateArbitraryGroupListSortKey(CoviMap params) throws Exception {

		CoviMap resultObj = new CoviMap();
		int returnCnt = 0;
		String strSortKey_A = null;
		String strSortPath_A = null;
		String strSortKey_B = null;
		String strSortPath_B = null;

		try {
			// get SortKey,SortPath
			params.put("TargetCode", params.get("GR_Code_A"));
			strSortKey_A = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
					.get("SortKey").toString();
			strSortPath_A = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
					.get("SortPath").toString();
			params.put("TargetCode", params.get("GR_Code_B"));
			strSortKey_B = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
					.get("SortKey").toString();
			strSortPath_B = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
					.get("SortPath").toString();

			// update SortKey
			params.put("TargetCode", params.get("GR_Code_A"));
			params.put("TargetSortKey", strSortKey_B);
			params.put("TargetSortPath", strSortPath_B);
			returnCnt += coviMapperOne.update("organization.syncmanage.updateArbitraryGroupSortKey", params);
			params.put("TargetCode", params.get("GR_Code_B"));
			params.put("TargetSortKey", strSortKey_A);
			params.put("TargetSortPath", strSortPath_A);
			returnCnt += coviMapperOne.update("organization.syncmanage.updateArbitraryGroupSortKey", params);

		} catch (NullPointerException e) {
			LOGGER.error("updateArbitraryGroupListSortKey Error  " + "[Message : " + e.getMessage() + "]");
		}  catch (Exception e) {
			LOGGER.error("updateArbitraryGroupListSortKey Error  " + "[Message : " + e.getMessage() + "]");
		}

		if (returnCnt > 0)
			resultObj.put("result", "OK");
		else
			resultObj.put("result", "FAIL");

		return resultObj;
	}

	/**
	 * 그룹 사용 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsUseGroup(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsUseGroup", params);
	};
	
	/**
	 * 그룹 표시 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsDisplayGroup(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsDisplayGroup", params);
	}

	/**
	 * 그룹 메일 사용 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsMailGroup(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsMailGroup", params);
	}

	/**
	 * 부서 정보 insert
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int insertGroup(CoviMap params) throws Exception {
		int iReturn = 0;

		try {
			LOGGER.debug("insertGroup execute [" + params.get("GroupCode") + "]");
			LOGGER.debug("insertGroup - insertObject execute");

			iReturn = coviMapperOne.insert("organization.syncmanage.insertObject", params); // insert to sys_object
			if (iReturn == 1) {
				LOGGER.debug("insertGroup - insertObject execute complete");
				LOGGER.debug("insertGroup - insertObjectGroup execute");
				iReturn = coviMapperOne.insert("organization.syncmanage.insertObjectGroup", params); // insert to sys_object_group

				if (iReturn == 1) {
					if (params.get("SyncManage") != "Sync") {
						String GroupPath = coviMapperOne.selectOne("organization.syncmanage.selectFNGroupPath", params);
						String SortPath = coviMapperOne.selectOne("organization.syncmanage.selectFNSortPath", params);
						String OUPath = coviMapperOne.selectOne("organization.syncmanage.selectFNOUPath", params);
						params.put("GroupPath", GroupPath);
						params.put("SortPath", SortPath);
						params.put("OUPath", OUPath);
						iReturn = coviMapperOne.update("organization.syncmanage.updateFNGroupPath", params);
						LOGGER.debug("insertGroup - insertObjectGroup execute complete");
					} 
					else {
						params.put("SyncStatus", 'Y');
						
						if (params.get("SyncManage") == "Sync") {
							String strLog = "";
							String sObjectType = params.get("GroupType").toString().toUpperCase();
							if ("DEPT".equals(sObjectType)) strLog = "부서 ";
							else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
							else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
							else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
							
							strLog += "[" + params.get("GroupCode") + "] 추가됨";
							
							insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", sObjectType, "INSERT", strLog, "");
						}
					}
				} 
				else {
					throw new Exception("insertGroup - insertObjectGroup execute failed");
				}
			} 
			else {
				throw new Exception("insertGroup - insertObject execute failed");
			}
		}  catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("insertGroup Error [ObjectCode : " + params.get("GroupCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 추가 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", sObjectType, "INSERT", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("insertGroup Error [ObjectCode : " + params.get("GroupCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 추가 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", sObjectType, "INSERT", strLog, "");
			}
		} 
		finally {
			insertGroupLogList(params);
		}

		return iReturn;
	}

	/**
	 * 부서 정보 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateGroup(CoviMap params) throws Exception {
		int iReturn = 0;

		try {
			LOGGER.debug("updateGroup execute [" + params.get("GroupCode") + "]");
			LOGGER.debug("updateGroup - updateObject execute");
			iReturn = coviMapperOne.update("organization.syncmanage.updateObject", params); // update to sys_object
			LOGGER.debug("updateGroup - updateObject execute complete");
			if (iReturn != 1) // sys_object에 해당 데이터가 없을 경우
			{
				LOGGER.debug("updateGroup - updateObject failed");
				LOGGER.debug("updateGroup - insertObject execute");
				coviMapperOne.insert("organization.syncmanage.insertObject", params); // insert to sys_object
				LOGGER.debug("updateGroup - insertObject execute complete");
			}

			LOGGER.debug("updateGroup - updateObjectGroup execute");
			iReturn = coviMapperOne.update("organization.syncmanage.updateObjectGroup", params); // update to sys_object_group
			LOGGER.debug("updateGroup - updateObjectGroup execute complete");
			if (iReturn != 1) // sys_object_group에 해당 데이터가 없을 경우
			{
				LOGGER.debug("updateGroup - updateObjectGroup failed");
				LOGGER.debug("updateGroup - insertObjectGroup execute");
				iReturn = coviMapperOne.insert("organization.syncmanage.insertObjectGroup", params); // insert to sys_object_group
			}

			if (iReturn > 0) { // path 업데이트
				if (params.get("SyncManage") != "Sync") {
					String GroupPath = coviMapperOne.selectOne("organization.syncmanage.selectFNGroupPath", params);
					String SortPath = coviMapperOne.selectOne("organization.syncmanage.selectFNSortPath", params);
					String OUPath = coviMapperOne.selectOne("organization.syncmanage.selectFNOUPath", params);
					params.put("GroupPath", GroupPath);
					params.put("SortPath", SortPath);
					params.put("OUPath", OUPath);
					iReturn = coviMapperOne.update("organization.syncmanage.updateFNGroupPath", params);
					LOGGER.debug("updateGroup - updateFNGroupPath execute complete");
				}
				params.put("SyncStatus", 'Y');

				if (params.get("GroupType").toString().contains("Job")
						|| params.get("GroupType").toString().equals("Dept")) { // update JobLevel, JobPosition, JobTitle, Dept
					LOGGER.debug("updateGroup - updateUserGroupName execute");
					coviMapperOne.update("organization.syncmanage.updateUserGroupName", params); // update to sys_object_user_basegroup
					LOGGER.debug("updateGroup - updateUserGroupName execute complete");
				}
			}

			params.put("SyncStatus", 'Y');
			
			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 수정됨";
				
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", sObjectType, "UPDATE", strLog, "");
			}
		}  catch (NullPointerException ex) {
			iReturn = 0;
			params.put("SyncStatus", 'N');
			LOGGER.error("updateGroup Error [ObjectCode : " + params.get("GroupCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);
			
			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 수정 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", sObjectType, "UPDATE", strLog, "");
			}
		} catch (Exception ex) {
			iReturn = 0;
			params.put("SyncStatus", 'N');
			LOGGER.error("updateGroup Error [ObjectCode : " + params.get("GroupCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);
			
			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 수정 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", sObjectType, "UPDATE", strLog, "");
			}
		}
		finally {
			insertGroupLogList(params);
		}

		return iReturn;
	}

	/**
	 * 부서 정보 delete
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int deleteGroup(CoviMap params) throws Exception {
		int iReturn = 0;

		try {
			LOGGER.debug("deleteGroup execute [" + params.get("GroupCode") + "]");
			LOGGER.debug("deleteGroup - deleteObject execute");

			iReturn = coviMapperOne.update("organization.syncmanage.deleteObject", params); // delete to sys_object
			if (iReturn == 1) {
				LOGGER.debug("deleteGroup - deleteObject execute complete");
				LOGGER.debug("deleteGroup - deleteObjectGroup execute");
				iReturn = coviMapperOne.delete("organization.syncmanage.deleteObjectGroup", params); // delete to
																										// sys_object_group
				if (iReturn == 1) {
					LOGGER.debug("deleteGroup - deleteObjectGroup execute complete");
					params.put("SyncStatus", 'Y');
					if (params.get("SyncManage") == "Sync") {
						String strLog = "";
						String sObjectType = params.get("GroupType").toString().toUpperCase();
						if ("DEPT".equals(sObjectType)) strLog = "부서 ";
						else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
						else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
						else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
						
						strLog += "[" + params.get("GroupCode") + "] 삭제됨";
						
						insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", sObjectType, "DELETE", strLog, "");
					}
				}
			}
		} catch (NullPointerException ex) {
			iReturn = 0;
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteGroup Error [ObjectCode : " + params.get("GroupCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", sObjectType, "DELETE", strLog, "");
			}
		} catch (Exception ex) {
			iReturn = 0;
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteGroup Error [ObjectCode : " + params.get("GroupCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "";
				String sObjectType = params.get("GroupType").toString().toUpperCase();
				if ("DEPT".equals(sObjectType)) strLog = "부서 ";
				else if ("JOBLEVEL".equals(sObjectType)) strLog = "직급 ";
				else if ("JOBPOSITION".equals(sObjectType)) strLog = "직위 ";
				else if ("JOBTITLE".equals(sObjectType)) strLog = "직책 ";
				
				strLog += "[" + params.get("GroupCode") + "] 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", sObjectType, "DELETE", strLog, "");
			}
		} 
		finally {
			insertGroupLogList(params);
		}

		return iReturn;
	}

	/**
	 * 임의그룹 리스트 가져오기 select
	 * 
	 * @param params CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectJobInfoList(CoviMap params) throws Exception {
		int cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectJobInfoCount", params);
		CoviList list = new CoviList();

		if (cnt > 0) {
			list = coviMapperOne.list("organization.syncmanage.selectJobInfoList", params);
		} else {
			CoviMap temp = new CoviMap();
			temp.put("domainCode", "ORGROOT");
			temp.put("groupType", params.get("groupType").toString());

			list = coviMapperOne.list("organization.syncmanage.selectJobInfoList", temp);
		}

		CoviMap resultList = new CoviMap();
		resultList.put("list",
				CoviSelectSet.coviSelectJSON(list, "GroupCode,DisplayName,GroupType,MemberOf,MultiDisplayName"));
		return resultList;
	}

	/**
	 * 하위 부서 Path 정보 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateDeptPathInfo(CoviMap params) throws Exception {
		int retCnt = coviMapperOne.update("organization.syncmanage.updateDeptPathInfo", params);
		return retCnt;
	}

	// 사용자 관련
	/**
	 * 사용자 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
 		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
	 		if(params.containsKey("pageNo")) {
	 			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectUserListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectUserList", params);
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"itemType,UserID,UserCode,MultiDisplayName,JobTitle,JobPosition,JobLevel,Mobile,MailAddress,PhoneNumber,EmpNo,DeptCode,MultiDeptName,CompanyCode,MultiCompanyName,JobType,PhoneNumberInter,IsUse,IsDisplay,IsHR,SortKey"));
			
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}

		return resultList;
	}

	/**
	 * 사용자 정보 가져오기 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserInfo(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectUserInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"LicSeq,UserID,UserCode,LogonID,EmpNo,DisplayName,MultiDisplayName,NickName,MultiAddress,HomePage,PhoneNumber,Mobile,FAX,IPPhone,UseMessengerConnect,SortKey,SecurityLevel,Description,IsUse,IsHR,IsDisplay,EnterDate,RetireDate,PhotoPath,BirthDiv,BirthDate,UseMailConnect,MailAddress,ExternalMailAddress,ChargeBusiness,PhoneNumberInter,LanguageCode,Reserved2,Reserved3,Reserved4,ManagerName,Reserved5,CompanyCode,MultiCompanyName,DeptCode,DeptName,MultiDeptName,RegionCode,MultiRegionName,JobPositionCode,JobTitleCode,JobLevelCode,JobPositionName,JobTitleName,JobLevelName,UseDeputy,DeputyOption,DeputyCode,DeputyName,DeputyFromDate,DeputyToDate,AlertConfig,ApprovalUnitCode,ApprovalUnitName,ReceiptUnitCode,ReceiptUnitName,UseApprovalMessageBoxView,UseMobile,DomainID,CheckUserIP,StartIP,EndIP,AD_DISPLAYNAME,AD_FIRSTNAME,AD_LASTNAME,AD_INITIALS,AD_OFFICE,AD_HOMEPAGE,AD_COUNTRY,AD_COUNTRYID,AD_COUNTRYCODE,AD_STATE,AD_CITY,AD_STREETADDRESS,AD_POSTOFFICEBOX,AD_POSTALCODE,AD_USERACCOUNTCONTROL,AD_ACCOUNTEXPIRES,AD_PHONENUMBER,AD_HOMEPHONE,AD_PAGER,AD_MOBILE,AD_FAX,AD_IPPHONE,AD_INFO,AD_TITLE,AD_DEPARTMENT,AD_COMPANY,AD_MANAGERCODE,EX_PRIMARYMAIL,EX_SECONDARYMAIL,EX_STORAGESERVER,EX_STORAGEGROUP,EX_STORAGESTORE,EX_CUSTOMATTRIBUTE01,EX_CUSTOMATTRIBUTE02,EX_CUSTOMATTRIBUTE03,EX_CUSTOMATTRIBUTE04,EX_CUSTOMATTRIBUTE05,EX_CUSTOMATTRIBUTE06,EX_CUSTOMATTRIBUTE07,EX_CUSTOMATTRIBUTE08,EX_CUSTOMATTRIBUTE09,EX_CUSTOMATTRIBUTE10,EX_CUSTOMATTRIBUTE11,EX_CUSTOMATTRIBUTE12,EX_CUSTOMATTRIBUTE13,EX_CUSTOMATTRIBUTE14,EX_CUSTOMATTRIBUTE15,MSN_POOLSERVERNAME,MSN_POOLSERVERDN,MSN_SIPADDRESS,MSN_ANONMY,MSN_MEETINGPOLICYNAME,MSN_MEETINGPOLICYDN,MSN_PHONECOMMUNICATION,MSN_PBX,MSN_LINEPOLICYNAME,MSN_LINEPOLICYDN,MSN_LINEURI,MSN_LINESERVERURI,MSN_FEDERATION,MSN_REMOTEACCESS,MSN_PUBLICIMCONNECTIVITY,MSN_INTERNALIMCONVERSATION,MSN_FEDERATEDIMCONVERSATION,AD_ISUSE,EX_ISUSE,MSN_ISUSE,AD_CN,AD_SamAccountName,AD_UserPrincipalName,CountryID,CountryCode,CityState,City,PostOfficeBox,OfficeAddress"));
						
		return resultList;
	}

	/**
	 * 사용자 정보 가져오기 select - 모바일조직도조회용
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserInfoOrg(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectUserInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"MultiDisplayName,MultiDeptName,MultiCompanyName,JobPositionName,JobTitleName,JobLevelName,PhoneNumber,Mobile,MailAddress,PhotoPath"));
		return resultList;
	}

	/**
	 * 사용자 사용 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsUseUser(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsUseUser", params);
	};

	/**
	 * 사용자 인사 연동 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsHRUser(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsHRUser", params);
	}

	/**
	 * 사용자 AD 연동 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsADUser(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsADUser", params);
	}

	/**
	 * 사용자 코드 중복 체크
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 중복 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsDuplicateUserCode(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateUserCode", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate"));
		return resultList;
	}

	/**
	 * 사용자 사번 중복 체크 (SaaS)
	 * 
	 * @param params
	 *            - CoviMap
	 * @return CoviMap - 중복 여부
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsDuplicateEmpno(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateEmpno", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate"));
		return resultList;
	}

	/**
	 * 사용자 정보 가져오기 select (패스워드 포함)
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserInfoByAdmin(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectUserInfoByAdmin", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"UserID,UserCode,LogonID,EmpNo,MultiDisplayName,NickName,MultiAddress,HomePage,PhoneNumber,Mobile,FAX,IPPhone,UseMessengerConnect,SortKey,SecurityLevel,Description,IsUse,IsHR,IsDisplay,EnterDate,RetireDate,PhotoPath,BirthDiv,BirthDate,UseMailConnect,MailAddress,ExternalMailAddress,PhoneNumberInter,Reserved2,Reserved3,Reserved4,Reserved5,CompanyCode,MultiCompanyName,DeptCode,MultiDeptName,RegionCode,MultiRegionName,JobPositionCode,JobTitleCode,JobLevelCode,JobPositionName,JobTitleName,JobLevelName,UseDeputy,DeputyCode,DeputyName,DeputyFromDate,DeputyToDate,AlertConfig,ApprovalUnitCode,ApprovalUnitName,ReceiptUnitCode,ReceiptUnitName,UseApprovalMessageBoxView,UseMobile,AD_DISPLAYNAME,AD_FIRSTNAME,AD_LASTNAME,AD_INITIALS,AD_OFFICE,AD_HOMEPAGE,AD_COUNTRY,AD_COUNTRYID,AD_COUNTRYCODE,AD_STATE,AD_CITY,AD_STREETADDRESS,AD_POSTOFFICEBOX,AD_POSTALCODE,AD_USERACCOUNTCONTROL,AD_ACCOUNTEXPIRES,AD_PHONENUMBER,AD_HOMEPHONE,AD_PAGER,AD_MOBILE,AD_FAX,AD_IPPHONE,AD_INFO,AD_TITLE,AD_DEPARTMENT,AD_COMPANY,AD_MANAGERCODE,EX_PRIMARYMAIL,EX_SECONDARYMAIL,EX_STORAGESERVER,EX_STORAGEGROUP,EX_STORAGESTORE,EX_CUSTOMATTRIBUTE01,EX_CUSTOMATTRIBUTE02,EX_CUSTOMATTRIBUTE03,EX_CUSTOMATTRIBUTE04,EX_CUSTOMATTRIBUTE05,EX_CUSTOMATTRIBUTE06,EX_CUSTOMATTRIBUTE07,EX_CUSTOMATTRIBUTE08,EX_CUSTOMATTRIBUTE09,EX_CUSTOMATTRIBUTE10,EX_CUSTOMATTRIBUTE11,EX_CUSTOMATTRIBUTE12,EX_CUSTOMATTRIBUTE13,EX_CUSTOMATTRIBUTE14,EX_CUSTOMATTRIBUTE15,MSN_POOLSERVERNAME,MSN_POOLSERVERDN,MSN_SIPADDRESS,MSN_ANONMY,MSN_MEETINGPOLICYNAME,MSN_MEETINGPOLICYDN,MSN_PHONECOMMUNICATION,MSN_PBX,MSN_LINEPOLICYNAME,MSN_LINEPOLICYDN,MSN_LINEURI,MSN_LINESERVERURI,MSN_FEDERATION,MSN_REMOTEACCESS,MSN_PUBLICIMCONNECTIVITY,MSN_INTERNALIMCONVERSATION,MSN_FEDERATEDIMCONVERSATION,AD_ISUSE,EX_ISUSE,MSN_ISUSE,LOGONPASSWORD,AD_USERID,EX_USERID,MSN_USERID,AD_CN,AD_SamAccountName,AD_UserPrincipalName"));
		return resultList;
	}

	/**
	 * 사용자 정보 가져오기
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserInfoList(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectUserInfoList", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"UserID,UserCode,LogonID,EmpNo,MultiDisplayName,NickName,MultiAddress,HomePage,PhoneNumber,Mobile,FAX,IPPhone,UseMessengerConnect,SortKey,SecurityLevel,Description,IsUse,IsHR,IsDisplay,EnterDate,RetireDate,PhotoPath,BirthDiv,BirthDate,UseMailConnect,MailAddress,ExternalMailAddress,PhoneNumberInter,Reserved2,Reserved3,Reserved4,Reserved5,CompanyCode,MultiCompanyName,DeptCode,MultiDeptName,RegionCode,MultiRegionName,JobPositionCode,JobTitleCode,JobLevelCode,JobPositionName,JobTitleName,JobLevelName,UseDeputy,DeputyCode,DeputyName,DeputyFromDate,DeputyToDate,AlertConfig,ApprovalUnitCode,ApprovalUnitName,ReceiptUnitCode,ReceiptUnitName,UseApprovalMessageBoxView,UseMobile,AD_DISPLAYNAME,AD_FIRSTNAME,AD_LASTNAME,AD_INITIALS,AD_OFFICE,AD_HOMEPAGE,AD_COUNTRY,AD_COUNTRYID,AD_COUNTRYCODE,AD_STATE,AD_CITY,AD_STREETADDRESS,AD_POSTOFFICEBOX,AD_POSTALCODE,AD_USERACCOUNTCONTROL,AD_ACCOUNTEXPIRES,AD_PHONENUMBER,AD_HOMEPHONE,AD_PAGER,AD_MOBILE,AD_FAX,AD_IPPHONE,AD_INFO,AD_TITLE,AD_DEPARTMENT,AD_COMPANY,AD_MANAGERCODE,EX_PRIMARYMAIL,EX_SECONDARYMAIL,EX_STORAGESERVER,EX_STORAGEGROUP,EX_STORAGESTORE,EX_CUSTOMATTRIBUTE01,EX_CUSTOMATTRIBUTE02,EX_CUSTOMATTRIBUTE03,EX_CUSTOMATTRIBUTE04,EX_CUSTOMATTRIBUTE05,EX_CUSTOMATTRIBUTE06,EX_CUSTOMATTRIBUTE07,EX_CUSTOMATTRIBUTE08,EX_CUSTOMATTRIBUTE09,EX_CUSTOMATTRIBUTE10,EX_CUSTOMATTRIBUTE11,EX_CUSTOMATTRIBUTE12,EX_CUSTOMATTRIBUTE13,EX_CUSTOMATTRIBUTE14,EX_CUSTOMATTRIBUTE15,MSN_POOLSERVERNAME,MSN_POOLSERVERDN,MSN_SIPADDRESS,MSN_ANONMY,MSN_MEETINGPOLICYNAME,MSN_MEETINGPOLICYDN,MSN_PHONECOMMUNICATION,MSN_PBX,MSN_LINEPOLICYNAME,MSN_LINEPOLICYDN,MSN_LINEURI,MSN_LINESERVERURI,MSN_FEDERATION,MSN_REMOTEACCESS,MSN_PUBLICIMCONNECTIVITY,MSN_INTERNALIMCONVERSATION,MSN_FEDERATEDIMCONVERSATION,AD_ISUSE,EX_ISUSE,MSN_ISUSE,LOGONPASSWORD,AD_USERID,EX_USERID,MSN_USERID,OUPath,OUName,DomainID,AD_CN,AD_SamAccountName,AD_UserPrincipalName"));
		return resultList;
	}

	/**
	 * 그룹 사용자 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupUserList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectGroupUserListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectGroupUserList", params);
			

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"itemType,UserID,UserCode,MultiDisplayName,JobLevel,JobTitle,JobPosition,Mobile,MailAddress,PhoneNumber,EmpNo,DeptCode,MultiDeptName,CompanyCode,MultiCompanyName,JobType,PhoneNumberInter,IsUse,IsDisplay,IsHR,SortKey"));
			
		}  catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectGroupUserList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectGroupUserList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectGroupUserList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 그룹 멤버 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupMemberList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectGroupMemberListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectGroupMemberList", params);

			resultList.put("list",
					CoviSelectSet.coviSelectJSON(list, "ROWNUM,Type,Code,CodeName,MailAddress,MemberID"));
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectGroupMemberList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectGroupMemberList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectGroupMemberList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 사용자 메일 사용 여부 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int - update 결과 상태
	 * @throws Exception
	 */
	@Override
	public int updateIsMailUser(CoviMap params) throws Exception {
		return coviMapperOne.update("organization.syncmanage.updateIsMailUser", params);
	}

	/**
	 * 사용자 정보 insert
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int insertUser(CoviMap params) throws Exception {
		int returnCnt = 0;

		try {
			LOGGER.debug("insertUser execute [" + params.get("UserCode") + "]");
			params.put("aeskey", aeskey);
			params.put("isSaaS", (isSaaS)?"Y":"N");

			//0.LogonID Dup Check
			LOGGER.debug("insertUser - checkDupLogonID execute");
			returnCnt = (int)coviMapperOne.getNumber("organization.syncmanage.selectDupLogonIDCnt", params);
			if(returnCnt != 0) {
				LOGGER.error("insertUser - checkDupLogonID Failed");
				throw new Exception("insertUser - checkDupLogonID Failed");
			}else {
				LOGGER.debug("insertUser - checkDupLogonID execute complete");
			}

			// 1. insert to sys_object
			LOGGER.debug("insertUser - insertObject execute");
			returnCnt = coviMapperOne.insert("organization.syncmanage.insertObject", params);
			if (returnCnt != 1) {
				LOGGER.error("insertUser - insertObject Failed");
				throw new Exception();
			}
			else {
				LOGGER.debug("insertUser - insertObject execute complete");
			}

			// 2. insert to sys_object_user
			LOGGER.debug("insertUser - insertUserDefaultInfo execute [" + params.get("UserCode") + "]");
			returnCnt = coviMapperOne.insert("organization.syncmanage.insertUserDefaultInfo", params);
			LOGGER.debug("insertUser - insertUserDefaultInfo execute complete");

			if (returnCnt == 1) {
				// 3. insert to sys_object_user_info
				LOGGER.debug("insertUser - insertUserDefaultInfo2 execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.insert("organization.syncmanage.insertUserDefaultInfo2", params);
				LOGGER.debug("insertUser - insertUserDefaultInfo2 complete complete");
			}

			if (returnCnt == 2) {
				// 4. insert to sys_object_user_basegroup
				LOGGER.debug("insertUser - insertUserBasegroupInfo execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.insert("organization.syncmanage.insertUserBasegroupInfo", params);
				LOGGER.debug("insertUser - insertUserBasegroupInfo execute complete");
			}

			if (returnCnt == 3) {
				// 5. insert to sys_object_user_approval
				LOGGER.debug("insertUser - insertUserApprovalInfo execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.insert("organization.syncmanage.insertUserApprovalInfo", params);
				LOGGER.debug("insertUser - insertUserApprovalInfo execute complete");
			}

			if (returnCnt == 4) {
				// 6. insert to sys_object_group_member
				LOGGER.debug("insertUser - insertUserGroupMember execute");
				returnCnt += coviMapperOne.insert("organization.syncmanage.insertUserGroupMember", params);
				LOGGER.debug("insertUser - insertUserGroupMember execute complete");
				params.put("SyncStatus", 'Y');
				if (params.get("SyncManage") == "Sync") {
					String strLog = "사용자 ";
					strLog += "[" + params.get("UserCode") + "] 추가됨";
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "USER", "INSERT", strLog, "");
				}
			}

			// 6. update to sys_object_user photopath
			if (params.get("SyncManage") == "Manage") {
				returnCnt += updateOrgUserPhotoPath(params);
			}
			else {
				if (RedisDataUtil.getBaseConfig("PhotoSync").toString().equalsIgnoreCase("URL")) {
					returnCnt += getImagefromUrl(params.get("PhotoPath").toString(), params.get("UserCode").toString(),
							params.get("PhotoDate").toString()); // 연동할 때 사진 변경일자 필요
				} 
				else if (RedisDataUtil.getBaseConfig("PhotoSync").toString().equalsIgnoreCase("API")) {
					returnCnt += convertBase64toImage(params); // 연동할 때 사진 변경일자 필요
				}
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("insertUser Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 추가 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "USER", "INSERT", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("insertUser Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 추가 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "USER", "INSERT", strLog, "");
			}
		} 
		finally {
			coviMapperOne.insert("organization.syncmanage.insertUserLogList", params);
		}

		return returnCnt;
	}

	/**
	 * 사용자 정보 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateUser(CoviMap params) throws Exception {
		int returnCnt = 0;
		try {
			LOGGER.debug("updateUser execute [" + params.get("UserCode") + "]");

			// 1. update to sys_object
			LOGGER.debug("updateUser - updateObject execute [" + params.get("UserCode") + "]");
			returnCnt = coviMapperOne.update("organization.syncmanage.updateObject", params);
			LOGGER.debug("updateUser - updateObject execute complete");

			// 2. update to sys_object_user
			LOGGER.debug("updateUser - updateUserDefaultInfo execute [" + params.get("UserCode") + "]");
			returnCnt = coviMapperOne.update("organization.syncmanage.updateUserDefaultInfo", params);
			LOGGER.debug("updateUser - updateUserDefaultInfo execute complete");

			if (returnCnt < 2) {
				// 3. insert to sys_object_user_info
				LOGGER.debug("insertUser - updateUserDefaultInfo2 execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.update("organization.syncmanage.updateUserDefaultInfo2", params);
				LOGGER.debug("insertUser - updateUserDefaultInfo2 complete complete");
			}

			if (returnCnt < 3) {
				// 4. update to sys_object_user_basegroup
				LOGGER.debug("updateUser - updateUserBaseGroupInfo execute [" + params.get("UserCode") + "]");

				if ((int) coviMapperOne.getNumber("organization.syncmanage.selectUserBaseGroupInfoCnt", params) > 0) {
					returnCnt += coviMapperOne.insert("organization.syncmanage.updateUserBaseGroupInfo", params);
				} 
				else {
					returnCnt += coviMapperOne.insert("organization.syncmanage.insertUserBasegroupInfo", params);
				}

				LOGGER.debug("updateUser - updateUserBaseGroupInfo execute complete");
			}

			if (returnCnt < 4) {
				// 5. update to sys_object_user_approval
				if (RedisDataUtil.getBaseConfig("IsUseApprovalSet").toString().equals("Y")) {
					LOGGER.debug("updateUser - updateUserApprovalInfo execute [" + params.get("UserCode") + "]");
					returnCnt += coviMapperOne.insert("organization.syncmanage.updateUserApprovalInfo", params);
					LOGGER.debug("updateUser - updateUserApprovalInfo execute complete");
				} 
				else returnCnt += 1;
			}

			if (returnCnt < 5) {
				// 6. delete to sys_object_group_member
				LOGGER.debug("updateUser - deleteUserGroupDefaultMember execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.delete("organization.syncmanage.deleteUserGroupDefaultMember", params);
				LOGGER.debug("updateUser - deleteUserGroupDefaultMember execute complete");

				// 7. insert to sys_object_group_member
				LOGGER.debug("updateUser - insertUserGroupMember execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.insert("organization.syncmanage.insertUserGroupMember", params);
				LOGGER.debug("updateUser - insertUserGroupMember execute complete");

				params.put("SyncStatus", 'Y');
				if (params.get("SyncManage") == "Sync") {
					String strLog = "사용자 ";
					strLog += "[" + params.get("UserCode") + "] 수정됨";
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "USER", "UPDATE", strLog, "");
				}
			}

			// 8. update to sys_object_user photopath
			if (params.get("SyncManage") == "Manage") {
				LOGGER.debug("updateUser - updateOrgUserPhotoPath execute [" + params.get("UserCode") + "]");
				returnCnt += updateOrgUserPhotoPath(params);
				LOGGER.debug("updateUser - updateOrgUserPhotoPath execute complete");
			} 
			else {
				if (RedisDataUtil.getBaseConfig("PhotoSync").toString().equalsIgnoreCase("URL")) {
					returnCnt += getImagefromUrl(params.get("PhotoPath").toString(), params.get("UserCode").toString(),
							params.get("PhotoDate").toString()); // 연동할 때 사진 변경일자 필요
				} 
				else if (RedisDataUtil.getBaseConfig("PhotoSync").toString().equalsIgnoreCase("API")) {
					returnCnt += convertBase64toImage(params); // 연동할 때 사진 변경일자 필요
				}
			}
		}  catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("updateUser Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 수정 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "USER", "UPDATE", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("updateUser Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 수정 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "USER", "UPDATE", strLog, "");
			}
		}
		finally {
			coviMapperOne.insert("organization.syncmanage.insertUserLogList", params);
		}
		
		return returnCnt;
	}

	/**
	 * 사용자 사진정보 업데이트 - 관리자화면
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateOrgUserPhotoPath(CoviMap params) throws Exception {
		FileOutputStream out = null;
		int retCnt = 0;

		if (!params.getString("PhotoPath").equals("")) {
			try {
				final String path = RedisDataUtil.getBaseConfig("ProfileImageFolderPath");
				String companyCode = SessionHelper.getSession("DN_Code");
				String backStorage = RedisDataUtil.getBaseConfig("BackStorage").replace("{0}", companyCode);

				// 0. 디렉토리, 파일 체크
				if(awsS3.getS3Active()) {
					String dir = FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1) + backStorage + path;
					String key = dir+"/"+params.getString("UserCode");
					if(awsS3.exist(key)){
						awsS3.delete(key);
					}
					String key2 = dir+"/"+params.getString("UserCode") + "_org";
					if(awsS3.exist(key2)){
						awsS3.delete(key2);
					}
					String orgKey = dir+"/"+params.getString("UserCode") + "_org.jpg";
					// 1. 파일 생성(리사이즈, 원본)
					MultipartFile mFile = (MultipartFile) params.get("FileInfo");
					awsS3.upload(mFile.getInputStream(), orgKey, mFile.getContentType(), mFile.getSize());
					String thumKey = FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1)
							+ backStorage + path + params.getString("UserCode") + ".jpg";

					fileUtilSvc.makeThumb(thumKey, mFile.getInputStream()); //썸네일 저장
				}else {
					File dir = new File(
							FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1) + backStorage + path);
					if (!dir.exists()) {
						boolean dReturn = dir.mkdirs();
						if (!dReturn)
							throw new IOException("updateOrgUserPhotoPath : Unable to create path.");
					}

					File existFile = new File(dir, params.getString("UserCode"));
					if (existFile.exists()) {
						boolean dReturn =false;
						try{
							dReturn = existFile.delete();	
						}
						catch (NullPointerException ex) {
							throw new IOException("updateUserPhotoPath : Fail to delete file." + existFile.getName());
						}
						catch(Exception ex){
							throw new IOException("updateUserPhotoPath : Fail to delete file." + existFile.getName());
						}

						dReturn = new File(dir, params.getString("UserCode") + "_org").delete();

						if (!dReturn)
							throw new IOException("updateOrgUserPhotoPath : Fail to delete file.");
					}

					// 1. 파일 생성(리사이즈, 원본)
					MultipartFile mFile = (MultipartFile) params.get("FileInfo");
					File file = new File(dir, params.getString("UserCode") + "_org.jpg");
					mFile.transferTo(file);

					Image src = ImageIO.read(file);
					BufferedImage resizeImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
					resizeImage.getGraphics().drawImage(src.getScaledInstance(200, 200, Image.SCALE_SMOOTH), 0, 0, null);
					out = new FileOutputStream(FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1)
							+ backStorage + path + params.getString("UserCode") + ".jpg");
					ImageIO.write(resizeImage, "jpg", out);
				}

				// 2. photoPath 업데이트
				params.put("PhotoPath", RedisDataUtil.getBaseConfig("BackStorage").replace("{0}", companyCode) + path
						+ params.getString("UserCode") + ".jpg");
				params.put("UserCode", params.getString("UserCode"));

				retCnt = coviMapperOne.update("organization.syncmanage.updateOrgUserPhotoPath", params);
				if (retCnt > 0) {
					if (params.get("SyncManage") == "Sync") {
						insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "UR", "DB",
								"updateOrgUserPhotoPath success [" + params.get("UserCode") + "]", "");
					}
				}
			} catch (NullPointerException ex) {
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "DB",
							"updateOrgUserPhotoPath Error [ObjectCode : " + params.get("UserCode") + "] "
									+ "[Message : " + ex.getMessage() + "]",
							"");
					LOGGER.error(ex.getLocalizedMessage(), ex);
				}
			} catch (Exception ex) {
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "DB",
							"updateOrgUserPhotoPath Error [ObjectCode : " + params.get("UserCode") + "] "
									+ "[Message : " + ex.getMessage() + "]",
							"");
					LOGGER.error(ex.getLocalizedMessage(), ex);
				}
			} 
			finally {
				if (out != null) {
					out.close();
				}
			}
		}
		return retCnt;
	}

	/**
	 * 자동동기화 사용자 사진정보 업데이트
	 */
	@Override
	public int convertBase64toImage(CoviMap params) throws Exception {
		int retCnt = 0;

		String apiURL = null;
		String method = "GET";

		SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMdd");
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -2);
		int beforeday = Integer.parseInt(dformat.format(today.getTime()));

		if (!params.getString("PhotoPath").equals("") && !params.getString("PHOTO_DATE").equals("")
				&& beforeday <= Integer.parseInt(params.getString("PHOTO_DATE"))) {
			FileOutputStream fos = null;
			try {
				final String path = RedisDataUtil.getBaseConfig("ProfileImagePath");
				apiURL = RedisDataUtil.getBaseConfig("PhotoAPI").toString();
				LOGGER.debug(apiURL);

				String sPERSONID = params.get("PERSONID").toString();

				apiURL = apiURL + sPERSONID;
				CoviMap jObj = getJson(apiURL, method);

				if (jObj.get("code").toString().equals("200")
						&& jObj.get("success").toString().equalsIgnoreCase("true")) {
					byte[] imageByte = Base64.decodeBase64(jObj.get("data").toString());
					
					String companyCode = SessionHelper.getSession("DN_Code");
					String backStorage = RedisDataUtil.getBaseConfig("BackStorage").replace("{0}", companyCode);

					File target = new File(FileUtil.getBackPath().substring(0, FileUtil.getBackPath().length() - 1)
							+ backStorage + path + sPERSONID + ".jpg");
					boolean bReturn = target.createNewFile();
					if (!bReturn)
						throw new IOException("convertBase64toImage : Fail to create new file.");

					fos = new FileOutputStream(target);
					fos.write(imageByte);
					fos.close();

					params.put("PhotoPath", path + sPERSONID + ".jpg");
					params.put("UserCode", params.getString("UserCode"));

					retCnt = coviMapperOne.update("organization.syncmanage.updateOrgUserPhotoPath", params);
					if (retCnt > 0) {
						if (params.get("SyncManage") == "Sync") {
							insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "UR",
									"DB", "ConvertBase64toImage success [" + params.get("UserCode") + "]", "");
						}
					}
				} else {
					if (params.get("SyncManage") == "Sync") {
						insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "DB",
								"ConvertBase64toImage Error [ObjectCode : " + params.get("UserCode") + "] "
										+ "[Message : " + jObj.get("message").toString() + "]",
								"");
						insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "DB",
								"ConvertBase64toImage Error [ObjectCode : " + params.get("UserCode") + "] "
										+ "[StackTrace : " + jObj.get("message").toString() + "]",
								"");
					}
				}

			} catch (NullPointerException e) {
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "DB",
							"ConvertBase64toImage Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
									+ e.getMessage() + "]",
							"");
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			} catch (Exception e) {
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "DB",
							"ConvertBase64toImage Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
									+ e.getMessage() + "]",
							"");
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			}finally {
				if(fos != null) {
					try {
						fos.close();
					} catch (NullPointerException ex) {
						LOGGER.error("OrganizationSyncManageSvcImpl.convertBase64toImage", ex);
					} catch (Exception ex) {
						LOGGER.error("OrganizationSyncManageSvcImpl.convertBase64toImage", ex);
					}
				}
			}
		}

		return retCnt;
	}

	/**
	 * URL 사진 경로에서 이미지 생성하기
	 *
	 * @param strUrl String
	 * @param strUserCode String
	 * @param strPhotoDate String
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getImagefromUrl(String strUrl, String strUserCode, String strPhotoDate) throws Exception {
		int iReturn = 0;
		final String path = RedisDataUtil.getBaseConfig("ProfileImagePath");
		URL url = null;
		CoviMap params = new CoviMap();

		SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMdd");
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -2);
		int beforeday = Integer.parseInt(dformat.format(today.getTime()));

		if (!strUrl.equals("") && !strPhotoDate.equals("") && beforeday <= Integer.parseInt(strPhotoDate)) {
			try {
				url = new URL(strUrl);
				try (InputStream in = url.openStream(); OutputStream out = new FileOutputStream(FileUtil.getBackPath() + path + strUserCode + ".jpg");) {
					while (true) {
						int data = in.read();
						if (data == -1) {
							break;
						}
						out.write(data);
					}
					params.put("PhotoPath", path + strUserCode + ".jpg");
					params.put("UserCode", strUserCode);

					iReturn = coviMapperOne.update("organization.syncmanage.updateOrgUserPhotoPath", params);
				}
			} catch (NullPointerException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
		
		return iReturn;
	}

	/**
	 * 사용자 정보 delete
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int deleteUser(CoviMap params) throws Exception {
		int returnCnt = 0;
		try {
			LOGGER.debug("deleteUser execute [" + params.get("UserCode") + "]");

			// 1. delete from sys_object
			LOGGER.debug("deleteUser - deleteObject execute");
			returnCnt = coviMapperOne.update("organization.syncmanage.deleteObject", params);
			LOGGER.debug("deleteUser - deleteObject execute complete");

			// 2. delete from sys_object_user
			LOGGER.debug("deleteUser - deleteUser execute [" + params.get("UserCode") + "]");
			returnCnt = coviMapperOne.update("organization.syncmanage.deleteUser", params);
			LOGGER.debug("deleteUser - deleteUser execute complete");

			if (returnCnt == 1) {
				// 3. delete from sys_object_user_basegroup
				LOGGER.debug("deleteUser - deleteUserBaseGroup execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.update("organization.syncmanage.deleteUserBaseGroup", params);
				LOGGER.debug("deleteUser - deleteUserBaseGroup execute complete");
			}

			if (returnCnt == 2) {
				// 4. delete from sys_object_user_basegroup (AddJob)
				LOGGER.debug("deleteUser - deleteAddJobBaseGroup execute [" + params.get("UserCode") + "]");
				coviMapperOne.delete("organization.syncmanage.deleteAddJobBaseGroup", params);
				LOGGER.debug("deleteUser - deleteAddJobBaseGroup execute");

				// 5. delete from sys_object_group_member
				LOGGER.debug("deleteUser - deleteUserGroupMember execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.delete("organization.syncmanage.deleteUserGroupMember", params);
				LOGGER.debug("deleteUser - deleteUserGroupMember execute");
			}

			if (returnCnt > 2) {
				// 5. delete from sys_object_group_member
				LOGGER.debug("deleteUser - insertRetiredGroupMember execute [" + params.get("UserCode") + "]");
				returnCnt += coviMapperOne.insert("organization.syncmanage.insertRetiredGroupMember", params);
				LOGGER.debug("deleteUser - insertRetiredGroupMember execute");
				params.put("SyncStatus", 'Y');
				
				if (params.get("SyncManage") == "Sync") {
					String strLog = "사용자 ";
					strLog += "[" + params.get("UserCode") + "] 삭제됨";
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "USER", "DELETE", strLog, "");
				}
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteUser Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "USER", "DELETE", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteUser Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "USER", "DELETE", strLog, "");
			}
		} 
		finally {
			coviMapperOne.insert("organization.syncmanage.insertUserLogList", params);
		}
		return returnCnt;
	}

	// 겸직 관련
	/**
	 * 겸직 목록 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAddJobList(CoviMap params) throws Exception {

		CoviMap resultList = new CoviMap();
		CoviMap page = new CoviMap();
		CoviList list = null;
		int cnt = 0;

		try {
			if(params.containsKey("pageNo")) {
				cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectAddJobListCnt", params);
	 			page = ComUtils.setPagingData(params,cnt);
	 			params.addAll(page);
	 			resultList.put("page", page);
	 		}
			list = coviMapperOne.list("organization.syncmanage.selectAddJobList", params);
			
			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"NO,UserCode,UserName,CompanyCode,DeptCode,JobTitleCode,JobPositionCode,JobLevelCode,JobType,CompanyName,DeptName,JobTitleName,JobPositionName,JobLevelName,IsHR"));
			
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectAddJobList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectAddJobList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectAddJobList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 사용자 겸직 정보 cnt
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserAddJobListCnt(CoviMap params) throws Exception {
		CoviMap resultList = null;
		int cnt = 0;

		try {
			resultList = new CoviMap();
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectUserAddJobListCnt", params);

			resultList.put("cnt", cnt);
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}  catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		return resultList;
	}

	/**
	 * 겸직 정보 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAddJobInfo(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectAddJobInfo", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"NO,UserCode,UserName,CompanyCode,DeptCode,JobTitleCode,JobPositionCode,JobLevelCode,JobType,CompanyName,DeptName,JobTitleName,JobPositionName,JobLevelName,IsHR"));

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectAddJobInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectAddJobInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectAddJobInfo Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 겸직 정보 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAddJobInfoList(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectAddJobInfoList", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"NO,UserCode,UserName,LogonID,DisplayName,CompanyCode,DeptCode,JobTitleCode,JobPositionCode,JobLevelCode,JobType,CompanyName,DeptName,JobTitleName,JobPositionName,JobLevelName,IsHR"));

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectAddJobInfoList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectAddJobInfoList Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectAddJobInfoList Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 겸직 정보(사용자 기본 정보) select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectAddJobUserInfo(CoviMap params) throws Exception {

		CoviMap resultList = null;
		CoviList list = null;

		try {
			resultList = new CoviMap();
			list = coviMapperOne.list("organization.syncmanage.selectUserInfo", params);

			resultList.put("list", CoviSelectSet.coviSelectJSON(list,
					"JobPositionCode,JobPositionName,JobTitleCode,JobTitleName,JobLevelCode,JobLevelName,UserID,MultiDisplayName,DeptCode,MultiDeptName,CompanyCode,MultiCompanyName"));

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("selectAddJobUserInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (NullPointerException e) {
			LOGGER.error("selectAddJobUserInfo Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("selectAddJobUserInfo Error  " + "[Message : " + e.getMessage() + "]");
		}

		return resultList;
	}

	/**
	 * 겸직 추가
	 * 
	 * @param params CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public int insertAddjob(CoviMap params) throws Exception {
		int iReturn = 0;

		try {
			LOGGER.debug("insertAddjob execute [" + params.get("UserCode") + "]");

			// 1. insert to sys_object_user_basegroup
			LOGGER.debug("insertAddjob - insertUserBasegroupInfo execute");
			iReturn = coviMapperOne.insert("organization.syncmanage.insertUserBasegroupInfo", params);
			LOGGER.debug("insertAddjob - insertUserBasegroupInfo execute complete");
			// 2. delete to sys_object_group_member
			LOGGER.debug("insertAddjob - deleteUserGroupDefaultMember execute");
			iReturn += coviMapperOne.delete("organization.syncmanage.deleteUserGroupDefaultMember", params);
			LOGGER.debug("insertAddjob - deleteUserGroupDefaultMember execute complete");
			// 3. insert to sys_object_group_member
			LOGGER.debug("insertAddjob - insertUserGroupMember execute");
			iReturn += coviMapperOne.insert("organization.syncmanage.insertGroupMemberUser", params);
			LOGGER.debug("insertAddjob - insertUserGroupMember execute complete");

			params.put("SyncStatus", 'Y');
			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 추가됨";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "ADDJOB", "INSERT", strLog, "");
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("insertAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 추가 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "INSERT", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("insertAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 추가 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "INSERT", strLog, "");
			}
		} 
		finally {
			coviMapperOne.insert("organization.syncmanage.insertAddJobLogList", params);
		}

		return iReturn;
	}

	/**
	 * 겸직 수정
	 * 
	 * @param params CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public int updateAddjob(CoviMap params) throws Exception {
		int bReturn = 0;

		try {
			LOGGER.debug("updateAddjob execute [" + params.get("UserCode") + "]");
			// 1.겸직 삭제
			bReturn = deleteAddjob(params);
			// 2.겸직추가 (SYS_OBJECT_USER_BASEGROUP Insert)
			bReturn += insertAddjob(params);

			LOGGER.debug("updateAddjob - updateObjectUserBasegroup execute complete");
			params.put("SyncStatus", 'Y');
			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 수정됨";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "ADDJOB", "UPDATE", strLog, "");
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("updateAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 수정 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "UPDATE", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("updateAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 수정 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "UPDATE", strLog, "");
			}
		}

		return bReturn;
	}

	/**
	 * 겸직 수정 (Manage)
	 * 
	 * @param params CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public int updateAddjobManage(CoviMap params) throws Exception {
		int bReturn = 0;

		try {
			LOGGER.debug("updateAddjobManage execute [" + params.get("UserCode") + "]");

			// 1. 겸직 수정
			bReturn = coviMapperOne.update("organization.syncmanage.updateAddJob", params);

			LOGGER.debug("updateAddjobManage - updateObjectUserBasegroup execute complete");
			params.put("SyncStatus", 'Y');
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info",
						params.get("JobType").toString(), "DB", "updateAddjob success [" + params.get("UserCode") + "]",
						"");
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("updateAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error",
						params.get("JobType").toString(), "DB", "updateAddjob Error [ObjectCode : "
								+ params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]",
						"");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("updateAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error",
						params.get("JobType").toString(), "DB", "updateAddjob Error [ObjectCode : "
								+ params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]",
						"");
			}
		}

		return bReturn;
	}

	/**
	 * 겸직 삭제 (Sync)
	 * 
	 * @param params CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public int deleteAddjobSync(CoviMap params) throws Exception {
		int iReturn = 0;

		try {
			LOGGER.debug("deleteAddjob execute [" + params.get("UserCode") + "]");

			LOGGER.debug("deleteAddjob - deleteObjectUserBasegroup execute");
			iReturn = coviMapperOne.update("organization.syncmanage.deleteObjectUserBasegroupForAddjob", params);

			if (iReturn > 0) {
				iReturn = coviMapperOne.delete("organization.syncmanage.deleteUserGroupDefaultMember", params);
			}
			
			if (iReturn > 0) {
				iReturn = coviMapperOne.insert("organization.syncmanage.insertGroupMemberUser", params);
				params.put("SyncStatus", 'Y');
				
				if (params.get("SyncManage") == "Sync") {
					String strLog = "사용자 ";
					strLog += "[" + params.get("UserCode") + "] 겸직 삭제됨";
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "ADDJOB", "DELETE", strLog, "");
				}
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "DELETE", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : "
					+ ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "DELETE", strLog, "");
			}
		} 
		finally {
			coviMapperOne.insert("organization.syncmanage.insertAddJobLogList", params);
		}
		return iReturn;
	}

	/**
	 * 겸직 삭제
	 * 
	 * @param params CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public int deleteAddjob(CoviMap params) throws Exception {
		int iReturn = 0;

		try {
			LOGGER.debug("deleteAddjob execute [" + params.get("UserCode") + "]");

			LOGGER.debug("deleteAddjob - deleteObjectUserBasegroup execute");
			iReturn = coviMapperOne.update("organization.syncmanage.deleteObjectUserBasegroup", params);

			if (iReturn > 0) {
				iReturn = coviMapperOne.delete("organization.syncmanage.deleteUserGroupDefaultMember", params);
			}
			
			if (iReturn > 0) {
				iReturn = coviMapperOne.delete("organization.syncmanage.insertGroupMemberUser", params);
				params.put("SyncStatus", 'Y');
				
				if (params.get("SyncManage") == "Sync") {
					String strLog = "사용자 ";
					strLog += "[" + params.get("UserCode") + "] 겸직 삭제됨";
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "ADDJOB", "DELETE", strLog, "");
				}
			}
		} catch (NullPointerException ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "DELETE", strLog, "");
			}
		} catch (Exception ex) {
			params.put("SyncStatus", 'N');
			LOGGER.error("deleteAddjob Error [ObjectCode : " + params.get("UserCode") + "] " + "[Message : " + ex.getMessage() + "]", ex);

			if (params.get("SyncManage") == "Sync") {
				String strLog = "사용자 ";
				strLog += "[" + params.get("UserCode") + "] 겸직 삭제 실패";
				strLog += "[Message : " + ex.getMessage() + "]";
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "ADDJOB", "DELETE", strLog, "");
			}
		} 
		finally {
			coviMapperOne.insert("organization.syncmanage.insertAddJobLogList", params);
		}
		
		return iReturn;
	}

	/**
	 * 겸직 설정 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap updateAddJobSetting(CoviMap params) throws Exception {
		CoviMap resultObj = new CoviMap();
		int returnCnt = 0;

		returnCnt = coviMapperOne.update("organization.syncmanage.updateAddJobSetting", params);

		if (returnCnt == 1)
			resultObj.put("result", "OK");
		else
			resultObj.put("result", "FAIL");

		return resultObj;
	}

	/**
	 * 그룹 멤버 추가
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addGroupMember(CoviMap params) throws Exception {
		int iCnt = 0;

		String URList1 = "";
		String GRList1 = "";
		if (params.get("URList") != null && !params.get("URList").equals("")) {
			URList1 = params.get("URList").toString();
		}
		if (params.get("GRList") != null && !params.get("GRList").equals("")) {
			GRList1 = params.get("GRList").toString();
		}

		if (URList1 != null && !URList1.equals("")) {
			String URList[] = URList1.split(";");
			for (int i = 0; i < URList.length; i++) {
				params.put("UserCode", URList[i]);

				int nCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectIsDuplicateGroupMemberUser",
						params);
				if (nCnt == 0) {
					params.put("URList", URList[i]);
					iCnt += coviMapperOne.insert("organization.syncmanage.insertAddGroupMemberUser", params);
				}
			}
		}
		if (GRList1 != null && !GRList1.equals("")) {
			String GRList[] = GRList1.split(";");
			for (int i = 0; i < GRList.length; i++) {
				params.put("MemberGroupCode", GRList[i]);

				int nCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectIsDuplicateGroupMember",
						params);
				if (nCnt == 0) {
					params.put("GRList", GRList[i]);
					iCnt += coviMapperOne.insert("organization.syncmanage.insertGroupMemberGroup", params);
				}
			}
		}

		return iCnt;
	}

	/**
	 * 그룹 멤버 삭제
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int deleteGroupMember(CoviMap params) throws Exception {
		int iCnt = 0;
		if (params.get("deleteDataUser") != null) {
			iCnt += coviMapperOne.delete("organization.syncmanage.deleteGroupMemberUser", params);
		}
		if (params.get("deleteDataGroup") != null) {
			iCnt += coviMapperOne.delete("organization.syncmanage.deleteGroupMemberGroup", params);
		}
		return iCnt;
	}

	// 인디메일 API
	/**
	 * 인디메일 연동 여부
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean getIndiSyncTF() throws Exception {
		boolean bReturn;
		bReturn = RedisDataUtil.getBaseConfig("IsSyncIndi").equals("Y") ? true : false;
		return bReturn;
	}

	/**
	 * IndiMail 사용자 계정상태 확인
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public CoviMap getUserStatus(CoviMap params) throws Exception {
		String apiURL = null;
		String sMode = "?job=userStatus";
		String sDomain = null;
		String method = "POST";
		CoviMap jObj = new CoviMap();

		try {
			apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode;
			LOGGER.debug(apiURL);

			String sMailAddress = params.get("MailAddress").toString();
			sDomain = sMailAddress.split("@")[1];// RedisDataUtil.getBaseConfig("IndiMailDomain").toString();

			String sLogonID = sMailAddress.split("@")[0];// params.get("LogonID").toString();

			sLogonID = java.net.URLEncoder.encode(sLogonID, java.nio.charset.StandardCharsets.UTF_8.toString());

			apiURL = apiURL + String.format("&id=%s&domain=%s", sLogonID, sDomain);
			jObj = getJson(apiURL, method);
		} catch (NullPointerException ex) {
			LOGGER.error("IndiMail getUserStatus Error [" + params.get("UserCode") + "]" + "[Message : "
					+ ex.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail getUserStatus Error [" + params.get("UserCode") + "]" + "[Message : "
								+ ex.getMessage() + "]",
						"");
			}
		} catch (Exception ex) {
			LOGGER.error("IndiMail getUserStatus Error [" + params.get("UserCode") + "]" + "[Message : "
					+ ex.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail getUserStatus Error [" + params.get("UserCode") + "]" + "[Message : "
								+ ex.getMessage() + "]",
						"");
			}
		}
		return jObj;
	}

	/**
	 * IndiMail 사용자 계정 추가
	 * 
	 * @param params
	 *            - CoviMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public CoviMap addUser(CoviMap params) throws Exception {
		String apiURL = null;
		String sMode = "?job=addUser";
		String sDomain = null;
		String method = "GET";
		CoviMap jObj = new CoviMap();

		try {
			apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode;
			LOGGER.debug(apiURL);

			String sMailAddress = params.get("MailAddress").toString();

			sDomain = sMailAddress.split("@")[1];// RedisDataUtil.getBaseConfig("IndiMailDomain").toString();

			String sLogonID = sMailAddress.split("@")[0];// params.get("LogonID").toString();
			// String sPassword = params.get("DecLogonPassword").toString();
			AES aes = new AES(aeskey, "N");
			String sPassword = aes.encrypt(params.get("DecLogonPassword").toString());
			String sName = params.get("DisplayName").toString();
			String sEnc = "";
			String sCompany = params.get("CompanyCode").toString();
			String sDeptId = params.get("GroupMailAddress").toString();
			String sDeptName = params.get("DeptName").toString();
			String sStatus = "A";
			String slang = params.get("LanguageCode").toString();
			String sTimeZone = params.get("TimeZoneCode").toString();
			String sMailSize = RedisDataUtil.getBaseConfig("IndiMailSize").toString();
			String sFileSize = "10";
			String sContacts = "1";
			sMailSize = "".equals(sMailSize) ? "1024" : sMailSize;
			sLogonID = java.net.URLEncoder.encode(sLogonID, java.nio.charset.StandardCharsets.UTF_8.toString());
			sName = java.net.URLEncoder.encode(sName, java.nio.charset.StandardCharsets.UTF_8.toString());
			sDeptName = java.net.URLEncoder.encode(sDeptName, java.nio.charset.StandardCharsets.UTF_8.toString());
			sDeptId = java.net.URLEncoder.encode(sDeptId, java.nio.charset.StandardCharsets.UTF_8.toString());

			apiURL = apiURL + String.format(
					"&id=%s&name=%s&domain=%s&company=%s&enc=%s&deptId=%s&deptName=%s&status=%s&lang=%s&timezone=%s&mailsize=%s&filesize=%s&contacts=%s&pw=%s",
					sLogonID, sName, sDomain, sCompany, sEnc, sDeptId, sDeptName, sStatus, slang, sTimeZone,
					sMailSize, sFileSize, sContacts, sPassword);
			jObj = getJson(apiURL, method);

			if (!jObj.get("returnCode").toString().equals("0")||!"SUCCESS".equals(jObj.get("returnMsg"))) {
				LOGGER.error(jObj.toString());
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
							"IndiMail addUser Error [" + params.get("UserCode") + "]" + "[Message : " + jObj.toString()
									+ "]",
							"");
				}
			} else {
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "UR", "IndiMail",
							"IndiMail addUser Success [" + params.get("UserCode") + "]", "");
				}
			}
		} catch (NullPointerException e) {
			LOGGER.error(
					"IndiMail addUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail addUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		} catch (Exception e) {
			LOGGER.error(
					"IndiMail addUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail addUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		}
		return jObj;
	}

	/**
	 * IndiMail 사용자 계정 수정/삭제
	 * 
	 * @param params
	 *            - CoviMap
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public CoviMap modifyUser(CoviMap params) throws Exception {
		StringBuffer apiURL = new StringBuffer();
		String sMode = "?job=modifyUser";
		String sDomain = null;
		String sLogonID = null;
		String method = "GET";
		CoviMap jObj = new CoviMap();

		try {
			apiURL.append(RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode);
			LOGGER.debug(apiURL.toString());

			String sMailAddress = params.get("MailAddress").toString();

			sDomain = sMailAddress.split("@")[1];// RedisDataUtil.getBaseConfig("IndiMailDomain").toString();
			String sMailStatus = params.get("mailStatus").toString();

			// S 인디메일 계정 비활성화
			if (sMailStatus.equals("S")) {
				// 동기화시
				if (params.get("SyncManage") == "Sync") {
					sLogonID = java.net.URLEncoder.encode(sMailAddress.split("@")[0].toString(),
							java.nio.charset.StandardCharsets.UTF_8.toString());
					// sLogonID = java.net.URLEncoder.encode(params.get("LogonID").toString(),
					// java.nio.charset.StandardCharsets.UTF_8.toString());
					sMailStatus = java.net.URLEncoder.encode(sMailStatus,
							java.nio.charset.StandardCharsets.UTF_8.toString());
					String sName = params.get("DisplayName").toString();
					String sCompany = params.get("CompanyCode").toString();
					String sDeptId = "";
					String sOldDeptId = params.get("oldGroupMailAddress") != null
							&& !params.get("oldGroupMailAddress").toString().equalsIgnoreCase("undefined")
							&& !params.get("oldGroupMailAddress").equals("")
									? params.get("oldGroupMailAddress").toString()
									: "";
					sOldDeptId = java.net.URLEncoder.encode(sOldDeptId,
							java.nio.charset.StandardCharsets.UTF_8.toString());

					apiURL.append(
							String.format("&id=%s&name=%s&status=%s&domain=%s&company=%s&deptId=%s&deptOldEmail=%s",
									sLogonID, sName, sMailStatus, sDomain, sCompany, sDeptId, sOldDeptId));
					jObj = getJson(apiURL.toString(), method);
					if (!jObj.get("returnCode").toString().equals("0")||!"SUCCESS".equals(jObj.get("returnMsg"))) {
						LOGGER.error(jObj.toString()); // print error context
						if (params.get("SyncManage") == "Sync") {
							insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR",
									"IndiMail", "IndiMail modifyUser Error [" + params.get("UserCode") + "]"
											+ "[Message : " + jObj.toString() + "]",
									"");
						}
					} else {
						if (params.get("SyncManage") == "Sync") {
							insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "UR",
									"IndiMail", "IndiMail modifyUser Success [" + params.get("UserCode") + "]", "");
						}
					}
				} else {
					// 조직관리
					sLogonID = sMailAddress.split("@")[0].toString();
					params.put("LogonID", sLogonID);

					CoviMap reObject = getUserStatus(params);
					String rReturnCode = reObject.get("returnCode").toString();
					String rResult = reObject.get("result").toString();
					if (rReturnCode.equals("0") && rResult.equals("0")) { // 계정이 존재하면
						sLogonID = java.net.URLEncoder.encode(sLogonID,
								java.nio.charset.StandardCharsets.UTF_8.toString());
						sMailStatus = java.net.URLEncoder.encode(sMailStatus,
								java.nio.charset.StandardCharsets.UTF_8.toString());
						String sName = params.get("DisplayName").toString();
						sName = java.net.URLEncoder.encode(sName, java.nio.charset.StandardCharsets.UTF_8.toString());
						String sCompany = params.get("CompanyCode").toString();
						String sDeptId = "";
						String sOldDeptId = params.get("oldGroupMailAddress") != null
								&& !params.get("oldGroupMailAddress").toString().equalsIgnoreCase("undefined")
								&& !params.get("oldGroupMailAddress").equals("")
										? params.get("oldGroupMailAddress").toString()
										: "";
						sOldDeptId = java.net.URLEncoder.encode(sOldDeptId,
								java.nio.charset.StandardCharsets.UTF_8.toString());

						apiURL.append(
								String.format("&id=%s&name=%s&status=%s&domain=%s&company=%s&deptId=%s&deptOldEmail=%s",
										sLogonID, sName, sMailStatus, sDomain, sCompany, sDeptId, sOldDeptId));
						jObj = getJson(apiURL.toString(), method);
						if (!jObj.get("returnCode").toString().equals("0")||!"SUCCESS".equals(jObj.get("returnMsg"))) {
							LOGGER.error(jObj.toString()); // print error context
							if (params.get("SyncManage") == "Sync") {
								insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR",
										"IndiMail", "IndiMail modifyUser Error [" + params.get("UserCode") + "]"
												+ "[Message : " + jObj.toString() + "]",
										"");
							}
						} else {
							if (params.get("SyncManage") == "Sync") {
								insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "UR",
										"IndiMail", "IndiMail modifyUser Success [" + params.get("UserCode") + "]", "");
							}
						}
					}
				}
			} else {
				sLogonID = sMailAddress.split("@")[0].toString();
				sLogonID = java.net.URLEncoder.encode(sLogonID, java.nio.charset.StandardCharsets.UTF_8.toString());
				AES aes = new AES(aeskey, "N");
				String sPassword = aes.encrypt(params.get("DecLogonPassword").toString());
				String sName = params.get("DisplayName").toString();
				sName = java.net.URLEncoder.encode(sName, java.nio.charset.StandardCharsets.UTF_8.toString());
				String sCompany = params.get("CompanyCode").toString();
				String sDeptId = params.get("GroupMailAddress").toString().isEmpty() ? ""
						: params.get("GroupMailAddress").toString();
				sMailStatus = java.net.URLEncoder.encode(sMailStatus,
						java.nio.charset.StandardCharsets.UTF_8.toString());
				String sOldDeptId = params.get("oldGroupMailAddress") != null
						&& !params.get("oldGroupMailAddress").toString().equalsIgnoreCase("undefined")
						&& !params.get("oldGroupMailAddress").equals("")
						&& !sDeptId.equals(params.get("oldGroupMailAddress"))
								? params.get("oldGroupMailAddress").toString()
								: "";
				sDeptId = java.net.URLEncoder.encode(sDeptId, java.nio.charset.StandardCharsets.UTF_8.toString());
				sOldDeptId = java.net.URLEncoder.encode(sOldDeptId, java.nio.charset.StandardCharsets.UTF_8.toString());

				/*apiURL.append(
						String.format("&id=%s&name=%s&status=%s&domain=%s&company=%s&deptId=%s&deptOldEmail=%s&pw=%s",
								sLogonID, sName, sMailStatus, sDomain, sCompany,
								sDeptId, sOldDeptId, sPassword));*/
				apiURL.append(
						String.format("&id=%s&name=%s&status=%s&domain=%s&company=%s&deptId=%s&deptOldEmail=%s",
								sLogonID, sName, sMailStatus, sDomain, sCompany,
								sDeptId, sOldDeptId));
				jObj = getJson(apiURL.toString(), method);
				if (!jObj.get("returnCode").toString().equals("0")||!"SUCCESS".equals(jObj.get("returnMsg"))) {
					LOGGER.error(jObj.toString()); // print error context
					if (params.get("SyncManage") == "Sync") {
						insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR",
								"IndiMail", "IndiMail modifyUser Error [" + params.get("UserCode") + "]" + "[Message : "
										+ jObj.toString() + "]",
								"");
					}
				} else {
					if (params.get("SyncManage") == "Sync") {
						insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Info", "UR",
								"IndiMail", "IndiMail modifyUser Success [" + params.get("UserCode") + "]", "");
					}
				}

			}
		} catch (NullPointerException e) {
			LOGGER.error("IndiMail modifyUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
					+ "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail modifyUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		} catch (Exception e) {
			LOGGER.error("IndiMail modifyUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
					+ "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail modifyUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		}
		return jObj;
	}

	/**
	 * IndiMail 비밀번호 수정
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int modifyPass(CoviMap params) throws Exception {
		int iReturn = 1;
		String apiURL = null;
		String sMode = "?job=modifyPass";
		String sDomain = null;
		String method = "POST";
		String sPassword = null;

		try {
			sPassword = params.get("Returnpass").toString();

			apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode;
			LOGGER.debug(apiURL);

			String sMailAddress = params.get("MailAddress").toString();

			sDomain = sMailAddress.split("@")[1];// RedisDataUtil.getBaseConfig("IndiMailDomain").toString();

			String sLogonID = sMailAddress.split("@")[0];// params.get("UserID").toString();
			sLogonID = java.net.URLEncoder.encode(sLogonID, java.nio.charset.StandardCharsets.UTF_8.toString());
			AES aes = new AES(aeskey, "N");
			sPassword = aes.encrypt(sPassword);

			apiURL = apiURL + String.format("&id=%s&domain=%s&pw=%s", sLogonID, sDomain, sPassword);
			CoviMap jObj = getJson(apiURL, method);

			if (jObj.get("returnCode").toString().equals("0")&&"SUCCESS".equals(jObj.get("returnMsg"))) {
				iReturn = 0;
				LOGGER.debug(jObj.get("returnMsg").toString());
			} else {
				iReturn = 3;
				LOGGER.error(jObj.toString()); // print error context
			}
		} catch (NullPointerException e) {
			LOGGER.error("IndiMail modifyPass Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
					+ "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail modifyPass Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		} catch (Exception e) {
			LOGGER.error("IndiMail modifyPass Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
					+ "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "IndiMail",
						"IndiMail modifyPass Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		}
		return iReturn;
	}

	@Override
	public String indiModifyPass(CoviMap params) throws Exception {
		String bReturn = "E0";
		boolean isSync = false;
		String apiURL = null;
		String sMode = "?job=modifyPass";
		String sDomain = null;
		// StringUtil func = new StringUtil();
		String key = PropertiesUtil.getDecryptedProperty(PropertiesUtil.getSecurityProperties().getProperty("aes.key"));

		try {
			isSync = RedisDataUtil.getBaseConfig("IsSyncIndi").equals("Y") ? true : false;
			if (isSync) {
				apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode;

				sDomain = params.get("Domain").toString();
				
				String sMailID = params.get("MailID").toString(); 
				String sLogonID = java.net.URLEncoder.encode(params.get("LogonID").toString(),"UTF-8");
				
				AES aes = new AES(key, "N");

				// String sLogonPW = URLEncoder.encode(new
				// String(func.hex2byte(aes.encrypt(params.get("LogonPW").toString()))));
				String sLogonPW = aes.encrypt(params.get("LogonPW").toString());
/*				String sLogonPW = URLEncoder.encode(new String(Base64.encodeBase64(aes.encrypt(params.get("LogonPW").toString()).getBytes())));
*/				
				//apiURL = apiURL + "&id="+sLogonID+"@"+sDomain+"&pw="+sLogonPW;
								
				if(!sMailID.equals(sLogonID)) { 
					apiURL = apiURL + String.format("&id=%s&domain=%s&pw=%s", sLogonID, sDomain, sLogonPW);
				} else { 
					apiURL = apiURL + String.format("&id=%s&domain=%s&pw=%s", sMailID, sDomain, sLogonPW);
				}
				
				CoviMap jObj = getJson(apiURL, "POST");
				
				if(jObj.get("returnCode").toString().equals("0")&&"SUCCESS".equals(jObj.get("returnMsg"))) {
					bReturn = "0";
				} 
				
			}else{
				bReturn = "0";
			}
		} catch (NullPointerException ex) {
			LOGGER.error(
					"indiModifyPass Error [" + params.get("LogonID") + "]" + "[Message : " + ex.getMessage() + "]");
		} catch (Exception ex) {
			LOGGER.error(
					"indiModifyPass Error [" + params.get("LogonID") + "]" + "[Message : " + ex.getMessage() + "]");
		}

		return bReturn;
	}

	/**
	 * IndiMail 그룹 추가
	 * 
	 * @param params
	 *            - CoviMap
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean addGroup(CoviMap params) throws Exception {
		boolean bReturn = false;
		String apiURL = null;
		String sMode = "?job=addGroup";
		String sDomain = null;
		String method = "POST";
		CoviMap map = new CoviMap();
		try {
			apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode;
			LOGGER.debug(apiURL);

			String sPrimaryMail = params.get("PrimaryMail").toString();
			sDomain = sPrimaryMail.split("@")[1];// RedisDataUtil.getBaseConfig("IndiMailDomain").toString();
			String sDeptCode = sPrimaryMail.split("@")[0];// params.get("GroupCode").toString();
			String sDisplayName = params.get("DisplayName").toString();
			String sMemberOf = params.get("MemberOf").toString();

			map.put("GroupCode", sMemberOf);
			String sParentMailAddress = selectGroupMail(map); 
			
			sDeptCode = java.net.URLEncoder.encode(sDeptCode, java.nio.charset.StandardCharsets.UTF_8.toString());
			sDisplayName = java.net.URLEncoder.encode(sDisplayName, java.nio.charset.StandardCharsets.UTF_8.toString());
			sParentMailAddress = java.net.URLEncoder.encode(sParentMailAddress, java.nio.charset.StandardCharsets.UTF_8.toString());
			
			apiURL = apiURL + String.format("&id=%s&name=%s&enc=%s&deptId=%s&status=%s&domain=%s", sDeptCode,
					sDisplayName, "0", sParentMailAddress, "A", sDomain);
			LOGGER.debug(apiURL);
			
			CoviMap jObj = getJson(apiURL, method);
			if (jObj.get("returnCode").toString().equals("0")&&"SUCCESS".equals(jObj.get("returnMsg"))) {
				bReturn = true;
			} else {
				LOGGER.error(jObj.toString()); // print error context
			}
		} catch (NullPointerException ex) {
			LOGGER.error("addGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "GR", "IndiMail",
						"IndiMail addGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage()
								+ "]",
						"");
			}
		} catch (Exception ex) {
			LOGGER.error("addGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "GR", "IndiMail",
						"IndiMail addGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage()
								+ "]",
						"");
			}
		}
		return bReturn;
	}

	/**
	 * IndiMail 그룹 수정
	 * 
	 * @param params
	 *            - CoviMap
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean modifyGroup(CoviMap params) throws Exception {
		boolean bReturn = false;
		String apiURL = null;
		String sMode = "?job=modifyGroup";
		String sDomain = null;
		String method = "POST";
		CoviMap map = new CoviMap();
		try {
			apiURL = RedisDataUtil.getBaseConfig("IndiMailAPIURL").toString() + sMode;
			LOGGER.debug(apiURL);

			String sPrimaryMail = params.get("PrimaryMail").toString();
			sDomain = sPrimaryMail.split("@")[1];// RedisDataUtil.getBaseConfig("IndiMailDomain").toString();
			String sDeptCode = sPrimaryMail.split("@")[0];// params.get("GroupCode").toString();
			String sDisplayName = params.get("DisplayName").toString();
			String sMemberOf = params.get("MemberOf").toString();
			String sGroupStatus = params.get("GroupStatus").toString();
			
			map.put("GroupCode", sMemberOf);
			String sParentMailAddress = selectGroupMail(map); 
			
			sDeptCode = java.net.URLEncoder.encode(sDeptCode, java.nio.charset.StandardCharsets.UTF_8.toString());
			sDisplayName = java.net.URLEncoder.encode(sDisplayName, java.nio.charset.StandardCharsets.UTF_8.toString());
			sParentMailAddress = java.net.URLEncoder.encode(sParentMailAddress, java.nio.charset.StandardCharsets.UTF_8.toString());

			apiURL = apiURL + String.format("&id=%s&name=%s&enc=%s&deptId=%s&status=%s&domain=%s", sDeptCode,
					sDisplayName, "0", sParentMailAddress, sGroupStatus, sDomain);

			CoviMap jObj = getJson(apiURL, method);
			if (jObj.get("returnCode").toString().equals("0")&&"SUCCESS".equals(jObj.get("returnMsg"))) {
				bReturn = true;
			} else {
				LOGGER.error(jObj.toString()); // print error context
			}
		} catch (NullPointerException ex) {
			LOGGER.error("modifyGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "GR", "IndiMail",
						"IndiMail modifyGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage()
								+ "]",
						"");
			}
		} catch (Exception ex) {
			LOGGER.error("modifyGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "GR", "IndiMail",
						"IndiMail modifyGroup Error [" + params.get("GroupCode") + "]" + "[Message : " + ex.getMessage()
								+ "]",
						"");
			}
		}
		return bReturn;
	}

	// 타임스퀘어 API
	/**
	 * 타임스퀘어 버젼
	 * 
	 * @return Stirng
	 * @throws Exception
	 */
	@Override
	public String getTSVer() throws Exception {
		String sReturn;
		sReturn = RedisDataUtil.getBaseConfig("TimeSquareAPIVer").toString();
		return sReturn;
	}

	/**
	 * 타임스퀘어 연동 여부
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean getTSSyncTF() throws Exception {
		boolean bReturn;
		bReturn = RedisDataUtil.getBaseConfig("IsSyncTimeSquare").equals("Y") ? true : false;
		return bReturn;
	}

	/**
	 * 타임스퀘어 사용자/부서 추가
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addUsersyncusermap(CoviMap params) throws Exception {
		int bReturn = 0;
		String apiURL = null;
		String sMode = "syncusermap";
		String method = "POST";

		try {
			String syncMode = params.get("SyncMode").toString(); // 조직도 동기화 부서/사용자 구분코드
			String sUc = null;
			String sGC = null;
			String sLv = null;
			String sDn = null;
			String sUt = null;
			String sOd = null;

			String job_title_code = null;
			String ex_job_title_name = null;
			String job_position_code = null;
			String ex_job_position_name = null;
			String job_level_code = null;
			String ex_job_level_name = null;
			String phone_number_inter = null;
			String phone_number = null;

			if (syncMode.equals("U")) {
				sUc = params.get("LogonID").toString(); // UserCode
				sGC = params.get("DeptCode").toString(); // GroupCode
				sLv = "0"; // 조직도상 노드의 레벨 0
				sDn = params.get("DisplayName").toString(); // Displayname
				sUt = "U"; // UserType U
				sOd = params.get("SortKey").toString(); // 부서내 정렬순서
				// sGroup_type = "company";
				// sCompany_code = params.get("CompanyCode").toString(); //계열사 경우 회사코드
				job_title_code = !params.get("JobTitleCode").toString().equals("") && params.get("JobTitleCode") != null
						? params.get("JobTitleCode").toString()
						: "";
				ex_job_title_name = !params.get("JobTitleName").toString().equals("")
						&& params.get("JobTitleName") != null ? params.get("JobTitleName").toString() : "";
				job_position_code = !params.get("JobPositionCode").toString().equals("")
						&& params.get("JobPositionCode") != null ? params.get("JobPositionCode").toString() : "";
				ex_job_position_name = !params.get("JobPositionName").toString().equals("")
						&& params.get("JobPositionName") != null ? params.get("JobPositionName").toString() : "";
				job_level_code = !params.get("JobLevelCode").toString().equals("") && params.get("JobLevelCode") != null
						? params.get("JobLevelCode").toString()
						: "";
				ex_job_level_name = !params.get("JobLevelName").toString().equals("")
						&& params.get("JobLevelName") != null ? params.get("JobLevelName").toString() : "";
				phone_number_inter = !params.get("PhoneNumberInter").toString().equals("")
						&& params.get("PhoneNumberInter") != null ? params.get("PhoneNumberInter").toString() : "";
				phone_number = !params.get("PhoneNumber").toString().equals("") && params.get("PhoneNumber") != null
						? params.get("PhoneNumber").toString()
						: "";
			} else if (syncMode.equals("G")) {
				sUc = params.get("GroupCode").toString();
				if (sUc.equals("ORGROOT"))
					sGC = "";
				else
					sGC = params.get("MemberOf").toString();
				sLv = "0";
				sDn = params.get("DisplayName").toString();
				sUt = "G";
				sOd = params.get("SortKey").toString();
			}

			if (sUc != null && sGC != null) {
				sUc = java.net.URLEncoder.encode(sUc, java.nio.charset.StandardCharsets.UTF_8.toString());
				sGC = java.net.URLEncoder.encode(sGC, java.nio.charset.StandardCharsets.UTF_8.toString());
				// sDn = java.net.URLEncoder.encode(sDn,
				// java.nio.charset.StandardCharsets.UTF_8.toString());
			}

			apiURL = RedisDataUtil.getBaseConfig("TimeSquareAPIURLv2").toString() + sMode;
			LOGGER.debug(apiURL);

			String apiData = "{\"uc\":\"" + sUc + "\",\"gc\":\"" + sGC + "\",\"lv\":\"" + sLv + "\",\"dn\":\"" + sDn
					+ "\",\"ut\":\"" + sUt + "\",\"od\":\"" + sOd
					// + "\",\"group_type\":\"" + sGroup_type + "\",\"company_code\":\"" +
					// sCompany_code
					+ "\",\"job_title_code\":\"" + job_title_code + "\",\"ex_job_title_name\":\"" + ex_job_title_name
					+ "\",\"job_position_code\":\"" + job_position_code + "\",\"ex_job_position_name\":\""
					+ ex_job_position_name + "\",\"job_level_code\":\"" + job_level_code + "\",\"ex_job_level_name\":\""
					+ ex_job_level_name + "\",\"phone_number_inter\":\"" + phone_number_inter + "\",\"phone_number\":\""
					+ phone_number + "\"}";
			byte[] postData = apiData.toString().getBytes("UTF-8");

			try {
				URL strurl;
				StringUtil func = new StringUtil();
				String responseMsg = "";
				String RequestDate = func.getCurrentTimeStr();
				int statusCode = 404;
				StringBuffer response = new StringBuffer();
				String jsonText = "";

				try {
					strurl = new URL(apiURL);
					conn = (HttpURLConnection) strurl.openConnection();
					conn.setReadTimeout(15000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod(method);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.setDoInput(true);
					conn.setDoOutput(true);

					if (true) {
						try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream());) {
							wr.write(postData);
							int responseCode = conn.getResponseCode();

							if (responseCode == 200) { // 정상 호출
								bReturn = 1;
								try (InputStream in = conn.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

									byte[] buf = new byte[1024 * 8];
									int length = 0;
									while ((length = in.read(buf)) != -1) {
										out.write(buf, 0, length);
									}

									jsonText = new String(out.toByteArray(), "UTF-8");
									CoviMap resultList = CoviMap.fromObject(jsonText);
									LOGGER.debug("[HttpURLConnect] [" + strurl + "] Return Msg : " + resultList);
								}
							} else {
								// 에러 발생
								// 오류 처리 추가 할 것
								bReturn = 0;
								throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
							}
						}
					}
					statusCode = conn.getResponseCode();
				}catch (NullPointerException e) {
					statusCode = 500;
					responseMsg = e.toString();
					bReturn = 0;
					LOGGER.debug("[HttpURLConnect] [" + apiURL + "] Connect Exception : " + e.toString());
				} catch (RuntimeException e) {
					throw new RuntimeException("Failed : HTTP error code (RuntimeException) : " + e.getMessage());
				} catch (Exception e) {
					statusCode = 500;
					responseMsg = e.toString();
					bReturn = 0;
					LOGGER.debug("[HttpURLConnect] [" + apiURL + "] Connect Exception : " + e.toString());
				} finally {
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
					if (apiURL.indexOf("sendmessaging") > 0 && statusCode < 299) {

					} else {
						params.put("LogType", "URL");
						params.put("Method", method);
						params.put("ConnetURL", apiURL);

						params.put("RequestDate", RequestDate);
						params.put("ResultState", Integer.toString(statusCode));
						params.put("RequestBody", "");

						if (func.f_NullCheck(responseMsg).equals("")) {
							if (statusCode < 299) {
								bReturn = 1;
								params.put("ResultType", "SUCCESS");
							} else {
								bReturn = 0;
								params.put("ResultType", "FAIL");
							}
							params.put("ResponseMsg", response.toString());
						} else {
							bReturn = 0;
							params.put("ResultType", "FAIL");
							params.put("ResponseMsg", responseMsg);
						}

						params.put("ResponseDate", func.getCurrentTimeStr());
						LoggerHelper.httpLog(params);
					}
					LoggerHelper.httpLog(params);
				}
			} catch (IOException e) {
				bReturn = 0;
				LOGGER.error("TimeSquare addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : "
						+ e.getMessage() + "]");
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "TS",
							"TS addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : "
									+ e.getMessage() + "]",
							"");
				}
			} catch (Exception e) {
				bReturn = 0;
				LOGGER.error("TimeSquare addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : "
						+ e.getMessage() + "]");
				if (params.get("SyncManage") == "Sync") {
					insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "TS",
							"TS addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : "
									+ e.getMessage() + "]",
							"");
				}
			}
		} catch (NullPointerException e) {
			bReturn = 0;
			LOGGER.error("TimeSquare addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "TS",
						"TS addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		} catch (Exception e) {
			bReturn = 0;
			LOGGER.error("TimeSquare addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "TS",
						"TS addUsersyncusermap Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		}
		return bReturn;
	}

	/**
	 * 타임스퀘어 사용자 계정 수정(비밀번호)
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public String resetuserpasswordTS(CoviMap params) throws Exception {
		String sReturn = "";
		return sReturn;
		//타임스퀘어 미사용
		/*int iReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "job", "modifyUser");
			wrTS = httpClient.streamAdd(wrTS, "loginId", params.get("LogonID").toString());

			if (RedisDataUtil.getBaseConfig("UseSaml").toString().equals("1")) {
				wrTS = httpClient.streamAdd(wrTS, "authType", "saml");
				wrTS = httpClient.streamAdd(wrTS, "password", "");
			} else {
				wrTS = httpClient.streamAdd(wrTS, "password", params.get("Returnpass").toString());
			}

			wrTS = httpClient.streamAdd(wrTS, "", "");
			iReturn = httpClient.httpClientTSConnect(conTS, wrTS);
			sReturn = Integer.toString(iReturn);

		} catch (Exception e) {
			sReturn = "0";
			LOGGER.error("TimeSquare resetuserpassword Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
			if (params.get("SyncManage") == "Sync") {
				insertLogListLog(Integer.parseInt(params.get("SyncMasterID").toString()), "Error", "UR", "TS",
						"TS resetuserpassword Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage()
								+ "]",
						"");
			}
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return sReturn;*/
	}

	/**
	 * 타임스퀘어 사용자 사진 변경
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Override
	public int setUserPhoto(CoviMap params) throws Exception {
		int bReturn = 0;
		String sMode = "syncimage";
		if (params.get("PhotoPath") != null && params.get("PhotoPath") != "") {
			final String path = RedisDataUtil.getBaseConfig("ProfileImageFolderPath");
			String companyCode = SessionHelper.getSession("DN_Code");
			String backStorage = RedisDataUtil.getBaseConfig("BackStorage").replace("{0}", companyCode);

			if(awsS3.getS3Active(companyCode)){
				String dir = FileUtil.getBackPath(companyCode).substring(0, FileUtil.getBackPath(companyCode).length() - 1) + backStorage + path;
				String key = dir+"/"+params.getString("UserCode");
				if(awsS3.exist(key)){
					awsS3.delete(key);
				}
				key = dir+"/"+params.getString("UserCode")+ "_org";
				if(awsS3.exist(key)){
					awsS3.delete(key);
				}
				if (params.get("FileInfo") != null && !params.get("FileInfo").equals("")) {
					MultipartFile mFile = (MultipartFile) params.get("FileInfo");
					String key2 = dir+"/"+params.getString("UserCode") + "_org.jpg";
					awsS3.upload(mFile.getInputStream(), key2, mFile.getContentType(), mFile.getSize());
				}
			}else {
				File dir = new File(FileUtil.getBackPath(companyCode).substring(0, FileUtil.getBackPath(companyCode).length() - 1) + backStorage + path);
				if (!dir.exists()) {
					boolean dReturn = dir.mkdirs();
					if (!dReturn)
						throw new IOException("updateOrgUserPhotoPath : Unable to create path.");
				}

				File existFile = new File(dir, params.getString("UserCode"));
				if (existFile.exists()) {
					boolean dReturn =false;
					try{
						dReturn = existFile.delete();	
					} catch (NullPointerException e) {
						throw new IOException("updateUserPhotoPath : Fail to delete file." + existFile.getName());
					} catch(Exception ex){
						throw new IOException("updateUserPhotoPath : Fail to delete file." + existFile.getName());
					}

					dReturn = new File(dir, params.getString("UserCode") + "_org").delete();

					if (!dReturn)
						throw new IOException("updateOrgUserPhotoPath : Fail to delete file.");
				}

				if (params.get("FileInfo") != null && !params.get("FileInfo").equals("")) {
					MultipartFile mFile = (MultipartFile) params.get("FileInfo");
					File file = new File(dir, params.getString("UserCode") + "_org.jpg");
					mFile.transferTo(file);
				}
			}

			String username = params.get("LogonID").toString();
			String filePath = FileUtil.getBackPath(companyCode).substring(0, FileUtil.getBackPath(companyCode).length() - 1) + backStorage + path
					+ params.getString("UserCode") + ".jpg";
			String requestURL = RedisDataUtil.getBaseConfig("TimeSquareAPIURLv2").toString() + sMode;
			String charset = "UTF-8";

			try {
				MultipartUtility multipart = new MultipartUtility(requestURL, charset);

				multipart.addFormField("username", username);
				if(awsS3.getS3Active(companyCode)){
					AwsS3Data awsS3Data = awsS3.downData(filePath);
					multipart.addFilePart("image", awsS3Data.getContent(), awsS3Data.getName());
				}else {
					multipart.addFilePart("image", new File(filePath));
				}
				List<String> response = multipart.finish();

				for (String line : response) {
					// System.out.println(line);
				}
				bReturn = 0;
			} catch (NullPointerException e) {
				bReturn = 2;
			} catch (Exception ex) {
				bReturn = 2;
			}
		}
		return bReturn;
	}

	/**
	 * 타임스퀘어 계정존재 여부 조회
	 * 
	 * @param params
	 *            - CoviMap
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean getUsersyncexistuser(CoviMap params) throws Exception {
		boolean bReturn = false;
		String apiURL = null;
		String sMode = "syncexistuser?d=";
		String method = "GET";

		try {
			String sUsername = params.get("LogonID").toString();

			sUsername = java.net.URLEncoder.encode(sUsername, java.nio.charset.StandardCharsets.UTF_8.toString());

			apiURL = RedisDataUtil.getBaseConfig("TimeSquareAPIURLv2").toString() + sMode;
			LOGGER.debug(apiURL);
			apiURL = apiURL + "{" + "\"username\":\"" + sUsername + "\"" + "}";
			CoviMap jObj = getJson(apiURL, method);

			if (jObj.get("returnCode").toString().equals("0")) {
				if (jObj.get("userstatus").toString().equals("false")) {
					bReturn = false;
				} else {
					bReturn = true;
				}

			} else {
				bReturn = false;
				LOGGER.error(jObj.toString()); // print error context
			}
		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare getUsersyncexistuser Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare getUsersyncexistuser Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		}
		return bReturn;
	}

	/**
	 * 타임스퀘어 계정활성화 여부 조회
	 * 
	 * @param params
	 *            - CoviMap
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean getUserStatusSyncstatususer(CoviMap params) throws Exception {
		boolean bReturn = false;
		String apiURL = null;
		String sMode = "syncstatususer?d=";
		String method = "GET";

		try {
			String sUsername = params.get("LogonID").toString();

			sUsername = java.net.URLEncoder.encode(sUsername, java.nio.charset.StandardCharsets.UTF_8.toString());

			apiURL = RedisDataUtil.getBaseConfig("TimeSquareAPIURLv2").toString() + sMode;
			LOGGER.debug(apiURL);
			apiURL = apiURL + "{" + "\"username\":\"" + sUsername + "\"" + "}";
			CoviMap jObj = getJson(apiURL, method);

			if (jObj.get("returnCode").toString().equals("0")) {
				bReturn = true;
			} else {
				bReturn = false;
				LOGGER.error(jObj.toString()); // print error context
			}
		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare getUserStatusSyncstatususer Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare getUserStatusSyncstatususer Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		}
		return bReturn;
	}

	/**
	 * 타임스퀘어 사용자 계정 업데이트(비밀번호 제외)
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateUserSyncupdate(CoviMap params) throws Exception {
		int bReturn = 0;
		return bReturn;
		//타임스퀘어 미사용
		/*HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "job", "modifyUser");
			wrTS = httpClient.streamAdd(wrTS, "loginId", params.get("LogonID").toString());
			wrTS = httpClient.streamAdd(wrTS, "email", params.get("MailAddress").toString());
			wrTS = httpClient.streamAdd(wrTS, "displayName", params.get("DisplayName").toString());
			wrTS = httpClient.streamAdd(wrTS, "order", params.get("SortKey").toString());
			wrTS = httpClient.streamAdd(wrTS, "phoneNumberInter", params.get("PhoneNumberInter").toString());
			wrTS = httpClient.streamAdd(wrTS, "phoneNumber", params.get("PhoneNumber").toString());
			wrTS = httpClient.streamAdd(wrTS, "memberOf", params.get("DeptCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobTitleCode", params.get("JobTitleCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobTitleName", params.get("JobTitleName").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobPositionCode", params.get("JobPositionCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobPositionName", params.get("JobPositionName").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobLevelCode", params.get("JobLevelCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobLevelName", params.get("JobLevelName").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());
			wrTS = httpClient.streamAdd(wrTS, "companyCode", params.get("CompanyCode").toString());

			if (params.get("oldCompanyCode") != null
					&& !params.getString("CompanyCode").equals(params.getString("oldCompanyCode"))) {
				wrTS = httpClient.streamAdd(wrTS, "oldTeamName", params.get("oldCompanyCode").toString());
				wrTS = httpClient.streamAdd(wrTS, "teamName", params.get("CompanyCode").toString());
			}

			if (params.get("oldDeptCode") != null
					&& !params.getString("DeptCode").equals(params.getString("oldDeptCode"))) {
				wrTS = httpClient.streamAdd(wrTS, "oldChannelName", params.get("oldDeptCode").toString());
				wrTS = httpClient.streamAdd(wrTS, "channelName", params.get("DeptCode").toString());
			}

			if (params.get("PhotoPath") != null && !params.get("PhotoPath").toString().equals("")) {
				try {
					wrTS = httpClient.streamFileAdd(wrTS, "profileImage", params);
				} catch (Exception e) {
					LOGGER.error("TimeSquare updateUserSyncupdate PhotoPath Error [" + params.get("UserCode") + "]"
							+ "[Message : " + e.getMessage() + "]");
				}
			}

			if (params.get("BySaml").toString().equals("1")) {
				wrTS = httpClient.streamAdd(wrTS, "authType", "saml");
				wrTS = httpClient.streamAdd(wrTS, "password", "");
			}

			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);

		} catch (Exception e) {
			LOGGER.error("TimeSquare updateUserSyncupdate Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return bReturn;*/
	}

	/**
	 * 타임스퀘어 사용자 계정 추가
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addUserSync(CoviMap params) throws Exception {
		int bReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "job", "addUser");
			wrTS = httpClient.streamAdd(wrTS, "loginId", params.get("LogonID").toString());
			wrTS = httpClient.streamAdd(wrTS, "email", params.get("MailAddress").toString());
			wrTS = httpClient.streamAdd(wrTS, "password", params.get("DecLogonPassword").toString());
			wrTS = httpClient.streamAdd(wrTS, "displayName", params.get("DisplayName").toString());
			wrTS = httpClient.streamAdd(wrTS, "order", params.get("SortKey").toString());
			wrTS = httpClient.streamAdd(wrTS, "phoneNumberInter", params.get("PhoneNumberInter").toString());
			wrTS = httpClient.streamAdd(wrTS, "phoneNumber", params.get("PhoneNumber").toString());
			wrTS = httpClient.streamAdd(wrTS, "memberOf", params.get("DeptCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobTitleCode", params.get("JobTitleCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobTitleName", params.get("JobTitleName").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobPositionCode", params.get("JobPositionCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobPositionName", params.get("JobPositionName").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobLevelCode", params.get("JobLevelCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobLevelName", params.get("JobLevelName").toString());
			wrTS = httpClient.streamAdd(wrTS, "companyCode", params.get("CompanyCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());
			// 기본 아님
			wrTS = httpClient.streamAdd(wrTS, "teamName", params.get("CompanyCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "channelName", params.get("DeptCode").toString());

			if (!params.getString("PhotoPath").equals("")) {
				try {
					wrTS = httpClient.streamFileAdd(wrTS, "profileImage", params);
				} catch (NullPointerException e) {
					LOGGER.error("TimeSquare addUser PhotoPath Error [" + params.get("UserCode") + "]" + "[Message : "
							+ e.getMessage() + "]");
				} catch (Exception e) {
					LOGGER.error("TimeSquare addUser PhotoPath Error [" + params.get("UserCode") + "]" + "[Message : "
							+ e.getMessage() + "]");
				}
			}

			if (params.get("BySaml").toString().equals("1")) {
				wrTS = httpClient.streamAdd(wrTS, "authType", "saml");
			}

			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);

		} catch (NullPointerException e) {
			LOGGER.error(
					"TimeSquare addUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error(
					"TimeSquare addUser Error [" + params.get("UserCode") + "]" + "[Message : " + e.getMessage() + "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return bReturn;
	}

	/**
	 * 타임스퀘어 사용자 계정 삭제(계정비활성화)
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int deleteUserSyncdelete(CoviMap params) throws Exception {
		return isUseUserSyncupdate(params, "N");
	}

	/**
	 * 타임스퀘어 사용자 계정 활성화
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int isUseUserSyncupdate(CoviMap params, String pIsUse) throws Exception {
		int bReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "job", "modifyUser");
			wrTS = httpClient.streamAdd(wrTS, "loginId", params.get("LogonID").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", pIsUse);
			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);
		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare isUseUserSyncupdate Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare isUseUserSyncupdate Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}
		return bReturn;
	}

	/**
	 * 타임스퀘어 부서/그룹 추가
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addGroupSync(CoviMap params) throws Exception {
		int bReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());
			// 공통 파라미터
			wrTS = httpClient.streamAdd(wrTS, "groupCode", params.get("GroupCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "displayName", params.get("DisplayName").toString());
			wrTS = httpClient.streamAdd(wrTS, "order", params.get("SortKey").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());

			switch (params.get("GroupType").toString().toUpperCase()) {
			case "DEPT":
				wrTS = httpClient.streamAdd(wrTS, "job", "addGroup");
				wrTS = httpClient.streamAdd(wrTS, "createType",
						RedisDataUtil.getBaseConfig("TSGroupSyncCreateType").toString());
				wrTS = httpClient.streamAdd(wrTS, "companyCode", params.get("CompanyCode").toString());
				wrTS = httpClient.streamAdd(wrTS, "memberOf", params.get("MemberOf").toString());
				wrTS = httpClient.streamAdd(wrTS, "level", "1");
				break;
			case "JOBTITLE":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "T");
				wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());
				break;
			case "JOBLEVEL":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "L");
				wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());
				break;
			case "JOBPOSITION":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "P");
				wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());
				break;
			default : 
				break;
			}

			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);

		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("GroupCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("GroupCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return bReturn;
	}

	/**
	 * 타임스퀘어 그룹 수정
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateGroupSync(CoviMap params) throws Exception {
		int bReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "groupCode", params.get("GroupCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "displayName", params.get("DisplayName").toString());
			wrTS = httpClient.streamAdd(wrTS, "order", params.get("SortKey").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());

			switch (params.get("GroupType").toString().toUpperCase()) {
			case "DEPT":
				wrTS = httpClient.streamAdd(wrTS, "job", "modifyGroup");
				wrTS = httpClient.streamAdd(wrTS, "createType", "channel");
				wrTS = httpClient.streamAdd(wrTS, "companyCode", params.get("CompanyCode").toString());
				wrTS = httpClient.streamAdd(wrTS, "memberOf", params.get("MemberOf").toString());
				wrTS = httpClient.streamAdd(wrTS, "level", "1");
				break;
			case "JOBTITLE":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "T");
				break;
			case "JOBLEVEL":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "L");
				break;
			case "JOBPOSITION":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "P");
				break;
			default :
				break;
			}

			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);

		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("GroupCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("GroupCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return bReturn;
	}

	/**
	 * 타임스퀘어 그룹 삭제
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int deleteGroupSync(CoviMap params) throws Exception {
		return isUseGroupSyncUpdate(params, "N");
	}

	/**
	 * 타임스퀘어 그룹 활성/비활성화
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int isUseGroupSyncUpdate(CoviMap params, String pIsUse) throws Exception {
		int bReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "groupCode", params.get("GroupCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", pIsUse);

			switch (params.get("GroupType").toString().toUpperCase()) {
			case "DEPT":
				wrTS = httpClient.streamAdd(wrTS, "job", "modifyGroup");
				break;
			case "JOBTITLE":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "T");
				break;
			case "JOBLEVEL":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "L");
				break;
			case "JOBPOSITION":
				wrTS = httpClient.streamAdd(wrTS, "job", "syncGroupType");
				wrTS = httpClient.streamAdd(wrTS, "groupType", "P");
				break;
			default :
				break;
			}

			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);

		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("GroupCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("GroupCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return bReturn;
	}

	/**
	 * 타임스퀘어 사용자 직급,직위,직책 동기화
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int setUserTypeSyncentcoder(CoviMap params) throws Exception {
		int bReturn = 0;
		String apiURL = null;
		String sMode = "syncentcode?d=";
		String method = "GET";

		try {
			String sId = params.get("GroupCode").toString();
			String sType = params.get("GroupType").toString().toUpperCase();
			String sDisplayname = params.get("GroupName").toString();
			String sSortkey = params.get("SortKey").toString();
			String sIsuse = params.get("IsUse").toString();

			if (sType.equals("JOBPOSITION"))
				sType = "P";
			else if (sType.equals("JOBTITLE"))
				sType = "T";
			else if (sType.equals("JOBLEVEL"))
				sType = "L";

			sId = java.net.URLEncoder.encode(sId, java.nio.charset.StandardCharsets.UTF_8.toString());
			sType = java.net.URLEncoder.encode(sType, java.nio.charset.StandardCharsets.UTF_8.toString());
			sDisplayname = java.net.URLEncoder.encode(sDisplayname, java.nio.charset.StandardCharsets.UTF_8.toString());
			sIsuse = java.net.URLEncoder.encode(sIsuse, java.nio.charset.StandardCharsets.UTF_8.toString());

			apiURL = RedisDataUtil.getBaseConfig("TimeSquareAPIURLv2").toString() + sMode;
			LOGGER.debug(apiURL);

			apiURL = apiURL + "{\"id\":\"" + sId + "\",\"type\":\"" + sType + "\",\"display_name\":\"" + sDisplayname
					+ "\",\"sort_key\":\"" + sSortkey + "\",\"is_use\":\"" + sIsuse + "\"}";
			CoviMap jObj = getJson(apiURL, method);

			if (!jObj.get("returnCode").toString().equals("0")) { // 추가오류
				bReturn = 0;
				LOGGER.error(jObj.toString()); // print error context
			} else {
				bReturn = 1;
			}
		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare setUserTypeSyncentcoder Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare setUserTypeSyncentcoder Error [" + params.get("UserCode") + "]" + "[Message : "
					+ e.getMessage() + "]");
		}
		return bReturn;
	}

	/**
	 * 타임스퀘어 겸직 연동
	 * 
	 * @param params
	 *            - CoviMap
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int insertUpdateAddJobSync(CoviMap params) throws Exception {
		int bReturn = 0;
		HttpClientUtil httpClient = new HttpClientUtil();
		HttpURLConnection conTS = null;
		DataOutputStream wrTS = null;
		try {
			conTS = httpClient.getTSConnection();
			wrTS = new DataOutputStream(conTS.getOutputStream());

			wrTS = httpClient.streamAdd(wrTS, "job", "syncAddJob");
			wrTS = httpClient.streamAdd(wrTS, "loginId", params.get("LogonID").toString());
			wrTS = httpClient.streamAdd(wrTS, "displayName", params.get("DisplayName").toString());
			if (params.get("IsUse").toString().equals("N")) // Y->N 으로 변경하는 경우 등록된 겸직부서 넘겨준다
				wrTS = httpClient.streamAdd(wrTS, "oldMemberOf", params.get("GroupCode").toString());
			else
				wrTS = httpClient.streamAdd(wrTS, "oldMemberOf", ""); // 추가하는 경우 안넘겨준다
			wrTS = httpClient.streamAdd(wrTS, "memberOf", params.get("GroupCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "isUse", params.get("IsUse").toString());
			wrTS = httpClient.streamAdd(wrTS, "order", params.get("SortKey").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobTitleCode", params.get("JobTitleCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobTitleName", params.get("JobTitleName").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobPositionCode", params.get("JobPositionCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobPositionName", params.get("JobPositionName").toString());
			wrTS = httpClient.streamAdd(wrTS, "jobLevelCode", params.get("JobLevelCode").toString());
			wrTS = httpClient.streamAdd(wrTS, "exJobLevelName", params.get("JobLevelName").toString());
			wrTS = httpClient.streamAdd(wrTS, "phoneNumberInter", params.get("PhoneNumberInter").toString());
			wrTS = httpClient.streamAdd(wrTS, "phoneNumber", params.get("PhoneNumber").toString());

			wrTS = httpClient.streamAdd(wrTS, "", "");

			bReturn = httpClient.httpClientTSConnect(conTS, wrTS);

		} catch (NullPointerException e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("LogonID") + "]" + "[Message : " + e.getMessage()
					+ "]");
		} catch (Exception e) {
			LOGGER.error("TimeSquare addDeptSync Error [" + params.get("LogonID") + "]" + "[Message : " + e.getMessage()
					+ "]");
		} finally {
			if (conTS != null) {
				conTS.disconnect();
				conTS = null;
			}
			if (wrTS != null) {
				wrTS.close();
			}
		}

		return bReturn;
	}

	/**
	 * @description 타임스퀘어 연동
	 * @param encodedUrl String
	 * @param method String
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap getJson(String encodedUrl, String method) throws Exception {
		CoviMap resultList = new CoviMap();
		HttpURLConnectUtil url = new HttpURLConnectUtil();
		String sMethod = method;

		try {
			if (sMethod.equals("POST")) {
				resultList = url.httpConnect(encodedUrl, sMethod, 10000, 10000, "xform", "Y");
			} else if (sMethod.equals("GET")) {
				resultList = url.httpConnect(encodedUrl, sMethod, 10000, 10000, "xform", "Y");
			}
			LOGGER.debug("URL : " + encodedUrl);
		} catch (NullPointerException e) {
			throw e;
		}  catch (Exception ex) {
			throw ex;
		} finally {
			url.httpDisConnect();
		}

		return resultList;
	}

	// 유틸리티
	/**
	 * @description 그룹의 Path 관련 정보 추출
	 * @param CompanyCode String
	 * @param MemberOf String
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String getGroupPath(String CompanyCode, String MemberOf) {
		String GroupPath = "";
		String SortPath = "";
		String OUPath = "";
		try {
			CoviMap params = new CoviMap();
			params.put("CompanyCode", CompanyCode);
			params.put("MemberOf", MemberOf);
			CoviMap returnList = selectGroupPath(params);

			GroupPath = returnList.getJSONArray("list").getJSONObject(0).getString("GroupPath");
			SortPath = returnList.getJSONArray("list").getJSONObject(0).getString("SortPath");
			OUPath = returnList.getJSONArray("list").getJSONObject(0).getString("OUPath");

		} catch (NullPointerException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}  catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		LOGGER.info(GroupPath + "&" + SortPath + "&" + OUPath);
		return GroupPath + "&" + SortPath + "&" + OUPath;
	}

	/**
	 * @description 그룹의 Path 관련 정보 조회
	 * @param params
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectGroupPath(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectGroupPath", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "GroupPath,SortPath,OUPath"));
		return resultList;
	}

	/**
	 * 사용자 정보 Group Code select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return string
	 * @throws Exception
	 */
	@Override
	public CoviMap selectUserSyncData(CoviMap params) throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectUserInfo", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list",
				CoviSelectSet.coviSelectJSON(list, "DeptCode,JobPositionName,JobTitleName,JobLevelName"));
		return resultList;
	}

	/**
	 * 서비스 유형 select
	 *
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectServiceTypeList() throws Exception {

		CoviList list = coviMapperOne.list("organization.syncmanage.selectServiceTypeList", null);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "optionValue,optionText"));
		return resultList;
	}

	/**
	 * 메일 주소 중복 여부 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap selectIsDuplicateMail(CoviMap params) throws Exception {
		CoviList list = coviMapperOne.list("organization.syncmanage.selectIsDuplicateMail", params);

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list, "isDuplicate"));
		return resultList;
	}

	/**
	 * 엑셀 데이터 조회
	 * 
	 * @param params CoviMap
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public CoviMap selectOrgExcelList(CoviMap params) throws Exception {
		CoviMap resultList = new CoviMap();
		String sField = ""; // 항목 설정
		CoviList list = null;
		int cnt = 0;
		
		if (isSaaS) params.put("isSaaS", "Y");

		if (params.get("type").toString().equalsIgnoreCase("dept")) {
			sField = "GroupCode,SortKey,DisplayName,ShortName,IsUse,IsHR,IsDisplay,IsMail,PrimaryMail,ManagerCode";
			list = coviMapperOne.list("organization.syncmanage.selectSubDeptList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectSubDeptListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("user")) {
			sField = "UserCode,SortKey,DisplayName,JobTitleName,JobPositionName,JobLevelName,IsUse,IsHR,MailAddress";
			list = coviMapperOne.list("organization.syncmanage.selectUserList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectUserListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("group")) { // group
			sField = "GroupCode,SortKey,DisplayName,ShortName,IsUse,IsMail,PrimaryMail";
			list = coviMapperOne.list("organization.syncmanage.selectSubGroupList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectSubGroupListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("title")
				|| params.get("type").toString().equalsIgnoreCase("position")
				|| params.get("type").toString().equalsIgnoreCase("level")) {
			sField = "GroupCode,GroupName,SortKey,IsUse,IsHR,PrimaryMail,Description";
			list = coviMapperOne.list("organization.syncmanage.selectArbitraryGroupList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectArbitraryGroupListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("region")) {
			sField = "DomainName,GroupCode,GroupName,SortKey,IsUse,IsHR,PrimaryMail,Description";
			list = coviMapperOne.list("organization.syncmanage.selectRegionList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectRegionListCnt", params);
		}
		else if(params.get("type").toString().equalsIgnoreCase("addjob")){
			sField = "UserName,CompanyName,DeptName,JobTitleName,JobPositionName,JobLevelName,IsHR";
			list = coviMapperOne.list("organization.syncmanage.selectAddJobList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectAddJobListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("groupmember")) {
			sField = "Type,Code,CodeName,MailAddress";
			list = coviMapperOne.list("organization.syncmanage.selectGroupMemberList", params);
			cnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectGroupMemberListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("exdept")) {
			sField = "GroupCode,CompanyCode,MemberOf,DisplayName,MultiDisplayName,SortKey,IsUse,IsHR,IsDisplay,IsMail,PrimaryMail,ManagerCode";
				
			list = coviMapperOne.list("organization.conf.syncexcel.selectallDeptList", params);
			cnt = (int) coviMapperOne.getNumber("organization.conf.syncexcel.selectallDeptListCnt", params);
		}
		else if (params.get("type").toString().equalsIgnoreCase("exuser")) {
			sField = "UserCode,CompanyCode,DeptCode,LogonID,LogonPW,EmpNo,DisplayName,MultiDisplayName,JobPositionCode,JobTitleCode,JobLevelCode,SortKey,IsUse,IsHR,IsDisplay,UseMailConnect,EnterDate,RetireDate,BirthDiv,BirthDate,PhotoPath,MailAddress,ExternalMailAddress,ChargeBusiness,PhoneNumberInter,PhoneNumber,Mobile,Fax,ManagerCode,UseMessengerConnect";

			list = coviMapperOne.list("organization.conf.syncexcel.selectallUserList", params);
			cnt = (int) coviMapperOne.getNumber("organization.conf.syncexcel.selectallUserListCnt", params);
		}

		resultList.put("list", coviSelectJSONForExcel(list, sField));
		resultList.put("cnt", cnt);
		return resultList;
	}

	/**
	 * 엑셀 정보 리턴
	 * 
	 * @param clist
	 * @param str
	 * @return JSONArray
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static CoviList coviSelectJSONForExcel(CoviList clist, String str) throws Exception {
		String[] cols = str.split(",");

		CoviList returnArray = new CoviList();

		if (null != clist && clist.size() > 0) {
			for (int i = 0; i < clist.size(); i++) {

				CoviMap newObject = new CoviMap();

				for (int j = 0; j < cols.length; j++) {
					Set<String> set = clist.getMap(i).keySet();
					Iterator<String> iter = set.iterator();

					while (iter.hasNext()) {
						Object ar = iter.next();
						if (ar.equals(cols[j].trim())) {
							newObject.put(cols[j], clist.getMap(i).getString(cols[j]));
						}
					}
				}
				returnArray.add(newObject);
			}
		}
		return returnArray;
	}

	/**
	 * 하위 그룹 존재 여부 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSONObject
	 * @throws Exception
	 */
	@Override
	public int selectHasChildGroup(CoviMap params) throws Exception {
		int hasChildCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectHasChildGroup", params);

		return hasChildCnt;
	}

	/**
	 * 하위 사용자 존재 여부 select
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSONObject
	 * @throws Exception
	 */
	@Override
	public int selectHasUserGroup(CoviMap params) throws Exception {
		int hasChildCnt = (int) coviMapperOne.getNumber("organization.syncmanage.selectHasUserGroup", params);

		return hasChildCnt;
	}

	/**
	 * 부서/사용자 목록 우선순위 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Override
	public CoviMap updateDeptUserListSortKey(CoviMap params) throws Exception {

		CoviMap resultObj = new CoviMap();
		int returnCnt = 0, returnCntChild = 0;
		String strSortKey_A = null;
		String strSortKey_B = null;
		String strSortPath_A = "";
		String strSortPath_B = "";
		String strTemp = null;
		String strType = null;
		try {
			strType = params.get("Type").toString();
			if (strType.equals("dept")) {
				// get SortKey
				params.put("TargetCode", params.get("Code_A"));
				strSortKey_A = coviMapperOne.select("organization.syncmanage.selectDeptSortKey", params).get("SortKey")
						.toString();
				strSortPath_A = coviMapperOne.select("organization.syncmanage.selectDeptSortKey", params)
						.get("SortPath").toString();
				params.put("TargetCode", params.get("Code_B"));
				strSortKey_B = coviMapperOne.select("organization.syncmanage.selectDeptSortKey", params).get("SortKey")
						.toString();
				strSortPath_B = coviMapperOne.select("organization.syncmanage.selectDeptSortKey", params)
						.get("SortPath").toString();

				// update SortKey
				params.put("TargetCode", params.get("Code_A"));
				params.put("TargetSortKey", strSortKey_B);
				params.put("TargetSortPath", strSortPath_B);
				params.put("CurrentSortPath", strSortPath_A);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateDeptSortKey", params);
				returnCntChild = coviMapperOne.update("organization.syncmanage.updateDeptChildSortKey", params);
				params.put("TargetCode", params.get("Code_B"));
				params.put("TargetSortKey", strSortKey_A);
				params.put("TargetSortPath", strSortPath_A);
				params.put("CurrentSortPath", strSortPath_B);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateDeptSortKey", params);
				returnCntChild = coviMapperOne.update("organization.syncmanage.updateDeptChildSortKey", params);
			} else if (strType.equals("user")) {
				// get SortKey
				params.put("TargetCode", params.get("Code_A"));
				strSortKey_A = coviMapperOne.select("organization.syncmanage.selectUserSortKey", params).get("SortKey")
						.toString();
				params.put("TargetCode", params.get("Code_B"));
				strSortKey_B = coviMapperOne.select("organization.syncmanage.selectUserSortKey", params).get("SortKey")
						.toString();

				// update SortKey
				params.put("TargetCode", params.get("Code_A"));
				params.put("TargetSortKey", strSortKey_B);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateUserSortKey", params);
				params.put("TargetCode", params.get("Code_B"));
				params.put("TargetSortKey", strSortKey_A);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateUserSortKey", params);
			}

		} catch (NullPointerException e) {
			LOGGER.error("updateDeptUserListSortKey Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("updateDeptUserListSortKey Error  " + "[Message : " + e.getMessage() + "]");
		}

		if (returnCnt > 0)
			resultObj.put("result", "OK");
		else
			resultObj.put("result", "FAIL");

		return resultObj;
	}

	/**
	 * 그룹/사용자 목록 우선순위 update
	 * 
	 * @param params
	 *            - CoviMap
	 * @return resultList - JSON
	 * @throws Exception
	 */
	@Override
	public CoviMap updateGroupUserListSortKey(CoviMap params) throws Exception {

		CoviMap resultObj = new CoviMap();
		int returnCnt = 0;
		String strSortKey_A = null;
		String strSortKey_B = null;
		String strSortPath_A = "";
		String strSortPath_B = "";
		String strType = null;
		try {
			strType = params.get("Type").toString();
			if (strType.equals("group")) {
				// get SortKey
				params.put("TargetCode", params.get("Code_A"));
				strSortKey_A = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
						.get("SortKey").toString();
				strSortPath_A = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
						.get("SortPath").toString();
				params.put("TargetCode", params.get("Code_B"));
				strSortKey_B = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
						.get("SortKey").toString();
				strSortPath_B = coviMapperOne.select("organization.syncmanage.selectArbitraryGroupSortKey", params)
						.get("SortPath").toString();

				// update SortKey
				params.put("TargetCode", params.get("Code_A"));
				params.put("TargetSortKey", strSortKey_B);
				params.put("TargetSortPath", strSortPath_B);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateArbitraryGroupSortKey", params);
				params.put("TargetCode", params.get("Code_B"));
				params.put("TargetSortKey", strSortKey_A);
				params.put("TargetSortPath", strSortPath_A);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateArbitraryGroupSortKey", params);
			} else if (strType.equals("user")) {
				// get SortKey
				params.put("TargetCode", params.get("Code_A"));
				strSortKey_A = coviMapperOne.select("organization.syncmanage.selectUserSortKey", params).get("SortKey")
						.toString();
				params.put("TargetCode", params.get("Code_B"));
				strSortKey_B = coviMapperOne.select("organization.syncmanage.selectUserSortKey", params).get("SortKey")
						.toString();

				// update SortKey
				params.put("TargetCode", params.get("Code_A"));
				params.put("TargetSortKey", strSortKey_B);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateUserSortKey", params);
				params.put("TargetCode", params.get("Code_B"));
				params.put("TargetSortKey", strSortKey_A);
				returnCnt += coviMapperOne.update("organization.syncmanage.updateUserSortKey", params);
			}

		} catch (NullPointerException e) {
			LOGGER.error("updateGroupUserListSortKey Error  " + "[Message : " + e.getMessage() + "]");
		} catch (Exception e) {
			LOGGER.error("updateGroupUserListSortKey Error  " + "[Message : " + e.getMessage() + "]");
		}

		if (returnCnt > 0)
			resultObj.put("result", "OK");
		else
			resultObj.put("result", "FAIL");

		return resultObj;
	}

	/**
	 * 부서 Path 정보 업데이트
	 * 
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void updateDeptPathInfoAll() {
		String groupcode = "";

		LOGGER.info("updateDeptPathInfo execute");

		try {
			// list 가져오기 => Dept 전체
			CoviList list = coviMapperOne.list("organization.syncmanage.selectSyncDeptInfo", null);
			CoviList groupList = CoviSelectSet.coviSelectJSON(list, "GroupCode,CompanyCode,MemberOf,SortKey,OUName");

			LOGGER.info("updateDeptPathInfo > selectDeptInfo completed");

			// Sync execution
			for (Object group : groupList) {
				CoviMap groupParams = new CoviMap();
				CoviMap groupObj = (CoviMap) group;
				groupcode = groupObj.get("GroupCode").toString();

				LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "] "
						+ groupObj.get("CompanyCode").toString() + " " + groupObj.get("MemberOf").toString());

				// GroupPath, SortPath, OUPath Create
				String[] resultString = getGroupPath(groupObj.get("CompanyCode").toString(),
						groupObj.get("MemberOf").toString()).split("&");
				String strGroupPath = "";
				String strSortPath = "";
				String strOUPath = "";

				LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "] getGroupPath completed");

				if (resultString.length > 0 && (resultString[1].isEmpty() != true)
						&& (resultString[2].isEmpty() != true)) {
					// LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "]
					// getGroupPath setting started ");
					strGroupPath = resultString[0] + groupObj.get("GroupCode") + ";";
					LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "] getGroupPath setting > "
							+ strGroupPath);
					strSortPath = resultString[1]
							+ String.format("%015d", Integer.parseInt((String) groupObj.get("SortKey"))) + ";";
					LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "] getGroupPath setting > "
							+ strSortPath);
					strOUPath = resultString[2] + groupObj.get("OUName") + ";";
				}

				LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "] getGroupPath setting finished ");

				// grouppath, sortpath, oupath update
				groupParams.put("GroupCode", groupObj.get("GroupCode"));
				groupParams.put("GroupPath", strGroupPath);
				groupParams.put("OUPath", strOUPath);
				groupParams.put("SortPath", strSortPath);

				LOGGER.info("updateDeptPathInfo > [" + groupObj.get("GroupCode") + "] updateDeptPathInfo excute ");
				coviMapperOne.update("organization.syncmanage.updateDeptPathInfo", groupParams);
			}

		} catch (NullPointerException ex) {
			LOGGER.error("updateDeptPathInfo Error [GroupCode : " + groupcode + "] " + "[Message : " + ex.getMessage()
					+ "]", ex);
		} catch (Exception ex) {
			LOGGER.error("updateDeptPathInfo Error [GroupCode : " + groupcode + "] " + "[Message : " + ex.getMessage()
					+ "]", ex);
		} finally {
			LOGGER.info("updateDeptPathInfo execute complete");
		}

	}

	/**
	 * 비밀번호 정책 확인
	 * 
	 * @param params
	 *            - CoviMap
	 * @return json
	 * @throws Exception
	 */
	@Override
	public CoviMap usePWPolicyCheck(CoviMap params) throws Exception {
		CoviList list = null;

		int cnt = (int) coviMapperOne.getNumber("organization.syncmanage.updatePasswordPolicyCount", params);

		if (cnt > 0) {
			list = coviMapperOne.list("organization.syncmanage.getPolicy", params);
		} else {
			params.clear();
			params.put("DomainCode", "GENERAL");

			list = coviMapperOne.list("organization.syncmanage.getPolicy", params);
		}

		CoviMap resultList = new CoviMap();
		resultList.put("list", CoviSelectSet.coviSelectJSON(list,
				"DomainID,IsUseComplexity,MaxChangeDate,MinimumLength,ChangeNoticeDate"));

		return resultList;
	}

	/**
	 * 동기화 로그 기록
	 * 
	 * @param params
	 *            - CoviMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void insertGroupLogList(CoviMap params) throws Exception {
		if (params.get("SyncType") != null)
			coviMapperOne.insert("organization.syncmanage.insertGroupLogList", params); // insert to log_object_group
	}

}
