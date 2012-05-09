/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmPricesAllUpdFormDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �ü� ��ü����
*   �������  : golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*2009-11-24   1.1    ������   �ü��� ���������� 0 �ʰ��� ������ ������ ����
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lounge;

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
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmPricesAllUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmPricesAllUpdFormDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmPricesAllUpdFormDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("QUT_DATE"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("GREEN_MEMRTK_NM_SEQ_NO" 			,rs.getLong("GREEN_MEMRTK_NM_SEQ_NO") );
					result.addString("GREEN_MEMRTK_NM" 			,rs.getString("GREEN_MEMRTK_NM") );
					result.addLong("QUT" 		,rs.getLong("QUT") );
					
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
        
        sql.append("\n SELECT");
		sql.append("\n 	TGM.GREEN_MEMRTK_NM_SEQ_NO, TGM.GREEN_MEMRTK_NM,	 ");
		sql.append("\n 	NVL ((SELECT QUT	 			");
		sql.append("\n 	      FROM (SELECT * 			"); 
		sql.append("\n 	      		FROM BCDBA.TBGGREENMEMRTKQUTMGMT  "); 
		sql.append("\n 	        	WHERE QUT_DATE <= ? AND QUT > 0 ORDER BY QUT_DATE DESC) "); 
		sql.append("\n 	      WHERE ROWNUM < 2 AND TGM.GREEN_MEMRTK_NM_SEQ_NO = GREEN_MEMRTK_NM_SEQ_NO) , 0) QUT "); 
		sql.append("\n FROM BCDBA.TBGGREENMEMRTKMGMT TGM 	");
		sql.append("\n ORDER BY TGM.GREEN_MEMRTK_NM_SEQ_NO 	");
		
		return sql.toString();
    }
}
