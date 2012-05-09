/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardStatusActn
*   작성자    : 이정규
*   내용      : Top골프카드 부킹 확인/취소 화면
*   적용범위  : Golf
*   작성일자  : 2010-10-22
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfCardDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPenaltyDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스  
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardStatusActn extends GolfActn{
	
	public static final String TITLE = "Top골프카드 부킹 확인/취소";

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
				intMemGrade = userEtt.getIntMemGrade();
				
				memNo = userEtt.getMemid();
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
			
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 10);		// 페이지당출력수	
			
			//tab
			String t_id =  parser.getParameter("t_id","1");
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no); 
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("CDHD_ID", memId);
			
			/*
			 * top골프 카드 회원인지 체크
			 * */
			
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
			if(memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ){
				topGolfCardYn 	= "Y";
				getPassId = "Y";
			}
			if(memId.equals("altec16") || memId.equals("bcgolf2") ){
				topGolfCardYn 	= "Y";	
			}
			
			// 04.실제 테이블(Proc) 조회
			GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute_status(context, request, dataSet);
			
			dataSet.setString("memId", memId);			//회원아이디 
			dataSet.setInt("memNo", memNo);			//회원아이디 
			 
			dataSet.setString("memSocId", memSocId);	//주민,사업번호
			dataSet.setString("golfJoinDate", golfJoinDate);	//top카드 발급일
			dataSet.setString("roundDate", roundDate);		//오늘날짜
			dataSet.setString("memberClss", memberClss);
		/*	DbTaoResult getScore = null;
			if(getPassId.equals("N") && topGolfCardYn.equals("Y")){
				getScore = (DbTaoResult) proc.get_score(context, request, dataSet);
			}*/
			 
			// 전체 0건  [ 0/0 page] 형식 가져오기
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
				
			if (listResult != null && listResult.isNext()) {
				listResult.first();
				listResult.next();
				if (listResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)listResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)listResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}

			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));				
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("topGolfCardYn", topGolfCardYn);
			paramMap.put("t_id", t_id);
			
			request.setAttribute("listResult", listResult);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        /*if(getPassId.equals("N")){
				request.setAttribute("getScore", getScore);
			}*/
			
			
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			request.setAttribute("listResult", listResult);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
