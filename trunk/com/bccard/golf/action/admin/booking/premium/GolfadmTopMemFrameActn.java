/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopMemFrameActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > �г�Ƽ����  > �г�Ƽ����  ���/���� -> �г�Ƽ���ȸ������
*   �������  : Golf
*   �ۼ�����  : 2010-12-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * ��ŷ������� �߰� �Է� �� ��� ���� �׼�.
 * @author  ����
 * @version 2010.12.08
 **************************************************************************** */
public class GolfadmTopMemFrameActn extends AbstractAction {

    public static final String TITLE ="��ŷ���ȸ�� ����";
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
