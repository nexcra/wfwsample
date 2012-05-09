/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthPassUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��� ��й�ȣ ���� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;
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
public class GolfAdmAuthPassUpdActn extends GolfActn  {
	
	public static final String TITLE = "�񾾰���  ������ ��й�ȣ ���� ó��";
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

		//debug("==== GolfAdmAuthPassUpdActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String p_idx	= parser.getParameter("p_idx", "");
			String p_passwd	= parser.getParameter("p_passwd", "");	
			
			//2.PROC�� �ѱ�� �� ����
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("p_passwd",	p_passwd);
			input.setString("p_idx",	p_idx);
			
			GolfAdmAuthPassUpdDaoProc proc = (GolfAdmAuthPassUpdDaoProc)context.getProc("GolfAdmAuthPassUpdDaoProc");
			DbTaoResult passwdUpd = (DbTaoResult)proc.execute(context, request, input);

			Map paramMap = parser.getParameterMap();	
			request.setAttribute("passwdUpd", passwdUpd);
			
			request.setAttribute("p_idx", p_idx);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmAuthPassUpdActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}

}
