/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodeRegActn
*   �ۼ���     : (��)�̵������ ������	
*   ����        : ������ �Խ��� ���� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

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
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmCyberBenefitRegActn extends GolfActn  {
	
	public static final String TITLE = "������ ���̹��Ӵ� ���� ��� ó��";
	
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
			//1. �Ķ��Ÿ �� 
			String search_yn	= parser.getParameter("search_yn", "N");					// �˻�����
			String mode			= parser.getParameter("mode", "ins");						// ó������
			String search_clss	= "";
			String search_word	= "";
			if("Y".equals(search_yn)){
				search_clss		= parser.getParameter("search_clss");						// �˻�����
				search_word		= parser.getParameter("search_word");						// �˻���
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ 
			long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�	
			
			String gn_BK		= parser.getParameter("GN_BK", "");	
			String gn_BK_WD		= parser.getParameter("GN_BK_WD", "");	
			String grn_PEE		= parser.getParameter("GRN_PEE", "");	
			String par_BK			= parser.getParameter("PAR_BK", "");	
			String drm		= parser.getParameter("DRM", "");	
			String dv_RG				= parser.getParameter("DV_RG", "");	
			String p_idx		= parser.getParameter("p_idx", "");
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			input.setString("mode",		mode);
			input.setString("GN_BK",		gn_BK);
			input.setString("GN_BK_WD",	gn_BK_WD);
			input.setString("GRN_PEE",		grn_PEE);
			input.setString("PAR_BK",		par_BK);
			input.setString("DRM",		drm);
			input.setString("DV_RG",		dv_RG);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			
			//debug("actc mode:"+mode);
			
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB ó�� 
			GolfAdmCyberBenefitRegDaoProc proc = (GolfAdmCyberBenefitRegDaoProc)context.getProc("GolfAdmCyberBenefitRegDaoProc");
			DbTaoResult benefitInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("benefitInq", benefitInq);						
			
			
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
