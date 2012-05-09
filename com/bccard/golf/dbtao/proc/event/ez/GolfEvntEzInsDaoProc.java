/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntKvpDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ����ȸ > ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.ez;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0 
 ******************************************************************************/
public class GolfEvntEzInsDaoProc extends AbstractProc {
	
	public GolfEvntEzInsDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	// ����ȸ�� ���� �������� (����ȸ�� ��������)
	public String cntMemFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = "";

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no		= data.getString("jumin_no");
		
			// ȸ�����ΰ�������
			pstmt = conn.prepareStatement(getCntMem());
			pstmt.setString(1, jumin_no);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				result = rs.getString("END_DATE");
			}	

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	// ��û���� ��Ͽ���
	public int cntEvntFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no		= data.getString("jumin_no");
		
			pstmt = conn.prepareStatement(getCntEvnt());
			pstmt.setString(1, jumin_no);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				result = rs.getInt("CNT");
			}	

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	// ��û���� ����ϱ�
	public int insEvnt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int maxSeq = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no		= data.getString("jumin_no");
			String ur_name		= data.getString("ur_name");
			String payMoney		= data.getString("payMoney");
			String goodsCd		= data.getString("goodsCd");
			

			pstmt = conn.prepareStatement(getEvntSeq());
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				maxSeq = rs.getInt("SEQ");
			}	

			
			// �̺�Ʈ ���� ����ϱ�
			int idx = 0;
			pstmt = conn.prepareStatement(getInsEvnt());
			pstmt.setInt(++idx, maxSeq);
			pstmt.setString(++idx, jumin_no);
			pstmt.setString(++idx, ur_name);
			pstmt.setString(++idx, payMoney);
			pstmt.setString(++idx, goodsCd);
			result = pstmt.executeUpdate();
			
			if(result>0){
				result = maxSeq;
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}

	


	/** ***********************************************************************
    * ����ȸ�� ���� ��������
    ************************************************************************ */
    private String getCntMem(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY-MM-DD') END_DATE 	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	WHERE JUMIN_NO = ? AND NVL(SECE_YN,'N') = 'N'	\n");
		sql.append("\t	    AND ACRG_CDHD_JONN_DATE <= TO_CHAR(SYSDATE,'yyyyMMdd') AND ACRG_CDHD_END_DATE >= TO_CHAR(SYSDATE,'yyyyMMdd')	\n");
		sql.append("\t	    AND CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	        FROM BCDBA.TBGCMMNCODE T1	\n");
		sql.append("\t	        JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO	\n");
		sql.append("\t	        WHERE T1.GOLF_URNK_CMMN_CLSS='0000' AND T1.GOLF_URNK_CMMN_CODE='0005' AND CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	        AND CDHD_CTGO_SEQ_NO NOT IN ('8','18','16') )	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * ����ȸ�� ���� ��������
    ************************************************************************ */
    private String getCntEvnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT COUNT(*) CNT FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='0012' AND PGRS_YN IN ('Y') AND CO_NM=? 	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * �̺�Ʈ ���̺� �ִ밪��������
    ************************************************************************ */
    private String getEvntSeq(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT MAX(APLC_SEQ_NO)+1 SEQ FROM BCDBA.TBGAPLCMGMT	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * �̺�Ʈ ���̺� �����ϱ�
    ************************************************************************ */
    private String getInsEvnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, JUMIN_NO, BKG_PE_NM, REG_ATON, CHNG_ATON, STTL_AMT, RSVT_CDHD_GRD_SEQ_NO)	\n");
		sql.append("\t	VALUES	\n");
		sql.append("\t	(?, '0012', 'I', '', ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), '', ?, ?)	\n");
		return sql.toString();
    }

}
