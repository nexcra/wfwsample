/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeTimeUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� �����ð� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

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
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeTimeUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �帲 ���������� �����ð� ���� ó��";

	/** *****************************************************************
	 * GolfAdmRangeTimeUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeTimeUpdDaoProc() {}
	
	/**
	 * ������ �帲 ���������� �����ð� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] rsvttime_sql_no, String[] start_hh, String[] start_mi, String[] end_hh, String[] end_mi, String[] day_rsvt_num) throws DbTaoException  {
		
		int result = 0;
		int iCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String flag = "";
			
			/*
			 * '���� ����'���� �����ϼ��ý� �ش� �����常 ó����.
			 *�ð� �� �ο�����/���������ο��� �����帶�� ���� �� �� ������ ���� ó�� 
			 */
			if (data.getString("SLS_END_YN").equals("Y")){
				flag = "Y";
			}else if (data.getString("HOLY_YN").equals("Y")){
				flag = "H";
			}	
			
            /*****************************************************************************/
			
			sql = this.getInsertQuery1();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			//pstmt.setString(++idx, data.getString("SLS_END_YN") ); 
			pstmt.setString(++idx, flag );
			pstmt.setLong(++idx, data.getLong("RSVT_TOTAL_NUM") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setLong(++idx, data.getLong("RSVTDIALY_SQL_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
            for (int i = 0; i < rsvttime_sql_no.length; i++) {				
				if (rsvttime_sql_no[i] != null && rsvttime_sql_no[i].length() > 0) {
		            
		            sql = this.getInsertQuery2();//Insert Query
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					
					pstmt.setString(++idx, start_hh[i]+start_mi[i] ); 
					pstmt.setString(++idx, end_hh[i]+end_mi[i] ); 
					pstmt.setString(++idx, day_rsvt_num[i] );
					pstmt.setString(++idx, data.getString("ADMIN_NO") );
					pstmt.setString(++idx, rsvttime_sql_no[i] );
					
					iCount += pstmt.executeUpdate();
			    }
            }
            /*****************************************************************************/
            
            if(result > 0 && iCount == rsvttime_sql_no.length) {
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
    private String getInsertQuery1(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");		
		sql.append("UPDATE BCDBA.TBGRSVTABLESCDMGMT SET	\n");
		sql.append("\t  RESM_YN=?, DLY_RSVT_ABLE_PERS=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE RSVT_ABLE_SCD_SEQ_NO=?	\n");		
        return sql.toString();
    }
    
    private String getInsertQuery2(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");		
		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
		sql.append("\t  RSVT_STRT_TIME=?, RSVT_END_TIME=?, DLY_RSVT_ABLE_PERS_NUM=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?	\n");		
        return sql.toString();
    }
}
