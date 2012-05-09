/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ����Ʈ 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.benest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntBnstPayDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBnstPayDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		String sql = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		int idx = 0;
		ResultSet rs = null;

		try {
			conn = context.getDbConnection("default", null);
			String aplc_seq_no = data.getString("aplc_seq_no");
			int etc_cnt = 0;
			

			// ������ ���� ���� ����	
			for(int i=1; i<20; i++){
				if(data.getString("seq_no"+i).equals("Y")){
					sql = this.getUpdCompnQuery();
					pstmt = conn.prepareStatement(sql.toString());
					idx = 0;
					pstmt.setString(++idx, aplc_seq_no);
					pstmt.setInt(++idx, i);
					result += pstmt.executeUpdate();
				}
			}
			
			// �������߿� ������ �ȵȻ���� ������ �ѱ��� �ʴ´�. getEtcCntQuery
			sql = this.getEtcCntQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, aplc_seq_no);

			rs = pstmt.executeQuery();
			
			if ( rs != null ) {
				rs.next();
				etc_cnt = rs.getInt("CNT");
			}
			
			// �̺�Ʈ�������¸� �Ϸ�� �����Ѵ�.
			if(etc_cnt==0){
				sql = this.getUpdEvtQuery();
				pstmt = conn.prepareStatement(sql.toString());
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no);
				result = pstmt.executeUpdate();
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/** ***********************************************************************
    * ������ �������� ����
    ************************************************************************ */
    private String getUpdCompnQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE SET STTL_STAT_CLSS='1' WHERE APLC_SEQ_NO=? AND SEQ_NO=? 	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * �̺�Ʈ ���� ���� ����
     ************************************************************************ */
     private String getUpdEvtQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET STTL_STAT_CLSS='1', EVNT_PGRS_CLSS='B' WHERE APLC_SEQ_NO=? 	\n");
 		return sql.toString();
     }
     
 	/** ***********************************************************************
 	 * �������߿� �����Ϸᰡ �ƴѰ��� �ִ��� �˾ƺ���.
 	 ************************************************************************ */
     private String getEtcCntQuery(){
    	 StringBuffer sql = new StringBuffer();		
    	 sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE STTL_STAT_CLSS<>'1' AND APLC_SEQ_NO=? 	\n");
    	 return sql.toString();
     }
}
