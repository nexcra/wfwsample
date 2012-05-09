/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > �󼼺��� 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.benest;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntBnstSchDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBnstSchDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String jumin_no						= data.getString("jumin_no");
			String hp_ddd_no					= data.getString("hp_ddd_no");
			String hp_tel_hno					= data.getString("hp_tel_hno");
			String hp_tel_sno					= data.getString("hp_tel_sno");
			
			
			String sql = this.getEvtCntQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;	// 11
			pstmt.setString(idx++, jumin_no);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);

			rs = pstmt.executeQuery();
			
			if ( rs != null ) {
				
				while(rs.next())  {
					result.addString("APLC_SEQ_NO" 			,rs.getString("APLC_SEQ_NO") );
					result.addString("TRM_UNT" 			,rs.getString("TRM_UNT") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("RSVT_DATE" 			,rs.getString("RSVT_DATE") );
					result.addString("RSV_TIME" 			,rs.getString("RSV_TIME") );
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );
					result.addString("EVNT_PGRS_CLSS" 		,rs.getString("EVNT_PGRS_CLSS") );
					result.addString("PAY_YN" 				,rs.getString("PAY_YN") );
					result.addString("RESULT", "00"); //������ 
				}
				
			}

			if (result.size() < 1) {
				result.addString("RESULT", "01");
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
    * ���� �̺�Ʈ ������ �ִ��� �˾ƺ���.
    ************************************************************************ */
    private String getEvtCntQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT APLC_SEQ_NO, GREEN_NM, TO_CHAR(TO_DATE(RSVT_DATE),'YYYY-MM-DD') RSVT_DATE, BKG_PE_NM , TRM_UNT	\n");
		sql.append("\t , CASE EVNT_PGRS_CLSS WHEN 'R' THEN '��û' WHEN 'A' THEN '���' WHEN 'P' THEN '��������' WHEN 'B' THEN 'Ȯ��' WHEN 'C' THEN '�������' WHEN 'E' THEN '�������' END EVNT_PGRS_CLSS	\n");
		sql.append("\t , CASE EVNT_PGRS_CLSS WHEN 'R' THEN 'N' ELSE 'Y' END PAY_YN, CASE WHEN SUBSTR(RSV_TIME,1,2)>12 THEN '����' ELSE '����' END RSV_TIME	\n");
		sql.append("\t FROM BCDBA.TBGGOLFEVNTAPLC WHERE JUMIN_NO=? AND GOLF_SVC_APLC_CLSS = '9003' OR ( GOLF_SVC_APLC_CLSS = '9003' AND HP_DDD_NO=? AND HP_TEL_HNO=? AND HP_TEL_SNO=?)	\n");
		sql.append("\t ORDER BY APLC_ATON DESC	\n"); 
		
		return sql.toString(); 
    }
}
