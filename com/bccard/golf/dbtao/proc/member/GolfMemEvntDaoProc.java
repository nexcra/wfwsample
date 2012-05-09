/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemEvntDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó��
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0   
******************************************************************************/
public class GolfMemEvntDaoProc extends AbstractProc {

	public static final String TITLE = "�̺�Ʈ Ȯ��";

	public GolfMemEvntDaoProc() {}
	
	public DbTaoResult execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}


			String evnt_no = data.getString("evntNo" ).trim();	
			String jumin_no = data.getString("juminNo").trim();	


			// �̺�Ʈ Ȯ�� ���� 
			sql = this.getQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1,  evnt_no );
			pstmt.setString(2,  jumin_no );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				result.addString("PWIN_DATE" 			,rs.getString("PWIN_DATE") );
				result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
				result.addString("TO_DATE" 			,rs.getString("TODATE") ); 
				result.addString("END_DATE" 			,rs.getString("ENDDATE") ); //��������������

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
	* �̺�Ʈ Ȯ�� ���� 
	************************************************************************ */
	private String getQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT PWIN_DATE,JUMIN_NO, TO_CHAR(SYSDATE,'YYYYMMDD') TODATE , TO_CHAR(TO_DATE(PWIN_DATE) + 30 ,'YYYYMMDD') ENDDATE  \n");
		sql.append("\t	FROM BCDBA.TBEVNTLOTPWIN  WHERE  SITE_CLSS ='10'  AND EVNT_NO = ?	AND JUMIN_NO= ? \n");

		return sql.toString();
	}


}
