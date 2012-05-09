/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemWhiteInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 카드회원 멤버쉽 가입
*   적용범위  : golf 
*   작성일자  : 2009-09-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	미디어포스  
* @version	1.0 
******************************************************************************/
public class GolfMemWhiteInsActn extends GolfActn{
	
	public static final String TITLE = "카드회원 멤버쉽 가입";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
				
		
		try {
					 				
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
				

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);					
						
			// 04.실제 테이블(Proc) 조회
			GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
			
			int cardjoin = proc.cardJoinExecute(context, dataSet, request);
						
			debug("## 골프회원이 멤버쉽가입 cardjoin : " + cardjoin);
						
			// 05. Return 값 세팅			
			paramMap.put("cardjoin", String.valueOf(cardjoin));	
			
			String script = "";
			
			if(cardjoin == 1)
			{
			
				request.setAttribute("returnUrl", "GolfMemJoinEndPop.do");
				request.setAttribute("resultMsg", "등록이 정상적으로 처리되었습니다.");
				script = "window.close()";
				request.setAttribute("script", script);
				
				userEtt.setIntMemberGrade(4);		//멤버쉽등급처리
			
			}
			else if(cardjoin == 8)
			{
				request.setAttribute("returnUrl", "GolfMemJoinEndPop.do");
				request.setAttribute("resultMsg", "이미 가입되셨습니다.");
				script = "window.close()";
				request.setAttribute("script", script);
			}
			else
			{
				request.setAttribute("returnUrl", "GolfMemJoinAgreePop.do");
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
			
			}
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
