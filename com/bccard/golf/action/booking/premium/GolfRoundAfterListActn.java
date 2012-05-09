/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRoundAfterListActn
*   작성자    : shin cheong gwi
*   내용      : 라운딩후기
*   적용범위  : golfloung
*   작성일자  : 2010-11-09
************************** 수정이력 ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.GolfRoundAfterListProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfRoundAfterListActn extends GolfActn { 

	public static final String TITLE = "라운딩 후기"; 
	
	// 부킹&라운딩 후기 내역
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default";
		
		try
		{ 
			// 01.입력값 조회		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);			
			paramMap.put("title", TITLE);			
			
			String tab_idx = parser.getParameter("tab_idx", "1");
			String board_cd = parser.getParameter("board_cd", "12");			// 게시물번호			
			String actn_key = parser.getParameter("actn_key", "after");			// 게시물 액션
			String search_type = parser.getParameter("search_type", "BOARD_SUBJ");	// 검색여부
			String search_word = parser.getParameter("search_word", "");	// 검색어	
			//String add_yn = parser.getParameter("add_yn", "");
			long pageNo = parser.getLongParameter("pageNo", 1L); 			// 페이지번호			
			long recordsInPage = parser.getLongParameter("recordsInPage", 10L); // 페이지당출력수			
			long totalPage = 0L;			// 전체페이지수
			long recordCnt = 0L; 						
			//actn_key = parser.getParameter("actn_key") == null ? "after" : actn_key;	
			int totalSize = 0;
			
			String topGolfCardNo 	= "";
			String topGolfCardYn 	= "N";		//탑골프카드 소지 여부
			//String getPassId = "N";		//강제등록아이디 
			String coMemType ="" ;				//카드
			String memberClss = "";
			String memId = "";
			int memNo =  0;
			int intMemGrade = 0;
			String memSocId ="";
			
			// 02.Proc 에 던질 값 세팅 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				//dataSet.setString("board_cd", board_cd);
				//dataSet.setString("board_dtl_cd", board_dtl_cd);
				dataSet.setString("search_type", search_type);
				dataSet.setString("search_word", search_word);
				dataSet.setString("add_yn", "");	
				dataSet.setString("board_cd", board_cd);
				dataSet.setLong("pageNo", pageNo);
				dataSet.setLong("recordsInPage", recordsInPage);
					
			// 03.Proc 실행
			GolfRoundAfterListProc instance = null;
			DbTaoResult roundList = null;
			instance = GolfRoundAfterListProc.getInstance();			
			roundList = instance.execute(context, request, dataSet);
			totalSize = roundList.size();
			debug("전체사이즈:"+totalSize);   
						
			if(roundList.isNext()){
				roundList.next();
				if(roundList.getString("RESULT").equals("00")){					
					paramMap.put("recordCnt", String.valueOf(roundList.getLong("RECORD_CNT")));
					recordCnt = roundList.getLong("RECORD_CNT");
				}else{
					paramMap.put("recordCnt", "0");
					recordCnt = 0L;
				}
			} 
			
			totalPage = (recordCnt % recordsInPage == 0) ? (recordCnt / recordsInPage) : (recordCnt / recordsInPage) + 1;
			
			// 04.세션정보체크 
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
			}
			
			// 05. top골프 카드 회원인지 체크			 			
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
						
						topGolfCardYn = "Y";		// 탑골프카드 소지자										
					}
					if("Y".equals(topGolfCardYn)){
						/**golfloung에서 아이디가 있는지 확인 없으면 약관 동의 페이지로 이동*/
						
						GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
						dataSet.setString("memId", memId);					//회원아이디
						int isYn = (int)proc.is_topMember(context, request, dataSet);		//티타임 신청자수						
						if(isYn < 1 ){
							return super.getActionResponse(context, "join");		//약관동의 페이지로 이동
						}
					}					
				}
				else 
				{
					topGolfCardYn = "N";			// 탑골프카드 미소지자						
				}			
				
			} catch(Throwable t) 
			{
				topGolfCardYn = "N";
				debug("## 탑골프카드 체크 에러");	
			}				
			
			// 06. 강제적용아이디
			if( memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ||memId.equals("altec16") || memId.equals("bcgolf2")|| memId.equals("leekj76")){
				topGolfCardYn 	= "Y";	
			}
			
			debug("totalPage====>"+totalPage+"====>recordCnt===>"+recordCnt+"===>listSize===>"+roundList.size());
			
			request.setAttribute("roundList", roundList);	
			paramMap.put("listSize", String.valueOf(roundList.size()));
			//paramMap.put("roundList", roundList);			
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("search_type", search_type);
			paramMap.put("search_word", search_word);
			paramMap.put("board_cd", board_cd);
			paramMap.put("actn_key", actn_key);
			paramMap.put("pageNo", String.valueOf(pageNo));			
			paramMap.put("recordsInPage", String.valueOf(recordsInPage));			
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("topGolfCardYn", topGolfCardYn);
			request.setAttribute("paramMap", paramMap);				
			viewType = actn_key;
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}
