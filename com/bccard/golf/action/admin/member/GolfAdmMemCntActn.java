/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemCntActn
*   �ۼ���    : ������
*   ����      : ������ > ���ΰ��� > ȸ������ > ȸ����
*   �������  : Golf
*   �ۼ�����  : 2009-07-28
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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComSelectListDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.*;
import com.bccard.golf.common.GolfUtil;
import com.bccard.waf.common.DateUtil;

/******************************************************************************
* Topn
* @author	
* @version	1.0
******************************************************************************/
public class GolfAdmMemCntActn extends GolfActn{
	
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ȸ����";

	/***************************************************************************************
	* ��������� ������ȭ��
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

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;

			
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("st_date", sch_date_st + "000000");
			dataSet.setString("end_date", sch_date_ed + "240000");



						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMemCntDaoProc proc = (GolfAdmMemCntDaoProc)context.getProc("GolfAdmMemCntDaoProc");
			DbTaoResult memberResult = (DbTaoResult) proc.execute(context, request, dataSet);


			DbTaoResult memberGradeResult = (DbTaoResult) proc.execute_grade(context, request, dataSet);
			
			DbTaoResult joinChnlResult = (DbTaoResult) proc.execute_joinchnl(context, request, dataSet);
			DbTaoResult dateGradeResult = (DbTaoResult) proc.execute_dategrade(context, request, dataSet);


			request.setAttribute("MemberResult", memberResult);
			request.setAttribute("MemberGradeResult", memberGradeResult);
			request.setAttribute("joinChnlResult", joinChnlResult);

			request.setAttribute("dateGradeResult", dateGradeResult);

			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
