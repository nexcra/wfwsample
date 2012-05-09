/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCouponInfoExcelActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �����μ��̷� �����ٿ�ε�
*   �������  : Golf
*   �ۼ�����  : 2009-07-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

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
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmCouponInfoExcelDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmCouponInfoExcelActn extends GolfActn{
	
	public static final String TITLE = "������ �����μ��̷� �����ٿ�ε�";

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
			
			String exec_type_cd	= parser.getParameter("s_exec_type_cd", "");		// ����
			String user_clss	= parser.getParameter("s_user_clss", "");		// ȸ�����
			String start_dt	= parser.getParameter("s_start_dt", "");		// �������1
			String end_dt	= parser.getParameter("s_end_dt", "");		// �������2
			String search_sel	= parser.getParameter("search_sel", "");		// �����˻�����
			String search_word	= parser.getParameter("search_word", "");		// �����˻�����
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("EXEC_TYPE_CD", exec_type_cd);
			dataSet.setString("USER_CLSS", user_clss);
			dataSet.setString("START_DT", start_dt);
			dataSet.setString("END_DT", end_dt);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmCouponInfoExcelDaoProc proc = (GolfAdmCouponInfoExcelDaoProc)context.getProc("GolfAdmCouponInfoExcelDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult couponinfoListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			
			//�ڵ� ��ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0008", "Y"); //�����������ڵ�

			paramMap.put("resultSize", String.valueOf(couponinfoListResult.size()));
			
			if (exec_type_cd.equals("")){
				paramMap.put("s_exec_type_cd", "0001");
			}
			
			request.setAttribute("couponinfoListResult", couponinfoListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel", coopCpSel);	
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
