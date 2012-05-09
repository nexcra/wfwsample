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

package com.bccard.golf.dbtao.proc.event;

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
public class GolfEvntKvpDaoProc extends AbstractProc {
	
	public GolfEvntKvpDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String socid						= data.getString("socid");
			String name							= data.getString("name");
			String ddd_no						= data.getString("ddd_no");
			String tel_hno						= data.getString("tel_hno");
			String tel_sno						= data.getString("tel_sno");
			String hp_ddd_no					= data.getString("hp_ddd_no");
			String hp_tel_hno					= data.getString("hp_tel_hno");
			String hp_tel_sno					= data.getString("hp_tel_sno");
			String email						= data.getString("email");
			String kvp_idx						= data.getString("idx");
			String sttl_amt						= data.getString("sttl_amt");
						
			// �̺�Ʈ ���
			String sql = this.getEvtQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, socid);
			pstmt.setString(idx++, sttl_amt);
			pstmt.setString(idx++, name);
			pstmt.setString(idx++, ddd_no);
			pstmt.setString(idx++, tel_hno);
			pstmt.setString(idx++, tel_sno);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, email);
			pstmt.setString(idx++, kvp_idx);
			result = pstmt.executeUpdate();
						

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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
    * �̺�Ʈ ��� 
    ************************************************************************ */
    private String getEvtQuery(){
        StringBuffer sql = new StringBuffer();	
        
		sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT (	");
		sql.append("\n	    APLC_SEQ_NO, GOLF_LESN_RSVT_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, JUMIN_NO, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT	");
		sql.append("\n	    , CSLT_YN, GREEN_NM, CO_NM, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, RSVT_CDHD_GRD_SEQ_NO	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    (SELECT MAX(APLC_SEQ_NO)+1 FROM BCDBA.TBGAPLCMGMT), 1, '1001', 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),? 	");
		sql.append("\n	    , 'N', 'KVP', ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	)	");
		
		return sql.toString();
    }

}
