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

package com.bccard.golf.dbtao.proc.admin.booking.premium;

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
public class GolfadmPreAvgListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreAvgListDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);

			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			
			//조회 ----------------------------------------------------------
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_GR_NM = data.getString("SCH_GR_NM");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			String listtype = data.getString("LISTTYPE");		
			String sort = data.getString("SORT");		
			if(sort.equals("0003")){
				sql = this.getSkySelectQuery(sort, sch_GR_SEQ_NO, sch_GR_NM, sch_DATE_ST, sch_DATE_ED, listtype);
			}else{
				sql = this.getSelectQuery(sort, sch_GR_SEQ_NO, sch_GR_NM, sch_DATE_ST, sch_DATE_ED, listtype);
			}
			
			debug("GolfadmPreRsListDaoProc========sort => " + sort); 
			//debug("GolfadmPreRsListDaoProc========sch_TEXT => " + sch_TEXT);
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));		
			if(listtype.equals("")){ pstmt.setLong(++idx, data.getLong("page_no"));	 }
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addString("CNT_A" 				,rs.getString("CNT_A") );
					result.addString("CNT_Y" 				,rs.getString("CNT_Y") );
					result.addString("CNT_N" 				,rs.getString("CNT_N") );
					result.addString("CNT_I" 				,rs.getString("CNT_I") );
					result.addString("CNT_D" 				,rs.getString("CNT_D") );
					result.addString("AVG_A" 				,rs.getString("AVG_A") );
					result.addString("AVG_Y" 				,rs.getString("AVG_Y") );
					
					if(!sort.equals("0003")){
						result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
						result.addString("AFFI_GREEN_SEQ_NO"	,rs.getString("AFFI_GREEN_SEQ_NO") );
					}else{
					
						if(sch_GR_SEQ_NO.equals("0001")){
							result.addString("GREEN_NM" 			,"7홀" );
						}else if(sch_GR_SEQ_NO.equals("0002")){
							result.addString("GREEN_NM" 			,"14홀" );
						}else{
							result.addString("GREEN_NM" 			,"전체" );
						}
					}

					result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"	,rs.getString("PAGE") );
					result.addString("RNUM"			,rs.getString("RNUM") );
					result.addInt("ART_NUM" 		,rs.getInt("ART_NUM")-art_num_no );
					
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
    * Query를 생성하여 리턴한다. - VIP, 파3  
    ************************************************************************ */
    private String getSelectQuery(String sort, String sch_GR_SEQ_NO, String sch_GR_NM, String sch_DATE_ST, String sch_DATE_ED, String listtype){
        StringBuffer sql = new StringBuffer();      
        
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, GREEN_NM, AFFI_GREEN_SEQ_NO, CNT_A, CNT_Y, CNT_N, CNT_I, CNT_D, AVG_A, AVG_Y  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	\n");
		
		sql.append("\t	, GREEN_NM, AFFI_GREEN_SEQ_NO, CNT_A, CNT_Y, CNT_N, CNT_I, CNT_D	\n");
		sql.append("\t	, TO_CHAR(AVG_A,'FM999,990.0') AVG_A, TO_CHAR(AVG_Y,'FM999,990.0') AVG_Y	\n");
		sql.append("\t	    FROM (	\n");

		sql.append("\t	    SELECT GREEN_NM, AFFI_GREEN_SEQ_NO, CNT_A, CNT_Y, CNT_N, CNT_I, CNT_D	\n");
		sql.append("\t	    , CASE CNT_A WHEN 0 THEN 0 ELSE (CNT_A-(CNT_N+CNT_I))/CNT_A*100 END AVG_A	\n");
		sql.append("\t	    , CASE CNT_A WHEN 0 THEN 0 ELSE CNT_Y/CNT_A*100 END AVG_Y	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT GREEN_NM, AFFI_GREEN_SEQ_NO	\n");
		
		sql.append("\t	        , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO	\n");
		
		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND SUBSTR(REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");	}
		if(!GolfUtil.empty(sch_DATE_ST)){	sql.append("\t	        AND REG_ATON>='"+sch_DATE_ST+"000000' 	\n");	}
		if(!GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND REG_ATON<='"+sch_DATE_ED+"999999'	\n");	}
		
		sql.append("\t	        ) AS CNT_A	\n");
		sql.append("\t	        , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='Y'	\n");
		
		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND SUBSTR(REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");	}
		if(!GolfUtil.empty(sch_DATE_ST)){	sql.append("\t	        AND REG_ATON>='"+sch_DATE_ST+"000000' 	\n");	}
		if(!GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND REG_ATON<='"+sch_DATE_ED+"999999'	\n");	}
		
		sql.append("\t	        ) AS CNT_Y	\n");
		sql.append("\t	        , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='N'	\n");
		
		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND SUBSTR(REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");	}
		if(!GolfUtil.empty(sch_DATE_ST)){	sql.append("\t	        AND REG_ATON>='"+sch_DATE_ST+"000000' 	\n");	}
		if(!GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND REG_ATON<='"+sch_DATE_ED+"999999'	\n");	}
		
		sql.append("\t	        ) AS CNT_N	\n");
		sql.append("\t	        , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='I'	\n");
		
		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND SUBSTR(REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");	}
		if(!GolfUtil.empty(sch_DATE_ST)){	sql.append("\t	        AND REG_ATON>='"+sch_DATE_ST+"000000' 	\n");	}
		if(!GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND REG_ATON<='"+sch_DATE_ED+"999999'	\n");	}
		
		sql.append("\t	        ) AS CNT_I	\n");
		sql.append("\t	        , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='D'	\n");
		
		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND SUBSTR(REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");	}
		if(!GolfUtil.empty(sch_DATE_ST)){	sql.append("\t	        AND REG_ATON>='"+sch_DATE_ST+"000000' 	\n");	}
		if(!GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	        AND REG_ATON<='"+sch_DATE_ED+"999999'	\n");	}
		
		sql.append("\t	        ) AS CNT_D	\n");

		sql.append("\t	        FROM BCDBA.TBGAFFIGREEN T1	\n");
		sql.append("\t	        WHERE AFFI_FIRM_CLSS='"+sort+"'	\n");
		
		if(!GolfUtil.empty(sch_GR_NM)){			sql.append("\t	        AND GREEN_NM LIKE '%"+sch_GR_NM+"%'	\n");	}
		if(!GolfUtil.empty(sch_GR_SEQ_NO)){		sql.append("\t	        AND AFFI_GREEN_SEQ_NO='"+sch_GR_SEQ_NO+"'	\n");	}
		
		sql.append("\t	        ORDER BY REG_ATON DESC	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	)	\n");
		
		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	"); 
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}

		return sql.toString();
    }
    
	/** ***********************************************************************
     * Query를 생성하여 리턴한다. - 스카이 드림듄스
     ************************************************************************ */
     private String getSkySelectQuery(String sort, String sch_GR_SEQ_NO, String sch_GR_NM, String sch_DATE_ST, String sch_DATE_ED, String listtype){
         StringBuffer sql = new StringBuffer();      
         
 		sql.append("\n SELECT	*	");
 		sql.append("\n FROM (SELECT ROWNUM RNUM	");
 		sql.append("\n 		, CNT_A, CNT_Y, CNT_N, CNT_I, CNT_D, AVG_A, AVG_Y  	");		
 		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
 		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
 		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	\n");	
 		sql.append("\t		FROM (SELECT ROWNUM RNUM	\n");
 		
 		
 		sql.append("\t		, CNT_A, CNT_Y, CNT_N, CNT_I, CNT_D	\n");	
 		sql.append("\t		, TO_CHAR(AVG_A,'FM999,990.0') AVG_A, TO_CHAR(AVG_Y,'FM999,990.0') AVG_Y	\n");	
 		sql.append("\t		FROM (	\n");	
 		sql.append("\t		    SELECT CNT_A, CNT_Y, CNT_N, CNT_I, CNT_D	\n");	
 		sql.append("\t		    , CASE CNT_A WHEN 0 THEN 0 ELSE (CNT_A-(CNT_N+CNT_I))/CNT_A*100 END AVG_A	\n");	
 		sql.append("\t		    , CASE CNT_A WHEN 0 THEN 0 ELSE CNT_Y/CNT_A*100 END AVG_Y	\n");	
 		sql.append("\t		    FROM(	\n");	
 		sql.append("\t		        SELECT (SELECT COUNT(*)	\n");	
 		sql.append("\t		        FROM BCDBA.TBGRSVTMGMT T1	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");	
 		sql.append("\t		        WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='S'	\n");	
 		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");}
 		if(!GolfUtil.empty(sch_DATE_ST)){sql.append("\t	AND T1.REG_ATON>='"+sch_DATE_ST+"000000'	\n");}	
 		if(!GolfUtil.empty(sch_DATE_ED)){sql.append("\t	AND T1.REG_ATON<='"+sch_DATE_ED+"999999'	\n");}	
 		if(!GolfUtil.empty(sch_GR_SEQ_NO)){	sql.append("\t		        AND T3.SKY72_HOLE_CODE='"+sch_GR_SEQ_NO+"'	\n");}		
 		sql.append("\t		        ) AS CNT_A	\n");	
 		sql.append("\t		        , (SELECT COUNT(*)	\n");	
 		sql.append("\t		        FROM BCDBA.TBGRSVTMGMT T1	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");	
 		sql.append("\t		        WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='S' AND RSVT_YN='Y'	\n");	
 		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");}
 		if(!GolfUtil.empty(sch_DATE_ST)){sql.append("\t	AND T1.REG_ATON>='"+sch_DATE_ST+"000000'	\n");}	
 		if(!GolfUtil.empty(sch_DATE_ED)){sql.append("\t	AND T1.REG_ATON<='"+sch_DATE_ED+"999999'	\n");}	
 		if(!GolfUtil.empty(sch_GR_SEQ_NO)){	sql.append("\t		        AND T3.SKY72_HOLE_CODE='"+sch_GR_SEQ_NO+"'	\n");}		
 		sql.append("\t		        ) AS CNT_Y	\n");	
 		sql.append("\t		        , (SELECT COUNT(*)	\n");	
 		sql.append("\t		        FROM BCDBA.TBGRSVTMGMT T1	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");	
 		sql.append("\t		        WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='S' AND RSVT_YN='N'	\n");	
 		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");}
 		if(!GolfUtil.empty(sch_DATE_ST)){sql.append("\t	AND T1.REG_ATON>='"+sch_DATE_ST+"000000'	\n");}	
 		if(!GolfUtil.empty(sch_DATE_ED)){sql.append("\t	AND T1.REG_ATON<='"+sch_DATE_ED+"999999'	\n");}	
 		if(!GolfUtil.empty(sch_GR_SEQ_NO)){	sql.append("\t		        AND T3.SKY72_HOLE_CODE='"+sch_GR_SEQ_NO+"'	\n");}		
 		sql.append("\t		        ) AS CNT_N	\n");	
 		sql.append("\t		        , (SELECT COUNT(*)	\n");	
 		sql.append("\t		        FROM BCDBA.TBGRSVTMGMT T1	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");	
 		sql.append("\t		        WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='S' AND RSVT_YN='I'	\n");
 		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");}
 		if(!GolfUtil.empty(sch_DATE_ST)){sql.append("\t	AND T1.REG_ATON>='"+sch_DATE_ST+"000000'	\n");}	
 		if(!GolfUtil.empty(sch_DATE_ED)){sql.append("\t	AND T1.REG_ATON<='"+sch_DATE_ED+"999999'	\n");}	
 		if(!GolfUtil.empty(sch_GR_SEQ_NO)){	sql.append("\t		        AND T3.SKY72_HOLE_CODE='"+sch_GR_SEQ_NO+"'	\n");}		
 		sql.append("\t		        ) AS CNT_I	\n");	
 		sql.append("\t		        , (SELECT COUNT(*)	\n");	
 		sql.append("\t		        FROM BCDBA.TBGRSVTMGMT T1	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");	
 		sql.append("\t		        JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");	
 		sql.append("\t		        WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='S' AND RSVT_YN='D'	\n");	
 		if(GolfUtil.empty(sch_DATE_ST) && GolfUtil.empty(sch_DATE_ED)){	sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");}
 		if(!GolfUtil.empty(sch_DATE_ST)){sql.append("\t	AND T1.REG_ATON>='"+sch_DATE_ST+"000000'	\n");}	
 		if(!GolfUtil.empty(sch_DATE_ED)){sql.append("\t	AND T1.REG_ATON<='"+sch_DATE_ED+"999999'	\n");}	
 		if(!GolfUtil.empty(sch_GR_SEQ_NO)){	sql.append("\t		        AND T3.SKY72_HOLE_CODE='"+sch_GR_SEQ_NO+"'	\n");}		
 		sql.append("\t		        ) AS CNT_D FROM DUAL	\n");	
 		sql.append("\t		    )	\n");	
 		sql.append("\t		)	\n");
 		 		
 		
 		
 		sql.append("\n 			)	");
 		sql.append("\n 	ORDER BY RNUM	");
 		sql.append("\n 	)	"); 
 		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}

 		return sql.toString();
     }
}
