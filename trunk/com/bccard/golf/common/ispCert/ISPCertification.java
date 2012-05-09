/***************************************************************************************************
 *   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 *   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 *   Ŭ������  : ISPCertification.java
 *   �ۼ���    : (��)�̵������ �̰���
 *   ����      : ISP���� �α� insert
 *   �������  : Golf
 *   �ۼ�����  : 2011.02.10
 ************************** �����̷� ****************************************************************
*    ����       �ۼ���      �������
 ***************************************************************************************************/
package com.bccard.golf.common.ispCert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class ISPCertification extends AbstractProc{

	
	/**
	 * ISP Log ����
	 * @param Connection conn
	 * @param TaoDataSet dataSet
	 * @return String
	 * @throws SQLException 
	 */
	public String insertIspLog(WaContext context, TaoDataSet data) throws BaseException, SQLException {

		Connection conn 			= null;
		PreparedStatement pstmt = null;				
		String resultCode		= "N";
		int res = 0;
		
		try {
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			//ISP log insert
			pstmt = conn.prepareStatement(getIspLogInsertQry());	

			int idx = 0;
			pstmt.setString(++idx, data.getString("userSocid") );
			pstmt.setString(++idx, data.getString("userNm") );
			pstmt.setString(++idx, data.getString("urlType") );
			pstmt.setString(++idx, data.getString("userIp") );
			pstmt.setString(++idx, data.getString("vfcRslt") );
			pstmt.setString(++idx, data.getString("userCardNo") );
			res = pstmt.executeUpdate();
			
			if(res > 0){			
				resultCode = "Y";
				conn.commit();
			}else {
				resultCode = "N";
				conn.rollback();
			}		
			
		} catch (Throwable t) {
			conn.rollback();			
		} finally {		
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}		
		
		return resultCode;
		
	}
	
	
	/*************************************************************************
	* TBUSRPROOFINFO(�������������) ���̺� ISP�α� ���
	*************************************************************************/
	private String getIspLogInsertQry() {
		
		StringBuffer sql = new StringBuffer();

		sql.append("\n INSERT INTO BCDBA.TBUSRPROOFINFO																						");
		sql.append("\n ( SEQ_NO, REG_DATE, REG_TIME, REQ_CLSS, INP_SUBJ_CLSS, INP_SUBJ_NO, CSTMR_NM, USE_MENU_NM, IP_ADDR, VFC_RSLT, PROOF_INFO_VAL )	");
		sql.append("\n VALUES	 ");
		sql.append("\n ( BCDBA.SEQ_USRPROOFINFO.NEXTVAL, to_char(SYSDATE,'yyyyMMdd'), to_char(SYSDATE,'hh24miss'), '2', '1', ?, ?, ?, ?, ?, ? ) 	");				

		return sql.toString();
		
	}

}
