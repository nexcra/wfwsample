/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemCntDaoProc
*   작성자    : 강선영
*   내용      : 관리자 회원수 조회
*   적용범위  : golf
*   작성일자  : 2009-07-28
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
public class GolfAdmMemCntDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemCntDaoProc() {}	

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
			
			String sql = this.getSelectMemberQuery();			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 
				while(rs.next())  {	
					result.addString("TODAY"			,rs.getString("TODAY") );
					result.addString("ALL_MEMBER"		,rs.getString("ALL_MEMBER") );
					result.addString("ALL_SITE_MEMBER"	,rs.getString("ALL_SITE_MEMBER") );
					result.addString("ALL_TM_MEMBER"	,rs.getString("ALL_TM_MEMBER") );
					result.addString("SECE_MEMBER"		,rs.getString("SECE_MEMBER") );
					//result.addString("TM_MEMBER"		,rs.getString("TM_MEMBER") );
					//result.addString("TM_GOLD_MEMBER"	,rs.getString("TM_GOLD_MEMBER") );
					//result.addString("TM_BLUE_MEMBER"	,rs.getString("TM_BLUE_MEMBER") );
					//result.addString("TM_SITE_GOLD_MEMBER"	,rs.getString("TM_SITE_GOLD_MEMBER") );
					//result.addString("TM_SITE_BLUE_MEMBER"	,rs.getString("TM_SITE_BLUE_MEMBER") );

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

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */

	public DbTaoResult execute_grade(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			String sql = this.getSelectMemberGradeQuery();			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();

			String grade = "";
			String grade_nm = "";
			if(rs != null) {			 
				while(rs.next())  {	
					
					grade = rs.getString("GOLF_CMMN_CODE_NM");

					result.addString("CDHD_CTGO_SEQ_NO"			,rs.getString("CDHD_CTGO_SEQ_NO") );
 					result.addString("CDHD_NM"			,grade );
					result.addString("CNT"		,rs.getString("CNT") );

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
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */

	public DbTaoResult execute_joinchnl(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			String sql = this.getSelectJoinChnlQuery();			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 
				while(rs.next())  {	
					
					result.addString("JOIN_CHNL"			, rs.getString("JOIN_CHNL") );
 					result.addString("GOLF_CMMN_CODE_NM"	, rs.getString("GOLF_CMMN_CODE_NM") );
					result.addString("ALL_CNT"				,rs.getString("ALL_CNT") );
					result.addString("NO_SITE_CNT"			,rs.getString("NO_SITE_CNT") );
					result.addString("SITE_CNT"				,rs.getString("SITE_CNT") );

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
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */

	public DbTaoResult execute_dategrade(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			String st_date		= data.getString("st_date");
			String end_date		= data.getString("end_date");

			conn = context.getDbConnection("default", null);	
			
			String sql = this.getSelectDateGradeQuery();			
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, st_date);
			pstmt.setString(++idx, end_date);


			rs = pstmt.executeQuery();

			if(rs != null) {			 
				while(rs.next())  {	
					
					result.addString("GOLF_CMMN_CODE_NM"	, rs.getString("GOLF_CMMN_CODE_NM") );
					result.addString("CNT"		,rs.getString("CNT") );
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
    * 회원수 Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectMemberQuery(){
        StringBuffer sql = new StringBuffer();        

      
		sql.append("SELECT TO_CHAR(SYSDATE,'yyyy-MM-dd hh24:mi:ss')  TODAY, SUM(CNT1) ALL_TM_MEMBER ,SUM(CNT2) ALL_SITE_MEMBER, SUM(CNT3) ALL_MEMBER, SUM(CNT4) SECE_MEMBER \n");
		sql.append("FROM  ( \n");
		sql.append("   SELECT        0 CNT1, COUNT(*) CNT2, COUNT(*) CNT3 ,       0 CNT4  FROM BCDBA.TBGGOLFCDHD  WHERE NVL(SECE_YN,'N')='N' \n");
		sql.append("   UNION ALL  \n");
		sql.append("   SELECT COUNT(*) CNT1,        0 CNT2, COUNT(*) CNT3,        0 CNT4  FROM BCDBA.TBLUGTMCSTMR WHERE RND_CD_CLSS='2' AND TB_RSLT_CLSS='01'   \n"); 
		sql.append("   UNION ALL  \n");
		sql.append("   SELECT        0 CNT1,        0 CNT2,        0 CNT3, COUNT(*) CNT4  FROM BCDBA.TBGGOLFCDHD  WHERE NVL(SECE_YN,'N')='Y'   \n"); 
		sql.append(") A  \n");

		return sql.toString();
    }


	/** ***********************************************************************
    * 등급별 회원수 Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectMemberGradeQuery(){
        StringBuffer sql = new StringBuffer();        
		sql.append(" SELECT  BB.CDHD_CTGO_SEQ_NO,GOLF_CMMN_CODE_NM,  CNT   FROM (   \n");
		sql.append("     SELECT A.CDHD_CTGO_SEQ_NO, COUNT(*) CNT  FROM BCDBA.TBGGOLFCDHD A  \n");  
		sql.append("     WHERE NVL(A.SECE_YN,'N')  ='N'   \n");
		sql.append("     GROUP BY A.CDHD_CTGO_SEQ_NO  ) BB ,    \n");
		sql.append("     BCDBA.TBGCMMNCODE CC 	  \n");
		sql.append("WHERE CC.GOLF_CMMN_CLSS='0052' \n");
		sql.append("AND   CC.GOLF_CMMN_CODE=BB.CDHD_CTGO_SEQ_NO \n");

		return sql.toString();
    }  


	/** ***********************************************************************
    * 채널별 회원수 Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectJoinChnlQuery(){
        StringBuffer sql = new StringBuffer();     
 
        sql.append(" SELECT  A.JOIN_CHNL, MAX(A.GOLF_CMMN_CODE_NM) GOLF_CMMN_CODE_NM, SUM(A.CNT) ALL_CNT , SUM(DECODE(A.CHNL,'1',A.CNT,0) ) NO_SITE_CNT , SUM(DECODE(A.CHNL,'2',A.CNT,0) )  SITE_CNT  \n");
		sql.append(" FROM (  \n");
		sql.append("    SELECT '1' CHNL, RCRU_PL_CLSS JOIN_CHNL , '' GOLF_CMMN_CODE_NM,  COUNT(*) CNT  \n");
		sql.append(" 	FROM BCDBA.TBLUGTMCSTMR WHERE RND_CD_CLSS='2' AND TB_RSLT_CLSS='01'  GROUP BY  RCRU_PL_CLSS  \n");
		sql.append(" 	UNION ALL  \n");
		sql.append(" 	SELECT '2' CHNL, C.JOIN_CHNL, MAX(B.GOLF_CMMN_CODE_NM) GOLF_CMMN_CODE_NM, COUNT(*) CNT  \n");
		sql.append(" 	FROM BCDBA.TBGGOLFCDHD C, BCDBA.TBGCMMNCODE B  \n");
		sql.append(" 	WHERE NVL(C.SECE_YN,'N')='N'  \n");
		sql.append(" 	AND B.GOLF_CMMN_CLSS='0051'  \n");
		sql.append(" 	AND C.JOIN_CHNL =  B.GOLF_CMMN_CODE  \n");
		sql.append(" 	GROUP BY  C.JOIN_CHNL  \n");
		sql.append(" ) A   \n");
		sql.append(" GROUP BY A.JOIN_CHNL  \n");

		return sql.toString();
    }

	/** ***********************************************************************
    * 가입일자별 등급 회원수 Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectDateGradeQuery(){
        StringBuffer sql = new StringBuffer();        
		sql.append(" SELECT  GOLF_CMMN_CODE_NM,  CNT  \n");
		sql.append(" FROM (  \n");
		sql.append("     SELECT B.CDHD_CTGO_SEQ_NO,  COUNT(*)  CNT  \n");
		sql.append("     FROM BCDBA.TBGGOLFCDHD A ,  \n");
		sql.append("          BCDBA.TBGGOLFCDHDGRDMGMT B   \n");
		sql.append("     WHERE NVL(A.SECE_YN,'N')  = 'N'    \n");
		sql.append("     AND A.JOIN_CHNL IN ('0001','1000','2000')  \n");
		sql.append("     AND A.JONN_ATON BETWEEN ? AND ?   \n");
		sql.append("     AND A.CDHD_ID =  B.CDHD_ID  \n");
		sql.append("     GROUP BY B.CDHD_CTGO_SEQ_NO  ) BB ,  \n");
		sql.append("     BCDBA.TBGCMMNCODE CC  \n");
		sql.append(" WHERE CC.GOLF_CMMN_CLSS='0052'  \n");
		sql.append(" AND   CC.GOLF_CMMN_CODE=BB.CDHD_CTGO_SEQ_NO  \n");

		return sql.toString();
    }









}
