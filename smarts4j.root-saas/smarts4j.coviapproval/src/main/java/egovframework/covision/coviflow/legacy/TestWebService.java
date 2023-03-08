package egovframework.covision.coviflow.legacy;

import java.io.Serializable;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class TestWebService {
	@WebMethod
	public ResultVO changeState1(String legacyKey) {
		System.out.println("Parameter : " + legacyKey);
		ResultVO result = new ResultVO("TRUE", "Success to process.");
		
		return result;
	}
	
	@WebMethod
	public String[] changeState2(String legacyKey) {
		System.out.println("Parameter : " + legacyKey);
		
		String[] returnObj = new String[2];
		returnObj[0] = "TRUE";
		returnObj[1] = "Success to process.";
		return returnObj;
	}
	
	@WebMethod
	public String changeState3(String legacyKey) {
		System.out.println("Parameter : " + legacyKey);
		return "TRUE";
	}

	@WebMethod
	public String changeState4(String arg1, int arg2, RequestVO request) {
		System.out.println("Parameter1 : " + arg1);
		System.out.println("Parameter2 : " + arg2);
		System.out.println("Parameter3-1 : " + request.getKey1());
		System.out.println("Parameter3-2 : " + request.getKey2());
		System.out.println("Parameter3-3 : " + request.getKey3());
		
		return "TRUE";
	}
	
	@WebMethod
	public ResultVO changeState5(String key1, String key2) {
		System.out.println("Parameter1 : " + key1);
		System.out.println("Parameter2 : " + key2);
		
		ResultVO result = new ResultVO("TRUE", "Success to process.");
		return result;
	}
	
	public static class ResultVO implements Serializable {
		private static final long serialVersionUID = -3788468010675049611L;
		private String code;
		private String msg;
		
		public ResultVO (String code, String msg) {
			this.code = code;
			this.msg = msg;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
	
	public static class RequestVO implements Serializable {
		private static final long serialVersionUID = 6808128864421490894L;
		private String key1;
		private String key2;
		private String key3;
		public String getKey1() {
			return key1;
		}
		public void setKey1(String key1) {
			this.key1 = key1;
		}
		public String getKey2() {
			return key2;
		}
		public void setKey2(String key2) {
			this.key2 = key2;
		}
		public String getKey3() {
			return key3;
		}
		public void setKey3(String key3) {
			this.key3 = key3;
		}
	}
	
	public static void main(String [] args ) throws Exception {
		String publishUrl = "http://localhost:8081/wstest/services/TestWebService";
		Endpoint.publish(publishUrl, new TestWebService());
		
		System.out.println(">>>>>>>>> JAX-WS Started [ "+ publishUrl +" ]");
	}
}
