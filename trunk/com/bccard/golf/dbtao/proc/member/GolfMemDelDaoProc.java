/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0  
******************************************************************************/
public class GolfMemDelDaoProc extends AbstractProc {

	public static final String TITLE = "회원삭제처리";

	public GolfMemDelDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		int result = 0; 

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			String payWay = data.getString("payWay");	//mn:월회비
			int idx = 0;
			String tb_rslt_clss = "03";					// TM 상태 - 탈퇴
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId").trim();	// 외부 연동 자동 탈퇴 처리하는 경우 추가 2009.11.23
			}
			
			// 카드회원 등급 갯수 가져오기
			String cardMemCnt = execute_card_grd_cnt(context, data, request);		// 카드 등급 갯수
			debug("GolfMemDelDaoProc ::: cardMemCnt(카드 등급 갯수) : " + cardMemCnt);
			
			if(cardMemCnt.equals("0")){
				// 카드 등급이 없을경우, 탈퇴처리
				
				// 탈퇴업데이트 => SECE_YN : Y, 탈퇴일시 => SECE_ATON : YYYYMMDDHH24MISS
	            sql = this.getUpdateQuery();
				pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, userId );
	        	
	        	result = pstmt.executeUpdate();
	        	
	        	if("mn".equals(payWay)){
	        		
	                // 신청 테이블 월결제 내역 종료 처리
	        		sql = this.getUpdateMnPayQuery();
	    			pstmt = conn.prepareStatement(sql);
	            	pstmt.setString(1, userId );
	            	pstmt.executeUpdate();

					// TM 테이블 상태 변경 
					pstmt = conn.prepareStatement(getTmUpdQuery());
					idx = 0;
					pstmt.setString(++idx, tb_rslt_clss );
					pstmt.setString(++idx, userId );
					pstmt.executeUpdate();
	        	}
	        	
	            if(pstmt != null) pstmt.close();
				
			}else{
				// 카드 등급이 있을경우, 멤버십 등급 삭제, 카드 등급으로 대표등급 변경

				// 멤버십 등급 삭제
                sql = this.getDelMemGradQuery();
    			pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, userId );
            	
            	result = pstmt.executeUpdate();
	            	
				// 대표등급 카드로 변경, 유료회원기간 삭제
                sql = this.getUpdMemGrdQuery();
    			pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, userId );
            	pstmt.setString(2, userId );
            	
            	result = pstmt.executeUpdate();
            	
	            if(pstmt != null) pstmt.close();
				
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	public DbTaoResult execute_period(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		DbTaoResult result =  new DbTaoResult(title);
		ResultSet rs = null;

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			String userId = "";
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId").trim();	// 외부 연동 자동 탈퇴 처리하는 경우 추가 2009.11.23
			}

			// 유료회원으로 가입한지 한달이 넘었는가?
			sql = this.getPeriodQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
			
            if(rs.next()){
	            result.addString("ONE_MONTH_LATER", rs.getString("ONE_MONTH_LATER"));
	            if(GolfUtil.empty(rs.getString("APLC_SEQ_NO"))){
		            result.addString("payWay", "yr");
	            }else{
	            	result.addString("payWay", "mn");
	            }
	            result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	public DbTaoResult execute_money_cnt(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId").trim();	// 외부 연동 자동 탈퇴 처리하는 경우 추가 2009.11.23
			}

			// 사용 내역이 있으면 돌려주지 않는다.
			sql = this.getMoneyCountQuery(userId); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();	
			if(rs.next()){
				result.addInt("MONEY_CNT", rs.getInt("MONEY_CNT"));
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	public DbTaoResult execute_cancel(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}

            sql = this.getCancelUpdateQuery();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
        	
			result_upd = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            			
			
			if(result_upd > 0) {				
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

	public int execute_payUp(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result = 0;

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 결제테이블을 업데이트 해준다.

			String odr_no	= data.getString("ODR_NO").trim();		// 1:카드 2:카드+포인트
			sql = this.getPayUpdateQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, odr_no );
			result_upd = pstmt.executeUpdate();
        	
            if(pstmt != null) pstmt.close();
            			
			
			if(result_upd > 0) {	
				result = 1;
				conn.commit();
			} else {
				result = 0;
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


	public int execute_payCancel(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result = 0;
		int result_upd = 0;
		boolean payCancelResult = false;
		
		// 결제 변수
		String odr_no = "";
		String sttl_amt = "";
		String mer_no = "";
		String card_no = "";
		String vald_date = "";
		String ins_mcnt = "";
		String auth_no = "";
		String ip = request.getRemoteAddr();
		String sttl_mthd_clss = "";
		String sttl_gds_clss = "";

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 결제 정보를 가져온다.  

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);		 	
			String userId = "";	
			int intMemGrade = 0;
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}
			else{
				userId = data.getString("userId").trim();	// 외부 연동 자동 탈퇴 처리하는 경우 추가 2009.11.23
			}

			sql = this.getPayViewQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );

            rs = pstmt.executeQuery();	
			if(rs != null) {
				while(rs.next())  {	
					
					odr_no = rs.getString("ORDER_NO");
					sttl_amt = rs.getString("STTL_AMT");
					mer_no = rs.getString("MER_NO");
					card_no = rs.getString("CARD_NO");
					vald_date = rs.getString("VALD_DATE");
					ins_mcnt = rs.getString("INS_MCNT");
					auth_no = rs.getString("AUTH_NO");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
					
					ip = request.getRemoteAddr();
					
					debug("====GolfMemDelActn======odr_no========> " + odr_no);
					debug("====GolfMemDelActn======STTL_AMT========> " + sttl_amt);
					debug("====GolfMemDelActn======MER_NO========> " + mer_no);
					debug("====GolfMemDelActn======CARD_NO========> " + card_no);
					debug("====GolfMemDelActn======VALD_DATE========> " + vald_date);
					debug("====GolfMemDelActn======INS_MCNT========> " + ins_mcnt);
					debug("====GolfMemDelActn======AUTH_NO========> " + auth_no);

					//무료쿠폰일 경우 승인취소내역 skip
					if ("1001".equals(sttl_mthd_clss)) {
						payCancelResult = true;
					} 
					
					// 비씨카드 또는 복합결제인 경우
					else if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
					
						payEtt.setMerMgmtNo(mer_no);		// 가맹점 번호
						payEtt.setCardNo(card_no);			// isp카드번호
						payEtt.setValid(vald_date);			// 만료 일자
						payEtt.setAmount(sttl_amt);				// 결제금액	
						payEtt.setInsTerm(ins_mcnt);		// 할부개월수
						payEtt.setRemoteAddr(ip);			// ip 주소
						payEtt.setUseNo(auth_no);			// 승인번호

						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
							//payCancelResult=true;

						} else {

							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
						}
						debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult);
					}
					
					// 타사카드 또는 계좌이체인 경우(올앳페이)
					else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
					
						String payType = "";
						if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// 신용카드
						else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// 계좌이체
						
						payEtt.setOrderNo(odr_no);			// 주문번호
						payEtt.setAmount(sttl_amt);			// 결제금액	
						payEtt.setPayType(payType);			// 결제방식

						payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// 승인취소 호출	
						
						debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
					}					
					
					if(payCancelResult){
						
						sql = this.getPayUpdateQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, odr_no );
						result_upd = pstmt.executeUpdate();
						
						debug("====GolfMemDelActn======ODR_NO========> " + odr_no);
						debug("====GolfMemDelActn======result_upd========> " + result_upd);

					}
					else{	// 결제실패시 내역 저장 2009.11.26
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
						
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
						dataSet.setString("CDHD_ID", userId);						//회원아이디
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
						dataSet.setString("STTL_STAT_CLSS", "Y");					//결제여부 N:결제완료 / Y:결재취소
							
						int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
						
						debug("====GolfMemDelActn======결제실패내역저장결과========> " + result_fail);						
						
					}				
					
					debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult);
					
				}
			}

			debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult);
			debug("====GolfMemDelActn======result_upd========> " + result_upd);
        	
            if(pstmt != null) pstmt.close();
            if(rs != null) rs.close();

			if(result_upd > 0 && payCancelResult) {	
				result = 1;
				conn.commit();
			} else {
				result = 0;
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

	public int execute_isCardMem(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result = 0;

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 결제테이블을 업데이트 해준다.

			String odr_no	= data.getString("ODR_NO").trim();		// 1:카드 2:카드+포인트
			sql = this.getCardGrdQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, odr_no );
			result_upd = pstmt.executeUpdate();
        	
            if(pstmt != null) pstmt.close();
            			
			
			if(result_upd > 0) {	
				result = 1;
				conn.commit();
			} else {
				result = 0;
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



	// 카드 등급 갯수 가져오기
	public String execute_card_grd_cnt(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		String result = "0";

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}

			sql = this.getCardGrdQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				result = rs.getString("CARD_GRD_CNT");
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
 	/** ***********************************************************************
	* 가입한지 한달이 넘었는지 가져온다.
	************************************************************************ */
	private String getPeriodQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT CDHD.CDHD_ID, CDHD.ACRG_CDHD_JONN_DATE, TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMMDD') MONTH_AGO	\n");
		sql.append("\t	, CASE WHEN CDHD.ACRG_CDHD_JONN_DATE>TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMMDD') THEN 'N' ELSE 'Y' END ONE_MONTH_LATER	\n");
		sql.append("\t	, LST.APLC_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD CDHD	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGAPLCMGMT LST ON LST.CDHD_ID=CDHD.CDHD_ID AND LST.GOLF_SVC_APLC_CLSS='1001' AND LST.PGRS_YN='Y'	\n");
		sql.append("\t	WHERE CDHD.CDHD_ID=?	\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* 신청내역이 있는지 확인 
	************************************************************************ */
	private String getMoneyCountQuery(String userId){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT (CNT_ABLE-CNT_DEL+CNT_RSV+CNT_PAY) MONEY_CNT	\n");
		sql.append("\t	FROM (SELECT	\n");
		sql.append("\t	(SELECT COUNT(*) CNT_ABLE FROM BCDBA.TBGAPLCMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND GOLF_SVC_APLC_CLSS IN ('0006','0007','0008') AND PGRS_YN='Y') CNT_ABLE	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT_DEL FROM BCDBA.TBGAPLCMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND GOLF_SVC_APLC_CLSS IN ('0006','0007','0008') AND PGRS_YN='N')  CNT_DEL	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT_RSV FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1) IN ('M','P','S','D') AND RSVT_YN='Y')  CNT_RSV	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT_PAY FROM BCDBA.TBGSTTLMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE STTL_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		//sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND STTL_GDS_CLSS NOT IN (SELECT REPLACE(CDHD_SQ2_CTGO,'0007','0008') FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE ANL_FEE>0 UNION SELECT '0004' FROM DUAL) AND STTL_STAT_CLSS='Y') CNT_PAY	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND ( STTL_GDS_CLSS NOT IN (SELECT REPLACE(CDHD_SQ2_CTGO,'0007','0008') FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE ANL_FEE>0 UNION SELECT '0004' FROM DUAL) AND STTL_STAT_CLSS='Y'	\n");		
		sql.append("\t										AND STTL_GDS_CLSS != '0009') ) CNT_PAY	\n");
		sql.append("\t	FROM DUAL)	\n");
		
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* 삭제 신청내역이 있는지 확인 
	************************************************************************ */
	private String getMoneyMinusQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT COUNT(*) MONEY_CNT2 FROM (	\n");
		sql.append("\t	    SELECT CDHD_ID, SUBSTR(REG_ATON,1,8) REG_ATON FROM (	\n");
		sql.append("\t	        SELECT CDHD_ID, REG_ATON FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS IN ('0006','0007','0008') AND PGRS_YN='N'	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	) T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID=?	\n");
		return sql.toString();
	}

    /** ***********************************************************************
    * 회원 삭제 업데이트    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET								\n");
 		sql.append("\t	SECE_YN='Y', SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE CDHD_ID=?												\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 월회비회원 삭제시 월회비 신청내역 종료처리  
    ************************************************************************ */
    private String getUpdateMnPayQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN='N'	\n");
 		sql.append("\t	, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='1001' AND PGRS_YN='Y'	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * TM 상태 변경
     ************************************************************************ */
 	private String getTmUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS=? WHERE RND_CD_CLSS='2' AND RCRU_PL_CLSS='5000' AND JUMIN_NO=(SELECT JUMIN_NO FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?)	\n");
 		return sql.toString();
 	}

    /** ***********************************************************************
    * 회원 삭제 업데이트    
    ************************************************************************ */
    private String getCancelUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET								\n");
 		sql.append("\t	SECE_YN='', SECE_ATON=''	\n");
 		sql.append("\t	WHERE CDHD_ID=?												\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 결제 취소 업데이트    
    ************************************************************************ */
    private String getPayUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGSTTLMGMT	\n");
 		sql.append("\t	SET STTL_STAT_CLSS='Y', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE ODR_NO=?	\n");
        return sql.toString();
    }

 	/** ***********************************************************************
	* 결제내역 가져오기
	************************************************************************ */
	private String getPayViewQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT ODR_NO AS ORDER_NO, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO ,STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT T_PAY	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T_MEM ON T_PAY.CDHD_ID=T_MEM.CDHD_ID	\n");
		sql.append("\t	WHERE T_PAY.CDHD_ID=? AND STTL_STAT_CLSS='N'	\n");
		sql.append("\t	AND STTL_GDS_CLSS IN (SELECT REPLACE(CDHD_SQ2_CTGO,'0007','0008') FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE ANL_FEE>0 UNION SELECT '0004' FROM DUAL)	\n");
		sql.append("\t	AND STTL_ATON BETWEEN ACRG_CDHD_JONN_DATE AND TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
		sql.append("\t	ORDER BY STTL_ATON DESC	\n");
		return sql.toString();
	}

 	/** ***********************************************************************
	* 카드 등급이 있는지 알아보기
	************************************************************************ */
	private String getCardGrdQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  SELECT COUNT(*) CARD_GRD_CNT	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CTGO.CDHD_SQ1_CTGO='0001' AND GRD.CDHD_ID=?	\n");
		return sql.toString();
	}
		
 	/** ***********************************************************************
	* 멤버십 등급 삭제
	************************************************************************ */
	private String getDelMemGradQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		sql.append("\t  AND CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_SQ1_CTGO='0002')	\n");
		return sql.toString();
	}

 	/** ***********************************************************************
	* 대표등급 업데이트 
	************************************************************************ */
	private String getUpdMemGrdQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD SET ACRG_CDHD_JONN_DATE='', ACRG_CDHD_END_DATE=''	\n");
		sql.append("\t  , CDHD_CTGO_SEQ_NO = (	\n");
		sql.append("\t      SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t      FROM (	\n");
		sql.append("\t          SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t          FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t          JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t          WHERE CTGO.CDHD_SQ1_CTGO='0001' AND GRD.CDHD_ID=?	\n");
		sql.append("\t          ORDER BY SORT_SEQ DESC	\n");
		sql.append("\t      ) WHERE ROWNUM=1	\n");
		sql.append("\t  )	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
		
		
}
