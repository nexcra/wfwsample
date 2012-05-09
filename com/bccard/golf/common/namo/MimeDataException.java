/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.08.16 [조용국(ykcho@e4net.net)]
* 내용 : MimeData 예외.
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.common.namo;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;

/** ***************************************************************************
* MimeData 처리시 예외.
* @author 조용국(ykcho@e4net.net)
* @version 2004.08.16
**************************************************************************** */
public class MimeDataException extends GolfException {
    /** NAMO_MIME_DATA_NOT_FOUND_MIME_HEADER     */ 
    public static final String NOT_FOUND_MIME_HEADER     = "NAMO_MIME_DATA_NOT_FOUND_MIME_HEADER";
    /** NAMO_MIME_DATA_NOT_FOUND_CONTENT_TYPE    */ 
    public static final String NOT_FOUND_CONTENT_TYPE    = "NAMO_MIME_DATA_NOT_FOUND_CONTENT_TYPE";
    /** NAMO_MIME_DATA_NOT_FOUND_BOUNDARY        */ 
    public static final String NOT_FOUND_BOUNDARY        = "NAMO_MIME_DATA_NOT_FOUND_BOUNDARY";
    /** NAMO_MIME_DATA_CANNOT_DECODE             */ 
    public static final String CANNOT_DECODE             = "NAMO_MIME_DATA_CANNOT_DECODE";
    /** NAMO_MIME_DATA_CANNOT_CREATE_INPUTSTREAM */ 
    public static final String CANNOT_CREATE_INPUTSTREAM = "NAMO_MIME_DATA_CANNOT_CREATE_INPUTSTREAM";
    /** NAMO_MIME_DATA_CANNOT_CREATE_FILE       */ 
    public static final String CANNOT_CREATE_FILE        = "NAMO_MIME_DATA_CANNOT_CREATE_FILE";
    /** NAMO_MIME_DATA_CANNOT_WRITE_FILE       */ 
    public static final String CANNOT_WRITE_FILE         = "NAMO_MIME_DATA_CANNOT_WRITE_FILE";
    /** NAMO_MIME_DATA_CANNOT_DECODE_FILE      */ 
    public static final String CANNOT_DECODE_FILE        = "NAMO_MIME_DATA_CANNOT_DECODE_FILE";

    /** NAMO_MIME_DATA_FTP_FAIL      */ 
    public static final String FTP_FAIL                  = "NAMO_MIME_DATA_FTP_FAIL";
    /** NAMO_MIME_DATA_FTP_CONNECT_FAIL      */ 
    public static final String FTP_CONNECT_FAIL          = "NAMO_MIME_DATA_FTP_CONNECT_FAIL";
    /** NAMO_MIME_DATA_FTP_LOGIN_FAIL      */ 
    public static final String FTP_LOGIN_FAIL            = "NAMO_MIME_DATA_FTP_LOGIN_FAIL";
    /** NAMO_MIME_DATA_FTP_NOT_FOUND_DIR  */ 
    public static final String FTP_NOT_FOUND_DIR         = "NAMO_MIME_DATA_FTP_NOT_FOUND_DIR";
    /** NAMO_MIME_DATA_FTP_MKDIR_FAIL  */ 
    public static final String FTP_MKDIR_FAIL            = "NAMO_MIME_DATA_FTP_MKDIR_FAIL";
    /** NAMO_MIME_DATA_FTP_PUT_FAIL  */ 
    public static final String FTP_PUT_FAIL              = "NAMO_MIME_DATA_FTP_PUT_FAIL";


    /** ***********************************************************************
    * MimeData 처리시 예외.
    * @param msgKey 메시지 키
    ************************************************************************ */
    public MimeDataException(String msgKey) {
        super( new MsgEtt(MsgEtt.TYPE_ERROR,"MimeData",msgKey,null) );
    }

    /** ***********************************************************************
    * MimeData 처리시 예외.
    * @param msgKey 메시지 키
    * @param t 발생한 원본 예외
    ************************************************************************ */
    public MimeDataException(String msgKey,Throwable t) {
        this(msgKey);
        super.setRootCause(t);
    }
}
