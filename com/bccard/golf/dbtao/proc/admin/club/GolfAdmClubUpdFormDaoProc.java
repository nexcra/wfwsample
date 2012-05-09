/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaChgFormDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 수정폼
*   적용범위  : golf
*   작성일자  : 2009-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.club;

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
public class GolfAdmClubUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaChgFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmClubUpdFormDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("RECV_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next())  {	
					
					result.addInt("RECV_NO" 				,rs.getInt("CLUB_SEQ_NO") );
					result.addString("OPN_PE_NM" 			,rs.getString("OPN_PE_NM") ); 			// 개설자명
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") ); 			// 휴대전화 국번호
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") ); 			// 휴대전화 국번호
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") ); 			// 휴대전화 일련번호
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );  	// 동호회 분류
					result.addString("CLUB_NM" 		,rs.getString("CLUB_NM") );						// 동호회 명칭
					result.addString("CLUB_SBJT_CTNT" 		,rs.getString("CLUB_SBJT_CTNT") );		// 동호회 주제
					result.addString("CLUB_INTD_CTNT" 		,rs.getString("CLUB_INTD_CTNT") );		// 동호회 소개
					result.addString("CLUB_OPN_PRPS_CTNT" 	,rs.getString("CLUB_OPN_PRPS_CTNT") );	// 개설 취지
					result.addString("CDHD_NUM_LIMT_YN" 	,rs.getString("CDHD_NUM_LIMT_YN") );	// 회원수제한여부
					result.addString("CLUB_JONN_MTHD_CLSS" 	,rs.getString("CLUB_JONN_MTHD_CLSS") );	// 가입방법 구분코드
					//result.addString("APLC_ATON" 			,rs.getString("APLC_ATON") ); 			// 신청 접수일자
					result.addString("CLUB_OPN_AUTH_YN" 	,rs.getString("CLUB_OPN_AUTH_YN") ); 	// 개설승인여부

					result.addString("RESULT", "00"); 												// 정상결과
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
        
		sql.append("\n SELECT");
		sql.append("\n 	CLUB_SEQ_NO,GOLF_CLUB_CTGO,CLUB_NM,OPN_PE_ID,OPN_PE_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,CLUB_SBJT_CTNT,CLUB_IMG,CLUB_INTD_CTNT,CLUB_OPN_PRPS_CTNT,CDHD_NUM_LIMT_YN,LIMT_CDHD_NUM,CLUB_JONN_MTHD_CLSS,CLUB_OPN_AUTH_YN,CLUB_ACT_YN,APLC_ATON,OPN_ATON,CHNG_MGR_ID,CHNG_ATON");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBMGMT TGL");
		sql.append("\n WHERE CLUB_SEQ_NO = ?	");		

		return sql.toString();
    }
}
