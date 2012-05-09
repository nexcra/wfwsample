/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGiftRegDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ����ǰ���� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-08-24
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* GOLFLOUNG		20100512	������	����ǰ ����
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

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

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-15
 **************************************************************************** */
public class GolfAdmGiftRegDaoProc extends AbstractProc {

	public static final String TITLE = "����ǰ ó��";
//	private String temporary;
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		//debug("==== GolfAdmGiftRegDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			//��ȸ ����
			/*
			String search_yn			= dataSet.getString("search_yn"); 		//�˻�����
			
			String search_clss		= "";									//�˻����
			String search_word		= "";									//�˻���

			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
			*/
			
			String cdhd_id					= dataSet.getString("cdhd_id"); 
			String rcvr_nm					= dataSet.getString("rcvr_nm"); 
			String golf_tmnl_gds_code		= dataSet.getString("golf_tmnl_gds_code"); 
			String hp_ddd_no				= dataSet.getString("hp_ddd_no");
			String hp_tel_hno				= dataSet.getString("hp_tel_hno"); 
			String hp_tel_sno				= dataSet.getString("hp_tel_sno");
			String zp						= dataSet.getString("zp"); 	
			String addr						= dataSet.getString("addr"); 	
			String dtl_addr					= dataSet.getString("dtl_addr");
			String addr_clss				= dataSet.getString("addr_clss");
			String memo_ctnt				= dataSet.getString("memo_ctnt");
			String snd_yn					= dataSet.getString("snd_yn"); 
			String mode						= dataSet.getString("mode"); 			//ó������
			String p_idx					= dataSet.getString("p_idx");
			String acrg_cdhd_jonn_date		= dataSet.getString("acrg_cdhd_jonn_date");
			
			int res = 0;	
			long maxValue = this.selectArticleNo(context);
			con.setAutoCommit(false);
			
			//String acrg_cdhd_jonn_date = this.selectJoinDate(context ,cdhd_id);
			//con.setAutoCommit(false);

			//��Ͻ�
			if("ins".equals(mode))
			{
				String sql = this.getSelectQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setLong(++pidx, maxValue);
				pstmt.setString(++pidx, cdhd_id);
				pstmt.setString(++pidx, rcvr_nm);
				pstmt.setString(++pidx, golf_tmnl_gds_code);
				pstmt.setString(++pidx, hp_ddd_no);
				pstmt.setString(++pidx, hp_tel_hno);
				pstmt.setString(++pidx, hp_tel_sno);
				pstmt.setString(++pidx, zp);
				pstmt.setString(++pidx, addr);
				pstmt.setString(++pidx, dtl_addr);
				pstmt.setString(++pidx, addr_clss);
				pstmt.setString(++pidx, memo_ctnt);
				pstmt.setString(++pidx, acrg_cdhd_jonn_date);
				//pstmt.setString(++pidx, admin_id);

				res = pstmt.executeUpdate();
							
			}
			else if("upd".equals(mode))
			{
				String sql = this.getSelectUpdQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, snd_yn);
				pstmt.setString(++pidx, memo_ctnt);
				pstmt.setString(++pidx, admin_id);
				pstmt.setString(++pidx, rcvr_nm);
				pstmt.setString(++pidx, golf_tmnl_gds_code);
				pstmt.setString(++pidx, hp_ddd_no);
				pstmt.setString(++pidx, hp_tel_hno);
				pstmt.setString(++pidx, hp_tel_sno);
				pstmt.setString(++pidx, zp);
				pstmt.setString(++pidx, addr);
				pstmt.setString(++pidx, dtl_addr);
				pstmt.setString(++pidx, addr_clss);
				pstmt.setString(++pidx, p_idx);
				
				res = pstmt.executeUpdate();
				
			}
			else if("del".equals(mode))
			{
				String sql = this.getSelectDelQuery(); 
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, p_idx);
				
				res = pstmt.executeUpdate();
				
			}
			
			result = new DbTaoResult(TITLE);

			if ( res == 1 ) {
				result.addString("RESULT", "00");
				con.commit();
			}else{
				result.addString("RESULT", "01");
				con.rollback();
			}
			
			
			//debug("==== GolfAdmGiftRegDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfAdmGiftRegDaoProc ERROR ===");
			if(con != null) try{ con.rollback(); }catch(Exception ignored){}
			
			//debug("==== GolfAdmGiftRegDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(pstmt1 != null) pstmt1.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
				
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGCDHDRIKMGMT					");
		sql.append("\n	(SEQ_NO,									");
		sql.append("\n	CDHD_ID,									");
		sql.append("\n	RCVR_NM,											");
		sql.append("\n	GOLF_TMNL_GDS_CODE,											");
		sql.append("\n	HP_DDD_NO,										");
		sql.append("\n	HP_TEL_HNO,									");
		sql.append("\n	HP_TEL_SNO,										");
		sql.append("\n	ZP,								");
		sql.append("\n	ADDR,								");
		sql.append("\n	DTL_ADDR,								");
		sql.append("\n	NW_OLD_ADDR_CLSS,								");
		sql.append("\n	MEMO_CTNT,								");
		sql.append("\n	ACRG_CDHD_JONN_DATE,								");
		sql.append("\n	SND_YN,								");
		sql.append("\n	APLC_ATON		)							");
		sql.append("\n	VALUES(?,?,?,?,?,?,	");		
		sql.append("\n	?,?,?,?,?,?,?,'N',											");
		sql.append("\n	TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))				");

		return sql.toString();
		
	}
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectUpdQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGCDHDRIKMGMT	SET				");
		sql.append("\n	SND_YN = ?	,										");
		sql.append("\n	MEMO_CTNT = ? ,												");
		sql.append("\n	CHNG_MGR_ID = ? ,														");
		sql.append("\n	CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')					");		
		sql.append("\n	, RCVR_NM=?, GOLF_TMNL_GDS_CODE=?, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?, ZP=?, ADDR=?, DTL_ADDR=?, NW_OLD_ADDR_CLSS=?		");
		sql.append("\n	WHERE SEQ_NO = ?	");

		return sql.toString();
	}

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		sql.append("\n	DELETE FROM BCDBA.TBGCDHDRIKMGMT WHERE SEQ_NO=?	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * �Խ��ǹ�ȣ ��������
	************************************************************************ */
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select nvl(max(SEQ_NO),'0')+1 as SEQ_NO from BCDBA.TBGCDHDRIKMGMT";
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
	 * ��ȿ�����ϰ�������
	************************************************************************ */
	private String selectJoinDate(WaContext context, String cdhd_id) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select ACRG_CDHD_JONN_DATE from BCDBA.TBGGOLFCDHD WHERE CDHD_ID = ? ";
        String join_date = "";
        try {
        	con = context.getDbConnection("default", null);
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
				join_date = rset1.getString(1);
			}	
			
        } catch (Throwable t) {          // SQLException �� ���� ó�� : ������ ������ ������ �߻�
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
            try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return join_date;

	}
	
}
