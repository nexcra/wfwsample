/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

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
public class GolfadmPreRsViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreRsViewDaoProc() {}	
 
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
			boolean eos = false;
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
			rs = pstmt.executeQuery();	
			

			if(rs != null) {
				while(rs.next())  {
					if(!eos) result.addString("RESULT", "00"); //정상결과
					
					String str_appr_opion = rs.getString("APPR_OPION");
					
					result.addString("RSVT_YN" 		,rs.getString("RSVT_YN") );
					result.addString("ID" 			,rs.getString("ID") );
					result.addString("HP_NO" 		,rs.getString("HP_NO") );
					result.addString("PHONE" 		,rs.getString("PHONE") );
					result.addString("EMAIL" 		,rs.getString("EMAIL") );
					result.addString("REG_DATE" 	,rs.getString("REG_DATE") );
					result.addString("CNCL_DATE" 	,rs.getString("CNCL_DATE") );
					result.addString("GR_NM" 		,rs.getString("GR_NM") );
					result.addString("COURSE" 		,rs.getString("COURSE") );
					result.addString("BK_TIME" 		,rs.getString("BK_TIME") );
					result.addString("BK_DATE" 		,rs.getString("BK_DATE") );
					result.addString("NAME" 		,rs.getString("NAME") );
					result.addString("GRADE_NM" 	,rs.getString("GRADE_NM") );
					result.addString("TOTAL_CNT"   	,Integer.toString(rs.getInt("TOTAL_CNT")) );
					//result.addString("CDHD_CTGO_SEQ_NO"   ,rs.getString("CDHD_CTGO_SEQ_NO") );
					//result.addString("PMI_BOKG_APO_YN"   ,rs.getString("PMI_BOKG_APO_YN") );
					result.addString("APPR_OPION"   ,str_appr_opion);
				
					result.addString("ESTM_ITM_CLSS" ,rs.getString("ESTM_ITM_CLSS") );
					result.addString("RSVT_ABLE_BOKG_TIME_SEQ_NO" 		,rs.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					
					
					if(!"".equals(str_appr_opion) && str_appr_opion != null){
						result.addString("APPR_OPION_SIZE",""+str_appr_opion.length());
						
					}else{
						result.addString("APPR_OPION_SIZE","0");
					}
					
										
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CTNT", bufferSt.toString());
					

					eos = true;
				}
			}

			
			if(!eos) result.addString("RESULT", "01");
			

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	public DbTaoResult execute(WaContext context, TaoDataSet data, String cdhd_id) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			
			//해당아이디의 멤버 GRADE를 구한다. 
			
			
			
			//해당 아이디의 멤버 유료회원 시작일시, 마감일시, 이달의 시작일시, 마지막일시
			
			
			
			//해당 아이디의 멤버 총 사용가능한 횟수, 사용한 횟수, 사용가능한 횟수
			
			String mem_Grade = "";
			String apo_yn = "N";
			
			
			conn = context.getDbConnection("default", null);
			String sql = this.getGradeSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id);
			
			rs = pstmt.executeQuery();
			boolean eof = false;
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				if(!"".equals(mem_Grade)){
					mem_Grade = mem_Grade + " / "+rs.getString("GOLF_CMMN_CODE_NM");
				}else{
					mem_Grade = rs.getString("GOLF_CMMN_CODE_NM");
				}
								
				if(!"Y".equals(apo_yn)){
					apo_yn = rs.getString("PMI_BOKG_APO_YN");
				}
				
				
				eof = true;
			}
			
			if(!eof) {
				result.addString("RESULT", "01");
			}else{
				result.addString("GOLF_CMMN_CODE_NM", mem_Grade);
				result.addString("PMI_BOKG_APO_YN", apo_yn);
			}
			
			
		}catch (Throwable t) {
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
        
		sql.append("\n SELECT 	\n");
		
		sql.append("\t  T1.RSVT_YN ,T1.CDHD_ID AS ID	\n");
		sql.append("\t  , (T1.HP_DDD_NO||'-'||T1.HP_TEL_HNO||'-'||T1.HP_TEL_SNO) AS HP_NO	\n");
		sql.append("\t  , T6.EMAIL , T6.PHONE	\n");
		sql.append("\t  , (TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON, 0, 8)), 'YYYY-MM-DD')||'('||	\n");
		sql.append("\t  TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON, 0, 8)),'DY')||') '||	\n");
		sql.append("\t  SUBSTR(T1.REG_ATON, 9, 2)||':'||SUBSTR(T1.REG_ATON, 11, 2)) AS REG_DATE	\n");

		sql.append("\n 	, CASE T1.RSVT_YN WHEN 'Y' THEN '' WHEN 'D' THEN ''  	");
		sql.append("\n 	ELSE TO_CHAR(TO_DATE(SUBSTR(T1.CNCL_ATON, 0, 8)), 'YYYY-MM-DD (DY) ')  	");
		sql.append("\n 	||SUBSTR(T1.CNCL_ATON, 9, 2)||':'||SUBSTR(T1.CNCL_ATON, 11, 2) END AS CNCL_DATE  	");		
		
		sql.append("\t  , T3.GREEN_NM AS GR_NM, T4.GOLF_RSVT_CURS_NM AS COURSE, T1.CTNT AS CTNT	\n");
		sql.append("\t  , (SUBSTR(T2.BOKG_ABLE_TIME,0,2)||':'||SUBSTR(T2.BOKG_ABLE_TIME,3,4)) AS BK_TIME	\n");
		sql.append("\t  , (TO_CHAR(TO_DATE(T4.BOKG_ABLE_DATE), 'YYYY-MM-DD')||'('||TO_CHAR(TO_DATE(T4.BOKG_ABLE_DATE), 'DY')||')') AS BK_DATE	\n");
		sql.append("\t  , T2.RSVT_ABLE_BOKG_TIME_SEQ_NO			\n");
		sql.append("\t  , T6.HG_NM as NAME			\n");
		sql.append("\t  , T6.ESTM_ITM_CLSS as ESTM_ITM_CLSS			\n");
		sql.append("\t  , T6.APPR_OPION as APPR_OPION, T8.GOLF_CMMN_CODE_NM AS GRADE_NM	\n");
		//sql.append("\t  , T7.CDHD_CTGO_SEQ_NO AS CDHD_CTGO_SEQ_NO			\n");
		//sql.append("\t  , (SELECT PMI_BOKG_APO_YN FROM BCDBA.TBGGOLFCDHDBNFTMGMT WHERE CDHD_SQ2_CTGO = T8.CDHD_SQ2_CTGO)AS PMI_BOKG_APO_YN			\n");
		sql.append("\t  , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE CDHD_ID=T1.CDHD_ID AND SUBSTR(GOLF_SVC_RSVT_NO,5,1)='M')AS TOTAL_CNT  \n");
		//sql.append("\t  , (SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CODE = T8.CDHD_SQ2_CTGO AND GOLF_CMMN_CLSS= '0005')AS GRADE_NM \n");
		
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t  JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGAFFIGREEN T3 ON T1.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGRSVTABLESCDMGMT T4 ON T2.RSVT_ABLE_SCD_SEQ_NO=T4.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD T6 ON T6.CDHD_ID=T1.CDHD_ID	\n");
		//sql.append("\t  JOIN BCDBA.TBGGOLFCDHDGRDMGMT T7 ON T7.CDHD_ID = T1.CDHD_ID	\n");
		//sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T8 ON T7.CDHD_CTGO_SEQ_NO = T8.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T7 ON T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T8 ON T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005'	\n");
		
		sql.append("\t  WHERE GOLF_SVC_RSVT_NO=?		\n");	
		
		return sql.toString();
    }
    
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getGradeSelectQuery(){
         StringBuffer sql = new StringBuffer();
         
 		sql.append("	SELECT 	T3.GOLF_CMMN_CODE_NM ,T4.PMI_BOKG_APO_YN    							\n");
 		sql.append("	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1 					  							\n");
 		sql.append("	 JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO = T2.CDHD_CTGO_SEQ_NO \n");
 		sql.append("	 JOIN BCDBA.TBGCMMNCODE T3 ON T3.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO  			\n");
 		sql.append("	 JOIN BCDBA.TBGGOLFCDHDBNFTMGMT T4 ON T4.CDHD_SQ2_CTGO = T2.CDHD_SQ2_CTGO  		\n");
 		sql.append("	 WHERE T1.CDHD_ID = ?  AND T3.GOLF_CMMN_CLSS='0005'  							\n");
 		
 		
 		
     return sql.toString();
     }
}
