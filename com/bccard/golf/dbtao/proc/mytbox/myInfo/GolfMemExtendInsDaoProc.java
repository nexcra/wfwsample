/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfMemExtendInsDaoProc extends AbstractProc {

	public static final String TITLE = "마이페이지 > 유료회원 연장처리 Proc";

	public GolfMemExtendInsDaoProc() {}


	/** ***********************************************************************
	* 알아보기    
	*********************************************************************** */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		ResultSet rs4 = null;
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;
		int insJoinChnlHistoryResult = 0;		// 회원가입경로 히스토리 인서트 처리결과
		int insGradeHistoryResult = 0;			// 회원등급 히스토리 인서트 처리결과
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String memId				= userEtt.getAccount();
			int intMemGrade				= userEtt.getIntMemGrade();
			String payWay				= data.getString("payWay").trim();		// yr:연회비, mn:연회비
			
			
			// 회원기간이 종료되었으면 오늘부터 다시 설정한다. // 투어 블랙은 회원기간이 남았어도 오늘부터 유료회원기간 셋팅
    		sql = this.getReYnQuery();
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, memId);
    		rs = pstmt.executeQuery();
    		String re_yn = "N";					// 기간 종료여부
    		String acrg_cdhd_end_date = "";		// 유료회원 종료일
    		String join_chnl = "";				// 가입경로
    		String cdhd_ctgo_seq_no = "";		// 대표등급
    		String tour_black_yn = "N";			// 투어블랙 회원 여부
    		String ibk_gold_yn = "N";			// IBK 기업골드 회원 여부

			if(rs != null) {			 
				while(rs.next())  {	
					re_yn = rs.getString("RE_YN");
					acrg_cdhd_end_date = rs.getString("ACRG_CDHD_END_DATE");
					join_chnl = rs.getString("JOIN_CHNL");
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
					
					// 투어블랙 회원은 기간이 남았어도 오늘부터 유료회원기간 셋팅
					if(join_chnl.equals("3000") && cdhd_ctgo_seq_no.equals("11")){
						tour_black_yn = "Y";
						re_yn = "N";
					}
					
					// IBK 기업골드 회원은 기간이 남았어도 오늘부터 유료회원기간 셋팅
					if(cdhd_ctgo_seq_no.equals("18")){
						ibk_gold_yn = "Y";
						re_yn = "N";
					}
				}
			}
			
			// 투어블랙 회원은 가입경로 히스토리 저장한 후 가입경로 변경
			if(tour_black_yn.equals("Y") || ibk_gold_yn.equals("Y")){
	            insJoinChnlHistoryResult = insJoinChnlHistoryExecute(context, data, request, conn);
			}
			
			// IBK 기업골드 회원은 등급을 변경해준다. - 등급변경 히스토리 저장
			if(ibk_gold_yn.equals("Y")){
	            insGradeHistoryResult = insGradeHistoryExecute(context, data, request, conn);
			}
			
			debug("re_yn : " + re_yn + " / insJoinChnlHistoryResult : " + insJoinChnlHistoryResult + " / insGradeHistoryResult : " + insGradeHistoryResult 
					+ " / tour_black_yn : " + tour_black_yn + " / ibk_gold_yn : " + ibk_gold_yn + " / intMemGrade : " + intMemGrade);
			
            sql = this.getExtendQuery(re_yn, insJoinChnlHistoryResult, ibk_gold_yn, intMemGrade);
			pstmt = conn.prepareStatement(sql);
			
			idx = 0;
			if(re_yn.equals("Y")){
	        	pstmt.setString(++idx, acrg_cdhd_end_date );
	        	pstmt.setString(++idx, acrg_cdhd_end_date );
			}
        	pstmt.setString(++idx, memId );
			result = pstmt.executeUpdate();
						
            // 월결제 일경우 
            if("mn".equals(payWay)){
	            mnInsExecute(context, data, request, "ins", "");
            }
			
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}


	/** ***********************************************************************
	* 월결제 저장하기    
	*********************************************************************** */
	public int mnInsExecute(WaContext context, TaoDataSet data, HttpServletRequest request, String memSort, String paySort) throws BaseException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		
		// memSort => ins:신규등록, upd:업데이트 / paySort => all:전액, half:반액

		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String sql = "";
			int idx = 0;

			String memId				= userEtt.getAccount();
			String sttl_amt				= data.getString("STTL_AMT").trim();		// 결제금액

            sql = this.getPayMonthQuery(memSort, paySort);
			pstmt = conn.prepareStatement(sql);
			idx = 0;
			
			if("ins".equals(memSort)){
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, sttl_amt );
			}else{
	        	pstmt.setString(++idx, sttl_amt );
	        	pstmt.setString(++idx, memId ); 
			}
		
			
        	
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
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	


	/** ***********************************************************************
	* 가입경로 히스토리 인서트
	*********************************************************************** */
	public int insJoinChnlHistoryExecute(WaContext context, TaoDataSet data, HttpServletRequest request, Connection conn) throws BaseException {

		PreparedStatement pstmt = null;
		int result =  0;
		
		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			String sql = "";
			String memId = userEtt.getAccount();
			int idx = 0;

            sql = this.getJoinChnlHistoryInsQuery();
			pstmt = conn.prepareStatement(sql);	
	        pstmt.setString(++idx, memId ); 
			result = pstmt.executeUpdate();
			//debug("가입경로 히스토리 인서트 result : " + result);


		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	

	/** ***********************************************************************
	* 회원등급 히스토리 인서트
	*********************************************************************** */
	public int insGradeHistoryExecute(WaContext context, TaoDataSet data, HttpServletRequest request, Connection conn) throws BaseException {

		PreparedStatement pstmt = null;
		int result =  0;
		
		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			String sql = "";
			String memId = userEtt.getAccount();
			int idx = 0;

            sql = this.setHistoryQuery();
			pstmt = conn.prepareStatement(sql);	
	        pstmt.setString(++idx, memId ); 
			result = pstmt.executeUpdate();
			
			if(result>0){
				// 등급을 업데이트 합니다.
				sql = this.updGradeQuery();
				pstmt = conn.prepareStatement(sql);
				idx = 0;
		        pstmt.setString(++idx, memId ); 
				result = pstmt.executeUpdate();
			}
			
			//debug("가입경로 히스토리 인서트 result : " + result);


		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	
	
	

 	/** ***********************************************************************
	* 월결제 등록하기
	************************************************************************ */
	private String getPayMonthQuery(String memSort, String paySort){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("ins".equals(memSort)){
			
			sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_LESN_RSVT_NO	\n");
			sql.append("\t  , GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT)	\n");
			sql.append("\t  (SELECT MAX(APLC_SEQ_NO)+1, 1, '1001', 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
			sql.append("\t  , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),?	\n");
			sql.append("\t	FROM BCDBA.TBGAPLCMGMT)	\n");	
			
		}else{
			if("all".equals(paySort)){
				
				sql.append("\t  UPDATE BCDBA.TBGAPLCMGMT SET 	\n");
				sql.append("\t  GOLF_LESN_RSVT_NO = 1, PU_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
				sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?	\n");
				sql.append("\t  WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
				
			}else{
				
				sql.append("\t  UPDATE BCDBA.TBGAPLCMGMT SET 	\n");
				sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?	\n");
				sql.append("\t  WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
				
			}
		}
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* 유료회원기간 연장하기
	************************************************************************ */
	private String getExtendQuery(String re_yn, int insJoinChnlHistoryResult, String ibk_gold_yn, int intMemGrade){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD SET 	\n");
		
		// e-champ 회원은 3개월 
		if(intMemGrade==12){
		
			if(re_yn.equals("Y")){	// 기간이 남았으면 종료일부터 일년
				sql.append("\t  ACRG_CDHD_JONN_DATE=?	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(?),3),'YYYYMMDD')	\n");
			}else{					// 기간이 종료 되었으면 오늘부터 일년
				sql.append("\t  ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD') 	\n");
			}
		}else{
		
			if(re_yn.equals("Y")){	// 기간이 남았으면 종료일부터 일년
				sql.append("\t  ACRG_CDHD_JONN_DATE=?	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(?),12),'YYYYMMDD')	\n");
			}else{					// 기간이 종료 되었으면 오늘부터 일년
				sql.append("\t  ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD') 	\n");
			}
		}
		
		// 투어블랙 회원은 가입경로를 일반으로 변경해준다.
		if(insJoinChnlHistoryResult>0){
			sql.append("\t  , JOIN_CHNL='0001' 	\n");
		}
		
		// IBK기업 골드 회원은 일반골드 회원으로 변경해준다.
		if(ibk_gold_yn.equals("Y")){
			sql.append("\t  , CDHD_CTGO_SEQ_NO='7' 	\n");
		}
		
		
		sql.append("\t  WHERE CDHD_ID=?	\n");
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* 유료회원기간이 남았는지 확인
	************************************************************************ */
	private String getReYnQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT CASE WHEN ACRG_CDHD_END_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ELSE 'N' END RE_YN, ACRG_CDHD_END_DATE, JOIN_CHNL, CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	WHERE CDHD_ID=?	\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * 가입경로 히스토리 인서트 쿼리
    ************************************************************************ */
    private String getJoinChnlHistoryInsQuery(){
    	StringBuffer sql = new StringBuffer();
    	sql.append("	\n");
 		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t	(SEQ_NO, CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON)	\n");
 		sql.append("\t	(SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST), JOIN_CHNL, CDHD_ID, '0'	\n");
 		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ACRG_CDHD_JONN_DATE , ACRG_CDHD_END_DATE , JOIN_CHNL FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?)	\n");
   		 		
 		
        return sql.toString();
    }

	/** ***********************************************************************
	* 등급 히스토리 테이블에 저장한다.
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST 	\n");
		sql.append("\t 	SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
		sql.append("\t	, GRD.CDHD_CTGO_SEQ_NO, CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO 	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID = B.CDHD_ID 	\n");
		sql.append("\t 	WHERE GRD.CDHD_ID=? AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
				
		
					
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 등급을 업데이트 한다.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO='7', CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT='IBK기업Gold회원 Gold로 기간연장' 	\n");
		sql.append("\t	WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO='18'	\n");
				
		return sql.toString();
	}
}
