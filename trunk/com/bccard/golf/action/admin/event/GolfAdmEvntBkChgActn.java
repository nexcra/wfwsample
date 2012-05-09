/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �����̾� ��ŷ �̺�Ʈ ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBkUpdDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkChgActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ���� ó��";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
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

			String seq_no	= parser.getParameter("p_idx", "");// �̺�Ʈ�Ϸù�ȣ
			String evnt_from = parser.getParameter("evnt_from", "");	// �̺�Ʈ��������
			String evnt_to = parser.getParameter("evnt_to", "");	// �̺�Ʈ��������
			String prize_nm = parser.getParameter("prize_nm", "");	// �̺�Ʈ ����
			String disp_yn = parser.getParameter("disp_yn", "");	// �Խÿ���
			
			evnt_from = evnt_from.length() == 10 ? DateUtil.format(evnt_from, "yyyy-MM-dd", "yyyyMMdd"): "";
			evnt_to = evnt_to.length() == 10 ? DateUtil.format(evnt_to, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("EVNT_FROM", evnt_from);
			dataSet.setString("EVNT_TO", evnt_to);
			dataSet.setString("PRIZE_NM", prize_nm);
			dataSet.setString("DISP_YN", disp_yn);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBkUpdDaoProc proc = (GolfAdmEvntBkUpdDaoProc)context.getProc("GolfAdmEvntBkUpdDaoProc");
			
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				request.setAttribute("returnUrl", "admEvntBkList.do");
				request.setAttribute("resultMsg", "�����̾� ��ŷ �̺�Ʈ ������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", "admEvntBkChgForm.do");
				request.setAttribute("resultMsg", "�����̾� ��ŷ �̺�Ʈ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
