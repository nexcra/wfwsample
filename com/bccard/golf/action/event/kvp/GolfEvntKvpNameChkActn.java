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
package com.bccard.golf.action.event.kvp;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpDaoProc;

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
			
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// �⺻ ��ȸ 
			String social_id_1 = (String)parser.getParameter("social_id_1").trim();
			String social_id_2 = (String)parser.getParameter("social_id_2").trim();
			String socid = social_id_1 + social_id_2; 
			String name = (String)parser.getParameter("name","").trim(); 
			
			String ddd_no = (String)parser.getParameter("ddd_no",""); 
			String tel_hno = (String)parser.getParameter("tel_hno",""); 
			String tel_sno = (String)parser.getParameter("tel_sno",""); 
			String hp_ddd_no = (String)parser.getParameter("hp_ddd_no",""); 
			String hp_tel_hno = (String)parser.getParameter("hp_tel_hno",""); 
			String hp_tel_sno = (String)parser.getParameter("hp_tel_sno",""); 
			String email = (String)parser.getParameter("email",""); 
			String email1 = (String)parser.getParameter("email1",""); 
			String email2 = (String)parser.getParameter("email2",""); 
			String idx = (String)parser.getParameter("idx",""); 

			String realPayAmt = (String)parser.getParameter("realPayAmt",""); 
			String inipluginData = (String)parser.getParameter("INIpluginData",""); 
			String kvppluginData = (String)parser.getParameter("KVPpluginData",""); 
			String user_r = (String)parser.getParameter("user_r",""); 
			String user_dn = (String)parser.getParameter("user_dn",""); 
			String signed_data = (String)parser.getParameter("signed_data",""); 
			String kvp_CARDCODE = (String)parser.getParameter("KVP_CARDCODE",""); 
			String kvp_LOGINGUBUN = (String)parser.getParameter("KVP_LOGINGUBUN",""); 
			
			String parameterManipulationProtectKey = (String)parser.getParameter("ParameterManipulationProtectKey",""); 
			String card_no = (String)parser.getParameter("card_no",""); 
			String isp_card_no = (String)parser.getParameter("isp_card_no",""); 
			String cavv = (String)parser.getParameter("cavv",""); 
			String sttl_clss = (String)parser.getParameter("sttl_clss",""); 
			String ins_term = (String)parser.getParameter("ins_term",""); 
			
			
			int chkResult = 0;
			String msg = "";
			String script = "";
			String nameChk = "N";
			
			script += "parent.iForm.social_id_1.value='"+social_id_1+"';";
			script += "parent.iForm.social_id_2.value='"+social_id_2+"';";
			script += "parent.iForm.name.value='"+name+"';";

			script += "parent.iForm.social_id_1_old.value='"+social_id_1+"';";
			script += "parent.iForm.social_id_2_old.value='"+social_id_2+"';";
			script += "parent.iForm.name_old.value='"+name+"';";
			
			script += "parent.iForm.tel_hno.value='"+tel_hno+"';";
			script += "parent.iForm.tel_sno.value='"+tel_sno+"';";
			script += "parent.iForm.hp_tel_hno.value='"+hp_tel_hno+"';";
			script += "parent.iForm.hp_tel_sno.value='"+hp_tel_sno+"';";
			script += "parent.iForm.email.value='"+email+"';";
			script += "parent.iForm.email1.value='"+email1+"';";
			script += "parent.iForm.email2.value='"+email2+"';";
			script += "parent.iForm.idx.value='"+idx+"';";
			
			script += "parent.iForm.realPayAmt.value='"+realPayAmt+"';";
			script += "parent.iForm.INIpluginData.value='"+inipluginData+"';";
			script += "parent.iForm.KVPpluginData.value='"+kvppluginData+"';";
			script += "parent.iForm.user_r.value='"+user_r+"';";
			script += "parent.iForm.user_dn.value='"+user_dn+"';";
			script += "parent.iForm.signed_data.value='"+signed_data+"';";
			script += "parent.iForm.KVP_CARDCODE.value='"+kvp_CARDCODE+"';";
			script += "parent.iForm.KVP_LOGINGUBUN.value='"+kvp_LOGINGUBUN+"';";
			script += "parent.iForm.ParameterManipulationProtectKey.value='"+parameterManipulationProtectKey+"';";
			script += "parent.iForm.card_no.value='"+card_no+"';";
			script += "parent.iForm.isp_card_no.value='"+isp_card_no+"';";
			script += "parent.iForm.cavv.value='"+cavv+"';";
			script += "parent.iForm.sttl_clss.value='"+sttl_clss+"';";
			script += "parent.iForm.ins_term.value='"+ins_term+"';";
			

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("socid", socid);

			
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
		
		
			if("1".equals(rtn)) { 
				// �������� - �Ǹ������� ������ ����� �˻��ϵ��� �Ѿ��.

				// �̹� ȸ������, ��û������ �ִ��� Ȯ���Ѵ�.
				GolfEvntKvpDaoProc proc = (GolfEvntKvpDaoProc)context.getProc("GolfEvntKvpDaoProc");
				chkResult = proc.execute_isJoin(context, request, dataSet);	
				
				if(chkResult==1){
					msg = "�̹� �����ϼ̽��ϴ�."; 
				}else if(chkResult==2){
					msg = "�̹� ��û�ϼ̽��ϴ�."; 
				}else{
					msg = "�Ǹ������� �����߽��ϴ�."; 
					nameChk = "Y";
					
					script += "parent.iForm.social_id_1.readOnly='true';";
					script += "parent.iForm.social_id_2.readOnly='true';";
					script += "parent.iForm.name.readOnly='true';";
				}
					
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
			
			script += "parent.iForm.chkResult.value='"+chkResult+"';";
			script += "parent.iForm.nameChk.value='"+nameChk+"';";
			script += "alert('"+msg+"');";
			script += "parent.goSelected('"+ddd_no+"', '"+hp_ddd_no+"');";

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
