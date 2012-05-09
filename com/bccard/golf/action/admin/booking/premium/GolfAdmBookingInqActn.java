/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardInqActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��� ���� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardlnqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmBookingInqActn extends GolfActn {
	
	public static final String TITLE = "������ �Խ��� ���� ����Ʈ";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü.   
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		ResultException rx;

		//debug("==== GolfAdmBoardInqActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String search_yn	= parser.getParameter("search_yn", "N");				// �˻�����
			String search_clss	= "";
			String search_word	= "";
			String sdate 		= "";
			String edate 		= "";
			if("Y".equals(search_yn)){
				search_clss	= parser.getParameter("search_clss");						// �˻�����
				search_word	= parser.getParameter("search_word");						// �˻���
				sdate 		= parser.getParameter("sdate");						// �˻����۳�¥
				edate 		= parser.getParameter("edate");
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�			

			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
				input.setString("sdate",	sdate);
				input.setString("edate",	edate);
			}
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			 
			GolfAdmBoardlnqDaoProc proc = (GolfAdmBoardlnqDaoProc)context.getProc("GolfAdmBoardlnqDaoProc");
			DbTaoResult boardListInq = (DbTaoResult)proc.execute(context, request, input);

			Map paramMap = parser.getParameterMap();	
			request.setAttribute("boardListInq", boardListInq);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardInqActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
