/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfMemIdCheckActn
*   �ۼ���	: (��)�̵������
*   ����		: ����� > ȸ������ > �ߺ� ���̵� üũ
*   �������	: golf 
*   �ۼ�����	: 2009-12-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinCorpDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfMemIdCheckActn extends GolfActn { 
	
	public static final String TITLE = "����� > ȸ������ > �ߺ� ���̵� üũ"; 

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		Connection con = null;
		
		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			GolfMemJoinCorpDaoProc corpProc = (GolfMemJoinCorpDaoProc)context.getProc("GolfMemJoinCorpDaoProc");
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
		
			String account = parser.getParameter("account", "");				// ���̵�
					
			dataSet.setString("account", account);			
			String idChkResult = corpProc.chkIdDuplicate(context, dataSet, request);	

			debug("## GolfMemIdCheckActn | ���̵� �ߺ� üũ | account : " + account + " | �ߺ� ���� : " + idChkResult + "\n");

			request.setAttribute("idChkResult", idChkResult); 	// Y:���̵��ߺ�	  
			request.setAttribute("idChkAccount", account); 		  
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
			
}
