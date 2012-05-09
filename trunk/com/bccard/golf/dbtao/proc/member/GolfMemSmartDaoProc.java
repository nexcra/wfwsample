/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemSmartDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 가입 > 등록처리 
*   적용범위  : golf 
*   작성일자  : 20110608
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

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
public class GolfMemSmartDaoProc extends AbstractProc {

	public static final String TITLE = "가입 > 스마트카드 가입 Proc";
	// 우선 4100 만 하자 나중에 여러 채널 시 수정 예정 (앞으로 어떤 유형으로 들어 올지 모르니..)

	public GolfMemSmartDaoProc() {}
	
	public int[] execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;
		Connection conn = null;
		
		ResultSet rs = null;
		ResultSet rs2 = null;		
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		
		int resultExecute = 0;
		int intResult = 0;		
		int retVals[]				= new int[2];

		boolean chk = false;
		int cnt = 0;
		
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId 				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();
			
			String joinChnl				= "";	// 골프회원테이블 가입경로구분 코드
			String cdhd_ctgo_seq_no		= "";	// 회원분류일련번호 = 대표등급
			String email1 				= "";	// 이메일주소
			String zipcode 				= "";	// 우편번호
			String zipaddr 				= "";	// 주소1
			String detailaddr 			= "";	// 주소2
			String mobile 				= "";	// 모바일
			String phone 				= "";	// 집전화

			pstmt = conn.prepareStatement(getSmartMem());
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			
			while(rs.next()){

				joinChnl 	= rs.getString("RCRU_PL_CLSS");
				email1 		= rs.getString("EMAIL1");
				zipcode 	= rs.getString("ZIPCODE");
				zipaddr 	= rs.getString("ZIPADDR");
				detailaddr 	= rs.getString("DETAILADDR");
				mobile 		= rs.getString("MOBILE");
				phone 		= rs.getString("PHONE");
				cdhd_ctgo_seq_no 		= rs.getString("GRADE");
				
				// 1단계 이미 가입된 ID인지 체크
				pstmt2 = conn.prepareStatement(getMemberedCheckQuery());
				pstmt2.setString(1, socId );
				pstmt2.setString(2, memId );
	        	rs2 = pstmt2.executeQuery();
				
	            if(!rs2.next()){// 신규가입처리
	            	
	            	chk = true;
			        
					// 회원테이블 인서트 - 대표등급, 회원정보, 최근접속일자
	            	pstmt3 = conn.prepareStatement(getInsertMemQuery());
					idx = 0;
					pstmt3.setString(++idx, memId );
					pstmt3.setString(++idx, memNm );
					pstmt3.setString(++idx, socId );
					pstmt3.setString(++idx, joinChnl );
					pstmt3.setString(++idx, strMemClss );
					pstmt3.setString(++idx, cdhd_ctgo_seq_no );
					pstmt3.setString(++idx, mobile );
					pstmt3.setString(++idx, phone );
					pstmt3.setString(++idx, email1 );
					pstmt3.setString(++idx, zipcode );
					pstmt3.setString(++idx, zipaddr );
					pstmt3.setString(++idx, detailaddr );		        					        	
		        	resultExecute = pstmt3.executeUpdate();
		        	
		        	debug("신규회원 테이블 인서트 결과 :: resultExecute : " + resultExecute);
		        	resultExecute = 0;
		            
	            }else { //재가입 처리
	            	
	            	if (!chk){ //결과가 1row일때
	            		
	            		chk = true;
	            	
		            	 // 재가입인 경우 기존의 레벨은 모두 삭제한다.
		            	pstmt3 = conn.prepareStatement(exeGradeDel());
						idx = 0;
						pstmt3.setString(++idx, memId ); 
						pstmt3.executeUpdate();
	
		            	// 2단계 회원테이블에 Update	            	
			            pstmt3 = conn.prepareStatement(exeReJoin());
		 				
		 				idx = 0;
		 				pstmt3.setString(++idx, joinChnl );
		 				pstmt3.setString(++idx, cdhd_ctgo_seq_no );
		 				pstmt3.setString(++idx, mobile );
		 				pstmt3.setString(++idx, phone );
		 				pstmt3.setString(++idx, email1 );
		 				pstmt3.setString(++idx, zipcode );
		 				pstmt3.setString(++idx, zipaddr );
		 				pstmt3.setString(++idx, detailaddr );
		 				pstmt3.setString(++idx, memId );
		 	        	resultExecute = pstmt3.executeUpdate();
		 	            
		 	            debug("재가입회원 테이블 업데이트 결과 :: resultExecute : " + resultExecute);
		 	            resultExecute = 0;
	 	            
	            	}else{ //결과가 n개일때 (1row 이상일때)	            		
	            		cnt++;
	            	}
	            	
	            }
	            
				// 회원등급 테이블 업데이트
				//같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
	            idx = 0;
	            pstmt2 = conn.prepareStatement(getChkGradeQuery());				
	            pstmt2.setString(++idx, memId ); 
	            pstmt2.setString(++idx, cdhd_ctgo_seq_no );
	            rs2 = pstmt2.executeQuery();	
				
				if(!rs2.next()){
				    
					idx = 0;
				    pstmt3 = conn.prepareStatement(getInsertGradeQuery());
				    pstmt3.setString(++idx, memId );
				    pstmt3.setString(++idx, cdhd_ctgo_seq_no );					
				    resultExecute = pstmt3.executeUpdate();
				    debug("골프회원등급관리 인서트  결과 :: resultExecute : " + resultExecute);
				    resultExecute = 0;
				    
			        if ( cnt >0 ) {
			            //대표 등급 변경
			            topGradeChange(conn, memId, cdhd_ctgo_seq_no, joinChnl);
			        }				    
		
				}
		        
				// 가입 후 이벤트 테이블 업데이트
	            idx = 0;
	            pstmt3 = conn.prepareStatement(exeUpdOfferEnd());				
	            pstmt3.setString(++idx, memId );
	            pstmt3.setString(++idx, joinChnl );
	            pstmt3.setString(++idx, socId );	        					        	
	        	resultExecute = pstmt3.executeUpdate();
				debug("가입 후 TM오퍼 테이블 업데이트  결과 :: resultExecute : " + resultExecute);
		        
			}	
						
			if(resultExecute > 0) {				
				conn.commit();
				intResult = 1;
			} else {
				conn.rollback();
			}
			
			cdhd_ctgo_seq_no = cdhd_ctgo_seq_no.trim().length() == 0 ? "0" : cdhd_ctgo_seq_no;
			retVals[0] = intResult;
			retVals[1] = Integer.parseInt(cdhd_ctgo_seq_no);;
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
            try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
            
		}			

		return retVals;
	}
	

	/**
	 *	<pre>
	 * 	<li> 대표 등급 업데이트
	 * 	</pre>
	 * @throws DbTaoException 
	 */
	private void topGradeChange(Connection con, String cdhdId, String grade, String joinChnl) throws DbTaoException {
	
		String title				= "대표 등급 업데이트";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		PreparedStatement pstmt2		= null;
		
		int idx = 0;
		
		try {
        	
        	// 현재 자기등급보다 높은 등급이 있는지 알아본다.
            idx = 0;
			pstmt = con.prepareStatement(getGrdChgYN());
			pstmt.setString(++idx, grade);	
			pstmt.setString(++idx, cdhdId);	
			rs = pstmt.executeQuery();
		
			if(rs.next()){
				
				if("Y".equals(rs.getString("CHG_YN"))){
					idx = 0;
					//스마트등급 - 골프회원 테이블 업데이트
					pstmt2 = con.prepareStatement(exeUpdTopGrade());
					pstmt2.setString(++idx, grade);
					pstmt2.setString(++idx, cdhdId);	
					pstmt2.executeUpdate();					
				}					
				
			}
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}            
	        try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
		}
		
	}
	
	
    /**
   	 *	<pre>
  	 * 	<li>  대표등급 변경여부 출력
   	 * 	</pre>
  	 *  @return String 쿼리
  	 */   	
  	private String getGrdChgYN(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
		sql.append("\t  SELECT (CASE WHEN T_CTGO.SORT_SEQ>(SELECT SORT_SEQ FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=?) THEN 'Y' ELSE 'N' END) CHG_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		
  		return sql.toString();
  		
  	}
  	
    /**
   	 *	<pre>
  	 * 	<li>  대표등급 업데이트
   	 * 	</pre>
  	 *  @return String 쿼리
  	 */     	
	private String exeUpdTopGrade(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO = ?	\n");
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}  	  	

    /** ***********************************************************************
    * 스마트 등급 회원이 있는지 조회
    ************************************************************************ */
    private String getSmartMem(){
    	
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT SMART.JUMIN_NO, SMART.RCRU_PL_CLSS	\n");
 		sql.append("\t	, TB_INFO.EMAIL1, TB_INFO.ZIPCODE, TB_INFO.ZIPADDR, TB_INFO.DETAILADDR, TB_INFO.MOBILE, TB_INFO.PHONE, SMART.MEMO_EXPL GRADE	\n");
 		sql.append("\t	FROM  BCDBA.TBACRGCDHDLODNTBL SMART	\n");
 		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON SMART.JUMIN_NO = TB_INFO.SOCID	\n");
 		sql.append("\t	WHERE SMART.SITE_CLSS='02' AND PROC_RSLT_CLSS<>'01'	\n");
 		sql.append("\t	AND SMART.MEMO_EXPL IN (	\n");
 		sql.append("\t							SELECT GOLF_CMMN_CODE	\n");
 		sql.append("\t							FROM BCDBA.TBGCMMNCODE	\n");
 		sql.append("\t							WHERE GOLF_CMMN_CLSS='0064'	\n");
 		sql.append("\t							AND GOLF_CMMN_CODE != '0027'	\n");
 		sql.append("\t							)\n");	
 		sql.append("\t	AND SMART.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  SMART.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND SMART.JUMIN_NO = ?	\n"); 		

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
		sql.append("\t  WHERE JUMIN_NO=? AND CDHD_ID = ?	\n");
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
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	?, ?, ?, ?	\n");
		sql.append("\t  	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y'	\n");
		sql.append("\t  	, 0, 0, ?	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");		
		sql.append("\t  )	\n");
        return sql.toString();
    }
    
    /*************************************************************************
     * 같은 등급이 등록되어 있는지 확인    
     ************************************************************************ */
     private String getChkGradeQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
        sql.append("\t  SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
 		return sql.toString();
     }

     
     /*************************************************************************
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
	* 완료 후 업데이트    
	************************************************************************ */
	private String exeUpdOfferEnd(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS=? AND JUMIN_NO=?	\n");
		return sql.toString();
	}   
	
 	/** ***********************************************************************
	* 골프회원등급 삭제
	************************************************************************ */
	private String exeGradeDel(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}	
	
    /** ***********************************************************************
     * 골프회원정보에 업데이트 - TBGGOLFCDHD => 재가입    
     ************************************************************************ */
     private String exeReJoin(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
 		sql.append("\t  , ACRG_CDHD_JONN_DATE=null, ACRG_CDHD_END_DATE=null			\n");	// 유료회원 입력
 		sql.append("\t  , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= ?		\n");
 		sql.append("\t  , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
 		sql.append("\t  , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  WHERE CDHD_ID=?		\n");
 		
         return sql.toString();
     }
     
}