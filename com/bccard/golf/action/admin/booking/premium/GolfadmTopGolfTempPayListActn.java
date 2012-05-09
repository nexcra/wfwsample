/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfTempPayListActn
*   작성자    : shin cheong gwi
*   내용      : 가결제 여부
*   적용범위  : golfloung
*   작성일자  : 2010-12-02
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopGolfTempPayListProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfTempPayListActn extends GolfActn {
	public static final String TITLE = "관리자 > 가결제 여부 내역";  
	
	// 가결제 내역
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
			
			long pageNo = parser.getLongParameter("pageNo", 1L);						// 페이지번호
            long recordsInPage = parser.getLongParameter("recordsInPage", 20);			// 페이지당 출력수
            long totalPage = 0L;														// 전체페이지수
			long recordCnt = 0L; 
            String sh_id = parser.getParameter("sh_id", "");							//검색키
			String sh_nm = parser.getParameter("sh_nm", "");							//검색키
			
			// 02.Proc 에 던질 값 세팅 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setLong("pageNo", pageNo);
	            dataSet.setLong("recordsInPage", recordsInPage);
	            dataSet.setString("sh_id", sh_id);
	            dataSet.setString("sh_nm", sh_nm);
			
			// 03.Proc 실행
	        GolfadmTopGolfTempPayListProc instance = GolfadmTopGolfTempPayListProc.getInstance();
	        DbTaoResult tempPayList = instance.execute(context, request, dataSet);
	        if(tempPayList.isNext()){
	        	tempPayList.next();
	        	if(tempPayList.getString("RESULT").equals("00")){
	        		paramMap.put("recordCnt", String.valueOf(tempPayList.getLong("totalRecord")));
	        		recordCnt = tempPayList.getLong("totalRecord");
	        	}else{ 
	        		paramMap.put("recordCnt", "0");
					recordCnt = 0L;
	        	}
	        }
	        
	        totalPage = (recordCnt % recordsInPage == 0) ? (recordCnt / recordsInPage) : (recordCnt / recordsInPage) + 1;
	        
	        // 04. Parameter Set
	        request.setAttribute("tempPayList", tempPayList);
	        paramMap.put("listSize", String.valueOf(tempPayList.size()));
	        paramMap.put("pageNo",String.valueOf(pageNo));
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
