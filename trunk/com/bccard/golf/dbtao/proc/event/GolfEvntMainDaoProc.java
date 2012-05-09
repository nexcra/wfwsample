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

package com.bccard.golf.dbtao.proc.event;

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
public class GolfEvntMainDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfMainDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntMainDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getEvntList(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
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
			sql.append("\n 		RNUM, SEQ_NO, TITL	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, SEQ_NO, TITL 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					TGEJ.SEQ_NO, TGEJ.TITL 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGEVNTMGMT TGE, BCDBA.TBGEVNTPRZPEMGMT TGEJ	");
			sql.append("\n 				WHERE TGE.EVNT_SEQ_NO = TGEJ.EVNT_SEQ_NO 	");
			sql.append("\n 				AND TGE.EVNT_CLSS = '0001' 	");
			sql.append("\n 				ORDER BY TGEJ.REG_ATON DESC	");	
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

					result.addLong("SEQ_NO" 				,rs.getLong("SEQ_NO") );
					
					String tTileTxt = rs.getString("TITL");
					if (TitleLen > 0) {
						if (tTileTxt.getBytes().length > TitleLen)	tTileTxt = GolfUtil.getCutKSCString(tTileTxt, TitleLen, "..."); 
					}
					result.addString("TITL" 				,tTileTxt );
					
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

}
