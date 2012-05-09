/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmSpecialBookingListActn
*   �ۼ���    : ������ ���弱
*   ����      : ������ > �̺�Ʈ->VIP��ŷ�̺�Ʈ->���������ŷ 
*   �������  : Golf
*   �ۼ�����  : 2009-09-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
* golfloung		20100305	������	�˻�����
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSpecialBookingDaoProc;

/******************************************************************************
* Golf
* @author	������ ���弱
* @version	1.0
******************************************************************************/
public class GolfAdmSpecialBookingListActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ��÷�� ����Ʈ";

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
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�		
			String green_nm				= parser.getParameter("green_nm","");           //��û�������
			String golf_cmmn_code		= parser.getParameter("golf_cmmn_code","");     //�����ڵ�
			String grade				= parser.getParameter("grade","");     
			String sch_yn				= parser.getParameter("sch_yn","");          	//�˻�����
			String sch_date				= parser.getParameter("sch_date","");          	//�˻����ڱ���
			String sch_type				= parser.getParameter("sch_type","");           //�̸�,ID��ȸ ����
			String search_word			= parser.getParameter("search_word","");        //��ȸ ��

			String sch_chng_aton_st		= parser.getParameter("sch_chng_aton_st","");   //��ȸ ������� ������
			String sch_chng_aton_ed		= parser.getParameter("sch_chng_aton_ed","");   //��ȸ ������� ������
			String doyn			        = parser.getParameter("doyn","");               //ó������
			

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
			
//			debug("green_nm>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + green_nm); 
//			debug("sch_yn>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_yn); 
//			debug("golf_cmmn_code>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + golf_cmmn_code); 
//			debug("grade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + grade);
//			debug("sch_reg_aton_st>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_reg_aton_st);
//			debug("sch_reg_aton_ed>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_reg_aton_ed);
//			debug("sch_pu_date_st>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_pu_date_st);
//			debug("sch_pu_date_ed>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_pu_date_ed);
//			debug("sch_type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_type);
//			debug("search_word>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + search_word);

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("sch_yn",sch_yn);
			dataSet.setString("sch_date",sch_date);
			dataSet.setString("golf_cmmn_code",golf_cmmn_code);
			dataSet.setString("grade",grade);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("sch_reg_aton_st",sch_date_st);
			dataSet.setString("sch_reg_aton_ed",sch_date_ed);
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			dataSet.setString("actnKey",actnKey);
			dataSet.setString("sch_chng_aton_st",sch_chng_aton_st);
			dataSet.setString("sch_chng_aton_ed",sch_chng_aton_ed);
			dataSet.setString("doyn",doyn);


	
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmSpecialBookingDaoProc proc = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
		
			DbTaoResult evntMMListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(evntMMListResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));

			
			paramMap.put("green_nm",green_nm);			
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("grade",grade	);		
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("total_cnt",String.valueOf(evntMMListResult.size()));
			paramMap.put("sch_chng_aton_st",sch_chng_aton_st);	
			paramMap.put("sch_chng_aton_ed",sch_chng_aton_ed);	
			paramMap.put("doyn",doyn);	
		
			request.setAttribute("evntMMListResult", evntMMListResult);	
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
