/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBusDateRegActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ > �̺�Ʈ->��������������̺�Ʈ->���� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-09-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmBusDateRegActn extends AbstractAction {

	public static final String TITLE = "������ ����� ���� ���� ���� ��� ó��";
	
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
			if(paramMap == null) 	   paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);		
			long page_no						= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size						= parser.getLongParameter("page_size", 10L);			// ����������¼�	
			String p_idx						= parser.getParameter("p_idx");							// ��¥
			String mode							= parser.getParameter("mode", "ins");					// ó������
			
			con = context.getTaoConnection("dbtao",null);
			
			// ������ �α��� ����
			HttpSession session 				= request.getSession(false);
			GolfAdminEtt userEtt 				= (GolfAdminEtt) session.getAttribute("SESSION_ADMIN");
						
			// Proc �Ķ���� ����
			TaoDataSet input 					= new DbTaoDataSet(TITLE);
			input.setObject("userEtt", 			userEtt);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			TITLE);					
			input.setLong("page_no",			page_no);
			input.setString("mode",				mode);
			input.setString("p_idx", 			parser.getParameter("p_idx",""));
			input.setString("reg_date", 		parser.getParameter("reg_date",""));	
			input.setString("greenNm", 			parser.getParameter("greenNm",""));	
			
			// DB ó��			
			result = con.execute("admin.event.golfbus.GolfAdmBusDateInsDaoProc",input);	
															
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token")); 
			paramMap.put("page_no"			, page_no+""		);
			paramMap.put("page_size"		, page_size+""		);
			paramMap.put("p_idx"			, p_idx);	
						
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
