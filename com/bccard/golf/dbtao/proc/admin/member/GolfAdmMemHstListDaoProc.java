/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemHstListDaoProc
*   작성자    : 장성재 
*   내용      : 관리자 > 어드민관리 > 회원관리 > 회원리스트 > 등급변경내역
*   적용범위  : golf 
*   작성일자  : 2010-11-10
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
 * @author	 
 * @version	1.0
 ******************************************************************************/
public class GolfAdmMemHstListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemPayListDaoProc 프로세스 생성자  
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemHstListDaoProc() {}	

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
	
			debug("cdhd_id : " + cdhd_id );
			debug("record_size : " + data.getLong("record_size") );
			debug("hst_page_no : " + data.getLong("hst_page_no") );
			
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery();    

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("hst_page_no"));
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setString(++idx, cdhd_id);
			pstmt.setString(++idx, cdhd_id);
			pstmt.setLong(++idx, data.getLong("hst_page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;

					result.addString("REG_ATON"				,rs.getString("REG_ATON") );
					result.addString("GOLF_CMMN_CODE_NM" 	,rs.getString("GOLF_CMMN_CODE_NM") );
					result.addString("AFTER_GRD" 	,rs.getString("AFTER_GRD") );
					result.addString("ACRG_CDHD_JONN_DATE"	,rs.getString("ACRG_CDHD_JONN_DATE") );
					result.addString("ACRG_CDHD_END_DATE"	,rs.getString("ACRG_CDHD_END_DATE") );
					result.addString("JOIN_CHNL"			,rs.getString("JOIN_CHNL") );
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();           
  
        sql.append("\n SELECT * \n");
        sql.append("\n FROM  (SELECT ROWNUM RNUM, \n"); 											
        sql.append("\n               CEIL(ROWNUM/?) AS PAGE, \n"); 					
        sql.append("\n               MAX(RNUM) OVER() TOT_CNT, \n"); 								
        sql.append("\n               ((MAX(RNUM) OVER())-(?-1)*?) AS ART_NUM, \n"); 
        sql.append("\n               TO_CHAR(TO_DATE(REG_ATON,'YYYYMMDDHH24MISS'),'YYYY.MM.DD HH24:MI:SS') REG_ATON, \n"); 
        sql.append("\n               GOLF_CMMN_CODE_NM, \n");
        sql.append("\n               AFTER_GRD, \n");
        sql.append("\n               ACRG_CDHD_JONN_DATE, \n");
        sql.append("\n               ACRG_CDHD_END_DATE, \n");
        sql.append("\n               JOIN_CHNL \n");
        sql.append("\n        FROM  (SELECT ROWNUM RNUM, \n");
        sql.append("\n                      REG_ATON, \n");
        sql.append("\n                      GOLF_CMMN_CODE_NM, \n");
        sql.append("\n                      AFTER_GRD, \n");
        sql.append("\n                      ACRG_CDHD_JONN_DATE, \n");
        sql.append("\n                      ACRG_CDHD_END_DATE, \n");
        sql.append("\n                      JOIN_CHNL \n");
        sql.append("\n               FROM  (SELECT REG_ATON, \n");
        sql.append("\n                             GOLF_CMMN_CODE_NM, \n");
        sql.append("\n                             LAG(GOLF_CMMN_CODE_NM, 1) OVER (ORDER BY REG_ATON DESC) AS AFTER_GRD, \n");
        sql.append("\n                             ACRG_CDHD_JONN_DATE, \n");
        sql.append("\n                             ACRG_CDHD_END_DATE, \n");
        sql.append("\n                             JOIN_CHNL \n");
        sql.append("\n                      FROM  (SELECT a.REG_ATON, \n");
        sql.append("\n                                    c.GOLF_CMMN_CODE_NM, \n"); 
        sql.append("\n                                    NVL(a.ACRG_CDHD_JONN_DATE,' ') ACRG_CDHD_JONN_DATE, \n"); 
        sql.append("\n                                    NVL(a.ACRG_CDHD_END_DATE,' ') ACRG_CDHD_END_DATE, \n"); 
        sql.append("\n                                    NVL(d.GOLF_CMMN_CODE_NM,' ') JOIN_CHNL \n"); 
        sql.append("\n                             FROM   BCDBA.TBGCDHDGRDCHNGHST a, \n"); 
        sql.append("\n                                    BCDBA.TBGGOLFCDHDCTGOMGMT b, \n"); 
        sql.append("\n                                    BCDBA.TBGCMMNCODE c, \n"); 
        sql.append("\n                                    BCDBA.TBGCMMNCODE d \n");
        sql.append("\n                             WHERE  a.CDHD_ID = ? \n");
        sql.append("\n                             AND    a.CDHD_CTGO_SEQ_NO <> '0' \n");  
        sql.append("\n                             AND    b.CDHD_CTGO_SEQ_NO = a.CDHD_CTGO_SEQ_NO \n"); 
        sql.append("\n                             AND    c.GOLF_CMMN_CODE = b.CDHD_SQ2_CTGO \n");         
        sql.append("\n                             AND    c.GOLF_CMMN_CLSS = '0005' \n"); 
        sql.append("\n                             AND    d.GOLF_CMMN_CODE(+) = a.JOIN_CHNL \n"); 
        sql.append("\n                             AND    d.GOLF_CMMN_CLSS(+) = '0051' \n");    
        sql.append("\n                             UNION ALL \n");
        sql.append("\n                             SELECT '99991231240000' REG_ATON, \n"); 
        sql.append("\n                                    c.GOLF_CMMN_CODE_NM, \n"); 
        sql.append("\n                                    NVL(a.ACRG_CDHD_JONN_DATE,' ') ACRG_CDHD_JONN_DATE, \n"); 
        sql.append("\n                                    NVL(a.ACRG_CDHD_END_DATE,' ') ACRG_CDHD_END_DATE, \n"); 
        sql.append("\n                                    NVL(d.GOLF_CMMN_CODE_NM,' ') JOIN_CHNL \n"); 
        sql.append("\n                             FROM   BCDBA.TBGGOLFCDHD a, \n"); 
        sql.append("\n                                    BCDBA.TBGGOLFCDHDCTGOMGMT b, \n"); 
        sql.append("\n                                    BCDBA.TBGCMMNCODE c, \n"); 
        sql.append("\n                                    BCDBA.TBGCMMNCODE d \n"); 
        sql.append("\n                             WHERE  a.CDHD_ID = ? \n");
        sql.append("\n                             AND    a.CDHD_CTGO_SEQ_NO <> '0' \n");  
        sql.append("\n                             AND    b.CDHD_CTGO_SEQ_NO = a.CDHD_CTGO_SEQ_NO \n"); 
        sql.append("\n                             AND    c.GOLF_CMMN_CODE = b.CDHD_SQ2_CTGO \n");         
        sql.append("\n                             AND    c.GOLF_CMMN_CLSS = '0005' \n"); 
        sql.append("\n                             AND    d.GOLF_CMMN_CODE(+) = a.JOIN_CHNL \n"); 
        sql.append("\n                             AND    d.GOLF_CMMN_CLSS(+) = '0051' ) ) \n");
        sql.append("\n               WHERE  REG_ATON <> '99991231240000' \n");               
        sql.append("\n               ORDER BY REG_ATON DESC ) \n");
        sql.append("\n        ORDER BY RNUM ) \n");
        sql.append("\n WHERE  PAGE = ? \n");                
        
		return sql.toString();
    }
}
