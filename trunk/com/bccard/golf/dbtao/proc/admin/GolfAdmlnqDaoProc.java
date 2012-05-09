/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointAdmlnqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ž����Ʈ ������ ��� �޴� �������� 
*   �������  : Golf
*   �ۼ�����  : 2009-05-06  
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;

public class GolfAdmlnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "�񾾰��� ������  ��ܸ޴� ��������";
	/** *****************************************************************
	 * GolfPointAdmLoginlnqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmlnqDaoProc() { 

	}
	
	/** ***********************************************************************
	* Proc ����.
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public TaoResult execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		DbTaoResult result = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {				
	
			result = new DbTaoResult(TITLE);
			//debug("GolfPointAdmlnqDaoProc start ");
			String account		= data.getString("account");	// ���Ǿ��̵�
			String log_p_idx	= data.getString("log_p_idx");	// PK
			
			StringBuffer sql = new StringBuffer();

			sql.append("\n").append(" select t0.SQ1_LEV_SEQ_NO as m0_idx, max(t0.SQ1_LEV_MENU_NM) as m0_name ");
			sql.append("\n").append(" from BCDBA.TBGSQ1MENUINFO t0 inner join BCDBA.TBGSQ2MENUINFO t1 on t0.SQ1_LEV_SEQ_NO = t1.SQ1_LEV_SEQ_NO ");
			sql.append("\n").append(" inner join BCDBA.TBGSQ3MENUINFO t2 on t1.SQ2_LEV_SEQ_NO = t2.SQ2_LEV_SEQ_NO inner join BCDBA.TBGSQ3MENUMGRPRIVFIXN t3 on t2.SQ3_LEV_SEQ_NO= t3.SQ3_LEV_SEQ_NO ");
			sql.append("\n").append(" where t3.MGR_ID = ? ");
			sql.append("\n").append(" group by t0.SQ1_LEV_SEQ_NO ");

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	

			int idx = 0; 		
			pstmt.setString(++idx, account);
			
			rs = pstmt.executeQuery();

			int ret = 0;
			if(rs != null )
			{
				while(rs.next())  {
			
				result.addString("m0_idx",		rs.getString("m0_idx"));
				result.addString("m0_name", 	rs.getString("m0_name"));
								
				ret++;
				}
			}
			  
			
			
			//���������ӳ�¥				  
			  StringBuffer sql_a = new StringBuffer();
			  sql_a.append("\n SELECT RC_CONN_DATE, RC_CONN_TIME  from BCDBA.TBGMGRINFO  ");
			  sql_a.append("\n WHERE MGR_ID = ?	");		
			  pstmt = con.prepareStatement(sql_a.toString());				
			  pstmt.setString(1, account);  
			  rs = pstmt.executeQuery();
			  if(rs != null) {	
				  while(rs.next())  {
				  result.addString("VISIT_DATE"       	, rs.getString("RC_CONN_DATE"));
				  result.addString("VISIT_TIME"       	, rs.getString("RC_CONN_TIME"));
				  }
			  }
			
			
			
			
			
			if(ret == 0){
				result.addString("RESULT","01");
				
			} else {
				result.addString("RESULT","00");	
			}
			
			//debug("GolfPointAdmlnqDaoProc end");

        } catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}


}
