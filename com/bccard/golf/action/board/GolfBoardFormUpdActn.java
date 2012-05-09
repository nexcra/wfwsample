/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardFormUpdActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 게시판 폼
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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardDetailInqDaoProc;
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
public class GolfBoardFormUpdActn extends GolfActn {

	
	public static final String TITLE = "게시판 폼";
	
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

		//debug("==== GolfBoardFormUpdActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			
			//1. 파라메타 값 
			String p_idx				= parser.getParameter("p_idx");								// 글 일련번호
			String boardid				= parser.getParameter("boardid");								// 게시판 번호
			String mode					= parser.getParameter("mode");								// 구분 ( view )
			String type					= parser.getParameter("type");
			
			String search_yn			= parser.getParameter("search_yn", "N");					// 검색여부
			String search_clss			= "";
			String search_word			= "";
			String sdate 				= "";
			String edate 				= "";
			
			if("Y".equals(search_yn))
			{
				search_clss				= parser.getParameter("search_clss");					// 검색구분
				search_word				= parser.getParameter("search_word");					// 검색어
				sdate 					= parser.getParameter("sdate");							// 검색시작날짜
				edate 					= parser.getParameter("edate");
			}
	
			long page_no				= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size				= parser.getLongParameter("page_size", 10L);			// 페이지당출력수		
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			
			if( !"".equals(p_idx) && !"".equals(boardid) ) {
				
				//게시물 상세정보
				input.setString("p_idx", p_idx);
				input.setString("boardid", boardid);
				input.setString("mode", mode);
	
				//게시물 상세정보 execute
				GolfBoardDetailInqDaoProc proc1 = (GolfBoardDetailInqDaoProc)context.getProc("GolfBoardDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
				request.setAttribute("boardid", boardid);
			}

			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfBoardFormUpdActn end ===");
			
		}catch(Throwable t) {
			//debug("==== GolfBoardFormUpdActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);			
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}	
	
}
