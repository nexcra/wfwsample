/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAuthEtt
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 결제 엔티티
*   적용범위  : golf
*   작성일자  : 2009-06-12
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* 20091223			이영주	주문번호, 결제수단 추가
***************************************************************************************************/
package com.bccard.golf.common;

/**
 * @author (주)만세커뮤니케이션
 * 2009-06-12
 */
public class GolfPayAuthEtt extends GolfPayEtt {

	protected String ocrBand;		// ocr밴드
	protected String merMgmtNo; // 가맹점 번호
	protected String cardNo;			// 카드번호
	protected String cardType;		// 카드타입 1:주민번호, 2:사업자번호, 3:카드번호
	protected String valid;				// 유효기간
	protected String amount;			// 승인금액
	protected String remoteAddr;	// 접속 아이피
	protected String insTerm;		// 할부기간
	protected String useNo;			// 승인번호
	protected String bankNo;			// 회원사번호
	protected String resMsg;			// 결과메시지
	protected String resCode;		// 결과코드
	protected String cavv;				// 하나비자 검증값
	protected String orderNo;			// 골프라운지 주문번호
	protected String payType;			// 결제수단
	protected String cardNm;			// 신용카드이름(계좌이체 은행이름)
	protected String encData;			// submit암호화값 - 올앳페이용
	protected String shopId;			// 샵아이디 - 올앳페이용
	protected String crossKey;			// 크로스키 - 올랫페이용
	

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

	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return the payType
	 */
	public String getPayType() {
		return payType;
	}
	/**
	 * @param payType the payType to set
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}

	/**
	 * @return the cardNm
	 */
	public String getCardNm() {
		return cardNm;
	}
	/**
	 * @param cardNm the cardNm to set
	 */
	public void setCardNm(String cardNm) {
		this.cardNm = cardNm;
	}

	/**
	 * @param the encData
	 */
	public void setEncData(String encData) {
		this.encData = encData;
	}
	/**
	 * @return the encData
	 */
	public String getEncData() {
		return encData;
	}
	
	/**
	 * @param the allat shopId
	 */
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	/**
	 * @return the allat shopId
	 */
	public String getShopId() {
		return shopId;
	}
	
	/**
	 * @param the allat cross key 
	 */
	public void setCrossKey(String crossKey) {
		this.crossKey = crossKey;
	}
	/**
	 * @return the allat cross key
	 */
	public String getCrossKey() {
		return crossKey;
	}
	
	


}

