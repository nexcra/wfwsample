/***************************************************************************************
*  클래스명        :   TopPointEtt
*  작 성 자        :   unknown
*  내    용        :   TOP 포인트 정보
*  적용범위        :   bccard 
*  작성일자        :   unknown
************************** 수정이력 ***************************************************
* 일자			버전		작성자		변경사항
* 2006.08.21	1.0		khko		제휴업체코드추가 
****************************************************************************************/

package com.bccard.golf.common.login;

/***************************************************************************************
 * TOP 포인트 정보
 * @version unknown
 * @author  unknown
****************************************************************************************/
public class TopPointEtt implements java.io.Serializable {

	/** TOP 포인트 */
	public static final String TOPPOINT = "01";

	/** 농협 슈퍼 탑 포인트 추가 2003.10.01*/
	public static final String SUPERPOINT = "51";

	/** TOP 현대 */
	public static final String TOPHYUNDAI = "36";
 
	/** TOP SK*/
	public static final String TOPSK = "38";

	/** TOP 마일리지 */
	public static final String TOPMILEAGE = "39";

	/** 제휴업체 포인트 */
	public static final String AFFILIATED = "00";

	/** 업체명 */
	private String companyName;

	/** 포인트 종류 */
	private String pointType;

	/** 포인트 이름 */
	private String pointName;

	/** 마지막으로 업데이트된 날짜 */
	private String updatedDate;

	/** 포인트(로그인옆에 나오는 포인트) */
	private int point;

	/** 가용 포인트 */
	private int finalPoint;
	
	/** 제휴업체코드 */
	private String companyCode;

	/**
	 * TOP 포인트 정보 생성자
	 * @param companyName 업체명
	 * @param pointType 포인트 종류
	 * @param pointName 포인트 명
	 * @param updatedDate 업데이트 날짜
	 * @param point 포인트(로그인옆에 나오는 포인트)
	 * @param finalPoint 가용포인트
	*/
	public TopPointEtt(String companyName, String pointType, String pointName, String updatedDate, int point, int finalPoint) {
		this.companyName = companyName;
		this.pointType = pointType;
		this.pointName = pointName;
		this.updatedDate = updatedDate;
		this.point = point;
		this.finalPoint = finalPoint;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 생성자
	* @param 	N/A
	* @return 	String		승인일시
	********************************************************************************** */
	public TopPointEtt(){
		this.point = 0;
		this.finalPoint = 0;
		this.updatedDate = "-";
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		업체명
	********************************************************************************** */
	public String getCompanyName() {
		return companyName;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		포인트 종류
	********************************************************************************** */
	public String getPointType() {
		return pointType;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		포인트 이름
	********************************************************************************** */
	public String getPointName() {
		return pointName;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		마지막으로 업데이트된 날짜
	********************************************************************************** */
	public String getUpdatedDate() {
		return updatedDate;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		포인트
	********************************************************************************** */
	public int getPoint() {
		return point;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		가용 포인트
	********************************************************************************** */
	public int getFinalPoint() {
		return finalPoint;
	}
	
	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		제휴업체코드
	********************************************************************************** */
	public String getCompanyCode() {
		return companyCode;
	}
	
	/** ********************************************************************************
	* TOP포인트 가맹점 이벤트
	* @param 	str(제휴업체코드)		String 객체.
	* @return 	void
	********************************************************************************** */
	public void setCompanyCode(String str) {
		this.companyCode = str;
	}

	/** ********************************************************************************
	* TOP 포인트 정보 
	* @param 	N/A
	* @return 	String		TOP 포인트 정보
	********************************************************************************** */
	public String toString() {

		StringBuffer buf = new StringBuffer();

		buf.append("[");
		buf.append("companyName="+companyName+",");
		buf.append("companyCode="+companyCode+",");
		buf.append("pointType="+pointType+",");
		buf.append("pointName="+pointName+",");
		buf.append("updatedDate="+updatedDate+",");
		buf.append("totalPoints="+point+",");
		buf.append("topPoints="+finalPoint);
		buf.append("]");

		return buf.toString();
	}
}

