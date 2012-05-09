/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopBkApplyStatisActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 > 부킹 > TOP골프카드전용부킹 > TOP부킹신청대비결과
*   적용범위  : golf
*   작성일자  : 2010-12-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopBkStatisDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfadmTopBkApplyStatisActn extends GolfActn{
 

	public static final String TITLE = "TOP부킹신청대비결과";
	
	/***************************************************************************************  
	 * @param context  WaContext 객체. 
	 * @param request  HttpServletRequest 객체. 
	 * @param response  HttpServletResponse 객체. 
	 * @return ActionResponse Action 처리후 화면에 디스플레이할 정보. 
	 ***************************************************************************************/	 
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
	
		String subpage_key = "default"; 
		  
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		  
		try {
			   
			// 02.입력값 조회  
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String mode = parser.getParameter("mode", "INIT");
			String diff = parser.getParameter("diff", "0");
			String yyyy = parser.getParameter("yyyy");
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");			
			String repMbNo = parser.getParameter("repMbNo", "00");			
			String repMbNoNm = parser.getParameter("repMbNoNm");
						
			String bkngStat   = parser.getParameter("parm1");
			String memberClss = parser.getParameter("parm2");
			String gubun = parser.getParameter("parm3");
			   
			paramMap.put("diff", diff);
			paramMap.put("yyyy", yyyy);
			paramMap.put("from", from);
			paramMap.put("to", to);			
			paramMap.put("repMbNo", repMbNo);			
			paramMap.put("repMbNoNm", repMbNoNm);			
			paramMap.put("bkngStat", bkngStat);
			paramMap.put("memberClss", memberClss);
			paramMap.put("gubun", gubun);			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);   
						
			dataSet.setString("bkngStat", bkngStat);
			dataSet.setString("memberClss", memberClss);
			dataSet.setString("gubun", gubun);
			dataSet.setString("diff", diff);
			dataSet.setString("yyyy", yyyy);
			dataSet.setString("from", from);
			dataSet.setString("to", to);
			dataSet.setString("repMbNo", repMbNo);
						
			GolfadmTopBkStatisDaoProc instance = GolfadmTopBkStatisDaoProc.getInstance();
			   
			DbTaoResult listResult = null;
			   
			if (!"INIT".equals(mode)) {
			    
				// 04.실제 테이블(Proc) 조회 - 리스트
				if (mode.equals("EXCELDETAIL")){	
					listResult = instance.excelDetail(context, request, dataSet);
				}else {					
				    listResult = instance.execute(context, request, dataSet);
				}
				
				request.setAttribute("BkngApplyStatis", listResult);
			    
			}
			
			request.setAttribute("paramMap", paramMap);
			
			request.setAttribute("bkngStat", bkngStat);	
			request.setAttribute("repMbNoNm", repMbNoNm);
			
			if (mode.equals("EXCELDETAIL")) {
				
				if(gubun.equals("1")) gubun = "신청";
				else if(gubun.equals("2")) gubun = "성공";
				else if(gubun.equals("3")) gubun = "실패";
				else if(gubun.equals("4")) gubun = "취소";
				
				if (memberClss.equals("1"))	request.setAttribute("gubun", "개인회원_"+gubun);
				else if (memberClss.equals("5")) request.setAttribute("gubun", "법인회원_"+gubun);
				else request.setAttribute("gubun", "개인+법인회원_"+gubun);
				
			}			
			   
			if (mode.equals("EXCEL")) { subpage_key = "excel"; }
			if (mode.equals("EXCELDETAIL")) { subpage_key = "excelDetail"; }
			if (mode.equals("PRINT")) { subpage_key = "print"; } 
		         
		} catch(Throwable t) {
			debug(TITLE, t);
			t.printStackTrace(); 
		    throw new GolfException(TITLE, t);
		} 
		  
		return super.getActionResponse(context, subpage_key);

	
	} 
  

}