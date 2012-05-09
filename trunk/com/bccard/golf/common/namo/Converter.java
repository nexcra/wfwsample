/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.08.16 [조용국(ykcho@e4net.net)]
* 내용 : Mime 데이터 취급 CharSet Encoding 처리
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.common.namo;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.mail.internet.MimeUtility;
import javax.mail.MessagingException;

/** ****************************************************************************
 * Mime 데이터 취급 CharSet Encoding 처리.
 * @author 조용국(ykcho@e4net.net)
 * @version 2004.08.16
 **************************************************************************** */
public class Converter {
    /** ***********************************************************************
    * 원본 문자열을 지정한 CharSet으로 인코딩.
    * @param src 원본 문자열
    * @param enc 지정 CharSet
    ************************************************************************ */
    static String convertString(String src, String enc) {
        try {
            return convertString(src.getBytes(), enc);
        } catch(Throwable t) {
            return null;
        }
    }
    
    /** ***********************************************************************
    * 원본 byte[]을 지정한 CharSet으로 인코딩.
    * @param src 원본 byte[]
    * @param enc 지정 CharSet
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
    * MIME의 name 프로퍼티에 사용되는 문자열을 시스템 인코딩에 맞게 디코딩.
    * @param encoded MIME Encode가 된 문자열
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