/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthDetailInqDaoProc
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 > 운영자 관리 > 상세보기 
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;

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
import com.bccard.golf.dbtao.proc.admin.*;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthDetailInqActn extends GolfActn {

	public static final String TITLE ="상세보기";

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
			//debug("==== GolfAdmAuthDetailInqActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);

			//int page_no			= parser.getIntParameter("page_no", 1);				// 페이지번호
			//String search_clss	= parser.getParameter("search_clss", "");
			//String search_word	= parser.getParameter("search_word","");
			//String sdate		= parser.getParameter("sdate","");
			//String edate		= parser.getParameter("edate","");
			//String search_yn	= parser.getParameter("search_yn","");
			//String uid			= parser.getParameter("uid","");
			String p_idx		= parser.getParameter("p_idx");
			//debug("==== GolfAdmAuthDetailInqActn 1 ===");
			
			if(p_idx!=null) {
				//게시물 상세정보
				DbTaoDataSet input = new DbTaoDataSet(TITLE);
				input.setString("p_idx", p_idx);
	
				//게시물 상세정보 execute
				GolfAdmAuthDetailInqDaoProc proc1 = (GolfAdmAuthDetailInqDaoProc)context.getProc("GolfAdmAuthDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
			}
			Map paramMap = parser.getParameterMap();	
			
			request.setAttribute("paramMap", paramMap);
			//debug("==== GolfAdmAuthDetailInqActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmAuthDetailInqActn Error ===");
			
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
	}
}