/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2004.08.16 [���뱹(ykcho@e4net.net)]
* ���� : MimeData ����.
* ���� : 
* ���� : 
******************************************************************************/
package com.bccard.golf.common.namo;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;

/** ***************************************************************************
* MimeData ó���� ����.
* @author ���뱹(ykcho@e4net.net)
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
    * MimeData ó���� ����.
    * @param msgKey �޽��� Ű
    ************************************************************************ */
    public MimeDataException(String msgKey) {
        super( new MsgEtt(MsgEtt.TYPE_ERROR,"MimeData",msgKey,null) );
    }

    /** ***********************************************************************
    * MimeData ó���� ����.
    * @param msgKey �޽��� Ű
    * @param t �߻��� ���� ����
    ************************************************************************ */
    public MimeDataException(String msgKey,Throwable t) {
        this(msgKey);
        super.setRootCause(t);
    }
}
