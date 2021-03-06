/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemCardInsDaoProc
*   작성자    : (주)미디어포스 권영만
*   내용      : 회원 > 회원가입처리 > 카드회원 가입
*   적용범위  : golf 
*   작성일자  : 2009-08-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfMemCardInsDaoProc extends AbstractProc {

	public static final String TITLE = "회원가입처리 > 카드회원 가입";

	public GolfMemCardInsDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;

				
		try {
			String strCode 				= data.getString("strCode").trim();	
			String cdhd_SQ2_CTGO		= GolfUtil.lpad(strCode+"", 4, "0");			
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();		// 회원Clss  2009.10.30 추가 .getStrMemChkNum()
			
			String joinMode 			= StrUtil.isNull(data.getString("joinMode"), "");
			debug("## joinMode : "+joinMode);
			
			String cdhd_SQ1_CTGO		= "0001";				// cdhd_SQ1_CTGO -> 0001:골프카드고객 0002:멤버쉽고객

			String cdhd_CTGO_SEQ_NO		= "";
			
			String memEmail				= "";	// 이메일
			String memZipCode			= "";	// 우편번호
			String memZipAddr			= "";	// 주소
			String memDetailAddr		= "";	// 상세주소
			String memMobile			= "";	// 핸드폰번호
			String memPhone				= "";	// 전화번호
			
			//등급
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");		// 9
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            // 회원정보 가져오기
			sql = this.getUserInfoQuery(strMemClss);  	// 회원등급번호 1:개인 / 5:법인
            userInfoPstmt = conn.prepareStatement(sql);
            userInfoPstmt.setString(1, memId );
            userInfoRs = userInfoPstmt.executeQuery();	
			if(userInfoRs.next()){
				memEmail			= userInfoRs.getString("EMAIL");	// 이메일
				memZipCode			= userInfoRs.getString("ZIPCODE");	// 우편번호
				memZipAddr			= userInfoRs.getString("ZIPADDR");	// 주소
				memDetailAddr		= userInfoRs.getString("DETAILADDR");	// 상세주소
				memMobile			= userInfoRs.getString("MOBILE");	// 핸드폰번호
				memPhone			= userInfoRs.getString("PHONE");	// 전화번호
			}
			if(userInfoRs != null) userInfoRs.close();
            if(userInfoPstmt != null) userInfoPstmt.close();
            
            
			// 1단계 이미 가입된 ID인지 체크 
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
        	pstmt.setString(2, memId );
            rs = pstmt.executeQuery();	
			
            if(!rs.next())	// 신규가입처리
            {	
            	// 2단계 회원테이블에 Insert
            	if("acrgJoin".equals(joinMode))
            	{
            		sql = this.getInsertAcrgJoinMemQuery();
            	}
            	else
            	{
            		sql = this.getInsertMemQuery();
            	}
 				pstmt = conn.prepareStatement(sql);
 				
 				idx = 0;
 				pstmt.setString(++idx, memId ); 
 	        	pstmt.setString(++idx, memNm );
 	        	pstmt.setString(++idx, socId );
 	        	//pstmt.setString(++idx, cdhd_SQ2_CTGO );	// join_chul
 	        	pstmt.setString(++idx, strMemClss );
				
				pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				pstmt.setString(++idx, memMobile );
				pstmt.setString(++idx, memPhone );
				pstmt.setString(++idx, memEmail );
				pstmt.setString(++idx, memZipCode );
				pstmt.setString(++idx, memZipAddr );
				pstmt.setString(++idx, memDetailAddr );
 	        	
 				result = pstmt.executeUpdate();
 	            if(pstmt != null) pstmt.close();
 	            
            	
            }
            else	// 재가입 처리
            {

	            // 재가입인 경우 기존의 레벨은 모두 삭제한다. getGradeDelQuery
        		sql = this.getGradeDelQuery();
				pstmt = conn.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, memId ); 
				pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();

            	// 2단계 회원테이블에 Update	            	            
	            if("acrgJoin".equals(joinMode))
            	{
	            	sql = this.getReInsertAcrgJoinMemQuery();
            	}
            	else
            	{
            		sql = this.getReInsertMemQuery();
            	}
            	
 				pstmt = conn.prepareStatement(sql);
 				
 				idx = 0;				
				//pstmt.setString(++idx, cdhd_SQ2_CTGO );
				pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				pstmt.setString(++idx, memMobile );
				pstmt.setString(++idx, memPhone );
				pstmt.setString(++idx, memEmail );
				pstmt.setString(++idx, memZipCode );
				pstmt.setString(++idx, memZipAddr );
				pstmt.setString(++idx, memDetailAddr );
 	        	pstmt.setString(++idx, memId ); 
 	        	
 				result = pstmt.executeUpdate();
 	            if(pstmt != null) pstmt.close();
            }
            
            
        	
            // 3단계 같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
			sql = this.getChkGradeQuery(); 
        	pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, memId ); 
    		pstmt.setString(2, cdhd_CTGO_SEQ_NO );
        	rs = pstmt.executeQuery();	
        	if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0"))
        	{
            	 /**SEQ_NO 가져오기**************************************************************/
				sql = this.getNextValQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				long max_seq_no = 0L;
				if(rs.next()){
					max_seq_no = rs.getLong("SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            /**Insert************************************************************************/
	            sql = this.getInsertGradeQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
	        	pstmt.setLong(++idx, max_seq_no ); 
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
	        	
				pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
            }
                       
			
			            
				        
			if(rs != null) rs.close();
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

	public int vipCardMemIns(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;

				
		try {
			String strCode 				= data.getString("strCode").trim();	
			String cdhd_SQ2_CTGO		= GolfUtil.lpad(strCode+"", 4, "0");			
			
			
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();		// 회원Clss  2009.10.30 추가 .getStrMemChkNum()
			
			String cdhd_SQ1_CTGO		= "0002";				// cdhd_SQ1_CTGO -> 0001:골프카드고객 0002:멤버쉽고객

			String cdhd_CTGO_SEQ_NO		= "";
			
			String memEmail				= "";	// 이메일
			String memZipCode			= "";	// 우편번호
			String memZipAddr			= "";	// 주소
			String memDetailAddr		= "";	// 상세주소
			String memMobile			= "";	// 핸드폰번호
			String memPhone				= "";	// 전화번호
			
			//등급
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");		// 7 Gold 회원
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            debug("## vipCardMemIns | strCode : "+strCode+" | cdhd_SQ2_CTGO : "+cdhd_SQ2_CTGO+" | cdhd_CTGO_SEQ_NO : "+cdhd_CTGO_SEQ_NO);
            
            // 회원정보 가져오기
			sql = this.getUserInfoQuery(strMemClss);  	// 회원등급번호 1:개인 / 5:법인
            userInfoPstmt = conn.prepareStatement(sql);
            userInfoPstmt.setString(1, memId );
            userInfoRs = userInfoPstmt.executeQuery();	
			if(userInfoRs.next()){
				memEmail			= userInfoRs.getString("EMAIL");	// 이메일
				memZipCode			= userInfoRs.getString("ZIPCODE");	// 우편번호
				memZipAddr			= userInfoRs.getString("ZIPADDR");	// 주소
				memDetailAddr		= userInfoRs.getString("DETAILADDR");	// 상세주소
				memMobile			= userInfoRs.getString("MOBILE");	// 핸드폰번호
				memPhone			= userInfoRs.getString("PHONE");	// 전화번호
			}
			if(userInfoRs != null) userInfoRs.close();
            if(userInfoPstmt != null) userInfoPstmt.close();
            
            
			// 1단계 이미 가입된 ID인지 체크 
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
        	pstmt.setString(2, memId );
            rs = pstmt.executeQuery();	
			
            if(!rs.next())	// 신규가입처리
            {	
            	// 2단계 회원테이블에 Insert
            	debug("## vipCardMemIns | 신규가입처리");
            	sql = this.getInsertVipMemQuery();
 				pstmt = conn.prepareStatement(sql);
 				
 				idx = 0;
 				pstmt.setString(++idx, memId ); 
 	        	pstmt.setString(++idx, memNm );
 	        	pstmt.setString(++idx, socId );
 	        	//pstmt.setString(++idx, cdhd_SQ2_CTGO );	// join_chul
 	        	
 	        	
 	        	pstmt.setString(++idx, strMemClss );
				
				pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				pstmt.setString(++idx, memMobile );
				pstmt.setString(++idx, memPhone );
				pstmt.setString(++idx, memEmail );
				pstmt.setString(++idx, memZipCode );
				pstmt.setString(++idx, memZipAddr );
				pstmt.setString(++idx, memDetailAddr );
 	        	
 				result = pstmt.executeUpdate();
 	            if(pstmt != null) pstmt.close();
 	            
            	
            }
            else	// 재가입 처리
            {

	            // 재가입인 경우 기존의 레벨은 모두 삭제한다. getGradeDelQuery
            	debug("## vipCardMemIns | 재가입처리");
        		sql = this.getGradeDelQuery();
				pstmt = conn.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, memId ); 
				pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();

            	// 2단계 회원테이블에 Update
            	sql = this.getReInsertVipMemQuery();
 				pstmt = conn.prepareStatement(sql);
 				
 				idx = 0;				
				//pstmt.setString(++idx, cdhd_SQ2_CTGO );
				pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				pstmt.setString(++idx, memMobile );
				pstmt.setString(++idx, memPhone );
				pstmt.setString(++idx, memEmail );
				pstmt.setString(++idx, memZipCode );
				pstmt.setString(++idx, memZipAddr );
				pstmt.setString(++idx, memDetailAddr );
 	        	pstmt.setString(++idx, memId ); 
 	        	
 				result = pstmt.executeUpdate();
 	            if(pstmt != null) pstmt.close();
            }
            
            
        	
            // 3단계 같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
			sql = this.getChkGradeQuery(); 
        	pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, memId ); 
    		pstmt.setString(2, cdhd_CTGO_SEQ_NO );
        	rs = pstmt.executeQuery();	
        	if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0"))
        	{
            	 /**SEQ_NO 가져오기**************************************************************/
				sql = this.getNextValQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				long max_seq_no = 0L;
				if(rs.next()){
					max_seq_no = rs.getLong("SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            /**Insert************************************************************************/
	            sql = this.getInsertGradeQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
	        	pstmt.setLong(++idx, max_seq_no ); 
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
	        	
				pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
            }
                       
			
			            
				        
			if(rs != null) rs.close();
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
     * SBS 회원   
     ************************************************************************ */
	public int sbsMemberCk(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;

				
		try {
			String socid 				= data.getString("socid").trim();	
			conn = context.getDbConnection("default", null);
			
			if(!"".equals(socid) && socid != null)
			{
				sql = this.getSbsMemCk(); 
				pstmt = conn.prepareStatement(sql);
		        pstmt.setString(1, socid );        	
		        rs = pstmt.executeQuery();
		        if(rs.next())
		        {
		        	result = 1;		        	
		        }
				
			}
			
			
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            debug("## sbsMemberCk | socid : "+socid+" | result : "+result);
            
           
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
     * SBS 회원   
     ************************************************************************ */
     private String getSbsMemCk(){
       StringBuffer sql = new StringBuffer();
       sql.append("\n");
       sql.append("\t  SELECT JUMIN_NO FROM BCDBA.TBACRGCDHDLODNTBL					\n");
       sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='3003'					\n");
       sql.append("\t  AND JUMIN_NO = ?					\n");
       sql.append("\t  AND JONN_DATE BETWEEN TO_CHAR(SYSDATE -365 ,'YYYYMMDD')  AND  TO_CHAR(SYSDATE,'YYYYMMDD')				\n");
       return sql.toString();
     }

	public String memCk(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		String strResult = "N";
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			String intMemGrade 				= data.getString("intMemGrade");	
			String intCardGrade 				= data.getString("intCardGrade");	
			conn = context.getDbConnection("default", null);	
			
			debug("## memCk | intMemGrade  : "+intMemGrade);
			debug("## memCk | intCardGrade : "+intCardGrade);
			
			sql = this.getMemberCkFreeQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();	
           
            while (rs.next())	{
            	
            	String ckNum = rs.getString("CDHD_SQ2_CTGO").replaceAll("0", "");
            	debug("## memCk | 유효등급여부 CDHD_SQ2_CTGO : "+rs.getString("CDHD_SQ2_CTGO")+" ==> ckNum : "+ckNum); 
            	
            	
            	if(intMemGrade.equals(ckNum))
            	{
            		strResult = "Y";
            	}
            	
            }
            
            //유료연회비 카드 추가 12 (8) : NH티타늄 , 13 (9) : NH플래티늄 , 20 (15) : APT 프리미엄, 19 (14) : 경남은행 Family카드, 21 (16): 탑골프, 22 (17): 리치카드 , 24(29) 카드등급 전북은행시그니처 
            if("8".equals(intMemGrade) || "9".equals(intMemGrade) || "15".equals(intMemGrade) || "14".equals(intMemGrade) || "16".equals(intMemGrade) || "17".equals(intMemGrade) || "24".equals(intCardGrade))
            {
            	strResult = "Y";
            }  
            
			
            debug("## memCk | strResult : "+strResult);
			
		
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
		return strResult;
	}
	/** ***********************************************************************
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMemberCkFreeQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_SQ2_CTGO	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT		\n");
		sql.append("\t  WHERE CDHD_SQ1_CTGO = '0002' AND ANL_FEE > 0			\n");
		return sql.toString();
	}
	
	public DbTaoResult cardExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String socId				= userEtt.getSocid();

			//조회 ----------------------------------------------------------			
			String sql = this.getGrdQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			//pstmt.setString(++idx, socId);
			pstmt.setString(1, userEtt.getAccount());	
			pstmt.setString(2, userEtt.getAccount());	
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

//					result.addString("memGrade" 	,rs.getString("MEM_GRADE") );
//					result.addInt("intMemGrade" 	,rs.getInt("INT_MEM_GRADE") );
					result.addString("memGrade" 	,rs.getString("GRD_NM") );
					result.addInt("intMemGrade" 	,rs.getInt("GRD_COMM") );
					result.addInt("intMemberGrade" 	,rs.getInt("GRD_MEM") );
					result.addInt("intCardGrade" 	,rs.getInt("GRD_CARD") );
					result.addString("RESULT", "00"); //정상결과
				}
			}else{
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
    * 골프회원정보에 인서트 - TBGGOLFCDHD     
    ************************************************************************ */
    private String getInsertAcrgJoinMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT,MEMBER_CLSS										\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(SYSDATE+365,'YYYYMMDD')																	\n");		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', '0001'						\n");
		sql.append("\t  		, 0, 0, ?																		\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
        return sql.toString();
    }
    /** ***********************************************************************
     * 골프회원정보에 업데이트 - TBGGOLFCDHD => 재가입    
     ************************************************************************ */
     private String getReInsertAcrgJoinMemQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t          UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
 		sql.append("\t          , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE+365,'YYYYMMDD')			\n");	// 유료회원 혜택기간을 공백으로 해준다.
 		sql.append("\t          , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t          , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t          , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= '0001'		\n");
 		sql.append("\t          , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
 		sql.append("\t          , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t          WHERE CDHD_ID=?		\n");
         return sql.toString();
     }   
    /** ***********************************************************************
    * 골프회원정보에 인서트 - TBGGOLFCDHD     
    ************************************************************************ */
    private String getInsertVipMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT,MEMBER_CLSS										\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(SYSDATE+365,'YYYYMMDD')		\n");		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', '3003'						\n");
		sql.append("\t  		, 0, 0, ?																		\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
        return sql.toString();
    }
    /** ***********************************************************************
     * 골프회원정보에 업데이트 - TBGGOLFCDHD => 재가입    
     ************************************************************************ */
     private String getReInsertVipMemQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t          UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
 		sql.append("\t          , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE+365,'YYYYMMDD')			\n");	// 유료회원 입력
 		sql.append("\t          , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t          , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t          , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= '3003'		\n");
 		sql.append("\t          , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
 		sql.append("\t          , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t          WHERE CDHD_ID=?		\n");
         return sql.toString();
     }   
	/** ***********************************************************************
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD		\n");
		sql.append("\t  WHERE JUMIN_NO=? AND CDHD_ID = ?			\n");
		return sql.toString();
	}
    /** ***********************************************************************
    * 골프회원정보에 인서트 - TBGGOLFCDHD     
    ************************************************************************ */
    private String getInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT,MEMBER_CLSS										\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");		
		sql.append("\t  		, '', ''																	\n");		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', '0001'						\n");
		sql.append("\t  		, 0, 0, ?																		\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
        return sql.toString();
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
      * 같은 등급이 등록되어 있는지 확인    
      ************************************************************************ */
      private String getChkGradeQuery(){
          StringBuffer sql = new StringBuffer();
  		sql.append("\n");
          sql.append("SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
  		return sql.toString();
      }
	
	
 	/** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다. = 골프회원등급관리    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(CDHD_GRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
		return sql.toString();
    }
       
   
    
    /** ***********************************************************************
    * 골프회원등급관리 인서트 - TBGGOLFCDHDGRDMGMT    
    ************************************************************************ */
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
  
    /** ***********************************************************************
    * 회원등급 가져오기 
    ************************************************************************ */
//    private String getMemGradeQuery(){
//        StringBuffer sql = new StringBuffer();
//		sql.append("	\n");
//		sql.append("\t	SELECT  GOLF_CMMN_CODE_NM MEM_GRADE, CDHD_SQ2_CTGO INT_MEM_GRADE	\n");
//		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
//		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
//		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0005'	\n");
//		sql.append("\t	WHERE T1.JUMIN_NO=?	\n");
//        return sql.toString();
//    }
  	private String getGrdQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT T_CTGO.CDHD_SQ2_CTGO GRD_COMM, T_CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t  , (SELECT T_CTGO.CDHD_SQ2_CTGO	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE T_GRD.CDHD_ID=T_CDHD.CDHD_ID AND T_CTGO.CDHD_SQ1_CTGO='0002') GRD_MEM	\n");
		sql.append("\t  , (SELECT CDHD_SQ2_CTGO FROM (	\n");
		sql.append("\t  SELECT ROWNUM RNUM, T_CTGO.CDHD_SQ2_CTGO, T_CTGO.SORT_SEQ	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE T_GRD.CDHD_ID=? AND T_CTGO.CDHD_SQ1_CTGO<>'0002'	\n");
		sql.append("\t        ORDER BY T_CTGO.SORT_SEQ)	\n");
		sql.append("\t        WHERE RNUM=1) GRD_CARD	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T_CODE ON T_CODE.GOLF_CMMN_CLSS='0005' AND T_CODE.GOLF_CMMN_CODE=T_CTGO.CDHD_SQ2_CTGO	\n");
		sql.append("\t  WHERE T_CDHD.CDHD_ID=?	\n");
  		return sql.toString();
  	}

 	/** ***********************************************************************
	* 회원정보 가져오기    strMemClss // 회원등급번호 1:개인 / 5:법인
	************************************************************************ */
	private String getUserInfoQuery(String strMemClss){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("5".equals(strMemClss)){
			sql.append("\t  SELECT USER_EMAIL EMAIL, '' ZIPCODE, '' ZIPADDR, '' DETAILADDR	\n");
			sql.append("\t  , USER_MOB_NO MOBILE, USER_TEL_NO PHONE	\n");
			sql.append("\t  FROM BCDBA.TBENTPUSER	\n");
			sql.append("\t  WHERE ACCOUNT=?	\n");
		}else{			
			sql.append("\t  SELECT EMAIL1 EMAIL, ZIPCODE, ZIPADDR, DETAILADDR, MOBILE, PHONE	\n");
			sql.append("\t  FROM BCDBA.UCUSRINFO	\n");
			sql.append("\t  WHERE ACCOUNT = ?	\n");
		}
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* 골프회원등급 삭제
	************************************************************************ */
	private String getGradeDelQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

    /** ***********************************************************************
    * 골프회원정보에 업데이트 - TBGGOLFCDHD => 재가입    
    ************************************************************************ */
    private String getReInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t          UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
		sql.append("\t          , ACRG_CDHD_JONN_DATE='', ACRG_CDHD_END_DATE=''			\n");	// 유료회원 혜택기간을 공백으로 해준다.
		sql.append("\t          , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
		sql.append("\t          , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
		sql.append("\t          , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= '0001'		\n");
		sql.append("\t          , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
		sql.append("\t          , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t          WHERE CDHD_ID=?		\n");
        return sql.toString();
    }          
}
