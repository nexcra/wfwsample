/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtGradeUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 마이티박스 > 나의 정보 > 회원등급 업그레이드
*   적용범위  : golf 
*   작성일자  : 2009-07-04 
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100514	임은혜	IBK기업은행 Gold 회원 업그레이드 => 전액결제, 유료회원기간 14개월 설정
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtGradeUpdDaoProc extends AbstractProc {

	public static final String TITLE = "마이티박스 > 나의 정보 > 회원등급 업그레이드";

	public GolfMtGradeUpdDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int iidx = 0;
		String memId = "";		// 회원아이디
		int intMemGrade = 0;	// 멤버십 등급

				
		try {

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memId = userEtt.getAccount();
				intMemGrade = userEtt.getIntMemGrade();
			}
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			int idx = data.getInt("idx");	
			String upd_pay = data.getString("UPD_PAY");					// 한달여부  half : 아직 한달안된상태	
			String payWay = data.getString("payWay");					// 결제 타입 => mn:월결제	
			String realPayAmt_old = data.getString("realPayAmt_old");	// 금액	
			String cdhd_SQ1_CTGO = "0002";								// 회원1차분류코드 => 0001:골프카드고객 0002멤버쉽고객 0003:사이버고객
			String cdhd_SQ2_CTGO = GolfUtil.lpad(idx+"", 4, "0");		// 회원2차분류코드 => 0001:VIP 0002:골드 0003:우량 , 공통 코드 테이블과 조인
			String cdhd_CTGO_SEQ_NO = "";								// 회원분류일련번호
			
			int grade_seq = 0;				// 멤버십 등급
			String is_charged_mem = "";		// 유료회원(유료회원 종료기간이 아직 남은 회원)  

			debug("GolfMtGradeUpdDaoProc : idx : " + idx + " | upd_pay : " + upd_pay + " | payWay : " + payWay + " | realPayAmt_old : " + realPayAmt_old 
					+ " | is_charged_mem : " + is_charged_mem + " | cdhd_SQ2_CTGO : " + cdhd_SQ2_CTGO);
            
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();	
            

    		// 현재 멤버십 등급이 있는지 확인
    		sql = this.getMembershipGradeQuery();
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, memId);
    		rs = pstmt.executeQuery();
    		
    		if(rs.next()){
    			//debug("===GolfMemInsDaoProc======= 멤버십 등급이 있으면 해당 결과를 가져와서 히스토리 테이블에 등록하고 회원 등급을 업데이트 한다.");
            	grade_seq = rs.getInt("GRADE_SEQ");
            	is_charged_mem = rs.getString("IS_CHARGED_MEM");
            	
            	if(grade_seq > 0){
            		
            		//debug("===GolfMemInsDaoProc======= 히스토리 인서트");
					/**SEQ_NO 가져오기**************************************************************/
					sql = this.getMaxHistoryQuery(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_seq_no = 0L;
					if(rs.next()){
						max_seq_no = rs.getLong("MAX_SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
		            sql = this.setHistoryQuery();
					pstmt = conn.prepareStatement(sql);
					
					iidx = 0;
					pstmt.setLong(++iidx, max_seq_no );	// 히스토리 테이블 일련번호
					pstmt.setLong(++iidx, grade_seq );	// 골프회원등급관리 테이블 일련번호
					
					int result2 = pstmt.executeUpdate();
					
					if(result2 > 0){
		            
						//debug("===GolfMemInsDaoProc======= 등급 업데이트");
	        			iidx = 0;
	        			sql = this.updGradeQuery();
	        			pstmt = conn.prepareStatement(sql);
	        			
	        			pstmt.setString(++iidx,  cdhd_CTGO_SEQ_NO);
	        			pstmt.setInt(++iidx,  grade_seq);
	        			
	        			result = pstmt.executeUpdate();
	        			
	        			if(result > 0){
	        				
    						//debug("===GolfMtGradeUpdDaoProc======= 한달이 넘은 회원은 유료회원 기간을 업데이트 해준다. e-champ->champ 기간 다시 셋팅"); 
	        				debug("upd_pay : " + upd_pay + " / intMemGrade : " + intMemGrade + " / is_charged_mem : " + is_charged_mem + " / cdhd_CTGO_SEQ_NO : " + cdhd_CTGO_SEQ_NO);
    						sql = this.getMemberUpdateQuery(upd_pay, intMemGrade, is_charged_mem, cdhd_CTGO_SEQ_NO);
    						pstmt = conn.prepareStatement(sql);
    			        	pstmt.setString(1, cdhd_CTGO_SEQ_NO );
    			        	pstmt.setString(2, memId );
    			        	
    						result = pstmt.executeUpdate();

	        			}
	        			
						if("mn".equals(payWay)){
    						//debug("===GolfMtGradeUpdDaoProc======= 신청관리 테이블에 금액업데이트, 한달넘은 회원은 횟수 없데이트");
    						sql = this.updMnPayQuery(upd_pay);
    						pstmt = conn.prepareStatement(sql);
    						iidx = 0;
    			        	pstmt.setString(++iidx, realPayAmt_old );	// 금액
    			        	pstmt.setString(++iidx, idx+"" );	// 변경되는 등급No 1:champion, 2:blue, 3:gold 
    			        	pstmt.setString(++iidx, memId );
    						result = pstmt.executeUpdate();
						}  
			        	
			            if(pstmt != null) pstmt.close();
					}
            		
            	}
    		}
    	


			
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
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}

	public int execute_mnSeq(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		String memId = "";			// 회원아이디
		String socId = "";			// 주민등록번호
		int aplc_seq_no = 0;	// 신청테이블(월결제) seq_no

				
		try {
			conn = context.getDbConnection("default", null);

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memId = userEtt.getAccount();
				socId = userEtt.getSocid();
			}	

			sql = this.getAplQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, memId );
        	pstmt.setString(2, socId );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				aplc_seq_no = rs.getInt("APLC_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();	
            
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return aplc_seq_no;
	}

	

   	/** ***********************************************************************
    * 회원 분류 정보 가져오기 - TBGGOLFCDHDCTGOMGMT    
    ************************************************************************ */
    private String getMemberLevelQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("\n");
      sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM					\n");
      sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
      sql.append("\t    WHERE CDHD_SQ1_CTGO=? AND CDHD_SQ2_CTGO=?	\n");
      return sql.toString();
    }

    
    /** ***********************************************************************
    * 회원 업데이트 - 유료회원으로    upd_pay = half 이면 유료결제일자를 업데이트 하지 않는다.
    ************************************************************************ */
    private String getMemberUpdateQuery(String upd_pay, int intMemGrade, String is_charged_mem, String cdhd_CTGO_SEQ_NO){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
 		
 		if(intMemGrade==13){	// IBK기업은행 Gold 회원은 유료회원 기간이 종료되기 전에 업데이트하면 유료회원기간 종료일을 '유료회원기간시작일+14개월 해준다. 
 			if(is_charged_mem.equals("Y")){
 				if(cdhd_CTGO_SEQ_NO.equals("17")){
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),5),'YYYYMMDD'), 	\n");
 				}else{
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),14),'YYYYMMDD'), 	\n");
 				}
 			}else{
		 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'),	\n");
 				if(cdhd_CTGO_SEQ_NO.equals("17")){
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD'), 	\n");
 				}else{
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD'), 	\n");
 				}
 			}
 		}else{
	 		if("half".equals(upd_pay)){	// 차액결제 회원들은 유료회원기간을 업데이트 하지 않는다.
	 			if(intMemGrade==12){	// e-champ 회원이 champ로 업데이트 할때는 기간을 1년으로 연장해준다.
	 				sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),12),'YYYYMMDD'), 	\n");
	 			}
	 		}else{	// 전액결제 회원은 유료회원기간을 업데이트 해준다.
		 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'),	\n");

		 		if(cdhd_CTGO_SEQ_NO.equals("17")){
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD'), 	\n");
 				}else{
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD'), 	\n");
 				}
	 		}
 		}
 		 		
 		sql.append("\t		CDHD_CTGO_SEQ_NO = ?	\n");
 		sql.append("\t		WHERE CDHD_ID=?	\n");
        return sql.toString();
    }
	 
	/** ***********************************************************************
	* 해당회원이 멤버십 등급을 가지고 있는지 검색 (멤버십만)
	************************************************************************ */
	private String getMembershipGradeQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	SELECT T2.CDHD_CTGO_SEQ_NO GRADE_NO, T1.CDHD_GRD_SEQ_NO GRADE_SEQ	\n");
		sql.append("\t	, CASE WHEN (TO_DATE(T4.ACRG_CDHD_END_DATE)+1-SYSDATE)>0 THEN 'Y' ELSE 'N' END IS_CHARGED_MEM	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHD T4 ON T1.CDHD_ID=T4.CDHD_ID	\n");
		sql.append("\t 	WHERE T1.CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 히스토리 테이블 일련번호 최대값 가져오기 
	************************************************************************ */
	private String getMaxHistoryQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGCDHDGRDCHNGHST 	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 히스토리 테이블에 저장한다. 
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	");
		sql.append("\n	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	");
		sql.append("\n	SELECT ?, A.CDHD_CTGO_SEQ_NO, A.CDHD_ID, A.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS')	");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHDGRDMGMT A , BCDBA.TBGGOLFCDHD B	");
		sql.append("\n	WHERE A.CDHD_GRD_SEQ_NO= ?	");
		sql.append("\n	AND A.CDHD_ID = B.CDHD_ID	");
		
					
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 등급을 업데이트 한다.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t	WHERE CDHD_GRD_SEQ_NO=?	\n");
				
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 월회비결제 업데이트
	************************************************************************ */
	private String updMnPayQuery(String upd_pay){
		StringBuffer sql = new StringBuffer();
		
 		sql.append("\n");
		sql.append("\t 	UPDATE BCDBA.TBGAPLCMGMT SET	\n");
		
		if(!"half".equals(upd_pay)){
			sql.append("\t	GOLF_LESN_RSVT_NO = 1, PU_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD'), 	\n");
		}
		
		sql.append("\t 	CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?, RSVT_CDHD_GRD_SEQ_NO=?	\n");
		sql.append("\t 	WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
		
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 월회비 결제 내역 가져오기
	************************************************************************ */
	private String getAplQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n 	SELECT APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND CDHD_ID=? AND JUMIN_NO=? AND PGRS_YN='Y'	\n");		
		return sql.toString();
	}
                          
}
