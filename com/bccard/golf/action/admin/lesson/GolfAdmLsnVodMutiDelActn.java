/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLsnVodMutiDelActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ���������� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-22
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
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnVodMutiDelDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnVodMutiDelActn extends GolfActn{
	
	public static final String TITLE = "������ ���������� ���� ���� ó��";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lsnVodDelResult = 0;
		
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
			paramMap.remove("pre_yn");

			// Request �� ����
			String[] seq_no = parser.getParameterValues("cidx", ""); 		// ���� �Ϸù�ȣ
			String pre_yn = parser.getParameter("pre_yn", "N"); 			// �����̾������� ���� (Y:�����̾�������, N:�Ϲݵ�����) 
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnVodMutiDelDaoProc proc = (GolfAdmLsnVodMutiDelDaoProc)context.getProc("GolfAdmLsnVodMutiDelDaoProc");
			if (seq_no != null && seq_no.length > 0) {
				lsnVodDelResult = proc.execute(context, dataSet, seq_no);
			}
			
			// return 
			String return_url = "admLsnVodList.do";
			String return_title = "������";
			
			if(pre_yn.equals("Y")){
				return_url = "admLsnPreVodList.do";
				return_title = "�����̾� ������";
			}
			
			request.setAttribute("returnUrl", return_url);	
			
			// ������ ���
			if (lsnVodDelResult == seq_no.length) {
				request.setAttribute("resultMsg", "���� " + return_title + " ������ ���������� ó���Ǿ����ϴ�.");	
			} else {
				request.setAttribute("resultMsg", "���� " + return_title + " ������ ���������� ó�� ���� �ʾҽ��ϴ�.");
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
