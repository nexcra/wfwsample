/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfRoundAfterListActn
*   작성자    : shin cheong gwi
*   내용      : 라운딩후기
*   적용범위  : golfloung
*   작성일자  : 2010-11-22
************************** 수정이력 ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfRoundAfterListProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfRoundAfterListActn extends GolfActn{

	public static final String TITLE = "관리자 > 라운딩 후기"; 
	
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
			String actn_key = parser.getParameter("actn_key", "");			// 게시물 액션
			String search_type = parser.getParameter("search_type", "BOARD_SUBJ");	// 검색여부
			String search_word = parser.getParameter("search_word", "");	// 검색어	
			//String add_yn = parser.getParameter("add_yn", "");
			long pageNo = parser.getLongParameter("pageNo", 1L); 			// 페이지번호			
			long recordsInPage = parser.getLongParameter("recordsInPage", 10L); // 페이지당출력수			
			long totalPage = 0L;			// 전체페이지수
			long recordCnt = 0L; 
						
			actn_key = parser.getParameter("actn_key") == null ? "after" : actn_key;			
						
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
			GolfAdmTopGolfRoundAfterListProc instance = GolfAdmTopGolfRoundAfterListProc.getInstance();			
			DbTaoResult roundList = null;			
			roundList = instance.execute(context, request, dataSet);
						
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
			debug("전체사이즈:"+roundList.size());   
			totalPage = (recordCnt % recordsInPage == 0) ? (recordCnt / recordsInPage) : (recordCnt / recordsInPage) + 1;
			
			request.setAttribute("roundList", roundList);	
			paramMap.put("listSize", String.valueOf(roundList.size()));
			paramMap.put("roundList", roundList);			
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("search_type", search_type);
			paramMap.put("search_word", search_word);
			paramMap.put("board_cd", board_cd);
			paramMap.put("actn_key", actn_key);
			paramMap.put("pageNo", String.valueOf(pageNo));			
			paramMap.put("recordsInPage", String.valueOf(recordsInPage));			
			paramMap.put("totalPage", String.valueOf(totalPage));
			request.setAttribute("paramMap", paramMap);				
			viewType = actn_key;
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}
