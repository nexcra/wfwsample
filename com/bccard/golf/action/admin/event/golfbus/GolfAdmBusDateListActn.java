/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBusDateListActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 이벤트->골프장버스운행이벤트->일정관리 리스트
*   적용범위  : Golf
*   작성일자  : 2009-09-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.golfbus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmBusDateListActn extends AbstractAction {

	public static final String TITLE = "관리자 골드장 버스 운행 일정관리 리스트";
	
	/**
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request,
		HttpServletResponse response) throws IOException, ServletException,
			BaseException
	{
		TaoConnection 		con 				= null;
		TaoResult 			result  			= null;		
		Map 				paramMap 			= null;
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);		
			long page_no						= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size						= parser.getLongParameter("page_size", 20L);			// 페이지당출력수			
			
			con = context.getTaoConnection("dbtao",null);
			
			// 관리자 로그인 정보
			HttpSession session 				= request.getSession(false);
			GolfAdminEtt userEtt 				= (GolfAdminEtt) session.getAttribute("SESSION_ADMIN");
						
			// Proc 파라메터 설정
			TaoDataSet input 					= new DbTaoDataSet(TITLE);
			input.setObject("userEtt", 			userEtt);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			TITLE);					
			input.setLong("page_no",			page_no);
			input.setLong("page_size",			page_size);	
			// 일정 리스트 조회			
			result = con.execute("admin.event.golfbus.GolfAdmBusDateInqDaoProc",input);
			
						
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));  
						
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);

			
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return getActionResponse(context, "default");
	}
				

}
