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
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntCorporationActn extends GolfActn {
	
	public static final String TITLE = "법인플랫폼 통해서 골프라운지 입장 - 회원가입시 20% 할인"; 

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		 
		
		try {
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			
			usrEntity.setStrEnterCorporation("Y");
			usrEntity.setStrEnterCorporationMemId("9970086");
		
			debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | strEnterCorporation : " + usrEntity.getStrEnterCorporation());
			debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | strEnterCorporationMemId : " + usrEntity.getStrEnterCorporationMemId());
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
