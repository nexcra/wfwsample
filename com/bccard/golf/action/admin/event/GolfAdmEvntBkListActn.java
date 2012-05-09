/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ��ŷ �̺�Ʈ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
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
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBkListDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkListActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ �̺�Ʈ ����Ʈ";

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
		
		String key = getActionKey(context);

		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String sgr_nm		= parser.getParameter("sgr_nm", "");
			String sevent_yn	= parser.getParameter("sevent_yn", "");
			String sort			= parser.getParameter("SORT", "0001"); //0001:�����̾� 0002:��3��ŷ


			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 			= parser.getParameter("ST_MONTH","");
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

			String st_year2 			= parser.getParameter("ST_YEAR2","");
			String st_month2 			= parser.getParameter("ST_MONTH2","");
			String st_day2 			= parser.getParameter("ST_DAY2","");
			String ed_year2 			= parser.getParameter("ED_YEAR2","");
			String ed_month2 		= parser.getParameter("ED_MONTH2","");
			String ed_day2 			= parser.getParameter("ED_DAY2","");
			
			String sch_date_st2		= st_year2+st_month2+st_day2;
			String sch_date_ed2		= ed_year2+ed_month2+ed_day2;
						
			paramMap.put("ST_YEAR2",st_year2);
			paramMap.put("ST_MONTH2",st_month2);
			paramMap.put("ST_DAY2",st_day2);
			paramMap.put("ED_YEAR2",ed_year2);
			paramMap.put("ED_MONTH2",ed_month2);
			paramMap.put("ED_DAY2",ed_day2);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SGR_NM", sgr_nm);
			dataSet.setString("SEVENT_YN", sevent_yn);
			dataSet.setString("SBKPS_SDATE", sch_date_st);
			dataSet.setString("SBKPS_EDATE", sch_date_ed);
			dataSet.setString("SREG_SDATE", sch_date_st2+"000000");
			dataSet.setString("SREG_EDATE", sch_date_ed2+"235959");
			dataSet.setString("SORT", sort);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBkListDaoProc proc = (GolfAdmEvntBkListDaoProc)context.getProc("GolfAdmEvntBkListDaoProc");
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult evntPreBkTimeListResult = (DbTaoResult) proc.execute(context, request, dataSet, key);
			DbTaoResult titimeGreenListResult = (DbTaoResult) proc2.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(evntPreBkTimeListResult.size()));
			
			request.setAttribute("evntPreBkTimeListResult", evntPreBkTimeListResult);
			request.setAttribute("titimeGreenListResult", titimeGreenListResult);
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
