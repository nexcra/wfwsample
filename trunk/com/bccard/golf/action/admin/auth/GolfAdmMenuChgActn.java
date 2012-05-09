/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMenuChgActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 메뉴 수정 처리
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
public class GolfAdmMenuChgActn extends GolfActn  {
	
	public static final String TITLE = "비씨골프  관리자  메뉴 수정 처리";
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

		//debug("==== GolfAdmMenuChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. 파라메타 값 
			String mode		= parser.getParameter("mode", "");
			String idx		= parser.getParameter("idx", "");	
			String gdx		= parser.getParameter("gdx", "");
			String str_name	= parser.getParameter("str_name", "");
			String str_url	= parser.getParameter("str_url", "");
			
			
			String str_table_name = "";
			String str_idx_col_name = "";
			String str_col_name = "";
			
			
			if("m0".equals(mode))
			{
				str_table_name 		= "TBGSQ1MENUINFO";
				str_idx_col_name 	= "SQ1_LEV_SEQ_NO";
				str_col_name 		= "SQ1_LEV_MENU_NM";
			}else if("m1".equals(mode))
			{
				str_table_name 		= "TBGSQ2MENUINFO";
				str_idx_col_name 	= "SQ2_LEV_SEQ_NO";
				str_col_name 		= "SQ2_LEV_MENU_NM";
			}else if("m2".equals(mode))
			{
				str_table_name	 	= "TBGSQ3MENUINFO";
				str_idx_col_name 	= "SQ3_LEV_SEQ_NO";
				str_col_name 		= "SQ3_LEV_MENU_NM";
			}
			
			//2.PROC로 넘기는 값 정의
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("str_table_name",	str_table_name);
			input.setString("str_idx_col_name",	str_idx_col_name);
			input.setString("str_col_name",	str_col_name);
			input.setString("mode",	mode);
			input.setString("idx",	idx);
			input.setString("str_name",	str_name);
			input.setString("str_url",	str_url);
			
			GolfAdmMenuChgDaoProc proc = (GolfAdmMenuChgDaoProc)context.getProc("GolfAdmMenuChgDaoProc");
			DbTaoResult menuListInq = (DbTaoResult)proc.execute(context, request, input);

			Map paramMap = parser.getParameterMap();	
			request.setAttribute("menuListInq", menuListInq);
			request.setAttribute("gdx", gdx);
			request.setAttribute("idx", idx);
			request.setAttribute("mode", mode);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmMenuChgActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}

}
