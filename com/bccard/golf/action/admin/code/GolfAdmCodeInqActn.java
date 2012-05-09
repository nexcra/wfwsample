/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodeInqActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �����ڵ���� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.code;

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
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodelnqDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCategorySubInqDaoProc;

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
public class GolfAdmCodeInqActn extends GolfActn {
	
	public static final String TITLE = "������ �����ڵ���� ����Ʈ";
	
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

		//debug("==== GolfAdmCodeInqActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String search_yn	= parser.getParameter("search_yn", "N");				// �˻�����
			String search_clss	= "";
			String search_word	= "";
			String search_cate_seq	= "";

			if("Y".equals(search_yn)){
				search_clss	= parser.getParameter("search_clss");						// �˻�����
				search_word	= parser.getParameter("search_word");						// �˻���
				search_cate_seq	= parser.getParameter("search_cate_seq");
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�			

			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
				input.setString("search_cate_seq",	search_cate_seq);
			}
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			 
			GolfAdmCodelnqDaoProc proc = (GolfAdmCodelnqDaoProc)context.getProc("GolfAdmCodelnqDaoProc");
			DbTaoResult codeListInq = (DbTaoResult)proc.execute(context, request, input);
			
			//ī�װ� ���
			GolfAdmCategorySubInqDaoProc proc2 = (GolfAdmCategorySubInqDaoProc)context.getProc("GolfAdmCategorySubInqDaoProc");
			DbTaoResult categoryListInq = (DbTaoResult)proc2.execute(context, request, input);
			request.setAttribute("categoryListInq", categoryListInq);	

			Map paramMap = parser.getParameterMap();	
			paramMap.put("search_cate_seq",			search_cate_seq);
			
			request.setAttribute("codeListInq", codeListInq);
			request.setAttribute("categoryListInq", categoryListInq);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeInqActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
