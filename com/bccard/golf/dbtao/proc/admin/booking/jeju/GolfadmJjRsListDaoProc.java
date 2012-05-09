/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄 티타임 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.jeju;

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
public class GolfadmJjRsListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmJjRsListDaoProc() {}	

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
			 
			//조회 ----------------------------------------------------------
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");	
			String sch_SORT = data.getString("SCH_SORT");	
			String sch_TEXT = data.getString("SCH_TEXT");			
			String listtype = data.getString("LISTTYPE");
			String sql = this.getSelectQuery(sch_DATE, sch_DATE_ST, sch_DATE_ED, sch_SORT, sch_TEXT, listtype);   	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if(sch_DATE.equals("roundDate")){								
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}							
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}else if (sch_DATE.equals("regDate")){		
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST+"000000");
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED+"240000");
				}
			}

			if(!sch_TEXT.equals("")){		
				if(sch_SORT.equals("sort_no")){
					pstmt.setString(++idx, "%"+sch_TEXT+"%");
					pstmt.setString(++idx, "%"+sch_TEXT+"%");
				}
				pstmt.setString(++idx, "%"+sch_TEXT+"%");	
			}
			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addString("GOLF_SVC_RSVT_NO" 	,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("HP" 					,rs.getString("HP") );
					result.addString("REG_DATE" 			,rs.getString("REG_DATE") );
					result.addString("NAME" 				,rs.getString("NAME") );
					result.addString("HOPE_RGN_CODE" 		,rs.getString("HOPE_RGN_CODE") );
					result.addString("CTCT_ABLE_TIME" 		,rs.getString("CTCT_ABLE_TIME") );
					result.addString("TEAM_NUM" 			,rs.getString("TEAM_NUM") );
					result.addString("TOT_PERS_NUM" 		,rs.getString("TOT_PERS_NUM") );
					result.addString("BK_TIME" 				,rs.getString("BK_TIME") );
					result.addString("BK_TIME_AP" 			,rs.getString("BK_TIME_AP") );
					result.addString("GRADE_NM" 			,rs.getString("GRADE_NM") );
					
					result.addString("TOT_CNT"				,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"			,rs.getString("PAGE") );
					result.addString("RNUM"					,rs.getString("RNUM") );
					result.addInt("ART_NUM" 				,rs.getInt("ART_NUM")-art_num_no );
					
					result.addString("RESULT", "00"); //정상결과
					
					art_num_no++;
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
    private String getSelectQuery(String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, String sch_SORT, String sch_TEXT, String listtype){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT * FROM (	\n");
		sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT	\n");
		sql.append("\t	    , ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM	\n");
		sql.append("\t	    , GOLF_SVC_RSVT_NO, CDHD_ID, BK_DATE, HP, REG_DATE, NAME, BK_TIME	\n");
		sql.append("\t	    , HOPE_RGN_CODE, CTCT_ABLE_TIME, TEAM_NUM, TOT_PERS_NUM, BK_TIME_AP, GRADE_NM	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t      	SELECT ROWNUM RNUM, GOLF_SVC_RSVT_NO, T6.CDHD_ID, T6.HG_NM as NAME, T6.MOBILE AS HP, TEAM_NUM, TOT_PERS_NUM	\n");
		sql.append("\t	        , T8.GOLF_CMMN_CODE_NM AS GRADE_NM, TO_CHAR(TO_DATE(ROUND_HOPE_DATE),'YY-MM-DD(DY)') AS BK_DATE	\n");
		sql.append("\t	        , TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON,1,8)),'YY-MM-DD(DY)')||' '||SUBSTR(T1.REG_ATON,9,2)||':'||SUBSTR(T1.REG_ATON,11,2) AS REG_DATE	\n");
		sql.append("\t	        , (CASE WHEN ROUND_HOPE_TIME_CLSS='A' THEN '오전' ELSE '오후' END) AS BK_TIME_AP	\n");
		sql.append("\t	        , (CASE WHEN ROUND_HOPE_TIME='000000' THEN '전체' ELSE SUBSTR(ROUND_HOPE_TIME,1,2)||':00' END) AS BK_TIME	\n");
		sql.append("\t	        , (CASE WHEN HOPE_RGN_CODE='0001' THEN '전체' WHEN HOPE_RGN_CODE='0002' THEN '제주시' WHEN HOPE_RGN_CODE='0003' THEN '서귀포시'	\n");
		sql.append("\t	            WHEN HOPE_RGN_CODE='0004' THEN '동부' WHEN HOPE_RGN_CODE='0005' THEN '서부' END) AS HOPE_RGN_CODE	\n");
		sql.append("\t	        , (CASE WHEN CTCT_ABLE_STRT_TIME='000000' THEN '전체' WHEN CTCT_ABLE_STRT_TIME='170000' THEN '17:00시 이후'	\n");
		sql.append("\t	            ELSE SUBSTR(CTCT_ABLE_STRT_TIME,1,2)||':00~'||SUBSTR(CTCT_ABLE_END_TIME,1,2)||':00' END) AS CTCT_ABLE_TIME	\n");
		sql.append("\t	        FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	        JOIN BCDBA.TBGGOLFCDHD T6 ON T6.CDHD_ID=T1.CDHD_ID	\n");
		sql.append("\t	        JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T7 ON T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	        JOIN BCDBA.TBGCMMNCODE T8 ON T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t      	WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='J'	\n");

		if(sch_DATE.equals("roundDate")){								
			if(!sch_DATE_ST.equals(""))	sql.append("\t      	AND ROUND_HOPE_DATE>=?	\n");
			if(!sch_DATE_ED.equals(""))	sql.append("\t      	AND ROUND_HOPE_DATE<=?	\n");
		}else if (sch_DATE.equals("regDate")){		
			if(!sch_DATE_ST.equals(""))	sql.append("\t      	AND REG_ATON>=?	\n");
			if(!sch_DATE_ED.equals(""))	sql.append("\t      	AND REG_ATON<=?	\n");
		} 
		
		if(!sch_TEXT.equals("")){	
			if(sch_SORT.equals("sort_name")){			
				sql.append("\t      	AND HG_NM LIKE ?	\n");	
			}else if(sch_SORT.equals("sort_id")){	
				sql.append("\t      	AND CDHD_ID LIKE ?	\n");	
			}else{
				sql.append("\t      	AND (HP_TEL_SNO LIKE ? OR HP_DDD_NO LIKE ? OR HP_TEL_HNO LIKE ?)	\n");
			}
		}
		
		sql.append("\t	        ORDER BY T1.REG_ATON DESC	\n");
		sql.append("\t	        )	\n");
		sql.append("\t	    ORDER BY RNUM	\n");
		sql.append("\t	    )	\n");
		
		if(listtype.equals(""))	sql.append("\t	WHERE PAGE = ?	\n");

		return sql.toString();
    }
}
