/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodeFormRegActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �����ڵ���� ��� ��
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.code;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 
import java.util.Map;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmCodeFormRegActn extends GolfActn {

	public static final String TITLE ="�����ڵ���� ��� ��";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext ��ü.
	* @param request		HttpServletRequest ��ü.
	* @param response		HttpServletResponse ��ü.
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		 
		DbTaoConnection con = null;

		try{
			//debug("==== GolfAdmCodeFormRegActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);
			String p_idx		= parser.getParameter("p_idx");
			String s_idx		= parser.getParameter("s_idx");	
			
			if( !"".equals(p_idx)&& !"".equals(s_idx)) {
				//�Խù� ������
				DbTaoDataSet input = new DbTaoDataSet(TITLE);
				input.setString("p_idx", p_idx);
				input.setString("s_idx", s_idx);
	
				//�Խù� ������ execute
				GolfAdmCodeDetailInqDaoProc proc1 = (GolfAdmCodeDetailInqDaoProc)context.getProc("GolfAdmCodeDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
				request.setAttribute("s_idx", s_idx);
			}
			
			
			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeFormRegActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmCodeFormRegActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
	}
}
