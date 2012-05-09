/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ��ŷ > �����̾� > ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

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
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmMemBkListActn extends GolfActn{
	
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ȸ������Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			long bk_page_no		= parser.getLongParameter("bk_page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 20);		// ����������¼�	
			String sch_GRADE	= parser.getParameter("SCH_GRADE", "");	
			String sch_STATE	= parser.getParameter("SCH_STATE", "");	
			String sch_MONEY_ST	= parser.getParameter("SCH_MONEY_ST", "");	
			String sch_MONEY_ED	= parser.getParameter("SCH_MONEY_ED", "");	
			String sch_DATE_ST	= parser.getParameter("SCH_DATE_ST", "");	
			String sch_DATE_ED	= parser.getParameter("SCH_DATE_ED", "");	
			String sch_TYPE		= parser.getParameter("SCH_TYPE", "");	
			String sch_TEXT		= parser.getParameter("SCH_TEXT", "");	
			String cdhd_ID		= parser.getParameter("CDHD_ID", "");	
			String order_yn		= parser.getParameter("ORDER_YN", "");	
			String order_nm		= parser.getParameter("ORDER_NM", "");	
			String order_value	= parser.getParameter("ORDER_VALUE", "");	
			String juminNo		= parser.getParameter("JUMIN_NO");
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("bk_page_no", bk_page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_GRADE", sch_GRADE);
			dataSet.setString("SCH_STATE", sch_STATE);
			dataSet.setString("SCH_MONEY_ST", sch_MONEY_ST);
			dataSet.setString("SCH_MONEY_ED", sch_MONEY_ED);
			dataSet.setString("SCH_DATE_ST", sch_DATE_ST);
			dataSet.setString("SCH_DATE_ED", sch_DATE_ED);
			dataSet.setString("SCH_TYPE", sch_TYPE);
			dataSet.setString("SCH_TEXT", sch_TEXT);
			dataSet.setString("CDHD_ID", cdhd_ID);
			dataSet.setString("ORDER_YN", order_yn);
			dataSet.setString("ORDER_NM", order_nm);
			dataSet.setString("ORDER_VALUE", order_value);

			// 04.���� ���̺�(Proc) ��ȸ - ��ü �ѹ�
			GolfAdmMemBkViewDaoProc procView = (GolfAdmMemBkViewDaoProc)context.getProc("GolfAdmMemBkViewDaoProc");
			DbTaoResult viewResult = (DbTaoResult) procView.execute(context, request, dataSet);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMemBkListDaoProc proc = (GolfAdmMemBkListDaoProc)context.getProc("GolfAdmMemBkListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult grdResult = (DbTaoResult) proc.memGrd_execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(listResult.size()));

			request.setAttribute("ViewResult", viewResult);
			request.setAttribute("ListResult", listResult);
			request.setAttribute("grdResult", grdResult);
			request.setAttribute("record_size", String.valueOf(record_size));			
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("JUMIN_NO", juminNo);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
