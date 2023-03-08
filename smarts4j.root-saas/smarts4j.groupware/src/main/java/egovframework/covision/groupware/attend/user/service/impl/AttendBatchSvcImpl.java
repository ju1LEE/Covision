package egovframework.covision.groupware.attend.user.service.impl;

import javax.annotation.Resource;

import egovframework.baseframework.util.PropertiesUtil;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.service.MessageService;
import egovframework.coviframework.util.MessageHelper;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.baseframework.data.CoviSelectSet;
import egovframework.covision.groupware.attend.user.service.AttendBatchSvc;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

import egovframework.baseframework.util.RedisDataUtil;

@Service("AttendBatchSvc")
public class AttendBatchSvcImpl extends EgovAbstractServiceImpl implements AttendBatchSvc {

	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;

	@Autowired
	private MessageService messageService;

	@Override
	public CoviList getDomainIdList(CoviMap params) throws Exception {
		CoviList domainList = coviMapperOne.list("attend.adminBatch.getDomainId",params);
		return domainList;
	}

	@Override
	public CoviMap setAbsentData(CoviMap params) throws Exception {
		CoviMap resultObj = new CoviMap();

		//int abentCnt = (int) coviMapperOne.getNumber("attend.adminBatch.setAbsentData",params);
		
		coviMapperOne.update("attend.adminBatch.setAbsentData", params);	
		//coviMapperOne.selectOne("attend.adminBatch.setAbsentData",params);
		//resultObj.put("cnt", abentCnt);
		resultObj.put("cnt", params.getInt("RetCount"));
		resultObj.put("domain", params.get("CompanyCode"));
		return resultObj;
	}

	@Override
	public CoviMap getCommuteDataByOtherSystem(CoviMap params)		throws Exception {
		CoviMap returnObj = new CoviMap();
		CoviList othList = new CoviList();
		if (StringUtil.replaceNull(RedisDataUtil.getBaseConfig("attendBatchMethod"),"").equals("PROC")){
			othList = coviMapperOne.list("attend.adminBatch.getCommuteDataByOtherSystem", params); // 프로시저 사용 부분 수정
		}
		else{
			String batchDate = coviMapperOne.getString("attend.adminBatch.getCommuteByOtherSystemBatchDate", params);
			params.put("batchDate", batchDate);
	
			String systemType = coviMapperOne.getString("attend.adminBatch.getCommuteByOtherSystemType", params);
			if("SECOM".equals(systemType)) {
				othList = coviMapperOne.list("attend.adminBatch.getCommuteByOtherSystmeSecom", params);
			} else if ("ADT".equals(systemType)) {
				othList = coviMapperOne.list("attend.adminBatch.getCommuteByOtherSystmeAdt", params);
			}
		}	
		returnObj.put("list", CoviSelectSet.coviSelectJSON(othList, "workdate,usercode,wstime,wctime,startbatch,endbatch,commuSeq"));
		return returnObj;
	}
	
	@Override
	public CoviMap autoCreateScheduleJob(CoviMap params) throws Exception {
		CoviMap resultObj = new CoviMap();

		coviMapperOne.update("attend.adminBatch.autoCreateScheduleJob", params);
		//CoviMap retMap = coviMapperOne.selectOne("attend.adminBatch.autoCreateScheduleJob",params);
		//System.out.println(retMap);
//		resultObj.put("cnt", retMap.getInt("RetCount"));
		resultObj.put("domain", params.get("CompanyCode"));
		return resultObj;
	}

	@Override
	public CoviList excuteAttendNotifyPush(CoviMap params) throws Exception {
		CoviList AttAlarmTime = coviMapperOne.list("attend.adminBatch.getAttAlarmTimeCheck",params);
		return AttAlarmTime;
	}

	@Override
	public int attendAlarmSendMessages(CoviMap params) throws Exception {
		int cnt = 0;
		CoviMap notificationMap = new CoviMap();
		notificationMap.put("ServiceType", "Attend");
		notificationMap.put("MsgType", params.getString("MsgType"));
		notificationMap.put("MediaType", params.getString("MediaType"));
		//notificationMap.put("SenderCode", "superadmin");
		notificationMap.put("MessagingSubject", params.getString("MessagingSubject"));
		notificationMap.put("MessageContext", params.getString("MessageContext"));
		//notificationMap.put("ReceiverText", params.getString("ReceiverText"));
		//notificationMap.put("ReceiversCode", userSetting.get("MailAddress"));
		notificationMap.put("ReceiversCode", params.getString("ReceiversCode"));
		notificationMap.put("ApprovalState", "P");
		notificationMap.put("IsUse", "Y");
		notificationMap.put("IsDelay", "N");
		MessageHelper.getInstance().createNotificationParam(notificationMap);
		messageService.insertMessagingData(notificationMap);
		return cnt;
	}
}
