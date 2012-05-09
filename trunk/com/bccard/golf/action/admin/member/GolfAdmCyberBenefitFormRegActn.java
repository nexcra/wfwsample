/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBenefitFormRegActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ ȸ������ ���� ��� ��
*   �������  : Golf
*   �ۼ�����  : 2009-05-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 
import java.util.Map;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmCyberBenefitFormRegActn extends GolfActn {

	public static final String TITLE ="���̹��Ӵ� ���� ��� ��";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext ��ü.
	* @param request		HttpServletRequest ��ü.
	* @param response		HttpServletResponse ��ü.
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		 
		DbTaoConnection con = null;

		try{
			//debug("==== GolfAdmBenefitFormRegActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);
			String p_idx		= parser.getParameter("p_idx");	
			
		//	if( !"".equals(p_idx) ) {
				//�Խù� ������
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			if( !"".equals(p_idx) ) {
				input.setString("p_idx", p_idx);
			}
	
			//�Խù� ������ execute
			GolfAdmCyberBenefitDetailInqDaoProc proc1 = (GolfAdmCyberBenefitDetailInqDaoProc)context.getProc("GolfAdmCyberBenefitDetailInqDaoProc");
			DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
			if (detailInq != null ) {
				detailInq.next();
			}
				
			request.setAttribute("detailInq", detailInq);
			request.setAttribute("p_idx", p_idx);
		//	}
			
			Map paramMap = parser.getParameterMap();
			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBenefitFormRegActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmBenefitFormRegActn Error ===");
			
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
	}
}
