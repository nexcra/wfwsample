/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntKvpDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ����ȸ > ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.kvp;

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
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntKvpPopDaoProc extends AbstractProc {
	
	public GolfEvntKvpPopDaoProc() {}	

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

			String payAmount = "0";
			String strPayAmount = "0";
			
			String idx = data.getString("idx");
			String sql = this.getMaxIdxQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, GolfUtil.lpad(idx, 4, "0"));
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()){
				
				payAmount = rs.getString("GOLF_CMMN_CODE_NM");
				
				if(!GolfUtil.empty(payAmount)){
					strPayAmount = GolfUtil.comma(payAmount);
				}
				
				result.addString("payAmount", payAmount);
				result.addString("grdName", rs.getString("EXPL"));
				result.addString("strPayAmount", strPayAmount);
				result.addString("RESULT", "00");
			}else{

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
    * ��û���̺��� max_idx ��������
    ************************************************************************ */
    private String getMaxIdxQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT GOLF_CMMN_CODE_NM, EXPL FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0065' AND GOLF_CMMN_CODE=?	");		
		return sql.toString();
    }

}
