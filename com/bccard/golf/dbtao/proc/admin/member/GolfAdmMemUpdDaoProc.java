/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 회원정보 > 수정
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* GOLFLOUNG		20100513	임은혜	유료가입일 수정 추가, 완전삭제시 TM 회원일 경우 TM회원 테이블에 취소처리 업데이트
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmMemUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 > 회원관리 > 회정 정보 수정";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemUpdDaoProc() {}

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 회원정보 변경 메소드
	 * @param N/A
	 ***************************************************************** */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
            
			//01.회원정보 테이블 수정 : 
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
			pstmt.setString(++idx, data.getString("BOKG_LIMT_YN") );
			pstmt.setString(++idx, data.getString("BOKG_LIMT_FIXN_STRT_DATE") );
			pstmt.setString(++idx, data.getString("BOKG_LIMT_FIXN_END_DATE") );
			pstmt.setString(++idx, data.getString("ACRG_CDHD_JONN_DATE") );
			pstmt.setString(++idx, data.getString("ACRG_CDHD_END_DATE") );
			pstmt.setString(++idx, data.getString("CDHD_ID") );		
			
			result = pstmt.executeUpdate();
           			
			
			conn.setAutoCommit(true);
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 등급변경 메소드
	 * @param N/A
	 ***************************************************************** */
	public int execute_grade(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
    	String strStDate = "";		// 유료회원시작일자 
    	String strEdDate = "";		// 유료회원종료일자
    	int addMonth = 0;			// 유료회원 

    	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
    	GregorianCalendar cal = new GregorianCalendar();
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String cdhd_id = data.getString("CDHD_ID");		// 회원아이디
			String grade_old = data.getString("GRADE_OLD");	// 기존등급
			String grade_new = data.getString("GRADE_NEW");	// 변경등급(파라미터)
			String grade_new_num = grade_new;	// 변경등급(실제저장)
			if("SKI".equals(grade_new))	grade_new_num = "6";
			
			int idx = 0;									// 쿼리 인자값 인덱스
			int grade_seq = 0;								// 등급관리 테이블 일련번호
			
			// 01. 멤버십 등급이 있는지 검색
			sql = this.getMemGradeQuery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, cdhd_id );
            rs = pstmt.executeQuery();	
            
            if(rs.next()){
            	// 현재 멤버십 등급을 가지고 있는 경우 => 히스토리에 저장하고 업데이트 해준다.
            	grade_seq = rs.getInt("GRADE_SEQ");
            	
            	if(grade_seq > 0){
            		
            		// 히스토리 인서트
					/**SEQ_NO 가져오기**************************************************************/
					sql = this.getMaxHistoryQuery(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_seq_no = 0L;
					if(rs.next()){
						max_seq_no = rs.getLong("MAX_SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
		            sql = this.setHistoryQuery();
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setLong(++idx, max_seq_no );	// 히스토리 테이블 일련번호
					pstmt.setLong(++idx, grade_seq );	// 골프회원등급관리 테이블 일련번호
					
					result2 = pstmt.executeUpdate();
					
					if(result2 > 0){
		            
	            		// 등급 업데이트
	        			idx = 0;
	        			sql = this.updGradeQuery();
	        			pstmt = conn.prepareStatement(sql);
	        			
	        			pstmt.setString(++idx,  grade_new_num);
	        			pstmt.setInt(++idx,  grade_seq);
	        			
	        			result = pstmt.executeUpdate();
	        			
					}
            		
            	}
            }else{
            	// 멤버십 등급이 없는 경우 멤버십 등급을 인서트 한다.
				/**SEQ_NO 가져오기**************************************************************/
				sql = this.getMaxGradeQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				long max_seq_no = 0L;
				if(rs.next()){
					max_seq_no = rs.getLong("MAX_SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            /**Insert************************************************************************/
	            sql = this.setGradeQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
				pstmt.setLong(++idx, max_seq_no );	// 히스토리 테이블 일련번호
				pstmt.setString(++idx, cdhd_id );	// 아이디
				pstmt.setString(++idx, grade_new_num );	// 새로운 등급 일련번호
				
				result = pstmt.executeUpdate();
            }
            
            if(result>0){

            	if(grade_new.equals("8")){
            		strStDate = "";
            		strEdDate = "";
            	}else{
            		
	            	if(grade_new.equals("17")){
	            		addMonth = 3;
	            	}else if(grade_new.equals("18")){
	            		addMonth = 2;
	            	}else{
	            		addMonth = 12;
	            	}
	            	
	            	Date stDate = cal.getTime();
	            	cal.add(cal.MONTH, addMonth);
	            	Date edDate = cal.getTime();
	
	            	strStDate = fmt.format(stDate);
	            	strEdDate = fmt.format(edDate);
            	}
	            	
            	
            	// 멤버십 테이블 등급 업데이트
    			idx = 0;
    			sql = this.updGradeMemQuery(grade_old, grade_new);
    			pstmt = conn.prepareStatement(sql);
    			
    			pstmt.setString(++idx,  grade_new_num);
    			pstmt.setString(++idx,  strStDate);
    			pstmt.setString(++idx,  strEdDate);
    			pstmt.setString(++idx,  cdhd_id);
    			
    			result = pstmt.executeUpdate();
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
	

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 서비스해지 메소드
	 * @param N/A
	 ***************************************************************** */
	public int execute_pay(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result_upd = 0;		// 연회비 환급 성공여부
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String cdhd_id = data.getString("CDHD_ID");		// 회원아이디
			String payBack = data.getString("payBack");		// 연회비환급여부
			int idx = 0;									// 쿼리 인자값 인덱스
			int grade_seq = 0;								// 등급관리 테이블 일련번호
			
            
        	if("Y".equals(payBack)){
        		
        		// 연회비 환급처리 해준다.
        		boolean payCancelResult = false;
        		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
        		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

				String ip = request.getRemoteAddr();  // 단말번호(IP, '.'제외)
				String merMgmtNo = "";	// 가맹점번호
				String ispCardNo = "";	// 카드번호
				String valdlim = "";	// 유효기간. 년도앞 2자리 제거 (200507 → 0507)
				String sum = "";		// 승인금액
				String useNo = "";		// 승인번호
				String ord_no = "";		// 주문번호
				String ins_mcnt = "";	// 할부기간
				String sttl_mthd_clss = "";	// 결제방법구분코드
				String sttl_gds_clss = "";	// 결제상품구분코드
				

				// 결제 내역 조회
				idx = 0;
				sql = this.setPayBackListQuery(); 
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, cdhd_id );
	            rs = pstmt.executeQuery();	
	            
	            while(rs.next()){
	            	payCancelResult = false;

	            	ord_no = rs.getString("ODR_NO");
	            	sum = rs.getString("STTL_AMT");
	            	merMgmtNo = rs.getString("MER_NO");
	            	ispCardNo = rs.getString("CARD_NO");
	            	valdlim = rs.getString("VALD_DATE");
	            	useNo = rs.getString("AUTH_NO");
	            	ins_mcnt = rs.getString("INS_MCNT");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");


					// 비씨카드 또는 복합결제인 경우
					if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
						payEtt.setMerMgmtNo(merMgmtNo);		
						payEtt.setCardNo(ispCardNo);
						payEtt.setValid(valdlim);	
						payEtt.setAmount(sum);
						payEtt.setRemoteAddr(ip);	
						payEtt.setUseNo(useNo);	
						payEtt.setInsTerm(ins_mcnt);
	
						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
						} else {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
						}
						//debug("payCancelResult : " + payCancelResult);

					}
					
					// 타사카드 또는 계좌이체인 경우(올앳페이)
					else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
					
						String payType = "";
						if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// 신용카드
						else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// 계좌이체

						String sShopid  = "bcgolf";							// 샵아이디
						String sCrossKey  = "e3f8453680e39fc1d6dfe72079874219";	// 크로스키
						
						payEtt.setOrderNo(ord_no);			// 주문번호
						payEtt.setAmount(sum);			// 결제금액	
						payEtt.setPayType(payType);			// 결제방식
						payEtt.setShopId(sShopid);
						payEtt.setCrossKey(sCrossKey);

						payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// 승인취소 호출	
						
						//debug("payCancelResult : " + payCancelResult + " | odr_no : " + ord_no + " | sttl_amt : " + sum + " | payType : " + payType);
					}				
						
						
						
					if(payCancelResult){

						sql = this.getPayUpdateQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, ord_no );
						result_upd = pstmt.executeUpdate();

						
					}
					else{	// 결제실패시 내역 저장 2009.11.26
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
						
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
						dataSet.setString("CDHD_ID", cdhd_id);						//회원아이디
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
						dataSet.setString("STTL_STAT_CLSS", "Y");					//결제여부 N:결제완료 / Y:결재취소
							
						int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
						
						debug("결제실패내역저장결과 : " + result_fail + " / ord_no : " + ord_no + " / useNo : " + useNo);
												
					}
	            }						
        	}

        	if("N".equals(payBack) || ("Y".equals(payBack)) && result_upd>0){
				// 탈퇴시킨다.
	        	idx = 0;
				sql = this.setMemCcQuery(); 
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, cdhd_id );
	            result = pstmt.executeUpdate();	
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

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 회원완전삭제 메소드
	 * @param N/A
	 ***************************************************************** */
	public int execute_del(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String cdhd_id = data.getString("CDHD_ID");		// 회원아이디
			String jumin_no = data.getString("JUMIN_NO");
			
			/*
			삭제할 테이블  = 16개
			TBGCLUBBBRD / 동호회게시판 -> CLUB_CDHD_SEQ_NO / 동호회회원일련번호	
			TBGCLUBBBRDREPY / 동호회게시판댓글 -> REG_PE_ID
			TBGCLUBCDHDMGMT / 동호회회원관리
			
			TBGBBRD / 게시판 => ID
			TBGBBRDREPY / 게시판댓글 => RGS_PE_ID
			TBGCUPNUSEHST / 쿠폰사용이력
			TBGRSVTMGMT / 예약관리
			TBGAPLCMGMT / 신청관리
			TBGSCORINFO / 스코어정보
			TBGSCRAP / 스크랩			
			TBGCBMOUSECTNTMGMT / 사이버머니사용내역관리
			TBGSTTLMGMT / 결제관리
			TBGCDHDGRDCHNGHST / 회원등급변경이력
			TBGCDHDRIKMGMT / 골프회원사은품관리
			
			TBGGOLFCDHDGRDMGMT / 골프회원등급관리
			TBGGOLFCDHD / 골프회원
			
			TBLUGTMCSTMR / TM회원 게시판 - TM 회원일 경우 강퇴처리 업데이트
			*/

			// Tm 회원여부 검색해서 취소처리 결과 업데이트
            sql = this.getTMquery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, jumin_no );
            rs = pstmt.executeQuery();	
            if(rs.next()){
    			sql = this.getTmUpdquery();
                pstmt = conn.prepareStatement(sql);
    			pstmt.setString(1, jumin_no );
                result = pstmt.executeUpdate();	
            }
            
//			TBGCLUBBBRD / 동호회게시판 -> CLUB_CDHD_SEQ_NO / 동호회회원일련번호	
			sql = this.getTBGCLUBBBRDquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
            
//			TBGCLUBBBRDREPY / 동호회게시판댓글 -> REG_PE_ID
			sql = this.getTBGCLUBBBRDREPYquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCLUBCDHDMGMT / 동호회회원관리
			sql = this.getTBGCLUBCDHDMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			
//			TBGBBRD / 게시판 => ID
			sql = this.getTBGBBRDquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGBBRDREPY / 게시판댓글 => RGS_PE_ID
			sql = this.getTBGBBRDREPYquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCUPNUSEHST / 쿠폰사용이력
			sql = this.getTBGCUPNUSEHSTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGRSVTMGMT / 예약관리
			sql = this.getTBGRSVTMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGAPLCMGMT / 신청관리
			sql = this.getTBGAPLCMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGSCORINFO / 스코어정보
			sql = this.getTBGSCORINFOquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGSCRAP / 스크랩	
			sql = this.getTBGSCRAPquery();		
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCBMOUSECTNTMGMT / 사이버머니사용내역관리
			sql = this.getTBGCBMOUSECTNTMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGSTTLMGMT / 결제관리
			sql = this.getTBGSTTLMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCDHDGRDCHNGHST / 회원등급변경이력
			sql = this.getTBGCDHDGRDCHNGHSTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCDHDRIKMGMT / 골프회원사은품관리
			sql = this.getTBGCDHDRIKMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			
//			TBGGOLFCDHDGRDMGMT / 골프회원등급관리
			sql = this.getTBGGOLFCDHDGRDMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGGOLFCDHD / 골프회원
			sql = this.getTBGGOLFCDHDquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();		

            if(pstmt != null) pstmt.close();
            
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    :회원테이블
    ************************************************************************ */
	private String getInsertQuery(){
		StringBuffer sql = new StringBuffer();
		    
		sql.append("\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD SET											\n");
		sql.append("\t  BOKG_LIMT_YN=?, BOKG_LIMT_FIXN_STRT_DATE=?, BOKG_LIMT_FIXN_END_DATE=?	\n");
		sql.append("\t  , ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		sql.append("\t  WHERE CDHD_ID=?															\n");
					
		return sql.toString();
	}
	
	 
	/** ***********************************************************************
	* 해당회원이 멤버십 등급을 가지고 있는지 검색
	************************************************************************ */
	private String getMemGradeQuery(){
		StringBuffer sql = new StringBuffer();
		      
		sql.append("\n	SELECT T2.CDHD_CTGO_SEQ_NO GRADE_NO, T1.CDHD_GRD_SEQ_NO GRADE_SEQ	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t 	WHERE T1.CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 히스토리 테이블 일련번호 최대값 가져오기 
	************************************************************************ */
	private String getMaxHistoryQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGCDHDGRDCHNGHST 	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 히스토리 테이블에 저장한다.
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	");
		sql.append("\n	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	");
		sql.append("\n	SELECT ?, A.CDHD_CTGO_SEQ_NO, A.CDHD_ID, A.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS')	");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHDGRDMGMT A , BCDBA.TBGGOLFCDHD B	");
		sql.append("\n	WHERE A.CDHD_GRD_SEQ_NO= ?	");
		sql.append("\n	AND A.CDHD_ID = B.CDHD_ID	");
					
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 등급을 업데이트 한다.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t	WHERE CDHD_GRD_SEQ_NO=?	\n");
				
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 회원테이블 등급을 업데이트 한다.
	************************************************************************ */
	private String updGradeMemQuery(String grade_old, String grade_new){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t 	, ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		if("SKI".equals(grade_new)){	// 블루스키일경우 변경
			sql.append("\t 	, JOIN_CHNL='2302', AFFI_FIRM_NM='SKI'	\n");
		}
		sql.append("\t	WHERE CDHD_ID=?	\n");
				
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 골프회원등급관리 일련번호 최대값을 가져온다. 
	************************************************************************ */
	private String getMaxGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(CDHD_GRD_SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT 	\n");
					
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * 멤버십 등급이 없을 경우 인서트 해준다.
	 ************************************************************************ */
	private String setGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT VALUES(	\n");
		sql.append("\t 	?, ?, ?, to_char(sysdate,'YYYYMMDDHH24MISS'), '', ''	\n");
		sql.append("\t 	) 	\n");
				
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * 서비스해지 처리
	 ************************************************************************ */
	private String setMemCcQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET 	\n");
		sql.append("\t 	SECE_YN='Y', SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t 	WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * 승인취소 리스트 가져오기
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT PAY.ODR_NO, PAY.STTL_AMT, PAY.MER_NO, PAY.CARD_NO, PAY.VALD_DATE, PAY.AUTH_NO, INS_MCNT, STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHD CDHD	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGSTTLMGMT PAY ON PAY.CDHD_ID=CDHD.CDHD_ID AND PAY.STTL_ATON BETWEEN CDHD.ACRG_CDHD_JONN_DATE AND CDHD.ACRG_CDHD_END_DATE	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=PAY.STTL_GDS_CLSS	\n");
		sql.append("\t	WHERE CDHD.CDHD_ID=? AND (SECE_YN IS NULL OR SECE_YN='N')	\n");
		sql.append("\t 	AND CODE.GOLF_CMMN_CLSS='0016' AND (CODE.GOLF_CMMN_CODE_NM LIKE '%멤버쉽%' OR CODE.GOLF_CMMN_CODE_NM='회원등급 업그레이드' )		\n");
		sql.append("\t 	AND PAY.STTL_STAT_CLSS='N' AND STTL_AMT>0	\n");		
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
	* TBGCLUBBBRD / 동호회게시판 -> CLUB_CDHD_SEQ_NO / 동호회회원일련번호	
	************************************************************************ */
	private String getTBGCLUBBBRDquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCLUBBBRD WHERE CLUB_CDHD_SEQ_NO IN (	\n");
 		sql.append("\t	    SELECT CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT WHERE CDHD_ID=?	\n");
 		sql.append("\t	)	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCLUBBBRDREPY / 동호회게시판댓글 -> REG_PE_ID	
	************************************************************************ */
	private String getTBGCLUBBBRDREPYquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCLUBBBRDREPY WHERE REG_PE_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCLUBCDHDMGMT / 동호회회원관리	
	************************************************************************ */
	private String getTBGCLUBCDHDMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCLUBCDHDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGBBRD / 게시판 => ID	
	************************************************************************ */
	private String getTBGBBRDquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGBBRD WHERE ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGBBRDREPY / 게시판댓글 => RGS_PE_ID	
	************************************************************************ */
	private String getTBGBBRDREPYquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGBBRDREPY WHERE RGS_PE_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCUPNUSEHST / 쿠폰사용이력	
	************************************************************************ */
	private String getTBGCUPNUSEHSTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCUPNUSEHST WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TBGRSVTMGMT / 예약관리	
	************************************************************************ */
	private String getTBGRSVTMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGRSVTMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGAPLCMGMT / 신청관리	
	************************************************************************ */
	private String getTBGAPLCMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGAPLCMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGSCORINFO / 스코어정보	
	************************************************************************ */
	private String getTBGSCORINFOquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGSCORINFO WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
		
	/** ***********************************************************************
	* TBGSCRAP / 스크랩	
	************************************************************************ */
	private String getTBGSCRAPquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGSCRAP WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCBMOUSECTNTMGMT / 사이버머니사용내역관리	
	************************************************************************ */
	private String getTBGCBMOUSECTNTMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCBMOUSECTNTMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TBGSTTLMGMT / 결제관리	
	************************************************************************ */
	private String getTBGSTTLMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGSTTLMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TBGCDHDGRDCHNGHST / 회원등급변경이력	
	************************************************************************ */
	private String getTBGCDHDGRDCHNGHSTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCDHDGRDCHNGHST WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCDHDRIKMGMT / 골프회원사은품관리
	************************************************************************ */
	private String getTBGCDHDRIKMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCDHDRIKMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGGOLFCDHDGRDMGMT / 골프회원등급관리	
	************************************************************************ */
	private String getTBGGOLFCDHDGRDMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGGOLFCDHD / 골프회원	
	************************************************************************ */
	private String getTBGGOLFCDHDquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TM회원여부 알아내기	
	************************************************************************ */
	private String getTMquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	SELECT * FROM BCDBA.TBLUGTMCSTMR	\n");
 		sql.append("\t	WHERE RND_CD_CLSS='2' AND  ACPT_CHNL_CLSS ='1' AND TB_RSLT_CLSS IN ('00','01') AND JUMIN_NO=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TM 회원테이블에 취소처리 업데이트
	************************************************************************ */
	private String getTmUpdquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS='98',REJ_RSON ='취소처리-강퇴'	\n");
 		sql.append("\t	WHERE  RND_CD_CLSS='2' AND  ACPT_CHNL_CLSS ='1' AND TB_RSLT_CLSS IN ('00','01') AND JUMIN_NO=?	\n");
		return sql.toString();
	}
}


