/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmLsnUccDetailActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ >  ���� > UCC ���� �󼼺���
*   �������	: golf
*   �ۼ�����	: 2009-07-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson.ucc;

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
import com.bccard.golf.dbtao.proc.admin.lesson.ucc.GolfAdmLsnUccDetailDaoProc;
import com.bccard.golf.dbtao.proc.admin.lesson.ucc.GolfAdmLsnUccInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmLsnUccDetailActn extends GolfActn{
	
	public static final String TITLE = "������ ���� UCC �󼼺���";

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
		

			// Request �� ����
			String bbrd_clss 	= "0022";
			String idx 			= parser.getParameter("idx","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			String search_answ 	= parser.getParameter("search_answ","");
			String page_no		= parser.getParameter("page_no","1");
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("bbrd_clss",	bbrd_clss);
			dataSet.setString("idx", 		idx);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnUccDetailDaoProc proc = (GolfAdmLsnUccDetailDaoProc)context.getProc("GolfAdmLsnUccDetailDaoProc");
			DbTaoResult lessonDetail = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("lessonDetail", lessonDetail);	
			
			// ��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		
			paramMap.put("title", TITLE);
			paramMap.put("idx",	 		idx);		
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_word", search_word);
			paramMap.put("search_answ", search_answ);
			paramMap.put("page_no", 	page_no);
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
