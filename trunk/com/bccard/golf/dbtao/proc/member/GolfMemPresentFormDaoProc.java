/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemCyberInfoDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����� Ŭ���Ͽ콺 > ���̹� �Ӵ� �ȳ� > ���̹� �Ӵ� �ȳ�
*   �������  : golf
*   �ۼ�����  : 2009-05-19
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
public class GolfMemPresentFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreGrViewDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfMemPresentFormDaoProc() {}	

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
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
			}
			 
			conn = context.getDbConnection("default", null);
						 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();
			
			// �Է°� (INPUT)  
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId ); 	
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				
				while(rs.next())  {
					
					String addrClss = rs.getString("NW_OLD_ADDR_CLSS");
					
					if ( addrClss == null || addrClss.trim().equals("")){
						addrClss = "1";
					}
					
					result.addString("NAME" 		,rs.getString("NAME"));
					
					if ( !addrClss.equals("2") ){
						result.addString("ZIPADDR" 		,rs.getString("ZIPADDR"));
						result.addString("DETAILADDR" 	,rs.getString("DETAILADDR"));
					}else {
						result.addString("ZIPADDR" 		,rs.getString("DONG_OVR_NEW_ADDR"));
						result.addString("DETAILADDR" 	,rs.getString("DONG_BLW_NEW_ADDR"));						
					}
						
					result.addString("ZP1" 			,rs.getString("ZP1"));
					result.addString("ZP2" 			,rs.getString("ZP2"));
					result.addString("ADDRCLSS" 	,addrClss );
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
		sql.append("\n");  
		sql.append("\t  SELECT NAME, SUBSTR(ZIPCODE,1,3) ZP1, SUBSTR(ZIPCODE,5,3) ZP2, ZIPADDR,	\n");  
		sql.append("\t  DETAILADDR, NW_OLD_ADDR_CLSS, DONG_OVR_NEW_ADDR, DONG_BLW_NEW_ADDR	\n");
		sql.append("\t  FROM BCDBA.UCUSRINFO	\n");  
		sql.append("\t  WHERE ACCOUNT=?	\n");
		return sql.toString();
    }
}
