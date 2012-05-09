/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admPreTimeListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾� ƼŸ�� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-21
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
import com.bccard.golf.dbtao.proc.admin.booking.*;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmPreTimeXlsActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ƼŸ�� ����Ʈ";

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
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String sort			= parser.getParameter("SORT", "0001"); //0001:�����̾� 0002:��3��ŷ
			String sch_YN				= parser.getParameter("SCH_YN", "");
			String sch_GR_SEQ_NO		= parser.getParameter("SCH_GR_SEQ_NO", "");
			String sch_RESER_CODE		= parser.getParameter("SCH_RESER_CODE", "");
			String sch_VIEW_YN			= parser.getParameter("SCH_VIEW_YN", "");
			String sch_EVNT_YN			= parser.getParameter("SCH_EVNT_YN", "");
			String sch_DATE				= parser.getParameter("SCH_DATE", "");
			String sch_DATE_ST			= parser.getParameter("SCH_DATE_ST", "");
			String sch_DATE_ED			= parser.getParameter("SCH_DATE_ED", "");
			
			
			paramMap.put("SORT", sort);
			paramMap.put("SCH_YN", sch_YN);
			paramMap.put("SCH_GR_SEQ_NO", sch_GR_SEQ_NO);
			paramMap.put("SCH_RESER_CODE", sch_RESER_CODE);
			paramMap.put("SCH_VIEW_YN", sch_VIEW_YN);
			paramMap.put("SCH_EVNT_YN", sch_EVNT_YN);
			paramMap.put("SCH_DATE", sch_DATE);
			paramMap.put("SCH_DATE_ST", sch_DATE_ST);
			paramMap.put("SCH_DATE_ED", sch_DATE_ED);

						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SORT", sort);
			dataSet.setString("SCH_YN", sch_YN);
			dataSet.setString("SCH_GR_SEQ_NO", sch_GR_SEQ_NO);
			dataSet.setString("SCH_RESER_CODE", sch_RESER_CODE);
			dataSet.setString("SCH_VIEW_YN", sch_VIEW_YN);
			dataSet.setString("SCH_EVNT_YN", sch_EVNT_YN);
			dataSet.setString("SCH_DATE", sch_DATE);
			dataSet.setString("SCH_DATE_ST", sch_DATE_ST);
			dataSet.setString("SCH_DATE_ED", sch_DATE_ED);
			dataSet.setString("LISTTYPE", "XLS");


			// 04.���� ���̺�(Proc) ��ȸ - ������
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);

									
			// 04.���� ���̺�(Proc) ��ȸ - ����Ʈ
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			else
				request.setAttribute("total_cnt", "0");

			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("sort", sort);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
