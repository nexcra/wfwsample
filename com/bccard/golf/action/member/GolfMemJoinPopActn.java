/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemJoinPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 가입 팝업
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentViewDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfMemJoinPopActn extends GolfActn{
	
	public static final String TITLE = "회원 > 가입 팝업";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		int topPoint = 0;					// 포인트
		String golfPointComma = "";			// 컴마있는 포인트
        int nMonth = 0;						// 현재 월
        int nDay = 0; 						// 현재 일
        String golfDate = "";				// 출력 일자
        String presentText = "";			// 사은품 선택 텍스트
        Connection con = null;
        
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		String action_key = super.getActionKey(context);
		debug(action_key);

		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = parser.getParameterMap();
			paramMap.put("title", TITLE);
			

			String openerType 				= parser.getParameter("openerType", "").trim();
			String openerTypeRe 			= parser.getParameter("openerTypeRe", "").trim();
			String money					= parser.getParameter("money", "1"); //1 :챔피온 2:블루 3:골드
			String realPayAmt				= parser.getParameter("realPayAmt", "0"); 

			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");
			//-- 2009.11.12 추가 
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "");
			String idx 						= parser.getParameter("idx", "1");

			String gds_code 				= parser.getParameter("gds_code", "");
			String name 					= parser.getParameter("rcvr_nm", "");
			String zp1 						= parser.getParameter("zp1", "");
			String zp2 						= parser.getParameter("zp2", "");
			String zipaddr 					= parser.getParameter("addr", "");
			String detailaddr 				= parser.getParameter("dtl_addr", "");
			String addr_clss 				= parser.getParameter("addr_clss", "");
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
			String gds_code_name 			= parser.getParameter("gds_code_name", "");
			String formtarget 				= parser.getParameter("formtarget", "");
			String call_actionKey 			= parser.getParameter("call_actionKey", action_key);
			
			debug (" ### zp1 : " + zp1 + ", zp2 : "  + zp2 + ", zipaddr : " + zipaddr + ", detailaddr : " + detailaddr  + ", addr_clss : " + addr_clss);

			debug("formtarget : " + formtarget + " / idx : " + idx + " / openerType : " + openerType + " / openerTypeRe : " + openerTypeRe + " / code : " + code 
					+ " / evnt_no : " + evnt_no + " / cupn_ctnt : " + cupn_ctnt + " / cupn_amt : " + cupn_amt + " / cupn_type : " + cupn_type 
					+ " / pmgds_pym_yn : " + pmgds_pym_yn + " / gds_code_name : " + gds_code_name);

			if(openerType.equals("")){
				openerType = openerTypeRe;
			}
			paramMap.put("openerType", openerType);
			 
			// 01. 세션정보체크
			//debug("========= GolfMemJoinPopActn =========> ");
			HttpSession session	= request.getSession(true);	
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request); 
			
			UcusrinfoEntity coUserEtt   = (UcusrinfoEntity)session.getAttribute("COEVNT_ENTITY"); 
			
			String strMemChkNum = userEtt.getStrMemChkNum();
			String strEnterCorporation = "";
			
			if(coUserEtt != null)
			{
				strEnterCorporation = coUserEtt.getStrEnterCorporation();			
			}
			else
			{
				debug("coUserEtt null");
			}
			System.out.print("## GolfMemJoinPopActn |  | ID : "+userEtt.getAccount()+" | strMemChkNum :"+strMemChkNum+" | strEnterCorporation :"+strEnterCorporation+"\n");

			
			//VIP카드 체크 추가
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);			
			String select_grade_no = "";
			String toDate  = DateUtil.currdate("yyyyMMdd");			
			String fromDate =  DateUtil.dateAdd('M', -3, toDate, "yyyyMMdd");
			fromDate = fromDate.substring(0,6) + "01";
			double sum_money = 0;
			double select_sum_money = 0;
			double temp_sum_money = 0;
			String select_card_no = "";
			String vipCardPayAmt	= "0";
			String vipCardYn 	= "N";		//Vip카드 소지 여부
			if(mbr != null)
			{
				select_grade_no = mbr.getVipMaxGrade();
				
				debug("## VIP카드 소지 체크 시작 | select_grade_no : "+select_grade_no);
				List cardVipList = mbr.getCardVipInfoList();
								
				if( cardVipList!=null && cardVipList.size() > 0 )
				{
					
					if(!"00".equals(select_grade_no))	// 플래티넘 회원일 경우	
					{
						
						for (int i = 0; i < cardVipList.size(); i++) 
						{
							vipCardYn = "Y";
							try { 
								
							
								CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
								String grade 		= (String)record.getVipGrade();
								String expDate 		= (String)record.getExpDate();
								String cardType 	= (String)record.getCardType();
								String cardNo 		= (String)record.getCardNo(); 
								String last_cardApp = (String)record.getCardAppType();
								String last_cardNo 	= (String)record.getLastCardNo();
								String bankNo 		= (String)record.getBankNo();
								String reg_date 	= (String)record.getAppDate();
								String cardJoinDate	= StrUtil.isNull((String)record.getCardJoinDate(), "");
								
								debug("## "+i+"번째 | cardJoinDate : "+cardJoinDate+" | grade : "+grade+" | cardNo : "+cardNo+" | bankNo : "+bankNo+" | reg_date : "+reg_date+" | last_cardApp : "+last_cardApp+" | cardType : "+cardType);												
								
								//실적체크
								/*
								CardAppType
								11:신규, 12:추가신규, 21:훼손재발급, 22:등급변경재발급, 
								24:분실재발급, 25:등급변경분실재발급, 31:일반갱신, 32:등급변경일반갱신, 
								33:자동갱신, 34:등급변경자동갱신, 35:조기갱신, 36:등급변경조기갱신, 
								37:지연갱신, 38:등급변경지연갱신, 41:제신고
								CardType
								1:본인,2:가족,3:지정,4:공용
								*/
								if( "03".equals(grade) || "12".equals(grade) )
								{
									
									if(expDate.equals("00")){
										
										if(cardType.equals("1") || cardType.equals("3"))
										{
											sum_money = getSumMoney(cardNo,cardType,sum_money,context,request,response);
											debug("## 최종카드("+cardNo+"):"+sum_money);
											
											if(last_cardApp.equals("21") || last_cardApp.equals("24") || last_cardApp.equals("31") || last_cardApp.equals("33") || last_cardApp.equals("35") || last_cardApp.equals("37")){
												sum_money = getSumMoney(last_cardNo,cardType,sum_money,context,request,response);
												debug("## 이전카드("+last_cardNo+"):"+sum_money);
											}
											
										}
										
										
									}
									
									
									//실적체크
									//카드가 발급날짜가 3개월 이내일 경우 체크
									String ckDate =  DateUtil.dateAdd('M', 3, cardJoinDate, "yyyyMMdd");											
									debug("## GolfMemJoinPopActn | 카드발급날짜 비교 | 오늘날짜 : "+toDate+" | 카드발급날짜 : "+cardJoinDate+ " | 카드3개월비교날짜 : "+ckDate);
									
									if( Integer.parseInt(toDate) < Integer.parseInt(ckDate) )
									{
										debug("## GolfMemJoinPopActn | 3개월이내 발급카드입니다. last_cardApp : "+last_cardApp+" | 카드발급 3개월 이내 등");
										sum_money = 10000000; //신규면 실적 천만원 넣어 우선부여.....
									}
									
									/*
									if(!(Integer.parseInt(fromDate) <= Integer.parseInt(reg_date))){
	
									} else {
										//단, 30만원 미만인경우 최근 최초 카드발급일자로 3개월 미만 고객은 1만 5천원 승인. 3개월 경과 회원은 2만원 승인
										//단, 신규 3개월 이내에 훼손재발급, 분실재발급 등 카드를 재발급 재변경시도 실적체크에서 예외처리 한다.(1만5천원 승인 해줌)
										if (last_cardApp.equals("11") || last_cardApp.equals("12") || last_cardApp.equals("21") || last_cardApp.equals("22") || last_cardApp.equals("24") || last_cardApp.equals("25")   ) {
											debug(" last_cardApp : "+last_cardApp+" | 신규 3개월 이내에 훼손재발급, 분실재발급 등");
											sum_money = 10000000; //신규면 실적 천만원 넣어 우선부여.....
	
										}
									}
									*/	
									temp_sum_money = sum_money;
									
									// 실적 많은 카드,회원사,실적이 우선
									if ( select_sum_money < temp_sum_money )	{
										select_sum_money = temp_sum_money;
										select_card_no = cardNo;
										//select_bank_no = bankNo;
									}
									
									
									
									
									
									
								}
							} catch(Throwable t) {}
							//vipCardYn = "Y";
						
						}
						
						
						// PT카드가  03,12,30,91 고객일경우 1만5천원 승인
						if( "03".equals(select_grade_no) || "12".equals(select_grade_no) || "30".equals(select_grade_no) || "91".equals(select_grade_no)  )
						{
							vipCardPayAmt = "15000";
						
						}
						
						
						debug("## 실적금액 sum_money : " + sum_money);
						
						// 단, PT카드가 03 , 12 일 경우엔 최근3개월간 국내신판금액이 30만원 이상일 경우만 1만5천원 승인
						if( "03".equals(select_grade_no) || "12".equals(select_grade_no) )
						{
																		
							if (sum_money >= 300000) {
								
								vipCardPayAmt = "15000";
								
							}else if (sum_money < 300000) {
								
								vipCardPayAmt = "20000";
								
							}
						}
						
																		
						// SBS골프멤버쉽 회원으로 5천원 공제받은 회원은 2만원 승인 (별도 테이블 구성 후 데이타 넣을 예정)
					
					}
					else
					{
						vipCardYn = "N";
						debug("## VIP플래티늄 회원 아님");						
					}					
				}
				else
				{
					debug("## VIP카드 소지 안함.");	
					vipCardYn = "N";
				}
			}
			System.out.print("## VIP카드 최종 결제금액 : vipCardPayAmt : "+vipCardPayAmt+" | select_card_no : "+select_card_no+" \n");
			paramMap.put("vipCardPayAmt", vipCardPayAmt);
			request.setAttribute("vipCardPayAmt", vipCardPayAmt);
			request.setAttribute("select_card_no", select_card_no);
			request.setAttribute("vipCardYn", vipCardYn);		
			
			
			
			
			
			///////////////////////////////////////////////////////////////////////////////////////
			// 법인회원이 지정된 경로를 통하여 왔는지 체크하여 20% 할인쿠폰 적용 CORPDSMEM1102
			//
			if( "Y".equals(strEnterCorporation) )
			{				 
					//쿠폰적용
					System.out.print("## GolfMemJoinPopActn | 법인회원 20% 쿠폰 적용 시작 | ID : "+userEtt.getAccount()+"\n");
					code = "CORPDSMEM1102";
					
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
					
					dataSet.setString("CODE", code); //제휴업체코드
					dataSet.setString("SITE_CLSS", "10");//제휴업체코드
					dataSet.setString("EVNT_NO", "111");//제휴업체코드
					dataSet.setString("EVNT_NO2", "112");//무료쿠폰코드
					GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
					DbTaoResult codeCheck = proc.codeExecute(context, dataSet, request);
					debug("===================codeCheck : " + codeCheck);
					
					
					if (codeCheck != null && codeCheck.isNext()) 
					{
						codeCheck.first();
						codeCheck.next();
						debug("===================memGrade : " + codeCheck.getString("RESULT"));
						if(codeCheck.getString("RESULT").equals("00"))
						{
							
							evnt_no = (String) codeCheck.getString("EVNT_NO");
							cupn_ctnt = (String) codeCheck.getString("CUPN_CTNT");
							cupn_amt = ""+codeCheck.getInt("CUPN_AMT");
							cupn_clss = (String) codeCheck.getString("CUPN_CLSS");

							System.out.print("## GolfMemJoinPopActn | 쿠폰사용판단체크 | ID : "+userEtt.getAccount()+" | code : "+code+" | cupn_ctnt : "+cupn_ctnt+" | cupn_amt : "+cupn_amt+" | cupn_clss : "+cupn_clss+ "\n");
							
							request.setAttribute("strMemChkNum", strMemChkNum);	
							request.setAttribute("strEnterCorporation", strEnterCorporation);	
							
							
						}
						
					}
				
			}
			
			

			// 결제 jsp 에서 사용
			Random rand = new Random();
		    String st = String.valueOf( rand.nextInt(99999999) );
		    session.setAttribute("ParameterManipulationProtectKey",st);
			paramMap.put("ParameterManipulationProtectKey", st);
			

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("cpn_code", code); //제휴업체코드
			dataSet.setString("vipCardPayAmt", vipCardPayAmt);
			
			// 선택한 사은품 가져오기
			GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
			DbTaoResult presentView = present_proc.execute(context, dataSet, request);
			// 회원종류 가져오기
			DbTaoResult memView = present_proc.execute_mem(context, dataSet, request);

			
			// 02. 포인트 가져오기
			//debug("========= GolfMemJoinPopActn =========> 1-2");
			debug("주민등록번호 : " + userEtt.getSocid()); 
			GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
			try
			{			
				TopPointInfoEtt pointInfo = resetProc.getTopPointInfoEtt(context, request , userEtt.getSocid());
				topPoint = pointInfo.getTopPoint().getPoint();
			}
			catch(Throwable ignore) {}

			
			//topPoint = 50000;
			golfPointComma = GolfUtil.comma(topPoint+"");

	        GregorianCalendar today = new GregorianCalendar ( );
	        nMonth = today.get ( today.MONTH ) + 1;
	        nDay = today.get ( today.DAY_OF_MONTH ); 
			golfDate = nMonth+"월 "+nDay+"일";
			
			// 주문번호 가져오기 2009.12.28
			String order_no = "";
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			order_no = addPayProc.getOrderNo(context, dataSet);
			
			debug("## GolfMemJoinPopActn | order_no : " + order_no); 
			
			//paramMap.put("actionKey", action_key);
			paramMap.put("actionKey", call_actionKey);
			paramMap.put("golfPoint", topPoint+"");
			paramMap.put("golfPointComma", golfPointComma);
			paramMap.put("golfDate", golfDate);
			paramMap.put("userNM", userEtt.getName());	

			paramMap.put("idx", idx);
			paramMap.put("code", code);
			paramMap.put("evnt_no", evnt_no);
			paramMap.put("cupn_ctnt", cupn_ctnt);
			paramMap.put("cupn_amt", cupn_amt); 
			paramMap.put("cupn_clss", cupn_clss);
			//-- 2009.11.12 추가 
			paramMap.put("cupn_type", cupn_type); 
			paramMap.put("pmgds_pym_yn", pmgds_pym_yn);

			paramMap.put("gds_code", gds_code);
			paramMap.put("name", name);
			paramMap.put("zp1", zp1);
			paramMap.put("zp2", zp2);
			paramMap.put("zipaddr", zipaddr);
			paramMap.put("detailaddr", detailaddr);
			paramMap.put("addr_clss", addr_clss);
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("gds_code_name", gds_code_name);
			paramMap.put("order_no", order_no);

			paramMap.put("formtarget", formtarget);
			
			paramMap.put("present_call_url", action_key);

	        request.setAttribute("presentView", presentView);	
	        request.setAttribute("memView", memView);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	/***********************************************************************
	 * 실적체크 로직
	 **********************************************************************/
	public double getSumMoney(String cardNo, String cardType, double sum,WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, BaseException{

		
		JtProcess process = new JtProcess();
		String joltServiceName = "BSNINPT";
		String toDay  = DateUtil.currdate("yyyyMMdd");
		
		String toDate  = DateUtil.dateAdd('M', -1, toDay, "yyyyMMdd");
		int datcount = DateUtil.getMonthlyDayCount(
						Integer.parseInt(toDate.substring(0,4)),
						Integer.parseInt(toDate.substring(4,6))); // 해당월의 말일
		toDate = toDate.substring(0,6) + Integer.toString(datcount);
		debug("toDate : " + toDate);
		String fromDate =  DateUtil.dateAdd('M', -3, toDay, "yyyyMMdd");
		fromDate = fromDate.substring(0,6) + "01";		

		if(cardType.equals("1") || cardType.equals("3")){  //1:본인 3:법인지정
			// 2008-10-13 수정
			JoltInput entity = new JoltInput(joltServiceName);					
			entity.setServiceName(joltServiceName);
			entity.setString("fml_trcode", "MGA0100R1600");

			TaoResult jout = null;

			entity.setString("fml_arg1", cardNo);		// 번호: 개인:주민번호/기업:회원사회원번호/기업:카드번호				
			entity.setString("fml_arg2", "3");			// 개인,기업구분: '1':주민번호(개인),'2':회원사회원번호(기업)'3':카드번호(기업)
			entity.setString("fml_arg3", fromDate);		// 이용조회일_FROM, YYYYMMDD(기업한달)
			entity.setString("fml_arg4", toDate);		// 이용조회일_TO, YYYYMMDD(기업한달)
			entity.setString("fml_arg5", " ");			// ISP구분(1.ISP, 나머지 : SPACE)
			entity.setString("fml_arg7", "1");			// 이전카드실적포함

			jout = process.call(context, request, entity);

			String rescode = jout.getString("fml_ret1");
			
				if ("0".equals(rescode)) {
					sum = sum + jout.getDouble("fml_retd3") + jout.getDouble("fml_retd5");
					debug("sum_money### >> " + sum);
			} 
		}
		return sum;
	}
}
