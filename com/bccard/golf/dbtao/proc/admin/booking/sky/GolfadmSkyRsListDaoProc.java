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

package com.bccard.golf.dbtao.proc.admin.booking.sky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스    
 * @version	1.0
 ******************************************************************************/
public class GolfadmSkyRsListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSkyRsListDaoProc() {}	

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
			String sch_HOLE				= data.getString("SCH_HOLE");
			String sch_RESER			= data.getString("SCH_RESER");
			String sch_DATE				= data.getString("SCH_DATE");
			String sch_SEARCH_YN		= data.getString("SEARCH_YN");
			String sch_DATE_ST			= data.getString("SCH_DATE_ST");
			String sch_DATE_ED			= data.getString("SCH_DATE_ED");
			String sch_SORT				= data.getString("SCH_SORT");
			String sch_TEXT				= data.getString("SCH_TEXT");	
			String listtype 			= data.getString("LISTTYPE");
			String sql = this.getSelectQuery(sch_HOLE, sch_RESER, sch_DATE, sch_DATE_ST, 
					sch_DATE_ED, sch_SORT, sch_TEXT, listtype, sch_SEARCH_YN);   	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if("Y".equals(sch_SEARCH_YN)){
				if (!sch_HOLE.equals("")){		pstmt.setString(++idx, sch_HOLE);	}
				if (!sch_RESER.equals("")){		pstmt.setString(++idx, sch_RESER);	}	
	
				
				if(sch_DATE.equals("regDate")){
					if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
				}else if (sch_DATE.equals("bkDate")){
					if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
				}else if (sch_DATE.equals("cnclDate")){
					if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
				}	
				/*
				if(!sch_DATE_ST.equals("")){
					if(sch_DATE.equals("regDate")){
						if (!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST+"000000");	}
					}else if(sch_DATE.equals("bkDate")){
						if (!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					}
				}
				if(!sch_DATE_ED.equals("")){
					if(sch_DATE.equals("regDate")){
						if (!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED+"999999");	}
					}else if(sch_DATE.equals("bkDate")){
						if (!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}
				}
				*/

				if(!sch_TEXT.equals("")){	
					if(sch_SORT.equals("sort_id")){
						pstmt.setString(++idx, "%"+sch_TEXT+"%");																							
					}else if(sch_SORT.equals("sort_name")){
						pstmt.setString(++idx, "%"+sch_TEXT+"%");																							
					}else if(sch_SORT.equals("sort_ph")){
						pstmt.setString(++idx, "%"+sch_TEXT+"%");	
						pstmt.setString(++idx, "%"+sch_TEXT+"%");	
						pstmt.setString(++idx, "%"+sch_TEXT+"%");	
					}
				}
			}
			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addString("RSVT_SQL_NO" 			,rs.getString("RSVT_SQL_NO") );
					result.addString("HOLE" 				,rs.getString("HOLE") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("BK_TIME" 				,rs.getString("BK_TIME") );
					result.addString("RSVT_YN" 				,rs.getString("RSVT_YN") );
					result.addString("HP" 					,rs.getString("HP") );
					result.addString("REG_DATE" 			,rs.getString("REG_DATE") );
					result.addString("REG_TIME" 			,rs.getString("REG_TIME") );
					result.addString("NAME" 				,rs.getString("NAME") );
					result.addString("SOCID" 				,rs.getString("SOCID") );
					result.addString("TOT_PERS_NUM" 		,rs.getString("TOT_PERS_NUM") );
					result.addString("CNCL_DATE" 			,rs.getString("CNCL_DATE") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("CTNT" 				,rs.getString("CTNT") );
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
    private String getSelectQuery(String sch_HOLE, String sch_RESER, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, 
    		String sch_SORT, String sch_TEXT, String listtype, String sch_SEARCH_YN){
        StringBuffer sql = new StringBuffer();      

        
		sql.append("\n SELECT	*																														\n");
		sql.append("\n FROM (SELECT ROWNUM RNUM																											\n");
		sql.append("\n 		, RSVT_SQL_NO, HOLE, BK_DATE, BK_TIME, RSVT_YN, HP, REG_DATE, REG_TIME, NAME  												\n");
		sql.append("\n 		, SOCID, TOT_PERS_NUM, CNCL_DATE, CDHD_ID, CTNT, GRADE_NM								  												\n");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE																									\n");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT																									\n");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM																									\n");
		
		sql.append("\n 			, T1.GOLF_SVC_RSVT_NO AS RSVT_SQL_NO, CTNT																				\n");
		sql.append("\t 			, (CASE WHEN T3.SKY72_HOLE_CODE='0001' THEN '7' ELSE '14' END) AS HOLE													\n");
		sql.append("\t 			, TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE),'YY-MM-DD(DY)') AS BK_DATE															\n");
		sql.append("\t 			, SUBSTR(T2.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(T2.BOKG_ABLE_TIME,3,4) AS BK_TIME											\n");
		sql.append("\t 			, (CASE WHEN T1.RSVT_YN='Y' THEN '예약완료' WHEN T1.RSVT_YN='N' THEN '취소완료' WHEN T1.RSVT_YN='I' THEN '임박취소' WHEN T1.RSVT_YN='D' THEN '불참' ELSE '' END) RSVT_YN	\n");
		sql.append("\t 			, (T1.HP_DDD_NO||'-'||T1.HP_TEL_HNO||'-'||T1.HP_TEL_SNO) AS HP															\n");
		sql.append("\t 			, (TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON,1,8)),'YY-MM-DD(DY)')) AS REG_DATE												\n");
		sql.append("\t 			, (SUBSTR(T1.REG_ATON,9,2)||':'||SUBSTR(T1.REG_ATON,11,2)) AS REG_TIME													\n");
		sql.append("\n 			, T6.HG_NM as NAME, SUBSTR(T6.JUMIN_NO,1,6) AS SOCID, TOT_PERS_NUM, T1.CDHD_ID  		\n");
		sql.append("\t 			, (CASE WHEN CNCL_ATON IS NULL THEN '' ELSE	\n");
		sql.append("\t 			TO_CHAR(TO_DATE(SUBSTR(T1.CNCL_ATON, 0, 8)), 'YYYY-MM-DD')||'('||	\n");
		sql.append("\t 			TO_CHAR(TO_DATE(SUBSTR(T1.CNCL_ATON, 0, 8)),'DY')||') '||	\n");
		sql.append("\t 			SUBSTR(T1.CNCL_ATON, 9, 2)||':'||SUBSTR(T1.CNCL_ATON, 11, 2) END) AS CNCL_DATE	\n");
		sql.append("\n 			, T8.GOLF_CMMN_CODE_NM AS GRADE_NM  	");	
		
		sql.append("\t 		FROM BCDBA.TBGRSVTMGMT T1																									\n");
		sql.append("\t 		JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO						\n");
		sql.append("\t 		JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO											\n");
		sql.append("\t 		JOIN BCDBA.TBGGOLFCDHD T6 ON T6.CDHD_ID=T1.CDHD_ID 	");
		sql.append("\n 		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T7 ON T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO 	");
		sql.append("\n 		JOIN BCDBA.TBGCMMNCODE T8 ON T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005' 	");
		sql.append("\t 		WHERE SUBSTR(T1.GOLF_SVC_RSVT_NO,5,1)='S'																					\n");
		
		if("Y".equals(sch_SEARCH_YN)){
			if(!sch_HOLE.equals("")){
				sql.append("\t 			AND T3.SKY72_HOLE_CODE=?																							\n");
			}
			if(!sch_RESER.equals("")){
				sql.append("\t 			AND T1.RSVT_YN=?																									\n");
			}
			if(sch_DATE.equals("regDate")){
				if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=?   	");			}
				if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=?  	");			}			
			}else if (sch_DATE.equals("bkDate")){
				if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T3.BOKG_ABLE_DATE>=?  	");			}
				if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T3.BOKG_ABLE_DATE<=?  	");			}
			}else if(sch_DATE.equals("cnclDate")){
				if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.CNCL_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD') >= ?  	");			}
				if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.CNCL_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD') <= ?  	");			}
			}
			/*
			if(!sch_DATE_ST.equals("")){
				if(sch_DATE.equals("regDate")){
					sql.append("\t 			AND T1.REG_ATON>=?																								\n");
				}else{
					sql.append("\t 			AND T3.BOKG_ABLE_DATE>=?																						\n");
				}
			}
			if(!sch_DATE_ED.equals("")){
				if(sch_DATE.equals("regDate")){
					sql.append("\t 			AND T1.REG_ATON<=?																								\n");
				}else{
					sql.append("\t 			AND T3.BOKG_ABLE_DATE<=?																						\n");
				}
			}
			*/
			if(!sch_TEXT.equals("")){	
				if(sch_SORT.equals("sort_id")){
					sql.append("\t 			AND CDHD_ID LIKE ?																									\n");
				}else if(sch_SORT.equals("sort_name")){
					sql.append("\t 			AND T6.HG_NM LIKE ?																									\n");
				}else if(sch_SORT.equals("sort_ph")){
					sql.append("\t 			AND  (T1.HP_DDD_NO LIKE ? OR T1.HP_TEL_HNO LIKE ? OR T1.HP_TEL_SNO LIkE ? )		\n");
				}
			}
		}
		sql.append("\n 		ORDER BY T1.REG_ATON DESC  																									\n");		
		
		sql.append("\n 			)																														\n");
		sql.append("\n 	ORDER BY RNUM																													\n");
		sql.append("\n 	)																																\n");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}		

		return sql.toString();
    }
}
