/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComUpdDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * Media4th / Golf
 * @author 
 * @version 2009-03-31 
 **************************************************************************** */
public class GolfAdmBoardComUpdDaoProc extends AbstractProc {

	public static final String TITLE = "�Խ��� ���� ó��";
	//private String temporary;

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
		
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
				
		//debug("==== GolfAdmBoardComUpdDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			//��ȸ ����
			String search_yn			= dataSet.getString("search_yn"); 		//�˻�����
			
			String search_clss			= "";									//�˻����
			String search_word			= "";									//�˻���
			if("Y".equals(search_yn)){
				search_clss		= dataSet.getString("search_clss"); 		// �˻���
				search_word		= dataSet.getString("search_word"); 		// ����˻�����
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");

			String idx					= dataSet.getString("idx");
			String titl					= dataSet.getString("TITL");
			String ctnt					= dataSet.getString("CTNT").trim();
			String eps_YN				= dataSet.getString("EPS_YN");
			String annx_FILE_NM			= dataSet.getString("ANNX_FILE_NM");
			String golf_clm_clss		= dataSet.getString("golf_clm_clss");
			
			int res = 0;	
			con.setAutoCommit(false);
			
			String sql = this.getSelectUpdQuery("");
			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(++pidx, titl);
			pstmt.setString(++pidx, eps_YN);
			pstmt.setString(++pidx, annx_FILE_NM);
			pstmt.setString(++pidx, admin_id);
			pstmt.setString(++pidx, golf_clm_clss);
			pstmt.setString(++pidx, idx);

			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();

			String serverip = InetAddress.getLocalHost().getHostAddress();	// ����������
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");	   // ���߱� ip ����
			
            /**clob ó��*********************************************************************/
			if (ctnt.length() > 0){
			
				sql = this.getSelectForUpdateQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, idx);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					java.sql.Clob clob = rs.getClob("CTNT");
					if(serverip.equals(devip)){
						writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
					}else{
//						writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
					}
					reader = new CharArrayReader(ctnt.toCharArray());
					
					char[] buffer = new char[1024];
					int read = 0;
					while ((read = reader.read(buffer,0,1024)) != -1) {
						writer.write(buffer,0,read);
					}
					writer.flush();
				}
				if (rs  != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
		

			if(result > 0) {
				con.commit();
			} else {
				con.rollback();
			}

			//debug("==== GolfAdmBoardComUpdDaoProc ����ó�� End ===");
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComUpdDaoProc ERROR 1 ===");
			
			//debug("==== GolfAdmBoardComUpdDaoProc ERROR 2 ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}


	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectUpdQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	UPDATE BCDBA.TBGBBRD	SET				");
		sql.append("\n	TITL = ?	,							");
		sql.append("\n	CTNT = EMPTY_CLOB()	,					");
		sql.append("\n	EPS_YN = ?	,							");
		sql.append("\n	ANNX_FILE_NM = ?	,					");
		sql.append("\n	CHNG_MGR_ID = ?	,						");
		sql.append("\n	CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDD'), GOLF_CLM_CLSS=?	");
		sql.append("\n	WHERE BBRD_SEQ_NO = ?					");

		return sql.toString();
	}	

    
	/** ***********************************************************************
    * CLOB Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectForUpdateQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CTNT FROM BCDBA.TBGBBRD \n");
        sql.append("WHERE BBRD_SEQ_NO = ? \n");
        sql.append("FOR UPDATE \n");
		return sql.toString();
    }
	
}
