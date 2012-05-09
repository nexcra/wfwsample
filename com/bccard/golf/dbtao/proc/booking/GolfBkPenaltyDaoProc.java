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
public class GolfBkPenaltyDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPermissionDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPenaltyDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String userId = "";

		try {
			conn = context.getDbConnection("default", null);

			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userId = userEtt.getAccount();
			}
						 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString (1, userId);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );		
					result.addString("BK_LIMIT_ST" 		,rs.getString("BK_LIMIT_ST") );	
					result.addString("BK_LIMIT_ED" 		,rs.getString("BK_LIMIT_ED") );				
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
		
		sql.append("\n	");
		sql.append("\t  SELECT CDHD_ID	\n");
		sql.append("\t  , TO_CHAR(TO_DATE(BOKG_LIMT_FIXN_STRT_DATE), 'YYYY-MM-DD') BK_LIMIT_ST	\n");
		sql.append("\t  , TO_CHAR(TO_DATE(BOKG_LIMT_FIXN_END_DATE), 'YYYY-MM-DD') BK_LIMIT_ED	\n");		
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  WHERE CDHD_ID=? AND BOKG_LIMT_YN='Y'	\n");
		sql.append("\t  AND TO_CHAR(SYSDATE,'YYYYMMDD') BETWEEN BOKG_LIMT_FIXN_STRT_DATE AND BOKG_LIMT_FIXN_END_DATE	\n");

		
		return sql.toString();
    }
}
