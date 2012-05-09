/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLsnRecvUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 수정 폼
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
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmLsnRecvUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLsnRecvUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnRecvUpdFormDaoProc() {}	

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
					result.addString("CHG_DDD_NO" 		,rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO" 		,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO" 		,rs.getString("TEL_SNO") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("MTTR"				,rs.getString("MEMO_EXPL") );
										
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
        
		sql.append("\n SELECT	");
		sql.append("\n TGL.LESN_CLSS, TGL.LESN_NM, TGL.GOLF_LESN_AFFI_FIRM_CLSS,	");
		sql.append("\n TGR.APLC_SEQ_NO, TGR.CDHD_ID, TGR.SEX_CLSS, TGR.EMAIL, TGR.DDD_NO, TGR.TEL_HNO, TGR.TEL_SNO, 	");
		sql.append("\n TO_CHAR(TO_DATE(TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, TGR.MEMO_EXPL,	");
		sql.append("\n TGU.HG_NM,	");
		sql.append("\n DECODE (T.USERCLSS,'()', '등급없음',T.USERCLSS) USERCLSS,	");
		sql.append("\n TMC1.GOLF_CMMN_CODE_NM LSN_TYPE_NM,	");
		sql.append("\n TMC2.GOLF_CMMN_CODE_NM COOP_CP_NM,	");
		sql.append("\n TMC3.GOLF_CMMN_CODE_NM LSN_EXPC_NM	");
		sql.append("\n FROM	");
		sql.append("\n (	");
		sql.append("\n 		SELECT CDHD_ID, SUBSTR(MAX(SYS_CONNECT_BY_PATH((GOLF_CMMN_CODE_NM1||'('||GOLF_CMMN_CODE_NM2||')'),',')),2) AS USERCLSS 	");
		sql.append("\n 		FROM  	");
		sql.append("\n 		( 	");
		sql.append("\n 			SELECT  	");
		sql.append("\n 				CDHD_ID, GOLF_CMMN_CODE_NM1, GOLF_CMMN_CODE_NM2, 	");
		sql.append("\n 				ROW_NUMBER() OVER(PARTITION BY CDHD_ID ORDER BY CDHD_ID) CNT 	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			( 	");
		sql.append("\n 				SELECT  	");
		sql.append("\n 					TGU.CDHD_ID,TMC1.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM1, TMC2.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM2 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGGOLFCDHD TGU, BCDBA.TBGGOLFCDHDGRDMGMT TGUC, BCDBA.TBGGOLFCDHDCTGOMGMT TGUD,  	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0001') TMC1, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0005') TMC2 	");
		sql.append("\n 				WHERE TGU.CDHD_ID=TGUC.CDHD_ID(+) 	");
		sql.append("\n 				AND TGUC.CDHD_CTGO_SEQ_NO=TGUD.CDHD_CTGO_SEQ_NO(+) 	");
		sql.append("\n 				AND TGUD.CDHD_SQ1_CTGO=TMC1.GOLF_CMMN_CODE(+) 	");
		sql.append("\n 				AND TGUD.CDHD_SQ2_CTGO=TMC2.GOLF_CMMN_CODE(+) 	");		
		sql.append("\n 				) 	");
		sql.append("\n 			) 	");
		sql.append("\n 		START WITH CNT=1 	");
		sql.append("\n 		CONNECT BY PRIOR CNT = CNT-1 	");
		sql.append("\n 		GROUP BY CDHD_ID 	");
		sql.append("\n )T,	");
		sql.append("\n BCDBA.TBGGOLFCDHD TGU, BCDBA.TBGAPLCMGMT TGR, BCDBA.TBGLESNMGMT TGL,	");
		sql.append("\n (SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0003') TMC1,	");
		sql.append("\n (SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0004') TMC2,	");
		sql.append("\n (SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0006') TMC3	");
		sql.append("\n WHERE TGU.CDHD_ID = T.CDHD_ID	");
		sql.append("\n AND TGR.CDHD_ID=TGU.CDHD_ID	");
		sql.append("\n AND TGR.LESN_SEQ_NO=TGL.LESN_SEQ_NO	");
		sql.append("\n AND TGL.LESN_CLSS=TMC1.GOLF_CMMN_CODE(+)	");
		sql.append("\n AND TGL.GOLF_LESN_AFFI_FIRM_CLSS=TMC2.GOLF_CMMN_CODE(+)	");
		sql.append("\n AND TGR.GOLF_LESN_EXPE_CLSS=TMC3.GOLF_CMMN_CODE(+)	");
		sql.append("\n AND TGR.GOLF_SVC_APLC_CLSS = '0001'	");
		sql.append("\n AND TGR.APLC_SEQ_NO = ?	");
		sql.append("\n ORDER BY TGR.APLC_SEQ_NO DESC	");
		
		return sql.toString();
    }
}
