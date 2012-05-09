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

package com.bccard.golf.dbtao.proc.admin.system.pay;

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
public class GolfAdmPayListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmPayListDaoProc 프로세스 생성자  
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmPayListDaoProc() {}	

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
			// 회원통합테이블 관련 수정사항 진행
			conn = context.getDbConnection("default", null);	
			String sch_PAY	= data.getString("SCH_PAY");		
			String sch_SERVICE	= data.getString("SCH_SERVICE");
			String sch_NAME	= data.getString("SCH_NAME");		
			String sch_DATE_ST	= data.getString("SCH_DATE_ST");		
			String sch_DATE_ED	= data.getString("SCH_DATE_ED");
			String listtype		= data.getString("LISTTYPE");	
			
			sch_DATE_ST = GolfUtil.rplc(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.rplc(sch_DATE_ED, "-", "");
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(sch_PAY, sch_SERVICE, sch_DATE_ST, sch_DATE_ED, sch_NAME, listtype);    

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			if(GolfUtil.empty(listtype))	pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
										
					result.addString("PAY_NM" 			,rs.getString("PAY_NM") );
					result.addString("STTL_AMT" 		,GolfUtil.comma(rs.getString("STTL_AMT")) );
					result.addString("REG_DATE" 		,rs.getString("REG_DATE") );
					result.addString("SERVICE_NM"		,rs.getString("SERVICE_NM") );
					result.addString("NAME"				,rs.getString("NAME") );
					
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
    private String getSelectQuery(String sch_PAY, String sch_SERVICE, String sch_DATE_ST, String sch_DATE_ED, String sch_NAME, String listtype){
        StringBuffer sql = new StringBuffer();        
        
		sql.append("\n SELECT	*														\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM											\n");
		sql.append("\t 			, CEIL(ROWNUM/?) AS PAGE								\n");
		sql.append("\t 			, MAX(RNUM) OVER() TOT_CNT								\n");
		sql.append("\t 			, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			\n");
		
		sql.append("\t 			, PAY_NM, STTL_AMT, REG_DATE, SERVICE_NM, NAME		\n");
		
		sql.append("\t 			FROM (SELECT ROWNUM RNUM										\n");
		
		
		sql.append("\t  , T2.GOLF_CMMN_CODE_NM AS PAY_NM, T1.STTL_AMT	\n");
		sql.append("\t 	, TO_CHAR(TO_DATE(SUBSTR(T1.STTL_ATON,1,8)),'YYYY-MM-DD(DY) ')	\n");
		sql.append("\t 	||SUBSTR(T1.STTL_ATON,9,2)||':'||SUBSTR(T1.STTL_ATON,11,2)||':'||SUBSTR(T1.STTL_ATON,13,2) AS REG_DATE	\n");
		sql.append("\t 	, T3.GOLF_CMMN_CODE_NM AS SERVICE_NM, T4.HG_NM AS NAME	\n");
		sql.append("\t 	FROM BCDBA.TBGSTTLMGMT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T2 ON T1.STTL_MTHD_CLSS=T2.GOLF_CMMN_CODE AND T2.GOLF_CMMN_CLSS='0015'	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T1.STTL_GDS_CLSS=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0016'	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGGOLFCDHD T4 ON T1.CDHD_ID=T4.CDHD_ID	\n");
		sql.append("\t 	WHERE T1.ODR_NO IS NOT NULL	\n");
		

		if(!GolfUtil.empty(sch_PAY))		sql.append("\t 	AND T1.STTL_MTHD_CLSS='"+sch_PAY+"'	\n");
		if(!GolfUtil.empty(sch_SERVICE))	sql.append("\t 	AND T1.STTL_GDS_CLSS='"+sch_SERVICE+"'	\n");
		if(!GolfUtil.empty(sch_NAME))		sql.append("\t 	AND T4.HG_NM LIKE '%"+sch_NAME+"%'	\n");
		if(!GolfUtil.empty(sch_DATE_ST))	sql.append("\t 	AND T1.STTL_ATON>='"+sch_DATE_ST+"000000'	\n");
		if(!GolfUtil.empty(sch_DATE_ED))	sql.append("\t 	AND T1.STTL_ATON<='"+sch_DATE_ED+"999999'	\n");

		
		sql.append("\t 	ORDER BY STTL_ATON DESC	\n");
				
		sql.append("\t 			)		\n");
		sql.append("\t 	ORDER BY RNUM	\n");
		sql.append("\t 	)				\n");
		if(GolfUtil.empty(listtype))	sql.append("\t WHERE PAGE = ?	\n");		

		return sql.toString();
    }
}
