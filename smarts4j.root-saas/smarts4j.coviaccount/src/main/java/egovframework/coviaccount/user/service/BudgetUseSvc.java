package egovframework.coviaccount.user.service;

import egovframework.baseframework.data.CoviMap;

public interface BudgetUseSvc {
	
	public CoviMap getBudgetExecuteList(CoviMap params) throws Exception;
	public CoviMap getBudgetAmount(CoviMap params) throws Exception;
	public CoviMap getBudgetType(CoviMap params) throws Exception;
	public CoviMap addExecuteRegist (CoviMap params) throws Exception;
	
	public int changeStatus(CoviMap params) throws Exception;
	
}