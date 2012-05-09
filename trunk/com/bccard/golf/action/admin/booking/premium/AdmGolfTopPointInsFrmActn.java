/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : AdmGolfTopPointInsFrmActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 포인트 관리  > 포인트관리 신규/수정 등록 화면
*   적용범위  : Golf  
*   작성일자  : 2010-12-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;


import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.DateUtil;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;


/** ****************************************************************************
 * 포인트관리 신청/수정 입력 폼 출력 수행 액션.
 * @author 이훈주
 * @version 2004.10.29
 **************************************************************************** */
public class AdmGolfTopPointInsFrmActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 부킹 > 포인트관리  > 포인트관리 신규/수정폼 ";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/

	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
			debug("***********************************************************************************");
			debug(" Action  AdmGolfTopPointInsFrmActn.java 수정 폼 execute");
			debug("***********************************************************************************");
			String subpage_key = "default";	
			String layout = super.getActionParam(context, "layout");
			String actnKey = getActionKey(context);
			
			request.setAttribute("layout", layout);

		try {
			debug("action AdmGolfTopPointInsFrmActn.java try");
			
			RequestParser parser = context.getRequestParser("default",request,response);

			String roundDateFmt	= parser.getParameter("roundDateFmt", "");
			String roundDate	= parser.getParameter("roundDate", "");
			String seqNo		= parser.getParameter("seqNo", "");
			String pointDetlCd	= parser.getParameter("pointDetlCd", "");
			String pointMemo	= parser.getParameter("pointMemo", "");
			String name			= parser.getParameter("name", "");
			String memId		= parser.getParameter("memId", "");
			String key			= parser.getParameter("key", "");

			Map paramMap = parser.getParameterMap();
			paramMap.put("roundDateFmt", roundDateFmt);
			paramMap.put("roundDate", roundDate);
			paramMap.put("seqNo", seqNo);
			paramMap.put("pointDetlCd", pointDetlCd);
			paramMap.put("pointMemo", pointMemo);
			paramMap.put("name", name);
			paramMap.put("memId", memId);
			paramMap.put("key", key);

			request.setAttribute("paramMap",paramMap);

		} catch(BaseException be) {
			throw be;
		} catch(Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler ) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,t.getMessage());
			}
			throw new GolfException(ett,t);
		} finally {
			try {  } catch(Throwable ignore) {}
		}
		return getActionResponse(context); // response key
	}
}
