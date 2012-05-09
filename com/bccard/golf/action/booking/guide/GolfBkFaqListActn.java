/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkFaqListActn
*   작성자     : (주)미디어포스 임은혜
*   내용        : 부킹 > 부킹 가이드 > FAQ 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.guide;

import java.io.IOException;
import java.sql.ResultSet;
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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComSelectListDaoProc;
import com.bccard.golf.dbtao.proc.booking.guide.*;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfBkFaqListActn extends GolfActn {
	
	public static final String TITLE = "FAQ 리스트";
	
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

		//debug("==== GolfAdmBoardComListActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			//1. 파라메타 값 
			long page_no		= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size		= parser.getLongParameter("page_size", 10L);			// 페이지당출력수
			
			String sch_TEXT		= parser.getParameter("SCH_TEXT", "");
			String sch_FAQ_CLSS	= parser.getParameter("SCH_FAQ_CLSS", "");
			String sch_ORDER	= parser.getParameter("SCH_ORDER", "");
			String sch_SORT		= parser.getParameter("SCH_SORT", "");
			String sch_TEXT2	= parser.getParameter("SCH_TEXT2", "");
			
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			
			input.setString("SCH_TEXT",			sch_TEXT);
			input.setString("SCH_FAQ_CLSS",		sch_FAQ_CLSS);
			input.setString("SCH_ORDER",		sch_ORDER);
			input.setString("SCH_SORT",			sch_SORT);
			input.setString("SCH_TEXT2",		sch_TEXT2);
			
			// 코드값 정의
			input.setString("boardCode", 		"0018");
			GolfAdmBoardComSelectListDaoProc proc_sel = (GolfAdmBoardComSelectListDaoProc)context.getProc("GolfAdmBoardComSelectListDaoProc");
			DbTaoResult boardSelectListInq = (DbTaoResult)proc_sel.execute(context, request, input);
			request.setAttribute("boardSelectListInq", boardSelectListInq);

			GolfBkFaqListDaoProc proc = (GolfBkFaqListDaoProc)context.getProc("GolfBkFaqListDaoProc");
			DbTaoResult boardListInq = (DbTaoResult)proc.execute(context, request, input);
			request.setAttribute("boardListInq", boardListInq);
			
			boardListInq.next();
			String result = boardListInq.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", boardListInq.getString("total_cnt"));
			else
				request.setAttribute("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(boardListInq.size()));
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComListActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
