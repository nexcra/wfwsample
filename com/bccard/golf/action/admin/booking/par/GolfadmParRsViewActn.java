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
package com.bccard.golf.action.admin.booking.par;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.par.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmParRsViewActn extends GolfActn{
	
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
			 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String rsvt_sql_no		= parser.getParameter("RSVT_SQL_NO", "");
			String cdhd_id 			= parser.getParameter("CDHD_ID","");
			String sort 			= parser.getParameter("sort","");
			
			int int_able = 0;
			int int_done = 0;
			int int_can = 0;
			String memGrade = "";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO"	,rsvt_sql_no);
			dataSet.setString("CDHD_ID"		,cdhd_id);
			debug("PAR3  >>>>>>>>>>  CDHD_ID :"+cdhd_id);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmParRsViewDaoProc proc = (GolfadmParRsViewDaoProc)context.getProc("GolfadmParRsViewDaoProc");
			DbTaoResult bkView = proc.execute(context, dataSet);
						
			// 04-1. ��� Grade ��ȸ
			GolfAdmBkBenefitTimesDaoProc benefit_proc = (GolfAdmBkBenefitTimesDaoProc)context.getProc("GolfAdmBkBenefitTimesDaoProc");
			DbTaoResult bkBenefit = benefit_proc.getParBenefit(context, dataSet);
			
			if(bkBenefit.isNext()){
				bkBenefit.next();
				int_able = bkBenefit.getInt("PAR_3_YR_ABLE");
				int_done = bkBenefit.getInt("PAR_3_YR_DONE");
				memGrade = bkBenefit.getString("MEMGRADE");
			}
			
			int_can = int_able - int_done ;
			debug(">>>>>>  int_able : "+int_able+" :: int_done : "+int_done+" :: int_can : "+int_can +" :: memGrade : "+memGrade);
			
			paramMap.put("TOT_CNT", Integer.toString(int_able));
			paramMap.put("DONE_CNT", Integer.toString(int_done));
			paramMap.put("CAN_CNT", Integer.toString(int_can));
			paramMap.put("MEMGRADE", memGrade);
			
			
			// 04-2. �ϴ� ����Ʈ ��ȸ
			dataSet.setLong("page_no", 1L);
			dataSet.setLong("record_size", 10L);
			dataSet.setString("SCH_GR_SEQ_NO", "");
			dataSet.setString("SCH_RSVT_YN", "");
			dataSet.setString("SCH_DATE", "");
			dataSet.setString("SCH_DATE_ST", "");
			dataSet.setString("SCH_DATE_ED", "");
			dataSet.setString("SEARCH_YN","Y");
			dataSet.setString("SCH_TEXT", cdhd_id);
			dataSet.setString("SCH_TYPE", "T1.CDHD_ID");
			dataSet.setString("SORT", sort);
			dataSet.setString("LISTTYPE", "XLS");
			
			
			GolfadmParRsListDaoProc inner_proc = (GolfadmParRsListDaoProc)context.getProc("GolfadmParRsListDaoProc");
			DbTaoResult listResult = (DbTaoResult) inner_proc.execute(context, request, dataSet);
			
			 
			// 05. Return �� ����		
			paramMap.put("CDHD_ID", cdhd_id); //ID
			
			request.setAttribute("BkView", bkView);	//���������
			request.setAttribute("ListResult", listResult); //�ϴ� ���
			request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
