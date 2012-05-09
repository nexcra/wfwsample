/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeTimeUpdFormDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� �����ð� ����
*   �������  : golf
*   �ۼ�����  : 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmRangeTimeUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeTimeUpdFormDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeTimeUpdFormDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public HashMap execute(WaContext context, HashMap resultMap, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery1();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("RSVTDIALY_SQL_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					resultMap.put("RSVTDIALY_SQL_NO" 			,rs.getString("RSVT_ABLE_SCD_SEQ_NO") );
					resultMap.put("SLS_END_YN" 			,rs.getString("RESM_YN") );
					resultMap.put("RSVT_TOTAL_NUM"			,rs.getString("DLY_RSVT_ABLE_PERS") );
					resultMap.put("RSVT_DATE" 		,rs.getString("RSVT_ABLE_DATE") );
					
					resultMap.put("RESULT", "00"); //������
				}
			}

			if(resultMap.size() < 1) {
				resultMap.put("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return resultMap;
	}	
	
	
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
			String sql = this.getSelectQuery2();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("RSVTDIALY_SQL_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("RSVTTIME_SQL_NO" 			,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("RSVT_STRT_HH" 		,rs.getString("RSVT_STRT_HH") );
					result.addString("RSVT_STRT_MI" 		,rs.getString("RSVT_STRT_MI") );
					result.addString("RSVT_END_HH" 		,rs.getString("RSVT_END_HH") );
					result.addString("RSVT_END_MI" 		,rs.getString("RSVT_END_MI") );
					result.addLong("DAY_RSVT_NUM"			,rs.getLong("DLY_RSVT_ABLE_PERS_NUM") );
					
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
    private String getSelectQuery1(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n SELECT");
		sql.append("\n 	RSVT_ABLE_SCD_SEQ_NO, RESM_YN, DLY_RSVT_ABLE_PERS,	 ");
		sql.append("\n 	SUBSTR (RSVT_ABLE_DATE, 1, 4) ||'-'|| SUBSTR (SUBSTR (RSVT_ABLE_DATE, 5, 6), 1, 2) ||'-'|| SUBSTR (RSVT_ABLE_DATE, 7, 8) RSVT_ABLE_DATE	 ");
		sql.append("\n FROM BCDBA.TBGRSVTABLESCDMGMT  	");
		sql.append("\n WHERE GOLF_RSVT_DAY_CLSS = 'D'	");
		sql.append("\n AND RSVT_ABLE_SCD_SEQ_NO = ?	");	
		
		return sql.toString();
    }
    
    private String getSelectQuery2(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n SELECT");
		sql.append("\n 	TGRTM.RSVT_ABLE_BOKG_TIME_SEQ_NO,  ");
		sql.append("\n 	SUBSTR (TGRTM.RSVT_STRT_TIME, 1, 2) RSVT_STRT_HH, SUBSTR (TRIM(TGRTM.RSVT_STRT_TIME), 3, 4) RSVT_STRT_MI,  ");
		sql.append("\n 	SUBSTR (TGRTM.RSVT_END_TIME, 1, 2) RSVT_END_HH, SUBSTR (TRIM(TGRTM.RSVT_END_TIME), 3, 4) RSVT_END_MI,	 ");
		sql.append("\n 	TGRTM.DLY_RSVT_ABLE_PERS_NUM  ");
		sql.append("\n FROM BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRTM 	");
		sql.append("\n WHERE TGRTM.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
		sql.append("\n AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'	");	
		sql.append("\t 	AND TGRD.RSVT_ABLE_SCD_SEQ_NO = ?	");	
		return sql.toString();
    }
}
