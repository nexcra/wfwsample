/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthSetProcChgActn
*   �ۼ���    : ������
*   ����      : ������ ���� ���� ó��
*   �������  : Golf
*   �ۼ�����  : 2010-09-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

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
import com.bccard.golf.dbtao.proc.admin.*;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthSetProcChgActn extends GolfActn {
	
	public static final String TITLE = "�񾾰���  �̺�Ʈ ���� ������ ���� ���� �μ� Ƚ�� ����";
	/***************************************************************************************
	* �񾾰��� �����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

		//debug("==== GolfAdmAuthSetProcChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			// 1. �Ķ��Ÿ �� 
			String seq_no	= parser.getParameter("seq_no", "");	
			
			//3. ���� 
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("seq_no",	seq_no);
			
			
			// 3.��ũ ��ũ��Ʈ 
			GolfAdmAuthSetChgDaoProc proc = (GolfAdmAuthSetChgDaoProc)context.getProc("GolfAdmAuthSetChgDaoProc");
			DbTaoResult authListInq = (DbTaoResult)proc.execute(context, request, input);

		
						
			Map paramMap = parser.getParameterMap();	
			request.setAttribute("authListInq", authListInq);
			request.setAttribute("seq_no", seq_no);			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmAuthSetProcChgActn End ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
	

}
