/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄 티타임 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스  
 * @version	1.0
 ******************************************************************************/
public class GolfadmPreTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreTimeListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		GolfAdminEtt userEtt = null;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();
			 
			//조회 ----------------------------------------------------------
			String sch_YN = data.getString("SCH_YN");
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_RESER_CODE = data.getString("SCH_RESER_CODE");
			String sch_VIEW_YN = data.getString("SCH_VIEW_YN");
			String sch_EVNT_YN = data.getString("SCH_EVNT_YN");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");		
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			String listtype = data.getString("LISTTYPE");
			String sort = data.getString("SORT");
			String sql = this.getSelectQuery(sch_YN, sch_GR_SEQ_NO, sch_RESER_CODE, sch_VIEW_YN, sch_DATE, sch_DATE_ST, sch_DATE_ED, listtype, admId, admClss, sort, sch_EVNT_YN);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if (!sch_GR_SEQ_NO.equals("")){
				pstmt.setString(++idx, sch_GR_SEQ_NO);
			}
			if (!sch_RESER_CODE.equals("")){
				pstmt.setString(++idx, sch_RESER_CODE);
			}
			if (!sch_VIEW_YN.equals("")){
				pstmt.setString(++idx, sch_VIEW_YN);
			}
			if(!sch_EVNT_YN.equals("")){
				pstmt.setString(++idx, sch_EVNT_YN);
			}	

			if (!"".equals(sort)){
				pstmt.setString(++idx, sort);
			}
			
			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addInt("TIME_SEQ_NO" 		,rs.getInt("TIME_SEQ_NO") );
					result.addString("VIEW_YN" 			,rs.getString("VIEW_YN") );
					result.addString("STATUS_NM" 			,rs.getString("STATUS_NM") );					
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BKPS_DATE"		,DateUtil.format(rs.getString("BKPS_DATE"), "yyyyMMdd", "yy-MM-dd") );
					result.addString("COURSE" 			,rs.getString("COURSE") );
					try {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4) );
					} catch(Throwable t) {
						result.addString("BKPS_TIME" 		,"");
					}
					result.addString("RESER_CODE" 		,rs.getString("RESER_CODE") );
					try {
						result.addString("REG_DATE"			,DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd") );
					} catch(Throwable t) {
						result.addString("REG_DATE"			,"" );
					}
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
					result.addString("RESULT", "00"); //정상결과
					
					art_num_no++;
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
	public DbTaoResult topGolfConfView(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			// 회원통합테이블 관련 수정사항 진행
			//조회 ----------------------------------------------------------
			

			String sql = this.getConfViewSQL();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("idx"));					
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addString("BKPS_DATE",DateUtil.format(rs.getString("BKPS_DATE"),"yyyyMMdd","yyyy-MM-dd"));										
					result.addString("GR_NM",rs.getString("GR_NM"));	
					result.addString("COURSE",rs.getString("COURSE"));
					try {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4) );
					} catch(Throwable t) {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME"));
					}
					result.addString("JOINCNT",rs.getString("JOINCNT"));
					result.addString("ESTM_PROC_CMPL_YN",rs.getString("ESTM_PROC_CMPL_YN"));
					result.addString("STATUS_NM",rs.getString("STATUS_NM"));
					result.addString("JOINCNT",rs.getString("JOINCNT"));
					
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
	 private String getConfViewSQL( ){
	     StringBuffer sql = new StringBuffer();
		
	     	sql.append("\n SELECT 	");
	 		sql.append("\n  T1.RSVT_ABLE_BOKG_TIME_SEQ_NO  	");
	 		sql.append("\n 	, (CASE WHEN T1.EPS_YN='Y' THEN '노출' ELSE '비노출' END) VIEW_YN	");
	 		sql.append("\n 	, (CASE WHEN T1.ESTM_PROC_CMPL_YN='Y' THEN '평가완료' ELSE '미처리' END) ESTM_PROC_CMPL_YN	");
	 		sql.append("\n 	, (CASE WHEN T1.EPS_YN='Y' THEN '신청가능' WHEN T1.EPS_YN='S' THEN '신청마감' ELSE '부킹확정' END) STATUS_NM	");
	 		sql.append("\n 	, T3.GREEN_NM AS GR_NM	");
	 		sql.append("\n 	, T2.BOKG_ABLE_DATE AS BKPS_DATE 	");
	 		sql.append("\n 	, T2.GOLF_RSVT_CURS_NM AS COURSE	");
	 		sql.append("\n 	, T1.BOKG_ABLE_TIME AS BKPS_TIME 	");					
	 		sql.append("\n 	, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '부킹대기' ELSE '부킹확정' END) RESER_CODE	");
	 		sql.append("\n 	, ( SELECT COUNT(GOLF_LESN_RSVT_NO) FROM BCDBA.TBGAPLCMGMT WHERE GOLF_LESN_RSVT_NO = TO_CHAR(T1.RSVT_ABLE_BOKG_TIME_SEQ_NO) ) AS JOINCNT 	");	
	 		sql.append("\n 	, T2.REG_ATON AS REG_DATE	");
	 		sql.append("\n 			FROM 	");
	 		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
	 		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
	 		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T2.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	");
	 		sql.append("\n 			WHERE T2.GOLF_RSVT_CURS_NM IS NOT NULL AND T2.BOKG_ABLE_DATE IS NOT NULL  	");		
	 		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='T'	");
	 		sql.append("\n 			AND T3.AFFI_FIRM_CLSS = '1000'	");
	 		sql.append("\n 			AND T1.RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	");
		return sql.toString();
	 }
	
 	/**
 	 * Proc 실행.
 	 * @param Connection con
 	 * @param TaoDataSet dataSet
 	 * @return TaoResult
 	 */
 	public DbTaoResult getConfListDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

 		String title = data.getString("TITLE");
 		ResultSet rs = null;
 		Connection conn = null;
 		PreparedStatement pstmt = null;
 		DbTaoResult  result =  new DbTaoResult(title);

 		try {
 			conn = context.getDbConnection("default", null);			
 			 
 			//조회 ----------------------------------------------------------
 			//String sort = data.getString("sort");	
 			String seq 	= data.getString("seq");	

 			// 입력값 (INPUT)         
 			
 			
 			String sql = this.getConfListSelectQuery();   
 			int pidx = 0;
 			pstmt = conn.prepareStatement(sql.toString()); 				
 			pstmt.setString(++pidx, seq);	 			 			
 			rs = pstmt.executeQuery();

 			if(rs != null) {			 

 				while(rs.next())  {	
 					 					 					
 					result.addString("bkngEvalClss"		,rs.getString("bkng_eval_clss"));
 					result.addString("bkngDetlevalClss", rs.getString("bkng_detleval_clss"));
 					result.addString("bkngEvalMemo", rs.getString("bkng_eval_memo"));
 					result.addLong("evalScore", rs.getLong("eval_score"));
 					result.addLong("evalApplyPsnt", rs.getLong("eval_apply_psnt"));
 										
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
	private String getConfListSelectQuery() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT DISTINCT a.bkng_eval_clss, a.bkng_detleval_clss, a.bkng_eval_memo, a.eval_score, a.eval_apply_psnt ").append("\n");
		sql.append("FROM bcdba.tbgfbkevlscore a ").append("\n");
		sql.append("WHERE a.bkng_req_no = ? ").append("\n");
		sql.append("ORDER BY bkng_eval_clss ").append("\n");
        return sql.toString();
    }
 	/**
 	 * Proc 실행.
 	 * @param Connection con
 	 * @param TaoDataSet dataSet
 	 * @return TaoResult
 	 */
 	public DbTaoResult getConfSumDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

 		String title = data.getString("TITLE");
 		ResultSet rs = null;
 		Connection conn = null;
 		PreparedStatement pstmt = null;
 		DbTaoResult  result =  new DbTaoResult(title);

 		try {
 			conn = context.getDbConnection("default", null);			
 			 
 			//조회 ----------------------------------------------------------
 			//String sort = data.getString("sort");	
 			String seq 	= data.getString("seq");	

 			// 입력값 (INPUT)         
 			
 			
 			String sql = this.getConfSumSelectQuery();   
 			int pidx = 0;
 			pstmt = conn.prepareStatement(sql.toString()); 				
 			pstmt.setString(++pidx, seq);	 			 			
 			rs = pstmt.executeQuery();

 			if(rs != null) {			 

 				while(rs.next())  {	
 					 					
 					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
 					result.addString("CO_NM" 			,rs.getString("CO_NM") );
 					
 					result.addString("TEOF_DATE"		,DateUtil.format(rs.getString("TEOF_DATE"), "yyyyMMdd", "yyyy.MM.dd") );
					try {
						result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME").substring(0,2)+":"+rs.getString("TEOF_TIME").substring(2,4) );
					} catch(Throwable t) {
						result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME"));
					}
 					
 					result.addString("GREEN_NM" 		,rs.getString("GREEN_NM") );
 					result.addString("STTL_AMT" 		,rs.getString("STTL_AMT") ); 													
 										
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
     private String getConfSumSelectQuery() throws Exception{
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
         sql.append("SELECT a.CDHD_ID, a.CO_NM, a.TEOF_DATE, a.TEOF_TIME, a.GREEN_NM, a.STTL_AMT ").append("\n");
         sql.append("FROM BCDBA.TBGAPLCMGMT a ").append("\n");
         sql.append("WHERE a.APLC_SEQ_NO = ? ").append("\n");
  
         return sql.toString();
     }
    /** 점수 받아오기 시작**/
        
    public DbTaoResult getConfProc(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet, String[] bkngObjNoArr) throws BaseException {
        	
            String message_key= "CommonProc_0000";
            PreparedStatement pstmt = null;
            ResultSet rset = null;
            String title = dataSet.getString("TITLE");
            DbTaoResult  result =  new DbTaoResult(title);
            Connection con = null;
            GolfAdminEtt userEtt = null;
    		
           // PreparedStatement pstmtBasic = null;//이용실적반영기준조회용
            PreparedStatement pstmtPre = null;//대상조회용
            PreparedStatement pstmtPst = null;//대상업데이트용
            PreparedStatement pstmtBkng = null;//부킹정보 테이블 업데이트용
            PreparedStatement pstmtTrgt = null;//부킹대상정보 테이블 업데이트용

           // ResultSet rsetBasic = null;
            ResultSet rsetPre = null;

    		PreparedStatement pstmtpurc = null;
            ResultSet rsetpurc = null;
            
            
            try{
            	con = context.getDbConnection("default", null);
            	
            	
            	//long[] bkngObjNoArr = (long[])dataSet.getObject("arr_seq_no");
            	
            	if (bkngObjNoArr != null){
            		
            		
            		HttpSession session = request.getSession(true);
        			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
        			String regAdminNo = userEtt.getMemNo();
        			//String admId = userEtt.getMemId();
        			String regAdminNm = userEtt.getMemNm();
        			String regIp = dataSet.getString("regIp");
                	
        		    //이용실적반영기준----------------------------------------------------
        			String currMonth = DateUtil.currdate("yyyyMM");
        			String appliedMonth = currMonth;
        			String basicMonth = currMonth;			

        			String sqlBasic = this.getSelectQueryBasicMonth();//이용금액
        			pstmt = con.prepareStatement(sqlBasic);
        			rset = pstmt.executeQuery();
        			if(rset.next()){
        				appliedMonth = rset.getString("applied_month");
        				debug("@@@@@appliedMonth"+appliedMonth);
        			}
        			
        			basicMonth = DateUtil.dateAdd('M', -5, appliedMonth, "yyyyMM");

        			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ currMonth:" + currMonth);
        			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ appliedMonth:" + appliedMonth);
        			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ basicMonth:" + basicMonth);
        			if(rset  != null) rset.close();
                    if(pstmt != null) pstmt.close();
                    
                	
                	
                    //부킹정보 테이블 쿼리-------------------------------------------------	
                    String sqlPre = this.getSelectPreQuery();
        			con.setAutoCommit(false);
        			pstmtPre = con.prepareStatement(sqlPre);
        			
        			String sqlpurc = this.getSelectPreQueryPurc();
        			pstmtpurc = con.prepareStatement(sqlpurc);
        			
        			String sqlTrgt = this.getUpdatePreQuery2();//부킹대상정보 테이블 업데이트 쿼리
        			pstmtTrgt = con.prepareStatement(sqlTrgt);
            		
            		
        			for(int i=0; bkngObjNoArr.length>0 && i<bkngObjNoArr.length; i++)
        			{	
        			
        				int pidx = 0;
        				pstmtPre.setString(++pidx, bkngObjNoArr[i]);
        				rsetPre = pstmtPre.executeQuery();
        			
        				String sqlPst = this.getInsertPreQuery();//부킹평가점수내역 테이블 업데이트 쿼리
        				pstmtPst = con.prepareStatement(sqlPst);

        				String sqlBkng = this.getUpdatePreQuery();//부킹정보 테이블 업데이트 쿼리
        				pstmtBkng = con.prepareStatement(sqlBkng);
        			
        				
        				
        				while(rsetPre.next()){
        					        					        					
        					String account 		= rsetPre.getString("ACCOUNT");
        					long memId 			= rsetPre.getLong("MEMID");
        					String memSocId 	= rsetPre.getString("socid");
        					String roundDate 	= rsetPre.getString("TEOF_DATE");
        					long  bkngReqNo		= rsetPre.getLong("APLC_SEQ_NO");
        					long  bkngObjNo		= rsetPre.getLong("GOLF_LESN_RSVT_NO");
        					String memberClss 	= rsetPre.getString("member_clss");        					        					
        					//String golfJoinDate = rsetPre.getString("DTL_ADDR");        					
        					
        					
        					String memberCard = "1";//개인
        					if("3".equals(memberClss) || "5".equals(memberClss)){//법인회원
        						pstmtpurc.setLong(1, memId);
        						rsetpurc = pstmtpurc.executeQuery();
        						if(rsetpurc.next()){
        							memberCard = "6".equals(rsetpurc.getString("mem_clss"))?"3":"2";//"지정":"공용"
        						}
        						if("2".equals(memberCard)){
        							memSocId = rsetpurc.getString("buz_no");
        						}
        					}
        					
        					
        					String bkngEvalClss = "";
        					String bkngDetlEvalClss = "";
        					long evalApplyPsnt = 0L;
        					String bkngEvalMemo = "";
        /***************************** 이하 마이골프의 부킹평가점수 산출 커스터마이징 **************************/
        					//카드이용실적
        					double useAmt = 0.0;					//이용금액
        					double evalScoreUseAmt = 0.0;			//평가점수(이용금액합계)
        					double useAmtApply = 0.0;				//반영율(이용금액)
        					double useAmtScore = 0.0;				//이용금액 평가점수
        					//신규가입
        					double monthsFromJoin = 0.0;			//신규가입 경과 월수
        					//double bookingCount = 0.0;				//부킹횟수
        					double evalScoreMonths = 0.0;			//평가점수(신규가입 경과 월수)
        					double monthsApply = 0.0;				//반영율(신규가입)
        					double monthsScore = 0.0;				//신규가입 평가점수

        					//부킹실적
        					double bookingCount6M = 0.0;			//최근 6개월 간 부킹횟수
        					double evalScoreBookingCount = 0.0;		//평가점수(최근 6개월 간 부킹횟수)
        					double bookingCountApply = 0.0;			//반영율(부킹실적)
        					double bookingCountScore = 0.0;			//부킹실적 평가점수
        					
        					//부킹취소횟수
        					double cancelCount6M = 0.0;				//최근 6개월 간 부킹취소횟수
        					double evalScoreCancelCount = 0.0;		//평가점수(최근 6개월 간 부킹취소횟수)
        					double cancelCountApply = 0.0;			//반영율(부킹취소횟수)
        					double cancelCountScore = 0.0;			//부킹취소횟수 평가점수

        					//사이트활동
        					double pointCountPlus6M = 0.0;			//최근 6개월 간 사이트활동(글쓰기)
        					double pointCountMinus6M = 0.0;			//최근 6개월 간 사이트활동(비방글쓰기)
        					double evalScorePointCountPlus = 0.0;	//평가점수(최근 6개월 간 사이트활동-글쓰기)
        					double evalScorePointCountMinus = 0.0;	//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
        					double pointCountApply = 0.0;			//반영율(사이트활동)
        					double pointCountScore = 0.0;			//사이트활동 평가점수
        					
        					//골프매너불량		
        					double evalScoreNoManner = 0.0;			//평가점수(최근 6개월 간 골프매너불량)
        					double noMannerApply = 0.0;				//반영율(골프매너불량)
        					double noMannerScore = 0.0;				//골프매너불량 평가점수

        					//평가점수 총합계
        					double evalScore = 0.0;
        					
        					//카드이용실적-------------------------------------------------------
        					String sql = this.getSelectQuery(dataSet);//이용금액
        					pstmt = con.prepareStatement(sql);
        					pidx = 0;
        					//pstmt.setString(++pidx, cardNo);
        					pstmt.setString(++pidx, memSocId);
        					pstmt.setString(++pidx, basicMonth);
        					pstmt.setString(++pidx, appliedMonth);
        					rset = pstmt.executeQuery();
        					if(rset.next()){
        						useAmt = rset.getDouble("use_amt");
        					}
        					if(rset  != null) rset.close();
        					if(pstmt != null) pstmt.close();
        					
        					bkngEvalMemo = "이용실적 반기 " + StrUtil.parseMoney(useAmt+"", ",") + "원";//

        					/*2005.05.03*/
        					if("3".equals(memberClss) || "5".equals(memberClss)){
        						useAmt = (double)useAmt/2.0;
        					}
        					/*2005.05.03*/
        					
        					
        					sql = this.getSelectQuery2();//평가점수(이용금액합계)
        					pstmt = con.prepareStatement(sql);
        					pidx = 0;
        					pstmt.setDouble(++pidx, useAmt);
        					pstmt.setDouble(++pidx, useAmt);
        					rset = pstmt.executeQuery();
        					if(rset.next()){
        						evalScoreUseAmt = rset.getDouble("eval_score");
        						useAmtApply = rset.getDouble("eval_apply_psnt");

        						bkngEvalClss = rset.getString("bkng_eval_clss");
        						bkngDetlEvalClss = rset.getString("bkng_detleval_clss");
        						evalApplyPsnt = rset.getLong("eval_apply_psnt2");
        					}
        					if(rset  != null) rset.close();
        					if(pstmt != null) pstmt.close();

        					useAmtScore = useAmtApply * evalScoreUseAmt;//이용금액 평가점수
        					debug("useAmtScore(" + useAmtScore + ") = useAmtApply(" + useAmtApply + ") * evalScoreUseAmt(" + evalScoreUseAmt + ")");
        					debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);
        					
        					
        					//부킹평가점수내역 테이블 업데이트-------------------------------------------------					
        					pidx = 0;
        					pstmtPst.setLong(++pidx, memId);
        					pstmtPst.setString(++pidx, bkngEvalClss);
        					pstmtPst.setLong(++pidx, bkngReqNo);
        					pstmtPst.setLong(++pidx, bkngObjNo);

        					pstmtPst.setString(++pidx, roundDate);
        					pstmtPst.setString(++pidx, bkngDetlEvalClss);
        					pstmtPst.setDouble(++pidx, evalScoreUseAmt);
        					pstmtPst.setLong(++pidx, evalApplyPsnt);
        					pstmtPst.setString(++pidx, bkngEvalMemo);

        					pstmtPst.setString(++pidx, regAdminNm);
        					pstmtPst.setString(++pidx, regIp);
        					pstmtPst.setString(++pidx, regAdminNo);
        					pstmtPst.setLong(++pidx, memId);
        					int updResult = pstmtPst.executeUpdate();
        					if(updResult<1){
        						message_key = "CommonProc_0000";
        						throw new Exception(message_key);
        					}

        					bkngEvalClss = "";
        					bkngDetlEvalClss = "";
        					evalApplyPsnt = 0L;
        					
        //신규가입-----------------------------------------------------------
        					/*2005.05.03
        					sql = this.getSelectQuery3();//부킹횟수
        					pstmt = con.prepareStatement(sql);
        					pidx = 0;
        					pstmt.setLong(++pidx, memId);
        					rset = pstmt.executeQuery();
        					if(rset.next()){
        						bookingCount = rset.getDouble("booking_count");
        					}
        					if(rset  != null) rset.close();
        					if(pstmt != null) pstmt.close();

        					if(bookingCount==0L){
        						sql = this.getSelectQuery4();//신규가입 경과 월수
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setString(++pidx, roundDate);
        						pstmt.setString(++pidx, golfJoinDate);
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							monthsFromJoin = rset.getDouble("passed_months");
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();
        					*/
        						sql = this.getSelectQuery5();//평가점수(신규가입 경과 월수)
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setDouble(++pidx, monthsFromJoin);
        						pstmt.setDouble(++pidx, monthsFromJoin);
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							evalScoreMonths = rset.getDouble("eval_score");
        							monthsApply = rset.getDouble("eval_apply_psnt");
        							
        							bkngEvalClss = rset.getString("bkng_eval_clss");
        							bkngDetlEvalClss = rset.getString("bkng_detleval_clss");
        							evalApplyPsnt = rset.getLong("eval_apply_psnt2");
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();

        						bkngEvalMemo = "골프카드 회원가입 기본 점수";
        					
        					
        						//부킹평가점수내역 테이블 업데이트-------------------------------------------------
        						pidx = 0;
        						pstmtPst.setLong(++pidx, memId);
        						pstmtPst.setString(++pidx, bkngEvalClss);
        						pstmtPst.setLong(++pidx, bkngReqNo);
        						pstmtPst.setLong(++pidx, bkngObjNo);

        						pstmtPst.setString(++pidx, roundDate);
        						pstmtPst.setString(++pidx, bkngDetlEvalClss);
        						pstmtPst.setDouble(++pidx, evalScoreMonths);
        						pstmtPst.setLong(++pidx, evalApplyPsnt);
        						pstmtPst.setString(++pidx, bkngEvalMemo);

        						pstmtPst.setString(++pidx, regAdminNm);
        						pstmtPst.setString(++pidx, regIp);
        						pstmtPst.setString(++pidx, regAdminNo);
        						pstmtPst.setLong(++pidx, memId);
        						updResult = pstmtPst.executeUpdate();
        						if(updResult<1){
        							message_key = "CommonProc_0000";
        							throw new Exception(message_key);
        						}

        						monthsScore = monthsApply * evalScoreMonths;//신규가입 평가점수
        						
        						
        						debug("monthsScore(" + monthsScore + ") = monthsApply(" + monthsApply + ") * evalScoreMonths(" + evalScoreMonths + ")");
        						debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);

        						bkngEvalClss = "";
        						bkngDetlEvalClss = "";
        						evalApplyPsnt = 0L;
        						
        	//부킹실적-----------------------------------------------------------
        						sql = this.getSelectQuery6();//최근 6개월 간 부킹횟수
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setLong(++pidx, memId);
        						pstmt.setString(++pidx, account);
        						rset = pstmt.executeQuery();

        						int simpleBookingCount = 0;
        						while(rset.next()){
        							bookingCount6M = rset.getDouble("booking_count");
        							double singleBookingScore = this.getBookingScore(rset.getString("green_no"), rset.getString("bkng_obj_clss"));
        							evalScoreBookingCount += bookingCount6M * singleBookingScore;
        							simpleBookingCount += bookingCount6M;
        							debug("evalScoreBookingCount=" + evalScoreBookingCount);
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();
        						
        						
        						sql = this.getSelectQuery7();//평가점수(최근 6개월 간 부킹횟수)
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						/*2005.05.03
        						pstmt.setDouble(++pidx, bookingCount6M);
        						pstmt.setDouble(++pidx, bookingCount6M);
        						*/
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							/*2005.05.03
        							evalScoreBookingCount = rset.getDouble("eval_score");
        							*/
        							bookingCountApply = rset.getDouble("eval_apply_psnt");

        							bkngEvalClss = "30";//rset.getString("bkng_eval_clss");
        							bkngDetlEvalClss = "";//rset.getString("bkng_detleval_clss");
        							evalApplyPsnt = rset.getLong("eval_apply_psnt")*100L;
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();

        						bookingCountScore = bookingCountApply * evalScoreBookingCount;//부킹실적 평가점수
        						debug("bookingCountScore(" + bookingCountScore + ") = bookingCountApply(" + bookingCountApply + ") * evalScoreBookingCount(" + evalScoreBookingCount + ")");
        						debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);

        						bkngEvalMemo = "부킹 횟수 반기 " + (int)simpleBookingCount + "회";
        						
        						//부킹평가점수내역 테이블 업데이트-------------------------------------------------
        						pidx = 0;
        						pstmtPst.setLong(++pidx, memId);
        						pstmtPst.setString(++pidx, bkngEvalClss);
        						pstmtPst.setLong(++pidx, bkngReqNo);
        						pstmtPst.setLong(++pidx, bkngObjNo);

        						pstmtPst.setString(++pidx, roundDate);
        						pstmtPst.setString(++pidx, bkngDetlEvalClss);
        						pstmtPst.setDouble(++pidx, evalScoreBookingCount);
        						pstmtPst.setLong(++pidx, evalApplyPsnt);
        						pstmtPst.setString(++pidx, bkngEvalMemo);

        						pstmtPst.setString(++pidx, regAdminNm);
        						pstmtPst.setString(++pidx, regIp);
        						pstmtPst.setString(++pidx, regAdminNo);
        						pstmtPst.setLong(++pidx, memId);
        						updResult = pstmtPst.executeUpdate();
        						if(updResult<1){
        							message_key = "CommonProc_0000";
        							throw new Exception(message_key);
        						}

        						bkngEvalClss = "";
        						bkngDetlEvalClss = "";
        						evalApplyPsnt = 0L;
        					
        						//부킹취소횟수-----------------------------------------------------------
        						sql = this.getSelectQuery8();//최근 6개월 간 부킹취소횟수
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setLong(++pidx, memId);
        						pstmt.setString(++pidx, account);
        						rset = pstmt.executeQuery();
        						
        						int simpleCancelCount = 0;
        						while(rset.next()){
        							cancelCount6M = rset.getDouble("booking_count");
        							double singleCancelScore = this.getCancelScore(rset.getString("green_no"), rset.getString("bkng_obj_clss"));
        							evalScoreCancelCount += cancelCount6M * singleCancelScore;
        							simpleCancelCount += cancelCount6M;
        							debug("evalScoreCancelCount=" + evalScoreCancelCount);
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();

        						sql = this.getSelectQuery9();//평가점수(최근 6개월 간 부킹취소횟수)
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						/*2005.05.03
        						pstmt.setDouble(++pidx, cancelCount6M);
        						pstmt.setDouble(++pidx, cancelCount6M);
        						*/
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							/*2005.05.03
        							evalScoreCancelCount = rset.getDouble("eval_score");
        							*/
        							cancelCountApply = rset.getDouble("eval_apply_psnt");

        							bkngEvalClss = "40";//rset.getString("bkng_eval_clss");
        							bkngDetlEvalClss = "";//rset.getString("bkng_detleval_clss");
        							evalApplyPsnt = rset.getLong("eval_apply_psnt") * 100L;
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();

        						cancelCountScore = cancelCountApply * evalScoreCancelCount;//부킹취소횟수 평가점수
        						debug("cancelCountScore(" + cancelCountScore + ") = cancelCountApply(" + cancelCountApply + ") * evalScoreCancelCount(" + evalScoreCancelCount + ")");
        						debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);

        						bkngEvalMemo = "부킹 취소 횟수 반기 " + (int)simpleCancelCount + "회";
        						
        						
        						//부킹평가점수내역 테이블 업데이트-------------------------------------------------
        						pidx = 0;
        						pstmtPst.setLong(++pidx, memId);
        						pstmtPst.setString(++pidx, bkngEvalClss);
        						pstmtPst.setLong(++pidx, bkngReqNo);
        						pstmtPst.setLong(++pidx, bkngObjNo);

        						pstmtPst.setString(++pidx, roundDate);
        						pstmtPst.setString(++pidx, bkngDetlEvalClss);
        						pstmtPst.setDouble(++pidx, evalScoreCancelCount);
        						pstmtPst.setLong(++pidx, evalApplyPsnt);
        						pstmtPst.setString(++pidx, bkngEvalMemo);

        						pstmtPst.setString(++pidx, regAdminNm);
        						pstmtPst.setString(++pidx, regIp);
        						pstmtPst.setString(++pidx, regAdminNo);
        						pstmtPst.setLong(++pidx, memId);
        						updResult = pstmtPst.executeUpdate();
        						if(updResult<1){
        							message_key = "CommonProc_0000";
        							throw new Exception(message_key);
        						}

        						bkngEvalClss = "";
        						bkngDetlEvalClss = "";
        						evalApplyPsnt = 0L;
        						
        						
        						
        						//사이트활동
        						//최근 6개월 간 사이트활동(글쓰기)
        						sql = this.getSelectQuery10();
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setLong(++pidx, memId);
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							pointCountPlus6M = rset.getDouble("point_count");
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();
        						
        						
        						//평가점수(최근 6개월 간 사이트활동-글쓰기)
        						sql = this.getSelectQuery11();
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setDouble(++pidx, pointCountPlus6M);
        						pstmt.setDouble(++pidx, pointCountPlus6M);
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							/*2005.05.03
        							evalScorePointCountPlus = rset.getDouble("eval_score");
        							*/
        							bkngEvalClss = rset.getString("bkng_eval_clss");
        							bkngDetlEvalClss = rset.getString("bkng_detleval_clss");
        							evalApplyPsnt = rset.getLong("eval_apply_psnt2");
        							
        							//반영율 추가 (점수 반영 오류 해결) 2007.04.05
        							pointCountApply = rset.getLong("eval_apply_psnt2");
        							
        							evalScorePointCountPlus = pointCountPlus6M * rset.getDouble("eval_score");
        							//점수 반영율을 100%일때 100으로 나누도록 수정(2007.04.06)
        							evalScorePointCountPlus = (pointCountApply/100.0) * evalScorePointCountPlus;
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();

        						debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);

        						bkngEvalMemo = "라운드후기/글쓰기 추천글 반기 " + (int)pointCountPlus6M + "건";
        						
        						//부킹평가점수내역 테이블 업데이트-------------------------------------------------
        						pidx = 0;
        						pstmtPst.setLong(++pidx, memId);
        						pstmtPst.setString(++pidx, bkngEvalClss);
        						pstmtPst.setLong(++pidx, bkngReqNo);
        						pstmtPst.setLong(++pidx, bkngObjNo);

        						pstmtPst.setString(++pidx, roundDate);
        						pstmtPst.setString(++pidx, bkngDetlEvalClss);
        						pstmtPst.setDouble(++pidx, evalScorePointCountPlus);
        						pstmtPst.setLong(++pidx, evalApplyPsnt);
        						pstmtPst.setString(++pidx, bkngEvalMemo);

        						pstmtPst.setString(++pidx, regAdminNm);
        						pstmtPst.setString(++pidx, regIp);
        						pstmtPst.setString(++pidx, regAdminNo);
        						pstmtPst.setLong(++pidx, memId);
        						updResult = pstmtPst.executeUpdate();
        						if(updResult<1){
        							message_key = "CommonProc_0000";
        							throw new Exception(message_key);
        						}

        						bkngEvalClss = "";
        						bkngDetlEvalClss = "";
        						evalApplyPsnt = 0L;
        						
        						
        						
        						//최근 6개월 간 사이트활동(비방글쓰기)
        						sql = this.getSelectQuery12();
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setLong(++pidx, memId);
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							pointCountMinus6M = rset.getDouble("point_count");
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();
        						

        						
        						//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
        						sql = this.getSelectQuery13();
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setDouble(++pidx, pointCountMinus6M);
        						pstmt.setDouble(++pidx, pointCountMinus6M);
        						rset = pstmt.executeQuery();
        						if(rset.next()){
        							//2005.05.03
        							//evalScorePointCountMinus = rset.getDouble("eval_score");
        							evalScorePointCountMinus = pointCountMinus6M * rset.getDouble("eval_score");
        							//점수 반영율을 100%일때 100으로 나누도록 수정(2007.04.06)
        							evalScorePointCountMinus = (pointCountApply/100.0) * evalScorePointCountMinus;
        							pointCountApply = rset.getDouble("eval_apply_psnt");

        							bkngEvalClss = rset.getString("bkng_eval_clss");
        							bkngDetlEvalClss = rset.getString("bkng_detleval_clss");
        							evalApplyPsnt = rset.getLong("eval_apply_psnt2");
        						}
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();

        						bkngEvalMemo = "비방 글쓰기 반기 " + (int)pointCountMinus6M + "건";

        						
        						
        						//부킹평가점수내역 테이블 업데이트(비방글쓰기는 있는경우에만 INSERT <= 위에 사이트활동 항목에 글쓰기가 반드시 들어가므로 ...)-------------------------------------------------
        						pidx = 0;
        						pstmtPst.setLong(++pidx, memId);
        						pstmtPst.setString(++pidx, bkngEvalClss);
        						pstmtPst.setLong(++pidx, bkngReqNo);
        						pstmtPst.setLong(++pidx, bkngObjNo);

        						pstmtPst.setString(++pidx, roundDate);
        						pstmtPst.setString(++pidx, bkngDetlEvalClss);
        						pstmtPst.setDouble(++pidx, evalScorePointCountMinus);
        						pstmtPst.setLong(++pidx, evalApplyPsnt);
        						pstmtPst.setString(++pidx, bkngEvalMemo);

        						pstmtPst.setString(++pidx, regAdminNm);
        						pstmtPst.setString(++pidx, regIp);
        						pstmtPst.setString(++pidx, regAdminNo);
        						pstmtPst.setLong(++pidx, memId);
        						updResult = pstmtPst.executeUpdate();
        						if(updResult<1){
        							message_key = "CommonProc_0000";
        							throw new Exception(message_key);
        						}

        						pointCountScore = evalScorePointCountPlus + evalScorePointCountMinus;//사이트활동 평가점수
        						debug("pointCountScore(" + pointCountScore + ") = evalScorePointCountPlus(" + evalScorePointCountPlus + ")"	+ " + evalScorePointCountMinus(" + evalScorePointCountMinus + ")");
        						debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);

        						bkngEvalClss = "";
        						bkngDetlEvalClss = "";
        						evalApplyPsnt = 0L;
        					
        						//골프매너불량
        						sql = this.getSelectQuery14();//평가점수(최근 6개월 간 골프매너불량)
        						pstmt = con.prepareStatement(sql);
        						pidx = 0;
        						pstmt.setLong(++pidx, memId);
        						rset = pstmt.executeQuery();
        						
        						boolean haseData = false;
        						while(rset.next()){
        							haseData = true;
        							evalScoreNoManner = rset.getDouble("eval_score");
        							noMannerApply = rset.getDouble("eval_apply_psnt");

        							bkngEvalClss = rset.getString("bkng_eval_clss");
        							bkngDetlEvalClss = rset.getString("bkng_detleval_clss");
        							evalApplyPsnt = rset.getLong("eval_apply_psnt2");		
        							
        							bkngEvalMemo = rset.getString("bkng_eval_memo");

        							//부킹평가점수내역 테이블 업데이트
        							pidx = 0;
        							pstmtPst.setLong(++pidx, memId);
        							pstmtPst.setString(++pidx, bkngEvalClss);
        							pstmtPst.setLong(++pidx, bkngReqNo);
        							pstmtPst.setLong(++pidx, bkngObjNo);

        							pstmtPst.setString(++pidx, roundDate);
        							pstmtPst.setString(++pidx, bkngDetlEvalClss);
        							pstmtPst.setDouble(++pidx, evalScoreNoManner);
        							pstmtPst.setLong(++pidx, evalApplyPsnt);
        							pstmtPst.setString(++pidx, bkngEvalMemo);

        							pstmtPst.setString(++pidx, regAdminNm);
        							pstmtPst.setString(++pidx, regIp);
        							pstmtPst.setString(++pidx, regAdminNo);
        							pstmtPst.setLong(++pidx, memId);
        							updResult = pstmtPst.executeUpdate();
        							if(updResult<1){
        								message_key = "CommonProc_0000";
        								throw new Exception(message_key);
        							}

        							noMannerScore += noMannerApply * evalScoreNoManner;//골프매너불량 평가점수
        							debug("noMannerScore(" + noMannerScore + ") += noMannerApply(" + noMannerApply + ") * evalScoreNoManner(" + evalScoreNoManner + ")");
        							debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);
        						}
        						
        						if(!haseData){
        							if(rset  != null) rset.close();
        							if(pstmt != null) pstmt.close();

        							sql = this.getSelectQuery15();//평가점수(최근 6개월 간 골프매너불량 없는 경우)
        							pstmt = con.prepareStatement(sql);
        							pidx = 0;
        							pstmt.setLong(++pidx, memId);
        							rset = pstmt.executeQuery();

        							if(rset.next()){
        								evalScoreNoManner = rset.getDouble("eval_score");
        								noMannerApply = rset.getDouble("eval_apply_psnt");

        								bkngEvalClss = rset.getString("bkng_eval_clss");
        								bkngDetlEvalClss = rset.getString("bkng_detleval_clss");
        								evalApplyPsnt = rset.getLong("eval_apply_psnt2");
        							}

        							bkngEvalMemo = rset.getString("bkng_eval_memo");
        							
        							//부킹평가점수내역 테이블 업데이트
        							pidx = 0;
        							pstmtPst.setLong(++pidx, memId);
        							pstmtPst.setString(++pidx, bkngEvalClss);
        							pstmtPst.setLong(++pidx, bkngReqNo);
        							pstmtPst.setLong(++pidx, bkngObjNo);

        							pstmtPst.setString(++pidx, roundDate);
        							pstmtPst.setString(++pidx, bkngDetlEvalClss);
        							pstmtPst.setDouble(++pidx, evalScoreNoManner);
        							pstmtPst.setLong(++pidx, evalApplyPsnt);
        							pstmtPst.setString(++pidx, bkngEvalMemo);

        							pstmtPst.setString(++pidx, regAdminNm);
        							pstmtPst.setString(++pidx, regIp);
        							pstmtPst.setString(++pidx, regAdminNo);
        							pstmtPst.setLong(++pidx, memId);
        							updResult = pstmtPst.executeUpdate();
        							if(updResult<1){
        								message_key = "CommonProc_0000";
        								throw new Exception(message_key);
        							}

        							noMannerScore = noMannerApply * evalScoreNoManner;//골프매너불량 평가점수
        							debug("noMannerScore(" + noMannerScore + ") = noMannerApply(" + noMannerApply + ") * evalScoreNoManner(" + evalScoreNoManner + ")");
        							debug("bkngEvalClss:" + bkngEvalClss + "|bkngDetlEvalClss:" + bkngDetlEvalClss + "|evalApplyPsnt:" + evalApplyPsnt);
        						}
        						
        						if(rset  != null) rset.close();
        						if(pstmt != null) pstmt.close();
        						
        						
        						//평가점수 총합계
        						evalScore += useAmtScore;
        						evalScore += monthsScore;
        						evalScore += bookingCountScore;
        						evalScore += cancelCountScore;
        						evalScore += pointCountScore;
        						evalScore += noMannerScore;
        				
        	/***************************** 이상 마이골프의 부킹평가점수 산출 커스터마이징 **************************/
        						pidx = 0;
        						pstmtBkng.setDouble(++pidx, evalScore);
        						//pstmtBkng.setString(++pidx, roundDate);
        						pstmtBkng.setLong(++pidx, bkngReqNo);

        						updResult = pstmtBkng.executeUpdate();
        						if(updResult<1){
        							message_key = "CommonProc_0000";
        							throw new Exception(message_key);
        						}
        						
        						        						        						    						
        						
        						
        						
        						
        					
        				} //while문끝
        				pidx = 0;
        				pstmtTrgt.setString(++pidx, bkngObjNoArr[i]);

        				int updResult = pstmtTrgt.executeUpdate();
        				if(updResult<1){
        					message_key = "CommonProc_0000";
        					throw new Exception(message_key);
        				}
        			
        				
        				
        				
        				result.addString("RESULT","00");

        				con.commit();
        			
        			}
        			
            		
            	}
            	else
    			{
    				result.addString("RESULT","01");
    			}
            	
            	
            	
            	
            	
            	
            	
            	
            	
            	

            }catch(Exception e){
                MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, message_key, null );
                throw new DbTaoException(msgEtt,e);
            }finally{
                try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
                try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
                try{ if(con != null) con.close(); }catch( Exception ignored){}
            }
            return result;
        }
    
	//골프매너불량 없는 경우
	//최근 6개월 간 골프매너불량
    private String getSelectQuery15(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");
		sql.append("	, b.bkng_eval_clss, a.bkng_detleval_clss, b.eval_apply_psnt eval_apply_psnt2, a.bkng_eval_memo").append("\n");
		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE a.bkng_eval_clss = '60'").append("\n");//골프매너불량골프매너불량
		sql.append("AND a.bkng_detleval_clss = '6035'").append("\n");//골프매너불량골프매너불량 없음
		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND 0 = ( ").append("\n");
		sql.append("	SELECT count(*) point_count").append("\n");		
		sql.append("	FROM bcdba.tbgfpoint").append("\n");
		sql.append("	WHERE memid = ?").append("\n");
		sql.append("	AND point_clss = '60'").append("\n");//골프매너불량
		sql.append("	AND point_detl_cd IN ('6010', '6015', '6020', '6025', '6030') ").append("\n");//No Show,지각,복장불량,기타 수칙위반
		sql.append("	AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
		sql.append(") ").append("\n");
		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND b.set_to = a.set_to").append("\n");
		sql.append("AND b.set_from = a.set_from").append("\n");
		sql.append("ORDER BY a.bkng_detleval_clss ASC").append("\n");
		
        return sql.toString();
    }
    
	//부킹정보 테이블 업데이트
    private String getUpdatePreQuery() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAPLCMGMT SET ").append("\n");
		sql.append("	STTL_AMT = ? ").append("\n");
		sql.append("WHERE APLC_SEQ_NO = ? ").append("\n");
        return sql.toString();
    }
    
    
	//부킹평가점수내역 테이블 업데이트
    private String getInsertPreQuery() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO bcdba.tbgfbkevlscore ( ").append("\n");
		sql.append("	memid, seq_no, bkng_eval_clss, bkng_req_no, bkng_obj_no, ").append("\n");
		sql.append("	round_date, bkng_detleval_clss, eval_score, eval_apply_psnt, bkng_eval_memo, ").append("\n");
		sql.append("	reg_date, reg_admin_nm, reg_ip, reg_admin_no ").append("\n");
		sql.append(") ( ").append("\n");
		sql.append("	SELECT ?, NVL(MAX(seq_no), 0) + 1, ?, ?, ?, ").append("\n");
		sql.append("		?, ?, ?, ?, ?, ").append("\n");
		sql.append("		TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?, ? ").append("\n");
		sql.append("	FROM bcdba.tbgfbkevlscore ").append("\n");
		sql.append("	WHERE memid = ? ").append("\n");
		sql.append(") ").append("\n");
        return sql.toString();
    }
    
    
	//부킹대상정보 테이블 업데이트
    private String getUpdatePreQuery2() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET ").append("\n");
		sql.append("	ESTM_PROC_CMPL_YN = 'Y' ").append("\n");
		sql.append("WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ? ").append("\n");
        return sql.toString();
    }
    
    
    
    
    
    
    
    
	//법인DB의 법인회원정보 테이블 쿼리
    private String getSelectPreQueryPurc() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT b.mem_clss, b.buz_no ").append("\n");
		sql.append("FROM bcdba.tbentpuser a, bcdba.tbentpmem b").append("\n");
		sql.append("WHERE a.uc_mem_id = ? ").append("\n");
		sql.append("AND b.mem_id = a.mem_id ").append("\n");
        return sql.toString();
    }
    
  //부킹정보 테이블 쿼리
    private String getSelectPreQuery() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.APLC_SEQ_NO, a.GOLF_LESN_RSVT_NO, a.DTL_ADDR, a.TEOF_DATE, a.TEOF_TIME , b.member_clss, b.socid, b.account, b.MEMID  ").append("\n");
		sql.append("	 ").append("\n");
		sql.append("FROM BCDBA.TBGAPLCMGMT a, bcdba.ucusrinfo b").append("\n");
		sql.append("WHERE a.GOLF_LESN_RSVT_NO = ? ").append("\n");
		sql.append("AND GOLF_SVC_APLC_CLSS= '1000' ").append("\n");
		sql.append("AND b.account = a.CDHD_ID ").append("\n");
        return sql.toString();
    }
    
    
    //이용실적반영기준-------------------------------------------------------
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQueryBasicMonth() throws Exception{//이용금액
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT MAX(use_yyyymm) applied_month").append("\n");		
		sql.append("FROM bcdba.tbgfuseamt").append("\n");
        return sql.toString();
    }

//카드이용실적-------------------------------------------------------
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet dataSet) throws Exception{//이용금액
		//String cardNo = dataSet.getString("cardNo");
		//String memSocId = dataSet.getString("memSocId");
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(SUM(NVL(cntry_crdt_amt, 0) + NVL(cntry_cash_amt, 0) +  NVL(ovsea_crdt_amt, 0) +  NVL(ovsea_cash_amt, 0)), 0) use_amt").append("\n");		
		sql.append("FROM bcdba.tbgfuseamt").append("\n");
		sql.append("WHERE mem_soc_id = ? ").append("\n");
		//sql.append("AND use_yyyymm >= TO_CHAR(ADD_MONTHS(SYSDATE, -?), 'YYYYMM')").append("\n");
		sql.append("AND use_yyyymm >= ? ").append("\n");
		sql.append("AND use_yyyymm <= ? ").append("\n");
        return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery2(){//평가점수(이용금액합계)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");	
		sql.append("	, b.bkng_eval_clss, a.bkng_detleval_clss, b.eval_apply_psnt eval_apply_psnt2").append("\n");
		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE a.bkng_eval_clss = '10'").append("\n");//카드이용실적
		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//이용금액합계
		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//이용금액합계
		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND b.set_to = a.set_to").append("\n");
		sql.append("AND b.set_from = a.set_from").append("\n");
        return sql.toString();
    }	

    //부킹 확정 시 점수
	private double getBookingScore(String greenNo, String bkngObjClss){
		if("2".equals(bkngObjClss)) return 10.0;//비회원부킹
		double bookingScore = 0.0;//아래 조건 미등재 골프장
		if("319".equals(greenNo) || "크리스탈밸리".equals(greenNo)) bookingScore = -70.0;//크리스탈밸리
		if("154".equals(greenNo) || "".equals(greenNo)) bookingScore = -60.0;//양평TPC
		if("196".equals(greenNo) || "".equals(greenNo)) bookingScore = -50.0;//프리스틴밸리
		if("216".equals(greenNo) || "떼제베".equals(greenNo)) bookingScore = -50.0;//떼제베
		if("255".equals(greenNo) || "상떼힐".equals(greenNo)) bookingScore = -50.0;//상떼힐
		if("269".equals(greenNo) || "센추리21".equals(greenNo)) bookingScore = -40.0;//센츄리21
		if("176".equals(greenNo) || "캐슬파인".equals(greenNo)) bookingScore = -10.0;//캐슬파인
		if("114".equals(greenNo) || "강남300".equals(greenNo)) bookingScore = -10.0;//강남300
		if("211".equals(greenNo) || "썬힐".equals(greenNo)) bookingScore = 0.0;//썬힐
		if("239".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0;//마우나오션
		if("310".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0;//아크로	
		if("오펠".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0; //오펠
		if("광릉".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0; //광릉
		if("세이트포".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0; //세이트포
/*
* 세인트포,오펠,광릉
* */
		return bookingScore;
	}
//부킹 확정취소 시 점수
	private double getCancelScore(String greenNo, String bkngObjClss){
		if("2".equals(bkngObjClss)) return -10.0;//비회원부킹
		double cancelScore = 0.0;//아래 조건 미등재 골프장
		if("319".equals(greenNo) || "크리스탈밸리".equals(greenNo)) cancelScore = -10.0;//크리스탈밸리
		if("154".equals(greenNo) || "".equals(greenNo)) cancelScore = -10.0;//양평TPC
		if("196".equals(greenNo) || "".equals(greenNo)) cancelScore = -10.0;//프리스틴밸리
		if("216".equals(greenNo) || "떼제베".equals(greenNo)) cancelScore = -10.0;//떼제베
		if("255".equals(greenNo) || "상떼힐".equals(greenNo)) cancelScore = -10.0;//상떼힐
		if("269".equals(greenNo) || "센추리21".equals(greenNo)) cancelScore = -10.0;//센츄리21
		if("176".equals(greenNo) || "캐슬파인".equals(greenNo)) cancelScore = -10.0;//캐슬파인
		if("114".equals(greenNo) || "강남300".equals(greenNo)) cancelScore = -10.0;//강남300
		if("211".equals(greenNo) || "강남300".equals(greenNo)) cancelScore = -10.0;//썬힐
		if("239".equals(greenNo) || "썬힐".equals(greenNo)) cancelScore = -10.0;//마우나오션
		if("310".equals(greenNo) || "".equals(greenNo)) cancelScore = -10.0;//아크로
		if("오펠".equals(greenNo) || "".equals(greenNo)) cancelScore = 0.0; //오펠
		if("광릉".equals(greenNo) || "".equals(greenNo)) cancelScore = 0.0; //광릉
		if("세이트포".equals(greenNo) || "".equals(greenNo)) cancelScore = 0.0; //세이트포

		return cancelScore;
	}
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery5(){//평가점수(신규가입 경과 월수)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");
		sql.append("	, b.bkng_eval_clss, a.bkng_detleval_clss, b.eval_apply_psnt eval_apply_psnt2").append("\n");
		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE a.bkng_eval_clss = '20'").append("\n");//신규가입
		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//월수
		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//월수
		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND b.set_to = a.set_to").append("\n");
		sql.append("AND b.set_from = a.set_from").append("\n");
        return sql.toString();
    }
	
//부킹실적-----------------------------------------------------------
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery6(){//최근 6개월 간 부킹횟수
        StringBuffer sql = new StringBuffer();
        sql.append("\n");
        sql.append(" SELECT  COUNT(*) booking_count, green_no , bkng_obj_clss    \n");
        sql.append(" FROM   \n");
        sql.append(" (   \n");
        sql.append("     SELECT TO_CHAR(green_no) AS green_no , bkng_obj_clss  \n");
        sql.append("     FROM  bcdba.tbgfbooking  \n");
        sql.append("     WHERE memid =  ?  \n");
        sql.append("     AND bkng_stat = '31' \n");
        sql.append("     AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM') \n");
        
        sql.append("   UNION ALL");
        sql.append("     SELECT GREEN_NM AS green_no ,CDHD_NON_CDHD_CLSS AS bkng_obj_clss  \n");
        sql.append("     from BCDBA.TBGAPLCMGMT  \n");
        sql.append("     where teof_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')  \n");
        sql.append("     and  pgrs_yn = 'B' \n");
        sql.append("     AND GOLF_SVC_APLC_CLSS='1000' \n");
        sql.append("     AND cdhd_id =  ?  \n");
        sql.append(" )   \n");
        sql.append(" GROUP BY green_no, bkng_obj_clss   \n");
        return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getSelectQuery7(){//평가점수(부킹실적)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
		sql.append("FROM bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE b.bkng_eval_clss = '30'").append("\n");//부킹실적
		sql.append("AND b.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND b.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
        return sql.toString();
    }

//부킹취소횟수-----------------------------------------------------------
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery8(){//최근 6개월 간 부킹취소횟수
    	 StringBuffer sql = new StringBuffer();
         sql.append("\n");
         sql.append(" SELECT  COUNT(*) booking_count, green_no , bkng_obj_clss    \n");
         sql.append(" FROM   \n");
         sql.append(" (   \n");
         sql.append("     SELECT TO_CHAR(green_no) AS green_no , bkng_obj_clss  \n");
         sql.append("     FROM  bcdba.tbgfbooking  \n");
         sql.append("     WHERE memid =  ?  \n");
         sql.append("     AND bkng_stat = '39' \n");
         sql.append("     AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM') \n");
         sql.append("   UNION ALL");
         sql.append("     SELECT GREEN_NM AS green_no ,CDHD_NON_CDHD_CLSS AS bkng_obj_clss  \n");
         sql.append("     from BCDBA.TBGAPLCMGMT  \n");
         sql.append("     where teof_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')  \n");
         sql.append("     and  pgrs_yn = 'E' \n");
         sql.append("     AND GOLF_SVC_APLC_CLSS='1000' \n");
         sql.append("     AND cdhd_id =  ?  \n");
         sql.append(" )   \n");
         sql.append(" GROUP BY green_no, bkng_obj_clss   \n");
         return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getSelectQuery9(){//평가점수(부킹취소횟수)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
		sql.append("FROM bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE b.bkng_eval_clss = '40'").append("\n");//부킹취소
		sql.append("AND b.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND b.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");		
        return sql.toString();
    }

//사이트활동-----------------------------------------------------------
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery10(){//최근 6개월 간 사이트활동(글쓰기)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT count(*) point_count").append("\n");		
		sql.append("FROM bcdba.tbgfpoint").append("\n");
		sql.append("WHERE memid = ?").append("\n");
		sql.append("AND point_clss = '50'").append("\n");//사이트활동
		sql.append("AND point_detl_cd IN ('5020', '5025') ").append("\n");//글쓰기
		sql.append("AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
        return sql.toString();
    }	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery11(){//평가점수(최근 6개월 간 사이트활동-글쓰기)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");	
		sql.append("	, b.bkng_eval_clss, a.bkng_detleval_clss, b.eval_apply_psnt eval_apply_psnt2").append("\n");
		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE a.bkng_eval_clss = '50'").append("\n");//사이트활동
		sql.append("AND a.bkng_detleval_clss IN ('5020', '5025') ").append("\n");//글쓰기
		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//최근 6개월 간 사이트활동(글쓰기)
		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//최근 6개월 간 사이트활동(글쓰기)
		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND b.set_to = a.set_to").append("\n");
		sql.append("AND b.set_from = a.set_from").append("\n");
        return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery12(){//최근 6개월 간 사이트활동(비방글쓰기)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT count(*) point_count").append("\n");		
		sql.append("FROM bcdba.tbgfpoint").append("\n");
		sql.append("WHERE memid = ?").append("\n");
		sql.append("AND point_clss = '50'").append("\n");//사이트활동
		sql.append("AND point_detl_cd IN ('5030', '5035') ").append("\n");//비방글쓰기
		sql.append("AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
        return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery13(){//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");	
		sql.append("	, b.bkng_eval_clss, a.bkng_detleval_clss, b.eval_apply_psnt eval_apply_psnt2").append("\n");
		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
		sql.append("WHERE a.bkng_eval_clss = '50'").append("\n");//사이트활동
		sql.append("AND a.bkng_detleval_clss IN ('5030', '5035') ").append("\n");//비방글쓰기
		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//최근 6개월 간 사이트활동(비방글쓰기)
		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//최근 6개월 간 사이트활동(비방글쓰기)
		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND b.set_to = a.set_to").append("\n");
		sql.append("AND b.set_from = a.set_from").append("\n");
        return sql.toString();
    }

//골프매너불량-----------------------------------------------------------
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getSelectQuery14(){//최근 6개월 간 골프매너불량
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt, a.bkng_eval_memo").append("\n");
		sql.append("	, b.bkng_eval_clss, a.bkng_detleval_clss, b.eval_apply_psnt eval_apply_psnt2, a.bkng_eval_memo").append("\n");
		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b, bcdba.tbgfpoint c ").append("\n");
		sql.append("WHERE a.bkng_eval_clss = '60'").append("\n");//골프매너불량골프매너불량
		sql.append("AND a.bkng_detleval_clss IN ('6010', '6015', '6020', '6025', '6030')").append("\n");//No Show,지각,복장불량,기타 수칙위반
		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND b.set_to = a.set_to").append("\n");
		sql.append("AND b.set_from = a.set_from").append("\n");
		sql.append("AND c.memid = ?").append("\n");
		sql.append("AND c.point_clss = a.bkng_eval_clss").append("\n");
		sql.append("AND c.point_detl_cd = a.bkng_detleval_clss").append("\n");
		sql.append("AND c.round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
        return sql.toString();
    }

	//카드이용실적-------------------------------------------------------
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQueryStd() throws Exception{//이용금액
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT TO_CHAR(TO_DATE(MAX(use_yyyymm), 'YYYY.MM'), 'YYYY.MM') max_month, ").append("\n");
		sql.append("	TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYY.MM') six_month, ").append("\n");
		sql.append("	TO_CHAR(TO_DATE(?, 'YYYY.MM'), 'YYYY.MM') six_month2, ").append("\n");
		sql.append("	TO_CHAR(SYSDATE, 'YYYY.MM') this_month").append("\n");
		sql.append("FROM bcdba.tbgfuseamt").append("\n");
        return sql.toString();
    }
	
	
	/**
	 * 관리자 탑골프부킹대상관리 신청마감 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException 
	 */
	public int joinEndProc(WaContext context, HttpServletRequest request, TaoDataSet data , String[] arr_seq_no) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		GolfAdminEtt userEtt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			
			//조회 ----------------------------------------------------------		
			String sql = this.getJoinEndUpdateSQL();	
			
			if("joinEnd".equals(data.getString("modeType")))
			{
				for (int i = 0; i < arr_seq_no.length; i++) {				
					if (arr_seq_no[i] != null && arr_seq_no[i].length() > 0) {		
						
						String[] pudarry = arr_seq_no[i].split("\\|");						
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, admId);
						pstmt.setString(2, "S");
						pstmt.setString(3, pudarry[0]); 

						iCount += pstmt.executeUpdate();
												

					}
				}		
								
			}
			
			if(iCount == arr_seq_no.length) {
				conn.commit();
			} else {
				conn.rollback();
			}	
		
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return iCount;
	}
	
	/**
	 * 관리자 탑골프부킹대상관리 신청마감 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException 
	 */
	public int joinEndCancelProc(WaContext context, HttpServletRequest request, TaoDataSet data , String[] arr_seq_no) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		GolfAdminEtt userEtt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			
			//조회 ----------------------------------------------------------		
			String sql = this.getJoinEndUpdateSQL();	
			
			if("joinEndCancel".equals(data.getString("modeType")))
			{
				for (int i = 0; i < arr_seq_no.length; i++) {				
					if (arr_seq_no[i] != null && arr_seq_no[i].length() > 0) {		
						
						String[] pudarry = arr_seq_no[i].split("\\|");
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, admId);
						pstmt.setString(2, "Y");
						pstmt.setString(3, pudarry[0]); 

						iCount += pstmt.executeUpdate();
					}
				}		
								
			}
			
			if(iCount == arr_seq_no.length) {
				conn.commit();
			} else {
				conn.rollback();
			}	
		
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return iCount;
	}	
	
	 /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
 	
     private String getJoinEndUpdateSQL() throws BaseException{
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n     UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT            ");
 		sql.append("\n     SET  CHNG_MGR_ID = ? , EPS_YN = ?         ");
 		sql.append("\n      WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?                  ");
 		return sql.toString();
 	}
     
     
     /**
 	 * 관리자 탑골프부킹대상관리 실패처리
 	 * @param conn
 	 * @param data
 	 * @return
 	 * @throws DbTaoException 
 	 */
 	public String topGolfPriYn(WaContext context, HttpServletRequest request, TaoDataSet data ) throws BaseException  {
 		
 		String title = data.getString("TITLE");
 		Connection conn = null;
 		PreparedStatement pstmt = null;
 		String iCount = "N";
 		ResultSet rs = null;
 		DbTaoResult  result =  new DbTaoResult(title);
 		
 		try {
 			conn = context.getDbConnection("default", null);
 			
 			//조회 ----------------------------------------------------------		
 			String sql = this.gettopGolfPriYn();	
 			
 			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("idx"));
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

 				while(rs.next())  {	

 					
 					result.addString("ESTM_PROC_CMPL_YN" 			,rs.getString("ESTM_PROC_CMPL_YN") ); 												
 										
 					result.addString("RESULT", "00"); //정상결과
 					
 					iCount = rs.getString("ESTM_PROC_CMPL_YN");
 					
 				}
 			}

 			if(result.size() < 1) {
 				result.addString("RESULT", "01");			
 			}
			
			
			
 			
 		} catch (Throwable t) {
 			throw new BaseException(t);
 		} finally {
             try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
             try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
             try { if(rs != null) rs.close(); } catch (Exception ignored) {}
 		}			

 		return iCount;
 	}
 	
	/** ***********************************************************************
	 * Query를 생성하여 리턴한다.    
	 ************************************************************************ */
	 private String gettopGolfPriYn( ){
	     StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	");
		sql.append("\n 		");
		sql.append("\n  ESTM_PROC_CMPL_YN	");		
		sql.append("\n 		");
		sql.append("\n FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT  	");									
		sql.append("\n WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?  	");
		sql.append("\n 	");
		
		return sql.toString();
	 }	
	/**
	 * 관리자 탑골프부킹대상관리 실패처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException 
	 */
	public int failProc(WaContext context, HttpServletRequest request, TaoDataSet data , String[] arr_seq_no) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		GolfAdminEtt userEtt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			
			//조회 ----------------------------------------------------------		
			String sql = this.getUpdateUsrSQL();	
			debug("## failProc");
			if("failUpd".equals(data.getString("modeType")) || "failUpdOk".equals(data.getString("modeType")))
			{
				for (int i = 0; i < arr_seq_no.length; i++) {	
					
					if (arr_seq_no[i] != null && arr_seq_no[i].length() > 0) {		
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, admId);
						pstmt.setString(2, data.getString("pgrs_yn"));
						pstmt.setString(3, arr_seq_no[i]); 

						iCount += pstmt.executeUpdate();
					}
				}		
								
			}
			
			if(iCount == arr_seq_no.length) {
				conn.commit();
			} else {
				conn.rollback();
			}	
		
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return iCount;
	}
	
	 /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
 	
     private String getUpdateUsrSQL() throws BaseException{
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT            ");
 		sql.append("\n     SET  CHNG_MGR_ID = ? , PGRS_YN = ?         ");
 		sql.append("\n      WHERE APLC_SEQ_NO = ?                  ");
 		return sql.toString();
 	}
     
     /**
  	 * Proc 실행.
  	 * @param Connection con
  	 * @param TaoDataSet dataSet
  	 * @return TaoResult
  	 */
  	public DbTaoResult getConfOkList(WaContext context,  HttpServletRequest request ,TaoDataSet data, String[] arr_seq_no) throws BaseException {

  		String title = data.getString("TITLE");
  		ResultSet rs = null;
  		Connection conn = null;
  		PreparedStatement pstmt = null;
  		DbTaoResult  result =  new DbTaoResult(title);

  		try {
  			conn = context.getDbConnection("default", null);			
  			debug("## getConfOkList");
  			//조회 ----------------------------------------------------------
  			String sort = data.getString("sort");	
  			
  			GregorianCalendar today = new GregorianCalendar ( );
	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        String strToday = nYear+"년 "+nMonth+"월 "+nDay+"일";

  			// 입력값 (INPUT)         
  			String arr_seq = "";
  			for (int i = 0; i < arr_seq_no.length; i++) {				
 				if (arr_seq_no[i] != null && arr_seq_no[i].length() > 0) {		
 					
 					if("".equals(arr_seq)) arr_seq = "'"+arr_seq_no[i]+"'";
 					else arr_seq = arr_seq + " ,'"+arr_seq_no[i]+"'";									
 					
 				}
 			}	
  			
  			String sql = this.getConfOkListSelectQuery(arr_seq);   
  			int pidx = 0;
  			pstmt = conn.prepareStatement(sql.toString());
  				
  			pstmt.setString(++pidx, sort);	 			 			
  			rs = pstmt.executeQuery();

  			if(rs != null) {			 

  				while(rs.next())  {	

  					
  					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
  					result.addString("CO_NM" 			,rs.getString("CO_NM") );
  					
  					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
  					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );
  					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );
  					result.addString("EMAIL" 			,rs.getString("EMAIL") );			
  					
 					// SMS 관련 셋팅
  					try {
	 					HashMap smsMap = new HashMap();
	 					
	 					smsMap.put("ip", request.getRemoteAddr());
	 					smsMap.put("sName", 	rs.getString("CO_NM"));
	 					smsMap.put("sPhone1", 	rs.getString("HP_DDD_NO"));
	 					smsMap.put("sPhone2", 	rs.getString("HP_TEL_HNO"));
	 					smsMap.put("sPhone3", 	rs.getString("HP_TEL_SNO"));
	 					
	 					String smsClss = "637";
	 					String message = "[Golf Loun.G] "+rs.getString("CO_NM")+"님,"+rs.getString("GREEN_NM")+ " "+rs.getString("TEOF_DATE")+" "+rs.getString("TEOF_TIME") +" 부킹확정되었습니다";
	 					//String message = "[VIP부킹] "+userNm+"님 "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" 예약완료- Golf Loun.G";
	 					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
	 					smsProc.send(smsClss, smsMap, message);
  					} catch(Throwable t) {}				
 					
 					
 					// 이메일 보내기
 					if(!"".equals(rs.getString("EMAIL"))){
 						try {
 						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
 						String imgPath = "<img src=\"";
 						String hrefPath = "<a href=\"";
 						String emailTitle = "";
 						String emailFileNm = "";
 						
 						EmailSend sender = new EmailSend();
 						EmailEntity emailEtt = new EmailEntity("EUC_KR");
 						
 						emailTitle = "[Golf Loun.G] TOP골프카드 전용부킹 확정 안내";
 						emailFileNm = "/email_tpl28.html";						
 						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, rs.getString("CO_NM")+"|"+rs.getString("CDHD_ID")+"|"+rs.getString("GREEN_NM") + "|" + rs.getString("TEOF_DATE") + "|" + rs.getString("TEOF_TIME") + "|" + strToday);
 						
 						emailEtt.setFrom(emailAdmin);
 						emailEtt.setSubject(emailTitle); 
 						emailEtt.setTo(rs.getString("EMAIL"));
 						sender.send(emailEtt);
 						} catch(Throwable t) {}
 					}
 					else
 					{
 						debug("## email 없어서 발송 불가");
 					}
 					
 					
 					
 					
  										
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
     private String getConfOkListSelectQuery(String arr_seq ){
         StringBuffer sql = new StringBuffer();
 		
 		sql.append("\n SELECT	");
 		sql.append("\n 		");
 		sql.append("\n  CDHD_ID	");
 		sql.append("\n 	,CO_NM  	");	
 		sql.append("\n 	,GREEN_NM 	");	
 		sql.append("\n 	,TEOF_DATE	");
 		sql.append("\n 	,TEOF_TIME	");		
 		sql.append("\n 	,HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL	");
 		
 		sql.append("\n 		");
 		sql.append("\n FROM BCDBA.TBGAPLCMGMT   	");									
 		sql.append("\n WHERE GOLF_SVC_APLC_CLSS = ?  	");
 		sql.append("\n AND APLC_SEQ_NO IN ("+arr_seq+")	");
 		
 		return sql.toString();
     }	
     
     
     /**
   	 * Proc 실행.
   	 * @param Connection con
   	 * @param TaoDataSet dataSet
   	 * @return TaoResult
   	 */
   	public DbTaoResult confStatOkProc(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

   		String title = data.getString("TITLE");
   		ResultSet rs = null;
   		Connection conn = null;
   		PreparedStatement pstmt = null;
   		DbTaoResult  result =  new DbTaoResult(title);
   		int updResult = 0;
   		try {
   			conn = context.getDbConnection("default", null);			
   			debug("## getConfOkList");
   			//조회 ----------------------------------------------------------
   			String idx = data.getString("idx");	
   			
   			
   			String sql = this.getConfOkUpdQuery();   
   			int pidx = 0;
   			if(!"".equals(idx) && idx != null)
   			{
   				pstmt = conn.prepareStatement(sql.toString());
   				pstmt.setString(++pidx, "N");
   	   			pstmt.setString(++pidx, idx);
   	   			updResult = pstmt.executeUpdate();
   			}
   			
   			if(updResult > 0) {
   				conn.commit();
			 	result.addString("RESULT","00");
			 	debug("===== UPDATE 성공 | ");
			} else {
				conn.rollback();
			 	result.addString("RESULT","01");
			 	debug("===== UPDATE 실패 | ");
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
   	
 
	//부킹정보 테이블 업데이트
    private String getConfOkUpdQuery() throws Exception{
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET ").append("\n");
		sql.append("	EPS_YN = ? ").append("\n");
		sql.append("WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ? ").append("\n");
        return sql.toString();
    }
 	/**
 	 * Proc 실행.
 	 * @param Connection con
 	 * @param TaoDataSet dataSet
 	 * @return TaoResult
 	 */
 	public DbTaoResult getFailList(WaContext context,  HttpServletRequest request ,TaoDataSet data, String[] arr_seq_no) throws BaseException {

 		String title = data.getString("TITLE");
 		ResultSet rs = null;
 		Connection conn = null;
 		PreparedStatement pstmt = null;
 		DbTaoResult  result =  new DbTaoResult(title);

 		try {
 			conn = context.getDbConnection("default", null);			
 			 
 			//조회 ----------------------------------------------------------
 			String sort = data.getString("sort");	
 			

 			// 입력값 (INPUT)         
 			String arr_seq = "";
 			for (int i = 0; i < arr_seq_no.length; i++) {				
				if (arr_seq_no[i] != null && arr_seq_no[i].length() > 0) {		
					
					if("".equals(arr_seq)) arr_seq = "'"+arr_seq_no[i]+"'";
					else arr_seq = arr_seq + " ,'"+arr_seq_no[i]+"'";									
					
				}
			}	
 			
 			String sql = this.getFailListSelectQuery(arr_seq);   
 			int pidx = 0;
 			pstmt = conn.prepareStatement(sql.toString());
 				
 			pstmt.setString(++pidx, sort);	 			 			
 			rs = pstmt.executeQuery();

 			if(rs != null) {			 

 				while(rs.next())  {	

 					
 					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
 					result.addString("CO_NM" 			,rs.getString("CO_NM") );
 					
 					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
 					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );
 					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );
 										
 					
					// SMS 관련 셋팅
 					try {
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", 	rs.getString("CO_NM"));
						smsMap.put("sPhone1", 	rs.getString("HP_DDD_NO"));
						smsMap.put("sPhone2", 	rs.getString("HP_TEL_HNO"));
						smsMap.put("sPhone3", 	rs.getString("HP_TEL_SNO"));
						
						String smsClss = "637";
						String message = "[Golf Loun.G] "+rs.getString("CO_NM")+"님,"+rs.getString("GREEN_NM")+ " "+rs.getString("TEOF_DATE")+" "+rs.getString("TEOF_TIME") +" 실패 되었습니다.";
						//String message = "[VIP부킹] "+userNm+"님 "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" 예약완료- Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						smsProc.send(smsClss, smsMap, message);
 					}catch(Throwable t){}	
 										
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
    private String getFailListSelectQuery(String arr_seq ){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	");
		sql.append("\n 		");
		sql.append("\n  CDHD_ID	");
		sql.append("\n 	,CO_NM  	");	
		sql.append("\n 	,GREEN_NM 	");	
		sql.append("\n 	,TEOF_DATE	");
		sql.append("\n 	,TEOF_TIME	");		
		sql.append("\n 	,HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO	");
		
		sql.append("\n 		");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT   	");									
		sql.append("\n WHERE GOLF_SVC_APLC_CLSS = ?  	");
		sql.append("\n AND APLC_SEQ_NO IN ("+arr_seq+")	");
		
		return sql.toString();
    }	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult topGolfConfList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			
			 
			//조회 ----------------------------------------------------------
			String[] bkngObjNo 	= (String[])data.getObject("bkngObjNo");
			
			
			
			if (bkngObjNo != null){
			
				
				String sql = this.getTargetConfSelectQuery(data);   

				// 입력값 (INPUT)         
				pstmt = conn.prepareStatement(sql.toString());								
				rs = pstmt.executeQuery();
				
				
				
			}
			
		
			if(rs != null) {			 

				while(rs.next())  {	

					
					result.addInt("TIME_SEQ_NO" 		,rs.getInt("TIME_SEQ_NO") );
					result.addLong("TIME_SEQ_NO2" 		,rs.getInt("TIME_SEQ_NO") );
					result.addString("VIEW_YN" 			,rs.getString("VIEW_YN") );
					result.addString("ESTM_PROC_CMPL_YN" 		,rs.getString("ESTM_PROC_CMPL_YN") );
					
					result.addString("STATUS_NM" 			,rs.getString("STATUS_NM").trim() );					
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BKPS_DATE"		,DateUtil.format(rs.getString("BKPS_DATE"), "yyyyMMdd", "yyyy.MM.dd") );
					result.addString("COURSE" 			,rs.getString("COURSE") );
					
					try {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4) );
					} catch(Throwable t) {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME"));
					}
					
					result.addString("RESER_CODE" 		,rs.getString("RESER_CODE") );
					try {
						result.addString("REG_DATE"			,DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd") );
					} catch(Throwable t) {
						result.addString("REG_DATE"			,"" );
					}
					result.addString("JOINCNT" 			,rs.getString("JOINCNT") );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
										
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
    private String getTargetConfSelectQuery(TaoDataSet data) throws Exception{
    	 StringBuffer sql = new StringBuffer();
         String[] bkngObjNo = (String[])data.getObject("bkngObjNo");
         
        sql.append("\n SELECT A.*,  MAX(RNUM) OVER() TOT_CNT FROM  (		"); 
     	sql.append("\n SELECT ROWNUM RNUM		");
 		sql.append("\n 	, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO  AS TIME_SEQ_NO 	");
 		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '노출' ELSE '비노출' END) VIEW_YN	");
 		sql.append("\n 			, (CASE WHEN T1.ESTM_PROC_CMPL_YN='Y' THEN '평가완료' ELSE '미처리' END) ESTM_PROC_CMPL_YN	");
 		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '신청가능' WHEN T1.EPS_YN='S' THEN '신청마감' ELSE '부킹확정' END) STATUS_NM	");
 		sql.append("\n 			, T3.GREEN_NM AS GR_NM	");
 		sql.append("\n 			, T2.BOKG_ABLE_DATE AS BKPS_DATE 	");
 		sql.append("\n 			, T2.GOLF_RSVT_CURS_NM AS COURSE	");
 		sql.append("\n 			, T1.BOKG_ABLE_TIME AS BKPS_TIME 	");					
 		sql.append("\n 			, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '부킹대기' ELSE '부킹확정' END) RESER_CODE	");
 		sql.append("\n 			, ( SELECT COUNT(GOLF_LESN_RSVT_NO) FROM BCDBA.TBGAPLCMGMT WHERE GOLF_LESN_RSVT_NO = TO_CHAR(T1.RSVT_ABLE_BOKG_TIME_SEQ_NO) ) AS JOINCNT 	");	
 		sql.append("\n 			, T2.REG_ATON AS REG_DATE	");
 		sql.append("\n 			FROM 	");
 		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
 		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
 		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T2.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	");
 		sql.append("\n 			WHERE T2.GOLF_RSVT_CURS_NM IS NOT NULL AND T2.BOKG_ABLE_DATE IS NOT NULL  	");		
 		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='T'	");
 		sql.append("\n 			AND T3.AFFI_FIRM_CLSS = '1000'	");

 		sql.append("AND ( T1.RSVT_ABLE_BOKG_TIME_SEQ_NO ) IN ( ").append("\n");
		sql.append("	");
		for(int i=0; i<bkngObjNo.length; i++){			
			
			sql.append("'").append(bkngObjNo[i]).append("'");
			if(i==bkngObjNo.length-1) sql.append("\n");
			else sql.append(", ");
		}		
		sql.append(") ").append("\n");
 		
 		sql.append("\n 	ORDER BY T2.BOKG_ABLE_DATE DESC, T1.BOKG_ABLE_TIME DESC 	");		
 		sql.append("\n 	) A	");		
 	

 		
 		return sql.toString();
    }
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult topGolfList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		GolfAdminEtt userEtt = null;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();
			 
			//조회 ----------------------------------------------------------
			String sch_YN = data.getString("SCH_YN");
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_RESER_CODE = data.getString("SCH_RESER_CODE");
			String sch_VIEW_YN = data.getString("SCH_VIEW_YN");
			String sch_EVNT_YN = data.getString("SCH_EVNT_YN");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");		
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			String listtype = data.getString("LISTTYPE");
			String sort = data.getString("SORT");
			String sql = this.getTargetSelectQuery(sch_YN, sch_GR_SEQ_NO, sch_RESER_CODE, sch_VIEW_YN, sch_DATE, sch_DATE_ST, sch_DATE_ED, listtype, admId, admClss, sort, sch_EVNT_YN);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if (!sch_GR_SEQ_NO.equals("")){
				pstmt.setString(++idx, sch_GR_SEQ_NO);
			}
			if (!sch_RESER_CODE.equals("")){
				pstmt.setString(++idx, sch_RESER_CODE);
			}
			if (!sch_VIEW_YN.equals("")){
				pstmt.setString(++idx, sch_VIEW_YN);
			}
			if(!sch_EVNT_YN.equals("")){
				pstmt.setString(++idx, sch_EVNT_YN);
			}	

			if (!"".equals(sort)){
				pstmt.setString(++idx, sort);
			}
			
			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addInt("TIME_SEQ_NO" 		,rs.getInt("TIME_SEQ_NO") );
					result.addLong("TIME_SEQ_NO2" 		,rs.getInt("TIME_SEQ_NO") );
					result.addString("VIEW_YN" 			,rs.getString("VIEW_YN") );
					result.addString("ESTM_PROC_CMPL_YN" 			,rs.getString("ESTM_PROC_CMPL_YN") );
					
					result.addString("STATUS_NM" 			,rs.getString("STATUS_NM").trim() );					
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BKPS_DATE"		,DateUtil.format(rs.getString("BKPS_DATE"), "yyyyMMdd", "yyyy.MM.dd") );
					result.addString("COURSE" 			,rs.getString("COURSE") );
					
					try {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4) );
					} catch(Throwable t) {
						result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME"));
					}
					
					result.addString("RESER_CODE" 		,rs.getString("RESER_CODE") );
					try {
						result.addString("REG_DATE"			,DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd") );
					} catch(Throwable t) {
						result.addString("REG_DATE"			,"" );
					}
					result.addString("JOINCNT" 			,rs.getString("JOINCNT") );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
					result.addString("RESULT", "00"); //정상결과
					
					art_num_no++;
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
	public DbTaoResult topGolfJoinPpList(WaContext context,  HttpServletRequest request ,TaoDataSet data, String strPriYn) throws BaseException {

		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);			
			 
			//조회 ----------------------------------------------------------
			String idx = data.getString("idx");	
			String sort = data.getString("sort");	
			String sql = this.getTargetPpSelectQuery(strPriYn);   

			// 입력값 (INPUT)         
			int pidx = 0;
			pstmt = conn.prepareStatement(sql.toString());
				
			pstmt.setString(++pidx, sort);	
			pstmt.setString(++pidx, idx);		
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	

					
					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
					result.addString("STTL_AMT" 		,rs.getString("STTL_AMT") );
					
					result.addString("CO_NM" 			,rs.getString("CO_NM") );
					result.addString("BKG_PE_NM" 		,rs.getString("BKG_PE_NM") );
					
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );
										
					result.addString("TEOF_DATE"		,DateUtil.format(rs.getString("TEOF_DATE"), "yyyyMMdd", "yyyy.MM.dd") );
					try {
						result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME").substring(0,2)+":"+rs.getString("TEOF_TIME").substring(2,4) );
					} catch(Throwable t) {
						result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME"));
					}
					result.addString("GREEN_NM" 		,rs.getString("GREEN_NM") );
					result.addString("PGRS_YN_NM" 		,rs.getString("PGRS_YN_NM") );
					result.addString("PGRS_YN" 			,rs.getString("PGRS_YN") );
					result.addString("APLC_SEQ_NO" 		,rs.getString("APLC_SEQ_NO") );
					
					try {
						result.addString("REG_ATON"			,DateUtil.format(rs.getString("REG_ATON"), "yyyyMMdd", "yyyy.MM.dd") );
					} catch(Throwable t) {
						result.addString("REG_ATON"			,"" );
					}
										
										
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
	 * Proc 실행.확인/취소
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_status(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sql = this.getStatusQuery(data);   

			long page_no			= data.getLong("PAGE_NO");               //페이지번호
			long record_size		= data.getLong("RECORD_SIZE");           //페이지당 출력될 갯수

			String cdhd_id			= data.getString("CDHD_ID");            //예약골프장명
			     
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setString(++idx, cdhd_id);
			pstmt.setLong(++idx, page_no);
			
			rs = pstmt.executeQuery();
			
			int dataVal1 = 0;
			int dataVal2 = 0;
			int dataVal3 = 0;
			int dataVal4 = 0;
			int dataVal5 = 0;
			int dataVal6 = 0;
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		//예약상태
					result.addString("PGRS_YN_NM",rs.getString("PGRS_YN_NM"));
					result.addString("CODE_PGRS_YN",rs.getString("CODE_PGRS_YN"));		//예약코드
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));	//골프장명
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));	//부킹일자
					
					String teof_time = rs.getString("TEOF_TIME");
					teof_time = teof_time.substring(0,2) + "시대";
					result.addString("TEOF_TIME",teof_time);	
										
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy-MM-dd"));	//신청일
					if(rs.getString("CHNG_ATON") == null){
						result.addString("CHNG_ATON","");	//확정일
						
					}else{ 
						result.addString("CHNG_ATON",rs.getString("c_HOUR")+":"+rs.getString("c_MIN") );	//확정일 
					}
					result.addString("IS_CANCEL"		,rs.getString("IS_CANCEL") );
					result.addString("UPD_BTN"		,rs.getString("UPD_BTN") );
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
					result.addString("CO_NM"			,rs.getString("CO_NM") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					result.addString("RESULT", "00"); //정상결과*/
					
					//통계 작업
					if("R".equals(rs.getString("PGRS_YN"))) // 예약신청
					{
						dataVal1++;
					}
					else if("A".equals(rs.getString("PGRS_YN"))) // 예약취소
					{
						dataVal2++;
					}
					else if("W".equals(rs.getString("PGRS_YN"))) // 부킹대기
					{
						dataVal3++;
					}
					else if("B".equals(rs.getString("PGRS_YN"))) // 확정
					{
						dataVal4++;
					}
					else if("F".equals(rs.getString("PGRS_YN"))) // 실패
					{
						dataVal5++;
					}
					else
					{
						dataVal6++;
					}
					
					
					
				}
				result.addString("dataVal1", ""+dataVal1); 
				result.addString("dataVal2", ""+dataVal2); 
				result.addString("dataVal3", ""+dataVal3); 
				result.addString("dataVal4", ""+dataVal4); 
				result.addString("dataVal5", ""+dataVal5); 
				result.addString("dataVal6", ""+dataVal6); 
				
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
	 * Proc 실행.확인/취소
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_status2(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sql = this.getStatus2Query(data);   


			String cdhd_id			= data.getString("CDHD_ID");            //예약골프장명
			     
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			
			pstmt.setString(++idx, cdhd_id);
			
			
			rs = pstmt.executeQuery();
			
			int dataVal1 = 0;
			int dataVal2 = 0;
			int dataVal3 = 0;
			int dataVal4 = 0;
			int dataVal5 = 0;
			int dataVal6 = 0;
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					
					result.addString("CO_NM"			,rs.getString("CO_NM") );
					result.addString("RESULT", "00"); //정상결과*/
					
					//통계 작업
					if("R".equals(rs.getString("PGRS_YN"))) // 예약신청
					{
						dataVal1++;
					}
					else if("A".equals(rs.getString("PGRS_YN"))) // 예약취소
					{
						dataVal2++;
					}
					else if("W".equals(rs.getString("PGRS_YN"))) // 부킹대기
					{
						dataVal3++;
					}
					else if("B".equals(rs.getString("PGRS_YN"))) // 확정
					{
						dataVal4++;
					}
					else if("F".equals(rs.getString("PGRS_YN"))) // 실패
					{
						dataVal5++;
					}
					else
					{
						dataVal6++;
					}
					
					
					
				}
				result.addString("dataVal1", ""+dataVal1); 
				result.addString("dataVal2", ""+dataVal2); 
				result.addString("dataVal3", ""+dataVal3); 
				result.addString("dataVal4", ""+dataVal4); 
				result.addString("dataVal5", ""+dataVal5); 
				result.addString("dataVal6", ""+dataVal6); 
				
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
	public DbTaoResult getDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		ResultSet rs2 = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			// 회원통합테이블 관련 수정사항 진행
			//조회 ----------------------------------------------------------
			

			String sql = this.getDetailSQL();   
			  
			String seq	= data.getString("seq");      //예약번호
			String sort	= data.getString("sort");
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, sort);	
			pstmt.setString(++idx, seq);		
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		//예약상태
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy/MM/dd"));
					result.addString("CO_NM",rs.getString("CO_NM"));
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("EMAIL",rs.getString("EMAIL"));
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));
					result.addString("TEOF_TIME",rs.getString("TEOF_TIME"));
					result.addString("CHNG_ATON",rs.getString("CHNG_ATON"));
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));
					result.addString("MEMID",rs.getString("MEMID"));
					
					
					if(!"".equals(rs.getString("MEMID")) && rs.getString("MEMID") != null)
					{
						//카드정보
						idx = 0;
						pstmt2 = conn.prepareStatement(getCardDetailSQL());
						pstmt2.setString(++idx, rs.getString("MEMID"));
						rs2 = pstmt2.executeQuery();
						
						if(rs2 != null) {	
							while(rs2.next())  {	
							
								result.addString("CARD_NO",rs2.getString("CARD_NO"));
								//result.addString("VALD_DATE",rs2.getString("VALD_DATE"));
							
							}
						}
						
					}
					
					
				}
			}
			
			
			

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
 	
     private String getCardDetailSQL() throws BaseException{
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n     select * from (  	 ");
 		sql.append("\n        select CARD_NO  from BCDBA.TBGFTEMPPAY			     			");
 		sql.append("\n        where MEMID=?            ");
 		sql.append("\n        order by ROUND_DATE desc                                          						  	 ");
 		sql.append("\n     ) where rownum < 2                                               "); 	
 		
 		
 		
 		
 		return sql.toString();
 	}
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
 	
     private String getDetailSQL() throws BaseException{
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n     SELECT SUBSTR(A.REG_ATON,1,8) REG_ATON,A.CO_NM,A.CDHD_ID,A.PU_TIME,A.PU_DATE,	 ");
 		sql.append("\n            A.HP_DDD_NO,A.HP_TEL_HNO,A.HP_TEL_SNO,A.EMAIL,A.CHNG_ATON,			     			");
 		sql.append("\n            A.MEMO_EXPL,A.GREEN_NM,A.TEOF_DATE,A.TEOF_TIME,A.PGRS_YN, A.BKG_PE_NM	, (SELECT MEMID FROM BCDBA.UCUSRINFO WHERE ACCOUNT = A.CDHD_ID ) AS MEMID            ");
 		sql.append("\n       FROM BCDBA.TBGAPLCMGMT A,                                          						  	 ");
 		sql.append("\n             BCDBA.TBGGOLFCDHD D                                                 ");
 		sql.append("\n      WHERE A.GOLF_SVC_APLC_CLSS = ?                                                              ");
 		sql.append("\n        AND A.APLC_SEQ_NO = ?                                                                          ");
 		sql.append("\n        AND A.CDHD_ID = D.CDHD_ID          ");
 		return sql.toString();
 	}
	
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.  (부킹 확인/취소   
     ************************************************************************ */
 	
     private String getStatus2Query(TaoDataSet data) throws BaseException{
         StringBuffer sql = new StringBuffer();

 		 
 		sql.append("\n     SELECT E.*                                                                                                 ");
 		sql.append("\n       FROM (SELECT D.*                          ");

 		sql.append("\n               FROM (SELECT ROWNUM RN,APLC_SEQ_NO,           ");
 		sql.append("\n                            (CASE WHEN PGRS_YN ='A' THEN '예약취소'  WHEN PGRS_YN = 'R' THEN '예약신청' WHEN PGRS_YN = 'W' THEN '부킹대기' WHEN PGRS_YN = 'B' THEN '부킹확정' WHEN PGRS_YN = 'F' THEN '실패'  WHEN PGRS_YN = 'C' THEN '부킹취소' END) AS PGRS_YN_NM,               ");
 		sql.append("\n                            PGRS_YN,               ");
 		sql.append("\n                            (CASE WHEN PGRS_YN ='S' THEN 'is'  WHEN PGRS_YN = 'R' THEN 'is'  ELSE  'not' END) AS UPD_BTN,               ");
 		sql.append("\n                            (CASE WHEN TEOF_DATE >= TO_CHAR(SYSDATE+4,'YYYYMMDD') THEN 'PO' ELSE 'IMPO' END) IS_CANCEL,               ");
 		sql.append("\n                            PGRS_YN AS CODE_PGRS_YN,               ");
 		sql.append("\n                            GREEN_NM,TEOF_DATE,TEOF_TIME,CO_NM,CDHD_ID,BKG_PE_NM,                ");
 		sql.append("\n                            HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,                ");
 		sql.append("\n                            SUBSTR(MEMO_EXPL,1,20) MEMO_EXPL,SUBSTR(REG_ATON,1,8) REG_ATON,SUBSTR(CHNG_ATON,1,8) CHNG_ATON , SUBSTR(CHNG_ATON,1,2) as c_HOUR,SUBSTR(CHNG_ATON,3,2) as c_MIN           ");
 		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT								                                 ");
 		sql.append("\n                      WHERE GOLF_SVC_APLC_CLSS = '1000'                                                     ");
 		sql.append("\n                      AND CDHD_ID = ?                                                                   ");
 		sql.append("\n                                                                                         ");
 		sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");
 		sql.append("\n                                                                                                            ");
 		sql.append("\n             ) E                                                                                             ");

 		return sql.toString();
     } 	 
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.  (부킹 확인/취소   
     ************************************************************************ */
 	
     private String getStatusQuery(TaoDataSet data) throws BaseException{
         StringBuffer sql = new StringBuffer();

 		 
 		sql.append("\n     SELECT E.*                                                                                                 ");
 		sql.append("\n       FROM (SELECT D.*,ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE,MAX(RN) OVER() TOT_CNT,                           ");
 		sql.append("\n                    (MAX(RN) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO                              ");
 		sql.append("\n               FROM (SELECT ROWNUM RN,APLC_SEQ_NO,           ");
 		sql.append("\n                            (CASE WHEN PGRS_YN ='A' THEN '예약취소'  WHEN PGRS_YN = 'R' THEN '예약신청' WHEN PGRS_YN = 'W' THEN '부킹대기' WHEN PGRS_YN = 'B' THEN '부킹확정' WHEN PGRS_YN = 'F' THEN '실패'  WHEN PGRS_YN = 'C' THEN '부킹취소' END) AS PGRS_YN_NM,               ");
 		sql.append("\n                            PGRS_YN,               ");
 		sql.append("\n                            (CASE WHEN PGRS_YN ='S' THEN 'is'  WHEN PGRS_YN = 'R' THEN 'is'  ELSE  'not' END) AS UPD_BTN,               ");
 		sql.append("\n                            (CASE WHEN TEOF_DATE >= TO_CHAR(SYSDATE+4,'YYYYMMDD') THEN 'PO' ELSE 'IMPO' END) IS_CANCEL,               ");
 		sql.append("\n                            PGRS_YN AS CODE_PGRS_YN,               ");
 		sql.append("\n                            GREEN_NM,TEOF_DATE,TEOF_TIME,CO_NM,CDHD_ID,BKG_PE_NM,                ");
 		sql.append("\n                            HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,                ");
 		sql.append("\n                            SUBSTR(MEMO_EXPL,1,20) MEMO_EXPL,SUBSTR(REG_ATON,1,8) REG_ATON,SUBSTR(CHNG_ATON,1,8) CHNG_ATON , SUBSTR(CHNG_ATON,1,2) as c_HOUR,SUBSTR(CHNG_ATON,3,2) as c_MIN           ");
 		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT								                                 ");
 		sql.append("\n                      WHERE GOLF_SVC_APLC_CLSS = '1000'                                                     ");
 		sql.append("\n                      AND CDHD_ID = ?                                                                   ");
 		sql.append("\n                                                                                         ");
 		sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");
 		sql.append("\n                                                                                                            ");
 		sql.append("\n             ) E                                                                                             ");
 		sql.append("\n      WHERE PAGE = ?   ");
 		return sql.toString();
     } 
	/** ***********************************************************************
	    * Query를 생성하여 리턴한다.    
	    ************************************************************************ */
	    private String getTargetPpSelectQuery(String strPriYn){
	        StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n 		");
			sql.append("\n  CDHD_ID	, NVL(STTL_AMT,0) AS STTL_AMT");
			sql.append("\n 	,CO_NM  	");		
			sql.append("\n 	,BKG_PE_NM	");
			sql.append("\n 	,HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO	");
			sql.append("\n 	,GREEN_NM 	");	
			sql.append("\n 	,TEOF_DATE	");
			sql.append("\n 	,TEOF_TIME	");
			sql.append("\n 	,(CASE WHEN PGRS_YN ='A' THEN '예약취소'  WHEN PGRS_YN = 'R' THEN '예약신청' WHEN PGRS_YN = 'W' THEN '부킹대기' WHEN PGRS_YN = 'B' THEN '부킹확정' WHEN PGRS_YN = 'F' THEN '실패'  WHEN PGRS_YN = 'C' THEN '부킹취소' END) AS PGRS_YN_NM	");
			sql.append("\n 	,PGRS_YN	");
			sql.append("\n 	,APLC_SEQ_NO 	");
			sql.append("\n  ,REG_ATON	");
			sql.append("\n 		");
			sql.append("\n FROM BCDBA.TBGAPLCMGMT   	");									
			sql.append("\n WHERE GOLF_SVC_APLC_CLSS = ?  	");
			sql.append("\n AND GOLF_LESN_RSVT_NO = ?	");
			sql.append("\n ORDER BY APLC_SEQ_NO DESC	");
			
			return sql.toString();
	    }	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getTargetSelectQuery(String sch_YN, String sch_GR_SEQ_NO, String sch_RESER_CODE, String sch_VIEW_YN, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, String listtype, String admId, String admClss, String sort, String sch_EVNT_YN){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, TIME_SEQ_NO, VIEW_YN, ESTM_PROC_CMPL_YN, STATUS_NM,  GR_NM, BKPS_DATE, COURSE, BKPS_TIME, RESER_CODE, JOINCNT, REG_DATE  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 			, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO  AS TIME_SEQ_NO 	");
		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '노출' ELSE '비노출' END) VIEW_YN	");
		sql.append("\n 			, (CASE WHEN T1.ESTM_PROC_CMPL_YN='Y' THEN '평가완료' ELSE '미처리' END) ESTM_PROC_CMPL_YN	");
		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '신청가능' WHEN T1.EPS_YN='S' THEN '신청마감' ELSE '부킹확정' END) STATUS_NM	");
		sql.append("\n 			, T3.GREEN_NM AS GR_NM	");
		sql.append("\n 			, T2.BOKG_ABLE_DATE AS BKPS_DATE 	");
		sql.append("\n 			, T2.GOLF_RSVT_CURS_NM AS COURSE	");
		sql.append("\n 			, T1.BOKG_ABLE_TIME AS BKPS_TIME 	");					
		sql.append("\n 			, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '부킹대기' ELSE '부킹확정' END) RESER_CODE	");
		sql.append("\n 			, ( SELECT COUNT(GOLF_LESN_RSVT_NO) FROM BCDBA.TBGAPLCMGMT WHERE GOLF_LESN_RSVT_NO = TO_CHAR(T1.RSVT_ABLE_BOKG_TIME_SEQ_NO) ) AS JOINCNT 	");	
		sql.append("\n 			, T2.REG_ATON AS REG_DATE	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T2.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 			WHERE T2.GOLF_RSVT_CURS_NM IS NOT NULL AND T2.BOKG_ABLE_DATE IS NOT NULL  	");
		if(!"1000".equals(sort))
		{
		sql.append("\n 			AND T2.PAR_3_BOKG_RESM_DATE IS NULL	");
		sql.append("\n 			AND T2.SKY72_HOLE_CODE IS NULL	");
		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='P'	");
		}
		else
		{
		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='T'	");
		}
		
		
		
		if("Y".equals(sch_YN)){
			if(!sch_GR_SEQ_NO.equals("")){
				sql.append("\n 				AND T3.AFFI_GREEN_SEQ_NO = ?	");
			}
			if(!sch_RESER_CODE.equals("")){
				sql.append("\n 				AND T1.BOKG_RSVT_STAT_CLSS = ?	");
			}
			if(!sch_VIEW_YN.equals("")){
				sql.append("\n 				AND T1.EPS_YN = ?	");
			}
			if(!sch_EVNT_YN.equals("")){
				sql.append("\n 				AND T1.EVNT_YN = ?	");
			}			
			
			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){
					sql.append("\n 			AND T2.BOKG_ABLE_DATE >= '"+sch_DATE_ST+"'	");
				}
				if(!sch_DATE_ED.equals("")){
					sql.append("\n 			AND T2.BOKG_ABLE_DATE <= '"+sch_DATE_ED+"'	");
				}
			}else{
				if(!sch_DATE_ST.equals("")){
					sql.append("\n 			AND T2.REG_ATON >= '"+sch_DATE_ST+"000000'	");
				}
				if(!sch_DATE_ED.equals("")){
					sql.append("\n 			AND T2.REG_ATON <= '"+sch_DATE_ED+"240000'	");
				}
			}		
		}
		if(!"".equals(sort)){
			sql.append("\n 			AND T3.AFFI_FIRM_CLSS = ?	");
		}
		
		
		
		if(!"1000".equals(sort)){
		if(!admClss.equals("master"))	sql.append("\n		AND T3.GREEN_ID='"+admId+"'  	");	
		}
		sql.append("\n 				ORDER BY T2.BOKG_ABLE_DATE DESC, T1.BOKG_ABLE_TIME DESC 	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}

		return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String sch_YN, String sch_GR_SEQ_NO, String sch_RESER_CODE, String sch_VIEW_YN, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, String listtype, String admId, String admClss, String sort, String sch_EVNT_YN){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, TIME_SEQ_NO, VIEW_YN, STATUS_NM, GR_NM, BKPS_DATE, COURSE, BKPS_TIME, RESER_CODE, REG_DATE  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 			, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO	");
		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '노출' ELSE '비노출' END) VIEW_YN	");
		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '신청가능' WHEN T1.EPS_YN='S' THEN '신청마감' ELSE '부킹확정' END) STATUS_NM	");
		sql.append("\n 			, T3.GREEN_NM AS GR_NM	");
		sql.append("\n 			, T2.BOKG_ABLE_DATE AS BKPS_DATE, T2.GOLF_RSVT_CURS_NM AS COURSE, T1.BOKG_ABLE_TIME AS BKPS_TIME	");
		sql.append("\n 			, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '부킹대기' ELSE '부킹확정' END) RESER_CODE	");
		sql.append("\n 			, T2.REG_ATON AS REG_DATE	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T2.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 			WHERE T2.GOLF_RSVT_CURS_NM IS NOT NULL AND T2.BOKG_ABLE_DATE IS NOT NULL  	");
		if(!"1000".equals(sort))
		{
		sql.append("\n 			AND T2.PAR_3_BOKG_RESM_DATE IS NULL	");
		sql.append("\n 			AND T2.SKY72_HOLE_CODE IS NULL	");
		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='P'	");
		}
		else
		{
		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='T'	");
		}
		
		
		
		if("Y".equals(sch_YN)){
			if(!sch_GR_SEQ_NO.equals("")){
				sql.append("\n 				AND T3.AFFI_GREEN_SEQ_NO = ?	");
			}
			if(!sch_RESER_CODE.equals("")){
				sql.append("\n 				AND T1.BOKG_RSVT_STAT_CLSS = ?	");
			}
			if(!sch_VIEW_YN.equals("")){
				sql.append("\n 				AND T1.EPS_YN = ?	");
			}
			if(!sch_EVNT_YN.equals("")){
				sql.append("\n 				AND T1.EVNT_YN = ?	");
			}			
			
			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){
					sql.append("\n 			AND T2.BOKG_ABLE_DATE >= '"+sch_DATE_ST+"'	");
				}
				if(!sch_DATE_ED.equals("")){
					sql.append("\n 			AND T2.BOKG_ABLE_DATE <= '"+sch_DATE_ED+"'	");
				}
			}else{
				if(!sch_DATE_ST.equals("")){
					sql.append("\n 			AND T2.REG_ATON >= '"+sch_DATE_ST+"000000'	");
				}
				if(!sch_DATE_ED.equals("")){
					sql.append("\n 			AND T2.REG_ATON <= '"+sch_DATE_ED+"240000'	");
				}
			}		
		}
		if(!"".equals(sort)){
			sql.append("\n 			AND T3.AFFI_FIRM_CLSS = ?	");
		}
		
		
		
		if(!"1000".equals(sort)){
		if(!admClss.equals("master"))	sql.append("\n		AND T3.GREEN_ID='"+admId+"'  	");	
		}
		sql.append("\n 				ORDER BY T2.BOKG_ABLE_DATE DESC, T1.BOKG_ABLE_TIME DESC 	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}

		return sql.toString();
    }
}
