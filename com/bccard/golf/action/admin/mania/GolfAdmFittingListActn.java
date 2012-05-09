/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

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
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmFittingListDaoProc;
/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmFittingListActn extends GolfActn{
	
	public static final String TITLE = "������ �����帮�������ν�û���� ����Ʈ";

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
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String scoop_cp_cd		= parser.getParameter("scoop_cp_cd", ""); 
			
			String search_club		= parser.getParameter("search_club", ""); 
			String search_csl		= parser.getParameter("search_csl", ""); 
			

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
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SEARCH_DT1", sch_date_st);
			dataSet.setString("SEARCH_DT2", sch_date_ed);
			dataSet.setString("SCOOP_CP_CD", scoop_cp_cd);
			
			dataSet.setString("SEARCH_CLUB", search_club);
			dataSet.setString("SEARCH_CSL", search_csl);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmFittingListDaoProc proc = (GolfAdmFittingListDaoProc)context.getProc("GolfAdmFittingListDaoProc");
			DbTaoResult maniaListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");//@ ���� �̾ƿ��� 
			DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0022", "Y"); //@ ���Ŭ�� �̾ƿ���

			paramMap.put("resultSize", String.valueOf(maniaListResult.size()));
			request.setAttribute("maniaListResult", maniaListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			
//			 ��ü 0��  [ 0/0 page] ���� �������� 
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (maniaListResult != null && maniaListResult.isNext()) {
				maniaListResult.first();
				maniaListResult.next(); 
				if (maniaListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)maniaListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)maniaListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			
			//05. Return �� ����	
			request.setAttribute("maniaListResult", maniaListResult);
			request.setAttribute("coopCpSel", coopCpSel);	//@ ���� �̾ƿ��� 
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}