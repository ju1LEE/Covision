package egovframework.covision.coviflow.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * @Class Name  : CoviEncryptPBE.java
 * @Description : PBE 인코딩/디코딩 방식을 이용한 데이터를 암호화/복호화하는 Business Interface class
 *
 * << 개정이력(Modification Information) >>
 *   
 *   수정일                      수정자                  수정내용
 *  -------       --------    ---------------------------
 *
 * @author 이성문
 * @since 2015.12.29
 * @version 1.0
 * @see
 */
public class PBE {
	/**
     * 데이터를 암호화하는 기능
     * @param byte[] data 암호화할 데이터
     * @param pw  String  암호화 패스워드
     * @return String result 암호화된 데이터
     * @exception Exception
     */
    public static String encode(String data, String pw){
    	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    	encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(pw);
		return encryptor.encrypt(data);
    }
    
    /**
     * 데이터를 복호화하는 기능
     * @param String data 복호화할 데이터
     *         String pw 복호화 패스워드
     * @return String result 복호화된 데이터
     * @exception Exception
     */
    public static String decode(String data, String pw){
    	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    	encryptor.setAlgorithm("PBEWithMD5AndDES");
    	encryptor.setPassword(pw);
		return encryptor.decrypt(data);
    }
}