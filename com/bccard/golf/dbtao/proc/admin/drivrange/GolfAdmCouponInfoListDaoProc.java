/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCouponInfoListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 쿠폰인쇄 이력 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-15
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmCouponInfoListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmCouponInfoListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponInfoListDaoProc() {}	

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

			// 회원통합테이블 관련 수정사항 진행
			//조회 ----------------------------------------------------------
			String exec_type_cd		= data.getString("EXEC_TYPE_CD");
			String user_clss		= data.getString("USER_CLSS");
			String start_dt		= data.getString("START_DT");
			String end_dt		= data.getString("END_DT");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			
			String sql = this.getSelectQuery(exec_type_cd,user_clss,start_dt,end_dt,search_sel,search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(exec_type_cd)) pstmt.setString(++idx, exec_type_cd);  // 구분 검색
			
			if (!GolfUtil.isNull(user_clss)) pstmt.setString(++idx, "%"+user_clss+"%"); // 회원등급 검색
			
			if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // 출력일자 검색
				pstmt.setString(++idx, start_dt);
				pstmt.setString(++idx, end_dt);
			}
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
				
				if (search_sel.equals("ALL")){ //전체
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else if (search_sel.equals("HG_NM")){ //이름
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("CDHD_ID")){ //아이디
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("USERCLSS")){ //회원등급
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("CHG_TEL")){ //전화번호
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("GOLF_RNG_CUPN_SEQ_NO")){ //일련번호
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}	else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("CPNINF_SQL_NO" 			,rs.getLong("CUPN_HST_SEQ_NO") );
					result.addString("CPN_SERIAL" 			,rs.getString("GOLF_RNG_CUPN_SEQ_NO") );
					result.addString("EXEC_TYPE_CD" 		,rs.getString("GOLF_RNG_CLSS") );
					result.addString("GF_NM" 				,rs.getString("GREEN_NM") );
					result.addString("USERCLSS" 			,rs.getString("USERCLSS") );
					result.addString("HAN_NM" 				,rs.getString("HG_NM") );
					result.addString("PHONE" 				,rs.getString("PHONE") );
					result.addString("CPN_PT_ATON"			,rs.getString("CUPN_OUTP_ATON") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
										
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
    private String getSelectQuery(String exec_type_cd, String user_clss, String start_dt, String end_dt, String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			CUPN_HST_SEQ_NO, GOLF_RNG_CUPN_SEQ_NO, GOLF_RNG_CLSS, GREEN_NM, USERCLSS, HG_NM, PHONE, CUPN_OUTP_ATON,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,		");
		sql.append("\n 	TGC.CUPN_HST_SEQ_NO, TGC.GOLF_RNG_CUPN_SEQ_NO															");
		sql.append("\n 	, TMC1.GOLF_CMMN_CODE_NM GOLF_RNG_CLSS, TGF.GREEN_NM, TMC2.GOLF_CMMN_CODE_NM USERCLSS					");
		sql.append("\n 	, TGU.HG_NM ||'('|| TGU.CDHD_ID ||')' HG_NM, PHONE														");
		sql.append("\n 	, TO_CHAR (TO_DATE (TGC.CUPN_OUTP_ATON, 'YYYYMMDDHH24MISS'), 'YY-MM-DD (DY) HH24:MI') CUPN_OUTP_ATON	");
		sql.append("\n	FROM BCDBA.TBGCUPNUSEHST TGC																			");
		sql.append("\n 	JOIN BCDBA.TBGAFFIGREEN TGF ON  TGC.RNG_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO AND TGF.AFFI_FIRM_CLSS = '0003'	");
		sql.append("\n 	JOIN BCDBA.TBGGOLFCDHD TGU ON TGC.CDHD_ID = TGU.CDHD_ID													");
		sql.append("\n 	JOIN BCDBA.TBGCMMNCODE TMC1 ON TGF.GOLF_RNG_CLSS = TMC1.GOLF_CMMN_CODE AND TMC1.GOLF_CMMN_CLSS='0008' AND TMC1.USE_YN = 'Y'	");
		sql.append("\n 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT TGUD ON TGUD.CDHD_CTGO_SEQ_NO=TGU.CDHD_CTGO_SEQ_NO						");
		sql.append("\n 	JOIN BCDBA.TBGCMMNCODE TMC2 ON TGUD.CDHD_SQ2_CTGO=TMC2.GOLF_CMMN_CODE AND TMC2.GOLF_CMMN_CLSS='0005'	");
		
		
		
		if (!GolfUtil.isNull(exec_type_cd)) sql.append("\n 	AND TGF.GOLF_RNG_CLSS = ?	"); // 구분 검색
		
		if (!GolfUtil.isNull(user_clss)) sql.append("\n 		AND TMC2.GOLF_CMMN_CODE_NM LIKE ?		"); // 회원등급 검색
		
		if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // 출력일자 검색
			sql.append("\n 				AND TGC.CUPN_OUTP_ATON BETWEEN ? AND ?	");
		}
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
			
			if (search_sel.equals("ALL")){ //전체
				sql.append("\n 	AND (TGU.HG_NM LIKE ? 	");
				sql.append("\n 	OR TGU.CDHD_ID LIKE ? 	");
				sql.append("\n 	OR TMC2.GOLF_CMMN_CODE_NM LIKE ? 	");
				sql.append("\n 	OR TGF.DDD_NO || TGF.TEL_HNO || TGF.TEL_SNO LIKE ? 	");
				sql.append("\n 	OR TGC.GOLF_RNG_CUPN_SEQ_NO LIKE ?	)	");
				
			} else if (search_sel.equals("HG_NM")){ //이름
				sql.append("\n 	AND TGU.HG_NM LIKE ?	");
				
			}  else if (search_sel.equals("CDHD_ID")){ //아이디
				sql.append("\n 	AND TGU.CDHD_ID LIKE ?	");
				
			}  else if (search_sel.equals("USERCLSS")){ //회원등급
				sql.append("\n 	AND TMC2.GOLF_CMMN_CODE_NM LIKE ?		");
				
			}  else if (search_sel.equals("CHG_TEL")){ //전화번호
				sql.append("\n 	AND TGF.DDD_NO || TGF.TEL_HNO || TGF.TEL_SNO LIKE ?		");
				
			}  else if (search_sel.equals("GOLF_RNG_CUPN_SEQ_NO")){ //일련번호
				sql.append("\n 	AND TGC.GOLF_RNG_CUPN_SEQ_NO LIKE ?	");
				
			}	else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
		
		
		sql.append("\n 				ORDER BY TGC.CUPN_HST_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		
		return sql.toString();
    }
}
