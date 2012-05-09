/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmLsnUccInqActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ >  ���� > UCC ���� ���
*   �������	: golf
*   �ۼ�����	: 2009-07-02
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
public class GolfAdmLsnUccInqActn extends GolfActn{
	
	public static final String TITLE = "������ ���� UCC �����ȸ";

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
			String bbrd_clss 	= "0022";
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			String search_answ 	= parser.getParameter("search_answ","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("bbrd_clss", 		bbrd_clss);
			dataSet.setString("search_clss", 	search_clss);
			dataSet.setString("search_word", 	search_word);
			dataSet.setString("search_answ", 	search_answ);
			dataSet.setLong("page_no", 			page_no);
			dataSet.setLong("record_size", 		record_size);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnUccInqDaoProc proc = (GolfAdmLsnUccInqDaoProc)context.getProc("GolfAdmLsnUccInqDaoProc");
			DbTaoResult lessonInq = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("lessonInq", lessonInq);	
			
			//�ѰԽù��� ����
			String ttCnt = proc.getTtCount(context, dataSet);
			
			request.setAttribute("record_size", String.valueOf(record_size));
			paramMap.put("ttCnt", ttCnt);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
