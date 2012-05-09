/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubMenuListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ �޴� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-06-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

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
public class GolfClubMyInfoInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfClubMenuListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMyInfoInqDaoProc() {}	

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
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {					
					result.addLong("CLUB_CDHD_SEQ_NO"		,rs.getLong("CLUB_CDHD_SEQ_NO") );
					result.addString("GREET_CTNT"			,rs.getString("GREET_CTNT") );
					
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
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 		CLUB_CDHD_SEQ_NO, GREET_CTNT 	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBCDHDMGMT	");
		sql.append("\n WHERE CLUB_SEQ_NO = ?	");
		sql.append("\n AND CDHD_ID = ?	");
		sql.append("\n AND SECE_YN = 'N'	");
		
		return sql.toString();
    }
}