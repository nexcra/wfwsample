/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardInqActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 게시판 목록 조회
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.board;

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
import com.bccard.golf.dbtao.proc.board.*;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfBoardInqActn extends GolfActn {

	
	public static final String TITLE = "게시판 목록 조회";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		
		//debug("==== GolfBoardInqActn Start : " + TITLE +  "  >>===");
		
		try {
			
			RequestParser	parser	= context.getRequestParser("default", request, response);
			
			//1. 파라메타 값 
			String search_yn			= parser.getParameter("search_yn", "N");				// 검색여부
			String search_clss		= "";																	// 검색어구분
			String search_word		= "";																	// 검색어
			String sdate				= "";																	// 검색시작날짜
			String edate				= "";																	// 검색종료날짜			
			if("Y".equals(search_yn))
			{
				search_clss			= parser.getParameter("search_clss");						// 검색구분
				search_word			= parser.getParameter("search_word");						// 검색어
				sdate 					= parser.getParameter("sdate");								// 검색시작날짜
				edate 					= parser.getParameter("edate");							
			}
			
			long page_no			= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size			= parser.getLongParameter("page_size", 10L);			// 페이지당출력수			

			String boardid				= parser.getParameter("boardid", "");								//게시판번호
			

			if(!"".equals(boardid))
			{
				//2.조회
				DbTaoDataSet input = new DbTaoDataSet(TITLE);
				input.setString("search_yn",	search_yn);
				if("Y".equals(search_yn))
				{
					input.setString("search_clss",	search_clss);
					input.setString("search_word",	search_word);
					input.setString("sdate",	sdate);
					input.setString("edate",	edate);
				}
				input.setLong("page_no",		page_no);
				input.setLong("page_size",	page_size);
				input.setString("boardid",		boardid);
				 
				//게시판환경
				GolfBoardConfiglnqDaoProc proc_config = (GolfBoardConfiglnqDaoProc)context.getProc("GolfBoardConfiglnqDaoProc");
				DbTaoResult boardConfigInq = (DbTaoResult)proc_config.execute(context, request, input);
				request.setAttribute("boardConfigInq", boardConfigInq);
				
				//게시판목록
				GolfBoardlnqDaoProc proc = (GolfBoardlnqDaoProc)context.getProc("GolfBoardlnqDaoProc");
				DbTaoResult boardListInq = (DbTaoResult)proc.execute(context, request, input);
				request.setAttribute("boardListInq", boardListInq);
				
			}
						

			Map paramMap = parser.getParameterMap();				
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("boardid", boardid);
			
			//debug("==== GolfBoardInqActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}	
	
}
