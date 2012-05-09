
/*
**************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : MailTemplate.java
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : �̸��� ���ø� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-22
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * ���� �߼� Ŭ����. 
 * <pre>
 * </pre>
 * @version   1.0
 * @author    2003 6 18 <A href="mailto:ykcho@e4net.net">yongkookcho</A>
 **************************************************************************** */
public class MailTemplate {
    /** SMTP ����.                                 */   private String host;
    /** SMTP ���� ��Ʈ.                            */   private int port;
    /** ESMTP ���� �α��� ID.                      */   private String user;
    /** ESMTP ���� �α��� ��й�ȣ.                */   private String pwd;
    /** ����׸� ���� �޽��� ���� ��Ȳ ��� ����.  */   private boolean debug;

    /** **********************************************
     * ���� �߼� Ŭ������ �����Ѵ�..
     * @param host �߼��� SMTP ����
     *********************************************** */
    public MailTemplate() {
        this.host = "mail.bccard.com";
        this.port = 25;     // �⺻��Ʈ
        this.user = null;
        this.pwd  = null;
        this.debug = false;  
 
   }

    /** **********************************************
     * ���� �߼� ��Ʈ�� �����Ѵ�..
     * @param port �߼��� SMTP ���� ��Ʈ
     *********************************************** */
    public void setPort(int port)
    {
        this.port = port;
    }

    /** **********************************************
     * ESMTP ���� �α��� ID.
     * @param user �α��� ID
     *********************************************** */
    public void setUser(String user)
    {
        this.user = user;
    }

    /** **********************************************
     * ESMTP ���� �α��� ��й�ȣ.
     * @param pwd ��й�ȣ
     *********************************************** */
    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    /** **********************************************
     * ����׸� ���� �޽��� ���� ��Ȳ ��� ���� ����.
     * @param debug ����� ����
     *********************************************** */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /** **********************************************
     * ������ ������ ����� ���� ��ü�� �����Ѵ�.
     * @return ���� ���� ��ü
     *********************************************** */
    private Session getSession() {
        Properties pt = System.getProperties();
        pt.put("mail.smtp.host",this.host);
        pt.put("mail.smtp.port",String.valueOf(this.port) );
        if ( user != null && pwd != null ) {
            pt.put("mail.smtp.auth","true");    // �α����� �ؾ��Ҷ� �� ������ �߰��ž���
        }
        Session session = Session.getDefaultInstance(pt,null);
        session.setDebug(this.debug);
        return session;
    }

    /** **********************************************
     * ���� �޽����� MimeMessage�� �ۼ��Ѵ�.
     * @param sess ���� ���� ��ü
     * @param entity ���� �߼� �׸� ����
     * @return ���� MimeMessage ��ü
     *********************************************** */
//    private MimeMessage getMessage(Session sess,
//                                   MailEntity entity)
//                            throws MessagingException
//    {
//            MimeMessage message = new MimeMessage(sess);
//
//            // ������ �ּ�
//            message.setFrom( entity.from );
//
//            // �޴� �ּ�/����/��������
//            message.setRecipients(Message.RecipientType.TO, entity.to );
//            if ( entity.cc  != null ) message.setRecipients(Message.RecipientType.CC , entity.to  );
//            if ( entity.bcc != null ) message.setRecipients(Message.RecipientType.BCC, entity.bcc );
//
//            // ����
//            message.setSubject( entity.subject );
//            //message.setText( entity.subject );
//
//            // �߿䵵
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
//            // ���� Ȯ�� ����
//            if ( entity.notification ) {
//                if ( entity.notificationAddress != null ) {
//                    message.setHeader("Disposition-Notification-To", entity.notificationAddress.toString() );
//                } else {
//                    message.setHeader("Disposition-Notification-To", entity.from.toString() );
//                }
//            }
//
//            MimeMultipart multipart_mixed   = null;     // ÷�� ������ �������� �����̳�
//            MimeMultipart multipart_related = null;     // ���� ������ �������� �����̳�
//            MimeMultipart multipart_alter   = new MimeMultipart("alternative"); // �⺻ �����̳�
//
//            MimeBodyPart bodypart_plain = new MimeBodyPart();
//            MimeBodyPart bodypart_html  = new MimeBodyPart();
//
//            // ���������� ������..
//            String inHtml = entity.html;
//            if ( entity.inlineList.size() > 0 ) {
//                for (int i=0; i<entity.inlineList.size(); i++ ) {
//                    File f = (File) entity.inlineList.get(i);
//                    String fname = f.getName();
//                    String fpath = f.getPath();
//                    // �޽����߿� ���� ��ü ��ΰ� �ִٸ� ���������� cid: �� �ٲپ��ش�.
//                    inHtml = this.replace(inHtml, fpath, "cid:embedded" + f.hashCode() + "" );
//                }
//            }
//
//            // �⺻ �����̳ʿ� ���� �Է�
//            bodypart_html.setContent(inHtml,"text/html; charset=\"" + entity.charset + "\"" );
//            bodypart_html.setHeader("Content-Transfer-Encoding","base64");
//            bodypart_plain.setContent(entity.plain,"text/plain; charset=\"" + entity.charset + "\"" );
//            multipart_alter.addBodyPart(bodypart_plain);
//            multipart_alter.addBodyPart(bodypart_html);
//
//            // ���������� ������
//            if ( entity.inlineList.size() > 0 ) {
//                multipart_related = new MimeMultipart("related");       // �������Ͽ� �����̳� �ۼ�
//                MimeBodyPart bodypart_related = new MimeBodyPart();
//                bodypart_related.setContent(multipart_alter);           // ���� �����̳ʿ� �⺻ �����̳� ����
//                multipart_related.addBodyPart(bodypart_related);
//                for (int i=0; i<entity.inlineList.size(); i++ ) {        // ���� �����̳ʿ� ���� ����
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
//            // ÷�������� ������
//            if ( entity.attachList.size() > 0 ) {
//                multipart_mixed = new MimeMultipart("mixed");           // ÷������ �����̳� �ۼ�
//                MimeBodyPart bodypart_mixed = new MimeBodyPart();
//                if ( multipart_related != null ) {                      // ���� �����̳ʰ� ������
//                    bodypart_mixed.setContent(multipart_related);       // ÷�� �����̳ʿ� ���� �����̳� ����
//                } else {
//                    bodypart_mixed.setContent(multipart_alter);         // �ƴϸ� �⺻ �����̳� ����
//                }
//                multipart_mixed.addBodyPart(bodypart_mixed);
//                for (int i=0; i<entity.attachList.size(); i++ ) {        // ÷�� �����̳ʿ� ���� ����
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
//            // �޽����� �����̳� ����
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
     * ���� �޽����� �߼��Ѵ�..
     * @param entity ���� �߼� �׸� ����
     *********************************************** */
//    public void send(MailEntity entity)
//                throws MessagingException
//    {
//        Transport transport = null;
//        try {
//            if ( entity.plain == null ) entity.plain = "";
//            if ( entity.html  == null ) entity.html  = "";
//
//            Session session = getSession();                     // ���� ����
//            MimeMessage message = getMessage(session,entity);   // ���� �޽���
//            transport = session.getTransport("smtp");           // ���� Transport
//            if ( this.user != null && this.pwd != null) {
//                transport.connect(this.host, this.user, this.pwd);  // �α��� Ŀ�ؼ�
//            } else {
//                transport.connect();
//            }
//            transport.sendMessage( message, message.getAllRecipients() );   // �޽��� �߼�
//            System.out.print("-----------------");
//        } catch ( MessagingException e1 ) {
//            throw e1;
//        } finally {
//            // Transport ����
//            try { if(transport!=null) transport.close(); } catch(MessagingException egnore) {}
//        }
//    } 

	/***********************************************************************
	 * �׼�ó��.
	 * @param source        String
	 * @param subject       String
	 * @param object        String
	 * @return ��������
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
	 * ���ø� �о� ����
	 * @param source        String
	 * @param subject       String
	 * @param object        String
	 * @return ��������
	 **********************************************************************/
	 
	
	public String generateTemplate(String fileNm){
		StringBuffer contents = new StringBuffer();
		GolfConfig config = GolfConfig.getInstance();
		String tempEmail = config.getEmailTempt()+fileNm;
		File testFile = new File(tempEmail);
		
		return contents.toString();
	}
	
	
	
}