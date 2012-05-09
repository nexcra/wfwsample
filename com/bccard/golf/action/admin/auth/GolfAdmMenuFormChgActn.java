/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMenuFormChgActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �޴� ����
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
public class GolfAdmMenuFormChgActn extends GolfActn  {
	
	public static final String TITLE = "�񾾰���  ������  �޴� ���� ��";
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

		//debug("==== GolfAdmMenuFormChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String m	= parser.getParameter("m", "");
			String idx	= parser.getParameter("idx", "");	
			String gdx	= parser.getParameter("gdx", "");			
			
			String strTitle = "";
			String str_table_name = "";
			String str_idx_col_name = "";
			String str_col_name = "";
			
			
			if("m0".equals(m))
			{
				strTitle = "��޴�";
				str_table_name 		= "TBGSQ1MENUINFO";
				str_idx_col_name 	= "SQ1_LEV_SEQ_NO";
				str_col_name 		= "SQ1_LEV_MENU_NM";
			}else if("m1".equals(m))
			{
				strTitle 			= "�߸޴�";
				str_table_name 		= "TBGSQ2MENUINFO";
				str_idx_col_name 	= "SQ2_LEV_SEQ_NO";
				str_col_name 		= "SQ2_LEV_MENU_NM";
			}else if("m2".equals(m))
			{
				strTitle 			= "�Ҹ޴�";
				str_table_name	 	= "TBGSQ3MENUINFO";
				str_idx_col_name 	= "SQ3_LEV_SEQ_NO";
				str_col_name 		= "SQ3_LEV_MENU_NM";
			}
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("str_table_name",	str_table_name);
			input.setString("str_idx_col_name",	str_idx_col_name);
			input.setString("str_col_name",	str_col_name);
			input.setString("mode",	m);
			input.setString("idx",	idx);
			
			GolfAdmMenuFormChgDaoProc proc = (GolfAdmMenuFormChgDaoProc)context.getProc("GolfAdmMenuFormChgDaoProc");
			DbTaoResult menuListInq = (DbTaoResult)proc.execute(context, request, input);

			Map paramMap = parser.getParameterMap();	
			request.setAttribute("menuListInq", menuListInq);
			request.setAttribute("gdx", gdx);
			request.setAttribute("idx", idx);
			request.setAttribute("m", m);
			request.setAttribute("strTitle", strTitle);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmMenuFormChgActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
