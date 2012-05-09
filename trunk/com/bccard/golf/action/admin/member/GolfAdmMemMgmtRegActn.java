/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemMgmtRegActn
*   �ۼ���     : (��)�̵������ õ����	
*   ����        : ������ ȸ����ϰ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-11-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.ArrayList;
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
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmBenefitRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class GolfAdmMemMgmtRegActn extends GolfActn  {
	
	public static final String TITLE = "������ ȸ������ ���� ��� ó��"; 
	
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

		//debug("==== GolfAdmCodeRegActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();
			//1. �Ķ��Ÿ �� 
			String mode				= parser.getParameter("mode", "ins");						// ó������
			long page_no			= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size			= parser.getLongParameter("page_size", 10L);			// ����������¼�	
			
			String p_idx			= parser.getParameter("p_idx", "");
			String cmmn_code		= parser.getParameter("CMMN_CODE", "");
			String cmmn_code_nm		= parser.getParameter("CMMN_CODE_NM", "");
			String expl				= parser.getParameter("EXPL", "");
			String use_yn			= parser.getParameter("USE_YN", "");
			String cdhd_sq1_ctgo 	= parser.getParameter("CDHD_SQ1_CTGO","");
			
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("mode",				mode.trim());
			input.setString("p_idx",			p_idx.trim());
			input.setString("cmmn_code",		cmmn_code.trim());
			input.setString("cmmn_code_nm",		cmmn_code_nm);
			input.setString("expl",				expl);
			input.setString("use_yn",			use_yn.trim());
			input.setString("cdhd_sq1_ctgo",	cdhd_sq1_ctgo.trim());
			
			
			// 3. DB ó�� 
			GolfAdmMemMgmtRegDaoProc proc = (GolfAdmMemMgmtRegDaoProc)context.getProc("GolfAdmMemMgmtRegDaoProc");
			DbTaoResult result = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("result", result);						
					
			paramMap.put("page_no",String.valueOf(page_no));
			paramMap.put("page_size",String.valueOf(page_size));
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeRegActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
