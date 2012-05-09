/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : AdmGolfTopPointInsFrmActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > ����Ʈ ����  > ����Ʈ���� �ű�/���� ��� ȭ��
*   �������  : Golf  
*   �ۼ�����  : 2010-12-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * ����Ʈ���� ��û/���� �Է� �� ��� ���� �׼�.
 * @author ������
 * @version 2004.10.29
 **************************************************************************** */
public class AdmGolfTopPointInsFrmActn extends GolfActn{
	
	public static final String TITLE = "������ > ��ŷ > ����Ʈ����  > ����Ʈ���� �ű�/������ ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/

	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
			debug("***********************************************************************************");
			debug(" Action  AdmGolfTopPointInsFrmActn.java ���� �� execute");
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
