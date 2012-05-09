/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardRoundingAfterListActn
*   작성자    : 장성재
*   내용      : 탑골프카드 라운딩 후기
*   적용범위  : Golf
*   작성일자  : 2010-11-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardListDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardRoundingAfterListDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardRoundingAfterListActn extends GolfActn{
	
	public static final String TITLE = "탑골프카드 라운딩 후기";

	/***************************************************************************************
	* 골프 관리자화면
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
		String memberClss = "";
		String memId = "";
		
		int memNo =  0;
		
		String strMemChkNum = "";		//회원종류 1:정회원 / 4: 비회원 / 5:법인회원
		// 00.레이아웃 URL 저장
		String topGolfCardNo 	= "";
		String topGolfCardYn 	= "N";		//탑골프카드 소지 여부
		
		try {
			
			// 01.세션정보체크 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
						
			
			if(userEtt != null){
				memId = userEtt.getAccount();				// 회원 아이디
				memNo = userEtt.getMemid();
			}

debug("memId :" +memId  );			
debug("memNo :" +memNo  );			
			
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
						topGolfCardYn = "Y";
						debug("## 탑골프카드 소지 회원 | topGolfCardNo : "+topGolfCardNo);
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
			if(	memId.equals("altec16") || 
				memId.equals("amazon6") || 
				memId.equals("graceyang") ||  
				memId.equals("mongina") || 
				memId.equals("msj9529") ||
				memId.equals("sava7")){
				topGolfCardYn 	= "Y";	
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL"));

			// Request 값 저장
			long pageNo		= parser.getLongParameter("pageNo", 1L);			// 페이지번호
			long recordSize	= parser.getLongParameter("recordSize", 10);		// 페이지당출력수
			String searchSel  = parser.getParameter("searchSel", "");
			String searchWord = parser.getParameter("searchWord", "");

debug("pageNo :" +pageNo  );			
debug("recordSize :" +recordSize  );			
debug("searchSel :" +searchSel  );			
debug("searchWord :" +searchWord  ); 			
			
			String boardCd		= parser.getParameter("boardCd", "12");
			if("".equals(boardCd) || boardCd == null) {
				boardCd = "12"; 
			}
debug("boardCd :" +boardCd  );			
			
//			paramMap.put("boardCd", boardCd);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("pageNo", pageNo);
			dataSet.setLong("recordSize", recordSize);
			dataSet.setString("boardCd", boardCd);
			dataSet.setString("searchSel", searchSel);
			dataSet.setString("searchWord", searchWord);

			
debug("pageNo :" +dataSet.getLong("pageNo")  );			
debug("recordSize :" +dataSet.getLong("recordSize" )  );			
debug("boardCd :" +dataSet.getString("boardCd")  );			
debug("searchSel :" +dataSet.getString("searchSel")  );			
debug("searchWord :" +dataSet.getString("searchWord")  );			
			
			
			// 04.실제 테이블(Proc) 조회
			GolfTopGolfCardRoundingAfterListDaoProc proc = (GolfTopGolfCardRoundingAfterListDaoProc)context.getProc("GolfTopGolfCardRoundingAfterListDaoProc");
			DbTaoResult bbsListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 전체 0건  [ 0/0 page] 형식 가져오기
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L; 
			
			if (bbsListResult != null && bbsListResult.isNext()) {
				bbsListResult.first();
				bbsListResult.next();
				if (bbsListResult.getObject("RESULT").equals("00")) {
//					totalRecord = Long.parseLong((String)bbsListResult.getString("RECORD_CNT"));
					totalRecord = bbsListResult.getLong("RECORD_CNT");
//					currPage = Long.parseLong((String)bbsListResult.getString("CURR_PAGE"));
					currPage = pageNo;
					totalPage = (totalRecord % recordSize == 0) ? (totalRecord / recordSize) : (totalRecord / recordSize)+1;
				}
			}

debug("totalRecord :" +totalRecord  );	 
debug("currPage :" +currPage  );
debug("totalPage :" +totalPage  );


			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("resultSize", String.valueOf(bbsListResult.size()));
			paramMap.put("topGolfCardYn", topGolfCardYn);
			
debug("totalRecord :" +paramMap.get("totalRecord" ));	
debug("currPage :" +paramMap.get("currPage" ));	
debug("totalPage :" +paramMap.get("totalPage" ));	
debug("resultSize :" +paramMap.get("resultSize" ));	
debug("topGolfCardYn :" +paramMap.get("topGolfCardYn" ));	
			
			request.setAttribute("bbsListResult", bbsListResult);
//			request.setAttribute("codeSelResult", codeSel);
			request.setAttribute("recordSize", String.valueOf(recordSize));
	        request.setAttribute("paramMap", paramMap);
		        
debug("bbsListResult :" +request.getAttribute("bbsListResult"));	
debug("recordSize :" +request.getAttribute("recordSize"));	
debug("paramMap :" +request.getAttribute("paramMap"));	
		
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
