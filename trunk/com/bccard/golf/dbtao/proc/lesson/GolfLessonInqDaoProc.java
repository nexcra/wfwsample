/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLessonInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨프로그램 상세조회
*   적용범위  : golf
*   작성일자  : 2009-06-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
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
public class GolfLessonInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfLessonInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfLessonInqDaoProc() {}	

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
					result.addString("LSN_START_DT" 	,rs.getString("LESN_STRT_DATE") );
					result.addString("LSN_END_DT" 		,rs.getString("LESN_END_DATE") );
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
					result.addLong("LESN_DC_RT" 		,rs.getLong("LESN_DC_RT") );		
					
					result.addString("CLOSE_YN"		,rs.getString("CLOSE_YN"));
					result.addString("ING_YN"		,rs.getString("ING_YN"));
					result.addString("COOP_CP_CD_NM"		,rs.getString("GOLF_LESN_AFFI_FIRM_CLSS_NM") );					

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
					result.addString("LSN_CTNT", bufferSt.toString());
					
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
	

	/**
	 * 조회수 업데이트 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int readCntUpd(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			sql = this.getUpdateQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("LSN_SEQ_NO"));
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			

		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/*************************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	LESN_SEQ_NO, LESN_CLSS, LESN_NM, EVNT_YN, LESN_IMG, LESN_TRM_INP_TP_CLSS,  	");
		sql.append("\n 	TO_CHAR(TO_DATE(LESN_STRT_DATE, 'YYYYMMDD'), 'YYYY.MM.DD') LESN_STRT_DATE, 	");
		sql.append("\n 	TO_CHAR(TO_DATE(LESN_END_DATE, 'YYYYMMDD'), 'YYYY.MM.DD') LESN_END_DATE, 	");
		sql.append("\n 	APLC_FIN_DATE, LESN_TRM_INFO, LESN_NORM_COST, 	");
		sql.append("\n 	LESN_DC_COST, LESN_PL_INFO, OLM_IMG, DDD_NO, TEL_HNO, TEL_SNO, LESN_APLC_MTHD_INFO, APLC_LIMT_PERS_NUM, GOLF_LESN_AFFI_FIRM_CLSS, LESN_EXPL,   ");
		sql.append("\n 	LESN_AFFI_EXPL, LESN_CTNT, INQR_NUM, CMD_NUM, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, LESN_DC_RT,");
		sql.append("\n 	CASE WHEN TO_CHAR(SYSDATE, 'YYYYMMDD') > NVL(APLC_FIN_DATE,'21001231') THEN 'N' ELSE 'Y' END ING_YN ,	");
		sql.append("\n 	CASE WHEN (SELECT COUNT(APLC_SEQ_NO) FROM BCDBA.TBGAPLCMGMT WHERE LESN_SEQ_NO=TGL.LESN_SEQ_NO) >= APLC_LIMT_PERS_NUM THEN 'Y' ELSE 'N' END CLOSE_YN ,	");
		sql.append("\n 	(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0004' AND GOLF_CMMN_CODE=TGL.GOLF_LESN_AFFI_FIRM_CLSS) GOLF_LESN_AFFI_FIRM_CLSS_NM	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGLESNMGMT TGL");
		sql.append("\n WHERE LESN_SEQ_NO = ?	");		

		return sql.toString();
    }
    

    /*************************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGLESNMGMT SET	\n");
		sql.append("\t  INQR_NUM=INQR_NUM+1	\n");
		sql.append("\t WHERE LESN_SEQ_NO=?	\n");
        return sql.toString();
    }
}
