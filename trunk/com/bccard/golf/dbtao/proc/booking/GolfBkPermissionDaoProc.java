/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrMapViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ �������� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfBkPermissionDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPermissionDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPermissionDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, String memb_id, String permissionColum) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String limit_yn = "N";	// ���ٱ���

		try {
			conn = context.getDbConnection("default", null);
					
			//��ȸ ----------------------------------------------------------	 		
			String sql = this.getSelectQuery(permissionColum);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString (1, memb_id);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {
					// ��� �� �ϳ��� ���ٱ����� ������ ���� �� �� �ִ�.
					if(rs.getString(permissionColum).equals("Y")){
						limit_yn = "Y";
					}
				}
				
				result.addString("RESULT", "00"); //������
				result.addString("LIMT_YN", limit_yn); //������
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
    private String getSelectQuery(String permissionColum){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT \n");
		sql.append("\t  "+permissionColum+"        		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDBNFTMGMT T3 ON T2.CDHD_SQ2_CTGO=T3.CDHD_SQ2_CTGO	\n");
		sql.append("\t  WHERE T1.CDHD_ID=?	\n");
		
		return sql.toString();
    }
}
