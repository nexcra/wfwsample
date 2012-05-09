/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthDetailInqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ��� ���� > �󼼺��� 
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;

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
import com.bccard.golf.dbtao.proc.admin.*;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthDetailInqActn extends GolfActn {

	public static final String TITLE ="�󼼺���";

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
			//debug("==== GolfAdmAuthDetailInqActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);

			//int page_no			= parser.getIntParameter("page_no", 1);				// ��������ȣ
			//String search_clss	= parser.getParameter("search_clss", "");
			//String search_word	= parser.getParameter("search_word","");
			//String sdate		= parser.getParameter("sdate","");
			//String edate		= parser.getParameter("edate","");
			//String search_yn	= parser.getParameter("search_yn","");
			//String uid			= parser.getParameter("uid","");
			String p_idx		= parser.getParameter("p_idx");
			//debug("==== GolfAdmAuthDetailInqActn 1 ===");
			
			if(p_idx!=null) {
				//�Խù� ������
				DbTaoDataSet input = new DbTaoDataSet(TITLE);
				input.setString("p_idx", p_idx);
	
				//�Խù� ������ execute
				GolfAdmAuthDetailInqDaoProc proc1 = (GolfAdmAuthDetailInqDaoProc)context.getProc("GolfAdmAuthDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
			}
			Map paramMap = parser.getParameterMap();	
			
			request.setAttribute("paramMap", paramMap);
			//debug("==== GolfAdmAuthDetailInqActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmAuthDetailInqActn Error ===");
			
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
	}
}