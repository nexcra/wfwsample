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
package com.bccard.golf.action.admin.booking.par;

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
import com.bccard.golf.dbtao.proc.admin.booking.par.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmParRsUpdActn extends GolfActn{
	
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

			// Request �� ����
			String rsvt_sql_no = parser.getParameter("RSVT_SQL_NO", "");
			String rsvt_yn = parser.getParameter("RSVT_YN", "");
			String ctnt = parser.getParameter("CTNT", "");
			String cdhd_id = parser.getParameter("CDHD_ID", "");
			String appr_opion = parser.getParameter("APPR_OPION","");
			String add_appr_opion = parser.getParameter("ADD_APPR_OPION","");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_sql_no);
			dataSet.setString("RSVT_YN", rsvt_yn);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("CDHD_ID",cdhd_id);
			dataSet.setString("APPR_OPION",appr_opion);
			dataSet.setString("ADD_APPR_OPION",add_appr_opion);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmParRsUpdDaoProc proc = (GolfadmParRsUpdDaoProc)context.getProc("GolfadmParRsUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);
			
			
	        if (editResult == 1) {
				request.setAttribute("returnUrl", "admParRsList.do");
				request.setAttribute("resultMsg", "���� �������� ���� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", "admParRsList.do");
				request.setAttribute("resultMsg", "���� ������ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
