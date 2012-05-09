/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntReInterpartActn
*   �ۼ���    : E4NET ���弱
*   ����      : �̺�Ʈ > ������ũ�̺�Ʈ > ������ũ���� üũ
*   �������  : Golf
*   �ۼ�����  : 2009-08-03
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
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkWinListDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntReInterpartActn extends GolfActn{
	
	public static final String TITLE = "�����̾� ��ŷ �̺�Ʈ ��÷�� ����Ʈ";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		
		
		try {
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 01.��������üũ
			String isInterpark = (String)request.getSession().getAttribute("isInterpark");
			String jumin_no		= "";  //�ֹε�Ϲ�ȣ
			String cupn			= "";  //������ȣ
			String email		= "";  //e-mail
			String pwin_date	= "";  //��÷����
			String userId		= "";  //ID
			

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			if(usrEntity != null) {
				userId          = (String)usrEntity.getAccount(); 
				jumin_no        = (String)usrEntity.getSocid();
			}

			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("socid"   , jumin_no);

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			DbTaoResult cpnInfo = (DbTaoResult) inter.getCpnNumber(context, request, dataSet);
			String useYN = (String) inter.getUseYN(context, request, dataSet);

			request.setAttribute("useYN", useYN);
			if (cpnInfo != null && cpnInfo.isNext()) {
				cpnInfo.first();
				cpnInfo.next();
				if(cpnInfo.getString("RESULT").equals("00")){
					cupn		= cpnInfo.getString("CUPN");
					email		= cpnInfo.getString("EMAIL");    
					pwin_date   = DateUtil.format(cpnInfo.getString("PWIN_DATE"),"yyyymmdd","yyyy/mm/dd");

					request.setAttribute("availCpn"  ,		"Y");
					request.setAttribute("cupn"      ,		cupn);
					request.setAttribute("email"    ,		email);
					request.setAttribute("userId"    ,		userId);
					request.setAttribute("pwin_date" ,		pwin_date);
				}else if(cpnInfo.getString("RESULT").equals("01")){					
					subpage_key = "notavail";
					request.setAttribute("availCpn","N");
				}
			}


		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
