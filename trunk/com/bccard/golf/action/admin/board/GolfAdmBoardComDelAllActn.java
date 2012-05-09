/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComDelActn
*   작성자     : (주)미디어포스 임은혜	
*   내용        : 관리자 게시판 삭제 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComDelAllDaoProc;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfAdmBoardComDelAllActn extends GolfActn  {
	
	public static final String TITLE = "관리자 게시판 삭제 처리";
	
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

		//debug("==== GolfAdmBoardComDelActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			// ResultScriptPag.jsp 에서 배열로 저장된 Object 있을 경우 에러 발생.
			paramMap.remove("cidx");
			
			// Request 값 저장
			String[] lsn_seq_no = parser.getParameterValues("cidx", ""); 		// 레슨 일련번호
			
			//1. 파라메타 값 
			String search_yn		= parser.getParameter("search_yn", "N");					// 검색여부
			String search_clss	= "";
			String search_word	= "";
			String sdate 			= "";
			String edate 			= "";
			if("Y".equals(search_yn)){
				search_clss			= parser.getParameter("search_clss");						// 검색구분
				search_word			= parser.getParameter("search_word");						// 검색어
			}
			long page_no			= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size			= parser.getLongParameter("page_size", 10L);			// 페이지당출력수		
				
			String idx				= parser.getParameter("idx", "");	
			String bbrd_clss		= parser.getParameter("boardid", "");
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);	
			
			input.setString("idx",				idx);	
			

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 3. DB 처리  
			int delResult = 0;
			GolfAdmBoardComDelAllDaoProc proc = (GolfAdmBoardComDelAllDaoProc)context.getProc("GolfAdmBoardComDelAllDaoProc");
			if (lsn_seq_no != null && lsn_seq_no.length > 0) {
				delResult = proc.execute(context, dataSet, lsn_seq_no);
			}			
			paramMap.put("BBRD_CLSS", bbrd_clss);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComDelActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
