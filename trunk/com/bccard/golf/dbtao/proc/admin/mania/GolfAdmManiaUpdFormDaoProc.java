/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaChgFormDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 수정폼
*   적용범위  : golf
*   작성일자  : 2009-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.mania;

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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmManiaUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaChgFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmManiaUpdFormDaoProc() {}	

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
					
					result.addInt("RECV_NO" 			,rs.getInt("APLC_SEQ_NO") );
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") ); 				// 신청자 아이디
					result.addString("USERCLSS" 		,rs.getString("GOLF_CMMN_01") );				// 등급
					result.addString("USERCLSS2" 		,rs.getString("GOLF_CMMN_02") );				// 등급
					result.addString("USERCLSSCODE" 	,rs.getString("GOLF_CMMN_CODE_01") );				// 등급
					result.addString("USERCLSSCODE2" 	,rs.getString("GOLF_CMMN_CODE_02") );				// 등급
					result.addString("CHG_DDD_NO" 		,rs.getString("DDD_NO") ); 					// 전화 국번호
					result.addString("CHG_TEL_HNO" 		,rs.getString("TEL_HNO") ); 				// 전화 국번호
					result.addString("CHG_TEL_SNO" 		,rs.getString("TEL_SNO") ); 				// 전화 일련번호
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") ); 				// 휴대전화 국번호
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") ); 				// 휴대전화 국번호
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") ); 				// 휴대전화 일련번호
					result.addString("HG_NM" 			,rs.getString("HG_NM") ); 					//
					result.addString("EMAIL_ID" 		,rs.getString("EMAIL") ); 	
					result.addString("CAR_KND_NM" 		,rs.getString("CAR_KND_NM") );				// 차종
					result.addString("NORM_PRIC" 		,rs.getString("NORM_PRIC") );				// 차량별 금액
					result.addString("PCT20_DC_PRIC" 	,rs.getString("PCT20_DC_PRIC") );			// 차량별 금액 20%할인
					result.addString("PCT30_DC_PRIC" 	,rs.getString("PCT30_DC_PRIC") );			// 차량별 금액 30%할인
					result.addString("DPRT_PL_INFO" 	,rs.getString("DPRT_PL_INFO") ); 			// 출발장소
					result.addString("GREEN_NM" 		,rs.getString("GREEN_NM") ); 				// 골프장명
					result.addString("RIDG_PERS_NUM" 	,rs.getString("RIDG_PERS_NUM") ); 			// 승차인원
					result.addString("DELI_TYPE" 		,rs.getString("GOLF_MGZ_DLV_PL_CLSS") ); 	// 배송지타입
					
					String zp =  rs.getString("ZP");								// 우편번호
					if (!GolfUtil.isNull(zp))
					{
						result.addString("ZP1" 		,rs.getString("ZP").substring(0,3) );
						result.addString("ZP2" 		,rs.getString("ZP").substring(3,6) );
					}
					result.addString("ZIPADDR" 			,rs.getString("ADDR") ); 					// 주소
					result.addString("DETAILADDR" 		,rs.getString("DTL_ADDR") ); 				// 상세주소
					result.addString("CAR_TYPE_CD" 		,rs.getString("GOLF_LMS_CAR_KND_CLSS") );	// 차종코드
					
					String pu_date = rs.getString("PU_DATE");
					if (!GolfUtil.isNull(pu_date)) pu_date = DateUtil.format(pu_date, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("PU_DATE"		,pu_date);
					
					String pu_time = rs.getString("PU_TIME");
					result.addString("PU_TIME" 		,rs.getString("PU_TIME") );
					if (!GolfUtil.isNull(pu_time))
					{

						result.addString("PU_TIME_SI" 		,rs.getString("PU_TIME").substring(0,2) );
						result.addString("PU_TIME_MI" 		,rs.getString("PU_TIME").substring(2,4) );
	
					}
					
					String tee_date = rs.getString("TEOF_DATE");
					if (!GolfUtil.isNull(tee_date)) tee_date = DateUtil.format(tee_date, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("TEE_DATE"		,tee_date);
					
					
					String tee_time = rs.getString("TEOF_TIME");
					result.addString("TEE_TIME" 		,rs.getString("TEOF_TIME") );
					if (!GolfUtil.isNull(tee_time))
					{

						result.addString("TEE_TIME_SI" 		,rs.getString("TEOF_TIME").substring(0,2) );
						result.addString("TEE_TIME_MI" 		,rs.getString("TEOF_TIME").substring(2,4) );
	
					}
					result.addString("COUNS_YN" 		,rs.getString("CSLT_YN") );					// 상담여부

					result.addString("RESULT", "00"); 												//정상결과
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
	
	public int getIntMemGrade(WaContext context, TaoDataSet data , String userId) throws BaseException {
		int intMemGrade = 0;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getSelectIntMemGradeQuery();
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				intMemGrade = rs.getInt("CDHD_SQ2_CTGO");
			}
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		
		return intMemGrade;
	}
	/** ***********************************************************************
	    * Query를 생성하여 리턴한다.    
	    ************************************************************************ */
	    private String getSelectIntMemGradeQuery(){
	        StringBuffer sql = new StringBuffer();
	        
	        sql.append("\n SELECT TO_NUMBER(T2.CDHD_SQ2_CTGO)AS CDHD_SQ2_CTGO							");
	        sql.append("\n FROM BCDBA.TBGGOLFCDHDGRDMGMT T1 				");
	        sql.append("\n JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO = T2.CDHD_CTGO_SEQ_NO  ");
	        sql.append("\n 	WHERE T1.CDHD_ID = ?  							");
	        
	        return sql.toString();
	    }
 
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT	TBG1.APLC_SEQ_NO, TBG2.HG_NM, TBG2.CDHD_ID	");
        sql.append("\n 			, TBG1.HP_DDD_NO, TBG1.HP_TEL_HNO, TBG1.HP_TEL_SNO	");
        sql.append("\n 			, TBG1.ZP, TBG1.ADDR, TBG1.DTL_ADDR 	");
        sql.append("\n 			, TBG1.DDD_NO, TBG1.TEL_HNO, TBG1.TEL_SNO , TBG1.EMAIL 	");
        sql.append("\n 			, TBG1.DPRT_PL_INFO, TBG1.GREEN_NM, TBG1.GOLF_LMS_CAR_KND_CLSS, TBG1.RIDG_PERS_NUM  	");
        sql.append("\n 			, TBG6.GOLF_CMMN_CODE_NM as GOLF_CMMN_01, TBG7.GOLF_CMMN_CODE_NM as GOLF_CMMN_02	");
        sql.append("\n 			, TBG6.GOLF_CMMN_CODE as GOLF_CMMN_CODE_01, TBG7.GOLF_CMMN_CODE as GOLF_CMMN_CODE_02	");
        sql.append("\n 			, to_char(to_date(TBG1.REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON	");
        sql.append("\n 			, TBG1.CSLT_YN, TBG1.PGRS_YN	");
        sql.append("\n 			, TBG1.PU_DATE, TBG1.PU_TIME, TBG1.TEOF_DATE, TBG1.TEOF_TIME, TBG4.CAR_KND_NM	");
        sql.append("\n 			, TBG4.NORM_PRIC, TBG4.PCT20_DC_PRIC, TBG4.PCT30_DC_PRIC	");
        sql.append("\n 			, TBG1.STTL_AMT, TBG1.GOLF_MGZ_DLV_PL_CLSS	");
        sql.append("\n from BCDBA.TBGAPLCMGMT TBG1	");
        sql.append("\n join BCDBA.TBGGOLFCDHD TBG2 on TBG1.CDHD_ID = TBG2.CDHD_ID	");
        sql.append("\n join BCDBA.TBGGOLFCDHDGRDMGMT TBG3 on TBG2.CDHD_ID = TBG3.CDHD_ID	");
        sql.append("\n join BCDBA.TBGGOLFCDHDCTGOMGMT TBG5 on TBG3.CDHD_CTGO_SEQ_NO = TBG5.CDHD_CTGO_SEQ_NO	");
        sql.append("\n join BCDBA.TBGCMMNCODE TBG6 on TBG5.CDHD_SQ1_CTGO = TBG6.GOLF_CMMN_CODE and TBG6.GOLF_CMMN_CLSS='0001'	");
        sql.append("\n join BCDBA.TBGCMMNCODE TBG7 on TBG5.CDHD_SQ2_CTGO = TBG7.GOLF_CMMN_CODE and TBG7.GOLF_CMMN_CLSS='0005'	");
        sql.append("\n left join BCDBA.TBGAMTMGMT TBG4 on  TBG1.GOLF_LMS_CAR_KND_CLSS = TBG4.CAR_KND_CLSS	");
        sql.append("\n where TBG1.APLC_SEQ_NO =	?	");        
        

		return sql.toString();
    }
}
