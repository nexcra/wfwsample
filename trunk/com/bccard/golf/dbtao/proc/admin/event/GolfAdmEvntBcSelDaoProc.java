/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkSelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ BC GOLF �̺�Ʈ ����Ʈ �ڽ�
*   �������  : golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

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
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmEvntBcSelDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBcSelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBcSelDaoProc() {}	

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

			String sql = this.getSelectQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("EVNT_SEQ_NO") );
					result.addString("EVNT_NM" 			,rs.getString("EVNT_NM") );
					result.addString("EVNT_STRT_DATE" 	,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_END_DATE" 	,rs.getString("EVNT_END_DATE") );
				}
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
		
		sql.append("\n SELECT 	");
		sql.append("\n 		EVNT_SEQ_NO, EVNT_NM, TO_CHAR(TO_DATE(EVNT_STRT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_STRT_DATE,  	");
		sql.append("\n 		TO_CHAR(TO_DATE(EVNT_END_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_END_DATE	");
		sql.append("\n 	FROM 	");
		sql.append("\n 	BCDBA.TBGEVNTMGMT	");
		sql.append("\n 	WHERE EVNT_CLSS = '0001' 	");
		sql.append("\n  ORDER BY REG_ATON DESC	");
		return sql.toString();
    }
}
