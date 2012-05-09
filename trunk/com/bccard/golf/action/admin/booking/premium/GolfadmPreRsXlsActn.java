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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmPreRsListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfadmPreRsXlsActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ���� ����Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletRespo nse ��ü. 
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
				
			
			String sch_GR_SEQ_NO		= parser.getParameter("SCH_GR_SEQ_NO", "");
			String sch_RSVT_YN			= parser.getParameter("SCH_RSVT_YN", "");
			String sch_SEARCH_YN		= parser.getParameter("SEARCH_YN", "N");
			String sch_DATE				= parser.getParameter("SCH_DATE", "");
			String sch_HP_NO			= parser.getParameter("SCH_HP_NO", "");
			String sch_ID				= parser.getParameter("SCH_ID", "");
			
			String sch_ST_YEAR 			= parser.getParameter("ST_YEAR","");
			String sch_ST_MONTH 		= parser.getParameter("ST_MONTH","");
			String sch_ST_DAY 			= parser.getParameter("ST_DAY","");
			String sch_ED_YEAR 			= parser.getParameter("ED_YEAR","");
			String sch_ED_MONTH 		= parser.getParameter("ED_MONTH","");
			String sch_ED_DAY 			= parser.getParameter("ED_DAY","");
			
			String sch_DATE_ST			= sch_ST_YEAR+sch_ST_MONTH+sch_ST_DAY;
			String sch_DATE_ED			= sch_ED_YEAR+sch_ED_MONTH+sch_ED_DAY;
			
			String sort					= "0001";
			
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_GR_SEQ_NO", sch_GR_SEQ_NO);
			dataSet.setString("SCH_RSVT_YN", sch_RSVT_YN);
			dataSet.setString("SEARCH_YN", sch_SEARCH_YN);
			dataSet.setString("SCH_DATE", sch_DATE);
			dataSet.setString("SCH_DATE_ST", sch_DATE_ST);
			dataSet.setString("SCH_DATE_ED", sch_DATE_ED);
			dataSet.setString("SCH_HP_NO", sch_HP_NO);
			dataSet.setString("SCH_ID", sch_ID);
			dataSet.setString("SORT", sort);
			dataSet.setString("LISTTYPE", "XLS");

			
			// 04.���� ���̺�(Proc) ��ȸ - ����Ʈ
			GolfadmPreRsListDaoProc proc = (GolfadmPreRsListDaoProc)context.getProc("GolfadmPreRsListDaoProc");
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
