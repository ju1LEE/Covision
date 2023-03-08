package egovframework.covision.coviflow.admin.service;


import egovframework.baseframework.data.CoviMap;

public interface DocFolderManagerSvc {
	public CoviMap selectDocClass(CoviMap params) throws Exception;
	public CoviMap selectDocClassPopup(CoviMap params) throws Exception;
	public CoviMap selectDdlCompany(CoviMap params) throws Exception;
	public Object insert(CoviMap params)throws Exception;
	public int delete(CoviMap params)throws Exception;
	public CoviMap selectdocclassRetrieveFolder(CoviMap params)throws Exception;
	public int update(CoviMap params)throws Exception;
	public CoviMap selectdocclassOne(CoviMap params) throws Exception;
}
