package egovframework.covision.coviflow.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/*
 * activiti 방식을 사용할 경우가 아니면 deprecated 시킬 것
 * */
public class MailAuthenticator extends Authenticator {
    private String id;
    private String pw;
    
    public MailAuthenticator(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(id, pw);
	}
}