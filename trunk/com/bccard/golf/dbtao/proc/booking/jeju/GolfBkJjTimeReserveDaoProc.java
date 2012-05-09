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
package com.bccard.golf.dbtao.proc.booking.jeju;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkJjTimeReserveDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 프리미엄부킹 골프장 등록 처리";

	/** *****************************************************************
	 * GolfadmGrRegDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkJjTimeReserveDaoProc() {}
	
	/**
	 * 관리자 프리미엄부킹 골프장 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int resultUp = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String userID = "";
		DbTaoResult result =  new DbTaoResult(title);
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userID = userEtt.getAccount();
			}
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

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
            
            /**Insert************************************************************************/

			String round_HOPE_DATE = data.getString("ROUND_HOPE_DATE");
			round_HOPE_DATE = GolfUtil.replace(round_HOPE_DATE, "-", "");
			String round_HOPE_TIME_CLSS = data.getString("ROUND_HOPE_TIME_CLSS");
			String round_HOPE_TIMEA = data.getString("ROUND_HOPE_TIMEA");
			String round_HOPE_TIMEP = data.getString("ROUND_HOPE_TIMEP");
			String round_HOPE_TIME = "";
			if(round_HOPE_TIME_CLSS.equals("A")){
				round_HOPE_TIME = round_HOPE_TIMEA+"00";
			}else{
				round_HOPE_TIME = round_HOPE_TIMEP+"00";
			}
			String ctct_ABLE_TIME = data.getString("CTCT_ABLE_TIME");
			String ctct_ABLE_STRT_TIME = "";
			String ctct_ABLE_END_TIME = "";
			if(ctct_ABLE_TIME.equals("0000")){
				ctct_ABLE_STRT_TIME = "000000";
				ctct_ABLE_END_TIME = "000000";
			}else if(ctct_ABLE_TIME.equals("0900")){
				ctct_ABLE_STRT_TIME = "090000";
				ctct_ABLE_END_TIME = "110000";
			}else if(ctct_ABLE_TIME.equals("1100")){
				ctct_ABLE_STRT_TIME = "110000";
				ctct_ABLE_END_TIME = "130000";
			}else if(ctct_ABLE_TIME.equals("1300")){
				ctct_ABLE_STRT_TIME = "130000";
				ctct_ABLE_END_TIME = "150000";
			}else if(ctct_ABLE_TIME.equals("1500")){
				ctct_ABLE_STRT_TIME = "150000";
				ctct_ABLE_END_TIME = "170000";
			}else if(ctct_ABLE_TIME.equals("1700")){
				ctct_ABLE_STRT_TIME = "170000";
				ctct_ABLE_END_TIME = "000000";
			}
			
            // 01. insert
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, rsvt_SQL_NO); 
			pstmt.setString(++idx, max_RSVT_CLSS); 
			pstmt.setString(++idx, userID); 			// 사용자 ID
			pstmt.setString(++idx, round_HOPE_DATE); 
			pstmt.setString(++idx, data.getString("HOPE_RGN_CODE")); 
			pstmt.setString(++idx, data.getString("TEAM_NUM")); 
			pstmt.setString(++idx, data.getString("TOT_PERS_NUM")); 
			pstmt.setString(++idx, round_HOPE_TIME_CLSS); 
			pstmt.setString(++idx, round_HOPE_TIME); 
			pstmt.setString(++idx, data.getString("HP_DDD_NO")); 
			pstmt.setString(++idx, data.getString("HP_TEL_HNO")); 
			pstmt.setString(++idx, data.getString("HP_TEL_SNO")); 
			pstmt.setString(++idx, ctct_ABLE_STRT_TIME); 
			pstmt.setString(++idx, ctct_ABLE_END_TIME);
			

			
			resultUp = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            

			result.addString("RSVT_SQL_NO" 		,rsvt_SQL_NO);
			
			// 리턴
            if(resultUp > 0) {
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
    * Insert Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (															\n");
		sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, ROUND_HOPE_DATE, HOPE_RGN_CODE	\n");
		sql.append("\t  , TEAM_NUM, TOT_PERS_NUM, ROUND_HOPE_TIME_CLSS, ROUND_HOPE_TIME						\n");
		sql.append("\t  , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, CTCT_ABLE_STRT_TIME, CTCT_ABLE_END_TIME		\n");
		sql.append("\t  , RSVT_YN, REG_ATON		\n");
		sql.append("\t																						\n");
		sql.append("\t  ) VALUES (																			\n");
		sql.append("\t  ?, ?, ?, ?, ?																		\n");
		sql.append("\t  , ?, ?, ?, ?																		\n");
		sql.append("\t  , ?, ?, ?, ?, ?																		\n");
		sql.append("\t 	,'Y', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')											\n");
		sql.append("\t  )																					\n");
        return sql.toString();
    }
         
     /** ***********************************************************************
      * 예약번호 MAX값을 가져온다.    
      ************************************************************************ */
	private String getMaxNoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(SUBSTR(MAX(GOLF_SVC_RSVT_NO),6,12)+1,1) AS MAX_IDX,  (TO_CHAR(SYSDATE, 'YYYY')||'J') AS MAX_RSVT_CLSS \n");
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT 														\n");
		sql.append("\t  WHERE GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'J')     						\n");      
		return sql.toString();
	}
 
}
