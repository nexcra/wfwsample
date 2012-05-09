/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCouponListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ���� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-05-20
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
public class GolfAdmCouponListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmCouponInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponListDaoProc() {}	

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
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			String sql = this.getSelectQuery(search_sel,search_word);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //�����˻�
				
				if (search_sel.equals("ALL")){ //��ü
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("CUPN_SEQ_NO" 			,rs.getLong("CUPN_SEQ_NO") );
					result.addString("CUPN_CLSS" 		,rs.getString("GOLF_RNG_CUPN_CLSS") );
					result.addString("CUPN_NM" 			,rs.getString("CUPN_NM") );
					result.addLong("CUPN_DC_RT" 			,rs.getLong("DC_RT") );
					result.addString("IMG_NM"			,rs.getString("CUPN_IMG") );
					result.addString("REG_PE_ID"			,rs.getString("REG_MGR_ID") );
					result.addString("CORR_PE_ID"			,rs.getString("CHNG_MGR_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("CORR_ATON"			,rs.getString("CHNG_ATON") );
										
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
										
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
    private String getSelectQuery(String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			CUPN_SEQ_NO, GOLF_RNG_CUPN_CLSS, CUPN_NM, DC_RT, CUPN_IMG, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			CUPN_SEQ_NO, GOLF_RNG_CUPN_CLSS, CUPN_NM, DC_RT, CUPN_IMG, REG_MGR_ID, CHNG_MGR_ID,   	");
		sql.append("\n 			TO_CHAR (TO_DATE (REG_ATON, 'YYYYMMDDHH24MISS'), 'YY-MM-DD') ||'('|| SUBSTR (TO_CHAR (TO_DATE (REG_ATON, 'YYYYMMDDHH24MISS'), 'DAY'), 1, 1) ||') ' || TO_CHAR (TO_DATE (REG_ATON, 'YYYYMMDDHH24MISS'), 'HH24:MI') REG_ATON, 	");
		sql.append("\n 			CHNG_ATON 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGCUPNMGMT	");
		sql.append("\n 				WHERE CUPN_SEQ_NO = CUPN_SEQ_NO	");
		
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //�����˻�
			
			if (search_sel.equals("ALL")){ //��ü
				sql.append("\n 	AND (CUPN_NM LIKE ? 	");
				sql.append("\n 	OR DC_RT LIKE ?)	");
				
			} else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
	

		
		sql.append("\n 				ORDER BY CUPN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
