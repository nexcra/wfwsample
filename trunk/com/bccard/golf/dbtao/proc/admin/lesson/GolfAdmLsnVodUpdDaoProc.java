/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLsnVodUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ���������� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-22
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnVodUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ ���������� ���� ó��";

	/** *****************************************************************
	 * GolfAdmLsnVodUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnVodUpdDaoProc() {}
	
	/**
	 * ������ ���������� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
            String vod_nm = data.getString("VOD_NM");
            String img_nm = data.getString("IMG_NM");
			
			sql = this.getInsertQuery(vod_nm, img_nm);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(vod_nm))	pstmt.setString(++idx, vod_nm);
			if (!GolfUtil.isNull(img_nm))	pstmt.setString(++idx, img_nm);
			pstmt.setString(++idx, data.getString("VOD_CLSS") ); 			
			pstmt.setString(++idx, data.getString("VOD_LSN_CLSS") ); 
			pstmt.setString(++idx, data.getString("TITL") );
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("BEST_YN") );
			pstmt.setString(++idx, data.getString("NEW_YN") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			
			pstmt.setString(++idx, data.getString("SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
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
    private String getInsertQuery(String vod_nm, String img_nm){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGMVPTMGMT SET	\n");
		if (!GolfUtil.isNull(vod_nm)) sql.append("\t 	 MVPT_ANNX_FILE_PATH=?,	");
		if (!GolfUtil.isNull(img_nm)) sql.append("\t 	 ANNX_IMG=?,	");
		sql.append("\t  GOLF_MVPT_CLSS=?, GOLF_MVPT_LESN_CLSS=?, TITL=?, CTNT=?, BEST_YN=?, ANW_BLTN_ARTC_YN=?, 	\n");
		sql.append("\t  CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS') 	\n");
		sql.append("\t WHERE GOLF_MVPT_SEQ_NO=?	\n");
        return sql.toString();
    }
}
