/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAuthEtt
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 결제 승인 프로세스
*   적용범위  : golf
*   작성일자  : 2009-06-12
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.payment;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AllatUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.inicis.crypto.SymEncDec;


public class GolfPaymentDaoProc extends AbstractObject {

	public static final String TITLE = "결제 프로세스";
	private final String BSXINPT   = "BSXINPT";					// 프레임웍 조회서비스 
	/**
	 * Constructor.
	 */
	public GolfPaymentDaoProc() {
		
	}
	
	/**
	 * 승인전문 수행.
	 * @param context WaContext
	 * @param seqNo 일련번호
	 * @param ett EtaxAuthEtt
	 * @param cardno 카드번호
	 * @param valid 유효기간
	 * @param remoteAddr 원격지
	 * @param auth 승인여부 (승인이 아니면 검증)
	 * @return 승인번호
	 * @throws EtaxException
	 */
	public boolean executePayAuth(WaContext context, HttpServletRequest request, GolfPayAuthEtt ett) throws GolfException { 
		JoltInput entity = null;
		TaoResult taoResult= null;
		
		boolean result = false;
		debug(" executeAuth start!!! ");
		
		String fml_arg1	 = "1080";											// 전문번호(기본값)
		String fml_arg2  = ett.getMerMgmtNo();								// 가맹점번호
		String fml_arg3  = ett.getCardNo();									// 카드번호
		String fml_arg4  = ett.getValid();									// 유효기간. 년도앞 2자리 제거 (200507 → 0507) 
	
		String fml_arg5  = ett.getAmount();									// 승인금액
		String fml_arg6  = ett.getInsTerm();								// 할부기간(00:일시불, 60:Point)
		String fml_arg7  = StrUtil.replace(ett.getRemoteAddr(), ".", "");	// 단말번호(IP, '.'제외)
		String fml_arg8  = " ";												// 승인접수자직원번호(공란처리)
		String fml_arg9  = "2";												// 구분코드(1:검증, 2:승인)
		String fml_arg10 = "35";											// 업무구분(35:Web승인, 26:보험, 25:여행, 24:통판)
		
		try {			

			entity = new JoltInput();

			entity.setServiceName(BSXINPT);
			entity.setString("fml_trcode", "MGA0030I0700");
			entity.setString("fml_channel", "WEB");
			entity.setString("fml_sec50", "UNKNOWN");
			entity.setString("fml_sec51", ett.getRemoteAddr());
			
			entity.setString("fml_arg1",  fml_arg1 );
			entity.setString("fml_arg2",  fml_arg2 );
			entity.setString("fml_arg3",  fml_arg3 );
			entity.setString("fml_arg4",  fml_arg4 );
			entity.setString("fml_arg5",  fml_arg5 );
			entity.setString("fml_arg6",  fml_arg6 );
			entity.setString("fml_arg7",  fml_arg7 );
			entity.setString("fml_arg8",  fml_arg8 );
			entity.setString("fml_arg9",  fml_arg9 );
			entity.setString("fml_arg10", fml_arg10);

			debug("+2++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		

			debug(" UGA003_TE_AUTHProc entity= ["+entity+"]");

			JtTransactionProc pgProc = null;

     		pgProc = (JtTransactionProc)context.getProc("UGA003_TE_AUTH");
			info("GOLF_JAE START:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5  );

			JoltOutput output = pgProc.execute(context,entity);


			if(output == null){
				info("GOLF_JAE END:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|null" );
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"GOLF.AUTH.FAIL", null);
				throw new GolfException(msgEtt);
			}
			
			if (output.containsKey("fml_ret5") && output.containsKey("fml_ret6")) { // 정상승인이면
				String fml_ret5 = output.getString("fml_ret5").trim();
				String fml_ret6 = output.getString("fml_ret6").trim();
				ett.setResCode(fml_ret5);
				ett.setResMsg(fml_ret6);
				if ("1".equals(fml_ret5))	{
					result = true;
					ett.setUseNo(output.getString("fml_ret4"));		// 승인번호
					ett.setBankNo(output.getString("fml_ret14"));	// 회원사번호
				} 
			} else {
				result = false;
			}
			
			info ("GOLF_JAE END:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|" + result);

			debug("+3++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		
			
		} catch(GolfException e) {
			throw e;
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}


	/**
	 * 취소전문 수행.
	 * @param context WaContext
	 * @param seqNo 일련번호
	 * @param ett EtaxAuthEtt
	 * @param cardno 카드번호
	 * @param valid 유효기간
	 * @param remoteAddr 원격지
	 * @param auth 승인여부 (승인이 아니면 검증)
	 * @return 취소여부
	 * @throws EtaxException
	 */
	public boolean executePayAuthCancel(WaContext context, GolfPayAuthEtt ett) throws GolfException {
		boolean result = false;
		debug(" executePayAuthCancel start!!! ");

		String fml_arg1	 = "1090";												// 전문번호(기본값)
		String fml_arg2  = ett.getMerMgmtNo();									// 가맹점번호
		String fml_arg3  = ett.getCardNo();										// 카드번호
		String fml_arg4  = ett.getValid();										// 유효기간. 년도앞 2자리 제거 (200507 → 0507) 
		String fml_arg5  = ett.getAmount();										// 승인금액
		String fml_arg6  = ett.getInsTerm();									// 할부기간(00:일시불, 60:Point)-> 60 일때만 취소할때는 무조건 00 처리
		if(GolfUtil.empty(fml_arg6)){
			fml_arg6 = "00";
		}else{
			if(fml_arg6.equals("60")){
				fml_arg6 = "00";
			}
		}
		//String fml_arg6  = "00";												// 할부기간(00:일시불, 60:Point) -> 취소할때는 무조건 00 처리 - 20090811 
		String fml_arg7  = StrUtil.replace(ett.getRemoteAddr(), ".", "");		// 단말번호(IP, '.'제외)
		String fml_arg8  = "김승환";												// 승인접수자직원번호(공란처리)
		String fml_arg9  = "19931311";											// 승인접수자 직원번~호(공란처리)
		String fml_arg10 = ett.getUseNo();										// 승인번호
		String fml_arg11 = "35";												// 업무구분(35:Web승인, 26:보험, 25:여행, 24:통판)
		try {
			if (fml_arg6.length() < 2)		{ fml_arg6 = "0"+fml_arg6; }
			if (fml_arg7.length() > 11)		{ fml_arg7 = fml_arg7.substring(0,11); }

			// INPUT ENTITY GENERATE
			debug(" executePayAuthCancel start >> ADMIN F/W JoltInput!!! ");

			JoltInput entity = new JoltInput();
			entity.setServiceName("BSXINPT");
			entity.setString("fml_trcode", "MGA0030I0800");
			entity.setString("fml_channel", "WEB");
			entity.setString("fml_sec50", " ");
			entity.setString("fml_sec51", ett.getRemoteAddr());
//			JoltInput entity = null;											// Jolt Input Entity
//			entity = new JoltInput("UGA003_TE_CAN");
//			  entity.setServiceName("UGA003_TE_CAN");
			  entity.setString("fml_arg1",  fml_arg1 );						// 전문번호(기본값)
			  entity.setString("fml_arg2",  fml_arg2 );						// 가맹점번호
			  entity.setString("fml_arg3",  fml_arg3 );						// 카드번호
			  entity.setString("fml_arg4",  fml_arg4 );						// 유효기간
			  entity.setString("fml_arg5",  fml_arg5 );						// 승인금액
			  entity.setString("fml_arg6",  fml_arg6 );						// 할부기간(00:일시불, 60:Point)
			  entity.setString("fml_arg7",  fml_arg7 );						// 단말번호(IP Address, '.'제외)
			  entity.setString("fml_arg8",  fml_arg8 );						// 승인접수자 요청자
			  entity.setString("fml_arg9",  fml_arg9 );						// 승인접수자 직원번~호(공란처리)
			  entity.setString("fml_arg10", fml_arg10);						// 승인번호
			  entity.setString("fml_arg11", fml_arg11);						// 업무구분(35:Web승~인)

			debug(" UGA003_TE_CANProc entity= ["+entity+"]");

			JtTransactionProc pgProc = null;

     		pgProc = (JtTransactionProc)context.getProc("UGA003_TE_CAN");
			info ("GOLF_JAE:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5  );

			debug(" UGA003_TE_CANProc pgProc= ["+pgProc+"]");

			JoltOutput output = pgProc.execute(context,entity);


			if(output == null){
				info ("GOLF_JAE:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|" + result);
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.AUTH.FAIL", null);
				throw new GolfException(msgEtt);
			}
			if (output.containsKey("fml_ret2") && "2".equals(output.getString("fml_ret2"))) { // 정상승인이면
				result = true;
				ett.setResCode(output.getString("fml_ret2"));
				ett.setResMsg(output.getString("fml_ret3"));
			} else {
				ett.setResCode(output.getString("fml_ret2"));
				ett.setResMsg(output.getString("fml_ret3"));
				result = false;
			}

			info ("GOLF_JAE:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|" + result);

		} catch(GolfException e) {
			throw e;
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}

	
	/**
	 * 승인 요청 - 올앳페이
	 */
	public boolean executePayAuth_Allat(WaContext context, HttpServletRequest request, GolfPayAuthEtt ett) throws GolfException { 
		
		boolean result = false;
		debug("## GolfPaymentDaoProc | executeAuth AllatPay START!!! ");
	
		  String sAmount   		= ett.getAmount();              			// 승인금액
		  String sEncData  		= ett.getEncData();							// submit암호화값
		  String sShopid  		= ett.getShopId();							// 샵아이디
		  String sCrossKey 		= ett.getCrossKey();						// 크로스키
		  String strReq 		= "";
		  String sPayType		= "";										// 지불수단
		  String sApprovalNo	= "";										// 신용카드 승인번호
		  String sCardNm		= "";										// 신용카드 이름
		  String sBankNm		= "";										// 계좌이체 은행이름
		  String sSellMm		= "";										// 할부개월
		  String sOrderNo		= "";										// 주문번호
		  
		  if(GolfUtil.empty(sCrossKey)){
			  sCrossKey = "e3f8453680e39fc1d6dfe72079874219";
		  }
		  
		  if(GolfUtil.empty(sShopid)){
			  sShopid = "bcgolf";
		  }
		
		  // 요청 데이터 설정
		  strReq  ="allat_shop_id="   +sShopid;
		  strReq +="&allat_amt="      +sAmount;
		  strReq +="&allat_enc_data=" +sEncData;
		  strReq +="&allat_cross_key="+sCrossKey;
		  		
		  try {			

			  // 올앳 결제 서버와 통신  : AllatUtil.approvalReq->통신함수, HashMap->결과값
			  AllatUtil util = new AllatUtil();
			  HashMap hm     = null;
			  hm = util.approvalReq(strReq, "SSL");

			  // 결제 결과 값 확인
			  String sReplyCd		= (String)hm.get("reply_cd");		// 결과코드
			  String sReplyMsg		= (String)hm.get("reply_msg");		// 결과메세지
			  
			  ett.setResCode(sReplyCd);
			  ett.setResMsg(sReplyMsg);			  

			  // 승인 성공 
			  //if( sReplyCd.equals("0001") ){	// 테스트용
			  if( sReplyCd.equals("0000") ){	// 실결제용
				  
				  sPayType		= (String)hm.get("pay_type");		// 지불수단
				  sApprovalNo	= (String)hm.get("approval_no");	// 신용카드 승인번호
				  sCardNm		= (String)hm.get("card_nm");		// 신용카드 이름			  
				  sBankNm		= (String)hm.get("bank_nm");		// 계좌이체 은행이름
				  sSellMm		= (String)hm.get("sell_mm");		// 할부개월
				  sOrderNo 		= (String)hm.get("order_no");		// 주문번호

				  if(sPayType.equals("3D") || sPayType.equals("ISP") || sPayType.equals("NOR")){
					  sPayType = "CARD";
					  sCardNm = sCardNm + "카드";
				  }
				  else if(sPayType.equals("ABANK")){
					  sCardNm = sBankNm + "은행";
				  }
				    			   				
				  ett.setPayType(sPayType);
				  ett.setUseNo(sApprovalNo);
				  ett.setCardNm(sCardNm);
				  ett.setInsTerm(sSellMm);
				  ett.setOrderNo(sOrderNo);

				  result = true;
				  

				  // 로그찍기 시작
				  String sAmt            = (String)hm.get("amt");
				  String sApprovalYmdHms = (String)hm.get("approval_ymdhms");
				  String sSeqNo          = (String)hm.get("seq_no");
				  String sCardId         = (String)hm.get("card_id");
				  String sZerofeeYn      = (String)hm.get("zerofee_yn");
				  String sCertYn         = (String)hm.get("cert_yn");
				  String sContractYn     = (String)hm.get("contract_yn");
				  String sSaveAmt        = (String)hm.get("save_amt");
				  String sBankId         = (String)hm.get("bank_id");
				  String sCashBillNo     = (String)hm.get("cash_bill_no");
				  String sCashApprovalNo = (String)hm.get("cash_approval_no");
				  String sEscrowYn       = (String)hm.get("escrow_yn");			  

				  debug("결과코드               : " + sReplyCd          + "\n");
				  debug("결과메세지             : " + sReplyMsg         + "\n");
				  debug("주문번호               : " + sOrderNo          + "\n");
				  debug("승인금액               : " + sAmt              + "\n");
				  debug("지불수단               : " + sPayType          + "\n");
				  debug("승인일시               : " + sApprovalYmdHms   + "\n");
				  debug("거래일련번호           : " + sSeqNo            + "\n");
				  debug("에스크로 적용 여부     : " + sEscrowYn         + "\n");
				  debug("==================== 신용 카드 ===================\n");
				  debug("승인번호               : " + sApprovalNo       + "\n");
				  debug("카드ID                 : " + sCardId           + "\n");
				  debug("카드명                 : " + sCardNm           + "\n");
				  debug("할부개월               : " + sSellMm           + "\n");
				  debug("무이자여부             : " + sZerofeeYn        + "\n");   //무이자(Y),일시불(N)
				  debug("인증여부               : " + sCertYn           + "\n");   //인증(Y),미인증(N)
				  debug("직가맹여부             : " + sContractYn       + "\n");   //3자가맹점(Y),대표가맹점(N)
				  debug("세이브 결제 금액       : " + sSaveAmt          + "\n");
				  debug("=============== 계좌 이체 / 가상계좌 =============\n");
				  debug("은행ID                 : " + sBankId           + "\n");
				  debug("은행명                 : " + sBankNm           + "\n");
				  debug("현금영수증 일련 번호   : " + sCashBillNo       + "\n");
				  debug("현금영수증 승인 번호   : " + sCashApprovalNo   + "\n");
				  // 로그찍기 끝
				    
			  } 
			  // 승인  실패
			  else {
				  result = false;
			  }
			
			  info("## GolfPaymentDaoProc | executeAuth AllatPay END | result : " + result + "| sReplyCd : " + sReplyCd + "| sReplyMsg : " + sReplyMsg + "| sApprovalNo : " + sApprovalNo + "| sCardNm : " + sCardNm + "\n");
			
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}


	/**
	 * 승인 취소 - 올앳페이
	 */
	public boolean executePayAuthCancel_Allat(WaContext context, GolfPayAuthEtt ett) throws GolfException { 
		
		boolean result = false;
		debug("## GolfPaymentDaoProc | executeAuthCancel AllatPay START!!! ");

		String szReqMsg 		= "";
		String szAllatEncData 	= "";			    
		String szAmt           	= ett.getAmount(); 		// 취소 금액               (최대  10 자리)
		String szOrderNo       	= ett.getOrderNo(); 	// 주문번호                (최대  80 자리)	      
		String szPayType       	= ett.getPayType(); 	// 원거래건의 결제방식[카드:CARD,계좌이체:ABANK]
		String szSeqNo         	= "";    				// 거래일련번호:옵션필드   (최대  10자리)
		String sCancelYMDHMS	= "";					// 취소 일시
		String sShopid  		= ett.getShopId();		// 샵아이디
		String sCrossKey 		= ett.getCrossKey();	// 크로스키

		  
		if(GolfUtil.empty(sCrossKey)){
			sCrossKey = "e3f8453680e39fc1d6dfe72079874219";
		}
	  
		if(GolfUtil.empty(sShopid)){
			sShopid = "bcgolf";
		}


		try {			

			AllatUtil util = new AllatUtil();
			HashMap reqHm = new HashMap();
			HashMap resHm = null;

			reqHm.put("allat_shop_id" ,  sShopid );
			reqHm.put("allat_cross_key", sCrossKey);
			reqHm.put("allat_order_no",  szOrderNo);
			reqHm.put("allat_amt"     ,  szAmt    );
			reqHm.put("allat_pay_type",  szPayType);
			reqHm.put("allat_test_yn" ,  "N"      );    //테스트 :Y, 서비스 :N 
			reqHm.put("allat_opt_pin" ,  "NOUSE"  );    //수정금지(올앳 참조 필드)
			reqHm.put("allat_opt_mod" ,  "APP"    );    //수정금지(올앳 참조 필드)
			reqHm.put("allat_seq_no"  ,  szSeqNo  );    //옵션 필드( 삭제 가능함 )

			szAllatEncData = util.setValue(reqHm);
			szReqMsg  = "allat_shop_id="   + sShopid
						+ "&allat_amt="      + szAmt
						+ "&allat_enc_data=" + szAllatEncData
						+ "&allat_cross_key="+ sCrossKey;

			resHm = util.cancelReq(szReqMsg, "SSL");

			// 취소 결과 값 확인
			String sReplyCd   = (String)resHm.get("reply_cd");
			String sReplyMsg  = (String)resHm.get("reply_msg");
			  
			ett.setResCode(sReplyCd);
			ett.setResMsg(sReplyMsg);			  

			// 취소 성공
			//if( sReplyCd.equals("0001") ){		// 테스트용
			if( sReplyCd.equals("0000") ){	// 실결제용			
				sCancelYMDHMS = (String)resHm.get("cancel_ymdhms");			      
				result = true;
			} 
			// 취소 실패
			else {
				result = false;
			}
			
			info("## GolfPaymentDaoProc | executeAuthCancel AllatPay END | result : " + result +  "| sReplyCd : " + sReplyCd + "| sReplyMsg : " + sReplyMsg + "| sCancelYMDHMS : " + sCancelYMDHMS + "\n");
			
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}

	
	/**
	 * ISP 파라메터 파싱.
	 * @param parameterValue 파라메터값
	 * @return HashMap 
	 * @throws EtaxException
	 */
	public HashMap getKvpParameter(String parameterValue) throws GolfException {
		
		HashMap map = new HashMap();
				
		try {
			
			String decryptData = SymEncDec.decrypt(1, parameterValue);

			for( StringTokenizer st = new StringTokenizer(decryptData,"%"); st.hasMoreTokens(); ) {
				String tempString = st.nextToken();
				int idx = tempString.indexOf('=');
				String key = tempString.substring(0,idx);
				String val = tempString.substring(idx+1);
				debug("GOLF_PIV:" + key + "=" + val + ".");
				map.put(key,val);
			}

		} catch(GolfException e) {
			throw e;
		} catch(Throwable t) {
			// 파라메터 파싱 실패
			warn("GOLF_PIF:" + parameterValue , t );
			throw new GolfException(TITLE,t);
		}
		return map;
	}	
	
}
