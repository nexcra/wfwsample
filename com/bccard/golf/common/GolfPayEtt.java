/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAuthEtt
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� ��ƼƼ ����
*   �������  : golf
*   �ۼ�����  : 2009-06-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

public abstract class GolfPayEtt implements java.io.Serializable {
	/** �ŷ����� */
	protected String tradeDate;
	/** �ŷ��ð� */
	protected String tradeTime;
	/** ó������ڵ� */
	protected String tradeResult;
	/** �ŷ������ڵ� */
	protected String tradeGubun;
	/** ��ü�����ڵ� */
	protected String bankCd;
	/** FILLER */
	protected String filler;
	
	/**
	 * Constructor.
	 */
	protected GolfPayEtt() {
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
	 * ���� �ʱ�ȭ.
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
	 * �ۼ��ŵ����� �Ľ�.
	 * @param raw �ۼ��ŵ�����
	 * @return �Ľ� ���� ����.
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
	 * �ۼ��ŵ����������� ��ȯ. 
	 * @return �ۼ��ŵ�����
	 */
	public byte[] getBytes() {
		byte[] tmpraw = new byte[28];
		System.arraycopy(GolfPayEtt.rpad(tradeDate  , 8,' ').getBytes(),0,tmpraw, 0, 8);
		System.arraycopy(GolfPayEtt.rpad(tradeTime  , 6,' ').getBytes(),0,tmpraw, 8, 6);
		System.arraycopy(GolfPayEtt.rpad(tradeResult, 4,'0').getBytes(),0,tmpraw,14, 4);
		System.arraycopy(GolfPayEtt.rpad(tradeGubun , 4,' ').getBytes(),0,tmpraw,18, 4);
		System.arraycopy(GolfPayEtt.rpad(bankCd     , 2,'0').getBytes(),0,tmpraw,22, 2);
		System.arraycopy(GolfPayEtt.rpad(filler     , 4,'0').getBytes(),0,tmpraw,24, 4);
		return tmpraw;
	}
	
	/**
	 * ���ڿ��� ��ȯ.
	 */
	public String toString() {
		return new String(getBytes());
	}

	/**
	 * pad ���ڿ� ��ȯ.
	 * @param source �ҽ����ڿ�
 	 * @param length ����
  	 * @param pad ������ ����
	 * @return pad ���ڿ�
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
	 * lpad ���ڿ� ��ȯ.
	 * @param source �ҽ����ڿ�
 	 * @param length ����
  	 * @param pad ������ ����
	 * @return pad ���ڿ�
	 */
	public static String lpad(String source, int length, char padChar) {
		if ( source == null ) source = "";
		int len = length-source.length();
		if ( len > length ) {
			return source.substring(0,length-1);
		} else {
			return GolfPayEtt.pad(source,length,padChar) + source;
		}
	}
	
	/**
	 * rpad ���ڿ� ��ȯ.
	 * @param source �ҽ����ڿ�
 	 * @param length ����
  	 * @param pad ������ ����
	 * @return pad ���ڿ�
	 */
	public static String rpad(String source, int length, char padChar) {
		if ( source == null ) source = "";
		int len = length-source.length();
		if ( len > length ) {
			return source.substring(0,length-1);
		} else {
			return source + GolfPayEtt.pad(source,length,padChar);
		}
	}


}
