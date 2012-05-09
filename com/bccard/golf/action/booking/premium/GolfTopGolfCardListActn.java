/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardListActn
*   작성자    : 이정규
*   내용      : Top골프카드 부킹 리스트 화면
*   적용범위  : Golf
*   작성일자  : 2010-10-18
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException; 
import java.util.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPenaltyDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardListActn extends GolfActn{
	
	public static final String TITLE = "Top골프카드 전용부킹 > 회원부킹";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체.  
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		String memSocId ="";
		String golfJoinDate = "";
		String roundDate = "";
		String memberClss = "";
		String memId = "";
		
		int memNo =  0;
		
		String strMemChkNum = "";		//회원종류 1:정회원 / 4: 비회원 / 5:법인회원
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String topGolfCardNo 	= "";
		String topGolfCardYn 	= "N";		//탑골프카드 소지 여부
		String getPassId = "N";		//강제등록아이디 
		String coMemType ="" ;				//카드
		
		try {
			
			// 01.세션정보체크 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				
				memId = userEtt.getAccount();				// 회원 아이디
				memNo = userEtt.getMemid();					//멤버 고유번호
				intMemGrade = userEtt.getIntMemGrade();	
				memberClss= userEtt.getStrMemChkNum();		// 1:정회원 / 4: 비회원 / 5:법인회원
				coMemType = userEtt.getStrCoMemType();		// 2:회계담당자(공용) 6:법인카드(지정)
				if("5".equals(memberClss)){
					if("6".equals(coMemType))
						memSocId = userEtt.getSocid();
						
					else{
						memSocId = userEtt.getStrCoNum();		//법인카드(공용) - 사업자 등록번호
					}
					
					
				}else{
					memSocId = userEtt.getSocid();			//카드법인카드(지정) -  주민등록번호
				}
				roundDate = DateUtil.currdate("yyyyMMdd");
				
			}
			
			/*
			 * top골프 카드 회원인지 체크
			 * */
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			//회원아이디
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			try {
				List topGolfCardList = mbr.getTopGolfCardInfoList();
				CardInfoEtt cardInfoTopGolfEtt = new CardInfoEtt();
				
				if( topGolfCardList!=null && topGolfCardList.size() > 0 )
				{
					for (int i = 0; i < topGolfCardList.size(); i++) 
					{
						cardInfoTopGolfEtt = (CardInfoEtt)topGolfCardList.get(0);
						topGolfCardNo = cardInfoTopGolfEtt.getCardNo();
						golfJoinDate = cardInfoTopGolfEtt.getAcctDay();		//join
						
						topGolfCardYn = "Y";
						debug("## 탑골프카드 소지 회원 | topGolfCardNo : "+topGolfCardNo); 
						
						
						
					}
					if("Y".equals(topGolfCardYn)){
						/**golfloung에서 아이디가 있는지 확인 없으면 약관 동의 페이지로 이동*/
						
						GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
						dataSet.setString("memId", memId);					//회원아이디
						int isYn = (int)proc.is_topMember(context, request, dataSet);		//티타임 신청자수
						debug("@@@@@@isYn : "+isYn);
						if(isYn < 1 ){
							return super.getActionResponse(context, "join");		//약관동의 페이지로 이동
						}
					}
					//golfCardCoYn = mbr.getGolfCardCoYn();
				}
				else 
				{
					topGolfCardYn = "N";
					debug("## 탑골프카드 미소지");					
				}
			} catch(Throwable t) 
			{
				topGolfCardYn = "N";
				debug("## 탑골프카드 체크 에러");	
			}
			/////////////////////////////////////////////////////////////////////////////////////
			/*
			 * 강제 적용 아이디
			 */
			if( memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ||memId.equals("altec16") || memId.equals("bcgolf2")|| memId.equals("leekj76")){
				topGolfCardYn 	= "Y";	
			}
			/////////////////////////////////////////////////////////////////////////////////////
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	
			if(topGolfCardYn.equals("Y")){
				// 02.입력값 조회		
				int defaultDate			= parser.getIntParameter("defaultDate", 5);
				paramMap.put("defaultDate", String.valueOf(defaultDate));
							
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
				dataSet.setInt("defaultDate", defaultDate);
				dataSet.setInt("intMemGrade",	intMemGrade);
				// 04.실제 테이블(Proc) 조회
			        
			        // 05. 달력그리는 변수 저장
			        GregorianCalendar today = new GregorianCalendar ( );
			        today.add(Calendar.DATE, defaultDate);
			        String [] dayOfWeek = {"","일","월","화","수","목","금","토"};
			        
			        int nYear = 0;
			        int nMonth = 0;
			        int nDay = 0; 
			        int nYoil = 0; 
			        int nHour = today.get ( today.HOUR_OF_DAY);
			        //debug("==============nHour==============" + nHour);
			        String nDate = "";
			        String divDate = "";
			        String clickDate = "";
			        String isWeekend = "";
		
			        
			        for (int d=0; d<14; d++){
			        	
				        nYear = today.get ( today.YEAR );
				        nMonth = today.get ( today.MONTH ) + 1;
				        nDay = today.get ( today.DAY_OF_MONTH ); 
				        nYoil = today.get ( today.DAY_OF_WEEK );
		
				        nDate = "";
				        divDate = "";
				        clickDate = "";
				        isWeekend = "";
				        
				        // 리스트 상단 날짜
				        if (nMonth<10) nDate = "0";
				        nDate = nDate + nMonth + ".";
				        if (nDay<10) nDate = nDate + "0";
				        nDate = nDate + nDay + "<br>("+dayOfWeek[nYoil]+")";
				        
				        // 레이어 출력용 날짜
				        if (nMonth<10) divDate = "0";
				        divDate = divDate + nMonth + "/";
				        if (nDay<10) divDate = divDate + "0";
				        divDate = divDate + nDay + " ("+dayOfWeek[nYoil]+")";
				        
				        // 주말여부
				        if ((nYoil==1) || (nYoil==7)){ isWeekend = "Y"; } else { isWeekend = "N"; }
		
						paramMap.put("nYear"+d, String.valueOf(nYear));
						paramMap.put("nMonth"+d, String.valueOf(nMonth));
						paramMap.put("nDay"+d, String.valueOf(nDay));
						paramMap.put("nDate"+d, String.valueOf(nDate));
						paramMap.put("divDate"+d, String.valueOf(divDate));
						paramMap.put("isWeekend"+d, String.valueOf(isWeekend));
				        today.add(Calendar.DATE, 1);
			        	
			        }	                
			      
			        dataSet.setInt("nHour",	nHour);
			        GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
					DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
					
					dataSet.setString("memId", memId);					//회원아이디
					dataSet.setInt("memNo", memNo);						//회원고유번호
					dataSet.setString("memSocId", memSocId);			//주민,사업번호
					dataSet.setString("golfJoinDate", golfJoinDate);	//top카드 발급일
					dataSet.setString("roundDate", roundDate);			//오늘날짜
					dataSet.setString("memberClss", memberClss);
					dataSet.setString("getPassId",getPassId);
					DbTaoResult getScore = (DbTaoResult) proc.get_score(context, request, dataSet);
					paramMap.put("topGolfCardYn", topGolfCardYn);
					paramMap.put("resultSize", String.valueOf(listResult.size()));
					
					request.setAttribute("listResult", listResult);		
					request.setAttribute("topGolfCardYn", topGolfCardYn);
					request.setAttribute("getScore", getScore);
					
			}
			paramMap.put("topGolfCardYn", topGolfCardYn);
	        request.setAttribute("paramMap", paramMap);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
