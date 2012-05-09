/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntSpMyprizeUpdActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: �̺�Ʈ����� > Ư���� �����̺�Ʈ > ���Ǵ�÷���� > ����
*   �������	: golf
*   �ۼ�����	: 2009-07-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.myprize;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/ 
public class GolfEvntSpMyprizeUpdActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ����� > Ư���� �����̺�Ʈ > ���Ǵ�÷���� > ����";
 
	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			// 01.��������üũ
			
			 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss 	= "0005";
			String p_idx 		= parser.getParameter("p_idx","");
			String mode 		= parser.getParameter("mode","");
			String userId 		= parser.getParameter("userId","");
			String sex_clss 	= parser.getParameter("sex_clss","");
			String hp_ddd_no 	= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno 	= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno 	= parser.getParameter("hp_tel_sno","");
			String email 		= parser.getParameter("email","");
			String evnt_nm 		= parser.getParameter("evnt_nm","");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("p_idx",    	p_idx);
			dataSet.setString("mode",		mode);
			dataSet.setString("userId",		userId);
			dataSet.setString("sex_clss",	sex_clss);
			dataSet.setString("hp_ddd_no",	hp_ddd_no);
			dataSet.setString("hp_tel_hno",	hp_tel_hno);
			dataSet.setString("hp_tel_sno",	hp_tel_sno);
			dataSet.setString("email",		email);
			dataSet.setString("golf_svc_aplc_clss",golf_svc_aplc_clss);
			
			/*
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntSpMyprizeUpdDaoProc proc = (GolfEvntSpMyprizeUpdDaoProc)context.getProc("GolfEvntSpMyprizeUpdDaoProc");
			DbTaoResult boardResult = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("boardResult", boardResult);
		 	*/
			
			// 05.��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("p_idx", p_idx);
			paramMap.put("mode", mode);	
			paramMap.put("evnt_nm", evnt_nm);		
	        request.setAttribute("paramMap", paramMap); 	
			 
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
