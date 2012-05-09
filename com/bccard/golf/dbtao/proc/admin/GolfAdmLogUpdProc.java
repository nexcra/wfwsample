/*****************************************************************
*	Ŭ������	:	GolfTaxUnifMgrUpdProc
*	�ۼ���		:	choijaechul
*	����		:	�ֱ��������� �ð� ���� �ϱ� DB Proc 
*	�������	:	bccard
*	�ۼ�����	:	2008.08.19 
* /BCWEB/WAS/bccorpext2/etax/src/com/bccard/etax/dbtao/proc/login/AdmTaxUnifMgrUpdProc.java
************************** �����̷� *********************************
*	����	����		������		�������
*
******************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;

public class GolfAdmLogUpdProc extends AbstractProc { 
	public static final String TITLE = "������ �������� �ð� ����";
	/** *****************************************************************
	 * AdmTaxUnifMgrUpdProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */ 
	public GolfAdmLogUpdProc() { 

	}
	
	/** ***********************************************************************
	* Proc ����.
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		//debug("AdmTaxUnifMgrUpdProc start");
		int result = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {				
			//info("AdmTaxUnifMgrUpdProc >> execute >> ");

			StringBuffer sql = new StringBuffer();
				
			sql.append(" UPDATE BCDBA.TBGMGRINFO  SET ");
			sql.append(" RC_CONN_DATE  = to_char(sysdate,'YYYYMMDD'), ");
			sql.append(" RC_CONN_TIME  = to_char(sysdate,'HH24MISS') ");
			sql.append(" WHERE MGR_ID = ? ");
				
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, data.getString("account"));
			
			result = pstmt.executeUpdate();
		
			if(result > 0) {
				con.commit();
			} else {
				con.rollback();

			}

        } catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}
		//debug("AdmTaxUnifMgrUpdProc end");
		return result;
	}
}
