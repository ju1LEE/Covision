package egovframework.covision.coviflow.legacy.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import egovframework.baseframework.base.Enums.Return;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviMapperOne;
import egovframework.covision.coviflow.legacy.service.LegacyCommonSvc;
import egovframework.covision.coviflow.legacy.service.LegacyInterfaceSvc;

public abstract class LegacyInterfaceCommon implements LegacyInterfaceSvc {
	private static final Logger LOGGER = LogManager.getLogger(LegacyInterfaceCommon.class);

	@Autowired
	private LegacyCommonSvc legacyCmmnSvc;
	
	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	protected CoviMap logParam = new CoviMap();
	protected CoviMap legacyInfo = new CoviMap();
	/**
	 * paramDataType(데이터타입) - A(전체데이터 spParams) , L(일부 추출된데이터 legacyParams)
	 */
	protected CoviMap legacyParams = new CoviMap();
	
	abstract void call() throws Exception;
	
	@Override
	public void call(CoviMap legacyInfo, CoviMap spParams, String callType) throws Exception {
		long startTime = System.currentTimeMillis();
		this.logParam.clear();
		this.legacyInfo = legacyInfo;
		this.legacyParams = spParams;
		
		try {
			// 기본 로그셋팅(EventStartTime,LegacyConfigID,FormPrefix,FormInstID,ProcessID,Subject,UserCode,ApvMode,IfType,LegacyInfo,Parameters)
			CoviMap defaultLogParam = legacyCmmnSvc.makeLogParamDefault(legacyInfo, spParams);
			this.logParam.putAll(defaultLogParam);
			
			// 데이터타입 - A(전체데이터 spParams) , L(일부 추출된데이터 legacyParams)
			// 전체데이터인 경우에만 파라미터 다시 셋팅 - 결재연동인경우(재처리가아닌경우), 파라미터 셋팅 전 오류난 건으로 재처리하는 경우, JAVA타입인 경우
			if(this.legacyParams.optString("paramDataType").equals("A")) { 
				// 연동 Event별 설정에 따른 파라미터 setting
				this.legacyParams = legacyCmmnSvc.makeLegacyParams(legacyInfo, spParams);
				// 기본 로그에서 셋팅 후 변경된 파라미터로 다시셋팅
				this.logParam.put("Parameters", new CoviMap(this.legacyParams)); 
			}
			
			// 연동 실행
			this.call();
			
			this.logParam.put("State", Return.SUCCESS);
		} finally {
			long elapsedTime = System.currentTimeMillis() - startTime;
			this.logParam.put("EventEndTime", new Date());
			this.logParam.put("ElapsedTime", elapsedTime);
		}
	}
	
	@Override
	public CoviMap getLogParam() {
		return logParam;
	}
}
