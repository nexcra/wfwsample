/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkNotiViewActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : �������׺���
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.guide;

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
import com.bccard.golf.dbtao.proc.booking.guide.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBkNotiViewActn extends GolfActn {

	public static final String TITLE ="�������׺���";

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
			String boardid		= parser.getParameter("boardid");	
			
			if( !"".equals(idx) ) {
				
				DbTaoDataSet input = new DbTaoDataSet(TITLE);
				input.setString("idx", idx);

				//�Խù� ������ execute
				GolfBkNotiViewDaoProc proc = (GolfBkNotiViewDaoProc)context.getProc("GolfBkNotiViewDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc.execute(context, request, input);

				if (detailInq != null ) {
					detailInq.next();
				}

				request.setAttribute("detailInq", detailInq);
				request.setAttribute("idx", idx);
			}
						
			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComUpdFormActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmBoardComUpdFormActn Error ===");
			
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
	}
}
