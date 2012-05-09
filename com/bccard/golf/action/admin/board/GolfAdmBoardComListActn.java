/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComListActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComListDaoProc;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfAdmBoardComListActn extends GolfActn {
	
	public static final String TITLE = "������ �Խ��� ����Ʈ";
	
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

		//debug("==== GolfAdmBoardComListActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();	
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
			
			String boardid = parser.getParameter("boardid", "");
			
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
			input.setString("boardid", 		boardid);

			GolfAdmBoardComListDaoProc proc = (GolfAdmBoardComListDaoProc)context.getProc("GolfAdmBoardComListDaoProc");
			DbTaoResult boardListInq = (DbTaoResult)proc.execute(context, request, input);
			request.setAttribute("boardListInq", boardListInq);
			
			boardListInq.next();
			String result = boardListInq.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", boardListInq.getString("total_cnt"));
			else
				request.setAttribute("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(boardListInq.size()));
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComListActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}