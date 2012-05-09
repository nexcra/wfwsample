/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopMemFrameActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 패널티관리  > 패널티관리  등록/수정 -> 패널티대상회원선택
*   적용범위  : Golf
*   작성일자  : 2010-12-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;


import java.io.IOException;
import com.bccard.waf.core.RequestParser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Map;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

/** ****************************************************************************
 * 부킹대상정보 추가 입력 폼 출력 수행 액션.
 * @author  김상범
 * @version 2010.12.08
 **************************************************************************** */
public class GolfadmTopMemFrameActn extends AbstractAction {

    public static final String TITLE ="부킹대상회원 선택";
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		TaoConnection con = null;
        String curActionKey = getActionKey(context);

		try {
			RequestParser parser = context.getRequestParser("default",request,response);
			
			String name	= parser.getParameter("name", "");
			String account  = parser.getParameter("account", "");
			String memId	= parser.getParameter("memId", "");
			String phone	= parser.getParameter("phone", "");
			String mobile	= parser.getParameter("mobile", "");

			Map paramMap = parser.getParameterMap();
			
			paramMap.put("name", name);
			paramMap.put("account", account);
			paramMap.put("memId", memId);
			paramMap.put("realNm", name);
			paramMap.put("realTel", phone);
			paramMap.put("mobile", mobile);
			paramMap.put("key", "ins");
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
			try { con.close(); } catch(Throwable ignore) {}
		}
		return getActionResponse(context); // response key
	}
}
