/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLsnRecvInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨신청 상세조회
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmLsnRecvInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLsnRecvInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnRecvInqDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("RECV_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
				
					result.addString("LSN_TYPE_CD" 		,rs.getString("LESN_CLSS") );
					result.addString("LSN_NM" 			,rs.getString("LESN_NM") );
					result.addString("COOP_CP_CD" 		,rs.getString("GOLF_LESN_AFFI_FIRM_CLSS") );
					
					result.addLong("RECV_NO" 			,rs.getLong("APLC_SEQ_NO") );
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") );
					result.addString("SEX" 				,rs.getString("SEX_CLSS") );
					result.addString("EMAIL_ID" 		,rs.getString("EMAIL") );
					result.addString("TEL" 				,rs.getString("TEL") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("MTTR"				,GolfUtil.getUrl(rs.getString("MEMO_EXPL")) );
					
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );
					result.addString("USERCLSS" 		,rs.getString("USERCLSS") );
					result.addString("LSN_TYPE_NM" 		,rs.getString("LSN_TYPE_NM") );
					result.addString("COOP_CP_NM" 		,rs.getString("COOP_CP_NM") );
					result.addString("LSN_EXPC_NM" 		,rs.getString("LSN_EXPC_NM") );
					
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
        
		sql.append("\n SELECT TGL.LESN_CLSS, TGL.LESN_NM, TGL.GOLF_LESN_AFFI_FIRM_CLSS, TGR.APLC_SEQ_NO	");
		sql.append("\n , DECODE(TGR.SEX_CLSS, 'M', '남', 'F', '여') SEX_CLSS, TGR.CDHD_ID	");
		sql.append("\n , TGR.EMAIL, TGR.DDD_NO||'-'||TGR.TEL_HNO||'-'||TGR.TEL_SNO TEL	");
		sql.append("\n , TO_CHAR(TO_DATE(TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON	");
		sql.append("\n , TGR.MEMO_EXPL, TGU.HG_NM, TGS.ODR_NO, TGS.CDHD_ID, TGS.STTL_AMT	");
		sql.append("\n , TMC4.GOLF_CMMN_CODE_NM USERCLSS ,TMC1.GOLF_CMMN_CODE_NM LSN_TYPE_NM	");
		sql.append("\n , TMC2.GOLF_CMMN_CODE_NM COOP_CP_NM ,TMC3.GOLF_CMMN_CODE_NM LSN_EXPC_NM	");
		sql.append("\n , NVL(TO_CHAR(TO_DATE(TGL.LESN_STRT_DATE, 'YYYYMMDDHH24MISS')-3, 'YYYY-MM-DD'),'-') POSS_CNCL_ATON	");
		sql.append("\n , CASE TGS.STTL_STAT_CLSS WHEN 'N' THEN '결제완료' WHEN 'Y' THEN '<FONT COLOR=RED>결제취소</FONT>' END STTL_STAT_CLSS	");
		sql.append("\n , TO_CHAR(TO_DATE(TGS.STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') STTL_ATON	");
		sql.append("\n , TO_CHAR(TO_DATE(TGS.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') CNCL_ATON	");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT TGR	");
		sql.append("\n JOIN BCDBA.TBGLESNMGMT TGL ON TGR.LESN_SEQ_NO=TGL.LESN_SEQ_NO	");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHD TGU ON TGR.CDHD_ID=TGU.CDHD_ID	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE TMC1 ON TGL.LESN_CLSS=TMC1.GOLF_CMMN_CODE AND TMC1.GOLF_CMMN_CLSS='0003'	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE TMC2 ON TGL.GOLF_LESN_AFFI_FIRM_CLSS=TMC2.GOLF_CMMN_CODE AND TMC2.GOLF_CMMN_CLSS='0004'	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE TMC3 ON TGR.GOLF_LESN_EXPE_CLSS=TMC3.GOLF_CMMN_CODE AND TMC3.GOLF_CMMN_CLSS='0006'	");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHDGRDMGMT TGUC ON TGU.CDHD_ID=TGUC.CDHD_ID	");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHDCTGOMGMT TGUD ON TGUD.CDHD_CTGO_SEQ_NO=TGUC.CDHD_CTGO_SEQ_NO	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE TMC4 ON TGUD.CDHD_SQ2_CTGO=TMC4.GOLF_CMMN_CODE AND TMC4.GOLF_CMMN_CLSS='0005'	");
		sql.append("\n LEFT JOIN BCDBA.TBGSTTLMGMT TGS ON TGR.APLC_SEQ_NO=TGS.STTL_GDS_SEQ_NO	");
		sql.append("\n WHERE TGR.GOLF_SVC_APLC_CLSS = '0001' AND TGR.APLC_SEQ_NO = ?	");
		
		return sql.toString();
    }
}
