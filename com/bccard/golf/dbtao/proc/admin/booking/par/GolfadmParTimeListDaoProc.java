/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admParTimeListDaoProc
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
public class GolfadmParTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmParTimeListDaoProc() {}	

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

			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();
			 
			//조회 ----------------------------------------------------------
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");		
			String listtype = data.getString("LISTTYPE");	
			String sql = this.getSelectQuery(sch_GR_SEQ_NO, sch_DATE, sch_DATE_ST, sch_DATE_ED, listtype, admId, admClss);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if (!sch_GR_SEQ_NO.equals("")){
				pstmt.setString(++idx, sch_GR_SEQ_NO);
			}

			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}else{
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}

			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("DAY_SEQ_NO" 			,rs.getInt("DAY_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("PAR_FREE"			,DateUtil.format(rs.getString("PAR_FREE"), "yyyyMMdd", "yy-MM-dd") );
					result.addString("REG_DATE"			,DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd") );
									
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("RESM_YN"			,rs.getString("RESM_YN") );
										
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
    private String getSelectQuery(String sch_GR_SEQ_NO, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, String listtype, String admId, String admClss){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM, DECODE(RESM_YN, 'Y','휴장', '공휴일') RESM_YN	");
		sql.append("\n 		, DAY_SEQ_NO, GR_NM, PAR_FREE, REG_DATE  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM, T1.RESM_YN RESM_YN	");
		sql.append("\n 			, T1.RSVT_ABLE_SCD_SEQ_NO AS DAY_SEQ_NO, T2.GREEN_NM AS GR_NM, T1.PAR_3_BOKG_RESM_DATE AS PAR_FREE, T1.REG_ATON AS REG_DATE	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGRSVTABLESCDMGMT T1  	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T2 ON T1.AFFI_GREEN_SEQ_NO=T2.AFFI_GREEN_SEQ_NO 	");
		sql.append("\n 			WHERE T1.PAR_3_BOKG_RESM_DATE IS NOT NULL AND T1.RESM_YN IN ('Y', 'H')	");
		
		if(!sch_GR_SEQ_NO.equals("")){
			sql.append("\n 				AND T2.AFFI_GREEN_SEQ_NO = ?	");
		}
		if(sch_DATE.equals("rounding")){
			
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T1.PAR_3_BOKG_RESM_DATE >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T1.PAR_3_BOKG_RESM_DATE <= ?	");
			}
			
			sql.append("\n 			AND T1.RESM_YN = 'Y'	");
			
		}else if(sch_DATE.equals("holiday")){
			
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T1.PAR_3_BOKG_RESM_DATE >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T1.PAR_3_BOKG_RESM_DATE <= ?	");
			}
			
			sql.append("\n 			AND T1.RESM_YN = 'H'	");
			
		}else{
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T1.REG_ATON >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T1.REG_ATON <= ?	");
			}
		}		

		if(!admClss.equals("master")){		sql.append("\n		AND T2.GREEN_ID='"+admId+"'  	");			}
		
		sql.append("\n 				ORDER BY T1.PAR_3_BOKG_RESM_DATE DESC, T1.REG_ATON 	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}	

		return sql.toString();
    }
}
