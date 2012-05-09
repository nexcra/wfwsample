/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrUpdFormActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfadmPreRsViewActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ ������ ���� ��";

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
			int int_yr_done = 0;
			int int_mo_done = 0;
			String permission = "N";
			String memGrade = "";
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
 
			// Request �� ����
			String rsvt_SQL_NO		= parser.getParameter("RSVT_SQL_NO", "");
			String cdhd_id		= parser.getParameter("CDHD_ID", "");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_SQL_NO);
			dataSet.setString("CDHD_ID",cdhd_id);
			
			// 04.���� ���̺�(Proc) ��ȸ  :: �󼼺���(1��)
			GolfadmPreRsViewDaoProc proc = (GolfadmPreRsViewDaoProc)context.getProc("GolfadmPreRsViewDaoProc");
			DbTaoResult bkView = proc.execute(context, dataSet);
			
			// 04-1. Benefit ��ȸ
			GolfAdmBkBenefitTimesDaoProc benefit_proc = (GolfAdmBkBenefitTimesDaoProc)context.getProc("GolfAdmBkBenefitTimesDaoProc");
			DbTaoResult benefit = benefit_proc.getPreBkBenefit(context, dataSet);
			
			if(benefit.isNext()){
				benefit.next();
				
				int_yr_done = benefit.getInt("YR_DONE");
				int_mo_done = benefit.getInt("MO_DONE");
				permission = benefit.getString("PERMISSION");
				memGrade = benefit.getString("MEMGRADE");
			}
			
			paramMap.put("permission", permission);
			paramMap.put("MEMGRADE", memGrade);
			paramMap.put("YR_DONE", Integer.toString(int_yr_done));
			paramMap.put("MO_DONE", Integer.toString(int_mo_done));
			
			// 04-2  �ϴ� ����Ʈ��ȸ : ������ ���� 
			dataSet.setLong("page_no", 1L);
			dataSet.setLong("record_size", 1L);
			dataSet.setString("SCH_GR_SEQ_NO", "");
			dataSet.setString("SCH_RSVT_YN", "");
			dataSet.setString("SEARCH_YN","Y");
			dataSet.setString("SCH_DATE", "");
			dataSet.setString("SCH_DATE_ST", "");
			dataSet.setString("SCH_DATE_ED", "");
			dataSet.setString("SCH_TEXT", cdhd_id);
			dataSet.setString("SCH_TYPE", "T1.CDHD_ID");
			dataSet.setString("SORT", "0001");
			dataSet.setString("LISTTYPE", "XLS");
		
			// 04-1  �ϴ� ����Ʈ��ȸ
			GolfadmPreRsListDaoProc list = (GolfadmPreRsListDaoProc)context.getProc("GolfadmPreRsListDaoProc");
			DbTaoResult listResult = (DbTaoResult) list.execute(context, request, dataSet);
			
					
			paramMap.put("CDHD_ID", cdhd_id);
			
			
			request.setAttribute("BkView", bkView);	
			request.setAttribute("ListResult", listResult);
			request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
