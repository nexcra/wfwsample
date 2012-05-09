/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmEvntBsLsnAcceptDetailActn
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
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnAcceptDetailDaoProc;
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
public class GolfAdmEvntBsLsnAcceptDetailActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ >Ư������ �̺�Ʈ >��÷�ڰԽ��ǰ��� ó��";

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
			String p_idx 		= parser.getParameter("p_idx","");
			String mode 		= parser.getParameter("mode","");
			String bltn_yn 		= parser.getParameter("bltn_yn","");
			String search_evnt 	= parser.getParameter("search_evnt","");
			String search_word 	= parser.getParameter("search_word","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_eps 	= parser.getParameter("search_eps","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss", 			evnt_clss);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			dataSet.setString("p_idx",				p_idx);
			dataSet.setString("mode",				"RegFormInq");
			dataSet.setString("bltn_yn",			bltn_yn);
			dataSet.setString("search_word",		search_word);
			dataSet.setString("search_clss",		search_clss);
			dataSet.setString("search_eps",			search_eps);
			//dataSet.setString("search_evnt", 		search_evnt);
			dataSet.setString("evntListMode", 		"RegFormInq");
			dataSet.setLong("page_no", 				page_no);
			
			//�̺�Ʈ ��� ��ȸ
			GolfAdmEvntBsLsnInqDaoProc evnt_proc = (GolfAdmEvntBsLsnInqDaoProc)context.getProc("GolfAdmEvntBsLsnInqDaoProc");
			DbTaoResult evntInq = (DbTaoResult)evnt_proc.execute(context,request,dataSet);
			request.setAttribute("evntInq", evntInq);
			
		
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBsLsnAcceptDetailDaoProc proc = (GolfAdmEvntBsLsnAcceptDetailDaoProc)context.getProc("GolfAdmEvntBsLsnAcceptDetailDaoProc");
			DbTaoResult boardDetail = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("boardDetail", boardDetail);	
		
			if(boardDetail.isNext()){
				boardDetail.next(); 
				if("00".equals(boardDetail.getString("RESULT"))){
					if("".equals(search_evnt) || "A".equals(search_evnt)){
						search_evnt = boardDetail.getString("evnt_seq_no");
					} 
				}
			}
			
			//if("A".equals(search_evnt)) search_evnt = "";
			
			//��÷�� ���
			dataSet.setString("mode",		 "PrzWinList");
			dataSet.setString("search_evnt", search_evnt);
			
			DbTaoResult przInq = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("przInq", przInq);
			
			
			//��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("p_idx",			p_idx);
			/*
			paramMap.put("search_word", search_word);
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_eps",  search_eps);
			*/
			paramMap.put("search_evnt", search_evnt);
			
			paramMap.put("page_no", 	Long.toString(page_no));
	        request.setAttribute("paramMap", paramMap); 		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
