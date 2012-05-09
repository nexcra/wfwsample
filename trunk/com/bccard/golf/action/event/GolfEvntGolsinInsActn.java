/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntGolsinInsActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : �����̾� ��ŷ �̺�Ʈ ��û ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-08
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
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.GolfEvntGolsinInsDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfEvntGolsinInsActn extends GolfActn{
	
	public static final String TITLE = "��� �̺�Ʈ ��û ó��"; 

	/***************************************************************************************
	* ���� �����ȭ��
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
		int myPointResult =  0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = "golfGolsinInsForm.do";
		String errReUrl = "golfGolsinInsForm.do";
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
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

			long recv_no	= 6;
			String lsn_type_cd = "0009";
			

			String taNum = parser.getParameter("taNum", "");
			String mobile1 = parser.getParameter("mobile1", "");
			String mobile2 = parser.getParameter("mobile2", "");
			String mobile3 = parser.getParameter("mobile3", "");

			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userId", userId);		
			dataSet.setString("taNum", taNum);		
			dataSet.setString("mobile1", mobile1);	
			dataSet.setString("mobile2", mobile2);	
			dataSet.setString("mobile3", mobile3);		
			dataSet.setLong("RECV_NO", recv_no);
			dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
			
			// 04.���� ���̺�(Proc) ��ȸ
			
			GolfEvntGolsinInsDaoProc proc = (GolfEvntGolsinInsDaoProc)context.getProc("GolfEvntGolsinInsDaoProc");
			int addResult = proc.execute(context, dataSet);
			String aplc_seq_no = proc.getMaxSeqNo(context, dataSet);
			
	        if (addResult == 1) {
	        	request.setAttribute("script", "parent.location.href='http://www.golfloung.com/app/golfloung/html/event/bcgolf_event/progress_event.jsp?p_idx=6';");
				//request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "��Ÿ���� �̺�Ʈ ��û�� \\n\\n���������� �Ϸ�Ǿ����ϴ�.\\n\\n�����մϴ�.");      	
	        } else if (addResult == 2) {
				//request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("script", "parent.location.href='http://www.golfloung.com/app/golfloung/html/event/bcgolf_event/progress_event.jsp?p_idx=6';");
				request.setAttribute("resultMsg", "�̹� ��û�ϼ̽��ϴ�.");      		        	
	        } else {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "��� �̺�Ʈ ��û�� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("aplc_seq_no", aplc_seq_no);		
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
