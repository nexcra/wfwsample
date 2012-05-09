/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : InqZipcodeActn.java
*   작성자    : E4NET 은장선
*   내용      : 모집인 가입처리
*   적용범위  : Golf
*   작성일자  : 2009-09-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class InqZipcodeActn extends GolfActn {
	
	public static final String TITLE = "모집인 가입처리"; 
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {		
		
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
					

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			String search_Keyword = parser.getParameter("dong","");	
			debug("Search_Keyword>>>>>>>>>>>>>>>>>" + search_Keyword);
			//GolfAdmMojibProc proc = (GolfAdmMojibProc)context.getProc("GolfAdmMojibProc");
			GolfAdmMojibProc proc = new GolfAdmMojibProc();
			
			if ((search_Keyword.trim()).length() != 0 ) {				
			
				dataSet.setString("Search_Keyword", search_Keyword);						//동명							

				DbTaoResult taoResult = (DbTaoResult)proc.getList(context, dataSet);

				request.setAttribute("InqZipcodeproc", taoResult);
			}
			
			request.setAttribute("paramMap", parser.getParameterMap());	

		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
