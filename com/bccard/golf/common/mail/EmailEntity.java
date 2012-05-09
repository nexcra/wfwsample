/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : MailEntity.java
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� �߼�
*   �������  : Golf
*   �ۼ�����  : 2009-06-23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.common.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

import com.bccard.golf.common.AppConfig;

 
/** ***************************************************************************
 * ���� �߼� �׸� ����.
 * <pre>
 * </pre>
 * @version   1.0
 * @author    2003 6 18 <A href="mailto:ykcho@e4net.net">yongkookcho</A>
 **************************************************************************** */
public class EmailEntity implements Serializable {
    /** �޽��� ���� ���ڵ�       */  protected String charset;
    /** ������ �̸��� �ּ�.      */  protected InternetAddress from;
    /** �޴� �̸��� �ּ�.        */  protected InternetAddress[] to;
    /** ���� �̸��� �ּ�.        */  protected InternetAddress[] cc;
    /** �������� �̸��� �ּ�.    */  protected InternetAddress[] bcc;
    /** �޽��� ����.             */  protected String subject;
    /** �޽��� �߿䵵.           */  protected int priority;
    /** ����Ȯ�� �ɼ� ���� ����. */  protected boolean notification;
    /** ����Ȯ�� ���� ���� �ּ�. */  protected InternetAddress notificationAddress;
    /** �޽��� ����(Text).       */  protected String plain;
    /** �޽��� ����(Html).       */  protected String html;
    /** ÷������.                */  protected ArrayList attachList;
    /** ��������.                */  protected ArrayList inlineList;

    /** **********************************************
     * ���� �߼� �׸� ���Ǹ� �����Ѵ�..
     * @param charset ���ڿ� ���ڵ� ����
     *********************************************** */
    public EmailEntity(String charset)
                    throws UnsupportedEncodingException
    {
        "".getBytes(charset);
        this.charset = charset;
        this.priority = 3;  // ����
        this.notification = false;
        this.attachList = new ArrayList();
        this.inlineList = new ArrayList();
    }

    /** **********************************************
     * ���� �߼� �׸� ���Ǹ� �����Ѵ�.
     * ���ڿ� ���ڵ��� 8859_1 �� ���õǾ�����.
     *********************************************** */
    public EmailEntity()
    {
        this.charset = "8859_1";
        this.priority = 3;  // ����
        this.notification = false;
        this.attachList = new ArrayList();
        this.inlineList = new ArrayList();
    }

    /** **********************************************
     * ���ڿ� �̸��� �ּҷκ��� InternetAddress Class �Ľ�.
     * @param address �̸����ּ�
     *********************************************** */
    private InternetAddress[] getAddress(String address)
                                  throws AddressException
    {
        InternetAddress[] tos = InternetAddress.parse( this.replace(address,";",",") );
        if ( tos.length > 0 ) {
            for ( int i=0; i<tos.length; i++ ) {
                String to = tos[i].getPersonal();
                try { tos[i].setPersonal(to,this.charset); } catch ( UnsupportedEncodingException e1 ) {}
            }
        }
        return tos;
    }

    /** **********************************************
     * ������ �̸��� �ּҸ� �Է��Ѵ�.
     * @param address �̸����ּ�
     *********************************************** */
    public void setFrom(String address)
                 throws javax.mail.internet.AddressException
    {
        InternetAddress[] froms = getAddress( address );
        if ( froms != null && froms.length > 0 ) {
            this.from = froms[0];
        } else {
            this.from = null;
        }
    }

    /** **********************************************
     * ���� �̸��� �ּҸ� �Է��Ѵ�.
     * �̸��� �ּҰ� �������Ӹ�, ',' ';' �� �и��Ѵ�.
     * @param address �̸����ּ�
     *********************************************** */
    public void setTo(String address)
               throws javax.mail.internet.AddressException
    {
        this.to = getAddress( address );
    }

    /** **********************************************
     * ���� ���� �̸��� �ּҸ� �Է��Ѵ�.
     * �̸��� �ּҰ� �������Ӹ�, ',' ';' �� �и��Ѵ�.
     * @param address �̸����ּ�
     *********************************************** */
    public void setBcc(String address)
                throws javax.mail.internet.AddressException
    {
        this.bcc = getAddress( address );
    }

    /** **********************************************
     * ���� �̸��� �ּҸ� �Է��Ѵ�.
     * �̸��� �ּҰ� �������Ӹ�, ',' ';' �� �и��Ѵ�.
     * @param address �̸����ּ�
     *********************************************** */
    public void setCc(String address)
               throws javax.mail.internet.AddressException
    {
        this.cc = getAddress( address );
    }

    /** **********************************************
     * �޽��� ������ �Է��Ѵ�.
     * @param subject ����
     *********************************************** */
    public void setSubject(String subject)
    {
        try {
            this.subject = MimeUtility.encodeWord( subject );

        } catch( UnsupportedEncodingException e1 ) {

        }
    }

    /** **********************************************
     * �߿䵵�� �Է��Ѵ�.
     * (����)1~5(����)
     * @param level �߿䵵 ���
     *********************************************** */
    public void setPriority(int level)
    {
        if ( level <= 5 && level > 0 ) {
            this.priority = level;
        } else {
            this.priority = 3;
        }
    }

    /** **********************************************
     * ���� Ȯ�� �ɼ��� �����Ѵ�.
     * @param notification ���� Ȯ�� �ɼ� ����
     *********************************************** */
    public void setNotification(boolean notification)
    {
        this.notification = notification;
    }

    /** **********************************************
     * ���� Ȯ�� �޽����� ���� �̸��� �ּҸ� �����Ѵ�.
     * ���� Ȯ�� �ɼǸ� �����ϸ� �⺻������ ������ �ּҷ� �ް� �ȴ�.
     * @param address �̸����ּ�
     *********************************************** */
    public void setNotificationAddress(String address)
                                throws javax.mail.internet.AddressException
    {
        InternetAddress[] noti = getAddress( address );
        if ( noti != null && noti.length > 0 ) {
            this.notificationAddress = noti[0];
            this.notification = true;
        } else {
            this.notificationAddress = null;
        }
    }

    /** **********************************************
     * �Ϲ� Text ���� ������ �Է��Ѵ�..
     * @param plain ����
     *********************************************** */
    public void setPlainContents(String plain) {
        this.plain = plain;
    }

    /** **********************************************
     * Html ���� ������ �Է��Ѵ�..
     * @param html ����
     *********************************************** */
    public void setHtmlContents(String fileNm, String imgPath, String hrefPath, String contData) throws IOException {   	
    	
		String emailContent = AppConfig.getAppProperty("EMAILDIR");
		emailContent = emailContent.replaceAll("\\.\\.","");
		File testFile = new File(emailContent+fileNm);
		StringBuffer contents = new StringBuffer();
		
		
        try {
            BufferedReader input =  new BufferedReader(new FileReader(testFile));
            try {
              String line = null; 

              while (( line = input.readLine()) != null){
           		  contents.append(line);
              }
            } 
            finally {
              input.close();
            }
          } 
          catch (IOException ex){
            
          }    	

          String conTempData = contents.toString();
          conTempData = conTempData.replaceAll("<img src=\"", imgPath);
          conTempData = conTempData.replaceAll("<a href=\"", hrefPath);
          
         if (contData.length() >0) {
     		String[] tempData = contData.split("[|]");
    		int sizeData = tempData.length;
    		if (sizeData > 0){
    			for(int i = 0 ; i < sizeData; i++){ 
    				conTempData = conTempData.replaceAll("[{]EMAIL"+i+"[}]", tempData[i]);
    			}
    		}
         }
    	this.html = conTempData.toString(); 
    }
     

    /** **********************************************
     * ���� ������ �Է��Ѵ�..
     * @param plain ����
     * @param html ����
     *********************************************** */
    public void setContents(String plain, String html)
    {
        this.plain = plain;
        this.html = html;
    }

    /** **********************************************
     * ÷�� ������ �߰��Ѵ�..
     * @param file ÷��������
     *********************************************** */
    public void addAttachFile(File file) {
        if ( file != null && file.isFile() ) {
            this.attachList.add( file );
        }
    }

    /** **********************************************
     * ���� ������ �߰��Ѵ�..
     * @param file ����������
     *********************************************** */
    public void addInlineFile(File file)
    {
        if ( file != null && file.isFile() ) {
            this.inlineList.add( file );
        }
    }
	

	/** �޽��� ���� ���ڵ�       */
    public String getCharset() { return this.charset; }
	/** ������ �̸��� �ּ�.      */
    public InternetAddress getFrom() { return this.from; }
	/** �޴� �̸��� �ּ�.        */
    public InternetAddress[] getTo() { return this.to; }
	/** ���� �̸��� �ּ�.        */ 
    public InternetAddress[] getCc() { return this.cc; }
/** �������� �̸��� �ּ�.    */
    public InternetAddress[] getBcc() { return this.bcc; }
	/** �޽��� ����.             */ 
    public String getSubject() { return this.subject; }
	/** �޽��� �߿䵵.           */
    public int getPriority() { return this.priority; }
	/** ����Ȯ�� �ɼ� ���� ����. */
    public boolean isNotification() { return this.notification; }
	/** ����Ȯ�� ���� ���� �ּ�. */
    public InternetAddress getNotificationAddress() { return this.notificationAddress; }
	/** �޽��� ����(Text).       */ 
    public String getPlain() { return this.plain; }
	/** �޽��� ����(Html).       */
    public String getHtml() { return this.html; }
	/** ÷������.                */
    public ArrayList getAttachList() { return this.attachList; }
	/** ��������.                */
    public ArrayList getInlineList() { return this.inlineList; }



	/***********************************************************************
	 * �׼�ó��.
	 * @param source        String
	 * @param subject       String
	 * @param object        String
	 * @return ��������
	 **********************************************************************/
	public String replace(String source, String subject, String object) {
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

}
