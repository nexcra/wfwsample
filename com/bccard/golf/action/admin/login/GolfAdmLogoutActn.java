/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointAdmLogoutActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ���� ������ �α��� ���μ���
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.action.admin.login;
 
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.proc.login.ChkDupAccountDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmLogoutActn extends AbstractAction {
	
	public static final String TITLE="�α׾ƿ�"; 
	
	/***************************************************************************************
	* �񾾰��������ڷα׾ƿ� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(  WaContext context,
				HttpServletRequest request,
				HttpServletResponse response)
				throws IOException, ServletException, BaseException {
		
		//debug("==== GolfPointAdmLogoutActn start ===");
		//------------------------------------------------------------------------------------
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		//debug("LOGOUT === > ");
		
		//debug("==== GolfPointAdmLogoutActn end ===");
		return getActionResponse(context, "default");
	
	
	}
}
