/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��� Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfMemChgFormDaoProc extends AbstractProc {
	
	public static final String TITLE = "��ŷ ��� Ȯ�� ó��";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc ��ŷ ��� Ȯ�� ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfMemChgFormDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public String execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws DbTaoException  {

		String result = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String userId = "";	

		try {
			conn = context.getDbConnection("default", null);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
			}
			
            // 02. ���Ŭ���� Ȯ��
			String sql = this.getMemberClssQuery(); 
            
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){
				result = rs.getString("MEMBER_CLSS");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
                        

		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	
	

    
	/** ***********************************************************************
	 * ȸ�����θ� �����Ѵ�.    
	 ************************************************************************ */
	private String getMemberClssQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("	SELECT MEMBER_CLSS FROM BCDBA.UCUSRINFO WHERE ACCOUNT=?	\n");
		
		return sql.toString();
	}
 
}
