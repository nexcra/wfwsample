/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtFieldMutiDelActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ����Ƽ�ڽ�(����������) ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mytbox.golf.GolfMtFieldMutiDelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfMtFieldMutiDelActn extends GolfActn{
	
	public static final String TITLE = "����Ƽ�ڽ�(����������) ���� ���� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int golfmtfieldDelResult = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); //�̸�
				memClss		= (String)usrEntity.getMemberClss(); //��޹�ȣ
				userId		= (String)usrEntity.getAccount(); //���̵�
				juminno 	= (String)usrEntity.getSocid(); //�ֹι�ȣ
				memGrade 	= (String)usrEntity.getMemGrade(); //���
				intMemGrade	= (int)usrEntity.getIntMemGrade(); //��޹�ȣ
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			// ResultScriptPag.jsp ���� �迭�� ����� Object ���� ��� ���� �߻�.
			paramMap.remove("cidx");

			// Request �� ����
			String[] seq_no = parser.getParameterValues("cidx", ""); 		//�Ϸù�ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", userId);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMtFieldMutiDelDaoProc proc = (GolfMtFieldMutiDelDaoProc)context.getProc("GolfMtFieldMutiDelDaoProc");
			// �� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			if (seq_no != null && seq_no.length > 0) {
				golfmtfieldDelResult = proc.execute(context, dataSet, seq_no);
			}			

			request.setAttribute("returnUrl", "golfMtFieldList.do");	
			// ������ ���
			if (golfmtfieldDelResult == seq_no.length) {
				request.setAttribute("resultMsg", "");	
			} else {
				request.setAttribute("resultMsg", "���������� ������ ���������� ó�� ���� �ʾҽ��ϴ�.");
			}
	
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
