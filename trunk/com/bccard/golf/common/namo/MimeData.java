/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2004.08.16 [���뱹(ykcho@e4net.net)]
* ���� : Namo ActiveSqaure 4.0 �̻󿡼� �����Ǵ� MIMEValue�� ���ڵ��ϴ� Ŭ����
* ���� : 
* ���� : 
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
 * Namo ActiveSqaure 4.0 �̻󿡼� �����Ǵ� MIMEValue�� ���ڵ��ϴ� Ŭ����.
 * ���ڵ� �����̸� ���ڵ��� �������� �ʴ´�. ���� �Ϲ����� MIME ���ڵ�
 * �����Ϳ� ȣȯ�� ���� �������� �ִ�.
 * @author ���뱹(ykcho@e4net.net)
 * @version 2004.08.16
 *****************************************************************************/
public class MimeData extends AbstractObject {
    /** multipart ����     */ private boolean multipart;
    /** multipart �ٿ���� */ private String boundary;
    /** ���ڵ��� ��Ʈ��    */ private ArrayList decodePart;
    /** save Path          */ private String savePath;
    /** save URL           */ private String saveURL;

    /** ***********************************************************************
    * Namo ActiveSqaure 4.0 �̻󿡼� �����Ǵ� MIMEValue�� ���ڵ��ϴ� Ŭ����.
    ************************************************************************ */
    public MimeData() {
        this.multipart = false;
        this.boundary = null;
    }

    /** ***********************************************************************
    * ÷�� ������ ������ ��ġ�� ����.
    * @param path ���� Path
    ************************************************************************ */
    public void setSavePath(String path) { this.savePath = path; }

    /** ***********************************************************************
    * ÷�� ������ �＼���� �� �ִ� URL ����.
    * @param url �＼�� URL
    ************************************************************************ */
    public void setSaveURL(String url) { this.saveURL = url; }


    /** ***********************************************************************
    * MIME �������� ������ �˻��Ѵ�.
    * @param encodedString ���ڵ��� MIME ������
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
    * �ϳ��� MIME Part�� ���, ���� ���� �и��Ѵ�.
    * @param encodedString ���ڵ��� MIME ������
    ************************************************************************ */
    protected void splitSinglePart(String encodedString) throws IOException {

        BufferedReader br = new BufferedReader(new StringReader(encodedString));    // ���پ� �б� ���ؼ� ��Ʈ���� �Ҵ�
        String line = null; // ������
        String compare = null;
        String body = new String();
        MimePart part = new MimePart();
        int nowState = MIME.bEGIN;

        while(((line = br.readLine()) != null) && (nowState != MIME.eND)) { // ���پ� �о� ���δ�
            line = line.trim();
            if(line.length() == 0 && nowState == MIME.bEGIN) {  // ���ۺκп� �ִ� ���� ����
                continue;
            } else {
                if(nowState == MIME.bEGIN) {    // ���� ��Ȳ�� �����̾����� ������·�
                    nowState = MIME.hEADER;
                }
                compare = line.toLowerCase();   // ��� �ҹ��ڷ� ����
                if(nowState == MIME.hEADER) {
                    if(compare.indexOf("mime-version") != -1) {  // MIME Version�� �������� �ʴ´�
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
                    } else if(compare.length() == 0) { // ��� ���¿��� ������ ���� �������·� ��ȯ ^__^
                        nowState = MIME.bODY;
                    }
                } else if(nowState == MIME.bODY) {  // MIME ����
                    if(line.length() == 0 && multipart) {  // ���� ��
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
        part.setBodypart(body); // MIME ���� ����
        decodePart.add(part);
    }

    /** ***********************************************************************
    * MultiPart �� ��� ������.
    * @param encodedString ���ڵ��� MIME ������
    ************************************************************************ */
    protected void splitMultiPart(String encodedString) throws IOException {
        String part = null;
        int start = 0;
        int pos = 0;
        
        while(true) {
            pos = encodedString.indexOf("--" + boundary, start); // �ٿ������ ã�´�
            if(pos == -1) {  // ���� �ٿ������ ���� ��� �н�
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
    * MIME �� part ���� ������, ������ �м�.
    * @param encodedString ���ڵ��� MIME ������
    ************************************************************************ */
    protected void splitMimePart(String encodedString) throws IOException {
        if(this.multipart) {  // ��Ƽ ��Ʈ�ΰ��
            splitMultiPart(encodedString);
        } else {              // �̱� ��Ʈ�ΰ��
            splitSinglePart(encodedString);
        }
    }

    /** ***********************************************************************
    * MIME ���ڵ�.
    * @param encodedString ���ڵ��� MIME ������
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
    * ���� ���ڿ����� Ư�� ���ڿ��� ã�Ƽ� �� ���ڿ��� ġȯ�Ѵ�.
    * @param encodedString ���ڵ��� MIME ������
    ************************************************************************ */
    public String replace(String original, String oldstr, String newstr) {
        return StrUtil.replace(original, oldstr, newstr);
    }

    /** ***********************************************************************
    * ÷�ε� ������ ���� ��� CID ��ũ ������ ������ ���� �̸����� ����.
    * @param content ����
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
    * HTML���� �Ǵ� TEXT �޽��� ������ ��ȯ�Ѵ�.
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

        if(part.getEncoding() == null) {  // ���ڵ� Ÿ���� �����Ƿ� ���ڵ� ���� ����
            return part.getBodypart();
        }

        try {
            // String �� ����Ʈ�� �ٲ㼭 �Է� ��Ʈ������ �ٲ۴�. �̶� ���ڵ� Ÿ���� iso-8859-1�� �����Ѵ�
            InputStream is = new ByteArrayInputStream(part.getBodypart().getBytes("iso-8859-1")); 
            try {
                is = MimeUtility.decode(is, part.getEncoding());  // ���ڵ�
            } catch(MessagingException me) {
                throw new MimeDataException( MimeDataException.CANNOT_DECODE );
            }
            decodeByte = new byte[is.available() + 1];  // ����Ʈ�迭�� �о� ���δ�
            is.read(decodeByte);
        } catch(IOException ioe) {
            throw new MimeDataException( MimeDataException.CANNOT_CREATE_INPUTSTREAM );
        }
        decodeText = new String(decodeByte); // ��Ʈ������ �ٲ۴�
        if (multipart) {
            decodeText = changeCIDPath(decodeText);
        }
        return decodeText;
    }

    /** ***********************************************************************
    * MIME�� ÷�ε� ������ �����Ѵ�. �ݵ�� decode()�� ���� �����߾�� �Ѵ�..
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

            // Mime file �� �����´�
            try  {
                // ���丮�� ������ ���� ����
                File pDir = new File(this.savePath);
                if ( ! pDir.exists() ) {
                    pDir.mkdirs();
                }

                is = new ByteArrayInputStream(part.getBodypart().getBytes("iso-8859-1"));  // String�� byte array�� �ٲ۴�
                is = MimeUtility.decode(is, part.getEncoding());  // MIME Decoding�� �Ѵ�
                fileContent = new byte [is.available() + 1];      // byte �迭�� ���ڵ� ������ �����´�

                is.read(fileContent);
                fileName = getWritableFileName(part.getName());
                part.setName(fileName);
                outFile = new File(this.savePath + File.separator + fileName); // ������ ���� �����Ѵ�
                os = new FileOutputStream(outFile);  // ���Ͽ� �����Ѵ�
                os.write(fileContent);
                os.close(); // ��Ʈ���� �ݴ´�
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
    * ������ ������ ���� �̸��� ���Ѵ�.
    * @param fileName ���ϸ� 
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
    * �Ľ̿� ���Ǵ� ���°���.
    ************************************************************************ */
    final static class MIME {
        public static int bEGIN = 0;
        public static int hEADER = 1;
        public static int bODY = 2;
        public static int eND = 3;
    };

    /** ***********************************************************************
    * MIME Data�� �� ��Ʈ.
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
