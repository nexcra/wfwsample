/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeDialyListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ���� ����Ʈ
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

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmRangeDialyListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeDialyListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeDialyListDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			String start_dt		= data.getString("START_DT");
			String end_dt		= data.getString("END_DT");
			String sch_gr		= data.getString("SCH_GR_SEQ_NO");
			
			
			sql = this.getSelectQuery(start_dt,end_dt, sch_gr);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // ��¥ �˻�
				pstmt.setString(++idx, start_dt);
				pstmt.setString(++idx, end_dt);
			}
			
			if (!GolfUtil.isNull(sch_gr)) { // ������
				pstmt.setString(++idx, sch_gr);
			}
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {
					
					result.addLong("RSVTDIALY_SQL_NO" 		,rs.getLong("RSVT_ABLE_SCD_SEQ_NO") );
					result.addString("SLS_END_YN"			,rs.getString("RESM_YN"));
					result.addString("RSVT_DATE"			,rs.getString("RSVT_ABLE_DATE"));
					result.addString("GREEN_NM"				,rs.getString("GREEN_NM"));					
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
										
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
    private String getSelectQuery(String start_dt, String end_dt, String sch_gr){

        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
        sql.append("\n FROM (SELECT ROWNUM RNUM, AFFI_GREEN_SEQ_NO,	GREEN_NM,		");
        sql.append("\n 			RSVT_ABLE_SCD_SEQ_NO, RESM_YN, RSVT_ABLE_DATE,  	");
        sql.append("\n 			CEIL (ROWNUM / ?) AS PAGE, MAX (ROWNUM) OVER () TOT_CNT		");
        sql.append("\n 		FROM (SELECT DISTINCT TGRD.AFFI_GREEN_SEQ_NO, GREEN.GREEN_NM, TGRD.RSVT_ABLE_SCD_SEQ_NO, TGRD.RESM_YN, 	");
        sql.append("\n 				SUBSTR (TGRD.RSVT_ABLE_DATE, 1, 4) ||'�� '|| SUBSTR (SUBSTR (TGRD.RSVT_ABLE_DATE, 5, 6), 1, 2) ||'�� '|| SUBSTR (TGRD.RSVT_ABLE_DATE, 7, 8) ||'��' RSVT_ABLE_DATE		");
        sql.append("\n 				FROM BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRTM, BCDBA.TBGAFFIGREEN GREEN	");
        sql.append("\n 				WHERE TGRTM.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
        sql.append("\n 				AND TGRD.AFFI_GREEN_SEQ_NO =  GREEN.AFFI_GREEN_SEQ_NO	");
        sql.append("\n 				AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'	");
        
        if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // ��¥ �˻�
			sql.append("\n 			AND TGRD.RSVT_ABLE_DATE BETWEEN ? AND ?	");
		}
        
        if (!GolfUtil.isNull(sch_gr)) { // ������ �˻�
        	sql.append("\n 			AND TGRD.AFFI_GREEN_SEQ_NO = ?	");
        }
        
        sql.append("\n 				ORDER BY RSVT_ABLE_DATE DESC)	");
        sql.append("\n 		ORDER BY RNUM)	");
        sql.append("\n WHERE PAGE = ?		");
      
       return sql.toString();
       
    }
  
}
