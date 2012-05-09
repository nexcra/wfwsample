/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointAdmMenuNoChklnqActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �޴� �ߺ� üũ
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
public class GolfAdmMenuNoChklnqActn extends GolfActn  {
	public static final String TITLE = "�񾾰���  ������  �޴� �ߺ� üũ";
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

		//debug("==== GolfAdmMenuNoChklnqActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String idx	= parser.getParameter("idx", "");	
			String mode	= parser.getParameter("mode", "");
			
			String str_table_name = "";
			String str_idx_col_name = "";
			String str_idx_cnt = "0";
			
			if("m0".equals(mode))
			{
				str_table_name = "TBGSQ1MENUINFO";
				str_idx_col_name = "SQ1_LEV_SEQ_NO";
			}
			else if("m1".equals(mode))
			{
				str_table_name = "TBGSQ2MENUINFO";
				str_idx_col_name = "SQ2_LEV_SEQ_NO";
			}
			else if("m1".equals(mode))
			{
				str_table_name = "TBGSQ3MENUINFO";
				str_idx_col_name = "SQ3_LEV_SEQ_NO";
			}
					
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("str_table_name",	str_table_name);
			input.setString("str_idx_col_name",	str_idx_col_name);
			input.setString("idx",	idx);
			
			GolfAdmMenuCheckInqDaoProc proc = (GolfAdmMenuCheckInqDaoProc)context.getProc("GolfAdmMenuCheckInqDaoProc");
			DbTaoResult menuListInq = (DbTaoResult)proc.execute(context, request, input);

			if(menuListInq != null && menuListInq.isNext() ) {
				menuListInq.next();
				str_idx_cnt = menuListInq.getString("int_idx_cnt");
		
			} 
			
			Map paramMap = parser.getParameterMap();	
			request.setAttribute("menuListInq", menuListInq);
			request.setAttribute("str_idx_cnt", str_idx_cnt);	
			request.setAttribute("idx", idx);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmMenuNoChklnqActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}

}
