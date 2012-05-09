/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110325  이경희 	회원리스트 엑셀다운로드 성능개선
*20110328  이경희 	회원리스트 엑셀다운로드 성능개선 원복 - 커서를 많이 이용함 
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
 * @author	미디어포스
 * @version	1.0  
 * GOLFLOUNG              20100222     임은혜     검색시 유료회원가입일 추가
 ******************************************************************************/
public class GolfAdmMemXlsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자   
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmMemXlsDaoProc() {}	

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
			String sch_YN		= data.getString("SCH_YN");	
			String sch_GRADE	= data.getString("SCH_GRADE");	
			String sch_STATE	= data.getString("SCH_STATE");
			String sch_ROUTE	= data.getString("SCH_ROUTE");
			String sch_DATE		= data.getString("SCH_DATE");
			String sch_MONEY_ST	= data.getString("SCH_MONEY_ST");
			String sch_MONEY_ED	= data.getString("SCH_MONEY_ED");
			String sch_DATE_ST	= data.getString("SCH_DATE_ST");
			String sch_DATE_ED	= data.getString("SCH_DATE_ED");
			String sch_TYPE		= data.getString("SCH_TYPE");
			String sch_TEXT		= data.getString("SCH_TEXT");
			String sql = "";
			
			sch_DATE_ST = GolfUtil.rplc(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.rplc(sch_DATE_ED, "-", "");
			
			//조회 ----------------------------------------------------------
//			if("SKI".equals(sch_GRADE)) {
//				sql = this.getSelectSkiQuery(sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT);
//			} else {
//				sql = this.getSelectQuery(sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT);
//			}
			sql = this.getSelectQuery(sch_YN, sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT, sch_ROUTE, sch_DATE);

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			if(!GolfUtil.empty(sch_YN) && "Y".equals(sch_YN)){		// 대표등급기준이 아닌, 이중등급소지자도 리스트 나오게 수정
				if(!GolfUtil.empty(sch_GRADE) && !"CHARGE".equals(sch_GRADE) && !"SKI".equals(sch_GRADE) && !"CORPDSMEM1102".equals(sch_GRADE)) {
					pstmt.setString(++idx, sch_GRADE);
				}
				if(!GolfUtil.empty(sch_GRADE) && ("SKI".equals(sch_GRADE)||"CORPDSMEM1102".equals(sch_GRADE))) {
					pstmt.setString(++idx, sch_GRADE);
				}
				if(!GolfUtil.empty(sch_ROUTE)){
					pstmt.setString(++idx, sch_ROUTE);
				}
			}
			
			
			rs = pstmt.executeQuery();
			String sttlAmt = "";

			if(rs != null) {			 

				while(rs.next())  {	
					
					if (rs.getString("golf_cmmn_code_nm").equals("Champion") || rs.getString("golf_cmmn_code_nm").equals("Blue")
							|| rs.getString("golf_cmmn_code_nm").equals("Gold") || rs.getString("golf_cmmn_code_nm").equals("Smart1000")
							|| rs.getString("golf_cmmn_code_nm").equals("e-Champion") || rs.getString("golf_cmmn_code_nm").equals("Black"))		
						
						sttlAmt = rs.getString("STTL_AMT");
					else 
						sttlAmt = "";					
										
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("NAME" 				,rs.getString("NAME") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("MOBILE" 				,rs.getString("MOBILE") );
					result.addString("AGE"					,rs.getString("AGE") );
					result.addString("SEX" 					,rs.getString("SEX") );
					result.addString("JOIN_DATE" 			,rs.getString("JOIN_DATE") );
					result.addString("PAY_START" 			,rs.getString("PAY_START") );
					result.addString("PAY_END" 				,rs.getString("PAY_END") );
					result.addString("GOLF_CMMN_CODE_NM" 	,rs.getString("GOLF_CMMN_CODE_NM") );
					result.addString("TOT_AMT" 				,GolfUtil.comma(rs.getString("TOT_AMT")) );
					result.addString("JOIN_CHNL" 			,rs.getString("JOIN_CHNL") );
					result.addString("JOIN_CHNL_NM" 		,rs.getString("JOIN_CHNL_NM") );
					result.addString("STATE" 				,rs.getString("STATE") );
					result.addString("ADDR"					,rs.getString("ADDR") );
					result.addString("ESTM_ITM_CLSS" 		,rs.getString("ESTM_ITM_CLSS") );
					result.addString("APPR_OPION" 			,rs.getString("APPR_OPION") );
					result.addString("AFFI_FIRM_NM" 		,rs.getString("AFFI_FIRM_NM") );
					result.addString("EMAIL" 				,rs.getString("EMAIL") );					
					result.addString("MEMBER_CLSS_HAN" 		,rs.getString("MEMBER_CLSS_HAN") );
					//result.addString("STTL_AMT" 			,rs.getString("STTL_AMT") );
					result.addString("STTL_AMT" 			,sttlAmt );
					
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
	private String getSelectQuery(String sch_YN, String sch_GRADE, String sch_STATE, String sch_MONEY_ST, String sch_MONEY_ED, String sch_DATE_ST, String sch_DATE_ED, String sch_TYPE, String sch_TEXT, String sch_ROUTE, String sch_DATE){
        StringBuffer sql = new StringBuffer();        

		sql.append("\n SELECT	*														\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM											\n");
		
		sql.append("\t 			, CDHD_ID, NAME,JUMIN_NO, AGE, SEX, JOIN_DATE, MOBILE, EMAIL			\n");
		sql.append("\t 			, PAY_START, PAY_END, GOLF_CMMN_CODE_NM, TOT_AMT,JOIN_CHNL,JOIN_CHNL_NM, STATE, ADDR, MEMBER_CLSS_HAN, STTL_AMT	\n");
		sql.append("\t			, ESTM_ITM_CLSS, AFFI_FIRM_NM,APPR_OPION 	\n");
		
		sql.append("\t 			FROM (SELECT ROWNUM RNUM								\n");
		
		
		sql.append("\t , T1.CDHD_ID, T1.HG_NM AS NAME, SUBSTR(T1.JUMIN_NO,1,6) || '-' || SUBSTR(T1.JUMIN_NO,7,13) JUMIN_NO , T1.MOBILE																	\n"); 
		sql.append("\t , (CASE SUBSTR(T1.JUMIN_NO,7,1) WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(T1.JUMIN_NO,1,2)+1	\n"); 
		sql.append("\t    WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(T1.JUMIN_NO,1,2)+1								\n"); 
		sql.append("\t    ELSE TO_CHAR(SYSDATE,'YYYY')-1900-SUBSTR(T1.JUMIN_NO,1,2)+1 END) AS AGE							\n");
            		
		sql.append("\t , (CASE SUBSTR(T1.JUMIN_NO,7,1) WHEN '1'  THEN '남' WHEN '3' THEN '남' WHEN '5' THEN '남' ELSE '여' END) AS SEX	\n"); 
		sql.append("\t , TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YY-MM-DD') AS JOIN_DATE								\n"); 
		sql.append("\t , TO_CHAR(TO_DATE(T1.ACRG_CDHD_JONN_DATE),'YY-MM-DD') AS PAY_START								\n"); 
		sql.append("\t , TO_CHAR(TO_DATE(T1.ACRG_CDHD_END_DATE),'YY-MM-DD') AS PAY_END									\n"); 
		sql.append("\t , T3.GOLF_CMMN_CODE_NM																			\n"); 
		sql.append("\t , T1.JOIN_CHNL, T1.EMAIL																			\n"); 
		sql.append("\t , T1.ESTM_ITM_CLSS,T1.AFFI_FIRM_NM, T1.APPR_OPION	\n");
		sql.append("\t , (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND T1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM	\n");

		sql.append("\t , (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT) AS TOT_AMT											\n"); 
		sql.append("\t , CASE T1.SECE_YN WHEN 'Y' THEN '탈퇴' ELSE '정상' END AS STATE									\n"); 
		sql.append("\t , T1.ZIPADDR||' '||T1.DETAILADDR AS ADDR															\n");
 		sql.append("\t	, (CASE  \n");
 		sql.append("\t	        WHEN T1.MEMBER_CLSS in ('1','4') THEN '개인' \n");
 		sql.append("\t	        WHEN T1.MEMBER_CLSS = '5' THEN '법인' END ) MEMBER_CLSS_HAN, STTL_AMT 	\n");

		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	            LEFT JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		
		sql.append("\t	            LEFT JOIN (   SELECT CDHD_ID, STTL_AMT	\n");
		sql.append("\t	            				FROM  BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	            				WHERE  (STTL_ATON, CDHD_ID) IN (SELECT  MAX(STTL_ATON)STTL_ATON, CDHD_ID	\n");
		sql.append("\t	            									FROM BCDBA.TBGSTTLMGMT  \n");
		sql.append("\t	            									WHERE STTL_GDS_CLSS IN (  \n");
		sql.append("\t	            									'0001','0002','0003','0008','0012','0022')  \n");
		sql.append("\t	            									GROUP BY CDHD_ID)	\n");
		sql.append("\t	            				) T5 ON T5.CDHD_ID = T1.CDHD_ID	\n");				


		if(!GolfUtil.empty(sch_YN) && "Y".equals(sch_YN)){		// 대표등급기준이 아닌, 이중등급소지자도 리스트 나오게 수정
			if(!GolfUtil.empty(sch_GRADE) && !("CHARGE".equals(sch_GRADE) || "SKI".equals(sch_GRADE) || "CORPDSMEM1102".equals(sch_GRADE))) {
				sql.append("\t		JOIN BCDBA.TBGGOLFCDHDGRDMGMT GRD ON T1.CDHD_ID=GRD.CDHD_ID AND GRD.CDHD_CTGO_SEQ_NO=?	\n");
			}
		}
			
		
		sql.append("\t WHERE T1.CDHD_ID IS NOT NULL																		\n");
		

		
		if(!GolfUtil.empty(sch_YN) && "Y".equals(sch_YN)){			

			if(!GolfUtil.empty(sch_GRADE)){
				if("CHARGE".equals(sch_GRADE)) {
					
		 			sql.append("\t 	AND T1.CDHD_CTGO_SEQ_NO IN															\n"); // 유료회원
		 			sql.append("\t 							(SELECT CDHD_CTGO_SEQ_NO									\n");	
		 			sql.append("\t 							 FROM BCDBA.TBGGOLFCDHDCTGOMGMT								\n");	
		 			sql.append("\t 							 WHERE (CDHD_SQ1_CTGO = '0002' OR CDHD_CTGO_SEQ_NO = '27')	\n");	
		 			sql.append("\t 							 AND CDHD_CTGO_SEQ_NO != '8')								\n");						
		 			
		 			
				}else if("SKI".equals(sch_GRADE)) {		// 블루스키회원
		 			sql.append("\t AND AFFI_FIRM_NM=? AND JOIN_CHNL='2302'	\n");
				}else if("CORPDSMEM1102".equals(sch_GRADE)) {		// Wisebiz(법인)
		 			sql.append("\t AND AFFI_FIRM_NM=?	\n");
		 		}
			}
			
			if(!GolfUtil.empty(sch_STATE)){
				if(sch_STATE.equals("Y")){
					sql.append("\t AND SECE_YN='Y'\n");
				}else{
					sql.append("\t AND (SECE_YN IS NULL OR SECE_YN ='N') \n");
				}
			}
			
			if(!GolfUtil.empty(sch_DATE)){
				if("charge_date".equals(sch_DATE)){	// 유료가입일
					sql.append("\t AND ACRG_CDHD_JONN_DATE>='"+sch_DATE_ST+"'\n");
					sql.append("\t AND ACRG_CDHD_JONN_DATE<='"+sch_DATE_ED+"'\n");
				}else{	// 가입일
					sql.append("\t AND JONN_ATON>='"+sch_DATE_ST+"000000'\n");
					sql.append("\t AND JONN_ATON<='"+sch_DATE_ED+"240000'\n");
				}
			}
			
			if(!GolfUtil.empty(sch_MONEY_ST))	sql.append("\t AND (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT)>="+sch_MONEY_ST+"\n");
			if(!GolfUtil.empty(sch_MONEY_ED))	sql.append("\t AND (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT)<="+sch_MONEY_ED+"\n");
			if(!GolfUtil.empty(sch_TEXT))	sql.append("\t AND "+sch_TYPE+" LIKE '%"+sch_TEXT+"%'\n");
			if(!GolfUtil.empty(sch_ROUTE)) sql.append("\t AND T1.JOIN_CHNL=? \n");
		}		
		
		//sql.append("\t ORDER BY T1.JONN_ATON DESC		\n");		
		
		
		sql.append("\t 			)		\n");
		//sql.append("\t 	ORDER BY RNUM	\n");
		sql.append("\t 	)				\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
     * Query를 생성하여 리턴한다.      
     ************************************************************************ */
     private String getSelectSkiQuery(String sch_GRADE, String sch_STATE, String sch_MONEY_ST, String sch_MONEY_ED, String sch_DATE_ST, String sch_DATE_ED, String sch_TYPE, String sch_TEXT){
         StringBuffer sql = new StringBuffer();        

 		sql.append("\n SELECT	*														\n");
 		sql.append("\t FROM (SELECT ROWNUM RNUM											\n");
 		
 		sql.append("\t 			, CDHD_ID, NAME,JUMIN_NO, AGE, SEX, JOIN_DATE, MOBILE			\n");
 		sql.append("\t 			, PAY_START, PAY_END, GOLF_CMMN_CODE_NM, TOT_AMT,JOIN_CHNL,JOIN_CHNL_NM, STATE, ADDR	\n");
 		sql.append("\t			, ESTM_ITM_CLSS, AFFI_FIRM_NM,APPR_OPION 	\n");
 		
 		sql.append("\t 			FROM (SELECT ROWNUM RNUM								\n");
 		
 		
 		sql.append("\t , T1.CDHD_ID, T1.HG_NM AS NAME,SUBSTR(T1.JUMIN_NO,1,6) || '-' || SUBSTR(T1.JUMIN_NO,7,13) JUMIN_NO , T1.MOBILE																	\n"); 
 		sql.append("\t , (CASE SUBSTR(T1.JUMIN_NO,7,1) WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(T1.JUMIN_NO,1,2)+1	\n"); 
 		sql.append("\t    WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(T1.JUMIN_NO,1,2)+1								\n"); 
 		sql.append("\t    ELSE TO_CHAR(SYSDATE,'YYYY')-1900-SUBSTR(T1.JUMIN_NO,1,2)+1 END) AS AGE							\n");
             		
 		sql.append("\t , (CASE SUBSTR(T1.JUMIN_NO,7,1) WHEN '1'  THEN '남' WHEN '3' THEN '남' WHEN '5' THEN '남' ELSE '여' END) AS SEX	\n"); 
 		sql.append("\t , TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YY-MM-DD') AS JOIN_DATE								\n"); 
 		sql.append("\t , TO_CHAR(TO_DATE(T1.ACRG_CDHD_JONN_DATE),'YY-MM-DD') AS PAY_START								\n"); 
 		sql.append("\t , TO_CHAR(TO_DATE(T1.ACRG_CDHD_END_DATE),'YY-MM-DD') AS PAY_END									\n"); 
 		sql.append("\t , T5.GOLF_CMMN_CODE_NM																			\n"); 
 		sql.append("\t , T1.JOIN_CHNL																			\n"); 
 		sql.append("\t , T1.ESTM_ITM_CLSS,T1.AFFI_FIRM_NM, T1.APPR_OPION	\n");
 		sql.append("\t , (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND T1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM	\n");

 		sql.append("\t , (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT) AS TOT_AMT											\n"); 
 		sql.append("\t , CASE T1.SECE_YN WHEN 'Y' THEN '탈퇴' ELSE '정상' END AS STATE									\n"); 
 		sql.append("\t , T1.ZIPADDR||' '||T1.DETAILADDR AS ADDR															\n"); 
 		
 		sql.append("\t FROM BCDBA.TBGGOLFCDHD T1																		\n"); 
 		sql.append("\t JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID										\n"); 
 		sql.append("\t JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO						\n"); 
 		sql.append("\t JOIN BCDBA.TBGCMMNCODE T5 ON T5.GOLF_CMMN_CODE=T4.CDHD_SQ2_CTGO AND T5.GOLF_CMMN_CLSS='0005'		\n");
 		sql.append("\t WHERE T1.CDHD_ID IS NOT NULL																		\n");
 		
 		if("SKI".equals(sch_GRADE)) {
 			sql.append("\t AND T1.AFFI_FIRM_NM='SKI'	\n");
 		} else if(!GolfUtil.empty(sch_GRADE))	sql.append("\t AND T3.CDHD_CTGO_SEQ_NO='"+sch_GRADE+"'\n");
 		if(!GolfUtil.empty(sch_STATE)){
 			if(sch_STATE.equals("Y")){
 				sql.append("\t AND T1.SECE_YN='Y'\n");
 			}else{
 				sql.append("\t AND (T1.SECE_YN IS NULL OR T1.SECE_YN ='N') \n");
 			}
 		}
 		if(!GolfUtil.empty(sch_MONEY_ST))	sql.append("\t AND (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT)>="+sch_MONEY_ST+"\n");
 		if(!GolfUtil.empty(sch_MONEY_ED))	sql.append("\t AND (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT)<="+sch_MONEY_ED+"\n");
 		if(!GolfUtil.empty(sch_DATE_ST))	sql.append("\t AND T1.JONN_ATON>='"+sch_DATE_ST+"000000'\n");
 		if(!GolfUtil.empty(sch_DATE_ED))	sql.append("\t AND T1.JONN_ATON<='"+sch_DATE_ED+"240000'\n");
 		if(!GolfUtil.empty(sch_TEXT))	sql.append("\t AND "+sch_TYPE+" LIKE '%"+sch_TEXT+"%'\n");
 		
 		
 		
 		sql.append("\t ORDER BY T1.JONN_ATON DESC		\n");		
 		
 		
 		sql.append("\t 			)		\n");
 		sql.append("\t 	ORDER BY RNUM	\n");
 		sql.append("\t 	)				\n");
 		
 		return sql.toString();
     }
}
