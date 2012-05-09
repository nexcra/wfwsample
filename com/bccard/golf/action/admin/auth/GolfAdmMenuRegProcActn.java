/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointAdmMenuRegProcActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �޴� ��� ó��
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

public class GolfAdmMenuRegProcActn extends GolfActn   {
	public static final String TITLE = "�񾾰���  ������  �޴� ��� ó��";
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

		//debug("==== GolfAdmMenuRegProcActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String mode		= parser.getParameter("mode", "");
			String pidx		= parser.getParameter("pidx", "");	
			String idx		= parser.getParameter("idx", "");
			String gdx		= parser.getParameter("gdx", "");	
			String str_name	= parser.getParameter("str_name", "");
			String str_url	= parser.getParameter("str_url", "");
			
			
			String strTitle = "";
			String str_table_name = "";
			String str_idx_col_name = "";
			String str_col_name = "";
			String str_ord_name = "";
			String str_pidx_col_name = "";
			String str_where = "";
			
			if("m0".equals(mode))
			{
				strTitle = "��޴�";
				str_table_name = "TBGSQ1MENUINFO";
				str_idx_col_name = "SQ1_LEV_SEQ_NO";
				str_col_name = "SQ1_LEV_MENU_NM";
				str_ord_name = "EPS_SEQ";				
			}else if("m1".equals(mode))
			{
				strTitle = "�߸޴�";
				str_table_name = "TBGSQ2MENUINFO";
				str_idx_col_name = "SQ2_LEV_SEQ_NO";
				str_col_name = "SQ2_LEV_MENU_NM";
				str_pidx_col_name = "SQ1_LEV_SEQ_NO";
				str_ord_name = "EPS_SEQ";
				str_where = " WHERE SQ1_LEV_SEQ_NO = "+pidx;
			}else if("m2".equals(mode))
			{
				strTitle = "�Ҹ޴�";
				str_table_name = "TBGSQ3MENUINFO";
				str_idx_col_name = "SQ3_LEV_SEQ_NO";
				str_col_name = "SQ3_LEV_MENU_NM";
				str_pidx_col_name = "SQ2_LEV_SEQ_NO";
				str_ord_name = "EPS_SEQ";
				str_where = " WHERE SQ2_LEV_SEQ_NO = "+pidx;
			}
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("str_table_name",		str_table_name);
			input.setString("str_ord_name",			str_ord_name);
			input.setString("str_where",			str_where);
			input.setString("str_idx_col_name",		str_idx_col_name);
			input.setString("str_pidx_col_name",	str_pidx_col_name);
			input.setString("str_col_name",			str_col_name);
			
			input.setString("idx",					idx);
			input.setString("str_name",				str_name);
			input.setString("str_url",				str_url);
			input.setString("pidx",					pidx);
			
			input.setString("mode",					mode);
			
			//��� ó��
			GolfAdmMenuProcRegDaoProc proc = (GolfAdmMenuProcRegDaoProc)context.getProc("GolfAdmMenuProcRegDaoProc");
			DbTaoResult menuListInq = (DbTaoResult)proc.execute(context, request, input);
			
			Map paramMap = parser.getParameterMap();	
			request.setAttribute("menuListInq", menuListInq);
			request.setAttribute("gdx", gdx);
			request.setAttribute("pidx", pidx);
			request.setAttribute("idx", idx);
			request.setAttribute("mode", mode);
			request.setAttribute("strTitle", strTitle);
			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmMenuRegProcActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}

}
