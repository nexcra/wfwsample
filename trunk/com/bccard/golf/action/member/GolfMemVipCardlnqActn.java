/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemVipCardlnqActn
*   작성자    : 미디어포스 권영만
*   내용      : 가입 > VIP카드
*   적용범위  : golf 
*   작성일자  : 2010-09-14
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemVipCardlnqActn extends GolfActn{
	
	public static final String TITLE = "가입 > VIP카드";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		
		try {
			// 01.세션정보체크
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);			
			
			String select_grade_no = "";
			String toDate  = DateUtil.currdate("yyyyMMdd");			
			String fromDate =  DateUtil.dateAdd('M', -3, toDate, "yyyyMMdd");
			fromDate = fromDate.substring(0,6) + "01";
			double sum_money = 0;
			double select_sum_money = 0;
			double temp_sum_money = 0;
			String select_card_no = "";
			//String select_bank_no = "";
			String vipCardPayAmt	= "0";
			//String vipCardYn 		= "N";
			
			List cardVipList = mbr.getCardVipInfoList();
			List lgCardList = new ArrayList();	
			if(mbr != null)
			{
				select_grade_no = mbr.getVipMaxGrade();
				
				debug("## VIP카드 소지 체크 시작 | ID : "+usrEntity.getAccount()+" | select_grade_no : "+select_grade_no);
				
				// SBS골프멤버쉽 회원으로 5천원 공제받은 회원은 2만원 승인 
				// 자료가 있다면 5천원 할인 제외 => 2만원 승인(실적체크 OK 되더라도 2만원 승인)
				// 자료가 없다면 기존 로직 처리 (1만5천원 승인, 실적체크에 따른 승인)
				int resultCk = 0;
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("socid", usrEntity.getSocid());	
				GolfMemCardInsDaoProc proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
				
				try{
					resultCk = proc.sbsMemberCk(context, dataSet, request);
				}catch(Throwable t){}
								
				if( cardVipList!=null && cardVipList.size() > 0 )
				{
					
					if(!"00".equals(select_grade_no))	// 플래티넘 회원일 경우	
					{
						
						for (int i = 0; i < cardVipList.size(); i++) 
						{
							try { 
								
								vipCardPayAmt = "";
								sum_money = 0;
								int usedMoney = 0;
								CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
								String grade 		= (String)record.getVipGrade();
								String expDate 		= (String)record.getExpDate();
								String cardType 	= (String)record.getCardType();
								String cardNo 		= StrUtil.isNull((String)record.getCardNo(), ""); 
								String last_cardApp = (String)record.getCardAppType();
								String last_cardNo 	= (String)record.getLastCardNo();
								String bankNo 		= (String)record.getBankNo();
								String reg_date 	= StrUtil.isNull((String)record.getAppDate(), "");
								String cardNm	 	= StrUtil.isNull((String)record.getJoinName(), "");
								String cardJoinDate	= StrUtil.isNull((String)record.getCardJoinDate(), "");
								
								CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
								
								try{
								
									cardVipInfo.setJoinName(cardNm);	//카드명
									String newRegDate = "";
									if(!"".equals(reg_date))
									{
										newRegDate =  reg_date.substring(0, 4)+"."+reg_date.substring(4, 6)+"."+reg_date.substring(6, 8);
									}
									cardVipInfo.setAppDate(newRegDate);	//등록날짜
									
									String newCardNo = "";
									if(!"".equals(cardNo))
									{
										newCardNo = cardNo.substring(0, 4)+"-"+cardNo.substring(4, 8)+"-****-"+cardNo.substring(12, 16);
									}
									cardVipInfo.setCardNo(newCardNo);	//카드번호
									cardVipInfo.setCardAppType(cardNo);
																		
								
								}catch(Throwable t){}
								
								
								
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
								
								
								// PT카드가  03,12,30,91 고객일경우 1만5천원 승인
								if( "03".equals(grade) || "12".equals(grade) || "30".equals(grade) || "91".equals(grade)  )
								{
									vipCardPayAmt = "15000";
								
								}
								
								if( "03".equals(grade) || "12".equals(grade) )
								{
									
									if("00".equals(expDate)){
										
										// 1:본인,2:가족,3:지정,4:공용
										if("1".equals(cardType) || "3".equals(cardType))
										{
											sum_money = getSumMoney(cardNo,cardType,sum_money,context,request,response);
											usedMoney = (int) sum_money;
											debug("## GolfMemVipCardlnqActn | 현재 카드번호 : "+cardNo+" | 실적금액 : "+usedMoney+ " | cardJoinDate : "+cardJoinDate);
											
											
											
											
											if(last_cardApp.equals("21") || last_cardApp.equals("24") || last_cardApp.equals("31") || last_cardApp.equals("33") || last_cardApp.equals("35") || last_cardApp.equals("37")){
												sum_money = getSumMoney(last_cardNo,cardType,sum_money,context,request,response);
												usedMoney = (int) sum_money;
												debug("## GolfMemVipCardlnqActn | 이전 카드번호 : "+last_cardNo+" | 실적금액 : "+usedMoney);
											}
											
										}
										
										
									}
									
									
									//카드가 발급날짜가 3개월 이내일 경우 체크
									String ckDate =  DateUtil.dateAdd('M', 3, cardJoinDate, "yyyyMMdd");											
									debug("## GolfMemVipCardlnqActn | 카드발급날짜 비교 | 오늘날짜 : "+toDate+" | 카드발급날짜 : "+cardJoinDate+ " | 카드3개월비교날짜 : "+ckDate);
									
									if( Integer.parseInt(toDate) < Integer.parseInt(ckDate) )
									{
										debug("## GolfMemVipCardlnqActn | 3개월이내 발급카드입니다. last_cardApp : "+last_cardApp+" | 카드발급 3개월 이내 등");
										sum_money = 10000000; //신규면 실적 천만원 넣어 우선부여.....
										usedMoney = (int) sum_money;
									}
									
									
									
									/*
									if(!(Integer.parseInt(fromDate) <= Integer.parseInt(reg_date))){
	
									} else {
										//단, 30만원 미만인경우 최근 최초 카드발급일자로 3개월 미만 고객은 1만 5천원 승인. 3개월 경과 회원은 2만원 승인
										//단, 신규 3개월 이내에 훼손재발급, 분실재발급 등 카드를 재발급 재변경시도 실적체크에서 예외처리 한다.(1만5천원 승인 해줌)
										
										
										
																				
										if (last_cardApp.equals("11") || last_cardApp.equals("12") || last_cardApp.equals("21") || last_cardApp.equals("22") || last_cardApp.equals("24") || last_cardApp.equals("25")   ) {
											debug(" last_cardApp : "+last_cardApp+" | 신규 3개월 이내에 훼손재발급, 분실재발급 등");
											sum_money = 10000000; //신규면 실적 천만원 넣어 우선부여.....
											usedMoney = (int) sum_money;
										}
										
										
										
									}
									*/	
									temp_sum_money = usedMoney;
									
									// 실적 많은 카드,회원사,실적이 우선
									if ( select_sum_money < temp_sum_money )	{
										select_sum_money = temp_sum_money;
										select_card_no = cardNo;
										//select_bank_no = bankNo;
									}
									
									if (usedMoney >= 300000) {
										
										vipCardPayAmt = "15000";
										
									}else if (usedMoney < 300000) {
										
										vipCardPayAmt = "20000";
										
									}
								
								}
																
								
								//SBS회원일경우 무조건 2만원 
								if(resultCk>0)
								{
									vipCardPayAmt = "20000";
								}
								
								
								
								
								cardVipInfo.setUsedAmt(usedMoney+"");
								cardVipInfo.setPayAmt(vipCardPayAmt+"");														
								
								lgCardList.add(cardVipInfo);
								
								debug("## "+i+"번째 | ID : "+usrEntity.getAccount()+" | grade : "+grade+" | cardNo : "+cardNo+" | 실적 : "+sum_money+" | 결제금액 : "+vipCardPayAmt+" | bankNo : "+bankNo+" | reg_date : "+reg_date+" | last_cardApp : "+last_cardApp+" | cardType : "+cardType);												
								
								
							} catch(Throwable t) {}
							
						
						}
						
									
						
						
												
						debug("## 실적금액 제일 실적많은 카드 ID : "+usrEntity.getAccount()+" | select_sum_money : " + select_sum_money+" | sum_money : "+sum_money);
						
						
						
						
						
					}
					else
					{
						debug("## VIP플래티늄 회원 아님");						
					}
					
					
				
				}
				else
				{
					debug("## VIP카드 소지 안함.");	
				}
				
				
			}
			System.out.print("## VIP카드 ID : "+usrEntity.getAccount()+" | 최종 결제금액 : vipCardPayAmt : "+vipCardPayAmt+" | select_card_no : "+select_card_no+" \n");
			
			
			
			
			
			// 05. Return 값 세팅					
			//paramMap.put("join_chnl", join_chnl);
			paramMap.put("vipCardPayAmt", vipCardPayAmt);
	        request.setAttribute("paramMap", paramMap); 
	        request.setAttribute("vipCardPayAmt", vipCardPayAmt);
			request.setAttribute("select_card_no", select_card_no);
			request.setAttribute("lgCardList", lgCardList); 
			
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
