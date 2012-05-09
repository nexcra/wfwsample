/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntMkMemberDetailActn
*   �ۼ���    : ������
*   ����      : ������ ��û ���� �󼼺���
*   �������  : golf
*   �ۼ�����  : 2010-09-01
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
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfEvntMkMemberDetailActn extends GolfActn{
	
	public static final String TITLE = " ������ ��û ���� �󼼺���";

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
		String jumin_no = "";
		int print_cnt = 0;
		String strResultCode = "";

		try {
			// 01.��������üũ
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				 jumin_no		= (String)usrEntity.getSocid(); 
			}
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String seq_no			= parser.getParameter("seq_no"); 
			String cupn_no			= parser.getParameter("cupn_no");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("CUPN_NO", cupn_no);
			dataSet.setString("JUMIN_NO", jumin_no);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			print_cnt = proc.getCupnPrintCnt(context, cupn_no); 		// ���� ���Ƚ��Ȯ��
			if(print_cnt < 1){
				DbTaoResult evntMkMemberAppDetail = proc.getMkMemberAppDetail(context, dataSet); 		// ���� ���� ��ȸ
				DbTaoResult evntMkPrcGroundDetail = proc.evntMkPrcGroundDetail(context, dataSet);		//���� ������ ����
				DbTaoResult getMkMember = proc.getMkMember(context, request, dataSet);		//���� ������ ����
				request.setAttribute("evntMkMemberAppDetail", evntMkMemberAppDetail);	
				request.setAttribute("evntMkPrcGroundDetail", evntMkPrcGroundDetail);
				request.setAttribute("getMkMember", getMkMember);
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			}else{
				strResultCode = "99";
				 //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			}
				
			// 05. Return �� ����	 		
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			request.setAttribute("strResultCode", strResultCode);		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
