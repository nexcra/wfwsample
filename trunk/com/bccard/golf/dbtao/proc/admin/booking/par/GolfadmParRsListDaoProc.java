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

package com.bccard.golf.dbtao.proc.admin.booking.par;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
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
public class GolfadmParRsListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmParRsListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		GolfAdminEtt userEtt = null;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null); 
			boolean eof = false;

			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();		
			
			 
			//조회 ----------------------------------------------------------
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_RSVT_YN = data.getString("SCH_RSVT_YN");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_SEARCH_YN = data.getString("SEARCH_YN");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");	
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			String sch_TYPE = data.getString("SCH_TYPE");	
			String sch_TEXT = data.getString("SCH_TEXT");		
			String listtype = data.getString("LISTTYPE");
			
			//아미가골프연습장 담당자 임시 꼼수
			if (admId.equals("amiga5001") ){			
				admId = "photo79";
			}
			
			String sql = this.getSelectQuery(sch_GR_SEQ_NO, sch_RSVT_YN, sch_DATE, sch_DATE_ST, sch_DATE_ED, 
					listtype, sch_TYPE, sch_TEXT, admId, admClss, sch_SEARCH_YN);
			//debug("============GolfadmParRsListDaoProc============sch_DATE => " + sch_DATE);
			debug("============GolfadmParRsListDaoProc============admClss => " + admClss);
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if("Y".equals(sch_SEARCH_YN)){
				if (!sch_GR_SEQ_NO.equals("")){	pstmt.setString(++idx, sch_GR_SEQ_NO);	}
				if (!sch_RSVT_YN.equals("") && !sch_RSVT_YN.equals("A")){	pstmt.setString(++idx, sch_RSVT_YN);	}			
				if(sch_DATE.equals("rounding")){
					if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
				}else if (sch_DATE.equals("teatime")){
					if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
				}else if (sch_DATE.equals("cncltime")){
					if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
					if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
				}	
			}
			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					if(!eof) result.addString("RESULT", "00");
					

					result.addString("RSVT_SQL_NO" 	,rs.getString("RSVT_SQL_NO") );
					result.addString("GR_NM" 		,rs.getString("GR_NM") );
					result.addString("RSVT_YN" 		,rs.getString("RSVT_YN") );
					result.addString("HP" 			,rs.getString("HP") );
					result.addString("REG_DATE" 	,rs.getString("REG_DATE") );
					result.addString("REG_YOIL" 	,rs.getString("REG_YOIL") );
					result.addString("REG_TIME" 	,rs.getString("REG_TIME") );
					result.addString("BK_DATE" 		,rs.getString("BK_DATE") );
					result.addString("NAME" 		,rs.getString("NAME") );
					result.addString("ACCOUNT" 		,rs.getString("ACCOUNT") );
					result.addString("CTNT" 		,rs.getString("CTNT") );
					result.addString("CNCL_DATE" 	,rs.getString("CNCL_DATE") );
					result.addString("GRADE_NM" 	,rs.getString("GRADE_NM") );

					result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"	,rs.getString("PAGE") );
					result.addString("RNUM"			,rs.getString("RNUM") );
					result.addInt("ART_NUM" 		,rs.getInt("ART_NUM")-art_num_no );

					
					eof = true;
					art_num_no++;
					
				}
			}
			if(!eof) result.addString("RESULT", "01");

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
    private String getSelectQuery(String sch_GR_SEQ_NO, String sch_RSVT_YN, String sch_DATE, String sch_DATE_ST, 
    		String sch_DATE_ED, String listtype, String sch_TYPE, String sch_TEXT, String admId, String admClss, String sch_SEARCH_YN){
        StringBuffer sql = new StringBuffer();      
        
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, RSVT_SQL_NO, GR_NM, RSVT_YN, HP, REG_DATE, REG_YOIL, REG_TIME, BK_DATE, NAME, ACCOUNT, CTNT, CNCL_DATE, GRADE_NM 	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	");
		
		sql.append("\n 		, T1.GOLF_SVC_RSVT_NO AS RSVT_SQL_NO  	");	
		sql.append("\n 		, T2.GREEN_NM AS GR_NM  	");	
		sql.append("\n 		, (CASE WHEN T1.RSVT_YN='Y' THEN '예약완료' WHEN T1.RSVT_YN='N' THEN '취소완료' WHEN T1.RSVT_YN='I' THEN '임박취소' WHEN T1.RSVT_YN='D' THEN '불참' ELSE '' END) RSVT_YN  	");
		
		sql.append("\n 		, (T1.HP_DDD_NO||'-'||T1.HP_TEL_HNO||'-'||T1.HP_TEL_SNO) AS HP  	");	
		sql.append("\n 		, (TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON,1,8)),'YY-MM-DD')) AS REG_DATE  	");	
		sql.append("\n 		, (TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON,1,8)),'DY')) AS REG_YOIL  	");	
		sql.append("\n 		, (SUBSTR(T1.REG_ATON,9,2)||':'||SUBSTR(T1.REG_ATON,11,2)) AS REG_TIME  	");	
		sql.append("\n 		, (TO_CHAR(TO_DATE(T1.ROUND_HOPE_DATE),'YY-MM-DD(DY)')) AS BK_DATE  	");	
		sql.append("\n 		, T6.HG_NM AS NAME, T6.CDHD_ID AS ACCOUNT, T1.CTNT  	");	
		
		sql.append("\n 		, CASE T1.RSVT_YN WHEN 'Y' THEN '' WHEN 'D' THEN ''  	");
		sql.append("\n 		ELSE TO_CHAR(TO_DATE(SUBSTR(T1.CNCL_ATON, 0, 8)), 'YYYY-MM-DD (DY) ')  	");
		sql.append("\n 		||SUBSTR(T1.CNCL_ATON, 9, 2)||':'||SUBSTR(T1.CNCL_ATON, 11, 2) END AS CNCL_DATE  	");
		sql.append("\n 		, T8.GOLF_CMMN_CODE_NM AS GRADE_NM  	");	
		
		sql.append("\n 		FROM  	");	
		sql.append("\n 		BCDBA.TBGRSVTMGMT T1  	");	
		sql.append("\n 		JOIN BCDBA.TBGAFFIGREEN T2 ON T1.AFFI_GREEN_SEQ_NO=T2.AFFI_GREEN_SEQ_NO  	");	
		sql.append("\n 		JOIN BCDBA.TBGGOLFCDHD T6 ON T6.CDHD_ID=T1.CDHD_ID 	");
		sql.append("\n 		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T7 ON T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO 	");
		sql.append("\n 		JOIN BCDBA.TBGCMMNCODE T8 ON T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005' 	");

		sql.append("\n 		WHERE T1.GOLF_SVC_RSVT_NO IS NOT NULL AND SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='P'  	");	
		
		if(!admClss.equals("master")){		sql.append("\n		AND T2.GREEN_ID='"+admId+"'  	");			}
		
		sql.append("\n  	");	
		
		if("Y".equals(sch_SEARCH_YN)){ 
			if(!sch_GR_SEQ_NO.equals("")){		sql.append("\n		AND T2.AFFI_GREEN_SEQ_NO=?  	");			}
			if(!sch_RSVT_YN.equals("") && !sch_RSVT_YN.equals("A")){		sql.append("\n 		AND T1.RSVT_YN = ?  	");			}
			
			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.ROUND_HOPE_DATE>=?  	");			}
				if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.ROUND_HOPE_DATE<=?  	");			}			
			}else if (sch_DATE.equals("teatime")){
				if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=?  	");			}
				if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=?  	");			}
			}else if(sch_DATE.equals("cncltime")){
				if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.CNCL_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD') >= ?  	");			}
				if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND TO_CHAR(TO_DATE(T1.CNCL_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD') <= ?  	");			}
			}
			
			if(!GolfUtil.empty(sch_TEXT)){
				if(sch_TYPE.equals("HP")){
					sql.append("\n AND (T1.HP_DDD_NO LIKE '%"+sch_TEXT+"%' OR T1.HP_TEL_HNO LIKE '%"+sch_TEXT+"%'  OR T1.HP_TEL_SNO LIKE '%"+sch_TEXT+"%'  ) ");
				}else{
					sql.append("\n 		AND "+sch_TYPE+" like '%"+sch_TEXT+"%'  	");
				}
			}
		}
		sql.append("\n  	");	
		sql.append("\n 		ORDER BY T1.REG_ATON DESC  	");		
		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}

		return sql.toString();
    }
}
