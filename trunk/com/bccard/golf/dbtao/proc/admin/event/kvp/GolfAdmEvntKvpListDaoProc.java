/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntKvpListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > Kvp이벤트 회원 > 리스트
*   적용범위  : golf
*   작성일자  : 2010-05-31
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.kvp;

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
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmEvntKvpListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntKvpListDaoProc() {}	

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
			
			// 검색값

			String sch_yn					= data.getString("sch_yn");
			String sch_date_gubun			= data.getString("sch_date_gubun");
			String sch_date_st				= data.getString("sch_date_st");
			String sch_date_ed				= data.getString("sch_date_ed");	
			String sch_type					= data.getString("sch_type");
			String sch_text					= data.getString("sch_text");
			String sch_pgrs_yn				= data.getString("sch_pgrs_yn");				// 진행여부
			String sch_cslt_yn				= data.getString("sch_cslt_yn");				// 가입여부
			String sch_rsvt_cdhd_grd_seq_no	= data.getString("sch_rsvt_cdhd_grd_seq_no");	// 회원등급
			String sch_golf_lesn_rsvt_no	= data.getString("sch_golf_lesn_rsvt_no");		// 결제횟수
						
			String sql = this.getSelectQuery(data);

			// 입력값 (INPUT)  
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, 20); 
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_date_st))				pstmt.setString(++idx, sch_date_st+"000000");
				if(!GolfUtil.empty(sch_date_ed))				pstmt.setString(++idx, sch_date_ed+"999999");
				if(!GolfUtil.empty(sch_pgrs_yn))				pstmt.setString(++idx, sch_pgrs_yn);
				if(!GolfUtil.empty(sch_cslt_yn))				pstmt.setString(++idx, sch_cslt_yn);
				if(!GolfUtil.empty(sch_rsvt_cdhd_grd_seq_no))	pstmt.setString(++idx, sch_rsvt_cdhd_grd_seq_no);
				if(!GolfUtil.empty(sch_golf_lesn_rsvt_no))		pstmt.setString(++idx, sch_golf_lesn_rsvt_no);
				if(!GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
					}else{
						pstmt.setString(++idx, "%"+sch_text+"%");
					}
				}
			}
			
			pstmt.setLong(++idx, data.getLong("page_no"));			
			
			rs = pstmt.executeQuery();			
			int art_num_no = 0;
			
			if(rs != null) {			 

				while(rs.next())  {	
				        
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					
					result.addString("APLC_SEQ_NO" 			,rs.getString("APLC_SEQ_NO") );
					result.addString("HG_NM" 				,rs.getString("HG_NM") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("STR_JUMIN_NO" 		,rs.getString("STR_JUMIN_NO") );
					
					result.addString("MOBILE" 				,rs.getString("MOBILE") );
					result.addString("TXT_PGRS" 			,rs.getString("TXT_PGRS") );
					result.addString("PGRS_YN" 				,rs.getString("PGRS_YN") );
					result.addString("GOLF_LESN_RSVT_NO" 	,rs.getString("GOLF_LESN_RSVT_NO") );
					result.addString("STR_REG_ATON" 		,rs.getString("STR_REG_ATON") );
					result.addString("STR_CHNG_ATON" 		,rs.getString("STR_CHNG_ATON") );
					
					result.addString("STR_PU_DATE" 			,rs.getString("STR_PU_DATE") );
					result.addString("GRD_NM"				,rs.getString("GRD_NM") );
					result.addString("GREEN_NM"				,rs.getString("GREEN_NM") );
					result.addString("CDHD_ID"				,rs.getString("CDHD_ID") );
					result.addString("CMMCODE"				,rs.getString("CMMCODE") );
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
    * 구매 리스트
	 * @throws TaoException 
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet data) throws TaoException{
        StringBuffer sql = new StringBuffer();
		
		String sch_yn					= data.getString("sch_yn");
		String sch_date_gubun			= data.getString("sch_date_gubun");
		String sch_date_st				= data.getString("sch_date_st");
		String sch_date_ed				= data.getString("sch_date_ed");	
		String sch_type					= data.getString("sch_type");
		String sch_text					= data.getString("sch_text");
		String sch_pgrs_yn				= data.getString("sch_pgrs_yn");				// 진행여부
		String sch_cslt_yn				= data.getString("sch_cslt_yn");				// 가입여부
		String sch_rsvt_cdhd_grd_seq_no	= data.getString("sch_rsvt_cdhd_grd_seq_no");	// 회원등급
		String sch_golf_lesn_rsvt_no	= data.getString("sch_golf_lesn_rsvt_no");		// 결제횟수
        
		sql.append("\n	SELECT *	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
		sql.append("\t	        , APLC_SEQ_NO, HG_NM, STR_JUMIN_NO, JUMIN_NO, MOBILE, TXT_PGRS, GOLF_LESN_RSVT_NO, STR_REG_ATON, STR_CHNG_ATON, STR_PU_DATE	\n");
		sql.append("\t	        , CDHD_ID, GREEN_NM, GRD_NM, PGRS_YN, CMMCODE	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT ROWNUM RNUM	\n");
		
		sql.append("\t	        , T_APL.APLC_SEQ_NO, T_GC.HG_NM, SUBSTR(T_GC.JUMIN_NO,1,6) STR_JUMIN_NO, T_GC.JUMIN_NO	\n");
		sql.append("\t	       	, T_GC.MOBILE, T_APL.GOLF_LESN_RSVT_NO, T_APL.PGRS_YN	\n");
		sql.append("\t	       	, DECODE (T_CODE.GOLF_CMMN_CODE , '0022' , DECODE( T_APL.PGRS_YN , 'Y', '회원', 'N' , '등급삭제'),	\n");
		sql.append("\t	       	 										   DECODE( T_APL.PGRS_YN , 'Y', '회원', 'N' , '탈회'))TXT_PGRS	\n");
		sql.append("\t	       	, TO_CHAR(TO_DATE(SUBSTR(T_APL.REG_ATON,1,8)),'YYYY-MM-DD') STR_REG_ATON	\n");
		sql.append("\t	       	, TO_CHAR(TO_DATE(SUBSTR(T_APL.CHNG_ATON,1,8)),'YYYY-MM-DD') STR_CHNG_ATON	\n");
		sql.append("\t	       	, TO_CHAR(TO_DATE(T_APL.PU_DATE),'YYYY-MM-DD') STR_PU_DATE	\n");
		sql.append("\t	       	, T_APL.CDHD_ID, T_APL.GREEN_NM, T_CODE.EXPL GRD_NM, T_CODE.GOLF_CMMN_CODE CMMCODE	\n");
		sql.append("\t	       	FROM BCDBA.TBGAPLCMGMT T_APL	\n");
		sql.append("\t	       	JOIN BCDBA.TBGGOLFCDHD T_GC ON T_APL.CDHD_ID = T_GC.CDHD_ID	\n");
		sql.append("\t	       	JOIN BCDBA.TBGCMMNCODE T_CODE ON T_APL.RSVT_CDHD_GRD_SEQ_NO=TO_NUMBER(T_CODE.GOLF_CMMN_CODE) AND GOLF_CMMN_CLSS='0056'	\n");
		sql.append("\t	       	WHERE T_APL.GOLF_SVC_APLC_CLSS IN ('1001', '1004')	\n");	

		if(!GolfUtil.empty(sch_yn)){
			if(!GolfUtil.empty(sch_date_st))				sql.append("\t	            AND T_APL."+sch_date_gubun+">=?	\n");
			if(!GolfUtil.empty(sch_date_ed))				sql.append("\t	            AND T_APL."+sch_date_gubun+"<=?	\n");
			if(!GolfUtil.empty(sch_pgrs_yn))				sql.append("\t	            AND T_APL.PGRS_YN=?	\n");
			if(!GolfUtil.empty(sch_cslt_yn))				sql.append("\t	            AND T_APL.CSLT_YN=?	\n");
			if(!GolfUtil.empty(sch_rsvt_cdhd_grd_seq_no))	sql.append("\t	            AND T_APL.RSVT_CDHD_GRD_SEQ_NO=?	\n");
			if(!GolfUtil.empty(sch_golf_lesn_rsvt_no))		sql.append("\t	            AND T_APL.GOLF_LESN_RSVT_NO=?	\n");
			if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
				if("MOBILE".equals(sch_type)){
					sql.append("\t	            AND (T_APL.HP_DDD_NO LIKE ? OR T_APL.HP_TEL_HNO LIKE ? OR T_APL.HP_TEL_SNO LIKE ?)	\n");
				}else{
					sql.append("\t	            AND T_GC."+sch_type+" LIKE ?	\n");
				}
			}
		}
		
		sql.append("\t	        ORDER BY T_APL.REG_ATON DESC	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    ORDER BY RNUM	\n");
		sql.append("\t	)	\n");
		sql.append("\t	WHERE PAGE=?	\n");
		
		return sql.toString();
    }

}

