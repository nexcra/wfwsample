/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTargetPpConfListActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ ��ŷ������
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmTopGolfTargetPpConfListActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ������";

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
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�		
			
			String idx				= parser.getParameter("idx","");           //idx
			String sort				= parser.getParameter("sort","1000"); 
			
			
						
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no); 
			dataSet.setLong("RECORD_SIZE", record_size);
			
			dataSet.setString("idx",idx);
			dataSet.setString("sort",sort);
			
			// 04.���� ���̺�(Proc) ��ȸ
			//GolfAdmTopGolfCardDaoProc proc = (GolfAdmTopGolfCardDaoProc)context.getProc("GolfAdmTopGolfCardDaoProc");
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			
			//�ش� ƼŸ���� ��ó�� �� ��������
			DbTaoResult viewResult = (DbTaoResult) proc.topGolfConfView(context, request, dataSet);						
			
			DbTaoResult listResult = (DbTaoResult) proc.topGolfJoinPpList(context, request, dataSet, "Y");			
			
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));
			
			paramMap.put("idx",idx);
			paramMap.put("sort",sort);
			
			request.setAttribute("listResult", listResult);	
			request.setAttribute("viewResult", viewResult);	
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	   
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
