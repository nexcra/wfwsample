/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfNewsListDaoProc
*   작성자    : (주)미디어포스 조은미
*   내용      : 뉴스 리스트
*   적용범위  : golf
*   작성일자  : 2009-09-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

import java.io.Reader;
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
public class GolfNewsListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfNewsListDaoProc() {}	

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
			
			//debug("bbs2 ====> "+ data.getString("BBS"));
			
			//조회 ----------------------------------------------------------
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			String sreg_sdate		= data.getString("SREG_SDATE");
			String sreg_edate		= data.getString("SREG_EDATE");	

			String sql = this.getSelectQuery(search_sel, search_word, sreg_sdate, sreg_edate);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
				pstmt.setString(++idx, sreg_sdate+"000000");
				pstmt.setString(++idx, sreg_edate+"235959");
			}
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("CTET_ID" 				,rs.getString("CTET_ID") );
					result.addString("TITL" 				,rs.getString("TITL") );

					result.addString("NEWS_RECP_DATE"					,rs.getString("NEWS_RECP_DATE") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
					result.addString("RNUM"				,rs.getString("RNUM") );

					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CTNT", bufferSt.toString());
					
					String ctnt_list = GolfUtil.getAnsiCode(GolfUtil.removeTag(bufferSt.toString()));
					if (ctnt_list.getBytes().length > 300)	ctnt_list = GolfUtil.getCutKSCString(ctnt_list,300,"..."); 
					
					result.addString("CTNT_LIST", ctnt_list);
					
					String titl_list = GolfUtil.getAnsiCode(GolfUtil.removeTag(rs.getString("TITL")));
					if (titl_list.getBytes().length > 55)	titl_list = GolfUtil.getCutKSCString(titl_list,55,"..."); 
					
					result.addString("TITL_LIST", titl_list);
							
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
    private String getSelectQuery(String search_sel, String search_word, String sreg_sdate, String sreg_edate){
        StringBuffer sql = new StringBuffer();

        sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			CTET_ID, TITL, CTNT, TO_CHAR(TO_DATE(NEWS_RECP_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') AS NEWS_RECP_DATE,  	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				CTET_ID, TITL, CTNT, NEWS_RECP_DATE 	");
		sql.append("\n 				FROM BCDBA.TBGGOLFNEWS TBN	");
		sql.append("\n 				WHERE CTET_ID IS NOT NULL	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TITL LIKE ?	");
				sql.append("\n 				OR CTNT LIKE ? 	) ");	
			} else if (search_sel.equals("TITL")) {
				sql.append("\n 				AND TITL LIKE ?	");
			} else if (search_sel.equals("CTNT")) {
				sql.append("\n 				AND CTNT LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
			sql.append("\n 				AND NEWS_RECP_DATE||NEWS_RECP_TIME BETWEEN ? AND ?	");
		}
		
		sql.append("\n 				ORDER BY CTET_ID DESC	");		
		
		sql.append("\n 			)	");
		//sql.append("\n 	START WITH REPY_URNK_SEQ_NO = 0	");
		//sql.append("\n 	CONNECT BY PRIOR SEQ_NO = REPY_URNK_SEQ_NO	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
    
}
