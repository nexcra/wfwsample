/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardReListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ����Խ��� ���� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.bbs;

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
public class GolfAdmBoardComtListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmBoardReListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBoardComtListDaoProc() {}	

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
			
			// ������ �������� ���� ��������(�뷮����) 
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("REPLY_NO" 				,rs.getLong("REPY_SEQ_NO") );
					result.addString("REPLY_CTNT" 			,rs.getString("REPY_CTNT") );
					result.addString("REG_ATON" 			,rs.getString("REG_ATON") );
					result.addString("REG_ID" 				,rs.getString("RGS_PE_ID") );
					result.addString("HAN_NM" 				,rs.getString("HG_NM") );
					result.addString("MOBILE" 				,rs.getString("MOBILE") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("MEMLEVEL" 				,rs.getString("MEMLEVEL") );

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

		sql.append("\n SELECT	");
		sql.append("\n 	TBR.REPY_SEQ_NO, TBR.REPY_CTNT, TO_CHAR(TO_DATE(TBR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI:SS') REG_ATON, TBR.RGS_PE_ID,	");
		sql.append("\n 	NVL(TGU.HG_NM,'�մ�') HG_NM	");

		sql.append("\n 	  , TGU.CDHD_ID, TGU.MOBILE				");
		sql.append("\n 	  ,  g2.GOLF_CMMN_CODE_NM as MEMLEVEL						");
		//sql.append("\n 	  , CASE WHEN g2.CDHD_SQ2_CTGO='0001' THEN 'VIP'			");
		//sql.append("\n 	    WHEN g2.CDHD_SQ2_CTGO='0002' THEN '���'			");
		//sql.append("\n 			    WHEN g2.CDHD_SQ2_CTGO='0003' THEN '�췮'	");
		//sql.append("\n 			    ELSE g2.CDHD_SQ2_CTGO						");
		//sql.append("\n 			    END as MEMLEVEL								");

		sql.append("\n FROM 	");
		sql.append("\n BCDBA.TBGBBRDREPY TBR, BCDBA.TBGGOLFCDHD TGU 						");
		sql.append("\n   inner join BCDBA.TBGGOLFCDHDGRDMGMT g on g.CDHD_ID=TGU.CDHD_ID		");
		//sql.append("\n   inner join BCDBA.TBGGOLFCDHDCTGOMGMT g2 on g2.CDHD_CTGO_SEQ_NO=g.CDHD_CTGO_SEQ_NO		");
		sql.append("\n   inner join BCDBA.TBGCMMNCODE g2 on g2.GOLF_CMMN_CODE=g.CDHD_CTGO_SEQ_NO 		");
		
		sql.append("\n WHERE TBR.RGS_PE_ID = TGU.CDHD_ID	");
		sql.append("\n AND TBR.REPY_CLSS = '0001'	");
		sql.append("\n AND TBR.BBRD_SEQ_NO = ?	");
		sql.append("\n AND g2.GOLF_CMMN_CLSS='0052'		");
		sql.append("\n ORDER BY TBR.REPY_SEQ_NO DESC	");

		return sql.toString();
    }
    
}
