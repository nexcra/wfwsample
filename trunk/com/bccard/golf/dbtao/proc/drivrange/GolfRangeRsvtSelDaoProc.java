/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtSelDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약시 회원 연월제한 여부 
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfRangeRsvtSelDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfRangeRsvtSelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtSelDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------			
			sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("USERID"));
			pstmt.setString(++idx, data.getString("USERID"));
			pstmt.setString(++idx, data.getString("USERID"));
			pstmt.setString(++idx, data.getString("USERID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("RSVT_YEAR_CNT", rs.getString("RSVT_YEAR_CNT") ); 	
					result.addString("RSVT_MONTH_CNT", rs.getString("RSVT_MONTH_CNT") ); 	
					
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
        
        sql.append("\n SELECT T1.RSVT_YEAR_CNT, T2.RSVT_MONTH_CNT	");
        sql.append("\n FROM (SELECT (RSVT_CNT - COME_CNT) RSVT_YEAR_CNT		");
        sql.append("\n 		FROM (SELECT RSVT_CNT,		");
        sql.append("\n 					(SELECT COUNT (COME_SEQ_SEQ_NO) COME_CNT		");
        sql.append("\n 					FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGCBMOUSECTNTMGMT TGCM	");
        sql.append("\n 					WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 					AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 					AND TGR.GOLF_SVC_RSVT_NO = TGCM.GOLF_SVC_RSVT_NO	");
        sql.append("\n 					AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 					AND TGCM.ACM_DDUC_CLSS = 'N'	");
        sql.append("\n 					AND TGR.CDHD_ID = ?		");
        sql.append("\n 					AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY') = TO_CHAR (SYSDATE, 'YYYY')) COME_CNT	");
        sql.append("\n 				FROM (SELECT COUNT (TGRD.RSVT_ABLE_DATE) RSVT_CNT		");
        sql.append("\n 						FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT		");
        sql.append("\n 						WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 						AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 						AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 						AND TGR.CDHD_ID = ?		");
        sql.append("\n 						AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY') = TO_CHAR (SYSDATE, 'YYYY') 	");
        sql.append("\n 						)	");
        sql.append("\n 				)	");
        sql.append("\n 		) T1, 		");
        sql.append("\n 		(SELECT (RSVT_CNT - COME_CNT) RSVT_MONTH_CNT	");
        sql.append("\n 		FROM (SELECT RSVT_CNT,		");
        sql.append("\n 					(SELECT COUNT (COME_SEQ_SEQ_NO) COME_CNT		");
        sql.append("\n 					FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGCBMOUSECTNTMGMT TGCM	");
        sql.append("\n 					WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 					AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 					AND TGR.GOLF_SVC_RSVT_NO = TGCM.GOLF_SVC_RSVT_NO	");
        sql.append("\n 					AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 					AND TGCM.ACM_DDUC_CLSS = 'N'	");
        sql.append("\n 					AND TGR.CDHD_ID = ?		");
        sql.append("\n 					AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'MM') = TO_CHAR (SYSDATE, 'MM')) COME_CNT	");
        sql.append("\n 				FROM (SELECT COUNT (TGRD.RSVT_ABLE_DATE) RSVT_CNT		");
        sql.append("\n 						FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT		");
        sql.append("\n 						WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 						AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 						AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 						AND TGR.CDHD_ID = ?		");
        sql.append("\n 						AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'MM') = TO_CHAR (SYSDATE, 'MM') 	");
        sql.append("\n 						)	");
        sql.append("\n 				)	");
        sql.append("\n 		) T2 		");
        
		return sql.toString();
    }
   
}
