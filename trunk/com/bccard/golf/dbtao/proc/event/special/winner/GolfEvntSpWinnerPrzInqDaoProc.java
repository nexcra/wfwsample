/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntSpWinnerPrzInqDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: �̺�Ʈ����� > Ư���ѷ����̺�Ʈ >��÷Ȯ�� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-09
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.winner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntSpWinnerPrzInqDaoProc extends AbstractProc {
	public static final String TITLE = "�̺�Ʈ����� > Ư���ѷ����̺�Ʈ >�����̺�Ʈ ���";
	/** **************************************************************************
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rset = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";
		String userid = ""; 

		try {
			 

			// ȸ���������̺� ���� �������� ����
			String p_idx 		= data.getString("p_idx");
			String evnt_seq_no 	= data.getString("evnt_seq_no");
			int pidx = 0;
			
			boolean eof = false;
			conn = context.getDbConnection("default", null);
			
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx, evnt_seq_no);
			rset = pstmt.executeQuery();
			
			while(rset.next()){
				
				if(!eof) result.addString("RESULT", "00");
				
				userid = rset.getString("CDHD_ID");
				if(userid.length() > 3){
					userid = "***"+userid.substring(3);
				}else{
					userid = "***";
				}
				
				
				result.addString("usr_nm",		rset.getString("USENAME"));
				result.addString("usr_id",		userid);
				eof = true;
				
				
			}
			if(!eof) result.addString("RESULT","01");
			
			
			
			
			 
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rset != null) rset.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT 													");
			sql.append("\n 		APLC_SEQ_NO											");
			sql.append("\n 		,CDHD_ID											");
			sql.append("\n 		,(SELECT HG_NM AS NAME FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T1.CDHD_ID)as USENAME ");
			sql.append("\n FROM BCDBA.TBGAPLCMGMT T1								");
			sql.append("\n WHERE  LESN_SEQ_NO = ? AND PRZ_WIN_YN = 'Y'				");

		return sql.toString();
	}

}
