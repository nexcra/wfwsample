/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmEvntSpApplicantInqActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ > �̺�Ʈ >Ư������ �̺�Ʈ >��û���� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.applicant;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.applicant.GolfAdmEvntSpApplicantUpdDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntSpApplicantUpdActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ >Ư������ �̺�Ʈ >��û���� ó��";

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
		String emailId = "";
		 
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout"); 
		request.setAttribute("layout", layout);

		try { 
			
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				emailId		= (String)usrEntity.getEmail1();
			}
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss = "0005";
			String p_idx 		= parser.getParameter("p_idx","");
			String mode 		= parser.getParameter("mode","");
			String prz_win_yn 	= parser.getParameter("prz_win_yn","");
			String search_evnt 		= parser.getParameter("search_evnt","");
			String search_word 		= parser.getParameter("search_word","");
			String search_clss 		= parser.getParameter("search_clss","");
			String search_status 	= parser.getParameter("search_status",""); 
			String search_przwin 	= parser.getParameter("search_przwin","");
			String search_sdate 	= parser.getParameter("search_sdate","");
			String search_edate 	= parser.getParameter("search_edate","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("p_idx",		p_idx);
			dataSet.setString("mode",		mode);
			dataSet.setString("userNm",		userNm);
			dataSet.setString("emailId",	emailId);
			dataSet.setString("prz_win_yn",	prz_win_yn);
			dataSet.setString("evnt_clss", 	evnt_clss);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			 
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntSpApplicantUpdDaoProc proc = (GolfAdmEvntSpApplicantUpdDaoProc)context.getProc("GolfAdmEvntSpApplicantUpdDaoProc");
			DbTaoResult boardResult = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("boardResult", boardResult);	
						
			
			//��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("mode",		mode);			
	        request.setAttribute("paramMap", paramMap); 		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
