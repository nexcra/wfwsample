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
public class GolfAdmBenefitFormRegActn extends GolfActn {

	public static final String TITLE ="ȸ�����ð��� ��� ��";

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
			Map paramMap = parser.getParameterMap();
			String p_idx		= parser.getParameter("p_idx");		// ���
			String bokg_knd		= "";	// ��ŷ�����ڵ�(�����庰 ���ð���)
			debug("p_idx : " + p_idx); 
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			
			if( !"".equals(p_idx) ) {
				input.setString("p_idx", p_idx);
			
				//�Խù� ������ execute
				GolfAdmBenefitDetailInqDaoProc proc1 = (GolfAdmBenefitDetailInqDaoProc)context.getProc("GolfAdmBenefitDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				
				if (detailInq != null ) {
					detailInq.next();
				}

				// �����庰 ���ð���(��3)
				bokg_knd = "0001";	// ��3
				input.setString("bokg_knd", bokg_knd);
				DbTaoResult parResult = (DbTaoResult)proc1.execute_green(context, request, input);			

				request.setAttribute("parResult", parResult);			
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
			}
						
			//ȸ�����ī�װ� ���
			GolfAdmBenefitCategoryInqDaoProc proc2 = (GolfAdmBenefitCategoryInqDaoProc)context.getProc("GolfAdmBenefitCategoryInqDaoProc");
			DbTaoResult categoryListInq = (DbTaoResult)proc2.execute(context, request, input);
			

			
			request.setAttribute("categoryListInq", categoryListInq);
			request.setAttribute("paramMap", paramMap);
			

		} catch(Throwable t) {
			//debug("==== GolfAdmBenefitFormRegActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
	}
}
