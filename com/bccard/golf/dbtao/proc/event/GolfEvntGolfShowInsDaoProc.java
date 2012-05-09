/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntGolfShowInsDaoProc
*   작성자	: (주)미디어포스 임은혜
*   내용		: 골프박람회 쿠폰 출력 내역 저장
*   적용범위	: golf
*   작성일자	: 2010-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntGolfShowInsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBcWinInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntGolfShowInsDaoProc() {}	

	/**
	 * 등록
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int insExecute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			sql = this.getInsertQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("userId"));
			
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
			

		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, CDHD_ID, REG_ATON) VALUES	");
		sql.append("\n ((SELECT MAX(APLC_SEQ_NO)+1 FROM BCDBA.TBGAPLCMGMT), '0010', ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'))	");
		return sql.toString();
    }

}
