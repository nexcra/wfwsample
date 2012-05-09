/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemTmInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리 > TM
*   적용범위  : golf 
*   작성일자  : 2009-07-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfMemEvtInsDaoProc extends AbstractProc {

	public static final String TITLE = "회원가입처리 > EVT성 회원 가입 처리";

	public GolfMemEvtInsDaoProc() {}
	
	public int execute_ibkGold(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int resultExecute = 0;
		int intResult = 0;
						
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId 				= userEtt.getSocid();

			String joinChnl				= "";	// 골프회원테이블 가입경로구분 코드
			String cdhd_ctgo_seq_no		= "";	// 회원분류일련번호 = 대표등급
			String email1 				= "";	// 이메일주소
			String zipcode 				= "";	// 우편번호
			String zipaddr 				= "";	// 주소1
			String detailaddr 			= "";	// 주소2
			String mobile 				= "";	// 모바일
			String phone 				= "";	// 집전화
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
			GregorianCalendar cal = new GregorianCalendar(); 
	        Date stdate = cal.getTime();
	        String strStDate = fmt.format(stdate);	// 유료회원기간 시작일
	        
	        cal.add(cal.MONTH, 2);
	        Date edDate = cal.getTime();
	        String strEdDate = fmt.format(edDate);	// 유료회원기간 종료일
	        
	        
			sql = getIbkGoldQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){

				joinChnl 	= rs.getString("RCRU_PL_CLSS");
				email1 		= rs.getString("EMAIL1");
				zipcode 	= rs.getString("ZIPCODE");
				zipaddr 	= rs.getString("ZIPADDR");
				detailaddr 	= rs.getString("DETAILADDR");
				mobile 		= rs.getString("MOBILE");
				phone 		= rs.getString("PHONE");
				cdhd_ctgo_seq_no 	= "18";

				// 이미 등록되어 있는지 알아본다.
				int isMem = execute_isMem(context, socId);
				if(isMem==0){
			        
					// 회원테이블 인서트 - 대표등급, 회원정보, 최근접속일자
					data.setString("memId", memId);
					data.setString("memNm", memNm);
					data.setString("socId", socId);
					data.setString("joinChnl", joinChnl);
					data.setString("cdhd_ctgo_seq_no", cdhd_ctgo_seq_no);
					data.setString("mobile", mobile);
					data.setString("phone", phone);
					data.setString("email1", email1);
					data.setString("zipcode", zipcode);
					data.setString("zipaddr", zipaddr);
					data.setString("detailaddr", detailaddr);
					data.setString("strStDate", strStDate);
					data.setString("strEdDate", strEdDate);
					
					// 회원 테이블 인서트
					resultExecute = execute_insMem(context, data);
					debug("회원 테이블 인서트 결과 :: resultExecute : " + resultExecute);
		            if(resultExecute>0){
		            	// 회원 등급 테이블 인서트
		            	resultExecute = execute_insGrd(context, data);
						debug("회원 등급 테이블 인서트 :: resultExecute : " + resultExecute);
		            }
		            
	            }
		        
				// 가입 후 이벤트 테이블 업데이트
		        if(resultExecute>0){
		            sql = this.getUpdIbkGoldEndQuery();
					pstmt = conn.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, memId );
		        	pstmt.setString(++idx, socId );
		        					        	
		        	resultExecute = pstmt.executeUpdate();
					debug("가입 후 이벤트 테이블 업데이트 :: resultExecute : " + resultExecute);
		        }
			}

				        
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
						
			if(resultExecute > 0) {				
				conn.commit();
				intResult = 1;
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

		return intResult;
	}
	
	// 골프라운지 회원인지 알아본다.
	public int execute_isMem(WaContext context, String socId) throws TaoException {

		String title				= "회원인지 알아본다.";	
		Connection conn 			= null;	
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		
		try {
			conn = context.getDbConnection("default", null);
			
			// 이미 등록되어 있는지 알아본다.
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
            rs = pstmt.executeQuery();	
			if(rs != null && rs.next()){
				result = 1;
			}


			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}
	
	// 골프라운지 회원테이블 인서트
	public int execute_insMem(WaContext context, TaoDataSet data) throws TaoException {

		String title				= "회원테이블 등록";	
		Connection conn 			= null;	
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		int returnResult			= 0;
		int idx						= 0;
		
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			sql = this.getInsertMemQuery();
			pstmt = conn.prepareStatement(sql);
			idx = 0;
        	pstmt.setString(++idx, data.getString("memId") );
        	pstmt.setString(++idx, data.getString("memNm") );
        	pstmt.setString(++idx, data.getString("socId") );
        	pstmt.setString(++idx, data.getString("joinChnl") );
        	pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
        	pstmt.setString(++idx, data.getString("mobile") );
        	pstmt.setString(++idx, data.getString("phone") );
        	pstmt.setString(++idx, data.getString("email1") );
        	pstmt.setString(++idx, data.getString("zipcode") );
        	pstmt.setString(++idx, data.getString("zipaddr") );
        	pstmt.setString(++idx, data.getString("detailaddr") );
        	pstmt.setString(++idx, data.getString("strStDate") );
        	pstmt.setString(++idx, data.getString("strEdDate") );
        					        	
        	result = pstmt.executeUpdate();

			if(result > 0) {				
				conn.commit();
				returnResult = 1;
			} else {
				conn.rollback();
			}

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return returnResult;
		
	}

	// 골프라운지 등급테이블 인서트
	public int execute_insGrd(WaContext context, TaoDataSet data) throws TaoException {

		String title				= "회원테이블 등록";	
		Connection conn 			= null;	
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;	
		int returnResult			= 0;
		int idx						= 0;
		
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 회원등급 테이블 업데이트
			sql = this.getChkGradeQuery(); 
			pstmt = conn.prepareStatement(sql);
			idx = 0;
			pstmt.setString(++idx, data.getString("memId") ); 
			pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
			rs = pstmt.executeQuery();	
			if(!rs.next()){
			
			    sql = this.getInsertGradeQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
				pstmt.setString(++idx, data.getString("memId") ); 
				pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
				
				result = pstmt.executeUpdate();

				if(result > 0) {				
					conn.commit();
					returnResult = 1;
				} else {
					conn.rollback();
				}
			}
			

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return returnResult;
		
	}
    /** ***********************************************************************
    * 기업골드 이벤트 회원인지 알아본다.
    ************************************************************************ */
    private String getIbkGoldQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT TM_IBK.JUMIN_NO, TM_IBK.RCRU_PL_CLSS	\n");
 		sql.append("\t	, TB_INFO.EMAIL1, TB_INFO.ZIPCODE, TB_INFO.ZIPADDR, TB_INFO.DETAILADDR, TB_INFO.MOBILE, TB_INFO.PHONE	\n");
 		sql.append("\t	FROM  BCDBA.TBACRGCDHDLODNTBL TM_IBK	\n");
 		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON TM_IBK.JUMIN_NO = TB_INFO.SOCID	\n");
 		sql.append("\t	WHERE TM_IBK.SITE_CLSS='02' AND TM_IBK.RCRU_PL_CLSS='4003'	\n");
 		sql.append("\t	AND TM_IBK.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  TM_IBK.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND TM_IBK.JUMIN_NO = ?	\n");
        return sql.toString();
    }

 	/** ***********************************************************************
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  WHERE JUMIN_NO=?	\n");
		return sql.toString();
	}

    /** ***********************************************************************
    * 골프회원정보에 인서트 - TBGGOLFCDHD    
    ************************************************************************ */
    private String getInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (	\n");
		sql.append("\t  	CDHD_ID, HG_NM, JUMIN_NO, JOIN_CHNL	\n");
		sql.append("\t  	, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN	\n");
		sql.append("\t  	, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT , MEMBER_CLSS	\n");
		sql.append("\t  	, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  	, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE	\n");
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	?, ?, ?, ?	\n");
		sql.append("\t  	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y'	\n");
		sql.append("\t  	, 0, 0, '1'	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  	, ?, ?	\n");
		sql.append("\t  )	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * 같은 등급이 등록되어 있는지 확인    
     ************************************************************************ */
     private String getChkGradeQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
        sql.append("\t  SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
 		return sql.toString();
     }

     
     /** ***********************************************************************
     * 골프회원등급관리 인서트 - TBGGOLFCDHDGRDMGMT    
     ************************************************************************ */
     private String getInsertGradeQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (	\n");
 		sql.append("\t  	CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
 		sql.append("\t  ) VALUES (	\n");
 		sql.append("\t  	(SELECT MAX(NVL(CDHD_GRD_SEQ_NO,0))+1 FROM BCDBA.TBGGOLFCDHDGRDMGMT), ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  )	\n");
         return sql.toString();
     }   
 	
	/** ***********************************************************************
	* 기업골드회원 완료 후 업데이트    
	************************************************************************ */
	private String getUpdIbkGoldEndQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND JUMIN_NO=?	\n");
		return sql.toString();
	}     
}
