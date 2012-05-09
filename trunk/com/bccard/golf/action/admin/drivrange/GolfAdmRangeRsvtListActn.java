/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeRsvtListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ���� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeRsvtListDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeTimeSelDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeRsvtListActn extends GolfActn{
	
	public static final String TITLE = "������ �帲 ���������� ���� ����Ʈ";

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
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			
			String rsvt_clss = nowYear+"D"; // ���౸����
			String time	= parser.getParameter("s_time", "");		// �ð�
			String start_time = "";
			String end_time = "";
			
			if (!GolfUtil.isNull(time)){
				String[] arr_time = time.split("~");
				start_time = arr_time[0];
				end_time = arr_time[1];
			}

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			String sch_gr 			= parser.getParameter("SCH_GR_SEQ_NO","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
						
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			 
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			
			dataSet.setString("RSVT_CLSS", rsvt_clss);
			dataSet.setString("START_DT", sch_date_st);
			dataSet.setString("END_DT", sch_date_ed);
			dataSet.setString("START_TIME", start_time);
			dataSet.setString("END_TIME", end_time);
			dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
			dataSet.setString("SORT", AppConfig.getDataCodeProp("DrivingRange"));
			dataSet.setString("DrivR", AppConfig.getDataCodeProp("DrivingRangeClss"));
			
			// 04.���� ���̺�(Proc) ��ȸ - ������
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);			
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmRangeRsvtListDaoProc proc = (GolfAdmRangeRsvtListDaoProc)context.getProc("GolfAdmRangeRsvtListDaoProc");
			GolfAdmRangeTimeSelDaoProc coopTimeSelProc = (GolfAdmRangeTimeSelDaoProc)context.getProc("GolfAdmRangeTimeSelDaoProc");
			
			DbTaoResult rangersvtListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// ����ð��� ��ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopTimeSel = coopTimeSelProc.execute(context, dataSet); 

			paramMap.put("resultSize", String.valueOf(rangersvtListResult.size()));
			
			request.setAttribute("rangersvtListResult", rangersvtListResult);
			request.setAttribute("coopTimeSel", coopTimeSel);
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
