/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfCodeSelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �ڵ� ����Ʈ �ڽ� ����
*   �������  : golf  
*   �ۼ�����  : 2009-06-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

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
 * Golf
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfadmTopCodeSelDaoProc extends AbstractProc {

	public static final String TITLE = "�ڵ� ����Ʈ �ڽ� ����";
	
	/** *****************************************************************
	 * GolfCodeSelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmTopCodeSelDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, String Code) throws BaseException {
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			con = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n AFFI_GREEN_SEQ_NO, GREEN_NM 	");
			sql.append("\n FROM 	");
			sql.append("\n BCDBA.TBGAFFIGREEN 	");
			sql.append("\n WHERE AFFI_FIRM_CLSS = '1000' 	");
			sql.append("\n ORDER BY GREEN_NM	");									
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			//pstmt.setString(++idx, Code);
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("CD" ,rs.getString("AFFI_GREEN_SEQ_NO") );
					result.addString("CD_NM" ,rs.getString("GREEN_NM") );
					
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
