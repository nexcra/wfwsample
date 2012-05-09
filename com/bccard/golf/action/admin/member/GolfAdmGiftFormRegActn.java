/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGiftFormRegActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ ����ǰ���� ��� �� 
*   �������  : Golf
*   �ۼ�����  : 2009-08-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

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
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmGiftFormRegActn extends GolfActn {

	public static final String TITLE ="����ǰ���� ��� ��";

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
			debug("==== GolfAdmGiftFormRegActn start ===");
			
			RequestParser parser = context.getRequestParser("default", request, response);
			String p_idx		= parser.getParameter("p_idx");
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			if( !"".equals(p_idx)) {
				//�Խù� ������
				input.setString("p_idx", p_idx);
	
				//�Խù� ������ execute
				
				GolfAdmGiftDtlInqDaoProc proc1 = (GolfAdmGiftDtlInqDaoProc)context.getProc("GolfAdmGiftDtlInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
				
			}
			//debug("==== GolfAdmGiftFormRegActn 111111111111111111111 ===");
			//ī�װ� ���
			GolfGiftCategoryInqDaoProc proc2 = (GolfGiftCategoryInqDaoProc)context.getProc("GolfGiftCategoryInqDaoProc");
			DbTaoResult categoryListInq = (DbTaoResult)proc2.execute(context, request, input);
			//request.setAttribute("categoryListInq", categoryListInq);
			//debug("==== GolfAdmGiftFormRegActn 2222222222222222222222 ===");
			Map paramMap = parser.getParameterMap();
			request.setAttribute("categoryListInq", categoryListInq);
			request.setAttribute("paramMap", paramMap);
			
			debug("==== GolfAdmGiftFormRegActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmCodeFormRegActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
	}
}
