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

package com.bccard.golf.dbtao.proc.event.shop;

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
public class GolfEvntShopListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntShopListDaoProc() {}	

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
			String brnd_seq_no = data.getString("brnd_seq_no");	
			String brnd_clss = data.getString("brnd_clss");

			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, brnd_seq_no);
			pstmt.setString(2, brnd_clss);
			rs = pstmt.executeQuery();

			if ( rs.next() ) {
				GolfUtil.toTaoResult(result, rs);
				result.addString("RESULT", "00");
			} else {
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT TP.GDS_CODE, TP.GDS_NM, TP.PX_100_IMG, TP.PRMT_NM, TP.INTNT_BRND_CLSS, TP.BRND_NM	\n");
		sql.append("\t	, (SELECT SALE_AMT FROM BCDBA.TBGDSPRIC WHERE APPL_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND GDS_CODE=TP.GDS_CODE AND ROWNUM=1) SALE_AMT	\n");
		sql.append("\t	, BRD.EPS_SEQ	\n");
		sql.append("\t	FROM BCDBA.TBBRNDGDS BRD	\n");
		sql.append("\t	JOIN BCDBA.TBGDS TP ON BRD.GDS_CODE=TP.GDS_CODE	\n");
		sql.append("\t	WHERE BRND_SEQ_NO=? AND TP.SALE_CLSS = '00' AND TP.EPS_YN = 'A' AND TP.INTNT_BRND_CLSS=?	\n");
		sql.append("\t	ORDER BY BRD.EPS_SEQ	\n");
		
		return sql.toString();
    }
}
