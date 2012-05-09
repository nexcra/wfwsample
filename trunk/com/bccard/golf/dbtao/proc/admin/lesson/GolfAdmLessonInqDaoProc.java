/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLessonInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 상세조회
*   적용범위  : golf
*   작성일자  : 2009-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmLessonInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLessonInqDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("LSN_SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("LSN_SEQ_NO" 		,rs.getString("LESN_SEQ_NO") );
					result.addString("LSN_TYPE_CD" 		,rs.getString("LESN_CLSS") );
					result.addString("LSN_NM" 			,rs.getString("LESN_NM") );
					result.addString("EVNT_YN" 			,rs.getString("EVNT_YN") );
					result.addString("IMG_NM" 			,rs.getString("LESN_IMG") );
					result.addString("LSN_PRD_CLSS" 	,rs.getString("LESN_TRM_INP_TP_CLSS") );
					String lsn_start_dt = rs.getString("LESN_STRT_DATE");
					if (!GolfUtil.isNull(lsn_start_dt)) lsn_start_dt = DateUtil.format(lsn_start_dt, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("LSN_START_DT"		,lsn_start_dt);
					String lsn_end_dt = rs.getString("LESN_END_DATE");
					if (!GolfUtil.isNull(lsn_end_dt)) lsn_end_dt = DateUtil.format(lsn_end_dt, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("LSN_END_DT"		,lsn_end_dt);
					String aplc_end_dt = rs.getString("APLC_FIN_DATE");
					if (!GolfUtil.isNull(aplc_end_dt)) aplc_end_dt = DateUtil.format(aplc_end_dt, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("APLC_END_DT"		,aplc_end_dt);
					
					result.addString("LSN_PRD_INFO"		,rs.getString("LESN_TRM_INFO") );

					result.addLong("LSN_STTL_CST" 		,rs.getLong("LESN_NORM_COST") );
					result.addLong("LSN_DC_CST" 		,rs.getLong("LESN_DC_COST") );
					result.addString("LSN_PL"			,rs.getString("LESN_PL_INFO") );
					result.addString("MAP_NM"			,rs.getString("OLM_IMG") );
					result.addString("CHG_DDD_NO"		,rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO"		,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO"		,rs.getString("TEL_SNO") );
					result.addString("APLC_MTHD"		,rs.getString("LESN_APLC_MTHD_INFO") );
					result.addInt("APLC_LETE_NUM"		,rs.getInt("APLC_LIMT_PERS_NUM") );
					result.addString("LSN_INTD"			,rs.getString("LESN_EXPL") );

					result.addString("COOP_CP_CD"		,rs.getString("GOLF_LESN_AFFI_FIRM_CLSS") );
					result.addString("COOP_RMRK"		,rs.getString("LESN_AFFI_EXPL") );

					result.addLong("INQR_NUM" 			,rs.getLong("INQR_NUM") );
					result.addInt("CMD_NUM"				,rs.getInt("CMD_NUM") );
					result.addString("REG_MGR_ID" 		,rs.getString("REG_MGR_ID") );
					result.addString("CORR_MGR_ID" 		,rs.getString("CHNG_MGR_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON"));
					result.addString("CORR_ATON"		,rs.getString("CHNG_ATON"));

					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("LESN_CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("LESN_CTNT", bufferSt.toString());
					
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
        
		sql.append("\n SELECT");
		sql.append("\n 	LESN_SEQ_NO, LESN_CLSS, LESN_NM, EVNT_YN, LESN_IMG, LESN_TRM_INP_TP_CLSS, LESN_STRT_DATE, LESN_END_DATE, APLC_FIN_DATE, LESN_TRM_INFO, LESN_NORM_COST,  ");
		sql.append("\n 	LESN_DC_COST, LESN_PL_INFO, OLM_IMG, DDD_NO, TEL_HNO, TEL_SNO, LESN_APLC_MTHD_INFO, APLC_LIMT_PERS_NUM, LESN_EXPL,   ");
		sql.append("\n 	LESN_AFFI_EXPL, LESN_CTNT, INQR_NUM, CMD_NUM, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, ");
		sql.append("\n 	DECODE( GOLF_LESN_AFFI_FIRM_CLSS 	");
		sql.append("\n 		,'0001', '제이슨골프'	");
		sql.append("\n 		,'0002', '골프다이제스트'	");
		sql.append("\n 	) GOLF_LESN_AFFI_FIRM_CLSS	");	
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGFLESSON");
		sql.append("\n WHERE LESN_SEQ_NO = ?	");		

		return sql.toString();
    }
}
