/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLsnRecvMutiDelActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ������û ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnRecvMutiDelDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnRecvMutiDelActn extends GolfActn{
	
	public static final String TITLE = "������ ������û ���� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lsnRecvDelResult = 0;
		
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
			String[] recv_no = parser.getParameterValues("cidx", ""); 		// ������û �Ϸù�ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnRecvMutiDelDaoProc proc = (GolfAdmLsnRecvMutiDelDaoProc)context.getProc("GolfAdmLsnRecvMutiDelDaoProc");
			if (recv_no != null && recv_no.length > 0) {
				lsnRecvDelResult = proc.execute(context, dataSet, recv_no);
			}			

			request.setAttribute("returnUrl", "admLsnRecvList.do");	
			// ������ ���
			if (lsnRecvDelResult == recv_no.length) {
				request.setAttribute("resultMsg", "");	
			} else {
				request.setAttribute("resultMsg", "������û ������ ���������� ó�� ���� �ʾҽ��ϴ�.");
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
