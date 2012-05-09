/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.08.16 [조용국(ykcho@e4net.net)]
* 내용 : Namo ActiveSqaure 4.0 이상에서 생성되는 MIMEValue를 디코딩하는 클래스
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.common.namo;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import javax.mail.internet.MimeUtility;
import javax.mail.MessagingException;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractObject;

import com.bccard.golf.common.namo.Converter;
import com.bccard.golf.common.namo.MimeDataException;

/******************************************************************************
 * Namo ActiveSqaure 4.0 이상에서 생성되는 MIMEValue를 디코딩하는 클래스.
 * 디코딩 전용이며 인코딩은 지원하지 않는다. 또한 일반적인 MIME 인코딩
 * 데이터와 호환이 되지 않을수도 있다.
 * @author 조용국(ykcho@e4net.net)
 * @version 2004.08.16
 *****************************************************************************/
public class MimeData extends AbstractObject {
    /** multipart 여부     */ private boolean multipart;
    /** multipart 바운더리 */ private String boundary;
    /** 디코드할 파트들    */ private ArrayList decodePart;
    /** save Path          */ private String savePath;
    /** save URL           */ private String saveURL;

    /** ***********************************************************************
    * Namo ActiveSqaure 4.0 이상에서 생성되는 MIMEValue를 디코딩하는 클래스.
    ************************************************************************ */
    public MimeData() {
        this.multipart = false;
        this.boundary = null;
    }

    /** ***********************************************************************
    * 첨부 파일을 저장할 위치를 지정.
    * @param path 저장 Path
    ************************************************************************ */
    public void setSavePath(String path) { this.savePath = path; }

    /** ***********************************************************************
    * 첨부 파일을 억세스할 수 있는 URL 지정.
    * @param url 억세스 URL
    ************************************************************************ */
    public void setSaveURL(String url) { this.saveURL = url; }


    /** ***********************************************************************
    * MIME 데이터의 형식을 검사한다.
    * @param encodedString 인코딩된 MIME 데이터
    ************************************************************************ */
    protected void checkMimeType(String encodedString) throws MimeDataException, IOException {
        String line, data;
        int pos = 0;
        BufferedReader br = new BufferedReader(new StringReader(encodedString));
        data = null;

        while(true) {
            line = br.readLine();
            line.trim();
            if(line == null || line.length() <= 0)
                break;
            data += line;
        }
        if(data.length() <= 0) {
            throw new MimeDataException(MimeDataException.NOT_FOUND_MIME_HEADER);
        }
        line = data.toLowerCase();

        // find content-type
        pos = line.indexOf("content-type"); 
        if(pos == -1) {
            throw new MimeDataException(MimeDataException.NOT_FOUND_CONTENT_TYPE);
        }

        // find multipart directive
        pos = line.indexOf("multipart", pos + 1); 
        if(pos != -1) {
            this.multipart = true;
        } else {
            this.multipart = false;
        }

        // find boundary
        pos = line.indexOf("boundary", pos + 1);
        if(pos == -1 && this.multipart == true) {
            throw new MimeDataException(MimeDataException.NOT_FOUND_BOUNDARY);
        }

        // find boundary data
        pos = data.indexOf("\"", pos + 1);
        if(pos == -1 && this.multipart == true) {
            throw new MimeDataException(MimeDataException.NOT_FOUND_BOUNDARY);
        }

        if(this.multipart == true) {
            this.boundary = data.substring(pos + 1, data.indexOf("\"", pos + 1));
        }
    }

    /** ***********************************************************************
    * 하나의 MIME Part를 헤더, 본문 으로 분리한다.
    * @param encodedString 인코딩된 MIME 데이터
    ************************************************************************ */
    protected void splitSinglePart(String encodedString) throws IOException {

        BufferedReader br = new BufferedReader(new StringReader(encodedString));    // 한줄씩 읽기 위해서 스트림을 할당
        String line = null; // 현재줄
        String compare = null;
        String body = new String();
        MimePart part = new MimePart();
        int nowState = MIME.bEGIN;

        while(((line = br.readLine()) != null) && (nowState != MIME.eND)) { // 한줄씩 읽어 들인다
            line = line.trim();
            if(line.length() == 0 && nowState == MIME.bEGIN) {  // 시작부분에 있는 공백 무시
                continue;
            } else {
                if(nowState == MIME.bEGIN) {    // 이전 상황에 시작이었으면 헤더상태로
                    nowState = MIME.hEADER;
                }
                compare = line.toLowerCase();   // 모두 소문자로 변경
                if(nowState == MIME.hEADER) {
                    if(compare.indexOf("mime-version") != -1) {  // MIME Version은 저장하지 않는다
                        continue;
                    } else if(compare.indexOf("content-type") != -1) { // content type
                        part.setContentType(compare.substring(compare.indexOf(":") + 1).trim());
                    } else if(compare.indexOf("content-transfer-encoding") != -1) { // encoding type
                        part.setEncoding(line.substring(line.indexOf(":") + 1).trim());
                    } else if(compare.indexOf("content-id") != -1) {  // CID
                        compare = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
                        part.setContentID(compare);
                    } else if(compare.indexOf("name") != -1) {  // File name
                        compare = line.substring(line.indexOf("\"") + 1, line.length() - 1);
                        part.setName(compare);
                    } else if(compare.length() == 0) { // 헤더 상태에서 공백이 오면 본문상태로 전환 ^__^
                        nowState = MIME.bODY;
                    }
                } else if(nowState == MIME.bODY) {  // MIME 본문
                    if(line.length() == 0 && multipart) {  // 본문 끝
                        nowState = MIME.eND;
                    } else {
                        body = body + line;
                        break;
                    }
                }
            }
        }
        if(nowState == MIME.bODY) {
            if(line != null) {
                body = encodedString.substring(encodedString.indexOf(line));
            }
        }
        part.setBodypart(body); // MIME 본문 셋팅
        decodePart.add(part);
    }

    /** ***********************************************************************
    * MultiPart 인 경우 나눈다.
    * @param encodedString 인코딩된 MIME 데이터
    ************************************************************************ */
    protected void splitMultiPart(String encodedString) throws IOException {
        String part = null;
        int start = 0;
        int pos = 0;
        
        while(true) {
            pos = encodedString.indexOf("--" + boundary, start); // 바운더리를 찾는다
            if(pos == -1) {  // 다음 바운더리가 없는 경우 패스
                break;
            }
            start = encodedString.indexOf("--" + boundary, pos + boundary.length() + 2);
            if(start == -1) {
                break;
            }
            part = encodedString.substring(pos + boundary.length() + 2, start);
            splitSinglePart(part);
        }
    }

    /** ***********************************************************************
    * MIME 을 part 별로 나누고, 정보를 분석.
    * @param encodedString 인코딩된 MIME 데이터
    ************************************************************************ */
    protected void splitMimePart(String encodedString) throws IOException {
        if(this.multipart) {  // 멀티 파트인경우
            splitMultiPart(encodedString);
        } else {              // 싱글 파트인경우
            splitSinglePart(encodedString);
        }
    }

    /** ***********************************************************************
    * MIME 디코딩.
    * @param encodedString 인코딩된 MIME 데이터
    ************************************************************************ */
    public boolean decode(String encodedString) throws MimeDataException, IOException {
        int i = 0;
        MimePart part;
        decodePart = null;
        decodePart = new ArrayList();
        checkMimeType(encodedString);
        splitMimePart(encodedString);
        return true;
    }

    /** ***********************************************************************
    * 원본 문자열에서 특정 문자열을 찾아서 새 문자열로 치환한다.
    * @param encodedString 인코딩된 MIME 데이터
    ************************************************************************ */
    public String replace(String original, String oldstr, String newstr) {
        return StrUtil.replace(original, oldstr, newstr);
    }

    /** ***********************************************************************
    * 첨부된 파일이 있을 경우 CID 링크 내용을 저장한 파일 이름으로 변경.
    * @param content 내용
    ************************************************************************ */
    protected String changeCIDPath(String content) {
        int i = 0;
        String convert;
        MimePart part;
        convert = content;

        for( i = 1; i < decodePart.size(); i++) {
            part = (MimePart)decodePart.get(i);
            if(part.getContentID() != null) {
                if(saveURL == null && saveURL.length() <= 0) {
                    convert = replace(convert, "cid:" + part.getContentID(), part.getName());
                } else {
                    convert = replace(convert, "cid:" + part.getContentID(), saveURL + "/" + part.getName());
                }
            }
        }
        return convert;
    }

    /** ***********************************************************************
    * HTML본문 또는 TEXT 메시지 내용을 반환한다.
    ************************************************************************ */
    public String getBodyContent() throws MimeDataException {
        MimePart part;
        byte [] decodeByte;
        String decodeText = null;

        if(decodePart.size() <= 0) {
            return null;
        }

        part = (MimePart)decodePart.get(0);
        decodeText = part.getBodypart();

        if(part.getEncoding() == null) {  // 인코딩 타입이 없으므로 인코딩 하지 말것
            return part.getBodypart();
        }

        try {
            // String 을 바이트로 바꿔서 입력 스트림으로 바꾼다. 이때 인코딩 타입을 iso-8859-1로 셋팅한다
            InputStream is = new ByteArrayInputStream(part.getBodypart().getBytes("iso-8859-1")); 
            try {
                is = MimeUtility.decode(is, part.getEncoding());  // 디코딩
            } catch(MessagingException me) {
                throw new MimeDataException( MimeDataException.CANNOT_DECODE );
            }
            decodeByte = new byte[is.available() + 1];  // 바이트배열로 읽어 들인다
            is.read(decodeByte);
        } catch(IOException ioe) {
            throw new MimeDataException( MimeDataException.CANNOT_CREATE_INPUTSTREAM );
        }
        decodeText = new String(decodeByte); // 스트링으로 바꾼다
        if (multipart) {
            decodeText = changeCIDPath(decodeText);
        }
        return decodeText;
    }

    /** ***********************************************************************
    * MIME에 첨부된 파일을 저장한다. 반드시 decode()를 먼저 실행했어야 한다..
    ************************************************************************ */
    public void saveFile() throws MimeDataException {
        File outFile;
        OutputStream os;
        InputStream is;
        MimePart part;
        String fileName;
        byte [] fileContent;
        int i;

        for(i = 1; i < decodePart.size(); i++) {
            outFile = null;
            os = null;
            is = null;
            fileContent = null;

            part = (MimePart)decodePart.get(i);

            // Mime file 을 가져온다
            try  {
                // 디렉토리가 없으면 새로 생성
                File pDir = new File(this.savePath);
                if ( ! pDir.exists() ) {
                    pDir.mkdirs();
                }

                is = new ByteArrayInputStream(part.getBodypart().getBytes("iso-8859-1"));  // String을 byte array로 바꾼다
                is = MimeUtility.decode(is, part.getEncoding());  // MIME Decoding을 한다
                fileContent = new byte [is.available() + 1];      // byte 배열로 디코딩 내용을 가져온다

                is.read(fileContent);
                fileName = getWritableFileName(part.getName());
                part.setName(fileName);
                outFile = new File(this.savePath + File.separator + fileName); // 파일을 새로 생성한다
                os = new FileOutputStream(outFile);  // 파일에 저장한다
                os.write(fileContent);
                os.close(); // 스트림을 닫는다
            } catch(FileNotFoundException fnfe) {
                throw new MimeDataException(MimeDataException.CANNOT_CREATE_FILE);
            } catch(IOException ioe) {
                throw new MimeDataException(MimeDataException.CANNOT_WRITE_FILE);
            } catch(MessagingException me) {
                throw new MimeDataException(MimeDataException.CANNOT_DECODE_FILE);
            }
        }
    }

    /** ***********************************************************************
    * 저장이 가능한 파일 이름을 정한다.
    * @param fileName 파일명 
    ************************************************************************ */
    public String getWritableFileName(String fileName) {
        String writableName = fileName;
        String name = null;
        String ext = null;
        File writeFile = new File(this.savePath + File.separator + writableName);
        int i = 0;

        name = fileName.substring(0, fileName.lastIndexOf('.'));
        ext = fileName.substring(fileName.lastIndexOf('.'));

        while(writeFile.exists() == true) {
            writableName = name + "[" + Integer.toString(i) + "]" + ext;
            writeFile = null;
            writeFile = new File(this.savePath + File.separator + writableName);
            i++;
        }
        return writableName;
    }

    /** ***********************************************************************
    * 파싱에 사용되는 상태값들.
    ************************************************************************ */
    final static class MIME {
        public static int bEGIN = 0;
        public static int hEADER = 1;
        public static int bODY = 2;
        public static int eND = 3;
    };

    /** ***********************************************************************
    * MIME Data의 한 파트.
    ************************************************************************ */
    class MimePart {
        private String bodypart;
        private String contentType;
        private String contentID;
        private String encoding;
        private String name;

        public void setName(String name) {
            String convstr = Converter.getMIMEEncodedString(name);
            if (convstr != null) {
                this.name = convstr;
            } else {
                this.name = name;
            }
        }
        public String getName() { return name; }

        public void setBodypart(String part) { bodypart = part; }
        public String getBodypart() { return bodypart; }

        public void setContentType(String contentType) { this.contentType = contentType; }
        public String getContentType() { return contentType; }

        public void setContentID(String contentID) { this.contentID = contentID; }
        public String getContentID() { return contentID; }

        public void setEncoding(String encoding) { this.encoding = encoding; }
        public String getEncoding() { return encoding; }
    }








}
