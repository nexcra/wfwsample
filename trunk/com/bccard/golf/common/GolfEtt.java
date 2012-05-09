/***************************************************************************************
*  클래스명        :   EtaxEtt
*  작 성 자        :   csj007
*  내    용        :   통합 지방세 수납 엔티티 원형
*  적용범위        :   etax
*  작성일자        :   2008.06.27
************************** 수정이력 ***************************************************
* 일자            수정자         변경사항 
****************************************************************************************/
package com.bccard.golf.common;

/**
 * 통합 지방세 수납 엔티티 원형. 
 * @author csj007
 * @version 20070912
 */
public abstract class GolfEtt implements java.io.Serializable {
	/** 거래일자 */
	protected String tradeDate;
	/** 거래시간 */
	protected String tradeTime;
	/** 처리결과코드 */
	protected String tradeResult;
	/** 거래구분코드 */
	protected String tradeGubun;
	/** 이체은행코드 */
	protected String bankCd;
	/** FILLER */
	protected String filler;
	
	/**
	 * Constructor.
	 */
	protected GolfEtt() {
		init();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBankCd() {
		return bankCd;
	}

	/**
	 * 
	 * @param bankCd
	 */
	public void setBankCd(String bankCd) {
		this.bankCd = bankCd;
	}

	/**
	 * 
	 * @return
	 */
	public String getFiller() {
		return filler;
	}

	/**
	 * 
	 * @param filler
	 */
	public void setFiller(String filler) {
		this.filler = filler;
	}

	/**
	 * 
	 * @return
	 */
	public String getTradeDate() {
		return tradeDate;
	}

	/**
	 * 
	 * @param tradeDate
	 */
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	/**
	 * 
	 * @return
	 */
	public String getTradeGubun() {
		return tradeGubun;
	}

	/**
	 * 
	 * @param tradeGubun
	 */
	public void setTradeGubun(String tradeGubun) {
		this.tradeGubun = tradeGubun;
	}

	/**
	 * 
	 * @return
	 */
	public String getTradeResult() {
		return tradeResult;
	}

	/**
	 * 
	 * @param tradeResult
	 */
	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}

	/**
	 * 
	 * @return
	 */
	public String getTradeTime() {
		return tradeTime;
	}

	/**
	 * 
	 * @param tradeTime
	 */
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	/**
	 * 변수 초기화.
	 */
	protected void init() {
		this.tradeDate = null;
		this.tradeTime = null;
		this.tradeResult = null;
		this.tradeGubun = null;
		this.bankCd = null;
		this.filler = null;
	}
	
	/**
	 * 송수신데이터 파싱.
	 * @param raw 송수신데이터
	 * @return 파싱 성공 여부.
	 */
	public boolean parse(byte[] raw) {
		init();
		if ( raw != null && raw.length >= 28 ) {
			this.tradeDate  = new String(raw, 0, 8);
			this.tradeTime  = new String(raw, 8, 6);
			this.tradeResult= new String(raw,14, 4);
			this.tradeGubun = new String(raw,18, 4);
			this.bankCd     = new String(raw,22, 2);
			this.filler     = new String(raw,24, 4);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 송수신데이터형으로 변환. 
	 * @return 송수신데이터
	 */
	public byte[] getBytes() {
		byte[] tmpraw = new byte[28];
		System.arraycopy(GolfEtt.rpad(tradeDate  , 8,' ').getBytes(),0,tmpraw, 0, 8);
		System.arraycopy(GolfEtt.rpad(tradeTime  , 6,' ').getBytes(),0,tmpraw, 8, 6);
		System.arraycopy(GolfEtt.rpad(tradeResult, 4,'0').getBytes(),0,tmpraw,14, 4);
		System.arraycopy(GolfEtt.rpad(tradeGubun , 4,' ').getBytes(),0,tmpraw,18, 4);
		System.arraycopy(GolfEtt.rpad(bankCd     , 2,'0').getBytes(),0,tmpraw,22, 2);
		System.arraycopy(GolfEtt.rpad(filler     , 4,'0').getBytes(),0,tmpraw,24, 4);
		return tmpraw;
	}
	
	/**
	 * 문자열로 변환.
	 */
	public String toString() {
		return new String(getBytes());
	}

	/**
	 * pad 문자열 반환.
	 * @param source 소스문자열
 	 * @param length 길이
  	 * @param pad 덧붙일 글자
	 * @return pad 문자열
	 */
	private static String pad(String source, int length, char pad) {
		if ( source == null ) source = "";
		
		int len = length-source.length();
		char[] arr = new char[len];

		for(int i=0; i<len; i++) {
			arr[i] = pad;
		}
		return new String(arr);
	}
	
	/**
	 * lpad 문자열 반환.
	 * @param source 소스문자열
 	 * @param length 길이
  	 * @param pad 덧붙일 글자
	 * @return pad 문자열
	 */
	public static String lpad(String source, int length, char padChar) {
		if ( source == null ) source = "";
		int len = length-source.length();
		if ( len > length ) {
			return source.substring(0,length-1);
		} else {
			return GolfEtt.pad(source,length,padChar) + source;
		}
	}
	
	/**
	 * rpad 문자열 반환.
	 * @param source 소스문자열
 	 * @param length 길이
  	 * @param pad 덧붙일 글자
	 * @return pad 문자열
	 */
	public static String rpad(String source, int length, char padChar) {
		if ( source == null ) source = "";
		int len = length-source.length();
		if ( len > length ) {
			return source.substring(0,length-1);
		} else {
			return source + GolfEtt.pad(source,length,padChar);
		}
	}
	
}
