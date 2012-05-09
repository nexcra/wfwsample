/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntKvpNameChkActn 
*   �ۼ���	: (��)�̵������ ������
*   ����		: KVP > �Ǹ�����
*   �������	: golf
*   �ۼ�����	: 2010-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;

import com.bccard.common.NameCheck;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntKvpNameChkActn extends GolfActn{
	
	public static final String TITLE = "KVP ó��";
	private static final String SITEID = "I829";		// �ѽ��� �ڵ�
	private static final String SITEPW = "44463742";	// �ѽ��� PASSWORD

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
				

		try {

			String socid = ""; 			// �ֹι�ȣ
			String social_id_1 = "";
			String social_id_2 = ""; 
			String name = ""; 
			String ddd_no = ""; 
			String tel_hno = ""; 
			String tel_sno = ""; 
			String hp_ddd_no = ""; 
			String hp_tel_hno = ""; 
			String hp_tel_sno = ""; 
			String email = "";  
			String idx = ""; 

			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// �⺻ ��ȸ 
			social_id_1 = (String)parser.getParameter("social_id_1").trim();
			social_id_2 = (String)parser.getParameter("social_id_2").trim();
			socid = social_id_1 + social_id_2; 
			name = (String)parser.getParameter("name","").trim(); 
			
			ddd_no = (String)parser.getParameter("ddd_no",""); 
			tel_hno = (String)parser.getParameter("tel_hno",""); 
			tel_sno = (String)parser.getParameter("tel_sno",""); 
			hp_ddd_no = (String)parser.getParameter("hp_ddd_no",""); 
			hp_tel_hno = (String)parser.getParameter("hp_tel_hno",""); 
			hp_tel_sno = (String)parser.getParameter("hp_tel_sno",""); 
			email = (String)parser.getParameter("email",""); 
			idx = (String)parser.getParameter("idx",""); 
						
			debug(">> �ѽ��� �Ǹ�����"); 
			// ��ȸ�� ���Խ� �ѽ��� �Ǹ�����
			NameCheck nm = new NameCheck(); 
			nm.setChkName(name);
			String rtn = nm.setJumin(socid + SITEPW);
			nm.setSiteCode(SITEID);

			if("0".equals(rtn)) {
				nm.setSiteCode(SITEID);
				nm.setTimeOut(30000);
				rtn = nm.getRtn().trim(); 
			} 
			debug(">> �ѽ��� �Ǹ����� > Return = " + rtn); 
			
			String msg = "";
			String script = "";
			String nameChk = "N";
			
			if("1".equals(rtn)) { 
				// ��������
				msg = "�Ǹ������� �����߽��ϴ�."; 
				nameChk = "Y";
			} else if("2".equals(rtn)) { 
				// ���ξƴ�
				msg = "�Ǹ������� �����߽��ϴ�[���� �ƴ�]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("3".equals(rtn)) {
				// �ڷ� ����
				msg = "�Ǹ������� �����߽��ϴ�[�ڷ� ����]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("4".equals(rtn)) {
				// �ý������ (ũ������ũ �̻�)
				msg = "�Ǹ������� �����߽��ϴ�[�ý������ (ũ������ũ �̻�)]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("5".equals(rtn)) {
				// �ֹι�ȣ ����
				msg = "�Ǹ������� �����߽��ϴ�[�ֹι�ȣ ����]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("50".equals(rtn)) {
				// �������� ���� ��û �ֹι�ȣ
				msg = "�Ǹ������� �����߽��ϴ�[�������� ���� ��û �ֹι�ȣ]. �ٽ�  �Է��� �ֽʽÿ�";
			} else  {
				// System ERROR
				msg = "�Ǹ������� �����߽��ϴ�[System ERROR]. �ٽ�  �Է��� �ֽʽÿ�";
			} 
			
			script += "parent.iForm.nameChk.value='"+nameChk+"';";
			script += "parent.iForm.social_id_1.value='"+social_id_1+"';";
			script += "parent.iForm.social_id_2.value='"+social_id_2+"';";
			script += "parent.iForm.name.value='"+name+"';";
			script += "parent.iForm.ddd_no.value='"+ddd_no+"';";
			script += "parent.iForm.tel_hno.value='"+tel_hno+"';";
			script += "parent.iForm.tel_sno.value='"+tel_sno+"';";
			script += "parent.iForm.hp_ddd_no.value='"+hp_ddd_no+"';";
			script += "parent.iForm.hp_tel_hno.value='"+hp_tel_hno+"';";
			script += "parent.iForm.hp_tel_sno.value='"+hp_tel_sno+"';";
			script += "parent.iForm.email.value='"+email+"';";
			script += "parent.iForm.idx.value='"+idx+"';";
			
//			script += "parent.iForm.social_id_1.readOnly='true';";
//			script += "parent.iForm.social_id_2.readOnly='true';";
//			script += "parent.iForm.name.readOnly='true';";

			script += "parent.iForm.social_id_1_old.value='"+social_id_1+"';";
			script += "parent.iForm.social_id_2_old.value='"+social_id_2+"';";
			script += "parent.iForm.name_old.value='"+name+"';";
			
			script += "alert('"+msg+"');";


			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
