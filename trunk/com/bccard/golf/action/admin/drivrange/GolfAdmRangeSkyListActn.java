/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeSkyListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ��û(sky72) ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeSkyListDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeSkyListActn extends GolfActn{
	
	public static final String TITLE = "������ �帲 ���������� ��û(sky72) ����Ʈ";

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

			String rsvt_yn			= parser.getParameter("s_rsvt_yn", "");		// ���±���
			String atd_yn			= parser.getParameter("s_atd_yn", "");		// ��������
			String s_gr				= parser.getParameter("s_gr", "");		// �����屸��
			String search_sel		= parser.getParameter("search_sel", "");		// �����˻�����
			String search_word		= parser.getParameter("search_word", "");		// �����˻�����
			String search_yn 		= parser.getParameter("SEARCH_YN","N");		
			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY",""); 
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
			String tot_cnt = "0";
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			
			dataSet.setString("START_DT", sch_date_st);
			dataSet.setString("END_DT", sch_date_ed);
			dataSet.setString("RSVT_YN", rsvt_yn);
			dataSet.setString("ATD_YN", atd_yn);
			dataSet.setString("s_gr", s_gr);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SEARCH_YN",search_yn);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmRangeSkyListDaoProc proc = (GolfAdmRangeSkyListDaoProc)context.getProc("GolfAdmRangeSkyListDaoProc");
			DbTaoResult rangeskyListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			if(rangeskyListResult.isNext()){
				rangeskyListResult.next();
				
				if("00".equals(rangeskyListResult.getString("RESULT"))){
					tot_cnt = rangeskyListResult.getString("TOTAL_CNT");
				}
			}
			paramMap.put("TOTAL_CNT", tot_cnt);
			paramMap.put("resultSize", String.valueOf(rangeskyListResult.size()));
			
			
			
			//�˻� �κ� ����
			paramMap.put("page_no",Long.toString(page_no));
			paramMap.put("record_size",Long.toString(record_size));
			paramMap.put("s_rsvt_yn", rsvt_yn);
			paramMap.put("s_atd_yn", atd_yn); 
			paramMap.put("s_gr",s_gr);
			paramMap.put("search_sel", search_sel);
			paramMap.put("search_word", search_word);
			paramMap.put("SEARCH_YN", search_yn);
			
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			
			
			request.setAttribute("rangeskyListResult", rangeskyListResult);
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
