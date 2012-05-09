/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : LoginYnActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 로그인 유무
*   적용범위  : Golf
*   작성일자  : 2009-06-11
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AjaxActn;
import com.bccard.golf.common.ResponseData;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
import com.bccard.golf.common.GolfUserEtt; 
import javax.servlet.http.HttpSession;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class LoginYnActn  extends AjaxActn {

	public static final String TITLE = "로그인 유무 XML"; 

	/***********************************************************************
	 * 액션처리.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @param responseData  ResponseData 
	 * @return 응답정보
	 **********************************************************************/
	public ActionResponse ajaxExecute(WaContext context, HttpServletRequest request, HttpServletResponse response, ResponseData responseData) throws ServletException, IOException, BaseException {


		try {
			 
			//UcusrinfoEntity ucusrinfo = SessionUtil.getFrontUserInfo(request);
			HttpSession session = request.getSession(false); 
			GolfUserEtt ucusrinfo   = (GolfUserEtt)session.getAttribute("GOLF_ENTITY"); //- 기본정보
			if (ucusrinfo != null) {
				
				//String userId		= (String)ucusrinfo.getAccount();
				String userId		= (String)ucusrinfo.getMemId(); 
				
				if(userId != null && !"".equals(userId))
				{
				
					responseData.put("loginYN"			, "Y"			);
				}
				else
				{
					
					responseData.put("loginYN"			, "N"			);
				}

			} else {
				responseData.put("loginYN"			, "N"			);

			}
					
			
			
			
		} catch (Throwable t) {
			ajaxException(context, request, t);
		} finally {
		}

		return super.getActionResponse(context);
	}
}
