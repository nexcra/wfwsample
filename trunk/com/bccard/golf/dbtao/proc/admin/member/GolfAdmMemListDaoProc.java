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
*   2012.03.07  shin cheong gwi  수정내용 : 여행팀 회원조회 소메뉴 추가로 인하여 소스 수정 (getSelectQuery())
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
public class GolfAdmMemListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		ResultSet rs2 = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
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
			String action_url 	= data.getString("ACTION_URL");			
			String pay_date		= "";		// 유료가입/종료일
			String sql = "", sql2 = "";
			
			sch_DATE_ST = GolfUtil.rplc(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.rplc(sch_DATE_ED, "-", "");
			 
			//조회 ----------------------------------------------------------
//			if("SKI".equals(sch_GRADE)) {
//				sql = this.getSelectSkiQuery(sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT);
//			}else if(!GolfUtil.empty(sch_GRADE) || sch_TYPE.equals("MOBILE")){
//				sql = this.getSelectOldQuery(sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT);
//			}else{
//				sql = this.getSelectQuery(sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT, sch_ROUTE);
//			}
			// 회원 테이블 통합작업으로 한가지로 통일 
			sql = this.getSelectQuery(sch_YN, sch_GRADE, sch_STATE, sch_MONEY_ST, sch_MONEY_ED, sch_DATE_ST, sch_DATE_ED, sch_TYPE, sch_TEXT, sch_ROUTE, sch_DATE, action_url);

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			//pstmt.setLong(++idx, 20data.getLong("record_size"));
			pstmt.setLong(++idx, 20);
			pstmt.setLong(++idx, data.getLong("page_no"));

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
			
			pstmt.setLong(++idx, data.getLong("page_no"));		
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					sql2 = this.getSTTLAMT();
					pstmt2 = conn.prepareStatement(sql2.toString());	
					pstmt2.setString(1, rs.getString("CDHD_ID"));
					pstmt2.setString(2, rs.getString("JUMIN"));
					rs2 = pstmt2.executeQuery();
				    
					if(rs2.next()) {
					
						if (rs.getString("GRD_NM").equals("Champion") || rs.getString("GRD_NM").equals("Blue")
								|| rs.getString("GRD_NM").equals("Gold") || rs.getString("GRD_NM").equals("Smart1000")
								|| rs.getString("GRD_NM").equals("e-Champion") || rs.getString("GRD_NM").equals("Black")){
							
							result.addString("STTL_AMT",	rs2.getString("STTL_AMT") );
							
						}else {
							
							result.addString("STTL_AMT",	"" );
							
						}
						
					}else {
						
						result.addString("STTL_AMT",	"" );
						
					}
					
					if(rs2 != null) rs2.close();
					if(pstmt2 != null) pstmt2.close();					
					
					pay_date = "";
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
										
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("NAME" 				,rs.getString("NAME") );
					result.addString("MOBILE" 				,rs.getString("MOBILE") );
					result.addString("AGE"					,rs.getString("AGE") );
					result.addString("SEX" 					,rs.getString("SEX") );
					result.addString("JOIN_DATE" 			,rs.getString("JOIN_DATE") );
					
					if(!GolfUtil.empty(rs.getString("PAY_START"))){
						pay_date = rs.getString("PAY_START") + "<br>~" + rs.getString("PAY_END");
					}
					
					result.addString("PAY_DATE" 			,pay_date );
					result.addString("TOT_AMT" 				,GolfUtil.comma(rs.getString("TOT_AMT")) );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );

					result.addString("JOIN_CHNL" 			,rs.getString("JOIN_CHNL") );
					result.addString("JOIN_CHNL_NM" 		,rs.getString("JOIN_CHNL_NM") );
					result.addString("STATE" 				,rs.getString("STATE") );

					result.addString("ESTM_ITM_CLSS" 		,rs.getString("ESTM_ITM_CLSS") );
					result.addString("AFFI_FIRM_NM" 		,rs.getString("AFFI_FIRM_NM") );

					result.addString("GRD_NM" 				,rs.getString("GRD_NM") );
					result.addString("ADDR" 				,rs.getString("ADDR") );
					result.addString("MEMBER_CLSS_HAN" 		,rs.getString("MEMBER_CLSS_HAN") );
				
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
    private String getSelectQuery(String sch_YN, String sch_GRADE, String sch_STATE, String sch_MONEY_ST, String sch_MONEY_ED, String sch_DATE_ST, String sch_DATE_ED, String sch_TYPE, String sch_TEXT, String sch_ROUTE, String sch_DATE, String action_url){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\t		SELECT *	\n");
		sql.append("\t		FROM (	\n");
		
		sql.append("\t			SELECT ROWNUM RNUM	\n");
		sql.append("\t			, CEIL(ROWNUM/?) AS PAGE	\n");
		sql.append("\t			, MAX(RNUM) OVER() TOT_CNT	\n");
		sql.append("\t			, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
		sql.append("\t			, CDHD_ID, NAME, JUMIN_NO, JUMIN, AGE, SEX, JOIN_DATE	\n");
		sql.append("\t			, PAY_START, PAY_END, TOT_AMT, JOIN_CHNL, JOIN_CHNL_NM, STATE, ESTM_ITM_CLSS ,AFFI_FIRM_NM	\n");
		sql.append("\t			, MOBILE, GRD_NM, ADDR, MEMBER_CLSS_HAN	\n");
		sql.append("\t			FROM (	\n");
		
		sql.append("\t				SELECT ROWNUM RNUM	\n");
		sql.append("\t				, T1.CDHD_ID, HG_NM NAME, SUBSTR(JUMIN_NO,1,6) JUMIN_NO, JUMIN_NO JUMIN	\n");
		sql.append("\t				, (CASE SUBSTR(JUMIN_NO,7,1) WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(JUMIN_NO,1,2)+1	\n");
		sql.append("\t				WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(JUMIN_NO,1,2)+1	\n");
		sql.append("\t				ELSE TO_CHAR(SYSDATE,'YYYY')-1900-SUBSTR(JUMIN_NO,1,2)+1 END) AS AGE	\n");
		sql.append("\t				, (CASE SUBSTR(JUMIN_NO,7,1) WHEN '1' THEN '남' WHEN '3' THEN '남' WHEN '5' THEN '남' ELSE '여' END) AS SEX	\n");
		sql.append("\t				, TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8)),'YY-MM-DD') AS JOIN_DATE	\n");
		sql.append("\t				, TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE),'YY-MM-DD') AS PAY_START	\n");
		sql.append("\t				, TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YY-MM-DD') AS PAY_END	\n");
		sql.append("\t				, JOIN_CHNL	\n");
		sql.append("\t				, (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND T1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM	\n");
		sql.append("\t				, (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT	\n");
		sql.append("\t	            , CASE SECE_YN WHEN 'Y' THEN '탈퇴' ELSE '정상' END AS STATE	\n");
		
		sql.append("\t	            , ESTM_ITM_CLSS,AFFI_FIRM_NM, MOBILE	\n");
		sql.append("\t	            , T3.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t	            , SUBSTR(REPLACE(ZIPADDR,' ',''),0,5) ADDR	\n");
		sql.append("\t	            , (CASE	\n");
		sql.append("\t	            	WHEN MEMBER_CLSS in ('1','4') THEN '개인'	\n");
		sql.append("\t	            	WHEN MEMBER_CLSS = '5' THEN '법인' END ) MEMBER_CLSS_HAN		\n");
		
		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	            LEFT JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");

		if(!GolfUtil.empty(sch_YN) && "Y".equals(sch_YN)){		// 대표등급기준이 아닌, 이중등급소지자도 리스트 나오게 수정
			if(!GolfUtil.empty(sch_GRADE) && !("CHARGE".equals(sch_GRADE) || "SKI".equals(sch_GRADE) || "CORPDSMEM1102".equals(sch_GRADE))) {
				sql.append("\t		JOIN BCDBA.TBGGOLFCDHDGRDMGMT GRD ON T1.CDHD_ID=GRD.CDHD_ID AND GRD.CDHD_CTGO_SEQ_NO=?	\n");
			}
		}
			
		sql.append("\t	            WHERE T1.CDHD_ID IS NOT NULL	\n");
		
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

			if(!GolfUtil.empty(sch_MONEY_ST))	sql.append("\t AND (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT)>="+sch_MONEY_ST+"\n");	// 포인트 검색 시작
			if(!GolfUtil.empty(sch_MONEY_ED))	sql.append("\t AND (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT)<="+sch_MONEY_ED+"\n");	// 포인트 검색 종료
			if(!GolfUtil.empty(action_url)){
				if(!GolfUtil.empty(sch_TEXT))	sql.append("\t AND "+sch_TYPE+" = '"+sch_TEXT+"'\n");	
			}else{
				if(!GolfUtil.empty(sch_TEXT))	sql.append("\t AND "+sch_TYPE+" LIKE '%"+sch_TEXT+"%'\n");	
			}
			if(!GolfUtil.empty(sch_ROUTE))	sql.append("\t AND T1.JOIN_CHNL=? \n");
		}

		if("charge_date".equals(sch_DATE)){	// 유료가입일
			sql.append("\t	            ORDER BY ACRG_CDHD_JONN_DATE DESC	\n");
		}else{
			sql.append("\t	            ORDER BY JONN_ATON DESC	\n");
		}
		
		sql.append("\t	        )	\n");
		sql.append("\t	        ORDER BY RNUM	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    WHERE PAGE = ?	\n");
		
		return sql.toString();
    }
    
    
    
	/*************************************************************************
     * Query를 생성하여 리턴한다.     
     ************************************************************************ */
     private String getSTTLAMT(){
    	 
    	 StringBuffer sql = new StringBuffer();
    	 sql.append("\t	   SELECT STTL_AMT     			\n");
    	 sql.append("\t	   FROM BCDBA.TBGSTTLMGMT      	\n");
    	 sql.append("\t	   WHERE  STTL_ATON  = (SELECT  MAX(STTL_ATON)		\n");
    	 sql.append("\t	   						FROM BCDBA.TBGSTTLMGMT     	\n");
    	 sql.append("\t	   						WHERE STTL_GDS_CLSS IN (	\n");
    	 sql.append("\t	   								'0001','0002','0003','0008','0012','0022')	\n");    	 
    	 sql.append("\t	   						AND (CDHD_ID = ? OR CDHD_ID = ? ))			\n");
    	 
    	 return sql.toString();
    	 
     }    
       

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.   
    ************************************************************************ */
    private String getSelectOldQuery(String sch_GRADE, String sch_STATE, String sch_MONEY_ST, String sch_MONEY_ED, String sch_DATE_ST, String sch_DATE_ED, String sch_TYPE, String sch_TEXT){
        StringBuffer sql = new StringBuffer();        
        
		sql.append("\n SELECT	*														\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM											\n");
		sql.append("\t 			, CEIL(ROWNUM/?) AS PAGE								\n");
		sql.append("\t 			, MAX(RNUM) OVER() TOT_CNT								\n");
		sql.append("\t 			, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM  			\n");
		
		//sql.append("\t 			, CDHD_ID, NAME,JUMIN_NO, AGE, SEX, JOIN_DATE, MOBILE			\n");
		sql.append("\t 			, CDHD_ID, NAME,JUMIN_NO, AGE, SEX, JOIN_DATE			\n");
		sql.append("\t 			, PAY_START, PAY_END, GOLF_CMMN_CODE_NM, TOT_AMT,JOIN_CHNL,JOIN_CHNL_NM, STATE, ADDR	\n");
		sql.append("\t			, ESTM_ITM_CLSS, AFFI_FIRM_NM, MOBILE 	\n");		
		
		sql.append("\t 			FROM (SELECT ROWNUM RNUM, T2.MEMBER_CLSS								\n");
				
		sql.append("\t , T1.CDHD_ID	, T1.MOBILE, T1.HG_NM AS NAME, T1.JUMIN_NO	\n");
		
		sql.append("\t , (CASE SUBSTR(T2.SOCID,7,1)	\n");
		sql.append("\t	WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(T2.SOCID,1,2)+1	\n");
		sql.append("\t	WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(T2.SOCID,1,2)+1	\n");
		sql.append("\t	ELSE TO_CHAR(SYSDATE,'YYYY')-1900-SUBSTR(T2.SOCID,1,2)+1 END) AS AGE	\n");

		sql.append("\t , T1.CDHD_ID	, T1.MOBILE, T1.HG_NM AS NAME, T1.JUMIN_NO	\n");
		
		sql.append("\t , (CASE SUBSTR(JUMIN_NO,7,1) WHEN '1' THEN '1' WHEN '3' THEN '1' ELSE '2' END) AS SEX	\n");            		
				
		
		sql.append("\t , TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YY-MM-DD') AS JOIN_DATE								\n"); 
		sql.append("\t , TO_CHAR(TO_DATE(T1.ACRG_CDHD_JONN_DATE),'YY-MM-DD') AS PAY_START								\n"); 
		sql.append("\t , TO_CHAR(TO_DATE(T1.ACRG_CDHD_END_DATE),'YY-MM-DD') AS PAY_END									\n"); 
		sql.append("\t , T5.GOLF_CMMN_CODE_NM																			\n"); 
		sql.append("\t , T1.JOIN_CHNL 																			\n"); 
		sql.append("\t , T1.ESTM_ITM_CLSS,T1.AFFI_FIRM_NM 	\n");
		sql.append("\t , (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND T1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM	\n");

		sql.append("\t , (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT) AS TOT_AMT											\n"); 
		sql.append("\t , CASE T1.SECE_YN WHEN 'Y' THEN '탈퇴' ELSE '정상' END AS STATE									\n"); 
		sql.append("\t , T1.ZIPADDR||' '||T1.DETAILADDR AS ADDR															\n"); 
		
		sql.append("\t FROM BCDBA.TBGGOLFCDHD T1																		\n"); 
		sql.append("\t JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID										\n"); 
		sql.append("\t JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO						\n"); 
		sql.append("\t JOIN BCDBA.TBGCMMNCODE T5 ON T5.GOLF_CMMN_CODE=T4.CDHD_SQ2_CTGO AND T5.GOLF_CMMN_CLSS='0005'		\n");
		sql.append("\t WHERE T1.CDHD_ID IS NOT NULL																		\n");
				
		if(!GolfUtil.empty(sch_GRADE))	sql.append("\t AND T3.CDHD_CTGO_SEQ_NO='"+sch_GRADE+"'\n");
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
		
		sql.append("\t WHERE PAGE = ?	\n");		

		return sql.toString();
    }

	/** ***********************************************************************
     * Query를 생성하여 리턴한다.     
     ************************************************************************ */
     private String getSelectSkiQuery(String sch_GRADE, String sch_STATE, String sch_MONEY_ST, String sch_MONEY_ED, String sch_DATE_ST, String sch_DATE_ED, String sch_TYPE, String sch_TEXT){
         StringBuffer sql = new StringBuffer();
         
 		sql.append("\t	SELECT RNUM, PAGE, TOT_CNT, ART_NUM, JOIN_DATE, PAY_START, PAY_END, TOT_AMT,JOIN_CHNL,JOIN_CHNL_NM	\n");
 		sql.append("\t	, STATE, T1.CDHD_ID, T1.NAME,T1.JUMIN_NO, AGE, T1.SEX, GOLF_CMMN_CODE_NM , ESTM_ITM_CLSS,AFFI_FIRM_NM	\n");
 		sql.append("\t	, CASE MEMBER_CLSS WHEN '5' THEN (select MAX(USER_MOB_NO)	\n");
 		sql.append("\t			from BCDBA.TBENTPUSER B2		\n");
 		sql.append("\t			JOIN BCDBA.TBENTPMEM B3 ON B3.mem_id=B2.mem_id		\n");
 		sql.append("\t			where T1.CDHD_ID=B2.ACCOUNT and B3.mem_stat='2' and B3.mem_CLSS='6' and B3.sec_Date is null group by B2.ACCOUNT )	\n");
 		sql.append("\t	ELSE MOBILE END as MOBILE		\n");
 		
 		sql.append("\t	FROM (	\n");
 		
 		sql.append("\t		SELECT *	\n");
 		sql.append("\t		FROM (	\n");
 		
 		sql.append("\t			SELECT ROWNUM RNUM	\n");
 		sql.append("\t			, CEIL(ROWNUM/?) AS PAGE	\n");
 		sql.append("\t			, MAX(RNUM) OVER() TOT_CNT	\n");
 		sql.append("\t			, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
 		sql.append("\t			, CDHD_ID, NAME, JUMIN_NO ,AGE, SEX, JOIN_DATE	\n");
 		sql.append("\t			, PAY_START, PAY_END, TOT_AMT,JOIN_CHNL, JOIN_CHNL_NM, STATE , ESTM_ITM_CLSS,AFFI_FIRM_NM	\n");
 		sql.append("\t			FROM (	\n");
 		
 		sql.append("\t				SELECT ROWNUM RNUM	\n");
 		sql.append("\t				, CDHD_ID, HG_NM NAME, SUBSTR(JUMIN_NO,1,6) JUMIN_NO 	\n");
 		sql.append("\t				, (CASE SUBSTR(JUMIN_NO,7,1) WHEN '3' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(JUMIN_NO,1,2)+1	\n");
 		sql.append("\t				WHEN '4' THEN TO_CHAR(SYSDATE,'YYYY')-2000-SUBSTR(JUMIN_NO,1,2)+1	\n");
 		sql.append("\t				ELSE TO_CHAR(SYSDATE,'YYYY')-1900-SUBSTR(JUMIN_NO,1,2)+1 END) AS AGE	\n");
 		sql.append("\t				, (CASE SUBSTR(JUMIN_NO,7,1) WHEN '1' THEN '남' WHEN '3' THEN '남' WHEN '5' THEN '남' ELSE '여' END) AS SEX	\n");
 		sql.append("\t				, TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8)),'YY-MM-DD') AS JOIN_DATE	\n");
 		sql.append("\t				, TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE),'YY-MM-DD') AS PAY_START	\n");
 		sql.append("\t				, TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YY-MM-DD') AS PAY_END	\n");
 		sql.append("\t				, JOIN_CHNL	\n");
 		sql.append("\t				, (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND T1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM	\n");
 		sql.append("\t				, (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT	\n");
 		sql.append("\t	            , CASE SECE_YN WHEN 'Y' THEN '탈퇴' ELSE '정상' END AS STATE	\n");
 		sql.append("\t	            , ESTM_ITM_CLSS,AFFI_FIRM_NM 	\n");
 		sql.append("\t	            FROM BCDBA.TBGGOLFCDHD T1	\n");
 		sql.append("\t	            WHERE CDHD_ID IS NOT NULL	\n");

 		if("SKI".equals(sch_GRADE)) {
 			sql.append("\t AND T1.AFFI_FIRM_NM='SKI'	\n");
 		}
 		if(!GolfUtil.empty(sch_STATE)){
 			if(sch_STATE.equals("Y")){
 				sql.append("\t AND SECE_YN='Y'\n");
 			}else{
 				sql.append("\t AND (SECE_YN IS NULL OR SECE_YN ='N') \n");
 			}
 		}
 		if(!GolfUtil.empty(sch_MONEY_ST))	sql.append("\t AND (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT)>="+sch_MONEY_ST+"\n");
 		if(!GolfUtil.empty(sch_MONEY_ED))	sql.append("\t AND (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT)<="+sch_MONEY_ED+"\n");
 		if(!GolfUtil.empty(sch_DATE_ST))	sql.append("\t AND JONN_ATON>='"+sch_DATE_ST+"000000'\n");
 		if(!GolfUtil.empty(sch_DATE_ED))	sql.append("\t AND JONN_ATON<='"+sch_DATE_ED+"240000'\n");
 		//if(sch_TYPE.equals("HG_NM") || sch_TYPE.equals("CDHD_ID")){
 			if(!GolfUtil.empty(sch_TEXT))	sql.append("\t AND "+sch_TYPE+" LIKE '%"+sch_TEXT+"%'\n");
 		//}
 		
 		sql.append("\t	            ORDER BY JONN_ATON DESC	\n");
 		sql.append("\t	        )	\n");
 		sql.append("\t	        ORDER BY RNUM	\n");
 		sql.append("\t	    )	\n");
 		sql.append("\t	    WHERE PAGE = ?	\n");
 		sql.append("\t	) T1	\n");
 		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID	\n");
 		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T5 ON T5.GOLF_CMMN_CODE=T4.CDHD_SQ2_CTGO AND T5.GOLF_CMMN_CLSS='0005'	\n");
 		
 		return sql.toString();
     }

}
