/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdFormDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� �� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.jeju;

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
public class GolfadmJjRsViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmJjRsViewDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
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
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("HP" 					,rs.getString("HP") );
					result.addString("REG_DATE" 			,rs.getString("REG_DATE") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("BK_TIME_AP" 			,rs.getString("BK_TIME_AP") );
					result.addString("BK_TIME" 				,rs.getString("BK_TIME") );
					result.addString("TEAM_NUM" 			,rs.getString("TEAM_NUM") );
					result.addString("TOT_PERS_NUM" 		,rs.getString("TOT_PERS_NUM") );
					result.addString("HOPE_RGN_CODE" 		,rs.getString("HOPE_RGN_CODE") );
					result.addString("CTCT_ABLE_TIME" 		,rs.getString("CTCT_ABLE_TIME") );
					result.addString("NAME" 				,rs.getString("NAME") );
										
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
        		
		sql.append("\n SELECT GOLF_SVC_RSVT_NO 																						\n");
		sql.append("\t  , T6.CDHD_ID , T6.MOBILE AS HP , T6.HG_NM AS NAME																									\n");
		sql.append("\t  , TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8)),'YY-MM-DD(DY)')														\n");
		sql.append("\t  ||' '||SUBSTR(REG_ATON,9,2)||':'||SUBSTR(REG_ATON,11,2) AS REG_DATE											\n");
		sql.append("\t  , TO_CHAR(TO_DATE(ROUND_HOPE_DATE), 'YY.MM.DD(DY)') BK_DATE													\n");
		sql.append("\t  , (CASE WHEN ROUND_HOPE_TIME_CLSS='A' THEN '����' ELSE '����' END) AS BK_TIME_AP								\n");
		sql.append("\t  , (CASE WHEN ROUND_HOPE_TIME='000000' THEN '��ü' ELSE SUBSTR(ROUND_HOPE_TIME,1,2)||':00' END) AS BK_TIME	\n");
		sql.append("\t  , TEAM_NUM																									\n");
		sql.append("\t  , TOT_PERS_NUM																								\n");
		sql.append("\t  , (CASE WHEN HOPE_RGN_CODE='0001' THEN '��ü'																\n");
		sql.append("\t      WHEN HOPE_RGN_CODE='0002' THEN '���ֽ�'																	\n");
		sql.append("\t      WHEN HOPE_RGN_CODE='0003' THEN '��������'																\n");
		sql.append("\t      WHEN HOPE_RGN_CODE='0004' THEN '����'																	\n");
		sql.append("\t      WHEN HOPE_RGN_CODE='0005' THEN '����' END) AS HOPE_RGN_CODE												\n");
		sql.append("\t  , (CASE WHEN CTCT_ABLE_STRT_TIME='000000' THEN '��ü'														\n");
		sql.append("\t      WHEN CTCT_ABLE_STRT_TIME='170000' THEN '17:00�� ����'													\n");
		sql.append("\t      ELSE SUBSTR(CTCT_ABLE_STRT_TIME,1,2)||':00~'||SUBSTR(CTCT_ABLE_END_TIME,1,2)||':00' END) AS CTCT_ABLE_TIME\n");
		sql.append("\t	FROM BCDBA.TBGRSVTMGMT T1																					\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHD T6 ON T6.CDHD_ID=T1.CDHD_ID 															\n");
		sql.append("\t  WHERE GOLF_SVC_RSVT_NO=?																					\n");
		
		return sql.toString();
    }
}
