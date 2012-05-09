/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��� Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.sky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

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
public class GolfBkSkyTimeRsViewDaoProc extends AbstractProc {
	
	String title = "";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc ��ŷ ��� Ȯ�� ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfBkSkyTimeRsViewDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("GOLF_SVC_RSVT_NO" 	,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("TOT_PERS_NUM" 		,rs.getString("TOT_PERS_NUM") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("HOLE" 				,rs.getString("HOLE") );
					result.addString("BK_TIME" 				,rs.getString("BK_TIME") );
					result.addString("HP_DDD_NO"		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO"		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO"		,rs.getString("HP_TEL_SNO") );
					result.addString("BK_DATE_REAL" 	,rs.getString("BK_DATE_REAL") );
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\t  	SELECT T1.GOLF_SVC_RSVT_NO AS GOLF_SVC_RSVT_NO															\n");
		sql.append("\t  	, T1.TOT_PERS_NUM AS TOT_PERS_NUM																		\n");
		sql.append("\t  	, TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE), 'YY.MM.DD(DY)') BK_DATE											\n");
		sql.append("\t  	, TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE), 'YYYYMMDD') BK_DATE_REAL											\n");
		sql.append("\t  	, (CASE WHEN T3.SKY72_HOLE_CODE='0001' THEN '7' ELSE '14' END ) AS HOLE									\n");
		sql.append("\t  	, SUBSTR(BOKG_ABLE_TIME,1,2)||':'||SUBSTR(BOKG_ABLE_TIME,3,2) BK_TIME									\n");
		sql.append("\t  	, T1.HP_DDD_NO, T1.HP_TEL_HNO, T1.HP_TEL_SNO															\n");
		sql.append("\t  	FROM BCDBA.TBGRSVTMGMT T1																				\n");
		sql.append("\t  	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t  	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T3.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO						\n");
		sql.append("\t  	WHERE GOLF_SVC_RSVT_NO=?																				\n");		
		
		return sql.toString();
    }
 
}
