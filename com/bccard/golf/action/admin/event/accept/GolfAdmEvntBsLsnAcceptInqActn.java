/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmEvntBsLsnAcceptInqActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ > �̺�Ʈ >Ư������ �̺�Ʈ >��÷�ڰԽ��ǰ���
*   �������	: golf
*   �ۼ�����	: 2009-07-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.accept;

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
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnAcceptInqDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBsLsnAcceptInqActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ >Ư������ �̺�Ʈ >��÷�ڰԽ��ǰ���";

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
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss = "0005";

			String search_evnt 	= parser.getParameter("search_evnt","");
			String search_word 	= parser.getParameter("search_word","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_eps 	= parser.getParameter("search_yn","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss", 			evnt_clss);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			dataSet.setString("search_word",		search_word);
			dataSet.setString("search_clss",		search_clss);
			dataSet.setString("search_eps",			search_eps);
			dataSet.setString("search_evnt", 		search_evnt);
			dataSet.setString("evntListMode", 		"Inq");
			dataSet.setLong("page_no", 				page_no);

			
			//�̺�Ʈ ��� ��ȸ
			GolfAdmEvntBsLsnInqDaoProc evnt_proc = (GolfAdmEvntBsLsnInqDaoProc)context.getProc("GolfAdmEvntBsLsnInqDaoProc");
			DbTaoResult evntInq = (DbTaoResult)evnt_proc.execute(context,request,dataSet);
			request.setAttribute("evntInq", evntInq);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBsLsnAcceptInqDaoProc proc = (GolfAdmEvntBsLsnAcceptInqDaoProc)context.getProc("GolfAdmEvntBsLsnAcceptInqDaoProc");
			DbTaoResult boardInq = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("boardInq", boardInq);	
			
			String ttCnt = "0";
			if(boardInq.isNext()){
				boardInq.next();
				ttCnt = boardInq.getString("ttCnt");
			}
			 
			
			//��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("ttCnt", ttCnt);
	        request.setAttribute("paramMap", paramMap); 		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
