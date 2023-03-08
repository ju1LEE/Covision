package egovframework.covision.coviflow.xform.service;

import egovframework.baseframework.data.CoviMap;


public interface XFormEditorSvc {
	public String getHTMLFormFile(String fileName) throws Exception;
	public String getJSFormFile(String fileName) throws Exception;
	public CoviMap selectFormList() throws Exception;
	public String createHTMLFormFile(String htmlFileName, String htmlFileContent)throws Exception;
	public String createJSFormFile(String jsFileName, String jsFileContent)throws Exception;
	public String setTemplate(String mode, CoviMap params) throws Exception;
	public String getEncrypt(String encryptStr)throws Exception;
}
