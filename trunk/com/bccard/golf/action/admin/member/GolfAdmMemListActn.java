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
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComSelectListDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
* golfloung		20100302	������	�˻����� ����Ʈ �߰�
******************************************************************************/
public class GolfAdmMemListActn extends GolfActn{
	
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
			// ȸ���������̺� ���� �������� ����
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 20);		// ����������¼�		
			String sch_YN		= parser.getParameter("SCH_YN", "");	
			String sch_GRADE	= parser.getParameter("SCH_GRADE", "");	
			String sch_STATE	= parser.getParameter("SCH_STATE", "");	
			String sch_ROUTE	= parser.getParameter("SCH_ROUTE", "");	
			String sch_DATE		= parser.getParameter("SCH_DATE", "join_date");				

			String st_year 		= parser.getParameter("ST_YEAR","");
			String st_month 	= parser.getParameter("ST_MONTH","");
			String st_day 		= parser.getParameter("ST_DAY","");
			String ed_year 		= parser.getParameter("ED_YEAR","");
			String ed_month 	= parser.getParameter("ED_MONTH","");
			String ed_day 		= parser.getParameter("ED_DAY","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
			
			String sch_MONEY_ST	= parser.getParameter("SCH_MONEY_ST", "");	
			String sch_MONEY_ED	= parser.getParameter("SCH_MONEY_ED", "");	
//			String sch_DATE_ST	= parser.getParameter("SCH_DATE_ST", "");	
//			String sch_DATE_ED	= parser.getParameter("SCH_DATE_ED", "");	
			String sch_TYPE		= parser.getParameter("SCH_TYPE", "");	
			String sch_TEXT		= parser.getParameter("SCH_TEXT", "");	
			String listype		= parser.getParameter("LISTTYPE", "");	
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_YN", sch_YN);
			dataSet.setString("SCH_GRADE", sch_GRADE);
			dataSet.setString("SCH_STATE", sch_STATE);
			dataSet.setString("SCH_ROUTE", sch_ROUTE);
			dataSet.setString("SCH_DATE", sch_DATE);
			dataSet.setString("SCH_MONEY_ST", sch_MONEY_ST);
			dataSet.setString("SCH_MONEY_ED", sch_MONEY_ED);
			dataSet.setString("SCH_DATE_ST", sch_date_st);
			dataSet.setString("SCH_DATE_ED", sch_date_ed);
			dataSet.setString("SCH_TYPE", sch_TYPE);
			dataSet.setString("SCH_TEXT", sch_TEXT);
			dataSet.setString("LISTTYPE", listype);
			

			// 04. ��޼���Ʈ ��ȸ
			dataSet.setString("boardCode", "0052");
			GolfAdmBoardComSelectListDaoProc proc_select = (GolfAdmBoardComSelectListDaoProc)context.getProc("GolfAdmBoardComSelectListDaoProc");
			DbTaoResult boardSelectListInq = (DbTaoResult)proc_select.execute(context, request, dataSet);
			request.setAttribute("boardSelectListInq", boardSelectListInq);

			// 04. ���԰�� ��ȸ
			dataSet.setString("boardCode", "0051");
			GolfAdmBoardComSelectListDaoProc proc_select2 = (GolfAdmBoardComSelectListDaoProc)context.getProc("GolfAdmBoardComSelectListDaoProc");
			DbTaoResult boardSelect2ListInq = (DbTaoResult)proc_select2.execute(context, request, dataSet);
			request.setAttribute("boardSelect2ListInq", boardSelect2ListInq);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMemListDaoProc proc = (GolfAdmMemListDaoProc)context.getProc("GolfAdmMemListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				paramMap.put("total_cnt", listResult.getString("TOT_CNT"));
			else
				paramMap.put("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("sch_GRADE", sch_GRADE);
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day); 
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);

			//ȸ�� �� ��ȸ	
			//DbTaoResult memberResult = (DbTaoResult) proc.execute_member(context, request, dataSet); 

			request.setAttribute("ListResult", listResult);
			//request.setAttribute("MemberResult", memberResult);
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
