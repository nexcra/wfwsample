/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntMkMemberListActn
*   �ۼ���    : ������
*   ����      : BC Golf ��û ���̺��� Ȯ��
*   �������  : Golf
*   �ۼ�����  : 2010-08-31
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBcListDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

public class GolfEvntMkMemberListActn extends GolfActn{
	
	public static final String TITLE = "BC Golf ������ ��� ������ ����";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			 
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.���� ���̺�(Proc) ��ȸ 
			// 04-01 ������ ȸ������ ��������
			// session ���� �ֹε�� ��ȣ ��������
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			String juminno = ""; 
			if(usrEntity != null) {
				juminno 	= (String)usrEntity.getSocid(); 
			}
			dataSet.setString("JUMIN_NO", juminno);
			
			
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			//������
			DbTaoResult evntMkMemberResult = (DbTaoResult) proc.getMkMember(context, request, dataSet);
			String card_no = "";
			
			if (evntMkMemberResult != null && evntMkMemberResult.isNext()) {
				evntMkMemberResult.next();
				if(evntMkMemberResult.getString("RESULT").equals("00")){
					card_no = evntMkMemberResult.getString("MER_NO");
					dataSet.setString("CARD_NO", card_no);	//������ ��ȣ
				}
			}
			//�� ���� �߱� ����Ʈ ����
			DbTaoResult evntMkMemberAppListResult = (DbTaoResult) proc.getMkMemberAppList(context, request, dataSet);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("evntMkMemberResult", evntMkMemberResult);	//������
			request.setAttribute("evntMkMemberAppListResult", evntMkMemberAppListResult);	//�� ���� �߱� ����Ʈ ����
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
