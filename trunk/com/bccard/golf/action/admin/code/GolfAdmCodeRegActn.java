/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCodeRegActn
*   작성자     : (주)미디어포스 조은미	
*   내용        : 관리자 게시판 관리 등록 처리
*   적용범위  : Golf 
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.code;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmCodeRegActn extends GolfActn  {
	
	public static final String TITLE = "관리자 게시판 관리 등록 처리";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

		//debug("==== GolfAdmCodeRegActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. 파라메타 값 
			String search_yn	= parser.getParameter("search_yn", "N");					// 검색여부
			String mode			= parser.getParameter("mode", "ins");						// 처리구분
			String search_clss	= "";
			String search_word	= "";
			if("Y".equals(search_yn)){
				search_clss		= parser.getParameter("search_clss");						// 검색구분
				search_word		= parser.getParameter("search_word");						// 검색어
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size		= parser.getLongParameter("page_size", 10L);			// 페이지당출력수		
			
			String cd_clss		= parser.getParameter("CD_CLSS", "");	
			String cd	= parser.getParameter("CD", "");
			String cd_nm	= parser.getParameter("CD_NM", "");	
			String cd_desc	= parser.getParameter("CD_DESC", "");	
			String use_yn		= parser.getParameter("USE_YN", "N");	
			String p_idx		= parser.getParameter("p_idx", "");
			String s_idx		= parser.getParameter("s_idx", "");	
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			input.setString("mode",		mode);
			input.setString("CD_CLSS",		cd_clss);
			input.setString("CD",			cd);
			input.setString("CD_NM",		cd_nm);
			input.setString("CD_DESC",		cd_desc);
			input.setString("USE_YN",		use_yn);
			input.setString("p_idx",		p_idx);
			input.setString("s_idx",		s_idx);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			
			//debug("actc mode:"+mode);
			
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB 처리 
			GolfAdmCodeRegDaoProc proc = (GolfAdmCodeRegDaoProc)context.getProc("GolfAdmCodeRegDaoProc");
			DbTaoResult codeInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("codeInq", codeInq);						
			
			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeRegActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
