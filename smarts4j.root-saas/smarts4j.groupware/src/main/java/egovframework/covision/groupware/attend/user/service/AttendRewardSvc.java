package egovframework.covision.groupware.attend.user.service;

import java.util.List;



import egovframework.baseframework.data.CoviMap;

public interface AttendRewardSvc {
	
	public CoviMap getAttendRewardList(CoviMap params) throws Exception;
	public CoviMap getAttendRewardDetail(CoviMap params) throws Exception;
	
	public int createAttendReward(CoviMap reqMap,List params) throws Exception;
	public int cancelAttendReward(CoviMap reqMap,List params) throws Exception;


}