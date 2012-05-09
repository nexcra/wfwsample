/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: SmsSend
*   작 성 자	: (주)만세커뮤니케이션 김태완
*   내    용	: SMS 발송 모듈
*   적용범위	: Golf
*   작성일자	: 2009.06.26
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.HashMap;
import java.util.Random;

import com.bccard.golf.common.AppConfig;
import com.bccard.waf.action.AbstractObject;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class SmsSendProc extends AbstractObject{
	/* */
	public static final String BLANK = " ";

	public SmsSendProc() {
		super();
	}

	/**
    * SMS 메시지 발송
	* @param arg1 16 일련번호(업무구분코드(3)+임의구분문자(13))
	* @param arg2 2 응답코드
	* @param arg3 4	업무구분코드 (631:구매,632:배송)
	* @param arg4 20	전송부서 또는 서비스명
	* @param arg5 20	전송 담당자 
	* @param arg6 20	문자 수신자명
	* @param arg7 12	문자 수신번호 문자만
	* @param arg8 12	회신번호
	* @param arg9 9	(선택)
	* @param arg10 1 (선택)
	* @param arg11 13 (선택)
	* @param arg12 100 전송내용(80byte이내)
	* @param arg13 100 참조안함
	*/
	public String send(String smsClss, HashMap smsInfo,  String message ) throws Exception {

		String host = AppConfig.getAppProperty("SMS.HOST");
		int port = Integer.parseInt(AppConfig.getAppProperty("SMS.PORT"));
		
		byte[] snd_dat = new byte[329];
		Random rand = new Random();
		String mobNo = (String)smsInfo.get("sPhone1")+(String)smsInfo.get("sPhone2")+(String)smsInfo.get("sPhone3");
		String hgNm  = (String)smsInfo.get("sName");
		String arg1 = smsClss+String.valueOf( rand.nextInt(99999999))+String.valueOf( rand.nextInt(99999));
		String arg2 = "";
		String arg4 = "플랫폼개발팀";
		String arg5 = "김승환 과장님";
		String arg8 = (String)smsInfo.get("sCallCenter"); 
		if (arg8==null || "".equals(arg8) || "null".equals(arg8) ) arg8 = "15666578";
		String arg9 = "";
		String arg10= "";
		String arg11= "";
		String arg13= "";
		System.arraycopy(setStrToByteLen(arg1    , 16).getBytes(), 0, snd_dat,   1, 16);  // arg1[16]
		System.arraycopy(setStrToByteLen(arg2    , 2).getBytes(), 0, snd_dat,  16,  2);  // arg2[2]
		System.arraycopy(setStrToByteLen(smsClss , 4).getBytes(), 0, snd_dat,  18,  4);  // arg3[4]
		System.arraycopy(setStrToByteLen(arg4    , 20).getBytes(), 0, snd_dat,  22, 20);  // arg4[20]
		System.arraycopy(setStrToByteLen(arg5    , 20).getBytes(), 0, snd_dat,  42, 20);  // arg5[20]
		System.arraycopy(setStrToByteLen(hgNm    , 20).getBytes(), 0, snd_dat,  62, 20);  // arg6[20]
		System.arraycopy(setStrToByteLen(mobNo   , 12).getBytes(), 0, snd_dat,  82, 12);  // arg7[12]
		System.arraycopy(setStrToByteLen(arg8    , 12).getBytes(), 0, snd_dat,  94, 12);  // arg8[12]
		System.arraycopy(setStrToByteLen(arg9    ,  9).getBytes(), 0, snd_dat, 106,  9);  // arg9[9]
		System.arraycopy(setStrToByteLen(arg10   ,  1).getBytes(), 0, snd_dat, 115,  1);  // arg10[1]
		System.arraycopy(setStrToByteLen(arg11   , 13).getBytes(), 0, snd_dat, 116, 13);  // arg11[13]
		System.arraycopy(setStrToByteLen(message ,100).getBytes(), 0, snd_dat, 129,100);  // arg12[100]
		System.arraycopy(setStrToByteLen(arg13   ,100).getBytes(), 0, snd_dat, 229,100);  // arg13[100]
		
		byte[] rcv_dat = sendSocket(host,port,snd_dat);
		debug("SMS PROC 결과"+getByteSubStr(rcv_dat, 16, 2));
		return getByteSubStr(rcv_dat, 16, 2);
		
	}
	private byte[] sendSocket(String to_host, int to_port, byte[] data) throws Exception {
		byte[] result = null;
		Socket socket = null;
		BufferedOutputStream bos = null;
		InputStream input = null;
		try {
			
			socket = new Socket(to_host, to_port);
			if ( socket.getSoTimeout() == 0 ) socket.setSoTimeout(3000); // socket timeout 5분
			bos = new BufferedOutputStream(socket.getOutputStream());
			input = socket.getInputStream();
			bos.write(data);
			bos.flush();
			socket.shutdownOutput();
			StringBuffer response = new StringBuffer();
			int j;
			while((j = input.read()) != -1) {
				response.append((char)j);
			}
			result = response.toString().getBytes();
			debug(" 소켓통신결과 : "+result.toString());
			
		} catch(Throwable t) {
			result = null;
			throw new Exception(t.getMessage());
		} finally {
			try { if(bos != null) bos.close(); }		catch(Exception e) { }
			try	{ if(input != null) input.close(); }	catch(Exception e) { }
			try { if(socket != null) socket.close(); }	catch(Exception e) { }
		}
		return result;
	}

	/** ***********************************************************************
	* 문자열의 바이트 크기로 변경.
	* @param str		원본 문자열.
	* @param length		BYTE 길이.
	************************************************************************ */
	private String setStrToByteLen(String str, int length) {
		if ( str == null )	str = "";
		StringBuffer strBuf = new StringBuffer();
		int size = str.getBytes().length;
		if(size < length) {
			strBuf.append(str);
			for(int i = 0; i < (length - size)/SmsSendProc.BLANK.getBytes().length; i++)
				strBuf.append(SmsSendProc.BLANK);
			
			debug("값:"+strBuf.toString());
			return strBuf.toString();
		} else if ( size > length ) {
			debug("값:"+getByteSubStr(str.getBytes(), 0, length));
			return getByteSubStr(str.getBytes(), 0, length);
		} else {
			debug("값:"+str);
			return str;
		}
		
	}

	/** ***********************************************************************
	* 해당 바이트에 대한 문자열 반환
	* @param byte[] src
	* @param int start_pos
	* @param int length
	* @param sms_msg_kind	SMS 메시지 종류.
	************************************************************************ */
	private String getByteSubStr(byte[] src, int start_pos, int length) {
		byte[] o = new byte[length];
		System.arraycopy(src, start_pos, o, 0, length);
		return new String(o).trim();
	}	


}