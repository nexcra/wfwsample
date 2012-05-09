/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2004.08.16 [���뱹(ykcho@e4net.net)]
* ���� : Mime ������ ��� CharSet Encoding ó��
* ���� : 
* ���� : 
******************************************************************************/
package com.bccard.golf.common.namo;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.mail.internet.MimeUtility;
import javax.mail.MessagingException;

/** ****************************************************************************
 * Mime ������ ��� CharSet Encoding ó��.
 * @author ���뱹(ykcho@e4net.net)
 * @version 2004.08.16
 **************************************************************************** */
public class Converter {
    /** ***********************************************************************
    * ���� ���ڿ��� ������ CharSet���� ���ڵ�.
    * @param src ���� ���ڿ�
    * @param enc ���� CharSet
    ************************************************************************ */
    static String convertString(String src, String enc) {
        try {
            return convertString(src.getBytes(), enc);
        } catch(Throwable t) {
            return null;
        }
    }
    
    /** ***********************************************************************
    * ���� byte[]�� ������ CharSet���� ���ڵ�.
    * @param src ���� byte[]
    * @param enc ���� CharSet
    ************************************************************************ */
    static String convertString(byte[] src, String enc) {
        String converted = null;
        try {
            converted = new String(src, enc);
        } catch(Throwable t) {
            return null;
        }
        return converted;
    }

    /** ***********************************************************************
    * MIME�� name ������Ƽ�� ���Ǵ� ���ڿ��� �ý��� ���ڵ��� �°� ���ڵ�.
    * @param encoded MIME Encode�� �� ���ڿ�
    ************************************************************************ */
    static String getMIMEEncodedString(String encoded) {
        String charset = null;
        String enctype = null;
        String content = null;
        String converted = null;
        byte [] convertsrc;
        int beginindex = 0;
        int endindex = 0;

        // is MIME Encoded word?
        if(encoded.charAt(0) == '=' && encoded.charAt(1) == '?') {
            beginindex = encoded.indexOf("?", endindex);
            endindex = encoded.indexOf("?", beginindex + 1);
            if(beginindex == -1 || endindex == -1) {
                return null;
            }
            charset = new String(encoded.substring(beginindex + 1, endindex));
            beginindex = endindex;
            endindex = encoded.indexOf("?", beginindex + 1);
            if(beginindex == -1 || endindex == -1) {
                return null;
            }
            enctype = new String(encoded.substring(beginindex + 1, endindex));
            if(enctype.equals("b")) {
                enctype = "base64";
            }
            beginindex = endindex;
            endindex = encoded.indexOf("?", beginindex + 1);
            if(beginindex == -1 || endindex == -1) {
                return null;
            }
            content = new String(encoded.substring(beginindex + 1, endindex));
        }

        if(charset != null && charset.length() > 0) {
            try {
                InputStream is = new ByteArrayInputStream(content.getBytes("iso-8859-1"));
                try {
                    is = MimeUtility.decode(is, enctype);
                } catch(MessagingException moe) {
                    return null;
                }
                convertsrc = new byte[is.available() + 1];
                is.read(convertsrc);
            } catch(IOException ioe) {
                return null;
            }
            converted = convertString(convertsrc, charset).trim();
        }
        return converted;
    }


}