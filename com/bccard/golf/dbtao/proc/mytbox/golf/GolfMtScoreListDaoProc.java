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

package com.bccard.golf.dbtao.proc.mytbox.golf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfMtScoreListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrListDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfMtScoreListDaoProc() {}	

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

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
            String userId	= "";
            
            if(usrEntity != null) 
        	{
        		userId		= (String)usrEntity.getAccount(); 
        	}
            
			String sch_DATE_ST	= data.getString("SCH_DATE_ST");
			String sch_DATE_ED	= data.getString("SCH_DATE_ED");
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(sch_DATE_ST, sch_DATE_ED);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, userId);	// 아이디
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("SEQ_NO" 					,rs.getInt("SEQ_NO") );
					result.addString("ROUND_DATE" 			,rs.getString("ROUND_DATE_VIEW") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("CURS_NM" 				,rs.getString("CURS_NM") );
					result.addString("HIT_CNT" 				,rs.getString("HIT_CNT") );
					result.addString("EG_NUM" 				,rs.getString("EG_NUM") );
					result.addString("BID_NUM" 				,rs.getString("BID_NUM") );
					result.addString("PAR_NUM" 				,rs.getString("PAR_NUM") );
					result.addString("BOG_NUM" 				,rs.getString("BOG_NUM") );
					result.addString("DOB_BOG_NUM" 			,rs.getString("DOB_BOG_NUM") );
					result.addString("TRP_BOG_NUM" 			,rs.getString("TRP_BOG_NUM") );
					result.addString("ETC_BOG_NUM" 			,rs.getString("ETC_BOG_NUM") );					

					result.addInt("ART_NUM" 				,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"				,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"			,rs.getString("PAGE") );
					result.addString("RNUM"					,rs.getString("RNUM") );
															
					art_num_no++;
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
    private String getSelectQuery(String sch_DATE_ST, String sch_DATE_ED){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*																		\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM,														\n");
		sql.append("\t 			SEQ_NO, TO_CHAR(TO_DATE(ROUND_DATE),'YYYY.MM.DD') AS ROUND_DATE_VIEW, GREEN_NM, CURS_NM, HIT_CNT, EG_NUM, BID_NUM 		\n");
		sql.append("\t 			, PAR_NUM, BOG_NUM, DOB_BOG_NUM, TRP_BOG_NUM, ETC_BOG_NUM, 				\n");
		sql.append("\t 			CEIL(ROWNUM/?) AS PAGE,													\n");
		sql.append("\t 			MAX(RNUM) OVER() TOT_CNT,												\n");
		sql.append("\t 			((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  								\n");	
		sql.append("\t 			FROM (SELECT ROWNUM RNUM,												\n");
		sql.append("\t 				SEQ_NO, ROUND_DATE, GREEN_NM, CURS_NM, HIT_CNT, EG_NUM, BID_NUM 	\n");
		sql.append("\t 				, PAR_NUM, BOG_NUM, DOB_BOG_NUM, TRP_BOG_NUM, ETC_BOG_NUM 			\n");
		sql.append("\t 				FROM 																\n");
		sql.append("\t 				BCDBA.TBGSCORINFO 													\n");
		sql.append("\t 				WHERE CDHD_ID=?														\n");	// 아이디
		if(!sch_DATE_ST.equals("")){
		sql.append("\t 				AND ROUND_DATE >= '"+sch_DATE_ST+"'									\n");
    	}
		if(!sch_DATE_ED.equals("")){
		sql.append("\t 				AND ROUND_DATE <= '"+sch_DATE_ED+"'									\n");
    	}
		sql.append("\t 				ORDER BY ROUND_DATE DESC											\n");		
		sql.append("\t 			)																		\n");
		sql.append("\t 	ORDER BY RNUM																	\n");
		sql.append("\t 	)																				\n");
		sql.append("\t WHERE PAGE = ?																	\n");		

		return sql.toString();
    }
}
