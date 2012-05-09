/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAuthEtt
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� ��ƼƼ
*   �������  : golf
*   �ۼ�����  : 2009-06-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

/**
 * @author (��)����Ŀ�´����̼�
 * 2009-06-12
 */
public class GolfAuthEtt {

	protected String ocrBand;		// ocr���
	protected String tradeNo;		// �ŷ���ȣ
	protected String merMgmtNo; // ������ ��ȣ
	protected String cardNo;			// ī���ȣ
	protected String cardType;		// ī��Ÿ�� 1:�ֹι�ȣ, 2:����ڹ�ȣ, 3:ī���ȣ
	protected String valid;				// ��ȿ�Ⱓ
	protected String amount;			// ���αݾ�
	protected String remoteAddr;	// ���� ������
	protected String pmtNo;			// ���ι�ȣ
	protected String insTerm;		// �ҺαⰣ
	protected String useNo;			// ���ι�ȣ
	protected String bankNo;			// ȸ�����ȣ
	protected String resMsg;			// ����޽���
	protected String resCode;		// ����ڵ�
	protected String cavv;				// �ϳ����� ������

	/**
	 * @return the cavv
	 */
	public String getCavv() {
		return cavv;
	}
	/**
	 * @param cavv the cavv to set
	 */
	public void setCavv(String cavv) {
		this.cavv = cavv;
	}
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	/**
	 * @return the bankNo
	 */
	public String getBankNo() {
		return bankNo;
	}
	/**
	 * @param bankNo the bankNo to set
	 */
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	/**
	 * @return the cardNo
	 */
	public String getCardNo() {
		return cardNo;
	}
	/**
	 * @param cardNo the cardNo to set
	 */
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}
	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	/**
	 * @return the insTerm
	 */
	public String getInsTerm() {
		return insTerm;
	}
	/**
	 * @param insTerm the insTerm to set
	 */
	public void setInsTerm(String insTerm) {
		this.insTerm = insTerm;
	}
	/**
	 * @return the merMgmtNo
	 */
	public String getMerMgmtNo() {
		return merMgmtNo;
	}
	/**
	 * @param merMgmtNo the merMgmtNo to set
	 */
	public void setMerMgmtNo(String merMgmtNo) {
		this.merMgmtNo = merMgmtNo;
	}
	/**
	 * @return the ocrBand
	 */
	public String getOcrBand() {
		return ocrBand;
	}
	/**
	 * @param ocrBand the ocrBand to set
	 */
	public void setOcrBand(String ocrBand) {
		this.ocrBand = ocrBand;
	}
	/**
	 * @return the pmtNo
	 */
	public String getPmtNo() {
		return pmtNo;
	}
	/**
	 * @param pmtNo the pmtNo to set
	 */
	public void setPmtNo(String pmtNo) {
		this.pmtNo = pmtNo;
	}
	/**
	 * @return the remoteAddr
	 */
	public String getRemoteAddr() {
		return remoteAddr;
	}
	/**
	 * @param remoteAddr the remoteAddr to set
	 */
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	/**
	 * @return the resCode
	 */
	public String getResCode() {
		return resCode;
	}
	/**
	 * @param resCode the resCode to set
	 */
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	/**
	 * @return the resMsg
	 */
	public String getResMsg() {
		return resMsg;
	}
	/**
	 * @param resMsg the resMsg to set
	 */
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}
	/**
	 * @param tradeNo the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	/**
	 * @return the useNo
	 */
	public String getUseNo() {
		return useNo;
	}
	/**
	 * @param useNo the useNo to set
	 */
	public void setUseNo(String useNo) {
		this.useNo = useNo;
	}
	/**
	 * @return the valid
	 */
	public String getValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(String valid) {
		this.valid = valid;
	}



}

