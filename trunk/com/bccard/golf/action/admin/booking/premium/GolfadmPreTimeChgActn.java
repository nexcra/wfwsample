/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admPreTimeChgActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾���ŷ ƼŸ�� ���⿩�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-21
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmPreTimeChgActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾���ŷ ƼŸ�� ���⿩�� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lessonDelResult = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			// ResultScriptPag.jsp ���� �迭�� ����� Object ���� ��� ���� �߻�.
			paramMap.remove("cidx");

			// Request �� ����
			String[] lsn_seq_no = parser.getParameterValues("cidx", ""); 		// �Ϸù�ȣ
			String view_YN = parser.getParameter("VIEW_YN", ""); 		// ����
			
			String sort 	= parser.getParameter("SORT", "0001"); 
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmPreTimeChgDaoProc proc = (GolfadmPreTimeChgDaoProc)context.getProc("admPreTimeChgDaoProc");
			if (lsn_seq_no != null && lsn_seq_no.length > 0) {
				lessonDelResult = proc.execute(context, dataSet, request, lsn_seq_no, view_YN);
			}			

			request.setAttribute("returnUrl", "admPreTimeList.do?SORT="+sort);	
			// ������ ���
			if (lessonDelResult == lsn_seq_no.length) {
				request.setAttribute("resultMsg", "����ó���� ���������� ó�� �Ǿ����ϴ�.");	
			} else {
				request.setAttribute("resultMsg", "����ó���� ���������� ó�� ���� �ʾҽ��ϴ�.");
			}
	
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}