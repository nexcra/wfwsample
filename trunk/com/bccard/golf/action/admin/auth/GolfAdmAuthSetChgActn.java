/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthSetChgActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 권한 설정
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;
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
import com.bccard.golf.dbtao.proc.admin.*;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthSetChgActn extends GolfActn {
	
	public static final String TITLE = "비씨골프  관리자  설정 셋팅";
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

		//debug("==== GolfAdmAuthSetChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			// 1. 파라메타 값 
			String p_idx	= parser.getParameter("p_idx", "");				// 관리자PK			
				
			// 2.쿼리 조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("p_idx",	p_idx);
			
			// 3.링크 스크립트 
			GolfAdmAuthSetChgProc proc = (GolfAdmAuthSetChgProc)context.getProc("GolfAdmAuthSetChgProc");
			DbTaoResult authListInq = (DbTaoResult)proc.execute(context, request, input);

			// 4.메뉴이름
			GolfAdmAuthMenulnqProc proc2 = (GolfAdmAuthMenulnqProc)context.getProc("GolfAdmAuthMenulnqProc");
			DbTaoResult authMenuListInq = (DbTaoResult)proc2.execute(context, request, input);
						
			Map paramMap = parser.getParameterMap();	
			request.setAttribute("authListInq", authListInq);
			request.setAttribute("authMenuListInq", authMenuListInq);
			request.setAttribute("p_idx", p_idx);			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmAuthSetChgActn End ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}

}
