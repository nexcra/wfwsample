/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��û ���
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.jeju;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.jeju.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkJjTimeRsViewActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ��û ���� Ȯ��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;

		String memb_id = "";
		String isLogin = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null) {
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
			}

			if(memb_id != null && !"".equals(memb_id)){
				isLogin = "1";
			} else {
				isLogin = "0";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String rsvt_SQL_NO			= parser.getParameter("RSVT_SQL_NO", "");
			debug("===============GolfBkJjTimeRsViewActn================rsvt_SQL_NO : " + rsvt_SQL_NO);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("intMemGrade",		intMemGrade);
			dataSet.setString("RSVT_SQL_NO",	rsvt_SQL_NO);
			//debug("GolfBkJjTimeRsViewActn===============rsvt_SQL_NO => " + rsvt_SQL_NO);


			// 04.���� ���̺�(Proc) ��ȸ
			//if(permission.equals("Y")){
				GolfBkJjTimeRsViewDaoProc proc = (GolfBkJjTimeRsViewDaoProc)context.getProc("GolfBkJjTimeRsViewDaoProc");
				DbTaoResult rsView = proc.execute(context, dataSet);
		        request.setAttribute("RsView", rsView); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			//}
			
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
