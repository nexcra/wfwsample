/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntKvpUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > ㅊ
*   적용범위  : golf
*   작성일자  : 2010-06-04
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.kvp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;


/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 > 이벤트 > Kvp > 수정처리";

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc 프로세스 생성자
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmEvntKvpUpdDaoProc() {}

	/** *****************************************************************
	 * execute 진행여부 변경
	 ***************************************************************** */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_mem = null;
		int idx = 0;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String pgrs_yn = data.getString("pgrs_yn");
			String jumin_no = data.getString("jumin_no");
			String cslt_yn = data.getString("cslt_yn");
			String cdhd_id = data.getString("cdhd_id");
			String mem_upd = "N";
			String tb_rslt_clss = "";	// TM결과구분코드
			int cmmCode = data.getInt("cmmCode");
						
			//kvp 월회원
			if (cmmCode != Integer.parseInt(AppConfig.getDataCodeProp("0005CODE11"))){
			
				if(pgrs_yn.equals("Y")){
					if(cslt_yn.equals("Y")){
						tb_rslt_clss = "00";
						// 회원 유료종료일자 : 시작날짜+365
						pstmt_mem = conn.prepareStatement(getMemPlusUpdQuery());
						mem_upd = "Y";
					}else{
						tb_rslt_clss = "01";
					}
				}else{
					tb_rslt_clss = "03";
					if(cslt_yn.equals("Y")){
						// 회원 유료종료일자 : 전날
						pstmt_mem = conn.prepareStatement(getMemMinusUpdQuery());
						mem_upd = "Y";
					}
				}
	
				// 신청테이블 상태 변경
				pstmt = conn.prepareStatement(getUpdQuery());
				idx = 0;
				pstmt.setString(++idx, pgrs_yn );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
				
				// TM 테이블 상태 변경 getTmUpdQuery
				pstmt = conn.prepareStatement(getTmUpdQuery());
				idx = 0;
				pstmt.setString(++idx, tb_rslt_clss );
				pstmt.setString(++idx, jumin_no );
				result += pstmt.executeUpdate();
				
				// 회원 유료종료기간 전날로 업데이트 
				if(mem_upd.equals("Y") && !cdhd_id.equals("미가입")){
					idx = 0;
					pstmt_mem.setString(++idx, cdhd_id );
					result += pstmt_mem.executeUpdate();
				}
							
			//스마트 월회원
			}else {
				
				if(pgrs_yn.equals("N")){			              
					result = exeGrdAction(conn, cdhd_id, pgrs_yn, aplc_seq_no);			      
			    }			    
			}			
			
			debug("result : " + result);
				
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			//
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
            
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
		
	}
	

	private int exeGrdAction(Connection con, String cdhdId, String pgrs_yn, String aplc_seq_no) throws DbTaoException {

		int cnt = 0;
	    int idx = 0;
	    int result = 0;
	    ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try{
	       
			//회원 소유 등급 개수
			idx = 0;
			pstmt = con.prepareStatement(getGrdCnt());
			pstmt.setString(++idx, cdhdId );
			rs = pstmt.executeQuery(); 
	 
			if (rs.next()){     
				cnt = rs.getInt("CNT");       
			}
	   
			//등급처리
			if(cnt > 1 ){
	     
				//삭제할 등급 히스토리 기록
				idx = 0;
				pstmt = con.prepareStatement(inGrdHistoryQuery());   
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate();   
				if(pstmt != null) pstmt.close();             
	 
				//등급 삭제
				idx = 0;
				pstmt = con.prepareStatement(delGrd());
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate(); 
				if(pstmt != null) pstmt.close();
				
				//대표등급 수정				
				idx = 0;
				pstmt = con.prepareStatement(exeUpdTopGrade1());
				pstmt.setString(++idx, cdhdId);
				pstmt.setString(++idx, cdhdId);	
				pstmt.executeUpdate();	
				if(pstmt != null) pstmt.close(); 
		        info(" || cdhdId : "+ cdhdId + " | 대표등급  변경");						
	     
			}else if( cnt == 1){
	    
				//삭제할 등급 히스토리 기록
				idx = 0;
				pstmt = con.prepareStatement(inGrdHistoryQuery());   
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate();   
				if(pstmt != null) pstmt.close();    
	 
				//화이트 등급으로 변경
				idx = 0;
				pstmt = con.prepareStatement(updateGrd());
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate(); 
				if(pstmt != null) pstmt.close(); 
				
				//대표등급 수정
				idx = 0;
				pstmt = con.prepareStatement(exeUpdTopGrade2());
				pstmt.setInt(++idx, 8);
				pstmt.setString(++idx, cdhdId);	
				pstmt.executeUpdate();		
				if(pstmt != null) pstmt.close(); 
		        info(" || cdhdId : "+ cdhdId + " | 대표등급 [8]으로 변경");				
	   
			}
	  
			if (cnt > 0 ){    
				// 신청테이블 상태 변경
				pstmt = con.prepareStatement(getUpdQuery());
				idx = 0;
				pstmt.setString(++idx, pgrs_yn );
				pstmt.setString(++idx, aplc_seq_no );
				result = pstmt.executeUpdate();  
	     
			} else {
				// 수정대상 내용 없습니다. 
				result = 0;
				//리턴메시지 확인하자
				
				debug(" not date ");
			}
			
			if(rs != null) rs.close();   
			if(pstmt != null) pstmt.close();
	   
		} catch(Exception e) {
		
			try {
				con.rollback();
		   }catch (Exception c){
	   
		   		MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "", "시스템오류입니다." );
		        throw new DbTaoException(msgEtt,e);
		        
		   }
	        
	  } finally {
	  
		   try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
		   try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	  }
	  
	  return result;
	  
	 } 	


	/** *****************************************************************
	 * execute_del 결제 취소 
	 ***************************************************************** */
	public String execute_cancel(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result_true = 0;
		int result_false = 0;
		String str_result = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(true);
			
			String pay_no 		= data.getString("pay_no");
			String aplc_seq_no = data.getString("aplc_seq_no");	
			
			debug("pay_no : " + pay_no);
			String [] arr_pay_no = GolfUtil.split(pay_no, "||");
			int cmmCode = data.getInt("cmmCode");

    		// 연회비 환급처리 해준다.
    		boolean payCancelResult = false;
    		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
    		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

			// 결제변수
			String odr_no = "";						// 주문번호
			String sttl_amt = "";					// 결제금액
			String mer_no = "";						// 가맹점번호
			String card_no = "";					// 카드번호
			String vald_date = "";					// 유효일자
			String ins_mcnt = "";					// 할부개월수
			String auth_no = "";					// 승인번호
			String ip = request.getRemoteAddr();	// 단말번호(IP, '.'제외)
			String sttl_mthd_clss = "";				// 결제방법구분코드
			String sttl_gds_clss = "";				// 결제상품구분코드
			String cdhd_id = "";
			
			//Smart1000월회원처리		
			if (cmmCode == Integer.parseInt(AppConfig.getDataCodeProp("0005CODE11"))){   
				
				// 결제 내역 조회
				pstmt = conn.prepareStatement(setPayBackListQuery());
				pstmt.setString(1, arr_pay_no[0] );
				rs = pstmt.executeQuery(); 
	
				if (rs.next()){
		
					odr_no = rs.getString("ODR_NO");
					sttl_amt = rs.getString("STTL_AMT");
					mer_no = rs.getString("MER_NO");
					card_no = rs.getString("CARD_NO");
					vald_date = rs.getString("VALD_DATE");
					ins_mcnt = rs.getString("INS_MCNT");
					auth_no = rs.getString("AUTH_NO");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
					cdhd_id = rs.getString("CDHD_ID");					
				
					// 비씨카드 결제인 경우
					if("0001".equals(sttl_mthd_clss)) {
		
						payEtt.setMerMgmtNo(mer_no);  // 가맹점 번호
						payEtt.setCardNo(card_no);   // isp카드번호
						payEtt.setValid(vald_date);   // 만료 일자
						payEtt.setAmount(sttl_amt);   // 결제금액 
						payEtt.setInsTerm(ins_mcnt);  // 할부개월수
						payEtt.setRemoteAddr(ip);   // ip 주소
						payEtt.setUseNo(auth_no);   // 승인번호
			
						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);   // 취소전문 호출
						} else {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);   // 취소전문 호출
						}
						
						debug("payCancelResult========> " + payCancelResult);
						
					} 
					
					if(payCancelResult){
						
						exeGrdAction(conn, cdhd_id, "N", aplc_seq_no);		
						
						sql = this.getPayUpdateQuery(); 
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, odr_no );
						result_true += pstmt.executeUpdate();
				
					// 결제실패시 내역 저장
					}else{		
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");      
			
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);      
						dataSet.setString("CDHD_ID", cdhd_id);      //회원아이디
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);  //결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);   //결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
						dataSet.setString("STTL_STAT_CLSS", "Y");     //결제여부 N:결제완료 / Y:결재취소		
						
						result_false += payFailProc.failExecute(context, dataSet, request, payEtt);
			
						debug("결제실패내역저장결과==> " + result_false);
						
					}
		
					debug("ODR_NO : " + odr_no + " / result_true : " + result_true +" / result_false : " + result_false);    
					//*****************************************************************************//*
				}   

			// KVP월회비 처리
			}else {				

				for(int i=0; i<arr_pay_no.length; i++){
					
					// 결제 내역 조회
		            pstmt = conn.prepareStatement(setPayBackListQuery());
					pstmt.setString(1, arr_pay_no[i] );
		            rs = pstmt.executeQuery();	
		            
		            while(rs.next()){
		            	
		            	payCancelResult = false;
		
		            	odr_no = rs.getString("ODR_NO");
						sttl_amt = rs.getString("STTL_AMT");
						mer_no = rs.getString("MER_NO");
						card_no = rs.getString("CARD_NO");
						vald_date = rs.getString("VALD_DATE");
						ins_mcnt = rs.getString("INS_MCNT");
						auth_no = rs.getString("AUTH_NO");
						sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
						sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
						cdhd_id = rs.getString("CDHD_ID");
		
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
		
							String sShopid  = "bcgolf";							// 샵아이디
							String sCrossKey  = "e3f8453680e39fc1d6dfe72079874219";	// 크로스키
							
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
				        	result_true += pstmt.executeUpdate();
							
							debug("ODR_NO : " + odr_no + " / result_true : " + result_true);
			
						}else{	// 결제실패시 내역 저장 2009.11.26
							
							GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
							
							DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
							dataSet.setString("CDHD_ID", cdhd_id);						//회원아이디
							dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
							dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
							dataSet.setString("STTL_STAT_CLSS", "Y");					//결제여부 N:결제완료 / Y:결재취소
								
							result_false += payFailProc.failExecute(context, dataSet, request, payEtt);
							
							debug("결제실패내역저장결과==> " + result_false);		
							
						}
		            }		
			        //*****************************************************************************//*
					debug("ODR_NO : " + odr_no + " / result_true : " + result_true +" / result_false : " + result_false);
				}
			
			}
			
			if(payCancelResult) {
				conn.commit();
				str_result = arr_pay_no.length + "개 중 " + "성공 : "+result_true+", 실패 : "+result_false+" 입니다.";
			} else {
				
				conn.rollback();	

				if ( result_false> 0 ) {
					str_result = "승인 취소 실패 => 응답코드 : "+ payEtt.getResCode() + " 응답메세지 : " + payEtt.getResMsg() + " 입니다.";
				}
				
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

		return str_result;
	}
								

	/** ***********************************************************************
	 * 승인 내역 가져오기
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT ODR_NO, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS, CDHD_ID	\n");
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
    
    /** ***********************************************************************
     * 진행여부 변경
     ************************************************************************ */
 	private String getUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN=? WHERE APLC_SEQ_NO=?	\n");		
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * TM 상태 변경
     ************************************************************************ */
 	private String getTmUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS=? WHERE RND_CD_CLSS='2' AND RCRU_PL_CLSS='5000' AND JUMIN_NO=?	\n");
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * 유료회원종료일자 : 어제
     ************************************************************************ */
 	private String getMemMinusUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE-1,'YYYYMMDD') WHERE CDHD_ID=?	\n");
 		return sql.toString();
 	}
 	
    
    /** ***********************************************************************
     * 유료회원종료일자 : 시작일자+365
     ************************************************************************ */
 	private String getMemPlusUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET ACRG_CDHD_END_DATE=TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE)+365,'YYYYMMDD') WHERE CDHD_ID=?	\n");
 		return sql.toString();
 	}
 	
 	
 	/*************************************************************************
 	* 회원 소유 등급 개수
 	************************************************************************ */ 
 	private String getGrdCnt(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  SELECT COUNT(*) CNT \n");
	 	sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  WHERE CDHD_ID=? \n");
	
	 	return sql.toString();

 	}  
 	
 	
    /*************************************************************************
     * 등급 히스토리 테이블 인서트 -> 변경내용 확인
     ************************************************************************ */    
 	private String inGrdHistoryQuery(){
	   
 		StringBuffer sql = new StringBuffer();
	   
		sql.append(" \n");
		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST \n");
		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST) \n");
		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') \n");
		sql.append("\n  , B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL ");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD \n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO \n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID \n");
		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRD.CDHD_CTGO_SEQ_NO = 27 \n");
		   
		return sql.toString();   
		   
	}   
  
 	/*************************************************************************
 	* 스마트등급 삭제
 	************************************************************************ */    
 	private String delGrd(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  WHERE CDHD_ID=?  \n");
	 	sql.append("\t  AND CDHD_CTGO_SEQ_NO = '27' \n");
	
	 	return sql.toString();   

 	}     


 	/*************************************************************************
 	* 스마트를 화이트로 변경
 	************************************************************************ */    
 	private String updateGrd(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  SET CDHD_CTGO_SEQ_NO = '8' , CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') \n");
	 	sql.append("\t  WHERE CDHD_ID=?  \n");
	
	 	return sql.toString();   

 	}     

 	/*************************************************************************
 	* 대표등급 변경
 	************************************************************************ */    	
	private String exeUpdTopGrade1(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD																			\n");				
		sql.append("\t	SET JOIN_CHNL ='0001', CDHD_CTGO_SEQ_NO=(SELECT CDHD_CTGO_SEQ_NO														\n");
		sql.append("\t						  FROM (	 																	\n");
		sql.append("\t								SELECT  GRD.CDHD_CTGO_SEQ_NO											\n");
		sql.append("\t								FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD, BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO		\n");
		sql.append("\t								WHERE CDHD_ID = ?														\n");
		sql.append("\t								AND  GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO						\n");
		sql.append("\t								ORDER BY T_CTGO.SORT_SEQ ASC											\n");
		sql.append("\t								 )																		\n");
		sql.append("\t						 WHERE ROWNUM = 1																\n");
		sql.append("\t						 )																				\n");
		sql.append("\t	WHERE CDHD_ID = ?																					\n");
		
		return sql.toString();
		
	} 	 	
 
 	/*************************************************************************
 	* 대표등급 변경
 	************************************************************************ */   	
	private String exeUpdTopGrade2(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO=? , JOIN_CHNL ='0001' \n");		
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}
	
}


