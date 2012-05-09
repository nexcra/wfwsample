/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmOrdUpdFormActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > �ֹ���
*   �������  : golf
*   �ۼ�����  : 2010-03-04
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
*20110323  �̰��� 	���̽�ĳ�� ����
***************************************************************************************************/
package com.bccard.golf.action.admin.event.shop;

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
import com.bccard.golf.dbtao.proc.admin.event.shop.GolfAdmOrdUpdFormDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfAdmOrdUpdFormActn extends GolfActn{

	public static final String TITLE = "������ > �̺�Ʈ > �ֹ���";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String odr_no			= parser.getParameter("odr_no", "");
			String gubun			= parser.getParameter("gubun", "");	
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("odr_no", odr_no);
			dataSet.setString("gubun", gubun);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmOrdUpdFormDaoProc proc = (GolfAdmOrdUpdFormDaoProc)context.getProc("GolfAdmOrdUpdFormDaoProc");
			DbTaoResult updFormResult = proc.execute(context, dataSet);

			// JSP �������� �������� �ʼ���
			request.setAttribute("UpdFormResult", updFormResult);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
