/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntPrimePay
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 해외프라임 > 결제
*   적용범위  : Golf
*   작성일자  : 2010-08-16
************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.02.11    이경희	   ISP인증기록 추가
***************************************************************************************************/
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeInsDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntPrimePayActn extends GolfActn{
	
	public static final String TITLE = "이벤트 > 해외프라임 > 결제";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)		
		String memName = "";
		String memSocid = "";	
		String ispCardNo = "";	// isp카드번호
		String cstIP = request.getRemoteAddr(); //접속IP		
		
		try { 
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				memName		= (String)usrEntity.getName(); 
				memSocid 	= (String)usrEntity.getSocid(); 
			}

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 후처리
			String script = "";
			boolean payResult = false;
			boolean payCancelResult = false;
			int addResult = 0;
				
			// 신청정보
			String cdhd_id		= parser.getParameter("cdhd_id","");		// 회원아이디
			String bkg_pe_num	= parser.getParameter("bkg_pe_num", "");	// 성명
			String jumin_no		= parser.getParameter("jumin_no", "");		// 주민등록번호
			String hp_ddd_no	= parser.getParameter("hp_ddd_no","");		// 연락처1
			String hp_tel_hno	= parser.getParameter("hp_tel_hno","");		// 연락처2
			String hp_tel_sno	= parser.getParameter("hp_tel_sno", "");	// 연락처3
			String ddd_no		= parser.getParameter("ddd_no","");			// 집전화1
			String tel_hno		= parser.getParameter("tel_hno","");		// 집전화2
			String tel_sno		= parser.getParameter("tel_sno", "");		// 집전화3
			String dtl_addr		= parser.getParameter("dtl_addr","");		// 주소
			String lesn_seq_no	= parser.getParameter("lesn_seq_no","");	// 가입멤버십
			String pu_date		= parser.getParameter("pu_date","");		// 회원시작일
			pu_date = GolfUtil.replace(pu_date, ".", "");
			String memo_expl	= parser.getParameter("memo_expl","");		// 기타 요청 사항
			
			// 결제정보
			String realPayAmt	= parser.getParameter("realPayAmt", "0");		// 결제 금액
			String order_no		= parser.getParameter("order_no", "0");			// 주문번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("bkg_pe_num", bkg_pe_num);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);			
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);			
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("dtl_addr", dtl_addr);
			dataSet.setString("lesn_seq_no", lesn_seq_no);
			dataSet.setString("pu_date", pu_date);
			dataSet.setString("memo_expl", memo_expl);
			
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("order_no", order_no);		

			// 04.실제 테이블(Proc) 조회
			
			// 주문정보 저장
			GolfEvntPrimeInsDaoProc proc = (GolfEvntPrimeInsDaoProc)context.getProc("GolfEvntPrimeInsDaoProc");
			
			
			/***주문정보저장 end **결제 start***********************************************/
			
			//debug("// STEP 1. 입력값에 대한 세션체크+");
			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
			if ( st_s == null ) st_s = "";
			request.getSession().removeAttribute("ParameterManipulationProtectKey");

			String st_p = request.getParameter("ParameterManipulationProtectKey");
			if ( st_p == null ) st_p = "";

			if ( !st_p.equals(st_s) ) {
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
			}				

			// 결제정보 변수 => 공통
			String payType 			= parser.getParameter("payType", "").trim();		// 1:카드 2:카드+포인트 3:타사카드
			String ip 				= request.getRemoteAddr();  
			String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD5");				// 가맹점 번호 765943401-연회비MBCDHD5
			String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP 인증값

			String cardNo			= parser.getParameter("card_no", "0");				// 카드번호
			String insTerm			= parser.getParameter("ins_term", "00");			// 할부개월수
			String siteType			= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
			
						
			// 비씨카드 debug("// STEP 1_2. 파라미터 입력"); 
			HashMap kvpMap = null;
			String user_r      = StrUtil.isNull(parser.getParameter("cdhd_id"),"");			// 사용자 아이디
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// 사인값
			String pcg         = "";														// 개인/법인 구분			
			String valdlim	   = "";														// 만료 일자
			String pid = null;																// 개인아이디
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			

			// 결제정보 저장 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
			String sttl_gds_clss = "1003";
			
			
				
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}	
			
			if(kvpMap != null) {
				ispAccessYn = "Y";
				pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
				ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
				valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
				if ( "2".equals(pcg) ) {
					pid = (String)kvpMap.get("BizId");								// 사업자번호
				} else {
					pid = (String)kvpMap.get("Pid");									// 개인 주민번호
				}
			} else {
				ispCardNo = parser.getParameter("isp_card_no","");	// 하나비자카드 경우
			}
			
			if ( valdlim.length() == 6 ) {
				valdlim = valdlim.substring(2);											
			}					

			//debug("// STEP 5. 승인처리");
			payEtt.setMerMgmtNo(merMgmtNo);
			payEtt.setCardNo(ispCardNo);
			payEtt.setValid(valdlim);			
			payEtt.setAmount(realPayAmt);
			payEtt.setInsTerm(insTerm);
			payEtt.setRemoteAddr(ip);				 

			if( "211.181.255.40".equals(host_ip)) {
				payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
			} else {
				payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
			}
			
			if("211.181.255.40".equals(host_ip)) {
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
			} else {
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
			}

			dataSet.setString("STTL_MINS_NM", "비씨카드");	// 신용카드 이름(계좌이체 은행이름)						  
			
			
			// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			dataSet.setString("CDHD_ID", cdhd_id);
			dataSet.setString("STTL_AMT", realPayAmt);				// 결제 금액
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());
			dataSet.setString("ORDER_NO", order_no);

			debug("결제관련 변수 셋팅 => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip);

			if (payResult) {
				// 결제 저장
				dataSet.setString("pgrs_yn", "G");
				dataSet.setString("cslt_yn", "1");
				addResult = proc.execute_upd(context, request, dataSet);
				
				if (addResult>0) {
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntPrimePay = addResult(결제 저장) : " + addResult);	
				}
			}else{ 
				veriResCode = "3";
				// 결제 실패내역 저장 + 신청내역 결제실패로 변경
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				dataSet.setString("pgrs_yn", "F");
				dataSet.setString("cslt_yn", "");
				result_fail += proc.execute_upd(context, request, dataSet);
				debug("GolfEvntPrimePay = result_fail : " + result_fail);											
			}

			if(addResult == 2 ){
				script = "alert('결제가 완료 되었습니다.'); parent.location.reload();";
			}else{
				veriResCode = "3";		
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
	        	}			
				script = "alert('결제과정에 오류가 있었습니다. 다시 시도해 주십시오.'); parent.location.reload();";
			}			

			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			veriResCode = "3";
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			
			if(ispAccessYn.equals("Y")){
			
				//ISP인증 로그 기록
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", memName);
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfEvntPrimePayActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
