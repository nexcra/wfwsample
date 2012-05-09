/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ��ŷ > �����̾� > ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmMemStatsGrdActn extends GolfActn{
	
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ��� > ��޺�";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {

			// ���-����
			GolfAdmMemStatsGrdDaoProc proc = (GolfAdmMemStatsGrdDaoProc)context.getProc("GolfAdmMemStatsGrdDaoProc");
			DbTaoResult resultGrdAge = (DbTaoResult) proc.executeGrdAge(context, request);
			DbTaoResult resultGrdLct = (DbTaoResult) proc.executeGrdLct(context, request);
			DbTaoResult resultGrdSex = (DbTaoResult) proc.executeGrdSex(context, request);

			request.setAttribute("resultGrdAge", resultGrdAge);
			request.setAttribute("resultGrdLct", resultGrdLct);
			request.setAttribute("resultGrdSex", resultGrdSex);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
