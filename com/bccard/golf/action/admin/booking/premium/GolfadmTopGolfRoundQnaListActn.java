/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfRoundQnaListActn
*   작성자    : shin cheong gwi
*   내용      : 질문과답변
*   적용범위  : golfloung
*   작성일자  : 2010-11-24
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfRoundAfterViewProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfRoundQnaListActn extends GolfActn {

	public static final String TITLE = "관리자 > 라운딩 질문과답변"; 
	
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
			
			String tab_idx = parser.getParameter("tab_idx", "2");
			String board_cd = parser.getParameter("board_cd", "52");						// 게시물번호			
			String actn_key = parser.getParameter("actn_key", "");							// 게시물 액션
			String search_type = parser.getParameter("search_type2", "BOARD_SUBJ");			// 검색여부
			String search_word = parser.getParameter("search_word2", "");					// 검색어	
			//String add_yn = parser.getParameter("add_yn", "");			
			long pageNo2 = parser.getLongParameter("pageNo2", 1L); 							// 페이지번호			
			long recordsInPage2 = parser.getLongParameter("recordsInPage2", 10L); 			// 페이지당출력수			
			long totalPage2 = 0L;															// 전체페이지수
			long recordCnt2 = 0L; 
			
			actn_key = parser.getParameter("actn_key") == null ? "qna" : actn_key;			
						
			// 02.Proc 에 던질 값 세팅 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("board_cd", board_cd);
				//dataSet.setString("board_dtl_cd", board_dtl_cd);
				dataSet.setLong("pageNo", pageNo2);
				dataSet.setLong("recordsInPage", recordsInPage2);					
				dataSet.setString("search_type", search_type);
				dataSet.setString("search_word", search_word);
				dataSet.setString("add_yn", "");			
			
			// 03.Proc 실행
			GolfAdmTopGolfRoundAfterListProc instance = GolfAdmTopGolfRoundAfterListProc.getInstance();				// 내역			
			DbTaoResult qnaList = instance.execute(context, request, dataSet);		
			
			//GolfAdmTopGolfRoundAfterViewProc instance2 = GolfAdmTopGolfRoundAfterViewProc.getInstance();			// Q&A 답변글..			
			
			if(qnaList.isNext()){
				qnaList.next();
				if(qnaList.getString("RESULT").equals("00")){
					paramMap.put("recordCnt2", String.valueOf(qnaList.getLong("RECORD_CNT")));
					recordCnt2 = qnaList.getLong("RECORD_CNT");					
				}else{
					paramMap.put("recordCnt2", "0");					
					recordCnt2 = 0L;
				}
			}
			
			totalPage2 = (recordCnt2 % recordsInPage2 == 0) ? (recordCnt2 / recordsInPage2) : (recordCnt2 / recordsInPage2) + 1;
						
			request.setAttribute("qnaList", qnaList);
			paramMap.put("qnaList", qnaList);						
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("search_type2", search_type);
			paramMap.put("search_word2", search_word);
			paramMap.put("board_cd", board_cd);
			paramMap.put("actn_key", actn_key);			
			paramMap.put("pageNo2", String.valueOf(pageNo2));			
			paramMap.put("recordsInPage2", String.valueOf(recordsInPage2));	
			paramMap.put("totalPage2", String.valueOf(totalPage2));
			request.setAttribute("paramMap", paramMap);					
			viewType = actn_key;
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}
