/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnVodReplyIfmListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨동영상 덧글 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.bbs;

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
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfBoardComtListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfLsnVodReplyIfmListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardComtListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			String sql = this.getSelectQuery();
						
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			pstmt.setString(++idx, data.getString("REPLY_CLSS"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));			
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("REPLY_NO" 			,rs.getLong("REPY_SEQ_NO") );
					result.addString("REPLY_CTNT"		,GolfUtil.getUrl(rs.getString("REPY_CTNT")) );					
					result.addString("RGS_PE_ID"		,rs.getString("RGS_PE_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("HAN_NM"			,rs.getString("HG_NM") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_noPageing(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//String sql = this.getSelectQuery2();   
			String actnKey = data.getString("actnKey");
			String sql = "";
			System.out.println("=====================" + actnKey);
			if("golfEvntBcPsInq".equals(actnKey)) {
				sql = this.getSelectQueryFieter();
			} else {
				sql = this.getSelectQuery2();
			}
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			pstmt.setString(++idx, data.getString("REPLY_CLSS"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("REPLY_NO" 			,rs.getLong("REPY_SEQ_NO") );
					result.addString("REPLY_CTNT"		,GolfUtil.getUrl(rs.getString("REPY_CTNT")) );					
					result.addString("RGS_PE_ID"		,rs.getString("RGS_PE_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("HAN_NM"			,rs.getString("HG_NM") );
										
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			REPY_SEQ_NO, REPY_CTNT, RGS_PE_ID, REG_ATON,	");
		sql.append("\n 			HG_NM,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TBR.REPY_SEQ_NO, TBR.REPY_CTNT, TBR.RGS_PE_ID, TO_CHAR(TO_DATE(TBR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI') REG_ATON, 	");
		sql.append("\n 				TBU.HG_NM 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGBBRDREPY TBR, BCDBA.TBGGOLFCDHD TBU	");
		sql.append("\n 				WHERE TBR.RGS_PE_ID = TBU.CDHD_ID(+)	");
		sql.append("\n 				AND TBR.BBRD_SEQ_NO = ?	");
		sql.append("\n 				AND TBR.REPY_CLSS = ?	");
		sql.append("\n 				ORDER BY TBR.REPY_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
    
	/** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectQueryFieter(){
         StringBuffer sql = new StringBuffer();

 		sql.append("\n	SELECT	");
		sql.append("\n 		TBR.REPY_SEQ_NO, TBR.REPY_CTNT, TBR.RGS_PE_ID, TO_CHAR(TO_DATE(TBR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI') REG_ATON, 	");
		sql.append("\n		substr(TBU.CDHD_ID, 1, LENGTH(TBU.CDHD_ID)-1) || '*' as HG_NM 	");
		sql.append("\n 	FROM 	");
		sql.append("\n	BCDBA.TBGBBRDREPY TBR, BCDBA.TBGGOLFCDHD TBU	");
		sql.append("\n 	WHERE TBR.RGS_PE_ID = TBU.CDHD_ID(+)	");
		sql.append("\n	AND TBR.BBRD_SEQ_NO = ?	");
		sql.append("\n	AND TBR.REPY_CLSS = ?	");
		sql.append("\n	ORDER BY TBR.REPY_SEQ_NO DESC	");		

 		return sql.toString();
     }
    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery2(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n	SELECT	");
		sql.append("\n 		TBR.REPY_SEQ_NO, TBR.REPY_CTNT, TBR.RGS_PE_ID, TO_CHAR(TO_DATE(TBR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI') REG_ATON, 	");
		sql.append("\n		TBU.HG_NM 	");
		sql.append("\n 	FROM 	");
		sql.append("\n	BCDBA.TBGBBRDREPY TBR, BCDBA.TBGGOLFCDHD TBU	");
		sql.append("\n 	WHERE TBR.RGS_PE_ID = TBU.CDHD_ID(+)	");
		sql.append("\n	AND TBR.BBRD_SEQ_NO = ?	");
		sql.append("\n	AND TBR.REPY_CLSS = ?	");
		sql.append("\n	ORDER BY TBR.REPY_SEQ_NO DESC	");		

		return sql.toString();
    }
}
