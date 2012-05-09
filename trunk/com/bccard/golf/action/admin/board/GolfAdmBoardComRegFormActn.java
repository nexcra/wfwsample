/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComListActn
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 게시판 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

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
import com.bccard.golf.dbtao.proc.admin.board.*;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfAdmBoardComRegFormActn extends GolfActn {
	
	public static final String TITLE = "관리자 게시판 셀렉트 리스트";
	
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
			Map paramMap = parser.getParameterMap();	
			//1. 파라메타 값 
			
			String boardid = parser.getParameter("boardid", "");
			String boardCode = "";
			
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);

			if(boardid.equals("0020")){
				
				// 코드값 정의
				boardCode = "0018";
				input.setString("boardCode", 		boardCode);
				
				GolfAdmBoardComSelectListDaoProc proc = (GolfAdmBoardComSelectListDaoProc)context.getProc("GolfAdmBoardComSelectListDaoProc");
				DbTaoResult boardSelectListInq = (DbTaoResult)proc.execute(context, request, input);
				request.setAttribute("boardSelectListInq", boardSelectListInq);
			}

			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComListActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
