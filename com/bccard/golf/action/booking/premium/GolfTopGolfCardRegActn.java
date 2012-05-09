/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardRegActn
*   작성자    : 이정규
*   내용      :  Top골프카드 전용 부킹  > top골프 부킹 신청처리
*   적용범위  : Golf
*   작성일자  : 2010-10-21
************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.02.11    이경희	   ISP인증기록 추가
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardRegActn extends GolfActn{
	
	public static final String TITLE = " Top골프카드 전용 부킹  > top골프 부킹 신청처리";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout"); 
		request.setAttribute("layout", layout);
		
		String memb_id ="";
		String memSocid = "";
		String checkYn = "N";
		int memNo= 0;
		String strMemChkNum = "";
		String coMemType ="" ;				//카드
		String memName = "";
		
		String cstIP = request.getRemoteAddr(); //접속IP
		String host_ip 		= java.net.InetAddress.getLocalHost().getHostAddress();
		String ispCardNo   	= "";				// isp카드번호
		
		// 01.세션정보체크 
		UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
		
		if(userEtt != null){
			
			memb_id = userEtt.getAccount();				// 회원 아이디
			strMemChkNum = userEtt.getStrMemChkNum();	// 1:정회원 / 4: 비회원 / 5:법인회원
			memNo = userEtt.getMemid();
			memName = userEtt.getName();				//성명				
			coMemType = userEtt.getStrCoMemType();		// 2:회계담당자(공용) 6:법인카드(지정)
			
			if("5".equals(strMemChkNum)){
				//if("6".equals(coMemType))
				//	memSocid = userEtt.getSocid();		//카드법인카드(지정)
					
				//else{
					memSocid = userEtt.getStrCoNum();		//법인카드(공용) - 사업자 등록번호
				//}					
			}else{
				memSocid = userEtt.getSocid();			//-  주민등록번호
			}
			
		}
		
		try {
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";

			String green_nm						= parser.getParameter("GREEN_NM", "");
			String teof_date					= parser.getParameter("TEOF_DATE", "");
			String teof_time					= parser.getParameter("TEOF_TIME","");
			String co_nm						= parser.getParameter("CO_NM","");
			String cdhd_id						= parser.getParameter("CDHD_ID","");
			String email_id						= parser.getParameter("EMAIL_ID","");
			
			
			String hp_ddd_no					= parser.getParameter("HP_DDD_NO","");
			String hp_tel_hno					= parser.getParameter("HP_TEL_HNO","");
			String hp_tel_sno					= parser.getParameter("HP_TEL_SNO","");
			String memp_expl					= parser.getParameter("MEMO_EXPL","");
			String bkg_pe_nm					= parser.getParameter("BKG_PE_NM","");
			String breach_amt					= parser.getParameter("BREACH_AMT","");
			//AFFI_GREEN_SEQ_NO
			String affi_green_seq_no			= parser.getParameter("AFFI_GREEN_SEQ_NO","");
			String golf_rsvt_curs_nm            = parser.getParameter("GOLF_RSVT_CURS_NM","");
			
			String paramater ="GREEN_NM="+green_nm+"&TEOF_DATE="+teof_date+"&TEOF_TIME="+teof_time+"&CO_NM="+co_nm+"&CDHD_ID="+cdhd_id +
							  "&EMAIL_ID="+email_id+"&HP_DDD_NO="+hp_ddd_no+"&HP_TEL_HNO="+hp_tel_hno+"&HP_TEL_SNO="+hp_tel_sno+
							  "&MEMO_EXPL="+memp_expl+"&bkg_pe_nm="+cdhd_id+"&breach_amt="+breach_amt+"&AFFI_GREEN_SEQ_NO="+affi_green_seq_no;
			
			// ISP인증 체크
			String iniplug 		= parser.getParameter("KVPpluginData", "");					// ISP 인증값
			HashMap kvpMap 		= null;
			String pcg         	= "";														// 개인/법인 구분			
			String valdlim	   	= "";														// 만료 일자
			String pid 			= null;														// 개인아이디
			String gubun        = "";
			
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}	
			
			if(kvpMap != null) {
				ispAccessYn = "Y";
				pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
				ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
				valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
				gubun 		= (String)kvpMap.get("ChCode");					// 01:개인, 02:가족, 03:기업지정, 04: 기업공용,05:기프트카드
				if ("04".equals(gubun) || "03".equals(gubun)) {
					pid = (String)kvpMap.get("BizId");		 						// 사업자번호
				} else {
					pid = (String)kvpMap.get("Pid");									// 개인 주민번호
				}
			} else {
				ispCardNo = parser.getParameter("isp_card_no","");	// 하나비자카드 경우
			}
			
			if(memSocid.equals(pid)){
				checkYn = "Y";
			}
			
//			if(memb_id.equals("bcgolf2") || memb_id.equals("altec16")){
//				checkYn = "Y";	
//			}
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GREEN_NM", green_nm);
			dataSet.setString("TEOF_DATE", teof_date);
			dataSet.setString("TEOF_TIME", teof_time);
			dataSet.setString("CO_NM", co_nm);
			dataSet.setString("CDHD_ID", cdhd_id);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);  
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("MEMO_EXPL", memp_expl);
			dataSet.setString("BKG_PE_NM", bkg_pe_nm);
			dataSet.setString("MEMB_ID", memb_id);
			dataSet.setString("BREACH_AMT", breach_amt);
			dataSet.setInt("MEMNO", memNo);
			dataSet.setString("GOLF_RSVT_CURS_NM", golf_rsvt_curs_nm); //코스
			
			//가결제 테이블 데이터SET
			dataSet.setString("GREEN_NO", affi_green_seq_no);	//골프장no
			dataSet.setString("CARD_NO", ispCardNo);		//카드번호
			dataSet.setString("TEMP_PAY_DATE", valdlim);		//유효기간
			dataSet.setString("PAYPROC_ADMIN_NM", co_nm);		//유효기간
				
			// 04.실제 테이블(Proc) 조회
			if(checkYn.equals("Y")){
				
				GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");

				int cntAppCnt = (int)proc.execute_appCnt(context, request, dataSet);		//티타임 신청자수
				int cntJumin = (int) proc.execute_idsubmit(context, request, dataSet);		//티타임 주민등록 중복 여부
				
				//cntAppCnt = 20;
					if(cntJumin > 0){ 
						script = "alert('동일한 아이디의 신청내역이 있습니다.'); parent.location.href='GolfTopGolfCardList.do';";
						
					}else{
						if(cntAppCnt  > 20){
							
							script = "alert('신청가능한 신청자수를 넘었습니다.'); parent.location.href='GolfTopGolfCardList.do';";
						}
						else{
							int appInt = (int) proc.app_insert(context, request, dataSet);			//신청테이블에 등록
							
							if(cntAppCnt == 20){
								int appEnd = (int) proc.execute_epsYn(context, request, dataSet);		//티타임 신청종료
							}
							
							if(appInt>0){
								int insertTemp = (int)proc.inputTemp(context, request, dataSet);		//가결제 테이블에 데이터 등록
								
								if(insertTemp > 0){
									script = "alert('부킹등록이 처리되었습니다.'); parent.location.href='GolfTopGolfCardList.do';";
								}else{
									script = "alert('ISP데이터 입력이 잘못 되었습니다'); location.href='GolfTopGolfCardSubmit.do?"+paramater + "';";
								}
							}else{
								script = "alert('부킹등록이 처리되지 않았습니다. 다시 시도해 주시기 바랍니다.'); location.href='GolfTopGolfCardSubmit.do?"+paramater + "';";
							}
						}
					}
					
			}else{
				
				veriResCode = "3";
				script = "alert('아이디와  인증카드가 일치하지 않습니다.'); location.href='GolfTopGolfCardSubmit.do?"+paramater + "';";
				debug("script =  "+script);
				
			}
				
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			
			veriResCode = "3";
			debug(TITLE, t);
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
				hmap.put("className", "GolfTopGolfCardRegActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
			
			}

		}

		return super.getActionResponse(context, subpage_key);
		
	}
	
}
