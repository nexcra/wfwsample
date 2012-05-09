/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemWhiteInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ī��ȸ�� ����� ����
*   �������  : golf 
*   �ۼ�����  : 2009-09-10
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	�̵������  
* @version	1.0 
******************************************************************************/
public class GolfMemWhiteInsActn extends GolfActn{
	
	public static final String TITLE = "ī��ȸ�� ����� ����";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
				
		
		try {
					 				
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
				

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);					
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
			
			int cardjoin = proc.cardJoinExecute(context, dataSet, request);
						
			debug("## ����ȸ���� ��������� cardjoin : " + cardjoin);
						
			// 05. Return �� ����			
			paramMap.put("cardjoin", String.valueOf(cardjoin));	
			
			String script = "";
			
			if(cardjoin == 1)
			{
			
				request.setAttribute("returnUrl", "GolfMemJoinEndPop.do");
				request.setAttribute("resultMsg", "����� ���������� ó���Ǿ����ϴ�.");
				script = "window.close()";
				request.setAttribute("script", script);
				
				userEtt.setIntMemberGrade(4);		//��������ó��
			
			}
			else if(cardjoin == 8)
			{
				request.setAttribute("returnUrl", "GolfMemJoinEndPop.do");
				request.setAttribute("resultMsg", "�̹� ���ԵǼ̽��ϴ�.");
				script = "window.close()";
				request.setAttribute("script", script);
			}
			else
			{
				request.setAttribute("returnUrl", "GolfMemJoinAgreePop.do");
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
			
			}
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
