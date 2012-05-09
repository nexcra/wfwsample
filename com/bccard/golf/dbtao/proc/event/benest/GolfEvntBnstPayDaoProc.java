/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntBnstPayDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBnstPayDaoProc() {}	

	/**
	 * Proc 실행.
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
			

			// 동반자 결제 상태 변경	
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
			
			// 동반자중에 결제가 안된사람이 있으면 넘기지 않는다. getEtcCntQuery
			sql = this.getEtcCntQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, aplc_seq_no);

			rs = pstmt.executeQuery();
			
			if ( rs != null ) {
				rs.next();
				etc_cnt = rs.getInt("CNT");
			}
			
			// 이벤트결제상태를 완료로 변경한다.
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
    * 동반자 결제상태 변경
    ************************************************************************ */
    private String getUpdCompnQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE SET STTL_STAT_CLSS='1' WHERE APLC_SEQ_NO=? AND SEQ_NO=? 	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * 이벤트 결제 상태 변경
     ************************************************************************ */
     private String getUpdEvtQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET STTL_STAT_CLSS='1', EVNT_PGRS_CLSS='B' WHERE APLC_SEQ_NO=? 	\n");
 		return sql.toString();
     }
     
 	/** ***********************************************************************
 	 * 동반자중에 결제완료가 아닌것이 있는지 알아본다.
 	 ************************************************************************ */
     private String getEtcCntQuery(){
    	 StringBuffer sql = new StringBuffer();		
    	 sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE STTL_STAT_CLSS<>'1' AND APLC_SEQ_NO=? 	\n");
    	 return sql.toString();
     }
}
