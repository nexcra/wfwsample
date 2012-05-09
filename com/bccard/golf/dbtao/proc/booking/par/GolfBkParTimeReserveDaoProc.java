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

package com.bccard.golf.dbtao.proc.booking.par;

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
public class GolfBkParTimeReserveDaoProc extends AbstractProc {
	
	public static final String TITLE = "��ŷ ��� Ȯ�� ó��";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc ��ŷ ��� Ȯ�� ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimeReserveDaoProc() {}	

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
			String rs_YN = "";
			int idx = 0;
			
			String sql = this.getReserveQuery(); 
            
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("BK_DATE") );
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){
				rs_YN = rs.getString("RS_YN");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            
            if(rs_YN.equals("Y")){
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
		sql.append("\t	SELECT 																	\n");
		sql.append("\t	(CASE WHEN MAX_ACPT_PNUM>RS_NUM THEN 'Y' ELSE 'N' END) RS_YN			\n");
		sql.append("\t			FROM(  															\n");
		sql.append("\t			    SELECT MAX_ACPT_PNUM  										\n");
		sql.append("\t			    , (SELECT COUNT(*) RS_NUM FROM BCDBA.TBGRSVTMGMT WHERE ROUND_HOPE_DATE=? AND AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='Y') AS RS_NUM  \n");
		sql.append("\t			    FROM BCDBA.TBGAFFIGREEN T1  								\n");
		sql.append("\t			    WHERE AFFI_GREEN_SEQ_NO=?	 								\n");
		sql.append("\t			)  																\n");
		
		return sql.toString();
	}
 
}
