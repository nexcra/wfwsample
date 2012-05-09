/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardFormUpdActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : �Խ��� ��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.board;

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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardDetailInqDaoProc;
import com.bccard.golf.dbtao.proc.board.*;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBoardFormUpdActn extends GolfActn {

	
	public static final String TITLE = "�Խ��� ��";
	
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

		//debug("==== GolfBoardFormUpdActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			
			//1. �Ķ��Ÿ �� 
			String p_idx				= parser.getParameter("p_idx");								// �� �Ϸù�ȣ
			String boardid				= parser.getParameter("boardid");								// �Խ��� ��ȣ
			String mode					= parser.getParameter("mode");								// ���� ( view )
			String type					= parser.getParameter("type");
			
			String search_yn			= parser.getParameter("search_yn", "N");					// �˻�����
			String search_clss			= "";
			String search_word			= "";
			String sdate 				= "";
			String edate 				= "";
			
			if("Y".equals(search_yn))
			{
				search_clss				= parser.getParameter("search_clss");					// �˻�����
				search_word				= parser.getParameter("search_word");					// �˻���
				sdate 					= parser.getParameter("sdate");							// �˻����۳�¥
				edate 					= parser.getParameter("edate");
			}
	
			long page_no				= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size				= parser.getLongParameter("page_size", 10L);			// ����������¼�		
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			
			if( !"".equals(p_idx) && !"".equals(boardid) ) {
				
				//�Խù� ������
				input.setString("p_idx", p_idx);
				input.setString("boardid", boardid);
				input.setString("mode", mode);
	
				//�Խù� ������ execute
				GolfBoardDetailInqDaoProc proc1 = (GolfBoardDetailInqDaoProc)context.getProc("GolfBoardDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
				request.setAttribute("boardid", boardid);
			}

			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfBoardFormUpdActn end ===");
			
		}catch(Throwable t) {
			//debug("==== GolfBoardFormUpdActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);			
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}	
	
}
