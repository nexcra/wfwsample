/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfGoodFoodSelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ���� �αٰ����� ���� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-06-22
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfGoodFoodSelDaoProc extends AbstractProc {

	public static final String TITLE = "���� �αٰ����� ���� ����Ʈ";
	
	/** *****************************************************************
	 * GolfGoodFoodSelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfGoodFoodSelDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			con = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n 	AFFI_GREEN_SEQ_NO, LTRIM (MAX (SYS_CONNECT_BY_PATH (GREEN_NM, ',')), ',') GREEN_NM	");
			sql.append("\n FROM (SELECT AFFI_GREEN_SEQ_NO, 	");
			sql.append("\n 			GREEN_NM, ROW_NUMBER () OVER (PARTITION BY AFFI_GREEN_SEQ_NO ORDER BY GREEN_NM) CNT 	");
			sql.append("\n 		  FROM (SELECT TGFDI.AFFI_GREEN_SEQ_NO, TGF.GREEN_NM 	");
			sql.append("\n 					FROM BCDBA.TBGETHSNGHBGREEN TGFDI, BCDBA.TBGAFFIGREEN TGF 	");
			sql.append("\n 					WHERE TGFDI.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO 	");
			sql.append("\n 					AND TGFDI.AFFI_ETHS_SEQ_NO = ? 	");
			sql.append("\n 					) ");
			sql.append("\n 			) ");
			sql.append("\n START WITH CNT = 1	");
			sql.append("\n CONNECT BY PRIOR CNT = CNT - 1	");
			sql.append("\n GROUP BY AFFI_GREEN_SEQ_NO	");
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("FD_SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GF_SEQ_NO" ,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("GF_NM" ,rs.getString("GREEN_NM") );
					
					result.addString("RESULT", "00"); //������
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
}
