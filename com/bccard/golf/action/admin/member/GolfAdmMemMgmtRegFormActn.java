/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemMgmtRegFormActn
*   �ۼ���     : (��)�̵������ õ����
*   ����        : ������ ȸ�� ��ް��� �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2009-11-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtDetailDaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31
 **************************************************************************** */

public class GolfAdmMemMgmtRegFormActn extends GolfActn  {
	
	public static final String TITLE = "������ ȸ�� ��ް��� �󼼺���";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 

			String p_idx		= parser.getParameter("p_idx", "");						//	�Խù���ȣ
			long page_no		= parser.getLongParameter("page_no", 1L);				// 	��������ȣ 
			long page_size		= parser.getLongParameter("page_size", 10L);			// 	����������¼�	
			
			
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("p_idx",		p_idx);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			
			
			// �� ����
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB ó�� 
			GolfAdmMemMgmtDetailDaoProc proc = (GolfAdmMemMgmtDetailDaoProc)context.getProc("GolfAdmMemMgmtDetailDaoProc");
			DbTaoResult detailInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("DetailInq", detailInq);						
			 
			paramMap.put("p_idx", p_idx);
			paramMap.put("page_no", String.valueOf(page_no));
			paramMap.put("page_size", String.valueOf(page_size));
			
			request.setAttribute("paramMap", paramMap);
			
			
		}catch(Throwable t) {
			debug("==== GolfAdmMemMgmtRegFormActn Error ===");
			return errorHandler(context,request,response,t);
		
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
