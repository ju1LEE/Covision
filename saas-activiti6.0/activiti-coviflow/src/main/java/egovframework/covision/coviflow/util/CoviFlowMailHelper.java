/**
 * @Class Name : CoviFlowMailHelper.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoviFlowMailHelper {

	/*
	 * activiti mail send부분 그대로 활용 - 문제가 발생 시 다른 외부라이브러리를 고려할 것
	 * 웹서비스 형태로 호출해야 할지에 대해서도 확인 해 볼 것...
	 * 
	 * org.activiti.engine.impl.bpmn.behavior.MailActivityBehavior
	 * 
	 * */
	
	private static final Logger LOG = LoggerFactory.getLogger(CoviFlowMailHelper.class);
	
	/*
	 * 블로깅 참고 http://biyam.tistory.com/29
	 * */
	public static Message message = null;


	public static void createMail(){
		MimeBodyPart mbp = new MimeBodyPart();
		
		try{
			//메일 본문 작성
			//text 경우
			mbp.setText("Mail send");
			
			//message 객체에 본문을 넣기 위하여 Multipart 객체 생성
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp);
			
			//파일 첨부일 경우
			MimeBodyPart mbp_file = new MimeBodyPart();
			mbp_file.setDataHandler(new DataHandler(new FileDataSource("[보낼 파일 경로]")));
			mbp_file.setFileName("[보낼 파일 이름]");
			mp.addBodyPart(mbp_file);
			
			//html일 경우
			MimeBodyPart mbp_html = new MimeBodyPart();
			mbp_html.setDataHandler(new DataHandler(new ByteArrayDataSource(new FileInputStream(new File("[보낼 HTML 경로]")), "text/html")));
			mp.addBodyPart(mbp_html);
			
			//메일 제목 넣기
			message.setSubject("[보낼 메일 제목]");
			//메일 본문을 넣기
			message.setContent(mp);
			//보내는 날짜
			message.setSentDate(new Date());
			//보내는 메일 주소
			message.setFrom(new InternetAddress("[보낸 사람의 메일 주소]]"));
			
			//단건 전송일 때는 사용 start
			//message.setRecipient(RecipientType.TO, new InternetAddress(""));
			//단건 전송일 때는 사용 end
			
			//복수 건 전송일 때는 사용 start 
			InternetAddress[] receive_address = {new InternetAddress("보낼 사람의 메일 주소")};
			message.setRecipients(RecipientType.TO, receive_address);
			//복수 건 전송일 때는 사용 end
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void connectSMTP(){
		Properties prop = new Properties();

		//Gmail 연결을 위하여 아래 설정 적용
		//사내 메일 망일 경우 smtp host 만 설정해도 됨 (특정 포트가 아닐경우)
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.starttls.enable","true");
		prop.put("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.auth", "true");
        
		//SMTP 서버 계정 정보 (GMail 용)
        MailAuthenticator authenticator = new MailAuthenticator("[Gmail ID]", "[Gmail Password]");
        
		Session session = Session.getDefaultInstance(prop, authenticator);
		
		try{
			message = new MimeMessage(session);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMail(){
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/* activiti 방식
	public static void sendMail(Mail mail, ActivityExecution execution){
		boolean doIgnoreException = mail.ignoreException;
	    String exceptionVariable = mail.exceptionVariableName;
	    Email email = null;
	    try {
	      String toStr = mail.to;
	      String fromStr = mail.from;
	      String ccStr = mail.cc;
	      String bccStr = mail.bcc;
	      String subjectStr = mail.subject;
	      String textStr = mail.text;//mail.textVar;
	      String htmlStr = mail.html;//mail.htmlVar;
	      String charSetStr = mail.charset;
	      //첨부 처리가 필요할 경우
	      //List<File> files = new LinkedList<File>();
	      //List<DataSource> dataSources = new LinkedList<DataSource>();
	      //getFilesFromFields(attachments, execution, files, dataSources);
		  
	      
	      //이메일을 보내는 부분은 좀 더 단순화 하거나, API 사용법을 별도로 찾아서 해결 할 것
	      //아래 부분에 대한 활용 여부는 실제 개발단에서 이용할 때 추가적으로 판단 할 것.
	      //
	      
	      email = createEmail(textStr, htmlStr, false);
	      addTo(email, toStr);
	      setFrom(email, fromStr, execution.getTenantId());
	      addCc(email, ccStr);
	      addBcc(email, bccStr);
	      setSubject(email, subjectStr);
	      setMailServerProperties(email, execution.getTenantId());
	      setCharset(email, charSetStr);
	      //attach(email, files, dataSources);

	      email.send();
	      
	    } catch (ActivitiException e) {
	      //handleException(execution, e.getMessage(), e, doIgnoreException, exceptionVariable);
	      	
	    } catch (EmailException e) {
	      //handleException(execution, "Could not send e-mail in execution " + execution.getId(), e, doIgnoreException, exceptionVariable);
	    }
		
	}
	
	public class Mail{
		String to;
		String from;
		String cc;
		String bcc;
		String subject;
		String text;
		String textVar;
		String html;
		String htmlVar;
		String charset;
		Boolean ignoreException;
		String exceptionVariableName;
		//protected Expression attachments;
		
		public Mail(String to, String from, String cc,String bcc,String subject,String text,
				String textVar,String html,String htmlVar,String charset,Boolean ignoreException,String exceptionVariableName){
			this.to = to;
			this.from = from;
			this.cc = cc;
			this.bcc = bcc;
			this.subject = subject;
			this.text = text;
			this.textVar = textVar;
			this.html = html;
			this.htmlVar = html;
			this.charset = charset;
			this.ignoreException = ignoreException;
			this.exceptionVariableName = exceptionVariableName;
		}
		
	}
	
	*/
	
}
