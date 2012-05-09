/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopBkSuccSatisAction
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������ > ��ŷ > TOP����ī�������ŷ > TOP�������ŷ��û��Ȳ
*   �������  : golf
*   �ۼ�����  : 2010-12-29
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopBkSuccSatisDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfadmTopBkSuccSatisAction extends GolfActn{
 

	public static final String TITLE = "TOP��ŷ����Ƚ�������";
	
	/***************************************************************************************
	 * ž��ŷ����Ƚ������� ������ȭ�� 
	 * @param context  WaContext ��ü. 
	 * @param request  HttpServletRequest ��ü. 
	 * @param response  HttpServletResponse ��ü. 
	 * @return ActionResponse Action ó���� ȭ�鿡 ���÷����� ����. 
	 ***************************************************************************************/	 
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
			
			// Request �� ����
			String mode = parser.getParameter("mode", "INIT");			
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");
			String repMbNo = parser.getParameter("repMbNo", "00");
			   
			paramMap.put("mode", mode);
			paramMap.put("from", from);
			paramMap.put("to", to);
			paramMap.put("repMbNo", repMbNo);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("mode", mode);
			dataSet.setString("from", from);
			dataSet.setString("to", to);
			dataSet.setString("repMbNo", repMbNo);
						
			GolfadmTopBkSuccSatisDaoProc instance = GolfadmTopBkSuccSatisDaoProc.getInstance();
			
			DbTaoResult listResult = null;
			
			if (!"INIT".equals(mode)) {	
				listResult = instance.execute(context, request, dataSet);								
				request.setAttribute("BkngSuccessStatis", listResult);			    
			}			

			request.setAttribute("paramMap", paramMap);			   
			
			if (mode.equals("EXCEL")) { subpage_key = "excel"; }
			if (mode.equals("PRINT")) { subpage_key = "print"; } 
		         
		} catch(Throwable t) {
			debug(TITLE, t);
			t.printStackTrace(); 
		    throw new GolfException(TITLE, t);
		} 
		  
		return super.getActionResponse(context, subpage_key);
	
	} 	  

}