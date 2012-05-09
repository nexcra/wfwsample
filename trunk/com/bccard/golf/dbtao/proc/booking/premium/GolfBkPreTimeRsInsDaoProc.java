/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 골프장 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.premium;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkPreTimeRsInsDaoProc extends AbstractProc {

	public static final String TITLE = "사용자 > 부킹 > 프리미엄 > 예약처리";

	/** *****************************************************************
	 * GolfadmGrRegDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPreTimeRsInsDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int resultUp = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String userID = "";
		int intCardGrade = 0;
		int intMemGrade = 0;
		int rsvt_cdhd_grd_seq_no = 0;
				
		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userID = userEtt.getAccount();
				intCardGrade = userEtt.getIntCardGrade();
				intMemGrade = userEtt.getIntMemGrade();
			}
			
			data.setString("CDHD_ID",userID);
			conn = context.getDbConnection("default", null);
			// 1일1회 신청 중복 체크
			int recvOverLapChk = this.getRecvOverLapChk(conn, data);

			if (recvOverLapChk == 0) {
				conn.setAutoCommit(false);		
				
				// 00. 프리미엄 부킹은 횟수제한이 없다.
				
	            /**SEQ_NO 가져오기**************************************************************/
				sql = this.getMaxNoQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				String max_IDX = "";
				String max_RSVT_CLSS = "";
				String rsvt_SQL_NO = "";
				if(rs.next()){
					max_IDX = GolfUtil.reSizeLen(rs.getString("MAX_IDX"),"0",7);    				
					max_RSVT_CLSS = rs.getString("MAX_RSVT_CLSS");
					rsvt_SQL_NO = max_RSVT_CLSS + "" + max_IDX;
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	
				/**골프회원예약등급일련번호 가져오기 20090907**************************************************************/
				// 기업은행카드 회원 조회
				if(intCardGrade>0)
				{	// 해당사항이 있으면 
					rsvt_cdhd_grd_seq_no = intCardGrade;
				}else{
					rsvt_cdhd_grd_seq_no = intMemGrade;
				}
				
	            /**Insert************************************************************************/
	            	
	            // 01. insert
	            sql = this.getInsertQuery();
				pstmt = conn.prepareStatement(sql);
				
				int idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, rsvt_SQL_NO); 
				pstmt.setString(++idx, max_RSVT_CLSS); 
				pstmt.setString(++idx, userID); 
				pstmt.setString(++idx, data.getString("GR_SEQ_NO")); 
				pstmt.setString(++idx, data.getString("TIME_SEQ_NO")); 
				pstmt.setString(++idx, data.getString("HP_DDD_NO")); 
				pstmt.setString(++idx, data.getString("HP_TEL_HNO")); 
				pstmt.setString(++idx, data.getString("HP_TEL_SNO")); 
				pstmt.setString(++idx, data.getString("EMAIL_ID")); 
				pstmt.setInt(++idx, rsvt_cdhd_grd_seq_no); 
				
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
	
	            // 02. update
	            sql = this.getUpdateQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, data.getString("TIME_SEQ_NO")); 
				
				resultUp = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	           
	           
				
				
				if(result > 0 && resultUp > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				//있을경우
				result = 2;
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
	
	/**
	 * 1일 1회 신청 중복 체크
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRecvOverLapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectOverQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, "M" );	
			pstmt.setString(++idx, data.getString("CDHD_ID") );
			pstmt.setString(++idx, data.getString("BK_DATE") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}

		}
		
		return result;
	}

    /** ***********************************************************************
	    * 1일 1회 중복신청 체크 Query를 생성하여 리턴한다.    
	    ************************************************************************ */
		private String getSelectOverQuery(){
	        StringBuffer sql = new StringBuffer();
			sql.append("SELECT	\n");
			sql.append("\t 	AFFI_GREEN_SEQ_NO	\n");
			sql.append("\t FROM BCDBA.TBGRSVTMGMT a	\n");
			sql.append("\t 		inner join BCDBA.TBGRSVTABLEBOKGTIMEMGMT b on a.RSVT_ABLE_BOKG_TIME_SEQ_NO=b.RSVT_ABLE_BOKG_TIME_SEQ_NO		\n");
			sql.append("\t 		inner join BCDBA.TBGRSVTABLESCDMGMT c on b.RSVT_ABLE_SCD_SEQ_NO=c.RSVT_ABLE_SCD_SEQ_NO						\n");
			
			sql.append("\t WHERE a.GOLF_SVC_RSVT_MAX_VAL LIKE '%' || ? 	\n"); //프리미엄부킹 : 2009M0000001 파3부킹 : 2009P0000001 스카이72드림골프레인지 : 2009D0000001 스카이72드림듄스 : 2009S0000001 제주골프라운지 : 2009J0000001 피팅서비스 : 2009F0000001
			sql.append("\t AND a.CDHD_ID = ?	\n");
			sql.append("\t AND c.BOKG_ABLE_DATE = ?	\n");
			sql.append("\t AND a.RSVT_YN = 'Y'	\n");
			
	        return sql.toString();
	    }
	
	/** ***********************************************************************
    * Insert Query를 생성하여 리턴한다.    - 예약 테이블에 넣기
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (																		\n");
		sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, AFFI_GREEN_SEQ_NO, RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t  , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, RSVT_YN, REG_ATON, RSVT_CDHD_GRD_SEQ_NO	\n");
		sql.append("\t																		\n");
		sql.append("\t  ) VALUES (															\n");
		sql.append("\t  ?, ?, ?, ?, ?														\n");
		sql.append("\t  , ?, ?, ?, ?, 'Y', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?			\n");
		sql.append("\t  )																	\n");
        return sql.toString();
    }
    
	/** ***********************************************************************
     * Update Query를 생성하여 리턴한다.    - 해당 티타임을 예약처리 해준다.
     ************************************************************************ */
     private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
        
  		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET		\n");
 		sql.append("\t  BOKG_RSVT_STAT_CLSS='0002'			\n");
 		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?			\n");    
         return sql.toString();
     }
     
     /** ***********************************************************************
      * 예약번호 MAX값을 가져온다.    - 예약번호를 생성해준다.
      ************************************************************************ */
	private String getMaxNoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(SUBSTR(MAX(GOLF_SVC_RSVT_NO),6,12)+1,1) AS MAX_IDX,  (TO_CHAR(SYSDATE, 'YYYY')||'M') AS MAX_RSVT_CLSS \n");
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT 														\n");
		sql.append("\t  WHERE GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'M')     						\n");      
		return sql.toString();
	}
 
}
