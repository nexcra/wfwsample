/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : MailEntity.java
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 메일 발송
*   적용범위  : Golf
*   작성일자  : 2009-06-23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
 * 메일 발송 항목 정의.
 * <pre>
 * </pre>
 * @version   1.0
 * @author    2003 6 18 <A href="mailto:ykcho@e4net.net">yongkookcho</A>
 **************************************************************************** */
public class EmailEntity implements Serializable {
    /** 메시지 문자 인코딩       */  protected String charset;
    /** 보내는 이메일 주소.      */  protected InternetAddress from;
    /** 받는 이메일 주소.        */  protected InternetAddress[] to;
    /** 참조 이메일 주소.        */  protected InternetAddress[] cc;
    /** 숨은참조 이메일 주소.    */  protected InternetAddress[] bcc;
    /** 메시지 제목.             */  protected String subject;
    /** 메시지 중요도.           */  protected int priority;
    /** 수신확인 옵션 삽입 여부. */  protected boolean notification;
    /** 수신확인 메일 받을 주소. */  protected InternetAddress notificationAddress;
    /** 메시지 내용(Text).       */  protected String plain;
    /** 메시지 내용(Html).       */  protected String html;
    /** 첨부파일.                */  protected ArrayList attachList;
    /** 포함파일.                */  protected ArrayList inlineList;

    /** **********************************************
     * 메일 발송 항목 정의를 생성한다..
     * @param charset 문자열 인코딩 정의
     *********************************************** */
    public EmailEntity(String charset)
                    throws UnsupportedEncodingException
    {
        "".getBytes(charset);
        this.charset = charset;
        this.priority = 3;  // 보통
        this.notification = false;
        this.attachList = new ArrayList();
        this.inlineList = new ArrayList();
    }

    /** **********************************************
     * 메일 발송 항목 정의를 생성한다.
     * 문자열 인코딩은 8859_1 롤 셋팅되어진다.
     *********************************************** */
    public EmailEntity()
    {
        this.charset = "8859_1";
        this.priority = 3;  // 보통
        this.notification = false;
        this.attachList = new ArrayList();
        this.inlineList = new ArrayList();
    }

    /** **********************************************
     * 문자열 이메일 주소로부터 InternetAddress Class 파싱.
     * @param address 이메일주소
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
     * 보내는 이메일 주소를 입력한다.
     * @param address 이메일주소
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
     * 받을 이메일 주소를 입력한다.
     * 이메일 주소가 여러개임면, ',' ';' 로 분리한다.
     * @param address 이메일주소
     *********************************************** */
    public void setTo(String address)
               throws javax.mail.internet.AddressException
    {
        this.to = getAddress( address );
    }

    /** **********************************************
     * 숨은 참조 이메일 주소를 입력한다.
     * 이메일 주소가 여러개임면, ',' ';' 로 분리한다.
     * @param address 이메일주소
     *********************************************** */
    public void setBcc(String address)
                throws javax.mail.internet.AddressException
    {
        this.bcc = getAddress( address );
    }

    /** **********************************************
     * 참조 이메일 주소를 입력한다.
     * 이메일 주소가 여러개임면, ',' ';' 로 분리한다.
     * @param address 이메일주소
     *********************************************** */
    public void setCc(String address)
               throws javax.mail.internet.AddressException
    {
        this.cc = getAddress( address );
    }

    /** **********************************************
     * 메시지 제목을 입력한다.
     * @param subject 제목
     *********************************************** */
    public void setSubject(String subject)
    {
        try {
            this.subject = MimeUtility.encodeWord( subject );

        } catch( UnsupportedEncodingException e1 ) {

        }
    }

    /** **********************************************
     * 중요도를 입력한다.
     * (높음)1~5(낮음)
     * @param level 중요도 등급
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
     * 수신 확인 옵션을 설정한다.
     * @param notification 수신 확인 옵션 설정
     *********************************************** */
    public void setNotification(boolean notification)
    {
        this.notification = notification;
    }

    /** **********************************************
     * 수신 확인 메시지를 받을 이메일 주소를 설정한다.
     * 수신 확인 옵션만 설정하면 기본적으로 보내는 주소로 받게 된다.
     * @param address 이메일주소
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
     * 일반 Text 메일 본문을 입력한다..
     * @param plain 본문
     *********************************************** */
    public void setPlainContents(String plain) {
        this.plain = plain;
    }

    /** **********************************************
     * Html 메일 본문을 입력한다..
     * @param html 본문
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
     * 메일 본문을 입력한다..
     * @param plain 본문
     * @param html 본문
     *********************************************** */
    public void setContents(String plain, String html)
    {
        this.plain = plain;
        this.html = html;
    }

    /** **********************************************
     * 첨부 파일을 추가한다..
     * @param file 첨부할파일
     *********************************************** */
    public void addAttachFile(File file) {
        if ( file != null && file.isFile() ) {
            this.attachList.add( file );
        }
    }

    /** **********************************************
     * 포함 파일을 추가한다..
     * @param file 포함할파일
     *********************************************** */
    public void addInlineFile(File file)
    {
        if ( file != null && file.isFile() ) {
            this.inlineList.add( file );
        }
    }
	

	/** 메시지 문자 인코딩       */
    public String getCharset() { return this.charset; }
	/** 보내는 이메일 주소.      */
    public InternetAddress getFrom() { return this.from; }
	/** 받는 이메일 주소.        */
    public InternetAddress[] getTo() { return this.to; }
	/** 참조 이메일 주소.        */ 
    public InternetAddress[] getCc() { return this.cc; }
/** 숨은참조 이메일 주소.    */
    public InternetAddress[] getBcc() { return this.bcc; }
	/** 메시지 제목.             */ 
    public String getSubject() { return this.subject; }
	/** 메시지 중요도.           */
    public int getPriority() { return this.priority; }
	/** 수신확인 옵션 삽입 여부. */
    public boolean isNotification() { return this.notification; }
	/** 수신확인 메일 받을 주소. */
    public InternetAddress getNotificationAddress() { return this.notificationAddress; }
	/** 메시지 내용(Text).       */ 
    public String getPlain() { return this.plain; }
	/** 메시지 내용(Html).       */
    public String getHtml() { return this.html; }
	/** 첨부파일.                */
    public ArrayList getAttachList() { return this.attachList; }
	/** 포함파일.                */
    public ArrayList getInlineList() { return this.inlineList; }



	/***********************************************************************
	 * 액션처리.
	 * @param source        String
	 * @param subject       String
	 * @param object        String
	 * @return 응답정보
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
