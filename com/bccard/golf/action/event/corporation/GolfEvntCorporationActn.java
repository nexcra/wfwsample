package com.bccard.golf.action.event.corporation;

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
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.loginAction.SessionUtil;

import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntCorporationActn extends GolfActn {
	
	public static final String TITLE = "�����÷��� ���ؼ� ��������� ���� - ȸ�����Խ� 20% ����"; 

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
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			
			usrEntity.setStrEnterCorporation("Y");
			usrEntity.setStrEnterCorporationMemId("9970086");
		
			debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | strEnterCorporation : " + usrEntity.getStrEnterCorporation());
			debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | strEnterCorporationMemId : " + usrEntity.getStrEnterCorporationMemId());
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
