/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : InqZipcodeActn.java
*   �ۼ���    : E4NET ���弱
*   ����      : ������ ����ó��
*   �������  : Golf
*   �ۼ�����  : 2009-09-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class InqZipcodeActn extends GolfActn {
	
	public static final String TITLE = "������ ����ó��"; 
	/***************************************************************************************
	* �񾾰��� �����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {		
		
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
					

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			String search_Keyword = parser.getParameter("dong","");	
			debug("Search_Keyword>>>>>>>>>>>>>>>>>" + search_Keyword);
			//GolfAdmMojibProc proc = (GolfAdmMojibProc)context.getProc("GolfAdmMojibProc");
			GolfAdmMojibProc proc = new GolfAdmMojibProc();
			
			if ((search_Keyword.trim()).length() != 0 ) {				
			
				dataSet.setString("Search_Keyword", search_Keyword);						//����							

				DbTaoResult taoResult = (DbTaoResult)proc.getList(context, dataSet);

				request.setAttribute("InqZipcodeproc", taoResult);
			}
			
			request.setAttribute("paramMap", parser.getParameterMap());	

		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
