/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTargetConfViewActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ ��ŷ������ �� ����
*   �������  : Golf
*   �ۼ�����  : 2010-11-02
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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmPreTimeListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfadmTopGolfTargetConfViewActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ������ �� ����";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
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
			String seq				= parser.getParameter("seq","");           //idx
			String sort				= parser.getParameter("sort","1000"); 
			String type				= parser.getParameter("type","2"); 
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("seq",seq);
			dataSet.setString("sort",sort);
			dataSet.setString("type",type);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			
			DbTaoResult viewResult = (DbTaoResult) proc.getConfSumDetail(context, request, dataSet);
			DbTaoResult viewListResult = (DbTaoResult) proc.getConfListDetail(context, request, dataSet);
						
			
			request.setAttribute("viewResult", viewResult);	
			request.setAttribute("viewListResult", viewListResult);	
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
