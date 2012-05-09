/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.member;

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
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfAdmMemBkListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자    
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemBkListDaoProc() {}	

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
			
			
			String cdhd_id	= data.getString("CDHD_ID");	
			String order_yn		= data.getString("ORDER_YN");	
			String order_nm		= data.getString("ORDER_NM");	
			String order_value	= data.getString("ORDER_VALUE");
			
			
			String s_date ="";
			String e_date = "";

			int idx = 0;
			String sql = this.getMemGrdQuery();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, cdhd_id);
			pstmt.setString(++idx, cdhd_id);
			pstmt.setString(++idx, cdhd_id);
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					s_date = rs.getString("SDATE");
					e_date = rs.getString("EDATE");
				}
			}

			//조회 ----------------------------------------------------------
			sql = this.getSelectQuery(cdhd_id, order_yn, order_nm, order_value);   

			// 입력값 (INPUT)         
			idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("bk_page_no"));
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setString(++idx, s_date.replaceAll("-", ""));
			pstmt.setString(++idx, e_date.replaceAll("-", ""));
			pstmt.setString(++idx, cdhd_id);
			pstmt.setString(++idx, cdhd_id);
			pstmt.setLong(++idx, data.getLong("bk_page_no"));
			
			rs = pstmt.executeQuery();
			int art_num_no = 0; 
			if(rs != null) {			
				String reg_aton = "";

				while(rs.next())  {	
					reg_aton = rs.getString("REG_ATON").substring(0,4)+"-"+rs.getString("REG_ATON").substring(4,6)+"-"+rs.getString("REG_ATON").substring(6,8);
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					
					result.addString("CLSS" 			,rs.getString("CLSS") );
					result.addString("REG_ATON" 		, reg_aton);
					result.addString("BK_NM" 			,rs.getString("BK_NM") );
					result.addString("CDHD_ID"			,rs.getString("CDHD_ID") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BK_DY" 			,rs.getString("BK_DY") );
					result.addString("COURSE" 			,rs.getString("COURSE") );
					result.addString("CH_ATON" 			,rs.getString("CH_ATON") );
					result.addString("STATE" 			,rs.getString("STATE") );
					result.addString("COLOR" 			,rs.getString("COLOR") );
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
	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult memGrd_execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			String cdhd_id		= data.getString("CDHD_ID");
			 
			//조회 ----------------------------------------------------------
			int idx = 0;
			String sql = this.getMemGrdQuery();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, cdhd_id);
			pstmt.setString(++idx, cdhd_id);
			pstmt.setString(++idx, cdhd_id);
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	
															
					result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
					result.addString("GRD_KN" 			,rs.getString("GRD_KN") );
					result.addString("SDATE" 			,rs.getString("SDATE") );
					result.addString("EDATE"			,rs.getString("EDATE") );
					
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
    private String getSelectQuery(String cdhd_id, String order_yn, String order_nm, String order_value){
        StringBuffer sql = new StringBuffer();        
        
		sql.append("\n	SELECT	*	\n");
		sql.append("\t	FROM (SELECT ROWNUM RNUM	\n");
		sql.append("\t	, CEIL(ROWNUM/?) AS PAGE	\n");
		sql.append("\t	, MAX(RNUM) OVER() TOT_CNT	\n");
		sql.append("\t 			, ((MAX(RNUM) OVER())-(?-1)*?) AS ART_NUM	\n");
		sql.append("\t 			, CLSS, REG_ATON, BK_NM, CDHD_ID, GR_NM, BK_DY, COURSE, CH_ATON, STATE	\n");
		sql.append("\t 			, (CASE WHEN SUBSTR(REG_ATON,1,8) >= ? AND SUBSTR(REG_ATON,1,8) <= ? AND STATE = '예약'  THEN 'RED' ELSE 'BLACK' END ) AS COLOR ");
		sql.append("\t 			FROM (SELECT ROWNUM RNUM	\n");
		sql.append("\t	, CLSS, REG_ATON, BK_NM, CDHD_ID, GR_NM, BK_DY, COURSE, CH_ATON, STATE	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT APL.GOLF_SVC_RSVT_MAX_VAL CLSS, APL.REG_ATON, CODE.GOLF_CMMN_CODE_NM BK_NM, APL.CDHD_ID	\n");
		sql.append("\t	    , CASE SUBSTR(APL.GOLF_SVC_RSVT_MAX_VAL,5,1)	\n");
		sql.append("\t	        WHEN 'M' THEN GREEN.GREEN_NM	\n");
		sql.append("\t	        WHEN 'P' THEN GREEN.GREEN_NM	\n");
		sql.append("\t	        END GR_NM	\n");
		sql.append("\t	    , CASE SUBSTR(APL.GOLF_SVC_RSVT_MAX_VAL,5,1)	\n");
		sql.append("\t	        WHEN 'M' THEN TO_CHAR(TO_DATE(BK_DY.BOKG_ABLE_DATE),'YYYY-MM-DD')||' '||SUBSTR(BK_TIME.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(BK_TIME.BOKG_ABLE_TIME,3,2)	\n");
		sql.append("\t	        WHEN 'P' THEN TO_CHAR(TO_DATE(APL.ROUND_HOPE_DATE),'YYYY-MM-DD')	\n");
		sql.append("\t	        WHEN 'D' THEN SUBSTR(BK_DY.RSVT_ABLE_DATE,1,4)||'-'||SUBSTR(BK_DY.RSVT_ABLE_DATE,5,2)||'-'||SUBSTR(BK_DY.RSVT_ABLE_DATE,7,2)	\n");
		sql.append("\t	        WHEN 'S' THEN TO_CHAR(TO_DATE(BK_DY.BOKG_ABLE_DATE),'YYYY-MM-DD')||' '||SUBSTR(BK_TIME.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(BK_TIME.BOKG_ABLE_TIME,3,2)	\n");
		sql.append("\t	        WHEN 'J' THEN TO_CHAR(TO_DATE(APL.ROUND_HOPE_DATE),'YYYY-MM-DD')	\n");
		sql.append("\t	        WHEN 'F' THEN TO_CHAR(TO_DATE(APL.ROUND_HOPE_DATE),'YYYY-MM-DD')||' '||APL.ROUND_HOPE_TIME	\n");
		sql.append("\t	        END BK_DY	\n");
		sql.append("\t	    , CASE SUBSTR(APL.GOLF_SVC_RSVT_MAX_VAL,5,1)	\n");
		sql.append("\t	        WHEN 'M' THEN BK_DY.GOLF_RSVT_CURS_NM	\n");
		sql.append("\t	        END COURSE	\n");
		sql.append("\t	    , TO_CHAR(TO_DATE(SUBSTR(APL.CNCL_ATON,1,8)),'YYYY-MM-DD') CH_ATON	\n");
		sql.append("\t	    , STT.GOLF_CMMN_CODE_NM STATE	\n");
		sql.append("\t	    FROM BCDBA.TBGRSVTMGMT APL	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCMMNCODE CODE ON SUBSTR(APL.GOLF_SVC_RSVT_MAX_VAL,5,1)=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0047'	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGAFFIGREEN GREEN ON APL.AFFI_GREEN_SEQ_NO=GREEN.AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT BK_TIME ON APL.RSVT_ABLE_BOKG_TIME_SEQ_NO=BK_TIME.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT BK_DY ON BK_DY.RSVT_ABLE_SCD_SEQ_NO=BK_TIME.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCMMNCODE STT ON APL.RSVT_YN=STT.GOLF_CMMN_CODE AND STT.GOLF_CMMN_CLSS='0055'	\n");
		sql.append("\t	    WHERE SUBSTR(APL.GOLF_SVC_RSVT_MAX_VAL,5,1) IN ('M','P','S','J','D') AND APL.CDHD_ID=?	\n");
		sql.append("\t	    UNION ALL	\n");
		
		sql.append("\t	    SELECT APL.GOLF_SVC_APLC_CLSS CLSS, APL.REG_ATON, CODE.GOLF_CMMN_CODE_NM BK_NM, APL.CDHD_ID	\n");
		sql.append("\t	    , CASE APL.GOLF_SVC_APLC_CLSS	\n");
		sql.append("\t	        WHEN '9001' THEN APL.DPRT_PL_INFO	\n");
		sql.append("\t	        WHEN '0004' THEN APL.GREEN_NM	\n");
		sql.append("\t	        END GR_NM	\n");
		sql.append("\t	    , CASE APL.GOLF_SVC_APLC_CLSS	\n");
		sql.append("\t	        WHEN '9001' THEN TO_CHAR(TO_DATE(APL.PU_DATE),'YYYY-MM-DD') || ' ' ||APL.PU_TIME	\n");
		sql.append("\t	        WHEN '0004' THEN TO_CHAR(TO_DATE(APL.TEOF_DATE),'YYYY-MM-DD') || ' ' ||APL.TEOF_TIME	\n");
		sql.append("\t	        WHEN '0006' THEN TO_CHAR(TO_DATE(SUBSTR(APL.REG_ATON,1,8)),'YYYY-MM-DD')	\n");
		sql.append("\t	        WHEN '0007' THEN TO_CHAR(TO_DATE(SUBSTR(APL.REG_ATON,1,8)),'YYYY-MM-DD')	\n");
		sql.append("\t	        WHEN '0008' THEN TO_CHAR(TO_DATE(SUBSTR(APL.REG_ATON,1,8)),'YYYY-MM-DD')	\n");
		sql.append("\t	        END BK_DY	\n");
		sql.append("\t	    , '' COURSE	\n");
		sql.append("\t	    , TO_CHAR(TO_DATE(APL.CHNG_ATON),'YYYY-MM-DD') CH_ATON	\n");
		sql.append("\t	    , STT.GOLF_CMMN_CODE_NM STATE	\n");
		sql.append("\t	    FROM BCDBA.TBGAPLCMGMT APL	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCMMNCODE CODE ON APL.GOLF_SVC_APLC_CLSS=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0048'	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCMMNCODE STT ON APL.PGRS_YN=STT.GOLF_CMMN_CODE AND STT.GOLF_CMMN_CLSS='0049'	\n");
		sql.append("\t	    WHERE APL.GOLF_SVC_APLC_CLSS IN ('0006', '0007', '9001', '0008') AND APL.CDHD_ID=?	\n");
		sql.append("\t	)	\n");
		
		
		sql.append("\t ORDER BY	\n");
		if("Y".equals(order_yn)){
			sql.append("\t "+order_nm+" "+order_value+" \n");
		}else{
			sql.append("\t REG_ATON DESC\n");
		}
		sql.append("\t 			)		\n");
		sql.append("\t 	ORDER BY RNUM	\n");
		sql.append("\t 	)				\n");
		sql.append("\t WHERE PAGE = ?	\n");		

		return sql.toString();
    }

	/** ***********************************************************************
    * 회원 유료기간    
    ************************************************************************ */
    private String getMemGrdQuery(){
    	
        StringBuffer sql = new StringBuffer();    
        
		sql.append("	\n");
		sql.append("\t	SELECT CODE2.GOLF_CMMN_CODE_NM GRD_NM, CODE1.GOLF_CMMN_CODE_NM GRD_KN	   	 \n");	
		
		sql.append("\t	, NVL(CASE GRDM.CDHD_SQ1_CTGO	   	 										 \n");
		sql.append("\t		  WHEN '0001' THEN    	 												 \n");		
		sql.append("\t		  DECODE(GRD.CDHD_CTGO_SEQ_NO \n");
		sql.append("\t					, 28, (SELECT TO_CHAR(TO_DATE(CAMP_STRT_DATE,'YYYYMMDDHH24MISS'), 'YYYYMMDD')   	 \n");		
		sql.append("\t		  				   FROM BCDBA.TBACRGCDHDLODNTBL 	 					 \n");		
		sql.append("\t		   				   WHERE MEMO_EXPL = '0028' 	 						 \n");
		sql.append("\t		   				   AND PROC_RSLT_CTNT  = ? 	) ,	 						 \n");		
		sql.append("\t		  			TO_CHAR(TO_DATE(SUBSTR(GRD.REG_ATON,1,8)),'YYYYMMDD') 	 	 \n");
		sql.append("\t		   		)																 \n");			
		sql.append("\t		  WHEN '0002' THEN TO_CHAR(TO_DATE(MEM.ACRG_CDHD_JONN_DATE),'YYYYMMDD')  \n");		
		sql.append("\t		  END,   	 															 \n");
		sql.append("\t		  CASE	 																 \n");
		sql.append("\t		  WHEN TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4)>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(SYSDATE,'YYYY')-1||SUBSTR(JONN_ATON,5,4)	\n");		
		sql.append("\t		  ELSE TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4) END) SDATE	   	 \n");
		
		sql.append("\t	, NVL(CASE GRDM.CDHD_SQ1_CTGO	   	 										 \n");		
		sql.append("\t		  WHEN '0001' THEN 	 													 \n");
		sql.append("\t		  DECODE (GRD.CDHD_CTGO_SEQ_NO 											 \n");		
		sql.append("\t		   	 	     , 23, TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYYMMDD') \n");		
		sql.append("\t		   	 		 , 24, TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYYMMDD')	\n");		
		sql.append("\t		   	 		 , 26, TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYYMMDD')	\n");
		sql.append("\t		   	 		 , 28, (SELECT TO_CHAR(TO_DATE(CAMP_END_DATE,'YYYYMMDDHH24MISS'), 'YYYYMMDD')			\n");
		sql.append("\t		   	 				FROM BCDBA.TBACRGCDHDLODNTBL													\n");
		sql.append("\t		   	 				WHERE MEMO_EXPL = '0028'														\n");		
		sql.append("\t		   	 				AND PROC_RSLT_CTNT = ? ),														\n");
		sql.append("\t		   	 		 TO_CHAR(TO_DATE(SUBSTR(GRD.REG_ATON,1,8))+365,'YYYYMMDD')								\n");		
		sql.append("\t		  		  ) 	 																					\n");		
		sql.append("\t		  WHEN '0002' THEN TO_CHAR(TO_DATE(MEM.ACRG_CDHD_END_DATE),'YYYYMMDD') 	 							\n");		
		sql.append("\t		  END,  	 																						\n");
		sql.append("\t		  CASE 	 																							\n");		
		sql.append("\t		  WHEN TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4)>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4) \n");		
		sql.append("\t		  ELSE TO_CHAR(SYSDATE,'YYYY')+1||SUBSTR(JONN_ATON,5,4) END) EDATE									\n");		
		
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD																		\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO						\n");
		sql.append("\t	JOIN BCDBA.TBGCMMNCODE CODE1 ON GRDM.CDHD_SQ1_CTGO=CODE1.GOLF_CMMN_CODE AND CODE1.GOLF_CMMN_CLSS='0001'	\n");
		sql.append("\t	JOIN BCDBA.TBGCMMNCODE CODE2 ON GRDM.CDHD_SQ2_CTGO=CODE2.GOLF_CMMN_CODE AND CODE2.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD MEM ON GRD.CDHD_ID=MEM.CDHD_ID													\n");
		sql.append("\t	WHERE GRD.CDHD_ID = ?   \n");
		sql.append("\t	ORDER BY GRDM.SORT_SEQ	\n");	

		return sql.toString();
		
    }
 
}
