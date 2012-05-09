/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfTargetUpdActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 부킹대상관리
*   적용범위  : Golf
*   작성일자  : 2010-11-02
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmPreTimeListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfadmTopGolfTargetUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹대상관리 대상자 상세보기";

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
		
		int intResultCnt = 0;
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String seq				= parser.getParameter("seq","");           //idx
			String sort				= parser.getParameter("sort","1000"); 
			String type				= parser.getParameter("type","2"); 
			
			String modeType			= parser.getParameter("modeType",""); 
			String pgrs_yn			= parser.getParameter("pgrs_yn",""); 
			
			String[] arr_seq_no = parser.getParameterValues("cidx", ""); 		// 일련번호
			
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("seq",seq);
			dataSet.setString("sort",sort);
			dataSet.setString("type",type);
			dataSet.setString("pgrs_yn",pgrs_yn);
			dataSet.setString("modeType",modeType);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			DbTaoResult viewResult = null;
			
			if("failUpd".equals(modeType))	//부킹실패처리
			{
				if (arr_seq_no != null && arr_seq_no.length > 0) 
				{
					
					
					
					intResultCnt = proc.failProc(context, request, dataSet, arr_seq_no);
					
					if (intResultCnt == arr_seq_no.length) {
						request.setAttribute("resultCode", "11");	
						request.setAttribute("resultMsg", "부킹실패가 정상적으로 처리 되었습니다.");	
						request.setAttribute("resultGoUrl", "admTopGolfTargetPpList.do");	
						
						// 부킹실패 SMS문자 처리
						viewResult = (DbTaoResult) proc.getFailList(context, request, dataSet, arr_seq_no);
						
						
						
					} else {
						request.setAttribute("resultCode", "22");
						request.setAttribute("resultMsg", "부킹실패가 처리 되지 않았습니다. \\n선택한 항목중에서 불가능한 건이 존재합니다.");
						request.setAttribute("resultGoUrl", "");	
					}
					
					
					
				}
				else
				{
					request.setAttribute("resultCode", "22");
					request.setAttribute("resultMsg", "부킹실패가 처리 되지 않았습니다. \\n선택한 항목이 없습니다.");
					request.setAttribute("resultGoUrl", "");	
				}
			}
			else if("failUpdOk".equals(modeType))	//부킹 확정처리
			{
				dataSet.setString("idx",parser.getParameter("idx",""));
				
				if (arr_seq_no != null && arr_seq_no.length > 0) 
				{
					
					
					
					intResultCnt = proc.failProc(context, request, dataSet, arr_seq_no);
					
					if (intResultCnt == arr_seq_no.length) {
						request.setAttribute("resultCode", "11");	
						request.setAttribute("resultMsg", "부킹확정이 정상적으로 처리 되었습니다.");	
						request.setAttribute("resultGoUrl", "admTopGolfTargetPpConfList.do");	
						
						
						proc.confStatOkProc(context, request, dataSet);

						
						
						
						// 부킹확정 이메일, SMS문자 처리
						viewResult = (DbTaoResult) proc.getConfOkList(context, request, dataSet, arr_seq_no);
						
						
						 
						
						
					} else {
						request.setAttribute("resultCode", "22");
						request.setAttribute("resultMsg", "부킹확정이 처리 되지 않았습니다. \\n선택한 항목중에서 불가능한 건이 존재합니다.");
						request.setAttribute("resultGoUrl", "admTopGolfTargetPpConfList.do");		
					}
					
					
					
				}
				else
				{
					request.setAttribute("resultCode", "22");
					request.setAttribute("resultMsg", "부킹실패가 처리 되지 않았습니다. \\n선택한 항목이 없습니다.");
					request.setAttribute("resultGoUrl", "admTopGolfTargetPpConfList.do");		
				}
			}
						
			else if("joinEnd".equals(modeType))	// 신청마감처리
			{
				if (arr_seq_no != null && arr_seq_no.length > 0) 
				{
					
					intResultCnt = proc.joinEndProc(context, request, dataSet, arr_seq_no);
					
					if (intResultCnt == arr_seq_no.length) 
					{
						request.setAttribute("resultCode", "11");	
						request.setAttribute("resultMsg", "선택하신 항목들의 신청마감이 정상적으로 처리 되었습니다.");	
						request.setAttribute("resultGoUrl", "admTopGolfTargetList.do");	
					}
					else 
					{
						request.setAttribute("resultCode", "22");
						request.setAttribute("resultMsg", "신청마감처리가 처리 되지 않았습니다. \\n선택한 항목중에서 불가능한 건이 존재합니다.");
						request.setAttribute("resultGoUrl", "");	
					}
					
				}
				else
				{
					request.setAttribute("resultCode", "22");
					request.setAttribute("resultMsg", "신청마감처리가 처리 되지 않았습니다. \\n선택한 항목이 없습니다.");
					request.setAttribute("resultGoUrl", "");	
				}
								
			}
			else if("joinEndCancel".equals(modeType))	// 신청마감해제처리
			{
				if (arr_seq_no != null && arr_seq_no.length > 0) 
				{
					
					intResultCnt = proc.joinEndCancelProc(context, request, dataSet, arr_seq_no);
					
					if (intResultCnt == arr_seq_no.length) 
					{
						request.setAttribute("resultCode", "11");	
						request.setAttribute("resultMsg", "선택하신 항목들의 신청마감해제가 정상적으로 처리 되었습니다.");	
						request.setAttribute("resultGoUrl", "admTopGolfTargetList.do");	
					}
					else 
					{
						request.setAttribute("resultCode", "22");
						request.setAttribute("resultMsg", "신청마감해제가 처리 되지 않았습니다. \\n선택한 항목중에서 불가능한 건이 존재합니다.");
						request.setAttribute("resultGoUrl", "");	
					}
					
				}
				else
				{
					request.setAttribute("resultCode", "22");
					request.setAttribute("resultMsg", "신청마감해제가 처리 되지 않았습니다. \\n선택한 항목이 없습니다.");
					request.setAttribute("resultGoUrl", "");	
				}
								
			}

			
			
			
			request.setAttribute("viewResult", viewResult);	
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
