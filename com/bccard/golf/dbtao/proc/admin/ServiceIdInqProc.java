/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ServiceIdInqProc
*   �ۼ���    : �强��
*   ����      : �������� ����
*   �������  : 
*   �ۼ�����  : 2010-12-29  
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.bccard.waf.core.WaContext;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;


public class ServiceIdInqProc extends AbstractProc{

	public static final String TITLE = "�������� ���� PROC";

	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public String execute(WaContext context, String NewServiceId) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection con = null;
		String NewServiceYn = "N";

		try{

			String sql = this.getSelectQuery();

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, NewServiceId);
			rs = pstmt.executeQuery();

			if(rs != null && rs.next()) {
				NewServiceYn = rs.getString("NW_SVC_YN");  
			}
			
		}catch ( Exception e ) {
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return NewServiceYn;
	}

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
        sql.append("\n  SELECT MAX(NW_SVC_YN) NW_SVC_YN ");      
        sql.append("\n  FROM  (SELECT CASE WHEN NVL(APPL_DATE,'99991231') <= TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ");
        sql.append("\n                     ELSE 'N' ");
        sql.append("\n                END  NW_SVC_YN ");
        sql.append("\n         FROM   BCDBA.TBPSTCAPPL ");
        sql.append("\n         WHERE  NW_SVC_ID = ? ");
        sql.append("\n         UNION ");
        sql.append("\n         SELECT 'N' NW_SVC_YN ");
        sql.append("\n         FROM   DUAL ) ");
		return sql.toString();
	} 

}
