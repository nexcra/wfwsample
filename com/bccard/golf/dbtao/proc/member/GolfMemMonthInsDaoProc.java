/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemMonthInsDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 월회비 처리 프로세스 
*   			GolfLoginInsProc.java에서 호출 (본 클래스를 콜하기 까지의 샘플 로직) 
*     			if(골프회원){
*     				if(탈회회원){ 
*     					//재가입 회원 처리
*     				}else { //탈회회원 아님 현재 살아있는 회원 처리
*						- 기타 프로세스
*						- 월회비 회원 처리 (GolfMemMonthInsDaoProc.java)   
*     				}
*    			else{ 
*    				//골프회원 아님, 즉, 완전 신규회원 처리 
*    			}
*     
*   적용범위  : golf 
*   작성일자  : 20110620
************************** 수정이력 ****************************************************************
*    일자     작성자   변경사항
*2011.12.28  이경희	  본 클래스는 기존 월회원(스마트시리즈)에 연회원 스마트시리즈도 추가
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.tao.TaoException;

public class GolfMemMonthInsDaoProc extends AbstractProc {
	
	public static final String TITLE = "스마트등급 처리 Proc";	
	
	
	/**
 	 *	<pre>
	 * 	<li> 
 	 * 	</pre>	  
	 *	@param
	 *  @param
	 *  @return    
	 * @throws TaoException 
	 */	
	public int execute_MonthMember(Connection con, String socId, String cdhdId) throws TaoException  {
		
		String title				= "스마트등급  회원인지 알아본다.";
		int whatGrade				= 0;		// 등급
		int whatJoinChnl			= 0;		// 가입경로
		int grd						= 0;		// 오퍼데이터에서 가져온 등급
		int vals[]					= new int[3];
		int successCnt				= 0;
		String end_date 			= "";		// 유료기간 종료일
		boolean chk 				= false;
		
		DbTaoResult result = isMonthMember(con, socId, cdhdId);
		
        while( result.isNext() ){
        	
        	result.next();   		
		
        	vals =  (int[]) result.getObject("retVals"); 
			whatJoinChnl = vals[0];
			whatGrade = vals[1];
			grd	= vals[2];
			
			if (chk) whatGrade = 0;
			
			//grd != 0  가져올 오퍼 데이터 없음
			if ( grd != 0 ){
			
				if(whatGrade==0){ //등급 테이블에 존재하지 않는  TM 오퍼 월회비 회원
					
					successCnt = execute_inGrd(con, socId, cdhdId, grd, whatJoinChnl);
					
				}else if(whatGrade==8){ // white 회원이면  등급으로 업그레이드
					
					chk = true;
					successCnt = execute_upgrade(con, socId, cdhdId, grd, whatJoinChnl);
					
				}else { // 같은 등급이 이미 존재; 일정 늘림 ? ; TM 오퍼 스마트등급 회원 // 업데이트시 채널이 다르면? 채널도업데이트?
					
					successCnt = execute_updExistGrd(con, socId, cdhdId, grd, whatJoinChnl);
					
				}
				
			}
			
        }
	
		return successCnt;	
	}
	
	/**
	 *	<pre>
	 * 	<li> 월회비 등급 대상인지 확인 
	 * 	</pre>
	 *  @return String returnGrd
	 */
	private DbTaoResult isMonthMember(Connection con, String socId, String cdhdId) throws DbTaoException  {
		
		String title				= "스마트등급 확인 : isMonthMember()";
		
		String sql 					= "";
		int grd						= 0; //등급		
		int returnGrd				= 0; //존재등급
				
		ResultSet rs 				= null;		
		PreparedStatement pstmt		= null;
		
		DbTaoResult result =  new DbTaoResult(title);
		DbTaoResult result2 =  new DbTaoResult(title);
		
		try {
			
			//유로회원로딩테이블에서 가입경로와 등급을 가져온다.
			sql = getTMOfferInfo();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			
			if(rs != null ){ 
				while (rs.next()){				
				
					result.addInt("joinChnl", rs.getInt("RCRU_PL_CLSS"));
					result.addInt("grd",	rs.getInt("GRADE"));
					
				}
			}
			
			sql = null;
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
                        
            while( result.isNext() ){
            	
            	result.next();           
            
	            //골프회원등급관리테이블에 같은 등급이 존재하는지
				sql = getMonthGrade();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, cdhdId);
				pstmt.setInt(2, result.getInt("grd"));
				rs = pstmt.executeQuery();			
					
				if(rs.next()){			
					returnGrd = rs.getInt("CDHD_CTGO_SEQ_NO");				
				}else {
					returnGrd = 0; // 같은 등급 없음 의미				
				}
				
				sql = null;
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	
	            //같은 등급 없을때 화이트 등급이 존재하는지
	            if (returnGrd == 0){		
	
					sql = getMonthGrade();			
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, cdhdId);	
					pstmt.setString(2, "8");
					rs = pstmt.executeQuery();
						
					if(rs.next()){
						returnGrd = rs.getInt("CDHD_CTGO_SEQ_NO");							
					}
					
	            }			
	            
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            grd = result.getInt("grd");	       
	            
	            int retVals[] = new int[3];
	            retVals[0] = result.getInt("joinChnl");
	    		retVals[1] = returnGrd;
	    		retVals[2] = grd;
	    			    		
	    		result2.addObject("retVals", retVals);
	    		
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
		}
		
		return result2;
		
	}

	
	/**
	 *	<pre>
	 * 	<li> 가입이나, 탈회회원 재가입시 TM 오퍼 스마트등급 회원 대상인지 알아본다
	 * 	</pre> execute_MonthMember
	 */
	public boolean execute_newJoinMemYN(Connection con, String socId) throws TaoException {

		String title				= "가입이나, 탈회회원 재가입시 TM 오퍼 스마트등급 회원 대상인지 알아본다";
		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;

		boolean flag 				= false;		
				
		try {
			
			//유로회원로딩테이블에서 가입경로와 등급을 가져온다.
			pstmt = con.prepareStatement(getTMOfferInfo());			
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			
			while(rs.next()){ 
				flag = true;
			}

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return flag;
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li> 등급 테이블에 존재하지 않는 TM 오퍼 스마트등급 회원 등록 처리
	 * 	</pre>
	 */
	private int execute_inGrd(Connection con, String socId, String cdhdId, int grade, int joinChnl) throws TaoException {

		String title				= "등급 테이블에 존재하지 않는  TM 오퍼; 스마트등급 회원 등록 처리";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0;
		
		try {
			
			// 등록되어 있지 않다면  인서트 해준다.
			/**SEQ_NO 가져오기**************************************************************/
            pstmt = con.prepareStatement(getNextValQuery());
            rs = pstmt.executeQuery();			
			long max_seq_no = 0L;
			if(rs.next()){
				max_seq_no = rs.getLong("SEQ_NO");
			}
			
            /**Insert************************************************************************/
			pstmt = con.prepareStatement(getInsertGradeQuery());			
        	pstmt.setLong(1, max_seq_no ); 
        	pstmt.setString(2, cdhdId ); 
        	pstmt.setInt(3, grade );
			pstmt.executeUpdate();
        	
            //대표 등급 변경
            topGradeChange(con, cdhdId, grade, joinChnl, "1");
	        
			// 가입 후 유료회원  테이블 업데이트
			idx = 0;
			pstmt = con.prepareStatement(exeUpdOfferEnd());
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, joinChnl );
        	pstmt.setString(++idx, socId );
        	result = pstmt.executeUpdate();
        	if(pstmt != null) pstmt.close();
	        
	        info(" || cdhdId : "+ cdhdId + " | 등급 테이블에 존재하지 않는  TM 오퍼; 스마트등급 회원 등록 처리 완료 ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> White -> 스마트등급 등급으로 업데이트  // 카드 등급인데 기간을 넣어야 하는지 다시 확인-안넣을거 같음
	 * 	</pre>
	 *  @return int 결과 cnt
	 */
	private int execute_upgrade(Connection con, String socId, String cdhdId, int grade, int joinChnl) throws TaoException {

		String title				= "White -> 스마트등급으로 업데이트";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0;
		
		try {
			
			//변경전 전 등급 히스토리에 기록
			idx = 0;
			pstmt = con.prepareStatement(inGrdHistoryQuery());			
        	pstmt.setString(++idx, cdhdId );
        	pstmt.executeUpdate();			
            if(pstmt != null) pstmt.close();        	

    		// 월회비 등급 - 등급  테이블 업그레이드				
			idx = 0;
			pstmt = con.prepareStatement(exeUpdGrd());
        	pstmt.setInt(++idx, grade );
        	pstmt.setString(++idx, cdhdId );
        	pstmt.executeUpdate();	
            if(pstmt != null) pstmt.close();
        	
            //대표 등급 변경
            topGradeChange(con, cdhdId, grade, joinChnl, "2");
	        
			// 가입 후 유료회원  테이블 업데이트
			idx = 0;
			pstmt = con.prepareStatement(exeUpdOfferEnd());
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, joinChnl );
        	pstmt.setString(++idx, socId );
        	result = pstmt.executeUpdate();
        	if(pstmt != null) pstmt.close();
	        
	        info(" || cdhdId : "+ cdhdId + " | White -> 스마트등급 으로 업데이트 완료 ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li> 스마트등급이 존재시 새로운 정보가 들어오므로 기간 업데이트 
	 * 	</pre>
	 */	
	private int execute_updExistGrd(Connection con, String socId, String cdhdId, int grade, int joinChnl) throws TaoException {

		String title				= "스마트등급이 존재시 새로운 정보가 들어오므로 기간 업데이트";		
		PreparedStatement pstmt		= null;
		
		int idx = 0;
		int result = 0;		
		
		try {
			
        	//TBGGOLFCDHDGRDMGMT(골프회원등급관리) 업데이트
			idx = 0;			
			pstmt = con.prepareStatement(exeUpdExistGrd());				
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, grade );
        	pstmt.executeUpdate();        	
        	if(pstmt != null) pstmt.close();
        	
			// 가입 후 유료회원  테이블 업데이트
			idx = 0;
			pstmt = con.prepareStatement(exeUpdOfferEnd());
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, joinChnl );
        	pstmt.setString(++idx, socId );
        	result = pstmt.executeUpdate();
        	if(pstmt != null) pstmt.close();
        	
        	info(" || cdhdId : "+ cdhdId + " | 스마트등급이 존재시 새로운 정보가 들어오므로 기간 업데이트  완료 ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}			


	/**
	 *	<pre>
	 * 	<li> 대표 등급 업데이트
	 * 	</pre>
	 * @throws DbTaoException 
	 */
	private void topGradeChange(Connection con, String cdhdId, int grade, int joinChnl, String gubun) throws DbTaoException {
	
		String title				= "대표 등급 업데이트";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		PreparedStatement pstmt2		= null;
		
		int idx = 0;
				
		try {
        	
        	// 현재 자기등급보다 높은 등급이 있는지 알아본다.
            idx = 0;
			pstmt = con.prepareStatement(getGrdChgYN());
			pstmt.setInt(++idx, grade);	
			pstmt.setString(++idx, cdhdId);	
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				
				if("Y".equals(rs.getString("CHG_YN"))){
					idx = 0;
					//스마트등급 - 골프회원 테이블 업데이트
					pstmt2 = con.prepareStatement(exeUpdTopGrade(gubun));
					pstmt2.setInt(++idx, grade);	
					if (gubun.equals("2")){
						pstmt2.setInt(++idx, joinChnl );
					}
					pstmt2.setString(++idx, cdhdId);	
					pstmt2.executeUpdate();					
				}					
				//N 일때, 조인체널은 어떻게 하나?
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
	 * 	<li> 유로회원로딩테이블에서 가입 가능한 회원의 가입경로와 등급을 가져온다.
	 * 	<li> 스마트 등급 {(Smart150, 200, 500, 캠패인기간은 3개월), (NH스마트5000  CAMP_END_DATE날짜로 유료회원 종료기간 설정)}
	 * 	</pre>
	 *  @return String 쿼리
	 */		
	private String getTMOfferInfo(){
		 
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
  		sql.append("\t	SELECT OFFER.RCRU_PL_CLSS, OFFER.MEMO_EXPL GRADE	\n");
  		sql.append("\t	FROM BCDBA.TBACRGCDHDLODNTBL OFFER	\n");
  		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON OFFER.JUMIN_NO = TB_INFO.SOCID	\n");
  		sql.append("\t	WHERE OFFER.SITE_CLSS='02' AND PROC_RSLT_CLSS<>'01' \n");
  		sql.append("\t	AND MEMO_EXPL IN ( \n");
  		sql.append("\t						SELECT GOLF_CMMN_CODE	 	\n");
  		sql.append("\t	 					FROM BCDBA.TBGCMMNCODE		\n");
  		sql.append("\t	 					WHERE GOLF_CMMN_CLSS='0064'	\n");
  		sql.append("\t	 					AND GOLF_CMMN_CODE != '0027'	\n");
  		sql.append("\t	) \n");  		
  		sql.append("\t	AND OFFER.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  OFFER.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
  		sql.append("\t	AND OFFER.JUMIN_NO = ?	\n");
		
		return sql.toString();
	
	}
     
     
 	/**
  	 *	<pre>
 	 * 	<li> 골프회원등급관리테이블에 같은 등급이 존재하는지
  	 * 	</pre>
 	 *  @return String 쿼리
 	 */
	private String getMonthGrade(){
		
		StringBuffer sql = new StringBuffer();

		sql.append("	\n");
		sql.append("\t	SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD		\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTG ON GRD.CDHD_CTGO_SEQ_NO=CTG.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE CDHD_ID = ? AND CTG.CDHD_CTGO_SEQ_NO = ?	\n");
		
		return sql.toString();
	
	}
	
	
 	/**
  	 *	<pre>
 	 * 	<li> 등급 히스토리 테이블 인서트    
  	 * 	</pre>
 	 *  @return String 쿼리
 	 */	
 	private String inGrdHistoryQuery(){
 		
 		StringBuffer sql = new StringBuffer();
 		
 		sql.append("	\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
 		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID	\n");
 		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002'	\n");
 		
 		return sql.toString(); 		
 		
 	}

     
    /**
   	 *	<pre>
  	 * 	<li> 등급변경 REG_ATON 도 넣어야 하지 않나?
   	 * 	</pre>
  	 *  @return String 쿼리
  	 */     
  	private String exeUpdGrd(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
  		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
  		sql.append("\t  , REG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT = NULL	 \n"); 
  		sql.append("\t  WHERE CDHD_GRD_SEQ_NO=(	\n");
  		sql.append("\t      SELECT GRD.CDHD_GRD_SEQ_NO	\n");
  		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
  		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t      WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002' AND GRDM.CDHD_CTGO_SEQ_NO = '8'	\n");
  		sql.append("\t  )	\n");

  		return sql.toString();
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
	private String exeUpdTopGrade(String gubun){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO = ?	\n");	
		if (gubun.equals("2")){
			sql.append("\t  , JOIN_CHNL = ?	\n");
		}
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}  	
     
	/**
	 *	<pre>
	 * 	<li>  스마트등급 완료 후 업데이트
	 * 	</pre>
	 *  @return String 쿼리
	 */	 
	private String exeUpdOfferEnd(){
		 
		StringBuffer sql = new StringBuffer();
			 
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS=? AND JUMIN_NO=?	\n");		
		
		return sql.toString();
	
	}	
     

 	/**
 	 *	<pre>
 	 * 	<li> 스마트등급 같은 등급 이미 존재시 업데이트 (기간)  
 	 * 	</pre>
 	 *  @return String 쿼리
 	 */	 	     
  	private String exeUpdExistGrd(){
  		
  		//같은등급을 변경 하므로 변경일시만 수정
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
  		sql.append("\t  SET CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
  		sql.append("\t  , REG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");  
  		sql.append("\t  , CHNG_RSON_CTNT = NULL								\n");   
  		sql.append("\t  WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?				\n"); 		 		
  		

  		return sql.toString();
  		
  	} 
  	
  	
 	/**
 	 *	<pre>
 	 * 	<li> Max IDX Query를 생성하여 리턴한다. = 골프회원등급관리
 	 * 	</pre>
 	 *  @return String 쿼리
 	 */
  	private String getNextValQuery(){
	
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n");
		sql.append("SELECT NVL(MAX(CDHD_GRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
		
		return sql.toString();
		
	}

  	
 	/**
 	 *	<pre>
 	 * 	<li> 골프회원등급관리 인서트 - TBGGOLFCDHDGRDMGMT 
 	 * 	</pre>
 	 *  @return String 쿼리
 	 */  	
     private String getInsertGradeQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (							\n");
 		sql.append("\t  		CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
 		sql.append("\t  		) VALUES (												\n");
 		sql.append("\t  		?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')			\n");
 		sql.append("\t  		)														\n");
         return sql.toString();
     }  	

}
