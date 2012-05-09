/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntSpWinnerDetailActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: �̺�Ʈ����� > Ư���� �����̺�Ʈ > ��÷Ȯ�� �󼼺���
*   �������	: golf
*   �ۼ�����	: 2009-07-09
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.winner;

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
import com.bccard.golf.dbtao.proc.event.special.winner.GolfEvntSpWinnerDetailDaoProc;
import com.bccard.golf.dbtao.proc.event.special.winner.GolfEvntSpWinnerPrzInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/ 
public class GolfEvntSpWinnerDetailActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ����� > Ư���� �����̺�Ʈ > ��÷Ȯ�� �󼼺���";
 
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
			
			String evnt_clss 	= "0003";
			String p_idx 		= parser.getParameter("p_idx","");
			String evnt_seq_no 	= parser.getParameter("evnt_seq_no","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss",	evnt_clss);
			dataSet.setString("p_idx",		p_idx);
			dataSet.setString("evnt_seq_no",evnt_seq_no);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntSpWinnerDetailDaoProc proc = (GolfEvntSpWinnerDetailDaoProc)context.getProc("GolfEvntSpWinnerDetailDaoProc");
			DbTaoResult boardDetail = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("boardDetail", boardDetail);
		 		
			//��÷�� ���
			GolfEvntSpWinnerPrzInqDaoProc  prz_proc = (GolfEvntSpWinnerPrzInqDaoProc)context.getProc("GolfEvntSpWinnerPrzInqDaoProc");
			DbTaoResult przWinInq = (DbTaoResult)prz_proc.execute(context, request,dataSet);
			request.setAttribute("przWinInq", przWinInq);
			
			
			// 05.��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
			paramMap.put("p_idx", 		p_idx);
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_word", search_word);
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
