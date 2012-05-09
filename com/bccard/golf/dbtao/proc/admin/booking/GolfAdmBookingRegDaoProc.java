/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : TpAdmBookingRegDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��ǰ��� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking;

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
 * @version 2009-05-11 
 **************************************************************************** */
public class GolfAdmBookingRegDaoProc extends AbstractProc {

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
		
		//debug("==== GolfAdmBookingRegDaoProc start ===");
		
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

			String booking_NM		= dataSet.getString("Booking_NM"); 
			String booking_CODE		= dataSet.getString("Booking_CODE"); 
			String use_YN			= dataSet.getString("USE_YN"); 
			String p_idx			= dataSet.getString("p_idx"); 
			String mode				= dataSet.getString("mode"); 			//ó������
			
			int res = 0;
			
			
			
			//debug("mode:"+mode);

			//��Ͻ�
			if("ins".equals(mode))
			{
				String sql = this.getSelectQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, booking_CODE);
				pstmt.setString(++pidx, booking_NM);
				pstmt.setString(++pidx, use_YN);
				pstmt.setString(++pidx, admin_no);


				res = pstmt.executeUpdate();
				
				
			}
			else if("upd".equals(mode))
			{
				String sql = this.getSelectUpdQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, booking_CODE);
				pstmt.setString(++pidx, booking_NM);
				pstmt.setString(++pidx, use_YN);
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
			
			
			//debug("==== GolfAdmBookingRegDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfAdmBookingRegDaoProc ERROR ===");
			
			//debug("==== GolfAdmBookingRegDaoProc ERROR ===");
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
		sql.append("\n	(BookingID,														");
		sql.append("\n	Booking_CODE,													");
		sql.append("\n	Booking_NM,												");
		sql.append("\n	USE_YN,														");
		sql.append("\n	RG_SEQ_NO,														");
		sql.append("\n	REG_DATE		)											");

		sql.append("\n	VALUES(BCGOLF.TBBBRDMGMT_BookingID.NEXTVAL,?,?,?,	");
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
		sql.append("\n	Booking_CODE = ?	,												");
		sql.append("\n	Booking_NM = ?	,										");
		sql.append("\n	USE_YN = ? ,												");
		sql.append("\n	UP_SEQ_NO = ? ,														");
		sql.append("\n	MOD_DATE = TO_CHAR(SYSDATE, 'YYYYMMDD')					");
		sql.append("\n	WHERE BookingID = ?	");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCGOLF.TBBBRDMGMT					");
		sql.append("\n	WHERE BookingID = ?	");

		return sql.toString();
	}
	
	
	
}
