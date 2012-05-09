/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComDelActn
*   �ۼ���     : (��)�̵������ ������	
*   ����        : ������ �Խ��� ���� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

import java.io.IOException;
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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComDelAllDaoProc;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfAdmBoardComDelAllActn extends GolfActn  {
	
	public static final String TITLE = "������ �Խ��� ���� ó��";
	
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

		//debug("==== GolfAdmBoardComDelActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			// ResultScriptPag.jsp ���� �迭�� ����� Object ���� ��� ���� �߻�.
			paramMap.remove("cidx");
			
			// Request �� ����
			String[] lsn_seq_no = parser.getParameterValues("cidx", ""); 		// ���� �Ϸù�ȣ
			
			//1. �Ķ��Ÿ �� 
			String search_yn		= parser.getParameter("search_yn", "N");					// �˻�����
			String search_clss	= "";
			String search_word	= "";
			String sdate 			= "";
			String edate 			= "";
			if("Y".equals(search_yn)){
				search_clss			= parser.getParameter("search_clss");						// �˻�����
				search_word			= parser.getParameter("search_word");						// �˻���
			}
			long page_no			= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size			= parser.getLongParameter("page_size", 10L);			// ����������¼�		
				
			String idx				= parser.getParameter("idx", "");	
			String bbrd_clss		= parser.getParameter("boardid", "");
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);	
			
			input.setString("idx",				idx);	
			

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 3. DB ó��  
			int delResult = 0;
			GolfAdmBoardComDelAllDaoProc proc = (GolfAdmBoardComDelAllDaoProc)context.getProc("GolfAdmBoardComDelAllDaoProc");
			if (lsn_seq_no != null && lsn_seq_no.length > 0) {
				delResult = proc.execute(context, dataSet, lsn_seq_no);
			}			
			paramMap.put("BBRD_CLSS", bbrd_clss);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComDelActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
