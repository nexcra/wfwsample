/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPreRsListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾� ���� ����Ʈ
*   �������  : Golf
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
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.*;
import com.bccard.golf.dbtao.proc.admin.booking.par.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmParRsXlsActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ���� ����Ʈ";

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
				

			String sort					= "0002";
			String sch_gr_seq_no			= parser.getParameter("SCH_GR_SEQ_NO", "");
			String sch_rsvt_yn			= parser.getParameter("SCH_RSVT_YN", "");
			String sch_date				= parser.getParameter("SCH_DATE", "");
			String sch_hp_no			= parser.getParameter("SCH_HP_NO", "");
			String sch_id				= parser.getParameter("SCH_ID", "");
			String sch_type				= parser.getParameter("SCH_TYPE", "");
			String sch_text				= parser.getParameter("SCH_TEXT", "");
			String sch_search_yn 		= parser.getParameter("SEARCH_YN","N");
			
			String sch_ST_YEAR 			= parser.getParameter("ST_YEAR","");
			String sch_ST_MONTH 		= parser.getParameter("ST_MONTH","");
			String sch_ST_DAY 			= parser.getParameter("ST_DAY","");
			String sch_ED_YEAR 			= parser.getParameter("ED_YEAR","");
			String sch_ED_MONTH 		= parser.getParameter("ED_MONTH","");
			String sch_ED_DAY 			= parser.getParameter("ED_DAY","");
			
			String sch_date_st			= sch_ST_YEAR+sch_ST_MONTH+sch_ST_DAY;
			String sch_date_ed			= sch_ED_YEAR+sch_ED_MONTH+sch_ED_DAY;
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_GR_SEQ_NO", sch_gr_seq_no);
			dataSet.setString("SCH_RSVT_YN", sch_rsvt_yn);
			dataSet.setString("SEARCH_YN",sch_search_yn);
			dataSet.setString("SCH_DATE", sch_date);
			dataSet.setString("SCH_DATE_ST", sch_date_st);
			dataSet.setString("SCH_DATE_ED", sch_date_ed);
			dataSet.setString("SCH_HP_NO", sch_hp_no);
			dataSet.setString("SCH_TYPE", sch_type);
			dataSet.setString("SCH_TEXT", sch_text);
			dataSet.setString("SCH_ID", sch_id);
			dataSet.setString("SORT", sort);
			dataSet.setString("LISTTYPE", "XLS");


			// 04.���� ���̺�(Proc) ��ȸ - ������
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);

									
			// 04.���� ���̺�(Proc) ��ȸ - ����Ʈ
			GolfadmParRsListDaoProc proc = (GolfadmParRsListDaoProc)context.getProc("GolfadmParRsListDaoProc");
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
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
