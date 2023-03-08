package egovframework.coviframework.service.impl;

import java.util.List;

import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.data.CoviList;
import egovframework.baseframework.data.CoviMapperOne;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import egovframework.coviframework.service.CoviService;;

@Service("coviService")
public class CoviServiceImpl extends EgovAbstractServiceImpl implements CoviService {

	@Resource(name="coviMapperOne")
	private CoviMapperOne coviMapperOne;
	
	@Override
	public int insert(String serviceId, CoviMap coviMap) throws Exception
	{
		coviMapperOne.insert(serviceId, coviMap);
		return  1;
	}
	
	@Override
	public List<?>   list(String serviceId, Object coviMap) throws Exception
	{
		return coviMapperOne.selectList(serviceId, coviMap);
	}

	 
}