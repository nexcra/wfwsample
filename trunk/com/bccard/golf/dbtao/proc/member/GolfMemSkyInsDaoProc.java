/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemSkyInsDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 스키판권 등록처리
*   적용범위  : golf 
*   작성일자  : 2009-12-01
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
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfMemSkyInsDaoProc extends AbstractProc {

	public static final String TITLE = "스키판권처리";

	public GolfMemSkyInsDaoProc() {}

	/** ***********************************************************************
	* 알아보기    
	*********************************************************************** */
	public int executeSky(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String payType 				= data.getString("payType").trim();		// 1:카드 2:카드+포인트
			String moneyType 			= data.getString("moneyType").trim();	// 1:챔피온(200,000) 2:블루(50,000) 3:골드(25,000)
			if(GolfUtil.empty(moneyType)){moneyType = "4";}
			String memType 				= data.getString("memType").trim();		// 회원구분 - 정회원 : 1 비회원:2
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			String insType				= data.getString("insType").trim();		// 유입경로 - TM : 1 일반 : ""
			//String code					= data.getString("CODE_NO");	//제휴처코드
			String code					= "SKY";	//제휴처코드
			String join_chnl			= data.getString("JOIN_CHNL");	
			
			String strMemClss		= userEtt.getStrMemChkNum();		// 회원Clss  2009.10.30 추가 .getStrMemChkNum()
			
			System.out.print("## GolfMemSkyInsDaoProc | memId : "+memId+" | strMemClss : "+strMemClss+"\n");
			if(!"5".equals(strMemClss)) strMemClss ="1";

			String cdhd_SQ1_CTGO		= "0002";				// cdhd_SQ1_CTGO -> 0001:골프카드고객 0002:멤버쉽고객
			String cdhd_SQ2_CTGO		= "000" + moneyType;	// cdhd_SQ2_CTGO -> 0001:챔피온 0002:블루 0003:골드 0004:일반
			
			String cdhd_CTGO_SEQ_NO = "";
			
			debug("GolfMemSkyInsDaoProc =============== payType => " + payType);
			debug("GolfMemSkyInsDaoProc =============== moneyType => " + moneyType);
			debug("GolfMemSkyInsDaoProc =============== insType => " + insType);
								            
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
            
			
			debug("===GolfMemSkyInsDaoProc=======01. 이미 등록된 회원인지 알아본다. (등록되어 있는 아이디가 있는지 검색)======");
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
            rs = pstmt.executeQuery();	
			if(!rs.next()){

			}else{
				debug("===GolfMemSkyInsDaoProc=======등록된 아이디가 있다============================");

	            sql = this.getMemberUpdateQuery(code);
				pstmt = conn.prepareStatement(sql);	
				pstmt.setString(++idx, join_chnl );
	        	pstmt.setString(++idx, code );
				pstmt.setString(++idx, memId ); 
	        	
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();

				debug("===GolfMemInsDaoProc=======재가입 회원 : 등급테이블 - 회원분류일련번호 업데이트");
	            sql = this.getMemberGradeUpdateQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
	        	pstmt.setString(++idx, memId ); 
	        	
				result = pstmt.executeUpdate();

				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
					
			}
				
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
			
			debug("===GolfMemSkyInsDaoProc=======00. 스키판권이벤트처리 완료 ===============");

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
	* 등급 알아보기    
	*********************************************************************** */
	public DbTaoResult gradeExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
		
			String sql = this.getMemGradeQuery(); 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("moneyType").trim());
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("memGrade" 	,rs.getString("MEM_GRADE") );
					result.addInt("intMemGrade" 	,rs.getInt("INT_MEM_GRADE") );
					result.addString("RESULT", "00"); //정상결과
					
					debug("MEM_GRADE : " + rs.getString("MEM_GRADE"));
					debug("INT_MEM_GRADE : " + rs.getInt("INT_MEM_GRADE"));
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
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD		\n");
		sql.append("\t  WHERE JUMIN_NO=?				\n");
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
    * 회원 업데이트 - 유료회원으로    
    ************************************************************************ */
    private String getMemberUpdateQuery(String code){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET												\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')							\n");
 		sql.append("\t		, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')			\n");
		if (!"".equals(code)) {
			sql.append("\t      , JOIN_CHNL= ? , AFFI_FIRM_NM= ?		\n");
		}
 		sql.append("\t		WHERE JUMIN_NO=?															\n");
        return sql.toString();
    }
       
    /** ***********************************************************************
    * 회원 등급관리 업데이트 - 유료회원으로    
    ************************************************************************ */
    private String getMemberGradeUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET					\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO=?								\n");
 		sql.append("\t		WHERE CDHD_ID=?									\n");
        return sql.toString();
    }
 
    /** ***********************************************************************
    * 회원등급 가져오기 
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT GOLF_CMMN_CODE_NM MEM_GRADE, SUBSTR(GOLF_CMMN_CODE,4,1) INT_MEM_GRADE	\n");
		sql.append("\t	FROM BCDBA.TBGCMMNCODE	\n");
		sql.append("\t	WHERE GOLF_CMMN_CLSS='0005' AND SUBSTR(GOLF_CMMN_CODE,4,1)=?	\n");
        return sql.toString();
    }
}
