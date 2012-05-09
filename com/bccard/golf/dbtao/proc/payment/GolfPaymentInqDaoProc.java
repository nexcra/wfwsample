/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfOrderInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      :	결제 관련 정보
*   적용범위  : Golf
*   작성일자  : 2009-06-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.payment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfPaymentInqDaoProc extends AbstractProc {

	public static final String TITLE = "결제 관련 정보"; 
	/** *****************************************************************
	 * TmOrderListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfPaymentInqDaoProc() {}	
	
	/**
	 * Top Point 가져오기
	 * @param context
	 * @param request
	 * @return
	 * @throws BaseException
	 */
	public int getMyPointInfo(WaContext context, HttpServletRequest request, String JuminNo) throws BaseException {
			
		int topPoint = 0;
		try
		{
			//jolt연동 top 포인트 가져오기
			GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
			//GolfUserEtt mbr 			= SessionUtil.getTopnUserInfo(request);
			//TopPointInfoEtt pointInfo   = resetProc.getTopPointInfoEtt(context, request, mbr.getJuminNo());	
			TopPointInfoEtt pointInfo   = resetProc.getTopPointInfoEtt(context, request, JuminNo);	
			
			topPoint = pointInfo.getTopPoint().getPoint();

		}catch (Throwable t)
		{
			throw new BaseException(t);
		}
		return topPoint;
	}
	
	/**
	 * 회원정보가져오기
	 * @param context
	 * @param data
	 * @return
	 * @throws BaseException
	 */
	public DbTaoResult getMyMemberInfo(WaContext context,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String sess_cstmr_id = data.getString("SESS_CSTMR_ID");
			
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT               ");
			sql.append("\n MEMID,             ");
			sql.append("\n ACCOUNT,             ");
			sql.append("\n NAME,                ");
			sql.append("\n EMAIL1,              ");
			sql.append("\n ZIPCODE,             ");
			sql.append("\n ZIPADDR,             ");
			sql.append("\n DETAILADDR,		");         
			sql.append("\n NVL(MOBILE,'--') MOBILE,              	");
			sql.append("\n NVL(PHONE,'--') PHONE, ");
			sql.append("\n SOCID ");
			sql.append("\n FROM BCDBA.UCUSRINFO 	");
			sql.append("\n WHERE 		");		
			sql.append("\n ACCOUNT=? ");
			//debug(sql.toString());
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, sess_cstmr_id);
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addString("ACCOUNT" ,rs.getString("ACCOUNT") );
					result.addString("NAME" ,rs.getString("NAME") );
					result.addString("EMAIL1" ,rs.getString("EMAIL1") );
					result.addString("ZIPCODE" ,rs.getString("ZIPCODE") );
					result.addString("ZIPADDR" ,rs.getString("ZIPADDR") );
					result.addString("DETAILADDR" ,rs.getString("DETAILADDR") );
					result.addString("MOBILE" ,rs.getString("MOBILE") );
					result.addString("PHONE"		,rs.getString("PHONE") );
					result.addString("SOCID"		,rs.getString("SOCID") );					
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);	
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
	

	/**
	 * 결제정보 가져오기
	 * @param context
	 * @param data
	 * @return
	 * @throws BaseException
	 */
	public DbTaoResult getPaymentInfo(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		try {
			conn = context.getDbConnection("default", null);			 
			//조회 ----------------------------------------------------------		
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT");
			sql.append("\n 	ODR_NO, CDHD_ID, STTL_MTHD_CLSS, STTL_GDS_CLSS, STTL_STAT_CLSS, STTL_AMT, MER_NO, CARD_NO,  	");
			sql.append("\n 	VALD_DATE, INS_MCNT, AUTH_NO, STTL_GDS_SEQ_NO, STTL_ATON, CNCL_ATON 	");
			sql.append("\n FROM");
			sql.append("\n BCDBA.TBGSTTLMGMT	");
			sql.append("\n WHERE ODR_NO = ?	");
			sql.append("\n AND CDHD_ID = ?	");
			
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("ODR_NO"));
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addString("ODR_NO" 				,rs.getString("ODR_NO") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("STTL_MTHD_CLSS" 		,rs.getString("STTL_MTHD_CLSS") );
					result.addString("STTL_GDS_CLSS"		,rs.getString("STTL_GDS_CLSS") );
					result.addString("STTL_STAT_CLSS"		,rs.getString("STTL_STAT_CLSS") );
					result.addString("STTL_AMT" 			,rs.getString("STTL_AMT") );
					result.addString("MER_NO" 				,rs.getString("MER_NO") );
					result.addString("CARD_NO"				,rs.getString("CARD_NO") );
					result.addString("VALD_DATE"			,rs.getString("VALD_DATE") );	
					result.addString("INS_MCNT"				,rs.getString("INS_MCNT") );		
					result.addString("AUTH_NO"				,rs.getString("AUTH_NO") );		
					result.addString("STTL_GDS_SEQ_NO"		,rs.getString("STTL_GDS_SEQ_NO") );		
					result.addString("STTL_ATON"			,rs.getString("STTL_ATON") );		
					result.addString("CNCL_ATON"			,rs.getString("CNCL_ATON") );						
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);	
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
	
	
	
	
}
