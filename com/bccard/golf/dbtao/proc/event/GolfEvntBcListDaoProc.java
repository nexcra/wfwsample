/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBcListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : BC Golf 이벤트 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-05
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

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
public class GolfEvntBcListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBcListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBcListDaoProc() {}	

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
			String sch_ing_yn = data.getString("SCH_ING_YN");
			
			String sql = this.getSelectQuery(sch_ing_yn);  			
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			//pstmt.setString(++idx, data.getString("SCH_ING_YN"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("EVNT_SEQ_NO") );
					result.addString("EVNT_CLSS" 		,rs.getString("EVNT_CLSS") );
					result.addString("EVNT_NM" 			,rs.getString("EVNT_NM") );
					String evnt_start_date = rs.getString("EVNT_STRT_DATE");
					if (!GolfUtil.isNull(evnt_start_date)) evnt_start_date = DateUtil.format(evnt_start_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("EVNT_FROM"		,evnt_start_date);
					String evnt_end_date = rs.getString("EVNT_END_DATE");
					if (!GolfUtil.isNull(evnt_end_date)) evnt_end_date = DateUtil.format(evnt_end_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("EVNT_TO"		,evnt_end_date);
					result.addString("TITL" 			,rs.getString("TITL") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("IMG_NM"			,rs.getString("IMG_FILE_PATH") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
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
    private String getSelectQuery(String sch_ing_yn){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM, EVNT_STRT_DATE, EVNT_END_DATE, TITL, CTNT, IMG_FILE_PATH, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM, EVNT_STRT_DATE, EVNT_END_DATE, 	");
		sql.append("\n 				TITL, CTNT, IMG_FILE_PATH, REG_ATON 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGEVNTMGMT	");
		sql.append("\n 				WHERE EVNT_CLSS = '0001'	");
		sql.append("\n 				AND BLTN_YN = 'Y'	");

		if (sch_ing_yn.equals("Y")) sql.append("\n 	AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN BLTN_STRT_DATE AND BLTN_END_DATE	");
		if (sch_ing_yn.equals("N")) sql.append("\n 	AND TO_CHAR(SYSDATE, 'YYYYMMDD') > BLTN_END_DATE	");
		
		sql.append("\n 				ORDER BY REG_ATON DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
