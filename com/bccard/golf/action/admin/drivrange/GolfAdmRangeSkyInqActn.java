/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeSkyInqActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ��û(sky72) �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-05-25
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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeSkyExcelDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeSkyInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeSkyInqActn extends GolfActn{
	
	public static final String TITLE = "������ �帲 ���������� ��û(sky72) �󼼺���";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			int int_able = 0;
			int int_done = 0;
			int int_can = 0;
			String memGrade = "";
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String rsvt_sql_no		= parser.getParameter("p_idx", "");
			String cdhd_id			= parser.getParameter("CDHD_ID","");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_sql_no);
			dataSet.setString("CDHD_ID", cdhd_id);
			
			debug(">>>>>>>>>>>>.    CDHD_ID:"+cdhd_id+" :: p_idx:"+rsvt_sql_no);
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmRangeSkyInqDaoProc proc = (GolfAdmRangeSkyInqDaoProc)context.getProc("GolfAdmRangeSkyInqDaoProc");
			DbTaoResult rangeskyInq = proc.execute(context, dataSet);
			
			// 04-1. benefit ��ȸ
			GolfAdmBkBenefitTimesDaoProc benefit_proc = (GolfAdmBkBenefitTimesDaoProc)context.getProc("GolfAdmBkBenefitTimesDaoProc");
			DbTaoResult benefit = benefit_proc.getDrivingSkyBenefit(context, dataSet);
			if(benefit.isNext()){
				benefit.next();
				int_able = benefit.getInt("DRGF_YR_ABLE");
				int_done = benefit.getInt("DRGF_YR_DONE");
				memGrade = benefit.getString("MEMGRADE");
			}
			int_can = int_able - int_done;
			
			paramMap.put("TOT_CNT", Integer.toString(int_able));
			paramMap.put("DONE_CNT", Integer.toString(int_done));
			paramMap.put("CAN_CNT", Integer.toString(int_can));
			paramMap.put("MEMGRADE", memGrade);
			
			
			
			// 04-2.�ϴ� ����Ʈ
			dataSet.setLong("PAGE_NO", 1L);
			dataSet.setLong("RECORD_SIZE", 10L);
			dataSet.setString("START_DT", "");
			dataSet.setString("END_DT", "");
			dataSet.setString("RSVT_YN", "");
			dataSet.setString("ATD_YN", "");
			dataSet.setString("SEARCH_YN","Y");
			dataSet.setString("SEARCH_SEL", "TGU.CDHD_ID");
			dataSet.setString("SEARCH_WORD", cdhd_id);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmRangeSkyExcelDaoProc inner_proc = (GolfAdmRangeSkyExcelDaoProc)context.getProc("GolfAdmRangeSkyExcelDaoProc");
			DbTaoResult rangeskyListResult = (DbTaoResult) inner_proc.execute(context, request, dataSet);

			paramMap.put("CDHD_ID", cdhd_id);			
			request.setAttribute("rangeskyListResult", rangeskyListResult);
			request.setAttribute("rangeskyInqResult", rangeskyInq);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
