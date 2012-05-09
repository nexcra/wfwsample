/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemberShipAllChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ȸ���� �ü� ��ü���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lounge;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmMemberShipAllUpdDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmMemberShipAllChgActn extends GolfActn{
	
	public static final String TITLE = "������ ȸ���� �ü� ��ü���� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			/*
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
			Date toDay = new Date(); 
			String nowDate = DateFormat.format(toDay);
			*/
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("gf_seq_no");
			paramMap.remove("today_fee");

			String[] gf_seq_no = parser.getParameterValues("gf_seq_no", "");	// ������ ��ȣ
			
			String fee_year = parser.getParameter("fee_year", "");	// ��¥ �⵵
			String fee_month = parser.getParameter("fee_month", "");	// ��¥ ��
			String fee_day = parser.getParameter("fee_day", "");	// ��¥ ��
			
			String[] today_fee = parser.getParameterValues("today_fee", "");	// �ü��Է�
			
			String fee_date = fee_year + fee_month + fee_day; //��¥
			
			//debug("fee_date :::: >>>> " + fee_date);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_no);			
			dataSet.setString("FEE_DATE", fee_date);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMemberShipAllUpdDaoProc proc = (GolfAdmMemberShipAllUpdDaoProc)context.getProc("GolfAdmMemberShipAllUpdDaoProc");
			
			// ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet, gf_seq_no, today_fee);			
			
	        if (editResult == gf_seq_no.length) {
				request.setAttribute("returnUrl", "admMemberShipList.do");
				request.setAttribute("resultMsg", "ȸ���� �ü� ��ü������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", "admMemberShipAllChgForm.do");
				request.setAttribute("resultMsg", "ȸ���� �ü� ��ü������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
