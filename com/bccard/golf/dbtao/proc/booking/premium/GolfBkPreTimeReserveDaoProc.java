/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��� Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfBkPreTimeReserveDaoProc extends AbstractProc {
	
	public static final String TITLE = "��ŷ ��� Ȯ�� ó��";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc ��ŷ ��� Ȯ�� ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPreTimeReserveDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);
			
            // ��Ͽ��� Ȯ��
			String reser_CODE = "0001";
			int idx = 0;
			
			String sql = this.getReserveQuery(); 
            
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("TIME_SEQ_NO") );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){
				reser_CODE = rs.getString("RESER_CODE");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            
            if(reser_CODE.equals("0001")){
            	result = 1;
            }else{
            	result = 0;
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
	 * ��Ͽ��θ� �����Ѵ�.    
	 ************************************************************************ */
	private String getReserveQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT BOKG_RSVT_STAT_CLSS AS RESER_CODE FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=? \n");
		return sql.toString();
	}
 
}
