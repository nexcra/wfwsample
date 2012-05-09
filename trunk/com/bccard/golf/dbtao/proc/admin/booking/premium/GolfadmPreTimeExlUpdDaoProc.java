/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPreTimeDelDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ�����̾� ƼŸ�� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import jxl.Sheet;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import java.sql.ResultSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmPreTimeExlUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����̾���ŷ ƼŸ�� ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmLessonMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreTimeExlUpdDaoProc() {}
	
	/**
	 * ������ �����̾���ŷ ƼŸ�� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,Sheet sheet) throws BaseException {

		String title = "aaa";
		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		PreparedStatement pstmtChkTime 	= null;	

		DbTaoResult  result =  new DbTaoResult(title);
		ResultSet rsChkTime				= null;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------

			String gr_id = "";				// ������ ���̵�
			String gr_id_pre = "";			// ������ ���̵� ����
			String gr_seq = "10";				// ������ seq
			String bk_date = "";			// ��ŷ����
			String bk_date_seq = "10";		// ��ŷ���� seq
			String bk_time = "1200";			// ��ŷ �ð�
			String bk_time_seq = "";		// ��ŷ�ð� seq
			String gr_cs = "";				// �ڽ�
			String view_yn = "";			// ���⿩��
			
			pstmtChkTime = null;
			rsChkTime = null;
			// ƼŸ�� ��ϵǾ� �ִ��� �˾ƺ��� getChkTimeQuery
			idx = 0;
			pstmtChkTime = conn.prepareStatement(getChkTimeQuery());
			pstmtChkTime.setString(++idx, bk_date_seq);
			pstmtChkTime.setString(++idx, gr_seq);
			pstmtChkTime.setString(++idx, bk_time);
			rsChkTime = pstmtChkTime.executeQuery();
			if ( rsChkTime.next() ){
				bk_time_seq = rsChkTime.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO");
				debug("aaaaa");
//			}else{
//				
			}
			debug("bk_time_seq : " + bk_time_seq);
		
			
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
        return sql.toString();
    }
    
	
	/** ***********************************************************************
	* ��ŷ�ð� �˻�
	************************************************************************ */
	private String getChkTimeQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT WHERE RSVT_ABLE_SCD_SEQ_NO=? AND AFFI_GREEN_SEQ_NO=? AND BOKG_ABLE_TIME=?	");
		return sql.toString();
	}
	
}
