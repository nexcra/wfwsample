/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBsExcelActn
*   �ۼ���    : (��)�̵������ õ����
*   ����      : ������ BC Golf Ư������ �̺�Ʈ �����ٿ�ε�
*   �������  : Golf
*   �ۼ�����  : 2009-07-20
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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBsExcelDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmBsExcelActn extends GolfActn{
	
	public static final String TITLE = "������ BC Golf Ư������ �̺�Ʈ ����Ʈ";

	/**************************************************************************************
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
		String subject = "";
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// Request �� ����
			String mode 			= parser.getParameter("mode","");
			String p_idx 			= parser.getParameter("p_idx","");

			String search_evnt		= parser.getParameter("search_evnt", "");
			String search_status	= parser.getParameter("search_status", "");
			String search_przwin	= parser.getParameter("search_przwin", "");
			String search_clss		= parser.getParameter("search_clss", "");
			String search_word		= parser.getParameter("search_word", "");
			String search_sdate 	= parser.getParameter("search_sdate", "");
			String search_edate 	= parser.getParameter("search_edate", "");
			String search_yn 		= parser.getParameter("search_yn", "");
			String search_grade 	= parser.getParameter("search_grade", "");
			String search_sex 		= parser.getParameter("search_sex", "");
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("mode", 			mode);
			dataSet.setString("p_idx", 			p_idx);
			dataSet.setString("search_evnt", 	search_evnt);
			dataSet.setString("search_status", 	search_status);
			dataSet.setString("search_przwin", 	search_przwin);
			dataSet.setString("search_clss", 	search_clss);
			dataSet.setString("search_word", 	search_word);
			dataSet.setString("search_sdate", 	search_sdate);
			dataSet.setString("search_edate", 	search_edate);
			dataSet.setString("search_yn", 		search_yn); 
			dataSet.setString("search_grade", 	search_grade); 
			dataSet.setString("search_sex", 	search_sex); 
			
			
			//TITLE ����
			
			if("EvntListExcel".equals(mode)){
				subject = "Ư������ �̺�Ʈ ���";
			}else if("SpApplicantListExcel".equals(mode)){
				subject = "Ư������ �̺�Ʈ  ��û���� ���";
			}else if("SpSettleListExcel".equals(mode)){
				subject = "Ư������ �̺�Ʈ  �������� ���";
			}
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBsExcelDaoProc proc = (GolfAdmEvntBsExcelDaoProc)context.getProc("GolfAdmEvntBsExcelDaoProc");
			DbTaoResult boardInqList = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("boardInqList", boardInqList);
		 	
			
			paramMap.put("subject", subject);
			paramMap.put("mode",	mode);
	        request.setAttribute("paramMap", paramMap);
	       
	         
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
