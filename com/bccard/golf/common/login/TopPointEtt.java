/***************************************************************************************
*  Ŭ������        :   TopPointEtt
*  �� �� ��        :   unknown
*  ��    ��        :   TOP ����Ʈ ����
*  �������        :   bccard 
*  �ۼ�����        :   unknown
************************** �����̷� ***************************************************
* ����			����		�ۼ���		�������
* 2006.08.21	1.0		khko		���޾�ü�ڵ��߰� 
****************************************************************************************/

package com.bccard.golf.common.login;

/***************************************************************************************
 * TOP ����Ʈ ����
 * @version unknown
 * @author  unknown
****************************************************************************************/
public class TopPointEtt implements java.io.Serializable {

	/** TOP ����Ʈ */
	public static final String TOPPOINT = "01";

	/** ���� ���� ž ����Ʈ �߰� 2003.10.01*/
	public static final String SUPERPOINT = "51";

	/** TOP ���� */
	public static final String TOPHYUNDAI = "36";
 
	/** TOP SK*/
	public static final String TOPSK = "38";

	/** TOP ���ϸ��� */
	public static final String TOPMILEAGE = "39";

	/** ���޾�ü ����Ʈ */
	public static final String AFFILIATED = "00";

	/** ��ü�� */
	private String companyName;

	/** ����Ʈ ���� */
	private String pointType;

	/** ����Ʈ �̸� */
	private String pointName;

	/** ���������� ������Ʈ�� ��¥ */
	private String updatedDate;

	/** ����Ʈ(�α��ο��� ������ ����Ʈ) */
	private int point;

	/** ���� ����Ʈ */
	private int finalPoint;
	
	/** ���޾�ü�ڵ� */
	private String companyCode;

	/**
	 * TOP ����Ʈ ���� ������
	 * @param companyName ��ü��
	 * @param pointType ����Ʈ ����
	 * @param pointName ����Ʈ ��
	 * @param updatedDate ������Ʈ ��¥
	 * @param point ����Ʈ(�α��ο��� ������ ����Ʈ)
	 * @param finalPoint ��������Ʈ
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
	* TOP ����Ʈ ���� ������
	* @param 	N/A
	* @return 	String		�����Ͻ�
	********************************************************************************** */
	public TopPointEtt(){
		this.point = 0;
		this.finalPoint = 0;
		this.updatedDate = "-";
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		��ü��
	********************************************************************************** */
	public String getCompanyName() {
		return companyName;
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		����Ʈ ����
	********************************************************************************** */
	public String getPointType() {
		return pointType;
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		����Ʈ �̸�
	********************************************************************************** */
	public String getPointName() {
		return pointName;
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		���������� ������Ʈ�� ��¥
	********************************************************************************** */
	public String getUpdatedDate() {
		return updatedDate;
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		����Ʈ
	********************************************************************************** */
	public int getPoint() {
		return point;
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		���� ����Ʈ
	********************************************************************************** */
	public int getFinalPoint() {
		return finalPoint;
	}
	
	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		���޾�ü�ڵ�
	********************************************************************************** */
	public String getCompanyCode() {
		return companyCode;
	}
	
	/** ********************************************************************************
	* TOP����Ʈ ������ �̺�Ʈ
	* @param 	str(���޾�ü�ڵ�)		String ��ü.
	* @return 	void
	********************************************************************************** */
	public void setCompanyCode(String str) {
		this.companyCode = str;
	}

	/** ********************************************************************************
	* TOP ����Ʈ ���� 
	* @param 	N/A
	* @return 	String		TOP ����Ʈ ����
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

