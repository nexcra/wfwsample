/***************************************************************************************************
 *   이 소스는 ㈜비씨카드 소유입니다.
 *   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 *   클래스명  : ISPCertification.java
 *   작성자    : (주)미디어포스 이경희
 *   내용      : ISP인증 로그 insert
 *   적용범위  : Golf
 *   작성일자  : 2011.02.10
 ************************** 수정이력 ****************************************************************
*    일자       작성자      변경사항
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
	 * ISP Log 저장
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
	* TBUSRPROOFINFO(사용자인증정보) 테이블에 ISP로그 기록
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
