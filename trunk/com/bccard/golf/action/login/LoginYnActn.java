/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : LoginYnActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : �α��� ����
*   �������  : Golf
*   �ۼ�����  : 2009-06-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class LoginYnActn  extends AjaxActn {

	public static final String TITLE = "�α��� ���� XML"; 

	/***********************************************************************
	 * �׼�ó��.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @param responseData  ResponseData 
	 * @return ��������
	 **********************************************************************/
	public ActionResponse ajaxExecute(WaContext context, HttpServletRequest request, HttpServletResponse response, ResponseData responseData) throws ServletException, IOException, BaseException {


		try {
			 
			//UcusrinfoEntity ucusrinfo = SessionUtil.getFrontUserInfo(request);
			HttpSession session = request.getSession(false); 
			GolfUserEtt ucusrinfo   = (GolfUserEtt)session.getAttribute("GOLF_ENTITY"); //- �⺻����
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
