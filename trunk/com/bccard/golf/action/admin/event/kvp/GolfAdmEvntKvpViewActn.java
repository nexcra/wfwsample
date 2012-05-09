/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntKvpViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > Kvp > �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2010-06-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.kvp;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.kvp.GolfAdmEvntKvpViewDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpViewActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ > Kvp > �󼼺���";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// �˻���		APLC_SEQ_NO
			String aplc_seq_no		= parser.getParameter("aplc_seq_no", "");
			String jumin_no			= parser.getParameter("jumin_no", "");
			int cmmCode				= Integer.parseInt(parser.getParameter("cmmcode", ""));
			
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("jumin_no", jumin_no);
			
			// 04.���� ���̺�(Proc) ��ȸ 
			GolfAdmEvntKvpViewDaoProc proc = (GolfAdmEvntKvpViewDaoProc)context.getProc("GolfAdmEvntKvpViewDaoProc");
			
			// 04-1. �󼼺���
			DbTaoResult viewResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 04-2. �������� ����
			DbTaoResult payResult = (DbTaoResult) proc.execute_pay(context, request, dataSet);
			
			paramMap.put("cmmCode", Integer.toString(cmmCode));
			
			request.setAttribute("viewResult", viewResult);
			request.setAttribute("payResult", payResult);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
