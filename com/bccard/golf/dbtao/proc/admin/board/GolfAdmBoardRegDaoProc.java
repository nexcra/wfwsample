/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : TpAdmBoardRegDaoProc
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : ������ �Խ��ǰ��� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-03-31
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

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

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31
 **************************************************************************** */
public class GolfAdmBoardRegDaoProc extends AbstractProc {

	public static final String TITLE = "�Խ��� ���� ��� ó��";
	//private String temporary;
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		//debug("==== GolfAdmBoardRegDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no		= (String)userEtt.getMemNo(); 							
			}
			
			//��ȸ ����
			String search_yn			= dataSet.getString("search_yn"); 		//�˻�����
			
			String search_clss		= "";									//�˻����
			String search_word		= "";									//�˻���
			String sdate				= "";
			String edate				= "";
			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
				sdate	= dataSet.getString("sdate");
				edate	= dataSet.getString("edate");
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");

			String board_nm			= dataSet.getString("BOARD_NM"); 
			String board_code		= dataSet.getString("BOARD_CODE"); 
			String use_yn				= dataSet.getString("USE_YN"); 
			String p_idx					= dataSet.getString("p_idx"); 
			String mode					= dataSet.getString("mode"); 			//ó������
			
			int res = 0;
			
			
			
			//debug("mode:"+mode);

			//��Ͻ�
			if("ins".equals(mode))
			{
				String sql = this.getSelectQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, board_code);
				pstmt.setString(++pidx, board_nm);
				pstmt.setString(++pidx, use_yn);
				pstmt.setString(++pidx, admin_no);


				res = pstmt.executeUpdate();
				
				
			}
			else if("upd".equals(mode))
			{
				String sql = this.getSelectUpdQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, board_code);
				pstmt.setString(++pidx, board_nm);
				pstmt.setString(++pidx, use_yn);
				pstmt.setString(++pidx, admin_no);
				pstmt.setString(++pidx, p_idx);

				res = pstmt.executeUpdate();
				
				
			}
			else if("del".equals(mode))
			{
				String sql = this.getSelectDelQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, p_idx);

				res = pstmt.executeUpdate();
				
				
			}
			
			
			result = new DbTaoResult(TITLE);

			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
			
			//debug("==== GolfAdmBoardRegDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardRegDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardRegDaoProc ERROR ===");
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

		sql.append("\n	INSERT INTO TBBBRDMGMT					");
		sql.append("\n	(BOARDID,														");
		sql.append("\n	BOARD_CODE,													");
		sql.append("\n	BOARD_NM,												");
		sql.append("\n	USE_YN,														");
		sql.append("\n	RG_SEQ_NO,														");
		sql.append("\n	REG_DATE		)											");

		sql.append("\n	VALUES(BCGOLF.TBBBRDMGMT_BOARDID.NEXTVAL,?,?,?,	");
		sql.append("\n	?,																				");
		sql.append("\n	TO_CHAR(SYSDATE, 'YYYYMMDD'))							");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectUpdQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCGOLF.TBBBRDMGMT	SET				");
		sql.append("\n	BOARD_CODE = ?	,												");
		sql.append("\n	BOARD_NM = ?	,										");
		sql.append("\n	USE_YN = ? ,												");
		sql.append("\n	UP_SEQ_NO = ? ,														");
		sql.append("\n	MOD_DATE = TO_CHAR(SYSDATE, 'YYYYMMDD')					");
		sql.append("\n	WHERE BOARDID = ?	");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCGOLF.TBBBRDMGMT					");
		sql.append("\n	WHERE BOARDID = ?	");

		return sql.toString();
	}

	/** ***********************************************************************
	 * �Խ��ǹ�ȣ ��������
	************************************************************************ */
	/*
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select max(BBRD_SEQ_NO)+1 as BBRD_SEQ_NO from BCDBA.TBBBRDMGMT";
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
	*/
	
}
