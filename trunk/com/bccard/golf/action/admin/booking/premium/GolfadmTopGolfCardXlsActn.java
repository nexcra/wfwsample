/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmVipEventListActn
*   �ۼ���    : ������
*   ����      : ������ >  ��ŷ > TOP����ī�� ���� ��ŷ
*   �������  : Golf
*   �ۼ�����  : 2010-10-15
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
public class GolfadmTopGolfCardXlsActn extends GolfActn{
	
	public static final String TITLE = "������ >  ��ŷ > TOP����ī�� ���� ��ŷ";

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
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			String green_nm				= parser.getParameter("green_nm","");           //��û�������
			String pgrs_yn		= parser.getParameter("pgrs_yn","");     //�������
			
			String sch_date_gubun				= parser.getParameter("sch_date_gubun","");          	//�˻����ڱ�����
			
			String sch_type				= parser.getParameter("sch_type","");           //�̸�,ID��ȸ ����
			String search_word			= parser.getParameter("search_word","");        //��ȸ ��

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
			
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("pgrs_yn",pgrs_yn); 
			
			dataSet.setString("sch_date_gubun",sch_date_gubun);
			dataSet.setString("sch_reg_aton_st",sch_date_st);
			dataSet.setString("sch_reg_aton_ed",sch_date_ed);
			
			
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmTopGolfCardDaoProc proc = (GolfAdmTopGolfCardDaoProc)context.getProc("GolfAdmTopGolfCardDaoProc");
		
			DbTaoResult listResult = (DbTaoResult) proc.execute_excel(context, request, dataSet); 

			paramMap.put("resultSize", String.valueOf(listResult.size()));
			
			paramMap.put("green_nm",green_nm);			
			paramMap.put("pgrs_yn",pgrs_yn);
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("total_cnt",String.valueOf(listResult.size()));
		
			request.setAttribute("listResult", listResult);	
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
