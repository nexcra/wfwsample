/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeDialyRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ���� ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeDialyInsDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeDialyRegActn extends GolfActn{
	
	public static final String TITLE = "������ �帲 ���������� ���� ��� ó��";

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
		String admin_id = "";
		int addResult = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				admin_id	= (String)userEtt.getMemId();			
			}
			
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("start_hh");
			paramMap.remove("start_mi");
			paramMap.remove("end_hh");
			paramMap.remove("end_mi");
			paramMap.remove("day_rsvt_num");

			String rsvt_date = parser.getParameter("rsvt_date", "");  // ��¥
			String sls_end_yn = parser.getParameter("sls_end_yn", "");  // ���忩��
			String holy_yn = parser.getParameter("holy_yn", "");  // ������
			String[] start_hh = parser.getParameterValues("start_hh", "");	// �ð�1
			String[] start_mi = parser.getParameterValues("start_mi", "");	// ��1
			String[] end_hh = parser.getParameterValues("end_hh", "");	// �ð�2
			String[] end_mi = parser.getParameterValues("end_mi", "");	// ��2
			String[] day_rsvt_num = parser.getParameterValues("day_rsvt_num", "");	// �ο�
			String rsvt_total_num = parser.getParameter("rsvt_total_num", "");	// ���������ο�
			String sch_gr = parser.getParameter("SCH_GR_SEQ_NO", "");	// ������
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);			
			dataSet.setString("RSVT_DATE", GolfUtil.toDateFormat(rsvt_date));
			dataSet.setString("SLS_END_YN", sls_end_yn);
			dataSet.setString("HOLY_YN", holy_yn);
			dataSet.setString("RSVT_TOTAL_NUM", rsvt_total_num);
			dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
	
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmRangeDialyInsDaoProc proc = (GolfAdmRangeDialyInsDaoProc)context.getProc("GolfAdmRangeDialyInsDaoProc");
			
			// ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			if (start_hh != null && start_hh.length > 0) {
				addResult = proc.execute(context, request, dataSet, start_hh, start_mi, end_hh, end_mi, day_rsvt_num);
			}			
			
			//debug("addResult :::: >>>> " + addResult);
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "admRangeDialyList.do");
				request.setAttribute("resultMsg", "�帲 ���������� ���� ����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else if (addResult == 9) {
				request.setAttribute("returnUrl", "admRangeDialyRegForm.do");
				request.setAttribute("resultMsg", "�̹� ��ϵǾ��ִ� �帲 ���������� �����Դϴ�.");      		        	
	        } else {
				request.setAttribute("returnUrl", "admRangeDialyRegForm.do");
				request.setAttribute("resultMsg", "�帲 ���������� ���� ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����		
	        paramMap.put("sch_gr", "");
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
