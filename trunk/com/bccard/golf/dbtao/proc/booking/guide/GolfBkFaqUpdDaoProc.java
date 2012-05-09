/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkFaqUpdDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ��ŷ > ��ŷ ���̵� > ��ȸ�� ������Ʈ ó�� 
*   �������  : Golf
*   �ۼ�����  : 2009-05-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.guide;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16
 **************************************************************************** */
public class GolfBkFaqUpdDaoProc extends AbstractProc {
	
	public static final String TITLE = "��ȸ�� ������Ʈ ó��";
	
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int result = 0;
		Connection con = null;
				
		
		try{
			con = context.getDbConnection("default", null);
			//��ȸ ����
			String idx			= dataSet.getString("idx");
			
			// 01. ��ȸ�� �ø���
			String sql = this.getSelectHit(idx);
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, idx);
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();


			if(result > 0) {
				con.commit();
			} else {
				con.rollback();
			}
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
			 
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. - ��ȸ�� �ø���
	************************************************************************ */
	private String getSelectHit(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n ");
		sql.append("\t UPDATE BCDBA.TBGBBRD				\n");
		sql.append("\t SET INQR_NUM=INQR_NUM+1 			\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?				\n");

		return sql.toString();
	}
}
