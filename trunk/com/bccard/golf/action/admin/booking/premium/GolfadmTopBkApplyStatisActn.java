/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopBkApplyStatisActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������ > ��ŷ > TOP����ī�������ŷ > TOP��ŷ��û�����
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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopBkStatisDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfadmTopBkApplyStatisActn extends GolfActn{
 

	public static final String TITLE = "TOP��ŷ��û�����";
	
	/***************************************************************************************  
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
			String diff = parser.getParameter("diff", "0");
			String yyyy = parser.getParameter("yyyy");
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");			
			String repMbNo = parser.getParameter("repMbNo", "00");			
			String repMbNoNm = parser.getParameter("repMbNoNm");
						
			String bkngStat   = parser.getParameter("parm1");
			String memberClss = parser.getParameter("parm2");
			String gubun = parser.getParameter("parm3");
			   
			paramMap.put("diff", diff);
			paramMap.put("yyyy", yyyy);
			paramMap.put("from", from);
			paramMap.put("to", to);			
			paramMap.put("repMbNo", repMbNo);			
			paramMap.put("repMbNoNm", repMbNoNm);			
			paramMap.put("bkngStat", bkngStat);
			paramMap.put("memberClss", memberClss);
			paramMap.put("gubun", gubun);			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);   
						
			dataSet.setString("bkngStat", bkngStat);
			dataSet.setString("memberClss", memberClss);
			dataSet.setString("gubun", gubun);
			dataSet.setString("diff", diff);
			dataSet.setString("yyyy", yyyy);
			dataSet.setString("from", from);
			dataSet.setString("to", to);
			dataSet.setString("repMbNo", repMbNo);
						
			GolfadmTopBkStatisDaoProc instance = GolfadmTopBkStatisDaoProc.getInstance();
			   
			DbTaoResult listResult = null;
			   
			if (!"INIT".equals(mode)) {
			    
				// 04.���� ���̺�(Proc) ��ȸ - ����Ʈ
				if (mode.equals("EXCELDETAIL")){	
					listResult = instance.excelDetail(context, request, dataSet);
				}else {					
				    listResult = instance.execute(context, request, dataSet);
				}
				
				request.setAttribute("BkngApplyStatis", listResult);
			    
			}
			
			request.setAttribute("paramMap", paramMap);
			
			request.setAttribute("bkngStat", bkngStat);	
			request.setAttribute("repMbNoNm", repMbNoNm);
			
			if (mode.equals("EXCELDETAIL")) {
				
				if(gubun.equals("1")) gubun = "��û";
				else if(gubun.equals("2")) gubun = "����";
				else if(gubun.equals("3")) gubun = "����";
				else if(gubun.equals("4")) gubun = "���";
				
				if (memberClss.equals("1"))	request.setAttribute("gubun", "����ȸ��_"+gubun);
				else if (memberClss.equals("5")) request.setAttribute("gubun", "����ȸ��_"+gubun);
				else request.setAttribute("gubun", "����+����ȸ��_"+gubun);
				
			}			
			   
			if (mode.equals("EXCEL")) { subpage_key = "excel"; }
			if (mode.equals("EXCELDETAIL")) { subpage_key = "excelDetail"; }
			if (mode.equals("PRINT")) { subpage_key = "print"; } 
		         
		} catch(Throwable t) {
			debug(TITLE, t);
			t.printStackTrace(); 
		    throw new GolfException(TITLE, t);
		} 
		  
		return super.getActionResponse(context, subpage_key);

	
	} 
  

}