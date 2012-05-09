/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComUpdFormActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��� ���� ���� ��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 
import java.util.Map;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.*;

/******************************************************************************
* Golf
* @author	(��)�̵������   
* @version	1.0
******************************************************************************/
public class GolfAdmBoardComUpdFormActn extends GolfActn {

	public static final String TITLE ="������ �Խ��� ���� ���� ��";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext ��ü.
	* @param request		HttpServletRequest ��ü.
	* @param response		HttpServletResponse ��ü.
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		 
		DbTaoConnection con = null;

		try{
			//debug("==== GolfAdmBoardComUpdFormActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);
			String idx		= parser.getParameter("idx");	
			String boardid	= parser.getParameter("boardid");	
			String boardCode = "";
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			
			if( !"".equals(idx) ) {
				
				input.setString("idx", idx);

				//�Խù� ������ execute
				GolfAdmBoardComUpdFormDaoProc proc = (GolfAdmBoardComUpdFormDaoProc)context.getProc("GolfAdmBoardComUpdFormDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc.execute(context, request, input);

				if (detailInq != null ) {
					detailInq.next();
				}

				request.setAttribute("detailInq", detailInq);
				request.setAttribute("idx", idx);
			}

			if(boardid.equals("0020")){
				
				// �ڵ尪 ����
				boardCode = "0018";
				input.setString("boardCode", 		boardCode);
				
				GolfAdmBoardComSelectListDaoProc proc = (GolfAdmBoardComSelectListDaoProc)context.getProc("GolfAdmBoardComSelectListDaoProc");
				DbTaoResult boardSelectListInq = (DbTaoResult)proc.execute(context, request, input);
				request.setAttribute("boardSelectListInq", boardSelectListInq);
			}
						
			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComUpdFormActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmBoardComUpdFormActn Error ===");
			
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
	}
}
