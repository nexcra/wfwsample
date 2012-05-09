/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmEvntSpApplicantInqActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ > �̺�Ʈ >Ư������ �̺�Ʈ >��û���� ���
*   �������	: golf
*   �ۼ�����	: 2009-07-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.applicant;

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
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnInqDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.applicant.GolfAdmEvntSpApplicantInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntSpApplicantInqActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ >Ư������ �̺�Ʈ >��û���� ���";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String ttCnt = ""; 
		 
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
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss = "0005";
 
			String search_evnt 		= parser.getParameter("search_evnt","");
			String search_word 		= parser.getParameter("search_word","");
			String search_clss 		= parser.getParameter("search_clss","");
			String search_status 	= parser.getParameter("search_status","");
			String search_przwin 	= parser.getParameter("search_przwin","");
			//debug("---------------------------------- action search_evnt  : "+search_evnt);
			
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ


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
			 
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss", 			evnt_clss);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			dataSet.setString("search_evnt",		search_evnt);
			dataSet.setString("search_word",		search_word);
			dataSet.setString("search_clss",		search_clss);
			dataSet.setString("search_status", 		search_status);
			dataSet.setString("search_przwin", 		search_przwin);
			dataSet.setString("search_sdate", 		sch_date_st);
			dataSet.setString("search_edate", 		sch_date_ed);
			dataSet.setString("evntListMode", 		"Inq");
			dataSet.setLong("page_no", 				page_no);

			
			//�̺�Ʈ ��� ��ȸ
			GolfAdmEvntBsLsnInqDaoProc evnt_proc = (GolfAdmEvntBsLsnInqDaoProc)context.getProc("GolfAdmEvntBsLsnInqDaoProc");
			DbTaoResult evntInq = (DbTaoResult)evnt_proc.execute(context,request,dataSet);
			request.setAttribute("evntInq", evntInq);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntSpApplicantInqDaoProc proc = (GolfAdmEvntSpApplicantInqDaoProc)context.getProc("GolfAdmEvntSpApplicantInqDaoProc");
			DbTaoResult boardInq = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("boardInq", boardInq);	
			if(boardInq.isNext()){
				boardInq.next();
				ttCnt = boardInq.getString("ttCnt");
			}
						
			
			//��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("ttCnt", ttCnt);
			paramMap.put("search_evnt",		search_evnt);
			paramMap.put("search_word",		search_word);
			paramMap.put("search_clss",		search_clss);
			paramMap.put("search_status", 	search_status);
			paramMap.put("search_przwin", 	search_przwin);
			paramMap.put("page_no", 		Long.toString(page_no));
			
			
	        request.setAttribute("paramMap", paramMap); 		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
