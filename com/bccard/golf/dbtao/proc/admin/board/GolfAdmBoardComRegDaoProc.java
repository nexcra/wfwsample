/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComRegDaoProc
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;

import java.net.InetAddress;
import com.bccard.golf.common.AppConfig;
/** ****************************************************************************
 * Media4th / Golf
 * @author 
 * @version 2009-03-31 
 **************************************************************************** */
public class GolfAdmBoardComRegDaoProc extends AbstractProc {

	public static final String TITLE = "�Խ��� ��� ó��";
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ�������� 
	************************************************************************ */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException  {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int result = 0;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no		= (String)userEtt.getMemId(); 			
			}

			String bbrd_CLSS				= dataSet.getString("BBRD_CLSS");
			String golf_BOKG_FAQ_CLSS		= dataSet.getString("GOLF_BOKG_FAQ_CLSS");
			String titl						= dataSet.getString("TITL");
			String ctnt						= dataSet.getString("CTNT");
			String eps_YN					= dataSet.getString("EPS_YN");
			String annx_FILE_NM				= dataSet.getString("ANNX_FILE_NM");
			String reg_MGR_ID				= admin_no;
			String reg_IP_ADDR				= request.getRemoteAddr();
			String golf_clm_clss			= dataSet.getString("golf_clm_clss");
			
			int res = 0;	
			long maxValue = this.selectArticleNo(context);
			con.setAutoCommit(false);
			
			String sql = this.getSelectQuery("");
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
							
			pstmt.setLong(++pidx, maxValue);
			pstmt.setString(++pidx, bbrd_CLSS);
			pstmt.setString(++pidx, golf_BOKG_FAQ_CLSS);
			pstmt.setString(++pidx, titl);
			pstmt.setString(++pidx, eps_YN);
			pstmt.setString(++pidx, annx_FILE_NM);
			pstmt.setString(++pidx, reg_MGR_ID);
			pstmt.setString(++pidx, reg_IP_ADDR);
			pstmt.setString(++pidx, golf_clm_clss);

			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();


			String serverip = InetAddress.getLocalHost().getHostAddress();	// ����������
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");	   // ���߱� ip ����
			
            /**clob ó��*********************************************************************/
			if (ctnt.length() > 0){
			
				sql = this.getSelectForUpdateQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(1, maxValue);
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

		}catch ( Exception e ) {
			
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
	private String getSelectQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	INSERT INTO BCDBA.TBGBBRD				");
		sql.append("\n	(BBRD_SEQ_NO,							");	// sequence (1)
		sql.append("\n	BBRD_CLSS,								");	// �Խ��ǰ����Ϸù�ȣ
		sql.append("\n	GOLF_BOKG_FAQ_CLSS,						");	// �з��ڵ� : ��ŷFAQ��������ϴºз�	0001:�����̾���ŷ 0002:��3��ŷ 0003:���ֱ׸������� 0004:Sky72�帲�ὺ 0005:���߱׸�������
		sql.append("\n	TITL,									");	// ����
		sql.append("\n	CTNT,									");	// ����
		
		sql.append("\n	EPS_YN,									");	// ���⿩�� : Y: ���� N: �����
		sql.append("\n	ANNX_FILE_NM,							");	// ÷������
		sql.append("\n	REG_MGR_ID,								");	// ��ϰ������Ϸù�ȣ
		sql.append("\n	REG_ATON,								");	// ����Ͻ�
		sql.append("\n	REG_IP_ADDR,							");	// ���IP�ּ�
		sql.append("\n	INQR_NUM, GOLF_CLM_CLSS					");	// ��ȸ��
		sql.append("\n	)										");	
		
								
		sql.append("\n	VALUES(?,?,?,?,EMPTY_CLOB(),								");
		sql.append("\n	?, ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDD'), ?,0, ?	");
		sql.append("\n	)										");

		return sql.toString();
	}
	/** ***********************************************************************
	 * �Խ��ǹ�ȣ ��������
	************************************************************************ */
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select nvl(max(BBRD_SEQ_NO),'0')+1 as BBRD_SEQ_NO from BCDBA.TBGBBRD";
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}	
			
        } catch (Throwable t) {          // SQLException �� ���� ó�� : ������ ������ ������ �߻�
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
            try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return pidx;

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
