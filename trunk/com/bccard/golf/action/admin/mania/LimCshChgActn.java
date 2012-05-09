/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : LimCshChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania; 
 
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.mania.LimCshUpdDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class LimCshChgActn extends GolfActn{
	
	public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String no		= parser.getParameter("p_idx", ""); 
			String name		= parser.getParameter("name", ""); 
			String price 	= parser.getParameter("price", "");	
			String price2 	= parser.getParameter("price2", "");
			String price3 	= parser.getParameter("price3", "");
			String code 	= parser.getParameter("code", "");
			
			debug("scccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccscccccccccccccccccccccccccccccccccccsccccccccccccccccccccccccccccccccccccccccct :: > " + name);

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("NO", no);
			dataSet.setString("NAME", name);
			dataSet.setString("PRICE", price);
			dataSet.setString("PRICE2", price2);
			dataSet.setString("PRICE3", price3);
			dataSet.setString("CODE", code);

			// 04.���� ���̺�(Proc) ��ȸ
			LimCshUpdDaoProc proc = (LimCshUpdDaoProc)context.getProc("LimCshUpdDaoProc");
			
			// ���������� ��û ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				
	        request.setAttribute("returnUrl", "admLimCshChgForm.do");
			request.setAttribute("resultMsg", "�ݾװ��� ������ ���������� ó�� �Ǿ����ϴ�."); 	

	        } else {
				request.setAttribute("returnUrl", "admLimCshChgForm.do");
				request.setAttribute("resultMsg", "�ݾװ��� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		return super.getActionResponse(context, subpage_key);
	}
}
