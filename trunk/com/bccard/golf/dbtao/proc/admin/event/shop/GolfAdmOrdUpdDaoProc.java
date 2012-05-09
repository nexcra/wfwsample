/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmOrdUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 쇼핑 > 구매 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.shop;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmOrdUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 > 이벤트 > 쇼핑 > 구매 수정 처리";

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc 프로세스 생성자
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmOrdUpdDaoProc() {}

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc 구매 수정 처리 메소드
	 * @param N/A
	 ***************************************************************** */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String refund_yn = data.getString("refund_yn");	// 상태값
			String odr_no = data.getString("ord_no");		// 주문번호
			String cdhd_id = data.getString("cdhd_id");		// 아이디
			String sttl_stat_clss = data.getString("sttl_stat_clss");		// 주문상태 Y:결제취소, N:결제
            /*****************************************************************************/
			
			debug("odr_no : " + odr_no);

        	if("61".equals(refund_yn) && "N".equals(sttl_stat_clss)){
        		
        		// 연회비 환급처리 해준다.
        		boolean payCancelResult = false;
        		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
        		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
	

				// 결제변수
				String sttl_amt = "";	// 결제금액
				String mer_no = "";		// 가맹점번호
				String card_no = "";	// 카드번호
				String vald_date = "";	// 유효일자
				String ins_mcnt = "";	// 할부개월수
				String auth_no = "";	// 승인번호
				String ip = request.getRemoteAddr();  // 단말번호(IP, '.'제외)
				String sttl_mthd_clss = "";	// 결제방법구분코드
				String sttl_gds_clss = "";	// 결제상품구분코드
				

				// 결제 내역 조회
				sql = this.setPayBackListQuery(); 
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, odr_no );
	            rs = pstmt.executeQuery();	
	            
	            while(rs.next()){
	            	payCancelResult = false;

					sttl_amt = rs.getString("STTL_AMT");
					mer_no = rs.getString("MER_NO");
					card_no = rs.getString("CARD_NO");
					vald_date = rs.getString("VALD_DATE");
					ins_mcnt = rs.getString("INS_MCNT");
					auth_no = rs.getString("AUTH_NO");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");

					// 비씨카드 또는 복합결제인 경우
					if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
					
						payEtt.setMerMgmtNo(mer_no);		// 가맹점 번호
						payEtt.setCardNo(card_no);			// isp카드번호
						payEtt.setValid(vald_date);			// 만료 일자
						payEtt.setAmount(sttl_amt);			// 결제금액	
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
						debug("payCancelResult========> " + payCancelResult);
					}
					
					// 타사카드 또는 계좌이체인 경우(올앳페이)
					else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
					
						String payType = "";
						if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// 신용카드
						else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// 계좌이체

						String sShopid  = "bcgolfshop";							// 샵아이디
						String sCrossKey  = "2a50a7795aaff3397e26d250b24d942f";	// 크로스키
						
						payEtt.setOrderNo(odr_no);			// 주문번호
						payEtt.setAmount(sttl_amt);			// 결제금액	
						payEtt.setPayType(payType);			// 결제방식
						payEtt.setShopId(sShopid);
						payEtt.setCrossKey(sCrossKey);

						payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// 승인취소 호출	
						
						debug("payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
					}					
					
					if(payCancelResult){
						
						sql = this.getPayUpdateQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, odr_no );
						result_upd = pstmt.executeUpdate();
						
						debug("ODR_NO========> " + odr_no);
						debug("result_upd========> " + result_upd);

					}
					else{	// 결제실패시 내역 저장 2009.11.26
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
						
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
						dataSet.setString("CDHD_ID", cdhd_id);						//회원아이디
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
						dataSet.setString("STTL_STAT_CLSS", "Y");					//결제여부 N:결제완료 / Y:결재취소
							
						int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
						
						debug("결제실패내역저장결과========> " + result_fail);						
						
					}
	            }						
        	}else{ 
        		result_upd = 1;
        	}

            /*****************************************************************************/
			
        	if(result_upd > 0){
				sql = this.getUpdQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
	
				int idx = 0;
				pstmt.setString(++idx, data.getString("buy_yn") );
				pstmt.setString(++idx, refund_yn );
				pstmt.setString(++idx, data.getString("dlv_yn") );
				pstmt.setString(++idx, odr_no );		
				
				result = pstmt.executeUpdate();
				
				conn.setAutoCommit(true);
        	}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
		
	
    /** ***********************************************************************
    * 구매 상태를 변경한다.
    ************************************************************************ */
	private String getUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGLUGODRCTNT	\n");
		sql.append("\t	SET ODR_DTL_CLSS = ?, ODR_STAT_CLSS = ?, DLV_YN = ?	\n");
		sql.append("\t	WHERE ODR_NO=?	\n");
				
		return sql.toString();
	}

	/** ***********************************************************************
	 * 승인 내역 가져오기
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE ODR_NO=?	\n");
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
	
}


