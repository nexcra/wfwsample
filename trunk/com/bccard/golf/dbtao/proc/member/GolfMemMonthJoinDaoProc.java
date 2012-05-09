/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemMonthJoinDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 가입 > 월회원 결제 가입
*   적용범위  : golf 
*   작성일자  : 20110622
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;

public class GolfMemMonthJoinDaoProc extends AbstractProc {
	

	public static final String TITLE = "월회원(스마트등급) 가입처리";

	public GolfMemMonthJoinDaoProc() {}
		
	
	/**
	 *	<pre>
	 * 	<li> 월회원(스마트등급) 가입처리
	 * 	</pre>
	 *  @return String returnGrd
	 */		
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		
		String sql = "";
		Connection conn = null;
		ResultSet rs = null;		
		PreparedStatement pstmt = null;
		
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();			
			String socId				= userEtt.getSocid();
				
			String moneyType 			= data.getString("moneyType").trim();	
			if(GolfUtil.empty(moneyType)){moneyType = "4";}	
			
			String cdhd_sq2_ctgo = GolfUtil.lpad(moneyType+"", 4, "0");// 웹을 통해 신청한 등급
			String cdhd_ctgo_seq_no = "";
			String sece_yn = ""; // Y:탈회회원, N:로그인가능회원
			String newMemYn = "N"; // 신규회원 여부
			
			debug("GolfMemMonthJoinDaoProc / moneyType : " + moneyType);		
			
			if(!"".equals(cdhd_sq2_ctgo) && cdhd_sq2_ctgo != null)
			{						
				//신청한 등급이 골프회원분류관리 테이블에 존재하는지 확인 및 코드 변환
				sql = this.getMemberLevelQuery();  
	            pstmt = conn.prepareStatement(sql);	        	
	        	pstmt.setString(1, cdhd_sq2_ctgo );
	            rs = pstmt.executeQuery();	
	            
				if(rs.next()){
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO"); //회원등급
				}
				
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            if(!"".equals(cdhd_ctgo_seq_no) && cdhd_ctgo_seq_no != null){
	            
	            	//이미 등록된 회원인지 알아본다. (등록되어 있는 아이디가 있는지 검색);
					sql = this.getMemberedCheckQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, socId );
		        	pstmt.setString(2, memId );
		            rs = pstmt.executeQuery();	
		            
					if(rs.next()){// 탈회 Y 이나 탈회 N 존재
						sece_yn = rs.getString("SECE_YN");						
					}else {// 신규회원
						newMemYn = "Y";
					}
					
					if ( newMemYn.equals("Y")||sece_yn.equals("Y") ){
						
						// 신규가입or재가입처리
						result = newMonthMemJoin(conn, cdhd_ctgo_seq_no, request); 
						
					}else {
						
						//로그인 가능한 회원 처리
						result = exeMonthMember(conn, socId, memId); 
						
					}
					
					if (result==1){
						result = mnInsExecute(conn, memId);
					}
		            
	            }
	            
			}			
			
			if(result > 0) {				
				conn.commit();
				debug("GolfMemMonthJoinDaoProc memId:"+memId+"/월회원 등록 성공");		
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
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}			
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li> 월결제 저장하기 
	 * 	</pre>
	 *  @return int intResult
	 */		
	public int mnInsExecute(Connection conn, String memId) throws BaseException {
		
		int idx = 0;
		int result =  0;
		int intResult = 0;
		PreparedStatement pstmt = null;		

		try {
			
			String sql = "";
			String sttl_amt	= AppConfig.getDataCodeProp("monPay");// 결제금액
			
            sql = this.getPayMonthQuery();
			pstmt = conn.prepareStatement(sql);        	 
        	pstmt.setString(++idx, AppConfig.getDataCodeProp("monPayHis") );
        	pstmt.setString(++idx, memId );
        	pstmt.setString(++idx, sttl_amt );	
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();

			if(result > 0) {	
				intResult = 1;
			}

		} catch(Exception e) {
			
			try	{
				conn.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "월회원 이력 등록 실패" );
	        throw new DbTaoException(msgEtt,e);
	        
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> 월결제 저장하기 
	 * 	</pre>
	 *  @return int intResult
	 */		
	public int getSeq(WaContext context, String memId) throws BaseException {	
		
		int idx = 0;				
		int seq = 0;
		PreparedStatement pstmt = null;
		
		String sql = "";
		Connection conn = null;
		ResultSet rs = null;		

		try {
			
			conn = context.getDbConnection("default", null);

            idx = 0;
			pstmt = conn.prepareStatement(getMonPaySeq());
			pstmt.setString(++idx, memId );			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				
				seq = rs.getInt("SEQ");
			}			
			
			rs.close();
			

		} catch(Exception e) {
			
			try	{
				conn.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "월회원 이력 등록 실패" );
	        throw new DbTaoException(msgEtt,e);
	        
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return seq;
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> 신규가입이나 재가입처리
	 * 	</pre>
	 *  @return int intResult
	 */		
	public int newMonthMemJoin(Connection conn, String grd, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;		

		String sql = "";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;		
		
		ResultSet userInfoRs = null;
		PreparedStatement userInfoPstmt = null;
		
		int resultExecute = 0;
		int intResult = 0;
		
		String joinChnl				= "";	// 골프회원테이블 가입경로구분 코드
		String cdhd_ctgo_seq_no		= "";	// 회원분류일련번호 = 대표등급
		String email 				= "";	// 이메일주소
		String zipcode 				= "";	// 우편번호
		String zipaddr 				= "";	// 주소1
		String detailaddr 			= "";	// 주소2
		String mobile 				= "";	// 모바일
		String phone 				= "";	// 집전화		
						
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId 				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();
			
			sql = this.getUserInfoQuery(strMemClss);  	// 회원등급번호 1:개인 / 5:법인
            userInfoPstmt = conn.prepareStatement(sql);
            userInfoPstmt.setString(1, memId );
            userInfoRs = userInfoPstmt.executeQuery();
            
            //회원 정보
			if(userInfoRs.next()){
				joinChnl 			= AppConfig.getDataCodeProp("monjoinChnl"); 
				cdhd_ctgo_seq_no 	= grd ;
				email				= userInfoRs.getString("EMAIL");		// 이메일
				zipcode				= userInfoRs.getString("ZIPCODE");		// 우편번호
				zipaddr				= userInfoRs.getString("ZIPADDR");		// 주소
				detailaddr			= userInfoRs.getString("DETAILADDR");	// 상세주소
				mobile				= userInfoRs.getString("MOBILE");		// 핸드폰번호
				phone				= userInfoRs.getString("PHONE");		// 전화번호				
			}
			
			if(userInfoRs != null) userInfoRs.close();
            if(userInfoPstmt != null) userInfoPstmt.close(); 
			
			// 이미 가입된 ID인지 체크
			pstmt = conn.prepareStatement(getMemberedCheckQuery());
			pstmt.setString(1, socId );
			pstmt.setString(1, memId );
        	rs = pstmt.executeQuery();
			
            if(!rs.next()){// 신규가입처리
		        
				// 골프회원테이블 인서트 - 대표등급, 회원정보, 최근접속일자 등등
            	idx = 0;
            	pstmt2 = conn.prepareStatement(getInsertMemQuery());				
				pstmt2.setString(++idx, memId );
				pstmt2.setString(++idx, memNm );
				pstmt2.setString(++idx, socId );
				pstmt2.setString(++idx, joinChnl );
				pstmt2.setString(++idx, strMemClss );
				pstmt2.setString(++idx, cdhd_ctgo_seq_no );
				pstmt2.setString(++idx, mobile );
				pstmt2.setString(++idx, phone );
				pstmt2.setString(++idx, email );
				pstmt2.setString(++idx, zipcode );
				pstmt2.setString(++idx, zipaddr );
				pstmt2.setString(++idx, detailaddr );		        					        	
	        	resultExecute = pstmt2.executeUpdate();
	        	if(pstmt2 != null) pstmt2.close();	        	
	
	        	info(" 신규회원 테이블 인서트 결과 :: cdhdId : "+ memId + " | resultExecute : " + resultExecute);
	        	resultExecute = 0;
	            
            }else { //재가입 처리
            	
            	// 재가입인 경우 기존의 레벨은 모두 삭제한다.
            	idx = 0;
            	pstmt2 = conn.prepareStatement(exeGradeDel());				
				pstmt2.setString(++idx, memId ); 
				pstmt2.executeUpdate();
				
	            if(pstmt2 != null) pstmt2.close();

            	// 골프회원테이블에 Update	      
	            idx = 0;
	            pstmt2 = conn.prepareStatement(exeReJoin());
	            pstmt2.setString(++idx, joinChnl );
	            pstmt2.setString(++idx, cdhd_ctgo_seq_no );
	            pstmt2.setString(++idx, mobile );
	            pstmt2.setString(++idx, phone );
	            pstmt2.setString(++idx, email );
	            pstmt2.setString(++idx, zipcode );
	            pstmt2.setString(++idx, zipaddr );
	            pstmt2.setString(++idx, detailaddr );
	            pstmt2.setString(++idx, memId );
 	        	resultExecute = pstmt2.executeUpdate();
 	            if(pstmt2 != null) pstmt2.close();
 	            
 	            info(" 재가입회원 테이블 인서트 결과 :: cdhdId : "+ memId + " | resultExecute : " + resultExecute);
 	            resultExecute = 0;
 	            
            }
            
	        if(rs != null) rs.close();
	        if(pstmt != null) pstmt.close();

			/*같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
            	신규는 아예 없고, 재가입은 위에서 삭제한는데 굳이 또 검사? 혹시나? */
            idx = 0;
            pstmt = conn.prepareStatement(getChkGradeQuery());				
            pstmt.setString(++idx, memId ); 
            pstmt.setString(++idx, cdhd_ctgo_seq_no );
            rs = pstmt.executeQuery();

			if(!rs.next()){
			    
				// 회원등급 테이블 업데이트
				idx = 0;
			    pstmt2 = conn.prepareStatement(getInsertGradeQuery());
			    pstmt2.setString(++idx, memId ); 
			    pstmt2.setString(++idx, cdhd_ctgo_seq_no );					
			    resultExecute = pstmt2.executeUpdate();
			    if(pstmt2 != null) pstmt2.close();			    
			    info(" 골프회원등급관리 테이블 인서트 결과 :: cdhdId : "+ memId + " | resultExecute : " + resultExecute);
	
			}	      
			
	        if(rs != null) rs.close();
	        if(pstmt != null) pstmt.close();				
	        
			if(resultExecute > 0) {
				intResult = 1;
			} 
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "신규가입이나 재가입처리 오류" );
            throw new DbTaoException(msgEtt,e);
		} finally {
			
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(userInfoRs != null) userInfoRs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}			
            try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
            try { if(userInfoPstmt != null) userInfoPstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return intResult;
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> 로그인 가능한 회원 처리
	 * 	</pre>
	 *  @return int intResult	 
	 * @throws IOException 
	 * @throws NumberFormatException 
	 *  
	 */		
	public int exeMonthMember(Connection con, String socId, String cdhdId) throws TaoException, NumberFormatException, IOException  {

		String title				= "월회비  회원인지 알아본다.";

		int grd						= Integer.parseInt(AppConfig.getDataCodeProp("0052CODE11"));// 개인스마트 등급
		int whatGrade				= 0;		// 등급		
		int successCnt				= 0;
		
		whatGrade = isMonthMember(con, grd, socId, cdhdId);  
		
		info("분기코드 : " + whatGrade);
		
		if(whatGrade==0){ //등급 테이블에  같은 등급 존재하지 않아 등급 넣어준다				
			successCnt = execute_inGrd(con, socId, cdhdId, grd);			
		}else if(whatGrade==8){ // white 회원이면 스마트 등급으로 업그레이드			
			successCnt = execute_upgrade(con, socId, cdhdId, grd);
		}else { 
			/* 
			       같은 등급이 이미 존재; '이미 월회원에 가입 되셨습니다.'라는 메세지 리턴;
			       같은 등급  존재시 결제전 자바 스크립트에서 막으나 , 혹시 뚫고 들어왔을때   리턴한다.
			 */
			successCnt = 0;			
		}			
	
		return successCnt;	 
		
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> 월회비 등급 대상인지 확인 
	 * 	</pre>
	 *  @return String returnGrd
	 */		
	private  int isMonthMember(Connection con, int grd, String socId, String cdhdId) throws DbTaoException  {
		
		String title				= "월회비 등급 대상인지 확인 : isMonthMember()";
		
		String sql 					= "";
		int returnGrd				= 0;  
		ResultSet rs 				= null;		
		PreparedStatement pstmt		= null;
		
		try {
            
            //골프회원등급관리테이블에 같은 등급이 존재하는지
			sql = getMonthGrade();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, cdhdId);	
			pstmt.setInt(2, grd);
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
		
		return returnGrd;			
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li>  등급 테이블에  같은 등급 존재하지 않아 신규로 in
	 * 	</pre>
	 */
	private int execute_inGrd(Connection con, String socId, String cdhdId, int grade) throws TaoException {

		String title				= "등급 등록 처리 완료";
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0, intResult =0;
		
		try {
			
			// 등록되어 있지 않다면  인서트 해준다.
            /**Insert************************************************************************/
			pstmt = con.prepareStatement(getInsertGradeQuery());
        	pstmt.setString(++idx, cdhdId ); 
        	pstmt.setInt(++idx, grade );
        	result = pstmt.executeUpdate();
        	
            //대표 등급 변경
			topGradeChange(con, cdhdId, grade, "1");
            
	        info(" || cdhdId : "+ cdhdId + " | 신규로 in ; 월회비 회원 등록 처리 완료 ");
	        
			if(result > 0) {
				intResult = 1;
			} 
			
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
	private void topGradeChange(Connection con, String cdhdId, int grade, String gubun) throws DbTaoException {
	
		String title				= "대표 등급 업데이트";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		PreparedStatement pstmt2		= null;
		
		int idx = 0;
		String joinChnl				= "";	// 골프회원테이블 가입경로구분 코드
		
		try {
        	
        	// 현재 자기등급보다 높은 등급이 있는지 알아본다.
            idx = 0;
			pstmt = con.prepareStatement(getGrdChgYN());
			pstmt.setInt(++idx, grade);	
			pstmt.setString(++idx, cdhdId);	
			rs = pstmt.executeQuery();
			
			idx = 0;
			
			if(rs.next()){
				
				joinChnl 			= AppConfig.getDataCodeProp("monjoinChnl");
				
				if("Y".equals(rs.getString("CHG_YN"))){
					//월회비 등급 - 골프회원 테이블 업데이트
					pstmt2 = con.prepareStatement(exeUpdTopGrade(gubun));
					pstmt2.setInt(++idx, grade);
					if (gubun.equals("2")){
						pstmt2.setString(++idx, joinChnl);
					}
					pstmt2.setString(++idx, cdhdId);	
					pstmt2.executeUpdate();		
			        info(" || cdhdId : "+ cdhdId + " | 대표등급 ["+grade +"]으로 변경");
				}					
				//N 일때 가입경로 업데이트 못한다.
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
	 * 	<li> White -> 월회비 등급으로 업데이트
	 * 	</pre>
	 *  @return int 결과 cnt
	 */
	private int execute_upgrade(Connection con, String socId, String cdhdId, int grade) throws TaoException {

		String title				= "White -> 월회비 등급으로 업데이트";
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0, intResult = 0;
		
		try {
			
			//변경전 전 등급 히스토리에 기록
			idx = 0;
			pstmt = con.prepareStatement(inGrdHistoryQuery());		
        	pstmt.setString(++idx, cdhdId );
        	pstmt.executeUpdate();			
            if(pstmt != null) pstmt.close();        	
  
    		//등급  테이블 업그레이드				
			idx = 0;
			pstmt = con.prepareStatement(exeUpdGrd());
        	pstmt.setInt(++idx, grade );
        	pstmt.setString(++idx, cdhdId );
        	result = pstmt.executeUpdate();	
            if(pstmt != null) pstmt.close();
         	
            //대표 등급 변경
            topGradeChange(con, cdhdId, grade, "2");
            
			if(result > 0) {
				intResult = 1;
			} 
	        
	        info(" || cdhdId : "+ cdhdId + " | White -> 월회비(스마트)등급으로 업데이트 완료 ");
			
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
	 * 	<li> 회원 분류 정보 가져오기 - TBGGOLFCDHDCTGOMGMT 
	 * 	</pre>
	 *  @return String 쿼리
	 */		
	private String getMemberLevelQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM				\n");
		sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
		sql.append("\t    WHERE CDHD_SQ2_CTGO=?						\n");
		return sql.toString();
	}
	

	/**
	 *	<pre>
	 * 	<li> 현재등록된 아이디인지 알아보기 
	 * 	</pre>
	 *  @return String 쿼리
	 */		
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD							\n");
		sql.append("\t  WHERE JUMIN_NO=? AND CDHD_ID=?					\n");
		return sql.toString();
	}
		
	
	/**
	 *	<pre>
	 * 	<li> 회원정보 가져오기    strMemClss // 회원등급번호 1:개인 / 5:법인
	 * 	</pre>
	 *  @return String 쿼리
	 */			
	private String getUserInfoQuery(String strMemClss){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("1".equals(strMemClss)){
			
			sql.append("\t  SELECT EMAIL1 EMAIL, ZIPCODE, ZIPADDR, DETAILADDR, MOBILE, PHONE	\n");
			sql.append("\t  FROM BCDBA.UCUSRINFO	\n");
			sql.append("\t  WHERE ACCOUNT = ?	\n");
			
		}else{					

			sql.append("\t  SELECT CMEM.USER_EMAIL EMAIL, CMEM.USER_MOB_NO MOBILE, CMEM.USER_TEL_NO PHONE	\n");
			sql.append("\t  , NMEM.ZIPCODE, NMEM.ZIPADDR, NMEM.DETAILADDR	\n");
			sql.append("\t  FROM BCDBA.TBENTPUSER CMEM	\n");
			sql.append("\t  LEFT JOIN BCDBA.UCUSRINFO NMEM ON CMEM.ACCOUNT=NMEM.ACCOUNT	\n");
			sql.append("\t  WHERE CMEM.ACCOUNT=?	\n");
			
		}
		
		return sql.toString();
	}	
	

	/**
	 *	<pre>
	 * 	<li> 골프회원정보에 인서트 - TBGGOLFCDHD    
	 * 	</pre>
	 *  @return String 쿼리
	 */		
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
		sql.append("\t  	, 0, 0, ?	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");		
		sql.append("\t  )	\n");
        return sql.toString();
    }	
    
    
 	/**
 	 *	<pre>
 	 * 	<li> 골프회원등급 삭제
 	 * 	</pre>
 	 *  @return String 쿼리
 	 */     
	private String exeGradeDel(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}    
	
	
 	/**
 	 *	<pre>
 	 * 	<li>  골프회원정보에 업데이트 - TBGGOLFCDHD => 재가입  
 	 * 	</pre>
 	 *  @return String 쿼리
 	 */ 
     private String exeReJoin(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
 		sql.append("\t  , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE+365,'YYYYMMDD')			\n");	// 유료회원 입력
 		sql.append("\t  , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= ?		\n");
 		sql.append("\t  , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
 		sql.append("\t  , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  WHERE CDHD_ID=?		\n");
 		
         return sql.toString();
     }	

     
 	/**
 	 *	<pre>
 	 * 	<li>  같은 등급이 등록되어 있는지 확인    
 	 * 	</pre>
 	 *  @return String 쿼리
 	 */      
	private String getChkGradeQuery(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t  SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
		
		return sql.toString();
		
	}
      
     
	/**
	 *	<pre>
	 * 	<li>  골프회원등급관리 인서트 - TBGGOLFCDHDGRDMGMT
	 * 	</pre>
	 *  @return String 쿼리
	 */       
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
  	 * 	<li> 화이트를 스마트로 업데이트시에는 REG_ATON를 업데이트 한다
  	 * 	<li> REG_ATON는 매월 자동결제시 기준 데이터가 되므로 꼭 넣는다
  	 *  <li> 기존 멤버쉽 변경시는 REG_ATON(등록일)는 업데이트 안하고, CHNG_ATON(변경일)만 없데이트 함
  	 *  <li> 개인 스마트는 한 번 가입하면 해지할때까지, 무한 결제 이므로 의미는 없느나 오퍼 스마트와의 일관성을 위해 REG_ATON를 업데이트
   	 * 	</pre>
  	 *  @return String 쿼리
  	 */     
  	private String exeUpdGrd(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
  		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 	\n");
  		sql.append("\t  REG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT = NULL		\n");
  		sql.append("\t  WHERE CDHD_GRD_SEQ_NO=(	\n");
  		sql.append("\t      SELECT GRD.CDHD_GRD_SEQ_NO	\n");
  		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
  		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t      WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002' AND GRDM.CDHD_CTGO_SEQ_NO = '8'	\n");
  		sql.append("\t  )	\n");
	
  		return sql.toString();
  	} 
  	
  	
 	/** ***********************************************************************
	* 월결제 등록하기
	************************************************************************ */
  	
    /**
   	 *	<pre>
  	 * 	<li> 월결제 등록하기
   	 * 	</pre>
  	 *  @return String 쿼리
  	 */      	
	private String getPayMonthQuery(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");			
		sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_LESN_RSVT_NO	\n");
		sql.append("\t  , GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT, RSVT_CDHD_GRD_SEQ_NO)	\n");
		sql.append("\t  (SELECT MAX(APLC_SEQ_NO)+1, 1, ?, 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
		sql.append("\t  , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),? , 22	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT)	\n");
		
		return sql.toString();
		
	}  	
	

    /**
   	 *	<pre>
  	 * 	<li>  월결제 등록 시퀀스 가져오기 
   	 * 	</pre>
  	 *  @return String 쿼리
  	 */   	
  	private String getMonPaySeq(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
		sql.append("\t  SELECT MAX(APLC_SEQ_NO)SEQ FROM BCDBA.TBGAPLCMGMT \n");
		sql.append("\t  WHERE GOLF_SVC_APLC_CLSS = '1004'	\n");
		sql.append("\t  AND CDHD_ID=?	\n");
		
  		return sql.toString();
  		
  	}   	
      
}
