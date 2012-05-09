/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCodeFormRegActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 공통코드관리 등록 폼
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.code;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 
import java.util.Map;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.*;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmCodeFormRegActn extends GolfActn {

	public static final String TITLE ="공통코드관리 등록 폼";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext 객체.
	* @param request		HttpServletRequest 객체.
	* @param response		HttpServletResponse 객체.
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		 
		DbTaoConnection con = null;

		try{
			//debug("==== GolfAdmCodeFormRegActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);
			String p_idx		= parser.getParameter("p_idx");
			String s_idx		= parser.getParameter("s_idx");	
			
			if( !"".equals(p_idx)&& !"".equals(s_idx)) {
				//게시물 상세정보
				DbTaoDataSet input = new DbTaoDataSet(TITLE);
				input.setString("p_idx", p_idx);
				input.setString("s_idx", s_idx);
	
				//게시물 상세정보 execute
				GolfAdmCodeDetailInqDaoProc proc1 = (GolfAdmCodeDetailInqDaoProc)context.getProc("GolfAdmCodeDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
				request.setAttribute("s_idx", s_idx);
			}
			
			
			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeFormRegActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmCodeFormRegActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
	}
}
