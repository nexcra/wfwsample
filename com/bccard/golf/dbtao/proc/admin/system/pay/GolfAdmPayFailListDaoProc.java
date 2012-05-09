/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmPayFailListDaoProc
*   작성자	: (주)미디어포스 
*   내용		: 관리자 > 모집인/TM관리 > 결제실패내역 > 리스트 처리
*   적용범위	: golf
*   작성일자	: 2009-11-27
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
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfAdmPayFailListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmPayListDaoProc 프로세스 생성자  
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmPayFailListDaoProc() {}	

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
			String sch_TYPE	= data.getString("SCH_TYPE");		
			String sch_TEXT	= data.getString("SCH_TEXT");
			String sch_DATE_ST	= data.getString("SCH_DATE_ST");		
			String sch_DATE_ED	= data.getString("SCH_DATE_ED");
			
			sch_DATE_ST = GolfUtil.rplc(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.rplc(sch_DATE_ED, "-", "");
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(sch_TYPE, sch_TEXT, sch_DATE_ST, sch_DATE_ED);    

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			 
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					
					// 휴대폰번호
					String hp_no1 = rs.getString("HP_DDD_NO");
					String hp_no2 = rs.getString("HP_TEL_HNO");
					String hp_no3 = rs.getString("HP_TEL_SNO");
					String hp_no = "";
					if(!GolfUtil.empty(hp_no1) && !GolfUtil.empty(hp_no2) && !GolfUtil.empty(hp_no2)){
						hp_no = hp_no1 + "-" + hp_no2 + "-" + hp_no3;
					}
					
					// 전화번호
					String tel_no1 = rs.getString("DDD_NO");
					String tel_no2 = rs.getString("TEL_HNO");
					String tel_no3 = rs.getString("TEL_SNO");
					String tel_no = "";
					if(!GolfUtil.empty(tel_no1) && !GolfUtil.empty(tel_no2) && !GolfUtil.empty(tel_no3)){
						tel_no = tel_no1 + "-" + tel_no2 + "-" + tel_no3;
					}
					
					// 승인/취소 상태
					String sttl_stat_clss = rs.getString("STTL_STAT_CLSS");
					if(!GolfUtil.empty(sttl_stat_clss)){
						if(sttl_stat_clss.equals("Y")){
							sttl_stat_clss = "취소";
						}
						else if(sttl_stat_clss.equals("N")){
							sttl_stat_clss = "승인";
						}
					}
										
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
										
					result.addString("PAY_NM" 			,rs.getString("PAY_NM") );
					result.addString("SERVICE_NM"		,rs.getString("SERVICE_NM") );
					result.addString("REG_DATE" 		,rs.getString("REG_DATE") );
					result.addString("ID"				,rs.getString("CDHD_ID") );
					result.addString("NM"				,rs.getString("NM") );
					result.addString("HP_NO"			,hp_no ); 
					result.addString("TEL_NO"			,tel_no );
					result.addString("STTL_STAT_CLSS"	,sttl_stat_clss );
					result.addString("CARD_NO"			,rs.getString("CARD_NO") );
					result.addString("STTL_AMT" 		,GolfUtil.comma(rs.getString("STTL_AMT")) );
					result.addString("RSPN_CODE"		,rs.getString("RSPN_CODE") );
					result.addString("RSPN_MSG_CTNT"	,rs.getString("RSPN_MSG_CTNT") );
					
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
    private String getSelectQuery(String sch_TYPE, String sch_TEXT, String sch_DATE_ST, String sch_DATE_ED){
        StringBuffer sql = new StringBuffer();        
        
		sql.append("\n SELECT	*														\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM											\n");
		sql.append("\t 			, CEIL(ROWNUM/?) AS PAGE								\n");
		sql.append("\t 			, MAX(RNUM) OVER() TOT_CNT								\n");
		sql.append("\t 			, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			\n");
		
		sql.append("\t 			, PAY_NM, SERVICE_NM, REG_DATE, CDHD_ID, NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO		\n");
		sql.append("\t 			, DDD_NO, TEL_HNO, TEL_SNO, STTL_STAT_CLSS, CARD_NO, STTL_AMT, RSPN_CODE, RSPN_MSG_CTNT		\n");
		
		sql.append("\t 			FROM (SELECT ROWNUM RNUM										\n");
				
		sql.append("\t  , T2.GOLF_CMMN_CODE_NM AS PAY_NM	\n");
		sql.append("\t 	, TO_CHAR(TO_DATE(SUBSTR(T1.STTL_ATON,1,8)),'YYYY-MM-DD(DY) ')	\n");
		sql.append("\t 	||SUBSTR(T1.STTL_ATON,9,2)||':'||SUBSTR(T1.STTL_ATON,11,2)||':'||SUBSTR(T1.STTL_ATON,13,2) AS REG_DATE	\n");
		sql.append("\t 	, T3.GOLF_CMMN_CODE_NM AS SERVICE_NM, T1.CDHD_ID, T1.NM, T1.HP_DDD_NO, T1.HP_TEL_HNO, T1.HP_TEL_SNO	\n");
		sql.append("\t 	, T1.DDD_NO, T1.TEL_HNO, T1.TEL_SNO, T1.STTL_STAT_CLSS, T1.CARD_NO, T1.STTL_AMT, T1.RSPN_CODE, T1.RSPN_MSG_CTNT 	\n");
		sql.append("\t 	FROM BCDBA.TBGSTTLFAILCTNT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T2 ON T1.STTL_MTHD_CLSS=T2.GOLF_CMMN_CODE AND T2.GOLF_CMMN_CLSS='0015'	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T1.STTL_GDS_CLSS=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0016'	\n");
		sql.append("\t 	WHERE T1.CDHD_ID IS NOT NULL	\n");
		
		if(!GolfUtil.empty(sch_TYPE) && !GolfUtil.empty(sch_TEXT)){
			if(sch_TYPE.equals("PHONE")){
				sql.append("\t 	AND(HP_DDD_NO = '"+sch_TEXT+"' OR HP_TEL_HNO = '"+sch_TEXT+"' OR HP_TEL_SNO = '"+sch_TEXT+"'	\n");		
				sql.append("\t 	OR DDD_NO = '"+sch_TEXT+"' OR TEL_HNO = '"+sch_TEXT+"' OR TEL_SNO = '"+sch_TEXT+"')	\n");		
			}
			else{
				sql.append("\t 	AND " + sch_TYPE + " LIKE '%"+sch_TEXT+"%'	\n");				
			}
		}
		if(!GolfUtil.empty(sch_DATE_ST))	sql.append("\t 	AND T1.STTL_ATON>='"+sch_DATE_ST+"000000'	\n");
		if(!GolfUtil.empty(sch_DATE_ED))	sql.append("\t 	AND T1.STTL_ATON<='"+sch_DATE_ED+"999999'	\n");
		
		sql.append("\t 	ORDER BY STTL_ATON DESC	\n");
				
		sql.append("\t 			)		\n");
		sql.append("\t 	ORDER BY RNUM	\n");
		sql.append("\t 	)				\n");
		sql.append("\t WHERE PAGE = ?	\n");		

		return sql.toString();
    }
}
