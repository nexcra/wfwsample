/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLessonInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨프로그램 상세조회
*   적용범위  : golf
*   작성일자  : 2009-06-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션  
 * @version	1.0
 ******************************************************************************/
public class GolfMainDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfMainDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMainDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getBoardList(WaContext context, TaoDataSet data, String BbsCode, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------		
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, BBRD_SEQ_NO, BBRD_CLSS, GOLF_CLM_CLSS, GOLF_BOKG_FAQ_CLSS, GOLF_VBL_RULE_PREM_CLSS,	");
			sql.append("\n 		TITL, REG_ATON	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, BBRD_SEQ_NO, BBRD_CLSS, GOLF_CLM_CLSS, GOLF_BOKG_FAQ_CLSS, GOLF_VBL_RULE_PREM_CLSS,  	");
			sql.append("\n 			TITL, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_ATON 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					BBRD_SEQ_NO, BBRD_CLSS, GOLF_CLM_CLSS, GOLF_BOKG_FAQ_CLSS, GOLF_VBL_RULE_PREM_CLSS,  	");
			sql.append("\n 					TITL, REG_ATON 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGBBRD	");
			sql.append("\n 				WHERE BBRD_CLSS = ?	");	
			sql.append("\n 				AND EPS_YN = 'Y'	");	
			sql.append("\n 				AND DEL_YN = 'N'	");	
			sql.append("\n 				ORDER BY REG_ATON DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, BbsCode);
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("BBRD_SEQ_NO" 				,rs.getLong("BBRD_SEQ_NO") );
					result.addString("BBRD_CLSS" 				,rs.getString("BBRD_CLSS") );
					result.addString("GOLF_CLM_CLSS" 			,rs.getString("GOLF_CLM_CLSS") );
					result.addString("GOLF_BOKG_FAQ_CLSS" 		,rs.getString("GOLF_BOKG_FAQ_CLSS") );
					result.addString("GOLF_VBL_RULE_PREM_CLSS" 	,rs.getString("GOLF_VBL_RULE_PREM_CLSS") );
					result.addString("REG_ATON" 				,rs.getString("REG_ATON") );
					
					String ttileText = rs.getString("TITL");
					if (TitleLen > 0) {
						if (ttileText.getBytes().length > TitleLen)	ttileText = GolfUtil.getCutKSCString(ttileText, TitleLen, "..."); 
					}
					result.addString("TITL" 	, ttileText);
					
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
	public DbTaoResult getLessonList(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
						 
			//조회 ----------------------------------------------------------
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, LESN_SEQ_NO, LESN_CLSS, LESN_NM, LESN_EXPL, MAIN_BANNER_IMG, MAIN_BANNER_URL	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, LESN_SEQ_NO, LESN_CLSS, LESN_NM, LESN_EXPL, MAIN_BANNER_IMG, MAIN_BANNER_URL 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					LESN_SEQ_NO, LESN_CLSS, LESN_NM, LESN_EXPL, REG_ATON, MAIN_BANNER_IMG, MAIN_BANNER_URL 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGLESNMGMT	");
			sql.append("\n 				WHERE MAIN_EPS_YN = 'Y'	");
			sql.append("\n 				ORDER BY REG_ATON DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("LESN_SEQ_NO" 				,rs.getLong("LESN_SEQ_NO") );
					result.addString("LESN_CLSS" 				,rs.getString("LESN_CLSS") );
					result.addString("LESN_NM" 					,rs.getString("LESN_NM") );
					//result.addString("LESN_EXPL" 				,rs.getString("LESN_EXPL") );
					result.addString("MAIN_BANNER_IMG" 			,rs.getString("MAIN_BANNER_IMG") );
					result.addString("MAIN_BANNER_URL" 			,rs.getString("MAIN_BANNER_URL") );
					
					String ttileText = rs.getString("LESN_EXPL");
					if (TitleLen > 0) {
						if (ttileText.getBytes().length > TitleLen)	ttileText = GolfUtil.getCutKSCString(ttileText, TitleLen, "..."); 
					}
					result.addString("LESN_EXPL" 				,ttileText );
					
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
	public DbTaoResult getParList(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, MAIN_BANNER_IMG	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, MAIN_BANNER_IMG 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					MAIN_BANNER_IMG 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGAFFIGREEN	");
			sql.append("\n 				WHERE MAIN_EPS_YN = 'Y'	");
			sql.append("\n 				ORDER BY CHNG_ATON DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addString("MAIN_BANNER_IMG" 			,rs.getString("MAIN_BANNER_IMG") );
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
	public DbTaoResult getParScrollList(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
	        StringBuffer sql = new StringBuffer();

			sql.append("\n");  
			sql.append("\t SELECT GREEN_NM, TO_CHAR(TO_DATE(SDATE),'MM.DD/DAY') AS BK_DATE										\n");  
			sql.append("\t 		, MAX_ACPT_PNUM-(SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE ROUND_HOPE_DATE=T5.SDATE AND AFFI_GREEN_SEQ_NO=T5.AFFI_GREEN_SEQ_NO) AS RS_NUM	\n");  
			sql.append("\t 		FROM (																							\n");  
			sql.append("\t 		    SELECT ROWNUM, T1.AFFI_GREEN_SEQ_NO, T1.GREEN_NM, T1.MAX_ACPT_PNUM, TO_CHAR(T2.SDATE,'YYYYMMDD') AS SDATE									\n");  
			sql.append("\t		    FROM BCDBA.TBGAFFIGREEN T1																	\n");  
			sql.append("\t 		    JOIN (																						\n");  
			sql.append("\t		    SELECT SDATE FROM(																			\n");  
			sql.append("\t 		        SELECT SYSDATE+LEVEL+1 SDATE															\n");  
			sql.append("\t 		        FROM DUAL																				\n");  
			sql.append("\t 		        CONNECT BY SYSDATE+LEVEL<=SYSDATE+19)													\n");  
			sql.append("\t 		        WHERE TO_CHAR(SDATE, 'DY') NOT IN ('토','일') AND TO_CHAR(SDATE,'YYYYMMDD') NOT IN (		\n");  
			sql.append("\t 		            SELECT PAR_3_BOKG_RESM_DATE															\n");  
			sql.append("\t 		            FROM BCDBA.TBGRSVTABLESCDMGMT														\n");  
			sql.append("\t 		            WHERE PAR_3_BOKG_RESM_DATE IS NOT NULL)												\n");  
			sql.append("\t 		        AND ROWNUM<11) T2 ON T1.AFFI_FIRM_CLSS='0002'											\n");  
			sql.append("\t 		    ) T5																						\n");  
			sql.append("\t 		ORDER BY AFFI_GREEN_SEQ_NO, BK_DATE																\n");
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addString("BK_DATE" 		,rs.getString("BK_DATE"));
					result.addString("GREEN_NM" 	,rs.getString("GREEN_NM"));
					result.addString("RS_NUM" 		,rs.getString("RS_NUM"));
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
	public DbTaoResult getGoodFoodList(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, AFFI_ETHS_SEQ_NO, ETHS_NM, CTNT, 	");
			sql.append("\n 		MAIN_BANNER_IMG, MAIN_BANNER_URL, GREEN_NM	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, AFFI_ETHS_SEQ_NO, ETHS_NM, CTNT, 	");
			sql.append("\n 			MAIN_BANNER_IMG, MAIN_BANNER_URL, GREEN_NM	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					TGFD.AFFI_ETHS_SEQ_NO, TGFD.ETHS_NM, TGFD.CTNT, 	");
			sql.append("\n 					TGFD.MAIN_BANNER_IMG, TGFD.MAIN_BANNER_URL, 	");
			sql.append("\n 					(SELECT GREEN_NM FROM BCDBA.TBGETHSNGHBGREEN TGFDI, BCDBA.TBGAFFIGREEN TGF WHERE TGFDI.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO AND TGFDI.AFFI_ETHS_SEQ_NO=TGFD.AFFI_ETHS_SEQ_NO AND ROWNUM = 1) GREEN_NM 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGAFFIETHS TGFD	");
			sql.append("\n 				WHERE TGFD.MAIN_EPS_YN = 'Y'	");
			sql.append("\n 				ORDER BY TGFD.AFFI_ETHS_SEQ_NO DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("AFFI_ETHS_SEQ_NO" 		,rs.getLong("AFFI_ETHS_SEQ_NO") );
					result.addString("ETHS_NM" 				,rs.getString("ETHS_NM") );
					result.addString("MAIN_BANNER_IMG" 		,rs.getString("MAIN_BANNER_IMG") );
					result.addString("MAIN_BANNER_URL" 		,rs.getString("MAIN_BANNER_URL") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					
					String ttileText = rs.getString("CTNT");
					if (TitleLen > 0) {
						if (ttileText.getBytes().length > TitleLen)	ttileText = GolfUtil.getCutKSCString(ttileText, TitleLen, "..."); 
					}
					result.addString("CTNT" 				,ttileText );
					
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
	public DbTaoResult getMainGoodFoodList(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, AFFI_ETHS_SEQ_NO, ETHS_NM, 	");
			sql.append("\n 		MAIN_RPRS_IMG, MAIN_RPRS_IMG_URL	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, AFFI_ETHS_SEQ_NO, ETHS_NM, 	");
			sql.append("\n 			MAIN_RPRS_IMG, MAIN_RPRS_IMG_URL	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					TGFD.AFFI_ETHS_SEQ_NO, TGFD.ETHS_NM,	");
			sql.append("\n 					TGFD.MAIN_RPRS_IMG, TGFD.MAIN_RPRS_IMG_URL 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGAFFIETHS TGFD	");
			sql.append("\n 				WHERE TGFD.MAIN_RPRS_IMG_EPS_YN = 'Y'	");
			sql.append("\n 				ORDER BY TGFD.AFFI_ETHS_SEQ_NO DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("AFFI_ETHS_SEQ_NO" 		,rs.getLong("AFFI_ETHS_SEQ_NO") );
					result.addString("ETHS_NM" 				,rs.getString("ETHS_NM") );
					result.addString("MAIN_RPRS_IMG" 		,rs.getString("MAIN_RPRS_IMG") );
					result.addString("MAIN_RPRS_IMG_URL" 		,rs.getString("MAIN_RPRS_IMG_URL") );
					
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
	public DbTaoResult getNewsList(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------	 	
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, CTET_ID, TITL, NEWS_RECP_DATE	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, CTET_ID, TITL, TO_CHAR(TO_DATE(NEWS_RECP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') AS NEWS_RECP_DATE 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					CTET_ID, TITL, NEWS_RECP_DATE  	");			
			sql.append("\n 				FROM BCDBA.TBGGOLFNEWS	");
			sql.append("\n 				ORDER BY CTET_ID DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addString("CTET_ID" 				,rs.getString("CTET_ID") );
					result.addString("NEWS_RECP_DATE" 			,rs.getString("NEWS_RECP_DATE") );
					
					String ttileText = rs.getString("TITL");
					if (TitleLen > 0) {
						if (ttileText.getBytes().length > TitleLen)	ttileText = GolfUtil.getCutKSCString(ttileText, TitleLen, "..."); 
					}
					result.addString("TITL" 	, ttileText);
					
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
}
