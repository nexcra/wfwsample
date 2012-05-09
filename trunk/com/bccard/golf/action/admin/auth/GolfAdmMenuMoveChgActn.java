/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMenuMoveChgActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �޴� �̵� ó��
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
public class GolfAdmMenuMoveChgActn extends GolfActn  {
	
	public static final String TITLE = "�񾾰���  ������   �޴� �̵� ó��";
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

		//debug("==== GolfAdmMenuMoveChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String mode		= parser.getParameter("mode", "");
			String idx			= parser.getParameter("idx", "");	
			String gdx			= parser.getParameter("gdx", "");
			String state			= parser.getParameter("state", "");
			
			
			String str_t_name = "";
			String tmp_mode = "";
			
			
			
			if("SQ3".equals(mode))
			{
				str_t_name = "TBGSQ3MENUINFO";
				tmp_mode = "SQ2";			
			}else if("SQ2".equals(mode))
			{
				str_t_name = "TBGSQ2MENUINFO";
				tmp_mode = "SQ1";
			}else if("SQ1".equals(mode))
			{
				str_t_name = "TBGSQ1MENUINFO";
			}
			
			//2.PROC�� �ѱ�� �� ����
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("str_t_name",	str_t_name);
			input.setString("state",	state);			
			input.setString("tmp_mode",	tmp_mode);
			input.setString("mode",	mode);
			input.setString("idx",	idx);
						
			GolfAdmMenuMoveChgDaoProc proc = (GolfAdmMenuMoveChgDaoProc)context.getProc("GolfAdmMenuMoveChgDaoProc");
			DbTaoResult menuListInq = (DbTaoResult)proc.execute(context, request, input);

			Map paramMap = parser.getParameterMap();	
			request.setAttribute("menuListInq", menuListInq);
			request.setAttribute("gdx", gdx);
			request.setAttribute("idx", idx);
			request.setAttribute("mode", mode);
			request.setAttribute("state", state);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmMenuMoveChgActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}

}
