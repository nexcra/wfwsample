/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ����Ʈ 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.prime;

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
public class GolfEvntPrimeInsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntPrimeInsDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);

			// ��û����
			String cdhd_id		= data.getString("cdhd_id");		// ȸ�����̵�
			String bkg_pe_num	= data.getString("bkg_pe_num");		// ����
			String jumin_no1	= data.getString("jumin_no1");		// �ֹε�Ϲ�ȣ1
			String jumin_no2	= data.getString("jumin_no2");		// �ֹε�Ϲ�ȣ2
			String hp_ddd_no	= data.getString("hp_ddd_no");		// ����ó1
			String hp_tel_hno	= data.getString("hp_tel_hno");		// ����ó2
			String hp_tel_sno	= data.getString("hp_tel_sno");		// ����ó3
			String ddd_no		= data.getString("ddd_no");			// ����ȭ1
			String tel_hno		= data.getString("tel_hno");		// ����ȭ2
			String tel_sno		= data.getString("tel_sno");		// ����ȭ3
			String dtl_addr		= data.getString("dtl_addr");		// �ּ�
			String lesn_seq_no	= data.getString("lesn_seq_no");	// ���Ը����
			String pu_date		= data.getString("pu_date");		// ȸ��������
			String memo_expl	= data.getString("memo_expl");		// ��Ÿ ��û ����
			
			// ��������
			String order_no		= data.getString("order_no");		// �ֹ��ڵ�
			String realPayAmt	= data.getString("realPayAmt");		// ���� �ݾ�
			 
			
			// ��û���� �Է�
			pstmt = conn.prepareStatement(this.getEvntInsQuery());
			int idx = 1;
			pstmt.setString(idx++, cdhd_id);
			pstmt.setString(idx++, bkg_pe_num);
			pstmt.setString(idx++, jumin_no1+jumin_no2);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, ddd_no);
			pstmt.setString(idx++, tel_hno);
			pstmt.setString(idx++, tel_sno);
			pstmt.setString(idx++, dtl_addr);
			pstmt.setString(idx++, lesn_seq_no);
			pstmt.setString(idx++, realPayAmt);
			pstmt.setString(idx++, pu_date);
			pstmt.setString(idx++, memo_expl);
			pstmt.setString(idx++, order_no);
			result = pstmt.executeUpdate();

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	// ���� �������� ��� �ֹ����� ������Ʈ
	public int execute_upd(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);

			String order_no			= data.getString("order_no");			// �ֹ���ȣ
			String pgrs_yn			= data.getString("pgrs_yn");			// ��������
			String cslt_yn			= data.getString("cslt_yn");			// ������� 1:�¶��ΰ���
			
			String sql = this.getUpdQuery();   
			
			// �Է°� (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, pgrs_yn);
			pstmt.setString(idx++, cslt_yn);
			pstmt.setString(idx++, order_no);
			result = pstmt.executeUpdate();

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	
	// �̹� ��û�� ������ �ִ��� �˾ƺ���.
	public int execute_insYn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		ResultSet rs = null;

		try {
			conn = context.getDbConnection("default", null);

			String jumin_no			= data.getString("jumin_no");			// �ֹε�Ϲ�ȣ
			String bkg_pe_num		= data.getString("bkg_pe_num");			// ����
			
			String sql = this.getEvntInsYnQuery();   
			
			// �Է°� (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, jumin_no);
			pstmt.setString(idx++, bkg_pe_num);
			rs = pstmt.executeQuery();

			if(rs.next()){
				result = rs.getInt("CNT");
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/** ***********************************************************************
    * �̹� ��û�� ������ �ִ��� �˾ƺ���.
    ************************************************************************ */
	
    private String getEvntInsYnQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT COUNT(*) CNT	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND PGRS_YN IN ('I','Y') AND JUMIN_NO=? AND BKG_PE_NM=?	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
    * ��û���� ���
    ************************************************************************ */
	
    private String getEvntInsQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGAPLCMGMT (	\n");
		sql.append("\t	APLC_SEQ_NO, CDHD_ID, BKG_PE_NM, JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO	\n");
		sql.append("\t	, DTL_ADDR, LESN_SEQ_NO, STTL_AMT, PU_DATE, MEMO_EXPL, CO_NM, PGRS_YN, REG_ATON, GREEN_NM, GOLF_SVC_APLC_CLSS	\n");
		sql.append("\t	) VALUES (	\n");
		sql.append("\t	(SELECT MAX(APLC_SEQ_NO)+1 FROM BCDBA.TBGAPLCMGMT), ?, ?, ?, ?, ?, ?, ?, ?, ?	\n");
		sql.append("\t	, ?, ?, ?, ?, ?, ?, 'I', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE+365,'YYYYMMDDHH24MISS'), '1003'	\n");
		sql.append("\t	)	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * ��û���� - ���� ���� ����
     ************************************************************************ */
 	
     private String getUpdQuery(){
         StringBuffer sql = new StringBuffer();		

  		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT 	\n");
 		sql.append("\t	SET PGRS_YN = ? , CSLT_YN = ?	\n");
 		sql.append("\t	WHERE CO_NM = ?	\n");
 		
 		return sql.toString();
     }
}
