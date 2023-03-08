package egovframework.coviframework.service;

import java.util.List;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviList;


public interface CoviService {

	int insert(String svcId, CoviMap params) throws Exception;
	List<?> list(String svcId, Object params) throws Exception;
}
