/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfUseRecordDaoProc
*   �ۼ���    : �强��
*   ����      : ������ > ��ŷ > TOP����ī���ŷ���� > TOP�����ī������� 
*   �������  : golf
*   �ۼ�����  : 2010-11-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������  
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfadmTopGolfUseRecordDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmTopGolfUseRecordDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmTopGolfUseRecordDaoProc() {}	 

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

			String diff = data.getString("diff");
			String yyyy	= data.getString("yyyy");      
			String from	= data.getString("from"); 	 
			String to	= data.getString("to"); 	 
			boolean dInq = "0".equals(diff);
			
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery(dInq );   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			if (dInq) {
				pstmt.setString( ++idx, yyyy + "01" );
				pstmt.setString( ++idx, yyyy + "12" );
			} else {
				pstmt.setString( ++idx, from + "01" );
				pstmt.setString( ++idx, to   + "12" );
			}
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("MB_NM",		rs.getString("MB_NM"));
					result.addString("USE_YYYYMM",	rs.getString("USE_YYYYMM"));
					result.addString("MB_NO",		rs.getString("MB_NO"));
					result.addString("INDIV",		rs.getString("INDIV"));
					result.addString("CORP",		rs.getString("CORP"));
					result.addString("SUM",			rs.getString("SUM"));

					result.addString("RESULT", "00"); //������*/
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
	
    private String getSelectQuery(boolean dInq) throws BaseException{
        StringBuffer sql = new StringBuffer();
		 
        sql.append("\n SELECT y.CODE_DESC_M MB_NM, ");
		if (dInq) { 
			sql.append("\n    SUBSTR(x.USE_YYYYMM, 1, 4)||'.'||SUBSTR(x.USE_YYYYMM, 5, 2) USE_YYYYMM, "); 
		} else { 
			sql.append("\n    SUBSTR(x.USE_YYYYMM, 1, 4) AS USE_YYYYMM, "); 
		}
        sql.append("\n        MAX(x.REP_MB_NO) MB_NO, ");
        sql.append("\n        TO_CHAR(COUNT( DECODE(x.MEMBER_CLSS, '1', 'A')), '999,999,999') AS INDIV, ");
        sql.append("\n        TO_CHAR(COUNT( DECODE(x.MEMBER_CLSS, '3', 'A')), '999,999,999') AS CORP, ");
        sql.append("\n        TO_CHAR(COUNT( DECODE(x.MEMBER_CLSS, '1', 'A', '3', 'A')), '999,999,999') AS SUM ");
        sql.append("\n FROM  (SELECT a.USE_YYYYMM, ");
        sql.append("\n               a.MEMBER_CLSS, ");
        sql.append("\n               b.REP_MB_NO ");
        sql.append("\n        FROM   BCDBA.TBGFUSEAMT a, ");
        sql.append("\n               BCDBA.TBGFMB b ");
        sql.append("\n        WHERE  a.USE_YYYYMM >= ? ");
        sql.append("\n        AND    a.USE_YYYYMM <= ? ");
        sql.append("\n        AND    a.MB_NO = b.MB_NO) x, ");
        sql.append("\n        BCDBA.TBGFCODEVAL y ");
        sql.append("\n WHERE  x.REP_MB_NO = y.CODE_VALUE ");
        sql.append("\n AND    y.CODE_ID = 'MB_NO' ");
		if (dInq) { 
			sql.append("\n GROUP BY y.CODE_DESC_M, SUBSTR(x.USE_YYYYMM, 1, 4)||'.'||SUBSTR(x.USE_YYYYMM, 5, 2) "); 
		} else { 
			sql.append("\n GROUP BY y.CODE_DESC_M, SUBSTR(x.USE_YYYYMM, 1, 4) "); 
		}

		return sql.toString();
    } 
    

}
