/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100513	������	���ᰡ���� ���� �߰�, ���������� TMȸ���� TMȸ�� ���̺� ���ó�� ������Ʈ
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.member.*;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmMemUpdActn extends GolfActn{

	public static final String TITLE = "������ ȸ������ ����";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {

			// ȸ���������̺� ���� �������� ����
			// 02.�Է°� ��ȸ�Ѵ�.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// ��������
			String bokg_LIMT_YN	= parser.getParameter("BOKG_LIMT_YN", "").trim();
			String bokg_LIMT_FIXN_STRT_DATE	= parser.getParameter("BOKG_LIMT_FIXN_STRT_DATE", "").trim();
			String bokg_LIMT_FIXN_END_DATE	= parser.getParameter("BOKG_LIMT_FIXN_END_DATE", "").trim();
			String cdhd_ID	= parser.getParameter("CDHD_ID", "").trim();
			
			bokg_LIMT_FIXN_STRT_DATE = GolfUtil.replace(bokg_LIMT_FIXN_STRT_DATE, "-", "");
			bokg_LIMT_FIXN_END_DATE = GolfUtil.replace(bokg_LIMT_FIXN_END_DATE, "-", "");

			String mod	= parser.getParameter("MOD", "").trim();	// ������Ʈ ���а� : grade => ��� ������Ʈ
			String grade_old	= parser.getParameter("GRADE_OLD", "").trim();	// ���� ���
			String grade_new	= parser.getParameter("GRADE_NEW", "").trim();	// ���� ���
			String payBack		= parser.getParameter("payBack", "N");	// ��ȸ�� ȯ�޿���

			String jumin_no = parser.getParameter("JUMIN_NO", "");
			String acrg_cdhd_join_date = parser.getParameter("ACRG_CDHD_JONN_DATE", "");
			String acrg_cdhd_end_date = parser.getParameter("ACRG_CDHD_END_DATE", "");
			acrg_cdhd_join_date = GolfUtil.replace(acrg_cdhd_join_date, "-", "");
			acrg_cdhd_end_date = GolfUtil.replace(acrg_cdhd_end_date, "-", "");
			
	        int editResult = 0;
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("BOKG_LIMT_YN", bokg_LIMT_YN);
			dataSet.setString("BOKG_LIMT_FIXN_STRT_DATE", bokg_LIMT_FIXN_STRT_DATE);
			dataSet.setString("BOKG_LIMT_FIXN_END_DATE", bokg_LIMT_FIXN_END_DATE);
			dataSet.setString("CDHD_ID", cdhd_ID);
			dataSet.setString("GRADE_OLD", grade_old);
			dataSet.setString("GRADE_NEW", grade_new);
			dataSet.setString("payBack", payBack);
			
			dataSet.setString("JUMIN_NO", jumin_no);
			dataSet.setString("ACRG_CDHD_JONN_DATE", acrg_cdhd_join_date);
			dataSet.setString("ACRG_CDHD_END_DATE", acrg_cdhd_end_date);

			// Proc ���� ���� 
			GolfAdmMemUpdDaoProc proc = (GolfAdmMemUpdDaoProc)context.getProc("GolfAdmMemUpdDaoProc");
			debug("mod : " + mod + " / payBack : " + payBack);
			
			// 04.���� ���̺�(Proc) ��ȸ
			if("grade".equals(mod)){
				
				//  ��޺����� ��� // ��� ������ ��� �ٸ� ���μ����� ź��.
				editResult = proc.execute_grade(context, dataSet);	
				
				if (editResult == 1) {
					request.setAttribute("script", "parent.location.reload();");   
					request.setAttribute("resultMsg", "��޺����� ���������� ó�� �Ǿ����ϴ�.");      	
		        } else {
					request.setAttribute("resultMsg", "��޺����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
				
			} else if("pay".equals(mod)) {

				
				//  ���������� ��� 
				editResult = proc.execute_pay(context, request, dataSet);		      
		        				
				if (editResult == 1) {
					request.setAttribute("script", "parent.location.reload();");   
					request.setAttribute("resultMsg", "���� ������ ���������� ó�� �Ǿ����ϴ�.");      	
		        } else {
					request.setAttribute("resultMsg", "���� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
				
			} else if("del".equals(mod)) {

				
				//  ȸ�� ���������� ��� 
				editResult = proc.execute_del(context, request, dataSet);		      
		        				
				if (editResult == 1) {
					request.setAttribute("script", "parent.location='admMemList.do';");   
					request.setAttribute("resultMsg", "ȸ�������� ���������� ó�� �Ǿ����ϴ�.");      	
		        } else {
					request.setAttribute("resultMsg", "ȸ�������� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        } 
				
			} else {
				
				// ȸ������ ������ ���	
				editResult = proc.execute(context, dataSet);		      

		        String returnUrlTrue = "admMemList.do";
		        String returnUrlFalse = "admMemList.do";
				
				if (editResult == 1) {
					request.setAttribute("returnUrl", returnUrlTrue);
					request.setAttribute("resultMsg", "������ ���������� ó�� �Ǿ����ϴ�.");      	
		        } else {
					request.setAttribute("returnUrl", returnUrlFalse);
					request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
				
			}
			
			// 05. Return �� ����			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
