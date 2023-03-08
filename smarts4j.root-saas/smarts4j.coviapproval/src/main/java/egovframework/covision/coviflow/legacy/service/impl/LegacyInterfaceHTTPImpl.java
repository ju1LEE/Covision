package egovframework.covision.coviflow.legacy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import egovframework.baseframework.data.CoviMap;
import egovframework.coviframework.util.HttpsUtil;
import egovframework.covision.coviflow.legacy.service.LegacyInterfaceSvc;

@Service
public class LegacyInterfaceHTTPImpl extends LegacyInterfaceCommon implements LegacyInterfaceSvc {

	@Override
	public void call() throws Exception {
		// TODO encoding 동작 확인 필요
		this.logParam.put("ActionValue", this.legacyInfo.getString("HttpUrl"));
		
		CoviMap result = executeHTTP(this.legacyInfo, this.legacyParams);
	}

	private CoviMap executeHTTP(CoviMap legacyInfo, CoviMap parameter) throws Exception {
		CoviMap result = new CoviMap();
		
		try {
			Map<String, Object> responseMap;
			
			String sURL = legacyInfo.optString("HttpUrl"); // http://localhost:8082/approval/admin/legacySample/restTest_json.do
			String method = legacyInfo.optString("Method"); // GET/POST
			String encoding = legacyInfo.optString("Encoding"); // UTF-8/EUC-KR
			String bodyType = legacyInfo.optString("BodyType"); // JSON/XML
			String responseBodyType = legacyInfo.optString("ResponseBodyType"); // JSON/XML
			String bodyData = legacyInfo.optString("HttpBody"); // "{\"bodyparam1\":\"${InitiatorOUDisplay}\",\"bodyparam2\":\"${INITIATORUNITID}\"}"
			String params = legacyInfo.optString("HttpParams"); // "{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}"
			String basicAuth = "";
			//basicAuth = "Basic " + Base64.encode(("id" + ":" + "pw").getBytes(StandardCharsets.UTF_8));
			
			String contentType;
			switch(bodyType) {
				case "JSON": contentType = "application/json"; break;
				case "XML": contentType = "application/xml"; break;
				default: contentType = "text/plain"; break;
			}
			
			// requset body 파라미터 매핑 ${xxx}
			bodyData = bindingParameters(parameter, bodyData, bodyType);
			
			// request parameter 파라미터 매핑
			params = bindingParameters(parameter, params, "JSON");
			List<NameValuePair> paramData;
			paramData = new ArrayList<NameValuePair>();
			Set<Map.Entry<String, Object>> set = new CoviMap(params).entrySet();
			for(Map.Entry<String, Object> entry : set) {
				paramData.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}
			
			// 호출
			HttpsUtil httpsUtil = new HttpsUtil();
			responseMap = httpsUtil.httpsClientConnectResponse(sURL, method, bodyData, contentType, encoding, basicAuth, paramData);
			
			for(Map.Entry<String, Object> entry : responseMap.entrySet()) {
				result.put(entry.getKey(), entry.getValue());
			}
			
			String responseCode = result.optString("STATUSCODE");
			if(!responseCode.equals("200")) throw new Exception("Error - HTTP Response " + responseCode);
			
			// 리턴값 체크
			String outStatusKey = legacyInfo.optString("OutStatusKey");			// RESULT - out status 키
			String outCompareType = legacyInfo.optString("OutCompareType");		// E - out parameter 비교조건 - E : 같을때 성공 , NE : 다를떄 성공
			String outCompareValue = legacyInfo.optString("OutCompareValue");	// S - out parameter 비교값
			String outMsgKey = legacyInfo.optString("OutMsgKey");				// MESSAGE - out message 키
			String strResult = result.optString("MESSAGE");
			
			this.logParam.put("RawResponse", strResult);
			
			if(!strResult.isEmpty() && !outStatusKey.isEmpty()) {
				
				String rtnStatus = "";
				String rtnMessage = "";
				String errorMessage = "";
				
				if(responseBodyType.equals("JSON")) {
					Object resultCodeObj = JsonPath.read(strResult, outStatusKey); // parse
					if(resultCodeObj == null) ;
					else if(resultCodeObj instanceof String) rtnStatus = resultCodeObj.toString();
					else rtnStatus = ((List<String>)resultCodeObj).get(0).toString();
					
					this.logParam.put("ResultCode", rtnStatus);
					
		 			errorMessage = "Error " + outStatusKey + "=" + rtnStatus;
		 			if(!outMsgKey.isEmpty()) {
		 				Object resultMsgObj = JsonPath.read(strResult, outMsgKey); // parse
		 				if(resultMsgObj == null) ;
		 				else if(resultMsgObj instanceof String) rtnMessage = resultMsgObj.toString();
		 				else rtnMessage = ((List<String>)resultMsgObj).get(0).toString();
		 				
		 				this.logParam.put("ResultMessage", rtnMessage);
						errorMessage = errorMessage + " , " + outMsgKey + "=" + rtnMessage;
					}
		 			if(outCompareType.equalsIgnoreCase("E") && !rtnStatus.equals(outCompareValue)) {
						throw new Exception(errorMessage);
					}else if(outCompareType.equalsIgnoreCase("NE") && rtnStatus.equals(outCompareValue)) {
						throw new Exception(errorMessage);
					}
				}else if(responseBodyType.equals("XML")) {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 			DocumentBuilder builder = factory.newDocumentBuilder();
		 			Document doc = builder.parse(new InputSource(new StringReader(strResult)));
		 			XPath xpath = XPathFactory.newInstance().newXPath();
		 			Node resultCodeNode = (Node)xpath.compile(outStatusKey).evaluate(doc, XPathConstants.NODE);
		 			//NodeList resultCodeNodeList = (NodeList)xpath.compile("rootNode/statusList").evaluate(doc, XPathConstants.NODESET);
		 			if(resultCodeNode != null) {
		 				rtnStatus = resultCodeNode.getTextContent();
		 				this.logParam.put("ResultCode", rtnStatus);
		 			}
		 			errorMessage = "Error " + outStatusKey + "=" + rtnStatus;
		 			if(!outMsgKey.isEmpty()) {
		 				Node resultMsgNode = (Node)xpath.compile(outMsgKey).evaluate(doc, XPathConstants.NODE);
		 				if(resultMsgNode != null) {
		 					rtnMessage = resultMsgNode.getTextContent();
		 					this.logParam.put("ResultMessage", rtnMessage);
		 				}
						errorMessage = errorMessage + " , " + outMsgKey + "=" + rtnMessage;
					}
		 			if(outCompareType.equalsIgnoreCase("E") && !rtnStatus.equals(outCompareValue)) {
						throw new Exception(errorMessage);
					}else if(outCompareType.equalsIgnoreCase("NE") && rtnStatus.equals(outCompareValue)) {
						throw new Exception(errorMessage);
					}
				}
			}
			
		}catch(Exception e) {
			//logger.error(e.getLocalizedMessage(), e);
			//result.put(, e.getMessage());
			throw e;
		}finally {
			// Close resources.
		}
		return result;
	}
	
	/*
	private void test() throws Exception {
		CoviMap result = new CoviMap();
		CoviMap legacyInfo = new CoviMap();
		CoviMap params = new CoviMap();
		
		
		try {
			// for HTTP
			legacyInfo.put("HttpUrl","http://localhost:8082/approval/admin/legacySample/restTest_json.do");
			legacyInfo.put("Method","POST");		// GET/POST
			legacyInfo.put("Encoding","UTF-8");		// UTF-8/EUC-KR
			legacyInfo.put("BodyType","JSON");		// JSON/XML
			legacyInfo.put("HttpBody","{\"bodyparam1\":\"${InitiatorOUDisplay}\",\"bodyparam2\":\"${INITIATORUNITID}\"}");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			// return
			legacyInfo.put("OutStatusKey","RESULT");	// out parameter 상태 키
			legacyInfo.put("OutCompareType","NE");		// out parameter 비교조건 - E : 같을때 성공 , NE : 다를떄 성공
			legacyInfo.put("OutCompareValue","E");		// out parameter 비교값
			legacyInfo.put("OutMsgKey","MESSAGE");		// out parameter message 키
			// test parameter
			params.put("InitiatorOUDisplay", "aaa1");
			params.put("INITIATORUNITID", "bbb2");
			params.put("InitiatorDisplay", "ccc3");
			params.put("INITIATORNAME", "ddd4");
			
			
			// case JSON
			// return : $.status , $.statusObj.status , $.statusList[0].status
			legacyInfo.put("OutStatusKey","$.statusList[0].status");
			legacyInfo.put("OutCompareType","E");
			legacyInfo.put("OutCompareValue","SUCCESS");
			legacyInfo.put("OutMsgKey","$.statusList[0]message");
			
			legacyInfo.put("HttpUrl","http://localhost:8082/approval/admin/legacySample/restTest_json.do");
			legacyInfo.put("HttpBody","{\"bodyparam1\":\"${InitiatorOUDisplay}\",\"bodyparam2\":\"${INITIATORUNITID}\"}");
			legacyInfo.put("BodyType","JSON");
			legacyInfo.put("Method","POST");
			legacyInfo.put("Encoding","UTF-8");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // json post utf-8
			legacyInfo.put("Method","POST");
			legacyInfo.put("Encoding","EUC-KR");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // json post euc-kr
			legacyInfo.put("Method","GET");
			legacyInfo.put("Encoding","UTF-8");
			legacyInfo.put("Params","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // json get utf-8
			legacyInfo.put("Method","GET");
			legacyInfo.put("Encoding","EUC-KR");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // json get euc-kr
			
			// case XML
			// return : //*[local-name()='rootNode']/status , //*[local-name()='rootNode']/statusC , //*[local-name()='rootNode']/statusChild/status , //*[local-name()='rootNode']/statusChild/statusC , //*[local-name()='rootNode']/statusList , //*[local-name()='rootNode']/statusListC  
			legacyInfo.put("OutStatusKey","//*[local-name()='rootNode']/statusChild/statusC"); // 
			legacyInfo.put("OutCompareType","E");
			legacyInfo.put("OutCompareValue","SUCCESS");
			legacyInfo.put("OutMsgKey","//*[local-name()='rootNode']/statusChild/messageC");
			
			legacyInfo.put("HttpUrl","http://localhost:8082/approval/admin/legacySample/restTest_xml.do");
			legacyInfo.put("HttpBody","<bodyData><bodyparam1>${InitiatorOUDisplay}</bodyparam1><bodyparam2>${INITIATORUNITID}</bodyparam2></bodyData>");
			legacyInfo.put("BodyType","XML");
			legacyInfo.put("Method","POST");
			legacyInfo.put("Encoding","UTF-8");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // xml post utf-8
			legacyInfo.put("Method","POST");
			legacyInfo.put("Encoding","EUC-KR");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // xml post euc-kr
			legacyInfo.put("Method","GET");
			legacyInfo.put("Encoding","UTF-8");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // xml get utf-8
			legacyInfo.put("Method","GET");
			legacyInfo.put("Encoding","EUC-KR");
			legacyInfo.put("HttpParams","{\"param1\":\"${InitiatorDisplay}\",\"param2\":\"${INITIATORNAME}\",\"encoding\":\"" + legacyInfo.optString("encoding") + "\"}");
			result = executeHTTP(legacyInfo, params); // xml get euc-kr
			
			String aa = "1";
		}
		catch(Exception e) {
			throw e;
		} finally {
		}
		
		
	}
	*/
	
	
	
	
	/**
	 * apache.common.lang3 활용 keyword 치환.
	 * @param params
	 * @param BodyData
	 * @param bodyType(JSON/XML)
	 * @return
	 * @throws Exception 
	 */
	public String bindingParameters(CoviMap parameter, String originalBodyData, String bodyType) throws Exception {
		Map<String, Object> parameterMap = new ObjectMapper().readValue(parameter.toString(), Map.class);
		String replacedBodyData = originalBodyData;
		for(Map.Entry<String, Object> entry : parameterMap.entrySet()) {
			if(bodyType.equals("JSON")) entry.setValue(JSONObject.escape(entry.getValue().toString()));
			else if(bodyType.equals("XML")) entry.setValue("<![CDATA["+entry.getValue()+"]]>");
		}
		replacedBodyData = StrSubstitutor.replace(originalBodyData, parameterMap);
		
		return replacedBodyData;
	}
	
}

