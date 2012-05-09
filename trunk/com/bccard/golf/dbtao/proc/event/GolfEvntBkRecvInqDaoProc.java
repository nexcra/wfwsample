/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkRecvInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 프리미엄 이벤트 신청 상세조회
*   적용범위  : golf
*   작성일자  : 2009-06-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class GolfEvntBkRecvInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkRecvInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBkRecvInqDaoProc() {}	

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

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("APLC_SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("SEQ_NO" 			,rs.getLong("EVNT_SEQ_NO") );
					result.addString("DISP_YN" 			,rs.getString("BLTN_YN") );
					result.addLong("TIME_SEQ_NO" 		,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GREEN_NM") );
					result.addString("COURSE" 			,rs.getString("GOLF_RSVT_CURS_NM") );
					String bokg_able_date = rs.getString("BOKG_ABLE_DATE");
					if (!GolfUtil.isNull(bokg_able_date)) bokg_able_date = DateUtil.format(bokg_able_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("BKPS_DATE"		,bokg_able_date);
					result.addString("BKPS_TIME" 		,rs.getString("BOKG_ABLE_TIME") );
					result.addString("DIPY_BKPS_TIME" 	,rs.getString("DIPY_BOKG_ABLE_TIME") );
					String evnt_start_date = rs.getString("EVNT_STRT_DATE");
					if (!GolfUtil.isNull(evnt_start_date)) evnt_start_date = DateUtil.format(evnt_start_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("EVNT_FROM"		,evnt_start_date);
					String evnt_end_date = rs.getString("EVNT_END_DATE");
					if (!GolfUtil.isNull(evnt_end_date)) evnt_end_date = DateUtil.format(evnt_end_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("EVNT_TO"		,evnt_end_date);	
					result.addString("PRIZE_NM" 		,rs.getString("EVNT_BNFT_EXPL") );
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n SELECT	");
		sql.append("\n		TGE.EVNT_SEQ_NO, TGE.BLTN_YN, TGE.RSVT_ABLE_BOKG_TIME_SEQ_NO, TGE.GREEN_NM, TGE.BOKG_ABLE_DATE, TGE.GOLF_RSVT_CURS_NM,  	");
		sql.append("\n		TGE.BOKG_ABLE_TIME, TO_CHAR (TO_DATE (TGE.BOKG_ABLE_TIME, 'HH24MI'), 'HH24:MI') DIPY_BOKG_ABLE_TIME,	");
		sql.append("\n		TGE.EVNT_STRT_DATE, TGE.EVNT_END_DATE, TGE.EVNT_BNFT_EXPL	");
		sql.append("\n	FROM 	");
		sql.append("\n	BCDBA.TBGAPLCMGMT TGR, BCDBA.TBGEVNTMGMT TGE	");
		sql.append("\n	WHERE TGR.LESN_SEQ_NO = TGE.EVNT_SEQ_NO	");	
		sql.append("\n  AND TGR.APLC_SEQ_NO = ?	");

		return sql.toString();
    }
}
