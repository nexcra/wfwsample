
/*
**************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : MailTemplate.java
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 이메일 템플릿 처리
*   적용범위  : golf
*   작성일자  : 2009-06-22
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
**************************************************************************************************
*/

package com.bccard.golf.common.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.bccard.golf.common.GolfConfig;


/** ****************************************************************************
 * 메일 발송 클래스. 
 * <pre>
 * </pre>
 * @version   1.0
 * @author    2003 6 18 <A href="mailto:ykcho@e4net.net">yongkookcho</A>
 **************************************************************************** */
public class MailTemplate {
    /** SMTP 서버.                                 */   private String host;
    /** SMTP 서버 포트.                            */   private int port;
    /** ESMTP 서버 로그인 ID.                      */   private String user;
    /** ESMTP 서버 로그인 비밀번호.                */   private String pwd;
    /** 디버그를 위한 메시지 전송 상황 출력 여부.  */   private boolean debug;

    /** **********************************************
     * 메일 발송 클래스를 생성한다..
     * @param host 발송할 SMTP 서버
     *********************************************** */
    public MailTemplate() {
        this.host = "mail.bccard.com";
        this.port = 25;     // 기본포트
        this.user = null;
        this.pwd  = null;
        this.debug = false;  
 
   }

    /** **********************************************
     * 메일 발송 포트를 정의한다..
     * @param port 발송할 SMTP 서버 포트
     *********************************************** */
    public void setPort(int port)
    {
        this.port = port;
    }

    /** **********************************************
     * ESMTP 서버 로그인 ID.
     * @param user 로그인 ID
     *********************************************** */
    public void setUser(String user)
    {
        this.user = user;
    }

    /** **********************************************
     * ESMTP 서버 로그인 비밀번호.
     * @param pwd 비밀번호
     *********************************************** */
    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    /** **********************************************
     * 디버그를 위한 메시지 전송 상황 출력 여부 설정.
     * @param debug 디버그 여부
     *********************************************** */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /** **********************************************
     * 메일을 보내는 사용할 세션 객체를 생성한다.
     * @return 메일 세션 객체
     *********************************************** */
    private Session getSession() {
        Properties pt = System.getProperties();
        pt.put("mail.smtp.host",this.host);
        pt.put("mail.smtp.port",String.valueOf(this.port) );
        if ( user != null && pwd != null ) {
            pt.put("mail.smtp.auth","true");    // 로그인이 해야할때 이 설정이 추가돼야함
        }
        Session session = Session.getDefaultInstance(pt,null);
        session.setDebug(this.debug);
        return session;
    }

    /** **********************************************
     * 보낼 메시지의 MimeMessage를 작성한다.
     * @param sess 메일 세션 객체
     * @param entity 메일 발송 항목 정의
     * @return 보낼 MimeMessage 객체
     *********************************************** */
//    private MimeMessage getMessage(Session sess,
//                                   MailEntity entity)
//                            throws MessagingException
//    {
//            MimeMessage message = new MimeMessage(sess);
//
//            // 보내는 주소
//            message.setFrom( entity.from );
//
//            // 받는 주소/참조/숨은참조
//            message.setRecipients(Message.RecipientType.TO, entity.to );
//            if ( entity.cc  != null ) message.setRecipients(Message.RecipientType.CC , entity.to  );
//            if ( entity.bcc != null ) message.setRecipients(Message.RecipientType.BCC, entity.bcc );
//
//            // 제목
//            message.setSubject( entity.subject );
//            //message.setText( entity.subject );
//
//            // 중요도
//            if ( entity.priority==1 ) {
//                message.setHeader("X-Priority", String.valueOf(entity.priority) );
//                message.setHeader("X-MSMail-Priority", "High" );
//                message.setHeader("importance", "High" );
//            } else if ( entity.priority==2 ) {
//                message.setHeader("X-Priority", String.valueOf(entity.priority) );
//                message.setHeader("X-MSMail-Priority", "High" );
//                message.setHeader("importance", "High" );
//            } else if ( entity.priority==3 ) {
//                message.setHeader("X-Priority", String.valueOf(entity.priority) );
//                message.setHeader("X-MSMail-Priority", "Normal" );
//                message.setHeader("importance", "Normal" );
//            } else if ( entity.priority==4 ) {
//                message.setHeader("X-Priority", String.valueOf(entity.priority) );
//                message.setHeader("X-MSMail-Priority", "Low" );
//                message.setHeader("importance", "Low" );
//            } else if ( entity.priority==5 ) {
//                message.setHeader("X-Priority", String.valueOf(entity.priority) );
//                message.setHeader("X-MSMail-Priority", "Low" );
//                message.setHeader("importance", "Low" );
//            }
//
//            // 수신 확인 설정
//            if ( entity.notification ) {
//                if ( entity.notificationAddress != null ) {
//                    message.setHeader("Disposition-Notification-To", entity.notificationAddress.toString() );
//                } else {
//                    message.setHeader("Disposition-Notification-To", entity.from.toString() );
//                }
//            }
//
//            MimeMultipart multipart_mixed   = null;     // 첨부 파일이 있을때의 컨테이너
//            MimeMultipart multipart_related = null;     // 포함 파일이 있을때의 컨테이너
//            MimeMultipart multipart_alter   = new MimeMultipart("alternative"); // 기본 컨테이너
//
//            MimeBodyPart bodypart_plain = new MimeBodyPart();
//            MimeBodyPart bodypart_html  = new MimeBodyPart();
//
//            // 포함파일이 있으면..
//            String inHtml = entity.html;
//            if ( entity.inlineList.size() > 0 ) {
//                for (int i=0; i<entity.inlineList.size(); i++ ) {
//                    File f = (File) entity.inlineList.get(i);
//                    String fname = f.getName();
//                    String fpath = f.getPath();
//                    // 메시지중에 파일 전체 경로가 있다면 포함파일의 cid: 로 바꾸어준다.
//                    inHtml = this.replace(inHtml, fpath, "cid:embedded" + f.hashCode() + "" );
//                }
//            }
//
//            // 기본 컨테이너에 본문 입력
//            bodypart_html.setContent(inHtml,"text/html; charset=\"" + entity.charset + "\"" );
//            bodypart_html.setHeader("Content-Transfer-Encoding","base64");
//            bodypart_plain.setContent(entity.plain,"text/plain; charset=\"" + entity.charset + "\"" );
//            multipart_alter.addBodyPart(bodypart_plain);
//            multipart_alter.addBodyPart(bodypart_html);
//
//            // 포함파일이 있으면
//            if ( entity.inlineList.size() > 0 ) {
//                multipart_related = new MimeMultipart("related");       // 포함파일용 컨테이너 작성
//                MimeBodyPart bodypart_related = new MimeBodyPart();
//                bodypart_related.setContent(multipart_alter);           // 포함 컨테이너에 기본 컨테이너 등재
//                multipart_related.addBodyPart(bodypart_related);
//                for (int i=0; i<entity.inlineList.size(); i++ ) {        // 포함 컨테이너에 파일 등재
//                    File f = (File) entity.inlineList.get(i);
//                    String fname = f.getName();
//                    String fpath = f.getPath();
//                    DataSource file_source = new FileDataSource( fpath );
//                    DataHandler file_handler = new DataHandler(file_source);
//                    MimeBodyPart bodypart_inline = new MimeBodyPart();
//                    bodypart_inline.setDataHandler(file_handler);
//                    bodypart_inline.setDisposition(bodypart_inline.INLINE);
//                    try {
//                        bodypart_inline.setFileName( MimeUtility.encodeWord(fname) );
//                    } catch ( UnsupportedEncodingException e1 ) {
//                        bodypart_inline.setFileName( fname );
//                    }
//                    bodypart_inline.setHeader("Content-ID","<embedded" + f.hashCode() + ">");
//                    multipart_related.addBodyPart(bodypart_inline);
//                }
//            }
//
//            // 첨부파일이 있으면
//            if ( entity.attachList.size() > 0 ) {
//                multipart_mixed = new MimeMultipart("mixed");           // 첨부파일 컨테이너 작성
//                MimeBodyPart bodypart_mixed = new MimeBodyPart();
//                if ( multipart_related != null ) {                      // 포함 컨테이너가 있으면
//                    bodypart_mixed.setContent(multipart_related);       // 첨부 컨테이너에 포함 컨테이너 등재
//                } else {
//                    bodypart_mixed.setContent(multipart_alter);         // 아니면 기본 컨테이너 등재
//                }
//                multipart_mixed.addBodyPart(bodypart_mixed);
//                for (int i=0; i<entity.attachList.size(); i++ ) {        // 첨부 컨테이너에 파일 등재
//                    File f = (File) entity.attachList.get(i);
//                    String fname = f.getName();
//                    String fpath = f.getPath();
//                    DataSource file_source = new FileDataSource( fpath );
//                    DataHandler file_handler = new DataHandler(file_source);
//                    MimeBodyPart bodypart_attach = new MimeBodyPart();
//                    bodypart_attach.setDataHandler(file_handler);
//                    try {
//                        bodypart_attach.setFileName( MimeUtility.encodeWord(fname) );
//                    } catch ( UnsupportedEncodingException e1 ) {
//                        bodypart_attach.setFileName( fname );
//                    }
//                    multipart_mixed.addBodyPart(bodypart_attach);
//                }
//            }
//
//            // 메시지에 컨테이너 등재
//            if ( multipart_mixed != null ) {
//                message.setContent(multipart_mixed);
//            } else {
//                if ( multipart_related != null ) {
//                    message.setContent(multipart_related);
//                } else {
//                    message.setContent(multipart_alter);
//                }
//            }
//            return message;
//    }

    /** **********************************************
     * 메일 메시지를 발송한다..
     * @param entity 메일 발송 항목 정의
     *********************************************** */
//    public void send(MailEntity entity)
//                throws MessagingException
//    {
//        Transport transport = null;
//        try {
//            if ( entity.plain == null ) entity.plain = "";
//            if ( entity.html  == null ) entity.html  = "";
//
//            Session session = getSession();                     // 보낼 세션
//            MimeMessage message = getMessage(session,entity);   // 보낼 메시지
//            transport = session.getTransport("smtp");           // 보낼 Transport
//            if ( this.user != null && this.pwd != null) {
//                transport.connect(this.host, this.user, this.pwd);  // 로그인 커넥션
//            } else {
//                transport.connect();
//            }
//            transport.sendMessage( message, message.getAllRecipients() );   // 메시지 발송
//            System.out.print("-----------------");
//        } catch ( MessagingException e1 ) {
//            throw e1;
//        } finally {
//            // Transport 종료
//            try { if(transport!=null) transport.close(); } catch(MessagingException egnore) {}
//        }
//    } 

	/***********************************************************************
	 * 액션처리.
	 * @param source        String
	 * @param subject       String
	 * @param object        String
	 * @return 응답정보
	 **********************************************************************/
	private String replace(String source, String subject, String object) {
		if ( source == null ) return null;
		StringBuffer rtnStr = new StringBuffer();
		String preStr = "";
		String nextStr = source;
		while ( source.indexOf(subject) >= 0 ) {
			preStr = source.substring(0, source.indexOf(subject));
			nextStr = source.substring(source.indexOf(subject)+subject.length(), source.length());
			source = nextStr;
			rtnStr.append(preStr).append(object);
		}
		rtnStr.append(nextStr);
		return rtnStr.toString();
	}


	/***********************************************************************
	 * 템플릿 읽어 오기
	 * @param source        String
	 * @param subject       String
	 * @param object        String
	 * @return 응답정보
	 **********************************************************************/
	 
	
	public String generateTemplate(String fileNm){
		StringBuffer contents = new StringBuffer();
		GolfConfig config = GolfConfig.getInstance();
		String tempEmail = config.getEmailTempt()+fileNm;
		File testFile = new File(tempEmail);
		
		return contents.toString();
	}
	
	
	
}