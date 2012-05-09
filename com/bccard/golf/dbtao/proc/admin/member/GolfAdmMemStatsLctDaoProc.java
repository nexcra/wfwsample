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

package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0  
 ******************************************************************************/
public class GolfAdmMemStatsLctDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 > 어드민관리 > 회원관리 > 통계 > 지역별";
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemStatsLctDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult executeLctGrd(WaContext context,  HttpServletRequest request) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getLctGrdQuery();
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			int grd1 = 0;
			int grd2 = 0;
			int grd3 = 0;
			int grd4 = 0;
			int grd5 = 0;
			int grd6 = 0;
			int grd7 = 0;
			int grd8 = 0;
			int grd9 = 0;
			int sumRow = 0;
			int sumGrd1 = 0;
			int sumGrd2 = 0;
			int sumGrd3 = 0;
			int sumGrd4 = 0;
			int sumGrd5 = 0;
			int sumGrd6 = 0;
			int sumGrd7 = 0;
			int sumGrd8 = 0;
			int sumGrd9 = 0;
			int sumGrd10 = 0;

			if(rs != null) {			 

				while(rs.next())  {
					grd1 = rs.getInt("GRD1");
					grd2 = rs.getInt("GRD2");
					grd3 = rs.getInt("GRD3");
					grd4 = rs.getInt("GRD4");
					grd5 = rs.getInt("GRD5");
					grd6 = rs.getInt("GRD6");
					grd7 = rs.getInt("GRD7");
					grd8 = rs.getInt("GRD8");
					grd9 = rs.getInt("GRD9");
					sumRow = grd1+grd2+grd3+grd4+grd5+grd6+grd7+grd8+grd9;

					result.addString("GRD",rs.getString("GRD"));
					result.addString("GRD1",GolfUtil.comma(grd1+""));
					result.addString("GRD2",GolfUtil.comma(grd2+""));
					result.addString("GRD3",GolfUtil.comma(grd3+""));
					result.addString("GRD4",GolfUtil.comma(grd4+""));
					result.addString("GRD5",GolfUtil.comma(grd5+""));
					result.addString("GRD6",GolfUtil.comma(grd6+""));
					result.addString("GRD7",GolfUtil.comma(grd7+""));
					result.addString("GRD8",GolfUtil.comma(grd8+""));
					result.addString("GRD9",GolfUtil.comma(grd9+""));
					result.addString("GRD10",GolfUtil.comma(sumRow+""));
					
					sumGrd1 += grd1;
					sumGrd2 += grd2;
					sumGrd3 += grd3;
					sumGrd4 += grd4;
					sumGrd5 += grd5;
					sumGrd6 += grd6;
					sumGrd7 += grd7;
					sumGrd8 += grd8;
					sumGrd9 += grd9;
					sumGrd10 += sumRow;				
				}

				result.addString("GRD","합계");
				result.addString("GRD1",GolfUtil.comma(sumGrd1+""));
				result.addString("GRD2",GolfUtil.comma(sumGrd2+""));
				result.addString("GRD3",GolfUtil.comma(sumGrd3+""));
				result.addString("GRD4",GolfUtil.comma(sumGrd4+""));
				result.addString("GRD5",GolfUtil.comma(sumGrd5+""));
				result.addString("GRD6",GolfUtil.comma(sumGrd6+""));
				result.addString("GRD7",GolfUtil.comma(sumGrd7+""));
				result.addString("GRD8",GolfUtil.comma(sumGrd8+""));
				result.addString("GRD9",GolfUtil.comma(sumGrd9+""));
				result.addString("GRD10",GolfUtil.comma(sumGrd10+""));
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");
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


	public DbTaoResult executeLctAge(WaContext context,  HttpServletRequest request) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getLctAgeQuery();
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			int grd1 = 0;
			int grd2 = 0;
			int grd3 = 0;
			int grd4 = 0;
			int sumRow = 0;
			int sumGrd1 = 0;
			int sumGrd2 = 0;
			int sumGrd3 = 0;
			int sumGrd4 = 0;
			int sumGrd5 = 0;

			if(rs != null) {			 

				while(rs.next())  {
					grd1 = rs.getInt("GRD1");
					grd2 = rs.getInt("GRD2");
					grd3 = rs.getInt("GRD3");
					grd4 = rs.getInt("GRD4");
					sumRow = grd1+grd2+grd3+grd4;

					result.addString("GRD",rs.getString("GRD"));
					result.addString("GRD1",GolfUtil.comma(grd1+""));
					result.addString("GRD2",GolfUtil.comma(grd2+""));
					result.addString("GRD3",GolfUtil.comma(grd3+""));
					result.addString("GRD4",GolfUtil.comma(grd4+""));
					result.addString("GRD5",GolfUtil.comma(sumRow+""));
					
					sumGrd1 += grd1;
					sumGrd2 += grd2;
					sumGrd3 += grd3;
					sumGrd4 += grd4;
					sumGrd5 += sumRow;				
				}

				result.addString("GRD","합계");
				result.addString("GRD1",GolfUtil.comma(sumGrd1+""));
				result.addString("GRD2",GolfUtil.comma(sumGrd2+""));
				result.addString("GRD3",GolfUtil.comma(sumGrd3+""));
				result.addString("GRD4",GolfUtil.comma(sumGrd4+""));
				result.addString("GRD5",GolfUtil.comma(sumGrd5+""));
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");
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
	
	public DbTaoResult executeLctSex(WaContext context,  HttpServletRequest request) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getLctSexQuery();
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			int grd1 = 0;
			int grd2 = 0;
			int sumRow = 0;
			int sumGrd1 = 0;
			int sumGrd2 = 0;
			int sumGrd3 = 0;

			if(rs != null) {			 

				while(rs.next())  {
					grd1 = rs.getInt("GRD1");
					grd2 = rs.getInt("GRD2");
					sumRow = grd1+grd2;

					result.addString("GRD",rs.getString("GRD"));
					result.addString("GRD1",GolfUtil.comma(grd1+""));
					result.addString("GRD2",GolfUtil.comma(grd2+""));
					result.addString("GRD3",GolfUtil.comma(sumRow+""));
					
					sumGrd1 += grd1;
					sumGrd2 += grd2;
					sumGrd3 += sumRow;				
				}

				result.addString("GRD","합계");
				result.addString("GRD1",GolfUtil.comma(sumGrd1+""));
				result.addString("GRD2",GolfUtil.comma(sumGrd2+""));
				result.addString("GRD3",GolfUtil.comma(sumGrd3+""));
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");
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
    * 지역-등급     
    ************************************************************************ */
    private String getLctGrdQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\t	WITH MEM AS (	\n");
		sql.append("\t	    SELECT GZIP, GRD, COUNT(*) CNT	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT CODE.GOLF_CMMN_CODE_NM AS GRD	\n");
		sql.append("\t	        , CASE	\n");
		sql.append("\t	            WHEN 100<=ZIP AND ZIP<=199 THEN '서울/경기'	\n");
		sql.append("\t	            WHEN 400<=ZIP AND ZIP<=499 THEN '서울/경기'	\n");
		sql.append("\t	            WHEN 360<=ZIP AND ZIP<=399 THEN '충북'	\n");
		sql.append("\t	            WHEN 300<=ZIP AND ZIP<=359 THEN '충남'	\n");
		sql.append("\t	            WHEN 500<=ZIP AND ZIP<=559 THEN '전라/제주'	\n");
		sql.append("\t	            WHEN 690<=ZIP AND ZIP<=699 THEN '전라/제주'	\n");
		sql.append("\t	            WHEN 600<=ZIP AND ZIP<=689 THEN '경상'	\n");
		sql.append("\t	            WHEN 700<=ZIP AND ZIP<=799 THEN '경상'	\n");
		sql.append("\t	            WHEN 200<=ZIP AND ZIP<=299 THEN '강원'	\n");
		sql.append("\t	            ELSE '기타' END GZIP	\n");
		sql.append("\t	        FROM(	\n");
		sql.append("\t	            SELECT CDHD_CTGO_SEQ_NO, TO_NUMBER(NVL(SUBSTR(REPLACE(REPLACE(ZIP_CODE,' ',''),'-',''),1,3),0)) ZIP	\n");
		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	            WHERE NVL(SECE_YN,'Y')='Y'	\n");
		sql.append("\t	        ) CDHD	\n");
		sql.append("\t	        JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	        JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    GROUP BY GZIP, GRD	\n");
		sql.append("\t	), T_GRD AS (SELECT DISTINCT(GOLF_CMMN_CODE_NM) GRD_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0005')	\n");
		sql.append("\t	SELECT GZIP AS GRD	\n");
		sql.append("\t	    , MIN(DECODE(RN,1,CNT)) GRD1	\n");
		sql.append("\t	    , MIN(DECODE(RN,2,CNT)) GRD2	\n");
		sql.append("\t	    , MIN(DECODE(RN,3,CNT)) GRD3	\n");
		sql.append("\t	    , MIN(DECODE(RN,4,CNT)) GRD4	\n");
		sql.append("\t	    , MIN(DECODE(RN,5,CNT)) GRD5	\n");
		sql.append("\t	    , MIN(DECODE(RN,6,CNT)) GRD6	\n");
		sql.append("\t	    , MIN(DECODE(RN,7,CNT)) GRD7	\n");
		sql.append("\t	    , MIN(DECODE(RN,8,CNT)) GRD8	\n");
		sql.append("\t	    , MIN(DECODE(RN,9,CNT)) GRD9	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT GZIP, CNT, ROW_NUMBER() OVER(PARTITION BY GZIP ORDER BY	\n");
		sql.append("\t	    DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NH티타늄', 6	\n");
		sql.append("\t	    , 'NH플래티늄', 7, '골프투어멤버스', 8, '나의 알파 플래티늄', 9, '나의 알파 플래티늄', 10)) RN	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT '서울/경기' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='서울/경기')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM UNION ALL	\n");
		sql.append("\t	   		SELECT '충북' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='충북')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM UNION ALL	\n");
		sql.append("\t	        SELECT '충남' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='충남')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM UNION ALL	\n");
		sql.append("\t	        SELECT '전라/제주' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='전라/제주')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM UNION ALL	\n");
		sql.append("\t	        SELECT '경상' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='경상')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM UNION ALL	\n");
		sql.append("\t	        SELECT '강원' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='강원')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM UNION ALL	\n");
		sql.append("\t	        SELECT '기타' GZIP, GRD_NM GRD, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='기타')T_MEM ON T_MEM.GRD=T_GRD.GRD_NM	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY GZIP	\n");
		sql.append("\t	ORDER BY DECODE(GZIP, '서울/경기', 1, '충북', 2, '충남', 3, '전라/제주', 4, '경상', 5, '강원', 6, '기타', 7)	\n");

		return sql.toString();
    }
    
	/** ***********************************************************************
	* 지역-연령    
	************************************************************************ */
	private String getLctAgeQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\t	WITH MEM AS (	\n");
		sql.append("\t	    SELECT GZIP, GAGE, COUNT(*) CNT	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT CASE	\n");
		sql.append("\t	            WHEN 100<=ZIP AND ZIP<=199 THEN '서울/경기'	\n");
		sql.append("\t	            WHEN 400<=ZIP AND ZIP<=499 THEN '서울/경기'	\n");
		sql.append("\t	            WHEN 360<=ZIP AND ZIP<=399 THEN '충북'	\n");
		sql.append("\t	            WHEN 300<=ZIP AND ZIP<=359 THEN '충남'	\n");
		sql.append("\t	            WHEN 500<=ZIP AND ZIP<=559 THEN '전라/제주'	\n");
		sql.append("\t	            WHEN 690<=ZIP AND ZIP<=699 THEN '전라/제주'	\n");
		sql.append("\t	            WHEN 600<=ZIP AND ZIP<=689 THEN '경상'	\n");
		sql.append("\t	            WHEN 700<=ZIP AND ZIP<=799 THEN '경상'	\n");
		sql.append("\t	            WHEN 200<=ZIP AND ZIP<=299 THEN '강원'	\n");
		sql.append("\t	            ELSE '기타' END GZIP	\n");
		sql.append("\t	        , CASE	\n");
		sql.append("\t	            WHEN AGE<=39 THEN 30	\n");
		sql.append("\t	            WHEN 40<=AGE AND AGE<=49 THEN 40	\n");
		sql.append("\t	            WHEN 50<=AGE AND AGE<=59 THEN 50	\n");
		sql.append("\t	            WHEN AGE>=60 THEN 60	\n");
		sql.append("\t	            ELSE 0 END GAGE	\n");
		sql.append("\t	        FROM(	\n");
		sql.append("\t	            SELECT TO_NUMBER(NVL(SUBSTR(REPLACE(REPLACE(ZIP_CODE,' ',''),'-',''),1,3),0)) ZIP	\n");
		sql.append("\t	            , CASE SUBSTR(JUMIN_NO, 7, 1)	\n");
		sql.append("\t	                WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-2000+1	\n");
		sql.append("\t	                WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-2000+1	\n");
		sql.append("\t	                WHEN '9' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-1800+1	\n");
		sql.append("\t	                WHEN '0' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-1800+1	\n");
		sql.append("\t	                ELSE TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-1900+1 END AGE	\n");
		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	            WHERE NVL(SECE_YN,'Y')='Y'	\n");
		sql.append("\t	        )	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    GROUP BY GZIP, GAGE	\n");
		sql.append("\t	), T_GRD AS (	\n");
		sql.append("\t	    SELECT DISTINCT(GAGE) AGE	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT CASE WHEN AGE<=39 THEN 30 WHEN 40<=AGE AND AGE<=49 THEN 40	\n");
		sql.append("\t	            WHEN 50<=AGE AND AGE<=59 THEN 50 WHEN AGE>=60 THEN 60 ELSE 0 END GAGE	\n");
		sql.append("\t	        FROM(	\n");
		sql.append("\t	            SELECT CASE SUBSTR(JUMIN_NO, 7, 1)	\n");
		sql.append("\t	                WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-2000+1	\n");
		sql.append("\t	                WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-2000+1	\n");
		sql.append("\t	                WHEN '9' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-1800+1	\n");
		sql.append("\t	                WHEN '0' THEN TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-1800+1	\n");
		sql.append("\t	                ELSE TO_CHAR(SYSDATE,'YYYY')-SUBSTR(JUMIN_NO, 1, 2)-1900+1 END AGE	\n");
		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	        )	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	)	\n");
		sql.append("\t	SELECT GZIP AS GRD	\n");
		sql.append("\t	    , MIN(DECODE(RN,1,CNT)) GRD1	\n");
		sql.append("\t	    , MIN(DECODE(RN,2,CNT)) GRD2	\n");
		sql.append("\t	    , MIN(DECODE(RN,3,CNT)) GRD3	\n");
		sql.append("\t	    , MIN(DECODE(RN,4,CNT)) GRD4	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT GZIP, CNT, ROW_NUMBER() OVER(PARTITION BY GZIP ORDER BY GAGE) RN	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT '서울/경기' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='서울/경기')T_MEM ON T_MEM.GAGE=T_GRD.AGE UNION ALL	\n");
		sql.append("\t	        SELECT '충북' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='충북')T_MEM ON T_MEM.GAGE=T_GRD.AGE UNION ALL	\n");
		sql.append("\t	        SELECT '충남' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='충남')T_MEM ON T_MEM.GAGE=T_GRD.AGE UNION ALL	\n");
		sql.append("\t	        SELECT '전라/제주' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='전라/제주')T_MEM ON T_MEM.GAGE=T_GRD.AGE UNION ALL	\n");
		sql.append("\t	        SELECT '경상' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='경상')T_MEM ON T_MEM.GAGE=T_GRD.AGE UNION ALL	\n");
		sql.append("\t	        SELECT '강원' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='강원')T_MEM ON T_MEM.GAGE=T_GRD.AGE UNION ALL	\n");
		sql.append("\t	        SELECT '기타' GZIP, GAGE, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='기타')T_MEM ON T_MEM.GAGE=T_GRD.AGE	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY GZIP	\n");
		sql.append("\t	ORDER BY DECODE(GZIP, '서울/경기', 1, '충북', 2, '충남', 3, '전라/제주', 4, '경상', 5, '강원', 6, '기타', 7)	\n");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* 지역-성별
	************************************************************************ */
	private String getLctSexQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\t	WITH MEM AS (	\n");
		sql.append("\t	    SELECT GZIP, SEX, COUNT(*) CNT	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT SEX, CASE	\n");
		sql.append("\t	            WHEN 100<=ZIP AND ZIP<=199 THEN '서울/경기'	\n");
		sql.append("\t	            WHEN 400<=ZIP AND ZIP<=499 THEN '서울/경기'	\n");
		sql.append("\t	            WHEN 360<=ZIP AND ZIP<=399 THEN '충북'	\n");
		sql.append("\t	            WHEN 300<=ZIP AND ZIP<=359 THEN '충남'	\n");
		sql.append("\t	            WHEN 500<=ZIP AND ZIP<=559 THEN '전라/제주'	\n");
		sql.append("\t	            WHEN 690<=ZIP AND ZIP<=699 THEN '전라/제주'	\n");
		sql.append("\t	            WHEN 600<=ZIP AND ZIP<=689 THEN '경상'	\n");
		sql.append("\t	            WHEN 700<=ZIP AND ZIP<=799 THEN '경상'	\n");
		sql.append("\t	            WHEN 200<=ZIP AND ZIP<=299 THEN '강원'	\n");
		sql.append("\t	            ELSE '기타' END GZIP	\n");
		sql.append("\t	        FROM(	\n");
		sql.append("\t	            SELECT TO_NUMBER(NVL(SUBSTR(REPLACE(REPLACE(ZIP_CODE,' ',''),'-',''),1,3),0)) ZIP	\n");
		sql.append("\t	            , CASE MOD(SUBSTR(JUMIN_NO, 7, 1),2)	\n");
		sql.append("\t	                WHEN 1 THEN '남'	\n");
		sql.append("\t	                ELSE '여' END SEX	\n");
		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	            WHERE NVL(SECE_YN,'Y')='Y'	\n");
		sql.append("\t	        )	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    GROUP BY GZIP, SEX	\n");
		sql.append("\t	), T_GRD AS (	\n");
		sql.append("\t	    SELECT DISTINCT(CASE	\n");
		sql.append("\t	        WHEN 100<=ZIP AND ZIP<=199 THEN '서울/경기'	\n");
		sql.append("\t	        WHEN 400<=ZIP AND ZIP<=499 THEN '서울/경기'	\n");
		sql.append("\t	        WHEN 360<=ZIP AND ZIP<=399 THEN '충북'	\n");
		sql.append("\t	        WHEN 300<=ZIP AND ZIP<=359 THEN '충남'	\n");
		sql.append("\t	        WHEN 500<=ZIP AND ZIP<=559 THEN '전라/제주'	\n");
		sql.append("\t	        WHEN 690<=ZIP AND ZIP<=699 THEN '전라/제주'	\n");
		sql.append("\t	        WHEN 600<=ZIP AND ZIP<=689 THEN '경상'	\n");
		sql.append("\t	        WHEN 700<=ZIP AND ZIP<=799 THEN '경상'	\n");
		sql.append("\t	        WHEN 200<=ZIP AND ZIP<=299 THEN '강원'	\n");
		sql.append("\t	        ELSE '기타' END) ZIP	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	       SELECT TO_NUMBER(NVL(SUBSTR(REPLACE(REPLACE(ZIP_CODE,' ',''),'-',''),1,3),0)) ZIP	\n");
		sql.append("\t	       FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	)	\n");
		sql.append("\t	SELECT GZIP AS GRD	\n");
		sql.append("\t	    , MIN(DECODE(RN,1,CNT)) GRD1	\n");
		sql.append("\t	    , MIN(DECODE(RN,2,CNT)) GRD2	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT GZIP, CNT, ROW_NUMBER() OVER(PARTITION BY GZIP ORDER BY SEX) RN	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT '서울/경기' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='서울/경기')T_MEM ON T_MEM.GZIP=T_GRD.ZIP UNION ALL	\n");
		sql.append("\t	        SELECT '충북' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='충북')T_MEM ON T_MEM.GZIP=T_GRD.ZIP UNION ALL	\n");
		sql.append("\t	        SELECT '충남' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='충남')T_MEM ON T_MEM.GZIP=T_GRD.ZIP UNION ALL	\n");
		sql.append("\t	        SELECT '전라/제주' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='전라/제주')T_MEM ON T_MEM.GZIP=T_GRD.ZIP UNION ALL	\n");
		sql.append("\t	        SELECT '경상' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='경상')T_MEM ON T_MEM.GZIP=T_GRD.ZIP UNION ALL	\n");
		sql.append("\t	        SELECT '강원' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='강원')T_MEM ON T_MEM.GZIP=T_GRD.ZIP UNION ALL	\n");
		sql.append("\t	        SELECT '기타' GZIP, SEX, NVL(CNT,0) CNT FROM T_GRD	\n");
		sql.append("\t	        LEFT JOIN (SELECT * FROM MEM WHERE GZIP='기타')T_MEM ON T_MEM.GZIP=T_GRD.ZIP	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY GZIP	\n");
		sql.append("\t	ORDER BY DECODE(GZIP, '서울/경기', 1, '충북', 2, '충남', 3, '전라/제주', 4, '경상', 5, '강원', 6, '기타', 7)	\n");
		
		return sql.toString();
	}
    
}
