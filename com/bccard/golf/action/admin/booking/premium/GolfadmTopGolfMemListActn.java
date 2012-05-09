/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfMemListActn
*   작성자    : shin cheong gwi
*   내용      : 가결제 여부 회원검색
*   적용범위  : golfloung
*   작성일자  : 2010-12-07
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopGolfMemListProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfMemListActn extends GolfActn {

	public static final String TITLE = "관리자 > 가결제 여부 내역 > 회원검색";  
	
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
			
			long pageNo = parser.getLongParameter("pageNo2", 1L);						// 페이지번호
            long recordsInPage = parser.getLongParameter("recordsInPage", 10L);			// 페이지당 출력수
            long totalPage = 0L;														// 전체페이지수
			long recordCnt = 0L; 
			String sh_id = parser.getParameter("sh_id", "");
			String sh_nm = parser.getParameter("sh_nm", "");
			String access = parser.getParameter("access", "");
			
			// 02.Proc 에 던질 값 세팅 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("sh_id", sh_id);
				dataSet.setString("sh_nm", sh_nm);
				dataSet.setLong("pageNo", pageNo);
				dataSet.setLong("recordsInPage", recordsInPage);
			
			// 03.Proc 실행
			GolfadmTopGolfMemListProc instance = GolfadmTopGolfMemListProc.getInstance();
			DbTaoResult memList = null;
			
			if(access.equals("search")){				
				memList = instance.execute(context, request, dataSet);
				if(memList.isNext()){
		        	memList.next();
		        	if(memList.getString("RESULT").equals("00")){
		        		paramMap.put("recordCnt", String.valueOf(memList.getLong("RECORD_CNT")));
		        		recordCnt = memList.getLong("RECORD_CNT");
		        	}else{ 
		        		paramMap.put("recordCnt", "0");
						recordCnt = 0L;
		        	}
		        }
				totalPage = (recordCnt % recordsInPage == 0) ? (recordCnt / recordsInPage) : (recordCnt / recordsInPage) + 1;
			}else{				
				recordCnt = 0L;
			}
						
			// 04. Parameter Set
			request.setAttribute("memList", memList);	  
			paramMap.put("recordCnt", String.valueOf(recordCnt));
	        paramMap.put("pageNo2",String.valueOf(pageNo));
	        paramMap.put("recordsInPage",String.valueOf(recordsInPage));
	        paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("sh_id", sh_id);
			paramMap.put("sh_nm", sh_nm);
			request.setAttribute("paramMap", paramMap);
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}