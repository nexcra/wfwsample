/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodeSelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �ڵ� ����Ʈ �ڽ� ����
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mania;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

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
public class GolfManiaCodeSelDaoProc extends AbstractProc {

	public static final String TITLE = "������ �ڵ� ����Ʈ �ڽ� ����";
	
	/** *****************************************************************
	 * GolfAdmCodeSelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfManiaCodeSelDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, String Code, String use_yn) throws BaseException {
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			con = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n 	*	");
			sql.append("\n FROM 	");
			sql.append("\n BCDBA.CKIND_MANIA_LIMUZINE	");
			sql.append("\n ORDER BY NO	");									
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());

			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("CD" ,rs.getString("CODE") );
					result.addString("CD_NM" ,rs.getString("NAME") );
					result.addString("PR_CE" ,rs.getString("PRICE") );
					result.addString("PR_CE2" ,rs.getString("PRICE2") );
					result.addString("PR_CE3" ,rs.getString("PRICE3") );
					
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
