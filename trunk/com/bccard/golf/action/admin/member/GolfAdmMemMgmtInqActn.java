/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemMgmtInqActn
*   �ۼ���     : (��)�̵������ õ����
*   ����        : ������ ȸ����ް��� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-11-13
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.sql.ResultSet;
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
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmBenefitlnqDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtInqDaoProc;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class GolfAdmMemMgmtInqActn extends GolfActn {
	
	public static final String TITLE = "������ ȸ����ް��� ����Ʈ";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		//debug("==== GolfAdmCodeInqActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String search_yn	= parser.getParameter("search_yn", "N");				// �˻�����
			String search_clss	= "";
			String search_word	= "";

			if("Y".equals(search_yn)){
				search_clss	= parser.getParameter("search_clss");						// �˻�����
				search_word	= parser.getParameter("search_word");						// �˻���
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size		= parser.getLongParameter("page_size", 20L);			// ����������¼�			

			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size); 
			 
			GolfAdmMemMgmtInqDaoProc proc = (GolfAdmMemMgmtInqDaoProc)context.getProc("GolfAdmMemMgmtInqDaoProc");
			DbTaoResult listInq = (DbTaoResult)proc.execute(context, request, input);
			
			Map paramMap = parser.getParameterMap();	
			
			paramMap.put("page_no", String.valueOf(page_no));
			
			request.setAttribute("ListInq", listInq);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeInqActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	} 

}
