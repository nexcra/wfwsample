/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

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
public class GolfBkParGrTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParGrTimeListDaoProc() {}	

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
						 
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery();

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setInt(++idx, data.getInt("SEQ_NO") ); 
			pstmt.setString(++idx, data.getString("dbDate") ); 
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	
					
				
					result.addString("TIME_SEQ_NO" 			,rs.getString("TIME_SEQ_NO") );
					result.addString("BKPS_DATE" 			,rs.getString("BKPS_DATE") );
					result.addString("BKPS_YOIL" 			,rs.getString("BKPS_YOIL") );
					result.addString("BKPS_TIME" 			,rs.getString("BKPS_TIME") );
					result.addString("BKPS_MINUTE" 			,rs.getString("BKPS_MINUTE") );
					result.addString("COURSE" 				,rs.getString("COURSE") );
					
				}
				result.addString("RESULT", "00"); //������
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			TIME_SEQ_NO, BKPS_DATE, BKPS_YOIL, BKPS_TIME, BKPS_MINUTE, COURSE 	");
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 				, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO 	");
		sql.append("\n	    		, TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'yyyy.mm.dd') AS BKPS_DATE 	");
		sql.append("\n 				, TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'dy') AS BKPS_YOIL 	");
		sql.append("\n 				, SUBSTR(T1.BOKG_ABLE_TIME,0,2) AS BKPS_TIME 	");
		sql.append("\n 				, SUBSTR(T1.BOKG_ABLE_TIME,3,4) AS BKPS_MINUTE 	");
		sql.append("\n 				, T2.GOLF_RSVT_CURS_NM AS COURSE 	");
		sql.append("\n				FROM  	");
		sql.append("\n 				BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1 	");
		sql.append("\n 				LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 				WHERE T1.RESER_CODE='0001' AND (T1.EVNT_YN is null or T1.EVNT_YN='N') 	");
		sql.append("\n 				AND T1.AFFI_GREEN_SEQ_NO=? AND T2.BOKG_ABLE_DATE=? 	");
		sql.append("\n 				ORDER BY BKPS_TIME, BKPS_MINUTE 	");
		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");

		return sql.toString();
    }
    
    
}
