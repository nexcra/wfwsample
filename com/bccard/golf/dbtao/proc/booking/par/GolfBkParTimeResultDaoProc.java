/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeResultDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��û ���� Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfBkParTimeResultDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreTimeResultDaoProc ��ŷ ��û ���� Ȯ�� ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimeResultDaoProc() {}	

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
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------	
			String bk_DATE = data.getString("BK_DATE");		
			String sql = this.getSelectQuery(bk_DATE);
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO"));
			rs = pstmt.executeQuery();
		
			if(rs != null) {
				while(rs.next())  {

					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("RESULT_DATE" 			,rs.getString("RESULT_DATE") );
					result.addString("MAX_ACPT_PNUM" 		,rs.getString("MAX_ACPT_PNUM") );
					result.addString("RS_NUM" 				,rs.getString("RS_NUM") );
					result.addString("RES_NUM" 				,rs.getString("RES_NUM") );	
					result.addString("BK_DATE_EMAIL" 		,rs.getString("BK_DATE_EMAIL") );	
					result.addString("DEL_DATE" 			,rs.getString("DEL_DATE") );									
					
					result.addString("RESULT", "00"); //������
				}
			}

			if(result.size() < 1) {
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
    private String getSelectQuery(String bk_DATE){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT GREEN_NM, RESULT_DATE, MAX_ACPT_PNUM, RS_NUM						\n");
		sql.append("\t 	, (MAX_ACPT_PNUM-RS_NUM) AS RES_NUM, BK_DATE_EMAIL, DEL_DATE			\n");
		sql.append("\t  FROM(																	\n");
		sql.append("\t      SELECT GREEN_NM														\n");
		sql.append("\t      , TO_CHAR(TO_DATE('"+bk_DATE+"'),'YYYY.MM.DD (DY)') AS RESULT_DATE	\n");
		sql.append("\t      , MAX_ACPT_PNUM														\n");
		sql.append("\t      , (SELECT COUNT(*) RS_NUM FROM BCDBA.TBGRSVTMGMT WHERE ROUND_HOPE_DATE='"+bk_DATE+"' AND AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='Y') AS RS_NUM	\n");
		sql.append("\t      , TO_CHAR(TO_DATE('"+bk_DATE+"'),'MM/DD (DY)') AS BK_DATE_EMAIL					\n");
		sql.append("\t      , TO_CHAR(TO_DATE('"+bk_DATE+"')-BOKG_TIME_COLL_TRM,'MM/DD (DY)') AS DEL_DATE	\n");
		sql.append("\t      FROM BCDBA.TBGAFFIGREEN T1									\n");
		sql.append("\t      WHERE AFFI_GREEN_SEQ_NO=?									\n");
		sql.append("\t  )\n");
        
		return sql.toString();
    }
}
